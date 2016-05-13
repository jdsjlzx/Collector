package com.lindo.collector.basic;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.os.Bundle;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class FullScreenBaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	
	
	protected void saveCookie(AsyncHttpClient client) {
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client.setCookieStore(cookieStore);
	}

	protected List<Cookie> getCookie(){
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		List<Cookie> cookies = cookieStore.getCookies();
		return cookies;
	}
	
	public void clearCookie(){
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();
	}
}
