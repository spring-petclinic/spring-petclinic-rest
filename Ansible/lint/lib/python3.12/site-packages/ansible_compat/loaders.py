"""Utilities for loading various files."""

from __future__ import annotations

from typing import TYPE_CHECKING, Any

import yaml

from ansible_compat.errors import InvalidPrerequisiteError

if TYPE_CHECKING:  # pragma: no cover
    from pathlib import Path


def yaml_from_file(path: Path) -> Any:  # noqa: ANN401
    """Return a loaded YAML file."""
    with path.open(encoding="utf-8") as content:
        return yaml.load(content, Loader=yaml.SafeLoader)


def colpath_from_path(path: Path) -> str | None:
    """Return a FQCN from a path."""
    galaxy_file = path / "galaxy.yml"
    if galaxy_file.exists():
        galaxy = yaml_from_file(galaxy_file)
        for k in ("namespace", "name"):
            if k not in galaxy:
                msg = f"{galaxy_file} is missing the following mandatory field {k}"
                raise InvalidPrerequisiteError(msg)
        return f"{galaxy['namespace']}/{galaxy['name']}"
    return None
