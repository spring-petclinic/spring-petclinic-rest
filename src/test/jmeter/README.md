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

| Parameter     | Description                          | Default Value |
|---------------|--------------------------------------|---------------|
| `threads`     | Number of concurrent users           | 50            |
| `duration`    | Duration of the test (seconds)       | 300           |
| `ops`         | Target throughput (operations/sec)   | 1000          |
| `ramp_time`   | Time to ramp up threads (seconds)    | 60            |

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
### Performance Summary Table


| **Transaction**                | **Total Requests** | **Avg Response Time (ms)** | **Min Response Time (ms)** | **Max Response Time (ms)** | **90th Percentile (ms)** | **95th Percentile (ms)** | **99th Percentile (ms)** |
|--------------------------------|--------------------|----------------------------|----------------------------|----------------------------|--------------------------|--------------------------|--------------------------|
| **Add Pet to Owner**           | 60,284             | 60.17                      | 1                          | 534                        | 151.00                   | 190.00                   | 255.15                   |
| **Create Owner**               | 60,284             | 145.94                     | 2                          | 696                        | 315.00                   | 368.00                   | 476.15                   |
| **Delete Owner**               | 60,224             | 158.76                     | 2                          | 825                        | 322.00                   | 375.00                   | 490.00                   |
| **Delete Pet**                 | 60,239             | 179.94                     | 2                          | 829                        | 358.00                   | 411.00                   | 550.00                   |
| **Get Pet Belonging to Owner** | 60,249             | 70.37                      | 1                          | 479                        | 171.00                   | 208.00                   | 260.50                   |
| **Schedule Visit**             | 60,279             | 166.48                     | 2                          | 880                        | 329.00                   | 381.00                   | 507.20                   |
| **Update Owner**               | 60,264             | 163.76                     | 2                          | 681                        | 345.00                   | 396.00                   | 486.35                   |
| **Update Pet**                 | 60,249             | 33.53                      | 2                          | 402                        | 84.00                    | 108.50                   | 161.50                   |


**Explanation:**
- **Total Requests**: Number of executions of the request.
- **Avg Response Time (ms)**: Average time taken for the request.
- **Min/Max Response Time (ms)**: The shortest and longest recorded response times.
- **Percentile Metrics**: The 90th, 95th, and 99th percentile response times show performance under load.


## Next Steps

- Run with different configurations to simulate varied workloads.
- Integrate into CI/CD pipelines for automated performance testing.
___

Contribute: Feel free to submit PRs to improve the test coverage or parameters!