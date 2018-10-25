package com.inventory.purchase.bean;

import com.inventory.purchase.model.PurchaseGRNDetailsModel;


public class PurchaseBean {

	long id;
	PurchaseGRNDetailsModel det;
	public PurchaseBean(long id, PurchaseGRNDetailsModel det) {
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
	public PurchaseGRNDetailsModel getDet() {
		return det;
	}
	public void setDet(PurchaseGRNDetailsModel det) {
		this.det = det;
	}
	
}
