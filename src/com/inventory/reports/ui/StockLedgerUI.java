package com.inventory.reports.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.reports.bean.StockLedgerBean;
import com.inventory.reports.dao.StockLedgerDao;
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
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class StockLedgerUI extends SparkLogic implements Serializable{

	private Report report;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable subTable;
	private StockLedgerDao stockLedgerDao;

	private static final String TBC_ITEM = "Item";
	private static final String TBC_DATE = "Date";
	private static final String TBC_LEDGER = "Ledger";
	private static final String TBC_COMMENTS = "Comments";
	private static final String TBC_RECEIVED = "Received";
	private static final String TBC_ISSUED = "Issued";
	private static final String TBC_BALANCE = "Balance";
	private OfficeDao officeDao;
	private ItemDao itemDao;
	private SComboField itemGroupComboField;
	private SComboField itemSubGroupComboField;
	private ItemSubGroupDao itemSubGroupDao;

	@Override
	public SPanel getGUI() {
		setSize(1200, 400);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		stockLedgerDao = new StockLedgerDao();
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		report = new Report(getLoginID());
		itemSubGroupDao = new ItemSubGroupDao();

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());
		
		List<ItemGroupModel> groupList = getItemGroupList(getOfficeID());
		itemGroupComboField = new SComboField(getPropertyName("item_group"), 125, groupList,
				"id", "name", false, getPropertyName("all"));
		itemGroupComboField.setValue((long)0);
		
		List<ItemSubGroupModel> subGroupList = getItemSubGroupList(getOfficeID(),toLong(itemGroupComboField.getValue()) );
		itemSubGroupComboField = new SComboField(getPropertyName("item_sub_group"), 125, subGroupList,
				"id", "name", false, getPropertyName("all"));
		itemSubGroupComboField.setValue((long)0);

		List<ItemModel> list = getItemList(getOfficeID(),toLong(itemSubGroupComboField.getValue()));
		itemComboField = new SComboField(getPropertyName("item"), 125, list,
				"id", "name", false, getPropertyName("all"));
		itemComboField.setValue((long)0);

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
		mainFormLayout.addComponent(itemComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		subTable = new STable(null, 750, 200);
		subTable.addContainerProperty(TBC_ITEM, String.class, null,
				getPropertyName("item"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_LEDGER, String.class, null,
				getPropertyName("ledger"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_COMMENTS, String.class, null,
				getPropertyName("comments"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_RECEIVED, String.class, null,
				getPropertyName("received_qty"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_ISSUED, String.class, null,
				getPropertyName("issued_qty"), null, Align.RIGHT);
		subTable.addContainerProperty(TBC_BALANCE, String.class, null,
				getPropertyName("balance_qty"), null, Align.RIGHT);

		subTable.setColumnExpandRatio(TBC_ITEM, 1f);
		subTable.setColumnExpandRatio(TBC_DATE, 1f);
		subTable.setColumnExpandRatio(TBC_LEDGER, 1f);
		subTable.setColumnExpandRatio(TBC_COMMENTS, 1f);
		subTable.setColumnExpandRatio(TBC_RECEIVED, 1f);
		subTable.setColumnExpandRatio(TBC_ISSUED, 1f);
		subTable.setColumnExpandRatio(TBC_BALANCE, 1f);

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
				List<ItemModel> list = getItemList(toLong(officeComboField
						.getValue()),toLong(itemSubGroupComboField.getValue()));
				itemComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				itemComboField.setItemCaptionPropertyId("name");
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

		itemComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();

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
					List<StockLedgerBean> list = null;
					try {
						list = getStockStockLedgerList();
					} catch (Exception e) {
						e.printStackTrace();
					}
					boolean isDataExist = false;
					boolean isFirst = true;
					for (StockLedgerBean bean : list) {
						isDataExist = true;
						if(isFirst){
							if((Long)itemComboField.getValue()!=0){
							subTable.addItem(
									new Object[] {"", "",
											bean.getLedger(),
											bean.getComments(),
											(bean.getReceivedQty() != 0) ? bean.getReceivedQty()+"" : "",
											(bean.getIssuedQty() != 0) ? bean.getIssuedQty()+"" : "",
											bean.getBalanceQty() + "" },
											subTable.getItemIds().size() + 1);
							}
							isFirst = false;
						} else {
							subTable.addItem(
									new Object[] {bean.getItem(),
											bean.getDateString(),
											bean.getLedger(),
											bean.getComments(),
											(bean.getReceivedQty() != 0) ? bean.getReceivedQty()+"" : "",
											(bean.getIssuedQty() != 0) ? bean.getIssuedQty()+"" : "",
											bean.getBalanceQty() + "" },
											subTable.getItemIds().size() + 1);
						}
						
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

					List stockLedgerBeanList = null;
					try {
						stockLedgerBeanList = getStockStockLedgerList();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println("============= "+list.size());
					boolean isDataExist = false;
					if(stockLedgerBeanList.size() > 0){
						isDataExist = true;
					} 
						
					/*for (StockLedgerBean bean : list) {
						isDataExist = true;
						subTable.addItem(
								new Object[] {bean.getDateString(),
										bean.getLedger(),
										bean.getComments(),
										(bean.getReceivedQty() != 0) ? bean.getReceivedQty()+"" : "",
										(bean.getIssuedQty() != 0) ? bean.getIssuedQty()+"" : "",
										bean.getBalanceQty() + "" },
										subTable.getItemIds().size() + 1);
					}
*/
					if (!isDataExist) {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}

				

						if (isDataExist) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("LEDGER_LABEL", getPropertyName("ledger"));
							map.put("COMMENTS_LABEL", getPropertyName("comments"));
							map.put("ISSUED_QTY_LABEL", getPropertyName("issued_qty"));
							map.put("RECEIVED_QTY_LABEL", getPropertyName("received_qty"));
							map.put("BALANCE_QTY_LABEL", getPropertyName("balance_qty"));
							report.setJrxmlFileName("StockLedger");
							report.setReportFileName("StockLedger");
							report.setReportTitle(getPropertyName("stock_ledger"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(stockLedgerBeanList, map);

							stockLedgerBeanList.clear();						
						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					
				}

			}

			private String getSubTitle() {
				StringBuffer titleStringBuffer = new StringBuffer();
				titleStringBuffer
				.append(getPropertyName("office")+" : "+ officeComboField
						.getItemCaption(officeComboField.getValue()))		
						
						.append("\n"+getPropertyName("item_group")+" : "+ itemGroupComboField
								.getItemCaption(itemGroupComboField.getValue()))		
								
								.append("\n"+getPropertyName("item_sub_group")+" : "+ itemSubGroupComboField
								.getItemCaption(itemSubGroupComboField.getValue()))		
						
						.append("\n"+getPropertyName("item")+" : "+ itemComboField
								.getItemCaption(itemComboField.getValue()))		

						.append("\n"+getPropertyName("from_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDateField
										.getValue()))
						.append(" "+getPropertyName("to_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(toDateField
										.getValue()));
				return titleStringBuffer.toString();
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
				List<ItemSubGroupModel> list = getItemSubGroupList(toLong(officeComboField
						.getValue()), toLong(itemGroupComboField.getValue()));
				itemSubGroupComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				itemSubGroupComboField.setItemCaptionPropertyId("name");
				itemSubGroupComboField.setValue((long)0);
			}
		});
		
		itemSubGroupComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				List<ItemModel> list = getItemList(toLong(officeComboField
						.getValue()), toLong(itemSubGroupComboField.getValue()));
				itemComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				itemComboField.setItemCaptionPropertyId("name");
				itemComboField.setValue((long)0);
			}
		});

		return panel;
	}

	@SuppressWarnings("unchecked")
	private List<ItemSubGroupModel> getItemSubGroupList(long officeID,
			long groupId) {
		List<ItemSubGroupModel> list = new ArrayList<ItemSubGroupModel>();
		list.add(new ItemSubGroupModel((long)0, "------- "+getPropertyName("all")+" -------"));
		try {
			list.addAll(itemSubGroupDao.getAllActiveItemSubGroups(groupId));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	private List<ItemGroupModel> getItemGroupList(long officeID) {
		List<ItemGroupModel> list = new ArrayList<ItemGroupModel>();
		list.add(0, new ItemGroupModel(0, "------- "+getPropertyName("all")+" -------"));
		try {
			list.addAll(new ItemGroupDao().getAllActiveItemGroupsNames());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	private List<StockLedgerBean> getStockStockLedgerList() {
		List<StockLedgerBean> list = null;
		try {
			list = stockLedgerDao.getStockLedger(toLong(officeComboField.getValue()),
					toLong(itemGroupComboField.getValue()),
					toLong(itemSubGroupComboField.getValue()),
					toLong(itemComboField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField.getValue()));
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

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

	private long toLong(Object obj) {
		if (obj == null) {
			return 0;
		} else {
			return toLong(obj.toString());
		}
	}

	private List<ItemModel> getItemList(long office_id, long itemGroupId) {
		List<ItemModel> list = new ArrayList<ItemModel>();
		list.add(new ItemModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(itemDao.getAllActiveItems(office_id, 0, itemGroupId, 0, getOrganizationID()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		if (itemGroupComboField.getValue() == null) {
			setRequiredError(itemGroupComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(itemGroupComboField, null, false);
		}
//		if (itemComboField.getValue() == null) {
//			setRequiredError(itemComboField,
//					getPropertyName("invalid_selection"), true);
//			valid = false;
//		} else {
//			setRequiredError(itemComboField, null, false);
//		}
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
