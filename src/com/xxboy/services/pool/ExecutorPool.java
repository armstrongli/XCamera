package com.xxboy.services.pool;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.xxboy.services.runnable.ImageExecutor;

public class ExecutorPool {

	private static Object lock = new Object();

	private static ConcurrentHashMap<String, Integer> runningPath2Position = new ConcurrentHashMap<String, Integer>();

	private static ConcurrentHashMap<String, ImageExecutor> waitingImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

	private static ConcurrentHashMap<String, ImageExecutor> runningImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

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

	private static final void moveToWaitingExecutor(String imagePath) {
		synchronized (lock) {
			runningPath2Position.remove(imagePath);
			ImageExecutor executor = runningImageExecutorPool.remove(imagePath);
			if (executor != null) {
				waitingImageExecutorPool.put(imagePath, executor);
			}
		}
	}

	private static final void moveToRunningExecutorPool(String imagePath) {
		synchronized (lock) {
			ImageExecutor executor = waitingImageExecutorPool.get(imagePath);
			if (executor != null) {
				runningImageExecutorPool.put(imagePath, executor);
			}
		}
	}

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
