package com.inventory.fixedasset.ui;


import java.util.ArrayList;
import java.util.Arrays;
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
import com.inventory.fixedasset.model.FixedAssetDepreciationMainModel;
import com.inventory.fixedasset.model.FixedAssetDepreciationModel;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
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

public class FixedAssetDepreciationUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SGridLayout masterDetailsGrid;
	private SComboField depreciationNoCombo;
	private SButton createNewButton;
	//private STextField referenceNoTextField;
	private SDateField dateField;
//	private STextField customerTextField;
	private SPanel panel;
	private STable table;
	private SVerticalLayout mainVerticalLayout;
	private SComboField fixedAssetCombo;
	private SButton newFixedAssetButton;
//	private STextField purchaseQuantityField;
	private SNativeSelect unitSelect;
//	private SCurrencyField purchaseUnitPriceField;
//	private STextField purchaseNetPriceField;
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
	private List<FixedAssetDepreciationModel> fixedAssetDepreciationList;
	// private UnitDao unitDao;
	private static String TBC_SN = "SN";
	private static String TBC_ID = "Id";
	private static String TBC_ASSET_PURCHASE_ID = "Asset Purchase Id";
	private static String TBC_FIXED_ASSET_DETAILS = "Asset Details";
	
	private static String TBC_QTY = "Qty";
	private static String TBC_UNIT = "Unit(S)";
	private static String TBC_UNIT_PRICE = "Unit Price";
	private static String TBC_AMOUNT = "Purchase Amount";
	
	private static String TBC_CURRENCY = "Currency";
	
	private static String TBC_DEPRECIATION_TYPE = "Depreciation Type";
	private static String TBC_DEPRECIATION_PERCENTAGE = "Depreciation Percentage";
	private static String TBC_DEPRECIATION_VALUE = "Depreciation Value";	
	
	private static String TBC_AMOUNT_AFTER_DEPRECIATION = "Amount After Depreciation";
//	private static String TBC_PROFIT_OR_LOSS = "Profit Or Loss";
	private static String TBC_TRANSACTION_ID = "Transaction Id";
	private static String TBC_ASSET_ID = "Asset Id";
	private static String TBC_SEQUENCE_NUMBER = "Sequence Number";
	
	private ArrayList<Long> deletedDepreciationIds;
	private SGridLayout paymentGrid;
	private WrappedSession session;
	private SettingsValuePojo settings;
//	private SRadioButton cashChequeRadio;
//	private SCurrencyField payingAmountCurrencyField;
//	private SCurrencyField netPriceCurrencyField;
//	private SCurrencyField convertedCurrencyField;
	
	private long transactionId = 0;
	private SComboField fixedAssetPurchaseCombo;
	private STextField quantityField;
	private STextField unitPriceTextField;
	private STextField purchaseNetPriceField;
	private SNativeSelect depreciationTypeNativeSelectField;
	private STextField depreciationPercentageTextField;
	private FixedAssetSalesDao fixedAssetSalesDao;
	private FixedAssetDepreciationDao fixedAssetDepreciationDao;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
	//	setWindowMode(WindowMode.MAXIMIZED);
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		
		
		deletedDepreciationIds = new ArrayList<Long>();
		
		fixedAssetDao = new FixedAssetDao();
		fixedAssetPurchaseDao = new FixedAssetPurchaseDao();
		fixedAssetSalesDao = new FixedAssetSalesDao();
		fixedAssetDepreciationDao = new FixedAssetDepreciationDao();
		// unitDao = new UnitDao();

		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_ASSET_ID,TBC_ASSET_PURCHASE_ID,TBC_FIXED_ASSET_DETAILS,				
				TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE, TBC_AMOUNT,
				TBC_CURRENCY, TBC_DEPRECIATION_TYPE,  TBC_DEPRECIATION_PERCENTAGE,
				TBC_DEPRECIATION_VALUE,
				TBC_AMOUNT_AFTER_DEPRECIATION,
				TBC_TRANSACTION_ID, TBC_SEQUENCE_NUMBER};

		visibleHeaders = new String[] { TBC_SN, TBC_FIXED_ASSET_DETAILS,				
				TBC_QTY, TBC_UNIT,TBC_UNIT_PRICE, TBC_AMOUNT,
				TBC_CURRENCY, TBC_DEPRECIATION_VALUE,
				TBC_AMOUNT_AFTER_DEPRECIATION};

		setSize(1200, 600);
		panel = new SPanel();
		panel.setSizeFull();

		addEditMainItemLayout = new SGridLayout(5, 2);
		addEditMainItemLayout.setSpacing(true);
		// addEditMainItemLayout.setWidth("1100");
		addEditMainItemLayout.setMargin(true);
	//	addEditMainItemLayout.setStyleName("po_border");	
	//	addEditMainItemLayout.setWidth("1200");

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
			

		depreciationNoCombo = new SComboField(null, 150, null, "id", "depreciationNo", false,
				getPropertyName("create_new"));

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Fixed Asset Sales");

	//	referenceNoTextField = new STextField(null, 150);

		dateField = new SDateField(null, 100, getDateFormat());
		dateField.setValue(getWorkingDate());

