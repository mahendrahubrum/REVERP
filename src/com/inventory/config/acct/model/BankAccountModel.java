package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 15, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_BANK_ACCOUNTS)
public class BankAccountModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7676675346429265494L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "account_no", length = 30)
	private String account_no;

	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;

	@OneToOne
	@JoinColumn(name = "bank_currency")
	private CurrencyModel bank_currency;

	@Column(name = "bank_address", length = 500)
	private String bank_address;

	public BankAccountModel() {
		super();
	}

	public BankAccountModel(long id) {
		super();
		this.id = id;
	}

	public BankAccountModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
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

	public String getAccount_no() {
		return account_no;
	}

	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public CurrencyModel getBank_currency() {
		return bank_currency;
	}

	public void setBank_currency(CurrencyModel bank_currency) {
		this.bank_currency = bank_currency;
	}

	public String getBank_address() {
		return bank_address;
	}

	public void setBank_address(String bank_address) {
		this.bank_address = bank_address;
	}

}