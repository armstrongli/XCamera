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
		@SuppressWarnings("rawtypes")
		private static ConcurrentHashMap<String, AsyncTask> imageViewAsyncPool = new ConcurrentHashMap<String, AsyncTask>();

		private static boolean checkExists(ImageView checked) {
			return imageViewAsyncPool.containsKey(checked.toString());
		}

		/**
		 * remove from image view pool and stop the thread.
		 * 
		 * @param path
		 * @return if the task has been completed, return false. else return true.
		 */
		@SuppressWarnings("rawtypes")
		private static boolean stopAndRemoveFromPool(ImageView path) {
			AsyncTask task = imageViewAsyncPool.remove(path.toString());
			if (task.isCancelled()) {
				return true;
			} else {
				Logger.log("Canceling: " + path);
				return task.cancel(true);
			}
		}

		/**
		 * just remove from pool.
		 * 
		 * @param path
		 */
		private static void removeFromPool(ImageView path) {
			imageViewAsyncPool.remove(path.toString());
		}

		@SuppressWarnings("rawtypes")
		private static boolean addToArray(ImageView path, AsyncTask task) {
			Logger.log("Adding Image Item to Pool: " + path.toString());
			boolean exists = checkExists(path);
			if (exists) {
				stopAndRemoveFromPool(path);
			}
			imageViewAsyncPool.put(path.toString(), task);

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
		Logger.log("Loading imageview: " + this.imageView);
		ImageItemTaskPool.addToArray(this.imageView, this);
		if (this.isCancelled()) {
			return null;
		}
		Bitmap bitmap = XCacheUtil.getFromMemCache(path);
		if (bitmap != null && !bitmap.isRecycled() && (bitmap.getWidth() + bitmap.getHeight() > 0)) {

		} else {
			if (this.isCancelled()) {
				return null;
			}
			bitmap = getImageItem(this.path);
			XCacheUtil.pushToCache(this.path, bitmap);
		}
		if (this.isCancelled()) {
			return null;
		}
		XQueueUtil.executeTaskDirectly(new ImageLoader(0, this.path, this.imageView));
		ImageItemTaskPool.removeFromPool(this.imageView);
		return null;
	}

	private Bitmap getImageItem(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageItemOption(imagePath));
	}
}
