package com.lzx.work.utils;

import org.apache.http.message.BasicNameValuePair;

import com.lzx.work.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtil {
	
	public static void startActivity(Activity activity, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}
	
	public static void startActivity(Activity activity, Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	public static void startActivity(Activity activity, Class<?> cls, BasicNameValuePair...name) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		for(int i=0;i<name.length;i++)
		{
			intent.putExtra(name[i].getName(), name[i].getValue());
		}
		activity.startActivity(intent);
		
		activity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}
	
	public static void startActivity2(Activity activity, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_top_in,
				R.anim.push_top_out);
	}
}
