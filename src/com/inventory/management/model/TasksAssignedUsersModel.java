package com.inventory.management.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;

@Entity
@Table(name = SConstants.tb_names.I_TASKS_ASSIGNED_USERS)
public class TasksAssignedUsersModel implements Serializable {

	private static final long serialVersionUID = 5179902910250160995L;

	public TasksAssignedUsersModel(S_LoginModel user) {
		super();
		this.user=user;
	}

	public TasksAssignedUsersModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "fk_user_id")
	private S_LoginModel user;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_LoginModel getUser() {
		return user;
	}

	public void setUser(S_LoginModel user) {
		this.user = user;
	}

}
