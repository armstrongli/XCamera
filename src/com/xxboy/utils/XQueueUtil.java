package com.xxboy.utils;

import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;

import com.xxboy.log.Logger;
import com.xxboy.services.runnable.ImageLoader;

public final class XQueueUtil {
	private static int startVisableIndex = 0;
	private static int lastVisableIndex = 0;

	/**
	 * sync the visable indexes for setting limitation of image runnable tasks
	 * 
	 * @param startVisableIndex
	 * @param lastVisableIndex
	 */
	public static final void syncVisableIndexes(int startVisableIndex, int lastVisableIndex) {
		XQueueUtil.startVisableIndex = startVisableIndex;
		XQueueUtil.lastVisableIndex = lastVisableIndex;
	}

	/**
	 * Runnable Pool<br/>
	 * This is one pool to handle the runnables which will be executed<br/>
	 * in the system image view pool and set images.
	 */
	private static final ConcurrentHashMap<String, ImageLoader> runnablePool = new ConcurrentHashMap<String, ImageLoader>();

	/**
	 * check whether the runnable existing in the runnable pool.
	 * 
	 * @param imagePath
	 * @param r
	 * @return
	 */
	private static boolean checkExistingRunnable(final String imagePath, final ImageLoader r) {
		String key = imagePath + r.getImagePath();
		return runnablePool.containsKey(key);
	}

	/**
	 * remove one Runnable from Runnable Pool.
	 * 
	 * @param imagePath
	 * @param r
	 * @return
	 */
	private static Runnable removeRunnableFromRunnablePool(final String imagePath, final ImageLoader r) {
		String key = imagePath + r.getImagePath();
		return runnablePool.remove(key);
	}

	/**
	 * put one Runnable to Runnable Pool for executing.<br/>
	 * This will be one executable runnable to prepare images for putting into OS main view thread.
	 * 
	 * @param imagePath
	 * @param r
	 * @return
	 */
	private static Runnable addRunnableToRunnablePool(final String imagePath, final ImageLoader r) {
		return runnablePool.put(imagePath + r.getImagePath(), r);
	}

	/**
	 * remove one Runnable from OS main thread.<br/>
	 * This operation is used for cut the one which won't be used anymore or <br/>
	 * another thread will be used to show another image.
	 * 
	 * @param imagePath
	 * @param r
	 */
	private static void removeFromOSMainThead(final String imagePath, final Runnable r) {
		removeRunningTasks(r);
	}

	/**
	 * run one task in OS main thread.
	 * 
	 * @param imagePath
	 * @param r
	 */
	private static void executeInOSMainThread(final String imagePath, final Runnable r) {
		executeTaskDirectly(r);
	}

	/**
	 * execute task: remove from runnable pool after the main thread finish. This one is called by the runnable itself.<br/>
	 * 
	 * @param imagePath
	 * @param r
	 */
	private static void executeRemoveFromRunnablePoolWhenMainThreadTaskFinishes(final String imagePath, final ImageLoader r) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				XQueueUtil.removeRunnableFromRunnablePool(imagePath, r);
			}
		}.start();
	}

	/**
	 * exec for executing in main thread runnable instance
	 * 
	 * @param imagePath
	 * @param r
	 */
	public static void execRemoveFromRunnablePoolAfterSetImages(final ImageLoader r) {
		executeRemoveFromRunnablePoolWhenMainThreadTaskFinishes(r.getImagePath(), r);
	}

	/**
	 * runnable pool management.<br/>
	 * 1. check whether existing in runnable pool<br/>
	 * 2. if existing in pool, try to remove from OS main thread<br/>
	 * 3. try to remove from runnable pool<br/>
	 * 4. add new runnable to pool<br/>
	 * 5. execute it in OS main thread<br/>
	 * 5.1. remove runnable from pool after runnable thread finishes in OS main thread
	 * 
	 * @param imageLoader
	 */
	public static void execAddImage(final ImageLoader imageLoader) {
		String imagePath = imageLoader.getImagePath();
		boolean isExists = checkExistingRunnable(imagePath, imageLoader);
		if (isExists) {
			removeFromOSMainThead(imagePath, imageLoader);
		}
		removeRunnableFromRunnablePool(imagePath, imageLoader);
		if (imageLoader.getPosition() < startVisableIndex || imageLoader.getPosition() > lastVisableIndex) {
			return;
		}
		addRunnableToRunnablePool(imagePath, imageLoader);
		executeInOSMainThread(imagePath, imageLoader);
	}

	private static Handler handler;

	public static final void setHandler(Handler handler) {
		XQueueUtil.handler = handler;
	}

	public static final void executeTaskDirectly(Runnable r) {
		XQueueUtil.handler.post(r);
	}

	private static void removeRunningTasks(Runnable r) {
		if (r == null) {
			return;
		}
		try {
			handler.removeCallbacks(r);
		} catch (Exception e) {
			Logger.log("Removing unused tasks");
		}
	}
}
