package com.inventory.finance.model;

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
 *         WebSpark.
 * 
 *         Sep 3, 2013
 */
@Entity
@Table(name = SConstants.tb_names.I_FINANCE_COMPONENTS)
public class FinanceComponentModel implements Serializable {


	private static final long serialVersionUID = -7985182730049695143L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "status")
	private int status;
	
	@Column(name = "opening_balance")
	private double opening_balance;
	
	@Column(name = "current_balance")
	private double current_balance;
	
	@Column(name = "office_id")
	private long officeId;
	
	public FinanceComponentModel(long id) {
		super();
		this.id = id;
	}
	
	

	public FinanceComponentModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}



	public FinanceComponentModel() {
		super();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}



	public double getOpening_balance() {
		return opening_balance;
	}



	public void setOpening_balance(double opening_balance) {
		this.opening_balance = opening_balance;
	}



	public long getOfficeId() {
		return officeId;
	}



	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}



	public double getCurrent_balance() {
		return current_balance;
	}



	public void setCurrent_balance(double current_balance) {
		this.current_balance = current_balance;
	}
}
