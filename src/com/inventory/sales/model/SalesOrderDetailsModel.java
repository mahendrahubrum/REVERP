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
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author anil
 * @date 02-Sep-2015
 * @Project REVERP
 */
@Entity
@Table(name = SConstants.tb_names.I_SALES_ORDER_DETAILS)
public class SalesOrderDetailsModel implements Serializable {

	private static final long serialVersionUID = 6936693042120439040L;

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
	
	@Column(name = "quotation_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long quotation_id;
	
	@Column(name = "quotation_child_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long quotation_child_id;
	
	@Column(name = "quantity_sold" ,columnDefinition ="double default 0", nullable=false)
	private double quantity_sold;
	
	@Column(name = "discount_type", columnDefinition = "int default 1", nullable = false)
	private int discount_type;
	
	@Column(name = "discount",columnDefinition ="double default 0", nullable=false)
	private double discount;
	
	@Column(name = "discount_percentage",columnDefinition ="double default 0", nullable=false)
	private double discountPercentage;

	public int getDiscount_type() {
		return discount_type;
	}

	public void setDiscount_type(int discount_type) {
		this.discount_type = discount_type;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
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

	public long getQuotation_id() {
		return quotation_id;
	}

	public void setQuotation_id(long quotation_id) {
		this.quotation_id = quotation_id;
	}

	public long getQuotation_child_id() {
		return quotation_child_id;
	}

	public void setQuotation_child_id(long quotation_child_id) {
		this.quotation_child_id = quotation_child_id;
	}

	public double getQuantity_sold() {
		return quantity_sold;
	}

	public void setQuantity_sold(double quantity_sold) {
		this.quantity_sold = quantity_sold;
	}
	
}
