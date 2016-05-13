package com.lindo.collector.activity;

import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.domain.User;
import com.lindo.collector.basic.BaseActivity;
import com.lzx.work.utils.IntentUtil;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{
	private TextView mUserNickText;//用户昵称
	private TextView mUserMobileText;//用户手机号
	private TextView mUserLevelText;//用户等级
	private TextView mModifyPasswordText;//修改密码
	private TextView mUserScoreText;//用户积分
	
	private View mUserNickView;
	
	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		initView();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}
	private void initView() {
		mTitleText = (TextView) findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mBackBtn.setVisibility(View.VISIBLE);

		mUserNickText = (TextView) findViewById(R.id.user_nick_text);
		mUserMobileText = (TextView) findViewById(R.id.user_mobile_text);
		mUserScoreText = (TextView) findViewById(R.id.user_score_text);
		mUserLevelText = (TextView) findViewById(R.id.user_level_text);
		mModifyPasswordText = (TextView) findViewById(R.id.modify_password_text);
		mUserNickView = findViewById(R.id.user_nick_layout);
		
		mBackBtn.setOnClickListener(this);
		mModifyPasswordText.setOnClickListener(this);
		mUserNickView.setOnClickListener(this);

	}

	private void initData() {
		
		mUser = PushApplication.getInstance().getUser();
		
		mTitleText.setText(R.string.person_info);
		mUserNickText.setText(mUser.nick);
		mUserMobileText.setText(mUser.mobile);
		
		String levelText = String.format(getResources().getString(R.string.user_level_text), mUser.level); 
		mUserLevelText.setText(levelText);
		mUserScoreText.setText(String.valueOf(mUser.score));
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_password_text:
			IntentUtil.startActivity(this, ModifyUserPwdActivity.class);
			break;
		case R.id.user_nick_layout:
			IntentUtil.startActivity(this, ModifyUserNickNameActivity.class,
					new BasicNameValuePair("nickname", mUser.nick));
			break;

		default:
			break;
		}
	}

}
