package com.xxboy.listeners;

import android.content.Intent;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

import com.xxboy.xcamera.XCamera;

public class CallCameraListener implements OnClickListener {
	private XCamera activity;
	private Camera camera;

	public CallCameraListener(XCamera context) {
		this.activity = context;
	}

	public CallCameraListener(XCamera context, Camera camera) {
		this.activity = context;
		this.camera = camera;
	}

	@Override
	public void onClick(View v) {
		if (this.camera != null) {
			this.camera.stopPreview();
			this.camera.release();
		}

		Intent intent = new Intent();
		intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

		// return to XCamera after take photos
		this.activity.startActivityForResult(intent, 1);
	}
}