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

	private Bitmap varBitmap;

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
			if (opt.outWidth > XCameraConst.PHOTO_ITEM_WIDTH) {
				cal_width = XCameraConst.PHOTO_ITEM_WIDTH;
				cal_height = cal_width * (opt.outHeight / opt.outWidth);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = opt.outWidth / cal_width;
		} else {
			if (opt.outHeight > XCameraConst.PHOTO_ITEM_HEIGHT) {
				cal_height = XCameraConst.PHOTO_ITEM_HEIGHT;
				cal_width = cal_height * (opt.outWidth / opt.outHeight);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = opt.outHeight / cal_height;
		}
		opt.outHeight = cal_height;
		opt.outWidth = cal_width;

		opt.inSampleSize = opt.inSampleSize;

		Logger.log("cal_height: " + cal_height + "==cal_width:" + cal_width + "==inSampleSize:" + opt.inSampleSize);
		opt.inJustDecodeBounds = false;

		try {
			this.varBitmap = XCache.getFromCache(this.resourcePath);
			if (this.varBitmap == null) {
				try {
					this.varBitmap = BitmapFactory.decodeFile(this.resourcePath, opt);
					Logger.log("Bitmap size:" + this.varBitmap.getByteCount());
					XCache.pushToCache(this.resourcePath, this.varBitmap);
				} catch (Exception e) {
					this.varBitmap = null;
				}
			}

			if (this.varBitmap != null) {
				xCamera.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						imageView.setImageBitmap(varBitmap);
					}
				});
			}
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}

		return null;
	}

}
