package com.inventory.purchase.model;

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

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         Jun 22, 2013
 */

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PROFORMA_PURCHASE)
public class ProformaPurchaseModel implements Serializable {

	public ProformaPurchaseModel() {
		super();
	}

	public ProformaPurchaseModel(long id, String proforma_no) {
		super();
		this.id = id;
		this.proforma_no = proforma_no;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "proforma_no", length = 50)
	private String proforma_no;
	
	@Column(name = "ref_no", length = 50)
	private String ref_no;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "supplier_id")
	private LedgerModel supplier;

	@Column(name = "responsible_employee")
	private long responsible_employee;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "comments", length = 400)
	private String comments;
	
	@Column(name = "expenseAmount" ,columnDefinition ="double default 0", nullable=false)
	private double expenseAmount;
	
	@Column(name = "expenseCreditAmount" ,columnDefinition ="double default 0", nullable=false)
	private double expenseCreditAmount;
	
	@Column(name = "amount" ,columnDefinition ="double default 0", nullable=false)
	private double amount;
	
	@Column(name = "net_currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long netCurrencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "paymentAmount" ,columnDefinition ="double default 0", nullable=false)
	private double paymentAmount;
	
	@Column(name = "payment_credit" ,columnDefinition ="bigint default 2", nullable=false)
	private long payment_credit;
	
	@Column(name = "cash_cheque" ,columnDefinition ="bigint default 1", nullable=false)
	private long cash_cheque;
	
	@Column(name = "purchase_account" ,columnDefinition ="bigint default 0", nullable=false)
	private long purchase_account;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currency_id;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "lock_count" ,columnDefinition ="double default 0", nullable=false)
	private double lock_count;
	
	@Column(name = "payment_conversion_rate" ,columnDefinition ="double default 0", nullable=false)
	private double paymentConversionRate;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "proforma_purchase_inventory_details_link", joinColumns = { @JoinColumn(name = "proforma_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<ProformaPurchaseInventoryDetailsModel> proforma_purchase_details_list = new ArrayList<ProformaPurchaseInventoryDetailsModel>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "proforma_urchase_expense_link", joinColumns = { @JoinColumn(name = "proforma_id") }, inverseJoinColumns = { @JoinColumn(name = "expense_id") })
	private List<ProformaPurchaseExpenseDetailsModel> proforma_purchase_expense_list = new ArrayList<ProformaPurchaseExpenseDetailsModel>();

	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long department_id;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long division_id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProforma_no() {
		return proforma_no;
	}

	public void setProforma_no(String proforma_no) {
		this.proforma_no = proforma_no;
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

	public LedgerModel getSupplier() {
		return supplier;
	}

	public void setSupplier(LedgerModel supplier) {
		this.supplier = supplier;
	}

	public long getResponsible_employee() {
		return responsible_employee;
	}

	public void setResponsible_employee(long responsible_employee) {
		this.responsible_employee = responsible_employee;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(double expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

	public double getExpenseCreditAmount() {
		return expenseCreditAmount;
	}

	public void setExpenseCreditAmount(double expenseCreditAmount) {
		this.expenseCreditAmount = expenseCreditAmount;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public long getPayment_credit() {
		return payment_credit;
	}

	public void setPayment_credit(long payment_credit) {
		this.payment_credit = payment_credit;
	}

	public long getCash_cheque() {
		return cash_cheque;
	}

	public void setCash_cheque(long cash_cheque) {
		this.cash_cheque = cash_cheque;
	}

	public long getPurchase_account() {
		return purchase_account;
	}

	public void setPurchase_account(long purchase_account) {
		this.purchase_account = purchase_account;
	}

	public long getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(long currency_id) {
		this.currency_id = currency_id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public double getLock_count() {
		return lock_count;
	}

	public void setLock_count(double lock_count) {
		this.lock_count = lock_count;
	}

	public List<ProformaPurchaseInventoryDetailsModel> getProforma_purchase_details_list() {
		return proforma_purchase_details_list;
	}

	public void setProforma_purchase_details_list(
			List<ProformaPurchaseInventoryDetailsModel> proforma_purchase_details_list) {
		this.proforma_purchase_details_list = proforma_purchase_details_list;
	}

	public List<ProformaPurchaseExpenseDetailsModel> getProforma_purchase_expense_list() {
		return proforma_purchase_expense_list;
	}

	public void setProforma_purchase_expense_list(
			List<ProformaPurchaseExpenseDetailsModel> proforma_purchase_expense_list) {
		this.proforma_purchase_expense_list = proforma_purchase_expense_list;
	}

	public long getNetCurrencyId() {
		return netCurrencyId;
	}

	public void setNetCurrencyId(long netCurrencyId) {
		this.netCurrencyId = netCurrencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public double getPaymentConversionRate() {
		return paymentConversionRate;
	}

	public void setPaymentConversionRate(double paymentConversionRate) {
		this.paymentConversionRate = paymentConversionRate;
	}
	
	public long getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(long department_id) {
		this.department_id = department_id;
	}

	public long getDivision_id() {
		return division_id;
	}

	public void setDivision_id(long division_id) {
		this.division_id = division_id;
	}

	
}
