package com.inventory.reports.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.ExceptionReportBean;
import com.inventory.reports.dao.SalesExceptionReportDao;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class SalesExceptionReportUI extends SparkLogic implements
		Serializable {

	private Report report;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable subTable;
	private SalesExceptionReportDao exceptionReportDao;

	private static final String TBC_ITEM = "Item";
	private static final String TBC_CUSTOMER = "Customer";

	private static final String TBC_SO_NO = "SO No";
	private static final String TBC_SO_DATE = "SO Date";
	private static final String TBC_SO_QTY = "SO Qty";

	private static final String TBC_DELIVERY_NO = "Deli. Note No";
	private static final String TBC_DELIVERY_DATE = "Deli. Note Date";
	private static final String TBC_DELIVERY_QTY = "Deli. Note Qty";

	private static final String TBC_SALES_NO = "Sales No";
	private static final String TBC_SALES_DATE = "Sales Date";
	private static final String TBC_SALES_QTY = "Sales Qty";

	private static final String TBC_BALANCE = "Balance";

	public static final int SALES_ORDER_AND_DELIVERY_NOTE = 0;
	public static final int SALES_ORDER_AND_SALES = 1;
	public static final int GRN_AND_PURCHASE = 2;
	private OfficeDao officeDao;
	private ItemDao itemDao;
	private SRadioButton reportTypeRadioButton;

	@Override
	public SPanel getGUI() {
		setSize(1350, 400);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		exceptionReportDao = new SalesExceptionReportDao();
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		report = new Report(getLoginID());

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());
		officeComboField.setInputPrompt("---- "+getPropertyName("select")+" -----");

		List<ItemModel> list = getItemList(getOfficeID());
		itemComboField = new SComboField(getPropertyName("item"), 125, list,
				"id", "name", false, getPropertyName("all"));
		itemComboField.setInputPrompt(list.get(0).getName());

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

		reportTypeRadioButton = new SRadioButton(
				getPropertyName("report_type"), 250, Arrays.asList(
						new KeyValue(SALES_ORDER_AND_DELIVERY_NOTE, getPropertyName("so_and_delivery_note")), 
						new KeyValue(SALES_ORDER_AND_SALES,	getPropertyName("so_and_sales"))/*,
						new KeyValue(GRN_AND_PURCHASE,
								getPropertyName("grn_and_purchase"))*/),
				"intKey", "value");
		reportTypeRadioButton.setStyleName("radio_horizontal");
		reportTypeRadioButton.setValue(0);
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
		mainFormLayout.addComponent(reportTypeRadioButton);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		subTable = new STable(null, 950, 200);
		buildTable(toInt(reportTypeRadioButton.getValue().toString()));

		subTable.setColumnExpandRatio(TBC_ITEM, 1.5f);
		subTable.setColumnExpandRatio(TBC_CUSTOMER, 2f);
		
		subTable.setColumnExpandRatio(TBC_SO_NO, 1f);
		subTable.setColumnExpandRatio(TBC_SO_DATE, 1f);
		subTable.setColumnExpandRatio(TBC_SO_QTY, 1f);
		
		subTable.setColumnExpandRatio(TBC_DELIVERY_NO, 1.5f);
		subTable.setColumnExpandRatio(TBC_DELIVERY_DATE, 1.5f);
		subTable.setColumnExpandRatio(TBC_DELIVERY_QTY, 1.5f);
		
		subTable.setColumnExpandRatio(TBC_SALES_NO, 1.5f);
		subTable.setColumnExpandRatio(TBC_SALES_DATE, 2f);
		subTable.setColumnExpandRatio(TBC_SALES_QTY, 2f);
		
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
						.getValue()));
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
		
		reportTypeRadioButton.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				buildTable(toInt(reportTypeRadioButton.getValue().toString()));

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
					List<ExceptionReportBean> list = null;
					try {
						list = generateReport();
					} catch (Exception e) {						
						e.printStackTrace();
					}
					System.out.println("=======UI SIZE====== " + list.size());
					boolean isDataExist = false;
				
					for (ExceptionReportBean bean : list) {
						isDataExist = true;					
						
						subTable.addItem(
								new Object[] {bean.getItem(),
										bean.getLedger(),
										bean.getFirstNo(),
										bean.getFirstDate(),
										bean.getFirstQty()+"",
										bean.getSecondNo(),
										bean.getSecondDate(),
										bean.getSecondQty()+"",
										bean.getBalanceQty()+"" },
								subTable.getItemIds().size() + 1);

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

					List exceptionBeanList = null;
					try {
						exceptionBeanList = generateReport();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println("============= "+list.size());
					boolean isDataExist = false;
					if (exceptionBeanList.size() > 0) {
						isDataExist = true;
					}

			
					if (isDataExist) {
						HashMap<String, Object> parameters = new HashMap<String, Object>();						
						
						if(toInt(reportTypeRadioButton.getValue().toString()) == SALES_ORDER_AND_DELIVERY_NOTE){
							parameters.put("firstNoHeader", getPropertyName("so_no"));
							parameters.put("firstDateHeader", getPropertyName("so_date"));
							parameters.put("firstQtyHeader", getPropertyName("so_qty"));		
							
							parameters.put("secondNoHeader", getPropertyName("delivery_no"));
							parameters.put("secondDateHeader", getPropertyName("delivery_date"));
							parameters.put("secondQtyHeader", getPropertyName("delivery_qty"));
						} else if(toInt(reportTypeRadioButton.getValue().toString()) == SALES_ORDER_AND_SALES){
							parameters.put("firstNoHeader", getPropertyName("so_no"));
							parameters.put("firstDateHeader", getPropertyName("so_date"));
							parameters.put("firstQtyHeader", getPropertyName("so_qty"));		
							
							parameters.put("secondNoHeader", getPropertyName("sales_no"));
							parameters.put("secondDateHeader", getPropertyName("sales_date"));
							parameters.put("secondQtyHeader", getPropertyName("sales_qty"));
						}
						parameters.put("ledger", getPropertyName("customer"));							
						parameters.put("ITEM_LABEL", getPropertyName("item"));
						parameters.put("BALANCE_LABEL", getPropertyName("balance"));
						
						
						report.setJrxmlFileName("ExceptionReport");
						report.setReportFileName("ExceptionReport");
						report.setReportTitle(getPropertyName("sales_exception_report"));
						report.setReportSubTitle(getSubTitle());
						report.setReportType(toInt(reportChoiceField.getValue()
								.toString()));
						report.setIncludeHeader(true);
						report.setOfficeName(officeComboField
								.getItemCaption(officeComboField.getValue()));
						report.createReport(exceptionBeanList, parameters);

						exceptionBeanList.clear();
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
						.append(getPropertyName("item")+" : ")
						.append((itemComboField.getItemCaption(itemComboField
										.getValue()) == null) ? getPropertyName("all") : itemComboField.getItemCaption(itemComboField
												.getValue()))
						.append("\n"+getPropertyName("report_type")+" : "+reportTypeRadioButton
								.getItemCaption(reportTypeRadioButton.getValue()))

						.append("\n"+getPropertyName("from_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDateField
										.getValue()))
						.append(" "+getPropertyName("to_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(toDateField
										.getValue()));
				return titleStringBuffer.toString();
			}
		});

		return panel;
	}
	
	private List<ExceptionReportBean> generateReport() {

		List<ExceptionReportBean> list = null;
		try {
			list = exceptionReportDao.getSalesExceptionReport(toLong(officeComboField.getValue()),
					toLong(itemComboField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
											toInt(reportTypeRadioButton.getValue().toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	
	}

	private void buildTable(int reportType) {
		subTable.removeContainerProperty(TBC_ITEM);
		subTable.removeContainerProperty(TBC_CUSTOMER);

		subTable.removeContainerProperty(TBC_SO_NO);
		subTable.removeContainerProperty(TBC_SO_DATE);
		subTable.removeContainerProperty(TBC_SO_QTY);

		subTable.removeContainerProperty(TBC_DELIVERY_NO);
		subTable.removeContainerProperty(TBC_DELIVERY_DATE);
		subTable.removeContainerProperty(TBC_DELIVERY_QTY);

		subTable.removeContainerProperty(TBC_SALES_NO);
		subTable.removeContainerProperty(TBC_SALES_DATE);
		subTable.removeContainerProperty(TBC_SALES_QTY);

		subTable.removeContainerProperty(TBC_BALANCE);

		switch (reportType) {
		case SALES_ORDER_AND_DELIVERY_NOTE:
			subTable.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);

			subTable.addContainerProperty(TBC_SO_NO, String.class, null,
					getPropertyName("so_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_SO_DATE, String.class, null,
					getPropertyName("so_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_SO_QTY, String.class, null,
					getPropertyName("so_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_DELIVERY_NO, String.class, null,
					getPropertyName("dn_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DELIVERY_DATE, String.class, null,
					getPropertyName("dn_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DELIVERY_QTY, String.class, null,
					getPropertyName("dn_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_BALANCE, String.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			break;
			
		case SALES_ORDER_AND_SALES:
			subTable.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("supplier"), null, Align.LEFT);

			subTable.addContainerProperty(TBC_SO_NO, String.class, null,
					getPropertyName("so_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_SO_DATE, String.class, null,
					getPropertyName("so_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_SO_QTY, String.class, null,
					getPropertyName("so_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_SALES_NO, String.class, null,
					getPropertyName("sales_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_SALES_DATE, String.class, null,
					getPropertyName("sales_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_SALES_QTY, String.class, null,
					getPropertyName("sales_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_BALANCE, String.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			break;			
		

		}

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
		boolean valid = true;
		if (officeComboField.getValue() == null || toInt(officeComboField.getValue().toString()) == 0) {
			setRequiredError(officeComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(officeComboField, null, false);
		}
		/*if (itemComboField.getValue() == null || toInt(itemComboField.getValue().toString()) == 0) {
			setRequiredError(itemComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(itemComboField, null, false);
		}*/
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
