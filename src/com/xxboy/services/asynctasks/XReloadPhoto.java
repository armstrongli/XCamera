package com.xxboy.services.asynctasks;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.hardware.Camera;
import android.os.AsyncTask;

import com.xxboy.adapters.xdata.XAdapterBase;
import com.xxboy.adapters.xdata.XAdapterCamera;
import com.xxboy.adapters.xdata.XAdapterPicture;
import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.utils.XColorUtil;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public final class XReloadPhoto extends AsyncTask<Void, Void, Void> {

	protected static final class Mover {
		public static Integer moveAllPhotos() {
			File[] freshFile = checkExistingImages();
			if (freshFile == null || freshFile.length == 0) {
				return 0;
			}
			Logger.log("Begin moving photos");
			int movedPhotosCount = movePhotos();
			Logger.log("Moved " + movedPhotosCount + " photos");
			return movedPhotosCount;
		}

		/**
		 * check whether there're images in the default image path
		 * 
		 * @return
		 */
		private static File[] checkExistingImages() {
			File defaultFolder = new File(XCameraConst.GLOBAL_X_DEFAULT_CAMERA_PATH);
			if (!defaultFolder.exists()) {
				return null;
			}
			return defaultFolder.listFiles();
		}

		/**
		 * generate current date folder and move camera photos to the date folder.
		 */
		private static int movePhotos() {
			File[] pictures = checkExistingImages();
			if (pictures != null && pictures.length > 0) {
				Logger.log(">>>>>>Begin moving files: " + pictures.length);
				XFunction.XDate date = new XFunction.XDate();
				String currentTargetFolderName = ""//
						+ XCameraConst.GLOBAL_X_CAMERA_PATH//
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

	@Override
	protected Void doInBackground(Void... param) {
		// moving files
		int hasCount = XCamera.getxView().getCount();
		Integer moveCount = Mover.moveAllPhotos();
		// if there's no change, it won't reload picture.
		if ((moveCount == null || moveCount <= 0) && hasCount > 0) {
			return null;
		}

		LinkedList<XAdapterBase> imageResources = getDaysPhotoResourceX();
		LinkedList<XAdapterBase> cameraResources = getCameraPreviewsX();
		LinkedList<XAdapterBase> allResources = new LinkedList<XAdapterBase>();
		allResources.addAll(cameraResources);
		Collections.reverse(imageResources);
		allResources.addAll(imageResources);
		/**
		 * reload gridview images
		 */
		XCamera.reloadGridview(allResources);
		return null;
	}

	/**
	 * get all camera resources
	 * 
	 * @return
	 */
	private LinkedList<XAdapterBase> getCameraPreviewsX() {
		LinkedList<XAdapterBase> cameraResources = new LinkedList<XAdapterBase>();
		if (Camera.getNumberOfCameras() > 0) {
			Map<String, Object> res = new HashMap<String, Object>();
			cameraResources.add(new XAdapterCamera(res));
		}

		return cameraResources;
	}

	/**
	 * get all xCamera photos
	 * 
	 * @return
	 */
	private LinkedList<XAdapterBase> getDaysPhotoResourceX() {
		String xcameraPath = XCameraConst.GLOBAL_X_CAMERA_PATH;
		File xCameraFolder = new File(xcameraPath);
		if (!xCameraFolder.exists()) {
			xCameraFolder.mkdirs();
			return new LinkedList<XAdapterBase>();
		}

		LinkedList<XAdapterBase> result = new LinkedList<XAdapterBase>();
		File[] xyyyymmFolders = xCameraFolder.listFiles();
		if (xyyyymmFolders == null || xyyyymmFolders.length == 0) {
			return result;
		}
		for (File yyyymmFolder : xyyyymmFolders) {
			Logger.log("Going to " + yyyymmFolder.getAbsolutePath());
			if (!yyyymmFolder.isDirectory() || yyyymmFolder.isHidden()) {
				continue;
			}
			File[] yyyymmddFolders = yyyymmFolder.listFiles();
			for (File yyyymmddFolder : yyyymmddFolders) {
				Logger.log("Going to " + yyyymmddFolder.getAbsolutePath());
				List<XAdapterBase> itemResult = get1DayPhotoResourceX(yyyymmddFolder);
				if (itemResult != null && itemResult.size() > 0) {
					result.addAll(itemResult);
				}
			}
		}
		return result;
	}

	/**
	 * generate 1 folder's image view item list.
	 * 
	 * @param xcameraDateFolder
	 * @return
	 */
	private LinkedList<XAdapterBase> get1DayPhotoResourceX(File xcameraDateFolder) {
		int color = XColorUtil.getBackgroundColor(xcameraDateFolder.getName());
		LinkedList<XAdapterBase> photoResource = new LinkedList<XAdapterBase>();
		if (!xcameraDateFolder.exists()) {
			return photoResource;
		}

		File[] photos = xcameraDateFolder.listFiles();
		if (photos != null && photos.length > 0)
			for (File photoItem : photos) {
				if (photoItem.isDirectory()) {
					Logger.log("Come up with one Directory: " + photoItem.getAbsolutePath());
					continue;
				} else if (photoItem.isHidden()) {
					Logger.log("Come up with one hidden file: " + photoItem.getAbsolutePath());
					continue;
				}
				HashMap<String, Object> item = new HashMap<String, Object>();
				// item.put(XCameraConst.VIEW_NAME_IMAGE_ITEM, R.drawable.big_load);
				item.put(XCameraConst.VIEW_NAME_IMAGE_ITEM, photoItem.getAbsolutePath());
				item.put(XCameraConst.VIEW_NAME_IMAGE_RESC, photoItem.getAbsolutePath());
				photoResource.add(new XAdapterPicture(item, color));
			}

		return photoResource;
	}
}
