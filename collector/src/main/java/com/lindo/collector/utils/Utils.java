/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.lindo.collector.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.cookie.Cookie;

import com.lindo.collector.R;

/**
 * Class containing some static utility methods.
 */
public class Utils {
	public static final int IO_BUFFER_SIZE = 8 * 1024;

	protected static String TAG = "Utils";

	private Utils() {
	};

	/**
	 * Workaround for bug pre-Froyo, see here for more info:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (hasHttpConnectionBug()) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * Get the size in bytes of a bitmap.
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */
	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Check if external storage is built-in or removable.
	 *
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@SuppressLint("NewApi")
	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * Get the external app cache directory.
	 *
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@SuppressLint("NewApi")
	public static File getExternalCacheDir(Context context) {
		if (hasExternalCacheDir()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * Check how much usable space is available at a given path.
	 *
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	@SuppressLint("NewApi")
	public static long getUsableSpace(File path) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * Get the memory class of this device (approx. per-app memory limit)
	 *
	 * @param context
	 * @return
	 */
	public static int getMemoryClass(Context context) {
		return ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

	/**
	 * Check if OS version has a http URLConnection bug. See here for more
	 * information:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 *
	 * @return
	 */
	public static boolean hasHttpConnectionBug() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
	}

	/**
	 * Check if OS version has built-in external cache dir method.
	 *
	 * @return
	 */
	public static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * Check if ActionBar is available.
	 *
	 * @return
	 */
	public static boolean hasActionBar() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * 保存数据到sp
	 * 
	 * @param key
	 * @param value
	 */
	public static void savePreference(Context context, String key, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, value).commit();
	}

	/**
	 * 读取配置参数
	 * 
	 * @param mContext
	 * @param key
	 * @return
	 */
	public static String getPreference(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(key, "");
	}

	private static List<Cookie> cookies;

	public static List<Cookie> getCookies() {
		return cookies != null ? cookies : new ArrayList<Cookie>();
	}

	public static void setCookies(List<Cookie> cookies) {
		Utils.cookies = cookies;
	}

	/**
	 * 检查标签是否有效
	 * 
	 * @param context
	 * @param tag
	 * @param total
	 * @return
	 */
	public static String checkTagIsValid(Context context, String subTagIdText,
			int total) {
		String result = null;
		if (TextUtils.isEmpty(subTagIdText)) {
			result = context.getResources().getString(
					R.string.task_pic_tag_is_null);
		} else {
			String[] subTagIdArray = Utils.getStringArrayFromText(subTagIdText,
					",");
			if (subTagIdArray.length < total) {
				result = context.getResources().getString(
						R.string.task_pic_tag_count_is_not_complete);
			}
		}
		return result;
	}

	public static String[] getStringArrayFromText(String result,
			String regularExpression) {
		String[] textArray = null;
		if (!TextUtils.isEmpty(result)) {
			if (result.contains(regularExpression)) {
				textArray = result.split(regularExpression);
			} else {
				textArray = new String[1];
				textArray[0] = result;
			}
		}

		return textArray;
	}

	public static boolean isOdd(int i) {
		return i % 2 != 0;
	}

	/**
	 * 
	 * dip------>px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 获取屏幕像素宽度
	 * 
	 * @param context
	 * @return 返回屏幕像素宽度
	 */
	private static int screenWidth;

	public static int getScreenWidthPixels(Context context) {
		if (screenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(dm);
			screenWidth = dm.widthPixels;
		}

		return screenWidth;
	}

	private static int screenHeight;

	public static int getScreenHeightPixels(Context context) {
		if (screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(dm);
			screenHeight = dm.heightPixels;
		}

		return screenHeight;
	}

	/**
	 * 获取手机IMEI号
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getVersionName(Context context) {
		String versionName = "0";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);

			// 当前应用的版本名称
			versionName = info.versionName;

		} catch (Exception e) {
		}

		return versionName;
	}

	// 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
	public static boolean hasBind(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}

	public static void setBind(Context context, boolean flag) {
		String flagStr = "not";
		if (flag) {
			flagStr = "ok";
		}
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();
	}

	public static String getDateText(Long time) {
		SimpleDateFormat format = new SimpleDateFormat( "MM-dd HH:mm:ss", Locale.CHINA);
		String date = format.format(time * 1000);
		return date;
	}
}
