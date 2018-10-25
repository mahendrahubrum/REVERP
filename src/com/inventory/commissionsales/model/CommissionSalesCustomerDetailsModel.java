package com.inventory.commissionsales.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 27, 2013
 */

@Entity
@Table(name = SConstants.tb_names.COMMISSION_SALES_CUSTOMER_DETAILS)
public class CommissionSalesCustomerDetailsModel implements Serializable {

	private static final long serialVersionUID = -3552712169255294732L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "quantity")
	private double qunatity;

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

	@Column(name = "unit_price")
	private double unit_price;

	@Column(name = "discount_amount")
	private double discount_amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
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

	public TaxModel getTax() {
		return tax;
	}

	public void setTax(TaxModel tax) {
		this.tax = tax;
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

}
