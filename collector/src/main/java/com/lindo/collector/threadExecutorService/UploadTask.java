package com.lindo.collector.threadExecutorService;

import java.io.File;

import org.apache.http.Header;

import android.content.Context;

import com.lindo.collector.dao.TaskService;
import com.lindo.collector.domain.Task;
import com.lindo.collector.http.FinalSyncHttpClient;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.FileUtil;
import com.lindo.collector.utils.JsonUtil;
import com.lindo.collector.utils.LogUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class UploadTask implements Runnable {
	private static final String TAG = "UploadTask";
	private Context context;
	private String name;
	private Task task;
	//Task：未上传的任务
	public UploadTask(Context context, Task task) {
		this.context = context;
		this.task = task;
		this.name = task.picPath;//将Task的图片路径作为唯一名字
	}
	@Override
	public void run() {
		try {
			
			uploadFile(task);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName(){
		return name;
	}
	
	public void uploadFile(final Task task) throws Exception {
		LogUtils.d(TAG, "uploadFile ");
		String url = Constant.USER_UPLOAD_PIC_URL;
		File file = new File(task.picPath);
		if (file.exists()) {
            
            FinalSyncHttpClient finalSyncHttpClient = new FinalSyncHttpClient();
            SyncHttpClient client = finalSyncHttpClient.getSyncHttpClient();
			
			RequestParams params = new RequestParams();
			params.put("photo_source", task.picSrc);
			params.put("latitude", "145.54");
			params.put("longitude", "165.54");
			params.put("photo_tag_id", task.tagId);
			params.put("task_id", task.participantDoingsId);
			params.put("photo", file);
			client.post(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onStart() {
					super.onStart();
				}
				
				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					super.onProgress(bytesWritten, totalSize);
					/*int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
		            // 上传进度显示  
					LogUtils.d(TAG, "上传 进度 = " + count);
		            LogUtils.d(TAG, "上传 Progress>>>>>" +  bytesWritten + " / " + totalSize);*/
				}
				
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					String json = new String(responseBody);
					/*LogUtils.d(TAG, "onSuccess " + json);*/
					if (1 == JsonUtil.getStateFromServer(json)) {
						FileUtil.deleteFile(task.picPath);
						TaskService taskService = new TaskService(context);
						taskService.deleteTask(task.picPath);
						LogUtils.d(TAG, "上传成功 " + task.picPath);
					} else {
						LogUtils.d(TAG, "上传任务出错了 "+ json);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					LogUtils.d(TAG, "onFailure statusCode = " + statusCode);
					if (responseBody != null) {
						String json = new String(responseBody);
						LogUtils.d(TAG, "onFailure " + json);
					}
					
				}
			});
		} else {
			LogUtils.d(TAG, "文件不存在");
		}

	}
}
