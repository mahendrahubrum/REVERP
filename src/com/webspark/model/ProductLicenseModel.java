package com.webspark.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 * 
 * @Date 18 Feb 2014
 */

@Entity
@Table(name = SConstants.tb_names.PRODUCT_LICENCE)
public class ProductLicenseModel implements Serializable {
	
	private static final long serialVersionUID = 4798164561153454132L;
	
	public ProductLicenseModel() {
		super();
	}

	public ProductLicenseModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "details", length = 100)
	private String details;

	@Column(name = "expiry_date")
	private Timestamp expiry_date;
	
	@Column(name = "organization_id")
	private long organizationId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Timestamp getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Timestamp expiry_date) {
		this.expiry_date = expiry_date;
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

}
