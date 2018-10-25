package com.inventory.purchase.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.ClearingAgentDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.ClearingAgentModel;
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
import com.inventory.model.LocationModel;
import com.inventory.purchase.dao.StockCreateDao;
import com.inventory.purchase.model.StockCreateDetailsModel;
import com.inventory.purchase.model.StockCreateModel;
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
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author sangeeth
 *
 */

/**
 * @author sangeeth
 * @date 21-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class CreateStockUI extends SparkLogic {

	static String TBC_SN = "SN";
	static String TBC_EDITABLE = "Editable";
	static String TBC_ID = "Id";
	static String TBC_ITEM_ID = "Item Id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_CONVERTION_QTY = "Convertion Qty";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";
	static String TBC_UNIT_ID = "Unit Id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_CID="Currency Id";
	static String TBC_CURRENCY="Currency";
	static String TBC_CONV_RATE="Conv Rate";
	
	static String TBC_DISCOUNT = "Discount";
	static String TBC_TAX_ID = "Tax Id";
	static String TBC_TAX_PERCENTAGE = "Tax Percentage";
	static String TBC_TAX_AMOUNT = "Tax Amount";
	static String TBC_CESS = "Cess";
	
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

	StockCreateDao dao;
	
	SComboField invoiceCombo;
	STextField referenceNoField;
	SDateField dateField;
	SComboField locationMasterCombo;
	SButton newLocationButton;
	SComboField responsilbeEmployeeCombo;
	
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
	SNativeSelect unitSelect;
	SButton newUnitButton;
	SButton unitMapButton;
	
	SCurrencyField unitPriceField;
	STextField discountField;
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
	
	ItemDao itemDao = new ItemDao();

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SWindow popupWindow;

	SCurrencyField netAmountField;
	SCurrencyField convertedField;
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
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		previousDate=new Date();
		previousDate=getWorkingDate();
		dao=new StockCreateDao();
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
		
		allHeaders = new String[] { TBC_SN, TBC_EDITABLE, TBC_ID, TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, TBC_CONVERTION_QTY, 
				TBC_QTY_IN_BASIC_UNIT,  TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE, TBC_CID, TBC_CURRENCY, TBC_CONV_RATE, TBC_DISCOUNT, 
				TBC_TAX_ID, TBC_TAX_PERCENTAGE, TBC_TAX_AMOUNT, TBC_CESS, TBC_NET_PRICE, TBC_MANUFACTURING_DATE_ID, TBC_MANUFACTURING_DATE, 
				TBC_EXPIRY_DATE_ID, TBC_EXPIRY_DATE, TBC_GRADE_ID, TBC_GRADE, TBC_LOCATION_ID, TBC_LOCATION, TBC_BARCODE, TBC_BATCH_ID, TBC_STOCK_ID};
		
		
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE, TBC_CURRENCY, 
					TBC_DISCOUNT, TBC_TAX_AMOUNT, TBC_CESS, TBC_MANUFACTURING_DATE, TBC_EXPIRY_DATE, TBC_GRADE, TBC_LOCATION, TBC_BARCODE,
					TBC_NET_PRICE };

		List<Object> tempList = new ArrayList<Object>();
		Collections.addAll(tempList, requiredHeaders);
		
		
		if(!settings.isDISCOUNT_ENABLE()){
			tempList.remove(TBC_DISCOUNT);
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
		setSize(1200, 645);

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
			invoiceCombo = new SComboField(null, 150, null, "id","ref_no", false, getPropertyName("create_new"));
			referenceNoField = new STextField(null, 150);
			dateField = new SDateField(null, 100, getDateFormat());
			responsilbeEmployeeCombo = new SComboField(null,150,
					new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name", false, getPropertyName("select"));
			
			List clearingList=new ArrayList();
			clearingList.add(0, new ClearingAgentModel(0, "None"));
			clearingList.addAll(new ClearingAgentDao().getAllActiveClearingAgentNamesWithLedgerID(getOfficeID()));
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(invoiceCombo);
			salLisrLay.addComponent(createNewButton);
			
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
			
			masterDetailsGrid.addComponent(new SLabel("Invoice No"), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 3, 0);
			masterDetailsGrid.addComponent(dateField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel("Location"), 5, 0);
			masterDetailsGrid.addComponent(locationLayout, 6, 0);
			
			masterDetailsGrid.setSpacing(true);

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

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("employee")), 1, 1);
			masterDetailsGrid.addComponent(responsilbeEmployeeCombo, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 1);
			masterDetailsGrid.addComponent(referenceNoField, 4, 1);
			
			masterDetailsGrid.setComponentAlignment(referenceNoField,Alignment.MIDDLE_LEFT);
			masterDetailsGrid.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);
		
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
			discountField = new STextField(getPropertyName("discount"),50);
			discountField.setValue("0");
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

			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			SHorizontalLayout unitLayout = new SHorizontalLayout();
			unitLayout.addComponent(unitSelect);
			unitLayout.addComponent(vert);
			
			itemLayout.addComponent(itemLay, 1, 1);
			itemLayout.addComponent(quantityField, 3, 1);
			itemLayout.addComponent(unitLayout, 4, 1);
			itemLayout.addComponent(convertionQuantityField, 5, 1);
			itemLayout.addComponent(convertedQuantityField, 6 ,1);
			itemLayout.addComponent(unitPriceField, 8, 1);
			if(isDiscountEnable()){
				itemLayout.addComponent(discountField, 9, 1);
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

			table = new STable(null, 1120, 150);

			table.setMultiSelect(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TBC_EDITABLE, Boolean.class, null, TBC_EDITABLE, null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null,TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null, getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_CONVERTION_QTY, Double.class, null,"Convertion Qty", null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class, null,"Qty Basic Unit", null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null, TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null, getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null, getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CID, Long.class, null, TBC_CID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY, String.class, null, TBC_CURRENCY, null, Align.CENTER);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null, TBC_CONV_RATE, null, Align.LEFT);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null, TBC_DISCOUNT, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null, TBC_TAX_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_PERCENTAGE, Double.class, null, TBC_TAX_PERCENTAGE, null, Align.LEFT);
			table.addContainerProperty(TBC_TAX_AMOUNT, Double.class, null, TBC_TAX_AMOUNT, null, Align.LEFT);
			table.addContainerProperty(TBC_CESS, Double.class, null, TBC_CESS, null, Align.LEFT);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null, getPropertyName("net_price"), null, Align.LEFT);
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
			
			table.setColumnExpandRatio(TBC_SN, 0.15f);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 0.75f);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 1.5f);
			table.setColumnExpandRatio(TBC_QTY, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 0.75f);
			
			table.setColumnExpandRatio(TBC_DISCOUNT, 0.75f);
			table.setColumnExpandRatio(TBC_TAX_AMOUNT, 0.75f);
			table.setColumnExpandRatio(TBC_CESS, 0.75f);
			
			table.setColumnExpandRatio(TBC_CURRENCY, 0.75f);
			table.setColumnExpandRatio(TBC_NET_PRICE, 0.95f);
			
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

			table.setWidth("1120");
			table.setHeight("150");

			commentArea = new STextArea(null, 250, 40);

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
//			mainButtonLayout.addComponent(printButton);
//			mainButtonLayout.addComponent(sendMailButton);

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
			
			loadOptions(0);
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)invoiceCombo.getValue(),confirmBox.getUserID());
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
						if(invoiceCombo.getValue()!=null && !invoiceCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)invoiceCombo.getValue(),
									"Purchase Order : No. "+invoiceCombo.getItemCaption(invoiceCombo.getValue()));
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
						if(invoiceCombo.getValue()!=null && !invoiceCombo.getValue().toString().equals("0")) {
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
			
			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					invoiceCombo.setValue((long) 0);
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
							
							if(isDiscountEnable()){
								try {
									discount=toDouble(discountField.getValue().toString());
								} catch (Exception e) {
									discount=0;
								}
							}
							else
								discount=0;
							
							double netPrice=(unitPriceField.getValue()*qty)+taxAmount+cess-discount;
							
							table.addItem(new Object[]{table.getItemIds().size()+1,
													true,
													(long)0,
													itemModel.getId(),
													itemModel.getItem_code(),
													itemModel.getName(),
													roundNumber(qty),
													roundNumber(conv_rat),
													roundNumber(conv_rat*qty),
													unitModel.getId(),
													unitModel.getSymbol(),
													roundNumber(amount),
													unitPriceField.getCurrency(),
													currency,
													roundNumber(unitPriceField.getConversionRate()),
													roundNumber(discount),
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
													(long)0, (long)0},table.getItemIds().size()+1);
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
							discountField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_DISCOUNT).getValue())+"");
							taxSelect.setNewValue((Long)item.getItemProperty(TBC_TAX_ID).getValue());
							netPriceField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_NET_PRICE).getValue())+"");
							convertionQuantityField.setNewValue(""+ roundNumber((Double)item.getItemProperty(TBC_CONVERTION_QTY).getValue()));
							manufacturingDateField.setNewValue((Date)item.getItemProperty(TBC_MANUFACTURING_DATE_ID).getValue());
							expiryDateField.setNewValue((Date)item.getItemProperty(TBC_EXPIRY_DATE_ID).getValue());
							gradeCombo.setNewValue((Long)item.getItemProperty(TBC_GRADE_ID).getValue());
							locationCombo.setNewValue((Long)item.getItemProperty(TBC_LOCATION_ID).getValue());
//							if(!(Boolean)item.getItemProperty(TBC_EDITABLE).getValue()){
								itemCombo.setReadOnly(true);
								quantityField.setReadOnly(true);
								unitSelect.setReadOnly(true);
//								setRequiredError(itemCombo, "Stock Blocked", true);
//							}
							manufacturingDateField.setReadOnly(true);
							expiryDateField.setReadOnly(true);
							gradeCombo.setReadOnly(true);
							taxSelect.setReadOnly(true);
							discountField.setReadOnly(true);
							barcodeField.setReadOnly(false);
							locationCombo.setReadOnly(true);
							addItemButton.setVisible(false);
							updateItemButton.setVisible(true);
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
								
								if(isDiscountEnable()){
									try {
										discount=toDouble(discountField.getValue().toString());
									} catch (Exception e) {
										discount=0;
									}
								}
								else
									discount=0;
								
								double netPrice=(unitPriceField.getValue()*qty)+taxAmount+cess-discount;
								
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
								item.getItemProperty(TBC_DISCOUNT).setValue(roundNumber(discount));
								item.getItemProperty(TBC_TAX_ID).setValue(taxId);
								item.getItemProperty(TBC_TAX_PERCENTAGE).setValue(roundNumber(taxPer));
								item.getItemProperty(TBC_TAX_AMOUNT).setValue(roundNumber(taxAmount));
								item.getItemProperty(TBC_CESS).setValue(roundNumber(cess));
								item.getItemProperty(TBC_NET_PRICE).setValue(roundNumber(netPrice));
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
							boolean savable=true;
							List<StockCreateDetailsModel> itemsList = new ArrayList<StockCreateDetailsModel>();
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								StockCreateDetailsModel det=new StockCreateDetailsModel();
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
								itemsList.add(det);
							}
							if(savable){
								StockCreateModel mdl=new StockCreateModel();
								mdl.setPurchase_number(getNextSequence("Stock Create Number", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setActive(true);
								mdl.setStatus((long)1);
								mdl.setComments(commentArea.getValue());
								mdl.setInventory_details_list(itemsList);
								long id=dao.save(mdl);
								loadOptions(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),"Stock Created. Purchase No : "+ mdl.getPurchase_number(),id);
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

			
			invoiceCombo.addValueChangeListener(new Property.ValueChangeListener() {

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
						responsilbeEmployeeCombo.setValue(null);
						commentArea.setValue("");
						locationMasterCombo.setValue((long)0);
						saveButton.setVisible(true);
						printButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						sendMailButton.setVisible(false);
						calculateTotals();
						resetItems();
						if(invoiceCombo.getValue()!=null && !invoiceCombo.getValue().toString().equals("0")){
							table.setVisibleColumns(allHeaders);
							StockCreateModel mdl=dao.getStockCreateModel((Long)invoiceCombo.getValue());
							referenceNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							netAmountField.setCurrencyDate(mdl.getDate());
							responsilbeEmployeeCombo.setValue(mdl.getResponsible_employee());
							commentArea.setValue(mdl.getComments());
							Iterator itr=mdl.getInventory_details_list().iterator();
							while (itr.hasNext()) {
								StockCreateDetailsModel det=(StockCreateDetailsModel)itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode();
								boolean editable=true;
								
								if(comDao.isStockBlocked(mdl.getId(),
														det.getId(),
														SConstants.stockPurchaseType.STOCK_CREATE))
									editable=false;
								
								table.addItem(new Object[]{table.getItemIds().size()+1,
														editable,
														det.getId(),
														det.getItem().getId(),
														det.getItem().getItem_code(),
														det.getItem().getName(),
														roundNumber(det.getQunatity()),
														roundNumber(det.getQty_in_basic_unit()/det.getQunatity()),
														roundNumber(det.getQty_in_basic_unit()),
														det.getUnit().getId(),
														det.getUnit().getSymbol(),
														roundNumber(det.getUnit_price()),
														det.getCurrencyId(),
														currency,
														roundNumber(det.getConversionRate()),
														roundNumber(det.getDiscount()),
														det.getTax().getId(),
														roundNumber(det.getTaxPercentage()),
														roundNumber(det.getTaxAmount()),
														roundNumber(det.getCessAmount()),
														roundNumber((det.getUnit_price()*det.getQunatity())+det.getTaxAmount()+det.getCessAmount()-det.getDiscount()),
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
														det.getStock_id()},table.getItemIds().size()+1);
							}
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
							netAmountField.setCurrency(getCurrencyID());
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
							if(invoiceCombo.getValue()!=null && !invoiceCombo.getValue().toString().equals("0")){
								
								List<StockCreateDetailsModel> itemsList = new ArrayList<StockCreateDetailsModel>();
								boolean savable=true;
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									StockCreateDetailsModel det=null;
									Item item = table.getItem(it.next());
									double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
									if(qty<=0){
										savable=false;
										break;
									}
									long id=(Long) item.getItemProperty(TBC_ID).getValue();
									if(id!=0)
										det=dao.getStockCreateDetailsModel(id);
									if(det==null)
										det=new StockCreateDetailsModel();
									
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
									itemsList.add(det);
								}
								if(savable){
									StockCreateModel mdl=dao.getStockCreateModel((Long)invoiceCombo.getValue());
									
									mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
									mdl.setRef_no(referenceNoField.getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									mdl.setComments(commentArea.getValue());
									mdl.setInventory_details_list(itemsList);
									dao.update(mdl);
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId());
									saveActivity(getOptionId(),"Stock Creation Updated. Purchase No : "+ mdl.getPurchase_number(),mdl.getId());
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
						if (invoiceCombo.getValue() != null && !invoiceCombo.getValue().toString().equals("0")) {
							Iterator itr=table.getItemIds().iterator();
							boolean blocked =false;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								if (comDao.isStockBlocked((Long) invoiceCombo.getValue(),
														(Long) item.getItemProperty(TBC_ID).getValue(),
														SConstants.stockPurchaseType.STOCK_CREATE)) {
									blocked=true;
									break;
								}
							}
							if(!blocked){
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												StockCreateModel mdl=dao.getStockCreateModel((Long)invoiceCombo.getValue());
												dao.delete(mdl);
												Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
												saveActivity(getOptionId()," Stock Creation Deleted. Purchase No : "+ 
														invoiceCombo.getItemCaption(invoiceCombo.getValue()),(Long)invoiceCombo.getValue());
												loadOptions(0);
											} catch (Exception e) {
												e.printStackTrace();
												SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
											}
										}
									}
								});
							}
							else{
								Notification.show("Cannot Delete, Stock Used", Type.ERROR_MESSAGE);
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
						if (invoiceCombo.getValue() != null && !invoiceCombo.getValue().toString().equals("0")) {
							Iterator itr=table.getItemIds().iterator();
							boolean blocked =false;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								if (comDao.isStockBlocked((Long) invoiceCombo.getValue(),
														(Long) item.getItemProperty(TBC_ID).getValue(),
														SConstants.stockPurchaseType.STOCK_CREATE)) {
									blocked=true;
									break;
								}
							}
							if(!blocked){
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												StockCreateModel mdl=dao.getStockCreateModel((Long)invoiceCombo.getValue());
												dao.cancel(mdl);
												Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
												saveActivity(getOptionId()," Stock Creation Cancelled. Purchase No : "
														+ invoiceCombo.getItemCaption(invoiceCombo.getValue()) ,(Long)invoiceCombo.getValue());
												loadOptions(0);
											} catch (Exception e) {
												e.printStackTrace();
												SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
											}
										}
									}
								});
							}
							else{
								Notification.show("Cannot Delete, Stock Used", Type.ERROR_MESSAGE);
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
					/*try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						if(invoiceCombo.getValue()!=null && !invoiceCombo.getValue().toString().equals("0")){
							StockCreateModel mdl=dao.getStockCreateModel((Long)invoiceCombo.getValue());
							String address = "";
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
								StockCreateDetailsModel det = (StockCreateDetailsModel) itr.next();
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
					}*/
				}
			});

			
			sendMailButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					/*try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						
						if(invoiceCombo.getValue()!=null && !invoiceCombo.getValue().toString().equals("0")){
							StockCreateModel mdl=dao.getStockCreateModel((Long)invoiceCombo.getValue());
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
							map.put("ADDRESS_LABEL", getPropertyName("address"));
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
								StockCreateDetailsModel det = (StockCreateDetailsModel) itr.next();
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
							
							Address[] mailTo=new Address[1];
							try {
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
					}*/
				}
			});

			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

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
		discountField.setReadOnly(false);
		barcodeField.setReadOnly(false);
		locationCombo.setReadOnly(false);
		
		itemCombo.setValue(null);
		quantityField.setValue("0");
		unitSelect.setValue(null);
		unitPriceField.setNewValue(getCurrencyID(),0.0);
		manufacturingDateField.setValue(getWorkingDate());
		expiryDateField.setValue(getWorkingDate());
		gradeCombo.setValue((long)0);
		try {
			taxSelect.setNewValue(new TaxDao().getDefaultTax(getOfficeID(), SConstants.tax.PURCHASE_TAX));
		} catch (Exception e) {
			e.printStackTrace();
		}
		discountField.setValue("0");
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
			double qty_ttl = 0, net_ttl = 0;
			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
				net_ttl += (((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue());
			}
			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));
			netAmountField.setNewValue(roundNumber(net_ttl));
			convertedField.setNewValue(roundNumber(net_ttl));
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
			
			if(settings.isDISCOUNT_ENABLE()){
				if (discountField.getValue() == null || discountField.getValue().equals("")) {
					discountField.setValue("0");
				} else {
					try {
						if (Double.parseDouble(discountField.getValue()) <0) {
							discountField.setNewValue("0");
						} 
					} catch (Exception e) {
						discountField.setNewValue("0");
					}
				}
			}
			else
				discountField.setNewValue("0");
			
			
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
			list.add(new StockCreateModel(0, "----Create New-----"));
			list.addAll(dao.getStockCreateModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			invoiceCombo.setContainerDataSource(bic);
			invoiceCombo.setItemCaptionPropertyId("comments");
			invoiceCombo.setValue(id);
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

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, "Select a Date", true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
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
		return invoiceCombo;
	}

	
	public void setPurchaseNumberList(SComboField invoiceCombo) {
		this.invoiceCombo = invoiceCombo;
	}
	
	
	@Override
	public SComboField getBillNoFiled() {
		return invoiceCombo;
	}
}
