package com.lindo.collector.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lindo.collector.R;
import com.lindo.collector.activity.UserCenterActivity;
import com.lzx.work.utils.IntentUtil;

public class TitleBarFragment extends Fragment {

	private ImageView mRightImage;
	//http://blog.csdn.net/lmj623565791/article/details/37970961
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_title_bar, container, false);
		mRightImage = (ImageView) view.findViewById(R.id.right_img);
		mRightImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentUtil.startActivity(getActivity(), UserCenterActivity.class);
			}
		});
		return view;
	}

}
