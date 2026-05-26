package inspection.sorting;

import inspection.model.Location;

/**
 * Merge Sort implementation (top-down, recursive).
 *
 * Algorithm:
 *   1. Divide the array into two halves.
 *   2. Recursively sort each half.
 *   3. Merge the two sorted halves into a single sorted array using a
 *      temporary buffer.
 *
 * Time complexity:
 *   - Best / Average / Worst case: O(n log n) – guaranteed regardless of
 *     input order.
 *
 * Space complexity: O(n) – requires auxiliary array for merging.
 */
public class MergeSort extends SortingAlgorithm {

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    protected void sortArray(Location[] data) {
        if (data.length <= 1) {
            return;
        }
        Location[] temp = new Location[data.length];
        mergeSort(data, temp, 0, data.length - 1);
    }

    private void mergeSort(Location[] data, Location[] temp, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(data, temp, left, mid);
        mergeSort(data, temp, mid + 1, right);
        merge(data, temp, left, mid, right);
    }

    /**
     * Merges two sorted sub-arrays [left..mid] and [mid+1..right].
     * Uses the temporary buffer to avoid overwriting elements being compared.
     */
    private void merge(Location[] data, Location[] temp, int left, int mid, int right) {
        System.arraycopy(data, left, temp, left, right - left + 1);

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (temp[i].compareTo(temp[j]) <= 0) {
                data[k++] = temp[i++];
            } else {
                data[k++] = temp[j++];
            }
        }
        while (i <= mid) {
            data[k++] = temp[i++];
        }
        while (j <= right) {
            data[k++] = temp[j++];
        }
    }
}
