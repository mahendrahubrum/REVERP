package com.inventory.sales.bean;

import com.inventory.sales.model.DeliveryNoteDetailsModel;


public class SalesBean {

	long id;
	DeliveryNoteDetailsModel det;
	public SalesBean(long id, DeliveryNoteDetailsModel det) {
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
	public DeliveryNoteDetailsModel getDet() {
		return det;
	}
	public void setDet(DeliveryNoteDetailsModel det) {
		this.det = det;
	}
	
}
