package com.inventory.expenditureposting.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 3, 2013
 */

@Entity
@Table(name = SConstants.tb_names.EXPENDETURE_PAYMENT_SETUP)
public class ExpenditurePaymentSetupModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2841579076377740806L;

	public ExpenditurePaymentSetupModel() {
		super();
	}

	public ExpenditurePaymentSetupModel(long id) {
		super();
		this.id = id;
	}

	public ExpenditurePaymentSetupModel(long id, String group_name) {
		super();
		this.id = id;
		this.group_name = group_name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "group_name")
	private String group_name;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "status")
	private long status;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "expendituree_payment_setup_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<ExpenditurePaymentSetupDetailsModel> details_list = new ArrayList<ExpenditurePaymentSetupDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public List<ExpenditurePaymentSetupDetailsModel> getDetails_list() {
		return details_list;
	}

	public void setDetails_list(
			List<ExpenditurePaymentSetupDetailsModel> details_list) {
		this.details_list = details_list;
	}

}
