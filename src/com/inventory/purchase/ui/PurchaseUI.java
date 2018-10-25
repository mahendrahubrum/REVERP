package com.inventory.purchase.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.ClearingAgentDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.ClearingAgentModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.acct.ui.AddSupplier;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.GradeModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.UnitManagementUI;
import com.inventory.dao.LocationDao;
import com.inventory.dao.PrivilageSetupDao;
import com.inventory.finance.dao.PaymentModeDao;
import com.inventory.finance.model.PaymentModeModel;
import com.inventory.model.LocationModel;
import com.inventory.purchase.bean.PurchaseBean;
import com.inventory.purchase.bean.PurchaseGRNBean;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseOrderDao;
import com.inventory.purchase.model.PurchaseExpenseDetailsModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseOrderDetailsModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.inventory.ui.AddLocationUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyException;
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
import com.webspark.Components.ExpenditurePanel;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SComboSearchField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SSelectionField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.bean.ExpenseBean;
import com.webspark.bean.ExpenseTransactionBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author anil
 * @date 01-Dec-2015
 * @Project REVERP
 */

public class PurchaseUI extends SparkLogic {

	private static final long serialVersionUID = 7300632721366226830L;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_EDITABLE = "Editable";
	static String TBC_ITEM_ID = "Item Id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty Received";
	static String TBC_CONVERTION_QTY = "Convertion Qty";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";
	static String TBC_QTY_ORDERED = "Qty Ordered";
	static String TBC_BALANCE = "Balance";
	static String TBC_UNIT_ID = "Unit Id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_CID="Currency Id";
	static String TBC_CURRENCY="Currency";
	static String TBC_CONV_RATE="Conv Rate";
	
	static String TBC_DISCOUNT_TYPE = "Discount Type";
	static String TBC_DISCOUNT_PERCENTAGE = "Discount %";
	static String TBC_DISCOUNT_UNIT_PRICE = "Discount Unit Price";
	static String TBC_DISCOUNT = "Discount";
	static String TBC_TAX_ID = "Tax Id";
	static String TBC_TAX_PERCENTAGE = "Tax Percentage";
	static String TBC_TAX_AMOUNT = "Tax Amount";
	static String TBC_CESS = "Cess";
	static String TBC_NET_PRICE_WITHOUT_DISCOUNT = "Total Price";
	
	static String TBC_MANUFACTURING_DATE_ID="Manf Date Id";
	static String TBC_MANUFACTURING_DATE="Manf. Date";
	static String TBC_EXPIRY_DATE_ID="Exp. Date Id";
	static String TBC_EXPIRY_DATE="Exp. Date";
	static String TBC_GRADE_ID="Grade Id";
	static String TBC_GRADE="Grade";
	static String TBC_LOCATION_ID="Location Id";
	static String TBC_LOCATION="Location";
	static String TBC_BARCODE = "Barcode";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_BATCH_ID = "Batch Id";
	static String TBC_STOCK_ID = "Stock Id";
	static String TBC_ORDER_ID = "Order Id";
	static String TBC_ORDER_CHILD_ID = "Order Child Id";
	static String TBC_GRN_ID = "GRN Id";
	static String TBC_GRN_CHILD_ID = "GRN Child Id";

	PurchaseDao dao;
	SComboField purchaseCombo;
	STextField referenceNoField;
	SDateField dateField;
	SComboField locationMasterCombo;
	SButton newLocationButton;
	SComboField supplierCombo;
	SButton newSupplierButton;
	SComboField responsilbeEmployeeCombo;
	STextField contactField;
	
	SRadioButton paymentCreditRadio;
	SComboField paymentModeCombo;
	SRadioButton cashChequeRadio;
	SComboField purchaseAccountCombo;
	SDateField chequeDateField;
	STextField chequenumberField;
	
	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout formLayout;
	SComboSearchField a;

	STable table;

	SGridLayout itemLayout;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	SComboField itemCombo;
	SButton newItemButton;
	STextField quantityField;
	STextField convertionQuantityField;
	STextField barcodeField;
	STextField convertedQuantityField;
	SComboField departmentCombo;
	SSelectionField divisionCombo;
	
	SNativeSelect unitSelect;
	SButton newUnitButton;
	SButton unitMapButton;
	
	SCurrencyField unitPriceField;
//	STextField discountField;
	SNativeSelect taxSelect;
	
	SDateField manufacturingDateField;
	SDateField expiryDateField;
	SComboField gradeCombo;
	SComboField locationCombo;
	
	STextField netPriceField;

	SButton addItemButton;
	SButton updateItemButton;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;
	SButton printButton;
	SButton sendMailButton;
	SButton addExpenseButton;
	
	ExpenditurePanel expensePanel;
	
	SButton importOrderButton;
	SButton addOrderButton;
	SButton importGRNButton;
	SButton addGRNButton;
	SOptionGroup purchaseOrderOptions;	
	SWindow purchaseOrderWindow;

	SOptionGroup purchaseGRNOptions;	
	SWindow purchaseGRNWindow;
	
	ItemDao itemDao = new ItemDao();

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SWindow popupWindow;

	SCurrencyField payingAmountField;
	SCurrencyField netAmountField;
	SCurrencyField convertedField;
	STextField expenseField;
	STextArea commentArea;

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	boolean taxEnable = isTaxEnable();

	CommonMethodsDao comDao;
	ItemDao itmDao;
	TaxDao taxDao;
	UnitDao untDao;
	SupplierDao supDao;

	SButton createNewButton;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	Date previousDate;
	
	PrivilageSetupDao privDao;
	
	SRadioButton discountRadio;
	STextField discountPercentField;
	STextField discountAmountField;
	
	private SRadioButton itemDiscountRadio;
	private STextField itemDiscountPercentField;
	private STextField itemDiscountAmountField;
	
	@SuppressWarnings({"serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		previousDate=new Date();
		previousDate=getWorkingDate();
		dao=new PurchaseDao();
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		taxEnable = isTaxEnable();

		comDao = new CommonMethodsDao();
		itmDao = new ItemDao();
		taxDao = new TaxDao();
		untDao = new UnitDao();
		supDao=new SupplierDao();
		privDao=new PrivilageSetupDao();
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Purchase Inquiry");
		
		
		/*static String TBC_DISCOUNT = "Discount";
		static String TBC_TAX_ID = "Tax Id";
		static String TBC_TAX_PERCENTAGE = "Tax Percentage";
		static String TBC_TAX_AMT = "Tax Amount";
		static String TBC_CESS = "Cess";*/
		
		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_EDITABLE, TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME
				, TBC_QTY, TBC_CONVERTION_QTY, TBC_QTY_IN_BASIC_UNIT, TBC_QTY_ORDERED, TBC_BALANCE, 
				TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE, TBC_CID, TBC_CURRENCY, TBC_CONV_RATE,
				TBC_DISCOUNT_TYPE,TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT,TBC_DISCOUNT_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_PERCENTAGE, TBC_TAX_AMOUNT, TBC_CESS, TBC_NET_PRICE, TBC_MANUFACTURING_DATE_ID, 
				TBC_MANUFACTURING_DATE, TBC_EXPIRY_DATE_ID, TBC_EXPIRY_DATE, TBC_GRADE_ID, TBC_GRADE, TBC_LOCATION_ID, TBC_LOCATION, 
				TBC_BARCODE, TBC_BATCH_ID, TBC_STOCK_ID, TBC_ORDER_ID, TBC_ORDER_CHILD_ID, TBC_GRN_ID, TBC_GRN_CHILD_ID,TBC_NET_PRICE_WITHOUT_DISCOUNT };
		
		
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, 
					/*TBC_QTY_ORDERED, TBC_BALANCE, */ TBC_UNIT, TBC_UNIT_PRICE, TBC_CURRENCY,
					TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT,TBC_DISCOUNT_UNIT_PRICE,
					TBC_TAX_AMOUNT, TBC_CESS, TBC_MANUFACTURING_DATE, TBC_EXPIRY_DATE, 
					TBC_GRADE, TBC_LOCATION, TBC_BARCODE, TBC_NET_PRICE,TBC_NET_PRICE_WITHOUT_DISCOUNT };

		List<Object> tempList = new ArrayList<Object>();
		Collections.addAll(tempList, requiredHeaders);
		
		
		if(!settings.isDISCOUNT_ENABLE()){
			tempList.remove(TBC_DISCOUNT);
			tempList.remove(TBC_DISCOUNT_PERCENTAGE);
			tempList.remove(TBC_DISCOUNT_UNIT_PRICE);
			tempList.remove(TBC_NET_PRICE_WITHOUT_DISCOUNT);
		}
		
		if(!settings.isTAX_ENABLED()){
			tempList.remove(TBC_TAX_AMOUNT);
		}
		
		if(!settings.isCESS_ENABLED()){
			tempList.remove(TBC_CESS);
		}
		
		if(!settings.isGRADING_ENABLED()){
			tempList.remove(TBC_GRADE);
		}
		if(!settings.isBARCODE_ENABLED()){
			tempList.remove(TBC_BARCODE);
		}
		if (!isManufDateEnable()) {
			tempList.remove(TBC_MANUFACTURING_DATE);
			tempList.remove(TBC_EXPIRY_DATE);
		}
		
		requiredHeaders = tempList.toArray(new String[tempList.size()]);
		setSize(1465, 645);

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		vLayout = new SVerticalLayout();
		formLayout = new SFormLayout();

		itemLayout = new SGridLayout(20, 2);
//		itemLayout.setSizeFull();

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(16);
		masterDetailsGrid.setRows(6);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(16);
		bottomGrid.setRows(2);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(8);
		buttonsGrid.setRows(1);

		qtyTotal = new SLabel(null);
		taxTotal = new SLabel(null);
		netTotal = new SLabel(null);
		qtyTotal.setValue("0.0");
		taxTotal.setValue("0.0");
		netTotal.setValue("0.0");

		pannel.setSizeFull();
		formLayout.setSizeFull();

		try {
			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);
			purchaseCombo = new SComboField(null, 150, null, "id","ref_no", false, getPropertyName("create_new"));
			referenceNoField = new STextField(null, 150);
			dateField = new SDateField(null, 100, getDateFormat());
			supplierCombo = new SComboField(null, 150, new LedgerDao().getAllSuppliers(getOfficeID()), 
					"id", "name", true, getPropertyName("select"));
			newSupplierButton = new SButton();
			newSupplierButton.setStyleName("addNewBtnStyle");
			newSupplierButton.setDescription("Add New Supplier");
			responsilbeEmployeeCombo = new SComboField(null,150,
					new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name", false, getPropertyName("select"));
			contactField = new STextField(null, 150);
			contactField.setReadOnly(true);
			
			List departmentList=new ArrayList();
			departmentList.add(0, new DepartmentModel(0, "None"));
			departmentList.addAll(new DepartmentDao().getDepartments(getOrganizationID()));
			divisionCombo = new SSelectionField(null,getPropertyName("none"),200, 400);
			divisionCombo.setContainerData(new DivisionDao().getDivisionsHierarchy(getOrganizationID()));
			departmentCombo = new SComboField(null, 100, departmentList,"id", "name", false, getPropertyName("select"));
			departmentCombo.setValue((long)0);
			
			paymentCreditRadio=new SRadioButton(null, 200, SConstants.paymentMode.paymentModeList, "key", "value");
			paymentCreditRadio.setHorizontal(true);
			paymentModeCombo=new SComboField(getPropertyName("payment_mode"), 150, new PaymentModeDao().getAllPaymentModeList(getOfficeID()), "id", "description", true, getPropertyName("select"));
			cashChequeRadio=new SRadioButton(null, 200, SConstants.paymentMode.cashChequeList, "key", "value");
			cashChequeRadio.setHorizontal(true);
			paymentCreditRadio.setImmediate(true);
			cashChequeRadio.setImmediate(true);
			
			purchaseAccountCombo = new SComboField("Purchase Account", 150,new LedgerDao().getAllActiveGeneralLedgerOnly(getOfficeID()), "id", "name", true, getPropertyName("select"));
			purchaseAccountCombo.setValue(settings.getPURCHASE_ACCOUNT());
			
			List clearingList=new ArrayList();
			clearingList.add(0, new ClearingAgentModel(0, "None"));
			clearingList.addAll(new ClearingAgentDao().getAllActiveClearingAgentNamesWithLedgerID(getOfficeID()));
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(purchaseCombo);
			salLisrLay.addComponent(createNewButton);
			
			SHorizontalLayout supplierLayout = new SHorizontalLayout();
			supplierLayout.addComponent(supplierCombo);
			supplierLayout.addComponent(newSupplierButton);
			
			newLocationButton = new SButton();
			newLocationButton.setStyleName("addNewBtnStyle");
			newLocationButton.setDescription("Add New Location");
			
			List locationList=new ArrayList();
			locationList.add(0, new LocationModel(0, "None"));
			locationList.addAll(new LocationDao().getLocationModelList(getOfficeID()));
			locationMasterCombo=new SComboField(null, 100, locationList, "id", "name", true, getPropertyName("select"));
			locationMasterCombo.setValue((long)0);
			
			SHorizontalLayout locationLayout = new SHorizontalLayout();
			locationLayout.addComponent(locationMasterCombo);
			locationLayout.addComponent(newLocationButton);
			
			masterDetailsGrid.addComponent(new SLabel("Purchase No"), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(referenceNoField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			masterDetailsGrid.addComponent(dateField, 6, 0);
			masterDetailsGrid.addComponent(new SLabel("Location"), 7, 0);
			masterDetailsGrid.addComponent(locationLayout, 8, 0);
			
			chequeDateField=new SDateField(getPropertyName("Cheque Date"),100,getDateFormat(),getWorkingDate());
			chequenumberField=new STextField(getPropertyName("Cheque No"),100);
			chequeDateField.setVisible(false);
			chequenumberField.setVisible(false);
			
			List<KeyValue> discountMethod = Arrays.asList(new KeyValue(1, "Percentage"), new KeyValue(2, "Amount"));
			discountRadio=new SRadioButton(getPropertyName("discount_type"), 200, discountMethod, "intKey", "value");
		//	discountRadio.setValue(1);
			
			discountPercentField=new STextField(getPropertyName("percentage"), 100);
			discountAmountField=new STextField(getPropertyName("amount"), 100);
			discountPercentField.setStyleName("textfield_align_right");
			discountAmountField.setStyleName("textfield_align_right");
			discountRadio.setHorizontal(true);
			discountRadio.setImmediate(true);
			discountPercentField.setImmediate(true);
			discountAmountField.setImmediate(true);
			
			SHorizontalLayout radioLayout=new SHorizontalLayout();
			radioLayout.setSpacing(true);
			radioLayout.addComponent(purchaseAccountCombo);
			radioLayout.addComponent(paymentCreditRadio);
			radioLayout.addComponent(paymentModeCombo);
			if(settings.isDISCOUNT_ENABLE()){
				SHorizontalLayout discLay=new SHorizontalLayout();
				discLay.setSpacing(true);
				discLay.setStyleName("layout_light_bordered");
				discLay.addComponent(discountRadio);
				discLay.addComponent(discountPercentField);
				discLay.addComponent(discountAmountField);
				radioLayout.addComponent(discLay);
			}
			radioLayout.addComponent(cashChequeRadio);
			radioLayout.addComponent(chequenumberField);
			radioLayout.addComponent(chequeDateField);
			radioLayout.setComponentAlignment(cashChequeRadio, Alignment.MIDDLE_CENTER);
			radioLayout.setComponentAlignment(paymentCreditRadio, Alignment.MIDDLE_CENTER);

			
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(referenceNoField,Alignment.MIDDLE_LEFT);
			masterDetailsGrid.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 1.25f);
			masterDetailsGrid.setColumnExpandRatio(2, 1);
			masterDetailsGrid.setColumnExpandRatio(3, 1.25f);
			masterDetailsGrid.setColumnExpandRatio(4, 1);
			masterDetailsGrid.setColumnExpandRatio(5, 1.25f);
			masterDetailsGrid.setColumnExpandRatio(6, 1);
			masterDetailsGrid.setColumnExpandRatio(7, 1.25f);
			masterDetailsGrid.setColumnExpandRatio(8, 1);
			masterDetailsGrid.setColumnExpandRatio(9, 1);
			masterDetailsGrid.setColumnExpandRatio(10, 1);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("supplier")), 1, 1);
			masterDetailsGrid.addComponent(supplierLayout, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("employee")), 3, 1);
			masterDetailsGrid.addComponent(responsilbeEmployeeCombo, 4, 1);
		
			if(settings.isDIVISION_ENABLED()){
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("Division")), 5, 1);
				masterDetailsGrid.addComponent(divisionCombo, 6, 1);
			}
			
			if(settings.isDEPARTMENT_ENABLED()){
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("department")), 7,1);
				masterDetailsGrid.addComponent(departmentCombo, 8, 1);
			}
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("contact")), 1, 2);
			masterDetailsGrid.addComponent(contactField, 2, 2);
			masterDetailsGrid.setStyleName("master_border");
			
			if(settings.isSHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE()){
				itemCombo = new SComboField(getPropertyName("item"), 100,
						null, "id", "name",true,getPropertyName("select"));
			} else {
				itemCombo = new SComboField(getPropertyName("item"), 100,
						itmDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()), "id", "name",true,getPropertyName("select"));
			}
