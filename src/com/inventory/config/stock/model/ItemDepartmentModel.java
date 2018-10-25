package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.tax.model.TaxModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * May 13, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_ITEM_DEPARTMENT)
public class ItemDepartmentModel implements Serializable {


	private static final long serialVersionUID = 7718099268732281712L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "code", length = 50)
	private String code;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "organization_id")
	private S_OrganizationModel organization;

	public ItemDepartmentModel() {
		super();
	}

	public ItemDepartmentModel(long id) {
		super();
		this.id = id;
	}

	public ItemDepartmentModel(long id, String name) {
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_OrganizationModel getOrganization() {
		return organization;
	}

	public void setOrganization(S_OrganizationModel organization) {
		this.organization = organization;
	}

}
