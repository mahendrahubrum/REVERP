package com.inventory.finance.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.finance.dao.FinanceComponentDao;
import com.inventory.finance.dao.FinancePaymentDao;
import com.inventory.finance.model.FinanceComponentModel;
import com.inventory.finance.model.FinancePaymentDetailsModel;
import com.inventory.finance.model.FinancePaymentModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 30, 2014
 */
public class FinancePaymentUI extends SparkLogic {

	private static final long serialVersionUID = -1887722250641449993L;

	static String TBL_SN = "#";
	static String TBL_FROM_LEDGER_ID = "From Account ID";
	static String TBL_FROM_LEDGER_NAME = "From";
	static String TBL_TO_LEDGER_ID = "To Account ID";
	static String TBL_TO_LEDGER_NAME = "To";
	static String TBL_AMOUNT = "Amount";
	static String TBL_CURRENCY_ID = "Currency ID";
	static String TBL_CURRENCY = "Currency";
	static String TBL_COMMENTS = "Comments";

	STable journalEntryTable;
	FinancePaymentDao dao;
	FinanceComponentDao compDao;

	SComboField journalNumberList;
	SComboField fromLedgerSelect;
	SComboField toLedgerSelect;
	SNativeSelect amountTypeSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField amountTextField;
	STextArea commentsArea;
	SDateField date;

	SButton saveJournalButton;
	SButton updateJournalButton;
	SButton deleteJournalButton;
	SButton cancelJournalButton;

	STextArea memoTextArea;

	SButton createNewButton;

	String[] allHead;
	String[] reqHead;
	
	private SettingsValuePojo settings;
	private WrappedSession session;
	
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

		setSize(980, 550);

		SPanel panel = null;

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		dao = new FinancePaymentDao();
		compDao = new FinanceComponentDao();

