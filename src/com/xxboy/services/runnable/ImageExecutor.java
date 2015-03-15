package com.xxboy.services.runnable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class ImageExecutor extends Thread {

	private int position;
	private String imagePath;
	private Bitmap bitmap;
	private ImageView imageView;

	public ImageExecutor(int position, String imagePath, ImageView imageView) {
		super();
		this.position = position;
		this.imagePath = imagePath;
		this.imageView = imageView;
	}

	@Override
	public void run() {
		try {
			this.bitmap = loadBitmapFromFile(this.imagePath);
			XQueueUtil.execAddImage(this.imagePath, new ImageLoader(this.imagePath, this.imageView, bitmap));
		} finally {
			XCacheUtil.pushToCache(this.imagePath, this.bitmap);
		}
	}

	/**
	 * load file bitmap from IMAGE file, currently.
	 * 
	 * @param imagePath
	 * @return
	 */
	private static Bitmap loadBitmapFromFile(String imagePath) {
		return BitmapFactory.decodeFile(imagePath, XBitmapUtil.getImageItemOption(imagePath));// the slowest one, from file to decode.
	}
}
