package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_DISPOSAL_ITEMS_DETAILS_MODEL)
public class DisposalItemsDetailsModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;
	
	@OneToOne
	@JoinColumn(name = "unit")
	private UnitModel unit;

	@Column(name = "quantity" ,columnDefinition ="double default 0", nullable=false)
	private double qunatity;
	
	@Column(name = "qty_in_basic_unit" ,columnDefinition ="double default 0", nullable=false)
	private double qty_in_basic_unit;
	
	@Column(name = "stock_id")
	private long stockId;
	
	@Column(name = "type")
	private int type;

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public long getStockId() {
		return stockId;
	}

	public void setStockId(long stockId) {
		this.stockId = stockId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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

	public double getQty_in_basic_unit() {
		return qty_in_basic_unit;
	}

	public void setQty_in_basic_unit(double qty_in_basic_unit) {
		this.qty_in_basic_unit = qty_in_basic_unit;
	}
	
	
}
