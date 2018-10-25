package com.inventory.journal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 11, 2013
 */
@SuppressWarnings("serial")
public class JournalUI extends SparkLogic {

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TSJ_FROM_LEDGER_ID = "From Account ID";
	static String TSJ_FROM_LEDGER_NAME = "From Account";
	static String TSJ_TO_LEDGER_ID = "To Account ID";
	static String TSJ_TO_LEDGER_NAME = "To Account";
	static String TSJ_AMOUNT = "Amount";
	static String TSJ_CR_OR_DR = "Amount Type";

	STable journalEntryTable;
	JournalDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout journalAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField journalNumberList;
	SComboField fromLedgerSelect;
	SComboField toLedgerSelect;
	SNativeSelect amountTypeSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField amountTextField;
	SDateField date;

	SButton saveJournalButton;
	SButton updateJournalButton;
	SButton deleteJournalButton;
	SButton cancelJournalButton;

	STextField refNoTextField;
	STextArea memoTextArea;

	SButton createNewButton;
	CommonMethodsDao comDao;

	SettingsValuePojo settings;
	WrappedSession session;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		daoObj = new JournalDao();
		comDao = new CommonMethodsDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Journal");

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton("Update");

		saveJournalButton = new SButton(getPropertyName("save"), 70);
		saveJournalButton.setStyleName("savebtnStyle");
		saveJournalButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateJournalButton = new SButton(getPropertyName("update"), 80);
		updateJournalButton.setIcon(new ThemeResource(
				"icons/updateSideIcon.png"));
		updateJournalButton.setStyleName("updatebtnStyle");

		deleteJournalButton = new SButton(getPropertyName("delete"), 78);
		deleteJournalButton.setIcon(new ThemeResource(
				"icons/deleteSideIcon.png"));
		deleteJournalButton.setStyleName("deletebtnStyle");

		cancelJournalButton = new SButton(getPropertyName("cancel"), 78);
		cancelJournalButton.setIcon(new ThemeResource(
				"icons/deleteSideIcon.png"));
		cancelJournalButton.setStyleName("deletebtnStyle");

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveJournalButton);
		mainButtonLayout.addComponent(updateJournalButton);

		if (settings.isKEEP_DELETED_DATA())
			mainButtonLayout.addComponent(cancelJournalButton);
		else
			mainButtonLayout.addComponent(deleteJournalButton);

		updateJournalButton.setVisible(false);
		deleteJournalButton.setVisible(false);
		cancelJournalButton.setVisible(false);

		date = new SDateField(null, 120, getDateFormat(), getWorkingDate());
		refNoTextField = new STextField();
		memoTextArea = new STextArea(getPropertyName("memo"), 810, 40);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(900, 560);
		try {

			updateItemButton.setVisible(false);

			journalEntryTable = new STable(null, 800, 200);

			List list = new ArrayList();
			list.add(new JournalModel(0, "----Create New-----"));
			list.addAll(daoObj.getJournalModelList(getOfficeID()));

			journalNumberList = new SComboField(null, 200, list, "id",
					"bill_no", true, getPropertyName("create_new"));

			journalAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			journalEntryTable.setSizeFull();
			journalEntryTable.setSelectable(true);
			journalEntryTable.setMultiSelect(true);

			journalEntryTable.setWidth("860px");
			journalEntryTable.setHeight("180px");

			journalEntryTable.addContainerProperty(TSR_SN, Integer.class, null,
					"#", null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_FROM_LEDGER_ID,
					Long.class, null, TSJ_FROM_LEDGER_ID, null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_FROM_LEDGER_NAME,
					String.class, null, getPropertyName("from_account"), null,
					Align.LEFT);
			journalEntryTable.addContainerProperty(TSJ_TO_LEDGER_ID,
					Long.class, null, TSJ_TO_LEDGER_ID, null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_TO_LEDGER_NAME,
					String.class, null, getPropertyName("to_account"), null,
					Align.LEFT);

			journalEntryTable.addContainerProperty(TSJ_AMOUNT, Double.class,
					null, getPropertyName("amount"), null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_CR_OR_DR, String.class,
					null, getPropertyName("amount_type"), null, Align.LEFT);

			journalEntryTable.setColumnExpandRatio(TSR_SN, (float) .5);
			journalEntryTable.setColumnExpandRatio(TSJ_CR_OR_DR, 1);
			journalEntryTable.setColumnExpandRatio(TSJ_FROM_LEDGER_NAME, 2);
			journalEntryTable.setColumnExpandRatio(TSJ_TO_LEDGER_NAME, 2);
			journalEntryTable.setColumnExpandRatio(TSJ_AMOUNT, (float) 1.5);

			journalAddGrid.setColumns(8);
			journalAddGrid.setRows(2);
			journalAddGrid.setSizeFull();

			amountTypeSelect = new SNativeSelect(
					getPropertyName("amount_type"), 50, SConstants.amountTypes,
					"stringKey", "value");
			amountTypeSelect.setValue("CR");

			List ledgerList = new LedgerDao()
					.getAllActiveLedgerNames(getOfficeID());
			fromLedgerSelect = new SComboField(getPropertyName("from_account"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));
			toLedgerSelect = new SComboField(getPropertyName("to_account"),
					200, ledgerList, "id", "name", true, getPropertyName("select"));

			amountTextField = new STextField(getPropertyName("amount"), 80);
			journalAddGrid.addComponent(amountTypeSelect, 1, 1);
			journalAddGrid.addComponent(fromLedgerSelect, 2, 1);
			journalAddGrid.addComponent(toLedgerSelect, 3, 1);
			journalAddGrid.addComponent(amountTextField, 4, 1);
			journalAddGrid.addComponent(addItemButton, 5, 1);
			journalAddGrid.addComponent(updateItemButton, 6, 1);

			amountTextField.setStyleName("textfield_align_right");

			journalAddGrid.setComponentAlignment(addItemButton,
					Alignment.MIDDLE_CENTER);
			journalAddGrid.setComponentAlignment(updateItemButton,
					Alignment.MIDDLE_CENTER);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("journal_id")), 1, 1);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(journalNumberList);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 1);
			masterDetailsGrid.addComponent(date, 8, 1);

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 1);
			masterDetailsGrid.addComponent(refNoTextField, 4, 1);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			journalAddGrid.setStyleName("journal_adding_grid");
