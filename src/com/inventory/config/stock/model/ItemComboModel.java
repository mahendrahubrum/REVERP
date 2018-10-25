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

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 25, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_ITEM_COMBO)
public class ItemComboModel implements Serializable {

	private static final long serialVersionUID = 8065481786269178415L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "fk_item_id")
	private ItemModel item;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "unit_price")
	private double unitPrice;

	@Column(name = "date")
	private Date date;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "item_combo_details_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<ItemComboDetailsModel> item_combo_details_list = new ArrayList<ItemComboDetailsModel>();

	
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


	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public List<ItemComboDetailsModel> getItem_combo_details_list() {
		return item_combo_details_list;
	}

	public void setItem_combo_details_list(List<ItemComboDetailsModel> item_combo_details_list) {
		this.item_combo_details_list = item_combo_details_list;
	}
}
