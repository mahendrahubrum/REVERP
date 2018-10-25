package com.inventory.config.unit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.ItemModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 10, 2013
 */
@Entity
@Table(name = SConstants.tb_names.I_ITEM_UNIT_MANAGEMENT)
public class ItemUnitMangementModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6830466499196220174L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "basicUnit")
	private long basicUnit;

	@Column(name = "alternateUnit")
	private long alternateUnit;

	@Column(name = "sales_type")
	private long sales_type;

	@Column(name = "convertion_rate")
	private double convertion_rate;

	@Column(name = "item_price")
	private double item_price;
	
	@OneToOne
	@JoinColumn(name = "purchase_currency")
	private CurrencyModel purchaseCurrency;
	
	@Column(name = "purchase_convertion_rate", columnDefinition = "double default 1", nullable = false)
	private double purchase_convertion_rate;

	@Column(name = "status")
	private long status;

	public ItemUnitMangementModel() {
		super();
	}

	public ItemUnitMangementModel(long id, ItemModel item, long basicUnit,
			long alternateUnit, long sales_type, double convertion_rate,
			double item_price, long status) {
		super();
		this.id = id;
		this.item = item;
		this.basicUnit = basicUnit;
		this.alternateUnit = alternateUnit;
		this.sales_type = sales_type;
		this.convertion_rate = convertion_rate;
		this.item_price = item_price;
		this.status = status;
	}
	
	public ItemUnitMangementModel(long id, ItemModel item, long basicUnit,
			long alternateUnit, long sales_type, double convertion_rate,
			double item_price, CurrencyModel currency, double purch_conv_rate, long status) {
		super();
		this.id = id;
		this.item = item;
		this.basicUnit = basicUnit;
		this.alternateUnit = alternateUnit;
		this.sales_type = sales_type;
		this.convertion_rate = convertion_rate;
		this.item_price = item_price;
		this.purchaseCurrency = currency;
		this.purchase_convertion_rate = purch_conv_rate;
		this.status = status;
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

	public long getBasicUnit() {
		return basicUnit;
	}

	public void setBasicUnit(long basicUnit) {
		this.basicUnit = basicUnit;
	}

	public long getAlternateUnit() {
		return alternateUnit;
	}

	public void setAlternateUnit(long alternateUnit) {
		this.alternateUnit = alternateUnit;
	}

	public double getConvertion_rate() {
		return convertion_rate;
	}

	public void setConvertion_rate(double convertion_rate) {
		this.convertion_rate = convertion_rate;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public double getItem_price() {
		return item_price;
	}

	public void setItem_price(double item_price) {
		this.item_price = item_price;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public CurrencyModel getPurchaseCurrency() {
		return purchaseCurrency;
	}

	public void setPurchaseCurrency(CurrencyModel purchaseCurrency) {
		this.purchaseCurrency = purchaseCurrency;
	}

	public double getPurchase_convertion_rate() {
		return purchase_convertion_rate;
	}

	public void setPurchase_convertion_rate(double purchase_convertion_rate) {
		this.purchase_convertion_rate = purchase_convertion_rate;
	}
	
}
