package com.lindo.collector.threadExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class UploadTaskThreadPool implements Runnable{

	private UploadTaskManager uploadTaskManager;
	// 创建一个可重用固定线程数的线程池
	private ExecutorService threadPool;
	// 线程池大小
	private final int POOL_SIZE = 1;
	// 轮询时间
	private final int SLEEP_TIME = 1000;
	// 是否停止
	private boolean isStop = false;

	public UploadTaskThreadPool() {
		uploadTaskManager = UploadTaskManager.getInstance();
		threadPool = Executors.newFixedThreadPool(POOL_SIZE);
	}
	
	@Override
	public void run() {
		while (!isStop) {
			UploadTask uploadTask = uploadTaskManager.getUploadTask();
			if (null != uploadTask) {
				Log.d("lzx", "333333开始执行下载任务");
				threadPool.execute(uploadTask);
			} else { //如果当前未有uploadTask在任务队列中
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (isStop) {
			threadPool.shutdown();
		}
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
}
