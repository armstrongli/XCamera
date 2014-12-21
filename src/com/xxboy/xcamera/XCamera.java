package com.xxboy.xcamera;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.xxboy.listeners.CallCameraListener;
import com.xxboy.listeners.XScrollListener;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.XCompressPhotosAsync;
import com.xxboy.services.XInitial;
import com.xxboy.services.XPhotoParam;
import com.xxboy.services.XReloadPhoto;
import com.xxboy.services.XViewMovePhotos;
import com.xxboy.view.XPreview;

public class XCamera extends Activity implements OnScrollListener {
	private String xPath, xCachePath, cameraPath;

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

	public XPreview getXPreview() {
		return this.xpreview;
	}

	private XPreview xpreview;
	private GridView xView;
	private Camera mCamera;
	int numberOfCameras;
	int defaultCameraId;

	public static final Integer COMPLETED = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera);

		initScreenParameters();

		// get components in the main view.
		this.xpreview = new XPreview(this);
		this.xView = (GridView) findViewById(R.id.photo_grid);
		this.xView.setOnScrollListener(new XScrollListener(this));

		FrameLayout previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
		previewLayout.addView(this.xpreview, 0);

		numberOfCameras = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraId = i;
			}
		}

		this.xPath = getString(R.string.picture_folder_path);
		this.xCachePath = getString(R.string.cash_picture_folder_path);
		this.cameraPath = getString(R.string.default_picture_folder_path);

		new XInitial(new XPhotoParam(xPath, xCachePath, cameraPath)).execute();
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			new XReloadPhoto(new XPhotoParam(xPath, xCachePath, cameraPath)).execute(this);
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			this.mCamera = Camera.open(0);
			this.xpreview.setCamera(mCamera);
			this.mCamera.startPreview();
		} catch (Exception e) {
			Toast.makeText(this, "Can't access default camera!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// set button click to call system default camera.
		this.xpreview.setOnClickListener(new CallCameraListener(this, this.mCamera));

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (this.mCamera != null) {
			this.xpreview.setCamera(null);
			this.mCamera.release();
			this.mCamera = null;
		}
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
		this.xpreview.surfaceDestroyed(null);
		if (this.mCamera != null) {
			this.mCamera.stopPreview();
			this.mCamera.release();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		XPhotoParam p = new XPhotoParam(this.xPath, this.xCachePath, this.cameraPath);
		XViewMovePhotos reload = new XViewMovePhotos(p);
		Integer result = null;
		try {
			result = reload.execute().get();
		} catch (InterruptedException e) {
			Logger.log(e);
		} catch (ExecutionException e) {
			Logger.log(e);
		}

		Logger.log("The return result is " + result);
		if (result == null || result <= 0) {
			Logger.log("return~~~~");
			return;
		}
		// showing message to tell it's doing reloading photos
		Toast.makeText(this, "Reloading your photos", Toast.LENGTH_SHORT).show();
		new XCompressPhotosAsync(p).execute();
		new XReloadPhoto(p).execute(this);
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO change the picture after the scroll stop.
		Logger.log("Scrolling state: " + scrollState);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO change loading picture and recycle picture resource.
		Logger.log("Scroll state change and load resource: " + firstVisibleItem + "--" + visibleItemCount + "--" + totalItemCount);
	}

	public GridView getxView() {
		return xView;
	}

	public void setxView(GridView xView) {
		this.xView = xView;
	}

}
