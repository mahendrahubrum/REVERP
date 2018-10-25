package com.webspark.uac.ui;

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
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SVerticalLayout;
import com.webspark.model.AddressModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class UserAddressPanel extends SContainerPanel {

	SVerticalLayout mainLayout;
	SComboField userCombo;
	SAddressField homeAddressField;
	SAddressField officeAddressField;
	SAddressField localAddressField;
	
	SButton saveButton;
	SButton createNewButton;
	
	public UserAddressPanel() {
		try {
			
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			setSize(1050, 500);
			userCombo=new SComboField(null, 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), isSuperAdmin()), "id", "first_name", true, "Select");
			SHorizontalLayout createLayout=new SHorizontalLayout(getPropertyName("user"));
			createLayout.setSpacing(true);
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			createLayout.addComponent(userCombo);
			createLayout.addComponent(createNewButton);
			
			homeAddressField=new SAddressField(1);
			homeAddressField.setCaption("Home Address");
			homeAddressField.getCountryComboField().setValue(getCountryID());
			officeAddressField=new SAddressField(1);
			officeAddressField.setCaption("Office Address");
			officeAddressField.getCountryComboField().setValue(getCountryID());
			localAddressField=new SAddressField(1);
			localAddressField.setCaption("Local Address");
			localAddressField.getCountryComboField().setValue(getCountryID());
			
			SHorizontalLayout addressLayout=new SHorizontalLayout();
			addressLayout.setSpacing(true);
			
			addressLayout.addComponent(homeAddressField);
			addressLayout.addComponent(officeAddressField);
			addressLayout.addComponent(localAddressField);
			
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			
			mainLayout.addComponent(createLayout);
			mainLayout.addComponent(addressLayout);
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
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						homeAddressField.clearAll();
						officeAddressField.clearAll();
						localAddressField.clearAll();
						homeAddressField.getCountryComboField().setValue(getCountryID());
						officeAddressField.getCountryComboField().setValue(getCountryID());
						localAddressField.getCountryComboField().setValue(getCountryID());
						if(userCombo.getValue()!=null){
							UserModel user=new UserManagementDao().getUser((Long)userCombo.getValue());
							if(user.getAddress()!=null)
								homeAddressField.loadAddress(user.getAddress().getId());
							if(user.getWork_address()!=null)
								officeAddressField.loadAddress(user.getWork_address().getId());
							if(user.getLocal_address()!=null)
								localAddressField.loadAddress(user.getLocal_address().getId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						userCombo.setComponentError(null);
						if(userCombo.getValue()!=null){
							UserModel user=new UserManagementDao().getUser((Long)userCombo.getValue());
							AddressModel homeAddress=homeAddressField.getAddress();
							AddressModel localAddress=localAddressField.getAddress();
							AddressModel workAddress=officeAddressField.getAddress();
							
							if(user.getAddress()!=null)
								homeAddress.setId(user.getAddress().getId());
							if(user.getLocal_address()!=null)
								localAddress.setId(user.getLocal_address().getId());
							if(user.getWork_address()!=null)
								workAddress.setId(user.getWork_address().getId());
							user.setAddress(homeAddress);
							user.setLocal_address(localAddress);
							user.setWork_address(workAddress);
							
							new UserManagementDao().saveUserAddress(user);
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
