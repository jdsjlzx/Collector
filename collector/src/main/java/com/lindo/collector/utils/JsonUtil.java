package com.lindo.collector.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lindo.collector.domain.JoinedDoings;
import com.lindo.collector.domain.Task;
import com.lindo.collector.domain.User;
import com.lzx.work.utils.AppConstant;

public class JsonUtil {
	protected static String TAG = "JsonUtil";
	public static int getStateFromServer(String json){
		int state = 600;
		try {
			JSONObject jsonObject = new JSONObject(json);
			state = jsonObject.getInt("status");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public static String getUserToken(String json){
		String token = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			token = jsonObject.getString("data");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return token;
	}
	
	public static JoinedDoings getJoinedDoingsByJson(String json) {
		if (AppConstant.DEBUG) {
			Log.d(TAG, "getJoinedDoingsByJson json = " + json);
		}
		JoinedDoings joinedDoings = new JoinedDoings();
		try {
			JSONObject jsonObject = new JSONObject(json);
			String imgPath = jsonObject.getString("attachment_path");
			JSONObject object = jsonObject.getJSONObject("data");
			joinedDoings.name = object.getString("title");
			joinedDoings.uploaded_photo_num = object.getInt("uploaded_img_num");
			joinedDoings.require_photo_num = object.getInt("require_photo_num");
			joinedDoings.state = object.getInt("status");
			
			if (!object.isNull("task_apply_photo")) {
				JSONArray tasksArray = object.getJSONArray("task_apply_photo");
				if (null != tasksArray) {
					for (int i = 0; i < tasksArray.length(); i++) {
						JSONObject taskObject = tasksArray.getJSONObject(i);
						Task task = new Task();
						task.picPath = imgPath + taskObject.getString("photo_path");
						task.picSrc = taskObject.getString("photo_source");
						task.pic_status = taskObject.getInt("status");
						task.state = 1;
						task.index = i;
						JSONArray tagsArray = taskObject.getJSONArray("photo_tag");
						if (null != tagsArray) {
							for (int j = 0; j < tagsArray.length(); j++) {
								JSONObject tagObject = (JSONObject) tagsArray.get(j);
								StringBuilder sb = new StringBuilder();
								sb.append(tagObject.get("level1")).append("-")
								.append(tagObject.get("level2")).append("-")
								.append(tagObject.get("level3")).append(";");
								task.tag += sb.toString();
							}
						}
						joinedDoings.taskList.add(task);
					}
				}
			}
			
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return joinedDoings;
	}
	
	public static User getUserInfoByJson(String json,String mobile) {
		if (AppConstant.DEBUG) {
			Log.d(TAG, "getUserInfoByJson json = " + json);
		}
		
		User user = new User();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject object = jsonObject.getJSONObject("data");
			user.mobile = mobile;
			user.nick = object.getString("nickname");
			user.pushId = object.getString("push_id");
			user.level = object.getInt("level");
			user.score = object.getInt("point");
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String checkNewMsgByJson(String json) {
		if (AppConstant.DEBUG) {
			Log.d(TAG, "checkNewMsgByJson json = " + json);
		}
		
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject object = jsonObject.getJSONObject("data");
			String msg_state = object.getString("new_msg");
			return msg_state;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getVerifyVodeByJson(String json) {
		Log.d(TAG, "getVerifyVodeByJson json = " + json);
		try {
			JSONObject object = new JSONObject(json);
			if (!object.isNull("data")) {
				JSONObject data = object.getJSONObject("data");
				String code = data.getString("token");
				return code;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
