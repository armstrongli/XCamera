package com.xxboy.services.asynctasks;

import java.util.LinkedList;
import java.util.List;

import android.hardware.Camera;
import android.os.AsyncTask;

import com.xxboy.log.Logger;

public final class XCameraAsyncTask extends AsyncTask<Void, Void, Void> {
	private static final List<Camera> cameras = new LinkedList<Camera>();
	private static final int cameraAmount = Camera.getNumberOfCameras();

	/**
	 * get all cameras from system resources.<br/>
	 * <b>DON'T DO ANYTHING WITH THIS RESOURCE</b>
	 * 
	 * @return
	 */
	public static final List<Camera> getCameras() {
		if (cameraAmount != cameras.size()) {
			new XCameraAsyncTask().execute();
		}
		return XCameraAsyncTask.cameras;
	}

	public static final void releaseCameras() {
		for (Camera cameraItem : cameras) {
			try {
				cameraItem.release();
			} catch (Exception e) {
				Logger.log("Error when releasing camera resource: " + e.getMessage(), e);
			}
		}
		cameras.clear();
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (int i = 0; i < cameraAmount; i++) {
			try {
				Camera cameraItem = Camera.open(i);
				cameras.add(cameraItem);
			} catch (Exception e) {
				Logger.log("Error when getting cameras from system resources: " + e.getMessage(), e);
			}
		}
		return null;
	}
}
