package com.inventory.fixedasset.ui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.fixedasset.dao.FixedAssetDao;
import com.inventory.fixedasset.dao.FixedAssetDepreciationDao;
import com.inventory.fixedasset.dao.FixedAssetPurchaseDao;
import com.inventory.fixedasset.dao.FixedAssetSalesDao;
import com.inventory.fixedasset.model.FixedAssetDepreciationModel;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.fixedasset.model.FixedAssetSalesDetailsModel;
import com.inventory.fixedasset.model.FixedAssetSalesModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

public class FixedAssetSalesUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SGridLayout masterDetailsGrid;
	private SComboField assetSaleNoCombo;
	private SButton createNewButton;
	private STextField referenceNoTextField;
	private SDateField dateField;
	private STextField customerTextField;
	private SPanel panel;
	private STable table;
	private SVerticalLayout mainVerticalLayout;
	private SComboField fixedAssetCombo;
	private SButton newFixedAssetButton;
	private STextField purchaseQuantityField;
	private SNativeSelect unitSelect;
	private SCurrencyField purchaseUnitPriceField;
	private STextField purchaseNetPriceField;
	private SButton addItemButton;
	private SButton updateItemButton;
	private SGridLayout addEditMainItemLayout;
	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	private FixedAssetDao fixedAssetDao;
	private HashMap<Long, String> currencyHashMap;
	private FixedAssetPurchaseDao fixedAssetPurchaseDao;
	private List<FixedAssetSalesDetailsModel> fixedAssetSalesList;
	// private UnitDao unitDao;
	private static String TBC_SN = "SN";
	private static String TBC_ID = "Id";
	private static String TBC_ASSET_PURCHASE_ID = "Asset Purchase Id";
	private static String TBC_FIXED_ASSET_DETAILS = "Asset Details";
	
//	private static String TBC_PURCHASE_QTY = "Purchase Qty";
//	private static String TBC_PURCHASE_UNIT = "Unit(P)";
	
	
	private static String TBC_SALES_QTY = "Sales Qty";
	private static String TBC_SALES_UNIT = "Unit(S)";
	private static String TBC_SALES_UNIT_PRICE = "Unit Price(Sales)";
	private static String TBC_SALES_AMOUNT = "Sales Amount";
	
	private static String TBC_PURCHASE_UNIT_PRICE = "Unit Price(Purchase)";
	private static String TBC_PURCHASE_AMOUNT = "Purchase Amount";
	
	private static String TBC_CURRENCY = "Currency";
	
	private static String TBC_DEPRECIATION_TYPE = "Depreciation Type";
	private static String TBC_DEPRECIATION_PERCENTAGE = "Depreciation Percentage";
	private static String TBC_DEPRECIATION_VALUE = "Depreciation Value";
//	
	
	private static String TBC_AMOUNT_AFTER_DEPRECIATION = "Amount After Depreciation";
	private static String TBC_PROFIT_OR_LOSS = "Profit Or Loss";
	private static String TBC_TRANSACTION_ID = "Transaction Id";
	private static String TBC_ASSET_ID = "Asset Id";
	private static String TBC_SEQUENCE_NUMBER = "Sequence Number";
	private static String TBC_DEPRECIATION_ID = "Depreciation Id";
	
	private ArrayList<Long> deletedSalesDetailsIds;
	private SGridLayout paymentGrid;
	private WrappedSession session;
	private SettingsValuePojo settings;
	private SRadioButton cashChequeRadio;
	private SCurrencyField payingAmountCurrencyField;
	private SCurrencyField netPriceCurrencyField;
	private SCurrencyField convertedCurrencyField;
	
	private long transactionId = 0;
	private SComboField fixedAssetPurchaseCombo;
	private STextField salesQuantityField;
	private SCurrencyField salesUnitPriceField;
	private STextField salesNetPriceField;
	private SNativeSelect depreciationTypeNativeSelectField;
	private STextField depreciationPercentageTextField;
	private FixedAssetSalesDao fixedAssetSalesDao;
	private FixedAssetDepreciationDao fixedAssetDepreciationDao;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		setWindowMode(WindowMode.MAXIMIZED);
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		
		
		deletedSalesDetailsIds = new ArrayList<Long>();
		
		fixedAssetDao = new FixedAssetDao();
		fixedAssetPurchaseDao = new FixedAssetPurchaseDao();
		fixedAssetSalesDao = new FixedAssetSalesDao();
		fixedAssetDepreciationDao = new FixedAssetDepreciationDao();
		// unitDao = new UnitDao();

		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_ASSET_ID,TBC_ASSET_PURCHASE_ID,TBC_FIXED_ASSET_DETAILS,				
				TBC_SALES_QTY, TBC_SALES_UNIT, TBC_SALES_UNIT_PRICE, TBC_SALES_AMOUNT,
				TBC_PURCHASE_UNIT_PRICE, TBC_PURCHASE_AMOUNT,
				TBC_CURRENCY, TBC_DEPRECIATION_TYPE,  TBC_DEPRECIATION_PERCENTAGE,
				TBC_DEPRECIATION_VALUE,
				TBC_AMOUNT_AFTER_DEPRECIATION,
				TBC_PROFIT_OR_LOSS,TBC_TRANSACTION_ID, TBC_SEQUENCE_NUMBER,TBC_DEPRECIATION_ID};

		visibleHeaders = new String[] { TBC_SN, TBC_FIXED_ASSET_DETAILS,				
				TBC_SALES_QTY, TBC_SALES_UNIT_PRICE, TBC_SALES_AMOUNT,
				TBC_PURCHASE_UNIT_PRICE, TBC_PURCHASE_AMOUNT,
				TBC_DEPRECIATION_VALUE,	TBC_AMOUNT_AFTER_DEPRECIATION,TBC_PROFIT_OR_LOSS};

		setSize(1200, 600);
		panel = new SPanel();
		panel.setSizeFull();

		addEditMainItemLayout = new SGridLayout(2, 2);
		addEditMainItemLayout.setSpacing(true);
		// addEditMainItemLayout.setWidth("1100");
		addEditMainItemLayout.setMargin(true);
	//	addEditMainItemLayout.setStyleName("po_border");	
	//	addEditMainItemLayout.setWidth("1300");

		mainVerticalLayout = new SVerticalLayout();
		mainVerticalLayout.setSpacing(true);
		mainVerticalLayout.setMargin(true);

		masterDetailsGrid = new SGridLayout();
		// masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(4);
		masterDetailsGrid.setRows(2);
		masterDetailsGrid.setMargin(true);
		masterDetailsGrid.setColumnExpandRatio(1, 1);
		masterDetailsGrid.setColumnExpandRatio(2, 1);
		masterDetailsGrid.setColumnExpandRatio(3, 1);
		masterDetailsGrid.setColumnExpandRatio(4, 1);
		masterDetailsGrid.setSpacing(true);
		masterDetailsGrid.setWidth("1100");
		
		paymentGrid = new SGridLayout();
		// masterDetailsGrid.setSizeFull();
		paymentGrid.setColumns(3);
		paymentGrid.setRows(1);
		paymentGrid.setMargin(true);	
		paymentGrid.setSpacing(true);
	//	paymentGrid.setWidth("1100");
		
//		paymentGrid.setColumnExpandRatio(1, 0.5f);
			paymentGrid.setColumnExpandRatio(1, 1);	
			paymentGrid.setColumnExpandRatio(2, 1);
			paymentGrid.setColumnExpandRatio(3, 1);
//			paymentGrid.setColumnExpandRatio(4, 1);
//			paymentGrid.setColumnExpandRatio(5, 1);	
			

		assetSaleNoCombo = new SComboField(null, 150, null, "id", "assetSalesNo", false,
				getPropertyName("create_new"));

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Fixed Asset Sales");

		referenceNoTextField = new STextField(null, 150);

		dateField = new SDateField(null, 100, getDateFormat());
		dateField.setValue(getWorkingDate());

		customerTextField = new STextField(null, 300);

		// ================================================================================================

		table = new STable(null, 1100, 200);
		table.setWidth("100%");

		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
				Align.CENTER);
		table.addContainerProperty(TBC_ASSET_PURCHASE_ID, Long.class, null, TBC_ASSET_PURCHASE_ID,
				null, Align.CENTER);
		table.addContainerProperty(TBC_FIXED_ASSET_DETAILS, String.class, null,
				getPropertyName("fixed_asset_details"), null, Align.LEFT);
		
