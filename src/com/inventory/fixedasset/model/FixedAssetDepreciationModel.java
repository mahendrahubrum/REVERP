package com.inventory.fixedasset.model;

import java.io.Serializable;
import java.sql.Date;

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
@Table(name = SConstants.tb_names.I_FIXED_ASSET_DEPRECIATION)
public class FixedAssetDepreciationModel implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "fixed_asset_purchase_details_id")
	private FixedAssetPurchaseDetailsModel fixedAssetPurchaseDetailsId;
	
	@Column(name = "quantity")
	private double quantity;
	
	@Column(name = "percentage")
	private double percentage;	
	
	@Column(name = "depreciation_type")
	private int type;
	
	@Column(name = "amount")
	private double amount;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "sequence_no")
	private long sequenceNo;
	
	@Column(name = "depreciation_mode")
	private int depreciationMode;
	
	
	
public FixedAssetDepreciationModel() {
	// TODO Auto-generated constructor stub
}
	public FixedAssetDepreciationModel(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

	public int getDepreciationMode() {
		return depreciationMode;
	}
	public void setDepreciationMode(int depreciationMode) {
		this.depreciationMode = depreciationMode;
	}
	public long getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	public FixedAssetPurchaseDetailsModel getFixedAssetPurchaseDetailsId() {
		return fixedAssetPurchaseDetailsId;
	}
	public void setFixedAssetPurchaseDetailsId(
			FixedAssetPurchaseDetailsModel fixedAssetPurchaseDetailsId) {
		this.fixedAssetPurchaseDetailsId = fixedAssetPurchaseDetailsId;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public boolean equals(Object obj) {
		FixedAssetDepreciationModel m = (FixedAssetDepreciationModel) obj;		
		return this.getId() == m.getId();
	}
	
	
}
