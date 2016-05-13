package com.lindo.collector.activity.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lindo.collector.R;
import com.lindo.collector.activity.PerformTaskActivity;
import com.lindo.collector.dao.TaskService;
import com.lindo.collector.domain.Task;
import com.lzx.work.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class TaskPageAdapter extends PagerAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private LinkedList<Task> mDataList;
	private View mCurrentView;
	private int size;// 页数
	private Handler mHandler;
	
	public TaskPageAdapter(Context context, LinkedList<Task> list, Handler handler) {
		this.mContext = context;
		this.mDataList = list;
		this.size = mDataList.size();
		this.mHandler = handler;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public void setListDatas(LinkedList<Task> list) {// 自己写的一个方法用来添加数据
		this.mDataList = list;
		size = mDataList.size();
	}

	@Override
	public int getCount() {
		return size;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (View) obj;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		
		View v = mInflater
				.inflate(R.layout.layout_task_pager_item, container, false);
		LinearLayout picCaptureLayout = (LinearLayout) v.findViewById(R.id.task_pic_capture_view);
		LinearLayout picContentLayout = (LinearLayout) v.findViewById(R.id.task_pic_content_view);
		LinearLayout picTagLayout = (LinearLayout) v.findViewById(R.id.task_pic_tag_layout);
		ImageView taskPicImage = (ImageView) v.findViewById(R.id.task_pic_image);
		TextView taskPicNOText = (TextView) v.findViewById(R.id.task_pic_num_text);
		TextView taskPicSrcText = (TextView) v.findViewById(R.id.task_pic_source_text);
		TextView taskPicTagText = (TextView) v.findViewById(R.id.task_pic_tag_text);
		ImageView deleteTaskPicImage = (ImageView) v.findViewById(R.id.task_pic_delete_image);
		ImageView pickCameraImage = (ImageView) picCaptureLayout.findViewById(R.id.choose_pic_camera_image);
		ImageView pickPictureImage = (ImageView) picCaptureLayout.findViewById(R.id.choose_pic_album_image);
		
		final Task task = mDataList.get(position);
		Log.d("lzx", "task.state -- " + task.state);
		switch (task.state) {
		case -1:
			picCaptureLayout.setVisibility(View.VISIBLE);
			displayImage(task.picPath, taskPicImage);
			pickCameraImage.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:  
	  
	                    mHandler.sendEmptyMessage(PerformTaskActivity.PICK_PIC_FROM_CAMERA); 
	                    break;  


					default:
						break;
					}
					return false;
				}
			});
			pickPictureImage.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Log.d("lzx", "taskPicImage onTouch ");
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:  
						Log.d("lzx", "ACTION_UP.............");  
						
						mHandler.sendEmptyMessage(PerformTaskActivity.PICK_PIC_FROM_PHOTO); 
						break;  
						
						
					default:
						break;
					}
					return false;
				}
			});
			
			break;
		case 0:
			picCaptureLayout.setVisibility(View.GONE);
			picContentLayout.setVisibility(View.VISIBLE);
			Bitmap smallBtimap = Utils.getSmallBtimap(task.picPath);
			taskPicImage.setImageBitmap(smallBtimap);
			deleteTaskPicImage.setVisibility(View.VISIBLE);
			taskPicImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Log.d("lzx", "taskPicImage 1111 ");
					
				}
			});
			deleteTaskPicImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Log.d("lzx", "55555 ");
					TaskService taskService = new TaskService(mContext);
					taskService.deleteTask(task.picPath);
					mHandler.obtainMessage(PerformTaskActivity.DELETE_TASK, position, 0).sendToTarget();
				}
			});
			
			taskPicSrcText.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					Log.d("lzx", "taskPicSrcText onTouch ");
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP: 
						String src_camera = mContext.getResources().getString(R.string.task_pic_source_camera);
						if (!task.picSrc.equals(src_camera)) {
							mHandler.sendEmptyMessage(PerformTaskActivity.SET_TASK_PIC_SRC);
						}
						 
						break;  
						
						
					default:
						break;
					}
					return false;
				}
			});
			taskPicTagText.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					Log.d("lzx", "taskPicTagText onTouch ");
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:  
						mHandler.sendEmptyMessage(PerformTaskActivity.SET_TASK_PIC_TAG); 
						break;  
						
						
					default:
						break;
					}
					return false;
				}
			});
			break;
		case 1:
			displayImage(task.picPath, taskPicImage);
			break;

		default:
			break;
		}
		
		Log.d("lzx", "picPath = " + task.picPath);
		taskPicNOText.setText((position+1)+"");
		taskPicSrcText.setText(task.picSrc);
		taskPicTagText.setText(task.tag);
		
		((ViewPager) container).addView(v, 0);

		return v;
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentView = (View)object;
		
	}
	
	public View getPrimaryItem() {
        return mCurrentView;
    }

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public float getPageWidth(int position) {
		/*if (position == 0 || position == 2) {
			return 0.95f;
		}
		return 1f;*/
		
		/*if ((position+1) == getCount()) {
			return 1f;
		} else {
			return 1f;
		}*/
		
		return 1f;
	}
	
	// 更新指定item的数据
    public void updateItemView(final int index, final Task task){
    	Log.d("lzx", "updateItemView index = " + index);
    	Log.d("lzx", "updateItemView task.state = " + task.state);
    	View v = getPrimaryItem();
    	LinearLayout picCaptureLayout = (LinearLayout) v.findViewById(R.id.task_pic_capture_view);
		ImageView taskPicImage = (ImageView) v.findViewById(R.id.task_pic_image);
		TextView taskPicSrcText = (TextView) v.findViewById(R.id.task_pic_source_text);
		TextView taskPicTagText = (TextView) v.findViewById(R.id.task_pic_tag_text);
		ImageView deleteTaskPicImage = (ImageView) v.findViewById(R.id.task_pic_delete_image);
		
		picCaptureLayout.setVisibility(View.GONE);
		Bitmap btimap = BitmapFactory.decodeFile(task.picPath);
		taskPicImage.setImageBitmap(btimap);
		taskPicSrcText.setText(task.picSrc);
		if (TextUtils.isEmpty(task.tagId)) {
			taskPicTagText.setText(R.string.task_pic_tag_hint);
		} else {
			taskPicTagText.setText(task.tag);
		}
		
		deleteTaskPicImage.setVisibility(View.VISIBLE);
		
		taskPicTagText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:  
					mHandler.obtainMessage(PerformTaskActivity.SET_TASK_PIC_TAG, task.id, -1).sendToTarget();
					
					break;  
					
					
				default:
					break;
				}
				return false;
			}
		});
		deleteTaskPicImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TaskService taskService = new TaskService(mContext);
				taskService.deleteTask(task.picPath);
				mHandler.obtainMessage(PerformTaskActivity.DELETE_TASK, index, 0).sendToTarget();
				
			}
		});
		mHandler.sendEmptyMessage(PerformTaskActivity.UPDATE_UPLOAD_BUTTON_STATE);
    }
    
	private void displayImage(String picPath, ImageView image){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(false) // default
		.delayBeforeLoading(1000)
		.cacheInMemory(true) // default 不缓存至内存
		.cacheOnDisk(true) // default 不缓存至手机SDCard
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// default
		.bitmapConfig(Bitmap.Config.ARGB_8888) // default
		.build();
		ImageLoader.getInstance().displayImage(picPath,
				image, options);
	}
}


