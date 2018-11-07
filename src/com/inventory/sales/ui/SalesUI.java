package com.inventory.sales.ui;

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
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.ui.AddCustomer;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.SalesTypeDao;
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
import com.inventory.model.ItemGroupModel;
import com.inventory.model.LocationModel;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.sales.bean.DeliveryNoteBean;
import com.inventory.sales.bean.SalesBean;
import com.inventory.sales.bean.SalesInventoryDetailsPojo;
import com.inventory.sales.dao.DeliveryNoteDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesExpenseDetailsModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesOrderDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
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
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
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
import com.webspark.business.AddressBusiness;
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
 * @author anil
 * @date 09-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class SalesUI extends SparkLogic {

	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_EDITABLE = "Editable";
	static String TBC_ITEM_ID = "Item Id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty Sold";
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
	
	static String TBC_GRADE_ID="Grade Id";
	static String TBC_GRADE="Grade";
	static String TBC_LOCATION_ID="Location Id";
	static String TBC_LOCATION="Location";
	
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_BATCH_ID = "Batch Id";
	static String TBC_STOCK_ID = "Stock Id";
	static String TBC_ORDER_CHILD_ID = "Order Child Id";
	static String TBC_ORDER_ID = "Order Id";
	static String TBC_DELIVERY_CHILD_ID = "Delivery Child Id";
	static String TBC_DELIVERY_ID = "Delivery Id";
	static String TBC_NET_PRICE_WITHOUT_DISCOUNT = "Total Price";

	SalesDao dao;
	DeliveryNoteDao delDao;
	SComboField salesNoCombo;
	STextField referenceNoField;
	SDateField dateField;
	SComboField locationMasterCombo;
	SButton newLocationButton;
	SComboField customerCombo;
	SButton newCustomerButton;
	SComboField responsilbeEmployeeCombo;
	STextField contactField;
	
	SRadioButton discountRadio;
	STextField discountPercentField;
	STextField discountAmountField;
	
	
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
	STextField convertedQuantityField;
	SComboField departmentCombo;
	SSelectionField divisionCombo;
	
	SNativeSelect unitSelect;
	SButton newUnitButton;
	SButton unitMapButton;
	
	SCurrencyField unitPriceField;
	
	STextField discountField;
	SNativeSelect taxSelect;
	
	private STextField barcodeField;
	
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
	
	SButton importButton;
	SButton importDeliveryButton;
	SButton importButtonSalesMan;
	SButton importDeliverySalesMan;
	SButton addOrderButton;
	SButton addDeliveryButton;
	SOptionGroup salesOrderOptions;	
	SWindow salesOrderWindow;
	SOptionGroup deliveryOptions;	
	SWindow salesGRNWindow;
	
	SNativeSelect salesTypeSelect;
	

	ItemDao itemDao = new ItemDao();

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SWindow popupWindow;

	SCurrencyField netAmountField;
	SCurrencyField convertedField;
	STextArea comment;

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	boolean taxEnable = isTaxEnable();
	

	CommonMethodsDao comDao;
	ItemDao itmDao;
	TaxDao taxDao;
	UnitDao untDao;
	CustomerDao supDao;

	SButton createNewButton;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	SListSelect stockSelectList;
	private SButton changeStkButton;
	private SButton stkDoneButton;
	SHorizontalLayout itemLay;
	
	STextField expenseField;
	SButton addExpenseButton;
	ExpenditurePanel expensePanel;
	Date previousDate;
	SCurrencyField payingAmountField;
	
	SRadioButton paymentCreditRadio;
	SRadioButton cashChequeRadio;
	SComboField salesAccountCombo;
	SDateField chequeDateField;
	STextField chequenumberField;
	
	SHorizontalLayout popupHor;
	SButton priceListButton;
	
	PrivilageSetupDao privDao;
	private SComboField itemGroupCombo ;
	private List itemGroupList;
	private SRadioButton itemDiscountRadio;
	private STextField itemDiscountPercentField;
	private STextField itemDiscountAmountField;
	private SComboField salesOrderCombo;
	
	SComboField paymentModeCombo;
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		
		previousDate=getWorkingDate();
		dao=new SalesDao();
		delDao=new DeliveryNoteDao();
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		taxEnable = isTaxEnable();
		boolean orderEnabled = settings.isSALES_ORDER_FOR_SALES();
		
		comDao = new CommonMethodsDao();
		itmDao = new ItemDao();
		taxDao = new TaxDao();
		untDao = new UnitDao();
		supDao=new CustomerDao();
		privDao=new PrivilageSetupDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Sales Inquiry");
		
		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_EDITABLE, 
				TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, 
				TBC_CONVERTION_QTY, TBC_QTY_IN_BASIC_UNIT, TBC_QTY_ORDERED, TBC_BALANCE, 
				TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE, TBC_CID, TBC_CURRENCY, TBC_CONV_RATE,
				TBC_DISCOUNT_TYPE,TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT,TBC_DISCOUNT_UNIT_PRICE,
				TBC_TAX_ID,TBC_TAX_PERCENTAGE, TBC_TAX_AMOUNT, TBC_CESS,
				TBC_NET_PRICE,  TBC_GRADE_ID, TBC_GRADE, TBC_LOCATION_ID, TBC_LOCATION, 
				TBC_BATCH_ID, TBC_STOCK_ID, TBC_ORDER_CHILD_ID, TBC_ORDER_ID,TBC_DELIVERY_CHILD_ID,TBC_DELIVERY_ID,TBC_NET_PRICE_WITHOUT_DISCOUNT };
		
		
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, 
					/* TBC_QTY_ORDERED,TBC_BALANCE, */ TBC_UNIT, TBC_UNIT_PRICE, TBC_CURRENCY 
					,TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT,TBC_DISCOUNT_UNIT_PRICE, TBC_TAX_AMOUNT, TBC_CESS,  
					TBC_GRADE, TBC_LOCATION,TBC_NET_PRICE,TBC_NET_PRICE_WITHOUT_DISCOUNT };

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
		requiredHeaders = tempList.toArray(new String[tempList.size()]);
		setSize(1350, 625);

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		vLayout = new SVerticalLayout();
		formLayout = new SFormLayout();

		itemLayout = new SGridLayout(18, 2);
		itemLayout.setSizeFull();

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(12);
		masterDetailsGrid.setRows(4);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(8);
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
			
			stockSelectList = new SListSelect(getPropertyName("stock"));
			stockSelectList.setHeight(200 + "px");
			stockSelectList.setWidth("400px");
			stockSelectList.setMultiSelect(false);
			
			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);
			salesNoCombo = new SComboField(null, 120, null, "id","ref_no", false, getPropertyName("create_new"));
			referenceNoField = new STextField(null, 150);
			dateField = new SDateField(null, 100, getDateFormat(), new Date(getWorkingDate().getTime()));
			customerCombo = new SComboField(null, 150, new LedgerDao().getAllCustomers(getOfficeID()), 
					"id", "name", true, getPropertyName("select"));
			newCustomerButton = new SButton();
			newCustomerButton.setStyleName("addNewBtnStyle");
			newCustomerButton.setDescription("Add New Customer");
			responsilbeEmployeeCombo = new SComboField(null,150,
					new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name", false, getPropertyName("select"));
			contactField = new STextField(null, 150);
			contactField.setReadOnly(true);
			
			paymentModeCombo=new SComboField(getPropertyName("payment_mode"), 150, new PaymentModeDao().getAllPaymentModeList(getOfficeID()), "id", "description", true, getPropertyName("select"));
			
			List<KeyValue> discountMethod = Arrays.asList(new KeyValue(1, "Percentage"), new KeyValue(2, "Amount"));
			discountRadio=new SRadioButton(getPropertyName("discount_type"), 200, discountMethod, "intKey", "value");
			discountPercentField=new STextField(getPropertyName("percentage"), 100);
			discountAmountField=new STextField(getPropertyName("amount"), 100);
			discountPercentField.setStyleName("textfield_align_right");
			discountAmountField.setStyleName("textfield_align_right");
			discountRadio.setHorizontal(true);
			discountRadio.setImmediate(true);
			discountPercentField.setImmediate(true);
			discountAmountField.setImmediate(true);
			
			List departmentList=new ArrayList();
			departmentList.add(0, new DepartmentModel(0, "None"));
			departmentList.addAll(new DepartmentDao().getDepartments(getOrganizationID()));
			divisionCombo = new SSelectionField(null,getPropertyName("none"),200, 400);
			divisionCombo.setContainerData(new DivisionDao().getDivisionsHierarchy(getOrganizationID()));
			departmentCombo = new SComboField(null, 100, departmentList,"id", "name", false, getPropertyName("select"));
			departmentCombo.setValue((long)0);
			
			salesTypeSelect = new SNativeSelect(null, 90,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");

			Iterator itt = salesTypeSelect.getItemIds().iterator();
			if (itt.hasNext())
				salesTypeSelect.setValue(itt.next());
			
			List orderList = new ArrayList();
			orderList.add(new SalesOrderModel(0, getPropertyName("all")));
			orderList.addAll(new SalesOrderDao().getAllSalesOrdersUnderOffice(getOfficeID()));

			salesOrderCombo = new SComboField(null, 80, orderList, "id","order_no");
			salesOrderCombo.setInputPrompt( getPropertyName("select"));
			SLabel salesNumlabel=new SLabel(getPropertyName("sales_number"));
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			if(orderEnabled){
				salLisrLay.addComponent(salesOrderCombo);
				salLisrLay.addComponent(salesNumlabel);
			}
			salLisrLay.addComponent(salesNoCombo);
			salLisrLay.addComponent(createNewButton);
			salLisrLay.setSpacing(true);
			salLisrLay.setComponentAlignment(salesNoCombo,Alignment.MIDDLE_CENTER);
			salLisrLay.setComponentAlignment(createNewButton,Alignment.MIDDLE_CENTER);
			
			SHorizontalLayout customerLayout = new SHorizontalLayout();
			customerLayout.addComponent(customerCombo);
			customerLayout.addComponent(newCustomerButton);
			
			newLocationButton = new SButton();
			newLocationButton.setStyleName("addNewBtnStyle");
			newLocationButton.setDescription("Add New Location");
			
			importButton = new SButton(null, "Import From Sales Order");
			importButton.setStyleName("importButtonStyle");
			
			importButtonSalesMan = new SButton(null, "Import From Sales Order");
			importButtonSalesMan.setStyleName("importButtonStyle");
			
			importDeliveryButton = new SButton(null, "Import From Delivery Note");
			importDeliveryButton.setStyleName("importButtonStyleNew");
			
			importDeliverySalesMan = new SButton(null, "Import From Delivery Note");
			importDeliverySalesMan.setStyleName("importButtonStyleNew");
			
			List locationList=new ArrayList();
			locationList.add(0, new LocationModel(0, "None"));
			locationList.addAll(new LocationDao().getLocationModelList(getOfficeID()));
			locationMasterCombo=new SComboField(null, 90, locationList, "id", "name", true, getPropertyName("select"));
			locationMasterCombo.setValue((long)0);
			
			SHorizontalLayout locationLayout = new SHorizontalLayout();
			locationLayout.addComponent(locationMasterCombo);
			locationLayout.addComponent(newLocationButton);
			
			if(orderEnabled)
				masterDetailsGrid.addComponent(new SLabel("Order No"), 1, 0);
			else
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("sales_number")), 1, 0);
			
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(referenceNoField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			masterDetailsGrid.addComponent(dateField, 6, 0);
			masterDetailsGrid.addComponent(new SLabel("Location"), 7, 0);
			masterDetailsGrid.addComponent(locationLayout, 8, 0);
			
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(referenceNoField,Alignment.MIDDLE_LEFT);
			masterDetailsGrid.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 1.5f);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2f);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 1);
			masterDetailsGrid.setColumnExpandRatio(7, 1.5f);

			SHorizontalLayout salesManLayout=new SHorizontalLayout();
			salesManLayout.setSpacing(true);
			
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("customer")), 1, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("sales_man")), 3, 1);
			
			salesManLayout.addComponent(responsilbeEmployeeCombo);
			
			if(!settings.isSALES_MAN_WISE_SALES()){
				customerLayout.addComponent(importButton);
				customerLayout.addComponent(importDeliveryButton);	
				customerLayout.setComponentAlignment(importButton, Alignment.TOP_CENTER);
				customerLayout.setComponentAlignment(importDeliveryButton, Alignment.TOP_CENTER);
			}
			else{
				salesManLayout.addComponent(importButtonSalesMan);
				salesManLayout.addComponent(importDeliverySalesMan);
				salesManLayout.setComponentAlignment(importButtonSalesMan, Alignment.TOP_CENTER);
				salesManLayout.setComponentAlignment(importDeliverySalesMan, Alignment.TOP_CENTER);
			}
			masterDetailsGrid.addComponent(customerLayout, 2, 1);
			masterDetailsGrid.addComponent(salesManLayout, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("contact")), 5, 1);
			masterDetailsGrid.addComponent(contactField, 6, 1);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_type")), 7, 1);
			masterDetailsGrid.addComponent(salesTypeSelect, 8, 1);
			
			if(settings.isDIVISION_ENABLED()){
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("Division")),  1,2);
				masterDetailsGrid.addComponent(divisionCombo, 2,2);
			}
			
			if(settings.isDEPARTMENT_ENABLED()){
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("department")), 3,2);
				masterDetailsGrid.addComponent(departmentCombo, 4,2);
			}
			
			masterDetailsGrid.setStyleName("master_border");
			
			List locationSubList=new ArrayList();
			locationSubList.add(0, new LocationModel(0, "None"));
			locationSubList.addAll(new LocationDao().getLocationModelList(getOfficeID()));
			locationCombo=new SComboField("Location", 100, locationSubList, "id", "name", true, getPropertyName("select"));
			locationCombo.setValue((long)0);
			
			barcodeField = new STextField(getPropertyName("barcode"), 80);
			barcodeField.setImmediate(true);
			
			itemGroupList = new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID());
			itemGroupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
			
			itemGroupCombo = new SComboField(getPropertyName("item_group"), 100, itemGroupList, "id", "name", true, getPropertyName("all"));
			itemGroupCombo.setValue((long)0);

			itemCombo = new SComboField(getPropertyName("item"), 100,
					dao.getAllActiveItemsWithAppendingItemCode(getOfficeID(),settings.isITEMS_IN_MULTIPLE_LANGUAGE()), "id", "name",true,getPropertyName("select"));
			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");
			newItemButton.setDescription("Add New Item");
			
			newUnitButton = new SButton();
			newUnitButton.setStyleName("smallAddNewBtnStyle");
			newUnitButton.setDescription(getPropertyName("add_new_unit"));
			unitMapButton = new SButton();
			unitMapButton.setStyleName("mapBtnStyle");
			unitMapButton
					.setDescription(getPropertyName("set_convertion_quantity"));
			
			changeStkButton = new SButton();
			changeStkButton.setStyleName("loadAllBtnStyle");
			changeStkButton.setDescription("Change Stock");
			
			stkDoneButton = new SButton(getPropertyName("done"));

			itemLay = new SHorizontalLayout();
			if (settings.isBARCODE_ENABLED())
				itemLay.addComponent(barcodeField);
			
			if(settings.isITEM_GROUP_FILTER_IN_SALES()){
				itemLay.addComponent(itemGroupCombo);
			}
			itemLay.addComponent(itemCombo);
			itemLay.addComponent(newItemButton);
			itemLay.addComponent(changeStkButton);
			
			SVerticalLayout lay = new SVerticalLayout();
			lay.addComponent(locationCombo);
			lay.addComponent(stockSelectList);
			lay.addComponent(stkDoneButton);
			lay.setComponentAlignment(stkDoneButton, Alignment.MIDDLE_CENTER);
			final SPopupView pop = new SPopupView("", lay);
			itemLay.addComponent(pop);
			
			itemLay.setComponentAlignment(newItemButton, Alignment.BOTTOM_CENTER);
			itemLay.setComponentAlignment(changeStkButton, Alignment.BOTTOM_CENTER);
			
			quantityField = new STextField(getPropertyName("quantity"), 60);
			quantityField.setStyleName("textfield_align_right");
			quantityField.setValue("0");
			convertionQuantityField = new STextField(getPropertyName("convertion_qty"), 40);
			convertionQuantityField.setStyleName("textfield_align_right");
			convertionQuantityField.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");
			
			List gradeList=new ArrayList();
			gradeList.add(0, new GradeModel(0, "None"));
			gradeList.addAll(new GradeDao().getAllGrades(getOfficeID()));
			gradeCombo=new SComboField("Grade", 100, gradeList, "id", "name", true, getPropertyName("select"));
			gradeCombo.setValue((long)0);
		
			convertedQuantityField = new STextField(getPropertyName("converted_qty"), 60);
			convertedQuantityField.setStyleName("textfield_align_right");
			convertedQuantityField.setDescription("Converted Quantity in Basic Unit");
			convertedQuantityField.setReadOnly(true);
			
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			taxSelect = new SNativeSelect(getPropertyName("tax"), 75,taxDao.getAllActiveTaxesFromType(getOfficeID(),SConstants.tax.SALES_TAX), "id", "name");
			taxSelect.setValue(SConstants.tax.SALES_TAX);
			discountField = new STextField(getPropertyName("discount"),50);
			discountField.setValue("0");
			unitPriceField=new SCurrencyField(getPropertyName("unit_price"), 50, getWorkingDate());
			unitPriceField.setStyleName("textfield_align_right");
			unitPriceField.currencySelect.setReadOnly(true);
			unitPriceField.setNewValue(getCurrencyID(), 0);
			
			if (!settings.isSALE_PRICE_EDITABLE()) {
				unitPriceField.setReadOnly(true);
			}
			priceListButton = new SButton();
			priceListButton.setDescription("Price History");
			priceListButton.setStyleName("showHistoryBtnStyle");
			
			popupHor = new SHorizontalLayout();
			popupHor.addComponent(unitPriceField);
			popupHor.addComponent(priceListButton);
			popupHor.setComponentAlignment(priceListButton,
					Alignment.BOTTOM_LEFT);

			netPriceField = new STextField(getPropertyName("net_price"),75);
			netPriceField.setNewValue("0.00");
			netPriceField.setStyleName("textfield_align_right");
			
			netAmountField = new SCurrencyField(null, 100, getWorkingDate());
			netAmountField.amountField.setReadOnly(true);
			netAmountField.setStyleName("textfield_align_right");
			convertedField = new SCurrencyField(null, 75,getWorkingDate());
			convertedField.setReadOnly(true);

			netPriceField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);
			
			SHorizontalLayout hrz2 = new SHorizontalLayout();
			hrz2.addComponent(unitSelect);
			
			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			vert.setSpacing(false);
			hrz2.addComponent(vert);
			hrz2.setComponentAlignment(vert, Alignment.MIDDLE_CENTER);
			hrz2.setSpacing(true);
			
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
			if(settings.isGRADING_ENABLED()){
				itemLayout.addComponent(gradeCombo, 2, 1);
			}
			itemLayout.addComponent(quantityField, 3, 1);
			itemLayout.addComponent(convertionQuantityField, 4, 1);
			itemLayout.addComponent(convertedQuantityField, 5, 1);
			itemLayout.addComponent(hrz2, 6, 1);
			
			itemLayout.addComponent(popupHor, 8, 1);
			
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
			
