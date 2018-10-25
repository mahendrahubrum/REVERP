package com.inventory.reports.ui;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.DisposeItemsDao;
import com.inventory.config.stock.model.DisposalItemsDetailsModel;
import com.inventory.config.stock.model.DisposeItemsModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class DisposalItemsReportUI extends SparkLogic{

	private SComboField officeComboField;
	
	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_DATE = "Date";
	
	STable table;
	private Object[] allHeaders;
	private Object[] requiredHeaders;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SButton generateButton;
	private SButton showButton;
	SHorizontalLayout buttonLayout;
	
	private SReportChoiceField reportChoiceField;

	private SFormLayout masterGrid;
	DisposeItemsDao disposeDao;
	
	Report report;

	private SVerticalLayout mainverticalLay;

	
	@Override
	public SPanel getGUI() {
		
		setSize(850, 300);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		
		disposeDao=new DisposeItemsDao();
		
		masterGrid = new SFormLayout();
		masterGrid.setSpacing(true);
		
		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY,TBC_DATE };
		requiredHeaders = new String[] { TBC_SN,
				TBC_ITEM_NAME, TBC_QTY,TBC_DATE};
		
		SHorizontalLayout mainLay = new SHorizontalLayout();
		mainLay.setMargin(true);
		mainLay.setSpacing(true);
		
		report = new Report(getLoginID());
		
		try {
			officeComboField = new SComboField(getPropertyName("office"), 150,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			
			officeComboField.setValue(getOfficeID());
			
			reportChoiceField = new SReportChoiceField(
					getPropertyName("print_as"));
			
			fromDateField = new SDateField(getPropertyName("from_date"),150);
			fromDateField.setValue(getMonthStartDate());
			fromDateField.setImmediate(true);
			toDateField = new SDateField(getPropertyName("to_date"),150);
			toDateField.setValue(getWorkingDate());
			toDateField.setImmediate(true);
			
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			mainverticalLay=new SVerticalLayout();
			
			generateButton = new SButton(getPropertyName("generate"),90);
			generateButton.setClickShortcut(KeyCode.ENTER);
			showButton = new SButton(getPropertyName("show"),80);
			
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			
			buttonLayout.setComponentAlignment(generateButton, Alignment.BOTTOM_CENTER);
			buttonLayout.setComponentAlignment(showButton, Alignment.BOTTOM_CENTER);
			
			masterGrid.addComponent(officeComboField);
			masterGrid.addComponent(fromDateField);
			masterGrid.addComponent(toDateField);
			masterGrid.addComponent(reportChoiceField);
			
			mainverticalLay.addComponent(masterGrid);
			mainverticalLay.addComponent(buttonLayout);
			
			
			
			table = new STable(null, 520, 200);
			table.setMultiSelect(true);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.LEFT);
			
			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 1);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 2);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_DATE, 1);
			
			table.setVisibleColumns(requiredHeaders);
			table.setSizeFull();
			table.setSelectable(true);
			table.setPageLength(table.size());
			table.setWidth("520");
			table.setHeight("200");

			mainLay.addComponent(mainverticalLay);
			mainLay.addComponent(table);
			panel.setContent(mainLay);
			
			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
				}
			});
			
			fromDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
				table.removeAllItems();
				}
			});

			
			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					table.removeAllItems();
				}
			});
			
			
			showButton.addClickListener(new ClickListener() {
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						table.removeAllItems();
						
						if(isValid()){
							List itemList=new ArrayList();
							itemList=disposeDao.getAllDisposalItemsForReport((Long)officeComboField.getValue(), 
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
							
							if(itemList.size()>0){

								DisposeItemsModel mdl;
								DisposeItemsModel itemMdl;
								DisposalItemsDetailsModel detMdl;
								java.util.Iterator it= itemList.iterator();
								while(it.hasNext()){
									mdl= (DisposeItemsModel) it.next();
									itemMdl=disposeDao.getDisposeItemsModel(mdl.getId());
									
									List itemDtlList=itemMdl.getItem_details_list();
									
									table.setVisibleColumns(allHeaders);
									java.util.Iterator itr= itemDtlList.iterator();
									while(itr.hasNext()){
										detMdl=(DisposalItemsDetailsModel) itr.next();
										table.addItem(
												new Object[] {
														table.getItemIds().size() + 1,
														detMdl.getItem().getId(),
														detMdl.getItem().getItem_code(),
														detMdl.getItem().getName(),
														detMdl.getQunatity(),
														itemMdl.getDate()},
														table.getItemIds().size() + 1);
									}
									table.setVisibleColumns(requiredHeaders);
								}
							}else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			generateButton.addClickListener(new ClickListener() {
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							HashMap<String, Object> map = new HashMap<String, Object>();
							List<Object> reportList = new ArrayList<Object>();
							ReportBean bean=null;
							
							map.put("REPORT_TITLE", getPropertyName("disposed_item_report"));
							map.put("HEADER", getPropertyName("disposed_item_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_NAME_LABEL",getPropertyName("item_name"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("ITEM_CODE_LABEL", getPropertyName("item_code"));
							
							S_OfficeModel office = null;
							List itemList=new ArrayList();
							itemList=disposeDao.getAllDisposalItemsForReport(getOfficeID(), 
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
							DisposeItemsModel mdl;
							DisposeItemsModel itemMdl;
							DisposalItemsDetailsModel detMdl;
							Iterator it= itemList.iterator();
							while(it.hasNext()){
								mdl= (DisposeItemsModel) it.next();
								itemMdl=disposeDao.getDisposeItemsModel(mdl.getId());
								
								office =new OfficeDao().getOffice(itemMdl.getOffice().getId());
								
								List itemDtlList=new ArrayList();
								itemDtlList=itemMdl.getItem_details_list();
								Iterator itr= itemDtlList.iterator();
								while(itr.hasNext()){
									detMdl=(DisposalItemsDetailsModel) itr.next();
									
									bean = new ReportBean(detMdl.getId(),
											detMdl.getItem().getName(),
											detMdl.getItem().getItem_code(),
											detMdl.getQunatity(),
											CommonUtil.formatDateToDDMMYYYY(itemMdl.getDate()));
									reportList.add(bean);
									
								}
							}
							
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
	                        map.put("OFFICE_NAME", office.getName());
	                        try {
	                            File headFile=new File(rootPath.trim()+office.getHeader().trim());
	                            if(headFile.exists()&& !headFile.isDirectory())
	                                map.put("HEADER", rootPath.trim()+office.getHeader().trim());
	                            else
	                                map.put("HEADER", rootPath.trim()+"blank.png");
	                        } catch (Exception e1) {
	                        	map.put("HEADER", rootPath.trim()+"blank.png");
	                        }
	                        try {
	                            File footFile=new File(rootPath.trim()+office.getFooter().trim());
	                            if(footFile.exists()&& !footFile.isDirectory())
	                                map.put("FOOTER", rootPath.trim()+office.getFooter().trim());
	                            else
	                                map.put("FOOTER", rootPath.trim()+"blank.png");
	                        } catch (Exception e1) {
	                        	map.put("FOOTER", rootPath.trim()+"blank.png");
	                        }
							
							if(reportList.size()>0){
								report.setJrxmlFileName("DisposalItemsReport");
								report.setReportFileName("DisposalItemsReportPrint");
								report.setReportTitle(getPropertyName("disposed_item_report"));
								report.setIncludeHeader(true);
								report.setIncludeFooter(true);
								report.setReportType((Integer)reportChoiceField.getValue());
								report.createReport(reportList, map);
								report.print();
								reportList.clear();
							}
							else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}

	
	@Override
	public Boolean isValid() {
		boolean ret=true;
		
		if (officeComboField.getValue() == null	|| officeComboField.getValue().equals("")) {
			setRequiredError(officeComboField,getPropertyName("invalid_selection"), true);
			officeComboField.focus();
			ret = false;
		} else
			setRequiredError(officeComboField, null, false);
		
		if (fromDateField.getValue() == null || fromDateField.getValue().equals("")) {
			setRequiredError(fromDateField, getPropertyName("invalid_selection"),true);
			fromDateField.focus();
			ret = false;
		} else
			setRequiredError(fromDateField, null, false);
		
		if (toDateField.getValue() == null || toDateField.getValue().equals("")) {
			setRequiredError(toDateField, getPropertyName("invalid_selection"),true);
			toDateField.focus();
			ret = false;
		} else
			setRequiredError(toDateField, null, false);
		
		return ret;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
