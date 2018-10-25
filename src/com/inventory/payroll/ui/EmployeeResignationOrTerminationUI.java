package com.inventory.payroll.ui;

import java.util.Arrays;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.EmployeeStatusUpdationDao;
import com.inventory.payroll.model.EmployeeStatusModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class EmployeeResignationOrTerminationUI extends SparkLogic {

	private SComboField employeeComboField;
	private SNativeSelect typeNativeSelect;
	private SDateField dateField;
	private STextArea reasonTextArea;
	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private UserManagementDao userDao;
	private EmployeeStatusUpdationDao dao;

	@Override
	public SPanel getGUI() {
		setSize(600, 400);

		userDao = new UserManagementDao();
		dao = new EmployeeStatusUpdationDao();
		SPanel panel = new SPanel();
		panel.setSizeFull();

		try {
			SVerticalLayout mainVerticalLayout = new SVerticalLayout();
			mainVerticalLayout.setMargin(true);
			mainVerticalLayout.setSpacing(true);

			SFormLayout formLayout = new SFormLayout();
			formLayout.setMargin(true);
			formLayout.setSpacing(true);

			employeeComboField = new SComboField(getPropertyName("employee"), 200,userDao.getUsersWithFullNameAndCodeFromOffice(getOfficeID()), "id", "first_name", 
					true, getPropertyName("select"));

			typeNativeSelect = new SNativeSelect(getPropertyName("type"), 150,Arrays.asList(new KeyValue(SConstants.EmployeeStatus.RESIGNED,
							getPropertyName("resignation")), new KeyValue(SConstants.EmployeeStatus.TERMINATED,getPropertyName("termination"))), "intKey", "value",
							true);
			typeNativeSelect.setValue(SConstants.EmployeeStatus.RESIGNED);

			dateField = new SDateField(getPropertyName("date"));
			dateField.setValue(getWorkingDate());

			reasonTextArea = new STextArea(getPropertyName("reason"), 400, 100);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			updateButton.setVisible(false);
			deleteButton.setVisible(false);

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.setSpacing(true);

			mainButtonLayout.addComponent(saveButton);
			mainButtonLayout.addComponent(updateButton);
			mainButtonLayout.addComponent(deleteButton);

			formLayout.addComponent(employeeComboField);
			formLayout.addComponent(typeNativeSelect);
			formLayout.addComponent(dateField);
			formLayout.addComponent(reasonTextArea);

			mainVerticalLayout.addComponent(formLayout);
			mainVerticalLayout.addComponent(mainButtonLayout);

			mainVerticalLayout.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			panel.setContent(mainVerticalLayout);
			
			
			employeeComboField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					
					try {
						if(employeeComboField.getValue()!= null && !employeeComboField.getValue().equals("")){
							EmployeeStatusModel empModel = dao.getEmployeeStatusModel(toLong(employeeComboField.getValue().toString()));
							if(empModel != null){
								typeNativeSelect.setValue(empModel.getStatus());
								dateField.setValue(empModel.getDate());
								reasonTextArea.setValue(empModel.getReason());
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}
							else{
								dateField.setValue(getWorkingDate());
								reasonTextArea.setValue("");
								saveButton.setVisible(true);
								updateButton.setVisible(false);
								deleteButton.setVisible(false);
							}
						}
						else{
							dateField.setValue(getWorkingDate());
							reasonTextArea.setValue("");
							saveButton.setVisible(true);
							updateButton.setVisible(false);
							deleteButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						try {
							UserModel userModel = userDao.getUser(toLong(employeeComboField.getValue().toString()));
							userModel.setStatus(toInt(typeNativeSelect.getValue().toString()));
							if(userModel.getLoginId()!=null)
								userModel.getLoginId().setStatus(1);
							EmployeeStatusModel empModel = new EmployeeStatusModel();
							empModel.setId(userModel.getId());
							empModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							empModel.setReason(reasonTextArea.getValue());
							empModel.setStatus(toInt(typeNativeSelect.getValue().toString()));
							empModel.setUser(userModel);
							dao.save(empModel);
							Object obj=employeeComboField.getValue();
							employeeComboField.setValue(null);
							employeeComboField.setValue(obj);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("error"),
									Type.WARNING_MESSAGE);
						}
					}
				}
			});

			
			updateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						try {

							EmployeeStatusModel empModel = dao.getEmployeeStatusModel(toLong(employeeComboField.getValue().toString()));
							
							UserModel userModel = empModel.getUser();
							userModel.setStatus(toInt(typeNativeSelect.getValue().toString()));
							if(userModel.getLoginId()!=null)
								userModel.getLoginId().setStatus(1);
							empModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							empModel.setReason(reasonTextArea.getValue());
							empModel.setStatus(toInt(typeNativeSelect.getValue().toString()));
							empModel.setUser(userModel);
							dao.update(empModel);
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							Object obj=employeeComboField.getValue();
							employeeComboField.setValue(null);
							employeeComboField.setValue(obj);
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("error"),
									Type.WARNING_MESSAGE);
						}
					}

				}
			});
			
			deleteButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								try {
									dao.delete(toLong(employeeComboField.getValue()+""));
									Notification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);										
									Object obj=employeeComboField.getValue();
									employeeComboField.setValue(null);
									employeeComboField.setValue(obj);											
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}
					});					
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return panel;
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (employeeComboField.getValue() == null || employeeComboField.getValue().equals("")) {
			setRequiredError(employeeComboField,getPropertyName("invalid_selection"), true);
			valid = false;
		} else 
			setRequiredError(employeeComboField, null, false);

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else 
			setRequiredError(dateField, null, false);

		if (reasonTextArea.getValue() == null || reasonTextArea.getValue().equals("") || reasonTextArea.getValue().toString().trim().length()<=0) {
			setRequiredError(reasonTextArea, getPropertyName("invalid_data"),true);
			valid = false;
		} else 
			setRequiredError(reasonTextArea, null, false);
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
