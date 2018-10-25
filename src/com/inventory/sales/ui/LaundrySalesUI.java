package com.inventory.sales.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.SetUnitManagementUI;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.bean.SalesInventoryDetailsPojo;
import com.inventory.sales.dao.LaundrySalesDao;
import com.inventory.sales.model.LaundrySalesDetailsModel;
import com.inventory.sales.model.LaundrySalesModel;
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
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SDateTimeField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
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

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */
public class LaundrySalesUI extends SparkLogic {

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

	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";

	LaundrySalesDao daoObj;

	CommonMethodsDao comDao;

	SComboField salesNumberList;

	SPanel pannel;
	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SDateTimeField expectedDeliveryDate, actualDeliveryDate;

	SComboField responsibleEmployeeCombo;

	SCheckBox deliveredCheck;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField netPriceTextField;

	STextField payingAmountTextField, advanceAmountTextField;
	STextField roomNoTextField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSalesButton;
	SButton updateSalesButton;
	SButton deleteSalesButton;
	SButton cancelSalesButton;

	ItemDao itemDao;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SComboField customerSelect;
	SDateField date;
	SDateField expiry_date;
	SDateField manufaturing_date;
	SComboField itemSelectCombo;

	SComboField employSelect;

	STextField grandTotalAmtTextField, balanceAmountTxtFld;
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

	// SNativeSelect salesTypeSelect;

	SWindow popupWindow;

	private SButton loadAllSuppliersButton;

	SHorizontalLayout popupHor;
	SButton priceListButton;

	UserManagementDao usrDao;

	CustomerDao custDao;
	TaxDao taxDao;

	private STextField refNoField;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		comDao = new CommonMethodsDao();
		custDao = new CustomerDao();
		taxDao = new TaxDao();
		itemDao = new ItemDao();
		usrDao = new UserManagementDao();

		taxEnable = isTaxEnable();

