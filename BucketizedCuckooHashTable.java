import java.util.Random;

public class BucketizedCuckooHashTable {
    private int[][] table1, table2;
    private int size;
    private static final int BUCKET_SIZE = 4;
    private int a1, b1, a2, b2;
    private static final int PRIME = 1000000007;
    private static final int EMPTY = Integer.MIN_VALUE;
    private static final int MAX_LOOP = 100;
    private Random rand = new Random();

    public BucketizedCuckooHashTable(int capacity) {
        // Divide capacity by bucket size to keep overall memory usage fair
        this.size = capacity / BUCKET_SIZE; 
        this.table1 = new int[size][BUCKET_SIZE];
        this.table2 = new int[size][BUCKET_SIZE];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < BUCKET_SIZE; j++) {
                table1[i][j] = EMPTY; table2[i][j] = EMPTY;
            }
        }
        a1 = rand.nextInt(PRIME - 1) + 1; b1 = rand.nextInt(PRIME);
        a2 = rand.nextInt(PRIME - 1) + 1; b2 = rand.nextInt(PRIME);
    }

    private int hash1(int key) { return (int) ((((long) a1 * Math.abs(key) + b1) % PRIME) % size); }
    private int hash2(int key) { return (int) ((((long) a2 * Math.abs(key) + b2) % PRIME) % size); }

    public boolean lookup(int key) {
        if (key == EMPTY) return false;
        int pos1 = hash1(key), pos2 = hash2(key);
        for (int i = 0; i < BUCKET_SIZE; i++) {
            if (table1[pos1][i] == key || table2[pos2][i] == key) return true;
        }
        return false;
    }

    public void delete(int key) {
        int pos1 = hash1(key), pos2 = hash2(key);
        for (int i = 0; i < BUCKET_SIZE; i++) {
            if (table1[pos1][i] == key) { table1[pos1][i] = EMPTY; return; }
            if (table2[pos2][i] == key) { table2[pos2][i] = EMPTY; return; }
        }
    }

    public boolean insert(int key) {
        if (key == EMPTY) return false;
        if (lookup(key)) return true;

        int currentKey = key;
        for (int loop = 0; loop < MAX_LOOP; loop++) {
            int pos1 = hash1(currentKey);
            int pos2 = hash2(currentKey);

            // 1. Try to find any empty slot in either bucket
            for (int i = 0; i < BUCKET_SIZE; i++) {
                if (table1[pos1][i] == EMPTY) { table1[pos1][i] = currentKey; return true; }
                if (table2[pos2][i] == EMPTY) { table2[pos2][i] = currentKey; return true; }
            }

            // 2. Both buckets are full. Randomly pick a table and a slot to evict.
            int tableToEvict = rand.nextInt(2);
            int slotToEvict = rand.nextInt(BUCKET_SIZE);
            int kickedKey;

            if (tableToEvict == 0) {
                kickedKey = table1[pos1][slotToEvict];
                table1[pos1][slotToEvict] = currentKey;
            } else {
                kickedKey = table2[pos2][slotToEvict];
                table2[pos2][slotToEvict] = currentKey;
            }
            currentKey = kickedKey;
        }
        return false; // Cycle detected
    }
}