package com.inventory.sales.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 14, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_TAILORING_ITEM_SPEC)
public class TailoringItemSpecModel implements Serializable {

	private static final long serialVersionUID = 6450017602488890357L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 1000)
	private String name;
	
	@Column(name = "details", length = 1000)
	private String details;
	
	@Column(name = "price")
	private double price;

	@Column(name = "type")
	private long type;
	
	@Column(name = "organization")
	private long organization;

	public TailoringItemSpecModel() {
		super();
	}

	public TailoringItemSpecModel(long id) {
		super();
		this.id = id;
	}

	public TailoringItemSpecModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public long getOrganization() {
		return organization;
	}

	public void setOrganization(long organization) {
		this.organization = organization;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}
}

