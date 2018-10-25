package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.CURRENCY)
public class CurrencyModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -64928555663462342L;

	public CurrencyModel() {
		super();
	}

	public CurrencyModel(long id) {
		super();
		this.id = id;
	}

	public CurrencyModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "currency_id")
	private long id;

	@Column(name = "currency_name", length = 100)
	private String name;

	@Column(name = "currency_code", length = 100)
	private String code;

	@Column(name = "currency_symbol")
	private String symbol;

	@Column(name = "integer_part")
	private String integer_part;

	@Column(name = "fractional_part")
	private String fractional_part;

	@Column(name = "no_of_precisions")
	private int no_of_precisions;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getInteger_part() {
		return integer_part;
	}

	public void setInteger_part(String integer_part) {
		this.integer_part = integer_part;
	}

	public String getFractional_part() {
		return fractional_part;
	}

	public void setFractional_part(String fractional_part) {
		this.fractional_part = fractional_part;
	}

	public int getNo_of_precisions() {
		return no_of_precisions;
	}

	public void setNo_of_precisions(int no_of_precisions) {
		this.no_of_precisions = no_of_precisions;
	}

}
