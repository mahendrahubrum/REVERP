package com.inventory.tailoring.report.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.tailoring.report.dao.TailoringCustomerProfitReportDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
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
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Dec 23 2014
 */
public class TailoringCustomerProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	LedgerDao ledDao;

	TailoringCustomerProfitReportDao daoObj;
	
	static String TBC_SN = "SN";
	static String TBC_SID = "SID";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_PAMOUNT = "Purchase Amount";
	static String TBC_SAMOUNT = "Sale Amount";
	static String TBC_LOSS = "Profit";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	

	@Override
	public SPanel getGUI() {
		
		ledDao = new LedgerDao();
		
		daoObj = new TailoringCustomerProfitReportDao();

		allColumns = new Object[] { TBC_SN, TBC_SID,TBC_CUSTOMER,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		visibleColumns = new Object[]{ TBC_SN, TBC_CUSTOMER,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton("Show");
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,TBC_CUSTOMER, null, Align.LEFT);
		table.addContainerProperty(TBC_SAMOUNT, Double.class, null,TBC_SAMOUNT, null, Align.LEFT);
		table.addContainerProperty(TBC_PAMOUNT, Double.class, null,TBC_PAMOUNT, null, Align.LEFT);
		table.addContainerProperty(TBC_LOSS, Double.class, null,TBC_LOSS, null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 2);
		table.setColumnExpandRatio(TBC_SAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_LOSS, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
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

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, "ALL");

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(customerComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			mainPanel.setContent(mainHorizontal);

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
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerComboField
										.getValue().toString());
							}
						}
					});

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
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
			
			
			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList;

						long custId = 0;

						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						reportList = daoObj.getSalesProfitDetails(custId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
								toLong(officeComboField.getValue().toString()));
						if(reportList.size()>0){
							ReportBean bean=null;
							Iterator itr=reportList.iterator();
							while(itr.hasNext()){
								bean=(ReportBean)itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getId(),
										bean.getClient_name(),
										bean.getInwards(),
										bean.getOutwards(),
										bean.getProfit()},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show("No Data Available",Type.WARNING_MESSAGE);
						}
						
						table.setVisibleColumns(visibleColumns);

					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList;

						long custId = 0;

						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						reportList = daoObj.getSalesProfitDetails(custId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
								toLong(officeComboField.getValue().toString()));

						if (reportList.size() > 0) {
							report.setJrxmlFileName("TailoringCustomerWiseProfit_Report");
							report.setReportFileName("Customer Wise Profit Report");
							report.setReportTitle("Customer Wise Profit Report");

							String subHeader = "";

							if (customerComboField.getValue() != null)
								if (!customerComboField.getValue().toString()
										.equals("0"))
									subHeader += "Customer : "
											+ customerComboField
													.getItemCaption(customerComboField
															.getValue()) + "\t";

							subHeader += "\n From : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t To : "
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
							report.createReport(reportList, null);

							reportList.clear();

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

}
