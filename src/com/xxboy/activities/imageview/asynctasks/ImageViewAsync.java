package com.xxboy.activities.imageview.asynctasks;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xxboy.async.XBitmapAsyncTask;
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
		super.postExecute(result);

		// load full image after loading image from cache
		WeakReference<ImageView> wImageView = super.getWeakImageView();
		if (wImageView != null) {
			new FullImageViewAsync(getImagePath(), wImageView.get()).execute();
		}
	}

}
