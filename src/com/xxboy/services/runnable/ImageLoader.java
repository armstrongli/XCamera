package com.xxboy.services.runnable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.utils.XQueueUtil;

public class ImageLoader implements Runnable {

	private ImageView imageView;

	private Bitmap varBitmap;

	private String imagePath;

	public ImageLoader(String imagePath, ImageView imageView, Bitmap varBitmap) {
		super();
		this.imageView = imageView;
		this.varBitmap = varBitmap;
	}

	@Override
	public void run() {
		if (Thread.interrupted()) {
			Logger.log("Thread interrupted");
			return;
		} else {
			this.imageView.setImageBitmap(this.varBitmap);
			XQueueUtil.execRemoveFromRunnablePoolAfterSetImages(this.imagePath, this);
		}
	}

}
