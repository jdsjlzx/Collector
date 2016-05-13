package com.lindo.collector.activity;


import org.apache.http.Header;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class ModifyUserNickNameActivity extends BaseActivity implements OnClickListener{

	private Button mRightBtn;
	private EditText mNickEditor;
	private String srcNickName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_user_nickname);
		initView();
		initData();
		
	}
	
	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mRightBtn = (Button) findViewById(R.id.right_btn);
		mNickEditor = (EditText) findViewById(R.id.nick_name_editor);
	}
	
	private void initData() {
		mRightBtn.setText(R.string.save);
		mTitleText.setText(R.string.user_modify_nickname);
		srcNickName = getIntent().getStringExtra("nickname");
		mNickEditor.setText(srcNickName);
		
		mRightBtn.setEnabled(false);
		mRightBtn.setOnClickListener(this);
		mNickEditor.addTextChangedListener(textWatcher);
		
		mNickEditor.setSelection(srcNickName.length());
	}
	

	TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable editable) {
			String result = editable.toString();
			if (!result.trim().equals(srcNickName)) {
				mRightBtn.setVisibility(View.VISIBLE);
				mRightBtn.setEnabled(true);
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_btn:
			modifyNickName(mNickEditor.getText().toString());
			break;


		default:
			break;
		}
	}
	
	private void modifyNickName(final String nickname) {
		String url = Constant.USER_MODIFY_INFO_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		params.put("nickname", nickname);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {
				
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				String json = new String(response);
				if (statusCode == 200) {
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						PushApplication.getInstance().getUser().nick = nickname;
						finish();
						break;

					default:
						break;
					}
					
				}
			}
		});
	}
}
