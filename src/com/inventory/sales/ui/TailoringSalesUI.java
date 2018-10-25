package com.inventory.sales.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.SetUnitManagementUI;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.bean.SalesInventoryDetailsPojo;
import com.inventory.sales.dao.TailoringItemSpecDao;
import com.inventory.sales.dao.TailoringSalesDao;
import com.inventory.sales.model.TailoringItemSpecModel;
import com.inventory.sales.model.TailoringSalesInventoryDetailsModel;
import com.inventory.sales.model.TailoringSalesModel;
import com.inventory.tailoring.dao.ProductionUnitDao;
import com.inventory.tailoring.model.ProductionUnitModel;
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
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SDialogBox;
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

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Feb 14, 2014
 */
public class TailoringSalesUI extends SparkLogic {

	private static final long serialVersionUID = -5415935778881929746L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_DESCRIPTION = "Description";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "Tax %";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_PO_ID = "PO ID";
	static String TBC_INV_ID = "INV ID";
	static String TBC_CESS_AMT = "CESS";
	static String TBC_NET_TOTAL = "Net Total";
	static String TBC_NET_FINAL = "Final Amt";
	static String TBC_STK_IDS = "Stock Ids";
	static String TBC_SPEC_FIELD_ID = "Spec Field Ids";

	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";

	TailoringSalesDao daoObj;

	CommonMethodsDao comDao;

	SComboField salesNumberList;

	SPanel pannel;
	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField chargeTextField;
	STextField netPriceTextField;
	
	STextField description;
	
	STextField payingAmountTextField;
	STextField creditPeriodTextField;
//	STextField advanceAmountField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSalesButton;
	SButton updateSalesButton;
	SButton deleteSalesButton;
	SButton cancelButton;
	SButton deliverSalesButton;

	SOptionGroup salesOrdersOptions;
	SButton addSOButton;
	SDialogBox poWindow;

	ItemDao itemDao;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SComboField customerSelect;
	SDateField date;
	SDateField exp_delv_date;
	SDateField act_delv_date;
	SComboField itemSelectCombo;

	SComboField employSelect;
	SComboField productionUnitSelect;
	SRadioButton materialSourceRadioButton;
	SNativeSelect paymentModeField;
	SComboField toAccountComboField;
	
	STextField grandTotalAmtTextField;
	STextField shippingChargeTextField;
	STextField exciseDutyTextField;
	STextArea comment;
	
	SComboField newproductionUnitSelect;
	SButton changeProdWindowButton;
	SButton cancelProdWindowButton;
	SFormLayout prodWindowFormLayout;

	// SRadioButton cashOrCreditRadio;

	SettingsValuePojo settings;

	WrappedSession session;

	boolean taxEnable = isTaxEnable();

	private String[] allHeaders;
	private String[] requiredHeaders;

	private SButton printButton;
	private SButton changeProdUnitButton;

	long status;

	private SButton newSaleButton;
	private SButton newCustomerButton;
	private SButton newItemButton;

	private SButton newUnitButton;
	private SButton unitMapButton;

	private SDialogBox newCustomerWindow;
	private SDialogBox newItemWindow;
	private SalesCustomerPanel salesCustomerPanel;
	private ItemPanel itemPanel;

	STextField barcodeField;
	SNativeSelect salesTypeSelect;

	SWindow popupWindow;

	private SButton loadAllSuppliersButton;

	SHorizontalLayout popupHor;
	SButton priceListButton;

	UserManagementDao usrDao;

	CustomerDao custDao;
	TaxDao taxDao;

	private STextField refNoField;

	private STextField specField;
	private STextField balanceField;

	private SOptionGroup specGroup;

	private TailoringItemSpecDao specDao;

	private List specArrayList;
	private SHorizontalLayout specFieldLay;

	private SLabel statusLabel;
	
	LedgerDao ledgDao;

	private ValueChangeListener barcodeListener;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		comDao = new CommonMethodsDao();
		custDao = new CustomerDao();
		taxDao = new TaxDao();
		itemDao = new ItemDao();
		usrDao = new UserManagementDao();
		specDao = new TailoringItemSpecDao();
		ledgDao = new LedgerDao();

		taxEnable = isTaxEnable();

		popupWindow = new SWindow();

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME,TBC_DESCRIPTION, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_NET_PRICE,
				TBC_PO_ID, TBC_INV_ID, TBC_CESS_AMT, TBC_NET_TOTAL,
				TBC_NET_FINAL, TBC_QTY_IN_BASIC_UNI, TBC_STK_IDS,
				TBC_SPEC_FIELD_ID };

		if (taxEnable) {
			if (isCessEnable()) {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME,TBC_DESCRIPTION, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT, TBC_CESS_AMT,
						TBC_NET_TOTAL, TBC_NET_FINAL };
			} else {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME,TBC_DESCRIPTION, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT,
						TBC_NET_TOTAL, TBC_NET_FINAL };
			}
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME,TBC_DESCRIPTION, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
					TBC_NET_PRICE, TBC_NET_FINAL };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		/*
		 * if (!isManufDateEnable()) { templist.remove(TBC_MANUFACT_DATE);
		 * templist.remove(TBC_EXPIRE_DATE); }
		 */

		requiredHeaders = templist.toArray(new String[templist.size()]);

		setSize(1260, 605);

		session = getHttpSession();

		daoObj = new TailoringSalesDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		// isPaymentNow=new SCheckBox("Receiving Cash", true);

		creditPeriodTextField = new STextField(null, 100);

		pannel = new SPanel();
		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

//		SGridLayout dateGrid = new SGridLayout();
//		dateGrid.setSizeFull();
//		dateGrid.setColumns(10);
//		dateGrid.setRows(1);

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(9);
		addingGrid.setRows(2);
