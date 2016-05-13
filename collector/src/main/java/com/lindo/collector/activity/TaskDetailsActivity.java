package com.lindo.collector.activity;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.JsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;

public class TaskDetailsActivity extends BaseActivity implements
		OnClickListener {
	private final String TAG = "TaskDetailsActivity";
	private WebView mWebView;
	private Button mNextBtn;
	private ImageView mRightImage;
	private Intent intent;
	private String mUrl;
	// 首先在您的Activity中添加如下成员变量
	//private UMSocialService mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);

		initView();
		initWebView();
		initData();

	}

	private void initView() {
		mTitleText = (TextView) findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mRightImage = (ImageView) findViewById(R.id.right_img);
		mWebView = (WebView) findViewById(R.id.content_webview);
		mNextBtn = (Button) findViewById(R.id.nextBtn);
	}

	private void initData() {
		intent = getIntent();
		mTitleText.setText(intent.getStringExtra("title"));
		mUrl = intent.getStringExtra("url");
		mWebView.loadUrl(mUrl);
		
		if (!TextUtils.isEmpty(intent.getStringExtra("only_show"))) {
			if (intent.getStringExtra("only_show").equals("1")) {
				mNextBtn.setVisibility(View.GONE);
			}
		}
		
		mRightImage.setVisibility(View.VISIBLE);
		mRightImage.setBackgroundResource(R.drawable.ic_share);
		
		String countText = String.format(getResources().getString(R.string.user_participate_in_task), intent.getStringExtra("total")); 
		mNextBtn.setText(countText);
		
		mRightImage.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
		
		initSocialSDK();
		
	}

	/**
	 * 初始化SDK，添加一些平台
	 */
	private void initSocialSDK() {
		/*mController = (UMSocialService) UMServiceFactory
				.getUMSocialService("com.umeng.share");
		
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
		
		UMImage localImage = new UMImage(this, R.drawable.icon);
		String title = getResources().getString(R.string.share_title);
		String content = String.format(getResources().getString(R.string.share_content),intent.getStringExtra("uploadPicCount"),intent.getStringExtra("score"));  
		String url = "http://123.56.132.166/app/index/download";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(TaskDetailsActivity.this,
				Constant.wx_appId, Constant.wx_appSecret);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(
				TaskDetailsActivity.this, Constant.wx_appId, Constant.wx_appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		
		// 设置微信好友分享内容
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		// 设置分享文字
		weixinContent.setShareContent(content);
		// 设置title
		weixinContent.setTitle(title);
		// 设置分享内容跳转URL
		weixinContent.setTargetUrl(url);
		// 设置分享图片
		weixinContent.setShareImage(localImage);
		mController.setShareMedia(weixinContent);
		
		// 设置微信朋友圈分享内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(content);
		// 设置朋友圈title
		circleMedia.setTitle(title);
		circleMedia.setShareImage(localImage);
		circleMedia.setTargetUrl(url);
		mController.setShareMedia(circleMedia);
		
		// 添加QQ平台
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
				TaskDetailsActivity.this, Constant.qq_appID, Constant.qq_appSecret);
		qqSsoHandler.addToSocialSDK();	
		
		// 设置QQ分享内容
		QQShareContent qqMedia = new QQShareContent();
		qqMedia.setShareContent(content);
		qqMedia.setTargetUrl(url);
		qqMedia.setTitle(title);
		qqMedia.setShareImage(localImage);
		mController.setShareMedia(qqMedia);
		
		SnsPostListener mSnsPostListener = new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int stCode,
					SocializeEntity entity) {
				Log.d(TAG, "onComplete platform " + platform.toString());
				//朋友圈：weixin_circle
				if (stCode == 200) {
					AppToast.showShortText(TaskDetailsActivity.this, "分享成功");

				} else {
					AppToast.showShortText(TaskDetailsActivity.this, "分享失败 : error code : " + stCode);
				}
			}
			
		};
		mController.registerListener(mSnsPostListener);*/
	}
	
	private void initWebView() {
		WebSettings mWebSetting = mWebView.getSettings();
		mWebSetting.setLoadWithOverviewMode(true);
		mWebSetting.setJavaScriptEnabled(true);
		mWebSetting.setLoadsImagesAutomatically(true);
		mWebSetting.setBuiltInZoomControls(true);
		mWebSetting.setDefaultTextEncodingName("utf-8");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextBtn:
			if (PushApplication.getInstance().isLogin()) {
				joinDoings(intent.getStringExtra("id"));
			} else {
				IntentUtil.startActivity2(this, UserLoginActivity.class);
				TaskDetailsActivity.this.finish();
			}
			break;
		case R.id.right_img:
			if (PushApplication.getInstance().isLogin()) {
				if ("1".equals(intent.getStringExtra("isJoined"))) {
					//mController.openShare(this, false);
				} else {
					AppToast.showShortText(TaskDetailsActivity.this, "亲，参加任务了才能分享给好友哦！");
				}
			}else {
				AppToast.showShortText(TaskDetailsActivity.this, "亲，登录了才能分享给好友哦！");
			}
			
			break;

		default:
			break;
		}

	}

	private void joinDoings(final String doingsId) {
		Log.d(TAG, "joinDongs id =  " + doingsId);
		String url = Constant.USER_JOIN_DOINGS_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();

		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		params.put("task_id", doingsId);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String json = new String(responseBody);
				Log.d(TAG, "onSuccess " + json);
				if (1 == JsonUtil.getStateFromServer(json)
						|| 0 == JsonUtil.getStateFromServer(json)) {
					IntentUtil.startActivity(
							TaskDetailsActivity.this,
							PerformTaskActivity.class,
							new BasicNameValuePair("id", intent.getStringExtra("id")),
							new BasicNameValuePair("title", intent.getStringExtra("title")),
							new BasicNameValuePair("thumb", intent.getStringExtra("thumb")), 
							new BasicNameValuePair("url", intent.getStringExtra("url")));
					TaskDetailsActivity.this.finish();
				} else {
					AppToast.showShortText(TaskDetailsActivity.this, R.string.join_doings_failed);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Log.e(TAG, "onFailure ", error);
				AppToast.showShortText(TaskDetailsActivity.this,
						R.string.join_doings_failed);
			}
		});

	}

}
