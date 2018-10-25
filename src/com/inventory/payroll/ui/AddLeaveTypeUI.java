package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.LeaveTypeDao;
import com.inventory.payroll.model.LeaveTypeModel;
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
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
public class AddLeaveTypeUI extends SparkLogic {
	
	SPanel mainPanel;
	SFormLayout mainLayout;
	
	SComboField leaveTypeCombo;
	STextField nameField;
	SCheckBox forwardCheckBox;
	SCheckBox lopCheckBox;
	LeaveTypeDao dao;
	
	SButton createNewButton;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		mainLayout=new SFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setSize(400, 250);
		dao=new LeaveTypeDao();
		try {
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			SHorizontalLayout createLayout=new SHorizontalLayout("Leave Type");
			createLayout.setSpacing(true);
			leaveTypeCombo=new SComboField(null, 200, null, "id", "name", false, "Create New");
			createLayout.addComponent(leaveTypeCombo);
			createLayout.addComponent(createNewButton);
			nameField=new STextField("Name",200, true);
			nameField.setInputPrompt("Name");
			forwardCheckBox=new SCheckBox("Leave Carry Forward", false);
			lopCheckBox=new SCheckBox("Loss Of Pay", false);
			
			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			
			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			
			mainLayout.addComponent(createLayout);
			mainLayout.addComponent(nameField);
			mainLayout.addComponent(lopCheckBox);
			mainLayout.addComponent(forwardCheckBox);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			loadOptions(0);
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					leaveTypeCombo.setValue((long)0);
				}
			});
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							LeaveTypeModel mdl=new LeaveTypeModel();
							mdl.setName(nameField.getValue());
							mdl.setLop(lopCheckBox.getValue());
							mdl.setCarry_forward(forwardCheckBox.getValue());
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
			
			
			leaveTypeCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(leaveTypeCombo.getValue()!=null && !leaveTypeCombo.getValue().toString().equals("0")){
							LeaveTypeModel mdl=dao.getLeaveTypeModel((Long)leaveTypeCombo.getValue());
							if(mdl!=null){
								nameField.setValue(mdl.getName());
								lopCheckBox.setValue(mdl.isLop());
								forwardCheckBox.setValue(mdl.isCarry_forward());
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}
						}
						else{
							nameField.setValue("");
							lopCheckBox.setValue(false);
							forwardCheckBox.setValue(false);
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
						if(leaveTypeCombo.getValue()!=null && !leaveTypeCombo.getValue().toString().equals("0")){
							if(isValid()){
								LeaveTypeModel mdl=dao.getLeaveTypeModel((Long)leaveTypeCombo.getValue());
								if(mdl!=null){
									mdl.setName(nameField.getValue());
									mdl.setLop(lopCheckBox.getValue());
									mdl.setCarry_forward(forwardCheckBox.getValue());
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
						if(leaveTypeCombo.getValue()!=null && !leaveTypeCombo.getValue().toString().equals("0")){
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											LeaveTypeModel mdl=dao.getLeaveTypeModel((Long)leaveTypeCombo.getValue());
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
			list.add(0, new LeaveTypeModel(0, "Create New"));
			list.addAll(dao.getLeaveTypeModelList(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			leaveTypeCombo.setContainerDataSource(bic);
			leaveTypeCombo.setItemCaptionPropertyId("name");
			leaveTypeCombo.setValue(id);
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
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	
	
}
