package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.reports.bean.StockTransactionAnalysisReportBean;
import com.inventory.reports.dao.StockTransactionAnalysisReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

public class StockTransactionAnalysisReportUI extends SparkLogic {

	private Report report;
	private SOfficeComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemGroupComboField;
	private SComboField itemSubGroupComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable subTable;
	private ItemGroupDao itemGroupDao;
	private ItemSubGroupDao itemSubGroupDao;
	private StockTransactionAnalysisReportDao stockTransactionAnalysisReportDao;

	private static final String TBC_SN = "Sl No";
	private static final String TBC_ITEM_NAME = "Item Name";
	private static final String TBC_OPENING_QTY = "Opening Qty";
	private static final String TBC_RECEIVED_QTY = "Received Qty";
	private static final String TBC_ISSUED_QTY = "Issued Qty";
	private static final String TBC_BALANCE_QTY = "Balance Qty";
	private final int FAST_MOVING = 0;
	private final int SLOW_MOVING = 1;

	@Override
	public SPanel getGUI() {
		setSize(1200, 400);

		SPanel panel = new SPanel();
		panel.setSizeFull();
		itemGroupDao = new ItemGroupDao();
		itemSubGroupDao = new ItemSubGroupDao();
		stockTransactionAnalysisReportDao = new StockTransactionAnalysisReportDao();
		report = new Report(getLoginID());

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		List<ItemGroupModel> list = getItemGroupList(getOrganizationID());
		itemGroupComboField = new SComboField(getPropertyName("item_group"),
				125, list, "id", "name", false, getPropertyName("all"));

		List<ItemSubGroupModel> list_1 = getItemSubGroupList(toLong(itemGroupComboField
				.getValue()));
		itemSubGroupComboField = new SComboField(
				getPropertyName("item_sub_grp"), 200, list_1, "id", "name",
				false, getPropertyName("all"));
		
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
		mainFormLayout.addComponent(itemGroupComboField);
		mainFormLayout.addComponent(itemSubGroupComboField);
	//	mainFormLayout.addComponent(movingStatusRadioButton);
		mainFormLayout.addComponent(dateHorizontalLayout);
	//	mainFormLayout.addComponent(noOfRowsTextField);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		subTable = new STable(null, 750, 200);
		subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		subTable.addContainerProperty(TBC_ITEM_NAME, String.class, null,
				getPropertyName("item_name"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_OPENING_QTY, String.class, null,
				getPropertyName("opening_qty"), null, Align.RIGHT);		
		subTable.addContainerProperty(TBC_ISSUED_QTY, String.class, null,
				getPropertyName("issued_qty"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_RECEIVED_QTY, String.class, null,
				getPropertyName("received_qty"), null, Align.RIGHT);		
		subTable.addContainerProperty(TBC_BALANCE_QTY, String.class, null,
				getPropertyName("balance_qty"), null, Align.RIGHT);

		subTable.setColumnExpandRatio(TBC_ITEM_NAME, 1f);
		subTable.setColumnExpandRatio(TBC_OPENING_QTY, 1f);
		subTable.setColumnExpandRatio(TBC_RECEIVED_QTY, 1f);
		subTable.setColumnExpandRatio(TBC_ISSUED_QTY, 1f);
		subTable.setColumnExpandRatio(TBC_BALANCE_QTY, 1f);

		// subTable.setVisibleColumns(visibleSubColumns);
		subTable.setSelectable(true);

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.addComponent(mainFormLayout);
		mainHorizontalLayout.addComponent(subTable);

		panel.setContent(mainHorizontalLayout);

		officeComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});

		itemSubGroupComboField
				.addValueChangeListener(new ValueChangeListener() {

					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						subTable.removeAllItems();
					}
				});

