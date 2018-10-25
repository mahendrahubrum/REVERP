package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.unit.model.UnitModel;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_STOCK_TRANSFER_INVENTORY_DETAILS)
public class StockTransferInventoryDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5658640507075388089L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "stock_id")
	private ItemStockModel stock_id;
	
	@Column(name = "quantity")
	private double quantity;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;

	@Column(name = "quantity_in_basic_unit")
	private double quantity_in_basic_unit;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemStockModel getStock_id() {
		return stock_id;
	}

	public void setStock_id(ItemStockModel stock_id) {
		this.stock_id = stock_id;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQunatity(double quantity) {
		this.quantity = quantity;
	}

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}

	

}
