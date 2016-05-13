package com.lindo.collector.activity;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lindo.collector.R;
import com.lindo.collector.activity.adapter.TaskGridAdapter;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.domain.Task;
import com.lindo.collector.widget.CustGridView;
import com.lzx.work.utils.IntentUtil;

public class CompletedTaskActivity extends BaseActivity{
	
	private TextView mCountText;
	private TextView mAuditCountText;
	private TextView mApprovedCountText;
	private TextView mNotApprovedCountText;
	private CustGridView mAuditGridView;
	private CustGridView mApprovedGridView;
	private CustGridView mNotApprovedGridView;
	
	private Bundle bundle;
	private List<Task> mListItems;
	private LinkedList<Task> mAuditList;
	private LinkedList<Task> mApprovedList;
	private LinkedList<Task> mNotApprovedList;
	private TaskGridAdapter mAuditGridAdapter;
	private TaskGridAdapter mApprovedGridAdapter;
	private TaskGridAdapter mNotApprovedGridAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_completed_task);
		
		initView();
		initData();
	}


	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mCountText = (TextView)findViewById(R.id.count_text);
		mAuditCountText = (TextView)findViewById(R.id.audit_count_text);
		mApprovedCountText = (TextView)findViewById(R.id.be_approved_count_text);
		mNotApprovedCountText = (TextView)findViewById(R.id.not_be_approved_count_text);
		mAuditGridView = (CustGridView)findViewById(R.id.audit_gridview);
		mApprovedGridView = (CustGridView)findViewById(R.id.approved_gridview);
		mNotApprovedGridView = (CustGridView)findViewById(R.id.not_approved_gridview);
		
	}

	private void initData() {
		bundle = getIntent().getExtras();
		mTitleText.setText(bundle.getString("title"));		
		mListItems = (List<Task>) bundle.getSerializable("task_list");
		mAuditList = new LinkedList<Task>();
		mApprovedList = new LinkedList<Task>();
		mNotApprovedList = new LinkedList<Task>();
		initList(mListItems);
		
		mCountText.setText(String.valueOf(mListItems.size()));
		mAuditCountText.setText(getCountText(mAuditList.size()));
		mApprovedCountText.setText(getCountText(mApprovedList.size()));
		mNotApprovedCountText.setText(getCountText(mNotApprovedList.size()));
		
		
		mAuditGridAdapter = new TaskGridAdapter(this, mAuditList);
		mApprovedGridAdapter = new TaskGridAdapter(this, mApprovedList);
		mNotApprovedGridAdapter = new TaskGridAdapter(this, mNotApprovedList);
		
		mAuditGridView.setAdapter(mAuditGridAdapter);
		mApprovedGridView.setAdapter(mApprovedGridAdapter);
		mNotApprovedGridView.setAdapter(mNotApprovedGridAdapter);
		
		mAuditGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Task task = mAuditList.get(position);
				Bundle b = new Bundle();
				b.putInt("position", task.index);
				b.putString("id", bundle.getString("id"));
				b.putString("title", bundle.getString("title"));
				IntentUtil.startActivity(CompletedTaskActivity.this, PerformTaskActivity.class, b);
				CompletedTaskActivity.this.finish();
			}
		});
		mApprovedGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Task task = mApprovedList.get(position);
				Bundle b = new Bundle();
				b.putInt("position", task.index);
				b.putString("id", bundle.getString("id"));
				b.putString("title", bundle.getString("title"));
				b.putString("thumb", bundle.getString("thumb"));
				IntentUtil.startActivity(CompletedTaskActivity.this, PerformTaskActivity.class, b);
			}
		});
		mNotApprovedGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Task task = mNotApprovedList.get(position);
				Bundle b = new Bundle();
				b.putInt("position", task.index);
				b.putString("id", bundle.getString("id"));
				b.putString("title", bundle.getString("title"));
				IntentUtil.startActivity(CompletedTaskActivity.this, PerformTaskActivity.class, b);
			}
		});
		
		
	}

	private void initList(List<Task> list) {
		for (Task task : list) {
			switch (task.pic_status) {
			case 1:
				mAuditList.add(task);
				break;
			case 2:
				mApprovedList.add(task);
				break;
			case 3:
				mNotApprovedList.add(task);
				break;

			default:
				break;
			}
		}
	}
	
	private String getCountText(int count){
		String countText = String.format(getResources().getString(R.string.digital), count); 
		return countText;
	}
}
