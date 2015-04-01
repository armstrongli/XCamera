package com.xxboy.activities.mainview.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.activities.mainview.runnables.ImageLoader;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class ImageItemAsync extends AsyncTask<Void, Void, Void> {
	private String path;
	private ImageView imageView;

	public ImageItemAsync(String path, ImageView imageView) {
		this.path = path;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Bitmap bitmap = XCacheUtil.getFromMemCache(path);
		if (bitmap != null && !bitmap.isRecycled() && (bitmap.getWidth() + bitmap.getHeight() > 0)) {
			XQueueUtil.executeTaskDirectly(new ImageLoader(0, this.path, this.imageView));
		} else {
			bitmap = getImage(this.path);
			XQueueUtil.executeTaskDirectly(new ImageLoader(0, this.path, this.imageView));
			XCacheUtil.pushToCache(this.path, bitmap);
		}
		return null;
	}

	private Bitmap getImage(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageItemOption(imagePath));
	}
}
