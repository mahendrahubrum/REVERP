package com.inventory.commissionsales.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 14, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_COMMISSION_PAYMENT)
public class CommissionPaymentModel implements Serializable {

	private static final long serialVersionUID = -3463610266611215001L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "number")
	private long number;
	
	@Column(name = "purchase_id")
	private long purchaseId;
	
	@Column(name = "supplier_id")
	private long supplierId;
	
	@Column(name = "from_account")
	private long fromAccount;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "gross_sale")
	private double gross_sale;

	@Column(name = "less_expense")
	private double less_expense;

	@Column(name = "net_sale")
	private double net_sale;

	@Column(name = "freight")
	private double freight;

	@Column(name = "airport_charges")
	private double airport_charges;

	@Column(name = "waste")
	private double waste;

	@Column(name = "dpa_charges")
	private double dpa_charges;

	@Column(name = "pickup_charge")
	private double pickup_charge;

	@Column(name = "unloading_charge")
	private double unloading_charge;

	@Column(name = "storage_charge")
	private double storage_charge;

	@Column(name = "port")
	private double port;

	@Column(name = "auction")
	private double auction;

	@Column(name = "commission")
	private double commission;

	@Column(name = "details", length = 500)
	private String details;

	public CommissionPaymentModel() {
		super();
	}

	public CommissionPaymentModel(long id, String details) {
		super();
		this.id=id;
		this.details=details;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public long getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(long purchaseId) {
		this.purchaseId = purchaseId;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getGross_sale() {
		return gross_sale;
	}

	public void setGross_sale(double gross_sale) {
		this.gross_sale = gross_sale;
	}

	public double getLess_expense() {
		return less_expense;
	}

	public void setLess_expense(double less_expense) {
		this.less_expense = less_expense;
	}

	public double getNet_sale() {
		return net_sale;
	}

	public void setNet_sale(double net_sale) {
		this.net_sale = net_sale;
	}

	public double getFreight() {
		return freight;
	}

	public void setFreight(double freight) {
		this.freight = freight;
	}

	public double getAirport_charges() {
		return airport_charges;
	}

	public void setAirport_charges(double airport_charges) {
		this.airport_charges = airport_charges;
	}

	public double getWaste() {
		return waste;
	}

	public void setWaste(double waste) {
		this.waste = waste;
	}

	public double getDpa_charges() {
		return dpa_charges;
	}

	public void setDpa_charges(double dpa_charges) {
		this.dpa_charges = dpa_charges;
	}

	public double getPickup_charge() {
		return pickup_charge;
	}

	public void setPickup_charge(double pickup_charge) {
		this.pickup_charge = pickup_charge;
	}

	public double getUnloading_charge() {
		return unloading_charge;
	}

	public void setUnloading_charge(double unloading_charge) {
		this.unloading_charge = unloading_charge;
	}

	public double getStorage_charge() {
		return storage_charge;
	}

	public void setStorage_charge(double storage_charge) {
		this.storage_charge = storage_charge;
	}

	public double getPort() {
		return port;
	}

	public void setPort(double port) {
		this.port = port;
	}

	public double getAuction() {
		return auction;
	}

	public void setAuction(double auction) {
		this.auction = auction;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(long supplierId) {
		this.supplierId = supplierId;
	}

	public long getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(long fromAccount) {
		this.fromAccount = fromAccount;
	}

}
