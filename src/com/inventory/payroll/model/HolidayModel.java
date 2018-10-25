package com.inventory.payroll.model;

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
import com.webspark.uac.model.S_OfficeModel;


/**
 * 
 * @author sangeeth
 * @date 02-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_HOLIDAYS)
public class HolidayModel implements Serializable {

	public HolidayModel() {
		super();
	}
	
	public HolidayModel(long id) {
		super();
		this.id = id;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "title", length = 300)
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "date")
	private Date date;
	
	@OneToOne
	@JoinColumn(name="office")
	private S_OfficeModel office;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

}