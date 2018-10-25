package com.inventory.sales.model;

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
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         Jun 22, 2013
 */

/**
 * @author sangeeth
 * @date 04-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_GRV_SALES)
public class GrvSalesModel implements Serializable {

	public GrvSalesModel() {
		super();
	}

	public GrvSalesModel(long id, String sales_number) {
		super();
		this.id = id;
		this.setSales_number(sales_number);
	}
	

	public GrvSalesModel(LedgerModel customer, double payment_amount) {
		super();
		this.customer = customer;
		this.payment_amount = payment_amount;
	}
	
	public GrvSalesModel(String sales_number, double payment_amount, double amount) {
		super();
		this.sales_number = sales_number;
		this.payment_amount = payment_amount;
		this.amount = amount;
	}
	
	public GrvSalesModel(double payment_amount, double amount,double paid_by_payment) {
		super();
		this.payment_amount = payment_amount;
		this.amount = amount;
		this.paid_by_payment=paid_by_payment;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "sales_number")
	private String sales_number;
	
	@Column(name = "ref_no", length = 50)
	private String ref_no;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "customer_id")
	private LedgerModel customer;

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
	
	@Column(name = "amount")
	private double amount;
	
	@OneToOne
	@JoinColumn(name = "net_currency_id")
	private CurrencyModel netCurrencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "payment_credit" ,columnDefinition ="bigint default 2", nullable=false)
	private long payment_credit;
	
	@Column(name = "cash_cheque" ,columnDefinition ="bigint default 1", nullable=false)
	private long cash_cheque;
	
	@Column(name = "sales_account" ,columnDefinition ="bigint default 0", nullable=false)
	private long sales_account;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "discount_type", columnDefinition = "int default 1", nullable = false)
	private int discount_type;
	
	@Column(name = "discount_percentage" ,columnDefinition ="double default 0", nullable=false)
	private double discountPercentage;
	
	@Column(name = "discount_amount" ,columnDefinition ="double default 0", nullable=false)
	private double discountAmount;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "grv_sales_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<GrvSalesInventoryDetailsModel> inventory_details_list = new ArrayList<GrvSalesInventoryDetailsModel>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "grv_sales_expense_link", joinColumns = { @JoinColumn(name = "sales_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<GrvSalesExpenseDetailsModel> sales_expense_list = new ArrayList<GrvSalesExpenseDetailsModel>();

	@Column(name = "transaction_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long transaction_id;
	
	@Column(name = "payment_amount")
	private double payment_amount;
	
	@Column(name = "payment_conversion_rate" ,columnDefinition ="double default 0", nullable=false)
	private double paymentConversionRate;
	
	@Column(name = "paid_by_payment", columnDefinition = "double default 0", nullable = false)
	private double paid_by_payment;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currency_id;
	
	@Column(name = "status" ,columnDefinition ="int default 1", nullable=false)
	private int status;

	@Column(name = "payment_done" ,columnDefinition ="char default 'N'", nullable=false)
	private char payment_done;
	
	@Column(name = "sales_type", columnDefinition = "bigint default 0", nullable = false)
	private long sales_type;
	
	@Column(name = "lock_count" ,columnDefinition ="double default 0", nullable=false)
	private double lock_count;
	
	@Column(name = "credit_note",columnDefinition ="double default 0", nullable=false)
	private double credit_note;
	
	@Column(name = "debit_note",columnDefinition ="double default 0", nullable=false)
	private double debit_note;
	
	@Column(name = "chequeNo", length = 50)
	private String chequeNo;

	@Column(name = "chequeDate")
	private Date chequeDate;

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

	public String getSales_number() {
		return sales_number;
	}

	public void setSales_number(String sales_number) {
		this.sales_number = sales_number;
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

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
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

	public CurrencyModel getNetCurrencyId() {
		return netCurrencyId;
	}

	public void setNetCurrencyId(CurrencyModel netCurrencyId) {
		this.netCurrencyId = netCurrencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
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

	public long getSales_account() {
		return sales_account;
	}

	public void setSales_account(long sales_account) {
		this.sales_account = sales_account;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getDiscount_type() {
		return discount_type;
	}

	public void setDiscount_type(int discount_type) {
		this.discount_type = discount_type;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public List<GrvSalesInventoryDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<GrvSalesInventoryDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

	public List<GrvSalesExpenseDetailsModel> getSales_expense_list() {
		return sales_expense_list;
	}

	public void setSales_expense_list(
			List<GrvSalesExpenseDetailsModel> sales_expense_list) {
		this.sales_expense_list = sales_expense_list;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public double getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
	}

	public double getPaymentConversionRate() {
		return paymentConversionRate;
	}

	public void setPaymentConversionRate(double paymentConversionRate) {
		this.paymentConversionRate = paymentConversionRate;
	}

	public double getPaid_by_payment() {
		return paid_by_payment;
	}

	public void setPaid_by_payment(double paid_by_payment) {
		this.paid_by_payment = paid_by_payment;
	}

	public long getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(long currency_id) {
		this.currency_id = currency_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public double getLock_count() {
		return lock_count;
	}

	public void setLock_count(double lock_count) {
		this.lock_count = lock_count;
	}

	public double getCredit_note() {
		return credit_note;
	}

	public void setCredit_note(double credit_note) {
		this.credit_note = credit_note;
	}

	public double getDebit_note() {
		return debit_note;
	}

	public void setDebit_note(double debit_note) {
		this.debit_note = debit_note;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
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
