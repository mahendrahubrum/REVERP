package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.inventory.reports.dao.LossSalesDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.sales.ui.SalesOrderNewUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
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
 *         Apr 7, 2014
 */

public class LossSaleReportUI extends SparkLogic {

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
	private LossSalesDao dao;
	private ItemDao itemDao;
	CommonMethodsDao comDao;
	SDateField fromDate;
	SDateField toDate;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;
	private SReportChoiceField reportChoiceField;
	
	static String TBC_SN = "SN";
	static String TBC_SID = "SID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_BILL = "Bill";
	static String TBC_SQTY = "Sale Qty";
	static String TBC_SRATE = "Sale Rate";
	static String TBC_PRATE = "Purchase Rate";
	static String TBC_PAMOUNT = "Purchase Amount";
	static String TBC_SAMOUNT = "Sale Amount";
	static String TBC_LOSS = "Loss";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_SID,TBC_ITEM,TBC_DATE,TBC_BILL,TBC_SQTY, TBC_SRATE,TBC_PRATE,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		visibleColumns = new Object[]  { TBC_SN,TBC_ITEM,TBC_DATE,TBC_BILL,TBC_SQTY, TBC_SRATE,TBC_PRATE,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 875, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_BILL, Long.class, null,getPropertyName("bill"), null, Align.LEFT);
		table.addContainerProperty(TBC_SQTY, Double.class, null,getPropertyName("sale_qty"), null, Align.LEFT);
		table.addContainerProperty(TBC_SRATE, Double.class, null,getPropertyName("sale_rate"), null, Align.LEFT);
		table.addContainerProperty(TBC_PRATE, Double.class, null,getPropertyName("purchase_rate"), null, Align.LEFT);
		table.addContainerProperty(TBC_SAMOUNT, Double.class, null,getPropertyName("sale_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_PAMOUNT, Double.class, null,getPropertyName("purchase_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_LOSS, Double.class, null,getPropertyName("loss"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
		table.setColumnExpandRatio(TBC_ITEM, (float) 1.5);
		table.setColumnExpandRatio(TBC_SQTY, (float) 1.5);
		table.setColumnExpandRatio(TBC_SRATE, (float) 1.5);
		table.setColumnExpandRatio(TBC_PRATE, (float) 1.5);
		table.setColumnExpandRatio(TBC_SAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_LOSS, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		setSize(1300, 350);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		 formLayout.setSpacing(true);
//		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		itemSubGroupDao = new ItemSubGroupDao();
		itemDao = new ItemDao();
		comDao = new CommonMethodsDao();
		dao = new LossSalesDao();

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
			// formLayout.addComponent(itemGroupComboField);
			// formLayout.addComponent(itemSubGroupComboField);
			formLayout.addComponent(itemComboField);
			formLayout.addComponent(dateLay);

			formLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);
			mainHorizontal.addComponent(formLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
		} 
		catch (Exception e) {
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

		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				showButton.click();
			}
		};
		
		final Action actionSales = new Action("Edit Sales");
		
		table.addActionHandler(new Handler() {
			
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				try{
					Item item = null;
					if (table.getValue() != null) {
						item = table.getItem(table.getValue());
						SalesNewUI option=new SalesNewUI();
						option.setCaption("Sales");
						option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_SID).getValue());
						option.center();
						getUI().getCurrent().addWindow(option);
						option.addCloseListener(closeListener);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			@Override
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { actionSales };
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						long id = (Long) item.getItemProperty(TBC_SID).getValue();
						SalesModel mdl=new SalesDao().getSale(id);
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getSales_number()+""));
						form.addComponent(new SLabel(getPropertyName("customer"),mdl.getCustomer().getName()));
						form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
						form.addComponent(new SLabel(getPropertyName("sale_qty"),item.getItemProperty(TBC_SQTY).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("sale_rate"),item.getItemProperty(TBC_SRATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("purchase_rate"),item.getItemProperty(TBC_PRATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("sale_amount"),item.getItemProperty(TBC_SAMOUNT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("purchase_amount"),item.getItemProperty(TBC_PAMOUNT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("loss"),item.getItemProperty(TBC_LOSS).getValue().toString()));
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

		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
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

		mainPanel.setContent(mainHorizontal);

		return mainPanel;
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

			List itemReportList = dao.getLossSalesReport(
					toLong(officeComboField.getValue().toString()), itemId,
					CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));

			if (itemReportList != null && itemReportList.size() > 0) {

				Collections.sort(itemReportList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object2.getId().compareTo(object1.getId());
						if (result == 0) {
							result = object2.getDate().compareTo(
									object1.getDate());
						}
						return result;
					}

				});
				HashMap<String, Object> map = new HashMap<String, Object>();
				report.setJrxmlFileName("LossSalesReport");
				report.setReportFileName("LossSalesReport");
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("loss_sales_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
				map.put("DATE_LABEL", getPropertyName("date"));
				map.put("SALE_QUANTITY_LABEL", getPropertyName("sale_quantity"));
				map.put("SALE_UNIT_PRICE_LABEL", getPropertyName("sale_unit_price"));
				map.put("PURCHASE_UNIT_PRICE_LABEL", getPropertyName("purchase_unit_price"));
				map.put("SALE_AMOUNT", getPropertyName("sale_amount"));
				map.put("PURCHASE_AMOUNT", getPropertyName("purchase_amount"));
				map.put("LOSS_LABEL", getPropertyName("loss"));
				map.put("TOTAL_LABEL", getPropertyName("total"));
				
				String subTitle = "";
				if (selected(itemComboField)) {
					subTitle += getPropertyName("item")+" : "
							+ itemComboField.getItemCaption(itemComboField
									.getValue());
				}
				subTitle += "\n "+getPropertyName("from")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(fromDate.getValue())
						+ "\t "+getPropertyName("to")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(toDate.getValue());
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
	
	@SuppressWarnings("unchecked")
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

			List itemReportList = dao.getLossSalesReport(
					toLong(officeComboField.getValue().toString()), itemId,
					CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
			if(itemReportList.size()>0){
				ReportBean bean=null;
				Iterator itr=itemReportList.iterator();
				while(itr.hasNext()){
					bean=(ReportBean)itr.next();
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							bean.getNumber(),
							bean.getItem_name(),
							bean.getDate().toString(),
							bean.getId(),
							bean.getQuantity(),
							bean.getRate(),
							bean.getAmount(),
							bean.getInwards(),
							bean.getOutwards(),
							bean.getProfit()},table.getItemIds().size()+1);
					
				}
			}
			else{
				SNotification.show("No Data Available",Type.WARNING_MESSAGE);
			}
			table.setVisibleColumns(visibleColumns);
			

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
