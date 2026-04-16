import java.util.Random;

public class CuckooWithStash {
    private int[] table1;
    private int[] table2;
    private int size;
    
    // The Stash Architecture
    private int[] stash;
    private int stashCount;
    private static final int STASH_SIZE = 4;
    
    private static final int PRIME = 1000000007;
    private int a1, b1, a2, b2;
    
    private static final int EMPTY = Integer.MIN_VALUE;
    private static final int MAX_LOOP = 100; 

    public CuckooWithStash(int capacity) {
        this.size = capacity;
        this.table1 = new int[size];
        this.table2 = new int[size];
        this.stash = new int[STASH_SIZE];
        this.stashCount = 0;
        
        for (int i = 0; i < size; i++) {
            table1[i] = EMPTY;
            table2[i] = EMPTY;
        }
        for (int i = 0; i < STASH_SIZE; i++) {
            stash[i] = EMPTY;
        }
        
        generateNewHashFunctions();
    }

    private void generateNewHashFunctions() {
        Random rand = new Random();
        a1 = rand.nextInt(PRIME - 1) + 1; 
        b1 = rand.nextInt(PRIME);
        a2 = rand.nextInt(PRIME - 1) + 1;
        b2 = rand.nextInt(PRIME);
    }

    private int hash1(int key) {
        long hash = ((long) a1 * Math.abs(key) + b1) % PRIME;
        return (int) (hash % size);
    }

    private int hash2(int key) {
        long hash = ((long) a2 * Math.abs(key) + b2) % PRIME;
        return (int) (hash % size);
    }

    public boolean lookup(int key) {
        if (key == EMPTY) return false;
        
        // 1. Check main tables
        if (table1[hash1(key)] == key) return true;
        if (table2[hash2(key)] == key) return true;
        
        // 2. Check the stash
        for (int i = 0; i < STASH_SIZE; i++) {
            if (stash[i] == key) return true;
        }
        return false;
    }

    public void delete(int key) {
        if (key == EMPTY) return;
        
        // 1. Check and delete from main tables
        int pos1 = hash1(key);
        if (table1[pos1] == key) {
            table1[pos1] = EMPTY;
            return;
        }
        
        int pos2 = hash2(key);
        if (table2[pos2] == key) {
            table2[pos2] = EMPTY;
            return;
        }
        
        // 2. Check and delete from the stash
        for (int i = 0; i < STASH_SIZE; i++) {
            if (stash[i] == key) {
                stash[i] = EMPTY;
                stashCount--;
                return;
            }
        }
    }

    public boolean insert(int key) {
        if (key == EMPTY) throw new IllegalArgumentException("Cannot insert EMPTY marker.");
        if (lookup(key)) return true;

        int currentKey = key;
        int tableID = 1; 

        for (int i = 0; i < MAX_LOOP; i++) {
            if (tableID == 1) {
                int pos1 = hash1(currentKey);
                if (table1[pos1] == EMPTY) {
                    table1[pos1] = currentKey;
                    return true;
                }
                int kickedKey = table1[pos1];
                table1[pos1] = currentKey;
                currentKey = kickedKey;
                tableID = 2; 
            } else {
                int pos2 = hash2(currentKey);
                if (table2[pos2] == EMPTY) {
                    table2[pos2] = currentKey;
                    return true;
                }
                int kickedKey = table2[pos2];
                table2[pos2] = currentKey;
                currentKey = kickedKey;
                tableID = 1; 
            }
        }

        // Cycle Detected! Attempt to use the Stash instead of failing.
        if (stashCount < STASH_SIZE) {
            for (int i = 0; i < STASH_SIZE; i++) {
                if (stash[i] == EMPTY) {
                    stash[i] = currentKey;
                    stashCount++;
                    System.err.println("Cycle averted. Key " + currentKey + " secured in Stash.");
                    return true;
                }
            }
        }

        // Only fail if the Stash is completely full.
        System.err.println("Fatal: Stash is full! Rehash required for key: " + currentKey);
        return false; 
    }
}