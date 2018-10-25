package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 27, 2013
 */
public class SalesPrintBean {

	private String item;
	private double quantity;
	private double rate;
	private double total;
	private String unit;
	private String itemCode;
	private String rack;
	private String currency;
	private String description;
	private String location;
	private double weight;
	private double taxPercentage;
	private double taxAmount;
	private double netAmount;
	private double discount;
	private double gross_weight, net_weight, no_of_cartons, cbm;
	
//	public SalesPrintBean(String item, double quantity, double rate,
//			double total, String unit) {
//		super();
//		this.item = item;
//		this.quantity = quantity;
//		this.rate = rate;
//		this.total = total;
//		this.unit = unit;
//	}
	
	public SalesPrintBean(String item, double quantity, double rate,
			double total, String unit, String itemCode, double weight,
			double gross_weight, double net_weight, double no_of_cartons, double cbm) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.rate = rate;
		this.total = total;
		this.unit = unit;
		this.itemCode = itemCode;
		this.weight = weight;
		this.gross_weight = gross_weight;
		this.net_weight = net_weight;
		this.no_of_cartons = no_of_cartons;
		this.cbm = cbm;
	}
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	public SalesPrintBean() {
		
	}
	
	public SalesPrintBean(String item, double quantity, double rate,
			double total, String unit, String itemCode, double weight) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.rate = rate;
		this.total = total;
		this.unit = unit;
		this.itemCode = itemCode;
		this.weight = weight;
	}
	
	public SalesPrintBean(String item, double quantity, double rate,double netAmount,double total,double milage,String location) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.rate = rate;
		this.total = total;
		this.netAmount=netAmount;
		this.location=location;
		this.net_weight=milage;
	}
	
	
	
	
	public double getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}
	public double getTaxPercentage() {
		return taxPercentage;
	}
	public void setTaxPercentage(double taxPercentage) {
		this.taxPercentage = taxPercentage;
	}
	public double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}
	public String getRack() {
		return rack;
	}
	public void setRack(String rack) {
		this.rack = rack;
	}

	public double getGross_weight() {
		return gross_weight;
	}

	public void setGross_weight(double gross_weight) {
		this.gross_weight = gross_weight;
	}

	public double getNet_weight() {
		return net_weight;
	}

	public void setNet_weight(double net_weight) {
		this.net_weight = net_weight;
	}

	public double getNo_of_cartons() {
		return no_of_cartons;
	}

	public void setNo_of_cartons(double no_of_cartons) {
		this.no_of_cartons = no_of_cartons;
	}

	public double getCbm() {
		return cbm;
	}

	public void setCbm(double cbm) {
		this.cbm = cbm;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
