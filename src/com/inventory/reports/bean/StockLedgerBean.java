package com.inventory.reports.bean;

import java.io.Serializable;
import java.util.Date;



public class StockLedgerBean implements Serializable{	
	
	private static final long serialVersionUID = 4812994229875017997L;
	//private Date date;
	private String item;
	private String dateString;
	private String ledger;
	private String comments;
	private double receivedQty;
	private double issuedQty;
	private double balanceQty;
	private Date date;
//	private long itemId;
/*	private long parentId;
	private long subGroupId;
	
	public boolean equals(Object obj){
		StockLedgerBean bean = (StockLedgerBean)obj;
		if(this.parentId == bean.parentId && this.subGroupId == bean.subGroupId){
			return true;
		}
		return false;		
	}*/
	
	
	public StockLedgerBean() {
		super();
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public StockLedgerBean(String item,String dateString, String ledger, String comments, double receivedQty, double issuedQty) {
		super();
		//this.date = date;
		this.item = item;
		this.dateString = dateString;
		this.ledger = ledger;
		this.comments = comments;
		this.receivedQty = receivedQty;
		this.issuedQty = issuedQty;	
		
	}
	public StockLedgerBean(String item,String dateString, String ledger, String comments, double receivedQty, double issuedQty,Date date) {
		super();
		//this.date = date;
		this.item = item;
		this.dateString = dateString;
		this.ledger = ledger;
		this.comments = comments;
		this.receivedQty = receivedQty;
		this.issuedQty = issuedQty;	
		this.date = date;	
	}


/*	public Date getDate() {		
		return CommonUtil.getSQLDateFromUtilDate(new java.util.Date(dateString));
	}

	public void setDate(Date date) {
		this.date = date;
	}*/

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public String getLedger() {
		return ledger;
	}

	public void setLedger(String ledger) {
		this.ledger = ledger;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getReceivedQty() {
		return receivedQty;
	}

	public void setReceivedQty(double receivedQty) {
		this.receivedQty = receivedQty;
	}

	public double getIssuedQty() {
		return issuedQty;
	}

	public void setIssuedQty(double issuedQty) {
		this.issuedQty = issuedQty;
	}

	public double getBalanceQty() {
		return balanceQty;
	}

	public void setBalanceQty(double balanceQty) {
		this.balanceQty = balanceQty;
	}
//	@Override
//	public int compareTo(StockLedgerBean obj) {	
//		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD", Locale.ENGLISH);
//		Date currentObjDate = null;
//		Date objDate = null;
//		try {
//			currentObjDate = format.parse(this.getDateString());
//			objDate = format.parse(obj.getDateString());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		return currentObjDate.compareTo(objDate);
//	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	/*public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(long subGroupId) {
		this.subGroupId = subGroupId;
	}
	*/
	
	
		
	
}
