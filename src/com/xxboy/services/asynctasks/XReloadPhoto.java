package com.xxboy.services.asynctasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.widget.GridView;

import com.xxboy.adapters.XAdapter;
import com.xxboy.adapters.XAdapterBase;
import com.xxboy.adapters.XAdapterCamera;
import com.xxboy.adapters.XAdapterPicture;
import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.XPhotoParam;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public final class XReloadPhoto extends AsyncTask<Void, Void, Void> {

	protected static final class Mover {
		public static Integer movePhotos(XCamera xCamera, XPhotoParam param) {
			File[] freshFile = checkExistingImages(param);
			if (freshFile == null || freshFile.length == 0) {
				return 0;
			}
			Logger.log("Begin moving photos");
			int movedPhotosCount = movePhotos(param);
			Logger.log("Moved " + movedPhotosCount + " photos");
			return movedPhotosCount;
		}

		/**
		 * check whether there're images in the default image path
		 * 
		 * @return
		 */
		private static File[] checkExistingImages(XPhotoParam param) {
			File defaultFolder = new File(param.getDefaultCameraPath());
			if (!defaultFolder.exists()) {
				return null;
			}
			return defaultFolder.listFiles();
		}

		/**
		 * generate current date folder and move camera photos to the date
		 * folder.
		 */
		private static int movePhotos(XPhotoParam param) {
			File[] pictures = checkExistingImages(param);
			if (pictures != null && pictures.length > 0) {
				Logger.log(">>>>>>Begin moving files: " + pictures.length);
				XFunction.XDate date = new XFunction.XDate();
				String currentTargetFolderName = ""//
						+ param.getxCameraPath()//
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

	protected static final class Compressor {
		public static final int compressPhotos(XCamera xCamera, XPhotoParam param) {
			// remove unsynchronized cache files
			Logger.log("Synchronizing cache folders");
			syncCacheFolder(param);

			Logger.log("Compress photos begin");
			File xCameraFolder = new File(param.getxCameraPath());

			Logger.log("Compress photos: begin checking date folders");
			return compressXFolder(xCameraFolder, xCamera, param);
		}

		private static final int compressXFolder(File xCameraFolder, XCamera xCamera, XPhotoParam param) {
			int count = 0;
			File[] yyyymmFolders = xCameraFolder.listFiles();
			for (File yyyymmFolder : yyyymmFolders) {
				count += compress1yyyymmFolder(yyyymmFolder, xCamera, param);
			}
			return count;
		}

		private static final int compress1yyyymmFolder(File yyyymmFolder, XCamera xCamera, XPhotoParam param) {
			if (yyyymmFolder.isHidden()) {
				return 0;
			}
			int count = 0;
			File[] yyyymmddFolders = yyyymmFolder.listFiles();
			for (File yyyymmddFolder : yyyymmddFolders) {
				count += compress1FolderPhotos(yyyymmddFolder, xCamera, param);
			}
			return count;
		}

		/**
		 * compress one bunch of photos
		 * 
		 * @param pictureFolder
		 *            it's one date folder.yyyy.mm.dd
		 * @param xCamera
		 * @param param
		 * @return
		 */
		private static final int compress1FolderPhotos(File pictureFolder, XCamera xCamera, XPhotoParam param) {
			int count = 0;
			File[] pictures = pictureFolder.listFiles();
			if (pictures == null || pictures.length == 0) {
				Logger.log("Compress photos: no files in [" + pictureFolder.getAbsolutePath() + "]");
				return count;
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
					compress1Photo(picture, xCamera, param);
				}
			}
			return count;
		}

		/**
		 * compress 1 photo to cache folder
		 * 
		 * @param picture
		 *            it's one picture
		 * @param xCamera
		 * @param param
		 */
		private static final void compress1Photo(File picture, XCamera xCamera, XPhotoParam param) {
			try {
				String cacheFileAbsolutePath = picture.getAbsolutePath().replaceAll(param.getxCameraPath(), param.getxCachePath());
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
				} else {
					// if the cache image exists, jump it
					return;
				}
				// new XToast(xCamera, "Compressing Photo: " +
				// picture.getAbsolutePath()).execute();
				FileOutputStream out = new FileOutputStream(file);
				Bitmap compressed = XFunction.XCompress.comp(BitmapFactory.decodeFile(picture.getAbsolutePath()));
				compressed.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				Logger.log(e);
			} catch (IOException e) {
				Logger.log(e);
			}
		}

		/**
		 * check whether years folder match the cache folder.<br/>
		 * yyyy.mm
		 * 
		 * @param param
		 */
		private static final void syncCacheFolder(XPhotoParam param) {
			File xCameraFolder = new File(param.getxCameraPath());
			File xCacheFolder = new File(param.getxCachePath());

			File[] xYearMonthFolder = xCameraFolder.listFiles();
			File[] cYearMonthFolder = xCacheFolder.listFiles();

			// remove unsynchronized cache files.
			List<File> cacheFolders = Arrays.asList(cYearMonthFolder);
			cacheFolders.removeAll(Arrays.asList(xYearMonthFolder));
			for (File item : cacheFolders) {
				XFunction.removeFolder(item);
			}
		}

	}

	private XCamera activity;
	private XPhotoParam param;

	public XReloadPhoto(XCamera activity, XPhotoParam param) {
		super();
		this.activity = activity;
		this.param = param;
	}

	@Override
	protected Void doInBackground(Void... param) {
		// moving files
		Mover.movePhotos(this.activity, this.param);
		// compressing files
		Compressor.compressPhotos(this.activity, this.param);
		// reloading grid view
		final GridView gridview = (this.activity).getxView();

		List<XAdapterBase> imageResources = getDaysPhotoResourceX();
		List<XAdapterBase> cameraResources = getCameraPreviewsX();
		List<XAdapterBase> allResources = cameraResources;
		allResources.addAll(imageResources);
		final XAdapter xAdp = new XAdapter(this.activity, allResources);

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
		String xcameraPath = this.param.getxCachePath();
		File xFolder = new File(xcameraPath);
		if (!xFolder.exists()) {
			xFolder.mkdirs();
			return new ArrayList<XAdapterBase>();
		}

		List<XAdapterBase> result = new ArrayList<XAdapterBase>();
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
		List<XAdapterBase> photoResource = new ArrayList<XAdapterBase>();
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
				// TODO load from cache
				item.put(XCameraConst.VIEW_NAME_IMAGE_ITEM, R.drawable.ic_launcher);
				item.put(XCameraConst.VIEW_NAME_IMAGE_RESOURCE, photoItem.getAbsolutePath());
				photoResource.add(new XAdapterPicture(item));
			}

		return photoResource;
	}
}
