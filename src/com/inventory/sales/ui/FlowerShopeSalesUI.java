package com.inventory.sales.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.GradeModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.StockRackMappingDao;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.SetUnitManagementUI;
import com.inventory.dao.RackDao;
import com.inventory.dao.SalesManMapDao;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.StockRateUpdateDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.bean.SalesInventoryDetailsPojo;
import com.inventory.sales.dao.SalesNewDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
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
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SDialogBox;
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
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.CurrencyRateDao;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */
public class FlowerShopeSalesUI extends SparkLogic {

	private static final long serialVersionUID = -5415935778881929746L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "Tax %";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_DISCOUNT = "DISCOUNT";
	static String TBC_PO_ID = "PO ID";
	static String TBC_INV_ID = "INV ID";
	static String TBC_CESS_AMT = "CESS";
	static String TBC_NET_TOTAL = "Net Total";
	static String TBC_NET_FINAL = "Final Amt";
	static String TBC_MANUFACT_DATE = "Mfg. Date";
	static String TBC_EXPIRE_DATE = "Exp. Date";
	static String TBC_STOCK_ID = "Stock ID";
	static String TBC_CONVERTION_QTY = "Convertion Qty";
	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";
	static String TBC_GRADE_ID = "Grade Id";
	static String TBC_GRADE = "Grade";
	static String TBC_RACK_ID = "Rack ID";
	static String TBC_RACK_NAME = "Rack";
	static String TBC_OLD_QTY = "Old Qty";

	SalesNewDao daoObj = new SalesNewDao();

	CommonMethodsDao comDao;

	private SComboField salesNumberList;

	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;

	STextField convertionQtyTextField;
	STextField convertedQtyTextField;

	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField netPriceTextField;

	STextField payingAmountTextField;
	STextField creditPeriodTextField;

	SComboField responsibleEmployeeCombo;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSalesButton;
	SButton updateSalesButton;
	SButton deleteSalesButton;
	SButton cancelSalesButton;

	SOptionGroup salesOrdersOptions;
	SButton addSOButton;
	SDialogBox poWindow;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SComboField customerSelect;
	SDateField date;
	SDateField expiry_date;
	SDateField manufaturing_date;
	SComboField itemSelectCombo;
	SNativeSelect gradeComboField;
	SListSelect stockSelectList;
	SComboField rackSelectList;

	SComboField employSelect;

	STextField grandTotalAmtTextField;
	STextField shippingChargeTextField;
	STextField exciseDutyTextField;
	STextArea comment;

	SRadioButton cashOrCreditRadio;

	SettingsValuePojo settings;

	WrappedSession session;

	boolean taxEnable = isTaxEnable();

	private String[] allHeaders;
	private String[] requiredHeaders;

	private SButton printButton;

	long status, sales_number = 0;

	private SButton newCustomerButton;
	private SButton newItemButton;

	private SButton newUnitButton;
	private SButton unitMapButton;

	private SButton changeStkButton;
	private SButton stkDoneButton;

	private SDialogBox newCustomerWindow;
	private SDialogBox newItemWindow;
	private SalesCustomerPanel salesCustomerPanel;
	private ItemPanel itemPanel;

	SNativeSelect salesTypeSelect;

	SLabel rackBalance;

	SWindow popupWindow;

	private SButton loadAllSuppliersButton;

	SHorizontalLayout popupHor;
	SButton priceListButton;

	SHorizontalLayout hrz1;

	SButton newSaleButton;

	UserManagementDao usrDao;
	CustomerDao custDao;
	TaxDao taxDao;
	ItemDao itmDao;
	GradeDao gradeDao;

	double conv_val = 1;

	SPopupView pop;

	private STextField refNoField;

	private STextField barcodeField;

	SNativeSelect salesLocalTypeField;

	private SNativeSelect currencyNativeSelect;
	private STextField foreignCurrField;
	private CurrencyRateDao rateDao;

	private STextField discountMainField;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		comDao = new CommonMethodsDao();
		custDao = new CustomerDao();
		taxDao = new TaxDao();
		itmDao = new ItemDao();
		usrDao = new UserManagementDao();
		taxEnable = isTaxEnable();
		gradeDao = new GradeDao();
		rateDao = new CurrencyRateDao();

		popupWindow = new SWindow();

		rackBalance = new SLabel(getPropertyName("rack_balance"), "0.0");

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Sale");

		daoObj = new SalesNewDao();

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_PO_ID, TBC_INV_ID, TBC_CESS_AMT,
				TBC_NET_TOTAL, TBC_NET_FINAL, TBC_QTY_IN_BASIC_UNI,
				TBC_STOCK_ID, TBC_CONVERTION_QTY, TBC_GRADE_ID, TBC_GRADE,
				TBC_RACK_ID, TBC_RACK_NAME, TBC_OLD_QTY };

		if (taxEnable) {
			if (isCessEnable()) {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME, TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT, TBC_CESS_AMT,
						TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL, TBC_GRADE,
						TBC_RACK_NAME };
			} else {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME, TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT,
						TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL, TBC_GRADE,
						TBC_RACK_NAME };
			}
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY,
					TBC_NET_PRICE, TBC_DISCOUNT, TBC_NET_FINAL, TBC_GRADE,
					TBC_RACK_NAME };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		/*
		 * if (!isManufDateEnable()) { templist.remove(TBC_MANUFACT_DATE);
		 * templist.remove(TBC_EXPIRE_DATE); }
		 */
		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}
		
		if (!settings.isGRADING_ENABLED()) {
			templist.remove(TBC_GRADE);
		}
		
		if (!settings.isRACK_ENABLED()) {
			templist.remove(TBC_RACK_NAME);
		}
		
		requiredHeaders = templist.toArray(new String[templist.size()]);
		
		setSize(1300, 605);
		
		payingAmountTextField = new STextField(null, 100);
		payingAmountTextField.setValue("0.00");
		payingAmountTextField.setStyleName("textfield_align_right");

		discountMainField = new STextField(null, 100);
		discountMainField.setValue("0.00");
		discountMainField.setStyleName("textfield_align_right");
		discountMainField.setImmediate(true);

		creditPeriodTextField = new STextField(null, 100);

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
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
		bottomGrid.setColumns(10);
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

		addSOButton = new SButton(null, "Add");
		addSOButton.setStyleName("updateItemBtnStyle");

		try {

			priceListButton = new SButton();

			employSelect = new SComboField(
					null,
					125,
					usrDao.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
							getOfficeID(), getOrganizationID()), "id",
					"first_name");

			responsibleEmployeeCombo = new SComboField(null, 125,
					new SalesManMapDao().getUsers(getOfficeID(),
							SConstants.SALES_MAN), "id", "first_name");

			employSelect.setValue(getLoginID());

			if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin())
				employSelect.setReadOnly(true);

			List list = new ArrayList();
			list.add(new SalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));
			salesNumberList = new SComboField(null, 125, list, "id",
					"comments", false, getPropertyName("create_new"));

			date = new SDateField(null, 120, getDateFormat(), getWorkingDate());

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

			cashOrCreditRadio = new SRadioButton(null, 150,
					SConstants.paymentModeList, "key", "value");
			cashOrCreditRadio.setStyleName("radio_horizontal");

			currencyNativeSelect = new SNativeSelect(null, 100,
					new CurrencyManagementDao().getCurrencyCode(), "id", "name");
			currencyNativeSelect.setValue(getCurrencyID());

			salesLocalTypeField = new SNativeSelect(null, 80,
					SConstants.local_foreign_type.local_foreign_type, "intKey",
					"value");
			salesLocalTypeField.setValue(SConstants.local_foreign_type.LOCAL);

			SHorizontalLayout hrl3 = new SHorizontalLayout();
			hrl3.setSpacing(true);
			// hrl3.addComponent(cashOrCreditRadio);

			if (settings.isMULTIPLE_CURRENCY_ENABLED()) {
				hrl3.addComponent(currencyNativeSelect);
			}

