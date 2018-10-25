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
 * @author sangeeth
 * @date 24-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_OVER_TIME)
public class OverTimeModel implements Serializable {

	public OverTimeModel() {
		
	}
	
	public OverTimeModel(long id) {
		super();
		this.id = id;
	}

	public OverTimeModel(long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="description")
	private String description;
	
	@Column(name="value_type", columnDefinition = "bigint default 2", nullable=false)
	private long valueType;
	
	@Column(name="value")
	private double value;
	
	@Column(name="payroll_component", columnDefinition = "bigint default 0", nullable=false)
	private long payrollComponent;
	
	@OneToOne
	@JoinColumn(name="office")
	private S_OfficeModel office;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getValueType() {
		return valueType;
	}

	public void setValueType(long valueType) {
		this.valueType = valueType;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public long getPayrollComponent() {
		return payrollComponent;
	}

	public void setPayrollComponent(long payrollComponent) {
		this.payrollComponent = payrollComponent;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}
	
}