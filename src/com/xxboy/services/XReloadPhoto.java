package com.xxboy.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XReloadPhoto extends AsyncTask<Activity, Void, ListView> {

	private XPhotoParam param;

	public XReloadPhoto(XPhotoParam param) {
		super();
		this.param = param;
	}

	@Override
	protected ListView doInBackground(Activity... params) {
		ListView g = new ListView(params[0]);
//		g.setColumnWidth(100);
//		g.setNumColumns(3);

		List<HashMap<String, Object>> resource = getDaysPhotoResource();
		Logger.log("There're " + resource.size() + " photos in the exact path");
		SimpleAdapter adp = new SimpleAdapter(params[0],//
				resource, //
				R.layout.xcamera_item,//
				new String[] { XCameraConst.VIEW_NAMW_IMAGE_ITEM },//
				new int[] { R.id.ItemImage });
		g.setAdapter(adp);

		return g;
	}

	/**
	 * get all xCamera photos
	 * 
	 * @return
	 */
	private List<HashMap<String, Object>> getDaysPhotoResource() {
		String xcameraPath = this.param.getxPath();
		File xFolder = new File(xcameraPath);
		if (!xFolder.exists()) {
			xFolder.mkdirs();
			return new ArrayList<HashMap<String, Object>>();
		}

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		File[] xFolders = xFolder.listFiles();
		if (xFolders == null || xFolders.length == 0) {
			return result;
		}
		for (File monthFolder : xFolders) {
			Logger.log("Going to " + monthFolder.getAbsolutePath());
			File[] daysFolder = monthFolder.listFiles();
			for (File dayFolder : daysFolder) {
				Logger.log("Going to " + dayFolder.getAbsolutePath());
				List<HashMap<String, Object>> itemResult = get1DayPhotoResource(dayFolder);
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
	private List<HashMap<String, Object>> get1DayPhotoResource(File xcameraDateFolder) {
		List<HashMap<String, Object>> photoResource = new ArrayList<HashMap<String, Object>>();
		if (!xcameraDateFolder.exists()) {
			return photoResource;
		}

		File[] photos = xcameraDateFolder.listFiles();
		for (File photoItem : photos) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(XCameraConst.VIEW_NAMW_IMAGE_ITEM, photoItem.getAbsolutePath());
			photoResource.add(item);
		}

		return photoResource;
	}
}
