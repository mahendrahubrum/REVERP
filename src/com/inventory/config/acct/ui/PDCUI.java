package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.PDCDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PdcDetailsModel;
import com.inventory.config.acct.model.PdcModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
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
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.PaymentInvoiceDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DivisionModel;

/**
 * 
 * @author anil
 * @date 12-Aug-2015
 * @Project REVERP
 * 
 */

/**
 * @author sangeeth
 * @date 14-Oct-2015
 * @Project REVERP
 */


@SuppressWarnings("serial")
public class PDCUI extends SparkLogic {

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TSR_EDITABLE = "Edit";
	static String TBC_ID = "ID";
	static String TBC_TYPE = "Type";
	static String TBC_LEDGER_ID = "Ledger Id";
	static String TBC_LEDGER_NAME = "Ledger";
	static String TBC_BANK = "Bank";
	static String TBC_BUTTON_VISIBLE = "Button Visible";
	static String TBC_BILL_NO = "Invoice No";
	static String TBC_AMOUNT = "Amount";
	static String TBC_CURRENCY_ID = "Currency ID";
	static String TBC_CURRENCY = "Currency";
	static String TBC_CONVERSION_RATE = "Conversion Rate";
	static String TBC_CHEQUE_NO= "Cheque No";
	static String TBC_ISSUE_DATE_ID = "Issue Date Id";
	static String TBC_ISSUE_DATE = "Issue Date";
	static String TBC_DEPARTMENT_ID = "Department ID";
	static String TBC_DEPARTMENT = "Department";
	static String TBC_DIVISION_ID = "Division ID";
	static String TBC_DIVISION = "Division";
	static String TBC_FROM_DATE = "From Date";
	static String TBC_TO_DATE = "To Date";

	STable table;
	PaymentInvoicePanel invoicePanel;
	PDCDao dao;
	
	PaymentInvoiceDao paymentDao;

	SGridLayout masterDetailsGrid;
	SGridLayout accountDepositAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField paymentCombo;
	
	SComboField departmentCombo;
	SSelectionField divisionCombo;

	SComboField accountHeadFilter;
	SRadioButton supplierCustomerRadio;
	SComboField ledgerCombo;

	SComboField bankAccountSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField billNoField;
	SCurrencyField amountField;
	SCurrencyField convertedField;
	SDateField dateField;
	SDateField pdcDateField;

//	DocumentAttach docAttach;

	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;

	STextField refNoTextField;
	STextArea memoTextArea;

	SDateField fromDateField;
	SDateField toDateField;
	
	
	LedgerDao ledgerDao = new LedgerDao();

	SButton createNewButton;

	SettingsValuePojo settings;
	WrappedSession session;

	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	private SDateField issueDateField;
	private STextField chequeNumberField;
	private STextField bankNameField;
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	private BankAccountDao bankDao;

	SButton selectBillButton;
	Date previousDate;
	@SuppressWarnings("rawtypes")
	List invoiceBillList;
	
	private DepartmentDao depDao;
	private DivisionDao divDao;
	
	@SuppressWarnings({"unchecked", "rawtypes" })
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
		
		depDao=new DepartmentDao();
		divDao=new DivisionDao();
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		selectBillButton=new SButton(null,"Select Bill");
		selectBillButton.setPrimaryStyleName("addBtnStyle");
		
		allHeaders=new Object[]{TSR_SN, TSR_EDITABLE, TBC_ID, TBC_TYPE, TBC_LEDGER_ID, TBC_LEDGER_NAME, TBC_BANK, TBC_BUTTON_VISIBLE, TBC_BILL_NO, TBC_AMOUNT, 
				TBC_CURRENCY_ID, TBC_CURRENCY, TBC_CONVERSION_RATE ,  TBC_CHEQUE_NO, TBC_ISSUE_DATE_ID, TBC_ISSUE_DATE, 
				TBC_DEPARTMENT_ID , TBC_DEPARTMENT , TBC_DIVISION_ID, TBC_DIVISION, TBC_FROM_DATE, TBC_TO_DATE};
		
		requiredHeaders=new Object[]{TSR_SN, TBC_LEDGER_NAME, TBC_BANK,TBC_BILL_NO, TBC_AMOUNT, TBC_CURRENCY, TBC_CHEQUE_NO, TBC_ISSUE_DATE, 
									TBC_DEPARTMENT , TBC_DIVISION,  };
		
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
		
