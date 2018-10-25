package com.inventory.process.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.ItemModel;
import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_ITEM_CLOSING_STOCK)
public class ItemClosingStockModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8710094450836685194L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "inwards_qty")
	private double inwards_qty;

	@Column(name = "outwards_qty")
	private double outwards_qty;

	@Column(name = "closing_stock")
	private double closing_stock;

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

	public double getInwards_qty() {
		return inwards_qty;
	}

	public void setInwards_qty(double inwards_qty) {
		this.inwards_qty = inwards_qty;
	}

	public double getOutwards_qty() {
		return outwards_qty;
	}

	public void setOutwards_qty(double outwards_qty) {
		this.outwards_qty = outwards_qty;
	}

	public double getClosing_stock() {
		return closing_stock;
	}

	public void setClosing_stock(double closing_stock) {
		this.closing_stock = closing_stock;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

}
