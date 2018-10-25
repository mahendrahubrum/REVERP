package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.model.PaymentModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.LaundryCustomerLedgerReportDao;
import com.inventory.sales.dao.LaundrySalesDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.LaundrySalesDetailsModel;
import com.inventory.sales.model.LaundrySalesModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinServlet;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 * @Date Feb 19 2014
 */
public class LaundryCustomerLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private LaundryCustomerLedgerReportDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_TYPE = "type";
	static String TBC_DATE = "Date";
	static String TBC_SALE = "Debit";
	static String TBC_CASH = "Credit";
	static String TBC_RETURN = "Return";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();

			allColumns = new String[] { TBC_SN, TBC_ID, TBC_TYPE, TBC_DATE,
					TBC_SALE, TBC_CASH, TBC_RETURN, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_DATE, TBC_SALE,
					TBC_CASH, TBC_BALANCE };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(980, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_TYPE, String.class, null,
					getPropertyName("type"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_SALE, Double.class, null,
					getPropertyName("debit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CASH, Double.class, null,
					getPropertyName("credit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_RETURN, Double.class, null,
					getPropertyName("return"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_SALE, 1);
			table.setColumnExpandRatio(TBC_CASH, 1);
			table.setColumnExpandRatio(TBC_RETURN, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("600");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());
			ledgertSelect = new SComboField(getPropertyName("customer"), 200,
					ledDao.getAllCustomers((Long) officeSelect.getValue()),
					"id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("select"));

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new LaundryCustomerLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID)
									.getValue();
							if (itm.getItemProperty(TBC_TYPE).getValue()
									.equals("Sale")) {

								LaundrySalesModel objModel = new LaundrySalesDao()
										.getSale(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Sale</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("sales_no"), objModel
												.getSales_number() + ""));
								form.addComponent(new SLabel(
										getPropertyName("customer"), objModel
												.getCustomer().getName()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("max_credit_period"),
										objModel.getCredit_period() + ""));

								if (isShippingChargeEnable())
									form.addComponent(new SLabel(
											getPropertyName("shipping_charge"),
											objModel.getShipping_charge() + ""));

								form.addComponent(new SLabel(
										getPropertyName("net_amount"), objModel
												.getAmount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("paid_amount"),
										objModel.getPayment_amount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel.getDetails_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								LaundrySalesDetailsModel invObj;
								Iterator itmItr = objModel.getDetails_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (LaundrySalesDetailsModel) itmItr
											.next();

									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getItem().getName()), 1, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getQuantity() + ""), 2, i);
									grid.addComponent(new SLabel(null, invObj
											.getUnit().getSymbol()), 3, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getUnit_price() + ""), 4,
											i);
									grid.addComponent(
											new SLabel(null, invObj
													.getDiscount_amount() + ""),
											5, i);
									grid.addComponent(
											new SLabel(
													null,
													(invObj.getUnit_price()
															* invObj.getQuantity()
															- invObj.getDiscount_amount() + invObj
																.getTax_amount())
															+ ""), 6, i);
									i++;
								}

								form.addComponent(grid);
								form.addComponent(new SLabel(
										getPropertyName("comment"), objModel
												.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_TYPE).getValue()
									.equals("Receipt")) {

								PaymentModel objModel = new PaymentDao()
										.getPaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Customer Receipt</u></h2>"));
								form.addComponent(new SLabel("Receipt No. :",
										objModel.getPayment_id() + ""));
								LedgerModel cust = ledDao.getLedgeer(objModel
										.getFrom_account_id());
								if (cust != null)
									form.addComponent(new SLabel(
											getPropertyName("customer"), cust
													.getName()));

								LedgerModel toAcc = ledDao.getLedgeer(objModel
										.getTo_account_id());
								if (toAcc != null)
									form.addComponent(new SLabel(
											getPropertyName("to_account"),
											toAcc.getName()));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("customer_amount"),
										objModel.getSupplier_amount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("discount"), objModel
												.getDiscount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("payment_amount"),
										objModel.getPayment_amount() + ""));

								form.addComponent(new SLabel(
										getPropertyName("description"),
										objModel.getDescription()));

								form.setWidth("400");

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							} else {
								SalesReturnModel objModel = new SalesReturnDao()
										.getSalesReturnModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Sales Return</u></h2>"));
//								form.addComponent(new SLabel(
//										getPropertyName("credit_note_no"),
//										objModel.getCredit_note_no() + ""));
								form.addComponent(new SLabel(
										getPropertyName("customer"), objModel
												.getCustomer().getName()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("net_amount"), objModel
												.getAmount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("paid_amount"),
//										objModel.getPayment_amount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Stock Qty"), 4, 0);
								grid.addComponent(new SLabel(null,
										"Purch. Rtn Qty"), 5, 0);
								grid.addComponent(
										new SLabel(null, "Waste Qty"), 6, 0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 7, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										8, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										9, 0);
								grid.setSpacing(true);

								int i = 1;
								SalesReturnInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getInventory_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (SalesReturnInventoryDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getItem().getName()), 1, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getStock_quantity() + ""),
//											2, i);
									grid.addComponent(new SLabel(null, invObj
											.getUnit().getSymbol()), 3, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getStock_quantity() + ""),
//											4, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getReturned_quantity()
//													+ ""), 5, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getWaste_quantity() + ""),
//											6, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getUnit_price() + ""), 7,
											i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getDiscount_amount() + ""),
//											8, i);
//									grid.addComponent(
//											new SLabel(
//													null,
//													(invObj.getUnit_price()
//															* (invObj
//																	.getStock_quantity()
//																	+ invObj.getReturned_quantity() + invObj
//																		.getWaste_quantity())
//															- invObj.getDiscount_amount() + invObj
//																.getTax_amount())
//															+ ""), 9, i);
									i++;
								}

								form.addComponent(grid);
								form.addComponent(new SLabel(
										getPropertyName("comment"), objModel
												.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						showReport();
					}
				}
			});

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						generateReport();
					}
				}
			});

			organizationSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
										.getValue()), "id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					officeSelect.setContainerDataSource(bic);
					officeSelect.setItemCaptionPropertyId("name");

				}
			});

			officeSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								ledDao.getAllCustomers((Long) officeSelect
										.getValue()), "id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ledgertSelect.setContainerDataSource(bic);
					ledgertSelect.setItemCaptionPropertyId("name");

				}
			});

			ledgertSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
				}
			});

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();

			LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List lst = daoObj.getLaundryCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						getOfficeID(), (Long) ledgertSelect.getValue());

				double opening_bal = daoObj.getOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				if (lst != null && lst.size() > 0) {
					Collections.sort(lst, new Comparator<AcctReportMainBean>() {
						@Override
						public int compare(final AcctReportMainBean object1,
								final AcctReportMainBean object2) {
							return object1.getDate().compareTo(
									object2.getDate());
						}
					});

					table.setVisibleColumns(allColumns);

					int ct = 0;
					double bal = opening_bal;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (obj.getParticulars().equals("Sal Return")) {
							bal -= (obj.getAmount() + obj.getPayed());
							// obj.setBalance(bal);
						} else {
							bal += obj.getAmount() - obj.getPayed();
							// obj.setBalance(bal);
						}

						if (obj.getParticulars().equals("Sal Return")) {
							if (obj.getPayed() == 0)
								obj.setPayed(0);
							table.addItem(new Object[] { ct + 1, obj.getId(),
									obj.getParticulars(), obj.getDate(), 0.0,
									obj.getPayed(), obj.getAmount(), Math.abs(bal) }, ct);
						} else if (obj.getParticulars().equals("Receipt")) {
							table.addItem(new Object[] { ct + 1, obj.getId(),
									obj.getParticulars(), obj.getDate(), 0.0,
									obj.getPayed(), 0.0, Math.abs(bal) }, ct);
						} else {
							table.addItem(
									new Object[] { ct + 1, obj.getId(),
											obj.getParticulars(),
											obj.getDate(), obj.getAmount(),
											obj.getPayed(), 0.0, Math.abs(bal) }, ct);
						}

						ct++;

					}

					table.setVisibleColumns(visibleColumns);

					lst.clear();
				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void generateReport() {
		try {

			LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List lst = daoObj.getLaundryCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						getOfficeID(), (Long) ledgertSelect.getValue());

				List reportList = new ArrayList();

				double opening_bal = daoObj.getOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				// if(opening_bal!=0)
				// reportList.add(new AcctReportMainBean("Opening Balance",
				// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue())
				// , 0, opening_bal));

				if (lst.size() > 0) {
					Collections.sort(lst, new Comparator<AcctReportMainBean>() {
						@Override
						public int compare(final AcctReportMainBean object1,
								final AcctReportMainBean object2) {
							return object1.getDate().compareTo(
									object2.getDate());
						}
					});
				}

				double bal = opening_bal;
				AcctReportMainBean obj;
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					obj = (AcctReportMainBean) it.next();

					if (obj.getParticulars().equals("Sal Return")) {
						bal -= obj.getAmount();

						if (obj.getPayed() == -0) {
							obj.setPayed(0);
						}

						bal -= obj.getPayed();
						obj.setBalance(Math.abs(bal));
					} else {
						bal += obj.getAmount() - obj.getPayed();
						obj.setBalance(Math.abs(bal));
					}
					reportList.add(obj);
				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
					params.put("LedgerName", ledger.getName());

					params.put("Balance",
							roundNumber(Math.abs(ledger.getCurrent_balance())));
					params.put("OpeningBalance",
							roundNumber(Math.abs(opening_bal)));
					params.put("Office", ledger.getOffice().getName());
					params.put("Organization", ledger.getOffice()
							.getOrganization().getName());
					
					params.put("IMAGE_PATH", VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/").toString());
					
					report.setJrxmlFileName("LaundryCustomerLedgerReport");
					report.setReportFileName("Customer Ledger Report");
					report.setReportTitle("Customer Ledger Report");
					report.setReportSubTitle("Customer : "+ledgertSelect.getItemCaption(ledgertSelect.getValue())+"\n From  : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ "   To  : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, params);

					reportList.clear();
					lst.clear();

				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (ledgertSelect.getValue() == null
				|| ledgertSelect.getValue().equals("")) {
			setRequiredError(ledgertSelect,
					getPropertyName("invalid_selection"), true);
			ledgertSelect.focus();
			ret = false;
		} else
			setRequiredError(ledgertSelect, null, false);

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			toDate.focus();
			ret = false;
		} else
			setRequiredError(toDate, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
