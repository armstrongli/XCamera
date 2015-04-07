package com.xxboy.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.xxboy.activities.mainview.XCamera.XCameraConst;
import com.xxboy.common.DiskLruCache;
import com.xxboy.log.Logger;

public class XCacheUtil {
	private static final Long M_MEMORY_CACHE_SIZE = Runtime.getRuntime().maxMemory() / 4;// 1/6 of runtime max memory
	private static LruCache<Integer, Bitmap> mMemoryCache = new LruCache<Integer, Bitmap>(M_MEMORY_CACHE_SIZE.intValue()) {
		@Override
		protected int sizeOf(Integer key, Bitmap value) {
			if (value == null || value.isRecycled()) {
				mMemoryCache.remove(key);
				return 0;
			} else {
				return value.getByteCount();
			}
		}

		@Override
		protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
			Logger.log(this.toString());
			if (evicted) {
				Bitmap bitmapInDisk = getFromDiskCache(key);
				if (bitmapInDisk == null) {
					pushToDiskCache(key, oldValue);
				}
			}
		}

	};

	public static final Bitmap getFromCache(final String path) {
		final int hashKey = HashKeyUtil.hashKey(path);
		final Bitmap bitmapFromMem = getFromMemCache(path);
		if (bitmapFromMem != null && !bitmapFromMem.isRecycled()) {
			Logger.log("hit in cache: memory cache");
			return bitmapFromMem;
		} else {
			final Bitmap bitmapFromDisk = getFromDiskCache(path);
			if (bitmapFromDisk != null && !bitmapFromDisk.isRecycled()) {
				pushToMemCache(hashKey, bitmapFromDisk);
				Logger.log("hit in cache: disk cache");
				return bitmapFromDisk;
			} else {
				Logger.log("hit empty " + path);
				return null;
			}
		}
	}

	/**
	 * push to mem cache & disk cache
	 * 
	 * @param path
	 * @param bitmap
	 */
	public static final Bitmap pushToCache(final String path, final Bitmap bitmap) {
		final int hashKey = HashKeyUtil.hashKey(path);
		Bitmap bitmapFromMem = getFromMemCache(path);
		if (bitmapFromMem == null) {
			pushToMemCache(hashKey, bitmap);
		}
		Bitmap bitmapFromDisk = getFromDiskCache(path);
		if (bitmapFromDisk == null) {
			pushToDiskCache(hashKey, bitmap);
		}
		return bitmap;
	}

	private static void pushToMemCache(final Integer hashKey, final Bitmap bitmap) {
		Logger.debug("Pushing to memory cache: " + hashKey);
		try {
			mMemoryCache.put(hashKey, bitmap);
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

	public static final void pushImageView(final String id, final Bitmap bitmap) {
		final String fullImageViewId = FULL_IMAGEVIEW_PREFIX + id;
		final int hashKey = HashKeyUtil.hashKey(fullImageViewId);
		pushToDiskCache(hashKey, bitmap);
	}

	/**
	 * getting bitmap from hard memory cache.
	 * 
	 * @param path
	 *            the primary path id of image.
	 * @return
	 */
	public static Bitmap getFromMemCache(String path) {
		try {
			Logger.debug("Getting From memcache(" + mMemoryCache.size() + "): " + path);
			// check whether it's in memory cache
			Bitmap bitmap = mMemoryCache.get(HashKeyUtil.hashKey(path));
			if (bitmap != null && bitmap.isRecycled()) {
				Logger.debug("hit in cache: memory cache");
				return bitmap;
			}
			return null;
		} catch (Exception e) {
			Logger.log("Error when getting bitmap from memory cache with id: " + path, e);
		}
		return null;
	}

	private static final Long M_DISK_CACHE_SIZE = 20l * 1024l * 1024l;// 20M
	private static DiskLruCache mDiskCache;

	private static Bitmap getFromDiskCache(String path) {
		Logger.debug("Getting from diskCache: " + path);
		return getFromDiskCache(HashKeyUtil.hashKey(path));
	}

	private static Bitmap getFromDiskCache(final Integer hashKey) {

		try {
			DiskLruCache.Snapshot snapshot = getDiskCache().get(hashKey.toString());
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

	private static void pushToDiskCache(final Integer hashKey, final Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		Logger.debug("Pushing to disk cache: " + hashKey);
		try {
			Logger.debug("Editor key is: " + hashKey);
			DiskLruCache.Editor editor = getDiskCache().edit(hashKey.toString());
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

	private static final class HashKeyUtil {
		public static final Integer hashKey(String path) {
			return path.hashCode();
		}

		// private static final String hashKeyForDisk(String key) {
		// String cacheKey;
		// MessageDigest mDigest = getMsgDgst();
		// mDigest.update(key.getBytes());
		// cacheKey = bytesToHexString(mDigest.digest());
		// return cacheKey;
		// }
		//
		// private static final MessageDigest getMsgDgst() {
		// try {
		// return MessageDigest.getInstance("MD5");
		// } catch (NoSuchAlgorithmException e) {
		// Logger.log(e);
		// }
		// return null;
		// }
		//
		// private static final String bytesToHexString(byte[] bytes) {
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < bytes.length; i++) {
		// String hex = Integer.toHexString(0xFF & bytes[i]);
		// if (hex.length() == 1) {
		// sb.append('0');
		// }
		// sb.append(hex);
		// }
		// return sb.toString();
		// }
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
