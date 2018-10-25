package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TRANSPORTATION)
public class TranspotationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -348574620716300382L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "code", length = 100)
	private String code;

	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;
	
	@Column(name = "credit_limit")
	private double credit_limit;

	@Column(name = "max_credit_period")
	private int max_credit_period;

	@Column(name = "description", length = 500)
	private String description;
	
	@Column(name="subscription", columnDefinition = "bigint default 0", nullable = false)
	private long subscription;
	
	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;
	
	public TranspotationModel() {
		super();
	}

	public TranspotationModel(long id) {
		super();
		this.id = id;
	}

	public TranspotationModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public double getCredit_limit() {
		return credit_limit;
	}

	public void setCredit_limit(double credit_limit) {
		this.credit_limit = credit_limit;
	}

	public int getMax_credit_period() {
		return max_credit_period;
	}

	public void setMax_credit_period(int max_credit_period) {
		this.max_credit_period = max_credit_period;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getSubscription() {
		return subscription;
	}

	public void setSubscription(long subscription) {
		this.subscription = subscription;
	}

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

}