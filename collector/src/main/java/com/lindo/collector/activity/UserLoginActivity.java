package com.lindo.collector.activity;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.domain.User;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.ui.MainUIActivity;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.FileUtil;
import com.lindo.collector.utils.HttpUtil;
import com.lindo.collector.utils.JsonUtil;
import com.lindo.collector.utils.LogUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lindo.collector.basic.BaseActivity;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;
import com.lzx.work.utils.StringUtils;
import com.lindo.collector.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;


public class UserLoginActivity extends BaseActivity implements View.OnClickListener {

	protected String TAG = "UserLoginActivity";

	private TextView mRegisterText;
	private TextView mForgetPasswordText;
	private Button mLoginBtn;
	private EditText mAccountText;
	private EditText mPasswordText;
	private ImageView mTencentLoginBtn;
	private ImageView mSinaLoginBtn;

	private ProgressDialog mDialog;
	UMSocialService mController = UMServiceFactory.getUMSocialService(Constant.DESCRIPTOR_LOGIN);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		
		initView();
		initData();
		autologin();

	}

	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mRegisterText = (TextView) findViewById(R.id.register);
		mForgetPasswordText = (TextView) findViewById(R.id.forgot_psw);
		mLoginBtn = (Button) findViewById(R.id.login);
		mAccountText = (EditText) findViewById(R.id.account_editor);
		mPasswordText = (EditText) findViewById(R.id.psw_editor);
		mTencentLoginBtn = (ImageView) findViewById(R.id.login_login_qq);
		mSinaLoginBtn = (ImageView) findViewById(R.id.login_login_sina);
	}

	private void initData() {
		mTitleText.setText(R.string.login);
		
		mRegisterText.setOnClickListener(this);
		mForgetPasswordText.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
		mTencentLoginBtn.setOnClickListener(this);
		mSinaLoginBtn.setOnClickListener(this);
		
		/*mAccountText.setText("15001066722");
		mPasswordText.setText("123456");*/
	}

	private void autologin() {
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		String password = intent.getStringExtra("password");
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			mAccountText.setText(username);
			mPasswordText.setText(password);
		}
		
		if (TextUtils.isEmpty(username)) {
			mAccountText.requestFocus();
		}else {
			mPasswordText.requestFocus();
		}
		
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			login(username, password);
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		autologin();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		hideKeyboard();
	}

	// 隐藏虚拟键盘
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) (getApplicationContext())
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(mAccountText.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void creatDialog() {
		hideKeyboard();
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(R.string.tip);
		mDialog.setMessage(getText(R.string.login_wait_tip));
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(true);
		mDialog.show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			IntentUtil.startActivity(this, UserRegisterActivity.class);
			break;
		case R.id.forgot_psw:
			IntentUtil.startActivity(this, ForgetUserPwdActivity.class);
			break;
		case R.id.login:
			
			final String phoneNum = mAccountText.getText().toString();
			final String userPass = mPasswordText.getText().toString();
			if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(userPass)) {
				AppToast.showShortText(UserLoginActivity.this, R.string.login_info_empty);
			} else {
				login(phoneNum, userPass);
			}
			break;

		default:
			break;
		}
	}

	private void login(final String phoneNum, final String userPass) {
		Log.d(TAG, "login userPass = " + userPass);
		String url = Constant.USER_LOGIN_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		
		saveCookie(client);
        
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		params.put("username", phoneNum);
		params.put("pwd", userPass);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				creatDialog();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {
				mHandler.sendEmptyMessage(USER_LOGIN_FAILED);
				Log.e(TAG, "获取数据异常 ", e);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				Log.d(TAG, "onSuccess statusCode = " + statusCode);
				String json = new String(response);
				Log.d(TAG, "onSuccess json = " + json);
				if (200 == statusCode) {
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						Utils.setCookies(getCookie());
						User user = JsonUtil.getUserInfoByJson(json, phoneNum);
						Log.d(TAG, "onSuccess user.mobile = " + user.mobile);
						// 推送百度userId
						HttpUtil.pushBaiduUserId(UserLoginActivity.this, PushApplication.getInstance().getUserId());
						
						// 保存用户信息
						try {
							FileUtil.saveUserInfo2Rom(getApplicationContext(), phoneNum, userPass, "user.txt");
						} catch (IOException e) {
							e.printStackTrace();
						}
						mHandler.obtainMessage(USER_LOGIN_SUCCESS, user).sendToTarget();
						break;
					case 504:
						mHandler.sendEmptyMessage(USER_LOGIN_INFO_INVALID);
						break;

					default:
						mHandler.sendEmptyMessage(USER_LOGIN_FAILED);
						break;
					}
				}
				
			}
		});
		
	}

	private final int USER_LOGIN_SUCCESS = 1001;
	private final int USER_LOGIN_INFO_INVALID = 1002;
	private final int USER_LOGIN_FAILED = 1003;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			closeDialog();
			switch (msg.what) {
			case USER_LOGIN_SUCCESS:
				User user = (User) msg.obj;
				Log.d(TAG, "USER_LOGIN_SUCCESS user.mobile = " + user.mobile);
				PushApplication.getInstance().setLogin(true);
				PushApplication.getInstance().setUser(user);

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("auto_login", true);
				com.lzx.work.utils.Utils.saveMsg(UserLoginActivity.this, Constant.APP_FILE_NAME, map);
				
				IntentUtil.startActivity(UserLoginActivity.this, MainUIActivity.class);
				UserLoginActivity.this.finish();
				
				break;
			case USER_LOGIN_INFO_INVALID:
				AppToast.showShortText(UserLoginActivity.this, R.string.login_info_error);
				break;
			case USER_LOGIN_FAILED:
				AppToast.showShortText(UserLoginActivity.this, R.string.login_failed);
				break;

			default:
				break;
			}
		};
	};

	protected void closeDialog(){
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
	
}
