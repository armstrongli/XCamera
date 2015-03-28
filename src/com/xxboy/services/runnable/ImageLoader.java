package com.xxboy.services.runnable;

import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.services.pool.RunnablePool;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class ImageLoader implements Runnable {

	private int position;

	private ImageView imageView;

	private String imagePath;

	public ImageLoader(int position, String imagePath, ImageView imageView) {
		super();
		this.position = position;
		this.imageView = imageView;
		this.imagePath = imagePath;
	}

	@Override
	public void run() {
		Logger.log("Setting images: " + this.position + "=" + this.imagePath);
		if (Thread.interrupted()) {
			Logger.log("Setting images: Thread interrupted " + this.position + "=" + this.position);
			return;
		}

		this.imageView.setImageBitmap(XCacheUtil.getFromMemCache(this.imagePath));

		RunnablePool.removeRunningImageLoader(this);
		XQueueUtil.execRemoveFromRunnablePoolAfterSetImages(this);
	}

	public String getImagePath() {
		return imagePath;
	}

	public int getPosition() {
		return position;
	}

}
