package com.lindo.collector.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Task implements Serializable{

	public int id;
	public String picPath;
	public String picSrc;//图片来源
	public String tag = "";//三级标签title
	public String tagId = "";//三级标签Id
	public String subTagId = "";//二级标签Id
	public int subTagCount = 0;// 该任务二级标签的数量
	public int subTagIsValid = 0;//0:无效  1:有效
	public int state = -1;// -1:空   0:等待上传  1:已经上传
	public int pic_status = 1;// 1 正常 2是审核通过 3审核未通过
	public int index = -1;// 已经上传的图片的索引
	public String participantDoingsId;//已参加的活动ID
	
}
