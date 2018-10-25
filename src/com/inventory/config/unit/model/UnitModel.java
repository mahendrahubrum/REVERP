package com.inventory.config.unit.model;

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
 * @author Anil K P
 * 
 *         Jun 24, 2013
 */
@Entity
@Table(name = SConstants.tb_names.I_UNIT)
public class UnitModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2061984785960950212L;

	public UnitModel() {
		super();
	}

	public UnitModel(long id, String symbol) {
		super();
		this.id = id;
		this.symbol = symbol;
	}

	public UnitModel(long id) {
		super();
		this.id = id;
	}

	public UnitModel(long id, String name, String symbol) {
		super();
		this.id = id;
		this.name = name;
		this.symbol = symbol;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "description", length = 400)
	private String description;

	@Column(name = "status")
	private long status;

	@Column(name = "symbol", length = 10)
	private String symbol;

	@OneToOne
	@JoinColumn(name = "organization_id")
	private S_OrganizationModel organization;

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

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public S_OrganizationModel getOrganization() {
		return organization;
	}

	public void setOrganization(S_OrganizationModel organization) {
		this.organization = organization;
	}
}
