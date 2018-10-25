package com.webspark.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.CURRENCY_RATE)
public class CurrencyRateModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "rate_id")
	private long id;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "base_currency")
	private CurrencyModel baseCurrency;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currencyId;

	@Column(name = "rate")
	private double rate;

	public CurrencyRateModel(long id) {
		super();
		this.id = id;
	}

	public CurrencyRateModel(long id, CurrencyModel baseCurrency) {
		super();
		this.id = id;
		this.baseCurrency = baseCurrency;
	}

	public CurrencyRateModel() {
		// TODO Auto-generated constructor stub
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CurrencyModel getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(CurrencyModel baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public CurrencyModel getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(CurrencyModel currencyId) {
		this.currencyId = currencyId;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
