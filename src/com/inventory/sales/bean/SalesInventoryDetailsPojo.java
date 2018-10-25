package com.inventory.sales.bean;

import java.util.Date;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 29, 2013
 */

public class SalesInventoryDetailsPojo {

	ItemModel item;
	TaxModel tax;

	long id, order_id, item_id, unit_id, tax_id, inventry_details_id, stock_id;

	String item_name, item_code, unit_name, stock_details, ledger_name;

	double qunatity, tax_amount, tax_percentage, balance, unit_price;

	double discount_amount, cess_amount,quantity_in_basic_unit;

	Date manufacturing_date, expiry_date,date;

	public SalesInventoryDetailsPojo(long order_id, long inventry_details_id,
			long item_id, String item_code, String item_name, long unit_id,
			String unit_name, long tax_id, double tax_amount,
			double tax_percentage, double qunatity, double unit_price,
			double discount_amount, double balance, double cess_amount) {
		super();
		this.order_id = order_id;
		this.inventry_details_id = inventry_details_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.tax_id = tax_id;
		this.item_name = item_name;
		this.item_code = item_code;
		this.unit_name = unit_name;
		this.qunatity = qunatity;
		this.tax_amount = tax_amount;
		this.tax_percentage = tax_percentage;
		this.unit_price = unit_price;
		this.discount_amount = discount_amount;
		this.balance = balance;
		this.cess_amount = cess_amount;
	}
	
	public SalesInventoryDetailsPojo(long order_id, long inventry_details_id,
			long item_id, String item_code, String item_name, long unit_id,
			String unit_name, long tax_id, double tax_amount,
			double tax_percentage, double qunatity, double unit_price,
			double discount_amount, double balance,
			 Date manufacturing_date , Date expiry_date) {
		super();
		this.order_id = order_id;
		this.inventry_details_id = inventry_details_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.tax_id = tax_id;
		this.item_name = item_name;
		this.item_code = item_code;
		this.unit_name = unit_name;
		this.qunatity = qunatity;
		this.tax_amount = tax_amount;
		this.tax_percentage = tax_percentage;
		this.unit_price = unit_price;
		this.discount_amount = discount_amount;
		this.balance = balance;
		this.expiry_date = expiry_date;
		this.manufacturing_date = manufacturing_date;
	}
	
	public SalesInventoryDetailsPojo(long order_id, long inventry_details_id,
			long item_id, String item_code, String item_name, long unit_id,
			String unit_name, long tax_id, double tax_amount,
			double tax_percentage, double qunatity, double unit_price,
			double discount_amount, double balance, double cess_amount,
			 Date manufacturing_date , Date expiry_date) {
		super();
		this.order_id = order_id;
		this.inventry_details_id = inventry_details_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.tax_id = tax_id;
		this.item_name = item_name;
		this.item_code = item_code;
		this.unit_name = unit_name;
		this.qunatity = qunatity;
		this.tax_amount = tax_amount;
		this.tax_percentage = tax_percentage;
		this.unit_price = unit_price;
		this.discount_amount = discount_amount;
		this.balance = balance;
		this.cess_amount = cess_amount;
		this.expiry_date = expiry_date;
		this.manufacturing_date = manufacturing_date;
	}
	
	public SalesInventoryDetailsPojo(long order_id, long inventry_details_id,
			long item_id, String item_code, String item_name, long unit_id,
			String unit_name, long tax_id, double tax_amount,
			double tax_percentage, double qunatity, double unit_price,
			double discount_amount, double balance, double cess_amount,
			 Date manufacturing_date , Date expiry_date, long stock_id) {
		super();
		this.order_id = order_id;
		this.inventry_details_id = inventry_details_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.tax_id = tax_id;
		this.item_name = item_name;
		this.item_code = item_code;
		this.unit_name = unit_name;
		this.qunatity = qunatity;
		this.tax_amount = tax_amount;
		this.tax_percentage = tax_percentage;
		this.unit_price = unit_price;
		this.discount_amount = discount_amount;
		this.balance = balance;
		this.cess_amount = cess_amount;
		this.expiry_date = expiry_date;
		this.manufacturing_date = manufacturing_date;
		this.stock_id=stock_id;
	}
	
	
	
