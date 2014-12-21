package com.xxboy.services;

import java.io.File;
import java.io.IOException;

import android.os.AsyncTask;

import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XInitial extends AsyncTask<Void, Void, Integer> {

	private XPhotoParam param;

	public XInitial(XPhotoParam param) {
		super();
		this.param = param;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		String xCachePath = this.param.getxCachePath();
		String xPath = this.param.getxPath();

		File cacheFolder = new File(xCachePath), xFolder = new File(xPath);
		Logger.log("checking xcamera cache folder: " + cacheFolder.getAbsolutePath());
		if (!cacheFolder.exists()) {
			Logger.log("creating xcamera cache folder: " + cacheFolder.getAbsolutePath());
			cacheFolder.mkdirs();
		}

		Logger.log("checking xcamera folder: " + xFolder.getAbsolutePath());
		if (!xFolder.exists()) {
			Logger.log("creating xcamera folder: " + xFolder.getAbsolutePath());
			xFolder.mkdirs();
		}

		File noMediaFlagFile = new File(cacheFolder.getAbsolutePath() + File.separator + ".nomedia");
		Logger.log("checking xcamera cache nomedia flag file: " + noMediaFlagFile.getAbsolutePath());
		if (!noMediaFlagFile.exists()) {
			try {
				Logger.log("creating xcamera cache nomedia flag file: " + noMediaFlagFile.getAbsolutePath());
				noMediaFlagFile.createNewFile();
			} catch (IOException e) {
				Logger.log(e);
				return R.string.INITIAL_FAIL;
			}
		}

		return R.string.INITIAL_SUCCESS;
	}

}
