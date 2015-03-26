package com.xxboy.services.pool;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.xxboy.services.runnable.ImageExecutor;

public class ExecutorPool {

	/** pool lock */
	private static Object lock = new Object();

	/** image path to image view index. this can be one quick path to get image position and check whether need to stop some running thread and make the thread focus on the showing image view */
	private static ConcurrentHashMap<String, Integer> runningPath2Position = new ConcurrentHashMap<String, Integer>();

	/**
	 * waiting pool.<br/>
	 * This is one pool for pooling the unruning waiting images.<br/>
	 * It can speed for showing images.
	 */
	private static ConcurrentHashMap<String, ImageExecutor> waitingImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

	/**
	 * running pool<br/>
	 * Pool for storing the running executors.<br/>
	 * When the task is finished, the executor will be removed.
	 */
	private static ConcurrentHashMap<String, ImageExecutor> runningImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

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
			if (!runningPath2Position.containsKey(path)) {
				runningPath2Position.put(path, position);
			}
			if (runningImageExecutorPool.contains(path)) {
				ImageExecutor tmpExecutor = runningImageExecutorPool.get(path);
				tmpExecutor.stop();
				tmpExecutor.setImageView(executor.getImageView());
				tmpExecutor.setPosition(position);
			} else if (waitingImageExecutorPool.containsKey(path)) {
				ImageExecutor tmpExecutor = waitingImageExecutorPool.get(path);
				tmpExecutor.setImageView(executor.getImageView());
				tmpExecutor.setPosition(position);
				moveToRunningExecutorPool(path);
			} else {
				runningImageExecutorPool.put(path, executor);
			}
			if (RunnablePool.checkCanBeRan(position)) {
				runningImageExecutorPool.get(path).start();
			} else {
				moveToWaitingExecutor(path);
			}

			// -- check whether the position has been over dual. if it is, move to waiting pool
			Collection<Integer> runningPathes = runningPath2Position.values();
			boolean needMoveToWaitingPool = false;
			for (Integer item : runningPathes) {
				boolean canBeRan = RunnablePool.checkCanBeRan(item);
				if (!canBeRan) {
					needMoveToWaitingPool = true;
					break;
				}
			}
			if (needMoveToWaitingPool) {
				for (String item : runningImageExecutorPool.keySet()) {
					if (!RunnablePool.checkCanBeRan(runningPath2Position.get(item))) {
						moveToWaitingExecutor(path);
					}
				}
			}
		}
	}

	/**
	 * stop the running one and move it to waiting pool.
	 * 
	 * @param imagePath
	 */
	private static final void moveToWaitingExecutor(String imagePath) {
		synchronized (lock) {
			runningPath2Position.remove(imagePath);
			ImageExecutor executor = runningImageExecutorPool.remove(imagePath);
			if (executor != null) {
				executor.stop();// stop the running image executor
				waitingImageExecutorPool.put(imagePath, executor);
			}
		}
	}

	/**
	 * move waiting executor to running executors' pool from waiting executors' pool.
	 * 
	 * @param imagePath
	 */
	private static final void moveToRunningExecutorPool(String imagePath) {
		synchronized (lock) {
			ImageExecutor executor = waitingImageExecutorPool.get(imagePath);
			if (executor != null) {
				runningImageExecutorPool.put(imagePath, executor);
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
		}
	}

	/**
	 * clear all image executor pool, thread.
	 */
	public static final void resetExecutorPool() {
		synchronized (lock) {
			for (ImageExecutor item : runningImageExecutorPool.values()) {
				item.stop();
			}
			for (ImageExecutor item : waitingImageExecutorPool.values()) {
				item.stop();
			}
			runningPath2Position.clear();
			runningImageExecutorPool.clear();
			waitingImageExecutorPool.clear();
		}
	}
}
