package com.inventory.purchase.model;

import java.io.Serializable;

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
 *         Jun 29, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_STOCK_SALES_PRICE)
public class StockRateModel implements Serializable {

	private static final long serialVersionUID = -7124601629117753930L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "stock_id")
	private long stock_id;

	@Column(name = "unit_id")
	private long unit_id;

	@Column(name = "rate")
	private double rate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStock_id() {
		return stock_id;
	}

	public void setStock_id(long stock_id) {
		this.stock_id = stock_id;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

}
