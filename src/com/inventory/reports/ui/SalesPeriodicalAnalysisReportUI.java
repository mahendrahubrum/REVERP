package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.PeriodicalAnalysisReportBean;
import com.inventory.reports.dao.PeriodicalAnalysisReportDao;
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
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class SalesPeriodicalAnalysisReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OfficeDao officeDao;
	private ItemDao itemDao;
	private Report report;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable subTable;
	private SComboField itemComboField;
	private PeriodicalAnalysisReportDao periodicalAnalysisReportDao;
	private HashMap<Long, String> currencyHashMap;
	private static final String TBC_SLNO = "Sl No";
	private static final String TBC_ITEM = "Item";
	private static final String TBC_MONTH = "Month";
	private static final String TBC_OPENING = "Opening";
	private static final String TBC_SALES = "Sales";
	private static final String TBC_AMOUNT = "Amount";

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		setSize(1300, 400);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		periodicalAnalysisReportDao = new PeriodicalAnalysisReportDao();
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		report = new Report(getLoginID());

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());

		List<ItemModel> list = getItemList(getOfficeID());
		itemComboField = new SComboField(getPropertyName("item"), 200, list,
				"id", "name", false, getPropertyName("all"));

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
		mainFormLayout.addComponent(itemComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		subTable = new STable(null, 900, 200);
	//	subTable.setStyleName("table_wrap_style");
		subTable.addContainerProperty(TBC_SLNO, String.class, null,
				"#", null, Align.CENTER);
		subTable.addContainerProperty(TBC_ITEM, String.class, null,
				getPropertyName("item"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_MONTH, String.class, null,
				getPropertyName("month"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_OPENING, String.class, null,
				getPropertyName("opening"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_SALES, String.class, null,
				getPropertyName("sales"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_AMOUNT, String.class, null,
				getPropertyName("amount"), null, Align.RIGHT);

		subTable.setColumnExpandRatio(TBC_SLNO, 0.3f);
		subTable.setColumnExpandRatio(TBC_ITEM, 2f);
		subTable.setColumnExpandRatio(TBC_MONTH, 2f);
		subTable.setColumnExpandRatio(TBC_OPENING, 2f);
		subTable.setColumnExpandRatio(TBC_SALES, 2f);
		subTable.setColumnExpandRatio(TBC_AMOUNT, 2f);
		

		// subTable.setVisibleColumns(visibleSubColumns);
		subTable.setSelectable(true);

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.addComponent(mainFormLayout);
		mainHorizontalLayout.addComponent(subTable);

		panel.setContent(mainHorizontalLayout);
		
		officeComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				List<ItemModel> list = getItemList(toLong(officeComboField
						.getValue()));
				itemComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				itemComboField.setItemCaptionPropertyId("name");				
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
						List<PeriodicalAnalysisReportBean> list = periodicalAnalysisReportDao
								.getSalesPeriodicalAnalysisReport(toLong(officeComboField.getValue()), 
										toLong(itemComboField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										getCurrencyDescription(getCurrencyID()));
						long prevId = 0;
						int slNo = 0;
						for(PeriodicalAnalysisReportBean bean : list){
							if(prevId != bean.getItemId()){
								subTable.addItem(new Object[]{
										(++slNo)+" ",
										bean.getItem(),
										bean.getMonth(),
										roundNumber(bean.getOpening()) + "",
										roundNumber(bean.getPurchaseOrSale()) + "",
												roundNumber(bean.getAmount()) +" "+bean.getCurrency()}, subTable.getItemIds().size() + 1);
								prevId = bean.getItemId();
							} else {
								subTable.addItem(new Object[]{
										" ",
										" ",
										bean.getMonth(),
										roundNumber(bean.getOpening()) + "",
												roundNumber(bean.getPurchaseOrSale()) + "",
														roundNumber(bean.getAmount()) +" "+getCurrencyDescription(getCurrencyID())}, subTable.getItemIds().size() + 1);
							}
							
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
						List list = periodicalAnalysisReportDao
								.getSalesPeriodicalAnalysisReport(toLong(officeComboField.getValue()), 
										toLong(itemComboField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										getCurrencyDescription(getCurrencyID()));
					/*	long prevId = 0;
						int slNo = 0;*/
						/*for(PeriodicalAnalysisReportBean bean : list){
							if(prevId != bean.getItemId()){
								subTable.addItem(new Object[]{
										(++slNo)+" ",
										bean.getItem(),
										bean.getMonth(),
										roundNumber(bean.getOpening()) + "",
										roundNumber(bean.getPurchase()) + "",
												roundNumber(bean.getAmount()) +" "}, subTable.getItemIds().size() + 1);
								prevId = bean.getItemId();
							} else {
								subTable.addItem(new Object[]{
										" ",
										" ",
										bean.getMonth(),
										roundNumber(bean.getOpening()) + "",
												roundNumber(bean.getPurchase()) + "",
														roundNumber(bean.getAmount()) +" "}, subTable.getItemIds().size() + 1);
							}
							
						}
						*/
						if(list.size() == 0){
							SNotification.show(getPropertyName("no_data_available"), 
									Type.WARNING_MESSAGE);
						} else {
							report.setJrxmlFileName("PeriodicalAnalysisReport");
							report.setReportFileName("Sales Periodical Analysis Report");
							report.setReportTitle(getPropertyName("sales_periodical_analysis_report"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							
							HashMap<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("headTag", getPropertyName("sales"));
							parameters.put("SL_NO_LABEL", getPropertyName("sl_no"));
							parameters.put("ITEM_LABEL", getPropertyName("item"));
							parameters.put("MONTH_LABEL", getPropertyName("month"));
							parameters.put("OPENING_LABEL", getPropertyName("opening"));
							parameters.put("AMOUNT_LABEL", getPropertyName("amount"));
							
							report.createReport(list, parameters);
							list.clear();		
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		return panel;
	}

	protected String getSubTitle() {
		StringBuffer titleStringBuffer = new StringBuffer();
		titleStringBuffer
				.append(getPropertyName("item")+" : "+ ((itemComboField.getValue() == null) ? 
						getPropertyName("all") : itemComboField.getItemCaption(itemComboField.getValue())))		

				.append("\n"+getPropertyName("from_date")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(fromDateField
								.getValue()))
				.append(getPropertyName("to_date")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(toDateField
								.getValue()));
		return titleStringBuffer.toString();
	}
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

	@SuppressWarnings("unchecked")
	private List<ItemModel> getItemList(long office_id) {
		List<ItemModel> list = new ArrayList<ItemModel>();
		list.add(new ItemModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(itemDao.getAllItemsWithCode(office_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getOfficeList() {
		List<S_OfficeModel> list = new ArrayList<S_OfficeModel>();
		list.add(new S_OfficeModel(0, "---- "+getPropertyName("select")+" -----"));
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
