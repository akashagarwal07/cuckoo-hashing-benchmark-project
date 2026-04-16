public class LinearProbingHashTable {
    private int[] table;
    private boolean[] isOccupied;
    private boolean[] isDeleted; // Acts as our "Tombstone"
    private int capacity;
    
    private static final int PRIME = 1000000007;
    private int a, b;

    public LinearProbingHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new int[capacity];
        this.isOccupied = new boolean[capacity];
        this.isDeleted = new boolean[capacity];
        
        // Use a simple universal hash function for fair comparison
        java.util.Random rand = new java.util.Random();
        this.a = rand.nextInt(PRIME - 1) + 1;
        this.b = rand.nextInt(PRIME);
    }

    private int hash(int key) {
        long h = ((long) a * Math.abs(key) + b) % PRIME;
        return (int) (h % capacity);
    }

    public boolean insert(int key) {
        int index = hash(key);
        int startIndex = index;

        do {
            // If the slot is completely empty OR was previously deleted, we can insert
            if (!isOccupied[index] || isDeleted[index]) {
                table[index] = key;
                isOccupied[index] = true;
                isDeleted[index] = false; // Remove tombstone
                return true;
            }
            // If the key already exists, do nothing
            if (table[index] == key) {
                return true;
            }
            
            // Probe sequentially
            index = (index + 1) % capacity;
        } while (index != startIndex);

        // Table is completely full
        System.out.println("Linear Probing failed: Table is full.");
        return false;
    }

    public boolean lookup(int key) {
        int index = hash(key);
        int startIndex = index;

        do {
            if (!isOccupied[index]) {
                return false; // Hit an empty slot, the chain ends
            }
            if (!isDeleted[index] && table[index] == key) {
                return true; // Found the key
            }
            index = (index + 1) % capacity;
        } while (index != startIndex);

        return false;
    }

    public void delete(int key) {
        int index = hash(key);
        int startIndex = index;

        do {
            if (!isOccupied[index]) {
                return; // Key doesn't exist
            }
            if (!isDeleted[index] && table[index] == key) {
                isDeleted[index] = true; // Mark as tombstone
                return;
            }
            index = (index + 1) % capacity;
        } while (index != startIndex);
    }
}