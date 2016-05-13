package com.lindo.collector.activity.adapter;

import java.util.List;

import com.lindo.collector.R;
import com.lindo.collector.domain.Task;
import com.lzx.work.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TaskGridAdapter extends BaseAdapter {

	private Context mContext;
	List<Task> dataList;
	
	public TaskGridAdapter(Context context, List<Task> list) {
		this.mContext = context;
		this.dataList = list;
	}
	
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.layout_task_undo_grid_item, null);
			holder.picImage = (ImageView) convertView.findViewById(R.id.picImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Task task = dataList.get(position);
		if (task.state == 1) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(false) // default
			.delayBeforeLoading(1000)
			.cacheInMemory(true) // default 不缓存至内存
			.cacheOnDisk(true) // default 不缓存至手机SDCard
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// default
			.bitmapConfig(Bitmap.Config.ARGB_8888) // default
			.build();
			ImageLoader.getInstance().displayImage(task.picPath,
					holder.picImage, options);
		} else {
			Bitmap smallBtimap = Utils.getSmallBtimap(task.picPath);
			holder.picImage.setImageBitmap(smallBtimap);
		}
		
		return convertView;
	}

	class ViewHolder {
		private ImageView picImage;
	}
}
