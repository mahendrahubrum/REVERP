package com.inventory.config.stock.ui;

import com.inventory.config.stock.dao.SizeDao;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
public class AddSizeUI extends SparkLogic {

	SPanel mainPanel;
	SFormLayout mainLayout;
	
	SComboField sizeCombo;
	STextField nameField;
	SComboField statusCombo;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton createNewButton;
	
	SizeDao dao;
	
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		setSize(300, 350);
		dao=new SizeDao();
		
		try {
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));
			SHorizontalLayout createLayout=new SHorizontalLayout(getPropertyName("size"));
			createLayout.setSpacing(true);
			sizeCombo=new SComboField(null, 200, null, "id", "name",false,getPropertyName("create_new"));

			createLayout.addComponent(sizeCombo);
			createLayout.addComponent(createNewButton);
			nameField=new STextField(getPropertyName("name"), 200, true);
			nameField.setInputPrompt(getPropertyName("name"));
			statusCombo=new SComboField(getPropertyName("status"), 200, SConstants.statuses.status, "key", "value", true, getPropertyName("select"));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return mainPanel;
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
