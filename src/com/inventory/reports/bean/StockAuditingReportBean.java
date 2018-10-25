package com.inventory.reports.bean;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 21, 2014
 */
public class StockAuditingReportBean {

	String item = "";
	String purchaseDate = "";
	String supplier = "";
	String purchaseNo = "";
	double purchaseQuantity;
	double purchaseReturnQuantity;
	double purchaseRate;
	private double purchaseNormalRate;

	String salesDate = "";
	String customer = "";
	String salesNo = "";
	private String saleUnit = "";
	private String purchaseUnit = "";
	double salesQuantity;
	double salesReturnQuantity;
	double salesRate;
	private double salesNormalRate;
	long purch_unit, sales_unit, inv_id;

	double profit = 0;

	private String stockIds;

	private Long saleId;
	private Long purchaseId;

	public StockAuditingReportBean() {
		super();
	}

	public StockAuditingReportBean(String salesDate, String stockIds,
			String salesNo, String customer, double salesQuantity,
			double salesRate, Long saleId, long sales_unit, String saleUnit) {
		super();
		this.salesDate = salesDate;
		this.customer = customer;
		this.salesNo = salesNo;
		this.salesQuantity = salesQuantity;
		this.salesRate = salesRate;
		this.saleId = saleId;
		this.setStockIds(stockIds);
		this.sales_unit = sales_unit;
		this.saleUnit = saleUnit;

	}

	public StockAuditingReportBean(String salesDate, String salesNo,
			String customer, double salesQuantity, double salesRate,
			Long saleId, long sales_unit, String saleUnit) {
		super();
		this.salesDate = salesDate;
		this.customer = customer;
		this.salesNo = salesNo;
		this.salesQuantity = salesQuantity;
		this.salesRate = salesRate;
		this.saleId = saleId;
		this.sales_unit = sales_unit;
		this.saleUnit = saleUnit;

	}

	public StockAuditingReportBean(String purchaseDate, String purchaseNo,
			String supplier, double purchaseQuantity, double purchaseRate,
			long purch_unit, String purchaseUnit) {
		super();
		this.purchaseDate = purchaseDate;
		this.supplier = supplier;
		this.purchaseNo = purchaseNo;
		this.purchaseQuantity = purchaseQuantity;
		this.purchaseRate = purchaseRate;
		this.purch_unit = purch_unit;
		this.purchaseUnit = purchaseUnit;
	}

	public StockAuditingReportBean(Long purchaseId, String purchaseDate,
			String purchaseNo, String supplier, double purchaseQuantity,
			double purchaseRate, long purch_unit, String purchaseUnit,
			long inv_id) {
		super();
		this.purchaseDate = purchaseDate;
		this.supplier = supplier;
		this.purchaseNo = purchaseNo;
		this.purchaseQuantity = purchaseQuantity;
		this.purchaseRate = purchaseRate;
		this.purch_unit = purch_unit;
		this.purchaseId = purchaseId;
		this.purchaseUnit = purchaseUnit;
		this.inv_id = inv_id;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}

	public double getPurchaseQuantity() {
		return purchaseQuantity;
	}

	public void setPurchaseQuantity(double purchaseQuantity) {
		this.purchaseQuantity = purchaseQuantity;
	}

	public double getPurchaseReturnQuantity() {
		return purchaseReturnQuantity;
	}

	public void setPurchaseReturnQuantity(double purchaseReturnQuantity) {
		this.purchaseReturnQuantity = purchaseReturnQuantity;
	}

	public double getPurchaseRate() {
		return purchaseRate;
	}

	public void setPurchaseRate(double purchaseRate) {
		this.purchaseRate = purchaseRate;
	}

	public String getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(String salesDate) {
		this.salesDate = salesDate;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSalesNo() {
		return salesNo;
	}

	public void setSalesNo(String salesNo) {
		this.salesNo = salesNo;
	}

	public double getSalesQuantity() {
		return salesQuantity;
	}

	public void setSalesQuantity(double salesQuantity) {
		this.salesQuantity = salesQuantity;
	}

	public double getSalesReturnQuantity() {
		return salesReturnQuantity;
	}

	public void setSalesReturnQuantity(double salesReturnQuantity) {
		this.salesReturnQuantity = salesReturnQuantity;
	}

	public double getSalesRate() {
		return salesRate;
	}

	public void setSalesRate(double salesRate) {
		this.salesRate = salesRate;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public String getStockIds() {
		return stockIds;
	}

	public void setStockIds(String stockIds) {
		this.stockIds = stockIds;
	}

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	public long getPurch_unit() {
		return purch_unit;
	}

	public void setPurch_unit(long purch_unit) {
		this.purch_unit = purch_unit;
	}

	public long getSales_unit() {
		return sales_unit;
	}

	public void setSales_unit(long sales_unit) {
		this.sales_unit = sales_unit;
	}

	public Long getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(Long purchaseId) {
		this.purchaseId = purchaseId;
	}

	public String getPurchaseUnit() {
		return purchaseUnit;
	}

	public void setPurchaseUnit(String purchaseUnit) {
		this.purchaseUnit = purchaseUnit;
	}

	public String getSaleUnit() {
		return saleUnit;
	}

	public void setSaleUnit(String saleUnit) {
		this.saleUnit = saleUnit;
	}

	public long getInv_id() {
		return inv_id;
	}

	public void setInv_id(long inv_id) {
		this.inv_id = inv_id;
	}

	public double getPurchaseNormalRate() {
		return purchaseNormalRate;
	}

	public void setPurchaseNormalRate(double purchaseNormalRate) {
		this.purchaseNormalRate = purchaseNormalRate;
	}

	public double getSalesNormalRate() {
		return salesNormalRate;
	}

	public void setSalesNormalRate(double salesNormalRate) {
		this.salesNormalRate = salesNormalRate;
	}

}
