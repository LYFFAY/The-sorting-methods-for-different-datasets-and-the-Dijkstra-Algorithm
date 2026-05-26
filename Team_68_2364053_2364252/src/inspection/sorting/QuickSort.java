package inspection.sorting;

import inspection.model.Location;

/**
 * Quick Sort implementation using the median-of-three pivot strategy.
 *
 * Algorithm:
 *   1. Choose a pivot using the median of the first, middle, and last elements.
 *   2. Partition the sub-array into elements <= pivot and elements > pivot.
 *   3. Recursively sort both partitions.
 *
 * The median-of-three strategy reduces the probability of the worst case
 * (already-sorted input) compared to naively picking the first element.
 *
 * Time complexity:
 *   - Best / Average case: O(n log n)
 *   - Worst case: O(n^2)  (degenerate partitioning; mitigated by median-of-three)
 *
 * Space complexity: O(log n) average (call stack depth).
 */
public class QuickSort extends SortingAlgorithm {

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    protected void sortArray(Location[] data) {
        quickSort(data, 0, data.length - 1);
    }

    private void quickSort(Location[] data, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(data, low, high);
            quickSort(data, low, pivotIndex - 1);
            quickSort(data, pivotIndex + 1, high);
        }
    }

    /**
     * Partitions the sub-array around the median-of-three pivot.
     * After partitioning, the pivot is in its final sorted position.
     *
     * @return the final index of the pivot element
     */
    private int partition(Location[] data, int low, int high) {
        int mid = low + (high - low) / 2;
        // Place median-of-three pivot at position high - 1 (classic approach)
        medianOfThree(data, low, mid, high);
        // Pivot is now data[mid]; swap it to high for in-place partitioning
        swap(data, mid, high);
        Location pivot = data[high];

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (data[j].compareTo(pivot) <= 0) {
                i++;
                swap(data, i, j);
            }
        }
        swap(data, i + 1, high);
        return i + 1;
    }

    /**
     * Rearranges data[a], data[b], data[c] so that data[b] holds the median.
     */
    private void medianOfThree(Location[] data, int a, int b, int c) {
        if (data[a].compareTo(data[b]) > 0) swap(data, a, b);
        if (data[a].compareTo(data[c]) > 0) swap(data, a, c);
        if (data[b].compareTo(data[c]) > 0) swap(data, b, c);
    }

    private void swap(Location[] data, int i, int j) {
        Location temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
}
