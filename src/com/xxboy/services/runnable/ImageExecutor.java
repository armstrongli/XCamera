package com.xxboy.services.runnable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;
import com.xxboy.xcamera.XCamera;

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
			this.bitmap = cutPicture(loadBitmapFromFile(this.imagePath));
			XQueueUtil.execAddImage(new ImageLoader(this.position, this.imagePath, this.imageView, bitmap));
		} finally {
			XCacheUtil.pushToCache(this.imagePath, this.bitmap);
		}
	}

	/**
	 * cut picture to the photo item proportion
	 * 
	 * @param original
	 * @return
	 */
	private static Bitmap cutPicture(final Bitmap original) {
		if (original.getWidth() == 0 || original.getHeight() == 0) {
			return original;
		}
		if (original.getWidth() / original.getHeight() > XCamera.XCameraConst.PHOTO_ITEM_WIDTH / XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
			if (original.getWidth() <= XCamera.XCameraConst.PHOTO_ITEM_WIDTH) {
				return original;
			} else {
				if (original.getHeight() < XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
					return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT, original.getHeight());
				} else {
					return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_WIDTH, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
				}
			}
		} else {
			if (original.getHeight() <= XCamera.XCameraConst.PHOTO_ITEM_HEIGHT) {
				return original;
			} else {
				if (original.getWidth() < XCamera.XCameraConst.PHOTO_ITEM_WIDTH) {
					return Bitmap.createBitmap(original, 0, 0, original.getWidth(), XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
				} else {
					return Bitmap.createBitmap(original, 0, 0, XCamera.XCameraConst.PHOTO_ITEM_WIDTH, XCamera.XCameraConst.PHOTO_ITEM_HEIGHT);
				}
			}
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
}
