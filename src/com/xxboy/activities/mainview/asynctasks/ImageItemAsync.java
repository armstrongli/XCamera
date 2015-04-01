package com.xxboy.activities.mainview.asynctasks;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;

public class ImageItemAsync extends AsyncTask<String, Void, Bitmap> {
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
	private final WeakReference<ImageView> imageViewReference;

	public ImageItemAsync(String path, ImageView imageView) {
		this.path = path;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// Logger.log("Loading imageview: " + this.imageView);
		// ImageItemTaskPool.addToArray(this.imageView, this);
		if (this.isCancelled()) {
			return null;
		}
		Bitmap bitmap = XCacheUtil.getFromMemCache(path);
		if (bitmap == null) {
			if (this.isCancelled()) {
				return null;
			}
			bitmap = getImageItem();
			return XCacheUtil.pushToCache(this.path, bitmap);
		} else {
			return bitmap;
		}
		// if (this.isCancelled()) {
		// return null;
		// }
		// XQueueUtil.executeTaskDirectly(new ImageLoader(0, this.path, this.imageView));
		// ImageItemTaskPool.removeFromPool(this.imageView);
		// return null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final ImageItemAsync bitmapWorkerTask = getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}

			// if (imageView != null) {
			// imageView.setImageBitmap(bitmap);
			// }
		}

	}

	private Bitmap getImageItem() {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageItemOption(this.path));
	}

	public static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<ImageItemAsync> bitmapTaskReference;

		public AsyncDrawable(Bitmap bitmap, ImageItemAsync imageItemAsync) {
			super(bitmap);
			this.bitmapTaskReference = new WeakReference<ImageItemAsync>(imageItemAsync);
		}

		public WeakReference<ImageItemAsync> getBitmapTaskReference() {
			return bitmapTaskReference;
		}

	}

	public static boolean cancelPotentialWork(String data, ImageView imageView) {
		final ImageItemAsync bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.path;
			// If bitmapData is not yet set or it differs from the new data
			if (bitmapData != null && !bitmapData.equals(data)) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}

	private static ImageItemAsync getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapTaskReference().get();
			}
		}
		return null;
	}

}
