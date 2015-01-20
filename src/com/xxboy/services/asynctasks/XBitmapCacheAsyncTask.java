package com.xxboy.services.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.services.runnable.ImageLoader;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

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
				this.varBitmap = BitmapFactory.decodeFile(this.resourcePath, this.getOptionsInCalculate());// the slowest one, from file to decode.
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
		if (original.getWidth() / original.getHeight() > XCamera.XCameraConst.PHOTO_ITEM_WIDTH / XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
			if (original.getHeight() < XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
				double width = XCamera.XCameraConst.PHOTO_ITEM_WIDTH * original.getHeight() / XCamera.XCameraConst.PHOTO_ITEM_HEIGHT;
				return Bitmap.createBitmap(original, 0, 0, Double.valueOf(width).intValue(), original.getHeight());
			} else {
				return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_WIDTH, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
			}
		} else {
			if (original.getWidth() < XCamera.XCameraConst.PHOTO_ITEM_WIDTH) {
				double height = XCamera.XCameraConst.PHOTO_ITEM_HEIGHT * original.getWidth() / XCamera.XCameraConst.PHOTO_ITEM_WIDTH;
				return Bitmap.createBitmap(original, 0, 0, original.getWidth(), Double.valueOf(height).intValue());
			} else {
				return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_WIDTH, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
			}
		}
	}

	private BitmapFactory.Options getOptionsInCalculate() {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(this.resourcePath, opt);
		float width_divide_height = opt.outHeight / opt.outWidth;
		int cal_width = 0, cal_height = 0;
		if (width_divide_height > XCameraConst.WIDTH_DIVIDE_HEIGHT) {
			if (opt.outHeight > XCameraConst.PHOTO_ITEM_HEIGHT) {
				cal_height = XCameraConst.PHOTO_ITEM_HEIGHT;
				cal_width = cal_height * (opt.outWidth / opt.outHeight);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = opt.outHeight / cal_height;
		} else {
			if (opt.outWidth > XCameraConst.PHOTO_ITEM_WIDTH) {
				cal_width = XCameraConst.PHOTO_ITEM_WIDTH;
				cal_height = cal_width * (opt.outHeight / opt.outWidth);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = opt.outWidth / cal_width;
		}
		opt.outHeight = cal_height;
		opt.outWidth = cal_width;

		opt.inSampleSize = opt.inSampleSize;

		Logger.log("cal_height: " + cal_height + "==cal_width:" + cal_width + "==inSampleSize:" + opt.inSampleSize);
		opt.inJustDecodeBounds = false;
		return opt;
	}
}
