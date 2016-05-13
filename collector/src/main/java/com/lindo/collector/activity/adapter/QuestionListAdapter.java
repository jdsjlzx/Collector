package com.lindo.collector.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lindo.collector.R;
import com.lindo.collector.domain.Question;

public class QuestionListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Question> mDataList;
	private LayoutInflater inflater;
	
	public QuestionListAdapter(Context context, List<Question> list) {
		this.mContext = context;
		this.mDataList = list;
		this.inflater=LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		return mDataList!=null ? mDataList.size():0;
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.layout_question_list_item, null);
			viewHolder=new ViewHolder();
			viewHolder.titleText=(TextView)convertView.findViewById(R.id.msg_title_text);
			viewHolder.timeText=(TextView)convertView.findViewById(R.id.msg_time_text);
			viewHolder.contentText=(TextView)convertView.findViewById(R.id.msg_content_text);

			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		
		Question question = mDataList.get(position);
		viewHolder.titleText.setText(question.title);
		viewHolder.timeText.setText(question.author);
		viewHolder.contentText.setText(question.reply);
		return convertView;
	}

	class ViewHolder{
		TextView titleText;
		TextView timeText;
		TextView contentText;
	}
}
