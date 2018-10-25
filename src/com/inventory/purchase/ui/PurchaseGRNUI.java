package com.inventory.purchase.ui;

import java.io.File;
import java.util.ArrayList;
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
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
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
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.LocationDao;
import com.inventory.model.LocationModel;
import com.inventory.purchase.bean.PurchaseGRNBean;
import com.inventory.purchase.dao.PurchaseGRNDao;
import com.inventory.purchase.dao.PurchaseOrderDao;
import com.inventory.purchase.model.PurchaseGRNDetailsModel;
import com.inventory.purchase.model.PurchaseGRNModel;
import com.inventory.purchase.model.PurchaseOrderDetailsModel;
import com.inventory.reports.bean.AcctReportMainBean;
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
import com.webspark.Components.SSelectionField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
public class PurchaseGRNUI extends SparkLogic {

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
	
	static String TBC_MANUFACTURING_DATE_ID="Manf Date Id";
	static String TBC_MANUFACTURING_DATE="Manf. Date";
	static String TBC_EXPIRY_DATE_ID="Exp. Date Id";
	static String TBC_EXPIRY_DATE="Exp. Date";
	static String TBC_GRADE_ID="Grade Id";
	static String TBC_GRADE="Grade";
	static String TBC_LOCATION_ID="Location Id";
	static String TBC_LOCATION="Location";
	
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_BATCH_ID = "Batch Id";
	static String TBC_STOCK_ID = "Stock Id";
	static String TBC_ORDER_CHILD_ID = "Order Child Id";
	static String TBC_ORDER_ID = "Order Id";

	PurchaseGRNDao dao;
	SComboField purchaseGrnCombo;
	STextField referenceNoField;
	SDateField dateField;
	SComboField locationMasterCombo;
	SButton newLocationButton;
	SComboField supplierCombo;
	SButton newSupplierButton;
	SComboField responsilbeEmployeeCombo;
	STextField contactField;
	
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
	SCurrencyField unitPriceField;
	Date previousDate;
	
	
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
	
	SButton importButton;
	SButton addOrderButton;
	SOptionGroup purchaseOrderOptions;	
	SWindow purchaseOrderWindow;

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
	SupplierDao supDao;

