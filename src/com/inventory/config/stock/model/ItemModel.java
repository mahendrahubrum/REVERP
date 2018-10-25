package com.inventory.config.stock.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.model.ItemSubGroupModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 17, 2013
 */

/**
 * @author sangeeth
 * @date 14-Dec-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_ITEM)
public class ItemModel implements Serializable {

	public ItemModel() {
		super();
	}

	public ItemModel(long id) {
		super();
		this.id = id;
	}

	public ItemModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public ItemModel(long id, String name, double current_balalnce) {
		super();
		this.id = id;
		this.name = name;
		this.current_balalnce = current_balalnce;
	}

	public ItemModel(long id, String item_code, String name) {
		super();
		this.id = id;
		this.name = name;
		this.item_code = item_code;
	}

	public ItemModel(long id, UnitModel unit) {
		super();
		this.id = id;
		this.unit = unit;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "item_code", length = 100)
	private String item_code;
	
	@Column(name = "supplier_code", columnDefinition = "varchar(500) default ''")
	private String supplier_code;

	@Column(name = "name", length = 200)
	private String name;
	
	@Column(name = "language_id", columnDefinition = "bigint default 0 ", nullable = false)
	private long languageId;
	
	@Column(name = "second_name", columnDefinition = "varchar(500) default '' ",nullable = false)
	private String secondName;

	@OneToOne
	@JoinColumn(name = "sub_group")
	private ItemSubGroupModel sub_group;

	@Column(name = "opening_balance")
	private double opening_balance;
	
	@Column(name = "reorder_level")
	private double reorder_level;
	
	@Column(name = "reserved_quantity", columnDefinition = "double default 0")
	private double reservedQuantity;
	
	@Column(name = "minimum_level", columnDefinition = "double default 0")
	private double minimum_level;
	
	@Column(name = "maximum_level", columnDefinition = "double default 0")
	private double maximum_level;

	@OneToOne
	@JoinColumn(name = "sale_tax")
	private TaxModel salesTax;

	@OneToOne
	@JoinColumn(name = "purchase_tax")
	private TaxModel purchaseTax;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;
	
	@Column(name = "status")
	private long status;
	
	@Column(name = "current_balance")
	private double current_balalnce;

	@Column(name = "opening_stock_date")
	private Date opening_stock_date;
	
	@Column(name = "affect_type")
	private int affect_type;
	
	@OneToOne
	@JoinColumn(name = "purchase_currency")
	private CurrencyModel purchaseCurrency;

	@Column(name = "rate")
	private double rate;
	
	@Column(name = "purchase_convertion_rate", columnDefinition = "double default 1", nullable = false)
	private double purchase_convertion_rate;
	
	@OneToOne
	@JoinColumn(name = "sale_currency")
	private CurrencyModel saleCurrency;
	
	@Column(name = "sale_rate", columnDefinition = "double default 0", nullable = false)
	private double sale_rate;
	
	@Column(name = "sale_convertion_rate", columnDefinition = "double default 1", nullable = false)
	private double sale_convertion_rate;
	
	@Column(name = "discount", columnDefinition = "double default 0", nullable = false)
	private double discount;
	
	@Column(name = "max_discount", columnDefinition = "double default 0", nullable = false)
	private double max_discount;
	
	@Column(name = "item_model", columnDefinition = "bigint default 0", nullable = false)
	private long item_model;
	
	@Column(name = "colour", columnDefinition = "bigint default 0", nullable = false)
	private long colour;
	
	@Column(name = "size", columnDefinition = "bigint default 0", nullable = false)
	private long size;
	
	@Column(name = "style", columnDefinition = "bigint default 0", nullable = false)
	private long style;
	
	@Column(name = "brand_id", columnDefinition = "bigint default 0", nullable = false)
	private long brand;
	
	@Column(name = "preferred_vendor", columnDefinition = "varchar(500) default ''")
	private String preferred_vendor;

	@Column(name = "specification")
	private String specification;
	
	@Column(name = "desciption", length = 400)
	private String desciption;
	
	@Column(name = "icon")
	private String icon;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@Column(name = "cess_enabled",columnDefinition = "char default 'N'", nullable = false)
	private char cess_enabled;
	
	@Column(name = "parent_id" , columnDefinition="bigint default 0", nullable=false)
	private long parentId;
	
	
	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}

	public String getSupplier_code() {
		return supplier_code;
	}

	public void setSupplier_code(String supplier_code) {
		this.supplier_code = supplier_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemSubGroupModel getSub_group() {
		return sub_group;
	}

	public void setSub_group(ItemSubGroupModel sub_group) {
		this.sub_group = sub_group;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public double getOpening_balance() {
		return opening_balance;
	}

	public void setOpening_balance(double opening_balance) {
		this.opening_balance = opening_balance;
	}

	public double getCurrent_balalnce() {
		return current_balalnce;
	}

	public void setCurrent_balalnce(double current_balalnce) {
		this.current_balalnce = current_balalnce;
	}

	public TaxModel getSalesTax() {
		return salesTax;
	}

	public void setSalesTax(TaxModel salesTax) {
		this.salesTax = salesTax;
	}

	public TaxModel getPurchaseTax() {
		return purchaseTax;
	}

	public void setPurchaseTax(TaxModel purchaseTax) {
		this.purchaseTax = purchaseTax;
	}

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public char getCess_enabled() {
		return cess_enabled;
	}

	public void setCess_enabled(char cess_enabled) {
		this.cess_enabled = cess_enabled;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public double getReorder_level() {
		return reorder_level;
	}

	public void setReorder_level(double reorder_level) {
		this.reorder_level = reorder_level;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getSale_rate() {
		return sale_rate;
	}

	public void setSale_rate(double sale_rate) {
		this.sale_rate = sale_rate;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getMax_discount() {
		return max_discount;
	}

	public void setMax_discount(double max_discount) {
		this.max_discount = max_discount;
	}

	public String getPreferred_vendor() {
		return preferred_vendor;
	}

	public void setPreferred_vendor(String preferred_vendor) {
		this.preferred_vendor = preferred_vendor;
	}

	public Date getOpening_stock_date() {
		return opening_stock_date;
	}

	public void setOpening_stock_date(Date opening_stock_date) {
		this.opening_stock_date = opening_stock_date;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getAffect_type() {
		return affect_type;
	}

	public void setAffect_type(int affect_type) {
		this.affect_type = affect_type;
	}

	public double getReservedQuantity() {
		return reservedQuantity;
	}

	public void setReservedQuantity(double reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}

	public double getMinimum_level() {
		return minimum_level;
	}

	public void setMinimum_level(double minimum_level) {
		this.minimum_level = minimum_level;
	}

	public double getMaximum_level() {
		return maximum_level;
	}

	public void setMaximum_level(double maximum_level) {
		this.maximum_level = maximum_level;
	}

	public long getBrand() {
		return brand;
	}

	public void setBrand(long brand) {
		this.brand = brand;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getColour() {
		return colour;
	}

	public void setColour(long colour) {
		this.colour = colour;
	}

	public long getStyle() {
		return style;
	}

	public void setStyle(long style) {
		this.style = style;
	}

	public long getItem_model() {
		return item_model;
	}

	public void setItem_model(long item_model) {
		this.item_model = item_model;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public CurrencyModel getPurchaseCurrency() {
		return purchaseCurrency;
	}

	public void setPurchaseCurrency(CurrencyModel purchaseCurrency) {
		this.purchaseCurrency = purchaseCurrency;
	}

	public CurrencyModel getSaleCurrency() {
		return saleCurrency;
	}

	public void setSaleCurrency(CurrencyModel saleCurrency) {
		this.saleCurrency = saleCurrency;
	}

	public double getPurchase_convertion_rate() {
		return purchase_convertion_rate;
	}

	public void setPurchase_convertion_rate(double purchase_convertion_rate) {
		this.purchase_convertion_rate = purchase_convertion_rate;
	}

	public double getSale_convertion_rate() {
		return sale_convertion_rate;
	}

	public void setSale_convertion_rate(double sale_convertion_rate) {
		this.sale_convertion_rate = sale_convertion_rate;
	}
	
}