//		addingGrid.setStyleName("po_border");

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(4);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(20);
		bottomGrid.setRows(4);
		bottomGrid.setStyleName("po_border");

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(8);
		buttonsGrid.setRows(1);

		SHorizontalLayout specLay = new SHorizontalLayout();
		specLay.setSpacing(true);

		SGridLayout commentsLay = new SGridLayout();
		commentsLay.setSpacing(true);
		commentsLay.setColumns(6);
		commentsLay.setRows(1);

		specFieldLay = new SHorizontalLayout();
		specFieldLay.setSpacing(true);

		SVerticalLayout verLay = new SVerticalLayout();
		verLay.setSpacing(true);
		verLay.setStyleName("po_border");

		qtyTotal = new SLabel(null);
		taxTotal = new SLabel(null);
		netTotal = new SLabel(null);
		qtyTotal.setValue("0.0");
		taxTotal.setValue("0.0");
		netTotal.setValue("0.0");

		pannel.setSizeFull();
		form.setSizeFull();

		addSOButton = new SButton(null, "Add");
		addSOButton.setStyleName("updateItemBtnStyle");

		try {

			newSaleButton = new SButton();
			newSaleButton.setStyleName("createNewBtnStyle");
			newSaleButton.setDescription("Add new Sale");

			priceListButton = new SButton();

			employSelect = new SComboField(
					null,
					125,
					usrDao.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
							getOfficeID(), getOrganizationID()), "id",
					"first_name",true,getPropertyName("select"));

			employSelect.setValue(getLoginID());

			if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin())
				employSelect.setReadOnly(true);
			
			
			productionUnitSelect=new SComboField(null,280,new ProductionUnitDao().getAllProductionUnitsInPriorityOrder(getOfficeID()),"id","name",false,getPropertyName("select"));
			materialSourceRadioButton=new SRadioButton(null,150,SConstants.materialSource.materialSource,"intKey","value");
			materialSourceRadioButton.setValue(SConstants.materialSource.STOCK);
			materialSourceRadioButton.setHorizontal(true);

			List list = new ArrayList();
			list.add(new TailoringSalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));
			salesNumberList = new SComboField(null, 125, list, "id",
					"comments", false, getPropertyName("create_new"));
			barcodeField=new STextField(null,120);
			barcodeField.setInputPrompt(getPropertyName("barcode"));

			date = new SDateField(null, 110, getDateFormat(), getWorkingDate());

			customerSelect = new SComboField(
					null,
					220,
					custDao.getAllActiveCustomerNamesWithLedgerID(getOfficeID()),
					"id", "name", true, getPropertyName("select"));

			salesTypeSelect = new SNativeSelect(null, 120,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");
			
			Iterator itt = salesTypeSelect.getItemIds().iterator();
			if (itt.hasNext())
				salesTypeSelect.setValue(itt.next());

			// cashOrCreditRadio = new SRadioButton(null, 300,
			// SConstants.paymentModeList, "key", "value");

			refNoField = new STextField(null, 120);
			refNoField.setValue("0");
			
			exp_delv_date = new SDateField(null, 110, getDateFormat(),getWorkingDate());
			act_delv_date = new SDateField(null, 110, getDateFormat(),getWorkingDate());

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesNumberList);
			salLisrLay.addComponent(newSaleButton);
			if(settings.isBARCODE_ENABLED())
				salLisrLay.addComponent(barcodeField);
			barcodeField.focus();
			
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("employee")), 3, 0);
			masterDetailsGrid.addComponent(employSelect, 4, 0);
			
			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 1);
			masterDetailsGrid.addComponent(refNoField, 4, 1);

			// masterDetailsGrid.addComponent(cashOrCreditRadio, 3, 0);
			//
			// cashOrCreditRadio.setStyleName("radio_horizontal");

			// masterDetailsGrid.setComponentAlignment(netTotal,
			// Alignment.MIDDLE_RIGHT);


			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("customer")), 1, 1);

			newCustomerButton = new SButton();
			newCustomerButton.setStyleName("addNewBtnStyle");
			newCustomerButton.setDescription("Add new Customer");

			loadAllSuppliersButton = new SButton();
			loadAllSuppliersButton.setStyleName("loadAllBtnStyle");
			loadAllSuppliersButton
					.setDescription("Load Customers under all offices.");
			loadAllSuppliersButton.setId("ALL");

			SHorizontalLayout hrl = new SHorizontalLayout();
			hrl.addComponent(customerSelect);
			hrl.addComponent(newCustomerButton);
			hrl.addComponent(loadAllSuppliersButton);

			masterDetailsGrid.addComponent(hrl, 2, 1);
			
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			masterDetailsGrid.addComponent(date, 6, 0);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("expected_delivery_date")),5, 1);
			masterDetailsGrid.addComponent(exp_delv_date, 6, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("actual_delivery_date")),5, 2);
			masterDetailsGrid.addComponent(act_delv_date, 6,2);

			
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("production_unit")),1,2);
			masterDetailsGrid.addComponent(productionUnitSelect,2,2);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("material_source")),3,2);
			masterDetailsGrid.addComponent(materialSourceRadioButton,4,2);


			/*
			 * masterDetailsGrid.addComponent(new SLabel("Employ :"), 4, 1);
			 * masterDetailsGrid.addComponent(employSelect, 5, 1);
			 */

			// newCustomerButton.setStyleName("v-button-link");
			// masterDetailsGrid.addComponent(newCustomerButton, 3, 1);

			// masterDetailsGrid.addComponent(new SLabel("Sales Type :"), 6, 1);
			// masterDetailsGrid.addComponent(salesTypeSelect, 8, 1);

			// masterDetailsGrid.addComponent(new SLabel("Max Credit Period :"),
			// 0, 2);
			// masterDetailsGrid.addComponent(creditPeriodTextField, 2, 2);


			masterDetailsGrid.setStyleName("master_border");
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 3);
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
			
			description= new STextField(getPropertyName("description"), 130);
			description.setMaxLength(300);

			quantityTextField = new STextField(getPropertyName("quantity"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60, null,
					"id", "symbol");
			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setValue("0.00");
			unitPriceTextField.setStyleName("textfield_align_right");

			if (taxEnable) {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80,
						taxDao.getAllActiveTaxesFromType(getOfficeID(),
								SConstants.tax.SALES_TAX), "id", "name");
				taxSelect.setVisible(true);
			} else {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80, null,
						"id", "name");
				taxSelect.setVisible(false);
			}

			netPriceTextField = new STextField(getPropertyName("net_price"),
					100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("textfield_align_right");

			itemSelectCombo = new SComboField(getPropertyName("item"), 250,
					daoObj.getAllItemsWithRealStck(getOfficeID()), "id",
					"name", true, getPropertyName("select"));

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
			hrz1.addComponent(itemSelectCombo);
			hrz1.addComponent(newItemButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);

			addingGrid.addComponent(hrz1);
			addingGrid.addComponent(description);

			addingGrid.addComponent(quantityTextField);

			SHorizontalLayout hrz2 = new SHorizontalLayout();
			hrz2.addComponent(unitSelect);

			popupHor = new SHorizontalLayout();
			popupHor.addComponent(unitPriceTextField);
			popupHor.addComponent(priceListButton);
			popupHor.setComponentAlignment(priceListButton,
					Alignment.BOTTOM_LEFT);
			addingGrid.addComponent(popupHor);

			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			vert.setSpacing(false);
			hrz2.addComponent(vert);
			hrz2.setComponentAlignment(vert, Alignment.MIDDLE_CENTER);
			hrz2.setSpacing(true);
			addingGrid.addComponent(hrz2);

			priceListButton.setDescription("Price History");
			priceListButton.setStyleName("showHistoryBtnStyle");

			specGroup = new SOptionGroup(getPropertyName("specifications"), 300,
					specDao.getSpecOfType(getOrganizationID(),
							SConstants.tailoring.TYPE_CHECKBOX), "id", "name",
					true);
			specGroup.setStyleName("checkbox_horizontal");
			specLay.addComponent(specGroup);

			try {
				specArrayList = specDao.getSpecOfType(getOrganizationID(),
						SConstants.tailoring.TYPE_TEXT);
			} catch (Exception e) {
				e.printStackTrace();
			}

			loadSpecFields("");

			verLay.addComponent(addingGrid);
			verLay.addComponent(specLay);
			verLay.addComponent(specFieldLay);

			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 2);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(7, 3);
			addingGrid.setColumnExpandRatio(8, 3);

			addingGrid.setWidth("1200");

			addingGrid.setSpacing(true);

			form.setStyleName("po_style");

			table = new STable(null, 1000, 100);

			table.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_DESCRIPTION, String.class, null,
					getPropertyName("description"), null, Align.LEFT);
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
					getPropertyName("tax_percentage"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_AMT, Double.class, null,
					getPropertyName("tax_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null,
					getPropertyName("net_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PO_ID, Long.class, null, TBC_PO_ID,
					null, Align.CENTER);
			table.addContainerProperty(TBC_INV_ID, Long.class, null,
					TBC_INV_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CESS_AMT, Double.class, null,
					getPropertyName("cess"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_TOTAL, Double.class, null,
					getPropertyName("net_total"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_FINAL, Double.class, null,
					getPropertyName("final_amount"), null, Align.RIGHT);
			/*
			 * table.addContainerProperty(TBC_MANUFACT_DATE, Date.class, null,
			 * TBC_MANUFACT_DATE, null, Align.RIGHT);
			 * table.addContainerProperty(TBC_EXPIRE_DATE, Date.class, null,
			 * TBC_EXPIRE_DATE, null, Align.RIGHT);
			 * table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
			 * TBC_STOCK_ID, null, Align.RIGHT);
			 */
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNI, Double.class,
					null, getPropertyName("qty_basic_unit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_STK_IDS, String.class, null,
					TBC_STK_IDS, null, Align.RIGHT);
			table.addContainerProperty(TBC_SPEC_FIELD_ID, String.class, null,
					TBC_SPEC_FIELD_ID, null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 2.5f);
			table.setColumnExpandRatio(TBC_DESCRIPTION, 2.5f);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, (float) 1.3);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 2);
			table.setColumnExpandRatio(TBC_NET_TOTAL, 2);
			table.setColumnExpandRatio(TBC_NET_FINAL, 2);
			table.setColumnExpandRatio(TBC_CESS_AMT, (float) 0.8);
			// table.setColumnExpandRatio(TBC_MANUFACT_DATE, (float) 1.1);
			// table.setColumnExpandRatio(TBC_EXPIRE_DATE, (float) 1.1);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);
			// table.setEditable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1200");
			table.setHeight("100");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			grandTotalAmtTextField = new STextField(
					getPropertyName("net_amount"), 100, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");

			shippingChargeTextField = new STextField(
					getPropertyName("shipping_charge"), 100, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(
					getPropertyName("excise_duty"), 100, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

			discountTextField = new STextField(getPropertyName("discount"), 80,
					"0.0");
			discountTextField.setValue("0.00");
			discountTextField.setStyleName("textfield_align_right");

			chargeTextField = new STextField(getPropertyName("charges"), 80,
					"0.0");
			chargeTextField.setValue("0.00");
			chargeTextField.setStyleName("textfield_align_right");

			balanceField = new STextField(getPropertyName("balance_amount"),
					80, "0.0");
			balanceField.setValue("0.00");
			balanceField.setStyleName("textfield_align_right");
			balanceField.setReadOnly(true);

			payingAmountTextField = new STextField(
					getPropertyName(getPropertyName("advance_amount")), 100);
			payingAmountTextField.setValue("0.00");
			payingAmountTextField.setStyleName("textfield_align_right");

//			advanceAmountField = new STextField(
//					getPropertyName("advance_amount"), 100);
//			advanceAmountField.setValue("0.00");
//			advanceAmountField.setStyleName("textfield_align_right");

			comment = new STextArea(getPropertyName("comment"), 400, 30);
			comment.setInputPrompt(getPropertyName("comment"));
			paymentModeField=new SNativeSelect(getPropertyName("payment_mode"),120,SConstants.paymentModes.paymentModes,"intKey","value");

			toAccountComboField=new SComboField(getPropertyName("to_account"),150);
			toAccountComboField.setInputPrompt("Select");
			
			if (isExciceDutyEnable()) {
				bottomGrid.addComponent(exciseDutyTextField, 4, 1);
				bottomGrid.setComponentAlignment(exciseDutyTextField,
						Alignment.TOP_RIGHT);

			}
			if (isShippingChargeEnable()) {
				bottomGrid.addComponent(shippingChargeTextField, 3, 1);
				bottomGrid.setComponentAlignment(shippingChargeTextField,
						Alignment.TOP_RIGHT);

			}
			// if (isDiscountEnable()) {
			// bottomGrid.addComponent(new SLabel("Discount :"), 0, 0);
			// bottomGrid.addComponent(discountTextField, 1, 0);
			// }
			bottomGrid.addComponent(discountTextField, 2, 0);

			bottomGrid.addComponent(chargeTextField, 4, 0);

			bottomGrid.addComponent(grandTotalAmtTextField, 6, 0);

//			bottomGrid.addComponent(advanceAmountField, 9, 0);

			bottomGrid.addComponent(payingAmountTextField, 9, 0);

			bottomGrid.addComponent(balanceField, 10, 0);
			
			bottomGrid.addComponent(paymentModeField, 11, 0);
			bottomGrid.addComponent(toAccountComboField,12, 0);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.MIDDLE_CENTER);
			
			newproductionUnitSelect=new SComboField("Production Unit",250,new ProductionUnitDao().getAllProductionUnitsInPriorityOrder(getOfficeID()),"id","name",false,"Select");
			changeProdWindowButton=new SButton("Change");
			cancelProdWindowButton=new SButton("Cancel");
			prodWindowFormLayout=new SFormLayout();
			prodWindowFormLayout.setSpacing(true);
			prodWindowFormLayout.setMargin(true);
			SHorizontalLayout btnHlay=new SHorizontalLayout();
			btnHlay.setSpacing(true);
			btnHlay.addComponent(changeProdWindowButton);
			btnHlay.addComponent(cancelProdWindowButton);
			prodWindowFormLayout.addComponent(newproductionUnitSelect);
			prodWindowFormLayout.addComponent(btnHlay);
			
			saveSalesButton = new SButton(getPropertyName("save"), 70);
			saveSalesButton.setStyleName("savebtnStyle");
			saveSalesButton
					.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateSalesButton = new SButton(getPropertyName("update"), 80);
			updateSalesButton.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updateSalesButton.setStyleName("updatebtnStyle");

			deleteSalesButton = new SButton(getPropertyName("delete"), 78);
			deleteSalesButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteSalesButton.setStyleName("deletebtnStyle");
			
			cancelButton = new SButton(getPropertyName("Cancel Order"));
			cancelButton.setVisible(false);
			cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			cancelButton.setStyleName("deletebtnStyle");

			deliverSalesButton = new SButton(getPropertyName("deliver"), 78);
			deliverSalesButton.setIcon(new ThemeResource("icons/deliver.png"));
			deliverSalesButton.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveSalesButton);
			mainButtonLayout.addComponent(updateSalesButton);
			mainButtonLayout.addComponent(deleteSalesButton);
			mainButtonLayout.addComponent(cancelButton);
			mainButtonLayout.addComponent(deliverSalesButton);
			updateSalesButton.setVisible(false);
			deleteSalesButton.setVisible(false);
			cancelButton.setVisible(false);
			deliverSalesButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			// Added by anil
			printButton = new SButton(getPropertyName("print"));
			printButton.setIcon(new ThemeResource("icons/print.png"));
			mainButtonLayout.addComponent(printButton);
			printButton.setVisible(false);
			
			changeProdUnitButton=new SButton("Change Production Unit");
			mainButtonLayout.addComponent(changeProdUnitButton);
			changeProdUnitButton.setVisible(false);

			statusLabel = new SLabel();
			statusLabel.setStyleName(Reindeer.LABEL_H2);
			statusLabel.setVisible(false);

			commentsLay.addComponent(comment, 1, 0);
			commentsLay.addComponent(statusLabel, 2, 0);
			commentsLay.setComponentAlignment(statusLabel, Alignment.MIDDLE_CENTER);

			form.addComponent(masterDetailsGrid);
//			form.addComponent(dateGrid);
			form.addComponent(table);
//			form.addComponent(addingGrid);
			form.addComponent(verLay);
			form.addComponent(bottomGrid);
			form.addComponent(commentsLay);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

//			customerSelect.focus();

			pannel.setContent(hLayout);

			Object a = table.getValue();
			Collection aa = (Collection) a;

			salesOrdersOptions = new SOptionGroup(getPropertyName("select_SO"),
					300, null, "id", "ref_no", true);
			poWindow = new SDialogBox(getPropertyName("sales_orders"), 400, 400);
			poWindow.center();
			poWindow.setResizable(false);
			poWindow.setModal(true);
			poWindow.setCloseShortcut(KeyCode.ESCAPE);
			SFormLayout fr1 = new SFormLayout();
			fr1.addComponent(salesOrdersOptions);
			fr1.addComponent(addSOButton);
			poWindow.addComponent(fr1);

			newCustomerWindow = new SDialogBox(getPropertyName("add_customer"),
					700, 600);
			newCustomerWindow.center();
			newCustomerWindow.setResizable(false);
			newCustomerWindow.setModal(true);
			newCustomerWindow.setCloseShortcut(KeyCode.ESCAPE);
			salesCustomerPanel = new SalesCustomerPanel();
			newCustomerWindow.addComponent(salesCustomerPanel);

			newItemWindow = new SDialogBox("Add Item", 500, 600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			newItemWindow.addComponent(itemPanel);

			// Iterator itr=cashOrCreditRadio.getItemIds()
			// .iterator();
			// itr.next();
			// cashOrCreditRadio.setValue(itr.next());
			
			addingGrid.addShortcutListener(new ShortcutListener("Submit Item",
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
					loadSale(0);
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
			
//			paymentModeField.addValueChangeListener(new ValueChangeListener() {
//				
//				@Override
//				public void valueChange(ValueChangeEvent event) {
//					try {
//						toAccountComboField.setVisible(true);
//						List groupList = new ArrayList();
//						SCollectionContainer container;
//						if ((Integer)paymentModeField.getValue()==SConstants.paymentModes.CASH){
//							groupList.add(SConstants.CASH_GROUP);
//							container = SCollectionContainer.setList(ledgDao
//									.getAllLedgersUnderGroupAndSubGroupsFromGroupList(
//											getOfficeID(), getOrganizationID(), groupList), "id");
//						}else if((Integer)paymentModeField.getValue()==SConstants.paymentModes.CHEQUE){
//							groupList.add(SConstants.BANK_ACCOUNT_GROUP_ID);
//							container = SCollectionContainer.setList(ledgDao
//									.getAllLedgersUnderGroupAndSubGroupsFromGroupList(
//											getOfficeID(), getOrganizationID(), groupList), "id");
//						}else if((Integer)paymentModeField.getValue()==SConstants.paymentModes.CREDIT_CARD){
//							groupList.add(SConstants.CASH_GROUP);
//							groupList.add(SConstants.BANK_ACCOUNT_GROUP_ID);
//							container = SCollectionContainer.setList(ledgDao
//									.getAllLedgersUnderGroupAndSubGroupsFromGroupList(
//											getOfficeID(), getOrganizationID(), groupList), "id");
//						}else{
//							groupList.add(ledgDao.getLedgeer(settings.getCASH_RECEIVABLE_ACCOUNT()));
//							container = SCollectionContainer.setList(groupList, "id");
//							toAccountComboField.setVisible(false);
//						}
//						
//						toAccountComboField.setContainerDataSource(container);
//						toAccountComboField.setItemCaptionPropertyId("name");
//						toAccountComboField.setValue(toAccountComboField.getItemIds().iterator().next());
//							
//					} catch (Exception e) {
//					}
//				}
//			});
			
			paymentModeField.setValue(SConstants.paymentModes.CREDIT);

			specGroup.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					double netPrice = 0;
					try {
						calculateNetPrice((Set) event.getProperty().getValue());
						// if (set != null && !set.isEmpty()) {
						// calculateNetPrice(set);
						// netPrice=toDouble(netPriceTextField.getValue());
						// netPrice += specDao.getSpecPrice(set);
						//
						// } else {
						// set = new HashSet(specGroup.getContainerDataSource()
						// .getItemIds());
						// calculateNetPrice();
						// netPrice=toDouble(netPriceTextField.getValue());
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}
					// netPriceTextField.setNewValue(asString(netPrice));

				}
			});

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesNumberList.setValue(null);
				}
			});

			priceListButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub

					if (itemSelectCombo.getValue() != null
							&& unitSelect.getValue() != null) {

						try {

							STable table = new STable("");

							/*
							 * Define the names and data types of columns. The
							 * "default value" parameter is meaningless here.
							 */
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
									(Long) itemSelectCombo.getValue(),
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

							SPopupView pop = new SPopupView("", table);

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

			loadAllSuppliersButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					List list;
					try {

						if (loadAllSuppliersButton.getId().equals("ALL")) {
							list = custDao
									.getAllActiveCustomerNamesWithOrgID(getOrganizationID());
							loadAllSuppliersButton.setId("CURRENT");
							loadAllSuppliersButton
									.setDescription("Load Customers under this office.");
						} else {
							list = custDao
									.getAllActiveCustomerNamesWithLedgerID(getOfficeID());
							loadAllSuppliersButton
									.setDescription("Load Customers under all offices.");
							loadAllSuppliersButton.setId("ALL");
						}

						SCollectionContainer bic = SCollectionContainer
								.setList(list, "id");
						customerSelect.setContainerDataSource(bic);
						customerSelect.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			newItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
					itemPanel.loadOptions(0);
					getUI().getCurrent().addWindow(newItemWindow);
					newItemWindow.setCaption("Add New Item");
				}
			});

			newUnitButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					popupWindow.setContent(new AddNewUnitUI());

					popupWindow.setWidth("502");
					popupWindow.setHeight("455");

					popupWindow.center();
					popupWindow.setModal(true);

					popupWindow.setCaption("Add New Unit");

					getUI().getCurrent().addWindow(popupWindow);
				}
			});

			unitMapButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					long itemId = 0;

					if (itemSelectCombo.getValue() != null) {
						ItemModel itm = null;
						try {
							itm = daoObj.getItem((Long) itemSelectCombo
									.getValue());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						itemId = itm.getId();
					}

					popupWindow.setContent(new SetUnitManagementUI(itemId,
							(Long) salesTypeSelect.getValue(), true));

					popupWindow.setWidth("910");
					popupWindow.setHeight("498");

					popupWindow.setCaption("Unit Management");

					popupWindow.center();
					popupWindow.setModal(true);
					getUI().getCurrent().addWindow(popupWindow);

				}
			});

			newItemWindow.addCloseListener(new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					reloadItemStocks();
				}
			});

			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					resetUnit();
				}
			});

			newCustomerButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					salesCustomerPanel.clearFields();
					getUI().getCurrent().addWindow(newCustomerWindow);
				}
			});

			newCustomerWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					reloadCustomers();
				}
			});
			// salesCustomerPanel.get
			// saveNewCustButton.addClickListener(new ClickListener() {
			//
			// @Override
			// public void buttonClick(ClickEvent event) {
			// salesCustomerPanel.ge
			// }
			// });

			customerSelect.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						salesOrdersOptions.setContainerDataSource(null);

						if (customerSelect.getValue() != null) {
							CustomerModel cust = custDao
									.getCustomerFromLedger((Long) customerSelect
											.getValue());
							if (cust != null) {
								creditPeriodTextField.setValue(asString(cust
										.getMax_credit_period()));
//								customerSelect
//										.setDescription("<h1><i>Current Balance</i> : "
//												+ cust.getLedger()
//														.getCurrent_balance()
//												+ "</h1>");
							}
						} else
							customerSelect.setDescription(null);

						/*if (session.getAttribute("SO_Select_Disabled") == null) {

							if (session.getAttribute("SO_Already_Added") != null) {
								table.removeAllItems();
								session.removeAttribute("SO_Already_Added");
							}

							if (customerSelect.getValue() != null) {

								List list = daoObj
										.getAllSalesOrdersForCustomer(
												(Long) customerSelect
														.getValue(),
												getOfficeID());

								if (list.size() > 0) {

									SCollectionContainer bic = SCollectionContainer
											.setList(list, "id");
									salesOrdersOptions
											.setContainerDataSource(bic);
									salesOrdersOptions
											.setItemCaptionPropertyId("ref_no");

									getUI().getCurrent().addWindow(poWindow);

									if (salesOrdersOptions.getItemIds().size() > 0) {
										salesOrdersOptions.focus();
										setRequiredError(salesOrdersOptions,
												null, false);
									} else {
										setRequiredError(
												salesOrdersOptions,
												getPropertyName("invalid_data"),
												true);
										addSOButton.focus();
									}

								}
							} else {
								setRequiredError(table, null, false);
							}

						} else {
							session.removeAttribute("SO_Select_Disabled");
						}*/

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}

			});

			addSOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						removeAllErrors();

						if (salesOrdersOptions.getValue() != null
								&& ((Set<Long>) salesOrdersOptions.getValue())
										.size() > 0) {

							table.removeAllItems();

							session.setAttribute("SO_Already_Added", 1);

							table.setVisibleColumns(allHeaders);

							Set<Long> SOs = (Set<Long>) salesOrdersOptions
									.getValue();

							List itemsList = daoObj
									.getAllItemsFromSalesOrders(SOs);

							int id = 0, ct = table.getItemIds().size();
							Iterator it1 = table.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							InventoryDetailsPojo invObj;
							double netTotal = 0, ttl_bfr_tax, ttl_bfr_disc, perc;
							Iterator it = itemsList.iterator();
							while (it.hasNext()) {
								invObj = (InventoryDetailsPojo) it.next();

								netTotal = 0;
								ttl_bfr_tax = 0;
								ttl_bfr_disc = 0;
								if (invObj.getTax_percentage() > 0) {
									perc = roundNumber((invObj.getUnit_price() * invObj
											.getBalance())
											* invObj.getTax_percentage() / 100);
									netTotal = roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ perc
											- invObj.getDiscount_amount());
									ttl_bfr_tax = roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance()));
									ttl_bfr_disc = roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ perc);
								} else {
									netTotal = roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ invObj.getTax_amount()
											- invObj.getDiscount_amount());
									ttl_bfr_tax = roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance()));
									ttl_bfr_disc = roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ invObj.getTax_amount());
								}

								id++;
								ct++;

								table.addItem(
										new Object[] {
												ct,
												invObj.getItem_id(),
												invObj.getItem_code(),
												invObj.getItem_name(),"",
												invObj.getBalance(),
												invObj.getUnit_id(),
												invObj.getUnit_name(),
												invObj.getUnit_price(),
												invObj.getTax_id(),
												invObj.getTax_amount(),
												invObj.getTax_percentage(),
												invObj.getDiscount_amount(),
												ttl_bfr_tax,
												invObj.getOrder_id(),
												invObj.getInventry_details_id(),
												invObj.getCess_amount(),
												roundNumber(ttl_bfr_disc
														+ invObj.getCess_amount()),
												roundNumber(netTotal
														+ invObj.getCess_amount()),
												invObj.getQuantity_in_basic_unit() },
										id);

							}

							table.setVisibleColumns(requiredHeaders);

							getUI().removeWindow(poWindow);

							calculateTotals();

						} else {
							getUI().removeWindow(poWindow);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});
			barcodeListener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(barcodeField.getValue()!=null&&barcodeField.getValue().toString().trim().length()>0){
						try {
							long id=daoObj.getSalesIdFromBarcode(barcodeField.getValue().toString(),getOfficeID());
							if(id!=0)
								salesNumberList.setValue(id);
						} catch (Exception e) {
						}
					}
				}
			};
			barcodeField.addValueChangeListener(barcodeListener);

			saveSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							long customer_id = (Long) customerSelect.getValue();

							double costof_inv_amt = 0;

							TailoringSalesModel salObj = new TailoringSalesModel();

							List<TailoringSalesInventoryDetailsModel> itemsList = new ArrayList<TailoringSalesInventoryDetailsModel>();

							TailoringSalesInventoryDetailsModel invObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new TailoringSalesInventoryDetailsModel();
								item = table.getItem(it.next());

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setDescription( item
										.getItemProperty(TBC_DESCRIPTION).getValue().toString());

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

								invObj.setOrder_id((Long) item.getItemProperty(
										TBC_PO_ID).getValue());
								invObj.setCess_amount((Double) item
										.getItemProperty(TBC_CESS_AMT)
										.getValue());

								/*
								 * invObj.setManufacturing_date(CommonUtil
								 * .getSQLDateFromUtilDate((Date) item
								 * .getItemProperty( TBC_MANUFACT_DATE)
								 * .getValue()));
								 * invObj.setExpiry_date(CommonUtil
								 * .getSQLDateFromUtilDate((Date) item
								 * .getItemProperty( TBC_EXPIRE_DATE)
								 * .getValue())); invObj.setStock_id((Long)
								 * item.getItemProperty(
								 * TBC_STOCK_ID).getValue());
								 */

								invObj.setId((Long) item.getItemProperty(
										TBC_INV_ID).getValue());

								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
										.getValue());

								invObj.setStock_ids(asString(item
										.getItemProperty(TBC_STK_IDS)
										.getValue()));

								invObj.setSpec_field_ids(asString(item
										.getItemProperty(TBC_SPEC_FIELD_ID)
										.getValue()));

								itemsList.add(invObj);

								std_cost = itemDao.getStandardCost(invObj
										.getItem().getId());
								costof_inv_amt += invObj.getQunatity()
										* std_cost;

							}
							// if (isDiscountEnable()) {
							// salObj.setDiscount(toDouble(discountTextField
							// .getValue()));
							// }
							if (isExciceDutyEnable()) {
								salObj.setExcise_duty(toDouble(exciseDutyTextField
										.getValue()));
							}
							if (isShippingChargeEnable()) {
								salObj.setShipping_charge(toDouble(shippingChargeTextField
										.getValue()));
							}

							salObj.setCreated_time(CommonUtil
									.getCurrentDateTime());

							salObj.setCredit_period(toInt(creditPeriodTextField
									.getValue()));
							salObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue()));
