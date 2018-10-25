package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.OverTimeDao;
import com.inventory.payroll.dao.PayrollComponentDao;
import com.inventory.payroll.model.OverTimeModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author sangeeth
 * @date 17-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class AddOverTimeUI extends SparkLogic{

	SPanel mainPanel;
	SFormLayout mainLayout;
	SComboField overTimeCombo;
	STextField nameField;
	SNativeSelect typeSelect;
	STextField valueField;
	SComboField payrollCombo;
	
	SButton createNewButton;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	OverTimeDao dao;
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		setSize(425, 325);
		dao=new OverTimeDao();
		try {
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			SHorizontalLayout createLayout=new SHorizontalLayout("Over Time");
			createLayout.setSpacing(true);
			overTimeCombo=new SComboField(null, 200, null, "key", "value", false, "Create New");
			createLayout.addComponent(overTimeCombo);
			createLayout.addComponent(createNewButton);
			nameField=new STextField("Description",200, true);
			nameField.setInputPrompt("Description");
			typeSelect = new SNativeSelect(getPropertyName("property2"), 200, SConstants.payroll.type, "key", "value");
			typeSelect.setNullSelectionAllowed(false);
			
			valueField=new STextField("Value",200, true);
			valueField.setStyleName("textfield_align_right");
			valueField.setInputPrompt("Value");
			valueField.setValue("0.0");
			
			payrollCombo=new SComboField("Percentage Of", 200, new PayrollComponentDao().getAllComponents(getOfficeID()), "id", "name", true, "Select");
			payrollCombo.setVisible(false);
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/save.png"));
			saveButton.setStyleName("saveButtonStyle");
			updateButton = new SButton(getPropertyName("update"), 100, 25);
			updateButton.setIcon(new ThemeResource("icons/update.png"));
			updateButton.setStyleName("updateButtonStyle");
			deleteButton = new SButton(getPropertyName("delete"), 100, 25);
			deleteButton.setIcon(new ThemeResource("icons/delete.png"));
			deleteButton.setStyleName("deleteButtonStyle");
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			
			mainLayout.addComponent(createLayout);
			mainLayout.addComponent(nameField);
			mainLayout.addComponent(typeSelect);
			mainLayout.addComponent(valueField);
			mainLayout.addComponent(payrollCombo);
			mainLayout.addComponent(buttonLayout);
			mainPanel.setContent(mainLayout);
			loadOptions(0);
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					overTimeCombo.setValue((long)0);
				}
			});
			
			
			typeSelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if((Long)typeSelect.getValue()==SConstants.payroll.PERCENTAGE){
						payrollCombo.setVisible(true);
					}
					else{
						payrollCombo.setVisible(false);
					}
				}
			});
			typeSelect.select(SConstants.payroll.PERCENTAGE);
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							OverTimeModel mdl=new OverTimeModel();
							mdl.setDescription(nameField.getValue());
							mdl.setValueType((Long)typeSelect.getValue());
							mdl.setValue(roundNumber(toDouble(valueField.getValue().toString())));
							if(payrollCombo.isVisible())
								mdl.setPayrollComponent((Long)payrollCombo.getValue());
							else
								mdl.setPayrollComponent((long)0);
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							long id=dao.save(mdl);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							loadOptions(id);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			overTimeCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(overTimeCombo.getValue()!=null && !overTimeCombo.getValue().toString().equals("0")){
							OverTimeModel mdl=dao.getOverTimeModel((Long)overTimeCombo.getValue());
							if(mdl!=null){
								nameField.setValue(mdl.getDescription());
								typeSelect.setValue(mdl.getValueType());
								valueField.setValue(roundNumber(mdl.getValue())+"");
								payrollCombo.setValue(mdl.getPayrollComponent());
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}
						}
						else{
							nameField.setValue("");
							typeSelect.setValue(SConstants.payroll.PERCENTAGE);
							valueField.setValue("0.0");
							payrollCombo.setValue(null);
							saveButton.setVisible(true);
							updateButton.setVisible(false);
							deleteButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			updateButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(overTimeCombo.getValue()!=null && !overTimeCombo.getValue().toString().equals("0")){
							if(isValid()){
								OverTimeModel mdl=dao.getOverTimeModel((Long)overTimeCombo.getValue());
								if(mdl!=null){
									mdl.setDescription(nameField.getValue());
									mdl.setValueType((Long)typeSelect.getValue());
									mdl.setValue(roundNumber(toDouble(valueField.getValue().toString())));
									if(payrollCombo.isVisible())
										mdl.setPayrollComponent((Long)payrollCombo.getValue());
									else
										mdl.setPayrollComponent((long)0);
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									dao.update(mdl);
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(overTimeCombo.getValue()!=null && !overTimeCombo.getValue().toString().equals("0")){
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											OverTimeModel mdl=dao.getOverTimeModel((Long)overTimeCombo.getValue());
											dao.delete(mdl);
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											loadOptions(0);
										} 
										catch (Exception e) {
											Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
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
		return mainPanel;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id){
		
		try {
			List list=new ArrayList();
			list.add(0, new OverTimeModel(0, "Create New"));
			list.addAll(dao.getOverTimeModelList(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			overTimeCombo.setContainerDataSource(bic);
			overTimeCombo.setItemCaptionPropertyId("description");
			overTimeCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		
		boolean valid=true;
		
		if(nameField.getValue()==null || nameField.getValue().toString().length()<=0){
			valid=false;
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
		}
		else
			setRequiredError(nameField, null, false);
		
		if(typeSelect.getValue()==null){
			valid=false;
			setRequiredError(typeSelect, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(typeSelect, null, false);
		
		if(payrollCombo.isVisible()){
			if(payrollCombo.getValue()==null){
				valid=false;
				setRequiredError(payrollCombo, getPropertyName("invalid_selection"), true);
			}
			else
				setRequiredError(payrollCombo, null, false);
		}
		
		if (valueField.getValue() == null || valueField.getValue().equals("")) {
			setRequiredError(valueField,"Invalid Data", true);
			valid = false;
		} 
		else {
			try {
				if (toDouble(valueField.getValue()) < 0) {
					setRequiredError(valueField,"Invalid Data", true);
					valid = false;
				} 
				else
					setRequiredError(valueField, null, false);
			} 
			catch (Exception e) {
				setRequiredError(valueField,"Invalid Data", true);
				valid = false;
			}
		}
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	
	
}
