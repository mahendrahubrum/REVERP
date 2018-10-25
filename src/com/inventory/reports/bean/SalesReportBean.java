package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 12, 2013
 */
public class SalesReportBean {

	String date = "";
	private String paymentDate = "";
	String customer = "";
	String salesNo = "";
	private String tableNo = "";
	String office = "";
	String items = "";
	String salesman = "";
	private String group = "";
	private String status;
	double amount = 0, payment_amt=0,pending=0;
	private String room_no;
	private double quantity;
	private long groupId;
	private long salesManId;
	private long customerId;
	private String currency;
	private long currencyId;
	
	private long deliveryNoteId;
	String deliveryNoteNo = "";
	
	private long itemId;
	String itemCode = "";
	
	private long unitId;
	String unit = "";
	
	private double stockIn;
	private double stockOut;
	
	private long id;
	
	

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public SalesReportBean() {
		super();
	}
	
	//Constructor #1
	public SalesReportBean(String date, String customer, String salesNo,
			String office, String items, double amount, String salesman) {
		super();
		this.date = date;
		this.customer = customer;
		this.salesNo = salesNo;
		this.office = office;
		this.items = items;
		this.amount = amount;
		this.setSalesman(salesman);
	}
	
	//Constructor #2
	public SalesReportBean(String date, String customer, String salesNo,
			String office, String items, double amount, String salesman,
			double payment_amt) {
		super();
		this.date = date;
		this.customer = customer;
		this.salesNo = salesNo;
		this.office = office;
		this.items = items;
		this.amount = amount;
		this.payment_amt = payment_amt;
		this.setSalesman(salesman);
	}
	
	//Constructor #3
	public SalesReportBean(String date, String customer, String salesNo,
			String office, String items, double amount, String salesman,
			double payment_amt,double pending) {
		super();
		this.date = date;
		this.customer = customer;
		this.salesNo = salesNo;
		this.office = office;
		this.items = items;
		this.amount = amount;
		this.payment_amt = payment_amt;
		this.setSalesman(salesman);
		this.pending=pending;
	}
	
	//Constructor #4
	public SalesReportBean(String date, String customer, String salesNo,
			String office, String items, double amount, String salesman,
			double payment_amt,double pending,String group) {
		super();
		this.date = date;
		this.customer = customer;
		this.salesNo = salesNo;
		this.office = office;
		this.items = items;
		this.amount = amount;
		this.payment_amt = payment_amt;
		this.setSalesman(salesman);
		this.pending=pending;
		this.group=group;
	}
	
	//Constructor #5
	public SalesReportBean(String date, String customer, String salesNo,
			String office, String items, double amount, String salesman, String room_no) {
			super();
			this.date = date;
			this.customer = customer;
			this.salesNo = salesNo;
			this.office = office;
			this.items = items;
			this.amount = amount;
			this.salesman = salesman;
			this.setRoom_no(room_no);
			}
	
	
	//Constructor #6
	public SalesReportBean(String date, String customer, String salesNo,
			String office, String items, double amount, String salesman, String room_no, double quantity) {
			super();
			this.date = date;
			this.customer = customer;
			this.salesNo = salesNo;
			this.office = office;
			this.items = items;
			this.amount = amount;
			this.salesman = salesman;
			this.setRoom_no(room_no);
			this.setQuantity(quantity);
			}
	
	//Constructor #7
	public SalesReportBean(long groupId, String group, double amount,	double payment_amt,double pending) {
			super();
			this.groupId = groupId;
			this.group = group;
			this.amount = amount;
			this.payment_amt = payment_amt;
			this.pending=pending;
		}

	//Constructor #8
	public SalesReportBean(String salesman, String customer, double amount,	double payment_amt,double pending) {
		super();
		this.salesman = salesman;
		this.customer = customer;
		this.amount = amount;
		this.payment_amt = payment_amt;
		this.pending=pending;
	}
	
	//Constructor #9
	public SalesReportBean(String salesman,long salesManId,long customerId, String customer, double amount,	double payment_amt,double pending) {
		super();
		this.salesman = salesman;
		this.customer = customer;
		this.amount = amount;
		this.payment_amt = payment_amt;
		this.pending=pending;
		this.salesManId=salesManId;
		this.customerId=customerId;
	}
	
	//Constructor #10
	public SalesReportBean(long customerId, String customer, double amount) {
		super();
		this.customerId = customerId;
		this.customer = customer;
		this.amount = amount;
	}
	
	//Constructor #11
		public SalesReportBean(long customerId, String customer, double amount, long currencyId) {
			super();
			this.customerId = customerId;
			this.customer = customer;
			this.amount = amount;			
//			this.amountInBaseCurrency = amountInBaseCurrency;
			this.currencyId = currencyId;
		}
		
		public SalesReportBean(long deliveryNoteId,String deliveryNoteNo,
				long itemId,String itemName,String itemCode,long unitId,String unit,
				double stockOut,double quantity,double stockIn) {
			super();
			this.deliveryNoteId = deliveryNoteId;
			this.deliveryNoteNo = deliveryNoteNo;
			this.items = itemName;
			this.itemId = itemId;
			this.itemCode = itemCode;
			this.unitId = unitId;
			this.unit = unit;
			this.stockOut = stockOut;
			this.quantity = quantity;
			this.stockIn = stockIn;
			
		}


	public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getPayment_amt() {
		return payment_amt;
	}

	public void setPayment_amt(double payment_amt) {
		this.payment_amt = payment_amt;
	}

	public String getRoom_no() {
		return room_no;
	}

	public void setRoom_no(String room_no) {
		this.room_no = room_no;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPending() {
		return pending;
	}

	public void setPending(double pending) {
		this.pending = pending;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public long getSalesManId() {
		return salesManId;
	}

	public void setSalesManId(long salesManId) {
		this.salesManId = salesManId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getTableNo() {
		return tableNo;
	}

	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}

	public long getDeliveryNoteId() {
		return deliveryNoteId;
	}

	public void setDeliveryNoteId(long deliveryNoteId) {
		this.deliveryNoteId = deliveryNoteId;
	}

	public String getDeliveryNoteNo() {
		return deliveryNoteNo;
	}

	public void setDeliveryNoteNo(String deliveryNoteNo) {
		this.deliveryNoteNo = deliveryNoteNo;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public long getUnitId() {
		return unitId;
	}

	public void setUnitId(long unitId) {
		this.unitId = unitId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getStockIn() {
		return stockIn;
	}

	public void setStockIn(double stockIn) {
		this.stockIn = stockIn;
	}

	public double getStockOut() {
		return stockOut;
	}

	public void setStockOut(double stockOut) {
		this.stockOut = stockOut;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	
	
	
}
