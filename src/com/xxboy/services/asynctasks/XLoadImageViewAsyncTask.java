package com.xxboy.services.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.services.runnable.ImageLoader;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera.XCameraConst;

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
			XQueueUtil.executeTask(new ImageLoader(imageView, bitmap));
		} else {
			bitmap = getImage(this.path);
			XQueueUtil.executeTask(new ImageLoader(imageView, bitmap));
			XCacheUtil.pushImageView(this.path, bitmap);
		}
		return null;
	}

	private Bitmap getImage(String imagePath) {
		return BitmapFactory.decodeFile(this.path, getOptionsInCalculate(imagePath));
	}

	private BitmapFactory.Options getOptionsInCalculate(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(resourcePath, opt);
		float width_divide_height = opt.outHeight / opt.outWidth;
		double cal_width = 0, cal_height = 0;
		if (width_divide_height > XCameraConst.WIDTH_DIVIDE_HEIGHT) {
			if (opt.outWidth > XCameraConst.SCREEN_WIDTH) {
				cal_width = XCameraConst.SCREEN_WIDTH;
				cal_height = cal_width * (opt.outHeight / opt.outWidth);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = (int) (opt.outHeight / cal_height);
		} else {
			if (opt.outHeight > XCameraConst.SCREEN_HEIGHT) {
				cal_height = XCameraConst.SCREEN_HEIGHT;
				cal_width = cal_height * (opt.outWidth / opt.outHeight);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = (int) (opt.outWidth / cal_width);
		}
		opt.outHeight = (int) (cal_height);
		opt.outWidth = (int) (cal_width);

		opt.inSampleSize = opt.inSampleSize;

		opt.inJustDecodeBounds = false;
		return opt;
	}
}
