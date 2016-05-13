package com.lindo.collector.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lindo.collector.R;
import com.lindo.collector.activity.adapter.UserDoingsListAdapter;
import com.lindo.collector.domain.Doings;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.JsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lindo.collector.basic.BaseActivity;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;
import com.lzx.work.widget.PullToRefreshBase;
import com.lzx.work.widget.PullToRefreshBase.OnRefreshListener;
import com.lzx.work.widget.PullToRefreshListView;

public class UserDoingsListActivity extends BaseActivity {

	private static final String TAG = "UserDoingsListActivity";

	private ListView mListView;
	private View mLoadView;

	static UserDoingsListAdapter mListAdapter;

	private PullToRefreshListView mPullListView;
	private LinkedList<Doings> mListItems;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm",Locale.CHINA);
	private boolean mIsStart = false;
	private int totalRecord = 0;
	private int mCurPageIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_task_list);
		
		initView();
		initData();
	}
	
	private void initData() {
		mTitleText.setText(R.string.user_tasks);
		mBackBtn.setVisibility(View.VISIBLE);

		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(true);

		mListView = mPullListView.getRefreshableView();
		mListView.setDivider(getResources().getDrawable(
				R.drawable.list_divider_gray));
		mListView.setDividerHeight(2);
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
						mHandler.sendEmptyMessage(GET_DOINGS_LIST_DATA);
					}
				}
				return false;
			}
		});
		((ViewGroup)mListView.getParent()).addView(emptyView);  
		mListView.setEmptyView(emptyView);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Log.d(TAG, "onItemClick  position = " + position);
				Doings doings = mListItems.get(position);
				if (doings.state < 4) {
					IntentUtil.startActivity(UserDoingsListActivity.this, PerformTaskActivity.class,
							new BasicNameValuePair("id", doings.id),
							new BasicNameValuePair("title", doings.name));
				}else {
					AppToast.showShortText(UserDoingsListActivity.this, "任务已过期不能继续参加");
				}
				
				

			}
		});
		
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				mIsStart = true;
				mHandler.sendEmptyMessage(GET_DOINGS_LIST_DATA); 
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				mIsStart = false;
				mHandler.sendEmptyMessage(GET_DOINGS_LIST_DATA);
			}
		});

		setLastUpdateTime();
		
		mListItems = new LinkedList<Doings>();
		mListAdapter = new UserDoingsListAdapter(this, mListItems);
		mListView.setAdapter(mListAdapter);
		
		mHandler.sendEmptyMessage(GET_DOINGS_LIST_DATA);
	}

	private void initView() {
		mTitleText = (TextView) findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);

		mLoadView = findViewById(R.id.load_view);
		mPullListView = (PullToRefreshListView) findViewById(R.id.refreshable_view);
		
	}
	
	private static final int GET_DOINGS_LIST_DATA = 1001;
	private static final int UPDATE_DOINGS_LIST = 1002;
	private static final int NO_DOINGS_LIST_DATA = 1003;
	private static final int USER_TOKEN_TIMEOUT = 1004;
	private static final int GET_DOINGS_LIST_DATA_FAILURE = 1005;
	private static final int NO_MORE_DOINGS_LIST_DATA = 1006;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DOINGS_LIST_DATA:
				String url = getRequestUrl(mIsStart);
				requestDoingsData(url);
				break;
			case UPDATE_DOINGS_LIST:
				String json = (String) msg.obj;
				List<Doings> data = getDoingsListByJosn(json);

				if (mIsStart) {
					mListItems.clear();
					mListItems.addAll(data);
				} else {
					mListItems.addAll(mListAdapter.getCount(), data);
				}

				Log.d(TAG, "size = " + mListItems.size());

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
			case NO_MORE_DOINGS_LIST_DATA:
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();
				if (mListAdapter.getCount() == totalRecord && totalRecord != 0) {
					mPullListView.setHasMoreData(false);
				} else {
					mPullListView.setHasMoreData(true);
				}
				
				setLastUpdateTime();
				
				break;
			case NO_DOINGS_LIST_DATA:
				//清空数据
				if (!mListItems.isEmpty()) {
					mListItems.clear();
				}
				mListAdapter.notifyDataSetChanged();
				
				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);
				
				break;
			case GET_DOINGS_LIST_DATA_FAILURE:
				AppToast.makeToast(UserDoingsListActivity.this, "获取数据超时，请稍后再试！");
				mLoadView.setVisibility(View.GONE);
				mPullListView.setVisibility(View.VISIBLE);
				
				break;
			case USER_TOKEN_TIMEOUT:
				//token失效跳转到登陆界面
				AppToast.makeToast(UserDoingsListActivity.this, "登陆超时，请您重新登陆!");
				startActivity(new Intent(UserDoingsListActivity.this, UserLoginActivity.class));
				break;
			default:
				break;
			}
		}
	};

	private void requestDoingsData(String url) {
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Log.e(TAG, "获取数据异常 ", arg3);
				mHandler.sendEmptyMessage(GET_DOINGS_LIST_DATA_FAILURE);
				
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				
				String json = new String(response);
				Log.d(TAG, "onSuccess " + json);
				
				if (200 == statusCode) {
					switch (JsonUtil.getStateFromServer(json)) {
					case 1:
						mHandler.obtainMessage(UPDATE_DOINGS_LIST, json).sendToTarget();
						break;
					case 201:
						if (mListAdapter.getCount() == 0) {
							mHandler.sendEmptyMessage(NO_DOINGS_LIST_DATA);
						}else {
							mHandler.sendEmptyMessage(NO_MORE_DOINGS_LIST_DATA);
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

	private List<Doings> getDoingsListByJosn(String json) {
		List<Doings> list = new ArrayList<Doings>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			totalRecord = jsonObject.getInt("total");
			String imgPath = jsonObject.getString("attachment_path");
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject object = jsonArray.getJSONObject(i);
			    
			    Doings doings = new Doings();
			    doings.id = object.getString("task_id");
			    doings.name = object.getString("title");
				doings.thumbUrl = imgPath + object.getString("example_photo");
				/*doings.detaiUrl = object.getString("detail_url");*/
				doings.reward = object.getInt("reward_point");
				/*doings.requirement = object.getString("requirement");*/
				/*doings.participation_number = object.getInt("apply_num");*/
				/*doings.height = 600;*/
				doings.state = object.getInt("status");
				/*doings.join_state = object.getInt("is_join");*/
			    list.add(doings);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "getdoingsListByJosn list.size() = " + list.size());
		
		return list;
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

	private String getRequestUrl(boolean isStart) {
		if ((mListItems.size() == 0) || isStart) {
			mCurPageIndex = 1;
		} else {
			++mCurPageIndex;
		}

		String url = Constant.USER_DOINGS_LIST_URL + "?p=" + mCurPageIndex;
		Log.d(TAG, "url -- " + url);
		return url;
	}
	
}
