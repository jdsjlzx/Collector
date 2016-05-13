package com.lzx.work.utils;

import android.util.Log;


public class LogUtils {

	public static void i(String tag, String msg) {

		if (AppConstant.DEBUG) {
			Log.i(tag, !StringUtils.isEmpty(msg)?msg:"null");
		}
	}
	public static void d(String tag, String msg) {
		
		if (AppConstant.DEBUG) {
			Log.d(tag, !StringUtils.isEmpty(msg)?msg:"null");
		}
	}
	public static void printf(String msg) {
		
		if (AppConstant.DEBUG) {
			System.out.println(!StringUtils.isEmpty(msg)?msg:"null");
		}
	}

}
