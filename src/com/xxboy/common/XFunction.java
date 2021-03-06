package com.xxboy.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.xxboy.activities.mainview.XCamera;
import com.xxboy.log.Logger;

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
			// compress it until it's less than 50k
			int coLength = Integer.MAX_VALUE;
			int count = 0;
			while (coLength > 40 * 1024 && count <= 10) {
				baos.reset();
				image.compress(JPEG, 50, baos);
				count++;
				coLength = baos.toByteArray().length;
				Logger.log("Compress length > " + coLength * 8);
			}
			Logger.log("Compress count: " + count);

			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			int picWidth = newOpts.outWidth;
			int picHeight = newOpts.outHeight;
			// set the with and height as the screen size.
			float cachedThumbHeight = XCamera.XCameraConst.SCREEN_HEIGHT / 3;
			float cachedThumbWidth = XCamera.XCameraConst.SCREEN_WIDTH / 3;
			int zoomPercentage = 1;
			if (picWidth > picHeight && picWidth > cachedThumbWidth) {
				zoomPercentage = (int) (picWidth / cachedThumbWidth);
			} else if (picWidth < picHeight && picHeight > cachedThumbHeight) {
				zoomPercentage = (int) (picHeight / cachedThumbHeight);
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
		private static final String FULL_FORMAT = "yyyy-MM-dd hh:mm:ss SSS";

		private String year = null;
		private String month = null;
		private String day = null;

		private static SimpleDateFormat FULL_SDF = new SimpleDateFormat(FULL_FORMAT, Locale.getDefault());
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

		public String getFull() {
			return FULL_SDF.format(this.date);
		}
	}

	public static final String getMonthTranslation(String monthNum) {
		if ("01".equals(monthNum)) {
			return "Jan";
		} else if ("02".equals(monthNum)) {
			return "Feb";
		} else if ("03".equals(monthNum)) {
			return "Mar";
		} else if ("04".equals(monthNum)) {
			return "Apr";
		} else if ("05".equals(monthNum)) {
			return "May";
		} else if ("06".equals(monthNum)) {
			return "Jun";
		} else if ("07".equals(monthNum)) {
			return "Jul";
		} else if ("08".equals(monthNum)) {
			return "Aug";
		} else if ("09".equals(monthNum)) {
			return "Sep";
		} else if ("10".equals(monthNum)) {
			return "Oct";
		} else if ("11".equals(monthNum)) {
			return "Nov";
		} else if ("12".equals(monthNum)) {
			return "Dec";
		} else {
			return monthNum;
		}
	}

	/**
	 * remove folder(including sub-files)
	 * 
	 * @param path
	 */
	public static final void removeFolder(File path) {
		if (path.exists()) {
			if (path.isFile()) {
				path.delete();
			} else {
				File[] subFiles = path.listFiles();
				for (File subFile : subFiles) {
					removeFolder(subFile);
				}
				path.delete();
			}
		}
	}

	private static final String[] okFileExtensions = new String[] { "jpg", "png", "gif", "jpeg" };

	public static final Boolean isImage(String fileFullPath) {
		for (String item : okFileExtensions) {
			if (fileFullPath.toLowerCase().endsWith(item)) {
				return true;
			}
		}
		return false;
	}

	public static final String getFileName(String filePath) {
		if (filePath == null || filePath.trim().length() == 0) {
			return "";
		} else {
			String[] pathes = filePath.split("\\\\|\\/");
			return pathes[pathes.length - 1];
		}
	}
}
