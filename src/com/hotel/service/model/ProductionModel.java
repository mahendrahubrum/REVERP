package com.hotel.service.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */

@Entity
@Table(name = SConstants.tb_names.H_ITEM_PRODUCTION)
public class ProductionModel implements Serializable {

	private static final long serialVersionUID = -2840074940775827211L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "production_no")
	private long production_no;

	@OneToOne
	@JoinColumn(name = "fk_item_id")
	private ItemModel item;

	@OneToOne
	@JoinColumn(name = "fk_unit_id")
	private UnitModel unit;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "qty_in_basic_unit")
	private double qty_in_basic_unit;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "fk_office_id")
	private S_OfficeModel office;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "production_details_link", joinColumns = { @JoinColumn(name = "production_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<ProductionDetailsModel> details_list = new ArrayList<ProductionDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProduction_no() {
		return production_no;
	}

	public void setProduction_no(long production_no) {
		this.production_no = production_no;
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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List<ProductionDetailsModel> getDetails_list() {
		return details_list;
	}

	public void setDetails_list(List<ProductionDetailsModel> details_list) {
		this.details_list = details_list;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getQty_in_basic_unit() {
		return qty_in_basic_unit;
	}

	public void setQty_in_basic_unit(double qty_in_basic_unit) {
		this.qty_in_basic_unit = qty_in_basic_unit;
	}

}
