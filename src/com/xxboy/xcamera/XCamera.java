package com.xxboy.xcamera;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxboy.activities.XViewActivity;
import com.xxboy.adapters.XAdapter;
import com.xxboy.adapters.xdata.XAdapterBase;
import com.xxboy.listeners.XScrollListener;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;
//import com.xxboy.services.asynctasks.XBackground;
//import com.xxboy.services.asynctasks.XCameraAsyncTask;
import com.xxboy.services.asynctasks.XInitialAsyncTask;
import com.xxboy.services.asynctasks.XPreCacheLoaderAsyncTask;
import com.xxboy.services.asynctasks.XReloadPhoto;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class XCamera extends Activity {
	private static XAdapter xAdp = null;
	private static ArrayList<String> resources = null;
	private static XCamera xCamera = null;

	public static final class XCameraConst {
		public static final String XCAMERA_IMAGE_VIEW = "com.xxboy.activities.XViewActivity";

		public static int VERSION = -1;

		public static final String VIEW_NAME_IMAGE_ITEM = "ItemImage";
		public static final String VIEW_NAME_IMAGE_RESC = "ItemResource";

		public static final String VIEW_NAME_CAMERA_ID = "id_camera_preview";

		/** screen width */
		public static int SCREEN_WIDTH = -1;
		/** screen height */
		public static int SCREEN_HEIGHT = -1;

		/** photo item width */
		public static int PHOTO_ITEM_WIDTH = -1;
		/** photo item height */
		public static int PHOTO_ITEM_HEIGHT = -1;

		public static float ITEM_WIDTH_DIVIDE_HEIGHT = 0;
		public static float VIEW_WIDTH_DIVIDE_HEIGHT = 0;

		public static void setWidthHeight(int width, int height) {
			SCREEN_WIDTH = width;
			SCREEN_HEIGHT = height;
			VIEW_WIDTH_DIVIDE_HEIGHT = ((float) width) / ((float) height);

			PHOTO_ITEM_WIDTH = width / 3;
			PHOTO_ITEM_HEIGHT = PHOTO_ITEM_WIDTH;

			ITEM_WIDTH_DIVIDE_HEIGHT = PHOTO_ITEM_WIDTH / PHOTO_ITEM_HEIGHT;
		}

		public static String GLOBAL_X_CACHE_PATH = null;
		public static String GLOBAL_X_DEFAULT_CAMERA_PATH = null;
		public static String GLOBAL_X_CAMERA_PATH = null;

		public static int GLOBAL_X_GRIDVIEW_VISIABLE_COUNT = 20;
	}

	private static GridView xGridView;

	public static final Integer COMPLETED = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera);

		initParameters();
		xAdp = new XAdapter(this, new LinkedList<XAdapterBase>());

		// get components in the main view.
		xGridView = (GridView) findViewById(R.id.photo_grid);
		xGridView.setOnScrollListener(new XScrollListener());
		xGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickImage(view);
			}
		});

		findViewById(R.id.id_float_camera).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
				startActivity(intent);
			}
		});

		XCameraConst.GLOBAL_X_CAMERA_PATH = getString(R.string.picture_folder_path);
		XCameraConst.GLOBAL_X_CACHE_PATH = this.getExternalCacheDir().getAbsolutePath();
		XCameraConst.GLOBAL_X_DEFAULT_CAMERA_PATH = getString(R.string.default_picture_folder_path);

		new XInitialAsyncTask().execute();
		XCamera.xCamera = this;

		XQueueUtil.setHandler(new Handler());
	}

	private void onClickImage(View v) {
		TextView txtPath = (TextView) v.findViewById(R.id.ItemResource);
		ImageView imageView4Camera = (ImageView) v.findViewById(R.id.id_camera_image);
		if (txtPath != null) {
			// means it's the image view item
			Intent intent = new Intent(this, XViewActivity.class);
			intent.putExtra(XViewActivity.INTENT_VAR_PATH, txtPath.getText().toString());
			intent.putStringArrayListExtra(XViewActivity.INTENT_VAR_PATHES, XCamera.resources);
			this.startActivity(intent);
		} else if (imageView4Camera != null) {
			// means it's the camera view
			Intent intent = new Intent();
			intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
			// return to XCamera after take photos
			this.startActivity(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
//		try {
//			new XBackground((LinearLayout) findViewById(R.id.main), BitmapFactory.decodeResource(getResources(), R.drawable.background)).execute();
//		} catch (Exception e) {
//			Logger.log(e.getMessage(), e);
//		}
		// prepare cameras
		// new XCameraAsyncTask().execute();
		new XPreCacheLoaderAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		moveAndLoadPhotos(true);
	}

	@Override
	protected void onPause() {
		XCacheUtil.closeDiskCache();
		super.onPause();
	}

	private void initParameters() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		XCameraConst.setWidthHeight(metric.widthPixels, metric.heightPixels);

		Logger.log("SCREEN_WIDTH: " + XCameraConst.SCREEN_WIDTH);
		Logger.log("SCREEN_HEIGHT: " + XCameraConst.SCREEN_HEIGHT);

		try {
			PackageManager pm = this.getPackageManager();
			PackageInfo pkgInfo = pm.getPackageInfo(this.getPackageName(), 0);
			XCameraConst.VERSION = pkgInfo.versionCode;
		} catch (NameNotFoundException e) {
			Logger.log(e.getMessage(), e);
		}
	}

	@Override
	protected void onDestroy() {
		// XCameraAsyncTask.releaseCameras();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		moveAndLoadPhotos(false);
	}

	private void moveAndLoadPhotos(boolean reloadFlag) {
		new XReloadPhoto().execute();
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

	public static GridView getxView() {
		return XCamera.xGridView;
	}

	public static void setxView(GridView xView) {
		XCamera.xGridView = xView;
	}

	private static XAdapter getXAdapter(LinkedList<XAdapterBase> data) {
		return XCamera.xAdp.setData(data != null ? data : new LinkedList<XAdapterBase>());
	}

	public static void setAllResourcePath(ArrayList<String> resources) {
		XCamera.resources = resources;
	}

	public static void reloadGridview(final LinkedList<XAdapterBase> data) {
		try {
			XCamera.xCamera.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					XCamera.xGridView.setAdapter(getXAdapter(data));
				}
			});
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		} finally {
			XQueueUtil.run();
		}
	}
}
