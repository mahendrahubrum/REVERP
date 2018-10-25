package com.inventory.reports.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.haijian.ExcelExporter;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.rent.dao.RentDetailsDao;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
import com.inventory.rent.model.RentPaymentModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.bean.TimewisereportBean;
import com.inventory.reports.dao.IntervalReportDao;
import com.inventory.reports.dao.RentCustomerLedgerReportDao;
import com.vaadin.data.Property;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Sangeeth
 * 
 *         WebSpark.
 * 
 *         Sep 1, 2014
 */
public class RentIntervalReport extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2590057858075912580L;

	Calendar calendar;
	int leap=0;
	boolean issueLeap=false,returnLeap=false,currentLeap=false;
	long currentTime=0,issueTime=0,returnTime=0,diffTime=0;
	int issueYear,issueMonth,issueDay,issueMaxDays,issueBalanceDays;
	int currentYear,currentMonth,currentDay,currentMaxDays,currentBalanceDays;
	Calendar issueCalendar, returnCalendar;
	
	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	SComboField rentidCombo;
	private Report report;

	private IntervalReportDao daoObj;
	private RentCustomerLedgerReportDao rentdao;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate;
	RentDetailsDao rentDao;
	private SComboField organizationCombo;
	private SComboField officeCombo;
	private SComboField customerCombo;
	private SRadioButton customerOrSupplier;

	STextField intervalDays, no_ofIntervals;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_PARTICULARS = "Particulars";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;
	CustomerDao custDao;

	ArrayList<String> visibleColumnsList;

	ExcelExporter excelExporter;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {
			rentDao=new RentDetailsDao();
			intervalDays = new STextField(getPropertyName("interval_days"),
					150, "15");
			no_ofIntervals = new STextField(getPropertyName("no_intervals"),
					150, "5");

			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();
			custDao = new CustomerDao();
			allColumns = new String[] { TBC_SN, TBC_PARTICULARS, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS,
					TBC_BALANCE };

			visibleColumnsList = new ArrayList<String>(Arrays.asList(TBC_SN,
					TBC_PARTICULARS, TBC_BALANCE));

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1200, 500);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,
					getPropertyName("particulars"), null, Align.CENTER);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.CENTER);

			table.setColumnWidth(TBC_SN, 20);
			table.setColumnWidth(TBC_BALANCE, 120);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("820");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

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
					table.removeAllItems();
					try {

						officeList.addAll(ofcDao
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
					table.removeAllItems();
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

			customerCombo = new SComboField(getPropertyName("customer"), 200);
			customerCombo.setInputPrompt("------------All-------------");
			customerCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					try {
						table.removeAllItems();

						List rentList = new ArrayList();
						if (officeCombo.getValue() == null
								|| (Long) officeCombo.getValue() == 0) {
							if (customerCombo.getValue() == null
									|| (Long) customerCombo.getValue() == 0) {

								rentList.addAll(new RentDetailsDao()
										.getActiveRentunderCustomers((Long) organizationCombo
												.getValue()));

							}

							else {

								rentList.addAll(new RentDetailsDao()
										.getActiveRentundereachCustomerwithnotreturnedqty((Long) customerCombo
												.getValue()));

							}
						} else {
							if (customerCombo.getValue() == null
									|| (Long) customerCombo.getValue() == 0) {

								rentList.addAll(new RentDetailsDao()
										.getActiveRentunderofficeandCustomers(
												(Long) organizationCombo
														.getValue(),
												(Long) officeCombo.getValue()));

							} else {

								rentList.addAll(new RentDetailsDao()
										.getActiveRentundereachCustomerwithnotreturnedqty((Long) customerCombo
												.getValue()));

							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new IntervalReportDao();

			rentdao = new RentCustomerLedgerReportDao();
			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), new Date(new Date().getTime()));

			formLayout.addComponent(organizationCombo);
			organizationCombo.setValue(getOrganizationID());
			formLayout.addComponent(officeCombo);
			officeCombo.setValue(getOfficeID());
			formLayout.addComponent(customerCombo);
			// formLayout.addComponent(customerOrSupplier);

			formLayout.addComponent(fromDate);
			formLayout.addComponent(intervalDays);
			formLayout.addComponent(no_ofIntervals);
			// formLayout.addComponent(reportType);

			reportType.setValue(0);

			excelExporter = new ExcelExporter(table);
			excelExporter.setCaption(getPropertyName("export_excel"));

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(showButton);
			buttonLayout.addComponent(excelExporter);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						if (customerCombo.getValue() != null) {
							showReport();
						} else {
							SNotification.show("Select Customer",
									Type.WARNING_MESSAGE);
						}
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

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();

			if (isValid()) 
			{
				Calendar cal=Calendar.getInstance();
				cal.setTime(fromDate.getValue());
				int afterDay=0,diffDay=0;
				issueCalendar = new GregorianCalendar(fromDate.getValue().getYear(), fromDate.getValue().getMonth(), fromDate.getValue().getDate());
				currentYear=cal.get(Calendar.YEAR);
				currentTime=fromDate.getValue().getTime();
				currentMonth=fromDate.getValue().getMonth();
				currentDay=fromDate.getValue().getDate();
				currentDay=fromDate.getValue().getDate();
				currentMaxDays=issueCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				currentBalanceDays=Math.abs(1-currentDay)+1;
				
				List lst = null;
				List list=null;
				List<RentInventoryDetailsModel> detailList=null;
				Date returnedDate;
				removeContainerProperties();
				table.setColumnHeader(TBC_BALANCE,"Balacne on "	+ CommonUtil.formatDateToDDMMMYYYY(fromDate.getValue()));
				
				list=rentdao.getRentDetails(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), (Long) customerCombo.getValue());
				
				if(list.size()!=0)
				{
					Iterator itr=list.iterator();
					RentDetailsModel mdl;
					RentInventoryDetailsModel detailmdl;
					List beanMainList=new ArrayList();
					TimewisereportBean bean;
					table.setVisibleColumns(allColumns);
					int count=0;
					Calendar calendar=Calendar.getInstance();
					Calendar fromCalender=Calendar.getInstance();
					List beanList;
					while(itr.hasNext())
					{
						double amount=0;
						double rentAmount=0,actualQuantity=0,returnedQuantity=0,quantity=0,rentRate=0,calculatedAmount=0,paidAmount=0,taxAmount=0,balanceAmount=0,netPrice=0;
						long tax=0;
						Date supplyDate = null;
						mdl=(RentDetailsModel)itr.next();
						detailList=mdl.getInventory_details_list();
						bean=new TimewisereportBean();
						bean.setParticulars(mdl.getRent_number()+"");
						beanList=new ArrayList();
						beanMainList.add(bean);
						bean.setSubList(beanList);
						for (int k = 0; k < detailList.size(); k++)
						{	
							detailmdl=detailList.get(k);
							quantity=detailmdl.getQunatity();
							returnedQuantity=detailmdl.getReturned_qty();
							actualQuantity=quantity-returnedQuantity;
							rentRate=detailmdl.getUnit_price();
							supplyDate=detailmdl.getSupplied_date();
							rentAmount=rentDao.getRentdetails(mdl.getId(),detailmdl.getId());
							System.out.println("Rent Amount "+rentAmount);
							netPrice+=(rentRate*quantity);
							amount+=netPriceCalculation(rentRate, quantity, supplyDate);
							bean.setAmount(roundNumber(amount));
						}
						System.out.println("Inital Net Amount "+netPrice);
						calendar.setTime(fromDate.getValue());
						for (int i = 1; i <=toInt(no_ofIntervals.getValue()); i++) 
						{
							double netprice=0;
							calendar.add(Calendar.DATE,Integer.parseInt(intervalDays.getValue()));
							netprice=netPrice(netPrice,1, supplyDate,calendar.getTime());
							System.out.println("Net Price "+netprice);
							beanList.add(netprice);
						}
						count++;
					}
					Iterator mainIter=beanMainList.iterator();
					TimewisereportBean repBean;
					int ct=0;
					int val = 0;
					while (mainIter.hasNext()) {
						repBean = (TimewisereportBean) mainIter.next();
						if(ct==0)
						{
							for (int i = 0; i < toInt(no_ofIntervals.getValue()); i++) 
							{
								val += toInt(intervalDays.getValue());
								table.addContainerProperty("After " + val+ " Days", Double.class, 0 , "After "+ val + " Days", null, Align.RIGHT);
								visibleColumnsList.add("After " + val + " Days");
							}
							table.setVisibleColumns((String[]) visibleColumnsList.toArray(new String[visibleColumnsList.size()]));
						}
						Object[] objs = new Object[visibleColumnsList.size()];
						objs[0] = ct + 1;
						objs[1] = repBean.getParticulars();
						objs[2] = repBean.getAmount();
						for (int i = 3; i < visibleColumnsList.size(); i++) 
						{
							objs[i] = repBean.getSubList().get(i - 3);
						}
						table.addItem(objs, ct);
						ct++;
					}
				}
				buttonLayout.removeComponent(excelExporter);
				excelExporter = new ExcelExporter(table);
				buttonLayout.addComponent(excelExporter);
				excelExporter.setCaption("Export to Excel");
			} 
			else
			{
				SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	protected void generateReport() {
		try {

			if (isValid()) {

				List lst = null;
				List reportList = new ArrayList();

				lst = rentdao.getCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						toInt(intervalDays.getValue()),
						toInt(no_ofIntervals.getValue()), getOfficeID(),
						(Long) customerCombo.getValue());

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("TODAY",
							CommonUtil.formatDateToDDMMYYYY(new Date()));
					params.put("PART_HEAD", "Customer");

					params.put("LedgerName", "");
					params.put("Balance", 0.0);
					params.put("OpeningBalance", 0.0);
					params.put("Office",
							officeCombo.getItemCaption(officeCombo.getValue()));
					params.put("Organization", organizationCombo
							.getItemCaption(organizationCombo.getValue()));

					report.setJrxmlFileName("ConsolidatedLedgerReport");
					report.setReportFileName("Consolidated Ledger Report");
					report.setReportTitle("Consolidated Ledger Report");
					report.setReportSubTitle("Date  : "+ CommonUtil.formatDateToCommonFormat(fromDate.getValue()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeCombo.getItemCaption(officeCombo
							.getValue()));
					report.createReport(reportList, params);

					reportList.clear();
					lst.clear();

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

		try {
			if (toInt(intervalDays.getValue()) <= 0) {
				setRequiredError(intervalDays, getPropertyName("invalid_data"),
						true);
				intervalDays.focus();
				ret = false;
			} else
				setRequiredError(intervalDays, null, false);
		} catch (Exception e) {
			setRequiredError(intervalDays, getPropertyName("invalid_data"),
					true);
			intervalDays.focus();
			ret = false;
		}

		try {
			if (toInt(no_ofIntervals.getValue()) < 0
					|| toInt(no_ofIntervals.getValue()) > 10) {
				setRequiredError(no_ofIntervals,
						getPropertyName("invalid_data"), true);
				no_ofIntervals.focus();
				ret = false;
			} else
				setRequiredError(no_ofIntervals, null, false);
		} catch (Exception e) {
			setRequiredError(no_ofIntervals, getPropertyName("invalid_data"),
					true);
			no_ofIntervals.focus();
			ret = false;
			// TODO: handle exception
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
		for (int i = 3; i < visibleColumnsList.size(); i++) {
			refId = visibleColumnsList.get(i);
			table.removeContainerProperty(refId);
			visibleColumnsList.remove(i);
			i--;
		}

	}

	public double netPriceCalculation(double rentRate,double quantity,Date supplyDate)
	{
		double netPrice=0;
		int diffDays=0,diffDay=0,diffYear=0,diffMonth=0,i,j,k;
		
		Calendar fromCal=Calendar.getInstance();
		fromCal.setTime(supplyDate);
		
		Calendar toCal=Calendar.getInstance();
		toCal.setTime(fromDate.getValue());
		
		issueCalendar = new GregorianCalendar(supplyDate.getYear(), supplyDate.getMonth(), supplyDate.getDate());
		returnCalendar = new GregorianCalendar(fromDate.getValue().getYear(), fromDate.getValue().getMonth(), fromDate.getValue().getDate());
		
		currentYear=toCal.get(Calendar.YEAR);
		currentTime=fromDate.getValue().getTime();
		currentMonth=fromDate.getValue().getMonth();
		currentDay=fromDate.getValue().getDate();
		currentMaxDays=returnCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		currentBalanceDays=Math.abs(1-currentDay)+1;
		
		issueTime=supplyDate.getTime();		
		issueYear=fromCal.get(Calendar.YEAR);
		issueMonth=supplyDate.getMonth();
		issueDay=supplyDate.getDate();
		issueMaxDays=issueCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		issueBalanceDays=(issueMaxDays-issueDay)+1;
		
		diffTime=currentTime-issueTime;
		
		diffDays=(int) (diffTime/(24*60*60*1000))+1;
		
		System.out.println("Difference in Days "+diffDays);
		if (issueYear == currentYear) 
		{
			if (issueMonth == currentMonth) 
			{
				if(diffDays<=31)
				{
					netPrice+=(rentRate*quantity);
				}
			}
			else if (issueMonth != currentMonth)
			{
				if(diffDays>31)
				{
					diffMonth=currentMonth - issueMonth;					
						netPrice=0;
						for(i=issueMonth;i<=currentMonth;i++)
						{
							if(i==issueMonth)
							{
								if(issueLeap)
								{
									leap=1;
								}
								else
								{
									leap=0;
								}
								netPrice+=(rentRate/calculateDays(i)*quantity*issueBalanceDays);
							}
							else if(i==currentMonth)
							{
								if(currentLeap)
								{
									leap=1;
								}
								else
								{
									leap=0;
								}
								netPrice+=(rentRate/calculateDays(i)*quantity*currentBalanceDays);
							}
							else
							{
								netPrice+=(rentRate*quantity);
							}
						}
						leap=0;
				}
				else
				{
					netPrice+=(rentRate*quantity);
				}
			}					
		} 
		else
		{
			if(diffDays>31)
			{
				netPrice=0;
				diffYear=currentYear - issueYear;
				for(j=issueMonth;j<=11;j++)
				{
					if(j==issueMonth)
					{
						if(issueLeap)
						{
							leap=1;
						}
						else
						{
							leap=0;
						}
						netPrice+=(rentRate/calculateDays(j)*quantity*issueBalanceDays);
					}
					else
					{
						netPrice+=(rentRate*quantity);
					}
				}
				for(k=0;k<=currentMonth;k++)
				{
					if(k==currentMonth)
					{
						if(currentLeap)
						{
							leap=1;
						}
						else
						{
							leap=0;
						}
						netPrice+=(rentRate/calculateDays(k)*quantity*currentBalanceDays);
					}
					else
					{
						netPrice+=(rentRate*quantity);
					}
				}
				leap=0;
				if(diffYear>1)
				{
					netPrice+=(rentRate*quantity)*(diffYear-1)*12;
				}
			}
			else
			{
				netPrice+=(rentRate*quantity);
			}
		}
		return netPrice;
	}

	public double netPrice(double rentRate,double quantity,Date supplyDate,Date finalDate)
	{
		double netPrice=0;
		int diffDays=0,diffDay=0,diffYear=0,diffMonth=0,i,j,k;
		
		Calendar fromCal=Calendar.getInstance();
		fromCal.setTime(supplyDate);
		
		Calendar toCal=Calendar.getInstance();
		toCal.setTime(finalDate);
		
		issueCalendar = new GregorianCalendar(supplyDate.getYear(), supplyDate.getMonth(), supplyDate.getDate());
		returnCalendar = new GregorianCalendar(finalDate.getYear(), finalDate.getMonth(), finalDate.getDate());
		
		currentYear=toCal.get(Calendar.YEAR);
		currentTime=finalDate.getTime();
		currentMonth=finalDate.getMonth();
		currentDay=finalDate.getDate();
		currentMaxDays=returnCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		currentBalanceDays=Math.abs(1-currentDay)+1;
		System.out.println("Final Date "+currentDay+"-"+currentMonth+"-"+currentYear);
		
		issueTime=supplyDate.getTime();		
		issueYear=fromCal.get(Calendar.YEAR);
		issueMonth=supplyDate.getMonth();
		issueDay=supplyDate.getDate();
		issueMaxDays=issueCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		issueBalanceDays=(issueMaxDays-issueDay)+1;
		System.out.println("Supply Date "+issueDay+"-"+issueMonth+"-"+issueYear);
		diffTime=currentTime-issueTime;
		
		
		diffDays=(int) (diffTime/(24*60*60*1000))+1;
		System.out.println("Differnce in days in calc "+diffDays);
		
		if (issueYear == currentYear) 
		{
			if (issueMonth == currentMonth) 
			{
				if(diffDays<=31)
				{
					netPrice=(rentRate*quantity);
					System.out.println("Net Pricce here "+netPrice	);
				}
			}
			else if (issueMonth != currentMonth)
			{
				if(diffDays>31)
				{
					diffMonth=currentMonth - issueMonth;					
						netPrice=0;
						for(i=issueMonth;i<=currentMonth;i++)
						{
							if(i==issueMonth)
							{
								if(issueLeap)
								{
									leap=1;
								}
								else
								{
									leap=0;
								}
								netPrice+=(rentRate/calculateDays(i)*quantity*issueBalanceDays);
							}
							else if(i==currentMonth)
							{
								if(currentLeap)
								{
									leap=1;
								}
								else
								{
									leap=0;
								}
								netPrice+=(rentRate/calculateDays(i)*quantity*currentBalanceDays);
							}
							else
							{
								netPrice+=(rentRate*quantity);
							}
						}
//						System.out.println("Net Pricce here "+netPrice	);
						leap=0;
				}
				else
				{
					netPrice+=(rentRate*quantity);
//					System.out.println("Net Pricce here "+netPrice	);
				}
			}					
		} 
		else
		{
			if(diffDays>31)
			{
				netPrice=0;
				diffYear=currentYear - issueYear;
				for(j=issueMonth;j<=11;j++)
				{
					if(j==issueMonth)
					{
						if(issueLeap)
						{
							leap=1;
						}
						else
						{
							leap=0;
						}
						netPrice+=(rentRate/calculateDays(j)*quantity*issueBalanceDays);
					}
					else
					{
						netPrice+=(rentRate*quantity);
					}
				}
				for(k=0;k<=currentMonth;k++)
				{
					if(k==currentMonth)
					{
						if(currentLeap)
						{
							leap=1;
						}
						else
						{
							leap=0;
						}
						netPrice+=(rentRate/calculateDays(k)*quantity*currentBalanceDays);
					}
					else
					{
						netPrice+=(rentRate*quantity);
					}
				}
//				System.out.println("Net Pricce here "+netPrice	);
				leap=0;
				if(diffYear>1)
				{
					netPrice+=(rentRate*quantity)*(diffYear-1)*12;
//					System.out.println("Net Pricce here "+netPrice	);
				}
			}
			else
			{
				netPrice+=(rentRate*quantity);
//				System.out.println("Net Pricce here "+netPrice	);
			}
		}
		System.out.println("Net Pricce here "+netPrice	);
		return netPrice;
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
	
	public int dayDifference(int finalDay,Date supplyDate)
	{
		int diff=0;
		Calendar fromCal=Calendar.getInstance();
		fromCal.setTime(supplyDate);
		issueCalendar = new GregorianCalendar(supplyDate.getYear(), supplyDate.getMonth(), supplyDate.getDate());
		currentYear=fromCal.get(Calendar.YEAR);
		currentDay=supplyDate.getDate();
		diff=Math.abs(finalDay-currentDay);
		return diff;
	}
}
