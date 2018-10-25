package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 18, 2013
 */
public class TrialBalanceBean {

	private String date;
	private String ledgerName,groupName;
	private double creditAmount,debitAmount,amount;
	private int type;
	private int level;
	private long ledgerId;
	private long groupId;
	private long id;
	private long parentId;
	private long classId;
	
	public TrialBalanceBean() {
	}
	
	public TrialBalanceBean(String ledgerName, String groupName,
			double creditAmount, double debitAmount, double amount, int type,
			int level, long ledgerId, long groupId, long parentId) {
		super();
		this.ledgerName = ledgerName;
		this.groupName = groupName;
		this.creditAmount = creditAmount;
		this.debitAmount = debitAmount;
		this.amount = amount;
		this.type = type;
		this.level = level;
		this.ledgerId = ledgerId;
		this.groupId = groupId;
		this.parentId = parentId;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}
	public double getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(double debitAmount) {
		this.debitAmount = debitAmount;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getLedgerId() {
		return ledgerId;
	}
	public void setLedgerId(long ledgerId) {
		this.ledgerId = ledgerId;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public long getClassId() {
		return classId;
	}

	public void setClassId(long classId) {
		this.classId = classId;
	}
}
