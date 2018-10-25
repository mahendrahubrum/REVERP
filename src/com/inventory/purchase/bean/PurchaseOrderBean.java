package com.inventory.purchase.bean;

import com.inventory.purchase.model.PurchaseQuotationDetailsModel;


public class PurchaseOrderBean {

	long id;
	PurchaseQuotationDetailsModel det;
	
	public PurchaseOrderBean(long id, PurchaseQuotationDetailsModel det) {
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

	public PurchaseQuotationDetailsModel getDet() {
		return det;
	}

	public void setDet(PurchaseQuotationDetailsModel det) {
		this.det = det;
	}
	
	
}
