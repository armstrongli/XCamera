package com.xxboy.activities.mainview.asynctasks;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable {
	private final WeakReference<ImageItemAsync> bitmapTaskReference;

	public AsyncDrawable(Bitmap bitmap, ImageItemAsync imageItemAsync) {
		super(bitmap);
		this.bitmapTaskReference = new WeakReference<ImageItemAsync>(imageItemAsync);
	}

	public WeakReference<ImageItemAsync> getBitmapTaskReference() {
		return bitmapTaskReference;
	}

}
