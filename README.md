# Performance-Aware Cuckoo Hashing Under High Load

An empirical analysis and implementation of advanced Cuckoo Hashing architectures designed to mitigate cycle-formation failures under high load factors. This project benchmarks standard Cuckoo Hashing against modern variants (Stash, $d$-ary, Bucketized) and classic baselines using real-world network data.

##  Project Overview
Traditional open-addressing and closed-addressing hashing methods provide expected `O(1)` performance but suffer from poor worst-case behavior under high load. Cuckoo Hashing guarantees worst-case `O(1)` lookups but is fragile, mathematically failing near a 50% load factor due to cycle formation. 

This project explores architectural shifts to bypass this 50% threshold, specifically implementing a constant-sized "Stash" and bucketized arrays to absorb collisions, successfully pushing stability past 90% load factors while retaining strict `O(1)` lookup bounds.

##  Algorithms Implemented
All data structures expose identical `insert(k)`, `lookup(k)`, and `delete(k)` APIs for fair lifecycle benchmarking:
1. **Standard Cuckoo Hashing** (2 tables, strict alternating evictions)
2. **Cuckoo Hashing with a Stash** (Auxiliary 4-item array to intercept cycles)
3. **3-ary Cuckoo Hashing** (3 independent tables to increase routing options)
4. **Bucketized Cuckoo Hashing** (Two-Way Chaining with 4 items per bucket)
5. **Linear Probing** (Open-addressing baseline)
6. **Separate Chaining** (Closed-addressing baseline using Linked Lists)

##  Dataset
The benchmark utilizes the **NASA HTTP Web Server Logs (Jul 95)**. 
A custom data ingestion pipeline parses the logs, extracts the primary identifier (IP/Host), converts it to a 32-bit integer, and deduplicates the traffic to ensure accurate load-factor scaling.

##  How to Run the Benchmark

### Prerequisites
* **Java:** JDK 8 or higher
* **Python:** Python 3.x with `pandas`, `matplotlib`, and `seaborn` installed (`pip install pandas matplotlib seaborn`)

### Step 1: Download the Dataset
Download and extract the public NASA access logs into the *project root/datasets/* directory.

    curl -O ftp://ita.ee.lbl.gov/traces/NASA_access_log_Jul95.gz
    mkdir -p datasets && gunzip -c NASA_access_log_Jul95.gz > datasets/NASA_access_log_Jul95

------------***OR***-----------

1. Download the zip file here: https://drive.google.com/file/d/1_nkgi5939YIJhlQRXx6O5khiq1aSgJN4/view?usp=sharing

2. Extract it into the *project root/datasets/* directory.

### Step 2: Compile the Java Source Code
    

    javac *.java

### Step 3: Execute the Runner
Run the main benchmark suite. This will ingest the data, test load factors from 50% to 95%, and output the metrics as a CSV file.

    java FinalProjectRunner datasets/NASA_access_log_Jul95 > benchmark_results.csv


### Step 4: Generate the Graphs
Run the Python visualization script to parse the CSV and generate publication-quality graphs.

    mkdir plots
    python plot_graphs.py

This will output the four visualizations (*insertion_time.png*, *failures.png*, *delete_time.png*, *lookup_time.png*) directly into the *plots/* directory.



## Results Summary
**The 50% Limit:** Standard Cuckoo Hashing reliably begins failing at exactly a 50% load factor.

**Stash Effectiveness:** Adding a 4-item stash successfully catches cycle-inducing keys, maintaining a 0% failure rate at the critical 50% boundary.

**High-Density Winners:** The 3-ary and Bucketized variants proved most resilient, maintaining O(1) speeds and zero failures well past 80% and 90% load factors, respectively.


***