package inspection.sorting;

import inspection.model.Location;

/**
 * Abstract base class for all sorting algorithms used in the inspection system.
 * Defines the common interface and timing infrastructure.
 * Subclasses implement the sort logic via the protected sortArray() method.
 *
 * Sorting rule (enforced by Location.compareTo):
 *   - primary key:   priority_score descending
 *   - secondary key: location_id ascending (tie-breaker)
 */
public abstract class SortingAlgorithm {

    private static final int TIMING_RUNS = 3;

    /**
     * Returns a human-readable name for this algorithm (e.g., "Bubble Sort").
     */
    public abstract String getName();

    /**
     * Performs the in-place sort on the given array.
     * Implementations must sort in descending priority order
     * using Location.compareTo() for comparisons.
     *
     * @param data array to sort (sorted in place)
     */
    protected abstract void sortArray(Location[] data);

    /**
     * Sorts a copy of the input list and returns the sorted array.
     * The original list is NOT modified.
     *
     * @param locations source list
     * @return sorted copy as an array
     */
    public Location[] sort(java.util.List<Location> locations) {
        Location[] arr = locations.toArray(new Location[0]);
        sortArray(arr);
        return arr;
    }

    /**
     * Runs the sort algorithm TIMING_RUNS times on fresh copies of the data
     * and returns the average elapsed time in nanoseconds.
     *
     * @param locations source list (not modified)
     * @return average sort time in nanoseconds
     */
    public long measureAverageTimeNs(java.util.List<Location> locations) {
        long total = 0;
        for (int i = 0; i < TIMING_RUNS; i++) {
            Location[] copy = locations.toArray(new Location[0]);
            long start = System.nanoTime();
            sortArray(copy);
            long end = System.nanoTime();
            total += (end - start);
        }
        return total / TIMING_RUNS;
    }
}
