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
import com.inventory.finance.model.PaymentModeModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
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
@Table(name = SConstants.tb_names.I_PURCHASE)
public class PurchaseModel implements Serializable {

	public PurchaseModel() {
		super();
	}

	public PurchaseModel(long id, String purchase_no) {
		super();
		this.id = id;
		this.purchase_no = purchase_no;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "purchase_no", length = 50)
	private String purchase_no;
	
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
	
	@Column(name = "discount_type", columnDefinition = "int default 1", nullable = false)
	private int discount_type;
	
	@Column(name = "discount_percentage" ,columnDefinition ="double default 0", nullable=false)
	private double discountPercentage;
	
	@Column(name = "discount_amount" ,columnDefinition ="double default 0", nullable=false)
	private double discountAmount;
	
	@OneToOne
	@JoinColumn(name = "net_currency_id")
	private CurrencyModel netCurrencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "paymentAmount" ,columnDefinition ="double default 0", nullable=false)
	private double paymentAmount;
	
	@Column(name = "paid_by_payment" ,columnDefinition ="double default 0", nullable=false)
	private double paid_by_payment;
	
	@Column(name = "payment_credit" ,columnDefinition ="bigint default 2", nullable=false)
	private long payment_credit;
	
	@Column(name = "cash_cheque" ,columnDefinition ="bigint default 1", nullable=false)
	private long cash_cheque;
	
	@Column(name = "purchase_account" ,columnDefinition ="bigint default 0", nullable=false)
	private long purchase_account;
	
	@Column(name = "transaction_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long transaction_id;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currency_id;
	
	@Column(name = "payment_conversion_rate" ,columnDefinition ="double default 0", nullable=false)
	private double paymentConversionRate;
	
	@Column(name = "payment_status" ,columnDefinition ="int default 1", nullable=false)
	private int payment_status;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "lock_count" ,columnDefinition ="double default 0", nullable=false)
	private double lock_count;
	
	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;
	
	@Column(name = "credit_note",columnDefinition ="double default 0", nullable=false)
	private double credit_note;
	
	@Column(name = "debit_note",columnDefinition ="double default 0", nullable=false)
	private double debit_note;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "purchase_inventory_details_link", joinColumns = { @JoinColumn(name = "purchase_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<PurchaseInventoryDetailsModel> purchase_details_list = new ArrayList<PurchaseInventoryDetailsModel>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "purchase_expense_link", joinColumns = { @JoinColumn(name = "purchase_id") }, inverseJoinColumns = { @JoinColumn(name = "expense_id") })
	private List<PurchaseExpenseDetailsModel> purchase_expense_list = new ArrayList<PurchaseExpenseDetailsModel>();

	@Column(name = "chequeNo", length = 50)
	private String chequeNo;

	@Column(name = "chequeDate")
	private Date chequeDate;
	
	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long department_id;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long division_id;
	
	@OneToOne
	@JoinColumn(name = "payment_mode")
	private PaymentModeModel paymentMode;
	
	
	
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPurchase_no() {
		return purchase_no;
	}

	public void setPurchase_no(String purchase_no) {
		this.purchase_no = purchase_no;
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

	public double getLock_count() {
		return lock_count;
	}

	public void setLock_count(double lock_count) {
		this.lock_count = lock_count;
	}

	public double getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(double expenseAmount) {
		this.expenseAmount = expenseAmount;
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
	
	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public long getPurchase_account() {
		return purchase_account;
	}

	public void setPurchase_account(long purchase_account) {
		this.purchase_account = purchase_account;
	}
	
	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public double getPaid_by_payment() {
		return paid_by_payment;
	}

	public void setPaid_by_payment(double paid_by_payment) {
		this.paid_by_payment = paid_by_payment;
	}
	
	public double getExpenseCreditAmount() {
		return expenseCreditAmount;
	}

	public void setExpenseCreditAmount(double expenseCreditAmount) {
		this.expenseCreditAmount = expenseCreditAmount;
	}

	public long getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(long currency_id) {
		this.currency_id = currency_id;
	}

	public List<PurchaseInventoryDetailsModel> getPurchase_details_list() {
		return purchase_details_list;
	}

	public void setPurchase_details_list(
			List<PurchaseInventoryDetailsModel> purchase_details_list) {
		this.purchase_details_list = purchase_details_list;
	}

	public List<PurchaseExpenseDetailsModel> getPurchase_expense_list() {
		return purchase_expense_list;
	}

	public void setPurchase_expense_list(
			List<PurchaseExpenseDetailsModel> purchase_expense_list) {
		this.purchase_expense_list = purchase_expense_list;
	}

	public int getPayment_status() {
		return payment_status;
	}

	public void setPayment_status(int payment_status) {
		this.payment_status = payment_status;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
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

	public CurrencyModel getNetCurrencyId() {
		return netCurrencyId;
	}

	public void setNetCurrencyId(CurrencyModel netCurrencyId) {
		this.netCurrencyId = netCurrencyId;
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
