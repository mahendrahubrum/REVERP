package com.inventory.config.acct.model;

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
 * Apr 24, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_CUSTOMER_GROUP)
public class CustomerGroupModel implements Serializable {

	private static final long serialVersionUID = -1450092808666477948L;

	public CustomerGroupModel() {
		super();
	}

	public CustomerGroupModel(long id) {
		super();
		this.id = id;
	}

	public CustomerGroupModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "details", length = 300)
	private String details;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "office_id")
	private long officeId;

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

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

}
