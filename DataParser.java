import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class DataParser {
    
    public static int[] parseLogFile(String filePath) {
        // Use a HashSet to automatically filter out duplicate IP addresses
        HashSet<Integer> uniqueKeys = new HashSet<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String[] tokens = line.split("\\s+");
                if (tokens.length > 0) {
                    int hashKey = Math.abs(tokens[0].hashCode());
                    if (hashKey == Integer.MIN_VALUE) hashKey = 0; 
                    
                    uniqueKeys.add(hashKey); // HashSet ignores duplicates
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the dataset: " + e.getMessage());
        }
        
        // Convert HashSet to primitive array
        int[] keysArray = new int[uniqueKeys.size()];
        int i = 0;
        for (Integer key : uniqueKeys) {
            keysArray[i++] = key;
        }
        
        System.out.println("Successfully extracted " + keysArray.length + " UNIQUE keys from " + filePath);
        return keysArray;
    }
}