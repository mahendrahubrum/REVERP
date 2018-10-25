package com.inventory.fixedasset.model;

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
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_FIXED_ASSET_PURCHASE)
public class FixedAssetPurchaseModel implements Serializable{
	
	public FixedAssetPurchaseModel() {

	}
	
	public FixedAssetPurchaseModel(long id, String assetNo) {
		this.id = id;
		this.assetNo = assetNo;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "asset_no")
	private String assetNo;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "ref_no")
	private String ref_no;
	
	@Column(name = "supplier")
	private String supplier;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@Column(name = "cash_or_cheque")
	private long cashOrCheque;
	
	@Column(name = "payingAmount")
	private double payingAmount;
	
	@Column(name = "net_price")
	private double netPrice;
	
	@Column(name = "currency_id")
	private long currencyId;
	
	@Column(name = "conversion_rate")
	private double conversionRate;
	
	@Column(name = "transaction_id")
	private long transactionId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "i_fixed_asset_details_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "asset_details_id") })
	private List<FixedAssetPurchaseDetailsModel> fixed_asset_purchase_details_list = new ArrayList<FixedAssetPurchaseDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getCashOrCheque() {
		return cashOrCheque;
	}

	public void setCashOrCheque(long cashOrCheque) {
		this.cashOrCheque = cashOrCheque;
	}

	public double getPayingAmount() {
		return payingAmount;
	}

	public void setPayingAmount(double payingAmount) {
		this.payingAmount = payingAmount;
	}

	public double getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(double netPrice) {
		this.netPrice = netPrice;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public List<FixedAssetPurchaseDetailsModel> getFixed_asset_purchase_details_list() {
		return fixed_asset_purchase_details_list;
	}

	public void setFixed_asset_purchase_details_list(
			List<FixedAssetPurchaseDetailsModel> fixed_asset_purchase_details_list) {
		this.fixed_asset_purchase_details_list = fixed_asset_purchase_details_list;
	}
	
}
