package com.inventory.rent.model;

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
import com.inventory.config.tax.model.TaxModel;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         May 2, 2014
 */

@Entity
@Table(name = SConstants.tb_names.RENT_MANAGEMENT)
public class RentDetailsModel implements Serializable {

	private static final long serialVersionUID = 3274463450778916062L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "rent_number")
	private long rent_number;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "month")
	private Date month;

	@Column(name = "created_time")
	private Timestamp created_time;

	@Column(name = "responsible_person")
	private long responsible_person;

	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "payment_amount")
	private double payment_amount;

	@Column(name = "amount")
	private double amount;
	
	@Column(name = "totalpaidamt")
	private double totalpaidamt;

	@Column(name = "status")
	private long status;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@Column(name = "type", length = 1)
	private int type;

	@Column(name = "sales_person")
	private long sales_person;

	@Column(name = "sales_type", columnDefinition = "bigint default 0", nullable = false)
	private long sales_type;

	@Column(name = "credit_period")
	private int credit_period;

	@Column(name = "outpass")
	private String outpass;
	
	@Column(name = "ref_no")
	private long ref_no;
	
	@Column(name = "from_data", columnDefinition = "bigint default 1", nullable = false)
	private long from_data;
	
	@OneToOne
	@JoinColumn(name = "responsible_customer_id")
	private LedgerModel responsible_customer;
	
	@OneToOne
	@JoinColumn(name = "tax_id")
	private TaxModel tax;

	@Column(name = "tax_amount")
	private double tax_amount;

	@Column(name = "tax_percentage")
	private double tax_percentage;

	@Column(name = "cash_credit_sale", columnDefinition = "bigint default 2", nullable = false)
	private long cash_credit_sale;

	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "rent_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<RentInventoryDetailsModel> inventory_details_list = new ArrayList<RentInventoryDetailsModel>();

	@Column(name = "comments")
	private String comments;

	@Column(name = "agreementbox", columnDefinition = "bigint default 1", nullable = false)
	private long agreementbox;
	
	@Column(name = "chequebox", columnDefinition = "bigint default 1", nullable = false)
	private long chequebox;
	
	@Column(name = "idproof", columnDefinition = "bigint default 1", nullable = false)
	private long idproof;
	
	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public RentDetailsModel() {
		super();
	}

	public RentDetailsModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}
	
	

	public RentDetailsModel(long id) {
		super();
		this.id = id;
	}

	public RentDetailsModel(LedgerModel customer, double payment_amount) {
		super();
		this.customer = customer;
		this.payment_amount = payment_amount;
	}

	public RentDetailsModel(long rent_number, double payment_amount,
			double amount) {
		super();
		this.rent_number = rent_number;
		this.payment_amount = payment_amount;
		this.amount = amount;
	}
	
	

	public RentDetailsModel(long id,
			List<RentInventoryDetailsModel> inventory_details_list) {
		super();
		this.id = id;
		this.inventory_details_list = inventory_details_list;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}
	
	

	public RentDetailsModel(long id, long rent_number) {
		super();
		this.id = id;
		this.rent_number = rent_number;
	}

	public String getOutpass() {
		return outpass;
	}

	public void setOutpass(String outpass) {
		this.outpass = outpass;
	}

	public long getFrom_data() {
		return from_data;
	}

	public void setFrom_data(long from_data) {
		this.from_data = from_data;
	}

	public LedgerModel getResponsible_customer() {
		return responsible_customer;
	}

	public void setResponsible_customer(LedgerModel responsible_customer) {
		this.responsible_customer = responsible_customer;
	}

	public TaxModel getTax() {
		return tax;
	}

	public void setTax(TaxModel tax) {
		this.tax = tax;
	}

	public double getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(double tax_amount) {
		this.tax_amount = tax_amount;
	}

	public double getTax_percentage() {
		return tax_percentage;
	}

	public void setTax_percentage(double tax_percentage) {
		this.tax_percentage = tax_percentage;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Timestamp getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Timestamp created_time) {
		this.created_time = created_time;
	}

	public long getResponsible_person() {
		return responsible_person;
	}

	public void setResponsible_person(long responsible_person) {
		this.responsible_person = responsible_person;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public double getShipping_charge() {
		return shipping_charge;
	}

	public void setShipping_charge(double shipping_charge) {
		this.shipping_charge = shipping_charge;
	}

	public double getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getSales_person() {
		return sales_person;
	}

	public void setSales_person(long sales_person) {
		this.sales_person = sales_person;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public int getCredit_period() {
		return credit_period;
	}

	public void setCredit_period(int credit_period) {
		this.credit_period = credit_period;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
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

	public List<RentInventoryDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<RentInventoryDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getRent_number() {
		return rent_number;
	}

	public void setRent_number(long rent_number) {
		this.rent_number = rent_number;
	}

	public long getRef_no() {
		return ref_no;
	}

	public void setRef_no(long ref_no) {
		this.ref_no = ref_no;
	}

	public long getCash_credit_sale() {
		return cash_credit_sale;
	}

	public void setCash_credit_sale(long cash_credit_sale) {
		this.cash_credit_sale = cash_credit_sale;
	}

	

	public long getAgreementbox() {
		return agreementbox;
	}

	public void setAgreementbox(long agreementbox) {
		this.agreementbox = agreementbox;
	}

	public long getChequebox() {
		return chequebox;
	}

	public void setChequebox(long chequebox) {
		this.chequebox = chequebox;
	}

	public long getIdproof() {
		return idproof;
	}

	public void setIdproof(long idproof) {
		this.idproof = idproof;
	}

	public double getTotalpaidamt() {
		return totalpaidamt;
	}

	public void setTotalpaidamt(double totalpaidamt) {
		this.totalpaidamt = totalpaidamt;
	}

	public RentDetailsModel(long id, double shipping_charge,
			double payment_amount, String comments) {
		super();
		this.id = id;
		this.shipping_charge = shipping_charge;
		this.payment_amount = payment_amount;
		this.comments = comments;
	}
	
	
	
	

}
