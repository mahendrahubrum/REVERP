package com.webspark.test;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.test.dao.QueryUpdateDao;

/**
 * @author Jinshad
 * 
 *         WebSpark.
 * 
 *         Feb 21 2014
 */
public class QueryUpdateToolUI extends SparkLogic {

	private static final long serialVersionUID = -6316053747180409181L;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SButton generateButton;
	
	QueryUpdateDao daoObj;

	private SOfficeComboField officeComboField;
	private SReportChoiceField reportChoiceField;

	SNativeSelect choice;
	
	@Override
	public SPanel getGUI() {
		setSize(320, 220);
		
		daoObj=new QueryUpdateDao();
		
		choice=new SNativeSelect("Choice");
		choice.addItem("All");
		choice.addItem("Customer");
		choice.addItem("Supplier");
		choice.setValue("All");
		
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);
		mainFormLayout.setSpacing(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField("Date");
		fromDateField.setValue(getWorkingDate());
		toDateField = new SDateField("To Date");
		toDateField.setValue(getWorkingDate());
		reportChoiceField = new SReportChoiceField("Export to");

		generateButton = new SButton("Update Values");
		generateButton.setClickShortcut(KeyCode.ENTER);

		dateHorizontalLayout.addComponent(fromDateField);
//		dateHorizontalLayout.addComponent(toDateField);
		
		

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		
		mainFormLayout.addComponent(choice);
//		mainFormLayout.addComponent(reportChoiceField);
		
		mainFormLayout.addComponent(generateButton);
		mainFormLayout.setComponentAlignment(generateButton,
				Alignment.MIDDLE_CENTER);

		panel.setContent(mainFormLayout);

		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {
						
						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
						
											if(choice.getValue().toString().equals("All")) {
												
												daoObj.updateCustomerLedgerBalance(CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														(Long)officeComboField.getValue());
												
												daoObj.updateSupplierLedgerBalance(CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														(Long)officeComboField.getValue());
												
												Notification.show("Success.!!",
														"Updated Successfully" ,
														Type.WARNING_MESSAGE);
											}
											
											if(choice.getValue().toString().equals("Customer")) {
												daoObj.updateCustomerLedgerBalance(CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																(Long)officeComboField.getValue());
												
												Notification.show("Success.!!",
														"Updated Successfully" ,
														Type.WARNING_MESSAGE);
											}
											if(choice.getValue().toString().equals("Supplier")) {
												daoObj.updateSupplierLedgerBalance(CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														(Long)officeComboField.getValue());
												
												Notification.show("Success.!!",
														"Updated Successfully" ,
														Type.WARNING_MESSAGE);
											}
											
											
										} catch (Exception e) {
											e.printStackTrace();
											Notification.show(
													"Error..!!",
													"Error Message :"
															+ e.getCause(),
													Type.ERROR_MESSAGE);
										}}}
									});
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		return panel;
	}

	@Override
	public Boolean isValid() {

		fromDateField.setComponentError(null);
		if (fromDateField.getValue()==null) {
			setRequiredError(fromDateField, "Invalid Date Selection", true);
			return false;
		}
		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
