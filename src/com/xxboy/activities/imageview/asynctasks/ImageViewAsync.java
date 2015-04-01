package com.xxboy.activities.imageview.asynctasks;

import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.activities.imageview.runnables.ImageViewLoader;
import com.xxboy.log.Logger;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class ImageViewAsync extends AsyncTask<Void, Void, Void> {

	private static final class ImageViewTaskArray {
		private static Object poolLock = new Object();
		private static ConcurrentHashMap<String, ImageViewAsync> imageViewAsyncPool = new ConcurrentHashMap<String, ImageViewAsync>();

		private static boolean checkExists(String checked) {
			return imageViewAsyncPool.containsKey(checked);
		}

		/**
		 * remove from image view pool and stop the thread.
		 * 
		 * @param path
		 * @return if the task has been completed, return false. else return true.
		 */
		private static boolean stopAndRemoveFromPool(String path) {
			synchronized (poolLock) {
				ImageViewAsync task = imageViewAsyncPool.remove(path);
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

		private static boolean addToArray(String path, ImageViewAsync task) {
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

	public ImageViewAsync(String path, ImageView imageView) {
		this.path = path;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			if (!this.isCancelled()) {
				ImageViewTaskArray.addToArray(this.path, this);
			}
			if (!this.isCancelled()) {
				Bitmap bitmap = XCacheUtil.getImaveView(this.path);
				if (bitmap != null && !bitmap.isRecycled() && (bitmap.getWidth() + bitmap.getHeight() > 0)) {
					if (!this.isCancelled()) {
						XQueueUtil.executeTaskDirectly(new ImageViewLoader(this.path, imageView));
					}
				} else {
					if (!this.isCancelled()) {
						bitmap = getImage(this.path);
					}
					if (!this.isCancelled()) {
						XQueueUtil.executeTaskDirectly(new ImageViewLoader(this.path, imageView));
					}
					if (!this.isCancelled()) {
						XCacheUtil.pushImageView(this.path, bitmap);
					}
				}
			}
			if (!this.isCancelled()) {
				ImageViewTaskArray.removeFromPool(this.path);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
		return null;
	}

	private Bitmap getImage(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageViewOption(imagePath));
	}

}
