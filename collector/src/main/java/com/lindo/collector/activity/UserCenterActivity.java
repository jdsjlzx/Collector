package com.lindo.collector.activity;

import org.apache.http.Header;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.umeng.fb.UmengCustomFBActivity;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.JsonUtil;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.domain.User;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;
import com.lzx.work.utils.Utils;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class UserCenterActivity extends BaseActivity implements OnClickListener{
	private final String TAG = "UserCenterActivity";
	private TextView mUserNickText;//用户昵称
	private TextView mUserTasksText;//我的任务
	private TextView mUserMsgText;//我的消息
	private TextView mVersionText;//版本号
	private TextView mUserScoreText;//用户积分
	private TextView mUserLevelText;//用户等级
	private View mUserInfoView;//登陆后显示的view
	private View mNoUserView;//未登陆显示的view
	private View mUserFeedbackView;//用户反馈
	private View mUpdateAppView;//检查版本
	private View mRightView;//帮助
	private TextView mSettingsText;//设置
	private TextView mHelpText;//帮助文本
	private ImageView mMsgIndicateImage;//消息指示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		
		initView();
		initData();
	}

	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mVersionText = (TextView) findViewById(R.id.app_version_text);
		mUserScoreText = (TextView) findViewById(R.id.user_score_text);
		mUserLevelText = (TextView) findViewById(R.id.user_level_text);
		mUserTasksText = (TextView)findViewById(R.id.user_tasks_text);
		mUserMsgText = (TextView)findViewById(R.id.user_msg_text);
		mUserNickText = (TextView)findViewById(R.id.user_nick_text);
		mSettingsText = (TextView)findViewById(R.id.settings_text);
		mHelpText = (TextView)findViewById(R.id.right_text);
		mMsgIndicateImage = (ImageView)findViewById(R.id.indicate_img);
		
		mNoUserView = findViewById(R.id.no_user_layout);
		mUserInfoView = findViewById(R.id.user_info_layout);
		mUserFeedbackView = findViewById(R.id.user_feedback_layout);
		mUpdateAppView = findViewById(R.id.update_app_layout);
		mRightView = findViewById(R.id.right_layout);
		
	}
	
	private void initData(){
		mTitleText.setText(R.string.person_center_title);
		mHelpText.setText(R.string.help);
		mVersionText.setText(Utils.getVersion(this));
		
		mRightView.setOnClickListener(this);
		mUserInfoView.setOnClickListener(this);
		mNoUserView.setOnClickListener(this);
		mUserFeedbackView.setOnClickListener(this);
		mUpdateAppView.setOnClickListener(this);
		mUserTasksText.setOnClickListener(this);
		mUserMsgText.setOnClickListener(this);
		mSettingsText.setOnClickListener(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PushApplication.getInstance().isLogin()) {
			mUserInfoView.setVisibility(View.VISIBLE);
			mNoUserView.setVisibility(View.GONE);
			getUserInfo();
			checkNewMsg();
		} else {
			mUserInfoView.setVisibility(View.GONE);
			mNoUserView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_layout:
			IntentUtil.startActivity(this, HelpActivity.class);
			break;
		case R.id.no_user_layout:
			IntentUtil.startActivity(this, UserLoginActivity.class);
			UserCenterActivity.this.finish();
			break;
		
		case R.id.user_info_layout:
			IntentUtil.startActivity(this, UserInfoActivity.class);
			break;
		case R.id.user_tasks_text:
			if (PushApplication.getInstance().isLogin()) {
				IntentUtil.startActivity(this, UserDoingsListActivity.class);
			} else {
				AppToast.showShortText(this, R.string.user_login_tip);
			}
			
			break;
		case R.id.user_msg_text:
			IntentUtil.startActivity(this, PushMsgListActivity.class);
			break;
		case R.id.user_feedback_layout:
			IntentUtil.startActivity(this, UmengCustomFBActivity.class);
			break;
		case R.id.update_app_layout:
			checkNewVersion();
			break;
		case R.id.settings_text:
			IntentUtil.startActivity(this, SettingsActivity.class);
			break;

		default:
			break;
		}
	}

	
	private void checkNewVersion() {
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					UmengUpdateAgent.showUpdateDialog(UserCenterActivity.this, updateInfo);
					break;
				case 1: // has no update
					AppToast.makeToast(UserCenterActivity.this, "已经是最新版本！^_^");
					break;
				case 2: // none wifi
					AppToast.makeToast(UserCenterActivity.this, "已经是最新版本！^_^");
					break;
				case 3: // time out
					AppToast.makeToast(UserCenterActivity.this, "网络超时,请检查网络！");
					break;
				}
			}
		});

		UmengUpdateAgent.update(this);
	}
	
	private void getUserInfo() {
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		client.get(Constant.USER_PROFILE_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				/*creatDialog(null,"请等待","正在加载个人信息...");*/
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				/*closeDialog();*/
				AppToast.showShortText(UserCenterActivity.this, "获取个人信息失败，请稍后再试！");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				
				String json = new String(response);
				Log.d(TAG, "onSuccess json " + json);
				if (200 == statusCode) {
					/*closeDialog();*/
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						User user = JsonUtil.getUserInfoByJson(json, PushApplication.getInstance().getUser().mobile);
						PushApplication.getInstance().setUser(user);
						mUserNickText.setText(user.nick);
						
						String scoreText = String.format(getResources().getString(R.string.user_score_text), user.score); 
						mUserScoreText.setText(scoreText);
						
						String levelText = String.format(getResources().getString(R.string.user_level_text), user.level); 
						mUserLevelText.setText(levelText);
						break;
					case -1:
						AppToast.showShortText(UserCenterActivity.this, "您的帐号在另一台终端登录，请重新登录！");
						PushApplication.getInstance().logoutUser();
						UserCenterActivity.this.finish();
						break;
					default:
						break;
					}

				}
			}

		});

	}
	private void checkNewMsg() {
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		client.get(Constant.CHECK_NEW_MESSAGE_URL, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				/*creatDialog(null,"请等待","正在加载个人信息...");*/
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				/*closeDialog();*/
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				
				String json = new String(response);
				Log.d(TAG, "onSuccess json " + json);
				if (200 == statusCode) {
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						String state = JsonUtil.checkNewMsgByJson(json);
						if ("1".equals(state)) {
							mMsgIndicateImage.setVisibility(View.VISIBLE);
						} else {
							mMsgIndicateImage.setVisibility(View.GONE);
						}
						break;
					case -1:
						AppToast.showShortText(UserCenterActivity.this, "您的帐号在另一台终端登录，请重新登录！");
						PushApplication.getInstance().logoutUser();
						UserCenterActivity.this.finish();
						break;
					default:
						break;
					}
					
				}
			}
			
		});
		
	}
	
}
