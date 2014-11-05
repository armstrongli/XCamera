package com.xxboy.xcamera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.xxboy.common.CommonFunction;
import com.xxboy.photo.R;

public class XCamera extends Activity {
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		button = (Button) findViewById(R.id.btn_camera);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
				// intent.putExtra(MediaStore.EXTRA_OUTPUT,
				// Uri.fromFile(new File(getFullFileName())));

				// return to XCamera after take photos
				startActivityForResult(intent, 1);
			}
		});
	}

	private String generateFullFileName() {
		/** get picture folder and create system locale date folder */
		File pictureFolder = new File(getString(R.string.picture_folder_path));
		if (!pictureFolder.exists()) {
			pictureFolder.mkdirs();
		}

		String fileNameDatePart = CommonFunction.getCurrentDateString();
		String folderName = fileNameDatePart.substring(0, 8);
		String targetFolderFullPath = pictureFolder.getPath() + File.separator
				+ folderName;
		File targetFolder = new File(targetFolderFullPath);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}

		/** prepare file name */
		String photoFullName = getString(R.string.picture_prefix)
				+ fileNameDatePart + getString(R.string.picture_cofix);
		return targetFolder.getPath() + File.separator + photoFullName;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.i("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}

			// String photoFullName = getFullFileName();

			Bundle bundle = data.getExtras();
			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

			// FileOutputStream b = null;
			//
			// try {
			// b = new FileOutputStream(photoFullName);
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// } finally {
			// try {
			// b.flush();
			// b.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			((ImageView) findViewById(R.id.img_photo)).setImageBitmap(bitmap);// 将图片显示在ImageView里
		}
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
