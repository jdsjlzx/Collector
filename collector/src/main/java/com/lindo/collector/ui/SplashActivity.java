package com.lindo.collector.ui;

import java.io.IOException;
import java.util.Map;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.basic.FullScreenBaseActivity;
import com.lindo.collector.domain.User;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.FileUtil;
import com.lindo.collector.utils.JsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lindo.collector.utils.Utils;

public class SplashActivity extends FullScreenBaseActivity {

	private TextView mVersionText;

	private static final int sleepTime = 2500;
	protected String TAG = "SplashActivity";

	Map<String, String> mUserMap;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		mVersionText = (TextView) findViewById(R.id.version_text);

		mVersionText.setText(com.lzx.work.utils.Utils.getVersion(this));

		// 初始化文件服务

	}

	@Override
	protected void onStart() {
		super.onStart();

		Map<String, ?> map = com.lzx.work.utils.Utils.getMsg(SplashActivity.this,
				Constant.APP_FILE_NAME);
		if (map != null && !map.isEmpty()) {
			if (map.containsKey("auto_login")) {
				if ((Boolean) map.get("auto_login")) {
					// 若值为true,从文件中读取用户信息开始登陆
					try {
						mUserMap = FileUtil.getUserInfo(this, "user.txt");
						if (mUserMap.containsKey("username")
								&& mUserMap.containsKey("password")) {
							login(mUserMap.get("username"),
									mUserMap.get("password"));
						} else {
							mHandler.sendEmptyMessageDelayed(ENTER_MAIN_UI_NOT_LOGIN,
									1000);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					mHandler.sendEmptyMessageDelayed(ENTER_MAIN_UI_NOT_LOGIN,
							2000);
				}
			} else {
				mHandler.sendEmptyMessageDelayed(ENTER_MAIN_UI_NOT_LOGIN, sleepTime);
			}

		} else {
			// 第一次登录
			/*startActivity(new Intent(this, GuideActivity.class));
			finish();*/
			gotoMainUI();
		}

	}

	private void login(final String phoneNum, final String userPass) {
		String url = Constant.USER_LOGIN_URL;
		final AsyncHttpClient client = new AsyncHttpClient();
		
		saveCookie(client);
		
		RequestParams params = new RequestParams();
		params.put("username", phoneNum);
		params.put("pwd", userPass);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {
				mHandler.sendEmptyMessage(USER_LOGIN_INFO_INVALID);
				Log.e(TAG, "获取数据异常 ", e);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				String json = new String(response);
				if (200 == statusCode) {
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						User user = JsonUtil.getUserInfoByJson(json, phoneNum);
						Utils.setCookies(getCookie());
						mHandler.obtainMessage(USER_LOGIN_SUCCESS, user)
								.sendToTarget();
						break;
					case 504:
						mHandler.sendEmptyMessage(USER_LOGIN_INFO_INVALID);
						break;

					default:
						mHandler.sendEmptyMessage(USER_LOGIN_INFO_INVALID);
						break;
					}
				}
				
			}
		});

	}
	
	private final int USER_LOGIN_SUCCESS = 1001;
	private final int USER_LOGIN_INFO_INVALID = 1002;
	private final int ENTER_MAIN_UI_NOT_LOGIN = 1005;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case USER_LOGIN_SUCCESS:
				User user = (User) msg.obj;
				// 保存用户信息
				PushApplication.getInstance().setLogin(true);
				PushApplication.getInstance().setUser(user);
				
				// 进入主页
				gotoMainUI();
				break;
			case USER_LOGIN_INFO_INVALID:
			case ENTER_MAIN_UI_NOT_LOGIN:
				// 进入主页
				gotoMainUI();

				break;

			default:
				break;
			}
		};
	};


	private void gotoMainUI() {
		Log.d(TAG, "gotoMainUI");
		startActivity(new Intent(SplashActivity.this, MainUIActivity.class));
		finish();
	}

}
