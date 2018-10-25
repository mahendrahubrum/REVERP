package com.inventory.sales.model;

import java.io.Serializable;

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
 *         Jul 1, 2013
 */

/**
 * 
 * @author sangeeth
 * @date 22-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALES_RETURN_INVENTORY_DETAILS)
public class SalesReturnInventoryDetailsModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity" ,columnDefinition ="double default 0", nullable=false)
	private double qunatity;

	@Column(name = "quantity_in_basic_unit" ,columnDefinition ="double default 0", nullable=false)
	private double quantity_in_basic_unit;
	
	@Column(name = "quantity_saled" ,columnDefinition ="double default 0", nullable=false)
	private double quantity_saled;
	
	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;

	@Column(name = "unit_price" ,columnDefinition ="double default 0", nullable=false)
	private double unit_price;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "discount_type", columnDefinition = "int default 1", nullable = false)
	private int discount_type;
	
	@Column(name = "discount_percentage",columnDefinition ="double default 0", nullable=false)
	private double discountPercentage;
	
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
	@Column(name = "grade_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long grade_id;
	
	@Column(name = "batch_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long batch_id;
	
	@Column(name = "location_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long location_id;
	
	@Column(name = "stock_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long stock_id;
	
	@Column(name = "delivery_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long delivery_id;
	
	@Column(name = "delivery_child_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long delivery_child_id;
	
	@Column(name = "sales_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long sales_id;
	
	@Column(name = "sales_child_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long sales_child_id;
	
	@Column(name = "lock_count" ,columnDefinition ="double default 0", nullable=false)
	private double lock_count;
	
	@Column(name = "barcode",columnDefinition ="varchar(500) default ''", nullable=false)
	private String barcode;
	
	@Column(name = "reason_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long reasonId;
	
	
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

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}

	public double getQuantity_saled() {
		return quantity_saled;
	}

	public void setQuantity_saled(double quantity_saled) {
		this.quantity_saled = quantity_saled;
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

	public long getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(long grade_id) {
		this.grade_id = grade_id;
	}

	public long getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(long batch_id) {
		this.batch_id = batch_id;
	}

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public long getStock_id() {
		return stock_id;
	}

	public void setStock_id(long stock_id) {
		this.stock_id = stock_id;
	}

	public long getDelivery_id() {
		return delivery_id;
	}

	public void setDelivery_id(long delivery_id) {
		this.delivery_id = delivery_id;
	}

	public long getDelivery_child_id() {
		return delivery_child_id;
	}

	public void setDelivery_child_id(long delivery_child_id) {
		this.delivery_child_id = delivery_child_id;
	}

	public long getSales_id() {
		return sales_id;
	}

	public void setSales_id(long sales_id) {
		this.sales_id = sales_id;
	}

	public long getSales_child_id() {
		return sales_child_id;
	}

	public void setSales_child_id(long sales_child_id) {
		this.sales_child_id = sales_child_id;
	}

	public double getLock_count() {
		return lock_count;
	}

	public void setLock_count(double lock_count) {
		this.lock_count = lock_count;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getDiscount_type() {
		return discount_type;
	}

	public void setDiscount_type(int discount_type) {
		this.discount_type = discount_type;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public long getReasonId() {
		return reasonId;
	}

	public void setReasonId(long reasonId) {
		this.reasonId = reasonId;
	}
	
}