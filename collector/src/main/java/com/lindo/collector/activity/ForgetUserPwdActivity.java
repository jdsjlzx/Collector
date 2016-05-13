package com.lindo.collector.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lindo.collector.R;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.JsonUtil;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class ForgetUserPwdActivity extends BaseActivity implements
		View.OnClickListener {

	private static final String TAG = "ForgetUserPwdActivity";
	private Button mGetVerifyCodeBtn;
	private Button mSubmitBtn;
	private EditText mAccountText;
	private EditText mVerifyCodeText;
	private EditText mPasswordText;
	private EditText mConfirmPasswordText;
	
	private String checkCode;
	private Timer timer;// 计时
	private int seconds = Constant.GET_VERIFY_CODE;

	private long mGetVerifyCodeTime;
	private long mNowTime;
	private TextView mNoticeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);

		initView();
		initData();
	}

	private void initView() {
		mTitleText = (TextView) findViewById(R.id.title_text);
		mTitleText.setText(R.string.forgot_psw_title);

		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mBackBtn.setVisibility(View.VISIBLE);

		mAccountText = (EditText) findViewById(R.id.account_editor);
		mVerifyCodeText = (EditText) findViewById(R.id.verify_code_editor);
		mPasswordText = (EditText) findViewById(R.id.password_editor);
		mConfirmPasswordText = (EditText) findViewById(R.id.confirm_password_editor);
		mGetVerifyCodeBtn = (Button) findViewById(R.id.get_verify_code);
		mSubmitBtn = (Button) findViewById(R.id.next);
		mNoticeText = (TextView) findViewById(R.id.notice);
	}

	private void initData() {
		mGetVerifyCodeBtn.setOnClickListener(this);
		mSubmitBtn.setOnClickListener(this);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void onGetVerifyCode(final String phoneNum) {
		Log.d(TAG, "onGetVerifyCode");
		String url = Constant.GET_VERIFY_CODE_URL;
		AsyncHttpClient client = new AsyncHttpClient();
		saveCookie(client);
		RequestParams params = new RequestParams();
		params.put("phone", phoneNum);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				String json = new String(response);
				Log.d(TAG, "onSuccess json = " + json);
				if (200 == statusCode) {
					if (1 == JsonUtil.getStateFromServer(json)) {
						com.lindo.collector.utils.Utils.setCookies(getCookie());
						String code = JsonUtil.getVerifyVodeByJson(json);
						Log.d(TAG, "code = " + code);
						if (!TextUtils.isEmpty(code)) {
							mGetVerifyCodeTime = System.currentTimeMillis();
							checkCode = code;
							String tip = getResources().getString(R.string.receive_sms_verify, phoneNum);
							mNoticeText.setVisibility(View.VISIBLE);
							mNoticeText.setText(tip);
						}
						
					} else {
						AppToast.showShortText(ForgetUserPwdActivity.this, R.string.get_verify_code_failure);
					}

				}
			}
		});
	}

	private void onReSetPassword(String phoneNum, String password,
			String verifyCode) {

		String url = Constant.FORGET_PASSWORD_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		params.put("phone", phoneNum);
		params.put("pwd", password);
		params.put("verifyCode", verifyCode);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {

				String json = new String(response);
				Log.d(TAG, "onSuccess json = " + json);
				if (200 == statusCode) {
					if (1 == JsonUtil.getStateFromServer(json)) {
						AppToast.showShortText(ForgetUserPwdActivity.this,R.string.reset_password_success);
						startActivity(new Intent(ForgetUserPwdActivity.this, UserLoginActivity.class));
						ForgetUserPwdActivity.this.finish();
					} else {
						AppToast.showShortText(ForgetUserPwdActivity.this,R.string.reset_password_failed);
					}

				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		String phoneNum = mAccountText.getText().toString();
		String verifyCode = mVerifyCodeText.getText().toString();
		String password = mPasswordText.getText().toString();
		String confirmPassword = mConfirmPasswordText.getText().toString();
		switch (v.getId()) {
		case R.id.next:

			if (TextUtils.isEmpty(phoneNum)) {
				AppToast.showShortText(ForgetUserPwdActivity.this, R.string.register_mobile_empty);
				return;
			}
			
			if (TextUtils.isEmpty(verifyCode)) {
				AppToast.showShortText(ForgetUserPwdActivity.this, R.string.register_verify_code_empty);
				return;
			}
			
			if (!Utils.getMD5Text(verifyCode).equals(checkCode)) {
				AppToast.showShortText(ForgetUserPwdActivity.this, R.string.register_verify_code_error);
				return;
			}
			
			mNowTime = System.currentTimeMillis();
			if ((mNowTime - mGetVerifyCodeTime) > Constant.PERIOD_VERIFY_CODE) {
				checkCode = null;
				AppToast.showShortText(ForgetUserPwdActivity.this, R.string.register_verify_code_failed);
				return;
			}

			if (TextUtils.isEmpty(password)
					|| TextUtils.isEmpty(confirmPassword)) {
				AppToast.showShortText(this, R.string.register_password_empty);
				return;
			}
			
			if ((password.length()<8 || password.length()>16)
					|| (confirmPassword.length()<8 || confirmPassword.length()>16)) {
				AppToast.showShortText(ForgetUserPwdActivity.this, R.string.register_password_length_failed);
				return;
			}
			
			if (!password.equals(confirmPassword)) {
				AppToast.showShortText(this, R.string.psw_conflict);
				return;
			}
			onReSetPassword(phoneNum, password, verifyCode);

			break;
		case R.id.get_verify_code:
			if (TextUtils.isEmpty(phoneNum)) {
				AppToast.showShortText(this, R.string.register_mobile_empty);
				return;
			} else {
				if (Utils.isMobileNO(phoneNum)) {
					onGetVerifyCode(phoneNum);
					mGetVerifyCodeBtn.setEnabled(false);
					mVerifyCodeText.requestFocus();
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							mHandler.obtainMessage(REMAING_SECONDS,seconds--,-1).sendToTarget();
						}
					}, 0, 1000);
				} else {
					AppToast.showShortText(ForgetUserPwdActivity.this, R.string.register_mobile_invalid);
				}
			}

			break;

		default:
			break;
		}

	}

	private static final int REMAING_SECONDS = 1001;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REMAING_SECONDS:
				int remainSeconds = msg.arg1;
				if (remainSeconds == 0) {
					mGetVerifyCodeBtn.setEnabled(true);
					mGetVerifyCodeBtn.setText(R.string.get_verify_code);
					timer.cancel();
					seconds = Constant.GET_VERIFY_CODE;
				} else {
					String text = getResources().getString(
							R.string.resend_verify_code, remainSeconds);
					mGetVerifyCodeBtn.setText(text);
				}
				break;

			default:
				break;
			}
		}
	};
	
}
