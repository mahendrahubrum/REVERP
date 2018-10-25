package com.inventory.purchase.bean;

import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;


public class PurchaseReturnBean {

	long id;
	PurchaseInventoryDetailsModel det;
	private PurchaseModel purchaseModel;
	
	public PurchaseReturnBean(long id, PurchaseInventoryDetailsModel det,
			PurchaseModel purchaseModel) {
		super();
		this.id = id;
		this.det = det;
		this.purchaseModel = purchaseModel;
	}

	public PurchaseReturnBean(long id, PurchaseInventoryDetailsModel det) {
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

	public PurchaseInventoryDetailsModel getDet() {
		return det;
	}

	public void setDet(PurchaseInventoryDetailsModel det) {
		this.det = det;
	}

	public PurchaseModel getPurchaseModel() {
		return purchaseModel;
	}

	public void setPurchaseModel(PurchaseModel purchaseModel) {
		this.purchaseModel = purchaseModel;
	}
	
		
	
}
