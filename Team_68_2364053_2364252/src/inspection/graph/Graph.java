package inspection.graph;

import inspection.model.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Undirected weighted graph represented using an adjacency list.
 *
 * Each node is identified by its location_id string.  For every edge
 * (u, v, w) loaded from paths.csv, two directed entries are stored:
 *   adjacency[u] -> (v, w)
 *   adjacency[v] -> (u, w)
 * This makes the graph effectively undirected for traversal purposes.
 *
 * Data structure choice:
 *   A HashMap<String, List<int[]>> adjacency list was chosen because:
 *   - O(1) average-case node lookup by location_id.
 *   - Memory proportional to the number of edges (sparse graph friendly).
 *   - Efficient iteration over neighbours during Dijkstra's relaxation step.
 */
public class Graph {

    /** adjacency.get(node) returns a list of {neighbourIndex, weight} pairs */
    private final Map<String, List<int[]>> adjacency;

    /** Ordered list of all unique node IDs (used for index mapping) */
    private final List<String> nodeList;

    /** Maps node ID -> index in nodeList */
    private final Map<String, Integer> nodeIndex;

    public Graph() {
        adjacency = new HashMap<>();
        nodeList = new ArrayList<>();
        nodeIndex = new HashMap<>();
    }

    /**
     * Adds a node if it does not already exist.
     */
    public void addNode(String nodeId) {
        if (!nodeIndex.containsKey(nodeId)) {
            nodeIndex.put(nodeId, nodeList.size());
            nodeList.add(nodeId);
            adjacency.put(nodeId, new ArrayList<>());
        }
    }

    /**
     * Adds an undirected weighted edge between fromNode and toNode.
     * Both nodes are created automatically if they do not exist.
     *
     * @param fromNode  source node ID
     * @param toNode    destination node ID
     * @param weight    non-negative edge weight
     */
    public void addEdge(String fromNode, String toNode, int weight) {
        addNode(fromNode);
        addNode(toNode);
        int toIdx = nodeIndex.get(toNode);
        int fromIdx = nodeIndex.get(fromNode);
        adjacency.get(fromNode).add(new int[]{toIdx, weight});
        adjacency.get(toNode).add(new int[]{fromIdx, weight});
    }

    /**
     * Populates the graph from a list of Edge objects read from paths.csv.
     *
     * @param edges list of directed edge descriptions (treated as undirected)
     */
    public void buildFromEdges(List<Edge> edges) {
        for (Edge e : edges) {
            addEdge(e.getFrom(), e.getTo(), e.getWeight());
        }
    }

    /**
     * Returns the adjacency list for a given node.
     * Each entry is an int[] of length 2: {neighbourIndex, weight}.
     */
    public List<int[]> getNeighbours(String nodeId) {
        return adjacency.getOrDefault(nodeId, new ArrayList<>());
    }

    /** Returns the node ID at the given index. */
    public String getNodeId(int index) {
        return nodeList.get(index);
    }

    /** Returns the internal index of a node ID, or -1 if not found. */
    public int getNodeIndex(String nodeId) {
        return nodeIndex.getOrDefault(nodeId, -1);
    }

    /** Returns the total number of nodes in the graph. */
    public int getNodeCount() {
        return nodeList.size();
    }

    /** Returns true if the given node ID exists in the graph. */
    public boolean containsNode(String nodeId) {
        return nodeIndex.containsKey(nodeId);
    }
}
