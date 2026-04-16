import java.util.LinkedList;

public class SeparateChainingHashTable {
    private LinkedList<Integer>[] table;
    private int capacity;
    
    private static final int PRIME = 1000000007;
    private int a, b;

    @SuppressWarnings("unchecked")
    public SeparateChainingHashTable(int capacity) {
        this.capacity = capacity;
        
        // Java doesn't allow generic array creation directly, so we cast
        this.table = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        
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
        LinkedList<Integer> chain = table[index];
        
        // Avoid duplicates
        if (chain.contains(key)) {
            return true;
        }
        
        chain.add(key);
        return true; // Chaining practically never fails unless out of memory
    }

    public boolean lookup(int key) {
        int index = hash(key);
        LinkedList<Integer> chain = table[index];
        
        return chain.contains(key);
    }

    public void delete(int key) {
        int index = hash(key);
        LinkedList<Integer> chain = table[index];
        
        // Note: use Integer.valueOf() so it removes the object, not the index
        chain.remove(Integer.valueOf(key)); 
    }
}