package com.inventory.reports.bean;

import java.util.List;

public class AcctReportSubBean {

	private long id, transaction_id;
	private int count;
	private String dispaly_name, name, particulars;

	private double debit, credit, amount, quantity, current_debit,
			current_credit;
	private double opening_debit, opening_credit, balance_debit,
			balance_credit;

	private List subList;

	
	
	public AcctReportSubBean() {
		super();
	}
	
	
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDispaly_name() {
		return dispaly_name;
	}

	public void setDispaly_name(String dispaly_name) {
		this.dispaly_name = dispaly_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getDebit() {
		return debit;
	}

	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List getSubList() {
		return subList;
	}

	public void setSubList(List subList) {
		this.subList = subList;
	}

	public double getCurrent_debit() {
		return current_debit;
	}

	public void setCurrent_debit(double current_debit) {
		this.current_debit = current_debit;
	}

	public double getCurrent_credit() {
		return current_credit;
	}

	public void setCurrent_credit(double current_credit) {
		this.current_credit = current_credit;
	}

	public double getOpening_debit() {
		return opening_debit;
	}

	public void setOpening_debit(double opening_debit) {
		this.opening_debit = opening_debit;
	}

	public double getOpening_credit() {
		return opening_credit;
	}

	public void setOpening_credit(double opening_credit) {
		this.opening_credit = opening_credit;
	}

	public double getBalance_debit() {
		return balance_debit;
	}

	public void setBalance_debit(double balance_debit) {
		this.balance_debit = balance_debit;
	}

	public double getBalance_credit() {
		return balance_credit;
	}

	public void setBalance_credit(double balance_credit) {
		this.balance_credit = balance_credit;
	}

}
