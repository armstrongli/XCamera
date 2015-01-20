package com.xxboy.services.asynctasks;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.xxboy.utils.blur.BlurProcess;
import com.xxboy.xcamera.XCamera;

public class XBackground extends AsyncTask<Void, Void, Void> {

	private LinearLayout main;
	private Bitmap bitmap;

	public XBackground(LinearLayout main, Bitmap bitmap) {
		super();
		this.main = main;
		this.bitmap = bitmap;
	}

	@Override
	protected Void doInBackground(Void... params) {
		int width = this.bitmap.getWidth();
		int height = this.bitmap.getHeight();

		float scaledWidth = 0, scaledHeight = 0, scaled = 0;

		if (width < XCamera.XCameraConst.SCREEN_WIDTH) {
			scaledWidth = XCamera.XCameraConst.SCREEN_WIDTH / width;
		}
		if (height < XCamera.XCameraConst.SCREEN_HEIGHT) {
			scaledHeight = XCamera.XCameraConst.SCREEN_HEIGHT / height;
		}
		scaled = scaledWidth <= scaledHeight ? scaledHeight : scaledWidth;
		if (scaled > 1) {
			this.bitmap = Bitmap.createScaledBitmap(this.bitmap, XCamera.XCameraConst.SCREEN_WIDTH, XCamera.XCameraConst.SCREEN_HEIGHT, true);
		} else {
			this.bitmap = Bitmap.createBitmap(this.bitmap, 0, 0, XCamera.XCameraConst.SCREEN_WIDTH, XCamera.XCameraConst.SCREEN_HEIGHT);
		}
		this.main.getHandler().post(new Runnable() {
			@Override
			public void run() {
				main.setBackgroundDrawable(new BitmapDrawable(BlurProcess.build().blur(bitmap, 80)));
			}
		});
		return null;
	}

}
