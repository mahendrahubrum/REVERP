package com.inventory.transaction.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 24, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TRANSACTION_DETAILS)
public class TransactionDetailsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7429567746163784678L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "si_no")
	private int si_no;

	@OneToOne
	@JoinColumn(name = "from_acct")
	private LedgerModel fromAcct;

	@OneToOne
	@JoinColumn(name = "to_acct")
	private LedgerModel toAcct;

	@Column(name = "type")
	private int type;

	@Column(name = "amount")
	private double amount;

	@Column(name = "narration", length = 100)
	private String narration;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long departmentId;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long divisionId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSi_no() {
		return si_no;
	}

	public void setSi_no(int si_no) {
		this.si_no = si_no;
	}

	public LedgerModel getFromAcct() {
		return fromAcct;
	}

	public void setFromAcct(LedgerModel fromAcct) {
		this.fromAcct = fromAcct;
	}

	public LedgerModel getToAcct() {
		return toAcct;
	}

	public void setToAcct(LedgerModel toAcct) {
		this.toAcct = toAcct;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	public long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(long divisionId) {
		this.divisionId = divisionId;
	}

}
