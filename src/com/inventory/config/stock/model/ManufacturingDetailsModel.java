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

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 25, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_MANUFACTURING_DETAILS)
public class ManufacturingDetailsModel implements Serializable {

	private static final long serialVersionUID = 1429519691271845268L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "fk_item_id")
	private ItemModel item;

	@OneToOne
	@JoinColumn(name = "fk_unit_id")
	private UnitModel unit;

	@Column(name = "quantity")
	private double quantity;
	
	@Column(name = "quantity_in_basic_unit",columnDefinition="double default 0",nullable =false)
	private double quantityInBasicUnit;


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

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getQuantityInBasicUnit() {
		return quantityInBasicUnit;
	}

	public void setQuantityInBasicUnit(double quantityInBasicUnit) {
		this.quantityInBasicUnit = quantityInBasicUnit;
	}
}
