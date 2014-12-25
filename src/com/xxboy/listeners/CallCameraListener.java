package com.xxboy.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

public class CallCameraListener implements OnClickListener {
	private Context activity;
	private Camera camera;

	private CallCameraListener(Context activity) {
		this.activity = activity;
	}

	public CallCameraListener(Context context, Camera camera) {
		this.activity = context;
		this.camera = camera;
	}

	@Override
	public void onClick(View v) {
		this.camera.stopPreview();
		this.camera.release();

		Intent intent = new Intent();
		intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

		// return to XCamera after take photos
		((Activity) this.activity).startActivityForResult(intent, 1);
	}
}