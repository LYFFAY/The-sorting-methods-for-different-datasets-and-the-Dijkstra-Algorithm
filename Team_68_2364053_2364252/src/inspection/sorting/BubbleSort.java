package inspection.sorting;

import inspection.model.Location;

/**
 * Bubble Sort implementation.
 *
 * Algorithm:
 *   Repeatedly compare adjacent elements and swap them when they are in the
 *   wrong order.  Each full pass "bubbles" the smallest remaining element to
 *   its final position.  An early-exit flag stops the algorithm as soon as a
 *   pass produces no swaps, giving O(n) best-case performance on already-sorted
 *   data.
 *
 * Time complexity:
 *   - Best  case: O(n)  (data already sorted – one pass, no swaps)
 *   - Average: O(n^2)
 *   - Worst  case: O(n^2)  (data in reverse order)
 *
 * Space complexity: O(1) – in-place.
 */
public class BubbleSort extends SortingAlgorithm {

    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    protected void sortArray(Location[] data) {
        int n = data.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (data[j].compareTo(data[j + 1]) > 0) {
                    Location temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
