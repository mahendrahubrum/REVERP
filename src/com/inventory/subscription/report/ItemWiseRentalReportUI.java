package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.subscription.bean.ItemWiseRentalReportBean;
import com.inventory.subscription.dao.ItemWiseRentalReportDao;
import com.inventory.subscription.dao.RentalTransactionNewDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.RentalTransactionModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.ui.RentalTransactionNewUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/****
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 28, 2015
 */

@SuppressWarnings("serial")
public class ItemWiseRentalReportUI extends SparkLogic {


	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;
	ItemWiseRentalReportDao dao;
	LedgerDao ledDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_QUANTITY = "Qty";
	static String TBC_RATE = "Rate";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_TOTAL = "Total Qty";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	SRadioButton accountRadio;
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_ITEM, TBC_QUANTITY,TBC_RATE, TBC_CUSTOMER, TBC_TOTAL};
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_ITEM, TBC_QUANTITY,TBC_RATE, TBC_CUSTOMER, TBC_TOTAL};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		dao=new ItemWiseRentalReportDao();
		ledDao = new LedgerDao();

		customerId = 0;
		report = new Report(getLoginID());
		accountRadio = new SRadioButton(getPropertyName("subscriber"), 200, SConstants.accountList, "key", "value");
		setSize(1050, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
//		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		// officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		try {
			table = new STable(null, 650, 250);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
			table.addContainerProperty(TBC_QUANTITY, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
			table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("rate"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_TOTAL, Double.class, null,getPropertyName("total"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
			table.setColumnExpandRatio(TBC_ITEM, 2);
			table.setColumnExpandRatio(TBC_QUANTITY, (float) 0.5);
			table.setColumnExpandRatio(TBC_RATE, (float) 0.6);
			table.setColumnExpandRatio(TBC_CUSTOMER, 2);
			table.setColumnExpandRatio(TBC_TOTAL, (float) 1);
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setVisibleColumns(visibleColumns);
			
			
			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);
			mainFormLayout.addComponent(accountRadio);
			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);

			itemsComboField = new SComboField(getPropertyName("rental_item"), 200,
					null, "id", "name", false, "ALL");
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			mainHorizontal.setSpacing(true);
			mainPanel.setContent(mainHorizontal);
			
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(accountRadio.getValue()!=null) {
							if(toLong(accountRadio.getValue().toString())==1){
								loadSubscriberIncome(0,(Long)officeComboField.getValue());
								customerComboField.setCaption(getPropertyName("customer"));
								table.setColumnHeader(TBC_CUSTOMER, getPropertyName("customer"));
							}
							else{
								loadSubscriberTransportation(0,(Long)officeComboField.getValue());
								customerComboField.setCaption(getPropertyName("transportation"));
								table.setColumnHeader(TBC_CUSTOMER, getPropertyName("transportation"));
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			organizationComboField
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								SCollectionContainer bic = SCollectionContainer.setList(
										new OfficeDao()
												.getAllOfficeNamesUnderOrg((Long) organizationComboField
														.getValue()), "id");
								officeComboField.setContainerDataSource(bic);
								officeComboField
										.setItemCaptionPropertyId("name");

								Iterator it = officeComboField.getItemIds()
										.iterator();
								if (it.hasNext())
									officeComboField.setValue(it.next());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			officeComboField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						List itemsList = new SubscriptionCreationDao().getAllSubscriptions(getOfficeID(), (long)0 );
						SubscriptionCreationModel salesModel = new SubscriptionCreationModel(0,getPropertyName("all"));
						if (itemsList == null) {
							itemsList = new ArrayList<Object>();
						}
						itemsList.add(0, salesModel);
						SCollectionContainer bic1 = SCollectionContainer.setList(itemsList, "id");
						itemsComboField.setContainerDataSource(bic1);
						itemsComboField.setItemCaptionPropertyId("name");
						accountRadio.setValue(null);
						accountRadio.setValue((long)1);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}

			organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());
			accountRadio.setValue((long)1);
			loadSubscriberIncome(0, (Long)officeComboField.getValue());
			itemsComboField.setValue((long)0);

			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action("Edit");
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							RentalTransactionNewUI sales = new RentalTransactionNewUI();
							sales.setCaption(getPropertyName("rental"));
							sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							sales.center();
							getUI().getCurrent().addWindow(sales);
							sales.addCloseListener(closeListener);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionDelete };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							RentalTransactionModel mdl=new RentalTransactionNewDao().getRentalTransactionModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("rental")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("quantity"),item.getItemProperty(TBC_QUANTITY).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("rate"),item.getItemProperty(TBC_RATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("total"),item.getItemProperty(TBC_TOTAL).getValue().toString()));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			showButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						if (officeComboField.getValue() != null) {

							List reportList;

							long itemID = 0;
							long custId = 0;
							table.setVisibleColumns(allColumns);
							if (itemsComboField.getValue() != null && 
								!itemsComboField.getValue().equals("") && 
								!itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (customerComboField.getValue() != null && 
								!customerComboField.getValue().equals("")) {
								custId = toLong(customerComboField.getValue().toString());
							}

							reportList = dao.getItemWiseRentalReport(	(Long)officeComboField.getValue(),
																		(Long)customerComboField.getValue(),
																		(Long)itemsComboField.getValue(),
																		CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																		CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));

							if(reportList.size()>0){
								ItemWiseRentalReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ItemWiseRentalReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getDate(),
											bean.getItem(),
											bean.getQuantity(),
											bean.getRate(),
											bean.getLedger(),
											bean.getTotal()},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show("No Data Available",Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
							setRequiredError(officeComboField, null, false);
						} 
						else {
							setRequiredError(officeComboField,getPropertyName("invalid_selection"), true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List reportList;

							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.equals("")) {
								custId = toLong(customerComboField.getValue()
										.toString());
							}

							reportList = dao.getItemWiseRentalReport(	(Long)officeComboField.getValue(),
									(Long)customerComboField.getValue(),
									(Long)itemsComboField.getValue(),
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));

							if (reportList.size() > 0) {

								/*
								 * Collections.sort(reportList, new
								 * Comparator<ReportBean>() {
								 * 
								 * @Override public int compare( final
								 * ReportBean object1, final ReportBean object2)
								 * {
								 * 
								 * int result = object1 .getDt() .compareTo(
								 * object2.getDt()); if (result == 0) { result =
								 * object1 .getItem_name() .toLowerCase()
								 * .compareTo( object2.getItem_name()
								 * .toLowerCase()); } return result; }
								 * 
								 * });
								 */
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("ItemWiseRentalReport");
								report.setReportFileName("Item Wise Rental Report");
//								report.setReportTitle("ItemWise Sales Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_rental_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
								map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
								map.put("RATE_LABEL", getPropertyName("rate"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								map.put("CURRENT_STOCK_LABEL", getPropertyName("current_stock"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ customerComboField
													.getItemCaption(customerComboField
															.getValue()) + "\t";
								}
								if (itemID != 0) {
									subHeader += getPropertyName("item")+" : "
											+ itemsComboField
													.getItemCaption(itemsComboField
															.getValue());
								}

								subHeader += "\n "+getPropertyName("from")+" : "
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
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, map);

								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			if (officeId != 0) {
				custList = ledDao.getAllCustomers(officeId);
			} else {
				custList = ledDao.getAllCustomers();
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			customerComboField.setContainerDataSource(custContainer);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(0);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberIncome(long id, long office){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.add(0, new SubscriptionCreationModel(0, getPropertyName("all")));
			list.addAll(dao.getAllIncomeSubscriptions(office));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			customerComboField.setContainerDataSource(bic);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberTransportation(long id, long office){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.add(0, new SubscriptionCreationModel(0, getPropertyName("all")));
			list.addAll(dao.getAllTransportationSubscriptions(office));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			customerComboField.setContainerDataSource(bic);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
