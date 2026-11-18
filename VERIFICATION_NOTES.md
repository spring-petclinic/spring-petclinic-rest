# Verification Notes

- Runtime: OpenJDK 21.0.12.1 was present in the preparation container.
- Maven wrapper bootstrap check attempted: `./mvnw -version`.
- Outcome: wrapper failed before Maven startup while downloading Maven because `repo.maven.apache.org` could not be resolved (`java.net.UnknownHostException`).
- Assignment baseline command `./mvnw clean install`: not executed.
- Focused command prepared: `./mvnw -Dtest=OwnerRestControllerV1Tests test`. Not run.
- `git diff --check` was run against a temporary baseline created from the uploaded ZIP and reported no whitespace errors. This is not a compile or test result.
- No test pass is claimed.
