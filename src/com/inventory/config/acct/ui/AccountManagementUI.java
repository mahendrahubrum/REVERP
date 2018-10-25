package com.inventory.config.acct.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

public class AccountManagementUI extends SparkLogic {
	
	private static final long serialVersionUID = -391670341082501552L;

	long id;
	
	SPanel pannel;
	
	SPanel ledgerPanel;
	SPanel ledgerGroupPanel;
	
	STabSheet tab;
	
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		setSize(500, 430);
		pannel = new SPanel();
		
		tab=new STabSheet(null);
		
		ledgerPanel=new LedgerPanel();
		ledgerGroupPanel=new LedgerGroupPanel();
		
		tab.addTab(ledgerPanel, getPropertyName("account"));
		tab.addTab(ledgerGroupPanel, getPropertyName("group"));
		
		
		pannel.setContent(tab);
		
		
		
		tab.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if(tab.getSelectedTab().getId().equals("Ledger")){
					
					((LedgerPanel) ledgerPanel).reloadGroup();
					
					setWidth(ledgerPanel.getWidth() + "");
					setHeight(ledgerPanel.getHeight() + 100 + "");
				}
				else {
					((LedgerGroupPanel) ledgerGroupPanel).loadOptions(0);
					
					setWidth(ledgerGroupPanel.getWidth() + "");
					setHeight(ledgerGroupPanel.getHeight() + 100 + "");
				}
			}
		});
		

		return pannel;
		
	}




	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