//			itemCombo = new SComboField(getPropertyName("item"), 100,
//					itmDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()), "id", "name",true,getPropertyName("select"));
			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");
			newItemButton.setDescription("Add New Item");
			
			
			SHorizontalLayout itemLay = new SHorizontalLayout();
			itemLay.addComponent(itemCombo);
			itemLay.addComponent(newItemButton);
			
			itemLay.setComponentAlignment(newItemButton, Alignment.BOTTOM_CENTER);
			
			quantityField = new STextField(TBC_QTY, 60);
			quantityField.setStyleName("textfield_align_right");
			quantityField.setValue("0");
			barcodeField = new STextField(TBC_BARCODE, 50);
			
			convertionQuantityField = new STextField(getPropertyName("convertion_qty"), 40);
			convertionQuantityField.setStyleName("textfield_align_right");
			convertionQuantityField.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");
			
			manufacturingDateField=new SDateField("Manufacturing Date", 100, getDateFormat(), getWorkingDate());
			expiryDateField=new SDateField("Expiry Date", 100, getDateFormat(), getWorkingDate());

			List gradeList=new ArrayList();
			gradeList.add(0, new GradeModel(0, "None"));
			gradeList.addAll(new GradeDao().getAllGrades(getOfficeID()));
			gradeCombo=new SComboField("Grade", 100, gradeList, "id", "name", true, getPropertyName("select"));
			gradeCombo.setValue((long)0);
			
			locationCombo=new SComboField("Location", 100, locationList, "id", "name", true, getPropertyName("select"));
			locationCombo.setValue((long)0);
			
			convertedQuantityField = new STextField(getPropertyName("converted_qty"), 60);
			convertedQuantityField.setStyleName("textfield_align_right");
			convertedQuantityField.setDescription("Converted Quantity in Basic Unit");
			convertedQuantityField.setReadOnly(true);
			
			
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			newUnitButton = new SButton();
			newUnitButton.setStyleName("smallAddNewBtnStyle");
			newUnitButton.setDescription(getPropertyName("add_new_unit"));
			unitMapButton = new SButton();
			unitMapButton.setStyleName("mapBtnStyle");
			unitMapButton.setDescription(getPropertyName("set_convertion_quantity"));
			
			taxSelect = new SNativeSelect(getPropertyName("tax"), 75,taxDao.getAllActiveTaxesFromType(getOfficeID(),SConstants.tax.PURCHASE_TAX), "id", "name");
			taxSelect.setValue(SConstants.tax.PURCHASE_TAX);
//			discountField = new STextField(getPropertyName("discount"),50);
//			discountField.setValue("0");
			unitPriceField=new SCurrencyField("Unit Price", 50, getWorkingDate());
			unitPriceField.setStyleName("textfield_align_right");
			unitPriceField.currencySelect.setReadOnly(true);
			unitPriceField.setNewValue(getCurrencyID(), 0);
			
			netPriceField = new STextField(getPropertyName("net_price"),75);
			netPriceField.setNewValue("0.00");
			netPriceField.setStyleName("textfield_align_right");
			
			netAmountField = new SCurrencyField(null, 100, getWorkingDate());
			netAmountField.amountField.setReadOnly(true);
			netAmountField.setStyleName("textfield_align_right");
			convertedField = new SCurrencyField(null, 75,getWorkingDate());
			convertedField.setReadOnly(true);
			
			payingAmountField = new SCurrencyField(null, 100, getWorkingDate());
			payingAmountField.currencySelect.setReadOnly(true);
			payingAmountField.rateButton.setVisible(false);
			payingAmountField.setStyleName("textfield_align_right");
			payingAmountField.setNewValue(getCurrencyID(), 0);
			
			expenseField = new STextField(null, 100, "0.0");
			expenseField.setReadOnly(true);
			expenseField.setStyleName("textfield_align_right");

			netPriceField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			SHorizontalLayout unitLayout = new SHorizontalLayout();
			unitLayout.addComponent(unitSelect);
			unitLayout.addComponent(vert);
			
			itemDiscountRadio = new SRadioButton(getPropertyName("discount_type"), 80, discountMethod,"intKey", "value");
//			itemDiscountRadio.setHorizontal(true);
			itemDiscountRadio.setImmediate(true);
			itemDiscountRadio.setId("itemDiscountRadio");

			itemDiscountPercentField = new STextField(getPropertyName("percentage"), 50);
			itemDiscountPercentField.setStyleName("textfield_align_right");
			itemDiscountPercentField.setImmediate(true);
			itemDiscountPercentField.setId("itemDiscountPercentField");

			itemDiscountAmountField = new STextField(getPropertyName("amount"),60);
			itemDiscountAmountField.setStyleName("textfield_align_right");
			itemDiscountAmountField.setImmediate(true);
			itemDiscountAmountField.setId("itemDiscountAmountField");
			
			itemLayout.addComponent(itemLay, 1, 1);
			itemLayout.addComponent(quantityField, 3, 1);
			itemLayout.addComponent(unitLayout, 4, 1);
			itemLayout.addComponent(convertionQuantityField, 5, 1);
			itemLayout.addComponent(convertedQuantityField, 6 ,1);
			itemLayout.addComponent(unitPriceField, 8, 1);
			if (isDiscountEnable()) {
				SHorizontalLayout discLay=new SHorizontalLayout();
				discLay.setSpacing(true);
				discLay.setStyleName("layout_light_bordered");
				discLay.addComponent(itemDiscountRadio);
				discLay.addComponent(itemDiscountPercentField);
				discLay.addComponent(itemDiscountAmountField);
				itemLayout.addComponent(discLay,9,1);
			}
			if(isTaxEnable()){
				itemLayout.addComponent(taxSelect, 10, 1);
			}
			if(isManufDateEnable()){
				itemLayout.addComponent(manufacturingDateField, 11, 1);
				itemLayout.addComponent(expiryDateField, 12, 1);
			}
			if(settings.isGRADING_ENABLED()){
				itemLayout.addComponent(gradeCombo, 13, 1);
			}
			itemLayout.addComponent(locationCombo, 14, 1);
			if(settings.isBARCODE_ENABLED()){
				itemLayout.addComponent(barcodeField, 15, 1);
			}
			itemLayout.addComponent(netPriceField, 16, 1);
			itemLayout.addComponent(buttonLay, 17, 1);
			itemLayout.setComponentAlignment(buttonLay, Alignment.BOTTOM_CENTER);
			
			convertionQuantityField.setValue("1");
			convertionQuantityField.setVisible(false);
			convertedQuantityField.setVisible(false);
			convertionQuantityField.setImmediate(true);
			convertedQuantityField.setImmediate(true);
			
			

			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			itemLayout.setStyleName("po_border");

			formLayout.setStyleName("po_style");

			table = new STable(null, 1100, 150);

			table.setMultiSelect(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null,TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_EDITABLE, Boolean.class, null,TBC_EDITABLE, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null, getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_CONVERTION_QTY, Double.class, null,"Convertion Qty", null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class, null,"Qty Basic Unit", null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_ORDERED, Double.class, null,TBC_QTY_ORDERED, null, Align.LEFT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,TBC_BALANCE, null, Align.LEFT);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null, TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null, getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null, getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CID, Long.class, null, TBC_CID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY, String.class, null, TBC_CURRENCY, null, Align.CENTER);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null, TBC_CONV_RATE, null, Align.LEFT);
			table.addContainerProperty(TBC_DISCOUNT_TYPE, Integer.class, null,"Discount Type", null, Align.CENTER);
			table.addContainerProperty(TBC_DISCOUNT_PERCENTAGE, Double.class,null, getPropertyName("discount") + " %", null,Align.CENTER);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null, getPropertyName("discount"), null, Align.LEFT);
			table.addContainerProperty(TBC_DISCOUNT_UNIT_PRICE, Double.class, null,getPropertyName("discounted_price"), null, Align.CENTER);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null, TBC_TAX_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_PERCENTAGE, Double.class, null, TBC_TAX_PERCENTAGE, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_AMOUNT, Double.class, null, TBC_TAX_AMOUNT, null, Align.LEFT);
			table.addContainerProperty(TBC_CESS, Double.class, null, TBC_CESS, null, Align.LEFT);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null, getPropertyName("net_price"), null, Align.LEFT);
			table.addContainerProperty(TBC_NET_PRICE_WITHOUT_DISCOUNT, Double.class, null, getPropertyName("total_price"), null, Align.LEFT);
			table.addContainerProperty(TBC_MANUFACTURING_DATE_ID, Date.class, null, TBC_MANUFACTURING_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_MANUFACTURING_DATE, String.class, null, TBC_MANUFACTURING_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_EXPIRY_DATE_ID, Date.class, null, TBC_EXPIRY_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_EXPIRY_DATE, String.class, null, TBC_EXPIRY_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_GRADE_ID, Long.class, null, TBC_GRADE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_GRADE, String.class, null, TBC_GRADE, null, Align.LEFT);
			table.addContainerProperty(TBC_LOCATION_ID, Long.class, null, TBC_LOCATION_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_LOCATION, String.class, null, TBC_LOCATION, null, Align.LEFT);
			table.addContainerProperty(TBC_BARCODE, String.class, null, TBC_BARCODE, null, Align.LEFT);
			table.addContainerProperty(TBC_BATCH_ID, Long.class, null,TBC_BATCH_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,TBC_STOCK_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ORDER_ID, Long.class, null,TBC_ORDER_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ORDER_CHILD_ID, Long.class, null,TBC_ORDER_CHILD_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_GRN_ID, Long.class, null,TBC_GRN_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_GRN_CHILD_ID, Long.class, null,TBC_GRN_CHILD_ID, null, Align.CENTER);
			
			table.setColumnExpandRatio(TBC_SN, 0.35f);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 0.5f);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 1.2f);
			table.setColumnExpandRatio(TBC_QTY, 0.5f);
			table.setColumnExpandRatio(TBC_QTY_ORDERED, 0.5f);
			table.setColumnExpandRatio(TBC_BALANCE, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 0.7f);
			
			table.setColumnExpandRatio(TBC_DISCOUNT, 0.75f);
			table.setColumnExpandRatio(TBC_DISCOUNT_UNIT_PRICE, 0.75f);
			table.setColumnExpandRatio(TBC_TAX_AMOUNT, 0.75f);
			table.setColumnExpandRatio(TBC_CESS, 0.75f);
			
			table.setColumnExpandRatio(TBC_CURRENCY, 0.75f);
			table.setColumnExpandRatio(TBC_NET_PRICE, 0.95f);
			table.setColumnExpandRatio(TBC_NET_PRICE_WITHOUT_DISCOUNT, 0.95f);
			
			table.setColumnExpandRatio(TBC_MANUFACTURING_DATE, 1f);
			table.setColumnExpandRatio(TBC_EXPIRY_DATE, 1f);
			table.setColumnExpandRatio(TBC_GRADE, 1f);
			table.setColumnExpandRatio(TBC_LOCATION, 1f);
			table.setColumnExpandRatio(TBC_BARCODE, 1f);
			

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			calculateTotals();

			table.setPageLength(table.size());

			table.setWidth("1250");
			table.setHeight("150");

			commentArea = new STextArea(null, 250, 40);
			
			

//			bottomGrid.addComponent(new SLabel(""), 6, 0);
			
			addExpenseButton=new SButton(null,"Add Expenses");
			addExpenseButton.setPrimaryStyleName("addBtnStyle");
			
			SHorizontalLayout expenseLayout=new SHorizontalLayout();
			expenseLayout.addComponent(expenseField);
			expenseLayout.addComponent(addExpenseButton);
			
			
			SHorizontalLayout commentLayout=new SHorizontalLayout();
			commentLayout.setSpacing(true);
			SHorizontalLayout expenseMainLayout=new SHorizontalLayout();
			expenseMainLayout.setSpacing(true);
			final SHorizontalLayout payingAmountLayout=new SHorizontalLayout();
			payingAmountLayout.setSpacing(true);
			SHorizontalLayout netAmountLayout=new SHorizontalLayout();
			netAmountLayout.setSpacing(true);
			
			commentLayout.addComponent(new SLabel(getPropertyName("comment")));
			commentLayout.addComponent(commentArea);
			
			
			
			expenseMainLayout.addComponent(new SLabel(getPropertyName("Expense")));
			expenseMainLayout.addComponent(expenseLayout);
			
			payingAmountLayout.addComponent(new SLabel(getPropertyName("paying_amount")));
			payingAmountLayout.addComponent(payingAmountField);
			payingAmountLayout.setVisible(true);
			
			netAmountLayout.addComponent(new SLabel(getPropertyName("net_amount")));
			netAmountLayout.addComponent(netAmountField);
			
			bottomGrid.addComponent(commentLayout, 0, 1);
			bottomGrid.addComponent(expenseMainLayout, 1, 1);
			bottomGrid.addComponent(payingAmountLayout, 2, 1);
			bottomGrid.addComponent(convertedField, 3, 1);
			bottomGrid.addComponent(netAmountLayout, 4, 1);
			convertedField.setVisible(false);
			
