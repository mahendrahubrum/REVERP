package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionSalesDetailsNewModel;
import com.inventory.commissionsales.model.CommissionSalesNewModel;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.CommissionSalesReportBean;
import com.inventory.reports.dao.ContainerSalesReportDao;
import com.vaadin.data.Item;
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
 *         Mar 26, 2014
 */
public class ContainerSalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -1126880982372191058L;

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

	private ContainerSalesReportDao dao;

	LedgerDao ledDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_BILL = "Sales No";
	static String TBC_DATE = "Date";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEM = "Items";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {
		
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DATE,TBC_CUSTOMER,TBC_BILL,TBC_ITEM,TBC_AMOUNT};
		visibleColumns = new Object[]{ TBC_SN, TBC_DATE,TBC_CUSTOMER,TBC_BILL,TBC_ITEM,TBC_AMOUNT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"), null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);
		table.addContainerProperty(TBC_BILL, Long.class, null, getPropertyName("sales_no"), null,Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, (float) 2);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		customerId = 0;
		report = new Report(getLoginID());

		ledDao = new LedgerDao();

		setSize(1000, 350);
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

			dao = new ContainerSalesReportDao();

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

			mainPanel.setContent(mainHorizontal);
			organizationComboField
					.addValueChangeListener(new ValueChangeListener() {
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

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						List<Object> customerList = ledDao
								.getAllCustomers((Long) officeComboField
										.getValue());
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel
								.setName(getPropertyName("all"));
						if (customerList == null) {
							customerList = new ArrayList<Object>();
						}
						customerList.add(0, ledgerModel);

						SCollectionContainer bic2 = SCollectionContainer
								.setList(customerList, "id");
						customerComboField.setContainerDataSource(bic2);
						customerComboField.setItemCaptionPropertyId("name");

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

			final Action actionDelete = new Action("Edit");
			
//			table.addActionHandler(new Handler() {
//				
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					try{
//						Item item = null;
//						if (table.getValue() != null) {
//							item = table.getItem(table.getValue());
//							CommissionSalesNewUI option=new CommissionSalesNewUI();
//							option.setCaption(getPropertyName("commission_sales"));
//							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
//							option.center();
//							getUI().getCurrent().addWindow(option);
//							option.addCloseListener(closeListener);
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
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("commission_sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("sales_no"),item.getItemProperty(TBC_BILL).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
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

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							List reportList;
							List resultList=new ArrayList();
							long custId = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.equals("")) {
								custId = toLong(customerComboField.getValue()
										.toString());
							}
							reportList = dao.getCommissionSalesReport(
									custId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());
							if(reportList.size()>0){
								CommissionSalesNewModel mdl=null;
								CommissionSalesDetailsNewModel detmdl=null;
								Iterator iter=reportList.iterator();
								while (iter.hasNext()) {
									
									mdl = (CommissionSalesNewModel) iter.next();
									String items="";
									Iterator it=mdl.getCommission_sales_list().iterator();
									while (it.hasNext()) {
										detmdl = (CommissionSalesDetailsNewModel) it.next();
										items+=detmdl.getItem().getName()+"("+detmdl.getQunatity()+detmdl.getUnit().getSymbol()+"), ";
									}
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											mdl.getId(),
											mdl.getDate().toString(),
											mdl.getCustomer().getName(),
											mdl.getSales_number(),
											items,
											mdl.getAmount()},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"),
									true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List reportList;
							List resultList=new ArrayList();

							long custId = 0;

							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.equals("")) {
								custId = toLong(customerComboField.getValue()
										.toString());
							}
							reportList = dao.getCommissionSalesReport(
									custId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());
							
							CommissionSalesNewModel salesNewModel;
							CommissionSalesDetailsNewModel salesDetMdl;

							if (reportList != null && reportList.size() > 0) {
								
								Iterator iter=reportList.iterator();
								while (iter.hasNext()) {
									
									salesNewModel = (CommissionSalesNewModel) iter.next();
									
									String items="";
									Iterator it=salesNewModel.getCommission_sales_list().iterator();
									while (it.hasNext()) {
										salesDetMdl = (CommissionSalesDetailsNewModel) it.next();
										items+=salesDetMdl.getItem().getName()+"("+salesDetMdl.getQunatity()+salesDetMdl.getUnit().getSymbol()+")";
									}
									
									
									resultList.add(
											new CommissionSalesReportBean(salesNewModel.getSales_number(),
													salesNewModel.getCustomer().getName(),
											salesNewModel.getDate(),
											salesNewModel.getAmount(), items));
								}
								 
								

								Collections.sort(resultList,
										new Comparator<CommissionSalesReportBean>() {
											@Override
											public int compare(
													final CommissionSalesReportBean object1,
													final CommissionSalesReportBean object2) {

												int result = object2
														.getIssue_date()
														.compareTo(
																object1.getIssue_date());
												return result;
											}

										});
								
								

								report.setJrxmlFileName("ContainerSalesReport");
								report.setReportFileName("ContainerSalesReport");
								HashMap<String, Object> map = new HashMap<String, Object>();
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("container_sales_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
								map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("AMOUNT_LABEL", getPropertyName("amount"));
								
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ customerComboField
													.getItemCaption(customerComboField
															.getValue()) + "\t";
								}

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(resultList, map);

								reportList.clear();
								resultList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"),
									true);
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
