package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.TrialBalanceBean;
import com.inventory.reports.dao.TrialBalanceDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TreeTable;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
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
public class TrialBalanceUI extends SparkLogic {

	private static final long serialVersionUID = -1299213589886922530L;
	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton,showButton;

	private Report report;

	private TrialBalanceDao daoObj;

	private SNativeSelect reportType;

	SDateField  toDate;

	SOfficeComboField officeSelect;
	
	TreeTable table;
	SNativeButton closeBtn;
	SHorizontalLayout mainHorizontalLayout,popHor,subHor;
	SPopupView pop,subPop;
	
	static String TBC_ID = "Id";
	static String TBC_NAME = "Account Name";
	static String TBC_DEBIT = "Debit";
	static String TBC_CREDIT = "Credit";
//	static String TBC_BALANCE = "Balance";
	static String TBC_OPENING_DEBIT = "Opening Debit";
	static String TBC_OPENING_CREDIT = "Opening Credit";
	static String TBC_CURRENT_DEBIT = "Current Debit";
	static String TBC_CURRENT_CREDIT = "Current Credit";
	
	Object[] allColoums;
	Object[] visibleColoums;
//	private ExcelExport excelExport;	
	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		allColoums=new Object[]{ TBC_ID, TBC_NAME, TBC_DEBIT, TBC_CREDIT};
		visibleColoums=new Object[]{ TBC_NAME, TBC_DEBIT, TBC_CREDIT};
		
		setSize(1000, 600);
		table=new TreeTable();
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

		daoObj = new TrialBalanceDao();
		
		table.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		table.addContainerProperty(TBC_NAME, String.class, null, getPropertyName("account_name"), null, Align.LEFT);
		table.addContainerProperty(TBC_DEBIT, Double.class, null, getPropertyName("debit"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CREDIT, Double.class, null, getPropertyName("credit"), null, Align.RIGHT);
//		table.addContainerProperty(TBC_BALANCE, Double.class, null, getPropertyName("balance"), null, Align.RIGHT);
		
		
		table.setColumnExpandRatio(TBC_NAME, 2f);
		table.setColumnExpandRatio(TBC_DEBIT, 1f);
		table.setColumnExpandRatio(TBC_CREDIT, 1f);
		table.setSelectable(true);
		table.setFooterVisible(true);
		table.setColumnFooter(TBC_NAME, "Total");
		table.setColumnFooter(TBC_DEBIT, "0.0");
		table.setColumnFooter(TBC_CREDIT, "0.0");
		table.setAnimationsEnabled(true);
		
		
		table.setVisibleColumns(visibleColoums);
		toDate = new SDateField(getPropertyName("date"), 150,
				getDateFormat(), getWorkingDate());
		
		table.setWidth("700px");
		table.setHeight("500px");
//		 excelExport = new ExcelExport(table);

		formLayout.addComponent(toDate);
		formLayout.addComponent(officeSelect);
//		formLayout.addComponent(reportType);
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
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue()!=null){
					if(table.isCollapsed(table.getValue()))
						table.setCollapsed(table.getValue(), false);
					else
						table.setCollapsed(table.getValue(), true);
				}
			}
		});
		
		generateButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
					
					
