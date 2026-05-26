package inspection;

import inspection.graph.DijkstraSolver;
import inspection.graph.Graph;
import inspection.io.CSVReader;
import inspection.model.Location;
import inspection.model.PathResult;
import inspection.sorting.BubbleSort;
import inspection.sorting.MergeSort;
import inspection.sorting.QuickSort;
import inspection.sorting.SortingAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point for the Urban Infrastructure Inspection System.
 *
 * Workflow:
 *   Task A - Load three candidate datasets, sort each with Bubble Sort,
 *             Quick Sort, and Merge Sort, measure runtimes, and select
 *             the top 10 highest-priority locations from each dataset.
 *
 *   Task B - Build the weighted graph from paths.csv, then solve the four
 *             required shortest-path query cases using Dijkstra's algorithm.
 *
 * Data files are expected in the "Group Project Datasets" folder relative
 * to the working directory when the program is launched.
 */
public class Main {

    private static final String DATA_DIR = "Group Project Datasets/";
    private static final String FILE_A   = DATA_DIR + "candidates_A.csv";
    private static final String FILE_B   = DATA_DIR + "candidates_B.csv";
    private static final String FILE_C   = DATA_DIR + "candidates_C.csv";
    private static final String FILE_G   = DATA_DIR + "paths.csv";

    private static final int TOP_N = 10;

    /** Shared string builder – all output is accumulated then flushed once. */
    private static final StringBuilder sb = new StringBuilder();

    public static void main(String[] args) {

        separator('=', 60);
        print("  Urban Infrastructure Inspection System");
        separator('=', 60);
        println();

        // ------------------------------------------------------------------ //
        //  TASK A - Sorting Algorithm Evaluation                              //
        // ------------------------------------------------------------------ //
        separator('=', 60);
        print("  TASK A - SORTING ALGORITHMS");
        separator('=', 60);
        println();

        SortingAlgorithm[] algorithms = {
            new BubbleSort(),
            new QuickSort(),
            new MergeSort()
        };

        String[] datasetNames = {"Dataset A", "Dataset B", "Dataset C"};
        String[] datasetFiles = {FILE_A, FILE_B, FILE_C};
        Location[][] topSelections = new Location[3][];

        for (int d = 0; d < 3; d++) {
            separator('-', 60);
            print("  " + datasetNames[d] + "  (" + datasetFiles[d] + ")");
            separator('-', 60);

            List<Location> locations;
            try {
                locations = CSVReader.readLocations(datasetFiles[d]);
            } catch (IOException e) {
                print("ERROR reading " + datasetFiles[d] + ": " + e.getMessage());
                continue;
            }

            printf("  Records loaded: %d%n%n", locations.size());

            printf("  %-15s  %18s  %18s%n", "Algorithm", "Avg Time (ns)", "Avg Time (ms)");
            separator('-', 56);

            Location[] sortedResult = null;

            for (SortingAlgorithm alg : algorithms) {
                long avgNs = alg.measureAverageTimeNs(locations);
                double avgMs = avgNs / 1_000_000.0;
                printf("  %-15s  %,18d  %18.4f%n", alg.getName(), avgNs, avgMs);

                if (alg instanceof MergeSort) {
                    sortedResult = alg.sort(locations);
                }
            }

            if (sortedResult == null) {
                sortedResult = algorithms[0].sort(locations);
            }

            Location[] top10 = Arrays.copyOf(sortedResult, TOP_N);
            topSelections[d] = top10;

            println();
            print("  Top 10 Highest-Priority Locations:");
            for (int i = 0; i < top10.length; i++) {
                printf("    %2d. %-10s  (priority_score = %d)%n",
                        i + 1, top10[i].getLocationId(), top10[i].getPriorityScore());
            }
            println();
        }

        // Summary table
        separator('=', 60);
        print("  TASK A - SUMMARY: Top-10 Locations per Dataset");
        separator('=', 60);
        printf("%-12s  %s%n", "Dataset", "Top-10 Location IDs (Rank 1 to 10)");
        separator('-', 80);
        for (int d = 0; d < 3; d++) {
            if (topSelections[d] == null) continue;
            sb.append(String.format("%-12s  ", datasetNames[d]));
            for (int i = 0; i < topSelections[d].length; i++) {
                sb.append(topSelections[d][i].getLocationId());
                if (i < topSelections[d].length - 1) sb.append(", ");
            }
            sb.append(System.lineSeparator());
        }
        println();

        // ------------------------------------------------------------------ //
        //  TASK B - Graph Algorithm Evaluation (Dijkstra's Algorithm)        //
        // ------------------------------------------------------------------ //
        separator('=', 60);
        print("  TASK B - GRAPH SHORTEST-PATH QUERIES");
        separator('=', 60);
        println();

        Graph graph = new Graph();
        try {
            graph.buildFromEdges(CSVReader.readEdges(FILE_G));
            printf("  Graph built: %d nodes loaded from %s%n%n",
                    graph.getNodeCount(), FILE_G);
        } catch (IOException e) {
            print("ERROR reading graph file: " + e.getMessage());
            flushOutput();
            return;
        }

        DijkstraSolver solver = new DijkstraSolver(graph);

        if (topSelections[0] == null || topSelections[1] == null || topSelections[2] == null) {
            print("Cannot run Task B: Task A did not complete for all datasets.");
            flushOutput();
            return;
        }

        String a1  = topSelections[0][0].getLocationId();
        String a10 = topSelections[0][9].getLocationId();
        String b1  = topSelections[1][0].getLocationId();
        String b5  = topSelections[1][4].getLocationId();
        String c1  = topSelections[2][0].getLocationId();
        String c5  = topSelections[2][4].getLocationId();

        print("  Key nodes derived from Task A:");
        printf("    A-1  (1st of Dataset A)  = %s%n", a1);
        printf("    A-10 (10th of Dataset A) = %s%n", a10);
        printf("    B-1  (1st of Dataset B)  = %s%n", b1);
        printf("    B-5  (5th of Dataset B)  = %s%n", b5);
        printf("    C-1  (1st of Dataset C)  = %s%n", c1);
        printf("    C-5  (5th of Dataset C)  = %s%n", c5);
        println();

        // Case 1: A1 to itself
        separator('-', 60);
        print("  Case 1: from A-1 to A-1 (self-to-self)");
        separator('-', 60);
        PathResult case1 = solver.shortestPath(a1, a1);
        printCaseResult(a1, a1, null, null, case1);

        // Case 2: A1 to A10
        separator('-', 60);
        printf("  Case 2: from A-1 (%s) to A-10 (%s)%n", a1, a10);
        separator('-', 60);
        PathResult case2 = solver.shortestPath(a1, a10);
        printCaseResult(a1, a10, null, null, case2);

        // Case 3: A1 to B1 via B5
        separator('-', 60);
        printf("  Case 3: from A-1 (%s) to B-1 (%s) via B-5 (%s)%n", a1, b1, b5);
        separator('-', 60);
        PathResult case3 = solver.shortestPathViaWaypoint(a1, b5, b1);
        printCaseResult(a1, b1, b5, null, case3);

        // Case 4: A1 to C1 via B5 then C5
        separator('-', 60);
        printf("  Case 4: from A-1 (%s) to C-1 (%s)%n", a1, c1);
        printf("          via B-5 (%s) then C-5 (%s)%n", b5, c5);
        separator('-', 60);
        PathResult case4 = solver.shortestPathViaTwoWaypoints(a1, b5, c5, c1);
        printCaseResult(a1, c1, b5, c5, case4);

        separator('=', 60);
        print("  Program completed successfully.");
        separator('=', 60);

        flushOutput();
    }

