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
 * @author Jinshad P.T.
 * 
 *         Nov 22, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TRANSPORTATION_PAYMENT)
public class TransportationPaymentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4757883860009841529L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "transportation_id")
	private long transportation_id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "payment_id")
	private long payment_id;

	@Column(name = "from_account_id")
	private long from_account_id;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "sales_person", columnDefinition="bigint default 0", nullable=false)
	private long sales_person;
	
	@Column(name = "cheque_date", columnDefinition = "date default '2014-06-01'", nullable = false)
	private Date cheque_date;
	
	@Column(name = "from_date", columnDefinition = "date default '2014-07-01'", nullable = false)
	private Date from_date;
	
	@Column(name = "to_date", columnDefinition = "date default '2014-07-01'", nullable = false)
	private Date to_date;

	@Column(name = "description", length = 400)
	private String description;

	@Column(name = "type")
	private int type;

	@Column(name = "supplier_amount")
	private double supplier_amount;

	@Column(name = "discount")
	private double discount;

	@Column(name = "payment_amount")
	private double payment_amount;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@Column(name = "cash_or_check", columnDefinition = "int default 1", nullable = false)
	private int cash_or_check;

	@Column(name = "bill_no")
	private String billNo;

	@Column(name = "place")
	private String place;

	@Column(name = "invoice_amount", columnDefinition = "double default 0", nullable = false)
	private double invoiceAmount;

	public TransportationPaymentModel() {
		super();
	}

	public TransportationPaymentModel(long id, String description) {
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

	public long getTransportation_id() {
		return transportation_id;
	}

	public void setTransportation_id(long transportation_id) {
		this.transportation_id = transportation_id;
	}

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

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Date getCheque_date() {
		return cheque_date;
	}

	public void setCheque_date(Date cheque_date) {
		this.cheque_date = cheque_date;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public Date getTo_date() {
		return to_date;
	}

	public void setTo_date(Date to_date) {
		this.to_date = to_date;
	}

	public long getSales_person() {
		return sales_person;
	}

	public void setSales_person(long sales_person) {
		this.sales_person = sales_person;
	}

}
