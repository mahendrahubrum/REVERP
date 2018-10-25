package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.inventory.budget.dao.BudgetDefinitionDao;
import com.inventory.budget.model.BudgetDefinitionModel;
import com.inventory.budget.model.BudgetLVMasterModel;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.rent.dao.RentDetailsDao;
import com.inventory.rent.dao.RentPaymentDao;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
import com.inventory.rent.model.RentReturnItemDetailModel;
import com.inventory.reports.bean.BudgetReportBean;
import com.inventory.reports.bean.RentReportBean;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.RentReportDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         May 13, 2014
 */
public class RentReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6307273287413955396L;

	
	Calendar calendar;
	int leap=0;
	boolean issueLeap=false,returnLeap=false,currentLeap=false;
	long currentTime=0,issueTime=0,returnTime=0,diffTime=0;
	int issueYear,issueMonth,issueDay,issueMaxDays,issueBalanceDays;
	int currentYear,currentMonth,currentDay,currentMaxDays,currentBalanceDays;
	Calendar issueCalendar, returnCalendar;
	RentPaymentDao paymentDao;
	SComboField officeCombo;
	SComboField employeeCombo;
	SDateField fromDate;
	SDateField toDate;
	SRadioButton paymentType;
	SComboField customerCombo;
	SComboField rentidCombo;
	private SReportChoiceField reportchoiceField;
	private SButton generate;
	private Report report;
	private SComboField organizationCombo;
	OfficeDao offDao;
	CustomerDao custDao;
	RentDetailsDao rentDao;
	RentReportDao reportDao;
	RentReportBean reportBean;
	private UserManagementDao userDao;
	RentDetailsDao rentDetailsDao;

	@Override
	public SPanel getGUI() {
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("400");
		setHeight("400");
		layout.setSpacing(true);
		offDao = new OfficeDao();
		custDao = new CustomerDao();
		paymentDao=new RentPaymentDao();
		fromDate = new SDateField(getPropertyName("from_date"));
		fromDate.setValue(CommonUtil.getCurrentSQLDate());
		toDate = new SDateField(getPropertyName("to_date"));
		toDate.setValue(CommonUtil.getCurrentSQLDate());
		paymentType = new SRadioButton(getPropertyName("payment_type"), 250,
				SConstants.filterTypeList, "intKey", "value");
		paymentType.setStyleName("radio_horizontal");
		paymentType.setValue(0);

		rentidCombo = new SComboField(getPropertyName("rent_id"), 200);
		reportchoiceField = new SReportChoiceField(getPropertyName("export_to"));
		generate = new SButton(getPropertyName("generate"));
		report = new Report(getLoginID());
		reportDao = new RentReportDao();

		rentDao = new RentDetailsDao();
		userDao = new UserManagementDao();
		employeeCombo = new SComboField("Employee", 200);

		rentDetailsDao = new RentDetailsDao();
		try {

			List organizationList = new ArrayList();
			organizationList
					.addAll(new OrganizationDao().getAllOrganizations());
			// organizationList.add(0, new S_OrganizationModel(0,
			// "------------All-------------"));

			organizationCombo = new SComboField(
					getPropertyName("organization"), 200, organizationList,
					"id", "name");

			// orgCombo.setInputPrompt("------------All-------------");

			officeCombo = new SComboField(getPropertyName("office"), 200);
			organizationCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					try {

						officeList.add(0, new S_OfficeModel(0,
								"-----------All----------"));
						officeList.addAll(offDao
								.getAllOfficesUnderOrg((Long) organizationCombo
										.getValue()));

						System.out.println(officeList.size());

						SCollectionContainer office = SCollectionContainer
								.setList(officeList, "id");
						officeCombo.setContainerDataSource(office);
						officeCombo
								.setInputPrompt("-----------All--------------");
						officeCombo.setItemCaptionPropertyId("name");
						officeCombo.setValue((long) 0);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			officeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					try {

						List customerlist = new ArrayList();
						List employeelist = new ArrayList();
						List rentList = new ArrayList();
						if (officeCombo.getValue() == null
								|| (Long) officeCombo.getValue() == 0) {
							customerlist.addAll(custDao
									.getAllActiveCustomerNamesWithOrgID((Long) organizationCombo
											.getValue()));
							employeelist.addAll(rentDetailsDao
									.getEmployeesWithFullNameAndCodeUnderOfficeIncludingSemiAdminforrentdetails((Long) organizationCombo
											.getValue()));
						}

						else {
							customerlist.addAll(custDao
									.getAllActiveCustomerNamesWithLedgerID((Long) officeCombo
											.getValue()));

							employeelist.addAll(rentDetailsDao
									.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdminforrentdetails(
											getOfficeID(), getOrganizationID()));
						}

						customerlist.add(0, new CustomerModel(0,
								"------------All-------------"));

						SCollectionContainer office = SCollectionContainer
								.setList(customerlist, "id");
						customerCombo.setContainerDataSource(office);
						customerCombo
								.setInputPrompt("-----------All--------------");
						customerCombo.setItemCaptionPropertyId("name");
						customerCombo.setValue((long) 0);

						employeelist.add(0, new UserModel(0,
								"------------All-------------"));

						SCollectionContainer employee = SCollectionContainer
								.setList(employeelist, "id");
						employeeCombo.setContainerDataSource(employee);
						employeeCombo
								.setInputPrompt("-----------All--------------");
						employeeCombo.setItemCaptionPropertyId("first_name");
						employeeCombo.setValue((long) 0);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			employeeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						List customerlist = new ArrayList();
						if (officeCombo.getValue() == null
								|| (Long) officeCombo.getValue() == 0) {
							if (employeeCombo.getValue() == null
									|| (Long) employeeCombo.getValue() == 0) {
								customerlist.addAll(custDao
										.getAllActiveCustomerNamesWithOrgID((Long) organizationCombo
												.getValue()));

							}

							else {
								customerlist.addAll(custDao
										.getAllActiveCustomerNamesunderanemployee(
												(Long) employeeCombo.getValue(),
												(Long) organizationCombo
														.getValue()));
							}
						} else {

							if (employeeCombo.getValue() == null
									|| (Long) employeeCombo.getValue() == 0) {
								customerlist.addAll(custDao
										.getAllActiveCustomerNamesWithLedgerID((Long) officeCombo
												.getValue()));

							}

							else {
								customerlist.addAll(custDao
										.getAllActiveCustomerNamesunderanemployeeandoffice(
												(Long) employeeCombo.getValue(),
												(Long) officeCombo.getValue()));
							}

						}
						customerlist.add(0, new CustomerModel(0,
								"------------All-------------"));

						SCollectionContainer customers = SCollectionContainer
								.setList(customerlist, "id");
						customerCombo.setContainerDataSource(customers);
						customerCombo
								.setInputPrompt("-----------All--------------");
						customerCombo.setItemCaptionPropertyId("name");
						customerCombo.setValue((long) 0);
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

						List rentList = new ArrayList();
						if (officeCombo.getValue() == null
								|| (Long) officeCombo.getValue() == 0) {

							if (employeeCombo.getValue() == null
									|| (Long) employeeCombo.getValue() == 0) {

								if (customerCombo.getValue() == null
										|| (Long) customerCombo.getValue() == 0) {

									rentList.addAll(new RentDetailsDao()
											.getActiveRentunderCustomers((Long) organizationCombo
													.getValue()));

								}

								else {

									rentList.addAll(new RentDetailsDao()
											.getActiveRentundereachCustomer((Long) customerCombo
													.getValue()));
								}
							} else {
								if (customerCombo.getValue() == null
										|| (Long) customerCombo.getValue() == 0) {

									rentList.addAll(new RentDetailsDao()
											.getActiveRentunderemployees(
													(Long) organizationCombo
															.getValue(),
													(Long) employeeCombo
															.getValue()));

								}

								else {

									rentList.addAll(new RentDetailsDao()
											.getActiveRentundereachCustomer((Long) customerCombo
													.getValue()));
								}
							}
						} else {
							if (employeeCombo.getValue() == null
									|| (Long) employeeCombo.getValue() == 0) {
								if (customerCombo.getValue() == null
										|| (Long) customerCombo.getValue() == 0) {

									rentList.addAll(new RentDetailsDao()
											.getActiveRentunderofficeandCustomers(
													(Long) organizationCombo
															.getValue(),
													(Long) officeCombo
															.getValue()));

								} else {

									rentList.addAll(new RentDetailsDao()
											.getAllRentIdsBasedoncustomers(
													(Long) officeCombo
															.getValue(),
													(Long) customerCombo
															.getValue()));
								}
							} else {
								if (customerCombo.getValue() == null
										|| (Long) customerCombo.getValue() == 0) {

									rentList.addAll(new RentDetailsDao()
											.getActiveRentunderofficeandemployees(
													(Long) employeeCombo
															.getValue(),
													(Long) officeCombo
															.getValue()));

								} else {

									rentList.addAll(new RentDetailsDao()
											.getAllRentIdsBasedoncustomers(
													(Long) officeCombo
															.getValue(),
													(Long) customerCombo
															.getValue()));
								}
							}
						}

						rentList.add(0, new RentDetailsModel(0,
								"------------All-------------"));

						SCollectionContainer office = SCollectionContainer
								.setList(rentList, "id");
						rentidCombo.setContainerDataSource(office);
						rentidCombo
								.setInputPrompt("-----------All--------------");
						rentidCombo.setItemCaptionPropertyId("comments");
						rentidCombo.setValue((long) 0);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			paymentType.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List rentList = new ArrayList();
					try {
						if ((Integer) paymentType.getValue() == 0) {
							rentList.addAll(new RentDetailsDao()
									.getActiveRentunderCustomers((Long) organizationCombo
											.getValue()));
						}
						if ((Integer) paymentType.getValue() == 1) {
							rentList.addAll(new RentDetailsDao()
									.getActiveCashRentunderCustomers((Long) organizationCombo
											.getValue()));
						}
						if ((Integer) paymentType.getValue() == 2) {
							rentList.addAll(new RentDetailsDao()
									.getActiveCreditRentunderCustomers((Long) organizationCombo
											.getValue()));
						}

						rentList.add(0, new RentDetailsModel(0,
								"------------All-------------"));

						SCollectionContainer office = SCollectionContainer
								.setList(rentList, "id");
						rentidCombo.setContainerDataSource(office);
						rentidCombo
								.setInputPrompt("-----------All--------------");
						rentidCombo.setItemCaptionPropertyId("comments");
						rentidCombo.setValue((long) 0);
					} catch (Exception e) {
						System.out.println("Payment type error :");
						e.printStackTrace();
					}

				}
			});

		} catch (Exception e) {
		}

		generate.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				List reportList = new CopyOnWriteArrayList();
				List<RentInventoryDetailsModel> newReportList;
				String items = "";
				List rentList = new ArrayList();
				List itemList = new ArrayList();
				Date returnedDate;
				try {
					RentDetailsModel mdl = null;
					RentInventoryDetailsModel detailmdl;
					RentReturnItemDetailModel itemreturnmdl;
					reportList.addAll(reportDao.getRentReport(
							(Long) organizationCombo.getValue(),
							(Long) officeCombo.getValue(), 
							(Long) employeeCombo.getValue(),
							(Integer) paymentType.getValue(), 
							(Long) customerCombo.getValue(), 
							(Long) rentidCombo.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue())));
					if (reportList.size() != 0) 
					{
						Iterator itr = reportList.iterator();
						while (itr.hasNext())
						{
							double rentAmount=0,actualQuantity=0,returnedQuantity=0,quantity=0,rentRate=0,calculatedAmount=0,paidAmount=0,taxAmount=0;
							long tax=0;
							mdl = (RentDetailsModel) itr.next();
							newReportList = mdl.getInventory_details_list();
							items = "";
							for (int k = 0; k < newReportList.size(); k++)
							{
								detailmdl = newReportList.get(k);
								if (k != 0) 
								{
									items += ",<br>";
								}
								items += detailmdl.getItem().getName()
										+ " (Qty: "
										+ detailmdl.getQunatity()
										+ ", Rate: "
										+ detailmdl.getUnit_price()
										+ ", Not Returned Qty: "
										+ (detailmdl.getQunatity() - detailmdl
												.getReturned_qty()) + ")";
								long actqty = 0;
								quantity=detailmdl.getQunatity();
								returnedQuantity=detailmdl.getReturned_qty();
								actualQuantity=quantity-returnedQuantity;
								rentRate=detailmdl.getUnit_price();
								returnedDate=rentDao.getReturned(detailmdl.getId());
								if(fromDate.getValue().getTime()<detailmdl.getSupplied_date().getTime())
								{
									fromDate.setValue(new Date(detailmdl.getSupplied_date().getTime()));
								}
								if(returnedDate!=null)
								{
									if(toDate.getValue().getTime()>returnedDate.getTime())
									{
										toDate.setValue(new Date(returnedDate.getTime()));
									}
								}
								rentAmount=rentDao.getRentdetails(mdl.getId(),detailmdl.getId());
								calculatedAmount+=netPriceCalculation(rentRate,actualQuantity);
								calculatedAmount+=rentAmount;
								calculatedAmount-=detailmdl.getDiscount_amount();
								tax=mdl.getTax().getId();
								
							}
							String salesPerson = "";
							if (isTaxEnable()) 
							{
								TaxModel txmdl = rentDetailsDao.gettaxDetails(tax);
								if (txmdl.getValue_type() == 2) 
								{
									taxAmount = txmdl.getValue();
								}
								else 
								{
									taxAmount= ((txmdl.getValue()) / 100) * calculatedAmount;
								}

							}
							else 
							{
								taxAmount = 0;
							}
							calculatedAmount+=taxAmount;
							paidAmount=calculatedAmount-paymentDao.getPaidAmount((Long)mdl.getId());
							salesPerson = userDao.getUserFromLogin(
									mdl.getResponsible_person())
									.getFirst_name();
							reportBean = new RentReportBean(
									mdl.getDate(), 
									mdl.getOffice().getName(),
									mdl.getCustomer().getName(), 
									mdl.getRent_number(),
									salesPerson, 
									items,
									roundNumber(calculatedAmount), 
									roundNumber(paidAmount));
							rentList.add(reportBean);
						}

						report.setJrxmlFileName("Rent_Report");
						report.setReportFileName("Rent_Report");
						report.setReportTitle("Rent Report");
						String subHeader = "";

						subHeader += "\n From : "+ CommonUtil.formatDateToDDMMYYYY(fromDate.getValue())
								  + "\t To : "   + CommonUtil.formatDateToDDMMYYYY(toDate.getValue());
						report.setReportSubTitle(subHeader);
						report.setIncludeHeader(true);
						report.setIncludeFooter(false);
						report.setReportType((Integer) reportchoiceField.getValue());
						report.createReport(rentList, null);
					}
					else 
					{
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
					}

				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		});

		layout.addComponent(organizationCombo);
		layout.addComponent(officeCombo);
		layout.addComponent(paymentType);
		layout.addComponent(employeeCombo);
		layout.addComponent(customerCombo);
		layout.addComponent(rentidCombo);
		layout.addComponent(fromDate);
		layout.addComponent(toDate);
		layout.addComponent(reportchoiceField);
		layout.addComponent(generate);

		organizationCombo.setValue(getOrganizationID());
		panel.setContent(layout);
		return panel;
	}

	private void loadBillNo(long customerId, long officeId) {
		List<Object> rentList = null;
		try {

			String condition1 = "";

			if ((Integer) paymentType.getValue() == 1) {
				condition1 = " and payment_amount=amount ";
			} else if ((Integer) paymentType.getValue() == 2) {
				condition1 = " and payment_amount<amount ";
			}

			if (customerId != 0) {
				rentList = rentDao.getAllRentNumbersForCustomer(officeId,
						customerId,
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						condition1);
			} else {
				rentList = rentDao.getAllRentidsByDate(officeId,
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						condition1);
			}

			RentDetailsModel rentModel = new RentDetailsModel();
			rentModel.setId(0);
			rentModel
					.setComments("---------------------ALL-------------------");
			if (rentList == null) {
				rentList = new ArrayList<Object>();
			}
			rentList.add(0, rentModel);
			SCollectionContainer container = SCollectionContainer.setList(
					rentList, "id");
			rentidCombo.setContainerDataSource(container);
			rentidCombo.setItemCaptionPropertyId("comments");
			rentidCombo.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public double netPriceCalculation(double rentRate,double quantity)
	{
		double netPrice=0;
		int diffDays=0,diffDay=0,diffYear=0,diffMonth=0,i,j,k;
		Calendar fromCal=Calendar.getInstance();
		fromCal.setTime(fromDate.getValue());
		Calendar toCal=Calendar.getInstance();
		toCal.setTime(toDate.getValue());
		
		
		issueCalendar = new GregorianCalendar(fromDate.getValue()
				.getYear(), fromDate.getValue().getMonth(), fromDate
				.getValue().getDate());
		returnCalendar = new GregorianCalendar(toDate.getValue()
				.getYear(), toDate.getValue().getMonth(), toDate
				.getValue().getDate());
		
		currentYear=toCal.get(Calendar.YEAR);
		currentTime=toDate.getValue().getTime();
		currentMonth=toDate.getValue().getMonth();
		currentDay=toDate.getValue().getDate();
		currentMaxDays=returnCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		currentBalanceDays=Math.abs(1-currentDay)+1;
		
		issueTime=fromDate.getValue().getTime();		
		issueYear=fromCal.get(Calendar.YEAR);
		issueMonth=fromDate.getValue().getMonth();
		issueDay=fromDate.getValue().getDate();
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
		System.out.println("Final Amount = "+netPrice);
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
	
}
