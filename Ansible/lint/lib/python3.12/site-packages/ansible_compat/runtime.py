"""Ansible runtime environment manager."""

# pylint: disable=too-many-lines

from __future__ import annotations

import contextlib
import importlib
import json
import logging
import os
import re
import shutil
import site
import subprocess  # noqa: S404
import sys
import warnings
from collections import OrderedDict
from dataclasses import dataclass, field
from pathlib import Path
from typing import TYPE_CHECKING, Any, no_type_check

import subprocess_tee
from packaging.version import Version

from ansible_compat.config import (
    AnsibleConfig,
    parse_ansible_version,
)
from ansible_compat.constants import (
    META_MAIN,
    MSG_INVALID_FQRL,
    RC_ANSIBLE_OPTIONS_ERROR,
    REQUIREMENT_LOCATIONS,
)
from ansible_compat.errors import (
    AnsibleCommandError,
    AnsibleCompatError,
    InvalidPrerequisiteError,
    MissingAnsibleError,
)
from ansible_compat.loaders import colpath_from_path, yaml_from_file
from ansible_compat.prerun import get_cache_dir

if TYPE_CHECKING:  # pragma: no cover
    # https://github.com/PyCQA/pylint/issues/3240
    # pylint: disable=unsubscriptable-object
    CompletedProcess = subprocess.CompletedProcess[Any]
    from collections.abc import Callable
else:
    CompletedProcess = subprocess.CompletedProcess


_logger = logging.getLogger(__name__)
# regex to extract the first version from a collection range specifier
version_re = re.compile(r":[>=<]*([^,]*)")
namespace_re = re.compile(r"^[a-z][a-z0-9_]+$")


class AnsibleWarning(Warning):
    """Warnings related to Ansible runtime."""


@dataclass
class Collection:
    """Container for Ansible collection information."""

    name: str
    version: str
    path: Path


class CollectionVersion(Version):
    """Collection version."""

    def __init__(self, version: str) -> None:
        """Initialize collection version."""
        # As packaging Version class does not support wildcard, we convert it
        # to "0", as this being the smallest version possible.
        if version == "*":
            version = "0"
        super().__init__(version)


@dataclass
class Plugins:  # pylint: disable=too-many-instance-attributes
    """Dataclass to access installed Ansible plugins, uses ansible-doc to retrieve them."""

    runtime: Runtime
    become: dict[str, str] = field(init=False)
    cache: dict[str, str] = field(init=False)
    callback: dict[str, str] = field(init=False)
    cliconf: dict[str, str] = field(init=False)
    connection: dict[str, str] = field(init=False)
    httpapi: dict[str, str] = field(init=False)
    inventory: dict[str, str] = field(init=False)
    lookup: dict[str, str] = field(init=False)
    netconf: dict[str, str] = field(init=False)
    shell: dict[str, str] = field(init=False)
    vars: dict[str, str] = field(init=False)
    module: dict[str, str] = field(init=False)
    strategy: dict[str, str] = field(init=False)
    test: dict[str, str] = field(init=False)
    filter: dict[str, str] = field(init=False)
    role: dict[str, str] = field(init=False)
    keyword: dict[str, str] = field(init=False)

    @no_type_check
    def __getattribute__(self, attr: str):  # noqa: ANN204
        """Get attribute."""
        if attr in {
            "become",
            "cache",
            "callback",
            "cliconf",
            "connection",
            "httpapi",
            "inventory",
            "lookup",
            "netconf",
            "shell",
            "vars",
            "module",
            "strategy",
            "test",
            "filter",
            "role",
            "keyword",
        }:
            try:
                result = super().__getattribute__(attr)
            except AttributeError as exc:
                proc = self.runtime.run(
                    ["ansible-doc", "--json", "-l", "-t", attr],
                )
                data = json.loads(proc.stdout)
                if not isinstance(data, dict):  # pragma: no cover
                    msg = "Unexpected output from ansible-doc"
                    raise AnsibleCompatError(msg) from exc
                result = data
        else:
            result = super().__getattribute__(attr)

        return result