//			if (settings.isLOCAL_FOREIGN_TYPE_ENABLED()) {
//				hrl3.addComponent(new SLabel("Type"));
//				hrl3.addComponent(salesLocalTypeField);
//			}

			refNoField = new STextField(null, 120);
			refNoField.setValue("0");

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesNumberList);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);

			masterDetailsGrid.addComponent(hrl3, 3, 0);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
			// masterDetailsGrid.setComponentAlignment(netTotal,
			// Alignment.MIDDLE_RIGHT);

			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("customer")), 1, 1);

			newCustomerButton = new SButton();

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

			SHorizontalLayout horr = new SHorizontalLayout();
			horr.addComponent(new SLabel(getPropertyName("employee")));
			horr.addComponent(employSelect);
			horr.setSpacing(true);
			masterDetailsGrid.addComponent(horr, 3, 1);

			SHorizontalLayout resp = new SHorizontalLayout();
			resp.addComponent(new SLabel(
					getPropertyName("responsible_employee")));
			resp.addComponent(responsibleEmployeeCombo);
			resp.setSpacing(true);
			masterDetailsGrid.addComponent(resp, 3, 2);

			/*
			 * masterDetailsGrid.addComponent(new SLabel("Employ :"), 4, 1);
			 * masterDetailsGrid.addComponent(employSelect, 5, 1);
			 */

			newCustomerButton.setStyleName("addNewBtnStyle");
			newCustomerButton.setDescription("Add new Customer");

			// newCustomerButton.setStyleName("v-button-link");
			// masterDetailsGrid.addComponent(newCustomerButton, 3, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_type")), 6, 1);
			masterDetailsGrid.addComponent(salesTypeSelect, 8, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("max_credit_period")), 0, 2);
			masterDetailsGrid.addComponent(creditPeriodTextField, 2, 2);

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 6, 2);
			masterDetailsGrid.addComponent(refNoField, 8, 2);

			masterDetailsGrid.setStyleName("master_border");

			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");

			newUnitButton = new SButton();
			newUnitButton.setStyleName("smallAddNewBtnStyle");
			newUnitButton.setDescription("Add new Unit");
			unitMapButton = new SButton();
			unitMapButton.setStyleName("mapBtnStyle");
			unitMapButton
					.setDescription("Set Convertion Quantity ( Unit Management )");

			changeStkButton = new SButton();
			changeStkButton.setStyleName("loadAllBtnStyle");
			changeStkButton.setDescription("Change Stock");

			newItemButton.setDescription("Add new Item");

			quantityTextField = new STextField(getPropertyName("quantity"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			quantityTextField
					.setDescription("Quantity of this Item (In seleceted Unit)");

			convertionQtyTextField = new STextField(
					getPropertyName("convertion_qty"), 40);
			convertionQtyTextField.setStyleName("textfield_align_right");
			convertionQtyTextField
					.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");

			convertedQtyTextField = new STextField(
					getPropertyName("converted_qty"), 60);
			convertedQtyTextField.setStyleName("textfield_align_right");
			convertedQtyTextField
					.setDescription("Converted Quantity in Basic Unit");
			convertedQtyTextField.setReadOnly(true);

			unitSelect = new SNativeSelect(getPropertyName("unit"), 60, null,
					"id", "symbol");
			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setNewValue("0.00");
			unitPriceTextField.setStyleName("textfield_align_right");

			if (!settings.isSALE_PRICE_EDITABLE()) {
				unitPriceTextField.setReadOnly(true);
			}

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

			discountTextField = new STextField(getPropertyName("discount"), 80,
					"0.0");
			discountTextField.setStyleName("textfield_align_right");

			netPriceTextField = new STextField(getPropertyName("net_price"),
					100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("textfield_align_right");

			expiry_date = new SDateField(getPropertyName("exp_date"), 100,
					"dd/MMM/yyyy", new Date());
			manufaturing_date = new SDateField(getPropertyName("mfg_date"),
					100, "dd/MMM/yyyy", new Date());
			itemSelectCombo = new SComboField(getPropertyName("item"), 150,
					daoObj.getAllItemsWithAllStocks(getOfficeID()), "id",
					"name", true, getPropertyName("select"));
			barcodeField = new STextField(getPropertyName("barcode"), 80);
			barcodeField.setImmediate(true);

			GradeModel gradeModel = new GradeModel(0, "None");
			List grdLis = new ArrayList();
			grdLis.add(0, gradeModel);
			grdLis.addAll(gradeDao.getAllGrades(getOfficeID()));
			gradeComboField = new SNativeSelect(getPropertyName("grade"), 80,
					grdLis, "id", "name");
			gradeComboField.setValue((long) 0);

			stockSelectList = new SListSelect(getPropertyName("stock"));
			stockSelectList.setHeight(200 + "px");
			// stockSelectList.setMultiSelect(true);

			rackSelectList = new SComboField(getPropertyName("rack"));

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SHorizontalLayout buttonLay = new SHorizontalLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			hrz1 = new SHorizontalLayout();
			hrz1.addComponent(itemSelectCombo);
			hrz1.addComponent(newItemButton);
			hrz1.addComponent(changeStkButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);
			hrz1.setComponentAlignment(changeStkButton, Alignment.BOTTOM_LEFT);

			if (settings.isBARCODE_ENABLED())
				addingGrid.addComponent(barcodeField);

			addingGrid.addComponent(hrz1);

			if (settings.isGRADING_ENABLED())
				addingGrid.addComponent(gradeComboField);

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

			addingGrid.addComponent(convertionQtyTextField);
			addingGrid.addComponent(convertedQtyTextField);

			priceListButton.setDescription("Price History");
			priceListButton.setStyleName("showHistoryBtnStyle");

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
			addingGrid.setColumnExpandRatio(7, 3);
			addingGrid.setColumnExpandRatio(8, 3);

			addingGrid.setWidth("1230");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1000, 200);

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
					getPropertyName("tax_percentage"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_AMT, Double.class, null,
					getPropertyName("tax_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null,
					getPropertyName("discount"), null, Align.CENTER);
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
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
					TBC_STOCK_ID, null, Align.RIGHT);

			table.addContainerProperty(TBC_CONVERTION_QTY, Double.class, null,
					getPropertyName("convertion_qty"), null, Align.RIGHT);

			table.addContainerProperty(TBC_QTY_IN_BASIC_UNI, Double.class,
					null, getPropertyName("qty_basic_unit"), null, Align.RIGHT);

			table.addContainerProperty(TBC_GRADE_ID, Long.class, null,
					TBC_GRADE_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_GRADE, String.class, null,
					getPropertyName("grade"), null, Align.RIGHT);

			table.addContainerProperty(TBC_RACK_ID, Long.class, null,
					TBC_RACK_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_RACK_NAME, String.class, null,
					getPropertyName("rack_name"), null, Align.RIGHT);

			table.addContainerProperty(TBC_OLD_QTY, Double.class, null,
					getPropertyName("old_quantity"), null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
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
			table.setColumnFooter(TBC_DISCOUNT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1230");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			foreignCurrField = new STextField(null, 100);
			foreignCurrField.setValue("0.0");
			foreignCurrField.setStyleName("textfield_align_right");
			foreignCurrField.setReadOnly(true);

			grandTotalAmtTextField = new STextField(null, 120, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			// grandTotalAmtTextField.setStyleName("textfield_align_right");

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

			comment = new STextArea(null, 250, 40);

			bottomGrid.addComponent(new SLabel(""), 1, 0);

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
						getPropertyName("other_charge")), 2, 1);
				bottomGrid.addComponent(shippingChargeTextField, 3, 1);
				bottomGrid.setComponentAlignment(shippingChargeTextField,
						Alignment.TOP_RIGHT);

			}

			bottomGrid.addComponent(new SLabel(getPropertyName("discount")), 5,
					1);
			bottomGrid.addComponent(discountMainField, 6, 1);

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),
					2, 3);
			bottomGrid.addComponent(grandTotalAmtTextField, 3, 3);

			// bottomGrid.addComponent(new SLabel("Paying Amt :"), 2, 3);
			// bottomGrid.addComponent(payingAmountTextField, 3, 3);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			if (settings.isMULTIPLE_CURRENCY_ENABLED()) {
				CurrencyModel cm = rateDao.getCurrencyModel(getCurrencyID());
				bottomGrid.addComponent(new SLabel(getPropertyName("amount_in")
						+ cm.getCode()), 5, 3);
				bottomGrid.addComponent(foreignCurrField, 6, 3);
			}

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

			cancelSalesButton = new SButton(getPropertyName("cancel"), 78);
			cancelSalesButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			cancelSalesButton.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveSalesButton);
			mainButtonLayout.addComponent(updateSalesButton);
			if (!settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(deleteSalesButton);
			else
				mainButtonLayout.addComponent(cancelSalesButton);
			updateSalesButton.setVisible(false);
			deleteSalesButton.setVisible(false);
			cancelSalesButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			// Added by anil
			printButton = new SButton(getPropertyName("print"));
			printButton.setIcon(new ThemeResource("icons/print.png"));
			mainButtonLayout.addComponent(printButton);
			printButton.setVisible(false);

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
			customerSelect.focus();
			      
			pannel.setContent(windowNotif);

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

			newItemWindow = new SDialogBox(getPropertyName("add_item"), 500,
					600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			newItemWindow.addComponent(itemPanel);

			Iterator itr = cashOrCreditRadio.getItemIds().iterator();
			itr.next();
			cashOrCreditRadio.setValue(itr.next());

			stockSelectList.setNullSelectionAllowed(false);

			stkDoneButton = new SButton(getPropertyName("done"));

			SVerticalLayout lay = new SVerticalLayout();
			// lay.setWidth("600");
			// lay.setHeight("250");
			lay.addComponent(stockSelectList);

			if (settings.isRACK_ENABLED()) {
				lay.addComponent(new SHorizontalLayout(true, rackSelectList,
						rackBalance));
			}

			lay.addComponent(stkDoneButton);
			lay.setComponentAlignment(stkDoneButton, Alignment.MIDDLE_CENTER);
			pop = new SPopupView("", lay);
			hrz1.addComponent(pop);

			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)salesNumberList.getValue(),confirmBox.getUserID());
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
						if(salesNumberList.getValue()!=null && !salesNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)salesNumberList.getValue(),
									"Sales : No. "+salesNumberList.getItemCaption(salesNumberList.getValue()));
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
						if(salesNumberList.getValue()!=null && !salesNumberList.getValue().toString().equals("0")) {
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
					salesNumberList.setValue((long) 0);
				}
			});

			stkDoneButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					pop.setPopupVisible(false);
					quantityTextField.focus();
				}
			});

			changeStkButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (itemSelectCombo.getValue() != null
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
						hrz1.addComponent(pop);
						pop.setPopupVisible(true);
					}
				}
			});

			gradeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {

						if (itemSelectCombo.getValue() != null
								&& !itemSelectCombo.getValue().equals("")
								&& unitSelect.getValue() != null
								&& !unitSelect.getValue().equals("")) {
							GradeModel gradeModel = gradeDao
									.getGrade((Long) gradeComboField.getValue());
							double price = comDao.getItemPrice(
									(Long) itemSelectCombo.getValue(),
									(Long) unitSelect.getValue(),
									toInt(salesTypeSelect.getValue().toString()));
							if (gradeModel != null) {
								if (gradeModel.getPercentage() != 0)
									price = price * gradeModel.getPercentage()
											/ 100;
								else
									price = 0;
							}

							unitPriceTextField
									.setValue(asString(getConvertedAmount(price)));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			priceListButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (itemSelectCombo.getValue() != null
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

							list = new PurchaseDao().getPurchaseRateHistory(
									(Long) itemSelectCombo.getValue(),
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

			customerSelect
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								salesOrdersOptions.setContainerDataSource(null);

								if (customerSelect.getValue() != null
										&& !customerSelect.getValue()
												.equals("")) {
									CustomerModel cust = custDao
											.getCustomerFromLedger((Long) customerSelect
													.getValue());
									if (cust != null) {
										responsibleEmployeeCombo.setValue(cust
												.getResponsible_person());
										creditPeriodTextField.setValue(asString(cust
												.getMax_credit_period()));
										customerSelect
												.setDescription("<h1><i>Current Balance</i> : "
														+ roundNumber(cust
																.getLedger()
																.getCurrent_balance())
														+ "</h1>");
										salesTypeSelect.setValue(cust
												.getSales_type());

										if (settings
												.isDISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT()) {
											customerSelect
													.setDescription("<h1><i>Current Balance</i> : "
															+ roundNumber(cust
																	.getLedger()
																	.getCurrent_balance())
															+ "</h1><br>"
															+ "<h2><i>Credit Limit</i> : "
															+ cust.getCredit_limit()
															+ "</h2>");
											if ((salesNumberList.getValue() == null
													|| salesNumberList
															.getValue().equals(
																	"") || salesNumberList
													.getValue().toString()
													.equals("0"))
													&& (cust.getCredit_limit() < cust
															.getLedger()
															.getCurrent_balance())) {
												buttonsGrid.setVisible(false);
												SNotification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
											} else {
												buttonsGrid.setVisible(true);
											}

										}
									}
								} else {
									customerSelect.setDescription(null);
									Iterator itt = salesTypeSelect.getItemIds()
											.iterator();
									if (itt.hasNext())
										salesTypeSelect.setValue(itt.next());
								}

								if (session.getAttribute("SO_Select_Disabled") == null) {

									/*
									 * Iterator
									 * it=table.getItemIds().iterator(); List
									 * delList=new ArrayList(); while
									 * (it.hasNext()) { Object obj=it.next();
									 * Item item = table.getItem(obj); if((Long)
									 * item
									 * .getItemProperty(TBC_PO_ID).getValue()
									 * >0){ delList.add(obj); } } Iterator
									 * it1=delList.iterator();
									 * while(it1.hasNext()) {
									 * table.removeItem(it1.next()); }
									 */

									if (session
											.getAttribute("SO_Already_Added") != null) {
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

											getUI().getCurrent().addWindow(
													poWindow);

											if (salesOrdersOptions.getItemIds()
													.size() > 0) {
												salesOrdersOptions.focus();
												setRequiredError(
														salesOrdersOptions,
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
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}
							barcodeField.focus();
							barcodeField.setValue("");
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

							if (SOs.size() > 0)
								sales_number = daoObj
										.getSalesOrderNumberFromSOID(SOs
												.iterator().next());

							List itemsList = daoObj
									.getAllItemsFromSalesOrders(SOs);

							int id = 0, ct = table.getItemIds().size();
							Iterator it1 = table.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							InventoryDetailsPojo invObj;
							double netTotal = 0, ttl_bfr_tax, ttl_bfr_disc, perc;
							long stk_id;
							Iterator it = itemsList.iterator();
							while (it.hasNext()) {
								invObj = (InventoryDetailsPojo) it.next();

								netTotal = 0;
								ttl_bfr_tax = 0;
								ttl_bfr_disc = 0;
								if (invObj.getTax_percentage() > 0) {
									perc = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											* invObj.getTax_percentage() / 100));
									netTotal = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ perc
											- invObj.getDiscount_amount()));
									ttl_bfr_tax = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())));
									ttl_bfr_disc = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ perc));
								} else {
									netTotal = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ invObj.getTax_amount()
											- invObj.getDiscount_amount()));
									ttl_bfr_tax = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())));
									ttl_bfr_disc = getConvertedAmount(roundNumber((invObj
											.getUnit_price() * invObj
											.getBalance())
											+ invObj.getTax_amount()));
								}

								id++;
								ct++;

								stk_id = comDao.getDefaultStockToSelect(invObj
										.getItem_id());

								table.addItem(
										new Object[] {
												ct,
												invObj.getItem_id(),
												invObj.getItem_code(),
												invObj.getItem_name(),
												invObj.getBalance(),
												invObj.getUnit_id(),
												invObj.getUnit_name(),
												getConvertedAmount(invObj
														.getUnit_price()),
												invObj.getTax_id(),
												getConvertedAmount(invObj
														.getTax_amount()),
												invObj.getTax_percentage(),
												getConvertedAmount(invObj
														.getDiscount_amount()),
												ttl_bfr_tax,
												invObj.getOrder_id(),
												invObj.getInventry_details_id(),
												getConvertedAmount(invObj
														.getCess_amount()),
												roundNumber(ttl_bfr_disc
														+ getConvertedAmount(invObj
																.getCess_amount())),
												roundNumber(netTotal
														+ getConvertedAmount(invObj
																.getCess_amount())),
												invObj.getQuantity_in_basic_unit(),
												stk_id,
												(invObj.getQuantity_in_basic_unit() / invObj
														.getQunatity()),
												(long) 0, "None", (long) 0, "",
												0.0 }, id);

							}

							table.setVisibleColumns(requiredHeaders);

							getUI().removeWindow(poWindow);

							calculateTotals();
							calculateForeignCurrencyAmount();

						} else {
							getUI().removeWindow(poWindow);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
						// TODO: handle exception
					}
					barcodeField.focus();
					barcodeField.setValue("");
				}
			});

			saveSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

