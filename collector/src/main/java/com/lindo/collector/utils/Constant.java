package com.lindo.collector.utils;

import android.os.Environment;


public class Constant {

	public final static int COLUMN_COUNT = 2; // 显示列数

	public final static int PICTURE_COUNT_PER_LOAD = 30; // 每次加载30张图片

	public final static int PICTURE_TOTAL_COUNT = 10000; // 允许加载的最多图片数
	public final static int PICTURE_TOTAL_TOTAL_COUNT = 6; // 允许选择的最多标签数量
	public final static int HANDLER_WHAT = 1;

	public final static int MESSAGE_DELAY = 200;
	
	public static final String APP_FILE_NAME = "app_sp";
	public static final String JSON_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Collector/JSON/";
	public static final String IMG_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Collector/DCIM/";
	public static final String DOMAIN = "http://123.56.132.166";
	/*public static final String DOMAIN = "http://120.25.209.221";*/
	public static final String GET_VERIFY_CODE_URL = DOMAIN + "/app/member/sendSms";
	public static final String MODIFY_PASSWORD_URL = DOMAIN + "/app/member/change_pwd";
	public static final String FORGET_PASSWORD_URL = DOMAIN + "/app/member/reset_pwd";
	public static final String USER_REGISTER_URL = DOMAIN + "/app/member/register";
	public static final String USER_LOGIN_URL = DOMAIN + "/app/member/login";
	public static final String USER_LOGOUT_URL = DOMAIN + "/app/member/login_out";
	public static final String USER_JOIN_DOINGS_URL = DOMAIN + "/app/task/join";
	public static final String USER_UPLOAD_PIC_URL = DOMAIN + "/app/task/upload_photo";
	public static final String USER_JOINED_TASK_INFO_URL = DOMAIN + "/app/task/get_task_apply_info";
	public static final String USER_MODIFY_INFO_URL = DOMAIN + "/app/member/set_profile";
	public static final String TASK_PIC_TAG_URL = DOMAIN + "/app/task/get_task_tag";
	public static final String USER_DOINGS_LIST_URL = DOMAIN + "/app/member/get_user_task_list";
	public static final String HELP_URL = DOMAIN + "/app/index/help";
	public static final String USER_PROFILE_URL = DOMAIN + "/app/member/get_profile";
	public static final String CHECK_NEW_MESSAGE_URL = DOMAIN + "/app/index/checkMsg";

	public static final String URL_ABOUT_US = "file:///android_asset/about.html";
	public static final String URL_PRIVACY_POLICY = "file:///android_asset/Agreement.html";

	public static final int GET_VERIFY_CODE = 60;
	public static final int PERIOD_VERIFY_CODE = 300000;
	
	public static final String DESCRIPTOR_LOGIN = "com.umeng.login";
	public static final String DESCRIPTOR_SHARE = "com.umeng.share";
	// 微信平台
	public static final String wx_appId = "wx40419133cbd461f8";
	public static final String wx_appSecret = "50c81678784fc581601864a27150f11c";
	// QQ平台
	public static final String qq_appID = "1104579615";
	public static final String qq_appSecret = "SupXWDKwQSGgbq6s";
}
