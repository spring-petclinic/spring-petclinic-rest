"""Module to deal with errors."""

from __future__ import annotations

from typing import TYPE_CHECKING, Any

from ansible_compat.constants import ANSIBLE_MISSING_RC, INVALID_PREREQUISITES_RC

if TYPE_CHECKING:  # pragma: no cover
    from subprocess import CompletedProcess  # noqa: S404


class AnsibleCompatError(RuntimeError):
    """Generic error originating from ansible_compat library."""

    code = 1  # generic error

    def __init__(
        self,
        message: str | None = None,
        proc: CompletedProcess[Any] | None = None,
    ) -> None:
        """Construct generic library exception."""
        super().__init__(message)
        self.proc = proc


class AnsibleCommandError(RuntimeError):
    """Exception running an Ansible command."""

    def __init__(self, proc: CompletedProcess[Any]) -> None:
        """Construct an exception given a completed process."""
        message = (
            f"Got {proc.returncode} exit code while running: {' '.join(proc.args)}"
        )
        super().__init__(message)
        self.proc = proc


class MissingAnsibleError(AnsibleCompatError):
    """Reports a missing or broken Ansible installation."""

    code = ANSIBLE_MISSING_RC

    def __init__(
        self,
        message: str | None = "Unable to find a working copy of ansible executable.",
        proc: CompletedProcess[Any] | None = None,
    ) -> None:
        """."""
        super().__init__(message)
        self.proc = proc


class InvalidPrerequisiteError(AnsibleCompatError):
    """Reports a missing requirement."""

    code = INVALID_PREREQUISITES_RC
