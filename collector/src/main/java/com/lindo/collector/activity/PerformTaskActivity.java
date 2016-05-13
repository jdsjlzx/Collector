package com.lindo.collector.activity;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.activity.adapter.TaskPageAdapter;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.dao.TaskService;
import com.lindo.collector.domain.JoinedDoings;
import com.lindo.collector.domain.Task;
import com.lindo.collector.http.FinalAsyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.FileUtil;
import com.lindo.collector.utils.JsonUtil;
import com.lindo.collector.utils.LogUtils;
import com.lindo.collector.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;

public class PerformTaskActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = "PerformTaskActivity";
	private ViewPager mViewPager;
	private TaskPageAdapter mPageAdapter;
	private LinkedList<Task> mListItems;
	
	private View mDoneTaskView;
	private View mUndoTaskView;
	private View mRemainderTaskView;
	
	private TextView mDoneTaskText;
	private TextView mUndoTaskText;
	private TextView mRemainderTaskText;
	private Button mUploadBtn;
	private TextView mPicStatusText;
	private ImageView mRightBtn;
	
	private Intent intent;
	private TaskService mTaskService;
	private String mDoingsId;
	private int mCount = 0;//起始数据
	private JoinedDoings mJoinedDoings;
	private Uri photoUri;
	private String photoPath;
	private boolean isEditPic;//是否在编辑图片
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perform_task);
		initView();
		initData();
	}
	
	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		
		mDoneTaskText = (TextView)findViewById(R.id.uploaded_pic_text);
		mUndoTaskText = (TextView)findViewById(R.id.undo_task_pic_text);
		mRemainderTaskText = (TextView)findViewById(R.id.remainder_task_pic_text);
		mDoneTaskView = findViewById(R.id.uploaded_pic_layout);
		mUndoTaskView = findViewById(R.id.undo_task_pic_layout);
		mRemainderTaskView = findViewById(R.id.remainder_task_pic_layout);
		
		mViewPager = (ViewPager) findViewById(R.id.task_viewpager);
		mUploadBtn = (Button) findViewById(R.id.next_button);
		mPicStatusText = (TextView) findViewById(R.id.pic_status);
		mRightBtn = (ImageView) findViewById(R.id.right_img);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setImageResource(R.drawable.ic_task_detail);
		
		//设置页与页之间的间距  
		mViewPager.setOffscreenPageLimit(3);
		
		mUploadBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		mDoneTaskView.setOnClickListener(this);
		mDoneTaskView.setOnClickListener(this);
		mUndoTaskView.setOnClickListener(this);
		mRemainderTaskView.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		
	}
	
	private void initData() {
		intent = getIntent();
		mTitleText.setText(intent.getStringExtra("title"));
		mDoingsId = intent.getStringExtra("id");
		
		mListItems = new LinkedList<Task>();
		mTaskService = new TaskService(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (PushApplication.getInstance().isLogin()) {
			//第一次进入页面
			if (null == mJoinedDoings) {
				mHandler.sendEmptyMessage(GET_JOINED_TASK_LIST_DATA);
			} else {
				int remainderNum = mJoinedDoings.require_photo_num-mJoinedDoings.uploaded_photo_num;
				if (remainderPicNum != remainderNum) {
					mHandler.sendEmptyMessage(GET_JOINED_TASK_LIST_DATA);
					remainderPicNum = remainderNum;
				} else {
					//数据发生变化，重新获取数据刷新界面
					int total = (int) mTaskService.getCount(mDoingsId);
					Log.d(TAG, "waitToUploadPicNum = " + waitToUploadPicNum);
					if (waitToUploadPicNum != total) {
						mHandler.sendEmptyMessage(GET_JOINED_TASK_LIST_DATA);
						waitToUploadPicNum = total;
					} else {//剩余数量和待上传数量没有发生变化
						//跳转到指定图片
						int position = getIntent().getIntExtra("position", -1);
						if (position > -1) {
							mCurIndex = position;
							mViewPager.setCurrentItem(mCurIndex);
						}
					}
				}
				updateTopViewText();
			}
			
		}else {
			IntentUtil.startActivity2(this, UserLoginActivity.class);
			finish();
		}
		
	}
	
	
	private int waitToUploadPicNum;
	private int remainderPicNum;
	private int uploadedPicNum;
	private void updateTopViewText(){
		if (null != mTaskService) {
			waitToUploadPicNum = (int) mTaskService.getCount(mDoingsId);
			mUndoTaskText.setText(String.valueOf(waitToUploadPicNum));
			Log.e(TAG, "updateTopViewText  waitToUploadPicNum = " + waitToUploadPicNum);
		}
		if (null != mJoinedDoings) {
			uploadedPicNum = mJoinedDoings.uploaded_photo_num;
			mDoneTaskText.setText(String.valueOf(uploadedPicNum));
			
			remainderPicNum = mJoinedDoings.require_photo_num-mJoinedDoings.uploaded_photo_num;
			
			if (remainderPicNum > 0) {
				mRemainderTaskText.setText(String.valueOf(remainderPicNum));
			} else {
				mRemainderTaskText.setText(R.string.task_state_under_review);
				mRemainderTaskText.setTextSize(15.0f);
				mRemainderTaskText.setTextColor(Color.BLACK);
				findViewById(R.id.remainder_text).setVisibility(View.GONE);
			}
			
			switch (mJoinedDoings.state) {
			case 1:
				mRemainderTaskText.setText(String.valueOf(remainderPicNum));
				break;
			case 2:
			case 3:
				if (mJoinedDoings.state == 2) {
					mRemainderTaskText.setText(R.string.task_state_under_review);
				} else if (mJoinedDoings.state == 3){
					mRemainderTaskText.setText(R.string.task_state_finished);
				}
				
				mRemainderTaskText.setTextSize(15.0f);
				mRemainderTaskText.setTextColor(Color.BLACK);
				findViewById(R.id.remainder_text).setVisibility(View.GONE);
				break;

			default:
				break;
			}
			
		}
	}
	
	private Task mCurTask;//当前
	private int mCurIndex;
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int position) {
		}

		@Override 
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
		}

		@Override
		public void onPageSelected(int position) {
			Log.d(TAG, "onPageSelected position = " + position);
			if (position == mViewPager.getAdapter().getCount()-1 && (position<= mCount-1)) {
				if (!isDeleteTask && (remainderPicNum > 0)) {
					Log.d(TAG, "onPageSelected addLastListData");
					addLastListData();// listViews添加数据
					mPageAdapter.setListDatas(mListItems);;// 重构adapter对象
					mPageAdapter.notifyDataSetChanged();// 刷新
				}
				
			}
			if (mListItems.size() == (position+1)) {
				mCurIndex = position;
				/*mCurTask = mListItems.get(position);*/ //放入initButtonState方法中
			}
			if (isDeleteTask) {
				initButtonState(position-1);
				isDeleteTask = false;
			} else {
				initButtonState(position);
			}
			
		}
		
	};
	
	private void addLastListData() {
		Log.d(TAG, "addLastListData 添加一个数据");
		Task nextTask = new Task();
		nextTask.state = -1;
		nextTask.index = mListItems.size();
		nextTask.picPath = intent.getStringExtra("thumb");
		nextTask.participantDoingsId = mDoingsId;
		mListItems.addLast(nextTask);
	}
	
	private boolean isDeleteTask = false;
	public void deleteTask(int position){
		Log.d(TAG, "deleteTask 删除一个数据 position = " + position);
		isDeleteTask = true;
		mListItems.remove(position);
		/*mPageAdapter = new TaskPageAdapter(PerformTaskActivity.this, mListItems, mHandler);*/
		mPageAdapter.setListDatas(mListItems);;// 重构adapter对象
		mViewPager.setAdapter(mPageAdapter);
		mPageAdapter.notifyDataSetChanged();// 刷新
		mViewPager.setCurrentItem(mPageAdapter.getCount());
		updateTopViewText();
	}
	
	public static final int CHOOSE_PIC_BY_TAKE_PHOTO = 1001;//使用照相机拍照获取图片
	public static final int CHOOSE_PIC_BY_PICK_PHOTO = 1002;//使用相册中的图片
	public static final int CHOOSE_PIC_TAG = 1003;//使用相册中的图片
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult requestCode = " + requestCode);
		
		if (resultCode == Activity.RESULT_OK) {
			
			switch (requestCode) {
			case CHOOSE_PIC_BY_TAKE_PHOTO:
				// 选择自拍结果
				Log.d(TAG, "photoPath = " + photoPath);
                FileUtil.compressPicture(photoPath, photoPath);
                saveTaskPicture(photoPath, getResources().getString(R.string.task_pic_source_camera));
				break;
			case CHOOSE_PIC_BY_PICK_PHOTO:
				
				// 选择图库图片结果
				if (null != data) {
					Uri uri = data.getData();
					String filePath = com.lzx.work.utils.Utils.getRealFilePath(this, uri);
					try {
						String picPath = FileUtil.generatePicName();
						File srcFile = new File(filePath);
						if (srcFile.exists()) {
							FileUtil.copyFile(srcFile, new File(picPath));
							FileUtil.compressPicture(filePath, picPath);
							saveTaskPicture(picPath, getResources().getString(R.string.task_pic_source_album));
						}else {
							AppToast.showShortText(PerformTaskActivity.this, "亲，你选择的图片我找不到位置啊，换一张试试吧");
						}
						
					} catch (Exception e) {
						Log.e(TAG, e.toString());
						AppToast.showShortText(PerformTaskActivity.this, "亲，对不起，我要离开您一会了");
					}
					
				}
				
				break;
			case CHOOSE_PIC_TAG:
				// 设置标签
				Log.d(TAG, "CHOOSE_PIC_TAG");
				
				if (null != data) {
					Task task = mTaskService.getUndoTask(data.getIntExtra("task_id", -1));
					mListItems.remove(mCurTask);
					mCurTask = task;
					mListItems.add(mCurIndex, mCurTask);
					mPageAdapter.updateItemView(mCurIndex, mCurTask);
				}
				break;
			default:
				break;
			}

		}
	}
	
	
	private void saveTaskPicture(String filePath, String source){
		Log.d(TAG, "handlePicture filePath = " + filePath);
		Log.d(TAG, "mCurTask is null ? " + (mCurTask==null));
		if (null == mCurTask) {
			mCurTask = new Task();
		}
		mCurTask.picPath = filePath;
		mCurTask.picSrc = source;
		mCurTask.state = 0;
		mCurTask.tag = null;
		mCurTask.participantDoingsId = mDoingsId;
		//从数据库查询到的task信息
		Task task = mTaskService.saveUndoTask(mCurTask);
		mListItems.remove(mCurTask);
		mCurTask = task;
		mListItems.add(mCurIndex, mCurTask);
		mPageAdapter.updateItemView(mCurIndex, mCurTask);
		
		Log.d(TAG, "saveTaskPicture addLastListData");
		addLastListData();
		mPageAdapter.setListDatas(mListItems);;// 重构adapter对象
		mPageAdapter.notifyDataSetChanged();// 刷新
		
	}
	
	
	/**
	 * 设置按钮状态
	 * @param mUploadBtn.setEnabled(true);
	 */
	private void initButtonState(int position){
		Log.d(TAG, "initButtonState position" + position);
		mCurTask = mListItems.get(position);
		if (mCurTask.state == 1) {
			mUploadBtn.setVisibility(View.GONE);
			mPicStatusText.setVisibility(View.VISIBLE);
			String[] data = getResources().getStringArray(R.array.pic_auditing_states);
			mPicStatusText.setText(data[mCurTask.pic_status-1]);
		} else {
			mUploadBtn.setVisibility(View.VISIBLE);
			mPicStatusText.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(mCurTask.picPath)
					/*&& !TextUtils.isEmpty(mCurTask.tag)*/) {
				mUploadBtn.setEnabled(true);
			} else {
				mUploadBtn.setEnabled(false);
			}
		}
		
	}
	
	
	public void choosePicByCamera() {
		// 现拍取图片
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (!SDState.equals(Environment.MEDIA_MOUNTED)) {
			AppToast.showShortText(this, "没有找到SD卡或者正在使用请关闭usb连接模式");
			return;
		}
		try {
			photoPath = FileUtil.generatePicName();
			File photo =new File(photoPath);
			photoUri = Uri.fromFile(photo);
			/*photoUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new ContentValues());*/
			if (photoUri != null) {
				Intent i = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(i, CHOOSE_PIC_BY_TAKE_PHOTO);

			} else {
				AppToast.showShortText(this, "发生意外，无法写入相册");
			}
		} catch (Exception e) {
			e.printStackTrace();
			AppToast.showShortText(this, "发生意外，无法写入相册");
		}
	}
	
	public void choosePicByPhoto() {
		// 从相册中取图片
		Intent intent = new Intent();
		intent.setType("image/*"); // 设置文件类型
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(intent, CHOOSE_PIC_BY_PICK_PHOTO);
	}

	private LinkedList<Task> tempTaskList = new LinkedList<Task>();
	private List<Task> localTaskList;
	private static final int GET_JOINED_TASK_LIST_DATA = 1000;
	private static final int UPDATE_JOINED_TASK_LIST_DATA = 1001;
	private static final int GET_LOCAL_TASK_LIST_DATA = 1002;
	private static final int UPDATAE_TASK_LIST = 1003;
	private static final int TASK_PIC_UPLOAD_SUCCESS = 1004;
	public static final int PICK_PIC_FROM_PHOTO = 1006;
	public static final int PICK_PIC_FROM_CAMERA = 1007;
	public static final int UPDATE_UPLOAD_BUTTON_STATE = 1008;
	public static final int DELETE_TASK = 1009;
	public static final int SET_TASK_PIC_TAG = 1010;
	public static final int SET_TASK_PIC_SRC = 1011;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.d(TAG, "handleMessage what = " + msg.what);
			switch (msg.what) {
			case GET_JOINED_TASK_LIST_DATA:
				getJoinedDoingsInfo(mDoingsId);
				break;
			case UPDATE_JOINED_TASK_LIST_DATA:
				Log.d(TAG, "mJoinedDoings.taskList size = " + mJoinedDoings.taskList.size());
				if (null != mJoinedDoings) {
					
					tempTaskList.addAll(mJoinedDoings.taskList);
				}
				
				sendEmptyMessage(GET_LOCAL_TASK_LIST_DATA);
				break;
			case GET_LOCAL_TASK_LIST_DATA:
				localTaskList = mTaskService.getUndoTasks(mDoingsId);
				tempTaskList.addAll(tempTaskList.size(), localTaskList);
				updateTopViewText();//don't move
				sendEmptyMessage(UPDATAE_TASK_LIST);
				break;
			case UPDATAE_TASK_LIST:
				closeDialog();
				
				mListItems.clear();
				mListItems.addAll(tempTaskList);
				Collections.sort(mListItems, comparator); 
				mCount = mListItems.size();
				
				Log.d(TAG, "UPDATAE_TASK_LIST isDeleteTask = " + isDeleteTask);
				Log.d(TAG, "mCount = " + mCount);
				if (!isDeleteTask) {
					if (remainderPicNum > 0) {
						Log.d(TAG, "UPDATAE_TASK_LIST addLastListData");
						addLastListData();
					}
					
				}
				
				mPageAdapter = new TaskPageAdapter(PerformTaskActivity.this, mListItems, mHandler);
				mViewPager.setAdapter(mPageAdapter);
				
				Log.d(TAG, "mPageAdapter.getCount() = " + mPageAdapter.getCount());
				int desIndex = getIntent().getIntExtra("position", -1);
				Log.d(TAG, "UPDATAE_TASK_LIST desIndex = " + desIndex);
				if (isEditPic) {
					Log.d(TAG, "UPDATAE_TASK_LIST isEditPic = " + isEditPic);
					mViewPager.setCurrentItem(mCurIndex);
				} else if (getIntent().getIntExtra("position", -1)>-1) {
					mCurIndex = desIndex;
					mViewPager.setCurrentItem(mCurIndex);
				} else {
					mViewPager.setCurrentItem(mPageAdapter.getCount());
				}
				
				mCurIndex = mViewPager.getCurrentItem();
				Log.d(TAG, "mCurIndex = " + mCurIndex);
				
				//update current task
				mCurTask = mListItems.get(mCurIndex);
				
				tempTaskList.clear();
				break;
			case TASK_PIC_UPLOAD_SUCCESS:
				closeDialog();
				Log.d(TAG, "上传成功，您可继续添加图片！");
				AppToast.showShortText(PerformTaskActivity.this, "上传成功！");
				mHandler.sendEmptyMessage(GET_JOINED_TASK_LIST_DATA);
				break;
			case PICK_PIC_FROM_CAMERA:
				if (isAllowedToUploadPic()) {
					isEditPic = true;
					choosePicByCamera();
				}
				
				break;
			case PICK_PIC_FROM_PHOTO:
				if (isAllowedToUploadPic()) {
					isEditPic = true;
					choosePicByPhoto();
				}
				
				break;
			case UPDATE_UPLOAD_BUTTON_STATE:
				initButtonState(mCurIndex);
				break;
			case DELETE_TASK:
				int position = msg.arg1;
				showConfirmDialog(position);
				break;
			case SET_TASK_PIC_TAG:
				Log.d(TAG, "SET_TASK_PIC_TAG mCurTask.id = " + mCurTask.id);
				isEditPic = true;
				if (TextUtils.isEmpty(mCurTask.tag)) {
					mCurTask.tagId = null;
					mCurTask.subTagId = null;
				}
				Intent i = new Intent(PerformTaskActivity.this, SetTaskPicTagActivity.class);
				i.putExtra("doings_id", intent.getStringExtra("id"));//三级标签的id
				i.putExtra("task_id", mCurTask.id);//待上传任务的id
				i.putExtra("task_tag_title", mCurTask.tag);//待上传任务的tag title
				i.putExtra("task_tag_id", mCurTask.tagId);//待上传任务的tag id
				i.putExtra("task_sub_tag_id", mCurTask.subTagId);//待上传任务的tag subId
				startActivityForResult(i, CHOOSE_PIC_TAG);
				break;
			case SET_TASK_PIC_SRC:
				showTagEditDialog();
				isEditPic = true;
				break;

			default:
				break;
			}
		}

	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.uploaded_pic_layout:
			Bundle bundle = new Bundle();
			bundle.putString("title", intent.getStringExtra("title"));
			bundle.putString("id", intent.getStringExtra("id"));
			bundle.putString("thumb", intent.getStringExtra("thumb"));
			bundle.putSerializable("task_list", (Serializable) mJoinedDoings.taskList);
			IntentUtil.startActivity(PerformTaskActivity.this, CompletedTaskActivity.class, bundle);
			break;
		case R.id.undo_task_pic_layout:
			Bundle bundle2 = new Bundle();
			bundle2.putString("title", intent.getStringExtra("title"));
			bundle2.putString("id", intent.getStringExtra("id"));
			bundle2.putString("thumb", intent.getStringExtra("thumb"));
			bundle2.putInt("uploadedSize", mJoinedDoings.taskList.size());
			IntentUtil.startActivity(PerformTaskActivity.this, UndoTaskActivity.class, bundle2);
			break;
		case R.id.remainder_task_pic_layout:
			mViewPager.setCurrentItem(mPageAdapter.getCount());
			
			break;
		case R.id.right_img:
			IntentUtil.startActivity(
					PerformTaskActivity.this,TaskDetailsActivity.class,
					new BasicNameValuePair("id", intent.getStringExtra("id")),
					new BasicNameValuePair("title", getResources().getString(R.string.task_details_title)),
					new BasicNameValuePair("thumb", intent.getStringExtra("thumb")), 
					new BasicNameValuePair("isJoined", intent.getStringExtra("isJoined")), 
					new BasicNameValuePair("score", intent.getStringExtra("score")), 
					new BasicNameValuePair("uploadPicCount", String.valueOf(mJoinedDoings.taskList.size())), 
					new BasicNameValuePair("only_show", "1"), 
					new BasicNameValuePair("url", intent.getStringExtra("url")));
			break;
		case R.id.next_button:
			//开始上传
			String result = Utils.checkTagIsValid(PerformTaskActivity.this, mCurTask.subTagId, mCurTask.subTagCount);
			if (!TextUtils.isEmpty(result)) {
				AppToast.showShortText(PerformTaskActivity.this, result);
				return;
			}
			
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					try {
						uploadFile(PerformTaskActivity.this, mCurTask);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			break;

		default:
			break;
		}
	}
	
	private boolean isAllowedToUploadPic(){
		if (remainderPicNum > waitToUploadPicNum) {
			return true;
		}else {
			if (waitToUploadPicNum > 0) {
				AppToast.showShortText(this, "先把本地待上传的图片上传了吧");
			}
			return false;
		}
	}
	
	Comparator<Task> comparator = new Comparator<Task>() {

		@Override
		public int compare(Task task1, Task task2) {
			if (task1.state > task2.state)
				return 1;
			else if (task1.state == task2.state)
				return 0;
			else if (task1.state > task2.state)
				return -1;
			return 0;
		}
	};
	
	private void getJoinedDoingsInfo(String id) {
		LogUtils.d(TAG, "getJoinedDoingsInfo");
		String url = Constant.USER_JOINED_TASK_INFO_URL;
		FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
		AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("task_id", id);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				creatDialog(null, "加载数据", "正在努力任务图片列表，请稍候...");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				closeDialog();
				AppToast.showShortText(PerformTaskActivity.this, "获取任务详情失败");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] data) {
				String json = new String(data);
				Log.d(TAG, "onSuccess " + json);
				if (1 == JsonUtil.getStateFromServer(json)) {
					closeDialog();
					mJoinedDoings = JsonUtil.getJoinedDoingsByJson(json);
					mHandler.sendEmptyMessage(UPDATE_JOINED_TASK_LIST_DATA);
				} else {
					AppToast.showShortText(PerformTaskActivity.this, "获取任务详情失败");
				}
			}

		});
	}

	public void uploadFile(final Context context, final Task task) throws Exception {
		String url = Constant.USER_UPLOAD_PIC_URL;
		Bitmap bitmap = BitmapFactory.decodeFile(task.picPath);
		if (null != bitmap) {
            
            FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
			AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
			
			RequestParams params = finalAsyncHttpClient.getCommonRequestParams(this);
			params.put("photo_source", task.picSrc);
			params.put("latitude", "145.54");
			params.put("longitude", "165.54");
			params.put("photo_tag_id", task.tagId);
			params.put("task_id", task.participantDoingsId);
			params.put("photo", new File(task.picPath));
			client.post(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onStart() {
					super.onStart();
					createUploadDialog();
				}
				
				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					super.onProgress(bytesWritten, totalSize);
					int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
		            // 上传进度显示  
		            pd.setProgress(count);  
		            /*Log.d(TAG, "上传 进度 = " + count);
		            Log.e(TAG, "上传 Progress>>>>>" +  bytesWritten + " / " + totalSize);*/
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					String json = new String(responseBody);
					 Log.d(TAG, "onSuccess " + json);
					 pd.dismiss();
					if (1 == JsonUtil.getStateFromServer(json)) {
						FileUtil.deleteFile(task.picPath);
						mTaskService.deleteTask(task.picPath);
						mHandler.sendEmptyMessage(TASK_PIC_UPLOAD_SUCCESS);
					} else {
						AppToast.showShortText(context, "上传图片出错了！");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					pd.dismiss();
					Log.d(TAG, "onFailure statusCode = " + statusCode);
					AppToast.showShortText(context, "上传图片出错了！");
					if (responseBody != null) {
						String json = new String(responseBody);
						 Log.d(TAG, "onFailure " + json);
					}
					
				}
			});
		} else {
			AppToast.showShortText(context, "图片文件不存在");
		}

	}
	
	private static ProgressDialog pd;
	private void createUploadDialog(){
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在上传图片，请稍候...");
		pd.setCancelable(false);
		pd.show();
		pd.setProgress(0);
	}
	
	private void showConfirmDialog(final int position){
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("请确认")
		.setMessage("确定要删除吗?")
		.setPositiveButton(android.R.string.yes, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteTask(position);
						
			}
		})
		.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		// 显示对话框
		dialog.show();
	}
	
	private void showTagEditDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.input_task_pic_source_tip);
	    final EditText editText = new EditText(this);
	    builder.setView(editText);
	    builder.setPositiveButton(android.R.string.ok,
	            new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	Log.d(TAG, "showTagEditDialog mCurTask.id = " + mCurTask.id);
	                	String text = editText.getText().toString();
	    				if (!TextUtils.isEmpty(text)) {
	    					mCurTask.picSrc = text;
	    					runOnUiThread(new Runnable() {
								public void run() {
									boolean isSuccess = mTaskService.updateUndoTask(mCurTask.id, mCurTask);
			    					if (isSuccess) {
			    						//从数据库查询到的task信息
			    						Task task = mTaskService.getUndoTask(mCurTask.id);
			    						mListItems.remove(mCurTask);
			    						mCurTask = task;
			    						mListItems.add(mCurIndex, mCurTask);
			    						Log.d(TAG, "showTagEditDialog 222 mCurTask.id = " + mCurTask.id);
			    						Log.d(TAG, "showTagEditDialog 222 mCurIndex = " + mCurIndex);
			    						mPageAdapter.updateItemView(mCurIndex, mCurTask);
			    					}
									
								}
							});
	    					
	    					
	    				}
	                }
	            });
	    builder.setNegativeButton(android.R.string.cancel,
	            new DialogInterface.OnClickListener() {
	 
	                @Override
	                public void onClick(DialogInterface dialog, int which) {

	                }
	            });
	 
	    builder.show();		
	};
}
