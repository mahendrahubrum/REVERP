package com.inventory.sales.bean;

import com.inventory.sales.model.SalesInquiryDetailsModel;


public class SalesQuotationBean {

	long id;
	SalesInquiryDetailsModel det;
	
	public SalesQuotationBean(long id,
			SalesInquiryDetailsModel det) {
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

	public SalesInquiryDetailsModel getDet() {
		return det;
	}

	public void setDet(SalesInquiryDetailsModel det) {
		this.det = det;
	}
	
	
}
