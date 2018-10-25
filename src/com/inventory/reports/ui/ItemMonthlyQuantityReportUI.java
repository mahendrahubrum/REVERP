package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.reports.dao.ItemMonthlyQuantityReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 4, 2014
 */

public class ItemMonthlyQuantityReportUI extends SparkLogic {

//	private static final String PROMPT_ALL = "-------------------All-----------------";

	private static final long serialVersionUID = -5835327703018639924L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SComboField itemGroupComboField;
	private SComboField itemSubGroupComboField;
	private SComboField itemComboField;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private List itemList;
	private List subGroupList;

	private ItemSubGroupDao itemSubGroupDao;
	private ItemMonthlyQuantityReportDao dao;
	private ItemDao itemDao;
	CommonMethodsDao comDao;
	SDateField fromDate;
	SDateField toDate;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;
	private SReportChoiceField reportChoiceField;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM = "Item";
	static String TBC_PURCHASE = "Purchase Qty";
	static String TBC_PURCHASE_RETURN = "Purchase Return Qty";
	static String TBC_SALE = "Sale Qty";
	static String TBC_SALE_RETURN = "Sale Return Qty";
	static String TBC_UNIT = "Unit";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_PURCHASE,TBC_PURCHASE_RETURN, TBC_SALE,TBC_SALE_RETURN,TBC_UNIT};
		visibleColumns = new Object[]{ TBC_SN,TBC_ITEM, TBC_PURCHASE,TBC_PURCHASE_RETURN, TBC_SALE,TBC_SALE_RETURN,TBC_UNIT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);
		table.addContainerProperty(TBC_PURCHASE, Double.class, null,getPropertyName("purchase_qty"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE_RETURN, Double.class, null,getPropertyName("purchase_return_qty"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALE, Double.class, null,getPropertyName("sales_qty"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALE_RETURN, Double.class, null,getPropertyName("sales_return_qty"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, (float) 2);
		table.setColumnExpandRatio(TBC_PURCHASE, (float) 1.5);
		table.setColumnExpandRatio(TBC_PURCHASE_RETURN, (float) 1.5);
		table.setColumnExpandRatio(TBC_SALE, (float) 1.5);
		table.setColumnExpandRatio(TBC_SALE_RETURN, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);

		setSize(1100, 350);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		 formLayout.setSpacing(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		itemSubGroupDao = new ItemSubGroupDao();
		itemDao = new ItemDao();
		comDao = new CommonMethodsDao();
		dao = new ItemMonthlyQuantityReportDao();

		SHorizontalLayout dateLay = new SHorizontalLayout();
		fromDate = new SDateField(getPropertyName("from_date"), 100,
				getDateFormat(), getMonthStartDate());
		toDate = new SDateField(getPropertyName("to_date"), 100,
				getDateFormat(), getWorkingDate());

		dateLay.addComponent(fromDate);
		dateLay.addComponent(toDate);
		dateLay.setSpacing(true);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			itemGroupComboField = new SComboField(
					getPropertyName("item_group"), 200);
			itemGroupComboField.setInputPrompt(getPropertyName("all"));

			try {
				subGroupList = new ArrayList();
			} catch (Exception e) {
				subGroupList = new ArrayList();
				e.printStackTrace();
			}
			ItemSubGroupModel itemSubGroupModel = new ItemSubGroupModel();
			itemSubGroupModel.setId(0);
			itemSubGroupModel.setName(getPropertyName("all"));
			subGroupList.add(0, itemSubGroupModel);
			itemSubGroupComboField = new SComboField(
					getPropertyName("item_sub_group"), 200, null, "id", "name");
			itemSubGroupComboField.setInputPrompt(getPropertyName("all"));

			itemList = new ArrayList();

			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName(getPropertyName("all"));
			itemList.add(0, itemModel);
			itemComboField = new SComboField(getPropertyName("item"), 200,
					itemList, "id", "name");
			itemComboField.setInputPrompt(getPropertyName("all"));
			reloadItemCombo();

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));

			formLayout.addComponent(organizationComboField);
			formLayout.addComponent(officeComboField);
			formLayout.addComponent(itemGroupComboField);
			formLayout.addComponent(itemSubGroupComboField);
			formLayout.addComponent(itemComboField);
			formLayout.addComponent(dateLay);

			formLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);
			mainHorizontal.addComponent(formLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		organizationComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {

						try {

							SCollectionContainer bic = SCollectionContainer.setList(
									new OfficeDao()
											.getAllOfficeNamesUnderOrg((Long) organizationComboField
													.getValue()), "id");
							officeComboField.setContainerDataSource(bic);
							officeComboField.setItemCaptionPropertyId("name");

							Iterator it = officeComboField.getItemIds()
									.iterator();
							if (it.hasNext())
								officeComboField.setValue(it.next());

							reloadGroupCombo();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		officeComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							reloadSubGroupCombo();
							reloadItemCombo();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		if (isSystemAdmin() || isSuperAdmin()) {
			organizationComboField.setEnabled(true);
			officeComboField.setEnabled(true);
		} else {
			organizationComboField.setEnabled(false);
			if (isOrganizationAdmin()) {
				officeComboField.setEnabled(true);
			} else {
				officeComboField.setEnabled(false);
			}
		}

		organizationComboField.setValue(getOrganizationID());
		officeComboField.setValue(getOfficeID());

		itemGroupComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				reloadSubGroupCombo();
				reloadItemCombo();

			}
		});

		itemSubGroupComboField
				.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChange(ValueChangeEvent event) {
						reloadItemCombo();
					}
		});

		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_report")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("unit"),item.getItemProperty(TBC_UNIT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("purchase_qty"),item.getItemProperty(TBC_PURCHASE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("purchase_return_qty"),item.getItemProperty(TBC_PURCHASE_RETURN).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("sales_qty"),item.getItemProperty(TBC_SALE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("sales_return_qty"),item.getItemProperty(TBC_SALE_RETURN).getValue().toString()));
						popupContainer.removeAllComponents();
						form.setStyleName("grid_max_limit");
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

		mainPanel.setContent(mainHorizontal);
		return mainPanel;
	}

	protected void showReport() {
		try {
			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			double balance = 0;
			if (selected(itemGroupComboField)) {
				groupId = toLong(itemGroupComboField.getValue().toString());
			}

			if (selected(itemSubGroupComboField)) {
				subgroupId = toLong(itemSubGroupComboField.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}

			List itemReportList = dao.getItemMonthlyQuantityReport(
					toLong(officeComboField.getValue().toString()), itemId,
					subgroupId, groupId,
					CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
			
			if(itemReportList.size()>0){
				ReportBean bean=null;
				Iterator itr=itemReportList.iterator();
				while(itr.hasNext()){
					bean=(ReportBean)itr.next();
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							(long)0,
							bean.getItem_name(),
							bean.getPurchaseQty(),
							bean.getPurchaseRtnQty(),
							bean.getSalesQty(),
							bean.getSalesRtnQty(),
							bean.getUnit()},table.getItemIds().size()+1);
				}
			}
			 else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			table.setVisibleColumns(visibleColumns);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	protected void generateReport() {
		try {

			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			double balance = 0;
			if (selected(itemGroupComboField)) {
				groupId = toLong(itemGroupComboField.getValue().toString());
			}

			if (selected(itemSubGroupComboField)) {
				subgroupId = toLong(itemSubGroupComboField.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}

			List itemReportList = dao.getItemMonthlyQuantityReport(
					toLong(officeComboField.getValue().toString()), itemId,
					subgroupId, groupId,
					CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));

			if (itemReportList != null && itemReportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				report.setJrxmlFileName("ItemMonthlyQuantityReport");
				report.setReportFileName("ItemMonthlyQuantityReport");
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("item_monthly_quantity_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("PURCHASE_QUANTITY_LABEL", getPropertyName("purchase_quantity"));
				map.put("PURCHASE_RETURN_QUANTITY_LABEL", getPropertyName("purchase_return_quantity"));
				map.put("SALES_QUANTITY_LABEL", getPropertyName("sales_quantity"));
				map.put("SALES_RETURN_QUANTITY_LABEL", getPropertyName("sales_return_quantity"));
				map.put("UNIT_LABEL", getPropertyName("unit"));
				
				
				String subTitle = "";
				if (selected(itemGroupComboField)) {
					subTitle += getPropertyName("item_group")+" : "
							+ itemGroupComboField
									.getItemCaption(itemGroupComboField
											.getValue());
				}
				if (selected(itemSubGroupComboField)) {
					subTitle += "\t "+getPropertyName("item_sub_group")+" : "
							+ itemSubGroupComboField
									.getItemCaption(itemSubGroupComboField
											.getValue());
				}
				if (selected(itemComboField)) {
					subTitle += "\n "+getPropertyName("item")+" : "
							+ itemComboField.getItemCaption(itemComboField
									.getValue());
				}
				report.setReportSubTitle(subTitle);
				report.setReportType(toInt(reportChoiceField.getValue()
						.toString()));
				report.setIncludeHeader(true);
				report.setIncludeFooter(false);
				report.setOfficeName(officeComboField
						.getItemCaption(officeComboField.getValue()));
				report.createReport(itemReportList, map);

				itemReportList.clear();

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private void reloadSubGroupCombo() {
		try {

			if (selected(itemGroupComboField)) {
				subGroupList = itemSubGroupDao.getAllActiveItemSubGroups(Long
						.parseLong(itemGroupComboField.getValue().toString()));
			} else {
				subGroupList = itemSubGroupDao
						.getAllActiveItemSubGroupsNames(getOrganizationID());
			}

			ItemSubGroupModel itemSubGroupModel = new ItemSubGroupModel();
			itemSubGroupModel.setId(0);
			itemSubGroupModel.setName(getPropertyName("all"));
			if (subGroupList == null)
				subGroupList = new ArrayList();

			subGroupList.add(0, itemSubGroupModel);

			itemSubGroupComboField.setInputPrompt(getPropertyName("all"));

			subGroupContainer = SCollectionContainer
					.setList(subGroupList, "id");
			itemSubGroupComboField.setContainerDataSource(subGroupContainer);
			itemSubGroupComboField.setItemCaptionPropertyId("name");
			itemSubGroupComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reloadItemCombo() {
		try {

			itemList = itemDao.getAllActiveItemsWithAppendingItemCode(
					getValue(officeComboField),
					getValue(itemSubGroupComboField),
					getValue(itemGroupComboField));

			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName(getPropertyName("all"));
			if (itemList == null)
				itemList = new ArrayList();

			itemList.add(0, itemModel);

			itemComboField.setInputPrompt(getPropertyName("all"));

			itemContainer = SCollectionContainer.setList(itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reloadGroupCombo() {
		try {

			List groupList;
			try {
				groupList = new ItemGroupDao()
						.getAllActiveItemGroupsNames(getOrganizationID());
			} catch (Exception e) {
				groupList = new ArrayList();
				e.printStackTrace();
			}

			ItemGroupModel itemGroupModel = new ItemGroupModel();
			itemGroupModel.setId(0);
			itemGroupModel.setName(getPropertyName("all"));
			groupList.add(0, itemGroupModel);

			itemContainer = SCollectionContainer.setList(groupList, "id");
			itemGroupComboField.setContainerDataSource(itemContainer);
			itemGroupComboField.setItemCaptionPropertyId("name");
			itemGroupComboField.setValue(0);

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
