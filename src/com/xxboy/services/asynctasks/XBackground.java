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
		this.bitmap = Bitmap.createScaledBitmap(this.bitmap, XCamera.XCameraConst.SCREEN_WIDTH, XCamera.XCameraConst.SCREEN_HEIGHT, true);
		this.main.getHandler().post(new Runnable() {
			@Override
			public void run() {
				main.setBackgroundDrawable(new BitmapDrawable(BlurProcess.build().blur(bitmap, 80)));
			}
		});
		return null;
	}

}
