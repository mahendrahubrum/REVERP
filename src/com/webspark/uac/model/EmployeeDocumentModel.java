package com.webspark.uac.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_EMPLOYEE_DOCUMENT)
public class EmployeeDocumentModel implements Serializable{

	public EmployeeDocumentModel() {
		super();
	}

	public EmployeeDocumentModel(long id) {
		super();
		this.id = id;
	}

	public EmployeeDocumentModel(long id, String filename) {
		super();
		this.id = id;
		this.filename = filename;
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="employee_id")
	private long employee_id;
	
	@OneToOne
	@JoinColumn(name="document")
	private EmployeeDocumentCategoryModel document;
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="expiry")
	private Date expiry;
	
	@Column(name="office_id", columnDefinition="bigint default 0", nullable=false)
	private long office_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(long employee_id) {
		this.employee_id = employee_id;
	}

	public EmployeeDocumentCategoryModel getDocument() {
		return document;
	}

	public void setDocument(EmployeeDocumentCategoryModel document) {
		this.document = document;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}
	
}
