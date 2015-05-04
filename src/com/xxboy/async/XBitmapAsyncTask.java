package com.xxboy.async;

import java.lang.ref.WeakReference;

import com.xxboy.drawables.XBitmapDrawable;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public abstract class XBitmapAsyncTask extends AsyncTask<Void, Void, Bitmap> {

	private String imagePath;
	private WeakReference<ImageView> weakImageView;

	public XBitmapAsyncTask(String imagePath, ImageView imageView) {
		super();
		this.imagePath = imagePath;
		this.weakImageView = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected final Bitmap doInBackground(Void... params) {
		return doInBackground();
	}

	protected abstract Bitmap doInBackground();

	@Override
	protected final void onPostExecute(Bitmap result) {
		postExecute(result);
	}

	protected void postExecute(Bitmap result) {
		if (result == null || result.isRecycled()) {
			return;
		}
		WeakReference<ImageView> imageViewReference = this.getWeakImageView();
		if (imageViewReference != null && result != null) {
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				final XBitmapAsyncTask bitmapWorkerTask = XBitmapDrawable.getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask) {
					imageView.setImageBitmap(result);
				}
			}
		}

	}

	public final String getImagePath() {
		return this.imagePath;
	}

	public final WeakReference<ImageView> getWeakImageView() {
		return this.weakImageView;
	}

}
