package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.CustomerProfitReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
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
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Nov 24, 2013
 */
public class EmployeeWiseProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField employeeCombo;
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

	CustomerProfitReportDao daoObj;
	
	static String TBC_SN = "SN";
	static String TBC_SID = "SID";
	static String TBC_CUSTOMER = "Employee";
	static String TBC_PAMOUNT = "Purchase Amount";
	static String TBC_SAMOUNT = "Sale Amount";
	static String TBC_EXPENDETURE = "Expendeture";
	static String TBC_TRANSPORTAION = "Transportaion";
	static String TBC_LOSS = "Profit";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings({ "deprecation", "serial" })
	@Override
	public SPanel getGUI() {
		
		allColumns = new Object[] { TBC_SN, TBC_SID,TBC_CUSTOMER,TBC_SAMOUNT, TBC_PAMOUNT, TBC_TRANSPORTAION,TBC_EXPENDETURE, TBC_LOSS};
		visibleColumns = new Object[]{ TBC_SN, TBC_CUSTOMER,TBC_SAMOUNT, TBC_PAMOUNT, TBC_TRANSPORTAION,TBC_EXPENDETURE, TBC_LOSS};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("employee"), null, Align.LEFT);
		table.addContainerProperty(TBC_SAMOUNT, Double.class, null,getPropertyName("sale_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_PAMOUNT, Double.class, null,getPropertyName("purchase_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_TRANSPORTAION, Double.class, null,getPropertyName("transportation"), null, Align.LEFT);
		table.addContainerProperty(TBC_EXPENDETURE, Double.class, null,getPropertyName("expendeture"), null, Align.LEFT);
		table.addContainerProperty(TBC_LOSS, Double.class, null,getPropertyName("profit"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 2);
		table.setColumnExpandRatio(TBC_SAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_TRANSPORTAION, (float) 1.5);
		table.setColumnExpandRatio(TBC_EXPENDETURE, (float) 1.5);
		table.setColumnExpandRatio(TBC_LOSS, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);

		ledDao = new LedgerDao();

		daoObj = new CustomerProfitReportDao();

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

			employeeCombo = new SComboField(getPropertyName("employee"), 200, null, "id", "name", false, getPropertyName("all"));

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(employeeCombo);

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

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadEmployeeCombo(toLong(officeComboField.getValue()
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
			
			final Action actionSales = new Action(getPropertyName("edit"));
			
			/*table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SalesNewUI option=new SalesNewUI();
							option.setCaption("Sales");
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_SID).getValue());
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
					return new Action[] { actionSales };
				}
			});*/
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_SID).getValue();
							SalesModel mdl=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),mdl.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("sale_amount"),item.getItemProperty(TBC_SAMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("purchase_amount"),item.getItemProperty(TBC_PAMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("transportation"),item.getItemProperty(TBC_TRANSPORTAION).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("expendeture"),item.getItemProperty(TBC_EXPENDETURE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("profit"),item.getItemProperty(TBC_LOSS).getValue().toString()));
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
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList;

						long empId = 0;

						if (employeeCombo.getValue() != null
								&& !employeeCombo.getValue().equals("")) {
							empId = toLong(employeeCombo.getValue().toString());
						}

						reportList = daoObj.getEmployeeWiseProfitReport(empId,
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
										roundNumber(bean.getInwards()),
										roundNumber(bean.getOutwards()),
										roundNumber(bean.getBalance()),
										roundNumber(bean.getAmount()),
										roundNumber(bean.getProfit())},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show("No Data Available",Type.WARNING_MESSAGE);
						}
						
						table.setVisibleColumns(visibleColumns);
						
					} catch (Exception e) {
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

						long empId = 0;

						if (employeeCombo.getValue() != null
								&& !employeeCombo.getValue().equals("")) {
							empId = toLong(employeeCombo.getValue().toString());
						}

						reportList = daoObj.getEmployeeWiseProfitReport(empId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
								toLong(officeComboField.getValue().toString()));

						if (reportList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("EmployeeWiseProfit_Report");
							report.setReportFileName("Employee Wise Profit Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("employee_wise_profit_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("EMPLOYEE_LABEL", getPropertyName("employee"));
							map.put("SALE_AMOUNT_LABEL", getPropertyName("sale_amount"));
							map.put("PURCHASE_AMOUNT_LABEL", getPropertyName("purchase_amount"));
							map.put("PROFIT_LABEL", getPropertyName("profit"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							map.put("TRANSPORT_LABEL", getPropertyName("transportation"));
							map.put("EXPENSE_LABEL", getPropertyName("expendeture"));

							String subHeader = "";

							if (employeeCombo.getValue() != null)
								if (!employeeCombo.getValue().toString()
										.equals("0"))
									subHeader += getPropertyName("employee")+" : "
											+ employeeCombo
													.getItemCaption(employeeCombo
															.getValue()) + "\t";

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
							report.createReport(reportList, map);

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

	protected void loadEmployeeCombo(long officeId) {
		List<Object> custList = null;
		try {

			if (officeId != 0) {
				custList = new SalesManMapDao().getUsers(getOfficeID(),SConstants.SALES_MAN);
			}
			S_LoginModel ledgerModel = new S_LoginModel();
			ledgerModel.setId(0);
			ledgerModel
					.setLogin_name(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			employeeCombo.setContainerDataSource(custContainer);
			employeeCombo.setItemCaptionPropertyId("first_name");
			employeeCombo.setValue(0);

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