	public SalesInventoryDetailsPojo(long order_id, long inventry_details_id,
			long item_id, String item_code, String item_name, long unit_id,
			String unit_name, long tax_id, double tax_amount,
			double tax_percentage, double qunatity, double unit_price,
			double discount_amount, double balance, double cess_amount,
			double quantity_in_basic_unit) {
		super();
		this.order_id = order_id;
		this.inventry_details_id = inventry_details_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.tax_id = tax_id;
		this.item_name = item_name;
		this.item_code = item_code;
		this.unit_name = unit_name;
		this.qunatity = qunatity;
		this.tax_amount = tax_amount;
		this.tax_percentage = tax_percentage;
		this.unit_price = unit_price;
		this.discount_amount = discount_amount;
		this.balance = balance;
		this.cess_amount = cess_amount;
		this.expiry_date = expiry_date;
		this.manufacturing_date = manufacturing_date;
		this.stock_id=stock_id;
		this.quantity_in_basic_unit=quantity_in_basic_unit;
	}
	
	

	public SalesInventoryDetailsPojo(long order_id, long inventry_details_id,
			long item_id, String item_code, String item_name, long unit_id,
			String unit_name, long tax_id, double tax_amount,
			double tax_percentage, double qunatity, double unit_price,
			double discount_amount, double balance) {
		super();
		this.order_id = order_id;
		this.inventry_details_id = inventry_details_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.tax_id = tax_id;
		this.item_name = item_name;
		this.item_code = item_code;
		this.unit_name = unit_name;
		this.qunatity = qunatity;
		this.tax_amount = tax_amount;
		this.tax_percentage = tax_percentage;
		this.unit_price = unit_price;
		this.discount_amount = discount_amount;
		this.balance = balance;
	}

	public SalesInventoryDetailsPojo(long id, String stock_details) {
		super();
		this.id = id;
		this.stock_details = stock_details;
	}

	public SalesInventoryDetailsPojo() {
		super();
	}
	
	
	

	public SalesInventoryDetailsPojo(String ledger_name, double unit_price, Date date) {
		super();
		this.ledger_name = ledger_name;
		this.unit_price = unit_price;
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	public double getQunatity() {
		return qunatity;
	}

	public void setQunatity(double qunatity) {
		this.qunatity = qunatity;
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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}

	public double getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(double discount_amount) {
		this.discount_amount = discount_amount;
	}

	public TaxModel getTax() {
		return tax;
	}

	public void setTax(TaxModel tax) {
		this.tax = tax;
	}

	public long getItem_id() {
		return item_id;
	}

	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}

	public long getTax_id() {
		return tax_id;
	}

	public void setTax_id(long tax_id) {
		this.tax_id = tax_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public long getInventry_details_id() {
		return inventry_details_id;
	}

	public void setInventry_details_id(long inventry_details_id) {
		this.inventry_details_id = inventry_details_id;
	}

	public double getCess_amount() {
		return cess_amount;
	}

	public void setCess_amount(double cess_amount) {
		this.cess_amount = cess_amount;
	}

	public Date getManufacturing_date() {
		return manufacturing_date;
	}

	public void setManufacturing_date(Date manufacturing_date) {
		this.manufacturing_date = manufacturing_date;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

	public String getStock_details() {
		return stock_details;
	}

	public void setStock_details(String stock_details) {
		this.stock_details = stock_details;
	}

	public long getStock_id() {
		return stock_id;
	}

	public void setStock_id(long stock_id) {
		this.stock_id = stock_id;
	}

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}

	public String getLedger_name() {
		return ledger_name;
	}

	public void setLedger_name(String ledger_name) {
		this.ledger_name = ledger_name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
