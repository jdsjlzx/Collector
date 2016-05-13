package com.lindo.collector.utils;

import android.util.Log;

import com.lzx.work.utils.StringUtils;

public class LogUtils {
	public static boolean DEBUG = true;
	public static void i(String tag, String msg) {

		if (DEBUG) {
			Log.i(tag, !StringUtils.isEmpty(msg)?msg:"null");
		}
	}
	public static void d(String tag, String msg) {
		
		if (DEBUG) {
			Log.d(tag, !StringUtils.isEmpty(msg)?msg:"null");
		}
	}
	public static void printf(String msg) {
		
		if (DEBUG) {
			System.out.println(!StringUtils.isEmpty(msg)?msg:"null");
		}
	}

}