//			journalAddGrid.setSpacing(true);

			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(journalAddGrid);

			stkrkVLay.addComponent(journalEntryTable);

			SFormLayout fm = new SFormLayout();
			fm.addComponent(memoTextArea);
			stkrkVLay.addComponent(fm);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(journalAddGrid,
					Alignment.BOTTOM_CENTER);

			journalEntryTable.setVisibleColumns(new String[] { TSR_SN,
					TSJ_CR_OR_DR, TSJ_FROM_LEDGER_NAME, TSJ_TO_LEDGER_NAME,
					TSJ_AMOUNT });

			journalEntryTable.setFooterVisible(true);
			journalEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(0)));
			journalEntryTable.setColumnFooter(TSJ_TO_LEDGER_NAME,
					getPropertyName("total"));

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(stkrkVLay);
			
			journalEntryTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					journalEntryTable.setValue(null);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadJournal(0);
				}
			});

			journalEntryTable.addShortcutListener(new ShortcutListener(
					"Submit Item", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			panel.setContent(windowNotif);

			fromLedgerSelect.focus();
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)journalNumberList.getValue(),confirmBox.getUserID());
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
						if(journalNumberList.getValue()!=null && !journalNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)journalNumberList.getValue(),
									"Journal : No. "+journalNumberList.getItemCaption(journalNumberList.getValue()));
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
						if(journalNumberList.getValue()!=null && !journalNumberList.getValue().toString().equals("0")) {
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

			fromLedgerSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (fromLedgerSelect.getValue() != null) {
							fromLedgerSelect.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ comDao.getLedgerCurrentBalance((Long) fromLedgerSelect
											.getValue()) + "</i>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			toLedgerSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (toLedgerSelect.getValue() != null) {
							toLedgerSelect.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ comDao.getLedgerCurrentBalance((Long) toLedgerSelect
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
					journalNumberList.setValue((long) 0);
					journalNumberList.setValue(null);
				}
			});

			journalNumberList.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {

						journalEntryTable.removeAllItems();
						if (journalNumberList.getValue() != null
								&& !journalNumberList.getValue().toString()
										.equals("0")) {

							updateJournalButton.setVisible(true);
							deleteJournalButton.setVisible(true);
							cancelJournalButton.setVisible(true);
							saveJournalButton.setVisible(false);

							JournalModel objModel = daoObj
									.getJournalModel((Long) journalNumberList
											.getValue());

							date.setValue(objModel.getDate());
							refNoTextField.setValue(objModel.getRef_no());

							journalEntryTable.setVisibleColumns(new String[] {
									TSR_SN, TSJ_CR_OR_DR, TSJ_FROM_LEDGER_ID,
									TSJ_FROM_LEDGER_NAME, TSJ_TO_LEDGER_ID,
									TSJ_TO_LEDGER_NAME, TSJ_AMOUNT });

							TransactionDetailsModel trn;
							int ct = 0;
							String amtType = "CR";
//							Iterator it = objModel.getTransaction_id()
//									.getTransaction_details_list().iterator();
//							while (it.hasNext()) {
//								trn = (TransactionDetailsModel) it.next();
//								ct++;
//
//								if (trn.getType() == SConstants.DR)
//									amtType = "DR";
//
//								journalEntryTable.addItem(
//										new Object[] { ct, amtType,
//												trn.getFromAcct().getId(),
//												trn.getFromAcct().getName(),
//												trn.getToAcct().getId(),
//												trn.getToAcct().getName(),
//												trn.getAmount() }, ct);
//
//							}

							journalEntryTable.setVisibleColumns(new String[] {
									TSR_SN, TSJ_CR_OR_DR, TSJ_FROM_LEDGER_NAME,
									TSJ_TO_LEDGER_NAME, TSJ_AMOUNT });

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);

							fromLedgerSelect.focus();

						} else {
							refNoTextField.setValue("");
							memoTextArea.setValue("");
							date.setValue(getWorkingDate());
							updateJournalButton.setVisible(false);
							deleteJournalButton.setVisible(false);
							cancelJournalButton.setVisible(false);
							saveJournalButton.setVisible(true);
						}

						if (!isFinYearBackEntry()) {
							saveJournalButton.setVisible(false);
							updateJournalButton.setVisible(false);
							deleteJournalButton.setVisible(false);
							cancelJournalButton.setVisible(false);
							if (journalNumberList.getValue() == null
									|| journalNumberList.getValue().toString()
											.equals("0")) {
								Notification
										.show(getPropertyName("warning_financial_year"),
												Type.WARNING_MESSAGE);
							}
						}

					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});

			updateJournalButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							JournalModel objMdl = daoObj
									.getJournalModel((Long) journalNumberList
											.getValue());

							objMdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setRef_no(refNoTextField.getValue());
