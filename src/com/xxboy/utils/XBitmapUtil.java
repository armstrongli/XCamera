package com.xxboy.utils;

import android.graphics.BitmapFactory;

import com.xxboy.activities.mainview.XCamera.XCameraConst;

public class XBitmapUtil {

	public static BitmapFactory.Options getImageItemOption(String resourcePath) {
		return getOptionsInCalculate4ImageItem(resourcePath);
	}

	public static BitmapFactory.Options getImageViewOption(String resourcePath) {
		return getOptionsInCalculate4ImageView(resourcePath);
	}

	private static BitmapFactory.Options getOptionsInCalculate4ImageItem(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(resourcePath, opt);

		float width_divide_height = (float) opt.outWidth / (float) opt.outHeight;
		double cal_width = 0, cal_height = 0;
		if (width_divide_height > XCameraConst.ITEM_WIDTH_DIVIDE_HEIGHT) {
			if (opt.outWidth > XCameraConst.PHOTO_ITEM_WIDTH) {
				cal_width = XCameraConst.PHOTO_ITEM_WIDTH;
				cal_height = cal_width / width_divide_height;
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = (int) (opt.outHeight / cal_height);
		} else {
			if (opt.outHeight > XCameraConst.PHOTO_ITEM_HEIGHT) {
				cal_height = XCameraConst.PHOTO_ITEM_HEIGHT;
				cal_width = cal_height * width_divide_height;
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

	private static BitmapFactory.Options getOptionsInCalculate4ImageView(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(resourcePath, opt);

		float width_divide_height = (float) opt.outWidth / (float) opt.outHeight;
		double cal_width = 0, cal_height = 0;
		if (width_divide_height > XCameraConst.VIEW_WIDTH_DIVIDE_HEIGHT) {
			if (opt.outWidth > XCameraConst.SCREEN_WIDTH) {
				cal_width = XCameraConst.SCREEN_WIDTH;
				cal_height = cal_width / width_divide_height;
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = (int) (opt.outHeight / cal_height);
		} else {
			if (opt.outHeight > XCameraConst.SCREEN_HEIGHT) {
				cal_height = XCameraConst.SCREEN_HEIGHT;
				cal_width = cal_height * width_divide_height;
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
