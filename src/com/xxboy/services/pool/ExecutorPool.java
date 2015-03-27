package com.xxboy.services.pool;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.xxboy.services.runnable.ImageExecutor;

public class ExecutorPool {

	/** pool lock */
	private static Object lock = new Object();

	/**
	 * image path to image view index. this can be one quick path to get image position and check whether need to stop some running thread and make the thread focus on the showing
	 * image view
	 */
	private static ConcurrentHashMap<String, Integer> runningPath2Position = new ConcurrentHashMap<String, Integer>();

	/**
	 * running pool<br/>
	 * Pool for storing the running executors.<br/>
	 * When the task is finished, the executor will be removed.
	 */
	private static ConcurrentHashMap<String, ImageExecutor> runningImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

	/**
	 * Image Path = Running Thread
	 */
	private static ConcurrentHashMap<String, Thread> runningThreadPool = new ConcurrentHashMap<String, Thread>();

	/**
	 * execute one executor thread. <br/>
	 * 1. check whether the current pool(running & waiting) has the image.<br/>
	 * 1.1. if have, stop the existing one and reset the image view and index parameters.<br/>
	 * 1.2. if not, add to running thread.<br/>
	 * 2. check the current running pool to make sure whether there're executors which don't need run any longer for show.<br/>
	 * 2.1. if have, stop the current running pool, and move to waiting pool.<br/>
	 * 2.2. if not, ignore.<br/>
	 * 3. add the current executor to running pool<br/>
	 * 4. start the current executor.
	 * 
	 * @param executor
	 */
	public static final void executeExecutor(ImageExecutor executor) {
		String path = executor.getImagePath();
		int position = executor.getPosition();
		synchronized (lock) {
			// check whether need to run
			Thread runningThread = runningThreadPool.get(path);
			if (!RunnablePool.checkCanBeRan(position)) {
				runningImageExecutorPool.remove(path);
				runningPath2Position.remove(path);
				if (runningThread != null) {
					runningThread.interrupt();
				}
				return;
			}

			// check whether path - position containers the one to be ran
			runningPath2Position.put(path, position);

			ImageExecutor tmpRunningExecutor = runningImageExecutorPool.get(path);
			if (executor.equals(tmpRunningExecutor) && runningThreadPool.containsKey(path)) {
				return;
			} else {
				runningImageExecutorPool.remove(path);
				if (runningThread != null) {
					runningThread.interrupt();
				}
			}

			// check can be run, and run if can
			Thread targetRunThread = new Thread(tmpRunningExecutor);
			runningThreadPool.put(path, targetRunThread);
			targetRunThread.start();

			// -- check whether the position has been over dual. if it is, move
			// to waiting pool
			Collection<Integer> runningPathes = runningPath2Position.values();
			boolean needToClearRunningThread = false;
			for (Integer item : runningPathes) {
				if (item == null) {
					continue;
				}
				boolean canBeRan = RunnablePool.checkCanBeRan(item);
				if (!canBeRan) {
					needToClearRunningThread = true;
					break;
				}
			}
			if (needToClearRunningThread) {
				for (String item : runningImageExecutorPool.keySet()) {
					Integer potentialPosition = runningPath2Position.get(item);
					if (potentialPosition != null && !RunnablePool.checkCanBeRan(potentialPosition)) {
						runningImageExecutorPool.remove(item);
						runningThreadPool.remove(item).interrupt();
					}
				}
			}
		}
	}

	/**
	 * remove one executor.<br/>
	 * this happens only when the executor is finished successfully.
	 * 
	 * @param imagePath
	 */
	public static final void removeExecutor(String imagePath) {
		synchronized (lock) {
			runningPath2Position.remove(imagePath);
			runningImageExecutorPool.remove(imagePath);
			Thread t = runningThreadPool.remove(imagePath);
			if (t != null) {
				t.interrupt();
			}
		}
	}

	/**
	 * clear all image executor pool, thread.
	 */
	public static final void resetExecutorPool() {
		synchronized (lock) {
			for (Thread item : runningThreadPool.values()) {
				item.interrupt();
			}
			runningThreadPool.clear();
			runningPath2Position.clear();
			runningImageExecutorPool.clear();
		}
	}
}
