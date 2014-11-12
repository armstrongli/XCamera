package com.xxboy.xcamera;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xxboy.common.XFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XCamera extends Activity {
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

	public static class XViewPhotos extends AsyncTask<XViewParam, Void, Void> {

		@Override
		protected Void doInBackground(XViewParam... path) {
			XViewParam param = path[0];
			List<HashMap<String, Object>> resource = get1DayPhotoResource(new File(param.getPath()));
			SimpleAdapter adp = new SimpleAdapter(param.getActivity(),//
					resource, //
					R.layout.xcamera_item,//
					new String[] { XCameraConst.VIEW_NAMW_IMAGE_ITEM },//
					new int[] { R.id.ItemImage });
			param.getGridView().setAdapter(adp);
			return null;
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

	public XPreview getXPreview() {
		return this.xpreview;
	}

	private XPreview xpreview;
	private Camera mCamera;
	int numberOfCameras;
	int defaultCameraId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera);

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
		GridView gridview = (GridView) findViewById(R.id.photo_grid);

		this.mCamera = Camera.open(0);
		this.xpreview.setCamera(mCamera);
		this.mCamera.startPreview();

		// set button click to call system default camera.
		this.xpreview.setOnClickListener(new CallCameraListener(this, this.mCamera));

		final XViewParam param = new XViewParam(this, getString(R.string.picture_folder_path) + "/2014.11/2014.11.09/",
				gridview);
		new XViewPhotos().execute(param);
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

	// public void stopPreview() {
	// this.mCamera.stopPreview();
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		this.xpreview.surfaceDestroyed(null);
		if (this.mCamera != null) {
			this.mCamera.stopPreview();
			this.mCamera.release();
		}
		super.onDestroy();
	}

	/**
	 * check whether there're images in the default image path
	 * 
	 * @return
	 */
	private File[] checkExistingImages() {
		File defaultFolder = new File(getString(R.string.default_picture_folder_path));
		if (!defaultFolder.exists()) {
			return null;
		}
		return defaultFolder.listFiles();
	}

	/**
	 * generate current date folder and move camera photos to the date folder.
	 */
	private void movingFile() {
		File[] pictures = checkExistingImages();
		if (pictures != null && pictures.length > 0) {
			Logger.log(">>>>>>Begin moving files: " + pictures.length);
			XFunction.XDate date = new XFunction.XDate();
			String currentTargetFolderName = getString(//
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Log.i("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		movingFile();
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
}