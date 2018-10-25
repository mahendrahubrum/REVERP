package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 24, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_ITEM_DAILY_RATE_DETAILS)
public class ItemDailyRateDetailModel implements Serializable {


	private static final long serialVersionUID = 519990193691468759L;

	public ItemDailyRateDetailModel() {
		super();
	}

	public ItemDailyRateDetailModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "item")
	private long item;
	
	@Column(name = "unit")
	private long unit;

	@Column(name = "rate")
	private double rate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItem() {
		return item;
	}

	public void setItem(long item) {
		this.item = item;
	}

	public long getUnit() {
		return unit;
	}

	public void setUnit(long unit) {
		this.unit = unit;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

}
