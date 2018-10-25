package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.DESIGNATION)
public class DesignationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -789943541908509779L;

	public DesignationModel() {
		super();
	}

	public DesignationModel(long id) {
		super();
		this.id = id;
	}

	public DesignationModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "designation_id")
	private long id;

	@Column(name = "designation_name", length = 100)
	private String name;

	@Column(name = "designation_description", length = 500)
	private String description;

	@Column(name = "organization_id")
	private long organization_id;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(long organization_id) {
		this.organization_id = organization_id;
	}

}
