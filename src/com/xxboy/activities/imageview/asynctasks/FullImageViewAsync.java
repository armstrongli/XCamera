package com.xxboy.activities.imageview.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.xxboy.async.XBitmapAsyncTask;
import com.xxboy.log.Logger;

public class FullImageViewAsync extends XBitmapAsyncTask {

	public FullImageViewAsync(String path, ImageView imageView) {
		super(path, imageView);
	}

	@Override
	protected Bitmap doInBackground() {
		try {
			return getFullImageOptions(getImagePath());
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	protected void postExecute(Bitmap result) {
		super.postExecute(result);
	}

	private static final Bitmap getFullImageOptions(String path) {
		long maxMemSize = Runtime.getRuntime().maxMemory();
		long realMemSize = -1;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int with = options.outWidth, height = options.outHeight;
		realMemSize = with * height / 2;

		int sampleSize = 1;
		long sampleSizeSqrt = realMemSize / maxMemSize;
		if (sampleSizeSqrt > 0) {
			do {
				sampleSize <<= 1;
			} while (sampleSize * sampleSize < sampleSizeSqrt);
		}

		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeFile(path, options);
	}
}
