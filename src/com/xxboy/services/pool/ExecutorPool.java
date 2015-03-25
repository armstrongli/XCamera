package com.xxboy.services.pool;

import java.util.concurrent.ConcurrentHashMap;

import com.xxboy.services.runnable.ImageExecutor;

public class ExecutorPool {

	private static Object lock = new Object();

	private static ConcurrentHashMap<String, ImageExecutor> waitingImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

	private static ConcurrentHashMap<String, ImageExecutor> runningImageExecutorPool = new ConcurrentHashMap<String, ImageExecutor>();

	public static final void executeExecutor(ImageExecutor executor) {
		String path = executor.getImagePath();
		synchronized (lock) {
			if (waitingImageExecutorPool.containsKey(path)) {
				ImageExecutor tmpExecutor = waitingImageExecutorPool.get(path);
				tmpExecutor.setImageView(executor.getImageView());
				tmpExecutor.setPosition(executor.getPosition());
				moveToRunningExecutorPool(path);
			}
		}
	}

	private static final void moveToWaitingExecutor(String imagePath) {
		synchronized (lock) {
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
			runningImageExecutorPool.remove(imagePath);
		}
	}

}
