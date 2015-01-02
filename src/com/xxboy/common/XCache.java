package com.xxboy.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.xxboy.log.Logger;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XCache {
	private static final int M_MEMORY_CACHE_SIZE = 10 * 1024 * 1024;// 10M
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(M_MEMORY_CACHE_SIZE) {
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getByteCount();
		}
	};

	public static void push2MemCache(String id, Bitmap bitmap) {
		Logger.log("Pushing " + id);
		if (getFromMemCache(id) == null) {
			mMemoryCache.put(id, bitmap);
		}
	}

	public static Bitmap getFromMemCache(String id) {
		Logger.log("Getting " + id);
		return mMemoryCache.get(id);
	}

	private static final int M_DISK_CACHE_SIZE = 20 * 1024 * 1024;// 20M
	private static DiskLruCache mDiskCache;

	public static Bitmap getFromDiskCache(String id) {
		try {
			DiskLruCache.Editor editor = getDiskCache().edit(hashKeyForDisk(id));
			if (editor != null) {
				InputStream input = editor.newInputStream(0);
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				if (input != null) {
					input.close();
				}
				return bitmap;
			}
		} catch (IOException e) {
			Logger.log(e.getMessage(), e);
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
		return null;
	}

	public static void push2DiskCache(String id, Bitmap bitmap) {
		Bitmap cachedBitmap = getFromDiskCache(id);
		if (cachedBitmap != null) {
			return;
		}
		try {
			DiskLruCache.Editor editor = getDiskCache().edit(hashKeyForDisk(id));
			if (editor != null) {
				OutputStream outputStream = editor.newOutputStream(0);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
				editor.commit();
				if (outputStream != null) {
					outputStream.close();
				}

				getDiskCache().flush();
			}
		} catch (IOException e) {
			Logger.log(e.getMessage(), e);
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
	}

	private static final DiskLruCache getDiskCache() {
		if (mDiskCache == null || mDiskCache.isClosed()) {
			try {
				mDiskCache = DiskLruCache.open(new File(XCameraConst.GLOBAL_X_CACHE_PATH), 1, 1, M_DISK_CACHE_SIZE);
			} catch (IOException e) {
				Logger.log(e.getMessage(), e);
			}
		}
		return mDiskCache;
	}

	private static final String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static final String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
