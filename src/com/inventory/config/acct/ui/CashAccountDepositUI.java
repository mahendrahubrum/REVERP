package com.inventory.config.acct.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.CashAccountDepositDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CashAccountDepositDetailsModel;
import com.inventory.config.acct.model.CashAccountDepositModel;
import com.inventory.config.acct.model.DebitCreditInvoiceMapModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.sales.bean.PaymentInvoiceBean;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.PaymentInvoiceMapModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.PaymentInvoicePanel;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SSelectionField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.PaymentInvoiceDao;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author anil
 * @date 12-Aug-2015
 * @Project REVERP
 * 
 */

public class CashAccountDepositUI extends SparkLogic {

	private static final long serialVersionUID = 173745310353492562L;

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TBC_EXPENSE_TYPE = "Expense";
	static String TBC_LEDGER_ID = "Account ID";
	static String TBC_LEDGER_NAME = "Account";
	static String TBC_BUTTON_VISIBLE = "Button Visible";
	static String TBC_BILL_NO = "Bill Nos";
	static String TBC_AMOUNT = "Amount";
	static String TBC_CURRENCY_ID = "Currency ID";
	static String TBC_CURRENCY = "Currency";
	static String TBC_CONVERSION_RATE = "Conversion Rate";
	static String TBC_DEPARTMENT_ID = "Department ID";
	static String TBC_DEPARTMENT = "Department";
	static String TBC_DIVISION_ID = "Division ID";
	static String TBC_DIVISION = "Division";
	static String TBC_FROM_DATE = "From Date";
	static String TBC_TO_DATE = "To Date";

	STable table;
	PaymentInvoicePanel invoicePanel;
	CashAccountDepositDao dao;
	
	PaymentInvoiceDao paymentDao;

	SGridLayout masterDetailsGrid;
	SGridLayout accountDepositAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField paymentCombo;
	
	SComboField departmentCombo;
	SSelectionField divisionCombo;

	SComboField accountHeadFilter;

	SComboField ledgerCombo;


	SComboField cashAccountSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField billNoField;
	SCurrencyField amountField;
	SCurrencyField convertedField;
	SDateField dateField;

//	DocumentAttach docAttach;

	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;
	SButton printButton;

	STextField refNoTextField;
	STextArea memoTextArea;

	SDateField fromDateField;
	SDateField toDateField;
	
	
	LedgerDao ledgerDao = new LedgerDao();

	SButton createNewButton;

	SettingsValuePojo settings;
	WrappedSession session;

	SRadioButton accountTypeRadio;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	private DepartmentDao depDao;
	private DivisionDao divDao;

	SButton selectSalesButton;
	Date previousDate;
	@SuppressWarnings("rawtypes")
	List invoiceBillList;
	List accClassList;
	
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		invoiceBillList=new ArrayList();
		previousDate=new Date();
		previousDate=getWorkingDate();
		paymentDao=new PaymentInvoiceDao();
		invoicePanel=new PaymentInvoicePanel(null);
		
		fromDateField=new SDateField(null, 100, getDateFormat(), getMonthStartDate());
		toDateField=new SDateField(null, 100, getDateFormat(), getWorkingDate());
		fromDateField.setVisible(false);
		toDateField.setVisible(false);
		
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		

		accountTypeRadio = new SRadioButton(getPropertyName("payment_type"), 200, SConstants.cash_account.cashCustomerList,"key", "value");
		accountTypeRadio.setHorizontal(true);
		
		selectSalesButton=new SButton(null,"Select Purchase");
		selectSalesButton.setPrimaryStyleName("addBtnStyle");
		
		allHeaders=new Object[]{TSR_SN, TBC_EXPENSE_TYPE, TBC_LEDGER_ID, TBC_LEDGER_NAME, TBC_BUTTON_VISIBLE, TBC_BILL_NO, TBC_AMOUNT, 
				TBC_CURRENCY_ID, TBC_CURRENCY, TBC_CONVERSION_RATE , 
				TBC_DEPARTMENT_ID , TBC_DEPARTMENT , TBC_DIVISION_ID, TBC_DIVISION, TBC_FROM_DATE, TBC_TO_DATE};
		
		requiredHeaders=new Object[]{TSR_SN, TBC_LEDGER_NAME, TBC_AMOUNT, TBC_CURRENCY, TBC_DEPARTMENT , TBC_DIVISION,  };
		
		List<Object> templist = new ArrayList<Object>();
		Collections.addAll(templist, requiredHeaders);
		
		if(!settings.isDEPARTMENT_ENABLED()){
			templist.remove(TBC_DEPARTMENT);
		}
		if(!settings.isDIVISION_ENABLED()){
			templist.remove(TBC_DIVISION);
		}
					
		requiredHeaders = templist.toArray(new String[templist.size()]);
		
//		docAttach = new DocumentAttach(SConstants.documentAttach.CHEQUE);
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		dao = new CashAccountDepositDao();
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

