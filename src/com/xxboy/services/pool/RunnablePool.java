package com.xxboy.services.pool;

public final class RunnablePool {
	private static int startIndex = 0;
	private static int endIndex = 0;
	private static Object indexLock = new Object();

	/**
	 * sync view item start position and end position.
	 * 
	 * @param newStartIndex
	 * @param newEndIndex
	 */
	public static void syncRunnableIndexes(int newStartIndex, int newEndIndex) {
		synchronized (indexLock) {
			startIndex = newStartIndex;
			endIndex = newEndIndex;
		}
	}

	/**
	 * check whether the exact position is shown
	 * 
	 * @param toBeRanIndex
	 * @return
	 */
	public static boolean checkCanBeRan(int toBeRanIndex) {
		synchronized (indexLock) {
			return toBeRanIndex >= startIndex && toBeRanIndex <= endIndex;
		}
	}
}
