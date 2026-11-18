# Git Branch and Push Instructions

> Important: assignment revision `4cd8e1b0cd42578e882247d8801f6be5d402f118`.
## 1. Create your fork

Fork `https://github.com/spring-petclinic/spring-petclinic-rest` into your own GitHub account.

## 2. Clone your fork and check out the required assignment revision

Replace `<your-github-username>` with your GitHub username:

```bash
git clone https://github.com/<your-github-username>/spring-petclinic-rest.git
cd spring-petclinic-rest
git fetch origin
git checkout 4cd8e1b0cd42578e882247d8801f6be5d402f118
git switch -c feature/owner-pet-relationship-validation
```

Confirm the revision:

```bash
git rev-parse HEAD
```

It must print `4cd8e1b0cd42578e882247d8801f6be5d402f118`.

## 3. Apply the implementation change

Copy `IMPLEMENTATION.patch` to the repository root, then try:

```bash
git apply --check IMPLEMENTATION.patch
git apply IMPLEMENTATION.patch
```

If `git apply --check` fails, do not force it. Open the two files and manually apply the equivalent logic only after checking the code at the pinned revision. The patch was generated against the uploaded `master` ZIP snapshot, not a verified checkout of the assignment commit.

Copy `QUALITY.md`, `AI_USAGE.md`, `AI_PROMPT_EVIDENCE.md` and `PR_DESCRIPTION.md` into the repository root. Merge the verification instructions into the repository's existing README rather than deleting unrelated project documentation.

## 4. Run verification

```bash
./mvnw -Dtest=OwnerRestControllerV1Tests test
```

If focused tests pass, run:

```bash
./mvnw clean verify
```

Record actual results in `QUALITY.md` and `AI_USAGE.md`. The commands were not successfully executed in the preparation environment because the Maven wrapper could not resolve `repo.maven.apache.org` to download Maven.

## 5. Review, commit and push

```bash
git diff --check
git status
git diff

git add src/main/java/org/springframework/samples/petclinic/rest/controller/v1/OwnerRestControllerV1.java
git add src/test/java/org/springframework/samples/petclinic/rest/controller/OwnerRestControllerV1Tests.java
git add QUALITY.md AI_USAGE.md AI_PROMPT_EVIDENCE.md PR_DESCRIPTION.md README.md

git diff --cached --check
git diff --cached
git commit -m "fix: validate owner pet relationship on nested update"
git push -u origin feature/owner-pet-relationship-validation
```

Do not push until the exact revision, test results and final diff have been checked. Do not open a pull request against upstream unless the maintainers invited you to do so.
