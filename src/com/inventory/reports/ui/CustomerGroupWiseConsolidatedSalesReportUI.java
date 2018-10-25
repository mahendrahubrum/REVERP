package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.CustomerGroupDao;
import com.inventory.config.acct.model.CustomerGroupModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 18, 2015
 */

public class CustomerGroupWiseConsolidatedSalesReportUI extends SparkLogic {

	private static final long serialVersionUID = 5560458618629678494L;
	
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerGroupComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	private SCollectionContainer container;

	private long customerId;

	private UserManagementDao userDao;
	SHorizontalLayout popupContainer;
	SHorizontalLayout detailPopupContainer;
	CustomerGroupDao ledDao;
	CustomerDao custDao;

	SRadioButton filterTypeRadio;
	SRadioButton statusRadioButton;

	private Report report;

	SalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_CUSTOMER = "Customer Group";
	static String TBC_AMOUNT = "Amount";
	static String TBC_PAYMENT = "Payment Amount";
	static String TBC_PENDING = "Pending Amount";
	
	
	static String POP_TBC_SN = "SN";
	static String POP_TBC_SALES_MAN = "Sales Man";
	static String POP_TBC_SALE_NO = "Sale No";
	static String POP_TBC_DATE = "Date";
	static String POP_TBC_CUSTOMER = "Customer";
	static String POP_TBC_AMOUNT = "Amount";
	static String POP_TBC_PAYMENT = "Payment Amount";
	static String POP_TBC_PENDING = "Pending Amount";

	private STable table;
	private STable popTable;

	private String[] allColumns;
	private String[] visibleColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;
	
