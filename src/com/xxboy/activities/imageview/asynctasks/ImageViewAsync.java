package com.xxboy.activities.imageview.asynctasks;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;

public class ImageViewAsync extends AsyncTask<Void, Void, Bitmap> {

	private String path;
	private final WeakReference<ImageView> imageViewReference;

	public ImageViewAsync(String path, ImageView imageView) {
		this.path = path;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap = XCacheUtil.getImaveView(this.path);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		}
		bitmap = getImage(this.path);
		XCacheUtil.pushImageView(this.path, bitmap);
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (this.imageViewReference != null && result != null) {
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(result);
			}
		}
	}

	private Bitmap getImage(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageViewOption(imagePath));
	}

}
