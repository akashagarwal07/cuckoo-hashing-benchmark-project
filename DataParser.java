import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class DataParser {
    
    /**
     * @param filePath The path to the dataset file
     * @param datasetType "NASA", "KDD", or "CAIDA"
     */
    public static int[] parseLogFile(String filePath, String datasetType) {
        HashSet<Integer> uniqueKeys = new HashSet<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore empty lines or comments
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String primaryElement = "";
                
                // --- DATASET SPECIFIC PARSING LOGIC ---
                if (datasetType.equalsIgnoreCase("NASA")) {
                    // NASA: Space-separated, Column 0 is the Host/IP
                    String[] tokens = line.split("\\s+");
                    if (tokens.length > 0) primaryElement = tokens[0];
                    
                } else if (datasetType.equalsIgnoreCase("KDD") || datasetType.equalsIgnoreCase("INTERNET")) {
                    // KDD Cup: Comma-separated. Hash the whole line to 
                    // deduplicate exact matching network connections.
                    primaryElement = line.trim();
                    
                }
                
                // --- HASHING ---
                if (!primaryElement.isEmpty()) {
                    int hashKey = Math.abs(primaryElement.hashCode());
                    // Prevent our EMPTY marker (-2147483648) from being used
                    if (hashKey == Integer.MIN_VALUE) hashKey = 0; 
                    uniqueKeys.add(hashKey);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the dataset: " + e.getMessage());
        }
        
        // Convert HashSet to primitive array for the benchmark runners
        int[] keysArray = new int[uniqueKeys.size()];
        int i = 0;
        for (Integer key : uniqueKeys) {
            keysArray[i++] = key;
        }
        
        System.out.println("Successfully extracted " + keysArray.length + " UNIQUE keys from " + filePath);
        return keysArray;
    }
}