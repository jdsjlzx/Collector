package com.lindo.collector.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.DialogInterface.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lindo.collector.R;
import com.lindo.collector.activity.adapter.TaskGridAdapter;
import com.lindo.collector.basic.BaseActivity;
import com.lindo.collector.dao.TaskService;
import com.lindo.collector.domain.Task;
import com.lindo.collector.service.CollectorService;
import com.lzx.work.utils.AppToast;
import com.lzx.work.utils.IntentUtil;

public class UndoTaskActivity extends BaseActivity implements View.OnClickListener{
	
	private TextView mCountText;
	private GridView mGridView;
	private Button mNextBtn;
	
	private TaskService mTaskService;
	private List<Task> mListItems;
	private TaskGridAdapter mGridAdapter;
	private Bundle bundle;
	private long effectTagCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_undo_task);
		
		initView();
		initData();
	}


	private void initView() {
		mTitleText = (TextView)findViewById(R.id.title_text);
		mBackBtn = (ImageView) findViewById(R.id.back_img);
		mCountText = (TextView)findViewById(R.id.count_text);
		mGridView = (GridView)findViewById(R.id.gridview);
		mNextBtn = (Button) findViewById(R.id.next_button);
		
		mNextBtn.setOnClickListener(this);
	}

	private void initData() {
		bundle = getIntent().getExtras();
		mTitleText.setText(bundle.getString("title"));		
		
		mTaskService = new TaskService(this);
		mListItems = mTaskService.getUndoTasks(bundle.getInt("uploadedSize",0),bundle.getString("id"));
		mCountText.setText(String.valueOf(mListItems.size()));
		
		if (mListItems.size() > 0) {
			mNextBtn.setVisibility(View.VISIBLE);
		}
		
		mGridAdapter = new TaskGridAdapter(this, mListItems);
		mGridView.setAdapter(mGridAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Task task = mListItems.get(position);
				Bundle b = new Bundle();
				b.putInt("position", task.index);
				b.putString("id", bundle.getString("id"));
				b.putString("title", bundle.getString("title"));
				b.putString("thumb", bundle.getString("thumb"));
				IntentUtil.startActivity(UndoTaskActivity.this, PerformTaskActivity.class, b);
				UndoTaskActivity.this.finish();
			}
		});
		effectTagCount = mTaskService.getEffectiveTagTaskCount(bundle.getString("id"));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_button:
			if (effectTagCount == 0) {//无有效标签的图片
				AppToast.showLongText(UndoTaskActivity.this, R.string.no_complete_tasks_tip);
			} else {
				showConfirmDialog();
			}
			
			break;

		default:
			break;
		}
	}
	
	private void showConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("只有信息完整的图片才能被上传，是否继续批量上传?").setTitle("上传图片提醒")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startService(new Intent(UndoTaskActivity.this, CollectorService.class));
						AppToast.showShortText(UndoTaskActivity.this, "亲，我已经在后台为您上传图片了，期间您可以做其他事情哦!");
					}
				}).setNegativeButton("否", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}

				});
		AlertDialog ad = builder.create();
		ad.setCanceledOnTouchOutside(false); //点击外面区域不会让dialog消失
		ad.show();
	}
}
