package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.BalancesReportDao;
import com.inventory.reports.dao.TrialBalanceDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

/**
 * 
 * @author anil
 * @date 08-Oct-2015
 * @Project REVERP
 */
public class BalancesReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton,showButton;

	private Report report;

	private BalancesReportDao daoObj;

	private SNativeSelect reportType;

	SDateField fromDate, toDate;

	SOfficeComboField officeSelect;
	
	STable table,subtable;
	SNativeButton closeBtn;
	SHorizontalLayout mainHorizontalLayout,popHor,subHor;
	SPopupView pop,subPop;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_NAME = "Account Name";
	static String TBC_DEBIT = "Debit";
	static String TBC_CREDIT = "Credit";
	static String TBC_BALANCE = "Balance";
	static String TBC_OPENING_DEBIT = "Opening Debit";
	static String TBC_OPENING_CREDIT = "Opening Credit";
	static String TBC_CURRENT_DEBIT = "Current Debit";
	static String TBC_CURRENT_CREDIT = "Current Credit";
	
	Object[] allColoums;
	Object[] visibleColoums;
	
	Object[] allSubColoums;
	Object[] visibleSubColoums;
	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		allColoums=new Object[]{TBC_SN, TBC_ID, TBC_NAME, TBC_DEBIT, TBC_CREDIT, TBC_BALANCE};
		visibleColoums=new Object[]{TBC_SN, TBC_NAME, TBC_DEBIT, TBC_CREDIT, TBC_BALANCE};
		
		allSubColoums=new Object[]{TBC_SN, TBC_ID, TBC_NAME, TBC_OPENING_DEBIT, TBC_OPENING_CREDIT, TBC_CURRENT_DEBIT, TBC_CURRENT_CREDIT, TBC_DEBIT, TBC_CREDIT};
		visibleSubColoums=new Object[]{TBC_SN, TBC_NAME, TBC_OPENING_DEBIT, TBC_OPENING_CREDIT, TBC_CURRENT_DEBIT, TBC_CURRENT_CREDIT, TBC_DEBIT, TBC_CREDIT};
		setSize(1000, 300);
		table=new STable(null, 700, 200);
		subtable=new STable(null, 900, 250);
		mainHorizontalLayout=new SHorizontalLayout();
		popHor=new SHorizontalLayout();
		subHor=new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		reportType = new SNativeSelect(getPropertyName("report_type"), 100,
				SConstants.reportTypes, "intKey", "value");

		officeSelect = new SOfficeComboField(getPropertyName("office"), 150);

		report = new Report(getLoginID());

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		 formLayout.setSpacing(true);
		 mainHorizontalLayout.setSpacing(true);
		 mainHorizontalLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		daoObj = new BalancesReportDao();
		
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null, Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		table.addContainerProperty(TBC_NAME, String.class, null, getPropertyName("account_name"), null, Align.LEFT);
		table.addContainerProperty(TBC_DEBIT, Double.class, null, getPropertyName("debit"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CREDIT, Double.class, null, getPropertyName("credit"), null, Align.RIGHT);
		table.addContainerProperty(TBC_BALANCE, Double.class, null, getPropertyName("balance"), null, Align.RIGHT);
		
		subtable.addContainerProperty(TBC_SN, Integer.class, null, "#", null, Align.CENTER);
		subtable.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		subtable.addContainerProperty(TBC_NAME, String.class, null, getPropertyName("account_name"), null, Align.LEFT);
		subtable.addContainerProperty(TBC_OPENING_DEBIT, Double.class, null, getPropertyName("opening_debit"), null, Align.RIGHT);
		subtable.addContainerProperty(TBC_OPENING_CREDIT, Double.class, null, getPropertyName("opening_credit"), null, Align.RIGHT);
		subtable.addContainerProperty(TBC_CURRENT_DEBIT, Double.class, null, getPropertyName("current_debit"), null, Align.RIGHT);
		subtable.addContainerProperty(TBC_CURRENT_CREDIT, Double.class, null, getPropertyName("current_credit"), null, Align.RIGHT);
		subtable.addContainerProperty(TBC_DEBIT, Double.class, null, getPropertyName("debit"), null, Align.RIGHT);
		subtable.addContainerProperty(TBC_CREDIT, Double.class, null, getPropertyName("credit"), null, Align.RIGHT);
		
		table.setColumnExpandRatio(TBC_NAME, 2f);
		table.setColumnExpandRatio(TBC_DEBIT, 1f);
		table.setColumnExpandRatio(TBC_CREDIT, 1f);
		table.setSelectable(true);
		
		subtable.setColumnExpandRatio(TBC_NAME, 2f);
		subtable.setColumnExpandRatio(TBC_OPENING_DEBIT, 1f);
		subtable.setColumnExpandRatio(TBC_OPENING_CREDIT, 1f);
		subtable.setColumnExpandRatio(TBC_CURRENT_DEBIT, 1f);
		subtable.setColumnExpandRatio(TBC_CURRENT_CREDIT, 1f);
		subtable.setColumnExpandRatio(TBC_DEBIT, 1f);
		subtable.setColumnExpandRatio(TBC_CREDIT, 1f);
		subtable.setSelectable(true);
		
		subtable.setFooterVisible(true);
		subtable.setColumnFooter(TBC_OPENING_DEBIT, "0.0");
		subtable.setColumnFooter(TBC_OPENING_CREDIT, "0.0");
		subtable.setColumnFooter(TBC_CURRENT_DEBIT, "0.0");
		subtable.setColumnFooter(TBC_CURRENT_CREDIT, "0.0");
		subtable.setColumnFooter(TBC_DEBIT, "0.0");
		subtable.setColumnFooter(TBC_CREDIT, "0.0");
		
		table.setVisibleColumns(visibleColoums);
		subtable.setVisibleColumns(visibleSubColoums);
		fromDate = new SDateField(getPropertyName("from_date"), 150,
				getDateFormat(), getFinStartDate());
		toDate = new SDateField(getPropertyName("to_date"), 150,
				getDateFormat(), getFinEndDate());

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(officeSelect);
		formLayout.addComponent(reportType);
		reportType.setValue(0);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);
		mainHorizontalLayout.addComponent(formLayout);
		mainHorizontalLayout.addComponent(popHor);
		mainHorizontalLayout.addComponent(subHor);
		mainHorizontalLayout.addComponent(table);
		

		
		
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

		
		closeBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				pop.setPopupVisible(false);
			}
		});
		
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if(table.getValue()!=null) {
						subtable.removeAllItems();
						subtable.setColumnFooter(TBC_OPENING_DEBIT, "0.0");
						subtable.setColumnFooter(TBC_OPENING_CREDIT, "0.0");
						subtable.setColumnFooter(TBC_CURRENT_DEBIT, "0.0");
						subtable.setColumnFooter(TBC_CURRENT_CREDIT, "0.0");
						subtable.setColumnFooter(TBC_DEBIT, "0.0");
						subtable.setColumnFooter(TBC_CREDIT, "0.0");
						Item item=table.getItem(table.getValue());
						long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
						subtable.setVisibleColumns(allSubColoums);
						
						List detailList=daoObj.showBalancesReportForGroup(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
																		CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
																		(Long) officeSelect.getValue(), getOrganizationID(),id);
						double od=0,oc=0,cd=0,cc=0,bd=0,bc=0;
						if(detailList.size()>0){
							Iterator itr=detailList.iterator();
							while (itr.hasNext()) {
								AcctReportMainBean bean=(AcctReportMainBean)itr.next();
								subtable.addItem(new Object[]{
										subtable.getItemIds().size()+1,
										bean.getId(),
										bean.getName(),
										roundNumber(bean.getOpening_debit()),
										roundNumber(bean.getOpening_credit()),
										roundNumber(bean.getCurrent_debit()),
										roundNumber(bean.getCurrent_credit()),
										roundNumber(bean.getBalance_debit()),
										roundNumber(bean.getBalance_credit())},subtable.getItemIds().size()+1);
								od+=roundNumber(bean.getOpening_debit());
								oc+=roundNumber(bean.getOpening_credit());
								cd+=roundNumber(bean.getCurrent_debit());
								cc+=roundNumber(bean.getCurrent_credit());
								bd+=roundNumber(bean.getBalance_debit());
								bc+=roundNumber(bean.getBalance_credit());
							}
						}
						else {
							SNotification.show(getPropertyName("no_data_available"),Type.TRAY_NOTIFICATION);
						}
						subtable.setVisibleColumns(visibleSubColoums);
						subtable.setColumnFooter(TBC_OPENING_DEBIT, roundNumberToString(od));
						subtable.setColumnFooter(TBC_OPENING_CREDIT, roundNumberToString(oc));
						subtable.setColumnFooter(TBC_CURRENT_DEBIT, roundNumberToString(cd));
						subtable.setColumnFooter(TBC_CURRENT_CREDIT, roundNumberToString(cc));
						subtable.setColumnFooter(TBC_DEBIT, roundNumberToString(bd));
						subtable.setColumnFooter(TBC_CREDIT, roundNumberToString(bc));
						pop = new SPopupView(
								"",
								new SVerticalLayout(
										true,
										new SHorizontalLayout(
												new SHTMLLabel(
														null,
														"<h2><u style='margin-left: 40px;'>"+table.getItem(table.getValue()).getItemProperty(TBC_NAME).getValue().toString(),
														875), closeBtn), subtable));

						popHor.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		
		subtable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if(subtable.getValue()!=null){
						Item item=subtable.getItem(subtable.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+table.getItem(table.getValue()).getItemProperty(TBC_NAME).getValue().toString()+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("account_name"),item.getItemProperty(TBC_NAME).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("opening_debit"),item.getItemProperty(TBC_OPENING_DEBIT).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("opening_credit"),item.getItemProperty(TBC_OPENING_CREDIT).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("current_debit"),item.getItemProperty(TBC_CURRENT_DEBIT).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("current_credit"),item.getItemProperty(TBC_CURRENT_CREDIT).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("debit"),item.getItemProperty(TBC_DEBIT).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("credit"),item.getItemProperty(TBC_CREDIT).getValue()+""));
						form.addComponent(new SLabel(getPropertyName("balance"),((Double)item.getItemProperty(TBC_DEBIT).getValue()-
																				(Double)item.getItemProperty(TBC_CREDIT).getValue())+""));
						form.setStyleName("grid_max_limit");
						subHor.removeAllComponents();
						SPopupView pop = new SPopupView("", form);
						subHor.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
					}
					else{
						subPop.setVisible(false);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		
		mainPanel.setContent(mainHorizontalLayout);

		return mainPanel;
	}

	
	
	@SuppressWarnings("rawtypes")
	protected void showReport() {
		try {

			List reportList = new ArrayList();

			if (isValid()) {

				reportList = daoObj.showBalancesReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());
				table.setVisibleColumns(allColoums);
				if (reportList != null && reportList.size() > 0) {
					Iterator itr=reportList.iterator();
					while (itr.hasNext()) {
						AcctReportMainBean bean=(AcctReportMainBean)itr.next();
						table.addItem(new Object[]{
								table.getItemIds().size()+1,
								bean.getId(),
								bean.getName(),
								roundNumber(bean.getBalance_debit()),
								roundNumber(bean.getBalance_credit()),
								roundNumber(bean.getBalance_debit()-bean.getBalance_credit())},table.getItemIds().size()+1);
					}
				} 
				else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
				table.setVisibleColumns(visibleColoums);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	protected void generateReport() {
		try {

			int slNo = 1;
			List reportList = new ArrayList();

			if (isValid()) {

				reportList = daoObj.getBalancesReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate",
							((Date) fromDate.getValue()).toString());
					params.put("ToDate", ((Date) toDate.getValue()).toString());

					report.setJrxmlFileName("TrialBalance");

					report.setReportFileName("Trial Balance");

					if (officeSelect.isVisible())
						report.setReportTitle("Trial Balance - "
								+ officeSelect.getItemCaption(officeSelect
										.getValue()));
					else
						report.setReportTitle("Trial Balance");

					report.setIncludeHeader(true);
					report.setReportSubTitle("From  : "
							+ (Date) fromDate.getValue() + "   To  : "
							+ (Date) toDate.getValue());
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, params);

					reportList.clear();

				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			toDate.focus();
			ret = false;
		} else
			setRequiredError(toDate, null, false);

		return ret;
	}

	
	
	@Override
	public Boolean getHelp() {
		return null;
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
