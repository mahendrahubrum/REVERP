package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_MANUAL_TRADING_DETAILS)
public class ManualTradingDetailsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -686338146612553213L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "login_id")
	private long client_id;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "unit_id")
	private long unit_id;

	@Column(name = "type")
	private int type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getClient_id() {
		return client_id;
	}

	public void setClient_id(long client_id) {
		this.client_id = client_id;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
