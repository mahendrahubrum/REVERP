package com.inventory.commissionsales.model;

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
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 4, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_COMMISSION_PURCHASE)
public class CommissionPurchaseModel implements Serializable {

	private static final long serialVersionUID = 7944661068215789554L;

	public CommissionPurchaseModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "number")
	private long number;
	
	@Column(name = "login")
	private long login;

	@OneToOne
	@JoinColumn(name = "supplier_id")
	private LedgerModel supplier;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "received_date")
	private Date received_date;

	@Column(name = "issue_date")
	private Date issue_date;

	@Column(name = "vesel", length = 100)
	private String vesel;

	@Column(name = "contr_no", length = 100)
	private String contr_no;

	@Column(name = "consignment_mark", length = 100)
	private String consignment_mark;

	@Column(name = "quantity", length = 100)
	private String quantity;

	@Column(name = "ss_cc", length = 100)
	private String ss_cc;

	@Column(name = "packages", length = 100)
	private String packages;

	@Column(name = "quality", length = 100)
	private String quality;

	@Column(name = "received_sound", length = 100)
	private String received_sound;

	@Column(name = "damage", length = 100)
	private String damage;

	@Column(name = "empty", length = 100)
	private String empty;

	@Column(name = "shorte", length = 100)
	private String shorte;

	@Column(name = "status")
	private long status;
	
	@Column(name = "amount")
	private double amount;
	
	@Column(name = "payment_amount")
	private double payment_amount;
	
	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "excise_duty")
	private double excise_duty;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "commission_purchase_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<CommissionPurchaseDetailsModel> commission_purchase_list = new ArrayList<CommissionPurchaseDetailsModel>();

	@Column(name = "comments")
	private String comments;

	public CommissionPurchaseModel() {
		super();
	}

	public CommissionPurchaseModel(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public LedgerModel getSupplier() {
		return supplier;
	}

	public void setSupplier(LedgerModel supplier) {
		this.supplier = supplier;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public Date getReceived_date() {
		return received_date;
	}

	public void setReceived_date(Date received_date) {
		this.received_date = received_date;
	}

	public Date getIssue_date() {
		return issue_date;
	}

	public void setIssue_date(Date issue_date) {
		this.issue_date = issue_date;
	}

	public String getVesel() {
		return vesel;
	}

	public void setVesel(String vesel) {
		this.vesel = vesel;
	}

	public String getContr_no() {
		return contr_no;
	}

	public void setContr_no(String contr_no) {
		this.contr_no = contr_no;
	}

	public String getConsignment_mark() {
		return consignment_mark;
	}

	public void setConsignment_mark(String consignment_mark) {
		this.consignment_mark = consignment_mark;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getSs_cc() {
		return ss_cc;
	}

	public void setSs_cc(String ss_cc) {
		this.ss_cc = ss_cc;
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getReceived_sound() {
		return received_sound;
	}

	public void setReceived_sound(String received_sound) {
		this.received_sound = received_sound;
	}

	public String getDamage() {
		return damage;
	}

	public void setDamage(String damage) {
		this.damage = damage;
	}

	public String getEmpty() {
		return empty;
	}

	public void setEmpty(String empty) {
		this.empty = empty;
	}

	public String getShorte() {
		return shorte;
	}

	public void setShorte(String shorte) {
		this.shorte = shorte;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}

	public List<CommissionPurchaseDetailsModel> getCommission_purchase_list() {
		return commission_purchase_list;
	}

	public void setCommission_purchase_list(
			List<CommissionPurchaseDetailsModel> commission_purchase_list) {
		this.commission_purchase_list = commission_purchase_list;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getLogin() {
		return login;
	}

	public void setLogin(long login) {
		this.login = login;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
	
}
