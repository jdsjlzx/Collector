package com.lindo.collector.utils;

import java.io.File;
import java.io.IOException;

import com.lzx.work.utils.StringUtils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class CacheConfig {

	private static final String TAG = "CacheConfig";
	
    public static String getUrlCache(String url){
    	if(TextUtils.isEmpty(url)){
    		return null;
    	}
    	String result = null;
    	File file = new File(Constant.JSON_CACHE_DIR + StringUtils.replaceUrlWithPlus(url));
    	if(file.exists() && file.isFile()){
    		try {
				result = FileUtil.readTextFromFile(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return result;
    }
    
    public static void setUrlCache(String data, String url) {
    	File dirFile = new File(Constant.JSON_CACHE_DIR);
    	if(!dirFile.exists() && Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
    		dirFile.mkdirs();
    	}
    	File file = new File(Constant.JSON_CACHE_DIR + StringUtils.replaceUrlWithPlus(url));
    	//创建缓存数据到磁盘，就是创建文件
    	try {
			FileUtil.writeText2File(file, data);
		} catch (IOException e) {
			 Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
			e.printStackTrace();
		}
    }
}
