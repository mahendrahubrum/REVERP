package com.inventory.config.stock.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 15, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_STOCK_RESET_DETAILS)
public class StockResetDetailsModel implements Serializable {

	private static final long serialVersionUID = -432581365669211025L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "reseted_quantity")
	private double reseted_quantity;

	@Column(name = "balance_before_reset")
	private double balance_before_reset;

	@Column(name = "date")
	private Date date;

	public StockResetDetailsModel() {
		super();
	}

	public StockResetDetailsModel(ItemModel item, double reseted_quantity, Date date,
			double balance_before_reset) {
		super();
		this.item = item;
		this.reseted_quantity = reseted_quantity;
		this.date = date;
		this.balance_before_reset = balance_before_reset;
	}

	public StockResetDetailsModel(long id) {
		super();
		this.id = id;
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

	public double getReseted_quantity() {
		return reseted_quantity;
	}

	public void setReseted_quantity(double reseted_quantity) {
		this.reseted_quantity = reseted_quantity;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getBalance_before_reset() {
		return balance_before_reset;
	}

	public void setBalance_before_reset(double balance_before_reset) {
		this.balance_before_reset = balance_before_reset;
	}

}