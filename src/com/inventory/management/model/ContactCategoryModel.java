package com.inventory.management.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_CONTACT_CATEGORY)
public class ContactCategoryModel implements Serializable {

	private static final long serialVersionUID = -8017486469107788366L;

	public ContactCategoryModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public ContactCategoryModel(long id) {
		super();
		this.id = id;
	}

	public ContactCategoryModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private int type;

	@Column(name = "organization_id")
	private long organization_id;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(long organization_id) {
		this.organization_id = organization_id;
	}

}
