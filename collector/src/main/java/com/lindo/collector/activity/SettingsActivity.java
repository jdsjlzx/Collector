package com.lindo.collector.activity;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzx.work.activity.CommonWebActivity;
import com.lzx.work.utils.IntentUtil;

public class SettingsActivity extends BaseActivity implements OnClickListener{

	private TextView mAboutUsText;//关于我们
	private TextView mUserLogOutText;//注销
	private View mHorizontalLineView;//分隔符
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initView();
		initData();
		
	}
	
	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mAboutUsText = (TextView)findViewById(R.id.about_us_text);
		mUserLogOutText = (TextView)findViewById(R.id.user_logout_text);
		mHorizontalLineView = findViewById(R.id.horizontal_line);
	}
	
	private void initData() {
		mTitleText.setText(R.string.app_settings);
		
		mAboutUsText.setOnClickListener(this);
		mUserLogOutText.setOnClickListener(this);
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		if (PushApplication.getInstance().isLogin()) {
			mUserLogOutText.setVisibility(View.VISIBLE);
			mHorizontalLineView.setVisibility(View.VISIBLE);
		} else {
			mUserLogOutText.setVisibility(View.GONE);
			mHorizontalLineView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_us_text:
			IntentUtil.startActivity(this, CommonWebActivity.class,
					new BasicNameValuePair("url", "http://www.baidu.com"), 
					new BasicNameValuePair("title", getResources().getString(R.string.about_us)));
			break;
		case R.id.user_logout_text:
			logoutUser();
			PushApplication.getInstance().logoutUser();
			finish();
			break;

		default:
			break;
		}
	}
	
	private void logoutUser() {
		String url = Constant.USER_LOGOUT_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {
				
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				String json = new String(response);
				
			}
		});
	}
}
