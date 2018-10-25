package com.manufacturing.config.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;
/**
 * 
 * @author Muhammed Shah
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_MOULD_MODEL)
public class MouldModel implements Serializable{
	@Id
	@GeneratedValue
	@Column(name = "id",nullable = false)
	public long id;
	
	@Column(name = "mould_name",nullable = false,length = 10)
	public String mouldName;
	
	@Column(name = "details",nullable = false,length = 10)
	public String details;
	
	@Column(name = "status",nullable = false)
	public int status;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	public S_OfficeModel office;
	
	public MouldModel() {
		super();		
	}
	public MouldModel(long id) {
		super();
		this.id = id;
	}
	public MouldModel(long id,String mouldName) {
		super();
		this.id = id;
		this.mouldName = mouldName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getMouldName() {
		return mouldName;
	}
	public void setMouldName(String mouldName) {
		this.mouldName = mouldName;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public S_OfficeModel getOffice() {
		return office;
	}
	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}	
}
