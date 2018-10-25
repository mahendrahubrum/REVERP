package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.fixedasset.dao.FixedAssetDao;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.inventory.reports.bean.FixedAssetLedgerBean;
import com.inventory.reports.dao.FixedAssetLedgerDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class FixedAssetLedgerUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OfficeDao officeDao;
	private Report report;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable subTable;
	private SComboField assetTypeGroupComboField;
	private HashMap<Long, String> currencyHashMap;	
	private SComboField fixedAssetComboField;
	private FixedAssetLedgerDao ledgerDao;
	private FixedAssetDao fixedAssetDao;
	private SPopupView popUpView;
	private SFormLayout popUpFormLayout;
	private SLabel groupLabel;
	private SLabel fixedAssetLabel;
	private SLabel dateLabel;
	private SLabel particularsLabel;
	private SLabel openingQtyLabel;
	private SLabel openingBalLabel;
	private SLabel quantityLabel;
	private SLabel unitPriceLabel;
	private SLabel amountLabel;
	private SLabel depPercentageLabel;
	private SLabel depValueLabel;
	private SLabel closingQtyLabel;
	private SLabel closingBalLabel;
	private static final String TBC_GROUP = "Group";
	private static final String TBC_FIXED_ASSET = "Fixed Asset";
	private static final String TBC_DATE = "Date";
	private static final String TBC_PARTICULARS = "Particulars";
	private static final String TBC_OPENING_BALANCE = "Opening Bal.";
	private static final String TBC_OPENING_QTY = "Opening Qty";
	private static final String TBC_AMOUNT = "Amount";
	private static final String TBC_QUANTITY = "Deduction";
	private static final String TBC_UNIT_PRICE = "Unit Price";
	private static final String TBC_DEP_PERCENTAGE = "Dep. Percentage";
	private static final String TBC_DEP_VALUE = "Dep. Amount";
	private static final String TBC_CLOSING_QTY = "Closing Qty.";
	private static final String TBC_CLOSING_BAL = "Closing Bal.";

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		setSize(1300, 400);
		setWidth("100%");
		SPanel panel = new SPanel();
		panel.setSizeFull();

				
		officeDao = new OfficeDao();				
		ledgerDao = new FixedAssetLedgerDao();
		fixedAssetDao = new FixedAssetDao();
		report = new Report(getLoginID());
		
		createPopUpForm();
		
		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());
		
		assetTypeGroupComboField = new SComboField(getPropertyName("asset_type_group"), 300, 
				getAssetTypeGroupList(),"id","name",false);
		assetTypeGroupComboField.setInputPrompt("--------------- "+getPropertyName("all")+" ------------------");
		assetTypeGroupComboField.setValue((long)0);
		
		fixedAssetComboField = new SComboField(getPropertyName("fixed_asset"), 300, 
				getFixedAssetList(),"id","name",false);
		fixedAssetComboField.setInputPrompt("--------------- "+getPropertyName("all")+" ------------------");
		fixedAssetComboField.setValue((long)0);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		fromDateField.setImmediate(true);

		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		toDateField.setImmediate(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// ===================================================================================

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.SPACEBAR);

		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);

		SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.addComponent(generateButton);
		buttonHorizontalLayout.addComponent(showButton);
		buttonHorizontalLayout.setSpacing(true);

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(assetTypeGroupComboField);
		mainFormLayout.addComponent(fixedAssetComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		subTable = new STable(null, 900, 200);
	//	subTable.setWidth("100%");
	//	subTable.setStyleName("table_wrap_style");
		subTable.addContainerProperty(TBC_GROUP, String.class, null,
				getPropertyName("group"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_FIXED_ASSET, String.class, null,
				getPropertyName("fixed_asset"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_DATE,String.class, null,
				getPropertyName("date"), null, Align.CENTER);
		subTable.addContainerProperty(TBC_PARTICULARS, String.class, null,
				getPropertyName("particulars"), null, Align.LEFT);		
		subTable.addContainerProperty(TBC_OPENING_QTY, String.class, null,
				getPropertyName("opening_qty"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_OPENING_BALANCE, String.class, null,
				getPropertyName("opening_balance"), null, Align.RIGHT);		
		subTable.addContainerProperty(TBC_QUANTITY, String.class, null,
				getPropertyName("quantity"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,
				getPropertyName("unit_price"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_AMOUNT, String.class, null,
				getPropertyName("amount"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_DEP_PERCENTAGE, Double.class, null,
				getPropertyName("percentage"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_DEP_VALUE, Double.class, null,
				getPropertyName("depreciation_value"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_CLOSING_QTY, String.class, null,
				getPropertyName("closing_qty"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_CLOSING_BAL, String.class, null,
				getPropertyName("closing_balance"), null, Align.RIGHT);

		subTable.setColumnExpandRatio(TBC_GROUP, 3f);
		subTable.setColumnExpandRatio(TBC_FIXED_ASSET, 3f);
		subTable.setColumnExpandRatio(TBC_DATE, 2f);
		subTable.setColumnExpandRatio(TBC_PARTICULARS, 2f);
		subTable.setColumnExpandRatio(TBC_OPENING_BALANCE, 2f);
		subTable.setColumnExpandRatio(TBC_OPENING_QTY, 2f);
		subTable.setColumnExpandRatio(TBC_QUANTITY, 2f);
		subTable.setColumnExpandRatio(TBC_DEP_PERCENTAGE, 2f);
		subTable.setColumnExpandRatio(TBC_DEP_VALUE, 2f);
		

		// subTable.setVisibleColumns(visibleSubColumns);
		subTable.setSelectable(true);

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		//mainHorizontalLayout.setWidth("100%");
		mainHorizontalLayout.addComponent(mainFormLayout);
		mainHorizontalLayout.addComponent(popUpView);
		mainHorizontalLayout.addComponent(subTable);		

		panel.setContent(mainHorizontalLayout);
		
		officeComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				List<GroupModel> list = getAssetTypeGroupList();
				assetTypeGroupComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				assetTypeGroupComboField.setItemCaptionPropertyId("name");		
				assetTypeGroupComboField.setValue((long)0);
			}			
		});
		
		assetTypeGroupComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				List<FixedAssetModel> list = getFixedAssetList();
				fixedAssetComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				fixedAssetComboField.setItemCaptionPropertyId("name");		
				fixedAssetComboField.setValue((long)0);
			}			
		});
		
		showButton.addClickListener(new ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				subTable.removeAllItems();
				if (isValid()) {
					try {
						List<FixedAssetLedgerBean> list = ledgerDao
								.getFixedAssetLedger(toLong(officeComboField.getValue()), 
										toLong(assetTypeGroupComboField.getValue()), 
										toLong(fixedAssetComboField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										getCurrencyDescription(getCurrencyID()));
					//	long prevTypeGroupId = 0;
						int slNo = 0;
						
						for(FixedAssetLedgerBean bean : list){
							slNo++;
							System.out.println("====="+slNo);
							subTable.addItem(new Object[]{
									bean.getGroup(),
									bean.getFixedAsset(),
									CommonUtil.formatDateToDDMMYYYY(bean.getDate()),
									bean.getParticulars(),
									bean.getOpeningQty()+" "+bean.getUnit(),
									bean.getOpeningBal()+" "+bean.getCurrency(),
									bean.getQty()+" "+bean.getUnit(),
									bean.getUnitPrice(),
									bean.getAmount()+" "+bean.getCurrency(),
									bean.getDepPercentage(),
									bean.getDepValue(),
									bean.getClosingQty()+" "+bean.getUnit(),
									bean.getClosingBalance()+" "+bean.getCurrency()}, 
									subTable.getItemIds().size() + 1);
							
						}
						if(slNo == 0){
							SNotification.show(getPropertyName("no_data_available"), 
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		generateButton.addClickListener(new ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void buttonClick(ClickEvent event) {
				//subTable.removeAllItems();
				if (isValid()) {
					try {
						List list = ledgerDao
								.getFixedAssetLedger(toLong(officeComboField.getValue()), 
										toLong(assetTypeGroupComboField.getValue()), 
										toLong(fixedAssetComboField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										getCurrencyDescription(getCurrencyID()));
					
						if(list.size() == 0){
							SNotification.show(getPropertyName("no_data_available"), 
									Type.WARNING_MESSAGE);
						} else {
							report.setJrxmlFileName("FixedAssetLedger");
							report.setReportFileName("FixedAssetLedger");
							report.setReportTitle(getPropertyName("fixed_asset_ledger"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("PARTICULARS_LABEL", getPropertyName("particulars"));
							map.put("QTY_LABEL", getPropertyName("qty"));
							map.put("BALANCE_LABEL", getPropertyName("balance"));
							map.put("OPENING_LABEL", getPropertyName("opening"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("DEPRECIATION_LABEL", getPropertyName("depreciation"));
							map.put("DEPRECIATION_VALUE_LABEL", getPropertyName("dep_value"));
							map.put("CLOSING_QTY_LABEL", getPropertyName("closing_qty"));
							map.put("CLOSING_BALANCE_LABEL", getPropertyName("closing_bal"));
							map.put("GROUP_LABEL", getPropertyName("group"));
							map.put("FIXED_ASSET_LABEL", getPropertyName("fixed_asset"));
							
							
							
							report.createReport(list, map);
							list.clear();		
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		subTable.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (subTable.getValue() != null) {
						Item item = subTable.getItem(subTable.getValue());
						groupLabel.setValue(item.getItemProperty(TBC_GROUP).getValue().toString());
						fixedAssetLabel.setValue(item.getItemProperty(TBC_FIXED_ASSET).getValue().toString());
						dateLabel.setValue(item.getItemProperty(TBC_DATE).getValue().toString());
						particularsLabel.setValue(item.getItemProperty(TBC_PARTICULARS).getValue().toString());
						openingQtyLabel.setValue(item.getItemProperty(TBC_OPENING_QTY).getValue().toString());
						openingBalLabel.setValue(item.getItemProperty(TBC_OPENING_BALANCE).getValue().toString());
						quantityLabel.setValue(item.getItemProperty(TBC_QUANTITY).getValue().toString());
						unitPriceLabel.setValue(item.getItemProperty(TBC_UNIT_PRICE).getValue().toString());
						amountLabel.setValue(item.getItemProperty(TBC_AMOUNT).getValue().toString());
						depPercentageLabel.setValue(item.getItemProperty(TBC_DEP_PERCENTAGE).getValue().toString());
						depValueLabel.setValue(item.getItemProperty(TBC_DEP_VALUE).getValue().toString());
						closingQtyLabel.setValue(item.getItemProperty(TBC_CLOSING_QTY).getValue().toString());
						closingBalLabel.setValue(item.getItemProperty(TBC_CLOSING_BAL).getValue().toString());
						popUpView.setPopupVisible(true);
						popUpView.setHideOnMouseOut(false);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		return panel;
	}

	private void createPopUpForm() {
		popUpFormLayout = new SFormLayout();
		popUpView = new SPopupView("", popUpFormLayout);
		popUpView.setHideOnMouseOut(false);
		
		groupLabel = new SLabel(getPropertyName("asset_type_group"));
		fixedAssetLabel = new SLabel(getPropertyName("fixed_asset"));
		dateLabel = new SLabel(getPropertyName("date"));
		particularsLabel = new SLabel(getPropertyName("particulars"));
		openingQtyLabel = new SLabel(getPropertyName("opening_qty"));
		openingBalLabel = new SLabel(getPropertyName("opening_balance"));
		quantityLabel = new SLabel(getPropertyName("quantity"));
		unitPriceLabel = new SLabel(getPropertyName("unit_price"));
		amountLabel = new SLabel(getPropertyName("amount"));
		depPercentageLabel = new SLabel(getPropertyName("percentage"));
		depValueLabel = new SLabel(getPropertyName("depreciation_value"));
		closingQtyLabel = new SLabel(getPropertyName("closing_qty"));
		closingBalLabel = new SLabel(getPropertyName("closing_balance"));
		
		popUpFormLayout.addComponent(groupLabel);
		popUpFormLayout.addComponent(fixedAssetLabel);
		popUpFormLayout.addComponent(dateLabel);
		popUpFormLayout.addComponent(particularsLabel);
		popUpFormLayout.addComponent(openingQtyLabel);
		popUpFormLayout.addComponent(openingBalLabel);
		popUpFormLayout.addComponent(quantityLabel);
		popUpFormLayout.addComponent(unitPriceLabel);
		popUpFormLayout.addComponent(amountLabel);
		popUpFormLayout.addComponent(depPercentageLabel);
		popUpFormLayout.addComponent(depValueLabel);
		popUpFormLayout.addComponent(closingQtyLabel);
		popUpFormLayout.addComponent(closingBalLabel);
	}

	private List<FixedAssetModel> getFixedAssetList() {
		List<FixedAssetModel> list = new ArrayList<FixedAssetModel>();
		list.add(new FixedAssetModel(0, "--------------- "+getPropertyName("all")+" ------------------"));
		try {
			list.addAll(fixedAssetDao.getAllFixedAssetList(toLong(officeComboField.getValue().toString()),
					toLong(assetTypeGroupComboField.getValue().toString())));
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<GroupModel> getAssetTypeGroupList() {
		List<GroupModel> list = new ArrayList<GroupModel>();
		list.add(new GroupModel(0, "--------------- "+getPropertyName("all")+" ------------------"));
		try {
			list.addAll(ledgerDao.getAllActiveAssetTypeGroupList(getOrganizationID(), toLong(officeComboField.getValue())));
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return list;
	}

	protected String getSubTitle() {
		StringBuffer titleStringBuffer = new StringBuffer();
		titleStringBuffer
			//	.append("Item : "+ ((assetTypeGroupComboField.getValue() == null) ? "All" : assetTypeGroupComboField.getItemCaption(assetTypeGroupComboField.getValue())))		

				.append("\n"+getPropertyName("from_date")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(fromDateField
								.getValue()))
				.append(" "+getPropertyName("to_date")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(toDateField
								.getValue()));
		return titleStringBuffer.toString();
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
	private long toLong(Object obj) {
		if (obj == null) {
			return 0;
		} else {
			return toLong(obj.toString());
		}
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getOfficeList() {
		List<S_OfficeModel> list = new ArrayList<S_OfficeModel>();
		list.add(new S_OfficeModel(0, "---- Select -----"));
		try {
			list.addAll(officeDao.getAllOfficesUnderOrg(getOrganizationID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (officeComboField.getValue() == null) {
			setRequiredError(officeComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(officeComboField, null, false);
		}

		if (fromDateField.getValue() == null) {
			setRequiredError(fromDateField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			setRequiredError(fromDateField, null, false);
		}
		if (toDateField.getValue() == null) {
			setRequiredError(toDateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(toDateField, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