//			itemLayout.addComponent(locationCombo, 12, 1);
			itemLayout.addComponent(netPriceField, 11, 1);
			itemLayout.addComponent(buttonLay, 12, 1);
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

			table = new STable(null, 1250, 200);
//			{
//				 @Override
//				    protected String formatPropertyValue(Object rowId,
//				            Object colId, Property property) {
//				        if (property.getType() == Double.class) {
//				            return new BigDecimal(String.valueOf(property.getValue())).setScale(toInt(session.getAttribute("no_of_precisions").toString())).toString();
//				        }
//
//				        return super.formatPropertyValue(rowId, colId, property);
//				    }
//			};

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
			table.addContainerProperty(TBC_CURRENCY, String.class, null, getPropertyName("currency"), null, Align.CENTER);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null, TBC_CONV_RATE, null, Align.LEFT);
			
			table.addContainerProperty(TBC_DISCOUNT_TYPE, Integer.class, null,"Discount Type", null, Align.CENTER);
			table.addContainerProperty(TBC_DISCOUNT_PERCENTAGE, Double.class,null, getPropertyName("discount") + " %", null,Align.CENTER);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null, getPropertyName("discount"), null, Align.LEFT);
			table.addContainerProperty(TBC_DISCOUNT_UNIT_PRICE, Double.class, null,getPropertyName("discounted_price"), null, Align.CENTER);
			
			table.addContainerProperty(TBC_TAX_ID, Long.class, null, TBC_TAX_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_PERCENTAGE, Double.class, null, TBC_TAX_PERCENTAGE, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_AMOUNT, Double.class, null, TBC_TAX_AMOUNT, null, Align.LEFT);
			table.addContainerProperty(TBC_CESS, Double.class, null, TBC_CESS, null, Align.LEFT);
			
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null, getPropertyName("net_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_PRICE_WITHOUT_DISCOUNT, Double.class, null, getPropertyName("total_price"), null, Align.RIGHT);
			
			table.addContainerProperty(TBC_GRADE_ID, Long.class, null, TBC_GRADE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_GRADE, String.class, null, TBC_GRADE, null, Align.LEFT);
			table.addContainerProperty(TBC_LOCATION_ID, Long.class, null, TBC_LOCATION_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_LOCATION, String.class, null, getPropertyName("location"), null, Align.LEFT);
			
			table.addContainerProperty(TBC_BATCH_ID, Long.class, null,TBC_BATCH_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,TBC_STOCK_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ORDER_CHILD_ID, Long.class, null,TBC_ORDER_CHILD_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ORDER_ID, Long.class, null,TBC_ORDER_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DELIVERY_CHILD_ID, Long.class, null,TBC_DELIVERY_CHILD_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DELIVERY_ID, Long.class, null,TBC_DELIVERY_ID, null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, 0.35f);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 0.75f);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 1.5f);
			table.setColumnExpandRatio(TBC_QTY, 0.5f);
			table.setColumnExpandRatio(TBC_QTY_ORDERED, 0.5f);
			table.setColumnExpandRatio(TBC_BALANCE, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 0.75f);
			table.setColumnExpandRatio(TBC_CURRENCY, 0.75f);
			table.setColumnExpandRatio(TBC_NET_PRICE, 0.75f);
			table.setColumnExpandRatio(TBC_NET_PRICE_WITHOUT_DISCOUNT, 0.75f);
			
			table.setColumnExpandRatio(TBC_GRADE, 1f);
			table.setColumnExpandRatio(TBC_LOCATION, 1f);
			

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));

			table.setPageLength(table.size());

			table.setWidth("1250");
			table.setHeight("200");

			comment = new STextArea(null, 400, 50);
			
			expensePanel=new ExpenditurePanel(getPropertyName("expenses"));
			
			expenseField = new STextField(null, 80, "0.0");
			expenseField.setReadOnly(true);
			expenseField.setStyleName("textfield_align_right");
			
			addExpenseButton=new SButton(null,"Add Expenses");
			addExpenseButton.setPrimaryStyleName("addBtnStyle");
			
			SHorizontalLayout expenseLayout=new SHorizontalLayout();
			expenseLayout.addComponent(expenseField);
			expenseLayout.addComponent(addExpenseButton);
			
			payingAmountField = new SCurrencyField(null, 80, getWorkingDate());
			payingAmountField.currencySelect.setReadOnly(true);
			payingAmountField.rateButton.setVisible(false);
			payingAmountField.setStyleName("textfield_align_right");
			payingAmountField.setNewValue(getCurrencyID(), 0);

			paymentCreditRadio=new SRadioButton(null, 200, SConstants.paymentMode.paymentModeList, "key", "value");
			paymentCreditRadio.setHorizontal(true);
			cashChequeRadio=new SRadioButton(null, 200, SConstants.paymentMode.cashChequeList, "key", "value");
			cashChequeRadio.setHorizontal(true);
			paymentCreditRadio.setImmediate(true);
			cashChequeRadio.setImmediate(true);
			
			salesAccountCombo = new SComboField(getPropertyName("sales_account"), 150,new LedgerDao().getAllActiveGeneralLedgerOnly(getOfficeID()), "id", "name");
			salesAccountCombo.setValue(settings.getSALES_ACCOUNT());

			SHorizontalLayout commentLayout=new SHorizontalLayout();
			commentLayout.setSpacing(true);
			SHorizontalLayout expenseMainLayout=new SHorizontalLayout();
			expenseMainLayout.setSpacing(true);
			final SHorizontalLayout payingAmountLayout=new SHorizontalLayout();
			payingAmountLayout.setSpacing(true);
			SHorizontalLayout netAmountLayout=new SHorizontalLayout();
			netAmountLayout.setSpacing(true);
			
			commentLayout.addComponent(new SLabel(getPropertyName("comment")));
			commentLayout.addComponent(comment);
			
			expenseMainLayout.addComponent(new SLabel(getPropertyName("Expense")));
			expenseMainLayout.addComponent(expenseLayout);
			
			payingAmountLayout.addComponent(new SLabel(getPropertyName("paying_amount")));
			payingAmountLayout.addComponent(payingAmountField);
			payingAmountLayout.setVisible(false);
			
			netAmountLayout.addComponent(new SLabel(getPropertyName("net_amount")));
			netAmountLayout.addComponent(netAmountField);
			
			bottomGrid.addComponent(commentLayout, 0, 1);
			bottomGrid.addComponent(expenseMainLayout, 1, 1);
			bottomGrid.addComponent(payingAmountLayout, 2, 1);
			bottomGrid.addComponent(convertedField, 3, 1);
			bottomGrid.addComponent(netAmountLayout, 4, 1);
			convertedField.setVisible(false);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			
			
			addOrderButton = new SButton(null,"Import Sales Orders");
			addOrderButton.setStyleName("importButtonStyle");
			
			addDeliveryButton = new SButton(null,"Import Delivery Note");
			addDeliveryButton.setStyleName("importButtonStyleNew");
			
			salesOrderOptions = new SOptionGroup("Select Sales Order : ", 300, null, "id", "name", true);
			salesOrderWindow = new SWindow("Sales Order", 650, 250);
			salesOrderWindow.center();
			salesOrderWindow.setResizable(false);
			salesOrderWindow.setModal(true);
			
			deliveryOptions = new SOptionGroup("Select Delivery Note : ", 300, null, "id", "name", true);
			salesGRNWindow = new SWindow("Delivery Note", 650, 250);
			salesGRNWindow.center();
			salesGRNWindow.setResizable(false);
			salesGRNWindow.setModal(true);
			
			SFormLayout popUpLayout = new SFormLayout();
			popUpLayout.setSpacing(true);
			popUpLayout.setMargin(true);
			popUpLayout.addComponent(salesOrderOptions);
			popUpLayout.addComponent(addOrderButton);
			salesOrderWindow.setContent(popUpLayout);

			SFormLayout popUpLayoutNew = new SFormLayout();
			popUpLayoutNew.setSpacing(true);
			popUpLayoutNew.setMargin(true);
			popUpLayoutNew.addComponent(deliveryOptions);
			popUpLayoutNew.addComponent(addDeliveryButton);
			salesGRNWindow.setContent(popUpLayoutNew);
			
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
							SConstants.privilegeTypes.EDIT_SALES, getLoginID());
				} catch (Exception e) {
					updateAvail=false;
				}
				try {
					printAvail = privDao.isFacilityAccessibleToUser(
							getOfficeID(),
							SConstants.privilegeTypes.PRINT_SALE, getLoginID());
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
			
			chequeDateField=new SDateField(getPropertyName("Cheque Date"),100,getDateFormat(),getWorkingDate());
			chequenumberField=new STextField(getPropertyName("Cheque No"),100);
			chequeDateField.setVisible(false);
			chequenumberField.setVisible(false);
			
			SHorizontalLayout radioLayout=new SHorizontalLayout();
			radioLayout.setSpacing(true);
			radioLayout.addComponent(salesAccountCombo);
			radioLayout.addComponent(paymentCreditRadio);
//			radioLayout.addComponent(paymentModeCombo);
			
			SHorizontalLayout discMainLay=new SHorizontalLayout();
			discMainLay.setSpacing(true);
			discMainLay.setStyleName("layout_light_bordered");
			discMainLay.addComponent(discountRadio);
			discMainLay.addComponent(discountPercentField);
			discMainLay.addComponent(discountAmountField);
			radioLayout.addComponent(discMainLay);
			
			if(!settings.isSALES_DISCOUNT_ENABLE()){
					discountRadio.setVisible(false);
					discountPercentField.setVisible(false);
					discountAmountField.setVisible(false);
			}else{
				boolean discountEnable=true;
				try {
					discountEnable = privDao.isFacilityAccessibleToUser(
							getOfficeID(),
							SConstants.privilegeTypes.SALES_DISCOUNT_ENABLED, getLoginID());
				} catch (Exception e) {
					discountEnable=false;
				}
				
				if(!discountEnable){
					discountRadio.setVisible(false);
					discountPercentField.setVisible(false);
					discountAmountField.setVisible(false);
				}
			}
			
			radioLayout.addComponent(cashChequeRadio);
			radioLayout.addComponent(chequenumberField);
			radioLayout.addComponent(chequeDateField);
			radioLayout.setComponentAlignment(cashChequeRadio, Alignment.MIDDLE_CENTER);
			radioLayout.setComponentAlignment(paymentCreditRadio, Alignment.MIDDLE_CENTER);

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
			loadOptions(0,0);
			
			salesOrderCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (salesOrderCombo.getValue() != null && !salesOrderCombo.getValue().equals("0")) {
							List salesList=dao.getAllSalesUnderSalesOrder(getOfficeID(), (Long)salesOrderCombo.getValue());
							System.out.println("SalesList Size------"+salesList.size());
							loadOptions(0,(Long)salesOrderCombo.getValue());
						}else{
							loadOptions(0,0);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
				
			
			discountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(discountRadio.getValue()!=null){
						if((Integer)discountRadio.getValue()==1){
							discountPercentField.setNewValue("0.0");	
							discountAmountField.setNewValue("0.0");
							discountPercentField.setReadOnly(false);
							discountAmountField.setReadOnly(true);
						}
						else{
							discountPercentField.setNewValue("0.0");	
							discountAmountField.setNewValue("0.0");
							discountPercentField.setReadOnly(true);
							discountAmountField.setReadOnly(false);
						}
					}
					else{
						discountPercentField.setNewValue("0.0");	
						discountAmountField.setNewValue("0.0");
						discountPercentField.setReadOnly(false);
						discountAmountField.setReadOnly(false);
					}
				}
			});
			discountRadio.setValue(1);
			calculateTotals();
			
			discountPercentField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					double total=roundNumber(toDouble(table.getColumnFooter(TBC_NET_PRICE).trim()));
					double expense=roundNumber(expensePanel.getDebitAmount());
					double discountPer=0, discount=0;
					try {
						discountPer=toDouble(discountPercentField.getValue().toString().trim());
					} catch (Exception e) {
							discountPer=0;
					}
					discountPercentField.setNewValue(roundNumber(discountPer)+"");
					discount=roundNumber((total+expense)*discountPer/100);
					discountAmountField.setNewValue(roundNumber(discount)+"");
					calculateTotals();
				}
			});
			
			
			discountAmountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					calculateTotals();
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});
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
			
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)salesNoCombo.getValue(),confirmBox.getUserID());
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
						if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)salesNoCombo.getValue(),
									"Sales Order : No. "+salesNoCombo.getItemCaption(salesNoCombo.getValue()));
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
						if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")) {
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
			
			
			changeStkButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (itemCombo.getValue() != null
							&& unitSelect.getValue() != null) {

						try {
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						SPopupView pop = new SPopupView("", new SFormLayout(
								getPropertyName("select_item_unit"), 200, 80));
						itemLay.addComponent(pop);
						pop.setPopupVisible(true);
					}
				}
			});
			
			
			stkDoneButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					pop.setPopupVisible(false);
				}
			});
			
			
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
			
			
			table.addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
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
			});
