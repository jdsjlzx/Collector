package com.lindo.collector.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lindo.collector.utils.ImageFetcher;
import com.lindo.collector.utils.JsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.activity.PerformTaskActivity;
import com.lindo.collector.activity.TaskDetailsActivity;
import com.lindo.collector.activity.UserLoginActivity;
import com.lindo.collector.activity.adapter.StaggeredAdapter;
import com.lindo.collector.domain.Doings;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.lib.internal.PLA_AdapterView;
import com.lindo.collector.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.view.SlideShowView;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;
import com.lindo.collector.view.XListView;
import com.lindo.collector.view.XListView.IXListViewListener;

public class HomeFragment extends Fragment implements OnClickListener, IXListViewListener{
	
	private static final String TAG = "HomeFragment";
	private static XListView mListView;
	private View mLoadView;
	private LayoutInflater mInflater;
	private View headerView;
	private SlideShowView slideShowView;
	
	private ImageFetcher mImageFetcher;
    private static StaggeredAdapter mStaggeredAdapter = null;
	private static boolean mIsStart = false;
	private int mCurPageIndex = 1;
	private static int totalRecord = 0;
	
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);  
		mLoadView = view.findViewById(R.id.load_view);
		mListView = (XListView) view.findViewById(R.id.refreshable_view);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mHandler.sendEmptyMessage(GET_TASK_LIST_DATA);

		mListView.setSelector(android.R.color.transparent);
		mListView.setCacheColorHint(android.R.color.transparent);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
        
		mInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerView = mInflater.inflate(R.layout.layout_header_view, null);
		slideShowView = (SlideShowView) headerView.findViewById(R.id.slideshowView);
		slideShowView.refreshView();
		mListView.addHeaderView(headerView);
		
		mImageFetcher = new ImageFetcher(getActivity(), 240);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        
        mStaggeredAdapter = new StaggeredAdapter(getActivity(), mImageFetcher);
        
        
        mImageFetcher.setExitTasksEarly(false);
        mListView.setAdapter(mStaggeredAdapter);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "onItemClick mStaggeredAdapter.getCount()  " + mStaggeredAdapter.getCount());
				Doings doings = (Doings) mStaggeredAdapter.getItem(position-2);
				
				PushApplication myApplication = PushApplication.getInstance();
		        if (myApplication.isLogin()) {
		        	if (doings.join_state == 1) {
						IntentUtil.startActivity(getActivity(), PerformTaskActivity.class,
								new BasicNameValuePair("id", doings.id),
								new BasicNameValuePair("url", doings.detaiUrl), 
								new BasicNameValuePair("thumb", doings.thumbUrl),
								new BasicNameValuePair("score", String.valueOf(doings.score)), 
								new BasicNameValuePair("uploadPicCount", String.valueOf(doings.upload_pic_number)), 
								new BasicNameValuePair("isJoined", String.valueOf(doings.join_state)), 
								new BasicNameValuePair("title", doings.name));
		    		} else {
		    			isJoinNewDoings = true;
						IntentUtil.startActivity(getActivity(), TaskDetailsActivity.class,
								new BasicNameValuePair("id", doings.id),
								new BasicNameValuePair("url", doings.detaiUrl), 
								new BasicNameValuePair("thumb", doings.thumbUrl), 
								new BasicNameValuePair("total", String.valueOf(doings.participation_number)), 
								new BasicNameValuePair("score", String.valueOf(doings.score)), 
								new BasicNameValuePair("uploadPicCount", String.valueOf(doings.upload_pic_number)), 
								new BasicNameValuePair("isJoined", String.valueOf(doings.join_state)), 
								new BasicNameValuePair("title", doings.name));
		    		}
		        	
				} else {
					isJoinNewDoings = true;
					IntentUtil.startActivity(getActivity(), TaskDetailsActivity.class,
							new BasicNameValuePair("id", doings.id),
							new BasicNameValuePair("url", doings.detaiUrl), 
							new BasicNameValuePair("thumb", doings.thumbUrl), 
							new BasicNameValuePair("total", String.valueOf(doings.participation_number)),
							new BasicNameValuePair("score", String.valueOf(doings.score)), 
							new BasicNameValuePair("uploadPicCount", String.valueOf(doings.upload_pic_number)), 
							new BasicNameValuePair("isJoined", String.valueOf(doings.join_state)), 
							new BasicNameValuePair("title", doings.name));
				}
		        
				
			}
		});
        
        setLastUpdateTime();
        
        /*int w = getActivity().getResources().getDisplayMetrics().widthPixels;
        int h = getActivity().getResources().getDisplayMetrics().heightPixels;
        Log.d(TAG, "w = " + w);
        Log.d(TAG, "h = " + h);
        float scale = getActivity().getResources().getDisplayMetrics().density; 
        Log.d(TAG, "scale = " + scale);
        int m = (int) (368 / scale + 0.5f);
        Log.d(TAG, "m = " + m);*/
	}

	private void setLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		mListView.setLastUpdatedLabel(text);
	}
	
	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}

		return mDateFormat.format(new Date(time));
	}
	
	private boolean isJoinNewDoings = false;
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume isJoinNewDoings = " + isJoinNewDoings);
		if (isJoinNewDoings) {
			onRefresh();
			isJoinNewDoings = false;
		}
	}

	public static void refreshList(){
		if (null != mStaggeredAdapter) {
			mListView.setAdapter(mStaggeredAdapter);
			mStaggeredAdapter.notifyDataSetChanged();
		}
		
	}
	
	/**
	 *下拉刷新 
	 */
	@Override
	public void onRefresh() {
		mListView.setPullLoadEnable(false);
		mIsStart = true;
		/*slideShowView.refreshView();*/
		Log.d(TAG, "onRefresh GET_TASK_LIST_DATA ");
		mHandler.sendEmptyMessage(GET_TASK_LIST_DATA);
	}

	/**
	 * 上拉加载更多
	 */
	@Override
	public void onLoadMore() {
		mIsStart = false;
		if (mStaggeredAdapter.getCount() == totalRecord) {
			if (!mIsStart) {
				AppToast.showShortText(getActivity(), "没有更多任务了！");
			}
			return;
		}
		Log.d(TAG, "onLoadMore GET_TASK_LIST_DATA ");
		mHandler.sendEmptyMessage(GET_TASK_LIST_DATA);
	}
	
	private static final int GET_TASK_LIST_DATA = 1002;
	private static final int UPDATAE_TASK_LIST = 1004;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_TASK_LIST_DATA:
				String url = getRequestUrl(mIsStart);
				requestTaskData(url);
				break;
			case UPDATAE_TASK_LIST:
				String json = (String) msg.obj;
				List<Doings> data = getTaskListByJosn(json);

				if (null == data) {
					return;
				}
				
				Log.d(TAG, "mListItems mIsStart = " + mIsStart);
				Log.d(TAG, "data.size() = " + data.size());
				Log.d(TAG, "totalRecord = " + totalRecord);

				boolean isAdd = mStaggeredAdapter.getCount() + data.size() <= totalRecord;
				Log.d(TAG, "isAdd = " + isAdd);
				if (isAdd) {
					if (mIsStart) {
						mStaggeredAdapter.clearData();
						//mStaggeredAdapter.addItemTop(data);暂时先不用此种方式
						mStaggeredAdapter.addItemLast(data);
						mListView.stopRefresh();
					} else {
						mStaggeredAdapter.addItemLast(data);
						mListView.stopLoadMore();
						
					}
				}else {
					if (mIsStart) {
						mStaggeredAdapter.clearData();
						mStaggeredAdapter.addItemLast(data);
						mListView.stopRefresh();
					} else {
						mListView.stopLoadMore();
						
					}
				}
				
				Log.d(TAG, "mStaggeredAdapter.getCount() = " + mStaggeredAdapter.getCount());
				
				if (mStaggeredAdapter.getCount() == totalRecord) {
					Log.d(TAG, "没有数据了，不再加载");
					mListView.setPullLoadEnable(false);
				}

				mLoadView.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				mStaggeredAdapter.notifyDataSetChanged();

				mIsStart = false;
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 1:
			IntentUtil.startActivity2(getActivity(), UserLoginActivity.class);
			
			break;

		default:
			break;
		}
	}
	
	private String getRequestUrl(boolean isStart) {
		if ((mStaggeredAdapter.getCount() == 0) || isStart) {
			mCurPageIndex = 1;
		} else {
			++mCurPageIndex;
		}

		String comUrl = Constant.DOMAIN + "/app/task/tasklist?p=" + mCurPageIndex;
		return comUrl;
	}
	
	private void requestTaskData(String url) {
		Log.d(TAG, "processBusinessData url = " + url);
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(getActivity());
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String json = new String(arg2);
				if (1 == JsonUtil.getStateFromServer(json)) {
					mHandler.obtainMessage(UPDATAE_TASK_LIST, json)
							.sendToTarget();
				} else {
					AppToast.showShortText(getActivity(), "获取任务数据超时");
				}
			}

		});

	}

	private List<Doings> getTaskListByJosn(String json) {
		/*Log.d(TAG, " getTaskListByJosn = " + json);*/
		List<Doings> list = new ArrayList<Doings>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			totalRecord = jsonObject.getInt("total");
			String imgPath = jsonObject.getString("attachment_path");
			int pic_height = (int) getResources().getDimension(R.dimen.home_task_pic_height);
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject object = jsonArray.getJSONObject(i);
			    
			    Doings task = new Doings();
			    task.id = object.getString("id");
			    task.name = object.getString("title");
				task.thumbUrl = imgPath + object.getString("example_photo");
				task.detaiUrl = object.getString("detail_url");
				task.reward = object.getInt("reward_point");
				task.requirement = object.getString("requirement");
				task.participation_number = object.getInt("apply_num");
				task.upload_pic_number = object.getInt("uploaded_img_num");
				task.score = object.getInt("get_point");
				task.height = pic_height;
				task.state = object.getInt("status");
				task.join_state = object.getInt("is_join");
			    list.add(task);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

}