//						if (isValid()) {
//
//							long customer_id = (Long) customerSelect.getValue();
//
//							double costof_inv_amt = 0;
//
//							SalesModel salObj = new SalesModel();
//
//							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
//
//							boolean ordrNo_to_salNo = false;
//
//							SalesInventoryDetailsModel invObj;
//							Item item;
//							double std_cost;
//							Iterator it = table.getItemIds().iterator();
////							while (it.hasNext()) {
////								invObj = new SalesInventoryDetailsModel();
////
////								item = table.getItem(it.next());
////
////								invObj.setItem(new ItemModel((Long) item
////										.getItemProperty(TBC_ITEM_ID)
////										.getValue()));
////								invObj.setQunatity((Double) item
////										.getItemProperty(TBC_QTY).getValue());
////								invObj.setBalance((Double) item
////										.getItemProperty(TBC_QTY).getValue());
////
////								if (taxEnable) {
////									invObj.setTax(new TaxModel((Long) item
////											.getItemProperty(TBC_TAX_ID)
////											.getValue()));
////									invObj.setTax_amount(getReverseConvertedAmount((Double) item
////											.getItemProperty(TBC_TAX_AMT)
////											.getValue()));
////									invObj.setTax_percentage((Double) item
////											.getItemProperty(TBC_TAX_PERC)
////											.getValue());
////								} else {
////									invObj.setTax(new TaxModel(1));
////									invObj.setTax_amount(0);
////									invObj.setTax_percentage(0);
////								}
////
////								invObj.setUnit(new UnitModel((Long) item
////										.getItemProperty(TBC_UNIT_ID)
////										.getValue()));
////								invObj.setUnit_price(getReverseConvertedAmount((Double) item
////										.getItemProperty(TBC_UNIT_PRICE)
////										.getValue()));
////
////								invObj.setStk_id((Long) item.getItemProperty(
////										TBC_STOCK_ID).getValue());
////
////								if (isDiscountEnable()) {
////									invObj.setDiscount_amount(getReverseConvertedAmount((Double) item
////											.getItemProperty(TBC_DISCOUNT)
////											.getValue()));
////								}
////
////								invObj.setOrder_id((Long) item.getItemProperty(
////										TBC_PO_ID).getValue());
////								invObj.setCess_amount(getReverseConvertedAmount((Double) item
////										.getItemProperty(TBC_CESS_AMT)
////										.getValue()));
////
////								/*
////								 * invObj.setManufacturing_date(CommonUtil
////								 * .getSQLDateFromUtilDate((Date) item
////								 * .getItemProperty( TBC_MANUFACT_DATE)
////								 * .getValue()));
////								 * invObj.setExpiry_date(CommonUtil
////								 * .getSQLDateFromUtilDate((Date) item
////								 * .getItemProperty( TBC_EXPIRE_DATE)
////								 * .getValue())); invObj.setStock_id((Long)
////								 * item.getItemProperty(
////								 * TBC_STOCK_ID).getValue());
////								 */
////
////								if (invObj.getOrder_id() != 0)
////									ordrNo_to_salNo = true;
////
////								invObj.setId((Long) item.getItemProperty(
////										TBC_INV_ID).getValue());
////
////								invObj.setQuantity_in_basic_unit((Double) item
////										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
////										.getValue());
////
////								invObj.setGradeId((Long) item.getItemProperty(
////										TBC_GRADE_ID).getValue());
////
////								invObj.setRack_id((Long) item.getItemProperty(
////										TBC_RACK_ID).getValue());
////
////								itemsList.add(invObj);
////
////								std_cost = itmDao.getStandardCost(invObj
////										.getItem().getId());
////								costof_inv_amt += invObj.getQunatity()
////										* std_cost;
////
////							}
//
//							if (isExciceDutyEnable()) {
//								salObj.setExcise_duty(getReverseConvertedAmount(toDouble(exciseDutyTextField
//										.getValue())));
//							}
//							if (isShippingChargeEnable()) {
//								salObj.setShipping_charge(getReverseConvertedAmount(toDouble(shippingChargeTextField
//										.getValue())));
//							}
//
//							salObj.setSales_type((Long) salesTypeSelect
//									.getValue());
//							salObj.setCreated_time(CommonUtil
//									.getCurrentDateTime());
//							salObj.setCredit_period(toInt(creditPeriodTextField
//									.getValue()));
//							salObj.setPayment_amount(getReverseConvertedAmount(toDouble(payingAmountTextField
//									.getValue())));
//							salObj.setAmount(getReverseConvertedAmount(toDouble(grandTotalAmtTextField
//									.getValue())));
//							salObj.setCurrencyId((Long) currencyNativeSelect
//									.getValue());
//							salObj.setForeignCurrencyAmount(toDouble(foreignCurrField
//									.getValue()));
//							salObj.setResponsible_person((Long) responsibleEmployeeCombo
//									.getValue());
//
//							salObj.setComments(comment.getValue());
//							salObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
//							salObj.setLogin(new S_LoginModel(getLoginID()));
//							salObj.setOffice(new S_OfficeModel(getOfficeID()));
//							salObj.setStatus(1);
//							salObj.setCustomer(new LedgerModel(
//									(Long) customerSelect.getValue()));
//							salObj.setInventory_details_list(itemsList);
//							salObj.setSales_local_type(((Integer) salesLocalTypeField
//									.getValue()));
//							salObj.setDiscount_amount(getReverseConvertedAmount(toDouble(discountMainField
//									.getValue())));
//
//							if (settings.isUSE_SALES_NO_IN_SALES_ORDER()) {
//								if (ordrNo_to_salNo)
//									salObj.setSales_number(sales_number);
//								else
//									salObj.setSales_number(getNextSequence(
//											"Sales Number", getLoginID()));
//							} else
//								salObj.setSales_number(getNextSequence(
//										"Sales Number", getLoginID()));
//
//							salObj.setSales_person((Long) employSelect
//									.getValue());
//							salObj.setVoucher_no(toLong(refNoField.getValue()));
//
//							FinTransaction trans = new FinTransaction();
//							double totalAmt = getReverseConvertedAmount(toDouble(grandTotalAmtTextField
//									.getValue()));
//							double netAmt = totalAmt;
//
//							double amt = 0;
//
//							double payingAmt = getReverseConvertedAmount(toDouble(payingAmountTextField
//									.getValue()));
//
//							long salesAcc = settings.getSALES_ACCOUNT();
//							if ((Integer) salesLocalTypeField.getValue() == SConstants.local_foreign_type.FOREIGN)
//								salesAcc = settings.getSALES_FOREIGN_ACCOUNT();
//
//							if (payingAmt == netAmt) {
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getCASH_ACCOUNT(),
//										roundNumber(payingAmt));
//								trans.addTransaction(SConstants.CR, salesAcc,
//										customer_id, roundNumber(netAmt));
//
//								salObj.setStatus(1);
//								status = 1;
//							} else if (payingAmt == 0) {
//								trans.addTransaction(SConstants.CR, salesAcc,
//										customer_id, roundNumber(netAmt));
//								salObj.setStatus(2);
//								status = 2;
//							} else {
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getCASH_ACCOUNT(),
//										roundNumber(payingAmt));
//								trans.addTransaction(SConstants.CR, salesAcc,
//										customer_id, roundNumber(netAmt));
//								status = 3;
//								salObj.setStatus(3);
//							}
//
//							if (settings.getINVENTORY_ACCOUNT() != 0) {
//								trans.addTransaction(SConstants.CR,
//										settings.getINVENTORY_ACCOUNT(),
//										settings.getCGS_ACCOUNT(),
//										roundNumber(costof_inv_amt));
//								totalAmt -= costof_inv_amt;
//							}
//
//							if (taxEnable) {
//								if (settings.getSALES_TAX_ACCOUNT() != 0) {
//									amt = getReverseConvertedAmount(toDouble(table
//											.getColumnFooter(TBC_TAX_AMT)));
//									if (amt != 0) {
//										trans.addTransaction(
//												SConstants.CR,
//												settings.getSALES_TAX_ACCOUNT(),
//												settings.getCGS_ACCOUNT(),
//												roundNumber(amt));
//										totalAmt -= amt;
//									}
//								}
//
//								if (settings.isCESS_ENABLED()) {
//									if (settings.getCESS_ACCOUNT() != 0) {
//										amt = getReverseConvertedAmount(toDouble(table
//												.getColumnFooter(TBC_CESS_AMT)));
//										if (amt != 0) {
//											trans.addTransaction(SConstants.CR,
//													settings.getCESS_ACCOUNT(),
//													settings.getCGS_ACCOUNT(),
//													roundNumber(amt));
//											totalAmt -= amt;
//										}
//									}
//								}
//							}
//
//							if (settings.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
//								amt = getReverseConvertedAmount(toDouble(exciseDutyTextField
//										.getValue()));
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_EXCISE_DUTY_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//
//							}
//
//							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
//								amt = getReverseConvertedAmount(toDouble(shippingChargeTextField
//										.getValue()));
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
//								amt = 0;
//								try {
//									amt = getReverseConvertedAmount(toDouble(discountMainField
//											.getValue()));
//								} catch (Exception e) {
//								}
//
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_DESCOUNT_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_REVENUE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(totalAmt));
//								}
//							}
//							
//							long id = daoObj.save(salObj, trans.getTransaction(
//									SConstants.SALES, CommonUtil
//											.getSQLDateFromUtilDate(date
//													.getValue())), payingAmt,
//									settings.getUPDATE_RATE_AND_CONV_QTY());
//							
//							saveActivity(getOptionId(),"New Sales Created. Bill No : "
//											+ salObj.getSales_number()
//											+ ", Customer : "
//											+ customerSelect
//													.getItemCaption(customerSelect
//															.getValue())
//											+ ", Amount : "
//											+ salObj.getAmount(),salObj.getId());
//							
//							loadSale(id);
//							
//							Notification.show(getPropertyName("save_success"),
//									Type.WARNING_MESSAGE);
//							
//							/*String notifcn="Some items are in negetive stock. ";
//							boolean negetive_notif=false;
//							invObj=null;
//							ItemModel itmObj;
//							Iterator<SalesInventoryDetailsModel> itr=salObj.getInventory_details_list().iterator();
//							while (itr.hasNext()) {
//								invObj = itr.next();
//								itmObj=itmDao.getNameAndCurrentBal(invObj.getItem().getId());
//								if(itmObj.getCurrent_balalnce()<0) {
//									notifcn+=itmObj.getName()+" : "+itmObj.getCurrent_balalnce()+", ";
//									negetive_notif=true;
//								}
//							}
//							if(negetive_notif)
//								Notification.show(notifcn,Type.ERROR_MESSAGE);*/
//						}
						
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});
			
			salesNumberList.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
