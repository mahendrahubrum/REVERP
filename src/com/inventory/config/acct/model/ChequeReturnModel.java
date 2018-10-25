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
 * 
 * @author sangeeth
 * @date 28-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_CHEQUE_RETURN)
public class ChequeReturnModel implements Serializable {


	public ChequeReturnModel() {
		super();
	}

	public ChequeReturnModel(long id) {
		super();
		this.id = id;
	}

	public ChequeReturnModel(long id, String ref_no) {
		super();
		this.id = id;
		this.ref_no = ref_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "supplier_customer", columnDefinition = "int default 1", nullable = false)
	private int supplier_customer;
	
	@OneToOne
	@JoinColumn(name = "ledger")
	private LedgerModel ledger;

	@Column(name = "ref_no")
	private String ref_no;
	
	@Column(name = "date")
	private Date date;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "memo")
	private String memo;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "cheque_return_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<ChequeReturnDetailsModel> cheque_return_list = new ArrayList<ChequeReturnDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSupplier_customer() {
		return supplier_customer;
	}

	public void setSupplier_customer(int supplier_customer) {
		this.supplier_customer = supplier_customer;
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

	public List<ChequeReturnDetailsModel> getCheque_return_list() {
		return cheque_return_list;
	}

	public void setCheque_return_list(
			List<ChequeReturnDetailsModel> cheque_return_list) {
		this.cheque_return_list = cheque_return_list;
	}

}
