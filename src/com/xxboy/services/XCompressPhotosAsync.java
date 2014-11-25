package com.xxboy.services;

import java.io.File;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.xxboy.common.XFunction;

public class XCompressPhotosAsync extends AsyncTask<File, Void, Integer> {

	@Override
	protected Integer doInBackground(File... params) {
		if (params == null || params.length == 0) {
			return 0;
		}
		Integer count = 0;
		for (File xFolder : params) {
			File[] pictures = xFolder.listFiles();
			if (pictures == null || pictures.length == 0) {
				continue;
			}
			for (File picture : pictures) {
				if (picture.isDirectory()) {
					continue;
				} else if (picture.isHidden()) {
					continue;
				} else {
					count++;
					Bitmap compressed = XFunction.XCompress.comp(image);
				}
			}
		}
		return null;
	}

}
