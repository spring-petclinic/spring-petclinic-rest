"""Custom types."""

from __future__ import annotations

from collections.abc import Mapping, Sequence
from typing import TypeAlias

JSON: TypeAlias = dict[str, "JSON"] | list["JSON"] | str | int | float | bool | None
JSON_ro: TypeAlias = (
    Mapping[str, "JSON_ro"] | Sequence["JSON_ro"] | str | int | float | bool | None
)

__all__ = ["JSON", "JSON_ro"]
