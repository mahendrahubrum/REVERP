package com.inventory.config.acct.model;

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

/**
 * @author anil
 * @date 04-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_LEDGER_OPENING_BALANCE)
public class LedgerOpeningBalanceModel implements Serializable {
	public LedgerOpeningBalanceModel() {

	}
	
	public LedgerOpeningBalanceModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "ledger")
	private LedgerModel ledger;
	
	@Column(name = "type")
	private int type;
	
	@Column(name = "amount")
	private double amount;

	@Column(name = "date")
	private Date date;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}