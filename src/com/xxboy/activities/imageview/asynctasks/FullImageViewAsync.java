package com.xxboy.activities.imageview.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.xxboy.async.XBitmapAsyncTask;
import com.xxboy.log.Logger;

public class FullImageViewAsync extends XBitmapAsyncTask {

	public FullImageViewAsync(String path, ImageView imageView) {
		super(path, imageView);
	}

	@Override
	protected Bitmap doInBackground() {
		try {
			return BitmapFactory.decodeFile(super.getImagePath());
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	protected void postExecute(Bitmap result) {
		super.postExecute(result);
	}

}
