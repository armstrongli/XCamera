package com.xxboy.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.GridView;

import com.xxboy.adapters.XAdapter;
import com.xxboy.adapters.XAdapterBase;
import com.xxboy.adapters.XAdapterPicture;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XReloadPhoto extends AsyncTask<Activity, Void, Void> {

	private XPhotoParam param;

	public XReloadPhoto(XPhotoParam param) {
		super();
		this.param = param;
	}

	@Override
	protected Void doInBackground(Activity... params) {
		Activity mainActivity = params[0];
		final GridView gridview = ((XCamera) mainActivity).getxView();

		final XAdapter xAdp = new XAdapter(mainActivity, getDaysPhotoResourceX());

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gridview.setAdapter(xAdp);
			}
		});
		return null;
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
