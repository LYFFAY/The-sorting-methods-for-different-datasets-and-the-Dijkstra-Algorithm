package inspection.model;

/**
 * Holds the result of a shortest-path query: start node, end node,
 * the ordered sequence of nodes in the path, and the total cost.
 */
public class PathResult {

    private final String startNode;
    private final String endNode;
    private final java.util.List<String> path;
    private final int totalCost;

    public PathResult(String startNode, String endNode, java.util.List<String> path, int totalCost) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.path = path;
        this.totalCost = totalCost;
    }

    public String getStartNode() {
        return startNode;
    }

    public String getEndNode() {
        return endNode;
    }

    public java.util.List<String> getPath() {
        return path;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public boolean isReachable() {
        return !path.isEmpty();
    }

    @Override
    public String toString() {
        if (!isReachable()) {
            return "No path found from " + startNode + " to " + endNode;
        }
        return String.join(" -> ", path) + "  (cost = " + totalCost + ")";
    }
}
