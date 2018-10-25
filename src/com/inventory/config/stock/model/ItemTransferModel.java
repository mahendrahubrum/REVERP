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
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 19, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_ITEM_TRANSFER)
public class ItemTransferModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3871765424517691608L;

	public ItemTransferModel(long id) {
		super();
		this.id = id;
	}

	public ItemTransferModel() {
		super();
	}

	public ItemTransferModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "transfer_id")
	private long transfer_no;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "comments")
	private String comments;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = SConstants.tb_names.I_ITEM_TRANSFER_LINK, joinColumns = { @JoinColumn(name = "transfer_master") }, inverseJoinColumns = { @JoinColumn(name = "inv_id") })
	private List<ItemTransferInventoryDetails> inventory_details_list = new ArrayList<ItemTransferInventoryDetails>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransfer_no() {
		return transfer_no;
	}

	public void setTransfer_no(long transfer_no) {
		this.transfer_no = transfer_no;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<ItemTransferInventoryDetails> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<ItemTransferInventoryDetails> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

}
