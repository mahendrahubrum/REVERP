package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.PaymentTermsModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_CONTRACTOR)
public class ContractorModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6145577928263228623L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "contractor_code", length = 100)
	private String contractor_code;

	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;

	@Column(name = "sales_type")
	private long sales_type;

	@OneToOne
	@JoinColumn(name = "payment_terms")
	private PaymentTermsModel payment_terms;

	@Column(name = "credit_limit")
	private double credit_limit;

	@Column(name = "max_credit_period")
	private int max_credit_period;

	@Column(name = "description", length = 500)
	private String description;

	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;

	public ContractorModel() {
		super();
	}

	public ContractorModel(long id) {
		super();
		this.id = id;
	}

	public ContractorModel(long id, String name) {
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

	public String getContractor_code() {
		return contractor_code;
	}

	public void setContractor_code(String contractor_code) {
		this.contractor_code = contractor_code;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public PaymentTermsModel getPayment_terms() {
		return payment_terms;
	}

	public void setPayment_terms(PaymentTermsModel payment_terms) {
		this.payment_terms = payment_terms;
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

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

}