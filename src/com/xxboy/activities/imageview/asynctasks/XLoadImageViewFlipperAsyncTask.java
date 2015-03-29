package com.xxboy.activities.imageview.asynctasks;

import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.xxboy.log.Logger;
import com.xxboy.utils.XQueueUtil;

public class XLoadImageViewFlipperAsyncTask extends XLoadImageViewAsyncTask {
	private ViewFlipper viewFlipper;

	public XLoadImageViewFlipperAsyncTask(String path, ViewFlipper viewFlipper, ImageView imageView) {
		this(path, imageView);
		this.viewFlipper = viewFlipper;
	}

	private XLoadImageViewFlipperAsyncTask(String path, ImageView imageView) {
		super(path, imageView);
	}

	@Override
	protected Void doInBackground(Void... params) {
		super.doInBackground(params);
		XQueueUtil.executeTaskDirectly(new Runnable() {
			@Override
			public void run() {
				Logger.log("Load to Next Flipper");
				viewFlipper.showNext();
			}
		});
		// viewFlipper.getHandler().post(new Runnable() {
		// @Override
		// public void run() {
		// Logger.log("Load to Next Flipper");
		// viewFlipper.showNext();
		// }
		// });
		return null;
	}
}
