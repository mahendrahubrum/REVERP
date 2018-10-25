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
 * @author Jinshad P.T.
 * 
 *         Aug 3, 2013
 */

/**
 * @author sangeeth
 * @date 14-Oct-2015
 * @Project REVERP
 */


@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PDC)
public class PdcModel implements Serializable {


	public PdcModel() {
		super();
	}

	public PdcModel(long id) {
		super();
		this.id = id;
	}

	public PdcModel(long id, String bill_no) {
		super();
		this.id = id;
		this.bill_no = bill_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "type", columnDefinition = "int default 1", nullable = false)
	private int type;
	
	@Column(name = "bill_no", columnDefinition="varchar(100) default ''", nullable=false)
	private String bill_no;
	
	@OneToOne
	@JoinColumn(name = "bank_account")
	private LedgerModel bankAccount;

	@Column(name = "ref_no")
	private String ref_no;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "cheque_date")
	private Date chequeDate;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "memo")
	private String memo;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "pdc_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<PdcDetailsModel> pdc_list = new ArrayList<PdcDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LedgerModel getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(LedgerModel bankAccount) {
		this.bankAccount = bankAccount;
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

	public List<PdcDetailsModel> getPdc_list() {
		return pdc_list;
	}

	public void setPdc_list(List<PdcDetailsModel> pdc_list) {
		this.pdc_list = pdc_list;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	
}
