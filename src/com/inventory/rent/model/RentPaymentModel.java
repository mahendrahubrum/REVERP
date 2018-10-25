package com.inventory.rent.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 26, 2014
 */

@Entity
@Table(name = SConstants.tb_names.RENT_PAYMENT)
public class RentPaymentModel extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3565629639320956296L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "payment_amount")
	private double payment_amount;
	
	@Column(name = "amount")
	private double amount;
	
	@Column(name = "balance")
	private double balance;
	
	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;

	@Column(name = "transaction_id")
	private long transaction_id;
	
	@Column(name = "rent_number")
	private long rent_number;

	@Column(name = "from_account_id")
	private long from_account_id;

	@Column(name = "to_account_id")
	private long to_account_id;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "responsible_person")
	private long responsible_person;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public double getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
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
	
	

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
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

	public long getFrom_account_id() {
		return from_account_id;
	}

	public void setFrom_account_id(long from_account_id) {
		this.from_account_id = from_account_id;
	}

	public long getTo_account_id() {
		return to_account_id;
	}

	public void setTo_account_id(long to_account_id) {
		this.to_account_id = to_account_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

	public long getResponsible_person() {
		return responsible_person;
	}

	public void setResponsible_person(long responsible_person) {
		this.responsible_person = responsible_person;
	}

	public RentPaymentModel() {
		super();
	}

	public RentPaymentModel(long id, LedgerModel customer,
			double payment_amount, double amount, double balance,
			char payment_done, S_OfficeModel office, CurrencyModel currency,
			long transaction_id, long rent_number, long from_account_id,
			long to_account_id, Date date) {
		super();
		this.id = id;
		this.customer = customer;
		this.payment_amount = payment_amount;
		this.amount = amount;
		this.balance = balance;
		this.payment_done = payment_done;
		this.office = office;
		this.currency = currency;
		this.transaction_id = transaction_id;
		this.rent_number = rent_number;
		this.from_account_id = from_account_id;
		this.to_account_id = to_account_id;
		this.date = date;
	}

	public long getRent_number() {
		return rent_number;
	}

	public void setRent_number(long rent_number) {
		this.rent_number = rent_number;
	}

	public RentPaymentModel(long responsible_person) {
		super();
		this.responsible_person = responsible_person;
	}

	public RentPaymentModel(double payment_amount, double amount,
			double balance, long rent_number) {
		super();
		this.payment_amount = payment_amount;
		this.amount = amount;
		this.balance = balance;
		this.rent_number = rent_number;
	}

	public RentPaymentModel(double payment_amount, double amount,
			double balance, Date date, long rent_number) {
		super();
		this.payment_amount = payment_amount;
		this.amount = amount;
		this.balance = balance;
		this.date = date;
		this.rent_number = rent_number;
		
	}

	



	
	
	

	

}
