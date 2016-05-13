package com.lindo.collector.domain;

/**
 * 活动
 * @author Lizhixian
 *
 */
public class Doings {

	public String id;
	public String name;
	public String thumbUrl;
	public String detaiUrl;
	public String description;
	public Integer reward;
	public String requirement;
	public int participation_number;//参与人数
	public int upload_pic_number;//已上传图片数量
	public int height;
	public int score;//积分
	public int state;//1是进行中，2是审核中，3是已完成，4是已过期
	public int join_state;// 0:未参与 1：已参与
}
