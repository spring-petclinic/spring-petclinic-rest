# PetClinic API - Postman Non-Regression Tests

## Overview

This project contains a Postman collection designed to validate the **Spring PetClinic REST API** through a series of functional and regression tests. The tests simulate user actions on the API, and are executed via Newman, with results exported to HTML reports.

### What This Test Covers

- It focuses on simulating the real-world scenario where a new customer brings a pet for a first visit, testing the nominal flow end-to-end.
> Note: We can extend the tests with more scenarios

## Running the Test

### Prerequisites

Ensure the following are available:

- **Node.js installed** ([Download here](https://nodejs.org))
- **jq installed** ([Download here](https://jqlang.org/download/))
- **Spring PetClinic REST API running locally**

```sh
# Run the application from its root directory
cd /path/to/spring-petclinic-rest
mvn spring-boot:run
```

_(Runs by default at http://localhost:9966)_

### Running the Test with Newman

There's 2 ways to run the test:

1. Giving Execution Permission to the script file:
```sh
chmod +x postman-tests.sh
./postman-tests.sh
```
2. Without Permission to the script file:
```sh
zsh postman-tests.sh
```
> Note: You can use your currently bash installed. Like: "bash postman-tests.sh"

This script will:

I. Print a readable scenario name before each execution (e.g., "🔎 Scenario: 01 - First Visit")
II. Run the corresponding test folder in the collection using `npx newman`
III. Generate an HTML report for each scenario in `src/test/postman/reports/`
IV. Output test result summaries directly to your terminal

## Analyzing Test Results

### 1. View HTML Report

After running the tests, open the generated report in your browser:

- Open the the report file in `src/test/postman/reports/`

The report includes:

- Status of each request
- Response time charts
- Assertions summary
- Environment variables at runtime

### 2. Command Line Summary

The shell script provides immediate feedback:

```sh
📄 HTML report generated at: src/test/postman/reports
```

## Project Structure

```
src/
├── test/
|    ├── postman/
|        ├── petclinic-env.postman_environment.json  # Environment configuration (e.g. baseUrl)
|        ├── petclinic-nonregressiontests.postman_collection.json  # Main collection with test scenarios
|        └── reports/ # Generated HTML reports
├── postman-tests.sh # Execution script for running the tests
```

## Notes

- `ownerId` and `petId` are kept in the environment file to aid debugging in Postman GUI.
- `--suppress-exit-code` ensures the HTML report is generated even if assertions fail.
- Newman is invoked via `npx` to avoid global installation.

---

Contributions welcome! Feel free to submit pull requests to add new scenarios or enhance the script/reporting.
