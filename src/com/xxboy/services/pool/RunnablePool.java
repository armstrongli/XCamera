package com.xxboy.services.pool;

public final class RunnablePool {
	private static int startIndex = 0;
	private static int endIndex = 0;
	private static Object indexLock = new Object();

	public static void syncRunnableIndexes(int newStartIndex, int newEndIndex) {
		synchronized (indexLock) {
			startIndex = newStartIndex;
			endIndex = newEndIndex;
		}
	}

	public static boolean checkCanBeRan(int toBeRanIndex) {
		return toBeRanIndex >= startIndex && toBeRanIndex <= endIndex;
	}
}
