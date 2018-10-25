package com.inventory.onlineSales.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 24, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_ONLINE_SALES_ORDER_DETAILS)
public class OnlineSalesOrderDetailsModel implements Serializable {

	private static final long serialVersionUID = 7656003128009835591L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "online_sales_order_id")
	private long onlineSalesOrderId;

	@Column(name = "item_id")
	private long item;

	@Column(name = "quantity")
	private double qunatity;

	@Column(name = "unit_id")
	private long unit;

	@Column(name = "unit_price")
	private double unit_price;

	@Column(name = "discount_amount")
	private double discount_amount;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItem() {
		return item;
	}

	public void setItem(long item) {
		this.item = item;
	}

	public double getQunatity() {
		return qunatity;
	}

	public void setQunatity(double qunatity) {
		this.qunatity = qunatity;
	}

	public long getUnit() {
		return unit;
	}

	public void setUnit(long unit) {
		this.unit = unit;
	}

	public double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}

	public double getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(double discount_amount) {
		this.discount_amount = discount_amount;
	}

	public long getOnlineSalesOrderId() {
		return onlineSalesOrderId;
	}

	public void setOnlineSalesOrderId(long onlineSalesOrderId) {
		this.onlineSalesOrderId = onlineSalesOrderId;
	}


}
