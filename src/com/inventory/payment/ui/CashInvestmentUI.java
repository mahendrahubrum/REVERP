package com.inventory.payment.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.CashInvestmentDao;
import com.inventory.payment.model.CashInvestmentModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 22, 2013
 */

public class CashInvestmentUI extends SparkLogic {

	private static final long serialVersionUID = 8774283694107704131L;

	private SComboField paymentIdComboField;
	private SComboField investmentComboField;
	private SComboField cashActComboField;
	private SDateField dateField;
	private STextField amountField;
	private STextArea descriptionField;
	private SComboField currencyComboField;

	SRadioButton depositOrWithdrawal;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private SButton cancelButton;

	// SListSelect workorderSelect;

	private SCollectionContainer container;

	private CashInvestmentDao objDao;

	private SettingsValuePojo settings;

	private WrappedSession session;

	private long paymentId = 0;

	LedgerDao ledgerDao;

	SButton createNewButton;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings("unchecked")
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		
		setSize(450, 420);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		ledgerDao = new LedgerDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Transportation");

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		objDao = new CashInvestmentDao();

		// workorderSelect=new SListSelect("Work Order Number", 200, null, "id",
		// "comments");
		// workorderSelect.setImmediate(true);
		// workorderSelect.setMultiSelect(true);

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SFormLayout contractorFormLayout = new SFormLayout();

		// SFormLayout amountFormLayout = new SFormLayout();
		// amountFormLayout.setMargin(true);

		SGridLayout gridLayout = new SGridLayout();
		gridLayout.setColumns(2);
		gridLayout.setRows(1);
		gridLayout.setSpacing(true);
		gridLayout.setStyleName("master_border");

		SGridLayout buttonGridLayout = new SGridLayout();
		buttonGridLayout.setColumns(8);
		buttonGridLayout.setRows(1);
		buttonGridLayout.setSpacing(true);
		buttonGridLayout.setSizeFull();

		SHorizontalLayout buttonFormLayout = new SHorizontalLayout();
		buttonFormLayout.setSpacing(true);

		depositOrWithdrawal = new SRadioButton("", 200,
				SConstants.depositOrWithdrowal, "intKey", "value");
		depositOrWithdrawal.setValue(1);
		depositOrWithdrawal.setHorizontal(true);

		paymentIdComboField = new SComboField(null, 200);
		paymentIdComboField
				.setInputPrompt(getPropertyName("create_new"));
		loadPaymentNo(0);

