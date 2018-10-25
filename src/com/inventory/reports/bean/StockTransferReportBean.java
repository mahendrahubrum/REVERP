package com.inventory.reports.bean;

public class StockTransferReportBean {
	private String date;
	private String to_office;
	private String from_location;
	private String to_location;
	private String items;
	private String transfer_no;
	
	public StockTransferReportBean(String transfer_no, String date, String to_office, String from_location, String to_location, String items) {
		super();
		this.transfer_no = transfer_no;
		this.date = date;
		this.to_office = to_office;
		this.from_location = from_location;
		this.to_location = to_location;
		this.items = items;
	}
	
	
	public String getTransfer_no() {
		return transfer_no;
	}


	public void setTransfer_no(String transfer_no) {
		this.transfer_no = transfer_no;
	}


	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTo_office() {
		return to_office;
	}
	public void setTo_office(String to_office) {
		this.to_office = to_office;
	}
	public String getFrom_location() {
		return from_location;
	}
	public void setFrom_location(String from_location) {
		this.from_location = from_location;
	}
	public String getTo_location() {
		return to_location;
	}
	public void setTo_location(String to_location) {
		this.to_location = to_location;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	
	
}
