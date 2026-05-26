package inspection.model;

/**
 * Represents a candidate inspection location with a unique ID and priority score.
 * Implements Comparable to support natural ordering by priority (descending),
 * with location_id as tie-breaker (ascending).
 */
public class Location implements Comparable<Location> {

    private final String locationId;
    private final int priorityScore;

    public Location(String locationId, int priorityScore) {
        this.locationId = locationId;
        this.priorityScore = priorityScore;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    /**
     * Natural ordering: descending priority_score; ascending location_id on tie.
     */
    @Override
    public int compareTo(Location other) {
        if (this.priorityScore != other.priorityScore) {
            return Integer.compare(other.priorityScore, this.priorityScore);
        }
        return this.locationId.compareTo(other.locationId);
    }

    @Override
    public String toString() {
        return locationId + " (score=" + priorityScore + ")";
    }
}
