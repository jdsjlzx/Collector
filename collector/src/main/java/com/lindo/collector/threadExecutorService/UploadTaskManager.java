package com.lindo.collector.threadExecutorService;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import android.util.Log;

public class UploadTaskManager {

	//请求队列
	private LinkedList<UploadTask> uploadTasks;
	// 线程名称集合，任务不能重复
	private Set<String> taskIdSet;
	private static UploadTaskManager uploadTaskMananger;
	public UploadTaskManager() {
		uploadTasks = new LinkedList<UploadTask>();
		taskIdSet = new HashSet<String>(); 
	}
	public static synchronized UploadTaskManager getInstance(){
		if (null == uploadTaskMananger) {
			uploadTaskMananger = new UploadTaskManager();
		}
		return uploadTaskMananger;
	}
	
	//添加任务
	public void addUploadTask(UploadTask uploadTask){
		synchronized (uploadTasks) {
			Log.d("lzx", "addUploadTask");
			if (!isTaskRepeat(uploadTask.getName())) {
				//增加新任务
				uploadTasks.addLast(uploadTask);
			}
		}
	}
	
	public boolean isTaskRepeat(String taskId){
		synchronized (taskId) {
			if (taskIdSet.contains(taskId)) {
				return true;
			} else {
				taskIdSet.add(taskId);;
				return false;
			}
		}
	}
	
	//取出任务
	public UploadTask getUploadTask(){
		synchronized (uploadTasks) {
			if (uploadTasks.size() > 0) {
				UploadTask uploadTask = uploadTasks.removeFirst();
				return uploadTask;
			}
		}
		return null;
	}
	
}
