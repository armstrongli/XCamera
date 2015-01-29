package com.xxboy.listeners;

import com.xxboy.activities.XViewActivity;
import com.xxboy.xcamera.XCamera;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class XImageViewListener implements OnClickListener {
	private XCamera xCamera;
	private String photoPath;

	public XImageViewListener(XCamera xCamera, String photoPath) {
		super();
		this.xCamera = xCamera;
		this.photoPath = photoPath;
	}

	public static final String VAR_PATH = "VAR_PATH";

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this.xCamera, XViewActivity.class);
		intent.putExtra(VAR_PATH, this.photoPath);
		xCamera.startActivity(intent);
	}
}
