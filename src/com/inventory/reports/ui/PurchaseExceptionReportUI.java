package com.inventory.reports.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.ExceptionReportBean;
import com.inventory.reports.dao.PurchaseExceptionReportDao;
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

public class PurchaseExceptionReportUI extends SparkLogic implements
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
	private PurchaseExceptionReportDao exceptionReportDao;

	private static final String TBC_ITEM = "Item";
	private static final String TBC_SUPPLIER = "Supplier";

	private static final String TBC_PO_NO = "PO No";
	private static final String TBC_PO_DATE = "PO Date";
	private static final String TBC_PO_QTY = "PO Qty";

	private static final String TBC_GRN_NO = "GRN No";
	private static final String TBC_GRN_DATE = "GRN Date";
	private static final String TBC_GRN_QTY = "GRN Qty";

	private static final String TBC_PURCHASE_NO = "Purch. No";
	private static final String TBC_PURCHASE_DATE = "Purch. Date";
	private static final String TBC_PURCHASE_QTY = "Purch. Qty";

	private static final String TBC_BALANCE = "Balance";

	public static final int PO_AND_GRN = 0;
	public static final int PO_AND_PURCHASE = 1;
	public static final int GRN_AND_PURCHASE = 2;
	private OfficeDao officeDao;
	private ItemDao itemDao;
	private SRadioButton reportTypeRadioButton;

	@Override
	public SPanel getGUI() {
		setSize(1350, 400);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		exceptionReportDao = new PurchaseExceptionReportDao();
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		report = new Report(getLoginID());

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("Select"));
		officeComboField.setValue(getOfficeID());
		officeComboField.setInputPrompt("---- Select -----");

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
						new KeyValue(PO_AND_GRN, "PO & GRN"), new KeyValue(
								PO_AND_PURCHASE,
								getPropertyName("po_and_purchase"))/*,
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
		subTable.setColumnExpandRatio(TBC_SUPPLIER, 2f);
		
		subTable.setColumnExpandRatio(TBC_PO_NO, 1f);
		subTable.setColumnExpandRatio(TBC_PO_DATE, 2f);
		subTable.setColumnExpandRatio(TBC_PO_QTY, 1f);
		
		subTable.setColumnExpandRatio(TBC_GRN_NO, 1f);
		subTable.setColumnExpandRatio(TBC_GRN_DATE, 1f);
		subTable.setColumnExpandRatio(TBC_GRN_QTY, 1f);
		
		subTable.setColumnExpandRatio(TBC_PURCHASE_NO, 1.5f);
		subTable.setColumnExpandRatio(TBC_PURCHASE_DATE, 2f);
		subTable.setColumnExpandRatio(TBC_PURCHASE_QTY, 2f);
		
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
						
						if(toInt(reportTypeRadioButton.getValue().toString()) == PO_AND_GRN){
											
							parameters.put("firstNoHeader", getPropertyName("po_no"));
							parameters.put("firstDateHeader", getPropertyName("po_date"));
							parameters.put("firstQtyHeader", getPropertyName("po_qty"));		
							
							parameters.put("secondNoHeader", getPropertyName("grn_no"));
							parameters.put("secondDateHeader", getPropertyName("grn_date"));
							parameters.put("secondQtyHeader", getPropertyName("grn_qty"));
							
						} else if(toInt(reportTypeRadioButton.getValue().toString()) == PO_AND_PURCHASE){
							parameters.put("firstNoHeader", getPropertyName("po_no"));
							parameters.put("firstDateHeader", getPropertyName("po_date"));
							parameters.put("firstQtyHeader", getPropertyName("po_qty"));		
							
							parameters.put("secondNoHeader", getPropertyName("purchase_no"));
							parameters.put("secondDateHeader", getPropertyName("purchase_date"));
							parameters.put("secondQtyHeader", getPropertyName("purchase_qty"));
						
							
							
						}
						parameters.put("ledger", getPropertyName("supplier"));				
						parameters.put("ITEM_LABEL", getPropertyName("item"));
						parameters.put("BALANCE_LABEL", getPropertyName("balance"));
						
						report.setJrxmlFileName("ExceptionReport");
						report.setReportFileName("ExceptionReport");
						report.setReportTitle(getPropertyName("purchase_exception_report"));
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
			list = exceptionReportDao.getPurchaseExceptionReport(toLong(officeComboField.getValue()),
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
		subTable.removeContainerProperty(TBC_SUPPLIER);

		subTable.removeContainerProperty(TBC_PO_NO);
		subTable.removeContainerProperty(TBC_PO_DATE);
		subTable.removeContainerProperty(TBC_PO_QTY);

		subTable.removeContainerProperty(TBC_GRN_NO);
		subTable.removeContainerProperty(TBC_GRN_DATE);
		subTable.removeContainerProperty(TBC_GRN_QTY);

		subTable.removeContainerProperty(TBC_PURCHASE_NO);
		subTable.removeContainerProperty(TBC_PURCHASE_DATE);
		subTable.removeContainerProperty(TBC_PURCHASE_QTY);

		subTable.removeContainerProperty(TBC_BALANCE);

		switch (reportType) {
		case PO_AND_GRN:
			subTable.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_SUPPLIER, String.class, null,
					getPropertyName("supplier"), null, Align.LEFT);

			subTable.addContainerProperty(TBC_PO_NO, String.class, null,
					getPropertyName("po_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PO_DATE, String.class, null,
					getPropertyName("po_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PO_QTY, String.class, null,
					getPropertyName("po_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_GRN_NO, String.class, null,
					getPropertyName("grn_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_GRN_DATE, String.class, null,
					getPropertyName("grn_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_GRN_QTY, String.class, null,
					getPropertyName("grn_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_BALANCE, String.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			break;
			
		case PO_AND_PURCHASE:
			subTable.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_SUPPLIER, String.class, null,
					getPropertyName("supplier"), null, Align.LEFT);

			subTable.addContainerProperty(TBC_PO_NO, String.class, null,
					getPropertyName("po_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PO_DATE, String.class, null,
					getPropertyName("po_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PO_QTY, String.class, null,
					getPropertyName("po_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_PURCHASE_NO, String.class, null,
					getPropertyName("purchase_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PURCHASE_DATE, String.class, null,
					getPropertyName("purchase_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PURCHASE_QTY, String.class, null,
					getPropertyName("purchase_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_BALANCE, String.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			break;
			
		case GRN_AND_PURCHASE:
			subTable.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_SUPPLIER, String.class, null,
					getPropertyName("supplier"), null, Align.LEFT);

			subTable.addContainerProperty(TBC_GRN_NO, String.class, null,
					getPropertyName("grn_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_GRN_DATE, String.class, null,
					getPropertyName("grn_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_GRN_QTY, String.class, null,
					getPropertyName("grn_qty"), null, Align.RIGHT);
			
			subTable.addContainerProperty(TBC_PURCHASE_NO, String.class, null,
					getPropertyName("purchase_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PURCHASE_DATE, String.class, null,
					getPropertyName("purchase_date"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_PURCHASE_QTY, String.class, null,
					getPropertyName("purchase_qty"), null, Align.RIGHT);

			subTable.addContainerProperty(TBC_BALANCE, String.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			break;

		}

	}



	private List getOfficeList() {
		List<S_OfficeModel> list = new ArrayList<S_OfficeModel>();
		list.add(new S_OfficeModel(0, "---- Select -----"));
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
		list.add(new ItemModel(0, "---- All -----"));
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