		SGridLayout masterDetailsGrid;
		SGridLayout journalAddGrid;
		SVerticalLayout stkrkVLay;

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("add_new"));

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton(getPropertyName("Update"));
		updateItemButton.setVisible(false);

		saveJournalButton = new SButton(getPropertyName("Save"), 70);
		saveJournalButton.setStyleName("savebtnStyle");
		saveJournalButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateJournalButton = new SButton(getPropertyName("Update"), 80);
		updateJournalButton.setIcon(new ThemeResource(
				"icons/updateSideIcon.png"));
		updateJournalButton.setStyleName("updatebtnStyle");

		deleteJournalButton = new SButton(getPropertyName("Delete"), 78);
		deleteJournalButton.setIcon(new ThemeResource(
				"icons/deleteSideIcon.png"));
		deleteJournalButton.setStyleName("deletebtnStyle");
		
		cancelJournalButton = new SButton(getPropertyName("Cancel"), 78);
		cancelJournalButton.setIcon(new ThemeResource(
				"icons/deleteSideIcon.png"));
		cancelJournalButton.setStyleName("deletebtnStyle");

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveJournalButton);
		mainButtonLayout.addComponent(updateJournalButton);
		if(settings.isKEEP_DELETED_DATA())
			mainButtonLayout.addComponent(cancelJournalButton);
		else
			mainButtonLayout.addComponent(deleteJournalButton);

		updateJournalButton.setVisible(false);
		deleteJournalButton.setVisible(false);
		cancelJournalButton.setVisible(false);
		date = new SDateField(null, 120, getDateFormat(), getWorkingDate());
		memoTextArea = new STextArea(getPropertyName("description"), 750, 40);

		allHead = new String[] { TBL_SN, TBL_FROM_LEDGER_ID,
				TBL_FROM_LEDGER_NAME, TBL_TO_LEDGER_ID, TBL_TO_LEDGER_NAME,
				TBL_AMOUNT, TBL_CURRENCY_ID, TBL_CURRENCY, TBL_COMMENTS };

		reqHead = new String[] { TBL_SN, TBL_FROM_LEDGER_NAME,
				TBL_TO_LEDGER_NAME, TBL_AMOUNT, TBL_CURRENCY, TBL_COMMENTS };

		panel = new SPanel();
		panel.setSizeFull();

		try {
			List list = new ArrayList();
			list.add(new FinancePaymentModel(0, "----Create New-----"));
			list.addAll(dao.getAllPaymentNumber(getOfficeID()));

			journalNumberList = new SComboField(null, 200, list, "id",
					"payment_no", true, getPropertyName("create_new"));

			journalAddGrid = new SGridLayout();
			journalAddGrid.setColumns(9);
			journalAddGrid.setRows(2);
			journalAddGrid.setStyleName("layout_bordered");
			journalAddGrid.setSpacing(true);
			journalAddGrid.setHeight("70px");
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(1);
			
			journalEntryTable = new STable(null, 910, 200);
			journalEntryTable.setSelectable(true);

			journalEntryTable.addContainerProperty(TBL_SN, Integer.class, null,
					"#", null, Align.CENTER);
			journalEntryTable.addContainerProperty(TBL_FROM_LEDGER_ID,
					Long.class, null, TBL_FROM_LEDGER_ID, null, Align.CENTER);
			journalEntryTable.addContainerProperty(TBL_FROM_LEDGER_NAME,
					String.class, null, 
					getPropertyName("from"), null, Align.LEFT);
			journalEntryTable.addContainerProperty(TBL_TO_LEDGER_ID,
					Long.class, null, TBL_TO_LEDGER_ID, null, Align.CENTER);
			journalEntryTable.addContainerProperty(TBL_TO_LEDGER_NAME,
					String.class, null, getPropertyName("to"), null, Align.LEFT);

			journalEntryTable.addContainerProperty(TBL_AMOUNT, Double.class,
					null, getPropertyName("amount"), null, Align.CENTER);
			journalEntryTable.addContainerProperty(TBL_CURRENCY_ID, Long.class,
					null, TBL_CURRENCY_ID, null, Align.LEFT);
			journalEntryTable.addContainerProperty(TBL_CURRENCY, String.class,
					null, getPropertyName("currency"), null, Align.LEFT);
			journalEntryTable.addContainerProperty(TBL_COMMENTS, String.class,
					null, getPropertyName("comments"), null, Align.LEFT);

			journalEntryTable.setColumnExpandRatio(TBL_SN, (float) .5);
			journalEntryTable.setColumnExpandRatio(TBL_CURRENCY, 1);
			journalEntryTable.setColumnExpandRatio(TBL_FROM_LEDGER_NAME, 2);
			journalEntryTable.setColumnExpandRatio(TBL_TO_LEDGER_NAME, 2);
			journalEntryTable.setColumnExpandRatio(TBL_AMOUNT, (float) 1.5);

			amountTypeSelect = new SNativeSelect(getPropertyName("currency"), 100,
					new CurrencyManagementDao().getlabels(), "id", "name");
			amountTypeSelect.setValue(getCurrencyID());

			List ledgerList = compDao.getAllActiveComponents(getOfficeID());
			fromLedgerSelect = new SComboField(getPropertyName("from_account"), 180, ledgerList,
					"id", "name", true, getPropertyName("select"));
			toLedgerSelect = new SComboField(getPropertyName("to_account"), 180, ledgerList,
					"id", "name", true, getPropertyName("select"));

			amountTextField = new STextField(getPropertyName("amount"), 80);
			amountTextField.setValue("0");
			commentsArea = new STextArea(getPropertyName("comment"), 200, 25);

			journalAddGrid.addComponent(fromLedgerSelect,1,1);
			journalAddGrid.addComponent(toLedgerSelect,2,1);
			journalAddGrid.addComponent(amountTextField,3,1);
			journalAddGrid.addComponent(amountTypeSelect,5,1);
			journalAddGrid.addComponent(commentsArea,6,1);
			journalAddGrid.addComponent(addItemButton,7,1);
			journalAddGrid.addComponent(updateItemButton,8,1);

			amountTextField.setStyleName("textfield_align_right");

			journalAddGrid.setComponentAlignment(addItemButton,
					Alignment.MIDDLE_CENTER);
			journalAddGrid.setComponentAlignment(updateItemButton,
					Alignment.MIDDLE_CENTER);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("payment_no")), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(journalNumberList);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 6, 0);
			masterDetailsGrid.addComponent(date, 7, 0);

			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			journalAddGrid.setSpacing(true);

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

			journalEntryTable.setVisibleColumns(reqHead);

			journalEntryTable.setFooterVisible(true);
			journalEntryTable.setColumnFooter(TBL_AMOUNT,
					asString(roundNumber(0)));
			journalEntryTable.setColumnFooter(TBL_TO_LEDGER_NAME, 
					getPropertyName("total"));

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
									"Finance Payment : No. "+journalNumberList.getItemCaption(journalNumberList.getValue()));
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
			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					journalNumberList.setValue((long) 0);
				}
			});
			
			fromLedgerSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (fromLedgerSelect.getValue() != null) {
							fromLedgerSelect.setDescription("<i class='ledger_bal_style'>Current Balance : "+ compDao.getCurrentBalance((Long) fromLedgerSelect.getValue())+"</i>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			toLedgerSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (toLedgerSelect.getValue() != null) {
							toLedgerSelect.setDescription("<i class='ledger_bal_style'>Current Balance : "+ compDao.getCurrentBalance((Long) toLedgerSelect.getValue())+"</i>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			addItemButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(journalEntryTable, null, false);
						if (isAddingValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							journalEntryTable.setVisibleColumns(allHead);

							int id = journalEntryTable.getItemIds().size() + 1;

							double amt = toDouble(amountTextField.getValue());

							journalEntryTable.addItem(
									new Object[] {
											id,
											(Long) fromLedgerSelect.getValue(),
											fromLedgerSelect
													.getItemCaption(fromLedgerSelect
															.getValue()),
											(Long) toLedgerSelect.getValue(),
											toLedgerSelect
													.getItemCaption(toLedgerSelect
															.getValue()),
											amt,
											(Long) amountTypeSelect.getValue(),
											amountTypeSelect
													.getItemCaption(amountTypeSelect
															.getValue()),
											commentsArea.getValue() }, id);

							journalEntryTable.setVisibleColumns(reqHead);

							fromLedgerSelect.setValue(null);
							toLedgerSelect.setValue(null);
							amountTextField.setValue("");
							commentsArea.setValue("");
							fromLedgerSelect.focus();

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			updateItemButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(journalEntryTable, null, false);
						if (isAddingValid()) {

							journalEntryTable.setVisibleColumns(allHead);

							Item item = journalEntryTable.getItem(journalEntryTable.getValue());

							item.getItemProperty(TBL_FROM_LEDGER_ID).setValue(
									fromLedgerSelect.getValue());
							item.getItemProperty(TBL_FROM_LEDGER_NAME)
									.setValue(
											fromLedgerSelect
													.getItemCaption(fromLedgerSelect
															.getValue()));
							item.getItemProperty(TBL_TO_LEDGER_ID).setValue(
									toLedgerSelect.getValue());
							item.getItemProperty(TBL_TO_LEDGER_NAME).setValue(
									toLedgerSelect
											.getItemCaption(toLedgerSelect
													.getValue()));

							item.getItemProperty(TBL_CURRENCY_ID).setValue(
									(Long) amountTypeSelect.getValue());
							item.getItemProperty(TBL_CURRENCY).setValue(
									amountTypeSelect
											.getItemCaption(amountTypeSelect
													.getValue()));
							item.getItemProperty(TBL_AMOUNT).setValue(
									toDouble(amountTextField.getValue()));
							item.getItemProperty(TBL_COMMENTS).setValue(
									commentsArea.getValue());

							journalEntryTable.setVisibleColumns(reqHead);

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);

							journalEntryTable.setValue(null);

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			journalEntryTable.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if (journalEntryTable.getValue() != null) {

						Item item = journalEntryTable.getItem(journalEntryTable.getValue());

						fromLedgerSelect.setValue(item.getItemProperty(
								TBL_FROM_LEDGER_ID).getValue());
						toLedgerSelect.setValue(item.getItemProperty(
								TBL_TO_LEDGER_ID).getValue());
						amountTextField.setValue(asString(item.getItemProperty(
								TBL_AMOUNT).getValue()));
						amountTypeSelect.setValue(item.getItemProperty(
								TBL_CURRENCY_ID).getValue());
						commentsArea.setValue(asString(item.getItemProperty(
								TBL_COMMENTS).getValue()));

						updateItemButton.setVisible(true);
						addItemButton.setVisible(false);

						fromLedgerSelect.focus();
					} else {
						updateItemButton.setVisible(false);
						addItemButton.setVisible(true);
						fromLedgerSelect.setValue(null);
						toLedgerSelect.setValue(null);
						amountTextField.setValue("0");
						commentsArea.setValue("");
						fromLedgerSelect.focus();
					}
				}
			});
			
			saveJournalButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						
						if(isValid()) {
							
							FinancePaymentModel objMdl=new FinancePaymentModel();
							
							objMdl.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setDescription(memoTextArea.getValue());
							objMdl.setPayment_no(getNextSequence("Finance_Payment_No", getLoginID())+"");
							objMdl.setActive(true);
							
							Item item;
							FinancePaymentDetailsModel det=null;
							Iterator it=journalEntryTable.getItemIds().iterator();
							List detList=new ArrayList();
							while (it.hasNext()) {
								item=journalEntryTable.getItem(it.next());
								det=new FinancePaymentDetailsModel();
								det.setComments(asString(item.getItemProperty(TBL_COMMENTS).getValue()));
								det.setAmount((Double)item.getItemProperty(TBL_AMOUNT).getValue());
								det.setCurrency(new CurrencyModel((Long)item.getItemProperty(TBL_CURRENCY_ID).getValue()));
								det.setFrom_account(new FinanceComponentModel((Long)item.getItemProperty(TBL_FROM_LEDGER_ID).getValue()));
								det.setTo_account(new FinanceComponentModel((Long)item.getItemProperty(TBL_TO_LEDGER_ID).getValue()));
								detList.add(det);
							}
							
							objMdl.setFinance_payment_list(detList);
						try{
							dao.save(objMdl);
							loadPayment(objMdl.getId());
							Notification.show(getPropertyName("Success"), getPropertyName("save_success"),Type.WARNING_MESSAGE);
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
						}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			updateJournalButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						
						if(isValid()) {
							
							FinancePaymentModel objMdl=dao.getPaymentModel((Long) journalNumberList.getValue());
							
							objMdl.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setDescription(memoTextArea.getValue());
							objMdl.setActive(true);
							
							Item item;
							FinancePaymentDetailsModel det=null;
							Iterator it=journalEntryTable.getItemIds().iterator();
							List detList=new ArrayList();
							while (it.hasNext()) {
								item=journalEntryTable.getItem(it.next());
								det=new FinancePaymentDetailsModel();
								det.setComments(asString(item.getItemProperty(TBL_COMMENTS).getValue()));
								det.setAmount((Double)item.getItemProperty(TBL_AMOUNT).getValue());
								det.setCurrency(new CurrencyModel((Long)item.getItemProperty(TBL_CURRENCY_ID).getValue()));
								det.setFrom_account(new FinanceComponentModel((Long)item.getItemProperty(TBL_FROM_LEDGER_ID).getValue()));
								det.setTo_account(new FinanceComponentModel((Long)item.getItemProperty(TBL_TO_LEDGER_ID).getValue()));
								detList.add(det);
							}
							objMdl.setFinance_payment_list(detList);
						try{	
							dao.updateJounal(objMdl);
							loadPayment(objMdl.getId());
							Notification.show(getPropertyName("Success"), getPropertyName("update_success"),Type.WARNING_MESSAGE);
						} catch (Exception e) {
							Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
							
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

						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												dao.delete((Long) journalNumberList
														.getValue());
												Notification.show("Success","Deleted Successfully..!",
																Type.WARNING_MESSAGE);
												loadPayment((long)0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification.show("Error", "Unable to delete",
								                        Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			journalNumberList.addValueChangeListener(new ValueChangeListener() {

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

							FinancePaymentModel objModel = dao
									.getPaymentModel((Long) journalNumberList
											.getValue());

							date.setValue(objModel.getDate());
							memoTextArea.setValue(objModel.getDescription());

							journalEntryTable.setVisibleColumns(allHead);

							FinancePaymentDetailsModel det;
							int ct = 0;
							Iterator it = objModel.getFinance_payment_list()
									.iterator();
							while (it.hasNext()) {
								det = (FinancePaymentDetailsModel) it.next();
								ct++;

								journalEntryTable.addItem(
										new Object[] {
												ct,
												det.getFrom_account().getId(),
												det.getFrom_account().getName(),
												det.getTo_account().getId(),
												det.getTo_account().getName(),
												det.getAmount(),
												det.getCurrency().getId(),
												det.getCurrency().getName(),
												det.getComments() }, ct);

							}

							journalEntryTable.setVisibleColumns(reqHead);

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);

							fromLedgerSelect.focus();

						} else {
							memoTextArea.setValue("");
							date.setValue(getWorkingDate());
							updateJournalButton.setVisible(false);
							deleteJournalButton.setVisible(false);
							cancelJournalButton.setVisible(false);
							saveJournalButton.setVisible(true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			cancelJournalButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					if (journalNumberList.getValue() != null
							&& !journalNumberList.getValue().toString()
							.equals("0")) {
						
						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.cancel((Long) journalNumberList
												.getValue());
										Notification.show(getPropertyName("Success"), getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadPayment((long)0);
										
									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
									}
								}
							}
						});
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
			
			
			journalEntryTable.addShortcutListener(new ShortcutListener("Clear entereded and edited data and Add new", ShortcutAction.KeyCode.ESCAPE, null) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	journalEntryTable.setValue(null);
		        }
		    });
			
			panel.addShortcutListener(new ShortcutListener("Add New Row", ShortcutAction.KeyCode.N, new int[] {
                    ShortcutAction.ModifierKey.ALT}) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	loadPayment((long)0);
		        }
		    });
			
			journalEntryTable.addShortcutListener(new ShortcutListener("Submit Item", ShortcutAction.KeyCode.ENTER, null) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	if(addItemButton.isVisible())
		        		addItemButton.click();
		        	else
		        		updateItemButton.click();
		        }
		    });
			
			

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(stkrkVLay);

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			panel.setContent(windowNotif);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return panel;
	}

	protected void loadPayment(long id) {
			List list;
			try {
				list=new ArrayList();
				list.add(0,new FinancePaymentModel(0, "----Create New-----"));
				list.addAll(dao.getAllPaymentNumber(getOfficeID()));
				
				SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			    journalNumberList.setContainerDataSource(bic);
			    journalNumberList.setItemCaptionPropertyId("payment_no");
			    journalNumberList.setInputPrompt("----Create New-----");
			
			    journalNumberList.setValue(id);
			
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	public void calculateTotals() {
		try {

			double amtttl = 0;
			Item item;
			Iterator it = journalEntryTable.getItemIds().iterator();
			while (it.hasNext()) {
				item = journalEntryTable.getItem(it.next());

				amtttl += (Double) item.getItemProperty(TBL_AMOUNT).getValue();
			}

			journalEntryTable.setColumnFooter(TBL_AMOUNT,
					asString(roundNumber(amtttl)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean isAddingValid() {
		boolean ret = true;

		if (amountTextField.getValue() == null
				|| amountTextField.getValue().equals("")) {
			setRequiredError(amountTextField, getPropertyName("invalid_data"), true);
			amountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(amountTextField.getValue()) <= 0) {
					setRequiredError(amountTextField, getPropertyName("invalid_data"),
							true);
					amountTextField.focus();
					ret = false;
				} else
					setRequiredError(amountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(amountTextField, getPropertyName("invalid_data"), true);
				amountTextField.focus();
				ret = false;
			}
		}

		if (toLedgerSelect.getValue() == null
				|| toLedgerSelect.getValue().equals("")) {
			setRequiredError(toLedgerSelect, getPropertyName("invalid_selection"), true);
			toLedgerSelect.focus();
			ret = false;
		} else
			setRequiredError(toLedgerSelect, null, false);

		if (fromLedgerSelect.getValue() == null
				|| fromLedgerSelect.getValue().equals("")) {
			setRequiredError(fromLedgerSelect, getPropertyName("invalid_selection"), true);
			fromLedgerSelect.focus();
			ret = false;
		} else
			setRequiredError(fromLedgerSelect, null, false);

		if (amountTypeSelect.getValue() == null
				|| amountTypeSelect.getValue().equals("")) {
			setRequiredError(amountTypeSelect, getPropertyName("invalid_selection"), true);
			amountTypeSelect.focus();
			ret = false;
		} else
			setRequiredError(amountTypeSelect, null, false);
		
		if(fromLedgerSelect.getValue() != null&&toLedgerSelect.getValue() != null){
		if (fromLedgerSelect.getValue().toString().equals(toLedgerSelect.getValue().toString())) {
			setRequiredError(fromLedgerSelect, getPropertyName("invalid_selection"), true);
			fromLedgerSelect.focus();
			ret = false;
		} else
			setRequiredError(fromLedgerSelect, null, false);
		}
		return ret;
	}

	public void deleteItem() {
		try {

			if (journalEntryTable.getValue() != null) {

					journalEntryTable.removeItem(journalEntryTable.getValue());

				Item newitem;
				int SN = 0;
				Iterator it = journalEntryTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = journalEntryTable.getItem((Integer) it.next());

					newitem.getItemProperty(TBL_SN).setValue(SN);

				}
			}
			journalEntryTable.focus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		boolean ret=true;
		
		if(journalEntryTable.getItemIds().size()<=0){
			setRequiredError( journalEntryTable, 
					getPropertyName("invalid_data"),true);
			fromLedgerSelect.focus();
			ret=false;
		}
		else
			setRequiredError( journalEntryTable, null,false);
		
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	public SComboField getJournalNumberList() {
		return journalNumberList;
	}

	public void setJournalNumberList(SComboField journalNumberList) {
		this.journalNumberList = journalNumberList;
	}

	@Override
	public SComboField getBillNoFiled() {
		return journalNumberList;
	}

}
