package com.lindo.collector.domain;

public class Tag implements Comparable<Tag>{
	public String id;
	public String title;
	public String parentId;
	@Override
	public int compareTo(Tag tag) {
		int tag1_id = Integer.parseInt(this.id);
		int tag2_id = Integer.parseInt(tag.id);
		if (tag1_id<tag2_id) {  
            return -1;  
        }if (tag1_id>tag2_id) {  
            return 1;  
        }else {  
        	//如果t_id相等的话就无法添加  
            return 0;  
        }  
	}
}
