package com.inventory.purchase.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 27, 2013
 */

/**
 * @author sangeeth
 * @date 21-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_STOCK_CREATE_DETAILS)
public class StockCreateDetailsModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity" ,columnDefinition ="double default 0", nullable=false)
	private double qunatity;

	@Column(name = "qty_in_basic_unit" ,columnDefinition ="double default 0", nullable=false)
	private double qty_in_basic_unit;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;

	@Column(name = "unit_price" ,columnDefinition ="double default 0", nullable=false)
	private double unit_price;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "discount",columnDefinition ="double default 0", nullable=false)
	private double discount;
	
	@OneToOne
	@JoinColumn(name = "tax_id")
	private TaxModel tax;
	
	@Column(name = "taxPercentage",columnDefinition ="double default 0", nullable=false)
	private double taxPercentage;
	
	@Column(name = "taxAmount",columnDefinition ="double default 0", nullable=false)
	private double taxAmount;
	
	@Column(name = "cessAmount",columnDefinition ="double default 0", nullable=false)
	private double cessAmount;
	
	@Column(name = "manufacturing_date")
	private Date manufacturing_date;
	
	@Column(name = "expiry_date")
	private Date expiry_date;
	
	@Column(name = "grade_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long grade_id;
	
	@Column(name = "location_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long location_id;
	
	@Column(name = "barcode",columnDefinition ="varchar(500) default ''", nullable=false)
	private String barcode;
	
	@Column(name = "batch_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long batch_id;
	
	@Column(name = "stock_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long stock_id;

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

	public double getQty_in_basic_unit() {
		return qty_in_basic_unit;
	}

	public void setQty_in_basic_unit(double qty_in_basic_unit) {
		this.qty_in_basic_unit = qty_in_basic_unit;
	}

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public TaxModel getTax() {
		return tax;
	}

	public void setTax(TaxModel tax) {
		this.tax = tax;
	}

	public double getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(double taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public double getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(double cessAmount) {
		this.cessAmount = cessAmount;
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

	public long getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(long grade_id) {
		this.grade_id = grade_id;
	}

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public long getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(long batch_id) {
		this.batch_id = batch_id;
	}

	public long getStock_id() {
		return stock_id;
	}

	public void setStock_id(long stock_id) {
		this.stock_id = stock_id;
	}
	
}
