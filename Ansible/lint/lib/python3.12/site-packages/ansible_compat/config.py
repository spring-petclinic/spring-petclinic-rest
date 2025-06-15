"""Store configuration options as a singleton."""

from __future__ import annotations

import ast
import copy
import os
import re
import subprocess  # noqa: S404
from collections import UserDict
from typing import TYPE_CHECKING, Literal

from packaging.version import Version

from ansible_compat.constants import ANSIBLE_MIN_VERSION
from ansible_compat.errors import InvalidPrerequisiteError, MissingAnsibleError
from ansible_compat.ports import cache

if TYPE_CHECKING:  # pragma: no cover
    from pathlib import Path


def parse_ansible_version(stdout: str) -> Version:
    """Parse output of 'ansible --version'."""
    # Ansible can produce extra output before displaying version in debug mode.

    # ansible-core 2.11+: 'ansible [core 2.11.3]'
    match = re.search(
        r"^ansible \[(?:core|base) (?P<version>[^\]]+)\]",
        stdout,
        re.MULTILINE,
    )
    if match:
        return Version(match.group("version"))
    msg = f"Unable to parse ansible cli version: {stdout}\nKeep in mind that only {ANSIBLE_MIN_VERSION } or newer are supported."
    raise InvalidPrerequisiteError(msg)


@cache
def ansible_version(version: str = "") -> Version:
    """Return current Version object for Ansible.

    If version is not mentioned, it returns current version as detected.
    When version argument is mentioned, it return converts the version string
    to Version object in order to make it usable in comparisons.
    """
    if version:
        return Version(version)

    proc = subprocess.run(
        ["ansible", "--version"],
        text=True,
        check=False,
        capture_output=True,
    )
    if proc.returncode != 0:
        raise MissingAnsibleError(proc=proc)

    return parse_ansible_version(proc.stdout)


