package com.webspark.bean;

import com.webspark.model.S_OptionModel;

public class SessionActivityBean {

	private long id;
	private long login,billId;
	private S_OptionModel option;
	private String details;

	public SessionActivityBean() {
		super();
	}

	public SessionActivityBean(long id, S_OptionModel option, String details, long billId) {
		super();
		this.id = id;
		this.option = option;
		this.details = details;
		this.billId = billId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLogin() {
		return login;
	}

	public void setLogin(long login) {
		this.login = login;
	}

	public S_OptionModel getOption() {
		return option;
	}

	public void setOption(S_OptionModel option) {
		this.option = option;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public long getBillId() {
		return billId;
	}

	public void setBillId(long billId) {
		this.billId = billId;
	}

}
