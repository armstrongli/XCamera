package com.xxboy.listeners;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

public class CallCameraListener implements OnClickListener {
	private Activity activity;
	private Camera camera;

	public CallCameraListener(Activity activity) {
		this.activity = activity;
	}

	public CallCameraListener(Activity activity, Camera camera) {
		this.activity = activity;
		this.camera = camera;
	}

	@Override
	public void onClick(View v) {
		this.camera.stopPreview();

		Intent intent = new Intent();
		intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

		// return to XCamera after take photos
		this.activity.startActivityForResult(intent, 1);
	}
}