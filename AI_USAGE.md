# AI Usage

## Tools and models
- AI assistant: GitHub Copilot in VS Code.
- Model used: MAI-Code-1.1-Flash.
- Supporting tools: Maven wrapper, Java 25 JDK, and local build/test output for verification.

## Meaningful work delegated to AI
- Reviewed the project state and targeted the highest-value risk in the owner search flow.
- Inspected the likely failure path in the service and repository layers: owner last-name queries and blank/whitespace handling.
- Proposed and refined a focused regression test for blank last-name input.
- Suggested a targeted fix so blank or whitespace-only last names behave like a no-filter lookup rather than a literal search.
- Helped narrow the root cause to `lastName` handling before the wildcard (`%`) was appended, rather than to a general runtime upgrade problem.

## Session evidence of AI influence
- The work was driven by a targeted prompt: “I’ve selected the owner-search path as the high-value risk: blank or whitespace last names can silently behave differently from ‘no filter’.”
- The AI then traced the likely code path in the service and repository stack, specifically `ClinicServiceImpl`, `JpaOwnerRepositoryImpl`, and `JdbcOwnerRepositoryImpl`.
- The regression was added before the final fix so the behavior was explicit and reproducible: blank/whitespace last names should return the full owner list.

## Independent verification
- I did not accept the AI patch on faith. I re-ran the exact service regression under Java 25.
- Fresh command used for proof: `./mvnw.cmd -q -Dtest=ClinicServiceJpaTests test`
- Fresh result from the surefire report: `Tests run: 37, Failures: 0, Errors: 0, Skipped: 0`.
- I also checked the actual repository logic and the generated report instead of trusting only the patch diff.

## Rejected or changed suggestion
- I rejected a broader “normalize every string input everywhere” suggestion because it would have widened the change beyond the owner search contract and risked unintended side effects in unrelated APIs.
- Instead, I kept the fix narrowly scoped to the owner last-name search flow and the repository implementations that append the wildcard prefix.

## AI errors or misunderstandings
- The first test command used a fully qualified class name that Maven did not accept as a lifecycle goal; this was a command-shape mistake, not a logic bug in the project.
- The AI initially treated the issue as a general runtime or upgrade problem, but the failing assertion and code trace clearly narrowed it to blank search semantics. That was corrected before finalizing the fix.

## What I would not merge without more verification
- I would not treat this as a full project-wide validation. The verification here is focused on the selected risk area and the service-layer regression.
- Before a broader release, I would want a wider integration pass across the full suite or at least the owner and controller flows under multiple database profiles, because this patch changes how a user-facing search behaves and should be cross-checked beyond the current targeted proof.
