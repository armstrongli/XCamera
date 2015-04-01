package com.xxboy.activities.mainview.asynctasks;

import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.activities.mainview.runnables.ImageLoader;
import com.xxboy.log.Logger;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class ImageItemAsync extends AsyncTask<Void, Void, Void> {
	private static final class ImageItemTaskPool {
		private static Object poolLock = new Object();
		@SuppressWarnings("rawtypes")
		private static ConcurrentHashMap<String, AsyncTask> imageViewAsyncPool = new ConcurrentHashMap<String, AsyncTask>();

		private static boolean checkExists(String checked) {
			return imageViewAsyncPool.containsKey(checked);
		}

		/**
		 * remove from image view pool and stop the thread.
		 * 
		 * @param path
		 * @return if the task has been completed, return false. else return true.
		 */
		@SuppressWarnings("rawtypes")
		private static boolean stopAndRemoveFromPool(String path) {
			synchronized (poolLock) {
				AsyncTask task = imageViewAsyncPool.remove(path);
				if (task.isCancelled()) {
					return true;
				} else {
					Logger.log("Canceling: " + path);
					return task.cancel(true);
				}
			}
		}

		/**
		 * just remove from pool.
		 * 
		 * @param path
		 */
		private static void removeFromPool(String path) {
			imageViewAsyncPool.remove(path);
		}

		@SuppressWarnings("rawtypes")
		private static boolean addToArray(String path, AsyncTask task) {
			synchronized (poolLock) {
				boolean exists = checkExists(path);
				if (exists) {
					stopAndRemoveFromPool(path);
				}
				imageViewAsyncPool.put(path, task);
			}

			return true;
		}
	}

	private String path;
	private ImageView imageView;

	public ImageItemAsync(String path, ImageView imageView) {
		this.path = path;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(Void... params) {
		ImageItemTaskPool.addToArray(this.path, this);
		if (this.isCancelled()) {
			return null;
		}
		Bitmap bitmap = XCacheUtil.getFromMemCache(path);
		if (bitmap != null && !bitmap.isRecycled() && (bitmap.getWidth() + bitmap.getHeight() > 0)) {
			if (this.isCancelled()) {
				return null;
			}
			XQueueUtil.executeTaskDirectly(new ImageLoader(0, this.path, this.imageView));
		} else {
			if (this.isCancelled()) {
				return null;
			}
			bitmap = getImageItem(this.path);
			XCacheUtil.pushToCache(this.path, bitmap);
			if (this.isCancelled()) {
				return null;
			}
			XQueueUtil.executeTaskDirectly(new ImageLoader(0, this.path, this.imageView));
		}
		ImageItemTaskPool.removeFromPool(path);
		return null;
	}

	private Bitmap getImageItem(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageItemOption(imagePath));
	}
}
