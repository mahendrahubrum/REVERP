package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.IncomeTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 30, 2013
 */
public class IncomeTransactionUI extends SparkLogic {

	private static final long serialVersionUID = 2561215323382808817L;

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TSJ_FROM_LEDGER_ID = "Account ID";
	static String TSJ_FROM_LEDGER_NAME = "Account";
	static String TSJ_AMOUNT = "Amount";
	static final long INCOME = 3;

	STable accountDepositEntryTable;
	IncomeTransactionDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout accountDepositAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField accountDepositNumberList;

	SComboField accountHeadFilter;

	SComboField accountsList;

	SNativeSelect accountTypeSelect;

	SComboField incomeAccSelect;

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

	LedgerDao ledgerDao = new LedgerDao();

	SButton createNewButton;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings({ "deprecation", "unchecked" })
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
		accountTypeSelect = new SNativeSelect(getPropertyName("from"), 140,
				lst, "intKey", "value");

		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute(
					"settings");

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		daoObj = new IncomeTransactionDao();
		ledgerDao = new LedgerDao();

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton(getPropertyName("Update"));

		saveButton = new SButton(getPropertyName("Save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateButton = new SButton(getPropertyName("Update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");

		deleteButton = new SButton(getPropertyName("Delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");

		cancelButton = new SButton(getPropertyName("Cancel"), 78);
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
		memoTextArea = new STextArea(getPropertyName("comments"), 760, 40);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(900, 580);
		try {

			updateItemButton.setVisible(false);

			accountDepositEntryTable = new STable(null, 800, 160);

			List list = new ArrayList();
			list.add(new PaymentDepositModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllIncomeAsRefNoList(getOfficeID()));

			accountDepositNumberList = new SComboField(null, 200, list, "id",
					"ref_no", true, getPropertyName("create_new"));

			incomeAccSelect = new SComboField(null, 200,
					daoObj.getAllLedgersUnderType(getOfficeID(), INCOME), "id",
					"name", true, getPropertyName("select"));

			accountsList = new SComboField(getPropertyName("from_account"),
					200, null, "id", "name", true, getPropertyName("select"));

			accountDepositAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			accountDepositEntryTable.setSizeFull();
			accountDepositEntryTable.setSelectable(true);
			accountDepositEntryTable.setMultiSelect(true);

			accountDepositEntryTable.setWidth("860px");
			accountDepositEntryTable.setHeight("180px");

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
			accountDepositAddGrid.setRows(2);

			amountTextField = new STextField(getPropertyName("amount"), 80);

			accountDepositAddGrid.addComponent(accountTypeSelect);
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

			accountDepositAddGrid.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("deposit_id")), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(accountDepositNumberList);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);

			// masterDetailsGrid.addComponent(new SLabel("Pay To :"), 3, 1);
			// masterDetailsGrid.addComponent(, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("income_acc")), 1, 1);
			masterDetailsGrid.addComponent(incomeAccSelect, 2, 1);

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(refNoTextField, 4, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

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

			SFormLayout fm = new SFormLayout();
			fm.addComponent(memoTextArea);
			stkrkVLay.addComponent(fm);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(accountDepositAddGrid,
					Alignment.BOTTOM_CENTER);

			accountDepositEntryTable.setVisibleColumns(new String[] { TSR_SN,
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

			incomeAccSelect.focus();
			
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

			final Action actionDeleteStock = new Action("Delete");

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
			e.printStackTrace();
		}
			
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
									"Income Transaction : No. "+accountDepositNumberList.getItemCaption(accountDepositNumberList.getValue()));
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

			incomeAccSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (incomeAccSelect.getValue() != null) {
							incomeAccSelect.setDescription("<i class='ledger_bal_style'>"
									+ getPropertyName("current_balance")
									+ " : "
									+ ledgerDao
											.getLedgerCurrentBalance((Long) incomeAccSelect
													.getValue()) + "</i>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			accountsList.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (accountsList.getValue() != null) {
							accountsList.setDescription("<i class='ledger_bal_style'>"
									+ getPropertyName("current_balance")
									+ " : "
									+ ledgerDao
											.getLedgerCurrentBalance((Long) accountsList
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
					accountDepositNumberList.setValue((long) 0);
				}
			});

			accountTypeSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						SCollectionContainer bic = null;

						if ((Integer) accountTypeSelect.getValue() == 0) {
							bic = SCollectionContainer.setList(ledgerDao
									.getAllActiveLedgerNames(getOfficeID()),
									"id");
						} else if ((Integer) accountTypeSelect.getValue() == 1) {
							bic = SCollectionContainer.setList(ledgerDao
									.getAllActiveLedgerNamesUnderGroup(
											settings.getCUSTOMER_GROUP(),
											getOfficeID()), "id");
						} else {
							bic = SCollectionContainer.setList(ledgerDao
									.getAllActiveLedgerNamesUnderGroup(
											settings.getSUPPLIER_GROUP(),
											getOfficeID()), "id");

						}
						accountsList.setContainerDataSource(bic);
						accountsList.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});

			accountTypeSelect.setValue(0);

			accountDepositNumberList
					.addValueChangeListener(new ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {

								accountDepositEntryTable.removeAllItems();
								if (accountDepositNumberList.getValue() != null
										&& !accountDepositNumberList.getValue()
												.toString().equals("0")) {

									updateButton.setVisible(true);
									deleteButton.setVisible(true);
									cancelButton.setVisible(true);
									saveButton.setVisible(false);

									PaymentDepositModel objModel = daoObj.getIncomeTransaction((Long) accountDepositNumberList
													.getValue());

									date.setValue(objModel.getDate());
									memoTextArea.setValue(objModel.getMemo());
									refNoTextField.setValue(objModel
											.getRef_no());

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
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
											incomeAccSelect.setValue(trn
													.getFromAcct().getId());

										accountDepositEntryTable
												.addItem(
														new Object[] {
																ct,
																trn.getToAcct()
																		.getId(),
																trn.getToAcct()
																		.getName(),
																trn.getAmount() },
														ct);

									}

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN,
													TSJ_FROM_LEDGER_NAME,
													TSJ_AMOUNT });

									updateItemButton.setVisible(false);
									addItemButton.setVisible(true);

									// incomeAccSelect.focus();

								} else {
									refNoTextField.setValue("");
									memoTextArea.setValue("");

									incomeAccSelect.setValue(null);
									accountsList.setValue(null);

									date.setValue(getWorkingDate());
									updateButton.setVisible(false);
									deleteButton.setVisible(false);
									cancelButton.setVisible(false);
									saveButton.setVisible(true);

									incomeAccSelect.focus();

								}

								calculateTotals();

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});

			updateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							PaymentDepositModel objMdl = daoObj
									.getIncomeTransaction((Long) accountDepositNumberList
											.getValue());

							objMdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setRef_no(refNoTextField.getValue());
							objMdl.setMemo(memoTextArea.getValue());
							objMdl.setType(SConstants.INCOME_TRANSACTION);

							FinTransaction tran = new FinTransaction();
							Iterator it = accountDepositEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it
										.next());
								int amtType = 1;
								tran.addTransaction(
										amtType,
										(Long) incomeAccSelect.getValue(),
										(Long) item.getItemProperty(
												TSJ_FROM_LEDGER_ID).getValue(),
										(Double) item.getItemProperty(
												TSJ_AMOUNT).getValue());
							}

							TransactionModel trObj = tran.getTransaction(
									SConstants.INCOME_TRANSACTION, CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue()));
							trObj.setTransaction_id(objMdl.getTransaction()
									.getTransaction_id());
							objMdl.setTransaction(trObj);

							daoObj.update(objMdl);

							Notification.show(getPropertyName("Success"),
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);

							saveActivity(
									getOptionId(),
									"Income Transaction Updated. Bill No : "
											+ objMdl.getBill_no()
											+ ", Income Acct. : "
											+ incomeAccSelect
													.getItemCaption(incomeAccSelect
															.getValue())
											+ ", Amount : "
											+ amountTextField.getValue(),objMdl.getId());

							loadData(objMdl.getId());

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification
								.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
					}
				}
			});

			deleteButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (accountDepositNumberList.getValue() != null
							&& !accountDepositNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) accountDepositNumberList
														.getValue());
												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"Income Transaction Updated. Bill No : "
																+ accountDepositNumberList
																		.getItemCaption(accountDepositNumberList
																				.getValue())
																+ ", Income Acct. : "
																+ incomeAccSelect
																		.getItemCaption(incomeAccSelect
																				.getValue())
																+ ", Amount : "
																+ amountTextField
																		.getValue(),(Long)accountDepositNumberList
																		.getValue());

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

			cancelButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (accountDepositNumberList.getValue() != null
							&& !accountDepositNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.cancel((Long) accountDepositNumberList
														.getValue());
												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"Income Transaction Updated. Bill No : "
																+ accountDepositNumberList
																		.getItemCaption(accountDepositNumberList
																				.getValue())
																+ ", Income Acct. : "
																+ incomeAccSelect
																		.getItemCaption(incomeAccSelect
																				.getValue())
																+ ", Amount : "
																+ amountTextField
																		.getValue(),(Long)accountDepositNumberList
																		.getValue());

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
					.addValueChangeListener(new ValueChangeListener() {

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

			updateItemButton.addClickListener(new ClickListener() {
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

			addItemButton.addClickListener(new ClickListener() {
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

			saveButton.addClickListener(new ClickListener() {
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
							objMdl.setType(SConstants.INCOME_TRANSACTION);
							objMdl.setBill_no(getNextSequence(
									"Income_Transaction_No", getLoginID()));

							FinTransaction tran = new FinTransaction();
							Iterator it = accountDepositEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it
										.next());
								int amtType = 1;
								tran.addTransaction(
										amtType,
										(Long) incomeAccSelect.getValue(),
										(Long) item.getItemProperty(TSJ_FROM_LEDGER_ID).getValue(),
										(Double) item.getItemProperty(
												TSJ_AMOUNT).getValue());
							}

							objMdl.setTransaction(tran.getTransaction(
									SConstants.INCOME_TRANSACTION, CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue())));

							long id = daoObj.save(objMdl);

							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							saveActivity(
									getOptionId(),
									"Income Transaction Saved. Bill No : "
											+ objMdl.getBill_no()
											+ ", Income Acct. : "
											+ incomeAccSelect
													.getItemCaption(incomeAccSelect
															.getValue())
											+ ", Amount : "
											+ amountTextField.getValue(),objMdl.getId());

							loadData(id);

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),

						Type.ERROR_MESSAGE);
					}
				}
			});

		

		return panel;
	}

	public void loadData(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new PaymentDepositModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllIncomeAsRefNoList(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			accountDepositNumberList.setContainerDataSource(bic);
			accountDepositNumberList.setItemCaptionPropertyId("ref_no");

			accountDepositNumberList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
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
			e.printStackTrace();
		}
	}

	public Boolean isAddingValid() {
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
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (accountDepositEntryTable.getItemIds().size() <= 0) {
			setRequiredError(accountDepositEntryTable,
					getPropertyName("add_some_items"), true);
			incomeAccSelect.focus();
			ret = false;
		} else
			setRequiredError(accountDepositEntryTable, null, false);

		if (incomeAccSelect.getValue() == null
				|| incomeAccSelect.getValue().equals("")) {
			setRequiredError(incomeAccSelect,
					getPropertyName("invalid_selection"), true);
			incomeAccSelect.focus();
			ret = false;
		} else
			setRequiredError(incomeAccSelect, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return accountDepositNumberList;
	}

}