	SalesReportDao dao;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		detailPopupContainer=new SHorizontalLayout();
		allColumns = new String[] { TBC_SN, TBC_ID,TBC_CUSTOMER, TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
		visibleColumns = new String[] { TBC_SN, TBC_CUSTOMER,  TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
		
		ledDao = new CustomerGroupDao();
		salDao = new SalesDao();
		dao=new SalesReportDao();

		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();
		custDao=new CustomerDao();
		
		setSize(1200, 420);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		SHorizontalLayout mainLay = new SHorizontalLayout();

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {

			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				sett = (SettingsValuePojo) session.getAttribute("settings");

			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			fromDateField = new SDateField(getPropertyName("from_date"));
			fromDateField.setValue(getMonthStartDate());
			fromDateField.setImmediate(true);
			toDateField = new SDateField(getPropertyName("to_date"));
			toDateField.setValue(getWorkingDate());
			toDateField.setImmediate(true);
			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);

			filterTypeRadio = new SRadioButton(getPropertyName("payment_type"),
					250, SConstants.filterTypeList, "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(0);

			statusRadioButton = new SRadioButton(getPropertyName("status"),
					250, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(
							1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);

			mainFormLayout.addComponent(filterTypeRadio);

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);
			

			customerGroupComboField = new SComboField(getPropertyName("group"),
					200);
			loadCustomerGroups(getOfficeID());
			mainFormLayout.addComponent(customerGroupComboField);


			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 800, 275);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_group"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_AMOUNT, 0.8f);
			table.setColumnExpandRatio(TBC_PAYMENT, 0.8f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 2f);
			

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);
			mainPanel.setContent(mainLay);
			
			
			popTable = new STable(null, 1000, 400);
			popTable.setSelectable(true);
			
			popTable.addContainerProperty(POP_TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			popTable.addContainerProperty(POP_TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null,Align.CENTER);
			popTable.addContainerProperty(POP_TBC_SALE_NO, String.class, null,getPropertyName("sales_no"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
			popTable.addContainerProperty(POP_TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			popTable.addContainerProperty(POP_TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			
			popTable.setFooterVisible(true);
			popTable.setColumnFooter(POP_TBC_CUSTOMER, getPropertyName("total"));
			
			customerGroupComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							if (customerGroupComboField.getValue() != null
									&& !customerGroupComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerGroupComboField
										.getValue().toString());
							}
						}
					});


			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerGroups(toLong(officeComboField.getValue()
							.toString()));
					
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesReportBean reportBean = null;

						List<Object> salesModelList = getSalesReportList();


						if (salesModelList!=null&&salesModelList.size()>0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("CustomerGroupWiseConsolidatedSalesReport");
							report.setReportFileName("CustomerGroupWiseConsolidatedSalesReport");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("customer_group_wise_consolidated_sales_report"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer_group"));
							map.put("PAID_AMOUNT_LABEL", getPropertyName("amount_paid"));
							map.put("PENDING_AMOUNT_LABEL", getPropertyName("pending_amount"));
							map.put("INVOICE_AMOUNT_LABEL", getPropertyName("invoice_amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("group")+" : "
										+ customerGroupComboField
												.getItemCaption(customerGroupComboField
														.getValue()) + "\t";
							}

							subHeader += " \t "+getPropertyName("from")+" : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t "+getPropertyName("to")+" : "
									+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue());

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(salesModelList, map);

							salesModelList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
//					SalesModel salesModel = null;
//					CustomerGroupModel group ;
					Object[] row;

					try{
					List repList = getSalesReportList();
					List beanList=new ArrayList();
					
					SalesReportBean bean;

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
					
					if (repList != null && repList.size() > 0) {
						
						for (int i = 0; i < repList.size(); i++) {
							bean=(SalesReportBean) repList.get(i);
							
							row = new Object[] {i + 1,bean.getGroupId(),bean.getGroup(),bean.getAmount(),bean.getPayment_amt(),bean.getPending()};
								
								ttl += bean.getAmount();
								
								table.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));
								
								table.addItem(row, i+1);
						}
						
					}else
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					
					
					
					
					
					
					
					
					
					
//					if (repList != null && repList.size() > 0) {
//						for (int i = 0; i < repList.size(); i++) {
//							salesModel = (SalesModel) repList.get(i);
//							
//							group=custDao.getCustomerGroupModelFromLedger(salesModel.getCustomer().getId());
//
//							try {
//								bean=new SalesReportBean(group.getId(),group.getName(),roundNumber(salesModel.getAmount()),
//										roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()),
//										roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment())));
//								
//								beanList.add(bean);
//								
//								
//							} catch (Exception e) {
//							}
//
//						}
//						
//						
//						Collections.sort(beanList, new Comparator<SalesReportBean>() {
//							@Override
//							public int compare(final SalesReportBean object1,
//									final SalesReportBean object2) {
//								int result = asString(object1.getGroupId()).compareTo(asString(object2.getGroupId()));
//								return result;
//							}
//						});
//						
//						
//						
//						long prevId=0;
//						double invAmount=0;
//						double paidAmount=0;
//						double pendingAmount=0;
//						for (int i = 0; i < beanList.size(); i++) {
//							
//							bean=(SalesReportBean) beanList.get(i);
//							
//							
//							if(bean.getGroupId()!=prevId){
//								
//								row = new Object[] {i + 1,bean.getGroupId(),bean.getGroup(),invAmount,paidAmount,pendingAmount};
//								
//								invAmount=roundNumber(salesModel.getAmount());
//								paidAmount=roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment());
//								pendingAmount=roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()));
//								
//								prevId=bean.getGroupId();
//								
//								table.addItem(row, table.getItemIds().size()+1);
//								
//							}else{
//								invAmount+=roundNumber(salesModel.getAmount());
//								paidAmount+=roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment());
//								pendingAmount+=roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()));
//							}
//							
//							row = new Object[] {i + 1,bean.getGroup(),roundNumber(salesModel.getAmount()),
//								roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()),
//								roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()))};
//						
//							
//							ttl += salesModel.getAmount();
//						
//						}
//						table.setColumnFooter(TBC_AMOUNT,
//								asString(roundNumber(ttl)));
//
//					} else
//						SNotification.show(getPropertyName("no_data_available"),
//								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

//			final Action actionDelete = new Action(getPropertyName("edit"));
//			
//			table.addActionHandler(new Handler() {
//				
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					try{
//						if (table.getValue() != null) {
//							Item item = table.getItem(table.getValue());
//							SalesNewUI sales = new SalesNewUI();
//							sales.setCaption(getPropertyName("sales"));
//							sales.getSalesNumberList().setValue(
//									(Long) item.getItemProperty(TBC_ID).getValue());
//							sales.center();
//							getUI().getCurrent().addWindow(sales);
//							sales.addCloseListener(closeListener);
//						}
//					}
//					catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//				
//				@Override
//				public Action[] getActions(Object target, Object sender) {
//					return new Action[] { actionDelete };
//				}
//			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							popupContainer.removeAllComponents();
							SFormLayout form=new SFormLayout();
							form.addComponent(detailPopupContainer);
							form.addComponent(new SHTMLLabel(null,"<b>Group : "+itm.getItemProperty(TBC_CUSTOMER).getValue().toString()+"</b>"));
							form.addComponent(popTable);
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
							
							popTable.removeAllItems();
							popTable.setColumnFooter(POP_TBC_AMOUNT,
									asString(roundNumber(0)));
							popTable.setColumnFooter(POP_TBC_PAYMENT,
								asString(roundNumber(0)));
							popTable.setColumnFooter(POP_TBC_PENDING,
								asString(roundNumber(0)));
							
							String condition1="";
							if ((Integer) filterTypeRadio.getValue() == 1) {
								condition1 = " and payment_amount=amount ";
							} else if ((Integer) filterTypeRadio.getValue() == 2) {
								condition1 = " and payment_amount<amount ";
							}

							if ((Integer) statusRadioButton.getValue() == 0) {
								condition1 += " and active=true ";
							} else if ((Integer) statusRadioButton.getValue() == 1) {
								condition1 += " and active=false ";
							}

							List<Object> salesModelList =dao
										.getCustomerGroupWiseSalesDetails(id, getOrganizationID(),toLong(officeComboField.getValue().toString())
												, CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
												CommonUtil.getSQLDateFromUtilDate(toDateField
														.getValue()), condition1);
							Object[] row;
							SalesModel salesModel;
							double totalAmount=0;
							double totalPaid=0;
							double totalPending=0;
							for (int i = 0; i < salesModelList.size(); i++) {
								salesModel = (SalesModel) salesModelList.get(i);
								row = new Object[] {
										i + 1,salesModel.getCustomer().getName(),salesModel.getSales_number() + "",
										salesModel.getDate().toString(),userDao.getUserFromLogin(salesModel.getResponsible_employee()).getFirst_name(),									
										roundNumber(salesModel.getAmount()),
										roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()),
										roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()))};
								
								popTable.addItem(row, salesModel.getId());
								totalAmount+=roundNumber(salesModel.getAmount());
								totalPaid+=roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment());
								totalPending+=roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()));
								
							}
							popTable.setColumnFooter(POP_TBC_AMOUNT,
										asString(roundNumber(totalAmount)));
							popTable.setColumnFooter(POP_TBC_PAYMENT,
									asString(roundNumber(totalPaid)));
							popTable.setColumnFooter(POP_TBC_PENDING,
									asString(roundNumber(totalPending)));
							
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			popTable.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (popTable.getValue() != null) {
//							Item itm = popTable.getItem(popTable.getValue());
//							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesModel sale=salDao.getSale((Long)popTable.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("group"),custDao.getCustomerGroupNameFromLedger(sale.getCustomer().getId())));
							form.addComponent(new SLabel(getPropertyName("sales_man"),userDao.getUserFromLogin(sale.getResponsible_employee()).getFirst_name()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							
							form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
							form.addComponent(new SLabel(getPropertyName("amount_paid"),sale.getPayment_amount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("unit_price")), 4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
							grid.setSpacing(true);
							
							int i = 1;
							SalesInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(SalesInventoryDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
								grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																	- invObj.getDiscount() 
																	+ invObj.getTaxAmount())+ ""), 6, i);
								i++;
							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
							form.setStyleName("grid_max_limit");
							detailPopupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							detailPopupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			final Action actionEdit = new Action(getPropertyName("edit"));
			
			popTable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (popTable.getValue() != null) {
							Item item = popTable.getItem(popTable.getValue());
							SalesNewUI sales = new SalesNewUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getSalesNumberList().setValue(
									(Long) popTable.getValue());
							sales.center();
							getUI().getCurrent().addWindow(sales);
							sales.addCloseListener(closeListener);
							popupContainer.removeAllComponents();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionEdit };
				}
			});
			

			officeComboField.setValue(getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}


	protected List<Object> getSalesReportList() {
		long custId = 0;

		if (customerGroupComboField.getValue() != null
				&& !customerGroupComboField.getValue().equals("")) {
			custId = toLong(customerGroupComboField.getValue().toString());
		}

		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and a.payment_amount=amount ";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and a.payment_amount<amount ";
		}

		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and a.active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and a.active=false ";
		}

		List<Object> salesModelList = null;
		try {
			salesModelList = dao
					.getCustomerGroupWiseConsolidatedSalesDetails(custId, getOrganizationID(),toLong(officeComboField.getValue().toString())
							, CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), condition1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
	}

	
	private void loadCustomerGroups(long officeID) {
		List<Object> customerList;
		try {
			customerList = ledDao.getAllCustomerGroups(officeID);

			CustomerGroupModel ledgerModel = new CustomerGroupModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			
			customerGroupComboField.setContainerDataSource(SCollectionContainer.setList(customerList, "id"));
			customerGroupComboField.setItemCaptionPropertyId("name");
			customerGroupComboField.setValue((long)0);
			
		} catch (Exception e) {
				e.printStackTrace();
			}
	}


	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
