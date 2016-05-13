package com.lindo.collector;

import java.util.HashMap;

import com.baidu.frontia.FrontiaApplication;
import com.lindo.collector.domain.User;
import com.lindo.collector.fragment.HomeFragment;
import com.lindo.collector.utils.Constant;
import com.lindo.collector.utils.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.content.Context;

public class PushApplication extends FrontiaApplication {
	private boolean isLogin = false;
	private User user = null;
	private String userId = null;//百度推送用
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private static PushApplication instance;

	public static PushApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initImageLoader(this);
	}
	
	/**
	 * ImageLoader 图片组件初始化
	 * 
	 * @param context
	 */
	private void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove
									// for
									// release
									// app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logoutUser() {
		setLogin(false);
		setUser(null);
		Utils.setCookies(null);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("auto_login", false);
		com.lzx.work.utils.Utils.saveMsg(getApplicationContext(), Constant.APP_FILE_NAME, map);

	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		HomeFragment.refreshList();
	}
}
