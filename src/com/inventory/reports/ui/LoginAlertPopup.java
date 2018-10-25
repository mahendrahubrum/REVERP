package com.inventory.reports.ui;

import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;

public class LoginAlertPopup extends SparkLogic {

	private static final long serialVersionUID = 4409260457103513121L;
	boolean popupEnable = false;

	public LoginAlertPopup() {
		AlertPanel panel=new AlertPanel();
		if(panel.getContent()!=null)
			setContent(panel);
		else
			setContent(null);
	}

	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}