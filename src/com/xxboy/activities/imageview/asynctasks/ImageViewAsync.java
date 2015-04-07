package com.xxboy.activities.imageview.asynctasks;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xxboy.async.XBitmapAsyncTask;
import com.xxboy.drawables.XBitmapDrawable;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;

public class ImageViewAsync extends XBitmapAsyncTask {

	public ImageViewAsync(String path, ImageView imageView) {
		super(path, imageView);
	}

	@Override
	protected Bitmap doInBackground() {
		Bitmap bitmap = XCacheUtil.getImaveView(super.getImagePath());
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		} else {
			bitmap = XBitmapUtil.getImageView(super.getImagePath());
			XCacheUtil.pushImageView(super.getImagePath(), bitmap);
			return bitmap;
		}
	}

	@Override
	protected void postExecute(Bitmap result) {
		WeakReference<ImageView> imageViewReference = super.getWeakImageView();
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

}
