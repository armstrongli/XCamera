package com.xxboy.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xxboy.log.Logger;
import com.xxboy.photo.R;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XViewActivity extends Activity implements OnGestureListener {

	public static final String INTENT_VAR_PATH = "INTENT_VAR_PATH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xcamera_imageview);
		String path = getIntent().getStringExtra(XViewActivity.INTENT_VAR_PATH);
		Logger.log("View Image: " + path);

		setImage(R.id.xcamera_imageview, path);
		setImage(R.id.xcamera_imageview1, path);
		setImage(R.id.xcamera_imageview2, path);
		setImage(R.id.xcamera_imageview3, path);
	}

	private void setImage(int imageviewResId, String imagePath) {
		ImageView imageview = (ImageView) findViewById(imageviewResId);
		if (imageview != null) {
			Bitmap photo = XCacheUtil.getImaveView(imagePath);
			if (photo == null) {
				photo = BitmapFactory.decodeFile(imagePath, getOptionsInCalculate(imagePath));
				if (photo != null) {
					setImage(imageview, photo);
				}
			}
		}
	}

	private void setImage(ImageView view, Bitmap image) {
		if (image != null && !image.isRecycled()) {
			view.setImageBitmap(image);
		}
	}

	private BitmapFactory.Options getOptionsInCalculate(String resourcePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(resourcePath, opt);
		float width_divide_height = opt.outHeight / opt.outWidth;
		double cal_width = 0, cal_height = 0;
		if (width_divide_height > XCameraConst.WIDTH_DIVIDE_HEIGHT) {
			if (opt.outWidth > XCameraConst.SCREEN_WIDTH) {
				cal_width = XCameraConst.SCREEN_WIDTH;
				cal_height = cal_width * (opt.outHeight / opt.outWidth);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = (int) (opt.outHeight / cal_height);
		} else {
			if (opt.outHeight > XCameraConst.SCREEN_HEIGHT) {
				cal_height = XCameraConst.SCREEN_HEIGHT;
				cal_width = cal_height * (opt.outWidth / opt.outHeight);
			} else {
				cal_height = opt.outHeight;
				cal_width = opt.outWidth;
			}
			opt.inSampleSize = (int) (opt.outWidth / cal_width);
		}
		opt.outHeight = (int) (cal_height);
		opt.outWidth = (int) (cal_width);

		opt.inSampleSize = opt.inSampleSize;

		opt.inJustDecodeBounds = false;
		return opt;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
}
