/*package com.inventory.commissionsales.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.commissionsales.dao.CommissionPurchaseDao;
import com.inventory.commissionsales.model.CommissionPurchaseDetailsModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.GradeModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.stock.ui.StockRackMappingPannel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.SetUnitManagementUI;
import com.inventory.dao.PrivilageSetupDao;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.ui.SupplierPannel;
import com.inventory.reports.bean.ItemReportBean;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.bean.SalesInventoryDetailsPojo;
import com.inventory.sales.dao.SalesDao;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

*//**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 4, 2014
 *//*
public class CommissionPurchaseUI extends SparkLogic {

	private static final long serialVersionUID = -3150324634526894760L;
	
	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "TaxPerc";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_DISCOUNT = "DISCOUNT";
	static String TBC_PO_ID = "PO ID";
	static String TBC_INV_ID = "INV ID";
	static String TBC_BARCODE = "Barcode";
	static String TBC_GRADE_ID = "Grade Id";
	static String TBC_GRADE = "Grade";
	static String TBC_GROSS_WEIGHT = "Gross Weight";
	static String TBC_NET_WEIGHT = "Net Weight";
	static String TBC_NO_OF_CARTON = "No. of Cartons";
	static String TBC_CBM_VALUE = "CBM";
	static String TBC_TAG = "Tag";

	CommissionPurchaseDao daoObj;
	PurchaseDao purchDao;

	private SComboField purchaseNumberList;

	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SPopupView priceListPopup;
	SButton priceListButton;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	SComboField itemsCompo;
	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField netPriceTextField;
	STextField payingAmountTextField;

	STextField grossWeightTextField, netWeightTextField, noOfCartonsTextField,
			cbmValueTextField;
	STextField tagTextField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton savePurchaseButton;
	SButton updatePurchaseButton;
	SButton deletePurchaseButton;
	SButton cancelPurchaseButton;
	SButton printButton;

	SButton updateRackStock;

	SWindow rackStockWindow;

	ItemDao itemDao;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SComboField supplierSelect;

	STextField grandTotalAmtTextField;
	STextField shippingChargeTextField;
	STextField exciseDutyTextField;
	STextArea comment;

	SettingsValuePojo settings;

	WrappedSession session;

	private String[] allHeaders;
	private String[] requiredHeaders;

	boolean taxEnable = isTaxEnable();

	CommonMethodsDao comDao;

	SHorizontalLayout popupHor;

	private SButton newSupplierButton;
	private SButton newItemButton;

	private SButton newUnitButton;
	private SButton unitMapButton;

	SWindow popupWindow;

	private SButton loadAllSuppliersButton;

	UserManagementDao usrDao;
	SupplierDao supDao;
	TaxDao taxDao;
	UnitDao untDao;

	SButton newSaleButton;

	SButton barcodePrintButton;
	private STextField barcodeField;

//	private DocumentAttach attach;
	GradeDao gradeDao;
	SNativeSelect gradeComboField;

	SPopupView barcodePop;
	SFormLayout barcodeLay;
	STextField noOfBarcodeField;

	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	SDateField received_date, issue_date;
	STextField vesel, contr_no, consignment_mark, quantity, ss_cc, packages, quality, received_sound, supplierAmount;
	STextField damage,empty,shorte;
	SComboField statusCombo;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		priceListPopup = new SPopupView("", new SLabel(""));

		priceListButton = new SButton();

		comDao = new CommonMethodsDao();
		supDao = new SupplierDao();
		itemDao = new ItemDao();
		taxDao = new TaxDao();
		untDao = new UnitDao();
		usrDao = new UserManagementDao();
		gradeDao = new GradeDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Purchase");

		taxEnable = isTaxEnable();

		setSize(1260, 600);

		session = getHttpSession();

		daoObj = new CommissionPurchaseDao();
		purchDao = new PurchaseDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_PO_ID, TBC_INV_ID,  TBC_BARCODE, TBC_GRADE_ID, TBC_GRADE,
				TBC_GROSS_WEIGHT, TBC_NET_WEIGHT, TBC_NO_OF_CARTON,
				TBC_CBM_VALUE, TBC_TAG };

		if (taxEnable) {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_GROSS_WEIGHT,
					TBC_NET_WEIGHT, TBC_NO_OF_CARTON, TBC_CBM_VALUE,
					TBC_TAX_AMT, TBC_DISCOUNT, TBC_GRADE,
					TBC_TAG };
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_GROSS_WEIGHT,
					TBC_NET_WEIGHT, TBC_NO_OF_CARTON, TBC_CBM_VALUE,
					TBC_DISCOUNT, TBC_GRADE, TBC_TAG };
		}
		if (settings.isBARCODE_ENABLED()) {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_GROSS_WEIGHT,
					TBC_NET_WEIGHT, TBC_NO_OF_CARTON, TBC_CBM_VALUE,
					TBC_DISCOUNT, TBC_BARCODE, TBC_GRADE,
					TBC_TAG };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}
		if (!settings.isGRADING_ENABLED()) {
			templist.remove(TBC_GRADE);
		}

		if (!settings.isUSE_SALES_RATE_FROM_STOCK()) {
			templist.remove(TBC_TAG);
		}

		if (!settings.isUSE_GROSS_AND_NET_WEIGHT()) {
			templist.remove(TBC_GROSS_WEIGHT);
			templist.remove(TBC_NET_WEIGHT);
			templist.remove(TBC_NO_OF_CARTON);
			templist.remove(TBC_CBM_VALUE);
		}

		requiredHeaders = templist.toArray(new String[templist.size()]);

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(25);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(6);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(6);
		bottomGrid.setRows(4);

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
		form.setSizeFull();

		try {
			List list = new ArrayList();
			list.add(new CommissionPurchaseModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllActivePurchaseNos(getOfficeID()));
			purchaseNumberList = new SComboField(null, 170, list, "id",
					"comments", false, getPropertyName("create_new"));

			supplierSelect = new SComboField(
					null,
					170,
					supDao.getAllActiveSupplierNamesWithLedgerID(getOfficeID()),
					"id", "name", true, getPropertyName("select"));


			received_date= new SDateField(null, 100, getDateFormat(), getWorkingDate());
			issue_date= new SDateField(null, 100, getDateFormat(), getWorkingDate());
			vesel= new STextField();
			contr_no= new STextField();
			consignment_mark= new STextField(null,170);
			quantity= new STextField(null,"0");
			ss_cc= new STextField();
			packages= new STextField(null,170);
			quality= new STextField();
			received_sound= new STextField();
			damage= new STextField(null,170);
			empty= new STextField();
			shorte= new STextField();
			statusCombo=new SComboField(null, 170, Arrays.asList(new KeyValue((long)1,"Active"),new KeyValue((long)2,"Inactive")), "key","value");
			statusCombo.setInputPrompt(getPropertyName("select"));
			statusCombo.setValue((long)1);
			
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(purchaseNumberList);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("purchase_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("issued_date")), 3, 0);
			masterDetailsGrid.addComponent(issue_date, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("received_date")),
					6, 0);
			masterDetailsGrid.addComponent(received_date, 8, 0);


			newSupplierButton = new SButton();
			newSupplierButton.setStyleName("addNewBtnStyle");
			newSupplierButton.setDescription("Add new Supplier");

			loadAllSuppliersButton = new SButton();
			loadAllSuppliersButton.setStyleName("loadAllBtnStyle");
			loadAllSuppliersButton
					.setDescription("Load Suppliers in all offices.");
			loadAllSuppliersButton.setId("ALL");

			SHorizontalLayout hrl = new SHorizontalLayout();
			hrl.addComponent(supplierSelect);
			hrl.addComponent(newSupplierButton);
			hrl.addComponent(loadAllSuppliersButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("supplier")), 1, 1);
			masterDetailsGrid.addComponent(hrl, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("vesel_no")), 3, 1);
			masterDetailsGrid.addComponent(vesel, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("contr_no")), 6, 1);
			masterDetailsGrid.addComponent(contr_no, 8, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("consignment_mark")), 1, 2);
			masterDetailsGrid.addComponent(consignment_mark, 2, 2);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("quantity"), 40), 3, 2);
			masterDetailsGrid.addComponent(quantity, 4, 2);
			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ss/cc")), 6, 2);
			masterDetailsGrid.addComponent(ss_cc, 8, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("packages")), 1, 3);
			masterDetailsGrid.addComponent(packages, 2, 3);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("quality"), 40), 3, 3);
			masterDetailsGrid.addComponent(quality, 4, 3);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("received_sound")), 6, 3);
			masterDetailsGrid.addComponent(received_sound, 8, 3);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("damage")), 1, 4);
			masterDetailsGrid.addComponent(damage, 2, 4);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("empty"), 40), 3, 4);
			masterDetailsGrid.addComponent(empty, 4, 4);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("short")), 6, 4);
			masterDetailsGrid.addComponent(shorte, 8, 4);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("status")), 1, 5);
			masterDetailsGrid.addComponent(statusCombo, 2, 5);
			
				masterDetailsGrid.setSpacing(true);
				masterDetailsGrid.setStyleName("master_border");
				masterDetailsGrid.setColumnExpandRatio(1, 2);
				masterDetailsGrid.setColumnExpandRatio(2, 1.5f);
				masterDetailsGrid.setColumnExpandRatio(3, 1f);
				masterDetailsGrid.setColumnExpandRatio(4, 1.5f);
				masterDetailsGrid.setColumnExpandRatio(5, 1);
				masterDetailsGrid.setColumnExpandRatio(6, 2);


			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");

			newUnitButton = new SButton();
			newUnitButton.setStyleName("smallAddNewBtnStyle");
			newUnitButton.setDescription("Add new Unit");
			unitMapButton = new SButton();
			unitMapButton.setStyleName("mapBtnStyle");
			unitMapButton
					.setDescription("Set Convertion Quantity ( Unit Management )");

			newItemButton.setDescription("Add new Item");

			itemsCompo = new SComboField(
					getPropertyName("item"),
					220,
					itemDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
					"id", "name");

			GradeModel gradeModel = new GradeModel((long) 0, "None");
			List grdLis = new ArrayList();
			grdLis.add(0, gradeModel);
			grdLis.addAll(gradeDao.getAllGrades(getOfficeID()));
			gradeComboField = new SNativeSelect(getPropertyName("grade"), 80,
					grdLis, "id", "name");
			gradeComboField.setValue((long) 0);

			quantityTextField = new STextField(getPropertyName("qty"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setValue("0.00");
			unitPriceTextField.setStyleName("textfield_align_right");

			if (taxEnable) {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80,
						taxDao.getAllActiveTaxesFromType(getOfficeID(),
								SConstants.tax.PURCHASE_TAX), "id", "name");
				taxSelect.setVisible(true);
			} else {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80, null,
						"id", "name");
				taxSelect.setVisible(false);
			}

			discountTextField = new STextField(getPropertyName("discount"), 80,
					"0.0");
			discountTextField.setStyleName("textfield_align_right");
			netPriceTextField = new STextField(getPropertyName("net_price"),
					100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("textfield_align_right");

			grossWeightTextField = new STextField(
					getPropertyName("gross_weight"), 60, "0");
			grossWeightTextField.setStyleName("textfield_align_right");
			netWeightTextField = new STextField(getPropertyName("net_weight"),
					60, "0");
			netWeightTextField.setStyleName("textfield_align_right");
			noOfCartonsTextField = new STextField(
					getPropertyName("no_cartons"), 60, "0");
			noOfCartonsTextField.setStyleName("textfield_align_right");
			cbmValueTextField = new STextField(getPropertyName("cbm"), 60, "0");
			cbmValueTextField.setStyleName("textfield_align_right");

			tagTextField = new STextField(getPropertyName("tag"), 80, "");
			tagTextField.setMaxLength(100);

			payingAmountTextField = new STextField(null, 100);
			payingAmountTextField.setValue("0.00");
			payingAmountTextField.setStyleName("textfield_align_right");

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SHorizontalLayout buttonLay = new SHorizontalLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			SHorizontalLayout hrz1 = new SHorizontalLayout();
			hrz1.addComponent(itemsCompo);
			hrz1.addComponent(newItemButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);

			barcodeField = new STextField(getPropertyName("barcode"));

			addingGrid.addComponent(hrz1);

			if (settings.isGRADING_ENABLED()) {
				addingGrid.addComponent(gradeComboField);
				if (gradeComboField.getItemIds().iterator().hasNext())
					gradeComboField.setValue(gradeComboField.getItemIds()
							.iterator().next());
			}

			if (settings.isBARCODE_ENABLED()) {
				addingGrid.addComponent(barcodeField);
			}

			if (settings.isUSE_GROSS_AND_NET_WEIGHT()) {
				addingGrid.addComponent(grossWeightTextField);
				addingGrid.addComponent(netWeightTextField);
				addingGrid.addComponent(noOfCartonsTextField);
				addingGrid.addComponent(cbmValueTextField);
			}

			addingGrid.addComponent(quantityTextField);

			popupHor = new SHorizontalLayout();
			popupHor.addComponent(unitPriceTextField);
			popupHor.addComponent(priceListButton);
			popupHor.setComponentAlignment(priceListButton,
					Alignment.BOTTOM_LEFT);
//			addingGrid.addComponent(popupHor);

			SHorizontalLayout hrz2 = new SHorizontalLayout();
			hrz2.addComponent(unitSelect);
			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			vert.setSpacing(false);
			hrz2.addComponent(vert);
			hrz2.setComponentAlignment(vert, Alignment.MIDDLE_CENTER);
			hrz2.setSpacing(true);
			addingGrid.addComponent(hrz2);

			if (settings.isUSE_SALES_RATE_FROM_STOCK()) {
				addingGrid.addComponent(tagTextField);
			}

			priceListButton.setDescription("Price History");

			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(discountTextField);
//			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 2);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(9, 3);

			addingGrid.setWidth("1200");
			addingGrid.setHeight("60");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1200, 200);

			table.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,
					getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,
					getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null,
					TBC_TAX_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TAX_PERC, Double.class, null,
					getPropertyName("tax_perc"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_AMT, Double.class, null,
					getPropertyName("tax_amt"), null, Align.RIGHT);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null,
					getPropertyName("discount"), null, Align.CENTER);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null,
					getPropertyName("net_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PO_ID, Long.class, null, TBC_PO_ID,
					null, Align.CENTER);
			table.addContainerProperty(TBC_INV_ID, Long.class, null,
					TBC_INV_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_BARCODE, String.class, null,
					getPropertyName("barcode"), null, Align.RIGHT);
			table.addContainerProperty(TBC_GRADE_ID, Long.class, null,
					TBC_GRADE_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_GRADE, String.class, null,
					getPropertyName("grade"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAG, String.class, null,
					getPropertyName("tag"), null, Align.RIGHT);

			table.addContainerProperty(TBC_GROSS_WEIGHT, Double.class, null,
					getPropertyName("gross_weight"), null, Align.CENTER);
			table.addContainerProperty(TBC_NET_WEIGHT, Double.class, null,
					getPropertyName("net_weight"), null, Align.CENTER);
			table.addContainerProperty(TBC_NO_OF_CARTON, Double.class, null,
					getPropertyName("no_cartons"), null, Align.CENTER);
			table.addContainerProperty(TBC_CBM_VALUE, Double.class, null,
					getPropertyName("cbm"), null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, 1);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 4);
			table.setColumnExpandRatio(TBC_QTY, 2);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 2);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 3);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_DISCOUNT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1200");
			table.setHeight("200");

			grandTotalAmtTextField = new STextField(null, 200, "0.0");
//			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");
			comment = new STextArea(null, 400, 30);

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

//			attach = new DocumentAttach(SConstants.documentAttach.PURCHASE_BILL);

			// bottomGrid.addComponent(new SLabel(""), 1, 0);

			if (!isDiscountEnable()) {
				discountTextField.setVisible(false);
			}
			if (isExciceDutyEnable()) {
				bottomGrid.addComponent(new SLabel(
						getPropertyName("excise_duty")), 4, 2);
				bottomGrid.addComponent(exciseDutyTextField, 5, 2);
				bottomGrid.setComponentAlignment(exciseDutyTextField,
						Alignment.TOP_RIGHT);
			}
			if (isShippingChargeEnable()) {
				bottomGrid.addComponent(new SLabel(
						getPropertyName("shipping_charge")), 4, 0);
				bottomGrid.addComponent(shippingChargeTextField, 5, 0);
				bottomGrid.setComponentAlignment(shippingChargeTextField,
						Alignment.TOP_RIGHT);
			}

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					0);
			bottomGrid.addComponent(comment, 1, 0);

//			bottomGrid.addComponent(attach, 0, 3);
//			bottomGrid.setComponentAlignment(attach, Alignment.TOP_CENTER);

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),
					4, 3);
			bottomGrid.addComponent(grandTotalAmtTextField, 5, 3);

			bottomGrid.addComponent(
					new SLabel(getPropertyName("paying_amount")), 2, 3);
			bottomGrid.addComponent(payingAmountTextField, 3, 3);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			savePurchaseButton = new SButton(getPropertyName("save"), 70);
			savePurchaseButton.setStyleName("savebtnStyle");
			savePurchaseButton.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			updatePurchaseButton = new SButton(getPropertyName("update"), 80);
			updatePurchaseButton.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updatePurchaseButton.setStyleName("updatebtnStyle");

			deletePurchaseButton = new SButton(getPropertyName("delete"), 78);
			deletePurchaseButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deletePurchaseButton.setStyleName("deletebtnStyle");

			cancelPurchaseButton = new SButton(getPropertyName("cancel"), 78);
			cancelPurchaseButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			cancelPurchaseButton.setStyleName("deletebtnStyle");

			printButton = new SButton(getPropertyName("print"), 78);
			printButton.setIcon(new ThemeResource("icons/print.png"));
			boolean avail = true;
			boolean editAvail = true;
			if (!isSuperAdmin() && !isSystemAdmin()) {
				avail = new PrivilageSetupDao().isOptionsAvailToUser(
						getOfficeID(),
						SConstants.privilegeTypes.PRINT_PURCHASE, getLoginID());
				editAvail = new PrivilageSetupDao().isOptionsAvailToUser(
						getOfficeID(), SConstants.privilegeTypes.EDIT_PURCHASE,
						getLoginID());
			} else {
				avail = true;
				editAvail = true;
			}

			if (!avail)
				printButton.setEnabled(false);
			if (!editAvail)
				updatePurchaseButton.setEnabled(false);

			updateRackStock = new SButton(getPropertyName("arrange_stocks"),
					135);
			updateRackStock.setIcon(new ThemeResource(
					"icons/arrangestockSideIcon.png"));
			updateRackStock.setStyleName("stockbtnStyle");

			barcodePrintButton = new SButton(getPropertyName("print_barcode"));

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(savePurchaseButton);
			mainButtonLayout.addComponent(updatePurchaseButton);
			if (settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(cancelPurchaseButton);
			else
				mainButtonLayout.addComponent(deletePurchaseButton);

			mainButtonLayout.addComponent(updateRackStock);
//			mainButtonLayout.addComponent(printButton);

			updatePurchaseButton.setVisible(false);
			updateRackStock.setVisible(false);
			deletePurchaseButton.setVisible(false);
			cancelPurchaseButton.setVisible(false);
			printButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			noOfBarcodeField = new STextField(getPropertyName("no_barcode"),
					100);
			noOfBarcodeField.setValue("1");
			barcodeLay = new SFormLayout();
			barcodePop = new SPopupView(null, barcodeLay);
			barcodePop.setHideOnMouseOut(false);
			barcodeLay.addComponent(noOfBarcodeField);
			barcodeLay.addComponent(barcodePrintButton);
			masterDetailsGrid.addComponent(barcodePop, 5, 2);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);

			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			  
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			      
			pannel.setContent(windowNotif);

			supplierSelect.focus();

			rackStockWindow = new SWindow(getPropertyName("stock_arrangement"));

			getHttpSession().setAttribute("firsttime", "Y");

			rackStockWindow.center();

			priceListButton.setStyleName("showHistoryBtnStyle");

			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);

			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)purchaseNumberList.getValue(),confirmBox.getUserID());
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
						if(purchaseNumberList.getValue()!=null && !purchaseNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)purchaseNumberList.getValue(),
									"Purchase : No. "+purchaseNumberList.getItemCaption(purchaseNumberList.getValue()));
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
						if(purchaseNumberList.getValue()!=null && !purchaseNumberList.getValue().toString().equals("0")) {
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
			
			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					purchaseNumberList.setValue((long) 0);
				}
			});


			loadAllSuppliersButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					List list;
					try {

						if (loadAllSuppliersButton.getId().equals("ALL")) {
							list = supDao
									.getAllActiveSupplierNamesWithLedgerIDFromOrg(getOrganizationID());
							loadAllSuppliersButton.setId("CURRENT");
							loadAllSuppliersButton
									.setDescription("Load Suppliers under this office.");
						} else {
							list = supDao
									.getAllActiveSupplierNamesWithLedgerID(getOfficeID());
							loadAllSuppliersButton
									.setDescription("Load Suppliers in all offices.");
							loadAllSuppliersButton.setId("ALL");
						}

						SCollectionContainer bic = SCollectionContainer
								.setList(list, "id");
						supplierSelect.setContainerDataSource(bic);
						supplierSelect.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			newItemButton.addClickListener(new ClickListener() {
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

			newUnitButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					popupWindow.setContent(new AddNewUnitUI());

					popupWindow.setWidth("502");
					popupWindow.setHeight("455");
					popupWindow.center();
					popupWindow.setId("UNIT");
					popupWindow.setCaption("Add New Unit");
					getUI().getCurrent().addWindow(popupWindow);
				}
			});

			newSupplierButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					SupplierPannel pan = new SupplierPannel();
					popupWindow.setContent(pan);
					popupWindow.setId("SUPPLIER");
					popupWindow.setCaption("Add Supplier");
					popupWindow.center();
					getUI().getCurrent().addWindow(popupWindow);
				}
			});

			unitMapButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					long itemId = 0;

					if ((Long) itemsCompo.getValue() != null)
						itemId = (Long) itemsCompo.getValue();

					popupWindow.setContent(new SetUnitManagementUI(itemId, 0,
							false));

					popupWindow.setWidth("910");
					popupWindow.setHeight("498");
					popupWindow.center();
					popupWindow.setId("UNIT_MAP");
					popupWindow.setCaption("Unit Management");

					getUI().getCurrent().addWindow(popupWindow);

				}
			});

			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {

					if (popupWindow.getId().equals("ITEM")) {
						reloadItems();
						if (getHttpSession().getAttribute("saved_id") != null) {
							itemsCompo.setValue(getHttpSession().getAttribute(
									"saved_id"));
							getHttpSession().removeAttribute("saved_id");
						}
					} else if (popupWindow.getId().equals("UNIT_MAP")) {
						reloadUnit();
					} else if (popupWindow.getId().equals("SUPPLIER")) {
						reloadSuppliers();
						if (getHttpSession().getAttribute("saved_id") != null) {
							supplierSelect.setValue(getHttpSession()
									.getAttribute("saved_id"));
							getHttpSession().removeAttribute("saved_id");
						}
					}
				}
			});

			priceListButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub

					if (itemsCompo.getValue() != null
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

							table.setVisibleColumns(new String[] { "Date",
									"Customer Name", "Price" });

							List list = daoObj.getSalesRateHistory(
									(Long) itemsCompo.getValue(),
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

							purchTable.setVisibleColumns(new String[] { "Date",
									"Supplier Name", "Price" });

							list = daoObj.getPurchaseRateHistory(
									(Long) itemsCompo.getValue(),
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
									new SHorizontalLayout(true, purchTable,
											table));

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

			supplierSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								if (supplierSelect.getValue() != null) {
									SupplierModel supObj = supDao
											.getSupplierFromLedger((Long) supplierSelect
													.getValue());
									if (supObj != null) {
										supplierSelect
												.setDescription("<h1><i>Current Balance</i> : "
														+ supObj.getLedger()
																.getCurrent_balance()
														+ "</h1>");
									}
								} else
									supplierSelect.setDescription(null);

								if (supplierSelect.getValue() != null) {
									supplierSelect.setDescription("<h1><i>Current Balance</i> : "
											+ comDao.getLedgerCurrentBalance((Long) supplierSelect
													.getValue()) + "</h1>");
								} else
									supplierSelect.setDescription(null);

								if (session.getAttribute("PO_Select_Disabled") == null) {

									Object obj;
									Collection lsst = table.getItemIds();
									Iterator itr = lsst.iterator();
									while (itr.hasNext()) {
										obj = itr.next();
										Item itm = table.getItem(obj);
										if (((Long) itm.getItemProperty(
												TBC_PO_ID).getValue()) != 0) {
											table.removeItem(obj);
										}
									}


									}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});


			savePurchaseButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {


						if (isValid()) {

							long supplier_id = (Long) supplierSelect.getValue();

							CommissionPurchaseModel purObj = new CommissionPurchaseModel();

							List<CommissionPurchaseDetailsModel> itemsList = new ArrayList<CommissionPurchaseDetailsModel>();
							HashMap<CommissionPurchaseDetailsModel, String> map = new HashMap<CommissionPurchaseDetailsModel, String>();

							CommissionPurchaseDetailsModel invObj;
							Item item;
							double conv_rat;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new CommissionPurchaseDetailsModel();
								item = table.getItem(it.next());

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());

								if (taxEnable) {
									invObj.setTax(new TaxModel((Long) item
											.getItemProperty(TBC_TAX_ID)
											.getValue()));
									invObj.setTax_amount((Double) item
											.getItemProperty(TBC_TAX_AMT)
											.getValue());
									invObj.setTax_percentage((Double) item
											.getItemProperty(TBC_TAX_PERC)
											.getValue());
								} else {
									invObj.setTax(new TaxModel(1));
									invObj.setTax_amount(0);
									invObj.setTax_percentage(0);
								}

								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

								if (isDiscountEnable()) {
									invObj.setDiscount_amount((Double) item
											.getItemProperty(TBC_DISCOUNT)
											.getValue());
								}

								invObj.setOrder_id((Long) item.getItemProperty(
										TBC_PO_ID).getValue());


								invObj.setId((Long) item.getItemProperty(
										TBC_INV_ID).getValue());

								conv_rat = comDao.getConvertionRate(invObj
										.getItem().getId(), invObj.getUnit()
										.getId(), 0);
								invObj.setQty_in_basic_unit(conv_rat
										* invObj.getQunatity());

								invObj.setGradeId((Long) item.getItemProperty(
										TBC_GRADE_ID).getValue());

								invObj.setGross_weight((Double) item
										.getItemProperty(TBC_GROSS_WEIGHT)
										.getValue());
								invObj.setNet_weight((Double) item
										.getItemProperty(TBC_NET_WEIGHT)
										.getValue());
								invObj.setNo_of_cartons((Double) item
										.getItemProperty(TBC_NO_OF_CARTON)
										.getValue());
								invObj.setCbm_value((Double) item
										.getItemProperty(TBC_CBM_VALUE)
										.getValue());
								invObj.setItem_tag((String) item
										.getItemProperty(TBC_TAG).getValue());

								itemsList.add(invObj);


								map.put(invObj,
										item.getItemProperty(TBC_BARCODE)
												.getValue().toString());
							}

							purObj.setConsignment_mark(consignment_mark.getValue());
							purObj.setContr_no(contr_no.getValue());
							purObj.setDamage(damage.getValue());
							purObj.setEmpty(empty.getValue());
							purObj.setIssue_date(CommonUtil.getSQLDateFromUtilDate(issue_date.getValue()));
							purObj.setPackages(packages.getValue());
							purObj.setQuality(quality.getValue());
							purObj.setQuantity(quantity.getValue());
							purObj.setReceived_date(CommonUtil.getSQLDateFromUtilDate(received_date.getValue()));
							purObj.setReceived_sound(received_sound.getValue());
							purObj.setShorte(shorte.getValue());
							purObj.setSs_cc(ss_cc.getValue());
							purObj.setStatus(toLong(statusCombo.getValue().toString()));
							purObj.setVesel(vesel.getValue());
							purObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue()));
							purObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							purObj.setComments(comment.getValue());
							purObj.setLogin(getLoginID());
							purObj.setOffice(new S_OfficeModel(getOfficeID()));
							purObj.setSupplier(new LedgerModel(
									(Long) supplierSelect.getValue()));
							purObj.setCommission_purchase_list(itemsList);
							purObj.setNumber(getNextSequence(
									"Commission Purchase Number", getLoginID()));
							purObj.setActive(true);
							purObj.setShipping_charge(toDouble(shippingChargeTextField.getValue()));
							purObj.setExcise_duty(toDouble(exciseDutyTextField.getValue()));

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField
									.getValue());

						
							
							long purchaseAcc = settings.getPURCHASE_ACCOUNT();

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										settings.getCASH_ACCOUNT(),
										supplier_id, roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										supplier_id, purchaseAcc,
										roundNumber(netAmt));
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										supplier_id, purchaseAcc,
										roundNumber(netAmt));
							} else {
								trans.addTransaction(SConstants.CR,
										settings.getCASH_ACCOUNT(),
										supplier_id, roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										supplier_id, purchaseAcc,
										roundNumber(netAmt));
							}

							if (taxEnable) {
								if (settings.getPURCHASE_TAX_ACCOUNT() != 0) {
									amt = toDouble(table
											.getColumnFooter(TBC_TAX_AMT));
									if (amt != 0) {
										trans.addTransaction(
												SConstants.CR,
												settings.getCASH_ACCOUNT(),
												settings.getPURCHASE_TAX_ACCOUNT(),
												roundNumber(amt));
										totalAmt -= amt;
									}
								}
							}

							if (settings.getPURCHASE_EXCISE_DUTY_ACCOUNT() != 0) {
								amt = toDouble(exciseDutyTextField.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getPURCHASE_EXCISE_DUTY_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}

							}

							if (settings.getPURCHASE_SHIPPING_CHARGE_ACCOUNT() != 0) {
								amt = toDouble(shippingChargeTextField
										.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getPURCHASE_SHIPPING_CHARGE_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							if (settings.getPURCHASE_DESCOUNT_ACCOUNT() != 0) {
								amt = toDouble(shippingChargeTextField
										.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getPURCHASE_DESCOUNT_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							
							 * if (settings.getINVENTORY_ACCOUNT() != 0) {
							 * trans.addTransaction(SConstants.CR,
							 * settings.getCASH_ACCOUNT(),
							 * settings.getINVENTORY_ACCOUNT(),
							 * roundNumber(totalAmt)); }
							 

							long id = daoObj.save(
									purObj,
									trans.getTransaction(
											SConstants.COMMISSION_PURCHASE,
											CommonUtil
													.getSQLDateFromUtilDate(received_date
															.getValue())));
							saveActivity(
									getOptionId(),
									"New Commission Purchase Created. Bill No : "
											+ purObj.getNumber()
											+ ", Supplier : "
											+ supplierSelect
													.getItemCaption(supplierSelect
															.getValue())
											+ ", Amount : "
											+ purObj.getAmount(),purObj.getId());

							loadPurchase(id);

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
							// updateRackStock.click();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			purchaseNumberList
					.addValueChangeListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {
//								attach.clear();
								removeAllErrors();

								if (purchaseNumberList.getValue() != null
										&& !purchaseNumberList.getValue()
												.toString().equals("0")) {

									CommissionPurchaseModel purObj = daoObj
											.getPurchase((Long) purchaseNumberList
													.getValue());

									table.setVisibleColumns(allHeaders);

									table.removeAllItems();

									Iterator it = purObj
											.getCommission_purchase_list().iterator();
									CommissionPurchaseDetailsModel invObj;
									String barcode = "";
									GradeModel gradeModel = null;
									String gradeName = "";

									double netTotal;
									while (it.hasNext()) {
										invObj = (CommissionPurchaseDetailsModel) it
												.next();

										netTotal = (invObj.getUnit_price() * invObj
												.getQunatity())
												+ invObj.getTax_amount()
												- invObj.getDiscount_amount();

										barcode = purchDao.getBarcode(purObj
												.getId(), invObj.getItem()
												.getId(), invObj.getId());

										gradeModel = gradeDao.getGrade(invObj
												.getGradeId());

										gradeName = "None";
										if (gradeModel != null) {
											gradeName = gradeModel.getName();
										}

										table.addItem(
												new Object[] {
														table.getItemIds()
																.size() + 1,
														invObj.getItem()
																.getId(),
														invObj.getItem()
																.getItem_code(),
														invObj.getItem()
																.getName(),
														invObj.getQunatity(),
														invObj.getUnit()
																.getId(),
														invObj.getUnit()
																.getSymbol(),
														invObj.getUnit_price(),
														invObj.getTax().getId(),
														invObj.getTax_amount(),
														invObj.getTax_percentage(),
														invObj.getDiscount_amount(),
														netTotal,
														invObj.getOrder_id(),
														invObj.getId(),
														barcode,
														invObj.getGradeId(),
														gradeName,
														invObj.getGross_weight(),
														invObj.getNet_weight(),
														invObj.getNo_of_cartons(),
														invObj.getCbm_value(),
														invObj.getItem_tag() },
												table.getItemIds().size() + 1);

									}

									table.setVisibleColumns(requiredHeaders);

									grandTotalAmtTextField
											.setNewValue(asString(purObj
													.getAmount()));
									// buildingSelect.setValue(purObj.getBuilding().getId());
									comment.setValue(purObj.getComments());

									getHttpSession().setAttribute(
											"PO_Select_Disabled", 'Y');

									supplierSelect.setValue(purObj
											.getSupplier().getId());


									shippingChargeTextField
											.setValue(asString(purObj
													.getShipping_charge()));
									exciseDutyTextField
											.setValue(asString(purObj
													.getExcise_duty()));
									payingAmountTextField
											.setValue(asString(purObj
													.getPayment_amount()));
									
									consignment_mark.setValue(purObj.getConsignment_mark());
									contr_no.setValue(purObj.getContr_no());
									damage.setValue(purObj.getDamage());
									empty.setValue(purObj.getEmpty());
									issue_date.setValue(purObj.getIssue_date());
									packages.setValue(purObj.getPackages());
									quality.setValue(purObj.getQuality());
									quantity.setValue(purObj.getQuantity());
									received_date.setValue(purObj.getReceived_date());
									received_sound.setValue(purObj.getReceived_sound());
									shorte.setValue(purObj.getShorte());
									ss_cc.setValue(purObj.getSs_cc());
									vesel.setValue(purObj.getVesel());
									statusCombo.setValue(purObj.getStatus());
									
//									attach.loadDocument(
//											(Long) purchaseNumberList
//													.getValue(), getOfficeID(),
//											SConstants.PURCHASE);

//									isValid();
									payingAmountTextField
											.setComponentError(null);
									grandTotalAmtTextField
									.setComponentError(null);
									updatePurchaseButton.setVisible(true);
									updateRackStock.setVisible(true);
									deletePurchaseButton.setVisible(true);
									cancelPurchaseButton.setVisible(true);
									printButton.setVisible(true);
									savePurchaseButton.setVisible(false);

								} else {
									table.removeAllItems();

									consignment_mark.setValue("");
									contr_no.setValue("");
									damage.setValue("");
									empty.setValue("");
									issue_date.setValue(getWorkingDate());
									packages.setValue("");
									quality.setValue("");
									quantity.setValue("");
									received_date.setValue(getWorkingDate());
									received_sound.setValue("");
									shorte.setValue("");
									ss_cc.setValue("");
									vesel.setValue("");
									statusCombo.setValue((long)1);
									
									grandTotalAmtTextField.setNewValue("0.0");
									payingAmountTextField.setNewValue("0.0");
									// buildingSelect.setValue(null);
									comment.setValue("");
									supplierSelect.setValue(null);
									grandTotalAmtTextField
										.setComponentError(null);

									savePurchaseButton.setVisible(true);
									updatePurchaseButton.setVisible(false);
									updateRackStock.setVisible(false);
									deletePurchaseButton.setVisible(false);
									cancelPurchaseButton.setVisible(false);
									printButton.setVisible(false);

								}


								calculateTotals();

								itemsCompo.setValue(null);
								itemsCompo.focus();
								quantityTextField.setValue("0.0");
								unitPriceTextField.setValue("0.0");
								netPriceTextField.setNewValue("0.0");
								barcodeField.setValue("");

								supplierSelect.focus();

								if (!isFinYearBackEntry()) {
									savePurchaseButton.setVisible(false);
									updatePurchaseButton.setVisible(false);
									deletePurchaseButton.setVisible(false);
									cancelPurchaseButton.setVisible(false);
									printButton.setVisible(false);
									updateRackStock.setVisible(false);
									if (purchaseNumberList.getValue() == null
											|| purchaseNumberList.getValue()
													.toString().equals("0")) {
										Notification
												.show(getPropertyName("warning_transaction"),
														Type.WARNING_MESSAGE);
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});

			updatePurchaseButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							ConfirmDialog
									.show(getUI(),
											"It'll remove old arranged stocks under this purchase. Are you sure?",
											new ConfirmDialog.Listener() {
												public void onClose(
														ConfirmDialog dialog) {
													if (dialog.isConfirmed()) {
														try {

															long supplier_id = (Long) supplierSelect
																	.getValue();

															CommissionPurchaseModel purObj = daoObj
																	.getPurchase((Long) purchaseNumberList
																			.getValue());

															List<CommissionPurchaseDetailsModel> itemsList = new ArrayList<CommissionPurchaseDetailsModel>();

															Iterator it = table
																	.getItemIds()
																	.iterator();
															CommissionPurchaseDetailsModel invObj;
															Item item;
															double conv_rat;
															while (it.hasNext()) {
																invObj = new CommissionPurchaseDetailsModel();

																item = table
																		.getItem(it
																				.next());

																invObj.setItem(new ItemModel(
																		(Long) item
																				.getItemProperty(
																						TBC_ITEM_ID)
																				.getValue()));
																invObj.setQunatity((Double) item
																		.getItemProperty(
																				TBC_QTY)
																		.getValue());
																invObj.setBalance((Double) item
																		.getItemProperty(
																				TBC_QTY)
																		.getValue());

																if (taxEnable) {
																	invObj.setTax(new TaxModel(
																			(Long) item
																					.getItemProperty(
																							TBC_TAX_ID)
																					.getValue()));
																	invObj.setTax_amount((Double) item
																			.getItemProperty(
																					TBC_TAX_AMT)
																			.getValue());
																	invObj.setTax_percentage((Double) item
																			.getItemProperty(
																					TBC_TAX_PERC)
																			.getValue());
																} else {
																	invObj.setTax(new TaxModel(
																			1));
																	invObj.setTax_amount(0);
																	invObj.setTax_percentage(0);
																}

																invObj.setUnit(new UnitModel(
																		(Long) item
																				.getItemProperty(
																						TBC_UNIT_ID)
																				.getValue()));
																invObj.setUnit_price((Double) item
																		.getItemProperty(
																				TBC_UNIT_PRICE)
																		.getValue());

																if (isDiscountEnable()) {
																	invObj.setDiscount_amount((Double) item
																			.getItemProperty(
																					TBC_DISCOUNT)
																			.getValue());
																}

																invObj.setOrder_id((Long) item
																		.getItemProperty(
																				TBC_PO_ID)
																		.getValue());

																conv_rat = comDao
																		.getConvertionRate(
																				invObj.getItem()
																						.getId(),
																				invObj.getUnit()
																						.getId(),
																				0);
																invObj.setQty_in_basic_unit(conv_rat
																		* invObj.getQunatity());

																invObj.setGradeId((Long) item
																		.getItemProperty(
																				TBC_GRADE_ID)
																		.getValue());

																invObj.setGross_weight((Double) item
																		.getItemProperty(
																				TBC_GROSS_WEIGHT)
																		.getValue());
																invObj.setNet_weight((Double) item
																		.getItemProperty(
																				TBC_NET_WEIGHT)
																		.getValue());

																invObj.setNo_of_cartons((Double) item
																		.getItemProperty(
																				TBC_NO_OF_CARTON)
																		.getValue());
																invObj.setCbm_value((Double) item
																		.getItemProperty(
																				TBC_CBM_VALUE)
																		.getValue());
																invObj.setItem_tag((String) item
																		.getItemProperty(
																				TBC_TAG)
																		.getValue());

																invObj.setId((Long) item
																		.getItemProperty(
																				TBC_INV_ID)
																		.getValue());

																itemsList
																		.add(invObj);

															}

															purObj.setConsignment_mark(consignment_mark.getValue());
															purObj.setContr_no(contr_no.getValue());
															purObj.setDamage(damage.getValue());
															purObj.setEmpty(empty.getValue());
															purObj.setIssue_date(CommonUtil.getSQLDateFromUtilDate(issue_date.getValue()));
															purObj.setPackages(packages.getValue());
															purObj.setQuality(quality.getValue());
															purObj.setQuantity(quantity.getValue());
															purObj.setReceived_date(CommonUtil.getSQLDateFromUtilDate(received_date.getValue()));
															purObj.setReceived_sound(received_sound.getValue());
															purObj.setShorte(shorte.getValue());
															purObj.setSs_cc(ss_cc.getValue());
															purObj.setStatus(toLong(statusCombo.getValue().toString()));
															purObj.setVesel(vesel.getValue());
															purObj.setPayment_amount(toDouble(payingAmountTextField
																	.getValue()));
															purObj.setAmount(toDouble(grandTotalAmtTextField
																	.getValue()));
															purObj.setComments(comment.getValue());
															purObj.setLogin(getLoginID());
															purObj.setSupplier(new LedgerModel(
																	(Long) supplierSelect.getValue()));
															purObj.setCommission_purchase_list(itemsList);
															purObj.setActive(true);
															purObj.setShipping_charge(toDouble(shippingChargeTextField.getValue()));
															purObj.setExcise_duty(toDouble(exciseDutyTextField.getValue()));

															FinTransaction trans = new FinTransaction();
															double totalAmt = toDouble(grandTotalAmtTextField
																	.getValue());
															double netAmt = totalAmt;

															double amt = 0;

															double payingAmt = toDouble(payingAmountTextField
																	.getValue());

															long purchaseAcc = settings
																	.getPURCHASE_ACCOUNT();

															if (payingAmt == netAmt) {
																trans.addTransaction(
																		SConstants.CR,
																		settings.getCASH_ACCOUNT(),
																		supplier_id,
																		roundNumber(payingAmt));
																trans.addTransaction(
																		SConstants.CR,
																		supplier_id,
																		purchaseAcc,
																		roundNumber(netAmt));
															} else if (payingAmt == 0) {
																trans.addTransaction(
																		SConstants.CR,
																		supplier_id,
																		purchaseAcc,
																		roundNumber(netAmt));
															} else {
																trans.addTransaction(
																		SConstants.CR,
																		settings.getCASH_ACCOUNT(),
																		supplier_id,
																		roundNumber(payingAmt));
																trans.addTransaction(
																		SConstants.CR,
																		supplier_id,
																		purchaseAcc,
																		roundNumber(netAmt));
															}

															if (taxEnable) {
																if (settings
																		.getPURCHASE_TAX_ACCOUNT() != 0) {
																	amt = toDouble(table
																			.getColumnFooter(TBC_TAX_AMT));
																	if (amt != 0) {
																		trans.addTransaction(
																				SConstants.CR,
																				settings.getCASH_ACCOUNT(),
																				settings.getPURCHASE_TAX_ACCOUNT(),
																				roundNumber(amt));
																		totalAmt -= amt;
																	}
																}
															}

															if (settings
																	.getPURCHASE_EXCISE_DUTY_ACCOUNT() != 0) {
																amt = toDouble(exciseDutyTextField
																		.getValue());
																if (amt != 0) {
																	trans.addTransaction(
																			SConstants.CR,
																			settings.getCASH_ACCOUNT(),
																			settings.getPURCHASE_EXCISE_DUTY_ACCOUNT(),
																			roundNumber(amt));
																	totalAmt -= amt;
																}

															}

															if (settings
																	.getPURCHASE_SHIPPING_CHARGE_ACCOUNT() != 0) {
																amt = toDouble(shippingChargeTextField
																		.getValue());
																if (amt != 0) {
																	trans.addTransaction(
																			SConstants.CR,
																			settings.getCASH_ACCOUNT(),
																			settings.getPURCHASE_SHIPPING_CHARGE_ACCOUNT(),
																			roundNumber(amt));
																	totalAmt -= amt;
																}
															}

															if (settings
																	.getPURCHASE_DESCOUNT_ACCOUNT() != 0) {
																amt = toDouble(shippingChargeTextField
																		.getValue());
																if (amt != 0) {
																	trans.addTransaction(
																			SConstants.CR,
																			settings.getCASH_ACCOUNT(),
																			settings.getPURCHASE_DESCOUNT_ACCOUNT(),
																			roundNumber(amt));
																	totalAmt -= amt;
																}
															}

															
															 * if (settings
															 * .getINVENTORY_ACCOUNT
															 * () != 0) {
															 * trans.addTransaction
															 * ( SConstants.CR,
															 * settings
															 * .getCASH_ACCOUNT
															 * (), settings.
															 * getINVENTORY_ACCOUNT
															 * (),
															 * roundNumber(totalAmt
															 * )); }
															 

															TransactionModel tran = new SalesDao()
																	.getTransaction(purObj
																			.getTransaction_id());
															tran.setTransaction_details_list(trans
																	.getChildList());
															tran.setDate(purObj
																	.getReceived_date());
															tran.setLogin_id(getLoginID());

															daoObj.update(
																	purObj,
																	tran);

//															attach.saveDocument(
//																	purObj.getId(),
//																	getOfficeID(),
//																	SConstants.PURCHASE);

															saveActivity(
																	getOptionId(),
																	"Commission Purchase Updated. Bill No : "
																			+ purObj.getNumber()
																			+ ", Supplier : "
																			+ supplierSelect
																					.getItemCaption(supplierSelect
																							.getValue())
																			+ ", Amount : "
																			+ purObj.getAmount(),purObj.getId());

															loadPurchase(purObj
																	.getId());

															Notification
																	.show(getPropertyName("update_success"),
																			Type.WARNING_MESSAGE);
															// updateRackStock
															// .click();

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

			updateRackStock.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						rackStockWindow.setContent(new StockRackMappingPannel(
								(Long) purchaseNumberList.getValue()));
						getUI().getCurrent().addWindow(rackStockWindow);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			deletePurchaseButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (purchaseNumberList.getValue() != null
							&& !purchaseNumberList.getValue().toString()
									.equals("0")) {
						boolean blocked = false;
						Collection selectedItems = table.getItemIds();
						Iterator it1 = selectedItems.iterator();
						while (it1.hasNext()) {
							Item item = table.getItem(it1.next());
							try {
								if (comDao.isStockBlocked(
										(Long) purchaseNumberList.getValue(),
										(Long) item.getItemProperty(TBC_INV_ID)
												.getValue())) {
									blocked = true;
									break;
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (!blocked) {
							ConfirmDialog.show(getUI(),
									getPropertyName("are_you_sure"),
									new ConfirmDialog.Listener() {
										public void onClose(ConfirmDialog dialog) {
											if (dialog.isConfirmed()) {
												try {
													daoObj.delete((Long) purchaseNumberList
															.getValue());

//													attach.deleteDocument(
//															toLong(purchaseNumberList
//																	.getValue()
//																	.toString()),
//															getOfficeID(),
//															SConstants.PURCHASE);

													saveActivity(
															getOptionId(),
															"Purchase Deleted. Bill No : "
																	+ purchaseNumberList
																			.getItemCaption(purchaseNumberList
																					.getValue())
																	+ ", Customer : "
																	+ supplierSelect
																			.getItemCaption(supplierSelect
																					.getValue()),(Long)purchaseNumberList
																					.getValue());

													Notification
															.show(getPropertyName("deleted_success"),
																	Type.WARNING_MESSAGE);
													loadPurchase(0);

												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
									});

						} else {
							Notification.show(getPropertyName("cannot_delete"),
									Type.ERROR_MESSAGE);
						}
					}

				}
			});

			cancelPurchaseButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (purchaseNumberList.getValue() != null
							&& !purchaseNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.cancel((Long) purchaseNumberList
														.getValue());

//												attach.deleteDocument(
//														toLong(purchaseNumberList
//																.getValue()
//																.toString()),
//														getOfficeID(),
//														SConstants.PURCHASE);

												saveActivity(
														getOptionId(),
														"Purchase Deleted. Bill No : "
																+ purchaseNumberList
																		.getItemCaption(purchaseNumberList
																				.getValue())
																+ ", Customer : "
																+ supplierSelect
																		.getItemCaption(supplierSelect
																				.getValue()),(Long)purchaseNumberList
																				.getValue());

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);
												loadPurchase(0);

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}

				}
			});

			table.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

					itemsCompo.setReadOnly(false);
					unitSelect.setReadOnly(false);

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						if (settings.isBARCODE_ENABLED()) {
							barcodePop.setPopupVisible(true);
							noOfBarcodeField.setComponentError(null);
							noOfBarcodeField.setValue("1");
						}
						Item item = table.getItem(selectedItems.iterator()
								.next());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

						itemsCompo.setValue(item.getItemProperty(TBC_ITEM_ID)
								.getValue());
						gradeComboField.setValue((Long) item.getItemProperty(
								TBC_GRADE_ID).getValue());
						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());
						unitPriceTextField.setValue(""
								+ item.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

						if (taxEnable) {
							taxSelect.setValue(item.getItemProperty(TBC_TAX_ID)
									.getValue());
						}

						discountTextField
								.setValue(""
										+ item.getItemProperty(TBC_DISCOUNT)
												.getValue());

						netPriceTextField.setNewValue(""
								+ item.getItemProperty(TBC_NET_PRICE)
										.getValue());

						barcodeField.setValue(asString(item.getItemProperty(
								TBC_BARCODE).getValue()));
						tagTextField.setValue(asString(item.getItemProperty(
								TBC_TAG).getValue()));

						grossWeightTextField.setValue(asString(item
								.getItemProperty(TBC_GROSS_WEIGHT).getValue()));
						netWeightTextField.setValue(asString(item
								.getItemProperty(TBC_NET_WEIGHT).getValue()));

						noOfCartonsTextField.setValue(asString(item
								.getItemProperty(TBC_NO_OF_CARTON).getValue()));
						cbmValueTextField.setValue(asString(item
								.getItemProperty(TBC_CBM_VALUE).getValue()));
						tagTextField.setValue(asString(item.getItemProperty(
								TBC_TAG).getValue()));

						visibleAddupdatePurchaseButton(false, true);

						itemsCompo.focus();

						if ((Long) item.getItemProperty(TBC_PO_ID).getValue() > 0) {
							itemsCompo.setReadOnly(true);
							unitSelect.setReadOnly(true);
							quantityTextField.focus();
						}

					} else {

						itemsCompo.setValue(null);
						itemsCompo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");
						barcodeField.setValue("");
						gradeComboField.setValue((long) 0);
						tagTextField.setValue("");

						grossWeightTextField.setValue("0.0");
						netWeightTextField.setValue("0.0");
						noOfCartonsTextField.setValue("0.0");
						cbmValueTextField.setValue("0.0");

						visibleAddupdatePurchaseButton(true, false);

						itemsCompo.focus();
					}

				}

			});

			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (table.getComponentError() != null)
							setRequiredError(table, null, false);

						if (isAddingValid()) {

							double price = 0, qty = 0, totalAmt = 0, discount_amt = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discount_amt = toDouble(discountTextField
									.getValue());

							netPriceTextField
									.setNewValue(asString(price * qty));

							table.setVisibleColumns(allHeaders);

							ItemModel itm = itemDao.getItem((Long) itemsCompo
									.getValue());
							UnitModel objUnit = untDao
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = price * qty * objTax.getValue()
											/ 100;
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							totalAmt = (price * qty + tax_amt) - discount_amt;

							int id = 0, ct = 0;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								id = (Integer) it.next();
							}
							id++;

							table.addItem(
									new Object[] {
											table.getItemIds().size() + 1,
											itm.getId(),
											itm.getItem_code(),
											itm.getName(),
											qty,
											objUnit.getId(),
											objUnit.getSymbol(),
											toDouble(unitPriceTextField
													.getValue()),
											objTax.getId(),
											tax_amt,
											tax_perc,
											discount_amt,
											totalAmt,
											(long) 0,
											(long) 0,
											barcodeField.getValue(),
											(Long) gradeComboField.getValue(),
											gradeComboField
													.getItemCaption(gradeComboField
															.getValue()),
											toDouble(grossWeightTextField
													.getValue()),
											toDouble(netWeightTextField
													.getValue()),
											toDouble(noOfCartonsTextField
													.getValue()),
											toDouble(cbmValueTextField
													.getValue()),
											tagTextField.getValue() }, id);

							table.setVisibleColumns(requiredHeaders);

							itemsCompo.setValue(null);
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");
							barcodeField.setValue("");
							tagTextField.setValue("");
							gradeComboField.setValue((long) 0);

							grossWeightTextField.setValue("0.0");
							netWeightTextField.setValue("0.0");
							noOfCartonsTextField.setValue("0.0");
							cbmValueTextField.setValue("0.0");

							calculateTotals();

							// payingAmountTextField
							// .setValue(grandTotalAmtTextField.getValue());

							itemsCompo.focus();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {

							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());

							double price = 0, qty = 0, totalAmt = 0, discount_amt = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discount_amt = toDouble(discountTextField
									.getValue());

							netPriceTextField.setNewValue(asString(price * qty
									- discount_amt));

							ItemModel itm = itemDao.getItem((Long) itemsCompo
									.getValue());
							UnitModel objUnit = untDao
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = price * qty * objTax.getValue()
											/ 100;
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							totalAmt = price * qty + tax_amt - discount_amt;

							item.getItemProperty(TBC_ITEM_ID).setValue(
									itm.getId());
							item.getItemProperty(TBC_ITEM_CODE).setValue(
									itm.getItem_code());
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									itm.getName());
							item.getItemProperty(TBC_GRADE_ID).setValue(
									(Long) gradeComboField.getValue());
							item.getItemProperty(TBC_GRADE).setValue(
									gradeComboField
											.getItemCaption(gradeComboField
													.getValue()));
							item.getItemProperty(TBC_QTY).setValue(qty);
							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());
							item.getItemProperty(TBC_UNIT_PRICE).setValue(
									toDouble(unitPriceTextField.getValue()));
							item.getItemProperty(TBC_TAX_ID).setValue(
									objTax.getId());
							item.getItemProperty(TBC_TAX_AMT).setValue(tax_amt);
							item.getItemProperty(TBC_TAX_PERC).setValue(
									tax_perc);
							item.getItemProperty(TBC_NET_PRICE).setValue(
									totalAmt);
							item.getItemProperty(TBC_DISCOUNT).setValue(
									discount_amt);
							item.getItemProperty(TBC_BARCODE).setValue(
									barcodeField.getValue());
							item.getItemProperty(TBC_TAG).setValue(
									tagTextField.getValue());

							item.getItemProperty(TBC_GROSS_WEIGHT).setValue(
									toDouble(grossWeightTextField.getValue()));
							item.getItemProperty(TBC_NET_WEIGHT).setValue(
									toDouble(netWeightTextField.getValue()));
							item.getItemProperty(TBC_NO_OF_CARTON).setValue(
									toDouble(noOfCartonsTextField.getValue()));
							item.getItemProperty(TBC_CBM_VALUE).setValue(
									toDouble(cbmValueTextField.getValue()));

							table.setVisibleColumns(requiredHeaders);

							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");
							barcodeField.setValue("");
							tagTextField.setValue("");

							grossWeightTextField.setValue("0.0");
							netWeightTextField.setValue("0.0");
							noOfCartonsTextField.setValue("0.0");
							cbmValueTextField.setValue("0.0");

							visibleAddupdatePurchaseButton(true, false);

							itemsCompo.focus();
							table.setValue(null);

							calculateTotals();

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			itemsCompo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {
							try {
								if (itemsCompo.getValue() != null) {

									barcodeField.setComponentError(null);
									itemsCompo.setComponentError(null);

									ItemModel itm = itemDao
											.getItem((Long) itemsCompo
													.getValue());

									SCollectionContainer bic = SCollectionContainer
											.setList(comDao
													.getAllItemUnitDetails(itm
															.getId()), "id");
									unitSelect.setContainerDataSource(bic);
									unitSelect
											.setItemCaptionPropertyId("symbol");

									if (taxEnable) {
										taxSelect.setValue(itm.getPurchaseTax()
												.getId());
									}
									unitSelect.setValue(itm.getUnit().getId());

									quantityTextField.selectAll();
									quantityTextField.focus();
									gradeComboField.setValue((long) 0);

									if (settings.isBARCODE_ENABLED()) {
										// barcodeField.focus();
										barcodeField.setValue(purchDao
												.getBarcodeFromStock(itm
														.getId()));
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});

			unitSelect.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							// ItemModel itm = itemDao.getItem((Long)
							// itemsCompo.getValue());
							//
							// if(itm.getUnit().getId()!=((Long)unitSelect.getValue()))
							// {

							unitPriceTextField.setValue(asString(comDao
									.getItemPrice((Long) itemsCompo.getValue(),
											(Long) unitSelect.getValue(), 0)));

							// }
							// else {
							// unitPriceTextField.setValue(asString(itm.get
							// }
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

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

			table.addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadPurchase(0);
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
					// if(deleteItemButton.isVisible())
					// deleteItemButton.click();
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			quantityTextField.setImmediate(true);
			quantityTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (!isFinYearBackEntry()) {
				savePurchaseButton.setVisible(false);
				updatePurchaseButton.setVisible(false);
				deletePurchaseButton.setVisible(false);
				cancelPurchaseButton.setVisible(false);
				printButton.setVisible(false);
				updateRackStock.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			printButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					List<Object> reportList = new ArrayList<Object>();
					SalesPrintBean bean = null;
					NumberToWords numberToWords = new NumberToWords();
					double total = 0;
					try {

						CommissionPurchaseModel purchObj = daoObj
								.getPurchase((Long) purchaseNumberList
										.getValue());

						SupplierModel customerModel = supDao
								.getSupplierFromLedger(purchObj.getSupplier()
										.getId());
						String address = "";
						if (customerModel != null) {
							address = new AddressDao()
									.getAddressString(customerModel.getLedger()
											.getAddress().getId());
						}

						map.put("CUSTOMER_NAME", customerModel.getName());
						map.put("CUSTOMER_ADDRESS", address);
						map.put("SALES_BILL_NO", toLong(purchaseNumberList
								.getItemCaption(purchaseNumberList.getValue())));
						map.put("BILL_DATE", CommonUtil
								.formatDateToDDMMMYYYY(issue_date.getValue()));
						map.put("SALES_MAN", "");



						map.put("SALES_TYPE", "Purchase Invoice");
						map.put("OFFICE_NAME", customerModel.getLedger()
								.getOffice().getName());

						map.put("PURCH_BILL_DATE", purchObj.getReceived_date());

						CommissionPurchaseDetailsModel invObj;
						Iterator<CommissionPurchaseDetailsModel> itr1 = purchObj
								.getCommission_purchase_list().iterator();
						while (itr1.hasNext()) {
							invObj = itr1.next();

							bean = new SalesPrintBean(invObj.getItem()
									.getName(), invObj.getQunatity(), invObj
									.getUnit_price(),
									(invObj.getQunatity() * invObj
											.getUnit_price())
											- invObj.getDiscount_amount()
											+ invObj.getTax_amount(), invObj
											.getUnit().getSymbol(), invObj
											.getItem().getItem_code(), invObj
											.getQunatity(), invObj
											.getGross_weight(), invObj
											.getNet_weight(), invObj
											.getNo_of_cartons(), invObj
											.getCbm_value());

							total += bean.getTotal();

							reportList.add(bean);
						}

						S_OfficeModel officeModel = new OfficeDao()
								.getOffice(getOfficeID());
						map.put("AMOUNT_IN_WORDS", numberToWords.convertNumber(
								roundNumber(total) + "", officeModel
										.getCurrency().getInteger_part(),
								officeModel.getCurrency().getFractional_part()));

						Report report = new Report(getLoginID());
						report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE));
						// report.setJrxmlFileName("FlowerPurchase_Print");

						String basePath = VaadinServlet.getCurrent()
								.getServletContext().getRealPath("/")
								+ "VAADIN/themes/testappstheme/OrganizationLogos/";

						File file = new File(basePath + getOrganizationID()
								+ ".png");
						if (file == null || !file.exists())
							map.put("LOGO_PATH", basePath + "BaseLogo.png");
						else
							map.put("LOGO_PATH", basePath + getOrganizationID()
									+ ".png");

						// report.setJrxmlFileName("Purchase_A4_Print");
						report.setReportFileName("Purchase Print");
						// report.setReportTitle("Sales Invoice");
						// report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						report.createReport(reportList, map);

						report.print();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			barcodePrintButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					List<Object> reportList = new ArrayList<Object>();

					Report report = new Report(getLoginID());

					ItemReportBean bean = null;

					if (noOfBarcodeField.getValue() != null
							&& toInt(noOfBarcodeField.getValue().toString()) > 0) {
						try {
							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());
							if (item != null
									&& toLong(item.getItemProperty(TBC_INV_ID)
											.getValue().toString()) != 0) {
								ItemStockModel stk = purchDao
										.getStockFromInvDetailsId(toLong(item
												.getItemProperty(TBC_INV_ID)
												.getValue().toString()));

								for (int i = 0; i < toInt(noOfBarcodeField
										.getValue().toString()); i++) {
									bean = new ItemReportBean();
									bean.setCode(stk.getBarcode());
									bean.setCurrency(stk.getItem().getOffice()
											.getCurrency().getSymbol());
									bean.setName(stk.getItem().getName());
									bean.setRate(comDao.getItemPrice(stk
											.getItem().getId(), toLong(item
											.getItemProperty(TBC_UNIT_ID)
											.getValue().toString()), -1));
									reportList.add(bean);
								}

								report.setJrxmlFileName("PurchaseBarcodeWithPrice");
								report.setReportFileName("PurchaseBarcode");
								report.setReportTitle("");
								report.setIncludeHeader(false);
								report.setReportType(Report.PDF);
								report.createReport(reportList, null);

								report.print();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						setRequiredError(noOfBarcodeField,
								getPropertyName("invalid_data"), true);
					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return pannel;
	}

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc;

		try {
			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
			disc = Double.parseDouble(discountTextField.getValue());
		} catch (Exception e) {
			// TODO: handle exception
		}
		netPriceTextField.setNewValue(asString(unitPrc * qty));
	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0, disc_ttl = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();

				if (taxEnable) {
					tax_ttl += (Double) item.getItemProperty(TBC_TAX_AMT)
							.getValue();
				}

				net_ttl += (Double) item.getItemProperty(TBC_NET_PRICE)
						.getValue();
				disc_ttl += (Double) item.getItemProperty(TBC_DISCOUNT)
						.getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_TAX_AMT, asString(roundNumber(tax_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));
			table.setColumnFooter(TBC_DISCOUNT, asString(roundNumber(disc_ttl)));

			double ship_charg = 0, excise_duty = 0;
			try {
				ship_charg = toDouble(shippingChargeTextField.getValue());
				excise_duty = toDouble(exciseDutyTextField.getValue());
			} catch (Exception e) {
				// TODO: handle exception
			}

//			grandTotalAmtTextField.setNewValue(asString(roundNumber(net_ttl
//					+ ship_charg + excise_duty)));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			barcodeField.setComponentError(null);

			if (discountTextField.getValue() == null
					|| discountTextField.getValue().equals("")) {
				setRequiredError(discountTextField,
						getPropertyName("invalid_data"), true);
				discountTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(discountTextField.getValue()) < 0) {
						setRequiredError(discountTextField,
								getPropertyName("invalid_data"), true);
						discountTextField.focus();
						ret = false;
					} else
						setRequiredError(discountTextField, null, false);
				} catch (Exception e) {
					setRequiredError(discountTextField,
							getPropertyName("invalid_data"), true);
					discountTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (taxEnable) {
				if (taxSelect.getValue() == null
						|| taxSelect.getValue().equals("")) {
					setRequiredError(taxSelect,
							getPropertyName("invalid_selection"), true);
					taxSelect.focus();
					ret = false;
				} else
					setRequiredError(taxSelect, null, false);
			}

			if (unitPriceTextField.getValue() == null
					|| unitPriceTextField.getValue().equals("")) {
				setRequiredError(unitPriceTextField,
						getPropertyName("invalid_data"), true);
				unitPriceTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,
								getPropertyName("invalid_data"), true);
						unitPriceTextField.focus();
						ret = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField,
							getPropertyName("invalid_data"), true);
					unitPriceTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("invalid_data"), true);
						quantityTextField.focus();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (settings.isUSE_GROSS_AND_NET_WEIGHT()) {
				if (grossWeightTextField.getValue() == null
						|| grossWeightTextField.getValue().equals("")) {
					setRequiredError(grossWeightTextField,
							getPropertyName("invalid_data"), true);
					grossWeightTextField.focus();
					ret = false;
				} else {
					try {
						if (toDouble(grossWeightTextField.getValue()) < 0) {
							setRequiredError(grossWeightTextField,
									getPropertyName("invalid_data"), true);
							grossWeightTextField.focus();
							ret = false;
						} else
							setRequiredError(grossWeightTextField, null, false);
					} catch (Exception e) {
						setRequiredError(grossWeightTextField,
								getPropertyName("invalid_data"), true);
						grossWeightTextField.focus();
						ret = false;
						// TODO: handle exception
					}
				}

				if (netWeightTextField.getValue() == null
						|| netWeightTextField.getValue().equals("")) {
					setRequiredError(netWeightTextField,
							getPropertyName("invalid_data"), true);
					netWeightTextField.focus();
					ret = false;
				} else {
					try {
						if (toDouble(netWeightTextField.getValue()) < 0) {
							setRequiredError(netWeightTextField,
									getPropertyName("invalid_data"), true);
							netWeightTextField.focus();
							ret = false;
						} else
							setRequiredError(netWeightTextField, null, false);
					} catch (Exception e) {
						setRequiredError(netWeightTextField,
								getPropertyName("invalid_data"), true);
						netWeightTextField.focus();
						ret = false;
						// TODO: handle exception
					}
				}

				if (noOfCartonsTextField.getValue() == null
						|| noOfCartonsTextField.getValue().equals("")) {
					setRequiredError(noOfCartonsTextField,
							getPropertyName("invalid_data"), true);
					noOfCartonsTextField.focus();
					ret = false;
				} else {
					try {
						if (toDouble(noOfCartonsTextField.getValue()) < 0) {
							setRequiredError(noOfCartonsTextField,
									getPropertyName("invalid_data"), true);
							noOfCartonsTextField.focus();
							ret = false;
						} else
							setRequiredError(noOfCartonsTextField, null, false);
					} catch (Exception e) {
						setRequiredError(noOfCartonsTextField,
								getPropertyName("invalid_data"), true);
						noOfCartonsTextField.focus();
						ret = false;
						// TODO: handle exception
					}
				}

				if (cbmValueTextField.getValue() == null
						|| cbmValueTextField.getValue().equals("")) {
					setRequiredError(cbmValueTextField,
							getPropertyName("invalid_data"), true);
					cbmValueTextField.focus();
					ret = false;
				} else {
					try {
						if (toDouble(cbmValueTextField.getValue()) < 0) {
							setRequiredError(cbmValueTextField,
									getPropertyName("invalid_data"), true);
							cbmValueTextField.focus();
							ret = false;
						} else
							setRequiredError(cbmValueTextField, null, false);
					} catch (Exception e) {
						setRequiredError(cbmValueTextField,
								getPropertyName("invalid_data"), true);
						cbmValueTextField.focus();
						ret = false;
						// TODO: handle exception
					}
				}

			}

			if (itemsCompo.getValue() == null
					|| itemsCompo.getValue().equals("")) {
				setRequiredError(itemsCompo,
						getPropertyName("invalid_selection"), true);
				itemsCompo.focus();
				ret = false;
			} else
				setRequiredError(itemsCompo, null, false);

			if (ret && barcodeField.getValue() != null
					&& barcodeField.getValue().toString().trim().length() > 0) {
				if (purchDao.isBarcodeExists((Long) itemsCompo.getValue(),
						barcodeField.getValue())) {
					setRequiredError(barcodeField,
							getPropertyName("invalid_data"), true);
					barcodeField.focus();
					ret = false;
				}
			}

		} catch (Exception e) {
		}

		return ret;

	}

	public void visibleAddupdatePurchaseButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				Collection selectedItems = (Collection) table.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					Object obj = it1.next();
					if (purchaseNumberList.getValue() != null
							&& !purchaseNumberList.getValue().toString()
									.equals("0")) {
						Item item = table.getItem(obj);
						if (comDao.isStockBlocked((Long) purchaseNumberList
								.getValue(),
								(Long) item.getItemProperty(TBC_INV_ID)
										.getValue())) {
							Notification.show(getPropertyName("sale_delete"),
									Type.WARNING_MESSAGE);
						} else {
							table.removeItem(obj);
						}
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
			itemsCompo.focus();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadPurchase(long id) {
		List testList;
		try {
			testList = daoObj.getAllActivePurchaseNos(getOfficeID());

			CommissionPurchaseModel sop = new CommissionPurchaseModel();
			sop.setId(0);
			sop.setComments("-------- Create New --------");
			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			SCollectionContainer bic = SCollectionContainer.setList(testList,
					"id");
			purchaseNumberList.setContainerDataSource(bic);
			purchaseNumberList.setItemCaptionPropertyId("comments");

			purchaseNumberList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadItems() {
		List list;
		try {
			list = itemDao
					.getAllActiveItemsWithAppendingItemCode(getOfficeID());

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			itemsCompo.setContainerDataSource(bic);
			itemsCompo.setItemCaptionPropertyId("name");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadSuppliers() {
		List list;
		try {
			list = supDao.getAllActiveSupplierNamesWithLedgerID(getOfficeID());
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			supplierSelect.setContainerDataSource(bic);
			supplierSelect.setItemCaptionPropertyId("name");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadUnit() {
		try {
			if (itemsCompo.getValue() != null) {

				Object temp = unitSelect.getValue();

				SCollectionContainer bic = SCollectionContainer.setList(comDao
						.getAllItemUnitDetails((Long) itemsCompo.getValue()),
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

				unitSelect.setValue(temp);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		quantityTextField.selectAll();
		quantityTextField.focus();

	}


	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (payingAmountTextField.getValue() == null
				|| payingAmountTextField.getValue().equals("")) {
			payingAmountTextField.setValue("0.0");
		} else {
			try {
				if (toDouble(payingAmountTextField.getValue()) < 0) {
					setRequiredError(payingAmountTextField,
							getPropertyName("invalid_data"), true);
					payingAmountTextField.focus();
					ret = false;
				} else
					setRequiredError(payingAmountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(payingAmountTextField,
						getPropertyName("invalid_data"), true);
				payingAmountTextField.focus();
				ret = false;
			}
		}

		if (shippingChargeTextField.getValue() == null
				|| shippingChargeTextField.getValue().equals("")) {
			setRequiredError(shippingChargeTextField,
					getPropertyName("invalid_data"), true);
			shippingChargeTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(shippingChargeTextField.getValue()) < 0) {
					setRequiredError(shippingChargeTextField,
							getPropertyName("invalid_data"), true);
					shippingChargeTextField.focus();
					ret = false;
				} else
					setRequiredError(shippingChargeTextField, null, false);
			} catch (Exception e) {
				setRequiredError(shippingChargeTextField,
						getPropertyName("invalid_data"), true);
				shippingChargeTextField.focus();
				ret = false;
			}
		}

		if (exciseDutyTextField.getValue() == null
				|| exciseDutyTextField.getValue().equals("")) {
			setRequiredError(exciseDutyTextField,
					getPropertyName("invalid_data"), true);
			exciseDutyTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(exciseDutyTextField.getValue()) < 0) {
					setRequiredError(exciseDutyTextField,
							getPropertyName("invalid_data"), true);
					exciseDutyTextField.focus();
					ret = false;
				} else
					setRequiredError(exciseDutyTextField, null, false);
			} catch (Exception e) {
				setRequiredError(exciseDutyTextField,
						getPropertyName("invalid_data"), true);
				exciseDutyTextField.focus();
				ret = false;
			}
		}

		if (grandTotalAmtTextField.getValue() == null
				|| grandTotalAmtTextField.getValue().equals("")) {
			setRequiredError(grandTotalAmtTextField,
					getPropertyName("invalid_data"), true);
			grandTotalAmtTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(grandTotalAmtTextField.getValue()) <= 0) {
					setRequiredError(grandTotalAmtTextField,
							getPropertyName("invalid_data"), true);
					grandTotalAmtTextField.focus();
					ret = false;
				} else
					setRequiredError(grandTotalAmtTextField, null, false);
			} catch (Exception e) {
				setRequiredError(grandTotalAmtTextField,
						getPropertyName("invalid_data"), true);
				grandTotalAmtTextField.focus();
				ret = false;
			}
		}
		
		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			itemsCompo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (supplierSelect.getValue() == null
				|| supplierSelect.getValue().equals("")) {
			setRequiredError(supplierSelect,
					getPropertyName("invalid_selection"), true);
			supplierSelect.focus();
			ret = false;
		} else
			setRequiredError(supplierSelect, null, false);

		return ret;
	}

	public void removeAllErrors() {
		if (discountTextField.getComponentError() != null)
			setRequiredError(discountTextField, null, false);
		if (taxSelect.getComponentError() != null)
			setRequiredError(taxSelect, null, false);
		if (unitPriceTextField.getComponentError() != null)
			setRequiredError(unitPriceTextField, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (itemsCompo.getComponentError() != null)
			setRequiredError(itemsCompo, null, false);

		if (grossWeightTextField.getComponentError() != null)
			setRequiredError(grossWeightTextField, null, false);
		if (netWeightTextField.getComponentError() != null)
			setRequiredError(itemsCompo, null, false);
		if (noOfCartonsTextField.getComponentError() != null)
			setRequiredError(noOfCartonsTextField, null, false);
		if (cbmValueTextField.getComponentError() != null)
			setRequiredError(cbmValueTextField, null, false);

		if (table.getComponentError() != null)
			setRequiredError(table, null, false);

	}
	
	@Override
	public Boolean getHelp() {
		return null;
	}

	public SComboField getPurchaseNumberList() {
		return purchaseNumberList;
	}

	public void setPurchaseNumberList(SComboField purchaseNumberList) {
		this.purchaseNumberList = purchaseNumberList;
	}
	@Override
	public SComboField getBillNoFiled() {
		return purchaseNumberList;
	}

}
*/