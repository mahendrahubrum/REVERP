package com.inventory.config.settings.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.biz.SettingsBiz;
import com.inventory.config.settings.dao.AccountSettingsDao;
import com.inventory.config.settings.model.AccountSettingsModel;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 31, 2013
 */

public class AccountSettingsUI extends SparkLogic {

	private static final long serialVersionUID = 6190191056263591076L;

	AccountSettingsDao objDao = new AccountSettingsDao();

	SVerticalLayout vertLayout;

	SFormLayout salesSettingsContent;
	SFormLayout purchaseSettingsContent;
	SFormLayout commonSettingsContent;
	SFormLayout payrollSettingsContent;

	SComboField inventoryAcctCombo;
	SComboField cashbookAcctCombo;
	SComboField chequeAcctCombo;
	SComboField supplierGrupoCombo;
	SComboField customerGrupoCombo;
	SComboField clearingAgentGroupCombo;
	SComboField cashGroupCombo;

	SComboField salesAcctCombo;
	SComboField salesReturnAcctCombo;
	SComboField salesDiscountCombo;
	SComboField salestaxAcctCombo;
	SComboField salesShippingChargeAcctCombo;
	SComboField cessAcctCombo;
	SComboField salesRevenueAcctCombo;


	SComboField purchaseAcctCombo;
	SComboField purchaseReturnAcctCombo;
	SComboField purchasetaxAcctCombo;
	SComboField purchaseShippingChargeAcctCombo;
	SComboField purchasediscountCombo;
	
	
	SComboField salaryAcctCombo;
	SComboField salaryPayableCombo;
	SComboField salaryLoanAcctCombo;
	SComboField salaryAdvanceAcctCombo;
	
	SComboField forexAcctCombo;

	SWindow newAcctWindow;

	HorizontalLayout buttonLayout = null;

	STabSheet tab;
	SButton newAccountButton;

	private SComboField profitAcctCombo;

	private SComboField lossAcctCombo;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		setSize(454, 520);

		newAcctWindow = new AddNewLedgerUI();
		newAcctWindow.setCaption("Add Account");
		newAcctWindow.center();
		newAcctWindow.setModal(true);

		List ledgerList;

		objDao = new AccountSettingsDao();

		buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		SPanel pan = new SPanel();
		pan.setSizeFull();

		vertLayout = new SVerticalLayout();
		vertLayout.setSpacing(true);

		tab = new STabSheet(null, 450, 420);

		newAccountButton = new SButton();
		newAccountButton.setStyleName("addNewBtnStyle");
		newAccountButton.setDescription("Add new Account");

