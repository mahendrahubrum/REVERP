package com.inventory.purchase.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.model.RackModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 29, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_STOCK_RACK_MAPPING)
public class StockRackMappingModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1804706111842229053L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "stock_id")
	private ItemStockModel stock;

	@OneToOne
	@JoinColumn(name = "rack_id")
	private RackModel rack;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "balance",columnDefinition = "double default 0", nullable = false)
	private double balance;

	@Column(name = "quantity_in_basic_unit")
	private double quantity_in_basic_unit;

	@Column(name = "unit_id")
	private long unit_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemStockModel getStock() {
		return stock;
	}

	public void setStock(ItemStockModel stock) {
		this.stock = stock;
	}

	public RackModel getRack() {
		return rack;
	}

	public void setRack(RackModel rack) {
		this.rack = rack;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

}
