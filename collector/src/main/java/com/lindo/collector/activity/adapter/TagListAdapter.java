package com.lindo.collector.activity.adapter;

import java.util.List;

import com.lindo.collector.R;
import com.lindo.collector.domain.Tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TagListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Tag> mTagList;
	private LayoutInflater mInflater;
	public TagListAdapter(Context context, List<Tag> list) {
		this.mContext = context;
		this.mTagList = list;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		return mTagList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTagList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.layout_pic_tag_list_tiem, parent, false);
			viewHolder.deleteImage = (ImageView) convertView.findViewById(R.id.delete_tag_image);
			viewHolder.nameText = (TextView) convertView.findViewById(R.id.tag_name_text);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Tag tag = mTagList.get(position);
		viewHolder.nameText.setText(tag.title);
		viewHolder.deleteImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTagList.remove(position);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		ImageView deleteImage;
		TextView nameText;
	}

}
