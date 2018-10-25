package com.inventory.subscription.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.tax.model.TaxModel;
import com.webspark.common.util.SConstants;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 24, 2015
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_RENTAL_TRANSACTION_DETAILS)
public class RentalTransactionDetailsModel implements Serializable {

	public RentalTransactionDetailsModel() {
		this.description="";
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "rental")
	private SubscriptionCreationModel rental;

	@Column(name = "description",length = 300, columnDefinition = "varchar(300) default ''", nullable = false)
	private String description;
	
	@Column(name = "quantity")
	private double qunatity;

	@Column(name = "unit_price")
	private double unit_price;
	
	@OneToOne
	@JoinColumn(name = "tax_id")
	private TaxModel tax;

	@Column(name = "tax_amount")
	private double tax_amount;

	@Column(name = "tax_percentage")
	private double tax_percentage;

	@Column(name = "discount_amount")
	private double discount_amount;

	@Column(name = "cess_amount")
	private double cess_amount;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "milage")
	private double milage;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getMilage() {
		return milage;
	}

	public void setMilage(double milage) {
		this.milage = milage;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SubscriptionCreationModel getRental() {
		return rental;
	}

	public void setRental(SubscriptionCreationModel rental) {
		this.rental = rental;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getQunatity() {
		return qunatity;
	}

	public void setQunatity(double qunatity) {
		this.qunatity = qunatity;
	}

	public double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
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

	public double getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(double discount_amount) {
		this.discount_amount = discount_amount;
	}

	public double getCess_amount() {
		return cess_amount;
	}

	public void setCess_amount(double cess_amount) {
		this.cess_amount = cess_amount;
	}

}