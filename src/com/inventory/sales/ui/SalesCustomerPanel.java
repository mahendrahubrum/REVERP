package com.inventory.sales.ui;

import java.util.Iterator;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.CustomerGroupDao;
import com.inventory.config.acct.dao.PaymentTermsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.PaymentTermsModel;
import com.inventory.dao.SalesManMapDao;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.STextField;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 18, 2013
 */
public class SalesCustomerPanel extends SContainerPanel {

	private static final long serialVersionUID = -3615395903850440174L;
	private STextField customerNameTextField;
	private STextField customerCodeTextField;
	private SNativeSelect currency;
	private SNativeSelect salesType;
	private SNativeSelect payment_terms;
	private STextField openingBalanceTextField;
	private SAddressField addressField;
	SComboField groupCombo;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;

	private WrappedSession session;

	SComboField responsibleEmployeeCombo;

	CustomerDao objDao = new CustomerDao();

	public SalesCustomerPanel() {

		try {
			setSizeFull();

			SFormLayout layout = new SFormLayout();
			layout.setMargin(true);

			SFormLayout customerLayout1 = new SFormLayout();

			SFormLayout customerLayout2 = new SFormLayout();

			SGridLayout buttonGridLayout = new SGridLayout();
			buttonGridLayout.setColumns(8);
			buttonGridLayout.setRows(1);
			buttonGridLayout.setSpacing(true);
			buttonGridLayout.setSizeFull();

			SGridLayout gridLayout = new SGridLayout();
			gridLayout.setColumns(2);
			gridLayout.setRows(1);
			gridLayout.setSpacing(true);
			gridLayout.setStyleName("master_border");

			SHorizontalLayout buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			responsibleEmployeeCombo = new SComboField(
					getPropertyName("sales_man"), 150,
					new SalesManMapDao().getUsers(getOfficeID(),
							SConstants.SALES_MAN), "id", "first_name");

			setContent(layout);

			session = getHttpSession();

			customerNameTextField = new STextField(
					getPropertyName("customer_name"));
			customerCodeTextField = new STextField(getPropertyName("code"));
			groupCombo = new SComboField(
					getPropertyName("group"), 150,
					new CustomerGroupDao().getAllCustomerGroups(getOfficeID()), "id", "name");
			groupCombo.setInputPrompt(getPropertyName("select"));
			currency = new SNativeSelect(getPropertyName("customer_currency"),
					150, new CurrencyManagementDao().getlabels(), "id", "name");
			currency.setNullSelectionAllowed(false);
			currency.setValue(toLong(session.getAttribute("currency_id")
					.toString()));
			salesType = new SNativeSelect(getPropertyName("sales_type"), 150,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");
			salesType.setValue(salesType.getItemIds().iterator().next());
			salesType.setNullSelectionAllowed(false);

			payment_terms = new SNativeSelect(getPropertyName("payment_terms"),
					150,
					new PaymentTermsDao()
							.getAllActivePaymentTerms(getOrganizationID()),
					"id", "name");
			payment_terms.setNullSelectionAllowed(false);

			Iterator itr = payment_terms.getItemIds().iterator();
			if (itr.hasNext())
				payment_terms.setValue(itr.next());

			openingBalanceTextField = new STextField(
					getPropertyName("opening_balance"));
			openingBalanceTextField.setValue("0.00");
			addressField = new SAddressField(1);
			addressField.setCaption(null);

			saveButton = new SButton(getPropertyName("add_customer"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			buttonGridLayout.addComponent(saveButton, 3, 0);

			customerLayout1.addComponent(customerNameTextField);
			customerLayout1.addComponent(responsibleEmployeeCombo);
			customerLayout1.addComponent(groupCombo);
			// customerLayout1.addComponent(customerCodeTextField);
			customerLayout1.addComponent(openingBalanceTextField);

			customerLayout2.addComponent(currency);
			customerLayout2.addComponent(payment_terms);
			customerLayout2.addComponent(salesType);

			// customerLayout2.addComponent(responsibleEmployeeCombo);

			gridLayout.addComponent(customerLayout1);
			gridLayout.addComponent(customerLayout2);

			layout.addComponent(gridLayout);
			layout.addComponent(addressField);
			layout.addComponent(buttonGridLayout);

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {

						try {
							if (true) {

								LedgerModel objModel = new LedgerModel();
								objModel.setName(customerNameTextField
										.getValue());
								objModel.setGroup(new GroupModel(
										getSettings().getCUSTOMER_GROUP()));
								
								objModel.setCurrent_balance(Double
										.parseDouble(openingBalanceTextField
												.getValue()));
								objModel.setStatus(SConstants.statuses.LEDGER_ACTIVE);

								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));

								CustomerModel customer = new CustomerModel();
								customer.setAddress(addressField.getAddress());
								customer.setName(customerNameTextField
										.getValue());
								customer.setCredit_limit(0);
								customer.setCustomer_code(customerNameTextField
										.getValue());
								customer.setCustomer_currency(new CurrencyModel(
										(Long) currency.getValue()));
								customer.setDescription("");
								customer.setPayment_terms(new PaymentTermsModel(
										(Long) payment_terms.getValue()));
								customer.setSales_type((Long) salesType
										.getValue());
								customer.setResponsible_person((Long) responsibleEmployeeCombo
										.getValue());
								customer.setCustomerGroupId((Long) groupCombo
										.getValue());

								customer.setLedger(objModel);

								try {
									customer = new CustomerDao()
											.saveAndGet(customer);

									session.setAttribute("new_id", customer
											.getLedger().getId());

									Notification.show(
											getPropertyName("save_success"),
											Type.TRAY_NOTIFICATION);

									Iterator itt = getUI().getWindows()
											.iterator();
									itt.next();
									getUI().removeWindow((Window) itt.next());

								} catch (Exception e) {
									Notification.show(getPropertyName("error"),
											Type.ERROR_MESSAGE);
									e.printStackTrace();
								}

								setRequiredError(customerCodeTextField, null,
										false);

							} else {

								setRequiredError(customerCodeTextField,
										getPropertyName("invalid_data"), true);
							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			});

			addressField.getCountryComboField().setValue(
					(Long) session.getAttribute("country_id"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isValid() {
		boolean ret = true;

		if (!addressField.isValid()) {
			addressField.getCountryComboField().setValue(
					Long.parseLong(session.getAttribute("country_id")
							.toString()));
			if (!addressField.isValid()) {
				ret = false;
			}
		}

		if (payment_terms.getValue() == null
				|| payment_terms.getValue().equals("")) {
			setRequiredError(payment_terms,
					getPropertyName("invalid_selection"), true);
			payment_terms.focus();
			ret = false;
		} else
			setRequiredError(payment_terms, null, false);

		if (responsibleEmployeeCombo.getValue() == null
				|| responsibleEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsibleEmployeeCombo,
					getPropertyName("invalid_selection"), true);
			responsibleEmployeeCombo.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployeeCombo, null, false);

		if (salesType.getValue() == null || salesType.getValue().equals("")) {
			setRequiredError(salesType, getPropertyName("invalid_selection"),
					true);
			salesType.focus();
			ret = false;
		} else
			setRequiredError(salesType, null, false);

		if (currency.getValue() == null || currency.getValue().equals("")) {
			setRequiredError(currency, getPropertyName("invalid_selection"),
					true);
			currency.focus();
			ret = false;
		} else
			setRequiredError(currency, null, false);
		
		if (groupCombo.getValue() == null || groupCombo.getValue().equals("")) {
			setRequiredError(groupCombo, getPropertyName("invalid_selection"),
					true);
			groupCombo.focus();
			ret = false;
		} else
			setRequiredError(groupCombo, null, false);

		// if (customerCodeTextField.getValue() == null
		// || customerCodeTextField.getValue().equals("")) {
		// setRequiredError(customerCodeTextField, "Enter customer code", true);
		// customerCodeTextField.focus();
		// ret = false;
		// } else
		// setRequiredError(customerCodeTextField, null, false);

		if (openingBalanceTextField.getValue() == null
				|| openingBalanceTextField.getValue().equals("")) {
			setRequiredError(openingBalanceTextField,
					getPropertyName("invalid_data"), true);
			openingBalanceTextField.focus();
			ret = false;
		} else {

			try {
				toDouble(openingBalanceTextField.getValue().toString());
				setRequiredError(openingBalanceTextField, null, false);
			} catch (Exception e) {
				setRequiredError(openingBalanceTextField,
						getPropertyName("invalid_data"), true);
				openingBalanceTextField.focus();
				ret = false;
			}
		}

		if (customerNameTextField.getValue() == null
				|| customerNameTextField.getValue().equals("")) {
			setRequiredError(customerNameTextField,
					getPropertyName("invalid_data"), true);
			customerNameTextField.focus();
			ret = false;
		} else
			setRequiredError(customerNameTextField, null, false);

		if (ret) {
			try {
				if (new CustomerDao().isAlreadyExists(getOfficeID(),
						customerNameTextField.getValue(),
						customerCodeTextField.getValue())) {
					setRequiredError(customerNameTextField,
							getPropertyName("invalid_selection"), true);
					customerNameTextField.focus();
					ret = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return ret;
	}

	public void clearFields() {
		customerNameTextField.setValue("");
		customerCodeTextField.setValue("");
		groupCombo.setValue(null);
		openingBalanceTextField.setValue("0.00");
		addressField.clearAll();
		addressField.getCountryComboField().setValue(
				(Long) session.getAttribute("country_id"));
	}
}
