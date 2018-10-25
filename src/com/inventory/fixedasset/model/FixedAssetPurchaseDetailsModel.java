package com.inventory.fixedasset.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_FIXED_ASSET_PURCHASE_DETAILS)
public class FixedAssetPurchaseDetailsModel implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "asset_id")
	private FixedAssetModel fixedAsset;
	
	@Column(name = "quantity")
	private double quantity;
	
	@Column(name = "unit_price")
	private double unitPrice;
	
	@Column(name = "current_balance")
	private double currentBalance;
	
	
	
public FixedAssetPurchaseDetailsModel() {
	// TODO Auto-generated constructor stub
}
	public FixedAssetPurchaseDetailsModel(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public long getTransactionDetailId() {
//		return transactionDetailId;
//	}
//	public void setTransactionDetailId(long transactionDetailId) {
//		this.transactionDetailId = transactionDetailId;
//	}
	public FixedAssetModel getFixedAsset() {
		return fixedAsset;
	}

	public void setFixedAsset(FixedAssetModel fixedAsset) {
		this.fixedAsset = fixedAsset;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public double getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}
	@Override
	public boolean equals(Object obj) {
		FixedAssetPurchaseDetailsModel m = (FixedAssetPurchaseDetailsModel) obj;		
		return this.getId() == m.getId();
	}
	
	
}
