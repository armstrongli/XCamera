package com.xxboy.async;

import java.lang.ref.WeakReference;

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

	protected abstract void postExecute(Bitmap result);

	public final String getImagePath() {
		return this.imagePath;
	}

	public final WeakReference<ImageView> getWeakImageView() {
		return this.weakImageView;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof XBitmapAsyncTask) {
			return ((XBitmapAsyncTask) o).imagePath.equals(this.imagePath) && ((XBitmapAsyncTask) o).weakImageView == this.weakImageView;
		} else {
			return false;
		}
	}
}
