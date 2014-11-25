package com.xxboy.xcamera;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.services.XPhotoParam;
import com.xxboy.services.XService;
import com.xxboy.services.XViewMovePhotos;

public class XCamera extends Activity {
	private String xPath, xCachePath, cameraPath;

	public static final class XCameraConst {
		public static final String VIEW_NAMW_IMAGE_ITEM = "ItemImage";

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

	public static class CallCameraListener implements OnClickListener {
		private Activity activity;
		private Camera camera;

		public CallCameraListener(Activity activity) {
			this.activity = activity;
		}

		public CallCameraListener(Activity activity, Camera camera) {
			this.activity = activity;
			this.camera = camera;
		}

		@Override
		public void onClick(View v) {
			this.camera.stopPreview();

			Intent intent = new Intent();
			intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

			// return to XCamera after take photos
			this.activity.startActivityForResult(intent, 1);
		}
	}

	public static class XViewParam {
		private String path;
		private GridView gridview;
		private Activity activity;

		public XViewParam(Activity activity, String path, GridView gridview) {
			this.activity = activity;
			this.path = path;
			this.gridview = gridview;
		}

		public Activity getActivity() {
			return this.activity;
		}

		public String getPath() {
			return this.path;
		}

		public GridView getGridView() {
			return this.gridview;
		}
	}

	public class XServiceAT extends AsyncTask<String, Void, Void> {
		private Activity activity;

		public XServiceAT(Activity activity) {
			super();
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(String... params) {
			this.activity.startService(new Intent(this.activity, XService.class));
			return null;
		}

	}

	public XPreview getXPreview() {
		return this.xpreview;
	}

	private XPreview xpreview;
	private Camera mCamera;
	int numberOfCameras;
	int defaultCameraId;

	public static final Integer COMPLETED = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == COMPLETED) {
				// stateText.setText("completed");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera);
		// start xcamera service
		new XServiceAT(this).execute();

		initScreenParameters();

		// get components in the main view.
		this.xpreview = new XPreview(this);
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

	}

	@Override
	protected void onResume() {
		super.onResume();
		// GridView gridview = (GridView) findViewById(R.id.photo_grid);

		this.mCamera = Camera.open(0);
		this.xpreview.setCamera(mCamera);
		this.mCamera.startPreview();

		// set button click to call system default camera.
		this.xpreview.setOnClickListener(new CallCameraListener(this, this.mCamera));
		//
		// final XViewParam param = new XViewParam(this,
		// getString(R.string.picture_folder_path) + "/2014.11/2014.11.23/",
		// gridview);
		//
		// XViewReloadPhotos reload = new XViewReloadPhotos(param);
		// Integer result = null;
		// try {
		// result = reload.execute().get();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// Logger.log("The return result is " + result);
		// if (result == null || COMPLETED != result) {
		// return;
		// }
		// List<HashMap<String, Object>> resource = get1DayPhotoResource(new
		// File(param.getPath()));
		// Logger.log("There're " + resource.size() +
		// " photos in the exact path");
		// SimpleAdapter adp = new SimpleAdapter(param.getActivity(),//
		// resource, //
		// R.layout.xcamera_item,//
		// new String[] { XCameraConst.VIEW_NAMW_IMAGE_ITEM },//
		// new int[] { R.id.ItemImage });
		// param.getGridView().setAdapter(adp);
		//
		// }

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
		// stop service
		this.stopService(new Intent(this, XService.class));
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		GridView gridview = (GridView) findViewById(R.id.photo_grid);
		final XViewParam param = new XViewParam(this, getString(R.string.picture_folder_path) + "/2014.11/2014.11.23/", gridview);

		XPhotoParam p = new XPhotoParam(this.xPath, this.xCachePath, this.cameraPath);
		XViewMovePhotos reload = new XViewMovePhotos(p);
		Integer result = null;
		try {
			result = reload.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Logger.log("The return result is " + result);
		if (result == null || COMPLETED != result) {
			Logger.log("return~~~~");
			return;
		}
		List<HashMap<String, Object>> resource = get1DayPhotoResource(new File(param.getPath()));
		Logger.log("There're " + resource.size() + " photos in the exact path");
		SimpleAdapter adp = new SimpleAdapter(param.getActivity(),//
				resource, //
				R.layout.xcamera_item,//
				new String[] { XCameraConst.VIEW_NAMW_IMAGE_ITEM },//
				new int[] { R.id.ItemImage });
		param.getGridView().setAdapter(adp);
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

	/**
	 * get all xCamera photos
	 * 
	 * @return
	 */
	private List<HashMap<String, Object>> getDaysPhotoResource() {
		String xcameraPath = getString(R.string.picture_folder_path);
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
		for (File xItem : xFolders) {
			List<HashMap<String, Object>> itemResult = get1DayPhotoResource(xItem);
			if (itemResult != null && itemResult.size() > 0) {
				result.addAll(itemResult);
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
