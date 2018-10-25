package com.inventory.finance.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;

@Entity
@Table(name = SConstants.tb_names.I_BANK_RECONCILIATION)
public class BankRecociliationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private long id;
	
	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;
	
	@OneToOne
	@JoinColumn(name = "transafer_ledger_id")
	private LedgerModel transafer_ledger;
	
	@Column(name = "date")
	private Date date;
	
	@OneToOne
	@JoinColumn(name = "transaction_id")
	private TransactionModel transaction;
	
	/*@OneToOne
	@JoinColumn(name = "tran_details_id")
	private TransactionDetailsModel tran_details;*/
	
	@Column(name = "clearing_date")
	private Date clearing_date;
	
	@Column(name = "comments")
	private String comments;
	
	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;
	
	@Column(name = "invoice_id")
	private long invoiceId;
	
	@Column(name = "amount")
	private double amount;
	public BankRecociliationModel() {
		super();
	}
	public BankRecociliationModel(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LedgerModel getTransafer_ledger() {
		return transafer_ledger;
	}

	public void setTransafer_ledger(LedgerModel transafer_ledger) {
		this.transafer_ledger = transafer_ledger;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TransactionModel getTransaction() {
		return transaction;
	}

	public void setTransaction(TransactionModel transaction) {
		this.transaction = transaction;
	}

	public Date getClearing_date() {
		return clearing_date;
	}

	public void setClearing_date(Date clearing_date) {
		this.clearing_date = clearing_date;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	
	
	
	
	
	
	

}
