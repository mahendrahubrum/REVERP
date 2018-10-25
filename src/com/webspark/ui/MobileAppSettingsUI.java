package com.webspark.ui;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.settings.dao.SettingsDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.model.MobileAppSettingsModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 10, 2014
 */
public class MobileAppSettingsUI extends SparkLogic {

	private static final long serialVersionUID = 8097901993605635137L;

	private SComboField loginComboField;
	private SCheckBox saleCheckBox;
	private SCheckBox purchaseCheckBox;
	private SCheckBox custPaymentCheckBox;
	private SCheckBox supplPaymentCheckBox;
	private SCheckBox payrollCheckBox;
	private SCheckBox expTransCheckBox;
	private SCheckBox bankTransCheckBox;
	private SCheckBox custSalesOrderCheckBox;
	private SCheckBox taskCheckBox;

	private SButton saveButton;
	private SettingsDao dao;

	@Override
	public SPanel getGUI() {

		setSize(400, 500);
		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout lay = new SFormLayout();
		lay.setMargin(true);

		dao = new SettingsDao();

		try {
			loginComboField = new SComboField(getPropertyName("Login"), 150,
					new UserManagementDao()
							.getAllLoginsForOrg(getOrganizationID()), "id",
					"login_name");
			loginComboField.setInputPrompt(getPropertyName("select"));

			saleCheckBox = new SCheckBox(getPropertyName("sales"));
			purchaseCheckBox = new SCheckBox(getPropertyName("purchase"));

			custPaymentCheckBox = new SCheckBox(
					getPropertyName("customer_payment"));
			supplPaymentCheckBox = new SCheckBox(
					getPropertyName("supplier_payment"));
			payrollCheckBox = new SCheckBox(getPropertyName("Payroll"));
			expTransCheckBox = new SCheckBox(
					getPropertyName("Expenditure_transactions"));
			bankTransCheckBox = new SCheckBox(
					getPropertyName("Bank_transactions"));
			custSalesOrderCheckBox = new SCheckBox(
					getPropertyName("customer_so"));
			taskCheckBox = new SCheckBox(getPropertyName("task_updates"));

			saveButton = new SButton(getPropertyName("save"));

			lay.addComponent(loginComboField);
			lay.addComponent(saleCheckBox);
			lay.addComponent(purchaseCheckBox);
			lay.addComponent(custPaymentCheckBox);
			lay.addComponent(supplPaymentCheckBox);
			lay.addComponent(payrollCheckBox);
			lay.addComponent(expTransCheckBox);
			lay.addComponent(bankTransCheckBox);
			lay.addComponent(custSalesOrderCheckBox);
			lay.addComponent(taskCheckBox);
			lay.addComponent(saveButton);

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (loginComboField.getValue() != null
							&& !loginComboField.getValue().equals("")) {
						List list = new ArrayList();

						MobileAppSettingsModel mdl = null;
						if (saleCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.SALES_ALERTS);
							list.add(mdl);
						}
						if (purchaseCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.PURCHASE_ALERTS);
							list.add(mdl);
						}
						if (custPaymentCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.CUSTOMER_PAY_ALERT);
							list.add(mdl);
						}
						if (supplPaymentCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.SUPPLIER_PAY_ALERT);
							list.add(mdl);
						}
						if (payrollCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.PAYROLL_ALERTS);
							list.add(mdl);
						}
						if (expTransCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.EXP_TRANS_ALERTS);
							list.add(mdl);
						}
						if (bankTransCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.BANK_TRANS_ALERTS);
							list.add(mdl);
						}
						if (custSalesOrderCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.CUSTOMER_SO_ALERT);
							list.add(mdl);
						}
						if (taskCheckBox.getValue()) {
							mdl = new MobileAppSettingsModel();
							mdl.setLevel(1);
							mdl.setLevel_id((Long) loginComboField.getValue());
							mdl.setSettings_name(SConstants.alerts.TASK_ALERT);
							list.add(mdl);
						}

						try {
							dao.saveMobileSettings(list,
									(Long) loginComboField.getValue());

							SNotification.show(getPropertyName("Success"),
									Type.TRAY_NOTIFICATION);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						setRequiredError(loginComboField,
								getPropertyName("invalid_selection"), true);
					}
				}
			});

			loginComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loginComboField.setComponentError(null);
					purchaseCheckBox.setValue(false);
					saleCheckBox.setValue(false);
					custPaymentCheckBox.setValue(false);
					supplPaymentCheckBox.setValue(false);
					payrollCheckBox.setValue(false);
					expTransCheckBox.setValue(false);
					bankTransCheckBox.setValue(false);
					custSalesOrderCheckBox.setValue(false);
					taskCheckBox.setValue(false);
					if (loginComboField.getValue() != null
							&& !loginComboField.getValue().equals("")) {
						try {
							List list = dao
									.getMobileAlerts((Long) loginComboField
											.getValue());
							MobileAppSettingsModel mdl = null;
							for (int i = 0; i < list.size(); i++) {
								mdl = (MobileAppSettingsModel) list.get(i);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.SALES_ALERTS))
									saleCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.PURCHASE_ALERTS))
									purchaseCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.CUSTOMER_PAY_ALERT))
									custPaymentCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.SUPPLIER_PAY_ALERT))
									supplPaymentCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.PAYROLL_ALERTS))
									payrollCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.EXP_TRANS_ALERTS))
									expTransCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.BANK_TRANS_ALERTS))
									bankTransCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.CUSTOMER_SO_ALERT))
									custSalesOrderCheckBox.setValue(true);
								if (mdl.getSettings_name().equals(
										SConstants.alerts.TASK_ALERT))
									taskCheckBox.setValue(true);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		pan.setContent(lay);
		return pan;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
