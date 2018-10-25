package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 23, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TRANSFER_STOCK_MAP)
public class TransferStockMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8970270668022504268L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "trnasfer_id")
	private long trnasfer_id;

	@OneToOne
	@JoinColumn(name = "stock_id")
	private ItemStockModel stock;

	public TransferStockMap() {
		super();
	}

	public TransferStockMap(long trnasfer_id, ItemStockModel stock) {
		super();
		this.trnasfer_id = trnasfer_id;
		this.stock = stock;
	}

	public long getTrnasfer_id() {
		return trnasfer_id;
	}

	public void setTrnasfer_id(long trnasfer_id) {
		this.trnasfer_id = trnasfer_id;
	}

	public ItemStockModel getStock() {
		return stock;
	}

	public void setStock(ItemStockModel stock) {
		this.stock = stock;
	}

}
