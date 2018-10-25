package com.inventory.config.settings.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.InternetAddress;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.biz.SettingsBiz;
import com.inventory.config.settings.dao.SettingsDao;
import com.inventory.config.settings.model.SettingsModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 25, 2013
 */

public class SettingsUI extends SparkLogic {

	private static final long serialVersionUID = -1010794915941085277L;

	SettingsDao objDao = new SettingsDao();

	SCheckBox cessEnable;
	SCheckBox taxEnable;
	STextField cesspercentageTextField;

	SCheckBox manufacturingDatesEnable;
	SCheckBox discountEnable;
	SCheckBox salesDiscountEnable;
//	SCheckBox exciseDutyEnable;
//	SCheckBox shippingChargeEnable;
	SCheckBox finYearBackEntryEnable;
	SCheckBox hideOrganizationDetails;
	SRadioButton expendetureAcctShow;
	SCheckBox showAllEmplyeeOnPayrol;
	SCheckBox barcodeEnabled;
	SComboField defaultCustomer;
	SCheckBox salePriceEditable;
	SCheckBox paymentBillSelectionMandatory;
	SCheckBox disableSalesForUnderCrLimitCustomer;
	SCheckBox alertForUnderCrLimit;
	SCheckBox keepDeletedData;
	SCheckBox autocreateSubgroupCode;
	SCheckBox autocreateCustomerCode;
	SCheckBox autocreateSupplierCode;
	SCheckBox useSalesNumberInSalesOrder;
	SCheckBox useSystemMailForSendCustomerMail;
	SCheckBox keepOtherWindows;
	SCheckBox hideAlerts;
	SCheckBox gradingEnabled;
//	SCheckBox localForeignTypeEnabled;
	SCheckBox rackEnabled;
	SCheckBox salesManWiseSales;
	SCheckBox purchaseOrderExpiry;
	
	
	SCheckBox multipleCurrencyEnabled;
	SCheckBox useGrossAndNetWeight;
	SCheckBox useSalesRateFromStock;
	SCheckBox showStockInProfitReport;
	SCheckBox salesNoCreationManual;
	SCheckBox showItemContainerNo;
	SCheckBox showItemAttributes;
	SCheckBox departmentEnabled;
	SCheckBox divisionEnabled;
	SCheckBox commissionEnabled;
	
	SCheckBox alertMail;
	SCheckBox alertNofification;
	STextField alertMailField;
	STextField customerCodeFormat;
	STextField supplierCodeFormat;

	SNativeSelect itemRateUpdate;
	SNativeSelect currencyFormat;
	SNativeSelect profitCalculation;
	SNativeSelect barcodeType;
	SNativeSelect payrollCalculation;

	HorizontalLayout buttonLayout = null;

	private SCheckBox itemInMultipleLang;

	private SCheckBox supplierSpecificItemCheckBox;

	private SCheckBox itemGroupFilterInSales;

	private SCheckBox salesOrderEnable;

