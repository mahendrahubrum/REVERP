package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Dec 27, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_SUPPLIER_QUOTATION_DETAILS)
public class SupplierQuotationDetailsModel implements Serializable {

	private static final long serialVersionUID = 3862985455788400481L;

	public SupplierQuotationDetailsModel() {
		super();
	}

	public SupplierQuotationDetailsModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item")
	private ItemModel item;

	@OneToOne
	@JoinColumn(name = "unit")
	private UnitModel unit;

	@OneToOne
	@JoinColumn(name = "fk_currency")
	private CurrencyModel currency;

	@Column(name = "rate")
	private double rate;
	
	@Column(name = "country_id" , columnDefinition="bigint default 0", nullable=false)
	private long countryId;

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

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public long getCountryId() {
		return countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

}
