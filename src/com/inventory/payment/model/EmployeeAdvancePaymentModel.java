package com.inventory.payment.model;

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
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 22, 2013
 */

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 25, 2015
 */

@Entity
@Table(name = SConstants.tb_names.I_EMPLOYEE_ADVANCE_PAYMENT)
public class EmployeeAdvancePaymentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3373190638667416012L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "login_id")
	private long login_id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@OneToOne
	@JoinColumn(name = "user")
	private UserModel user;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;

	@Column(name = "transaction_id")
	private long transaction_id;
	
	@Column(name = "conversion_rate")
	private double conversionRate;

	@Column(name = "payment_id")
	private long payment_id;

	@Column(name = "account_id")
	private long account_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "description", length = 400)
	private String description;
	
	@Column(name = "cheque_date")
	private Date chequeDate;
	
	@Column(name = "cheque_no", length = 50)
	private String chequeNo;

	@Column(name = "type")
	private int type;

	@Column(name = "amount")
	private double amount;

//	@Column(name = "discount")
//	private double discount;

	@Column(name = "cash_or_check", columnDefinition = "int default 1", nullable = false)
	private int cash_or_check;

//	@Column(name = "payment_amount")
//	private double payment_amount;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "salary_id", columnDefinition = "bigint default 0", nullable = false)
	private long salary_id;

	public EmployeeAdvancePaymentModel() {
		super();
		this.active=true;
	}

	public EmployeeAdvancePaymentModel(long id, String description) {
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

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(long account_id) {
		this.account_id = account_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

//	public double getDiscount() {
//		return discount;
//	}
//
//	public void setDiscount(double discount) {
//		this.discount = discount;
//	}
//
//	public double getPayment_amount() {
//		return payment_amount;
//	}
//
//	public void setPayment_amount(double payment_amount) {
//		this.payment_amount = payment_amount;
//	}

	public long getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(long payment_id) {
		this.payment_id = payment_id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
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

	public long getSalary_id() {
		return salary_id;
	}

	public void setSalary_id(long salary_id) {
		this.salary_id = salary_id;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	
}
