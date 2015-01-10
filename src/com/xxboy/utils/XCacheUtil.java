package com.xxboy.utils;

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

import com.xxboy.common.DiskLruCache;
import com.xxboy.log.Logger;
import com.xxboy.xcamera.XCamera.XCameraConst;

public class XCacheUtil {
	private static final Long M_MEMORY_CACHE_SIZE = Runtime.getRuntime().maxMemory() / 4;// 10M
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(M_MEMORY_CACHE_SIZE.intValue()) {
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getByteCount();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			if (oldValue != null && !oldValue.isRecycled()) {
				XCacheUtil.pushToSoftCache(key, oldValue);
			}
			super.entryRemoved(evicted, key, oldValue, newValue);
		}

	};

	public static final Bitmap getFromCache(String id) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem != null && !bitmapFromMem.isRecycled()) {
			return bitmapFromMem;
		} else {
			Bitmap bitmapFromSoft = getFromSoftCache(id);
			if (bitmapFromSoft != null && !bitmapFromSoft.isRecycled()) {
				pushToMemCache(id, bitmapFromSoft);
				deleteFromSoftCache(id);
				return bitmapFromSoft;
			} else {
				Bitmap bitmapFromDisk = getFromDiskCache(id);
				if (bitmapFromDisk != null && !bitmapFromDisk.isRecycled()) {
					pushToMemCache(id, bitmapFromDisk);
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
		// Bitmap bitmapFromSoft = getFromSoftCache(id);
		// if (bitmapFromSoft == null) {
		// pushToSoftCache(id, bitmap);
		// }
		Bitmap bitmapFromDisk = getFromDiskCache(id);
		if (bitmapFromDisk == null) {
			pushToDiskCache(id, bitmap);
		}
	}

	public static void pushToMemCache(String id, Bitmap bitmap) {
		Logger.log("Pushing to memory cache: " + id);
		try {
			mMemoryCache.put(hashKeyForDisk(id), bitmap);
		} catch (Exception e) {
			Logger.log(e.getMessage(), e);
		}
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
			Logger.log("Getting From memcache(" + mMemoryCache.size() + "): " + id);
			// check whether it's in memory cache
			Bitmap bitmap = mMemoryCache.get(hashKeyForDisk(id));
			return (bitmap != null && !bitmap.isRecycled()) ? bitmap : null;
		} catch (Exception e) {
			Logger.log("Error when getting bitmap from memory cache with id: " + id, e);
		}
		return null;
	}

	private static final long M_DISK_CACHE_SIZE = 20 * 1024 * 1024;// 20M
	private static ConcurrentHashMap<String, SoftReference<Bitmap>> xSoftCache = null;
	private static DiskLruCache mDiskCache;

	/**
	 * getting bitmap from soft reference
	 * 
	 * @param id
	 *            image path
	 * @return
	 */
	private static final Bitmap getFromSoftCache(String id) {
		try {
			Logger.log("Getting from softcache(" + xSoftCache.size() + "): " + id);
			if (!xSoftCache.containsKey(hashKeyForDisk(id))) {
				return null;
			}
			SoftReference<Bitmap> softCache = xSoftCache.get(hashKeyForDisk(id));
			if (softCache == null) {
				return null;
			}
			Bitmap bitmap = softCache.get();
			if (bitmap == null || bitmap.isRecycled()) {
				Logger.log("Bitmap has been recycled: " + id);
				return null;
			} else {
				return bitmap;
			}
		} catch (Exception e) {
			Logger.log("Error when getting bitmap from soft reference cache: " + id, e);
		}
		return null;
	}

	private static final void pushToSoftCache(String id, Bitmap bitmap) {
		Logger.log("Pushing to soft reference cache: " + id);
		xSoftCache.put(hashKeyForDisk(id), new SoftReference<Bitmap>(bitmap));
	}

	/**
	 * remove cache from softreference cache
	 * 
	 * @param id
	 */
	private static final void deleteFromSoftCache(String id) {
		xSoftCache.remove(hashKeyForDisk(id));
	}

	private static Bitmap getFromDiskCache(String id) {
		Logger.log("Getting from diskCache: " + id);
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
		Logger.log("Pushing to disk cache: " + id);
		try {
			String editorKey = hashKeyForDisk(id);
			Logger.log("Editor key is: " + editorKey);
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
