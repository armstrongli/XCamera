package com.xxboy.services.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.services.runnable.ImageLoader;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera;

public class XBitmapCacheAsyncTask extends AsyncTask<Void, Void, Void> {
	private int position;
	private String resourcePath;
	private ImageView imageView;

	private Bitmap varBitmap;

	public XBitmapCacheAsyncTask(Integer position, String resourcePath, ImageView imageView) {
		super();
		this.position = position;
		this.resourcePath = resourcePath;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (!XQueueUtil.checkInMask(this.position)) {
			return null;
		}
		try {
			this.varBitmap = XCacheUtil.getFromCache(this.resourcePath);// get from soft reference cache or disk cache, the 2nd fast.
			if (this.varBitmap == null) {
				this.varBitmap = BitmapFactory.decodeFile(this.resourcePath, XBitmapUtil.getOptionsInCalculate(this.resourcePath));// the slowest one, from file to decode.
			}

			if (this.varBitmap != null) {
				this.varBitmap = cutPicture(this.varBitmap);
				XQueueUtil.addTasks(this.position, new ImageLoader(imageView, varBitmap));
				XCacheUtil.pushToCache(this.resourcePath, this.varBitmap);
			} else {
				Logger.log("File can't be decoded: " + this.resourcePath);
			}
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * cut picture to the photo item proportion
	 * 
	 * @param original
	 * @return
	 */
	private Bitmap cutPicture(final Bitmap original) {
		if (original.getWidth() == 0 || original.getHeight() == 0) {
			return original;
		}
		if (original.getWidth() / original.getHeight() > XCamera.XCameraConst.PHOTO_ITEM_WIDTH / XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
			if (original.getWidth() <= XCamera.XCameraConst.PHOTO_ITEM_WIDTH) {
				return original;
			} else {
				if (original.getHeight() < XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
					return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT, original.getHeight());
				} else {
					return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_WIDTH, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
				}
			}
		} else {
			if (original.getHeight() <= XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
				return original;
			} else {
				if (original.getWidth() < XCamera.XCameraConst.PHOTO_ITEM_WIDTH) {
					return Bitmap.createBitmap(original, 0, 0, original.getWidth(), XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
				} else {
					return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_WIDTH, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
				}
			}
		}
	}
}