//							objMdl.setMemo(memoTextArea.getValue());

							Item item;
							int amtType = 1;
							FinTransaction tran = new FinTransaction();
							Iterator it = journalEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								item = journalEntryTable.getItem(it.next());
								if (amountTypeSelect.getValue().toString()
										.equals("CR"))
									amtType = 1;
								else
									amtType = 2;
								tran.addTransaction(amtType, (Long) item
										.getItemProperty(TSJ_FROM_LEDGER_ID)
										.getValue(), (Long) item
										.getItemProperty(TSJ_TO_LEDGER_ID)
										.getValue(), (Double) item
										.getItemProperty(TSJ_AMOUNT).getValue());
							}

							TransactionModel trObj = tran.getTransaction(
									SConstants.JOURNAL, CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue()));
							trObj.setTransaction_id(objMdl.getTransaction_id());
							objMdl.setTransaction_id(trObj.getTransaction_id());

//							daoObj.updateJounal(objMdl);

							Notification.show("Success",
									"Updated Successfully..!",
									Type.WARNING_MESSAGE);
							loadJournal(objMdl.getId());

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			deleteJournalButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (journalNumberList.getValue() != null
							&& !journalNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.deleteJounal((Long) journalNumberList
														.getValue());
												Notification
														.show("Success",
																"Deleted Successfully..!",
																Type.WARNING_MESSAGE);
												loadJournal(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			cancelJournalButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (journalNumberList.getValue() != null
							&& !journalNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
//												daoObj.cancel((Long) journalNumberList
//														.getValue());
												Notification
														.show("Success",
																"Deleted Successfully..!",
																Type.WARNING_MESSAGE);
												loadJournal(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("deleted_success"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			journalEntryTable.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					Collection selectedItems = null;

					if (journalEntryTable.getValue() != null) {
						selectedItems = (Collection) journalEntryTable
								.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = journalEntryTable.getItem(selectedItems
								.iterator().next());

						fromLedgerSelect.setValue(item.getItemProperty(
								TSJ_FROM_LEDGER_ID).getValue());
						toLedgerSelect.setValue(item.getItemProperty(
								TSJ_TO_LEDGER_ID).getValue());
						amountTextField.setValue(asString(item.getItemProperty(
								TSJ_AMOUNT).getValue()));
						amountTypeSelect.setValue(item.getItemProperty(
								TSJ_CR_OR_DR).getValue());

						updateItemButton.setVisible(true);
						addItemButton.setVisible(false);

						fromLedgerSelect.focus();
					} else {
						updateItemButton.setVisible(false);
						addItemButton.setVisible(true);
						fromLedgerSelect.setValue(null);
						toLedgerSelect.setValue(null);
						amountTextField.setValue("");
						fromLedgerSelect.focus();
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(journalEntryTable, null, false);
						if (isAddingValid()) {

							journalEntryTable.setVisibleColumns(new String[] {
									TSR_SN, TSJ_CR_OR_DR, TSJ_FROM_LEDGER_ID,
									TSJ_FROM_LEDGER_NAME, TSJ_TO_LEDGER_ID,
									TSJ_TO_LEDGER_NAME, TSJ_AMOUNT });

							Collection selectedItems = null;

							if (journalEntryTable.getValue() != null) {
								selectedItems = (Collection) journalEntryTable
										.getValue();
							}

							int sel_id = (Integer) selectedItems.iterator()
									.next();
							Item item = journalEntryTable.getItem(sel_id);

							item.getItemProperty(TSJ_FROM_LEDGER_ID).setValue(
									fromLedgerSelect.getValue());
							item.getItemProperty(TSJ_FROM_LEDGER_NAME)
									.setValue(
											fromLedgerSelect
													.getItemCaption(fromLedgerSelect
															.getValue()));
							item.getItemProperty(TSJ_TO_LEDGER_ID).setValue(
									toLedgerSelect.getValue());
							item.getItemProperty(TSJ_TO_LEDGER_NAME).setValue(
									toLedgerSelect
											.getItemCaption(toLedgerSelect
													.getValue()));

							double amt = toDouble(amountTextField.getValue());

							item.getItemProperty(TSJ_CR_OR_DR).setValue(
									amountTypeSelect.getValue().toString());
							item.getItemProperty(TSJ_AMOUNT).setValue(amt);

							journalEntryTable.setVisibleColumns(new String[] {
									TSR_SN, TSJ_CR_OR_DR, TSJ_FROM_LEDGER_NAME,
									TSJ_TO_LEDGER_NAME, TSJ_AMOUNT });

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);

							journalEntryTable.setValue(null);

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
						setRequiredError(journalEntryTable, null, false);
						if (isAddingValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							journalEntryTable.setVisibleColumns(new String[] {
									TSR_SN, TSJ_CR_OR_DR, TSJ_FROM_LEDGER_ID,
									TSJ_FROM_LEDGER_NAME, TSJ_TO_LEDGER_ID,
									TSJ_TO_LEDGER_NAME, TSJ_AMOUNT });

							int id = 0, ct = journalEntryTable.getItemIds()
									.size();
							Iterator it1 = journalEntryTable.getItemIds()
									.iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							id++;
							ct++;

							double amt = toDouble(amountTextField.getValue());

							journalEntryTable.addItem(
									new Object[] {
											ct,
											amountTypeSelect.getValue()
													.toString(),
											(Long) fromLedgerSelect.getValue(),
											fromLedgerSelect
													.getItemCaption(fromLedgerSelect
															.getValue()),
											(Long) toLedgerSelect.getValue(),
											toLedgerSelect
													.getItemCaption(toLedgerSelect
															.getValue()), amt },
									id);

							journalEntryTable.setVisibleColumns(new String[] {
									TSR_SN, TSJ_CR_OR_DR, TSJ_FROM_LEDGER_NAME,
									TSJ_TO_LEDGER_NAME, TSJ_AMOUNT });

							fromLedgerSelect.setValue(null);
							toLedgerSelect.setValue(null);
							amountTextField.setValue("");
							fromLedgerSelect.focus();

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			saveJournalButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							JournalModel objMdl = new JournalModel();

							objMdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setRef_no(refNoTextField.getValue());

							int amtType = 1;
							Item item;
							FinTransaction tran = new FinTransaction();
							Iterator it = journalEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								item = journalEntryTable.getItem(it.next());

								if (amountTypeSelect.getValue().toString()
										.equals("CR"))
									amtType = 1;
								else
									amtType = 2;
								tran.addTransaction(amtType, (Long) item
										.getItemProperty(TSJ_FROM_LEDGER_ID)
										.getValue(), (Long) item
										.getItemProperty(TSJ_TO_LEDGER_ID)
										.getValue(), (Double) item
										.getItemProperty(TSJ_AMOUNT).getValue());
							}

//							objMdl.setTransaction(tran.getTransaction(SConstants.JOURNAL, CommonUtil.getSQLDateFromUtilDate(date.getValue())));

//							long id = daoObj.saveJounal(objMdl);

							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);

//							loadJournal(id);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			

			final Action actionDeleteStock = new Action("Delete");

			journalEntryTable.addActionHandler(new Action.Handler() {
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

			if (!isFinYearBackEntry()) {
				saveJournalButton.setVisible(false);
				updateJournalButton.setVisible(false);
				deleteJournalButton.setVisible(false);
				cancelJournalButton.setVisible(false);
				Notification.show(getPropertyName("warning_financial_year"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return panel;
	}

	public void loadJournal(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new JournalModel(0, "----Create New-----"));
			list.addAll(daoObj.getJournalModelList(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			journalNumberList.setContainerDataSource(bic);
			journalNumberList.setItemCaptionPropertyId("bill_no");

			journalNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public void deleteItem() {
		try {

			if (journalEntryTable.getValue() != null) {

				Collection selectedItems = (Collection) journalEntryTable
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					journalEntryTable.removeItem(it1.next());
				}

				Item newitem;
				int SN = 0;
				Iterator it = journalEntryTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = journalEntryTable.getItem((Integer) it.next());

					newitem.getItemProperty(TSR_SN).setValue(SN);

				}
			}
			journalEntryTable.focus();

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
			setRequiredError(amountTextField, getPropertyName("enter_amount"),
					true);
			amountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(amountTextField.getValue()) < 0) {
					setRequiredError(amountTextField,
							getPropertyName("enter_valid_amount"), true);
					amountTextField.focus();
					ret = false;
				} else
					setRequiredError(amountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(amountTextField,
						getPropertyName("enter_valid_amount"), true);
				amountTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (toLedgerSelect.getValue() == null
				|| toLedgerSelect.getValue().equals("")) {
			setRequiredError(toLedgerSelect, getPropertyName("select_account"),
					true);
			toLedgerSelect.focus();
			ret = false;
		} else
			setRequiredError(toLedgerSelect, null, false);

		if (fromLedgerSelect.getValue() == null
				|| fromLedgerSelect.getValue().equals("")) {
			setRequiredError(fromLedgerSelect,
					getPropertyName("select_account"), true);
			fromLedgerSelect.focus();
			ret = false;
		} else
			setRequiredError(fromLedgerSelect, null, false);

		if (amountTypeSelect.getValue() == null
				|| amountTypeSelect.getValue().equals("")) {
			setRequiredError(amountTypeSelect,
					getPropertyName("select_amount_type"), true);
			amountTypeSelect.focus();
			ret = false;
		} else
			setRequiredError(amountTypeSelect, null, false);

		return ret;
	}

	public void calculateTotals() {
		try {

			double amtttl = 0;
			Item item;
			Iterator it = journalEntryTable.getItemIds().iterator();
			while (it.hasNext()) {
				item = journalEntryTable.getItem(it.next());

				amtttl += (Double) item.getItemProperty(TSJ_AMOUNT).getValue();
			}

			journalEntryTable.setColumnFooter(TSJ_AMOUNT,
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

		if (journalEntryTable.getItemIds().size() <= 0) {
			setRequiredError(journalEntryTable,
					getPropertyName("add_some_items"), true);
			fromLedgerSelect.focus();
			ret = false;
		} else
			setRequiredError(journalEntryTable, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return journalNumberList;
	}

}
