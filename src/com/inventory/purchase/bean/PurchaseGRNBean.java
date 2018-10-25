package com.inventory.purchase.bean;

import com.inventory.purchase.model.PurchaseOrderDetailsModel;


public class PurchaseGRNBean {

	long id;
	PurchaseOrderDetailsModel det;
	
	public PurchaseGRNBean(long id, PurchaseOrderDetailsModel det) {
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
	public PurchaseOrderDetailsModel getDet() {
		return det;
	}
	public void setDet(PurchaseOrderDetailsModel det) {
		this.det = det;
	}
	
	
}
