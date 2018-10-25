package com.inventory.reports.bean;

import java.io.Serializable;

public class CostCenterBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private double debitAmount;
	private double creditAmount;
	private double balance;
	private long parentId;
	private int typeId;
	public CostCenterBean() {
		
	}
	public CostCenterBean(String name, double debitAmount, double creditAmount, long parentId){
		this.name = name;
		this.debitAmount = debitAmount;
		this.creditAmount = creditAmount;
		this.parentId = parentId;
	}
	public CostCenterBean(double debitAmount, double creditAmount){
		this.debitAmount = debitAmount;
		this.creditAmount = creditAmount;
	//	this.parentId = parentId;
	}
	public CostCenterBean(String name, double debitAmount, double creditAmount){
		this.name = name;
		this.debitAmount = debitAmount;
		this.creditAmount = creditAmount;
	//	this.parentId = parentId;
	}
	
	@Override
	public boolean equals(Object obj) {
		CostCenterBean bean = (CostCenterBean) obj;
		return (this.id == bean.getId());
	}
	@Override
	public int hashCode() {
		return (int) this.id;
	}
	
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(double debitAmount) {
		this.debitAmount = debitAmount;
	}
	public double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	

}
