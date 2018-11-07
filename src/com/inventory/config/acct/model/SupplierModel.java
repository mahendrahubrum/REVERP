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
@Table(name = SConstants.tb_names.I_SUPPLIER)
public class SupplierModel implements Serializable {

	public SupplierModel() {

	}

	public SupplierModel(long id) {
		super();
		this.id = id;
	}

	public SupplierModel(long id, String name) {
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

	@Column(name = "supplier_code", length = 200)
	private String supplier_code;

	@Column(name = "bank_name", length = 80)
	private String bank_name;

	@Column(name = "website", length = 50)
	private String website;

	@Column(name = "responsible_person")
	private long responsible_person;

	@Column(name = "contact_person", length = 100)
	private String contact_person;

	@Column(name = "contact_person_fax", length = 100)
	private String contact_person_fax;

	@Column(name = "contact_person_email", length = 100)
	private String contact_person_email;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "credit_limit")
	private double credit_limit;

	@Column(name = "credit_period")
	private int credit_period;

	@OneToOne
	@JoinColumn(name = "payment_terms")
	private PaymentTermsModel payment_terms;

	@OneToOne
	@JoinColumn(name = "supplier_currency")
	private CurrencyModel supplier_currency;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@Column(name = "subscription", columnDefinition = "bigint default 0", nullable = false)
	private long subscription;

	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;

	@Column(name = "loginEnabled", columnDefinition = "boolean default false", nullable = false)
	private boolean loginEnabled;

	@Column(name = "vat_number", length = 500)
	private String vatNumber;

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

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

	public String getSupplier_code() {
		return supplier_code;
	}

	public void setSupplier_code(String supplier_code) {
		this.supplier_code = supplier_code;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public long getResponsible_person() {
		return responsible_person;
	}

	public void setResponsible_person(long responsible_person) {
		this.responsible_person = responsible_person;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public String getContact_person_fax() {
		return contact_person_fax;
	}

	public void setContact_person_fax(String contact_person_fax) {
		this.contact_person_fax = contact_person_fax;
	}

	public String getContact_person_email() {
		return contact_person_email;
	}

	public void setContact_person_email(String contact_person_email) {
		this.contact_person_email = contact_person_email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getCredit_limit() {
		return credit_limit;
	}

	public void setCredit_limit(double credit_limit) {
		this.credit_limit = credit_limit;
	}

	public int getCredit_period() {
		return credit_period;
	}

	public void setCredit_period(int credit_period) {
		this.credit_period = credit_period;
	}

	public PaymentTermsModel getPayment_terms() {
		return payment_terms;
	}

	public void setPayment_terms(PaymentTermsModel payment_terms) {
		this.payment_terms = payment_terms;
	}

	public CurrencyModel getSupplier_currency() {
		return supplier_currency;
	}

	public void setSupplier_currency(CurrencyModel supplier_currency) {
		this.supplier_currency = supplier_currency;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public long getSubscription() {
		return subscription;
	}

	public void setSubscription(long subscription) {
		this.subscription = subscription;
	}

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

	public boolean isLoginEnabled() {
		return loginEnabled;
	}

	public void setLoginEnabled(boolean loginEnabled) {
		this.loginEnabled = loginEnabled;
	}

}