package inspection.io;

import inspection.model.Edge;
import inspection.model.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading CSV datasets into model objects.
 * Supports reading candidate location files and the graph edge file.
 */
public class CSVReader {

    /**
     * Reads a candidate dataset CSV file and returns a list of Location objects.
     * Expected CSV format (with header): location_id,priority_score
     *
     * @param filePath path to the CSV file
     * @return list of Location objects parsed from the file
     * @throws IOException if the file cannot be read
     */
    public static List<Location> readLocations(String filePath) throws IOException {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }
                String id = parts[0].trim();
                int score = Integer.parseInt(parts[1].trim());
                locations.add(new Location(id, score));
            }
        }
        return locations;
    }

    /**
     * Reads the paths CSV file and returns a list of Edge objects.
     * Expected CSV format (with header): from_location,to_location,weight
     * Each row represents an undirected edge (stored once; the graph handles both directions).
     *
     * @param filePath path to the paths CSV file
     * @return list of Edge objects parsed from the file
     * @throws IOException if the file cannot be read
     */
    public static List<Edge> readEdges(String filePath) throws IOException {
        List<Edge> edges = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue;
                }
                String from = parts[0].trim();
                String to = parts[1].trim();
                int weight = Integer.parseInt(parts[2].trim());
                edges.add(new Edge(from, to, weight));
            }
        }
        return edges;
    }
}