//							try {
//
//								removeAllErrors();
//
//								responsibleEmployeeCombo.setValue(null);
//
//								updateSalesButton.setVisible(true);
//								deleteSalesButton.setVisible(true);
//								cancelSalesButton.setVisible(true);
//								printButton.setVisible(true);
//								saveSalesButton.setVisible(false);
//								if (salesNumberList.getValue() != null
//										&& !salesNumberList.getValue()
//												.toString().equals("0")) {
//
//									SalesModel salObj = daoObj
//											.getSale((Long) salesNumberList
//													.getValue());
//
//									table.setVisibleColumns(allHeaders);
//
//									table.removeAllItems();
//
//									currencyNativeSelect.setValue(salObj
//											.getCurrencyId());
//
//									SalesInventoryDetailsModel invObj;
//									double netTotal;
//									Iterator it = salObj
//											.getInventory_details_list()
//											.iterator();
//									GradeModel grd;
//									String grdName = "";
//									while (it.hasNext()) {
//										invObj = (SalesInventoryDetailsModel) it
//												.next();
//
//										grd = gradeDao.getGrade(invObj
//												.getGradeId());
//										grdName = "None";
//										if (grd != null)
//											grdName = grd.getName();
//
//										netTotal = getConvertedAmount(roundNumber((invObj
//												.getUnit_price() * invObj
//												.getQunatity())
//												+ invObj.getTax_amount()
//												+ invObj.getCess_amount()
//												- invObj.getDiscount_amount()));
//
//										table.addItem(
//												new Object[] {
//														table.getItemIds()
//																.size() + 1,
//														invObj.getItem()
//																.getId(),
//														invObj.getItem()
//																.getItem_code(),
//														invObj.getItem()
//																.getName(),
//														invObj.getQunatity(),
//														invObj.getUnit()
//																.getId(),
//														invObj.getUnit()
//																.getSymbol(),
//														getConvertedAmount(invObj
//																.getUnit_price()),
//														invObj.getTax().getId(),
//														getConvertedAmount(invObj
//																.getTax_amount()),
//														invObj.getTax_percentage(),
//														getConvertedAmount(invObj
//																.getDiscount_amount()),
//														getConvertedAmount((invObj
//																.getUnit_price() * invObj
//																.getQunatity())),
//														invObj.getOrder_id(),
//														(long) 0,
//														getConvertedAmount(invObj
//																.getCess_amount()),
//														getConvertedAmount(roundNumber((invObj
//																.getUnit_price() * invObj
//																.getQunatity())
//																+ invObj.getTax_amount()
//																+ invObj.getCess_amount())),
//														netTotal,
//														invObj.getQuantity_in_basic_unit(),
//														invObj.getStk_id(),
//														(invObj.getQuantity_in_basic_unit() / invObj
//																.getQunatity()),
//														invObj.getGradeId(),
//														grdName,
//														invObj.getRack_id(),
//														new RackDao()
//																.getRackName(invObj
//																		.getRack_id()),
//														invObj.getQuantity_in_basic_unit() },
//												table.getItemIds().size() + 1);
//									}
//
//									table.setVisibleColumns(requiredHeaders);
//
//									discountMainField
//											.setNewValue(asString(getConvertedAmount(salObj
//													.getDiscount_amount())));
//									// buildingSelect.setValue(salObj.getBuilding().getId());
//									comment.setValue(salObj.getComments());
//									date.setValue(salObj.getDate());
//									// expected_delivery_date.setValue(salObj.getExpected_delivery_date());
//
//									session.setAttribute("SO_Select_Disabled",
//											'Y');
//
//									customerSelect.setValue(salObj
//											.getCustomer().getId());
//									salesLocalTypeField.setValue(salObj
//											.getSales_local_type());
//
//									employSelect.setNewValue(salObj
//											.getSales_person());
//
//									creditPeriodTextField
//											.setValue(asString(salObj
//													.getCredit_period()));
//									refNoField.setValue(asString(salObj
//											.getVoucher_no()));
//									shippingChargeTextField
//											.setValue(asString(getConvertedAmount(salObj
//													.getShipping_charge())));
//
//									grandTotalAmtTextField
//											.setNewValue(asString(getConvertedAmount(salObj
//													.getAmount())));
//
//									exciseDutyTextField
//											.setValue(asString(getConvertedAmount(salObj
//													.getExcise_duty())));
//									payingAmountTextField
//											.setValue(asString(getConvertedAmount(salObj
//													.getPayment_amount())));
//
//									if (salObj.getPayment_amount() == salObj
//											.getAmount()) {
//										cashOrCreditRadio.setValue((long) 1);
//									} else {
//										cashOrCreditRadio.setValue((long) 2);
//									}
//
//									responsibleEmployeeCombo.setValue(salObj
//											.getResponsible_person());
//
//									salesTypeSelect.setValue(salObj
//											.getSales_type());
//
//									// foreignCurrField.setNewValue(asString(salObj.getForeignCurrencyAmount()));
//
//									isValid();
//									updateSalesButton.setVisible(true);
//									printButton.setVisible(true);
//									deleteSalesButton.setVisible(true);
//									cancelSalesButton.setVisible(true);
//									saveSalesButton.setVisible(false);
//
//									status = salObj.getStatus();
//
//								} else {
//									table.removeAllItems();
//
//									discountMainField.setNewValue("0.0");
//									grandTotalAmtTextField.setNewValue("0.0");
//									foreignCurrField.setNewValue("0.0");
//									currencyNativeSelect
//											.setNewValue(getCurrencyID());
//									payingAmountTextField.setValue("0.0");
//									// buildingSelect.setValue(null);
//									comment.setValue("");
//									date.setValue(new Date(getWorkingDate()
//											.getTime()));
//									// expected_delivery_date.setValue(new
//									// Date());
//									customerSelect.setValue(null);
//									employSelect.setNewValue(getLoginID());
//									salesLocalTypeField
//											.setNewValue(SConstants.local_foreign_type.LOCAL);
//
//									shippingChargeTextField.setValue("0");
//									exciseDutyTextField.setValue("0");
//
//									creditPeriodTextField.setValue("0");
//									refNoField.setValue("0");
//									saveSalesButton.setVisible(true);
//									updateSalesButton.setVisible(false);
//									printButton.setVisible(false);
//									deleteSalesButton.setVisible(false);
//									cancelSalesButton.setVisible(false);
//								}
//
//								calculateTotals();
//
//								itemSelectCombo.setNewValue(null);
//								itemSelectCombo.focus();
//								quantityTextField.setValue("0.0");
//								unitPriceTextField.setNewValue("0.0");
//								netPriceTextField.setNewValue("0.0");
//								discountTextField.setNewValue("0.0");
//
//								customerSelect.focus();
//
//								if (!isFinYearBackEntry()) {
//									saveSalesButton.setVisible(false);
//									updateSalesButton.setVisible(false);
//									deleteSalesButton.setVisible(false);
//									cancelSalesButton.setVisible(false);
//									if (salesNumberList.getValue() == null
//											|| salesNumberList.getValue()
//													.toString().equals("0")) {
//										Notification
//												.show(getPropertyName("warning_transaction"),
//														Type.WARNING_MESSAGE);
//									}
//								}
//
//							} catch (Exception e) {
//								e.printStackTrace();
//								Notification.show(getPropertyName("error"),
//										Type.ERROR_MESSAGE);
//							}
						}

					});