		try {

			ledgerList = new LedgerDao()
					.getAllActiveGeneralLedgerOnly(getOfficeID());

			inventoryAcctCombo = new SComboField(
					getPropertyName("inventory_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			cashbookAcctCombo = new SComboField(getPropertyName("cash_book"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));
			chequeAcctCombo = new SComboField("Cheque Account",
					200, ledgerList, "id", "name", true, getPropertyName("select"));
			supplierGrupoCombo = new SComboField(
					getPropertyName("supplier_group"), 200, new GroupDao().getAllActiveGroups(getOrganizationID()), "id",
					"name", true, getPropertyName("select"));
			customerGrupoCombo = new SComboField(
					getPropertyName("customer_group"), 200, new GroupDao().getAllActiveGroups(getOrganizationID()), "id",
					"name", true, getPropertyName("select"));
			clearingAgentGroupCombo = new SComboField(
					getPropertyName("clearing_agent_group"), 200, new GroupDao().getAllActiveGroups(getOrganizationID()), "id",
					"name", true, getPropertyName("select"));
			cashGroupCombo = new SComboField(
					getPropertyName("cash_group"), 200, new GroupDao().getAllActiveGroups(getOrganizationID()), "id",
					"name", true, getPropertyName("select"));
			profitAcctCombo = new SComboField(
					getPropertyName("profit_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			lossAcctCombo = new SComboField(
					getPropertyName("loss_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));

			salesAcctCombo = new SComboField(getPropertyName("sales_account"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));
			salesReturnAcctCombo = new SComboField(
					getPropertyName("sales_return_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			salesDiscountCombo = new SComboField(getPropertyName("sales_discount_account"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));
			salestaxAcctCombo = new SComboField(
					getPropertyName("sales_tax_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			salesShippingChargeAcctCombo = new SComboField(
					getPropertyName("shipping_charge_account"), 200,
					ledgerList, "id", "name", true, getPropertyName("select"));
			cessAcctCombo = new SComboField(getPropertyName("cess_account"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));
			
			salesRevenueAcctCombo = new SComboField(
					getPropertyName("sales_revenue_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			forexAcctCombo = new SComboField(
					getPropertyName("forex_account"), 200,
					ledgerList, "id", "name", true, getPropertyName("select"));

			purchaseAcctCombo = new SComboField(
					getPropertyName("purchase_account"), 200, ledgerList, "id",
					"name", true, getPropertyName("select"));
			purchaseReturnAcctCombo = new SComboField(
					getPropertyName("purchase_return_account"), 200,
					ledgerList, "id", "name", true, getPropertyName("select"));
			purchasetaxAcctCombo = new SComboField(
					getPropertyName("purchase_tax_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			purchaseShippingChargeAcctCombo = new SComboField(
					getPropertyName("shipping_charge_account"), 200,
					ledgerList, "id", "name", true, getPropertyName("select"));
			purchasediscountCombo = new SComboField(getPropertyName("purchase_discount_account"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));
		

			salaryAcctCombo = new SComboField(
					getPropertyName("salary_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			salaryPayableCombo = new SComboField(
					getPropertyName("salary_payable"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			salaryAdvanceAcctCombo = new SComboField(
					getPropertyName("salary_advance_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));
			salaryLoanAcctCombo = new SComboField(
					getPropertyName("employee_loan_account"), 200, ledgerList,
					"id", "name", true, getPropertyName("select"));

			final SButton save = new SButton(getPropertyName("save"));
			final SButton cancel = new SButton(getPropertyName("cancel"));

			salesSettingsContent = new SFormLayout();
			commonSettingsContent = new SFormLayout();
			purchaseSettingsContent = new SFormLayout();
			payrollSettingsContent = new SFormLayout();

			commonSettingsContent.setMargin(true);
			purchaseSettingsContent.setMargin(true);
			payrollSettingsContent.setMargin(true);

			commonSettingsContent.setId("commonSett");
			salesSettingsContent.setId("salesSett");
			purchaseSettingsContent.setId("purchaseSett");
			payrollSettingsContent.setId("payrollSett");

			salesSettingsContent.setMargin(true);
			salesSettingsContent.setWidth("280px");
			salesSettingsContent.setHeight("250px");

			commonSettingsContent.addComponent(newAccountButton);
			commonSettingsContent.addComponent(inventoryAcctCombo);
			commonSettingsContent.addComponent(cashbookAcctCombo);
			commonSettingsContent.addComponent(chequeAcctCombo);
			commonSettingsContent.addComponent(forexAcctCombo);
			commonSettingsContent.addComponent(supplierGrupoCombo);
			commonSettingsContent.addComponent(customerGrupoCombo);
			commonSettingsContent.addComponent(clearingAgentGroupCombo);
			commonSettingsContent.addComponent(cashGroupCombo);
			commonSettingsContent.addComponent(profitAcctCombo);
			commonSettingsContent.addComponent(lossAcctCombo);
			

			salesSettingsContent.addComponent(salesAcctCombo);
			salesSettingsContent.addComponent(salesReturnAcctCombo);
			salesSettingsContent.addComponent(salesDiscountCombo);
			salesSettingsContent.addComponent(salestaxAcctCombo);
			salesSettingsContent.addComponent(salesShippingChargeAcctCombo);
			salesSettingsContent.addComponent(cessAcctCombo);
			
			salesSettingsContent.addComponent(salesRevenueAcctCombo);
		

			purchaseSettingsContent.addComponent(purchaseAcctCombo);
			purchaseSettingsContent.addComponent(purchaseReturnAcctCombo);
			purchaseSettingsContent.addComponent(purchasetaxAcctCombo);
			purchaseSettingsContent
					.addComponent(purchaseShippingChargeAcctCombo);
			purchaseSettingsContent.addComponent(purchasediscountCombo);
			
			
			payrollSettingsContent.addComponent(salaryAcctCombo);
			payrollSettingsContent.addComponent(salaryPayableCombo);
			payrollSettingsContent.addComponent(salaryAdvanceAcctCombo);
			payrollSettingsContent.addComponent(salaryLoanAcctCombo);
		

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(cancel);

			// salesSettingsContent.addComponent(buttonLayout);

			tab.addTab(commonSettingsContent,
					getPropertyName("common_settings"));
			tab.addTab(payrollSettingsContent,
					getPropertyName("payroll_settings"));
			tab.addTab(salesSettingsContent, getPropertyName("sales_settings"));
			tab.addTab(purchaseSettingsContent,
					getPropertyName("purchase_settings"));

			vertLayout.addComponent(tab);
			vertLayout.addComponent(buttonLayout);
			vertLayout.setComponentAlignment(buttonLayout,
					Alignment.MIDDLE_CENTER);

			// Loading the settings

			List settingsValueList = objDao.getAccountSettings(getOfficeID());

			Iterator it = settingsValueList.iterator();
			while (it.hasNext()) {
				AccountSettingsModel obj = (AccountSettingsModel) it.next();

				if (obj.getSettings_name().equals(
						SConstants.settings.INVENTORY_ACCOUNT)) {
					inventoryAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CASH_ACCOUNT)) {
					cashbookAcctCombo.setValue(toLong(obj.getValue()));
				}else if (obj.getSettings_name().equals(
						SConstants.settings.PROFIT_ACCOUNT)) {
					profitAcctCombo.setValue(toLong(obj.getValue()));
				}else if (obj.getSettings_name().equals(
						SConstants.settings.LOSS_ACCOUNT)) {
					lossAcctCombo.setValue(toLong(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.CHEQUE_ACCOUNT)) {
					chequeAcctCombo.setValue(toLong(obj.getValue()));
				}else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_ACCOUNT)) {
					salesAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_RETURN_ACCOUNT)) {
					salesReturnAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_DESCOUNT_ACCOUNT)) {
					salesDiscountCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_TAX_ACCOUNT)) {
					salestaxAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_SHIPPING_CHARGE_ACCOUNT)) {
					salesShippingChargeAcctCombo
							.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CESS_ACCOUNT)) {
					cessAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_REVENUE_ACCOUNT)) {
					salesRevenueAcctCombo.setValue(toLong(obj.getValue()));
				}

				else if (obj.getSettings_name().equals(
						SConstants.settings.PURCHASE_ACCOUNT)) {
					purchaseAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PURCHASE_RETURN_ACCOUNT)) {
					purchaseReturnAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PURCHASE_TAX_ACCOUNT)) {
					purchasetaxAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PURCHASE_SHIPPING_CHARGE_ACCOUNT)) {
					purchaseShippingChargeAcctCombo.setValue(toLong(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.PURCHASE_DISCOUNT_ACCOUNT)) {
					purchasediscountCombo.setValue(toLong(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.FOREX_DIFFERENCE_ACCOUNT)) {
					forexAcctCombo.setValue(toLong(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALARY_ACCOUNT)) {
					salaryAcctCombo
							.setValue(toLong(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SALARY_PAYABLE_ACCOUNT)) {
					salaryPayableCombo.setValue(toLong(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.SALARY_ADVANCE_ACCOUNT)) {
					salaryAdvanceAcctCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALARY_LOAN_ACCOUNT)) {
					salaryLoanAcctCombo.setValue(toLong(obj.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CUSTOMER_GROUP)) {
					customerGrupoCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SUPPLIER_GROUP)) {
					supplierGrupoCombo.setValue(toLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CLEARING_AGENT_GROUP)) {
					clearingAgentGroupCombo.setValue(toLong(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.CASH_GROUP)) {
					cashGroupCombo.setValue(toLong(obj.getValue()));
				}
			}
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					save.click();
				}
			});

			newAcctWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					// TODO Auto-generated method stub
					try {
						List lst = new LedgerDao()
								.getAllActiveGeneralLedgerOnly(getOfficeID());

						CollectionContainer bic = CollectionContainer.fromBeans(
								new LedgerDao()
										.getAllActiveGeneralLedgerOnly(getOfficeID()),
								"id");
						Object temp=null;
						
							temp = inventoryAcctCombo.getValue();
							inventoryAcctCombo.setContainerDataSource(bic);
							inventoryAcctCombo.setItemCaptionPropertyId("name");
							inventoryAcctCombo.setValue(temp);
							
							temp = profitAcctCombo.getValue();
							profitAcctCombo.setContainerDataSource(bic);
							profitAcctCombo.setItemCaptionPropertyId("name");
							profitAcctCombo.setValue(temp);
							
							temp = lossAcctCombo.getValue();
							lossAcctCombo.setContainerDataSource(bic);
							lossAcctCombo.setItemCaptionPropertyId("name");
							lossAcctCombo.setValue(temp);
						
						
							temp = cashbookAcctCombo.getValue();
							cashbookAcctCombo.setContainerDataSource(bic);
							cashbookAcctCombo.setItemCaptionPropertyId("name");
							cashbookAcctCombo.setValue(temp);
							
							temp = chequeAcctCombo.getValue();
							chequeAcctCombo.setContainerDataSource(bic);
							chequeAcctCombo.setItemCaptionPropertyId("name");
							chequeAcctCombo.setValue(temp);
				
							temp = salesAcctCombo.getValue();
							salesAcctCombo.setContainerDataSource(bic);
							salesAcctCombo.setItemCaptionPropertyId("name");
							salesAcctCombo.setValue(temp);
				
							temp = salesReturnAcctCombo.getValue();
							salesReturnAcctCombo.setContainerDataSource(bic);
							salesReturnAcctCombo
									.setItemCaptionPropertyId("name");
							salesReturnAcctCombo.setValue(temp);

							
						temp = salesDiscountCombo.getValue();
						salesDiscountCombo.setContainerDataSource(bic);
						salesDiscountCombo.setItemCaptionPropertyId("name");
						salesDiscountCombo.setValue(temp);

						temp = salestaxAcctCombo.getValue();
						salestaxAcctCombo.setContainerDataSource(bic);
						salestaxAcctCombo.setItemCaptionPropertyId("name");
						salestaxAcctCombo.setValue(temp);

						temp = salesShippingChargeAcctCombo.getValue();
						salesShippingChargeAcctCombo
								.setContainerDataSource(bic);
						salesShippingChargeAcctCombo
								.setItemCaptionPropertyId("name");
						salesShippingChargeAcctCombo.setValue(temp);
						
						temp = cessAcctCombo.getValue();
						cessAcctCombo.setContainerDataSource(bic);
						cessAcctCombo.setItemCaptionPropertyId("name");
						cessAcctCombo.setValue(temp);

						
						temp = salesRevenueAcctCombo.getValue();
						salesRevenueAcctCombo.setContainerDataSource(bic);
						salesRevenueAcctCombo.setItemCaptionPropertyId("name");
						salesRevenueAcctCombo.setValue(temp);
					
						temp = purchaseAcctCombo.getValue();
						purchaseAcctCombo.setContainerDataSource(bic);
						purchaseAcctCombo.setItemCaptionPropertyId("name");
						purchaseAcctCombo.setValue(temp);
						
				
						temp = purchaseReturnAcctCombo.getValue();
						purchaseReturnAcctCombo.setContainerDataSource(bic);
						purchaseReturnAcctCombo
								.setItemCaptionPropertyId("name");
						purchaseReturnAcctCombo.setValue(temp);
						
						temp = purchasetaxAcctCombo.getValue();
						purchasetaxAcctCombo.setContainerDataSource(bic);
						purchasetaxAcctCombo.setItemCaptionPropertyId("name");
						purchasetaxAcctCombo.setValue(temp);

						temp = purchaseShippingChargeAcctCombo.getValue();
						purchaseShippingChargeAcctCombo
								.setContainerDataSource(bic);
						purchaseShippingChargeAcctCombo
								.setItemCaptionPropertyId("name");
						purchaseShippingChargeAcctCombo.setValue(temp);
						
						temp = purchasediscountCombo.getValue();
						purchasediscountCombo
						.setContainerDataSource(bic);
						purchasediscountCombo
						.setItemCaptionPropertyId("name");
						purchasediscountCombo.setValue(temp);
						
						temp = forexAcctCombo.getValue();
						forexAcctCombo.setContainerDataSource(bic);
						forexAcctCombo
								.setItemCaptionPropertyId("name");
						forexAcctCombo.setValue(temp);
						
						temp = salaryAcctCombo.getValue();
						salaryAcctCombo.setContainerDataSource(bic);
						salaryAcctCombo.setItemCaptionPropertyId("name");
						salaryAcctCombo.setValue(temp);
						
						temp = salaryPayableCombo.getValue();
						salaryPayableCombo.setContainerDataSource(bic);
						salaryPayableCombo.setItemCaptionPropertyId("name");
						salaryPayableCombo.setValue(temp);
						
						temp = salaryAdvanceAcctCombo.getValue();
						salaryAdvanceAcctCombo.setContainerDataSource(bic);
						salaryAdvanceAcctCombo
								.setItemCaptionPropertyId("name");
						salaryAdvanceAcctCombo.setValue(temp);
						
						temp = salaryLoanAcctCombo.getValue();
						salaryLoanAcctCombo.setContainerDataSource(bic);
						salaryLoanAcctCombo
							.setItemCaptionPropertyId("name");
						salaryLoanAcctCombo.setValue(temp);

//						temp = customerGrupoCombo.getValue();
//						customerGrupoCombo.setContainerDataSource(bic);
//						customerGrupoCombo
//								.setItemCaptionPropertyId("name");
//						customerGrupoCombo.setValue(temp);
//						
//						temp = supplierGrupoCombo.getValue();
//						supplierGrupoCombo.setContainerDataSource(bic);
//						supplierGrupoCombo
//							.setItemCaptionPropertyId("name");
//						supplierGrupoCombo.setValue(temp);
//						
//						temp = clearingAgentGrupoCombo.getValue();
//						clearingAgentGrupoCombo.setContainerDataSource(bic);
//						clearingAgentGrupoCombo
//							.setItemCaptionPropertyId("name");
//						clearingAgentGrupoCombo.setValue(temp);

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			pan.setContent(vertLayout);

			save.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							long office_id = getOfficeID();

							List settingsList = new ArrayList();
							AccountSettingsModel objModel;

							if (inventoryAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.INVENTORY_ACCOUNT);
							objModel.setValue(inventoryAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (profitAcctCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.PROFIT_ACCOUNT);
								objModel.setValue(profitAcctCombo.getValue()
										.toString());
								objModel.setType(1);
								settingsList.add(objModel);
								}
							
							if (lossAcctCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.LOSS_ACCOUNT);
								objModel.setValue(lossAcctCombo.getValue()
										.toString());
								objModel.setType(1);
								settingsList.add(objModel);
								}
							
							if (cashbookAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.CASH_ACCOUNT);
							objModel.setValue(cashbookAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (chequeAcctCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.CHEQUE_ACCOUNT);
								objModel.setValue(chequeAcctCombo.getValue().toString());
								objModel.setType(1);
								settingsList.add(objModel);
							}

							if (salesAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_ACCOUNT);
							objModel.setValue(salesAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}

							if (salesReturnAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_RETURN_ACCOUNT);
							objModel.setValue(salesReturnAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (salesDiscountCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_DESCOUNT_ACCOUNT);
							objModel.setValue(salesDiscountCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}

							if (salestaxAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_TAX_ACCOUNT);
							objModel.setValue(salestaxAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (salesShippingChargeAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_SHIPPING_CHARGE_ACCOUNT);
							objModel.setValue(salesShippingChargeAcctCombo
									.getValue().toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (cessAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.CESS_ACCOUNT);
							objModel.setValue(cessAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (salesRevenueAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SALES_REVENUE_ACCOUNT);
							objModel.setValue(salesRevenueAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (purchaseAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.PURCHASE_ACCOUNT);
							objModel.setValue(purchaseAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (purchaseReturnAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.PURCHASE_RETURN_ACCOUNT);
							objModel.setValue(purchaseReturnAcctCombo
									.getValue().toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}

							if (purchasetaxAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.PURCHASE_TAX_ACCOUNT);
							objModel.setValue(purchasetaxAcctCombo.getValue()
									.toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}

							if (purchaseShippingChargeAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.PURCHASE_SHIPPING_CHARGE_ACCOUNT);
							objModel.setValue(purchaseShippingChargeAcctCombo
									.getValue().toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							
							if (purchasediscountCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.PURCHASE_DISCOUNT_ACCOUNT);
								objModel.setValue(purchasediscountCombo
										.getValue().toString());
								objModel.setType(1);
								settingsList.add(objModel);
							}
							
							if (forexAcctCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.FOREX_DIFFERENCE_ACCOUNT);
							objModel.setValue(forexAcctCombo
									.getValue().toString());
							objModel.setType(1);
							settingsList.add(objModel);
							}
							

							if (salaryAcctCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.SALARY_ACCOUNT);
								objModel.setValue(salaryAcctCombo
									.getValue().toString());
								objModel.setType(1);
								settingsList.add(objModel);
							}
							
							if (salaryPayableCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.SALARY_PAYABLE_ACCOUNT);
								objModel.setValue(salaryPayableCombo.getValue().toString());
								objModel.setType(1);
								settingsList.add(objModel);
							}
							
							if (salaryAdvanceAcctCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.SALARY_ADVANCE_ACCOUNT);
								objModel.setValue(salaryAdvanceAcctCombo
										.getValue().toString());
								objModel.setType(1);
								settingsList.add(objModel);
								}
							
							if (salaryLoanAcctCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.SALARY_LOAN_ACCOUNT);
								objModel.setValue(salaryLoanAcctCombo
										.getValue().toString());
								objModel.setType(1);
								settingsList.add(objModel);
							}
							
							
							if (customerGrupoCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.CUSTOMER_GROUP);
							objModel.setValue(customerGrupoCombo
									.getValue().toString());
							objModel.setType(2);
							settingsList.add(objModel);
							}
							
							if (supplierGrupoCombo.getValue() != null) {
							objModel = new AccountSettingsModel();
							objModel.setOffice_id(office_id);
							objModel.setSettings_name(SConstants.settings.SUPPLIER_GROUP);
							objModel.setValue(supplierGrupoCombo
									.getValue().toString());
							objModel.setType(2);
							settingsList.add(objModel);
							}
							
							if (clearingAgentGroupCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.CLEARING_AGENT_GROUP);
								objModel.setValue(clearingAgentGroupCombo
										.getValue().toString());
								objModel.setType(2);
								settingsList.add(objModel);
							}
							
							if (cashGroupCombo.getValue() != null) {
								objModel = new AccountSettingsModel();
								objModel.setOffice_id(office_id);
								objModel.setSettings_name(SConstants.settings.CASH_GROUP);
								objModel.setValue(cashGroupCombo.getValue().toString());
								objModel.setType(2);
								settingsList.add(objModel);
							}

							try {
								objDao.saveAccountSettings(settingsList,
										office_id);

								// lomd.addOptionsToUserFromRole(id,
								// (Long)currency.getValue());
								Notification.show(
										getPropertyName("save_successfully"),
										Type.WARNING_MESSAGE);

								new SettingsBiz().updateSettingsValue(
										getOrganizationID(), getOfficeID());

								// if(getHttpSession().getAttribute("settings_not_set")!=null)
								// {
								//
								// getUI().removeWindow(getUI().getWindows().iterator().next());
								//
								//
								// }

								closeWindow();

							} catch (Exception e) {
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

			newAccountButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						getUI().getCurrent().addWindow(newAcctWindow);

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (getHttpSession().getAttribute("settings_not_set") != null)
				cancel.setVisible(false);

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

//		if (cashbookAcctCombo.getValue() == null
//				|| cashbookAcctCombo.getValue().equals("")) {
//			setRequiredError(cashbookAcctCombo,
//					getPropertyName("select_cash_account"), true);
//			cashbookAcctCombo.focus();
//			ret = false;
//		} else
//			setRequiredError(cashbookAcctCombo, null, false);
//
//
//		if (forexAcctCombo.getValue() == null
//				|| forexAcctCombo.getValue().equals("")) {
//			setRequiredError(forexAcctCombo,
//					getPropertyName("select_forex_account"), true);
//			forexAcctCombo.focus();
//			ret = false;
//		} else
//			setRequiredError(forexAcctCombo, null, false);
		
		if (supplierGrupoCombo.getValue() == null
				|| supplierGrupoCombo.getValue().equals("")) {
			setRequiredError(supplierGrupoCombo,
					getPropertyName("select_supplier_group"), true);
			supplierGrupoCombo.focus();
			ret = false;
		} else
			setRequiredError(supplierGrupoCombo, null, false);
		
		if (customerGrupoCombo.getValue() == null
				|| customerGrupoCombo.getValue().equals("")) {
			setRequiredError(customerGrupoCombo,
					getPropertyName("select_customer_group"), true);
			customerGrupoCombo.focus();
			ret = false;
		} else
			setRequiredError(customerGrupoCombo, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
