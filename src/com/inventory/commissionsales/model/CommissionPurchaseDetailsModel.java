package com.inventory.commissionsales.model;

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
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 4, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_COMMISSION_PURCHASE_DETAILS)
public class CommissionPurchaseDetailsModel implements Serializable {

	private static final long serialVersionUID = 500472822544196574L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity")
	private double qunatity;

	@Column(name = "qty_in_basic_unit")
	private double qty_in_basic_unit;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;

	@OneToOne
	@JoinColumn(name = "tax_id")
	private TaxModel tax;

	@Column(name = "tax_amount")
	private double tax_amount;

	@Column(name = "tax_percentage")
	private double tax_percentage;

	@Column(name = "balance")
	private double balance;

	@Column(name = "order_id")
	private long order_id;

	@Column(name = "unit_price")
	private double unit_price;

	@Column(name = "discount_amount")
	private double discount_amount;

	@Column(name = "gross_weight", columnDefinition = "double default 0.0", nullable = false)
	private double gross_weight;

	@Column(name = "net_weight", columnDefinition = "double default 0.0", nullable = false)
	private double net_weight;

	@Column(name = "no_of_cartons", columnDefinition = "double default 0.0", nullable = false)
	private double no_of_cartons;

	@Column(name = "cbm_value", columnDefinition = "double default 0.0", nullable = false)
	private double cbm_value;

	@Column(name = "grade_id", columnDefinition = "bigint default 0", nullable = false)
	private long gradeId;

	@Column(name = "item_tag", length=100)
	private String item_tag;

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

	public double getQty_in_basic_unit() {
		return qty_in_basic_unit;
	}

	public void setQty_in_basic_unit(double qty_in_basic_unit) {
		this.qty_in_basic_unit = qty_in_basic_unit;
	}

	public long getGradeId() {
		return gradeId;
	}

	public void setGradeId(long gradeId) {
		this.gradeId = gradeId;
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

	public double getCbm_value() {
		return cbm_value;
	}

	public void setCbm_value(double cbm_value) {
		this.cbm_value = cbm_value;
	}

	public String getItem_tag() {
		return item_tag;
	}

	public void setItem_tag(String item_tag) {
		this.item_tag = item_tag;
	}

}
