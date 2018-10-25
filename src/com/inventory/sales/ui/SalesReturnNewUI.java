package com.inventory.sales.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.StockRackMappingPannel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.SalesManMapDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.reports.bean.CreditNoteBean;
import com.inventory.sales.dao.SalesReturnNewDao;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date Jan 9, 2014
 */
public class SalesReturnNewUI extends SparkLogic {

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

	static String TBC_STOCK_QTY = "GRV Stock";
	static String TBC_GOOD_STOCK = "Good Stock";
	static String TBC_WASTE_QTY = "Waste Qty";
	static String TBC_PURCHASE_RETURN_QTY = "Return Qty";

	static String TBC_PURCHASE_RATE = "Purch Rate";

	static String TBC_SUPPLIER_ID = "Supplier ID";

	protected long STATUS_RETURNED = 3;

	// static String TBC_MANUFACT_DATE = "Mfg. Date";
	// static String TBC_EXPIRE_DATE = "Exp. Date";

	static String TBC_CONVERTION_QTY = "Convertion Qty";
	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";

	SalesReturnNewDao daoObj;

	static int flag = 0;

	private SComboField salesreturnNumberList;

	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	private Map<String, Object> map;

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

	STextField goodStkQtyTextField, stkQtyTextField, wasteQtyTextField,
			returnQtyTextField, purchRateTextField;
	SComboField supplierSelect;

	STextField refNoTextField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSalesReturnButton;
	SButton updatePurchaseButton;
	SButton deleteReturnButton;
	SButton cancelReturnButton;
	SButton printButton;

	SButton updateRackStock;

	SWindow rackStockWindow;

	ItemDao itemDao;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	STextField salesBillNoTextField;
	// SComboField buildingSelect;
	SComboField customerSelect;
	SDateField date;
	// SDateField expected_delivery_date;

	SDateField expiry_date;
	SDateField manufacturing_date;

	STextField grandTotalAmtTextField;
	STextField shippingChargeTextField;
	STextField exciseDutyTextField;
	STextArea comment;

	STextField convertionQtyTextField;
	STextField convertedQtyTextField;

	SettingsValuePojo settings;

	WrappedSession session;

	private String[] allHeaders;
	private String[] requiredHeaders;

	boolean taxEnable = isTaxEnable();

	CommonMethodsDao comDao;

	SHorizontalLayout popupHor;

	SWindow popupWindow;

	List<Long> PONoList;

	CustomerDao custDao;
	TaxDao taxDao;
	UnitDao untDao;
	LedgerDao ledDao;

	SButton newSaleButton;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	SComboField responsibleEmployeeCombo;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		comDao = new CommonMethodsDao();
		custDao = new CustomerDao();
		itemDao = new ItemDao();
		taxDao = new TaxDao();
		untDao = new UnitDao();
		ledDao = new LedgerDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Purchase");

