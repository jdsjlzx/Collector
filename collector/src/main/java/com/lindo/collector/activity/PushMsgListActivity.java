package com.lindo.collector.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushManager;
import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.activity.adapter.PushMsgListAdapter;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.domain.PushMsg;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.JsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzx.work.utils.AppToast;
import com.lzx.work.widget.PullToRefreshBase;
import com.lzx.work.widget.PullToRefreshBase.OnRefreshListener;
import com.lzx.work.widget.PullToRefreshListView;

public class PushMsgListActivity extends BaseActivity{

	private static final String TAG = PushMsgListActivity.class.getSimpleName();

	private View mLoadView;
	private ListView mListView;
	private PullToRefreshListView mPullListView;
	
	private PushMsgListAdapter mListAdapter;
	private LinkedList<PushMsg> mListItems;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm",Locale.CHINA);
	private boolean mIsStart = false;
	private static int totalRecord = 0;
	private int mCurPageIndex = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_msg_list);
        
        initView();
        initData();
    }

    private void initData() {
    	mListAdapter = new PushMsgListAdapter(this, mListItems);
		mListView.setAdapter(mListAdapter);
		
		Resources resource = this.getResources();
        String pkgName = this.getPackageName();
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // PushManager.enableLbs(getApplicationContext());

        // Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                getApplicationContext(), resource.getIdentifier(
                        "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "simple_notification_icon", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);
	}

	private void initView() {
    	mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mTitleText.setText(R.string.person_push_msg_text);
		mBackBtn.setVisibility(View.VISIBLE);
		mLoadView = findViewById(R.id.load_view);
		mPullListView = (PullToRefreshListView) findViewById(R.id.refreshable_view);
		
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(true);

		mListItems = new LinkedList<PushMsg>();
		mListView = mPullListView.getRefreshableView();

		/*mListView.setDivider(getResources().getDrawable(R.drawable.icon_home_line));*/
		mListView.setDividerHeight(0);
		mListView.setSelector(android.R.color.transparent);
		mListView.setCacheColorHint(android.R.color.transparent);
		
		View emptyView = LayoutInflater.from(this).inflate(R.layout.layout_reload, null);
		emptyView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mListAdapter.getCount() == 0) {
						mLoadView.setVisibility(View.VISIBLE);
						mPullListView.setVisibility(View.GONE);
						mHandler.sendEmptyMessage(GET_PUSH_MSG_LIST_DATA);
					}
				}
				return false;
			}
		});
		
		((ViewGroup)mListView.getParent()).addView(emptyView);  
		mListView.setEmptyView(emptyView);
		
		
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				mIsStart = true;
				mHandler.sendEmptyMessage(GET_PUSH_MSG_LIST_DATA); 
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				mIsStart = false;
				mHandler.sendEmptyMessage(GET_PUSH_MSG_LIST_DATA);
			}
		});

		setLastUpdateTime();
    }
    
    private void setLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		mPullListView.setLastUpdatedLabel(text);
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}
		return mDateFormat.format(new Date(time));
	}
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mHandler.sendEmptyMessage(GET_PUSH_MSG_LIST_DATA);
    }

    private static final int GET_PUSH_MSG_LIST_DATA = 1001;
	private static final int UPDATE_PUSH_MSG_LIST = 1002;
	private static final int NO_PUSH_MSG_DATA = 1003;
	private static final int GET_PUSH_MSG_DATA_FAILURE = 1004;
	private static final int NO_MORE_PUSH_MSG_DATA = 1005;
	private static final int USER_TOKEN_TIMEOUT = 1006;
    private Handler mHandler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case GET_PUSH_MSG_LIST_DATA:
				String url = getRequestUrl(mIsStart);
				requestPushMsgData(url);
				break;
			case UPDATE_PUSH_MSG_LIST:
				String json = (String) msg.obj;
				List<PushMsg> data = getPushMsgListByJosn(json);

				if (null == data) {
					return;
				}
				

				if (mIsStart) {
					//清空数据
					if (!mListItems.isEmpty()) {
						mListItems.clear();
					}
					mListItems.addAll(data);
				} else {
					if (mListItems.size() < totalRecord) {
						mListItems.addAll(mListAdapter.getCount(), data);
					}else {
						mListItems.addAll(data);
					}
					
				}

				Log.d(TAG, "mListItems size = " + mListItems.size());

				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);

				mListAdapter.notifyDataSetChanged();
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();

				if (mListAdapter.getCount() == totalRecord && totalRecord != 0) {
					mPullListView.setHasMoreData(false);
				} else {
					mPullListView.setHasMoreData(true);
				}

				setLastUpdateTime();
				mIsStart = false;
				break;
			case NO_PUSH_MSG_DATA:
				//清空数据
				if (!mListItems.isEmpty()) {
					mListItems.clear();
				}
				mListAdapter.notifyDataSetChanged();
				
				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);
				break;
			case NO_MORE_PUSH_MSG_DATA:
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();
				
				if (mListAdapter.getCount() == totalRecord && totalRecord != 0) {
					mPullListView.setHasMoreData(false);
				} else {
					mPullListView.setHasMoreData(true);
				}
				
				setLastUpdateTime();
				break;
			case GET_PUSH_MSG_DATA_FAILURE:
				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);
				
				break;
			case USER_TOKEN_TIMEOUT:
				//token失效跳转到登陆界面
				AppToast.makeToast(PushMsgListActivity.this, "登录超时，请您重新登陆!");
				PushApplication.getInstance().logoutUser();
				PushMsgListActivity.this.finish();
				break;
			default:
				break;
			}
    	};
    };
    
    private void requestPushMsgData(String url) {
    	FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Log.e(TAG, "获取数据异常 ", arg3);
				mHandler.sendEmptyMessage(GET_PUSH_MSG_DATA_FAILURE);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				
				String json = new String(response);
				Log.d(TAG, "onSuccess " + json);
				
				if (200 == statusCode) {
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						mHandler.obtainMessage(UPDATE_PUSH_MSG_LIST, json).sendToTarget();
						break;
					case 0:
						if (mListAdapter.getCount() == 0) {
							mHandler.sendEmptyMessage(NO_PUSH_MSG_DATA);
						}else {
							mHandler.sendEmptyMessage(NO_MORE_PUSH_MSG_DATA);
						}
						
						break;
					case -1:
						mHandler.sendEmptyMessage(USER_TOKEN_TIMEOUT);
						
						break;
					default:
						break;
					}

				}
			}

		});

	}
    
    private List<PushMsg> getPushMsgListByJosn(String json) {
		List<PushMsg> list = new ArrayList<PushMsg>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.isNull("data")) {
				return null;
			}
			JSONArray boxesArray = jsonObject.getJSONArray("data");
			totalRecord = jsonObject.getInt("total");
			for (int i = 0; i < boxesArray.length(); i++) {
				JSONObject msgObject = boxesArray.getJSONObject(i);
				PushMsg msg = new PushMsg();
				msg.title = msgObject.getString("title");
				msg.content = msgObject.getString("message");
				msg.createTime = msgObject.getString("addtime");
				
				list.add(msg);
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "JSONException -- " + e.toString());
		}
		
		return list;
	}
    
    private String getRequestUrl(boolean isStart) {
		if ((mListItems.size() == 0) || isStart) {
			mCurPageIndex = 1;
		} else {
			++mCurPageIndex;
		}
		String url = Constant.DOMAIN
				+ "/app/index/message?"
				+ "p="+mCurPageIndex;
		Log.d(TAG, "url -- " + url);
		return url;
	}
}
