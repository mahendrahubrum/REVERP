package com.inventory.commissionsales.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 14, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_COMMISSION_SALES)
public class CommissionSalesModel implements Serializable {

	private static final long serialVersionUID = -7162842819556373707L;

	public CommissionSalesModel(long id, String contr_no) {
		super();
		this.id = id;
		this.contr_no = contr_no;
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

	@Column(name = "gross_sale")
	private double gross_sale;

	@Column(name = "less_expense")
	private double less_expense;

	@Column(name = "net_sale")
	private double net_sale;

	@Column(name = "freight")
	private double freight;

	@Column(name = "airport_charges")
	private double airport_charges;

	@Column(name = "waste")
	private double waste;

	@Column(name = "dpa_charges")
	private double dpa_charges;

	@Column(name = "pickup_charge")
	private double pickup_charge;

	@Column(name = "unloading_charge")
	private double unloading_charge;

	@Column(name = "storage_charge")
	private double storage_charge;

	@Column(name = "port")
	private double port;

	@Column(name = "auction")
	private double auction;

	@Column(name = "commission")
	private double commission;

	@Column(name = "details", length = 500)
	private String details;

	@Column(name = "status")
	private long status;

	public CommissionSalesModel() {
		super();
	}

	public CommissionSalesModel(long id) {
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

	public double getGross_sale() {
		return gross_sale;
	}

	public void setGross_sale(double gross_sale) {
		this.gross_sale = gross_sale;
	}

	public double getLess_expense() {
		return less_expense;
	}

	public void setLess_expense(double less_expense) {
		this.less_expense = less_expense;
	}

	public double getNet_sale() {
		return net_sale;
	}

	public void setNet_sale(double net_sale) {
		this.net_sale = net_sale;
	}

	public double getFreight() {
		return freight;
	}

	public void setFreight(double freight) {
		this.freight = freight;
	}

	public double getAirport_charges() {
		return airport_charges;
	}

	public void setAirport_charges(double airport_charges) {
		this.airport_charges = airport_charges;
	}

	public double getWaste() {
		return waste;
	}

	public void setWaste(double waste) {
		this.waste = waste;
	}

	public double getDpa_charges() {
		return dpa_charges;
	}

	public void setDpa_charges(double dpa_charges) {
		this.dpa_charges = dpa_charges;
	}

	public double getPickup_charge() {
		return pickup_charge;
	}

	public void setPickup_charge(double pickup_charge) {
		this.pickup_charge = pickup_charge;
	}

	public double getUnloading_charge() {
		return unloading_charge;
	}

	public void setUnloading_charge(double unloading_charge) {
		this.unloading_charge = unloading_charge;
	}

	public double getStorage_charge() {
		return storage_charge;
	}

	public void setStorage_charge(double storage_charge) {
		this.storage_charge = storage_charge;
	}

	public double getPort() {
		return port;
	}

	public void setPort(double port) {
		this.port = port;
	}

	public double getAuction() {
		return auction;
	}

	public void setAuction(double auction) {
		this.auction = auction;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

}
