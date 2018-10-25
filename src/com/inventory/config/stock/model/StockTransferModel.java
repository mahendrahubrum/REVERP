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

import com.inventory.model.LocationModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 19, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_STOCK_TRANSFER)
public class StockTransferModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074101965619731348L;

	public StockTransferModel(long id) {
		super();
		this.id = id;
	}

	public StockTransferModel() {
		super();
	}

	public StockTransferModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "transfer_no",nullable = false)
	private long transfer_no;

	@Column(name = "transfer_date",nullable = false)
	private Date transfer_date;
	
	@OneToOne
	@JoinColumn(name = "from_office_id")
	private S_OfficeModel from_office;
	
	@OneToOne
	@JoinColumn(name = "from_location_id")
	private LocationModel from_location;
	
	@OneToOne
	@JoinColumn(name = "to_office_id")
	private S_OfficeModel to_office;
	
	@OneToOne
	@JoinColumn(name = "to_location_id")
	private LocationModel to_location;
	
	@Column(name = "comments")
	private String comments;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@Column(name = "status")
	private short status;	

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = SConstants.tb_names.I_STOCK_TRANSFER_LINK, joinColumns = { @JoinColumn(name = "transfer_master") }, inverseJoinColumns = { @JoinColumn(name = "inv_id") })
	private List<StockTransferInventoryDetails> inventory_details_list = new ArrayList<StockTransferInventoryDetails>();

	

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

	public Date getTransfer_date() {
		return transfer_date;
	}

	public void setTransfer_date(Date transfer_date) {
		this.transfer_date = transfer_date;
	}

	public S_OfficeModel getFrom_office() {
		return from_office;
	}

	public void setFrom_office(S_OfficeModel from_office) {
		this.from_office = from_office;
	}

	public LocationModel getFrom_location() {
		return from_location;
	}

	public void setFrom_location(LocationModel from_location) {
		this.from_location = from_location;
	}

	public S_OfficeModel getTo_office() {
		return to_office;
	}

	public void setTo_office(S_OfficeModel to_office) {
		this.to_office = to_office;
	}

	public LocationModel getTo_location() {
		return to_location;
	}

	public void setTo_location(LocationModel to_location) {
		this.to_location = to_location;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public List<StockTransferInventoryDetails> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<StockTransferInventoryDetails> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

}