//									+ toDouble(advanceAmountField.getValue()));
							salObj.setDiscount(toDouble(discountTextField
									.getValue()));
							salObj.setCharges(toDouble(chargeTextField
									.getValue()));
							salObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							salObj.setAdvance_amount(toDouble(payingAmountTextField
									.getValue()));

							salObj.setComments(comment.getValue());
							salObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							salObj.setExpected_delivery_date(CommonUtil
									.getSQLDateFromUtilDate(exp_delv_date
											.getValue()));
							salObj.setActual_delivery_date(CommonUtil
									.getSQLDateFromUtilDate(act_delv_date
											.getValue()));
							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							salObj.setLogin(new S_LoginModel(getLoginID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							salObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							salObj.setTailoring_inventory_details_list(itemsList);

							salObj.setSales_number(getNextSequence(
									"Sales Number", getLoginID()));

							salObj.setSales_person((Long) employSelect
									.getValue());
							salObj.setVoucher_no(toLong(refNoField.getValue()));
							salObj.setDelivery_status((long) 1);
							salObj.setProductionUnit(new ProductionUnitModel((Long)productionUnitSelect.getValue()));
							salObj.setMaterialSource(toInt(materialSourceRadioButton.getValue().toString()));
							salObj.setPaymentMode(1);
							salObj.setPaymentMode((Integer)paymentModeField.getValue());
							salObj.setPaymentAccount(toLong(toAccountComboField.getValue().toString()));
							salObj.setActive(true);
							salObj.setBarcode(barcodeField.getValue().toString());
							
							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField.getValue());
//									+ toDouble(advanceAmountField.getValue());
							long toAccId=toLong(toAccountComboField.getValue().toString());
							
							salObj.setPayment_done('N');

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										toAccId,
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));

								salObj.setStatus(1);
								salObj.setPayment_done('Y');
								status = 1;
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								salObj.setStatus(2);
								status = 2;
							} else {
								trans.addTransaction(SConstants.CR,
										customer_id,
										toAccId,
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								status = 3;
								salObj.setStatus(3);
							}

							if (settings.getINVENTORY_ACCOUNT() != 0) {
								trans.addTransaction(SConstants.CR,
										settings.getINVENTORY_ACCOUNT(),
										settings.getCGS_ACCOUNT(),
										roundNumber(costof_inv_amt));
								totalAmt -= costof_inv_amt;
							}
							
							

							if (taxEnable) {
								if (settings.getSALES_TAX_ACCOUNT() != 0) {
									amt = toDouble(table
											.getColumnFooter(TBC_TAX_AMT));
									if (amt != 0) {
										trans.addTransaction(
												SConstants.CR,
												settings.getSALES_TAX_ACCOUNT(),
												settings.getCGS_ACCOUNT(),
												roundNumber(amt));
										totalAmt -= amt;
									}
								}

								if (settings.isCESS_ENABLED()) {
									if (settings.getCESS_ACCOUNT() != 0) {
										amt = toDouble(table
												.getColumnFooter(TBC_CESS_AMT));
										if (amt != 0) {
											trans.addTransaction(SConstants.CR,
													settings.getCESS_ACCOUNT(),
													settings.getCGS_ACCOUNT(),
													roundNumber(amt));
											totalAmt -= amt;
										}
									}
								}
							}

							if (settings.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
								amt = toDouble(exciseDutyTextField.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_EXCISE_DUTY_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}

							}

							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
								amt = toDouble(shippingChargeTextField
										.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
								amt = toDouble(shippingChargeTextField
										.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_DESCOUNT_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_REVENUE_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(totalAmt));
								}
							}

							long id = daoObj.save(salObj, trans.getTransaction(
									SConstants.SALES, CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue())), payingAmt);

							saveActivity(
									getOptionId(),
									"New Sales Created. Bill No : "
											+ salObj.getSales_number()
											+ ", Customer : "
											+ customerSelect
													.getItemCaption(customerSelect
															.getValue())
											+ ", Amount : "
											+ salObj.getAmount(),salObj.getId());

							loadSale(id);

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}

			});

			salesNumberList.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {

						removeAllErrors();

						updateSalesButton.setVisible(true);
						deleteSalesButton.setVisible(true);
						cancelButton.setVisible(true);
						deliverSalesButton.setVisible(true);
						printButton.setVisible(true);
						changeProdUnitButton.setVisible(true);
						saveSalesButton.setVisible(false);
						if (salesNumberList.getValue() != null
								&& !salesNumberList.getValue().toString()
										.equals("0")) {

							TailoringSalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());

							table.setVisibleColumns(allHeaders);

							table.removeAllItems();

							TailoringSalesInventoryDetailsModel invObj;
							double netTotal;
							double specAmount;
							String specString = "";
							String specFieldString = "";
							Iterator it = salObj
									.getTailoring_inventory_details_list()
									.iterator();
							while (it.hasNext()) {
								invObj = (TailoringSalesInventoryDetailsModel) it
										.next();

								specString = daoObj.getSpecString(invObj
										.getId());

								specFieldString = daoObj
										.getSpecFieldString(invObj.getId());

								specAmount = specDao
										.getSpecPrice(getSpecSet(specString));

								netTotal = roundNumber((invObj.getUnit_price() * invObj
										.getQunatity())
										+ invObj.getTax_amount()
										+ invObj.getCess_amount() + specAmount);

								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,
												invObj.getItem().getId(),
												invObj.getItem().getItem_code(),
												invObj.getItem().getName(),invObj.getDescription(),
												invObj.getQunatity(),
												invObj.getUnit().getId(),
												invObj.getUnit().getSymbol(),
												invObj.getUnit_price(),
												invObj.getTax().getId(),
												invObj.getTax_amount(),
												invObj.getTax_percentage(),
												(invObj.getUnit_price() * invObj
														.getQunatity()),
												invObj.getOrder_id(),
												(long) 0,
												invObj.getCess_amount(),
												netTotal,
												netTotal,
												invObj.getQuantity_in_basic_unit(),
												specString, specFieldString },
										table.getItemIds().size() + 1);
							}

							table.setVisibleColumns(requiredHeaders);

							grandTotalAmtTextField.setNewValue(asString(salObj
									.getAmount()));
							// buildingSelect.setValue(salObj.getBuilding().getId());
							comment.setValue(salObj.getComments());
							date.setValue(salObj.getDate());
							exp_delv_date.setValue(salObj
									.getExpected_delivery_date());
							act_delv_date.setValue(salObj
									.getActual_delivery_date());
							// expected_delivery_date.setValue(salObj.getExpected_delivery_date());

							session.setAttribute("SO_Select_Disabled", 'Y');

							customerSelect.setValue(salObj.getCustomer()
									.getId());

							employSelect.setNewValue(salObj.getSales_person());

							creditPeriodTextField.setValue(asString(salObj
									.getCredit_period()));
							refNoField.setValue(asString(salObj.getVoucher_no()));
							shippingChargeTextField.setValue(asString(salObj
									.getShipping_charge()));
							exciseDutyTextField.setValue(asString(salObj
									.getExcise_duty()));