		popupWindow = new SWindow();

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_PO_ID, TBC_INV_ID, TBC_CESS_AMT,
				TBC_NET_TOTAL, TBC_NET_FINAL, TBC_QTY_IN_BASIC_UNI };

		if (taxEnable) {
			if (isCessEnable()) {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT, TBC_CESS_AMT,
						TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL };
			} else {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT,
						TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL };
			}
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
					TBC_NET_PRICE, TBC_DISCOUNT, TBC_NET_FINAL };
		}

		expectedDeliveryDate = new SDateTimeField(null, 120, getWorkingDate());
		actualDeliveryDate = new SDateTimeField(null, 120, getWorkingDate());

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		/*
		 * if (!isManufDateEnable()) { templist.remove(TBC_MANUFACT_DATE);
		 * templist.remove(TBC_EXPIRE_DATE); }
		 */
		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}

		
		balanceAmountTxtFld=new STextField(null, 140);
		balanceAmountTxtFld.setValue("0");
		balanceAmountTxtFld.setReadOnly(true);
		balanceAmountTxtFld.setStyleName("textfield_align_right");
		
		requiredHeaders = templist.toArray(new String[templist.size()]);

		setSize(1260, 605);

		session = getHttpSession();

		daoObj = new LaundrySalesDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		// isPaymentNow=new SCheckBox("Receiving Cash", true);

		payingAmountTextField = new STextField(null, 100);
		payingAmountTextField.setValue("0.00");
		payingAmountTextField.setStyleName("textfield_align_right");

		advanceAmountTextField = new STextField(null, 100);
		advanceAmountTextField.setValue("0");
		advanceAmountTextField.setStyleName("textfield_align_right");

		roomNoTextField = new STextField(null, 100);

		pannel = new SPanel();
		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(9);
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

		try {

			deliveredCheck = new SCheckBox(null, false);

			responsibleEmployeeCombo = new SComboField(null, 125,
					new SalesManMapDao().getUsers(getOfficeID(),
							SConstants.SALES_MAN), "id", "first_name");

			newSaleButton = new SButton();
			newSaleButton.setStyleName("createNewBtnStyle");
			newSaleButton.setDescription("Add new Sale");

			priceListButton = new SButton();

			employSelect = new SComboField(
					null,
					125,
					usrDao.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
							getOfficeID(), getOrganizationID()), "id",
					"first_name");

			employSelect.setValue(getLoginID());

			if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin())
				employSelect.setReadOnly(true);

			List list = new ArrayList();
			list.add(new LaundrySalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));
			salesNumberList = new SComboField(null, 125, list, "id",
					"comments", false, getPropertyName("create_new"));

			date = new SDateField(null, 120, getDateFormat(), getWorkingDate());

			customerSelect = new SComboField(
					null,
					220,
					custDao.getAllActiveCustomerNamesWithLedgerID(getOfficeID()),
					"id", "name", true, getPropertyName("select"));

			cashOrCreditRadio = new SRadioButton(null, 300,
					SConstants.paymentModeList, "key", "value");

			refNoField = new STextField(null, 120);
			refNoField.setValue("0");

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesNumberList);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("order_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);

//			masterDetailsGrid.addComponent(cashOrCreditRadio, 3, 0);

			cashOrCreditRadio.setStyleName("radio_horizontal");

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

			SHorizontalLayout horr = new SHorizontalLayout();
			horr.addComponent(new SLabel(getPropertyName("delivery_date")));
			horr.addComponent(expectedDeliveryDate);
			horr.setSpacing(true);
			masterDetailsGrid.addComponent(horr, 3, 1);

//			SHorizontalLayout resp = new SHorizontalLayout();
//			resp.addComponent(new SLabel(
//					getPropertyName("responsible_employee")));
//			resp.addComponent(responsibleEmployeeCombo);
//			resp.setSpacing(true);
//			masterDetailsGrid.addComponent(resp, 3, 2);

			/*
			 * masterDetailsGrid.addComponent(new SLabel("Employ :"), 4, 1);
			 * masterDetailsGrid.addComponent(employSelect, 5, 1);
			 */

			// newCustomerButton.setStyleName("v-button-link");
			// masterDetailsGrid.addComponent(newCustomerButton, 3, 1);

//			masterDetailsGrid.addComponent(new SLabel(
//					getPropertyName("actual_delivery_date")), 6, 1);
//			masterDetailsGrid.addComponent(actualDeliveryDate, 8, 1);

			SHorizontalLayout horr1 = new SHorizontalLayout();
			horr1.addComponent(new SLabel(getPropertyName("room_no"), 30));
			horr1.addComponent(roomNoTextField);
			horr1.setSpacing(true);
			masterDetailsGrid.addComponent(horr1, 3, 0);


			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 6, 1);
			masterDetailsGrid.addComponent(refNoField, 8, 1);

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

			newItemButton.setDescription("Add new Item");

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

			addingGrid.setWidth("1200");

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

			table.setWidth("1200");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			grandTotalAmtTextField = new STextField(null, 200, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

			comment = new STextArea(null, 400, 30);

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
						getPropertyName("shipping_charge")), 4, 1);
				bottomGrid.addComponent(shippingChargeTextField, 5, 1);
				bottomGrid.setComponentAlignment(shippingChargeTextField,
						Alignment.TOP_RIGHT);

			}

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.addComponent(new SLabel(
					getPropertyName("advance_amount"), 40), 2, 1);
			bottomGrid.addComponent(advanceAmountTextField, 3, 1);

			bottomGrid.addComponent(
					new SLabel(getPropertyName("paying_amount")), 2, 3);
			bottomGrid.addComponent(payingAmountTextField, 3, 3);

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),
					4, 1);
			bottomGrid.addComponent(grandTotalAmtTextField, 5, 1);
			
			bottomGrid.addComponent(new SLabel("Balance Amt"),
					4, 3);
			bottomGrid.addComponent(balanceAmountTxtFld, 5, 3);

			bottomGrid.addComponent(new SLabel(getPropertyName("delivered")),
					0, 3);
			bottomGrid.addComponent(deliveredCheck, 1, 3);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);
			bottomGrid.setComponentAlignment(balanceAmountTxtFld,
					Alignment.TOP_RIGHT);

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

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			customerSelect.focus();

			pannel.setContent(hLayout);

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

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesNumberList.setValue((long) 0);
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

					popupWindow.setContent(new SetUnitManagementUI(itemId, 0,
							true));

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

			customerSelect.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (customerSelect.getValue() != null) {
							CustomerModel cust = custDao
									.getCustomerFromLedger((Long) customerSelect
											.getValue());
							if (cust != null) {
								responsibleEmployeeCombo.setValue(cust
										.getResponsible_person());
//								roomNoTextField.setValue(asString(cust
//										.getMax_credit_period()));
								customerSelect
										.setDescription("<h1><i>Current Balance</i> : "
												+ cust.getLedger()
														.getCurrent_balance()
												+ "</h1>");
							}
						} else
							customerSelect.setDescription(null);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}

			});

			saveSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							long customer_id = (Long) customerSelect.getValue();

							double costof_inv_amt = 0;

							LaundrySalesModel salObj = new LaundrySalesModel();

							List<LaundrySalesDetailsModel> itemsList = new ArrayList<LaundrySalesDetailsModel>();

							LaundrySalesDetailsModel invObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new LaundrySalesDetailsModel();
								item = table.getItem(it.next());

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQuantity((Double) item
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

								invObj.setCess_amount((Double) item
										.getItemProperty(TBC_CESS_AMT)
										.getValue());

								invObj.setId((Long) item.getItemProperty(
										TBC_INV_ID).getValue());

								itemsList.add(invObj);

							}

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

							salObj.setExpected_delivery_time(CommonUtil
									.getTimestampFromUtilDate(expectedDeliveryDate
											.getValue()));
							salObj.setActual_delivery_time(CommonUtil
									.getTimestampFromUtilDate(actualDeliveryDate
											.getValue()));

							salObj.setRoom_no(roomNoTextField
									.getValue());
							salObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue())
									+ toDouble(advanceAmountTextField
											.getValue()));
							salObj.setAdvance_amount(toDouble(advanceAmountTextField
									.getValue()));

							salObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));

							salObj.setResponsible_person(getLoginID());
							salObj.setDelivared(deliveredCheck.getValue());
							salObj.setComments(comment.getValue());
							salObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							salObj.setLogin(new S_LoginModel(getLoginID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							salObj.setStatus(1);
							salObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							salObj.setDetails_list(itemsList);

							salObj.setSales_number(getNextSequence(
									"Sales Number", getLoginID()));

							salObj.setSales_person(getLoginID());
							salObj.setVoucher_no(toLong(refNoField.getValue()));

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField
									.getValue())
									+ toDouble(advanceAmountTextField
											.getValue());

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								salObj.setPayment_done('Y');
								salObj.setStatus(1);
								status = 1;
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								salObj.setStatus(2);
								salObj.setPayment_done('N');
								status = 2;
							} else {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								status = 3;
								salObj.setPayment_done('Y');
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

			salesNumberList.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {

						removeAllErrors();

						responsibleEmployeeCombo.setValue(null);

						updateSalesButton.setVisible(true);
						deleteSalesButton.setVisible(true);
						cancelSalesButton.setVisible(true);
						printButton.setVisible(true);
						saveSalesButton.setVisible(false);
						if (salesNumberList.getValue() != null
								&& !salesNumberList.getValue().toString()
										.equals("0")) {

							LaundrySalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());

							table.setVisibleColumns(allHeaders);

							table.removeAllItems();

							LaundrySalesDetailsModel invObj;
							double netTotal;
							Iterator it = salObj.getDetails_list().iterator();
							while (it.hasNext()) {
								invObj = (LaundrySalesDetailsModel) it.next();

								netTotal = roundNumber((invObj.getUnit_price() * invObj
										.getQuantity())
										+ invObj.getTax_amount()
										+ invObj.getCess_amount()
										- invObj.getDiscount_amount());

								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,
												invObj.getItem().getId(),
												invObj.getItem().getItem_code(),
												invObj.getItem().getName(),
												invObj.getQuantity(),
												invObj.getUnit().getId(),
												invObj.getUnit().getSymbol(),
												invObj.getUnit_price(),
												invObj.getTax().getId(),
												invObj.getTax_amount(),
												invObj.getTax_percentage(),
												invObj.getDiscount_amount(),
												(invObj.getUnit_price() * invObj
														.getQuantity()),
												(long) 0,
												(long) 0,
												invObj.getCess_amount(),
												roundNumber((invObj
														.getUnit_price() * invObj
														.getQuantity())
														+ invObj.getTax_amount()
														+ invObj.getCess_amount()),
												netTotal, invObj.getQuantity() },
										table.getItemIds().size() + 1);
							}

							table.setVisibleColumns(requiredHeaders);

							grandTotalAmtTextField.setNewValue(asString(salObj
									.getAmount()));
							// buildingSelect.setValue(salObj.getBuilding().getId());
							comment.setValue(salObj.getComments());
							date.setValue(salObj.getDate());

							expectedDeliveryDate.setValue(salObj
									.getExpected_delivery_time());
							actualDeliveryDate.setValue(salObj
									.getActual_delivery_time());

							session.setAttribute("SO_Select_Disabled", 'Y');

							customerSelect.setValue(salObj.getCustomer()
									.getId());

							employSelect.setNewValue(salObj.getSales_person());

							roomNoTextField.setValue(salObj
									.getRoom_no());
							refNoField.setValue(asString(salObj.getVoucher_no()));
							shippingChargeTextField.setValue(asString(salObj
									.getShipping_charge()));
							exciseDutyTextField.setValue(asString(salObj
									.getExcise_duty()));
							payingAmountTextField.setValue(asString(salObj
									.getPayment_amount()
									- salObj.getAdvance_amount()));
							advanceAmountTextField.setValue(asString(salObj
									.getAdvance_amount()));

							deliveredCheck.setValue(salObj.isDelivared());

							if (salObj.getPayment_amount() == salObj
									.getAmount()) {
								cashOrCreditRadio.setValue((long) 1);
							} else {
								cashOrCreditRadio.setValue((long) 2);
							}

							salObj.setExcise_duty(toDouble(exciseDutyTextField
									.getValue()));

							responsibleEmployeeCombo.setValue(salObj
									.getResponsible_person());

							updateSalesButton.setVisible(true);
							printButton.setVisible(true);
							deleteSalesButton.setVisible(true);
							cancelSalesButton.setVisible(true);
							saveSalesButton.setVisible(false);

							status = salObj.getStatus();

							removeErrors();

						} else {
							table.removeAllItems();

							grandTotalAmtTextField.setNewValue("0.0");
							payingAmountTextField.setValue("0.0");
							advanceAmountTextField.setValue("0");
							comment.setValue("");
							date.setValue(new Date(getWorkingDate().getTime()));

							expectedDeliveryDate.setValue(getWorkingDate());
							actualDeliveryDate.setValue(getWorkingDate());

							// expected_delivery_date.setValue(new Date());
							customerSelect.setValue(null);
							employSelect.setNewValue(getLoginID());

							deliveredCheck.setValue(false);

							shippingChargeTextField.setValue("0");
							exciseDutyTextField.setValue("0");

							roomNoTextField.setValue("");
							refNoField.setValue("0");
							saveSalesButton.setVisible(true);
							updateSalesButton.setVisible(false);
							printButton.setVisible(false);
							deleteSalesButton.setVisible(false);
							cancelSalesButton.setVisible(false);
						}

						calculateTotals();

						itemSelectCombo.setValue(null);
						itemSelectCombo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setNewValue("0.0");

						customerSelect.focus();

						if (!isFinYearBackEntry()) {
							saveSalesButton.setVisible(false);
							updateSalesButton.setVisible(false);
							deleteSalesButton.setVisible(false);
							cancelSalesButton.setVisible(false);
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
				}

			});

			updateSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							long customer_id = (Long) customerSelect.getValue();

							double costof_inv_amt = 0;

							LaundrySalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());

							List<LaundrySalesDetailsModel> itemsList = new ArrayList<LaundrySalesDetailsModel>();

							LaundrySalesDetailsModel invObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new LaundrySalesDetailsModel();

								item = table.getItem(it.next());

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQuantity((Double) item
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
								invObj.setCess_amount((Double) item
										.getItemProperty(TBC_CESS_AMT)
										.getValue());

								itemsList.add(invObj);

								std_cost = itemDao.getStandardCost(invObj
										.getItem().getId());
								costof_inv_amt += invObj.getQuantity()
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

							salObj.setExpected_delivery_time(CommonUtil
									.getTimestampFromUtilDate(expectedDeliveryDate
											.getValue()));
							salObj.setActual_delivery_time(CommonUtil
									.getTimestampFromUtilDate(actualDeliveryDate
											.getValue()));

							salObj.setDelivared(deliveredCheck.getValue());

							salObj.setResponsible_person(getLoginID());

							salObj.setRoom_no(roomNoTextField
									.getValue());

							salObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue())
									+ toDouble(advanceAmountTextField
											.getValue()));
							salObj.setAdvance_amount(toDouble(advanceAmountTextField
									.getValue()));
							salObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							// salObj.setBuilding(new BuildingModel((Long)
							// buildingSelect.getValue()));
							salObj.setComments(comment.getValue());
							salObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							salObj.setLogin(new S_LoginModel(getLoginID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							salObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							salObj.setDetails_list(itemsList);
							salObj.setVoucher_no(toLong(refNoField.getValue()));
							salObj.setSales_person(getLoginID());

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField.getValue())+ toDouble(advanceAmountTextField.getValue())+salObj.getPaid_by_payment();

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								salObj.setPayment_done('Y');
								salObj.setStatus(1);
								status = 1;
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								salObj.setPayment_done('N');
								salObj.setStatus(2);
								status = 2;
							} else {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id, roundNumber(netAmt));
								status = 3;
								salObj.setPayment_done('Y');
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
												daoObj.cancelSale((Long) salesNumberList
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

			table.addListener(new Property.ValueChangeListener() {

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

						discountTextField
								.setValue(""
										+ item.getItemProperty(TBC_DISCOUNT)
												.getValue());

						netPriceTextField.setNewValue(""
								+ item.getItemProperty(TBC_NET_PRICE)
										.getValue());

						itemSelectCombo.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());

						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());

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

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

					} else {

						itemSelectCombo.setValue(null);
						itemSelectCombo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");

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
							while (itr1.hasNext()) {
								obj = itr1.next();
								item = table.getItem(obj);

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelectCombo.getValue()
												.toString())
										&& item.getItemProperty(TBC_UNIT_ID)
												.getValue()
												.toString()
												.equals(unitSelect.getValue()
														.toString())) {

									qty = 0;
									qty = (Double) item
											.getItemProperty(TBC_QTY)
											.getValue()
											+ toDouble(quantityTextField
													.getValue());
									price = toDouble(unitPriceTextField
											.getValue());
									item.getItemProperty(TBC_QTY).setValue(qty);
									item.getItemProperty(TBC_UNIT_PRICE)
											.setValue(price);
									discount = (Double) item.getItemProperty(
											TBC_DISCOUNT).getValue()
											+ toDouble(discountTextField
													.getValue());

									tax_amt = 0;
									tax_perc = 0;

									if (taxEnable) {
										tax_perc = (Double) item
												.getItemProperty(TBC_TAX_PERC)
												.getValue();

										if (tax_perc > 0) {
											tax_amt = roundNumber(price * qty
													* tax_perc / 100);
										} else {
											tax_perc = 0;
											tax_amt = (Double) item
													.getItemProperty(
															TBC_TAX_AMT)
													.getValue();
										}
									}

									total = roundNumber(price * qty);

									cess_amt = 0;
									if (isCessEnableOnItem((Long) item
											.getItemProperty(TBC_ITEM_ID)
											.getValue())) {
										cess_perc = (Double) session
												.getAttribute("cess_percentage");
										cess_amt = roundNumber(tax_amt
												* getCessPercentage() / 100);
									}

									item.getItemProperty(TBC_TAX_AMT).setValue(
											tax_amt);
									item.getItemProperty(TBC_NET_PRICE)
											.setValue(total);
									item.getItemProperty(TBC_DISCOUNT)
											.setValue(discount);
									item.getItemProperty(TBC_CESS_AMT)
											.setValue(cess_amt);
									item.getItemProperty(TBC_NET_TOTAL)
											.setValue(
													roundNumber(total + tax_amt
															+ cess_amt));
									item.getItemProperty(TBC_NET_FINAL)
											.setValue(
													roundNumber(total + tax_amt
															+ cess_amt
															- discount));

									/*
									 * item.getItemProperty(TBC_MANUFACT_DATE)
									 * .setValue( stk.getManufacturing_date());
									 * item.getItemProperty(TBC_EXPIRE_DATE)
									 * .setValue(stk.getExpiry_date());
									 * item.getItemProperty(TBC_STOCK_ID)
									 * .setValue(stk.getId());
									 */

									already_added_item = true;

									break;
								}
							}

							if (!already_added_item) {

								price = 0;
								qty = 0;
								total = 0;
								double discount_amt = 0;

								price = toDouble(unitPriceTextField.getValue());
								qty = toDouble(quantityTextField.getValue());
								discount_amt = toDouble(discountTextField
										.getValue());

								netPriceTextField.setNewValue(asString(price
										* qty));

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

								double conv_rat = 1;

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
												conv_rat * qty }, id);

								table.setVisibleColumns(requiredHeaders);

								itemSelectCombo.setValue(null);
								quantityTextField.setValue("0.0");
								unitPriceTextField.setValue("0.0");
								netPriceTextField.setNewValue("0.0");
								discountTextField.setValue("0.0");
							} else {
								itemSelectCombo.setValue(null);
								quantityTextField.setValue("0.0");
								unitPriceTextField.setValue("0.0");
								netPriceTextField.setNewValue("0.0");
								discountTextField.setValue("0.0");
							}
							calculateTotals();

							itemSelectCombo.focus();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
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

							/*
							 * item.getItemProperty(TBC_MANUFACT_DATE).setValue(
							 * stk.getManufacturing_date());
							 * item.getItemProperty(TBC_EXPIRE_DATE).setValue(
							 * stk.getExpiry_date());
							 * item.getItemProperty(TBC_STOCK_ID).setValue(
							 * stk.getId());
							 */

							double conv_rat = 1;

							item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
									.setValue(conv_rat * qty);

							table.setVisibleColumns(requiredHeaders);

							// itemsCompo.setValue(null);
							// itemsCompo.focus();
							itemSelectCombo.setValue(null);
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");

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
										itm.getId(),
										(Long) unitSelect.getValue(), 0)));

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

			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					List<Object> reportList = new ArrayList<Object>();
					SalesPrintBean bean = null;
					NumberToWords numberToWords = new NumberToWords();
					double total = 0;
					try {

						LaundrySalesModel salObj = daoObj
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
						map.put("ROOM_NO", salObj.getRoom_no());
						map.put("DELIVERY_DATE", salObj.getExpected_delivery_time());
						map.put("CUSTOMER_ADDRESS", address);
						map.put("SALES_BILL_NO", toLong(salesNumberList
								.getItemCaption(salesNumberList.getValue())));
						map.put("BILL_DATE", CommonUtil.formatDateToDDMMMYYYY(salObj.getDate()));
						
						map.put("IMAGE_PATH", VaadinServlet.getCurrent().getServletContext()
								.getRealPath("/").toString());

						map.put("SALES_MAN", usrDao
								.getUserNameFromLoginID(salObj
										.getSales_person()));

						String resp = "";
						if (salObj.getResponsible_person() != 0) {
							UserModel usrObj = usrDao.getUserFromLogin(salObj
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

						String type = "";
						if (status == 1) {
							type = "Cash Sale";
						} else {
							type = "Credit Sale";
						}
						map.put("SALES_TYPE", type);
						map.put("OFFICE_NAME", customerModel.getLedger()
								.getOffice().getName());

						LaundrySalesDetailsModel invObj;
						Iterator<LaundrySalesDetailsModel> itr1 = salObj
								.getDetails_list().iterator();
						while (itr1.hasNext()) {
							invObj = itr1.next();

							bean = new SalesPrintBean(invObj.getItem()
									.getName(), invObj.getQuantity(), invObj
									.getUnit_price(),
									(invObj.getQuantity() * invObj
											.getUnit_price())
											- invObj.getDiscount_amount()
											+ invObj.getCess_amount()
											+ invObj.getTax_amount(), invObj
											.getUnit().getSymbol(), invObj
											.getItem().getItem_code(), invObj
											.getQuantity());

							total += bean.getTotal();

							reportList.add(bean);
						}
						
						map.put("advance_amount", salObj.getAdvance_amount());
						map.put("comments", salObj.getComments());
						map.put("due_amount", salObj.getAmount()-salObj.getPayment_amount());
						

						S_OfficeModel officeModel = new OfficeDao()
								.getOffice(getOfficeID());
						map.put("AMOUNT_IN_WORDS", numberToWords.convertNumber(
								roundNumber(total) + "", officeModel
										.getCurrency().getInteger_part(),
								officeModel.getCurrency().getFractional_part()));

						Report report = new Report(getLoginID());
//						report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
//						report.setJrxmlFileName("LaundrySales_Print");
//						report.setReportFileName("Invoice");
						// report.setReportTitle("Sales Invoice");
						// report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						
						
						report.setJrxmlFileName("LaundrySales_Print");
						report.setReportFileName("Invoice Copy 3");
						map.put("copy_no", (int)3);
						report.createReport(reportList, map);
						
						
						map.remove("copy_no");
						report.setJrxmlFileName("LaundrySales_Print");
						report.setReportFileName("Invoice Copy 2");
						map.put("copy_no", (int)2);
						report.createReport(reportList, map);
						
						
						map.remove("copy_no");
						report.setJrxmlFileName("LaundrySales_Print");
						report.setReportFileName("Invoice");
						map.put("copy_no", (int)1);
						report.createReport(reportList, map);
						

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (!isFinYearBackEntry()) {
				saveSalesButton.setVisible(false);
				updateSalesButton.setVisible(false);
				deleteSalesButton.setVisible(false);
				cancelSalesButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addListener(new Property.ValueChangeListener() {
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

			quantityTextField.setImmediate(true);

			quantityTextField.addListener(new Property.ValueChangeListener() {
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

			shippingChargeTextField
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								double fianalAmt = 0;
								double ship_charg = 0, excise_duty = 0;
								try {
									fianalAmt = toDouble(table
											.getColumnFooter(TBC_NET_FINAL));
									ship_charg = toDouble(shippingChargeTextField
											.getValue());
									excise_duty = toDouble(exciseDutyTextField
											.getValue());
								} catch (Exception e) {
									// TODO: handle exception
								}

								grandTotalAmtTextField
										.setNewValue(asString(roundNumber(fianalAmt
												+ ship_charg + excise_duty)));

							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}
						}
					});

			exciseDutyTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						double fianalAmt = toDouble(table
								.getColumnFooter(TBC_NET_FINAL));
						double ship_charg = 0, excise_duty = 0;
						try {
							ship_charg = toDouble(shippingChargeTextField
									.getValue());
							excise_duty = toDouble(exciseDutyTextField
									.getValue());
						} catch (Exception e) {
							// TODO: handle exception
						}

						grandTotalAmtTextField
								.setNewValue(asString(roundNumber(fianalAmt
										+ ship_charg + excise_duty)));
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});
			
			ValueChangeListener valChnLis=new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						balanceAmountTxtFld.setNewValue(asString(roundNumber(toDouble(grandTotalAmtTextField.getValue())-toDouble(advanceAmountTextField.getValue())-
								toDouble(payingAmountTextField.getValue()))));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			};
			
			payingAmountTextField.addValueChangeListener(valChnLis);
			advanceAmountTextField.addValueChangeListener(valChnLis);
			payingAmountTextField.setImmediate(true);
			advanceAmountTextField.setImmediate(true);
			// advanceAmountTextField.addListener(new
			// Property.ValueChangeListener() {
			// public void valueChange(ValueChangeEvent event) {
			// try {
			// if(toDouble(advanceAmountTextField.getValue())>toDouble(payingAmountTextField.getValue()))
			// payingAmountTextField.setValue(advanceAmountTextField.getValue());
			// } catch (Exception e) {
			// e.printStackTrace();
			// Notification.show("Error..!", "Error Message :" +
			// e.getCause(),Type.ERROR_MESSAGE);
			// }
			// }
			// });

			if (isSaleEditable()) {
				updateSalesButton.setEnabled(true);
			} else
				updateSalesButton.setEnabled(false);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return pannel;
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

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc = 0;

		try {
			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
			disc = Double.parseDouble(discountTextField.getValue());
		} catch (Exception e) {
			// TODO: handle exception
		}
		netPriceTextField.setNewValue(asString(new BigDecimal((unitPrc * qty)
				- disc)));
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
			
			double ship_charg = 0, excise_duty = 0;
			try {
				ship_charg = toDouble(shippingChargeTextField.getValue());
				excise_duty = toDouble(exciseDutyTextField.getValue());
			} catch (Exception e) {
				// TODO: handle exception
			}

			grandTotalAmtTextField.setNewValue(asString(roundNumber(net_ttl
					+ ship_charg + excise_duty)));
			
			try {
				balanceAmountTxtFld.setNewValue(asString(roundNumber(toDouble(grandTotalAmtTextField.getValue())-toDouble(advanceAmountTextField.getValue())-
						toDouble(payingAmountTextField.getValue()))));
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {
			// TODO: handle exception
			Notification.show(getPropertyName("error")+e.getCause(), Type.ERROR_MESSAGE);
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
			Notification.show("Error..!!",
					"Error Message from method, deleteIte() :" + e.getCause(),
					Type.ERROR_MESSAGE);
		}
	}

	public void loadSale(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new LaundrySalesModel(0, "----Create New-----"));
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

	public void removeErrors() {
		payingAmountTextField.setComponentError(null);
		advanceAmountTextField.setComponentError(null);
		cashOrCreditRadio.setComponentError(null);
		shippingChargeTextField.setComponentError(null);
		exciseDutyTextField.setComponentError(null);
		table.setComponentError(null);
		responsibleEmployeeCombo.setComponentError(null);
		roomNoTextField.setComponentError(null);
		refNoField.setComponentError(null);
		customerSelect.setComponentError(null);
		date.setComponentError(null);

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
				// TODO: handle exception
			}
		}

		if (advanceAmountTextField.getValue() == null
				|| advanceAmountTextField.getValue().equals("")) {
			advanceAmountTextField.setValue("0.0");
		} /*
		 * else { try { if (toDouble(advanceAmountTextField.getValue()) >
		 * toDouble(grandTotalAmtTextField.getValue())) {
		 * setRequiredError(advanceAmountTextField,
		 * "Enter a valid amount. Must be les than", true);
		 * advanceAmountTextField.focus(); ret = false; } else {
		 * if(toDouble(advanceAmountTextField
		 * .getValue())>toDouble(payingAmountTextField.getValue())) {
		 * setRequiredError(advanceAmountTextField,
		 * "Paying amount must be greater than or equal to Advance amount",
		 * true); advanceAmountTextField.focus(); ret = false; } else
		 * setRequiredError(advanceAmountTextField, null, false);
		 * 
		 * }
		 * 
		 * } catch (Exception e) { setRequiredError(advanceAmountTextField,
		 * "Enter a valid amount", true); advanceAmountTextField.focus(); ret =
		 * false; // TODO: handle exception } }
		 */

		/*if ((Long) cashOrCreditRadio.getValue() == 1) {

			if (toDouble(payingAmountTextField.getValue())
					+ toDouble(advanceAmountTextField.getValue()) != toDouble(grandTotalAmtTextField
						.getValue())) {
				setRequiredError(payingAmountTextField,
						getPropertyName("invalid_data"), true);
				payingAmountTextField.focus();
				ret = false;
			}

		} else if (toDouble(payingAmountTextField.getValue()) > toDouble(grandTotalAmtTextField
				.getValue())
				&& toDouble(grandTotalAmtTextField.getValue()) != 0) {
			setRequiredError(payingAmountTextField,
					getPropertyName("invalid_data"), true);
			payingAmountTextField.focus();
			ret = false;
		}*/

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

