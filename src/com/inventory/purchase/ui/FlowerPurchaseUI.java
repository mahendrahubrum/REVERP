/*package com.inventory.purchase.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

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
import com.inventory.dao.SalesManMapDao;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseOrderDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseOrderModel;
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
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.DocumentAttach;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

*//**
 * @author Jinshad P.T.
 * 
 *         Jun 25, 2013
 *//*
public class FlowerPurchaseUI extends SparkLogic {

	private static final long serialVersionUID = 3875664609920107406L;

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
	static String TBC_MANUFACT_DATE = "Mfg. Date";
	static String TBC_EXPIRE_DATE = "Exp. Date";
	static String TBC_BARCODE = "Barcode";
	static String TBC_GRADE_ID = "Grade Id";
	static String TBC_GRADE = "Grade";
	static String TBC_GROSS_WEIGHT = "Gross Weight";
	static String TBC_NET_WEIGHT = "Net Weight";
	static String TBC_NO_OF_CARTON = "No. of Cartons";
	static String TBC_CBM_VALUE = "CBM";
	static String TBC_TAG = "Tag";

	PurchaseDao daoObj = new PurchaseDao();

	static int flag = 0;

	private SComboField purchaseNumberList;

	SPanel pannel;
	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	SComboField responsibleEmployeeCombo;

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
	SComboField poSelect;
	SButton addPOBtn;

	SButton updateRackStock;

	SOptionGroup purchaseOrdersOptions;
	SButton addPOButton;
	SWindow poWindow;

	SWindow rackStockWindow;

	ItemDao itemDao;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	STextField purchaseBillNoTextField;
	STextField catalogNoField;
	// SComboField buildingSelect;
	SComboField supplierSelect;
	SDateField date;
	// SDateField expected_delivery_date;

	SDateField expiry_date;
	SDateField manufacturing_date;

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

	List<Long> PONoList;

	private SButton loadAllSuppliersButton;

	private STextField creditPeriodTextField;

	private SRadioButton cashOrCreditRadio;

	UserManagementDao usrDao;
	SupplierDao supDao;
	TaxDao taxDao;
	UnitDao untDao;

	SButton newSaleButton;

	SButton barcodePrintButton;
	private STextField barcodeField;

	private DocumentAttach attach;
	GradeDao gradeDao;
	SNativeSelect gradeComboField;

	SPopupView barcodePop;
	SFormLayout barcodeLay;
	STextField noOfBarcodeField;

	SNativeSelect purchaseTypeField;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

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

		daoObj = new PurchaseDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_PO_ID, TBC_INV_ID, TBC_MANUFACT_DATE,
				TBC_EXPIRE_DATE, TBC_BARCODE, TBC_GRADE_ID, TBC_GRADE,
				TBC_GROSS_WEIGHT, TBC_NET_WEIGHT, TBC_NO_OF_CARTON,
				TBC_CBM_VALUE, TBC_TAG };

		if (taxEnable) {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_GROSS_WEIGHT,
					TBC_NET_WEIGHT, TBC_NO_OF_CARTON, TBC_CBM_VALUE,
					TBC_UNIT_PRICE, TBC_MANUFACT_DATE, TBC_EXPIRE_DATE,
					TBC_TAX_AMT, TBC_DISCOUNT, TBC_NET_PRICE, TBC_GRADE,
					TBC_TAG };
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_GROSS_WEIGHT,
					TBC_NET_WEIGHT, TBC_NO_OF_CARTON, TBC_CBM_VALUE,
					TBC_UNIT_PRICE, TBC_MANUFACT_DATE, TBC_EXPIRE_DATE,
					TBC_DISCOUNT, TBC_NET_PRICE, TBC_GRADE, TBC_TAG };
		}
		if (settings.isBARCODE_ENABLED()) {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_GROSS_WEIGHT,
					TBC_NET_WEIGHT, TBC_NO_OF_CARTON, TBC_CBM_VALUE,
					TBC_UNIT_PRICE, TBC_MANUFACT_DATE, TBC_EXPIRE_DATE,
					TBC_DISCOUNT, TBC_NET_PRICE, TBC_BARCODE, TBC_GRADE,
					TBC_TAG };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		if (!isManufDateEnable()) {
			templist.remove(TBC_MANUFACT_DATE);
			templist.remove(TBC_EXPIRE_DATE);
		}
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
		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(16);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(3);

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

		addPOButton = new SButton(null, "Add");
		addPOButton.setStyleName("updateItemBtnStyle");

		try {
			List list = new ArrayList();
			list.add(new PurchaseModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllPurchaseNumbersAsComment(getOfficeID()));
			purchaseNumberList = new SComboField(null, 125, list, "id",
					"comments", false, "Create New");

			purchaseBillNoTextField = new STextField(null, 120);
			catalogNoField = new STextField(null, 120);
			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date(
					getWorkingDate().getTime()));

			supplierSelect = new SComboField(
					null,
					250,
					supDao.getAllActiveSupplierNamesWithLedgerID(getOfficeID()),
					"id", "name", true, "Select");

			responsibleEmployeeCombo = new SComboField(null, 125,
					new SalesManMapDao().getUsers(getOfficeID(),
							SConstants.PURCHASE_MAN), "id", "first_name");

			poSelect = new SComboField(null, 120,
					daoObj.getAllPurchaseOrdersForOffice(getOfficeID()), "id",
					"ref_no");
			addPOBtn = new SButton("Add");

			cashOrCreditRadio = new SRadioButton(null, 150,
					SConstants.paymentModeList, "key", "value");
			cashOrCreditRadio.setStyleName("radio_horizontal");

			purchaseTypeField = new SNativeSelect(null, 80,
					SConstants.local_foreign_type.local_foreign_type, "intKey",
					"value");
			purchaseTypeField.setValue(SConstants.local_foreign_type.LOCAL);

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(purchaseNumberList);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel("Purchase No. :"), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("bill_no")), 3, 0);
			masterDetailsGrid.addComponent(purchaseBillNoTextField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(purchaseBillNoTextField,
					Alignment.MIDDLE_LEFT);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("supplier")), 1, 1);

			newSupplierButton = new SButton();
			newSupplierButton.setStyleName("addNewBtnStyle");
			newSupplierButton.setDescription("Add new Supplier");

			loadAllSuppliersButton = new SButton();
			loadAllSuppliersButton.setStyleName("loadAllBtnStyle");
			loadAllSuppliersButton
					.setDescription("Load Suppliers in all offices.");
			loadAllSuppliersButton.setId("ALL");

			creditPeriodTextField = new STextField(null, 100);

			SHorizontalLayout hrl = new SHorizontalLayout();
			hrl.addComponent(supplierSelect);
			hrl.addComponent(newSupplierButton);
			hrl.addComponent(loadAllSuppliersButton);

			masterDetailsGrid.addComponent(hrl, 2, 1);

			masterDetailsGrid.setStyleName("master_border");

			SHorizontalLayout hrl2 = new SHorizontalLayout();
			hrl2.addComponent(poSelect);
			hrl2.addComponent(addPOBtn);

			SHorizontalLayout hrl3 = new SHorizontalLayout();
			hrl3.setSpacing(true);
			// hrl3.addComponent(cashOrCreditRadio);

			if (settings.isLOCAL_FOREIGN_TYPE_ENABLED()) {
				hrl3.addComponent(new SLabel(getPropertyName("type")));
				hrl3.addComponent(purchaseTypeField);
			}

			masterDetailsGrid.addComponent(new SLabel("PO"), 3, 1);
			masterDetailsGrid.addComponent(hrl2, 4, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("max_cr_period")), 6, 1);
			masterDetailsGrid.addComponent(creditPeriodTextField, 8, 1);

			masterDetailsGrid.addComponent(hrl3, 2, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("responsible_employee"), 40), 3, 2);
			masterDetailsGrid.addComponent(responsibleEmployeeCombo, 4, 2);

			if (settings.isGRADING_ENABLED()) {
				masterDetailsGrid.addComponent(new SLabel(
						getPropertyName("catalog_no"), 40), 6, 2);
				masterDetailsGrid.addComponent(catalogNoField, 8, 2);
			}

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
			unitSelect = new SNativeSelect("Unit", 60);
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

			expiry_date = new SDateField(getPropertyName("exp_date"), 80,
					"dd/MMM/yyyy", new Date());
			manufacturing_date = new SDateField(getPropertyName("mfg_date"),
					80, "dd/MMM/yyyy", new Date());

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
			addingGrid.addComponent(popupHor);

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

			addingGrid.addComponent(manufacturing_date);
			addingGrid.addComponent(expiry_date);
			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(discountTextField);
			addingGrid.addComponent(netPriceTextField);
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
					getPropertyName("qty"), null, Align.CENTER);
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
			table.addContainerProperty(TBC_MANUFACT_DATE, Date.class, null,
					getPropertyName("mfg_date"), null, Align.RIGHT);
			table.addContainerProperty(TBC_EXPIRE_DATE, Date.class, null,
					getPropertyName("exp_date"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BARCODE, String.class, null,
					getPropertyName("barcode"), null, Align.RIGHT);
			table.addContainerProperty(TBC_GRADE_ID, Long.class, null,
					TBC_GRADE_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_GRADE, String.class, null,
					getPropertyName("grade"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAG, String.class, null, TBC_TAG,
					null, Align.RIGHT);

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
			table.setColumnExpandRatio(TBC_MANUFACT_DATE, (float) 1.1);
			table.setColumnExpandRatio(TBC_EXPIRE_DATE, (float) 1.1);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, "Total :");
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_DISCOUNT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1200");
			table.setHeight("200");

			grandTotalAmtTextField = new STextField(null, 200, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");
			comment = new STextArea(null, 400, 30);

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

			attach = new DocumentAttach(SConstants.documentAttach.PURCHASE_BILL);

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

			if (!isManufDateEnable()) {
				expiry_date.setVisible(false);
				manufacturing_date.setVisible(false);
			}

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					0);
			bottomGrid.addComponent(comment, 1, 0);

			bottomGrid.addComponent(attach, 0, 3);
			bottomGrid.setComponentAlignment(attach, Alignment.TOP_CENTER);

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),
					4, 3);
			bottomGrid.addComponent(grandTotalAmtTextField, 5, 3);

			// bottomGrid.addComponent(new SLabel("Paying Amt :"), 2, 3);
			// bottomGrid.addComponent(payingAmountTextField, 3, 3);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			savePurchaseButton = new SButton(getPropertyName("Save"), 70);
			savePurchaseButton.setStyleName("savebtnStyle");
			savePurchaseButton.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			updatePurchaseButton = new SButton(getPropertyName("Update"), 80);
			updatePurchaseButton.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updatePurchaseButton.setStyleName("updatebtnStyle");

			deletePurchaseButton = new SButton(getPropertyName("Delete"), 78);
			deletePurchaseButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deletePurchaseButton.setStyleName("deletebtnStyle");

			cancelPurchaseButton = new SButton(getPropertyName("Cancel"), 78);
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
			mainButtonLayout.addComponent(printButton);

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

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			supplierSelect.focus();

			pannel.setContent(hLayout);

			purchaseOrdersOptions = new SOptionGroup(
					getPropertyName("select_po"), 300, null, "id", "ref_no",
					true);
			poWindow = new SWindow(getPropertyName("purchase_orders"), 400, 400);
			poWindow.center();
			poWindow.setResizable(false);
			poWindow.setModal(true);
			poWindow.setCloseShortcut(KeyCode.ESCAPE);
			SFormLayout fr1 = new SFormLayout();
			fr1.addComponent(purchaseOrdersOptions);
			fr1.addComponent(addPOButton);
			poWindow.setContent(fr1);

			rackStockWindow = new SWindow(getPropertyName("stock_arrangement"));

			getHttpSession().setAttribute("firsttime", "Y");

			rackStockWindow.center();

			priceListButton.setStyleName("showHistoryBtnStyle");

			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);

			PONoList = new ArrayList<Long>();

			Iterator itr = cashOrCreditRadio.getItemIds().iterator();
			itr.next();
			cashOrCreditRadio.setValue(itr.next());

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					purchaseNumberList.setValue((long) 0);
				}
			});

			addPOBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						removeAllErrors();

						if (poSelect.getValue() != null
								&& !PONoList.contains((Long) poSelect
										.getValue())) {

							table.setVisibleColumns(allHeaders);

							PurchaseOrderModel opModel = new PurchaseOrderDao()
									.getPurchaseOrder((Long) poSelect
											.getValue());

							session.setAttribute("PO_Select_Disabled", "Y");

							supplierSelect.setValue(opModel.getSupplier()
									.getId());

							List itemsList = daoObj
									.getAllItemsFromPurchaseOrder((Long) poSelect
											.getValue());

							int id = 0, ct = table.getItemIds().size();
							Iterator it1 = table.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							InventoryDetailsPojo invObj;
							double netTotal;
							double perc;
							Iterator it = itemsList.iterator();
							while (it.hasNext()) {
								invObj = (InventoryDetailsPojo) it.next();

								netTotal = 0;
								if (invObj.getTax_percentage() > 0) {
									perc = (invObj.getUnit_price() * invObj
											.getBalance())
											* invObj.getTax_percentage() / 100;
									netTotal = (invObj.getUnit_price() * invObj
											.getBalance())
											+ perc
											- invObj.getDiscount_amount();
								} else {
									netTotal = (invObj.getUnit_price() * invObj
											.getBalance())
											+ invObj.getTax_amount()
											- invObj.getDiscount_amount();
								}

								id++;
								ct++;
								table.addItem(
										new Object[] {
												ct,
												invObj.getItem_id(),
												invObj.getItem_code(),
												invObj.getItem_name(),
												invObj.getBalance(),
												invObj.getUnit_id(),
												invObj.getUnit_name(),
												invObj.getUnit_price(),
												invObj.getTax_id(),
												invObj.getTax_amount(),
												invObj.getTax_percentage(),
												invObj.getDiscount_amount(),
												netTotal,
												invObj.getOrder_id(),
												invObj.getInventry_details_id(),
												invObj.getManufacturing_date(),
												invObj.getExpiry_date(), "",
												(long) 0, "None", 0.0, 0.0,
												0.0, 0.0, "" }, id);

							}

							table.setVisibleColumns(requiredHeaders);

							calculateTotals();
							// payingAmountTextField
							// .setValue(grandTotalAmtTextField.getValue());

							PONoList.add((Long) poSelect.getValue());

						} else {

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
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

							STable table = new STable("Sales Rate History :");

							table.addContainerProperty("Date", Date.class,
									null, "Date", null, Align.CENTER);
							table.addContainerProperty("Customer Name",
									String.class, null, "Customer Name", null,
									Align.LEFT);
							table.addContainerProperty("Price", Double.class,
									null, "Price", null, Align.RIGHT);

							table.setVisibleColumns(new String[] { "Date",
									"Customer Name", "Price" });

							List list = new SalesDao().getSalesRateHistory(
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
									"Purchase Rate History :");

							purchTable.addContainerProperty("Date", Date.class,
									null, "Date", null, Align.CENTER);
							purchTable.addContainerProperty("Supplier Name",
									String.class, null, "Supplier Name", null,
									Align.LEFT);
							purchTable.addContainerProperty("Price",
									Double.class, null, "Price", null,
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						SPopupView pop = new SPopupView("", new SLabel(
								"Select a Item and Unit"));
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
										responsibleEmployeeCombo.setValue(supObj
												.getResponsible_person());
										creditPeriodTextField
												.setValue(asString(supObj
														.getCredit_period()));
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

								purchaseOrdersOptions
										.setContainerDataSource(null);

								if (supplierSelect.getValue() != null)
									creditPeriodTextField.setValue(asString(supDao
											.getSupplierCreditPeriodFromLedger((Long) supplierSelect
													.getValue())));

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

									if (supplierSelect.getValue() != null) {

										List list = daoObj
												.getAllPurchaseOrdersForSupplier(
														(Long) supplierSelect
																.getValue(),
														getOfficeID());

										if (list.size() > 0) {

											SCollectionContainer bic = SCollectionContainer
													.setList(list, "id");
											purchaseOrdersOptions
													.setContainerDataSource(bic);
											purchaseOrdersOptions
													.setItemCaptionPropertyId("ref_no");

											getUI().getCurrent().addWindow(
													poWindow);

											if (purchaseOrdersOptions
													.getItemIds().size() > 0) {
												purchaseOrdersOptions.focus();
												setRequiredError(
														purchaseOrdersOptions,
														null, false);
											} else {
												setRequiredError(
														purchaseOrdersOptions,
														getPropertyName("no_po_ledger"),
														true);
												addPOButton.focus();
											}
										}

									}

								} else {
									getHttpSession().removeAttribute(
											"PO_Select_Disabled");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			addPOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						removeAllErrors();

						if (purchaseOrdersOptions.getValue() != null
								&& ((Set<Long>) purchaseOrdersOptions
										.getValue()).size() > 0) {

							table.setVisibleColumns(allHeaders);

							Set<Long> POs = (Set<Long>) purchaseOrdersOptions
									.getValue();

							PONoList.addAll(POs);

							List itemsList = daoObj
									.getAllItemsFromPurchaseOrders(POs);

							int id = 0, ct = table.getItemIds().size();
							Iterator it1 = table.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							double netTotal = 0;
							double perc;
							InventoryDetailsPojo invObj;
							Iterator it = itemsList.iterator();
							while (it.hasNext()) {
								invObj = (InventoryDetailsPojo) it.next();

								netTotal = 0;
								if (invObj.getTax_percentage() > 0) {
									perc = (invObj.getUnit_price() * invObj
											.getBalance())
											* invObj.getTax_percentage() / 100;
									netTotal = (invObj.getUnit_price() * invObj
											.getBalance())
											+ perc
											- invObj.getDiscount_amount();
								} else {
									netTotal = (invObj.getUnit_price() * invObj
											.getBalance())
											+ invObj.getTax_amount()
											- invObj.getDiscount_amount();
								}

								id++;
								ct++;
								table.addItem(
										new Object[] {
												ct,
												invObj.getItem_id(),
												invObj.getItem_code(),
												invObj.getItem_name(),
												invObj.getBalance(),
												invObj.getUnit_id(),
												invObj.getUnit_name(),
												invObj.getUnit_price(),
												invObj.getTax_id(),
												invObj.getTax_amount(),
												invObj.getTax_percentage(),
												invObj.getDiscount_amount(),
												netTotal,
												invObj.getOrder_id(),
												invObj.getInventry_details_id(),
												invObj.getManufacturing_date(),
												invObj.getExpiry_date(), "",
												(long) 0, "None", 0.0, 0.0,
												0.0, 0.0, "" }, id);

							}

							table.setVisibleColumns(requiredHeaders);

							getUI().removeWindow(poWindow);

							calculateTotals();
							// payingAmountTextField
							// .setValue(grandTotalAmtTextField.getValue());

						} else {
							getUI().removeWindow(poWindow);
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

						Set<Long> itemIdsSet = new HashSet<Long>();

						if (isValid()) {

							long supplier_id = (Long) supplierSelect.getValue();

							PurchaseModel purObj = new PurchaseModel();

							List<PurchaseInventoryDetailsModel> itemsList = new ArrayList<PurchaseInventoryDetailsModel>();
							HashMap<PurchaseInventoryDetailsModel, String> map = new HashMap<PurchaseInventoryDetailsModel, String>();

							PurchaseInventoryDetailsModel invObj;
							Item item;
							double conv_rat;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new PurchaseInventoryDetailsModel();
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

								invObj.setManufacturing_date(CommonUtil
										.getSQLDateFromUtilDate((Date) item
												.getItemProperty(
														TBC_MANUFACT_DATE)
												.getValue()));
								invObj.setExpiry_date(CommonUtil
										.getSQLDateFromUtilDate((Date) item
												.getItemProperty(
														TBC_EXPIRE_DATE)
												.getValue()));

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

								itemIdsSet.add((Long) item.getItemProperty(
										TBC_ITEM_ID).getValue());

								
								 * if (purObj.getPayment_amount() == purObj
								 * .getAmount()) {
								 * cashOrCreditRadio.setValue((long) 1); } else
								 * { cashOrCreditRadio.setValue((long) 2); }
								 

								map.put(invObj,
										item.getItemProperty(TBC_BARCODE)
												.getValue().toString());

							}

							purObj.setCreated_time(CommonUtil
									.getCurrentDateTime());

							if (isExciceDutyEnable()) {
								purObj.setExcise_duty(toDouble(exciseDutyTextField
										.getValue()));
							}
							if (isShippingChargeEnable()) {
								purObj.setShipping_charge(toDouble(shippingChargeTextField
										.getValue()));
							}
							purObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue()));
							purObj.setCredit_period(toInt(creditPeriodTextField
									.getValue()));
							purObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							// purObj.setBuilding(new BuildingModel((Long)
							// buildingSelect.getValue()));
							purObj.setComments(comment.getValue());
							purObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							// purObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							purObj.setLogin(new S_LoginModel(getLoginID()));
							purObj.setOffice(new S_OfficeModel(getOfficeID()));
							purObj.setPurchase_bill_number(toLong(purchaseBillNoTextField
									.getValue()));
							purObj.setStatus(1);
							purObj.setSupplier(new LedgerModel(
									(Long) supplierSelect.getValue()));
							purObj.setInventory_details_list(itemsList);

							purObj.setPurchase_number(getNextSequence(
									"Purchase Number", getLoginID()));

							purObj.setResponsible_person((Long) responsibleEmployeeCombo
									.getValue());
							purObj.setActive(true);
							purObj.setCatalogNo(catalogNoField.getValue());
							purObj.setPurchase_local_type(((Integer) purchaseTypeField
									.getValue()));

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField
									.getValue());

							long purchaseAcc = settings.getPURCHASE_ACCOUNT();
							if ((Integer) purchaseTypeField.getValue() == SConstants.local_foreign_type.FOREIGN)
								purchaseAcc = settings
										.getPURCHASE_FOREIGN_ACCOUNT();

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										settings.getCASH_ACCOUNT(),
										supplier_id, roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										supplier_id, purchaseAcc,
										roundNumber(netAmt));
								purObj.setStatus(1);
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										supplier_id, purchaseAcc,
										roundNumber(netAmt));
								purObj.setStatus(2);
							} else {
								trans.addTransaction(SConstants.CR,
										settings.getCASH_ACCOUNT(),
										supplier_id, roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										supplier_id, purchaseAcc,
										roundNumber(netAmt));
								purObj.setStatus(3);
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
							 

							long id = daoObj.save(purObj, trans.getTransaction(
									SConstants.PURCHASE, CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue())), map,
									settings.getUPDATE_RATE_AND_CONV_QTY());

							attach.saveDocument(id, getOfficeID(),
									SConstants.PURCHASE);

							saveActivity(
									getOptionId(),
									"New Purchase Created. Bill No : "
											+ purObj.getPurchase_number()
											+ ", Supplier : "
											+ supplierSelect
													.getItemCaption(supplierSelect
															.getValue())
											+ ", Amount : "
											+ purObj.getAmount(),purObj.getId());

							loadPurchase(id);

							daoObj.updateItemStandardCost(itemIdsSet);

							Notification.show("Success",
									"Saved Successfully..!",
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
								attach.clear();
								removeAllErrors();

								responsibleEmployeeCombo.setValue(null);

								if (purchaseNumberList.getValue() != null
										&& !purchaseNumberList.getValue()
												.toString().equals("0")) {

									PurchaseModel purObj = daoObj
											.getPurchase((Long) purchaseNumberList
													.getValue());

									table.setVisibleColumns(allHeaders);

									table.removeAllItems();

									Iterator it = purObj
											.getInventory_details_list()
											.iterator();
									PurchaseInventoryDetailsModel invObj;
									String barcode = "";
									GradeModel gradeModel = null;
									String gradeName = "";

									double netTotal;
									while (it.hasNext()) {
										invObj = (PurchaseInventoryDetailsModel) it
												.next();

										netTotal = (invObj.getUnit_price() * invObj
												.getQunatity())
												+ invObj.getTax_amount()
												- invObj.getDiscount_amount();

										barcode = daoObj.getBarcode(purObj
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
														invObj.getManufacturing_date(),
														invObj.getExpiry_date(),
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
									date.setValue(purObj.getDate());
									// expected_delivery_date.setValue(purObj.getExpected_delivery_date());
									purchaseBillNoTextField.setValue(asString(purObj
											.getPurchase_bill_number()));
									catalogNoField.setValue(purObj
											.getCatalogNo());
									purchaseTypeField.setValue(purObj
											.getPurchase_local_type());

									getHttpSession().setAttribute(
											"PO_Select_Disabled", 'Y');

									supplierSelect.setValue(purObj
											.getSupplier().getId());

									creditPeriodTextField
											.setValue(asString(purObj
													.getCredit_period()));

									shippingChargeTextField
											.setValue(asString(purObj
													.getShipping_charge()));
									exciseDutyTextField
											.setValue(asString(purObj
													.getExcise_duty()));
									payingAmountTextField
											.setValue(asString(purObj
													.getPayment_amount()));

									if (purObj.getPayment_amount() == purObj
											.getAmount()) {
										cashOrCreditRadio.setValue((long) 1);
									} else {
										cashOrCreditRadio.setValue((long) 2);
									}

									responsibleEmployeeCombo.setValue(purObj
											.getResponsible_person());

									attach.loadDocument(
											(Long) purchaseNumberList
													.getValue(), getOfficeID(),
											SConstants.PURCHASE);

									isValid();
									payingAmountTextField
											.setComponentError(null);
									updatePurchaseButton.setVisible(true);
									updateRackStock.setVisible(true);
									deletePurchaseButton.setVisible(true);
									cancelPurchaseButton.setVisible(true);
									printButton.setVisible(true);
									savePurchaseButton.setVisible(false);

								} else {
									table.removeAllItems();

									grandTotalAmtTextField.setNewValue("0.0");
									payingAmountTextField.setNewValue("0.0");
									// buildingSelect.setValue(null);
									comment.setValue("");
									date.setValue(new Date(getWorkingDate()
											.getTime()));
									// expected_delivery_date.setValue(new
									// Date());
									purchaseBillNoTextField.setValue("");
									catalogNoField.setValue("");
									purchaseTypeField
											.setValue(SConstants.local_foreign_type.LOCAL);
									supplierSelect.setValue(null);

									creditPeriodTextField.setValue("0");
									savePurchaseButton.setVisible(true);
									updatePurchaseButton.setVisible(false);
									updateRackStock.setVisible(false);
									deletePurchaseButton.setVisible(false);
									cancelPurchaseButton.setVisible(false);
									printButton.setVisible(false);
									PONoList.clear();

								}

								reloadPO();

								calculateTotals();

								itemsCompo.setValue(null);
								itemsCompo.focus();
								quantityTextField.setValue("0.0");
								unitPriceTextField.setValue("0.0");
								netPriceTextField.setNewValue("0.0");
								barcodeField.setValue("");

								supplierSelect.focus();
								poSelect.setValue(null);

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

															PurchaseModel purObj = daoObj
																	.getPurchase((Long) purchaseNumberList
																			.getValue());

															List<PurchaseInventoryDetailsModel> itemsList = new ArrayList<PurchaseInventoryDetailsModel>();
															HashMap<PurchaseInventoryDetailsModel, String> map = new HashMap<PurchaseInventoryDetailsModel, String>();

															Iterator it = table
																	.getItemIds()
																	.iterator();
															PurchaseInventoryDetailsModel invObj;
															Item item;
															double conv_rat;
															while (it.hasNext()) {
																invObj = new PurchaseInventoryDetailsModel();

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

																invObj.setManufacturing_date(CommonUtil
																		.getSQLDateFromUtilDate((Date) item
																				.getItemProperty(
																						TBC_MANUFACT_DATE)
																				.getValue()));
																invObj.setExpiry_date(CommonUtil
																		.getSQLDateFromUtilDate((Date) item
																				.getItemProperty(
																						TBC_EXPIRE_DATE)
																				.getValue()));

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

																map.put(invObj,
																		item.getItemProperty(
																				TBC_BARCODE)
																				.getValue()
																				.toString());
															}

															if (isExciceDutyEnable()) {
																purObj.setExcise_duty(toDouble(exciseDutyTextField
																		.getValue()));
															}
															if (isShippingChargeEnable()) {
																purObj.setShipping_charge(toDouble(shippingChargeTextField
																		.getValue()));
															}
															purObj.setCredit_period(toInt(creditPeriodTextField
																	.getValue()));
															purObj.setPayment_amount(toDouble(payingAmountTextField
																	.getValue()));
															purObj.setAmount(toDouble(grandTotalAmtTextField
																	.getValue()));
															// purObj.setBuilding(new
															// BuildingModel((Long)
															// buildingSelect.getValue()));
															purObj.setComments(comment
																	.getValue());
															purObj.setDate(CommonUtil
																	.getSQLDateFromUtilDate(date
																			.getValue()));
															// purObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
															purObj.setLogin(new S_LoginModel(
																	getLoginID()));
															purObj.setOffice(new S_OfficeModel(
																	getOfficeID()));
															purObj.setPurchase_bill_number(toLong(purchaseBillNoTextField
																	.getValue()));
															purObj.setSupplier(new LedgerModel(
																	(Long) supplierSelect
																			.getValue()));

															purObj.setResponsible_person((Long) responsibleEmployeeCombo
																	.getValue());
															purObj.setActive(true);
															purObj.setCatalogNo(catalogNoField
																	.getValue());
															purObj.setPurchase_local_type((Integer) purchaseTypeField
																	.getValue());
															purObj.setInventory_details_list(itemsList);

															FinTransaction trans = new FinTransaction();
															double totalAmt = toDouble(grandTotalAmtTextField
																	.getValue());
															double netAmt = totalAmt;

															double amt = 0;

															double payingAmt = toDouble(payingAmountTextField
																	.getValue());

															long purchaseAcc = settings
																	.getPURCHASE_ACCOUNT();
															if ((Integer) purchaseTypeField
																	.getValue() == SConstants.local_foreign_type.FOREIGN)
																purchaseAcc = settings
																		.getPURCHASE_FOREIGN_ACCOUNT();

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
																purObj.setStatus(1);
															} else if (payingAmt == 0) {
																trans.addTransaction(
																		SConstants.CR,
																		supplier_id,
																		purchaseAcc,
																		roundNumber(netAmt));
																purObj.setStatus(2);
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
																purObj.setStatus(3);
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
																	.getDate());
															tran.setLogin_id(getLoginID());

															daoObj.update(
																	purObj,
																	tran, map);

															attach.saveDocument(
																	purObj.getId(),
																	getOfficeID(),
																	SConstants.PURCHASE);

															saveActivity(
																	getOptionId(),
																	"Purchase Updated. Bill No : "
																			+ purObj.getPurchase_number()
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
							ConfirmDialog.show(getUI(), "Are you sure?",
									new ConfirmDialog.Listener() {
										public void onClose(ConfirmDialog dialog) {
											if (dialog.isConfirmed()) {
												try {
													daoObj.delete((Long) purchaseNumberList
															.getValue());

													attach.deleteDocument(
															toLong(purchaseNumberList
																	.getValue()
																	.toString()),
															getOfficeID(),
															SConstants.PURCHASE);

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

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.cancel((Long) purchaseNumberList
														.getValue());

												attach.deleteDocument(
														toLong(purchaseNumberList
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.PURCHASE);

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

						manufacturing_date.setValue((Date) item
								.getItemProperty(TBC_MANUFACT_DATE).getValue());
						expiry_date.setValue((Date) item.getItemProperty(
								TBC_EXPIRE_DATE).getValue());
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
											manufacturing_date.getValue(),
											expiry_date.getValue(),
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
							item.getItemProperty(TBC_MANUFACT_DATE).setValue(
									manufacturing_date.getValue());
							item.getItemProperty(TBC_EXPIRE_DATE).setValue(
									expiry_date.getValue());
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
										barcodeField.setValue(daoObj
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

						PurchaseModel purchObj = daoObj
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
								.formatDateToDDMMMYYYY(date.getValue()));
						map.put("SALES_MAN", "");

						String resp = "";
						if (purchObj.getResponsible_person() != 0) {
							UserModel usrObj = usrDao.getUserFromLogin(purchObj
									.getResponsible_person());

							resp = usrObj.getFirst_name();

							if (usrObj.getAddress() != null) {
								if (usrObj.getAddress().getMobile() != null
										&& !usrObj.getAddress().getMobile()
												.equals(""))
									resp += " Mob: "
											+ usrObj.getAddress().getMobile();
								if (usrObj.getAddress().getPhone() != null
										&& !usrObj.getAddress().getPhone()
												.equals(""))
									resp += " Ph: "
											+ usrObj.getAddress().getPhone();
							}

						}
						map.put("RESPONSIBLE_PERSON", resp);

						map.put("SALES_TYPE", "Purchase Invoice");
						map.put("OFFICE_NAME", customerModel.getLedger()
								.getOffice().getName());

						map.put("PURCH_BILL_DATE", purchObj.getDate());

						PurchaseInventoryDetailsModel invObj;
						Iterator<PurchaseInventoryDetailsModel> itr1 = purchObj
								.getInventory_details_list().iterator();
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
						// report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE));
						report.setJrxmlFileName("FlowerPurchase_Print");

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
								ItemStockModel stk = daoObj
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

			grandTotalAmtTextField.setNewValue(asString(roundNumber(net_ttl
					+ ship_charg + excise_duty)));

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
						getPropertyName("enter_discount"), true);
				discountTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(discountTextField.getValue()) < 0) {
						setRequiredError(discountTextField,
								getPropertyName("enter_valid_discount"), true);
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

			if (expiry_date.getValue() == null
					|| expiry_date.getValue().equals("")) {
				setRequiredError(expiry_date,
						getPropertyName("invalid_selection"), true);
				expiry_date.focus();
				ret = false;
			} else
				setRequiredError(expiry_date, null, false);

			if (manufacturing_date.getValue() == null
					|| manufacturing_date.getValue().equals("")) {
				setRequiredError(manufacturing_date,
						getPropertyName("invalid_selection"), true);
				manufacturing_date.focus();
				ret = false;
			} else
				setRequiredError(manufacturing_date, null, false);

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
								"Enter a valid Price", true);
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
							"Enter a valid Quantity", true);
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
								"Enter a valid Quantity", true);
						netWeightTextField.focus();
						ret = false;
						// TODO: handle exception
					}
				}

				if (noOfCartonsTextField.getValue() == null
						|| noOfCartonsTextField.getValue().equals("")) {
					setRequiredError(noOfCartonsTextField, "Enter a Quantity",
							true);
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
				if (daoObj.isBarcodeExists((Long) itemsCompo.getValue(),
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

			if (table.getItemIds().size() <= 0)
				PONoList.clear();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadPurchase(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new PurchaseModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllPurchaseNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			purchaseNumberList.setContainerDataSource(bic);
			purchaseNumberList.setItemCaptionPropertyId("comments");

			purchaseNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	public void reloadPO() {
		try {
			SCollectionContainer bic = SCollectionContainer
					.setList(new PurchaseDao()
							.getAllPurchaseOrdersForOffice(getOfficeID()), "id");
			poSelect.setContainerDataSource(bic);
			poSelect.setItemCaptionPropertyId("ref_no");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;
		setRequiredError(purchaseBillNoTextField, null, false);

		
		 * if (payingAmountTextField.getValue() == null ||
		 * payingAmountTextField.getValue().equals("")) {
		 * payingAmountTextField.setValue("0.0"); } else { try { if
		 * (toDouble(payingAmountTextField.getValue()) < 0) {
		 * setRequiredError(payingAmountTextField, "Enter a valid amount",
		 * true); payingAmountTextField.focus(); ret = false; } else
		 * setRequiredError(payingAmountTextField, null, false); } catch
		 * (Exception e) { setRequiredError(payingAmountTextField,
		 * "Enter a valid amount", true); payingAmountTextField.focus(); ret =
		 * false; // TODO: handle exception } }
		 * 
		 * 
		 * if ((Long) cashOrCreditRadio.getValue() == 1) {
		 * 
		 * if (toDouble(payingAmountTextField.getValue()) !=
		 * toDouble(grandTotalAmtTextField .getValue())) { setRequiredError(
		 * payingAmountTextField,
		 * "Enter a valid amount. Need to set Paying amount to Total amount. Else select Credit sale."
		 * , true); payingAmountTextField.focus(); ret = false; }
		 * 
		 * } else if (toDouble(payingAmountTextField.getValue()) >=
		 * toDouble(grandTotalAmtTextField .getValue())) {
		 * setRequiredError(payingAmountTextField,
		 * "Enter a valid amount. Must be less than total. Else select Cash Sale."
		 * , true); payingAmountTextField.focus(); ret = false; }
		 

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
				// TODO: handle exception
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
				// TODO: handle exception
			}
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			itemsCompo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		
		 * if(buildingSelect.getValue()==null ||
		 * buildingSelect.getValue().equals("")){ setRequiredError(
		 * buildingSelect, "Select a building",true); buildingSelect.focus();
		 * ret=false; } else setRequiredError(buildingSelect , null,false);
		 

		if (supplierSelect.getValue() == null
				|| supplierSelect.getValue().equals("")) {
			setRequiredError(supplierSelect,
					getPropertyName("invalid_selection"), true);
			supplierSelect.focus();
			ret = false;
		} else
			setRequiredError(supplierSelect, null, false);

		
		 * if(expected_delivery_date.getValue()==null ||
		 * expected_delivery_date.getValue().equals("")){
		 * setRequiredError(expected_delivery_date, "Select a Date",true);
		 * expected_delivery_date.focus(); ret=false; } else
		 * setRequiredError(expected_delivery_date, null,false);
		 

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		if (purchaseBillNoTextField.getValue() == null
				|| purchaseBillNoTextField.getValue().equals("")) {
			setRequiredError(purchaseBillNoTextField,
					getPropertyName("invalid_data"), true);
			purchaseBillNoTextField.focus();
			ret = false;
		} else {
			try {
				toLong(purchaseBillNoTextField.getValue().toString());
			} catch (Exception e) {
				setRequiredError(purchaseBillNoTextField,
						getPropertyName("invalid_data"), true);
				ret = false;
			}
		}

		if (creditPeriodTextField.getValue() == null
				|| creditPeriodTextField.getValue().equals("")) {
			creditPeriodTextField.setValue("0");
		} else {
			try {
				if (toInt(creditPeriodTextField.getValue()) < 0) {
					setRequiredError(creditPeriodTextField,
							getPropertyName("invalid_data"), true);
					creditPeriodTextField.focus();
					ret = false;
				} else
					setRequiredError(creditPeriodTextField, null, false);
			} catch (Exception e) {
				setRequiredError(creditPeriodTextField,
						getPropertyName("invalid_data"), true);
				creditPeriodTextField.focus();
				ret = false;
			}
		}
		if ((Long) cashOrCreditRadio.getValue() == 1) {

			if (toDouble(payingAmountTextField.getValue()) != toDouble(grandTotalAmtTextField
					.getValue())) {
				setRequiredError(payingAmountTextField,
						getPropertyName("invalid_data"), true);
				payingAmountTextField.focus();
				ret = false;
			}

		} else if (toDouble(payingAmountTextField.getValue()) >= toDouble(grandTotalAmtTextField
				.getValue())) {
			setRequiredError(payingAmountTextField,
					getPropertyName("invalid_data"), true);
			payingAmountTextField.focus();
			ret = false;
		}

		if (responsibleEmployeeCombo.getValue() == null
				|| responsibleEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsibleEmployeeCombo,
					getPropertyName("invalid_selection"), true);
			responsibleEmployeeCombo.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployeeCombo, null, false);

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
		if (expiry_date.getComponentError() != null)
			setRequiredError(expiry_date, null, false);
		if (manufacturing_date.getComponentError() != null)
			setRequiredError(manufacturing_date, null, false);
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

		if (responsibleEmployeeCombo.getComponentError() != null)
			setRequiredError(responsibleEmployeeCombo, null, false);

	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public SComboField getPurchaseNumberList() {
		return purchaseNumberList;
	}

	public void setPurchaseNumberList(SComboField purchaseNumberList) {
		this.purchaseNumberList = purchaseNumberList;
	}

}
*/