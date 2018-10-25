package com.inventory.reports.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.rent.dao.RentDetailsDao;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
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
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         May 25, 2014
 */
public class CustomerDueReport extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5905202557361121082L;

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
	SHorizontalLayout horizontalLayout;
	SHorizontalLayout buttonLayout;
	RentCustomerLedgerReportDao reportDao;
	RentCustomerLedgerReportBean reportBean;
	SWindow popup;
	STable childTable;
	RentDetailsDao rentDetailsDao;
	RentDetailsModel mastermdl;
	LedgerDao ledDao;

	@Override
	public SPanel getGUI() {
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("400");
		setHeight("400");
		layout.setSpacing(true);
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
		fromDate.setValue(getMonthStartDate());
		toDate = new SDateField(getPropertyName("to_date"));
		toDate.setValue(getWorkingDate());
		reportchoiceField = new SReportChoiceField(getPropertyName("export_to"));
		generate = new SButton(getPropertyName("generate"));
		report = new Report(getLoginID());
		offDao = new OfficeDao();
		custDao = new CustomerDao();
		customerCombo = new SComboField(getPropertyName("customer"), 200);
		customerCombo.setInputPrompt("--------- "+getPropertyName("all")+" ---------");
		entryTable = new STable();
		entryTable.setWidth("800");
		entryTable.addContainerProperty("Date", java.util.Date.class, null,
				getPropertyName("date"), null, null);
		entryTable.addContainerProperty("Rent No", Long.class, null,
				getPropertyName("rent_no"), null, null);
		entryTable.addContainerProperty("Amount Paid", Double.class, null,
				getPropertyName("amount_paid"), null, null);
		// entryTable.addContainerProperty("Budget Id", Long.class, null);
		// entryTable.addContainerProperty("Notes", String.class, null);
		entryTable.addContainerProperty("Total Amount", Double.class, null,
				getPropertyName("total_amount"), null, null);
		entryTable.addContainerProperty("Balance", Double.class, null,
				getPropertyName("balance"), null, null);
		entryTable.addContainerProperty("Shipping charge", Double.class, null,
				getPropertyName("shipping_charge"), null, null);

		entryTable.addContainerProperty("Max credit period", Integer.class,
				null, getPropertyName("max_credit_period"), null, null);
		entryTable.addContainerProperty("customer", String.class, null,
				getPropertyName("customer"), null, null);

		// entryTable.addContainerProperty("Variation Amount", Double.class,
		// null);
		entryTable.setFooterVisible(true);
		// entryTable.setColumnFooter("Ref. No", "Total");
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

		childTable.addContainerProperty("Item", String.class, null);
		childTable.addContainerProperty("Quantity", Double.class, null);
		childTable.addContainerProperty("Period", Double.class, null);
		childTable.addContainerProperty("Status", String.class, null);
		childTable.addContainerProperty("Return Date", java.util.Date.class,
				null);
		childTable.addContainerProperty("Discount", Double.class, null);
		childTable.addContainerProperty("Amount", Double.class, null);

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
					entryTable.removeAllItems();
					try {

						// officeList.add(0,
						// new S_OfficeModel(0,
						// "-----------All----------"));
						officeList.addAll(offDao
								.getAllOfficesUnderOrg((Long) organizationCombo
										.getValue()));

						System.out.println(officeList.size());

						SCollectionContainer office = SCollectionContainer
								.setList(officeList, "id");
						officeCombo.setContainerDataSource(office);
						// officeCombo
						// .setInputPrompt("-----------All--------------");
						officeCombo.setItemCaptionPropertyId("name");
						// officeCombo.setValue((long) 0);

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
						// if (officeCombo.getValue() == null
						// || (Long) officeCombo.getValue() == 0) {
						// customerlist.addAll(custDao.getAllActiveCustomerNamesWithOrgID((Long)
						// organizationCombo.getValue()));
						// }

						// else{
						customerlist.addAll(custDao
								.getAllActiveCustomerNamesWithLedgerID((Long) officeCombo
										.getValue()));

						// }

						// customerlist.add(0, new CustomerModel(0,
						// "------------All-------------"));

						SCollectionContainer office = SCollectionContainer
								.setList(customerlist, "id");
						customerCombo.setContainerDataSource(office);
						// customerCombo
						// .setInputPrompt("-----------All--------------");
						customerCombo.setItemCaptionPropertyId("name");
						// customerCombo.setValue((long) 0);

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
								"Shipping charge").getValue();
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

							List lis = rentDetailsDao.getbudgetMaster(rentid);

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

					try {

						if (customerCombo.getValue() != null
								&& (Long) customerCombo.getValue() != 0) {

							RentDetailsModel mdl = null;
							reportList.addAll(reportDao.getRentCustomerReport(
									(Long) organizationCombo.getValue(),
									(Long) officeCombo.getValue(),
									(Long) customerCombo.getValue(), CommonUtil
											.getSQLDateFromUtilDate(fromDate
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue())));
							Iterator itr = reportList.iterator();
							while (itr.hasNext()) {
								mdl = (RentDetailsModel) itr.next();

								reportBean = new RentCustomerLedgerReportBean(
										mdl.getDate(), mdl.getRent_number(),
										mdl.getAmount(), mdl.getTotalpaidamt(),
										mdl.getAmount() - mdl.getTotalpaidamt()

								);
								System.out.println(mdl.getDate());
								System.out.println("Amount : "
										+ mdl.getAmount());
								System.out.println("Balance : "
										+ (mdl.getAmount() - mdl
												.getTotalpaidamt()));

								if ((mdl.getAmount() - mdl.getTotalpaidamt()) != 0) {
									newReportList.add(reportBean);

								}

							}
							if (newReportList != null
									&& newReportList.size() > 0) {

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
								params.put("SL_NO_LABEL", getPropertyName("sl_no"));
								params.put("ISSUE_DATE_LABEL", getPropertyName("issue_date"));
								params.put("RENT_NO_LABEL", getPropertyName("rent_no"));
								params.put("TOTAL_AMOUNT_LABEL", getPropertyName("total_amount"));
								params.put("PAYMENT_LABEL", getPropertyName("payment"));
								params.put("BALANCE_LABEL", getPropertyName("balance"));
								params.put("NAME_LABEL", getPropertyName("name"));
								params.put("OFFICE_LABEL", getPropertyName("office"));
								params.put("ORGANIZATION_LABEL", getPropertyName("organization"));
								

								report.setJrxmlFileName("RentCustomerLedgerReport");
								report.setReportFileName("RentCustomerLedgerReport");
								report.setReportTitle(getPropertyName("rent_customer_ledger_report"));
								String subHeader = "";
								// if ((Long) budgetDefCombo.getValue() != 0) {
								// subHeader += "Budget Definition : "
								// + budgetDefCombo
								// .getItemCaption(budgetDefCombo
								// .getValue()) + "\t";
								// }
								// else{
								// subHeader += "Budget Definition : All" +
								// "\t";
								// }
								// if ((Long) budgetCombo.getValue() != 0) {
								// subHeader += "\n Budget : "
								// + budgetCombo
								// .getItemCaption(budgetCombo
								// .getValue());
								// }
								// else{
								// subHeader += "\n Budget : All" + "\t";
								// }

								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDate
														.getValue())
										+ "\t "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDate
														.getValue());

								report.setReportSubTitle(""+getPropertyName("from")+"  : "
										+ CommonUtil
												.formatDateToCommonFormat(fromDate
														.getValue())
										+ "   "+getPropertyName("to")+"  : "
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
							customerCombo.setComponentError(new UserError(
									"Select a customer"));
						}

					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			});

			// show.addClickListener(new ClickListener() {
			//
			// @Override
			// public void buttonClick(ClickEvent event) {
			// List reportList = new ArrayList();
			// List newReportList = new ArrayList();
			// customerCombo.setComponentError(null);
			// // entryTable.setComponentError(null);
			//
			// entryTable.removeAllItems();
			// // if(customerCombo.getValue() != null || (Long)
			// customerCombo.getValue() !=0){
			// // if(entryTable.getItemIds().size() != 0){
			//
			//
			// try {
			// entryTable.setVisibleColumns(new String[]
			// {"Date","Rent No","Amount Paid","Total Amount","Balance",
			// "Shipping charge", "Max credit period", "customer"});
			// RentDetailsModel mdl;
			// reportList.addAll(reportDao.getRentCustomerReport((Long)
			// organizationCombo.getValue(),
			// (Long) officeCombo.getValue(),(Long) customerCombo.getValue(),
			// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
			// CommonUtil.getSQLDateFromUtilDate(toDate.getValue())));
			//
			//
			//
			// Iterator itr = reportList.iterator();
			// while(itr.hasNext()){
			// mdl= (RentDetailsModel) itr.next();
			//
			//
			//
			// reportBean = new RentCustomerLedgerReportBean(mdl.getDate(),
			// mdl.getRent_number(), mdl.getAmount(), mdl.getPayment_amount(),
			// mdl.getAmount() - mdl.getPayment_amount()
			//
			// );
			// System.out.println(mdl.getDate());
			// System.out.println("Amount : " + mdl.getAmount());
			// System.out.println("Balance : " + (mdl.getAmount() -
			// mdl.getPayment_amount()));
			// // newReportList.add(reportBean);
			// //
			// //
			// // for(int m=0; m<newReportList.size(); m++){
			//
			// entryTable.addItem(
			// new Object[] {
			// reportBean.getDate(),reportBean.getRentno(),
			// reportBean.getReturnAmount(), reportBean.getCash(),
			// reportBean.getCash()-reportBean.getReturnAmount(),
			// mdl.getShipping_charge(),mdl.getCredit_period(),mdl.getCustomer().getName()
			// }, entryTable.getItemIds().size()+1);
			// // }
			//
			//
			//
			// }
			//
			// entryTable.setVisibleColumns(new String[]
			// {"Date","Rent No","Amount Paid","Total Amount","Balance"});
			//
			//
			//
			//
			// }catch (Exception e) {
			//
			// e.printStackTrace();
			// }
			// // }
			// // else{
			// // entryTable.setComponentError(new
			// UserError("No data available"));
			// // }
			// // }
			// //
			// // else{
			// // customerCombo.setComponentError(new
			// UserError("Select a customer"));
			// // }
			// }
			// });

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
		masterDetailsGrid.addComponent(new SLabel("Shipping charge :"), 1, 3);
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
		layout.addComponent(buttonLayout);
		buttonLayout.addComponent(generate);
		// buttonLayout.addComponent(show);

		horizontalLayout.addComponent(layout);
		// horizontalLayout.addComponent(entryTable);
		organizationCombo.setValue(getOrganizationID());
		officeCombo.setValue(getOfficeID());
		customerCombo.setInputPrompt("----- "+getPropertyName("select")+" ----");
		panel.setContent(horizontalLayout);

		return panel;

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

}
