package com.xxboy.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;

import com.xxboy.log.Logger;
import com.xxboy.services.runnable.ImageLoader;
import com.xxboy.xcamera.XCamera;

public final class XQueueUtil {
	/**
	 * Runnable Pool<br/>
	 * This is one pool to handle the runnables which will be executed<br/>
	 * in the system image view pool and set images.
	 */
	private static final ConcurrentHashMap<String, Runnable> runnablePool = new ConcurrentHashMap<String, Runnable>();

	/**
	 * check whether the runnable existing in the runnable pool.
	 * 
	 * @param imagePath
	 * @param r
	 * @return
	 */
	private static boolean checkExistingRunnable(final String imagePath, final Runnable r) {
		String key = imagePath + r;
		return runnablePool.containsKey(key);
	}

	/**
	 * remove one Runnable from Runnable Pool.
	 * 
	 * @param imagePath
	 * @param r
	 * @return
	 */
	private static Runnable removeRunnableFromRunnablePool(final String imagePath, final Runnable r) {
		String key = imagePath + r;
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
	private static Runnable addRunnableToRunnablePool(final String imagePath, final Runnable r) {
		return runnablePool.put(imagePath + r, r);
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
		executeTask(r);
	}

	/**
	 * execute task: remove from runnable pool after the main thread finish. This one is called by the runnable itself.<br/>
	 * 
	 * @param imagePath
	 * @param r
	 */
	private static void executeRemoveFromRunnablePoolWhenMainThreadTaskFinishes(final String imagePath, final Runnable r) {
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
	public static void execRemoveFromRunnablePoolAfterSetImages(final String imagePath, final Runnable r) {
		executeRemoveFromRunnablePoolWhenMainThreadTaskFinishes(imagePath, r);
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
		addRunnableToRunnablePool(imagePath, imageLoader);
		executeInOSMainThread(imagePath, imageLoader);
	}

	private static Handler handler;

	public static final void setHandler(Handler handler) {
		XQueueUtil.handler = handler;
	}

	private static boolean AUTO_LOAD_DIRECTLY = false;

	private static LinkedList<Integer> maskQueue = new LinkedList<Integer>();
	private static LinkedList<Integer> kQueue = new LinkedList<Integer>();
	private static Map<Integer, Runnable> rQueue = new LinkedHashMap<Integer, Runnable>();

	static {
		for (int i = XCamera.XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT; i >= 0; i--) {
			maskQueue.add(0, i);
		}
	}

	public static synchronized final void run() {
		AUTO_LOAD_DIRECTLY = true;
		while (kQueue.size() > 0) {
			Logger.log("Posting: " + kQueue.get(0));
			XQueueUtil.handler.post(rQueue.remove(kQueue.remove(0)));
		}
	}

	/**
	 * reset not auto load
	 */
	private static synchronized final void resetAutoLoad() {
		AUTO_LOAD_DIRECTLY = false;
	}

	public static synchronized final void addMaskTask(Integer taskIndex) {
		if (maskQueue.contains(taskIndex)) {
			return;
		}
		maskQueue.add(taskIndex);
		if (maskQueue.size() > XCamera.XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT) {
			maskQueue.remove(0);
		}
	}

	public static final void executeTask(Runnable r) {
		XQueueUtil.handler.post(r);
	}

	public static synchronized final void addTasks(Integer taskIndex, Runnable r) {
		/* reduce the task queue to small size */
		int toBeRemovedCount = kQueue.size() - XCamera.XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT;
		while (toBeRemovedCount > 0) {
			removeRunningTasks(rQueue.remove(kQueue.remove(0)));
			toBeRemovedCount--;
		}
		/* remove unused tasks */
		while (true) {
			if (kQueue.size() > 0 && Math.abs(taskIndex - kQueue.get(0)) > XCamera.XCameraConst.GLOBAL_X_GRIDVIEW_VISIABLE_COUNT) {
				removeRunningTasks(rQueue.remove(kQueue.remove(0)));
			} else {
				break;
			}
		}
		/* add new task */
		if (maskQueue.contains(taskIndex)) {
			if (kQueue.add(taskIndex)) {
				rQueue.put(taskIndex, r);
			}
		}

		if (AUTO_LOAD_DIRECTLY) {
			run();
		}
	}

	public static final boolean checkInMask(Integer index) {
		return maskQueue.contains(index);
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
