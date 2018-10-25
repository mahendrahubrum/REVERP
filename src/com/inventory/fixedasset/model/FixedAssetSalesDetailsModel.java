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
@Table(name = SConstants.tb_names.I_FIXED_ASSET_SALES_DETAILS)
public class FixedAssetSalesDetailsModel implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "depreciationId")
	private FixedAssetDepreciationModel depreciationId;
	
	@Column(name = "sales_quantity")
	private double salesQuantity;
	
	@Column(name = "sales_unit_price")
	private double salesUnitPrice;
	
	
public FixedAssetSalesDetailsModel() {
	
}
	public FixedAssetSalesDetailsModel(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public FixedAssetDepreciationModel getDepreciationId() {
		return depreciationId;
	}
	public void setDepreciationId(FixedAssetDepreciationModel depreciationId) {
		this.depreciationId = depreciationId;
	}
	public double getSalesQuantity() {
		return salesQuantity;
	}
	public void setSalesQuantity(double salesQuantity) {
		this.salesQuantity = salesQuantity;
	}
	public double getSalesUnitPrice() {
		return salesUnitPrice;
	}
	public void setSalesUnitPrice(double salesUnitPrice) {
		this.salesUnitPrice = salesUnitPrice;
	}
	@Override
	public boolean equals(Object obj) {
		FixedAssetSalesDetailsModel m = (FixedAssetSalesDetailsModel) obj;		
		return this.getId() == m.getId();
	}
	
	
}
