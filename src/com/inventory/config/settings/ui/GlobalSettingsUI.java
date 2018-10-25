package com.inventory.config.settings.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.biz.SettingsBiz;
import com.inventory.config.settings.dao.SettingsDao;
import com.inventory.config.settings.model.SettingsModel;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 25, 2013
 */

@Theme("testappstheme")
public class GlobalSettingsUI extends SparkLogic {

	SettingsDao objDao = new SettingsDao();

	SFormLayout content;

	SNativeSelect defaultDateTypeSelect;
	SNativeSelect dateFormatSelect;

	SNativeSelect stockSettingsField;

	HorizontalLayout buttonLayout = null;

	STextField systemHostNameField;
	STextField systemEmailField;
	SPasswordField systemEmailPwdField;

	STextField salesEmailField;
	STextField salesHostNameField;
	SPasswordField salesEmailPwdField;
	
	STextField applicationEmailField;
	STextField applicationHostNameField;
	SPasswordField applicationEmailPwdField;
	
	SNativeSelect themeField;
	
	int old_theme; 

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		setSize(440, 560);
		
		objDao = new SettingsDao();

		buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		defaultDateTypeSelect = new SNativeSelect(
				getPropertyName("default_date_selection"), 200,
				SConstants.defaultDateSelectionOptions, "intKey", "value");
		dateFormatSelect = new SNativeSelect(getPropertyName("date_format"),
				200, SConstants.dateformatsOptions, "stringKey", "value");

		stockSettingsField = new SNativeSelect(
				getPropertyName("stock_management"), 80,
				SConstants.stock_management, "intKey", "value");
		stockSettingsField.setValue(1);
		stockSettingsField.setNullSelectionAllowed(false);
		
		themeField = new SNativeSelect(
				getPropertyName("theme"), 80,
				SConstants.theme, "intKey", "value");
		themeField.setValue(1);
		themeField.setNullSelectionAllowed(false);

		systemHostNameField = new STextField(getPropertyName("system_host"), 150);
		systemEmailField = new STextField(getPropertyName("system_email"), 150);
		systemEmailPwdField = new SPasswordField(
				getPropertyName("system_email_password"), 150);
		
		systemHostNameField.setDescription("Hints :- For Gmail use : gmail-smtp-msa.l.google.com , For Yahoo Mail : smtp.mail.yahoo.com , For Webmail : rs16.websitehostserver.net , For Godaddy Mail : smtpout.asia.secureserver.net ");

		salesHostNameField = new STextField(getPropertyName("sales_host"), 150);
		salesEmailField = new STextField(getPropertyName("sales_email"), 150);
		salesEmailPwdField = new SPasswordField(
				getPropertyName("sales_email_password"), 150);
		
		salesHostNameField.setDescription("Hints :- For Gmail use : gmail-smtp-msa.l.google.com , For Yahoo Mail : smtp.mail.yahoo.com , For Webmail : rs16.websitehostserver.net , For Godaddy Mail : smtpout.asia.secureserver.net ");

		
		applicationHostNameField = new STextField(getPropertyName("application_host"), 150);
		applicationEmailField = new STextField(getPropertyName("application_email"), 150);
		applicationEmailPwdField = new SPasswordField(
				getPropertyName("application_email_password"), 150);
		
		applicationHostNameField.setDescription("Hints :- For Gmail use : gmail-smtp-msa.l.google.com , For Yahoo Mail : smtp.mail.yahoo.com , For Webmail : rs16.websitehostserver.net , For Godaddy Mail : smtpout.asia.secureserver.net ");

		
		final SButton save = new SButton(getPropertyName("save"));
		final SButton cancel = new SButton(getPropertyName("cancel"));

		SPanel pan = new SPanel();
		pan.setSizeFull();

