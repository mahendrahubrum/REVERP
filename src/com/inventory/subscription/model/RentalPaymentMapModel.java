package com.inventory.subscription.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 27, 2015
 */

@Entity
@Table(name = SConstants.tb_names.I_RENTAL_PAYMENT_MAP)
public class RentalPaymentMapModel implements Serializable {

	private static final long serialVersionUID = -920234719869440766L;


	public RentalPaymentMapModel() {
		super();
	}

	
	public RentalPaymentMapModel(long id) {
		super();
		this.id = id;
	}

	
	public RentalPaymentMapModel(long rental_id, long payment_id, int type) {
		super();
		this.rental_id = rental_id;
		this.payment_id = payment_id;
		this.type = type;
	}
	
	public RentalPaymentMapModel(long rental_id, long payment_id, int type, double amount) {
		super();
		this.rental_id = rental_id;
		this.payment_id = payment_id;
		this.type = type;
		this.amount = amount;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "rental_id")
	private long rental_id;

	@Column(name = "payment_id")
	private long payment_id;
	
	@Column(name = "amount")
	private double amount;

	@Column(name = "type")
	private int type;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getRental_id() {
		return rental_id;
	}


	public void setRental_id(long rental_id) {
		this.rental_id = rental_id;
	}


	public long getPayment_id() {
		return payment_id;
	}


	public void setPayment_id(long payment_id) {
		this.payment_id = payment_id;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}
	
	
	
}
