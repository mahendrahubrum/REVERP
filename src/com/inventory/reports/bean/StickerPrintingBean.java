package com.inventory.reports.bean;

import java.util.List;


public class StickerPrintingBean {
	private List<StickerSubReportBean> details;
	private String title;

	public StickerPrintingBean() {
		// TODO Auto-generated constructor stub
	}

	// public StickerPrintingBeanWithList(List<StickerSubReportBean> details,
	// String title) {
	// this.details = details;
	// this.title = title;
	// }
	//
	 public List<StickerSubReportBean> getDetails() {
	 return details;
	 }
	
	 public void setDetails(List<StickerSubReportBean> details) {
	 this.details = details;
	 }
	//
	// public String getTitle() {
	// return title;
	// }
	//
	// public void setTitle(String title) {
	// this.title = title;
	// }
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}


}
