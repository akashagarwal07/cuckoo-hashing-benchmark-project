public class FinalProjectRunner {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java FinalProjectRunner <path_to_dataset>");
            return;
        }

        String filePath = args[0];
        int[] dataset = DataParser.parseLogFile(filePath);

        if (dataset == null || dataset.length == 0) return;

        // We will test the structures as they fill up from 10% to 95% capacity
        double[] loadFactors = {0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.85, 0.90, 0.95};
        
        System.out.println("\n--- GRAPH DATA OUTPUT ---");
        System.out.println("LoadFactor,Algorithm,InsertTime_ms,LookupTime_ms,DeleteTime_ms,Failures");

        for (double lf : loadFactors) {
            int capacity = (int) (dataset.length / lf); 
            runAllBenchmarks(capacity, dataset, lf);
        }
    }

private static void runAllBenchmarks(int capacity, int[] keys, double currentLoadFactor) {
        // Fix: Divide capacity by the number of internal tables for true load factors

        // 1. Standard Cuckoo (2 tables) -> capacity / 2
        long[] m1 = runFullLifecycle(new CuckooHashTable(capacity / 2), keys, 1);
        printCsvRow(currentLoadFactor, "Standard Cuckoo", m1);

        // 2. Cuckoo with Stash (2 tables) -> capacity / 2
        long[] m2 = runFullLifecycle(new CuckooWithStash(capacity / 2), keys, 2);
        printCsvRow(currentLoadFactor, "Cuckoo w/ Stash", m2);

        // 3. 3-ary Cuckoo (3 tables) -> capacity / 3
        long[] m3 = runFullLifecycle(new DAryCuckooHashTable(capacity / 3), keys, 3);
        printCsvRow(currentLoadFactor, "3-ary Cuckoo", m3);

        // 4. Bucketized Cuckoo (2 tables) -> capacity / 2
        long[] m4 = runFullLifecycle(new BucketizedCuckooHashTable(capacity / 2), keys, 4);
        printCsvRow(currentLoadFactor, "Bucketized Cuckoo", m4);

        // 5. Linear Probing (1 table) -> total capacity
        long[] m5 = runFullLifecycle(new LinearProbingHashTable(capacity), keys, 5);
        printCsvRow(currentLoadFactor, "Linear Probing", m5);

        // 6. Separate Chaining (1 array of chains) -> total capacity
        long[] m6 = runFullLifecycle(new SeparateChainingHashTable(capacity), keys, 6);
        printCsvRow(currentLoadFactor, "Separate Chaining", m6);
    }
    private static long[] runFullLifecycle(Object table, int[] keys, int type) {
        long insertStart = System.nanoTime();
        int insertFailures = 0;
        for (int key : keys) {
            boolean success = false;
            if (type == 1) success = ((CuckooHashTable) table).insert(key);
            else if (type == 2) success = ((CuckooWithStash) table).insert(key);
            else if (type == 3) success = ((DAryCuckooHashTable) table).insert(key);
            else if (type == 4) success = ((BucketizedCuckooHashTable) table).insert(key);
            else if (type == 5) success = ((LinearProbingHashTable) table).insert(key);
            else if (type == 6) success = ((SeparateChainingHashTable) table).insert(key);
            
            if (!success) insertFailures++;
        }
        long insertTime = System.nanoTime() - insertStart;

        long lookupStart = System.nanoTime();
        for (int key : keys) {
            if (type == 1) ((CuckooHashTable) table).lookup(key);
            else if (type == 2) ((CuckooWithStash) table).lookup(key);
            else if (type == 3) ((DAryCuckooHashTable) table).lookup(key);
            else if (type == 4) ((BucketizedCuckooHashTable) table).lookup(key);
            else if (type == 5) ((LinearProbingHashTable) table).lookup(key);
            else if (type == 6) ((SeparateChainingHashTable) table).lookup(key);
        }
        long lookupTime = System.nanoTime() - lookupStart;

        long deleteStart = System.nanoTime();
        for (int key : keys) {
            if (type == 1) ((CuckooHashTable) table).delete(key);
            else if (type == 2) ((CuckooWithStash) table).delete(key);
            else if (type == 3) ((DAryCuckooHashTable) table).delete(key);
            else if (type == 4) ((BucketizedCuckooHashTable) table).delete(key);
            else if (type == 5) ((LinearProbingHashTable) table).delete(key);
            else if (type == 6) ((SeparateChainingHashTable) table).delete(key);
        }
        long deleteTime = System.nanoTime() - deleteStart;

        return new long[]{insertTime, lookupTime, deleteTime, insertFailures};
    }

    private static void printCsvRow(double loadFactor, String name, long[] metrics) {
        double insMs = metrics[0] / 1000000.0;
        double lookMs = metrics[1] / 1000000.0;
        double delMs = metrics[2] / 1000000.0;
        long failures = metrics[3];
        
        // Formatted perfectly for Excel or Python graphing
        System.out.printf("%.2f,%s,%.2f,%.2f,%.2f,%d\n", 
                loadFactor, name, insMs, lookMs, delMs, failures);
    }
}