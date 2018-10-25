package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.inventory.reports.bean.BalanceSheetBean;
import com.inventory.reports.bean.BalanceSheetReportBean;
import com.inventory.reports.dao.ProfitAndLossReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

/**
 * 
 * @author anil
 * @date 04-Nov-2015
 * @Project REVERP
 */
public class ProfitAndLossReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton,showButton;

	private Report report;

	private ProfitAndLossReportDao daoObj;

	SDateField  fromDate,toDate;

	private SNativeSelect reportType;

	SOfficeComboField officeSelect;
	
	static String TBC_ID = "Id";
	static String TBC_income = "income";
	static String TBC_expense = "expense";
	static String TBC_DEBIT = "Debit";
	static String TBC_CREDIT = "Credit";
	static String TBC_LEVEL = "Level";
	
	static String TBC_LEDGER = "Ledger";
	static String TBC_AMOUNT = "Amount";
	
	Object[] allColoumsincome;
	Object[] visibleColoumsincome;
	Object[] allColoumsexpense;
	Object[] visibleColoumsexpense;
	Object[] allColoumsTotal;
	Object[] visibleColoumsTotal;
	
	TreeTable totalTable;
	TreeTable incomeTable;
	TreeTable expenseTable;
	SLabel profitLabel;
	SHorizontalLayout tableLay;
	SNativeSelect formatSelect;

	@Override
	public SPanel getGUI() {
		
		allColoumsincome=new Object[]{ TBC_ID, TBC_income, TBC_DEBIT,TBC_LEVEL};
		allColoumsexpense=new Object[]{ TBC_ID,  TBC_expense, TBC_CREDIT,TBC_LEVEL};
		visibleColoumsincome=new Object[]{ TBC_income,TBC_DEBIT};
		visibleColoumsexpense=new Object[]{TBC_expense,  TBC_CREDIT};
		allColoumsTotal=new Object[]{ TBC_ID,  TBC_LEDGER, TBC_AMOUNT,TBC_LEVEL};
		visibleColoumsTotal=new Object[]{TBC_LEDGER,  TBC_AMOUNT};

		setSize(900, 610);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		officeSelect = new SOfficeComboField(getPropertyName("office"), 150);
		
		formatSelect=new SNativeSelect(getPropertyName("format"), 100,
				Arrays.asList(new KeyValue((int) 1, "Format 1"), new KeyValue((int) 2, "Format 2")), "intKey", "value");
		formatSelect.setValue(1);

		reportType = new SNativeSelect(getPropertyName("report_type"), 100,
				SConstants.reportTypes, "intKey", "value");

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		// formLayout.setSpacing(true);
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		daoObj = new ProfitAndLossReportDao();

		fromDate = new SDateField(getPropertyName("from_date"), 150,
				getDateFormat(), getMonthStartDate());
		
		toDate = new SDateField(getPropertyName("to_date"), 150,
				getDateFormat(), getWorkingDate());

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(officeSelect);
		formLayout.addComponent(formatSelect);
//		formLayout.addComponent(reportType);
		incomeTable=new TreeTable();
		incomeTable.setWidth("300px");
		incomeTable.setHeight("500px");
		incomeTable.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		incomeTable.addContainerProperty(TBC_income, String.class, null, getPropertyName("income"), null, Align.LEFT);
		incomeTable.addContainerProperty(TBC_DEBIT, String.class, null, getPropertyName("amount"), null, Align.RIGHT);
		incomeTable.addContainerProperty(TBC_LEVEL, Integer.class, null, "Id", null, Align.CENTER);
		
		incomeTable.setColumnExpandRatio(TBC_income, 3f);
		incomeTable.setColumnExpandRatio(TBC_DEBIT, 1f);
		
		incomeTable.setSelectable(true);
		incomeTable.setFooterVisible(true);
		incomeTable.setColumnFooter(TBC_DEBIT, "0.0");
		incomeTable.setAnimationsEnabled(true);
		incomeTable.setVisibleColumns(visibleColoumsincome);
		
		
		expenseTable=new TreeTable();
		expenseTable.setWidth("300px");
		expenseTable.setHeight("500px");
		
		expenseTable.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		expenseTable.addContainerProperty(TBC_expense, String.class, null, getPropertyName("expense"), null, Align.LEFT);
		expenseTable.addContainerProperty(TBC_CREDIT, String.class, null, getPropertyName("amount"), null, Align.RIGHT);
		expenseTable.addContainerProperty(TBC_LEVEL, Integer.class, null, "Id", null, Align.CENTER);
		
		
		expenseTable.setColumnExpandRatio(TBC_expense, 3f);
		expenseTable.setColumnExpandRatio(TBC_CREDIT, 1f);
		expenseTable.setColumnFooter(TBC_CREDIT, "0.0");
		
		expenseTable.setSelectable(true);
		expenseTable.setFooterVisible(true);
		expenseTable.setAnimationsEnabled(true);
		expenseTable.setVisibleColumns(visibleColoumsexpense);
		
		
		totalTable=new TreeTable();
		totalTable.setWidth("300px");
		totalTable.setHeight("500px");
		
		totalTable.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		totalTable.addContainerProperty(TBC_LEDGER, String.class, null, getPropertyName("ledger"), null, Align.LEFT);
		totalTable.addContainerProperty(TBC_AMOUNT, String.class, null, getPropertyName("amount"), null, Align.RIGHT);
		totalTable.addContainerProperty(TBC_LEVEL, Integer.class, null, "Id", null, Align.CENTER);
		
		
		totalTable.setColumnExpandRatio(TBC_LEDGER, 3f);
		totalTable.setColumnExpandRatio(TBC_AMOUNT, 1f);
		totalTable.setColumnFooter(TBC_AMOUNT, "0.0");
		
		totalTable.setSelectable(true);
		totalTable.setFooterVisible(true);
		totalTable.setAnimationsEnabled(true);
		totalTable.setVisibleColumns(visibleColoumsTotal);
		totalTable.setStyleName("profit_table");
		
		profitLabel=new SLabel();
		profitLabel.setStyleName(Reindeer.LABEL_H2);
		
		reportType.setValue(0);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);

		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);
		
		SHorizontalLayout mainLay=new SHorizontalLayout();
		 tableLay=new SHorizontalLayout();
		SFormLayout profLay=new SFormLayout();
		tableLay.setSpacing(false);
		mainLay.setSpacing(true);
		mainLay.addComponent(formLayout);
		mainLay.addComponent(profLay);
		tableLay.addComponent(expenseTable);
		tableLay.addComponent(incomeTable);
		profLay.addComponent(tableLay);
		profLay.addComponent(profitLabel);
		
		formatSelect.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(toInt(formatSelect.getValue().toString())==1){
					tableLay.removeAllComponents();
					tableLay.addComponent(expenseTable);
					tableLay.addComponent(incomeTable);
				}else{
					tableLay.removeAllComponents();
					tableLay.addComponent(totalTable);
				}
			}
		});
		
		totalTable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(totalTable.getValue()!=null){
					if(totalTable.isCollapsed(totalTable.getValue()))
						totalTable.setCollapsed(totalTable.getValue(), false);
					else
						totalTable.setCollapsed(totalTable.getValue(), true);
				}
			}
		});
		
		totalTable.setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId,
					Object propertyId) {
				  Item itm=totalTable.getItem(itemId);
					 if (toLong(itm.getItemProperty(TBC_ID).getValue().toString())<1) {
			                return "lightBlue";
			         }else{
			        	 return "white";
			         }
			}
		    });
		
		
		incomeTable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(incomeTable.getValue()!=null){
					if(incomeTable.isCollapsed(incomeTable.getValue()))
						incomeTable.setCollapsed(incomeTable.getValue(), false);
					else
						incomeTable.setCollapsed(incomeTable.getValue(), true);
				}
			}
		});
		
		expenseTable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(expenseTable.getValue()!=null){
					if(expenseTable.isCollapsed(expenseTable.getValue()))
						expenseTable.setCollapsed(expenseTable.getValue(), false);
					else
						expenseTable.setCollapsed(expenseTable.getValue(), true);
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
		showButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
	
					showReport();
			}
		});

		mainPanel.setContent(mainLay);

		return mainPanel;
	}

	@SuppressWarnings("unchecked")
	protected void showReport() {
		if(toInt(formatSelect.getValue().toString())==1){
		try{
		List incomeList = new ArrayList();
		List expenseList = new ArrayList();
		incomeTable.removeAllItems();
		expenseTable.removeAllItems();
		incomeTable.setColumnFooter(TBC_DEBIT, "0.0");
		expenseTable.setColumnFooter(TBC_CREDIT, "0.0");
		
		incomeTable.setVisibleColumns(allColoumsincome);
		expenseTable.setVisibleColumns(allColoumsexpense);
		
		profitLabel.setValue("");
		
		if (isValid()) {

			boolean flag=false;
			incomeList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.INCOME,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID()));
			
			incomeList.add(daoObj.getAllSalesDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID()));
			BalanceSheetBean closingBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID());
			closingBean.setLedgerName("Closing Stock Value");
			incomeList.add(closingBean);
			
			
			double totalDr=0;
			double totalCr=0;
			if (incomeList != null && incomeList.size() > 0) {
				
				Collections.sort(incomeList, new Comparator<BalanceSheetBean>() {
					@Override
					public int compare(final BalanceSheetBean object1,
							final BalanceSheetBean object2) {
						int result = ((Long)object1.getParentId()).compareTo( (Long)object2.getParentId());
						return result;
					}
				});
				Iterator itr=incomeList.iterator();
				
				while (itr.hasNext()) {
					
					BalanceSheetBean bean=(BalanceSheetBean)itr.next();
					String amount=bean.getAmount()+"";
					if(bean.getAmount()<0)
						amount="-"+Math.abs(roundNumber(bean.getAmount()));
					
					incomeTable.addItem(new Object[]{
							bean.getLedgerId(),
							bean.getLedgerName(),
							amount,
							bean.getLevel()},bean.getId());
					if(bean.getParentId()!=0){
						incomeTable.setParent(bean.getId(), bean.getParentId());
					}else{
						//totalDr+=Math.abs(bean.getAmount());
						totalDr+=bean.getAmount();
					}
					
//					table.setCollapsed(bean.getId(), false);
					if (bean.getLevel()==-1)
						incomeTable.setChildrenAllowed(bean.getId(), false);
				}
				
				//incomeTable.setColumnFooter(TBC_DEBIT, Math.abs(totalDr)+"");
				incomeTable.setColumnFooter(TBC_DEBIT, roundNumberToString(Math.abs(totalDr)));
				
				flag=true;
			} 
			
			
			expenseList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.EXPENSE,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID()));
			expenseList.add(daoObj.getAllPurchaseDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID()));
			BalanceSheetBean openingStockBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID());
			openingStockBean.setLedgerName("Opening Stock Value");
			expenseList.add(openingStockBean);
			
			if(expenseList!=null&&expenseList.size()>0){
				
				Collections.sort(expenseList, new Comparator<BalanceSheetBean>() {
					@Override
					public int compare(final BalanceSheetBean object1,
							final BalanceSheetBean object2) {
						int result = ((Long)object1.getParentId()).compareTo( (Long)object2.getParentId());
						return result;
					}
				});
				Iterator itr=expenseList.iterator();
				
				while (itr.hasNext()) {
					
					BalanceSheetBean bean=(BalanceSheetBean)itr.next();
					String amount=bean.getAmount()+"";
					if(bean.getAmount()<0)
						amount="-"+Math.abs(roundNumber(bean.getAmount()));
						
					expenseTable.addItem(new Object[]{
							bean.getLedgerId(),
							bean.getLedgerName(),
							amount,
							bean.getLevel()},bean.getId());
					if(bean.getParentId()!=0){
						expenseTable.setParent(bean.getId(), bean.getParentId());
					}else{
						//totalCr+=Math.abs(bean.getAmount());
						totalCr+=bean.getAmount();
					}
					
					if (bean.getLevel()==-1)
						expenseTable.setChildrenAllowed(bean.getId(), false);
				}
				//expenseTable.setColumnFooter(TBC_CREDIT,Math.abs(totalCr)	+"");
				expenseTable.setColumnFooter(TBC_CREDIT,roundNumberToString(Math.abs(totalCr)));
				
				flag=true;
			}
			//double prof=Math.abs(totalDr)-Math.abs(totalCr);
			double prof=totalDr-totalCr;
			if(prof>0){
				profitLabel.setValue("Profit "+roundNumber(prof));
			}else if(prof<0){
				profitLabel.setValue("Loss "+ roundNumber(Math.abs(prof)));
			}
			if(!flag){
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
			}
		}
		incomeTable.setVisibleColumns(visibleColoumsincome);
		expenseTable.setVisibleColumns(visibleColoumsexpense);
	} catch (Exception e) {
		e.printStackTrace();
	}
		
		}else{
			try{
				List incomeList = new ArrayList();
				totalTable.removeAllItems();
				totalTable.setColumnFooter(TBC_DEBIT, "0.0");
				
				totalTable.setVisibleColumns(allColoumsTotal);
				
				profitLabel.setValue("");
				
				if (isValid()) {

					boolean flag=false;
					incomeList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.INCOME,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
							(Long) officeSelect.getValue(), getOrganizationID()));
					
					incomeList.add(new BalanceSheetBean("Total Incomes",0,0,-1,0,-10,0));
					
//					incomeList.add(daoObj.getAllSalesDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID()));
//					BalanceSheetBean closingBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID());
//					closingBean.setLedgerName("Closing Stock Value");
//					incomeList.add(closingBean);
					
				
					incomeList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.EXPENSE,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
							(Long) officeSelect.getValue(), getOrganizationID()));
					
					incomeList.add(new BalanceSheetBean("Total Expenses",0,0,-1,0,-9,0));
					
