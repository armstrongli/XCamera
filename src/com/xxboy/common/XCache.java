package com.xxboy.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

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

		@Override
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			if (!oldValue.isRecycled()) {
				oldValue.recycle();
			}
			super.entryRemoved(evicted, key, oldValue, newValue);
		}

	};

	public static final Bitmap getFromCache(String id) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem != null) {
			return bitmapFromMem;
		} else {
			Bitmap bitmapFromSoft = getFromSoftCache(id);
			if (bitmapFromSoft != null && !bitmapFromSoft.isRecycled()) {
				pushToMemCache(id, bitmapFromSoft);
				return bitmapFromSoft;
			} else {
				Bitmap bitmapFromDisk = getFromDiskCache(id);
				if (bitmapFromDisk != null) {
					pushToMemCache(id, bitmapFromDisk);
					pushToSoftCache(id, bitmapFromDisk);
					return bitmapFromDisk;
				} else {
					return null;
				}
			}
		}
	}

	public static final void pushToCache(String id, Bitmap bitmap) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem == null) {
			pushToMemCache(id, bitmap);
		}
		Bitmap bitmapFromSoft = getFromSoftCache(id);
		if (bitmapFromSoft == null) {
			pushToSoftCache(id, bitmap);
		}
		Bitmap bitmapFromDisk = getFromDiskCache(id);
		if (bitmapFromDisk == null) {
			pushToDiskCache(id, bitmap);
		}
	}

	public static void pushToMemCache(String id, Bitmap bitmap) {
		Logger.log("Pushing " + id);
		mMemoryCache.put(hashKeyForDisk(id), bitmap);
	}

	public static Bitmap getFromMemCache(String id) {
		Logger.log("Getting From memcache(" + mMemoryCache.size() + "): " + id);
		// check whether it's in memory cache
		return mMemoryCache.get(hashKeyForDisk(id));
	}

	private static final long M_DISK_CACHE_SIZE = 20 * 1024 * 1024;// 20M
	private static ConcurrentHashMap<String, SoftReference<Bitmap>> xSoftCache = null;
	private static DiskLruCache mDiskCache;

	private static final Bitmap getFromSoftCache(String id) {
		Logger.log("Getting from softcache(" + xSoftCache.size() + "): " + id);
		SoftReference<Bitmap> softCache = xSoftCache.get(hashKeyForDisk(id));
		return softCache != null ? softCache.get() : null;
	}

	private static final void pushToSoftCache(String id, Bitmap bitmap) {
		xSoftCache.put(hashKeyForDisk(id), new SoftReference<Bitmap>(bitmap));
	}

	private static Bitmap getFromDiskCache(String id) {
		Logger.log("Getting from diskCache: " + id);
		try {
			DiskLruCache.Snapshot snapshot = getDiskCache().get(hashKeyForDisk(id));
			if (snapshot != null) {
				InputStream bitmapInputStream = snapshot.getInputStream(0);
				try {
					return BitmapFactory.decodeStream(bitmapInputStream);
				} finally {
					if (bitmapInputStream != null) {
						bitmapInputStream.close();
					}
				}
			}
		} catch (IOException e) {
			Logger.log(e.getMessage(), e);
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
		return null;
	}

	private static void pushToDiskCache(String id, Bitmap bitmap) {
		Logger.log("Pushing to disk cache: " + id);
		try {
			String editorKey = hashKeyForDisk(id);
			Logger.log("Editor key is: " + editorKey);
			DiskLruCache.Editor editor = getDiskCache().edit(editorKey);
			if (editor != null) {
				OutputStream outputStream = editor.newOutputStream(0);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
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
				Logger.log("The cache version is: " + XCameraConst.VERSION);
				mDiskCache = DiskLruCache.open(new File(XCameraConst.GLOBAL_X_CACHE_PATH), XCameraConst.VERSION, 1, M_DISK_CACHE_SIZE);
			} catch (IOException e) {
				Logger.log(e.getMessage(), e);
			}
		}
		return mDiskCache;
	}

	private static MessageDigest mDigest = null;
	static {
		try {
			mDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Logger.log(e.getMessage(), e);
		}

		try {
			xSoftCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(1024);
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
	}

	private static final String hashKeyForDisk(String key) {
		String cacheKey;
		mDigest.update(key.getBytes());
		cacheKey = bytesToHexString(mDigest.digest());
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

	public static final void closeDiskCache() {
		try {
			if (!mDiskCache.isClosed()) {
				mDiskCache.close();
			}
		} catch (IOException e) {
			Logger.log("Error when closing disk cache: " + e.getMessage(), e);
		} catch (Exception e) {
			Logger.log("Error when closing disk cache: " + e.getMessage(), e);
		}
	}
}
