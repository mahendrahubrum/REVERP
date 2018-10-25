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

import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 3, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_PAYMENT_DEPOSIT)
public class PaymentDepositModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2841579076377740806L;

	public PaymentDepositModel() {
		super();
		this.active = true;
	}

	public PaymentDepositModel(long id) {
		super();
		this.id = id;
	}

	public PaymentDepositModel(long id, String ref_no) {
		super();
		this.id = id;
		this.ref_no = ref_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "transaction_id", nullable = true)
	private TransactionModel transaction;

	@Column(name = "date")
	private Date date;

	@Column(name = "ref_no")
	private String ref_no;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "status")
	private long status;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@Column(name = "cash_or_check", columnDefinition = "int default 1", nullable = false)
	private int cash_or_check;

	@Column(name = "type")
	private long type;

	@Column(name = "memo")
	private String memo;

	@Column(name = "bill_no")
	private long bill_no;
	
	@Column(name = "subscription", columnDefinition = "bigint default 0", nullable = false)
	private long subscription;

	public long getSubscription() {
		return subscription;
	}

	public void setSubscription(long subscription) {
		this.subscription = subscription;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TransactionModel getTransaction() {
		return transaction;
	}

	public void setTransaction(TransactionModel transaction) {
		this.transaction = transaction;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public long getBill_no() {
		return bill_no;
	}

	public void setBill_no(long bill_no) {
		this.bill_no = bill_no;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCash_or_check() {
		return cash_or_check;
	}

	public void setCash_or_check(int cash_or_check) {
		this.cash_or_check = cash_or_check;
	}

}
