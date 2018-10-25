package com.inventory.payment.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 23, 2013
 */
@Entity
@Table(name = SConstants.tb_names.I_PAYMENT)
public class PaymentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9112662340340281011L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "payment_id")
	private long payment_id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "from_account_id")
	private long from_account_id;

	@Column(name = "to_account_id")
	private long to_account_id;

	// @Column(name = "purchase_id")
	// private long purchase_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "cheque_date")
	private Date cheque_date;

	@Column(name = "description", length = 400)
	private String description;
	
	@Column(name = "cheque_no", length = 50)
	private String chequeNo;

	@Column(name = "type")
	private int type;

	@Column(name = "cash_or_check", columnDefinition = "int default 1", nullable = false)
	private int cash_or_check;

	@Column(name = "supplier_amount")
	private double supplier_amount;

	@Column(name = "discount")
	private double discount;

	@Column(name = "payment_amount")
	private double payment_amount;

	// Added by Jinshad
	@Column(name = "sales_ids", length = 1000)
	private String sales_ids;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "from_date")
	private Date fromDate;
	
	@Column(name = "to_date")
	private Date toDate;

	public PaymentModel() {
		super();
	}

	public PaymentModel(long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public long getFrom_account_id() {
		return from_account_id;
	}

	public void setFrom_account_id(long from_account_id) {
		this.from_account_id = from_account_id;
	}

	public long getTo_account_id() {
		return to_account_id;
	}

	public void setTo_account_id(long to_account_id) {
		this.to_account_id = to_account_id;
	}

	// public long getPurchase_id() {
	// return purchase_id;
	// }

	// public void setPurchase_id(long purchase_id) {
	// this.purchase_id = purchase_id;
	// }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getSupplier_amount() {
		return supplier_amount;
	}

	public void setSupplier_amount(double supplier_amount) {
		this.supplier_amount = supplier_amount;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
	}

	public long getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(long payment_id) {
		this.payment_id = payment_id;
	}

	public String getSales_ids() {
		return sales_ids;
	}

	public void setSales_ids(String sales_ids) {
		this.sales_ids = sales_ids;
	}

	public Date getCheque_date() {
		return cheque_date;
	}

	public void setCheque_date(Date cheque_date) {
		this.cheque_date = cheque_date;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCash_or_check() {
		return cash_or_check;
	}

	public void setCash_or_check(int cash_or_check) {
		this.cash_or_check = cash_or_check;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

}
