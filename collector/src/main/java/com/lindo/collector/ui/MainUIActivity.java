package com.lindo.collector.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.lindo.collector.PushApplication;
import com.lindo.collector.R;
import com.lindo.collector.dao.TaskService;
import com.lindo.collector.fragment.HomeFragment;
import com.lindo.collector.service.CollectorService;
import com.lindo.collector.utils.NetHelper;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainUIActivity extends FragmentActivity {
	private static final String TAG = "MainUIActivity";
	private HomeFragment mHomeFragment;
	FragmentManager mFragmentManager;
	FragmentTransaction mTransaction;
	 
	private TaskService mTaskService;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);  
		
		mFragmentManager = getSupportFragmentManager();
		mTransaction = mFragmentManager.beginTransaction();
		
		//设置默认Fragment  
        setDefaultFragment();  
        
        //检查更新
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        UmengUpdateAgent.update(this);
        //同步反馈新回复通知
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
        
        PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, "48YiX3RgcCYmv9aaTcr3uTVK");
        
        mTaskService = new TaskService(MainUIActivity.this);
        long count = mTaskService.getEffectiveTagTaskCount();
		Log.d(TAG, "getEffectiveTagTaskCount count = " + count);
		if (PushApplication.getInstance().isLogin()) {
			if (count > 0) {
				if (NetHelper.isWifi(this)) {
					Log.d(TAG, "wifi is enable");
					showConfirmDialog();
				}
			}
		} 
		
	}

	private void showConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("是否批量上传未完成的任务?")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startService(new Intent(MainUIActivity.this, CollectorService.class));
					}
				}).setNegativeButton("否", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		AlertDialog ad = builder.create();
		ad.setCanceledOnTouchOutside(false);                                   //点击外面区域不会让dialog消失
		ad.show();
	}
	
	private void setDefaultFragment() {
		mHomeFragment = new HomeFragment();
		if (!mHomeFragment.isAdded()) {
			mTransaction.add(R.id.content_frame, mHomeFragment);
		}
		mTransaction.show(mHomeFragment);
		mTransaction.commit();
	}
	
	/**
	 * 多于2个fragment使用此方法
	 * @param transaction
	 */
	/*private void hideFragments(FragmentTransaction transaction) {
		if (null != mHomeFragment) {
			transaction.hide(mHomeFragment);
		}
	}*/
	
	private static Boolean isExit = false;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						isExit = false;
					}
				}, 2000);
			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}
}
