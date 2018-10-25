package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.PurchaseReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.model.SalesInquiryDetailsModel;
import com.inventory.sales.model.SalesInquiryModel;
import com.inventory.sales.ui.SalesInquiryUI;
import com.vaadin.data.Item;
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
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         May 21, 2014
 */

public class SalesInquiryReportUI extends SparkLogic {

	private static final long serialVersionUID = -4662121877669280864L;

	private SOfficeComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField salesInquiryNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	SLabel totalLabel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	private CollectionContainer container;
	private CollectionContainer suppContainer;

	private long supplierid;

	private Report report;

	LedgerDao ledDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_QUOTATION_NO = "Inquiry No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";

	private STable table;

	private String[] allColumns;
	private String[] visibleColumns;

	private SalesReportDao salesReportDao;

	private HashMap<Long, String> currencyHashMap;

	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();
		salesReportDao = new SalesReportDao();

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_QUOTATION_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleColumns = new String[] { TBC_SN, TBC_QUOTATION_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };

		totalLabel = new SLabel(null, "0.0");

		supplierid = 0;
		report = new Report(getLoginID());

		setSize(1100, 380);
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

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);
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

		try {
			List<Object> customerList = ledDao.getAllCustomers(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("----- "+getPropertyName("all")+" -----");
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(getPropertyName("customer"),
					200, customerList, "id", "name", false, "----- "+getPropertyName("all")+" -----");
			mainFormLayout.addComponent(customerComboField);
			
			List list =salesReportDao
					.getAllSalesInquiryForOffice(getOfficeID(),
							CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
			salesInquiryNoComboField = new SComboField(
					getPropertyName("inquiry_no"), 200,list , "id",
					"inquiry_no", false, "----- "+getPropertyName("all")+" -----");
			mainFormLayout.addComponent(salesInquiryNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 700, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_QUOTATION_NO, String.class, null,
					getPropertyName("inquiry_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, String.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_ITEMS, String.class, null,
					getPropertyName("items"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 1);
			table.setColumnExpandRatio(TBC_QUOTATION_NO, 0.9f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_ITEMS, 3f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(new SVerticalLayout(true, table,
					new SHorizontalLayout(true, new SLabel(null, 200),
							new SLabel(getPropertyName("total_amount"), 120),
							totalLabel)));

			mainPanel.setContent(mainLay);

			salesInquiryNoComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
							totalLabel.setValue("0.0");
						}
					});

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							supplierid = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								supplierid = toLong(customerComboField
										.getValue().toString());
							}
							loadBillNo(supplierid, toLong(officeComboField
									.getValue().toString()));
						}
					});

			fromDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadSupplierCombo(toLong(officeComboField.getValue()
							.toString()));
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesInquiryModel inquiryModel = null;
						SalesInquiryDetailsModel inquiryDetailsModel = null;
						PurchaseReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();
						List<Object> inquiryModelList = getSalesInquiryReportList();
						double amount;
						for (int i = 0; i < inquiryModelList.size(); i++) {
							noData = false;
							inquiryModel = (SalesInquiryModel) inquiryModelList
									.get(i);
							List<SalesInquiryDetailsModel> detailsList = inquiryModel
									.getSales_inquiry_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inquiryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inquiryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inquiryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inquiryDetailsModel.getUnit_price()
										+ " ) ";
							}
							amount = inquiryModel.getAmount() / inquiryModel.getConversionRate();
							if(inquiryModel.getCurrencyId() == getCurrencyID()){
								reportBean = new PurchaseReportBean(inquiryModel
										.getDate().toString(), inquiryModel
										.getCustomer().getName(), 
										inquiryModel.getInquiry_no(),
										inquiryModel.getOffice().getName(), items,
										inquiryModel.getAmount());
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID()));
							} else {
								reportBean = new PurchaseReportBean(inquiryModel
										.getDate().toString(), inquiryModel
										.getCustomer().getName(), 
										inquiryModel.getInquiry_no(),
										inquiryModel.getOffice().getName(), items,
										roundNumber(amount));
							
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID())+
										" ("+inquiryModel.getAmount()+" "+getCurrencyDescription(inquiryModel.getCurrencyId())+")");
							}
							
							reportList.add(reportBean);

						}

						if (!noData) {
						
							HashMap<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("SL_NO_LABEL", getPropertyName("sl_no"));
							parameters.put("DATE_LABEL", getPropertyName("date"));
							parameters.put("BILL_NO_LABEL", getPropertyName("inquiry_no"));
							parameters.put("SUPPLIER_LABEL", getPropertyName("customer"));
							parameters.put("AMOUNT_LABEL", getPropertyName("amount"));
							parameters.put("ITEM_LABEL", getPropertyName("items"));
							
							report.setJrxmlFileName("Purchase_Report");
							report.setReportFileName("Sales Inquiry Report");
							report.setReportTitle(getPropertyName("sales_inquiry_report"));
							String subHeader = "";
							if (supplierid != 0) {
								subHeader +=  getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (salesInquiryNoComboField.getValue() != null
									&& !salesInquiryNoComboField.getValue().equals(
											"")
									&& !salesInquiryNoComboField.getValue()
											.toString().equals("0")) {
								subHeader += getPropertyName("inquiry_no")+" : "
										+ salesInquiryNoComboField
												.getItemCaption(salesInquiryNoComboField
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
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, parameters);

							reportList.clear();
							inquiryModelList.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					SalesInquiryModel inquiryModel = null;
					SalesInquiryDetailsModel inquiryDetailsModel = null;
					String items = "";
					Object[] row;

					List repList = getSalesInquiryReportList();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					totalLabel.setValue("0.0");
					double ttl = 0;
					double amount;
					if (repList != null && repList.size() > 0) {
						List<SalesInquiryDetailsModel> detailsList;
						for (int i = 0; i < repList.size(); i++) {
							inquiryModel = (SalesInquiryModel) repList.get(i);
							detailsList = inquiryModel
									.getSales_inquiry_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inquiryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inquiryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inquiryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inquiryDetailsModel.getUnit_price()
										+ " ) ";
							}
							amount = inquiryModel.getAmount() / inquiryModel.getConversionRate();
							if(inquiryModel.getCurrencyId() == getCurrencyID()){
								row = new Object[] {
										i + 1,
										inquiryModel.getId(),
										inquiryModel.getInquiry_no()
												+ "",
										CommonUtil
												.formatDateToCommonDateTimeFormat(inquiryModel
														.getDate()),
										inquiryModel.getCustomer().getName(),
										inquiryModel.getAmount()+" "+getCurrencyDescription(getCurrencyID()),
										items };								
							} else {
								row = new Object[] {
										i + 1,
										inquiryModel.getId(),
										inquiryModel.getInquiry_no()
												+ "",
										CommonUtil
												.formatDateToCommonDateTimeFormat(inquiryModel
														.getDate()),
										inquiryModel.getCustomer().getName(),
										roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())+
										" ("+inquiryModel.getAmount()+" "+getCurrencyDescription(inquiryModel.getCurrencyId())+")",
										items };			
								
							}							
							table.addItem(row, i + 1);
							ttl += roundNumber(amount);
						}
						totalLabel.setValue(asString(roundNumber(ttl)));

					} else
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
				}
			});

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

