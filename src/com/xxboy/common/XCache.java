package com.xxboy.common;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

public class XCache {
	private static final int HARD_CACHE_CAPACITY = 16;
	// Hard cache, with a fixed maximum capacity and a life duration
	private static final Map<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY, 0.75f, true) {
		private static final long serialVersionUID = -57738079457331894L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else {
				return false;
			}
		}
	};
	// Soft cache for bitmap kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY);

	public Bitmap getBitmap(String id) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(id);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(id);
				sHardBitmapCache.put(id, bitmap);
				return bitmap;
			} else {
				// Then try the soft reference cache
				SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(id);
				if (bitmapReference != null) {
					final Bitmap bitmap1 = bitmapReference.get();
					if (bitmap1 != null) {
						// Bitmap found in soft cache
						return bitmap1;
					} else {
						// Soft reference has been Garbage Collected
						sSoftBitmapCache.remove(id);
					}
				}
			}
		}
		return null;
	}

	public void putBitmap(String id, Bitmap bitmap) {
		synchronized (sHardBitmapCache) {
			if (sHardBitmapCache != null) {
				sHardBitmapCache.put(id, bitmap);
			}
		}
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	private LruCache<String, Bitmap> mMemoryCache;

	public void loadBitmap(int resId, ImageView imageView) {
		// final String imageKey = String.valueOf(resId);
		// final Bitmap bitmap = getBitmapFromMemCache(imageKey);
		// if (bitmap != null) {
		// mImageView.setImageBitmap(bitmap);
		// } else {
		// mImageView.setImageResource(R.drawable.image_placeholder); // Ä¬ÈÏÍ¼Æ¬
		// BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
		// task.execute(resId);
		// }
	}
}