		try {

			List actList = null;
			if ((Boolean) getHttpSession().getAttribute(
					"expendeture_acct_cash_only") == true) {
				actList = ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						settings.getCASH_GROUP());
			} else {
				actList = new ArrayList();
				actList.addAll(ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						settings.getCASH_GROUP()));
			}

			investmentComboField = new SComboField(
					getPropertyName("capital_account"), 200,
					ledgerDao.getAllLedgersUnderGroupAndSubGroups(
							getOfficeID(), getOrganizationID(),
							settings.getCUSTOMER_GROUP()), "id", "name");
			investmentComboField
					.setInputPrompt(getPropertyName("select"));
			cashActComboField = new SComboField(
					getPropertyName("invest_account"), 200, actList, "id",
					"name");
			cashActComboField
					.setInputPrompt(getPropertyName("select"));

		} catch (Exception e) {
			// TODO: handle exception
		}

		dateField = new SDateField(getPropertyName("date"), 100);
		dateField.setValue(getWorkingDate());
		amountField = new STextField(getPropertyName("amount"), 200);
		amountField.setStyleName("textfield_align_right");
		amountField.setValue("0.00");
		descriptionField = new STextArea(getPropertyName("description"), 200,
				30);
		currencyComboField = new SComboField(getPropertyName("currency"), 200);
		loadCurrecny();

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("investment_no"));
		salLisrLay.addComponent(paymentIdComboField);
		salLisrLay.addComponent(createNewButton);

		contractorFormLayout.addComponent(salLisrLay);
		contractorFormLayout.addComponent(dateField);
		contractorFormLayout.addComponent(investmentComboField);
		contractorFormLayout.addComponent(cashActComboField);
		contractorFormLayout.addComponent(amountField);
		contractorFormLayout.addComponent(currencyComboField);
		contractorFormLayout.addComponent(depositOrWithdrawal);
		contractorFormLayout.addComponent(descriptionField);

		gridLayout.addComponent(contractorFormLayout);
		// gridLayout.addComponent(amountFormLayout);

		saveButton = new SButton(getPropertyName("save"));
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		updateButton = new SButton(getPropertyName("update"));
		updateButton.setVisible(false);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		deleteButton = new SButton(getPropertyName("delete"));
		deleteButton.setVisible(false);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		cancelButton = new SButton(getPropertyName("cancel"));
		cancelButton.setVisible(false);
		cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		cancelButton.setStyleName("deletebtnStyle");

		buttonFormLayout.addComponent(saveButton);
		buttonFormLayout.addComponent(updateButton);
		if (settings.isKEEP_DELETED_DATA())
			buttonFormLayout.addComponent(cancelButton);
		else
			buttonFormLayout.addComponent(deleteButton);
		buttonGridLayout.addComponent(buttonFormLayout, 4, 0);

		mainFormLayout.addComponent(gridLayout);
		mainFormLayout.addComponent(buttonGridLayout);
		
		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getButton().getId().equals("1")) {
					try {
						saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)paymentIdComboField.getValue(),confirmBox.getUserID());
						Notification.show("Success",
								"Session Saved Successfully..!",
								Type.WARNING_MESSAGE);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				confirmBox.close();
			}
		};
		
		confirmBox.setClickListener(confirmListener);
		
		ClickListener clickListnr=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
					if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")) {
						saveSessionActivity(getOptionId(), (Long)paymentIdComboField.getValue(),
								"Cash Investment : No. "+paymentIdComboField.getItemCaption(paymentIdComboField.getValue()));
						Notification.show("Success",
								"Session Saved Successfully..!",
								Type.WARNING_MESSAGE);
					}
					else
						Notification.show("Select an Invoice..!",
								"Select an Invoice for save in session",
								Type.HUMANIZED_MESSAGE);
				}
				else if(event.getButton().getId().equals(windowNotif.REPORT_ISSUE)) {
					if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")) {
						confirmBox.open();
					}
					else
						Notification.show("Select an Invoice..!", "Select an Invoice for Save in session",
								Type.HUMANIZED_MESSAGE);
				}
				else {
					try {
						helpPopup=new SHelpPopupView(getOptionId());
						popupLay.removeAllComponents();
						popupLay.addComponent(helpPopup);
						helpPopup.setPopupVisible(true);
						helpPopup.setHideOnMouseOut(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
		windowNotif.setClickListener(clickListnr);
		

		investmentComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							if (investmentComboField.getValue() != null) {
								investmentComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
										+ ledgerDao
												.getLedgerCurrentBalance((Long) investmentComboField
														.getValue()) + "</i>");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		cashActComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							if (cashActComboField.getValue() != null) {
								cashActComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
										+ ledgerDao
												.getLedgerCurrentBalance((Long) cashActComboField
														.getValue()) + "</i>");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		createNewButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				paymentIdComboField.setValue((long) 0);
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (isValid()) {
						int typ = (Integer) depositOrWithdrawal.getValue();

						CashInvestmentModel paymentModel = new CashInvestmentModel();
						paymentModel.setInvestment_no(getNextSequence(
								"Investment_No", getLoginID()));
						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel
								.setCapital_account_id(toLong(investmentComboField
										.getValue().toString()));
						paymentModel
								.setCash_account_id(toLong(cashActComboField
										.getValue().toString()));
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel.setType(typ);
						paymentModel.setAmount(roundNumber(toDouble(amountField
								.getValue())));
						paymentModel.setActive(true);

						FinTransaction transaction = new FinTransaction();
						if (paymentModel.getType() == 1) {
							transaction.addTransaction(SConstants.DR,
									toLong(investmentComboField.getValue()
											.toString()),
									toLong(cashActComboField.getValue()
											.toString()),
									roundNumber(toDouble(amountField.getValue()
											.toString())));
						} else {
							transaction.addTransaction(SConstants.DR,
									toLong(cashActComboField.getValue()
											.toString()),
									toLong(investmentComboField.getValue()
											.toString()),
									roundNumber(toDouble(amountField.getValue()
											.toString())));
						}

						paymentId = objDao.save(paymentModel, transaction
								.getTransaction(SConstants.INVESTMENT,
										paymentModel.getDate()));

						saveActivity(
								getOptionId(),
								"Cash Investment Saved. Investment No : "
										+ paymentModel.getInvestment_no()
										+ ", Investment Acct : "
										+ investmentComboField
												.getItemCaption(investmentComboField
														.getValue())
										+ ", Payment Amount : "
										+ paymentModel.getAmount(),paymentModel.getId());

						loadPaymentNo(paymentId);

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});

		updateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				try {
					if (isValid()) {
						int typ = (Integer) depositOrWithdrawal.getValue();
						long transp_id = toLong(investmentComboField.getValue()
								.toString());

						long cash = getSettings().getCASH_ACCOUNT();
//						long liability = getSettings().getCASH_ACCOUNT();

						CashInvestmentModel paymentModel = objDao
								.getCashInvestmentModel(toLong(paymentIdComboField
										.getValue().toString()));

						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel
								.setCapital_account_id(toLong(investmentComboField
										.getValue().toString()));
						paymentModel
								.setCash_account_id(toLong(cashActComboField
										.getValue().toString()));
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel.setType(typ);
						paymentModel.setAmount(roundNumber(toDouble(amountField
								.getValue())));
						paymentModel.setActive(true);

						FinTransaction transaction = new FinTransaction();
						if (paymentModel.getType() == 1) {
							transaction.addTransaction(SConstants.DR,
									toLong(investmentComboField.getValue()
											.toString()),
									toLong(cashActComboField.getValue()
											.toString()),
									roundNumber(toDouble(amountField.getValue()
											.toString())));
						} else {
							transaction.addTransaction(SConstants.DR,
									toLong(cashActComboField.getValue()
											.toString()),
									toLong(investmentComboField.getValue()
											.toString()),
									roundNumber(toDouble(amountField.getValue()
											.toString())));
						}

						TransactionModel tran = objDao
								.getTransaction(paymentModel
										.getTransaction_id());
						tran.setTransaction_details_list(transaction
								.getChildList());
						tran.setDate(paymentModel.getDate());
						tran.setLogin_id(getLoginID());

						objDao.update(paymentModel, tran);

						saveActivity(
								getOptionId(),
								"Cash Investment Updated. Investment No : "
										+ paymentModel.getInvestment_no()
										+ ", Investment Acct : "
										+ investmentComboField
												.getItemCaption(investmentComboField
														.getValue())
										+ ", Payment Amount : "
										+ paymentModel.getAmount(),paymentModel.getId());

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("update_success"),
								Type.WARNING_MESSAGE);

					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		deleteButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), "Are you sure?",
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										objDao.delete(toLong(paymentIdComboField
												.getValue().toString()));

										saveActivity(
												getOptionId(),
												"Cash Investment Deleted. Investment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Investment Acct : "
														+ investmentComboField
																.getItemCaption(investmentComboField
																		.getValue())
														+ ", Payment Amount : "
														+ amountField
																.getValue(),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);

										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);
										cancelButton.setVisible(false);

									} catch (Exception e) {
										SNotification.show(
												getPropertyName("error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		cancelButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), "Are you sure?",
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										objDao.cancel(toLong(paymentIdComboField
												.getValue().toString()));

										saveActivity(
												getOptionId(),
												"Cash Investment Deleted. Investment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Investment Acct : "
														+ investmentComboField
																.getItemCaption(investmentComboField
																		.getValue())
														+ ", Payment Amount : "
														+ amountField
																.getValue(),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);

										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);
										cancelButton.setVisible(false);

									} catch (Exception e) {
										SNotification.show(
												getPropertyName("error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		paymentIdComboField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				try {

					if (paymentIdComboField.getValue() != null
							&& !paymentIdComboField.getValue().toString()
									.equals("0")) {
						CashInvestmentModel model = objDao
								.getCashInvestmentModel(toLong(paymentIdComboField
										.getValue().toString()));

						if (model != null) {

							cashActComboField.setValue(model
									.getCash_account_id());
							investmentComboField.setValue(model
									.getCapital_account_id());
							descriptionField.setValue(model.getDescription());
							dateField.setValue(model.getDate());
							amountField.setValue(String.valueOf(model
									.getAmount()));
							currencyComboField.setValue(model.getCurrency()
									.getId());
							depositOrWithdrawal.setValue(model.getType());

							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);
						}
					} else {
						cashActComboField.setValue(null);
						investmentComboField.setValue(null);
						descriptionField.setValue("");
						dateField.setValue(getWorkingDate());
						amountField.setValue("0");
						currencyComboField.setValue(getCurrencyID());

						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						depositOrWithdrawal.setValue(1);
					}

					if (!isFinYearBackEntry()) {
						saveButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						if (paymentIdComboField.getValue() == null
								|| paymentIdComboField.getValue().toString()
										.equals("0")) {
							Notification.show(
									getPropertyName("warning_transaction"),
									Type.WARNING_MESSAGE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if (!isFinYearBackEntry()) {
			saveButton.setVisible(false);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			cancelButton.setVisible(false);
			Notification.show(getPropertyName("warning_transaction"),
					Type.WARNING_MESSAGE);
		}

		SVerticalLayout hLayout=new SVerticalLayout();
		hLayout.addComponent(popupLay);
		hLayout.addComponent(mainFormLayout);

		windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
		panel.setContent(windowNotif);
		return panel;
	}

	private void loadPaymentNo(long paymentId) {
		List<Object> list = new ArrayList<Object>();
		try {
			CashInvestmentModel paymentModel = new CashInvestmentModel();
			paymentModel.setId(0);
			paymentModel
					.setDescription("---------------Create New---------------");
			list.add(0, paymentModel);
			list.addAll(objDao.getPaymnetNo(getOfficeID()));
			container = SCollectionContainer.setList(list, "id");
			paymentIdComboField.setContainerDataSource(container);
			paymentIdComboField.setItemCaptionPropertyId("description");
			if (paymentId > 0) {
				paymentIdComboField.setValue(paymentId);
			} else {
				paymentIdComboField.setValue(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadCurrecny() {
		List<Object> list = null;
		try {
			list = new CurrencyManagementDao().getlabels();
			container = SCollectionContainer.setList(list, "id");
			currencyComboField.setContainerDataSource(container);
			currencyComboField.setItemCaptionPropertyId("name");
			currencyComboField.setValue(getCurrencyID());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		clearErrorMessages();

		boolean valid = true;
		if (investmentComboField.getValue() == null
				|| investmentComboField.getValue().equals("")) {
			setRequiredError(investmentComboField,
					getPropertyName("select_account"), true);
			valid = false;
		} else
			investmentComboField.setComponentError(null);

		if (cashActComboField.getValue() == null
				|| cashActComboField.getValue().equals("")) {
			setRequiredError(cashActComboField,
					getPropertyName("select_account"), true);
			valid = false;
		} else
			cashActComboField.setComponentError(null);

		if (!isValidAmount(amountField)) {
			valid = false;
		}
		return valid;
	}

	private void clearErrorMessages() {
		investmentComboField.setComponentError(null);
		amountField.setComponentError(null);
		cashActComboField.setComponentError(null);
	}

	private boolean isValidAmount(STextField field) {
		boolean valid = true;
		if (field.getValue() == null || field.getValue().equals("")) {
			setRequiredError(field, getPropertyName("enter_valid_amount"), true);
			valid = false;
		} else {
			try {
				Double.parseDouble(field.getValue().toString());
				valid = true;
			} catch (Exception e) {
				setRequiredError(field, getPropertyName("enter_valid_amount"),
						true);
				e.printStackTrace();
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return paymentIdComboField;
	}

}
