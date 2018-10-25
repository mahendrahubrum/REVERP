package com.inventory.sales.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
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
import com.inventory.tailoring.model.ProductionUnitModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TAILORING_SALES)
public class TailoringSalesModel implements Serializable {

	private static final long serialVersionUID = -1203620826390294494L;

	public TailoringSalesModel() {
		super();
		this.setActive(true);
	}

	public TailoringSalesModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	public TailoringSalesModel(LedgerModel customer, double payment_amount) {
		super();
		this.customer = customer;
		this.payment_amount = payment_amount;
	}
	public TailoringSalesModel(long id, double amount) {
		super();
		this.id = id;
		this.amount = amount;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "sales_number")
	private long sales_number;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "voucher_no")
	private long voucher_no;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "expected_delivery_date")
	private Date expected_delivery_date;
	
	@Column(name = "actual_delivery_date")
	private Date actual_delivery_date;

	@Column(name = "created_time")
	private Timestamp created_time;

	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "excise_duty")
	private double excise_duty;
	
	@Column(name = "discount")
	private double discount;
	
	@Column(name = "charges")
	private double charges;

	@Column(name = "payment_amount")
	private double payment_amount;
	
	@Column(name = "advance_amount")
	private double advance_amount;

	@Column(name = "amount")
	private double amount;

	@Column(name = "status")
	private long status;
	
	@Column(name = "delivery_status")
	private long delivery_status;

	@Column(name = "type", length = 1)
	private int type;

	@Column(name = "sales_person")
	private long sales_person;

	@Column(name = "credit_period")
	private int credit_period;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;
	
	@OneToOne
	@JoinColumn(name = "production_unit")
	private ProductionUnitModel productionUnit;
	
	@Column(name = "material_source")
	private int materialSource;
	
	@Column(name = "paymentMode")
	private int paymentMode;
	
	@Column(name = "paid_by_payment", columnDefinition = "double default 0", nullable = false)
	private double paid_by_payment;

	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;
	
	@Column(name = "payment_account")
	private long paymentAccount;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tailoring_sales_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<TailoringSalesInventoryDetailsModel> tailoring_inventory_details_list = new ArrayList<TailoringSalesInventoryDetailsModel>();

	@Column(name = "comments")
	private String comments;
	
	@Column(name = "barcode")
	private String barcode;

	public TailoringSalesModel(long sales_number, double payment_amount, double amount) {
		super();
		this.sales_number = sales_number;
		this.payment_amount = payment_amount;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getVoucher_no() {
		return voucher_no;
	}

	public void setVoucher_no(long voucher_no) {
		this.voucher_no = voucher_no;
	}

	public long getSales_number() {
		return sales_number;
	}

	public void setSales_number(long sales_number) {
		this.sales_number = sales_number;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getShipping_charge() {
		return shipping_charge;
	}

	public void setShipping_charge(double shipping_charge) {
		this.shipping_charge = shipping_charge;
	}

	public double getExcise_duty() {
		return excise_duty;
	}

	public void setExcise_duty(double excise_duty) {
		this.excise_duty = excise_duty;
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

	public long getSales_person() {
		return sales_person;
	}

	public void setSales_person(long sales_person) {
		this.sales_person = sales_person;
	}

	public int getCredit_period() {
		return credit_period;
	}

	public void setCredit_period(int credit_period) {
		this.credit_period = credit_period;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Timestamp getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Timestamp created_time) {
		this.created_time = created_time;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getCharges() {
		return charges;
	}

	public void setCharges(double charges) {
		this.charges = charges;
	}

	public List<TailoringSalesInventoryDetailsModel> getTailoring_inventory_details_list() {
		return tailoring_inventory_details_list;
	}

	public void setTailoring_inventory_details_list(
			List<TailoringSalesInventoryDetailsModel> tailoring_inventory_details_list) {
		this.tailoring_inventory_details_list = tailoring_inventory_details_list;
	}

	public Date getExpected_delivery_date() {
		return expected_delivery_date;
	}

	public void setExpected_delivery_date(Date expected_delivery_date) {
		this.expected_delivery_date = expected_delivery_date;
	}

	public Date getActual_delivery_date() {
		return actual_delivery_date;
	}

	public void setActual_delivery_date(Date actual_delivery_date) {
		this.actual_delivery_date = actual_delivery_date;
	}

	public double getAdvance_amount() {
		return advance_amount;
	}

	public void setAdvance_amount(double advance_amount) {
		this.advance_amount = advance_amount;
	}

	public long getDelivery_status() {
		return delivery_status;
	}

	public void setDelivery_status(long delivery_status) {
		this.delivery_status = delivery_status;
	}

	public int getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(int paymentMode) {
		this.paymentMode = paymentMode;
	}

	public int getMaterialSource() {
		return materialSource;
	}

	public void setMaterialSource(int materialSource) {
		this.materialSource = materialSource;
	}

	public ProductionUnitModel getProductionUnit() {
		return productionUnit;
	}

	public void setProductionUnit(ProductionUnitModel productionUnit) {
		this.productionUnit = productionUnit;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}

	public double getPaid_by_payment() {
		return paid_by_payment;
	}

	public void setPaid_by_payment(double paid_by_payment) {
		this.paid_by_payment = paid_by_payment;
	}

	public long getPaymentAccount() {
		return paymentAccount;
	}

	public void setPaymentAccount(long paymentAccount) {
		this.paymentAccount = paymentAccount;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}
