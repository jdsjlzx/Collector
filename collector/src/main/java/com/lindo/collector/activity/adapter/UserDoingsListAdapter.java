package com.lindo.collector.activity.adapter;

import java.util.List;

import com.lindo.collector.R;
import com.lindo.collector.domain.Doings;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserDoingsListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	List<Doings> mTaskList;
	public UserDoingsListAdapter(Context context, List<Doings> list) {
		this.mContext = context;
		this.mTaskList = list;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mTaskList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTaskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.layout_user_task_list_tiem, parent,
					false);

			viewHolder.thumbnailImage = (ImageView) convertView
					.findViewById(R.id.task_thumbnail_image);
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.task_name_text);
			viewHolder.descriptionText = (TextView) convertView
					.findViewById(R.id.task_description_text);
			viewHolder.taskStateText = (TextView) convertView
					.findViewById(R.id.task_state_text);
			viewHolder.taskStateView = convertView
					.findViewById(R.id.task_state_laylout);


			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Doings doings = mTaskList.get(position);
		viewHolder.nameText.setText(doings.name);
		String rewardPoint = String.format(mContext.getResources().getString(R.string.task_reward_point), doings.reward);
		viewHolder.descriptionText.setText(rewardPoint);
		//1是进行中，2是审核中，3是已完成，4是已过期
		switch (doings.state) {
		case 1:
			viewHolder.taskStateView.setBackgroundResource(R.drawable.shape_circle_red);
			viewHolder.taskStateText.setText(R.string.task_state_ongoing);
			break;
		case 2:
			viewHolder.taskStateView.setBackgroundResource(R.drawable.shape_circle_golden);
			viewHolder.taskStateText.setText(R.string.task_state_under_review);
			break;
		case 3:
			viewHolder.taskStateView.setBackgroundResource(R.drawable.shape_circle_golden);
			viewHolder.taskStateText.setText(R.string.task_state_finished);
			break;
		case 4:
			viewHolder.taskStateView.setBackgroundResource(R.drawable.shape_circle_golden);
			viewHolder.taskStateText.setText(R.string.task_state_expired);
			break;

		default:
			break;
		}

		DisplayImageOptions options = new DisplayImageOptions.Builder()  
	    .resetViewBeforeLoading(false)  // default  
	    .delayBeforeLoading(1000)  
	    /*.cacheInMemory(true)*/           // default 不缓存至内存  
	    .cacheOnDisk(true)             // default 不缓存至手机SDCard  
	    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// default  
	    .bitmapConfig(Bitmap.Config.ARGB_8888)              // default  
	    .build();  
		
		ImageLoader.getInstance().displayImage(doings.thumbUrl, viewHolder.thumbnailImage,options);
		return convertView;
	}

	class ViewHolder {
		ImageView thumbnailImage;
		TextView nameText;
		TextView descriptionText;
		TextView taskStateText;
		View taskStateView;
	}
}
