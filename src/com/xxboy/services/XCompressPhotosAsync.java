package com.xxboy.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;

/**
 * xcamera folder
 * 
 * @author Armstrong
 * 
 */
public class XCompressPhotosAsync extends AsyncTask<Void, Void, Integer> {

	public static class XPhotoParam {
		private String xPath;
		private String xCachePath;

		public String getxPath() {
			return xPath;
		}

		public void setxPath(String xPath) {
			this.xPath = xPath;
		}

		public String getxCachePath() {
			return xCachePath;
		}

		public void setxCachePath(String xCachePath) {
			this.xCachePath = xCachePath;
		}

	}

	private XPhotoParam param;

	public XCompressPhotosAsync(XPhotoParam param) {
		super();
		this.param = param;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		String xCameraPath = this.param.getxPath();
		String xCachePath = this.param.getxCachePath();

		Integer count = 0;
		File cameraFolder = new File(xCameraPath);
		if (!cameraFolder.exists()) {
			return count;
		}

		File[] xFolders = cameraFolder.listFiles();
		for (File xFolder : xFolders) {
			if (!xFolder.isDirectory() || xFolder.isHidden()) {
				continue;
			}
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
					Bitmap compressed = XFunction.XCompress.comp(BitmapFactory.decodeFile(picture.getAbsolutePath()));

					try {
						File file = new File(picture.getAbsolutePath().replaceAll(xCameraPath, xCachePath));
						FileOutputStream out = new FileOutputStream(file);
						compressed.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.flush();
						out.close();
					} catch (FileNotFoundException e) {
						Logger.log(e);
					} catch (IOException e) {
						Logger.log(e);
					}
				}
			}
		}
		return null;
	}
}
