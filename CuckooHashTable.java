import java.util.Random;

public class CuckooHashTable {
    private int[] table1;
    private int[] table2;
    private int size;
    
    // Hash function parameters: h(x) = ((a*x + b) % PRIME) % size
    private static final int PRIME = 1000000007; // A large prime
    private int a1, b1, a2, b2;
    
    private static final int EMPTY = Integer.MIN_VALUE;
    private static final int MAX_LOOP = 100; // Threshold before triggering a rehash 

    public CuckooHashTable(int capacity) {
        this.size = capacity;
        this.table1 = new int[size];
        this.table2 = new int[size];
        
        // Initialize tables with EMPTY marker
        for (int i = 0; i < size; i++) {
            table1[i] = EMPTY;
            table2[i] = EMPTY;
        }
        
        generateNewHashFunctions();
    }

    // Generates new random parameters for h1 and h2
    private void generateNewHashFunctions() {
        Random rand = new Random();
        a1 = rand.nextInt(PRIME - 1) + 1; // a > 0
        b1 = rand.nextInt(PRIME);
        
        a2 = rand.nextInt(PRIME - 1) + 1;
        b2 = rand.nextInt(PRIME);
    }

    private int hash1(int key) {
        // Handle negative keys cleanly before modulo
        long hash = ((long) a1 * Math.abs(key) + b1) % PRIME;
        return (int) (hash % size);
    }

    private int hash2(int key) {
        long hash = ((long) a2 * Math.abs(key) + b2) % PRIME;
        return (int) (hash % size);
    }

    public boolean lookup(int key) {
        if (key == EMPTY) return false;
        return table1[hash1(key)] == key || table2[hash2(key)] == key; 
    }

    public void delete(int key) {
        if (key == EMPTY) return;
        
        if (table1[hash1(key)] == key) {
            table1[hash1(key)] = EMPTY;
            return;
        }
        if (table2[hash2(key)] == key) {
            table2[hash2(key)] = EMPTY;
        }
    }

    public boolean insert(int key) {
        if (key == EMPTY) throw new IllegalArgumentException("Cannot insert the EMPTY marker.");
        if (lookup(key)) return true; // Key already exists

        int currentKey = key;
        int tableID = 1; // Start by trying to insert into Table 1

        for (int i = 0; i < MAX_LOOP; i++) { 
            if (tableID == 1) {
                int pos1 = hash1(currentKey);
                if (table1[pos1] == EMPTY) {
                    table1[pos1] = currentKey;
                    return true;
                }
                // Kick out the existing key and take its place
                int kickedKey = table1[pos1];
                table1[pos1] = currentKey;
                currentKey = kickedKey;
                tableID = 2; // The kicked key must now find a home in Table 2
            } else {
                int pos2 = hash2(currentKey);
                if (table2[pos2] == EMPTY) {
                    table2[pos2] = currentKey;
                    return true;
                }
                // Kick out the existing key and take its place
                int kickedKey = table2[pos2];
                table2[pos2] = currentKey;
                currentKey = kickedKey;
                tableID = 1; // The kicked key must now find a home in Table 1
            }
        }

        // If we break out of the loop, we hit a cycle.
        // In Phase 2, this is where we will add to the STASH instead of failing!
        //System.err.println("Max loop reached! Cycle detected. Rehash needed for key: " + currentKey);
        return false; 
    }

    // A quick debug method to see table states
    public void printTables() {
        System.out.println("--- Table 1 ---");
        for (int i = 0; i < size; i++) {
            if (table1[i] != EMPTY) System.out.println("Index " + i + ": " + table1[i]);
        }
        System.out.println("--- Table 2 ---");
        for (int i = 0; i < size; i++) {
            if (table2[i] != EMPTY) System.out.println("Index " + i + ": " + table2[i]);
        }
    }
}