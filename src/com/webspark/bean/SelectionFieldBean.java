package com.webspark.bean;

/**
 * @author anil
 * @date 12-Nov-2015
 * @Project REVERP
 */

public class SelectionFieldBean {

	long id;
	long parentId;
	String value;
	
	public SelectionFieldBean() {
		
	}

	public SelectionFieldBean(long id, long parentId, String value) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.value = value;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}
