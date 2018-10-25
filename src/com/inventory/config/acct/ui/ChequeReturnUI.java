package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.BankAccountDepositDao;
import com.inventory.config.acct.dao.BankAccountPaymentDao;
import com.inventory.config.acct.dao.ChequeReturnDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.BankAccountDepositDetailsModel;
import com.inventory.config.acct.model.BankAccountPaymentDetailsModel;
import com.inventory.config.acct.model.BankDetailsInvoiceMapModel;
import com.inventory.config.acct.model.ChequeReturnDetailsModel;
import com.inventory.config.acct.model.ChequeReturnModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
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
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
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
 * 
 * @author sangeeth
 * @date 28-Sep-2015
 * @Project REVERP
 */

public class ChequeReturnUI extends SparkLogic {

	private static final long serialVersionUID = 173745310353492562L;

	SPanel mainPanel = null;
	static String TBC_SELECT = "#";
	static String TBC_TYPE = "Type";
	static String TBC_BILL_ID = "Bill Id";
	static String TBC_BILL_NO = "Bill No";
	static String TBC_PAYMENT_ID = "Payment 	Id";
	static String TBC_PAYMENT_CHILD_ID = "Payment Child Id";	
	static String TBC_CHEQUE_NO = "Cheque No";
	static String TBC_CHEQUE_DATE_ID = "Cheque Date Id";	
	static String TBC_CHEQUE_DATE = "Cheque Date";
	static String TBC_AMOUNT = "Amount";
	static String TBC_CURRENCY_ID = "Currency ID";
	static String TBC_CURRENCY = "Currency";
	static String TBC_CONVERSION_RATE = "Conversion Rate";
	static String TBC_DEPARTMENT_ID = "Department ID";
	static String TBC_DEPARTMENT = "Department";
	static String TBC_DIVISION_ID = "Division ID";
	static String TBC_DIVISION = "Division";
	static String TBC_IS_RETURNED = "Is Returned";

	STable table;
	ChequeReturnDao dao;
	
	SGridLayout masterDetailsGrid;
	SGridLayout itemGridLayout;
	SVerticalLayout mainLayout;

	SComboField chequeReturnCombo;
	SLabel ledgerLabel;
	SRadioButton supplierCustomerRadio;
	SComboField ledgerCombo;
	SComboField billNoCombo;
	STextField refNoField;
	SDateField dateField;
	
	
	
	private SCurrencyField amountField;
	private SCurrencyField convertedField;
	private SComboField departmentCombo;
	private SSelectionField divisionCombo;
	private SNativeButton addItemButton;
	private SNativeButton updateItemButton;

	STextArea memoTextArea;
	
	SButton createNewButton;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;

	LedgerDao ledgerDao = new LedgerDao();

	SettingsValuePojo settings;
	WrappedSession session;
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;

	Date previousDate;
	@SuppressWarnings("rawtypes")
	List invoiceBillList;
	
	private DepartmentDao depDao;
	private DivisionDao divDao;
	
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		invoiceBillList=new ArrayList();
		previousDate=new Date();
		previousDate=getWorkingDate();
		
		divDao=new DivisionDao();
		depDao=new DepartmentDao();
		
		allHeaders=new Object[]{TBC_SELECT, TBC_TYPE, TBC_BILL_ID, TBC_BILL_NO, TBC_PAYMENT_ID, TBC_PAYMENT_CHILD_ID, TBC_CHEQUE_NO, 
				TBC_CHEQUE_DATE_ID, TBC_CHEQUE_DATE, TBC_AMOUNT, TBC_CURRENCY_ID, TBC_CURRENCY, TBC_CONVERSION_RATE , 
				TBC_DEPARTMENT_ID , TBC_DEPARTMENT , TBC_DIVISION_ID, TBC_DIVISION, TBC_IS_RETURNED };
		
		requiredHeaders=new Object[]{ TBC_SELECT, TBC_BILL_NO, TBC_CHEQUE_NO, TBC_CHEQUE_DATE, TBC_AMOUNT, TBC_CURRENCY, 
									TBC_DEPARTMENT , TBC_DIVISION };
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		supplierCustomerRadio = new SRadioButton(null, 150, SConstants.creditDebitNote.supplierCustomerList, "intKey", "value");
		supplierCustomerRadio.setHorizontal(true);
		ledgerLabel=new SLabel("Supplier");
		
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

