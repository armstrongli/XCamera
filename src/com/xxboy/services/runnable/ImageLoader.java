package com.xxboy.services.runnable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.utils.XQueueUtil;

public class ImageLoader implements Runnable {

	private int position;

	private ImageView imageView;

	private Bitmap varBitmap;

	private String imagePath;

	public ImageLoader(int position, String imagePath, ImageView imageView, Bitmap varBitmap) {
		super();
		this.position = position;
		this.imageView = imageView;
		this.varBitmap = varBitmap;
		this.imagePath = imagePath;
	}

	@Override
	public void run() {
		if (Thread.interrupted()) {
			Logger.log("Thread interrupted");
			return;
		} else {
			this.imageView.setImageBitmap(this.varBitmap);
			XQueueUtil.execRemoveFromRunnablePoolAfterSetImages(this);
		}
	}

	public String getImagePath() {
		return imagePath;
	}

	public int getPosition() {
		return position;
	}

}
