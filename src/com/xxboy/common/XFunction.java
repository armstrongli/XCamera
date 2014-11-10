package com.xxboy.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public final class XFunction {
	public static final class XCompress {
		public static final CompressFormat JPEG = Bitmap.CompressFormat.JPEG;

		public final static Bitmap compressImage(Bitmap image) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			return bitmap;
		}

		public final static Bitmap getimage(String srcPath) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
			float hh = 800f;// 这里设置高度为800f
			float ww = 480f;// 这里设置宽度为480f
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
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
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
			float hh = 800f;// 这里设置高度为800f
			float ww = 480f;// 这里设置宽度为480f
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			isBm = new ByteArrayInputStream(baos.toByteArray());
			bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
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
