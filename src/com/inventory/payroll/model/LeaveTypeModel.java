package com.inventory.payroll.model;

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
 * @author sangeeth
 * @date 11-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_LEAVE_TYPE)
public class LeaveTypeModel implements Serializable {

	public LeaveTypeModel() {
		
	}
	
	public LeaveTypeModel(long id) {
		super();
		this.id = id;
	}

	public LeaveTypeModel(long id, String name) {
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
	
	@Column(name="carry_forward", columnDefinition="boolean default false", nullable=false)
	private boolean carry_forward;
	
	@Column(name="loss_of_pay", columnDefinition="boolean default false", nullable=false)
	private boolean lop;
	
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

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public boolean isCarry_forward() {
		return carry_forward;
	}

	public void setCarry_forward(boolean carry_forward) {
		this.carry_forward = carry_forward;
	}

	public boolean isLop() {
		return lop;
	}

	public void setLop(boolean lop) {
		this.lop = lop;
	}
	
}