final Action actionDelete = new Action("Edit");
			
			
			table.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SalesInquiryUI sales = new SalesInquiryUI();
							sales.setCaption("Sales Inquiry");
							sales.getBillNoFiled().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							//popUp.setPopupVisible(false);
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
			officeComboField.setValue(getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	private String getCurrencyDescription(long currencyId) {
		if(currencyHashMap == null){
			currencyHashMap = new HashMap<Long, String>();
			try {
				List list = new CurrencyManagementDao().getCurrencySymbol();
				Iterator<CurrencyModel> itr = list.iterator();
				while(itr.hasNext()){
					CurrencyModel model = itr.next();
					currencyHashMap.put(model.getId(), model.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return currencyHashMap.get(currencyId);
	}
	protected List<Object> getSalesInquiryReportList() {
		long salesInquiryId = 0;
		long supplierId = 0;

		if (salesInquiryNoComboField.getValue() != null
				&& !salesInquiryNoComboField.getValue().equals("")
				&& !salesInquiryNoComboField.getValue().toString().equals("0")) {
			salesInquiryId = toLong(salesInquiryNoComboField.getValue().toString());
		}
		if (customerComboField.getValue() != null
				&& !customerComboField.getValue().equals("")) {
			supplierId = toLong(customerComboField.getValue().toString());
		}

		List<Object> inquiryModelList = null;
		try {
			inquiryModelList = salesReportDao
					.getSalesInquiryDetails(salesInquiryId, supplierId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inquiryModelList;
	}

	@SuppressWarnings("unchecked")
	private void loadBillNo(long supplierId, long officeId) {
		List<SalesInquiryModel> salesInquiryBillList = null;
		try {

			if (supplierId != 0) {
				salesInquiryBillList = salesReportDao
						.getAllSalesInquiryForCustomer(supplierId, officeId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
			} else {
				salesInquiryBillList = salesReportDao
						.getAllSalesInquiryForOffice(officeId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
			}

			SalesInquiryModel inquiryModel = new SalesInquiryModel();
			inquiryModel.setId(0);
			inquiryModel
					.setInquiry_no("----- "+getPropertyName("all")+" -----");
			if (salesInquiryBillList == null) {
				salesInquiryBillList = new ArrayList<SalesInquiryModel>();
			}
			salesInquiryBillList.add(0, inquiryModel);
			container = CollectionContainer.fromBeans(salesInquiryBillList, "id");
			salesInquiryNoComboField.setContainerDataSource(container);
			salesInquiryNoComboField.setItemCaptionPropertyId("inquiry_no");
			salesInquiryNoComboField.setValue(0);

			table.removeAllItems();
			totalLabel.setValue("0.0");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void loadSupplierCombo(long officeId) {
		List<Object> suppList = null;
		try {
			if (officeId != 0) {
				suppList = ledDao.getAllSuppliers(officeId);
			} else {
				suppList = ledDao.getAllSuppliersFromOrgId(getOrganizationID());
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("----- "+getPropertyName("all")+" -----");
			if (suppList == null) {
				suppList = new ArrayList<Object>();
			}
			suppList.add(0, ledgerModel);
			suppContainer = CollectionContainer.fromBeans(suppList, "id");
			customerComboField.setContainerDataSource(suppContainer);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(0);

			table.removeAllItems();
			totalLabel.setValue("0.0");

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
