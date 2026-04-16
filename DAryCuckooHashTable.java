import java.util.Random;

public class DAryCuckooHashTable {
    private int[] table1, table2, table3;
    private int size;
    private int a1, b1, a2, b2, a3, b3;
    private static final int PRIME = 1000000007;
    private static final int EMPTY = Integer.MIN_VALUE;
    private static final int MAX_LOOP = 100;
    private Random rand = new Random();

    public DAryCuckooHashTable(int capacity) {
        this.size = capacity;
        this.table1 = new int[size];
        this.table2 = new int[size];
        this.table3 = new int[size];
        
        for (int i = 0; i < size; i++) {
            table1[i] = EMPTY; table2[i] = EMPTY; table3[i] = EMPTY;
        }
        
        a1 = rand.nextInt(PRIME - 1) + 1; b1 = rand.nextInt(PRIME);
        a2 = rand.nextInt(PRIME - 1) + 1; b2 = rand.nextInt(PRIME);
        a3 = rand.nextInt(PRIME - 1) + 1; b3 = rand.nextInt(PRIME);
    }

    private int hash1(int key) { return (int) ((((long) a1 * Math.abs(key) + b1) % PRIME) % size); }
    private int hash2(int key) { return (int) ((((long) a2 * Math.abs(key) + b2) % PRIME) % size); }
    private int hash3(int key) { return (int) ((((long) a3 * Math.abs(key) + b3) % PRIME) % size); }

    public boolean lookup(int key) {
        if (key == EMPTY) return false;
        return table1[hash1(key)] == key || table2[hash2(key)] == key || table3[hash3(key)] == key;
    }

    public void delete(int key) {
        if (table1[hash1(key)] == key) table1[hash1(key)] = EMPTY;
        else if (table2[hash2(key)] == key) table2[hash2(key)] = EMPTY;
        else if (table3[hash3(key)] == key) table3[hash3(key)] = EMPTY;
    }

    public boolean insert(int key) {
        if (key == EMPTY) return false;
        if (lookup(key)) return true;

        int currentKey = key;
        for (int i = 0; i < MAX_LOOP; i++) {
            int pos1 = hash1(currentKey);
            int pos2 = hash2(currentKey);
            int pos3 = hash3(currentKey);

            // 1. Try to place in any empty leftmost location first
            if (table1[pos1] == EMPTY) { table1[pos1] = currentKey; return true; }
            if (table2[pos2] == EMPTY) { table2[pos2] = currentKey; return true; }
            if (table3[pos3] == EMPTY) { table3[pos3] = currentKey; return true; }

            // 2. All full. Pick a random table to evict from.
            int tableToEvict = rand.nextInt(3);
            int kickedKey;
            
            if (tableToEvict == 0) {
                kickedKey = table1[pos1]; table1[pos1] = currentKey;
            } else if (tableToEvict == 1) {
                kickedKey = table2[pos2]; table2[pos2] = currentKey;
            } else {
                kickedKey = table3[pos3]; table3[pos3] = currentKey;
            }
            currentKey = kickedKey;
        }
        return false; // Cycle detected
    }
}