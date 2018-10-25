package com.inventory.config.acct.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author anil
 * 
 *   Aug 3, 2013
 */


@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_CREDIT_NOTE)
public class CreditNoteModel implements Serializable {


	public CreditNoteModel() {
		super();
	}

	public CreditNoteModel(long id) {
		super();
		this.id = id;
	}

	public CreditNoteModel(long id, String bill_no) {
		super();
		this.id = id;
		this.bill_no = bill_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "supplier_customer", columnDefinition = "int default 1", nullable = false)
	private int supplier_customer;
	
	@Column(name = "bill_no", columnDefinition="varchar(100) default ''", nullable=false)
	private String bill_no;
	
	@OneToOne
	@JoinColumn(name = "ledger")
	private LedgerModel ledger;

	@Column(name = "ref_no")
	private String ref_no;
	
	@Column(name = "date")
	private Date date;

	@Column(name = "transaction_id")
	private long transactionId;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "memo")
	private String memo;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "credit_note_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<CreditNoteDetailsModel> credit_note_list = new ArrayList<CreditNoteDetailsModel>();

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

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<CreditNoteDetailsModel> getCredit_note_list() {
		return credit_note_list;
	}

	public void setCredit_note_list(List<CreditNoteDetailsModel> credit_note_list) {
		this.credit_note_list = credit_note_list;
	}

	public int getSupplier_customer() {
		return supplier_customer;
	}

	public void setSupplier_customer(int supplier_customer) {
		this.supplier_customer = supplier_customer;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	
}
