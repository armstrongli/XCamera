package com.xxboy.activities.mainview.asynctasks;

import java.lang.ref.WeakReference;

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

	private String path;
	private final WeakReference<ImageView> imageViewReference;

	public ImageItemAsync(String path, ImageView imageView) {
		this.path = path;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		if (this.isCancelled()) {
			return null;
		}
		Bitmap bitmap = XCacheUtil.getFromMemCache(path);
		if (bitmap == null) {
			if (this.isCancelled()) {
				return null;
			}
			bitmap = getImageItem();
			Logger.log("Sample size: " + bitmap.getWidth() + "-" + bitmap.getHeight());
			return XCacheUtil.pushToCache(this.path, bitmap);
		} else {
			return bitmap;
		}
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
