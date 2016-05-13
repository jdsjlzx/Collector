package com.lindo.collector.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.lindo.collector.dao.TaskService;
import com.lindo.collector.domain.Task;
import com.lindo.collector.threadExecutorService.UploadTask;
import com.lindo.collector.threadExecutorService.UploadTaskManager;
import com.lindo.collector.threadExecutorService.UploadTaskThreadPool;
import com.lindo.collector.utils.LogUtils;
import com.lindo.collector.utils.Utils;

public class CollectorService extends Service {
	
	private static final String TAG = "CollectorService";
	
	private TaskService mTaskService;
	private UploadTaskManager uploadTaskManager;//new一个线程管理队列 
	private UploadTaskThreadPool taskThreadPool;//new一个线程池 
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mTaskService = new TaskService(this);
		uploadTaskManager = UploadTaskManager.getInstance();
		taskThreadPool = new UploadTaskThreadPool();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand  addUploadTask");
		new Thread(taskThreadPool).start();
		List<Task> undoTasks = mTaskService.getEffectiveTagUndoTasks();
		for (Task task : undoTasks) {
			
			String result = Utils.checkTagIsValid(this, task.subTagId, task.subTagCount);
			if (TextUtils.isEmpty(result)) {//标签有效
				uploadTaskManager.addUploadTask(new UploadTask(this, task));
			} else {
				LogUtils.d(TAG, "标签不完整 id = " +task.id);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		taskThreadPool.setStop(true);
	}


}
