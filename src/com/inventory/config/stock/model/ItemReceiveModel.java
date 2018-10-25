package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_ITEM_RECEIVE)
public class ItemReceiveModel implements Serializable {

	private static final long serialVersionUID = -8586758645974298559L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;
	
	@Column(name = "foreign_item_id")
	private long foreignItemId;
	
	@Column(name = "transfer_id")
	private long transferId;

	@Column(name = "quantity")
	private double qunatity;

	@Column(name = "waste_quantity")
	private double waste_quantity;
	
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

	public double getQunatity() {
		return qunatity;
	}

	public void setQunatity(double qunatity) {
		this.qunatity = qunatity;
	}

	public double getWaste_quantity() {
		return waste_quantity;
	}

	public void setWaste_quantity(double waste_quantity) {
		this.waste_quantity = waste_quantity;
	}

	public long getForeignItemId() {
		return foreignItemId;
	}

	public void setForeignItemId(long foreignItemId) {
		this.foreignItemId = foreignItemId;
	}

	public long getTransferId() {
		return transferId;
	}

	public void setTransferId(long transferId) {
		this.transferId = transferId;
	}
}
