package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.StockTransferInventoryDetails;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.model.LocationModel;
import com.inventory.reports.bean.StockTransferReportBean;
import com.inventory.reports.dao.StockTransferReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class StockTransferReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9088524298374205414L;
	private static final String TBC_SN = "Sl No";
	private static final String TBC_STOCK_TRANSFER_NO = "Transfer No";
	private static final String TBC_DATE = "Date";
	private static final String TBC_TO_BRANCH = "To Branch";
	private static final String TBC_FROM_LOCATION = "From Location";
	private static final String TBC_TO_LOCATION = "To Location";
	private static final String TBC_ITEMS = "Items";
	private static final String TBC_COMMENTS = "Comments";

	private Report report;
	private SOfficeComboField fromOfficeComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField stockTransferNumberComboField;
//	private StockTransferDao stockTransferDao;
	private SComboField fromLocationCombo;
//	private LocationDao locationDao;
	private SComboField toOfficeCombo;
	private OfficeDao officeDao;
	private SComboField toLocationCombo;
	private STable subTable;
	private SButton showButton;
	private StockTransferReportDao stockTransferReportDao;

	@Override
	public SPanel getGUI() {
		stockTransferReportDao = new StockTransferReportDao();
	//	stockTransferDao = new StockTransferDao();
	//	locationDao = new LocationDao();
		officeDao = new OfficeDao();
		setSize(1200, 400);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		report = new Report(getLoginID());

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.setSpacing(true);
		mainHorizontalLayout.setMargin(true);

		fromOfficeComboField = new SOfficeComboField(getPropertyName("from_branch"), 200);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		fromDateField.setImmediate(true);

		dateHorizontalLayout.addComponent(fromDateField);

		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		toDateField.setImmediate(true);

		dateHorizontalLayout.addComponent(toDateField);

		List<StockTransferModel> list = getStockTransferList(getOfficeID());
		stockTransferNumberComboField = new SComboField(
				getPropertyName("stock_transfr_no"), 125, list, "id",
				"comments", false, getPropertyName("all"));

		List<LocationModel> list_1 = getFromLocation(getOfficeID());
		fromLocationCombo = new SComboField(getPropertyName("from_location"),
				200, list_1, "id", "name", false, getPropertyName("all"));

		List<S_OfficeModel> list_2 = getToOfficeList();
		toOfficeCombo = new SComboField(getPropertyName("to_branch"), 200,
				list_2, "id", "name", false, getPropertyName("all"));
		toOfficeCombo.setValue(getOfficeID());

		List<LocationModel> list_3 = getToLocationList(getOfficeID());
		toLocationCombo = new SComboField(getPropertyName("to_location"), 200,
				list_3, "id", "name", false, getPropertyName("all"));

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.SPACEBAR);

		subTable = new STable(null, 750, 200);
		subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		subTable.addContainerProperty(TBC_STOCK_TRANSFER_NO, String.class, null,
				getPropertyName("stock_transfr_no"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.CENTER);
		subTable.addContainerProperty(TBC_TO_BRANCH, String.class, null,
				getPropertyName("to_branch"), null, Align.CENTER);
		subTable.addContainerProperty(TBC_FROM_LOCATION, String.class, null,
				getPropertyName("from_location"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_TO_LOCATION, String.class, null,
				getPropertyName("to_location"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_ITEMS, String.class, null,
				getPropertyName("item"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_COMMENTS, String.class, null,
				getPropertyName("comments"), null, Align.LEFT);

		subTable.setColumnExpandRatio(TBC_STOCK_TRANSFER_NO, (float) 0.9);
		subTable.setColumnExpandRatio(TBC_DATE, (float) 0.9);
		subTable.setColumnExpandRatio(TBC_TO_BRANCH, 1f);
		subTable.setColumnExpandRatio(TBC_FROM_LOCATION, 1);
		subTable.setColumnExpandRatio(TBC_TO_LOCATION, 1);
		subTable.setColumnExpandRatio(TBC_ITEMS, 2);
		subTable.setColumnExpandRatio(TBC_COMMENTS, 1f);

		// subTable.setVisibleColumns(visibleSubColumns);
		subTable.setSelectable(true);

		// subTable.setFooterVisible(true);
		// subTable.setColumnFooter(TBC_DATE, getPropertyName("total"));

		mainFormLayout.addComponent(fromOfficeComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(stockTransferNumberComboField);
		mainFormLayout.addComponent(toOfficeCombo);
		mainFormLayout.addComponent(fromLocationCombo);
		mainFormLayout.addComponent(toLocationCombo);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		buttonHorizontalLayout.addComponent(generateButton);
		buttonHorizontalLayout.addComponent(showButton);

		mainHorizontalLayout.addComponent(mainFormLayout);
		mainHorizontalLayout.addComponent(subTable);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.BOTTOM_CENTER);

		mainHorizontalLayout.setComponentAlignment(mainFormLayout,
				Alignment.MIDDLE_CENTER);
		mainHorizontalLayout.setComponentAlignment(subTable,
				Alignment.MIDDLE_CENTER);

		panel.setContent(mainHorizontalLayout);

		showButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {			
				subTable.removeAllItems();
				try {
					ArrayList<StockTransferModel> stockTransferList = getStockTransferList();
					boolean isDataExist = false;
					for (StockTransferModel model : stockTransferList) {
						isDataExist = true;
						
						subTable.addItem(new Object[] {
								subTable.getItemIds().size() + 1,
								model.getTransfer_no()+"",
								model.getTransfer_date().toString(),
								model.getTo_office().getName(),
								model.getFrom_location().getName(),
								model.getTo_location().getName(),
								getItems(model), model.getComments() },
								subTable.getItemIds().size() + 1);
					}
					if(!isDataExist){
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});
		
		
		generateButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {			
	//		subTable.removeAllItems();
				try {
					ArrayList<StockTransferModel> stockTransferList = getStockTransferList();
					List reportArrayList = new ArrayList<StockTransferReportBean>();
					boolean isDataExist = false;
					for (StockTransferModel model : stockTransferList) {
						isDataExist = true;
						reportArrayList.add(new StockTransferReportBean(model.getTransfer_no()+"",
								model.getTransfer_date().toString(),
								model.getTo_office().getName(),
								model.getFrom_location().getName(),
								model.getTo_location().getName(),
								getItems(model)));
					}
					if(isDataExist){
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("SL_NO_LABEL", getPropertyName("sl_no"));
						map.put("TRANSFER_NO_LABEL", getPropertyName("transfer_no"));
						map.put("DATE_LABEL", getPropertyName("date"));
						map.put("TO_BRANCH_LABEL", getPropertyName("to_branch"));
						map.put("FROM_LOCATION_LABEL", getPropertyName("from_location"));
						map.put("TO_LOCATION_LABEL", getPropertyName("to_location"));
						map.put("ITEMS_LABEL", getPropertyName("items"));
						
						report.setJrxmlFileName("StockTransferReport");
						report.setReportFileName("StockTransferReport");
						report.setReportTitle(getPropertyName("stock_transfer_report"));
						report.setReportSubTitle(getSubTitle());
						report.setReportType(toInt(reportChoiceField
								.getValue().toString()));
						report.setIncludeHeader(true);
						report.setOfficeName(fromOfficeComboField
								.getItemCaption(fromOfficeComboField
										.getValue()));
						report.createReport(reportArrayList, map);

						reportArrayList.clear();
						stockTransferList.clear();
					} else {
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private String getSubTitle() {
				StringBuffer titleStringBuffer = new StringBuffer();
				titleStringBuffer.append(getPropertyName("from_office")+" : "+fromOfficeComboField.getItemCaption(fromOfficeComboField.getValue()))
						.append("\n"+getPropertyName("from_date")+" : "+CommonUtil.formatDateToDDMMYYYY(fromDateField.getValue()))
						.append(getPropertyName("to_date")+" : "+CommonUtil.formatDateToDDMMYYYY(toDateField.getValue()));				 
				return titleStringBuffer.toString();
			}			
		});

		fromOfficeComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				reloadStockTransferComboField();
				reloadFromLocationComboField();
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
		stockTransferNumberComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});
		toOfficeCombo.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				reloadToLocationComboField();
			}		
		});
		
		fromLocationCombo.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();

			}
		});
		
		toLocationCombo.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();

			}
		});
		return panel;
	}
	
	private String getItems(StockTransferModel model) {
		StringBuffer itemBuffer = new StringBuffer();
		Iterator<StockTransferInventoryDetails> itr = model
				.getInventory_details_list().iterator();
		while (itr.hasNext()) {
			StockTransferInventoryDetails inv_list = itr.next();
			itemBuffer.append(inv_list.getStock_id().getItem()
					.getName()
					+ "("
					+ inv_list.getQuantity()
					+ " "
					+ inv_list.getUnit().getSymbol() + "); ");
		}

		return itemBuffer.toString();
	}

	@SuppressWarnings("unchecked")
	private ArrayList<StockTransferModel> getStockTransferList()
			throws Exception {
		return (ArrayList<StockTransferModel>) stockTransferReportDao.getStockTransferList(
				toLong(fromOfficeComboField.getValue()),
				CommonUtil.getSQLDateFromUtilDate(fromDateField
						.getValue()),
				CommonUtil.getSQLDateFromUtilDate(toDateField
						.getValue()),
				toLong(stockTransferNumberComboField.getValue()), 
				toLong(toOfficeCombo.getValue()), 
				toLong(fromLocationCombo.getValue()), 
				toLong(toLocationCombo	.getValue()));
	}
	
	
	private void reloadToLocationComboField() {
		List<LocationModel> list = getToLocationList(toLong(toOfficeCombo.getValue()));
		toLocationCombo.setContainerDataSource(SCollectionContainer.setList(list, "id"));
		toLocationCombo.setItemCaptionPropertyId("name");	
	}
	private void reloadFromLocationComboField() {
		List<LocationModel> list = getFromLocation(toLong(fromOfficeComboField.getValue()));
		fromLocationCombo.setContainerDataSource(SCollectionContainer.setList(list, "id"));
		fromLocationCombo.setItemCaptionPropertyId("name");		
	}
	private void reloadStockTransferComboField() {
		List<StockTransferModel> list = getStockTransferList(toLong(fromOfficeComboField.getValue()));
		stockTransferNumberComboField.setContainerDataSource(SCollectionContainer.setList(list, "id"));
		stockTransferNumberComboField.setItemCaptionPropertyId("comments");		
	}
	private long toLong(Object obj){
		if(obj == null){
			return 0;
		} else {
			return toLong(obj.toString());
		}
	}
	private List<StockTransferModel> getStockTransferList(long office_id) {
		List<StockTransferModel> list = new ArrayList<StockTransferModel>();
		list.add(new StockTransferModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(stockTransferReportDao
					.getAllStockTransferNumbersAsComment(office_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private List<LocationModel> getFromLocation(long office_id) {
		List<LocationModel> list_1 = new ArrayList<LocationModel>();
		list_1.add(new LocationModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list_1.addAll(stockTransferReportDao.getLocationModelList(office_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list_1;
	}

	private List<S_OfficeModel> getToOfficeList() {
		List<S_OfficeModel> list_2 = new ArrayList<S_OfficeModel>();
		list_2.add(new S_OfficeModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list_2.addAll(officeDao
					.getAllOfficeNamesUnderOrg(getOrganizationID()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list_2;
	}

	private List<LocationModel> getToLocationList(long office_id) {
		List<LocationModel> list_3 = new ArrayList<LocationModel>();
		list_3.add(new LocationModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list_3.addAll(stockTransferReportDao.getLocationModelList(office_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list_3;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
