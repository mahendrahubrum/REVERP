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
import com.webspark.uac.model.S_UserRoleModel;


/**
 * 
 * @author sangeeth
 * @date 11-Nov-2015
 * @Project REVERP
 */


@Entity
@Table(name = SConstants.tb_names.I_ROLE_LEAVE_MAP)
public class RoleLeaveMapModel implements Serializable {

	private static final long serialVersionUID = -3149015555199273714L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "role")
	private S_UserRoleModel role;

	@OneToOne
	@JoinColumn(name = "leave_type")
	private LeaveTypeModel leave_type;

	@Column(name = "value")
	private double value;
	
	@Column(name = "year")
	private long year;
	
	@Column(name = "office")
	private long officeId;
	
	public RoleLeaveMapModel(long id) {
		super();
		this.id = id;
	}

	public RoleLeaveMapModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public LeaveTypeModel getLeave_type() {
		return leave_type;
	}

	public void setLeave_type(LeaveTypeModel leave_type) {
		this.leave_type = leave_type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public S_UserRoleModel getRole() {
		return role;
	}

	public void setRole(S_UserRoleModel role) {
		this.role = role;
	}

	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}
	
}
