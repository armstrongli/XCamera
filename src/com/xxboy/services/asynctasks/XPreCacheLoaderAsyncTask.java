package com.xxboy.services.asynctasks;

import java.io.File;

import com.xxboy.common.XCache;
import com.xxboy.xcamera.XCamera.XCameraConst;

import android.os.AsyncTask;

public class XPreCacheLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... params) {
		File xCameraFolder = new File(XCameraConst.GLOBAL_X_CAMERA_PATH);
		if (xCameraFolder == null || !xCameraFolder.exists()) {
			return null;
		}
		File[] yyyymmFolders = xCameraFolder.listFiles();
		if (yyyymmFolders == null || yyyymmFolders.length == 0) {
			return null;
		}
		int cashedCount = 20;
		for (int i = yyyymmFolders.length - 1; i >= 0; i--) {
			if (cashedCount <= 0) {
				break;
			}
			File[] yyyymmddFolders = yyyymmFolders[i].listFiles();
			if (yyyymmddFolders == null || yyyymmddFolders.length == 0) {
				continue;
			}
			if (cashedCount <= 0) {
				break;
			}
			for (int j = yyyymmddFolders.length - 1; j >= 0; j--) {
				File[] pictures = yyyymmddFolders[j].listFiles();
				if (pictures == null || pictures.length == 0) {
					continue;
				}
				if (cashedCount <= 0) {
					break;
				}
				for (int k = pictures.length - 1; k >= 0; k--) {
					XCache.getFromCache(pictures[k].getAbsolutePath());
					cashedCount--;
					if (cashedCount <= 0) {
						break;
					}
				}
			}
		}
		return null;
	}

}