	@Override
	public SPanel getGUI() {

		setSize(1200, 650);

		List ledgerList;

		objDao = new SettingsDao();

		buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		SPanel pan = new SPanel();
		pan.setSizeFull();

		try {

			ledgerList = new LedgerDao().getAllActiveLedgerNames();

			List notSpecList = new ArrayList();
			notSpecList.add(new LedgerModel(0, "No Specified Account"));
			notSpecList.addAll(ledgerList);

			cessEnable = new SCheckBox(getPropertyName("cess_enabled"), false);
			cesspercentageTextField = new STextField(getPropertyName("cess_percentage"), 200);
			cesspercentageTextField.setStyleName("textfield_align_right");
			cesspercentageTextField.setValue("0.01");

			manufacturingDatesEnable = new SCheckBox(
					getPropertyName("use_manufacturing_expiry_date"), false);
			discountEnable = new SCheckBox(getPropertyName("enable_discount"),false);
			salesDiscountEnable = new SCheckBox(getPropertyName("sales_enable_discount"),false);
//			exciseDutyEnable = new SCheckBox(
//					getPropertyName("enable_excise_duty"), false);
//			shippingChargeEnable = new SCheckBox(
//					getPropertyName("enable_shipping_charge"), false);
			finYearBackEntryEnable = new SCheckBox(
					getPropertyName("enable_financial_year_back_entry"), false);

			taxEnable = new SCheckBox(getPropertyName("tax_enabled"), false);

			hideOrganizationDetails = new SCheckBox(
					getPropertyName("hide_organization_details"), false);

			final SButton save = new SButton(getPropertyName("save"));
			final SButton cancel = new SButton(getPropertyName("cancel"));

			expendetureAcctShow = new SRadioButton(
					getPropertyName("expenditure_accounts"), 200,
					Arrays.asList(new KeyValue((int) 1, "Cash Only"),
							new KeyValue((int) 2, "Bank & Cash")), "intKey",
					"value");
			expendetureAcctShow.setValue(1);
			expendetureAcctShow.setHorizontal(true);

			showAllEmplyeeOnPayrol = new SCheckBox(
					getPropertyName("show_all_employees_on_payroll"), false);
			barcodeEnabled = new SCheckBox(getPropertyName("enable_barcode"),
					false);
			
			itemGroupFilterInSales = new SCheckBox(getPropertyName("item_group_filter_in_sales"),
					false);
			
			defaultCustomer = new SComboField(
					getPropertyName("default_customer_name"),
					200,
					new CustomerDao()
							.getAllActiveCustomerNamesWithLedgerID(getOfficeID()),
					"id", "name", false, getPropertyName("select"));

			salePriceEditable = new SCheckBox(
					getPropertyName("sale_price_editable"), true);
			paymentBillSelectionMandatory = new SCheckBox(
					getPropertyName("bill_selection_mandatory_payments"), false);
			disableSalesForUnderCrLimitCustomer = new SCheckBox(
					getPropertyName("disable_sales_customers_under_credit_limit"),
					false);
			alertForUnderCrLimit = new SCheckBox(
					getPropertyName("alert_for_under_credit_limit"),
					false);
			keepDeletedData = new SCheckBox(
					getPropertyName("keep_deleted_data"), false);
			autocreateSubgroupCode = new SCheckBox(
					getPropertyName("auto_create_item_subgroup_code"), false);
			autocreateSupplierCode = new SCheckBox(
					getPropertyName("auto_create_supplier_code"), false);
			autocreateCustomerCode = new SCheckBox(
					getPropertyName("auto_create_customer_code"), false);
			useSalesNumberInSalesOrder = new SCheckBox(
					getPropertyName("use_sales_number_sales_order"), false);

			useSystemMailForSendCustomerMail = new SCheckBox(
					getPropertyName("use_system_mail_send_customer_mail"),
					false);
			keepOtherWindows = new SCheckBox(
					getPropertyName("dont_close_other_windows"), false);
			hideAlerts = new SCheckBox(getPropertyName("hide_alerts"), false);
			gradingEnabled = new SCheckBox(getPropertyName("grading_enabled"),
					false);
//			localForeignTypeEnabled = new SCheckBox(
//					getPropertyName("local_foreign_type_enabled"), false);
//			localForeignTypeEnabled
//					.setDescription("Local And Foreign Type Enabled In Purchase & Sales");
			rackEnabled = new SCheckBox(getPropertyName("rack_enabled"), false);
			salesManWiseSales = new SCheckBox(getPropertyName("sales_man_wise_sales"), false);
			purchaseOrderExpiry = new SCheckBox(getPropertyName("purchase_order_expiry"), false);
			itemInMultipleLang = new SCheckBox(getPropertyName("item_in_multiple_language"), false);
			supplierSpecificItemCheckBox = new SCheckBox(getPropertyName("show_supplier_specific_item_in_purchase"), false);
			
			multipleCurrencyEnabled = new SCheckBox(
					getPropertyName("enable_multiple_currency"), false);
			useGrossAndNetWeight = new SCheckBox(
					getPropertyName("use_gross_net_weight"), false);
			useGrossAndNetWeight
					.setDescription("Show gross and net weight in purchase");
			useSalesRateFromStock = new SCheckBox(
					getPropertyName("use_sales_rate_stock"), false);
			showStockInProfitReport = new SCheckBox(
					getPropertyName("show_stock_in_profit_report"), false);
			salesNoCreationManual = new SCheckBox(
					getPropertyName("create_invoice_manually"), false);
			
			showItemContainerNo = new SCheckBox(getPropertyName("show_item_container"), false);
			showItemAttributes = new SCheckBox(getPropertyName("show_item_attributes"), false);
			
			departmentEnabled = new SCheckBox(getPropertyName("enable_department"), false);
			divisionEnabled = new SCheckBox(getPropertyName("enable_division"), false);
			commissionEnabled = new SCheckBox(getPropertyName("enable_commission_salary"), false);
			
			alertMail = new SCheckBox(getPropertyName("mail"), false);
			alertNofification = new SCheckBox(getPropertyName("notification"), false);
			alertMailField=new STextField("Send Email To",150);
			alertMailField.setDescription("Separate emailids by , ");
			alertMailField.setVisible(false);
			
			salesOrderEnable = new SCheckBox(getPropertyName("enable_sales_order_for_sales"), false);
			
			SGridLayout grid=new SGridLayout(2,2);
			grid.setCaption(getPropertyName("alert"));
			grid.setSizeFull();
			grid.addComponent(alertMail);
			grid.addComponent(alertMailField);
			grid.addComponent(alertNofification);
//			grid.setComponentAlignment(alertMail, Alignment.MIDDLE_LEFT);
//			grid.setComponentAlignment(alertNofification, Alignment.MIDDLE_LEFT);

			itemRateUpdate = new SNativeSelect(getPropertyName("update_rate"),
					100, SConstants.rateAndConvQty_update.rate_update_select,
					"intKey", "value");
			itemRateUpdate.setValue((int) 0);
			itemRateUpdate
					.setDescription("Update the Convertion quantity and rate of an item when doing Sales and Purchase.");
			
			currencyFormat = new SNativeSelect(getPropertyName("currency_format"),
					100, SConstants.currencyFormat.currencyFormat,"intKey", "value");
			currencyFormat.setValue((int) 0);
			currencyFormat.setDescription("Format of curency in words");
			
			profitCalculation = new SNativeSelect(getPropertyName("profit_calculation"),
					100, SConstants.profitCalcutaion.profitCalcutaion,"intKey", "value");
			profitCalculation.setValue((int) 1);
			profitCalculation.setDescription("profit calculation method");
			
			barcodeType = new SNativeSelect(getPropertyName("barcode_type"),
					150, SConstants.barcode_types.barcode_types,"intKey", "value");
			barcodeType.setValue((int) 1);
			barcodeType.setDescription("barcode_customer_or_stock_specific");
			
			payrollCalculation = new SNativeSelect(getPropertyName("payroll_calculation"),
					150, SConstants.payrollCalculation.payrollCalculation,"intKey", "value");
			payrollCalculation.setValue((int) 1);
//			payrollCalculation.setDescription("barcode_customer_or_stock_specific");

			SFormLayout content = new SFormLayout();
			content.setMargin(true);
			SFormLayout contentRight = new SFormLayout();
			contentRight.setMargin(true);
			SFormLayout contenMid = new SFormLayout();
			contenMid.setMargin(true);
			SFormLayout mainForm = new SFormLayout();
			mainForm.setSpacing(true);
			mainForm.setSizeFull();

			SGridLayout hlay = new SGridLayout(4, 1);
//			hlay.setSizeFull();
			hlay.setSpacing(true);
			hlay.setHeight("400px");

			hlay.addComponent(content, 0, 0);
			hlay.addComponent(contenMid, 1, 0);
			hlay.addComponent(contentRight, 2, 0);

			mainForm.addComponent(hlay);

			content.setMargin(true);

			content.addComponent(taxEnable);
			content.addComponent(cessEnable);
			content.addComponent(cesspercentageTextField);
			content.addComponent(manufacturingDatesEnable);
			content.addComponent(discountEnable);
			content.addComponent(salesDiscountEnable);
//			content.addComponent(exciseDutyEnable);
//			content.addComponent(shippingChargeEnable);
			content.addComponent(finYearBackEntryEnable);
			content.addComponent(hideOrganizationDetails);
			content.addComponent(expendetureAcctShow);
			content.addComponent(defaultCustomer);
			content.addComponent(showAllEmplyeeOnPayrol);
			content.addComponent(barcodeEnabled);
			content.addComponent(itemGroupFilterInSales);
			content.addComponent(barcodeType);
			content.addComponent(payrollCalculation);
			content.addComponent(salePriceEditable);
			content.addComponent(paymentBillSelectionMandatory);
			
			contenMid.addComponent(disableSalesForUnderCrLimitCustomer);
			contenMid.addComponent(alertForUnderCrLimit);
			contenMid.addComponent(keepDeletedData);
			contenMid.addComponent(autocreateSubgroupCode);
			contenMid.addComponent(autocreateSupplierCode);
			contenMid.addComponent(autocreateCustomerCode);
			contenMid.addComponent(useSalesNumberInSalesOrder);
			contenMid.addComponent(useSystemMailForSendCustomerMail);
			contenMid.addComponent(keepOtherWindows);
			contenMid.addComponent(hideAlerts);
			contenMid.addComponent(gradingEnabled);
//			contenMid.addComponent(localForeignTypeEnabled);
			contenMid.addComponent(rackEnabled);
			contenMid.addComponent(salesManWiseSales);
			contenMid.addComponent(purchaseOrderExpiry);
			contenMid.addComponent(itemInMultipleLang);
			
			contenMid.addComponent(supplierSpecificItemCheckBox);
			contentRight.addComponent(multipleCurrencyEnabled);
			contentRight.addComponent(useGrossAndNetWeight);
			contentRight.addComponent(useSalesRateFromStock);
			contentRight.addComponent(showStockInProfitReport);
			contentRight.addComponent(showItemContainerNo);
			contentRight.addComponent(showItemAttributes);
			contentRight.addComponent(itemRateUpdate);
			contentRight.addComponent(currencyFormat);
			contentRight.addComponent(profitCalculation);
			contentRight.addComponent(salesNoCreationManual);
			contentRight.addComponent(departmentEnabled);
			contentRight.addComponent(divisionEnabled);
			contentRight.addComponent(commissionEnabled);
			contentRight.addComponent(salesOrderEnable);
			contentRight.addComponent(grid);
			
			

			SGridLayout btnGrid = new SGridLayout(8, 1);
			btnGrid.setWidth("70%");
			buttonLayout.addComponent(save);
			buttonLayout.addComponent(cancel);
			btnGrid.addComponent(buttonLayout, 4, 0);
			mainForm.addComponent(btnGrid);
			pan.setContent(mainForm);
			
			alertMail.addValueChangeListener(new  ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(alertMail.getValue())
						alertMailField.setVisible(true);
					else
						alertMailField.setVisible(false);
				}
			});
			
