package com.xxboy.services.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.services.runnable.ImageLoader;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class XLoadImageViewAsyncTask extends AsyncTask<Void, Void, Void> {

	private String path;
	private ImageView imageView;

	public XLoadImageViewAsyncTask(String path, ImageView imageView) {
		this.path = path;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Bitmap bitmap = XCacheUtil.getImaveView(this.path);
		if (bitmap != null && (bitmap.getWidth() + bitmap.getHeight() > 0)) {
			XQueueUtil.executeTask(new ImageLoader(this.path, imageView, bitmap));
		} else {
			bitmap = getImage(this.path);
			XQueueUtil.executeTask(new ImageLoader(this.path, imageView, bitmap));
			XCacheUtil.pushImageView(this.path, bitmap);
		}
		return null;
	}

	private Bitmap getImage(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageViewOption(imagePath));
	}

}
