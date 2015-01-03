package com.xxboy.services.asynctasks;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.widget.GridView;

import com.xxboy.adapters.XAdapter;
import com.xxboy.adapters.XAdapterBase;
import com.xxboy.adapters.XAdapterCamera;
import com.xxboy.adapters.XAdapterPicture;
import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public final class XReloadPhoto extends AsyncTask<Void, Void, Void> {

	protected static final class Mover {
		public static Integer movePhotos(XCamera xCamera) {
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

	private XCamera activity;

	public XReloadPhoto(XCamera activity) {
		super();
		this.activity = activity;
	}

	@Override
	protected Void doInBackground(Void... param) {
		// moving files
		Mover.movePhotos(this.activity);
		// reloading grid view
		final GridView gridview = (this.activity).getxView();

		List<XAdapterBase> imageResources = getDaysPhotoResourceX();
		List<XAdapterBase> cameraResources = getCameraPreviewsX();
		List<XAdapterBase> allResources = new LinkedList<XAdapterBase>();
		allResources.addAll(cameraResources);
		allResources.addAll(imageResources);
		final XAdapter xAdp = this.activity.getXAdapter(allResources);

		this.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gridview.setAdapter(xAdp);
			}
		});
		return null;
	}

	/**
	 * get all camera resources
	 * 
	 * @return
	 */
	private List<XAdapterBase> getCameraPreviewsX() {
		List<Camera> cameras = XCameraAsyncTask.getCameras();
		List<XAdapterBase> cameraResources = new LinkedList<XAdapterBase>();
		for (Camera cameraItem : cameras) {
			Map<String, Object> res = new HashMap<String, Object>();
			res.put(XCameraConst.VIEW_NAME_CAMERA_ID, cameraItem);
			cameraResources.add(new XAdapterCamera(this.activity, res));
		}
		return cameraResources;
	}

	/**
	 * get all xCamera photos
	 * 
	 * @return
	 */
	private List<XAdapterBase> getDaysPhotoResourceX() {
		String xcameraPath = XCameraConst.GLOBAL_X_CAMERA_PATH;
		File xFolder = new File(xcameraPath);
		if (!xFolder.exists()) {
			xFolder.mkdirs();
			return new LinkedList<XAdapterBase>();
		}

		List<XAdapterBase> result = new LinkedList<XAdapterBase>();
		File[] xFolders = xFolder.listFiles();
		if (xFolders == null || xFolders.length == 0) {
			return result;
		}
		for (File monthFolder : xFolders) {
			Logger.log("Going to " + monthFolder.getAbsolutePath());
			if (!monthFolder.isDirectory() || monthFolder.isHidden()) {
				continue;
			}
			File[] daysFolder = monthFolder.listFiles();
			for (File dayFolder : daysFolder) {
				Logger.log("Going to " + dayFolder.getAbsolutePath());
				List<XAdapterBase> itemResult = get1DayPhotoResourceX(dayFolder);
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
	private List<XAdapterBase> get1DayPhotoResourceX(File xcameraDateFolder) {
		List<XAdapterBase> photoResource = new LinkedList<XAdapterBase>();
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
				photoResource.add(new XAdapterPicture(item));
			}

		return photoResource;
	}
}
