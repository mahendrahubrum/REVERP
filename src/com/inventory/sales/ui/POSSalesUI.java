package com.inventory.sales.ui;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.SetUnitManagementUI;
import com.inventory.dao.PrivilageSetupDao;
import com.inventory.finance.dao.PaymentModeDao;
import com.inventory.finance.model.PaymentModeModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.bean.SalesInventoryDetailsPojo;
import com.inventory.sales.dao.HoldSalesDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.HoldSalesInventoryDetailsModel;
import com.inventory.sales.model.HoldSalesModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesPaymentModeDetailsModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SKeyPad;
import com.webspark.Components.SLabel;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
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
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Feb 24, 2014
 */


public class POSSalesUI extends SparkLogic {

	private static final long serialVersionUID = -5415935778881929746L;

	private static final int ACTIVE = 0;

	static String TBP_SN = "SN";
	static String TBP_PAYMENT_MODE_ID = "Payment Mode Id";
	static String TBP_PAYMENT_MODE = "Payment Mode";
	static String TBP_PAYMENT_AMOUNT_FIELD = "Amount";

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "Tax %";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_DISCOUNT_TYPE = "Discount Type";
	static String TBC_DISCOUNT_PERCENTAGE = "Discount %";
	static String TBC_DISCOUNT = "Discount Amount";
	static String TBC_PO_ID = "PO ID";
	static String TBC_INV_ID = "INV ID";
	static String TBC_NET_TOTAL = "Net Total";
	static String TBC_NET_FINAL = "Final Amt";
	static String TBC_STOCK_ID = "Stock ID";
	static String TBC_CONVERTION_QTY = "Convertion Qty";
	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";
	static String TBC_BILL_TYPE_ID = "Bill Type Id";
	// static String TBC_PAYMENT_MODE_ID = "Payment Mode Id";
	// static String TBC_PAYMENT_AMOUNT = "Payment Amount";

	SalesDao daoObj;
	CommonMethodsDao comDao;
	SComboField salesNumberList;

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
	// STextField discountTextField;
	STextField netPriceTextField;

	STextField payingAmountTextField;
	STextField creditPeriodTextField;

	SRadioButton discountRadio;
	STextField discountPercentField;
	STextField discountAmountField;

	SRadioButton itemDiscountRadio;
	STextField itemDiscountPercentField;
	STextField itemDiscountAmountField;

	SButton addItemButton;
	SButton newEntryButton;
	SButton updateItemButton;
	SButton deleteItemButton;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;

	SDialogBox poWindow;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SComboField customerSelect;
	SComboField itemComboField;
	SDateField date;
	SDateField expiry_date;
	SDateField manufaturing_date;
	SListSelect itemSelectCombo;
	SListSelect categorySelectList;
	SListSelect stockSelectList;

	SComboField employSelect;
	STextField grandTotalAmtTextField;
	STextArea comment;

	SettingsValuePojo settings;
	WrappedSession session;

	boolean taxEnable = isTaxEnable();
	private Object[] allHeaders;
	private Object[] requiredHeaders;

	private SButton printButton;
	long status;

	private SButton newCustomerButton;
	private SButton newItemButton;
	private SButton newUnitButton;
	private SButton unitMapButton;
	private SButton changeStkButton;

	private SDialogBox newCustomerWindow;
	private SDialogBox newItemWindow;
	private SalesCustomerPanel salesCustomerPanel;
	private ItemPanel itemPanel;

	SNativeSelect salesTypeSelect;

	SWindow popupWindow;

	SHorizontalLayout popupHor;
	SButton priceListButton;

	SHorizontalLayout hrz1;

	SButton newSaleButton;

	UserManagementDao usrDao;
	CustomerDao custDao;
	TaxDao taxDao;
	ItemDao itmDao;
	SalesOrderDao soDao;
	HoldSalesDao holdSalesDao;

	private STextField refNoField;

	private STextField barcodeField;

	private SKeyPad keyBoard;
	SFormLayout keyboardLay;
	SPopupView popKeyboad;

	private int focusType = 1;

	private STextField keyboardTextField;
	Component focusedComponent;

	private SPopupView itemPopupView;

	private SCheckBox enterBarcodeButton;
	private SButton selectItemButton;

	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	SRadioButton cashChequeRadio;

	private PaymentModeDao paymentModeDao;
	private SComboField paymentModeCombo;
	private SCurrencyField paymentAmountTextField;
	private STextField balanceAmountTextField;
	private SRadioButton billTypeField;
	private SButton addPaymentModeButton;
	private STable paymentAmountTable;
	private SPopupView paymentAmountPopup;
	private ValueChangeListener paymentAmountValueChangeListner;
	private HashMap<Long, STextField> paymentModeHashMap;
	private List<Long> paymentModePrintOrderArrayList;

	private SButton holdButton;
	private SButton recallButton;
	private boolean isHold=false;
	SHorizontalLayout popupContainer;
	LayoutClickListener layoutListener;
	long holdSalesId=0;

	@SuppressWarnings({ "serial", "unchecked" })
	@Override
	public SPanel getGUI() {

		windowNotif = new WindowNotifications();
		helpPopup = new SHelpPopupView("");
		popupLay = new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox = new SConfirmWithCommonds("Confirm..?", getOfficeID());

		setWindowMode(WindowMode.MAXIMIZED);

		comDao = new CommonMethodsDao();
		custDao = new CustomerDao();
		soDao = new SalesOrderDao();
		taxDao = new TaxDao();
		itmDao = new ItemDao();
		usrDao = new UserManagementDao();
		taxEnable = isTaxEnable();
		paymentModeDao = new PaymentModeDao();
		holdSalesDao=new HoldSalesDao();

		popupWindow = new SWindow();

		focusType = 1;

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Sale");

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_NAME,
				TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE, TBC_TAX_ID,
				TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT_TYPE,
				TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT, TBC_NET_PRICE,
				TBC_PO_ID, TBC_INV_ID, TBC_NET_TOTAL, TBC_NET_FINAL,
				TBC_QTY_IN_BASIC_UNI, TBC_STOCK_ID, TBC_CONVERTION_QTY,
				TBC_BILL_TYPE_ID };

		if (taxEnable) {
			if (isCessEnable()) {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_NAME,
						TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY, TBC_NET_PRICE,
						TBC_TAX_PERC, TBC_TAX_AMT, TBC_NET_TOTAL,
						TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT, TBC_NET_FINAL };
			} else {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_NAME,
						TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY, TBC_NET_PRICE,
						TBC_TAX_PERC, TBC_TAX_AMT, TBC_NET_TOTAL,
						TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT, TBC_NET_FINAL };
			}
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_NAME, TBC_UNIT,
					TBC_UNIT_PRICE, TBC_QTY, TBC_NET_PRICE,
					TBC_DISCOUNT_PERCENTAGE, TBC_DISCOUNT, TBC_NET_FINAL };
		}

		List<Object> templist = new ArrayList<Object>();
		Collections.addAll(templist, requiredHeaders);

		/*
		 * if (!isManufDateEnable()) { templist.remove(TBC_MANUFACT_DATE);
		 * templist.remove(TBC_EXPIRE_DATE); }
		 */
		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
			templist.remove(TBC_DISCOUNT_PERCENTAGE);
			templist.remove(TBC_NET_FINAL);
		}

		requiredHeaders = templist.toArray(new String[templist.size()]);

		setSize(1300, 605);

		session = getHttpSession();

		daoObj = new SalesDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		// isPaymentNow=new SCheckBox("Receiving Cash", true);

		payingAmountTextField = new STextField(getPropertyName("total_amount"),
				100);
		payingAmountTextField.setNewValue("0.00");
		payingAmountTextField.setStyleName("sup_textfield_align_right");
		payingAmountTextField.setId("Paying Amt");
		payingAmountTextField.setReadOnly(true);
		payingAmountTextField.setHeight("30px");

		cashChequeRadio = new SRadioButton(null, 200,
				SConstants.paymentMode.cashCardList, "key", "value");
		cashChequeRadio.setHorizontal(true);
		cashChequeRadio.setValue((long) 1);

		creditPeriodTextField = new STextField(null, 100);
		creditPeriodTextField.setId("Max Credit Period");

		pannel = new SPanel();
		pannel.setSizeFull();
		hLayout = new SVerticalLayout();
