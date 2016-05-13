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

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.activity.adapter.QuestionListAdapter;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.domain.Question;
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

public class HelpActivity extends BaseActivity {
	private static final String TAG = HelpActivity.class.getSimpleName();
	private View mLoadView;
	private ListView mListView;
	private PullToRefreshListView mPullListView;
	
	private QuestionListAdapter mListAdapter;
	private LinkedList<Question> mListItems;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm",Locale.CHINA);
	private boolean mIsStart = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_list);
        
        initView();
        initData();
    }

    private void initData() {
    	mListAdapter = new QuestionListAdapter(this, mListItems);
		mListView.setAdapter(mListAdapter);
		
	}

	private void initView() {
    	mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mTitleText.setText(R.string.help);
		mBackBtn.setVisibility(View.VISIBLE);
		mLoadView = findViewById(R.id.load_view);
		mPullListView = (PullToRefreshListView) findViewById(R.id.refreshable_view);
		
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(true);

		mListItems = new LinkedList<Question>();
		mListView = mPullListView.getRefreshableView();

		/*mListView.setDivider(getResources().getDrawable(R.drawable.icon_home_line));*/
		mListView.setDividerHeight(0);
		mListView.setSelector(android.R.color.transparent);
		mListView.setCacheColorHint(android.R.color.transparent);
		
		/*View emptyView = LayoutInflater.from(this).inflate(R.layout.reload_layout, null);
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
		mListView.setEmptyView(emptyView);*/
		
		
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
				requestQuestionData();
				break;
			case UPDATE_PUSH_MSG_LIST:
				String json = (String) msg.obj;
				List<Question> data = getQuestionListByJosn(json);

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
					mListItems.addAll(data);
					
				}

				Log.d(TAG, "mListItems size = " + mListItems.size());

				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);

				mListAdapter.notifyDataSetChanged();
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();
				mPullListView.setHasMoreData(false);
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
				mPullListView.setHasMoreData(false);
				
				setLastUpdateTime();
				break;
			case GET_PUSH_MSG_DATA_FAILURE:
				AppToast.makeToast(HelpActivity.this, "获取数据超时，请稍后再试！");
				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);
				
				break;
			case USER_TOKEN_TIMEOUT:
				//token失效跳转到登陆界面
				AppToast.makeToast(HelpActivity.this, "登录超时，请您重新登陆!");
				PushApplication.getInstance().logoutUser();
				HelpActivity.this.finish();
				break;
			default:
				break;
			}
    	};
    };
    
    private void requestQuestionData() {
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(HelpActivity.this);
		client.get(Constant.HELP_URL, params, new AsyncHttpResponseHandler() {

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
					case 201:
						if (mListAdapter.getCount() == 0) {
							mHandler.sendEmptyMessage(NO_PUSH_MSG_DATA);
						}else {
							mHandler.sendEmptyMessage(NO_MORE_PUSH_MSG_DATA);
						}
						
						break;
					case 0:
						mHandler.sendEmptyMessage(USER_TOKEN_TIMEOUT);
						
						break;
					default:
						break;
					}

				}
			}

		});

	}
    
    private List<Question> getQuestionListByJosn(String json) {
		List<Question> list = new ArrayList<Question>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.isNull("data")) {
				return null;
			}
			JSONArray array = jsonObject.getJSONArray("data");
			for (int i = 0; i < array.length(); i++) {
				JSONObject msgObject = array.getJSONObject(i);
				Question msg = new Question();
				msg.title = msgObject.getString("question");
				msg.reply = msgObject.getString("answer");
				msg.author = msgObject.getString("author");
				
				list.add(msg);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "JSONException -- " + e.toString());
		}
		
		return list;
	}
    
}
