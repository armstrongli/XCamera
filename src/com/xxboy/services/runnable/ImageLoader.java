package com.xxboy.services.runnable;

import com.xxboy.log.Logger;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageLoader implements Runnable {

	private ImageView imageView;

	private Bitmap varBitmap;

	public ImageLoader(ImageView imageView, Bitmap varBitmap) {
		super();
		this.imageView = imageView;
		this.varBitmap = varBitmap;
	}

	@Override
	public void run() {
		if (Thread.interrupted()) {
			Logger.log("Thread interrupted");
			return;
		}
		this.imageView.setImageBitmap(this.varBitmap);
	}

}
