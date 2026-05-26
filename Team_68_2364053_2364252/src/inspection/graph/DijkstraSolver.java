package inspection.graph;

import inspection.model.PathResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Dijkstra's shortest-path algorithm on a non-negative weighted graph.
 *
 * Why Dijkstra?
 *   - All edge weights in paths.csv are positive integers.
 *   - Dijkstra's algorithm guarantees the globally optimal shortest path
 *     between any two nodes in a non-negatively weighted graph.
 *   - With a binary-heap priority queue the time complexity is
 *     O((V + E) log V), which is well-suited to the 1000-node graph here.
 *
 * Space complexity: O(V + E) for the dist[] and prev[] arrays plus the
 *   priority queue.
 *
 * Constrained queries (via waypoints) are solved by chaining multiple
 * independent Dijkstra runs and concatenating the resulting sub-paths.
 */
public class DijkstraSolver {

    private final Graph graph;

    public DijkstraSolver(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the shortest path between two nodes.
     *
     * @param startId   source node ID
     * @param endId     destination node ID
     * @return PathResult containing the path and total cost, or an empty path
     *         if the destination is unreachable
     */
    public PathResult shortestPath(String startId, String endId) {
        int n = graph.getNodeCount();
        int startIdx = graph.getNodeIndex(startId);
        int endIdx = graph.getNodeIndex(endId);

        if (startIdx == -1 || endIdx == -1) {
            return new PathResult(startId, endId, new ArrayList<>(), Integer.MAX_VALUE);
        }

        // Handle trivial self-path
        if (startIdx == endIdx) {
            List<String> self = new ArrayList<>();
            self.add(startId);
            return new PathResult(startId, endId, self, 0);
        }

        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[startIdx] = 0;

        // Priority queue entry: {distance, nodeIndex}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, startIdx});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentDist = current[0];
            int currentIdx = current[1];

            if (currentDist > dist[currentIdx]) {
                continue; // stale entry
            }
            if (currentIdx == endIdx) {
                break; // target reached
            }

            String currentId = graph.getNodeId(currentIdx);
            for (int[] neighbour : graph.getNeighbours(currentId)) {
                int neighbourIdx = neighbour[0];
                int edgeWeight = neighbour[1];
                int newDist = dist[currentIdx] + edgeWeight;
                if (newDist < dist[neighbourIdx]) {
                    dist[neighbourIdx] = newDist;
                    prev[neighbourIdx] = currentIdx;
                    pq.offer(new int[]{newDist, neighbourIdx});
                }
            }
        }

        if (dist[endIdx] == Integer.MAX_VALUE) {
            return new PathResult(startId, endId, new ArrayList<>(), Integer.MAX_VALUE);
        }

        List<String> path = reconstructPath(prev, startIdx, endIdx);
        return new PathResult(startId, endId, path, dist[endIdx]);
    }

    /**
     * Finds the shortest path from start to end passing through one waypoint
     * (the path must visit waypoint before end).
     * Solved as two consecutive Dijkstra calls: start->waypoint, waypoint->end.
     *
     * @param startId    source node ID
     * @param waypointId intermediate mandatory node
     * @param endId      destination node ID
     * @return concatenated PathResult, or an empty result if any segment is unreachable
     */
    public PathResult shortestPathViaWaypoint(String startId, String waypointId, String endId) {
        PathResult seg1 = shortestPath(startId, waypointId);
        PathResult seg2 = shortestPath(waypointId, endId);

        if (!seg1.isReachable() || !seg2.isReachable()) {
            return new PathResult(startId, endId, new ArrayList<>(), Integer.MAX_VALUE);
        }

        List<String> fullPath = new ArrayList<>(seg1.getPath());
        // Avoid duplicating the waypoint node at the junction
        List<String> seg2Path = seg2.getPath();
        for (int i = 1; i < seg2Path.size(); i++) {
            fullPath.add(seg2Path.get(i));
        }

        int totalCost = seg1.getTotalCost() + seg2.getTotalCost();
        return new PathResult(startId, endId, fullPath, totalCost);
    }

    /**
     * Finds the shortest path from start to end passing through two waypoints
     * in order: start -> wp1 -> wp2 -> end.
     * Solved as three consecutive Dijkstra calls.
     *
     * @param startId source node ID
     * @param wp1Id   first mandatory waypoint
     * @param wp2Id   second mandatory waypoint
     * @param endId   destination node ID
     * @return concatenated PathResult, or an empty result if any segment is unreachable
     */
    public PathResult shortestPathViaTwoWaypoints(String startId, String wp1Id,
                                                   String wp2Id, String endId) {
        PathResult seg1 = shortestPath(startId, wp1Id);
        PathResult seg2 = shortestPath(wp1Id, wp2Id);
        PathResult seg3 = shortestPath(wp2Id, endId);

        if (!seg1.isReachable() || !seg2.isReachable() || !seg3.isReachable()) {
            return new PathResult(startId, endId, new ArrayList<>(), Integer.MAX_VALUE);
        }

        List<String> fullPath = new ArrayList<>(seg1.getPath());
        appendWithoutFirstNode(fullPath, seg2.getPath());
        appendWithoutFirstNode(fullPath, seg3.getPath());

        int totalCost = seg1.getTotalCost() + seg2.getTotalCost() + seg3.getTotalCost();
        return new PathResult(startId, endId, fullPath, totalCost);
    }

    /** Appends all nodes except the first from source into target. */
    private void appendWithoutFirstNode(List<String> target, List<String> source) {
        for (int i = 1; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    /**
     * Reconstructs the path from start to end using the prev[] array
     * produced by Dijkstra's algorithm.
     */
    private List<String> reconstructPath(int[] prev, int startIdx, int endIdx) {
        List<String> path = new ArrayList<>();
        int current = endIdx;
        while (current != -1) {
            path.add(graph.getNodeId(current));
            if (current == startIdx) break;
            current = prev[current];
        }
        Collections.reverse(path);
        return path;
    }
}
