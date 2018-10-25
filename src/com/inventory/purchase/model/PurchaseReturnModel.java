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
 * @author Anil K P.
 * 
 *         Jun 21, 2013
 */
@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PURCHASE_RETURN)
public class PurchaseReturnModel implements Serializable {

	public PurchaseReturnModel() {
		super();
	}

	public PurchaseReturnModel(long id, String return_no) {
		super();
		this.id = id;
		this.return_no = return_no;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "return_no", length = 50)
	private String return_no;
	
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
	
	@Column(name = "purchase_account" ,columnDefinition ="bigint default 0", nullable=false)
	private long purchase_account;
	
	@Column(name = "transaction_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long transaction_id;
	
	@Column(name = "amount" ,columnDefinition ="double default 0", nullable=false)
	private double amount;
	
	@OneToOne
	@JoinColumn(name = "net_currency_id")
	private CurrencyModel netCurrencyId;
	
	@Column(name = "expenseAmount" ,columnDefinition ="double default 0", nullable=false)
	private double expenseAmount;
	
	@Column(name = "expenseCreditAmount" ,columnDefinition ="double default 0", nullable=false)
	private double expenseCreditAmount;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "paid_by_payment" ,columnDefinition ="double default 0", nullable=false)
	private double paid_by_payment;
	
	@Column(name = "payment_status" ,columnDefinition ="int default 1", nullable=false)
	private int payment_status;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "lock_count" ,columnDefinition ="double default 0", nullable=false)
	private double lock_count;
	
	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "purchase_return_expense_link", joinColumns = { @JoinColumn(name = "purchase_id") }, inverseJoinColumns = { @JoinColumn(name = "expense_id") })
	private List<PurchaseReturnExpenseDetailsModel> purchase_return_expense_list = new ArrayList<PurchaseReturnExpenseDetailsModel>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "purchase_return_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<PurchaseReturnInventoryDetailsModel> inventory_details_list = new ArrayList<PurchaseReturnInventoryDetailsModel>();

	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long department_id;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long division_id;
	
	@OneToOne
	@JoinColumn(name = "payment_mode")
	private PaymentModeModel paymentMode;
	
	
	@Column(name = "discount_type", columnDefinition = "int default 1", nullable = false)
	private int discount_type;
	
	@Column(name = "discount_percentage" ,columnDefinition ="double default 0", nullable=false)
	private double discountPercentage;
	
	@Column(name = "discount_amount" ,columnDefinition ="double default 0", nullable=false)
	private double discountAmount;
	
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

	public long getPurchase_account() {
		return purchase_account;
	}

	public void setPurchase_account(long purchase_account) {
		this.purchase_account = purchase_account;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
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

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public double getPaid_by_payment() {
		return paid_by_payment;
	}

	public void setPaid_by_payment(double paid_by_payment) {
		this.paid_by_payment = paid_by_payment;
	}

	public int getPayment_status() {
		return payment_status;
	}

	public void setPayment_status(int payment_status) {
		this.payment_status = payment_status;
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

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}

	public List<PurchaseReturnExpenseDetailsModel> getPurchase_return_expense_list() {
		return purchase_return_expense_list;
	}

	public void setPurchase_return_expense_list(
			List<PurchaseReturnExpenseDetailsModel> purchase_return_expense_list) {
		this.purchase_return_expense_list = purchase_return_expense_list;
	}

	public List<PurchaseReturnInventoryDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<PurchaseReturnInventoryDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
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
