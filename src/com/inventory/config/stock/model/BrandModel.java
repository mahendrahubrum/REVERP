package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 24, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_BRAND)
public class BrandModel implements Serializable {

	private static final long serialVersionUID = -7992385488646438134L;

	public BrandModel() {
		super();
	}

	public BrandModel(long id) {
		super();
		this.id = id;
	}

	public BrandModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "brand_code", length = 100)
	private String brandCode;

	@Column(name = "name", length = 200)
	private String name;

	@OneToOne
	@JoinColumn(name = "organization_id")
	private S_OrganizationModel organization;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public S_OrganizationModel getOrganization() {
		return organization;
	}

	public void setOrganization(S_OrganizationModel organization) {
		this.organization = organization;
	}

	

}