		supplierCustomerRadio = new SRadioButton(null, 150, SConstants.creditDebitNote.supplierCustomerList, "intKey", "value");
		supplierCustomerRadio.setHorizontal(true);
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		dao = new PDCDao();
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

		dateField = new SDateField(null, 120, getDateFormat());
		pdcDateField = new SDateField(null, 120, getDateFormat(),getWorkingDate());
		refNoTextField = new STextField();
		memoTextArea = new STextArea(getPropertyName("description"), 810, 40);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(1200, 590);
		try {
			
			bankDao=new BankAccountDao();

			updateItemButton.setVisible(false);

			table = new STable(null, 800, 200);

			paymentCombo = new SComboField(null, 200, null, "id",
					"ref_no", true, getPropertyName("create_new"));

			bankAccountSelect = new SComboField(null, 200,
					bankDao.getAllActiveBankAccountNamesWithLedgerID(
							getOfficeID()), "id", "name",
					false, getPropertyName("select"));

			ledgerCombo = new SComboField(getPropertyName("supplier"), 150, 
											new LedgerDao().getAllSuppliers(getOfficeID()), "id", "name", false, getPropertyName("select"));

			accountDepositAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();
			
			List divisionList=new ArrayList();
			divisionList.add(0, new DivisionModel(0, "None"));
			divisionList.addAll(new DivisionDao().getDivisions(getOrganizationID()));
			List departmentList=new ArrayList();
			departmentList.add(0, new DepartmentModel(0, "None"));
			departmentList.addAll(new DepartmentDao().getDepartments(getOrganizationID()));
			divisionCombo = new SSelectionField(getPropertyName("Division"),getPropertyName("none"),200, 400);
			divisionCombo.setContainerData(new DivisionDao().getDivisionsHierarchy(getOrganizationID()));
			departmentCombo = new SComboField(getPropertyName("Department"), 100, departmentList,"id", "name", false, getPropertyName("select"));
			departmentCombo.setValue((long)0);
			
			issueDateField = new SDateField(getPropertyName("issue_date"), 100);
			issueDateField.setValue(getWorkingDate());
			
			chequeNumberField = new STextField(getPropertyName("cheuqe_no"), 100);
			bankNameField = new STextField(getPropertyName("bank"), 150);
			

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
			table.addContainerProperty(TSR_EDITABLE, Boolean.class, null, TSR_EDITABLE, null, Align.CENTER);
			table.addContainerProperty(TBC_ID,Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE,Integer.class, null, TBC_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBC_LEDGER_ID,Long.class, null, TBC_LEDGER_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_LEDGER_NAME,String.class, null, getPropertyName("account"), null,Align.LEFT);
			table.addContainerProperty(TBC_BANK,String.class, null, getPropertyName("bank"), null,Align.LEFT);
			table.addContainerProperty(TBC_BUTTON_VISIBLE,Boolean.class, null, TBC_BUTTON_VISIBLE, null,Align.LEFT);
			table.addContainerProperty(TBC_BILL_NO,String.class, null, TBC_BILL_NO, null,Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT,Double.class, null, getPropertyName("amount"), null,Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY_ID,Long.class, null, TBC_CURRENCY_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY,String.class, null, getPropertyName("currency"), null,Align.LEFT);
			table.addContainerProperty(TBC_CONVERSION_RATE,Double.class, null, getPropertyName("conversion_rate"), null,Align.CENTER);
			table.addContainerProperty(TBC_CHEQUE_NO,String.class, null, getPropertyName("cheque_no"), null,Align.LEFT);
			table.addContainerProperty(TBC_ISSUE_DATE_ID,Date.class, null, TBC_ISSUE_DATE_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_ISSUE_DATE,String.class, null, getPropertyName("issue_date"), null,Align.LEFT);
			table.addContainerProperty(TBC_DEPARTMENT_ID,Long.class, null, TBC_DEPARTMENT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DEPARTMENT,String.class, null, getPropertyName("department"), null,Align.LEFT);
			table.addContainerProperty(TBC_DIVISION_ID,Long.class, null, TBC_DIVISION_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DIVISION,String.class, null, getPropertyName("division"), null,Align.LEFT);
			table.addContainerProperty(TBC_FROM_DATE,Date.class, null, TBC_FROM_DATE, null,Align.LEFT);
			table.addContainerProperty(TBC_TO_DATE,Date.class, null, TBC_TO_DATE, null,Align.LEFT);
			
			
			table.setColumnExpandRatio(TSR_SN, (float) .5);
			table.setColumnExpandRatio(TBC_LEDGER_NAME,1.25f);
			table.setColumnExpandRatio(TBC_BANK,1.25f);
			table.setColumnExpandRatio(TBC_BILL_NO,1f);
			table.setColumnExpandRatio(TBC_CURRENCY,0.5f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1f);
			table.setColumnExpandRatio(TBC_CHEQUE_NO, 1f);
			table.setColumnExpandRatio(TBC_ISSUE_DATE, 1f);
			table.setVisibleColumns(requiredHeaders);

			accountDepositAddGrid.setColumns(7);
			accountDepositAddGrid.setRows(2);

			billNoField=new STextField(null, 75);
			billNoField.setVisible(false);
			billNoField.setValue("");
			amountField = new SCurrencyField(getPropertyName("amount"), 80,getWorkingDate());
			convertedField = new SCurrencyField(null, 75,getWorkingDate());

			SHorizontalLayout expenseLayout=new SHorizontalLayout();
			expenseLayout.addComponent(ledgerCombo);
			expenseLayout.addComponent(selectBillButton);
			
			selectBillButton.setVisible(false);
			expenseLayout.setComponentAlignment(selectBillButton, Alignment.BOTTOM_CENTER);
			
			accountDepositAddGrid.addComponent(expenseLayout);
			accountDepositAddGrid.addComponent(billNoField);
			accountDepositAddGrid.addComponent(amountField);
			accountDepositAddGrid.addComponent(convertedField);
			accountDepositAddGrid.setComponentAlignment(convertedField, Alignment.BOTTOM_CENTER);
			convertedField.currencySelect.setReadOnly(true);
			convertedField.amountField.setReadOnly(true);
			convertedField.setVisible(false);
			accountDepositAddGrid.addComponent(bankNameField);
			accountDepositAddGrid.addComponent(chequeNumberField);
			accountDepositAddGrid.addComponent(issueDateField);
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

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("payment_id"), 30), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(paymentCombo);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(refNoTextField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),5, 0);
			masterDetailsGrid.addComponent(dateField, 6, 0);

			// masterDetailsGrid.addComponent(new SLabel("Pay To :"), 3, 1);
			// masterDetailsGrid.addComponent(, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("bank_account")),1, 1);
			masterDetailsGrid.addComponent(bankAccountSelect, 2, 1);
			masterDetailsGrid.addComponent(supplierCustomerRadio, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("cheque_date")),5, 1);
			masterDetailsGrid.addComponent(pdcDateField, 6, 1);
			masterDetailsGrid.setSpacing(true);
