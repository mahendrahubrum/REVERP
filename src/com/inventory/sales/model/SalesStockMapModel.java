package com.inventory.sales.model;

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
 * Apr 30, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_SALES_STOCK_MAP)
public class SalesStockMapModel implements Serializable {

	private static final long serialVersionUID = 8621383556633954154L;


	public SalesStockMapModel() {
		super();
	}

	
	public SalesStockMapModel(long id) {
		super();
		this.id = id;
	}


	public SalesStockMapModel(long salesId, long salesInventoryId,
			long stockId, double quantity,int type) {
		super();
		this.salesId = salesId;
		this.salesInventoryId = salesInventoryId;
		this.stockId = stockId;
		this.quantity = quantity;
		this.type = type;
	}


	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "sales_id")
	private long salesId;

	@Column(name = "sales_inventory_id")
	private long salesInventoryId;

	@Column(name = "stock_id")
	private long stockId;

	@Column(name = "quantity")
	private double quantity;
	
	//type=1 sales. type=2 deliverynote
	@Column(name = "type", columnDefinition="integer default 1",nullable=false)
	private int type;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSalesId() {
		return salesId;
	}

	public void setSalesId(long salesId) {
		this.salesId = salesId;
	}

	public long getSalesInventoryId() {
		return salesInventoryId;
	}

	public void setSalesInventoryId(long salesInventoryId) {
		this.salesInventoryId = salesInventoryId;
	}

	public long getStockId() {
		return stockId;
	}

	public void setStockId(long stockId) {
		this.stockId = stockId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}
}
