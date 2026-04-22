# Performance-Aware Cuckoo Hashing Under High Load

An empirical analysis and implementation of advanced Cuckoo Hashing architectures designed to mitigate cycle-formation failures under high load factors. This project benchmarks standard Cuckoo Hashing against modern variants (Stash, $d$-ary, Bucketized) and classic baselines using real-world network data.

## Project Overview
Traditional open-addressing and closed-addressing hashing methods provide expected `O(1)` performance but suffer from poor worst-case behavior under high load. Cuckoo Hashing guarantees worst-case `O(1)` lookups but is fragile, mathematically failing near a 50% load factor due to cycle formation. 

This project explores architectural shifts to bypass this 50% threshold, specifically implementing a constant-sized "Stash" and bucketized arrays to absorb collisions, successfully pushing stability past 90% load factors while retaining strict `O(1)` lookup bounds.

## Algorithms Implemented
All data structures expose identical `insert(k)`, `lookup(k)`, and `delete(k)` APIs for fair lifecycle benchmarking:
1. **Standard Cuckoo Hashing** (2 tables, strict alternating evictions)
2. **Cuckoo Hashing with a Stash** (Auxiliary 4-item array to intercept cycles)
3. **3-ary Cuckoo Hashing** (3 independent tables to increase routing options)
4. **Bucketized Cuckoo Hashing** (Two-Way Chaining with 4 items per bucket)
5. **Linear Probing** (Open-addressing baseline)
6. **Separate Chaining** (Closed-addressing baseline using Linked Lists)

## Datasets
The benchmark utilizes three diverse real-world datasets to test structural resilience across different traffic distributions:
1. **NASA HTTP Web Server Logs (Jul 95):** HTTP requests deduplicated by Host/IP.
2. **KDD Cup 1999 (10% Subset):** Network intrusion data deduplicated by exact connection signature.
3. **Internet Traffic:** Backbone internet traffic deduplicated by Source IP.

A custom dynamic data ingestion pipeline parses the logs, extracts the primary identifier based on the dataset type (`NASA`, `KDD`, or `INTERNET`), converts it to a 32-bit integer, and deduplicates the traffic to ensure accurate load-factor scaling.

## How to Run the Benchmark

### Prerequisites
* **Java:** JDK 8 or higher
* **Python:** Python 3.x with `pandas`, `matplotlib`, and `seaborn` installed (`pip install pandas matplotlib seaborn`)

### Step 1: Download the Datasets
Create a `datasets` folder in your project root and download the desired datasets from this drive folder: https://drive.google.com/drive/folders/19p2tOJzPeeL_-STrGNqV5sl-M8nTNGog?usp=sharing

### Step 2: Compile the Java Source Code

    javac *.java


### Step 3: Execute the Runner
The runner now requires two arguments: `<file_path>` and `<dataset_type>` (`NASA`, `KDD`, or `CAIDA`). Output the results to a dataset-specific CSV file.

**Example: NASA Dataset**

    java FinalProjectRunner datasets/NASA_access_log_Jul95.txt NASA > nasa_results.csv


**Example: KDD Dataset**

    java FinalProjectRunner datasets/kddcup1999_data.csv KDD > kdd_results.csv

**Example: INTERNET TRAFFIC Dataset**

    java FinalProjectRunner datasets/internet_traffic.csv INTERNET > internet_results.csv


### Step 4: Generate the Graphs
The Python visualization script takes two arguments: `<csv_file>` and `<prefix>` (to prevent overwriting graphs from different datasets). Ensure you have a `plots/` directory.

    mkdir -p plots
    python plot_graphs.py nasa_results.csv nasa
    python plot_graphs.py kdd_results.csv kdd
    python plot_graphs.py internet_results.csv internet

This will output prefixed visualizations (e.g., `nasa_insertion_time.png`, `kdd_failures.png`) directly into the `plots/` directory.

## 📈 Results Summary
* **The 50% Limit:** Standard Cuckoo Hashing reliably begins failing at exactly a 50% load factor.
* **Stash Effectiveness:** Adding a 4-item stash successfully catches cycle-inducing keys, maintaining a 0% failure rate at the critical 50% boundary.
* **High-Density Winners:** The 3-ary and Bucketized variants proved most resilient, maintaining `O(1)` speeds and zero failures well past 80% and 90% load factors, respectively.
* **Lifecycle Operations:** The inclusion of `delete_time` metrics illustrates the teardown speeds of each structure, completing the empirical lifecycle analysis.
