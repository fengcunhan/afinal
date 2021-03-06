/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tsz.afinal.bitmap.core;

import java.io.OutputStream;

import net.tsz.afinal.bitmap.core.BitmapCache.ImageCacheParams;
import net.tsz.afinal.bitmap.download.Downloader;
import net.tsz.afinal.core.FileNameGenerator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapProcess {
	private static final String TAG = "BitmapProcess";
	private boolean mHttpDiskCacheStarting = true;
	private int cacheSize;
	private static final int DEFAULT_CACHE_SIZE = 20 * 1024 * 1024; // 20MB

	private LruDiskCache mOriginalDiskCache;// 原始图片的路径，不进行任何的压缩操作
	private final Object mHttpDiskCacheLock = new Object();
	private static final int DISK_CACHE_INDEX = 0;

	private Downloader downloader;
<<<<<<< HEAD
	private Context mContext;
=======
	
	private boolean neverCalculate = false;
>>>>>>> e98e1925de374f757d390a892f1a26b4dee90e7c

	public BitmapProcess(Context context, Downloader downloader, int cacheSize) {
		this.mContext = context;
		this.downloader = downloader;
		if (cacheSize <= 0)
			cacheSize = DEFAULT_CACHE_SIZE;
		this.cacheSize = cacheSize;
	}
	
	public void configCalculateBitmap(boolean neverCalculate){
		this.neverCalculate = neverCalculate;
	}

	public Bitmap processBitmap(String data, BitmapDisplayConfig config) {
		final String key = FileNameGenerator.generator(data);
		Bitmap bitmap = null;
		synchronized (mHttpDiskCacheLock) {
			// Wait for disk cache to initialize
			while (mHttpDiskCacheStarting) {
				try {
					mHttpDiskCacheLock.wait();
				} catch (InterruptedException e) {
				}
			}

			if (mOriginalDiskCache != null) {
				OutputStream out = mOriginalDiskCache.getOutputStream(key);

				try {

					if (downloader.downloadToLocalStreamByUrl(data, out)) {
						bitmap = mOriginalDiskCache.get(key);
					} else {
						return null;
					}

				} catch (Exception e) {
					Log.e(TAG, "processBitmap - " + e);
				}
			}
		}

<<<<<<< HEAD
=======
		Bitmap bitmap = null;
		if (fileDescriptor != null) {
			if(neverCalculate)
				bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			else
				bitmap = BitmapDecoder.decodeSampledBitmapFromDescriptor(fileDescriptor, config.getBitmapWidth(),config.getBitmapHeight());
		}
		if (fileInputStream != null) {
			try {
				fileInputStream.close();
			} catch (IOException e) {
			}
		}
>>>>>>> e98e1925de374f757d390a892f1a26b4dee90e7c
		return bitmap;
	}

	/**
	 * 下载网络的原图，不做任何处理
	 * 
	 * @param data
	 * @return
	 */
	public Bitmap processBitmap(String data) {
		final String key = FileNameGenerator.generator(data);
		Bitmap bitmap = null;
		synchronized (mHttpDiskCacheLock) {
			// Wait for disk cache to initialize
			while (mHttpDiskCacheStarting) {
				try {
					mHttpDiskCacheLock.wait();
				} catch (InterruptedException e) {
				}
			}

			if (mOriginalDiskCache != null) {
				OutputStream out = mOriginalDiskCache.getOutputStream(key);
				try {
					if (downloader.downloadToLocalStreamByUrl(data, out)) {
						bitmap = mOriginalDiskCache.get(key);
					} else {
						return null;
					}
				} catch (Exception e) {
					Log.e(TAG, "processBitmap - " + e);
				}
			}
		}
		return bitmap;
	}

	public void initHttpDiskCache() {
		synchronized (mHttpDiskCacheLock) {
			try {
				ImageCacheParams imageParams=new ImageCacheParams(BitmapCommonUtils.getDiskCacheDir(mContext,
								"afinalCache"));
				mOriginalDiskCache = LruDiskCache.openCache(imageParams);
			} catch (Exception e) {
				mOriginalDiskCache = null;
			}
			mHttpDiskCacheStarting = false;
			mHttpDiskCacheLock.notifyAll();
		}
	}

	public void clearCacheInternal() {
		synchronized (mHttpDiskCacheLock) {
			if (mOriginalDiskCache != null) {
				try {
					mOriginalDiskCache.clearCache();
				} catch (Exception e) {
					Log.e(TAG, "clearCacheInternal - " + e);
				}
				mOriginalDiskCache = null;
				mHttpDiskCacheStarting = true;
				initHttpDiskCache();
			}
		}
	}
}