		dao = new ChequeReturnDao();
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
		refNoField = new STextField();
		memoTextArea = new STextArea(getPropertyName("description"), 810, 40);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		setSize(1000, 450);
		try {
			
			updateItemButton.setVisible(false);

			table = new STable(null, 800, 200);

			chequeReturnCombo = new SComboField(null, 200, null, "id",
					"ref_no", true, getPropertyName("create_new"));

			ledgerCombo = new SComboField(null, 150, new LedgerDao().getAllSuppliers(getOfficeID()), 
					"id", "name", false, getPropertyName("select"));
			billNoCombo=new SComboField(null, 150);
			billNoCombo.setInputPrompt(getPropertyName("select"));
			itemGridLayout = new SGridLayout();
			mainLayout = new SVerticalLayout();
			
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
			
			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			table.setSizeFull();
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setWidth("900px");
			table.setHeight("180px");
			
			table.addContainerProperty(TBC_SELECT, SCheckBox.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE, Integer.class, null, TBC_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBC_BILL_ID, Long.class, null, TBC_BILL_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_BILL_NO, String.class, null, TBC_BILL_NO, null,Align.LEFT);
			table.addContainerProperty(TBC_PAYMENT_ID, Long.class, null, TBC_PAYMENT_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_PAYMENT_CHILD_ID, Long.class, null, TBC_PAYMENT_CHILD_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_NO, String.class, null, TBC_CHEQUE_NO, null,Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_DATE_ID, Date.class, null, TBC_CHEQUE_DATE_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_DATE, String.class, null, TBC_CHEQUE_DATE, null,Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null, getPropertyName("amount"), null,Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY_ID, Long.class, null, TBC_CURRENCY_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY, String.class, null, getPropertyName("currency"), null,Align.LEFT);
			table.addContainerProperty(TBC_CONVERSION_RATE, Double.class, null, getPropertyName("conversion_rate"), null,Align.CENTER);
			table.addContainerProperty(TBC_DEPARTMENT_ID, Long.class, null, TBC_DEPARTMENT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DEPARTMENT, String.class, null, getPropertyName("department"), null,Align.LEFT);
			table.addContainerProperty(TBC_DIVISION_ID, Long.class, null, TBC_DIVISION_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DIVISION, String.class, null, getPropertyName("division"), null,Align.LEFT);
			table.addContainerProperty(TBC_IS_RETURNED, Boolean.class, null, TBC_IS_RETURNED, null,Align.LEFT);
			
			
			table.setColumnExpandRatio(TBC_SELECT, 0.25f);
			table.setColumnExpandRatio(TBC_BILL_NO, 0.5f);
			table.setColumnExpandRatio(TBC_CHEQUE_NO, 1f);
			table.setColumnExpandRatio(TBC_CHEQUE_DATE, 1f);
			table.setColumnExpandRatio(TBC_CURRENCY, 0.5f);
			table.setColumnExpandRatio(TBC_DEPARTMENT, 1f);
			table.setColumnExpandRatio(TBC_DIVISION, 1f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1f);
			table.setVisibleColumns(requiredHeaders);

			itemGridLayout.setColumns(7);
			itemGridLayout.setRows(2);

			amountField = new SCurrencyField(getPropertyName("amount"), 80,getWorkingDate());
			convertedField = new SCurrencyField(null, 75,getWorkingDate());

			itemGridLayout.addComponent(amountField);
			itemGridLayout.addComponent(convertedField);
			if(settings.isDEPARTMENT_ENABLED())
				itemGridLayout.addComponent(departmentCombo);
			if(settings.isDIVISION_ENABLED())
				itemGridLayout.addComponent(divisionCombo);
			itemGridLayout.addComponent(addItemButton);
			itemGridLayout.addComponent(updateItemButton);
			itemGridLayout.setComponentAlignment(convertedField, Alignment.BOTTOM_CENTER);
			convertedField.currencySelect.setReadOnly(true);
			convertedField.amountField.setReadOnly(true);
			convertedField.setVisible(false);

			itemGridLayout.setStyleName("bankacctdeposit_adding_grid");

			itemGridLayout.setComponentAlignment(addItemButton,Alignment.BOTTOM_RIGHT);
			itemGridLayout.setComponentAlignment(updateItemButton, Alignment.BOTTOM_RIGHT);

			itemGridLayout.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel("Debit Note No", 30), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(chequeReturnCombo);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(refNoField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			masterDetailsGrid.addComponent(dateField, 6, 0);
			masterDetailsGrid.addComponent(supplierCustomerRadio, 2, 1);
			masterDetailsGrid.addComponent(ledgerLabel, 3, 1);
			masterDetailsGrid.addComponent(ledgerCombo, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("bill_no")), 5, 1);
			masterDetailsGrid.addComponent(billNoCombo, 6, 1);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 1);
			masterDetailsGrid.setColumnExpandRatio(2, 1.5f);
			masterDetailsGrid.setColumnExpandRatio(3, 1.5f);
			masterDetailsGrid.setColumnExpandRatio(4, 1.5f);
			masterDetailsGrid.setColumnExpandRatio(5, 1.5f);
			masterDetailsGrid.setColumnExpandRatio(6, 1.5f);


