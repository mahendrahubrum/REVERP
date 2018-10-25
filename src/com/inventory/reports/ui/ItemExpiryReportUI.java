package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.dao.LocationDao;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.model.LocationModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.ItemReportBean;
import com.inventory.reports.dao.ItemExpiryReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.ReportReview;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithReview;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

/**
 * @author Anil. K P
 * 
 *         Jul 8, 2013
 */


@SuppressWarnings("serial")
public class ItemExpiryReportUI extends SparkLogic {

	private SOfficeComboField officeCombo;
	private SComboField itemGroupCombo;
	private SComboField itemSubGroupCombo;
	private SComboField itemComboField;
	private SComboField locationCombo;

//	SCheckBox isRackWise;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private ItemSubGroupDao itemSubGroupDao;
	private ItemDao itemDao;
	CommonMethodsDao comDao;
	SDateField date;
	ItemExpiryReportDao dao;

	private SReportChoiceField reportChoiceField;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	SConfirmWithReview confirmBox;
	ReportReview review;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM = "Item";
	static String TBC_STOCK = "Stock";
	static String TBC_PURCHASE_TYPE = "Purchase Type";
	static String TBC_REAL = "Balance";
	static String TBC_MANUFACTURING = "Manufacturing Date";
	static String TBC_EXPIRY = "Expiry Date";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_STOCK,TBC_PURCHASE_TYPE,TBC_REAL, TBC_MANUFACTURING,TBC_EXPIRY};
		visibleColumns = new Object[] { TBC_SN, TBC_ITEM,TBC_STOCK,TBC_PURCHASE_TYPE,TBC_REAL, TBC_MANUFACTURING,TBC_EXPIRY};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_STOCK, String.class, null,getPropertyName("stock"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE_TYPE, String.class, null,getPropertyName("purchase_type"), null, Align.LEFT);
		table.addContainerProperty(TBC_REAL, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
		table.addContainerProperty(TBC_MANUFACTURING, String.class, null,getPropertyName("manuf_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_EXPIRY, String.class, null,getPropertyName("expiry_date"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, 1);
		table.setColumnExpandRatio(TBC_REAL, 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		setSize(1050, 350);
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		formLayout = new SFormLayout();
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		itemSubGroupDao = new ItemSubGroupDao();
		itemDao = new ItemDao();
		comDao = new CommonMethodsDao();
		dao = new ItemExpiryReportDao();

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		date = new SDateField(getPropertyName("date"), 120, getDateFormat(),
				getWorkingDate());

		officeCombo = new SOfficeComboField(getPropertyName("office"), 200);
		officeCombo.setValue(null);

		List groupList=new ArrayList();
		try {
			groupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
			groupList.addAll(new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		itemGroupCombo = new SComboField(getPropertyName("item_group"),200, groupList, "id", "name",false,getPropertyName("all"));
		itemGroupCombo.setValue((long)0);
		itemSubGroupCombo = new SComboField(getPropertyName("item_sub_group"), 200, null, "id", "name",false,getPropertyName("all"));
		reloadSubGroupCombo();
		itemComboField = new SComboField(getPropertyName("item"), 200,null, "id", "name", false, getPropertyName("all"));
		locationCombo=new SComboField(getPropertyName("location"), 200, null, "id", "name", false, getPropertyName("all"));

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		formLayout.addComponent(officeCombo);
		formLayout.addComponent(itemGroupCombo);
		formLayout.addComponent(itemSubGroupCombo);
		formLayout.addComponent(itemComboField);
		formLayout.addComponent(locationCombo);
		formLayout.addComponent(date);
		formLayout.addComponent(reportChoiceField);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);

		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getButton().getId().equals("1")) {
					try {
						saveReview(getOptionId(),confirmBox.getTitle(),confirmBox.getComments()	,getLoginID(),report.getReportFile());
						SNotification.show(getPropertyName("review_saved"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				confirmBox.close();
				confirmBox.setTitle("");
				confirmBox.setComments("");
			}
			
		};
		confirmBox.setClickListener(confirmListener);
		
		review.setClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals(ReportReview.REVIEW)){
					if(generateReport())
						confirmBox.open();
				}
			}
		});
		
		
		itemGroupCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					reloadSubGroupCombo();
					reloadItemCombo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
		itemSubGroupCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					reloadItemCombo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		officeCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (selected(officeCombo)){
						reloadItemCombo();
						List locationList=new ArrayList();
						locationList.add(0, new LocationModel(0, getPropertyName("all")));
						locationList.addAll(new LocationDao().getLocationModelList((Long)officeCombo.getValue()));
						locationCombo.setContainerDataSource(SCollectionContainer.setList(locationList, "id"));
						locationCombo.setItemCaptionPropertyId("name");
						locationCombo.setValue((long)0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		officeCombo.setValue(getOfficeID());
		
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item itm = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_details")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("item"),itm.getItemProperty(TBC_ITEM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("stock"),itm.getItemProperty(TBC_STOCK).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("purchase_type"),itm.getItemProperty(TBC_PURCHASE_TYPE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("quantity"),itm.getItemProperty(TBC_REAL).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("manufacturing date"),itm.getItemProperty(TBC_MANUFACTURING).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("expiry_date"),itm.getItemProperty(TBC_EXPIRY).getValue().toString()));
						form.setStyleName("grid_max_limit");
						popupContainer.removeAllComponents();
						SPopupView pop = new SPopupView("", form);
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		
		showButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					showReport();
				}
			}
		});
		
		
		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
				}
			}
		});
		
		
		
		mainHorizontal.addComponent(formLayout);
		mainHorizontal.addComponent(table);
		mainHorizontal.addComponent(popupContainer);
		
		review.addComponent(mainHorizontal, "left: 0px; right: 0px; z-index:-1;");
		mainPanel.setContent(review);

		return mainPanel;
	}

	
	
	@SuppressWarnings("rawtypes")
	protected boolean showReport() {
		boolean flag=false;
		try {
			List itemReportList=new ArrayList();
			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			ArrayList<Object> reportList = new ArrayList<Object>();

			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			long location = 0;
			if (selected(itemGroupCombo)) {
				groupId = toLong(itemGroupCombo.getValue().toString());
			}

			if (selected(itemSubGroupCombo)) {
				subgroupId = toLong(itemSubGroupCombo.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}
			
			if (selected(locationCombo)) {
				location = toLong(locationCombo.getValue().toString());
			}

			itemReportList = dao.getAllExpiredItems((Long)officeCombo.getValue(),
														itemId,
														subgroupId,
														groupId,
														location,
														getOrganizationID(),CommonUtil.getSQLDateFromUtilDate(date.getValue()));
			
			if(itemReportList.size()>0){
			
			ItemStockModel allModel;

			for (int i = 0; i < itemReportList.size(); i++) {
				
					allModel = (ItemStockModel) itemReportList.get(i);
					
					PurchaseModel purMdl=new PurchaseDao().getPurchaseModel(allModel.getPurchase_id());
					
					String purchaseType="";
					if(allModel.getPurchase_type()==SConstants.stockPurchaseType.PURCHASE_GRN)
						purchaseType="Purchase GRN";
					else if(allModel.getPurchase_type()==SConstants.stockPurchaseType.PURCHASE)
						purchaseType="Purchase";
					else if(allModel.getPurchase_type()==SConstants.stockPurchaseType.SALES_RETURN)
						purchaseType="Sales return";
					else if(allModel.getPurchase_type()==SConstants.stockPurchaseType.STOCK_TRANSFER)
						purchaseType="Stock Transfer";
						
					table.addItem(new Object[]{ table.getItemIds().size()+1,
													allModel.getId(),
													allModel.getItem().getName()+" [ "+allModel.getItem().getItem_code()+" ]",
													"ID: "+purMdl.getPurchase_no()+"["+purMdl.getDate()+"]",
													purchaseType,
													roundNumber(allModel.getBalance()),
													CommonUtil.formatDateToCommonFormat(allModel.getManufacturing_date())
													,CommonUtil.formatDateToCommonFormat(allModel.getExpiry_date())
													},table.getItemIds().size()+1);
						
				}
			table.setVisibleColumns(visibleColumns);
			table.sort(new Object[]{TBC_ITEM}, new boolean[]{true});
			
			}else{
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	

	@SuppressWarnings("rawtypes")
	protected boolean generateReport() {
		boolean flag=false;
		try {

			ArrayList<Object> reportList = new ArrayList<Object>();
			List itemReportList = new ArrayList();
			ItemReportBean bean = new ItemReportBean();

			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			long location = 0;
			if (selected(itemGroupCombo)) {
				groupId = toLong(itemGroupCombo.getValue().toString());
			}

			if (selected(itemSubGroupCombo)) {
				subgroupId = toLong(itemSubGroupCombo.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}
			
			if (selected(locationCombo)) {
				location = toLong(locationCombo.getValue().toString());
			}
			
			itemReportList = dao.getAllExpiredItems((Long)officeCombo.getValue(), itemId, subgroupId, groupId,location,getOrganizationID()
					,CommonUtil.getSQLDateFromUtilDate(date.getValue()));

			ItemStockModel allModel;

				for (int i = 0; i < itemReportList.size(); i++) {
					allModel = (ItemStockModel) itemReportList.get(i);
					
					String purchaseType="";
					if(allModel.getPurchase_type()==SConstants.stockPurchaseType.PURCHASE_GRN)
						purchaseType="Purchase GRN";
					else if(allModel.getPurchase_type()==SConstants.stockPurchaseType.PURCHASE)
						purchaseType="Purchase";
					else if(allModel.getPurchase_type()==SConstants.stockPurchaseType.SALES_RETURN)
						purchaseType="Sales return";
					else if(allModel.getPurchase_type()==SConstants.stockPurchaseType.STOCK_TRANSFER)
						purchaseType="Stock Transfer";
					
					PurchaseModel purMdl=new PurchaseDao().getPurchaseModel(allModel.getPurchase_id());

						bean = new ItemReportBean();
						bean.setName(allModel.getItem().getName());
						bean.setStock("ID: "+purMdl.getPurchase_no()+"["+purMdl.getDate()+"]");
						bean.setPurchaseType(purchaseType);
						bean.setCurrent_quantity(allModel.getBalance());
						bean.setManufacturingDate(CommonUtil.formatDateToCommonFormat(allModel.getManufacturing_date()));
						bean.setExpiryDate(CommonUtil.formatDateToCommonFormat(allModel.getExpiry_date()));
						reportList.add(bean);
				}
				report.setJrxmlFileName("ItemExpiry");
				report.setReportFileName("ItemExpiry");

			if (reportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("item_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("STOCK_LABEL", getPropertyName("stock"));
				map.put("QUANTITY_LABEL", getPropertyName("quantity"));
				map.put("GRV_STOCK_LABEL", getPropertyName("GRV_stock"));
				map.put("TOTAL_STOCK_LABEL", getPropertyName("total_stock"));
				map.put("MANUFACTURE_DATE_LABEL", getPropertyName("manufacture_date"));
				map.put("EXPIRY_DATE_LABEL", getPropertyName("expiry_date"));
				map.put("RACK_LABEL", getPropertyName("rack"));
				map.put("QUANTITY_LABEL", getPropertyName("quantity"));
				map.put("PURCHASE_TYPE_LABEL", getPropertyName("purchase_type"));
				
				
				
				
				String subTitle = "";
				if (selected(itemGroupCombo)) {
					subTitle += getPropertyName("item_group")+" : "
							+ itemGroupCombo
									.getItemCaption(itemGroupCombo
											.getValue());
				}
				if (selected(itemSubGroupCombo)) {
					subTitle += "\t "+getPropertyName("item_subgroup")+" : "
							+ itemSubGroupCombo
									.getItemCaption(itemSubGroupCombo
											.getValue());
				}
				if (selected(itemComboField)) {
					subTitle += "\n "+getPropertyName("item")+" : "
							+ itemComboField.getItemCaption(itemComboField
									.getValue());
				}
				
				map.put("REPORT_SUB_TITLE", subTitle);
				//report.setReportSubTitle(subTitle);
				report.setReportType(toInt(reportChoiceField.getValue().toString()));
				report.setIncludeHeader(true);
				report.setIncludeFooter(true);
				report.setOfficeName(officeCombo
						.getItemCaption(officeCombo.getValue()));
				report.createReport(reportList, map);

				reportList.clear();
				itemReportList.clear();
				
				flag=true;

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}

	
	@Override
	public Boolean isValid() {

		boolean valid = true;

		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void reloadSubGroupCombo() {
		try {
			List subGroupList=new ArrayList();
			subGroupList.add(0, new ItemSubGroupModel(0, getPropertyName("all")));
			if (selected(itemGroupCombo)) {
				subGroupList.addAll(itemSubGroupDao.getAllActiveItemSubGroups((Long)itemGroupCombo.getValue()));
			} else {
				subGroupList.addAll(itemSubGroupDao.getAllActiveItemSubGroupsNames(getOrganizationID()));
			}
			SCollectionContainer subGroupContainer = SCollectionContainer.setList(subGroupList, "id");
			itemSubGroupCombo.setContainerDataSource(subGroupContainer);
			itemSubGroupCombo.setItemCaptionPropertyId("name");
			itemSubGroupCombo.setValue((long)0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void reloadItemCombo() {
		try {
			List itemList=new ArrayList();
			itemList.add(0, new ItemModel(0, getPropertyName("all")));
			itemList.addAll(itemDao.getAllActiveItemsWithAppendingItemCode(getValue(officeCombo),
																	getValue(itemSubGroupCombo),
																	getValue(itemGroupCombo)));
			SCollectionContainer itemContainer = SCollectionContainer.setList(itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue((long)0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	
	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
