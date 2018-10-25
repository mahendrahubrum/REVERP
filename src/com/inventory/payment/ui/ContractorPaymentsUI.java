package com.inventory.payment.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.ContractorDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.model.PaymentModel;
import com.inventory.sales.dao.WorkOrderDao;
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
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
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
 *         Nov 6, 2013
 */

public class ContractorPaymentsUI extends SparkLogic {

	private static final long serialVersionUID = 8774283694107704131L;

	private SComboField paymentIdComboField;
	private SComboField contractorComboField;
	private SComboField toAccountComboField;
	private SDateField dateField;
	private STextField contractorAmountField;
	private STextField discountField;
	private STextField paymentAmountField;
	private STextArea descriptionField;
	private SComboField currencyComboField;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private SButton cancelButton;

	SListSelect workorderSelect;

	private SCollectionContainer container;

	private PaymentDao paymentDao;

	private SettingsValuePojo settings;

	private WrappedSession session;

	private long paymentId = 0;

	SButton createNewButton;

	LedgerDao ledgerDao;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		
		setSize(800, 400);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		ledgerDao = new LedgerDao();

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		paymentDao = new PaymentDao();

		workorderSelect = new SListSelect(getPropertyName("work_order_number"),
				200, null, "id", "comments");
		workorderSelect.setImmediate(true);
		workorderSelect.setMultiSelect(true);

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Create new Payment");

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SFormLayout contractorFormLayout = new SFormLayout();

		SFormLayout amountFormLayout = new SFormLayout();
		amountFormLayout.setMargin(true);

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

		paymentIdComboField = new SComboField(null, 200);
		paymentIdComboField
				.setInputPrompt(getPropertyName("create_new"));
		loadPaymentNo(0);

		contractorComboField = new SComboField(getPropertyName("contractor"),
				200);
		contractorComboField
				.setInputPrompt(getPropertyName("select"));
		loadContractorCombo();

		toAccountComboField = new SComboField(getPropertyName("to_account"),
				200);
		toAccountComboField
				.setInputPrompt(getPropertyName("select"));
		loadAccountCombo();

