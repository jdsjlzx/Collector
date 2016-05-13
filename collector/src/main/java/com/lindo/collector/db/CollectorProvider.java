package com.lindo.collector.db;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class CollectorProvider extends ContentProvider {

	// The incoming URI matches the Tasks URI pattern
	private static final int TASKS = 1;

	// The incoming URI matches the Task ID URI pattern
	private static final int TASK_ID = 2;

	/**
	 * A projection map used to select columns from the database
	 */
	private static HashMap<String, String> sTasksProjectionMap;

	/**
	 * A UriMatcher instance
	 */
	private static final UriMatcher sUriMatcher;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Collector.AUTHORITY, "tasks", TASKS);
		sUriMatcher.addURI(Collector.AUTHORITY, "tasks/#", TASK_ID);

		sTasksProjectionMap = new HashMap<String, String>();
		sTasksProjectionMap.put(Collector.Tasks._ID, Collector.Tasks._ID);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_NAME,
				Collector.Tasks.COLUMN_NAME_NAME);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_PATH,
				Collector.Tasks.COLUMN_NAME_PIC_PATH);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_SOURCE,
				Collector.Tasks.COLUMN_NAME_PIC_SOURCE);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_TAG,
				Collector.Tasks.COLUMN_NAME_PIC_TAG);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID,
				Collector.Tasks.COLUMN_NAME_PIC_TAG_ID);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID,
				Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT,
				Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID,
				Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_DOINGS_ID,
				Collector.Tasks.COLUMN_NAME_DOINGS_ID);
		sTasksProjectionMap.put(Collector.Tasks.COLUMN_NAME_STATE,
				Collector.Tasks.COLUMN_NAME_STATE);
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String finalSelection;
		switch (sUriMatcher.match(uri))
		{
			case TASKS:
				count = db.delete(Collector.Tasks.TABLE_NAME, selection, selectionArgs);
				break;

			case TASK_ID:
				String taskId = uri.getPathSegments().get(Collector.Tasks.TASK_ID_PATH_POSITION);
				finalSelection = Collector.Tasks._ID +  " = " + taskId;
				// If there were additional selection criteria, append them to the final WHERE
	            // clause
	            if (selection !=null) {
	            	finalSelection = finalSelection + " AND " + selection;
	            }
				count = db.delete(Collector.Tasks.TABLE_NAME, finalSelection, selectionArgs);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		/**
		 * Chooses the MIME type based on the incoming URI pattern
		 */
		switch (sUriMatcher.match(uri)) {

		// If the pattern is for TASKs or live folders, returns the general
		// content type.
		case TASKS:
			return Collector.Tasks.CONTENT_TYPE;

			// If the pattern is for TASK IDs, returns the TASK ID content type.
		case TASK_ID:
			return Collector.Tasks.CONTENT_ITEM_TYPE;

			// If the URI pattern doesn't match any permitted patterns, throws
			// an exception.
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != TASKS){
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null)
		{
			values = new ContentValues(initialValues);
		}
		else
		{
			values = new ContentValues();
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_NAME) == false) {
            values.put(Collector.Tasks.COLUMN_NAME_NAME, "");
        }
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_DOINGS_ID) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_DOINGS_ID, "");
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_PATH) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_PATH, "");
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_SOURCE) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_SOURCE, "");
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_TAG) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_TAG, "");
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_TAG_ID, "");
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_ID, "");
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_COUNT, 0);
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_PIC_SUB_TAG_VALID, 0);
		}
		if (values.containsKey(Collector.Tasks.COLUMN_NAME_STATE) == false) {
			values.put(Collector.Tasks.COLUMN_NAME_STATE, -1);
		}
		// Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(Collector.Tasks.TABLE_NAME,null,values );
        
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(Collector.Tasks.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Collector.Tasks.TABLE_NAME);
		/**
		 * Choose the projection and adjust the "where" clause based on URI
		 * pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) {
		case TASKS:
			qb.setProjectionMap(sTasksProjectionMap);
			break;
		case TASK_ID:
			qb.setProjectionMap(sTasksProjectionMap);
			qb.appendWhere(Collector.Tasks._ID
					+ "="
					+
					// the position of the TASK ID itself in the incoming URI
					uri.getPathSegments().get(
							Collector.Tasks.TASK_ID_PATH_POSITION));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
		// If no sort order is specified, uses the default
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Collector.Tasks.DEFAULT_SORT_ORDER;
		} else {
			// otherwise, uses the incoming sort order
			orderBy = sortOrder;
		}
		// Opens the database object in "read" mode, since no writes need to be
		// done.
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String finalSelection;
		switch (sUriMatcher.match(uri)) {
		case TASKS:
			count = db.update(Collector.Tasks.TABLE_NAME, values, selection, selectionArgs);
			break;
		case TASK_ID:
			String taskId = uri.getPathSegments().get(Collector.Tasks.TASK_ID_PATH_POSITION);
			finalSelection = Collector.Tasks._ID +  " = " + taskId;
			// If there were additional selection criteria, append them to the final WHERE
            // clause
            if (selection !=null) {
            	finalSelection = finalSelection + " AND " + selection;
            }
            
			count = db.update(Collector.Tasks.TABLE_NAME, values, finalSelection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
