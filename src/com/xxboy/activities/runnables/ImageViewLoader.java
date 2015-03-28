package com.xxboy.activities.runnables;

import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.services.pool.RunnablePool;
import com.xxboy.utils.XCacheUtil;

public class ImageViewLoader implements Runnable {

	private ImageView imageView;

	private String imagePath;

	public ImageViewLoader(String imagePath, ImageView imageView) {
		super();
		this.imageView = imageView;
		this.imagePath = imagePath;
	}

	@Override
	public void run() {
		if (Thread.interrupted()) {
			Logger.log("Setting images: Thread interrupted " + this.imagePath);
			return;
		}

		this.imageView.setImageBitmap(XCacheUtil.getImaveView(this.imagePath));
		if (Thread.interrupted()) {
			Logger.log("Setting images: Thread interrupted " + this.imagePath);
			return;
		}

		// remove runnable from runnable pool
		RunnablePool.removeRunningImageLoader(this);
	}

	public String getImagePath() {
		return imagePath;
	}

}