    private static void printCaseResult(String start, String end,
                                        String wp1, String wp2,
                                        PathResult result) {
        printf("  Start Node   : %s%n", start);
        printf("  Destination  : %s%n", end);
        if (wp1 != null) printf("  Waypoint 1   : %s%n", wp1);
        if (wp2 != null) printf("  Waypoint 2   : %s%n", wp2);

        if (!result.isReachable()) {
            print("  Result       : NO PATH FOUND");
        } else {
            printf("  Total Cost   : %d%n", result.getTotalCost());
            printf("  Nodes in Path: %d%n", result.getPath().size());
            sb.append("  Path         : ");
            List<String> path = result.getPath();
            int chunk = 8;
            for (int i = 0; i < path.size(); i++) {
                if (i > 0 && i % chunk == 0) {
                    sb.append(System.lineSeparator()).append("                 ");
                }
                sb.append(path.get(i));
                if (i < path.size() - 1) sb.append(" -> ");
            }
            sb.append(System.lineSeparator());
        }
        println();
    }

    // ---------- output helpers (all write to sb) ----------

    private static void print(String s) {
        sb.append(s).append(System.lineSeparator());
    }

    private static void println() {
        sb.append(System.lineSeparator());
    }

    private static void printf(String fmt, Object... args) {
        sb.append(String.format(fmt, args));
    }

    private static void separator(char ch, int len) {
        for (int i = 0; i < len; i++) sb.append(ch);
        sb.append(System.lineSeparator());
    }

    private static void flushOutput() {
        System.out.print(sb.toString());
        System.out.flush();
    }
}
