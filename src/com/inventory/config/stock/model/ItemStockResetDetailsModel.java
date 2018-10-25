package com.inventory.config.stock.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 15, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_ITEM_PRICE)
public class ItemStockResetDetailsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027471080724276399L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@OneToOne
	@JoinColumn(name = "sales_type_id")
	private SalesTypeModel sales_type;

	@Column(name = "rate")
	private double rate;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "unit")
	private UnitModel unit;

	@Column(name = "description", length = 500)
	private String description;

	public ItemStockResetDetailsModel() {
		super();
	}

	public ItemStockResetDetailsModel(long id) {
		super();
		this.id = id;
	}

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

	public SalesTypeModel getSales_type() {
		return sales_type;
	}

	public void setSales_type(SalesTypeModel sales_type) {
		this.sales_type = sales_type;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

}