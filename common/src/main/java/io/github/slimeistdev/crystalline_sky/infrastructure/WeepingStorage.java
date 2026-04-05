package io.github.slimeistdev.crystalline_sky.infrastructure;

import java.util.function.Consumer;

/**
 * Note: y coordinates are [0, ...)
 */
public interface WeepingStorage {
	void insertSky(int localX, int y, int localZ, WeepingScanner scanner);

	void insertSolid(int localX, int y, int localZ, WeepingScanner scanner);

	void insertAir(int localX, int y, int localZ, WeepingScanner scanner);

	void clear();

	boolean isLit(int localX, int y, int localZ);

	Iterable<Run> iterateLitRuns(int localX, int localZ, int lowestSourceY);

	Iterable<Run> iterateUnlitRuns(int localX, int localZ, int lowestSourceY);

	void clearTempRunSplits(int localX, int localZ);

	boolean isColumnEmpty(int localX, int localZ);

	boolean hasNoUnlitSplits(int localX, int localZ);

	void debugState(int localX, int localZ, Consumer<String> stringConsumer);

	/** A continuous run of positions [bottomY, topY] */
	record Run(int bottomY, int topY) {
		public Run withBottomY(int newBottomY) {
			return new Run(newBottomY, this.topY);
		}

		public Run withTopY(int newTopY) {
			return new Run(this.bottomY, newTopY);
		}
	}

	interface WeepingScanner {
		/** Scans downwards from yMax (exclusive) to find the first solid block.
		 * Returns the y coordinate of the first solid block, or -1 if none was found.
		 * The returned value is guaranteed to be less than yMax.
		 */
		int scan(int localX, int yMax, int localZ);

		int scanForWeepingSky(int localX, int yMax, int localZ);

		boolean faceBlocksLight(int localX, int upperY, int localZ);
	}
}
