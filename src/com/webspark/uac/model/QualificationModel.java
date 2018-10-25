package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author sangeeth
 * Automobile
 * 10-Jun-2015
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_QUALIFICATION)
public class QualificationModel implements Serializable {

	public QualificationModel() {
		
	}
	
	public QualificationModel(long id) {
		super();
		this.id = id;
	}

	public QualificationModel(long id, String name) {
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
	
	@Column(name="status", columnDefinition="bigint default 1", nullable=false)
	private long status;
	
	@OneToOne
	@JoinColumn(name="office")
	private S_OfficeModel office;

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

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}
	
}
