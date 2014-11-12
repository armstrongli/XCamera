package com.xxboy.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.xxboy.xcamera.XCamera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public final class XFunction {
	/**
	 * calculate and zoom pic.
	 * 
	 * @author Armstrong
	 * 
	 */
	public static final class XCompress {
		public static final CompressFormat JPEG = Bitmap.CompressFormat.JPEG;

		private final static Bitmap compressImage(Bitmap image) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(JPEG, 100, baos);
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) {
				baos.reset();
				image.compress(JPEG, options, baos);
				options -= 10;
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
			return bitmap;
		}

		/**
		 * compress the image less than 1MB, then set the size
		 * 
		 * @param image
		 * @return
		 */
		public static final Bitmap comp(Bitmap image) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(JPEG, 100, baos);
			// compress it until it's less than 1MB
			while (baos.toByteArray().length / 1024 > 1024) {
				baos.reset();
				image.compress(JPEG, 50, baos);
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			int picWidth = newOpts.outWidth;
			int picHeight = newOpts.outHeight;
			// set the with and height as the screen size.
			float screenHeight = XCamera.XCameraConst.SCREEN_HEIGHT;
			float screenWidth = XCamera.XCameraConst.SCREEN_WIDTH;
			int zoomPercentage = 1;
			if (picWidth > picHeight && picWidth > screenWidth) {
				zoomPercentage = (int) (picWidth / screenWidth);
			} else if (picWidth < picHeight && picHeight > screenHeight) {
				zoomPercentage = (int) (picHeight / screenHeight);
			}
			if (zoomPercentage <= 0)
				zoomPercentage = 1;
			newOpts.inSampleSize = zoomPercentage;
			newOpts.inJustDecodeBounds = false;
			isBm = new ByteArrayInputStream(baos.toByteArray());
			bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			return compressImage(bitmap);
		}
	}

	public static final class XDate {
		public static final String YEAR_FORMAT = "yyyy";
		public static final String MONTH_FORMAT = "MM";
		public static final String DAY_FORMAT = "dd";

		private String year = null;
		private String month = null;
		private String day = null;

		private static SimpleDateFormat YEAR_SDF = new SimpleDateFormat(YEAR_FORMAT, Locale.getDefault());
		private static SimpleDateFormat MONTH_SDF = new SimpleDateFormat(MONTH_FORMAT, Locale.getDefault());
		private static SimpleDateFormat DAY_SDF = new SimpleDateFormat(DAY_FORMAT, Locale.getDefault());

		private Date date;

		public XDate() {
			this.date = new Date();
		}

		public String getYear() {
			return year != null ? year : (year = YEAR_SDF.format(this.date));
		}

		public String getMonth() {
			return month != null ? month : (month = MONTH_SDF.format(this.date));
		}

		public String getDay() {
			return day != null ? day : (day = DAY_SDF.format(this.date));
		}

	}

}