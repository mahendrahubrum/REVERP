package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.dao.LocationDao;
import com.inventory.model.LocationModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.StockValuationReportBean;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class StockValuationReportUI extends SparkLogic{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2054277795892634844L;
	private Report report;
	private SComboField officeComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SComboField itemComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private ItemDao itemDao;
	private OfficeDao officeDao;
	private SComboField locationCombo;
	private LocationDao locationDao;
	@Override
	public SPanel getGUI() {
		setSize(500, 350);
		
		SPanel panel = new SPanel();
		panel.setSizeFull();

		report = new Report(getLoginID());
		itemDao = new ItemDao();
		officeDao = new OfficeDao();
		locationDao = new LocationDao();
		
		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("Select"));
		officeComboField.setValue(getOfficeID());
		
		List<ItemModel> list = getItemList(getOfficeID());
		itemComboField = new SComboField(getPropertyName("item"), 200, list,
				"id", "name", false, getPropertyName("all"));
		
	
		locationCombo = new SComboField(getPropertyName("location"), 200,
					getLocationList(getOfficeID()), "id",
					"name", false, getPropertyName("all"));
	

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
		generateButton.setClickShortcut(KeyCode.ENTER);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(itemComboField);
		mainFormLayout.addComponent(locationCombo);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(generateButton);
		panel.setContent(mainFormLayout);
		
		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					List reportList = new ArrayList();
					StockValuationReportBean beans = null;
					ItemStockModel model = null;
					LocationDao locationDao = new LocationDao();

					try {

						List list = itemDao.getItemStockModelList(toLong(officeComboField.getValue()),
								toLong(itemComboField.getValue()),
								toLong(locationCombo.getValue()),
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								model = (ItemStockModel) list.get(i);
//System.out.println("=========model.getLocation_id() = ========= "+model.getLocation_id());
								if(model.getLocation_id() == 0){
									beans = new StockValuationReportBean(
											model.getItem().getName(), 
											"None",
											model.getQuantity()+"",
											model.getRate()+"",
											CommonUtil.formatDateToDDMMYYYY(model.getDate_time()),
											model.getBalance()+"");
								} else {
									beans = new StockValuationReportBean(
											model.getItem().getName(), 
											locationDao.getLocationModel(model.getLocation_id()).getName(),
											model.getQuantity()+"",
											model.getRate()+"",
											CommonUtil.formatDateToDDMMYYYY(model.getDate_time()),
											model.getBalance()+"");
								}
								
								reportList.add(beans);
							}
							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("LOCATION_LABEL", getPropertyName("location"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								map.put("RATE_LABEL", getPropertyName("rate"));
								map.put("CURRENT_STOCK_LABEL", getPropertyName("current_stock"));
								
								
								report.setJrxmlFileName("StockValuationReport");
								report.setReportFileName("StockValuationReport");
								report.setReportTitle(getPropertyName("stock_valuation_report"));
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setReportSubTitle(getReportSubTitle());
								report.setIncludeHeader(true);
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, map);

								reportList.clear();
								list.clear();
							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
						}else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

			private String getReportSubTitle() {
				StringBuffer title = new StringBuffer();
				title.append(getPropertyName("item")+" : "+(toLong(itemComboField.getValue()) == 0  ? 
						getPropertyName("all") : itemComboField.getItemCaption(itemComboField.getValue())))
					
					.append("\n"+getPropertyName("location")+" : "+(toLong(locationCombo.getValue()) == 0  ?
							getPropertyName("all") : locationCombo.getItemCaption(locationCombo.getValue())))
						.append("\n"+getPropertyName("from_date")+" : "+CommonUtil.formatDateToDDMMMYYYY(fromDateField.getValue()))
						.append("\t "+getPropertyName("to_date")+" : "+CommonUtil.formatDateToDDMMMYYYY(toDateField.getValue()));
				return title.toString();
			}
		});
		officeComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				List<ItemModel> list = getItemList(toLong(officeComboField
						.getValue()));
				itemComboField.setContainerDataSource(SCollectionContainer
						.setList(list, "id"));
				itemComboField.setItemCaptionPropertyId("name");
				
				List<LocationModel> list1 = getLocationList(toLong(officeComboField
						.getValue()));
				locationCombo.setContainerDataSource(SCollectionContainer
						.setList(list1, "id"));
				locationCombo.setItemCaptionPropertyId("name");
			}
		});
		return panel;
	}
	private List getLocationList(long officeId) {
		
		List<LocationModel> list = new ArrayList<LocationModel>();
		list.add(new LocationModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(locationDao.getLocationModelList(officeId));
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	private List<ItemModel> getItemList(long office_id) {
		List<ItemModel> list = new ArrayList<ItemModel>();
		list.add(new ItemModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(itemDao.getAllItemsWithCode(office_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