//			updateSalesButton.addClickListener(new Button.ClickListener() {
//				@Override
//				public void buttonClick(ClickEvent event) {
//
//					try {
//
//						if (isValid()) {
//
//							long customer_id = (Long) customerSelect.getValue();
//
//							double costof_inv_amt = 0;
//
//							SalesModel salObj = daoObj
//									.getSale((Long) salesNumberList.getValue());
//
//							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
//
//							SalesInventoryDetailsModel invObj;
//							Item item;
//							double std_cost;
//							Iterator it = table.getItemIds().iterator();
//							while (it.hasNext()) {
//								invObj = new SalesInventoryDetailsModel();
//
//								item = table.getItem(it.next());
//
//								invObj.setItem(new ItemModel((Long) item
//										.getItemProperty(TBC_ITEM_ID)
//										.getValue()));
//								invObj.setQunatity((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//								invObj.setBalance((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//
//								if (taxEnable) {
//									invObj.setTax(new TaxModel((Long) item
//											.getItemProperty(TBC_TAX_ID)
//											.getValue()));
//									invObj.setTax_amount(getReverseConvertedAmount((Double) item
//											.getItemProperty(TBC_TAX_AMT)
//											.getValue()));
//									invObj.setTax_percentage((Double) item
//											.getItemProperty(TBC_TAX_PERC)
//											.getValue());
//								} else {
//									invObj.setTax(new TaxModel(1));
//									invObj.setTax_amount(0);
//									invObj.setTax_percentage(0);
//								}
//
//								invObj.setUnit(new UnitModel((Long) item
//										.getItemProperty(TBC_UNIT_ID)
//										.getValue()));
//								invObj.setUnit_price(getReverseConvertedAmount((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue()));
//
//								invObj.setStk_id((Long) item.getItemProperty(
//										TBC_STOCK_ID).getValue());
//
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount(getReverseConvertedAmount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue()));
//								}
//								invObj.setOrder_id((Long) item.getItemProperty(
//										TBC_PO_ID).getValue());
//								invObj.setCess_amount(getReverseConvertedAmount((Double) item
//										.getItemProperty(TBC_CESS_AMT)
//										.getValue()));
//
//								invObj.setQuantity_in_basic_unit((Double) item
//										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
//										.getValue());
//
//								invObj.setGradeId((Long) item.getItemProperty(
//										TBC_GRADE_ID).getValue());
//
//								invObj.setRack_id((Long) item.getItemProperty(
//										TBC_RACK_ID).getValue());
//
//								itemsList.add(invObj);
//
//								std_cost = itmDao.getStandardCost(invObj
//										.getItem().getId());
//								costof_inv_amt += invObj.getQunatity()
//										* std_cost;
//
//							}
//
//							if (isExciceDutyEnable()) {
//								salObj.setExcise_duty(getReverseConvertedAmount(toDouble(exciseDutyTextField
//										.getValue())));
//							}
//							if (isShippingChargeEnable()) {
//								salObj.setShipping_charge(getReverseConvertedAmount(toDouble(shippingChargeTextField
//										.getValue())));
//							}
//							salObj.setSales_type((Long) salesTypeSelect
//									.getValue());
//							salObj.setCredit_period(toInt(creditPeriodTextField
//									.getValue()));
//							
//							salObj.setPayment_amount(getReverseConvertedAmount(toDouble(payingAmountTextField
//									.getValue())));
//							salObj.setAmount(getReverseConvertedAmount(toDouble(grandTotalAmtTextField
//									.getValue())));
//							salObj.setCurrencyId((Long) currencyNativeSelect
//									.getValue());
//							salObj.setForeignCurrencyAmount(toDouble(foreignCurrField
//									.getValue()));
//							
//							// salObj.setBuilding(new BuildingModel((Long)
//							// buildingSelect.getValue()));
//							salObj.setComments(comment.getValue());
//							salObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
//							salObj.setLogin(new S_LoginModel(getLoginID()));
//							salObj.setOffice(new S_OfficeModel(getOfficeID()));
//							salObj.setCustomer(new LedgerModel(
//									(Long) customerSelect.getValue()));
//							salObj.setInventory_details_list(itemsList);
//
//							salObj.setResponsible_person((Long) responsibleEmployeeCombo
//									.getValue());
//
//							salObj.setSales_person((Long) employSelect
//									.getValue());
//							salObj.setVoucher_no(toLong(refNoField.getValue()));
//							salObj.setSales_local_type(((Integer) salesLocalTypeField
//									.getValue()));
//							salObj.setDiscount_amount(getReverseConvertedAmount(toDouble(discountMainField
//									.getValue())));
//
//							FinTransaction trans = new FinTransaction();
//							double totalAmt = getReverseConvertedAmount(toDouble(grandTotalAmtTextField
//									.getValue()));
//							double netAmt = totalAmt;
//
//							double amt = 0;
//
//							double payingAmt = getReverseConvertedAmount(toDouble(payingAmountTextField
//									.getValue()));
//
//							long salesAcc = settings.getSALES_ACCOUNT();
//							if ((Integer) salesLocalTypeField.getValue() == SConstants.local_foreign_type.FOREIGN)
//								salesAcc = settings.getSALES_FOREIGN_ACCOUNT();
//
//							if (payingAmt == netAmt) {
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getCASH_ACCOUNT(),
//										roundNumber(payingAmt));
//								trans.addTransaction(SConstants.CR, salesAcc,
//										customer_id, roundNumber(netAmt));
//
//								salObj.setStatus(1);
//								status = 1;
//							} else if (payingAmt == 0) {
//								trans.addTransaction(SConstants.CR, salesAcc,
//										customer_id, roundNumber(netAmt));
//								salObj.setStatus(2);
//								status = 2;
//							} else {
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getCASH_ACCOUNT(),
//										roundNumber(payingAmt));
//								trans.addTransaction(SConstants.CR, salesAcc,
//										customer_id, roundNumber(netAmt));
//								status = 3;
//								salObj.setStatus(3);
//							}
//
//							if (settings.getINVENTORY_ACCOUNT() != 0) {
//								trans.addTransaction(SConstants.DR,
//										settings.getINVENTORY_ACCOUNT(),
//										settings.getCGS_ACCOUNT(),
//										roundNumber(costof_inv_amt));
//								totalAmt -= costof_inv_amt;
//							}
//
//							if (settings.getSALES_TAX_ACCOUNT() != 0) {
//								amt = getReverseConvertedAmount(toDouble(table
//										.getColumnFooter(TBC_TAX_AMT)));
//								if (amt != 0) {
//									trans.addTransaction(SConstants.DR,
//											settings.getSALES_TAX_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (taxEnable) {
//								if (settings.isCESS_ENABLED()) {
//									if (settings.getCESS_ACCOUNT() != 0) {
//										amt = getReverseConvertedAmount(toDouble(table
//												.getColumnFooter(TBC_CESS_AMT)));
//										if (amt != 0) {
//											trans.addTransaction(SConstants.DR,
//													settings.getCESS_ACCOUNT(),
//													settings.getCGS_ACCOUNT(),
//													roundNumber(amt));
//											totalAmt -= amt;
//										}
//									}
//								}
//
//								if (settings.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
//									amt = toDouble(exciseDutyTextField
//											.getValue());
//									if (amt != 0) {
//										trans.addTransaction(
//												SConstants.CR,
//												settings.getSALES_EXCISE_DUTY_ACCOUNT(),
//												settings.getCGS_ACCOUNT(),
//												roundNumber(amt));
//										totalAmt -= amt;
//									}
//								}
//							}
//
//							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
//								amt = getReverseConvertedAmount(toDouble(shippingChargeTextField
//										.getValue()));
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
//								amt = 0;
//								try {
//									amt = getReverseConvertedAmount(toDouble(discountMainField
//											.getValue()));
//								} catch (Exception e) {
//								}
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_DESCOUNT_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_REVENUE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(totalAmt));
//								}
//							}
//
//							TransactionModel tran = daoObj
//									.getTransaction(salObj.getTransaction_id());
//							tran.setTransaction_details_list(trans
//									.getChildList());
//							tran.setDate(salObj.getDate());
//							tran.setLogin_id(getLoginID());
//
//							daoObj.update(salObj, tran, payingAmt);
//
//							saveActivity(
//									getOptionId(),
//									"Sales Updated. Bill No : "
//											+ salObj.getSales_number()
//											+ ", Customer : "
//											+ customerSelect
//													.getItemCaption(customerSelect
//															.getValue())
//											+ ", Amount : "
//											+ salObj.getAmount(),salObj.getId());
//
//							loadSale(salObj.getId());
//
//							Notification.show(
//									getPropertyName("update_success"),
//									Type.WARNING_MESSAGE);
//							
//							/*String notifcn="Some items are in negetive stock. ";
//							boolean negetive_notif=false;
//							invObj=null;
//							ItemModel itmObj;
//							Iterator<SalesInventoryDetailsModel> itr=salObj.getInventory_details_list().iterator();
//							while (itr.hasNext()) {
//								invObj = itr.next();
//								itmObj=itmDao.getNameAndCurrentBal(invObj.getItem().getId());
//								if(itmObj.getCurrent_balalnce()<0) {
//									notifcn+=itmObj.getName()+" : "+itmObj.getCurrent_balalnce()+", ";
//									negetive_notif=true;
//								}
//							}
//							if(negetive_notif)
//								Notification.show(notifcn,Type.ERROR_MESSAGE);*/
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//						Notification.show(getPropertyName("error"),
//								Type.ERROR_MESSAGE);
//					}
//				}
//			});

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

			cancelSalesButton.addClickListener(new Button.ClickListener() {
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
												daoObj.cancelSale((Long) salesNumberList
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
														.show(getPropertyName("success"),
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

			table.addValueChangeListener(new Property.ValueChangeListener() {

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

						itemSelectCombo.setNewValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());
						gradeComboField.setValue((Long) item.getItemProperty(
								TBC_GRADE_ID).getValue());
						unitSelect.setNewValue(item
								.getItemProperty(TBC_UNIT_ID).getValue());

						netPriceTextField.setNewValue(""
								+ roundNumber((Double) item.getItemProperty(
										TBC_NET_PRICE).getValue()));

						visibleAddupdateSalesButton(false, true);

						itemSelectCombo.focus();

						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());

						unitPriceTextField.setNewValue(""
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

						stockSelectList.setValue(item.getItemProperty(
								TBC_STOCK_ID).getValue());

						rackSelectList.setValue(item.getItemProperty(
								TBC_RACK_ID).getValue());

						convertionQtyTextField.setNewValue(""
								+ item.getItemProperty(TBC_CONVERTION_QTY)
										.getValue());

						if ((Long) item.getItemProperty(TBC_PO_ID).getValue() > 0) {
							itemSelectCombo.setReadOnly(true);
							unitSelect.setReadOnly(true);
							quantityTextField.focus();
						}

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

					} else {

						itemSelectCombo.setNewValue(null);
						itemSelectCombo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setNewValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");
						convertionQtyTextField.setValue("1");
						gradeComboField.setValue((long) 0);
						stockSelectList.setValue(null);
						rackSelectList.setNewValue(null);

						visibleAddupdateSalesButton(true, false);

						itemSelectCombo.focus();
					}
					barcodeField.focus();
					barcodeField.setValue("");
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

							Item item;
							// Iterator itr1 = table.getItemIds().iterator();
							List delList = new ArrayList();
							double qty, price, discount, tax_amt, tax_perc, total, cess_amt, cess_perc, conv_rat;
							/*
							 * while (itr1.hasNext()) { // Object obj =
							 * itr1.next(); item = table.getItem(itr1.next());
							 * 
							 * if (item.getItemProperty(TBC_ITEM_ID)
							 * .getValue().toString()
							 * .equals(itemSelectCombo.getValue() .toString())
							 * && item.getItemProperty(TBC_UNIT_ID) .getValue()
							 * .toString() .equals(unitSelect.getValue()
							 * .toString()) &&
							 * item.getItemProperty(TBC_STOCK_ID) .getValue()
							 * .toString() .equals(stockSelectList.getValue()
							 * .toString())) {
							 * 
							 * qty = 0; qty = (Double) item
							 * .getItemProperty(TBC_QTY) .getValue() +
							 * toDouble(quantityTextField .getValue()); price =
							 * toDouble(unitPriceTextField .getValue());
							 * item.getItemProperty(TBC_QTY).setValue(qty);
							 * item.getItemProperty(TBC_UNIT_PRICE)
							 * .setValue(price); discount = (Double) item
							 * .getItemProperty(TBC_DISCOUNT) .getValue() +
							 * toDouble(discountTextField .getValue());
							 * 
							 * tax_amt = 0; tax_perc = 0;
							 * 
							 * if (taxEnable) { tax_perc = (Double) item
							 * .getItemProperty(TBC_TAX_PERC) .getValue();
							 * 
							 * if (tax_perc > 0) { tax_amt = roundNumber(price *
							 * qty tax_perc / 100); } else { tax_perc = 0;
							 * tax_amt = (Double) item .getItemProperty(
							 * TBC_TAX_AMT) .getValue(); } }
							 * 
							 * total = roundNumber(price * qty);
							 * 
							 * cess_amt = 0; if (isCessEnableOnItem((Long) item
							 * .getItemProperty(TBC_ITEM_ID) .getValue())) {
							 * cess_perc = (Double) session
							 * .getAttribute("cess_percentage"); cess_amt =
							 * roundNumber(tax_amt getCessPercentage() / 100); }
							 * 
							 * item.getItemProperty(TBC_TAX_AMT).setValue(
							 * tax_amt); item.getItemProperty(TBC_NET_PRICE)
							 * .setValue(total);
							 * item.getItemProperty(TBC_DISCOUNT)
							 * .setValue(discount);
							 * item.getItemProperty(TBC_CESS_AMT)
							 * .setValue(cess_amt);
							 * item.getItemProperty(TBC_NET_TOTAL) .setValue(
							 * roundNumber(total + tax_amt + cess_amt));
							 * item.getItemProperty(TBC_NET_FINAL) .setValue(
							 * roundNumber(total + tax_amt + cess_amt -
							 * discount));
							 * 
							 * 
							 * item.getItemProperty(TBC_STOCK_ID).setValue(
							 * (Long)stockSelectList.getValue());
							 * item.getItemProperty
							 * (TBC_CONVERTION_QTY).setValue(
							 * toDouble(convertionQtyTextField.getValue()));
							 * 
							 * conv_rat =
							 * toDouble(convertionQtyTextField.getValue());
							 * 
							 * item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
							 * .setValue(conv_rat * qty);
							 * 
							 * 
							 * 
							 * item.getItemProperty(TBC_MANUFACT_DATE)
							 * .setValue( stk.getManufacturing_date());
							 * item.getItemProperty(TBC_EXPIRE_DATE)
							 * .setValue(stk.getExpiry_date());
							 * item.getItemProperty(TBC_STOCK_ID)
							 * .setValue(stk.getId());
							 * 
							 * already_added_item = true;
							 * 
							 * break; } }
							 */

							if (!already_added_item) {

								price = 0;
								qty = 0;
								total = 0;
								double discount_amt = 0;

								price = toDouble(unitPriceTextField.getValue());
								qty = toDouble(quantityTextField.getValue());
								discount_amt = toDouble(discountTextField
										.getValue());

								netPriceTextField
										.setNewValue(asString(roundNumber(price
												* qty)));

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

								// double conv_rat =
								// comDao.getConvertionRate(itm
								// .getId(), objUnit.getId(),
								// toInt(salesTypeSelect.getValue()
								// .toString()));
								String rack = "";
								long rack_id = 0;
								if (settings.isRACK_ENABLED()) {
									rack_id = (Long) rackSelectList.getValue();
									rack = rackSelectList
											.getItemCaption(rackSelectList
													.getValue());
								}

								conv_rat = toDouble(convertionQtyTextField
										.getValue());

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
												total,
												(long) 0,
												(long) 0,
												cess_amt,
												roundNumber(total + tax_amt
														+ cess_amt),
												roundNumber(total + tax_amt
														+ cess_amt
														- discount_amt),
												roundNumber(conv_rat * qty),
												(Long) stockSelectList
														.getValue(),
												conv_rat,
												(Long) gradeComboField
														.getValue(),
												gradeComboField
														.getItemCaption(gradeComboField
																.getValue()),
												rack_id, rack, 0.0 }, id);

								table.setVisibleColumns(requiredHeaders);

								itemSelectCombo.setNewValue(null);
								quantityTextField.setValue("0.0");
								unitPriceTextField.setNewValue("0.0");
								netPriceTextField.setNewValue("0.0");
								discountTextField.setValue("0.0");
								gradeComboField.setValue((long) 0);
							} else {
								itemSelectCombo.setNewValue(null);
								quantityTextField.setValue("0.0");
								unitPriceTextField.setNewValue("0.0");
								netPriceTextField.setNewValue("0.0");
								discountTextField.setValue("0.0");
								gradeComboField.setValue((long) 0);
							}
							calculateTotals();
							calculateForeignCurrencyAmount();

							itemSelectCombo.focus();
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}
					barcodeField.focus();
					barcodeField.setValue("");

				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isUpdatingValid()) {

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

							netPriceTextField
									.setNewValue(asString(roundNumber(price
											* qty - discount_amt)));

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

							item.getItemProperty(TBC_ITEM_ID).setValue(
									itm.getId());
							item.getItemProperty(TBC_ITEM_CODE).setValue(
									itm.getItem_code());
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									itm.getName());
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
							item.getItemProperty(TBC_DISCOUNT).setValue(
									discount_amt);
							item.getItemProperty(TBC_CESS_AMT).setValue(
									cess_amt);
							item.getItemProperty(TBC_NET_TOTAL).setValue(
									roundNumber(total + tax_amt + cess_amt));
							item.getItemProperty(TBC_NET_FINAL).setValue(
									roundNumber(total + tax_amt + cess_amt
											- discount_amt));

							if (stockSelectList.getValue() != null)
								item.getItemProperty(TBC_STOCK_ID).setValue(
										(Long) stockSelectList.getValue());

							item.getItemProperty(TBC_CONVERTION_QTY)
									.setValue(
											toDouble(convertionQtyTextField
													.getValue()));

							item.getItemProperty(TBC_GRADE_ID).setValue(
									(Long) gradeComboField.getValue());
							item.getItemProperty(TBC_GRADE).setValue(
									gradeComboField
											.getItemCaption(gradeComboField
													.getValue()));

							if (settings.isRACK_ENABLED()) {
								item.getItemProperty(TBC_RACK_ID).setValue(
										(Long) rackSelectList.getValue());
								item.getItemProperty(TBC_RACK_NAME).setValue(
										rackSelectList
												.getItemCaption(rackSelectList
														.getValue()));
							} else {
								item.getItemProperty(TBC_RACK_ID).setValue(
										(long) 0);
								item.getItemProperty(TBC_RACK_NAME)
										.setValue("");
							}

							/*
							 * item.getItemProperty(TBC_MANUFACT_DATE).setValue(
							 * stk.getManufacturing_date());
							 * item.getItemProperty(TBC_EXPIRE_DATE).setValue(
							 * stk.getExpiry_date());
							 * item.getItemProperty(TBC_STOCK_ID).setValue(
							 * stk.getId());
							 */

							double conv_rat = toDouble(convertionQtyTextField
									.getValue());

							item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
									.setValue(conv_rat * qty);

							table.setVisibleColumns(requiredHeaders);

							itemSelectCombo.setNewValue(null);
							quantityTextField.setValue("0.0");
							unitPriceTextField.setNewValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");
							gradeComboField.setValue((long) 0);

							visibleAddupdateSalesButton(true, false);

							itemSelectCombo.focus();

							table.setValue(null);

							calculateTotals();
							calculateForeignCurrencyAmount();

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
					barcodeField.focus();
					barcodeField.setValue("");
				}
			});

			itemSelectCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								itemSelectCombo.setComponentError(null);
								stockSelectList.setComponentError(null);
								changeStkButton.setComponentError(null);
								if (itemSelectCombo.getValue() != null) {

									ItemModel itm = daoObj
											.getItem((Long) itemSelectCombo
													.getValue());

									// SCollectionContainer bic =
									// SCollectionContainer
									// .setList(new
									// UnitDao().getAllActiveUnits(getOrganizationID()),
									// "id");
									SCollectionContainer bic = SCollectionContainer
											.setList(comDao
													.getAllItemUnitDetails(itm
															.getId()), "id");

									unitSelect.setContainerDataSource(bic);
									unitSelect
											.setItemCaptionPropertyId("symbol");

									if (taxEnable) {
										taxSelect.setValue(itm.getSalesTax()
												.getId());
									}

									unitSelect.setNewValue(itm.getUnit()
											.getId());

									List lst = comDao.getStocksWithGRV(
											(Long) itemSelectCombo.getValue(),
											settings.isUSE_SALES_RATE_FROM_STOCK());

									SCollectionContainer bic1 = SCollectionContainer
											.setList(lst, "id");
									stockSelectList
											.setContainerDataSource(bic1);
									stockSelectList
											.setItemCaptionPropertyId("stock_details");

									long stk_id = comDao
											.getDefaultStockToSelect((Long) itemSelectCombo
													.getValue());

									if (stk_id != 0)
										stockSelectList.setValue(stk_id);
									else {
										Iterator it = stockSelectList
												.getItemIds().iterator();
										if (it.hasNext())
											stockSelectList.setValue(it.next());
									}

									quantityTextField.focus();
									quantityTextField.selectAll();
									gradeComboField.setValue((long) 0);

									if (settings.isRACK_ENABLED()) {
										if (rackSelectList.getItemIds() == null
												|| rackSelectList.getItemIds()
														.size() == 0) {
											changeStkButton.click();
										} else if (rackSelectList.getItemIds() != null
												&& rackSelectList.getItemIds()
														.size() > 1) {
											changeStkButton.click();
										}
									}

								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

							barcodeField.focus();
							barcodeField.setValue("");
						}
					});

			unitSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (unitSelect.getValue() != null) {
									if (itemSelectCombo.getValue() != null) {

										ItemModel itm = daoObj
												.getItem((Long) itemSelectCombo
														.getValue());

										if (((Long) unitSelect.getValue()) == itm
												.getUnit().getId()) {
											convertionQtyTextField
													.setValue("1");
											convertionQtyTextField
													.setVisible(false);
											convertedQtyTextField
													.setVisible(false);
										} else {
											convertionQtyTextField
													.setVisible(true);
											convertedQtyTextField
													.setVisible(true);

											convertionQtyTextField
													.setCaption("Conv Qty");
											convertedQtyTextField
													.setCaption("Qty - "
															+ itm.getUnit()
																	.getSymbol());

											double cnvr_qty = comDao.getConvertionRate(
													itm.getId(),
													(Long) unitSelect
															.getValue(),
													toInt(salesTypeSelect
															.getValue()
															.toString()));

											convertionQtyTextField
													.setValue(asString(cnvr_qty));

										}

										unitPriceTextField
												.setNewValue(asString(getConvertedAmount(comDao.getItemPrice(
														itm.getId(),
														(Long) unitSelect
																.getValue(),
														toInt(salesTypeSelect
																.getValue()
																.toString())))));

										if (quantityTextField.getValue() != null
												&& !quantityTextField
														.getValue().equals("")) {

											convertedQtyTextField.setNewValue(asString(Double
													.parseDouble(quantityTextField
															.getValue())
													* Double.parseDouble(convertionQtyTextField
															.getValue())));

											netPriceTextField
													.setNewValue(asString(roundNumber(Double
															.parseDouble(unitPriceTextField
																	.getValue())
															* Double.parseDouble(quantityTextField
																	.getValue()))));
										}

										if (stockSelectList.getValue() != null) {
											double rat = new StockRateUpdateDao()
													.getStockSalesRate(
															(Long) stockSelectList
																	.getValue(),
															(Long) unitSelect
																	.getValue());
											if (rat > 0)
												unitPriceTextField
														.setValue(asString(rat));
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

			salesTypeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							Object obj = unitSelect.getValue();
							unitSelect.setNewValue(null);
							unitSelect.setNewValue(obj);

							barcodeField.focus();
							barcodeField.setValue("");
						}
					});

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

			// Added by Anil

