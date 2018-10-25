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
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 3, 2013
 */

/**
 * @author sangeeth
 * @date 15-Oct-2015
 * @Project REVERP
 */


@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PDC_PAYMENT)
public class PdcPaymentModel implements Serializable {


	public PdcPaymentModel() {
		super();
	}

	public PdcPaymentModel(long id) {
		super();
		this.id = id;
	}

	public PdcPaymentModel(long id, String bill_no) {
		super();
		this.id = id;
		this.bill_no = bill_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "bill_no", columnDefinition="varchar(100) default ''", nullable=false)
	private String bill_no;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "office_id")
	private long office_id;
	
	@Column(name = "transaction_id")
	private long transactionId;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "memo")
	private String memo;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "pdc_payment_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<PdcPaymentDetailsModel> pdc_payment_list = new ArrayList<PdcPaymentDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
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

	public List<PdcPaymentDetailsModel> getPdc_payment_list() {
		return pdc_payment_list;
	}

	public void setPdc_payment_list(List<PdcPaymentDetailsModel> pdc_payment_list) {
		this.pdc_payment_list = pdc_payment_list;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	
}