//			masterDetailsGrid.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 1f);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 1.25f);
			masterDetailsGrid.setColumnExpandRatio(5, 1.25f);
			masterDetailsGrid.setColumnExpandRatio(6, 1);


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

			
			supplierCustomerRadio.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent arg0) {
					try {
						if(supplierCustomerRadio.getValue()!=null){
							SCollectionContainer bic;
							List mainList=new ArrayList();
							table.removeAllItems();
							invoiceBillList.clear();
							amountField.setNewValue(getCurrencyID(), 0);
							amountField.setReadOnly(false);
							if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER){
								mainList=new LedgerDao().getAllSuppliers(getOfficeID());
								bic=SCollectionContainer.setList(mainList, "id");
								ledgerCombo.setContainerDataSource(bic);
								ledgerCombo.setItemCaptionPropertyId("name");
								ledgerCombo.setCaption("Supplier");
							}else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER) {
								mainList=new LedgerDao().getAllCustomers(getOfficeID());
								bic=SCollectionContainer.setList(mainList, "id");
								ledgerCombo.setContainerDataSource(bic);
								ledgerCombo.setItemCaptionPropertyId("name");
								ledgerCombo.setCaption("Customer");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			supplierCustomerRadio.setValue(SConstants.creditDebitNote.SUPPLIER);
			
			
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

			
			bankAccountSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (bankAccountSelect.getValue() != null) {
							bankAccountSelect.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ ledgerDao
											.getLedgerCurrentBalance((Long) bankAccountSelect
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
							ledgerCombo.setDescription("<i class='ledger_bal_style'>Current Balance : "+ ledgerDao
											.getLedgerCurrentBalance((Long) ledgerCombo.getValue()) + "</i>");
							if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER) {
								
								if(paymentDao.isPaymentPendingForSupplier((Long) ledgerCombo.getValue())>0){
									selectBillButton.setVisible(true);
									
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
															SConstants.PURCHASE, 
															fromDateField.getValue(),
															toDateField.getValue(),
															billNoSet,isCreateNew);
									invoicePanel.netAmountField.rateButton.setVisible(false);
//									getUI().addWindow(invoicePanel);
									
								}
								else
									selectBillButton.setVisible(false);
							}
							else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER) {
								
								if(paymentDao.isPaymentPendingForCustomer((Long) ledgerCombo.getValue())>0){
									selectBillButton.setVisible(true);
									
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
									
								}
								else
									selectBillButton.setVisible(false);
							}
							else
								selectBillButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			selectBillButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (ledgerCombo.getValue() != null) {
							ledgerCombo.setDescription("<i class='ledger_bal_style'>Current Balance : "+ ledgerDao
											.getLedgerCurrentBalance((Long) ledgerCombo.getValue()) + "</i>");
							if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER) {
								
								if(paymentDao.isPaymentPendingForSupplier((Long) ledgerCombo.getValue())>0){
									selectBillButton.setVisible(true);
									
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
															SConstants.PURCHASE, 
															fromDateField.getValue(),
															toDateField.getValue(),
															billNoSet,isCreateNew);
									invoicePanel.netAmountField.rateButton.setVisible(false);
									getUI().addWindow(invoicePanel);
									
								}
								else
									selectBillButton.setVisible(false);
							}
							else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER) {
								
								if(paymentDao.isPaymentPendingForCustomer((Long) ledgerCombo.getValue())>0){
									selectBillButton.setVisible(true);
									
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
									getUI().addWindow(invoicePanel);
									
								}
								else
									selectBillButton.setVisible(false);
							}
							else
								selectBillButton.setVisible(false);
						}
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
							
							int type=0;
							if(supplierCustomerRadio.getValue()!=null){
								if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER)
									type=SConstants.PURCHASE;
								else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER)
									type=SConstants.SALES;
							}
							
							if(isAddable){
								table.addItem(new Object[] {
										table.getItemIds().size()+1,
										true,
										(long)0,
										type,
										(Long) ledgerCombo.getValue(),
										ledgerCombo.getItemCaption(ledgerCombo.getValue()),
										bankNameField.getValue().toString().trim(),
										selectBillButton.isVisible(),
										billNoField.getValue().toString().trim(),
										roundNumber(amountField.getValue()),
										amountField.getCurrency(),
										new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getCode(),
										roundNumber(amountField.getConversionRate()),
										chequeNumberField.getValue().toString().trim(),
										issueDateField.getValue(),
										CommonUtil.formatDateToDDMMYYYY(issueDateField.getValue()),
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
						selectBillButton.setVisible((Boolean)item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
						billNoField.setValue(item.getItemProperty(TBC_BILL_NO).getValue().toString());
						fromDateField.setValue((Date)item.getItemProperty(TBC_FROM_DATE).getValue());
						toDateField.setValue((Date)item.getItemProperty(TBC_TO_DATE).getValue());
						amountField.setNewValue((Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(), 
								(Double)item.getItemProperty(TBC_AMOUNT).getValue());
						ledgerCombo.setValue((Long)item.getItemProperty(TBC_LEDGER_ID).getValue());
						chequeNumberField.setValue(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
						bankNameField.setValue(item.getItemProperty(TBC_BANK).getValue().toString());
						if((Date)item.getItemProperty(TBC_ISSUE_DATE_ID).getValue()!=null)
							issueDateField.setValue((Date)item.getItemProperty(TBC_ISSUE_DATE_ID).getValue());
						else
							issueDateField.setValue(getWorkingDate());
						departmentCombo.setValue(item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
						divisionCombo.setValue(item.getItemProperty(TBC_DIVISION_ID).getValue());
						if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0)
							amountField.currencySelect.setReadOnly(true);
						else
							amountField.currencySelect.setReadOnly(false);
						if((Boolean)item.getItemProperty(TSR_EDITABLE).getValue()){
							updateItemButton.setVisible(true);
							addItemButton.setVisible(false);
						}
						else{
							updateItemButton.setVisible(false);
							addItemButton.setVisible(false);
							SNotification.show("Payment made on THis PDC", Type.TRAY_NOTIFICATION);
						}
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
								item.getItemProperty(TBC_LEDGER_ID).setValue((Long)ledgerCombo.getValue());
								item.getItemProperty(TBC_LEDGER_NAME).setValue(ledgerCombo.getItemCaption(ledgerCombo.getValue()));
								item.getItemProperty(TBC_BUTTON_VISIBLE).setValue(selectBillButton.isVisible());
								item.getItemProperty(TBC_BILL_NO).setValue(billNoField.getValue().toString().trim());
								item.getItemProperty(TBC_BANK).setValue(bankNameField.getValue());
								item.getItemProperty(TBC_AMOUNT).setValue(roundNumber(amountField.getValue()));
								item.getItemProperty(TBC_CURRENCY_ID).setValue(amountField.getCurrency());
								item.getItemProperty(TBC_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getCode());
								item.getItemProperty(TBC_CONVERSION_RATE).setValue(roundNumber(amountField.getConversionRate()));
								item.getItemProperty(TBC_ISSUE_DATE_ID).setValue(issueDateField.getValue());
								item.getItemProperty(TBC_ISSUE_DATE).setValue(CommonUtil.formatDateToDDMMYYYY(issueDateField.getValue()));
								item.getItemProperty(TBC_CHEQUE_NO).setValue(chequeNumberField.getValue().toString().trim());
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
								PdcModel mdl = new PdcModel();
								List<PdcDetailsModel> childList=new ArrayList<PdcDetailsModel>();
								
								mdl.setType((Integer)supplierCustomerRadio.getValue());
								mdl.setBankAccount(new LedgerModel((Long)bankAccountSelect.getValue()));
								mdl.setRef_no(refNoTextField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setChequeDate(CommonUtil.getSQLDateFromUtilDate(pdcDateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								mdl.setBill_no(getNextSequence("PDC No", getLoginID())+"");
								mdl.setActive(true);
								
								Iterator itr = table.getItemIds().iterator();							
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									PdcDetailsModel det=new PdcDetailsModel();
									det.setType((Integer) item.getItemProperty(TBC_TYPE).getValue());
									det.setAccount(new LedgerModel((Long) item.getItemProperty(TBC_LEDGER_ID).getValue()));
									det.setButtonVisible((Boolean) item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
									det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
									det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
									det.setCurrencyId(new CurrencyModel((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()));
									det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue()));
									det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
									det.setBankName(item.getItemProperty(TBC_BANK).getValue().toString());
									det.setIssueDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_ISSUE_DATE_ID).getValue()));
									det.setFromDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_FROM_DATE).getValue()));
									det.setToDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_TO_DATE).getValue()));
									det.setStatus(SConstants.PDCStatus.ISSUED);
									childList.add(det);
								}
								mdl.setPdc_list(childList);

								long id = dao.save(mdl);
								saveActivity(getOptionId(), "PDC Saved. Bill No : "
												+ id+ ", Bank Acct. : "+ bankAccountSelect.getItemCaption(bankAccountSelect.getValue())
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
						table.setVisibleColumns(allHeaders);
						table.removeAllItems();
						invoiceBillList.clear();
						bankAccountSelect.setValue(null);
						supplierCustomerRadio.setReadOnly(false);
						supplierCustomerRadio.setValue(SConstants.creditDebitNote.SUPPLIER);
						refNoTextField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						pdcDateField.setValue(getWorkingDate());
						amountField.setCurrencyDate(getWorkingDate());
						amountField.setNewValue(getCurrencyID(),0);
						memoTextArea.setValue("");
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						resetItems();
						if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {

							PdcModel objModel = dao.getPdcModel((Long) paymentCombo.getValue());
							previousDate=objModel.getDate();
							dateField.setValue(objModel.getDate());
							pdcDateField.setValue(objModel.getChequeDate());
							supplierCustomerRadio.setValue(objModel.getType());
							amountField.setCurrencyDate(objModel.getDate());
							memoTextArea.setValue(objModel.getMemo());
							refNoTextField.setValue(objModel.getRef_no());
							bankAccountSelect.setValue(objModel.getBankAccount().getId());
							boolean isLocked=false;
							Iterator it = objModel.getPdc_list().iterator();
							while (it.hasNext()) {
								PdcDetailsModel det = (PdcDetailsModel) it.next();

								String department="";
								String division="";
								
								if(det.getDepartmentId()!=0)
									department=depDao.getDepartment(det.getDepartmentId()).getName();
								else
									department="None";
								if(det.getDivisionId()!=0)
									division=divDao.getDivision(det.getDivisionId()).getName();
								else
									division="None";
								
								boolean isEditable=true;
								
								if(det.getStatus()==SConstants.PDCStatus.APPROVED){
									isEditable=false;
									isLocked=true;
								}
								
								table.addItem(
										new Object[] {
												table.getItemIds().size()+1,
												isEditable,
												det.getId(),
												det.getType(),
												det.getAccount().getId(),
												det.getAccount().getName(),
												det.getBankName(),
												det.isButtonVisible(),
												det.getBill_no(),
												roundNumber(det.getAmount()),
												det.getCurrencyId().getId(),
												det.getCurrencyId().getCode(),
												roundNumber(det.getConversionRate()),
												det.getChequeNo(),
												det.getIssueDate(),
												CommonUtil.formatDateToDDMMYYYY(det.getIssueDate()),
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
							if(isLocked)
								supplierCustomerRadio.setReadOnly(true);
							calculateTotals();
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);
						}
						if (!isFinYearBackEntry()) {
							saveButton.setVisible(false);
							updateButton.setVisible(false);
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
						if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {
							if (isValid()) {
								PdcModel mdl = dao.getPdcModel((Long) paymentCombo.getValue());
								List<PdcDetailsModel> childList=new ArrayList<PdcDetailsModel>();
								
								mdl.setType((Integer)supplierCustomerRadio.getValue());
								mdl.setBankAccount(new LedgerModel((Long)bankAccountSelect.getValue()));
								mdl.setRef_no(refNoTextField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setChequeDate(CommonUtil.getSQLDateFromUtilDate(pdcDateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								
								Iterator itr = table.getItemIds().iterator();							
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									PdcDetailsModel det=null;
									if((Long)item.getItemProperty(TBC_ID).getValue()!=0)
										det=dao.getPdcDetailsModel((Long)item.getItemProperty(TBC_ID).getValue());
									if(det==null)
										det=new PdcDetailsModel();
									det.setType((Integer) item.getItemProperty(TBC_TYPE).getValue());
									det.setAccount(new LedgerModel((Long) item.getItemProperty(TBC_LEDGER_ID).getValue()));
									det.setButtonVisible((Boolean) item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
									det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
									det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
									det.setCurrencyId(new CurrencyModel((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()));
									det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue()));
									det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
									det.setBankName(item.getItemProperty(TBC_BANK).getValue().toString());
									det.setIssueDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_ISSUE_DATE_ID).getValue()));
									det.setFromDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_FROM_DATE).getValue()));
									det.setToDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_TO_DATE).getValue()));
									if(det.getId()==0)
										det.setStatus(SConstants.PDCStatus.ISSUED);
									childList.add(det);
								}
								mdl.setPdc_list(childList);
								dao.update(mdl);
								saveActivity(getOptionId(),"PDC Updated. Bill No : "
										+ paymentCombo.getItemCaption(paymentCombo.getValue())
										+ ", Bank Acct. : "+ bankAccountSelect.getItemCaption(bankAccountSelect.getValue())
										+ ", Payment Amount : "+ table.getColumnFooter(TBC_AMOUNT).toString(),(Long)paymentCombo.getValue());
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								loadData(mdl.getId());
							}
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
										PdcModel mdl = dao.getPdcModel((Long) paymentCombo.getValue());
										Iterator it=mdl.getPdc_list().iterator();
										boolean isLocked=false;
										while (it.hasNext()) {
											PdcDetailsModel det = (PdcDetailsModel) it.next();
											if(det.getStatus()==SConstants.PDCStatus.APPROVED)
												isLocked=true;
										}
										if(!isLocked){
											dao.delete((Long) paymentCombo.getValue());
											saveActivity(getOptionId(),"PDC Deleted. Bill No : "
														+ paymentCombo.getItemCaption(paymentCombo.getValue())
														+ ", Bank Acct. : "+ bankAccountSelect.getItemCaption(bankAccountSelect.getValue())
														+ ", Payment Amount : "
														+ table.getColumnFooter(TBC_AMOUNT).toString(),
														(Long)paymentCombo.getValue());
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											loadData(0);
										}
										else{
											SNotification.show("Payment made in this PDC. Cannot Delete", Type.ERROR_MESSAGE);
										}
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
										PdcModel mdl = dao.getPdcModel((Long) paymentCombo.getValue());
										Iterator it=mdl.getPdc_list().iterator();
										boolean isLocked=false;
										while (it.hasNext()) {
											PdcDetailsModel det = (PdcDetailsModel) it.next();
											if(det.getStatus()==SConstants.PDCStatus.APPROVED)
												isLocked=true;
										}
										if(!isLocked){
											dao.cancel((Long) paymentCombo.getValue());
											saveActivity(getOptionId(),"Bank Account Payment Deleted. Bill No : "
														+ paymentCombo.getItemCaption(paymentCombo.getValue())+ ", Bank Acct. : "
														+ bankAccountSelect.getItemCaption(bankAccountSelect.getValue())+ ", Payment Amount : "+ 
														table.getColumnFooter(TBC_AMOUNT).toString(),
														(Long)paymentCombo.getValue());

											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											loadData(0);
										}
										else{
											SNotification.show("Payment made in this PDC. Cannot Cancel", Type.ERROR_MESSAGE);
										}
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
				cancelButton.setVisible(false);
				Notification.show(getPropertyName("warning_financial_year"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadData(long id) {
		List list= new ArrayList();
		try {
			list.add(new PdcModel(0, "----Create New-----"));
			list.addAll(dao.getPdcModelList(getOfficeID()));
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
				if((Boolean)item.getItemProperty(TSR_EDITABLE).getValue()){
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
				else{
					SNotification.show("Payment made in this PDC. Cannot Delete", Type.ERROR_MESSAGE);
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
			
		if (bankNameField.getValue() == null || bankNameField.getValue().equals("")) {
			setRequiredError(bankNameField, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(bankNameField, null, false);
		
		if (chequeNumberField.getValue() == null || chequeNumberField.getValue().equals("")) {
			setRequiredError(chequeNumberField, getPropertyName("invalid_data"), true);
			ret = false;
		} else{
			try {
				if(toDouble(chequeNumberField.getValue().toString())<0){
					setRequiredError(chequeNumberField, getPropertyName("invalid_data"), true);
					ret = false;
				}
				else
					setRequiredError(chequeNumberField, null, false);
				
			} catch (Exception e) {
				setRequiredError(chequeNumberField, getPropertyName("invalid_data"), true);
				ret = false;
			}
		}
		
		if (ledgerCombo.getValue() == null || ledgerCombo.getValue().equals("")) {
			setRequiredError(ledgerCombo, getPropertyName("select_account"), true);
			ret = false;
		} 
		else {

			if (ledgerCombo.getValue().toString().equals(bankAccountSelect.getValue())) {
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

		if (bankAccountSelect.getValue() == null || bankAccountSelect.getValue().equals("")) {
			setRequiredError(bankAccountSelect, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(bankAccountSelect, null, false);

		if (dateField.getValue() == null) {
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
		if (pdcDateField.getValue() == null) {
			setRequiredError(pdcDateField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(pdcDateField, null, false);
		
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
	
	
	public void setPaymentCombo(long id) {
		paymentCombo.setValue(id);
	}


	public void resetItems(){
		ledgerCombo.setValue(null);
		selectBillButton.setVisible(false);
		billNoField.setVisible(false);
		billNoField.setValue("");
		amountField.currencySelect.setReadOnly(false);
		amountField.setValue(getCurrencyID(), 0.0);
		departmentCombo.setValue((long)0);
		divisionCombo.setValue(null);
		chequeNumberField.setValue("");
		bankNameField.setValue("");
		issueDateField.setValue(getWorkingDate());
		fromDateField.setValue(getMonthStartDate());
		toDateField.setValue(getWorkingDate());
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
	}

}
