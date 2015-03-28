package com.xxboy.services.pool;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.xxboy.log.Logger;
import com.xxboy.services.runnable.ImageLoader;
import com.xxboy.utils.XQueueUtil;

public final class RunnablePool {
	private static int startIndex = 0;
	private static int endIndex = Integer.MAX_VALUE;
	private static int futureIndex = 0;
	private static Object indexLock = new Object();

	public static void syncFutureIndex(int futureIndex) {
		RunnablePool.futureIndex = futureIndex;
	}

	/**
	 * sync view item start position and end position.
	 * 
	 * @param newStartIndex
	 * @param newEndIndex
	 */
	public static void syncRunnableIndexes(int newStartIndex, int newEndIndex) {
		synchronized (indexLock) {
			if (startIndex == newStartIndex && endIndex == newEndIndex) {
				return;
			}
			Logger.log("Pool-executing-10 : sync >>> " + startIndex + "-" + newStartIndex + " & " + newEndIndex + "-" + endIndex);
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
		Logger.log("Pool-executing-10 : check " + toBeRanIndex + " >>> " + startIndex + "-" + endIndex + "--" + futureIndex);
		synchronized (indexLock) {
			return (startIndex >= endIndex) || (toBeRanIndex >= startIndex && toBeRanIndex <= endIndex) || (futureIndex <= startIndex && futureIndex <= toBeRanIndex) || (futureIndex >= endIndex && futureIndex >= toBeRanIndex);
		}
	}

	// ------------------------------------------------------------------------------------------

	public static Object imageLoaderLock = new Object();

	/**
	 * 
	 */
	private static ConcurrentHashMap<String, Integer> imageLoader2Position = new ConcurrentHashMap<String, Integer>();
	/**
	 * image loader pool<br/>
	 */
	private static ConcurrentHashMap<String, ImageLoader> runningImageLoaderPool = new ConcurrentHashMap<String, ImageLoader>();

	/**
	 * execute image loader
	 * 
	 * @param imageLoader
	 */
	public static void runImageLoader(ImageLoader imageLoader) {
		if (checkCanBeRan(imageLoader.getPosition())) {

			synchronized (imageLoaderLock) {
				// get base data ready
				imageLoader2Position.put(imageLoader.getImagePath(), imageLoader.getPosition());
				runningImageLoaderPool.put(imageLoader.getImagePath(), imageLoader);

				// check history pool and clean the history invalid image loaders.
				boolean needClean = false;
				Collection<Integer> imageLoaderPositions = imageLoader2Position.values();
				for (int item : imageLoaderPositions) {
					needClean |= !checkCanBeRan(item);
					if (needClean) {
						break;
					}
				}

				// clear the running ones from UI main thread.
				if (needClean) {
					for (String path : runningImageLoaderPool.keySet()) {
						if (!checkCanBeRan(runningImageLoaderPool.get(path).getPosition())) {
							removeRunningImageLoader(runningImageLoaderPool.remove(path));
						}
					}
				}
			}

			XQueueUtil.executeTaskDirectly(imageLoader);
		}
	}

	public static void removeRunningImageLoader(Runnable imageLoader) {
		XQueueUtil.removeRunningTasks(imageLoader);
	}
}
