package com.inventory.reports.ui;

import java.util.ArrayList;
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
import com.inventory.reports.dao.BalanceSheetDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinServlet;
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
 * @date 02-Nov-2015
 * @Project REVERP
 */
public class BalanceSheetUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton,showButton;

	private Report report;

	private BalanceSheetDao daoObj;

	SDateField  toDate;

	private SNativeSelect reportType;

	SOfficeComboField officeSelect;
	
	static String TBC_ID = "Id";
	static String TBC_ASSET = "Asset";
	static String TBC_LEVEL = "Level";
	static String TBC_LIABILITY = "Liability";
	static String TBC_DEBIT = "Debit";
	static String TBC_CREDIT = "Credit";
	
	Object[] allColoumsAsset;
	Object[] visibleColoumsAsset;
	Object[] allColoumsLiability;
	Object[] visibleColoumsLiability;
	
	TreeTable assetTable;
	TreeTable liabilityTable;
	
	SPopupView pop;

	@Override
	public SPanel getGUI() {
		
		allColoumsAsset=new Object[]{ TBC_ID, TBC_ASSET, TBC_DEBIT,TBC_LEVEL};
		allColoumsLiability=new Object[]{ TBC_ID,  TBC_LIABILITY, TBC_CREDIT,TBC_LEVEL};
		visibleColoumsAsset=new Object[]{ TBC_ASSET,TBC_DEBIT};
		visibleColoumsLiability=new Object[]{TBC_LIABILITY,  TBC_CREDIT};

		setSize(900, 600);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		officeSelect = new SOfficeComboField(getPropertyName("office"), 150);

		reportType = new SNativeSelect(getPropertyName("report_type"), 100,
				SConstants.reportTypes, "intKey", "value");

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		// formLayout.setSpacing(true);
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		daoObj = new BalanceSheetDao();

		toDate = new SDateField(getPropertyName("date"), 150,
				getDateFormat(), getFinEndDate());

		formLayout.addComponent(toDate);
		formLayout.addComponent(officeSelect);
//		formLayout.addComponent(reportType);
		assetTable=new TreeTable();
		assetTable.setWidth("300px");
		assetTable.setHeight("500px");
		assetTable.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		assetTable.addContainerProperty(TBC_ASSET, String.class, null, getPropertyName("asset"), null, Align.LEFT);
		assetTable.addContainerProperty(TBC_DEBIT, String.class, null, getPropertyName("amount"), null, Align.RIGHT);
		assetTable.addContainerProperty(TBC_LEVEL, Integer.class, null, "Id", null, Align.CENTER);
		
		assetTable.setColumnExpandRatio(TBC_ASSET, 3f);
		assetTable.setColumnExpandRatio(TBC_DEBIT, 1f);
		
		assetTable.setSelectable(true);
		assetTable.setFooterVisible(true);
		assetTable.setColumnFooter(TBC_DEBIT, "0.0");
		assetTable.setAnimationsEnabled(true);
		assetTable.setVisibleColumns(visibleColoumsAsset);
		
		
		liabilityTable=new TreeTable();
		liabilityTable.setWidth("300px");
		liabilityTable.setHeight("500px");
		
		liabilityTable.addContainerProperty(TBC_ID, Long.class, null, "Id", null, Align.CENTER);
		liabilityTable.addContainerProperty(TBC_LIABILITY, String.class, null, getPropertyName("liability"), null, Align.LEFT);
		liabilityTable.addContainerProperty(TBC_CREDIT, String.class, null, getPropertyName("amount"), null, Align.RIGHT);
		liabilityTable.addContainerProperty(TBC_LEVEL, Integer.class, null, "Id", null, Align.CENTER);
		
		liabilityTable.setColumnExpandRatio(TBC_LIABILITY, 3f);
		liabilityTable.setColumnExpandRatio(TBC_CREDIT, 1f);
		liabilityTable.setColumnFooter(TBC_CREDIT, "0.0");
		
		liabilityTable.setSelectable(true);
		liabilityTable.setFooterVisible(true);
		liabilityTable.setAnimationsEnabled(true);
		liabilityTable.setVisibleColumns(visibleColoumsLiability);
		
		reportType.setValue(0);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);

		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);
		
		SHorizontalLayout mainLay=new SHorizontalLayout();
		SHorizontalLayout tableLay=new SHorizontalLayout();
		tableLay.setSpacing(false);
		mainLay.setSpacing(true);
		mainLay.addComponent(formLayout);
		mainLay.addComponent(tableLay);
		
		tableLay.addComponent(liabilityTable);
		tableLay.addComponent(assetTable);
		
		assetTable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(assetTable.getValue()!=null){
					if(assetTable.isCollapsed(assetTable.getValue()))
						assetTable.setCollapsed(assetTable.getValue(), false);
					else
						assetTable.setCollapsed(assetTable.getValue(), true);
				}
			}
		});
		
		liabilityTable.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(liabilityTable.getValue()!=null){
					if(liabilityTable.isCollapsed(liabilityTable.getValue()))
						liabilityTable.setCollapsed(liabilityTable.getValue(), false);
					else
						liabilityTable.setCollapsed(liabilityTable.getValue(), true);
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
				if (isValid()) {
					showReport();
				}
			}
		});

		mainPanel.setContent(mainLay);

		return mainPanel;
	}

	@SuppressWarnings("unchecked")
	protected void showReport() {
		
		try{
		List assetList = new ArrayList();
		List liabilityList = new ArrayList();
		assetTable.removeAllItems();
		liabilityTable.removeAllItems();
		assetTable.setColumnFooter(TBC_DEBIT, "0.0");
		liabilityTable.setColumnFooter(TBC_CREDIT, "0.0");
		
		assetTable.setVisibleColumns(allColoumsAsset);
		liabilityTable.setVisibleColumns(allColoumsLiability);
		
		if (isValid()) {

			boolean flag=false;
			assetList = daoObj.getBalanceSheet(SConstants.account_parent_groups.ASSET,
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID());
			
			liabilityList = daoObj.getBalanceSheet(SConstants.account_parent_groups.LIABILITY,
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long) officeSelect.getValue(), getOrganizationID());
			
			
			long id=0;
			if (assetList != null && assetList.size() > 0) {
				
				Collections.sort(assetList, new Comparator<BalanceSheetBean>() {
					@Override
					public int compare(final BalanceSheetBean object1,
							final BalanceSheetBean object2) {
						int result = ((Long)object1.getParentId()).compareTo( (Long)object2.getParentId());
						return result;
					}
				});
				Iterator itr=assetList.iterator();
				
				double totalDr=0;
				while (itr.hasNext()) {
					
					BalanceSheetBean bean=(BalanceSheetBean)itr.next();
					String amount=bean.getAmount()+"";
					if(bean.getAmount()<0)
						amount=""+Math.abs(roundNumber(bean.getAmount()));
					
					assetTable.addItem(new Object[]{
							bean.getLedgerId(),
							bean.getLedgerName(),
							amount,
							bean.getLevel()},bean.getId());
					if(bean.getParentId()!=0){
						assetTable.setParent(bean.getId(), bean.getParentId());
					}else{
						totalDr+=bean.getAmount();
					}
					
//					table.setCollapsed(bean.getId(), false);
					if (bean.getLevel()==-1)
						assetTable.setChildrenAllowed(bean.getId(), false);
				}
				
				assetTable.setColumnFooter(TBC_DEBIT, Math.abs(totalDr)+"");
				
				flag=true;
			} 
			
			if(liabilityList!=null&&liabilityList.size()>0){
				
				Collections.sort(liabilityList, new Comparator<BalanceSheetBean>() {
					@Override
					public int compare(final BalanceSheetBean object1,
							final BalanceSheetBean object2) {
						int result = ((Long)object1.getParentId()).compareTo( (Long)object2.getParentId());
						return result;
					}
				});
				Iterator itr=liabilityList.iterator();
				double totalCr=0;
				while (itr.hasNext()) {
					
					BalanceSheetBean bean=(BalanceSheetBean)itr.next();
					String amount=bean.getAmount()+"";
					if(bean.getAmount()<0)
						amount=""+Math.abs(roundNumber(bean.getAmount()));
						
					liabilityTable.addItem(new Object[]{
							bean.getLedgerId(),
							bean.getLedgerName(),
							amount,
							bean.getLevel()},bean.getId());
					if(bean.getParentId()!=0){
						liabilityTable.setParent(bean.getId(), bean.getParentId());
					}else{
						totalCr+=bean.getAmount();
					}
					
					if (bean.getLevel()==-1)
						liabilityTable.setChildrenAllowed(bean.getId(), false);
				}
				liabilityTable.setColumnFooter(TBC_CREDIT,Math.abs(totalCr)	+"");
				
				flag=true;
			}
			
			if(!flag){
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
			}
		}
		assetTable.setVisibleColumns(visibleColoumsAsset);
		liabilityTable.setVisibleColumns(visibleColoumsLiability);
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}
	
	
	@SuppressWarnings("unchecked")
	protected void generateReport() {
		try {

			int slNo = 1;
			List reportList = new ArrayList();
			List reportList1 = new ArrayList();
			List resList = new ArrayList();

			if (isValid()) {

				boolean flag=false;
				
				reportList = daoObj.getBalanceSheet(SConstants.account_parent_groups.ASSET,
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());
				
				reportList1 = daoObj.getBalanceSheet(SConstants.account_parent_groups.LIABILITY,
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(), getOrganizationID());
				
				List asseteList = new ArrayList(); 
				List liabilityList = new ArrayList(); 
				
				double totalAsset=0;
				double totalLiability=0;
				
				if (reportList != null && reportList.size() > 0) {
					Iterator iter=assetTable.getItemIds().iterator();
					
					while(iter.hasNext()){
						Item item = assetTable.getItem(iter.next());
						BalanceSheetReportBean bean = new BalanceSheetReportBean();
						bean.setAmount(roundNumber(toDouble((String)  item.getItemProperty(TBC_DEBIT).getValue())));
						bean.setLedgerName(item.getItemProperty(TBC_ASSET).getValue().toString());
						bean.setLevel((Integer) item.getItemProperty(TBC_LEVEL).getValue());
						asseteList.add(bean);
					}
					flag=true;
				}
					
				if(reportList1 != null && reportList1.size() > 0){
					Iterator iter=liabilityTable.getItemIds().iterator();
					while(iter.hasNext()){
						Item item = liabilityTable.getItem(iter.next());
						BalanceSheetReportBean bean = new BalanceSheetReportBean();
						bean.setAmount(roundNumber(toDouble((String) item.getItemProperty(TBC_CREDIT).getValue())));
						bean.setLedgerName(item.getItemProperty(TBC_LIABILITY).getValue().toString());
						bean.setLevel((Integer) item.getItemProperty(TBC_LEVEL).getValue());
						liabilityList.add(bean);
					}
					flag=true;
				}
				if(asseteList.size() > 0 || liabilityList.size() > 0){
					BalanceSheetBean bean = new BalanceSheetBean();
					bean.setAsseteDetails(asseteList);
					bean.setLiabilityDetails(liabilityList);
					resList.add(bean);
					
					totalAsset=toDouble(assetTable.getColumnFooter(TBC_DEBIT));
					totalLiability=toDouble(liabilityTable.getColumnFooter(TBC_CREDIT));
					
					
					String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"Jasper/";
					String asseteFileName="BalanceSheetAsseteReport.jrxml";
					String newAsseteFileName="BalanceSheetAsseteReport.jasper";
					String liabilityFileName="BalanceSheetLiabilityReport.jrxml";
					String newLiabilityFileName="BalanceSheetLiabilityReport.jasper";
					
					
					HashMap<String, Object> params = new HashMap<String, Object>();
					JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(asseteList, true);
					JasperDesign jasperDesign = JRXmlLoader.load(rootPath.trim()+asseteFileName.trim());
					JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newAsseteFileName.trim());
					JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
					JasperFillManager.fillReport(jasperReport, params,dataSource);
					
					HashMap<String, Object> params1 = new HashMap<String, Object>();
					JRBeanCollectionDataSource dataSource1 = new JRBeanCollectionDataSource(liabilityList, true);
					JasperDesign jasperDesign1 = JRXmlLoader.load(rootPath.trim()+liabilityFileName.trim());
					JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newLiabilityFileName.trim());
					JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);
					JasperFillManager.fillReport(jasperReport1, params1,dataSource1);
					
					
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("ToDate", toDate.getValue().toString());
						map.put("ASSETE_LABEL", getPropertyName("asset"));
						map.put("LIABILITY_LABEL", getPropertyName("liability"));
						map.put("ACCOUNT_NAME_LABEL", getPropertyName("account_name"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						
						map.put("TOTAL_LABEL", getPropertyName("total"));
						map.put("ASSETE_TOTAL",totalAsset);
						map.put("LIABILITY_TOTAL", totalLiability);
						
						map.put("SUBREPORT_DIR", rootPath);

						report.setJrxmlFileName("BalanceSheetNew");
						report.setReportFileName("Balance Sheet");

						report.setReportTitle("Balance Sheet as on "+ toDate.getValue());

						report.setIncludeHeader(true);
						report.setIncludeFooter(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(resList, map);

						resList.clear();

					
				}else{
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
