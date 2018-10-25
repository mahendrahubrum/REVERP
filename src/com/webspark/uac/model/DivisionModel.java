package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_DIVISION)
public class DivisionModel implements Serializable {

	private static final long serialVersionUID = 5180170079562960641L;

	public DivisionModel() {
		super();
	}

	public DivisionModel(long id) {
		super();
		this.id = id;
	}

	public DivisionModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public DivisionModel(long id, String name,long parent_id) {
		super();
		this.id = id;
		this.parent_id = parent_id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "organization_id")
	private long organization_id;
	
	@Column(name = "parent_id",columnDefinition="bigint default 0",nullable=false)
	private long parent_id;

	@Column(name = "level",columnDefinition="integer default 0",nullable=false)
	private int level;

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

	public long getParent_id() {
		return parent_id;
	}

	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
