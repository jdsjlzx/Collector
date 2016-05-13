package com.lindo.collector.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Collector {
	public static final String AUTHORITY = "com.lindo.provider.Collector";

	public Collector() {
	}

	/**
	 * Tasks table contract
	 */
	public static final class Tasks implements BaseColumns {
		public Tasks() {
		}

		/**
		 * The table name offered by this provider
		 */
		public static final String TABLE_NAME = "tasks";

		/*
		 * URI definitions
		 */

		/**
		 * The scheme part for this provider's URI
		 */
		private static final String SCHEME = "content://";

		/**
		 * Path part for the Tasks URI
		 */
		private static final String PATH_TASKS = "/tasks";

		/**
		 * Path part for the Task ID URI
		 */
		private static final String PATH_TASK_ID = "/tasks/";
		/**
		 * 0-relative position of a note ID segment in the path part of a note
		 * ID URI
		 */
		public static final int TASK_ID_PATH_POSITION = 1;
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
				+ PATH_TASKS);
		/**
		 * The content URI base for a single note. Callers must append a numeric
		 * note id to this Uri to retrieve a note
		 */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
				+ AUTHORITY + PATH_TASK_ID);

		/**
		 * The content URI match pattern for a single note, specified by its ID.
		 * Use this to match incoming URIs or to construct an Intent.
		 */
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
				+ AUTHORITY + PATH_TASK_ID + "/#");

		// 新的MIME类型-多个
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lindo.task";

		// 新的MIME类型-单个
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.lindo.task";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";
		/**
		 * Column name
		 */
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_DOINGS_ID = "doings_id";// 活动ID
		public static final String COLUMN_NAME_PIC_PATH = "pic_path";
		public static final String COLUMN_NAME_PIC_SOURCE = "pic_source";
		public static final String COLUMN_NAME_PIC_TAG = "pic_tag";
		public static final String COLUMN_NAME_PIC_TAG_ID = "pic_tag_id";
		public static final String COLUMN_NAME_PIC_SUB_TAG_ID = "pic_sub_tag_id";
		public static final String COLUMN_NAME_PIC_SUB_TAG_VALID = "pic_sub_tag_valid";// 1:有效 0：无效
		public static final String COLUMN_NAME_STATE = "state";
		public static final String COLUMN_NAME_PIC_SUB_TAG_COUNT = "pic_sub_tag_count";
	}
}
