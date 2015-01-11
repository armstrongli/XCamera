package com.xxboy.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.xxboy.log.Logger;
import com.xxboy.xcamera.XCamera;

public final class XQueueUtil {
	private static Handler handler;

	public static final void setHandler(Handler handler) {
		XQueueUtil.handler = handler;
	}

	private static boolean AUTO_LOAD_DIRECTLY = false;

	private static List<Integer> maskQueue = new LinkedList<Integer>();
	private static List<Integer> kQueue = new LinkedList<Integer>();
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
	public static synchronized final void resetAutoLoad() {
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
