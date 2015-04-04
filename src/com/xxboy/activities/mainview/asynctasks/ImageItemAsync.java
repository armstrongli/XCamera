package com.xxboy.activities.mainview.asynctasks;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.xxboy.async.XBitmapAsyncTask;
import com.xxboy.drawables.XBitmapDrawable;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;

public class ImageItemAsync extends XBitmapAsyncTask {

	public ImageItemAsync(String imagePath, ImageView imageView) {
		super(imagePath, imageView);
	}

	private Bitmap getImageItem() {
		return BitmapFactory.decodeFile(super.getImagePath(), XBitmapUtil.getImageItemOption(super.getImagePath()));
	}

	@Override
	protected Bitmap doInBackground() {
		if (this.isCancelled()) {
			return null;
		}
		Bitmap bitmap = XCacheUtil.getFromCache(super.getImagePath());
		if (bitmap != null) {
			return bitmap;
		} else {
			return XCacheUtil.pushToCache(super.getImagePath(), getImageItem());
		}
	}

	@Override
	protected void postExecute(Bitmap bitmap) {
		if (isCancelled()) {
			return;
		}

		WeakReference<ImageView> imageViewReference = super.getWeakImageView();
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final XBitmapAsyncTask bitmapWorkerTask = XBitmapDrawable.getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

}
