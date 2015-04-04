package com.xxboy.drawables;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.xxboy.async.XBitmapAsyncTask;

public class XBitmapDrawable extends BitmapDrawable {

	private final WeakReference<XBitmapAsyncTask> asyncTaskReference;

	public XBitmapDrawable(Bitmap bitmap, XBitmapAsyncTask imageItemAsync) {
		super(bitmap);
		this.asyncTaskReference = new WeakReference<XBitmapAsyncTask>(imageItemAsync);
	}

	public WeakReference<XBitmapAsyncTask> getBitmapTaskReference() {
		return asyncTaskReference;
	}

	public static boolean cancelPotentialWork(String data, ImageView imageView) {
		final XBitmapAsyncTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.getImagePath();
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

	public static XBitmapAsyncTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof XBitmapDrawable) {
				final XBitmapDrawable asyncDrawable = (XBitmapDrawable) drawable;
				return asyncDrawable.getBitmapTaskReference().get();
			}
		}
		return null;
	}
}
