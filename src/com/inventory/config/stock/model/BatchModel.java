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
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_BATCH)
public class BatchModel implements Serializable {

	public BatchModel() {
		super();
	}

	public BatchModel(long id) {
		super();
		this.id = id;
	}

	public BatchModel(long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;
	
	@Column(name = "manufacturing_date")
	private Date manufacturing_date;
	
	@Column(name = "expiry_date")
	private Date expiry_date;
	
	@Column(name = "manufacturer")
	private String manufacturer;
	
	@Column(name = "rate" ,columnDefinition ="double default 0", nullable=false)
	private double rate;

	@Column(name = "description")
	private String description;
	
	@Column(name = "office")
	private long office_id;

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

	public Date getManufacturing_date() {
		return manufacturing_date;
	}

	public void setManufacturing_date(Date manufacturing_date) {
		this.manufacturing_date = manufacturing_date;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

}