//		table.addContainerProperty(TBC_PURCHASE_QTY, Double.class, null,
//				getPropertyName("purchase_qty"), null, Align.CENTER);
//		table.addContainerProperty(TBC_PURCHASE_UNIT, String.class, null,
//				getPropertyName("unit"), null, Align.CENTER);
		
//		table.addContainerProperty(TBC_PURCHASE_AMOUNT, Double.class, null,
//				getPropertyName("purchase_amount"), null, Align.RIGHT);
		
		table.addContainerProperty(TBC_SALES_QTY, Double.class, null,
				getPropertyName("sales_qty"), null, Align.CENTER);
		table.addContainerProperty(TBC_SALES_UNIT, String.class, null,
				getPropertyName("unit"), null, Align.CENTER);
		table.addContainerProperty(TBC_SALES_UNIT_PRICE, Double.class, null, 
				getPropertyName("sale_unit_price"),	null, Align.CENTER);
		table.addContainerProperty(TBC_SALES_AMOUNT, Double.class, null,
				getPropertyName("sale_amount"), null, Align.RIGHT);
		
		table.addContainerProperty(TBC_PURCHASE_UNIT_PRICE, Double.class, null, 
				getPropertyName("purchase_unit_price"),	null, Align.CENTER);
		table.addContainerProperty(TBC_PURCHASE_AMOUNT, Double.class, null,
				getPropertyName("purchase_amount"), null, Align.RIGHT);
		
		table.addContainerProperty(TBC_CURRENCY, String.class, null,
				TBC_CURRENCY, null, Align.CENTER);
		
		table.addContainerProperty(TBC_DEPRECIATION_TYPE, Integer.class, null,
				getPropertyName("depreciation_type"), null, Align.RIGHT);		
		table.addContainerProperty(TBC_DEPRECIATION_PERCENTAGE, Double.class, null,
				getPropertyName("depreciation_percentage"), null, Align.RIGHT);
	table.addContainerProperty(TBC_DEPRECIATION_VALUE, Double.class, null,
				getPropertyName("depreciation_value"), null, Align.RIGHT);
		
		
		table.addContainerProperty(TBC_AMOUNT_AFTER_DEPRECIATION, Double.class, null,
				getPropertyName("amount_after_depreciation"), null, Align.RIGHT);
		table.addContainerProperty(TBC_PROFIT_OR_LOSS, Double.class, null,
				getPropertyName("profit_or_loss"), null, Align.RIGHT);
		table.addContainerProperty(TBC_TRANSACTION_ID, Long.class, null,
				getPropertyName("transaction_id"), null, Align.RIGHT);
		table.addContainerProperty(TBC_ASSET_ID, Long.class, null,
				getPropertyName("asset_id"), null, Align.RIGHT);
		table.addContainerProperty(TBC_SEQUENCE_NUMBER, Long.class, null,
				TBC_SEQUENCE_NUMBER, null, Align.RIGHT);
		table.addContainerProperty(TBC_DEPRECIATION_ID, Long.class, null,
				TBC_DEPRECIATION_ID, null, Align.RIGHT);
		
		table.setColumnExpandRatio(TBC_SN, 0.35f);
		table.setColumnExpandRatio(TBC_FIXED_ASSET_DETAILS, 2);
	//	table.setColumnExpandRatio(TBC_PURCHASE_QTY, 0.75f);
		table.setColumnExpandRatio(TBC_PURCHASE_UNIT_PRICE, 1f);
		table.setColumnExpandRatio(TBC_SALES_UNIT_PRICE, 1);
		table.setColumnExpandRatio(TBC_CURRENCY, 1);
		table.setColumnExpandRatio(TBC_AMOUNT_AFTER_DEPRECIATION, 1);
		
		table.setFooterVisible(true);
		table.setColumnFooter(TBC_FIXED_ASSET_DETAILS, getPropertyName("total"));

		table.setVisibleColumns(visibleHeaders);
		table.setSelectable(true);

		// ================================================================================================
		List<FixedAssetModel> fixedAssetList = getFixedAssetList();
		fixedAssetCombo = new SComboField(getPropertyName("fixed_asset"), 200,
				fixedAssetList, "id", "name", true, getPropertyName("select"));
	//	fixedAssetCombo.setValue((long)0);
		
	//	List<FixedAssetPurchaseModel> list = getFixedAssetPurchaseList();
		fixedAssetPurchaseCombo = new SComboField(getPropertyName("asset_purchase"), 300,
				null, "id", "assetNo", true, getPropertyName("select"));

		newFixedAssetButton = new SButton();
		newFixedAssetButton.setStyleName("addNewBtnStyle");
		newFixedAssetButton.setDescription(getPropertyName("fixed_asset"));
		
		unitSelect = new SNativeSelect(getPropertyName("unit"), 100, null,
				"id", "symbol");
		
		depreciationTypeNativeSelectField = new SNativeSelect(getPropertyName("depreciation_type"),
				150, getDepreciationTypeList(), "intKey", "value",true);
		depreciationTypeNativeSelectField.setValue(1);		
		depreciationTypeNativeSelectField.setReadOnly(true);
		
		depreciationPercentageTextField = new STextField(getPropertyName("percentage"), 150,true);
	//	depreciationPercentageTextField.setStyleName("textfield_align_right");
		depreciationPercentageTextField.setValue("0");
		depreciationPercentageTextField.setReadOnly(true);

		purchaseQuantityField = new STextField(getPropertyName("purchase_qty_available"), 100);
		purchaseQuantityField.setStyleName("textfield_align_right");
		purchaseQuantityField.setValue("0");		
		purchaseQuantityField.setReadOnly(true);

		purchaseUnitPriceField = new SCurrencyField(getPropertyName("purchase_unit_price"), 150, getWorkingDate());
		purchaseUnitPriceField.setStyleName("textfield_align_right");
		purchaseUnitPriceField.currencySelect.setVisible(false);
		purchaseUnitPriceField.setValue(getCurrencyID(), 0);
		purchaseUnitPriceField.setReadOnly(true);
		 
		purchaseNetPriceField = new STextField(getPropertyName("purchase_amount"), 100);
		purchaseNetPriceField.setNewValue("0.00");
		purchaseNetPriceField.setStyleName("textfield_align_right");
		purchaseNetPriceField.setReadOnly(true);
		purchaseNetPriceField.setReadOnly(true);
		
		salesQuantityField = new STextField(getPropertyName("sales_qty"), 100);
		salesQuantityField.setStyleName("textfield_align_right");
		salesQuantityField.setValue("0");		

		salesUnitPriceField = new SCurrencyField(getPropertyName("sale_unit_price"), 150, getWorkingDate());
		salesUnitPriceField.setStyleName("textfield_align_right");
		salesUnitPriceField.currencySelect.setVisible(false);
		salesUnitPriceField.setValue(getCurrencyID(), 0);

		salesNetPriceField = new STextField(getPropertyName("sale_amount"), 100);
		salesNetPriceField.setNewValue("0.00");
		salesNetPriceField.setStyleName("textfield_align_right");
		salesNetPriceField.setReadOnly(true);
		

		addItemButton = new SButton(null, "Add");
		addItemButton.setStyleName("addItemBtnStyle");
		addItemButton.setClickShortcut(KeyCode.ENTER);

		updateItemButton = new SButton(null, "Update");
		updateItemButton.setStyleName("updateItemBtnStyle");
		updateItemButton.setVisible(false);
		updateItemButton.setClickShortcut(KeyCode.ENTER);

		// ================================================================================================
		
		cashChequeRadio=new SRadioButton(null, 200, SConstants.paymentMode.cashChequeList, "key", "value");
		cashChequeRadio.setHorizontal(true);
		cashChequeRadio.setImmediate(true);
		cashChequeRadio.setValue(SConstants.paymentMode.CASH);
		
		payingAmountCurrencyField = new SCurrencyField(getPropertyName("paying_amount"),100,getWorkingDate());
		payingAmountCurrencyField.setStyleName("textfield_align_right");
		payingAmountCurrencyField.currencySelect.setReadOnly(true);
		payingAmountCurrencyField.rateButton.setVisible(false);
		payingAmountCurrencyField.setValue(getCurrencyID(), 0);
		
		netPriceCurrencyField = new SCurrencyField(getPropertyName("net_price"),100,getWorkingDate());
		netPriceCurrencyField.setStyleName("textfield_align_right");
		netPriceCurrencyField.setValue(getCurrencyID(), 0);
		netPriceCurrencyField.amountField.setReadOnly(true);
		
		convertedCurrencyField = new SCurrencyField(null,100,getWorkingDate());
		convertedCurrencyField.setStyleName("textfield_align_right");
		convertedCurrencyField.setValue(getCurrencyID(), 0);
		convertedCurrencyField.setReadOnly(true);
		convertedCurrencyField.setVisible(false);

		saveButton = new SButton(getPropertyName("save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateButton = new SButton(getPropertyName("update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");

		deleteButton = new SButton(getPropertyName("delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");

		updateButton.setVisible(false);
		deleteButton.setVisible(false);
		
		loadAssetSalesDetails(0);

		// =====================================================================================

		SHorizontalLayout assetNohorizontalLayout = new SHorizontalLayout();
		assetNohorizontalLayout.addComponent(assetSaleNoCombo);
		assetNohorizontalLayout.addComponent(createNewButton);

		masterDetailsGrid.addComponent(new SLabel(getPropertyName("asset_purchase_no")));
		masterDetailsGrid.addComponent(assetNohorizontalLayout);
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")));
		masterDetailsGrid.addComponent(dateField);
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")));
		masterDetailsGrid.addComponent(referenceNoTextField);
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("customer")));
		masterDetailsGrid.addComponent(customerTextField);

		SHorizontalLayout itemLayout = new SHorizontalLayout();
		itemLayout.addComponent(fixedAssetPurchaseCombo);
		itemLayout.addComponent(newFixedAssetButton);
		itemLayout.setComponentAlignment(newFixedAssetButton,
				Alignment.BOTTOM_CENTER);
		
		SHorizontalLayout percentageLayout = new SHorizontalLayout();
		percentageLayout.addComponent(depreciationPercentageTextField);
		percentageLayout.addComponent(unitSelect);
//		percentageLayout.setComponentAlignment(newFixedAssetButton,
//				Alignment.BOTTOM_CENTER);
		
		SGridLayout purchaseAndSaleLayout = new SGridLayout();
		purchaseAndSaleLayout.setColumns(3);
		purchaseAndSaleLayout.setRows(2);
		purchaseAndSaleLayout.setMargin(true);
		purchaseAndSaleLayout.setSpacing(true);
		purchaseAndSaleLayout.setStyleName("po_border");
		
		purchaseAndSaleLayout.addComponent(purchaseQuantityField);		
		purchaseAndSaleLayout.addComponent(purchaseUnitPriceField);
		purchaseAndSaleLayout.addComponent(purchaseNetPriceField);
		
		purchaseAndSaleLayout.addComponent(salesQuantityField);		
		purchaseAndSaleLayout.addComponent(salesUnitPriceField);
		purchaseAndSaleLayout.addComponent(salesNetPriceField);
		// itemLayout.setWidth("300");

		SFormLayout buttonLay = new SFormLayout();
		buttonLay.addComponent(addItemButton);
		buttonLay.addComponent(updateItemButton);

		addEditMainItemLayout.addComponent(fixedAssetCombo);
		addEditMainItemLayout.addComponent(itemLayout);
		addEditMainItemLayout.addComponent(depreciationTypeNativeSelectField);
		addEditMainItemLayout.addComponent(percentageLayout);
		
		SHorizontalLayout mainTableLayout = new SHorizontalLayout();
		mainTableLayout.setStyleName("po_border");	
		mainTableLayout.setWidth("1300");
		mainTableLayout.setSpacing(true);
		
		mainTableLayout.addComponent(addEditMainItemLayout);		
		mainTableLayout.addComponent(purchaseAndSaleLayout);		
		mainTableLayout.addComponent(buttonLay);
		
		mainTableLayout.setExpandRatio(addEditMainItemLayout, 0.3f);
		mainTableLayout.setExpandRatio(purchaseAndSaleLayout, 0.2f);
		mainTableLayout.setExpandRatio(buttonLay, 0.1f);
		
		mainTableLayout.setComponentAlignment(buttonLay, Alignment.MIDDLE_CENTER);
		
//		addEditMainItemLayout.setComponentAlignment(fixedAssetCombo, Alignment.MIDDLE_CENTER);
//		addEditMainItemLayout.setComponentAlignment(itemLayout, Alignment.MIDDLE_CENTER);
//		addEditMainItemLayout.setComponentAlignment(unitSelect, Alignment.MIDDLE_CENTER);
//		addEditMainItemLayout.setComponentAlignment(buttonLay, Alignment.MIDDLE_CENTER);

//		addEditMainItemLayout.setExpandRatio(itemLayout, 0.2f);
//		addEditMainItemLayout.setExpandRatio(unitSelect, 0.1f);
//		addEditMainItemLayout.setExpandRatio(buttonLay, 0.1f);
	//	addEditMainItemLayout.setExpandRatio(purchaseQuantityField, 0.1f);
		
		//addEditMainItemLayout.setExpandRatio(purchaseUnitPriceField, 0.1f);
		//addEditMainItemLayout.setExpandRatio(purchaseNetPriceField, 0.1f);
		
		
		SHorizontalLayout netPriceLayout = new SHorizontalLayout();
		netPriceLayout.setSpacing(true);
		netPriceLayout.addComponent(netPriceCurrencyField);
		netPriceLayout.addComponent(convertedCurrencyField);
		netPriceLayout.setComponentAlignment(convertedCurrencyField, Alignment.BOTTOM_CENTER);
		
		paymentGrid.addComponent(cashChequeRadio);
	//	paymentGrid.addComponent(new SLabel(getPropertyName("paying_amount")));
		paymentGrid.addComponent(payingAmountCurrencyField);
	//	paymentGrid.addComponent(new SLabel(getPropertyName("net_price")));
		paymentGrid.addComponent(netPriceLayout);
	//	paymentGrid.addComponent(netPriceDisplayCurrencyField);
		

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.setSpacing(true);

		mainButtonLayout.addComponent(saveButton);
		mainButtonLayout.addComponent(updateButton);
		mainButtonLayout.addComponent(deleteButton);

		mainVerticalLayout.addComponent(masterDetailsGrid);
		mainVerticalLayout.addComponent(table);
		mainVerticalLayout.addComponent(mainTableLayout);
		mainVerticalLayout.addComponent(paymentGrid);
		mainVerticalLayout.addComponent(mainButtonLayout);
		
		mainVerticalLayout.setComponentAlignment(mainButtonLayout,
				Alignment.BOTTOM_CENTER);

		panel.setContent(mainVerticalLayout);
		
		
		if(settings.getCASH_ACCOUNT()==0 || settings.getCHEQUE_ACCOUNT()==0){
			SNotification.show("Account Settings Not Set", Type.ERROR_MESSAGE);
		}
		
		assetSaleNoCombo.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					deletedSalesDetailsIds.clear();
					table.removeAllItems();
					setRequiredError(table, null, false);
					if(assetSaleNoCombo.getValue() != null && !assetSaleNoCombo.getValue().equals("") 
							&& toLong(assetSaleNoCombo.getValue().toString()) != 0){
						loadAssetDetailsFromDB(toLong(assetSaleNoCombo.getValue().toString()));
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
					} else {
						dateField.setValue(getWorkingDate());
						referenceNoTextField.setValue("");
						customerTextField.setValue("");
						payingAmountCurrencyField.setValue(0);
						table.setColumnFooter(TBC_SALES_AMOUNT, 0+"");
						table.setColumnFooter(TBC_PROFIT_OR_LOSS, 0+"");
						netPriceCurrencyField.setNewValue(getCurrencyID(), 0);
						convertedCurrencyField.setNewValue(getCurrencyID(), 0);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						saveButton.setVisible(true);
						transactionId = 0;
					}
			}
		});
		
		createNewButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				assetSaleNoCombo.setValue((long)0);				
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue() == null){
					clearEntryFields();
				} else {
					fixedAssetCombo.setValue(null);
					fixedAssetPurchaseCombo.setValue(null);
					
					Item item = table.getItem(table.getValue());
					fixedAssetCombo.setValue((Long) item.getItemProperty(TBC_ASSET_ID).getValue());
					fixedAssetPurchaseCombo.setValue((Long) item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue());
					salesQuantityField.setValue(item.getItemProperty(TBC_SALES_QTY).getValue().toString());
					salesUnitPriceField.setValue(toDouble(item.getItemProperty(TBC_SALES_UNIT_PRICE).getValue().toString()));
					salesNetPriceField.setNewValue(item.getItemProperty(TBC_SALES_AMOUNT).getValue().toString());
			//		unitSelect.setValue(item.getItemProperty(TBC_PURCHASE_UNIT_PRICE).getValue());
					//tbc_s
					try {
						long maxSequenceNo = fixedAssetDepreciationDao
								.getFixedAssetDepreciationMaxSequenceNumber((Long) item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue(),0);
						long currentSeqNo = (Long) item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue();
						if(maxSequenceNo != currentSeqNo){
							updateItemButton.setVisible(false);
							addItemButton.setVisible(false);
							setRequiredError(table, getPropertyName("cannot_be_change"), true);
						} else {
							setRequiredError(table, null, false);
							updateItemButton.setVisible(true);
							addItemButton.setVisible(false);
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					salesQuantityField.focus();
					
				}
				
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
				if(table.getValue() != null){
					/*if(deletedIds == null){
						
					}*/
					
					long id = toLong(table.getItem(table.getValue()).getItemProperty(TBC_ID).getValue().toString());
					if(id == 0){
						table.removeItem(table.getValue());
						double total = getTotalPrice() ;
						double totalProfitOrLoss = getTotalProfitOrLoss() ;
						table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");
						table.setColumnFooter(TBC_PROFIT_OR_LOSS, roundNumber(totalProfitOrLoss)+"");
						
						convertedCurrencyField.setNewValue(getCurrencyID(), total);
						netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
						return;
					}
					
					try {
						Item item = table.getItem(table.getValue());
						long maxSequenceNo = fixedAssetDepreciationDao
								.getFixedAssetDepreciationMaxSequenceNumber((Long) item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue(),0);
						long currentSeqNo = (Long) item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue();
						if(maxSequenceNo != currentSeqNo){							
							setRequiredError(table, getPropertyName("deletion_is_not_allowed"), true);
							return;
						} else {
							setRequiredError(table, null, false);							
						}						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Iterator<FixedAssetSalesDetailsModel> itr = fixedAssetSalesList.iterator();
					while(itr.hasNext()){
						FixedAssetSalesDetailsModel detModel = itr.next();
						if(detModel.getId() == id){
							deletedSalesDetailsIds.add(id);
							itr.remove();
							System.out.println("===REMOVE===");
							break;
						}
					}
				}
				table.removeItem(table.getValue());
				double total = getTotalPrice() ;
				double totalProfitOrLoss = getTotalProfitOrLoss() ;
				table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");
				table.setColumnFooter(TBC_PROFIT_OR_LOSS, roundNumber(totalProfitOrLoss)+"");
				
				convertedCurrencyField.setNewValue(getCurrencyID(), total);
				netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
				
			}

		});		

		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				if(fixedAssetCombo.getValue() != null){
					try {
						List<FixedAssetPurchaseModel> list = getFixedAssetPurchaseList();
						SCollectionContainer bic = SCollectionContainer.setList(
								list, "id");
						fixedAssetPurchaseCombo.setContainerDataSource(bic);
						fixedAssetPurchaseCombo.setItemCaptionPropertyId("assetNo");
						fixedAssetPurchaseCombo.setInputPrompt("---------- "+ getPropertyName("select") + " ------------");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
			}
		};
		fixedAssetCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (fixedAssetCombo.getValue() == null) {
					setRequiredError(purchaseQuantityField, null, false);
					return;
				}
				List<UnitModel> list;
				List<FixedAssetPurchaseModel> list_1;
				try {
					list_1 = fixedAssetPurchaseDao.getAllFixedAssetPurchaseDetailList(getOfficeID(),
							toLong(fixedAssetCombo.getValue().toString()));

					SCollectionContainer bic = SCollectionContainer.setList(list_1, "id");
					fixedAssetPurchaseCombo.setContainerDataSource(bic);
					fixedAssetPurchaseCombo.setItemCaptionPropertyId("assetNo");
					//=============================
					list = fixedAssetDao.getFixedAssetUnitList(getOfficeID(),
							toLong(fixedAssetCombo.getValue().toString()));

					bic = SCollectionContainer.setList(
							list, "id");
					unitSelect.setContainerDataSource(bic);
					unitSelect.setItemCaptionPropertyId("symbol");
					if (unitSelect.getItemIds().iterator().hasNext()) {
						unitSelect.setValue(unitSelect.getItemIds().iterator()
								.next());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		fixedAssetPurchaseCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (fixedAssetPurchaseCombo.getValue() == null) {
					setRequiredError(purchaseQuantityField, null, false);
					purchaseQuantityField.setNewValue("0");
					purchaseUnitPriceField.setNewValue(0);
					purchaseNetPriceField.setNewValue("0.00");	
					depreciationPercentageTextField.setNewValue("0");
					return;
				}
				FixedAssetPurchaseDetailsModel detModel = null;
				try {
					detModel = fixedAssetPurchaseDao.getFixedAssetPurchaseDetailsModel(
							toLong(fixedAssetPurchaseCombo.getValue().toString()));
					
					double salesQty = getSalesQtyFromUITable();
				//	double totalSalesQty =  salesQty + toDouble(salesQuantityField.getValue());
					double purchaseAvailableQty = detModel.getCurrentBalance() - salesQty;
					if(assetSaleNoCombo.getValue() != null && !assetSaleNoCombo.getValue().equals("") 
							&& toLong(assetSaleNoCombo.getValue().toString()) != 0){
						purchaseAvailableQty += fixedAssetSalesDao
								.getFixedAssetTotalSalesQty(getOfficeID(),toLong(assetSaleNoCombo.getValue().toString()));
					}
					
					purchaseQuantityField.setNewValue(purchaseAvailableQty+"");
					purchaseUnitPriceField.setNewValue(detModel.getUnitPrice());
					purchaseNetPriceField.setNewValue(roundNumber(detModel.getCurrentBalance() * detModel.getUnitPrice())+"");	
					depreciationPercentageTextField.setNewValue(detModel.getFixedAsset().getPercentage()+"");
					depreciationTypeNativeSelectField.setNewValue(detModel.getFixedAsset().getDepreciationType());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		newFixedAssetButton.addClickListener(new ClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void buttonClick(ClickEvent event) {
				FixedAssetPurchaseUI fixedAssetUI = new FixedAssetPurchaseUI();
				fixedAssetUI.center();
				fixedAssetUI.setCaption("Fixed Asset Purchase");
				getUI().getCurrent().addWindow(fixedAssetUI);
				fixedAssetUI.addCloseListener(closeListener);
			}
		});
		
		salesQuantityField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (salesQuantityField.getValue() == null
						|| salesQuantityField.getValue().equals("")) {
					setRequiredError(salesQuantityField, getPropertyName("invalid_data"),
							true);
					return;
				} else {
					try {
									
						if (Double.parseDouble(salesQuantityField.getValue()) < 0) {
							setRequiredError(salesQuantityField,
									getPropertyName("invalid_data"), true);
							return;
						} else if (toDouble(salesQuantityField.getValue()) > toDouble(purchaseQuantityField.getValue())) {
							setRequiredError(salesQuantityField,
									getPropertyName("available_balance_qty_is")+" "+purchaseQuantityField.getValue(), true);
							return;
						}  else
							setRequiredError(salesQuantityField, null, false);
					} catch (Exception e) {
						setRequiredError(salesQuantityField,
								getPropertyName("invalid_data"), true);
						return;
					}
				}
				double netPrice = toDouble(salesQuantityField.getValue()) * salesUnitPriceField.getValue() ;		
				salesNetPriceField.setNewValue(roundNumber(netPrice)+"");
			}
		});
				
		salesUnitPriceField.amountField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (salesQuantityField.getValue() == null
						|| salesQuantityField.getValue().equals("")) {
					setRequiredError(salesQuantityField, getPropertyName("invalid_data"),
							true);
					return;
				} else {
					try {
						
						
						if (Double.parseDouble(salesQuantityField.getValue()) < 0) {
							setRequiredError(salesQuantityField,
									getPropertyName("invalid_data"), true);
							return;
						} else if (toDouble(salesQuantityField.getValue()) > toDouble(purchaseQuantityField.getValue())) {
							setRequiredError(salesQuantityField,
									getPropertyName("available_balance_qty_is")+" "+purchaseQuantityField.getValue(), true);
							return;
						} else
							setRequiredError(salesQuantityField, null, false);
					} catch (Exception e) {
						setRequiredError(salesQuantityField,
								getPropertyName("invalid_data"), true);
						return;
					}
				}
				double netPrice = toDouble(salesQuantityField.getValue()) * salesUnitPriceField.getValue() ;		
				System.out.println("========== NET PRICE ===== "+netPrice);
				salesNetPriceField.setNewValue(roundNumber(netPrice)+"");
			}
		});
		addItemButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidTableData()) {
					table.setVisibleColumns(allHeaders);
					
					
			//		depreciationValue = 
					double salesAmount = toDouble(salesNetPriceField.getValue().toString());
					double purchaseAmount = toDouble(salesQuantityField.getValue().toString()) * purchaseUnitPriceField.getValue();
					long purchaseDetailId = toLong(fixedAssetPurchaseCombo.getValue().toString());
					long sequenceNumber = getSequenceNumber(purchaseDetailId);
					double depreciationValue = getDepreciationValue(
							roundNumber(purchaseAmount),
							toLong(fixedAssetPurchaseCombo.getValue().toString()),
							toInt(depreciationTypeNativeSelectField.getValue().toString()),
							toDouble(depreciationPercentageTextField.getValue().toString()),
							sequenceNumber,
							0);
					
					
					
					
					
					table.addItem(new Object[]{table.getItemIds().size() + 1,
							(long)0,
							toLong(fixedAssetCombo.getValue().toString()),
							purchaseDetailId,
							fixedAssetPurchaseCombo.getItemCaption(fixedAssetPurchaseCombo.getValue()),					
							
							
							toDouble(salesQuantityField.getValue().toString()),
							unitSelect.getItemCaption(unitSelect.getValue()),
							salesUnitPriceField.getValue(),
							salesAmount,
							
							purchaseUnitPriceField.getValue(),
							purchaseAmount,
							
							getCurrencyDescription(getCurrencyID()),
							toInt(depreciationTypeNativeSelectField.getValue().toString()),
							toDouble(depreciationPercentageTextField.getValue().toString()),
							depreciationValue,
							
							(purchaseAmount - depreciationValue),
							
							(salesAmount - 
									(roundNumber(purchaseAmount)
									- depreciationValue)),
									
							transactionId,
							sequenceNumber,
							(long)0
							}, 
							table.getItemIds().size() + 1);
					table.setVisibleColumns(visibleHeaders);
					clearEntryFields();
					fixedAssetCombo.focus();
					
					double total = getTotalPrice();
					double totalProfitOrLoss = getTotalProfitOrLoss();
					table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");
					table.setColumnFooter(TBC_PROFIT_OR_LOSS, roundNumber(totalProfitOrLoss)+"");
					
					convertedCurrencyField.setNewValue(getCurrencyID(), total);
				//	netPriceCurrencyField.setNewValue(0);
					netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
				}				
				
			}

			@SuppressWarnings("rawtypes")
			private long getSequenceNumber(long purchaseDetailId) {
				long sequenceNo = 0;
				Iterator itr = table.getItemIds().iterator();
				while(itr.hasNext()){
					Item item = table.getItem(itr.next());
					if(purchaseDetailId == toLong(item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue().toString())){
						sequenceNo = toLong(item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue().toString());
					}					
				}
				
				if(sequenceNo == 0){
					try {
						sequenceNo = fixedAssetDepreciationDao.getFixedAssetDepreciationMaxSequenceNumber(purchaseDetailId, 0);						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(sequenceNo == 0){
					sequenceNo++;
				} else {
					try{
						double totalDepreciationQty = fixedAssetDepreciationDao
								.getFixedAssetTotalDepreciationQty(sequenceNo, SConstants.FixedAsset.NORMAL_DEPRECIATION);
						double purchaseCurrentBal = fixedAssetPurchaseDao.getFixedAssetPurchaseCurrentBalance(purchaseDetailId);
						if(totalDepreciationQty == purchaseCurrentBal){
							sequenceNo++;
						}
					} catch(Exception e){
						e.printStackTrace();
					}
					
				}
				System.out.println("============ SEQUENCE NO ==== "+sequenceNo);
				return sequenceNo;
			}
		});
		
		
		updateItemButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidTableData()) {
					Item item = table.getItem(table.getValue());
					
					double saleAmount = toDouble(salesNetPriceField.getValue().toString());
					double purchaseAmount = toDouble(salesQuantityField.getValue().toString()) * purchaseUnitPriceField.getValue();					
					long sequenceNo = toLong(item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue().toString());
					long depreciationId = toLong(item.getItemProperty(TBC_DEPRECIATION_ID).getValue().toString());
					double depreciationValue = getDepreciationValue(
							roundNumber(purchaseAmount),
							toLong(fixedAssetPurchaseCombo.getValue().toString()),
							toInt(depreciationTypeNativeSelectField.getValue().toString()),
							toDouble(depreciationPercentageTextField.getValue().toString()),
							sequenceNo,depreciationId);
					double profitOrLossAmount = saleAmount - 
							(roundNumber(purchaseAmount)
							- depreciationValue);
					
					
					item.getItemProperty(TBC_ASSET_ID).setValue(toLong(fixedAssetCombo.getValue().toString()));
					item.getItemProperty(TBC_ASSET_PURCHASE_ID).setValue(toLong(fixedAssetPurchaseCombo.getValue().toString()));
					item.getItemProperty(TBC_FIXED_ASSET_DETAILS).setValue(fixedAssetCombo.getItemCaption(fixedAssetCombo.getValue()));
					
//					item.getItemProperty(TBC_PURCHASE_QTY).setValue(toDouble(purchaseQuantityField.getValue().toString()));
//					item.getItemProperty(TBC_PURCHASE_UNIT).setValue(unitSelect.getItemCaption(unitSelect.getValue()));
					item.getItemProperty(TBC_PURCHASE_UNIT_PRICE).setValue(purchaseUnitPriceField.getValue());
					item.getItemProperty(TBC_PURCHASE_AMOUNT).setValue(purchaseAmount);
					
					item.getItemProperty(TBC_SALES_QTY).setValue(toDouble(salesQuantityField.getValue().toString()));
					item.getItemProperty(TBC_SALES_UNIT).setValue(unitSelect.getItemCaption(unitSelect.getValue()));
					item.getItemProperty(TBC_SALES_UNIT_PRICE).setValue(salesUnitPriceField.getValue());
					item.getItemProperty(TBC_SALES_AMOUNT).setValue(saleAmount);					
					
					item.getItemProperty(TBC_DEPRECIATION_TYPE).setValue(toInt(depreciationTypeNativeSelectField.getValue().toString()));
					item.getItemProperty(TBC_DEPRECIATION_PERCENTAGE).setValue(toDouble(depreciationPercentageTextField.getValue().toString()));
					item.getItemProperty(TBC_DEPRECIATION_VALUE).setValue(depreciationValue);
					item.getItemProperty(TBC_AMOUNT_AFTER_DEPRECIATION).setValue(purchaseAmount - depreciationValue);
					item.getItemProperty(TBC_PROFIT_OR_LOSS).setValue(profitOrLossAmount);
		//			item.getItemProperty(TBC_ACCOUNT_ID).setValue(getFixedAccountId(toLong(fixedAssetCombo.getValue().toString())));			
					
					clearEntryFields();
					fixedAssetCombo.focus();
					table.clear();
					
				//	double total = getTotalPrice() + toDouble(salesNetPriceField.getValue().toString());
					//table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");
					
					double total = getTotalPrice() + toDouble(salesNetPriceField.getValue().toString());
					double totalProfitOrLoss = getTotalProfitOrLoss() ;
					table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");
					table.setColumnFooter(TBC_PROFIT_OR_LOSS, roundNumber(totalProfitOrLoss)+"");
					
					
					convertedCurrencyField.setNewValue(getCurrencyID(), total);
					netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
				}
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					FixedAssetSalesModel fixedAssetModel = null;
					
					try {
						fixedAssetModel = setValuesToModel(0);
						TransactionModel tran = null;
						if(fixedAssetModel.getPayingAmount() == fixedAssetModel.getNetPrice()){
							tran = setTransactions(fixedAssetModel);
						}
						
						long id = fixedAssetSalesDao.save(fixedAssetModel, tran);
						deletedSalesDetailsIds.clear();
					//	saveTransactions();
						
						loadAssetSalesDetails(id);
						
						Notification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
						
						
								
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
//
		updateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					FixedAssetSalesModel fixedAssetModel = null;
					
					try {
						long id = toLong(assetSaleNoCombo.getValue().toString());
						fixedAssetModel = setValuesToModel(id);
						TransactionModel tran = null;
						if(fixedAssetModel.getPayingAmount() == fixedAssetModel.getNetPrice()){
							tran = setTransactions(fixedAssetModel);
						}
						fixedAssetSalesDao.update(fixedAssetModel, deletedSalesDetailsIds,tran );
						assetSaleNoCombo.setValue((long)0);		
						loadAssetSalesDetails(id);
						deletedSalesDetailsIds.clear();
						
						Notification.show(getPropertyName("update_success"),
								Type.WARNING_MESSAGE);
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
								
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		deleteButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					
					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										long id = toLong(assetSaleNoCombo.getValue().toString());
										try {
											fixedAssetSalesDao.delete(id);
											Notification.show(getPropertyName("delete_success"),
													Type.WARNING_MESSAGE);										

											loadAssetSalesDetails(0);
											deletedSalesDetailsIds.clear();
											
											updateButton.setVisible(false);
											deleteButton.setVisible(false);
											saveButton.setVisible(true);
											

										} catch (Exception e) {
											Notification
													.show(getPropertyName("Error"),
															Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});	
				
						
					
				}
			}
		});
		
		netPriceCurrencyField.currencySelect.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if(netPriceCurrencyField.currencySelect.getValue()!=null){
						if((Long)netPriceCurrencyField.currencySelect.getValue()!=getCurrencyID()){
							convertedCurrencyField.setVisible(true);
						}
						else{
							convertedCurrencyField.setVisible(false);
						}
						netPriceCurrencyField.setNewValue(roundNumber(convertedCurrencyField.getValue() * netPriceCurrencyField.getConversionRate()));
						payingAmountCurrencyField.currencySelect.setNewValue(netPriceCurrencyField.currencySelect.getValue());
						payingAmountCurrencyField.rateButton.setVisible(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		netPriceCurrencyField.conversionField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if(netPriceCurrencyField.conversionField.getValue()!=null){
						netPriceCurrencyField.setNewValue(roundNumber(convertedCurrencyField.getValue()*netPriceCurrencyField.getConversionRate()));
					}
				} catch (ReadOnlyException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		

		return panel;
	}
	@SuppressWarnings("rawtypes")
	private double getTotalProfitOrLoss() {

		double total = 0;
		Iterator itr = table.getItemIds().iterator();
		while(itr.hasNext()){
			Item item = table.getItem(itr.next());
			if(table.getValue() != null 
					&& table.getItem(table.getValue()).equals(item)){
				continue;
			}
			total += toDouble(item.getItemProperty(TBC_PROFIT_OR_LOSS).getValue().toString());
		}
		return total;
	
	}
	private double getDepreciationValue(double amount, long fixedAssetPurchaseDetailsId, int depreciationType, 
			double percentage, long sequenceNumber, long depreciationId) {
		double depreciationAmount = 0;
		long noOfDays = getNoOfDaysOfFixedAsset(fixedAssetPurchaseDetailsId, sequenceNumber, depreciationId);
		System.out.println("=====NO_OF_DAYS= "+noOfDays+" ========");
		if(depreciationType == SConstants.FixedAsset.FLAT){
			depreciationAmount = (amount * noOfDays * percentage) / 36500;
		} else if(depreciationType == SConstants.FixedAsset.WRITTEN_DOWN_VALUE){
			try {
				double totalDepreciation = fixedAssetDepreciationDao
						.getFixedAssetTotalDepreciation(fixedAssetPurchaseDetailsId, 
								CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 0);
				depreciationAmount = ((amount - totalDepreciation) * noOfDays * percentage) / 36500;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return roundNumber(depreciationAmount);
	}
	private long getNoOfDaysOfFixedAsset(long id, long sequenceNumber, long depreciationId) {
		long noOfDays = 0;
		try {
			FixedAssetPurchaseModel purchaseDetModel = fixedAssetPurchaseDao
					.getFixedAssetPurchaseModelByDetailId(id);
			Date depreciationMaxDate = fixedAssetDepreciationDao
					.getFixedAssetDepreciationMaxDate(id, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()),sequenceNumber);
			Date fromDate = null;
			if(depreciationMaxDate != null && purchaseDetModel.getDate().compareTo(depreciationMaxDate) < 0){
				fromDate = depreciationMaxDate;
			} else {
				fromDate = purchaseDetModel.getDate();
			}
			noOfDays = (int)( (dateField.getValue().getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noOfDays;
	}
	private List<KeyValue> getDepreciationTypeList() {
		return Arrays.asList(new KeyValue(SConstants.FixedAsset.FLAT, getPropertyName("flat")),
				new KeyValue(SConstants.FixedAsset.WRITTEN_DOWN_VALUE, getPropertyName("written_down_value")));
	}
	private List<FixedAssetPurchaseModel> getFixedAssetPurchaseList() {
		long id = 0;
		if(fixedAssetCombo.getValue() != null){
			id = toLong(fixedAssetCombo.getValue().toString());
		}
		List<FixedAssetPurchaseModel> list = new ArrayList<FixedAssetPurchaseModel>();
		try {
			list.addAll(fixedAssetPurchaseDao.getAllFixedAssetPurchaseDetailList(getOfficeID(), id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	
	}
	private TransactionModel setTransactions(FixedAssetSalesModel assetModel) throws Exception {
		FinTransaction tran = new FinTransaction();
		long toAccountId;
		double depreciationAmount;
		double salesAmountByPurchaseUnitPrice;
	//	double salesAmount;
		
		long fromAccountId;
		long cashOrChequeAccountId;
		if((Long)cashChequeRadio.getValue() ==SConstants.paymentMode.CASH) {
			cashOrChequeAccountId = settings.getCASH_ACCOUNT();				
		} else {
			cashOrChequeAccountId = settings.getCHEQUE_ACCOUNT();
		}

		Iterator<FixedAssetSalesDetailsModel> itr = assetModel
				.getFixed_asset_sales_details_list().iterator();
		while (itr.hasNext()) {
			FixedAssetSalesDetailsModel detModel = itr.next();
			
//			amount = CommonUtil.roundNumber(detModel.getSalesQuantity()
//					* detModel.getSalesUnitPrice());
			depreciationAmount = detModel.getDepreciationId().getAmount();
			salesAmountByPurchaseUnitPrice = roundNumber(detModel.getSalesQuantity()
					* detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getUnitPrice());
//			salesAmount = CommonUtil.roundNumber(detModel.getSalesQuantity()
//					* detModel.getSalesUnitPrice());
			
			fromAccountId = detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getAccount().getId();
			toAccountId = detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getDepreciationAccount().getId();
			depreciationAmount = roundNumber(depreciationAmount);
			if(depreciationAmount > 0){
				tran.addTransaction(SConstants.DR, fromAccountId, toAccountId,
						depreciationAmount, "Fixed Asset Sales(Depreciation)",
						assetModel.getCurrencyId(),
						assetModel.getConversionRate());		
			}
			
			toAccountId = cashOrChequeAccountId;			
			double amount = roundNumber(Math.abs(salesAmountByPurchaseUnitPrice - depreciationAmount));
			if(amount > 0){
				tran.addTransaction(SConstants.DR, fromAccountId, toAccountId,
						amount,
						"Fixed Asset Sales(After Depreciation)",
						assetModel.getCurrencyId(),
						assetModel.getConversionRate());		
			}			
		}
		double totalProfitOrLoss = getTotalProfitOrLoss();
		totalProfitOrLoss = roundNumber(totalProfitOrLoss);
		if(totalProfitOrLoss > 0){
			fromAccountId = settings.getPROFIT_ACCOUNT();
		} else {
			fromAccountId = settings.getLOSS_ACCOUNT();
		}
		toAccountId = cashOrChequeAccountId;		
		totalProfitOrLoss = roundNumber(Math.abs(totalProfitOrLoss));
		if(totalProfitOrLoss > 0){
			tran.addTransaction(SConstants.DR, fromAccountId, toAccountId,
					totalProfitOrLoss,
					"Fixed Asset Sales(Profit/Loss)",
					assetModel.getCurrencyId(),
					assetModel.getConversionRate());		
		}
		TransactionModel transaction = tran.getTransaction(
				SConstants.FIXED_ASSET_SALES, assetModel.getDate());		
		return transaction;
	}

//	private FixedAssetModel getFixedAssetModel(long id) {
//		FixedAssetModel model = null;
//		try {
//			model = fixedAssetDao.getFixedAssetModel(id);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return model;
//	}

	

	@SuppressWarnings("rawtypes")
	private double getTotalPrice() {
		double total = 0;
		Iterator itr = table.getItemIds().iterator();
		while(itr.hasNext()){
			Item item = table.getItem(itr.next());
			if(table.getValue() != null 
					&& table.getItem(table.getValue()).equals(item)){
				continue;
			}
			total += toDouble(item.getItemProperty(TBC_SALES_AMOUNT).getValue().toString());
		}
		return total;
	}

	private void loadAssetDetailsFromDB(long id) {
		if(fixedAssetSalesList == null){
			fixedAssetSalesList = new ArrayList<FixedAssetSalesDetailsModel>();
		} else {
			fixedAssetSalesList.clear();
		}
		try {
			FixedAssetSalesModel model = fixedAssetSalesDao.getFixedAssetSalesModel(id);
			dateField.setValue(model.getDate());
			referenceNoTextField.setValue(model.getRef_no());
			customerTextField.setValue(model.getCustomer());
			cashChequeRadio.setValue(model.getCashOrCheque());
			transactionId = model.getTransactionId();
//			convertedCurrencyField.conversionField.setValue(model.getConversionRate()+"");
//			convertedCurrencyField.setNewValue(getCurrencyID(), roundNumber(model.getNetPrice() / model.getConversionRate()));			
		//	netPriceCurrencyField.setNewValue(model.getNetPrice());
			
			
			fixedAssetSalesList = model.getFixed_asset_sales_details_list();
			Iterator<FixedAssetSalesDetailsModel> itr = fixedAssetSalesList.iterator();
			double total = 0;
			double totalProfitOrLoss = 0;
			double depreciationValue;
			double salesAmount;
			double purchaseAmount;
			table.setVisibleColumns(allHeaders);
		//	String description;
			while(itr.hasNext()){
				FixedAssetSalesDetailsModel detModel = itr.next();
				salesAmount = roundNumber(detModel.getSalesQuantity() * detModel.getSalesUnitPrice());
				purchaseAmount = detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getUnitPrice() *
						detModel.getSalesQuantity();
				purchaseAmount = roundNumber(purchaseAmount);
				
				depreciationValue = detModel.getDepreciationId().getAmount();
				
				total += salesAmount;
				totalProfitOrLoss += (salesAmount - 
						(roundNumber(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getUnitPrice() *
								detModel.getSalesQuantity())
						- depreciationValue));			
					
				
				fixedAssetCombo.setValue(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getId());
				fixedAssetPurchaseCombo.setValue(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getId());
				
				table.addItem(new Object[]{table.getItemIds().size() + 1,
						detModel.getId(),
						detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getId(),
						detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getId(),
						fixedAssetPurchaseCombo.getItemCaption(fixedAssetPurchaseCombo.getValue()),
						
						detModel.getSalesQuantity(),
						detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getUnit().getSymbol(),
						detModel.getSalesUnitPrice(),
						salesAmount,
						
						detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getUnitPrice(),
						purchaseAmount,						
						
						getCurrencyDescription(getCurrencyID()),
						detModel.getDepreciationId().getType(),
						detModel.getDepreciationId().getPercentage(),
						depreciationValue,
						
						(purchaseAmount - depreciationValue),
						
						(salesAmount - 	(purchaseAmount	- depreciationValue)),
								
						transactionId,
						detModel.getDepreciationId().getSequenceNo(),
						detModel.getDepreciationId().getId()
						}, 
						table.getItemIds().size() + 1);
			}
			table.setVisibleColumns(visibleHeaders);	
			table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");			
			table.setColumnFooter(TBC_PROFIT_OR_LOSS, roundNumber(totalProfitOrLoss)+"");			
			convertedCurrencyField.setNewValue(roundNumber(total));			
			netPriceCurrencyField.setCurrency(model.getCurrencyId());	
			netPriceCurrencyField.conversionField.setValue(model.getConversionRate()+"");	
			System.out.println(model.getConversionRate()+" ======= "+netPriceCurrencyField.getConversionRate()+"==== "+
					netPriceCurrencyField.conversionField.getValue());
			netPriceCurrencyField.setNewValue(roundNumber(convertedCurrencyField.getValue() * model.getConversionRate()));
			payingAmountCurrencyField.setValue(model.getCurrencyId(), model.getPayingAmount());
			
			fixedAssetCombo.setValue(null);
			fixedAssetPurchaseCombo.setValue(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("rawtypes")
	private FixedAssetSalesModel setValuesToModel(long id) throws Exception {
		FixedAssetSalesModel model = null;
		if(id != 0){
			model = fixedAssetSalesDao.getFixedAssetSalesModel(id);
		//	fixedAssetSalesList.clear();
		//	fixedAssetPurchaseList = model.getFixed_asset_purchase_details_list();
//			
			Iterator itr = table.getItemIds().iterator();
			long tempId;
		//	List<FixedAssetSalesDetailsModel> detailsList = new ArrayList<FixedAssetSalesDetailsModel>();
			FixedAssetSalesDetailsModel childModel = null;
			while(itr.hasNext()){
				Item item = (Item) table.getItem(itr.next());
				tempId = toLong(item.getItemProperty(TBC_ID).getValue().toString());
				
				if(tempId == 0){					
					FixedAssetDepreciationModel depModel = getFixedAssetDepreciationModel(item, null);				
					
					childModel = new FixedAssetSalesDetailsModel();
					childModel.setSalesQuantity(depModel.getQuantity());
					childModel.setSalesUnitPrice(toDouble(item.getItemProperty(TBC_SALES_UNIT_PRICE).getValue().toString()));
					childModel.setDepreciationId(depModel);
					
					fixedAssetSalesList.add(childModel);
					
					model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
				} else {
					for(FixedAssetSalesDetailsModel m : fixedAssetSalesList){
						if(m.equals(new FixedAssetSalesDetailsModel(tempId))){
							m.setSalesQuantity(toDouble(item.getItemProperty(TBC_SALES_QTY).getValue().toString()));
							m.setSalesUnitPrice(toDouble(item.getItemProperty(TBC_SALES_UNIT_PRICE).getValue().toString()));
							m.setDepreciationId(getFixedAssetDepreciationModel(item, m.getDepreciationId()));
							model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
							break;
						}
					}				
				}
//			//	list.add(childModel);
			}
			model.setFixed_asset_sales_details_list(fixedAssetSalesList);
//			
		} else {
			model = new FixedAssetSalesModel();
			model.setAssetSalesNo(getNextSequence("Fixed_Asset_Sales_No", getLoginID())+"");			
			model.setOffice(new S_OfficeModel(getOfficeID()));
			model.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
			
			List<FixedAssetSalesDetailsModel> detailsList = new ArrayList<FixedAssetSalesDetailsModel>();
			Iterator itr = table.getItemIds().iterator();
		
			while(itr.hasNext()){
				Item item = (Item) table.getItem(itr.next());
				FixedAssetDepreciationModel depModel = getFixedAssetDepreciationModel(item, null);
				
				
				FixedAssetSalesDetailsModel childModel = new FixedAssetSalesDetailsModel();
				childModel.setSalesQuantity(depModel.getQuantity());
				childModel.setSalesUnitPrice(toDouble(item.getItemProperty(TBC_SALES_UNIT_PRICE).getValue().toString()));
				childModel.setDepreciationId(depModel);
				
				detailsList.add(childModel);
				
				model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
				
			}
			
			model.setFixed_asset_sales_details_list(detailsList);
		}
		
		
		model.setRef_no(referenceNoTextField.getValue().trim());
		model.setCustomer(customerTextField.getValue().trim());
		model.setCashOrCheque(toLong(cashChequeRadio.getValue().toString()));
		model.setPayingAmount(payingAmountCurrencyField.getValue());
		model.setNetPrice(netPriceCurrencyField.getValue());
		model.setCurrencyId(netPriceCurrencyField.getCurrency());
		model.setConversionRate(netPriceCurrencyField.getConversionRate());
		model.setLogin(new S_LoginModel(getLoginID()));
		
		
		return model;
	}
	private FixedAssetDepreciationModel getFixedAssetDepreciationModel(
			Item item,FixedAssetDepreciationModel depModel) throws Exception {
	//	long depreciationId;
	//	depreciationId= toLong(item.getItemProperty(TBC_DEPRECIATION_ID).getValue().toString());
		if(depModel == null){
			depModel = new FixedAssetDepreciationModel();
		}		
		depModel.setFixedAssetPurchaseDetailsId(fixedAssetPurchaseDao.getFixedAssetPurchaseDetailsModel(
				toLong(item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue().toString())));
		depModel.setQuantity(toDouble(item.getItemProperty(TBC_SALES_QTY).getValue().toString()));
		depModel.setPercentage(toDouble(item.getItemProperty(TBC_DEPRECIATION_PERCENTAGE).getValue().toString()));
		depModel.setType(toInt(item.getItemProperty(TBC_DEPRECIATION_TYPE).getValue().toString()));
		depModel.setAmount(toDouble(item.getItemProperty(TBC_DEPRECIATION_VALUE).getValue().toString()));
		depModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
		depModel.setSequenceNo(toLong(item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue().toString()));
		depModel.setDepreciationMode(SConstants.FixedAsset.SALES_DEPRECIATION);
	//	if(depreciationId != 0){
	//		depModel.setId(depreciationId);
	//	}
		return depModel;
	}

	private void loadAssetSalesDetails(long id) {
		List<FixedAssetSalesModel> list = new ArrayList<FixedAssetSalesModel>();
		list.add(0, new FixedAssetSalesModel(0,"-------- "+getPropertyName("create_new")+" --------"));
		try {
			list.addAll(fixedAssetSalesDao.getAllFixedAssetSalesList(getOfficeID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SCollectionContainer bic = SCollectionContainer.setList(list, "id");
		assetSaleNoCombo.setContainerDataSource(bic);
		assetSaleNoCombo.setItemCaptionPropertyId("assetSalesNo");	
		assetSaleNoCombo.setValue(id);		
		assetSaleNoCombo.setInputPrompt("------ "+getPropertyName("create_new")+" ------");		
		
	}

	private void clearEntryFields() {		
		purchaseQuantityField.setNewValue("0");
		purchaseUnitPriceField.setNewValue(0);
		purchaseNetPriceField.setNewValue("0");
		
		salesQuantityField.setValue("0");
		salesUnitPriceField.setValue(0);
		salesNetPriceField.setNewValue("0");
		
		fixedAssetCombo.setValue(null);
		fixedAssetPurchaseCombo.setValue(null);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
		
		depreciationPercentageTextField.setNewValue("0");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getCurrencyDescription(long currencyId) {
		if(currencyHashMap == null){
			currencyHashMap = new HashMap<Long, String>();
			try {
				List list = new CurrencyManagementDao().getCurrencySymbol();
				Iterator<CurrencyModel> itr = list.iterator();
				while(itr.hasNext()){
					CurrencyModel model = itr.next();
					currencyHashMap.put(model.getId(), model.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return currencyHashMap.get(currencyId);
	}

	private List<FixedAssetModel> getFixedAssetList() {
		List<FixedAssetModel> list = new ArrayList<FixedAssetModel>();
	//	 list.add(new FixedAssetModel(0, "------ "+getPropertyName("all")+" ------"));
		try {
			list.addAll(fixedAssetDao.getAllFixedAssetList(getOfficeID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private boolean isValidTableData() {
		boolean valid = true;
		if (fixedAssetCombo.getValue() == null
				|| fixedAssetCombo.getValue().equals("")) {
			setRequiredError(fixedAssetCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(fixedAssetCombo, null, false);
		}
		
		if (fixedAssetPurchaseCombo.getValue() == null
				|| fixedAssetPurchaseCombo.getValue().equals("")) {
			setRequiredError(fixedAssetPurchaseCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(fixedAssetPurchaseCombo, null, false);
		}

		if (unitSelect.getValue() == null || unitSelect.getValue().equals("")) {
			setRequiredError(unitSelect, getPropertyName("invalid_selection"),
					true);
			valid = false;
		} else
			setRequiredError(unitSelect, null, false);

		if (salesQuantityField.getValue() == null
				|| salesQuantityField.getValue().equals("")) {
			setRequiredError(salesQuantityField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			try {
			
		//	System.out.println("===== == SALES QTY ===== "+salesQty);
				if (Double.parseDouble(salesQuantityField.getValue()) <= 0) {
					setRequiredError(salesQuantityField,
							getPropertyName("invalid_data"), true);
					valid = false;
				}else if (Double.parseDouble(salesQuantityField.getValue()) > Double.parseDouble(purchaseQuantityField.getValue())) {
					setRequiredError(salesQuantityField,
							getPropertyName("available_balance_qty_is")+" "+purchaseQuantityField.getValue(), true);
					valid = false;
				} else
					setRequiredError(salesQuantityField, null, false);
			} catch (Exception e) {
				setRequiredError(salesQuantityField,
						getPropertyName("invalid_data"), true);
				valid = false;
			}
		}

		if (salesUnitPriceField.getValue() <= 0
				|| salesUnitPriceField.conversionField.getValue().equals("")) {
			setRequiredError(salesUnitPriceField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			setRequiredError(salesUnitPriceField, null, false);
		}

		return valid;
	}

	@SuppressWarnings("rawtypes")
	private double getSalesQtyFromUITable() {
		
		double total = 0;
		Iterator itr = table.getItemIds().iterator();
		while(itr.hasNext()){
			Item item = table.getItem(itr.next());
			if((table.getValue() != null 
					&& table.getItem(table.getValue()).equals(item)) ||
					(!item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue()
							.equals(fixedAssetPurchaseCombo.getValue()))){
				continue;
			}
			total += toDouble(item.getItemProperty(TBC_SALES_QTY).getValue().toString());
		}
		return total;		
	}
	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(table, null, false);
		}

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(dateField, null, false);
		}

		if (customerTextField.getValue() == null
				|| customerTextField.getValue().trim().length() == 0) {
			setRequiredError(customerTextField,
					getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(customerTextField, null, false);
		}
		
		if(payingAmountCurrencyField.getValue() < 0){
			setRequiredError(payingAmountCurrencyField, getPropertyName("invalid_data"), true);
			valid = false;
		} else if(payingAmountCurrencyField.getValue() > netPriceCurrencyField.getValue()){			
			setRequiredError(payingAmountCurrencyField, getPropertyName("invalid_data"), true);
			valid = false;
		}else if(payingAmountCurrencyField.getValue() == 0){			
			setRequiredError(payingAmountCurrencyField, null, false);
		}else if(payingAmountCurrencyField.getValue() != netPriceCurrencyField.getValue()){			
			setRequiredError(payingAmountCurrencyField, getPropertyName("please_enter_full_amount_or_zero"), true);
			valid = false;
		}else {
			setRequiredError(payingAmountCurrencyField, null, false);
		}

		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