//			bottomGrid.setColumnExpandRatio(0, 0.75f);
//			bottomGrid.setColumnExpandRatio(1, 1f);
//			bottomGrid.setColumnExpandRatio(2, 0.5f);
//			bottomGrid.setColumnExpandRatio(3, 0.5f);
//			bottomGrid.setColumnExpandRatio(4, 1f);
//			bottomGrid.setColumnExpandRatio(5, 0.5f);
//			bottomGrid.setColumnExpandRatio(6, 0.5f);
//			bottomGrid.setColumnExpandRatio(7, 0.5f);
			
			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			importOrderButton = new SButton(null, "Import From Purchase Order");
			importOrderButton.setStyleName("importButtonStyle");
			addOrderButton = new SButton(null,"Import Purchase Orders");
			addOrderButton.setStyleName("importButtonStyle");
			
			importGRNButton = new SButton(null, "Import From GRN");
			importGRNButton.setStyleName("importButtonStyleNew");
			addGRNButton = new SButton(null,"Import GRN");
			addGRNButton.setStyleName("importButtonStyleNew");
			
			purchaseOrderOptions = new SOptionGroup("Select Purchase Order : ", 300, null, "id", "name", true);
			purchaseOrderWindow = new SWindow("Purchase Order", 650, 250);
			purchaseOrderWindow.center();
			purchaseOrderWindow.setResizable(false);
			purchaseOrderWindow.setModal(true);
			
			purchaseGRNOptions = new SOptionGroup("Select Purchase GRN : ", 300, null, "id", "name", true);
			purchaseGRNWindow = new SWindow("Purchase GRN", 650, 250);
			purchaseGRNWindow.center();
			purchaseGRNWindow.setResizable(false);
			purchaseGRNWindow.setModal(true);
			
			SFormLayout popUpLayout = new SFormLayout();
			popUpLayout.setSpacing(true);
			popUpLayout.setMargin(true);
			popUpLayout.addComponent(purchaseOrderOptions);
			popUpLayout.addComponent(addOrderButton);
			purchaseOrderWindow.setContent(popUpLayout);
			
			SFormLayout popUpLayoutNew = new SFormLayout();
			popUpLayoutNew.setSpacing(true);
			popUpLayoutNew.setMargin(true);
			popUpLayoutNew.addComponent(purchaseGRNOptions);
			popUpLayoutNew.addComponent(addGRNButton);
			purchaseGRNWindow.setContent(popUpLayoutNew);
			
			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			cancelButton = new SButton(getPropertyName("cancel"), 78);
			cancelButton
					.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			cancelButton.setStyleName("deletebtnStyle");

			sendMailButton = new SButton(getPropertyName("send_mail"), 100);
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");
			
			expensePanel=new ExpenditurePanel("Expenses");

			printButton = new SButton(getPropertyName("print"), 78);
			printButton.setIcon(new ThemeResource("icons/print.png"));
			printButton.setStyleName("deletebtnStyle");

			supplierLayout.addComponent(importGRNButton);
			supplierLayout.setComponentAlignment(importGRNButton, Alignment.TOP_CENTER);
			supplierLayout.addComponent(importOrderButton);
			supplierLayout.setComponentAlignment(importOrderButton, Alignment.TOP_CENTER);
			
			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveButton);
			mainButtonLayout.addComponent(updateButton);
			if (settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(cancelButton);
			else
				mainButtonLayout.addComponent(deleteButton);
			mainButtonLayout.addComponent(printButton);
			mainButtonLayout.addComponent(sendMailButton);

			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			cancelButton.setVisible(false);
			sendMailButton.setVisible(false);
			printButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			boolean updateAvail=true;
			boolean printAvail=true;
			if(!isSuperAdmin()){
				try {
					updateAvail = privDao.isFacilityAccessibleToUser(
							getOfficeID(),
							SConstants.privilegeTypes.EDIT_PURCHASE, getLoginID());
				} catch (Exception e) {
					updateAvail=false;
				}
				try {
					printAvail = privDao.isFacilityAccessibleToUser(
							getOfficeID(),
							SConstants.privilegeTypes.PRINT_PURCHASE, getLoginID());
				} catch (Exception e) {
					printAvail=false;
				}
			}
			if(!updateAvail){
				updateButton.setEnabled(false);
			}
			if(!printAvail){
				printButton.setEnabled(false);
			}
			
			formLayout.addComponent(masterDetailsGrid);
			formLayout.addComponent(table);
			formLayout.addComponent(itemLayout);
			formLayout.addComponent(radioLayout);
			formLayout.addComponent(bottomGrid);
			formLayout.addComponent(buttonsGrid);

			formLayout.setWidth("700");

			hLayout.addComponent(popupLay);
			hLayout.addComponent(formLayout);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			
			pannel.setContent(windowNotif);
				
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(dateField.getValue()!=null){
						if(previousDate.getTime()!=dateField.getValue().getTime()){
							final long id=(Long)netAmountField.currencySelect.getValue();
							final long cid=(Long)unitPriceField.currencySelect.getValue();
							if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID()){
								ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are You Sure ? Update Currency Rate Accordingly.",new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (!dialog.isConfirmed()) {
											dateField.setValue(previousDate);
										}
										previousDate=dateField.getValue();
										payingAmountField.setCurrencyDate(previousDate);
										netAmountField.setCurrencyDate(previousDate);
										unitPriceField.setCurrencyDate(previousDate);
										netAmountField.currencySelect.setValue(null);
										netAmountField.currencySelect.setValue(id);
										unitPriceField.currencySelect.setNewValue(null);
										unitPriceField.currencySelect.setNewValue(cid);
										expensePanel.setCurrency(netAmountField.getCurrency());
										expensePanel.amountField.setCurrencyDate(previousDate);
										expensePanel.reloadConvertionRate();
										double expense=expensePanel.getAmount();
										if(expense<0){
											getUI().addWindow(expensePanel);
											SNotification.show("Enter Expenses", Type.ERROR_MESSAGE);
										}
										else{
											expenseField.setNewValue(roundNumber(expensePanel.getDebitAmount())+"");
										}
									}
								});
							}
							netAmountField.currencySelect.setValue(null);
							netAmountField.currencySelect.setValue(id);
							unitPriceField.currencySelect.setNewValue(null);
							unitPriceField.currencySelect.setNewValue(cid);
						}
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			loadOptions(0);
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)purchaseCombo.getValue(),confirmBox.getUserID());
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
						if(purchaseCombo.getValue()!=null && !purchaseCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)purchaseCombo.getValue(),
									"Purchase Order : No. "+purchaseCombo.getItemCaption(purchaseCombo.getValue()));
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
						if(purchaseCombo.getValue()!=null && !purchaseCombo.getValue().toString().equals("0")) {
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
			
			
			netAmountField.currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(netAmountField.currencySelect.getValue()!=null){
							if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID()){
								convertedField.setVisible(true);
							}
							else{
								convertedField.setVisible(false);
							}
							netAmountField.setNewValue(roundNumber(convertedField.getValue()*netAmountField.getConversionRate()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			netAmountField.conversionField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(netAmountField.conversionField.getValue()!=null){
							netAmountField.setNewValue(roundNumber(convertedField.getValue()*netAmountField.getConversionRate()));
						}
					} catch (ReadOnlyException e) {
						e.printStackTrace();
					}
				}
			});
			
			
			convertedField.amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						netAmountField.setNewValue(roundNumber(convertedField.getValue()*netAmountField.getConversionRate()));
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}
			});
			netAmountField.setNewValue(getCurrencyID(), 0);
			
			table.addShortcutListener(new ShortcutListener("Submit Item", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			
			/*table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			
			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});*/

			
			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
				}
			});

			
			final Action actionDelete = new Action("Delete");

			
			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

			
			newItemButton.addClickListener(new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					ItemPanel itemPanel = new ItemPanel();
					popupWindow.setContent(itemPanel);
					popupWindow.setId("ITEM");
					popupWindow.center();
					popupWindow.setCaption("Add New Item");
					getUI().getCurrent().addWindow(popupWindow);
				}
			});
			
			
			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					try {
						if (popupWindow.getId().equals("ITEM")) {
							List list=new ArrayList();
							list=itmDao.getAllActiveItemsWithAppendingItemCode(getOfficeID());
							SCollectionContainer bic=SCollectionContainer.setList(list, "id");
							itemCombo.setContainerDataSource(bic);
							itemCombo.setItemCaptionPropertyId("name");
							itemCombo.setInputPrompt(getPropertyName("select"));
							Object temp = null;
							if(unitSelect.getValue()!=null)
								temp=unitSelect.getValue();
							unitSelect.setValue(null);
							unitSelect.setValue(temp);
						}
						else if (popupWindow.getId().equals("UNIT")) {
							if(itemCombo.getValue()!=null){
								ItemModel itm=new ItemDao().getItem((Long)itemCombo.getValue());
								SCollectionContainer bic = SCollectionContainer.setList(comDao.getAllItemUnitDetails(itm.getId()), "id");
								unitSelect.setContainerDataSource(bic);
								unitSelect.setItemCaptionPropertyId("symbol");
								unitSelect.setValue(null);
								unitSelect.setValue(itm.getUnit().getId());
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			});
			
			
			final CloseListener locationCloseListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					try {
						List locationList=new ArrayList();
						locationList.add(0, new LocationModel(0, "None"));
						locationList.addAll(new LocationDao().getLocationModelList(getOfficeID()));
						SCollectionContainer bic=SCollectionContainer.setList(locationList, "id");
						locationMasterCombo.setContainerDataSource(bic);
						locationMasterCombo.setItemCaptionPropertyId("name");
						locationMasterCombo.setInputPrompt(getPropertyName("select"));
						locationCombo.setContainerDataSource(bic);
						locationCombo.setItemCaptionPropertyId("name");
						locationCombo.setInputPrompt(getPropertyName("select"));
						locationMasterCombo.setValue((long)0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};
			
			
			newLocationButton.addClickListener(new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					AddLocationUI location=new AddLocationUI();
					location.center();
					location.setCaption("Add New Location");
					getUI().getCurrent().addWindow(location);
					location.addCloseListener(locationCloseListener);
					location.setModal(true);
				}
			});
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					try {
						List list=new ArrayList();
						list=new LedgerDao().getAllSuppliers(getOfficeID());
						SCollectionContainer bic=SCollectionContainer.setList(list, "id");
						supplierCombo.setContainerDataSource(bic);
						supplierCombo.setItemCaptionPropertyId("name");
						supplierCombo.setInputPrompt(getPropertyName("select"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};
			
			
			newSupplierButton.addClickListener(new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					AddSupplier supplier=new AddSupplier();
					supplier.center();
					supplier.setCaption("Add Supplier");
					getUI().getCurrent().addWindow(supplier);
					supplier.addCloseListener(closeListener);
				}
			});

			
			expenseField.setImmediate(true);
			expenseField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						double expense=0;
						try {
							expense=toDouble(expenseField.getValue().toString());
						} catch (Exception e) {
							expense=0;
						}
						convertedField.setNewValue(roundNumber(toDouble(table.getColumnFooter(TBC_NET_PRICE))+expense));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
			
			addExpenseButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().addWindow(expensePanel);				
				}
			});

			
			expensePanel.addCloseListener(new CloseListener() {
				
				@Override
				public void windowClose(CloseEvent e) {
					double expense=0;
					try {
						expense=expensePanel.getAmount();
						if(expense<0){
							getUI().addWindow(expensePanel);
							SNotification.show("Enter Expenses", Type.ERROR_MESSAGE);
						}
						else{
							expenseField.setNewValue(roundNumber(expensePanel.getDebitAmount())+"");
						}
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			
			
			locationMasterCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(locationMasterCombo.getValue()!=null){
						locationCombo.setValue(locationMasterCombo.getValue());
					}
					else
						locationCombo.setValue((long)0);
				}
			});
			
			
			supplierCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						List<Object> idList=new ArrayList<Object>();
						Iterator it=table.getItemIds().iterator();
						while (it.hasNext()) {
							Object obj=(Object)it.next();
							Item item = table.getItem(obj);
							long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
							long grn=toLong(item.getItemProperty(TBC_GRN_ID).getValue().toString());
							if(id!=0)
								idList.add(obj);
							if(grn!=0)
								idList.add(obj);
						}
						if(idList.size()>0){
							Iterator it1 = idList.iterator();
							while (it1.hasNext()) {
								table.removeItem(it1.next());
							}
						}
						it = table.getItemIds().iterator();
						int SN = 0;
						while (it.hasNext()) {
							SN++;
							Item newitem = table.getItem((Integer) it.next());
							newitem.getItemProperty(TBC_SN).setValue(SN);
						}
						calculateTotals();
						if (supplierCombo.getValue() != null) {
							setRequiredError(supplierCombo, null, false);
							supplierCombo.setDescription("<h1><i>Current Balance</i> : "+
									roundNumberToString(Math.abs(comDao.getLedgerCurrentBalance((Long) supplierCombo.getValue()))) + "</h1>");
							SupplierModel supObj = supDao.getSupplierFromLedger((Long) supplierCombo.getValue());
							responsilbeEmployeeCombo.setValue(supObj.getResponsible_person());
							contactField.setNewValue(supObj.getAddress().getPhone()+", "+supObj.getAddress().getMobile());
							
							if(settings.isSHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE()){
								table.removeAllItems();
								List<ItemModel> itemModelList = itemDao.getAllActiveItemsFromOfc(getOfficeID());
								List<ItemModel> itemUnderSupplierList = new ArrayList<ItemModel>();
								
								Iterator itr = itemModelList.iterator();
								long supplierId = (Long)supplierCombo.getValue();
								String[] supplierIdsStringArray; 
								while(itr.hasNext()){
									ItemModel model = (ItemModel) itr.next();
									if(model.getPreferred_vendor() == null || model.getPreferred_vendor().trim().length() == 0){
										continue;
									}
									supplierIdsStringArray = model.getPreferred_vendor().split(",");
									
									for(int i = 0; i< supplierIdsStringArray.length; i++){
										if(Long.parseLong(supplierIdsStringArray[i]) == supplierId){
											itemUnderSupplierList.add(model);
											break;
										}
									}
								}
								
								SCollectionContainer bic=SCollectionContainer.setList(itemUnderSupplierList, "id");
								itemCombo.setContainerDataSource(bic);
								itemCombo.setItemCaptionPropertyId("name");
							}
						} else{
							supplierCombo.setDescription(null);
							responsilbeEmployeeCombo.setValue(null);
							contactField.setNewValue("");
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					purchaseCombo.setValue((long) 0);
					purchaseCombo.setValue(null);
				}
			});

			
			paymentCreditRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(paymentCreditRadio.getValue()!=null){
							if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.PAYMENT){
								cashChequeRadio.setVisible(false);
								payingAmountLayout.setVisible(true);
								payingAmountField.setValue(0);
								cashChequeRadio.setValue(SConstants.paymentMode.CASH);
								paymentModeCombo.setVisible(true);
							}
							else if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.CREDIT){
								cashChequeRadio.setVisible(false);
								payingAmountLayout.setVisible(false);
								payingAmountField.setValue(0);
								cashChequeRadio.setValue(SConstants.paymentMode.CASH);
								paymentModeCombo.setVisible(false);
//								List list=new ArrayList();
//								list=new LedgerDao().getAllActiveLedgerNamesExcluding(	getOfficeID(),
//																						SConstants.LEDGER_ADDED_INDIRECTLY, 
//																						settings.getCASH_GROUP());
//								SCollectionContainer bic=SCollectionContainer.setList(list, "id");
//								purchaseAccountCombo.setContainerDataSource(bic);
//								purchaseAccountCombo.setItemCaptionPropertyId("name");
//								purchaseAccountCombo.setValue(settings.getPURCHASE_ACCOUNT());
//								purchaseAccountCombo.setInputPrompt(getPropertyName("select"));
							}
							chequeDateField.setValue(getWorkingDate());
							chequenumberField.setValue("");
							
							chequeDateField.setVisible(false);
							chequenumberField.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			cashChequeRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(cashChequeRadio.getValue()!=null){
							if(cashChequeRadio.isVisible()){
								if((Long)cashChequeRadio.getValue()==SConstants.paymentMode.CASH){
//									List list=new ArrayList();
//									list=new LedgerDao().getAllActiveLedgerNamesOfGroup(getOfficeID(), 
//																						SConstants.LEDGER_ADDED_DIRECTLY, 
//																						settings.getCASH_GROUP());
//									SCollectionContainer bic=SCollectionContainer.setList(list, "id");
//									purchaseAccountCombo.setContainerDataSource(bic);
//									purchaseAccountCombo.setItemCaptionPropertyId("name");
//									purchaseAccountCombo.setValue(null);
//									purchaseAccountCombo.setInputPrompt(getPropertyName("select"));
									
									chequeDateField.setVisible(false);
									chequenumberField.setVisible(false);
								}
								else if((Long)cashChequeRadio.getValue()==SConstants.paymentMode.CHEQUE){
//									List list=new ArrayList();
//									list=new BankAccountDao().getAllActiveBankAccountNamesWithLedgerID(getOfficeID());
//									SCollectionContainer bic=SCollectionContainer.setList(list, "id");
//									purchaseAccountCombo.setContainerDataSource(bic);
//									purchaseAccountCombo.setItemCaptionPropertyId("name");
//									purchaseAccountCombo.setValue(null);
//									purchaseAccountCombo.setInputPrompt(getPropertyName("select"));
									
									//chequeDateField.setVisible(true);
									//chequenumberField.setVisible(true);
									
									chequeDateField.setValue(getWorkingDate());
									chequenumberField.setValue("");
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			paymentCreditRadio.setValue(SConstants.paymentMode.CREDIT);
			cashChequeRadio.setValue(SConstants.paymentMode.CASH);
			
			
			importOrderButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					setRequiredError(supplierCombo, null, false);
					try {
						if(supplierCombo.getValue() != null && !supplierCombo.getValue().toString().equals("")){
							List<Long> idList=new ArrayList<Long>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
								if(id!=0)
									idList.add(id);
							}
							List orderList=new ArrayList();
							orderList=dao.getPurchaseOrderModelSupplierList(getOfficeID(), (Long)supplierCombo.getValue(), idList);
							if(orderList.size()>0){
								SCollectionContainer bic=SCollectionContainer.setList(orderList, "id");
								purchaseOrderOptions.setContainerDataSource(bic);
								purchaseOrderOptions.setItemCaptionPropertyId("order_no");
								getUI().getCurrent().addWindow(purchaseOrderWindow);
							}
							else{
								SNotification.show("No Purchase Order Available",Type.WARNING_MESSAGE);
							}
						}
						else
							setRequiredError(supplierCombo, "Select Supplier", true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			importGRNButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					setRequiredError(supplierCombo, null, false);
					try {
						if(supplierCombo.getValue() != null && !supplierCombo.getValue().toString().equals("")){
							List<Long> idList=new ArrayList<Long>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								long id=toLong(item.getItemProperty(TBC_GRN_ID).getValue().toString());
								if(id!=0)
									idList.add(id);
							}
							List grnList=new ArrayList();
							grnList=dao.getPurchaseGRNModelSupplierList(getOfficeID(), (Long)supplierCombo.getValue(), idList);
							if(grnList.size()>0){
								SCollectionContainer bic=SCollectionContainer.setList(grnList, "id");
								purchaseGRNOptions.setContainerDataSource(bic);
								purchaseGRNOptions.setItemCaptionPropertyId("grn_no");
								getUI().getCurrent().addWindow(purchaseGRNWindow);
							}
							else{
								SNotification.show("No Purchase GRN Available",Type.WARNING_MESSAGE);
							}
						}
						else
							setRequiredError(supplierCombo, "Select Supplier", true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});


			addOrderButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (purchaseOrderOptions.getValue() != null && ((Set<Long>) purchaseOrderOptions.getValue()).size() > 0) {
							table.setVisibleColumns(allHeaders);
							Set<Long> purchaseOrders = (Set<Long>) purchaseOrderOptions.getValue();
							List orderList=new ArrayList();
							orderList=dao.getAllDataFromPurchaseOrder(purchaseOrders);
							Iterator itr = orderList.iterator();
							while (itr.hasNext()) {
								PurchaseGRNBean bean = (PurchaseGRNBean) itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(bean.getDet().getCurrencyId()).getCode();
							
								double discount=0;
								long taxId=SConstants.tax.PURCHASE_TAX;
								double taxPer=0;
								double taxAmount=0;
								double cess=0;
								
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										true,
										bean.getDet().getItem().getId(),
										bean.getDet().getItem().getItem_code(),
										bean.getDet().getItem().getName(),
										roundNumber(bean.getDet().getQunatity()),
										roundNumber(bean.getDet().getQty_in_basic_unit()/bean.getDet().getQunatity()),
										roundNumber(bean.getDet().getQty_in_basic_unit()),
										roundNumber(bean.getDet().getQty_in_basic_unit()),
										roundNumber(bean.getDet().getQty_in_basic_unit()-bean.getDet().getQuantity_received()),
										bean.getDet().getUnit().getId(),
										bean.getDet().getUnit().getSymbol(),
										roundNumber(bean.getDet().getUnit_price()),
										bean.getDet().getCurrencyId(),
										currency,
										roundNumber(bean.getDet().getConversionRate()),
										1,
										0.0,
										0.0,
										0.0,
										taxId,
										roundNumber(taxPer),
										roundNumber(taxAmount),
										roundNumber(cess),
										roundNumber((bean.getDet().getUnit_price()*bean.getDet().getQunatity())+taxAmount+cess-discount),
										getWorkingDate(),
										CommonUtil.formatDateToDDMMYYYY(getWorkingDate()),
										getWorkingDate(),
										CommonUtil.formatDateToDDMMYYYY(getWorkingDate()),
										(long)0,
										"None",
										(long)0,
										"None",
										"",
										(long)0,
										(long)0,
										bean.getId(),
										bean.getDet().getId(),
										(long)0,
										(long)0,roundNumber((bean.getDet().getUnit_price()*bean.getDet().getQunatity())+taxAmount+cess)},table.getItemIds().size()+1);
							}
							getUI().removeWindow(purchaseOrderWindow);
							calculateTotals();
							calculateNetPrice();
							table.setVisibleColumns(requiredHeaders);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			addGRNButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (purchaseGRNOptions.getValue() != null && ((Set<Long>) purchaseGRNOptions.getValue()).size() > 0) {
							table.setVisibleColumns(allHeaders);
							Set<Long> purchaseGRN = (Set<Long>) purchaseGRNOptions.getValue();
							List GRNList=new ArrayList();
							GRNList=dao.getAllDataFromPurchaseGRN(purchaseGRN);
							Iterator itr = GRNList.iterator();
							while (itr.hasNext()) {
								PurchaseBean bean = (PurchaseBean) itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(bean.getDet().getCurrencyId()).getCode();
								
								double discount=0;
								long taxId=SConstants.tax.PURCHASE_TAX;
								double taxPer=0;
								double taxAmount=0;
								double cess=0;
								
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										false,
										bean.getDet().getItem().getId(),
										bean.getDet().getItem().getItem_code(),
										bean.getDet().getItem().getName(),
										roundNumber(bean.getDet().getQunatity()),
                                        roundNumber(bean.getDet().getQty_in_basic_unit()/bean.getDet().getQunatity()),
                                        roundNumber(bean.getDet().getQty_in_basic_unit()),
										roundNumber(bean.getDet().getQty_in_basic_unit()),
										roundNumber(bean.getDet().getQty_in_basic_unit()),
										bean.getDet().getUnit().getId(),
										bean.getDet().getUnit().getSymbol(),
										roundNumber(bean.getDet().getUnit_price()),
										bean.getDet().getCurrencyId(),
										currency,
										roundNumber(bean.getDet().getConversionRate()),
										1,
										0.0,
										0.0,
										0.0,
										taxId,
										roundNumber(taxPer),
										roundNumber(taxAmount),
										roundNumber(cess),
										roundNumber(bean.getDet().getUnit_price()*bean.getDet().getQunatity()+taxAmount+cess-discount),
										getWorkingDate(),
										CommonUtil.formatDateToDDMMYYYY(getWorkingDate()),
										getWorkingDate(),
										CommonUtil.formatDateToDDMMYYYY(getWorkingDate()),
										bean.getDet().getGrade_id(),
										gradeCombo.getItemCaption(bean.getDet().getGrade_id()),
										bean.getDet().getLocation_id(),
										locationCombo.getItemCaption(bean.getDet().getLocation_id()),
										"",
										bean.getDet().getBatch_id(),
										bean.getDet().getStock_id(),
										bean.getDet().getOrder_id(),
										bean.getDet().getOrder_child_id(),
										bean.getId(),
										bean.getDet().getId(),roundNumber(bean.getDet().getUnit_price()*bean.getDet().getQunatity()+taxAmount+cess)},table.getItemIds().size()+1);
							}
							getUI().removeWindow(purchaseGRNWindow);
							List<Long> grnIdList=new ArrayList<Long>();
							List<Object> idList=new ArrayList<Object>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Object obj=(Object)it.next();
								Item item = table.getItem(obj);
								long grn=toLong(item.getItemProperty(TBC_GRN_ID).getValue().toString());
								if(grn!=0){
									long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
									if(id!=0)
										grnIdList.add(id);
								}
							}
							
							if(grnIdList.size()>0){
								Iterator itr2=table.getItemIds().iterator();
								while (itr2.hasNext()) {
									Object obj2=(Object)itr2.next();
									Item item = table.getItem(obj2);
									long grn=toLong(item.getItemProperty(TBC_GRN_ID).getValue().toString());
									if(grn==0){
										long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
										if(id!=0){
											if(grnIdList.contains(id)){
												idList.add(obj2);
											}
										}
									}
								}
							}
							if(idList.size()>0){
								Iterator it1 = idList.iterator();
								while (it1.hasNext()) {
									table.removeItem(it1.next());
								}
							}
							it = table.getItemIds().iterator();
							int SN = 0;
							while (it.hasNext()) {
								SN++;
								Item newitem = table.getItem((Integer) it.next());
								newitem.getItemProperty(TBC_SN).setValue(SN);
							}
							calculateTotals();
							calculateNetPrice();
							table.setVisibleColumns(requiredHeaders);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			itemCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemCombo.getValue() != null) {
							ItemModel itm = itmDao.getItem((Long) itemCombo.getValue());
							SCollectionContainer bic = SCollectionContainer.setList(comDao.getAllItemUnitDetails(itm.getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");
							unitSelect.setValue(null);
							unitSelect.setValue(itm.getUnit().getId());
							if (settings.isBARCODE_ENABLED()) {
								barcodeField.setValue(dao.getBarcodeFromStock(itm.getId()));
							}
							quantityField.selectAll();
							quantityField.focus();
						}
						else{
							convertionQuantityField.setValue("1");
							barcodeField.setValue("");
							convertedQuantityField.setVisible(false);
							convertionQuantityField.setVisible(false);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			
			newUnitButton.addClickListener(new ClickListener() {

				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						popupWindow.setContent(new AddNewUnitUI());
						popupWindow.setWidth("502");
						popupWindow.setHeight("455");
						popupWindow.center();
						popupWindow.setId("UNIT");
						popupWindow.setModal(true);
						popupWindow.setCaption(getPropertyName("add_new_unit"));
						getUI().getCurrent().addWindow(popupWindow);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			final CloseListener unitCloseListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					try {
						if(itemCombo.getValue()!=null){
							ItemModel itm=new ItemDao().getItem((Long)itemCombo.getValue());
							SCollectionContainer bic = SCollectionContainer.setList(comDao.getAllItemUnitDetails(itm.getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");
							unitSelect.setValue(null);
							unitSelect.setValue(itm.getUnit().getId());
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};

			
			unitMapButton.addClickListener(new ClickListener() {

				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						UnitManagementUI unit=new UnitManagementUI();

						unit.setCaption(getPropertyName("unit_management"));
						if(itemCombo.getValue()!=null)
							unit.itemCombo.setValue((Long)itemCombo.getValue());
						else
							unit.itemCombo.setValue(null);
						unit.center();
						getUI().getCurrent().addWindow(unit);
						unit.addCloseListener(unitCloseListener);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			unitSelect.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							if (itemCombo.getValue() != null) {

								ItemModel itm = new ItemDao().getItem((Long) itemCombo.getValue());
								if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
									convertionQuantityField.setValue("1");
									convertionQuantityField.setVisible(false);
									convertedQuantityField.setVisible(false);
								} 
								else {
									convertionQuantityField.setVisible(true);
									convertedQuantityField.setVisible(true);
									double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(), 0);
									convertionQuantityField.setValue(asString(cnvr_qty));
								}
								unitPriceField.setNewValue(comDao.getItemCurrency(itm.getId(), (Long) unitSelect.getValue(), (long)0),
										roundNumber(comDao.getItemPrice(itm.getId(), (Long) unitSelect.getValue(), (long)0)));
								if (quantityField.getValue() != null && !quantityField.getValue().toString().equals("0")) {
									convertedQuantityField.setNewValue(asString(Double.parseDouble(quantityField.getValue())
											* Double.parseDouble(convertionQuantityField.getValue())));
									double discountPrice=0;
									if((Integer) itemDiscountRadio.getValue()==1){
										discountPrice=roundNumber(unitPriceField.getValue()-roundNumber(unitPriceField.getValue()*toDouble(itemDiscountPercentField.getValue())/100));
									}else{
										discountPrice=(unitPriceField.getValue()-toDouble(itemDiscountAmountField.getValue()));
									}
									netPriceField.setNewValue(roundNumberToString(discountPrice*toDouble(quantityField.getValue())));
									}
							}
						}
						else
							unitPriceField.setNewValue(getCurrencyID(), 0.0);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			unitPriceField.setImmediate(true);
			quantityField.setImmediate(true);
			quantityField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemCombo.getValue() != null) {

							ItemModel itm = new ItemDao().getItem((Long) itemCombo.getValue());
							if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
								convertionQuantityField.setValue("1");
								convertionQuantityField.setVisible(false);
								convertedQuantityField.setVisible(false);
							} 
							else {
								convertionQuantityField.setVisible(true);
								convertedQuantityField.setVisible(true);
								double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(), 0);
								convertionQuantityField.setValue(asString(cnvr_qty));
							}
							if (quantityField.getValue() != null && !quantityField.getValue().toString().equals("0")) {
								convertedQuantityField.setNewValue(asString(Double.parseDouble(quantityField.getValue())
										* Double.parseDouble(convertionQuantityField.getValue())));
								double discountPrice=0;
								if((Integer) itemDiscountRadio.getValue()==1){
									discountPrice=roundNumber(unitPriceField.getValue()-roundNumber(unitPriceField.getValue()*toDouble(itemDiscountPercentField.getValue())/100));
								}else{
									discountPrice=(unitPriceField.getValue()-toDouble(itemDiscountAmountField.getValue()));
								}
								netPriceField.setNewValue(roundNumberToString(discountPrice*toDouble(quantityField.getValue())));
							}
						}
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			convertionQuantityField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (convertionQuantityField.getValue().equals("") || toDouble(convertionQuantityField.getValue()) <= 0) {
							convertionQuantityField.setValue("1");
						}
						calculateNetPrice();
					} catch (Exception e) {
						convertionQuantityField.setValue("1");
						e.printStackTrace();
					}
				}
			});

			
			unitPriceField.amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			itemDiscountAmountField.setImmediate(true);
			itemDiscountPercentField.setImmediate(true);
			itemDiscountAmountField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			itemDiscountPercentField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			netAmountField.currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(netAmountField.currencySelect.getValue()!=null){
						expensePanel.setCurrency(netAmountField.getCurrency());
						payingAmountField.currencySelect.setNewValue(netAmountField.getCurrency());
						payingAmountField.rateButton.setVisible(false);
						expensePanel.amountField.setCurrencyDate(dateField.getValue());
						expensePanel.reloadConvertionRate();
						double expense=expensePanel.getAmount();
						if(expense<0){
							getUI().addWindow(expensePanel);
							SNotification.show("Enter Expenses", Type.ERROR_MESSAGE);
						}
						else{
							expenseField.setNewValue(roundNumber(expensePanel.getDebitAmount())+"");
						}
					}
				}
			});
			
			
			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {
							table.setVisibleColumns(allHeaders);
							ItemModel itemModel = itemDao.getItem((Long) itemCombo.getValue());
							UnitModel unitModel = untDao.getUnit((Long) unitSelect.getValue());
							double qty=toDouble(quantityField.getValue().toString());
							double amount=unitPriceField.getValue();
							String currency=new CurrencyManagementDao().getselecteditem(unitPriceField.getCurrency()).getCode();
							double conv_rat;
							try {
								conv_rat = toDouble(convertionQuantityField.getValue());
							} catch (Exception e) {
								conv_rat=1;
							}
							Date manufacturingDate;
							Date expiryDate;
							if(isManufDateEnable()){
								manufacturingDate=manufacturingDateField.getValue();
								expiryDate=expiryDateField.getValue();
							}
							else{
								manufacturingDate=getWorkingDate();
								expiryDate=getWorkingDate();
							}
							
							double discount=0;
							long taxId=0;
							double taxPer=0;
							double taxAmount=0;
							double cess=0;
							
							TaxModel taxModel = null;
							if(taxEnable){
								taxModel=new TaxDao().getTax((Long) taxSelect.getValue());
								if(taxModel!=null){
									if(taxModel.getValue_type()==SConstants.tax.PERCENTAGE){
										taxId=taxModel.getId();
										taxPer=taxModel.getValue();
										taxAmount=(qty*amount*taxPer)/(100*unitPriceField.getConversionRate());
									}
									else{
										taxId=taxModel.getId();
										taxPer=0;
										taxAmount=taxModel.getValue();
									}
								}
								else{
									taxId=new TaxDao().getDefaultTax(getOfficeID(), SConstants.tax.PURCHASE_TAX).getId();
									taxPer=0;
									taxAmount=0;
								}
							}
							else{
								taxId=new TaxDao().getDefaultTax(getOfficeID(), SConstants.tax.PURCHASE_TAX).getId();
								taxPer=0;
								taxAmount=0;
							}
							
							if(isCessEnable()){
								cess=taxAmount*getCessPercentage()/100;
							}
							else
								cess=0;
							
//							if(isDiscountEnable()){
//								try {
//									discount=toDouble(discountField.getValue().toString());
//								} catch (Exception e) {
//									discount=0;
//								}
//							}
//							else
//								discount=0;
							

							double	discPer = toDouble(itemDiscountPercentField
									.getValue());
							double	discount_amt = toDouble(itemDiscountAmountField
									.getValue().trim());
							
							double discountPrice=0;
							if((Integer) itemDiscountRadio.getValue()==1){
								discountPrice=roundNumber(amount-roundNumber(amount*discPer/100));
							}else{
								discountPrice=(amount-discount_amt);
							}
							double netPrice=(discountPrice*qty)+taxAmount+cess;
							double totalPrice=(amount*qty)+taxAmount+cess;
							
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									(long)0,
									true,
									itemModel.getId(),
									itemModel.getItem_code(),
									itemModel.getName(),
									roundNumber(qty),
									roundNumber(conv_rat),
									roundNumber(conv_rat*qty),
									(double)0.0,
									(double)0.0,
									unitModel.getId(),
									unitModel.getSymbol(),
									roundNumber(amount),
									unitPriceField.getCurrency(),
									currency,
									roundNumber(unitPriceField.getConversionRate()),
									(Integer) itemDiscountRadio.getValue(),
									roundNumber(discPer),
									roundNumber(discount_amt),
									roundNumber(discountPrice),
									taxId,
									roundNumber(taxPer),
									roundNumber(taxAmount),
									roundNumber(cess),
									roundNumber(netPrice),
									manufacturingDate,
									CommonUtil.formatDateToDDMMYYYY(manufacturingDate),
									expiryDate,
									CommonUtil.formatDateToDDMMYYYY(expiryDate),
									(Long)gradeCombo.getValue(),
									gradeCombo.getItemCaption((Long)gradeCombo.getValue()),
									(Long)locationCombo.getValue(),
									locationCombo.getItemCaption((Long)locationCombo.getValue()),
									barcodeField.getValue(),
									(long)0,
									(long)0,
									(long)0,
									(long)0,
									(long)0,
									(long)0,roundNumber(totalPrice)},table.getItemIds().size()+1);
							resetItems();
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						setRequiredError(itemCombo, null, false);
						Collection selectedItems = null;
						resetItems();
						if (table.getValue() != null) {
							selectedItems = (Collection) table.getValue();
						}
						if (selectedItems != null && selectedItems.size() == 1) {
							Item item = table.getItem(selectedItems.iterator().next());
							itemCombo.setNewValue((Long)item.getItemProperty(TBC_ITEM_ID).getValue());
							quantityField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_QTY).getValue())+"");
							unitSelect.setNewValue((Long)item.getItemProperty(TBC_UNIT_ID).getValue());
							unitPriceField.setNewValue((Long)item.getItemProperty(TBC_CID).getValue(), 
													roundNumber((Double)item.getItemProperty(TBC_UNIT_PRICE).getValue()));
							
							itemDiscountRadio.setValue((Integer) item.getItemProperty(TBC_DISCOUNT_TYPE)
									.getValue());
							itemDiscountPercentField.setNewValue((Double) item.getItemProperty(TBC_DISCOUNT_PERCENTAGE).getValue()+ "");
							itemDiscountAmountField.setNewValue(Math.abs((Double) item.getItemProperty(TBC_DISCOUNT).getValue())+ "");
							
