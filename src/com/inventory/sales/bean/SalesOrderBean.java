package com.inventory.sales.bean;

import com.inventory.sales.model.QuotationDetailsModel;


public class SalesOrderBean {

	long id;
	QuotationDetailsModel det;
	
	public SalesOrderBean(long id, QuotationDetailsModel det) {
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

	public QuotationDetailsModel getDet() {
		return det;
	}

	public void setDet(QuotationDetailsModel det) {
		this.det = det;
	}
	
	
}