		dateField = new SDateField(getPropertyName("date"), 100);
		dateField.setValue(getWorkingDate());
		contractorAmountField = new STextField(
				getPropertyName("contractor_amount"), 200);
		contractorAmountField.setStyleName("textfield_align_right");
		contractorAmountField.setValue("0.00");
		discountField = new STextField(getPropertyName("discount"), 200);
		discountField.setStyleName("textfield_align_right");
		discountField.setValue("0.00");
		paymentAmountField = new STextField(getPropertyName("payment_amount"),
				200);
		paymentAmountField.setStyleName("textfield_align_right");
		paymentAmountField.setValue("0.00");
		descriptionField = new STextArea(getPropertyName("description"), 200,
				30);
		currencyComboField = new SComboField(getPropertyName("currency"), 200);
		loadCurrecny();

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("payment_no"));
		salLisrLay.addComponent(paymentIdComboField);
		salLisrLay.addComponent(createNewButton);
		contractorFormLayout.addComponent(salLisrLay);
		contractorFormLayout.addComponent(contractorComboField);
		contractorFormLayout.addComponent(toAccountComboField);
		contractorFormLayout.addComponent(descriptionField);
		contractorFormLayout.addComponent(workorderSelect);

		workorderSelect.setHeight("80");

		amountFormLayout.addComponent(dateField);
		amountFormLayout.addComponent(contractorAmountField);
		amountFormLayout.addComponent(discountField);
		amountFormLayout.addComponent(paymentAmountField);
		amountFormLayout.addComponent(currencyComboField);

		gridLayout.addComponent(contractorFormLayout);
		gridLayout.addComponent(amountFormLayout);

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
								"Contractor Payment : No. "+paymentIdComboField.getItemCaption(paymentIdComboField.getValue()));
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
		
		
		toAccountComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							if (toAccountComboField.getValue() != null) {
								toAccountComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
										+ ledgerDao
												.getLedgerCurrentBalance((Long) toAccountComboField
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

		contractorComboField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				List lst = new ArrayList();

				if (contractorComboField.getValue() != null
						&& !contractorComboField.getValue().equals("0")) {
					try {
						lst = new WorkOrderDao()
								.getAllWorkOrderNumbersOfContractorAsComment(
										(Long) contractorComboField.getValue(),
										getFinStartDate(), getFinEndDate());

						contractorComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
								+ ledgerDao
										.getLedgerCurrentBalance((Long) contractorComboField
												.getValue()) + "</i>");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				CollectionContainer bic = CollectionContainer.fromBeans(lst,
						"id");
				workorderSelect.setContainerDataSource(bic);
				workorderSelect.setItemCaptionPropertyId("comments");

			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (isValid()) {
						long toAccId = toLong(toAccountComboField.getValue()
								.toString());
						long fromAccId = toLong(contractorComboField.getValue()
								.toString());

						PaymentModel paymentModel = new PaymentModel();
						paymentModel.setPayment_id(getNextSequence(
								"Contractor_Payment_Id", getLoginID()));
						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setCheque_date(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel.setDiscount(toDouble(discountField
								.getValue()));
						paymentModel.setFrom_account_id(fromAccId);
						paymentModel.setTo_account_id(toAccId);
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel
								.setPayment_amount(toDouble(paymentAmountField
										.getValue().toString()));
						paymentModel
								.setSupplier_amount(toDouble(contractorAmountField
										.getValue().toString()));
						paymentModel.setType(SConstants.CONTRACTOR_PAYMENTS);
						paymentModel.setActive(true);
						paymentModel.setFromDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setToDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));

						Set<Long> options_selected = new HashSet<Long>();
						if (options_selected != null) {
							String salesIDs = "";
							options_selected = (Set<Long>) workorderSelect
									.getValue();
							Iterator it1 = options_selected.iterator();
							while (it1.hasNext()) {
								salesIDs += it1.next() + ",";

							}
							paymentModel.setSales_ids(salesIDs);
						}

						FinTransaction transaction = new FinTransaction();
						transaction.addTransaction(SConstants.DR, fromAccId,
								toAccId,
								roundNumber(toDouble(paymentAmountField
										.getValue().toString())));

						paymentId = paymentDao.save(
								paymentModel,
								transaction
										.getTransaction(
												SConstants.CONTRACTOR_PAYMENTS,
												CommonUtil
														.getSQLDateFromUtilDate(dateField
																.getValue())),
								options_selected);

						saveActivity(
								getOptionId(),
								"Contractor Payment Saved. Payment No : "
										+ paymentModel.getPayment_id()
										+ ", Contractor Acct : "
										+ contractorComboField
												.getItemCaption(contractorComboField
														.getValue())
										+ ", Payment Amount : "
										+ paymentModel.getSupplier_amount(),paymentModel.getId());

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
						long toAccId = toLong(toAccountComboField.getValue()
								.toString());
						long fromAccId = toLong(contractorComboField.getValue()
								.toString());

						PaymentModel paymentModel = paymentDao
								.getPaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setCheque_date(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel.setDiscount(toDouble(discountField
								.getValue()));
						paymentModel.setFrom_account_id(fromAccId);
						paymentModel.setTo_account_id(toAccId);
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel
								.setPayment_amount(toDouble(paymentAmountField
										.getValue().toString()));
						paymentModel
								.setSupplier_amount(toDouble(contractorAmountField
										.getValue().toString()));
						paymentModel.setType(SConstants.CONTRACTOR_PAYMENTS);
						paymentModel.setActive(true);
						paymentModel.setFromDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setToDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));

						Set<Long> options_selected = new HashSet<Long>();
						if (options_selected != null) {
							String salesIDs = "";
							options_selected = (Set<Long>) workorderSelect
									.getValue();
							Iterator it1 = options_selected.iterator();
							while (it1.hasNext()) {
								salesIDs += it1.next() + ",";
							}
							paymentModel.setSales_ids(salesIDs);
						}

						FinTransaction trans = new FinTransaction();
						trans.addTransaction(SConstants.DR, fromAccId, toAccId,
								roundNumber(toDouble(paymentAmountField
										.getValue().toString())));
						TransactionModel tran = paymentDao
								.getTransaction(paymentModel
										.getTransaction_id());
						tran.setTransaction_details_list(trans.getChildList());
						tran.setDate(paymentModel.getDate());
						tran.setLogin_id(getLoginID());

						paymentDao.update(paymentModel, tran);

						saveActivity(
								getOptionId(),
								"Contractor Payment Updated. Payment No : "
										+ paymentModel.getPayment_id()
										+ ", Contractor Acct : "
										+ contractorComboField
												.getItemCaption(contractorComboField
														.getValue())
										+ ", Payment Amount : "
										+ paymentModel.getSupplier_amount(),paymentModel.getId());

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
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.delete(toLong(paymentIdComboField
														.getValue().toString()));
										loadPaymentNo(0);
										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										saveActivity(
												getOptionId(),
												"Contractor Payment Deleted. Payment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Contractor Acct : "
														+ contractorComboField
																.getItemCaption(contractorComboField
																		.getValue())
														+ ", Payment Amount : "
														+ contractorAmountField
																.getValue(),(Long)paymentIdComboField
																.getValue());

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
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.cancel(toLong(paymentIdComboField
														.getValue().toString()));
										loadPaymentNo(0);
										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										saveActivity(
												getOptionId(),
												"Contractor Payment Deleted. Payment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Contractor Acct : "
														+ contractorComboField
																.getItemCaption(contractorComboField
																		.getValue())
														+ ", Payment Amount : "
														+ contractorAmountField
																.getValue(),(Long)paymentIdComboField
																.getValue());

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
						PaymentModel model = paymentDao
								.getPaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						if (model != null) {

							contractorComboField.setValue(model
									.getFrom_account_id());
							toAccountComboField.setValue(model
									.getTo_account_id());
							descriptionField.setValue(model.getDescription());
							dateField.setValue(model.getDate());
							contractorAmountField.setValue(String.valueOf(model
									.getSupplier_amount()));
							discountField.setValue(String.valueOf(model
									.getDiscount()));
							paymentAmountField.setValue(String.valueOf(model
									.getPayment_amount()));
							currencyComboField.setValue(model.getCurrency()
									.getId());

							if (model.getSales_ids() != null
									&& !model.getSales_ids().equals("")) {
								Set<Long> val = new HashSet<Long>();
								String[] a = model.getSales_ids().split(",");
								if (a.length > 0) {
									for (int i = 0; i < a.length; i++) {
										val.add(toLong(a[i]));
									}
								}
								workorderSelect.setValue(val);
							}

							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);
						}
					} else {
						contractorComboField.setValue(null);
						toAccountComboField.setValue(null);
						descriptionField.setValue("");
						dateField.setValue(getWorkingDate());
						contractorAmountField.setValue("0.00");
						discountField.setValue("0.00");
						paymentAmountField.setValue("0.00");
						currencyComboField.setValue(getCurrencyID());

						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
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

		contractorAmountField.setImmediate(true);

		contractorAmountField.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					calculateNetPrice();
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
				}

			}
		});

		discountField.setImmediate(true);

		discountField.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					calculateNetPrice();
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
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

	public void calculateNetPrice() {
		double custAmt = 0, payAmt = 0, disc = 0;

		try {
			custAmt = Double.parseDouble(contractorAmountField.getValue());
			disc = Double.parseDouble(discountField.getValue());
		} catch (Exception e) {
			// TODO: handle exception
		}
		paymentAmountField.setNewValue(asString(custAmt - disc));
	}

	public void loadPaymentNo(long paymentId) {
		List<Object> list = new ArrayList<Object>();
		try {
			PaymentModel paymentModel = new PaymentModel();
			paymentModel.setId(0);
			paymentModel
					.setDescription("---------------Create New---------------");
			list.add(0, paymentModel);
			list.addAll(paymentDao.getPaymnetNo(getOfficeID(),
					SConstants.CONTRACTOR_PAYMENTS));
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

	private void loadAccountCombo() {
		List<Object> list = null;
		try {
			list = new LedgerDao().getAllActiveLedgerNames(getOfficeID());
			container = SCollectionContainer.setList(list, "id");
			toAccountComboField.setContainerDataSource(container);
			toAccountComboField.setItemCaptionPropertyId("name");
			toAccountComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadContractorCombo() {
		List<Object> suppList = null;
		try {
			suppList = new ContractorDao()
					.getAllActiveContractorNamesWithLedgerID(getOfficeID());
			container = SCollectionContainer.setList(suppList, "id");
			contractorComboField.setContainerDataSource(container);
			contractorComboField.setItemCaptionPropertyId("name");
			contractorComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		clearErrorMessages();

		boolean valid = true;
		if (contractorComboField.getValue() == null
				|| contractorComboField.getValue().equals("")) {
			setRequiredError(contractorComboField,
					getPropertyName("select_contractor"), true);
			valid = false;
		}
		if (toAccountComboField.getValue() == null
				|| toAccountComboField.getValue().equals("")) {
			setRequiredError(toAccountComboField,
					getPropertyName("select_account"), true);
			valid = false;
		}

		if (!isValidAmount(contractorAmountField)) {
			valid = false;
		}
		if (!isValidAmount(discountField)) {
			valid = false;
		}
		if (!isValidAmount(paymentAmountField)) {
			valid = false;
		}
		return valid;
	}

	private void clearErrorMessages() {
		contractorComboField.setComponentError(null);
		toAccountComboField.setComponentError(null);
		contractorAmountField.setComponentError(null);
		discountField.setComponentError(null);
		paymentAmountField.setComponentError(null);
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