//		if (responsibleEmployeeCombo.getValue() == null
//				|| responsibleEmployeeCombo.getValue().equals("")) {
//			setRequiredError(responsibleEmployeeCombo,
//					getPropertyName("invalid_selection"), true);
//			responsibleEmployeeCombo.focus();
//			ret = false;
//		} else
//			setRequiredError(responsibleEmployeeCombo, null, false);

		/*if (roomNoTextField.getValue() == null
				|| roomNoTextField.getValue().equals("")) {
			roomNoTextField.setValue("0");
		} else {
			try {
				if (toInt(roomNoTextField.getValue()) < 0) {
					setRequiredError(roomNoTextField,
							getPropertyName("invalid_data"), true);
					roomNoTextField.focus();
					ret = false;
				} else
					setRequiredError(roomNoTextField, null, false);
			} catch (Exception e) {
				setRequiredError(roomNoTextField,
						getPropertyName("invalid_data"), true);
				roomNoTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}*/
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

		// if (employSelect.getValue() == null
		// || employSelect.getValue().equals("")) {
		// setRequiredError(employSelect, "Select a employ", true);
		// employSelect.focus();
		// ret = false;
		// } else
		// setRequiredError(employSelect, null, false);

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
		return null;
	}
	@Override
	public SComboField getBillNoFiled() {
		return salesNumberList;
	}
}
