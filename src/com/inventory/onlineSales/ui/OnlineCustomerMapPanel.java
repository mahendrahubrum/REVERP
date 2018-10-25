package com.inventory.onlineSales.ui;

import java.util.Arrays;
import java.util.Iterator;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.PaymentTermsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.PaymentTermsModel;
import com.inventory.dao.SalesManMapDao;
import com.inventory.onlineSales.dao.ImportOnlineSalesOrderDao;
import com.inventory.onlineSales.model.OnlineCustomerModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 25, 2014
 */
public class OnlineCustomerMapPanel extends SContainerPanel {

	private static final long serialVersionUID = 839749571430499042L;

	private SComboField customerComboField;
	private SComboField onlineCustomerComboField;
	private STextField customerNameTextField;
	private STextField customerCodeTextField;
	private SNativeSelect currency;
	private SNativeSelect salesType;
	private SNativeSelect payment_terms;
	private STextField openingBalanceTextField;
	private SAddressField addressField;

	private SButton saveButton;
	private SButton updateButton;

	private WrappedSession session;

	SComboField responsibleEmployeeCombo;

	CustomerDao objDao = new CustomerDao();
	ImportOnlineSalesOrderDao soDao;
	SRadioButton custTypeButton;

	OnlineCustomerModel mainModel;