		taxEnable = isTaxEnable();

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_PO_ID, TBC_INV_ID, TBC_CONVERTION_QTY,
				TBC_QTY_IN_BASIC_UNI, TBC_GOOD_STOCK, TBC_STOCK_QTY,
				TBC_WASTE_QTY, TBC_PURCHASE_RETURN_QTY, TBC_SUPPLIER_ID,
				TBC_PURCHASE_RATE };

		if (taxEnable) {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_GOOD_STOCK, TBC_STOCK_QTY,
					TBC_WASTE_QTY, TBC_PURCHASE_RETURN_QTY, TBC_UNIT,
					TBC_UNIT_PRICE, TBC_TAX_AMT, TBC_DISCOUNT, TBC_NET_PRICE };
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_GOOD_STOCK, TBC_STOCK_QTY,
					TBC_WASTE_QTY, TBC_PURCHASE_RETURN_QTY, TBC_UNIT,
					TBC_UNIT_PRICE, TBC_DISCOUNT, TBC_NET_PRICE };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}

		requiredHeaders = templist.toArray(new String[templist.size()]);

		setSize(1260, 600);

		session = getHttpSession();

		daoObj = new SalesReturnNewDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(18);
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

		pannel.setSizeFull();
		form.setSizeFull();

		try {
			// List list = new ArrayList();
			// list.add(new SalesReturnModel(0, "----Create New-----"));
			// list.addAll(daoObj.getAllCreditNotesAsComment(getOfficeID()));

			goodStkQtyTextField = new STextField(getPropertyName("good_stock"),
					60);
			stkQtyTextField = new STextField(getPropertyName("grv_stock"), 60);
			wasteQtyTextField = new STextField(getPropertyName("waste_qty"), 60);
			returnQtyTextField = new STextField(
					getPropertyName("purchase_return"), 60);
			purchRateTextField = new STextField(
					getPropertyName("purchase_rate"), 60);

			supplierSelect = new SComboField(
					getPropertyName("supplier"),
					80,
					new SupplierDao()
							.getAllActiveSupplierNamesWithLedgerID(getOfficeID()),
					"id", "name", true, "Select");

			salesreturnNumberList = new SComboField(null, 125, null, "id",
					"comments", false, "Create New");

			loadSalesReturn(0);

			salesBillNoTextField = new STextField(null, 120);

			refNoTextField = new STextField(null, 120);

			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date(
					getWorkingDate().getTime()));

			customerSelect = new SComboField(null, 250, null, "id", "name",
					true, getPropertyName("select"));
			
			responsibleEmployeeCombo = new SComboField(null, 125,
					new SalesManMapDao().getUsers(getOfficeID(),
							SConstants.SALES_MAN), "id", "first_name", false,
					"Select");

			reloadCustomers();

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesreturnNumberList);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("credit_note_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_no")), 3, 0);
			masterDetailsGrid.addComponent(salesBillNoTextField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(salesBillNoTextField,
					Alignment.MIDDLE_LEFT);
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

			SHorizontalLayout hrl = new SHorizontalLayout();
			hrl.addComponent(customerSelect);

			masterDetailsGrid.addComponent(hrl, 2, 1);

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 1);
			masterDetailsGrid.addComponent(refNoTextField, 4, 1);
			
			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("sales_man")), 6, 1);
			masterDetailsGrid.addComponent(responsibleEmployeeCombo, 8, 1);

			masterDetailsGrid.setStyleName("master_border");

			itemsCompo = new SComboField(
					getPropertyName("item"),
					220,
					itemDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
					"id", "name");

			quantityTextField = new STextField(getPropertyName("quantity"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			unitPriceTextField = new STextField(
					getPropertyName("sales_return_unit_price"), 80);
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

			addingGrid.addComponent(hrz1);

			addingGrid.addComponent(goodStkQtyTextField);
			addingGrid.addComponent(stkQtyTextField);
			addingGrid.addComponent(wasteQtyTextField);
			addingGrid.addComponent(returnQtyTextField);
			addingGrid.addComponent(purchRateTextField);
			addingGrid.addComponent(supplierSelect);

			goodStkQtyTextField.setStyleName("textfield_align_right");
			stkQtyTextField.setStyleName("textfield_align_right");
			wasteQtyTextField.setStyleName("textfield_align_right");
			returnQtyTextField.setStyleName("textfield_align_right");
			purchRateTextField.setStyleName("textfield_align_right");

			addingGrid.addComponent(quantityTextField);

			SHorizontalLayout hrz2 = new SHorizontalLayout();
			hrz2.addComponent(unitSelect);
			SVerticalLayout vert = new SVerticalLayout();
			vert.setSpacing(false);
			hrz2.addComponent(vert);
			hrz2.setComponentAlignment(vert, Alignment.MIDDLE_CENTER);
			hrz2.setSpacing(true);
			addingGrid.addComponent(hrz2);

			addingGrid.addComponent(convertionQtyTextField);

			addingGrid.addComponent(convertedQtyTextField);

			popupHor = new SHorizontalLayout();
			popupHor.addComponent(unitPriceTextField);
			addingGrid.addComponent(popupHor);

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

			table.addContainerProperty(TBC_GOOD_STOCK, Double.class, null,
					getPropertyName("good_stock"), null, Align.RIGHT);
			table.addContainerProperty(TBC_STOCK_QTY, Double.class, null,
					getPropertyName("stock_qty"), null, Align.RIGHT);
			table.addContainerProperty(TBC_WASTE_QTY, Double.class, null,
					getPropertyName("waste_qty"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PURCHASE_RETURN_QTY, Double.class,
					null, getPropertyName("return_qty"), null, Align.RIGHT);
			table.addContainerProperty(TBC_SUPPLIER_ID, Long.class, null,
					getPropertyName("supplier_id"), null, Align.CENTER);
			table.addContainerProperty(TBC_PURCHASE_RATE, Double.class, null,
					getPropertyName("purchase_rate"), null, Align.RIGHT);

			table.addContainerProperty(TBC_CONVERTION_QTY, Double.class, null,
					getPropertyName("convertion_qty"), null, Align.RIGHT);

			table.addContainerProperty(TBC_QTY_IN_BASIC_UNI, Double.class,
					null, getPropertyName("qty_basic_unit"), null, Align.RIGHT);

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
			// table.setColumnExpandRatio(TBC_MANUFACT_DATE, (float) 1.1);
			// table.setColumnExpandRatio(TBC_EXPIRE_DATE, (float) 1.1);

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
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");
			comment = new STextArea(null, 400, 30);

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

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

			if (!isManufDateEnable()) {
				expiry_date.setVisible(false);
				manufacturing_date.setVisible(false);
			}

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

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

			saveSalesReturnButton = new SButton(getPropertyName("save"), 70);
			saveSalesReturnButton.setStyleName("savebtnStyle");
			saveSalesReturnButton.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			updatePurchaseButton = new SButton(getPropertyName("update"), 80);
			updatePurchaseButton.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updatePurchaseButton.setStyleName("updatebtnStyle");

			deleteReturnButton = new SButton(getPropertyName("delete"), 78);
			deleteReturnButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteReturnButton.setStyleName("deletebtnStyle");

			cancelReturnButton = new SButton(getPropertyName("cancel"), 78);
			cancelReturnButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			cancelReturnButton.setStyleName("deletebtnStyle");

			printButton = new SButton(getPropertyName("print"), 78);
			printButton.setIcon(new ThemeResource("icons/print.png"));

			updateRackStock = new SButton(getPropertyName("arrange_stocks"),
					135);
			updateRackStock.setIcon(new ThemeResource(
					"icons/arrangestockSideIcon.png"));
			updateRackStock.setStyleName("stockbtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveSalesReturnButton);
			mainButtonLayout.addComponent(updatePurchaseButton);
			if (!settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(deleteReturnButton);
			else
				mainButtonLayout.addComponent(cancelReturnButton);
			// mainButtonLayout.addComponent(updateRackStock);
			mainButtonLayout.addComponent(printButton);

			updatePurchaseButton.setVisible(false);
			updateRackStock.setVisible(false);
			deleteReturnButton.setVisible(false);
			cancelReturnButton.setVisible(false);
			printButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			
			customerSelect.focus();
			
			pannel.setContent(windowNotif);

			rackStockWindow = new SWindow(getPropertyName("stock_arrangement"));

			getHttpSession().setAttribute("firsttime", "Y");

			rackStockWindow.center();

			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);

			goodStkQtyTextField.setValue("0");
			stkQtyTextField.setValue("0");
			wasteQtyTextField.setValue("0");
			returnQtyTextField.setValue("0");
			purchRateTextField.setValue("0");
			supplierSelect.setValue(null);

			PONoList = new ArrayList<Long>();
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)salesreturnNumberList.getValue(),confirmBox.getUserID());
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
						if(salesreturnNumberList.getValue()!=null && !salesreturnNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)salesreturnNumberList.getValue(),
									"Sales Return : No. "+salesreturnNumberList.getItemCaption(salesreturnNumberList.getValue()));
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
						if(salesreturnNumberList.getValue()!=null && !salesreturnNumberList.getValue().toString().equals("0")) {
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
					loadSalesReturn(0);
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

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesreturnNumberList.setValue((long) 0);
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
						reloadCustomers();
						if (getHttpSession().getAttribute("saved_id") != null) {
							customerSelect.setValue(getHttpSession()
									.getAttribute("saved_id"));
							getHttpSession().removeAttribute("saved_id");
						}
					}
				}
			});

			customerSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {
								if (customerSelect.getValue() != null) {
									customerSelect.setDescription("<h1><i>Current Balance</i> : "
											+ comDao.getLedgerCurrentBalance((Long) customerSelect
													.getValue()) + "</h1>");
									CustomerModel cust = custDao
											.getCustomerFromLedger((Long) customerSelect
													.getValue());
									responsibleEmployeeCombo.setValue(cust
											.getResponsible_person());
								} else{
									customerSelect.setDescription(null);
									responsibleEmployeeCombo.setValue(null);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			saveSalesReturnButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						Set<Long> itemIdsSet = new HashSet<Long>();

						if (isValid()) {

							long customer_id = (Long) customerSelect.getValue();

							SalesReturnModel purObj = new SalesReturnModel();

							List<SalesReturnInventoryDetailsModel> itemsList = new ArrayList<SalesReturnInventoryDetailsModel>();

							List<PurchaseReturnInventoryDetailsModel> purchaseArrayList;

							ArrayList<SalesReturnInventoryDetailsModel> itemsArrayList = new ArrayList<SalesReturnInventoryDetailsModel>();
							List<ItemStockModel> itemStockList = new ArrayList<ItemStockModel>();
							HashMap<TransactionModel, PurchaseReturnModel> hash = new HashMap<TransactionModel, PurchaseReturnModel>();

							ItemStockModel model;
							SalesReturnInventoryDetailsModel invObj;
							PurchaseReturnInventoryDetailsModel purchaseInvDetModel;
							PurchaseReturnModel purchaseReturnModel;

							Item item;
							long stockId = 0;
							double conv_rat;
							Iterator it = table.getItemIds().iterator();
//							while (it.hasNext()) {
//								invObj = new SalesReturnInventoryDetailsModel();
//								item = table.getItem(it.next());
//
//								invObj.setItem(new ItemModel((Long) item
//										.getItemProperty(TBC_ITEM_ID)
//										.getValue()));
//								// invObj.setStock_quantity((Double) item
//								// .getItemProperty(TBC_QTY).getValue());
//								invObj.setBalance((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//
//								if (taxEnable) {
//									invObj.setTax(new TaxModel((Long) item
//											.getItemProperty(TBC_TAX_ID)
//											.getValue()));
//									invObj.setTax_amount((Double) item
//											.getItemProperty(TBC_TAX_AMT)
//											.getValue());
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
//								invObj.setUnit_price((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue());
//
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue());
//								}
//
//								invObj.setOrder_id((Long) item.getItemProperty(
//										TBC_SUPPLIER_ID).getValue());
//
//								// invObj.setManufacturing_date(CommonUtil
//								// .getSQLDateFromUtilDate((Date) item
//								// .getItemProperty(
//								// TBC_MANUFACT_DATE)
//								// .getValue()));
//								// invObj.setExpiry_date(CommonUtil
//								// .getSQLDateFromUtilDate((Date) item
//								// .getItemProperty(
//								// TBC_EXPIRE_DATE)
//								// .getValue()));
//
//								invObj.setId((Long) item.getItemProperty(
//										TBC_INV_ID).getValue());
//
//								conv_rat = (Double) item.getItemProperty(
//										TBC_CONVERTION_QTY).getValue();
//
//								invObj.setStock_quantity(CommonUtil
//										.roundNumber(conv_rat
//												* Double.parseDouble(item
//														.getItemProperty(
//																TBC_STOCK_QTY)
//														.getValue().toString())));
//								invObj.setGood_stock(CommonUtil
//										.roundNumber(conv_rat
//												* Double.parseDouble(item
//														.getItemProperty(
//																TBC_GOOD_STOCK)
//														.getValue().toString())));
//								invObj.setReturned_quantity(CommonUtil.roundNumber(conv_rat
//										* Double.parseDouble(item
//												.getItemProperty(
//														TBC_PURCHASE_RETURN_QTY)
//												.getValue().toString())));
//								invObj.setWaste_quantity(CommonUtil
//										.roundNumber(conv_rat
//												* Double.parseDouble(item
//														.getItemProperty(
//																TBC_WASTE_QTY)
//														.getValue().toString())));
//
//								invObj.setQuantity_in_basic_unit(CommonUtil
//										.roundNumber(conv_rat
//												* invObj.getBalance()));
//
//								itemsList.add(invObj);
//
//								itemIdsSet.add((Long) item.getItemProperty(
//										TBC_ITEM_ID).getValue());
//
//								if (toDouble(item
//										.getItemProperty(TBC_GOOD_STOCK)
//										.getValue().toString()) > 0) {
//									stockId = 0;
//									model = new ItemStockModel();
//									/*
//									 * model.setBalance(Double
//									 * .parseDouble(salesReturnTable .getItem(
//									 * obj) .getItemProperty( TABLE_BALANCE)
//									 * .toString()));
//									 */
//
//									model.setBalance(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_GOOD_STOCK)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setExpiry_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setDate_time(CommonUtil
//											.getCurrentDateTime());
//
//									model.setItem(new ItemModel((Long) item
//											.getItemProperty(TBC_ITEM_ID)
//											.getValue()));
//
//									model.setManufacturing_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setPurchase_id(0);
//									model.setQuantity(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_GOOD_STOCK)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setStatus(SConstants.stock_statuses.GOOD_STOCK);
//									itemStockList.add(model);
//									// stockId =
//									// salesReturnDao
//									// .addNewStock(model);
//									// stockList.add(stockId);
//									/*
//									 * inventoryDetailsModel .
//									 * setStock_id(stockId);
//									 */
//								}
//
//								if (toDouble(item
//										.getItemProperty(TBC_STOCK_QTY)
//										.getValue().toString()) > 0) {
//									stockId = 0;
//									model = new ItemStockModel();
//									/*
//									 * model.setBalance(Double
//									 * .parseDouble(salesReturnTable .getItem(
//									 * obj) .getItemProperty( TABLE_BALANCE)
//									 * .toString()));
//									 */
//
//									model.setBalance(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_STOCK_QTY)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setExpiry_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setDate_time(CommonUtil
//											.getCurrentDateTime());
//
//									model.setItem(new ItemModel((Long) item
//											.getItemProperty(TBC_ITEM_ID)
//											.getValue()));
//
//									model.setManufacturing_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setPurchase_id(0);
//									model.setQuantity(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_STOCK_QTY)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setStatus(STATUS_RETURNED);
//									itemStockList.add(model);
//									// stockId =
//									// salesReturnDao
//									// .addNewStock(model);
//									// stockList.add(stockId);
//									/*
//									 * inventoryDetailsModel .
//									 * setStock_id(stockId);
//									 */
//								}
//
//								// Purchase
//								// Return=====================================================================
//								if (toDouble(item
//										.getItemProperty(
//												TBC_PURCHASE_RETURN_QTY)
//										.getValue().toString()) > 0) {
//
//									purchaseArrayList = new ArrayList<PurchaseReturnInventoryDetailsModel>();
//									purchaseReturnModel = new PurchaseReturnModel();
//									purchaseInvDetModel = new PurchaseReturnInventoryDetailsModel();
//
//									purchaseInvDetModel.setItem(new ItemModel(
//											(Long) item.getItemProperty(
//													TBC_ITEM_ID).getValue()));
//
//									purchaseInvDetModel.setQunatity(CommonUtil
//											.roundNumber((Double) item
//													.getItemProperty(
//															TBC_PURCHASE_RETURN_QTY)
//													.getValue()));
//									purchaseInvDetModel.setQty_in_basic_unit(CommonUtil
//											.roundNumber(conv_rat
//													* (Double) item
//															.getItemProperty(
//																	TBC_PURCHASE_RETURN_QTY)
//															.getValue()));
//
//									purchaseInvDetModel.setUnit(new UnitModel(
//											(Long) item.getItemProperty(
//													TBC_UNIT_ID).getValue()));
//
//									double purchasePrice = (Double) item
//											.getItemProperty(TBC_PURCHASE_RATE)
//											.getValue();
//
//									purchaseInvDetModel
//											.setUnit_price(purchasePrice);
//
//									purchaseInvDetModel.setTax(new TaxModel(
//											(Long) item.getItemProperty(
//													TBC_TAX_ID).getValue()));
//									purchaseInvDetModel
//											.setTax_amount((Double) item
//													.getItemProperty(
//															TBC_TAX_AMT)
//													.getValue());
//									purchaseInvDetModel
//											.setTax_percentage((Double) item
//													.getItemProperty(
//															TBC_TAX_PERC)
//													.getValue());
//
//									purchaseInvDetModel
//											.setBalance((Double) item
//													.getItemProperty(
//															TBC_PURCHASE_RETURN_QTY)
//													.getValue());
//
//									purchaseInvDetModel.setDiscount_amount(0);
//									purchaseInvDetModel
//											.setManufacturing_date(CommonUtil
//													.getCurrentSQLDate());
//									purchaseInvDetModel
//											.setExpiry_date(CommonUtil
//													.getCurrentSQLDate());
//									purchaseInvDetModel.setOrder_id((long) 0);
//
//									purchaseArrayList.add(purchaseInvDetModel);
//
//									purchaseReturnModel
//											.setLogin_id(new S_LoginModel(
//													getLoginID()));
//									purchaseReturnModel.setRefNo(refNoTextField
//											.getValue());
//									purchaseReturnModel
//											.setOffice(new S_OfficeModel(
//													getOfficeID()));
//									purchaseReturnModel.setAmount(CommonUtil
//											.roundNumber(purchasePrice
//													* toDouble(item
//															.getItemProperty(
//																	TBC_PURCHASE_RETURN_QTY)
//															.getValue()
//															.toString())));
//									purchaseReturnModel.setStatus(2);
//									purchaseReturnModel.setComments("");
//									purchaseReturnModel
//											.setSupplier(new LedgerModel(
//													(Long) item
//															.getItemProperty(
//																	TBC_SUPPLIER_ID)
//															.getValue()));
//									purchaseReturnModel.setDate(CommonUtil
//											.getSQLDateFromUtilDate(date
//													.getValue()));
//									purchaseReturnModel
//											.setInventory_details_list(purchaseArrayList);
//									purchaseReturnModel
//											.setDebit_note_no(getNextSequence(
//													"Debit Note Number",
//													getLoginID()));
//									purchaseReturnModel.setPayment_amount(0);
//									purchaseReturnModel.setActive(true);
//
//									// Transaction Starts
//									// Here
//
//									FinTransaction purfinTransaction = new FinTransaction();
//
//									double amount = roundNumber(purchasePrice
//											* toDouble(item
//													.getItemProperty(
//															TBC_PURCHASE_RETURN_QTY)
//													.getValue().toString()));
//
//									purfinTransaction.addTransaction(
//											SConstants.CR,
//											settings.getPURCHASE_RETURN_ACCOUNT(),
//											(Long) item.getItemProperty(
//													TBC_SUPPLIER_ID).getValue(),
//											amount);
//
//									hash.put(
//											purfinTransaction
//													.getTransaction(
//															SConstants.PURCHASE_RETURN,
//															CommonUtil
//																	.getSQLDateFromUtilDate(date
//																			.getValue())),
//											purchaseReturnModel);
//
//								}
//
//								/*
//								 * if (purObj.getPayment_amount() == purObj
//								 * .getAmount()) {
//								 * cashOrCreditRadio.setValue((long) 1); } else
//								 * { cashOrCreditRadio.setValue((long) 2); }
//								 */
//
//							}

							/*
							 * if (isExciceDutyEnable()) {
							 * purObj.setExcise_duty(
							 * toDouble(exciseDutyTextField .getValue())); } if
							 * (isShippingChargeEnable()) {
							 * purObj.setShipping_charge
							 * (toDouble(shippingChargeTextField .getValue()));
							 * }
							 */
//							purObj.setPayment_amount(toDouble(payingAmountTextField
//									.getValue()));
							// purObj.setCredit_period(toInt(creditPeriodTextField.getValue()));
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
							purObj.setSales_numbers(salesBillNoTextField
									.getValue());
							purObj.setRef_no(refNoTextField.getValue());
							purObj.setStatus(1);
							purObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							purObj.setInventory_details_list(itemsList);

//							purObj.setCredit_note_no(getNextSequence(
//									"Credit Note Number", getLoginID()));
							purObj.setActive(true);
//							purObj.setResponsible_person((Long) responsibleEmployeeCombo
//									.getValue());

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField
									.getValue());

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										settings.getCASH_ACCOUNT(),
										customer_id, roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getSALES_RETURN_ACCOUNT(),
										roundNumber(netAmt));
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getSALES_RETURN_ACCOUNT(),
										roundNumber(netAmt));
							} else {
								trans.addTransaction(SConstants.CR,
										settings.getCASH_ACCOUNT(),
										customer_id, roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getSALES_RETURN_ACCOUNT(),
										roundNumber(netAmt));
							}

							long id = daoObj.returnItems(
									itemStockList,
									purObj,
									trans.getTransaction(
											SConstants.SALES_RETURN,
											CommonUtil
													.getSQLDateFromUtilDate(date
															.getValue())), hash);

							// long id = daoObj.save(purObj,
							// trans.getTransaction(SConstants.SALES_RETURN,CommonUtil.getSQLDateFromUtilDate(date.getValue())));

							saveActivity(
									getOptionId(),
									"New Sales Return. Bill No : "
//											+ purObj.getCredit_note_no()
											+ ", Customer : "
											+ customerSelect
													.getItemCaption(customerSelect
															.getValue())
											+ ", Amount : "
											+ purObj.getAmount(),purObj.getId());

							loadSalesReturn(id);

							daoObj.updateItemStandardCost(itemIdsSet);

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
							// updateRackStock.click();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