//		             excelExport.excludeCollapsedColumns();
//		             excelExport.export();
				}
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
//				 excelExport = new ExcelExport(table);
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
		
		
		mainPanel.setContent(mainHorizontalLayout);

		return mainPanel;
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void showReport() {
		try {

			List reportList = new ArrayList();
			table.removeAllItems();
			table.setColumnFooter(TBC_DEBIT, "0.0");
			table.setColumnFooter(TBC_CREDIT, "0.0");
			if (isValid()) {

				reportList = daoObj.showTrialBalance(
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());
				table.setVisibleColumns(allColoums);
				
				long id=0;
				if (reportList != null && reportList.size() > 0) {
					
					Collections.sort(reportList, new Comparator<TrialBalanceBean>() {
						@Override
						public int compare(final TrialBalanceBean object1,
								final TrialBalanceBean object2) {
							int result = ((Long)object1.getParentId()).compareTo( (Long)object2.getParentId());
							return result;
						}
					});
					Iterator itr=reportList.iterator();
					
					double totalCr=0;
					double totalDr=0;
					double credit=0,debit=0;
					while (itr.hasNext()) {
						TrialBalanceBean bean=(TrialBalanceBean)itr.next();
						if(bean.getClassId()==SConstants.account_parent_groups.ASSET||bean.getClassId()==SConstants.account_parent_groups.EXPENSE){
							credit=0;
							debit=roundNumber(bean.getDebitAmount()-bean.getCreditAmount());
						}else{
							credit=roundNumber(bean.getDebitAmount()-bean.getCreditAmount());
							debit=0;
						}
							
						table.addItem(new Object[]{
								bean.getLedgerId(),
								bean.getLedgerName(),
								Math.abs(debit),
								Math.abs(credit)
								},bean.getId());
						if(bean.getParentId()!=0){
							table.setParent(bean.getId(), bean.getParentId());
						}else{
							totalDr+=debit;
							totalCr+=credit;
						}
						
//						table.setCollapsed(bean.getId(), false);
						if (bean.getLevel()==-1)
							table.setChildrenAllowed(bean.getId(), false);
					}
					
					table.setColumnFooter(TBC_DEBIT, roundNumberToString(Math.abs(totalDr)));
					table.setColumnFooter(TBC_CREDIT,roundNumberToString(Math.abs(totalCr)));
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

			List reportList = new ArrayList();
			List resList = new ArrayList();

			if (isValid()) {

				reportList = daoObj.showTrialBalance(
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());

				if (reportList != null && reportList.size() > 0) {
					
					TrialBalanceBean bean;
					Iterator iter=reportList.iterator();
					String name="";
					double credit=0,debit=0;
					while (iter.hasNext()) {
						bean = (TrialBalanceBean) iter.next();
						bean.setAmount(roundNumber(bean.getDebitAmount()-bean.getCreditAmount()));
						
						name=bean.getLedgerName();
//						if(bean.getType()==0)
//							name=bean.getLedgerName();
//						else if(bean.getType()==1){
//							name+="		";
//							for(int i=0;i<bean.getLevel();i++){
//								name+="		";
//							}
//							name+="-->"+bean.getLedgerName();
//						}else{
//							name+="		";
//							name+="-->"+bean.getLedgerName();
//						}
						bean.setLedgerName(name);
						
						if(roundNumber(bean.getDebitAmount()-bean.getCreditAmount())>0){
							credit=0;
							debit=roundNumber(bean.getDebitAmount()-bean.getCreditAmount());
						}else{
							credit=roundNumber(bean.getDebitAmount()-bean.getCreditAmount());
							debit=0;
						}
						
						bean.setCreditAmount(Math.abs(credit));
						bean.setDebitAmount(debit);
						
						
						resList.add(bean);
					}
					
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("ToDate", ((Date) toDate.getValue()).toString());
					params.put("ACCOUNT_NAME_LABEL", getPropertyName("particulars"));
					params.put("DEBIT_LABEL", getPropertyName("debit"));
					params.put("CREDIT_LABEL", getPropertyName("credit"));
//					params.put("BALANCE_LABEL", getPropertyName("balance"));
					
					report.setJrxmlFileName("TrialBalanceNew");

					report.setReportFileName("Trial Balance");

					if (officeSelect.isVisible())
						report.setReportTitle(getPropertyName("trial_balance")+" - "
								+ officeSelect.getItemCaption(officeSelect
										.getValue()));
					else
						report.setReportTitle(getPropertyName("trial_balance"));

					report.setIncludeHeader(true);
					report.setReportSubTitle(getPropertyName("as_on")+" : "
							+ (Date) toDate.getValue());
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(resList, params);
					
					reportList.clear();
					resList.clear();

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
