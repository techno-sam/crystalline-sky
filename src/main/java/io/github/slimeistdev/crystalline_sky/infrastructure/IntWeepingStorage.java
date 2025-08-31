package io.github.slimeistdev.crystalline_sky.infrastructure;

import com.google.common.base.Preconditions;
import io.github.slimeistdev.crystalline_sky.util.ArrayUtils;
import it.unimi.dsi.fastutil.shorts.ShortList;

import java.util.*;
import java.util.function.Consumer;

public class IntWeepingStorage implements WeepingStorage {
	private final int[] sizes;

	// each long is (y_sky << 32 | y_bottom)
	// y_sky is the position of the highest weeping sky block in a layer
	// y_bottom is the lowest illuminated position (so 1 above a solid block) (less than y_sky) in a layer, or -1 if the layer is unbounded
	// must be sorted top-to-bottom by y_sky
	private final int[][] columns;

	private final int[] tempRunSplitSizes;
	private final int[][] tempRunSplits;

	// only used for iterators
	private final int minY;

	public IntWeepingStorage(int minY) {
		this.sizes = new int[16*16];
		this.columns = new int[16*16][0];

		this.tempRunSplitSizes = new int[16*16*2];
		this.tempRunSplits = new int[16*16*2][0];

		this.minY = minY;
	}

	private static int getIndex(int localX, int localZ) {
		return localX + localZ * 16;
	}

	private static int pack(short ySky, short yBottom) {
		return ((int)ySky << 16) | Short.toUnsignedInt(yBottom);
	}

	private static short unpackSky(int packed) {
		return (short)(packed >>> 16);
	}

	private static short unpackBottom(int packed) {
		return (short)(packed & 0xFFFF);
	}

	private static int calculateIncreasedLength(int currentLength) {
		if (currentLength == 0)
			return 1;

		// calculate next power of two
		return Integer.highestOneBit(currentLength) << 1;
	}

	private void maybeShrinkColumn(int index) {
		int[] column = columns[index];
		int size = sizes[index];

		int newLength = calculateIncreasedLength(size);
		if (column.length > newLength) {
			int[] newColumn = new int[newLength];
			System.arraycopy(column, 0, newColumn, 0, size);
			columns[index] = newColumn;
		}
	}

	private void insertValue(int columnIndex, int i, int packed) {
		int[] column = columns[columnIndex];
		int size = sizes[columnIndex];

		if (size + 1 > column.length) {
			int newLength = calculateIncreasedLength(column.length);
			int[] newColumn = new int[newLength];

			System.arraycopy(column, 0, newColumn, 0, i);
			newColumn[i] = packed;
			System.arraycopy(column, i, newColumn, i + 1, size - i);

			columns[columnIndex] = newColumn;
		} else {
			System.arraycopy(column, i, column, i + 1, size - i);
			column[i] = packed;
		}
		sizes[columnIndex] = ++size;
	}

	/*
	unlit sections are iterated downward
	lit sections are iterated upward

	a newly-unlit section should call insertTempRunSplit at its topY
	a newly-lit section should call insertTempRunSplit at its bottomY
	 */

	/**
	 * Will split a run in subsequent iterators
	 * @param columnIndex column to split
	 * @param splitY the new (isLit ? bottomY : topY) of a run that intersects this one
	 * @param isLit whether this should be added to the splits for lit or unlit runs
	 */
	private void insertTempRunSplit(int columnIndex, int splitY, boolean isLit) {
		int index = columnIndex * 2 + (isLit ? 1 : 0);

		int size = tempRunSplitSizes[index];
		int[] splits = tempRunSplits[index];

		if (size == 0) {
			if (splits.length == 0) {
				tempRunSplits[index] = new int[] {splitY};
			} else {
				splits[0] = splitY;
			}
			tempRunSplitSizes[index] = ++size;
		} else {
			// binary search for insertion point
			int insertIndex = ArrayUtils.binarySearchSortedInsertionPoint(splits, splitY, size);
			if (insertIndex != -1) {
				if (size + 1 > splits.length) {
					int newLength = calculateIncreasedLength(splits.length);
					int[] newSplits = new int[newLength];

					System.arraycopy(splits, 0, newSplits, 0, insertIndex);
					newSplits[insertIndex] = splitY;
					System.arraycopy(splits, insertIndex, newSplits, insertIndex + 1, splits.length - insertIndex);

					tempRunSplits[index] = newSplits;
				} else {
					System.arraycopy(splits, insertIndex, splits, insertIndex + 1, size - insertIndex);
					splits[insertIndex] = splitY;
				}
				tempRunSplitSizes[index] = ++size;
			}
		}
	}

