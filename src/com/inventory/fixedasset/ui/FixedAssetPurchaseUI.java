package com.inventory.fixedasset.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.fixedasset.dao.FixedAssetDao;
import com.inventory.fixedasset.dao.FixedAssetPurchaseDao;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
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
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

public class FixedAssetPurchaseUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SGridLayout masterDetailsGrid;
	private SComboField assetNoCombo;
	private SButton createNewButton;
	private STextField referenceNoTextField;
	private SDateField dateField;
	private STextField supplierTextField;
	private SPanel panel;
	private STable table;
	private SVerticalLayout mainVerticalLayout;
	private SComboField fixedAssetCombo;
	private SButton newFixedAssetButton;
	private STextField quantityField;
	private SNativeSelect unitSelect;
	private SCurrencyField unitPriceField;
	private STextField netPriceField;
	private SButton addItemButton;
	private SButton updateItemButton;
	private SHorizontalLayout addEditMainItemLayout;
	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	private FixedAssetDao fixedAssetDao;
	private HashMap<Long, String> currencyHashMap;
	private FixedAssetPurchaseDao fixedAssetPurchaseDao;
	private List<FixedAssetPurchaseDetailsModel> fixedAssetPurchaseList;
	// private UnitDao unitDao;
	private static String TBC_SN = "SN";
	private static String TBC_ID = "Id";
	private static String TBC_ASSET_ID = "Asset Id";
	private static String TBC_FIXED_ASSET = "Asset Name";
	private static String TBC_QTY = "Qty";
	private static String TBC_UNIT_ID = "Unit Id";
	private static String TBC_UNIT = "Unit";
	private static String TBC_UNIT_PRICE = "Unit Price";
	private static String TBC_CURRENCY = "Currency";
	private static String TBC_NET_PRICE = "Net Price";
	private static String TBC_TRANSACTION_DETAIL_ID = "Transaction Detail Id";
	private static String TBC_TRANSACTION_ID = "Transaction Id";
	private ArrayList<Long> deletedIds;
	private SGridLayout paymentGrid;
	private WrappedSession session;
	private SettingsValuePojo settings;
	private SRadioButton cashChequeRadio;
	private SCurrencyField payingAmountCurrencyField;
	private SCurrencyField netPriceCurrencyField;
	private SCurrencyField convertedCurrencyField;
	
	private long transactionId = 0;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		deletedIds = new ArrayList<Long>();
		
		fixedAssetDao = new FixedAssetDao();
		fixedAssetPurchaseDao = new FixedAssetPurchaseDao();
		// unitDao = new UnitDao();

		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_ASSET_ID,
				TBC_FIXED_ASSET, TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
				TBC_UNIT_PRICE, TBC_CURRENCY, TBC_NET_PRICE,
				TBC_TRANSACTION_ID,TBC_TRANSACTION_DETAIL_ID };

		visibleHeaders = new String[] { TBC_SN, TBC_FIXED_ASSET, TBC_QTY,
				TBC_UNIT, TBC_UNIT_PRICE, TBC_CURRENCY, TBC_NET_PRICE };

		setSize(1200, 600);
		panel = new SPanel();
		panel.setSizeFull();

		addEditMainItemLayout = new SHorizontalLayout();
		addEditMainItemLayout.setSpacing(true);
		// addEditMainItemLayout.setWidth("1100");
		addEditMainItemLayout.setMargin(true);
		addEditMainItemLayout.setStyleName("po_border");
		addEditMainItemLayout.setWidth("1100");

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
			

		assetNoCombo = new SComboField(null, 150, null, "id", "assetNo", false,
				getPropertyName("create_new"));

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Fixed Asset Purchase");

		referenceNoTextField = new STextField(null, 150);

		dateField = new SDateField(null, 100, getDateFormat());
		dateField.setValue(getWorkingDate());

		supplierTextField = new STextField(null, 300);

		// ================================================================================================

		table = new STable(null, 1100, 200);

		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
				Align.CENTER);
		table.addContainerProperty(TBC_ASSET_ID, Long.class, null, TBC_ASSET_ID,
				null, Align.CENTER);
		table.addContainerProperty(TBC_FIXED_ASSET, String.class, null,
				getPropertyName("fixed_asset"), null, Align.LEFT);
		table.addContainerProperty(TBC_QTY, Double.class, null,
				getPropertyName("qty"), null, Align.CENTER);
		table.addContainerProperty(TBC_UNIT_ID, Long.class, null, TBC_UNIT_ID,
				null, Align.CENTER);
		table.addContainerProperty(TBC_UNIT, String.class, null,
				getPropertyName("unit"), null, Align.CENTER);
		table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,
				getPropertyName("unit_price"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CURRENCY, String.class, null,
				TBC_CURRENCY, null, Align.CENTER);
		table.addContainerProperty(TBC_NET_PRICE, Double.class, null,
				getPropertyName("net_price"), null, Align.RIGHT);
		table.addContainerProperty(TBC_TRANSACTION_ID, Long.class, null,
				getPropertyName("transaction_id"), null, Align.RIGHT);
		table.addContainerProperty(TBC_TRANSACTION_DETAIL_ID, Long.class, null,
				getPropertyName("transaction_detail_id"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_SN, 0.35f);
		table.setColumnExpandRatio(TBC_FIXED_ASSET, 2);
		table.setColumnExpandRatio(TBC_QTY, 1);
		table.setColumnExpandRatio(TBC_UNIT, 0.5f);
		table.setColumnExpandRatio(TBC_UNIT_PRICE, 1);
		table.setColumnExpandRatio(TBC_CURRENCY, 1);
		table.setColumnExpandRatio(TBC_NET_PRICE, 1);
		
		table.setFooterVisible(true);
		table.setColumnFooter(TBC_FIXED_ASSET, getPropertyName("total"));

		table.setVisibleColumns(visibleHeaders);
		table.setSelectable(true);

		// ================================================================================================
		List<FixedAssetModel> fixedAssetList = getFixedAssetList();
		fixedAssetCombo = new SComboField(getPropertyName("fixed_asset"), 200,
				fixedAssetList, "id", "name", true, getPropertyName("select"));

		newFixedAssetButton = new SButton();
		newFixedAssetButton.setStyleName("addNewBtnStyle");
		newFixedAssetButton.setDescription(getPropertyName("fixed_asset"));

		quantityField = new STextField(getPropertyName("qty"), 60);
		quantityField.setStyleName("textfield_align_right");
		quantityField.setValue("0");

		unitSelect = new SNativeSelect(getPropertyName("unit"), 100, null,
				"id", "symbol");

		unitPriceField = new SCurrencyField("Unit Price", 100, getWorkingDate());
		unitPriceField.setStyleName("textfield_align_right");
		unitPriceField.currencySelect.setVisible(false);
		unitPriceField.setValue(getCurrencyID(), 0);

		netPriceField = new STextField(getPropertyName("net_price"), 100);
		netPriceField.setNewValue("0.00");
		netPriceField.setStyleName("textfield_align_right");
		netPriceField.setReadOnly(true);

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
		
		loadAssetDetails(0);

		// =====================================================================================

		SHorizontalLayout assetNohorizontalLayout = new SHorizontalLayout();
		assetNohorizontalLayout.addComponent(assetNoCombo);
		assetNohorizontalLayout.addComponent(createNewButton);

		masterDetailsGrid.addComponent(new SLabel(getPropertyName("asset_purchase_no")));
		masterDetailsGrid.addComponent(assetNohorizontalLayout);
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")));
		masterDetailsGrid.addComponent(dateField);
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")));
		masterDetailsGrid.addComponent(referenceNoTextField);
		masterDetailsGrid.addComponent(new SLabel(getPropertyName("supplier")));
		masterDetailsGrid.addComponent(supplierTextField);

		SHorizontalLayout itemLayout = new SHorizontalLayout();
		itemLayout.addComponent(fixedAssetCombo);
		itemLayout.addComponent(newFixedAssetButton);
		itemLayout.setComponentAlignment(newFixedAssetButton,
				Alignment.BOTTOM_CENTER);
		// itemLayout.setWidth("300");

		SFormLayout buttonLay = new SFormLayout();
		buttonLay.addComponent(addItemButton);
		buttonLay.addComponent(updateItemButton);

		addEditMainItemLayout.addComponent(itemLayout);
		addEditMainItemLayout.addComponent(quantityField);
		addEditMainItemLayout.addComponent(unitSelect);
		addEditMainItemLayout.addComponent(unitPriceField);
		addEditMainItemLayout.addComponent(netPriceField);
		addEditMainItemLayout.addComponent(buttonLay);

		addEditMainItemLayout.setExpandRatio(itemLayout, 0.2f);
		addEditMainItemLayout.setExpandRatio(quantityField, 0.1f);
		addEditMainItemLayout.setExpandRatio(unitSelect, 0.1f);
		addEditMainItemLayout.setExpandRatio(unitPriceField, 0.1f);
		addEditMainItemLayout.setExpandRatio(netPriceField, 0.1f);
		addEditMainItemLayout.setExpandRatio(buttonLay, 0.1f);
		
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
		mainVerticalLayout.addComponent(addEditMainItemLayout);
		mainVerticalLayout.addComponent(paymentGrid);
		mainVerticalLayout.addComponent(mainButtonLayout);
		
		mainVerticalLayout.setComponentAlignment(mainButtonLayout,
				Alignment.BOTTOM_CENTER);

		panel.setContent(mainVerticalLayout);
		
		
		if(settings.getCASH_ACCOUNT()==0 || settings.getCHEQUE_ACCOUNT()==0){
			SNotification.show("Account Settings Not Set", Type.ERROR_MESSAGE);
		}
		
		assetNoCombo.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					deletedIds.clear();
					table.removeAllItems();
					if(assetNoCombo.getValue() != null && !assetNoCombo.getValue().equals("") 
							&& toLong(assetNoCombo.getValue().toString()) != 0){
						loadAssetDetailsFromDB(toLong(assetNoCombo.getValue().toString()));
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
					} else {
						dateField.setValue(getWorkingDate());
						referenceNoTextField.setValue("");
						supplierTextField.setValue("");
						payingAmountCurrencyField.setValue(0);
						table.setColumnFooter(TBC_NET_PRICE, 0+"");
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
				assetNoCombo.setValue((long)0);				
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue() == null){
					clearEntryFields();
				} else {
					Item item = table.getItem(table.getValue());
					fixedAssetCombo.setValue((Long) item.getItemProperty(TBC_ASSET_ID).getValue());
					quantityField.setValue(item.getItemProperty(TBC_QTY).getValue().toString());
					unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID).getValue());
					unitPriceField.setValue(toDouble(item.getItemProperty(TBC_UNIT_PRICE).getValue().toString()));
					netPriceField.setNewValue(item.getItemProperty(TBC_NET_PRICE).getValue().toString());
					
					quantityField.focus();
					updateItemButton.setVisible(true);
					addItemButton.setVisible(false);
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
						table.setColumnFooter(TBC_NET_PRICE, roundNumber(total)+"");
						convertedCurrencyField.setNewValue(getCurrencyID(), total);
						netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
						return;
					}
					Iterator<FixedAssetPurchaseDetailsModel> itr = fixedAssetPurchaseList.iterator();
					while(itr.hasNext()){
						FixedAssetPurchaseDetailsModel detModel = itr.next();
						if(detModel.getId() == id){
							deletedIds.add(id);
							itr.remove();
							System.out.println("===REMOVE===");
							break;
						}
					}
				}
				table.removeItem(table.getValue());
				double total = getTotalPrice() ;
				table.setColumnFooter(TBC_NET_PRICE, roundNumber(total)+"");
				convertedCurrencyField.setNewValue(getCurrencyID(), total);
				netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
				
			}

		});		

		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				try {
					List<FixedAssetModel> list = getFixedAssetList();
					SCollectionContainer bic = SCollectionContainer.setList(
							list, "id");
					fixedAssetCombo.setContainerDataSource(bic);
					fixedAssetCombo.setItemCaptionPropertyId("name");
					fixedAssetCombo.setInputPrompt("---------- "
							+ getPropertyName("select") + " ------------");
				} catch (Exception e1) {
					e1.printStackTrace();
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
				try {
					list = fixedAssetDao.getFixedAssetUnitList(getOfficeID(),
							toLong(fixedAssetCombo.getValue().toString()));

					SCollectionContainer bic = SCollectionContainer.setList(
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
		newFixedAssetButton.addClickListener(new ClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void buttonClick(ClickEvent event) {
				AddFixedAssetUI fixedAssetUI = new AddFixedAssetUI();
				fixedAssetUI.center();
				fixedAssetUI.setCaption("Add Fixed Asset");
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
						if (Double.parseDouble(quantityField.getValue()) <= 0) {
							setRequiredError(quantityField,
									getPropertyName("invalid_data"), true);
							return;
						} else
							setRequiredError(quantityField, null, false);
					} catch (Exception e) {
						setRequiredError(quantityField,
								getPropertyName("invalid_data"), true);
						return;
					}
				}
				double netPrice = toDouble(quantityField.getValue()) * unitPriceField.getValue() ;		
				netPriceField.setNewValue(roundNumber(netPrice)+"");
			}
		});
				
		unitPriceField.amountField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (quantityField.getValue() == null
						|| quantityField.getValue().equals("")) {
					setRequiredError(quantityField, getPropertyName("invalid_data"),
							true);
					return;
				} else {
					try {
						if (Double.parseDouble(quantityField.getValue()) <= 0) {
							setRequiredError(quantityField,
									getPropertyName("invalid_data"), true);
							return;
						} else
							setRequiredError(quantityField, null, false);
					} catch (Exception e) {
						setRequiredError(quantityField,
								getPropertyName("invalid_data"), true);
						return;
					}
				}
				double netPrice = toDouble(quantityField.getValue()) * unitPriceField.getValue() ;		
				System.out.println("========== NET PRICE ===== "+netPrice);
				netPriceField.setNewValue(roundNumber(netPrice)+"");
			}
		});

		addItemButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidTableData()) {
					table.setVisibleColumns(allHeaders);
					table.addItem(new Object[]{table.getItemIds().size() + 1,
							(long)0,
							toLong(fixedAssetCombo.getValue().toString()),
							fixedAssetCombo.getItemCaption(fixedAssetCombo.getValue()),
							toDouble(quantityField.getValue().toString()),
							toLong(unitSelect.getValue().toString()),
							unitSelect.getItemCaption(unitSelect.getValue()),
							unitPriceField.getValue(),
							getCurrencyDescription(getCurrencyID()),
							toDouble(netPriceField.getValue().toString()),
							/*getFixedAccountId(toLong(fixedAssetCombo.getValue().toString())),*/
							transactionId,
							(long)0
							}, 
							table.getItemIds().size() + 1);
					table.setVisibleColumns(visibleHeaders);
					clearEntryFields();
					fixedAssetCombo.focus();
					
					double total = getTotalPrice();
					table.setColumnFooter(TBC_NET_PRICE, roundNumber(total)+"");
					
					convertedCurrencyField.setNewValue(getCurrencyID(), total);
				//	netPriceCurrencyField.setNewValue(0);
					netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
				}				
				
			}
		});
		
		updateItemButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidTableData()) {
					Item item = table.getItem(table.getValue());
					item.getItemProperty(TBC_ASSET_ID).setValue(toLong(fixedAssetCombo.getValue().toString()));
					item.getItemProperty(TBC_FIXED_ASSET).setValue(fixedAssetCombo.getItemCaption(fixedAssetCombo.getValue()));
					item.getItemProperty(TBC_QTY).setValue(toDouble(quantityField.getValue().toString()));
					item.getItemProperty(TBC_UNIT_ID).setValue(toLong(unitSelect.getValue().toString()));
					item.getItemProperty(TBC_UNIT).setValue(unitSelect.getItemCaption(unitSelect.getValue()));
					item.getItemProperty(TBC_UNIT_PRICE).setValue(unitPriceField.getValue());
					item.getItemProperty(TBC_NET_PRICE).setValue(toDouble(netPriceField.getValue().toString()));
		//			item.getItemProperty(TBC_ACCOUNT_ID).setValue(getFixedAccountId(toLong(fixedAssetCombo.getValue().toString())));			
										
					clearEntryFields();
					fixedAssetCombo.focus();
					table.clear();
					
					double total = getTotalPrice() + toDouble(netPriceField.getValue().toString());
					table.setColumnFooter(TBC_NET_PRICE, roundNumber(total)+"");
					
					
					convertedCurrencyField.setNewValue(getCurrencyID(), total);
					netPriceCurrencyField.setNewValue(roundNumber(total * netPriceCurrencyField.getConversionRate()));
				}
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					FixedAssetPurchaseModel fixedAssetModel = null;
					
					try {
						fixedAssetModel = setValuesToModel(0);
						TransactionModel tran = null;
						if(fixedAssetModel.getPayingAmount() == fixedAssetModel.getNetPrice()){
							tran = setTransactions(fixedAssetModel);
						}
						
						long id = fixedAssetPurchaseDao.save(fixedAssetModel, tran);
					//	saveTransactions();
						
						loadAssetDetails(id);
						deletedIds.clear();
						
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
					FixedAssetPurchaseModel fixedAssetModel = null;
					
					try {
						long id = toLong(assetNoCombo.getValue().toString());
						fixedAssetModel = setValuesToModel(id);
						TransactionModel tran = null;
						if(fixedAssetModel.getPayingAmount() == fixedAssetModel.getNetPrice()){
							tran = setTransactions(fixedAssetModel);
						}
						fixedAssetPurchaseDao.update(fixedAssetModel, deletedIds,tran );
						assetNoCombo.setValue((long)0);		
						loadAssetDetails(id);
						deletedIds.clear();
						
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
										long id = toLong(assetNoCombo.getValue().toString());
										try {
											fixedAssetPurchaseDao.delete(id);
											Notification.show(getPropertyName("delete_success"),
													Type.WARNING_MESSAGE);										

											loadAssetDetails(0);
											deletedIds.clear();
											
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
	private TransactionModel setTransactions(FixedAssetPurchaseModel assetModel) throws Exception {
		FinTransaction tran = new FinTransaction();
		long toAccountId;
		double amount;
		
		long fromAccountId;
		if((Long)cashChequeRadio.getValue() ==SConstants.paymentMode.CASH) {
			fromAccountId = settings.getCASH_ACCOUNT();				
		} else {
			fromAccountId = settings.getCHEQUE_ACCOUNT();
		}

		Iterator<FixedAssetPurchaseDetailsModel> itr = assetModel
				.getFixed_asset_purchase_details_list().iterator();
		while (itr.hasNext()) {
			FixedAssetPurchaseDetailsModel detModel = itr.next();
			
			amount = CommonUtil.roundNumber(detModel.getQuantity()
					* detModel.getUnitPrice());
			toAccountId = detModel.getFixedAsset().getAccount().getId();
			
			tran.addTransaction(SConstants.DR, fromAccountId, toAccountId,
					amount, "Fixed Asset Purchase",
					assetModel.getCurrencyId(),
					assetModel.getConversionRate());			
		}

		TransactionModel transaction = tran.getTransaction(
				SConstants.FIXED_ASSET_PURCHASE, assetModel.getDate());		
		return transaction;
	}

	private FixedAssetModel getFixedAssetModel(long id) {
		FixedAssetModel model = null;
		try {
			model = fixedAssetDao.getFixedAssetModel(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}

	

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
			total += toDouble(item.getItemProperty(TBC_NET_PRICE).getValue().toString());
		}
		return total;
	}

	private void loadAssetDetailsFromDB(long id) {
		if(fixedAssetPurchaseList == null){
			fixedAssetPurchaseList = new ArrayList<FixedAssetPurchaseDetailsModel>();
		} else {
			fixedAssetPurchaseList.clear();
		}
		try {
			FixedAssetPurchaseModel model = fixedAssetPurchaseDao.getFixedAssetPurchaseModel(id);
			dateField.setValue(model.getDate());
			referenceNoTextField.setValue(model.getRef_no());
			supplierTextField.setValue(model.getSupplier());
			cashChequeRadio.setValue(model.getCashOrCheque());
			transactionId = model.getTransactionId();
//			convertedCurrencyField.conversionField.setValue(model.getConversionRate()+"");
//			convertedCurrencyField.setNewValue(getCurrencyID(), roundNumber(model.getNetPrice() / model.getConversionRate()));			
		//	netPriceCurrencyField.setNewValue(model.getNetPrice());
			
			
			fixedAssetPurchaseList = model.getFixed_asset_purchase_details_list();
			Iterator<FixedAssetPurchaseDetailsModel> itr = fixedAssetPurchaseList.iterator();
			double total = 0;
			while(itr.hasNext()){
				
				FixedAssetPurchaseDetailsModel detModel = itr.next();
				total += roundNumber(detModel.getQuantity() * detModel.getUnitPrice());
				
				table.setVisibleColumns(allHeaders);
				table.addItem(new Object[]{
						table.getItemIds().size() + 1,
						detModel.getId(),
						detModel.getFixedAsset().getId(),
						detModel.getFixedAsset().getName(),
						detModel.getQuantity(),
						detModel.getFixedAsset().getUnit().getId(),
						detModel.getFixedAsset().getUnit().getSymbol(),
						detModel.getUnitPrice(),
						getCurrencyDescription(getCurrencyID()),
						roundNumber(detModel.getQuantity() * detModel.getUnitPrice()),
						model.getTransactionId(),
						(long)0
				}, table.getItemIds().size() + 1);
				table.setVisibleColumns(visibleHeaders);	
				
			}
			table.setColumnFooter(TBC_NET_PRICE, roundNumber(total)+"");						
			convertedCurrencyField.setNewValue(roundNumber(total));			
			netPriceCurrencyField.setCurrency(model.getCurrencyId());	
			netPriceCurrencyField.conversionField.setValue(model.getConversionRate()+"");	
			System.out.println(model.getConversionRate()+" ======= "+netPriceCurrencyField.getConversionRate()+"==== "+
					netPriceCurrencyField.conversionField.getValue());
			netPriceCurrencyField.setNewValue(roundNumber(convertedCurrencyField.getValue() * model.getConversionRate()));
			payingAmountCurrencyField.setValue(model.getCurrencyId(), model.getPayingAmount());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("rawtypes")
	private FixedAssetPurchaseModel setValuesToModel(long id) throws Exception {
		FixedAssetPurchaseModel model = null;
		if(id != 0){
			model = fixedAssetPurchaseDao.getFixedAssetPurchaseModel(id);		
		//	fixedAssetPurchaseList.clear();
	//		fixedAssetPurchaseList = model.getFixed_asset_purchase_details_list();
			
			Iterator itr = table.getItemIds().iterator();
			long tempId;
	//		List<FixedAssetPurchaseDetailsModel> list = new ArrayList<FixedAssetPurchaseDetailsModel>();
			FixedAssetPurchaseDetailsModel childModel = null;
			while(itr.hasNext()){
				Item item = (Item) table.getItem(itr.next());
				tempId = toLong(item.getItemProperty(TBC_ID).getValue().toString());
				
				if(tempId == 0){					
					childModel = new FixedAssetPurchaseDetailsModel();
					childModel.setFixedAsset(getFixedAssetModel(toLong(item.getItemProperty(TBC_ASSET_ID).getValue().toString())));
					childModel.setQuantity(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
					childModel.setUnitPrice(toDouble(item.getItemProperty(TBC_UNIT_PRICE).getValue().toString()));	
					childModel.setCurrentBalance(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
					model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
					fixedAssetPurchaseList.add(childModel);
				} else {
					for(FixedAssetPurchaseDetailsModel m : fixedAssetPurchaseList){
						if(m.equals(new FixedAssetPurchaseDetailsModel(tempId))){
							m.setFixedAsset(getFixedAssetModel(toLong(item.getItemProperty(TBC_ASSET_ID).getValue().toString())));
							m.setQuantity(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
							m.setUnitPrice(toDouble(item.getItemProperty(TBC_UNIT_PRICE).getValue().toString()));	
							m.setCurrentBalance(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
							model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
							break;
						}
					}				
				}
			//	list.add(childModel);
			}
			model.setFixed_asset_purchase_details_list(fixedAssetPurchaseList);
			
		} else {
			model = new FixedAssetPurchaseModel();
			model.setAssetNo(getNextSequence("Fixed_Asset_Purchase_No", getLoginID())+"");			
			model.setOffice(new S_OfficeModel(getOfficeID()));
			
			List<FixedAssetPurchaseDetailsModel> detailsList = new ArrayList<FixedAssetPurchaseDetailsModel>();
			Iterator itr = table.getItemIds().iterator();
			while(itr.hasNext()){
				Item item = (Item) table.getItem(itr.next());
				FixedAssetPurchaseDetailsModel childModel = new FixedAssetPurchaseDetailsModel();
				childModel.setFixedAsset(getFixedAssetModel(toLong(item.getItemProperty(TBC_ASSET_ID).getValue().toString())));
				childModel.setQuantity(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
				childModel.setUnitPrice(toDouble(item.getItemProperty(TBC_UNIT_PRICE).getValue().toString()));
				childModel.setCurrentBalance(toDouble(item.getItemProperty(TBC_QTY).getValue().toString()));
				model.setTransactionId(toLong(item.getItemProperty(TBC_TRANSACTION_ID).getValue().toString()));
				
				detailsList.add(childModel);
			}
			
			model.setFixed_asset_purchase_details_list(detailsList);
		}
		
		model.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
		model.setRef_no(referenceNoTextField.getValue().trim());
		model.setSupplier(supplierTextField.getValue().trim());
		model.setCashOrCheque(toLong(cashChequeRadio.getValue().toString()));
		model.setPayingAmount(payingAmountCurrencyField.getValue());
		model.setNetPrice(netPriceCurrencyField.getValue());
		model.setCurrencyId(netPriceCurrencyField.getCurrency());
		model.setConversionRate(netPriceCurrencyField.getConversionRate());
		
		
		return model;
	}

	private void loadAssetDetails(long id) {
		List<FixedAssetPurchaseModel> list = new ArrayList<FixedAssetPurchaseModel>();
		list.add(0, new FixedAssetPurchaseModel(0,"-------- "+getPropertyName("create_new")+" --------"));
		try {
			list.addAll(fixedAssetPurchaseDao.getAllFixedAssetPurchaseList(getOfficeID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SCollectionContainer bic = SCollectionContainer.setList(list, "id");
		assetNoCombo.setContainerDataSource(bic);
		assetNoCombo.setItemCaptionPropertyId("assetNo");	
		assetNoCombo.setValue(id);		
		assetNoCombo.setInputPrompt("------ "+getPropertyName("create_new")+" ------");		
		
	}

	private void clearEntryFields() {		
		quantityField.setValue("");
		unitPriceField.setValue(0);
		netPriceField.setNewValue("0");
		fixedAssetCombo.setValue(null);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
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
		// list.add(new FixedAssetModel(0,
		// "------ "+getPropertyName("create_new")+" ------"));
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

		if (unitPriceField.getValue() <= 0
				|| unitPriceField.conversionField.getValue().equals("")) {
			setRequiredError(unitPriceField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			setRequiredError(unitPriceField, null, false);
		}

		return valid;
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

		if (supplierTextField.getValue() == null
				|| supplierTextField.getValue().trim().length() == 0) {
			setRequiredError(supplierTextField,
					getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(supplierTextField, null, false);
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
