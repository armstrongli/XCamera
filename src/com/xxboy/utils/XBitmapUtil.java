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
		opt.inSampleSize = calculateInSampleSize(opt, XCameraConst.PHOTO_ITEM_WIDTH, XCameraConst.PHOTO_ITEM_HEIGHT);

		opt.inJustDecodeBounds = false;
		return opt;
	}

	private static BitmapFactory.Options getOptionsInCalculate4ImageView(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(resourcePath, opt);
		opt.inSampleSize = calculateInSampleSize(opt, XCameraConst.SCREEN_WIDTH, XCameraConst.SCREEN_HEIGHT);

		opt.inJustDecodeBounds = false;
		return opt;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while (halfHeight > inSampleSize * reqHeight || halfWidth > inSampleSize * reqWidth) {
				inSampleSize <<= 1;
			}
		}

		return inSampleSize;
	}

}