//							discountField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_DISCOUNT).getValue())+"");
							taxSelect.setNewValue((Long)item.getItemProperty(TBC_TAX_ID).getValue());
							netPriceField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_NET_PRICE).getValue())+"");
							convertionQuantityField.setNewValue(""+ roundNumber((Double)item.getItemProperty(TBC_CONVERTION_QTY).getValue()));
							manufacturingDateField.setNewValue((Date)item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue());
							expiryDateField.setNewValue((Date)item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue());
							gradeCombo.setNewValue((Long)item.getItemProperty(TBC_GRADE_ID).getValue());
							locationCombo.setNewValue((Long)item.getItemProperty(TBC_LOCATION_ID).getValue());
							
							long grnId=(Long)item.getItemProperty(TBC_GRN_ID).getValue();
							if(grnId==0){
								if(!(Boolean)item.getItemProperty(TBC_EDITABLE).getValue()){
									addItemButton.setVisible(false);
									updateItemButton.setVisible(false);
									setRequiredError(itemCombo, "Goods Returned From This Order. Cannot Edit or Delete", true);
								}
								else{
									addItemButton.setVisible(false);
									updateItemButton.setVisible(true);
								}
							}
							else{
								itemCombo.setReadOnly(true);
								quantityField.setReadOnly(true);
								unitSelect.setReadOnly(true);
								manufacturingDateField.setReadOnly(true);
								expiryDateField.setReadOnly(true);
								gradeCombo.setReadOnly(true);
								taxSelect.setReadOnly(true);
//								discountField.setReadOnly(true);
								barcodeField.setReadOnly(true);
								locationCombo.setReadOnly(true);
								addItemButton.setVisible(false);
								updateItemButton.setVisible(true);
								
								itemDiscountRadio.setValue(2);
								itemDiscountPercentField.setNewValue("0");
								itemDiscountAmountField.setNewValue("0");
							}
						}
						else{
							setRequiredError(itemCombo, null, false);
							resetItems();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			updateItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {
							Collection selectedItems = null;
							if (table.getValue() != null) {
								selectedItems = (Collection) table.getValue();
							}
							if (selectedItems != null && selectedItems.size() == 1) {
								Item item = table.getItem(selectedItems.iterator().next());
								
								ItemModel itemModel = itemDao.getItem((Long) itemCombo.getValue());
								UnitModel unitModel = untDao.getUnit((Long) unitSelect.getValue());
								double requested=toDouble(item.getItemProperty(TBC_QTY_ORDERED).getValue().toString());
								double balance=toDouble(item.getItemProperty(TBC_BALANCE).getValue().toString());
								double oldQty=toDouble(item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue().toString());
								double qty=toDouble(quantityField.getValue().toString());
								double amount=unitPriceField.getValue();
								String currency=new CurrencyManagementDao().getselecteditem(unitPriceField.getCurrency()).getCode();
								double conv_rat;
								try {
									conv_rat = toDouble(convertionQuantityField.getValue());
								} catch (Exception e) {
									conv_rat=1;
								}
								if(requested!=0){
									balance+=oldQty;
									balance-=(qty*conv_rat);
								}
								Date manufacturingDate;
								Date expiryDate;
								if(isManufDateEnable()){
									manufacturingDate=manufacturingDateField.getValue();
									expiryDate=expiryDateField.getValue();
								}
								else{
									manufacturingDate=getWorkingDate();
									expiryDate=getWorkingDate();
								}
								
								double discount=0;
								long taxId=0;
								double taxPer=0;
								double taxAmount=0;
								double cess=0;
								
								TaxModel taxModel = null;
								if(taxEnable){
									taxModel=new TaxDao().getTax((Long) taxSelect.getValue());
									if(taxModel!=null){
										if(taxModel.getValue_type()==SConstants.tax.PERCENTAGE){
											taxId=taxModel.getId();
											taxPer=taxModel.getValue();
											taxAmount=(qty*amount*taxPer)/(100*unitPriceField.getConversionRate());
										}
										else{
											taxId=taxModel.getId();
											taxPer=0;
											taxAmount=taxModel.getValue();
										}
									}
									else{
										taxId=new TaxDao().getDefaultTax(getOfficeID(), SConstants.tax.PURCHASE_TAX).getId();
										taxPer=0;
										taxAmount=0;
									}
								}
								else{
									taxId=new TaxDao().getDefaultTax(getOfficeID(), SConstants.tax.PURCHASE_TAX).getId();
									taxPer=0;
									taxAmount=0;
								}
								
								if(isCessEnable()){
									cess=taxAmount*getCessPercentage()/100;
								}
								else
									cess=0;
								
//								if(isDiscountEnable()){
//									try {
//										discount=toDouble(discountField.getValue().toString());
//									} catch (Exception e) {
//										discount=0;
//									}
//								}
//								else
//									discount=0;
								
//								double netPrice=(unitPriceField.getValue()*qty)+taxAmount+cess-discount;
								
								double	discPer = toDouble(itemDiscountPercentField
										.getValue());
								double	discount_amt = toDouble(itemDiscountAmountField
										.getValue().trim());
								double discountPrice=0;
								if((Integer) itemDiscountRadio.getValue()==1){
									discountPrice=roundNumber(amount-roundNumber(amount*discPer/100));
								}else{
									discountPrice=(amount-discount_amt);
								}
								double netPrice=(discountPrice*qty)+taxAmount+cess;
								double totalPrice=(amount*qty)+taxAmount+cess;
								
								item.getItemProperty(TBC_ITEM_ID).setValue(itemModel.getId());
								item.getItemProperty(TBC_ITEM_CODE).setValue(itemModel.getItem_code());
								item.getItemProperty(TBC_ITEM_NAME).setValue(itemModel.getName());
								item.getItemProperty(TBC_QTY).setValue(roundNumber(qty));
								item.getItemProperty(TBC_CONVERTION_QTY).setValue(roundNumber(conv_rat));
								item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).setValue(roundNumber(qty*conv_rat));
								item.getItemProperty(TBC_UNIT_ID).setValue(unitModel.getId());
								item.getItemProperty(TBC_UNIT).setValue(unitModel.getSymbol());
								item.getItemProperty(TBC_UNIT_PRICE).setValue(roundNumber(amount));
								item.getItemProperty(TBC_CID).setValue(unitPriceField.getCurrency());
								item.getItemProperty(TBC_CURRENCY).setValue(currency);
								item.getItemProperty(TBC_CONV_RATE).setValue(roundNumber(unitPriceField.getConversionRate()));
								item.getItemProperty(TBC_DISCOUNT_TYPE).setValue((Integer)itemDiscountRadio.getValue());
								item.getItemProperty(TBC_DISCOUNT_PERCENTAGE).setValue(discPer);
								item.getItemProperty(TBC_DISCOUNT).setValue(roundNumber(discount_amt));
								item.getItemProperty(TBC_DISCOUNT_UNIT_PRICE).setValue(roundNumber(discountPrice));
								item.getItemProperty(TBC_TAX_ID).setValue(taxId);
								item.getItemProperty(TBC_TAX_PERCENTAGE).setValue(roundNumber(taxPer));
								item.getItemProperty(TBC_TAX_AMOUNT).setValue(roundNumber(taxAmount));
								item.getItemProperty(TBC_CESS).setValue(roundNumber(cess));
								item.getItemProperty(TBC_NET_PRICE).setValue(roundNumber(netPrice));
								item.getItemProperty(TBC_NET_PRICE_WITHOUT_DISCOUNT).setValue(totalPrice);
								if(requested!=0){
									item.getItemProperty(TBC_BALANCE).setValue(roundNumber(balance));
								}
								item.getItemProperty(TBC_MANUFACTURING_DATE_ID).setValue(manufacturingDate);
								item.getItemProperty(TBC_MANUFACTURING_DATE).setValue(CommonUtil.formatDateToDDMMYYYY(manufacturingDate));
								item.getItemProperty(TBC_EXPIRY_DATE_ID).setValue(expiryDate);
								item.getItemProperty(TBC_EXPIRY_DATE).setValue(CommonUtil.formatDateToDDMMYYYY(expiryDate));
								item.getItemProperty(TBC_GRADE_ID).setValue((Long)gradeCombo.getValue());
								item.getItemProperty(TBC_GRADE).setValue(gradeCombo.getItemCaption((Long)gradeCombo.getValue()));
								item.getItemProperty(TBC_LOCATION_ID).setValue((Long)locationCombo.getValue());
								item.getItemProperty(TBC_LOCATION).setValue(locationCombo.getItemCaption((Long)locationCombo.getValue()));
								item.getItemProperty(TBC_BARCODE).setValue(barcodeField.getValue());
								resetItems();
							}
							calculateTotals();
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
						if(isValid()){
							PurchaseModel mdl=new PurchaseModel();
							boolean savable=true;
							List<PurchaseInventoryDetailsModel> itemsList = new ArrayList<PurchaseInventoryDetailsModel>();
							List<PurchaseExpenseDetailsModel> expenseList = new ArrayList<PurchaseExpenseDetailsModel>();
							Iterator it = table.getItemIds().iterator();
							double netDiscount=0;
							PaymentModeModel payMdl=null;
							while (it.hasNext()) {
								PurchaseInventoryDetailsModel det=new PurchaseInventoryDetailsModel();
								Item item = table.getItem(it.next());
								double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
								double price=(Double) item.getItemProperty(TBC_UNIT_PRICE).getValue();
								if(qty<=0){
									savable=false;
									break;
								}
								if(price<=0){
									savable=false;
									break;
								}
								det.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
								det.setQunatity(roundNumber((Double) item.getItemProperty(TBC_QTY).getValue()));
								det.setQty_in_basic_unit(roundNumber((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue()));
								det.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
								det.setUnit_price(roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()));
								det.setCurrencyId((Long) item.getItemProperty(TBC_CID).getValue());
								det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONV_RATE).getValue()));
								det.setDiscount(roundNumber((Double) item.getItemProperty(TBC_DISCOUNT).getValue()));
								det.setTax(new TaxModel((Long) item.getItemProperty(TBC_TAX_ID).getValue()));
								det.setTaxPercentage(roundNumber((Double) item.getItemProperty(TBC_TAX_PERCENTAGE).getValue()));
								det.setTaxAmount(roundNumber((Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()));
								det.setCessAmount(roundNumber((Double) item.getItemProperty(TBC_CESS).getValue()));
								det.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue()));
								det.setExpiry_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue()));
								det.setGrade_id((Long) item.getItemProperty(TBC_GRADE_ID).getValue());
								det.setLocation_id((Long) item.getItemProperty(TBC_LOCATION_ID).getValue());
								det.setBarcode(item.getItemProperty(TBC_BARCODE).getValue().toString());
								det.setStock_id((Long) item.getItemProperty(TBC_STOCK_ID).getValue());
								det.setOrder_id((Long) item.getItemProperty(TBC_ORDER_ID).getValue());
								det.setOrder_child_id((Long) item.getItemProperty(TBC_ORDER_CHILD_ID).getValue());
								det.setGrn_id((Long) item.getItemProperty(TBC_GRN_ID).getValue());
								det.setGrn_child_id((Long) item.getItemProperty(TBC_GRN_CHILD_ID).getValue());
								
								if (isDiscountEnable()) {
									double amount=roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue());
									double	discPer = roundNumber((Double) item.getItemProperty(TBC_DISCOUNT_PERCENTAGE).getValue());
									double	discount_amt = roundNumber((Double) item.getItemProperty(TBC_DISCOUNT).getValue());
									double discountPrice=0;
									double quantity=roundNumber((Double) item.getItemProperty(TBC_QTY).getValue());
									if((Integer) item.getItemProperty(TBC_DISCOUNT_TYPE).getValue()==1){
										discountPrice=roundNumber((amount*discPer/100)*quantity);
									}else{
										discountPrice=(discount_amt*quantity);
									}
									netDiscount=netDiscount+discountPrice;
									
									det.setDiscount_type((Integer) item.getItemProperty(TBC_DISCOUNT_TYPE).getValue());
									det.setDiscountPercentage(discPer);
									det.setDiscount(discount_amt);
								} else {
									det.setDiscount(0);
									det.setDiscount_type(1);
									det.setDiscountPercentage(0);
								}
								
								itemsList.add(det);
							}
							if(savable){
								mdl.setPurchase_no(getNextSequence("Purchase Number", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setSupplier(new LedgerModel((Long)supplierCombo.getValue()));
								mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
								mdl.setAmount(roundNumber(netAmountField.getValue()));
								mdl.setNetCurrencyId(new CurrencyModel(netAmountField.getCurrency()));
								mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
								double expense=0;
								try {
									expense=toDouble(expenseField.getValue().toString());
								} catch (Exception e) {
									expense=0;
								}
								mdl.setExpenseCreditAmount(roundNumber(expensePanel.getCreditAmount()));
								mdl.setExpenseAmount(roundNumber(expense));
								mdl.setPaymentAmount(roundNumber(payingAmountField.getValue()));
								mdl.setPaymentConversionRate(roundNumber(netAmountField.getConversionRate()));
								mdl.setCurrency_id(payingAmountField.getCurrency());
								mdl.setPayment_credit((Long)paymentCreditRadio.getValue());
								mdl.setCash_cheque((Long)cashChequeRadio.getValue());
								mdl.setChequeNo(chequenumberField.getValue());
								mdl.setChequeDate(CommonUtil.getSQLDateFromUtilDate(chequeDateField.getValue()));
								mdl.setPurchase_account((Long)purchaseAccountCombo.getValue());
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setComments(commentArea.getValue());
								mdl.setActive(true);
								mdl.setPurchase_details_list(itemsList);
								mdl.setDepartment_id((Long)departmentCombo.getValue());
								mdl.setDivision_id((Long)divisionCombo.getValue());
								if(paymentModeCombo.getValue()!=null){
									mdl.setPaymentMode(new PaymentModeModel((Long)paymentModeCombo.getValue()));
									payMdl = new PaymentModeDao().getPaymentModeModel((Long)paymentModeCombo.getValue());	
								}
								else
									mdl.setPaymentMode(null);
								
								mdl.setDiscount_type((Integer) discountRadio
										.getValue());
								mdl.setDiscountPercentage(roundNumber(toDouble(discountPercentField
										.getValue().trim())));
								mdl.setDiscountAmount(roundNumber(toDouble(discountAmountField
										.getValue().trim())));

								if((Integer) mdl.getDiscount_type()==1){
									netDiscount+=(mdl.getAmount()*mdl.getDiscountPercentage()/100);
								}else{
									netDiscount+=mdl.getDiscountAmount();
								}
								
								Iterator itr=expensePanel.getValue().iterator();
								while (itr.hasNext()) {
									ExpenseBean bean = (ExpenseBean) itr.next();
									PurchaseExpenseDetailsModel det=new PurchaseExpenseDetailsModel();
									det.setClearingAgent(bean.isClearingAgent());
									det.setLedger_id(bean.getLedger());
									det.setTransaction_type(bean.getTransactionType());
									det.setCurrencyId(bean.getCurrencyId());
									det.setConversionRate(roundNumber(bean.getConversionRate()));
									det.setAmount(roundNumber(bean.getAmount()));
									expenseList.add(det);
								}
								mdl.setPurchase_expense_list(expenseList);
								
								FinTransaction transaction = new FinTransaction();
								
								transaction.addTransaction(SConstants.DR, 
														(Long)supplierCombo.getValue(),
														(Long)purchaseAccountCombo.getValue(),
														roundNumber((netAmountField.getValue()/ netAmountField.getConversionRate())-
																(expensePanel.getDebitAmount()/ netAmountField.getConversionRate())),
														"",
														payingAmountField.getCurrency(),
														roundNumber(netAmountField.getConversionRate()));
								
								if(netDiscount>0)
									transaction.addTransaction(SConstants.DR, 
											settings.getPURCHASE_DESCOUNT_ACCOUNT(),
											(Long)purchaseAccountCombo.getValue(),
											roundNumber(netDiscount),
											"",
											payingAmountField.getCurrency(),
											roundNumber(netAmountField.getConversionRate()));
								
								if(payingAmountField.getValue()>0)
									transaction.addTransaction(SConstants.DR, 
															payMdl.getLedger().getId(), 
															(Long)supplierCombo.getValue(),
															roundNumber(payingAmountField.getValue()/netAmountField.getConversionRate()),
															"",
															payingAmountField.getCurrency(),
															roundNumber(netAmountField.getConversionRate()));
								
								Iterator expItr=expensePanel.getTransactionList().iterator();
								while (expItr.hasNext()) {
									ExpenseTransactionBean bean = (ExpenseTransactionBean) expItr.next();
									if(bean.getToId()!=0){
										transaction.addTransaction(SConstants.CR, 
																	bean.getToId(),
																	bean.getFromId(),
																	roundNumber(bean.getAmount()),
																	"",
																	bean.getCurrencyId(),
																	roundNumber(bean.getConversionRate()));
									}
									else{
										transaction.addTransaction(SConstants.CR, 
																	(Long)supplierCombo.getValue(),
																	bean.getFromId(),
																	roundNumber(bean.getAmount()),
																	"",
																	bean.getCurrencyId(),
																	roundNumber(bean.getConversionRate()));
									}
								}
								double fullAmount=0,paymentAmount=0;
								fullAmount=(netAmountField.getValue()- expensePanel.getDebitAmount()) +
											(expensePanel.getDebitAmount()- expensePanel.getCreditAmount());
								
								paymentAmount=roundNumber(payingAmountField.getValue());
								
								if(paymentAmount==0){
									mdl.setPayment_status(SConstants.NOT_PAID);
									mdl.setPayment_done('N');
								}	
								else if(paymentAmount>0 && paymentAmount<fullAmount){
									mdl.setPayment_status(SConstants.PARTIALLY_PAID);
									mdl.setPayment_done('N');
								}
								else if(paymentAmount>=fullAmount){
									mdl.setPayment_status(SConstants.FULLY_PAID);
									mdl.setPayment_done('Y');
								}
								
								long id=dao.save(mdl,transaction.getTransaction(SConstants.PURCHASE, CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								loadOptions(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),
										"Purchase Created. Purchase No : "
												+ mdl.getPurchase_no()+ ", Supplier : "
												+ supplierCombo.getItemCaption(supplierCombo.getValue())
												+ ", Amount : "+ mdl.getAmount(),id);
							}
							else
								Notification.show("Check Item Quantity Or Unit Price",Type.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}

				}
			});

			
			purchaseCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						table.removeAllItems();
						calculateTotals();
						netAmountField.currencySelect.setReadOnly(false);
						netAmountField.setCurrency(getCurrencyID());
						table.setValue(null);
						referenceNoField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						netAmountField.setCurrencyDate(getWorkingDate());
						supplierCombo.setValue(null);
						discountRadio.setValue(null);
						discountRadio.setValue(1);
						responsilbeEmployeeCombo.setValue(null);
						commentArea.setValue("");
						locationMasterCombo.setValue((long)0);
						paymentModeCombo.setValue(null);
						paymentCreditRadio.setValue(null);
						paymentCreditRadio.setValue(SConstants.paymentMode.CREDIT);
						cashChequeRadio.setValue(SConstants.paymentMode.CASH);
						purchaseAccountCombo.setValue(settings.getPURCHASE_ACCOUNT());
						chequenumberField.setValue("");
						chequeDateField.setValue(getWorkingDate());
						chequenumberField.setVisible(false);
						chequeDateField.setVisible(false);
						expensePanel.clearAll();
						expenseField.setNewValue("0.0");
						payingAmountField.setValue(getCurrencyID(),0);
						departmentCombo.setValue((long)0);
						divisionCombo.setValue(null);
						saveButton.setVisible(true);
						printButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						sendMailButton.setVisible(false);
						
						itemDiscountRadio.setValue(2);
						itemDiscountPercentField.setNewValue("0");
						itemDiscountAmountField.setNewValue("0");
						
						calculateTotals();
						resetItems();
						if(purchaseCombo.getValue()!=null && !purchaseCombo.getValue().toString().equals("0")){
							table.setVisibleColumns(allHeaders);
							PurchaseModel mdl=dao.getPurchaseModel((Long)purchaseCombo.getValue());
							referenceNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							netAmountField.setCurrencyDate(mdl.getDate());
							supplierCombo.setValue(mdl.getSupplier().getId());
							responsilbeEmployeeCombo.setValue(mdl.getResponsible_employee());
							commentArea.setValue(mdl.getComments());
							paymentCreditRadio.setValue(mdl.getPayment_credit());
							cashChequeRadio.setValue(mdl.getCash_cheque());
							chequenumberField.setValue(mdl.getChequeNo());
							if(mdl.getChequeDate()!=null)
								chequeDateField.setValue(mdl.getChequeDate());
							purchaseAccountCombo.setValue(mdl.getPurchase_account());
							payingAmountField.setNewValue(mdl.getPaymentAmount());
							payingAmountField.conversionField.setValue(""+roundNumber(mdl.getPaymentConversionRate()));
							departmentCombo.setValue(mdl.getDepartment_id());
							divisionCombo.setValue(mdl.getDivision_id());
							if (settings.isDISCOUNT_ENABLE()) {
								discountRadio.setValue(mdl
										.getDiscount_type());
								discountPercentField.setValue(roundNumber(mdl
										.getDiscountPercentage()) + "");
								discountAmountField
										.setNewValue(roundNumber(mdl
												.getDiscountAmount())
												+ "");
							}

							if(mdl.getPaymentMode()!=null)
								paymentModeCombo.setValue(mdl.getPaymentMode().getId());
							Iterator itr=mdl.getPurchase_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseInventoryDetailsModel det=(PurchaseInventoryDetailsModel)itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode();
								boolean editable=true;
								
								if(det.getGrn_id()==0){
									if (comDao.isStockBlocked(mdl.getId(),
																det.getId(),
																SConstants.stockPurchaseType.PURCHASE)){
										editable=false;
									}
								}
								else{
										editable=false;
								}
								if(det.getLock_count()>0)
									editable=false;
								
								PurchaseOrderDetailsModel podet=null;
								double requested=0,balance=0;
								if(det.getOrder_child_id()!=0)
									podet=new PurchaseOrderDao().getPurchaseOrderDetailsModel(det.getOrder_child_id());
									if(podet!=null){
										requested= podet.getQty_in_basic_unit();
										balance= podet.getQty_in_basic_unit()-podet.getQuantity_received();
									}
									
									
									double amount=det.getUnit_price();
									double	discPer = det.getDiscountPercentage();
									double	discount_amt = det.getDiscount();
									double discountPrice=0;
									if(det.getDiscount_type()==1){
										discountPrice=roundNumber(amount-roundNumber(amount*discPer/100));
									}else{
										discountPrice=(amount-discount_amt);
									}
									double netPrice=(discountPrice*det.getQunatity())+det.getTaxAmount()+det.getCessAmount();
									double totalPrice=(amount*det.getQunatity())+det.getTaxAmount()+det.getCessAmount();
									
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										det.getId(),
										editable,
										det.getItem().getId(),
										det.getItem().getItem_code(),
										det.getItem().getName(),
										roundNumber(det.getQunatity()),
										roundNumber(det.getQty_in_basic_unit()/det.getQunatity()),
										roundNumber(det.getQty_in_basic_unit()),
										roundNumber(requested),
										roundNumber(balance),
										det.getUnit().getId(),
										det.getUnit().getSymbol(),
										roundNumber(det.getUnit_price()),
										det.getCurrencyId(),
										currency,
										roundNumber(det.getConversionRate()),
										det.getDiscount_type(),
										roundNumber(discPer),
										roundNumber(discount_amt),
										roundNumber(discountPrice),
										det.getTax().getId(),
										roundNumber(det.getTaxPercentage()),
										roundNumber(det.getTaxAmount()),
										roundNumber(det.getCessAmount()),
										roundNumber(netPrice),
										det.getManufacturing_date(),
										CommonUtil.formatDateToDDMMYYYY(det.getManufacturing_date()),
										det.getExpiry_date(),
										CommonUtil.formatDateToDDMMYYYY(det.getExpiry_date()),
										det.getGrade_id(),
										gradeCombo.getItemCaption(det.getGrade_id()),
										det.getLocation_id(),
										locationCombo.getItemCaption(det.getLocation_id()),
										det.getBarcode(),
										det.getBatch_id(),
										det.getStock_id(),
										det.getOrder_id(),
										det.getOrder_child_id(),
										det.getGrn_id(),
										det.getGrn_child_id(),roundNumber(totalPrice)},table.getItemIds().size()+1);
								

							}
							itr=mdl.getPurchase_expense_list().iterator();
							List<ExpenseBean> resultList = new ArrayList<ExpenseBean>();
							while (itr.hasNext()) {
								PurchaseExpenseDetailsModel det = (PurchaseExpenseDetailsModel) itr.next();
								ExpenseBean bean=new ExpenseBean(det.isClearingAgent(),
																det.getLedger_id(),
																det.getTransaction_type(),
																det.getCurrencyId(),
																roundNumber(det.getConversionRate()),
																roundNumber(det.getAmount()));
								resultList.add(bean);
							}
							expensePanel.loadTable(resultList);
							expenseField.setNewValue(expensePanel.getDebitAmount()+"");
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
							if(mdl.getNetCurrencyId()!=null)
								netAmountField.setCurrency(mdl.getNetCurrencyId().getId());
							else
								netAmountField.setCurrency(getCurrencyID());
							netAmountField.conversionField.setValue(""+roundNumber(mdl.getConversionRate()));
							if(mdl.getPaid_by_payment()>0)
								netAmountField.currencySelect.setReadOnly(true);
							saveButton.setVisible(false);
							printButton.setVisible(true);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);
							sendMailButton.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			
			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							if(purchaseCombo.getValue()!=null && !purchaseCombo.getValue().toString().equals("0")){
								PurchaseModel mdl=dao.getPurchaseModel((Long)purchaseCombo.getValue());
								List<PurchaseInventoryDetailsModel> itemsList = new ArrayList<PurchaseInventoryDetailsModel>();
								List<PurchaseExpenseDetailsModel> expenseList = new ArrayList<PurchaseExpenseDetailsModel>();
								boolean savable=true;
								double netDiscount=0;
								PaymentModeModel payMdl=null;
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									PurchaseInventoryDetailsModel det=null;
									Item item = table.getItem(it.next());
									double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
									double price=(Double) item.getItemProperty(TBC_UNIT_PRICE).getValue();
									if(qty<=0){
										savable=false;
										break;
									}
									if(price<=0){
										savable=false;
										break;
									}
									long id=(Long) item.getItemProperty(TBC_ID).getValue();
									if(id!=0)
										det=dao.getPurchaseInventoryDetailsModel(id);
									if(det==null)
										det=new PurchaseInventoryDetailsModel();
									det.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
									det.setQunatity(roundNumber((Double) item.getItemProperty(TBC_QTY).getValue()));
									det.setQty_in_basic_unit(roundNumber((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue()));
									det.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
									det.setUnit_price(roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()));
									det.setCurrencyId((Long) item.getItemProperty(TBC_CID).getValue());
									det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONV_RATE).getValue()));
									det.setDiscount(roundNumber((Double) item.getItemProperty(TBC_DISCOUNT).getValue()));
									det.setTax(new TaxModel((Long) item.getItemProperty(TBC_TAX_ID).getValue()));
									det.setTaxPercentage(roundNumber((Double) item.getItemProperty(TBC_TAX_PERCENTAGE).getValue()));
									det.setTaxAmount(roundNumber((Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()));
									det.setCessAmount(roundNumber((Double) item.getItemProperty(TBC_CESS).getValue()));
									det.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue()));
									det.setExpiry_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue()));
									det.setGrade_id((Long) item.getItemProperty(TBC_GRADE_ID).getValue());
									det.setLocation_id((Long) item.getItemProperty(TBC_LOCATION_ID).getValue());
									det.setStock_id((Long) item.getItemProperty(TBC_STOCK_ID).getValue());
									det.setBarcode(item.getItemProperty(TBC_BARCODE).getValue().toString());
									det.setOrder_id((Long) item.getItemProperty(TBC_ORDER_ID).getValue());
									det.setOrder_child_id((Long) item.getItemProperty(TBC_ORDER_CHILD_ID).getValue());
									det.setGrn_id((Long) item.getItemProperty(TBC_GRN_ID).getValue());
									det.setGrn_child_id((Long) item.getItemProperty(TBC_GRN_CHILD_ID).getValue());
									
									if (isDiscountEnable()) {
										double amount=roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue());
										double	discPer = roundNumber((Double) item.getItemProperty(TBC_DISCOUNT_PERCENTAGE).getValue());
										double	discount_amt = roundNumber((Double) item.getItemProperty(TBC_DISCOUNT).getValue());
										double discountPrice=0;
										double quantity=roundNumber((Double) item.getItemProperty(TBC_QTY).getValue());
										
										if((Integer) item.getItemProperty(TBC_DISCOUNT_TYPE).getValue()==1){
											discountPrice=roundNumber((amount*discPer/100)*quantity);
										}else{
											discountPrice=(discount_amt*quantity);
										}
										netDiscount=netDiscount+discountPrice;
										
										det.setDiscount_type((Integer) item.getItemProperty(TBC_DISCOUNT_TYPE).getValue());
										det.setDiscountPercentage(discPer);
										det.setDiscount(discount_amt);
									} else {
										det.setDiscount(0);
										det.setDiscount_type(1);
										det.setDiscountPercentage(0);
									}
									itemsList.add(det);
								}
								if(savable){
									mdl.setRef_no(referenceNoField.getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									mdl.setSupplier(new LedgerModel((Long)supplierCombo.getValue()));
									mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
									mdl.setAmount(roundNumber(netAmountField.getValue()));
									mdl.setNetCurrencyId(new CurrencyModel(netAmountField.getCurrency()));
									mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
									double expense=0;
									try {
										expense=toDouble(expenseField.getValue().toString());
									} catch (Exception e) {
										expense=0;
									}
									mdl.setExpenseCreditAmount(roundNumber(expensePanel.getCreditAmount()));
									mdl.setExpenseAmount(roundNumber(expense));
									mdl.setPaymentAmount(roundNumber(payingAmountField.getValue()));
									mdl.setPaymentConversionRate(roundNumber(netAmountField.getConversionRate()));
									mdl.setCurrency_id(payingAmountField.getCurrency());
									mdl.setPayment_credit((Long)paymentCreditRadio.getValue());
									mdl.setCash_cheque((Long)cashChequeRadio.getValue());
									mdl.setChequeNo(chequenumberField.getValue());
									mdl.setChequeDate(CommonUtil.getSQLDateFromUtilDate(chequeDateField.getValue()));
									mdl.setPurchase_account((Long)purchaseAccountCombo.getValue());
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									mdl.setComments(commentArea.getValue());
									mdl.setPurchase_details_list(itemsList);
									mdl.setDepartment_id((Long)departmentCombo.getValue());
									mdl.setDivision_id((Long)divisionCombo.getValue());
									if(paymentModeCombo.getValue()!=null){
										mdl.setPaymentMode(new PaymentModeModel((Long)paymentModeCombo.getValue()));
										payMdl = new PaymentModeDao().getPaymentModeModel((Long)paymentModeCombo.getValue());
									}else
										mdl.setPaymentMode(null);
									mdl.setDiscount_type((Integer) discountRadio
											.getValue());
									mdl.setDiscountPercentage(roundNumber(toDouble(discountPercentField
											.getValue().trim())));
									mdl.setDiscountAmount(roundNumber(toDouble(discountAmountField
											.getValue().trim())));
									if((Integer) mdl.getDiscount_type()==1){
										netDiscount+=(mdl.getDiscountAmount()*mdl.getDiscountPercentage()/100);
									}else{
										netDiscount+=mdl.getDiscountAmount();
									}
									
									
									Iterator itr=expensePanel.getValue().iterator();
									while (itr.hasNext()) {
										ExpenseBean bean = (ExpenseBean) itr.next();
										PurchaseExpenseDetailsModel det=new PurchaseExpenseDetailsModel();
										det.setClearingAgent(bean.isClearingAgent());
										det.setLedger_id(bean.getLedger());
										det.setTransaction_type(bean.getTransactionType());
										det.setCurrencyId(bean.getCurrencyId());
										det.setConversionRate(roundNumber(bean.getConversionRate()));
										det.setAmount(roundNumber(bean.getAmount()));
										expenseList.add(det);
									}
									mdl.setPurchase_expense_list(expenseList);
									
									FinTransaction transaction = new FinTransaction();
									
									transaction.addTransaction(SConstants.DR, 
															(Long)supplierCombo.getValue(),
															(Long)purchaseAccountCombo.getValue(),
															roundNumber((netAmountField.getValue()/ netAmountField.getConversionRate())-
																	(expensePanel.getDebitAmount()/ netAmountField.getConversionRate())),
															"",
															payingAmountField.getCurrency(),
															roundNumber(netAmountField.getConversionRate()));
									
									if(netDiscount>0)
										transaction.addTransaction(SConstants.DR, 
												settings.getPURCHASE_DESCOUNT_ACCOUNT(),
												(Long)purchaseAccountCombo.getValue(),
												roundNumber(netDiscount),
												"",
												payingAmountField.getCurrency(),
												roundNumber(netAmountField.getConversionRate()));
					
									if(payingAmountField.getValue()>0)
										transaction.addTransaction(SConstants.DR, 
																payMdl.getLedger().getId(), 
																(Long)supplierCombo.getValue(),
																roundNumber(payingAmountField.getValue()/netAmountField.getConversionRate()),
																"",
																payingAmountField.getCurrency(),
																roundNumber(netAmountField.getConversionRate()));
									
									Iterator expItr=expensePanel.getTransactionList().iterator();
									while (expItr.hasNext()) {
										ExpenseTransactionBean bean = (ExpenseTransactionBean) expItr.next();
										if(bean.getToId()!=0){
											transaction.addTransaction(SConstants.CR, 
																		bean.getToId(),
																		bean.getFromId(),
																		roundNumber(bean.getAmount()),
																		"",
																		bean.getCurrencyId(),
																		roundNumber(bean.getConversionRate()));
										}
										else{
											transaction.addTransaction(SConstants.CR, 
																		(Long)supplierCombo.getValue(),
																		bean.getFromId(),
																		roundNumber(bean.getAmount()),
																		"",
																		bean.getCurrencyId(),
																		roundNumber(bean.getConversionRate()));
										}
									}	
									
									double fullAmount=0, paymentAmount=0, balance=0;
									fullAmount=((netAmountField.getValue()) -
												(expensePanel.getDebitAmount())) +
												((expensePanel.getDebitAmount()) -
												(expensePanel.getCreditAmount()));
									
									paymentAmount=roundNumber(payingAmountField.getValue());
									balance=fullAmount - paymentAmount - mdl.getPaid_by_payment();
									
									if(balance<=0){
										mdl.setPayment_status(SConstants.FULLY_PAID);
										mdl.setPayment_done('Y');
									}
									else{
										mdl.setPayment_status(SConstants.PARTIALLY_PAID);
										mdl.setPayment_done('N');
									}
									
									TransactionModel trans = dao.getTransactionModel(mdl.getTransaction_id());
									trans.setTransaction_details_list(transaction.getChildList());
									trans.setDate(mdl.getDate());
									trans.setLogin_id(getLoginID());
									
									dao.update(mdl, trans);
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId());
									saveActivity(getOptionId(),
											"Purchase Updated. Purchase No : "
													+ mdl.getPurchase_no()+ ", Supplier : "
													+ supplierCombo.getItemCaption(supplierCombo.getValue())
													+ ", Amount : "+ mdl.getAmount(),mdl.getId());
								}
								else
									Notification.show("Check Item Quantity Or Unit Price",Type.ERROR_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}

				}
			});

			
			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (purchaseCombo.getValue() != null && !purchaseCombo.getValue().toString().equals("0")) {
							Iterator itr=table.getItemIds().iterator();
							boolean blocked =false;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								if (comDao.isStockBlocked(	(Long) purchaseCombo.getValue(),
															(Long) item.getItemProperty(TBC_ID).getValue(),
															SConstants.stockPurchaseType.PURCHASE)) {
									blocked=true;
									break;
								}
								if((Long)item.getItemProperty(TBC_GRN_ID).getValue()==0){
									if(!(Boolean)item.getItemProperty(TBC_EDITABLE).getValue()){
										blocked=true;
										break;	
									}
								}
							}
							if(!blocked){
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												PurchaseModel mdl=dao.getPurchaseModel((Long)purchaseCombo.getValue());
												if(mdl.getLock_count()>0){
													Notification.show("Purchase Return Created On This Purchase. Cannot Delete",Type.ERROR_MESSAGE);
												}
												else{
													dao.delete(mdl);
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													saveActivity(getOptionId()," Purchase deleted. Purchase No : "
																+ purchaseCombo.getItemCaption(purchaseCombo.getValue())
																+ ", Supplier : "+supplierCombo.getItemCaption(supplierCombo.getValue()),
																(Long)purchaseCombo.getValue());
													loadOptions(0);
												}
												

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
							}
							else{
								Notification.show("Cannot Delete, Stock Exists in Sale", Type.ERROR_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			cancelButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (purchaseCombo.getValue() != null && !purchaseCombo.getValue().toString().equals("0")) {
							Iterator itr=table.getItemIds().iterator();
							boolean blocked =false;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								if (comDao.isStockBlocked(	(Long) purchaseCombo.getValue(),
															(Long) item.getItemProperty(TBC_ID).getValue(),
															SConstants.stockPurchaseType.PURCHASE)) {
									blocked=true;
									break;
								}
								if(!(Boolean)item.getItemProperty(TBC_EDITABLE).getValue()){
									blocked=true;
									break;	
								}
							}
							if(!blocked){
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												PurchaseModel mdl=dao.getPurchaseModel((Long)purchaseCombo.getValue());
												if(mdl.getLock_count()>0){
													Notification.show("Purchase Return Created On This Purchase. Cannot Delete",Type.ERROR_MESSAGE);
												}
												else{
													dao.cancel(mdl);
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													saveActivity(getOptionId()," Purchase Cancelled. Purchase No : "
															+ purchaseCombo.getItemCaption(purchaseCombo.getValue())
															+ ", Supplier : "+supplierCombo.getItemCaption(supplierCombo.getValue()),
															(Long)purchaseCombo.getValue());
													loadOptions(0);
												}
												

											} catch (Exception e) {
												e.printStackTrace();
												SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
											}
										}
									}
								});
							}
							else{
								Notification.show("Cannot Cancel, Stock Exists in Sale", Type.ERROR_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
//			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
//				@Override
//				public void handleAction(Object sender, Object target) {
//					if (saveButton.isVisible())
//						saveButton.click();
//					else
//						updateButton.click();
//				}
//			});
			
			itemDiscountRadio.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (itemDiscountRadio.getValue() != null) {
						if ((Integer) itemDiscountRadio.getValue() == 1) {
							itemDiscountPercentField.setNewValue("0.0");
							itemDiscountAmountField.setNewValue("0.0");
							itemDiscountPercentField.setReadOnly(false);
							itemDiscountAmountField.setReadOnly(true);
						} else {
							itemDiscountPercentField.setNewValue("0.0");
							itemDiscountAmountField.setNewValue("0.0");
							itemDiscountPercentField.setReadOnly(true);
							itemDiscountAmountField.setReadOnly(false);
						}
					} else {
						itemDiscountPercentField.setNewValue("0.0");
						itemDiscountAmountField.setNewValue("0.0");
						itemDiscountPercentField.setReadOnly(false);
						itemDiscountAmountField.setReadOnly(false);
					}
				}
			});
			itemDiscountRadio.setValue(2);
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.F4, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			
			
			if (!isFinYearBackEntry()) {
				saveButton.setVisible(false);
				updateButton.setVisible(false);
				deleteButton.setVisible(false);
				cancelButton.setVisible(false);
				sendMailButton.setVisible(false);
				printButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),Type.WARNING_MESSAGE);
			}

			
			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						if(purchaseCombo.getValue()!=null && !purchaseCombo.getValue().toString().equals("0")){
							PurchaseModel mdl=dao.getPurchaseModel((Long)purchaseCombo.getValue());
							SupplierModel ledger = supDao.getSupplierFromLedger(toLong(supplierCombo.getValue().toString()));
							String address = "";
							if (ledger != null) {
								address = new AddressDao().getAddressString(ledger.getAddress().getId());
							}
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
							map.put("ADDRESS_LABEL", getPropertyName("address"));
							map.put("ADDRESS", address);
							map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("BILL_NO", mdl.getPurchase_no());
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("NET_PRICE_LABEL", getPropertyName("net_price"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							S_OfficeModel office = new OfficeDao().getOffice(mdl.getOffice().getId());
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
							
							Iterator itr=mdl.getPurchase_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrency_id()).getCode());
							map.put("EXPENSE_LABEL", getPropertyName("expendeture"));
							map.put("EXPENSE", roundNumber(mdl.getExpenseAmount()));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("PAYMENT_AMOUNT", roundNumber(mdl.getPaymentAmount()));
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE));
							report.setReportFileName("Purchase Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.printReport();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			sendMailButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						
						if(purchaseCombo.getValue()!=null && !purchaseCombo.getValue().toString().equals("0")){
							PurchaseModel mdl=dao.getPurchaseModel((Long)purchaseCombo.getValue());
							SupplierModel ledger = supDao.getSupplierFromLedger(toLong(supplierCombo.getValue().toString()));
							String addr = "";
							if (ledger != null) {
								addr = new AddressDao().getAddressString(ledger.getAddress().getId());
							}
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
							map.put("ADDRESS_LABEL", getPropertyName("address"));
							map.put("ADDRESS", addr);
							map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("BILL_NO", mdl.getPurchase_no());
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("NET_PRICE_LABEL", getPropertyName("net_price"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							S_OfficeModel office = new OfficeDao().getOffice(mdl.getOffice().getId());
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
							
							Iterator itr=mdl.getPurchase_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrency_id()).getCode());
							map.put("EXPENSE_LABEL", getPropertyName("expendeture"));
							map.put("EXPENSE", roundNumber(mdl.getExpenseAmount()));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("PAYMENT_AMOUNT", roundNumber(mdl.getPaymentAmount()));
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE_ORDER));
							report.setReportFileName("Purchase Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.print();
							
							InternetAddress toAddr=null;
							Address[] mailTo=new Address[1];
							try {
								toAddr=new InternetAddress(ledger.getAddress().getEmail());
								mailTo[0]=toAddr;
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(toAddr!=null) {
								SMail mail=new SMail();
								File file=new File(report.getReportFile());
								EmailConfigurationModel configModel=new EmailConfigDao().getEmailConfiguration(getUserID());
								if(configModel!=null){
									if(file.exists())
										mail.sendMailWithFromAddress(mailTo, 
																	"You have a new Purchase from "+ledger.getName()+" Bill No. "+mdl.getPurchase_no(),
																	"Purchase",
																	file, 
																	getUserID());
									Notification.show("Email Successfully Sent to "+toAddr,Type.TRAY_NOTIFICATION);
								}
								else
									SNotification.show("Configure Email", Type.ERROR_MESSAGE);
							}
							else 
								Notification.show("Invalid Email-Id. Please enter valid Email-Id.",Type.ERROR_MESSAGE);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		discountRadio.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (discountRadio.getValue() != null) {
					if ((Integer) discountRadio.getValue() == 1) {
						discountPercentField.setNewValue("0.0");
						discountAmountField.setNewValue("0.0");
						discountPercentField.setReadOnly(false);
						discountAmountField.setReadOnly(true);
					} else {
						discountPercentField.setNewValue("0.0");
						discountAmountField.setNewValue("0.0");
						discountPercentField.setReadOnly(true);
						discountAmountField.setReadOnly(false);
					}
				} else {
					discountPercentField.setNewValue("0.0");
					discountAmountField.setNewValue("0.0");
					discountPercentField.setReadOnly(false);
					discountAmountField.setReadOnly(false);
				}
			}
		});
		discountRadio.setValue(1);
		
		discountPercentField.setImmediate(true);
		discountAmountField.setImmediate(true);
		discountPercentField
		.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				double total = roundNumber(toDouble(table
						.getColumnFooter(TBC_NET_PRICE).trim()));
				double discountPer = 0, discount = 0;
				try {
					discountPer = toDouble(discountPercentField
							.getValue().toString().trim());
				} catch (Exception e) {
					discountPer = 0;
				}
				discountPercentField
						.setNewValue(roundNumber(discountPer) + "");
				discount = roundNumber((total) * discountPer / 100);
				discountAmountField
						.setNewValue(roundNumber(discount) + "");
				calculateTotals();
			}
		});
		
		discountAmountField
		.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				calculateTotals();
			}
		});

		return pannel;
	}

	
	public void resetItems(){
		itemCombo.setReadOnly(false);
		quantityField.setReadOnly(false);
		unitSelect.setReadOnly(false);
		manufacturingDateField.setReadOnly(false);
		expiryDateField.setReadOnly(false);
		gradeCombo.setReadOnly(false);
		taxSelect.setReadOnly(false);
//		discountField.setReadOnly(false);
		barcodeField.setReadOnly(false);
		locationCombo.setReadOnly(false);
		
		itemDiscountRadio.setValue(2);
		itemDiscountPercentField.setNewValue("0");
		itemDiscountAmountField.setNewValue("0");
		
		itemCombo.setValue(null);
		quantityField.setValue("0");
		unitSelect.setValue(null);
		unitPriceField.setNewValue(getCurrencyID(),0.0);
		manufacturingDateField.setValue(getWorkingDate());
		expiryDateField.setValue(getWorkingDate());
		gradeCombo.setValue((long)0);
		try {
			taxSelect.setValue(new TaxDao().getDefaultTax(getOfficeID(), SConstants.tax.PURCHASE_TAX).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		discountField.setValue("0");
		barcodeField.setValue("");
		if(locationMasterCombo.getValue()!=null){
			locationCombo.setValue(locationMasterCombo.getValue());
		}
		else
			locationCombo.setValue((long)0);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
	}
	
	
	@SuppressWarnings("rawtypes")
	public void calculateTotals() {
		try {
			double qty_ttl = 0, net_ttl = 0,total=0;
			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
				net_ttl += (((Double) item.getItemProperty(TBC_DISCOUNT_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue());
				total += (((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue());
			}
			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));
			table.setColumnFooter(TBC_NET_PRICE_WITHOUT_DISCOUNT, asString(roundNumber(total)));
			double expense=0;
			try {
				expense=expensePanel.getDebitAmount();
			} catch (Exception e) {
				expense=0;
			}
			double discount = 0;
			if (discountRadio.getValue() != null) {
				if ((Integer) discountRadio.getValue() == 1) {
					double discPer = 0;
					try {
						discPer = toDouble(discountPercentField.getValue()
								.trim());
					} catch (Exception e1) {
						discPer = 0;
					}
					discount = roundNumber((net_ttl) * discPer / 100);
				} else {
					try {
						discount = toDouble(discountAmountField.getValue()
								.trim());
					} catch (Exception e1) {
						discount = 0;
					}
				}
			}
			netAmountField.setNewValue(roundNumber(net_ttl+expense - discount));
			convertedField.setNewValue(roundNumber(net_ttl+expense - discount));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (itemCombo.getValue() == null || itemCombo.getValue().equals("")) {
				setRequiredError(itemCombo, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(itemCombo, null, false);
			
			if (unitSelect.getValue() == null || unitSelect.getValue().equals("")) {
				setRequiredError(unitSelect, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(unitSelect, null, false);

			if (quantityField.getValue() == null || quantityField.getValue().equals("")) {
				setRequiredError(quantityField, getPropertyName("invalid_data"), true);
				ret = false;
			} else {
				try {
					if (Double.parseDouble(quantityField.getValue()) <=0) {
						setRequiredError(quantityField, getPropertyName("invalid_data"), true);
						ret = false;
					}
					else
						setRequiredError(quantityField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityField, getPropertyName("invalid_data"), true);
					ret = false;
				}
			}
			
			if (convertionQuantityField.getValue() == null || convertionQuantityField.getValue().equals("")) {
				setRequiredError(convertionQuantityField, getPropertyName("invalid_data"), true);
				ret = false;
			} else {
				try {
					if (Double.parseDouble(convertionQuantityField.getValue()) <=0) {
						setRequiredError(convertionQuantityField, getPropertyName("invalid_data"), true);
						ret = false;
					}
					else
						setRequiredError(convertionQuantityField, null, false);
				} catch (Exception e) {
					setRequiredError(convertionQuantityField, getPropertyName("invalid_data"), true);
					ret = false;
				}
			}
			
			if(dateField.getValue()==null || dateField.getValue().toString().equals("")){
				ret=false;
				setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			}
			else
				setRequiredError(dateField, null, false);
			
			if(dateField.getValue()!=null){
				if(!unitPriceField.isFieldValid(dateField.getValue()))
					ret=false;
			}
			
			if(settings.isTAX_ENABLED()){
				if (taxSelect.getValue() == null || taxSelect.getValue().equals("")) {
					setRequiredError(taxSelect,getPropertyName("invalid_selection"), true);
					ret = false;
				} else
					setRequiredError(taxSelect, null, false);
			}
			else
				taxSelect.setValue(SConstants.tax.PURCHASE_TAX);
			
			if(!settings.isBARCODE_ENABLED())
				barcodeField.setValue("");
			
//			if(settings.isDISCOUNT_ENABLE()){
//				if (discountField.getValue() == null || discountField.getValue().equals("")) {
//					discountField.setValue("0");
//				} else {
//					try {
//						if (Double.parseDouble(discountField.getValue()) <0) {
//							discountField.setNewValue("0");
//						} 
//					} catch (Exception e) {
//						discountField.setNewValue("0");
//					}
//				}
//			}
//			else
//				discountField.setNewValue("0");
			
			
			if(settings.isGRADING_ENABLED()){
				if (gradeCombo.getValue() == null || gradeCombo.getValue().equals("")) {
					gradeCombo.setValue((long)0);
				} 
			}
			
			if(isManufDateEnable()){
				if (manufacturingDateField.getValue() == null) {
					setRequiredError(manufacturingDateField,getPropertyName("invalid_selection"), true);
					ret = false;
				} else
					setRequiredError(manufacturingDateField, null, false);
				
				if (expiryDateField.getValue() == null) {
					setRequiredError(expiryDateField,getPropertyName("invalid_selection"), true);
					ret = false;
				} else
					setRequiredError(expiryDateField, null, false);
			}
			if (locationCombo.getValue() == null || locationCombo.getValue().equals("")) {
				locationCombo.setValue((long)0);
			} 
			if(unitPriceField.getValue()<=0){
				unitPriceField.setNewValue(0.0);
				setRequiredError(unitPriceField,getPropertyName("invalid_data"), true);
				ret = false;
			}
			else
				setRequiredError(unitPriceField, null, false);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;

	}
	
	
	public void visibleAddupdatePOButton(boolean AddVisible, boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				Collection selectedItems = (Collection) table.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					Object obj=it1.next();
					Item item=table.getItem(obj);
					if((Boolean)item.getItemProperty(TBC_EDITABLE).getValue())
						table.removeItem(obj);
				}
				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					newitem = table.getItem((Integer) it.next());
					newitem.getItemProperty(TBC_SN).setValue(SN);
				}
				calculateTotals();
			}
			itemCombo.focus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.add(new PurchaseModel(0, "----Create New-----"));
			list.addAll(dao.getPurchaseModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			purchaseCombo.setContainerDataSource(bic);
			purchaseCombo.setItemCaptionPropertyId("purchase_no");
			purchaseCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {

		boolean ret = true;
		
		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (responsilbeEmployeeCombo.getValue() == null || responsilbeEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsilbeEmployeeCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(responsilbeEmployeeCombo, null, false);

		
		if (supplierCombo.getValue() == null || supplierCombo.getValue().equals("")) {
			setRequiredError(supplierCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(supplierCombo, null, false);
		
		if (purchaseAccountCombo.getValue() == null || purchaseAccountCombo.getValue().equals("")) {
			setRequiredError(purchaseAccountCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(purchaseAccountCombo, null, false);
		
		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, "Select a Date", true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
		if(expensePanel.getAmount()<0){
			setRequiredError(expenseField, "Add Expenses", true);
			ret = false;
		} else
			setRequiredError(expenseField, null, false);

		if(payingAmountField.getValue()<0){
			setRequiredError(payingAmountField, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(payingAmountField, null, false);
		
		if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.PAYMENT){
			
			if (paymentModeCombo.getValue() == null || paymentModeCombo.getValue().equals("")) {
				setRequiredError(paymentModeCombo, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(paymentModeCombo, null, false);
			
			if(payingAmountField.getValue()<=0){
				setRequiredError(payingAmountField, getPropertyName("invalid_data"), true);
				ret = false;
			} else
				setRequiredError(payingAmountField, null, false);
		}
		if(settings.isDEPARTMENT_ENABLED()){
			if (departmentCombo.getValue() == null || departmentCombo.getValue().equals("")) 
				departmentCombo.setValue((long)0);	
		}
		else
			departmentCombo.setValue((long)0);
		
		if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID() && dateField.getValue() != null) {
			
			try {
				double value=new CommonMethodsDao().getCurrencyRate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 
															(Long)netAmountField.currencySelect.getValue());
				if(value==0){
					setRequiredError(netAmountField.rateButton, "Set Conversion Rate", true);
					ret = false;
				}
				else
					setRequiredError(netAmountField.rateButton, null, false);
				
			} catch (Exception e) {
				setRequiredError(netAmountField.rateButton, "Set Conversion Rate", true);
				ret = false;
				e.printStackTrace();
			}
		}
		
		if(discountRadio.getValue()==null)
			discountRadio.setValue(1);
		
		try {
			if(toDouble(discountPercentField.getValue().toString().trim())<0){
				discountPercentField.setNewValue("0.0");
			}
		} catch (Exception e1) {
			discountPercentField.setNewValue("0.0");
		}
		
		try {
			if(toDouble(discountAmountField.getValue().toString().trim())<0){
				discountAmountField.setNewValue("0.0");
			}
		} catch (Exception e1) {
			discountAmountField.setNewValue("0.0");
		}
		
		return ret;
	}

	
	public void calculateNetPrice(){
		if (quantityField.getValue() == null || quantityField.getValue().toString().equals("")) {
			quantityField.setValue("0");
		} else {
			try {
				if (Double.parseDouble(quantityField.getValue()) <0) {
					quantityField.setNewValue("0");
				} 
			} catch (Exception e) {
				quantityField.setNewValue("0");
			}
		}
		if(unitPriceField.getValue()<0){
			unitPriceField.setNewValue(0);
		}
		double netPrice;
		double discountPrice=0;
		if(toDouble(quantityField.getValue())>0){
		if((Integer) itemDiscountRadio.getValue()==1){
				discountPrice=roundNumber(unitPriceField.getValue()-roundNumber(unitPriceField.getValue()*toDouble(itemDiscountPercentField.getValue())/100));
		}else{
			discountPrice=(unitPriceField.getValue()-toDouble(itemDiscountAmountField.getValue()));
		}
		}
		try {
			netPrice = discountPrice*toDouble(quantityField.getValue());
		} catch (Exception e) {
			netPrice=0;
		}
		netPriceField.setNewValue(roundNumberToString(netPrice));
	}
	
	
	public void removeAllErrors() {
		if (unitPriceField.getComponentError() != null)
			setRequiredError(unitPriceField, null, false);
		if (quantityField.getComponentError() != null)
			setRequiredError(quantityField, null, false);
		if (itemCombo.getComponentError() != null)
			setRequiredError(itemCombo, null, false);
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		sendMailButton.setComponentError(null);
	}

	
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reloadConvertionRate() {
		Iterator itr = table.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			unitPriceField.setNewValue((Long)item.getItemProperty(TBC_CID).getValue(), (Double)item.getItemProperty(TBC_UNIT_PRICE).getValue());
			item.getItemProperty(TBC_CONV_RATE).setValue(CommonUtil.roundNumber(unitPriceField.getConversionRate()));
			double netPrice=(unitPriceField.getValue()*(Double)item.getItemProperty(TBC_QTY).getValue())
					+(Double)item.getItemProperty(TBC_TAX_AMOUNT).getValue()
					+(Double)item.getItemProperty(TBC_CESS).getValue()-
					(Double)item.getItemProperty(TBC_DISCOUNT).getValue();
			item.getItemProperty(TBC_NET_PRICE).setValue(CommonUtil.roundNumber(netPrice));
		}
		unitPriceField.setNewValue(getCurrencyID(), 0);
		calculateTotals();
	}*/
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	public SComboField getPurchaseNumberList() {
		return purchaseCombo;
	}

	
	public void setPurchaseNumberList(SComboField purchaseCombo) {
		this.purchaseCombo = purchaseCombo;
	}
	
	
	@Override
	public SComboField getBillNoFiled() {
		return purchaseCombo;
	}
}
