package com.inventory.payroll.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_SALES_MAN_COMMISSION_MAP)
public class SalesManCommissionMapModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="user_id")
	private long userId;
	
	@Column(name="office_id")
	private long officeId;
	
	@Column(name="commission_percentage", columnDefinition="double default 0", nullable=false)
	private double commissionPercentage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getCommissionPercentage() {
		return commissionPercentage;
	}

	public void setCommissionPercentage(double commissionPercentage) {
		this.commissionPercentage = commissionPercentage;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}
	
}