//		customerTextField = new STextField(null, 300);

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
		
		table.addContainerProperty(TBC_QTY, Double.class, null,
				getPropertyName("quantity"), null, Align.CENTER);
		table.addContainerProperty(TBC_UNIT, String.class, null,
				getPropertyName("unit"), null, Align.CENTER);
		table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null, 
				getPropertyName("unit_price"),	null, Align.CENTER);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,
				getPropertyName("amount"), null, Align.RIGHT);
		
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
		table.addContainerProperty(TBC_TRANSACTION_ID, Long.class, null,
				getPropertyName("transaction_id"), null, Align.RIGHT);
		table.addContainerProperty(TBC_ASSET_ID, Long.class, null,
				getPropertyName("asset_id"), null, Align.RIGHT);
		table.addContainerProperty(TBC_SEQUENCE_NUMBER, Long.class, null,
				TBC_SEQUENCE_NUMBER, null, Align.RIGHT);
		
		table.setColumnExpandRatio(TBC_SN, 0.35f);
		table.setColumnExpandRatio(TBC_FIXED_ASSET_DETAILS, 2);
		table.setColumnExpandRatio(TBC_UNIT_PRICE, 1);
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
				null, "id", "assetNo", true, getPropertyName("all"));

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
	
		quantityField = new STextField(getPropertyName("quantity"), 100);		
	//	quantityField.setStyleName("textfield_align_right");
		quantityField.setValue("0");		
		quantityField.setReadOnly(true);
		
		unitPriceTextField = new STextField(getPropertyName("unit_price"), 150);		
	//	unitPriceField.setStyleName("textfield_align_left");
		unitPriceTextField.setValue(0+"");
		unitPriceTextField.setReadOnly(true);

		purchaseNetPriceField = new STextField(getPropertyName("amount"), 100);
		purchaseNetPriceField.setNewValue("0.00");
		//	purchaseNetPriceField.setStyleName("textfield_align_right");
		purchaseNetPriceField.setReadOnly(true);		

		addItemButton = new SButton(null, "Add");
		addItemButton.setStyleName("addItemBtnStyle");
		addItemButton.setClickShortcut(KeyCode.ENTER);

		updateItemButton = new SButton(null, "Update");
		updateItemButton.setStyleName("updateItemBtnStyle");
		updateItemButton.setVisible(false);
		updateItemButton.setClickShortcut(KeyCode.ENTER);

		// ================================================================================================
		
	

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
		
		loadDepreciationDetails(0);

		// =====================================================================================

		SHorizontalLayout assetNohorizontalLayout = new SHorizontalLayout();
		assetNohorizontalLayout.addComponent(depreciationNoCombo);
		assetNohorizontalLayout.addComponent(createNewButton);
		
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("depreciation_no")));
		masterDetailsGrid.addComponent(assetNohorizontalLayout);		
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")));
		masterDetailsGrid.addComponent(dateField);

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
		
	
		
	
		
		SFormLayout buttonLay = new SFormLayout();
		buttonLay.addComponent(addItemButton);
		buttonLay.addComponent(updateItemButton);

		addEditMainItemLayout.addComponent(fixedAssetCombo);
		addEditMainItemLayout.addComponent(itemLayout);
		addEditMainItemLayout.addComponent(quantityField);		
		addEditMainItemLayout.addComponent(unitPriceTextField);		
		addEditMainItemLayout.addComponent(purchaseNetPriceField);		
		addEditMainItemLayout.addComponent(depreciationTypeNativeSelectField);
		addEditMainItemLayout.addComponent(percentageLayout);
		
		SHorizontalLayout mainTableLayout = new SHorizontalLayout();
		mainTableLayout.setStyleName("po_border");	
	//	mainTableLayout.setWidth("1100");
		mainTableLayout.setWidth("100%");
		mainTableLayout.setSpacing(true);
		
		mainTableLayout.addComponent(addEditMainItemLayout);		
		mainTableLayout.addComponent(buttonLay);
		
		mainTableLayout.setExpandRatio(addEditMainItemLayout, 0.3f);
		mainTableLayout.setExpandRatio(buttonLay, 0.1f);
		
		mainTableLayout.setComponentAlignment(buttonLay, Alignment.MIDDLE_CENTER);		
		
	
		

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
		
		depreciationNoCombo.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					deletedDepreciationIds.clear();
					table.removeAllItems();
					setRequiredError(table, null, false);
					if(depreciationNoCombo.getValue() != null && !depreciationNoCombo.getValue().equals("") 
							&& toLong(depreciationNoCombo.getValue().toString()) != 0){
						loadAssetDetailsFromDB(toLong(depreciationNoCombo.getValue().toString()));
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
					} else {
						dateField.setValue(getWorkingDate());
				//.setValue("");
					//	customerTextField.setValue("");
					
						table.setColumnFooter(TBC_AMOUNT, 0+"");
						table.setColumnFooter(TBC_DEPRECIATION_VALUE, 0+"");
				
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
				depreciationNoCombo.setValue((long)0);				
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
					quantityField.setNewValue(item.getItemProperty(TBC_QTY).getValue().toString());
					unitPriceTextField.setNewValue(toDouble(item.getItemProperty(TBC_UNIT_PRICE).getValue().toString())+"");
					purchaseNetPriceField.setNewValue(item.getItemProperty(TBC_AMOUNT).getValue().toString());
					
					
					try {
						long maxSequenceNo = fixedAssetDepreciationDao
								.getFixedAssetDepreciationMaxSequenceNumber((Long) item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue(),0);
						long currentSeqNo = (Long) item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue();
						if(maxSequenceNo > currentSeqNo){
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
					
					
					quantityField.focus();
					
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
						double totalDepreciationValue = getTotalDepreciationValue() ;
						table.setColumnFooter(TBC_AMOUNT, roundNumber(total)+"");
						table.setColumnFooter(TBC_DEPRECIATION_VALUE, roundNumber(totalDepreciationValue)+"");
						
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
						
						e.printStackTrace();
					}
					
					Iterator<FixedAssetDepreciationModel> itr = fixedAssetDepreciationList.iterator();
					while(itr.hasNext()){
						FixedAssetDepreciationModel detModel = itr.next();
						if(detModel.getId() == id){
							deletedDepreciationIds.add(id);
							itr.remove();
							System.out.println("===REMOVE===");
							break;
						}
					}
				}
				table.removeItem(table.getValue());
				double total = getTotalPrice() ;
				double totalProfitOrLoss = getTotalDepreciationValue() ;
				table.setColumnFooter(TBC_AMOUNT, roundNumber(total)+"");
				table.setColumnFooter(TBC_DEPRECIATION_VALUE, roundNumber(totalProfitOrLoss)+"");
				
				
			}

		});		

		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				if(fixedAssetCombo.getValue() != null){
					try {
						List<FixedAssetPurchaseModel> list = getFixedAssetPurchaseList();
						if(list == null){
							list = new ArrayList<FixedAssetPurchaseModel>();
						}
						list.add(0, new FixedAssetPurchaseModel(0, "--------- "+getPropertyName("all")+" ------------"));
						SCollectionContainer bic = SCollectionContainer.setList(
								list, "id");
						fixedAssetPurchaseCombo.setContainerDataSource(bic);
						fixedAssetPurchaseCombo.setItemCaptionPropertyId("assetNo");
						fixedAssetPurchaseCombo.setInputPrompt("---------- "+ getPropertyName("all") + " ------------");
						fixedAssetPurchaseCombo.setValue((long)0);
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
					setRequiredError(quantityField, null, false);
					return;
				}
				List<UnitModel> list;
				List<FixedAssetPurchaseModel> list_1;
				try {
					list_1 = fixedAssetPurchaseDao.getAllFixedAssetPurchaseDetailList(getOfficeID(),
							toLong(fixedAssetCombo.getValue().toString()));
					
					if(list_1 == null){
						list_1 = new ArrayList<FixedAssetPurchaseModel>();
					}
					list_1.add(0, new FixedAssetPurchaseModel(0, "--------- "+getPropertyName("all")+" ------------"));

					SCollectionContainer bic = SCollectionContainer.setList(list_1, "id");
					fixedAssetPurchaseCombo.setContainerDataSource(bic);
					fixedAssetPurchaseCombo.setItemCaptionPropertyId("assetNo");
					fixedAssetPurchaseCombo.setValue((long)0);
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
				if (fixedAssetPurchaseCombo.getValue() == null ||
						toLong(fixedAssetPurchaseCombo.getValue().toString()) == 0) {
					setRequiredError(quantityField, null, false);
					quantityField.setNewValue("0");
					unitPriceTextField.setNewValue("0");
					purchaseNetPriceField.setNewValue("0.00");	
					depreciationPercentageTextField.setNewValue("0");
					return;
				}
				FixedAssetPurchaseDetailsModel detModel = null;
				try {
					detModel = fixedAssetPurchaseDao.getFixedAssetPurchaseDetailsModel(
							toLong(fixedAssetPurchaseCombo.getValue().toString()));
					
					double depreciationQty = getDepreciationQtyFromUITable();
				//	double totalSalesQty =  salesQty + toDouble(salesQuantityField.getValue());
					double purchaseAvailableQty = detModel.getCurrentBalance() - depreciationQty;
					if(depreciationNoCombo.getValue() != null && !depreciationNoCombo.getValue().equals("") 
							&& toLong(depreciationNoCombo.getValue().toString()) != 0){
						purchaseAvailableQty += fixedAssetSalesDao
								.getFixedAssetTotalSalesQty(getOfficeID(),toLong(depreciationNoCombo.getValue().toString()));
					}
					
					quantityField.setNewValue(purchaseAvailableQty+"");
					unitPriceTextField.setNewValue(detModel.getUnitPrice()+"");
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
		
		quantityField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (quantityField.getValue() == null
						|| quantityField.getValue().equals("")) {
					setRequiredError(quantityField, getPropertyName("invalid_data"),
							true);
					return;
				} else {
					try {
									
						if (Double.parseDouble(quantityField.getValue()) < 0) {
							setRequiredError(quantityField,
									getPropertyName("invalid_data"), true);
							return;
						}  else
							setRequiredError(quantityField, null, false);
					} catch (Exception e) {
						setRequiredError(quantityField,
								getPropertyName("invalid_data"), true);
						return;
					}
				}
				double netPrice = toDouble(quantityField.getValue()) * toDouble(unitPriceTextField.getValue()) ;		
				purchaseNetPriceField.setNewValue(roundNumber(netPrice)+"");
			}
		});
				
		unitPriceTextField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (quantityField.getValue() == null
						|| quantityField.getValue().equals("")) {
					setRequiredError(quantityField, getPropertyName("invalid_data"),
							true);
					return;
				} else {
					try {
						
						
						if (Double.parseDouble(quantityField.getValue()) < 0) {
							setRequiredError(quantityField,
									getPropertyName("invalid_data"), true);
							return;
						}  else
							setRequiredError(quantityField, null, false);
					} catch (Exception e) {
						setRequiredError(quantityField,
								getPropertyName("invalid_data"), true);
						return;
					}
				}
				double netPrice = toDouble(quantityField.getValue()) * toDouble(unitPriceTextField.getValue()) ;		
				System.out.println("========== NET PRICE ===== "+netPrice);
				purchaseNetPriceField.setNewValue(roundNumber(netPrice)+"");
			}
		});
		addItemButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidTableData(true)) {
					table.setVisibleColumns(allHeaders);
					
					double purchaseAmount = 0;
					long purchaseDetailId = 0;
					double depreciationValue = 0;
					long sequenceNumber = 0;
					if(fixedAssetPurchaseCombo.getValue() == null 
							|| toLong(fixedAssetPurchaseCombo.getValue().toString()) == 0){
						
						try {
							List<FixedAssetPurchaseDetailsModel> modelList = fixedAssetPurchaseDao
									.getAllFixedAssetPurchaseDetailListByFixedAssetId(getOfficeID(),
									toLong(fixedAssetCombo.getValue().toString()));
							Iterator<FixedAssetPurchaseDetailsModel> itr = modelList.iterator();
							boolean flag = false;
							while(itr.hasNext()){
								FixedAssetPurchaseDetailsModel model = itr.next();
								purchaseAmount = model.getCurrentBalance() * model.getUnitPrice();
								purchaseDetailId = model.getId();			
								if(isExistTable(purchaseDetailId)){
									System.out.println("========== IS EXIST ================");
									flag = true;
									continue;
								}
								
								System.out.println("========== IS NOT EXIST ================");
								flag = false;
								
								depreciationValue = fixedAssetDepreciationDao
										.getDepreciationValue(purchaseDetailId, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 0, 0);
								sequenceNumber = fixedAssetDepreciationDao.getSequenceNumber();		
								
								fixedAssetPurchaseCombo.setValue(purchaseDetailId);
								table.addItem(new Object[]{table.getItemIds().size() + 1,
										(long)0,
										model.getFixedAsset().getId(),
										purchaseDetailId,
										fixedAssetPurchaseCombo.getItemCaption(fixedAssetPurchaseCombo.getValue()),	
										
										model.getCurrentBalance(),
										model.getFixedAsset().getUnit().getSymbol(),
										model.getUnitPrice(),			
										purchaseAmount,
										
										getCurrencyDescription(getCurrencyID()),
										model.getFixedAsset().getDepreciationType(),
										model.getFixedAsset().getPercentage(),
										depreciationValue,
										
										(purchaseAmount - depreciationValue),
															
										transactionId,
										sequenceNumber
										}, 
										purchaseDetailId);
								
							}
							if(flag){
								setRequiredError(fixedAssetPurchaseCombo, getPropertyName("already_exist"), true);
							//	return;
							} else {
								setRequiredError(fixedAssetPurchaseCombo, null, false);
							}
						} catch (Exception e) {							
							e.printStackTrace();
						}
					} else {
						purchaseAmount = toDouble(quantityField.getValue().toString()) * toDouble(unitPriceTextField.getValue());
						purchaseDetailId = toLong(fixedAssetPurchaseCombo.getValue().toString());					
						depreciationValue = fixedAssetDepreciationDao
								.getDepreciationValue(purchaseDetailId, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 0, 0);
						sequenceNumber = fixedAssetDepreciationDao.getSequenceNumber();		
						
						table.addItem(new Object[]{table.getItemIds().size() + 1,
								(long)0,
								toLong(fixedAssetCombo.getValue().toString()),
								purchaseDetailId,
								fixedAssetPurchaseCombo.getItemCaption(fixedAssetPurchaseCombo.getValue()),					
								
								
								toDouble(quantityField.getValue().toString()),
								unitSelect.getItemCaption(unitSelect.getValue()),
								toDouble(unitPriceTextField.getValue()),						
								purchaseAmount,
								
								getCurrencyDescription(getCurrencyID()),
								toInt(depreciationTypeNativeSelectField.getValue().toString()),
								toDouble(depreciationPercentageTextField.getValue().toString()),
								depreciationValue,
								
								(purchaseAmount - depreciationValue),
													
								transactionId,
								sequenceNumber
								}, 
								table.getItemIds().size() + 1);
					}				
					
					table.setVisibleColumns(visibleHeaders);
					clearEntryFields();
					fixedAssetCombo.focus();
					
					double total = getTotalPrice();
					double totalDepreciationValue = getTotalDepreciationValue();
					table.setColumnFooter(TBC_AMOUNT, roundNumber(total)+"");
					table.setColumnFooter(TBC_DEPRECIATION_VALUE, roundNumber(totalDepreciationValue)+"");
				
				}				
				
			}

			@SuppressWarnings("rawtypes")
			private boolean isExistTable(long purchaseDetailId) {
				Iterator itr = table.getItemIds().iterator();
				while(itr.hasNext()){
					Item item = table.getItem(itr.next());
					if(purchaseDetailId == toLong(item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue().toString())){
						return true;
					}					
				}
				return false;
			}

