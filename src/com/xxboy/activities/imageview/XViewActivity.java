package com.xxboy.activities.imageview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.xxboy.activities.imageview.asynctasks.ImageViewAsync;
import com.xxboy.activities.imageview.listeners.XViewTouchListener;
import com.xxboy.log.Logger;
import com.xxboy.photo.R;

public class XViewActivity extends Activity {

	public static final String INTENT_VAR_PATH = "INTENT_VAR_PATH";
	public static final String INTENT_VAR_PATHES = "INTENT_VAR_PATHES";

	private ViewFlipper viewFlipper;
	private ArrayList<String> pathes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximage_view);

		this.viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

		String path = getIntent().getStringExtra(XViewActivity.INTENT_VAR_PATH);
		this.pathes = getIntent().getStringArrayListExtra(INTENT_VAR_PATHES);
		int currentindex = this.pathes.indexOf(path);
		Logger.log("View Image: " + path);
		this.viewFlipper.setOnTouchListener(new XViewTouchListener(currentindex, this.pathes));

		boolean result = setImage(R.id.xcamera_imageview, path);
		if (!result) {
			setImage(R.id.xcamera_imageview1, path);
		}
	}

	private boolean setImage(int imageviewResId, String imagePath) {
		ImageView imageview = (ImageView) findViewById(imageviewResId);
		if (imageview == null) {
			return false;
		}
		new ImageViewAsync(imagePath, imageview).execute();
		return true;
	}

}