//		hLayout.setSizeFull();
		// hLayout.setStyleName(Reindeer.LAYOUT_BLUE);
		vLayout = new SVerticalLayout();
		form = new SFormLayout();
		
		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(16);
		addingGrid.setRows(5);
		addingGrid.setStyleName("sup_body_lay2");

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(8);
		masterDetailsGrid.setRows(2);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(10);
		bottomGrid.setRows(4);
		// bottomGrid.setStyleName("sup_body_lay2");

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(11);
		buttonsGrid.setRows(1);
		buttonsGrid.setStyleName("sup_body_lay1");

		qtyTotal = new SLabel(null);
		taxTotal = new SLabel(null);
		netTotal = new SLabel(null);
		qtyTotal.setValue("0.0");
		taxTotal.setValue("0.0");
		netTotal.setValue("0.0");

		pannel.setSizeFull();
		form.setSizeFull();

		try {
			popupContainer=new SHorizontalLayout();
			priceListButton = new SButton();

			employSelect = new SComboField(
					null,
					200,
					usrDao.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name");

			employSelect.setValue(getUserID());

			if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin())
				employSelect.setReadOnly(true);

			List list = new ArrayList();
			list.add(new SalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));
			salesNumberList = new SComboField(null, 125, list, "id",
					"sales_number", false, getPropertyName("create_new"));

			date = new SDateField(null, 120, getDateFormat(), getWorkingDate());

			customerSelect = new SComboField(
					null,
					220,
					custDao.getAllActiveCustomerNamesWithLedgerID(getOfficeID()),
					"id", "name", true, getPropertyName("select"));
			customerSelect.setValue(settings.getDEFAULT_CUSTOMER());

			salesTypeSelect = new SNativeSelect(null, 120,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");

			Iterator itt = salesTypeSelect.getItemIds().iterator();
			if (itt.hasNext())
				salesTypeSelect.setValue(itt.next());

			refNoField = new STextField(null, 100);
			refNoField.setId("Ref No");
			refNoField.setValue("0");

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesNumberList);
			salLisrLay.addComponent(newSaleButton);

			SHorizontalLayout salNoLay = new SHorizontalLayout();
			salNoLay.setSpacing(true);
			salNoLay.addComponent(new SLabel(getPropertyName("bill_no")));
			salNoLay.addComponent(salLisrLay);

			masterDetailsGrid.addComponent(salNoLay, 1, 0);

			// masterDetailsGrid.addComponent(cashOrCreditRadio, 3, 0);

			SHorizontalLayout dateLay = new SHorizontalLayout();
			dateLay.addComponent(new SLabel(getPropertyName("date")));
			dateLay.setSpacing(true);
			dateLay.addComponent(date);
			masterDetailsGrid.addComponent(dateLay, 5, 0);
			masterDetailsGrid.setSpacing(true);

			SHorizontalLayout crhorr = new SHorizontalLayout();
			crhorr.addComponent(new SLabel(getPropertyName("max_credit_period")));
			crhorr.setSpacing(true);
			crhorr.addComponent(creditPeriodTextField);
			crhorr.setSpacing(true);
			// masterDetailsGrid.addComponent(crhorr, 5, 0);

			SHorizontalLayout refhorr = new SHorizontalLayout();
			refhorr.addComponent(new SLabel(getPropertyName("ref_no")));
			refhorr.addComponent(refNoField);
			refhorr.setSpacing(true);
			// masterDetailsGrid.addComponent(refhorr, 7, 0);

			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			newCustomerButton = new SButton();

			SHorizontalLayout hrl = new SHorizontalLayout();
			hrl.addComponent(customerSelect);
			hrl.addComponent(newCustomerButton);

			List<KeyValue> discountMethod = Arrays.asList(new KeyValue(1,
					getPropertyName("percentage")), new KeyValue(2,
					getPropertyName("amount")));

			discountRadio = new SRadioButton(getPropertyName("discount_type"),
					160, discountMethod, "intKey", "value");
			discountRadio.setHorizontal(true);
			discountRadio.setImmediate(true);
			discountRadio.setId("discountRadio");
			
			discountPercentField = new STextField(
					getPropertyName("percentage"), 70);
			discountPercentField.setStyleName("textfield_align_right");
			discountPercentField.setImmediate(true);
			discountPercentField.setId("discountPercentField");
			
			discountAmountField = new STextField(getPropertyName("amount"), 70);			
			discountAmountField.setStyleName("textfield_align_right");			
			discountAmountField.setImmediate(true);
			discountAmountField.setId("discountAmountField");

			itemDiscountRadio = new SRadioButton(getPropertyName("discount_type"), 160, discountMethod,"intKey", "value");
			itemDiscountRadio.setHorizontal(true);
			itemDiscountRadio.setImmediate(true);
			itemDiscountRadio.setId("itemDiscountRadio");

			itemDiscountPercentField = new STextField(getPropertyName("percentage"), 70);
			itemDiscountPercentField.setStyleName("textfield_align_right");
			itemDiscountPercentField.setImmediate(true);
			itemDiscountPercentField.setId("itemDiscountPercentField");

			itemDiscountAmountField = new STextField(getPropertyName("amount"),70);
			itemDiscountAmountField.setStyleName("textfield_align_right");
			itemDiscountAmountField.setImmediate(true);
			itemDiscountAmountField.setId("itemDiscountAmountField");

			SHorizontalLayout custLay = new SHorizontalLayout();
			custLay.addComponent(new SLabel(getPropertyName("customer")));
			custLay.addComponent(hrl);
			custLay.setSpacing(true);

			// masterDetailsGrid.addComponent(custLay, 1, 1);

			SHorizontalLayout horr = new SHorizontalLayout();
			horr.addComponent(new SLabel(getPropertyName("employee")));
			horr.addComponent(employSelect);
			horr.setSpacing(true);
			masterDetailsGrid.addComponent(horr, 3, 0);

			SHorizontalLayout saleTypLay = new SHorizontalLayout();
			saleTypLay.addComponent(new SLabel(getPropertyName("sales_type")));
			saleTypLay.addComponent(salesTypeSelect);
			saleTypLay.setSpacing(true);
			// masterDetailsGrid.addComponent(saleTypLay, 5, 1);

			/*
			 * masterDetailsGrid.addComponent(new SLabel("Employ :"), 4, 1);
			 * masterDetailsGrid.addComponent(employSelect, 5, 1);
			 */

			newCustomerButton.setStyleName("addNewBtnStyle");
			newCustomerButton.setDescription("Add new Customer");

			// newCustomerButton.setStyleName("v-button-link");
			// masterDetailsGrid.addComponent(newCustomerButton, 3, 1);

			masterDetailsGrid.setStyleName("sup_head_lay2");

			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");

			newUnitButton = new SButton();
			newUnitButton.setStyleName("smallAddNewBtnStyle");
			newUnitButton.setDescription("Add new Unit");
			unitMapButton = new SButton();
			unitMapButton.setStyleName("mapBtnStyle");
			unitMapButton.setDescription("Set Convertion Quantity ( Unit Management )");

			changeStkButton = new SButton();
			changeStkButton.setStyleName("loadAllBtnStyle");
			changeStkButton.setDescription("Change Stock");

			newItemButton.setDescription("Add new Item");

			quantityTextField = new STextField(getPropertyName("quantity"), 80);
			quantityTextField.setId("quantityTextField");
			quantityTextField
					.setDescription("Quantity of this Item (In seleceted Unit)");
			quantityTextField.setValue("1");
			quantityTextField.setHeight("30px");
			quantityTextField.setStyleName("sup_textfield_align_right");

			convertionQtyTextField = new STextField(
					getPropertyName("convertion_qty"), 40);
			convertionQtyTextField.setId("Cnv.Qty");
			convertionQtyTextField.setStyleName("textfield_align_right");
			convertionQtyTextField
					.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");

			convertedQtyTextField = new STextField(
					getPropertyName("converted_qty"), 60);
			convertedQtyTextField.setId("Cnvtd.Qty");
			convertedQtyTextField.setStyleName("textfield_align_right");
			convertedQtyTextField
					.setDescription("Converted Quantity in Basic Unit");
			convertedQtyTextField.setReadOnly(true);

			unitSelect = new SNativeSelect(getPropertyName("unit"), 60, null,
					"id", "symbol");
			unitSelect.setReadOnly(true);
			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setId(null);
			unitPriceTextField.setNewValue("0.00");
			quantityTextField.setHeight("30px");
			unitPriceTextField.setStyleName("sup_textfield_align_left");
			unitPriceTextField.setReadOnly(true);

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

			/*
			 * discountTextField = new STextField(getPropertyName("discount"),
			 * 80,"0.0"); discountTextField.setId("Discount");
			 * discountTextField.setStyleName("textfield_align_right");
			 */

			netPriceTextField = new STextField("Net Price", 100);
			netPriceTextField.setId("Net Price");
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("sup_textfield_align_right");
			netPriceTextField.setHeight("30px");

			billTypeField = new SRadioButton(null, 100, Arrays.asList(
					new KeyValue(SConstants.BILL_TYPE_NORMAL,
							getPropertyName("normal")), new KeyValue(
							SConstants.BILL_TYPE_EXCHANGE,
							getPropertyName("exchange"))), "intKey", "value");
			billTypeField.setValue(SConstants.BILL_TYPE_NORMAL);
			billTypeField.setId("billTypeField");

			paymentModeCombo = new SComboField(getPropertyName("payment_mode"),
					150, getPaymentModeList(), "id", "description");
			paymentModeCombo.setInputPrompt("--------- "
					+ getPropertyName("select") + " --------");

			paymentAmountTextField = new SCurrencyField(
					getPropertyName("payment_amount"), 100, getWorkingDate());
			paymentAmountTextField.setId("payment_mode");
			paymentAmountTextField.setValue(0.00);
			paymentAmountTextField.amountField
					.setStyleName("sup_textfield_align_right");
			paymentAmountTextField.setHeight("30px");
			paymentAmountTextField.setReadOnly(true);

			addPaymentModeButton = new SButton(null, "Add Payment Amount");
			addPaymentModeButton
					.setIcon(new ThemeResource("icons/load_all.png"));
			addPaymentModeButton.setStyleName("deletebtnStyle");

			balanceAmountTextField = new STextField(getPropertyName("balance"),
					100);
			balanceAmountTextField.setId("payment_mode");
			balanceAmountTextField.setValue("0.00");
			balanceAmountTextField.setStyleName("sup_textfield_align_right");
			balanceAmountTextField.setHeight("30px");
			balanceAmountTextField.setReadOnly(true);

			SFormLayout popupLayout = new SFormLayout();
			popupLayout.setWidth("425");

			paymentAmountTable = new STable(null, 400, 300);
			paymentAmountTable.setFooterVisible(true);
			paymentAmountTable.setStyleName("sup_tbl_style");

			// paymentAmountTable.setSizeFull();

			popupLayout.addComponent(paymentAmountTable);

			paymentAmountTable.setMultiSelect(true);
			// paymentAmountTable.set

			paymentAmountTable.addContainerProperty(TBP_SN, Integer.class,
					null, "#", null, Align.CENTER);
			paymentAmountTable.addContainerProperty(TBP_PAYMENT_MODE_ID,
					Long.class, null, TBP_PAYMENT_MODE_ID, null, Align.LEFT);
			paymentAmountTable.addContainerProperty(TBP_PAYMENT_MODE,
					String.class, null, TBP_PAYMENT_MODE, null, Align.LEFT);
			paymentAmountTable.addContainerProperty(TBP_PAYMENT_AMOUNT_FIELD,
					STextField.class, null, TBP_PAYMENT_AMOUNT_FIELD, null,
					Align.RIGHT);

			paymentAmountTable.setColumnFooter(TBP_PAYMENT_MODE, "Total");

			paymentAmountPopup = new SPopupView(null, popupLayout);
			paymentAmountPopup.setHideOnMouseOut(false);
			// paymentAmountPopup.setWidth("300");
			// paymentAmountPopup.setHeight("0");

			expiry_date = new SDateField(getPropertyName("exp_date"), 100,
					"dd/MMM/yyyy", new Date());
			manufaturing_date = new SDateField(getPropertyName("mfg_date"),
					100, "dd/MMM/yyyy", new Date());
			itemSelectCombo = new SListSelect(getPropertyName("item"), 250, 400);
			itemSelectCombo.setNullSelectionAllowed(false);
			itemSelectCombo.setStyleName("sup_select_list_style");
			itemSelectCombo.setImmediate(true);

			List catList = new ItemSubGroupDao()
					.getAllActiveItemSubGroupsNames(getOrganizationID());
			catList.add(0, new ItemSubGroupModel(0, "ALL"));

			categorySelectList = new SListSelect(getPropertyName("category"),
					250, 400, catList, "id", "name");
			categorySelectList.setNullSelectionAllowed(false);
			categorySelectList.setStyleName("sup_select_list_style");
			categorySelectList.setImmediate(true);

			barcodeField = new STextField(getPropertyName("barcode"), 90);
			barcodeField.setId("barcodeField");
			barcodeField.setImmediate(true);
			barcodeField.setHeight("30px");
			barcodeField.setStyleName("sup_textfield_align_right");

			enterBarcodeButton = new SCheckBox(null);
			enterBarcodeButton.setDescription("Enter Barcode");

			itemComboField = new SComboField(getPropertyName("item"), 150,
					daoObj.getAllItemsWithRealStck(getOfficeID()), "id", "name");
			itemComboField.setInputPrompt(getPropertyName("select"));
			itemComboField.setId("itemComboField");

			stockSelectList = new SListSelect(getPropertyName("stock"), 300,
					200);
			// stockSelectList.setMultiSelect(true);

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setId("addItemButton");
			addItemButton.setStyleName("addItemBtnStyle");

			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setId("updateItemButton");
			updateItemButton.setVisible(false);

			deleteItemButton = new SButton(null, "Delete");
			deleteItemButton.setStyleName("deleteItemBtnStyle");
			deleteItemButton.setId("deleteItemButton");
			deleteItemButton.setVisible(false);

			newEntryButton = new SButton(null, "New Entry");
			newEntryButton.setStyleName("createNewBtnStyle");
			newEntryButton.setVisible(false);

			SHorizontalLayout buttonLay = new SHorizontalLayout();
			buttonLay.setSpacing(true);
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);
			buttonLay.addComponent(deleteItemButton);
			buttonLay.addComponent(newEntryButton);

			hrz1 = new SHorizontalLayout();
			SHorizontalLayout itemLayout = new SHorizontalLayout();
			itemLayout.setSpacing(true);
			itemLayout.addComponent(categorySelectList);
			itemLayout.addComponent(itemSelectCombo);
			// itemLayout.addComponent(newItemButton);
			// itemLayout.addComponent(changeStkButton);
			//
			// itemLayout.setComponentAlignment(newItemButton,
			// Alignment.BOTTOM_LEFT);
			// itemLayout.setComponentAlignment(changeStkButton,
			// Alignment.BOTTOM_LEFT);

			itemPopupView = new SPopupView("", itemLayout);

			selectItemButton = new SButton(null, "Select Item");
			selectItemButton.setIcon(new ThemeResource("icons/load_all.png"));
			selectItemButton.setStyleName("deletebtnStyle");

			popupHor = new SHorizontalLayout();
			popupHor.addComponent(unitPriceTextField);
			popupHor.addComponent(priceListButton);
			popupHor.setComponentAlignment(priceListButton,
					Alignment.BOTTOM_LEFT);

			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			vert.setSpacing(false);
			// hrz2.addComponent(vert);
			// hrz2.setComponentAlignment(vert, Alignment.MIDDLE_CENTER);

			// addingGrid.addComponent(convertionQtyTextField);
			// addingGrid.addComponent(convertedQtyTextField);

			priceListButton.setDescription("Price History");
			priceListButton.setStyleName("showHistoryBtnStyle");

			addingGrid.addComponent(selectItemButton);
			addingGrid.setComponentAlignment(selectItemButton,
					Alignment.MIDDLE_CENTER);

			SHorizontalLayout radioLayout = new SHorizontalLayout();
			radioLayout.setSpacing(true);
			radioLayout.addComponent(discountRadio);
			radioLayout.addComponent(discountPercentField);
			radioLayout.addComponent(discountAmountField);

			if (!settings.isSALES_DISCOUNT_ENABLE()) {
				discountRadio.setVisible(false);
				discountPercentField.setVisible(false);
				discountAmountField.setVisible(false);
			}else{
				boolean discountEnable=true;
				try {
					discountEnable = new PrivilageSetupDao().isFacilityAccessibleToUser(
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

			if (settings.isBARCODE_ENABLED()) {

				SHorizontalLayout barLay = new SHorizontalLayout();
				barLay.addComponent(barcodeField);
				barLay.addComponent(enterBarcodeButton);
				barLay.setComponentAlignment(enterBarcodeButton,
						Alignment.MIDDLE_CENTER);
				addingGrid.addComponent(barLay);
				addingGrid.setComponentAlignment(barLay,
						Alignment.MIDDLE_CENTER);
			}
			// addingGrid.addComponent(hrz1);

			addingGrid.addComponent(itemComboField);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(popupHor);
			addingGrid.addComponent(unitSelect);
			if (isTaxEnable()) {
				addingGrid.addComponent(taxSelect);
			}

			if (isDiscountEnable()) {
				addingGrid.addComponent(itemDiscountRadio);
				addingGrid.addComponent(itemDiscountPercentField);
				addingGrid.addComponent(itemDiscountAmountField);
			}

			// addingGrid.addComponent(discountTextField);
			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(billTypeField);
			// addingGrid.addComponent(paymentModeCombo);
			// addingGrid.addComponent(paymentAmountTextField);
			// addingGrid.addComponent(balanceAmountTextField);

			addingGrid.addComponent(buttonLay);
			addingGrid
					.setComponentAlignment(buttonLay, Alignment.MIDDLE_CENTER);

			addingGrid.setColumnExpandRatio(0, 0.4f);
			addingGrid.setColumnExpandRatio(1, 1f);
			addingGrid.setColumnExpandRatio(2, 1f);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(7, 1);
			addingGrid.setColumnExpandRatio(8, 1);
			addingGrid.setColumnExpandRatio(9, 1);
			addingGrid.setColumnExpandRatio(10, 1);
			addingGrid.setColumnExpandRatio(11, 1);
			addingGrid.setColumnExpandRatio(12, 1);
			addingGrid.setColumnExpandRatio(13, 1);

			// addingGrid.setWidth("1230");

			addingGrid.setSpacing(true);

			// addingGrid.setStyleName(Reindeer.LAYOUT_BLUE);
			addingGrid.setStyleName("sup_body_lay3");

			table = new STable(null);
			table.setSizeFull();

			table.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
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
			table.addContainerProperty(TBC_DISCOUNT_TYPE, Integer.class, null,
					"Discount Type", null, Align.CENTER);
			table.addContainerProperty(TBC_DISCOUNT_PERCENTAGE, Double.class,
					null, getPropertyName("discount") + " %", null,
					Align.CENTER);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null,
					getPropertyName("discount"), null, Align.CENTER);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null,
					getPropertyName("net_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PO_ID, Long.class, null, TBC_PO_ID,
					null, Align.CENTER);
			table.addContainerProperty(TBC_INV_ID, Long.class, null,
					TBC_INV_ID, null, Align.CENTER);
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

			table.addContainerProperty(TBC_BILL_TYPE_ID, Integer.class, null,
					getPropertyName("bill_type_id"), null, Align.RIGHT);

			// table.addContainerProperty(TBC_PAYMENT_MODE_ID, Long.class,
			// null, getPropertyName("payment_mode_id"), null, Align.RIGHT);
			//
			// table.addContainerProperty(TBC_PAYMENT_AMOUNT, Double.class,
			// null, getPropertyName("payment_amount"), null, Align.RIGHT);
			/*
			 * table.addContainerProperty(TBC_MANUFACT_DATE, Date.class, null,
			 * TBC_MANUFACT_DATE, null, Align.RIGHT);
			 * table.addContainerProperty(TBC_EXPIRE_DATE, Date.class, null,
			 * TBC_EXPIRE_DATE, null, Align.RIGHT);
			 */

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, (float) 0.2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 2);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, (float) 0.2);
			table.setColumnExpandRatio(TBC_UNIT, (float) 0.6);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, (float) 1);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 1);
			table.setColumnExpandRatio(TBC_NET_TOTAL, 1);
			table.setColumnExpandRatio(TBC_NET_FINAL, 1);
			// table.setColumnExpandRatio(TBC_MANUFACT_DATE, (float) 1.1);
			// table.setColumnExpandRatio(TBC_EXPIRE_DATE, (float) 1.1);

			table.setVisibleColumns(requiredHeaders);

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

			table.setWidth(getUI().getCurrent().getPage().getBrowserWindowWidth()- 50 + "px");
			table.setHeight("320");

			// table.setSizeFull();

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);
			table.setStyleName("sup_tbl_style");

			keyboardTextField = new STextField(getPropertyName("value"), 330);
			keyboardTextField.setHeight("40");
			keyboardTextField.setStyleName("qty_textfield_style");

			keyBoard = new SKeyPad(null, 80, 80);
			keyboardLay = new SFormLayout(null, 490, 500);
			keyboardLay.setMargin(true);
			keyboardLay.addComponent(keyboardTextField);
			keyboardLay.addComponent(keyBoard);
			popKeyboad = new SPopupView("", keyboardLay);
			popKeyboad.setHideOnMouseOut(false);

			grandTotalAmtTextField = new STextField(null, 100, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("sup_textfield_align_right");

			comment = new STextArea(null, 400, 30);

			if (!isDiscountEnable()) {
				itemDiscountRadio.setVisible(false);
				itemDiscountPercentField.setVisible(false);
				itemDiscountAmountField.setVisible(false);
			}

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),7, 1);
			bottomGrid.addComponent(grandTotalAmtTextField, 9, 1);

			// bottomGrid.addComponent(new SLabel("Paying Amt :"), 3, 1);
			// bottomGrid.addComponent(payingAmountTextField, 5, 1);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,Alignment.TOP_RIGHT);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setId("saveButton");

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			updateButton.setId("updateButton");

			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			cancelButton = new SButton(getPropertyName("cancel"), 78);
			cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			cancelButton.setStyleName("deletebtnStyle");
			
			holdButton = new SButton(getPropertyName("hold"), 70);
			holdButton.setStyleName("savebtnStyle");
			//holdButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			holdButton.setId("holdButton");
			
			recallButton = new SButton(getPropertyName("recall"), 70);
			recallButton.setStyleName("savebtnStyle");
			//recallButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			recallButton.setId("recallButton");
			

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		//	mainButtonLayout.setSizeFull();
			mainButtonLayout.addComponent(saveButton);
			mainButtonLayout.addComponent(updateButton);
			mainButtonLayout.addComponent(holdButton);
			mainButtonLayout.addComponent(recallButton);
			if (!settings.isKEEP_DELETED_DATA()){
				mainButtonLayout.addComponent(deleteButton);
			//	mainButtonLayout.setComponentAlignment(deleteButton, Alignment.BOTTOM_CENTER);
			}	else {
				mainButtonLayout.addComponent(cancelButton);
			//	mainButtonLayout.setComponentAlignment(cancelButton, Alignment.BOTTOM_CENTER);
			}
				
			
		//	mainButtonLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
		//	mainButtonLayout.setComponentAlignment(updateButton, Alignment.BOTTOM_CENTER);
			SVerticalLayout buttonVerticalLayout = new SVerticalLayout();
			buttonVerticalLayout.setSizeFull();
			buttonVerticalLayout.addComponent(mainButtonLayout);
			
			buttonVerticalLayout.setComponentAlignment(mainButtonLayout, Alignment.BOTTOM_CENTER);
			
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			cancelButton.setVisible(false);

			// buttonsGrid.addComponent(new SLabel("Payment Mode"), 0, 0);
			// buttonsGrid.addComponent(cashChequeRadio, 1, 0);
		//	buttonsGrid.addComponent(mainButtonLayout, 3, 0);
			buttonsGrid.addComponent(radioLayout, 4, 0);
			// buttonsGrid.addComponent(
			// new SLabel(getPropertyName("total_amount")), 5, 0);

			buttonsGrid.addComponent(payingAmountTextField, 5, 0);
			buttonsGrid.addComponent(paymentAmountTextField, 6, 0);
			buttonsGrid.addComponent(addPaymentModeButton, 7, 0);
			buttonsGrid.addComponent(balanceAmountTextField, 8, 0);

			mainButtonLayout.setSpacing(true);

			// Added by anil
			printButton = new SButton(getPropertyName("print"));
			printButton.setIcon(new ThemeResource("icons/print.png"));
			printButton.setId("printButton");
			mainButtonLayout.addComponent(printButton);
			printButton.setVisible(false);

			// SHorizontalLayout itemLay=new SHorizontalLayout();
			// itemLay.setStyleName("sup_body_lay1");
			// itemLay.setSizeFull();
			// itemLay.addComponent(table);

			masterDetailsGrid.addComponent(itemPopupView);
			masterDetailsGrid.addComponent(popKeyboad);
			masterDetailsGrid.addComponent(paymentAmountPopup);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			// form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);
			form.addComponent(buttonVerticalLayout);
			
			form.setComponentAlignment(buttonVerticalLayout, Alignment.BOTTOM_CENTER);

			hLayout.addComponent(popupContainer);
			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupContainer, Alignment.TOP_CENTER);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);

			windowNotif.addComponent(hLayout,
					"left: 0px; right: 0px; z-index:-1;");

			customerSelect.focus();

			pannel.setContent(windowNotif);

			barcodeField.focus();

			poWindow = new SDialogBox(getPropertyName("sales_orders"), 400, 400);
			poWindow.center();
			poWindow.setResizable(false);
			poWindow.setModal(true);
	//		poWindow.setCloseShortcut(KeyCode.ESCAPE);
			SFormLayout fr1 = new SFormLayout();
			poWindow.addComponent(fr1);

			newCustomerWindow = new SDialogBox(getPropertyName("add_customer"),
					700, 600);
			newCustomerWindow.center();
			newCustomerWindow.setResizable(false);
			newCustomerWindow.setModal(true);
	//		newCustomerWindow.setCloseShortcut(KeyCode.ESCAPE);
			salesCustomerPanel = new SalesCustomerPanel();
			newCustomerWindow.addComponent(salesCustomerPanel);

			newItemWindow = new SDialogBox(getPropertyName("add_item"), 500,
					600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
	//		newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			newItemWindow.addComponent(itemPanel);

			stockSelectList.setNullSelectionAllowed(false);

			if (settings.getDEFAULT_CUSTOMER() == 0) {
				SNotification.show(getPropertyName("default_customer"),
						Type.ERROR_MESSAGE);
			}

			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			/*
			 * table.addShortcutListener(new
			 * ShortcutListener("Add New Purchase", ShortcutAction.KeyCode.N,
			 * new int[] { ShortcutAction.ModifierKey.ALT }) {
			 * 
			 * @Override public void handleAction(Object sender, Object target)
			 * { loadSale(0); } });
			 */

//			table.addShortcutListener(new ShortcutListener(
//					"Clear entereded and edited data and Add new",
//					ShortcutAction.KeyCode.ESCAPE, null) {
//				@Override
//				public void handleAction(Object sender, Object target) {
//					table.setValue(null);
//				}
//			});

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

			ClickListener confirmListener = new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),
									confirmBox.getComments(),
									(Long) salesNumberList.getValue(),
									confirmBox.getUserID());
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

			ClickListener clickListnr = new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (event.getButton().getId()
							.equals(windowNotif.SAVE_SESSION)) {
						if (salesNumberList.getValue() != null
								&& !salesNumberList.getValue().toString()
										.equals("0")) {
							saveSessionActivity(
									getOptionId(),
									(Long) salesNumberList.getValue(),
									"Sales : No. "
											+ salesNumberList
													.getItemCaption(salesNumberList
															.getValue()));
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} else
							Notification.show("Select an Invoice..!",
									"Select an Invoice for save in session",
									Type.HUMANIZED_MESSAGE);
					} else if (event.getButton().getId()
							.equals(windowNotif.REPORT_ISSUE)) {
						if (salesNumberList.getValue() != null
								&& !salesNumberList.getValue().toString()
										.equals("0")) {
							confirmBox.open();
						} else
							Notification.show("Select an Invoice..!",
									"Select an Invoice for Save in session",
									Type.HUMANIZED_MESSAGE);
					} else {
						try {
							helpPopup = new SHelpPopupView(getOptionId());
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
			itemDiscountRadio.setValue(1);

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

			paymentAmountTextField.amountField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							calculateBalance();
						}

					});

			discountAmountField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							calculateTotals();
						}
					});

			itemDiscountPercentField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							double qty = 0, price = 0;
							double discountPer = 0, discount = 0;
							try {
								qty = roundNumber(toDouble(quantityTextField
										.getValue().trim()));
							} catch (Exception e1) {
								qty = 0;
							}

							try {
								price = roundNumber(toDouble(unitPriceTextField
										.getValue().trim()));
							} catch (Exception e1) {
								price = 0;
							}

							try {
								discountPer = toDouble(itemDiscountPercentField
										.getValue().toString().trim());
							} catch (Exception e) {
								discountPer = 0;
							}
							itemDiscountPercentField
									.setNewValue(roundNumber(discountPer) + "");
							discount = roundNumber((qty * price) * discountPer
									/ 100);
							itemDiscountAmountField
									.setNewValue(roundNumber(discount) + "");
							calculateTotals();
						}
					});

			itemDiscountAmountField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							calculateNetPrice();
						}
					});

			payingAmountTextField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							calculateBalance();
						}
					});
			selectItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPopupView.setPopupVisible(true);
				}
			});

			final FocusListener focusListener = new FocusListener() {
				@Override
				public void focus(FocusEvent event) {
					if (event.getComponent() != null) {

						focusedComponent = event.getComponent();
						if (((STextField) event.getSource()).getId() != null)

							popKeyboad.setPopupVisible(true);
						// if(!((STextField)focusedComponent).getValue().equals("0"))
						// keyboardTextField.setValue(((STextField)focusedComponent).getValue());
						keyboardTextField.setValue("");
						keyboardTextField.setCaption(((STextField) event
								.getSource()).getId());
						keyboardTextField.setComponentError(null);
						keyboardTextField.selectAll();
					}
				}
			};

			enterBarcodeButton
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {

							if (enterBarcodeButton.getValue()) {
								barcodeField.addFocusListener(focusListener);
							} else {
								barcodeField.removeFocusListener(focusListener);
							}
							barcodeField.focus();
						}
					});

			ClickListener keyBoardListener = new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (event.getComponent().getId().equals("1")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "1");
					} else if (event.getComponent().getId().equals("2")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "2");
					} else if (event.getComponent().getId().equals("3")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "3");
					} else if (event.getComponent().getId().equals("4")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "4");
					} else if (event.getComponent().getId().equals("5")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "5");
					} else if (event.getComponent().getId().equals("6")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "6");
					} else if (event.getComponent().getId().equals("7")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "7");
					} else if (event.getComponent().getId().equals("8")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "8");
					} else if (event.getComponent().getId().equals("9")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "9");
					} else if (event.getComponent().getId().equals("0")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "0");
					} else if (event.getComponent().getId().equals("00")) {
						keyboardTextField.setValue(keyboardTextField.getValue()
								+ "00");
					} else if (event.getComponent().getId().equals(".")) {
						if (!keyboardTextField.getValue().contains("."))
							keyboardTextField.setValue(keyboardTextField
									.getValue() + ".");
					} else if (event.getComponent().getId().equals("Clr")) {
						keyboardTextField.setValue("");
					} else if (event.getComponent().getId().equals("Close")) {
						keyboardTextField.setValue("");
						popKeyboad.setPopupVisible(false);
					} else if (event.getComponent().getId().equals("Del")) {
						if (keyboardTextField.getValue().length() > 0) {
							keyboardTextField.setValue(keyboardTextField
									.getValue().substring(
											0,
											keyboardTextField.getValue()
													.length() - 1));
						}
					} else if (event.getComponent().getId().equals("Enter")) {

						if (isAddingValidIncludingZero(keyboardTextField)) {

							((STextField) focusedComponent)
									.setValue(keyboardTextField.getValue());

							if (focusType == 1) {
								addItemButton.click();
							} else if (focusType == 2) {
								updateItemButton.click();
							}
							keyboardTextField.setValue("");
							popKeyboad.setPopupVisible(false);
							calculateNetPrice();
						}
					}

				}
			};

			keyBoard.setImmediate(true);
			keyBoard.setListener(keyBoardListener);

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesNumberList.setValue(null);
					salesNumberList.setValue((long) 0);
				}
			});

			changeStkButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (itemSelectCombo.getValue() != null	&& unitSelect.getValue() != null) {
						try {
							SFormLayout lay = new SFormLayout();
							lay.setWidth("400");
							lay.setHeight("250");
							lay.addComponent(stockSelectList);

							SPopupView pop = new SPopupView("", lay);

							hrz1.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						SPopupView pop = new SPopupView("", new SFormLayout(getPropertyName("select_item_unit"), 200, 80));
						hrz1.addComponent(pop);
						pop.setPopupVisible(true);
					}
				}
			});

			priceListButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (itemSelectCombo.getValue() != null	&& unitSelect.getValue() != null) {
						try {

							STable table = new STable(getPropertyName("sales_rate_history"));

							table.addContainerProperty("Date", Date.class,null, getPropertyName("date"), null,Align.CENTER);
							table.addContainerProperty("Customer Name",String.class, null,getPropertyName("customer_name"), null,Align.LEFT);
							table.addContainerProperty("Price", Double.class,null, getPropertyName("price"), null,Align.RIGHT);
							table.setVisibleColumns(new String[] { "Date","Customer Name", "Price" });

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
							table.setColumnExpandRatio("Customer Name",(float) 1);
							table.setColumnExpandRatio("Price", (float) 0.5);

							table.setWidth("400");
							table.setHeight("300");
							table.setSelectable(true);

							STable purchTable = new STable(getPropertyName("purchase_rate_history"));

							purchTable.addContainerProperty("Date", Date.class,null, getPropertyName("date"), null,Align.CENTER);
							purchTable.addContainerProperty("Supplier Name",String.class, null,getPropertyName("supplier_name"), null,
									Align.LEFT);
							purchTable.addContainerProperty("Price",Double.class, null,getPropertyName("price"), null,Align.RIGHT);
							purchTable.setVisibleColumns(new String[] { "Date","Supplier Name", "Price" });

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

							purchTable.setColumnExpandRatio("Date", (float) 0.3);
							purchTable.setWidth("400");
							purchTable.setHeight("300");
							purchTable.setSelectable(true);

							SPopupView pop = new SPopupView("",new SHorizontalLayout(true, table,purchTable));

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
							itm = itmDao.getItem((Long) itemSelectCombo
									.getValue());
						} catch (Exception e) {
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

			customerSelect.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						if (customerSelect.getValue() != null) {
							CustomerModel cust = custDao
									.getCustomerFromLedger((Long) customerSelect
											.getValue());
							if (cust != null) {
								creditPeriodTextField.setValue(asString(cust
										.getMax_credit_period()));
								customerSelect
										.setDescription("<h1><i>Current Balance</i> : "
												+ cust.getLedger()
														.getCurrent_balance()
												+ "</h1>");

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
											|| salesNumberList.getValue()
													.equals("") || salesNumberList
											.getValue().toString().equals("0"))
											&& (cust.getCredit_limit() < cust
													.getLedger()
													.getCurrent_balance())) {
										buttonsGrid.setVisible(false);
										SNotification
												.show(getPropertyName("limit_excess"),
														Type.ERROR_MESSAGE);
									} else {
										buttonsGrid.setVisible(true);
									}

								}

								if (settings.isALERT_FOR_UNDER_CREDIT_LIMIT()) {
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
											|| salesNumberList.getValue()
													.equals("") || salesNumberList
											.getValue().toString().equals("0"))
											&& (cust.getCredit_limit() < cust
													.getLedger()
													.getCurrent_balance())) {
										SNotification
												.show(getPropertyName("limit_excess_warning")
														+ ". Limit "
														+ cust.getCredit_limit()
														+ ": Current balance "
														+ cust.getLedger()
																.getCurrent_balance(),
														Type.ERROR_MESSAGE);
									}
								}
							}
						} else
							customerSelect.setDescription(null);

						if (session.getAttribute("SO_Select_Disabled") == null) {

							if (session.getAttribute("SO_Already_Added") != null) {
								table.removeAllItems();
								session.removeAttribute("SO_Already_Added");
							}

						} else {
							session.removeAttribute("SO_Select_Disabled");
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
					barcodeField.focus();
					barcodeField.setValue("");
				}
			});
			
			holdButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							
							List<HoldSalesInventoryDetailsModel> itemsList = new ArrayList<HoldSalesInventoryDetailsModel>();

							HoldSalesInventoryDetailsModel invObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new HoldSalesInventoryDetailsModel();

								item = table.getItem(it.next());

								invObj.setBatch_id(0);
								invObj.setCessAmount(0);
								invObj.setConversionRate(1);
								invObj.setCurrencyId(getCurrencyID());

								if (isDiscountEnable()) {
									invObj.setDiscount_type((Integer) item
											.getItemProperty(TBC_DISCOUNT_TYPE)
											.getValue());
									invObj.setDiscountPercentage(roundNumber((Double) item
											.getItemProperty(
													TBC_DISCOUNT_PERCENTAGE)
											.getValue()));
									invObj.setDiscount(roundNumber((Double) item
											.getItemProperty(TBC_DISCOUNT)
											.getValue()));
								} else {
									invObj.setDiscount(0);
									invObj.setDiscount_type(1);
									invObj.setDiscountPercentage(0);
								}
								
								invObj.setGrade_id(0);
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
										.getValue());
								invObj.setQuantity_returned(0);
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setStock_id((Long) item.getItemProperty(
										TBC_STOCK_ID).getValue());
								if (taxEnable) {
									invObj.setTax(new TaxModel((Long) item
											.getItemProperty(TBC_TAX_ID)
											.getValue()));
									invObj.setTaxAmount((Double) item
											.getItemProperty(TBC_TAX_AMT)
											.getValue());
									invObj.setTaxPercentage((Double) item
											.getItemProperty(TBC_TAX_PERC)
											.getValue());
								} else {
									invObj.setTax(new TaxModel(1));
									invObj.setTaxAmount(0);
									invObj.setTaxPercentage(0);
								}
								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue());
								invObj.setBillType((Integer) item
										.getItemProperty(TBC_BILL_TYPE_ID)
										.getValue());

								itemsList.add(invObj);
							}
							
							HoldSalesModel salObj = new HoldSalesModel();
							salObj.setCustomer(new LedgerModel((Long) customerSelect.getValue()));
							salObj.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							salObj.setInventory_details_list(itemsList);
							salObj.setResponsible_employee((Long) employSelect.getValue());
							
							boolean isHoldExist=holdSalesDao.isExist(date.getValue(), getOfficeID(), holdSalesId);
							if(isHoldExist){
								
								HoldSalesModel holdModel=holdSalesDao.getHoldSalesModel(holdSalesId);
								
								holdModel.setCustomer(new LedgerModel((Long) customerSelect.getValue()));
								holdModel.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
								holdModel.setOffice(new S_OfficeModel(getOfficeID()));
								holdModel.setInventory_details_list(itemsList);
								holdModel.setResponsible_employee((Long) employSelect.getValue());
								
								holdModel.setHoldSalesNumber(holdModel.getHoldSalesNumber());
								holdSalesDao.update(holdModel);
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								salesNumberList.setValue((long) 0);
							
							}else{
								HoldSalesModel saleObj = new HoldSalesModel();
								saleObj.setCustomer(new LedgerModel((Long) customerSelect.getValue()));
								saleObj.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
								saleObj.setOffice(new S_OfficeModel(getOfficeID()));
								saleObj.setInventory_details_list(itemsList);
								saleObj.setResponsible_employee((Long) employSelect.getValue());
								
								saleObj.setHoldSalesNumber(getNextSequence("Hold Sales Number", getLoginID()) + "");
								long holdId=holdSalesDao.save(saleObj);
								Notification.show("Sales Holded ",Type.WARNING_MESSAGE);
								salesNumberList.setValue((long) 0);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			recallButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (date.getValue() == null || date.getValue().equals("")) {
						setRequiredError(date, getPropertyName("invalid_selection"), true);
					}else{
						List holdSalesList=new ArrayList();
						try {
							holdSalesList=holdSalesDao.getAllSalesHolded(date.getValue(), getOfficeID());
							if(holdSalesList.size()>0){
								Iterator itr=holdSalesList.iterator();
								SFormLayout mainForm = new SFormLayout();
								mainForm.setSpacing(true);
								mainForm.setHeight("350");
								mainForm.setWidth("430");
								
								while(itr.hasNext()){
									HoldSalesModel salMdl=(HoldSalesModel) itr.next();
									
									SFormLayout form = new SFormLayout();
									form.setStyleName("inner_form_style");
									
									form.addComponent(new SLabel(getPropertyName("sales_man"),usrDao.getUser(salMdl.getResponsible_employee()).getFirst_name()));
									form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(salMdl.getDate())));
									SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
									grid.setColumns(12);
									grid.setRows(salMdl
											.getInventory_details_list().size() + 3);
									grid.setId(asString(salMdl.getId()));
									grid.addComponent(new SLabel(null, "#"), 0, 0);
									grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
									grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
									grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
									grid.addComponent(new SLabel(null, getPropertyName("unit_price")), 4, 0);
									grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
									grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
									grid.setSpacing(true);
									
									int i = 1;
									HoldSalesInventoryDetailsModel invObj;
									Iterator itr1 = salMdl.getInventory_details_list().iterator();
									while(itr1.hasNext()){
										invObj=(HoldSalesInventoryDetailsModel)itr1.next();
										grid.addComponent(new SLabel(null, i + ""),	0, i);
										grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
										grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
										grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
										grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
										grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
										grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																			- invObj.getDiscount() 
																			+ invObj.getTaxAmount())+ ""), 6, i);
										i++;
									}
									grid.addLayoutClickListener(layoutListener);
									form.addComponent(grid);
									mainForm.addComponent(form);
								}
								mainForm.setStyleName("formLayoutScroll");
								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", mainForm);
								pop.setWidth("600");
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
								
							}else{
								Notification.show(getPropertyName(""),Type.WARNING_MESSAGE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			layoutListener=new LayoutClickListener() {
				@Override
				public void layoutClick(LayoutClickEvent event) {
					try {
						holdSalesId=0;
						deleteButton.setVisible(false);
						long id=Long.parseLong(event.getComponent().getId());
						if(id!=0){
							holdSalesId=id;
							HoldSalesModel salMdl=holdSalesDao.getHoldSalesModel(id);
							table.setVisibleColumns(allHeaders);
							table.removeAllItems();
							
							employSelect.setValue(salMdl.getResponsible_employee());
							date.setValue(salMdl.getDate());
							
							HoldSalesInventoryDetailsModel invObj;
							double netTotal;
							Iterator it = salMdl.getInventory_details_list().iterator();
							while (it.hasNext()) {
								invObj = (HoldSalesInventoryDetailsModel) it.next();

								netTotal = roundNumber((invObj
										.getUnit_price() * invObj
										.getQunatity())
										+ invObj.getTaxAmount()
										- invObj.getDiscount());

								table.addItem(
										new Object[] {
												table.getItemIds()
														.size() + 1,
												invObj.getItem()
														.getId(),
												invObj.getItem()
														.getName()
														+ " ("
														+ invObj.getItem()
																.getItem_code()
														+ ")",
												invObj.getQunatity(),
												invObj.getUnit()
														.getId(),
												invObj.getUnit()
														.getSymbol(),
												invObj.getUnit_price(),
												invObj.getTax().getId(),
												invObj.getTaxAmount(),
												invObj.getTaxPercentage(),
												invObj.getDiscount_type(),
												roundNumber(invObj
														.getDiscountPercentage()),
												roundNumber(invObj
														.getDiscount()),
												(invObj.getUnit_price() * invObj
														.getQunatity()),
												(long)0,
												invObj.getId(),
												roundNumber(invObj
														.getUnit_price()
														* invObj.getQunatity()
														+ invObj.getTaxAmount()),
												netTotal,
												invObj.getQuantity_in_basic_unit(),
												invObj.getStock_id(),
												(invObj.getQuantity_in_basic_unit() / invObj
														.getQunatity()),
														invObj.getBillType()},
										table.getItemIds().size() + 1);
							}
							table.setVisibleColumns(requiredHeaders);
							deleteButton.setVisible(true);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							long customer_id = (Long) customerSelect.getValue();
							SalesModel salObj = new SalesModel();

							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();

							SalesInventoryDetailsModel invObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new SalesInventoryDetailsModel();

								item = table.getItem(it.next());

								invObj.setBatch_id(0);
								invObj.setCessAmount(0);
								invObj.setConversionRate(1);
								invObj.setCurrencyId(getCurrencyID());
								invObj.setDelivery_child_id(0);
								invObj.setDelivery_id(0);

								if (isDiscountEnable()) {
									invObj.setDiscount_type((Integer) item
											.getItemProperty(TBC_DISCOUNT_TYPE)
											.getValue());
									invObj.setDiscountPercentage(roundNumber((Double) item
											.getItemProperty(
													TBC_DISCOUNT_PERCENTAGE)
											.getValue()));
									invObj.setDiscount(roundNumber((Double) item
											.getItemProperty(TBC_DISCOUNT)
											.getValue()));
								} else {
									invObj.setDiscount(0);
									invObj.setDiscount_type(1);
									invObj.setDiscountPercentage(0);
								}
								invObj.setGrade_id(0);
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setLocation_id(0);
								invObj.setLock_count(0);
								invObj.setOrder_child_id(0);
								invObj.setOrder_id(0);
								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
										.getValue());
								invObj.setQuantity_returned(0);
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setStock_id((Long) item.getItemProperty(
										TBC_STOCK_ID).getValue());
								invObj.setStock_ids("");
								if (taxEnable) {
									invObj.setTax(new TaxModel((Long) item
											.getItemProperty(TBC_TAX_ID)
											.getValue()));
									invObj.setTaxAmount((Double) item
											.getItemProperty(TBC_TAX_AMT)
											.getValue());
									invObj.setTaxPercentage((Double) item
											.getItemProperty(TBC_TAX_PERC)
											.getValue());
								} else {
									invObj.setTax(new TaxModel(1));
									invObj.setTaxAmount(0);
									invObj.setTaxPercentage(0);
								}
								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

								invObj.setBillType((Integer) item
										.getItemProperty(TBC_BILL_TYPE_ID)
										.getValue());

								itemsList.add(invObj);

							}

							salObj.setActive(true);
							salObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							salObj.setCash_cheque((Long) cashChequeRadio
									.getValue());
							salObj.setChequeDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							salObj.setChequeNo("");
							salObj.setComments(comment.getValue());
							salObj.setConversionRate(1);
							salObj.setCredit_note(0);
							salObj.setCurrency_id(getCurrencyID());
							salObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							salObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							salObj.setDebit_note(0);
							salObj.setDepartment_id(0);
							salObj.setDivision_id(0);
							salObj.setExpenseAmount(0);
							salObj.setExpenseCreditAmount(0);
							salObj.setInventory_details_list(itemsList);
							salObj.setLock_count(0);
							salObj.setNetCurrencyId(new CurrencyModel(
									getCurrencyID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							salObj.setPaid_by_payment(0);
							salObj.setPayment_amount(paymentAmountTextField
									.getValue());
							salObj.setPayment_credit(0);
							salObj.setPayment_done('Y');
							salObj.setPaymentConversionRate(1);
							salObj.setPayment_credit(1);
							salObj.setRef_no(refNoField.getValue());
							salObj.setResponsible_employee((Long) employSelect.getValue());
							salObj.setSales_account(settings.getSALES_ACCOUNT());
							salObj.setSales_expense_list(null);
							salObj.setSales_number(getNextSequence(
									"Sales Number", getLoginID()) + "");
							salObj.setSales_type((Long) salesTypeSelect
									.getValue());
							salObj.setStatus(1);

							salObj.setDiscount_type((Integer) discountRadio
									.getValue());
							salObj.setDiscountPercentage(roundNumber(toDouble(discountPercentField
									.getValue().trim())));
							salObj.setDiscountAmount(roundNumber(toDouble(discountAmountField
									.getValue().trim())));

							FinTransaction trans = new FinTransaction();
							double payingAmount = toDouble(payingAmountTextField
									.getValue());
							double paymentPaidAmount;
							double transferAmount;
							if (payingAmount > 0) {
								List<SalesPaymentModeDetailsModel> paymentModeList = new ArrayList<SalesPaymentModeDetailsModel>();
								SalesPaymentModeDetailsModel paymentMode;
								it = paymentAmountTable.getItemIds().iterator();
								STextField field;
								while (it.hasNext()) {
									item = paymentAmountTable.getItem(it.next());
									field = (STextField) item.getItemProperty(
											TBP_PAYMENT_AMOUNT_FIELD)
											.getValue();
									if (toDouble(field.getValue()) == 0
											|| payingAmount == 0) {
										continue;
									}
									paymentPaidAmount = toDouble(field.getValue());

									paymentMode = new SalesPaymentModeDetailsModel();
									paymentMode.setPaymentMode(paymentModeDao.getPaymentModeModel((Long) item
													.getItemProperty(TBP_PAYMENT_MODE_ID)
													.getValue()));
									paymentMode.setAmount(paymentPaidAmount);

									paymentModeList.add(paymentMode);

									if (paymentPaidAmount > payingAmount) {
										transferAmount = payingAmount;
										payingAmount = 0;
									} else {
										transferAmount = paymentPaidAmount;
										payingAmount -= paymentPaidAmount;
									}

									if (paymentMode.getPaymentMode().getTransactionType() == SConstants.INWARD) {
										trans.addTransaction(SConstants.DR,	(Long) customerSelect.getValue(),
												paymentMode.getPaymentMode().getLedger().getId(),
												transferAmount);

										trans.addTransaction(SConstants.DR,settings.getSALES_ACCOUNT(),
												(Long) customerSelect.getValue(),
												transferAmount);
									} else {
										trans.addTransaction(SConstants.CR,paymentMode.getPaymentMode().getLedger().getId(),
												(Long) customerSelect.getValue(),
												transferAmount);

										trans.addTransaction(SConstants.CR,(Long) customerSelect.getValue(), 
												settings.getSALES_ACCOUNT(),
												transferAmount);
									}

								}

								salObj.setSales_payment_mode_list(paymentModeList);

							} else {
								salObj.setSales_payment_mode_list(null);
							}
							double totalAmt = toDouble(grandTotalAmtTextField.getValue());
							double netAmt = totalAmt;
							double amt = 0;
							double payingAmt = toDouble(payingAmountTextField.getValue());
							// if (paymentAmountTextField.getValue() > 0) {
							//
							// }
							double taxAmount = toDouble(table
									.getColumnFooter(TBC_TAX_AMT));
							double discountAmnt = toDouble(table
									.getColumnFooter(TBC_DISCOUNT));

							// if (settings.getSALES_TAX_ACCOUNT() != 0) {
							// amt = toDouble(table
							// .getColumnFooter(TBC_TAX_AMT));
							// if (amt != 0) {
							// trans.addTransaction(
							// SConstants.CR,
							// settings.getSALES_ACCOUNT(),
							// settings.getSALES_TAX_ACCOUNT(),
							// roundNumber(amt));
							// totalAmt -= amt;
							// }
							// }

							// if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
							// amt = 0;
							// if (amt != 0) {
							// trans.addTransaction(
							// SConstants.CR,
							// settings.getSALES_ACCOUNT(),
							// settings.getSALES_DESCOUNT_ACCOUNT(),
							// roundNumber(amt));
							// totalAmt -= amt;
							// }
							// }

							long id = daoObj.save(salObj, trans.getTransaction(
									SConstants.SALES, CommonUtil.getSQLDateFromUtilDate(date.getValue())), payingAmt,
									settings.getUPDATE_RATE_AND_CONV_QTY());

							saveActivity(	getOptionId(),
									"New Sales Created. Bill No : "
											+ salObj.getSales_number()
											+ ", Customer : "
											+ customerSelect
													.getItemCaption(customerSelect
															.getValue())
											+ ", Amount : "
											+ salObj.getAmount(), salObj
											.getId());
							
							if(holdSalesId!=0)
								holdSalesDao.delete(holdSalesId);

							loadSale(id);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							printButton.click();
							barcodeField.focus();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});

			salesNumberList.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								removeAllErrors();
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
								cancelButton.setVisible(true);
								printButton.setVisible(true);
								saveButton.setVisible(false);
								holdButton.setVisible(false);
								recallButton.setVisible(false);
								discountRadio.setValue(null);
								discountRadio.setValue(1);
								if (salesNumberList.getValue() != null
										&& !salesNumberList.getValue()
												.toString().equals("0")) {

									SalesModel salObj = daoObj
											.getSale((Long) salesNumberList
													.getValue());

									table.setVisibleColumns(allHeaders);

									table.removeAllItems();

									SalesInventoryDetailsModel invObj;
									double netTotal;
									Iterator it = salObj
											.getInventory_details_list()
											.iterator();
									while (it.hasNext()) {
										invObj = (SalesInventoryDetailsModel) it
												.next();

										netTotal = roundNumber((invObj
												.getUnit_price() * invObj
												.getQunatity())
												+ invObj.getTaxAmount()
												- invObj.getDiscount());

										table.addItem(
												new Object[] {
														table.getItemIds()
																.size() + 1,
														invObj.getItem()
																.getId(),
														invObj.getItem()
																.getName()
																+ " ("
																+ invObj.getItem()
																		.getItem_code()
																+ ")",
														invObj.getQunatity(),
														invObj.getUnit()
																.getId(),
														invObj.getUnit()
																.getSymbol(),
														invObj.getUnit_price(),
														invObj.getTax().getId(),
														invObj.getTaxAmount(),
														invObj.getTaxPercentage(),
														invObj.getDiscount_type(),
														roundNumber(invObj
																.getDiscountPercentage()),
														roundNumber(invObj
																.getDiscount()),
														(invObj.getUnit_price() * invObj
																.getQunatity()),
														invObj.getOrder_id(),
														invObj.getId(),
														roundNumber(invObj
																.getUnit_price()
																* invObj.getQunatity()
																+ invObj.getTaxAmount()),
														netTotal,
														invObj.getQuantity_in_basic_unit(),
														invObj.getStock_id(),
														(invObj.getQuantity_in_basic_unit() / invObj
																.getQunatity()),
														invObj.getBillType() },
												table.getItemIds().size() + 1);
									}

									paymentAmountTextField.setNewValue(salObj
											.getAmount());
									it = salObj.getSales_payment_mode_list()
											.iterator();
									while (it.hasNext()) {
										SalesPaymentModeDetailsModel model = (SalesPaymentModeDetailsModel) it
												.next();
										paymentModeHashMap.get(
												model.getPaymentMode().getId())
												.setValue(
														model.getAmount() + "");
									}
									table.setVisibleColumns(requiredHeaders);

									grandTotalAmtTextField
											.setNewValue(roundNumberToString(salObj
													.getAmount()));
									// buildingSelect.setValue(salObj.getBuilding().getId());
									comment.setValue(salObj.getComments());
									date.setValue(salObj.getDate());
									// expected_delivery_date.setValue(salObj.getExpected_delivery_date());

									session.setAttribute("SO_Select_Disabled",
											'Y');

									customerSelect.setValue(salObj
											.getCustomer().getId());

									employSelect.setNewValue(salObj
											.getResponsible_employee());

									refNoField.setValue(asString(salObj
											.getRef_no()));
									payingAmountTextField
											.setNewValue(roundNumberToString(salObj
													.getPayment_amount()));
									if (settings.isSALES_DISCOUNT_ENABLE()) {
										discountRadio.setValue(salObj
												.getDiscount_type());
										discountPercentField.setValue(roundNumber(salObj
												.getDiscountPercentage()) + "");
										discountAmountField
												.setNewValue(roundNumber(salObj
														.getDiscountAmount())
														+ "");
									}

									salesTypeSelect.setValue(salObj
											.getSales_type());
									cashChequeRadio.setValue(salObj
											.getCash_cheque());

									updateButton.setVisible(true);
									printButton.setVisible(true);
									deleteButton.setVisible(true);
									cancelButton.setVisible(true);
									saveButton.setVisible(false);

									status = salObj.getStatus();

								} else {
									table.removeAllItems();
									holdSalesId=(long)0;
									resetPaymentModeTable();
									paymentAmountTextField.setNewValue(0);

									grandTotalAmtTextField.setNewValue("0.0");
									payingAmountTextField.setNewValue("0.0");
									comment.setValue("");
									date.setValue(new Date(getWorkingDate()
											.getTime()));
									customerSelect.setValue(settings
											.getDEFAULT_CUSTOMER());
									employSelect.setNewValue(getLoginID());
									cashChequeRadio.setValue((long) 1);
									creditPeriodTextField.setValue("0");
									refNoField.setValue("0");
									discountRadio.setValue(null);
									discountRadio.setValue(1);
									saveButton.setVisible(true);
									holdButton.setVisible(true);
									recallButton.setVisible(true);
									updateButton.setVisible(false);
									printButton.setVisible(false);
									deleteButton.setVisible(false);
									cancelButton.setVisible(false);
								}

								calculateTotals();

								categorySelectList.setValue((long) 0);
								itemSelectCombo.setValue(null);
								itemSelectCombo.focus();
								quantityTextField.setValue("1");
								keyboardTextField.setValue("0.0");
								unitPriceTextField.setNewValue("0.0");
								netPriceTextField.setNewValue("0.0");
								itemDiscountRadio.setValue(null);
								itemDiscountRadio.setValue(1);

								CustomerModel cust = custDao
										.getCustomerFromLedger((Long) customerSelect
												.getValue());
								if (settings.isBARCODE_ENABLED())
									barcodeField.focus();
								else
									customerSelect.focus();

								if (!isFinYearBackEntry()) {
									saveButton.setVisible(false);
									updateButton.setVisible(false);
									deleteButton.setVisible(false);
									cancelButton.setVisible(false);
									if (salesNumberList.getValue() == null
											|| salesNumberList.getValue()
													.toString().equals("0")) {
										Notification
												.show(getPropertyName("warning_transaction"),
														Type.WARNING_MESSAGE);
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}
						}

					});

			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							long customer_id = (Long) customerSelect.getValue();

							SalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());

							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();

							SalesInventoryDetailsModel invObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new SalesInventoryDetailsModel();

								item = table.getItem(it.next());

								// invObj.setBatch_id(0);
								// invObj.setCessAmount(0);
								// invObj.setConversionRate(1);
								// invObj.setCurrencyId(getCurrencyID());
								// invObj.setDelivery_child_id(0);
								// invObj.setDelivery_id(0);

								invObj.setDiscount(0);
								invObj.setDiscount_type(1);
								invObj.setDiscountPercentage(0);

								if (isDiscountEnable()) {
									invObj.setDiscount_type((Integer) item
											.getItemProperty(TBC_DISCOUNT_TYPE)
											.getValue());
									invObj.setDiscountPercentage(roundNumber((Double) item
											.getItemProperty(
													TBC_DISCOUNT_PERCENTAGE)
											.getValue()));
									invObj.setDiscount(roundNumber((Double) item
											.getItemProperty(TBC_DISCOUNT)
											.getValue()));
								}

								// invObj.setGrade_id(0);
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								// invObj.setLocation_id(0);
								// invObj.setLock_count(0);
								invObj.setOrder_child_id(0);
								invObj.setOrder_id(0);
								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
										.getValue());
								invObj.setQuantity_returned(0);
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setStock_id((Long) item.getItemProperty(
										TBC_STOCK_ID).getValue());
								invObj.setStock_ids("");
								if (taxEnable) {
									invObj.setTax(new TaxModel((Long) item
											.getItemProperty(TBC_TAX_ID)
											.getValue()));
									invObj.setTaxAmount((Double) item
											.getItemProperty(TBC_TAX_AMT)
											.getValue());
									invObj.setTaxPercentage((Double) item
											.getItemProperty(TBC_TAX_PERC)
											.getValue());
								} else {
									invObj.setTax(new TaxModel(1));
									invObj.setTaxAmount(0);
									invObj.setTaxPercentage(0);
								}
								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue());
								invObj.setBillType((Integer) item
										.getItemProperty(TBC_BILL_TYPE_ID)
										.getValue());

								itemsList.add(invObj);
							}

							salObj.setActive(true);
							salObj.setAmount(toDouble(grandTotalAmtTextField
									.getValue()));
							salObj.setCash_cheque((Long) cashChequeRadio
									.getValue());
							// salObj.setChequeDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							// salObj.setChequeNo("");
							salObj.setComments(comment.getValue());
							// salObj.setConversionRate(1);
							// salObj.setCredit_note(0);
							// salObj.setCurrency_id(getCurrencyID());
							salObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							salObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							// salObj.setDebit_note(0);
							// salObj.setDepartment_id(0);
							// salObj.setDivision_id(0);
							// salObj.setExpenseAmount(0);
							// salObj.setExpenseCreditAmount(0);
							salObj.setInventory_details_list(itemsList);
							// salObj.setLock_count(0);
							// salObj.setNetCurrencyId(new
							// CurrencyModel(getCurrencyID()));
							// salObj.setOffice(new
							// S_OfficeModel(getOfficeID()));
							// salObj.setPaid_by_payment(0);
							salObj.setPayment_amount(toDouble(payingAmountTextField
									.getValue()));
							// salObj.setPayment_credit(0);
							salObj.setPayment_done('Y');
							// salObj.setPaymentConversionRate(1);
							// salObj.setPayment_credit(1);
							salObj.setRef_no(refNoField.getValue());
							salObj.setResponsible_employee((Long) employSelect
									.getValue());
							salObj.setSales_account(settings.getSALES_ACCOUNT());
							salObj.setSales_expense_list(null);
							salObj.setDiscount_type((Integer) discountRadio
									.getValue());
							salObj.setDiscountPercentage(roundNumber(toDouble(discountPercentField
									.getValue().trim())));
							salObj.setDiscountAmount(roundNumber(toDouble(discountAmountField
									.getValue().trim())));
							// salObj.setSales_number(getNextSequence("Sales Number",
							// getLoginID())+"");
							// salObj.setSales_type((Long)
							// salesTypeSelect.getValue());
							// salObj.setStatus(1);

							FinTransaction trans = new FinTransaction();
							double totalAmt = toDouble(grandTotalAmtTextField
									.getValue());
							double netAmt = totalAmt;

							double amt = 0;

							double payingAmt = toDouble(payingAmountTextField
									.getValue());

							// if (toDouble(payingAmountTextField.getValue()
							// .toString()) > 0) {
							// trans.addTransaction(
							// SConstants.DR,
							// (Long) customerSelect.getValue(),
							// settings.getCASH_ACCOUNT(),
							// roundNumber(toDouble(payingAmountTextField
							// .getValue().toString())));
							// }

							double payingAmount = toDouble(payingAmountTextField
									.getValue());
							double paymentPaidAmount;
							double transferAmount;
							if (payingAmount > 0) {
								List<SalesPaymentModeDetailsModel> paymentModeList = new ArrayList<SalesPaymentModeDetailsModel>();
								SalesPaymentModeDetailsModel paymentMode;
								it = paymentAmountTable.getItemIds().iterator();
								STextField field;
								while (it.hasNext()) {
									item = paymentAmountTable.getItem(it.next());
									field = (STextField) item.getItemProperty(
											TBP_PAYMENT_AMOUNT_FIELD)
											.getValue();
									if (toDouble(field.getValue()) == 0
											|| payingAmount == 0) {
										continue;
									}
									paymentPaidAmount = toDouble(field
											.getValue());

									paymentMode = new SalesPaymentModeDetailsModel();
									paymentMode.setPaymentMode(paymentModeDao
											.getPaymentModeModel((Long) item
													.getItemProperty(
															TBP_PAYMENT_MODE_ID)
													.getValue()));
									paymentMode.setAmount(paymentPaidAmount);

									paymentModeList.add(paymentMode);

									if (paymentPaidAmount > payingAmount) {
										transferAmount = payingAmount;
										payingAmount = 0;
									} else {
										transferAmount = paymentPaidAmount;
										payingAmount -= paymentPaidAmount;
									}

									if (paymentMode.getPaymentMode()
											.getTransactionType() == SConstants.INWARD) {
										trans.addTransaction(SConstants.DR,
												(Long) customerSelect
														.getValue(),
												paymentMode.getPaymentMode()
														.getLedger().getId(),
												transferAmount);

										trans.addTransaction(SConstants.DR,
												settings.getSALES_ACCOUNT(),
												(Long) customerSelect
														.getValue(),
												transferAmount);
									} else {
										trans.addTransaction(SConstants.CR,
												paymentMode.getPaymentMode()
														.getLedger().getId(),
												(Long) customerSelect
														.getValue(),
												transferAmount);

										trans.addTransaction(SConstants.CR,
												(Long) customerSelect
														.getValue(), settings
														.getSALES_ACCOUNT(),
												transferAmount);
									}

								}

								salObj.setSales_payment_mode_list(paymentModeList);

							} else {
								salObj.setSales_payment_mode_list(null);
							}
							// =============================

							// if (paymentAmountTextField.getValue() > 0) {
							// List<SalesPaymentModeDetailsModel>
							// paymentModeList = new
							// ArrayList<SalesPaymentModeDetailsModel>();
							// SalesPaymentModeDetailsModel paymentMode;
							// it = paymentAmountTable.getItemIds().iterator();
							// STextField field;
							// while (it.hasNext()) {
							// item = paymentAmountTable.getItem(it.next());
							// field =
							// (STextField)item.getItemProperty(TBP_PAYMENT_AMOUNT_FIELD).getValue();
							// if(toDouble(field.getValue()) == 0){
							// continue;
							// }
							// paymentMode = new SalesPaymentModeDetailsModel();
							// paymentMode.setPaymentMode(paymentModeDao
							// .getPaymentModeModel((Long)item.getItemProperty(TBP_PAYMENT_MODE_ID).getValue()));
							// paymentMode.setAmount(toDouble(field.getValue()));
							//
							// paymentModeList.add(paymentMode);
							//
							// if(paymentMode.getPaymentMode().getTransactionType()
							// == SConstants.INWARD){
							// trans.addTransaction(
							// SConstants.DR,
							// (Long) customerSelect.getValue(),
							// paymentMode.getPaymentMode().getLedger().getId(),
							// paymentMode.getAmount());
							//
							// trans.addTransaction(SConstants.DR, settings
							// .getSALES_ACCOUNT(), (Long) customerSelect
							// .getValue(),
							// paymentMode.getAmount());
							// } else {
							// trans.addTransaction(
							// SConstants.CR,
							// paymentMode.getPaymentMode().getLedger().getId(),
							// (Long) customerSelect.getValue(),
							// paymentMode.getAmount());
							//
							// trans.addTransaction(SConstants.CR,
							// (Long) customerSelect.getValue(),
							// settings.getSALES_ACCOUNT(),
							// paymentMode.getAmount());
							// }
							// }
							//
							// salObj.setSales_payment_mode_list(paymentModeList);
							//
							// } else {
							// salObj.setSales_payment_mode_list(null);
							// }
							// trans.addTransaction(SConstants.DR, settings
							// .getSALES_ACCOUNT(), (Long) customerSelect
							// .getValue(),
							// roundNumber(toDouble(payingAmountTextField
							// .getValue().toString())));

							// if (settings.getSALES_TAX_ACCOUNT() != 0) {
							// amt = toDouble(table
							// .getColumnFooter(TBC_TAX_AMT));
							// if (amt != 0) {
							// trans.addTransaction(
							// SConstants.CR,
							// settings.getSALES_TAX_ACCOUNT(),
							// settings.getCGS_ACCOUNT(),
							// roundNumber(amt));
							// totalAmt -= amt;
							// }
							// }

							// if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
							// amt = 0;
							// if (amt != 0) {
							// trans.addTransaction(
							// SConstants.CR,
							// settings.getSALES_DESCOUNT_ACCOUNT(),
							// settings.getCGS_ACCOUNT(),
							// roundNumber(amt));
							// totalAmt -= amt;
							// }
							// }

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
											+ salObj.getAmount(), salObj
											.getId());

							loadSale(salObj.getId());

							Notification.show(
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
							printButton.click();
							
							barcodeField.focus();
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (salesNumberList.getValue() != null	&& !salesNumberList.getValue().toString()
									.equals("0")) {
						ConfirmDialog.show(getUI(),	getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) salesNumberList.getValue());

												saveActivity(getOptionId(),
														"Sales Deleted. Bill No : "
																+ salesNumberList.getItemCaption(salesNumberList.getValue())
																+ ", Customer : "
																+ customerSelect.getItemCaption(customerSelect.getValue()),
																	(Long) salesNumberList.getValue());

												Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
												loadSale(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification.show(getPropertyName("error"),	Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}else{
						boolean isHoldExist=false;
						try {
							isHoldExist = holdSalesDao.isExist(date.getValue(), getOfficeID(), holdSalesId);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						if(isHoldExist){
							ConfirmDialog.show(getUI(),	getPropertyName("are_you_sure"),
									new ConfirmDialog.Listener() {
										public void onClose(ConfirmDialog dialog) {
											if (dialog.isConfirmed()) {
												try {
													holdSalesDao.delete(holdSalesId);
													Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
													loadSale(0);
												} catch (Exception e) {
													e.printStackTrace();
													Notification.show(getPropertyName("error"),	Type.ERROR_MESSAGE);
												}
											}
										}
									});
								}
							}
						}
					});

			cancelButton.addClickListener(new Button.ClickListener() {
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
																				.getValue()),
														(Long) salesNumberList
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

			table.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

					itemSelectCombo.setReadOnly(false);
					// unitSelect.setReadOnly(false);

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}
					itemDiscountRadio.setValue(null);
					itemDiscountRadio.setValue(1);
					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = table.getItem(selectedItems.iterator()
								.next());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");
						categorySelectList.setValue((long) 0);

						itemSelectCombo.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());
						itemComboField.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());

						if (((Integer) item.getItemProperty(TBC_BILL_TYPE_ID)
								.getValue()) == SConstants.BILL_TYPE_NORMAL) {
							netPriceTextField.setNewValue(""
									+ item.getItemProperty(TBC_NET_PRICE)
											.getValue());
							quantityTextField.setValue(""
									+ item.getItemProperty(TBC_QTY).getValue());
							unitPriceTextField.setNewValue(""
									+ item.getItemProperty(TBC_UNIT_PRICE)
											.getValue());
						} else {
							netPriceTextField.setNewValue(""
									+ (-1)
									* (Double) item.getItemProperty(
											TBC_NET_PRICE).getValue());
							quantityTextField.setValue(""
									+ (-1)
									* (Double) item.getItemProperty(TBC_QTY)
											.getValue());

							unitPriceTextField.setNewValue(""
									+ (Double) item.getItemProperty(
											TBC_UNIT_PRICE).getValue());
						}

						visibleAddupdateSalesButton(false, true);

						itemSelectCombo.focus();

						if ((Long) item.getItemProperty(TBC_PO_ID).getValue() > 0) {
							// itemSelectCombo.setReadOnly(true);
							// unitSelect.setReadOnly(true);
							quantityTextField.focus();
						}

						unitSelect.setNewValue(item
								.getItemProperty(TBC_UNIT_ID).getValue());

						if (taxEnable) {
							taxSelect.setValue(item.getItemProperty(TBC_TAX_ID)
									.getValue());
						}
						if (isDiscountEnable()) {
							itemDiscountRadio.setValue((Integer) item
									.getItemProperty(TBC_DISCOUNT_TYPE)
									.getValue());
							itemDiscountPercentField.setValue((Double) item
									.getItemProperty(TBC_DISCOUNT_PERCENTAGE)
									.getValue()
									+ "");
							itemDiscountAmountField.setNewValue(Math
									.abs((Double) item.getItemProperty(
											TBC_DISCOUNT).getValue())
									+ "");
						}
						stockSelectList.setValue(item.getItemProperty(
								TBC_STOCK_ID).getValue());
						convertionQtyTextField.setNewValue(""
								+ item.getItemProperty(TBC_CONVERTION_QTY)
										.getValue());

						billTypeField.setValue(item.getItemProperty(
								TBC_BILL_TYPE_ID).getValue());
						// paymentModeCombo.setValue(item.getItemProperty(TBC_PAYMENT_MODE_ID).getValue());
						// paymentAmountTextField.setValue((Double)item.getItemProperty(TBC_PAYMENT_AMOUNT).getValue());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

						quantityTextField.focus();

						focusType = 2;

					} else {

						itemSelectCombo.setValue(null);
						itemComboField.setValue(null);
						itemSelectCombo.focus();
						quantityTextField.setValue("1");
						keyboardTextField.setValue("0.0");
						unitPriceTextField.setNewValue("0.0");
						netPriceTextField.setNewValue("0.0");
						itemDiscountRadio.setValue(null);
						itemDiscountRadio.setValue(1);
						convertionQtyTextField.setValue("1");
						paymentModeCombo.setValue(null);
						// paymentAmountTextField.setValue(0.00);

						visibleAddupdateSalesButton(true, false);

						// itemSelectCombo.focus();
						focusType = 1;

						barcodeField.focus();
					}
					barcodeField.setValue("");
				}

			});

			addItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("rawtypes")
				public void buttonClick(ClickEvent event) {
					try {

						if (table.getComponentError() != null)
							setRequiredError(table, null, false);

						setRequiredError(unitPriceTextField, null, false);
						setRequiredError(quantityTextField, null, false);

						if (isAddingValid()) {

							ItemModel itm = itmDao
									.getItem((Long) itemComboField.getValue());
							Item item;
							List delList = new ArrayList();
							double qty, price, discount, tax_amt, tax_perc, total, conv_rat, discPer = 0;
							price = 0;
							qty = 0;
							total = 0;
							double discount_amt = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discPer = toDouble(itemDiscountPercentField
									.getValue());
							discount_amt = toDouble(itemDiscountAmountField
									.getValue().trim());
							netPriceTextField
									.setNewValue(roundNumberToString(price * qty));

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

							int ct = 0;

							// double conv_rat =
							// comDao.getConvertionRate(itm
							// .getId(), objUnit.getId(),
							// toInt(salesTypeSelect.getValue()
							// .toString()));

							conv_rat = toDouble(convertionQtyTextField
									.getValue());
							int tableValue = isAlreadyDataExist(itm.getId(), toInt(billTypeField.getValue().toString()));
							if(tableValue == 0){
								if (toInt(billTypeField.getValue().toString()) == SConstants.BILL_TYPE_NORMAL) {
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													itm.getId(),
													itm.getName() + " ("
															+ itm.getItem_code()
															+ ")",
													qty,
													objUnit.getId(),
													objUnit.getSymbol(),
													toDouble(unitPriceTextField
															.getValue()),
													objTax.getId(),
													tax_amt,
													tax_perc,
													(Integer) itemDiscountRadio
															.getValue(),
													roundNumber(discPer),
													roundNumber(discount_amt),
													total,
													(long) 0,
													(long) 0,
													roundNumber(total + tax_amt),
													roundNumber(total + tax_amt
															- discount_amt),
													conv_rat * qty,
													(Long) stockSelectList
															.getValue(),
													conv_rat,
													(Integer) billTypeField
															.getValue() }, table
													.getItemIds().size() + 1);
								} else {
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													itm.getId(),
													itm.getName() + " ("
															+ itm.getItem_code()
															+ ")",
													-qty,
													objUnit.getId(),
													objUnit.getSymbol(),
													toDouble(unitPriceTextField
															.getValue()),
													objTax.getId(),
													-tax_amt,
													tax_perc,
													(Integer) itemDiscountRadio
															.getValue(),
													roundNumber(discPer),
													-roundNumber(discount_amt),
													-total,
													(long) 0,
													(long) 0,
													-roundNumber(total + tax_amt),
													-roundNumber(total + tax_amt
															- discount_amt),
													-conv_rat * qty,
													(Long) stockSelectList
															.getValue(),
													-conv_rat,
													(Integer) billTypeField
															.getValue() }, table
													.getItemIds().size() + 1);
								}
							} else {
								
								item = table.getItem(tableValue);
								qty += Math.abs((Double)item.getItemProperty(TBC_QTY).getValue());
								tax_amt += Math.abs((Double)item.getItemProperty(TBC_TAX_AMT).getValue());
								total += Math.abs((Double)item.getItemProperty(TBC_NET_PRICE).getValue());
								discount_amt += Math.abs((Double)item.getItemProperty(TBC_DISCOUNT).getValue());
								
								if ((Integer) billTypeField.getValue() == SConstants.BILL_TYPE_NORMAL) {
//									qty += (Double)item.getItemProperty(TBC_QTY).getValue();
//									tax_amt += (Double)item.getItemProperty(TBC_TAX_AMT).getValue();
//									total += (Double)item.getItemProperty(TBC_NET_PRICE).getValue();
//									discount_amt += (Double)item.getItemProperty(TBC_DISCOUNT).getValue();
							//		total += (Double)item.getItemProperty(TBC_NET_TOTAL).getValue();
								//	total += (Double)item.getItemProperty(TBC_NET_FINAL).getValue();
									
									
									item.getItemProperty(TBC_QTY).setValue(qty);
									item.getItemProperty(TBC_TAX_AMT).setValue(
											tax_amt);
									item.getItemProperty(TBC_NET_PRICE).setValue(
											total);
									item.getItemProperty(TBC_DISCOUNT).setValue(
											roundNumber(discount_amt));
									item.getItemProperty(TBC_NET_TOTAL)
											.setValue(
													roundNumber(total + tax_amt));
									item.getItemProperty(TBC_NET_FINAL).setValue(
											roundNumber(total + tax_amt  - discount_amt));
								} else {											
									item.getItemProperty(TBC_QTY).setValue(-qty);
									item.getItemProperty(TBC_TAX_AMT).setValue(
											-tax_amt);
									item.getItemProperty(TBC_NET_PRICE).setValue(
											-total);
									item.getItemProperty(TBC_DISCOUNT).setValue(
											-roundNumber(discount_amt));
									item.getItemProperty(TBC_NET_TOTAL)
											.setValue(
													-roundNumber(total + tax_amt
															));
									item.getItemProperty(TBC_NET_FINAL).setValue(
											-roundNumber(total + tax_amt 
													- discount_amt));
								}

								item.getItemProperty(TBC_UNIT_ID).setValue(
										objUnit.getId());
								item.getItemProperty(TBC_UNIT).setValue(
										objUnit.getSymbol());
								item.getItemProperty(TBC_UNIT_PRICE).setValue(
										toDouble(unitPriceTextField.getValue()));
								item.getItemProperty(TBC_TAX_ID).setValue(
										objTax.getId());

								item.getItemProperty(TBC_TAX_PERC).setValue(
										tax_perc);

								item.getItemProperty(TBC_DISCOUNT_TYPE).setValue(
										(Integer) itemDiscountRadio.getValue());
								item.getItemProperty(TBC_DISCOUNT_PERCENTAGE)
										.setValue(roundNumber(discPer));

							

								item.getItemProperty(TBC_CONVERTION_QTY)
										.setValue((Double)item.getItemProperty(TBC_CONVERTION_QTY).getValue() +
												toDouble(convertionQtyTextField
														.getValue()));
							

								conv_rat = toDouble(convertionQtyTextField
										.getValue());

								item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
										.setValue(conv_rat * qty);
							}

							

							table.setVisibleColumns(requiredHeaders);

							itemSelectCombo.setValue(null);
							itemComboField.setValue(null);
							quantityTextField.setValue("1");
							keyboardTextField.setValue("0.0");
							unitPriceTextField.setNewValue("0.0");
							netPriceTextField.setNewValue("0.0");
							itemDiscountRadio.setValue(null);
							itemDiscountRadio.setValue(1);
							// billTypeField.setValue(SConstants.BILL_TYPE_NORMAL);
							paymentModeCombo.setValue(null);
							// paymentAmountTextField.setValue(0.00);

							focusType = 1;
						} else {
							System.out
									.println("===============IS NOT VALID ==============================");
						}
						/*
						 * else { itemSelectCombo.setValue(null);
						 * itemComboField.setValue(null);
						 * quantityTextField.setValue("1");
						 * keyboardTextField.setValue("0.0");
						 * unitPriceTextField.setNewValue("0.0");
						 * netPriceTextField.setNewValue("0.0");
						 * itemDiscountRadio.setValue(null);
						 * itemDiscountRadio.setValue(1); }
						 */
						calculateTotals();

						itemSelectCombo.focus();

						enterBarcodeButton.setValue(false);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
					if (settings.isBARCODE_ENABLED()) {
						barcodeField.focus();
						barcodeField.setValue("");
					}

				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isUpdatingValid()) {

							ItemModel itm = itmDao
									.getItem((Long) itemComboField.getValue());

							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());

							double price = 0, qty = 0, total = 0, discount_amt = 0, discPer = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discount_amt = toDouble(itemDiscountAmountField.getValue());
							discPer = toDouble(itemDiscountPercentField.getValue());

							netPriceTextField.setNewValue(roundNumberToString(roundNumber(price
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
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									itm.getName() + " (" + itm.getItem_code()
											+ ")");
							if ((Integer) billTypeField.getValue() == SConstants.BILL_TYPE_NORMAL) {
								item.getItemProperty(TBC_QTY).setValue(qty);
								item.getItemProperty(TBC_TAX_AMT).setValue(
										tax_amt);
								item.getItemProperty(TBC_NET_PRICE).setValue(
										total);
								item.getItemProperty(TBC_DISCOUNT).setValue(
										roundNumber(discount_amt));
								item.getItemProperty(TBC_NET_TOTAL)
										.setValue(
												roundNumber(total + tax_amt
														+ cess_amt));
								item.getItemProperty(TBC_NET_FINAL).setValue(
										roundNumber(total + tax_amt + cess_amt
												- discount_amt));
							} else {
								item.getItemProperty(TBC_QTY).setValue(-qty);
								item.getItemProperty(TBC_TAX_AMT).setValue(
										-tax_amt);
								item.getItemProperty(TBC_NET_PRICE).setValue(
										-total);
								item.getItemProperty(TBC_DISCOUNT).setValue(
										-roundNumber(discount_amt));
								item.getItemProperty(TBC_NET_TOTAL)
										.setValue(
												-roundNumber(total + tax_amt
														+ cess_amt));
								item.getItemProperty(TBC_NET_FINAL).setValue(
										-roundNumber(total + tax_amt + cess_amt
												- discount_amt));
							}

							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());
							item.getItemProperty(TBC_UNIT_PRICE).setValue(
									toDouble(unitPriceTextField.getValue()));
							item.getItemProperty(TBC_TAX_ID).setValue(
									objTax.getId());

							item.getItemProperty(TBC_TAX_PERC).setValue(
									tax_perc);

							item.getItemProperty(TBC_DISCOUNT_TYPE).setValue(
									(Integer) itemDiscountRadio.getValue());
							item.getItemProperty(TBC_DISCOUNT_PERCENTAGE)
									.setValue(roundNumber(discPer));

							item.getItemProperty(TBC_BILL_TYPE_ID).setValue(
									(Integer) billTypeField.getValue());
						

							if (stockSelectList.getValue() != null)
								item.getItemProperty(TBC_STOCK_ID).setValue(
										(Long) stockSelectList.getValue());

							item.getItemProperty(TBC_CONVERTION_QTY)
									.setValue(
											toDouble(convertionQtyTextField
													.getValue()));

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

							itemSelectCombo.setValue(null);
							itemComboField.setValue(null);
							quantityTextField.setValue("1");
							keyboardTextField.setValue("0.0");
							unitPriceTextField.setNewValue("0.0");
							netPriceTextField.setNewValue("0.0");
							itemDiscountRadio.setValue(null);
							itemDiscountRadio.setValue(1);

							visibleAddupdateSalesButton(true, false);

							itemSelectCombo.focus();

							table.setValue(null);

							calculateTotals();

							focusType = 1;

							enterBarcodeButton.setValue(false);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
					if (settings.isBARCODE_ENABLED()) {
						barcodeField.focus();
						barcodeField.setValue("");
					}
				}
			});

			deleteItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					deleteItem();
					focusType = 1;
				}
			});

			newEntryButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					table.setValue(null);
					itemSelectCombo.setValue(null);
					itemComboField.setValue(null);
					itemSelectCombo.focus();
					quantityTextField.setValue("1");
					keyboardTextField.setValue("0.0");
					unitPriceTextField.setNewValue("0.0");
					netPriceTextField.setNewValue("0.0");
					itemDiscountRadio.setValue(null);
					itemDiscountRadio.setValue(1);
					convertionQtyTextField.setValue("1");

					visibleAddupdateSalesButton(true, false);

					focusType = 1;

					barcodeField.focus();
					barcodeField.setValue("");
				}
			});

			categorySelectList
					.addValueChangeListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							SCollectionContainer bic = null;
							try {
								if (categorySelectList.getValue() != null)
									bic = SCollectionContainer.setList(
											itmDao.getAllActiveItemsWithAppendingItemCode(
													getOfficeID(),
													(Long) categorySelectList
															.getValue(), 0),
											"id");
							} catch (Exception e) {
								e.printStackTrace();
							}
							itemSelectCombo.setContainerDataSource(bic);
							itemSelectCombo.setItemCaptionPropertyId("name");
							if (settings.isBARCODE_ENABLED()) {
								barcodeField.focus();
								barcodeField.setValue("");
							}
						}
					});
			categorySelectList.setValue((long) 0);

			itemSelectCombo.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					itemComboField.setValue(itemSelectCombo.getValue());
				}
			});

			itemComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {

					try {
						if (itemComboField.getValue() != null) {

							// creditPeriodTextField.removeFocusListener(focusListener);
							// refNoField.removeFocusListener(focusListener);
							// unitPriceTextField.removeFocusListener(focusListener);
							// convertionQtyTextField.removeFocusListener(focusListener);

							ItemModel itm = itmDao
									.getItem((Long) itemComboField.getValue());

							SCollectionContainer bic = SCollectionContainer.setList(
									new UnitDao()
											.getAllActiveUnits(getOrganizationID()),
									"id");
							unitSelect.setReadOnly(false);
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							if (taxEnable) {
								taxSelect.setValue(itm.getSalesTax().getId());
							}

							unitSelect.setNewValue(itm.getUnit().getId());
							unitSelect.setReadOnly(true);

							List lst = comDao.getStocks(
									(Long) itemComboField.getValue(),
									settings.isUSE_SALES_RATE_FROM_STOCK());

							SCollectionContainer bic1 = SCollectionContainer
									.setList(lst, "id");
							stockSelectList.setContainerDataSource(bic1);
							stockSelectList
									.setItemCaptionPropertyId("stock_details");

							long stk_id = comDao
									.getDefaultStockToSelect((Long) itemComboField
											.getValue());

							if (stk_id != 0)
								stockSelectList.setValue(stk_id);
							else {
								Iterator it = stockSelectList.getItemIds()
										.iterator();
								if (it.hasNext())
									stockSelectList.setValue(it.next());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!", Type.ERROR_MESSAGE);
					}

				}
			});

			unitSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (unitSelect.getValue() != null) {
									if (itemComboField.getValue() != null) {

										ItemModel itm = itmDao
												.getItem((Long) itemComboField
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
													.setCaption("Conv. Qty");
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

										unitPriceTextField.setNewValue(roundNumberToString(comDao
												.getItemPrice(itm.getId(),
														(Long) unitSelect
																.getValue(),
														toInt(salesTypeSelect
																.getValue()
																.toString()))));

										if (quantityTextField.getValue() != null
												&& !quantityTextField
														.getValue().equals("")) {

											convertedQtyTextField.setNewValue(asString(Double
													.parseDouble(quantityTextField
															.getValue())
													* Double.parseDouble(convertionQtyTextField
															.getValue())));

											netPriceTextField.setNewValue(roundNumberToString(Double
													.parseDouble(unitPriceTextField
															.getValue())
													* Double.parseDouble(quantityTextField
															.getValue())));
											// paymentAmountTextField.setValue(toDouble(netPriceTextField.getValue()));
										}

									}
								} else {
									convertionQtyTextField.setValue("1");
									convertionQtyTextField.setVisible(false);
									convertedQtyTextField.setVisible(false);
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

						SalesModel salObj = daoObj
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
						map.put("SALES_BILL_NO", toLong(salesNumberList
								.getItemCaption(salesNumberList.getValue())));
						map.put("SALES_MAN", usrDao
								.getUserNameFromLoginID(salObj
										.getResponsible_employee()));
						map.put("BILL_DATE", CommonUtil
								.formatDateToDDMMMYYYY(salObj.getDate()));
						map.put("roundOffDisc",
								toDouble(discountAmountField.getValue()));
						map.put("paidAmount", paymentAmountTextField.getValue());
						map.put("CURRENCY", salObj.getOffice().getCurrency()
								.getCode());
						map.put("balanceAmount",
								toDouble(balanceAmountTextField.getValue()));

						String basePath = VaadinServlet.getCurrent()
								.getServletContext().getRealPath("/")
								+ "VAADIN/themes/testappstheme/OrganizationLogos/";
						File file = new File(basePath + salObj.getOffice().getOrganization().getLogoName());
						if (file == null || !file.exists())
							map.put("HEADER_DIR", basePath + "BaseLogo.png");
						else
							map.put("HEADER_DIR", basePath
									+ salObj.getOffice().getOrganization().getLogoName() );

						SalesInventoryDetailsModel invObj;
						Iterator<SalesInventoryDetailsModel> itr1 = salObj
								.getInventory_details_list().iterator();
						while (itr1.hasNext()) {
							invObj = itr1.next();

							bean = new SalesPrintBean();
							bean.setItem(invObj.getItem().getName());
							bean.setRate(invObj.getUnit_price());
							bean.setQuantity(invObj.getQunatity());
							bean.setDiscount(invObj.getDiscount());
							bean.setTotal((invObj.getQunatity() * invObj
									.getUnit_price()) - invObj.getDiscount());

							total += bean.getTotal();

							reportList.add(bean);
						}

						SalesPaymentModeDetailsModel detModel;
						Iterator<SalesPaymentModeDetailsModel> itr = salObj
								.getSales_payment_mode_list().iterator();
						StringBuffer paymentModeDetails = new StringBuffer();
						// paymentModeDetails.append("<table>");
						while (itr.hasNext()) {
							detModel = itr.next();
							paymentModeDetails.append(detModel.getPaymentMode()
									.getDescription()
									+ " : "
									+ detModel.getAmount() + "<br>");
						}
						// paymentModeDetails.append("</table>");
						map.put("paymentModeDetails",
								paymentModeDetails.toString());
						// ===================================================

						// map.put("CUSTOMER_NAME", customerModel.getName());
						// map.put("CUSTOMER_ADDRESS", address);
						//
						//
						//
						// map.put("ORGANIZATION", salObj.getOffice()
						// .getOrganization().getName());
						//
						//
						//
						// String resp = "";
						// if (salObj.getResponsible_employee() != 0) {
						// UserModel usrObj = usrDao.getUserFromLogin(salObj
						// .getResponsible_employee());
						//
						// if (usrObj != null) {
						// resp = usrObj.getFirst_name();
						//
						// if (usrObj.getAddress() != null) {
						// if (usrObj.getAddress().getMobile() != null
						// && !usrObj.getAddress().getMobile()
						// .equals(""))
						// resp += " Mob: "
						// + usrObj.getAddress()
						// .getMobile();
						// if (usrObj.getAddress().getPhone() != null
						// && !usrObj.getAddress().getPhone()
						// .equals(""))
						// resp += " Ph: "
						// + usrObj.getAddress()
						// .getPhone();
						// }
						// }
						// }
						// map.put("RESPONSIBLE_PERSON", resp);

						String type = "";
						if (status == 1) {
							type = "Cash Sale";
						} else {
							type = "Credit Sale";
						}
						map.put("SALES_TYPE", type);
						map.put("OFFICE_NAME", customerModel.getLedger()
								.getOffice().getName());
						
						map.put("ARABIC_FOOTER", "شكراً لتسوقكم معنا مدة التبديل والإسترجاع 15 يوم على أن يكون المنتج غير مستخدم و مرفق بالفاتورة الأصلية" );

						// /* S_OfficeModel officeModel = new OfficeDao()
						// .getOffice(getOfficeID());
						// map.put("AMOUNT_IN_WORDS", getAmountInWords(total));
						// map.put("OFFICE_ADDRESS",
						// new AddressDao().getAddressString(salObj
						// .getOffice().getAddress().getId()));
						//
						// map.put("PAID_AMOUNT", salObj.getPayment_amount());
						// map.put("REF_NO", salObj.getRef_no());
						// map.put("CUR_DATE", getFormattedTime(new Date()));
						// String adr1 = "", adr2 = "";
						// if (salObj.getOffice().getAddress() != null) {
						//
						// adr1 += salObj.getOffice().getAddress()
						// .getCountry().getName();
						//
						// if (salObj.getOffice().getAddress().getPhone() !=
						// null
						// && salObj.getOffice().getAddress()
						// .getPhone().length() > 0)
						// adr2 += "Tel : "
						// + salObj.getOffice().getAddress()
						// .getPhone() + "   ";
						//
						// }
						// map.put("ADDRESS1", adr1);
						// map.put("ADDRESS2", adr2);
						// map.put("OFFICE",
						// customerModel.getLedger().getOffice()
						// .getName());*/

						Report report = new Report(getLoginID());
						report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
						report.setReportFileName("SalesPrint");
						// report.setReportTitle("Sales Invoice");
						// report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						report.createReport(reportList, map);

						report.print();
						report.printReport();
						
						newSaleButton.click();

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
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			unitPriceTextField.setImmediate(true);

			// unitPriceTextField.addListener(new Property.ValueChangeListener()
			// {
			// public void valueChange(ValueChangeEvent event) {
			// try {
			// popKeyboad.setPopupVisible(true);
			// } catch (Exception e) {
			// e.printStackTrace();
			// Notification.show("Error..!!", "Error Message :" +
			// e.getCause(),Type.ERROR_MESSAGE);
			// }
			//
			// }
			// });

			quantityTextField.setImmediate(true);

			quantityTextField.addValueChangeListener(new ValueChangeListener() {
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

			convertionQtyTextField
					.addValueChangeListener(new ValueChangeListener() {
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

			if (isSaleEditable()) {
				updateButton.setEnabled(true);
			} else
				updateButton.setEnabled(false);

			barcodeField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						String barcode = barcodeField.getValue();

						if (barcode.trim().length() > 0) {
							ItemStockModel mdl = null;
							ItemModel itemModel = null;
							if(settings.getBARCODE_TYPE()==SConstants.barcode_types.CUSTOMER_SPECIFIC){
								if(employSelect.getValue()!=null)
									mdl = daoObj.getNewItemStock(barcode,(Long)employSelect.getValue());
							}else if(settings.getBARCODE_TYPE()==SConstants.barcode_types.STOCK_SPECIFIC){
								mdl = daoObj.getItemFromBarcode(barcode);
							}else if(settings.getBARCODE_TYPE()==SConstants.barcode_types.ITEM_SPECIFIC){
								itemModel = daoObj.getItemUsingItemSpecific(barcode, getOfficeID());
							}
								

							 
							if (mdl != null) {
								categorySelectList.setValue((long) 0);
								itemSelectCombo.setValue(mdl.getItem().getId());
								itemComboField.setValue(mdl.getItem().getId());
								stockSelectList.setValue(mdl.getId());
								barcodeField.setValue("");
							} else if(itemModel != null){
								categorySelectList.setValue((long) 0);
								itemSelectCombo.setValue(itemModel.getId());
								itemComboField.setValue(itemModel.getId());
							//	stockSelectList.setValue((long)0);
								barcodeField.setValue("");
							} else {
								categorySelectList.setValue((long) 0);
								itemSelectCombo.setValue(null);
								itemComboField.setValue(null);
								barcodeField.setValue("");
								barcodeField.focus();
							}
						} else {
							barcodeField.setValue("");
							barcodeField.focus();
						}
					} catch (Exception e) {
						barcodeField.setValue("");
						barcodeField.focus();
					}
				}
			});

			creditPeriodTextField.setImmediate(true);
			refNoField.setImmediate(true);

			// creditPeriodTextField.setId("1");
			// refNoField.setId("1");
			// payingAmountTextField.setId("1");

			// creditPeriodTextField.addFocusListener(focusListener);
			// refNoField.addFocusListener(focusListener);
			// unitPriceTextField.addFocusListener(focusListener);
			// quantityTextField.addFocusListener(focusListener);
			// convertionQtyTextField.addFocusListener(focusListener);
			// payingAmountTextField.addFocusListener(focusListener);
			// barcodeField.addFocusListener(focusListener);

			employSelect.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					barcodeField.focus();
				}
			});

			customerSelect.setValue(settings.getDEFAULT_CUSTOMER());

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		paymentAmountValueChangeListner = new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Iterator itr = paymentAmountTable.getItemIds().iterator();
				double total = 0;
				while (itr.hasNext()) {
					Integer value = (Integer) itr.next();
					Item item = paymentAmountTable.getItem(value);
					STextField field = (STextField) item.getItemProperty(
							TBP_PAYMENT_AMOUNT_FIELD).getValue();
					if (field != null && field.getValue().trim().length() > 0) {
						total += toDouble(field.getValue());
					}
				}

				paymentAmountTable.setColumnFooter(TBP_PAYMENT_AMOUNT_FIELD,
						roundNumber(total) + "");
				paymentAmountTextField.setNewValue(roundNumber(total));

			}
		};
		paymentModeHashMap = new HashMap<Long, STextField>();
		paymentModePrintOrderArrayList = new ArrayList<Long>();
		addPaymentModeDetails();
		addPaymentModeButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				paymentAmountPopup.setPopupVisible(true);
				if (paymentAmountTable.getItemIds().size() > 0) {
					STextField field = ((STextField) paymentAmountTable
							.getItem(
									paymentAmountTable.getItemIds().iterator()
											.next())
							.getItemProperty(TBP_PAYMENT_AMOUNT_FIELD)
							.getValue());
					field.focus();
					field.selectAll();
				}
			}
		});
		hLayout.addShortcutListener(new ShortcutListener("Submit Item",
				ShortcutAction.KeyCode.SPACEBAR, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				
					
				if(target instanceof STextField){
					STextField field = (STextField)target;
					if(field.getId() == null){
						return;
					}
					if(field.getId().equals("barcodeField")){
						discountRadio.focus();
					}
				}
				
			}
		});
		addingGrid.addShortcutListener(new ShortcutListener("Submit Item",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (addItemButton.isVisible()) {
					addItemButton.click();
				} else {
					updateItemButton.click();
				}
//				if (target instanceof SButton) {
//					SButton field = (SButton) target;
//					if(field.getId() == null){
//						return;
//					}
					
//					if (field.getId().equals("addItemButton")
//							|| field.getId().equals("updateItemButton")) {
//						if (addItemButton.isVisible()) {
//							addItemButton.click();
//						} else {
//							updateItemButton.click();
//						}
//					}
						//else if(field.getId().equals("saveButton") || 
//							field.getId().equals("updateButton")){
//						printButton.focus();
//					//	newSaleButton.click();
//					} else if(field.getId().equals("printButton")){
//						newSaleButton.click();
//					//	newSaleButton.click();
//					}
//				} else if (target instanceof STextField) {
//					STextField field = (STextField) target;
//					if(field.getId() == null){
//						return;
//					}
//					if (field.getId().equals("barcodeField")) {
//						itemComboField.focus();
//					} else if (field.getId().equals("quantityTextField")) {
//						if (itemDiscountRadio.isVisible()) {
//							itemDiscountRadio.focus();
//						} else {
//							billTypeField.focus();
//						}
//					} else if (field.getId().equals("itemDiscountAmountField")
//							|| field.getId().equals("itemDiscountPercentField")) {
//						billTypeField.focus();
//					}else if (field.getId().equals("discountPercentField")
//							|| field.getId().equals("discountAmountField")) {
//						addPaymentModeButton.focus();
//						addPaymentModeButton.click();
//					}
//					
//					Iterator<Long> itr = paymentModePrintOrderArrayList.iterator();
//					long id;
//					while(itr.hasNext()){
//						id = itr.next(); 
//						if(field.getId().equals(paymentModeHashMap.get(id).getId())){
//							if(itr.hasNext()){
//								id = itr.next();
//								paymentModeHashMap.get(id).focus();
//								paymentModeHashMap.get(id).selectAll();
//								break;
//							} else {
//								paymentAmountPopup.setPopupVisible(false);
//								if(saveButton.isVisible()){
//									saveButton.focus();
//								} else if(updateButton.isVisible()){
//									updateButton.focus();
//								}								
//							}
//						}
//					}
//				} else if (target instanceof SComboField) {
//					SComboField field = (SComboField) target;
//					if (field.getId().equals("itemComboField")) {
//						quantityTextField.focus();
//						quantityTextField.selectAll();
//					}
//				} else if (target instanceof SRadioButton) {
//					SRadioButton field = (SRadioButton) target;
//					if (field.getId().equals("itemDiscountRadio")) {
//						if (!itemDiscountPercentField.isReadOnly()) {
//							itemDiscountPercentField.focus();
//							itemDiscountPercentField.selectAll();
//						} else if (!itemDiscountAmountField.isReadOnly()) {
//							itemDiscountAmountField.focus();
//							itemDiscountAmountField.selectAll();
//						}
//					} else if (field.getId().equals("billTypeField")) {
//						if (addItemButton.isVisible()) {
//							addItemButton.click();
//						} else {
//							updateItemButton.click();
//						}
//					} else if (field.getId().equals("discountRadio")) {
//						if (!discountPercentField.isReadOnly()) {
//							discountPercentField.focus();
//							discountPercentField.selectAll();
//						} else if (!discountAmountField.isReadOnly()) {
//							discountAmountField.focus();
//							discountAmountField.selectAll();
//						}
//					}
//				}
				
			}
		
			
		});
		
		

		return pannel;
	}

	protected int isAlreadyDataExist(long itemId, int billType) {
		Iterator itr = table.getItemIds().iterator();
		int value;
		while(itr.hasNext()){
			value = (Integer)itr.next();
			Item item = table.getItem(value);
			if((Long)item.getItemProperty(TBC_ITEM_ID).getValue() == itemId && 
					(Integer)item.getItemProperty(TBC_BILL_TYPE_ID).getValue() == billType){
				return value;
			}
		}
		return 0;
	}

	protected void resetPaymentModeTable() {
		Iterator itr = paymentAmountTable.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = paymentAmountTable.getItem(itr.next());
			STextField field = (STextField) item.getItemProperty(
					TBP_PAYMENT_AMOUNT_FIELD).getValue();
			field.setValue("0.00");
		}

	}

	private void addPaymentModeDetails() {
		paymentModeHashMap.clear();
		paymentModePrintOrderArrayList.clear();
		List list = getPaymentModeList();
		Iterator itr = list.iterator();
		int slNo = 1;
		paymentAmountTable
				.setVisibleColumns(new Object[] { TBP_SN, TBP_PAYMENT_MODE_ID,
						TBP_PAYMENT_MODE, TBP_PAYMENT_AMOUNT_FIELD });
		List<PaymentModeModel> cashModeIds = new ArrayList<PaymentModeModel>();
		while (itr.hasNext()) {
			PaymentModeModel model = (PaymentModeModel) itr.next();
			STextField amountField = new STextField();
			amountField.setValue("0.00");
			amountField.setStyleName("sup_textfield_align_right");
			amountField.setHeight("30px");
			amountField.addValueChangeListener(paymentAmountValueChangeListner);
			amountField.setId(model.getId()+"");

			paymentModeHashMap.put(model.getId(), amountField);

			if (settings.getCASH_ACCOUNT() == model.getLedger().getId()) {
				cashModeIds.add(model);
			} else {
				paymentAmountTable.addItem(new Object[] { slNo, model.getId(),
						model.getDescription(), amountField }, slNo);
				paymentModePrintOrderArrayList.add(model.getId());
			}
			slNo++;
		}

		itr = cashModeIds.iterator();
		while (itr.hasNext()) {
			PaymentModeModel model = (PaymentModeModel) itr.next();
			paymentAmountTable.addItem(
					new Object[] { slNo, model.getId(), model.getDescription(),
							paymentModeHashMap.get(model.getId()) },

					slNo);
			paymentModePrintOrderArrayList.add(model.getId());
			slNo++;
		}

		paymentAmountTable.setVisibleColumns(new Object[] { TBP_SN,
				TBP_PAYMENT_MODE, TBP_PAYMENT_AMOUNT_FIELD });

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getPaymentModeList() {

		List list = new ArrayList();
		try {
			list.addAll(paymentModeDao.getPaymentModeDetailsListByStatus(
					getOfficeID(), ACTIVE));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

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

			itemComboField.setContainerDataSource(bic);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(itemSelectCombo.getValue());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isAddingValidIncludingZero(STextField valueField) {
		boolean ret = true;
		try {

			if (valueField.getValue() == null
					|| valueField.getValue().equals("")) {
				setRequiredError(valueField, getPropertyName("invalid_data"),
						true);
				valueField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(valueField.getValue()) < 0) {
						setRequiredError(valueField,
								getPropertyName("invalid_data"), true);
						valueField.focus();
						ret = false;
					} else
						setRequiredError(valueField, null, false);
				} catch (Exception e) {
					setRequiredError(valueField,
							getPropertyName("invalid_data"), true);
					valueField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	protected void resetUnit() {
		try {
			Object temp = unitSelect.getValue();
			unitSelect.setReadOnly(false);
			if (itemComboField.getValue() != null) {
				ItemModel itm = itmDao
						.getItem((Long) itemComboField.getValue());

				List lst = new ArrayList();
				lst.addAll(new UnitDao().getAllActiveUnits(getOrganizationID()));
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");

				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

				unitSelect.setNewValue(null);
				unitSelect.setNewValue(temp);

			} else {

				List lst = new ArrayList();
				lst.addAll(new UnitDao().getAllActiveUnits(getOrganizationID()));
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

			}
			unitSelect.setReadOnly(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc = 0;

		try {
			try {
				unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			} catch (Exception e) {
				unitPrc = 0;
			}
			try {
				qty = Double.parseDouble(quantityTextField.getValue());
			} catch (Exception e) {
				qty = 0;
			}
			if ((Integer) itemDiscountRadio.getValue() == 1) {
				double discPer = 0;
				try {
					discPer = Double.parseDouble(itemDiscountPercentField
							.getValue());
				} catch (Exception e) {
					discPer = 0;
				}
				disc = roundNumber((unitPrc * qty) * discPer / 100);
			} else {
				try {
					disc = Double.parseDouble(itemDiscountAmountField
							.getValue());
				} catch (Exception e) {
					disc = 0;
				}
			}
			convertedQtyTextField.setNewValue(asString(qty
					* Double.parseDouble(convertionQtyTextField.getValue())));
		} catch (Exception e) {

		}
		netPriceTextField.setNewValue(roundNumberToString((unitPrc * qty)- disc));
	}

	public boolean isEnterValid() {
		boolean ret = true;
		try {
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
				}
			}

			try {
				if (toDouble(itemDiscountPercentField.getValue().trim()) < 0) {
					itemDiscountPercentField.setValue("0");
				}
			} catch (Exception e) {
				itemDiscountPercentField.setValue("0");
			}

			try {
				if (toDouble(itemDiscountAmountField.getValue().trim()) < 0) {
					itemDiscountAmountField.setValue("0");
				}
			} catch (Exception e) {
				itemDiscountAmountField.setValue("0");
			}

		} catch (Exception e) {

		}

		if (paymentAmountTextField == null) {
			setRequiredError(paymentAmountTextField,
					getPropertyName("invalid_data"), true);
			// paymentAmountTextField.focus();
			ret = false;
		} else {
			try {
				if (paymentAmountTextField.getValue() <= 0) {
					setRequiredError(paymentAmountTextField,
							getPropertyName("invalid_data"), true);
					paymentAmountTextField.amountField.focus();
					ret = false;
				} else
					setRequiredError(paymentAmountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(paymentAmountTextField,
						getPropertyName("invalid_data"), true);
				// paymentAmountTextField.focus();
				ret = false;
			}
		}

		return ret;

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

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0, disc_ttl = 0, ttl_bfr_tax = 0, ttl_bfr_disc = 0, cess_ttl = 0, discount = 0, totalPaymentAmount = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();

				// totalPaymentAmount += (Double)
				// item.getItemProperty(TBC_PAYMENT_AMOUNT).getValue();

				if (taxEnable) {
					tax_ttl += (Double) item.getItemProperty(TBC_TAX_AMT)
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
			table.setColumnFooter(TBC_TAX_AMT, roundNumberToString(tax_ttl));
			table.setColumnFooter(TBC_NET_PRICE, roundNumberToString(net_ttl));
			table.setColumnFooter(TBC_DISCOUNT, roundNumberToString(disc_ttl));
			// table.setColumnFooter(TBC_PAYMENT_AMOUNT,
			// asString(roundNumber(totalPaymentAmount)));
			table.setColumnFooter(TBC_NET_TOTAL,
					roundNumberToString(ttl_bfr_tax));
			table.setColumnFooter(TBC_NET_FINAL,
					roundNumberToString(ttl_bfr_disc));

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
			grandTotalAmtTextField.setNewValue(roundNumberToString(Math
					.abs(roundNumber(net_ttl - discount))));
			payingAmountTextField
					.setNewValue(grandTotalAmtTextField.getValue());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (itemComboField.getValue() == null
					|| itemComboField.getValue().equals("")) {
				setRequiredError(itemComboField,
						getPropertyName("invalid_selection"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				setRequiredError(itemComboField, null, false);
			}
			double disc = 0;
			if (itemDiscountAmountField.isVisible()) {
				try {
					if (toDouble(itemDiscountAmountField.getValue().trim()) < 0) {
						itemDiscountAmountField.setValue("0");
					}

					disc = toDouble(itemDiscountAmountField.getValue()
							.toString().trim());

				} catch (Exception e1) {
					itemDiscountAmountField.setValue("0");
				}

			}

			if (itemDiscountPercentField.isVisible()) {
				try {
					if (toDouble(itemDiscountPercentField.getValue().trim()) < 0) {
						itemDiscountPercentField.setValue("0");
					}
				} catch (Exception e1) {
					itemDiscountPercentField.setValue("0");
				}
			}

			if (itemComboField.getValue() != null) {
				ItemModel mdl = new ItemDao().getItem((Long) itemComboField
						.getValue());
				if (mdl.getMax_discount() != 0) {
					if (disc > mdl.getMax_discount()) {
						setRequiredError(itemDiscountAmountField,
								getPropertyName("invalid_selection"), true);
						ret = false;
					} else
						setRequiredError(itemDiscountAmountField, null, false);
				} else
					setRequiredError(itemDiscountAmountField, null, false);

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
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,
								getPropertyName("invalid_data"), true);
						barcodeField.setValue("");
						barcodeField.focus();
						ret = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField,
							getPropertyName("invalid_data"), true);
					barcodeField.setValue("");
					barcodeField.focus();
					ret = false;
				}
			}

			if (convertionQtyTextField.getValue() == null
					|| convertionQtyTextField.getValue().equals("")) {
				setRequiredError(convertionQtyTextField,
						getPropertyName("invalid_data"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(convertionQtyTextField.getValue()) <= 0) {
						setRequiredError(convertionQtyTextField,
								getPropertyName("invalid_data"), true);
						barcodeField.setValue("");
						barcodeField.focus();
						ret = false;
					} else
						setRequiredError(convertionQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(convertionQtyTextField,
							getPropertyName("invalid_data"), true);
					barcodeField.setValue("");
					barcodeField.focus();
					ret = false;
				}
			}

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("invalid_data"), true);
						barcodeField.setValue("");
						barcodeField.focus();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					barcodeField.setValue("");
					barcodeField.focus();
					ret = false;
				}
			}

			if (stockSelectList.getValue() == null
					|| stockSelectList.getValue().equals("")) {
				setRequiredError(stockSelectList,
						getPropertyName("invalid_selection"), true);
				setRequiredError(changeStkButton,
						getPropertyName("invalid_selection"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				setRequiredError(stockSelectList, null, false);
				setRequiredError(changeStkButton, null, false);
			}

			if (unitSelect.getValue() == null
					|| unitSelect.getValue().equals("")) {
				setRequiredError(unitSelect,
						getPropertyName("invalid_selection"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else
				setRequiredError(unitSelect, null, false);

		} catch (Exception e) {
			ret = false;
		}

		return ret;

	}

	public boolean isUpdatingValid() {
		boolean ret = true;
		try {

			try {
				if (toDouble(itemDiscountAmountField.getValue().trim()) < 0) {
					itemDiscountAmountField.setValue("0");
				}
			} catch (Exception e1) {
				itemDiscountAmountField.setValue("0");
			}

			try {
				if (toDouble(itemDiscountPercentField.getValue().trim()) < 0) {
					itemDiscountPercentField.setValue("0");
				}
			} catch (Exception e1) {
				itemDiscountPercentField.setValue("0");
			}
			// if (paymentModeCombo.getValue() == null
			// || paymentModeCombo.getValue().equals("")) {
			// setRequiredError(paymentModeCombo,
			// getPropertyName("invalid_selection"), true);
			// ret = false;
			// } else {
			// setRequiredError(paymentModeCombo, null, false);
			// }
			//
			// if (paymentAmountTextField == null) {
			// setRequiredError(paymentAmountTextField,
			// getPropertyName("invalid_data"), true);
			// ret = false;
			// } else {
			// try {
			// if (paymentAmountTextField.getValue() <= 0) {
			// setRequiredError(paymentAmountTextField,
			// getPropertyName("invalid_data"), true);
			// ret = false;
			// } else
			// setRequiredError(paymentAmountTextField, null, false);
			// } catch (Exception e) {
			// setRequiredError(paymentAmountTextField,
			// getPropertyName("invalid_data"), true);
			// ret = false;
			// }
			// }

			double disc = 0;
			try {
				disc = toDouble(itemDiscountAmountField.getValue().toString()
						.trim());
			} catch (Exception e1) {
				disc = 0;
			}
			if (itemComboField.getValue() != null) {
				ItemModel mdl = new ItemDao().getItem((Long) itemComboField
						.getValue());
				if (mdl.getMax_discount() != 0) {
					if (disc > mdl.getMax_discount()) {
						setRequiredError(itemDiscountAmountField,
								getPropertyName("invalid_selection"), true);
						ret = false;
					} else
						setRequiredError(itemDiscountAmountField, null, false);
				} else
					setRequiredError(itemDiscountAmountField, null, false);
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
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,
								getPropertyName("invalid_data"), true);
						barcodeField.setValue("");
						barcodeField.focus();
						;
						ret = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField,
							getPropertyName("invalid_data"), true);
					barcodeField.setValue("");
					barcodeField.focus();
					ret = false;
				}
			}

			if (convertionQtyTextField.getValue() == null || convertionQtyTextField.getValue().equals("")) {
				setRequiredError(convertionQtyTextField,
						getPropertyName("invalid_data"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(convertionQtyTextField.getValue()) <= 0) {
						setRequiredError(convertionQtyTextField,
								getPropertyName("invalid_data"), true);
						barcodeField.setValue("");
						barcodeField.focus();
						ret = false;
					} else
						setRequiredError(convertionQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(convertionQtyTextField,
							getPropertyName("invalid_data"), true);
					barcodeField.setValue("");
					barcodeField.focus();
					ret = false;
				}
			}

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("invalid_data"), true);
						barcodeField.setValue("");
						barcodeField.focus();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					barcodeField.setValue("");
					barcodeField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (itemComboField.getValue() != null) {

				Collection selectedItems = (Collection) table.getValue();
				Item item = table.getItem(selectedItems.iterator().next());

				if (!itemComboField
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
						barcodeField.setValue("");
						barcodeField.focus();
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

			if (itemComboField.getValue() == null
					|| itemComboField.getValue().equals("")) {
				setRequiredError(itemComboField,
						getPropertyName("invalid_selection"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else
				setRequiredError(itemComboField, null, false);

			if (unitSelect.getValue() == null
					|| unitSelect.getValue().equals("")) {
				setRequiredError(unitSelect,
						getPropertyName("invalid_selection"), true);
				barcodeField.setValue("");
				barcodeField.focus();
				ret = false;
			} else
				setRequiredError(unitSelect, null, false);
		} catch (Exception e) {
			ret = false;
			barcodeField.setValue("");
			barcodeField.focus();
		}

		return ret;

	}

	public void visibleAddupdateSalesButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
		deleteItemButton.setVisible(UpdateVisible);
		newEntryButton.setVisible(UpdateVisible);
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
			list.add(new SalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			salesNumberList.setContainerDataSource(bic);
			salesNumberList.setItemCaptionPropertyId("sales_number");

			reloadItemStocks();

			salesNumberList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (payingAmountTextField.getValue() == null
				|| payingAmountTextField.getValue().equals("")) {
			payingAmountTextField.setNewValue("0.0");
		} else {
			try {
				if (toDouble(payingAmountTextField.getValue()) < 0) {
					setRequiredError(payingAmountTextField,getPropertyName("invalid_data"), true);
					payingAmountTextField.focus();
					ret = false;
				} else
					setRequiredError(payingAmountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(payingAmountTextField,getPropertyName("invalid_data"), true);
				payingAmountTextField.focus();
				ret = false;
			}
		}
		// if (paymentModeCombo.getValue() == null
		// || paymentModeCombo.getValue().equals("")) {
		// setRequiredError(paymentModeCombo,
		// getPropertyName("invalid_selection"), true);
		// ret = false;
		// } else {
		// setRequiredError(paymentModeCombo, null, false);
		// }

		// if (paymentAmountTextField == null) {
		// setRequiredError(paymentAmountTextField,
		// getPropertyName("invalid_data"), true);
		// ret = false;
		// } else {
		// try {
		// if (paymentAmountTextField.getValue() <= 0) {
		// setRequiredError(paymentAmountTextField,
		// getPropertyName("invalid_data"), true);
		// ret = false;
		// } else
		// setRequiredError(paymentAmountTextField, null, false);
		// } catch (Exception e) {
		// setRequiredError(paymentAmountTextField,
		// getPropertyName("invalid_data"), true);
		// ret = false;
		// }
		// }
		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			itemSelectCombo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		// if (responsibleEmployeeCombo.getValue() == null
		// || responsibleEmployeeCombo.getValue().equals("")) {
		// setRequiredError(responsibleEmployeeCombo,
		// getPropertyName("invalid_selection"), true);
		// responsibleEmployeeCombo.focus();
		// ret = false;
		// } else
		// setRequiredError(responsibleEmployeeCombo, null, false);

		if (creditPeriodTextField.getValue() == null || creditPeriodTextField.getValue().equals("")) {
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
				setRequiredError(creditPeriodTextField,getPropertyName("invalid_data"), true);
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
		return ret;
	}

	public void removeAllErrors() {
		if (taxSelect.getComponentError() != null)
			setRequiredError(taxSelect, null, false);
		if (unitPriceTextField.getComponentError() != null)
			setRequiredError(unitPriceTextField, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (itemSelectCombo.getComponentError() != null)
			setRequiredError(itemSelectCombo, null, false);
		if (itemComboField.getComponentError() != null)
			setRequiredError(itemComboField, null, false);
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
	}

	private void calculateBalance() {
		double netPrice = roundNumber(toDouble(payingAmountTextField.getValue()));
		double paymentAmount = roundNumber(paymentAmountTextField.getValue());

		balanceAmountTextField.setNewValue(roundNumberToString(paymentAmount - netPrice));
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
