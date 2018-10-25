package com.inventory.subscription.model;

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

@Entity
@Table(name=SConstants.tb_names.I_SUBSCRIPTION_PAYMENT)
public class SubscriptionPaymentModel implements Serializable{

	private static final long serialVersionUID = 7449013028213078718L;

	public SubscriptionPaymentModel() {
		
	}
	
	public SubscriptionPaymentModel(long id) {
		super();
	}

	public SubscriptionPaymentModel(long id,String name) {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="subscription")
	private SubscriptionInModel subscription;
	
	@Column(name="payment_date")
	private Date payment_date;
	
	@Column(name="subscription_date")
	private Date subscription_date;
	
	@Column(name="cheque_date")
	private Date cheque_date;
	
	@Column(name="return_date",nullable=true)
	private Date return_date;
	
	@Column(name="cash_cheque")
	private int cash_cheque;
	
	@Column(name="from_account")
	private long from_account;
	
	@Column(name="to_account",columnDefinition = "bigint default 0", nullable = false)
	private long to_account;
	
	@Column(name="transaction_id")
	private long transaction_id;
	
	@Column(name="cheque_number")
	private String cheque_number;
	
	@Column(name="amount_due")
	private double amount_due;
	
	@Column(name="amount_paid")
	private double amount_paid;
	
	@Column(name="type",columnDefinition = "bigint default 0", nullable = false)
	private long type;
	
	@Column(name="pay_credit",columnDefinition = "bigint default 0", nullable = false)
	private long pay_credit;
	
	@Column(name="credit_transaction",columnDefinition = "bigint default 0", nullable = false)
	private long credit_transaction;
	
	@Column(name="special_credit_payment",columnDefinition = "bigint default 0", nullable = false)
	private long special_credit_payment;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "subscription_payment_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<SubscriptionPaymentDetailsModel> subscription_payment_list = new ArrayList<SubscriptionPaymentDetailsModel>();
	
	public long getSpecial_credit_payment() {
		return special_credit_payment;
	}

	public void setSpecial_credit_payment(long special_credit_payment) {
		this.special_credit_payment = special_credit_payment;
	}

	public long getCredit_transaction() {
		return credit_transaction;
	}

	public void setCredit_transaction(long credit_transaction) {
		this.credit_transaction = credit_transaction;
	}

	public long getPay_credit() {
		return pay_credit;
	}

	public void setPay_credit(long pay_credit) {
		this.pay_credit = pay_credit;
	}

	public List<SubscriptionPaymentDetailsModel> getSubscription_payment_list() {
		return subscription_payment_list;
	}

	public void setSubscription_payment_list(
			List<SubscriptionPaymentDetailsModel> subscription_payment_list) {
		this.subscription_payment_list = subscription_payment_list;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public long getTo_account() {
		return to_account;
	}

	public void setTo_account(long to_account) {
		this.to_account = to_account;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SubscriptionInModel getSubscription() {
		return subscription;
	}

	public void setSubscription(SubscriptionInModel subscription) {
		this.subscription = subscription;
	}

	public Date getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}

	public Date getSubscription_date() {
		return subscription_date;
	}

	public void setSubscription_date(Date subscription_date) {
		this.subscription_date = subscription_date;
	}

	public Date getCheque_date() {
		return cheque_date;
	}

	public void setCheque_date(Date cheque_date) {
		this.cheque_date = cheque_date;
	}

	public int getCash_cheque() {
		return cash_cheque;
	}

	public void setCash_cheque(int cash_cheque) {
		this.cash_cheque = cash_cheque;
	}

	public long getFrom_account() {
		return from_account;
	}

	public void setFrom_account(long from_account) {
		this.from_account = from_account;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getCheque_number() {
		return cheque_number;
	}

	public void setCheque_number(String cheque_number) {
		this.cheque_number = cheque_number;
	}

	public double getAmount_due() {
		return amount_due;
	}

	public void setAmount_due(double amount_due) {
		this.amount_due = amount_due;
	}

	public double getAmount_paid() {
		return amount_paid;
	}

	public void setAmount_paid(double amount_paid) {
		this.amount_paid = amount_paid;
	}

	public Date getReturn_date() {
		return return_date;
	}

	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}
	
}