//			@SuppressWarnings("rawtypes")
//			private long getSequenceNumber(long purchaseDetailId) {
//				long sequenceNo = 0;
//				Iterator itr = table.getItemIds().iterator();
//				while(itr.hasNext()){
//					Item item = table.getItem(itr.next());
//					if(purchaseDetailId == toLong(item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue().toString())){
//						sequenceNo = toLong(item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue().toString());
//					}					
//				}
//				
//				if(sequenceNo == 0){
//					try {
//						sequenceNo = fixedAssetDepreciationDao.getFixedAssetDepreciationMaxSequenceNumber(purchaseDetailId, 0);						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				
//				if(sequenceNo == 0){
//					sequenceNo++;
//				} else {
//					try{
//						double totalDepreciationQty = fixedAssetDepreciationDao
//								.getFixedAssetTotalDepreciationQty(sequenceNo, SConstants.FixedAsset.NORMAL_DEPRECIATION);
//						double purchaseCurrentBal = fixedAssetPurchaseDao.getFixedAssetPurchaseCurrentBalance(purchaseDetailId);
//						if(totalDepreciationQty == purchaseCurrentBal){
//							sequenceNo++;
//						}
//					} catch(Exception e){
//						e.printStackTrace();
//					}
//					
//				}
//				System.out.println("============ SEQUENCE NO ==== "+sequenceNo);
//				return sequenceNo;
//			}
		});
		
		
		updateItemButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidTableData(false)) {
					
					
					Item item = table.getItem(table.getValue());
					
					double purchaseAmount = toDouble(purchaseNetPriceField.getValue().toString());
				//	double purchaseAmount = toDouble(quantityField.getValue().toString()) * toDouble(unitPriceTextField.getValue());					
					long sequenceNo = toLong(item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue().toString());
					long depreciationId = toLong(item.getItemProperty(TBC_ID).getValue().toString());
					long purchaseDetId = toLong(fixedAssetPurchaseCombo.getValue().toString());
					double depreciationValue = fixedAssetDepreciationDao.getDepreciationValue(purchaseDetId, 
							CommonUtil.getSQLDateFromUtilDate(dateField.getValue()),
							depreciationId,
							sequenceNo);
					
					
					item.getItemProperty(TBC_ASSET_ID).setValue(toLong(fixedAssetCombo.getValue().toString()));
					item.getItemProperty(TBC_ASSET_PURCHASE_ID).setValue(purchaseDetId);
					item.getItemProperty(TBC_FIXED_ASSET_DETAILS).setValue(fixedAssetCombo.getItemCaption(fixedAssetCombo.getValue()));
					
					item.getItemProperty(TBC_QTY).setValue(toDouble(quantityField.getValue().toString()));
					item.getItemProperty(TBC_UNIT).setValue(unitSelect.getItemCaption(unitSelect.getValue()));
					item.getItemProperty(TBC_UNIT_PRICE).setValue(toDouble(unitPriceTextField.getValue()));
					item.getItemProperty(TBC_AMOUNT).setValue(purchaseAmount);					
					
					item.getItemProperty(TBC_DEPRECIATION_TYPE).setValue(toInt(depreciationTypeNativeSelectField.getValue().toString()));
					item.getItemProperty(TBC_DEPRECIATION_PERCENTAGE).setValue(toDouble(depreciationPercentageTextField.getValue().toString()));
					item.getItemProperty(TBC_DEPRECIATION_VALUE).setValue(depreciationValue);
					item.getItemProperty(TBC_AMOUNT_AFTER_DEPRECIATION).setValue(purchaseAmount - depreciationValue);
				
					clearEntryFields();
					fixedAssetCombo.focus();
					table.clear();
					
				//	double total = getTotalPrice() + toDouble(salesNetPriceField.getValue().toString());
					//table.setColumnFooter(TBC_SALES_AMOUNT, roundNumber(total)+"");
					
					double total = getTotalPrice() + toDouble(purchaseNetPriceField.getValue().toString());
					double totalDepreciationValue = getTotalDepreciationValue() ;
					table.setColumnFooter(TBC_AMOUNT, roundNumber(total)+"");
					table.setColumnFooter(TBC_DEPRECIATION_VALUE, roundNumber(totalDepreciationValue)+"");
					
				}
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					FixedAssetDepreciationMainModel depreciationModel = null;
					
					try {
						depreciationModel = setValuesToModel(0);
						TransactionModel tran = null;
						tran = setTransactions(depreciationModel);
						
						long id = fixedAssetDepreciationDao.save(depreciationModel, tran);
						deletedDepreciationIds.clear();
					//	saveTransactions();
						
						loadDepreciationDetails(id);
						
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
					FixedAssetDepreciationMainModel depMainModel = null;
					
					try {
						long id = toLong(depreciationNoCombo.getValue().toString());
						depMainModel = setValuesToModel(id);
						TransactionModel tran = null;
						tran = setTransactions(depMainModel);
						
						fixedAssetDepreciationDao.update(depMainModel, deletedDepreciationIds,tran );
						depreciationNoCombo.setValue((long)0);		
						loadDepreciationDetails(id);
						deletedDepreciationIds.clear();
						
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
										long id = toLong(depreciationNoCombo.getValue().toString());
										try {
											fixedAssetDepreciationDao.delete(id);
											Notification.show(getPropertyName("delete_success"),
													Type.WARNING_MESSAGE);										

											loadDepreciationDetails(0);
											deletedDepreciationIds.clear();
											
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
		
		
		
		

		return panel;
	}
	@SuppressWarnings("rawtypes")
	private double getTotalDepreciationValue() {

		double total = 0;
		Iterator itr = table.getItemIds().iterator();
		while(itr.hasNext()){
			Item item = table.getItem(itr.next());
			if(table.getValue() != null 
					&& table.getItem(table.getValue()).equals(item)){
				continue;
			}
			total += toDouble(item.getItemProperty(TBC_DEPRECIATION_VALUE).getValue().toString());
		}
		return total;
	
	}
//	private double getDepreciationValue(double amount, long fixedAssetPurchaseDetailsId, int depreciationType, 
//			double percentage, long sequenceNumber, long depreciationId) {
//		double depreciationAmount = 0;
//		long noOfDays = getNoOfDaysOfFixedAsset(fixedAssetPurchaseDetailsId, sequenceNumber, depreciationId);
//		System.out.println("=====NO_OF_DAYS= "+noOfDays+" ========");
//		if(depreciationType == SConstants.FixedAsset.FLAT){
//			depreciationAmount = (amount * noOfDays * percentage) / 36500;
//		} else if(depreciationType == SConstants.FixedAsset.WRITTEN_DOWN_VALUE){
//			try {
//				double totalDepreciation = fixedAssetDepreciationDao
//						.getFixedAssetTotalDepreciation(fixedAssetPurchaseDetailsId, 
//								CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 0);
//				depreciationAmount = ((amount - totalDepreciation) * noOfDays * percentage) / 36500;
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return roundNumber(depreciationAmount);
//	}
//	private long getNoOfDaysOfFixedAsset(long id, long sequenceNumber, long depreciationId) {
//		long noOfDays = 0;
//		try {
//			FixedAssetPurchaseModel purchaseDetModel = fixedAssetPurchaseDao
//					.getFixedAssetPurchaseModelByDetailId(id);
//			Date depreciationMaxDate = fixedAssetDepreciationDao
//					.getFixedAssetDepreciationMaxDate(id, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()),
//							sequenceNumber);
//			Date fromDate = null;
//			if(depreciationMaxDate != null && purchaseDetModel.getDate().compareTo(depreciationMaxDate) < 0){
//				fromDate = depreciationMaxDate;
//			} else {
//				fromDate = purchaseDetModel.getDate();
//			}
//			noOfDays = (int)( (dateField.getValue().getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return noOfDays;
//	}
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
	private TransactionModel setTransactions(FixedAssetDepreciationMainModel depModel) throws Exception {
		FinTransaction tran = new FinTransaction();
		long toAccountId;
		double depreciationAmount;
	
		
		long fromAccountId;
	

		Iterator<FixedAssetDepreciationModel> itr = depModel
				.getFixed_asset_depreciation_list().iterator();
		while (itr.hasNext()) {
			FixedAssetDepreciationModel detModel = itr.next();
			depreciationAmount = detModel.getAmount();		
			
			fromAccountId = detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getAccount().getId();
			toAccountId = detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getDepreciationAccount().getId();
			depreciationAmount = roundNumber(depreciationAmount);
			if(depreciationAmount > 0){
				tran.addTransactionWithNarration(SConstants.DR, fromAccountId, toAccountId,
						depreciationAmount, "Fixed Asset Depreciation)");		
			}
			
		}
		
		TransactionModel transaction = tran.getTransaction(
				SConstants.FIXED_ASSET_DEPRECIATION, depModel.getDate());		
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
			total += toDouble(item.getItemProperty(TBC_AMOUNT).getValue().toString());
		}
		return total;
	}

	private void loadAssetDetailsFromDB(long id) {
		if(fixedAssetDepreciationList == null){
			fixedAssetDepreciationList = new ArrayList<FixedAssetDepreciationModel>();
		} else {
			fixedAssetDepreciationList.clear();
		}
		try {
			FixedAssetDepreciationMainModel model = fixedAssetDepreciationDao.getFixedAssetDepreciationMainModel(id);
			dateField.setValue(model.getDate());
			transactionId = model.getTransactionId();	
			
			fixedAssetDepreciationList = model.getFixed_asset_depreciation_list();
			Iterator<FixedAssetDepreciationModel> itr = fixedAssetDepreciationList.iterator();
			double total = 0;
			double totalProfitOrLoss = 0;
			double depreciationValue;
			//double salesAmount;
			double purchaseAmount;
			table.setVisibleColumns(allHeaders);
		//	String description;
			while(itr.hasNext()){
				FixedAssetDepreciationModel detModel = itr.next();
				purchaseAmount = detModel.getFixedAssetPurchaseDetailsId().getUnitPrice() *
						detModel.getQuantity();
				purchaseAmount = roundNumber(purchaseAmount);
				
				depreciationValue = detModel.getAmount();
				
			
				
				fixedAssetCombo.setValue(detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getId());
				fixedAssetPurchaseCombo.setValue(detModel.getFixedAssetPurchaseDetailsId().getId());
				
				table.addItem(new Object[]{table.getItemIds().size() + 1,
						detModel.getId(),
						detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getId(),
						detModel.getFixedAssetPurchaseDetailsId().getId(),
						fixedAssetPurchaseCombo.getItemCaption(fixedAssetPurchaseCombo.getValue()),
						
						detModel.getQuantity(),
						detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getUnit().getSymbol(),
						detModel.getFixedAssetPurchaseDetailsId().getUnitPrice(),
						purchaseAmount,						
						
						getCurrencyDescription(getCurrencyID()),
						detModel.getType(),
						detModel.getPercentage(),
						depreciationValue,
						
						(purchaseAmount - depreciationValue),					
								
						transactionId,
						detModel.getSequenceNo()
						}, 
						table.getItemIds().size() + 1);
			}
			table.setVisibleColumns(visibleHeaders);	
			table.setColumnFooter(TBC_AMOUNT, roundNumber(total)+"");			
			table.setColumnFooter(TBC_DEPRECIATION_VALUE, roundNumber(totalProfitOrLoss)+"");			
		
			
			fixedAssetCombo.setValue(null);
			fixedAssetPurchaseCombo.setValue(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("rawtypes")
	private FixedAssetDepreciationMainModel setValuesToModel(long id) throws Exception {
		FixedAssetDepreciationMainModel model = null;
		if(id != 0){
			model = fixedAssetDepreciationDao.getFixedAssetDepreciationMainModel(id);
			Iterator itr = table.getItemIds().iterator();
			long tempId;
			
			while(itr.hasNext()){
				Item item = (Item) table.getItem(itr.next());
				tempId = toLong(item.getItemProperty(TBC_ID).getValue().toString());
				
				if(tempId == 0){				
					FixedAssetDepreciationModel depModel = getFixedAssetDepreciationModel(item, null);										
					fixedAssetDepreciationList.add(depModel);					
					model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
				} else {
					for(FixedAssetDepreciationModel m : fixedAssetDepreciationList){
						if(m.equals(new FixedAssetDepreciationModel(tempId))){
							getFixedAssetDepreciationModel(item, m);	
							break;
						}
					}				
				}
//			//	list.add(childModel);
			}
			model.setFixed_asset_depreciation_list(fixedAssetDepreciationList);
//			
		} else {
			model = new FixedAssetDepreciationMainModel();
			model.setDepreciationNo(getNextSequence("Fixed_Asset_Depreciation_No", getLoginID())+"");			
			model.setOffice(new S_OfficeModel(getOfficeID()));
			model.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
			model.setLogin(new S_LoginModel(getLoginID()));
			
			List<FixedAssetDepreciationModel> detailsList = new ArrayList<FixedAssetDepreciationModel>();
			Iterator itr = table.getItemIds().iterator();
		
			while(itr.hasNext()){
				Item item = (Item) table.getItem(itr.next());
				FixedAssetDepreciationModel depModel = getFixedAssetDepreciationModel(item, null);		
							
				detailsList.add(depModel);				
				model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
				
			}			
			model.setFixed_asset_depreciation_list(detailsList);
		}
		
		return model;
	}
	private FixedAssetDepreciationModel getFixedAssetDepreciationModel(
			Item item,FixedAssetDepreciationModel depModel) throws Exception {
	
		if(depModel == null){
			depModel = new FixedAssetDepreciationModel();
		}		
		depModel.setFixedAssetPurchaseDetailsId(fixedAssetPurchaseDao.getFixedAssetPurchaseDetailsModel(
				toLong(item.getItemProperty(TBC_ASSET_PURCHASE_ID).getValue().toString())));
		depModel.setQuantity(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
		depModel.setPercentage(toDouble(item.getItemProperty(TBC_DEPRECIATION_PERCENTAGE).getValue().toString()));
		depModel.setType(toInt(item.getItemProperty(TBC_DEPRECIATION_TYPE).getValue().toString()));
		depModel.setAmount(toDouble(item.getItemProperty(TBC_DEPRECIATION_VALUE).getValue().toString()));
		depModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
		depModel.setSequenceNo(toLong(item.getItemProperty(TBC_SEQUENCE_NUMBER).getValue().toString()));
		depModel.setDepreciationMode(SConstants.FixedAsset.NORMAL_DEPRECIATION);
		
		return depModel;
	}

	private void loadDepreciationDetails(long id) {
		List<FixedAssetDepreciationMainModel> list = new ArrayList<FixedAssetDepreciationMainModel>();
		list.add(0, new FixedAssetDepreciationMainModel(0,"-------- "+getPropertyName("create_new")+" --------"));
		try {
			list.addAll(fixedAssetDepreciationDao.getFixedAssetDepreciationMainModelList(getOfficeID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SCollectionContainer bic = SCollectionContainer.setList(list, "id");
		depreciationNoCombo.setContainerDataSource(bic);
		depreciationNoCombo.setItemCaptionPropertyId("depreciationNo");	
		depreciationNoCombo.setValue(id);		
		depreciationNoCombo.setInputPrompt("------ "+getPropertyName("create_new")+" ------");		
		
	}

	private void clearEntryFields() {		
				
		quantityField.setNewValue("0");
		unitPriceTextField.setNewValue("0");
		purchaseNetPriceField.setNewValue("0");
		
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

	private boolean isValidTableData(boolean isAdd) {
		boolean valid = true;
		if (fixedAssetCombo.getValue() == null
				|| fixedAssetCombo.getValue().equals("")) {
			setRequiredError(fixedAssetCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(fixedAssetCombo, null, false);
		}
		
		
		if(isAdd && 
				(fixedAssetPurchaseCombo.getValue() == null || toLong(fixedAssetPurchaseCombo.getValue().toString()) == 0)){
			
		} else {
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

			if (quantityField.getValue() == null
					|| quantityField.getValue().equals("")) {
				setRequiredError(quantityField, getPropertyName("invalid_data"),
						true);
				valid = false;
			} else {
				try {
				
			//	System.out.println("===== == SALES QTY ===== "+salesQty);
					if (Double.parseDouble(quantityField.getValue()) <= 0) {
						setRequiredError(quantityField,
								getPropertyName("invalid_data"), true);
						valid = false;
					} else
						setRequiredError(quantityField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityField,
							getPropertyName("invalid_data"), true);
					valid = false;
				}
			}
			
			if (unitPriceTextField.getValue() == null
					|| unitPriceTextField.getValue().equals("")) {
				setRequiredError(unitPriceTextField, getPropertyName("invalid_data"),
						true);
				valid = false;
			} else {
				try {
				
			//	System.out.println("===== == SALES QTY ===== "+salesQty);
					if (Double.parseDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,
								getPropertyName("invalid_data"), true);
						valid = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField,
							getPropertyName("invalid_data"), true);
					valid = false;
				}
			}
		}		

		return valid;
	}

	@SuppressWarnings("rawtypes")
	private double getDepreciationQtyFromUITable() {
		
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
			total += toDouble(item.getItemProperty(TBC_QTY).getValue().toString());
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
		
		

		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