//			printButton.addClickListener(new ClickListener() {
//
//				@Override
//				public void buttonClick(ClickEvent event) {
//					HashMap<String, Object> map = new HashMap<String, Object>();
//					List<Object> reportList = new ArrayList<Object>();
//					SalesPrintBean bean = null;
//					NumberToWords numberToWords = new NumberToWords();
//					double total = 0;
//					try {
//
//						SalesModel salObj = daoObj
//								.getSale((Long) salesNumberList.getValue());
//
//						CustomerModel customerModel = custDao
//								.getCustomerFromLedger(salObj.getCustomer()
//										.getId());
//						String address = "";
//						if (customerModel != null) {
//							address = new AddressDao()
//									.getAddressString(customerModel.getLedger()
//											.getAddress().getId());
//						}
//
//						map.put("CUSTOMER_NAME", customerModel.getName());
//						map.put("CUSTOMER_ADDRESS", address);
//						map.put("SALES_BILL_NO", toLong(salesNumberList
//								.getItemCaption(salesNumberList.getValue())));
//						map.put("BILL_DATE", CommonUtil
//								.formatDateToDDMMMYYYY(salObj.getDate()));
//						map.put("SALES_MAN", usrDao
//								.getUserNameFromLoginID(salObj
//										.getSales_person()));
//
//						map.put("CURRENCY", salObj.getOffice().getCurrency()
//								.getCode());
//						map.put("PAID_AMOUNT", salObj.getPayment_amount());
//						map.put("REF_NO", salObj.getVoucher_no());
//						map.put("CUR_DATE", getFormattedTime(new Date()));
//						String adr1 = "", adr2 = "";
//						if (salObj.getOffice().getAddress() != null) {
//
//							adr1 += salObj.getOffice().getAddress()
//									.getCountry().getName();
//
//							if (salObj.getOffice().getAddress().getPhone() != null
//									&& salObj.getOffice().getAddress()
//											.getPhone().length() > 0)
//								adr2 += "Tel : "
//										+ salObj.getOffice().getAddress()
//												.getPhone() + "   ";
//
//						}
//						map.put("ADDRESS1", adr1);
//						map.put("ADDRESS2", adr2);
//						map.put("OFFICE_ADDRESS",
//								new AddressDao().getAddressString(salObj
//										.getOffice().getAddress().getId()));
//
//						String resp = "";
//						if (salObj.getResponsible_person() != 0) {
//							UserModel usrObj = usrDao.getUserFromLogin(salObj
//									.getResponsible_person());
//
//							resp = usrObj.getFirst_name();
//
//							if (usrObj.getAddress() != null) {
//								if (usrObj.getAddress().getMobile() != null
//										&& !usrObj.getAddress().getMobile()
//												.equals(""))
//									resp += " Mob: "
//											+ usrObj.getAddress().getMobile();
//								if (usrObj.getAddress().getPhone() != null
//										&& !usrObj.getAddress().getPhone()
//												.equals(""))
//									resp += " Ph: "
//											+ usrObj.getAddress().getPhone();
//							}
//						}
//						map.put("RESPONSIBLE_PERSON", resp);
//
//						String type = "";
//						if (status == 1) {
//							type = "Cash Sale";
//						} else {
//							type = "Credit Sale";
//						}
//						map.put("SALES_TYPE", type);
//						map.put("TAX_ENABLED", isTaxEnable());
//						map.put("OFFICE_NAME", customerModel.getLedger()
//								.getOffice().getName());
//
//						double netTotal = 0;
//						SalesInventoryDetailsModel invObj;
//						Iterator<SalesInventoryDetailsModel> itr1 = salObj
//								.getInventory_details_list().iterator();
//						while (itr1.hasNext()) {
//							invObj = itr1.next();
//
//							bean = new SalesPrintBean(
//									invObj.getItem().getName(),
//									invObj.getQunatity(),
//									getConvertedAmount(invObj.getUnit_price()),
//									getConvertedAmount((invObj.getQunatity() * invObj
//											.getUnit_price())
//											- invObj.getDiscount_amount()
//											+ invObj.getCess_amount()
//											+ invObj.getTax_amount()), invObj
//											.getUnit().getSymbol(), invObj
//											.getItem().getItem_code(), invObj
//											.getQunatity());
//							bean.setRack(new RackDao().getRackName(invObj
//									.getRack_id()));
//							bean.setDiscount(getConvertedAmount(invObj
//									.getDiscount_amount()));
//
//							total += bean.getTotal();
//
//							netTotal += invObj.getQunatity()
//									* invObj.getUnit_price();
//							reportList.add(bean);
//						}
//
//						map.put("GRAND_TOTAL",
//								total
//										+ getConvertedAmount(salObj
//												.getShipping_charge())
//										- getConvertedAmount(salObj
//												.getDiscount_amount()));
//						map.put("OTHER CHARGES",
//								getConvertedAmount(salObj.getShipping_charge()));
//						map.put("DISCOUNT",
//								getConvertedAmount(salObj.getDiscount_amount()));
//						map.put("NET_TOTAL", getConvertedAmount(netTotal));
//
//						map.put("IMAGE_PATH", VaadinServlet.getCurrent()
//								.getServletContext().getRealPath("/")
//								.toString());
//
//						int pages = 1;
//						if (salObj.getInventory_details_list().size() % 20 == 0)
//							pages = (int) (salObj.getInventory_details_list()
//									.size() / 20);
//						else
//							pages = (int) (salObj.getInventory_details_list()
//									.size() / 20) + 1;
//
//						map.put("TOTAL_PAGE_NOS", pages);
//
//						CurrencyModel cm = rateDao.getCurrencyModel(salObj
//								.getCurrencyId());
//						if (cm != null) {
//							map.put("AMOUNT_IN_WORDS",
//									numberToWords.convertNumber(
//											roundNumber(total
//													+ getConvertedAmount(salObj
//															.getShipping_charge())
//													- getConvertedAmount(salObj
//															.getDiscount_amount()))
//													+ "", cm.getInteger_part(),
//											cm.getFractional_part()));
//						} else {
//							S_OfficeModel officeModel = new OfficeDao()
//									.getOffice(getOfficeID());
//							map.put("AMOUNT_IN_WORDS",
//									numberToWords.convertNumber(
//											roundNumber(total
//													+ getConvertedAmount(salObj
//															.getShipping_charge())
//													- getConvertedAmount(salObj
//															.getDiscount_amount()))
//													+ "", officeModel
//													.getCurrency()
//													.getInteger_part(),
//											officeModel.getCurrency()
//													.getFractional_part()));
//						}
//
//						Report report = new Report(getLoginID());
//						report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
//						report.setReportFileName("SalesPrint");
//						report.setIncludeHeader(true);
//						report.setReportType(Report.PDF);
//						report.createReport(reportList, map);
//
//						report.print();
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});

			if (!isFinYearBackEntry()) {
				saveSalesButton.setVisible(false);
				updateSalesButton.setVisible(false);
				deleteSalesButton.setVisible(false);
				cancelSalesButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			unitPriceTextField.setImmediate(true);

			unitPriceTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateNetPrice();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			discountMainField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateTotals();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			shippingChargeTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateTotals();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			quantityTextField.setImmediate(true);

			quantityTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateNetPrice();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			grandTotalAmtTextField.setImmediate(true);
			grandTotalAmtTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateForeignCurrencyAmount();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			convertionQtyTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								try {
									if (convertionQtyTextField.getValue()
											.equals("")
											|| toDouble(convertionQtyTextField
													.getValue()) <= 0) {
										convertionQtyTextField.setValue("1");
									}
								} catch (Exception e) {
									convertionQtyTextField.setValue("1");
									// TODO: handle exception
								}

								calculateNetPrice();

							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			stockSelectList.setImmediate(true);
			stockSelectList.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (stockSelectList.getValue() != null) {
							ItemStockModel stk = daoObj
									.getItemStocks((Long) stockSelectList
											.getValue());
							gradeComboField.setValue(stk.getGradeId());

							if (unitSelect.getValue() != null) {
								if (settings.isUSE_SALES_RATE_FROM_STOCK()) {
									double rat = new StockRateUpdateDao()
											.getStockSalesRate(
													(Long) stockSelectList
															.getValue(),
													(Long) unitSelect
															.getValue());
									if (rat > 0) {
										unitPriceTextField
												.setValue(asString(rat));
									} else {
										unitPriceTextField.setNewValue("0");
										unitPriceTextField.setNewValue(asString(comDao
												.getItemPrice(
														(Long) itemSelectCombo
																.getValue(),
														(Long) unitSelect
																.getValue(),
														toInt(salesTypeSelect
																.getValue()
																.toString()))));
									}
								}
							}

						}
						reloadRack();

						Iterator it = rackSelectList.getItemIds().iterator();
						if (it.hasNext())
							rackSelectList.setValue(it.next());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			stockSelectList.setImmediate(true);

			if (settings.isRACK_ENABLED()) {
				rackSelectList
						.addValueChangeListener(new ValueChangeListener() {
							@Override
							public void valueChange(ValueChangeEvent event) {
								try {
									if (rackSelectList.getValue() != null)
										rackBalance
												.setValue(asString(new StockRackMappingDao()
														.getRacksBalance(
																(Long) rackSelectList
																		.getValue(),
																(Long) stockSelectList
																		.getValue())));
									else
										rackBalance.setValue("0");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
			}

			if (isSaleEditable()) {
				updateSalesButton.setEnabled(true);
			} else
				updateSalesButton.setEnabled(false);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		barcodeField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					String barcode = barcodeField.getValue();

					if (barcode.trim().length() > 0) {
						itemSelectCombo.setComponentError(null);
						stockSelectList.setComponentError(null);
						ItemStockModel mdl = daoObj.getItemFromBarcode(barcode);
						if (mdl != null) {
							itemSelectCombo.setNewValue(mdl.getItem().getId());
							gradeComboField.setValue(mdl.getGradeId());
							quantityTextField.focus();

						} else {
							itemSelectCombo.setNewValue(null);
							stockSelectList.setValue(null);
							gradeComboField.setValue((long) 0);
							barcodeField.focus();
						}

					} else {
						barcodeField.focus();
						if (itemSelectCombo.getValue() != null)
							quantityTextField.focus();
					}

				} catch (Exception e) {
					barcodeField.focus();
				}
			}
		});

		currencyNativeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				multiplyWithOld(conv_val);

				double convRate;
				try {
					convRate = rateDao.getConversionRate(getCurrencyID(),
							toLong(currencyNativeSelect.getValue().toString()));

					conv_val = convRate;

					discountMainField
							.setNewValue(asString(getConvertedAmount(toDouble(discountMainField
									.getValue().toString()))));
					grandTotalAmtTextField
							.setNewValue(asString(getConvertedAmount(toDouble(grandTotalAmtTextField
									.getValue().toString()))));
					shippingChargeTextField
							.setValue(asString(getConvertedAmount(toDouble(shippingChargeTextField
									.getValue().toString()))));
					exciseDutyTextField
							.setValue(asString(getConvertedAmount(toDouble(exciseDutyTextField
									.getValue().toString()))));
					payingAmountTextField
							.setValue(asString(getConvertedAmount(toDouble(payingAmountTextField
									.getValue().toString()))));

					// foreignCurrField.setNewValue(asString(getConvertedAmount(toDouble(foreignCurrField.getValue()
					// .toString()))));

					Iterator iter = table.getItemIds().iterator();
					Item item = null;
					while (iter.hasNext()) {
						item = table.getItem(iter.next());
						item.getItemProperty(TBC_CESS_AMT).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_CESS_AMT)
										.getValue().toString())));
						item.getItemProperty(TBC_DISCOUNT).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_DISCOUNT)
										.getValue().toString())));
						item.getItemProperty(TBC_NET_FINAL).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_NET_FINAL)
										.getValue().toString())));
						item.getItemProperty(TBC_NET_PRICE).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_NET_PRICE)
										.getValue().toString())));
						item.getItemProperty(TBC_NET_TOTAL).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_NET_TOTAL)
										.getValue().toString())));
						item.getItemProperty(TBC_TAX_AMT).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_TAX_AMT)
										.getValue().toString())));
						item.getItemProperty(TBC_UNIT_PRICE).setValue(
								getConvertedAmount(toDouble(item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue().toString())));
					}

					calculateTotals();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return pannel;
	}

	protected void multiplyWithOld(double conv_val2) {
		discountMainField
				.setNewValue(asString(roundNumber(toDouble(discountMainField
						.getValue().toString())) / conv_val2));
		grandTotalAmtTextField
				.setNewValue(asString(roundNumber(toDouble(grandTotalAmtTextField
						.getValue().toString())) / conv_val2));
		shippingChargeTextField
				.setValue(asString(roundNumber(toDouble(shippingChargeTextField
						.getValue().toString())) / conv_val2));
		exciseDutyTextField
				.setValue(asString(roundNumber(toDouble(exciseDutyTextField
						.getValue().toString())) / conv_val2));
		payingAmountTextField
				.setValue(asString(roundNumber(toDouble(payingAmountTextField
						.getValue().toString())) / conv_val2));

		// foreignCurrField.setNewValue(asString(roundNumber(toDouble(foreignCurrField.getValue()
		// .toString()))/conv_val2));

		Iterator iter = table.getItemIds().iterator();
		Item item = null;
		while (iter.hasNext()) {
			item = table.getItem(iter.next());
			item.getItemProperty(TBC_CESS_AMT).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_CESS_AMT)
							.getValue().toString())
							/ conv_val2));
			item.getItemProperty(TBC_DISCOUNT).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_DISCOUNT)
							.getValue().toString())
							/ conv_val2));
			item.getItemProperty(TBC_NET_FINAL).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_NET_FINAL)
							.getValue().toString())
							/ conv_val2));
			item.getItemProperty(TBC_NET_PRICE).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_NET_PRICE)
							.getValue().toString())
							/ conv_val2));
			item.getItemProperty(TBC_NET_TOTAL).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_NET_TOTAL)
							.getValue().toString())
							/ conv_val2));
			item.getItemProperty(TBC_TAX_AMT).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_TAX_AMT)
							.getValue().toString())
							/ conv_val2));
			item.getItemProperty(TBC_UNIT_PRICE).setValue(
					roundNumber(toDouble(item.getItemProperty(TBC_UNIT_PRICE)
							.getValue().toString())
							/ conv_val2));
		}

	}

	protected double getConvertedAmount(double price) {
		double amount = 0;
		try {
			double convRate = rateDao.getConversionRate(getCurrencyID(),
					toLong(currencyNativeSelect.getValue().toString()));
			amount = price * convRate;
		} catch (Exception e) {
			amount = price;
		}
		return roundNumber(amount);
	}

	protected void calculateForeignCurrencyAmount() {
		try {
			foreignCurrField
					.setNewValue(asString(getReverseConvertedAmount(toDouble(grandTotalAmtTextField
							.getValue()))));
		} catch (Exception e) {
			foreignCurrField.setNewValue(grandTotalAmtTextField.getValue());
		}
	}

	private double getReverseConvertedAmount(double price) {
		double amount = 0;
		try {
			double convRate = rateDao.getConversionRate(getCurrencyID(),
					toLong(currencyNativeSelect.getValue().toString()));
			amount = price / convRate;
		} catch (Exception e) {
			amount = price;
		}
		return roundNumber(amount);
	}

	protected void reloadItemStocks() {
		try {
			List list = daoObj.getAllItemsWithAllStocks(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			itemSelectCombo.setContainerDataSource(bic);
			itemSelectCombo.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				itemSelectCombo.setNewValue((Long) getHttpSession()
						.getAttribute("saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void reloadRack() {
		try {
			if (settings.isRACK_ENABLED()) {
				if (stockSelectList.getValue() != null) {

					List list = new StockRackMappingDao()
							.getRacksArranged((Long) stockSelectList.getValue());
					CollectionContainer bic = CollectionContainer.fromBeans(
							list, "id");
					rackSelectList.setContainerDataSource(bic);
					rackSelectList.setItemCaptionPropertyId("rack_number");

				} else {
					rackSelectList.setContainerDataSource(null);
					rackSelectList.setItemCaptionPropertyId("rack_number");
				}
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
				// lst.addAll(new
				// UnitDao().getAllActiveUnits(getOrganizationID()));
				lst.addAll(comDao.getAllItemUnitDetails(itm.getId()));
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				boolean readOnly = unitSelect.isReadOnly();
				unitSelect.setReadOnly(false);
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

				unitSelect.setNewValue(null);
				unitSelect.setNewValue(temp);
				unitSelect.setReadOnly(readOnly);

			} else {

				List lst = new ArrayList();
				// lst.addAll(new
				// UnitDao().getAllActiveUnits(getOrganizationID()));
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc = 0;

		try {
			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
			disc = Double.parseDouble(discountTextField.getValue());

			convertedQtyTextField.setNewValue(asString(qty
					* Double.parseDouble(convertionQtyTextField.getValue())));

		} catch (Exception e) {
			// TODO: handle exception
		}
		netPriceTextField
				.setNewValue(roundNumber(toDouble(asString(new BigDecimal(
						(unitPrc * qty) - disc)))) + "");
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

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0, disc_ttl = 0, ttl_bfr_tax = 0, ttl_bfr_disc = 0, cess_ttl = 0;

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

				net_ttl += (Double) item.getItemProperty(TBC_NET_FINAL)
						.getValue();
				disc_ttl += (Double) item.getItemProperty(TBC_DISCOUNT)
						.getValue();

				ttl_bfr_tax += (Double) item.getItemProperty(TBC_NET_TOTAL)
						.getValue();
				ttl_bfr_disc += (Double) item.getItemProperty(TBC_NET_FINAL)
						.getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_TAX_AMT, asString(roundNumber(tax_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));
			table.setColumnFooter(TBC_DISCOUNT, asString(roundNumber(disc_ttl)));
			table.setColumnFooter(TBC_CESS_AMT, asString(roundNumber(cess_ttl)));
			table.setColumnFooter(TBC_NET_TOTAL,
					asString(roundNumber(ttl_bfr_tax)));
			table.setColumnFooter(TBC_NET_FINAL,
					asString(roundNumber(ttl_bfr_disc)));

			double ship_charg = 0, excise_duty = 0, discount = 0;
			try {
				ship_charg = toDouble(shippingChargeTextField.getValue());
				excise_duty = toDouble(exciseDutyTextField.getValue());
				discount = toDouble(discountMainField.getValue());
			} catch (Exception e) {
			}

			grandTotalAmtTextField.setNewValue(asString(roundNumber(net_ttl
					+ ship_charg + excise_duty - discount)));

		} catch (Exception e) {
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (discountTextField.getValue() == null
					|| discountTextField.getValue().equals("")) {
				// setRequiredError(discountTextField, "Enter discount", true);
				discountTextField.setValue("0");
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
				unitPriceTextField.selectAll();
				ret = false;
			} else {
				try {
					if (toDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,
								getPropertyName("invalid_data"), true);
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

			if (convertionQtyTextField.getValue() == null
					|| convertionQtyTextField.getValue().equals("")) {
				setRequiredError(convertionQtyTextField,
						getPropertyName("invalid_data"), true);
				convertionQtyTextField.focus();
				convertionQtyTextField.selectAll();
				ret = false;
			} else {
				try {
					if (toDouble(convertionQtyTextField.getValue()) <= 0) {
						setRequiredError(convertionQtyTextField,
								getPropertyName("invalid_data"), true);
						convertionQtyTextField.focus();
						convertionQtyTextField.selectAll();
						ret = false;
					} else
						setRequiredError(convertionQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(convertionQtyTextField,
							getPropertyName("invalid_data"), true);
					convertionQtyTextField.focus();
					convertionQtyTextField.selectAll();
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

			if (settings.isRACK_ENABLED()) {
				if (rackSelectList.getValue() == null
						|| rackSelectList.getValue().equals("")) {
					setRequiredError(rackSelectList,
							getPropertyName("invalid_selection"), true);
					setRequiredError(changeStkButton,
							getPropertyName("invalid_selection"), true);
					rackSelectList.focus();
					ret = false;
				} else {
					setRequiredError(rackSelectList, null, false);
					setRequiredError(changeStkButton, null, false);
				}
			}
			
			if(ret) {
				Item itm;
				Iterator it=table.getItemIds().iterator();
				while (it.hasNext()) {
					itm=table.getItem(it.next());
					if(itm.getItemProperty(TBC_ITEM_ID).getValue().toString().equals(itemSelectCombo.getValue().toString())) {
						setRequiredError(itemSelectCombo,"Item already exist.!", true);
						itemSelectCombo.focus();
						ret = false;
						break;
					}
				}
			}

			if(ret) {
				isItemBalanceAvailForAdd();
			}
			
			
		} catch (Exception e) {
			ret = false;
		}

		return ret;

	}

	public boolean isQtyAvailOnRack(long rack_id) {
		boolean ret = true;
		try {

			double qty = toDouble(convertionQtyTextField.getValue())
					* toDouble(quantityTextField.getValue()), ttl = 0;

			Item itm;
			Iterator itr = table.getItemIds().iterator();
			while (itr.hasNext()) {
				itm = table.getItem(itr.next());
				if (itm.getItemProperty(TBC_STOCK_ID).getValue().toString()
						.equals(stockSelectList.getValue().toString())
						&& itm.getItemProperty(TBC_RACK_ID).getValue()
								.toString()
								.equals(rackSelectList.getValue().toString()))
					ttl += (Double) itm.getItemProperty(TBC_QTY_IN_BASIC_UNI)
							.getValue();
			}
			ttl += qty;

			double avail = new StockRackMappingDao().getRacksBalance(
					(Long) rackSelectList.getValue(),
					(Long) stockSelectList.getValue());

			if (ttl > avail) {
				setRequiredError(rackSelectList,
						getPropertyName("invalid_selection") + avail, true);
				changeStkButton.click();
				ret = false;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;
	}

	public boolean isUpdateQtyAvailOnRack(long rack_id) {
		boolean ret = true;
		try {

			double qty = toDouble(convertionQtyTextField.getValue())
					* toDouble(quantityTextField.getValue()), ttl = 0;

			Item itm;
			Iterator itr = table.getItemIds().iterator();
			while (itr.hasNext()) {
				itm = table.getItem(itr.next());
				if (itm.getItemProperty(TBC_STOCK_ID).getValue().toString()
						.equals(stockSelectList.getValue().toString())
						&& itm.getItemProperty(TBC_RACK_ID).getValue()
								.toString()
								.equals(rackSelectList.getValue().toString()))
					ttl += (Double) itm.getItemProperty(TBC_QTY_IN_BASIC_UNI)
							.getValue();
			}
			ttl += qty;

			itm = table.getItem(((Set) table.getValue()).iterator().next());

			double old = 0;

			if (itm.getItemProperty(TBC_STOCK_ID).getValue().toString()
					.equals(stockSelectList.getValue().toString())
					&& itm.getItemProperty(TBC_RACK_ID).getValue().toString()
							.equals(rackSelectList.getValue().toString()))
				old = (Double) itm.getItemProperty(TBC_OLD_QTY).getValue();

			ttl -= old;
			double avail = new StockRackMappingDao().getRacksBalance(
					(Long) rackSelectList.getValue(),
					(Long) stockSelectList.getValue());

			if (ttl > (avail + old)) {
				setRequiredError(rackSelectList,
						getPropertyName("invalid_selection") + (avail + old),
						true);
				changeStkButton.click();
				ret = false;
			}

		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}

		return ret;
	}

	public boolean isUpdatingValid() {
		boolean ret = true;
		try {

			if (discountTextField.getValue() == null
					|| discountTextField.getValue().equals("")) {
				// setRequiredError(discountTextField, "Enter discount", true);
				discountTextField.setValue("0");
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
								getPropertyName("invalid_data"), true);
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

			if (convertionQtyTextField.getValue() == null
					|| convertionQtyTextField.getValue().equals("")) {
				setRequiredError(convertionQtyTextField,
						getPropertyName("invalid_data"), true);
				convertionQtyTextField.focus();
				convertionQtyTextField.selectAll();
				ret = false;
			} else {
				try {
					if (toDouble(convertionQtyTextField.getValue()) <= 0) {
						setRequiredError(convertionQtyTextField,
								getPropertyName("invalid_data"), true);
						convertionQtyTextField.focus();
						convertionQtyTextField.selectAll();
						ret = false;
					} else
						setRequiredError(convertionQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(convertionQtyTextField,
							getPropertyName("invalid_data"), true);
					convertionQtyTextField.focus();
					convertionQtyTextField.selectAll();
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

			if (itemSelectCombo.getValue() != null) {

				Collection selectedItems = (Collection) table.getValue();
				Item item = table.getItem(selectedItems.iterator().next());

				if (!itemSelectCombo
						.getValue()
						.toString()
						.equals(item.getItemProperty(TBC_ITEM_ID).getValue()
								.toString())) {
					if (stockSelectList.getValue() == null
							|| stockSelectList.getValue().equals("")) {
						setRequiredError(stockSelectList,
								getPropertyName("invalid_selection"), true);
						setRequiredError(changeStkButton,
								getPropertyName("invalid_selection"), true);
						changeStkButton.click();
						stockSelectList.focus();
						ret = false;
					} else {
						setRequiredError(stockSelectList, null, false);
						setRequiredError(changeStkButton, null, false);
					}
				}
			}

			// if (stockSelectList.getValue() == null ||
			// stockSelectList.getValue().equals("")) {
			// setRequiredError(stockSelectList, "Select a Stock", true);
			// setRequiredError(changeStkButton, "Select a Stock", true);
			// stockSelectList.focus();
			// ret = false;
			// } else {
			// setRequiredError(stockSelectList, null, false);
			// setRequiredError(changeStkButton, null, false);
			// }

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

			if (settings.isRACK_ENABLED()) {

				if (rackSelectList.getValue() == null
						|| rackSelectList.getValue().equals("")) {
					setRequiredError(rackSelectList,
							getPropertyName("invalid_selection"), true);
					setRequiredError(changeStkButton,
							getPropertyName("invalid_selection"), true);
					rackSelectList.focus();
					ret = false;
				} else {
					setRequiredError(rackSelectList, null, false);
					setRequiredError(changeStkButton, null, false);
				}
			}
			
			if(ret) {
				Object obj;
				Item itm;
				Iterator it=table.getItemIds().iterator();
				while (it.hasNext()) {
					obj=it.next();
					itm=table.getItem(obj);
					if(itm.getItemProperty(TBC_ITEM_ID).getValue().toString().equals(itemSelectCombo.getValue().toString())) {
						if(((Set)table.getValue()).iterator().next()!=obj) {
							setRequiredError(itemSelectCombo,"Item already exist.!", true);
							itemSelectCombo.focus();
							ret = false;
							break;
						}
					}
				}
			}
			
			if(ret) {
				isItemBalanceAvailForUpdate();
			}

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
				calculateForeignCurrencyAmount();
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
			list.add(new SalesModel(0, "----Create New-----"));
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
	
	public void isItemBalanceAvailForUpdate() {
		Item itm=table.getItem(table.getValue());
		
		try {
		
			double newQty=toDouble(convertedQtyTextField.getValue());
			double curBal=itmDao.getNameAndCurrentBal((Long) itemSelectCombo.getValue()).
					getCurrent_balalnce();
			if(itm.getItemProperty(TBC_ITEM_ID).getValue().toString().
					equals(itemSelectCombo.getValue().toString())) {
				double oldQty=(Double) itm.getItemProperty(TBC_OLD_QTY).getValue();
				
				if((newQty-oldQty)> curBal) {
					Notification.show("There is balance shortage on this item. Bal: "+(oldQty+curBal), Type.WARNING_MESSAGE);
				}
			}
			else {
				if((newQty)> curBal) {
					Notification.show("There is balance shortage on this item. Bal: "+curBal, Type.WARNING_MESSAGE);
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void isItemBalanceAvailForAdd() {
		
		try {
		
			double newQty=toDouble(convertedQtyTextField.getValue());
			double curBal=itmDao.getNameAndCurrentBal((Long) itemSelectCombo.getValue()).
					getCurrent_balalnce();
			if(newQty> curBal) {
				Notification.show("There is balance shortage on this item. Bal: "+curBal, Type.WARNING_MESSAGE);
			}
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public Boolean isValid() {

		boolean ret = true;

		/*
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
		 * if ((Long) cashOrCreditRadio.getValue() == 1) {
		 * 
		 * if (toDouble(payingAmountTextField.getValue()) !=
		 * toDouble(grandTotalAmtTextField .getValue())) { setRequiredError(
		 * payingAmountTextField,
		 * "Enter a valid amount. Need to set Paying amount to Total amount. Else select Credit sale."
		 * , true); payingAmountTextField.focus(); ret = false; }
		 * 
		 * } else if (toDouble(payingAmountTextField.getValue()) >=
		 * toDouble(grandTotalAmtTextField .getValue()) &&
		 * toDouble(grandTotalAmtTextField.getValue())!=0) {
		 * setRequiredError(payingAmountTextField,
		 * "Enter a valid amount. Must be less than total. Else select Cash Sale."
		 * , true); payingAmountTextField.focus(); ret = false; }
		 */

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
		if (discountMainField.getValue() == null
				|| discountMainField.getValue().equals("")) {
			setRequiredError(discountMainField,
					getPropertyName("invalid_data"), true);
			discountMainField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(discountMainField.getValue()) < 0) {
					setRequiredError(discountMainField,
							getPropertyName("invalid_data"), true);
					discountMainField.focus();
					ret = false;
				} else
					setRequiredError(discountMainField, null, false);
			} catch (Exception e) {
				setRequiredError(discountMainField,
						getPropertyName("invalid_data"), true);
				discountMainField.focus();
				ret = false;
			}
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
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

		if (responsibleEmployeeCombo.getValue() == null
				|| responsibleEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsibleEmployeeCombo,
					getPropertyName("invalid_selection"), true);
			responsibleEmployeeCombo.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployeeCombo, null, false);

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
			refNoField.focus();
			ret = false;
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

		if (responsibleEmployeeCombo.getComponentError() != null)
			setRequiredError(responsibleEmployeeCombo, null, false);
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SComboField getSalesNumberList() {
		return salesNumberList;
	}
	
	public void setSalesNumberList(SComboField salesNumberList) {
		this.salesNumberList = salesNumberList;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return salesNumberList;
	}
}
