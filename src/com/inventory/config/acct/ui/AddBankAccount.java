package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.model.BankAccountModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.StatusDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class AddBankAccount extends SparkLogic {

	long id;

	SPanel mainPanel;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	SComboField bankNameListCombo;
	STextField bankNameTextField;
	SComboField groupCombo;
	SComboField statusCombo;
	STextField accountNoTextField;
	SNativeSelect currency;
	STextArea bankAddressTextArea;

	BankAccountDao objDao = new BankAccountDao();

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(470, 460);
		objDao = new BankAccountDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {

			accountNoTextField = new STextField(getPropertyName("account_no"),
					250);
			accountNoTextField.setValue("0");
			currency = new SNativeSelect(
					getPropertyName("bank_account_currency"), 250,
					new CurrencyManagementDao().getlabels(), "id", "name");
			currency.setValue(getCurrencyID());

			bankAddressTextArea = new STextArea(
					getPropertyName("bank_address"), 250);

			/*
			 * accountNoTextField currency gstnoTextField groupCombo
			 * discount_percentageTextField
			 * prompt_payment_discount_percentTextField payment_terms
			 * credit_limitTextField bankAddressTextArea
			 */

			hLayout = new SHorizontalLayout();
			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			save = new SButton(getPropertyName("Save"));
			edit = new SButton(getPropertyName("Edit"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));
			cancel = new SButton(getPropertyName("Cancel"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(edit);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(cancel);
			buttonLayout.setSpacing(true);

			edit.setVisible(false);
			delete.setVisible(false);
			update.setVisible(false);
			cancel.setVisible(false);

			List list = objDao.getAllBankAccountNames(getOfficeID());
			BankAccountModel og = new BankAccountModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			bankNameListCombo = new SComboField(null, 250, list, "id", "name");
			bankNameListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 250,
					new StatusDao().getStatuses("GroupModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));
			statusCombo.setValue((long) 1);

			groupCombo = new SComboField(getPropertyName("account_type"), 250,
					new GroupDao().getAllActiveGroups(getOrganizationID()), "id",
					"name", true, getPropertyName("select"));
			Iterator itt = groupCombo.getItemIds().iterator();
			if (itt.hasNext())
				groupCombo.setValue(itt.next());

			bankNameTextField = new STextField(getPropertyName("bank_name"),
					250);


			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("bank_account"));
			salLisrLay.addComponent(bankNameListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(bankNameTextField);
			form.addComponent(accountNoTextField);
			form.addComponent(groupCombo);
			form.addComponent(currency);
			form.addComponent(bankAddressTextArea);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			// form.addComponent(buttonLayout);

			hLayout.addComponent(form);

			hLayout.setMargin(true);
			hLayout.setSpacing(true);

			vLayout.addComponent(hLayout);
			vLayout.addComponent(buttonLayout);

			vLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

			mainPanel.setContent(vLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					bankNameListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (bankNameListCombo.getValue() == null
								|| bankNameListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								LedgerModel objModel = new LedgerModel();
								objModel.setName(bankNameTextField.getValue());
								objModel.setGroup(new GroupModel(
										(Long) groupCombo.getValue()));
								objModel.setCurrent_balance(0);
								objModel.setStatus((Long) statusCombo
										.getValue());

								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));

								BankAccountModel bankAcct = new BankAccountModel();

								bankAcct.setName(bankNameTextField.getValue());
								bankAcct.setAccount_no(accountNoTextField
										.getValue());
								bankAcct.setBank_currency(new CurrencyModel(
										(Long) currency.getValue()));
								bankAcct.setBank_address(bankAddressTextArea
										.getValue());

								bankAcct.setLedger(objModel);

								try {
									id = objDao.save(bankAcct);
									loadOptions(id);
									Notification.show(
											getPropertyName("Success"),
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

								} catch (Exception e) {
									// TODO Auto-generated catch block
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			bankNameListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (bankNameListCombo.getValue() != null
										&& !bankNameListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									edit.setVisible(true);
									delete.setVisible(true);
									update.setVisible(false);
									cancel.setVisible(false);

									BankAccountModel bankAcctModel = objDao
											.getBankAccount((Long) bankNameListCombo
													.getValue());
									LedgerModel objModel = bankAcctModel
											.getLedger();

									setWritableAll();
									bankNameTextField.setValue(objModel
											.getName());
									statusCombo.setValue(objModel.getStatus());
									groupCombo.setValue(objModel.getGroup()
											.getId());

									accountNoTextField.setValue(bankAcctModel
											.getAccount_no());
									currency.setValue(bankAcctModel
											.getBank_currency().getId());
									bankAddressTextArea.setValue(""
											+ bankAcctModel.getBank_address());

									setReadOnlyAll();
									isValid();

								} else {
									save.setVisible(true);
									edit.setVisible(false);
									delete.setVisible(false);
									update.setVisible(false);
									cancel.setVisible(false);

									setWritableAll();
									bankNameTextField.setValue("");
									statusCombo.setValue((long) 1);

									accountNoTextField.setValue("0");
									currency.setValue(getCurrencyID());
									groupCombo.setValue(null);
									bankAddressTextArea.setValue("");

								}

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			edit.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(true);
						cancel.setVisible(true);

						LedgerModel objModel = objDao.getBankAccount(
								(Long) bankNameListCombo.getValue())
								.getLedger();

						setWritableAll();

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
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);
						loadOptions(Long.parseLong(bankNameListCombo.getValue()
								.toString()));

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) bankNameListCombo
														.getValue();
												objDao.delete(id);
												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (bankNameListCombo.getValue() != null) {

							if (isValid()) {


								BankAccountModel bankAcct = objDao
										.getBankAccount((Long) bankNameListCombo
												.getValue());
								LedgerModel objModel = bankAcct.getLedger();

								objModel.setName(bankNameTextField.getValue());
								objModel.setGroup(new GroupModel(
										(Long) groupCombo.getValue()));
								objModel.setStatus((Long) statusCombo
										.getValue());

								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));

								bankAcct.setName(bankNameTextField.getValue());
								bankAcct.setAccount_no(accountNoTextField
										.getValue());
								bankAcct.setBank_currency(new CurrencyModel(
										(Long) currency.getValue()));
								bankAcct.setBank_address(bankAddressTextArea
										.getValue());

								bankAcct.setLedger(objModel);

								try {
									objDao.update(bankAcct);
									Notification.show(
											getPropertyName("Success"),
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);
									loadOptions(bankAcct.getId());
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});

			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (save.isVisible())
						save.click();
					else
						update.click();
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
		}
		return mainPanel;

	}

	public void setReadOnlyAll() {
		bankNameTextField.setReadOnly(true);
		statusCombo.setReadOnly(true);

		accountNoTextField.setReadOnly(true);
		currency.setReadOnly(true);
		groupCombo.setReadOnly(true);
		bankAddressTextArea.setReadOnly(true);

		bankNameTextField.focus();

	}

	public void setWritableAll() {
		bankNameTextField.setReadOnly(false);
		statusCombo.setReadOnly(false);
		accountNoTextField.setReadOnly(false);
		currency.setReadOnly(false);
		groupCombo.setReadOnly(false);
		bankAddressTextArea.setReadOnly(false);

	}

	public void loadOptions(long id) {
		List list;
		try {
			list = objDao.getAllBankAccountNames(getOfficeID());

			BankAccountModel sop = new BankAccountModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			bankNameListCombo.setContainerDataSource(bic);
			bankNameListCombo.setItemCaptionPropertyId("name");

			bankNameListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

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

		if (accountNoTextField.getValue() == null
				|| accountNoTextField.getValue().equals("")) {
			setRequiredError(accountNoTextField,
					getPropertyName("invalid_data"), true);
			accountNoTextField.focus();
			ret = false;
		} else
			setRequiredError(accountNoTextField, null, false);

		if (bankNameTextField.getValue() == null
				|| bankNameTextField.getValue().equals("")) {
			setRequiredError(bankNameTextField,
					getPropertyName("invalid_data"), true);
			bankNameTextField.focus();
			ret = false;
		} else
			setRequiredError(bankNameTextField, null, false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
