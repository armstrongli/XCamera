package com.xxboy.utils;

import android.graphics.BitmapFactory;

import com.xxboy.xcamera.XCamera.XCameraConst;

public class XBitmapUtil {

	public static BitmapFactory.Options getOptionsInCalculate(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(resourcePath, opt);
		float width_divide_height = opt.outWidth / opt.outHeight;
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
