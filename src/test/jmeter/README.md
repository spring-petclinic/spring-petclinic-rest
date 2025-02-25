# PetClinic JMeter Performance Test

## Overview

This JMeter test plan is designed to benchmark and measure the scalability of the **Spring PetClinic REST API**. The test performs CRUD operations on the API endpoints, simulating real-world usage patterns.

### What This Test Covers

- Creating, updating, retrieving, and deleting **owners** and **pets**.
- Scheduling and managing **vet visits**.
- Configurable workload parameters for **scalability testing**.

## Running the Test

### Prerequisites

Ensure you have the following installed:

- **Apache JMeter 5.6.3+** ([Download here](https://jmeter.apache.org/download_jmeter.cgi))
- **Spring PetClinic REST API running locally**
    ```sh
    # Navigate to the Spring PetClinic REST project directory and start the application
    cd /path/to/spring-petclinic-rest
    mvn spring-boot:run
    ```

_(Runs on http://localhost:9966 by default)_

### Running the Test from CLI

Run the JMeter test in **non-GUI mode**:

```sh
jmeter -n -t src/test/jmeter/petclinic-jmeter-crud-benchmark.jmx \
 -Jthreads=100 -Jduration=600 -Jops=2000 -Jramp_time=120 \
 -l results/petclinic-test-results.jtl
```

#### CLI Parameters

| Parameter  | Description                  | Default Value |
|------------|------------------------------|---------------|
| `threads`  | Number of concurrent users   | 50            |
| `duration` | Duration of the test (seconds) | 300          |
| `ops`      | Target throughput (operations/sec)  | 1000          |
| `ramp_time` | Time to ramp up threads (seconds) | 60       |

## Analyzing Test Results

1. **Generate an HTML Report**

    To generate an **interactive HTML performance report**:
    ```sh
    jmeter -g results/petclinic-test-results.jtl -o results/html-report
    ```

    Then open `results/html-report/index.html` in your browser.

2. **View Raw Results in CLI**

    To quickly inspect the last 20 results:
    ```sh
    tail -n 20 results/petclinic-test-results.jtl
    ```

3. **Process CSV Results for Custom Analysis**

    JMeter results are stored as `.jtl` files, which use `CSV` format by default unless configured otherwise. You can analyze them using:
    - Python Pandas:
        ```python
        import pandas as pd
        df = pd.read_csv('results/petclinic-test-results.jtl')
        print(df.describe())
        ```
    - Command-line tools (`awk`):
        ```sh
        awk -F',' '{print $1, $2, $3}' results/petclinic-test-results.jtl | head -20
        ```

## Next Steps

- Run with different configurations to simulate varied workloads.
- Integrate into CI/CD pipelines for automated performance testing.
___

Contribute: Feel free to submit PRs to improve the test coverage or parameters!