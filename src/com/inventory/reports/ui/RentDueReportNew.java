package com.inventory.reports.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.budget.model.BudgetLVChildModel;
import com.inventory.budget.model.BudgetLVMasterModel;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.rent.dao.RentDetailsDao;
import com.inventory.rent.dao.RentPaymentDao;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
import com.inventory.rent.model.RentReturnItemDetailModel;
import com.inventory.reports.bean.BudgetReportBean;
import com.inventory.reports.bean.RentCustomerLedgerReportBean;
import com.inventory.reports.dao.RentCustomerLedgerReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SUserError;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         May 16, 2014
 */////RentCustomerLedgerReport
public class RentDueReportNew extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 706969490190337682L;
	Calendar calendar;
	int leap=0;
	boolean issueLeap=false,returnLeap=false,currentLeap=false;
	long currentTime=0,issueTime=0,returnTime=0,diffTime=0;
	int issueYear,issueMonth,issueDay,issueMaxDays,issueBalanceDays;
	int currentYear,currentMonth,currentDay,currentMaxDays,currentBalanceDays;
	Calendar issueCalendar, returnCalendar;
	
	
	private SComboField organizationCombo;
	SComboField officeCombo;
	SDateField fromDate;
	SDateField toDate;
	SComboField customerCombo;
	private SReportChoiceField reportchoiceField;
	private SButton generate;
	private SButton show;
	private Report report;
	OfficeDao offDao;
	
	CustomerDao custDao;
	STable entryTable;
	
	RentPaymentDao paymentDao;
	
	SHorizontalLayout horizontalLayout;
	SHorizontalLayout buttonLayout;
	RentCustomerLedgerReportDao reportDao;
	RentCustomerLedgerReportBean reportBean;
	SWindow popup;
	STable childTable;
	RentDetailsDao rentDao;
	RentDetailsModel mastermdl;
	LedgerDao ledDao;

	@Override
	public SPanel getGUI() {
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("1200");
		setHeight("500");
		layout.setSpacing(true);
		paymentDao=new RentPaymentDao();
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
		fromDate = new SDateField(getPropertyName("from_date"));
		fromDate.setValue(CommonUtil.getCurrentSQLDate());
		toDate = new SDateField(getPropertyName("to_date"));
		toDate.setValue(CommonUtil.getCurrentSQLDate());
		reportchoiceField = new SReportChoiceField(getPropertyName("export_to"));
		report = new Report(getLoginID());
		offDao = new OfficeDao();
		custDao = new CustomerDao();
		customerCombo = new SComboField(getPropertyName("customer"), 200);
		customerCombo.setRequired(true);
		customerCombo.setInputPrompt("------------All-------------");
		entryTable = new STable();
		entryTable.setWidth("800");
		entryTable.addContainerProperty("Date", java.util.Date.class, null,
				getPropertyName("date"), null, Align.CENTER);
		entryTable.addContainerProperty("Rent No", Long.class, null,
				getPropertyName("rent_no"), null, Align.CENTER);
		entryTable.addContainerProperty("Amount Paid", Double.class, null,
				getPropertyName("amount_paid"), null, Align.CENTER);
		entryTable.addContainerProperty("Total Amount", Double.class, null,
				getPropertyName("total_amount"), null, Align.CENTER);
		entryTable.addContainerProperty("Balance", Double.class, null,
				getPropertyName("balance"), null, Align.CENTER);
		entryTable.addContainerProperty("loading charge", Double.class, null,
				getPropertyName("loading_charge"), null, Align.CENTER);

		entryTable.addContainerProperty("Max credit period", Integer.class,
				null, getPropertyName("max_credit_period"), null, Align.CENTER);
		entryTable.addContainerProperty("customer", String.class, null,
				getPropertyName("customer"), null, Align.CENTER);

		entryTable.setFooterVisible(true);
		entryTable.setSelectable(true);
		horizontalLayout = new SHorizontalLayout();
		reportDao = new RentCustomerLedgerReportDao();
		reportBean = new RentCustomerLedgerReportBean();
		popup = new SWindow(getPropertyName("details"));
		popup.setHeight("450");
		popup.setWidth("850");
		popup.setModal(true);
		popup.center();
		SPanel pan = new SPanel();
		pan.setSizeFull();
		popup.setContent(pan);
		pan.setContent(lay);
		childTable = new STable();
		childTable.setWidth("800");
		childTable.setHeight("200");

		childTable.addContainerProperty("Item", String.class, null,
				getPropertyName("item"), null, Align.CENTER);
		childTable.addContainerProperty("Quantity", Double.class, null,
				getPropertyName("quantity"), null, Align.CENTER);
		childTable.addContainerProperty("Period", Double.class, null,
				getPropertyName("period"), null, Align.CENTER);
		childTable.addContainerProperty("Status", String.class, null,
				getPropertyName("status"), null, Align.CENTER);
		childTable.addContainerProperty("Return Date", java.util.Date.class,
				null, getPropertyName("return_date"), null, Align.CENTER);
		childTable.addContainerProperty("Discount", Double.class, null,
				getPropertyName("discount"), null, Align.CENTER);
		childTable.addContainerProperty("Amount", Double.class, null,
				getPropertyName("amount"), null, Align.CENTER);

		rentDao = new RentDetailsDao();

		try {

			List organizationList = new ArrayList();
			organizationList = new OrganizationDao().getAllOrganizations();

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

			entryTable.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (entryTable.getValue() != null) {
						rentNo.setValue("0");
						shippingCharge.setValue("0");

						customer.setValue("");
						maxCreditPeriod.setValue("0");
						datefield.setValue(CommonUtil.getCurrentSQLDate());
						netAmount.setValue("0");
						paidAmnt.setValue("0");
						childTable.removeAllItems();
						Item itm = entryTable.getItem(entryTable.getValue());
						if (getUI().getCurrent().getWindows().contains(popup)) {

							getUI().getCurrent().removeWindow(popup);

						}
						getUI().getCurrent().addWindow(popup);
						Item itmm = entryTable.getItem(entryTable.getValue());

						Long rentid = (Long) itmm.getItemProperty("Rent No")
								.getValue(); // TODO: Do something with this
												// value.
						Double shipping_charge = (Double) itmm.getItemProperty(
								"loading charge").getValue();
						Integer max_credit = (Integer) itmm.getItemProperty(
								"Max credit period").getValue();
						String customer_name = (String) itmm.getItemProperty(
								"customer").getValue();
						Date datefieldnew = (Date) itmm.getItemProperty("Date")
								.getValue();
						Double netAmnt = (Double) itmm.getItemProperty(
								"Total Amount").getValue();
						Double paidAmt = (Double) itmm.getItemProperty(
								"Amount Paid").getValue();

						try {
							RentDetailsModel mastermdl = new RentDetailsModel();

							RentInventoryDetailsModel childmdl = new RentInventoryDetailsModel();

							List lis = rentDao.getbudgetMaster(rentid);

							rentNo.setValue(Long.toString(rentid));
							shippingCharge.setValue(Double
									.toString(shipping_charge));

							customer.setValue(customer_name);
							maxCreditPeriod.setValue(Integer
									.toString(max_credit));
							datefield.setValue(CommonUtil
									.getUtilFromSQLDate(datefieldnew));
							netAmount.setValue(Double.toString(netAmnt));
							paidAmnt.setValue(Double.toString(paidAmt));

							for (int m = 0; m < lis.size(); m++) {

								mastermdl = (RentDetailsModel) lis.get(m);

								for (int n = 0; n < mastermdl
										.getInventory_details_list().size(); n++) {
									childmdl = mastermdl
											.getInventory_details_list().get(n);

									double finalamnt = 0;

									if (childmdl.getDiscount_amount() != 0) {

										finalamnt = childmdl.getNet_price()
												- childmdl.getDiscount_amount();
									} else {
										finalamnt = childmdl.getNet_price();
									}
									childTable.setVisibleColumns(new String[] {
											"Item", "Quantity", "Period",
											"Status", "Return Date",
											"Discount", "Amount" });

									childTable.addItem(
											new Object[] {
													childmdl.getItem()
															.getName(),
													childmdl.getQunatity(),
													childmdl.getPeriod(),
													childmdl.getReturned_status(),
													childmdl.getReturned_date(),
													childmdl.getDiscount_amount(),
													finalamnt

											},
											childTable.getItemIds().size() + 1);

								}

							}

							childTable.setVisibleColumns(new String[] { "Item",
									"Quantity", "Period", "Status",
									"Return Date", "Discount", "Amount" });
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			});

			generate.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					customerCombo.setComponentError(null);
					List reportList = new ArrayList();
					List newReportList = new ArrayList();
					List<RentInventoryDetailsModel> newDetailsList = new ArrayList();
					Date returnedDate;
					try {
						
						if (customerCombo.getValue() != null) 
						{
							RentDetailsModel mdl = null;
							RentInventoryDetailsModel detailmdl;
							RentReturnItemDetailModel itemreturnmdl;
							reportList.addAll(reportDao.getRentCustomerReport(
									(Long) organizationCombo.getValue(),
									(Long) officeCombo.getValue(),
									(Long) customerCombo.getValue(), 
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDate.getValue())));
							if (reportList.size() != 0) 
							{
								Iterator itr = reportList.iterator();
								while (itr.hasNext()) 
								{
									double rentAmount=0,actualQuantity=0,returnedQuantity=0,quantity=0,rentRate=0,calculatedAmount=0,paidAmount=0,taxAmount=0,balanceAmount=0;
									long tax=0;
									mdl = (RentDetailsModel) itr.next();
									newDetailsList = mdl.getInventory_details_list();
									for (int k = 0; k < newDetailsList.size(); k++)
									{
										detailmdl =newDetailsList.get(k);
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
									if (isTaxEnable()) 
									{
										TaxModel txmdl = rentDao.gettaxDetails(tax);
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
									paidAmount=paymentDao.getPaidAmount((Long)mdl.getId());
									balanceAmount=calculatedAmount-paidAmount;
									reportBean = new RentCustomerLedgerReportBean(
											mdl.getDate(),
											mdl.getRent_number(), 
											roundNumber(calculatedAmount), 
											roundNumber(paidAmount), 
											roundNumber(balanceAmount)
									);
									newReportList.add(reportBean);
								}

								HashMap<String, Object> params = new HashMap<String, Object>();
								params.put("FromDate", fromDate.getValue()
										.toString());
								params.put("ToDate", toDate.getValue()
										.toString());
								params.put("LedgerName", mdl.getCustomer()
										.getName());

								params.put("Office", mdl.getOffice().getName());
								params.put("Organization", mdl.getOffice()
										.getOrganization().getName());

								report.setJrxmlFileName("RentCustomerLedgerReport");
								report.setReportFileName("RentCustomerLedgerReport");
								report.setReportTitle("Rent Customer Ledger Report");
								String subHeader = "";

								subHeader += "\n From : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDate
														.getValue())
										+ "\t To : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDate
														.getValue());

								report.setReportSubTitle("From  : "
										+ CommonUtil
												.formatDateToCommonFormat(fromDate
														.getValue())
										+ "   To  : "
										+ CommonUtil
												.formatDateToCommonFormat(toDate
														.getValue()));

								report.setReportSubTitle(subHeader);
								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType((Integer) reportchoiceField
										.getValue());
								report.setOfficeName(officeCombo
										.getItemCaption(officeCombo.getValue()));
								report.createReport(newReportList, params);

								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
						} else {
							setRequiredError(customerCombo, "Select Customer",
									true);
						}

					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			});

			show.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					List reportList = new ArrayList();
					List newReportList = new ArrayList();
					List<RentInventoryDetailsModel> newDetailsList = new ArrayList();
					Date returnedDate;
					
					customerCombo.setComponentError(null);
					double balance=0;
					entryTable.removeAllItems();
					try {
						if (customerCombo.getValue() != null) {
							entryTable.setVisibleColumns(new String[] { "Date",
									"Rent No", "Amount Paid", "Total Amount",
									"Balance", "loading charge",
									"Max credit period", "customer" });
							RentDetailsModel mdl = null;
							RentInventoryDetailsModel detailmdl;
							RentReturnItemDetailModel itemreturnmdl;
							reportList.addAll(reportDao.getRentCustomerReport(
									(Long) organizationCombo.getValue(),
									(Long) officeCombo.getValue(),
									(Long) customerCombo.getValue(), CommonUtil
											.getSQLDateFromUtilDate(fromDate
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue())));
							if (reportList.size() != 0) 
							{
								Iterator itr = reportList.iterator();
								while (itr.hasNext()) 
								{
									double rentAmount=0,actualQuantity=0,returnedQuantity=0,quantity=0,rentRate=0,calculatedAmount=0,paidAmount=0,taxAmount=0,balanceAmount=0;
									long tax=0;
									mdl = (RentDetailsModel) itr.next();
									newDetailsList = mdl.getInventory_details_list();
									for (int k = 0; k < newDetailsList.size(); k++)
									{
										detailmdl =newDetailsList.get(k);
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
									if (isTaxEnable()) 
									{
										TaxModel txmdl = rentDao.gettaxDetails(tax);
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
									paidAmount=paymentDao.getPaidAmount((Long)mdl.getId());
									balanceAmount=calculatedAmount-paidAmount;

									reportBean = new RentCustomerLedgerReportBean(
											mdl.getDate(),
											mdl.getRent_number(), 
											roundNumber(calculatedAmount), 
											roundNumber(paidAmount), 
											roundNumber(balanceAmount)
									);

									entryTable.addItem(
											new Object[] 
													{
													reportBean.getDate(),
													reportBean.getRentno(),
													reportBean.getReturnAmount(),
													reportBean.getCash(),
													reportBean.getCash() - reportBean.getReturnAmount(),
													mdl.getShipping_charge(),
													mdl.getCredit_period(),
													mdl.getCustomer().getName() 
													},
											entryTable.getItemIds().size() + 1);

							}

								entryTable.setVisibleColumns(new String[] 
										{
										"Date", "Rent No", "Amount Paid",
										"Total Amount", "Balance" });
							} 
							else 
							{
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
						} else {
							SNotification.show("Select Customer",
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		masterDetailsGrid.addComponent(new SLabel("Rent No :"), 1, 1);
		masterDetailsGrid.addComponent(rentNo, 3, 1);
		masterDetailsGrid.addComponent(new SLabel("Customer :"), 4, 1);
		masterDetailsGrid.addComponent(customer, 6, 1);
		masterDetailsGrid.addComponent(new SLabel("Max credit period:"), 1, 2);
		masterDetailsGrid.addComponent(maxCreditPeriod, 3, 2);
		masterDetailsGrid.addComponent(new SLabel("Date :"), 4, 2);
		masterDetailsGrid.addComponent(datefield, 6, 2);
		masterDetailsGrid.addComponent(new SLabel("Loading charge :"), 1, 3);
		masterDetailsGrid.addComponent(shippingCharge, 3, 3);
		masterDetailsGrid.addComponent(new SLabel("Net Amount :"), 4, 3);
		masterDetailsGrid.addComponent(new SLabel("Advance Amount :"), 1, 4);
		masterDetailsGrid.addComponent(netAmount, 6, 3);
		masterDetailsGrid.addComponent(paidAmnt, 3, 4);

		lay.addComponent(masterDetailsGrid);
		lay.addComponent(childTable);
		lay.setComponentAlignment(childTable, Alignment.MIDDLE_CENTER);

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
		customerCombo.setInputPrompt("-------Select Customer-------");
		panel.setContent(horizontalLayout);

		return panel;
	}

	@Override
	public Boolean isValid() {
		
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
