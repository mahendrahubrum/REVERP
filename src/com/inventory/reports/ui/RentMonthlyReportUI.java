package com.inventory.reports.ui;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.rent.dao.RentDetailsDao;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
import com.inventory.rent.model.RentReturnItemDetailModel;
import com.inventory.reports.bean.RentItemReportBean;
import com.inventory.reports.dao.RentCustomerLedgerReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Sangeeth
 * 
 * WebSpark.
 *
 * Aug 27 2014
 */
public class RentMonthlyReportUI extends SparkLogic{

	private static final long serialVersionUID = 6563329586087389800L;
	private SComboField organizationCombo;
	SComboField officeCombo;
	InlineDateField fromDate,toDate;
	Calendar calendar,retCal,supCal;
	SComboField customerCombo;
	private SReportChoiceField reportchoiceField;
	private SButton generate;
	private SButton show;
	private Report report;
	static String TBC_ITEM = "Item";
	static String TBC_QUANTITY = "Quantity";
	static String TBC_SUPPLY = "Supply Date";
	static String TBC_RETURN = "Month End/Return Date";
	static String TBC_DAYS = "Days Used";
	static String TBC_RENT_RATE = "Rent Rate";
	static String TBC_RETURN_ITEM = "Returned Item";
	static String TBC_RETURN_AMOUNT = "Return Amount";
	static String TBC_TOTAL = "Total Rent";
	static String TBC_RDATE = "Return Date";
	
	
	OfficeDao offDao;
	CustomerDao custDao;
	STable entryTable;
	SHorizontalLayout horizontalLayout;
	SHorizontalLayout buttonLayout;
	RentCustomerLedgerReportDao reportDao;
	RentItemReportBean reportBean;
	SWindow popup;
	DateFormat df;
	STable childTable;
	RentDetailsDao rentDetailsDao;
	RentDetailsModel mastermdl;
	LedgerDao ledDao;
	
	Calendar issueCalendar, returnCalendar,fromCalendar;
	boolean issueLeap=false,returnLeap=false,currentLeap=false;
	int issueYear,issueMonth,issueDay,issueMaxdays,issueBalance,issueMonthBalance;
	int returnYear,returnMonth,returnDay, returnMaxDays,returnBalance,returnMonthBalance;
	int fromYear,fromMonth,fromDay,fromMaxdays,fromBalance;
	int mreturnYear,mreturnMonth,mreturnDay, mreturnMaxDays,mreturnBalance,mreturnMonthBalance;
	
	int currentYear,currentMonth,currentDay,currentMaxDays,currentBalanceDays;
	
