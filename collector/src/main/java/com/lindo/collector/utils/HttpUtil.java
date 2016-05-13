package com.lindo.collector.utils;

import org.apache.http.Header;

import android.content.Context;
import android.util.Log;

import com.lindo.collector.http.FinalAsyncHttpClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {

	private static final String TAG = "HttpUtil";
	public static void pushBaiduUserId(Context context, final String userId){
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(context);
		params.put("push_id", userId);
		client.get(Constant.USER_MODIFY_INFO_URL, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String data = new String(arg2);
				Log.d(TAG, "pushBaiduChannleId onSuccess data " + data);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Log.e(TAG, " onFailure " + new String(arg2));
			}
		});
	}
	
	
}
