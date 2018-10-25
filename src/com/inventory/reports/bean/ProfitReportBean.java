package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 28, 2013
 */
public class ProfitReportBean {

	private double purchase, purchase_return,income;
	private double sales, sales_return;
	private double profit, transportation, expendetures, net_profit,
			stock_amount;

	public ProfitReportBean(double purchase, double purchase_return,
			double sales, double sales_return, double transportation,
			double expendetures, double net_profit) {
		super();
		this.purchase = purchase;
		this.purchase_return = purchase_return;
		this.sales = sales;
		this.sales_return = sales_return;
		this.transportation = transportation;
		this.expendetures = expendetures;
		this.net_profit = net_profit;
	}
	
	public ProfitReportBean(double purchase, double purchase_return,
			double sales, double sales_return, double transportation,
			double expendetures, double net_profit, double stock_amount) {
		super();
		this.purchase = purchase;
		this.purchase_return = purchase_return;
		this.sales = sales;
		this.sales_return = sales_return;
		this.transportation = transportation;
		this.expendetures = expendetures;
		this.net_profit = net_profit;
		this.stock_amount = stock_amount;
	}
	
	public ProfitReportBean(double purchase, double purchase_return,
			double sales, double sales_return, double transportation,
			double expendetures, double income, double net_profit, double stock_amount) {
		super();
		this.purchase = purchase;
		this.purchase_return = purchase_return;
		this.sales = sales;
		this.sales_return = sales_return;
		this.transportation = transportation;
		this.expendetures = expendetures;
		this.net_profit = net_profit;
		this.stock_amount = stock_amount;
		this.income=income;
	}

	public ProfitReportBean(double purchase, double sales, double profit) {
		super();
		this.purchase = purchase;
		this.sales = sales;
		this.profit = profit;
	}

	public double getPurchase() {
		return purchase;
	}

	public void setPurchase(double purchase) {
		this.purchase = purchase;
	}

	public double getSales() {
		return sales;
	}

	public void setSales(double sales) {
		this.sales = sales;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public double getTransportation() {
		return transportation;
	}

	public void setTransportation(double transportation) {
		this.transportation = transportation;
	}

	public double getNet_profit() {
		return net_profit;
	}

	public void setNet_profit(double net_profit) {
		this.net_profit = net_profit;
	}

	public double getPurchase_return() {
		return purchase_return;
	}

	public void setPurchase_return(double purchase_return) {
		this.purchase_return = purchase_return;
	}

	public double getSales_return() {
		return sales_return;
	}

	public void setSales_return(double sales_return) {
		this.sales_return = sales_return;
	}

	public double getExpendetures() {
		return expendetures;
	}

	public void setExpendetures(double expendetures) {
		this.expendetures = expendetures;
	}

	public double getStock_amount() {
		return stock_amount;
	}

	public void setStock_amount(double stock_amount) {
		this.stock_amount = stock_amount;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

}
