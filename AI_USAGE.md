# AI Usage Report

## 1. AI Tool Used

**Tool:** ChatGPT  
**Model:** GPT-6

ChatGPT was used
## 2. Meaningful Work Delegated

The assistant was asked to:
- Review the assignment and identify a focused quality improvement.
- Inspect the supplied Spring Petclinic REST source archive.
- Trace the nested Owner–Pet update operation and compare its behaviour with the OpenAPI response contract.
- Propose and implement a guard against updating a pet through a different owner's nested URL.
- Add a negative controller test proving that the mismatched request returns 404 and does not call the save operation.

## 3. Prompt / Session Evidence

The user asked:

> To inspect and analyze the code and suggest the recommendations

## 4. Independent Verification

The assistant inspected the supplied source files directly:
- `OwnerRestControllerV1.java`
- `OwnerRestControllerV1Tests.java`
- `src/main/resources/openapi.yml`
- `Pet.java` and `Owner.java`
- `pom.xml`

The original `updateOwnersPet` method was observed to check that owner and pet records were non-null but not to compare the pet's owner ID with the path owner ID. The OpenAPI operation describes 404 as “Pet not found for this owner”.

The change was reviewed statically to ensure the relationship check occurs before mutating the pet and calling `savePet`. The test was reviewed to ensure it uses a pet owned by a different owner, expects 404 and verifies `savePet` is never called. This was a static review by the same AI assistant and is **not independent verification**. The user has not yet independently reviewed the patch or run the test.

The Maven wrapper bootstrap check `./mvnw -version` was attempted. The wrapper could not download Maven because the environment failed to resolve `repo.maven.apache.org` (`UnknownHostException`). Consequently, the assignment baseline command `./mvnw clean install` and the focused test were not executed. Runtime correctness is not claimed.

## 5. Suggestions Changed or Not Accepted

The initial broad idea was to cover Owner–Pet–Visit relationships generally. The implementation was narrowed to the nested Owner–Pet update operation because source inspection showed a concrete missing relationship check and the OpenAPI contract provided a clear expected 404 behaviour.

The separate `addVisitToOwner` concern was deliberately left out of this change to keep the implementation focused. It is documented as follow-up work rather than being silently included without tests.


## 6. Work That Requires Further Verification

Before merge:
- Confirm the source revision is exactly the assignment revision.
- Run `./mvnw -Dtest=OwnerRestControllerV1Tests test` with Maven Central access.
- Run `./mvnw clean verify` and review the full test report.
- Review the final Git diff and checkstyle/formatting.
- Add and test owner/pet relationship validation in `addVisitToOwner` as a follow-up if time permits.

No test pass or successful build is claimed in this report.
