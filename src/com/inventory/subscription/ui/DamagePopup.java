package com.inventory.subscription.ui;

import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;

@SuppressWarnings("serial")
public class DamagePopup extends SparkLogic{

	SPanel mainPanel;
	SFormLayout form;
	SHorizontalLayout buttonLayout;
	STable table;
	private Object[] allHeaders,visibleHeaders;
	static String TBC_PID = "Parent Id";
	static String TBC_CID = "Child Id";
	static String TBC_SELECT = "";
	static String TBC_NAME = "Expenditure";
	static String TBC_DETAILS = "Details";
	@Override
	public SPanel getGUI() {
		try{
			mainPanel=new SPanel();
			setSize(500, 350);
			form=new SFormLayout();
			form.setSpacing(true);
			allHeaders = new String[] {TBC_PID, TBC_CID, TBC_SELECT,TBC_NAME,TBC_DETAILS};
			visibleHeaders = new String[] {TBC_SELECT,TBC_NAME,TBC_DETAILS};
			
		}
		catch(Exception e){
			
		}
		return null;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
