package io.github.slimeistdev.crystalline_sky.util;

public class ArrayUtils {
	/**
	 * Binary search for the first index i in the sorted array where array[i] < value and array[i + 1] > value.
	 * @param array must be sorted in ascending order
	 * @param value the value to find a place for
	 * @return the index to insert at, or -1 if no such index exists
	 */
	public static int binarySearchSortedInsertionPoint(int[] array, int value) {
		return binarySearchSortedInsertionPoint(array, value, array.length);
	}

	/**
	 * Binary search for the first index i in the sorted array where array[i] < value and array[i + 1] > value.
	 * @param array must be sorted in ascending order
	 * @param value the value to find a place for
	 * @param length limits the search to indexes [0, length)
	 * @return the index to insert at, or -1 if no such index exists
	 */
	public static int binarySearchSortedInsertionPoint(int[] array, int value, int length) {
		if (length == 0) return 0;
		if (value < array[0]) return 0;
		if (value > array[length - 1]) return length;

		int left = 0;
		int right = length - 1;

		while (left < right) {
			int mid = (left + right) / 2;
			int midValue = array[mid];

			if (midValue == value)
				return -1;

			if (midValue < value) {
				left = mid + 1;
			} else { // midValue > value
				right = mid;
			}
		}

		if (array[left] == value) return -1;
		if (array[right] == value) return -1;
		return left;
	}
}
