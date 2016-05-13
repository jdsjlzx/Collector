package com.lzx.work.activity;

import com.lzx.work.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonWebActivity extends Activity {

	private TextView titleText;
	private ImageView mBackBtn;
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_web);
		titleText = (TextView) findViewById(R.id.title_text);

		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mBackBtn.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mWebView = (WebView) findViewById(R.id.content_webview);

		initWebView();

		initData();
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	private void initData() {
		Intent intent = getIntent();
		titleText.setText(intent.getStringExtra("title"));
		mWebView.loadUrl(intent.getStringExtra("url"));
	}

	private void initWebView() {
		WebSettings mWebSetting = mWebView.getSettings();
		mWebSetting.setLoadWithOverviewMode(true);
		mWebSetting.setJavaScriptEnabled(true);
		mWebSetting.setLoadsImagesAutomatically(true);
		mWebSetting.setBuiltInZoomControls(true);
		mWebSetting.setDefaultTextEncodingName("utf-8");

	}

}
