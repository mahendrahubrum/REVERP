package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_ITEM_CUSTOMER_BARCODE_MAP)
public class ItemCustomerBarcodeMapModel implements Serializable {

	public ItemCustomerBarcodeMapModel() {
		
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	//customer ledger
	@Column(name="customer_id")
	private long customerId;
	
	@Column(name="item_id")
	private long itemId;
	
	@Column(name="barcode")
	private String barcode;
	
	@Column(name="percentage",columnDefinition="double default 0",nullable=false)
	private double percentage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}