# pylint: disable=too-many-instance-attributes
class Runtime:
    """Ansible Runtime manager."""

    _version: Version | None = None
    collections: OrderedDict[str, Collection] = OrderedDict()
    cache_dir: Path
    # Used to track if we have already initialized the Ansible runtime as attempts
    # to do it multiple tilmes will cause runtime warnings from within ansible-core
    initialized: bool = False
    plugins: Plugins
    _has_playbook_cache: dict[tuple[str, Path | None], bool] = {}
    require_module: bool = False

    def __init__(
        self,
        project_dir: Path | None = None,
        *,
        isolated: bool = False,
        min_required_version: str | None = None,
        require_module: bool = False,
        max_retries: int = 0,
        environ: dict[str, str] | None = None,
        verbosity: int = 0,
    ) -> None:
        """Initialize Ansible runtime environment.

        :param project_dir: The directory containing the Ansible project. If
                            not mentioned it will be guessed from the current
                            working directory.
        :param isolated: Assure that installation of collections or roles
                         does not affect Ansible installation, an unique cache
                         directory being used instead.
        :param min_required_version: Minimal version of Ansible required. If
                                     not found, a :class:`RuntimeError`
                                     exception is raised.
        :param require_module: If set, instantiation will fail if Ansible
                               Python module is missing or is not matching
                               the same version as the Ansible command line.
                               That is useful for consumers that expect to
                               also perform Python imports from Ansible.
        :param max_retries: Number of times it should retry network operations.
                            Default is 0, no retries.
        :param environ: Environment dictionary to use, if undefined
                        ``os.environ`` will be copied and used.
        :param verbosity: Verbosity level to use.
        """
        self.project_dir = project_dir or Path.cwd()
        self.isolated = isolated
        self.max_retries = max_retries
        self.environ = environ or os.environ.copy()
        if "ANSIBLE_COLLECTIONS_PATHS" in self.environ:
            msg = "ANSIBLE_COLLECTIONS_PATHS was detected, replace it with ANSIBLE_COLLECTIONS_PATH to continue."
            raise RuntimeError(msg)
        self.plugins = Plugins(runtime=self)
        self.verbosity = verbosity

        self.initialize_logger(level=self.verbosity)

        # Reduce noise from paramiko, unless user already defined PYTHONWARNINGS
        # paramiko/transport.py:236: CryptographyDeprecationWarning: Blowfish has been deprecated
        # https://github.com/paramiko/paramiko/issues/2038
        # As CryptographyDeprecationWarning is not a builtin, we cannot use
        # PYTHONWARNINGS to ignore it using category but we can use message.
        # https://stackoverflow.com/q/68251969/99834
        if "PYTHONWARNINGS" not in self.environ:  # pragma: no cover
            self.environ["PYTHONWARNINGS"] = "ignore:Blowfish has been deprecated"

        self.cache_dir = get_cache_dir(self.project_dir, isolated=self.isolated)

        self.config = AnsibleConfig(cache_dir=self.cache_dir)

        # Add the sys.path to the collection paths if not isolated
        self._patch_collection_paths()

        if not self.version_in_range(lower=min_required_version):
            msg = f"Found incompatible version of ansible runtime {self.version}, instead of {min_required_version} or newer."
            raise RuntimeError(msg)
        if require_module:
            self.require_module = True
            self._ensure_module_available()

        # pylint: disable=import-outside-toplevel
        from ansible.utils.display import Display

        # pylint: disable=unused-argument
        def warning(  # noqa: DOC103
            self: Display,  # noqa: ARG001
            msg: str,
            formatted: bool = False,  # noqa: ARG001,FBT001,FBT002
            *,
            help_text: str | None = None,  # noqa: ARG001
            obj: Any = None,  # noqa: ARG001,ANN401
        ) -> None:  # pragma: no cover
            """Override ansible.utils.display.Display.warning to avoid printing warnings."""
            warnings.warn(
                message=msg,
                category=AnsibleWarning,
                stacklevel=2,
                source={"msg": msg},
            )

        # Monkey patch ansible warning in order to use warnings module.
        Display.warning = warning

    def initialize_logger(self, level: int = 0) -> None:  # noqa: PLR6301
        """Set up the global logging level based on the verbosity number."""
        verbosity_map = {
            -2: logging.CRITICAL,
            -1: logging.ERROR,
            0: logging.WARNING,
            1: logging.INFO,
            2: logging.DEBUG,
        }
        # Unknown logging level is treated as DEBUG
        logging_level = verbosity_map.get(level, logging.DEBUG)
        _logger.setLevel(logging_level)
        # Use module-level _logger instance to validate it
        _logger.debug("Logging initialized to level %s", logging_level)

    def _patch_collection_paths(self) -> None:
        """Modify Ansible collection path for testing purposes.

        - Add the sys.path to the end of collection paths.
        - Add the site-packages to the beginning of collection paths to match
          ansible-core and ade behavior and trick ansible-galaxy to install
          default to the venv site-packages location (isolation).
        """
        # ansible-core normal precedence is: adjacent, local paths, configured paths, site paths
        collections_paths: list[str] = self.config.collections_paths.copy()
        if self.config.collections_scan_sys_path:
            for path in sys.path:
                if (
                    path not in collections_paths
                    and (Path(path) / "ansible_collections").is_dir()
                ):
                    collections_paths.append(  # pylint: disable=E1101
                        path,
                    )
            # When inside a venv, we also add the site-packages to the end of the
            # collections path because this is the last place where ansible-core
            # will look for them. This also ensures that when calling ansible-galaxy
            # to install content.
            for path in reversed(site.getsitepackages()):
                if path not in collections_paths:
                    collections_paths.append(path)

            if collections_paths != self.config.collections_paths:
                _logger.info(
                    "Collection paths was patched to include extra directories %s",
                    ",".join(collections_paths),
                )
        else:
            msg = "ANSIBLE_COLLECTIONS_SCAN_SYS_PATH is disabled, not patching collection paths. This may lead to unexpected behavior when using dev tools and prevent full isolation from user environment."
            _logger.warning(msg)
        self.config.collections_paths = collections_paths

    def load_collections(self) -> None:
        """Load collection data."""
        self.collections = OrderedDict()
        no_collections_msg = "None of the provided paths were usable"

        # do not use --path because it does not allow multiple values
        proc = self.run(
            [
                "ansible-galaxy",
                "collection",
                "list",
                "--format=json",
            ],
        )
        if proc.returncode == RC_ANSIBLE_OPTIONS_ERROR and (
            no_collections_msg in proc.stdout or no_collections_msg in proc.stderr
        ):  # pragma: no cover
            _logger.debug("Ansible reported no installed collections at all.")
            return
        if proc.returncode != 0:
            _logger.error(proc)
            msg = f"Unable to list collections: {proc}"
            raise RuntimeError(msg)
        try:
            data = json.loads(proc.stdout)
        except json.decoder.JSONDecodeError as exc:
            msg = f"Unable to parse galaxy output as JSON: {proc.stdout}"
            raise RuntimeError(msg) from exc
        if not isinstance(data, dict):
            msg = f"Unexpected collection data, {data}"
            raise TypeError(msg)
        for path in data:
            if not isinstance(data[path], dict):
                msg = f"Unexpected collection data, {data[path]}"
                raise TypeError(msg)
            for collection, collection_info in data[path].items():
                if not isinstance(collection_info, dict):
                    msg = f"Unexpected collection data, {collection_info}"
                    raise TypeError(msg)

                if collection in self.collections:
                    msg = f"Another version of '{collection}' {collection_info['version']} was found installed in {path}, only the first one will be used, {self.collections[collection].version} ({self.collections[collection].path})."
                    _logger.warning(msg)
                else:
                    self.collections[collection] = Collection(
                        name=collection,
                        version=collection_info["version"],
                        path=path,
                    )

    def _ensure_module_available(self) -> None:
        """Assure that Ansible Python module is installed and matching CLI version."""
        ansible_release_module = None
        with contextlib.suppress(ModuleNotFoundError, ImportError):
            ansible_release_module = importlib.import_module("ansible.release")

        if ansible_release_module is None:
            msg = "Unable to find Ansible python module."
            raise RuntimeError(msg)

        ansible_module_version = Version(
            ansible_release_module.__version__,
        )
        if ansible_module_version != self.version:
            msg = f"Ansible CLI ({self.version}) and python module ({ansible_module_version}) versions do not match. This indicates a broken execution environment."
            raise RuntimeError(msg)

        # We need to initialize the plugin loader
        # https://github.com/ansible/ansible-lint/issues/2945
        if not Runtime.initialized:
            col_path = [f"{self.cache_dir}/collections"]
            # noinspection PyProtectedMember
            # pylint: disable=import-outside-toplevel,no-name-in-module
            from ansible.plugins.loader import init_plugin_loader
            from ansible.utils.collection_loader._collection_finder import (  # pylint: disable=import-outside-toplevel
                _AnsibleCollectionFinder,  # noqa: PLC2701
            )

            _AnsibleCollectionFinder(  # noqa: SLF001
                paths=col_path,
            )._remove()  # pylint: disable=protected-access
            init_plugin_loader(col_path)

    def clean(self) -> None:
        """Remove content of cache_dir."""
        shutil.rmtree(self.cache_dir, ignore_errors=True)

    def run(  # ruff: disable=PLR0913
        self,
        args: str | list[str],
        *,
        retry: bool = False,
        tee: bool = False,
        env: dict[str, str] | None = None,
        cwd: Path | None = None,
        set_acp: bool = True,
    ) -> CompletedProcess:
        """Execute a command inside an Ansible environment.

        :param retry: Retry network operations on failures.
        :param tee: Also pass captured stdout/stderr to system while running.
        :param set_acp: Set the ANSIBLE_COLLECTIONS_PATH
        """
        if tee:
            run_func: Callable[..., CompletedProcess] = subprocess_tee.run
        else:
            run_func = subprocess.run
        env = self.environ if env is None else env.copy()
        # Presence of ansible debug variable or config option will prevent us
        # from parsing its JSON output due to extra debug messages on stdout.
        env["ANSIBLE_DEBUG"] = "0"

        # https://github.com/ansible/ansible-lint/issues/3522
        env["ANSIBLE_VERBOSE_TO_STDERR"] = "True"

        if set_acp:
            env["ANSIBLE_COLLECTIONS_PATH"] = ":".join(
                list(dict.fromkeys(self.config.collections_paths)),
            )

        for _ in range(self.max_retries + 1 if retry else 1):
            result = run_func(
                args,
                universal_newlines=True,
                check=False,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                env=env,
                cwd=str(cwd) if cwd else None,
            )
            if result.returncode == 0:
                break
            _logger.debug("Environment: %s", env)
            if retry:
                _logger.warning(
                    "Retrying execution failure %s of: %s",
                    result.returncode,
                    " ".join(args),
                )
        return result

    @property
    def version(self) -> Version:
        """Return current Version object for Ansible.

        If version is not mentioned, it returns current version as detected.
        When version argument is mentioned, it return converts the version string
        to Version object in order to make it usable in comparisons.
        """
        if self._version:
            return self._version

        proc = self.run(["ansible", "--version"])
        if proc.returncode == 0:
            self._version = parse_ansible_version(proc.stdout)
            return self._version

        msg = "Unable to find a working copy of ansible executable."
        raise MissingAnsibleError(msg, proc=proc)

    def version_in_range(
        self,
        lower: str | None = None,
        upper: str | None = None,
    ) -> bool:
        """Check if Ansible version is inside a required range.

        The lower limit is inclusive and the upper one exclusive.
        """
        if lower and self.version < Version(lower):
            return False
        return not (upper and self.version >= Version(upper))

    def has_playbook(self, playbook: str, *, basedir: Path | None = None) -> bool:
        """Return true if ansible can load a given playbook.

        This is also used for checking if playbooks from within collections
        are present and if they pass syntax check.
        """
        if (playbook, basedir) in self._has_playbook_cache:
            return self._has_playbook_cache[playbook, basedir]

        proc = self.run(["ansible-playbook", "--syntax-check", playbook], cwd=basedir)
        result = proc.returncode == 0
        if not result:
            if not basedir:
                basedir = Path()
            msg = f"has_playbook returned false for '{basedir / playbook}' due to syntax check returning {proc.returncode}"
            _logger.debug(msg)

        # cache the result
        self._has_playbook_cache[playbook, basedir] = result

        return result

    def install_collection(
        self,
        collection: str | Path,
        *,
        destination: Path | None = None,
        force: bool = False,
    ) -> None:
        """Install an Ansible collection.

        Can accept arguments like:
            'foo.bar:>=1.2.3'
            'git+https://github.com/ansible-collections/ansible.posix.git,main'
        """
        cmd = [
            "ansible-galaxy",
            "collection",
            "install",
            "-vvv",  # this is needed to make ansible display important info in case of failures
        ]
        if force:
            cmd.append("--force")

        if isinstance(collection, Path):
            collection = str(collection)
        # As ansible-galaxy install is not able to automatically determine
        # if the range requires a pre-release, we need to manually add the --pre
        # flag when needed.
        matches = version_re.search(collection)

        if (
            not is_url(collection)
            and matches
            and CollectionVersion(matches[1]).is_prerelease
        ):
            cmd.append("--pre")

        cpaths: list[str] = self.config.collections_paths
        if destination and str(destination) not in cpaths:
            # we cannot use '-p' because it breaks galaxy ability to ignore already installed collections, so
            # we hack ANSIBLE_COLLECTIONS_PATH instead and inject our own path there.
            # pylint: disable=no-member
            cpaths.insert(0, str(destination))
        cmd.append(f"{collection}")

        _logger.info("Running from %s : %s", Path.cwd(), " ".join(cmd))
        process = self.run(
            cmd,
            retry=True,
            env={**self.environ, "ANSIBLE_COLLECTIONS_PATH": ":".join(cpaths)},
        )
        if process.returncode != 0:
            msg = f"Command {' '.join(cmd)}, returned {process.returncode} code:\n{process.stdout}\n{process.stderr}"
            _logger.error(msg)
            raise InvalidPrerequisiteError(msg)

    def install_collection_from_disk(
        self,
        path: Path,
        destination: Path | None = None,
    ) -> None:
        """Build and install collection from a given disk path."""
        self.install_collection(path, destination=destination, force=True)

    # pylint: disable=too-many-branches
    def install_requirements(  # noqa: C901
        self,
        requirement: Path,
        *,
        retry: bool = False,
        offline: bool = False,
    ) -> None:
        """Install dependencies from a requirements.yml.

        :param requirement: path to requirements.yml file
        :param retry: retry network operations on failures
        :param offline: bypass installation, may fail if requirements are not met.
        """
        if not Path(requirement).exists():
            return
        reqs_yaml = yaml_from_file(Path(requirement))
        if not isinstance(reqs_yaml, dict | list):
            msg = f"{requirement} file is not a valid Ansible requirements file."
            raise InvalidPrerequisiteError(msg)

        if isinstance(reqs_yaml, dict):
            for key in reqs_yaml:
                if key not in {"roles", "collections"}:
                    msg = f"{requirement} file is not a valid Ansible requirements file. Only 'roles' and 'collections' keys are allowed at root level. Recognized valid locations are: {', '.join(REQUIREMENT_LOCATIONS)}"
                    raise InvalidPrerequisiteError(msg)

        if isinstance(reqs_yaml, list) or "roles" in reqs_yaml:
            cmd = [
                "ansible-galaxy",
                "role",
                "install",
                "-r",
                f"{requirement}",
            ]
            if self.verbosity > 0:
                cmd.extend(["-" + ("v" * self.verbosity)])
            cmd.extend(["--roles-path", f"{self.cache_dir}/roles"])

            if offline:
                _logger.warning(
                    "Skipped installing old role dependencies due to running in offline mode.",
                )
            else:
                _logger.info("Running %s", " ".join(cmd))

                result = self.run(cmd, retry=retry)
                _logger.debug(result.stdout)
                if result.returncode != 0:
                    _logger.error(result.stderr)
                    raise AnsibleCommandError(result)

        # Run galaxy collection install works on v2 requirements.yml
        if (
            isinstance(reqs_yaml, dict)
            and "collections" in reqs_yaml
            and reqs_yaml["collections"] is not None
        ):
            cmd = [
                "ansible-galaxy",
                "collection",
                "install",
            ]
            if self.verbosity > 0:
                cmd.extend(["-" + ("v" * self.verbosity)])

            for collection in reqs_yaml["collections"]:
                if isinstance(collection, dict) and collection.get("type", "") == "git":
                    _logger.info(
                        "Adding '--pre' to ansible-galaxy collection install because we detected one collection being sourced from git.",
                    )
                    cmd.append("--pre")
                    break
            if offline:
                _logger.warning(
                    "Skipped installing collection dependencies due to running in offline mode.",
                )
            else:
                cmd.extend(["-r", str(requirement)])
                _logger.info("Running %s", " ".join(cmd))
                result = self.run(
                    cmd,
                    retry=retry,
                )
                _logger.debug(result.stdout)
                if result.returncode != 0:
                    _logger.error(result.stderr)
                    raise AnsibleCommandError(result)
        if self.require_module:
            Runtime.initialized = False
            self._ensure_module_available()

    # pylint: disable=too-many-locals
    def prepare_environment(  # noqa: C901
        self,
        required_collections: dict[str, str] | None = None,
        *,
        retry: bool = False,
        install_local: bool = False,
        offline: bool = False,
        role_name_check: int = 0,
    ) -> None:
        """Make dependencies available if needed."""
        destination: Path = self.cache_dir / "collections"
        if required_collections is None:
            required_collections = {}

        self._prepare_ansible_paths()
        # first one is standard for collection layout repos and the last two
        # are part of Tower specification
        # https://docs.ansible.com/ansible-tower/latest/html/userguide/projects.html#ansible-galaxy-support
        # https://docs.ansible.com/ansible-tower/latest/html/userguide/projects.html#collections-support
        for req_file in REQUIREMENT_LOCATIONS:
            file_path = Path(req_file)
            if self.project_dir:
                file_path = self.project_dir / req_file
            self.install_requirements(file_path, retry=retry, offline=offline)

        if not install_local:
            return

        for item in search_galaxy_paths(self.project_dir):
            # processing all found galaxy.yml files
            if item.exists():
                data = yaml_from_file(item)
                if isinstance(data, dict) and "dependencies" in data:
                    for name, required_version in data["dependencies"].items():
                        _logger.info(
                            "Provisioning collection %s:%s from galaxy.yml",
                            name,
                            required_version,
                        )
                        self.install_collection(
                            f"{name}{',' if is_url(name) else ':'}{required_version}",
                            destination=destination,
                        )

        for name, min_version in required_collections.items():
            self.install_collection(
                f"{name}:>={min_version}",
                destination=destination,
            )

        galaxy_path = self.project_dir / "galaxy.yml"
        if (galaxy_path).exists():
            # while function can return None, that would not break the logic
            colpath = Path(
                f"{destination}/ansible_collections/{colpath_from_path(self.project_dir)}",
            )
            if colpath.is_symlink():
                if os.path.realpath(colpath) == str(Path.cwd()):
                    _logger.warning(
                        "Found symlinked collection, skipping its installation.",
                    )
                    return
                _logger.warning(
                    "Collection is symlinked, but not pointing to %s directory, so we will remove it.",
                    Path.cwd(),
                )
                colpath.unlink()

            # molecule scenario within a collection
            self.install_collection_from_disk(
                galaxy_path.parent,
                destination=destination,
            )
        elif Path.cwd().parent.name == "roles" and Path("../../galaxy.yml").exists():
            # molecule scenario located within roles/<role-name>/molecule inside
            # a collection
            self.install_collection_from_disk(
                Path("../.."),
                destination=destination,
            )
        else:
            # no collection, try to recognize and install a standalone role
            self._install_galaxy_role(
                self.project_dir,
                role_name_check=role_name_check,
                ignore_errors=True,
            )
        # reload collections
        self.load_collections()

    def require_collection(
        self,
        name: str,
        version: str | None = None,
        *,
        install: bool = True,
    ) -> tuple[CollectionVersion, Path]:
        """Check if a minimal collection version is present or exits.

        In the future this method may attempt to install a missing or outdated
        collection before failing.

        Args:
            name: collection name
            version: minimal version required
            install: if True, attempt to install a missing collection

        Returns:
            tuple of (found_version, collection_path)
        """
        try:
            ns, coll = name.split(".", 1)
        except ValueError as exc:
            msg = f"Invalid collection name supplied: {name}%s"
            raise InvalidPrerequisiteError(
                msg,
            ) from exc

        paths: list[str] = self.config.collections_paths
        if not paths or not isinstance(paths, list):
            msg = f"Unable to determine ansible collection paths. ({paths})"
            raise InvalidPrerequisiteError(
                msg,
            )

        for path in paths:
            collpath = Path(path) / "ansible_collections" / ns / coll
            if collpath.exists():
                mpath = collpath / "MANIFEST.json"
                if not mpath.exists():
                    msg = f"Found collection at '{collpath}' but missing MANIFEST.json, cannot get info."
                    _logger.fatal(msg)
                    raise InvalidPrerequisiteError(msg)

                with mpath.open(encoding="utf-8") as f:
                    manifest = json.loads(f.read())
                    found_version = CollectionVersion(
                        manifest["collection_info"]["version"],
                    )
                    if version and found_version < CollectionVersion(version):
                        if install:
                            self.install_collection(f"{name}:>={version}")
                            self.require_collection(name, version, install=False)
                        else:
                            msg = f"Found {name} collection {found_version} but {version} or newer is required."
                            _logger.fatal(msg)
                            raise InvalidPrerequisiteError(msg)
                    return found_version, collpath.resolve()
        if install:
            self.install_collection(f"{name}:>={version}" if version else name)
            return self.require_collection(
                name=name,
                version=version,
                install=False,
            )
        msg = f"Collection '{name}' not found in '{paths}'"
        _logger.fatal(msg)
        raise InvalidPrerequisiteError(msg)

    def _prepare_ansible_paths(self) -> None:
        """Configure Ansible environment variables."""
        try:
            library_paths: list[str] = self.config.default_module_path.copy()
            roles_path: list[str] = self.config.default_roles_path.copy()
            collections_path: list[str] = self.config.collections_paths.copy()
        except AttributeError as exc:
            msg = "Unexpected ansible configuration"
            raise RuntimeError(msg) from exc

        alterations_list: list[tuple[list[str], str, bool]] = [
            (library_paths, "plugins/modules", True),
            (roles_path, "roles", True),
        ]

        alterations_list.extend(
            (
                [
                    (roles_path, f"{self.cache_dir}/roles", False),
                    (library_paths, f"{self.cache_dir}/modules", False),
                    (collections_path, f"{self.cache_dir}/collections", False),
                ]
                if self.isolated
                else []
            ),
        )

        for path_list, path_, must_be_present in alterations_list:
            path = Path(path_)
            if not path.exists():
                if must_be_present:
                    continue
                path.mkdir(parents=True, exist_ok=True)
            if str(path) not in path_list:
                path_list.insert(0, str(path))

        if library_paths != self.config.DEFAULT_MODULE_PATH:
            self._update_env("ANSIBLE_LIBRARY", library_paths)
        if collections_path != self.config.default_collections_path:
            self._update_env("ANSIBLE_COLLECTIONS_PATH", collections_path)
        if roles_path != self.config.default_roles_path:
            self._update_env("ANSIBLE_ROLES_PATH", roles_path)

    def _get_roles_path(self) -> Path:
        """Return roles installation path.

        If `self.isolated` is set to `True`, `self.cache_dir` would be
        created, then it returns the `self.cache_dir/roles`. When `self.isolated` is
        not mentioned or set to `False`, it returns the first path in
        `default_roles_path`.
        """
        path = Path(f"{self.cache_dir}/roles")
        return path

    def _install_galaxy_role(
        self,
        project_dir: Path,
        role_name_check: int = 0,
        *,
        ignore_errors: bool = False,
    ) -> None:
        """Detect standalone galaxy role and installs it.

        Args:
            project_dir: path to the role
            role_name_check: logic to used to check role name
                0: exit with error if name is not compliant (default)
                1: warn if name is not compliant
                2: bypass any name checking

            ignore_errors: if True, bypass installing invalid roles.

        Our implementation aims to match ansible-galaxy's behavior for installing
        roles from a tarball or scm. For example ansible-galaxy will install a role
        that has both galaxy.yml and meta/main.yml present but empty. Also missing
        galaxy.yml is accepted but missing meta/main.yml is not.
        """
        yaml = None
        galaxy_info = {}

        for meta_main in META_MAIN:
            meta_filename = Path(project_dir) / meta_main

            if meta_filename.exists():
                break
        else:
            if ignore_errors:
                return

        yaml = yaml_from_file(meta_filename)

        if yaml and "galaxy_info" in yaml:
            galaxy_info = yaml["galaxy_info"]

        fqrn = _get_role_fqrn(galaxy_info, project_dir)

        if role_name_check in {0, 1}:
            if not re.match(r"[a-z0-9][a-z0-9_-]+\.[a-z][a-z0-9_]+$", fqrn):
                msg = MSG_INVALID_FQRL.format(fqrn)
                if role_name_check == 1:
                    _logger.warning(msg)
                else:
                    _logger.error(msg)
                    raise InvalidPrerequisiteError(msg)
        elif "role_name" in galaxy_info:
            # when 'role-name' is in skip_list, we stick to plain role names
            role_namespace = _get_galaxy_role_ns(galaxy_info)
            role_name = _get_galaxy_role_name(galaxy_info)
            fqrn = f"{role_namespace}{role_name}"
        else:
            fqrn = Path(project_dir).absolute().name
        path = self._get_roles_path()
        path.mkdir(parents=True, exist_ok=True)
        link_path = path / fqrn
        # despite documentation stating that is_file() reports true for symlinks,
        # it appears that is_dir() reports true instead, so we rely on exists().
        target = Path(project_dir).absolute()
        if not link_path.exists() or (
            link_path.is_symlink() and link_path.readlink() != target
        ):
            # must call unlink before checking exists because a broken
            # link reports as not existing and we want to repair it
            link_path.unlink(missing_ok=True)
            # https://github.com/python/cpython/issues/73843
            link_path.symlink_to(str(target), target_is_directory=True)
        _logger.info(
            "Using %s symlink to current repository in order to enable Ansible to find the role using its expected full name.",
            link_path,
        )

    def _update_env(self, varname: str, value: list[str], default: str = "") -> None:
        """Update colon based environment variable if needed.

        New values are prepended to make sure they take precedence.
        """
        if not value:
            return
        orig_value = self.environ.get(varname, default)
        if orig_value:
            # we just want to avoid repeating the same entries, but order is important
            value = list(dict.fromkeys([*value, *orig_value.split(":")]))
        value_str = ":".join(value)
        if value_str != self.environ.get(varname, ""):
            self.environ[varname] = value_str
            _logger.info("Set %s=%s", varname, value_str)