		try {

			content = new SFormLayout();

			content.setMargin(true);
			content.setSizeFull();

			content.addComponent(defaultDateTypeSelect);
			content.addComponent(dateFormatSelect);

			content.addComponent(stockSettingsField);
			content.addComponent(themeField);

			content.addComponent(systemHostNameField);
			content.addComponent(systemEmailField);
			content.addComponent(systemEmailPwdField);
			content.addComponent(salesHostNameField);
			content.addComponent(salesEmailField);
			content.addComponent(salesEmailPwdField);
			content.addComponent(applicationHostNameField);
			content.addComponent(applicationEmailField);
			content.addComponent(applicationEmailPwdField);

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(cancel);

			content.addComponent(buttonLayout);

			// Loading the settings

			List settingsValueList = objDao
					.getGlobalSettings(getOrganizationID());

			Iterator it = settingsValueList.iterator();
			while (it.hasNext()) {
				SettingsModel obj = (SettingsModel) it.next();

				if (obj.getSettings_name().equals(
						SConstants.settings.DATE_FORMAT)) {
					dateFormatSelect.setValue(obj.getValue());
				} else if (obj.getSettings_name().equals(
						SConstants.settings.DEFAULT_DATE_SELECTION)) {
					defaultDateTypeSelect.setValue(toInt(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.STOCK_MANAGEMENT)) {
					stockSettingsField.setValue(toInt(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.THEME)) {
					themeField.setValue(toInt(obj.getValue()));
					old_theme=toInt(obj.getValue());
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SYSTEM_EMAIL_HOST)) {
					systemHostNameField.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SYSTEM_EMAIL)) {
					systemEmailField.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SYSTEM_EMAIL_PASSWORD)) {
					systemEmailPwdField
							.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_EMAIL_HOST)) {
					salesHostNameField.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_EMAIL)) {
					salesEmailField.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_EMAIL_PASSWORD)) {
					salesEmailPwdField.setValue(String.valueOf(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.APPLICATION_EMAIL_HOST)) {
					applicationHostNameField.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.APPLICATION_EMAIL)) {
					applicationEmailField.setValue(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.APPLICATION_EMAIL_PASSWORD)) {
					applicationEmailPwdField.setValue(String.valueOf(obj.getValue()));
				}
			}
				


			pan.setContent(content);

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						long organization_id = getOrganizationID();

						if (isValid()) {
							List settingsList = new ArrayList();
							SettingsModel objModel;

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.DEFAULT_DATE_SELECTION);
							objModel.setValue(defaultDateTypeSelect.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.DATE_FORMAT);
							objModel.setValue(dateFormatSelect.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.STOCK_MANAGEMENT);
							objModel.setValue(stockSettingsField.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.THEME);
							objModel.setValue(themeField.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.SYSTEM_EMAIL_HOST);
							objModel.setValue(systemHostNameField.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.SYSTEM_EMAIL);
							objModel.setValue(systemEmailField.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.SYSTEM_EMAIL_PASSWORD);
							objModel.setValue(systemEmailPwdField.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.SALES_EMAIL_HOST);
							objModel.setValue(salesHostNameField.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.SALES_EMAIL);
							objModel.setValue(salesEmailField.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.SALES_EMAIL_PASSWORD);
							objModel.setValue(salesEmailPwdField.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.APPLICATION_EMAIL_HOST);
							objModel.setValue(applicationHostNameField.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.APPLICATION_EMAIL);
							objModel.setValue(applicationEmailField.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
							objModel.setLevel_id(organization_id);
							objModel.setSettings_name(SConstants.settings.APPLICATION_EMAIL_PASSWORD);
							objModel.setValue(applicationEmailPwdField.getValue()
									.toString());
							settingsList.add(objModel);

							try {
								objDao.saveGlobalSettings(settingsList,
										organization_id);

								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								new SettingsBiz().updateSettingsValue(
										getOrganizationID(), getOfficeID());
								System.out.println(themeField.getValue());
								System.out.println(old_theme);
								if(!themeField.getValue().toString().equals(asString(old_theme))) {
									getUI().getPage().setLocation(
											VaadinService.getCurrentRequest().getContextPath()
													+ "/");
								}

							} catch (Exception e) {
								Notification.show(getPropertyName("error"),
										Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});

			cancel.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						cancel.setVisible(false);
						closeWindow();

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					save.click();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		// TODO Auto-generated method stub
		return pan;
	}

	public void closeWindow() {
		// TODO Auto-generated method stub
		this.close();
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (stockSettingsField.getValue() == null
				|| stockSettingsField.getValue().equals("")) {
			setRequiredError(stockSettingsField,
					getPropertyName("select_stock_managing"), true);
			stockSettingsField.focus();
			ret = false;
		} else
			setRequiredError(stockSettingsField, null, false);
		
		if (themeField.getValue() == null
				|| themeField.getValue().equals("")) {
			setRequiredError(themeField,
					getPropertyName("invalid_selection"), true);
			themeField.focus();
			ret = false;
		} else
			setRequiredError(themeField, null, false);

		if (dateFormatSelect.getValue() == null
				|| dateFormatSelect.getValue().equals("")) {
			setRequiredError(dateFormatSelect,
					getPropertyName("select_date_format"), true);
			dateFormatSelect.focus();
			ret = false;
		} else
			setRequiredError(dateFormatSelect, null, false);

		if (defaultDateTypeSelect.getValue() == null
				|| defaultDateTypeSelect.getValue().equals("")) {
			setRequiredError(defaultDateTypeSelect,
					getPropertyName("select_date_type"), true);
			defaultDateTypeSelect.focus();
			ret = false;
		} else
			setRequiredError(defaultDateTypeSelect, null, false);

		// TODO Auto-generated method stub
		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
