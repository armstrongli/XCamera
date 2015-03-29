package com.xxboy.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.common.DiskLruCache;
import com.xxboy.log.Logger;

public class XCacheUtil {
	private static final Long M_MEMORY_CACHE_SIZE = Runtime.getRuntime().maxMemory() / 4;// 1/4 of runtime max memory
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(M_MEMORY_CACHE_SIZE.intValue()) {
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getByteCount();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			if (oldValue != null && !oldValue.isRecycled()) {
				Logger.debug("Moving cache from memory cache to soft referene cache: " + key + ", old value: " + oldValue);
				XCacheUtil.pushToSoftCache(key, oldValue);
			}
			super.entryRemoved(evicted, key, oldValue, newValue);
		}

	};

	public static final Bitmap getFromCache(String id) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem != null && !bitmapFromMem.isRecycled()) {
			Logger.debug("hit in cache: memory cache");
			return bitmapFromMem;
		} else {
			Bitmap bitmapFromSoft = getFromSoftCache(id);
			if (bitmapFromSoft != null && !bitmapFromSoft.isRecycled()) {
				pushToMemCache(id, bitmapFromSoft);
				deleteFromSoftCache(id);
				Logger.debug("hit in cache: soft reference cache");
				return bitmapFromSoft;
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
	}

	public static final void pushToCache(String id, Bitmap bitmap) {
		Bitmap bitmapFromMem = getFromMemCache(id);
		if (bitmapFromMem == null) {
			pushToMemCache(id, bitmap);
		}
		Bitmap bitmapFromDisk = getFromDiskCache(id);
		if (bitmapFromDisk == null) {
			pushToDiskCache(id, bitmap);
		}
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
			if (bitmap != null && !bitmap.isRecycled()) {
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
	private static LruCache<String, SoftReference<Bitmap>> xSoftCache = new LruCache<String, SoftReference<Bitmap>>(M_DISK_CACHE_SIZE.intValue()) {

		@Override
		protected int sizeOf(String key, SoftReference<Bitmap> value) {
			if (value == null) {
				return 0;
			} else {
				Bitmap b = value.get();
				if (b == null || b.isRecycled()) {
					return 0;
				} else {
					return b.getByteCount();
				}
			}
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, SoftReference<Bitmap> oldValue, SoftReference<Bitmap> newValue) {
			if (oldValue != null) {
				Bitmap oldBitmap = oldValue.get();
				if (oldBitmap != null && !oldBitmap.isRecycled()) {
					oldBitmap.recycle();
				}
			}
			super.entryRemoved(evicted, key, oldValue, newValue);
		}

	};
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
			Logger.debug("Getting from softcache(" + xSoftCache.size() + "): " + id);
			String xkey = hashKeyForDisk(id);
			SoftReference<Bitmap> softCache = xSoftCache.get(xkey);
			if (softCache == null) {
				Logger.debug("Getting from softcache NONE " + id);
				xSoftCache.remove(xkey);
				return null;
			}
			Bitmap bitmap = softCache.get();
			if (bitmap == null || bitmap.isRecycled()) {
				Logger.debug("Bitmap has been recycled: " + id);
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
		Logger.debug("Pushing to soft reference cache: " + id);
		String xkey = hashKeyForDisk(id);
		Logger.debug("Soft reference cache size is: " + xSoftCache.size());
		xSoftCache.put(xkey, new SoftReference<Bitmap>(bitmap));
		Logger.debug("Soft reference cache size up to: " + xSoftCache.size());
	}

	/**
	 * remove cache from softreference cache
	 * 
	 * @param id
	 */
	private static final void deleteFromSoftCache(String id) {
		String xkey = hashKeyForDisk(id);
		xSoftCache.remove(xkey);
	}

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
