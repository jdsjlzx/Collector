package com.lindo.collector.http;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import android.content.Context;

import com.lindo.collector.utils.MD5;
import com.lindo.collector.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FinalAsyncHttpClient {

	AsyncHttpClient client;
	
	public FinalAsyncHttpClient() {
		client = new AsyncHttpClient();
		client.setConnectTimeout(5);//5s超时
		if (Utils.getCookies() != null) {
			BasicCookieStore bcs = new BasicCookieStore();
			bcs.addCookies(Utils.getCookies().toArray(
					new Cookie[Utils.getCookies().size()]));
			client.setCookieStore(bcs);
		}
	}
	
	public RequestParams getCommonRequestParams(Context context) {
		RequestParams params = new RequestParams();
		params.put("dpi", Utils.getScreenWidthPixels(context) + "x"
						+ Utils.getScreenHeightPixels(context));
		params.put("version", Utils.getVersionName(context));
		params.put("deviceType", "0");
		params.put("imei", Utils.getIMEI(context));
		params.put("phoneModel", android.os.Build.MODEL);
		params.put("osVersion", android.os.Build.VERSION.RELEASE);
		params.put("authCode", getAuthCode("version","deviceType","imei","dpi","phoneModel","osVersion"));
		return params;
	}
	
	public AsyncHttpClient getAsyncHttpClient(){
		return this.client;
	}
	
	private String getAuthCode(String... array){
		StringBuffer sb = new StringBuffer();
		MD5 md5 = new MD5();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}
		return md5.md5(sb.toString());
	}
}
