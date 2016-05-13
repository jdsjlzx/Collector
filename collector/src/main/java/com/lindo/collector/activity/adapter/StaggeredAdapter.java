package com.lindo.collector.activity.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lindo.collector.utils.ImageFetcher;
import com.lindo.collector.utils.Utils;
import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.domain.Doings;

public class StaggeredAdapter extends BaseAdapter {
    private LinkedList<Doings> mDoings;
    ImageFetcher mImageFetcher;
    private Context mContext;
    public StaggeredAdapter(Context context, ImageFetcher f) {
    	this.mContext = context;
        mDoings = new LinkedList<Doings>();
        mImageFetcher = f;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Doings doings = mDoings.get(position);
        
        LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
        if (Utils.isOdd(position)) {
        	convertView = layoutInflator.inflate(R.layout.layout_task_list_item_right, null);
		} else {
			convertView = layoutInflator.inflate(R.layout.layout_task_list_item_left, null);
		}
        holder = new ViewHolder();
        holder.taskImage = (ImageView) convertView.findViewById(R.id.task_pic);
        holder.taskNameText = (TextView) convertView.findViewById(R.id.task_name);
        holder.taskRewardText = (TextView) convertView.findViewById(R.id.task_reward_text);
        holder.participationNumText = (TextView) convertView.findViewById(R.id.task_participation_number_text);
        holder.participateInImage = (ImageView) convertView.findViewById(R.id.participate_in_image);

        holder.taskNameText.setText(doings.name);
        
        String rewardPoint = String.format(mContext.getResources().getString(R.string.doings_reward_point), doings.reward);
        holder.taskRewardText.setText(rewardPoint);
        String participationNum = String.format(mContext.getResources().getString(R.string.doings_task_participation_number), doings.participation_number);
        holder.participationNumText.setText(participationNum);
        
        holder.taskImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, doings.height));
        
        if (PushApplication.getInstance().isLogin()) {
        	if (doings.join_state == 1) {
            	holder.participateInImage.setVisibility(View.VISIBLE);
    		} else {
    			holder.participateInImage.setVisibility(View.GONE);
    		}
		} else {
			holder.participateInImage.setVisibility(View.GONE);
		}
        
        mImageFetcher.loadImage(doings.thumbUrl, holder.taskImage);

        return convertView;
    }

    class ViewHolder {
        ImageView taskImage;
        ImageView participateInImage;
        TextView taskNameText;
        TextView taskRewardText;
        TextView participationNumText;
    }

    @Override
    public int getCount() {
        return mDoings.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mDoings.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public void addItemLast(List<Doings> datas) {
    	mDoings.addAll(datas);
    }

    public void addItemTop(List<Doings> datas) {
        for (Doings info : datas) {
        	mDoings.addFirst(info);
        }
    }

	public void clearData() {
		//清空数据
		if (!mDoings.isEmpty()) {
			mDoings.clear();
		}
	}
}
