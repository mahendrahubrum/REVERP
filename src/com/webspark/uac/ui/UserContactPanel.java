package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
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
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.uac.dao.UserContactDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserContactModel;
import com.webspark.uac.model.UserFamilyContactModel;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class UserContactPanel extends SContainerPanel {

	SVerticalLayout mainLayout;
	SComboField userCombo;
	
	STable table;
	STable familyTable;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_NAME = "Name";
	static String TBC_RELATION = "Relation";
	static String TBC_PHONE = "Phone";
	
	SButton saveButton;
	SButton createNewButton;
	
	SButton addItemButton;
	SButton updateItemButton;
	SButton addFamilyItemButton;
	SButton updateFamilyItemButton;
	
	STextField nameField;
	STextField relationField;
	STextField phoneField;
	
	STextField familyNameField;
	STextField familyRelationField;
	STextField familyPhoneField;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	UserContactDao dao;
	
	public UserContactPanel() {
		try {
			dao=new UserContactDao();
			allHeaders=new Object[]{TBC_SN, TBC_ID, TBC_NAME, TBC_RELATION, TBC_PHONE};
			requiredHeaders=new Object[]{TBC_SN, TBC_NAME, TBC_RELATION, TBC_PHONE};
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			setSize(750, 535);
			userCombo=new SComboField(null, 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), isSuperAdmin()), "id", "first_name", true, "Select");
			SHorizontalLayout createLayout=new SHorizontalLayout(getPropertyName("user"));
			createLayout.setSpacing(true);
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			createLayout.addComponent(userCombo);
			createLayout.addComponent(createNewButton);
			
			SHorizontalLayout addressLayout=new SHorizontalLayout();
			addressLayout.setSpacing(true);
			
			table=new STable("Immediate Contacts", 540, 100);
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.LEFT);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_NAME, String.class, null, TBC_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_RELATION, String.class, null, TBC_RELATION, null, Align.LEFT);
			table.addContainerProperty(TBC_PHONE, String.class, null, TBC_PHONE, null, Align.LEFT);
			table.setVisibleColumns(requiredHeaders);
			table.setColumnExpandRatio(TBC_SN, 0.5f);
			table.setColumnExpandRatio(TBC_NAME, 1f);
			table.setColumnExpandRatio(TBC_RELATION, 1f);
			table.setColumnExpandRatio(TBC_PHONE, 1f);
			
			familyTable=new STable("Family Contacts", 540, 100);
			familyTable.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.LEFT);
			familyTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.LEFT);
			familyTable.addContainerProperty(TBC_NAME, String.class, null, TBC_NAME, null, Align.LEFT);
			familyTable.addContainerProperty(TBC_RELATION, String.class, null, TBC_RELATION, null, Align.LEFT);
			familyTable.addContainerProperty(TBC_PHONE, String.class, null, TBC_PHONE, null, Align.LEFT);
			familyTable.setVisibleColumns(requiredHeaders);
			familyTable.setColumnExpandRatio(TBC_SN, 0.5f);
			familyTable.setColumnExpandRatio(TBC_NAME, 1f);
			familyTable.setColumnExpandRatio(TBC_RELATION, 1f);
			familyTable.setColumnExpandRatio(TBC_PHONE, 1f);
			
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			
			addFamilyItemButton = new SButton(null, "Add Item");
			addFamilyItemButton.setStyleName("addItemBtnStyle");
			updateFamilyItemButton = new SButton(null, "Update");
			updateFamilyItemButton.setStyleName("updateItemBtnStyle");
			updateFamilyItemButton.setVisible(false);
			
			nameField=new STextField("Name", 150, true);
			relationField=new STextField("Relation", 150, false);
			phoneField=new STextField("Phone", 150, true);
			
			familyNameField=new STextField("Name", 150, true);
			familyRelationField=new STextField("Relation", 150, false);
			familyPhoneField=new STextField("Phone", 150, true);
			
			SHorizontalLayout itemLayout=new SHorizontalLayout();
			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			itemLayout.setStyleName("po_border");
			
			SHorizontalLayout familyItemLayout=new SHorizontalLayout();
			familyItemLayout.setSpacing(true);
			familyItemLayout.setMargin(true);
			familyItemLayout.setStyleName("po_border");
			
			itemLayout.addComponent(nameField);
			itemLayout.addComponent(relationField);
			itemLayout.addComponent(phoneField);
			itemLayout.addComponent(addItemButton);
			itemLayout.addComponent(updateItemButton);
			itemLayout.setComponentAlignment(addItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setComponentAlignment(updateItemButton, Alignment.BOTTOM_CENTER);
			
			familyItemLayout.addComponent(familyNameField);
			familyItemLayout.addComponent(familyRelationField);
			familyItemLayout.addComponent(familyPhoneField);
			familyItemLayout.addComponent(addFamilyItemButton);
			familyItemLayout.addComponent(updateFamilyItemButton);
			
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			
			mainLayout.addComponent(createLayout);
			mainLayout.addComponent(addressLayout);
			mainLayout.addComponent(table);
			mainLayout.addComponent(itemLayout);
			mainLayout.addComponent(familyTable);
			mainLayout.addComponent(familyItemLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			setContent(mainLayout);
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					userCombo.setValue(null);
				}
			});
			
			userCombo.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(userCombo.getValue()!=null){
							table.removeAllItems();
							nameField.setValue("");
							relationField.setValue("");
							phoneField.setValue("");
							List list=new ArrayList();
							list=dao.getUserContactModelList((Long)userCombo.getValue(), getOfficeID());
							if(list.size()>0){
								Iterator itr=list.iterator();
								table.setVisibleColumns(allHeaders);
								while (itr.hasNext()) {
									UserContactModel mdl = (UserContactModel) itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											mdl.getId(),
											mdl.getName(),
											mdl.getRelation(),
											mdl.getPhone()},table.getItemIds().size()+1);
								}
								table.setVisibleColumns(requiredHeaders);
							}
							list=dao.getUserFamilyContactModelList((Long)userCombo.getValue(), getOfficeID());
							if(list.size()>0){
								Iterator itr=list.iterator();
								familyTable.setVisibleColumns(allHeaders);
								while (itr.hasNext()) {
									UserFamilyContactModel mdl = (UserFamilyContactModel) itr.next();
									familyTable.addItem(new Object[]{
											familyTable.getItemIds().size()+1,
											mdl.getId(),
											mdl.getName(),
											mdl.getRelation(),
											mdl.getPhone()},familyTable.getItemIds().size()+1);
								}
								familyTable.setVisibleColumns(requiredHeaders);
							}
						}
						else{
							table.removeAllItems();
							familyTable.removeAllItems();
							nameField.setValue("");
							relationField.setValue("");
							phoneField.setValue("");
							familyNameField.setValue("");
							familyRelationField.setValue("");
							familyPhoneField.setValue("");
							addItemButton.setVisible(true);
							updateItemButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			addItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							table.setVisibleColumns(allHeaders);
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										nameField.getValue(),
										relationField.getValue(),
										phoneField.getValue()},table.getItemIds().size()+1);
							table.setVisibleColumns(requiredHeaders);
							nameField.setValue("");
							relationField.setValue("");
							phoneField.setValue("");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						Item item =table.getItem(table.getValue());
						nameField.setValue(item.getItemProperty(TBC_NAME).getValue().toString());
						relationField.setValue(item.getItemProperty(TBC_RELATION).getValue().toString());
						phoneField.setValue(item.getItemProperty(TBC_PHONE).getValue().toString());
						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
					}
					else{
						nameField.setValue("");
						relationField.setValue("");
						phoneField.setValue("");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
					}
				}
			});
			
			updateItemButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(table.getValue()!=null){
							if(isAddingValid()){
								Item item =table.getItem(table.getValue());
								item.getItemProperty(TBC_NAME).setValue(nameField.getValue());
								item.getItemProperty(TBC_RELATION).setValue(relationField.getValue());
								item.getItemProperty(TBC_PHONE).setValue(phoneField.getValue());
								table.setValue(null);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			addFamilyItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isFamilyAddingValid()){
							familyTable.setVisibleColumns(allHeaders);
							familyTable.addItem(new Object[]{
										familyTable.getItemIds().size()+1,
										(long)0,
										familyNameField.getValue(),
										familyRelationField.getValue(),
										familyPhoneField.getValue()},familyTable.getItemIds().size()+1);
							familyTable.setVisibleColumns(requiredHeaders);
							familyNameField.setValue("");
							familyRelationField.setValue("");
							familyPhoneField.setValue("");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			familyTable.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(familyTable.getValue()!=null){
						Item item =familyTable.getItem(familyTable.getValue());
						familyNameField.setValue(item.getItemProperty(TBC_NAME).getValue().toString());
						familyRelationField.setValue(item.getItemProperty(TBC_RELATION).getValue().toString());
						familyPhoneField.setValue(item.getItemProperty(TBC_PHONE).getValue().toString());
						addFamilyItemButton.setVisible(false);
						updateFamilyItemButton.setVisible(true);
					}
					else{
						familyNameField.setValue("");
						familyRelationField.setValue("");
						familyPhoneField.setValue("");
						addFamilyItemButton.setVisible(true);
						updateFamilyItemButton.setVisible(false);
					}
				}
			});
			
			updateFamilyItemButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(familyTable.getValue()!=null){
							if(isFamilyAddingValid()){
								Item item =familyTable.getItem(familyTable.getValue());
								item.getItemProperty(TBC_NAME).setValue(familyNameField.getValue());
								item.getItemProperty(TBC_RELATION).setValue(familyRelationField.getValue());
								item.getItemProperty(TBC_PHONE).setValue(familyPhoneField.getValue());
								familyTable.setValue(null);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						userCombo.setComponentError(null);
						if(userCombo.getValue()!=null){
							Iterator itr=table.getItemIds().iterator();
							List<UserContactModel> list=new ArrayList<UserContactModel>();
							List<UserFamilyContactModel> familyList=new ArrayList<UserFamilyContactModel>();
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								UserContactModel mdl=null;
								long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
								if(id!=0)
									mdl=dao.getUserContactModel(id);
								if(mdl==null)
									mdl=new UserContactModel();
								mdl.setName(item.getItemProperty(TBC_NAME).getValue().toString());
								mdl.setRelation(item.getItemProperty(TBC_RELATION).getValue().toString());
								mdl.setPhone(item.getItemProperty(TBC_PHONE).getValue().toString());
								mdl.setUser(new UserModel((Long)userCombo.getValue()));
								mdl.setOfficeId(getOfficeID());
								list.add(mdl);
							}
							Iterator it=familyTable.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = familyTable.getItem(it.next());
								UserFamilyContactModel mdl=null;
								long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
								if(id!=0)
									mdl=dao.getUserFamilyContactModel(id);
								if(mdl==null)
									mdl=new UserFamilyContactModel();
								mdl.setName(item.getItemProperty(TBC_RELATION).getValue().toString());
								mdl.setRelation(item.getItemProperty(TBC_RELATION).getValue().toString());
								mdl.setPhone(item.getItemProperty(TBC_PHONE).getValue().toString());
								mdl.setUser(new UserModel((Long)userCombo.getValue()));
								mdl.setOfficeId(getOfficeID());
								familyList.add(mdl);
							}
							dao.save(list,familyList,(Long)userCombo.getValue(), getOfficeID());
							Notification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);
							long uid=(Long)userCombo.getValue();
							userCombo.setValue(null);
							userCombo.setValue(uid);
						}
						else
							setRequiredError(userCombo, getPropertyName("invalid_data"), true);
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
				}
			});
			
			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					return new Action[] { actionDelete };
				}
				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					deleteItem();
				}
			});
			
			familyTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					return new Action[] { actionDelete };
				}
				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					deleteFamilyItem();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				table.removeItem(table.getValue());
				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					newitem = table.getItem((Integer) it.next());
					newitem.getItemProperty(TBC_SN).setValue(SN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteFamilyItem() {
		try {
			if (familyTable.getValue() != null) {
				familyTable.removeItem(familyTable.getValue());
				int SN = 0;
				Item newitem;
				Iterator it = familyTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					newitem = familyTable.getItem((Integer) it.next());
					newitem.getItemProperty(TBC_SN).setValue(SN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	public boolean isAddingValid(){
		boolean valid=true;
		
		if(nameField.getValue()==null || nameField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
		}
		else
			setRequiredError(nameField, null, false);
		
		if(phoneField.getValue()==null || phoneField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(phoneField, getPropertyName("invalid_data"), true);
		}
		else
			setRequiredError(phoneField, null, false);
		
		return valid;
	}
	
	public boolean isFamilyAddingValid(){
		boolean valid=true;
		
		if(familyNameField.getValue()==null || familyNameField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(familyNameField, getPropertyName("invalid_data"), true);
		}
		else
			setRequiredError(familyNameField, null, false);
		
		if(familyPhoneField.getValue()==null || familyPhoneField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(familyPhoneField, getPropertyName("invalid_data"), true);
		}
		else
			setRequiredError(familyPhoneField, null, false);
		
		return valid;
	}
	
}
