# Quality Improvement: Enforce nested Owner–Pet relationship on update

## Summary

This change validates that a pet belongs to the owner identified in the nested URL before updating it.

## Risk addressed

Previously, the supplied source snapshot's `updateOwnersPet` method checked only that the owner and pet existed independently. It did not verify the relationship between them. This could allow a mismatched owner/pet path to update a pet belonging to another owner.

## Changes

- Added an owner-ID relationship check in `OwnerRestControllerV1.updateOwnersPet`.
- Return HTTP 404 when the pet is missing, has no owner, or belongs to a different owner.
- Added a negative MockMvc regression test that expects 404 and verifies `ClinicService.savePet` is not called.
- Updated the positive test fixture to attach the pet to the expected owner.

## Verification

Focused command:

```bash
./mvnw -Dtest=OwnerRestControllerV1Tests test
```

Not executed in the preparation environment because Maven wrapper download failed with `UnknownHostException: repo.maven.apache.org`. No test pass is claimed.

## Source revision caveat

The provided ZIP was a `master` archive without Git metadata. Confirm/apply this change against assignment revision `4cd8e1b0cd42578e882247d8801f6be5d402f118` before merging.

## Remaining work

- Run focused and full Maven verification.
- Review nested visit creation: `addVisitToOwner` in the supplied snapshot does not validate owner/pet relationship.
