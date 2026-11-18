# Quality Engineering Assessment

## Submission status and source provenance

**Implemented change:** Enforce the Owner–Pet relationship when updating a pet through the nested owner route.

**Source supplied for this work:** `git repo. revision `4cd8e1b0cd42578e882247d8801f6be5d402f118`. Its `pom.xml` identifies project version `4.0.2` and Spring Boot parent `4.1.2`. The required assignment revision must therefore still be confirmed before claiming compliance with the pinned-revision requirement.

## 1. Scope and System Understanding

**Project:** Spring Petclinic REST  
**Selected area:** nested Owner–Pet resource integrity in the REST API.

The implementation is in `OwnerRestControllerV1`. The `PUT /api/owners/{ownerId}/pets/{petId}` operation obtains the owner and pet independently. In the supplied snapshot, it originally checked only that both records existed before updating and saving the pet. It did not check that the pet's actual owner ID matched the `ownerId` in the request path.

The OpenAPI definition in `src/main/resources/openapi.yml` documents HTTP 404 as “Pet not found for this owner” for this operation. The original controller behaviour therefore did not enforce the documented nested-resource boundary.

## 2. Risks Considered and Prioritisation

| Priority | Finding / risk | Potential impact | Verification / disposition |
|---|---|---|---|
| P1 | **Confirmed in supplied snapshot:** `updateOwnersPet` did not verify that the pet belongs to the owner in the URL. | A caller with the required application role could update a different owner's pet through a mismatched nested URL. | Added a controller guard and a negative MockMvc test. The test expects 404 and verifies `savePet` is not called. Runtime execution is blocked by dependency-download network failure. |
| P2 | **Confirmed in supplied snapshot:** `addVisitToOwner` accepts `ownerId` and `petId`, but the implementation constructs a Pet using only the supplied pet ID and does not load/validate the owner or confirm the pet belongs to that owner. | A visit may be associated with a pet through a mismatched owner/pet nested route. | Not changed in this timeboxed submission. Recommended next change: load owner and pet, validate the relationship, return 404 for a missing/mismatched resource, and add negative tests. |
| P3 | Resource relationship checks may differ between other nested operations and their OpenAPI contracts. | Inconsistent resource boundaries across API operations. | Not comprehensively assessed; extend the same review to every nested endpoint. |
| P4 | Invalid or unknown IDs may produce inconsistent responses across resources. | Client integration and error handling become less predictable. | Existing tests cover some not-found cases; full endpoint-wide consistency was not assessed. |
| P5 | Create/update responses may not always be verified against persisted state. | A success response could conceal persistence or relationship issues. | Existing test conventions include controller-level MockMvc tests; repository-backed persistence verification remains future work. |

### Why P1 was selected

P1 is a narrow, high-impact data-integrity issue with a direct contract expectation. It can be addressed with a small controller guard and tested through the existing MockMvc test style. P2 is also a concrete concern in the supplied snapshot, but was left out to keep this change limited to one operation.

## 3. Changes and Rationale

### Production change

File: `src/main/java/org/springframework/samples/petclinic/rest/controller/v1/OwnerRestControllerV1.java`

`updateOwnersPet` now proceeds only when:
- the owner in the URL exists;
- the pet exists;
- the pet has an owner; and
- the pet's owner ID equals the owner ID in the URL.

Otherwise, the method returns HTTP 404, consistent with the OpenAPI response description “Pet not found for this owner”. No pet update is saved for a mismatched owner/pet pair.

### Test changes

File: `src/test/java/org/springframework/samples/petclinic/rest/controller/OwnerRestControllerV1Tests.java`

- Updated the successful nested-pet test fixture to attach the pet to the requested owner.
- Added `testUpdateOwnersPetRejectsPetBelongingToDifferentOwner`.
- The negative test submits a pet owned by one owner under another owner's URL, expects HTTP 404, and verifies `ClinicService.savePet` is never called.

## 4. Evidence and Reproducible Verification

### Baseline build attempt

Maven wrapper bootstrap check attempted:

```bash
./mvnw -version
```

**Result:** Could not start Maven. The wrapper attempted to download Maven from Maven Central and failed with `java.net.UnknownHostException: repo.maven.apache.org` in this environment. The assignment baseline command `./mvnw clean install` was therefore **not executed**. This is an environment/network limitation; it is not evidence that the project build or tests pass or fail.

### Focused verification command

Run from the repository root in an environment with Maven Central access:

```bash
./mvnw -Dtest=OwnerRestControllerV1Tests test
```

**Execution status:** Not run. Maven could not be downloaded because network name resolution was unavailable. No passing test result is claimed.

### Evidence produced

- Source inspection of `OwnerRestControllerV1` and the corresponding OpenAPI operation.
- A production guard for owner/pet relationship validation.
- A negative MockMvc test with a `savePet` non-interaction assertion.
- A reviewable two-file code change.

The test report is not available because tests were not executed.

## 5. Confidence Limits

Static inspection supports the finding that the supplied snapshot's original `updateOwnersPet` implementation did not validate the relationship. The code change and test are present in the working copy, but runtime confidence is limited because Maven and dependencies could not be downloaded and the tests were not executed.

This work does not establish complete API correctness, authorization/security correctness beyond the route-level scenario, performance, concurrency behaviour, or cross-database compatibility.

## 6. Work Left Out

- Verify and rebase/apply the change on the exact required Git revision.
- Execute baseline and focused Maven tests in a network-enabled environment.
- Fix and test the nested visit creation relationship check in `addVisitToOwner`.
- Review all other nested API operations against their OpenAPI contracts.
- Add repository-backed persistence tests and broader regression coverage.
- Performance, concurrency and cross-database testing.

## 7. Next Step

First obtain the exact assignment revision and run `./mvnw -Dtest=OwnerRestControllerV1Tests test`. Then implement the analogous owner/pet validation for `addVisitToOwner`, add positive and negative tests, and run the complete `./mvnw clean verify` suite.

## 8. Time Spent

Counted time was tracked approximately. The assignment's three-hour timebox includes investigation, implementation, verification and both reports.
