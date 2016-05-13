package com.lindo.collector.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 已经参加的活动
 * @author Lizhixian
 *
 */
public class JoinedDoings {

	public String id;
	public String name;
	public Integer require_photo_num;
	public Integer uploaded_photo_num;
	public int state;//1是进行中，2是审核中，3是已完成，4是已过期
	public List<Task> taskList = new ArrayList<Task>();//已经上传的图片
}