//							advanceAmountField.setValue(asString(salObj
//									.getAdvance_amount()));
							payingAmountTextField.setValue(asString(salObj
									.getPayment_amount()));
							discountTextField.setValue(asString(salObj
									.getDiscount()));
							chargeTextField.setValue(asString(salObj
									.getCharges()));
							balanceField.setNewValue(asString(salObj
									.getAmount() - salObj.getPayment_amount()));
							
							productionUnitSelect.setNewValue(salObj.getProductionUnit().getId());
							paymentModeField.setNewValue(salObj.getPaymentMode());
							materialSourceRadioButton.setValue(salObj.getMaterialSource());
							
							barcodeField.removeValueChangeListener(barcodeListener);
							barcodeField.setValue(salObj.getBarcode());
							barcodeField.addValueChangeListener(barcodeListener);

							// if (salObj.getPayment_amount() == salObj
							// .getAmount()) {
							// cashOrCreditRadio.setValue((long) 1);
							// } else {
							// cashOrCreditRadio.setValue((long) 2);
							// }

							salObj.setExcise_duty(toDouble(exciseDutyTextField
									.getValue()));

							updateSalesButton.setVisible(true);
							printButton.setVisible(true);
							changeProdUnitButton.setVisible(true);
							deleteSalesButton.setVisible(true);
							cancelButton.setVisible(true);
							deliverSalesButton.setVisible(true);
							saveSalesButton.setVisible(false);
							
							productionUnitSelect.setReadOnly(true);

							status = salObj.getStatus();

							if (salObj.getDelivery_status() == 1)
								statusLabel
										.setValue("Status : Order Collected");
							else
								statusLabel.setValue("Status : Delivered");

							statusLabel.setVisible(true);

						} else {
							table.removeAllItems();

							grandTotalAmtTextField.setNewValue("0.0");
							payingAmountTextField.setValue("0.0");
							balanceField.setNewValue("0.0");
							discountTextField.setValue("0.0");
//							advanceAmountField.setValue("0.0");
							chargeTextField.setValue("0.0");
							// buildingSelect.setValue(null);
							comment.setValue("");
							date.setValue(new Date(getWorkingDate().getTime()));
							act_delv_date.setValue(new Date(getWorkingDate()
									.getTime()));
							exp_delv_date.setValue(new Date(getWorkingDate()
									.getTime()));
							// expected_delivery_date.setValue(new Date());
							customerSelect.setValue(null);
							barcodeField.setValue("");
							employSelect.setNewValue(getLoginID());

							creditPeriodTextField.setValue("0");
							refNoField.setValue("0");
							saveSalesButton.setVisible(true);
							updateSalesButton.setVisible(false);
							printButton.setVisible(false);
							changeProdUnitButton.setVisible(false);
							deleteSalesButton.setVisible(false);
							cancelButton.setVisible(false);
							deliverSalesButton.setVisible(false);
							statusLabel.setValue("");
							statusLabel.setVisible(false);
							
							productionUnitSelect.setReadOnly(false);
							productionUnitSelect.setNewValue(null);
							paymentModeField.setNewValue(SConstants.paymentModes.CREDIT);
							materialSourceRadioButton.setValue(SConstants.materialSource.STOCK);
							
						}

						calculateTotals();

						description.setValue("");
						
						itemSelectCombo.setValue(null);
						itemSelectCombo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");

						specGroup.setValue(null);
						loadSpecFields("");

						customerSelect.focus();
						

						if (!isFinYearBackEntry()) {
							saveSalesButton.setVisible(false);
							updateSalesButton.setVisible(false);
							deleteSalesButton.setVisible(false);
							cancelButton.setVisible(false);
							deliverSalesButton.setVisible(false);
							if (salesNumberList.getValue() == null
									|| salesNumberList.getValue().toString()
											.equals("0")) {
								Notification.show(
										getPropertyName("warning_transaction"),
										Type.WARNING_MESSAGE);
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
					barcodeField.focus();
				}

			});

			updateSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {
							long customer_id = (Long) customerSelect.getValue();

							double costof_inv_amt = 0;

							TailoringSalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());

							List<TailoringSalesInventoryDetailsModel> itemsList = new ArrayList<TailoringSalesInventoryDetailsModel>();

							TailoringSalesInventoryDetailsModel invObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new TailoringSalesInventoryDetailsModel();

								item = table.getItem(it.next());

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setDescription(item
										.getItemProperty(TBC_DESCRIPTION).getValue().toString());

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

								invObj.setOrder_id((Long) item.getItemProperty(
										TBC_PO_ID).getValue());
								invObj.setCess_amount((Double) item
										.getItemProperty(TBC_CESS_AMT)
										.getValue());

								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
										.getValue());

								invObj.setStock_ids(asString(item
										.getItemProperty(TBC_STK_IDS)
										.getValue()));

								invObj.setSpec_field_ids(asString(item
										.getItemProperty(TBC_SPEC_FIELD_ID)
										.getValue()));

								itemsList.add(invObj);

								std_cost = itemDao.getStandardCost(invObj
										.getItem().getId());
								costof_inv_amt += invObj.getQunatity()
										* std_cost;

							}

							if (isExciceDutyEnable()) {
								salObj.setExcise_duty(toDouble(exciseDutyTextField
										.getValue()));
							}
							if (isShippingChargeEnable()) {
								salObj.setShipping_charge(toDouble(shippingChargeTextField
										.getValue()));
							}
							// if (isDiscountEnable()) {
							// salObj.setDiscount(toDouble(discountTextField
							// .getValue()));
							// }

							salObj.setCredit_period(toInt(creditPeriodTextField
									.getValue()));

							salObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue()));
