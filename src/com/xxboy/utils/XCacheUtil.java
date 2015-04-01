package com.xxboy.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.common.DiskLruCache;
import com.xxboy.log.Logger;

public class XCacheUtil {
	private static final Long M_MEMORY_CACHE_SIZE = Runtime.getRuntime().maxMemory() / 6;// 1/6 of runtime max memory
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(M_MEMORY_CACHE_SIZE.intValue()) {
		@Override
		protected int sizeOf(String key, Bitmap value) {
			if (value == null || value.isRecycled()) {
				mMemoryCache.remove(key);
				return 0;
			} else {
				return value.getByteCount();
			}
		}

	};

	public static final Bitmap getFromCache(String id) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem != null && !bitmapFromMem.isRecycled()) {
			Logger.debug("hit in cache: memory cache");
			return bitmapFromMem;
		} else {
			Bitmap bitmapFromDisk = getFromDiskCache(id);
			if (bitmapFromDisk != null && !bitmapFromDisk.isRecycled()) {
				pushToMemCache(id, bitmapFromDisk);
				Logger.debug("hit in cache: disk cache");
				return bitmapFromDisk;
			} else {
				return null;
			}
		}
	}

	/**
	 * push to mem cache & disk cache
	 * 
	 * @param id
	 * @param bitmap
	 */
	public static final Bitmap pushToCache(String id, Bitmap bitmap) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem == null) {
			pushToMemCache(id, bitmap);
		}
		Bitmap bitmapFromDisk = getFromDiskCache(id);
		if (bitmapFromDisk == null) {
			pushToDiskCache(id, bitmap);
		}
		return bitmap;
	}

	public static void pushToMemCache(String id, Bitmap bitmap) {
		Logger.debug("Pushing to memory cache: " + id);
		try {
			mMemoryCache.put(hashKeyForDisk(id), bitmap);
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
	}

	// -- APIs for image view
	private static final String FULL_IMAGEVIEW_PREFIX = "FULL_IMAGEVIEW:";

	public static final Bitmap getImaveView(String id) {
		String fullImageViewId = FULL_IMAGEVIEW_PREFIX + id;
		return getFromDiskCache(fullImageViewId);
	}

	public static final void pushImageView(String id, Bitmap bitmap) {
		String fullImageViewId = FULL_IMAGEVIEW_PREFIX + id;
		pushToDiskCache(fullImageViewId, bitmap);
	}

	/**
	 * getting bitmap from hard memory cache.
	 * 
	 * @param id
	 *            the primary path id of image.
	 * @return
	 */
	public static Bitmap getFromMemCache(String id) {
		try {
			Logger.debug("Getting From memcache(" + mMemoryCache.size() + "): " + id);
			// check whether it's in memory cache
			Bitmap bitmap = mMemoryCache.get(hashKeyForDisk(id));
			if (bitmap != null && bitmap.isRecycled()) {
				Logger.debug("hit in cache: memory cache");
				return bitmap;
			}
			return null;
		} catch (Exception e) {
			Logger.log("Error when getting bitmap from memory cache with id: " + id, e);
		}
		return null;
	}

	private static final Long M_DISK_CACHE_SIZE = 20l * 1024l * 1024l;// 20M
	private static DiskLruCache mDiskCache;

	private static Bitmap getFromDiskCache(String id) {
		Logger.debug("Getting from diskCache: " + id);
		try {
			DiskLruCache.Snapshot snapshot = getDiskCache().get(hashKeyForDisk(id));
			if (snapshot != null) {
				InputStream bitmapInputStream = snapshot.getInputStream(0);
				try {
					return BitmapFactory.decodeStream(bitmapInputStream);
				} catch (Exception e) {
					Logger.log(e.getMessage(), e);
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
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		Logger.debug("Pushing to disk cache: " + id);
		try {
			String editorKey = hashKeyForDisk(id);
			Logger.debug("Editor key is: " + editorKey);
			DiskLruCache.Editor editor = getDiskCache().edit(editorKey);
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
				Logger.debug("The cache version is: " + XCameraConst.VERSION);
				mDiskCache = DiskLruCache.open(new File(XCameraConst.GLOBAL_X_CACHE_PATH), XCameraConst.VERSION, 1, M_DISK_CACHE_SIZE);
			} catch (IOException e) {
				Logger.log(e.getMessage(), e);
			}
		}
		return mDiskCache;
	}

	private static final String hashKeyForDisk(String key) {
		String cacheKey;
		MessageDigest mDigest = getMsgDgst();
		mDigest.update(key.getBytes());
		cacheKey = bytesToHexString(mDigest.digest());
		return cacheKey;
	}

	private static final MessageDigest getMsgDgst() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Logger.log(e);
		}
		return null;
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
