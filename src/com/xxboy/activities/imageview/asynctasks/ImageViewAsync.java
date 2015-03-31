package com.xxboy.activities.imageview.asynctasks;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xxboy.activities.imageview.runnables.ImageViewLoader;
import com.xxboy.log.Logger;
import com.xxboy.utils.XBitmapUtil;
import com.xxboy.utils.XCacheUtil;
import com.xxboy.utils.XQueueUtil;

public class ImageViewAsync extends AsyncTask<Void, Void, Void> {

	private static final class ImageTaskArray {
		private static Object arrayLock = new Object();
		private static LinkedList<Integer> array = new LinkedList<Integer>();

		private static boolean checkExists(Integer checked) {
			return array.indexOf(checked) >= 0;
		}

		private static boolean addToArray(Integer i) {
			boolean exists = checkExists(i);
			if (exists) {
				return false;
			} else {
				synchronized (arrayLock) {
					array.addFirst(i);
					array.removeLast();
				}
				return true;
			}
		}
	}

	private int position;
	private String path;
	private ImageView imageView;

	public ImageViewAsync(int position, String path, ImageView imageView) {
		this.position = position;
		this.path = path;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(Void... params) {
		boolean successAdded = ImageTaskArray.addToArray(this.position);
		if (successAdded) {
			Bitmap bitmap = XCacheUtil.getImaveView(this.path);
			if (bitmap != null && !bitmap.isRecycled() && (bitmap.getWidth() + bitmap.getHeight() > 0)) {
				XQueueUtil.executeTaskDirectly(new ImageViewLoader(this.path, imageView));
			} else {
				bitmap = getImage(this.path);
				XQueueUtil.executeTaskDirectly(new ImageViewLoader(this.path, imageView));
				XCacheUtil.pushImageView(this.path, bitmap);
			}
		} else {
			Bitmap bitmap = null;
			while (bitmap == null) {
				Logger.log("load gallery : " + this.path + this.imageView);
				bitmap = XCacheUtil.getImaveView(this.path);
				if (bitmap != null && (bitmap.getWidth() + bitmap.getHeight() > 0)) {
					break;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Logger.log(e);
				}
			}
			XQueueUtil.executeTaskDirectly(new ImageViewLoader(this.path, imageView));
		}
		return null;
	}

	private Bitmap getImage(String imagePath) {
		return BitmapFactory.decodeFile(this.path, XBitmapUtil.getImageViewOption(imagePath));
	}

}
