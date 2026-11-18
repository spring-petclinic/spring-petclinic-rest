# AI Prompt Evidence

## Tool

ChatGPT (GPT-6).

## User requests recorded in this conversation

- “Follow the instructions clearly in the file I attach and generate clean steps to identify the risks”
- “Identify the risks and give me the changes that I need to make in the files”
- “Clear summary about the changes”
- The user then supplied forked repo `https://github.com/niteshkardi-droid/spring-petclinic-rest` as the source code to verify the changes.

## Work delegated

Inspect the supplied source snapshot, identify a concrete quality risk in the Owner/Pet nested API workflow, implement a small production fix and regression test, and prepare the reports with honest verification limits.

## Implementation prompt summary

Review the actual `OwnerRestControllerV1` implementation, its OpenAPI operation and existing `OwnerRestControllerV1Tests`. Check whether `PUT /api/owners/{ownerId}/pets/{petId}` verifies that the loaded pet belongs to the owner in the URL. If the check is missing, add the smallest guard consistent with the documented 404 response. Add a MockMvc regression test for a pet belonging to another owner and assert the service save method is not called. Do not claim tests passed unless they run.

## Limitations

The uploaded archive did not contain Git metadata and therefore did not prove the assignment's pinned commit. Maven could not be downloaded because DNS resolution for `repo.maven.apache.org` failed. The tests remain unexecuted.