	SButton createNewButton;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		previousDate=new Date();
		previousDate=getWorkingDate();
		dao=new PurchaseGRNDao();
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

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Purchase Inquiry");
		
		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_EDITABLE, TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME,
				TBC_QTY, TBC_CONVERTION_QTY, TBC_QTY_IN_BASIC_UNIT, TBC_QTY_ORDERED, TBC_BALANCE, 
				TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE, TBC_CID, TBC_CURRENCY, TBC_CONV_RATE, TBC_NET_PRICE, TBC_MANUFACTURING_DATE_ID, 
				TBC_MANUFACTURING_DATE, TBC_EXPIRY_DATE_ID, TBC_EXPIRY_DATE, TBC_GRADE_ID, TBC_GRADE, TBC_LOCATION_ID, TBC_LOCATION, 
				TBC_BATCH_ID, TBC_STOCK_ID, TBC_ORDER_CHILD_ID, TBC_ORDER_ID };
		
		
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, 
					/*TBC_QTY_ORDERED, TBC_BALANCE, */ TBC_UNIT, TBC_UNIT_PRICE, TBC_CURRENCY,TBC_MANUFACTURING_DATE, TBC_EXPIRY_DATE, 
					TBC_GRADE, TBC_LOCATION, TBC_NET_PRICE };

		List<Object> tempList = new ArrayList<Object>();
		Collections.addAll(tempList, requiredHeaders);
		
		if(!settings.isGRADING_ENABLED()){
			tempList.remove(TBC_GRADE);
		}
		if (!isManufDateEnable()) {
			tempList.remove(TBC_MANUFACTURING_DATE);
			tempList.remove(TBC_EXPIRY_DATE);
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
		bottomGrid.setColumns(12);
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
			purchaseGrnCombo = new SComboField(null, 150, null, "id","ref_no", false, getPropertyName("create_new"));
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
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(purchaseGrnCombo);
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
			locationMasterCombo=new SComboField(null, 150, locationList, "id", "name", true, getPropertyName("select"));
			locationMasterCombo.setValue((long)0);
			
			SHorizontalLayout locationLayout = new SHorizontalLayout();
			locationLayout.addComponent(locationMasterCombo);
			locationLayout.addComponent(newLocationButton);
			
			masterDetailsGrid.addComponent(new SLabel("GRN No"), 1, 0);
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

			masterDetailsGrid.setColumnExpandRatio(1, 1);
			masterDetailsGrid.setColumnExpandRatio(2, 1);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 1);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 1);
			masterDetailsGrid.setColumnExpandRatio(7, 1);

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
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("contact")), 1,2);
			masterDetailsGrid.addComponent(contactField, 2,2);
			
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

			netPriceField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			itemLayout.addComponent(itemLay, 1, 1);
			itemLayout.addComponent(quantityField, 3, 1);
			itemLayout.addComponent(convertionQuantityField, 4, 1);
			itemLayout.addComponent(convertedQuantityField);
			itemLayout.addComponent(unitSelect, 5, 1);
			itemLayout.addComponent(unitPriceField, 8, 1);
			if(isManufDateEnable()){
				itemLayout.addComponent(manufacturingDateField, 9, 1);
				itemLayout.addComponent(expiryDateField, 10, 1);
			}
			if(settings.isGRADING_ENABLED()){
				itemLayout.addComponent(gradeCombo, 11, 1);
			}
			itemLayout.addComponent(locationCombo, 12, 1);
			itemLayout.addComponent(netPriceField, 13, 1);
			itemLayout.addComponent(buttonLay, 14, 1);
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
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null, getPropertyName("net_price"), null, Align.LEFT);
			
			table.addContainerProperty(TBC_MANUFACTURING_DATE_ID, Date.class, null, TBC_MANUFACTURING_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_MANUFACTURING_DATE, String.class, null, TBC_MANUFACTURING_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_EXPIRY_DATE_ID, Date.class, null, TBC_EXPIRY_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_EXPIRY_DATE, String.class, null, TBC_EXPIRY_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_GRADE_ID, Long.class, null, TBC_GRADE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_GRADE, String.class, null, TBC_GRADE, null, Align.LEFT);
			table.addContainerProperty(TBC_LOCATION_ID, Long.class, null, TBC_LOCATION_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_LOCATION, String.class, null, TBC_LOCATION, null, Align.LEFT);
			
			table.addContainerProperty(TBC_BATCH_ID, Long.class, null,TBC_BATCH_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,TBC_STOCK_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ORDER_CHILD_ID, Long.class, null,TBC_ORDER_CHILD_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ORDER_ID, Long.class, null,TBC_ORDER_ID, null, Align.CENTER);

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
			
			table.setColumnExpandRatio(TBC_MANUFACTURING_DATE, 1f);
			table.setColumnExpandRatio(TBC_EXPIRY_DATE, 1f);
			table.setColumnExpandRatio(TBC_GRADE, 1f);
			table.setColumnExpandRatio(TBC_LOCATION, 1f);
			

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			calculateTotals();

			table.setPageLength(table.size());

			table.setWidth("1250");
			table.setHeight("200");

			comment = new STextArea(null, 400, 70);

			bottomGrid.addComponent(new SLabel(""), 6, 0);
			bottomGrid.addComponent(new SLabel(getPropertyName("approximate_amount")), 6, 1);
			bottomGrid.addComponent(netAmountField, 9, 1);
			bottomGrid.addComponent(convertedField, 4, 1);
			convertedField.setVisible(false);
			
			
			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.setComponentAlignment(netAmountField,
					Alignment.TOP_RIGHT);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			importButton = new SButton(null, "Import From Purchase Order");
			importButton.setStyleName("importButtonStyle");
			addOrderButton = new SButton(null,"Import Purchase Orders");
			addOrderButton.setStyleName("importButtonStyle");
			purchaseOrderOptions = new SOptionGroup("Select Purchase Order : ", 300, null, "id", "name", true);
			purchaseOrderWindow = new SWindow("Purchase Order", 650, 250);
			purchaseOrderWindow.center();
			purchaseOrderWindow.setResizable(false);
			purchaseOrderWindow.setModal(true);
			
			SFormLayout popUpLayout = new SFormLayout();
			popUpLayout.setSpacing(true);
			popUpLayout.setMargin(true);
			popUpLayout.addComponent(purchaseOrderOptions);
			popUpLayout.addComponent(addOrderButton);
			purchaseOrderWindow.setContent(popUpLayout);
			
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

			supplierLayout.addComponent(importButton);
			supplierLayout.setComponentAlignment(importButton, Alignment.TOP_CENTER);
			
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

			formLayout.addComponent(masterDetailsGrid);
			formLayout.addComponent(table);
			formLayout.addComponent(itemLayout);
			formLayout.addComponent(bottomGrid);
			formLayout.addComponent(buttonsGrid);

			formLayout.setWidth("700");

			hLayout.addComponent(popupLay);
			hLayout.addComponent(formLayout);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			
			pannel.setContent(windowNotif);
			loadOptions(0);
			
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
			
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(dateField.getValue()!=null){
						if(previousDate.getTime()!=dateField.getValue().getTime()){
							final long id=(Long)netAmountField.currencySelect.getValue();
							final long cid=(Long)unitPriceField.currencySelect.getValue();
							if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID()){
								ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Update Currency Rate Accordingly.",new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (!dialog.isConfirmed()) {
											dateField.setValue(previousDate);
										}
										previousDate=dateField.getValue();
										netAmountField.setCurrencyDate(previousDate);
										unitPriceField.setCurrencyDate(previousDate);
										netAmountField.currencySelect.setValue(null);
										netAmountField.currencySelect.setValue(id);
										unitPriceField.currencySelect.setNewValue(null);
										unitPriceField.currencySelect.setNewValue(cid);
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
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)purchaseGrnCombo.getValue(),confirmBox.getUserID());
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
						if(purchaseGrnCombo.getValue()!=null && !purchaseGrnCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)purchaseGrnCombo.getValue(),
									"Purchase Order : No. "+purchaseGrnCombo.getItemCaption(purchaseGrnCombo.getValue()));
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
						if(purchaseGrnCombo.getValue()!=null && !purchaseGrnCombo.getValue().toString().equals("0")) {
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

			
			table.addShortcutListener(new ShortcutListener("Delete Item",
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
					if(locationMasterCombo.getValue()!=null){
						locationCombo.setValue(locationMasterCombo.getValue());
					}
					else
						locationCombo.setValue((long)0);
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
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
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
						if (supplierCombo.getValue() != null) {
							setRequiredError(supplierCombo, null, false);
							supplierCombo.setDescription("<h1><i>Current Balance</i> : "+ comDao.getLedgerCurrentBalance((Long) supplierCombo.getValue()) + "</h1>");
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
					purchaseGrnCombo.setValue((long) 0);
					purchaseGrnCombo.setValue(null);
				}
			});

			
			importButton.addClickListener(new ClickListener() {
				
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
										roundNumber(bean.getDet().getUnit_price()*bean.getDet().getQunatity()),
										getWorkingDate(),
										CommonUtil.formatDateToDDMMYYYY(getWorkingDate()),
										getWorkingDate(),
										CommonUtil.formatDateToDDMMYYYY(getWorkingDate()),
										(long)0,
										"None",
										(long)0,
										"None",
										(long)0,
										(long)0,
										bean.getDet().getId(),
										bean.getId()},table.getItemIds().size()+1);
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
							quantityField.selectAll();
							quantityField.focus();
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
									double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(), 0);
									convertionQuantityField.setValue(asString(cnvr_qty));
								}
								unitPriceField.setNewValue(comDao.getItemCurrency(itm.getId(), (Long) unitSelect.getValue(), (long)0),
										roundNumber(comDao.getItemPrice(itm.getId(), (Long) unitSelect.getValue(), (long)0)));
								if (quantityField.getValue() != null && !quantityField.getValue().toString().equals("0")) {
									convertedQuantityField.setNewValue(asString(Double.parseDouble(quantityField.getValue())
											* Double.parseDouble(convertionQuantityField.getValue())));
									netPriceField.setNewValue(asString(unitPriceField.getValue()*toDouble(quantityField.getValue())));
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
								netPriceField.setNewValue(asString(unitPriceField.getValue()*toDouble(quantityField.getValue())));
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

			
			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {
							table.setVisibleColumns(allHeaders);
							ItemModel itemModel = itemDao.getItem((Long) itemCombo.getValue());
							UnitModel unitModel = untDao.getUnit((Long) unitSelect.getValue());
							double qty=toDouble(quantityField.getValue().toString());
							double amount=unitPriceField.getValue();
							double netPrice=(unitPriceField.getValue()*qty);
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
									roundNumber(netPrice),
									manufacturingDate,
									CommonUtil.formatDateToDDMMYYYY(manufacturingDate),
									expiryDate,
									CommonUtil.formatDateToDDMMYYYY(expiryDate),
									(Long)gradeCombo.getValue(),
									gradeCombo.getItemCaption((Long)gradeCombo.getValue()),
									(Long)locationCombo.getValue(),
									locationCombo.getItemCaption((Long)locationCombo.getValue()),
									(long)0,
									(long)0,
									(long)0,
									(long)0},table.getItemIds().size()+1);
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
							netPriceField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_NET_PRICE).getValue())+"");
							convertionQuantityField.setNewValue(""+ roundNumber((Double)item.getItemProperty(TBC_CONVERTION_QTY).getValue()));
							manufacturingDateField.setValue((Date)item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue());
							expiryDateField.setValue((Date)item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue());
							gradeCombo.setValue((Long)item.getItemProperty(TBC_GRADE_ID).getValue());
							locationCombo.setValue((Long)item.getItemProperty(TBC_LOCATION_ID).getValue());
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
								double netPrice=(unitPriceField.getValue()*qty);
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
								item.getItemProperty(TBC_NET_PRICE).setValue(roundNumber(netPrice));
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
							boolean savable=true;
							PurchaseGRNModel mdl=new PurchaseGRNModel();
							List<PurchaseGRNDetailsModel> itemsList = new ArrayList<PurchaseGRNDetailsModel>();
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								PurchaseGRNDetailsModel det=new PurchaseGRNDetailsModel();
								Item item = table.getItem(it.next());
								double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
								if(qty<=0){
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
								
								det.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue()));
								det.setExpiry_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue()));
								det.setGrade_id((Long) item.getItemProperty(TBC_GRADE_ID).getValue());
								det.setLocation_id((Long) item.getItemProperty(TBC_LOCATION_ID).getValue());
								
								det.setOrder_child_id((Long) item.getItemProperty(TBC_ORDER_CHILD_ID).getValue());
								det.setOrder_id((Long) item.getItemProperty(TBC_ORDER_ID).getValue());
								itemsList.add(det);
							}
							if(savable){
								mdl.setGrn_no(getNextSequence("Purchase GRN No", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setSupplier(new LedgerModel((Long)supplierCombo.getValue()));
								mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setComments(comment.getValue());
								mdl.setAmount(roundNumber(netAmountField.getValue()));
								mdl.setCurrencyId(netAmountField.getCurrency());
								mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
								mdl.setActive(true);
								mdl.setDepartment_id((Long)departmentCombo.getValue());
								mdl.setDivision_id((Long)divisionCombo.getValue());
								mdl.setGrn_details_list(itemsList);
								long id=dao.save(mdl);
								loadOptions(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),
										"Purchase GRN Created. GRN No : "
												+ mdl.getGrn_no()+ ", Supplier : "
												+ supplierCombo.getItemCaption(supplierCombo.getValue())
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

			
			purchaseGrnCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						table.removeAllItems();
						calculateTotals();
						netAmountField.setCurrency(getCurrencyID());
						table.setValue(null);
						referenceNoField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						netAmountField.setCurrencyDate(getWorkingDate());
						supplierCombo.setValue(null);
						responsilbeEmployeeCombo.setValue(null);
						comment.setValue("");
						departmentCombo.setValue((long)0);
						divisionCombo.setValue(null);
						saveButton.setVisible(true);
						printButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						sendMailButton.setVisible(false);
						resetItems();
						if(purchaseGrnCombo.getValue()!=null && !purchaseGrnCombo.getValue().toString().equals("0")){
							table.setVisibleColumns(allHeaders);
							PurchaseGRNModel mdl=dao.getPurchaseGRNModel((Long)purchaseGrnCombo.getValue());
							referenceNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							netAmountField.setCurrencyDate(mdl.getDate());
							supplierCombo.setValue(mdl.getSupplier().getId());
							responsilbeEmployeeCombo.setValue(mdl.getResponsible_employee());
							comment.setValue(mdl.getComments());
							departmentCombo.setValue(mdl.getDepartment_id());
							divisionCombo.setValue(mdl.getDivision_id());
							Iterator itr=mdl.getGrn_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseGRNDetailsModel det=(PurchaseGRNDetailsModel)itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode();
								boolean editable=true;
								if (comDao.isStockBlocked(mdl.getId(),
															det.getId(),
															SConstants.stockPurchaseType.PURCHASE_GRN)){
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
										roundNumber(det.getUnit_price()*det.getQunatity()),
										det.getManufacturing_date(),
										CommonUtil.formatDateToDDMMYYYY(det.getManufacturing_date()),
										det.getExpiry_date(),
										CommonUtil.formatDateToDDMMYYYY(det.getExpiry_date()),
										det.getGrade_id(),
										gradeCombo.getItemCaption(det.getGrade_id()),
										det.getLocation_id(),
										locationCombo.getItemCaption(det.getLocation_id()),
										det.getBatch_id(),
										det.getStock_id(),
										det.getOrder_child_id(),
										det.getOrder_id()},table.getItemIds().size()+1);
							}
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
							netAmountField.setCurrency(mdl.getCurrencyId());
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
							if(purchaseGrnCombo.getValue()!=null && !purchaseGrnCombo.getValue().toString().equals("0")){
								boolean savable=true;
								PurchaseGRNModel mdl=dao.getPurchaseGRNModel((Long)purchaseGrnCombo.getValue());
								List<PurchaseGRNDetailsModel> itemsList = new ArrayList<PurchaseGRNDetailsModel>();
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									PurchaseGRNDetailsModel det=null;
									Item item = table.getItem(it.next());
									long id=(Long) item.getItemProperty(TBC_ID).getValue();
									if(id!=0)
										det=dao.getPurchaseGRNDetailsModel(id);
									if(det==null)
										det=new PurchaseGRNDetailsModel();
									double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
									if(qty<=0){
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
									
									det.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue()));
									det.setExpiry_date(CommonUtil.getSQLDateFromUtilDate((Date) item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue()));
									det.setGrade_id((Long) item.getItemProperty(TBC_GRADE_ID).getValue());
									det.setLocation_id((Long) item.getItemProperty(TBC_LOCATION_ID).getValue());
									
									det.setOrder_child_id((Long) item.getItemProperty(TBC_ORDER_CHILD_ID).getValue());
									det.setOrder_id((Long) item.getItemProperty(TBC_ORDER_ID).getValue());
									itemsList.add(det);
								}
								if(savable){
									mdl.setRef_no(referenceNoField.getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									mdl.setSupplier(new LedgerModel((Long)supplierCombo.getValue()));
									mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									mdl.setComments(comment.getValue());
									mdl.setAmount(roundNumber(netAmountField.getValue()));
									mdl.setCurrencyId(netAmountField.getCurrency());
									mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
									mdl.setGrn_details_list(itemsList);
									mdl.setDepartment_id((Long)departmentCombo.getValue());
									mdl.setDivision_id((Long)divisionCombo.getValue());
									dao.update(mdl);
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId());
									saveActivity(getOptionId(),
											"Purchase GRN Updated. GRN No : "
													+ mdl.getGrn_no()+ ", Supplier : "
													+ supplierCombo.getItemCaption(supplierCombo.getValue())
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
						if (purchaseGrnCombo.getValue() != null && !purchaseGrnCombo.getValue().toString().equals("0")) {
							Iterator itr=table.getItemIds().iterator();
							boolean blocked =false;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								if (comDao.isStockBlocked(	(Long) purchaseGrnCombo.getValue(),
															(Long) item.getItemProperty(TBC_ID).getValue(),
															SConstants.stockPurchaseType.PURCHASE_GRN)) {
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
												PurchaseGRNModel mdl=dao.getPurchaseGRNModel((Long)purchaseGrnCombo.getValue());
												if(mdl.getLock_count()>0){
													Notification.show("Purchase Created On This Order. Cannot Delete",Type.ERROR_MESSAGE);
												}
												else{
													dao.delete(mdl);
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													saveActivity(getOptionId()," Purchase GRN deleted. GRN No : "
																+ purchaseGrnCombo.getItemCaption(purchaseGrnCombo.getValue())
																+ ", Supplier : "+supplierCombo.getItemCaption(supplierCombo.getValue()),
																(Long)purchaseGrnCombo.getValue());
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
						if (purchaseGrnCombo.getValue() != null && !purchaseGrnCombo.getValue().toString().equals("0")) {
							Iterator itr=table.getItemIds().iterator();
							boolean blocked =false;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								if (comDao.isStockBlocked(	(Long) purchaseGrnCombo.getValue(),
															(Long) item.getItemProperty(TBC_ID).getValue(),
															SConstants.stockPurchaseType.PURCHASE_GRN)) {
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
												PurchaseGRNModel mdl=dao.getPurchaseGRNModel((Long)purchaseGrnCombo.getValue());
												if(mdl.getLock_count()>0){
													Notification.show("Purchase Created On This Order. Cannot Cancel",Type.ERROR_MESSAGE);
												}
												else{
													dao.cancel(mdl);
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													saveActivity(getOptionId()," Purchase GRN Cancelled. GRN No : "
																+ purchaseGrnCombo.getItemCaption(purchaseGrnCombo.getValue())
																+ ", Supplier : "+supplierCombo.getItemCaption(supplierCombo.getValue()),
																(Long)purchaseGrnCombo.getValue());
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
								Notification.show("Cannot Cancel, Stock Exists in Sale", Type.ERROR_MESSAGE);
							}
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
						if(purchaseGrnCombo.getValue()!=null && !purchaseGrnCombo.getValue().toString().equals("0")){
							PurchaseGRNModel mdl=dao.getPurchaseGRNModel((Long)purchaseGrnCombo.getValue());
							SupplierModel ledger = supDao.getSupplierFromLedger(toLong(supplierCombo.getValue().toString()));
							String address = "";
							if (ledger != null) {
								address = new AddressDao().getAddressString(ledger.getAddress().getId());
							}
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_grn"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase_grn"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
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
							map.put("BILL_NO", mdl.getGrn_no());
							
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
							
							Iterator itr=mdl.getGrn_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrencyId()).getCode());
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE_ORDER));
							report.setReportFileName("Purchase GRN Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.print();
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
						
						if(purchaseGrnCombo.getValue()!=null && !purchaseGrnCombo.getValue().toString().equals("0")){
							PurchaseGRNModel mdl=dao.getPurchaseGRNModel((Long)purchaseGrnCombo.getValue());
							SupplierModel ledger = supDao.getSupplierFromLedger(toLong(supplierCombo.getValue().toString()));
							String addr = "";
							if (ledger != null) {
								addr = new AddressDao().getAddressString(ledger.getAddress().getId());
							}
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_grn"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase_grn"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
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
							map.put("BILL_NO", mdl.getGrn_no());
							
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
							
							Iterator itr=mdl.getGrn_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrencyId()).getCode());
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE_ORDER));
							report.setReportFileName("Purchase GRN Print");
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
																	"You have a new Purchase GRN from "+ledger.getName()+" Bill No. "+mdl.getGrn_no(),
																	"Purchase GRN",
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

		return pannel;
	}

	
	public void resetItems(){
		itemCombo.setValue(null);
		quantityField.setValue("0");
		unitSelect.setValue(null);
		unitPriceField.setNewValue(getCurrencyID(),0.0);
		manufacturingDateField.setValue(getWorkingDate());
		expiryDateField.setValue(getWorkingDate());
		gradeCombo.setValue((long)0);
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
			double qty_ttl = 0, netTotal = 0;
			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
				netTotal += (((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue());
			}
			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(netTotal)));
			netAmountField.setNewValue(roundNumber(netTotal));
			convertedField.setNewValue(roundNumber(netTotal));
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
			
			if(settings.isGRADING_ENABLED()){
				if (gradeCombo.getValue() == null || gradeCombo.getValue().equals("")) {
					gradeCombo.setValue((long)0);
				} 
			}else
				gradeCombo.setValue((long)0);
			
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
			
			if(unitPriceField.getValue()<0){
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
	
	
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				Collection selectedItems = (Collection) table.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					Object obj=it1.next();
					Item item=table.getItem(obj);
					if (purchaseGrnCombo.getValue() != null && !purchaseGrnCombo.getValue().toString() .equals("0")) {
						
						if (!comDao.isStockBlocked(	(Long) purchaseGrnCombo.getValue(),
													(Long) item.getItemProperty(TBC_ID).getValue(),
													SConstants.stockPurchaseType.PURCHASE_GRN)) 
							table.removeItem(obj);
					} else {
						table.removeItem(obj);
					}
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
	}*/

	
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
			list.add(new PurchaseGRNModel(0, "----Create New-----"));
			list.addAll(dao.getPurchaseGRNModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			purchaseGrnCombo.setContainerDataSource(bic);
			purchaseGrnCombo.setItemCaptionPropertyId("grn_no");
			purchaseGrnCombo.setValue(id);
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
		
		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, "Select a Date", true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
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
		try {
			netPrice = toDouble(quantityField.getValue().toString())*unitPriceField.getValue();
		} catch (Exception e) {
			netPrice=0;
		}
		netPriceField.setNewValue(roundNumber(netPrice)+"");
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

	
	public SComboField getPurchaseOrderNumberList() {
		return purchaseGrnCombo;
	}

	
	public void setPurchaseOrderNumberList(SComboField purchaseGrnCombo) {
		this.purchaseGrnCombo = purchaseGrnCombo;
	}
	
	
	@Override
	public SComboField getBillNoFiled() {
		return purchaseGrnCombo;
	}
}
