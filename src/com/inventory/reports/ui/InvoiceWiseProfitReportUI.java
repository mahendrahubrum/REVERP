package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.dao.InvoiceWiseProfitReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
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
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 4, 2014
 */
public class InvoiceWiseProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = 4359538592329604909L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField invoiceComboField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private long customerId;

	private Report report;

	LedgerDao ledDao;

	private SalesDao saleDao;
	private ItemDao itemDao;
	private InvoiceWiseProfitReportDao dao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_BILL = "Invoice No";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_SALES = "Sale";
	static String TBC_PURCHASE = "Purchase";
	static String TBC_PROFIT = "Profit";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	@Override
	public SPanel getGUI() {
		
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_BILL,TBC_CUSTOMER, TBC_SALES,TBC_PURCHASE,TBC_PROFIT };
		visibleColumns = new Object[]  { TBC_SN, TBC_BILL,TBC_CUSTOMER, TBC_SALES,TBC_PURCHASE,TBC_PROFIT };
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("invoice_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALES, Double.class, null,getPropertyName("sale"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE, Double.class, null,getPropertyName("purchase"), null, Align.RIGHT);
		table.addContainerProperty(TBC_PROFIT, Double.class, null,getPropertyName("profit"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_CUSTOMER, 2);
		table.setColumnExpandRatio(TBC_SALES, (float) 1);
		table.setColumnExpandRatio(TBC_PURCHASE, (float) 1);
		table.setColumnExpandRatio(TBC_PROFIT, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		
		ledDao = new LedgerDao();
		saleDao = new SalesDao();
		itemDao = new ItemDao();
		dao = new InvoiceWiseProfitReportDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1050, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

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

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			invoiceComboField = new SComboField(getPropertyName("invoice_no"),
					200);
			invoiceComboField
					.setInputPrompt(getPropertyName("all"));
			mainFormLayout.addComponent(invoiceComboField);

			itemsComboField = new SComboField(getPropertyName("item"), 200);
			itemsComboField
					.setInputPrompt(getPropertyName("all"));
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
			mainPanel.setContent(mainHorizontal);


			Listener listener = new Listener() {
				@SuppressWarnings("unchecked")
				@Override
				public void componentEvent(Event event) {

					try {
						List<Object> saleList = dao
								.getAllSalesNumbersAsComment(
										(Long) officeComboField.getValue(),
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));
						SalesModel saleMdl = new SalesModel();
						saleMdl.setId(0);
						saleMdl.setSales_number(getPropertyName("all"));
						if (saleList == null) {
							saleList = new ArrayList<Object>();
						}
						saleList.add(0, saleMdl);

						SCollectionContainer bic2 = SCollectionContainer
								.setList(saleList, "id");
						invoiceComboField.setContainerDataSource(bic2);
						invoiceComboField.setItemCaptionPropertyId("sales_number");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			fromDateField.setImmediate(true);
			toDateField.setImmediate(true);

			toDateField.addListener(listener);
			fromDateField.addListener(listener);

			organizationComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
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
								e.printStackTrace();
							}
						}
					});

			officeComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								List<Object> saleList = dao.getAllSalesNumbersAsComment(
										(Long) officeComboField.getValue(),
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));
								SalesModel saleMdl = new SalesModel();
								saleMdl.setId(0);
								saleMdl.setSales_number(getPropertyName("all"));
								if (saleList == null) {
									saleList = new ArrayList<Object>();
								}
								saleList.add(0, saleMdl);

								SCollectionContainer bic2 = SCollectionContainer
										.setList(saleList, "id");
								invoiceComboField.setContainerDataSource(bic2);
								invoiceComboField
										.setItemCaptionPropertyId("sales_number");

								List itemsList = itemDao
										.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
												.getValue());
								ItemModel salesModel = new ItemModel(0,
										getPropertyName("all"));
								if (itemsList == null) {
									itemsList = new ArrayList<Object>();
								}
								itemsList.add(0, salesModel);

								SCollectionContainer bic1 = SCollectionContainer
										.setList(itemsList, "id");
								itemsComboField.setContainerDataSource(bic1);
								itemsComboField
										.setItemCaptionPropertyId("name");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			invoiceComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {

						List itemsList = null;
						if (invoiceComboField.getValue() != null
								&& !invoiceComboField.getValue().equals("")
								&& !invoiceComboField.getValue().toString()
										.equals("0")) {

							itemsList = dao
									.getAllActiveItemsUnderSaleNumber((Long) invoiceComboField
											.getValue());

						} else {
							itemsList = itemDao
									.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
											.getValue());
						}

						ItemModel salesModel = new ItemModel(0,
								getPropertyName("all"));
						if (itemsList == null) {
							itemsList = new ArrayList<Object>();
						}
						itemsList.add(0, salesModel);

						SCollectionContainer bic1 = SCollectionContainer
								.setList(itemsList, "id");
						itemsComboField.setContainerDataSource(bic1);
						itemsComboField.setItemCaptionPropertyId("name");

					} catch (Exception e) {
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

			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action(getPropertyName("edit"));
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SalesNewUI option=new SalesNewUI();
							option.setCaption(getPropertyName("sales"));
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
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
							SalesModel mdl=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("sale"),item.getItemProperty(TBC_SALES).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("purchase"),item.getItemProperty(TBC_PURCHASE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("profit"),item.getItemProperty(TBC_PROFIT).getValue().toString()));
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
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							List<Object> reportList;

							long itemID = 0;
							long invId = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (invoiceComboField.getValue() != null
									&& !invoiceComboField.getValue().equals("")) {
								invId = toLong(invoiceComboField.getValue()
										.toString());
							}

							reportList = dao.getInvoiceWiseProfitReport(
									itemID,
									invId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getEmployee(),
											bean.getClient_name(),
											bean.getInwards(),
											bean.getOutwards(),
											bean.getProfit()},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							
							table.setVisibleColumns(visibleColumns);
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
			
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List<Object> reportList;

							long itemID = 0;
							long invId = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (invoiceComboField.getValue() != null
									&& !invoiceComboField.getValue().equals("")) {
								invId = toLong(invoiceComboField.getValue()
										.toString());
							}

							reportList = dao.getInvoiceWiseProfitReport(
									itemID,
									invId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("InvoiceWiseProfitReport");
								report.setReportFileName("InvoiceWiseProfitReport");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("invoice_wise_profit_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
								map.put("INVOICE_NO_LABEL", getPropertyName("invoice_no"));
								map.put("SALE_AMOUNT_LABEL", getPropertyName("sale_amount"));
								map.put("PURCHASE_AMOUNT_LABEL", getPropertyName("purchase_amount"));
								map.put("PROFIT_LABEL", getPropertyName("profit"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("invoice_no")+" : "
											+ invoiceComboField
													.getItemCaption(invoiceComboField
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

							} 
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