			barcodeEnabled.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(barcodeEnabled.getValue())
						barcodeType.setVisible(true);
					else
						barcodeType.setVisible(false);
				}
			});
			


			// Loading the settings

			List settingsValueList = objDao.getSettings(getOfficeID());

			Iterator it = settingsValueList.iterator();
			while (it.hasNext()) {
				SettingsModel obj = (SettingsModel) it.next();

				if (obj.getSettings_name().equals(
						SConstants.settings.CESS_ENABLED)) {
					cessEnable.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CESS_PERCENTAGE)) {
					cesspercentageTextField.setValue(obj.getValue());
				} else if (obj.getSettings_name().equals(
						SConstants.settings.TAX_ENABLED)) {
					taxEnable.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(SConstants.settings.MANUFACTURING_DATES_ENABLE)) {
					manufacturingDatesEnable.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(SConstants.settings.DISCOUNT_ENABLE)) {
					discountEnable.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(SConstants.settings.SALES_DISCOUNT_ENABLED)) {
					salesDiscountEnable.setValue(Boolean.parseBoolean(obj.getValue()));
//				} else if (obj.getSettings_name().equals(
//						SConstants.settings.EXCISE_DUTY_ENABLE)) {
//					exciseDutyEnable.setValue(Boolean.parseBoolean(obj
//							.getValue()));
//				} else if (obj.getSettings_name().equals(
//						SConstants.settings.SHIPPINGCHARGEENABLE)) {
//					shippingChargeEnable.setValue(Boolean.parseBoolean(obj
//							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.FIN_YEAR_BACK_ENTRY_ENABLE)) {
					finYearBackEntryEnable.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.HIDE_ORGANIZATION_DETAILS)) {
					hideOrganizationDetails.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.EXPENDETURE_SHOW_ACCOUNTS)) {
					expendetureAcctShow.setValue(toInt(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SHOW_ALL_EMPLOYEES_ON_PAYROLL)) {
					showAllEmplyeeOnPayrol.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.BARCODE_ENABLED)) {
					barcodeEnabled
							.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.ITEM_GROUP_FILTER_IN_SALES)) {
					itemGroupFilterInSales
							.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.DEFAULT_CUSTOMER)) {
					defaultCustomer.setValue(Long.parseLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.KEEP_DELETED_DATA)) {
					keepDeletedData.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.AUTO_CREATE_SUBGROUP_CODE)) {
					autocreateSubgroupCode.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.AUTO_CREATE_SUPPLIER_CODE)) {
					autocreateSupplierCode.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.AUTO_CREATE_CUSTOMER_CODE)) {
					autocreateCustomerCode.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.USE_SALES_NO_IN_SALES_ORDER)) {
					useSalesNumberInSalesOrder.setValue(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj
						.getSettings_name()
						.equals(SConstants.settings.USE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL)) {
					useSystemMailForSendCustomerMail.setValue(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.KEEP_OTHER_WINDOWS)) {
					keepOtherWindows.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.HIDE_ALERTS)) {
					hideAlerts.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.GRADING_ENABLED)) {
					gradingEnabled
							.setValue(Boolean.parseBoolean(obj.getValue()));
//				} else if (obj.getSettings_name().equals(
//						SConstants.settings.LOCAL_FOREIGN_TYPE_ENABLED)) {
//					localForeignTypeEnabled.setValue(Boolean.parseBoolean(obj
//							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.RACK_ENABLED)) {
					rackEnabled.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.MULTIPLE_CURRENCY_ENABLED)) {
					multipleCurrencyEnabled.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.USE_GROSS_AND_NET_WEIGHT)) {
					useGrossAndNetWeight.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.USE_SALES_RATE_FROM_STOCK)) {
					useSalesRateFromStock.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SHOW_STOCK_IN_PROFIT_REPORT)) {
					showStockInProfitReport.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(SConstants.settings.SALES_NO_CREATION_MANUAL)) {
					salesNoCreationManual.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} 
				else if (obj.getSettings_name().equals(SConstants.settings.SHOW_CONTAINER_NO)) {
					showItemContainerNo.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SHOW_ITEM_ATTRIBUTES)) {
					showItemAttributes.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				
				else if (obj.getSettings_name().equals(SConstants.settings.DEPARTMENT_ENABLED)) {
					departmentEnabled.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.DIVISION_ENABLED)) {
					divisionEnabled.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.COMMISSION_SALARY_ENABLED)) {
					commissionEnabled.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				
				else if (obj.getSettings_name().equals(
						SConstants.settings.SALE_PRICE_EDITABLE)) {
					salePriceEditable.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj
						.getSettings_name()
						.equals(SConstants.settings.DISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT)) {
					disableSalesForUnderCrLimitCustomer.setValue(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj
						.getSettings_name()
						.equals(SConstants.settings.ALERT_FOR_UNDER_CREDIT_LIMIT)) {
					alertForUnderCrLimit.setValue(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PAYMENT_BILL_SELECTION_MANDATORY)) {
					paymentBillSelectionMandatory.setValue(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.UPDATE_RATE_AND_CONV_QTY)) {
					itemRateUpdate.setValue(Integer.parseInt(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CURRENCY_FORMAT)) {
					currencyFormat.setValue(Integer.parseInt(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PROFIT_CALCULATION)) {
					profitCalculation.setValue(Integer.parseInt(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.BARCODE_TYPE)) {
					barcodeType.setValue(Integer.parseInt(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PAYROLL_CALCULATION)) {
					payrollCalculation.setValue(Integer.parseInt(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.ALERT_EMAIL)) {
					alertMail.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.ALERT_NOTIFICATION)) {
					alertNofification.setValue(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.ALERT_EMAILIDS)) {
					alertMailField.setValue(obj
							.getValue().toString());
			
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_MAN_WISE_SALES)) {
					salesManWiseSales.setValue(Boolean.parseBoolean(obj.getValue()));
				} 
				else if (obj.getSettings_name().equals(
						SConstants.settings.PURCHSE_ORDER_EXPIRY)) {
					purchaseOrderExpiry.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.ITEMS_IN_MULTIPLE_LANGUAGE)) {
					itemInMultipleLang.setValue(Boolean.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE)) {
					supplierSpecificItemCheckBox.setValue(Boolean.parseBoolean(obj.getValue()));
				}else if (obj.getSettings_name().equals(SConstants.settings.SALES_ORDER_FOR_SALES)) {
					salesOrderEnable.setValue(Boolean.parseBoolean(obj.getValue()));
				}
				
			}
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					save.click();
				}
			});

			save.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							long office_id = getOfficeID();

							List settingsList = new ArrayList();
							SettingsModel objModel;

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.CESS_ENABLED);
							objModel.setValue(cessEnable.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.CESS_PERCENTAGE);
							objModel.setValue(cesspercentageTextField
									.getValue());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.TAX_ENABLED);
							objModel.setValue(taxEnable.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.MANUFACTURING_DATES_ENABLE);
							objModel.setValue(manufacturingDatesEnable
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.DISCOUNT_ENABLE);
							objModel.setValue(discountEnable.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_DISCOUNT_ENABLED);
							objModel.setValue(salesDiscountEnable.getValue().toString());
							settingsList.add(objModel);