class AnsibleConfig(
    UserDict[str, object],
):  # pylint: disable=too-many-ancestors # noqa: DOC605
    """Interface to query Ansible configuration.

    This should allow user to access everything provided by `ansible-config dump` without having to parse the data himself.

    Attributes:
        _aliases:
        action_warnings:
        agnostic_become_prompt:
        allow_world_readable_tmpfiles:
        ansible_connection_path:
        ansible_cow_acceptlist:
        ansible_cow_path:
        ansible_cow_selection:
        ansible_force_color:
        ansible_nocolor:
        ansible_nocows:
        ansible_pipelining:
        any_errors_fatal:
        become_allow_same_user:
        become_plugin_path:
        cache_plugin:
        cache_plugin_connection:
        cache_plugin_prefix:
        cache_plugin_timeout:
        callable_accept_list:
        callbacks_enabled:
        collections_on_ansible_version_mismatch:
        collections_paths:
        collections_scan_sys_path:
        color_changed:
        color_console_prompt:
        color_debug:
        color_deprecate:
        color_diff_add:
        color_diff_lines:
        color_diff_remove:
        color_error:
        color_highlight:
        color_ok:
        color_skip:
        color_unreachable:
        color_verbose:
        color_warn:
        command_warnings:
        conditional_bare_vars:
        connection_facts_modules:
        controller_python_warning:
        coverage_remote_output:
        coverage_remote_paths:
        default_action_plugin_path:
        default_allow_unsafe_lookups:
        default_ask_pass:
        default_ask_vault_pass:
        default_become:
        default_become_ask_pass:
        default_become_exe:
        default_become_flags:
        default_become_method:
        default_become_user:
        default_cache_plugin_path:
        default_callback_plugin_path:
        default_cliconf_plugin_path:
        default_collections_path:
        default_connection_plugin_path:
        default_debug:
        default_executable:
        default_fact_path:
        default_filter_plugin_path:
        default_force_handlers:
        default_forks:
        default_gather_subset:
        default_gather_timeout:
        default_gathering:
        default_handler_includes_static:
        default_hash_behaviour:
        default_host_list:
        default_httpapi_plugin_path:
        default_internal_poll_interval:
        default_inventory_plugin_path:
        default_jinja2_extensions:
        default_jinja2_native:
        default_keep_remote_files:
        default_libvirt_lxc_noseclabel:
        default_load_callback_plugins:
        default_local_tmp:
        default_log_filter:
        default_log_path:
        default_lookup_plugin_path:
        default_managed_str:
        default_module_args:
        default_module_compression:
        default_module_name:
        default_module_path:
        default_module_utils_path:
        default_netconf_plugin_path:
        default_no_log:
        default_no_target_syslog:
        default_null_representation:
        default_poll_interval:
        default_private_key_file:
        default_private_role_vars:
        default_remote_port:
        default_remote_user:
        default_roles_path:
        default_selinux_special_fs:
        default_stdout_callback:
        default_strategy:
        default_strategy_plugin_path:
        default_su:
        default_syslog_facility:
        default_task_includes_static:
        default_terminal_plugin_path:
        default_test_plugin_path:
        default_timeout:
        default_transport:
        default_undefined_var_behavior:
        default_vars_plugin_path:
        default_vault_encrypt_identity:
        default_vault_id_match:
        default_vault_identity:
        default_vault_identity_list:
        default_vault_password_file:
        default_verbosity:
        deprecation_warnings:
        devel_warning:
        diff_always:
        diff_context:
        display_args_to_stdout:
        display_skipped_hosts:
        doc_fragment_plugin_path:
        docsite_root_url:
        duplicate_yaml_dict_key:
        enable_task_debugger:
        error_on_missing_handler:
        facts_modules:
        galaxy_cache_dir:
        galaxy_display_progress:
        galaxy_ignore_certs:
        galaxy_role_skeleton:
        galaxy_role_skeleton_ignore:
        galaxy_server:
        galaxy_server_list:
        galaxy_token_path:
        host_key_checking:
        host_pattern_mismatch:
        inject_facts_as_vars:
        interpreter_python:
        interpreter_python_distro_map:
        interpreter_python_fallback:
        invalid_task_attribute_failed:
        inventory_any_unparsed_is_failed:
        inventory_cache_enabled:
        inventory_cache_plugin:
        inventory_cache_plugin_connection:
        inventory_cache_plugin_prefix:
        inventory_cache_timeout:
        inventory_enabled:
        inventory_export:
        inventory_ignore_exts:
        inventory_ignore_patterns:
        inventory_unparsed_is_failed:
        localhost_warning:
        max_file_size_for_diff:
        module_ignore_exts:
        netconf_ssh_config:
        network_group_modules:
        old_plugin_cache_clearing:
        paramiko_host_key_auto_add:
        paramiko_look_for_keys:
        persistent_command_timeout:
        persistent_connect_retry_timeout:
        persistent_connect_timeout:
        persistent_control_path_dir:
        playbook_dir:
        playbook_vars_root:
        plugin_filters_cfg:
        python_module_rlimit_nofile:
        retry_files_enabled:
        retry_files_save_path:
        run_vars_plugins:
        show_custom_stats:
        string_conversion_action:
        string_type_filters:
        system_warnings:
        tags_run:
        tags_skip:
        task_debugger_ignore_errors:
        task_timeout:
        transform_invalid_group_chars:
        use_persistent_connections:
        variable_plugins_enabled:
        variable_precedence:
        verbose_to_stderr:
        win_async_startup_timeout:
        worker_shutdown_poll_count:
        worker_shutdown_poll_delay:
        yaml_filename_extensions:
    """

    _aliases = {
        "COLLECTIONS_PATH": "COLLECTIONS_PATHS",  # 2.9 -> 2.10
    }
    # Expose some attributes to enable auto-complete in editors, based on
    # https://docs.ansible.com/ansible/latest/reference_appendices/config.html
    action_warnings: bool = True
    agnostic_become_prompt: bool = True
    allow_world_readable_tmpfiles: bool = False
    ansible_connection_path: str | None = None
    ansible_cow_acceptlist: list[str]
    ansible_cow_path: str | None = None
    ansible_cow_selection: str = "default"
    ansible_force_color: bool = False
    ansible_nocolor: bool = False
    ansible_nocows: bool = False
    ansible_pipelining: bool = False
    any_errors_fatal: bool = False
    become_allow_same_user: bool = False
    become_plugin_path: list[str] = [
        "~/.ansible/plugins/become",
        "/usr/share/ansible/plugins/become",
    ]
    cache_plugin: str = "memory"
    cache_plugin_connection: str | None = None
    cache_plugin_prefix: str = "ansible_facts"
    cache_plugin_timeout: int = 86400
    callable_accept_list: list[str] = []
    callbacks_enabled: list[str] = []
    collections_on_ansible_version_mismatch: Literal["warning", "ignore"] = "warning"
    collections_paths: list[str] = [
        "~/.ansible/collections",
        "/usr/share/ansible/collections",
    ]
    collections_scan_sys_path: bool = True
    color_changed: str = "yellow"
    color_console_prompt: str = "white"
    color_debug: str = "dark gray"
    color_deprecate: str = "purple"
    color_diff_add: str = "green"
    color_diff_lines: str = "cyan"
    color_diff_remove: str = "red"
    color_error: str = "red"
    color_highlight: str = "white"
    color_ok: str = "green"
    color_skip: str = "cyan"
    color_unreachable: str = "bright red"
    color_verbose: str = "blue"
    color_warn: str = "bright purple"
    command_warnings: bool = False
    conditional_bare_vars: bool = False
    connection_facts_modules: dict[str, str]
    controller_python_warning: bool = True
    coverage_remote_output: str | None
    coverage_remote_paths: list[str]
    default_action_plugin_path: list[str] = [
        "~/.ansible/plugins/action",
        "/usr/share/ansible/plugins/action",
    ]
    default_allow_unsafe_lookups: bool = False
    default_ask_pass: bool = False
    default_ask_vault_pass: bool = False
    default_become: bool = False
    default_become_ask_pass: bool = False
    default_become_exe: str | None = None
    default_become_flags: str
    default_become_method: str = "sudo"
    default_become_user: str = "root"
    default_cache_plugin_path: list[str] = [
        "~/.ansible/plugins/cache",
        "/usr/share/ansible/plugins/cache",
    ]
    default_callback_plugin_path: list[str] = [
        "~/.ansible/plugins/callback",
        "/usr/share/ansible/plugins/callback",
    ]
    default_cliconf_plugin_path: list[str] = [
        "~/.ansible/plugins/cliconf",
        "/usr/share/ansible/plugins/cliconf",
    ]
    default_connection_plugin_path: list[str] = [
        "~/.ansible/plugins/connection",
        "/usr/share/ansible/plugins/connection",
    ]
    default_debug: bool = False
    default_executable: str = "/bin/sh"
    default_fact_path: str | None = None
    default_filter_plugin_path: list[str] = [
        "~/.ansible/plugins/filter",
        "/usr/share/ansible/plugins/filter",
    ]
    default_force_handlers: bool = False
    default_forks: int = 5
    default_gather_subset: list[str] = ["all"]
    default_gather_timeout: int = 10
    default_gathering: Literal["smart", "explicit", "implicit"] = "smart"
    default_handler_includes_static: bool = False
    default_hash_behaviour: str = "replace"
    default_host_list: list[str] = ["/etc/ansible/hosts"]
    default_httpapi_plugin_path: list[str] = [
        "~/.ansible/plugins/httpapi",
        "/usr/share/ansible/plugins/httpapi",
    ]
    default_internal_poll_interval: float = 0.001
    default_inventory_plugin_path: list[str] = [
        "~/.ansible/plugins/inventory",
        "/usr/share/ansible/plugins/inventory",
    ]
    default_jinja2_extensions: list[str] = []
    default_jinja2_native: bool = False
    default_keep_remote_files: bool = False
    default_libvirt_lxc_noseclabel: bool = False
    default_load_callback_plugins: bool = False
    default_local_tmp: str = "~/.ansible/tmp"
    default_log_filter: list[str] = []
    default_log_path: str | None = None
    default_lookup_plugin_path: list[str] = [
        "~/.ansible/plugins/lookup",
        "/usr/share/ansible/plugins/lookup",
    ]
    default_managed_str: str = "Ansible managed"
    default_module_args: str
    default_module_compression: str = "ZIP_DEFLATED"
    default_module_name: str = "command"
    default_module_path: list[str] = [
        "~/.ansible/plugins/modules",
        "/usr/share/ansible/plugins/modules",
    ]
    default_module_utils_path: list[str] = [
        "~/.ansible/plugins/module_utils",
        "/usr/share/ansible/plugins/module_utils",
    ]
    default_netconf_plugin_path: list[str] = [
        "~/.ansible/plugins/netconf",
        "/usr/share/ansible/plugins/netconf",
    ]
    default_no_log: bool = False
    default_no_target_syslog: bool = False
    default_null_representation: str | None = None
    default_poll_interval: int = 15
    default_private_key_file: str | None = None
    default_private_role_vars: bool = False
    default_remote_port: str | None = None
    default_remote_user: str | None = None
    # https://docs.ansible.com/ansible/latest/reference_appendices/config.html#collections-paths
    default_collections_path: list[str] = [
        "~/.ansible/collections",
        "/usr/share/ansible/collections",
    ]
    default_roles_path: list[str] = [
        "~/.ansible/roles",
        "/usr/share/ansible/roles",
        "/etc/ansible/roles",
    ]
    default_selinux_special_fs: list[str] = [
        "fuse",
        "nfs",
        "vboxsf",
        "ramfs",
        "9p",
        "vfat",
    ]
    default_stdout_callback: str = "default"
    default_strategy: str = "linear"
    default_strategy_plugin_path: list[str] = [
        "~/.ansible/plugins/strategy",
        "/usr/share/ansible/plugins/strategy",
    ]
    default_su: bool = False
    default_syslog_facility: str = "LOG_USER"
    default_task_includes_static: bool = False
    default_terminal_plugin_path: list[str] = [
        "~/.ansible/plugins/terminal",
        "/usr/share/ansible/plugins/terminal",
    ]
    default_test_plugin_path: list[str] = [
        "~/.ansible/plugins/test",
        "/usr/share/ansible/plugins/test",
    ]
    default_timeout: int = 10
    default_transport: str = "smart"
    default_undefined_var_behavior: bool = True
    default_vars_plugin_path: list[str] = [
        "~/.ansible/plugins/vars",
        "/usr/share/ansible/plugins/vars",
    ]
    default_vault_encrypt_identity: str | None = None
    default_vault_id_match: bool = False
    default_vault_identity: str = "default"
    default_vault_identity_list: list[str] = []
    default_vault_password_file: str | None = None
    default_verbosity: int = 0
    deprecation_warnings: bool = False
    devel_warning: bool = True
    diff_always: bool = False
    diff_context: int = 3
    display_args_to_stdout: bool = False
    display_skipped_hosts: bool = True
    docsite_root_url: str = "https://docs.ansible.com/ansible/"
    doc_fragment_plugin_path: list[str] = [
        "~/.ansible/plugins/doc_fragments",
        "/usr/share/ansible/plugins/doc_fragments",
    ]
    duplicate_yaml_dict_key: Literal["warn", "error", "ignore"] = "warn"
    enable_task_debugger: bool = False
    error_on_missing_handler: bool = True
    facts_modules: list[str] = ["smart"]
    galaxy_cache_dir: str = "~/.ansible/galaxy_cache"
    galaxy_display_progress: str | None = None
    galaxy_ignore_certs: bool = False
    galaxy_role_skeleton: str | None = None
    galaxy_role_skeleton_ignore: list[str] = ["^.git$", "^.*/.git_keep$"]
    galaxy_server: str = "https://galaxy.ansible.com"
    galaxy_server_list: str | None = None
    galaxy_token_path: str = "~/.ansible/galaxy_token"  # noqa: S105
    host_key_checking: bool = True
    host_pattern_mismatch: Literal["warning", "error", "ignore"] = "warning"
    inject_facts_as_vars: bool = True
    interpreter_python: str = "auto_legacy"
    interpreter_python_distro_map: dict[str, str]
    interpreter_python_fallback: list[str]
    invalid_task_attribute_failed: bool = True
    inventory_any_unparsed_is_failed: bool = False
    inventory_cache_enabled: bool = False
    inventory_cache_plugin: str | None = None
    inventory_cache_plugin_connection: str | None = None
    inventory_cache_plugin_prefix: str = "ansible_facts"
    inventory_cache_timeout: int = 3600
    inventory_enabled: list[str] = [
        "host_list",
        "script",
        "auto",
        "yaml",
        "ini",
        "toml",
    ]
    inventory_export: bool = False
    inventory_ignore_exts: str
    inventory_ignore_patterns: list[str] = []
    inventory_unparsed_is_failed: bool = False
    localhost_warning: bool = True
    max_file_size_for_diff: int = 104448
    module_ignore_exts: str
    netconf_ssh_config: str | None = None
    network_group_modules: list[str] = [
        "eos",
        "nxos",
        "ios",
        "iosxr",
        "junos",
        "enos",
        "ce",
        "vyos",
        "sros",
        "dellos9",
        "dellos10",
        "dellos6",
        "asa",
        "aruba",
        "aireos",
        "bigip",
        "ironware",
        "onyx",
        "netconf",
        "exos",
        "voss",
        "slxos",
    ]
    old_plugin_cache_clearing: bool = False
    paramiko_host_key_auto_add: bool = False
    paramiko_look_for_keys: bool = True
    persistent_command_timeout: int = 30
    persistent_connect_retry_timeout: int = 15
    persistent_connect_timeout: int = 30
    persistent_control_path_dir: str = "~/.ansible/pc"
    playbook_dir: str | None
    playbook_vars_root: Literal["top", "bottom", "all"] = "top"
    plugin_filters_cfg: str | None = None
    python_module_rlimit_nofile: int = 0
    retry_files_enabled: bool = False
    retry_files_save_path: str | None = None
    run_vars_plugins: str = "demand"
    show_custom_stats: bool = False
    string_conversion_action: Literal["warn", "error", "ignore"] = "warn"
    string_type_filters: list[str] = [
        "string",
        "to_json",
        "to_nice_json",
        "to_yaml",
        "to_nice_yaml",
        "ppretty",
        "json",
    ]
    system_warnings: bool = True
    tags_run: list[str] = []
    tags_skip: list[str] = []
    task_debugger_ignore_errors: bool = True
    task_timeout: int = 0
    transform_invalid_group_chars: Literal[
        "always",
        "never",
        "ignore",
        "silently",
    ] = "never"
    use_persistent_connections: bool = False
    variable_plugins_enabled: list[str] = ["host_group_vars"]
    variable_precedence: list[str] = [
        "all_inventory",
        "groups_inventory",
        "all_plugins_inventory",
        "all_plugins_play",
        "groups_plugins_inventory",
        "groups_plugins_play",
    ]
    verbose_to_stderr: bool = False
    win_async_startup_timeout: int = 5
    worker_shutdown_poll_count: int = 0
    worker_shutdown_poll_delay: float = 0.1
    yaml_filename_extensions: list[str] = [".yml", ".yaml", ".json"]

    def __init__(
        self,
        config_dump: str | None = None,
        data: dict[str, object] | None = None,
        cache_dir: Path | None = None,
    ) -> None:
        """Load config dictionary."""
        super().__init__()

        self.cache_dir = cache_dir
        if data:
            self.data = copy.deepcopy(data)
        else:
            if not config_dump:
                env = os.environ.copy()
                # Avoid possible ANSI garbage
                env["ANSIBLE_FORCE_COLOR"] = "0"
                config_dump = subprocess.check_output(
                    ["ansible-config", "dump"],
                    universal_newlines=True,
                    env=env,
                )

            for match in re.finditer(
                r"^(?P<key>[A-Za-z0-9_]+).* = (?P<value>.*)$",
                config_dump,
                re.MULTILINE,
            ):
                key = match.groupdict()["key"]
                value = match.groupdict()["value"]
                try:
                    self[key] = ast.literal_eval(value)
                except (NameError, SyntaxError, ValueError):
                    self[key] = value
        if data:
            return

    def __getattribute__(self, attr_name: str) -> object:
        """Allow access of config options as attributes."""
        parent_dict = super().__dict__  # pylint: disable=no-member
        if attr_name in parent_dict:
            return parent_dict[attr_name]

        data = super().__getattribute__("data")
        if attr_name == "data":  # pragma: no cover
            return data

        name = attr_name.upper()
        if name in data:
            return data[name]
        if name in AnsibleConfig._aliases:
            return data[AnsibleConfig._aliases[name]]

        return super().__getattribute__(attr_name)

    def __getitem__(self, name: str) -> object:
        """Allow access to config options using indexing."""
        return super().__getitem__(name.upper())

    def __copy__(self) -> AnsibleConfig:
        """Allow users to run copy on Config."""
        return AnsibleConfig(data=self.data)

    def __deepcopy__(self, memo: object) -> AnsibleConfig:
        """Allow users to run deeepcopy on Config."""
        return AnsibleConfig(data=self.data)


__all__ = [
    "AnsibleConfig",
    "ansible_version",
    "parse_ansible_version",
]