	long supplyTime=0,returnTime=0,fromTime=0,finalTime=0,mreturnTime=0,currentTime=0,issueTime=0;
	long diffFrom=0;
	int leap=0;
	int diffDaysFrom=0;
	@Override
	public SPanel getGUI() {
		df= new SimpleDateFormat("yyyy-MM-dd");
		SPanel panel = new SPanel();
		calendar=Calendar.getInstance();
		retCal=Calendar.getInstance();
		supCal=Calendar.getInstance();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("1300");
		setHeight("500");
		layout.setSpacing(true);
		SVerticalLayout lay = new SVerticalLayout();
		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		generate = new SButton(getPropertyName("generate"));
		show = new SButton(getPropertyName("show"));

		ledDao = new LedgerDao();
		final STextField rentNo = new STextField();
		final STextField customer = new STextField();
		final STextField maxCreditPeriod = new STextField();
		final STextField shippingCharge = new STextField();
		final STextField netAmount = new STextField();
		final SDateField datefield = new SDateField();
		STextArea commentArea = new STextArea();
		final STextField paidAmnt = new STextField();
		SGridLayout masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(8);
		masterDetailsGrid.setSpacing(true);
		masterDetailsGrid.setStyleName("master_border");
		
		mastermdl = new RentDetailsModel();
		fromDate=new InlineDateField("From Month", CommonUtil.getCurrentSQLDate());
		fromDate.setResolution(Resolution.MONTH);
		
		toDate=new InlineDateField("To Month", CommonUtil.getCurrentSQLDate());
		toDate.setResolution(Resolution.MONTH);
		
		reportchoiceField = new SReportChoiceField(getPropertyName("export_to"));
		report = new Report(getLoginID());
		offDao = new OfficeDao();
		custDao = new CustomerDao();
		customerCombo = new SComboField(getPropertyName("customer"), 200);
		customerCombo.setInputPrompt("------------All-------------");
		entryTable = new STable();
		entryTable.setWidth("925");
		
		entryTable.addContainerProperty(TBC_ITEM, String.class, null,TBC_ITEM, null, null);
		entryTable.addContainerProperty(TBC_QUANTITY, Double.class, null,TBC_QUANTITY, null, null);
		entryTable.addContainerProperty(TBC_SUPPLY, String.class, null,TBC_SUPPLY, null, null);
		entryTable.addContainerProperty(TBC_RETURN, String.class, null,TBC_RETURN, null, null);
		entryTable.addContainerProperty(TBC_RDATE, String.class, null,TBC_RDATE, null, null);
		entryTable.addContainerProperty(TBC_DAYS, Double.class, null,TBC_DAYS, null, null);
		entryTable.addContainerProperty(TBC_RENT_RATE, Double.class, null,TBC_RENT_RATE, null, null);
		entryTable.addContainerProperty(TBC_RETURN_ITEM, Double.class, null,TBC_RETURN_ITEM, null, null);
		entryTable.addContainerProperty(TBC_RETURN_AMOUNT, Double.class, null,TBC_RETURN_AMOUNT, null, null);
		entryTable.addContainerProperty(TBC_TOTAL, Double.class, null,TBC_TOTAL, null, null);
		entryTable.setColumnExpandRatio(TBC_ITEM, 1);
		entryTable.setColumnExpandRatio(TBC_QUANTITY, (float)0.4);
		entryTable.setColumnExpandRatio(TBC_SUPPLY, (float)0.60);
		entryTable.setColumnExpandRatio(TBC_RETURN, (float)1.2);
		entryTable.setColumnExpandRatio(TBC_RDATE, (float)0.60);
		entryTable.setColumnExpandRatio(TBC_DAYS, (float)0.55);
		entryTable.setColumnExpandRatio(TBC_RENT_RATE, (float)0.60);
		entryTable.setColumnExpandRatio(TBC_RETURN_ITEM, (float)0.70);
		entryTable.setColumnExpandRatio(TBC_RETURN_AMOUNT, (float)0.70);
		entryTable.setColumnExpandRatio(TBC_TOTAL, (float)0.5);
		entryTable.setFooterVisible(true);
		
		entryTable.setColumnFooter(TBC_ITEM, " Total ");
		entryTable.setColumnFooter(TBC_RETURN_ITEM, "0.0");
		entryTable.setColumnFooter(TBC_RETURN_AMOUNT, "0.0");
		entryTable.setColumnFooter(TBC_TOTAL, "0.0");
		
		entryTable.setSelectable(true);
		horizontalLayout = new SHorizontalLayout();
		reportDao = new RentCustomerLedgerReportDao();
		reportBean = new RentItemReportBean();
		popup = new SWindow(getPropertyName("details"));
		popup.setHeight("450");
		popup.setWidth("850");
		popup.setModal(true);
		popup.center();
		rentDetailsDao = new RentDetailsDao();

		try {
			List organizationList = new ArrayList();
			organizationList
					.addAll(new OrganizationDao().getAllOrganizations());

			organizationCombo = new SComboField(
					getPropertyName("organization"), 200, organizationList,
					"id", "name");


			officeCombo = new SComboField(getPropertyName("office"), 200);
			organizationCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					entryTable.removeAllItems();
					try {

						officeList.addAll(offDao
								.getAllOfficesUnderOrg((Long) organizationCombo
										.getValue()));

						System.out.println(officeList.size());

						SCollectionContainer office = SCollectionContainer
								.setList(officeList, "id");
						officeCombo.setContainerDataSource(office);
						officeCombo.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			officeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					entryTable.removeAllItems();
					try {
						List customerlist = new ArrayList();
						List rentList = new ArrayList();
						customerlist.addAll(custDao
								.getAllActiveCustomerNamesWithLedgerID((Long) officeCombo
										.getValue()));
						SCollectionContainer office = SCollectionContainer
								.setList(customerlist, "id");
						customerCombo.setContainerDataSource(office);
						customerCombo.setItemCaptionPropertyId("name");
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			customerCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					entryTable.removeAllItems();

				}
			});

			generate.addClickListener(new ClickListener() 
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					customerCombo.setComponentError(null);
					fromDate.setComponentError(null);
					try{
						if (isValid()){
							boolean flag=false,valid=false;
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							customerCombo.setComponentError(null);
							Calendar fcal=Calendar.getInstance();
							fcal.setTime(fromDate.getValue());
							Calendar tcal=Calendar.getInstance();
							tcal.setTime(toDate.getValue());
							int sday,smonth,syear;
							int tday,tmonth,tyear;
							sday=1;
							smonth=fcal.get(Calendar.MONTH);
							syear=fcal.get(Calendar.YEAR);
							tday=tcal.getActualMaximum(Calendar.DAY_OF_MONTH);
							tmonth=tcal.get(Calendar.MONTH);
							tyear=tcal.get(Calendar.YEAR);
							Date parseTDate = format.parse(tyear+"-"+(tmonth+1)+"-"+tday);
							java.sql.Date dateEnd = new java.sql.Date(parseTDate.getTime());
							Date parseSDate = format.parse(syear+"-"+(smonth+1)+"-"+sday);
							java.sql.Date dateStart = new java.sql.Date(parseSDate.getTime());
							int diffmonth=calculateMonth(smonth, syear, tmonth, tyear);
							System.out.println("Difference "+diffmonth);
							int fy,fm,fd,fmd;
							Calendar calend=Calendar.getInstance();
							calend.setTime(dateStart);
							for(int s=0;s<diffmonth;s++){
								fromYear=calend.get(Calendar.YEAR);
								fromMonth=calend.get(Calendar.MONTH);
								fromDay=1;
								fromMaxdays=calend.getActualMaximum(Calendar.DAY_OF_MONTH);
								Date psd = format.parse(fromYear+"-"+(fromMonth+1)+"-"+fromDay);
								java.sql.Date dateFrom = new java.sql.Date(psd.getTime());
								Date pfd = format.parse(fromYear+"-"+(fromMonth+1)+"-"+fromMaxdays);
								java.sql.Date dateFinal = new java.sql.Date(pfd.getTime());
		//						System.out.println("Start Date "+dateFrom);
		//						System.out.println("End Date "+dateFinal);
								double rentRate=0,quantity=0,daysUsed=0,returnedQuantity=0,rentAmount=0,actualQuantity=0,daysUseed=0;
								double monthOpeningQuantity=0,monthTotalReturn=0,monthActualQuantity=0,previousReturnQuantity=0,itemReturn=0;
								long returnCount=0;
								String status=null;
								Date supplyDate=null,returnDate=null,issueDate=null,itemReturnDate=null,firstReturn;
								Date finalDate = null;
								List reportList = new ArrayList();
								List newReportList = new ArrayList();
								double tableReturn=0,tableAmout=0,tableRent=0;
								RentDetailsModel mdl;
								RentInventoryDetailsModel childmdl = new RentInventoryDetailsModel();
								RentReturnItemDetailModel returnmdl=null;
								List returnList=null;
								reportList.addAll(reportDao.getRentDetailsReport(
											(Long) organizationCombo.getValue(),
											(Long) officeCombo.getValue(),
											(Long) customerCombo.getValue()));
								if(reportList !=null && reportList.size() >0){
									Iterator itr = reportList.iterator();
									while (itr.hasNext()){
										mdl = (RentDetailsModel) itr.next();
										Long rentid = mdl.getRent_number();
										List lis = rentDetailsDao.getbudgetMaster(rentid);
										for (int m = 0; m < lis.size(); m++){
											mastermdl = (RentDetailsModel) lis.get(m);
											for (int n = 0; n < mastermdl.getInventory_details_list().size(); n++){
												flag=false;
												valid=false;
												childmdl = mastermdl.getInventory_details_list().get(n);
												double finalamnt = 0,calcualtedRent=0,totalRent=0;
												long rentno = 0,childId=0;
												rentno = mastermdl.getId();
												childId=childmdl.getId();
												rentRate=childmdl.getUnit_price();
												supplyDate=childmdl.getSupplied_date();
												quantity=childmdl.getQunatity();
												returnedQuantity=childmdl.getReturned_qty();
												rentAmount=rentDetailsDao.getRentdetails(rentno,childId);
												status=childmdl.getReturned_status();
												returnDate=reportDao.getReturnedDate(childId);
												if(returnDate!=null){
													if((((returnDate.getTime()-supplyDate.getTime())/(24*60*60*1000))+1)<31){
														flag=true;
													}
												}
												monthTotalReturn=rentDetailsDao.getMonthlyReturnedItems(dateFrom, dateFinal,childmdl.getId());
												previousReturnQuantity=rentDetailsDao.getPreviousReturnedItems(dateFrom, childmdl.getId());
												monthOpeningQuantity=quantity-previousReturnQuantity;
												monthActualQuantity=monthOpeningQuantity-monthTotalReturn;
												if(supplyDate!=null){
												supCal.setTime(df.parse(supplyDate.toString()));
													issueYear=supCal.get(Calendar.YEAR);
													issueMonth=supCal.get(Calendar.MONTH);
													issueDay=supCal.get(Calendar.DATE);
													supplyDate.getTime();
												}
												if(issueYear==fromYear){
													if(issueMonth==fromMonth){
														if(issueDay>fromDay){
															issueDate=supplyDate;
															if(flag){
																monthActualQuantity=0;
															}
														}
														else{
															issueDate=dateFrom;
														}
													}
													else if(issueMonth<fromMonth){
														issueDate=dateFrom;
													}
													else if(issueMonth>fromMonth){
														continue;
													}
												}
												else if(issueYear<fromYear){
													issueDate=dateFrom;
												}
												else{
													continue;
												}
												if(returnDate!=null){
													retCal.setTime(df.parse(returnDate.toString()));
													returnYear=retCal.get(Calendar.YEAR);
													returnMonth=retCal.get(Calendar.MONTH);
													returnDay=retCal.get(Calendar.DATE);
													returnTime=returnDate.getTime();
													
													if(returnYear==fromYear){
														if(returnMonth==fromMonth){
																if(returnDay<fromMaxdays){
																finalDate=returnDate;
															}
															else{
																finalDate=dateFinal;
															}
														}
														else if(returnMonth>fromMonth){
															finalDate=dateFinal;
														}
														else if(returnMonth<fromMonth){
															continue;
														}
													}
													else if(returnYear>fromYear){
														finalDate=dateFinal;
													}
													else{
														continue;
													}
												}
												else{
													finalDate=dateFinal;
												}
												daysUsed=Double.parseDouble(asString(Math.abs(issueDate.getDate()-finalDate.getDate())+1));
												returnList=rentDetailsDao.getRentItemReturndetail(childmdl.getId(), dateFrom, dateFinal);
												if(returnList!=null && returnList.size()!=0){
													int rcount=0;
													Iterator returnIterator=returnList.iterator();
													while(returnIterator.hasNext()){
														Calendar cal=Calendar.getInstance();
														returnmdl=(RentReturnItemDetailModel)returnIterator.next();
														itemReturn=returnmdl.getTotal_return();
														itemReturnDate=returnmdl.getReturn_date();
														cal.setTime(itemReturnDate);
														mreturnYear=cal.get(Calendar.YEAR);
														mreturnMonth=itemReturnDate.getMonth();
														mreturnDay=itemReturnDate.getDate();
														mreturnTime=itemReturnDate.getTime();
														calcualtedRent=updatedNetPriceCalculation(
																	rentRate, 
																	itemReturn, 
																	mreturnYear, 
																	mreturnMonth, 
																	mreturnDay, 
																	mreturnTime, 
																	supplyDate,flag);
														daysUseed=Double.parseDouble(asString(Math.abs(issueDate.getDate()-itemReturnDate.getDate())+1));
														if(rcount==0){
															totalRent=calcualtedRent+(monthActualQuantity/calculateDays(fromMonth)*rentRate*daysUsed);
														}
														else{
															totalRent=calcualtedRent;
														}
														reportBean = new RentItemReportBean(
																CommonUtil.formatDateToDDMMYYYY(issueDate),
																childmdl.getItem().getName(),
																CommonUtil.formatDateToDDMMYYYY(finalDate),
																totalRent,
																daysUseed,
																monthActualQuantity,
																rentRate,
																itemReturn,
																calcualtedRent,
																CommonUtil.formatDateToDDMMYYYY(itemReturnDate));
														newReportList.add(reportBean);
														rcount++;
													}
												}
												else{
													totalRent=calcualtedRent+(monthActualQuantity/calculateDays(fromMonth)*rentRate*daysUsed);
													reportBean = new RentItemReportBean(
															CommonUtil.formatDateToDDMMYYYY(issueDate),
															childmdl.getItem().getName(),
															CommonUtil.formatDateToDDMMYYYY(finalDate),
															totalRent,
															daysUsed,
															monthActualQuantity,
															rentRate,
															monthTotalReturn,
															calcualtedRent,
															"");
													newReportList.add(reportBean);
												}
													
												
												
//													daysUsed=Double.parseDouble(asString(Math.abs(issueDate.getDate()-finalDate.getDate())+1));
//													if(valid){
//													totalRent=calcualtedRent+(monthActualQuantity/calculateDays(fromMonth)*rentRate*daysUsed);
//													reportBean = new RentItemReportBean(
//															CommonUtil.formatDateToDDMMYYYY(issueDate),
//															childmdl.getItem().getName(),
//															CommonUtil.formatDateToDDMMYYYY(finalDate),
//															totalRent,
//															daysUsed,
//															monthActualQuantity,
//															rentRate,
//															monthTotalReturn,
//															calcualtedRent);
//													newReportList.add(reportBean);
												} 											
											}	
										}	
									}
									else{
											SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);	
									}
									if (newReportList != null && newReportList.size() > 0) 
									{
										HashMap<String, Object> params = new HashMap<String, Object>();
										params.put("FromDate", fromDate.getValue()
												.toString());
										params.put("ToDate", toDate.getValue()
												.toString());
										params.put("LedgerName", childmdl.getCustomer()
												.getName());
										params.put("Office", officeCombo.getValue());
										params.put("Organization", organizationCombo.getValue());
										report.setJrxmlFileName("MonthlyRentReport");
										report.setReportFileName("MonthlyRentReport");
										report.setReportTitle("Monthly Rent Report");
										String subHeader = "";
										subHeader="STATEMENT OF RENT FOR THE MONTH OF "+getMonth(fromMonth)+" "+fromYear; 
										report.setReportSubTitle(subHeader.toUpperCase());
										report.setIncludeHeader(true);
										report.setIncludeFooter(false);
										report.setReportType((Integer) reportchoiceField
												.getValue());
										report.setOfficeName(officeCombo
												.getItemCaption(officeCombo.getValue()));
										report.createReport(newReportList, params);
										reportList.clear();
									}
									else{ 
										SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
									}
									Thread.sleep(1000);
									calend.add(calendar.DATE, fromMaxdays);
								}
							}
					}
					catch(Exception e){
						SNotification.show("Error",Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

			show.addClickListener(new ClickListener() {
				@SuppressWarnings({ "unused", "rawtypes" })
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try{
						customerCombo.setComponentError(null);
						fromDate.setComponentError(null);
						if(isValid()){
							boolean flag=false;
							entryTable.removeAllItems();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							customerCombo.setComponentError(null);
							Calendar fcal=Calendar.getInstance();
							fcal.setTime(fromDate.getValue());
							Calendar tcal=Calendar.getInstance();
							tcal.setTime(toDate.getValue());
							int sday,smonth,syear;
							int tday,tmonth,tyear;
							sday=1;
							smonth=fcal.get(Calendar.MONTH);
							syear=fcal.get(Calendar.YEAR);
							tday=tcal.getActualMaximum(Calendar.DAY_OF_MONTH);
							tmonth=tcal.get(Calendar.MONTH);
							tyear=tcal.get(Calendar.YEAR);
							Date parseTDate = format.parse(tyear+"-"+(tmonth+1)+"-"+tday);
							java.sql.Date dateEnd = new java.sql.Date(parseTDate.getTime());
							Date parseSDate = format.parse(syear+"-"+(smonth+1)+"-"+sday);
							java.sql.Date dateStart = new java.sql.Date(parseSDate.getTime());
							int diffmonth=calculateMonth(smonth, syear, tmonth, tyear);
							int fy,fm,fd,fmd;
							Calendar calend=Calendar.getInstance();
							calend.setTime(dateStart);
							for(int s=0;s<diffmonth;s++){
								fromYear=calend.get(Calendar.YEAR);
								fromMonth=calend.get(Calendar.MONTH);
								fromDay=1;
								fromMaxdays=calend.getActualMaximum(Calendar.DAY_OF_MONTH);
								Date psd = format.parse(fromYear+"-"+(fromMonth+1)+"-"+fromDay);
								java.sql.Date dateFrom = new java.sql.Date(psd.getTime());
								Date pfd = format.parse(fromYear+"-"+(fromMonth+1)+"-"+fromMaxdays);
								java.sql.Date dateFinal = new java.sql.Date(pfd.getTime());
		//						System.out.println("Start Date "+dateFrom);
		//						System.out.println("End Date "+dateFinal);
								double rentRate=0,quantity=0,daysUsed=0,returnedQuantity=0,rentAmount=0,actualQuantity=0,daysUseed=0;
								double monthOpeningQuantity=0,monthTotalReturn=0,monthActualQuantity=0,previousReturnQuantity=0,itemReturn=0;
								long returnCount=0;
								String status=null;
								Date supplyDate=null,returnDate=null,issueDate=null,itemReturnDate=null,firstReturn=null;
								Date finalDate = null;
								List reportList = new ArrayList();
								List newReportList = new ArrayList();
								double tableReturn=0,tableAmout=0,tableRent=0;
								entryTable.setVisibleColumns(new String[] { TBC_ITEM,
										TBC_QUANTITY, TBC_SUPPLY, TBC_RETURN,
										TBC_DAYS, TBC_RENT_RATE,TBC_RETURN_ITEM,TBC_RETURN_AMOUNT, TBC_TOTAL});
								RentDetailsModel mdl;
								RentInventoryDetailsModel childmdl = new RentInventoryDetailsModel();
								RentReturnItemDetailModel returnmdl=null;
								List returnList=null;
								reportList.addAll(reportDao.getRentDetailsReport(
										(Long) organizationCombo.getValue(),
										(Long) officeCombo.getValue(),
										(Long) customerCombo.getValue()));
								if(reportList !=null && reportList.size() >0){
									Iterator itr = reportList.iterator();
										while (itr.hasNext()){
										mdl = (RentDetailsModel) itr.next();
										Long rentid = mdl.getRent_number();
										List lis = rentDetailsDao.getbudgetMaster(rentid);
										for (int m = 0; m < lis.size(); m++){
											mastermdl = (RentDetailsModel) lis.get(m);
											for (int n = 0; n < mastermdl.getInventory_details_list().size(); n++){
												flag=false;
												childmdl = mastermdl.getInventory_details_list().get(n);
												double finalamnt = 0,calcualtedRent=0,totalRent=0;
												long rentno = 0,childId=0;
												rentno = mastermdl.getId();
												childId=childmdl.getId();
												rentRate=childmdl.getUnit_price();
												supplyDate=childmdl.getSupplied_date();
												quantity=childmdl.getQunatity();
												returnedQuantity=childmdl.getReturned_qty();
												rentAmount=rentDetailsDao.getRentdetails(rentno,childId);
												status=childmdl.getReturned_status();
												returnDate=reportDao.getReturnedDate(childId);
												if(returnDate!=null){
													if((((returnDate.getTime()-supplyDate.getTime())/(24*60*60*1000))+1)<31){
														flag=true;
													}
												}
												monthTotalReturn=rentDetailsDao.getMonthlyReturnedItems(dateFrom, dateFinal,childmdl.getId());
												returnCount=rentDetailsDao.getMonthlyReturnCount(dateFrom, dateFinal, childmdl.getId());
												previousReturnQuantity=rentDetailsDao.getPreviousReturnedItems(dateFrom, childmdl.getId());
												monthOpeningQuantity=quantity-previousReturnQuantity;
												monthActualQuantity=monthOpeningQuantity-monthTotalReturn;
												if(supplyDate!=null){
													supCal.setTime(df.parse(supplyDate.toString()));
													issueYear=supCal.get(Calendar.YEAR);
													issueMonth=supCal.get(Calendar.MONTH);
													issueDay=supCal.get(Calendar.DATE);
													supplyDate.getTime();
												}
												if(issueYear==fromYear){
													if(issueMonth==fromMonth){
														if(issueDay>fromDay){
																issueDate=supplyDate;
																if(flag){
																	monthActualQuantity=0;
																}
														}
														else{
															issueDate=dateFrom;
														}
													}
													else if(issueMonth<fromMonth){
														issueDate=dateFrom;
													}
													else if(issueMonth>fromMonth){
														continue;
													}
												}
												else if(issueYear<fromYear){
													issueDate=dateFrom;
												}
												else{
													continue;
												}
												if(returnDate!=null){
													retCal.setTime(df.parse(returnDate.toString()));
													returnYear=retCal.get(Calendar.YEAR);
													returnMonth=retCal.get(Calendar.MONTH);
													returnDay=retCal.get(Calendar.DATE);
													returnTime=returnDate.getTime();
													if(returnYear==fromYear){
														if(returnMonth==fromMonth){
															if(returnDay<fromMaxdays){
																finalDate=returnDate;
															}
															else{
																finalDate=dateFinal;
															}
														}
														else if(returnMonth>fromMonth){
															finalDate=dateFinal;
														}
														else if(returnMonth<fromMonth){
															continue;
														}
													}
													else if(returnYear>fromYear){
														finalDate=dateFinal;
													}
													else{
														continue;
													}
												}
												else{
													finalDate=dateFinal;
												}
												daysUsed=Double.parseDouble(asString(Math.abs(issueDate.getDate()-finalDate.getDate())+1));
												returnList=rentDetailsDao.getRentItemReturndetail(childmdl.getId(), dateFrom, dateFinal);
												entryTable.setVisibleColumns(new String[] {TBC_ITEM,TBC_QUANTITY, TBC_SUPPLY, TBC_RETURN,TBC_RDATE,
														TBC_DAYS, TBC_RENT_RATE,TBC_RETURN_ITEM,TBC_RETURN_AMOUNT, TBC_TOTAL});
												if(returnList!=null && returnList.size()!=0){
													int rcount=0;
													Iterator returnIterator=returnList.iterator();
													while(returnIterator.hasNext()){
														Calendar cal=Calendar.getInstance();
														returnmdl=(RentReturnItemDetailModel)returnIterator.next();
														itemReturn=returnmdl.getTotal_return();
														itemReturnDate=returnmdl.getReturn_date();
														cal.setTime(itemReturnDate);
														mreturnYear=cal.get(Calendar.YEAR);
														mreturnMonth=itemReturnDate.getMonth();
														mreturnDay=itemReturnDate.getDate();
														mreturnTime=itemReturnDate.getTime();
														calcualtedRent=updatedNetPriceCalculation(
															rentRate, 
															itemReturn, 
															mreturnYear, 
															mreturnMonth, 
															mreturnDay, 
															mreturnTime, 
															supplyDate,flag);
														daysUseed=Double.parseDouble(asString(Math.abs(issueDate.getDate()-itemReturnDate.getDate())+1));
														if(rcount==0){
															totalRent=calcualtedRent+(monthActualQuantity/calculateDays(fromMonth)*rentRate*daysUsed);
														}
														else{
															totalRent=calcualtedRent;
														}
														
														entryTable.addItem(
																new Object[] 
																		{
																		childmdl.getItem().getName(),
																		monthOpeningQuantity,
																		CommonUtil.formatDateToDDMMYYYY(issueDate),
																		CommonUtil.formatDateToDDMMYYYY(finalDate),
																		CommonUtil.formatDateToDDMMYYYY(itemReturnDate),
																		daysUseed,
																		rentRate,
																		itemReturn,
																		calcualtedRent,
																		totalRent
														},entryTable.getItemIds().size() + 1);
														rcount++;
													}
												}
												else{
													totalRent=calcualtedRent+(monthActualQuantity/calculateDays(fromMonth)*rentRate*daysUsed);
													entryTable.addItem(
															new Object[] 
																	{
																	childmdl.getItem().getName(),
																	monthOpeningQuantity,
																	CommonUtil.formatDateToDDMMYYYY(issueDate),
																	CommonUtil.formatDateToDDMMYYYY(finalDate),
																	"",
																	daysUsed,
																	rentRate,
																	monthTotalReturn,
																	calcualtedRent,
																	totalRent
													},entryTable.getItemIds().size() + 1);
												}
													
												} 
											}	
										}	
										List liis = (List) entryTable.getItemIds();
										for (int i = 0; i < liis.size(); i++){ 
											Item item;
											item = entryTable.getItem(liis.get(i));
											tableReturn += toDouble(item.getItemProperty(TBC_RETURN_ITEM).getValue().toString());
											tableAmout+=toDouble(item.getItemProperty(TBC_RETURN_AMOUNT).getValue().toString());
											tableRent+=toDouble(item.getItemProperty(TBC_TOTAL).getValue().toString());
										}
										entryTable.setColumnFooter(TBC_RETURN_ITEM, asString(roundNumber(tableReturn)));
										entryTable.setColumnFooter(TBC_RETURN_AMOUNT, asString(roundNumber(tableAmout)));
										entryTable.setColumnFooter(TBC_TOTAL,asString(roundNumber(tableRent)));
									}
									else{
											SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);	
									}
									flag=false;
									calend.add(calendar.DATE, fromMaxdays);
								}
						}
					}
					catch(Exception e){
						SNotification.show("Error",Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		layout.addComponent(organizationCombo);
		layout.addComponent(officeCombo);
		layout.addComponent(customerCombo);
		layout.addComponent(fromDate);
		layout.addComponent(toDate);
		layout.addComponent(reportchoiceField);
		layout.addComponent(buttonLayout);
		buttonLayout.addComponent(generate);
		buttonLayout.addComponent(show);

		horizontalLayout.addComponent(layout);
		horizontalLayout.addComponent(entryTable);
		organizationCombo.setValue(getOrganizationID());
		officeCombo.setValue(getOfficeID());
		customerCombo.setInputPrompt("-----------Select Customer-----------");
		panel.setContent(horizontalLayout);

		return panel;
	}

	@Override
	public Boolean isValid() {
		boolean valid=true;
		if (customerCombo.getValue() == null || customerCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(customerCombo, "Invalid Selection", true);
		}
		else{
			setRequiredError(customerCombo, null, false);
		}
		if(fromDate.getValue().getTime()>toDate.getValue().getTime()){
			valid=false;
			setRequiredError(fromDate, "Invalid Selection", true);
		}
		else{
			setRequiredError(fromDate, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	String getMonth(int m) 
	{
	    String month = null;
	    DateFormatSymbols dfs = new DateFormatSymbols();
	    String[] months = dfs.getMonths();
	    if (m >= 0 && m <= 11 ) {
	        month = months[m];
	    }
	    return month;
	}
	
	public int calculateDays(int mon)
	{
		int days=0,month=0;
		month=mon;
		switch(month)
		{
			case 0:	days=31;
					break;
			case 1:	days=leapYear(leap);
					break;
			case 2:	days=31;
					break;
			case 3:	days=30;
					break;
			case 4:	days=31;
					break;
			case 5:	days=30;
					break;
			case 6:	days=31;
					break;
			case 7:	days=31;
					break;
			case 8:	days=30;
					break;
			case 9:	days=31;
					break;
			case 10: days=30;
					 break;
			case 11: days=31;
					 break;
			
		}
		return days;
	}
	
	public int leapYear(int flag)
	{
		int days=0;
		int notice=flag;
		switch(notice)
		{
		case 0: days=28;
				break;
		case 1: days=29;
				break;
		}
		return days;
	}

	public void checkLeap()
	{
		if(issueCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365)
		{
			issueLeap=true;
		}
		else
		{
			issueLeap=false;
		}
		if(returnCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365)
		{
			returnLeap=true;
		}
		else
		{
			returnLeap=false;
		}
		if(calendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365)
		{
			currentLeap=true;
		}
		else
		{
			currentLeap=false;
		}
	}
	
	public double updatedNetPriceCalculation(double rentRate,double quantity,int currentYear,int currentMonth,int currentDay,long time,Date supplyDate,Boolean flag)
	{
		double netPrice=0;
		int diffDays=0,diffDay=0,diffYear=0,diffMonth=0,i,j,k;
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(supplyDate);
		issueCalendar = new GregorianCalendar(supplyDate.getYear(),supplyDate.getMonth(),supplyDate.getDate());
		issueTime=supplyDate.getTime();		
		issueYear=cal.get(Calendar.YEAR);
		issueMonth=supplyDate.getMonth();
		issueDay=supplyDate.getDate();
		issueMaxdays=issueCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int issueBalanceDays = (issueMaxdays-issueDay)+1;
		currentTime=time;
		currentYear=currentYear;
		currentMonth=currentMonth;
		currentDay=currentDay;
		returnCalendar = new GregorianCalendar(currentYear, currentMonth, currentDay);
		currentMaxDays=returnCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		currentBalanceDays=Math.abs(1-currentDay)+1;
		long diffTime = currentTime-issueTime;					
		diffDays=(int) (diffTime/(24*60*60*1000))+1;
		System.out.println("Difference in Days "+diffDays);
		if (issueYear == currentYear){ 
			if (issueMonth == currentMonth){ 
				if(diffDays<=31){
					netPrice=roundNumber((rentRate*quantity));
					System.out.println("Same Year Same Month Net Price = "+netPrice);
				}
			}
			else if (issueMonth != currentMonth){
				if(diffDays>31){
					netPrice=roundNumber((rentRate/calculateDays(currentMonth)*quantity*currentDay));
					System.out.println("Same Year Different Month Net Price = "+netPrice);
				}
				else{
					netPrice=(rentRate*quantity);
				}
			}					
		} 
		else{
			if(diffDays>31){
				netPrice=roundNumber((rentRate/calculateDays(currentMonth)*quantity*currentDay));
				System.out.println("Different Year Some Month Net Price = "+netPrice);
			}
			else{
				netPrice=roundNumber((rentRate*quantity));
				System.out.println("Different Year Net Price = "+netPrice);
			}
		}
		return roundNumber(netPrice);
	}
	
	public int calculateMonth(int smonth,int syear,int tmonth,int tyear){
		int diff=0,diffyear=0;
		try{
			if(syear==tyear){
				diff=(tmonth-smonth)+1;
			}
			else{
				diffyear=tyear-syear;
				if(diffyear>1){
					diff=(13-smonth)+(tmonth)+((diffyear-1)*12);
				}
				else{
					diff=(13-smonth)+(tmonth);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diff;
	}
	
}
