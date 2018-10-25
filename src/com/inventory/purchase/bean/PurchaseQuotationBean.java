package com.inventory.purchase.bean;

import com.inventory.purchase.model.PurchaseInquiryDetailsModel;


public class PurchaseQuotationBean {

	long id;
	PurchaseInquiryDetailsModel det;
	
	public PurchaseQuotationBean(long id,
			PurchaseInquiryDetailsModel det) {
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

	public PurchaseInquiryDetailsModel getDet() {
		return det;
	}

	public void setDet(PurchaseInquiryDetailsModel det) {
		this.det = det;
	}
	
	
}
