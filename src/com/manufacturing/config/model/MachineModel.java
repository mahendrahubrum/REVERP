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
@Table(name = SConstants.tb_names.I_MACHINE_MODEL)
public class MachineModel implements Serializable{
	@Id
	@GeneratedValue
	@Column(name = "id",nullable = false)
	public long id;
	
	@Column(name = "machine_name",nullable = false,length = 10)
	public String machineName;
	
	@Column(name = "details",nullable = false,length = 10)
	public String details;
	
	@Column(name = "status",nullable = false)
	public int status;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	public S_OfficeModel office;
	
	public MachineModel() {
		super();		
	}
	public MachineModel(long id) {
		super();
		this.id = id;
	}
	public MachineModel(long id,String machineName) {
		super();
		this.id = id;
		this.machineName = machineName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
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
