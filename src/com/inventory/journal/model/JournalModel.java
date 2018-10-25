package com.inventory.journal.model;

import java.io.Serializable;
import java.sql.Date;
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

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_JOURNAL)
public class JournalModel implements Serializable {

	public JournalModel() {
		super();
	}

	public JournalModel(long id) {
		super();
		this.id = id;
	}

	public JournalModel(long id, String bill_no) {
		super();
		this.id = id;
		this.bill_no = bill_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "ref_no")
	private String ref_no;

	@Column(name = "bill_no", columnDefinition="varchar(100) default ''", nullable=false)
	private String bill_no;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "office_id")
	private long office_id;
	
	@Column(name = "login_id")
	private long login_id;
	
	@Column(name = "transaction_id")
	private long transaction_id;
	
	@Column(name = "active", columnDefinition="boolean default true", nullable=false)
	private boolean active;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "journal_details_link", joinColumns = { @JoinColumn(name = "journal_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<JournalDetailsModel> journal_details_list = new ArrayList<JournalDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public List<JournalDetailsModel> getJournal_details_list() {
		return journal_details_list;
	}

	public void setJournal_details_list(
			List<JournalDetailsModel> journal_details_list) {
		this.journal_details_list = journal_details_list;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	
}
