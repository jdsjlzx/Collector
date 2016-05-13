package com.lindo.collector.basic;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.lzx.work.R;

public class Base2Activity extends Activity {

	private final String TAG = "Base2Activity";
	protected ImageView mBackBtn;
	protected TextView mTitleText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.push_bottom_in,
				R.anim.push_bottom_out);
	}
	
	protected void saveCookie(AsyncHttpClient client) {
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client.setCookieStore(cookieStore);
	}

	protected List<Cookie> getCookie(){
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		List<Cookie> cookies = cookieStore.getCookies();
		Log.d(TAG, "Base2Activity getCookies().size() " + cookies.size());
		return cookies;
	}
	
	public void clearCookie(){
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		cookieStore.clear();
	}
}
