package com.lindo.collector.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase db;
	
	public DBManager(Context context) {
		mDatabaseHelper = new DatabaseHelper(context);
		db = mDatabaseHelper.getWritableDatabase();
	}
	
	public void addUndoTask(){
		db.beginTransaction();
	}
}
