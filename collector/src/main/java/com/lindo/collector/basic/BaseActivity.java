package com.lindo.collector.basic;


import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.lzx.work.R;

public class BaseActivity extends Activity {

	protected ImageView mBackBtn;
	protected TextView mTitleText;
	
	protected ProgressDialog mDialog;
	
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
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	protected void creatDialog(EditText editText, String title, String msg) {
		if (null != editText) {
			hideKeyboard(editText);
		}
		
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(msg);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
		
	}
	
	protected void closeDialog(){
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
	
	// 隐藏虚拟键盘
	protected void hideKeyboard(EditText editText) {
		InputMethodManager imm = (InputMethodManager) (getApplicationContext())
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
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
