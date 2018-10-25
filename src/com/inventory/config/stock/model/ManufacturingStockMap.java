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
 * Oct 17, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_MANUFACTURING_STOCK_MAP)
public class ManufacturingStockMap implements Serializable {

	private static final long serialVersionUID = 8056566227908999689L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "manufacturing_detail_id")
	private long manufacturing_detail_id;
	
	@Column(name = "stock_id")
	private long stock_id;


	public ManufacturingStockMap() {
		super();
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getManufacturing_detail_id() {
		return manufacturing_detail_id;
	}


	public void setManufacturing_detail_id(long manufacturing_detail_id) {
		this.manufacturing_detail_id = manufacturing_detail_id;
	}


	public long getStock_id() {
		return stock_id;
	}


	public void setStock_id(long stock_id) {
		this.stock_id = stock_id;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
