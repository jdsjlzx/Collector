package com.lindo.collector.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "collector.db";  
    private static final int DATABASE_VERSION = 6;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
					+ Collector.Tasks.TABLE_NAME + " ("  
					+ Collector.Tasks._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Collector.Tasks.COLUMN_NAME_NAME + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_PATH + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_SOURCE + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_TAG + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_TAG_ID + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_DOINGS_ID + " VARCHAR,"
					+ Collector.Tasks.COLUMN_NAME_STATE + " VARCHAR"
					 + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS "+ Collector.Tasks.TABLE_NAME);

        // Recreates the database with a new version
        onCreate(db);
	}

}
