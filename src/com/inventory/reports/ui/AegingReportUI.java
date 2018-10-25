package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.haijian.ExcelExporter;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.AegingReportBean;
import com.inventory.reports.dao.AegingReportDao;
import com.inventory.reports.dao.IntervalReportDao;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;

/**
 * 
 * @author Muhammed shah A
 * @date Nov 3, 2015
 * @Project REVERP
 */

public class AegingReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private static final int DETAIL_REPORT = 2;
	private static final int SUMMARY_REPORT = 1;

	private static final int CUSTOMER = 1;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	// private SButton generateButton;
	private SButton showButton;

	private Report report;

	private IntervalReportDao daoObj;

	private SDateField fromDateField;

	// private SComboField organizationSelect;
	private SComboField officeSelect;
	private SRadioButton customerOrSupplierRadioButton;

	private STextField intervalDaysTextField;
	private STextField no_ofIntervalsTextField;

	private SRadioButton reportTypeRadioButton;

	static String TBC_SN = "SN";
	static String TBC_PARTICULARS = "Customer";
	static String TBC_UN_ADJUSTED_AMOUNT = "Un Adjusted";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLayout;

	STable table;

	Object[] allColumns;
	Object[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	private ArrayList<String> visibleColumnsList;

	ExcelExporter excelExporter;

	private SComboField customerOrSupplierSComboField;

	private AegingReportDao aegingReportDao;

	private CustomerDao customerDao;

	private HashMap<Long, AegingReportBean> reportBeanHashMap;

	private List beanList;

	private boolean flag;

	private HashMap<Long, Integer> creditPeriodHashMap;

	private SupplierDao supplierDao;

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	@Override
	public SPanel getGUI() {

		try {
			allColumns = new String[] { TBC_SN, TBC_PARTICULARS,TBC_UN_ADJUSTED_AMOUNT, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS,TBC_UN_ADJUSTED_AMOUNT,
					TBC_BALANCE };
			visibleColumnsList = new ArrayList<String>(Arrays.asList(TBC_SN,
					TBC_PARTICULARS, TBC_UN_ADJUSTED_AMOUNT, TBC_BALANCE));

			setSize(1200, 500);
			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();
			aegingReportDao = new AegingReportDao();
			customerDao = new CustomerDao();
			supplierDao = new SupplierDao();

			officeSelect = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			officeSelect.setValue(getOfficeID());

			if (isSuperAdmin() || isSystemAdmin()) {
				officeSelect.setEnabled(true);
			} else {
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			List rentList = new ArrayList();
			rentList.add(new KeyValue(1, getPropertyName("customers")));
			rentList.add(new KeyValue(2, getPropertyName("suppliers")));
			customerOrSupplierRadioButton = new SRadioButton(
					getPropertyName("type"), 100, rentList, "intKey", "value");
			customerOrSupplierRadioButton.setValue(1);

			fromDateField = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());

			intervalDaysTextField = new STextField(
					getPropertyName("interval_days"), 150, "15");
			no_ofIntervalsTextField = new STextField(
					getPropertyName("no_intervals"), 150, "5");

			customerOrSupplierSComboField = new SComboField(null, 200, null,
					"id", "name");
			customerOrSupplierSComboField.setVisible(false);

			List reportTypeList = new ArrayList();
			reportTypeList.add(new KeyValue(1, getPropertyName("summary")));
			reportTypeList.add(new KeyValue(2, getPropertyName("detail")));
			reportTypeRadioButton = new SRadioButton(getPropertyName("report_type"),
					200, reportTypeList, "intKey", "value");
			reportTypeRadioButton.setHorizontal(true);
			reportTypeRadioButton.setValue(1);
			// reportTypeRadioButton.setStyleName("radio_horizontal");

			table = new STable(null, 1000, 200);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_UN_ADJUSTED_AMOUNT, Double.class, null,
					getPropertyName("un_adjusted"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnWidth(TBC_SN, 30);
			table.setColumnWidth(TBC_BALANCE, 140);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("820");

			// generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			excelExporter = new ExcelExporter(table);			
			excelExporter.setCaption(getPropertyName("export_excel"));

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			popupContainer = new SHorizontalLayout();
			mainLayout = new SHorizontalLayout();

			report = new Report(getLoginID());

			formLayout = new SFormLayout();
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new IntervalReportDao();

			buttonLayout.addComponent(showButton);
			buttonLayout.addComponent(excelExporter);

			formLayout.addComponent(officeSelect);
			formLayout.addComponent(customerOrSupplierRadioButton);
			formLayout.addComponent(fromDateField);
			formLayout.addComponent(intervalDaysTextField);
			formLayout.addComponent(no_ofIntervalsTextField);
			formLayout.addComponent(reportTypeRadioButton);
			formLayout.addComponent(customerOrSupplierSComboField);
			formLayout.addComponent(buttonLayout);

			mainLayout.addComponent(formLayout);
			mainLayout.addComponent(table);
			mainLayout.addComponent(popupContainer);

			mainLayout.setMargin(true);

			// table.addValueChangeListener(new ValueChangeListener() {
			//
			// @Override
			// public void valueChange(ValueChangeEvent event) {
			// try {
			// if (table.getValue() != null) {
			// int count = 1;
			// Item item = table.getItem(table.getValue());
			// SFormLayout form = new SFormLayout();
			// form.addComponent(new SHTMLLabel(null, "<h2><u>"
			// + getPropertyName("period_wise_ledger")
			// + "</u></h2>"));
			// form.addComponent(new SLabel(
			// customerOrSupplierRadioButton
			// .getItemCaption(customerOrSupplierRadioButton
			// .getValue()), item
			// .getItemProperty(TBC_PARTICULARS)
			// .getValue().toString()));
			// form.addComponent(new SLabel(
			// getPropertyName("balance_on")
			// + CommonUtil
			// .formatDateToDDMMYYYY(fromDate
			// .getValue()), item
			// .getItemProperty(TBC_BALANCE)
			// .getValue().toString()));
			// for (int i = 3; i < visibleColumnsList.size(); i++) {
			// form.addComponent(new SLabel(
			// getPropertyName("balance_after")
			// + toDouble(intervalDays
			// .getValue().toString())
			// * count + " Days", item
			// .getItemProperty(
			// visibleColumnsList
			// .get(i))
			// .getValue().toString()));
			// count++;
			// }
			//
			// form.setStyleName("grid_max_limit");
			// popupContainer.removeAllComponents();
			// SPopupView pop = new SPopupView("", form);
			// popupContainer.addComponent(pop);
			// pop.setPopupVisible(true);
			// pop.setHideOnMouseOut(false);
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// });
			//
			// customerOrSupplierRadioButton
			// .addValueChangeListener(new ValueChangeListener() {
			//
			// @Override
			// public void valueChange(ValueChangeEvent event) {
			// if ((Long) customerOrSupplierRadioButton.getValue() == 1) {
			// table.setColumnHeader(TBC_PARTICULARS,
			// getPropertyName("customer"));
			// } else if ((Long) customerOrSupplierRadioButton
			// .getValue() == 2) {
			// table.setColumnHeader(TBC_PARTICULARS,
			// getPropertyName("supplier"));
			// } else {
			// table.setColumnHeader(TBC_PARTICULARS,
			// getPropertyName("transportation"));
			// }
			//
			// }
			// });
			loadCustomerOrSupplierFields();

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						showReport();
					}
				}
			});

		
			customerOrSupplierRadioButton
					.addValueChangeListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
							
							if ((Integer) reportTypeRadioButton.getValue() == DETAIL_REPORT) {
								table.setColumnHeader(TBC_PARTICULARS,
										getPropertyName("bill_no"));
							} else {
								if ((Integer) customerOrSupplierRadioButton
										.getValue() == CUSTOMER) {
									table.setColumnHeader(TBC_PARTICULARS,
											getPropertyName("customer"));
								} else {
									table.setColumnHeader(TBC_PARTICULARS,
											getPropertyName("supplier"));
								}
							}
							loadCustomerOrSupplierFields();
						}
					});
			reportTypeRadioButton
					.addValueChangeListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
							customerOrSupplierSComboField.setValue(0);
							if ((Integer) reportTypeRadioButton.getValue() == SUMMARY_REPORT) {
								customerOrSupplierSComboField.setVisible(false);
								if ((Integer) customerOrSupplierRadioButton
										.getValue() == CUSTOMER) {
									table.setColumnHeader(TBC_PARTICULARS,
											getPropertyName("customer"));
								} else {
									table.setColumnHeader(TBC_PARTICULARS,
											getPropertyName("supplier"));
								}
								customerOrSupplierSComboField.setValue((long)0);
							} else {
								customerOrSupplierSComboField.setVisible(true);
								loadCustomerOrSupplierFields();
								table.setColumnHeader(TBC_PARTICULARS,
										getPropertyName("bill_no"));
							}
						}					
					});

			mainPanel.setContent(mainLayout);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadCustomerOrSupplierFields() {
		try{
			List list = new ArrayList();
			
			if ((Integer) customerOrSupplierRadioButton
					.getValue() == CUSTOMER) {
				customerOrSupplierSComboField
						.setCaption(getPropertyName("customer"));
				list.addAll(ledDao
						.getAllCustomers(getOfficeID()));
				list.add(0, new LedgerModel((long) 0,
						"---- Select -----"));
			} else {
				customerOrSupplierSComboField
						.setCaption(getPropertyName("supplier"));
				list.addAll(ledDao
						.getAllSuppliers(getOfficeID()));
				list.add(0, new LedgerModel((long) 0,
						"---- Select -----"));
			}
			
			customerOrSupplierSComboField.setContainerDataSource(SCollectionContainer.setList(list, "id"));
			customerOrSupplierSComboField.setItemCaptionPropertyId("name");
			customerOrSupplierSComboField.setValue((long)0);
		} catch (Exception e){
			e.printStackTrace();
		}							
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void showReport() {
		try {

			table.removeAllItems();

			if (isValid()) {

				List list = null;

				removeContainerProperties();

				table.setColumnHeader(
						TBC_BALANCE,
						getPropertyName("balacne_on")
								+ CommonUtil.formatDateToDDMMMYYYY(fromDateField.getValue()));
				int no_of_intervals = toInt(no_ofIntervalsTextField.getValue()) - 1;
				int intervalDays = toInt(intervalDaysTextField.getValue());

				table.setVisibleColumns(allColumns);
				StringBuffer dynamicHeader = new StringBuffer();
				
				Calendar toCal = Calendar.getInstance();
				toCal.setTime(fromDateField.getValue());
				toCal.add(Calendar.YEAR, 100);
				
				Date toDate = toCal.getTime();
				beanList = new ArrayList();
				boolean isCustomer;
				creditPeriodHashMap = new HashMap<Long, Integer>();
				
				if((Integer) customerOrSupplierRadioButton.getValue() == CUSTOMER){
					isCustomer = true;
					beanList.addAll(aegingReportDao.getSalesDetails((Long)customerOrSupplierSComboField.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
							CommonUtil.getSQLDateFromUtilDate(toDate), 
							(Long)officeSelect.getValue(),							
							""));				
					List<CustomerModel> customerList = customerDao.getAllActiveCustomers((Long)officeSelect.getValue());
					for(CustomerModel model : customerList){
						creditPeriodHashMap.put(model.getLedger().getId(), model.getMax_credit_period());
					}
				} else {					
					isCustomer = false;
					beanList.addAll(aegingReportDao.getPurchaseDetails((Long)customerOrSupplierSComboField.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
							CommonUtil.getSQLDateFromUtilDate(toDate), 
							(Long)officeSelect.getValue(),							
							""));	
					List<SupplierModel> supplierList = supplierDao.getAllActiveSuppliers((Long)officeSelect.getValue());
					for(SupplierModel model : supplierList){
						creditPeriodHashMap.put(model.getLedger().getId(), model.getCredit_period());
					}
				}
				
				
				
				reportBeanHashMap = new HashMap<Long, AegingReportBean>();
				
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(fromDateField.getValue());
				tempCal.add(Calendar.DATE, -1);
				Date tempDate = tempCal.getTime();
				int val = 0;		
				int intervalIndex = 0;
//				List<Double> periodWiseAmountList = null;;
//				AegingReportBean reportBean = null;
//				double amount;
		//		List<Double> periodWiseAmountList;
				for (int i = 0; i < no_of_intervals; i++) {
					val += intervalDays;
					
					dynamicHeader.delete(0, dynamicHeader.length());
					dynamicHeader.append(1 + (i * intervalDays) + "-" + val
							+ " " + getPropertyName("days"));
					
					
					
					table.addContainerProperty(dynamicHeader.toString(),
							Double.class, 0, dynamicHeader.toString(), null,
							Align.RIGHT);

					visibleColumnsList.add(dynamicHeader.toString());	
					tempCal.add(Calendar.DATE, intervalDays);
					tempDate = tempCal.getTime();
					System.out.println("\n\n"+intervalIndex+" ===== TEMP DATE ===== "+CommonUtil.formatDateToDDMMYYYY(tempDate));
					if(isCustomer){
						if((Integer)reportTypeRadioButton.getValue() == SUMMARY_REPORT){
							loadSalesSummaryDetails(tempDate, intervalIndex);		
						} else {
							loadSalesDetailReport(tempDate, intervalIndex);		
						}
					} else {
						if((Integer)reportTypeRadioButton.getValue() == SUMMARY_REPORT){
							loadPurchaseSummaryDetails(tempDate, intervalIndex);		
						} else {
							loadPurchaseDetailReport(tempDate, intervalIndex);		
						}
										
					}
					setZeroToFields(intervalIndex);
				
					intervalIndex++;
				}
				dynamicHeader.delete(0, dynamicHeader.length());
				dynamicHeader.append(getPropertyName("after") + " " + val + " "
						+ getPropertyName("days"));
				
				table.addContainerProperty(dynamicHeader.toString(),
						Double.class, 0, dynamicHeader.toString(), null,
						Align.RIGHT);
				visibleColumnsList.add(dynamicHeader.toString());
				
			//	System.out.println("============  visibleColumnsList = ===== "+visibleColumnsList);
				table.setVisibleColumns((Object[]) visibleColumnsList
									.toArray(new Object[visibleColumnsList
											.size()]));

				buttonLayout.removeComponent(excelExporter);
				excelExporter = new ExcelExporter(table);
				buttonLayout.addComponent(excelExporter);
				excelExporter.setCaption("Export to Excel");
				if(reportBeanHashMap.size() <= 0){
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				} else {
					
					setOpeningBalance(fromDateField.getValue(),intervalIndex);
					setDateAfterBalance(tempDate,intervalIndex);	
					
					tempCal.add(Calendar.DATE, intervalDays);
					tempDate = tempCal.getTime();
					
					System.out.println("===== FROM DATE == "+CommonUtil.formatDateToDDMMYYYY(fromDateField.getValue())+
							" === TO DATE == "+CommonUtil.formatDateToDDMMYYYY(tempDate));
					setUnAdjustedAmount(fromDateField.getValue(),tempDate);
					
					for(Long key : reportBeanHashMap.keySet()){
						AegingReportBean bean = reportBeanHashMap.get(key);
					//	bean.setClosingBalance(0);
						Object[] objs = new Object[visibleColumnsList.size()];
						objs[0] = (table.getItemIds().size() + 1);
						objs[1] = bean.getParticulars();
						objs[2] = bean.getUnAdjustAmount();
						objs[3] = bean.getOpeningBalance();
						for(int i = 0 ; i < bean.getPeriodWiseAmountList().size() ; i++){
							objs[i+4] = bean.getPeriodWiseAmountList().get(i);
						}
						objs[visibleColumnsList.size()-1] = bean.getClosingBalance();
						
						table.addItem(objs , table.getItemIds().size() + 1);
					}
				}

				// lst.clear();

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUnAdjustedAmount(Date fromDate, Date toDate) {

		
	//	double amount;
		try {
			List<AegingReportBean> list = new ArrayList<AegingReportBean>();
			if((Integer)customerOrSupplierRadioButton.getValue() == CUSTOMER){
				list = aegingReportDao.getUnAdjustedAmountOfCustomer((Long)customerOrSupplierSComboField.getValue(), 
						CommonUtil.getSQLDateFromUtilDate(fromDate), 
						CommonUtil.getSQLDateFromUtilDate(toDate),
						(Long)officeSelect.getValue(), 
						(Integer)reportTypeRadioButton.getValue());
			} else {
				list = aegingReportDao.getUnAdjustedAmountOfSupplier((Long)customerOrSupplierSComboField.getValue(), 
						CommonUtil.getSQLDateFromUtilDate(fromDate), 
						CommonUtil.getSQLDateFromUtilDate(toDate),
						(Long)officeSelect.getValue(), 
						(Integer)reportTypeRadioButton.getValue());
			}
			
			
			for(AegingReportBean bean : list){
				if(reportBeanHashMap.containsKey(bean.getId())){
					reportBeanHashMap.get(bean.getId()).setUnAdjustAmount(bean.getClosingBalance());
				} else {
					System.out.println(bean.getId()+" ==== NOT CONTAIN ===== "+bean.getClosingBalance() );
				}
			}
		//	setZeroToFields(intervalIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void loadSalesDetailReport(Date toDate, int intervalIndex) {
		List<Double> periodWiseAmountList;
		AegingReportBean reportBean;
		List tempList = new ArrayList();
		double amount;
		Iterator<SalesModel> itr = beanList.iterator();
	//	flag = false;
		Date salesDate;
		Calendar salesCal = Calendar.getInstance();
		while(itr.hasNext()){
			
			SalesModel model = itr.next();
			
			amount = (model.getAmount() + model.getDebit_note() 
					- model.getPayment_amount() - model.getCredit_note() 
					- model.getPaid_by_payment() - model.getExpenseCreditAmount());
			
			if(reportBeanHashMap.containsKey(model.getId())){
				//System.out.println(amount+" ===== REPORT BEAN ==== CONTAINS ====== "+model.getId());
				reportBean = reportBeanHashMap.get(model.getId());
			} else {
				//System.out.println(amount+"===== NOT REPORT BEAN ==== CONTAINS ====== "+model.getId());
				reportBean = new AegingReportBean();
				reportBean.setParticulars(model.getSales_number());
				reportBean.setOpeningBalance(0);
				reportBean.setClosingBalance(0);
				reportBean.setUnAdjustAmount(0);
			}
			salesCal.setTime(model.getDate());
			salesCal.add(Calendar.DATE, creditPeriodHashMap.get(model.getCustomer().getId()));
			salesDate = salesCal.getTime();
			
			//System.out.println("== CREDIT PER =="+creditPeriodHashMap.get(model.getCustomer().getId())+" == "
//					+model.getCustomer().getName()+" == "+ CommonUtil.formatDateToDDMMYYYY(salesDate)
//					+ " == "+CommonUtil.formatDateToDDMMYYYY(toDate)+" === "+ model.getAmount()+
//					" == "+salesDate.compareTo(toDate));
			if(salesDate.compareTo(toDate) < 0){
			//	flag = true;			
				
				if(reportBean.getPeriodWiseAmountList() == null){
					//System.out.println("======= reportBean.getPeriodWiseAmountList() == null ======");
					periodWiseAmountList = new ArrayList<Double>();
					periodWiseAmountList.add(amount);
				} else {
					
					periodWiseAmountList = reportBean.getPeriodWiseAmountList();
					//System.out.println((periodWiseAmountList.size()-1)+"======"+intervalIndex+"======= reportBean.getPeriodWiseAmountList() == not null ======");
					if(periodWiseAmountList.size()-1 == intervalIndex){
						//System.out.println("======"+amount+"===="+periodWiseAmountList.get(intervalIndex));
						periodWiseAmountList.set(intervalIndex, amount + periodWiseAmountList.get(intervalIndex));
					} else {
						periodWiseAmountList.add(amount);
					}
					
				}
				reportBean.setPeriodWiseAmountList(periodWiseAmountList);
				reportBeanHashMap.put(model.getId(), reportBean);
				
				tempList.add(model);
			} else {
				//flag = false;
				if(reportBeanHashMap.containsKey(model.getId())){
					break;
				}
				reportBeanHashMap.put(model.getId(), reportBean);
				intervalIndex ++;
				
			//	break;
			}
		}
		beanList.removeAll(tempList);
		//intervalIndex ++;
		
	}

	private void loadSalesSummaryDetails(Date toDate, int intervalIndex) {

		List<Double> periodWiseAmountList;
		AegingReportBean reportBean;
		List tempList = new ArrayList();
		double amount;
		Iterator<SalesModel> itr = beanList.iterator();
	//	flag = false;
		Date salesDate;
		Calendar salesCal = Calendar.getInstance();
		while(itr.hasNext()){
			
			SalesModel model = itr.next();
			
			amount = (model.getAmount() + model.getDebit_note() 
					- model.getPayment_amount() - model.getCredit_note() 
					- model.getPaid_by_payment() - model.getExpenseCreditAmount());
			
			if(reportBeanHashMap.containsKey(model.getCustomer().getId())){
				//System.out.println(amount+" ===== REPORT BEAN ==== CONTAINS ====== "+model.getCustomer().getId());
				reportBean = reportBeanHashMap.get(model.getCustomer().getId());
			} else {
				//System.out.println(amount+"===== NOT REPORT BEAN ==== CONTAINS ====== "+model.getCustomer().getId());
				reportBean = new AegingReportBean();
				reportBean.setParticulars(model.getCustomer().getName());
				reportBean.setOpeningBalance(0);
				reportBean.setClosingBalance(0);
				reportBean.setUnAdjustAmount(0);
			}
			salesCal.setTime(model.getDate());
			salesCal.add(Calendar.DATE, creditPeriodHashMap.get(model.getCustomer().getId()));
			salesDate = salesCal.getTime();
			
			//System.out.println("== CREDIT PER =="+creditPeriodHashMap.get(model.getCustomer().getId())+" == "
//					+model.getCustomer().getName()+" == "+ CommonUtil.formatDateToDDMMYYYY(salesDate)
//					+ " == "+CommonUtil.formatDateToDDMMYYYY(toDate)+" === "+ model.getAmount()+
//					" == "+salesDate.compareTo(toDate));
			if(salesDate.compareTo(toDate) < 0){
			//	flag = true;			
				
				if(reportBean.getPeriodWiseAmountList() == null){
					//System.out.println("======= reportBean.getPeriodWiseAmountList() == null ======");
					periodWiseAmountList = new ArrayList<Double>();
					periodWiseAmountList.add(amount);
				} else {
					
					periodWiseAmountList = reportBean.getPeriodWiseAmountList();
					//System.out.println((periodWiseAmountList.size()-1)+"======"+intervalIndex+"======= reportBean.getPeriodWiseAmountList() == not null ======");
					if(periodWiseAmountList.size()-1 == intervalIndex){
						//System.out.println("======"+amount+"===="+periodWiseAmountList.get(intervalIndex));
						periodWiseAmountList.set(intervalIndex, amount + periodWiseAmountList.get(intervalIndex));
					} else {
						periodWiseAmountList.add(amount);
					}
					
				}
				reportBean.setPeriodWiseAmountList(periodWiseAmountList);
				reportBeanHashMap.put(model.getCustomer().getId(), reportBean);
				
				tempList.add(model);
			} else {
				//flag = false;
				if(reportBeanHashMap.containsKey(model.getCustomer().getId())){
					break;
				}
				reportBeanHashMap.put(model.getCustomer().getId(), reportBean);
				intervalIndex ++;
				
			//	break;
			}
		}
		beanList.removeAll(tempList);
		//intervalIndex ++;
		
	
		
	}

	private void loadPurchaseDetailReport(Date toDate, int intervalIndex) {
		List<Double> periodWiseAmountList;
		AegingReportBean reportBean;
		List tempList = new ArrayList();
		double amount;
		Iterator<PurchaseModel> itr = beanList.iterator();
	//	flag = false;
		Date purchaseDate;
		Calendar purchaseCal = Calendar.getInstance();
		while(itr.hasNext()){
			
			PurchaseModel model = itr.next();
			
			amount = (model.getAmount() + model.getDebit_note() 
					- model.getPaymentAmount() - model.getCredit_note() 
					- model.getPaid_by_payment() - model.getExpenseCreditAmount());
			
			if(reportBeanHashMap.containsKey(model.getId())){
		//		System.out.println(amount+" ===== REPORT BEAN ==== CONTAINS ====== "+model.getSupplier().getId());
				reportBean = reportBeanHashMap.get(model.getId());
			} else {
		//		System.out.println(amount+"===== NOT REPORT BEAN ==== CONTAINS ====== "+model.getSupplier().getId());
				reportBean = new AegingReportBean();
				reportBean.setParticulars(model.getPurchase_no());
				reportBean.setOpeningBalance(0);
				reportBean.setClosingBalance(0);
				reportBean.setUnAdjustAmount(0);
			}
			purchaseCal.setTime(model.getDate());
			purchaseCal.add(Calendar.DATE, creditPeriodHashMap.get(model.getSupplier().getId()));
			purchaseDate = purchaseCal.getTime();
			
			//System.out.println("== CREDIT PER =="+creditPeriodHashMap.get(model.getSupplier().getId())+" == "
			//		+model.getSupplier().getName()+" == "+ CommonUtil.formatDateToDDMMYYYY(purchaseDate)
		//			+ " == "+CommonUtil.formatDateToDDMMYYYY(toDate)+" === "+ model.getAmount()+
		//			" == "+purchaseDate.compareTo(toDate));
			if(purchaseDate.compareTo(toDate) < 0){
			//	flag = true;			
				
				if(reportBean.getPeriodWiseAmountList() == null){//System.out.printlnrintln("======= reportBean.getPeriodWiseAmountList() == null ======");
					periodWiseAmountList = new ArrayList<Double>();
					periodWiseAmountList.add(amount);
				} else {
					
					periodWiseAmountList = reportBean.getPeriodWiseAmountList();
					//System.out.println((periodWiseAmountList.size()-1)+"======"+intervalIndex+"======= reportBean.getPeriodWiseAmountList() == not null ======");
					if(periodWiseAmountList.size()-1 == intervalIndex){
						//System.out.println("======"+amount+"===="+periodWiseAmountList.get(intervalIndex));
						periodWiseAmountList.set(intervalIndex, amount + periodWiseAmountList.get(intervalIndex));
					} else {
						periodWiseAmountList.add(amount);
					}
					
				}
				reportBean.setPeriodWiseAmountList(periodWiseAmountList);
				reportBeanHashMap.put(model.getId(), reportBean);
				
				tempList.add(model);
			} else {
				//flag = false;
				if(reportBeanHashMap.containsKey(model.getId())){
					break;
				}
				reportBeanHashMap.put(model.getId(), reportBean);
				intervalIndex ++;
				
			//	break;
			}
		}
		beanList.removeAll(tempList);
		//intervalIndex ++;
		
	}

	private void setOpeningBalance(Date fromDate, int intervalIndex) {
	//	List<Double> periodWiseAmountList;
		//	double amount;
			try {
				List<AegingReportBean> list = new ArrayList<AegingReportBean>();
				if((Integer)customerOrSupplierRadioButton.getValue() == CUSTOMER){
					list = aegingReportDao.getSalesOpeningBalance((Long)customerOrSupplierSComboField.getValue(), 
							CommonUtil.getSQLDateFromUtilDate(fromDate), 
										(Long)officeSelect.getValue(), (Integer)reportTypeRadioButton.getValue());
				} else {
					list = aegingReportDao.getPurchaseOpeningBalance((Long)customerOrSupplierSComboField.getValue(), 
							CommonUtil.getSQLDateFromUtilDate(fromDate), 
										(Long)officeSelect.getValue(), (Integer)reportTypeRadioButton.getValue());
				}
				
				
				for(AegingReportBean bean : list){
					if(reportBeanHashMap.containsKey(bean.getId())){
						reportBeanHashMap.get(bean.getId()).setOpeningBalance(bean.getClosingBalance());			
					}	
							
				}
			//	setZeroToFields(intervalIndex);
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}

	private void setDateAfterBalance(Date fromDate, int intervalIndex) {

		List<Double> periodWiseAmountList;
	//	double amount;
		try {
			List<AegingReportBean> list = new ArrayList<AegingReportBean>();
			if((Integer)customerOrSupplierRadioButton.getValue() == CUSTOMER){
				list = aegingReportDao.getBalanceAfterDateOfSales((Long)customerOrSupplierSComboField.getValue(), 
						CommonUtil.getSQLDateFromUtilDate(fromDate), 
						(Long)officeSelect.getValue(), (Integer)reportTypeRadioButton.getValue());
			} else {
				list = aegingReportDao.getBalanceAfterDateOfPurchase((Long)customerOrSupplierSComboField.getValue(), 
						CommonUtil.getSQLDateFromUtilDate(fromDate), 
						(Long)officeSelect.getValue(), (Integer)reportTypeRadioButton.getValue());
			}
			
			
			for(AegingReportBean bean : list){
				if(reportBeanHashMap.containsKey(bean.getId())){
					reportBeanHashMap.get(bean.getId()).setClosingBalance(bean.getClosingBalance());				
				}	
							
			}
		//	setZeroToFields(intervalIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void setZeroToFields(int intervalIndex) {
		List<Double> periodWiseAmountList;
		for(Long key : reportBeanHashMap.keySet()){
			
			
			if(reportBeanHashMap.get(key).getPeriodWiseAmountList() == null){
				//System.out.println(intervalIndex+"=======ZERO=== reportBean.getPeriodWiseAmountList() == null ======");
				periodWiseAmountList = new ArrayList<Double>();
				periodWiseAmountList.add(0.0);
			} else {
				
				periodWiseAmountList = reportBeanHashMap.get(key).getPeriodWiseAmountList();
				//System.out.println((periodWiseAmountList.size()-1)+"===ZERO======="+intervalIndex+"======= reportBean.getPeriodWiseAmountList() == not null ======");
				if(periodWiseAmountList.size()-1 == intervalIndex){
					//periodWiseAmountList.set(intervalIndex, amount + periodWiseAmountList.get(intervalIndex));
				} else {
					periodWiseAmountList.add(0.0);
				}
				
			}
			reportBeanHashMap.get(key).setPeriodWiseAmountList(periodWiseAmountList);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void loadPurchaseSummaryDetails(Date toDate, int intervalIndex) {
		List<Double> periodWiseAmountList;
		AegingReportBean reportBean;
		List tempList = new ArrayList();
		double amount;
		Iterator<PurchaseModel> itr = beanList.iterator();
	//	flag = false;
		Date purchaseDate;
		Calendar purchaseCal = Calendar.getInstance();
		while(itr.hasNext()){
			
			PurchaseModel model = itr.next();
			
			amount = (model.getAmount() + model.getDebit_note() 
					- model.getPaymentAmount() - model.getCredit_note() 
					- model.getPaid_by_payment() - model.getExpenseCreditAmount());
			
			if(reportBeanHashMap.containsKey(model.getSupplier().getId())){
			//	System.out.println(amount+" ===== REPORT BEAN ==== CONTAINS ====== "+model.getSupplier().getId());
				reportBean = reportBeanHashMap.get(model.getSupplier().getId());
			} else {
		//		System.out.println(amount+"===== NOT REPORT BEAN ==== CONTAINS ====== "+model.getSupplier().getId());
				reportBean = new AegingReportBean();
				reportBean.setParticulars(model.getSupplier().getName());
				reportBean.setOpeningBalance(0);
				reportBean.setClosingBalance(0);
				reportBean.setUnAdjustAmount(0);
			}
			purchaseCal.setTime(model.getDate());
			purchaseCal.add(Calendar.DATE, creditPeriodHashMap.get(model.getSupplier().getId()));
			purchaseDate = purchaseCal.getTime();
			
//			System.out.println("== CREDIT PER =="+creditPeriodHashMap.get(model.getSupplier().getId())+" == "
//					+model.getSupplier().getName()+" == "+ CommonUtil.formatDateToDDMMYYYY(purchaseDate)
//					+ " == "+CommonUtil.formatDateToDDMMYYYY(toDate)+" === "+ model.getAmount()+
//					" == "+purchaseDate.compareTo(toDate));
			if(purchaseDate.compareTo(toDate) < 0){
			//	flag = true;			
				
				if(reportBean.getPeriodWiseAmountList() == null){
				//	System.out.println("======= reportBean.getPeriodWiseAmountList() == null ======");
					periodWiseAmountList = new ArrayList<Double>();
					periodWiseAmountList.add(amount);
				} else {
					
					periodWiseAmountList = reportBean.getPeriodWiseAmountList();
				//	System.out.println((periodWiseAmountList.size()-1)+"======"+intervalIndex+"======= reportBean.getPeriodWiseAmountList() == not null ======");
					if(periodWiseAmountList.size()-1 == intervalIndex){
						System.out.println("======"+amount+"===="+periodWiseAmountList.get(intervalIndex));
						periodWiseAmountList.set(intervalIndex, amount + periodWiseAmountList.get(intervalIndex));
					} else {
						periodWiseAmountList.add(amount);
					}
					
				}
				reportBean.setPeriodWiseAmountList(periodWiseAmountList);
				reportBeanHashMap.put(model.getSupplier().getId(), reportBean);
				
				tempList.add(model);
			} else {
				//flag = false;
			//	periodWiseAmountList = reportBean.getPeriodWiseAmountList();
				if(reportBeanHashMap.containsKey(model.getSupplier().getId())){
					break;
				}
				reportBeanHashMap.put(model.getSupplier().getId(), reportBean);
				
				intervalIndex ++;
				
			//	break;
			}
		}
		beanList.removeAll(tempList);
		//intervalIndex ++;
		
	}

	@SuppressWarnings({ })
//	protected void generateReport() {
//		try {
//
//			if (isValid()) {
//
//				List lst = null;
//				List reportList = new ArrayList();
//				if (customerOrSupplierRadioButton.getValue()
//						.equals("Customers")) {
//
//					reportList = daoObj.getCustomerLedgerReport(CommonUtil
//							.getSQLDateFromUtilDate(fromDateField.getValue()),
//							toInt(intervalDaysTextField.getValue()),
//							toInt(no_ofIntervalsTextField.getValue()),
//							getOfficeID());
//
//					if (reportList != null && reportList.size() > 0) {
//						HashMap<String, Object> params = new HashMap<String, Object>();
//						params.put("FromDate", fromDateField.getValue().getValue().toString());
//						params.put("TODAY",
//								CommonUtil.formatDateToDDMMYYYY(new Date()));
//						params.put("PART_HEAD", "Customer");
//
//						params.put("LedgerName", "");
//						params.put("Balance", 0.0);
//						params.put("OpeningBalance", 0.0);
//						params.put("Office", officeSelect
//								.getItemCaption(officeSelect.getValue()));
//
//						report.setJrxmlFileName("ConsolidatedLedgerReport");
//						report.setReportFileName("Consolidated Ledger Report");
//						report.setReportTitle("Consolidated Ledger Report");
//						report.setReportSubTitle("Date  : "
//								+ CommonUtil.formatDateToCommonFormat(fromDateField.getValue()
//										.getValue()));
//						report.setIncludeHeader(true);
//						report.setReportType((Integer) reportTypeRadioButton
//								.getValue());
//						report.setOfficeName(officeSelect
//								.getItemCaption(officeSelect.getValue()));
//						report.createReport(reportList, params);
//
//						reportList.clear();
//						lst.clear();
//
//					} else {
//						SNotification.show(
//								getPropertyName("no_data_available"),
//								Type.WARNING_MESSAGE);
//					}
//				} else if (customerOrSupplierRadioButton.getValue().equals(
//						"Suppliers")) {
//					reportList = daoObj.getSupplierLedgerReport(CommonUtil
//							.getSQLDateFromUtilDate(fromDateField.getValue().getValue()),
//							toInt(intervalDaysTextField.getValue()),
//							toInt(no_ofIntervalsTextField.getValue()),
//							getOfficeID());
//
//					if (reportList != null && reportList.size() > 0) {
//						HashMap<String, Object> params = new HashMap<String, Object>();
//						params.put("FromDate", fromDateField.getValue().getValue().toString());
//						params.put("TODAY",
//								CommonUtil.formatDateToDDMMYYYY(new Date()));
//						params.put("PART_HEAD", "Supplier");
//						params.put("LedgerName", "");
//						params.put("Balance", 0.0);
//						params.put("OpeningBalance", 0.0);
//						params.put("Office", officeSelect
//								.getItemCaption(officeSelect.getValue()));
//
//						report.setJrxmlFileName("ConsolidatedLedgerReport");
//						report.setReportFileName("Consolidated Ledger Report");
//						report.setReportTitle("Consolidated Ledger Report");
//						report.setReportSubTitle("Date  : "
//								+ CommonUtil.formatDateToCommonFormat(fromDateField.getValue()
//										.getValue()));
//						report.setIncludeHeader(true);
//						report.setReportType((Integer) reportTypeRadioButton
//								.getValue());
//						report.setOfficeName(officeSelect
//								.getItemCaption(officeSelect.getValue()));
//						report.createReport(reportList, params);
//
//						reportList.clear();
//						lst.clear();
//
//					} else {
//						SNotification.show(
//								getPropertyName("no_data_available"),
//								Type.WARNING_MESSAGE);
//					}
//				} else {
//					reportList = daoObj.getTransportationReport(CommonUtil
//							.getSQLDateFromUtilDate(fromDateField.getValue().getValue()),
//							toInt(intervalDaysTextField.getValue()),
//							toInt(no_ofIntervalsTextField.getValue()),
//							getOfficeID());
//
//					if (reportList != null && reportList.size() > 0) {
//						HashMap<String, Object> params = new HashMap<String, Object>();
//						params.put("FromDate", fromDateField.getValue().getValue().toString());
//						params.put("TODAY",
//								CommonUtil.formatDateToDDMMYYYY(new Date()));
//						params.put("PART_HEAD", "Supplier");
//						params.put("LedgerName", "");
//						params.put("Balance", 0.0);
//						params.put("OpeningBalance", 0.0);
//						params.put("Office", officeSelect
//								.getItemCaption(officeSelect.getValue()));
//						// /*params.put("Organization", organizationSelect
//						// .getItemCaption(organizationSelect.getValue()));*/
//
//						report.setJrxmlFileName("ConsolidatedLedgerReportTransportation");
//						report.setReportFileName("Consolidated Ledger Report");
//						report.setReportTitle("Consolidated Ledger Report");
//						report.setReportSubTitle("Date  : "
//								+ CommonUtil.formatDateToCommonFormat(fromDateField.getValue()
//										.getValue()));
//						report.setIncludeHeader(true);
//						report.setReportType((Integer) reportTypeRadioButton
//								.getValue());
//						report.setOfficeName(officeSelect
//								.getItemCaption(officeSelect.getValue()));
//						report.createReport(reportList, params);
//
//						reportList.clear();
//						lst.clear();
//
//					} else {
//						SNotification.show(
//								getPropertyName("no_data_available"),
//								Type.WARNING_MESSAGE);
//					}
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (customerOrSupplierRadioButton.getValue() == null
				|| customerOrSupplierRadioButton.getValue().equals("")) {
			setRequiredError(customerOrSupplierRadioButton,
					getPropertyName("invalid_selection"), true);
			customerOrSupplierRadioButton.focus();
			ret = false;
		} else
			setRequiredError(customerOrSupplierRadioButton, null, false);

		if (fromDateField.getValue() == null || fromDateField.getValue().equals("")) {
			setRequiredError(fromDateField, getPropertyName("invalid_selection"),
					true);
			fromDateField.focus();
			ret = false;
		} else
			setRequiredError(fromDateField, null, false);

		try {
			if (toInt(intervalDaysTextField.getValue()) < 0) {
				setRequiredError(intervalDaysTextField,
						getPropertyName("invalid_data"), true);
				intervalDaysTextField.focus();
				ret = false;
			} else
				setRequiredError(intervalDaysTextField, null, false);
		} catch (Exception e) {
			setRequiredError(intervalDaysTextField,
					getPropertyName("invalid_data"), true);
			intervalDaysTextField.focus();
			ret = false;
			// TODO: handle exception
		}

		try {
			if (toInt(no_ofIntervalsTextField.getValue()) < 0
					|| toInt(no_ofIntervalsTextField.getValue()) > 10) {
				setRequiredError(no_ofIntervalsTextField,
						getPropertyName("invalid_data"), true);
				no_ofIntervalsTextField.focus();
				ret = false;
			} else
				setRequiredError(no_ofIntervalsTextField, null, false);
		} catch (Exception e) {
			setRequiredError(no_ofIntervalsTextField,
					getPropertyName("invalid_data"), true);
			no_ofIntervalsTextField.focus();
			ret = false;
			// TODO: handle exception
		}
		if((Integer) reportTypeRadioButton.getValue() == DETAIL_REPORT &&
				(Long)customerOrSupplierSComboField.getValue() == 0){
			setRequiredError(customerOrSupplierSComboField, getPropertyName("invalid_selection"), true);
			customerOrSupplierSComboField.focus();
			ret = false;
		} else {
			setRequiredError(customerOrSupplierSComboField,	null, false);
		}

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

	public void removeContainerProperties() {

		String refId;
		for (int i = 4; i < visibleColumnsList.size(); i++) {
			refId = visibleColumnsList.get(i);
			table.removeContainerProperty(refId);
			visibleColumnsList.remove(i);
			i--;
		}

	}

}
