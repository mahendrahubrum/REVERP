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

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_MANUAL_TRADING_MASTER)
public class ManualTradingMasterModel implements Serializable {

	private static final long serialVersionUID = 3241211857308059570L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "date")
	private Date date;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "office_id")
	private long office_id;

	@OneToOne
	@JoinColumn(name = "fk_item_id")
	private ItemModel item;

	@Column(name = "opening_qty")
	private double opening_qty;

	@Column(name = "opening_unit")
	private long opening_unit;

	@Column(name = "total_purchase")
	private double total_purchase;

	@Column(name = "purch_unit")
	private long purch_unit;

	@Column(name = "total_sale")
	private double total_sale;

	@Column(name = "sale_unit")
	private long sale_unit;

	@Column(name = "waste_qty")
	private double waste_qty;

	@Column(name = "balance")
	private double balance;

	@Column(name = "waste_unit")
	private long waste_unit;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "manual_trad_link", joinColumns = { @JoinColumn(name = "mast_id") }, inverseJoinColumns = { @JoinColumn(name = "det_id") })
	private List<ManualTradingDetailsModel> detailsList = new ArrayList<ManualTradingDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	public double getTotal_purchase() {
		return total_purchase;
	}

	public void setTotal_purchase(double total_purchase) {
		this.total_purchase = total_purchase;
	}

	public long getPurch_unit() {
		return purch_unit;
	}

	public void setPurch_unit(long purch_unit) {
		this.purch_unit = purch_unit;
	}

	public double getTotal_sale() {
		return total_sale;
	}

	public void setTotal_sale(double total_sale) {
		this.total_sale = total_sale;
	}

	public long getSale_unit() {
		return sale_unit;
	}

	public void setSale_unit(long sale_unit) {
		this.sale_unit = sale_unit;
	}

	public double getWaste_qty() {
		return waste_qty;
	}

	public void setWaste_qty(double waste_qty) {
		this.waste_qty = waste_qty;
	}

	public long getWaste_unit() {
		return waste_unit;
	}

	public void setWaste_unit(long waste_unit) {
		this.waste_unit = waste_unit;
	}

	public List<ManualTradingDetailsModel> getDetailsList() {
		return detailsList;
	}

	public void setDetailsList(List<ManualTradingDetailsModel> detailsList) {
		this.detailsList = detailsList;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getOpening_qty() {
		return opening_qty;
	}

	public void setOpening_qty(double opening_qty) {
		this.opening_qty = opening_qty;
	}

	public long getOpening_unit() {
		return opening_unit;
	}

	public void setOpening_unit(long opening_unit) {
		this.opening_unit = opening_unit;
	}

}
