package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.CostCenterBean;
import com.inventory.reports.dao.CostCenterReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TreeTable;
import com.webspark.Components.SButton;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

public class CostCenterReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SOfficeComboField officeSelect;
	// private SFormLayout formLayout;
	// private SPanel mainPanel;
	private SDateField fromDateField;
	private SNativeSelect reportTypeNativeSelect;
	// private SHorizontalLayout buttonHorizontalLayout;
	private SButton generateButton;
	private SButton showButton;
	private TreeTable tableField;
	// private SHorizontalLayout mainHorizontalLayout;
	private Object[] allColoums;
	private Object[] visibleColoums;
	private SDateField toDateField;
	private CostCenterReportDao costCenterReportDao;
	private Report report;
	private static String TBC_ID = "Id";
	private static String TBC_NAME = "Account Name";
	private static String TBC_DEBIT = "Debit";
	private static String TBC_CREDIT = "Credit";
	private static String TBC_BALANCE = "Balance";
	private static String TBC_TYPE_ID = "Type Id";
	private static String TBC_CLASS_OBJECT = "Class Obj";

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		allColoums = new Object[] { TBC_ID, TBC_NAME, TBC_DEBIT, TBC_CREDIT,
				TBC_BALANCE, TBC_TYPE_ID, TBC_CLASS_OBJECT };
		visibleColoums = new Object[] { TBC_NAME, TBC_DEBIT, TBC_CREDIT};

		costCenterReportDao = new CostCenterReportDao();

		setSize(1000, 800);
		SPanel mainPanel = new SPanel();
		mainPanel.setSizeFull();

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.setSpacing(true);
		mainHorizontalLayout.setMargin(true);

		SFormLayout formLayout = new SFormLayout();
		formLayout.setSpacing(true);

		SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		report = new Report(getLoginID());
		
		officeSelect = new SOfficeComboField(getPropertyName("office"), 150);

		fromDateField = new SDateField(getPropertyName("from_date"), 100,
				getDateFormat(), getWorkingDate());
		toDateField = new SDateField(getPropertyName("to_date"), 100,
				getDateFormat(), getWorkingDate());

		reportTypeNativeSelect = new SNativeSelect(
				getPropertyName("report_type"), 100, SConstants.reportTypes,
				"intKey", "value");
		reportTypeNativeSelect.setValue(0);
		// ============================================================================================
		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);
		// ============================================================================================
		tableField = new TreeTable();
		tableField.setWidth("700px");
		tableField.setHeight("500px");
		tableField.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
				Align.CENTER);
		tableField.addContainerProperty(TBC_NAME, String.class, null,
				getPropertyName("division"), null, Align.LEFT);
		tableField.addContainerProperty(TBC_DEBIT, Double.class, null,
				getPropertyName("debit"), null, Align.RIGHT);
		tableField.addContainerProperty(TBC_CREDIT, Double.class, null,
				getPropertyName("credit"), null, Align.RIGHT);
		tableField.addContainerProperty(TBC_BALANCE, Double.class, null,
				getPropertyName("balance"), null, Align.RIGHT);
		tableField.addContainerProperty(TBC_TYPE_ID, Integer.class, null,
				getPropertyName("type_id"), null, Align.RIGHT);
		tableField.addContainerProperty(TBC_CLASS_OBJECT, CostCenterBean.class, null,
				getPropertyName("class_object"), null, Align.RIGHT);

		tableField.setColumnExpandRatio(TBC_NAME, 2f);
		tableField.setColumnExpandRatio(TBC_DEBIT, 1f);
		tableField.setColumnExpandRatio(TBC_CREDIT, 1f);
		tableField.setSelectable(true);
		tableField.setFooterVisible(true);
		tableField.setColumnFooter(TBC_NAME, "Total");
		tableField.setColumnFooter(TBC_DEBIT, "0.0");
		tableField.setColumnFooter(TBC_CREDIT, "0.0");
		tableField.setAnimationsEnabled(true);
		tableField.setVisibleColumns(visibleColoums);

		buttonHorizontalLayout.addComponent(generateButton);
		buttonHorizontalLayout.addComponent(showButton);

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		formLayout.addComponent(officeSelect);
		formLayout.addComponent(dateHorizontalLayout);
		formLayout.addComponent(reportTypeNativeSelect);
		formLayout.addComponent(buttonHorizontalLayout);
		formLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.BOTTOM_CENTER);

		mainHorizontalLayout.addComponent(formLayout);
		mainHorizontalLayout.addComponent(tableField);

		mainPanel.setContent(mainHorizontalLayout);

		tableField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (tableField.getValue() != null) {
					if (tableField.isCollapsed(tableField.getValue()))
						tableField.setCollapsed(tableField.getValue(), false);
					else
						tableField.setCollapsed(tableField.getValue(), true);
				}
			}
		});

		showButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					try {
						tableField.removeAllItems();
						List<?> beanList = costCenterReportDao
								.generateCostCenterReport(getOrganizationID(), toLong(officeSelect
										.getValue().toString()), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));
						System.out.println("===== LIST SIZE =========  "
								+ beanList.size());
						double totalDebit = 0;
						double totalCredit = 0;
						if (beanList.size() > 0) {
							tableField.setVisibleColumns(allColoums);
							Iterator<?> itr = beanList.iterator();
							while (itr.hasNext()) {
								CostCenterBean bean = (CostCenterBean) itr.next();
								bean.setBalance(bean.getDebitAmount() - bean.getCreditAmount());
								tableField.addItem(
										new Object[] { bean.getId(),
												bean.getName(),
												bean.getDebitAmount(),
												bean.getCreditAmount(),
												bean.getBalance(),
												bean.getTypeId(),
												bean}, bean.getId());
								if (bean.getParentId() != 0) {
									tableField.setParent(bean.getId(),bean.getParentId());
								} else {
									totalDebit += bean.getDebitAmount();
									totalCredit += bean.getCreditAmount();
								}
							}
							tableField.setVisibleColumns(visibleColoums);
							tableField.setColumnFooter(TBC_DEBIT, roundNumber(totalDebit)+"");
							tableField.setColumnFooter(TBC_CREDIT, roundNumber(totalCredit)+"");
						} else {
							SNotification.show(getPropertyName("no_data_available"), Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		
		generateButton.addClickListener(new ClickListener() {
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					showButton.click();
					if(tableField.getItemIds().size() > 0){
						List reportList = new ArrayList();
						Iterator itr = tableField.getItemIds().iterator();
						CostCenterBean bean = null;
						while(itr.hasNext()){
						//	bean = new CostCenterBean();
							Item item = tableField.getItem(itr.next());
							bean = (CostCenterBean) item.getItemProperty(TBC_CLASS_OBJECT).getValue();
							reportList.add(bean);							
						}
						
						HashMap<String, Object> params = new HashMap<String, Object>();
//						params.put("ToDate", ((Date) toDate.getValue()).toString());
						params.put("ACCOUNT_NAME_LABEL", getPropertyName("account_name"));
						params.put("DEBIT_LABEL", getPropertyName("debit"));
						params.put("CREDIT_LABEL", getPropertyName("credit"));
						params.put("BALANCE_LABEL", getPropertyName("balance"));
						
						report.setJrxmlFileName("CostCenterReport");

						report.setReportFileName("CostCenterReport");

						report.setReportTitle(getPropertyName("cost_center_report")+" - "
								+ officeSelect.getItemCaption(officeSelect
										.getValue()));

						report.setIncludeHeader(true);
						report.setReportSubTitle(getPropertyName("from")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDateField.getValue())+ " "
								+getPropertyName("to")+" : "+CommonUtil.formatDateToDDMMYYYY(toDateField.getValue()));
						report.setReportType((Integer) reportTypeNativeSelect.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);
						
						reportList.clear();						
					}					
				}
			}
		});

		return mainPanel;
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