*/
			
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
			
			
			addExpenseButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().addWindow(expensePanel);				
				}
			});
			
			
			newUnitButton.addClickListener(new ClickListener() {

				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {

					popupWindow.setContent(new AddNewUnitUI());

					popupWindow.setWidth("502");
					popupWindow.setHeight("455");

					popupWindow.center();
					popupWindow.setModal(true);

					popupWindow.setCaption(getPropertyName("add_new_unit"));

					getUI().getCurrent().addWindow(popupWindow);
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


			
			gradeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {

						if (itemCombo.getValue() != null
								&& !itemCombo.getValue().equals("")
								&& unitSelect.getValue() != null
								&& !unitSelect.getValue().equals("")) {
							GradeModel gradeModel = new GradeDao()
									.getGrade((Long) itemCombo.getValue());
							double price = comDao.getItemPrice(
									(Long) itemCombo.getValue(),
									(Long) unitSelect.getValue(),
									toLong(salesTypeSelect.getValue().toString()));
							if (gradeModel != null) {
								if (gradeModel.getPercentage() != 0)
									price = price * gradeModel.getPercentage()
											/ 100;
								else
									price = 0;
							}
							if(settings.isBARCODE_ENABLED()&&settings.getBARCODE_TYPE()==SConstants.barcode_types.CUSTOMER_SPECIFIC&&customerCombo.getValue()!=null){
								double perc=comDao.getCustomerSpecificPercentage((Long) itemCombo.getValue(),  (Long) customerCombo.getValue());
								price+=price*perc/100;
							}
							unitPriceField.setNewValue(roundNumber(price));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			locationCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(locationCombo.getValue()!=null && itemCombo.getValue()!=null){
							List lst=new ArrayList();
							lst = comDao.getStocksInLocation(
								(Long) itemCombo.getValue(),
								settings.isUSE_SALES_RATE_FROM_STOCK(),(Long)locationCombo.getValue());
							if(lst!=null){
								if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")) {
									Item item;
									List<Long> stkIDs=new ArrayList<Long>();
									Iterator it = table.getItemIds().iterator();
									while (it.hasNext()) {

										item = table.getItem(it.next());
										if(item.getItemProperty(TBC_ITEM_ID).getValue()==(Long) itemCombo.getValue()&&
												!item.getItemProperty(TBC_STOCK_ID).getValue().toString().equals("0"))
											stkIDs.add((Long)item.getItemProperty(TBC_STOCK_ID).getValue());
									}
									if(stkIDs.size()>0)
									lst.addAll(comDao.getUsedStocks(stkIDs, settings.isUSE_SALES_RATE_FROM_STOCK()));
							}

							SCollectionContainer bic1 = SCollectionContainer
									.setList(lst, "id");
							stockSelectList
									.setContainerDataSource(bic1);
							stockSelectList
									.setItemCaptionPropertyId("stock_details");

							long stk_id = comDao
									.getDefaultStockToSelect((Long) itemCombo
											.getValue());
							if (stk_id != 0)
								stockSelectList.setValue(stk_id);
							else {
								Iterator it = stockSelectList
										.getItemIds().iterator();
								if (it.hasNext())
									stockSelectList.setValue(it.next());
							}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
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
										expensePanel.setCurrency(netAmountField.getCurrency());
										expensePanel.amountField.setCurrencyDate(previousDate);
										expensePanel.reloadConvertionRate();
										netAmountField.currencySelect.setNewValue(null);
										netAmountField.currencySelect.setNewValue(id);
										unitPriceField.currencySelect.setNewValue(null);
										unitPriceField.currencySelect.setNewValue(cid);
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
							netAmountField.currencySelect.setNewValue(null);
							netAmountField.currencySelect.setNewValue(id);
							unitPriceField.currencySelect.setNewValue(null);
							unitPriceField.currencySelect.setNewValue(cid);
						}
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			if(settings.getCASH_ACCOUNT()==0 || settings.getCHEQUE_ACCOUNT()==0){
				SNotification.show("Account Settings Not Set", Type.ERROR_MESSAGE);
			}
			
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

			
			paymentCreditRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(paymentCreditRadio.getValue()!=null){
							if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.PAYMENT){
								cashChequeRadio.setVisible(false);
								payingAmountLayout.setVisible(true);
//								paymentModeCombo.setVisible(true);
								payingAmountField.setValue(0);
								cashChequeRadio.setValue(SConstants.paymentMode.CASH);
							}
							else if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.CREDIT){
								cashChequeRadio.setVisible(false);
								payingAmountLayout.setVisible(false);
//								paymentModeCombo.setVisible(false);
								payingAmountField.setValue(0);
								cashChequeRadio.setValue(SConstants.paymentMode.CASH);
//								List list=new ArrayList();
//								list=new LedgerDao().getAllActiveLedgerNamesExcluding(	getOfficeID(),
//																						SConstants.LEDGER_ADDED_INDIRECTLY, 
//																						settings.getCASH_GROUP());
//								SCollectionContainer bic=SCollectionContainer.setList(list, "id");
//								salesAccountCombo.setContainerDataSource(bic);
//								salesAccountCombo.setItemCaptionPropertyId("name");
//								salesAccountCombo.setValue(settings.getSALES_ACCOUNT());
//								salesAccountCombo.setInputPrompt(getPropertyName("select"));
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
//									salesAccountCombo.setContainerDataSource(bic);
//									salesAccountCombo.setItemCaptionPropertyId("name");
//									salesAccountCombo.setValue(null);
//									salesAccountCombo.setInputPrompt(getPropertyName("select"));
									
									chequeDateField.setVisible(false);
									chequenumberField.setVisible(false);
								}
								else if((Long)cashChequeRadio.getValue()==SConstants.paymentMode.CHEQUE){
//									List list=new ArrayList();
//									list=new BankAccountDao().getAllActiveBankAccountNamesWithLedgerID(getOfficeID());
//									SCollectionContainer bic=SCollectionContainer.setList(list, "id");
//									salesAccountCombo.setContainerDataSource(bic);
//									salesAccountCombo.setItemCaptionPropertyId("name");
//									salesAccountCombo.setValue(null);
//									salesAccountCombo.setInputPrompt(getPropertyName("select"));
									
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
			
			
			priceListButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (itemCombo.getValue() != null
							&& unitSelect.getValue() != null) {

						try {

							STable table = new STable(
									getPropertyName("sales_rate_history"));

							table.addContainerProperty("Date", Date.class,
									null, getPropertyName("date"), null,
									Align.CENTER);
							table.addContainerProperty("Customer Name",
									String.class, null,
									getPropertyName("customer_name"), null,
									Align.LEFT);
							table.addContainerProperty("Price", Double.class,
									null, getPropertyName("price"), null,
									Align.RIGHT);

							table.setVisibleColumns(new Object[] { "Date", "Customer Name", "Price" });

							List list = dao.getSalesRateHistory(
									(Long) itemCombo.getValue(),
									(Long) unitSelect.getValue(), getOfficeID());

							Iterator it = list.iterator();
							int i = 0;
							SalesInventoryDetailsPojo objIn;
							while (it.hasNext()) {
								objIn = (SalesInventoryDetailsPojo) it.next();
								i++;
								table.addItem(
										new Object[] { objIn.getDate(),
												objIn.getLedger_name(),
												objIn.getUnit_price() }, i);
							}

							table.setColumnExpandRatio("Date", (float) 0.5);
							table.setColumnExpandRatio("Customer Name",
									(float) 1);
							table.setColumnExpandRatio("Price", (float) 0.5);

							table.setWidth("400");
							table.setHeight("300");

							table.setSelectable(true);

							STable purchTable = new STable(
									getPropertyName("purchase_rate_history"));

							purchTable.addContainerProperty("Date", Date.class,
									null, getPropertyName("date"), null,
									Align.CENTER);
							purchTable.addContainerProperty("Supplier Name",
									String.class, null,
									getPropertyName("supplier_name"), null,
									Align.LEFT);
							purchTable
									.addContainerProperty("Price",
											Double.class, null,
											getPropertyName("price"), null,
											Align.RIGHT);

							purchTable.setVisibleColumns(new Object[] { "Date",
									"Supplier Name", "Price" });

							list =dao.getPurchaseRateHistory(
									(Long) itemCombo.getValue(),
									(Long) unitSelect.getValue(), getOfficeID());

							it = list.iterator();
							i = 0;
							InventoryDetailsPojo objInvDet = null;
							while (it.hasNext()) {
								objInvDet = (InventoryDetailsPojo) it.next();
								i++;
								purchTable.addItem(
										new Object[] { objInvDet.getDate(),
												objInvDet.getLedger_name(),
												objInvDet.getUnit_price() }, i);
							}

							purchTable
									.setColumnExpandRatio("Date", (float) 0.3);

							purchTable.setWidth("400");
							purchTable.setHeight("300");
							purchTable.setSelectable(true);

							SPopupView pop = new SPopupView("",
									new SHorizontalLayout(true, table,
											purchTable));

							popupHor.addComponent(pop);
							pop.setPopupVisible(true);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						SPopupView pop = new SPopupView("", new SLabel(
								getPropertyName("select_item_unit")));
						popupHor.addComponent(pop);
						pop.setPopupVisible(true);
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
			
			
			locationMasterCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(locationMasterCombo.getValue()!=null){
							locationCombo.setNewValue(locationMasterCombo.getValue());
						}
						else
							locationCombo.setNewValue((long)0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					try {
						List list=new ArrayList();
						list=new LedgerDao().getAllCustomers(getOfficeID());
						SCollectionContainer bic=SCollectionContainer.setList(list, "id");
						customerCombo.setContainerDataSource(bic);
						customerCombo.setItemCaptionPropertyId("name");
						customerCombo.setInputPrompt(getPropertyName("select"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};
			
			
			newCustomerButton.addClickListener(new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					AddCustomer customer=new AddCustomer();
					customer.center();
					customer.setCaption("Add Customer");
					getUI().getCurrent().addWindow(customer);
					customer.addCloseListener(closeListener);
				}
			});
			
			
			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					try {
						if (popupWindow.getId().equals("ITEM")) {
							List list=new ArrayList();
							list=dao.getAllActiveItemsWithAppendingItemCode(getOfficeID(),settings.isITEMS_IN_MULTIPLE_LANGUAGE());
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

			
			customerCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						
						List<Object> idList=new ArrayList<Object>();
						Iterator it=table.getItemIds().iterator();
						while (it.hasNext()) {
							Object obj=(Object)it.next();
							Item item = table.getItem(obj);
							long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
							if(id!=0)
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
						if (customerCombo.getValue() != null) {
							setRequiredError(customerCombo, null, false);
							customerCombo.setDescription("<h1><i>Current Balance</i> : "+ 
									roundNumberToString(Math.abs(comDao.getLedgerCurrentBalance((Long) customerCombo.getValue()))) + "</h1>");
							CustomerModel supObj = supDao.getCustomerFromLedger((Long) customerCombo.getValue());
							responsilbeEmployeeCombo.setValue(supObj.getResponsible_person());
							contactField.setNewValue(supObj.getAddress().getPhone()+", "+supObj.getAddress().getMobile());
							salesTypeSelect.setValue(supObj
									.getSales_type());
						} else{
							customerCombo.setDescription(null);
							responsilbeEmployeeCombo.setValue(null);
							contactField.setNewValue("");
							
							Iterator itt = salesTypeSelect.getItemIds()
									.iterator();
							if (itt.hasNext())
								salesTypeSelect.setValue(itt.next());
						}
						
						barcodeField.focus();
						barcodeField.setValue("");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesNoCombo.setValue((long) 0);
					salesNoCombo.setValue(null);
				}
			});
			
			salesTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					Object obj = unitSelect.getValue();
					unitSelect.setNewValue(null);
					unitSelect.setNewValue(obj);

					barcodeField.focus();
					barcodeField.setValue("");
				}
			});
			
			importButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					setRequiredError(customerCombo, null, false);
					try {
						if(customerCombo.getValue() != null && !customerCombo.getValue().toString().equals("")){
							List<Long> idList=new ArrayList<Long>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
								if(id!=0)
									idList.add(id);
							}
							List orderList=new ArrayList();
							orderList=delDao.getSalesOrderModelCustomerList(getOfficeID(), (Long)customerCombo.getValue(), idList);
							if(orderList.size()>0){
								SCollectionContainer bic=SCollectionContainer.setList(orderList, "id");
								salesOrderOptions.setContainerDataSource(bic);
								salesOrderOptions.setItemCaptionPropertyId("order_no");
								getUI().getCurrent().addWindow(salesOrderWindow);
							}
							else{
								SNotification.show("No Sales Order Available",Type.WARNING_MESSAGE);
							}
						}
						else
							setRequiredError(customerCombo, "Select Customer", true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			
			importButtonSalesMan.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					setRequiredError(customerCombo, null, false);
					try {
						if(responsilbeEmployeeCombo.getValue() != null && !responsilbeEmployeeCombo.getValue().toString().equals("")){
							List<Long> idList=new ArrayList<Long>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
								if(id!=0)
									idList.add(id);
							}
							List orderList=new ArrayList();
							orderList=delDao.getSalesOrderModelSalesManList(getOfficeID(), (Long)responsilbeEmployeeCombo.getValue(), idList);
							if(orderList.size()>0){
								SCollectionContainer bic=SCollectionContainer.setList(orderList, "id");
								salesOrderOptions.setContainerDataSource(bic);
								salesOrderOptions.setItemCaptionPropertyId("order_no");
								getUI().getCurrent().addWindow(salesOrderWindow);
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else
							setRequiredError(responsilbeEmployeeCombo, getPropertyName("invalid_selection"), true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			addOrderButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (salesOrderOptions.getValue() != null && ((Set<Long>) salesOrderOptions.getValue()).size() > 0) {
							table.setVisibleColumns(allHeaders);
							Set<Long> salesOrders = (Set<Long>) salesOrderOptions.getValue();
							List orderList=new ArrayList();
							orderList=delDao.getAllDataFromSalesOrderWithCustomer(salesOrders);
							double qty=0,qtySold=0,qtyBasic=0;
							double conversionQty=1;
							SalesOrderModel orderModel;
							long prevId=0;
							
							Iterator itr = orderList.iterator();
							while (itr.hasNext()) {
								DeliveryNoteBean bean = (DeliveryNoteBean) itr.next();
								
								customerCombo.setValue(bean.getCustId());
								
								long taxId=SConstants.tax.SALES_TAX;
								double taxPer=0;
								double taxAmount=0;
								double cess=0;
								
								qty=0;qtySold=0;qtyBasic=0;conversionQty=1;
								long stockId=comDao.getDefaultStockToSelect(bean.getDet().getItem().getId());
								if(stockId!=0){
									qty=roundNumber(bean.getDet().getQunatity());
									conversionQty=roundNumber(bean.getDet().getQty_in_basic_unit()/bean.getDet().getQunatity());
									qtyBasic=roundNumber(bean.getDet().getQty_in_basic_unit());
									qtySold=roundNumber(bean.getDet().getQty_in_basic_unit()-bean.getDet().getQuantity_sold());
								}
								
								if(bean.getDet().getQuantity_sold()<qtyBasic){
								
								String itemName="";
								if(settings.isITEMS_IN_MULTIPLE_LANGUAGE())
									itemName=bean.getDet().getItem().getName()+"/"+bean.getDet().getItem().getSecondName();
								else
									itemName=bean.getDet().getItem().getName();
								
								
								double amount=bean.getDet().getUnit_price();
								double	discPer = bean.getDet().getDiscountPercentage();
								double	discount_amt = bean.getDet().getDiscount();
								double discountPrice=0;
								if(bean.getDet().getDiscount_type()==1){
									discountPrice=roundNumber(amount-roundNumber(amount*discPer/100));
								}else{
									discountPrice=(amount-discount_amt);
								}
								double netPrice=(discountPrice*bean.getDet().getQunatity())+taxAmount+cess;
								double totalPrice=(amount*bean.getDet().getQunatity())+taxAmount+cess;
								
								String currency=new CurrencyManagementDao().getselecteditem(bean.getDet().getCurrencyId()).getCode();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										true,
										bean.getDet().getItem().getId(),
										bean.getDet().getItem().getItem_code(),
										itemName,
										qtySold/conversionQty,
										conversionQty,
										qtyBasic,
										qtyBasic,
										qtySold,
										bean.getDet().getUnit().getId(),
										bean.getDet().getUnit().getSymbol(),
										roundNumber(bean.getDet().getUnit_price()),
										bean.getDet().getCurrencyId(),
										currency,
										roundNumber(bean.getDet().getConversionRate()),
										bean.getDet().getDiscount_type(),
										roundNumber(discPer),
										roundNumber(discount_amt),
										roundNumber(discountPrice),
										taxId,
										taxPer,
										taxAmount,
										cess,
										roundNumber(netPrice),
										(long)0,
										"None",
										(long)0,
										"None",
										(long)0,
										stockId,
										bean.getDet().getId(),
										bean.getId(),
										(long)0,
										(long)0,roundNumber(totalPrice)},table.getItemIds().size()+1);
								
								if(prevId!=bean.getId()){
									orderModel=bean.getSalesOrder();
									discountRadio.setValue(orderModel.getDiscount_type());
									discountPercentField.setNewValue(orderModel.getDiscountPercentage()+"");
									discountAmountField.setNewValue(orderModel.getDiscountAmount()+"");
									
									prevId=bean.getId();
								}
							}
							}
							getUI().removeWindow(salesOrderWindow);
							
							
							
							calculateTotals();
							calculateNetPrice();
							table.setVisibleColumns(requiredHeaders);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			importDeliveryButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					setRequiredError(customerCombo, null, false);
					try {
						if(customerCombo.getValue() != null && !customerCombo.getValue().toString().equals("")){
							List<Long> idList=new ArrayList<Long>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								long id=toLong(item.getItemProperty(TBC_DELIVERY_ID).getValue().toString());
								if(id!=0)
									idList.add(id);
							}
							List grnList=new ArrayList();
							grnList=dao.getDeliveryNoteModelCustomerList(getOfficeID(), (Long)customerCombo.getValue(), idList);
							if(grnList.size()>0){
								SCollectionContainer bic=SCollectionContainer.setList(grnList, "id");
								deliveryOptions.setContainerDataSource(bic);
								deliveryOptions.setItemCaptionPropertyId("deliveryNo");
								getUI().getCurrent().addWindow(salesGRNWindow);
							}
							else{
								SNotification.show("No Delivery Note Available",Type.WARNING_MESSAGE);
							}
						}
						else
							setRequiredError(customerCombo, "Select customer", true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			importDeliverySalesMan.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					setRequiredError(responsilbeEmployeeCombo, null, false);
					try {
						if(responsilbeEmployeeCombo.getValue() != null && !responsilbeEmployeeCombo.getValue().toString().equals("")){
							List<Long> idList=new ArrayList<Long>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Item item = table.getItem(it.next());
								long id=toLong(item.getItemProperty(TBC_DELIVERY_ID).getValue().toString());
								if(id!=0)
									idList.add(id);
							}
							List grnList=new ArrayList();
							grnList=dao.getDeliveryNoteModelSalesManList(getOfficeID(), (Long)responsilbeEmployeeCombo.getValue(), idList);
							if(grnList.size()>0){
								SCollectionContainer bic=SCollectionContainer.setList(grnList, "id");
								deliveryOptions.setContainerDataSource(bic);
								deliveryOptions.setItemCaptionPropertyId("deliveryNo");
								getUI().getCurrent().addWindow(salesGRNWindow);
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else
							setRequiredError(responsilbeEmployeeCombo, getPropertyName("invalid_selection"), true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	
			
			addDeliveryButton.addClickListener(new ClickListener() {
		
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (deliveryOptions.getValue() != null && ((Set<Long>) deliveryOptions.getValue()).size() > 0) {
							table.setVisibleColumns(allHeaders);
							Set<Long> delNoteSet = (Set<Long>) deliveryOptions.getValue();
							List delList=dao.getAllDataFromDeliveryNote(delNoteSet);
							Iterator itr = delList.iterator();
							while (itr.hasNext()) {
								SalesBean bean = (SalesBean) itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(bean.getDet().getCurrencyId()).getCode();
								
								double discount=0;
								long taxId=SConstants.tax.SALES_TAX;
								double taxPer=0;
								double taxAmount=0;
								double cess=0;
								
								String itemName="";
								if(settings.isITEMS_IN_MULTIPLE_LANGUAGE())
									itemName=bean.getDet().getItem().getName()+"/"+bean.getDet().getItem().getSecondName();
								else
									itemName=bean.getDet().getItem().getName();
								double disPerc=0;
								double discPrice=0;
								
								
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										true,
										bean.getDet().getItem().getId(),
										bean.getDet().getItem().getItem_code(),
										itemName,
										roundNumber(bean.getDet().getQunatity()),
		                                roundNumber(bean.getDet().getQty_in_basic_unit()/bean.getDet().getQunatity()),
		                                roundNumber(bean.getDet().getQty_in_basic_unit()),
										roundNumber(bean.getDet().getQty_in_basic_unit()),
										roundNumber(bean.getDet().getQty_in_basic_unit()-bean.getDet().getQunatity()),
										bean.getDet().getUnit().getId(),
										bean.getDet().getUnit().getSymbol(),
										roundNumber(bean.getDet().getUnit_price()),
										bean.getDet().getCurrencyId(),
										currency,
										roundNumber(bean.getDet().getConversionRate()),
										(Integer)1,
										roundNumber(disPerc),
										roundNumber(discount),
										roundNumber(discPrice),
										taxId,
										taxPer,
										taxAmount,
										cess,
										roundNumber(bean.getDet().getUnit_price()*bean.getDet().getQunatity()+taxAmount+cess),
										(long)0,
										"None",
										(long)0,
										"None",
										bean.getDet().getBatch_id(),
										bean.getDet().getStock_id(),
										bean.getDet().getOrder_id(),
										bean.getDet().getOrder_child_id(),
										bean.getDet().getId(),
										bean.getId(),
										roundNumber(bean.getDet().getUnit_price()*bean.getDet().getQunatity()+taxAmount+cess)},table.getItemIds().size()+1);
							}
							getUI().removeWindow(salesGRNWindow);
							List<Long> delIdList=new ArrayList<Long>();
							List<Object> idList=new ArrayList<Object>();
							Iterator it=table.getItemIds().iterator();
							while (it.hasNext()) {
								Object obj=(Object)it.next();
								Item item = table.getItem(obj);
								long delId=toLong(item.getItemProperty(TBC_DELIVERY_ID).getValue().toString());
								if(delId!=0){
									long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
									if(id!=0)
										delIdList.add(id);
								}
							}
							
							if(delIdList.size()>0){
								Iterator itr2=table.getItemIds().iterator();
								while (itr2.hasNext()) {
									Object obj2=(Object)itr2.next();
									Item item = table.getItem(obj2);
									long delId=toLong(item.getItemProperty(TBC_DELIVERY_ID).getValue().toString());
									if(delId==0){
										long id=toLong(item.getItemProperty(TBC_ORDER_ID).getValue().toString());
										if(id!=0){
											if(delIdList.contains(id)){
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
						
						stockSelectList.setComponentError(null);
						changeStkButton.setComponentError(null);
						
						if (itemCombo.getValue() != null) {
							ItemModel itm = itmDao.getItem((Long) itemCombo.getValue());
							SCollectionContainer bic = SCollectionContainer.setList(comDao.getAllItemUnitDetails(itm.getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");
							unitSelect.setValue(null);
							unitSelect.setValue(itm.getUnit().getId());
							quantityField.selectAll();
							quantityField.focus();
							
							List lst = comDao.getStocksInLocation(
									(Long) itemCombo.getValue(),
									settings.isUSE_SALES_RATE_FROM_STOCK(),(Long)locationCombo.getValue());
							
							if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")) {
								Item item;
								List<Long> stkIDs=new ArrayList<Long>();
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {

									item = table.getItem(it.next());
									if(item.getItemProperty(TBC_ITEM_ID).getValue()==(Long) itemCombo.getValue()&&
											!item.getItemProperty(TBC_STOCK_ID).getValue().toString().equals("0"))
										stkIDs.add((Long)item.getItemProperty(TBC_STOCK_ID).getValue());
								}
								if(stkIDs.size()>0)
								lst.addAll(comDao.getUsedStocks(stkIDs, settings.isUSE_SALES_RATE_FROM_STOCK()));
							}

							SCollectionContainer bic1 = SCollectionContainer
									.setList(lst, "id");
							stockSelectList
									.setContainerDataSource(bic1);
							stockSelectList
									.setItemCaptionPropertyId("stock_details");

							long stk_id = comDao
									.getDefaultStockToSelect((Long) itemCombo
											.getValue());
							if (stk_id != 0)
								stockSelectList.setValue(stk_id);
							else {
								Iterator it = stockSelectList
										.getItemIds().iterator();
								if (it.hasNext())
									stockSelectList.setValue(it.next());
							}
							
							if(settings.isDISCOUNT_ENABLE()){
								discountField.setValue(roundNumberToString(itmDao.getItemDiscount((Long) itemCombo.getValue())));
							}
							if(settings.isTAX_ENABLED()) {
								taxSelect.setValue(itm.getSalesTax().getId());
							}
						}
						else{
							convertionQuantityField.setValue("1");
							convertedQuantityField.setVisible(false);
							convertionQuantityField.setVisible(false);
						}
					} 
					catch (Exception e) {
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
									double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(),(Long)salesTypeSelect.getValue());
									convertionQuantityField.setValue(asString(cnvr_qty));
								}
								double unitPrice=comDao.getItemPrice(itm.getId(), (Long) unitSelect.getValue(),(Long)salesTypeSelect.getValue());
								if(settings.isBARCODE_ENABLED()&&settings.getBARCODE_TYPE()==SConstants.barcode_types.CUSTOMER_SPECIFIC&&customerCombo.getValue()!=null){
									double perc=comDao.getCustomerSpecificPercentage(itm.getId(),  (Long) customerCombo.getValue());
									unitPrice+=unitPrice*perc/100;
								}
								
								unitPriceField.setNewValue(comDao.getItemCurrency(itm.getId(), (Long) unitSelect.getValue(), toLong(salesTypeSelect.getValue().toString())),
										roundNumber(unitPrice));
								
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
									
									
									/*double disc=0;
									if(settings.isDISCOUNT_ENABLE()){
										disc=itmDao.getItemDiscount((Long) itemCombo.getValue());
									}
									netPriceField.setNewValue(roundNumberToString(unitPriceField.getValue()*toDouble(quantityField.getValue())-disc));*/
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			unitPriceField.setImmediate(true);
			quantityField.setImmediate(true);
			discountField.setImmediate(true);
			
			
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
								double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(),(Long)salesTypeSelect.getValue());
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
								
								
							/*	double disc=0;
								if(settings.isDISCOUNT_ENABLE()){
									disc=itmDao.getItemDiscount((Long) itemCombo.getValue());
								}
								netPriceField.setNewValue(roundNumberToString(unitPriceField.getValue()*toDouble(quantityField.getValue())-disc));
							*/
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
			discountField.addValueChangeListener(new Property.ValueChangeListener() {
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
//							Iterator itr=table.getItemIds().iterator();
//							List<Long> idList=new ArrayList<Long>();
//							while (itr.hasNext()) {
//								Item item = table.getItem(itr.next());
//								idList.add(toLong(item.getItemProperty(TBC_ITEM_ID).getValue().toString()));
//							}
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
									taxId=SConstants.tax.PURCHASE_TAX;
									taxPer=0;
									taxAmount=0;
								}
							}
							else{
								taxId=SConstants.tax.PURCHASE_TAX;
								taxPer=0;
								taxAmount=0;
							}
							
							if(isCessEnable()){
								cess=taxAmount*getCessPercentage()/100;
							}
							else
								cess=0;
							
							/*if(isDiscountEnable()){
								try {
									discount=toDouble(discountField.getValue().toString());
								} catch (Exception e) {
									discount=0;
								}
							}
							else
								discount=0;*/
							
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
							
							//double netPrice=(unitPriceField.getValue()*qty)+taxAmount+cess-discount;
							
							String itemName="";
							if(settings.isITEMS_IN_MULTIPLE_LANGUAGE())
								itemName=itemModel.getName()+"/"+itemModel.getSecondName();
							else
								itemName=itemModel.getName();
							
//							if(!idList.contains((Long)itemCombo.getValue())){
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										true,
										itemModel.getId(),
										itemModel.getItem_code(),
										itemName,
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
										(Long)gradeCombo.getValue(),
										gradeCombo.getItemCaption((Long)gradeCombo.getValue()),
										(Long)locationCombo.getValue(),
										locationCombo.getItemCaption((Long)locationCombo.getValue()),
										itemDao.getBatchIdFromStock((Long)stockSelectList.getValue()),
										(Long)stockSelectList.getValue(),
										(long)0,
										(long)0,
										(long)0,
										(long)0,roundNumber(totalPrice)},table.getItemIds().size()+1);
//							}
//							else{
//								Notification.show(getPropertyName("item_added_earlier"), Type.WARNING_MESSAGE);
//							}
							resetItems();
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
						}
						
						barcodeField.focus();
						barcodeField.setValue("");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			barcodeField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						String barcode = barcodeField.getValue();

						if (barcode.trim().length() > 0) {
							itemCombo.setComponentError(null);
							stockSelectList.setComponentError(null);
							ItemStockModel mdl=null;
							if(settings.getBARCODE_TYPE()==SConstants.barcode_types.CUSTOMER_SPECIFIC){
								if(customerCombo.getValue()!=null)
									mdl = dao.getNewItemStock(barcode,(Long)customerCombo.getValue());
							}else
								mdl = dao.getItemFromBarcode(barcode);
							if (mdl != null) {
								itemCombo.setNewValue(mdl.getItem().getId());
								stockSelectList.setValue(mdl.getId());
								gradeCombo.setValue(mdl.getGradeId());
								quantityField.focus();
							} else {
								itemCombo.setNewValue(null);
								stockSelectList.setValue(null);
								gradeCombo.setValue((long) 0);
								barcodeField.focus();
							}

						} else {
							barcodeField.focus();
							if (itemCombo.getValue() != null)
								quantityField.focus();
						}

					} catch (Exception e) {
						barcodeField.focus();
						barcodeField.selectAll();
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
							itemCombo.setValue((Long)item.getItemProperty(TBC_ITEM_ID).getValue());
							quantityField.setValue(roundNumber((Double)item.getItemProperty(TBC_QTY).getValue())+"");
							unitSelect.setValue((Long)item.getItemProperty(TBC_UNIT_ID).getValue());
							unitPriceField.setNewValue((Long)item.getItemProperty(TBC_CID).getValue(), 
													roundNumber((Double)item.getItemProperty(TBC_UNIT_PRICE).getValue()));
							
							itemDiscountRadio.setValue((Integer) item.getItemProperty(TBC_DISCOUNT_TYPE)
									.getValue());
							itemDiscountPercentField.setNewValue((Double) item.getItemProperty(TBC_DISCOUNT_PERCENTAGE).getValue()+ "");
							itemDiscountAmountField.setNewValue(Math.abs((Double) item.getItemProperty(TBC_DISCOUNT).getValue())+ "");
							
							taxSelect.setNewValue((Long)item.getItemProperty(TBC_TAX_ID).getValue());
							discountField.setNewValue(roundNumberToString((Double)item.getItemProperty(TBC_DISCOUNT).getValue()));
							netPriceField.setNewValue(roundNumberToString((Double)item.getItemProperty(TBC_NET_PRICE).getValue()));
							convertionQuantityField.setNewValue(""+ roundNumber((Double)item.getItemProperty(TBC_CONVERTION_QTY).getValue()));
							gradeCombo.setValue((Long)item.getItemProperty(TBC_GRADE_ID).getValue());
							locationCombo.setValue((Long)item.getItemProperty(TBC_LOCATION_ID).getValue());
							stockSelectList.setValue((Long)item.getItemProperty(TBC_STOCK_ID).getValue());
							
							long grnId=(Long)item.getItemProperty(TBC_DELIVERY_ID).getValue();
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
								itemCombo.setReadOnly(false);
//								quantityField.setReadOnly(true);
								unitSelect.setReadOnly(true);
								gradeCombo.setReadOnly(true);
								taxSelect.setReadOnly(true);
								discountField.setReadOnly(true);
								barcodeField.setReadOnly(true);
								locationCombo.setReadOnly(true);
								addItemButton.setVisible(false);
								updateItemButton.setVisible(true);
								stockSelectList.setReadOnly(true);
								itemDiscountRadio.setValue(2);
								itemDiscountPercentField.setNewValue("0");
								itemDiscountAmountField.setNewValue("0");
							}
							
							
							if(!(Boolean)item.getItemProperty(TBC_EDITABLE).getValue()){
								addItemButton.setVisible(false);
								updateItemButton.setVisible(false);
								setRequiredError(itemCombo, "Stock Used in Sale. Cannot Edit or Delete", true);
							}
							else{
								addItemButton.setVisible(false);
								updateItemButton.setVisible(true);
							}
						}
						else{
							setRequiredError(itemCombo, null, false);
							resetItems();
						}
						
						barcodeField.focus();
						barcodeField.setValue("");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			updateItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {
//							List<Long> idList=new ArrayList<Long>();
//							Iterator itr=table.getItemIds().iterator();
//							while (itr.hasNext()) {
//								Item itm = table.getItem(itr.next());
//								idList.add(toLong(itm.getItemProperty(TBC_ITEM_ID).getValue().toString()));
//							}
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
//								idList.remove((Long)item.getItemProperty(TBC_ITEM_ID).getValue());
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
										taxId=SConstants.tax.PURCHASE_TAX;
										taxPer=0;
										taxAmount=0;
									}
								}
								else{
									taxId=SConstants.tax.PURCHASE_TAX;
									taxPer=0;
									taxAmount=0;
								}
								
								if(isCessEnable()){
									cess=taxAmount*getCessPercentage()/100;
								}
								else
									cess=0;
								
								/*if(isDiscountEnable()){
									try {
										discount=toDouble(discountField.getValue().toString());
									} catch (Exception e) {
										discount=0;
									}
								}
								else
									discount=0;*/
								
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
								
								
								//double netPrice=(unitPriceField.getValue()*qty)+taxAmount+cess-discount;
								
//								if(!idList.contains((Long)itemCombo.getValue())){
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
									item.getItemProperty(TBC_NET_PRICE_WITHOUT_DISCOUNT).setValue(roundNumber(totalPrice));
									if(requested!=0){
										item.getItemProperty(TBC_BALANCE).setValue(roundNumber(balance));
									}
									item.getItemProperty(TBC_GRADE_ID).setValue((Long)gradeCombo.getValue());
									item.getItemProperty(TBC_GRADE).setValue(gradeCombo.getItemCaption((Long)gradeCombo.getValue()));
									item.getItemProperty(TBC_LOCATION_ID).setValue((Long)locationCombo.getValue());
									item.getItemProperty(TBC_STOCK_ID).setValue((Long)stockSelectList.getValue());
									item.getItemProperty(TBC_BATCH_ID).setValue(itemDao.getBatchIdFromStock((Long)stockSelectList.getValue()));
									item.getItemProperty(TBC_LOCATION).setValue(locationCombo.getItemCaption((Long)locationCombo.getValue()));
									resetItems();
//								}
//								else{
//									Notification.show(getPropertyName("item_added_earlier"), Type.WARNING_MESSAGE);
//								}
							}
							calculateTotals();
							table.setValue(null);
						}
						barcodeField.focus();
						barcodeField.setValue("");

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
							boolean savable=true;
							double netDiscount=0;
							SalesModel mdl=new SalesModel();
							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								SalesInventoryDetailsModel det=new SalesInventoryDetailsModel();
								Item item = table.getItem(it.next());
								double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
								if(qty<=0){
									savable=false;
									break;
								}
								det.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
								det.setQunatity(roundNumber((Double) item.getItemProperty(TBC_QTY).getValue()));
								det.setQuantity_in_basic_unit(roundNumber((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue()));
								det.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
								det.setUnit_price(roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()));
								det.setCurrencyId((Long) item.getItemProperty(TBC_CID).getValue());
								det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONV_RATE).getValue()));
								//det.setDiscount(roundNumber((Double) item.getItemProperty(TBC_DISCOUNT).getValue()));
								det.setTax(new TaxModel((Long) item.getItemProperty(TBC_TAX_ID).getValue()));
								det.setTaxPercentage(roundNumber((Double) item.getItemProperty(TBC_TAX_PERCENTAGE).getValue()));
								det.setTaxAmount(roundNumber((Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()));
								det.setCessAmount(roundNumber((Double) item.getItemProperty(TBC_CESS).getValue()));
								det.setGrade_id((Long) item.getItemProperty(TBC_GRADE_ID).getValue());
								det.setLocation_id((Long) item.getItemProperty(TBC_LOCATION_ID).getValue());
								
								det.setOrder_child_id((Long) item.getItemProperty(TBC_ORDER_CHILD_ID).getValue());
								det.setOrder_id((Long) item.getItemProperty(TBC_ORDER_ID).getValue());
								det.setDelivery_child_id((Long) item.getItemProperty(TBC_DELIVERY_CHILD_ID).getValue());
								det.setDelivery_id((Long) item.getItemProperty(TBC_DELIVERY_ID).getValue());
								det.setStock_id((Long) item.getItemProperty(TBC_STOCK_ID).getValue());
								
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
//								if (settings.isSALES_NO_CREATION_MANUAL()) {
//									mdl.setSales_number(toLong(salesNoCombo.getValue()));
//								}else{
//									if (settings.isUSE_SALES_NO_IN_SALES_ORDER()) {
//										if (ordrNo_to_salNo)
//											mdl.setSales_number(sales_number);
//										else
//											mdl.setSales_number(getNextSequence(
//												"Sales Number", getLoginID())+"");
//									} else
									mdl.setSales_number(getNextSequence("Sales Number", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								
//								}
								mdl.setSales_type((Long) salesTypeSelect
										.getValue());
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setCustomer(new LedgerModel((Long)customerCombo.getValue()));
								mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setComments(comment.getValue());
								mdl.setAmount(roundNumber(netAmountField.getValue()));
								mdl.setNetCurrencyId(new CurrencyModel(netAmountField.getCurrency()));
								mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
								mdl.setActive(true);
								
								mdl.setDiscount_type((Integer)discountRadio.getValue());
								mdl.setDiscountPercentage(roundNumber(toDouble(discountPercentField.getValue().toString().trim())));
								mdl.setDiscountAmount(roundNumber(toDouble(discountAmountField.getValue().toString().trim())));
								
								mdl.setInventory_details_list(itemsList);
								mdl.setPaid_by_payment(0);
								mdl.setPayment_amount(roundNumber(payingAmountField.getValue()));
								mdl.setPaymentConversionRate(roundNumber(netAmountField.getConversionRate()));
								mdl.setCurrency_id(payingAmountField.getCurrency());							
								mdl.setPayment_credit((Long)paymentCreditRadio.getValue());
								mdl.setCash_cheque((Long)cashChequeRadio.getValue());
								mdl.setSales_account((Long)salesAccountCombo.getValue());
								mdl.setChequeNo(chequenumberField.getValue());
								mdl.setChequeDate(CommonUtil.getSQLDateFromUtilDate(chequeDateField.getValue()));
								mdl.setDepartment_id((Long)departmentCombo.getValue());
								mdl.setDivision_id((Long)divisionCombo.getValue());
								double expense=0;
								try {
									expense=toDouble(expenseField.getValue().toString());
								} catch (Exception e) {
									expense=0;
								}
								mdl.setExpenseCreditAmount(roundNumber(expensePanel.getCreditAmount()));
								mdl.setExpenseAmount(roundNumber(expense));
								if((Integer) mdl.getDiscount_type()==1){
									netDiscount+=(mdl.getAmount()*mdl.getDiscountPercentage()/100);
								}else{
									netDiscount+=mdl.getDiscountAmount();
								}
								
								Iterator itr=expensePanel.getValue().iterator();
								List expenseList=new ArrayList();
								while (itr.hasNext()) {
									ExpenseBean bean = (ExpenseBean) itr.next();
									SalesExpenseDetailsModel det=new SalesExpenseDetailsModel();
									det.setClearingAgent(bean.isClearingAgent());
									det.setLedger_id(bean.getLedger());
									det.setTransaction_type(bean.getTransactionType());
									det.setCurrencyId(bean.getCurrencyId());
									det.setConversionRate(roundNumber(bean.getConversionRate()));
									det.setAmount(roundNumber(bean.getAmount()));
									expenseList.add(det);
								}
								mdl.setSales_expense_list(expenseList);
								
								
								FinTransaction transaction=new FinTransaction();
								
								transaction.addTransaction(SConstants.DR, 
														(Long)salesAccountCombo.getValue(),
														(Long)customerCombo.getValue(),
														roundNumber((netAmountField.getValue()/ netAmountField.getConversionRate())-
																(expensePanel.getDebitAmount()/ netAmountField.getConversionRate())),
														"",
														payingAmountField.getCurrency(),
														roundNumber(netAmountField.getConversionRate()));
								
								if(netDiscount>0)
									transaction.addTransaction(SConstants.DR, 
											(Long)salesAccountCombo.getValue(),
											settings.getSALES_DESCOUNT_ACCOUNT(),
											roundNumber(netDiscount),
											"",
											payingAmountField.getCurrency(),
											roundNumber(netAmountField.getConversionRate()));
//									transaction.addTransaction(SConstants.DR, 
//											settings.getSALES_DESCOUNT_ACCOUNT(), 
//											(Long)customerCombo.getValue(),
//											roundNumber(netDiscount),
//											"",
//											payingAmountField.getCurrency(),
//											roundNumber(netAmountField.getConversionRate()));
								
								
								if(payingAmountField.getValue()>0)
									transaction.addTransaction(SConstants.DR, 
															(Long)customerCombo.getValue(), 
															settings.getCASH_ACCOUNT(),
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
																	bean.getFromId(),
																	(Long)customerCombo.getValue(),
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
									mdl.setStatus(SConstants.NOT_PAID);
									mdl.setPayment_done('N');
								}	
								else if(paymentAmount>0 && paymentAmount<fullAmount){
									mdl.setStatus(SConstants.PARTIALLY_PAID);
									mdl.setPayment_done('N');
								}
								else if(paymentAmount>=fullAmount){
									mdl.setStatus(SConstants.FULLY_PAID);
									mdl.setPayment_done('Y');
								}
								
								long id = dao.save(
										mdl,
										transaction.getTransaction(
												SConstants.SALES,
												CommonUtil
														.getSQLDateFromUtilDate(dateField
																.getValue())),payingAmountField.getValue(),
										settings.getUPDATE_RATE_AND_CONV_QTY());
								loadOptions(id,0);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),
										"Sales Created. Sales No : "
												+ mdl.getSales_number()+ ", Customer : "
												+ customerCombo.getItemCaption(customerCombo.getValue())
												+ ", Amount : "+ mdl.getAmount(),id);
							}
							else
								Notification.show("Check Item Quantity",Type.ERROR_MESSAGE);
								
						}
					} catch (Exception e) {
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}

				}
			});

			
			salesNoCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						table.removeAllItems();
						calculateTotals();
						table.setValue(null);
						referenceNoField.setValue("");
						netAmountField.currencySelect.setReadOnly(false);
						netAmountField.setCurrency(getCurrencyID());
						table.setValue(null);
						referenceNoField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						netAmountField.setCurrencyDate(getWorkingDate());
						customerCombo.setValue(null);
						responsilbeEmployeeCombo.setValue(null);
						comment.setValue("");
						paymentCreditRadio.setValue(SConstants.paymentMode.CREDIT);
						cashChequeRadio.setValue(SConstants.paymentMode.CASH);
						salesAccountCombo.setValue(settings.getSALES_ACCOUNT());
						chequenumberField.setValue("");
						chequeDateField.setValue(getWorkingDate());
						chequenumberField.setVisible(false);
						chequeDateField.setVisible(false);
						payingAmountField.setValue(getCurrencyID(),0);
						saveButton.setVisible(true);
						printButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						sendMailButton.setVisible(false);
						expensePanel.clearAll();
						expenseField.setNewValue("0");
						departmentCombo.setValue((long)0);
						divisionCombo.setValue(null);
						discountRadio.setValue(null);
						discountRadio.setValue(1);
