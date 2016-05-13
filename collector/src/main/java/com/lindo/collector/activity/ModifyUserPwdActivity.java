package com.lindo.collector.activity;

import org.apache.http.Header;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lindo.collector.R;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.JsonUtil;
import com.lzx.work.utils.AppToast;
import com.umeng.analytics.MobclickAgent;

public class ModifyUserPwdActivity extends BaseActivity implements
		View.OnClickListener {

	private String TAG = "ModifyPasswordFragment";

	private Button mNextBtn;
	private EditText mPasswordText;
	private EditText mNewPasswordText;
	private EditText mRePasswordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		
		initView();
		initData();
	}

	private void initView() {
		TextView titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText(R.string.reset_password);

		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mBackBtn.setVisibility(View.VISIBLE);

		mNextBtn = (Button) findViewById(R.id.next);
		mPasswordText = (EditText) findViewById(R.id.psw_editor);
		mNewPasswordText = (EditText) findViewById(R.id.new_psw_editor);
		mRePasswordText = (EditText) findViewById(R.id.confirm_psw_editor);
	}

	private void initData() {
		mNextBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			final String password = mPasswordText.getText().toString();
			final String newPassword = mNewPasswordText.getText().toString();
			final String rePassword = mRePasswordText.getText().toString();
			if (TextUtils.isEmpty(password)) {
				AppToast.showShortText(this, R.string.old_password_empty);
				return;
			}else {
				if (password.length()<8 || password.length()>16) {
					AppToast.showShortText(this, R.string.old_password_length_failed);
					return;
				}
			}
			
			if (TextUtils.isEmpty(newPassword)
					|| TextUtils.isEmpty(rePassword)) {
				AppToast.showShortText(this, R.string.modify_password_empty);
				return;
			}
			
			if ((newPassword.length()<8 || newPassword.length()>16)
					|| (rePassword.length()<8 || rePassword.length()>16)) {
				AppToast.showShortText(this, R.string.new_password_length_failed);
				return;
			}
			
			if (!newPassword.equals(rePassword)) {
				AppToast.showShortText(this, getText(R.string.psw_conflict));
				return;
			}
			
			modifyPassword(password, newPassword);
			break;
		case R.id.back_img:
			finish();

			break;

		default:
			break;
		}

	}

	private void modifyPassword(String password, String newPassword) {
		Log.d(TAG, "modifyPassword");
		String url = Constant.MODIFY_PASSWORD_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		params.put("old_pwd", password);
		params.put("pwd", newPassword);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				Log.d(TAG, "onSuccess statusCode = " + statusCode);
				String json = new String(response);
				Log.d(TAG, "onSuccess json = " + json);
				if (200 == statusCode) {
					if (1 == JsonUtil.getStateFromServer(json)) {
						AppToast.showShortText(ModifyUserPwdActivity.this, R.string.reset_password_success);
						finish();
					} else {
						AppToast.showShortText(ModifyUserPwdActivity.this, R.string.modify_password_failed);
					}

				}
			}
		});
	}
}
