package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.TranspotationDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.TranspotationModel;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.payment.ui.TransportationPaymentsUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.CustomerLedgerReportDao;
import com.inventory.reports.dao.LedgerViewDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
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
 *         Dec 11, 2013
 */
public class TransportaionLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private CustomerLedgerReportDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	SCheckBox usePaymentEndDate;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_TRANSPORTATION = "Transportation";
	static String TBC_DATE = "Date";
	static String TBC_SALE = "Credit";
	static String TBC_CASH = "Cash";
	static String TBC_PERIOD_BAL = "Period Balance";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	LedgerViewDao lvDao;
	TranspotationDao trnspDao;
	OfficeDao ofcDao;
	LedgerDao ledDao;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			usePaymentEndDate = new SCheckBox(
					getPropertyName("use_date_payment"));

			lvDao = new LedgerViewDao();
			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();
			trnspDao = new TranspotationDao();

			allColumns = new String[] { TBC_SN, TBC_ID, TBC_TRANSPORTATION,
					TBC_DATE, TBC_SALE, TBC_CASH, TBC_PERIOD_BAL, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_TRANSPORTATION,
					TBC_DATE, TBC_SALE, TBC_CASH, TBC_PERIOD_BAL, TBC_BALANCE };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(980, 400);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_TRANSPORTATION, String.class, null,
					getPropertyName("transportation"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_SALE, Double.class, null,
					getPropertyName("sale"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CASH, Double.class, null,
					getPropertyName("cash"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PERIOD_BAL, Double.class, null,
					getPropertyName("period_balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_SALE, 1);
			table.setColumnExpandRatio(TBC_CASH, 1);
			table.setColumnExpandRatio(TBC_PERIOD_BAL, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("600");
			table.setHeight("300");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));
			calculateTableTotals();

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

			ledgertSelect = new SComboField(getPropertyName("transportation"),
					200, null, "id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("all"));

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

			daoObj = new CustomerLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(usePaymentEndDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);

			mainLay.setMargin(true);

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action("Edit");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						TransportationPaymentsUI trans = new TransportationPaymentsUI();
						trans.setCaption(getPropertyName("transportation_payments"));
						try {
							trans.getPaymentIdComboField().setValue(
									new TransportationPaymentDao()
											.getTransportationPaymentModel(
													(Long) item
															.getItemProperty(
																	TBC_ID)
															.getValue())
											.getId());

							trans.center();
							getUI().getCurrent().addWindow(trans);
							trans.addCloseListener(closeListener);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			});

			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID)
									.getValue();

							TransportationPaymentModel objModel = new TransportationPaymentDao()
									.getTransportationPaymentModel(id);

							if (objModel != null) {

								SFormLayout form = new SFormLayout();
								if (objModel.getType() == 1) {
									form.addComponent(new SHTMLLabel(null,
											"<h2><u>"+getPropertyName("transportation_cash")+"</u></h2>"));
								} else {
									form.addComponent(new SHTMLLabel(null,
											"<h2><u>"+getPropertyName("transportation_credit")+"</u></h2>"));
								}

								form.addComponent(new SLabel(
										getPropertyName("bill_no"), objModel
												.getPayment_id() + ""));
								// LedgerModel
								// trans=ledDao.getLedgeer(objModel.getTransportation_id());
								// if(trans!=null)
								// form.addComponent(new
								// SLabel("Transportation :",trans.getName()));

								// LedgerModel
								// toAcc=ledDao.getLedgeer(objModel.getTo_account_id());
								// if(toAcc!=null)
								// form.addComponent(new
								// SLabel("To Account :",toAcc.getName()));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("transportation_amount"),
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
								form.addComponent(new SLabel(
										getPropertyName("place"), objModel
												.getPlace()));
								form.addComponent(new SLabel(
										getPropertyName("invoice_amount"),
										objModel.getInvoiceAmount() + ""));

								form.setWidth("400");

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

			organizationSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
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

			officeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							SCollectionContainer bic = null;
							try {

								List lst = new ArrayList();
								lst.add(new TranspotationModel(0, getPropertyName("all")));

								lst.addAll(trnspDao
										.getAllActiveTranspotationNamesWithLedgerID((Long) officeSelect
												.getValue()));

								bic = SCollectionContainer.setList(lst, "id");
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
					calculateTableTotals();
				}
			});

			officeSelect.setValue(getOfficeID());

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();
			calculateTableTotals();
			if (isValid()) {
				List lst;
				double opening_bal = 0;

				long ledgerId = 0;

				if (ledgertSelect.getValue() != null
						&& !ledgertSelect.getValue().equals(""))
					ledgerId = (Long) ledgertSelect.getValue();

				if (ledgerId != 0) {

					LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
							.getValue());
					lst = lvDao
							.getTransportationPaymentReport(
									CommonUtil.getSQLDateFromUtilDate(fromDate
											.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()),
									(Long) officeSelect.getValue(),
									(Long) ledgertSelect.getValue(),
									usePaymentEndDate.getValue());

					visibleColumns = new String[] { TBC_SN, TBC_DATE, TBC_SALE,
							TBC_CASH, TBC_PERIOD_BAL, TBC_BALANCE };

					opening_bal = daoObj.getOpeningBalance(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							(Long) ledgertSelect.getValue());

//					opening_bal += ledger.getOpening_balance();

				} else {
					List ledgers = trnspDao
							.getAllActiveTranspotationLedgerIDs((Long) officeSelect
									.getValue());

					lst = new ArrayList();
					if (ledgers.size() >= 0)
						lst = lvDao.getTransportationLedgerView(CommonUtil
								.getSQLDateFromUtilDate(fromDate.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDate
										.getValue()), ledgers,
								usePaymentEndDate.getValue(),(Long) officeSelect.getValue());

					visibleColumns = new String[] { TBC_SN, TBC_TRANSPORTATION,
							TBC_DATE, TBC_SALE, TBC_CASH, TBC_BALANCE };
				}

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
					System.out.println("List "+lst.size());
					int ct = 0;
					double bal = -opening_bal, periodBal = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (obj.getAmount_type().equals("Dr")) {
							obj.setDebit(obj.getAmount());
							bal -= obj.getAmount() + obj.getPayed();
							periodBal -= obj.getAmount() + obj.getPayed();
						} else {
							obj.setCredit(obj.getAmount());
							bal += (obj.getAmount() - obj.getPayed());
							periodBal += (obj.getAmount() - obj.getPayed());
						}
						obj.setBalance(bal);

						table.addItem(
								new Object[] { ct + 1, obj.getId(),
										obj.getName(), obj.getDate(),
										obj.getCredit(), obj.getDebit(),
										periodBal, obj.getBalance() }, ct);

						ct++;

					}
					table.setVisibleColumns(visibleColumns);

					calculateTableTotals();

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

			if (isValid()) {

				long ledgerId = 0;

				if (ledgertSelect.getValue() != null
						&& !ledgertSelect.getValue().equals(""))
					ledgerId = (Long) ledgertSelect.getValue();

				if (ledgerId != 0) {

					LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
							.getValue());

					List lst = lvDao
							.getTransportationPaymentReport(
									CommonUtil.getSQLDateFromUtilDate(fromDate
											.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()),
									(Long) officeSelect.getValue(),
									(Long) ledgertSelect.getValue(),
									usePaymentEndDate.getValue());

					List reportList = new ArrayList();

					double opening_bal = daoObj.getOpeningBalance(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							(Long) ledgertSelect.getValue());

//					opening_bal += ledger.getOpening_balance();

					// if(opening_bal!=0)
					// reportList.add(new AcctReportMainBean("Opening Balance",
					// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue())
					// , 0, opening_bal));

					if (lst.size() > 0) {
						Collections.sort(lst,
								new Comparator<AcctReportMainBean>() {
									@Override
									public int compare(
											final AcctReportMainBean object1,
											final AcctReportMainBean object2) {
										return object1.getDate().compareTo(
												object2.getDate());
									}
								});
					}

					double bal = -opening_bal, periodBal = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (obj.getAmount_type().equals("Dr")) {
							obj.setDebit(obj.getAmount());
							bal -= obj.getAmount() + obj.getPayed();
							periodBal -= obj.getAmount() + obj.getPayed();
						} else {
							obj.setCredit(obj.getAmount());
							bal += (obj.getAmount() - obj.getPayed());
							periodBal += (obj.getAmount() - obj.getPayed());
						}
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);

						reportList.add(obj);
					}

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("ToDate", toDate.getValue().toString());
						params.put("LedgerName", ledger.getName());
						params.put("Opening Balance", roundNumber(opening_bal));
						params.put("Current Balance",
								roundNumber(ledger.getCurrent_balance()));
						params.put("Balance",
								roundNumber(-ledger.getCurrent_balance()));
						params.put("Office", ledger.getOffice().getName());
						params.put("Organization", ledger.getOffice().getOrganization().getName());

						params.put("name_label", getPropertyName("name"));
						params.put("organization_label", getPropertyName("organization"));
						params.put("opening_balance_label", getPropertyName("opening_balance"));
						params.put("office_label", getPropertyName("office"));
						params.put("current_balance_label", getPropertyName("current_balance"));
						params.put("REPORT_TITLE_LABEL", getPropertyName("transportation_ledger_report"));
						params.put("SL_NO_LABEL", getPropertyName("sl_no"));
						params.put("DATE_LABEL", getPropertyName("date"));
						params.put("CREDIT_LABEL", getPropertyName("credit"));
						params.put("CASH_LABEL", getPropertyName("cash"));
						params.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
						params.put("BALANCE_LABEL", getPropertyName("balance"));
						params.put("TOTAL_LABEL", getPropertyName("total"));
						
						
						
						report.setJrxmlFileName("TransportationLedgerReport");
						report.setReportFileName("Transportation Ledger Report");
						
						report.setReportSubTitle(getPropertyName("from")+" : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue())
								+ "  "+getPropertyName("from")+" : "
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
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}

				} else {

					List ledgers = trnspDao
							.getAllActiveTranspotationLedgerIDs((Long) officeSelect
									.getValue());

					List lst = null;
					if (ledgers.size() >= 0)
						lst = lvDao.getTransportationLedgerView(CommonUtil
								.getSQLDateFromUtilDate(fromDate.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDate
										.getValue()), ledgers,
								usePaymentEndDate.getValue(),(Long) officeSelect.getValue());

					List reportList = new ArrayList();

					// double
					// opening_bal=daoObj.getOpeningBalance(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
					// (Long) ledgertSelect.getValue());

					// opening_bal+=ledger.getOpening_balance();

					// if(opening_bal!=0)
					// reportList.add(new AcctReportMainBean("Opening Balance",
					// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue())
					// , 0, opening_bal));

					if (lst.size() > 0) {
						Collections.sort(lst,
								new Comparator<AcctReportMainBean>() {
									@Override
									public int compare(
											final AcctReportMainBean object1,
											final AcctReportMainBean object2) {
										return object1.getDate().compareTo(
												object2.getDate());
									}
								});
					}

					double bal = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (obj.getAmount_type().equals("Dr")) {
							obj.setDebit(obj.getAmount());
							bal -= obj.getAmount() + obj.getPayed();
						} else {
							obj.setCredit(obj.getAmount());
							bal += (obj.getAmount() - obj.getPayed());
						}
						obj.setBalance(bal);

						reportList.add(obj);
					}

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("ToDate", toDate.getValue().toString());
						// params.put("LedgerName", ledger.getName());
						params.put("Opening Balance", 0);
						// params.put("Current Balance",
						// roundNumber(ledger.getCurrent_balance()));
						// params.put("Balance",
						// roundNumber(ledger.getCurrent_balance()));
						params.put("Office", officeSelect
								.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						report.setJrxmlFileName("TransportationLedgerUnderOfficeReport");
						report.setReportFileName("Transportation Ledger Report");
						report.setReportTitle("Transportation Ledger Report");
						report.setReportSubTitle("From  : "
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
					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

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

	public void calculateTableTotals() {
		Iterator it = table.getItemIds().iterator();
		Item itm;
		double sal_ttl = 0, cash_ttl = 0, per_bal = 0, bal = 0;
		while (it.hasNext()) {
			itm = table.getItem(it.next());
			sal_ttl += (Double) itm.getItemProperty(TBC_SALE).getValue();
			cash_ttl += (Double) itm.getItemProperty(TBC_CASH).getValue();

			per_bal = (Double) itm.getItemProperty(TBC_PERIOD_BAL).getValue();
			bal = (Double) itm.getItemProperty(TBC_BALANCE).getValue();
		}
		table.setColumnFooter(TBC_SALE, asString(roundNumber(sal_ttl)));
		table.setColumnFooter(TBC_CASH, asString(roundNumber(cash_ttl)));
		table.setColumnFooter(TBC_PERIOD_BAL, asString(roundNumber(per_bal)));
		table.setColumnFooter(TBC_BALANCE, asString(roundNumber(bal)));
	}

}
