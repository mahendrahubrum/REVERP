package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.PurchaseRateReportBean;
import com.inventory.reports.dao.PurchaseReportDao;
import com.vaadin.data.Item;
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
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 2, 2014
 */
public class PurchaseRateReportUI extends SparkLogic {

	private static final long serialVersionUID = 4320561595499922115L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private SDateField fromDate, toDate;

	private SReportChoiceField reportType;

	private SOfficeComboField officeSelect;

	private PurchaseReportDao dao;
	private SComboField employSelect;
	private SCollectionContainer c;
	private UserManagementDao userDao;
	
	static String TBC_SN = "SN";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_QUANTITY = "Quantity";
	static String TBC_RATE = "Rate";
	static String TBC_UNIT = "Unit";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {
		setSize(1050, 350);
		allColumns = new Object[] { TBC_SN, TBC_DATE,TBC_ITEM, TBC_QUANTITY,TBC_RATE, TBC_UNIT};
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_ITEM, TBC_QUANTITY,TBC_RATE, TBC_UNIT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_QUANTITY, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
		table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("rate"), null, Align.RIGHT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
		table.setColumnExpandRatio(TBC_ITEM, 2);
		table.setColumnExpandRatio(TBC_QUANTITY, (float) 0.5);
		table.setColumnExpandRatio(TBC_RATE, (float) 0.6);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		dao = new PurchaseReportDao();
		userDao = new UserManagementDao();
		report = new Report(getLoginID());

		officeSelect = new SOfficeComboField(getPropertyName("office"), 200);
		fromDate = new SDateField(getPropertyName("from_date"), 100,
				getDateFormat(), getMonthStartDate());
		toDate = new SDateField(getPropertyName("to_date"), 100,
				getDateFormat(), getWorkingDate());

		employSelect = new SComboField(getPropertyName("employee"), 200);
		loadEmployees((Long) officeSelect.getValue());

		reportType = new SReportChoiceField(getPropertyName("export_to"));

		formLayout = new SFormLayout();
		formLayout.setSpacing(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		formLayout.addComponent(officeSelect);
		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(employSelect);
		formLayout.addComponent(reportType);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);
		mainHorizontal.addComponent(formLayout);
		mainHorizontal.addComponent(table);
		mainHorizontal.addComponent(popupContainer);
		mainHorizontal.setMargin(true);

		officeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				loadEmployees((Long) officeSelect.getValue());
			}
		});

		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase_rate")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("quantity"),item.getItemProperty(TBC_QUANTITY).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("rate"),item.getItemProperty(TBC_RATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("unit"),item.getItemProperty(TBC_UNIT).getValue().toString()));
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
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						List list = dao.getPurchaseRateReport(
								(Long) officeSelect.getValue(),
								(Long) employSelect.getValue(), 
								CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
								CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
						if(list.size()>0){
							Iterator iter = list.iterator();
							PurchaseRateReportBean bean = null;
							PurchaseRateReportBean prevBean = new PurchaseRateReportBean("", 0, "", 0, new Date(), 0);

							while (iter.hasNext()) {
								bean = (PurchaseRateReportBean) iter.next();
								if (prevBean.getItem().equals(bean.getItem()) && prevBean.getUnit().equals(bean.getUnit()) && prevBean.getDate().equals(bean.getDate())) {
									bean.setRepeat(1);
									prevBean.setRepeat(1);
								} 
								else {
									bean.setRepeat(0);
									prevBean = bean;
								}
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getDate().toString(),
										bean.getItem(),
										bean.getQuantity(),
										bean.getRate(),
										bean.getUnit()},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
		
		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					try {

						List list = dao.getPurchaseRateReport(
								(Long) officeSelect.getValue(),
								(Long) employSelect.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDate
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDate
												.getValue()));

						Iterator iter = list.iterator();

						PurchaseRateReportBean bean = null;
						PurchaseRateReportBean prevBean = new PurchaseRateReportBean(
								"", 0, "", 0, new Date(), 0);

						while (iter.hasNext()) {
							bean = (PurchaseRateReportBean) iter.next();
							if (prevBean.getItem().equals(bean.getItem())
									&& prevBean.getUnit()
											.equals(bean.getUnit())
									&& prevBean.getDate()
											.equals(bean.getDate())) {
								bean.setRepeat(1);
								prevBean.setRepeat(1);
							} else {
								bean.setRepeat(0);
								prevBean = bean;
							}
						}

						if (list != null && list.size() > 0) {

							Collections.sort(list,
									new Comparator<PurchaseRateReportBean>() {
										@Override
										public int compare(
												final PurchaseRateReportBean object1,
												final PurchaseRateReportBean object2) {

											int result = object1.getDate()
													.compareTo(
															object2.getDate());
											if (result == 0) {
												result = object1
														.getItem()
														.toLowerCase()
														.compareTo(
																object2.getItem()
																		.toLowerCase());
											}
											return result;
										}

									});
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("PurchaseRateReport");
							report.setReportFileName("PurchaseRateReport");

							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_rate_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							
							
							report.setIncludeHeader(true);
							report.setReportSubTitle(getPropertyName("from")+" : "
									+ CommonUtil.formatDateToDDMMMYYYY(fromDate
											.getValue())
									+ "   "+getPropertyName("to")+"  : "
									+ CommonUtil.formatDateToDDMMMYYYY(toDate
											.getValue()));
							report.setReportType((Integer) reportType
									.getValue());
							report.setOfficeName(officeSelect
									.getItemCaption(officeSelect.getValue()));
							report.createReport(list, map);

							list.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
		mainPanel.setContent(mainHorizontal);

		return mainPanel;
	}

	private void loadEmployees(long officeID) {
		try {
			List lst = new ArrayList();
			lst.add(new UserModel(0, getPropertyName("all")));
			lst.addAll(userDao.getUsersWithLoginId(officeID));

			c = SCollectionContainer.setList(lst, "id");
			employSelect.setContainerDataSource(c);
			employSelect.setItemCaptionPropertyId("first_name");
			employSelect.setValue((long) 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
