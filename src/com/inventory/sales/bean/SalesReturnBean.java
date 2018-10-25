package com.inventory.sales.bean;

import com.inventory.sales.model.SalesInventoryDetailsModel;


public class SalesReturnBean {

	long id;
	SalesInventoryDetailsModel det;
	public SalesReturnBean(long id, SalesInventoryDetailsModel det) {
		super();
		this.id = id;
		this.det = det;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public SalesInventoryDetailsModel getDet() {
		return det;
	}
	public void setDet(SalesInventoryDetailsModel det) {
		this.det = det;
	}
	
	
}
