package com.inventory.rent.model;

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
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 2, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_RENT_INVENTORY_DETAILS)
public class RentInventoryDetailsModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1723093877074839053L;

	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity")
	private double qunatity;

	@Column(name = "quantity_in_basic_unit")
	private double quantity_in_basic_unit;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;
	
	@Column(name = "balance")
	private double balance;

	@Column(name = "unit_price")
	private double unit_price;

	@Column(name = "discount_amount")
	private double discount_amount;

	@Column(name = "net_price")
	private double net_price;
	
	@Column(name = "conv_qty")
	private double conv_qty;
	
	@Column(name = "returned_qty")
	private double returned_qty;
	
	@Column(name = "returned_status")
	private String returned_status;
	
	@Column(name = "returned_date")
	private Date returned_date;
	
	@Column(name = "supplied_date")
	private Date supplied_date;

	@Column(name = "period")
	private Double period;
	
	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	public RentInventoryDetailsModel() {
		// TODO Auto-generated constructor stub
	}
 
	public Double getPeriod() {
		return period;
	}

	public void setPeriod(Double period) {
		this.period = period;
	}

	public Date getReturned_date() {
		return returned_date;
	}

	public void setReturned_date(Date returned_date) {
		this.returned_date = returned_date;
	}

	public String getReturned_status() {
		return returned_status;
	}

	public void setReturned_status(String returned_status) {
		this.returned_status = returned_status;
	}

	public double getReturned_qty() {
		return returned_qty;
	}

	public void setReturned_qty(double returned_qty) {
		this.returned_qty = returned_qty;
	}

	public double getNet_price() {
		return net_price;
	}

	public void setNet_price(double net_price) {
		this.net_price = net_price;
	}

	public double getConv_qty() {
		return conv_qty;
	}

	public void setConv_qty(double conv_qty) {
		this.conv_qty = conv_qty;
	}

	@Column(name = "stk_id")
	private long stk_id;

	/*
	 * @Column(name = "manufacturing_date") private Date manufacturing_date;
	 * 
	 * @Column(name = "expiry_date") private Date expiry_date;
	 * 
	 * @Column(name="stock_id") private long stock_id;
	 */

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

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
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

	

	/*
	 * public Date getManufacturing_date() { return manufacturing_date; }
	 * 
	 * public void setManufacturing_date(Date manufacturing_date) {
	 * this.manufacturing_date = manufacturing_date; }
	 * 
	 * public Date getExpiry_date() { return expiry_date; }
	 * 
	 * public void setExpiry_date(Date expiry_date) { this.expiry_date =
	 * expiry_date; }
	 * 
	 * public long getStock_id() { return stock_id; }
	 * 
	 * public void setStock_id(long stock_id) { this.stock_id = stock_id; }
	 */

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}



	public long getStk_id() {
		return stk_id;
	}

	public void setStk_id(long stk_id) {
		this.stk_id = stk_id;
	}

	public Date getSupplied_date() {
		return supplied_date;
	}

	public void setSupplied_date(Date supplied_date) {
		this.supplied_date = supplied_date;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	
	
	
}