			mainLayout.addComponent(masterDetailsGrid);

			mainLayout.setMargin(true);
			mainLayout.setSpacing(true);

			mainLayout.addComponent(table);

//			mainLayout.addComponent(new SHorizontalLayout(true,
//					itemGridLayout/*, docAttach*/));

			SFormLayout fm = new SFormLayout();
			fm.addComponent(memoTextArea);
			mainLayout.addComponent(fm);

			mainLayout.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			mainLayout.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_AMOUNT,asString(roundNumber(0)));
			table.setColumnFooter(TBC_BILL_NO,getPropertyName("total"));

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(mainLayout);

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			mainPanel.setContent(windowNotif);

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
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)chequeReturnCombo.getValue(),confirmBox.getUserID());
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
						if(chequeReturnCombo.getValue()!=null && !chequeReturnCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)chequeReturnCombo.getValue(),
									"Bank Account Deposit : No. "+chequeReturnCombo.getItemCaption(chequeReturnCombo.getValue()));
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
						if(chequeReturnCombo.getValue()!=null && !chequeReturnCombo.getValue().toString().equals("0")) {
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

			
			supplierCustomerRadio.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent arg0) {
					try {
						if(supplierCustomerRadio.getValue()!=null){
							SCollectionContainer bic;
							List mainList=new ArrayList();
							table.removeAllItems();
							amountField.setNewValue(getCurrencyID(), 0);
							amountField.setReadOnly(false);
							if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER){
								mainList=new LedgerDao().getAllSuppliers(getOfficeID());
								bic=SCollectionContainer.setList(mainList, "id");
								ledgerCombo.setContainerDataSource(bic);
								ledgerCombo.setItemCaptionPropertyId("name");
								ledgerLabel.setCaption("Supplier");
							}else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER) {
								mainList=new LedgerDao().getAllCustomers(getOfficeID());
								bic=SCollectionContainer.setList(mainList, "id");
								ledgerCombo.setContainerDataSource(bic);
								ledgerCombo.setItemCaptionPropertyId("name");
								ledgerLabel.setCaption("Customer");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			supplierCustomerRadio.setValue(SConstants.creditDebitNote.SUPPLIER);

			
			ledgerCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						billNoCombo.removeAllItems();
						table.removeAllItems();
						if (ledgerCombo.getValue() != null) {
							ledgerCombo.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ ledgerDao.getLedgerCurrentBalance((Long) ledgerCombo.getValue()) + "</i>");
							SCollectionContainer bic;
							List mainList=new ArrayList();
							if(supplierCustomerRadio.getValue()!=null){
								if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER){
									mainList=new PurchaseDao().getPurchaseModelOfSupplier(getOfficeID(), (Long)ledgerCombo.getValue());
									bic=SCollectionContainer.setList(mainList, "id");
									billNoCombo.setContainerDataSource(bic);
									billNoCombo.setItemCaptionPropertyId("purchase_no");
								}
								else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER){
									mainList=new SalesDao().getAllSalesNumbersOfCustomer(getOfficeID(), (Long)ledgerCombo.getValue());
									bic=SCollectionContainer.setList(mainList, "id");
									billNoCombo.setContainerDataSource(bic);
									billNoCombo.setItemCaptionPropertyId("sales_number");
								}
								billNoCombo.setInputPrompt(getPropertyName("select"));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			billNoCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(billNoCombo.getValue()!=null){
							if(supplierCustomerRadio.getValue()!=null){
								long cid=0;
								if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER){
									PurchaseModel mdl=new PurchaseDao().getPurchaseModel((Long)billNoCombo.getValue());
									cid=mdl.getNetCurrencyId().getId();
								}
								else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER){
									SalesModel mdl=new SalesDao().getSale((Long)billNoCombo.getValue());
									cid=mdl.getNetCurrencyId().getId();
								}
								amountField.currencySelect.setReadOnly(true);
								amountField.setNewValue(cid, 0);
							}
							else{
								amountField.setNewValue(getCurrencyID(), 0);
								amountField.setReadOnly(false);
							}
							
							List idList=new ArrayList();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								idList.add((Long)item.getItemProperty(TBC_PAYMENT_CHILD_ID).getValue());
							}
							int type=0;
							if(supplierCustomerRadio.getValue()!=null){
								if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER)
									type=SConstants.PURCHASE;
								else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER)
									type=SConstants.SALES;
							}
							List list=new ArrayList();
							table.setVisibleColumns(allHeaders);
							list=dao.getAllBankTransactionList(getOfficeID(), type, (Long)billNoCombo.getValue());
							if(list.size()>0){
								Iterator itr=list.iterator();
								while (itr.hasNext()) {
									
									BankDetailsInvoiceMapModel map = (BankDetailsInvoiceMapModel) itr.next();
									
									if(!idList.contains(map.getPaymentDetailsId())){
										String chequeNo="";
										Date chequeDate=null;
										String date="";
										double amount=0, convRate=0;
										long cid = 0;
										String currency="";
										long departmentId=0,divisionId=0;
										String department="";
										String division="";
										
										if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.SUPPLIER){
											BankAccountPaymentDetailsModel pdetmdl=new BankAccountPaymentDao().getBankAccountPaymentDetailsModel(map.getPaymentDetailsId());
											
											chequeNo=pdetmdl.getChequeNo();
											amount=pdetmdl.getAmount();
											cid=pdetmdl.getCurrencyId().getId();
											convRate=pdetmdl.getConversionRate();
											currency=pdetmdl.getCurrencyId().getCode();
											departmentId=pdetmdl.getDepartmentId();
											divisionId=pdetmdl.getDivisionId();
											if(pdetmdl.getChequeDate()!=null)
												chequeDate=pdetmdl.getChequeDate();
											if(pdetmdl.getDepartmentId()!=0)
												department=depDao.getDepartment(pdetmdl.getDepartmentId()).getName();
											else
												department="None";
											if(pdetmdl.getDivisionId()!=0)
												division=divDao.getDivision(pdetmdl.getDivisionId()).getName();
											else
												division="None";
										}
										else if ((Integer) supplierCustomerRadio.getValue() == SConstants.creditDebitNote.CUSTOMER){
											BankAccountDepositDetailsModel ddetmdl=new BankAccountDepositDao().getBankAccountDepositDetailsModel(map.getPaymentDetailsId());
											
											chequeNo=ddetmdl.getChequeNo();
											amount=ddetmdl.getAmount();
											cid=ddetmdl.getCurrencyId().getId();
											convRate=ddetmdl.getConversionRate();
											currency=ddetmdl.getCurrencyId().getCode();
											departmentId=ddetmdl.getDepartmentId();
											divisionId=ddetmdl.getDivisionId();
											if(ddetmdl.getDepartmentId()!=0)
												department=depDao.getDepartment(ddetmdl.getDepartmentId()).getName();
											else
												department="None";
											if(ddetmdl.getDivisionId()!=0)
												division=divDao.getDivision(ddetmdl.getDivisionId()).getName();
											else
												division="None";
											if(ddetmdl.getChequeDate()!=null)
												chequeDate=ddetmdl.getChequeDate();
										}
										if(chequeDate!=null)
											date=CommonUtil.formatDateToDDMMYYYY(chequeDate);
										
										table.addItem(new Object[]{new SCheckBox(null, false),
																	type,
																	(Long)billNoCombo.getValue(),
																	billNoCombo.getItemCaption(billNoCombo.getValue()),
																	map.getPaymentId(),
																	map.getPaymentDetailsId(),
																	chequeNo,
																	chequeDate,
																	date,
																	roundNumber(amount),
																	cid,
																	currency,
																	roundNumber(convRate),
																	departmentId,
																	department,
																	divisionId,
																	division,
																	false}, table.getItemIds().size()+1);
									}
								}
							}
							table.setVisibleColumns(requiredHeaders);
							calculateTotals();
							billNoCombo.setValue(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					chequeReturnCombo.setValue((long) 0);
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
							if(invoiceBillList.contains((Long) billNoCombo.getValue())){
								isAddable=false;
							}
							
							if(isAddable){
								table.addItem(new Object[] {
										table.getItemIds().size()+1,
										(Long) billNoCombo.getValue(),
										billNoCombo.getItemCaption(billNoCombo.getValue()),
										roundNumber(amountField.getValue()),
										amountField.getCurrency(),
										new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getCode(),
										roundNumber(amountField.getConversionRate()),
										departmentId,
										department,
										divisionId,
										division},table.getItemIds().size()+1);
								
								if(!invoiceBillList.contains((Long) billNoCombo.getValue())){
									invoiceBillList.add((Long) billNoCombo.getValue());
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
						billNoCombo.setValue((Long)item.getItemProperty(TBC_BILL_ID).getValue());
 						amountField.setNewValue((Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
 												(Double)item.getItemProperty(TBC_AMOUNT).getValue());
						departmentCombo.setValue(item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
						divisionCombo.setValue(item.getItemProperty(TBC_DIVISION_ID).getValue());
						amountField.currencySelect.setReadOnly(true);
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
							table.setVisibleColumns(allHeaders);
							Item item = table.getItem(table.getValue());
							
							boolean isAddable=true;
							if(invoiceBillList.contains((Long)item.getItemProperty(TBC_BILL_ID).getValue()))
								invoiceBillList.remove((Long)item.getItemProperty(TBC_BILL_ID).getValue());
							if(invoiceBillList.contains((Long) billNoCombo.getValue()))
								isAddable=false;
							
							long departmentId=0;
							long divisionId=0;
							String department="";
							String division="";
							
							department=departmentCombo.getItemCaption(departmentCombo.getValue());
							departmentId=(Long)departmentCombo.getValue();
							division=divisionCombo.getItemCaption();
							divisionId=(Long)divisionCombo.getValue();
							
							if(isAddable){
								item.getItemProperty(TBC_BILL_ID).setValue((Long)billNoCombo.getValue());
								item.getItemProperty(TBC_BILL_NO).setValue(billNoCombo.getItemCaption(billNoCombo.getValue()));
								item.getItemProperty(TBC_AMOUNT).setValue(roundNumber(amountField.getValue()));
								item.getItemProperty(TBC_CURRENCY_ID).setValue(amountField.getCurrency());
								item.getItemProperty(TBC_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getCode());
								item.getItemProperty(TBC_CONVERSION_RATE).setValue(roundNumber(amountField.getConversionRate()));
								item.getItemProperty(TBC_DEPARTMENT_ID).setValue(departmentId);
								item.getItemProperty(TBC_DEPARTMENT).setValue(department);
								item.getItemProperty(TBC_DIVISION_ID).setValue(divisionId);
								item.getItemProperty(TBC_DIVISION).setValue(division);
								
								if(!invoiceBillList.contains((Long) billNoCombo.getValue())){
									invoiceBillList.add((Long) billNoCombo.getValue());
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
							if (chequeReturnCombo.getValue() == null || chequeReturnCombo.getValue().toString().equals("0")) {
								ChequeReturnModel mdl = new ChequeReturnModel();
								boolean isSavable=false;
								table.setComponentError(null);
								List<ChequeReturnDetailsModel> childList=new ArrayList<ChequeReturnDetailsModel>();
								
								mdl.setSupplier_customer((Integer)supplierCustomerRadio.getValue());
								mdl.setLedger(new LedgerModel((Long)ledgerCombo.getValue()));
								mdl.setRef_no(refNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								mdl.setActive(true);
								
								Iterator itr = table.getItemIds().iterator();							
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									SCheckBox box=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
									if(box.getValue() || (Boolean)item.getItemProperty(TBC_IS_RETURNED).getValue()==true) {
										isSavable=true;
										ChequeReturnDetailsModel det=new ChequeReturnDetailsModel();
										det.setType((Integer)item.getItemProperty(TBC_TYPE).getValue());
										det.setBill_no((Long)item.getItemProperty(TBC_BILL_ID).getValue());
										det.setBill(item.getItemProperty(TBC_BILL_NO).getValue().toString());
										det.setPay_id((Long)item.getItemProperty(TBC_PAYMENT_ID).getValue());
										det.setPay_child_id((Long)item.getItemProperty(TBC_PAYMENT_CHILD_ID).getValue());
										det.setReturned((Boolean)item.getItemProperty(TBC_IS_RETURNED).getValue());
										det.setAmount(roundNumber((Double)item.getItemProperty(TBC_AMOUNT).getValue()));
										det.setCurrencyId(new CurrencyModel((Long)item.getItemProperty(TBC_CURRENCY_ID).getValue()));
										det.setConversionRate(roundNumber((Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()));
										det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
										det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
										det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
										if((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()!=null)
											det.setChequeDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()));
										else
											det.setChequeDate(null);
										childList.add(det);
									}
								}
								mdl.setCheque_return_list(childList);
								if(isSavable){
									long id = dao.save(mdl);
									saveActivity(getOptionId(), "Cheque Return Saved"
													+ id+ ", Return Amount : "
													+ table.getColumnFooter(TBC_AMOUNT).toString(),id);
									Notification.show("Success","Saved Successfully..!",Type.WARNING_MESSAGE);
									loadData(id);
								}
								else{
									setRequiredError(table, getPropertyName("invalid_selection"), true);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			chequeReturnCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						table.removeAllItems();
						invoiceBillList.clear();
						supplierCustomerRadio.setValue(SConstants.creditDebitNote.SUPPLIER);
						ledgerCombo.setValue(null);
						refNoField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(previousDate);
						amountField.setCurrencyDate(previousDate);
						amountField.setNewValue(getCurrencyID(),0);
						memoTextArea.setValue("");
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						supplierCustomerRadio.setValue(SConstants.creditDebitNote.SUPPLIER);
						resetItems();
						table.setVisibleColumns(allHeaders);
						if (chequeReturnCombo.getValue() != null && !chequeReturnCombo.getValue().toString().equals("0")) {

							ChequeReturnModel mdl = dao.getChequeReturnModel((Long) chequeReturnCombo.getValue());
							
							supplierCustomerRadio.setValue(mdl.getSupplier_customer());
							ledgerCombo.setValue(mdl.getLedger().getId());
							refNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							amountField.setCurrencyDate(mdl.getDate());
							amountField.setNewValue(getCurrencyID(),0);
							memoTextArea.setValue(mdl.getMemo());
							
							Iterator it = mdl.getCheque_return_list().iterator();
							while (it.hasNext()) {
								ChequeReturnDetailsModel det = (ChequeReturnDetailsModel) it.next();

								String date="";
								if(det.getChequeDate()!=null)
									date=CommonUtil.formatDateToDDMMYYYY(det.getChequeDate());
								
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
								
								table.addItem(new Object[]{new SCheckBox(null, false),
															det.getType(),
															det.getBill_no(),
															det.getBill(),
															det.getPay_id(),
															det.getPay_child_id(),
															det.getChequeNo(),
															det.getChequeDate(),
															date,
															roundNumber(det.getAmount()),
															det.getCurrencyId().getId(),
															det.getCurrencyId().getCode(),
															roundNumber(det.getConversionRate()),
															det.getDepartmentId(),
															department,
															det.getDivisionId(),
															division,
															det.isReturned()}, table.getItemIds().size()+1);
							}
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
							if (chequeReturnCombo.getValue() == null || chequeReturnCombo.getValue().toString().equals("0")) {
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
							if (chequeReturnCombo.getValue() != null && !chequeReturnCombo.getValue().toString().equals("0")) {
								boolean isSavable=false;
								table.setComponentError(null);
								ChequeReturnModel mdl = dao.getChequeReturnModel((Long) chequeReturnCombo.getValue());
								List<ChequeReturnDetailsModel> childList=new ArrayList<ChequeReturnDetailsModel>();
								
								mdl.setSupplier_customer((Integer)supplierCustomerRadio.getValue());
								mdl.setLedger(new LedgerModel((Long)ledgerCombo.getValue()));
								mdl.setRef_no(refNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								mdl.setActive(true);
								
								Iterator itr = table.getItemIds().iterator();							
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									SCheckBox box=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
									if(box.getValue() || (Boolean)item.getItemProperty(TBC_IS_RETURNED).getValue()==true) {
										isSavable=true;
										ChequeReturnDetailsModel det=new ChequeReturnDetailsModel();
										det.setType((Integer)item.getItemProperty(TBC_TYPE).getValue());
										det.setBill_no((Long)item.getItemProperty(TBC_BILL_ID).getValue());
										det.setBill(item.getItemProperty(TBC_BILL_NO).getValue().toString());
										det.setPay_id((Long)item.getItemProperty(TBC_PAYMENT_ID).getValue());
										det.setPay_child_id((Long)item.getItemProperty(TBC_PAYMENT_CHILD_ID).getValue());
										det.setReturned((Boolean)item.getItemProperty(TBC_IS_RETURNED).getValue());
										det.setAmount(roundNumber((Double)item.getItemProperty(TBC_AMOUNT).getValue()));
										det.setCurrencyId(new CurrencyModel((Long)item.getItemProperty(TBC_CURRENCY_ID).getValue()));
										det.setConversionRate(roundNumber((Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()));
										det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
										det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
										det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
										if((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()!=null)
											det.setChequeDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()));
										else
											det.setChequeDate(null);
										childList.add(det);
									}
								}
								mdl.setCheque_return_list(childList);
								if(isSavable){
									dao.update(mdl);
									saveActivity(getOptionId(), "Cheque Return Updated"
											+ mdl.getId()+ ", Return Amount : "
											+ table.getColumnFooter(TBC_AMOUNT).toString(),mdl.getId());
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadData(mdl.getId());
								}
								else{
									setRequiredError(table, getPropertyName("invalid_selection"), true);
								}
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

					if (chequeReturnCombo.getValue() != null && !chequeReturnCombo.getValue().toString() .equals("0")) {
						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.delete((Long) chequeReturnCombo.getValue());
										saveActivity(getOptionId(), "Cheque Return Deleted"
												+ (Long)chequeReturnCombo.getValue()+ ", Return Amount : "
												+ table.getColumnFooter(TBC_AMOUNT).toString(),(Long)chequeReturnCombo.getValue());
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

					if (chequeReturnCombo.getValue() != null && !chequeReturnCombo.getValue().toString().equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.cancel((Long) chequeReturnCombo.getValue());
										saveActivity(getOptionId(), "Cheque Return Cancelled"
												+ (Long)chequeReturnCombo.getValue()+ ", Return Amount : "
												+ table.getColumnFooter(TBC_AMOUNT).toString(),(Long)chequeReturnCombo.getValue());

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
				cancelButton.setVisible(false);
				Notification.show(getPropertyName("warning_financial_year"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadData(long id) {
		List list= new ArrayList();
		try {
			list.add(new ChequeReturnModel(0, "----Create New-----"));
			list.addAll(dao.getChequeReturnModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			chequeReturnCombo.setContainerDataSource(bic);
			chequeReturnCombo.setItemCaptionPropertyId("ref_no");
			chequeReturnCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes"})
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
			
		if (billNoCombo.getValue() == null || billNoCombo.getValue().equals("")) {
			setRequiredError(billNoCombo, getPropertyName("select_account"), true);
			ret = false;
		} 
		else
			setRequiredError(billNoCombo, null, false);
		
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
			table.setColumnFooter(TBC_AMOUNT,roundNumber(total)+" "+new CurrencyManagementDao().getselecteditem(getCurrencyID()).getCode());
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

		if (ledgerCombo.getValue() == null || ledgerCombo.getValue().equals("")) {
			setRequiredError(ledgerCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(ledgerCombo, null, false);

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
		return chequeReturnCombo;
	}

	
	public void setAccountDepositNumberList(SComboField chequeReturnCombo) {
		this.chequeReturnCombo = chequeReturnCombo;
	}

	
	@Override
	public SComboField getBillNoFiled() {
		return chequeReturnCombo;
	}

	
	public void resetItems(){
		amountField.currencySelect.setReadOnly(false);
		amountField.setValue(getCurrencyID(), 0.0);
		departmentCombo.setValue((long)0);
		divisionCombo.setValue((long)0);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
	}

}
