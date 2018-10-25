package com.hotel.service.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */

@Entity
@Table(name = SConstants.tb_names.H_CASH_PAY_DETAILS)
public class CashPayDetailsModel implements Serializable {

	private static final long serialVersionUID = 2863627730224980095L;

	public CashPayDetailsModel() {
		super();
	}

	public CashPayDetailsModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "card_type")
	private long card_type;

	@Column(name = "bank_type")
	private long bank_type;

	@Column(name = "ccn_no", length = 20)
	private String ccn_no;

	@Column(name = "cheque_no", length = 20)
	private String cheque_no;

	@Column(name = "cheque_date")
	private Date cheque_date;

	@Column(name = "card_amount")
	private double card_amount;

	@Column(name = "bank_amount")
	private double bank_amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCard_type() {
		return card_type;
	}

	public void setCard_type(long card_type) {
		this.card_type = card_type;
	}

	public long getBank_type() {
		return bank_type;
	}

	public void setBank_type(long bank_type) {
		this.bank_type = bank_type;
	}

	public String getCcn_no() {
		return ccn_no;
	}

	public void setCcn_no(String ccn_no) {
		this.ccn_no = ccn_no;
	}

	public String getCheque_no() {
		return cheque_no;
	}

	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
	}

	public Date getCheque_date() {
		return cheque_date;
	}

	public void setCheque_date(Date cheque_date) {
		this.cheque_date = cheque_date;
	}

	public double getCard_amount() {
		return card_amount;
	}

	public void setCard_amount(double card_amount) {
		this.card_amount = card_amount;
	}

	public double getBank_amount() {
		return bank_amount;
	}

	public void setBank_amount(double bank_amount) {
		this.bank_amount = bank_amount;
	}

}
