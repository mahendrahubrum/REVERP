package com.inventory.subscription.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.DocumentAttach;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 27, 2014
 */
public class SubscripitonExpendetureTransactionUI extends SparkLogic {

	private static final long serialVersionUID = 2561215323382808817L;

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TSJ_FROM_LEDGER_ID = "Account ID";
	static String TSJ_FROM_LEDGER_NAME = "Account";
	static String TSJ_AMOUNT = "Amount";
	static final long EXPENSE = 4;

	STable accountDepositEntryTable;
	ExpendetureTransactionDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout accountDepositAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField accountDepositNumberList;

	SComboField accountHeadFilter;

	SComboField accountsList;

	// SNativeSelect accountTypeSelect;

	SComboField expendetureAccSelect;
	SComboField subscriptionCombo;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField amountTextField;
	SDateField date;

	SettingsValuePojo settings;

	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;

	STextField refNoTextField;
	STextArea memoTextArea;

	LedgerDao ledgerDao;

	DocumentAttach attach;

	SButton createNewButton;

	SRadioButton cashOrCheck;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings({ "deprecation", "unchecked", "serial" })
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		List lst = new ArrayList();
		lst.add(new KeyValue(0, "All"));
		lst.add(new KeyValue(1, "Customers"));
		lst.add(new KeyValue(2, "Suppliers"));
		// accountTypeSelect = new SNativeSelect("From", 140, lst, "intKey",
		// "value");

		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute(
					"settings");

		cashOrCheck = new SRadioButton(null, 200, SConstants.cashOrCheckList,
				"intKey", "value");
		cashOrCheck.setHorizontal(true);

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		daoObj = new ExpendetureTransactionDao();
		ledgerDao = new LedgerDao();

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton(getPropertyName("update"));

		saveButton = new SButton(getPropertyName("save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateButton = new SButton(getPropertyName("update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");

		deleteButton = new SButton(getPropertyName("delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");

		cancelButton = new SButton(getPropertyName("cancel"), 78);
		cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		cancelButton.setStyleName("deletebtnStyle");

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveButton);
		mainButtonLayout.addComponent(updateButton);
		if (settings.isKEEP_DELETED_DATA())
			mainButtonLayout.addComponent(cancelButton);
		else
			mainButtonLayout.addComponent(deleteButton);

		updateButton.setVisible(false);
		deleteButton.setVisible(false);
		cancelButton.setVisible(false);

		date = new SDateField(null, 120, getDateFormat(), getWorkingDate());
		refNoTextField = new STextField();
		memoTextArea = new STextArea(getPropertyName("comments"), 500, 40);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(900, 505);
		try {

			updateItemButton.setVisible(false);

			accountDepositEntryTable = new STable(null, 800, 160);

			List list = new ArrayList();
			list.add(new PaymentDepositModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllExpendetureAsRefNoList(getOfficeID()));

			accountDepositNumberList = new SComboField(null, 200, list, "id",
					"ref_no", true, "Create New");

			expendetureAccSelect = new SComboField(null, 200,
					daoObj.getAllDirectAddedLedgersUnderType(getOfficeID(),
							EXPENSE), "id", "name", true, "Select");
			subscriptionCombo=new SComboField(null, 175,
					new SubscriptionCreationDao().getAllSubscriptions(getOfficeID(),getLoginID()),
					"id","name",true,"--------------Select--------------");

			accountsList = new SComboField(getPropertyName("from_account"), 200);

			accountDepositAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSpacing(true);
//			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			accountDepositEntryTable.setSizeFull();
			accountDepositEntryTable.setSelectable(true);
			accountDepositEntryTable.setMultiSelect(true);

			accountDepositEntryTable.setWidth("860px");
			accountDepositEntryTable.setHeight("100px");

			accountDepositEntryTable.addContainerProperty(TSR_SN,
					Integer.class, null, "#", null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_FROM_LEDGER_ID,
					Long.class, null, TSJ_FROM_LEDGER_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_FROM_LEDGER_NAME,
					String.class, null, getPropertyName("account"), null,
					Align.LEFT);

			accountDepositEntryTable.addContainerProperty(TSJ_AMOUNT,
					Double.class, null, getPropertyName("amount"), null,
					Align.CENTER);

			accountDepositEntryTable.setColumnExpandRatio(TSR_SN, (float) .5);
			accountDepositEntryTable.setColumnExpandRatio(TSJ_FROM_LEDGER_NAME,
					2);
			accountDepositEntryTable.setColumnExpandRatio(TSJ_AMOUNT,
					(float) 1.5);

			accountDepositAddGrid.setColumns(8);
			accountDepositAddGrid.setSizeFull();
			accountDepositAddGrid.setRows(2);

			amountTextField = new STextField(getPropertyName("amount"), 80);

			// accountDepositAddGrid.addComponent(accountTypeSelect);
			accountDepositAddGrid.addComponent(accountsList);
			accountDepositAddGrid.addComponent(amountTextField);
			accountDepositAddGrid.addComponent(addItemButton);
			accountDepositAddGrid.addComponent(updateItemButton);

			accountDepositAddGrid.setStyleName("journal_adding_grid");

			amountTextField.setStyleName("textfield_align_right");

			accountDepositAddGrid.setComponentAlignment(addItemButton,
					Alignment.BOTTOM_RIGHT);
			accountDepositAddGrid.setComponentAlignment(updateItemButton,
					Alignment.BOTTOM_RIGHT);

//			accountDepositAddGrid.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("bill_no")), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(accountDepositNumberList);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(refNoTextField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),5, 0);
			masterDetailsGrid.addComponent(date, 6, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("exp_account"), 30), 1, 1);
			masterDetailsGrid.addComponent(expendetureAccSelect, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("payment_type"), 30), 3, 1);
			masterDetailsGrid.addComponent(cashOrCheck, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("rental_item"), 30), 5, 1);
			masterDetailsGrid.addComponent(subscriptionCombo, 6, 1);

			
			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			accountDepositAddGrid.setSpacing(true);

			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(accountDepositEntryTable);