//									+ toDouble(advanceAmountField.getValue()));
							salObj.setDiscount(toDouble(discountTextField
									.getValue()));
							salObj.setCharges(toDouble(chargeTextField
									.getValue()));
							salObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							salObj.setAdvance_amount(toDouble(payingAmountTextField
									.getValue()));
							// salObj.setBuilding(new BuildingModel((Long)
							// buildingSelect.getValue()));
							salObj.setComments(comment.getValue());
							salObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							salObj.setExpected_delivery_date(CommonUtil
									.getSQLDateFromUtilDate(exp_delv_date
											.getValue()));
							salObj.setActual_delivery_date(CommonUtil
									.getSQLDateFromUtilDate(act_delv_date
											.getValue()));
							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							salObj.setLogin(new S_LoginModel(getLoginID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							salObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							salObj.setTailoring_inventory_details_list(itemsList);
							salObj.setVoucher_no(toLong(refNoField.getValue()));
							salObj.setSales_person((Long) employSelect
									.getValue());
							salObj.setDelivery_status((long) 1);
							salObj.setProductionUnit(new ProductionUnitModel((Long)productionUnitSelect.getValue()));
							salObj.setMaterialSource(toInt(materialSourceRadioButton.getValue().toString()));
							salObj.setPaymentMode(1);
							salObj.setPaymentMode((Integer)paymentModeField.getValue());
							salObj.setPaymentAccount(toLong(toAccountComboField.getValue().toString()));
							salObj.setBarcode(barcodeField.getValue().toString());

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField
									.getValue());
//									+ toDouble(advanceAmountField.getValue());
							long toAccId=toLong(toAccountComboField.getValue().toString());

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										toAccId,
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));

								salObj.setStatus(1);
								status = 1;
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								salObj.setStatus(2);
								status = 2;
							} else {
								trans.addTransaction(SConstants.CR,
										customer_id,
										toAccId,
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								status = 3;
								salObj.setStatus(3);
							}

							if (settings.getINVENTORY_ACCOUNT() != 0) {
								trans.addTransaction(SConstants.DR,
										settings.getINVENTORY_ACCOUNT(),
										settings.getCGS_ACCOUNT(),
										roundNumber(costof_inv_amt));
								totalAmt -= costof_inv_amt;
							}

							if (settings.getSALES_TAX_ACCOUNT() != 0) {
								amt = toDouble(table
										.getColumnFooter(TBC_TAX_AMT));
								if (amt != 0) {
									trans.addTransaction(SConstants.DR,
											settings.getSALES_TAX_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							if (taxEnable) {
								if (settings.isCESS_ENABLED()) {
									if (settings.getCESS_ACCOUNT() != 0) {
										amt = toDouble(table
												.getColumnFooter(TBC_CESS_AMT));
										if (amt != 0) {
											trans.addTransaction(SConstants.DR,
													settings.getCESS_ACCOUNT(),
													settings.getCGS_ACCOUNT(),
													roundNumber(amt));
											totalAmt -= amt;
										}
									}
								}

								if (settings.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
									amt = toDouble(exciseDutyTextField
											.getValue());
									if (amt != 0) {
										trans.addTransaction(
												SConstants.CR,
												settings.getSALES_EXCISE_DUTY_ACCOUNT(),
												settings.getCGS_ACCOUNT(),
												roundNumber(amt));
										totalAmt -= amt;
									}
								}
							}

							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
								amt = toDouble(shippingChargeTextField
										.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
								amt = toDouble(shippingChargeTextField
										.getValue());
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amt));
									totalAmt -= amt;
								}
							}

							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
								if (amt != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_REVENUE_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(totalAmt));
								}
							}
							TransactionModel tran = daoObj
									.getTransaction(salObj.getTransaction_id());
							tran.setTransaction_details_list(trans
									.getChildList());
							tran.setDate(salObj.getDate());
							tran.setLogin_id(getLoginID());

							daoObj.update(salObj, tran, payingAmt);

							saveActivity(
									getOptionId(),
									"Sales Updated. Bill No : "
											+ salObj.getSales_number()
											+ ", Customer : "
											+ customerSelect
													.getItemCaption(customerSelect
															.getValue())
											+ ", Amount : "
											+ salObj.getAmount(),salObj.getId());

							loadSale(salObj.getId());

							Notification.show(
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);

						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			deleteSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesNumberList.getValue() != null
							&& !salesNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) salesNumberList
														.getValue());

												saveActivity(
														getOptionId(),
														"Sales Deleted. Bill No : "
																+ salesNumberList
																		.getItemCaption(salesNumberList
																				.getValue())
																+ ", Customer : "
																+ customerSelect
																		.getItemCaption(customerSelect
																				.getValue()),(Long)salesNumberList
																				.getValue());

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);
												loadSale(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});
			
			cancelButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (salesNumberList.getValue() != null
							&& !salesNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.cancel((Long) salesNumberList
														.getValue());

												saveActivity(
														getOptionId(),
														"Sales Cancelled. Bill No : "
																+ salesNumberList
																		.getItemCaption(salesNumberList
																				.getValue())
																+ ", Customer : "
																+ customerSelect
																		.getItemCaption(customerSelect
																				.getValue()),(Long)salesNumberList
																				.getValue());

												Notification
														.show(getPropertyName("Order Cancelled"),
																Type.WARNING_MESSAGE);
												loadSale(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			table.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

					itemSelectCombo.setReadOnly(false);
					unitSelect.setReadOnly(false);

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = table.getItem(selectedItems.iterator()
								.next());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

						if (taxEnable) {
							taxSelect.setValue(item.getItemProperty(TBC_TAX_ID)
									.getValue());
						}

						netPriceTextField.setNewValue(""
								+ item.getItemProperty(TBC_NET_PRICE)
										.getValue());

						itemSelectCombo.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());

						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());
						
						description.setValue(""
								+ item.getItemProperty(TBC_DESCRIPTION).getValue());

						visibleAddupdateSalesButton(false, true);

						itemSelectCombo.focus();

						if ((Long) item.getItemProperty(TBC_PO_ID).getValue() > 0) {
							// itemSelectCombo.setReadOnly(true);
							unitSelect.setReadOnly(true);
							quantityTextField.focus();
						}

						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());

						unitPriceTextField.setValue(""
								+ item.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

						specGroup.setValue(getSpecSet((String) item
								.getItemProperty(TBC_STK_IDS).getValue()));

						loadSpecFields(item.getItemProperty(TBC_SPEC_FIELD_ID)
								.getValue() + "");

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

					} else {

						itemSelectCombo.setValue(null);
						itemSelectCombo.focus();
						description.setValue("");
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");
//						advanceAmountField.setValue("0.0");
						specGroup.setValue(null);
						loadSpecFields("");

						visibleAddupdateSalesButton(true, false);

						itemSelectCombo.focus();
					}

				}

			});

			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (table.getComponentError() != null)
							setRequiredError(table, null, false);

						if (isAddingValid()) {

							boolean already_added_item = false;

							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());

							Iterator itr1 = table.getItemIds().iterator();
							List delList = new ArrayList();
							Object obj;
							Item item;
							double qty = 0, price = 0, discount;
							double tax_amt = 0, tax_perc = 0;
							double total, cess_amt, cess_perc;

							price = 0;
							qty = 0;
							total = 0;
							double discount_amt = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discount_amt = toDouble(discountTextField
									.getValue());

							// netPriceTextField.setNewValue(asString(price
							// * qty));

							table.setVisibleColumns(allHeaders);

							UnitModel objUnit = new UnitDao()
									.getUnit((Long) unitSelect.getValue());

							tax_amt = 0;
							tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = roundNumber(price * qty
											* objTax.getValue() / 100);
								} else {
									tax_perc = 0;
									tax_amt = roundNumber(objTax.getValue());
								}
							} else {
								objTax = new TaxModel(1);
							}

							total = roundNumber(price * qty);

							int id = 0, ct = 0;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								id = (Integer) it.next();
							}
							id++;

							cess_amt = 0;
							if (isCessEnableOnItem(itm.getId())) {
								cess_perc = (Double) session
										.getAttribute("cess_percentage");
								cess_amt = roundNumber(tax_amt
										* getCessPercentage() / 100);
							}

							double conv_rat = comDao.getConvertionRate(
									itm.getId(),
									objUnit.getId(),
									toInt(salesTypeSelect.getValue().toString()));

							table.addItem(
									new Object[] {
											table.getItemIds().size() + 1,
											itm.getId(),
											itm.getItem_code(),
											itm.getName(),description.getValue(),
											qty,
											objUnit.getId(),
											objUnit.getSymbol(),
											toDouble(unitPriceTextField
													.getValue()),
											objTax.getId(),
											tax_amt,
											tax_perc,
											total,
											(long) 0,
											(long) 0,
											cess_amt,
											roundNumber(toDouble(netPriceTextField
													.getValue())),
											roundNumber(toDouble(netPriceTextField
													.getValue())),
											conv_rat * qty, getSpecString(),
											getSpecFieldString() }, id);

							table.setVisibleColumns(requiredHeaders);

							itemSelectCombo.setValue(null);
							description.setValue("");
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							specGroup.setValue(null);
							loadSpecFields("");
						} else {
							itemSelectCombo.setValue(null);
							description.setValue("");
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							specGroup.setValue(null);
							loadSpecFields("");
						}
						calculateTotals();

						itemSelectCombo.focus();
						// }

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isAddingValid()) {

							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());

							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());

							double price = 0, qty = 0, total = 0, discount_amt = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discount_amt = toDouble(discountTextField
									.getValue());

							// netPriceTextField
							// .setNewValue(asString(roundNumber(price
							// * qty - discount_amt)));

							// table.setVisibleColumns(new String[] {TBC_SN,
							// TBC_ITEM_ID,TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY,
							// TBC_UNIT_ID, TBC_UNIT,
							// TBC_UNIT_PRICE,TBC_TAX_ID, TBC_TAX_AMT,
							// TBC_TAX_PERC , TBC_NET_PRICE});

							UnitModel objUnit = new UnitDao()
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = roundNumber(price * qty
											* objTax.getValue() / 100);
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							total = roundNumber(price * qty);

							double cess_amt = 0;
							if (isCessEnableOnItem(itm.getId())) {
								double cess_perc = roundNumber((Double) session
										.getAttribute("cess_percentage"));
								cess_amt = roundNumber(tax_amt
										* getCessPercentage() / 100);
							}

							// int id=(Integer) table.getValue();
							// table.removeItem(table.getValue());
							// table.addItem(new Object[] {id, itm.getId(),
							// itm.getItem_code(),
							// itm.getName(), qty , objUnit.getId() ,
							// objUnit.getSymbol(),
							// toDouble(unitPriceTextField.getValue()),
							// objTax.getId(), tax_amt, tax_perc, totalAmt},
							// id);

							item.getItemProperty(TBC_ITEM_ID).setValue(
									itm.getId());
							item.getItemProperty(TBC_ITEM_CODE).setValue(
									itm.getItem_code());
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									itm.getName());
							item.getItemProperty(TBC_DESCRIPTION).setValue(description.getValue());
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
							item.getItemProperty(TBC_NET_PRICE).setValue(total);
							item.getItemProperty(TBC_CESS_AMT).setValue(
									cess_amt);
							item.getItemProperty(TBC_NET_TOTAL).setValue(
									toDouble(netPriceTextField.getValue()));
							item.getItemProperty(TBC_NET_FINAL).setValue(
									roundNumber(toDouble(netPriceTextField
											.getValue())));

							/*
							 * item.getItemProperty(TBC_MANUFACT_DATE).setValue(
							 * stk.getManufacturing_date());
							 * item.getItemProperty(TBC_EXPIRE_DATE).setValue(
							 * stk.getExpiry_date());
							 * item.getItemProperty(TBC_STOCK_ID).setValue(
							 * stk.getId());
							 */

							double conv_rat = comDao.getConvertionRate(
									itm.getId(),
									objUnit.getId(),
									toInt(salesTypeSelect.getValue().toString()));

							item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
									.setValue(conv_rat * qty);

							item.getItemProperty(TBC_STK_IDS).setValue(
									getSpecString());

							item.getItemProperty(TBC_SPEC_FIELD_ID).setValue(
									getSpecFieldString());

							table.setVisibleColumns(requiredHeaders);

							// itemsCompo.setValue(null);
							// itemsCompo.focus();
							itemSelectCombo.setValue(null);
							description.setValue("");
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");
//							advanceAmountField.setValue("0.0");
							specGroup.setValue(null);
							loadSpecFields("");

							visibleAddupdateSalesButton(true, false);

							itemSelectCombo.focus();

							table.setValue(null);

							calculateTotals();

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			itemSelectCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemSelectCombo.getValue() != null) {
							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());

							SCollectionContainer bic = SCollectionContainer
									.setList(comDao.getAllItemUnitDetails(itm
											.getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							if (taxEnable) {
								taxSelect.setValue(itm.getSalesTax().getId());
							}

							unitSelect.setValue(itm.getUnit().getId());

							quantityTextField.focus();
							quantityTextField.selectAll();

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			unitSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							if (itemSelectCombo.getValue() != null) {

								ItemModel itm = daoObj
										.getItem((Long) itemSelectCombo
												.getValue());
								unitPriceTextField.setValue(asString(comDao.getItemPrice(
										itm.getId(), (Long) unitSelect
												.getValue(),
										toInt(salesTypeSelect.getValue()
												.toString()))));

								if (quantityTextField.getValue() != null
										&& !quantityTextField.getValue()
												.equals("")) {
									netPriceTextField.setNewValue(asString(Double
											.parseDouble(unitPriceTextField
													.getValue())
											* Double.parseDouble(quantityTextField
													.getValue())));
								}

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			salesTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					Object obj = unitSelect.getValue();
					unitSelect.setValue(null);
					unitSelect.setValue(obj);
				}
			});

			changeProdUnitButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						newproductionUnitSelect.setValue((Long)productionUnitSelect.getValue());
						popupWindow.setContent(prodWindowFormLayout);

						popupWindow.setWidth("480");
						popupWindow.setHeight("180");

						popupWindow.setCaption("Change Production Unit");

						popupWindow.center();
						popupWindow.setModal(true);
						getUI().getCurrent().addWindow(popupWindow);
					}catch(Exception r){
						r.printStackTrace();
					}
				}
			});
			
		changeProdWindowButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
						daoObj.updateProductionUnit(toLong(salesNumberList
								.getValue().toString()),
								toLong(newproductionUnitSelect.getValue()
										.toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				productionUnitSelect.setNewValue(toLong(newproductionUnitSelect.getValue()
										.toString()));
				getUI().getCurrent().removeWindow(popupWindow);
			}
		});
		cancelProdWindowButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().removeWindow(popupWindow);
			}
		});

			// Added by Anil

			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					List<Object> reportList = new ArrayList<Object>();
					SalesPrintBean bean = null;
					NumberToWords numberToWords = new NumberToWords();
					double total = 0;
					try {

						TailoringSalesModel salObj = daoObj
								.getSale((Long) salesNumberList.getValue());

						CustomerModel customerModel = custDao
								.getCustomerFromLedger(salObj.getCustomer()
										.getId());
						String address = "";
						if (customerModel != null) {
							address = new AddressDao()
									.getAddressString(customerModel
											.getAddress().getId());
						}

						map.put("CUSTOMER_NAME", customerModel.getName());
						map.put("CUSTOMER_ADDRESS", address);
						map.put("SALES_BILL_NO", toLong(salesNumberList
								.getItemCaption(salesNumberList.getValue())));
						map.put("BILL_DATE", CommonUtil
								.formatDateToDDMMMYYYY(salObj.getDate()));

						map.put("SALES_MAN", usrDao
								.getUserNameFromLoginID(salObj
										.getSales_person()));

						String resp = "";

						map.put("RESPONSIBLE_PERSON", resp);

						String type = "";
						if (status == 1) {
							type = "Cash Sale";
						} else {
							type = "Credit Sale";
						}
						map.put("SALES_TYPE", type);
						map.put("OFFICE_NAME", customerModel.getLedger()
								.getOffice().getName());

						TailoringSalesInventoryDetailsModel invObj;
						Iterator<TailoringSalesInventoryDetailsModel> itr1 = salObj
								.getTailoring_inventory_details_list()
								.iterator();
						while (itr1.hasNext()) {
							invObj = itr1.next();

							bean = new SalesPrintBean(invObj.getItem()
									.getName()+" "+invObj.getDescription(), invObj.getQunatity(), invObj
									.getUnit_price(),
									(invObj.getQunatity() * invObj
											.getUnit_price())
											- invObj.getCess_amount()
											+ invObj.getTax_amount(), invObj
											.getUnit().getSymbol(), invObj
											.getItem().getItem_code(), invObj
											.getQunatity());

							total += bean.getTotal();
							bean.setDescription(invObj.getDescription());

							reportList.add(bean);
						}

						S_OfficeModel officeModel = new OfficeDao()
								.getOffice(getOfficeID());
						map.put("AMOUNT_IN_WORDS", numberToWords.convertNumber(
								roundNumber(total) + "", officeModel
										.getCurrency().getInteger_part(),
								officeModel.getCurrency().getFractional_part()));
						map.put("IMAGE_PATH", VaadinServlet.getCurrent()
								.getServletContext().getRealPath("/")
								.toString());
						map.put("DELIVERY_DATE", CommonUtil.formatDateToCommonDateTimeFormat(salObj.getActual_delivery_date()));
						map.put("ADVANCE", salObj.getAdvance_amount());
						map.put("BALANCE", salObj.getAmount()-salObj.getAdvance_amount());
						map.put("GRAND_TOTAL", salObj.getAmount());
						

						Report report = new Report(getLoginID());
						report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
						report.setReportFileName("SalesPrint");
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

			if (!isFinYearBackEntry()) {
				saveSalesButton.setVisible(false);
				updateSalesButton.setVisible(false);
				deleteSalesButton.setVisible(false);
				cancelButton.setVisible(false);
				deliverSalesButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice((Set) specGroup.getValue());
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			quantityTextField.setImmediate(true);

			quantityTextField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice((Set) specGroup.getValue());
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			discountTextField.setImmediate(true);
			chargeTextField.setImmediate(true);
			netPriceTextField.setImmediate(true);
//			advanceAmountField.setImmediate(true);
			payingAmountTextField.setImmediate(true);
//			advanceAmountField
//					.addValueChangeListener(new ValueChangeListener() {
//
//						@Override
//						public void valueChange(ValueChangeEvent event) {
//							try {
//								if (toDouble(grandTotalAmtTextField.getValue()) > toDouble(advanceAmountField
//										.getValue())) {
//									// payingAmountTextField.setValue(advanceAmountField.getValue());
//									balanceField.setNewValue(toDouble(grandTotalAmtTextField
//											.getValue())
//											- (toDouble(payingAmountTextField
//													.getValue()) + toDouble(advanceAmountField
//													.getValue())) + "");
//								} else {
//									// payingAmountTextField.setValue(grandTotalAmtTextField.getValue());
//									balanceField.setNewValue("0");
//								}
//							} catch (Exception e) {
//								// payingAmountTextField.setValue("0");
//								balanceField.setNewValue(grandTotalAmtTextField
//										.getValue());
//							}
//						}
//					});

			payingAmountTextField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							try {
								balanceField.setNewValue(toDouble(grandTotalAmtTextField
										.getValue())
										- (toDouble(payingAmountTextField.getValue())) 
//												+ toDouble(advanceAmountField.getValue()))
												+ "");
							} catch (Exception e) {
								balanceField.setNewValue(grandTotalAmtTextField
										.getValue());
							}
						}
					});

			ValueChangeListener listnr = new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					calculateTotals();
				}
			};
			chargeTextField.addValueChangeListener(listnr);
			discountTextField.addValueChangeListener(listnr);
			netPriceTextField.addValueChangeListener(listnr);

			if (isSaleEditable()) {
				updateSalesButton.setEnabled(true);
			} else
				updateSalesButton.setEnabled(false);

			deliverSalesButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						payingAmountTextField.setComponentError(null);
