package com.inventory.reports.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 14, 2013
 */

public class CommissionSalesReportBean implements Serializable {

	private static final long serialVersionUID = 3984878497421997999L;
	private long id;
	private String office;
	private long number;
	private String supplier;
	private String customer;
	private Date received_date;
	private Date issue_date;
	private String vesel;
	private String contr_no;
	private String consignment_mark;
	private String quantity;
	private String ss_cc;
	private String packages;
	private String quality;
	private String received_sound;
	private String damage;
	private String empty;
	private String shorte;
	private double gross_sale;
	private double less_expense;
	private double net_sale;
	private double freight;
	private double airport_charges;
	private double waste;
	private double dpa_charges;
	private double pickup_charge;
	private double unloading_charge;
	private double storage_charge;
	private double port;
	private double auction;
	private double commission;
	private double amount;
	private String details;
	private String items;
	private long status;
	
	

	public CommissionSalesReportBean(String office, long number,
			String supplier, Date received_date, Date issue_date, String vesel,
			String contr_no, String consignment_mark, String quantity,
			String ss_cc, String packages, String quality,
			String received_sound, String damage, String empty, String shorte,
			double gross_sale, double less_expense, double net_sale,
			double freight, double airport_charges, double waste,
			double dpa_charges, double pickup_charge, double unloading_charge,
			double storage_charge, double port, double auction,
			double commission, String details) {
		super();
		this.office = office;
		this.number = number;
		this.supplier = supplier;
		this.received_date = received_date;
		this.issue_date = issue_date;
		this.vesel = vesel;
		this.contr_no = contr_no;
		this.consignment_mark = consignment_mark;
		this.quantity = quantity;
		this.ss_cc = ss_cc;
		this.packages = packages;
		this.quality = quality;
		this.received_sound = received_sound;
		this.damage = damage;
		this.empty = empty;
		this.shorte = shorte;
		this.gross_sale = gross_sale;
		this.less_expense = less_expense;
		this.net_sale = net_sale;
		this.freight = freight;
		this.airport_charges = airport_charges;
		this.waste = waste;
		this.dpa_charges = dpa_charges;
		this.pickup_charge = pickup_charge;
		this.unloading_charge = unloading_charge;
		this.storage_charge = storage_charge;
		this.port = port;
		this.auction = auction;
		this.commission = commission;
		this.details = details;
	}
	
	public CommissionSalesReportBean(String office, long number,
			String supplier, Date received_date, Date issue_date, String vesel,
			String contr_no, String consignment_mark, String quantity,
			String ss_cc, String packages, String quality,
			String received_sound, String damage, String empty, String shorte,double amount,
		 String items) {
		super();
		this.office = office;
		this.number = number;
		this.supplier = supplier;
		this.received_date = received_date;
		this.issue_date = issue_date;
		this.vesel = vesel;
		this.contr_no = contr_no;
		this.consignment_mark = consignment_mark;
		this.quantity = quantity;
		this.ss_cc = ss_cc;
		this.packages = packages;
		this.quality = quality;
		this.received_sound = received_sound;
		this.damage = damage;
		this.empty = empty;
		this.shorte = shorte;
		this.items = items;
		this.amount = amount;
	}


	public CommissionSalesReportBean(long number, String customer,
			Date issue_date, double amount, String items) {
		super();
		this.number = number;
		this.customer = customer;
		this.issue_date = issue_date;
		this.amount = amount;
		this.items = items;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
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

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

}