	private void litTempSplit(int columnIndex, int bottomY) {
		insertTempRunSplit(columnIndex, bottomY, true);
	}

	private void unlitTempSplit(int columnIndex, int topY) {
		insertTempRunSplit(columnIndex, topY, false);
	}

	@Override
	public void insertSky(int localX, int y, int localZ, WeepingScanner scanner) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];
		short yShort = (short) y;

		if (size == 0) {
			int solidY = scanner.scan(localX, y, localZ);
			short shortLowestLitY = (short) (solidY == -1 ? -1 : solidY + 1);
			int packed = pack(yShort, shortLowestLitY);
			if (column.length == 0) {
				columns[index] = new int[] {packed};
			} else {
				column[0] = packed;
			}
			sizes[index] = ++size;
			litTempSplit(index, shortLowestLitY);
		} else {
			// possible cases:
			// 1. we are in a layer, and do nothing
			// 2. we are below the lowest layer, and create a new layer
			// 3. we are above a layer, and must scan to determine whether to join it or insert one above it

			for (int i = 0; i < size; i++) {
				int packed = column[i];
				short sky = unpackSky(packed);
				short bottom = unpackBottom(packed);

				if (yShort == sky)
					return; // no change

				if (bottom != -1 && yShort < sky && (yShort == bottom || yShort == bottom - 1)) {
					// scan if layer can continue now, and extend it down if so. Else, fall through to in-or-below handling
					if (!scanner.faceBlocksLight(localX, bottom, localZ)) {
						int solidY = scanner.scan(localX, yShort, localZ);
						short shortLowestLitY = (short) (solidY == -1 ? -1 : solidY + 1);

						if (i + 1 < size) { // maybe join layer below
							int packedBelow = column[i + 1];
							short skyBelow = unpackSky(packedBelow);
							short bottomBelow = unpackBottom(packedBelow);

							if (solidY == -1 || solidY > skyBelow) { // join layer[i] and layer[i + 1]
								litTempSplit(index, skyBelow + 1); // the newly-lit region is [skyBelow + 1, bottom - 1]

								column[i] = pack(sky, bottomBelow);
								System.arraycopy(column, i + 2, column, i + 1, size - (i + 2));
								sizes[index] = --size;

								maybeShrinkColumn(index);
								return;
							}
						}

						// extend layer[i] down
						column[i] = pack(sky, shortLowestLitY);
						litTempSplit(index, shortLowestLitY);

						return;
					}
				}

				if (yShort > sky) { // above layer
					int solidY = scanner.scan(localX, y, localZ);
					if (solidY == -1 || solidY < sky) { // join layer
						column[i] = pack(yShort, bottom);
						litTempSplit(index, sky + 1); // the newly-lit region is [sky + 1, yShort]
					} else { // insert new layer above (at index i)
						insertValue(index, i, pack(yShort, (short) (solidY + 1)));
						litTempSplit(index, solidY + 1);
					}
					return;
				} else { // (yShort < sky) in or below
					if (bottom == -1 || yShort >= bottom) { // in layer, do nothing
						return;
					} else if (i == size - 1) { // below layer, add if last layer
						int solidY = scanner.scan(localX, y, localZ);
						short shortLowestLitY = (short) (solidY == -1 ? -1 : solidY + 1);

						insertValue(index, size, pack(yShort, shortLowestLitY));
						litTempSplit(index, shortLowestLitY);
						return;
					}
				}
			}
		}
	}

	@Override
	public void insertSolid(int localX, int y, int localZ, WeepingScanner scanner) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];
		short yShort = (short) y;

		// if we're inside a layer, limit it

		boolean lightBlockedHere;
		boolean lightBlockedAbove;
		int lastLitY = -1;
		boolean blockingCalculated = false;

		for (int i = 0; i < size; i++) {
			int packed = column[i];
			short sky = unpackSky(packed);
			short bottom = unpackBottom(packed);

			if (yShort > sky) { // we're above all remaining layers, so nothing to do
				return;
			} else if (yShort == sky) { // either remove layer or lower the start (scan for sky)
				int nextSky = scanner.scanForWeepingSky(localX, y, localZ);

				if (nextSky == -1 || nextSky < bottom) { // remove layer
					System.arraycopy(column, i + 1, column, i, size - (i + 1));
					sizes[index] = --size;
					maybeShrinkColumn(index);
				} else { // lower start of layer
					column[i] = pack((short) nextSky, bottom);
				}

				unlitTempSplit(index, sky);
				return;
			} else { // (yShort < sky) limit layer and potentially split layer
				if (!blockingCalculated) {
					lightBlockedHere = scanner.faceBlocksLight(localX, y, localZ);
					lightBlockedAbove = scanner.faceBlocksLight(localX, y + 1, localZ);

					if (lightBlockedAbove) {
						lastLitY = y + 1;
					} else if (lightBlockedHere) {
						lastLitY = y;
					} else { // no effect
						return;
					}

					blockingCalculated = true;
				}

				if (lastLitY <= bottom) // no effect
					continue;

				int nextSky = scanner.scanForWeepingSky(localX, y, localZ);

				// limit
				column[i] = pack(sky, (short) lastLitY);

				unlitTempSplit(index, lastLitY - 1);

				if (nextSky != -1 && nextSky >= bottom) { // split
					insertValue(index, i + 1, pack((short) nextSky, bottom));
				}
				return;
			}
		}
	}

	@Override
	public void insertAir(int localX, int y, int localZ, WeepingScanner scanner) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];
		short yShort = (short) y;

		for (int i = 0; i < size; i++) {
			int packed = column[i];
			short sky = unpackSky(packed);
			short bottom = unpackBottom(packed);

			if (yShort > sky) { // above all remaining layers, nothing to do
				return;
			} else if (yShort == sky) { // rescan for sky
				int nextSky = scanner.scanForWeepingSky(localX, y, localZ);

				if (nextSky == -1 || nextSky < bottom) { // remove layer
					System.arraycopy(column, i + 1, column, i, size - (i + 1));
					sizes[index] = --size;
					maybeShrinkColumn(index);

					unlitTempSplit(index, sky);
				} else { // lower start of layer
					column[i] = pack((short) nextSky, bottom);

					unlitTempSplit(index, sky);
				}
				return;
			} else { // (yShort < sky) maybe extend layer down
				if (bottom != -1 && (yShort == bottom || yShort == bottom - 1)) { // we may have freed things up
					if (!scanner.faceBlocksLight(localX, bottom, localZ)) { // extend layer down or join with layer below
						int solidY = scanner.scan(localX, yShort, localZ);
						short shortLowestLitY = (short) (solidY == -1 ? -1 : solidY + 1);

						if (i + 1 < size) { // maybe join layer below
							int packedBelow = column[i + 1];
							short skyBelow = unpackSky(packedBelow);
							short bottomBelow = unpackBottom(packedBelow);

							if (solidY == -1 || solidY < skyBelow) { // join layer[i] and layer[i + 1]
								column[i] = pack(sky, bottomBelow);
								System.arraycopy(column, i + 2, column, i + 1, size - (i + 2));
								sizes[index] = --size;

								litTempSplit(index, skyBelow + 1);

								maybeShrinkColumn(index);
								return;
							}
						}

						// extend layer[i] down
						column[i] = pack(sky, shortLowestLitY);
						litTempSplit(index, shortLowestLitY);

						return;
					}
				}
			}
		}
	}

	@Override
	public void clear() {
		int[] empty = new int[0];
		for (int i = 0; i < 16*16; i++) {
			sizes[i] = 0;
			columns[i] = empty;

			tempRunSplitSizes[i] = 0;
			tempRunSplits[i] = empty;
		}
	}

	public void set(int localX, int localZ, ShortList layers) {
		Preconditions.checkArgument(layers.size() % 2 == 0, "Expected even number of shorts in layers");

		int index = getIndex(localX, localZ);
		int size = layers.size() / 2;
		sizes[index] = size;
		int[] column = columns[index];

		if (column.length < size) {
			column = new int[calculateIncreasedLength(size)];
			columns[index] = column;
		}

		for (int i = 0; i < size; i++) {
			short sky = layers.getShort(i * 2);
			short bottom = layers.getShort(i * 2 + 1);
			column[i] = pack(sky, bottom);
		}

		maybeShrinkColumn(index);
	}

	@Override
	public boolean isLit(int localX, int y, int localZ) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];

		if (size == 0)
			return false;

		short yShort = (short) (y - minY);

		// binary search
		int left = 0;
		int right = size - 1;

		while (left <= right) {
			int mid = (left + right) / 2;
			int packed = column[mid];

			short sky = unpackSky(packed);
			short bottom = unpackBottom(packed);

			if (yShort > sky) { // we're higher
				right = mid - 1;
			} else if (yShort < sky) { // we're lower
				if (bottom == -1 || yShort >= bottom) {
					return true;
				} else {
					left = mid + 1;
				}
			} else { // yShort == sky
				return true;
			}
		}

		return false;
	}

	/**
	 * NOTE: unlike all other methods in this class, this uses global y coordinates.
	 */
	@Override
	public Iterable<Run> iterateLitRuns(int localX, int localZ, int lowestSourceY) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];

		int splitsColumnIndex = index * 2 + 1;
		int splitsSize = tempRunSplitSizes[splitsColumnIndex];
		int[] splits = tempRunSplits[splitsColumnIndex];

		List<Run> runs = new ArrayList<>(size);
		runs.add(new Run(lowestSourceY, Integer.MAX_VALUE));

		if (lowestSourceY == Integer.MIN_VALUE) {
			return wrapUnlitRuns(splitsSize, splits, runs);
		}

		for (int i = 0; i < size; i++) {
			int packed = column[i];
			short sky = unpackSky(packed);
			short bottom = unpackBottom(packed);

			int globalSky = sky + minY;
			int globalBottom = bottom == -1 ? Integer.MIN_VALUE : bottom + minY;

			// if we're above lowestSourceY, don't bother

			if (globalBottom >= lowestSourceY)
				continue;

			// if we intersect with default sky, merge
			if (globalSky >= lowestSourceY - 1) {
				runs.set(0, new Run(globalBottom, Integer.MAX_VALUE));
				continue;
			}

			// otherwise, add a new run
			runs.add(new Run(globalBottom, globalSky));
		}

		return wrapLitRuns(splitsSize, splits, runs);
	}

	/**
	 * NOTE: unlike all other methods in this class, this uses global y coordinates.
	 */
	@Override
	public Iterable<Run> iterateUnlitRuns(int localX, int localZ, int lowestSourceY) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];

		int splitsColumnIndex = index * 2;
		int splitsSize = tempRunSplitSizes[splitsColumnIndex];
		int[] splits = tempRunSplits[splitsColumnIndex];

		// fill with all gaps between layers, which are below lowestSourceY
		List<Run> runs = new ArrayList<>(size);

		if (lowestSourceY == Integer.MIN_VALUE) {
			return wrapUnlitRuns(splitsSize, splits, runs);
		}

		if (size == 0) {
			runs.add(new Run(Integer.MIN_VALUE, lowestSourceY - 1));
		} else {
			int lastLowestLit = lowestSourceY;

			// each iteration adds the gap above layer[i]
			for (int i = 0; i < size && lastLowestLit > Integer.MIN_VALUE; i++) {
				int packed = column[i];
				short sky = unpackSky(packed);
				short bottom = unpackBottom(packed);

				int globalSky = sky + minY;
				int globalBottom = bottom == -1 ? Integer.MIN_VALUE : bottom + minY;

				if (globalBottom >= lowestSourceY) // completely lit, what's the point?
					continue;

				if (globalSky < lastLowestLit - 1) // gap
					runs.add(new Run(globalSky + 1, lastLowestLit - 1));

				lastLowestLit = globalBottom;
			}

			// add the gap below the lowest layer
			if (lastLowestLit > Integer.MIN_VALUE && lastLowestLit != lowestSourceY) {
				runs.add(new Run(Integer.MIN_VALUE, lastLowestLit - 1));
			}
		}

		return wrapUnlitRuns(splitsSize, splits, runs);
	}

	@Override
	public void clearTempRunSplits(int localX, int localZ) {
		int[] empty = new int[0];

		int baseIndex = getIndex(localX, localZ) * 2;

		for (int i = 0; i < 2; i++) {
			int index = baseIndex + i;

			tempRunSplitSizes[index] = 0;
			tempRunSplits[index] = empty;
		}
	}

	@Override
	public boolean isColumnEmpty(int localX, int localZ) {
		return sizes[getIndex(localX, localZ)] <= 0;
	}

	@Override
	public boolean hasNoUnlitSplits(int localX, int localZ) {
		return tempRunSplitSizes[getIndex(localX, localZ) * 2] <= 0;
	}

	@Override
	public void debugState(int localX, int localZ, Consumer<String> stringConsumer) {
		int index = getIndex(localX, localZ);
		int size = sizes[index];
		int[] column = columns[index];

		if (size == 0) {
			stringConsumer.accept("  <empty>");
		} else {
			for (int i = 0; i < size; i++) {
				int packed = column[i];
				short sky = unpackSky(packed);
				short bottom = unpackBottom(packed);

				stringConsumer.accept("  layer " + i + ": sky=" + (sky + minY) + ", bottom=" + (bottom == -1 ? "-1" : (bottom + minY)));
			}
		}
	}

	/** wrapped iterable must be sorted top-to-bottom, splits must be sorted bottom-to-top */
	public Iterable<Run> wrapLitRuns(int splitsSize, int[] splits, Iterable<Run> iterable) {
		if (splitsSize <= 0) return iterable;
		return () -> new LitSplittingIterator(splitsSize, splits, iterable.iterator());
	}

	private class LitSplittingIterator implements Iterator<Run> {
		private final int[] splits;
		private final Iterator<Run> iterator;

		private final Deque<Run> remainingSplits = new ArrayDeque<>();

		private int currentSplitIndex;

		private LitSplittingIterator(int splitsSize, int[] splits, Iterator<Run> iterator) {
			this.splits = splits;
			this.iterator = iterator;

			this.currentSplitIndex = splitsSize - 1;
		}

		@Override
		public boolean hasNext() {
			return !remainingSplits.isEmpty() || iterator.hasNext();
		}

		@Override
		public Run next() {
			if (!remainingSplits.isEmpty()) {
				return remainingSplits.removeFirst();
			}

			Run run = iterator.next();
			while (currentSplitIndex >= 0) {
				int splitY = splits[currentSplitIndex] + minY; // becomes the new bottomY of a run
				if (splitY < run.bottomY()) { // split is below run, not relevant (yet)
					return run;
				} else if (splitY > run.topY()) { // split is above run, will never be relevant
					currentSplitIndex--;
				} else if (splitY == run.bottomY()) { // run already fulfills the split
					currentSplitIndex--;
					return run;
				} else { // split is inside run, split it
					Run upper = new Run(splitY, run.topY());

					run = new Run(run.bottomY(), splitY - 1);
					remainingSplits.addFirst(upper);
					currentSplitIndex--;
				}
			}

			return run;
		}
	}

	/** wrapped iterable must be sorted top-to-bottom, splits must be sorted bottom-to-top */
	private Iterable<Run> wrapUnlitRuns(int splitsSize, int[] splits, Iterable<Run> iterable) {
		if (splitsSize <= 0) return iterable;
		return () -> new UnlitSplittingIterator(splitsSize, splits, iterable.iterator());
	}

	private class UnlitSplittingIterator implements Iterator<Run> {
		private final int[] splits;
		private final Iterator<Run> iterator;

		private final Deque<Run> remainingSplits = new ArrayDeque<>();

		private int currentSplitIndex;

		private UnlitSplittingIterator(int splitsSize, int[] splits, Iterator<Run> iterator) {
			this.splits = splits;
			this.iterator = iterator;

			this.currentSplitIndex = splitsSize - 1;
		}

		@Override
		public boolean hasNext() {
			return !remainingSplits.isEmpty() || iterator.hasNext();
		}

		@Override
		public Run next() {
			if (!remainingSplits.isEmpty()) {
				return remainingSplits.removeFirst();
			}

			Run run = iterator.next();
			while (currentSplitIndex >= 0) {
				int splitY = splits[currentSplitIndex] + minY; // becomes the new topY of a run
				if (splitY < run.bottomY()) { // split is below run, not relevant (yet)
					return run;
				} else if (splitY > run.topY()) { // split is above run, will never be relevant
					currentSplitIndex--;
					return run;
				} else if (splitY == run.topY()) { // run already fulfills the split
					currentSplitIndex--;
					return run;
				} else { // split is inside run, split it
					Run lower = new Run(run.bottomY(), splitY);

					run = new Run(splitY + 1, run.topY());
					remainingSplits.addFirst(lower);
					currentSplitIndex--;
				}
			}
			return run;
		}
	}

	/*private class DownwardIterator implements IntIterator {
		private final int[] column;
		private final int size;
		private int i;
		private int currentY;

		public DownwardIterator(int localX, int localZ, int yMax) {
			int index = getIndex(localX, localZ);

			int actualYMax = yMax - minY;

			this.column = columns[index];
			this.size = sizes[index];

			this.i = -1;

			// advance to first layer
			for (int i = 0; i < size; i++) {
				int packed = column[i];
				short sky = unpackSky(packed);
				short bottom = unpackBottom(packed);

				if (bottom != -1 && bottom > actualYMax)
					continue;

				this.i = i;
				currentY = Math.min(currentY, sky);
				break;
			}
		}

		@Override
		public int nextInt() {
			if (i == -1) {
				throw new NoSuchElementException();
			}

			int ret = currentY;

			int packed = column[i];
			short bottom = unpackBottom(packed);

			if (bottom != -1 && ret == bottom) {
				i++;
				if (i == size) {
					i = -1;
				} else {
					packed = column[i];
					currentY = unpackSky(packed);
				}
			} else {
				currentY--;
			}

			return ret + minY;
		}

		@Override
		public boolean hasNext() {
			return i != -1;
		}
	}*/
}
