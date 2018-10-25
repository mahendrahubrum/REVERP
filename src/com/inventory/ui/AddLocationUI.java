package com.inventory.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.LocationDao;
import com.inventory.model.LocationModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class AddLocationUI extends SparkLogic{

	SVerticalLayout mainLayout;
	SFormLayout formLayout;
	SPanel mainPanel;
	SComboField locationCombo;
	STextField nameField;
	
	SButton createNewButton;
	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;
	LocationDao dao;
	@Override
	public SPanel getGUI() {
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		formLayout=new SFormLayout();
		formLayout.setSpacing(true);
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		setSize(350, 225);
		dao=new LocationDao();
		
		try {
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));
			locationCombo=new SComboField(null, 200, null, "id", "name");
			nameField=new STextField("Name", 200);
			nameField.setInputPrompt("Name");
			
			SHorizontalLayout createLayout = new SHorizontalLayout("Location");
			createLayout.setSpacing(true);
			createLayout.addComponent(locationCombo);
			createLayout.addComponent(createNewButton);
			
			saveButton = new SButton(getPropertyName("Save"));
			updateButton = new SButton(getPropertyName("Update"));
			deleteButton = new SButton(getPropertyName("Delete"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			saveButton.setVisible(true);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			
			HorizontalLayout buttonLayout=new HorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			
			formLayout.addComponent(createLayout);
			formLayout.addComponent(nameField);
			mainLayout.addComponent(formLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			loadOptions(0);
			
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					locationCombo.setValue((long) 0);
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (locationCombo.getValue() == null || locationCombo.getValue().toString().equals("0")) {
							if(isValid()){
								LocationModel mdl=new LocationModel();
								mdl.setName(nameField.getValue());
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								long id=dao.save(mdl);
								loadOptions(id);
								SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						SNotification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});
			
			
			locationCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (locationCombo.getValue() != null && !locationCombo.getValue().toString().equals("0")) {
							LocationModel mdl=dao.getLocationModel((Long)locationCombo.getValue());
							nameField.setValue(mdl.getName());
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
						}
						else{
							nameField.setValue("");
							saveButton.setVisible(true);
							updateButton.setVisible(false);
							deleteButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			updateButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (locationCombo.getValue() != null && !locationCombo.getValue().toString().equals("0")) {
							if(isValid()){
								LocationModel mdl=dao.getLocationModel((Long)locationCombo.getValue());
								mdl.setName(nameField.getValue());
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								dao.update(mdl);
								loadOptions(mdl.getId());
								SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						SNotification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});
			
			
			deleteButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("static-access")
				public void buttonClick(ClickEvent event) {
					try {
						ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are you sure?",new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										if (locationCombo.getValue() != null && !locationCombo.getValue().toString().equals("0")) {
											dao.delete((Long)locationCombo.getValue());
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											loadOptions(0);
										}
									} 
									catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Save",
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
		return mainPanel;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		try {
			List list=new ArrayList();
			list.add(0, new LocationModel(0, "Create New"));
			list.addAll(dao.getLocationModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			locationCombo.setContainerDataSource(bic);
			locationCombo.setItemCaptionPropertyId("name");
			locationCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public Boolean isValid() {
		boolean ret = true;
		if (nameField.getValue() == null || nameField.getValue().equals("")) {
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
			ret = false;
			nameField.focus();
		} else
			setRequiredError(nameField, null,false);
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	
	
}