//							objModel = new SettingsModel();
//							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
//							objModel.setLevel_id(office_id);
//							objModel.setSettings_name(SConstants.settings.EXCISE_DUTY_ENABLE);
//							objModel.setValue(exciseDutyEnable.getValue()
//									.toString());
//							settingsList.add(objModel);
//
//							objModel = new SettingsModel();
//							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
//							objModel.setLevel_id(office_id);
//							objModel.setSettings_name(SConstants.settings.SHIPPINGCHARGEENABLE);
//							objModel.setValue(shippingChargeEnable.getValue()
//									.toString());
//							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.FIN_YEAR_BACK_ENTRY_ENABLE);
							objModel.setValue(finYearBackEntryEnable.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.HIDE_ORGANIZATION_DETAILS);
							objModel.setValue(hideOrganizationDetails
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.EXPENDETURE_SHOW_ACCOUNTS);
							objModel.setValue(expendetureAcctShow.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SHOW_ALL_EMPLOYEES_ON_PAYROLL);
							objModel.setValue(showAllEmplyeeOnPayrol.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.BARCODE_ENABLED);
							objModel.setValue(barcodeEnabled.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.ITEM_GROUP_FILTER_IN_SALES);
							objModel.setValue(itemGroupFilterInSales.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.DEFAULT_CUSTOMER);
							if (defaultCustomer.getValue() == null
									|| defaultCustomer.getValue().equals(""))
								objModel.setValue("0");
							else
								objModel.setValue(defaultCustomer.getValue()
										.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.KEEP_DELETED_DATA);
							objModel.setValue(keepDeletedData.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.AUTO_CREATE_SUBGROUP_CODE);
							objModel.setValue(autocreateSubgroupCode.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.AUTO_CREATE_SUPPLIER_CODE);
							objModel.setValue(autocreateSupplierCode.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.AUTO_CREATE_CUSTOMER_CODE);
							objModel.setValue(autocreateCustomerCode.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.USE_SALES_NO_IN_SALES_ORDER);
							objModel.setValue(useSalesNumberInSalesOrder
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.USE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL);
							objModel.setValue(useSystemMailForSendCustomerMail
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.KEEP_OTHER_WINDOWS);
							objModel.setValue(keepOtherWindows.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.HIDE_ALERTS);
							objModel.setValue(hideAlerts.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.GRADING_ENABLED);
							objModel.setValue(gradingEnabled.getValue()
									.toString());
							settingsList.add(objModel);