//			salesreturnNumberList
//					.addListener(new Property.ValueChangeListener() {
//						public void valueChange(ValueChangeEvent event) {
//
//							try {
//
//								removeAllErrors();
//								responsibleEmployeeCombo.setValue(null);
//								
//								goodStkQtyTextField.setValue("0");
//								stkQtyTextField.setValue("0");
//								wasteQtyTextField.setValue("0");
//								returnQtyTextField.setValue("0");
//								purchRateTextField.setValue("0");
//								supplierSelect.setValue(null);
//
//								if (salesreturnNumberList.getValue() != null
//										&& !salesreturnNumberList.getValue()
//												.toString().equals("0")) {
//
//									SalesReturnModel purObj = daoObj
//											.getSalesReturnModel((Long) salesreturnNumberList
//													.getValue());
//
//									table.setVisibleColumns(allHeaders);
//
//									table.removeAllItems();
//
//									Iterator it = purObj
//											.getInventory_details_list()
//											.iterator();
//									SalesReturnInventoryDetailsModel invObj;
//									double netTotal, purchRat = 0;
//									while (it.hasNext()) {
//										invObj = (SalesReturnInventoryDetailsModel) it
//												.next();
//
//										netTotal = (invObj.getUnit_price() * invObj
//												.getBalance());
//
//										purchRat = 0;
//										if (invObj.getReturned_quantity() > 0) {
//											purchRat = daoObj
//													.getPurchaseReturnRate(
//															purObj.getId(),
//															invObj.getItem()
//																	.getId());
//										}
//
//										double convQty = (invObj
//												.getQuantity_in_basic_unit() / invObj
//												.getBalance());
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
//														invObj.getBalance(),
//														invObj.getUnit()
//																.getId(),
//														invObj.getUnit()
//																.getSymbol(),
//														invObj.getUnit_price(),
//														invObj.getTax().getId(),
//														invObj.getTax_amount(),
//														invObj.getTax_percentage(),
//														invObj.getDiscount_amount(),
//														netTotal,
//														invObj.getOrder_id(),
//														(long) 0,
//														convQty,
//														invObj.getQuantity_in_basic_unit(),
//														roundNumber(invObj
//																.getGood_stock()
//																/ convQty),
//														roundNumber(invObj
//																.getStock_quantity()
//																/ convQty),
//														roundNumber(invObj
//																.getWaste_quantity()
//																/ convQty),
//														roundNumber(invObj
//																.getReturned_quantity()
//																/ convQty),
//														invObj.getOrder_id(),
//														purchRat },
//												table.getItemIds().size() + 1);
//
//									}
//
//									table.setVisibleColumns(requiredHeaders);
//
//									grandTotalAmtTextField
//											.setNewValue(asString(purObj
//													.getAmount()));
//									// buildingSelect.setValue(purObj.getBuilding().getId());
//									comment.setValue(purObj.getComments());
//									date.setValue(purObj.getDate());
//									// expected_delivery_date.setValue(purObj.getExpected_delivery_date());
//									salesBillNoTextField.setValue(purObj
//											.getSales_numbers());
//
//									getHttpSession().setAttribute(
//											"PO_Select_Disabled", 'Y');
//
//									refNoTextField.setValue(purObj.getRef_no());
//
//									customerSelect.setValue(purObj
//											.getCustomer().getId());
//									responsibleEmployeeCombo.setValue(purObj
//											.getResponsible_person());
//
//									// creditPeriodTextField.setValue(asString(purObj.getCredit_period()));
//
//									// shippingChargeTextField.setValue(asString(purObj
//									// .getShipping_charge()));
//									// exciseDutyTextField.setValue(asString(purObj
//									// .getExcise_duty()));
//									payingAmountTextField
//											.setValue(asString(purObj
//													.getPayment_amount()));
//
//									isValid();
//									payingAmountTextField
//											.setComponentError(null);
//									updatePurchaseButton.setVisible(true);
//									// updateRackStock.setVisible(true);
//									deleteReturnButton.setVisible(true);
//									cancelReturnButton.setVisible(true);
//									printButton.setVisible(true);
//									saveSalesReturnButton.setVisible(false);
//
//								} else {
//									table.removeAllItems();
//
//									grandTotalAmtTextField.setNewValue("0.0");
//									payingAmountTextField.setNewValue("0.0");
//									// buildingSelect.setValue(null);
//									comment.setValue("");
//									date.setValue(new Date(getWorkingDate()
//											.getTime()));
//									// expected_delivery_date.setValue(new
//									// Date());
//									salesBillNoTextField.setValue("");
//									customerSelect.setValue(null);
//									responsibleEmployeeCombo.setValue(null);
//									saveSalesReturnButton.setVisible(true);
//									updatePurchaseButton.setVisible(false);
//									updateRackStock.setVisible(false);
//									deleteReturnButton.setVisible(false);
//									cancelReturnButton.setVisible(false);
//									printButton.setVisible(false);
//									refNoTextField.setValue("");
//
//								}
//
//								calculateTotals();
//
//								itemsCompo.setValue(null);
//								itemsCompo.focus();
//								quantityTextField.setValue("0.0");
//								unitPriceTextField.setValue("0.0");
//								netPriceTextField.setNewValue("0.0");
//
//								customerSelect.focus();
//
//								if (!isFinYearBackEntry()) {
//									saveSalesReturnButton.setVisible(false);
//									updatePurchaseButton.setVisible(false);
//									deleteReturnButton.setVisible(false);
//									cancelReturnButton.setVisible(false);
//									printButton.setVisible(false);
//									updateRackStock.setVisible(false);
//									if (salesreturnNumberList.getValue() == null
//											|| salesreturnNumberList.getValue()
//													.toString().equals("0")) {
//										Notification
//												.show(getPropertyName("warning_transaction"),
//														Type.WARNING_MESSAGE);
//									}
//								}
//
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//
//					});

