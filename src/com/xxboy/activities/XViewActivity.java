package com.xxboy.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.xxboy.listeners.XImageViewListener;
import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera_imageview);
		String path = getIntent().getStringExtra(XImageViewListener.VAR_PATH);
		ImageView imageview = (ImageView) findViewById(R.id.xcamera_imageview);
		Bitmap photo = BitmapFactory.decodeFile(path, getOptionsInCalculate(path));
		imageview.setImageBitmap(photo);
	}

	private BitmapFactory.Options getOptionsInCalculate(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(resourcePath, opt);
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
			opt.inSampleSize = opt.outHeight / cal_height;
		} else {
			if (opt.outHeight > XCameraConst.PHOTO_ITEM_HEIGHT) {
				cal_height = XCameraConst.PHOTO_ITEM_HEIGHT;
				cal_width = cal_height * (opt.outWidth / opt.outHeight);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = opt.outWidth / cal_width;
		}
		opt.outHeight = cal_height;
		opt.outWidth = cal_width;

		opt.inSampleSize = opt.inSampleSize;

		opt.inJustDecodeBounds = false;
		return opt;
	}
}
