package com.xxboy.xcamera;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xxboy.common.CommonFunction;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XCamera extends Activity {
	public static interface XCameraConst {
		String VIEW_NAMW_IMAGE_ITEM = "ItemImage";
	}

	public static class CallCameraListener implements OnClickListener {
		private Activity activity;

		public CallCameraListener(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

			// return to XCamera after take photos
			this.activity.startActivityForResult(intent, 1);
		}
	}

	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera);

		// get components in the main view.
		this.button = (Button) findViewById(R.id.btn_camera);
		GridView gridview = (GridView) findViewById(R.id.photo_grid);

		List<HashMap<String, Object>> resource = get1DayPhotoResource(new File(getString(R.string.picture_folder_path)
				+ "/2014.11/2014.11.09/"));
		SimpleAdapter adp = new SimpleAdapter(this,//
				resource, //
				R.layout.xcamera_item,//
				new String[] { XCameraConst.VIEW_NAMW_IMAGE_ITEM },//
				new int[] { R.id.ItemImage });
		gridview.setAdapter(adp);

		// set button click to call system default camera.
		this.button.setOnClickListener(new CallCameraListener(this));
	}

	/**
	 * generate 1 folder's image view item list.
	 * 
	 * @param xcameraDateFolder
	 * @return
	 */
	private List<HashMap<String, Object>> get1DayPhotoResource(File xcameraDateFolder) {
		List<HashMap<String, Object>> photoResource = new ArrayList<HashMap<String, Object>>();

		File[] photos = xcameraDateFolder.listFiles();
		for (File photoItem : photos) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(XCameraConst.VIEW_NAMW_IMAGE_ITEM, photoItem.getAbsolutePath());
			photoResource.add(item);
		}

		return photoResource;
	}

	/**
	 * get local image bitmap
	 * 
	 * @param localImgFullPath
	 *            full path. e.g. /sdcard/DCIM/camera/1.jpg
	 * @return
	 */
	private Bitmap getLoacalBitmap(String localImgFullPath) {
		return BitmapFactory.decodeFile(localImgFullPath);
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
			CommonFunction.XDate date = new CommonFunction.XDate();
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
