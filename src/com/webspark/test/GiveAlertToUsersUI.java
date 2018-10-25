package com.webspark.test;

import com.inventory.config.acct.dao.LedgerDao;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.SparkLogic;
import com.webspark.test.dao.SalesStockUpdateDao;
import com.webspark.ui.MainGUI;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Jan 23 2014
 */
public class GiveAlertToUsersUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private STextArea detailsTextArea;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton broadCastButton;

	SPanel mainPanel;
	LedgerDao ledDao;
	
	SalesStockUpdateDao daoObj;

	@Override
	public SPanel getGUI() {
		
		ledDao=new LedgerDao();
		
		daoObj=new SalesStockUpdateDao();
		
		setSize(530, 400);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);
		
		try {
			
			detailsTextArea = new STextArea(getPropertyName("message"), 400,200);
			detailsTextArea.setValue("REVERP Will shutdown with in 5 minutes. Please save your data. Thank You. SparkNova Technical Team.");
			
			mainFormLayout.addComponent(detailsTextArea);

			broadCastButton = new SButton(getPropertyName("broadcast"));
//			broadCastButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(broadCastButton);
			buttonHorizontalLayout.setComponentAlignment(broadCastButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainPanel.setContent(mainFormLayout);
			
			broadCastButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						
						MainGUI main=(MainGUI) getUI();
						main.sendBroadCast(detailsTextArea.getValue());
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
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
