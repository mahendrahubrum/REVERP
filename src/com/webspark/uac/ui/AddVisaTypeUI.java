package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Item;
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
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.VisaTypeDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.VisaTypeModel;

/**
 * 
 * @author sangeeth
 * Automobile
 * 10-Jun-2015
 */

@SuppressWarnings("serial")
public class AddVisaTypeUI extends SparkLogic {

	SPanel mainPanel;
	SHorizontalLayout mainLayout;
	SFormLayout topLayout;
	SVerticalLayout itemLayout;
	
	SComboField visaTypeCombo;
	SComboField statusCombo;
	STextField nameField;
	VisaTypeDao dao;
	
	STable table;
	static String TBC_SN = "#";
	static String TBC_ID = "Id";
	static String TBC_NAME = "Description";
	static String TBC_STATUS_ID = "Status Id";
	static String TBC_STATUS = "Status";
	
	SButton createNewButton;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		setSize(775, 400);
		allHeaders=new Object[]{TBC_SN, TBC_ID, TBC_NAME, TBC_STATUS_ID, TBC_STATUS };
		requiredHeaders=new Object[]{TBC_SN, TBC_NAME, TBC_STATUS };
		try {
			dao=new VisaTypeDao();
			mainLayout=new SHorizontalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			topLayout=new SFormLayout();
			topLayout.setSpacing(true);
			itemLayout=new SVerticalLayout();
			itemLayout.setSpacing(true);
			
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			SHorizontalLayout createLayout=new SHorizontalLayout("Visa Type");
			createLayout.setSpacing(true);
			visaTypeCombo=new SComboField(null, 200, null, "key", "value", false, "Create New");
			createLayout.addComponent(visaTypeCombo);
			createLayout.addComponent(createNewButton);
			nameField=new STextField("Description",200, true);
			nameField.setInputPrompt("Description");
			statusCombo=new SComboField("Status", 200, SConstants.statuses.status, "key", "value", true, "Select");
			topLayout.addComponent(createLayout);
			topLayout.addComponent(nameField);
			topLayout.addComponent(statusCombo);
			
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
			
			itemLayout.addComponent(topLayout);
			itemLayout.addComponent(buttonLayout);
			itemLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			
			table=new STable();
			table.setWidth("400");
			table.setHeight("300");
			table.setSelectable(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.LEFT);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_NAME, String.class, null, TBC_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_STATUS_ID, Long.class, null, TBC_STATUS_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_STATUS, String.class, null, TBC_STATUS, null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, 0.15f);
			table.setColumnExpandRatio(TBC_NAME, 1.5f);
			table.setColumnExpandRatio(TBC_STATUS, 0.5f);
			
			table.setVisibleColumns(requiredHeaders);
			loadTable();
			
			mainLayout.addComponent(itemLayout);
			mainLayout.addComponent(table);
			mainPanel.setContent(mainLayout);
			
			loadOptions(0);
			
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					visaTypeCombo.setValue((long)0);
				}
			});
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							VisaTypeModel mdl=new VisaTypeModel();
							mdl.setName(nameField.getValue());
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							mdl.setStatus((Long)statusCombo.getValue());
							long id=dao.save(mdl);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							loadOptions(id);
							loadTable();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			visaTypeCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(visaTypeCombo.getValue()!=null && !visaTypeCombo.getValue().toString().equals("0")){
							VisaTypeModel mdl=dao.getVisaTypeModel((Long)visaTypeCombo.getValue());
							if(mdl!=null){
								nameField.setValue(mdl.getName());
								statusCombo.setValue(mdl.getStatus());
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}
						}
						else{
							nameField.setValue("");
							statusCombo.setValue(null);
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
						if(visaTypeCombo.getValue()!=null && !visaTypeCombo.getValue().toString().equals("0")){
							if(isValid()){
								VisaTypeModel mdl=dao.getVisaTypeModel((Long)visaTypeCombo.getValue());
								if(mdl!=null){
									mdl.setName(nameField.getValue());
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									mdl.setStatus((Long)statusCombo.getValue());
									dao.update(mdl);
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId());
									loadTable();
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
						if(visaTypeCombo.getValue()!=null && !visaTypeCombo.getValue().toString().equals("0")){
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											VisaTypeModel mdl=dao.getVisaTypeModel((Long)visaTypeCombo.getValue());
											dao.delete(mdl);
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											loadOptions(0);
											loadTable();
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
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							loadOptions(toLong(item.getItemProperty(TBC_ID).getValue().toString()));
						}
						else
							loadOptions(0);
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

	@SuppressWarnings({ "rawtypes" })
	public void loadTable(){
		
		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			List list=new ArrayList();
			list=dao.getVisaTypeModelList(getOfficeID());
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					VisaTypeModel omdl = (VisaTypeModel) itr.next();
					VisaTypeModel mdl=dao.getVisaTypeModel(omdl.getId());
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							mdl.getId(),
							mdl.getName(),
							mdl.getStatus(),
							statusCombo.getItemCaption(mdl.getStatus())},table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(requiredHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id){
		
		try {
			List list=new ArrayList();
			list.add(0, new VisaTypeModel(0, "Create New"));
			list.addAll(dao.getVisaTypeModelList(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			visaTypeCombo.setContainerDataSource(bic);
			visaTypeCombo.setItemCaptionPropertyId("name");
			visaTypeCombo.setValue(id);
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
		
		if(statusCombo.getValue()==null || statusCombo.getValue().toString().equals("")){
			valid=false;
			setRequiredError(statusCombo, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(statusCombo, null, false);
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	
	
}
