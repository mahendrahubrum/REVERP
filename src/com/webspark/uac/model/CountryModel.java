package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.COUNTRY)
public class CountryModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8690154361277302379L;

	public CountryModel() {
		super();
	}

	public CountryModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public CountryModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "country_id")
	private long id;

	@Column(name = "country_name", length = 100)
	private String name;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;
	

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

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

}
