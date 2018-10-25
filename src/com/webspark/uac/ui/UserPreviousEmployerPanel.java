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
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.dao.UserPreviousEmployerDao;
import com.webspark.uac.model.UserModel;
import com.webspark.uac.model.UserPreviousEmployerModel;

@SuppressWarnings("serial")
public class UserPreviousEmployerPanel extends SContainerPanel {

	SVerticalLayout mainLayout;
	SComboField userCombo;
	
	STable table;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_NAME = "Name";
	static String TBC_PLACE = "Place";
	static String TBC_PHONE = "Phone";
	
	SButton saveButton;
	SButton createNewButton;
	
	SButton addItemButton;
	SButton updateItemButton;
	
	STextField nameField;
	STextField placeField;
	STextField numberField;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	UserPreviousEmployerDao dao;
	
	public UserPreviousEmployerPanel() {
		try {
			dao=new UserPreviousEmployerDao();
			allHeaders=new Object[]{TBC_SN, TBC_ID, TBC_NAME, TBC_PLACE, TBC_PHONE};
			requiredHeaders=new Object[]{TBC_SN, TBC_NAME, TBC_PLACE, TBC_PHONE};
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			setSize(750, 400);
			userCombo=new SComboField(null, 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), 
					isSuperAdmin()), "id", "first_name", true, "Select");
			SHorizontalLayout createLayout=new SHorizontalLayout(getPropertyName("user"));
			createLayout.setSpacing(true);
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			createLayout.addComponent(userCombo);
			createLayout.addComponent(createNewButton);
			
			table=new STable(null, 500, 150);
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.LEFT);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_NAME, String.class, null, TBC_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_PLACE, String.class, null, TBC_PLACE, null, Align.LEFT);
			table.addContainerProperty(TBC_PHONE, String.class, null, TBC_PHONE, null, Align.LEFT);
			table.setVisibleColumns(requiredHeaders);
			
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			
			nameField=new STextField(getPropertyName("name"), 150);
			placeField=new STextField(getPropertyName("place"), 150);
			numberField=new STextField(getPropertyName("number"), 150);
			
			SHorizontalLayout itemLayout=new SHorizontalLayout();
			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			itemLayout.setStyleName("po_border");
			
			itemLayout.addComponent(nameField);
			itemLayout.addComponent(placeField);
			itemLayout.addComponent(numberField);
			itemLayout.addComponent(addItemButton);
			itemLayout.addComponent(updateItemButton);
			itemLayout.setComponentAlignment(addItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setComponentAlignment(updateItemButton, Alignment.BOTTOM_CENTER);
			
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			
			mainLayout.addComponent(createLayout);
			mainLayout.addComponent(table);
			mainLayout.addComponent(itemLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
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
						table.removeAllItems();
						nameField.setValue("");
						placeField.setValue("");
						numberField.setValue("");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						if(userCombo.getValue()!=null){
							List list=new ArrayList();
							list=dao.getUserPreviousEmployerModel((Long)userCombo.getValue(), getOfficeID());
							if(list.size()>0){
								Iterator itr=list.iterator();
								table.setVisibleColumns(allHeaders);
								while (itr.hasNext()) {
									UserPreviousEmployerModel mdl = (UserPreviousEmployerModel) itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											mdl.getId(),
											mdl.getName(),
											mdl.getPlace(),
											mdl.getPhone()},table.getItemIds().size()+1);
								}
								table.setVisibleColumns(requiredHeaders);
							}
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
									placeField.getValue(),
									numberField.getValue()},table.getItemIds().size()+1);
							table.setVisibleColumns(requiredHeaders);
							nameField.setValue("");
							placeField.setValue("");
							numberField.setValue("");
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
						placeField.setValue(item.getItemProperty(TBC_PLACE).getValue().toString());
						numberField.setValue(item.getItemProperty(TBC_PHONE).getValue().toString());
						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
					}
					else{
						nameField.setValue("");
						placeField.setValue("");
						numberField.setValue("");
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
								item.getItemProperty(TBC_PLACE).setValue(placeField.getValue());
								item.getItemProperty(TBC_PHONE).setValue(numberField.getValue());
								table.setValue(null);
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
							List<UserPreviousEmployerModel> list=new ArrayList<UserPreviousEmployerModel>();
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								UserPreviousEmployerModel mdl=null;
								long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
								if(id!=0)
									mdl=dao.getUserPreviousEmployerModel(id);
								if(mdl==null)
									mdl=new UserPreviousEmployerModel();
								mdl.setName(item.getItemProperty(TBC_NAME).getValue().toString());
								mdl.setPlace(item.getItemProperty(TBC_PLACE).getValue().toString());
								mdl.setPhone(item.getItemProperty(TBC_PHONE).getValue().toString());
								mdl.setUser(new UserModel((Long)userCombo.getValue()));
								mdl.setOfficeId(getOfficeID());
								list.add(mdl);
							}
							dao.save(list, (Long)userCombo.getValue(), getOfficeID());
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isAddingValid(){
		boolean valid=true;
		
		if(nameField.getValue()==null || nameField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(nameField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(nameField, null, false);
		
		if(placeField.getValue()==null || placeField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(placeField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(placeField, null, false);
		
		return valid;
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
	
}
