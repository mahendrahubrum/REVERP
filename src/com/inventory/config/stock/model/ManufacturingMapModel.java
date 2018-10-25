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
 * @author Jinshad P.T.
 * 
 *         Jan 13, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_MANUFACTURING_MAP)
public class ManufacturingMapModel implements Serializable {

	private static final long serialVersionUID = 4368506474233027833L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "fk_item_id")
	private ItemModel item;
	
	@OneToOne
	@JoinColumn(name = "fk_sub_item_id")
	private ItemModel subItem;

	@OneToOne
	@JoinColumn(name = "fk_unit_id")
	private UnitModel unit;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "qty_in_basic_unit")
	private double qty_in_basic_unit;
	
	@Column(name = "master_quantity",columnDefinition="double default 0",nullable=false)
	private double master_quantity;
	
//	@Column(name = "master_quantity_in_basic",columnDefinition="double default 0",nullable=false)
//	private double master_quantity_in_basic;
//	
//	@Column(name = "master_unit",columnDefinition="bigint default 0",nullable=false)
//	private double master_unit;

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

	public double getQty_in_basic_unit() {
		return qty_in_basic_unit;
	}

	public void setQty_in_basic_unit(double qty_in_basic_unit) {
		this.qty_in_basic_unit = qty_in_basic_unit;
	}

	public ItemModel getSubItem() {
		return subItem;
	}

	public void setSubItem(ItemModel subItem) {
		this.subItem = subItem;
	}

	public double getMaster_quantity() {
		return master_quantity;
	}

	public void setMaster_quantity(double master_quantity) {
		this.master_quantity = master_quantity;
	}
}
