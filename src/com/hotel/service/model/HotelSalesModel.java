package com.hotel.service.model;

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
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

@Entity
@Table(name = SConstants.tb_names.H_HOTEL_SALES)
public class HotelSalesModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7320635836751365720L;

	public HotelSalesModel() {
		super();
	}

	public HotelSalesModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
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

	@Column(name = "table_id",columnDefinition="bigint default 0", nullable=false)
	private long tableId;

	@Column(name = "date")
	private Date date;

	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "excise_duty")
	private double excise_duty;

	@Column(name = "payment_amount")
	private double payment_amount;

	@Column(name = "amount")
	private double amount;

	@Column(name = "discount")
	private double discount;

	@Column(name = "status")
	private long status;

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
	
	@Column(name = "type", length = 1)
	private int type;

	@OneToOne
	@JoinColumn(name = "cash_pay_id", nullable = true)
	private CashPayDetailsModel cash_pay_id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "hotel_sales_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<HotelSalesInventoryDetailsModel> inventory_details_list = new ArrayList<HotelSalesInventoryDetailsModel>();

	@Column(name = "comments")
	private String comments;
	
	@Column(name = "customer")
	private String customer;

	public HotelSalesModel(long sales_number, double payment_amount, double amount) {
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

	public List<HotelSalesInventoryDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<HotelSalesInventoryDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
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

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public CashPayDetailsModel getCash_pay_id() {
		return cash_pay_id;
	}

	public void setCash_pay_id(CashPayDetailsModel cash_pay_id) {
		this.cash_pay_id = cash_pay_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}
}
