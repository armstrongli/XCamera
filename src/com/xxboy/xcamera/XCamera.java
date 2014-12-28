package com.xxboy.xcamera;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.xxboy.listeners.XScrollListener;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.XCameraAsyncTask;
import com.xxboy.services.XInitialAsyncTask;
import com.xxboy.services.XPhotoParam;
import com.xxboy.services.XReloadPhoto;

public class XCamera extends Activity {
	private String xPath, xCachePath, cameraPath;
	public static int count = 20;
	public static Map<String, Bitmap> imageCache = new LinkedHashMap<String, Bitmap>();

	public static final class XCameraConst {
		public static final String VIEW_NAME_IMAGE_ITEM = "ItemImage";
		public static final String VIEW_NAME_IMAGE_RESOURCE = "ImageResource";

		public static final String VIEW_NAME_CAMERA_ID = "id_camera_preview";

		/** screen width */
		public static int SCREEN_WIDTH = -1;
		/** screen height */
		public static int SCREEN_HEIGHT = -1;

		/** photo item width */
		public static int PHOTO_ITEM_WIDTH = -1;
		/** photo item height */
		public static int PHOTO_ITEM_HEIGHT = -1;

		public static void setWidthHeight(int width, int height) {
			SCREEN_WIDTH = width;
			SCREEN_HEIGHT = height;

			PHOTO_ITEM_WIDTH = width / 3;
			PHOTO_ITEM_HEIGHT = PHOTO_ITEM_WIDTH;
		}
	}

	private GridView xGridView;

	public static final Integer COMPLETED = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera);

		initScreenParameters();

		// get components in the main view.
		this.xGridView = (GridView) findViewById(R.id.photo_grid);
		this.xGridView.setOnScrollListener(new XScrollListener(this));

		this.xPath = getString(R.string.picture_folder_path);
		this.xCachePath = getString(R.string.cash_picture_folder_path);
		this.cameraPath = getString(R.string.default_picture_folder_path);

		new XInitialAsyncTask(new XPhotoParam(xPath, xCachePath, cameraPath)).execute();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// prepare cameras
		new XCameraAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		moveAndLoadPhotos(true);
	}

	@Override
	protected void onPause() {
		XCameraAsyncTask.releaseCameras();
		super.onPause();
	}

	private void initScreenParameters() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		XCameraConst.setWidthHeight(metric.widthPixels, metric.heightPixels);

		Logger.log("SCREEN_WIDTH: " + XCameraConst.SCREEN_WIDTH);
		Logger.log("SCREEN_HEIGHT: " + XCameraConst.SCREEN_HEIGHT);
	}

	@Override
	protected void onDestroy() {
		XCameraAsyncTask.releaseCameras();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		moveAndLoadPhotos(false);
	}

	private void moveAndLoadPhotos(boolean reloadFlag) {
		XPhotoParam photoParam = new XPhotoParam(this.xPath, this.xCachePath, this.cameraPath);
		new XReloadPhoto(this, photoParam).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.camera_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public GridView getxView() {
		return xGridView;
	}

	public void setxView(GridView xView) {
		this.xGridView = xView;
	}

}
