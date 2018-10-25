package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * May 8, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_GRADE)
public class GradeModel implements Serializable {

	private static final long serialVersionUID = -9104659040797133352L;

	public GradeModel() {
		super();
	}

	public GradeModel(long id) {
		super();
		this.id = id;
	}

	public GradeModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "code", length = 100)
	private String code;

	@Column(name = "name", length = 200)
	private String name;
	
	@Column(name = "description", length = 300)
	private String description;

	@Column(name = "office_id")
	private long officeId;
	
	@Column(name = "percentage")
	private double percentage;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}
