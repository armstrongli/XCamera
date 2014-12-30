package com.xxboy.common;

import android.graphics.Bitmap;
import android.util.LruCache;

public class XCache {
	private static final int M_MEMORY_CACHE_SIZE = 10 * 1024 * 1024;// 10M
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(M_MEMORY_CACHE_SIZE) {
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getByteCount();
		}
	};

	public static void push2MemCache(String id, Bitmap bitmap) {
		if (getFromMemCache(id) == null) {
			mMemoryCache.put(id, bitmap);
		}
	}

	public static Bitmap getFromMemCache(String id) {
		return mMemoryCache.get(id);
	}

	private static final int M_DISK_CACHE_SIZE = 20 * 1024 * 1024;// 20M
}
