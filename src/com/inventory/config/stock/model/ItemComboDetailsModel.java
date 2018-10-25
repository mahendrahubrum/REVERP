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
@Table(name = SConstants.tb_names.I_ITEM_COMBO_DETAILS)
public class ItemComboDetailsModel implements Serializable {

	private static final long serialVersionUID = -1981339226043604937L;

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
	
	@Column(name = "qty_in_basic_unit")
	private double qty_in_basic_unit;

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
}
