package com.inventory.sales.bean;

import com.inventory.sales.model.SalesOrderDetailsModel;
import com.inventory.sales.model.SalesOrderModel;


public class DeliveryNoteBean {

	long id;
	private long custId;
	private SalesOrderModel salesOrder;
	SalesOrderDetailsModel det;
	
	public DeliveryNoteBean(long id, SalesOrderDetailsModel det) {
		super();
		this.id = id;
		this.det = det;
	}
	public DeliveryNoteBean(long id,long custId, SalesOrderDetailsModel det) {
		super();
		this.id = id;
		this.det = det;
		this.custId = custId;
	}
	public DeliveryNoteBean(long id,long custId, SalesOrderDetailsModel det,SalesOrderModel salesOrder) {
		super();
		this.id = id;
		this.det = det;
		this.custId = custId;
		this.salesOrder = salesOrder;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public SalesOrderDetailsModel getDet() {
		return det;
	}
	public void setDet(SalesOrderDetailsModel det) {
		this.det = det;
	}
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public SalesOrderModel getSalesOrder() {
		return salesOrder;
	}
	public void setSalesOrder(SalesOrderModel salesOrder) {
		this.salesOrder = salesOrder;
	}
	
	
}
