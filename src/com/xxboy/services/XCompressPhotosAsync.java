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
		Logger.log("Compress photos begin");
		File cameraFolder = new File(xCameraPath);// XCamera/
		if (!cameraFolder.exists()) {
			cameraFolder.mkdirs();
			Logger.log("Compress photos: create camera folder and break compress files.");
			return count;
		}

		Logger.log("Compress photos: begin checking date folders");
		File[] xFolders = cameraFolder.listFiles();// XCamera/YYYY.MM[]
		if (xFolders == null || xFolders.length == 0) {
			Logger.log("Compress photos: There're no XCamera date folders in path.");
			return count;
		}

		for (File xFolder : xFolders) {
			if (!xFolder.isDirectory() || xFolder.isHidden()) {
				continue;
			}
			Logger.log("Compress photos: begin checking date detail folders");
			File[] pictureDates = xFolder.listFiles();// XCamera/YYYY.MM/YYYY.MM.DD[]
			if (pictureDates == null || pictureDates.length == 0) {
				Logger.log("Compress photos: no date files in date detail folders");
				continue;
			}

			for (File pictureFolder : pictureDates) {
				File[] pictures = pictureFolder.listFiles();
				if (pictures == null || pictures.length == 0) {
					Logger.log("Compress photos: no files in [" + pictureFolder.getAbsolutePath() + "]");
					continue;
				}
				for (File picture : pictures) {
					Logger.log("Compress photos: compressing file [" + picture.getAbsolutePath() + "]");
					if (picture.isDirectory()) {
						continue;
					} else if (picture.isHidden()) {
						continue;
					} else {
						count++;
						Logger.log("Compressing file: " + picture.getAbsolutePath());
						Bitmap compressed = XFunction.XCompress.comp(BitmapFactory.decodeFile(picture.getAbsolutePath()));

						try {
							String cacheFileAbsolutePath = picture.getAbsolutePath().replaceAll(xCameraPath, xCachePath);
							Logger.log("Compressing file to cache file: " + cacheFileAbsolutePath);
							File file = new File(cacheFileAbsolutePath);
							File cacheFolder = new File(file.getParent());
							if (!cacheFolder.exists()) {
								Logger.log("Creating cache dirs: " + cacheFolder.getAbsolutePath());
								cacheFolder.mkdirs();
							}
							if (!file.exists()) {
								Logger.log("creating cache File: " + file.getAbsolutePath());
								file.createNewFile();
							}
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
		}
		return null;
	}
}
