package com.inventory.finance.model;

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
 * 
 * @author Muhammed Shah
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PAYMENT_MODE_MODEL)
public class PaymentModeModel implements Serializable{
	@Id
	@GeneratedValue
	@Column(name = "id",nullable = false)
	public long id;
	
	@Column(name = "description",nullable = false,length = 60)
	public String description;
	
	@OneToOne
	@JoinColumn(name = "ledger_id")
	public LedgerModel ledger;
	
	@Column(name = "transaction_type",nullable = false)
	public int transactionType;
	
	@Column(name = "status",nullable = false)
	public int status;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	public S_OfficeModel office;
	
	public PaymentModeModel() {
		super();		
	}
	public PaymentModeModel(long id) {
		super();
		this.id = id;
	}
	public PaymentModeModel(long id,String description) {
		super();
		this.id = id;
		this.description = description;				
	}
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
	public LedgerModel getLedger() {
		return ledger;
	}
	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
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