		fromDateField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});
		toDateField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});
	

		itemGroupComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				loadItemSubGroupComboField();
			}

			private void loadItemSubGroupComboField() {
				List<ItemSubGroupModel> list = getItemSubGroupList(toLong(itemGroupComboField
						.getValue()));
				itemSubGroupComboField
						.setContainerDataSource(SCollectionContainer.setList(
								list, "id"));
				itemSubGroupComboField.setItemCaptionPropertyId("name");
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
					List<StockTransactionAnalysisReportBean> list = null;
					try {
						list = getStockTransactionAnalysisReportList();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println("============= "+list.size());
					
					boolean isDataExist = false;
					for (StockTransactionAnalysisReportBean bean : list) {						
						isDataExist = true;
						
						subTable.addItem(
								new Object[] {
										subTable.getItemIds().size() + 1,
										bean.getItemName(),
										bean.getOpeningQty()+"",
										bean.getIssuedQty() + "",
										bean.getReceivedQty()+"",
										bean.getBalanceQty()+ "" }, subTable
										.getItemIds().size() + 1);				
					}

					if (!isDataExist) {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}

				}
			}

		});

		generateButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// subTable.removeAllItems();
				if (isValid()) {
					try {
						List<StockTransactionAnalysisReportBean> list = null;
						try {
							list = getStockTransactionAnalysisReportList();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boolean isDataExist = false;
						
						List reportList = new ArrayList<StockTransactionAnalysisReportBean>();
						for (StockTransactionAnalysisReportBean bean : list) {
							isDataExist = true;
							reportList.add(bean);							
						}

						if (isDataExist) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("OPENING_QTY_LABEL", getPropertyName("opening_qty"));
							map.put("ISSUED_QTY_LABEL", getPropertyName("issued_qty"));
							map.put("RECEIVED_QTY_LABEL", getPropertyName("received_qty"));
							map.put("BALANCE_QTY_LABEL", getPropertyName("balance_qty"));
							
							report.setJrxmlFileName("StockTransactionAnalysisReport");
							report.setReportFileName("StockTransactionAnalysisReport");
							report.setReportTitle(getPropertyName("stock_transaction_analysis_report"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);

							reportList.clear();
							list.clear();
						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			private String getSubTitle() {
				StringBuffer titleStringBuffer = new StringBuffer();
				titleStringBuffer
						.append(getPropertyName("item_group")+" : ")
						.append((itemGroupComboField.getValue() != null) ? itemGroupComboField
								.getItemCaption(itemGroupComboField.getValue())
								: getPropertyName("all"))
						
						.append("\n"+getPropertyName("item_sub_group")+" : ")
						.append((itemSubGroupComboField.getValue() != null) ? itemSubGroupComboField
								.getItemCaption(itemSubGroupComboField.getValue())
								: getPropertyName("all"))					
						.append("\n"+getPropertyName("from_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDateField
										.getValue()))
						.append(getPropertyName("to_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(toDateField
										.getValue()));
				return titleStringBuffer.toString();
			}
		});

		return panel;
	}

	private List<StockTransactionAnalysisReportBean> getStockTransactionAnalysisReportList()
			throws Exception {
		List<StockTransactionAnalysisReportBean> list;
		list = stockTransactionAnalysisReportDao.getItemListWithPurchaseAndSale(
				getOrganizationID(), toLong(officeComboField.getValue()),
				toLong(itemGroupComboField.getValue()),
				toLong(itemSubGroupComboField.getValue()),
				CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
				CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
	
		return list;
	}

	private long toLong(Object obj) {
		if (obj == null) {
			return 0;
		} else {
			return toLong(obj.toString());
		}
	}

	/*
	 * private long toInt(Object obj) { if (obj == null) { return 0; } else {
	 * return toInt(obj.toString()); } }
	 */

	private List<ItemSubGroupModel> getItemSubGroupList(long item_group_id) {
		List<ItemSubGroupModel> list = new ArrayList<ItemSubGroupModel>();
		list.add(new ItemSubGroupModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			if (item_group_id == 0) {
				list.addAll(itemSubGroupDao
						.getAllActiveItemSubGroupsNames(getOrganizationID()));
			} else {
				list.addAll(itemSubGroupDao
						.getAllActiveItemSubGroups(item_group_id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<ItemGroupModel> getItemGroupList(long organization_id) {
		List<ItemGroupModel> list = new ArrayList<ItemGroupModel>();
		list.add(new ItemGroupModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(itemGroupDao.getAllItemGroupsNames(organization_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Boolean isValid() {
		
		return true;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
