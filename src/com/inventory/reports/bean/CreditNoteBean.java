package com.inventory.reports.bean;
/**
 * 
 * @author Anil K P.
 *
 * Aug 7, 2013
 */
public class CreditNoteBean {

	private String item="";
	private String itemName="";
	private String unit="";
	private double unitPrice=0;
	private double quantity=0;
	private double amount=0;
	private double tax=0;
	private double totalAmount=0;
	
	public CreditNoteBean(String item, String itemName, String unit,
			double unitPrice, double quantity, double amount, double tax,
			double totalAmount) {
		super();
		this.item = item;
		this.itemName = itemName;
		this.unit = unit;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.amount = amount;
		this.tax = tax;
		this.totalAmount = totalAmount;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getDescription() {
		return itemName;
	}

	public void setDescription(String description) {
		this.itemName = description;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
		
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

}