def _get_role_fqrn(galaxy_infos: dict[str, Any], project_dir: Path) -> str:
    """Compute role fqrn."""
    role_namespace = _get_galaxy_role_ns(galaxy_infos)
    role_name = _get_galaxy_role_name(galaxy_infos)

    if len(role_name) == 0:
        role_name = Path(project_dir).absolute().name
        role_name = re.sub(r"(ansible-|ansible-role-)", "", role_name).split(
            ".",
            maxsplit=2,
        )[-1]

    return f"{role_namespace}{role_name}"


def _get_galaxy_role_ns(galaxy_infos: dict[str, Any]) -> str:
    """Compute role namespace from meta/main.yml, including trailing dot."""
    role_namespace = galaxy_infos.get("namespace", "")
    if len(role_namespace) == 0:
        role_namespace = galaxy_infos.get("author", "")
    if not isinstance(role_namespace, str):
        msg = f"Role namespace must be string, not {role_namespace}"
        raise AnsibleCompatError(msg)
    # if there's a space in the name space, it's likely author name
    # and not the galaxy login, so act as if there was no namespace
    if not role_namespace or re.match(r"^\w+ \w+", role_namespace):
        role_namespace = ""
    else:
        role_namespace = f"{role_namespace}."
    return role_namespace


def _get_galaxy_role_name(galaxy_infos: dict[str, Any]) -> str:
    """Compute role name from meta/main.yml."""
    result = galaxy_infos.get("role_name", "")
    if not isinstance(result, str):
        return ""
    return result


def search_galaxy_paths(search_dir: Path) -> list[Path]:
    """Search for galaxy paths (only one level deep).

    Returns:
        list[Path]: List of galaxy.yml found.
    """
    galaxy_paths: list[Path] = []
    for item in [Path(), *search_dir.iterdir()]:
        # We ignore any folders that are not valid namespaces, just like
        # ansible galaxy does at this moment.
        file_path = item.resolve()
        if file_path.is_file() and file_path.name == "galaxy.yml":
            galaxy_paths.append(file_path)
            continue
        if file_path.is_dir() and namespace_re.match(file_path.name):
            file_path /= "galaxy.yml"
            if file_path.exists():
                galaxy_paths.append(file_path)
    return galaxy_paths


def is_url(name: str) -> bool:
    """Return True if a dependency name looks like an URL."""
    return bool(re.match(r"^git[+@]", name))