//					incomeList.add(daoObj.getAllPurchaseDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID()));
//					BalanceSheetBean openingStockBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID());
//					openingStockBean.setLedgerName("Opening Stock Value");
//					incomeList.add(openingStockBean);
					
					
					double totalCr=0;
					double totalAmnt=0;
					if (incomeList != null && incomeList.size() > 0) {
						
						Collections.sort(incomeList, new Comparator<BalanceSheetBean>() {
							@Override
							public int compare(final BalanceSheetBean object1,
									final BalanceSheetBean object2) {
								int result = ((Long)object1.getParentId()).compareTo( (Long)object2.getParentId());
								return result;
							}
						});
						Iterator itr=incomeList.iterator();
						
						while (itr.hasNext()) {
							
							BalanceSheetBean bean=(BalanceSheetBean)itr.next();
							String amount=bean.getAmount()+"";
							if(bean.getAmount()<0)
								amount="-"+Math.abs(roundNumber(bean.getAmount()));
							
							totalTable.addItem(new Object[]{
									bean.getLedgerId(),
									bean.getLedgerName(),
									amount,
									bean.getLevel()},bean.getId());
							if(bean.getParentId()!=0){
								totalTable.setParent(bean.getId(), bean.getParentId());
							}else{
								//totalDr+=Math.abs(bean.getAmount());
								totalAmnt+=bean.getAmount();
							}
							
//							table.setCollapsed(bean.getId(), false);
							if (bean.getLevel()==-1)
								totalTable.setChildrenAllowed(bean.getId(), false);
						}
						
						//incomeTable.setColumnFooter(TBC_DEBIT, Math.abs(totalDr)+"");
						totalTable.setColumnFooter(TBC_AMOUNT, roundNumberToString(Math.abs(totalAmnt)));
						
						flag=true;
					} 
					
					//double prof=Math.abs(totalDr)-Math.abs(totalCr);
					double prof=totalAmnt;
					if(prof>0){
						profitLabel.setValue("Profit "+roundNumber(prof));
					}else if(prof<0){
						profitLabel.setValue("Loss "+ roundNumber(Math.abs(prof)));
					}
					if(!flag){
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
					}
				}
				totalTable.setVisibleColumns(visibleColoumsTotal);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void generateReport() {
		try {

			if(toInt(formatSelect.getValue().toString())==1){
			int slNo = 1;
			List reportList = new ArrayList();
			
			List incomeList = new ArrayList();
			List expenseList = new ArrayList();
			
			

			if (isValid()) {
				boolean flag=false;
				
				incomeList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.INCOME,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID()));
				
				incomeList.add(daoObj.getAllSalesDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID()));
				BalanceSheetBean closingBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());
				closingBean.setLedgerName("Closing Stock Value");
				incomeList.add(closingBean);
				
				expenseList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.EXPENSE,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID()));
				expenseList.add(daoObj.getAllPurchaseDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID()));
				BalanceSheetBean openingStockBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());
				openingStockBean.setLedgerName("Opening Stock Value");
				expenseList.add(openingStockBean);
				
				List incList = new ArrayList();
				List expList = new ArrayList();
				double totalIncome=0;
				double totalExpense=0;
				double profit=0;
				

				if (incomeList != null && incomeList.size() > 0) {
					Iterator iter=incomeTable.getItemIds().iterator();
					while(iter.hasNext()){
						Item item = incomeTable.getItem(iter.next());
						BalanceSheetReportBean bean = new BalanceSheetReportBean();
						bean.setAmount(roundNumber(toDouble((String)  item.getItemProperty(TBC_DEBIT).getValue())));
						bean.setLedgerName(item.getItemProperty(TBC_income).getValue().toString());
						bean.setLevel((Integer) item.getItemProperty(TBC_LEVEL).getValue());
						incList.add(bean);
					}
					flag=true;
				}
				
				
				if (expenseList != null && expenseList.size() > 0) {
					Iterator iter=expenseTable.getItemIds().iterator();
					while(iter.hasNext()){
						Item item = expenseTable.getItem(iter.next());
						BalanceSheetReportBean bean = new BalanceSheetReportBean();
						bean.setAmount(roundNumber(toDouble((String) item.getItemProperty(TBC_CREDIT).getValue())));
						bean.setLedgerName(item.getItemProperty(TBC_expense).getValue().toString());
						bean.setLevel((Integer) item.getItemProperty(TBC_LEVEL).getValue());
						expList.add(bean);
					}
					flag=true;
				}	
				
				if(incList.size() > 0 || expenseList.size() > 0){
					BalanceSheetBean bean = new BalanceSheetBean();
					bean.setAsseteDetails(incList);
					bean.setLiabilityDetails(expenseList);
					reportList.add(bean);
					
					totalIncome=toDouble(incomeTable.getColumnFooter(TBC_DEBIT));
					totalExpense=toDouble(expenseTable.getColumnFooter(TBC_CREDIT));
					profit=totalIncome-totalExpense;
				
					
					String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"Jasper/";
					String incomeFileName="ProfitAndLossIncomeReportFormat2.jrxml";
					String newIncomeFileName="ProfitAndLossIncomeReportFormat2.jasper";
					String expenseFileName="ProfitAndLossExpenseReportFormat2.jrxml";
					String newExpenseFileName="ProfitAndLossExpenseReportFormat2.jasper";
					

					HashMap<String, Object> params = new HashMap<String, Object>();
					JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(incList, true);
					JasperDesign jasperDesign = JRXmlLoader.load(rootPath.trim()+incomeFileName.trim());
					JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newIncomeFileName.trim());
					JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
					JasperFillManager.fillReport(jasperReport, params,dataSource);
					
					HashMap<String, Object> params1 = new HashMap<String, Object>();
					JRBeanCollectionDataSource dataSource1 = new JRBeanCollectionDataSource(expenseList, true);
					JasperDesign jasperDesign1 = JRXmlLoader.load(rootPath.trim()+expenseFileName.trim());
					JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newExpenseFileName.trim());
					JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);
					JasperFillManager.fillReport(jasperReport1, params1,dataSource1);
					
					
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ToDate", toDate.getValue().toString());
					
					map.put("INCOME_LABEL", getPropertyName("income"));
					map.put("EXPENSE_LABEL", getPropertyName("expense"));
					map.put("ACCOUNT_NAME_LABEL", getPropertyName("account_name"));
					map.put("AMOUNT_LABEL", getPropertyName("amount"));
					map.put("TOTAL_LABEL", getPropertyName("total"));
					map.put("INCOME_TOTAL",roundNumber(totalIncome));
					map.put("EXPENSE_TOTAL", roundNumber(totalExpense));
					map.put("PROFIT", roundNumber(Math.abs(profit)));
					
					if(profit>0){
						map.put("PROFIT_LABEL", getPropertyName("profit"));
					}else if(profit<0){
						map.put("PROFIT_LABEL", getPropertyName("loss"));
					}
					
					
					map.put("SUBREPORT_DIR", rootPath);

					
						report.setJrxmlFileName("ProfitAndLossReport");
						
					
					report.setReportFileName("Profit And Loss Report");

