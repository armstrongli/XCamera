package com.xxboy.services;

import java.io.File;

import android.os.AsyncTask;

import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XViewParam;

public final class XViewMovePhotos extends AsyncTask<XViewParam, Void, Integer> {
	private XViewParam param;

	public XViewMovePhotos(XViewParam param) {
		this.param = param;
	}

	@Override
	protected Integer doInBackground(XViewParam... path) {
		File[] freshFile = checkExistingImages();
		if (freshFile == null || freshFile.length == 0) {
			return null;
		}
		int movedPhotosCount = movePhotos();
		Logger.log("Moved " + movedPhotosCount + " photos");
		return XCamera.COMPLETED;
	}

	/**
	 * check whether there're images in the default image path
	 * 
	 * @return
	 */
	private File[] checkExistingImages() {
		File defaultFolder = new File(param.getActivity().getString(R.string.default_picture_folder_path));
		if (!defaultFolder.exists()) {
			return null;
		}
		return defaultFolder.listFiles();
	}

	/**
	 * generate current date folder and move camera photos to the date folder.
	 */
	private int movePhotos() {
		File[] pictures = checkExistingImages();
		if (pictures != null && pictures.length > 0) {
			Logger.log(">>>>>>Begin moving files: " + pictures.length);
			XFunction.XDate date = new XFunction.XDate();
			String currentTargetFolderName = param.getActivity().getString(//
					R.string.picture_folder_path) //
					+ File.separator + date.getYear() + "." + date.getMonth() //
					+ File.separator//
					+ date.getYear() + "." + date.getMonth() + "." + date.getDay();

			/** get picture folder and create system locale date folder */
			File pictureFolder = new File(currentTargetFolderName);
			if (!pictureFolder.exists()) {
				pictureFolder.mkdirs();
			}

			/** moving pictures */
			for (File pictureItem : pictures) {
				pictureItem.renameTo(new File(currentTargetFolderName + File.separator + pictureItem.getName()));
			}
		} else {
			Logger.log("There're no files in the default camera folder");
		}
		return pictures.length;
	}
}