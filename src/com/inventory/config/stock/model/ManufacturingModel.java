package com.inventory.config.stock.model;

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

import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 25, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_MANUFACTURING)
public class ManufacturingModel implements Serializable {

	private static final long serialVersionUID = -2840074940775827211L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "manufacturing_no")
	private long manufacturing_no;

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
	
	@Column(name = "batch", columnDefinition="double default 0",nullable=false)
	private double batch;
	
	@Column(name = "expense", columnDefinition="double default 0",nullable=false)
	private double expense;
	
	@Column(name = "stock_id", columnDefinition="bigint default 0",nullable=false)
	private long stockId;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "fk_office_id")
	private S_OfficeModel office;
	
	@Column(name = "sales_order_id")
	private long salesOrderId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "manufacturing_details_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<ManufacturingDetailsModel> manufacturing_details_list = new ArrayList<ManufacturingDetailsModel>();

	public double getExpense() {
		return expense;
	}

	public void setExpense(double expense) {
		this.expense = expense;
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

	public long getManufacturing_no() {
		return manufacturing_no;
	}

	public void setManufacturing_no(long manufacturing_no) {
		this.manufacturing_no = manufacturing_no;
	}

	public List<ManufacturingDetailsModel> getManufacturing_details_list() {
		return manufacturing_details_list;
	}

	public void setManufacturing_details_list(
			List<ManufacturingDetailsModel> manufacturing_details_list) {
		this.manufacturing_details_list = manufacturing_details_list;
	}

	public long getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(long salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public double getBatch() {
		return batch;
	}

	public void setBatch(double batch) {
		this.batch = batch;
	}

	public long getStockId() {
		return stockId;
	}

	public void setStockId(long stockId) {
		this.stockId = stockId;
	}

}