			stkrkVLay.addComponent(accountDepositAddGrid);

			SHorizontalLayout fm = new SHorizontalLayout();
			fm.addComponent(memoTextArea);
			attach = new DocumentAttach(SConstants.documentAttach.CHEQUE);
			fm.addComponent(attach);
			stkrkVLay.addComponent(fm);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(accountDepositAddGrid,
					Alignment.BOTTOM_CENTER);

			accountDepositEntryTable.setVisibleColumns(new Object[] { TSR_SN,
					TSJ_FROM_LEDGER_NAME, TSJ_AMOUNT });

			accountDepositEntryTable.setFooterVisible(true);
			accountDepositEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(0)));
			accountDepositEntryTable.setColumnFooter(TSJ_FROM_LEDGER_NAME,
					getPropertyName("total"));

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(stkrkVLay);

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			panel.setContent(windowNotif);
			expendetureAccSelect.focus();
			loadData(0);
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)accountDepositNumberList.getValue(),confirmBox.getUserID());
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
						if(accountDepositNumberList.getValue()!=null && !accountDepositNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)accountDepositNumberList.getValue(),
									"Expenditure Transaction : No. "+accountDepositNumberList.getItemCaption(accountDepositNumberList.getValue()));
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
						if(accountDepositNumberList.getValue()!=null && !accountDepositNumberList.getValue().toString().equals("0")) {
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

			cashOrCheck.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent arg0) {
					try {
						if ((Integer) cashOrCheck.getValue() == 1)
							attach.setVisible(false);
						else
							attach.setVisible(true);

						loadAccountCombo();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			cashOrCheck.setValue((int) 1);
			// if((Boolean)getHttpSession().getAttribute("expendeture_acct_cash_only")==true)
			// {
			// cashOrCheck.setReadOnly(true);
			// cashOrCheck.setDescription("Change settings as Bank and Cash if you need to change Bank.");
			// }

			expendetureAccSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (expendetureAccSelect.getValue() != null) {
									expendetureAccSelect.setDescription("<i class='ledger_bal_style'>"
											+ getPropertyName("current_balance")
											+ " : "
											+ ledgerDao
													.getLedgerCurrentBalance((Long) expendetureAccSelect
															.getValue())
											+ "</i>");
								} else
									expendetureAccSelect.setDescription(null);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			accountsList
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (accountsList.getValue() != null) {
									accountsList.setDescription("<i class='ledger_bal_style'>"
											+ getPropertyName("current_balance")
											+ " : "
											+ ledgerDao
													.getLedgerCurrentBalance((Long) accountsList
															.getValue())
											+ "</i>");
								} else
									accountsList.setDescription(null);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					accountDepositNumberList.setValue((long) 0);
				}
			});

			accountDepositNumberList.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								attach.clear();
								accountDepositEntryTable.removeAllItems();
								if (accountDepositNumberList.getValue() != null
										&& !accountDepositNumberList.getValue()
												.toString().equals("0")) {

									updateButton.setVisible(true);
									deleteButton.setVisible(true);
									cancelButton.setVisible(true);
									saveButton.setVisible(false);

									PaymentDepositModel objModel = daoObj.getExpendetureTransaction((Long) accountDepositNumberList.getValue());

									date.setValue(objModel.getDate());
									memoTextArea.setValue(objModel.getMemo());
									refNoTextField.setValue(objModel.getRef_no());
									subscriptionCombo.setValue(objModel.getSubscription());

									accountDepositEntryTable
											.setVisibleColumns(new Object[] {
													TSR_SN, TSJ_FROM_LEDGER_ID,
													TSJ_FROM_LEDGER_NAME,
													TSJ_AMOUNT });

									int ct = 0;
									Iterator it = objModel.getTransaction()
											.getTransaction_details_list()
											.iterator();
									while (it.hasNext()) {
										TransactionDetailsModel trn = (TransactionDetailsModel) it
												.next();
										ct++;

										String amtType = "CR";
										if (ct == 1)
											expendetureAccSelect.setValue(trn
													.getToAcct().getId());

										accountDepositEntryTable.addItem(
												new Object[] {
														ct,
														trn.getFromAcct()
																.getId(),
														trn.getFromAcct()
																.getName(),
														trn.getAmount() }, ct);

									}
									accountDepositEntryTable
											.setVisibleColumns(new Object[] {
													TSR_SN,
													TSJ_FROM_LEDGER_NAME,
													TSJ_AMOUNT });
									updateItemButton.setVisible(false);
									addItemButton.setVisible(true);
									attach.loadDocument(
											(Long) accountDepositNumberList
													.getValue(), getOfficeID(),
											SConstants.EXPENDETURE_TRANSACTION);

								} 
								else {
									refNoTextField.setValue("");
									memoTextArea.setValue("");
									subscriptionCombo.setValue(null);
									expendetureAccSelect.setValue(null);
									accountsList.setValue(null);
									date.setValue(getWorkingDate());
									updateButton.setVisible(false);
									deleteButton.setVisible(false);
									cancelButton.setVisible(false);
									saveButton.setVisible(true);

									expendetureAccSelect.focus();

								}
								calculateTotals();

							} catch (Exception e) {
							}

						}
					});

			updateButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							PaymentDepositModel objMdl = daoObj
									.getExpendetureTransaction((Long) accountDepositNumberList
											.getValue());

							objMdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setRef_no(refNoTextField.getValue());
							objMdl.setMemo(memoTextArea.getValue());
							objMdl.setType(SConstants.RENTAL_EXPENDETURE);
							objMdl.setSubscription((Long)subscriptionCombo.getValue());

							FinTransaction tran = new FinTransaction();
							Iterator it = accountDepositEntryTable.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it.next());
								int amtType = 1;
								tran.addTransactionWithNarration(amtType,
										(Long) item.getItemProperty(TSJ_FROM_LEDGER_ID).getValue(),
										(Long) expendetureAccSelect.getValue(),
										(Double) item.getItemProperty(TSJ_AMOUNT).getValue(), 
										subscriptionCombo.getItemCaption((Long)subscriptionCombo.getValue()));
							}
							TransactionModel trObj = tran.getTransaction(
									SConstants.RENTAL_EXPENDETURE,
									CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							trObj.setTransaction_id(objMdl.getTransaction()
									.getTransaction_id());
							objMdl.setTransaction(trObj);

							daoObj.update(objMdl);

							attach.saveDocument(
									(Long) accountDepositNumberList.getValue(),
									getOfficeID(),
									SConstants.RENTAL_EXPENDETURE);

							saveActivity(
									getOptionId(),
									"Expenditure Transaction Updated. Bill No : "
											+ accountDepositNumberList
													.getItemCaption(accountDepositNumberList
															.getValue())
											+ ", Expenditure Acct. : "
											+ expendetureAccSelect
													.getItemCaption(expendetureAccSelect
															.getValue())
											+ ", Payment Amount : "
											+ roundNumber(toDouble(accountDepositEntryTable
													.getColumnFooter(TSJ_AMOUNT)
													.toString())),(Long)accountDepositNumberList
													.getValue());

							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
							loadData(objMdl.getId());
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (accountDepositNumberList.getValue() != null
							&& !accountDepositNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) accountDepositNumberList.getValue());
												attach.deleteDocument(toLong(accountDepositNumberList
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.RENTAL_EXPENDETURE);

												saveActivity(
														getOptionId(),
														"Expenditure Transaction Deleted. Bill No : "
																+ accountDepositNumberList
																		.getItemCaption(accountDepositNumberList
																				.getValue())
																+ ", Expenditure Acct. : "
																+ expendetureAccSelect
																		.getItemCaption(expendetureAccSelect
																				.getValue())
																+ ", Payment Amount : "
																+ roundNumber(toDouble(accountDepositEntryTable
																		.getColumnFooter(
																				TSJ_AMOUNT)
																		.toString())),(Long)accountDepositNumberList
																		.getValue());

												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadData(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			cancelButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (accountDepositNumberList.getValue() != null
							&& !accountDepositNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.cancel((Long) accountDepositNumberList
														.getValue());
												attach.deleteDocument(
														toLong(accountDepositNumberList
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.EXPENDETURE_TRANSACTION);

												saveActivity(
														getOptionId(),
														"Expenditure Transaction Deleted. Bill No : "
																+ accountDepositNumberList
																		.getItemCaption(accountDepositNumberList
																				.getValue())
																+ ", Expenditure Acct. : "
																+ expendetureAccSelect
																		.getItemCaption(expendetureAccSelect
																				.getValue())
																+ ", Payment Amount : "
																+ roundNumber(toDouble(accountDepositEntryTable
																		.getColumnFooter(
																				TSJ_AMOUNT)
																		.toString())),(Long)accountDepositNumberList
																		.getValue());

												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadData(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			accountDepositEntryTable
					.addListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							Collection selectedItems = null;

							if (accountDepositEntryTable.getValue() != null) {
								selectedItems = (Collection) accountDepositEntryTable
										.getValue();
							}

							if (selectedItems != null
									&& selectedItems.size() == 1) {

								Item item = accountDepositEntryTable
										.getItem(selectedItems.iterator()
												.next());

								accountsList.setValue(item.getItemProperty(
										TSJ_FROM_LEDGER_ID).getValue());
								amountTextField
										.setValue(asString(item
												.getItemProperty(TSJ_AMOUNT)
												.getValue()));

								updateItemButton.setVisible(true);
								addItemButton.setVisible(false);

								accountsList.focus();
							} else {
								updateItemButton.setVisible(false);
								addItemButton.setVisible(true);

								accountsList.setValue(null);
								amountTextField.setValue("");
								accountsList.focus();
							}
						}
					});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(accountDepositEntryTable, null, false);
						if (isAddingValid()) {

							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_FROM_LEDGER_ID,
											TSJ_FROM_LEDGER_NAME, TSJ_AMOUNT });

							Collection selectedItems = null;

							if (accountDepositEntryTable.getValue() != null) {
								selectedItems = (Collection) accountDepositEntryTable
										.getValue();
							}

							int sel_id = (Integer) selectedItems.iterator()
									.next();
							Item item = accountDepositEntryTable
									.getItem(sel_id);

							item.getItemProperty(TSJ_FROM_LEDGER_ID).setValue(
									accountsList.getValue());
							item.getItemProperty(TSJ_FROM_LEDGER_NAME)
									.setValue(
											accountsList
													.getItemCaption(accountsList
															.getValue()));

							double amt = toDouble(amountTextField.getValue());

							item.getItemProperty(TSJ_AMOUNT).setValue(amt);

							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_FROM_LEDGER_NAME, TSJ_AMOUNT });

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);

							accountDepositEntryTable.setValue(null);

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(accountDepositEntryTable, null, false);
						if (isAddingValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_FROM_LEDGER_ID,
											TSJ_FROM_LEDGER_NAME, TSJ_AMOUNT });

							int id = 0, ct = accountDepositEntryTable
									.getItemIds().size();
							Iterator it1 = accountDepositEntryTable
									.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							id++;
							ct++;

							double amt = toDouble(amountTextField.getValue());

							accountDepositEntryTable.addItem(
									new Object[] {
											ct,
											(Long) accountsList.getValue(),
											accountsList
													.getItemCaption(accountsList
															.getValue()), amt },
									id);

							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_FROM_LEDGER_NAME, TSJ_AMOUNT });

							accountsList.setValue(null);
							amountTextField.setValue("");
							accountsList.focus();

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							PaymentDepositModel objMdl = new PaymentDepositModel();

							objMdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setRef_no(refNoTextField.getValue());
							objMdl.setMemo(memoTextArea.getValue());
							objMdl.setStatus(1);
							objMdl.setType(SConstants.RENTAL_EXPENDETURE);
							objMdl.setBill_no(getNextSequence("Rental Expenditure", getLoginID()));
							objMdl.setSubscription((Long)subscriptionCombo.getValue());
							
							FinTransaction tran = new FinTransaction();
							Iterator it = accountDepositEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it
										.next());
								int amtType = 1;
								tran.addTransactionWithNarration(amtType,
										(Long) item.getItemProperty(TSJ_FROM_LEDGER_ID).getValue(),
										(Long) expendetureAccSelect.getValue(),
										(Double) item.getItemProperty(TSJ_AMOUNT).getValue(), 
										subscriptionCombo.getItemCaption((Long)subscriptionCombo.getValue()));
							}
							objMdl.setTransaction(tran.getTransaction(SConstants.RENTAL_EXPENDETURE,CommonUtil.getSQLDateFromUtilDate(date.getValue())));

							long id = daoObj.save(objMdl);
							attach.saveDocument(id, getOfficeID(),
									SConstants.RENTAL_EXPENDETURE);

							saveActivity(
									getOptionId(),
									"Expenditure Transaction Saved. Bill No : "
											+ objMdl.getBill_no()
											+ ", Expenditure Acct. : "
											+ expendetureAccSelect
													.getItemCaption(expendetureAccSelect
															.getValue())
											+ ", Payment Amount : "
											+ roundNumber(toDouble(accountDepositEntryTable
													.getColumnFooter(TSJ_AMOUNT)
													.toString())),objMdl.getId());

							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							loadData(id);

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			accountDepositEntryTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					accountDepositEntryTable.setValue(null);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadData(0);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			accountDepositEntryTable.addShortcutListener(new ShortcutListener(
					"Submit Item", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			final Action actionDeleteStock = new Action(
					getPropertyName("Delete"));

			accountDepositEntryTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return panel;
	}

	@SuppressWarnings("unchecked")
	public void loadData(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new PaymentDepositModel(0, "-----------Create New-----------"));
			list.addAll(daoObj.getAllSubscriptionExpendetureAsBillNoList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			accountDepositNumberList.setContainerDataSource(bic);
			accountDepositNumberList.setItemCaptionPropertyId("ref_no");
			accountDepositNumberList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	private void loadAccountCombo() {
		List<Object> list = null;
		try {
			List groupList = new ArrayList();
			if ((Integer) cashOrCheck.getValue() == 1)
				groupList.add(settings.getCASH_GROUP());
//			else
//				groupList.add(SConstants.BANK_ACCOUNT_GROUP_ID);
			list = new LedgerDao()
					.getAllLedgersUnderGroupAndSubGroupsFromGroupList(
							getOfficeID(), getOrganizationID(), groupList);
			SCollectionContainer suppContainer = SCollectionContainer.setList(
					list, "id");
			accountsList.setContainerDataSource(suppContainer);
			accountsList.setItemCaptionPropertyId("name");
			accountsList.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteItem() {
		try {

			if (accountDepositEntryTable.getValue() != null) {

				Collection selectedItems = (Collection) accountDepositEntryTable
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					accountDepositEntryTable.removeItem(it1.next());
				}

				int SN = 0;
				Iterator it = accountDepositEntryTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					Item newitem = accountDepositEntryTable
							.getItem((Integer) it.next());

					newitem.getItemProperty(TSR_SN).setValue(SN);

				}
				calculateTotals();
			}
			accountDepositEntryTable.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isAddingValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (amountTextField.getValue() == null
				|| amountTextField.getValue().equals("")) {
			setRequiredError(amountTextField, getPropertyName("invalid_data"),
					true);
			amountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(amountTextField.getValue()) < 0) {
					setRequiredError(amountTextField,
							getPropertyName("invalid_data"), true);
					amountTextField.focus();
					ret = false;
				} else
					setRequiredError(amountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(amountTextField,
						getPropertyName("invalid_data"), true);
				amountTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (accountsList.getValue() == null
				|| accountsList.getValue().equals("")) {
			setRequiredError(accountsList,
					getPropertyName("invalid_selection"), true);
			accountsList.focus();
			ret = false;
		} else
			setRequiredError(accountsList, null, false);

		return ret;
	}

	public void calculateTotals() {
		try {

			double amtttl = 0;

			Iterator it = accountDepositEntryTable.getItemIds().iterator();
			while (it.hasNext()) {
				Item item = accountDepositEntryTable.getItem(it.next());

				amtttl += (Double) item.getItemProperty(TSJ_AMOUNT).getValue();
			}

			accountDepositEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(amtttl)));

		} catch (Exception e) {
			// TODO: handle exception
			Notification.show(
					"Error..!!",
					"Error Message from Method calculateTotal() :"
							+ e.getCause(), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (accountDepositEntryTable.getItemIds().size() <= 0) {
			setRequiredError(accountDepositEntryTable,
					getPropertyName("add_some_items"), true);
			expendetureAccSelect.focus();
			ret = false;
		} else
			setRequiredError(accountDepositEntryTable, null, false);

		if (expendetureAccSelect.getValue() == null || expendetureAccSelect.getValue().equals("")) {
			setRequiredError(expendetureAccSelect,
					getPropertyName("invalid_selection"), true);
			expendetureAccSelect.focus();
			ret = false;
		} else
			setRequiredError(expendetureAccSelect, null, false);
		
		if (subscriptionCombo.getValue() == null || subscriptionCombo.getValue().equals("")) {
			setRequiredError(subscriptionCombo,
					getPropertyName("invalid_selection"), true);
			subscriptionCombo.focus();
			ret = false;
		} else
			setRequiredError(subscriptionCombo, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	public SComboField getAccountDepositNumberList() {
		return accountDepositNumberList;
	}

	public void setAccountDepositNumberList(SComboField accountDepositNumberList) {
		this.accountDepositNumberList = accountDepositNumberList;
	}

	@Override
	public SComboField getBillNoFiled() {
		return accountDepositNumberList;
	}

	
	
}
