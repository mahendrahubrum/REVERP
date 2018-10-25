package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 17, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_DAILY_QUOTATION_DETAILS)
public class DailyQuotationDetailsModel implements Serializable {

	private static final long serialVersionUID = 8620888744615412730L;

	public DailyQuotationDetailsModel() {
		super();
	}

	public DailyQuotationDetailsModel(long id) {
		super();
		this.id = id;
	}


	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "supplier")
	private SupplierModel supplier;

	@OneToOne
	@JoinColumn(name = "item")
	private ItemModel item;
	
	@OneToOne
	@JoinColumn(name = "unit")
	private UnitModel unit;

	@Column(name = "rate")
	private double rate;
	
	@Column(name = "quantity")
	private double quantity;
	
	@Column(name = "country_id" , columnDefinition="bigint default 0", nullable=false)
	private long countryId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SupplierModel getSupplier() {
		return supplier;
	}

	public void setSupplier(SupplierModel supplier) {
		this.supplier = supplier;
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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public long getCountryId() {
		return countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
}