//					if (officeSelect.isVisible())
//						report.setReportTitle("Profit And Loss Report - "
//								+ officeSelect.getItemCaption(officeSelect
//										.getValue()));
//					else
						report.setReportTitle("Profit And Loss Report for the year ended "+CommonUtil.formatDateToCommonFormat(toDate.getValue()));

					report.setIncludeHeader(true);
					report.setIncludeFooter(true);
					report.setReportSubTitle("");
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, map);

					reportList.clear();
					map.clear();
				}else{
					SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
				}
			}
			}else{
				
				int slNo = 1;
				List reportList = new ArrayList();
				
				List incomeList = new ArrayList();
				List expenseList = new ArrayList();
				
				

				if (isValid()) {
					boolean flag=false;
					
					incomeList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.INCOME,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
							(Long) officeSelect.getValue(), getOrganizationID()));
					
//					incomeList.add(daoObj.getAllSalesDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID()));
//					BalanceSheetBean closingBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID());
//					closingBean.setLedgerName("Closing Stock Value");
//					incomeList.add(closingBean);
					
					expenseList.addAll(daoObj.getProfitAndLoss(SConstants.account_parent_groups.EXPENSE,CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
							(Long) officeSelect.getValue(), getOrganizationID()));
//					expenseList.add(daoObj.getAllPurchaseDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID()));
//					BalanceSheetBean openingStockBean=daoObj.getStockValueTillDate(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
//							(Long) officeSelect.getValue(), getOrganizationID());
//					openingStockBean.setLedgerName("Opening Stock Value");
//					expenseList.add(openingStockBean);
					
					List incList = new ArrayList();
					List expList = new ArrayList();
					double totalIncome=0;
					double totalExpense=0;
					double profit=0;
					

					if (incomeList != null && incomeList.size() > 0) {
						Iterator iter=incomeTable.getItemIds().iterator();
						while(iter.hasNext()){
							Item item = incomeTable.getItem(iter.next());
							BalanceSheetReportBean bean = new BalanceSheetReportBean();
							bean.setAmount(roundNumber(toDouble((String)  item.getItemProperty(TBC_DEBIT).getValue())));
							bean.setLedgerName(item.getItemProperty(TBC_income).getValue().toString());
							bean.setLevel((Integer) item.getItemProperty(TBC_LEVEL).getValue());
							incList.add(bean);
						}
						flag=true;
					}
					
					
					if (expenseList != null && expenseList.size() > 0) {
						Iterator iter=expenseTable.getItemIds().iterator();
						while(iter.hasNext()){
							Item item = expenseTable.getItem(iter.next());
							BalanceSheetReportBean bean = new BalanceSheetReportBean();
							bean.setAmount(roundNumber(toDouble((String) item.getItemProperty(TBC_CREDIT).getValue())));
							bean.setLedgerName(item.getItemProperty(TBC_expense).getValue().toString());
							bean.setLevel((Integer) item.getItemProperty(TBC_LEVEL).getValue());
							expList.add(bean);
						}
						flag=true;
					}	
					
					if(incList.size() > 0 || expenseList.size() > 0){
						BalanceSheetBean bean = new BalanceSheetBean();
						bean.setAsseteDetails(incList);
						bean.setLiabilityDetails(expenseList);
						reportList.add(bean);
						
						totalIncome=toDouble(incomeTable.getColumnFooter(TBC_DEBIT));
						totalExpense=toDouble(expenseTable.getColumnFooter(TBC_CREDIT));
						profit=totalIncome-totalExpense;
					
						
						String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"Jasper/";
						String incomeFileName="ProfitAndLossIncomeReport.jrxml";
						String newIncomeFileName="ProfitAndLossIncomeReport.jasper";
						String expenseFileName="ProfitAndLossExpenseReport.jrxml";
						String newExpenseFileName="ProfitAndLossExpenseReport.jasper";
						

						HashMap<String, Object> params = new HashMap<String, Object>();
						JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(incList, true);
						JasperDesign jasperDesign = JRXmlLoader.load(rootPath.trim()+incomeFileName.trim());
						JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newIncomeFileName.trim());
						JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
						JasperFillManager.fillReport(jasperReport, params,dataSource);
						
						HashMap<String, Object> params1 = new HashMap<String, Object>();
						JRBeanCollectionDataSource dataSource1 = new JRBeanCollectionDataSource(expenseList, true);
						JasperDesign jasperDesign1 = JRXmlLoader.load(rootPath.trim()+expenseFileName.trim());
						JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newExpenseFileName.trim());
						JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);
						JasperFillManager.fillReport(jasperReport1, params1,dataSource1);
						
						
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("ToDate", toDate.getValue().toString());
						
						map.put("INCOME_LABEL", getPropertyName("income"));
						map.put("EXPENSE_LABEL", getPropertyName("expense"));
						map.put("ACCOUNT_NAME_LABEL", getPropertyName("account_name"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						map.put("TOTAL_LABEL", getPropertyName("total"));
						map.put("INCOME_TOTAL",roundNumber(totalIncome));
						map.put("EXPENSE_TOTAL", roundNumber(totalExpense));
						map.put("PROFIT", roundNumber(Math.abs(profit)));
						
						if(profit>0){
							map.put("PROFIT_LABEL", getPropertyName("profit"));
						}else if(profit<0){
							map.put("PROFIT_LABEL", getPropertyName("loss"));
						}
						
						
						map.put("SUBREPORT_DIR", rootPath);
						
						report.setJrxmlFileName("ProfitAndLossReportFormat2");
						report.setReportFileName("Profit And Loss Report");

//						if (officeSelect.isVisible())
//							report.setReportTitle("Profit And Loss Report - "
//									+ officeSelect.getItemCaption(officeSelect
//											.getValue()));
//						else
							report.setReportTitle("Profit And Loss Report for the year ended "+CommonUtil.formatDateToCommonFormat(toDate.getValue()));

						report.setIncludeHeader(true);
						report.setIncludeFooter(true);
						report.setReportSubTitle("");
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, map);

						reportList.clear();
						map.clear();
					}else{
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
					}
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
			setRequiredError(fromDate, getPropertyName("invalid_selection"), true);
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
		
//		if(fromDate.getValue().compareTo(getFinStartDate())<0){
//			setRequiredError(fromDate, getPropertyName("invalid_date"), true);
//			fromDate.focus();
//			ret = false;
//		}else{
//			setRequiredError(fromDate, null, false);
//		}
//		
//		if(toDate.getValue().compareTo(getFinEndDate())>0){
//			setRequiredError(toDate, getPropertyName("invalid_date"), true);
//			toDate.focus();
//			ret = false;
//		}else{
//			setRequiredError(toDate, null, false);
//		}

		
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
