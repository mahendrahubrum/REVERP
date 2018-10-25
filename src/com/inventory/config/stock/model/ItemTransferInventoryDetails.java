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
import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_ITEM_TRANSFER_INVENTORY_DETAILS)
public class ItemTransferInventoryDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7927533760179878450L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity")
	private double qunatity;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;

	@Column(name = "stock_id")
	private String stock_id;

	@Column(name = "to_office_id")
	private long to_office_id;

	@Column(name = "stk_id")
	private long stk_id;

	@Column(name = "quantity_in_basic_unit")
	private double quantity_in_basic_unit;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	public double getQunatity() {
		return qunatity;
	}

	public void setQunatity(double qunatity) {
		this.qunatity = qunatity;
	}

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public long getTo_office_id() {
		return to_office_id;
	}

	public void setTo_office_id(long to_office_id) {
		this.to_office_id = to_office_id;
	}

	public String getStock_id() {
		return stock_id;
	}

	public void setStock_id(String stock_id) {
		this.stock_id = stock_id;
	}

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}

	public long getStk_id() {
		return stk_id;
	}

	public void setStk_id(long stk_id) {
		this.stk_id = stk_id;
	}
}
