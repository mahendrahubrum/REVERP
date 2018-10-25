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


@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_ITEM_PHYSICAL_STOCK)
public class ItemPhysicalStockModel implements Serializable {

	public ItemPhysicalStockModel() {
		
	}
	
	public ItemPhysicalStockModel(long id) {
		super();
		this.id = id;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@OneToOne
	@JoinColumn(name="item")
	private ItemModel item;
	
	@Column(name="office")
	private long office;
	
	@Column(name="date")
	private Date date;
	
	@Column(name="current_stock")
	private double current_stock;
	
	@Column(name="physical_stock")
	private double physical_stock;
	
	@Column(name="difference")
	private double difference;
	
	@Column(name="value_difference")
	private double value_difference;

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

	public long getOffice() {
		return office;
	}

	public void setOffice(long office) {
		this.office = office;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getCurrent_stock() {
		return current_stock;
	}

	public void setCurrent_stock(double current_stock) {
		this.current_stock = current_stock;
	}

	public double getPhysical_stock() {
		return physical_stock;
	}

	public void setPhysical_stock(double physical_stock) {
		this.physical_stock = physical_stock;
	}

	public double getDifference() {
		return difference;
	}

	public void setDifference(double difference) {
		this.difference = difference;
	}

	public double getValue_difference() {
		return value_difference;
	}

	public void setValue_difference(double value_difference) {
		this.value_difference = value_difference;
	}
	
}
