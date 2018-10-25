package com.inventory.purchase.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.inventory.config.stock.model.ItemModel;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 5, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_COMMISSION_STOCK)
public class CommissionStockModel implements Serializable {

	private static final long serialVersionUID = 3677674213037109600L;

	public CommissionStockModel(long id, String details) {
		super();
		this.id = id;
		this.details = details;
	}

	public CommissionStockModel(long id) {
		super();
		this.id = id;
	}

	public CommissionStockModel(long id, double quantity) {
		super();
		this.id = id;
		this.quantity = quantity;
	}

	public CommissionStockModel(long id, String barcode, String details) {
		super();
		this.id = id;
		this.barcode = barcode;
		this.details = details;
	}

	public CommissionStockModel() {
		super();
		this.item_tag = "";
		this.stock_arranged = 'N';
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "purchase_id")
	private long purchase_id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "rate")
	private double rate;

	@Column(name = "date_time")
	private Timestamp date_time;

	@Column(name = "manufacturing_date")
	private Date manufacturing_date;

	@Column(name = "expiry_date")
	private Date expiry_date;

	@Column(name = "status")
	private long status;

	@Column(name = "balance")
	private double balance;

	@Column(name = "barcode")
	private String barcode;

	@Column(name = "item_tag", columnDefinition = "varchar(100) default ''", nullable = false, length = 100)
	private String item_tag;

	@Column(name = "inv_det_id", columnDefinition = "bigint default 0", nullable = false)
	private long inv_det_id;

	@Column(name = "blocked", columnDefinition = "boolean default false", nullable = false)
	private boolean blocked;

	@Column(name = "grade_id", columnDefinition = "bigint default 0", nullable = false)
	private long gradeId;

	@Column(name = "stock_arranged", columnDefinition = "char default 'N'", nullable = false)
	private char stock_arranged;

	@Transient
	private String details;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPurchase_id() {
		return purchase_id;
	}

	public void setPurchase_id(long purchase_id) {
		this.purchase_id = purchase_id;
	}

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Date getManufacturing_date() {
		return manufacturing_date;
	}

	public void setManufacturing_date(Date manufacturing_date) {
		this.manufacturing_date = manufacturing_date;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public Timestamp getDate_time() {
		return date_time;
	}

	public void setDate_time(Timestamp date_time) {
		this.date_time = date_time;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public long getInv_det_id() {
		return inv_det_id;
	}

	public void setInv_det_id(long inv_det_id) {
		this.inv_det_id = inv_det_id;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public long getGradeId() {
		return gradeId;
	}

	public void setGradeId(long gradeId) {
		this.gradeId = gradeId;
	}

	public String getItem_tag() {
		return item_tag;
	}

	public void setItem_tag(String item_tag) {
		this.item_tag = item_tag;
	}

	public char getStock_arranged() {
		return stock_arranged;
	}

	public void setStock_arranged(char stock_arranged) {
		this.stock_arranged = stock_arranged;
	}

}