//						paymentModeCombo.setValue(null);
						itemDiscountRadio.setValue(2);
						itemDiscountPercentField.setNewValue("0");
						itemDiscountAmountField.setNewValue("0");
						
						resetItems();
						if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")){
							table.setVisibleColumns(allHeaders);
							SalesModel mdl=dao.getSale((Long)salesNoCombo.getValue());
							referenceNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							netAmountField.setCurrencyDate(mdl.getDate());
							customerCombo.setValue(mdl.getCustomer().getId());
							responsilbeEmployeeCombo.setValue(mdl.getResponsible_employee());
							comment.setValue(mdl.getComments());
							paymentCreditRadio.setValue(mdl.getPayment_credit());
							cashChequeRadio.setValue(mdl.getCash_cheque());
							salesAccountCombo.setValue(mdl.getSales_account());
							chequenumberField.setValue(mdl.getChequeNo());
							if(mdl.getChequeDate()!=null)
								chequeDateField.setValue(mdl.getChequeDate());
							payingAmountField.setValue(mdl.getPayment_amount());
							departmentCombo.setValue(mdl.getDepartment_id());
							divisionCombo.setValue(mdl.getDivision_id());
							if(settings.isSALES_DISCOUNT_ENABLE()){
								discountRadio.setValue(mdl.getDiscount_type());
								discountPercentField.setNewValue(roundNumber(mdl.getDiscountPercentage())+"");
								discountAmountField.setNewValue(roundNumber(mdl.getDiscountAmount())+"");
							}
							