//							objModel = new SettingsModel();
//							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
//							objModel.setLevel_id(office_id);
//							objModel.setSettings_name(SConstants.settings.LOCAL_FOREIGN_TYPE_ENABLED);
//							objModel.setValue(localForeignTypeEnabled
//									.getValue().toString());
//							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.RACK_ENABLED);
							objModel.setValue(rackEnabled.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.USE_GROSS_AND_NET_WEIGHT);
							objModel.setValue(useGrossAndNetWeight.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.USE_SALES_RATE_FROM_STOCK);
							objModel.setValue(useSalesRateFromStock.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SHOW_STOCK_IN_PROFIT_REPORT);
							objModel.setValue(showStockInProfitReport.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_NO_CREATION_MANUAL);
							objModel.setValue(salesNoCreationManual.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SHOW_CONTAINER_NO);
							objModel.setValue(showItemContainerNo.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SHOW_ITEM_ATTRIBUTES);
							objModel.setValue(showItemAttributes.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.MULTIPLE_CURRENCY_ENABLED);
							objModel.setValue(multipleCurrencyEnabled
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALE_PRICE_EDITABLE);
							objModel.setValue(salePriceEditable.getValue()
									.toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.DISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT);
							objModel.setValue(disableSalesForUnderCrLimitCustomer
									.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.ALERT_FOR_UNDER_CREDIT_LIMIT);
							objModel.setValue(alertForUnderCrLimit
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.PAYMENT_BILL_SELECTION_MANDATORY);
							objModel.setValue(paymentBillSelectionMandatory
									.getValue().toString());
							settingsList.add(objModel);

							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.UPDATE_RATE_AND_CONV_QTY);
							objModel.setValue(itemRateUpdate.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.CURRENCY_FORMAT);
							objModel.setValue(currencyFormat.getValue()
									.toString());
							settingsList.add(objModel);
							
					//----------------------ALERT--------------------//		
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.ALERT_EMAIL);
							objModel.setValue(alertMail.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.ALERT_NOTIFICATION);
							objModel.setValue(alertNofification.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.ALERT_EMAILIDS);
							objModel.setValue(alertMailField.getValue()
									.toString());
							settingsList.add(objModel);
							
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.DEPARTMENT_ENABLED);
							objModel.setValue(departmentEnabled.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.DIVISION_ENABLED);
							objModel.setValue(divisionEnabled.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.COMMISSION_SALARY_ENABLED);
							objModel.setValue(commissionEnabled.getValue().toString());
							settingsList.add(objModel);
					//----------------------ALERT--------------------//		
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.PROFIT_CALCULATION);
							objModel.setValue(profitCalculation.getValue()
									.toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.BARCODE_TYPE);
							objModel.setValue(barcodeType.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.PROFIT_CALCULATION);
							objModel.setValue(payrollCalculation.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_MAN_WISE_SALES);
							objModel.setValue(salesManWiseSales.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.PURCHSE_ORDER_EXPIRY);
							objModel.setValue(purchaseOrderExpiry.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.ITEMS_IN_MULTIPLE_LANGUAGE);
							objModel.setValue(itemInMultipleLang.getValue().toString());
							settingsList.add(objModel);
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE);
							objModel.setValue(supplierSpecificItemCheckBox.getValue().toString());
							settingsList.add(objModel);
							
							System.out.println("Sales Enabled value---"+salesOrderEnable.getValue().toString());
							
							objModel = new SettingsModel();
							objModel.setLevel(SConstants.scopes.OFFICE_LEVEL_GENERAL);
							objModel.setLevel_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_ORDER_FOR_SALES);
							objModel.setValue(salesOrderEnable.getValue().toString());
							settingsList.add(objModel);

							if (finYearBackEntryEnable.getValue())
								getHttpSession().setAttribute(
										"fin_yr_back_entry", true);

							try {
								objDao.saveOrganizationSettings(settingsList,
										office_id);

								// lomd.addOptionsToUserFromRole(id,
								// (Long)currency.getValue());
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								new SettingsBiz().updateSettingsValue(
										getOrganizationID(), getOfficeID());

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("error"),
										Type.WARNING_MESSAGE);
								e.printStackTrace();
							}

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
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
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			taxEnable.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					if (taxEnable.getValue() == false) {
						cessEnable.setValue(false);
					}
				}
			});

			cessEnable.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					if (cessEnable.getValue() == true) {
						taxEnable.setValue(true);
					}
				}
			});
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pan;
	}

	public void closeWindow() {
		this.close();
	}
	

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (cesspercentageTextField.getValue() == null
				|| cesspercentageTextField.getValue().equals("")) {
			setRequiredError(cesspercentageTextField,
					getPropertyName("enter_cess_percentage"), true);
			cesspercentageTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(cesspercentageTextField.getValue()) < 0) {
					setRequiredError(cesspercentageTextField,
							getPropertyName("enter_valid_value"), true);
					cesspercentageTextField.focus();
					ret = false;
				} else
					setRequiredError(cesspercentageTextField, null, false);
			} catch (Exception e) {
				setRequiredError(cesspercentageTextField,
						getPropertyName("enter_valid_value"), true);
				cesspercentageTextField.focus();
				ret = false;
			}
		}
		if(alertMail.getValue()){
			if(alertMailField.getValue()==null||alertMailField.getValue().toString().trim().length()<=0){
				setRequiredError(alertMailField,
						getPropertyName("invalid email"), true);
				alertMailField.focus();
				ret = false;
				
			}else{
				if(!isValidEmail(alertMailField.getValue().toString())){
					setRequiredError(alertMailField,
							getPropertyName("invalid email"), true);
					alertMailField.focus();
					ret = false;
				}else
					alertMailField.setComponentError(null);
			}
		}

		return ret;
	}
	protected boolean isValidEmail(String value) {
		boolean ret = true;
		
		String mailarr[];
		if(value.contains(",")){
			mailarr=value.split(",");
			for(String mail:mailarr){
				if (mail == null || mail.equals("")) {
					ret = false;
					break;
				} else {
					try {
						InternetAddress emailAddr = new InternetAddress(mail);
						emailAddr.validate();
					} catch (Exception ex) {
						ret = false;
						break;
					}
				}
			}
		} else {
			if (value == null || value.equals("")) {
				ret = false;
			} else {
				try {
					InternetAddress emailAddr = new InternetAddress(value);
					emailAddr.validate();
				} catch (Exception ex) {
					ret = false;
				}
			}
		}
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
