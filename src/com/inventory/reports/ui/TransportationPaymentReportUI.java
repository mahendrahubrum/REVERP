package com.inventory.reports.ui;

import java.util.ArrayList;
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
import com.inventory.reports.dao.PaymentReportDao;
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
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T. Inventory Jan 21 2014
 */
public class TransportationPaymentReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField transportationComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	PaymentReportDao daoObj;
	LedgerDao ledDao;
	TranspotationDao trnspDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_TRANSPORTATION = "Transportation";
	static String TBC_DATE = "Date";
	static String TBC_PAY_NO = "Payment No";
	static String TBC_TYPE = "Type";
	static String TBC_AMOUNT = "Amount";
	static String TBC_DESCRIPTION = "Description";

	String[] allColumns;
	String[] visibleColumns;

	STable table;
	private SHorizontalLayout popupContainer;

	@Override
	public SPanel getGUI() {
		customerId = 0;
		report = new Report(getLoginID());

		daoObj = new PaymentReportDao();
		trnspDao = new TranspotationDao();
		ledDao = new LedgerDao();

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		popupContainer = new SHorizontalLayout();
		SHorizontalLayout mainLay = new SHorizontalLayout();

		setSize(1000, 400);

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

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

			allColumns = new String[] { TBC_SN, TBC_ID, TBC_DATE, TBC_PAY_NO,
					TBC_TRANSPORTATION, TBC_TYPE, TBC_DESCRIPTION, TBC_AMOUNT };
			visibleColumns = new String[] { TBC_SN, TBC_DATE, TBC_PAY_NO,
					TBC_TRANSPORTATION, TBC_TYPE, TBC_DESCRIPTION, TBC_AMOUNT };

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_TRANSPORTATION, String.class, null,
					getPropertyName("transportation"), null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_PAY_NO, String.class, null,
					getPropertyName("payment_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE, String.class, null,
					getPropertyName("type"), null, Align.LEFT);
			table.addContainerProperty(TBC_DESCRIPTION, String.class, null,
					getPropertyName("description"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,
					getPropertyName("amount"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 1);
			table.setColumnExpandRatio(TBC_TRANSPORTATION, 1.5f);
			table.setColumnExpandRatio(TBC_TYPE, 1);
			table.setColumnExpandRatio(TBC_DESCRIPTION, 2);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("650");
			table.setHeight("300");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));
			calculateTableTotals();

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			transportationComboField = new SComboField(
					getPropertyName("transportation"), 200, null, "id", "name",
					false, getPropertyName("all"));
			mainFormLayout.addComponent(transportationComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			showButton = new SButton(getPropertyName("show"));

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);

			mainPanel.setContent(mainLay);

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action(getPropertyName("edit"));

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

			officeComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						@SuppressWarnings("unchecked")
						public void valueChange(ValueChangeEvent event) {
							SCollectionContainer bic = null;
							try {

								List lst = new ArrayList();
								lst.add(new TranspotationModel(0, getPropertyName("all")));

								lst.addAll(trnspDao
										.getAllActiveTranspotationNamesWithLedgerID((Long) officeComboField
												.getValue()));

								bic = SCollectionContainer.setList(lst, "id");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							transportationComboField
									.setContainerDataSource(bic);
							transportationComboField
									.setItemCaptionPropertyId("name");
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

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							String items = "";

							List<Object> reportList;

							long clientId = 0;

							if (transportationComboField.getValue() != null
									&& !transportationComboField.getValue()
											.equals("")) {
								clientId = toLong(transportationComboField
										.getValue().toString());
							}
							List lst = daoObj.getTransportationPaymentReport(
									(Long) officeComboField.getValue(),
									clientId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()));

							reportList = new ArrayList<Object>();
							ReportBean obj;
							long oldSup = 0;
							double total = 0;
							
							Iterator it = lst.iterator();
							while (it.hasNext()) {
								obj = (ReportBean) it.next();

								if (obj.getId() == oldSup) {
									total += obj.getAmount();
								} else {
									oldSup = obj.getId();
									total = obj.getAmount();
								}

								obj.setTotal(total);
								
								
								if (obj.getType() == 1) {
									if (obj.getCashOrCheque() == 1) {
										obj.setChequeDate("");
										obj.setIssuedDate("");
									}
								}else{
									obj.setChequeDate("");
									obj.setIssuedDate("");
									obj.setInvoicePeriod("");
								}

								reportList.add(obj);
							}

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("TransportationPayment_Report");
								report.setReportFileName("Transportation Payment Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("transportation_payment_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("PAYMENT_NO_LABEL", getPropertyName("payment_no"));
								map.put("TRANSPORTATION_LABEL", getPropertyName("transportation"));
								map.put("PAYMENT_TYPE_LABEL", getPropertyName("payment_type"));
								map.put("AMOUNT_LABEL", getPropertyName("amount"));
								map.put("CHEQUE_DATE_LABEL", getPropertyName("cheque_date"));
								map.put("ISSUED_DATE_LABEL", getPropertyName("issued_date"));
								map.put("INVOICE_PERIOD_LABEL", getPropertyName("invoice_period"));
								map.put("DESCRIPTION_LABEL", getPropertyName("description"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("transportation")+" : "
											+ transportationComboField
													.getItemCaption(transportationComboField
															.getValue()) + "\t";
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
								SNotification.show(getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField, "Select Office",
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

	protected void showReport() {
		try {

			table.removeAllItems();
			calculateTableTotals();
			if (isValid()) {
				double opening_bal = 0;

				long ledgerId = 0;

				if (transportationComboField.getValue() != null
						&& !transportationComboField.getValue().equals(""))
					ledgerId = (Long) transportationComboField.getValue();

				List lst = daoObj
						.getTransportationPaymentReport((Long) officeComboField
								.getValue(), ledgerId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
								.getSQLDateFromUtilDate(toDateField.getValue()));

				String type = "";
				String date = "";

				if (lst != null && lst.size() > 0) {

					table.setVisibleColumns(allColumns);

					int ct = 0;
					double bal = -opening_bal, periodBal = 0;
					ReportBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (ReportBean) it.next();
						if (obj.getType() == 1) {
							type = getPropertyName("payment");
						} else {
							type = getPropertyName("credit");
						}
						date = CommonUtil.formatDateToCommonFormat(obj.getDt());

						table.addItem(new Object[] { ct + 1, obj.getId(), date,
								obj.getPaymentNo(), obj.getClient_name(), type,
								obj.getDescription(), obj.getAmount() }, ct);
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
			ledgerModel.setName(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			transportationComboField.setContainerDataSource(custContainer);
			transportationComboField.setItemCaptionPropertyId("name");
			transportationComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateTableTotals() {
		Iterator it = table.getItemIds().iterator();
		Item itm;
		double sal_ttl = 0;
		while (it.hasNext()) {
			itm = table.getItem(it.next());
			sal_ttl += (Double) itm.getItemProperty(TBC_AMOUNT).getValue();

		}
		table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(sal_ttl)));
	}

	@Override
	public Boolean isValid() {
		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