		printButton = new SButton(getPropertyName("print"), 78);
		printButton.setIcon(new ThemeResource("icons/print.png"));
		printButton.setStyleName("deletebtnStyle");
		
		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveButton);
		mainButtonLayout.addComponent(updateButton);

		if (settings.isKEEP_DELETED_DATA())
			mainButtonLayout.addComponent(cancelButton);
		else
			mainButtonLayout.addComponent(deleteButton);

		mainButtonLayout.addComponent(printButton);
		
		updateButton.setVisible(false);
		deleteButton.setVisible(false);
		cancelButton.setVisible(false);
		printButton.setVisible(false);

		dateField = new SDateField(null, 120, getDateFormat());
		refNoTextField = new STextField();
		memoTextArea = new STextArea(getPropertyName("description"), 810, 40);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(1000, 590);
		try {
			
			depDao=new DepartmentDao();
			divDao=new DivisionDao();

			updateItemButton.setVisible(false);

			table = new STable(null, 800, 200);

			paymentCombo = new SComboField(null, 200, null, "id",
					"ref_no", true, getPropertyName("create_new"));

			cashAccountSelect = new SComboField(null, 200,
												new LedgerDao().
												getAllActiveLedgerNamesOfGroup(getOfficeID(),
																				SConstants.LEDGER_ADDED_DIRECTLY,  
																				settings.getCASH_GROUP()),
									
																				"id","name",false,"Select");
			accClassList=new ArrayList();
			accClassList.add(SConstants.account_parent_groups.INCOME);
			accClassList.add(SConstants.account_parent_groups.LIABILITY);
			ledgerCombo = new SComboField(getPropertyName("to_account"),
					150,
					ledgerDao.getAllActiveLedgersUnderClasses(getOfficeID(), accClassList), "id", "name", true,
							getPropertyName("select"));

			accountDepositAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();
			
			List departmentList=new ArrayList();
			departmentList.add(0, new DepartmentModel(0, "None"));
			departmentList.addAll(new DepartmentDao().getDepartments(getOrganizationID()));
			departmentCombo = new SComboField(getPropertyName("Department"), 100, departmentList,"id", "name", false, getPropertyName("select"));
			divisionCombo = new SSelectionField(getPropertyName("Division"),getPropertyName("none"),200, 400);
			divisionCombo.setContainerData(new DivisionDao().getDivisionsHierarchy(getOrganizationID()));
			departmentCombo.setValue((long)0);
			
			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			table.setSizeFull();
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setWidth("900px");
			table.setHeight("180px");
			
			table.addContainerProperty(TSR_SN,Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TBC_EXPENSE_TYPE,Long.class, null, TBC_EXPENSE_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBC_LEDGER_ID,Long.class, null, TBC_LEDGER_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_LEDGER_NAME,String.class, null, getPropertyName("account"), null,Align.LEFT);
			table.addContainerProperty(TBC_BUTTON_VISIBLE,Boolean.class, null, TBC_BUTTON_VISIBLE, null,Align.LEFT);
			table.addContainerProperty(TBC_BILL_NO,String.class, null, TBC_BILL_NO, null,Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT,Double.class, null, getPropertyName("amount"), null,Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY_ID,Long.class, null, TBC_CURRENCY_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY,String.class, null, getPropertyName("currency"), null,Align.LEFT);
			table.addContainerProperty(TBC_CONVERSION_RATE,Double.class, null, getPropertyName("conversion_rate"), null,Align.CENTER);
			table.addContainerProperty(TBC_DEPARTMENT_ID,Long.class, null, TBC_DEPARTMENT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DEPARTMENT,String.class, null, getPropertyName("department"), null,Align.LEFT);
			table.addContainerProperty(TBC_DIVISION_ID,Long.class, null, TBC_DIVISION_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DIVISION,String.class, null, getPropertyName("division"), null,Align.LEFT);
			table.addContainerProperty(TBC_FROM_DATE,Date.class, null, TBC_FROM_DATE, null,Align.LEFT);
			table.addContainerProperty(TBC_TO_DATE,Date.class, null, TBC_TO_DATE, null,Align.LEFT);
			
			
			table.setColumnExpandRatio(TSR_SN, (float) .5);
			table.setColumnExpandRatio(TBC_LEDGER_NAME,2);
			table.setColumnExpandRatio(TBC_AMOUNT,(float) 1.5);
			table.setVisibleColumns(requiredHeaders);

			accountDepositAddGrid.setColumns(12);
			accountDepositAddGrid.setRows(2);

			billNoField=new STextField(null, 75);
			billNoField.setVisible(false);
			billNoField.setValue("");
			amountField = new SCurrencyField(getPropertyName("amount"), 80,getWorkingDate());
			convertedField = new SCurrencyField(null, 75,getWorkingDate());

			SHorizontalLayout expenseLayout=new SHorizontalLayout();
			expenseLayout.addComponent(ledgerCombo);
			expenseLayout.addComponent(selectSalesButton);
			
			selectSalesButton.setVisible(false);
			expenseLayout.setComponentAlignment(selectSalesButton, Alignment.BOTTOM_CENTER);
			
			accountDepositAddGrid.addComponent(accountTypeRadio);
			
			accountDepositAddGrid.addComponent(expenseLayout);
			accountDepositAddGrid.addComponent(billNoField);
			accountDepositAddGrid.addComponent(amountField);
			accountDepositAddGrid.addComponent(convertedField);
			accountDepositAddGrid.setComponentAlignment(convertedField, Alignment.BOTTOM_CENTER);
			convertedField.currencySelect.setReadOnly(true);
			convertedField.amountField.setReadOnly(true);
			convertedField.setVisible(false);
			
			if(settings.isDEPARTMENT_ENABLED())
				accountDepositAddGrid.addComponent(departmentCombo);
			if(settings.isDIVISION_ENABLED())
				accountDepositAddGrid.addComponent(divisionCombo);
			accountDepositAddGrid.addComponent(fromDateField);
			accountDepositAddGrid.addComponent(toDateField);
			accountDepositAddGrid.addComponent(addItemButton);
			accountDepositAddGrid.addComponent(updateItemButton);

			accountDepositAddGrid.setStyleName("bankacctdeposit_adding_grid");

			accountDepositAddGrid.setComponentAlignment(addItemButton,Alignment.BOTTOM_RIGHT);
			accountDepositAddGrid.setComponentAlignment(updateItemButton, Alignment.BOTTOM_RIGHT);

			accountDepositAddGrid.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("payment_id"), 30), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(paymentCombo);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(dateField, 8, 0);

			// masterDetailsGrid.addComponent(new SLabel("Pay To :"), 3, 1);
			// masterDetailsGrid.addComponent(, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("cash_account")),
					1, 1);
			masterDetailsGrid.addComponent(cashAccountSelect, 2, 1);

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(refNoTextField, 4, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);


			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(table);

			stkrkVLay.addComponent(new SHorizontalLayout(true,
					accountDepositAddGrid/*, docAttach*/));

			SFormLayout fm = new SFormLayout();
			fm.addComponent(memoTextArea);
			stkrkVLay.addComponent(fm);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_AMOUNT,
					asString(roundNumber(0)));
			table.setColumnFooter(TBC_LEDGER_NAME,
					getPropertyName("total"));

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(stkrkVLay);

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			panel.setContent(windowNotif);

			loadData(0);
			
			
			/*addShortcutListener(new ShortcutListener("Add New Purchase",
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
			});*/

			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(dateField.getValue()!=null){
							if(previousDate.getTime()!=dateField.getValue().getTime()){
								final long id=(Long)amountField.currencySelect.getValue();
								if((Long)amountField.currencySelect.getValue()!=getCurrencyID()){
									ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Update Currency Rate Accordingly.",new ConfirmDialog.Listener() {
										public void onClose(ConfirmDialog dialog) {
											if (!dialog.isConfirmed()) {
												dateField.setValue(previousDate);
											}
											previousDate=dateField.getValue();
											amountField.setCurrencyDate(previousDate);
											amountField.currencySelect.setNewValue(null);
											amountField.currencySelect.setNewValue(id);
										}
									});
								}
								amountField.currencySelect.setNewValue(null);
								amountField.currencySelect.setNewValue(id);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			amountField.currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(amountField.currencySelect.getValue()!=null){
						if((Long)amountField.currencySelect.getValue()!=getCurrencyID()){
							convertedField.setVisible(true);
						}
						else{
							convertedField.setVisible(false);
						}
						convertedField.setNewValue(roundNumber(amountField.getValue()/amountField.getConversionRate()));
					}
				}
			});
			
			
			amountField.amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					convertedField.setNewValue(roundNumber(amountField.getValue()/amountField.getConversionRate()));	
				}
			});
			amountField.setNewValue(getCurrencyID(), 0);
			
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)paymentCombo.getValue(),confirmBox.getUserID());
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					confirmBox.close();
				}
			};
			
			
			confirmBox.setClickListener(confirmListener);
			
			
			ClickListener clickListnr=new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
						if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)paymentCombo.getValue(),
									"Bank Account Deposit : No. "+paymentCombo.getItemCaption(paymentCombo.getValue()));
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
						if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0")) {
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
							e.printStackTrace();
						}
						
					}
				}
			};
			
			
			windowNotif.setClickListener(clickListnr);

			
			accountTypeRadio.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent arg0) {
					try {
						SCollectionContainer bic;
						List mainList=new ArrayList();
						amountField.currencySelect.setReadOnly(false);
						amountField.setCurrency(getCurrencyID());
						if ((Long) accountTypeRadio.getValue() == SConstants.cash_account.INCOME){
							mainList=ledgerDao.getAllActiveLedgersUnderClasses(getOfficeID(), accClassList);
							
							bic=SCollectionContainer.setList(mainList, "id");
							ledgerCombo.setContainerDataSource(bic);
							ledgerCombo.setItemCaptionPropertyId("name");
						}
						else{
							mainList=new LedgerDao().getAllCustomers(getOfficeID());
							
							bic=SCollectionContainer.setList(mainList, "id");
							ledgerCombo.setContainerDataSource(bic);
							ledgerCombo.setItemCaptionPropertyId("name");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			accountTypeRadio.setValue(SConstants.cash_account.INCOME);

			
			cashAccountSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (cashAccountSelect.getValue() != null) {
							cashAccountSelect.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ ledgerDao
											.getLedgerCurrentBalance((Long) cashAccountSelect
													.getValue()) + "</i>");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			ledgerCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (ledgerCombo.getValue() != null) {
							ledgerCombo.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ ledgerDao
											.getLedgerCurrentBalance((Long) ledgerCombo
													.getValue()) + "</i>");
							if ((Long) accountTypeRadio.getValue() == SConstants.cash_account.CUSTOMER) {
								
								if(paymentDao.isPaymentPendingForCustomer((Long) ledgerCombo.getValue())>0){
									selectSalesButton.setVisible(true);
									
									HashSet<Long> billNoSet = new HashSet<Long>();
									String billNos=billNoField.getValue().toString().trim();
									if(billNos.length()>1){
										List list=new ArrayList();
										list=Arrays.asList(billNos.split(","));
										Iterator itr=list.iterator();
										while (itr.hasNext()) {
											long id = Long.parseLong(itr.next().toString().trim());
											if(id!=0)
												billNoSet.add(id);
										}
									}
									boolean isCreateNew=true;
									if(paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {
										isCreateNew=false;
									}
									invoicePanel.loadLedgers((Long) ledgerCombo.getValue(), 
															SConstants.SALES, 
															fromDateField.getValue(),
															toDateField.getValue(),
															billNoSet,isCreateNew);
									invoicePanel.netAmountField.rateButton.setVisible(false);
//									getUI().addWindow(invoicePanel);
									
									selectSalesButton.click();
									
								}
								else
									selectSalesButton.setVisible(false);
							}
							else
								selectSalesButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			selectSalesButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if ((Long) accountTypeRadio.getValue() == SConstants.cash_account.CUSTOMER) {
						
							if (ledgerCombo.getValue() != null){
								if(paymentDao.isPaymentPendingForCustomer((Long) ledgerCombo.getValue())>0){
									
									HashSet<Long> billNoSet = new HashSet<Long>();
									String billNos=billNoField.getValue().toString().trim();
									if(billNos.length()>1){
										List list=new ArrayList();
										list=Arrays.asList(billNos.split(","));
										Iterator itr=list.iterator();
										while (itr.hasNext()) {
											long id = Long.parseLong(itr.next().toString().trim());
											if(id!=0)
												billNoSet.add(id);
										}
									}
									selectSalesButton.setVisible(true);
									boolean isCreateNew=true;
									if(paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {
										isCreateNew=false;
									}
									invoicePanel.loadLedgers((Long) ledgerCombo.getValue(), 
															SConstants.SALES, 
															fromDateField.getValue(),
															toDateField.getValue(),
															billNoSet,isCreateNew);
									invoicePanel.netAmountField.rateButton.setVisible(false);
									getUI().addWindow(invoicePanel);
								}
								else
									selectSalesButton.setVisible(false);
							}
						}
						else
							selectSalesButton.setVisible(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			invoicePanel.addCloseListener(new CloseListener() {
				
				@Override
				public void windowClose(CloseEvent e) {
					double amount=0;
					try {
						amount=invoicePanel.netAmountField.getValue();
						billNoField.setValue(invoicePanel.getBillNos());
						if(amount>0)
							amountField.currencySelect.setReadOnly(true);
						amountField.setNewValue(invoicePanel.netAmountField.getCurrency(),roundNumber(amount));
						fromDateField.setValue(invoicePanel.getFromDate());
						toDateField.setValue(invoicePanel.getToDate());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			
			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					paymentCombo.setValue((long) 0);
					paymentCombo.setValue(null);
				}
			});

			
			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(table, null, false);
						
						if (isAddingValid()) {
							table.setVisibleColumns(allHeaders);
							long departmentId=0;
							long divisionId=0;
							String department="";
							String division="";
							
							department=departmentCombo.getItemCaption(departmentCombo.getValue());
							departmentId=(Long)departmentCombo.getValue();
							division=divisionCombo.getItemCaption();
							divisionId=(Long)divisionCombo.getValue();
							
							boolean isAddable=true;
							List billList=new ArrayList();
							if(billNoField.getValue().toString().trim().length()>0){
								billList=Arrays.asList(billNoField.getValue().toString().trim().split(","));
								Iterator it=billList.iterator();
								while (it.hasNext()) {
									long pid = Long.parseLong(it.next().toString().trim());
									if(pid!=0){
										if(invoiceBillList.contains((Long)pid)){
											isAddable=false;
										}
									}
								}
							}
							
							if(isAddable){
								table.addItem(new Object[] {
										table.getItemIds().size()+1,
										(Long)accountTypeRadio.getValue(),
										(Long) ledgerCombo.getValue(),
										ledgerCombo.getItemCaption(ledgerCombo.getValue()),
										selectSalesButton.isVisible(),
										billNoField.getValue().toString().trim(),
										roundNumber(amountField.getValue()),
										amountField.getCurrency(),
										new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getCode(),
										roundNumber(amountField.getConversionRate()),
										departmentId,
										department,
										divisionId,
										division,
										fromDateField.getValue(),
										toDateField.getValue()},table.getItemIds().size()+1);
								
								if(billNoField.getValue().toString().trim().length()>0){
									billList=Arrays.asList(billNoField.getValue().toString().trim().split(","));
									Iterator it=billList.iterator();
									while (it.hasNext()) {
										long pid = Long.parseLong(it.next().toString().trim());
										if(pid!=0){
											if(!invoiceBillList.contains((Long)pid)){
												invoiceBillList.add(pid);
											}
										}
									}
								}
							}
							else
								SNotification.show("Bill Selected Earlier", Type.ERROR_MESSAGE);
							table.setValue(null);
							table.setVisibleColumns(requiredHeaders);
							resetItems();
							calculateTotals();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if (table.getValue() != null) {

						Item item = table.getItem(table.getValue());
						accountTypeRadio.setValue((Long)item.getItemProperty(TBC_EXPENSE_TYPE).getValue());
						selectSalesButton.setVisible((Boolean)item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
						billNoField.setValue(item.getItemProperty(TBC_BILL_NO).getValue().toString());
						fromDateField.setValue((Date)item.getItemProperty(TBC_FROM_DATE).getValue());
						toDateField.setValue((Date)item.getItemProperty(TBC_TO_DATE).getValue());
						amountField.setNewValue((Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(), 
								(Double)item.getItemProperty(TBC_AMOUNT).getValue());
						ledgerCombo.setValue((Long)item.getItemProperty(TBC_LEDGER_ID).getValue());
						departmentCombo.setValue(item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
						divisionCombo.setValue(item.getItemProperty(TBC_DIVISION_ID).getValue());
						if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0)
							amountField.currencySelect.setReadOnly(true);
						else
							amountField.currencySelect.setReadOnly(false);
						updateItemButton.setVisible(true);
						addItemButton.setVisible(false);
					} else {
						resetItems();
					}
				}
			});

			
			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(table, null, false);
						if (isAddingValid()) {
							boolean isAddable=true;
							List billList=new ArrayList();
							table.setVisibleColumns(allHeaders);
							Item item = table.getItem(table.getValue());
							
							if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
								billList=Arrays.asList(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().split(","));
								Iterator it=billList.iterator();
								while (it.hasNext()) {
									long pid = Long.parseLong(it.next().toString().trim());
									if(pid!=0){
										if(invoiceBillList.contains(pid)){
											invoiceBillList.remove(pid);
										}
									}
								}
							}
							
							if(billNoField.getValue().toString().trim().length()>0){
								billList=Arrays.asList(billNoField.getValue().toString().trim().split(","));
								Iterator it=billList.iterator();
								while (it.hasNext()) {
									long pid = Long.parseLong(it.next().toString().trim());
									if(pid!=0){
										if(invoiceBillList.contains((Long)pid)){
											isAddable=false;
										}
									}
								}
							}
							
							long departmentId=0;
							long divisionId=0;
							String department="";
							String division="";
							
							department=departmentCombo.getItemCaption(departmentCombo.getValue());
							departmentId=(Long)departmentCombo.getValue();
							division=divisionCombo.getItemCaption();
							divisionId=(Long)divisionCombo.getValue();
							
							if(isAddable){
								item.getItemProperty(TBC_EXPENSE_TYPE).setValue((Long)accountTypeRadio.getValue());
								item.getItemProperty(TBC_LEDGER_ID).setValue((Long)ledgerCombo.getValue());
								item.getItemProperty(TBC_LEDGER_NAME).setValue(ledgerCombo.getItemCaption(ledgerCombo.getValue()));
								item.getItemProperty(TBC_BUTTON_VISIBLE).setValue(selectSalesButton.isVisible());
								item.getItemProperty(TBC_BILL_NO).setValue(billNoField.getValue().toString().trim());
								item.getItemProperty(TBC_AMOUNT).setValue(roundNumber(amountField.getValue()));
								item.getItemProperty(TBC_CURRENCY_ID).setValue(amountField.getCurrency());
								item.getItemProperty(TBC_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getCode());
								item.getItemProperty(TBC_CONVERSION_RATE).setValue(roundNumber(amountField.getConversionRate()));
								item.getItemProperty(TBC_DEPARTMENT_ID).setValue(departmentId);
								item.getItemProperty(TBC_DEPARTMENT).setValue(department);
								item.getItemProperty(TBC_DIVISION_ID).setValue(divisionId);
								item.getItemProperty(TBC_DIVISION).setValue(division);
								item.getItemProperty(TBC_FROM_DATE).setValue(fromDateField.getValue());
								item.getItemProperty(TBC_TO_DATE).setValue(toDateField.getValue());
								
								if(billNoField.getValue().toString().trim().length()>0){
									billList=Arrays.asList(billNoField.getValue().toString().trim().split(","));
									Iterator it=billList.iterator();
									while (it.hasNext()) {
										long pid = Long.parseLong(it.next().toString().trim());
										if(pid!=0){
											if(!invoiceBillList.contains((Long)pid)){
												invoiceBillList.add(pid);
											}
										}
									}
								}
							}
							else
								SNotification.show("Bill Selected Earlier", Type.ERROR_MESSAGE);
							
							table.setValue(null);
							resetItems();	
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							if (paymentCombo.getValue() == null || paymentCombo.getValue().toString().equals("0")) {
								CashAccountDepositModel mdl = new CashAccountDepositModel();
								List<CashAccountDepositDetailsModel> childList=new ArrayList<CashAccountDepositDetailsModel>();
								List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
								
								mdl.setCashAccount(new LedgerModel((Long)cashAccountSelect.getValue()));
								mdl.setRef_no(refNoTextField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								mdl.setBill_no(getNextSequence("Cash Account Deposit", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								mdl.setActive(true);
								
								FinTransaction tran = new FinTransaction();
								Iterator itr = table.getItemIds().iterator();							

								while (itr.hasNext()) {
									boolean isBaseCurrency=true;
									Item item = table.getItem(itr.next());
									CashAccountDepositDetailsModel det=new CashAccountDepositDetailsModel();
									
									det.setPaymentType((Long) item.getItemProperty(TBC_EXPENSE_TYPE).getValue());
									det.setAccount(new LedgerModel((Long) item.getItemProperty(TBC_LEDGER_ID).getValue()));
									det.setButtonVisible((Boolean) item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
									det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
									det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
									det.setCurrencyId((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue());
									if((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()!=getCurrencyID())
										isBaseCurrency=false;
									det.setConversionRate((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
									det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setFromDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_FROM_DATE).getValue()));
									det.setToDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_TO_DATE).getValue()));
									childList.add(det);
									
									if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
										double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
										double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
										List billList=new ArrayList();
										billList=Arrays.asList(det.getBill_no().split(","));
										Iterator it=billList.iterator();
										while (it.hasNext()) {
											long pid = Long.parseLong(it.next().toString().trim());
											double actualPaidAmount=0;
											double actualAmount=0;
											double actual_balance_to_pay=0;
											double actualPayingAmount=0;
											
											double totalPayed=0;
											double totalAmount=0;
											double payingAmount=0;
											double balance_to_pay=0;
											
											if(paymentAmount<=0)
												break;
											if(pid!=0) {
												SalesModel pmdl=new SalesDao().getSale(pid);
												List paymentList=new ArrayList();
												
												paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.SALES, pid);
												if(paymentList.size()>0){
													Iterator payItr=paymentList.iterator();
													while (payItr.hasNext()) {
														PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
														if(mapMdl.getPaymentId()!=mdl.getId()){
															if(isBaseCurrency){
																totalPayed+=roundNumber(mapMdl.getAmount());
															}
															else{
																totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																actualPaidAmount+=mapMdl.getAmount();
															}
														}
													}
												}
												
												paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.CUSTOMER);
												if(paymentList.size()>0){
													Iterator payItr=paymentList.iterator();
													while (payItr.hasNext()) {
														DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
														if(mapMdl.getPaymentId()!=mdl.getId()){
															if(isBaseCurrency){
																totalAmount+=roundNumber(mapMdl.getAmount());
															}
															else{
																totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																actualAmount+=mapMdl.getAmount();
															}
														}
													}
												}
												
												paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.CUSTOMER);
												if(paymentList.size()>0){
													Iterator payItr=paymentList.iterator();
													while (payItr.hasNext()) {
														DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
														if(mapMdl.getPaymentId()!=mdl.getId()){
															if(isBaseCurrency){
																totalPayed+=roundNumber(mapMdl.getAmount());
															}
															else{
																totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																actualPaidAmount+=mapMdl.getAmount();
															}
														}
													}
												}
												
												if(isBaseCurrency){
													totalAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
													totalPayed+=roundNumber(pmdl.getPayment_amount());
												}
												else{
													totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate())-(pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
													actualAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
													totalPayed+=roundNumber(pmdl.getPayment_amount()/pmdl.getConversionRate());
													actualPaidAmount+=roundNumber(pmdl.getPayment_amount());
												}
												
												balance_to_pay=totalAmount-totalPayed;
												actual_balance_to_pay=actualAmount-actualPaidAmount;
												
												PaymentInvoiceBean bean;
												if(isBaseCurrency) {
													if(paymentAmount>=balance_to_pay){
														payingAmount=balance_to_pay;
														paymentAmount-=balance_to_pay;
													}
													else{
														payingAmount=paymentAmount;
														paymentAmount=0;
													}
													bean=new PaymentInvoiceBean(
																		SConstants.SALES,
																		pid,
																		mdl.getId(),
																		getOfficeID(),
																		(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		roundNumber(payingAmount),
																		roundNumber(paymentConversionRate));
													invoiceMapList.add(bean);
													tran.addTransaction(SConstants.CR, 
																		(Long) item.getItemProperty(TBC_LEDGER_ID).getValue(),
																		(Long) cashAccountSelect.getValue(),
																		roundNumber(payingAmount),
																		"",
																		(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																		,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																		(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
												}
												else{
													boolean isSavable=false;
													if(paymentAmount>=actual_balance_to_pay){
														isSavable=true;
														actualPayingAmount=actual_balance_to_pay;
														paymentAmount-=actual_balance_to_pay;
													}
													else{
														actualPayingAmount=paymentAmount;
														paymentAmount=0;
													}
													
													double actualBaseCurrency=0;
													actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
													double differenceAmount=0;
													differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
													
													bean=new PaymentInvoiceBean(
																		SConstants.SALES,
																		pid,
																		mdl.getId(),
																		getOfficeID(),
																		(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		roundNumber(actualPayingAmount),
																		roundNumber(paymentConversionRate));
													if(isSavable){
														if(differenceAmount>0){
															tran.addTransaction(SConstants.CR, 
																				settings.getFOREX_DIFFERENCE_ACCOUNT(),
																				(Long) cashAccountSelect.getValue(),
																				roundNumber(differenceAmount),
																				"",
																				(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																				(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																				(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
															actualBaseCurrency-=differenceAmount;
														}
													}
													
													tran.addTransaction(SConstants.CR, 
																(Long)item.getItemProperty(TBC_LEDGER_ID).getValue(),
																(Long) cashAccountSelect.getValue(),
																roundNumber(actualBaseCurrency),
																"",
																(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
													invoiceMapList.add(bean);
												}
											}
										}
										if(paymentAmount>0){
											tran.addTransaction(SConstants.CR, 
													(Long) item.getItemProperty(TBC_LEDGER_ID).getValue(),
													(Long) cashAccountSelect.getValue(),
													roundNumber(paymentAmount/paymentConversionRate),
													"",
													(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
													(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
													(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
										}
									}
									else{
										tran.addTransaction(SConstants.CR, 
												(Long) item.getItemProperty(TBC_LEDGER_ID).getValue(),
												(Long) cashAccountSelect.getValue(),
												roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue() /
															(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
												"",
												(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
												(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
												(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									}
								}
								mdl.setCash_account_deposit_list(childList);


								long id = dao.save(mdl,
												tran.getTransaction(SConstants.CASH_ACCOUNT_DEPOSITS,
																	CommonUtil.getSQLDateFromUtilDate(dateField.getValue())),
												invoiceMapList);
								

								saveActivity(getOptionId(), "Cash Account Deposit Saved. Bill No : "
												+ id+ ", Cash Acct. : "+ cashAccountSelect.getItemCaption(cashAccountSelect.getValue())
												+ ", Payment Amount : "
												+ table.getColumnFooter(TBC_AMOUNT).toString(),id);
								Notification.show("Success","Saved Successfully..!",Type.WARNING_MESSAGE);
								loadData(id);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			paymentCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						table.removeAllItems();
						invoiceBillList.clear();
						cashAccountSelect.setValue(null);
						refNoTextField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						amountField.setCurrencyDate(getWorkingDate());
						amountField.setNewValue(getCurrencyID(),0);
						memoTextArea.setValue("");
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						printButton.setVisible(false);
						resetItems();
						table.setVisibleColumns(allHeaders);
						if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {

							CashAccountDepositModel objModel = dao.getCashAccountDepositModel((Long) paymentCombo.getValue());
							previousDate=objModel.getDate();
							dateField.setValue(objModel.getDate());
							amountField.setCurrencyDate(objModel.getDate());
							memoTextArea.setValue(objModel.getMemo());
							refNoTextField.setValue(objModel.getRef_no());
							cashAccountSelect.setValue(objModel.getCashAccount().getId());
							

							Iterator it = objModel.getCash_account_deposit_list().iterator();
							while (it.hasNext()) {
								CashAccountDepositDetailsModel det = (CashAccountDepositDetailsModel) it.next();

								String department="";
								String division="";
								
								if(det.getDepartmentId()!=0)
									department=depDao.getDepartmentName(det.getDepartmentId());
								else
									department="None";
								if(det.getDivisionId()!=0)
									division=divDao.getDivisionName(det.getDivisionId());
								else
									division="None";
								
								table.addItem(
										new Object[] {
												table.getItemIds().size()+1,
												det.getPaymentType(),
												det.getAccount().getId(),
												det.getAccount().getName(),
												det.isButtonVisible(),
												det.getBill_no(),
												roundNumber(det.getAmount()),
												det.getCurrencyId(),
												new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode(),
												roundNumber(det.getConversionRate()),
												det.getDepartmentId(),
												department,
												det.getDivisionId(),
												division,
												det.getFromDate(),
												det.getToDate()}, table.getItemIds().size()+1);
								
								List billList=new ArrayList();
								if(det.getBill_no().trim().length()>0){
									billList=Arrays.asList(det.getBill_no().trim().split(","));
									Iterator itr=billList.iterator();
									while (itr.hasNext()) {
										long pid = Long.parseLong(itr.next().toString().trim());
										if(pid!=0){
											if(!invoiceBillList.contains((Long)pid)){
												invoiceBillList.add(pid);
											}
										}
									}
								}
							}
							calculateTotals();
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							printButton.setVisible(true);
							cancelButton.setVisible(true);
						}
						if (!isFinYearBackEntry()) {
							saveButton.setVisible(false);
							updateButton.setVisible(false);
							printButton.setVisible(false);
							deleteButton.setVisible(false);
							cancelButton.setVisible(false);
							if (paymentCombo.getValue() == null || paymentCombo.getValue().toString().equals("0")) {
								Notification.show(getPropertyName("warning_financial_year"),Type.WARNING_MESSAGE);
							}
						}
						table.setVisibleColumns(requiredHeaders);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			
			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							CashAccountDepositModel mdl = dao.getCashAccountDepositModel((Long) paymentCombo.getValue());
							List<CashAccountDepositDetailsModel> childList=new ArrayList<CashAccountDepositDetailsModel>();
							List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
							
							mdl.setCashAccount(new LedgerModel((Long)cashAccountSelect.getValue()));
							mdl.setRef_no(refNoTextField.getValue());
							mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							mdl.setOffice_id(getOfficeID());
							mdl.setLogin_id(getLoginID());
							mdl.setMemo(memoTextArea.getValue());
							
							FinTransaction tran = new FinTransaction();
							Iterator itr = table.getItemIds().iterator();							

							while (itr.hasNext()) {
								boolean isBaseCurrency=true;
								Item item = table.getItem(itr.next());
								CashAccountDepositDetailsModel det=new CashAccountDepositDetailsModel();
								
								det.setPaymentType((Long) item.getItemProperty(TBC_EXPENSE_TYPE).getValue());
								det.setAccount(new LedgerModel((Long) item.getItemProperty(TBC_LEDGER_ID).getValue()));
								det.setButtonVisible((Boolean) item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
								det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
								det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
								det.setCurrencyId((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue());
								if((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()!=getCurrencyID())
									isBaseCurrency=false;
								det.setConversionRate((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
								det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
								det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
								det.setFromDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_FROM_DATE).getValue()));
								det.setToDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_TO_DATE).getValue()));
								childList.add(det);
								
								if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
									double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
									double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
									List billList=new ArrayList();
									billList=Arrays.asList(det.getBill_no().split(","));
									Iterator it=billList.iterator();
									while (it.hasNext()) {
										long pid = Long.parseLong(it.next().toString().trim());
										double actualPaidAmount=0;
										double actualAmount=0;
										double actual_balance_to_pay=0;
										double actualPayingAmount=0;
										
										double totalPayed=0;
										double totalAmount=0;
										double payingAmount=0;
										double balance_to_pay=0;
										
										if(paymentAmount<=0)
											break;
										if(pid!=0) {
											SalesModel pmdl=new SalesDao().getSale(pid);
											List paymentList=new ArrayList();
											
											paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.SALES, pid);
											if(paymentList.size()>0){
												Iterator payItr=paymentList.iterator();
												while (payItr.hasNext()) {
													PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
													if(mapMdl.getPaymentId()!=mdl.getId()){
														if(isBaseCurrency){
															totalPayed+=roundNumber(mapMdl.getAmount());
														}
														else{
															totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
															actualPaidAmount+=mapMdl.getAmount();
														}
													}
												}
											}
											
											paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.CUSTOMER);
											if(paymentList.size()>0){
												Iterator payItr=paymentList.iterator();
												while (payItr.hasNext()) {
													DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
													if(mapMdl.getPaymentId()!=mdl.getId()){
														if(isBaseCurrency){
															totalAmount+=roundNumber(mapMdl.getAmount());
														}
														else{
															totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
															actualAmount+=mapMdl.getAmount();
														}
													}
												}
											}
											
											paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.CUSTOMER);
											if(paymentList.size()>0){
												Iterator payItr=paymentList.iterator();
												while (payItr.hasNext()) {
													DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
													if(mapMdl.getPaymentId()!=mdl.getId()){
														if(isBaseCurrency){
															totalPayed+=roundNumber(mapMdl.getAmount());
														}
														else{
															totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
															actualPaidAmount+=mapMdl.getAmount();
														}
													}
												}
											}
											
											if(isBaseCurrency){
												totalAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
												totalPayed+=roundNumber(pmdl.getPayment_amount());
											}
											else{
												totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate())-(pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
												actualAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
												totalPayed+=roundNumber(pmdl.getPayment_amount()/pmdl.getConversionRate());
												actualPaidAmount+=roundNumber(pmdl.getPayment_amount());
											}
											
											balance_to_pay=totalAmount-totalPayed;
											actual_balance_to_pay=actualAmount-actualPaidAmount;
											
											PaymentInvoiceBean bean;
											if(isBaseCurrency) {
												if(paymentAmount>=balance_to_pay){
													payingAmount=balance_to_pay;
													paymentAmount-=balance_to_pay;
												}
												else{
													payingAmount=paymentAmount;
													paymentAmount=0;
												}
												bean=new PaymentInvoiceBean(
																	SConstants.SALES,
																	pid,
																	mdl.getId(),
																	getOfficeID(),
																	(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																	roundNumber(payingAmount),
																	roundNumber(paymentConversionRate));
												invoiceMapList.add(bean);
												tran.addTransaction(SConstants.CR, 
																	(Long) item.getItemProperty(TBC_LEDGER_ID).getValue(),
																	(Long) cashAccountSelect.getValue(),
																	roundNumber(payingAmount),
																	"",
																	(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																	(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																	(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
												
											}
											else{
												boolean isSavable=false;
												if(paymentAmount>=actual_balance_to_pay){
													isSavable=true;
													actualPayingAmount=actual_balance_to_pay;
													paymentAmount-=actual_balance_to_pay;
												}
												else{
													actualPayingAmount=paymentAmount;
													paymentAmount=0;
												}
												
												double actualBaseCurrency=0;
												actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
												double differenceAmount=0;
												differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
												
												bean=new PaymentInvoiceBean(
																	SConstants.SALES,
																	pid,
																	mdl.getId(),
																	getOfficeID(),
																	(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																	roundNumber(actualPayingAmount),
																	roundNumber(paymentConversionRate));
												if(isSavable){
													if(differenceAmount>0){
														tran.addTransaction(SConstants.CR, 
																			settings.getFOREX_DIFFERENCE_ACCOUNT(),
																			(Long) cashAccountSelect.getValue(),
																			roundNumber(differenceAmount),
																			"",
																			(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																			(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																			(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
														actualBaseCurrency-=differenceAmount;
													}
												}
												
												tran.addTransaction(SConstants.CR, 
															(Long)item.getItemProperty(TBC_LEDGER_ID).getValue(),
															(Long) cashAccountSelect.getValue(),
															roundNumber(actualBaseCurrency),
															"",
															(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
															(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
															(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
												invoiceMapList.add(bean);
											}
										}
									}
									if(paymentAmount>0){
										tran.addTransaction(SConstants.CR, 
												(Long) item.getItemProperty(TBC_LEDGER_ID).getValue(),
												(Long) cashAccountSelect.getValue(),
												roundNumber(paymentAmount/paymentConversionRate),
												"",
												(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
												(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
												(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									}
								}
								else{
									tran.addTransaction(SConstants.CR, 
											(Long) item.getItemProperty(TBC_LEDGER_ID).getValue(),
											(Long) cashAccountSelect.getValue(),
											roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue() /
														(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
											"",
											(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
											(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue(),(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
											(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
								}
							}
							mdl.setCash_account_deposit_list(childList);
							
							TransactionModel transaction = new PurchaseDao().getTransactionModel(mdl.getTransactionId());
							transaction.setTransaction_details_list(tran.getChildList());
							transaction.setDate(mdl.getDate());
							transaction.setLogin_id(getLoginID());

							dao.update(mdl, transaction, invoiceMapList);

							saveActivity(getOptionId(),"Cash Account Deposit Updated. Bill No : "
											+ paymentCombo.getItemCaption(paymentCombo.getValue())
											+ ", Cash Acct. : "+ cashAccountSelect.getItemCaption(cashAccountSelect.getValue())
											+ ", Payment Amount : "+ table.getColumnFooter(TBC_AMOUNT).toString(),(Long)paymentCombo.getValue());
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							loadData(mdl.getId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString() .equals("0")) {
						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.delete((Long) paymentCombo.getValue());
										saveActivity(getOptionId(),"Cash Account Deposit Deleted. Bill No : "
													+ paymentCombo.getItemCaption(paymentCombo.getValue())
													+ ", Cash Acct. : "+ cashAccountSelect.getItemCaption(cashAccountSelect.getValue())
													+ ", Payment Amount : "
													+ table.getColumnFooter(TBC_AMOUNT).toString(),
													(Long)paymentCombo.getValue());
										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadData(0);
									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
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

					if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.cancel((Long) paymentCombo.getValue());
										saveActivity(getOptionId(),"Cash Account Deposit Deleted. Bill No : "
													+ paymentCombo.getItemCaption(paymentCombo.getValue())+ ", Bank Acct. : "
													+ cashAccountSelect.getItemCaption(cashAccountSelect.getValue())+ ", Payment Amount : "+ 
													table.getColumnFooter(TBC_AMOUNT).toString(),
													(Long)paymentCombo.getValue());

										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadData(0);
									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									}
								}
							}
						});
					}

				}
			});
			
			
			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
				}
			});

			
			table.addShortcutListener(new ShortcutListener(
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

			
			table.addActionHandler(new Action.Handler() {
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

			
			addShortcutListener(new ShortcutListener("Add", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});
			
			if (!isFinYearBackEntry()) {
				saveButton.setVisible(false);
				updateButton.setVisible(false);
				deleteButton.setVisible(false);
				printButton.setVisible(false);
				cancelButton.setVisible(false);
				Notification.show(getPropertyName("warning_financial_year"),
						Type.WARNING_MESSAGE);
			}
			
			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0")){
							CashAccountDepositModel mdl = dao.getCashAccountDepositModel((Long) paymentCombo.getValue());
							
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("cash_account_deposit"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("cash_account_deposit"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("cash_account"));
							map.put("LEDGER", mdl.getCashAccount().getName());
							map.put("ADDRESS", "");
							/*map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}*/
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("BILL_NO_LABEL", getPropertyName("ref_no"));
							map.put("BILL_NO", mdl.getRef_no());
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("Account"));
							map.put("QUANTITY_LABEL", getPropertyName("amount"));
//							map.put("UNIT_LABEL", getPropertyName("unit"));
//							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
//							map.put("NET_PRICE_LABEL", getPropertyName("net_price"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							S_OfficeModel office = new OfficeDao().getOffice(mdl.getOffice_id());
							map.put("OFFICE_NAME", office.getName());
							
							try {
								File headFile=new File(rootPath.trim()+office.getHeader().trim());
								if(headFile.exists()&& !headFile.isDirectory())
									map.put("HEADER", rootPath.trim()+office.getHeader().trim());
								else
									map.put("HEADER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("HEADER", rootPath.trim()+"blank.png");
							}
							
							try {
								File footFile=new File(rootPath.trim()+office.getFooter().trim());
								if(footFile.exists()&& !footFile.isDirectory())
									map.put("FOOTER", rootPath.trim()+office.getFooter().trim());
								else
									map.put("FOOTER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("FOOTER", rootPath.trim()+"blank.png");
							}
							double total=0;
							Iterator itr=mdl.getCash_account_deposit_list().iterator();
							while (itr.hasNext()) {
								CashAccountDepositDetailsModel det = (CashAccountDepositDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setAccount(det.getAccount().getName());
								bean.setAmount(roundNumber(det.getAmount()));
								bean.setCurrency(new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode());
								total+=roundNumber(det.getAmount()/det.getConversionRate());
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(total));
							map.put("CURRENCY", office.getCurrency().getCode());
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.CASH_VOVCHER));
							report.setReportFileName("Cash Account Deposit Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.print();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadData(long id) {
		List list= new ArrayList();
		try {
			list.add(new CashAccountDepositModel(0, "----Create New-----"));
			list.addAll(dao.getCashAccountDepositModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			paymentCombo.setContainerDataSource(bic);
			paymentCombo.setItemCaptionPropertyId("bill_no");
			paymentCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				Object obj=table.getValue();
				Item item=table.getItem(obj);
				
				List billList=new ArrayList();
				if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
					billList=Arrays.asList(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().split(","));
					Iterator it=billList.iterator();
					while (it.hasNext()) {
						long pid = Long.parseLong(it.next().toString().trim());
						if(pid!=0){
							if(invoiceBillList.contains(pid)){
								invoiceBillList.remove(pid);
							}
						}
					}
				}
				
				table.removeItem(obj);
				int SN = 0;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					Item newitem = table.getItem((Integer) it.next());
					newitem.getItemProperty(TSR_SN).setValue(SN);
				}
			}
			calculateTotals();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public Boolean isAddingValid() {
		boolean ret = true;

		if (!amountField.isFieldValid(dateField.getValue()) || amountField.getValue()<0) {
			ret = false;
			setRequiredError(amountField, getPropertyName("invalid data"),
					true);
		} 
			
		if (ledgerCombo.getValue() == null || ledgerCombo.getValue().equals("")) {
			setRequiredError(ledgerCombo, getPropertyName("select_account"), true);
			ret = false;
		} 
		else {

			if (ledgerCombo.getValue().toString().equals(cashAccountSelect.getValue())) {
				setRequiredError(ledgerCombo, getPropertyName("from_to"), true);
				ret = false;
			} 
			else
				setRequiredError(ledgerCombo, null, false);
		}
		return ret;
	}

	
	@SuppressWarnings("rawtypes")
	public void calculateTotals() {
		try {
			double total=0;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				Item item = table.getItem(it.next());
				total += roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()/ 
										(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
			}
			table.setColumnFooter(TBC_AMOUNT,roundNumber(total)+new CurrencyManagementDao().getselecteditem(getCurrencyID()).getCode());
		} catch (Exception e) {
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	
	@Override
	public Boolean isValid() {
		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table,getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (cashAccountSelect.getValue() == null || cashAccountSelect.getValue().equals("")) {
			setRequiredError(cashAccountSelect, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(cashAccountSelect, null, false);

		if (dateField.getValue() == null) {
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		return ret;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}
	
	
	public SComboField getAccountDepositNumberList() {
		return paymentCombo;
	}

	
	public void setAccountDepositNumberList(SComboField paymentCombo) {
		this.paymentCombo = paymentCombo;
	}

	
	@Override
	public SComboField getBillNoFiled() {
		return paymentCombo;
	}

	
	public void resetItems(){
		accountTypeRadio.setValue(SConstants.cash_account.EXPENSE);
		ledgerCombo.setValue(null);
		selectSalesButton.setVisible(false);
		billNoField.setVisible(false);
		billNoField.setValue("");
		amountField.currencySelect.setReadOnly(false);
		amountField.setValue(getCurrencyID(), 0.0);
		departmentCombo.setValue((long)0);
		divisionCombo.setValue(null);
		fromDateField.setValue(getMonthStartDate());
		toDateField.setValue(getWorkingDate());
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
	}

}
