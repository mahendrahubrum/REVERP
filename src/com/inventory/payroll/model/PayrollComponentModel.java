package com.inventory.payroll.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 *         WebSpark.
 *         Sep 3, 2013
 */

/**
 * @author sangeeth
 * @date 20-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PAYROLL_COMPONENTS)
public class PayrollComponentModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "code", length = 50)
	private String code;

	@Column(name = "action")
	private long action;

	@Column(name = "type")
	private long type;

	@Column(name = "value")
	private double value;

	@Column(name = "parent_id")
	private long parent_id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;
	
	@Column(name = "commission_enable", columnDefinition="boolean default false", nullable=false)
	private boolean commissionEnabled;
	
	public PayrollComponentModel() {
		super();
	}

	public PayrollComponentModel(long id) {
		super();
		this.id = id;
	}

	public PayrollComponentModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getAction() {
		return action;
	}

	public void setAction(long action) {
		this.action = action;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public long getParent_id() {
		return parent_id;
	}

	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public boolean isCommissionEnabled() {
		return commissionEnabled;
	}

	public void setCommissionEnabled(boolean commissionEnabled) {
		this.commissionEnabled = commissionEnabled;
	}
	
}