	public OnlineCustomerMapPanel(long onlineCustId) {

		try {
			setSize(400, 250);
			setSizeFull();

			SFormLayout layout = new SFormLayout();
			layout.setMargin(true);

			final SVerticalLayout nameMainLay = new SVerticalLayout();
			final SFormLayout customerLayout = new SFormLayout();
			final SFormLayout customerLayout1 = new SFormLayout();

			final SFormLayout customerLayout2 = new SFormLayout();

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

			soDao = new ImportOnlineSalesOrderDao();

			responsibleEmployeeCombo = new SComboField(
					getPropertyName("responsible_employee"), 150,
					new SalesManMapDao().getUsers(getOfficeID(),
							SConstants.SALES_MAN), "id", "first_name");

			setContent(layout);

			session = getHttpSession();

			custTypeButton = new SRadioButton(getPropertyName("Customer"), 200,
					Arrays.asList(new KeyValue(1, "Existing"), new KeyValue(2,
							"New")), "intKey", "value");
			custTypeButton.setValue(1);
			custTypeButton.setStyleName("radio_horizontal");

			customerComboField = new SComboField(getPropertyName("Customer"),
					150, objDao.getAllActiveCustomers(getOfficeID()), "id",
					"name");
			onlineCustomerComboField = new SComboField(
					getPropertyName("online_customer"), 150,
					soDao.getAllOnlineCustomersWithSO(), "id", "firstName");
			onlineCustomerComboField.setValue(onlineCustId);

			long custId = soDao
					.getCustomerIdOfOnlineCustomer((Long) onlineCustomerComboField
							.getValue());
			customerComboField.setValue(custId);

			mainModel = soDao.getOnlineCustomer(onlineCustId);

			customerNameTextField = new STextField(
					getPropertyName("customer_name"));
			customerCodeTextField = new STextField(getPropertyName("code"));
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
			addressField = new SAddressField(4);
			addressField.setCaption(null);

			saveButton = new SButton(getPropertyName("add_map_customer"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			buttonGridLayout.addComponent(saveButton, 3, 0);

			updateButton = new SButton(getPropertyName("map_customer"));
			updateButton.setStyleName("savebtnStyle");
			updateButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			buttonGridLayout.addComponent(updateButton, 4, 0);

			customerLayout.addComponent(onlineCustomerComboField);
			customerLayout.addComponent(custTypeButton);
			customerLayout.addComponent(customerComboField);
			customerLayout1.addComponent(customerNameTextField);
			customerLayout1.addComponent(customerCodeTextField);
			customerLayout1.addComponent(openingBalanceTextField);

			nameMainLay.addComponent(customerLayout);
			nameMainLay.addComponent(customerLayout1);

			customerLayout2.addComponent(currency);
			customerLayout2.addComponent(payment_terms);
			customerLayout2.addComponent(salesType);

			customerLayout2.addComponent(responsibleEmployeeCombo);

			customerLayout1.setVisible(false);
			customerLayout2.setVisible(false);
			addressField.setVisible(false);
			saveButton.setVisible(false);
			updateButton.setVisible(true);
			customerComboField.setVisible(true);

			gridLayout.addComponent(nameMainLay);
			gridLayout.addComponent(customerLayout2);

			layout.addComponent(gridLayout);
			layout.addComponent(addressField);
			layout.addComponent(buttonGridLayout);

			custTypeButton.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if ((Integer) custTypeButton.getValue() == 1) {
						customerLayout1.setVisible(false);
						customerLayout2.setVisible(false);
						addressField.setVisible(false);
						saveButton.setVisible(false);
						updateButton.setVisible(true);
						customerComboField.setVisible(true);
						setSize(400, 250);

						customerNameTextField.setValue("");
						customerCodeTextField.setValue("");
						openingBalanceTextField.setValue("0");
						responsibleEmployeeCombo.setValue(getLoginID());

						addressField.clearAll();

					} else {
						customerLayout1.setVisible(true);
						customerLayout2.setVisible(true);
						addressField.setVisible(true);
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						customerComboField.setVisible(false);
						setSize(780, 520);

						if (mainModel != null) {
							customerNameTextField.setValue(mainModel
									.getFirstName()
									+ " "
									+ mainModel.getLastName());
							customerCodeTextField.setValue("");
							openingBalanceTextField.setValue("0");
							responsibleEmployeeCombo.setValue(getLoginID());

							addressField.getEmailTextField().setValue(
									mainModel.getEmail());
							addressField.getCountryComboField().setValue(
									mainModel.getCountry_id());
							addressField.getMobileTextField().setValue(
									mainModel.getMobile());
						}
					}
				}
			});

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {

						try {
							if (!objDao.isCodeExists(getOfficeID(),
									customerCodeTextField.getValue(), 0)) {

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
								customer.setCustomer_code(customerCodeTextField
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

								customer.setLedger(objModel);

								try {
									customer = new CustomerDao()
											.saveAndGet(customer);

									session.setAttribute("new_online_cust_id",
											customer.getLedger().getId());

									OnlineCustomerModel custMdl = soDao
											.getOnlineCustomer((Long) onlineCustomerComboField
													.getValue());
									custMdl.setCustomer_id(customer.getId());
									soDao.updateOnlineCustomer(custMdl);

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
										getPropertyName("customer_code_exist"),
										true);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			});

			updateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isUpdateValid()) {
							OnlineCustomerModel custMdl = soDao
									.getOnlineCustomer((Long) onlineCustomerComboField
											.getValue());
							custMdl.setCustomer_id((Long) customerComboField
									.getValue());
							soDao.updateOnlineCustomer(custMdl);

							session.setAttribute(
									"new_online_cust_id",
									objDao.getCustomer(
											(Long) customerComboField
													.getValue()).getLedger()
											.getId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			addressField.getCountryComboField().setValue(
					(Long) session.getAttribute("country_id"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean isUpdateValid() {
		boolean ret = true;

		if (onlineCustomerComboField.getValue() == null
				|| onlineCustomerComboField.getValue().equals("")) {
			setRequiredError(onlineCustomerComboField,
					getPropertyName("select_customer"), true);
			ret = false;
		} else
			setRequiredError(onlineCustomerComboField, null, false);

		if (customerComboField.getValue() == null
				|| customerComboField.getValue().equals("")) {
			setRequiredError(customerComboField,
					getPropertyName("select_customer"), true);
			ret = false;
		} else
			setRequiredError(customerComboField, null, false);

		return ret;
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
					getPropertyName("select_payment_terms"), true);
			payment_terms.focus();
			ret = false;
		} else
			setRequiredError(payment_terms, null, false);

		if (responsibleEmployeeCombo.getValue() == null
				|| responsibleEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsibleEmployeeCombo,
					getPropertyName("select_person"), true);
			responsibleEmployeeCombo.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployeeCombo, null, false);

		if (salesType.getValue() == null || salesType.getValue().equals("")) {
			setRequiredError(salesType, getPropertyName("select_sales_type"),
					true);
			salesType.focus();
			ret = false;
		} else
			setRequiredError(salesType, null, false);

		if (currency.getValue() == null || currency.getValue().equals("")) {
			setRequiredError(currency, getPropertyName("select_currency"), true);
			currency.focus();
			ret = false;
		} else
			setRequiredError(currency, null, false);

		if (customerCodeTextField.getValue() == null
				|| customerCodeTextField.getValue().equals("")) {
			setRequiredError(customerCodeTextField,
					getPropertyName("enter_customer_code"), true);
			customerCodeTextField.focus();
			ret = false;
		} else
			setRequiredError(customerCodeTextField, null, false);

		if (openingBalanceTextField.getValue() == null
				|| openingBalanceTextField.getValue().equals("")) {
			setRequiredError(openingBalanceTextField,
					getPropertyName("enter_opening_balance"), true);
			openingBalanceTextField.focus();
			ret = false;
		} else {

			try {
				toDouble(openingBalanceTextField.getValue().toString());
				setRequiredError(openingBalanceTextField, null, false);
			} catch (Exception e) {
				setRequiredError(openingBalanceTextField,
						getPropertyName("enter_valid_balance"), true);
				openingBalanceTextField.focus();
				ret = false;
			}
		}

		if (customerNameTextField.getValue() == null
				|| customerNameTextField.getValue().equals("")) {
			setRequiredError(customerNameTextField,
					getPropertyName("enter_customer_name"), true);
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
							getPropertyName("already_exists"), true);
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
		openingBalanceTextField.setValue("0.00");
		addressField.clearAll();
		addressField.getCountryComboField().setValue(
				(Long) session.getAttribute("country_id"));
	}
}