//						if ((toDouble(payingAmountTextField.getValue())/* + toDouble(advanceAmountField.getValue())*/) == toDouble(grandTotalAmtTextField
//								.getValue())) {

							ConfirmDialog.show(getUI(), "",
									"Items will be delivered", "OK", "CANCEL",
									new ConfirmDialog.Listener() {

										@Override
										public void onClose(ConfirmDialog dlg) {

											if (dlg.isConfirmed()) {
												try {

													if (isValid()) {
														long customer_id = (Long) customerSelect
																.getValue();

														double costof_inv_amt = 0;

														TailoringSalesModel salObj = daoObj
																.getSale((Long) salesNumberList
																		.getValue());

														List<TailoringSalesInventoryDetailsModel> itemsList = new ArrayList<TailoringSalesInventoryDetailsModel>();

														TailoringSalesInventoryDetailsModel invObj;
														Item item;
														double std_cost;
														Iterator it = table
																.getItemIds()
																.iterator();
														while (it.hasNext()) {
															invObj = new TailoringSalesInventoryDetailsModel();

															item = table.getItem(it
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

															invObj.setOrder_id((Long) item
																	.getItemProperty(
																			TBC_PO_ID)
																	.getValue());
															invObj.setCess_amount((Double) item
																	.getItemProperty(
																			TBC_CESS_AMT)
																	.getValue());

															invObj.setQuantity_in_basic_unit((Double) item
																	.getItemProperty(
																			TBC_QTY_IN_BASIC_UNI)
																	.getValue());

															invObj.setStock_ids(asString(item
																	.getItemProperty(
																			TBC_STK_IDS)
																	.getValue()));

															invObj.setSpec_field_ids(asString(item
																	.getItemProperty(
																			TBC_SPEC_FIELD_ID)
																	.getValue()));

															itemsList
																	.add(invObj);

															std_cost = itemDao
																	.getStandardCost(invObj
																			.getItem()
																			.getId());
															costof_inv_amt += invObj
																	.getQunatity()
																	* std_cost;

														}

														if (isExciceDutyEnable()) {
															salObj.setExcise_duty(toDouble(exciseDutyTextField
																	.getValue()));
														}
														if (isShippingChargeEnable()) {
															salObj.setShipping_charge(toDouble(shippingChargeTextField
																	.getValue()));
														}
														// if
														// (isDiscountEnable())
														// {
														// salObj.setDiscount(toDouble(discountTextField
														// .getValue()));
														// }

														salObj.setCredit_period(toInt(creditPeriodTextField
																.getValue()));

														salObj.setPayment_amount(toDouble(payingAmountTextField
																.getValue())
//																+ toDouble(advanceAmountField
//																		.getValue())
																		);
														salObj.setDiscount(toDouble(discountTextField
																.getValue()));
														salObj.setCharges(toDouble(chargeTextField
																.getValue()));
														salObj.setAmount(toDouble(grandTotalAmtTextField
																.getValue()));
														salObj.setAdvance_amount(toDouble(payingAmountTextField
																.getValue()));
														// salObj.setBuilding(new
														// BuildingModel((Long)
														// buildingSelect.getValue()));
														salObj.setComments(comment
																.getValue());
														salObj.setDate(CommonUtil
																.getSQLDateFromUtilDate(date
																		.getValue()));
														salObj.setExpected_delivery_date(CommonUtil
																.getSQLDateFromUtilDate(exp_delv_date
																		.getValue()));
														salObj.setActual_delivery_date(CommonUtil
																.getSQLDateFromUtilDate(act_delv_date
																		.getValue()));
														// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
														salObj.setLogin(new S_LoginModel(
																getLoginID()));
														salObj.setOffice(new S_OfficeModel(
																getOfficeID()));
														salObj.setCustomer(new LedgerModel(
																(Long) customerSelect
																		.getValue()));
														salObj.setTailoring_inventory_details_list(itemsList);
														salObj.setVoucher_no(toLong(refNoField
																.getValue()));
														salObj.setSales_person((Long) employSelect
																.getValue());
														salObj.setDelivery_status((long) 2);

														FinTransaction trans = new FinTransaction();
														double totalAmt = toDouble(grandTotalAmtTextField
																.getValue());
														double netAmt = totalAmt;

														double amt = 0;

														double payingAmt = toDouble(payingAmountTextField
																.getValue());
//																+ toDouble(advanceAmountField
//																		.getValue()	);
														long toAccId=toLong(toAccountComboField.getValue().toString());

														if (payingAmt == netAmt) {
															trans.addTransaction(
																	SConstants.CR,
																	customer_id,
																	toAccId,
																	roundNumber(payingAmt));
															trans.addTransaction(
																	SConstants.CR,
																	settings.getSALES_ACCOUNT(),
																	customer_id,
																	roundNumber(netAmt));

															salObj.setStatus(1);
															status = 1;
														} else if (payingAmt == 0) {
															trans.addTransaction(
																	SConstants.CR,
																	settings.getSALES_ACCOUNT(),
																	customer_id,
																	roundNumber(netAmt));
															salObj.setStatus(2);
															status = 2;
														} else {
															trans.addTransaction(
																	SConstants.CR,
																	customer_id,
																	toAccId,
																	roundNumber(payingAmt));
															trans.addTransaction(
																	SConstants.CR,
																	settings.getSALES_ACCOUNT(),
																	customer_id,
																	roundNumber(netAmt));
															status = 3;
															salObj.setStatus(3);
														}

														if (settings
																.getINVENTORY_ACCOUNT() != 0) {
															trans.addTransaction(
																	SConstants.DR,
																	settings.getINVENTORY_ACCOUNT(),
																	settings.getCGS_ACCOUNT(),
																	roundNumber(costof_inv_amt));
															totalAmt -= costof_inv_amt;
														}

														if (settings
																.getSALES_TAX_ACCOUNT() != 0) {
															amt = toDouble(table
																	.getColumnFooter(TBC_TAX_AMT));
															if (amt != 0) {
																trans.addTransaction(
																		SConstants.DR,
																		settings.getSALES_TAX_ACCOUNT(),
																		settings.getCGS_ACCOUNT(),
																		roundNumber(amt));
																totalAmt -= amt;
															}
														}

														if (taxEnable) {
															if (settings
																	.isCESS_ENABLED()) {
																if (settings
																		.getCESS_ACCOUNT() != 0) {
																	amt = toDouble(table
																			.getColumnFooter(TBC_CESS_AMT));
																	if (amt != 0) {
																		trans.addTransaction(
																				SConstants.DR,
																				settings.getCESS_ACCOUNT(),
																				settings.getCGS_ACCOUNT(),
																				roundNumber(amt));
																		totalAmt -= amt;
																	}
																}
															}

															if (settings
																	.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
																amt = toDouble(exciseDutyTextField
																		.getValue());
																if (amt != 0) {
																	trans.addTransaction(
																			SConstants.CR,
																			settings.getSALES_EXCISE_DUTY_ACCOUNT(),
																			settings.getCGS_ACCOUNT(),
																			roundNumber(amt));
																	totalAmt -= amt;
																}
															}
														}

														if (settings
																.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
															amt = toDouble(shippingChargeTextField
																	.getValue());
															if (amt != 0) {
																trans.addTransaction(
																		SConstants.CR,
																		settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
																		settings.getCGS_ACCOUNT(),
																		roundNumber(amt));
																totalAmt -= amt;
															}
														}

														if (settings
																.getSALES_DESCOUNT_ACCOUNT() != 0) {
															amt = toDouble(shippingChargeTextField
																	.getValue());
															if (amt != 0) {
																trans.addTransaction(
																		SConstants.CR,
																		settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
																		settings.getCGS_ACCOUNT(),
																		roundNumber(amt));
																totalAmt -= amt;
															}
														}

														if (settings
																.getSALES_REVENUE_ACCOUNT() != 0) {
															if (amt != 0) {
																trans.addTransaction(
																		SConstants.CR,
																		settings.getSALES_REVENUE_ACCOUNT(),
																		settings.getCGS_ACCOUNT(),
																		roundNumber(totalAmt));
															}
														}
														TransactionModel tran = daoObj
																.getTransaction(salObj
																		.getTransaction_id());
														tran.setTransaction_details_list(trans
																.getChildList());
														tran.setDate(salObj
																.getDate());
														tran.setLogin_id(getLoginID());

														daoObj.update(salObj,
																tran, payingAmt);

														saveActivity(
																getOptionId(),
																"Sales Delivered. Bill No : "
																		+ salObj.getSales_number()
																		+ ", Customer : "
																		+ customerSelect
																				.getItemCaption(customerSelect
																						.getValue())
																		+ ", Amount : "
																		+ salObj.getAmount(),salObj.getId());

														loadSale(salObj.getId());

														Notification
																.show(getPropertyName("update_success"),
																		Type.WARNING_MESSAGE);

													}

												} catch (Exception e) {
													e.printStackTrace();
													Notification
															.show(getPropertyName("error"),
																	Type.ERROR_MESSAGE);
												}

											}
										}
									});
//						} else {
//							setRequiredError(payingAmountTextField,
//									getPropertyName("invalid_data"), true);
//						}
					} catch (Exception e2) {
					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return pannel;
	}

	protected String getSpecFieldString() {
		String ids = "";
		String item;
		STextField field = null;
		Iterator itr = specFieldLay.getComponentIterator();
		while (itr.hasNext()) {
			field = (STextField) itr.next();
			item = field.getId() + ":" + field.getValue();
			ids += item + ",";
		}
		return ids;
	}

	private void loadSpecFields(String data) {

		if (specArrayList != null) {
			specFieldLay.removeAllComponents();
			TailoringItemSpecModel mdl;
			String specValue = "";
			Set<String> specSet = null;
			String[] arr = null;
			Hashtable<String, String> hash = new Hashtable<String, String>();

			try {
				if (data.trim().length() > 0) {
					specSet = getSpecFieldSet(data);

					for (String s : specSet) {
						arr = s.split(":");
						hash.put(arr[0], arr[1]);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			Iterator ietr = specArrayList.iterator();
			while (ietr.hasNext()) {
				mdl = (TailoringItemSpecModel) ietr.next();

				specField = new STextField(mdl.getName(), 80);
				specField.setId(mdl.getId() + "");
				specField.setValue("0");
				specFieldLay.addComponent(specField);

				if (hash.containsKey(specField.getId())) {
					specField.setValue(hash.get(specField.getId()));
				}
			}

		}
	}

	protected Set getSpecSet(String value) {
		Set idSet = new HashSet();
		try {
			String str = value.substring(0, value.length() - 1);
			String[] arr = str.split(",");

			for (int i = 0; i < arr.length; i++) {
				idSet.add(toLong(arr[i]));
			}
		} catch (Exception e) {
		}
		return idSet;
	}

	protected Set getSpecFieldSet(String value) {
		Set idSet = new HashSet();
		try {
			String str = value.substring(0, value.length() - 1);
			String[] arr = str.split(",");

			for (int i = 0; i < arr.length; i++) {
				idSet.add(arr[i]);
			}
		} catch (Exception e) {
		}
		return idSet;
	}

	protected String getSpecString() {
		String ids = "";
		String item;
		List lst = new ArrayList((Set) specGroup.getValue());
		Iterator itr = lst.iterator();
		while (itr.hasNext()) {
			ids += itr.next() + ",";
		}
		return ids;
	}

	protected void reloadItemStocks() {
		try {
			List list = daoObj.getAllItemsWithRealStck(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			itemSelectCombo.setContainerDataSource(bic);
			itemSelectCombo.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				itemSelectCombo.setValue((Long) getHttpSession().getAttribute(
						"saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void resetUnit() {
		try {
			Object temp = unitSelect.getValue();
			if (itemSelectCombo.getValue() != null) {
				ItemModel itm = daoObj.getItem((Long) itemSelectCombo
						.getValue());

				List lst = new ArrayList();
				lst.addAll(comDao.getAllItemUnitDetails(itm.getId()));
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

				unitSelect.setValue(null);
				unitSelect.setValue(temp);

			} else {

				List lst = new ArrayList();
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void calculateNetPrice(Set set) {
		double unitPrc = 0, qty = 0, disc = 0, specAmnt = 0, total = 0;

		try {
			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
			disc = Double.parseDouble(discountTextField.getValue());
		} catch (Exception e) {
		}
		total = (unitPrc * qty) - disc;
		if (set != null && !set.isEmpty()) {
			try {
				total += specDao.getSpecPrice(set);
			} catch (Exception e) {
			}
		}
		netPriceTextField.setNewValue(asString(new BigDecimal(total)));
	}

	protected void reloadCustomers() {
		try {
			List list = custDao
					.getAllActiveCustomerNamesWithLedgerID(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			customerSelect.setContainerDataSource(bic);
			customerSelect.setItemCaptionPropertyId("name");

			if (session.getAttribute("new_id") != null) {
				customerSelect.setValue((Long) session.getAttribute("new_id"));
				session.removeAttribute("new_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0, ttl_bfr_tax = 0, ttl_bfr_disc = 0, cess_ttl = 0, discount = 0, charges = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();

				if (taxEnable) {
					tax_ttl += (Double) item.getItemProperty(TBC_TAX_AMT)
							.getValue();
					cess_ttl += (Double) item.getItemProperty(TBC_CESS_AMT)
							.getValue();
				}

				net_ttl += (Double) item.getItemProperty(TBC_NET_PRICE)
						.getValue();

				ttl_bfr_tax += (Double) item.getItemProperty(TBC_NET_TOTAL)
						.getValue();
				ttl_bfr_disc += (Double) item.getItemProperty(TBC_NET_FINAL)
						.getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_TAX_AMT, asString(roundNumber(tax_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));
			table.setColumnFooter(TBC_CESS_AMT, asString(roundNumber(cess_ttl)));
			table.setColumnFooter(TBC_NET_TOTAL,
					asString(roundNumber(ttl_bfr_tax)));
			table.setColumnFooter(TBC_NET_FINAL,
					asString(roundNumber(ttl_bfr_disc)));

			double ship_charg = 0, excise_duty = 0;
			try {
				ship_charg = toDouble(shippingChargeTextField.getValue());
				excise_duty = toDouble(exciseDutyTextField.getValue());
				discount = toDouble(discountTextField.getValue());
				charges = toDouble(chargeTextField.getValue());

			} catch (Exception e) {
			}

			grandTotalAmtTextField
					.setNewValue(asString(roundNumber(ttl_bfr_disc + ship_charg
							+ excise_duty - discount + charges)));

		} catch (Exception e) {
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (discountTextField.getValue() == null
					|| discountTextField.getValue().equals("")) {
				discountTextField.setValue("0");
			} else {
				try {
					if (toDouble(discountTextField.getValue()) < 0) {
						setRequiredError(discountTextField,
								getPropertyName("invalid_data"), true);
						discountTextField.focus();
						discountTextField.selectAll();
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
				unitPriceTextField.selectAll();
				ret = false;
			} else {
				try {
					if (toDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,
								"Enter a valid Price", true);
						unitPriceTextField.focus();
						unitPriceTextField.selectAll();
						ret = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField,
							getPropertyName("invalid_data"), true);
					unitPriceTextField.focus();
					unitPriceTextField.selectAll();
					ret = false;
					// TODO: handle exception
				}
			}

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				quantityTextField.selectAll();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("invalid_data"), true);
						quantityTextField.focus();
						quantityTextField.selectAll();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					quantityTextField.selectAll();
					ret = false;
					// TODO: handle exception
				}
			}

			if (itemSelectCombo.getValue() == null
					|| itemSelectCombo.getValue().equals("")) {
				setRequiredError(itemSelectCombo,
						getPropertyName("invalid_selection"), true);
				itemSelectCombo.focus();
				ret = false;
			} else
				setRequiredError(itemSelectCombo, null, false);

			if (unitSelect.getValue() == null
					|| unitSelect.getValue().equals("")) {
				setRequiredError(unitSelect,
						getPropertyName("invalid_selection"), true);
				unitSelect.focus();
				ret = false;
			} else
				setRequiredError(unitSelect, null, false);
		} catch (Exception e) {
			ret = false;
		}

		return ret;

	}

	public void visibleAddupdateSalesButton(boolean AddVisible,
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
					// Item item=table.getItem(selectedItems.iterator().next());
					table.removeItem(it1.next());
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
			itemSelectCombo.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public void loadSale(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new TailoringSalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			salesNumberList.setContainerDataSource(bic);
			salesNumberList.setItemCaptionPropertyId("comments");

			reloadItemStocks();

			salesNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
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

		try {
			if (toDouble(payingAmountTextField.getValue()) <= 0
					&& toInt(paymentModeField.getValue().toString()) != SConstants.paymentModes.CREDIT) {
				setRequiredError(paymentModeField,
						getPropertyName("invalid_selection . Select credit if the payment amount is 0"), true);
				paymentModeField.focus();
				ret = false;
				
			} else
				setRequiredError(paymentModeField, null, false);
		}catch(Exception ex){
			setRequiredError(payingAmountTextField,
					getPropertyName("invalid_data"), true);
			payingAmountTextField.focus();
			ret = false;
		}
		
//		if (advanceAmountField.getValue() == null
//				|| advanceAmountField.getValue().equals("")) {
//			advanceAmountField.setValue("0.0");
//		} else {
//			try {
//				if (toDouble(advanceAmountField.getValue()) < 0) {
//					setRequiredError(advanceAmountField,
//							getPropertyName("invalid_data"), true);
//					advanceAmountField.focus();
//					ret = false;
//				} else
//					setRequiredError(advanceAmountField, null, false);
//			} catch (Exception e) {
//				setRequiredError(advanceAmountField,
//						getPropertyName("invalid_data"), true);
//				advanceAmountField.focus();
//				ret = false;
//			}
//		}

		if (discountTextField.getValue() == null
				|| discountTextField.getValue().equals("")) {
			discountTextField.setValue("0.0");
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
			}
		}

		if (chargeTextField.getValue() == null
				|| chargeTextField.getValue().equals("")) {
			chargeTextField.setValue("0.0");
		} else {
			try {
				if (toDouble(chargeTextField.getValue()) < 0) {
					setRequiredError(chargeTextField,
							getPropertyName("invalid_data"), true);
					chargeTextField.focus();
					ret = false;
				} else
					setRequiredError(chargeTextField, null, false);
			} catch (Exception e) {
				setRequiredError(chargeTextField,
						getPropertyName("invalid_data"), true);
				chargeTextField.focus();
				ret = false;
			}
		}

		// if ((Long) cashOrCreditRadio.getValue() == 1) {
		//
		// if (toDouble(payingAmountTextField.getValue()) !=
		// toDouble(grandTotalAmtTextField
		// .getValue())) {
		// setRequiredError(
		// payingAmountTextField,
		// "Enter a valid amount. Need to set Paying amount to Total amount. Else select Credit sale.",
		// true);
		// payingAmountTextField.focus();
		// ret = false;
		// }
		//
		// } else if (toDouble(payingAmountTextField.getValue()) >=
		// toDouble(grandTotalAmtTextField
		// .getValue())) {
		// setRequiredError(payingAmountTextField,
		// "Enter a valid amount. Must be less than total. Else select Cash Sale.",
		// true);
		// payingAmountTextField.focus();
		// ret = false;
		// }

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
			setRequiredError(table, "Add some items", true);
			itemSelectCombo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		/*
		 * if(buildingSelect.getValue()==null ||
		 * buildingSelect.getValue().equals("")){ setRequiredError(
		 * buildingSelect, "Select a building",true); buildingSelect.focus();
		 * ret=false; } else setRequiredError(buildingSelect , null,false);
		 */

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
				// TODO: handle exception
			}
		}
		if (refNoField.getValue() == null || refNoField.getValue().equals("")) {
			setRequiredError(refNoField, getPropertyName("invalid_data"), true);
			ret = false;
			refNoField.focus();
		} else {
			try {
				if (toInt(refNoField.getValue()) < 0) {
					setRequiredError(refNoField,
							getPropertyName("invalid_data"), true);
					refNoField.focus();
					ret = false;
				} else
					setRequiredError(refNoField, null, false);
			} catch (Exception e) {
				setRequiredError(refNoField, getPropertyName("invalid_data"),
						true);
				refNoField.focus();
				ret = false;
			}
		}

		if (employSelect.getValue() == null
				|| employSelect.getValue().equals("")) {
			setRequiredError(employSelect,
					getPropertyName("invalid_selection"), true);
			employSelect.focus();
			ret = false;
		} else
			setRequiredError(employSelect, null, false);
		if (productionUnitSelect.getValue() == null
				|| productionUnitSelect.getValue().equals("")) {
			setRequiredError(productionUnitSelect,
					getPropertyName("invalid_selection"), true);
			productionUnitSelect.focus();
			ret = false;
		} else
			setRequiredError(productionUnitSelect, null, false);

		if (customerSelect.getValue() == null
				|| customerSelect.getValue().equals("")) {
			setRequiredError(customerSelect,
					getPropertyName("invalid_selection"), true);
			customerSelect.focus();
			ret = false;
		} else
			setRequiredError(customerSelect, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		if (exp_delv_date.getValue() == null
				|| exp_delv_date.getValue().equals("")) {
			setRequiredError(exp_delv_date,
					getPropertyName("invalid_selection"), true);
			exp_delv_date.focus();
			ret = false;
		} else
			setRequiredError(exp_delv_date, null, false);

		if (act_delv_date.getValue() == null
				|| act_delv_date.getValue().equals("")) {
			setRequiredError(act_delv_date,
					getPropertyName("invalid_selection"), true);
			act_delv_date.focus();
			ret = false;
		} else
			setRequiredError(act_delv_date, null, false);

		if (ret)
			calculateTotals();
		// TODO Auto-generated method stub
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
		if (itemSelectCombo.getComponentError() != null)
			setRequiredError(itemSelectCombo, null, false);
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		payingAmountTextField.setComponentError(null);
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	@Override
	public SComboField getBillNoFiled() {
		return salesNumberList;
	}
}
