package com.inventory.rent.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.model.ItemModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Jun 10, 2014
 */

@Entity
@Table(name = SConstants.tb_names.RENT_ITEM_RETURN_DETAIL)

public class RentReturnItemDetailModel extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5178568995077601181L;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "rent_number")
	private long rent_number;
	
	@Column(name = "return_date")
	private Date return_date;
	
	@Column(name = "rent_date")
	private Date rent_date;
	
	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "total_amount")
	private double total_amount;
	
	@OneToOne
	@JoinColumn(name = "itemid")
	private ItemModel itemname;
	
	@Column(name = "quantity")
	private double quantity;
	
	@Column(name = "return_status")
	private String return_status;
	
	@Column(name = "total_return")
	private double total_return;
	
	@Column(name = "rent_inventory_id", columnDefinition = "bigint default 0", nullable = false)
	private long rent_inventory_id;

	public long getRent_inventory_id() {
		return rent_inventory_id;
	}

	public void setRent_inventory_id(long rent_inventory_id) {
		this.rent_inventory_id = rent_inventory_id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRent_number() {
		return rent_number;
	}

	public void setRent_number(long rent_number) {
		this.rent_number = rent_number;
	}

	public Date getReturn_date() {
		return return_date;
	}

	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}

	public Date getRent_date() {
		return rent_date;
	}

	public void setRent_date(Date rent_date) {
		this.rent_date = rent_date;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}

	public ItemModel getItemname() {
		return itemname;
	}

	public void setItemname(ItemModel itemname) {
		this.itemname = itemname;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getReturn_status() {
		return return_status;
	}

	public void setReturn_status(String return_status) {
		this.return_status = return_status;
	}
	
	

	public double getTotal_return() {
		return total_return;
	}

	public void setTotal_return(double total_return) {
		this.total_return = total_return;
	}

	public RentReturnItemDetailModel() {
		super();
	}

	public RentReturnItemDetailModel(long id) {
		super();
		this.id = id;
	}

	public RentReturnItemDetailModel(long id, long rent_number) {
		super();
		this.id = id;
		this.rent_number = rent_number;
	}

	public RentReturnItemDetailModel(long id, long rent_number,
			ItemModel itemname) {
		super();
		this.id = id;
		this.rent_number = rent_number;
		this.itemname = itemname;
	}

	public RentReturnItemDetailModel(double total_amount) {
		super();
		this.total_amount = total_amount;
	}
	
	
	
	
	
	
	
	
	

}
