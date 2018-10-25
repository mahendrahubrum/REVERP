package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.PaymentTermsModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 15, 2013
 */

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_CUSTOMER)
public class CustomerModel implements Serializable {
	public CustomerModel() {

	}
	
	public CustomerModel(long id) {
		super();
		this.id = id;
	}

	public CustomerModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "customer_code", length = 200)
	private String customer_code;
	
	@Column(name = "status")
	private long status;
	
	@Column(name = "responsible_person")
	private long responsible_person;
	
	@OneToOne
	@JoinColumn(name = "customer_currency")
	private CurrencyModel customer_currency;
	
	@Column(name = "sales_type")
	private long sales_type;
	
	@OneToOne
	@JoinColumn(name = "payment_terms")
	private PaymentTermsModel payment_terms;
	
	@Column(name = "credit_limit")
	private double credit_limit;

	@Column(name = "max_credit_period")
	private int max_credit_period;
	
	@Column(name = "description", length = 500)
	private String description;
	
	@Column(name="subscription", columnDefinition = "bigint default 0", nullable = false)
	private long subscription;
	
	@Column(name = "loginEnabled",columnDefinition="boolean default false", nullable=false)
	private boolean loginEnabled;
	
	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;
	
	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;

	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;
	
	@Column(name="customer_group", columnDefinition = "bigint default 0", nullable = false)
	private long customerGroupId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCustomer_code() {
		return customer_code;
	}

	public void setCustomer_code(String customer_code) {
		this.customer_code = customer_code;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getResponsible_person() {
		return responsible_person;
	}

	public void setResponsible_person(long responsible_person) {
		this.responsible_person = responsible_person;
	}

	public CurrencyModel getCustomer_currency() {
		return customer_currency;
	}

	public void setCustomer_currency(CurrencyModel customer_currency) {
		this.customer_currency = customer_currency;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public PaymentTermsModel getPayment_terms() {
		return payment_terms;
	}

	public void setPayment_terms(PaymentTermsModel payment_terms) {
		this.payment_terms = payment_terms;
	}

	public double getCredit_limit() {
		return credit_limit;
	}

	public void setCredit_limit(double credit_limit) {
		this.credit_limit = credit_limit;
	}

	public int getMax_credit_period() {
		return max_credit_period;
	}

	public void setMax_credit_period(int max_credit_period) {
		this.max_credit_period = max_credit_period;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getSubscription() {
		return subscription;
	}

	public void setSubscription(long subscription) {
		this.subscription = subscription;
	}

	public boolean isLoginEnabled() {
		return loginEnabled;
	}

	public void setLoginEnabled(boolean loginEnabled) {
		this.loginEnabled = loginEnabled;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

	public long getCustomerGroupId() {
		return customerGroupId;
	}

	public void setCustomerGroupId(long customerGroupId) {
		this.customerGroupId = customerGroupId;
	}

}