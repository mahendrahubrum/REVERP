package com.inventory.management.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;

@Entity
@Table(name = SConstants.tb_names.I_TASKS)
public class TasksModel implements Serializable {

	private static final long serialVersionUID = 9064453041502259243L;

	public TasksModel(long id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public TasksModel(long id) {
		super();
		this.id = id;
	}

	public TasksModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "date")
	private Date date;

	@Column(name = "start_time")
	private Timestamp start_time;

	@Column(name = "end_time")
	private Timestamp end_time;

	@Column(name = "actual_completion_time")
	private Timestamp actual_completion_time;

	@Column(name = "hours_taken")
	private int hours_taken;

	@OneToOne
	@JoinColumn(name = "created_by")
	private S_LoginModel created_by;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tast_assigned_user_link", joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<TasksAssignedUsersModel> assignedList = new ArrayList<TasksAssignedUsersModel>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "tast_component_details_link", joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = { @JoinColumn(name = "comp_det_id") })
	private List<TaskComponentDetailsModel> componentDetailsList = new ArrayList<TaskComponentDetailsModel>();

	@Column(name = "status")
	private long status;

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

	public Timestamp getStart_time() {
		return start_time;
	}

	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}

	public Timestamp getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}

	public Timestamp getActual_completion_time() {
		return actual_completion_time;
	}

	public void setActual_completion_time(Timestamp actual_completion_time) {
		this.actual_completion_time = actual_completion_time;
	}

	public int getHours_taken() {
		return hours_taken;
	}

	public void setHours_taken(int hours_taken) {
		this.hours_taken = hours_taken;
	}

	public S_LoginModel getCreated_by() {
		return created_by;
	}

	public void setCreated_by(S_LoginModel created_by) {
		this.created_by = created_by;
	}

	public List<TasksAssignedUsersModel> getAssignedList() {
		return assignedList;
	}

	public void setAssignedList(List<TasksAssignedUsersModel> assignedList) {
		this.assignedList = assignedList;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public List<TaskComponentDetailsModel> getComponentDetailsList() {
		return componentDetailsList;
	}

	public void setComponentDetailsList(
			List<TaskComponentDetailsModel> componentDetailsList) {
		this.componentDetailsList = componentDetailsList;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
