package com.xxboy.services.asynctasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.xxboy.log.Logger;
import com.xxboy.xcamera.XCamera;

public class XToast extends AsyncTask<Void, Void, Void> {
	private XCamera xCamera;
	private String toastMessage;

	public XToast(XCamera xCamera, String toastMessage) {
		super();
		this.xCamera = xCamera;
		this.toastMessage = toastMessage;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			this.xCamera.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(xCamera, toastMessage, Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
		return null;
	}

}
