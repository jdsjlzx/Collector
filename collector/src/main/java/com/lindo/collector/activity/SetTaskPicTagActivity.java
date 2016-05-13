package com.lindo.collector.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.lindo.collector.R;
import com.lindo.collector.dao.TaskService;
import com.lindo.collector.domain.Tag;
import com.lindo.collector.domain.Task;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.CacheConfig;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.Utils;
import com.lindo.collector.view.LineBreakLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SetTaskPicTagActivity extends Activity implements OnClickListener{

	private static final String TAG = "SetTaskPicTagActivity";
	private String mDoingsId;
	
	private ImageView mCloseImage;
	private ImageView mFinishImage;
	private TextView mPicTagCountText;
	private View mLoadView;
	private LineBreakLayout mLineBreakLayout;
	
	private Set<Tag> mTagSet;//用户选择的tag
	List<Tag> deletedList = new ArrayList<Tag>();//需要删除的tag集合
	
	private List<Tag> mSuperTagList = new ArrayList<Tag>();//一级标签
	private Map<String, List<Tag>> mSubTagsMap = new LinkedHashMap<String, List<Tag>>();//二级标签
	private Map<String, List<Tag>> mThirdLevelTagsMap = new LinkedHashMap<String, List<Tag>>();//三级标签
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_task_pic_tag);
		
		initView();
		initData();
	}
	
	private void initView() {
		mCloseImage = (ImageView) findViewById(R.id.close_image);
		mFinishImage = (ImageView) findViewById(R.id.finish_image);
		mPicTagCountText = (TextView) findViewById(R.id.pic_tag_count_text);
		mLoadView = findViewById(R.id.load_view);
		mLineBreakLayout = (LineBreakLayout) findViewById(R.id.lineBreakLayout);
		
		mCloseImage.setOnClickListener(this);
		mFinishImage.setOnClickListener(this);
	}

	private void initData() {
		mTagSet = getSelectedTagList();
		mDoingsId = getIntent().getStringExtra("doings_id");
		String url = Constant.TASK_PIC_TAG_URL + "?task_id=" + mDoingsId;
		String cacheJSON = CacheConfig.getUrlCache(url);
		Log.d(TAG, "initData cacheJSON = " + cacheJSON);
		if (TextUtils.isEmpty(cacheJSON)) {
			getTaskPicTags(url);
		} else {// 读取缓存
			Log.d(TAG, "read tag info from cache");
			getTagListByJson(cacheJSON);
			mHandler.sendEmptyMessage(UPDATE_SUPER_LIST);
		}
		
	}
	
	private void updatePicTagCountView(int count) {
		int total = mThirdLevelTagsMap.size();
		String tagCountText = String.format(getResources().getString(R.string.task_pic_tag_count), count,total); 
		mPicTagCountText.setText(tagCountText);
	}

	private TreeSet<Tag> getSelectedTagList() {
		TreeSet<Tag> tagSet = new TreeSet<Tag>();
		String tagIdText = getIntent().getStringExtra("task_tag_id");
		String[] tagIdArray = Utils.getStringArrayFromText(tagIdText, ",");
		
		Collection<List<Tag>> values = mThirdLevelTagsMap.values();
		for (List<Tag> list : values) {
			for (Tag tag : list) {
				if (null != tagIdText) {
					for (int i = 0; i < tagIdArray.length; i++) {
						String tagId = tagIdArray[i];
						if (tag.id.equals(tagId)) {
							tagSet.add(tag);
						}
					}
				}
			}
		}
		/*String tagTitleText = getIntent().getStringExtra("task_tag_title");
		String[] tagTitleArray = getStringArrayFromText(tagTitleText, ";");
		
		String subTagIdText = getIntent().getStringExtra("task_sub_tag_id");
		String[] subTagIdArray = getStringArrayFromText(subTagIdText, ",");
		
		Log.d(TAG, "tagTitleText = " + tagTitleText);
		Log.d(TAG, "tagIdText = " + tagIdText);
		Log.d(TAG, "subTagIdText = " + subTagIdText);*/
		
		return tagSet;
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_image:
			finish();
			
			break;
		case R.id.finish_image:
			TaskService taskService = new TaskService(SetTaskPicTagActivity.this);
			int taskId = getIntent().getIntExtra("task_id", 0);
			String tagText = getTagTitleCharacter(mTagSet);
			String tagIdText = getTagIdCharacter(mTagSet);
			String subTagIdText = getSubTagIdCharacter(mTagSet);
			Task task = taskService.getUndoTask(taskId);
			Log.d(TAG, "taskId "  + taskId);
			task.tag = tagText;
			task.tagId = tagIdText;
			task.subTagId = subTagIdText;
			task.subTagCount = mThirdLevelTagsMap.size();
			String result = Utils.checkTagIsValid(SetTaskPicTagActivity.this, task.subTagId, task.subTagCount);
			task.subTagIsValid = TextUtils.isEmpty(result) ? 1 : 0;//标签是否有效
			boolean isSuccess = taskService.updateUndoTask(taskId, task);
			if (isSuccess) {
				Intent intent = new Intent(SetTaskPicTagActivity.this, PerformTaskActivity.class);
				intent.putExtra("task_id", taskId);
				setResult(RESULT_OK,intent); 
				finish();
			}
			
			break;

		default:
			break;
		}
	}
	
	private String getTagTitleCharacter(Set<Tag> tagSet){
		StringBuilder sb = new StringBuilder();
		for (Tag tag : tagSet) {
			sb.append(tag.title).append(";");
		}
		String result = sb.toString();
		if (!TextUtils.isEmpty(result)) {
			result = result.substring(0, result.length()-1);
		}
		
		return result;
	}
	
	private String getTagIdCharacter(Set<Tag> tagSet){
		StringBuilder sb = new StringBuilder();
		for (Tag tag : tagSet) {
			sb.append(tag.id).append(",");
		}
		String result = sb.toString();
		if (!TextUtils.isEmpty(result)) {
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	
	private String getSubTagIdCharacter(Set<Tag> tagSet){
		StringBuilder sb = new StringBuilder();
		for (Tag tag : tagSet) {
			sb.append(tag.parentId).append(",");
		}
		String result = sb.toString();
		if (!TextUtils.isEmpty(result)) {
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	
	int subTagTotal = 0;
	
	private static final int UPDATE_SUPER_LIST = 1001;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_SUPER_LIST:
				updatePicTagCountView(mTagSet.size());
				Collection<List<Tag>> values = mThirdLevelTagsMap.values();
				for (List<Tag> list : values) {
					for (int i = 0; i < list.size(); i++) {
						final Tag tag = list.get(i);
						final CheckBox box = new CheckBox(SetTaskPicTagActivity.this, null,
								android.R.attr.imageButtonStyle);
						box.setChecked(false);
						box.setBackgroundResource(R.drawable.selector_tag_checkbox);
						ColorStateList colorStateList = getResources().getColorStateList(R.color.checkbox_text_color);
						box.setTextColor(colorStateList);
						box.setTextSize(getResources().getDimension(R.dimen.subitem_textSize));
						box.setText(tag.title);
						box.setTag(tag.id);
						box.setGravity(Gravity.CENTER);
						int PAD_W = (int) getResources().getDimension(R.dimen.checkbox_padding_horizontal);
						int PAD_V = (int) getResources().getDimension(R.dimen.checkbox_padding_vertical);
						box.setPadding(PAD_W, PAD_V, PAD_W, PAD_V);
						box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								Log.d(TAG, "onCheckedChanged tag id = " + tag.id + "  isChecked = " + isChecked);
								if (isChecked) {
									Log.d(TAG, "add一个tag id = " + tag.id);
									checkIsRepeat(tag);
									mTagSet.add(tag);
								}else {
									Log.d(TAG, "remove一个tag id = " + tag.id);
									mTagSet.remove(tag);
									
								}
								updatePicTagCountView(mTagSet.size());
								Log.d(TAG, "mTagSet.size()  " + mTagSet.size());
								updateCheckboxState(isChecked, tag.parentId);
							}

						});
						
						mLineBreakLayout.addView(box);
					}
					
				}
				
				initCheckboxState();
				mLoadView.setVisibility(View.GONE);
				mLineBreakLayout.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		};
	};
	
	
	/**
	 * 检查是否重复，如果有重复就删除
	 * @param tag
	 */
	private void checkIsRepeat(Tag tag){
		for (Tag selectedTag: mTagSet) {
			if (selectedTag.parentId.equals(tag.parentId)) {
				Log.d(TAG, "checkIsRepeat 重复的tagId = " + selectedTag.id);
				deletedList.add(selectedTag);
				/*mTagSet.remove(selectedTag);*/  //容易导致异常： java.util.ConcurrentModificationException
			}
		}
		mTagSet.removeAll(deletedList);
	}

	private void initCheckboxState() {
		String tagIdText = getIntent().getStringExtra("task_tag_id");
		if (TextUtils.isEmpty(tagIdText)) {
			return;
		} else {
			String[] tagIdArray = Utils.getStringArrayFromText(tagIdText, ",");
			String subTagIdText = getIntent().getStringExtra("task_sub_tag_id");
			String[] subTagIdArray = Utils.getStringArrayFromText(subTagIdText, ",");
			
			if (null != subTagIdArray) {
				for (int i = 0; i < subTagIdArray.length; i++) {
					String selectedSubTagId = subTagIdArray[i];
					List<Tag> thirdTaglist = mThirdLevelTagsMap.get(selectedSubTagId);
					for (Tag thirdTag : thirdTaglist) {//用户选择的二级标签下的三级标签
						//相同的二级标签才做比较
						//根据二级标签的索引得到选择的三级标签
						String tagId = tagIdArray[i];//二级标签下只能选择一个三级标签
						if (thirdTag.id.equals(tagId)) {//不能用==
							
							Log.e(TAG, "设置为可以用 tagId = " + thirdTag.id);
							CheckBox box = (CheckBox) mLineBreakLayout.findViewWithTag(thirdTag.id);
							box.setChecked(true);
							box.setEnabled(true);
						}else {
							Log.d(TAG, "不可用 tagId = " + thirdTag.id);
							CheckBox box = (CheckBox) mLineBreakLayout.findViewWithTag(thirdTag.id);
							/*box.setEnabled(false);*/
							/*box.setChecked(false);*/
						}
						
						
					}
	
				}
			}
			
		}
		
		
	}
	
	private void updateCheckboxState(boolean isChecked, String parentId) {
		List<Tag> list = mThirdLevelTagsMap.get(parentId);
		for (Tag tag : list) {
			CheckBox box = (CheckBox) mLineBreakLayout.findViewWithTag(tag.id);
			if (isChecked) {
				for (Tag selectedTag : mTagSet) {
					if (selectedTag.parentId.equals(parentId)) {
						if (tag.id == selectedTag.id) {
							Log.d(TAG, "updateCheckboxState setEnabled true id = " + tag.id);
							box.setEnabled(true);
						}else {
							Log.d(TAG, "updateCheckboxState setEnabled false id = " + tag.id);
							box.setEnabled(false);
						}
					}
					
				}
			} else {
				Log.d(TAG, "noChecked updateCheckboxState setEnabled false id = " + tag.id);
				box.setEnabled(true);
			}
			
			
			
		}
	}

	private void getTaskPicTags(final String url){
		Log.d(TAG, "getTaskPicTags url = " + url);
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
		RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
		client.get(url, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String json = new String(responseBody);
				 Log.d(TAG, "onSuccess " + json);
				 CacheConfig.setUrlCache(json, url);
				 getTagListByJson(json);
				 mHandler.sendEmptyMessage(UPDATE_SUPER_LIST);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Log.e(TAG, "onFailure " , error);
				 
				
			}
		});
	
	}
	
	private void getTagListByJson(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray superTagsArray = jsonObject.getJSONArray("data");
			if (null != superTagsArray) {
				for (int i = 0; i < superTagsArray.length(); i++) {
					JSONObject superTagObject = superTagsArray.getJSONObject(i);
					Tag superTag = new Tag();
					superTag.id = superTagObject.getString("id");
					superTag.title = superTagObject.getString("title");
					mSuperTagList.add(superTag);
					JSONArray subTagsArray = superTagObject.getJSONArray("children");
					List<Tag> mSubTagList = new ArrayList<Tag>();
					for (int j = 0; j < subTagsArray.length(); j++) {
						JSONObject subTagObject = subTagsArray.getJSONObject(j);
						Tag subTag = new Tag();
						subTag.id = subTagObject.getString("id");
						subTag.title = subTagObject.getString("title");
						subTag.parentId = superTag.id;
						mSubTagList.add(subTag);
						JSONArray thirdTagsArray = subTagObject.getJSONArray("children");
						List<Tag> thirdTagList = new ArrayList<Tag>();
						for (int k = 0; k < thirdTagsArray.length(); k++) {
							JSONObject thirdTagObject = thirdTagsArray.getJSONObject(k);
							Tag thirdTag = new Tag();
							thirdTag.id = thirdTagObject.getString("id");
							thirdTag.title = thirdTagObject.getString("title");
							thirdTag.parentId = subTag.id;
							thirdTagList.add(thirdTag);
						}
						mThirdLevelTagsMap.put(subTag.id, thirdTagList);
					}
					mSubTagsMap.put(superTag.id, mSubTagList);
					
				}
			}
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
