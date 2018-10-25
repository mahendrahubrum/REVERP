package com.inventory.reports.bean;

import java.sql.Date;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Apr 28, 2014
 */
public class BudgetReportBean {

	String budgetDefinition;
	String budget;
	String notes;
	Date date;
	Double budgetAmount;
	Double actualAmount;
	Double variationAmount;
	public BudgetReportBean() {
		super();
	}
	public BudgetReportBean(String budgetDefinition, String budget,String notes, Date date,
			Double budgetAmount, Double actualAmount, Double variationAmount) {
		super();
		this.budgetDefinition = budgetDefinition;
		this.budget = budget;
		this.notes = notes;
		this.date = date;
		this.budgetAmount = budgetAmount;
		this.actualAmount = actualAmount;
		this.variationAmount = variationAmount;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getBudgetDefinition() {
		return budgetDefinition;
	}
	public void setBudgetDefinition(String budgetDefinition) {
		this.budgetDefinition = budgetDefinition;
	}
	public String getBudget() {
		return budget;
	}
	public void setBudget(String budget) {
		this.budget = budget;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Double getBudgetAmount() {
		return budgetAmount;
	}
	public void setBudgetAmount(Double budgetAmount) {
		this.budgetAmount = budgetAmount;
	}
	public Double getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(Double actualAmount) {
		this.actualAmount = actualAmount;
	}
	public Double getVariationAmount() {
		return variationAmount;
	}
	public void setVariationAmount(Double variationAmount) {
		this.variationAmount = variationAmount;
	}
	
	
	
}