//							paymentModeCombo.setValue(mdl.get);
							
							Iterator itr=mdl.getInventory_details_list().iterator();
							while (itr.hasNext()) {
								SalesInventoryDetailsModel det=(SalesInventoryDetailsModel)itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode();
								boolean editable=true;
//								if (comDao.isStockBlocked(mdl.getId(),
//															det.getId(),
//															SConstants.stockSalesType.DE)){
//									editable=false;
//								}
//									
								SalesOrderDetailsModel podet=null;
								double requested=0,balance=0;
								if(det.getOrder_child_id()!=0)
									podet=new SalesOrderDao().getSalesOrderDetailsModel(det.getOrder_child_id());
									if(podet!=null){
										requested= podet.getQty_in_basic_unit();
										balance= podet.getQty_in_basic_unit()-podet.getQuantity_sold();
									}
									
									String itemName="";
									if(settings.isITEMS_IN_MULTIPLE_LANGUAGE())
										itemName=det.getItem().getName()+"/"+det.getItem().getSecondName();
									else
										itemName=det.getItem().getName();
									
									

									double amount=det.getUnit_price();
									double	discPer = det.getDiscountPercentage();
									double	discount_amt = det.getDiscount();
									double discountPrice=0;
									if(det.getDiscount_type()==1){
										discountPrice=roundNumber(amount-amount*discPer/100);
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
										itemName,
										roundNumber(det.getQunatity()),
										roundNumber(det.getQuantity_in_basic_unit()/det.getQunatity()),
										roundNumber(det.getQuantity_in_basic_unit()),
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
										det.getGrade_id(),
										gradeCombo.getItemCaption(det.getGrade_id()),
										det.getLocation_id(),
										locationCombo.getItemCaption(det.getLocation_id()),
										det.getBatch_id(),
										det.getStock_id(),
										det.getOrder_child_id(),
										det.getOrder_id(),
										det.getDelivery_child_id(),
										det.getDelivery_id(),roundNumber(totalPrice)},table.getItemIds().size()+1);
							}
							
							
							itr=mdl.getSales_expense_list().iterator();
							List<ExpenseBean> resultList = new ArrayList<ExpenseBean>();
							while (itr.hasNext()) {
								SalesExpenseDetailsModel det = (SalesExpenseDetailsModel) itr.next();
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
							if(mdl.getPaid_by_payment()>0)
								netAmountField.currencySelect.setReadOnly(true);
							netAmountField.conversionField.setValue(""+roundNumber(mdl.getConversionRate()));
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
							if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")){
								boolean savable=true;
								double netDiscount=0;
								SalesModel mdl=dao.getSale((Long)salesNoCombo.getValue());
								List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									SalesInventoryDetailsModel det=null;
									Item item = table.getItem(it.next());
									long id=(Long) item.getItemProperty(TBC_ID).getValue();
									if(id!=0)
										det=dao.getSaleInventoryDetailsModel(id);
									if(det==null)
										det=new SalesInventoryDetailsModel();
									double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
									if(qty<=0){
										savable=false;
										break;
									}
									det.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
									det.setQunatity(roundNumber((Double) item.getItemProperty(TBC_QTY).getValue()));
									det.setQuantity_in_basic_unit(roundNumber((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue()));
									det.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
									det.setUnit_price(roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()));
									det.setCurrencyId((Long) item.getItemProperty(TBC_CID).getValue());
									det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONV_RATE).getValue()));
									//det.setDiscount(roundNumber((Double) item.getItemProperty(TBC_DISCOUNT).getValue()));
									det.setTax(new TaxModel((Long) item.getItemProperty(TBC_TAX_ID).getValue()));
									det.setTaxPercentage(roundNumber((Double) item.getItemProperty(TBC_TAX_PERCENTAGE).getValue()));
									det.setTaxAmount(roundNumber((Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()));
									det.setCessAmount(roundNumber((Double) item.getItemProperty(TBC_CESS).getValue()));
									det.setGrade_id((Long) item.getItemProperty(TBC_GRADE_ID).getValue());
									det.setLocation_id((Long) item.getItemProperty(TBC_LOCATION_ID).getValue());
									
									det.setOrder_child_id((Long) item.getItemProperty(TBC_ORDER_CHILD_ID).getValue());
									det.setOrder_id((Long) item.getItemProperty(TBC_ORDER_ID).getValue());
									det.setStock_id((Long) item.getItemProperty(TBC_STOCK_ID).getValue());
									
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
									mdl.setSales_type((Long) salesTypeSelect.getValue());
									mdl.setRef_no(referenceNoField.getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									mdl.setCustomer(new LedgerModel((Long)customerCombo.getValue()));
									mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
//									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									mdl.setComments(comment.getValue());
									mdl.setAmount(roundNumber(netAmountField.getValue()));
									mdl.setNetCurrencyId(new CurrencyModel(netAmountField.getCurrency()));
									mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
									mdl.setInventory_details_list(itemsList);
//									mdl.setPaid_by_payment(0);
									
									mdl.setDiscount_type((Integer)discountRadio.getValue());
									mdl.setDiscountPercentage(roundNumber(toDouble(discountPercentField.getValue().toString().trim())));
									mdl.setDiscountAmount(roundNumber(toDouble(discountAmountField.getValue().toString().trim())));
									
									mdl.setPayment_amount(roundNumber(payingAmountField.getValue()));
									mdl.setPaymentConversionRate(roundNumber(netAmountField.getConversionRate()));
									mdl.setCurrency_id(payingAmountField.getCurrency());							
									mdl.setPayment_credit((Long)paymentCreditRadio.getValue());
									mdl.setCash_cheque((Long)cashChequeRadio.getValue());
									mdl.setChequeNo(chequenumberField.getValue());
									mdl.setChequeDate(CommonUtil.getSQLDateFromUtilDate(chequeDateField.getValue()));
									mdl.setSales_account((Long)salesAccountCombo.getValue());
									mdl.setDepartment_id((Long)departmentCombo.getValue());
									mdl.setDivision_id((Long)divisionCombo.getValue());
									double expense=0;
									try {
										expense=toDouble(expenseField.getValue().toString());
									} catch (Exception e) {
										expense=0;
									}
									mdl.setExpenseCreditAmount(roundNumber(expensePanel.getCreditAmount()));
									mdl.setExpenseAmount(roundNumber(expense));
									
									if((Integer) mdl.getDiscount_type()==1){
										netDiscount+=(mdl.getDiscountAmount()*mdl.getDiscountPercentage()/100);
									}else{
										netDiscount+=mdl.getDiscountAmount();
									}
									
									Iterator itr=expensePanel.getValue().iterator();
									List expenseList=new ArrayList();
									while (itr.hasNext()) {
										ExpenseBean bean = (ExpenseBean) itr.next();
										SalesExpenseDetailsModel det=new SalesExpenseDetailsModel();
										det.setClearingAgent(bean.isClearingAgent());
										det.setLedger_id(bean.getLedger());
										det.setTransaction_type(bean.getTransactionType());
										det.setCurrencyId(bean.getCurrencyId());
										det.setConversionRate(roundNumber(bean.getConversionRate()));
										det.setAmount(roundNumber(bean.getAmount()));
										expenseList.add(det);
									}
									mdl.setSales_expense_list(expenseList);
									
									FinTransaction transaction=new FinTransaction();
									
									transaction.addTransaction(SConstants.DR, 
															(Long)salesAccountCombo.getValue(),
															(Long)customerCombo.getValue(),
															roundNumber((netAmountField.getValue()/ netAmountField.getConversionRate())-
																	(expensePanel.getDebitAmount()/ netAmountField.getConversionRate())),
															"",
															payingAmountField.getCurrency(),
															roundNumber(netAmountField.getConversionRate()));
									
									
									if(netDiscount>0)
										transaction.addTransaction(SConstants.DR, 
										(Long)salesAccountCombo.getValue(),
										settings.getSALES_DESCOUNT_ACCOUNT(), 
										roundNumber(netDiscount),
										"",
										payingAmountField.getCurrency(),
										roundNumber(netAmountField.getConversionRate()));
//										transaction.addTransaction(SConstants.DR, 
//												settings.getSALES_DESCOUNT_ACCOUNT(), 
//												(Long)customerCombo.getValue(),
//												roundNumber(netDiscount),
//												"",
//												payingAmountField.getCurrency(),
//												roundNumber(netAmountField.getConversionRate()));

					
									if(payingAmountField.getValue()>0)
										transaction.addTransaction(SConstants.DR, 
																(Long)customerCombo.getValue(), 
																settings.getCASH_ACCOUNT(),
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
																		bean.getFromId(),
																		(Long)customerCombo.getValue(),
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
										mdl.setStatus(SConstants.FULLY_PAID);
										mdl.setPayment_done('Y');
									}
									else{
										mdl.setStatus(SConstants.PARTIALLY_PAID);
										mdl.setPayment_done('N');
									}
									
									TransactionModel trans = dao.getTransactionModel(mdl.getTransaction_id());
									trans.setTransaction_details_list(transaction.getChildList());
									trans.setDate(mdl.getDate());
									trans.setLogin_id(getLoginID());
									
									dao.update(mdl, trans, payingAmountField.getValue());
									
									
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId(),0);
									saveActivity(getOptionId(),
											"Sales Updated. Sales No : "
													+ mdl.getSales_number()+ ", Customer : "
													+ customerCombo.getItemCaption(customerCombo.getValue())
													+ ", Amount : "+ mdl.getAmount(),mdl.getId());
								}
								else
									Notification.show("Check Item Quantity",Type.ERROR_MESSAGE);
								
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
						if (salesNoCombo.getValue() != null && !salesNoCombo.getValue().toString().equals("0")) {
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
													dao.delete((Long)salesNoCombo.getValue());
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													saveActivity(getOptionId()," Sales deleted. Sales No : "
																+ salesNoCombo.getItemCaption(salesNoCombo.getValue())
																+ ", Customer : "+customerCombo.getItemCaption(customerCombo.getValue()),
																(Long)salesNoCombo.getValue());
													loadOptions(0,0);
												

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
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
						if (salesNoCombo.getValue() != null && !salesNoCombo.getValue().toString().equals("0")) {
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
//												SalesModel mdl=dao.getSale((Long)salesNoCombo.getValue());
													dao.cancelSale((Long)salesNoCombo.getValue());
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													saveActivity(getOptionId()," Sale Cancelled.Sale No : "
																+ salesNoCombo.getItemCaption(salesNoCombo.getValue())
																+ ", Customer : "+customerCombo.getItemCaption(customerCombo.getValue()),
																(Long)salesNoCombo.getValue());
													loadOptions(0,0);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
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
						if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")){
							SalesModel mdl=dao.getSale((Long)salesNoCombo.getValue());
							
							CustomerModel ledger =  new CustomerDao().getCustomerFromLedger(mdl.getCustomer().getId());
							String address = "";
							if (ledger != null) {
								address = new AddressBusiness().getAddressString(ledger.getAddress().getId());
							}
							map.put("ADDRESS_LABEL", getPropertyName("address"));
							map.put("ADDRESS", address);
							map.put("LEDGER_NAME_LABEL", getPropertyName("mr_messers"));
							map.put("LEDGER_NAME_LABEL_IN_ARABIC", "Ø§Ù„Ø³ÙŠØ¯ / Ø§Ù„Ø³Ø§Ø¯Ø©");
							map.put("LEDGER", mdl.getCustomer().getName());
							
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("cash_credit_invoice"));
							map.put("REPORT_TITLE_LABEL_ARABIC", "Ø§Ù„Ø§Ø¦ØªÙ…Ø§Ù† Ø§Ù„Ù�Ø§ØªÙˆØ±Ø© / Ø§Ù„Ù†Ù‚Ø¯ÙŠØ©");
							
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("BILL_NO", mdl.getSales_number());
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE_LABEL_ARABIC", "Ø§Ù„ØªØ§Ø±ÙŠØ®");
							
							map.put("PACK_LABEL", getPropertyName("pack"));
							map.put("PACK_LABEL_ARABIC", "Ø­Ø²Ù…Ø©");
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("LPO_NO_LABEL", getPropertyName("lpo_no"));
							map.put("LPO_NO",referenceNoField.getValue() );
							map.put("DO_NO_LABEL", getPropertyName("do_no"));
							map.put("DO_NO", "");
							map.put("REF_LABEL", getPropertyName("ref_no"));
							map.put("REF", mdl.getRef_no());
							
							
							map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("description"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("NET_PRICE_LABEL", getPropertyName("total_amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							if(mdl.getDiscountAmount() == 0){
								map.put("TOTAL_DISCOUNT_LABEL", "");
								map.put("TOTAL_DISCOUNT", (double)0);
							} else {
								map.put("TOTAL_DISCOUNT_LABEL", getPropertyName("total_discount"));
								map.put("TOTAL_DISCOUNT", mdl.getDiscountAmount());
							}
							
							
							map.put("SL_NO_LABEL_ARABIC", "Ø±Ù‚Ù…");
							map.put("ITEM_LABEL_ARABIC", "Ø§Ù„ÙˆØµÙ�");
							map.put("QUANTITY_LABEL_ARABIC", "Ø§Ù„ÙƒÙ…ÙŠØ©");
							map.put("UNIT_LABEL_ARABIC", "Ø§Ù„ÙˆØ­Ø¯Ø§Øª");
							map.put("UNIT_PRICE_LABEL_ARABIC", "Ø³Ø¹Ø± Ø§Ù„ÙˆØ­Ø¯Ø©");
							map.put("NET_PRICE_LABEL_ARABIC", "Ø§Ù„ÙƒÙ…ÙŠØ© Ø§Ù„ÙƒÙ„ÙŠØ©");
							map.put("TOTAL_LABEL_ARABIC", "Ø§Ù„Ù…Ø¬Ù…ÙˆØ¹");
							
							map.put("CURRENCY_LABEL", new CurrencyManagementDao().getselecteditem(getCurrencyID()).getCode());
							map.put("CURRENCY_LABEL_ARABIC", "Ø¯ÙŠÙ†Ø§Ø±");
							map.put("FILS_LABEL", new CurrencyManagementDao().getselecteditem(getCurrencyID()).getFractional_part());
							map.put("FILS_LABEL_ARABIC", "Ù�Ù„Ø³");
							
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
							
							String itemName="";
							double discountPrice=0;
							
							Iterator itr=mdl.getInventory_details_list().iterator();
							while (itr.hasNext()) {
								SalesInventoryDetailsModel det = (SalesInventoryDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								discountPrice=0;
								
								if(settings.isITEMS_IN_MULTIPLE_LANGUAGE())
									itemName=det.getItem().getName()+" / "+det.getItem().getSecondName();
								else
									itemName=det.getItem().getName();
								
								if(det.getDiscount_type()==1){
									discountPrice=roundNumber(det.getUnit_price()-roundNumber(det.getUnit_price()*det.getDiscountPercentage()/100));
								}else{
									discountPrice=(det.getUnit_price()-det.getDiscount());
								}
								
								bean.setItem(itemName);
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(discountPrice));
								bean.setTotal(roundNumber(discountPrice*bean.getQuantity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("TOTAL_IN_WORDS", getAmountInWords(mdl.getAmount()));
							
							map.put("DISTRIBUTION_LABEL", getPropertyName("distribution"));
							map.put("WHITE_COPY_LABEL", getPropertyName("white_copy_customers"));
							map.put("GREEN_COPY_LABEL", getPropertyName("green_copy_customers"));
							map.put("PINK_COPY_LABEL", getPropertyName("pink_copy_accounts"));
							
							map.put("PREPARED_BY_LABEL", getPropertyName("prepared_by"));
							map.put("PREPARED_BY_LABEL_ARABIC", "Ø£Ù�Ø¹Ø¯Øª Ø¨ÙˆØ§Ø³Ø·Ø©");
							
							map.put("REVIEWED_BY_LABEL", getPropertyName("received_by"));
							map.put("REVIEWED_BY_LABEL_ARABIC", "Ø§Ù„ØªÙŠ ØªÙ„Ù‚Ø§Ù‡Ø§");
							
							map.put("SIGN_LABEL", getPropertyName("signature"));
							map.put("SIGN_LABEL_ARABIC", "Ø§Ù„ØªÙˆÙ‚ÙŠØ¹");
							
							UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
							map.put("PREPARED_BY", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							map.put("REVIEWED_BY", "");
							
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrency_id()).getCode());
							map.put("EXPENSE_LABEL", getPropertyName("expendeture"));
							map.put("EXPENSE", roundNumber(mdl.getExpenseAmount()));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("PAYMENT_AMOUNT", roundNumber(mdl.getPayment_amount()));
							
							Report report = new Report(getLoginID());
//							report.setExportReport(false);
							report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
							report.setReportFileName("Sales Print");
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
						
						if(salesNoCombo.getValue()!=null && !salesNoCombo.getValue().toString().equals("0")){
							SalesModel mdl=dao.getSale((Long)salesNoCombo.getValue());
							
							CustomerModel ledger =  new CustomerDao().getCustomerFromLedger(mdl.getCustomer().getId());
							String address = "";
							if (ledger != null) {
								address = new AddressBusiness().getAddressString(ledger.getAddress().getId());
							}
							map.put("ADDRESS_LABEL", getPropertyName("address"));
							map.put("ADDRESS", address);
							map.put("LEDGER_NAME_LABEL", getPropertyName("mr_messers"));
							map.put("LEDGER_NAME_LABEL_IN_ARABIC", "Ø§Ù„Ø³ÙŠØ¯ / Ø§Ù„Ø³Ø§Ø¯Ø©");
							map.put("LEDGER", mdl.getCustomer().getName());
							
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("cash_credit_invoice"));
							map.put("REPORT_TITLE_LABEL_ARABIC", "Ø§Ù„Ø§Ø¦ØªÙ…Ø§Ù† Ø§Ù„Ù�Ø§ØªÙˆØ±Ø© / Ø§Ù„Ù†Ù‚Ø¯ÙŠØ©");
							
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("BILL_NO", mdl.getSales_number());
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE_LABEL_ARABIC", "Ø§Ù„ØªØ§Ø±ÙŠØ®");
							
							map.put("PACK_LABEL", getPropertyName("pack"));
							map.put("PACK_LABEL_ARABIC", "Ø­Ø²Ù…Ø©");
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("LPO_NO_LABEL", getPropertyName("lpo_no"));
							map.put("LPO_NO", dao.getSalesOrderSales(mdl.getId(), getOfficeID()));
							map.put("DO_NO_LABEL", getPropertyName("do_no"));
							map.put("DO_NO", "");
							map.put("REF_LABEL", getPropertyName("ref_no"));
							map.put("REF", mdl.getRef_no());
							
							
							map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("description"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("NET_PRICE_LABEL", getPropertyName("total_amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							map.put("SL_NO_LABEL_ARABIC", "Ø±Ù‚Ù…");
							map.put("ITEM_LABEL_ARABIC", "Ø§Ù„ÙˆØµÙ�");
							map.put("QUANTITY_LABEL_ARABIC", "Ø§Ù„ÙƒÙ…ÙŠØ©");
							map.put("UNIT_LABEL_ARABIC", "Ø§Ù„ÙˆØ­Ø¯Ø§Øª");
							map.put("UNIT_PRICE_LABEL_ARABIC", "Ø³Ø¹Ø± Ø§Ù„ÙˆØ­Ø¯Ø©");
							map.put("NET_PRICE_LABEL_ARABIC", "Ø§Ù„ÙƒÙ…ÙŠØ© Ø§Ù„ÙƒÙ„ÙŠØ©");
							map.put("TOTAL_LABEL_ARABIC", "Ø§Ù„Ù…Ø¬Ù…ÙˆØ¹");
							
							map.put("CURRENCY_LABEL", new CurrencyManagementDao().getselecteditem(getCurrencyID()).getCode());
							map.put("CURRENCY_LABEL_ARABIC", "Ø¯ÙŠÙ†Ø§Ø±");
							map.put("FILS_LABEL", new CurrencyManagementDao().getselecteditem(getCurrencyID()).getFractional_part());
							map.put("FILS_LABEL_ARABIC", "Ù�Ù„Ø³");
							
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
							
							Iterator itr=mdl.getInventory_details_list().iterator();
							while (itr.hasNext()) {
								SalesInventoryDetailsModel det = (SalesInventoryDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("TOTAL_IN_WORDS", getAmountInWords(mdl.getAmount()));
							
							map.put("DISTRIBUTION_LABEL", getPropertyName("distribution"));
							map.put("WHITE_COPY_LABEL", getPropertyName("white_copy_customers"));
							map.put("GREEN_COPY_LABEL", getPropertyName("green_copy_customers"));
							map.put("PINK_COPY_LABEL", getPropertyName("pink_copy_accounts"));
							
							map.put("PREPARED_BY_LABEL", getPropertyName("prepared_by"));
							map.put("PREPARED_BY_LABEL_ARABIC", "Ø£Ù�Ø¹Ø¯Øª Ø¨ÙˆØ§Ø³Ø·Ø©");
							
							map.put("REVIEWED_BY_LABEL", getPropertyName("received_by"));
							map.put("REVIEWED_BY_LABEL_ARABIC", "Ø§Ù„ØªÙŠ ØªÙ„Ù‚Ø§Ù‡Ø§");
							
							map.put("SIGN_LABEL", getPropertyName("signature"));
							map.put("SIGN_LABEL_ARABIC", "Ø§Ù„ØªÙˆÙ‚ÙŠØ¹");
							
							UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
							map.put("PREPARED_BY", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							map.put("REVIEWED_BY", "");
							
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrency_id()).getCode());
							map.put("EXPENSE_LABEL", getPropertyName("expendeture"));
							map.put("EXPENSE", roundNumber(mdl.getExpenseAmount()));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("PAYMENT_AMOUNT", roundNumber(mdl.getPayment_amount()));
							
							Report report = new Report(getLoginID());
//							report.setExportReport(false);
							report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
							report.setReportFileName("Sales Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.printReport();
							
							if(ledger!=null){
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
																		"You have a new Sales from "+ledger.getName()+" Bill No. "+mdl.getSales_number(),
																		"Sales",
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
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		itemGroupCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				reloadItemCombo();
			}
		});
		
		
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
		

		return pannel;
	}

	
	
	private void reloadItemCombo() {
		try {

			List itemList = dao.getAllActiveItemsWithAppendingItemCode(getOfficeID(),
					settings.isITEMS_IN_MULTIPLE_LANGUAGE(), 
					(Long)itemGroupCombo.getValue());

			itemCombo.setContainerDataSource(SCollectionContainer.setList(itemList, "id"));
			itemCombo.setItemCaptionPropertyId("name");
			itemCombo.setValue((long)0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void resetItems(){
		
		itemCombo.setReadOnly(false);
		quantityField.setReadOnly(false);
		unitSelect.setReadOnly(false);
		gradeCombo.setReadOnly(false);
		taxSelect.setReadOnly(false);
		discountField.setReadOnly(false);
		barcodeField.setReadOnly(false);
		locationCombo.setReadOnly(false);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
		stockSelectList.setReadOnly(false);
		itemDiscountRadio.setValue(2);
		itemDiscountPercentField.setNewValue("0");
		itemDiscountAmountField.setNewValue("0");
		
		itemCombo.setValue(null);
		quantityField.setValue("0");
		taxSelect.setValue(SConstants.tax.SALES_TAX);
		discountField.setValue("0");
		unitSelect.setValue(null);
		unitPriceField.setNewValue(getCurrencyID(),0);
		gradeCombo.setValue((long)0);
		if(locationMasterCombo.getValue()!=null){
			locationCombo.setValue(locationMasterCombo.getValue());
		}
		else
			locationCombo.setValue((long)0);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
	}
	
	
	public void calculateTotals() {
		try {
			double qty_ttl = 0, net_ttl = 0, discount=0,totalPrice=0;
			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				
				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
				net_ttl += toDouble(roundNumberToString((((Double) item.getItemProperty(TBC_DISCOUNT_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue())+(Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()+
						(Double) item.getItemProperty(TBC_CESS).getValue()));
				totalPrice += toDouble(roundNumberToString((((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue())+(Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()+
						(Double) item.getItemProperty(TBC_CESS).getValue()));
				
				
				/*qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
				net_ttl += (((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()/(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue())+(Double) item.getItemProperty(TBC_TAX_AMOUNT).getValue()+
						(Double) item.getItemProperty(TBC_CESS).getValue()-(Double) item.getItemProperty(TBC_DISCOUNT).getValue();*/
			}
			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, roundNumberToString(net_ttl));
			table.setColumnFooter(TBC_NET_PRICE_WITHOUT_DISCOUNT, roundNumberToString(totalPrice));
			double expense=0;
			try {
				expense=expensePanel.getDebitAmount();
			} catch (Exception e) {
				expense=0;
			}
			if(discountRadio.getValue()!=null){
				if((Integer)discountRadio.getValue()==1){
					double discPer=0;
					try {
						discPer=toDouble(discountPercentField.getValue().trim());
					} catch (Exception e1) {
						discPer=0;
					}
					discount=roundNumber((net_ttl+expense)*discPer/100);
				}
				else{
					try {
						discount=toDouble(discountAmountField.getValue().trim());
					} catch (Exception e1) {
						discount=0;
					}
				}
			}
			netAmountField.setNewValue(roundNumber(net_ttl+expense-discount));
			convertedField.setNewValue(roundNumber(net_ttl+expense-discount));
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
			
			if (stockSelectList.getValue() == null
					|| stockSelectList.getValue().equals("")) {
				setRequiredError(stockSelectList,
						getPropertyName("invalid_selection"), true);
				setRequiredError(changeStkButton,
						getPropertyName("invalid_selection"), true);
				stockSelectList.focus();
				ret = false;
			} else {
				setRequiredError(stockSelectList, null, false);
				setRequiredError(changeStkButton, null, false);
			}
			
			if(settings.isGRADING_ENABLED()){
				if (gradeCombo.getValue() == null || gradeCombo.getValue().equals("")) {
					gradeCombo.setValue((long)0);
				} 
			}else
				gradeCombo.setValue((long)0);
			
			if (locationCombo.getValue() == null || locationCombo.getValue().equals("")) {
				locationCombo.setValue((long)0);
			} 
			
			if(unitPriceField.getValue()<=0){
				unitPriceField.setNewValue(0.0);
			}

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
	public void loadOptions(long id,long orderId) {
		List list = new ArrayList();
		try {
			list.add(new SalesModel(0, "----Create New-----"));
			if(orderId==0)
			  list.addAll(dao.getAllSalesNumbersAsComment(getOfficeID()));
			else
				list.addAll(dao.getAllSalesUnderSalesOrder(getOfficeID(), orderId));
			
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			salesNoCombo.setContainerDataSource(bic);
			salesNoCombo.setItemCaptionPropertyId("sales_number");
			if(id!=0)
				salesNoCombo.setValue(id);
			else
				salesNoCombo.setValue(null);
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

		if (customerCombo.getValue() == null || customerCombo.getValue().equals("")) {
			setRequiredError(customerCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(customerCombo, null, false);
		
		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, "Select a Date", true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
		if (salesAccountCombo.getValue() == null || salesAccountCombo.getValue().equals("")) {
			setRequiredError(salesAccountCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(salesAccountCombo, null, false);
		
		if(payingAmountField.getValue()<0){
			setRequiredError(payingAmountField, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(payingAmountField, null, false);
		
		if(expensePanel.getAmount()<0){
			setRequiredError(expenseField, "Add Expenses", true);
			ret = false;
		} else
			setRequiredError(expenseField, null, false);
		
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
		
		if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.PAYMENT){
			if(payingAmountField.getValue()<=0){
				setRequiredError(payingAmountField, getPropertyName("invalid_data"), true);
				ret = false;
			} else
				setRequiredError(payingAmountField, null, false);
			
//			if (paymentModeCombo.getValue() == null || paymentModeCombo.getValue().equals("")) {
//				setRequiredError(paymentModeCombo, getPropertyName("invalid_selection"), true);
//				ret = false;
//			} else
//				setRequiredError(paymentModeCombo, null, false);
			
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
		
		if((Long)paymentCreditRadio.getValue()==SConstants.paymentMode.CASH){
			if(payingAmountField.getValue()<(netAmountField.getValue()-expensePanel.getCreditAmount())){
				setRequiredError(payingAmountField, getPropertyName("invalid_data"), true);
				ret = false;
			}
			else
				setRequiredError(payingAmountField, null, false);
		}
		else if(payingAmountField.getValue()<0) {
			setRequiredError(payingAmountField, getPropertyName("invalid_data"), true);
			ret = false;
		}
		else
			setRequiredError(payingAmountField, null, false);
		
		return ret;
	}

	
	public void calculateNetPrice(){
		if (quantityField.getValue() == null || quantityField.getValue().toString().equals("")) {
			quantityField.setValue("0");
		} else {
			try {
				if (Double.parseDouble(quantityField.getValue()) <0) {
					quantityField.setValue("0");
				} 
			} catch (Exception e) {
				quantityField.setValue("0");
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

	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	public SComboField getSalesNumberList() {
		return salesNoCombo;
	}

	
	public void setSalesNumberList(SComboField salesNoCombo) {
		this.salesNoCombo = salesNoCombo;
	}
	
	
	@Override
	public SComboField getBillNoFiled() {
		return salesNoCombo;
	}
}
