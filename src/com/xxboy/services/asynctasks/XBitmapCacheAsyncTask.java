package com.xxboy.services.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.common.XCache;
import com.xxboy.log.Logger;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XBitmapCacheAsyncTask extends AsyncTask<Void, Void, Void> {

	private String resourcePath;
	private ImageView imageView;
	private XCamera xCamera;

	public XBitmapCacheAsyncTask(String resourcePath, ImageView imageView, XCamera xCamera) {
		super();
		this.resourcePath = resourcePath;
		this.imageView = imageView;
		this.xCamera = xCamera;
	}

	@Override
	protected Void doInBackground(Void... params) {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(this.resourcePath, opt);
		float width_divide_height = opt.outHeight / opt.outWidth;
		int cal_width = 0, cal_height = 0;
		if (width_divide_height > XCameraConst.WIDTH_DIVIDE_HEIGHT) {
			if (opt.outWidth > XCameraConst.SCREEN_WIDTH / 13) {
				cal_width = XCameraConst.SCREEN_WIDTH / 13;
				cal_height = cal_width * (opt.outHeight / opt.outWidth);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
		} else {
			if (opt.outHeight > XCameraConst.SCREEN_HEIGHT / 13) {
				cal_height = XCameraConst.SCREEN_HEIGHT / 13;
				cal_width = cal_height * (opt.outWidth / opt.outHeight);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
		}
		opt.outHeight = cal_height;
		opt.outWidth = cal_width;

		Logger.log("cal_height: " + cal_height + "==cal_width:" + cal_width);
		opt.inJustDecodeBounds = false;

		try {
			final Bitmap resizedBitmap = BitmapFactory.decodeFile(this.resourcePath, opt);
			Logger.log("Bitmap size:" + resizedBitmap.getByteCount());
			XCache.push2MemCache(this.resourcePath, resizedBitmap);
			xCamera.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					imageView.setImageBitmap(resizedBitmap);
				}
			});
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}

		return null;
	}

}
