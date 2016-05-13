package com.lindo.collector.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.lindo.collector.db.Collector;
import com.lindo.collector.db.DatabaseHelper;
import com.lindo.collector.domain.Task;

public class TaskService {
	private static final String TAG = "TaskService";
	private Context mContext;
	private DatabaseHelper mDatabaseHelper;
	public TaskService(Context context) {
		this.mContext = context;
		this.mDatabaseHelper = new DatabaseHelper(context);
	}
	
	/**
	 * 获取所有待上传的任务列表
	 * @return
	 */
	public List<Task> getUndoTasks(){
		List<Task> list = new ArrayList<Task>();
		Cursor cursor = mContext.getContentResolver().query(
				Collector.Tasks.CONTENT_URI, null, null,null, Collector.Tasks._ID);
		while (cursor.moveToNext()) {
			Task task = new Task();
			task.id = cursor.getInt(cursor.getColumnIndex(Collector.Tasks._ID));
			task.picPath = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_PATH));
			task.picSrc = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SOURCE));
			task.tag = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG));
			task.tagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID));
			task.subTagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID));
			task.subTagCount = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT));
			task.participantDoingsId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_DOINGS_ID));
			task.state = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_STATE));
			list.add(task);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获取所有待上传的标签有效的任务列表
	 * @return
	 */
	public List<Task> getEffectiveTagUndoTasks(){
		List<Task> list = new ArrayList<Task>();
		Cursor cursor = mContext.getContentResolver().query(
				Collector.Tasks.CONTENT_URI, null, Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID + " = ?",
				new String[] { "1" }, Collector.Tasks._ID);
		while (cursor.moveToNext()) {
			Task task = new Task();
			task.id = cursor.getInt(cursor.getColumnIndex(Collector.Tasks._ID));
			task.picPath = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_PATH));
			task.picSrc = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SOURCE));
			task.tag = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG));
			task.tagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID));
			task.subTagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID));
			task.subTagCount = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT));
			task.subTagIsValid = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID));
			task.participantDoingsId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_DOINGS_ID));
			task.state = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_STATE));
			list.add(task);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获取某个活动待上传的任务列表
	 * @return
	 */
	public List<Task> getUndoTasks(String id){
		Log.d(TAG, "getUndoTasks OINGS_ID = " + id);
		List<Task> list = new ArrayList<Task>();
		Cursor cursor = mContext.getContentResolver().query(
				Collector.Tasks.CONTENT_URI, null,
				Collector.Tasks.COLUMN_NAME_DOINGS_ID + " = ?",
				new String[] { id }, Collector.Tasks._ID);
		while (cursor.moveToNext()) {
			Task task = new Task();
			task.id = cursor.getInt(cursor.getColumnIndex(Collector.Tasks._ID));
			Log.d(TAG, "getUndoTasks task.id = " + task.id);
			task.picPath = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_PATH));
			task.picSrc = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SOURCE));
			task.tag = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG));
			task.tagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID));
			task.subTagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID));
			task.subTagCount = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT));
			task.subTagIsValid = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID));
			task.participantDoingsId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_DOINGS_ID));
			task.state = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_STATE));
			list.add(task);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获取某个活动待上传的任务列表
	 * @return
	 */
	public List<Task> getUndoTasks(int size, String id){
		Log.d(TAG, "getUndoTasks OINGS_ID = " + id);
		List<Task> list = new ArrayList<Task>();
		Cursor cursor = mContext.getContentResolver().query(
				Collector.Tasks.CONTENT_URI, null,
				Collector.Tasks.COLUMN_NAME_DOINGS_ID + " = ?",
				new String[] { id }, Collector.Tasks._ID);
		int i = 0;
		while (cursor.moveToNext()) {
			Task task = new Task();
			task.id = cursor.getInt(cursor.getColumnIndex(Collector.Tasks._ID));
			Log.d(TAG, "getUndoTasks task.id = " + task.id);
			task.picPath = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_PATH));
			task.picSrc = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SOURCE));
			task.tag = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG));
			task.tagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID));
			task.subTagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID));
			task.subTagCount = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT));
			task.subTagIsValid = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID));
			task.participantDoingsId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_DOINGS_ID));
			task.state = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_STATE));
			if (size > 0) {
				task.index = size + i;
			}else {
				task.index = i;
			}
			i++;
			list.add(task);
			
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获取某个活动待上传且标签有效的任务列表
	 * @return
	 */
	public List<Task> getEffectiveTagUndoTasks(String id){
		Log.d(TAG, "getUndoTasks OINGS_ID = " + id);
		List<Task> list = new ArrayList<Task>();
		Cursor cursor = mContext.getContentResolver().query(
				Collector.Tasks.CONTENT_URI, null,
				Collector.Tasks.COLUMN_NAME_DOINGS_ID + " = ? and " + Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID +" = ?",
				new String[] { id,"1" }, Collector.Tasks._ID);
		while (cursor.moveToNext()) {
			Task task = new Task();
			task.id = cursor.getInt(cursor.getColumnIndex(Collector.Tasks._ID));
			Log.d(TAG, "getUndoTasks task.id = " + task.id);
			task.picPath = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_PATH));
			task.picSrc = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SOURCE));
			task.tag = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG));
			task.tagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID));
			task.subTagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID));
			task.subTagCount = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT));
			task.subTagIsValid = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID));
			task.participantDoingsId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_DOINGS_ID));
			task.state = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_STATE));
			list.add(task);
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获取某个待上传的活动
	 * @return
	 */
	public Task getUndoTask(int id){
		Task task = new Task();
		Uri uri = ContentUris.withAppendedId(Collector.Tasks.CONTENT_URI, id);
		Log.d(TAG, "getUndoTask uri = " + uri.toString());
		Cursor cursor = mContext.getContentResolver().query(uri, null,null,null,Collector.Tasks._ID);
		if (cursor.moveToFirst()) {
			task.id = id;
			task.picPath = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_PATH));
			task.picSrc = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SOURCE));
			task.tag = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG));
			task.tagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID));
			task.subTagId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID));
			task.subTagCount = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT));
			task.subTagIsValid = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID));
			task.participantDoingsId = cursor.getString(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_DOINGS_ID));
			task.state = cursor.getInt(cursor.getColumnIndex(Collector.Tasks.COLUMN_NAME_STATE));
		}
		cursor.close();
		return task;
	}
	
	/**
	 * 更新某个待上传的活动
	 * @return
	 */
	public boolean updateUndoTask(int id,Task task){
		Uri uri = ContentUris.withAppendedId(Collector.Tasks.CONTENT_URI, id);
		Log.d(TAG, "updateUndoTask uri = " + uri.toString());
		ContentValues values = new ContentValues();
		values.put(Collector.Tasks.COLUMN_NAME_PIC_PATH, task.picPath);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SOURCE, task.picSrc);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_TAG, task.tag);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID, task.tagId);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID, task.subTagId);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT, task.subTagCount);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID, task.subTagIsValid);
		values.put(Collector.Tasks.COLUMN_NAME_DOINGS_ID, task.participantDoingsId);
		values.put(Collector.Tasks.COLUMN_NAME_STATE, task.state);
		int count = mContext.getContentResolver().update(uri, values, null, null);
		return count > 0;
	}
	
	/**
	 * 保存并返回完整的task信息
	 * @param task
	 * @return
	 */
	public Task saveUndoTask(Task task){
		//如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		ContentValues values = new ContentValues();
		values.put(Collector.Tasks.COLUMN_NAME_PIC_PATH, task.picPath);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SOURCE, task.picSrc);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_TAG, task.tag);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID, task.tagId);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID, task.subTagId);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT, task.subTagCount);
		values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID, task.subTagIsValid);
		values.put(Collector.Tasks.COLUMN_NAME_DOINGS_ID, task.participantDoingsId);
		values.put(Collector.Tasks.COLUMN_NAME_STATE, task.state);
		Uri uri = mContext.getContentResolver().insert(Collector.Tasks.CONTENT_URI, values);
		Task newTask = task;
		newTask.id = Integer.valueOf(uri.getPathSegments().get(Collector.Tasks.TASK_ID_PATH_POSITION));
		Log.d(TAG, "saveUndoTask id = " + newTask.id);
		Log.d(TAG, uri.toString());
		return newTask;
	}
	
	public boolean deleteTask(String picPath){
		Uri uri = Collector.Tasks.CONTENT_URI;
		int count = mContext.getContentResolver().delete(uri, Collector.Tasks.COLUMN_NAME_PIC_PATH + " = ?", new String[] { picPath });
		return count > 0;  
	}
	
	/*
     * get the count of all Task
     */
	public long getCount(){
		long count = 0;
		if (null != mContext) {
			File dirFile = mContext.getFilesDir();
			if (dirFile.exists()) {
				String appRootDir = dirFile.getAbsolutePath();
				String dbPath = appRootDir.replace("files", "databases") + File.separator + "collector.db";
				File file = new File(dbPath);
				if (file.exists()) {
					SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);
					Cursor cursor = db.rawQuery("select count(*) from " + Collector.Tasks.TABLE_NAME, null);
					
					if (cursor.moveToNext()) {  
						count =  cursor.getLong(0);  
			        }  
					cursor.close();
				}
			}
			
		}
		
        return count;  
	}

	/*
	 * get the count of effectiveTag Task
	 */
	public long getEffectiveTagTaskCount() {
		long count = 0;
		if (null != mContext) {
			File dirFile = mContext.getFilesDir();
			if (dirFile.exists()) {
				String appRootDir = dirFile.getAbsolutePath();
				String dbPath = appRootDir.replace("files", "databases") + File.separator + "collector.db";
				File file = new File(dbPath);
				if (file.exists()) {
					SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase( file, null);
					Cursor cursor = db.rawQuery("select count(*) from "
							+ Collector.Tasks.TABLE_NAME + " where "
							+ Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID
							+ " = ?", new String[] { "1" });

					if (cursor.moveToNext()) {
						count = cursor.getLong(0);
					}
					cursor.close();
				}
			}

		}

		return count;
	}
	
	/*
	 * get the count of one Task
	 */
	public long getCount(String id){
		long count = 0;
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "
				+ Collector.Tasks.TABLE_NAME + " where "
				+ Collector.Tasks.COLUMN_NAME_DOINGS_ID + " = ?", new String[] { id });
		if (cursor.moveToNext()) {  
			count =  cursor.getLong(0);  
		}  
		cursor.close();
		return count;  
	}
	
	/*
	 * get the count of one Task
	 */
	public long getEffectiveTagTaskCount(String id){
		long count = 0;
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "
				+ Collector.Tasks.TABLE_NAME + " where "
				+ Collector.Tasks.COLUMN_NAME_DOINGS_ID +  " = ? and " + Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID +" = ?",
				new String[] { id,"1" });
		if (cursor.moveToNext()) {  
			count =  cursor.getLong(0);  
		}  
		cursor.close();
		return count;  
	}

	
}
