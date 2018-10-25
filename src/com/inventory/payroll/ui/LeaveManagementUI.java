package com.inventory.payroll.ui;

import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

/**
 * @author sangeeth
 * @date 12-Nov-2015
 * @Project REVERP
 */

public class LeaveManagementUI extends SparkLogic {

	private static final long serialVersionUID = 6203609379908220864L;

	private STabSheet tab;
	private SPanel applyPanel;
	private SPanel approvePanel;

	@Override
	public SPanel getGUI() {

		SPanel pan = new SPanel();
		pan.setSizeFull();
		setSize(1200, 580);

		SFormLayout mainLay = new SFormLayout();
		mainLay.setMargin(true);

		pan.setContent(mainLay);

		applyPanel=new LeaveApplyUI().getGUI();
		applyPanel.setId("Apply Leave");
		approvePanel=new LeaveApproveUI().getGUI();
		approvePanel.setId("Approve Leave");
		
		tab=new STabSheet();
		tab.addTab(applyPanel,"Apply Leave");
		tab.addTab(approvePanel,"Approve Leave");
		
//		if(!isAdmin())
//			approvePanel.setEnabled(false);
		
		mainLay.addComponent(tab);
		
		tab.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if(tab.getSelectedTab().getId().equals("Approve Leave")){
					approvePanel=new LeaveApproveUI().getGUI();
				}else{
					applyPanel=new LeaveApplyUI().getGUI();
				}
				
			}
		});

		return pan;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public STabSheet getTab() {
		return tab;
	}
}
