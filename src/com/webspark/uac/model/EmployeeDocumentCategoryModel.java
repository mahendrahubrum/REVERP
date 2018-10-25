package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_EMPLOYEE_DOCUMENT_CATEGORY)
public class EmployeeDocumentCategoryModel implements Serializable{
	
	public EmployeeDocumentCategoryModel() {
		super();
	}

	public EmployeeDocumentCategoryModel(long id) {
		super();
		this.id = id;
	}

	public EmployeeDocumentCategoryModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="alert_before")
	private int alert_before;
	
	@Column(name="org_id")
	private long org_id;

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

	public int getAlert_before() {
		return alert_before;
	}

	public void setAlert_before(int alert_before) {
		this.alert_before = alert_before;
	}

	public long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(long org_id) {
		this.org_id = org_id;
	}
	
}
