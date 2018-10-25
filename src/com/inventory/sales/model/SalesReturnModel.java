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
import com.inventory.config.tax.model.TaxModel;
import com.inventory.finance.model.PaymentModeModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P.
 * 
 *         Jul 1, 2013
 */

/**
 * 
 * @author sangeeth
 * @date 22-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALES_RETURN)
public class SalesReturnModel implements Serializable {

	public SalesReturnModel() {
		super();
		this.active=true;
	}

	public SalesReturnModel(long id, String return_no) {
		super();
		this.id = id;
		this.return_no = return_no;
	}

	public SalesReturnModel(long id) {
		super();
		this.id = id;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "return_no")
	private String return_no;
	
	@Column(name = "ref_no")
	private String ref_no;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "responsible_employee")
	private long responsible_employee;
	
	@Column(name = "comments")
	private String comments;
	
	@Column(name = "expenseAmount" ,columnDefinition ="double default 0", nullable=false)
	private double expenseAmount;
	
	@Column(name = "expenseCreditAmount" ,columnDefinition ="double default 0", nullable=false)
	private double expenseCreditAmount;
	
	@OneToOne
	@JoinColumn(name = "net_currency_id")
	private CurrencyModel netCurrencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "sales_account" ,columnDefinition ="bigint default 0", nullable=false)
	private long sales_account;
	
	@Column(name = "paid_by_payment", columnDefinition = "double default 0", nullable = false)
	private double paid_by_payment;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currency_id;
	
	@Column(name = "payment_done" ,columnDefinition ="char default 'N'", nullable=false)
	private char payment_done;
	
	@Column(name = "sales_numbers", length = 500)
	private String sales_numbers;
	
	@Column(name = "discount",columnDefinition ="double default 0", nullable=false)
	private double discount;
	
	@OneToOne
	@JoinColumn(name = "tax_id")
	private TaxModel tax;
	
	@Column(name = "taxPercentage",columnDefinition ="double default 0", nullable=false)
	private double taxPercentage;
	
	@Column(name = "taxAmount",columnDefinition ="double default 0", nullable=false)
	private double taxAmount;
	
	@Column(name = "cessAmount",columnDefinition ="double default 0", nullable=false)
	private double cessAmount;

	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "amount")
	private double amount;

	@Column(name = "status")
	private long status;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@Column(name = "transaction_id")
	private long transaction_id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "sales_return_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<SalesReturnInventoryDetailsModel> inventory_details_list = new ArrayList<SalesReturnInventoryDetailsModel>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "sales_return_expense_link", joinColumns = { @JoinColumn(name = "sales_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<SalesReturnExpenseDetailsModel> sales_expense_list = new ArrayList<SalesReturnExpenseDetailsModel>();

	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long department_id;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long division_id;
	
	@OneToOne
	@JoinColumn(name = "payment_mode")
	private PaymentModeModel paymentMode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReturn_no() {
		return return_no;
	}

	public void setReturn_no(String return_no) {
		this.return_no = return_no;
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

	public long getResponsible_employee() {
		return responsible_employee;
	}

	public void setResponsible_employee(long responsible_employee) {
		this.responsible_employee = responsible_employee;
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

	public long getSales_account() {
		return sales_account;
	}

	public void setSales_account(long sales_account) {
		this.sales_account = sales_account;
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

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}

	public String getSales_numbers() {
		return sales_numbers;
	}

	public void setSales_numbers(String sales_numbers) {
		this.sales_numbers = sales_numbers;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public List<SalesReturnInventoryDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<SalesReturnInventoryDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

	public List<SalesReturnExpenseDetailsModel> getSales_expense_list() {
		return sales_expense_list;
	}

	public void setSales_expense_list(
			List<SalesReturnExpenseDetailsModel> sales_expense_list) {
		this.sales_expense_list = sales_expense_list;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public TaxModel getTax() {
		return tax;
	}

	public void setTax(TaxModel tax) {
		this.tax = tax;
	}

	public double getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(double taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public double getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(double cessAmount) {
		this.cessAmount = cessAmount;
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

	public PaymentModeModel getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentModeModel paymentMode) {
		this.paymentMode = paymentMode;
	}
	
}