//			updatePurchaseButton.addClickListener(new Button.ClickListener() {
//				@Override
//				public void buttonClick(ClickEvent event) {
//
//					try {
//
//						Set<Long> itemIdsSet = new HashSet<Long>();
//
//						if (isValid()) {
//
//							long customer_id = (Long) customerSelect.getValue();
//
//							SalesReturnModel purObj = daoObj
//									.getSalesReturnModel((Long) salesreturnNumberList
//											.getValue());
//
//							List<SalesReturnInventoryDetailsModel> itemsList = new ArrayList<SalesReturnInventoryDetailsModel>();
//
//							List<PurchaseReturnInventoryDetailsModel> purchaseArrayList;
//
//							ArrayList<SalesReturnInventoryDetailsModel> itemsArrayList = new ArrayList<SalesReturnInventoryDetailsModel>();
//							List<ItemStockModel> itemStockList = new ArrayList<ItemStockModel>();
//							HashMap<TransactionModel, PurchaseReturnModel> hash = new HashMap<TransactionModel, PurchaseReturnModel>();
//
//							ItemStockModel model;
//							SalesReturnInventoryDetailsModel invObj;
//							PurchaseReturnInventoryDetailsModel purchaseInvDetModel;
//							PurchaseReturnModel purchaseReturnModel;
//
//							Item item;
//							long stockId = 0;
//							double conv_rat;
//							Iterator it = table.getItemIds().iterator();
//							while (it.hasNext()) {
//								invObj = new SalesReturnInventoryDetailsModel();
//								item = table.getItem(it.next());
//
//								invObj.setItem(new ItemModel((Long) item
//										.getItemProperty(TBC_ITEM_ID)
//										.getValue()));
//								// invObj.setStock_quantity((Double) item
//								// .getItemProperty(TBC_QTY).getValue());
//								invObj.setBalance((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//
//								if (taxEnable) {
//									invObj.setTax(new TaxModel((Long) item
//											.getItemProperty(TBC_TAX_ID)
//											.getValue()));
//									invObj.setTax_amount((Double) item
//											.getItemProperty(TBC_TAX_AMT)
//											.getValue());
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
//								invObj.setUnit_price((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue());
//
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue());
//								}
//
//								invObj.setOrder_id((Long) item.getItemProperty(
//										TBC_SUPPLIER_ID).getValue());
//
//								// invObj.setManufacturing_date(CommonUtil
//								// .getSQLDateFromUtilDate((Date) item
//								// .getItemProperty(
//								// TBC_MANUFACT_DATE)
//								// .getValue()));
//								// invObj.setExpiry_date(CommonUtil
//								// .getSQLDateFromUtilDate((Date) item
//								// .getItemProperty(
//								// TBC_EXPIRE_DATE)
//								// .getValue()));
//
//								invObj.setId((Long) item.getItemProperty(
//										TBC_INV_ID).getValue());
//
//								conv_rat = (Double) item.getItemProperty(
//										TBC_CONVERTION_QTY).getValue();
//
//								invObj.setGood_stock(CommonUtil
//										.roundNumber(conv_rat
//												* Double.parseDouble(item
//														.getItemProperty(
//																TBC_GOOD_STOCK)
//														.getValue().toString())));
//								invObj.setStock_quantity(CommonUtil
//										.roundNumber(conv_rat
//												* Double.parseDouble(item
//														.getItemProperty(
//																TBC_STOCK_QTY)
//														.getValue().toString())));
//								invObj.setReturned_quantity(CommonUtil.roundNumber(conv_rat
//										* Double.parseDouble(item
//												.getItemProperty(
//														TBC_PURCHASE_RETURN_QTY)
//												.getValue().toString())));
//								invObj.setWaste_quantity(CommonUtil
//										.roundNumber(conv_rat
//												* Double.parseDouble(item
//														.getItemProperty(
//																TBC_WASTE_QTY)
//														.getValue().toString())));
//
//								invObj.setQuantity_in_basic_unit(CommonUtil
//										.roundNumber(conv_rat
//												* invObj.getBalance()));
//
//								itemsList.add(invObj);
//
//								itemIdsSet.add((Long) item.getItemProperty(
//										TBC_ITEM_ID).getValue());
//
//								if (toDouble(item
//										.getItemProperty(TBC_GOOD_STOCK)
//										.getValue().toString()) > 0) {
//									stockId = 0;
//									model = new ItemStockModel();
//									/*
//									 * model.setBalance(Double
//									 * .parseDouble(salesReturnTable .getItem(
//									 * obj) .getItemProperty( TABLE_BALANCE)
//									 * .toString()));
//									 */
//
//									model.setBalance(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_GOOD_STOCK)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setExpiry_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setDate_time(CommonUtil
//											.getCurrentDateTime());
//
//									model.setItem(new ItemModel((Long) item
//											.getItemProperty(TBC_ITEM_ID)
//											.getValue()));
//
//									model.setManufacturing_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setPurchase_id(0);
//									model.setQuantity(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_GOOD_STOCK)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setStatus(SConstants.stock_statuses.GOOD_STOCK);
//									itemStockList.add(model);
//									// stockId =
//									// salesReturnDao
//									// .addNewStock(model);
//									// stockList.add(stockId);
//									/*
//									 * inventoryDetailsModel .
//									 * setStock_id(stockId);
//									 */
//								}
//
//								if (toDouble(item
//										.getItemProperty(TBC_STOCK_QTY)
//										.getValue().toString()) > 0) {
//									stockId = 0;
//									model = new ItemStockModel();
//									/*
//									 * model.setBalance(Double
//									 * .parseDouble(salesReturnTable .getItem(
//									 * obj) .getItemProperty( TABLE_BALANCE)
//									 * .toString()));
//									 */
//
//									model.setBalance(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_STOCK_QTY)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setExpiry_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setDate_time(CommonUtil
//											.getCurrentDateTime());
//
//									model.setItem(new ItemModel((Long) item
//											.getItemProperty(TBC_ITEM_ID)
//											.getValue()));
//
//									model.setManufacturing_date(CommonUtil
//											.getCurrentSQLDate());
//
//									model.setPurchase_id(0);
//									model.setQuantity(CommonUtil
//											.roundNumber(toDouble(item
//													.getItemProperty(
//															TBC_STOCK_QTY)
//													.getValue().toString())
//													* conv_rat));
//
//									model.setStatus(STATUS_RETURNED);
//									itemStockList.add(model);
//									// stockId =
//									// salesReturnDao
//									// .addNewStock(model);
//									// stockList.add(stockId);
//									/*
//									 * inventoryDetailsModel .
//									 * setStock_id(stockId);
//									 */
//								}
//
//								// Purchase
//								// Return=====================================================================
//								if (toDouble(item
//										.getItemProperty(
//												TBC_PURCHASE_RETURN_QTY)
//										.getValue().toString()) > 0) {
//
//									purchaseArrayList = new ArrayList<PurchaseReturnInventoryDetailsModel>();
//									purchaseReturnModel = new PurchaseReturnModel();
//									purchaseInvDetModel = new PurchaseReturnInventoryDetailsModel();
//
//									purchaseInvDetModel.setItem(new ItemModel(
//											(Long) item.getItemProperty(
//													TBC_ITEM_ID).getValue()));
//
//									purchaseInvDetModel.setQunatity(CommonUtil
//											.roundNumber((Double) item
//													.getItemProperty(
//															TBC_PURCHASE_RETURN_QTY)
//													.getValue()));
//									purchaseInvDetModel.setQty_in_basic_unit(CommonUtil
//											.roundNumber(conv_rat
//													* (Double) item
//															.getItemProperty(
//																	TBC_PURCHASE_RETURN_QTY)
//															.getValue()));
//
//									purchaseInvDetModel.setUnit(new UnitModel(
//											(Long) item.getItemProperty(
//													TBC_UNIT_ID).getValue()));
//
//									double purchasePrice = (Double) item
//											.getItemProperty(TBC_PURCHASE_RATE)
//											.getValue();
//
//									purchaseInvDetModel
//											.setUnit_price(purchasePrice);
//
//									purchaseInvDetModel.setTax(new TaxModel(
//											(Long) item.getItemProperty(
//													TBC_TAX_ID).getValue()));
//									purchaseInvDetModel
//											.setTax_amount((Double) item
//													.getItemProperty(
//															TBC_TAX_AMT)
//													.getValue());
//									purchaseInvDetModel
//											.setTax_percentage((Double) item
//													.getItemProperty(
//															TBC_TAX_PERC)
//													.getValue());
//
//									purchaseInvDetModel
//											.setBalance((Double) item
//													.getItemProperty(
//															TBC_PURCHASE_RETURN_QTY)
//													.getValue());
//
//									purchaseInvDetModel.setDiscount_amount(0);
//									purchaseInvDetModel
//											.setManufacturing_date(CommonUtil
//													.getCurrentSQLDate());
//									purchaseInvDetModel
//											.setExpiry_date(CommonUtil
//													.getCurrentSQLDate());
//									purchaseInvDetModel.setOrder_id((long) 0);
//
//									purchaseArrayList.add(purchaseInvDetModel);
//
//									purchaseReturnModel
//											.setLogin_id(new S_LoginModel(
//													getLoginID()));
//									purchaseReturnModel.setRefNo(refNoTextField
//											.getValue());
//									purchaseReturnModel
//											.setOffice(new S_OfficeModel(
//													getOfficeID()));
//									purchaseReturnModel.setAmount(CommonUtil
//											.roundNumber(purchasePrice
//													* toDouble(item
//															.getItemProperty(
//																	TBC_PURCHASE_RETURN_QTY)
//															.getValue()
//															.toString())));
//									purchaseReturnModel.setStatus(2);
//									purchaseReturnModel.setComments("");
//									purchaseReturnModel
//											.setSupplier(new LedgerModel(
//													(Long) item
//															.getItemProperty(
//																	TBC_SUPPLIER_ID)
//															.getValue()));
//									purchaseReturnModel.setDate(CommonUtil
//											.getSQLDateFromUtilDate(date
//													.getValue()));
//									purchaseReturnModel
//											.setInventory_details_list(purchaseArrayList);
//									purchaseReturnModel
//											.setDebit_note_no(getNextSequence(
//													"Debit Note Number",
//													getLoginID()));
//									purchaseReturnModel.setPayment_amount(0);
//									purchaseReturnModel.setActive(true);
//
//									// Transaction Starts
//									// Here
//
//									FinTransaction purfinTransaction = new FinTransaction();
//
//									double amount = roundNumber(purchasePrice
//											* toDouble(item
//													.getItemProperty(
//															TBC_PURCHASE_RETURN_QTY)
//													.getValue().toString()));
//
//									purfinTransaction.addTransaction(
//											SConstants.CR,
//											settings.getPURCHASE_RETURN_ACCOUNT(),
//											(Long) item.getItemProperty(
//													TBC_SUPPLIER_ID).getValue(),
//											amount);
//
//									hash.put(
//											purfinTransaction
//													.getTransaction(
//															SConstants.PURCHASE_RETURN,
//															CommonUtil
//																	.getSQLDateFromUtilDate(date
//																			.getValue())),
//											purchaseReturnModel);
//
//								}
//
//								/*
//								 * if (purObj.getPayment_amount() == purObj
//								 * .getAmount()) {
//								 * cashOrCreditRadio.setValue((long) 1); } else
//								 * { cashOrCreditRadio.setValue((long) 2); }
//								 */
//
//							}
//
//							/*
//							 * if (isExciceDutyEnable()) {
//							 * purObj.setExcise_duty(
//							 * toDouble(exciseDutyTextField .getValue())); } if
//							 * (isShippingChargeEnable()) {
//							 * purObj.setShipping_charge
//							 * (toDouble(shippingChargeTextField .getValue()));
//							 * }
//							 */
//							purObj.setPayment_amount(toDouble(payingAmountTextField
//									.getValue()));
//							// purObj.setCredit_period(toInt(creditPeriodTextField.getValue()));
//							purObj.setAmount(toDouble(grandTotalAmtTextField
//									.getValue()));
//							// purObj.setBuilding(new BuildingModel((Long)
//							// buildingSelect.getValue()));
//							purObj.setComments(comment.getValue());
//							purObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							// purObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
//							purObj.setLogin(new S_LoginModel(getLoginID()));
//							purObj.setOffice(new S_OfficeModel(getOfficeID()));
//							purObj.setSales_numbers(salesBillNoTextField
//									.getValue());
//							purObj.setRef_no(refNoTextField.getValue());
//							purObj.setStatus(1);
//							purObj.setCustomer(new LedgerModel(
//									(Long) customerSelect.getValue()));
//							purObj.setInventory_details_list(itemsList);
//							purObj.setActive(true);
//							purObj.setResponsible_person((Long) responsibleEmployeeCombo
//									.getValue());
//
//							// purObj.setCredit_note_no(getNextSequence(
//							// "Credit Note Number", getLoginID()));
//
//							FinTransaction trans = new FinTransaction();
//							double totalAmt = toDouble(grandTotalAmtTextField
//									.getValue());
//							double netAmt = totalAmt;
//
//							double amt = 0;
//
//							double payingAmt = toDouble(payingAmountTextField
//									.getValue());
//
//							if (payingAmt == netAmt) {
//								trans.addTransaction(SConstants.CR,
//										settings.getCASH_ACCOUNT(),
//										customer_id, roundNumber(payingAmt));
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getSALES_RETURN_ACCOUNT(),
//										roundNumber(netAmt));
//							} else if (payingAmt == 0) {
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getSALES_RETURN_ACCOUNT(),
//										roundNumber(netAmt));
//							} else {
//								trans.addTransaction(SConstants.CR,
//										settings.getCASH_ACCOUNT(),
//										customer_id, roundNumber(payingAmt));
//								trans.addTransaction(SConstants.CR,
//										customer_id,
//										settings.getSALES_RETURN_ACCOUNT(),
//										roundNumber(netAmt));
//							}
//
//							long id = daoObj.updateReturn(
//									itemStockList,
//									purObj,
//									trans.getTransactionWithoutID(
//											SConstants.SALES_RETURN,
//											CommonUtil
//													.getSQLDateFromUtilDate(date
//															.getValue())),
//									hash, purObj.getId());
//
//							// stockList.addAll(itemsList);
//
//							// long id = daoObj.save(purObj,
//							// trans.getTransaction(SConstants.SALES_RETURN,CommonUtil.getSQLDateFromUtilDate(date.getValue())));
//
//							saveActivity(
//									getOptionId(),
//									"Update Sales Return. Bill No : "
//											+ purObj.getCredit_note_no()
//											+ ", Customer : "
//											+ customerSelect
//													.getItemCaption(customerSelect
//															.getValue())
//											+ ", Amount : "
//											+ purObj.getAmount(),purObj.getId());
//
//							loadSalesReturn(id);
//
//							Notification.show(getPropertyName("save_success"),
//									Type.WARNING_MESSAGE);
//							// updateRackStock.click();
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//			});

			updateRackStock.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {
						rackStockWindow.setContent(new StockRackMappingPannel(
								(Long) salesreturnNumberList.getValue()));
						getUI().getCurrent().addWindow(rackStockWindow);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			deleteReturnButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesreturnNumberList.getValue() != null
							&& !salesreturnNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) salesreturnNumberList
														.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"Sales Return Deleted. Bill No : "
																+ salesreturnNumberList
																		.getItemCaption(salesreturnNumberList
																				.getValue())
																+ ", Customer : "
																+ customerSelect
																		.getItemCaption(customerSelect
																				.getValue())
																+ ", Amount : "
																+ table.getColumnFooter(TBC_NET_PRICE),(Long)salesreturnNumberList
																.getValue());

												loadSalesReturn(0);

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}

				}
			});

			cancelReturnButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesreturnNumberList.getValue() != null
							&& !salesreturnNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.cancelReturn((Long) salesreturnNumberList
														.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"New Sales Return. Bill No : "
																+ salesreturnNumberList
																		.getItemCaption(salesreturnNumberList
																				.getValue())
																+ ", Customer : "
																+ customerSelect
																		.getItemCaption(customerSelect
																				.getValue())
																+ ", Amount : "
																+ table.getColumnFooter(TBC_NET_PRICE),(Long)salesreturnNumberList
																.getValue());

												loadSalesReturn(0);

											} catch (Exception e) {
												e.printStackTrace();
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

					supplierSelect.setValue(null);

					itemsCompo.setReadOnly(false);
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

						itemsCompo.setValue(item.getItemProperty(TBC_ITEM_ID)
								.getValue());
						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());
						unitPriceTextField.setValue(""
								+ item.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

						convertionQtyTextField.setNewValue(""
								+ item.getItemProperty(TBC_CONVERTION_QTY)
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

						stkQtyTextField.setValue(""
								+ item.getItemProperty(TBC_STOCK_QTY)
										.getValue());
						goodStkQtyTextField.setValue(""
								+ item.getItemProperty(TBC_GOOD_STOCK)
										.getValue());
						wasteQtyTextField.setValue(""
								+ item.getItemProperty(TBC_WASTE_QTY)
										.getValue());
						returnQtyTextField.setValue(""
								+ item.getItemProperty(TBC_PURCHASE_RETURN_QTY)
										.getValue());
						purchRateTextField.setValue(""
								+ item.getItemProperty(TBC_PURCHASE_RATE)
										.getValue());
						supplierSelect.setValue(item.getItemProperty(
								TBC_SUPPLIER_ID).getValue());

						// manufacturing_date.setValue((Date) item
						// .getItemProperty(TBC_MANUFACT_DATE).getValue());
						// expiry_date.setValue((Date) item.getItemProperty(
						// TBC_EXPIRE_DATE).getValue());
						visibleAddupdatePurchaseButton(false, true);

						itemsCompo.focus();

						/*
						 * if ((Long) item.getItemProperty(TBC_PO_ID).getValue()
						 * > 0) { itemsCompo.setReadOnly(true);
						 * unitSelect.setReadOnly(true);
						 * quantityTextField.focus(); }
						 */

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

					} else {
						itemsCompo.setValue(null);
						itemsCompo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");
						convertionQtyTextField.setValue("1");

						goodStkQtyTextField.setValue("0");
						stkQtyTextField.setValue("0");
						wasteQtyTextField.setValue("0");
						returnQtyTextField.setValue("0");
						purchRateTextField.setValue("0");
						supplierSelect.setValue(null);

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

							double wasteQty = toDouble(wasteQtyTextField
									.getValue()), stkQty = toDouble(stkQtyTextField
									.getValue()), goodStkQty = toDouble(goodStkQtyTextField
									.getValue()), retnQty = toDouble(returnQtyTextField
									.getValue()), purRate = toDouble(purchRateTextField
									.getValue());

							long supId = 0;

							if (retnQty > 0) {
								if (supplierSelect.getValue() != null) {
									supId = (Long) supplierSelect.getValue();
								}
							}

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

							double conv_rat = toDouble(convertionQtyTextField
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
											objTax.getId(), tax_amt, tax_perc,
											discount_amt, totalAmt, (long) 0,
											(long) 0, conv_rat, conv_rat * qty,
											goodStkQty, stkQty, wasteQty,
											retnQty, supId, purRate }, id);

							table.setVisibleColumns(requiredHeaders);

							itemsCompo.setValue(null);
							quantityTextField.setValue("0");
							unitPriceTextField.setValue("0");
							netPriceTextField.setNewValue("0");
							discountTextField.setValue("0");

							goodStkQtyTextField.setValue("0");
							stkQtyTextField.setValue("0");
							wasteQtyTextField.setValue("0");
							returnQtyTextField.setValue("0");
							supplierSelect.setValue(null);

							calculateTotals();

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

							double wasteQty = toDouble(wasteQtyTextField
									.getValue()), stkQty = toDouble(stkQtyTextField
									.getValue()), goodStkQty = toDouble(goodStkQtyTextField
									.getValue()), retnQty = toDouble(returnQtyTextField
									.getValue()), purRate = toDouble(purchRateTextField
									.getValue());
							long supId = 0;

							if (retnQty > 0) {
								if (supplierSelect.getValue() != null) {
									supId = (Long) supplierSelect.getValue();
								}
							}

							item.getItemProperty(TBC_GOOD_STOCK).setValue(
									goodStkQty);
							item.getItemProperty(TBC_STOCK_QTY)
									.setValue(stkQty);
							item.getItemProperty(TBC_WASTE_QTY).setValue(
									wasteQty);
							item.getItemProperty(TBC_PURCHASE_RETURN_QTY)
									.setValue(retnQty);
							item.getItemProperty(TBC_SUPPLIER_ID).setValue(
									supId);
							item.getItemProperty(TBC_PURCHASE_RATE).setValue(
									purRate);

							item.getItemProperty(TBC_CONVERTION_QTY)
									.setValue(
											toDouble(convertionQtyTextField
													.getValue()));

							double conv_rat = toDouble(convertionQtyTextField
									.getValue());

							item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
									.setValue(conv_rat * qty);

							// item.getItemProperty(TBC_MANUFACT_DATE).setValue(
							// manufacturing_date.getValue());
							// item.getItemProperty(TBC_EXPIRE_DATE).setValue(
							// expiry_date.getValue());

							table.setVisibleColumns(requiredHeaders);

							// itemsCompo.setNewValue(null);
							// itemsCompo.focus();
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");

							goodStkQtyTextField.setValue("0");
							stkQtyTextField.setValue("0");
							wasteQtyTextField.setValue("0");
							returnQtyTextField.setValue("0");
							purchRateTextField.setValue("0");
							supplierSelect.setValue(null);

							visibleAddupdatePurchaseButton(true, false);

							itemsCompo.focus();

							table.setValue(null);

							calculateTotals();

							// payingAmountTextField
							// .setValue(grandTotalAmtTextField.getValue());

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			itemsCompo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemsCompo.getValue() != null) {
							ItemModel itm = itemDao.getItem((Long) itemsCompo
									.getValue());

							SCollectionContainer bic = SCollectionContainer.setList(
									untDao.getAllActiveUnits(getOrganizationID()),
									"id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							if (taxEnable) {
								taxSelect
										.setValue(itm.getPurchaseTax().getId());
							}
							unitSelect.setValue(itm.getUnit().getId());

							goodStkQtyTextField.selectAll();
							goodStkQtyTextField.focus();

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			unitSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {
							if (itemsCompo.getValue() != null) {

								ItemModel itm = daoObj
										.getItem((Long) itemsCompo.getValue());

								if (((Long) unitSelect.getValue()) == itm
										.getUnit().getId()) {
									convertionQtyTextField.setValue("1");
									convertionQtyTextField.setVisible(false);
									convertedQtyTextField.setVisible(false);
								} else {
									convertionQtyTextField.setVisible(true);
									convertedQtyTextField.setVisible(true);

									// convertionQtyTextField.setCaption("Qty - "+itm.getUnit().getSymbol());
									convertedQtyTextField.setCaption("Qty - "
											+ itm.getUnit().getSymbol());

									double cnvr_qty = comDao.getConvertionRate(
											itm.getId(),
											(Long) unitSelect.getValue(), 0);

									convertionQtyTextField
											.setValue(asString(cnvr_qty));

								}

								unitPriceTextField.setValue(asString(comDao.getItemPrice(
										itm.getId(),
										(Long) unitSelect.getValue(), 0)));

								if (quantityTextField.getValue() != null
										&& !quantityTextField.getValue()
												.equals("")) {

									convertedQtyTextField.setNewValue(asString(Double
											.parseDouble(quantityTextField
													.getValue())
											* Double.parseDouble(convertionQtyTextField
													.getValue())));

									netPriceTextField.setNewValue(asString(Double
											.parseDouble(unitPriceTextField
													.getValue())
											* Double.parseDouble(quantityTextField
													.getValue())));
								}

							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

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

			convertionQtyTextField.setImmediate(true);

			convertionQtyTextField
					.addListener(new Property.ValueChangeListener() {
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

			goodStkQtyTextField.setImmediate(true);
			stkQtyTextField.setImmediate(true);
			returnQtyTextField.setImmediate(true);
			wasteQtyTextField.setImmediate(true);

			goodStkQtyTextField.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						quantityTextField.setValue(asString(toDouble(returnQtyTextField
								.getValue())
								+ toDouble(wasteQtyTextField.getValue())
								+ toDouble(stkQtyTextField.getValue())
								+ toDouble(goodStkQtyTextField.getValue())));
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});

			stkQtyTextField.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						quantityTextField.setValue(asString(toDouble(returnQtyTextField
								.getValue())
								+ toDouble(wasteQtyTextField.getValue())
								+ toDouble(stkQtyTextField.getValue())
								+ toDouble(goodStkQtyTextField.getValue())));
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});
			returnQtyTextField.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						quantityTextField.setValue(asString(toDouble(returnQtyTextField
								.getValue())
								+ toDouble(wasteQtyTextField.getValue())
								+ toDouble(stkQtyTextField.getValue())
								+ toDouble(goodStkQtyTextField.getValue())));
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});
			wasteQtyTextField.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						quantityTextField.setValue(asString(toDouble(returnQtyTextField
								.getValue())
								+ toDouble(wasteQtyTextField.getValue())
								+ toDouble(stkQtyTextField.getValue())
								+ toDouble(goodStkQtyTextField.getValue())));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});

			if (!isFinYearBackEntry()) {
				saveSalesReturnButton.setVisible(false);
				updatePurchaseButton.setVisible(false);
				deleteReturnButton.setVisible(false);
				cancelReturnButton.setVisible(false);
				printButton.setVisible(false);
				updateRackStock.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

//			printButton.addClickListener(new ClickListener() {
//
//				@Override
//				public void buttonClick(ClickEvent event) {
//					try {
//						SalesReturnModel purObj = daoObj
//								.getSalesReturnModel((Long) salesreturnNumberList
//										.getValue());
//
//						CreditNoteBean creditNoteBean;
//						ArrayList creditNoteList = new ArrayList<Object>();
//						Iterator<SalesReturnInventoryDetailsModel> iter = purObj
//								.getInventory_details_list().iterator();
//						SalesReturnInventoryDetailsModel invObj;
//						while (iter.hasNext()) {
//							invObj = iter.next();
//
//							creditNoteBean = new CreditNoteBean(invObj
//									.getItem().getItem_code(), invObj.getItem()
//									.getName(), invObj.getUnit().getSymbol(),
//									invObj.getUnit_price(),
//									invObj.getBalance(), invObj.getBalance()
//											* invObj.getUnit_price(), invObj
//											.getTax_amount(), invObj
//											.getBalance()
//											* invObj.getUnit_price());
//							creditNoteList.add(creditNoteBean);
//
//						}
//						map = new HashMap<String, Object>();
//
//						String address = "";
//
//						LedgerModel customerModel = purObj.getCustomer();
//						if (customerModel.getAddress() != null) {
//							address = new AddressDao()
//									.getAddressString(customerModel
//											.getAddress().getId());
//						}
//
//						map.put("CUSTOMER_NAME", customerModel.getName());
//						map.put("CUSTOMER_ADDRESS", address);
//						map.put("DATE", CommonUtil
//								.getUtilDateFromSQLDate(purObj.getDate()));
//						map.put("CREDIT_NOTE_NO", salesreturnNumberList
//								.getItemCaption(salesreturnNumberList
//										.getValue()));
//						map.put("SHIPPING", 0);
//						map.put("AMOUNT_IN_WORDS",
//								getAmountInWords(roundNumber(purObj.getAmount())));
//						map.put("LOGO_PATH",
//								VaadinServlet.getCurrent().getServletContext()
//										.getRealPath("/")
//										+ "VAADIN/themes/testappstheme/OrganizationLogos/"
//										+ getOrganizationID() + ".png");
//
//						Report report = new Report(getLoginID());
//
//						report.setJrxmlFileName(getBillName(SConstants.bills.SALES_RETURN));
//						report.setReportFileName("CreditNote");
//						report.setReportType(Report.PDF);
//						report.createReport(creditNoteList, map);
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//			});

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return pannel;
	}

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc;

		try {
			quantityTextField.setValue(asString(toDouble(returnQtyTextField
					.getValue())
					+ toDouble(wasteQtyTextField.getValue())
					+ toDouble(stkQtyTextField.getValue())
					+ toDouble(goodStkQtyTextField.getValue())));

			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
			disc = Double.parseDouble(discountTextField.getValue());

			convertedQtyTextField.setNewValue(asString(qty
					* Double.parseDouble(convertionQtyTextField.getValue())));

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

			if (goodStkQtyTextField.getValue() == null
					|| goodStkQtyTextField.getValue().equals("")) {
				setRequiredError(goodStkQtyTextField,
						getPropertyName("invalid_data"), true);
				goodStkQtyTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(goodStkQtyTextField.getValue()) < 0) {
						setRequiredError(goodStkQtyTextField,
								getPropertyName("invalid_data"), true);
						goodStkQtyTextField.focus();
						ret = false;
					} else
						setRequiredError(goodStkQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(goodStkQtyTextField,
							getPropertyName("invalid_data"), true);
					goodStkQtyTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (stkQtyTextField.getValue() == null
					|| stkQtyTextField.getValue().equals("")) {
				setRequiredError(stkQtyTextField,
						getPropertyName("invalid_data"), true);
				stkQtyTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(stkQtyTextField.getValue()) < 0) {
						setRequiredError(stkQtyTextField,
								getPropertyName("invalid_data"), true);
						stkQtyTextField.focus();
						ret = false;
					} else
						setRequiredError(stkQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(stkQtyTextField,
							getPropertyName("invalid_data"), true);
					stkQtyTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (wasteQtyTextField.getValue() == null
					|| wasteQtyTextField.getValue().equals("")) {
				setRequiredError(wasteQtyTextField,
						getPropertyName("invalid_data"), true);
				wasteQtyTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(wasteQtyTextField.getValue()) < 0) {
						setRequiredError(wasteQtyTextField,
								getPropertyName("invalid_data"), true);
						wasteQtyTextField.focus();
						ret = false;
					} else
						setRequiredError(wasteQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(wasteQtyTextField,
							getPropertyName("invalid_data"), true);
					wasteQtyTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (purchRateTextField.getValue() == null
					|| purchRateTextField.getValue().equals("")) {
				purchRateTextField.setValue("0");
			}

			if (returnQtyTextField.getValue() == null
					|| returnQtyTextField.getValue().equals("")) {
				setRequiredError(returnQtyTextField,
						getPropertyName("invalid_data"), true);
				returnQtyTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(returnQtyTextField.getValue()) < 0) {
						setRequiredError(returnQtyTextField,
								getPropertyName("invalid_data"), true);
						returnQtyTextField.focus();
						ret = false;
					} else {
						if (toDouble(returnQtyTextField.getValue()) > 0) {

							try {
								if (toDouble(purchRateTextField.getValue()) <= 0) {
									setRequiredError(purchRateTextField,
											getPropertyName("invalid_data"),
											true);
									purchRateTextField.focus();
									ret = false;
								} else
									setRequiredError(purchRateTextField, null,
											false);

							} catch (Exception e) {
								setRequiredError(supplierSelect,
										getPropertyName("invalid_data"), true);
								supplierSelect.focus();
								ret = false;
								// TODO: handle exception
							}

							if (supplierSelect.getValue() == null
									|| supplierSelect.getValue().equals("")) {
								setRequiredError(supplierSelect,
										getPropertyName("invalid_selection"),
										true);
								supplierSelect.focus();
								ret = false;
							} else
								setRequiredError(supplierSelect, null, false);
						} else
							setRequiredError(returnQtyTextField, null, false);
					}

				} catch (Exception e) {
					setRequiredError(returnQtyTextField,
							getPropertyName("invalid_data"), true);
					returnQtyTextField.focus();
					ret = false;
					// TODO: handle exception
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

			if (ret) {

				if ((toDouble(returnQtyTextField.getValue())
						+ toDouble(wasteQtyTextField.getValue())
						+ toDouble(stkQtyTextField.getValue()) + toDouble(goodStkQtyTextField
							.getValue())) != toDouble(quantityTextField
						.getValue())) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					ret = false;
				} else {
					setRequiredError(quantityTextField, null, false);
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
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
			itemsCompo.focus();

			if (table.getItemIds().size() <= 0)
				PONoList.clear();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadSalesReturn(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new SalesReturnModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllCreditNotesAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			salesreturnNumberList.setContainerDataSource(bic);
			salesreturnNumberList.setItemCaptionPropertyId("comments");

			salesreturnNumberList.setValue(id);

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

	public void reloadCustomers() {
		List list;
		try {
			list = custDao.getAllActiveCustomerNamesWithLedgerID(getOfficeID());
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			customerSelect.setContainerDataSource(bic);
			customerSelect.setItemCaptionPropertyId("name");

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
		setRequiredError(salesBillNoTextField, null, false);

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
		
		if (responsibleEmployeeCombo.getValue() == null
				|| responsibleEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsibleEmployeeCombo,
					getPropertyName("invalid_selection"), true);
			responsibleEmployeeCombo.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployeeCombo, null, false);
		

		if (shippingChargeTextField.getValue() == null
				|| shippingChargeTextField.getValue().equals("")) {
			setRequiredError(shippingChargeTextField, "Enter Shipping charge",
					true);
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
		if (refNoTextField.getValue() == null
				|| refNoTextField.getValue().equals("")) {
			setRequiredError(refNoTextField, getPropertyName("invalid_data"),
					true);
			refNoTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(refNoTextField.getValue()) < 0) {
					setRequiredError(refNoTextField,
							getPropertyName("invalid_data"), true);
					refNoTextField.focus();
					ret = false;
				} else
					setRequiredError(refNoTextField, null, false);
			} catch (Exception e) {
				setRequiredError(refNoTextField,
						getPropertyName("invalid_data"), true);
				refNoTextField.focus();
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
				// TODO: handle exception
			}
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			itemsCompo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		/*
		 * if(buildingSelect.getValue()==null ||
		 * buildingSelect.getValue().equals("")){ setRequiredError(
		 * buildingSelect, "Select a building",true); buildingSelect.focus();
		 * ret=false; } else setRequiredError(buildingSelect , null,false);
		 */

		if (customerSelect.getValue() == null
				|| customerSelect.getValue().equals("")) {
			setRequiredError(customerSelect,
					getPropertyName("invalid_selection"), true);
			customerSelect.focus();
			ret = false;
		} else
			setRequiredError(customerSelect, null, false);

		/*
		 * if(expected_delivery_date.getValue()==null ||
		 * expected_delivery_date.getValue().equals("")){
		 * setRequiredError(expected_delivery_date
		 * ,getPropertyName("invalid_selection"),true);
		 * expected_delivery_date.focus(); ret=false; } else
		 * setRequiredError(expected_delivery_date, null,false);
		 */

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		/*
		 * if (salesBillNoTextField.getValue() == null ||
		 * salesBillNoTextField.getValue().equals("")) {
		 * setRequiredError(salesBillNoTextField, "Enter Purchase Bill No.",
		 * true); salesBillNoTextField.focus(); ret = false; } else
		 * salesBillNoTextField.setComponentError(null);
		 */

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
		if (supplierSelect.getComponentError() != null)
			setRequiredError(supplierSelect, null, false);
		if (purchRateTextField.getComponentError() != null)
			setRequiredError(purchRateTextField, null, false);

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

	public SComboField getSalesreturnNumberList() {
		return salesreturnNumberList;
	}

	public void setSalesreturnNumberList(SComboField salesreturnNumberList) {
		this.salesreturnNumberList = salesreturnNumberList;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return salesreturnNumberList;
	}

}
