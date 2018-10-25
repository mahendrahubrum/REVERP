package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.model.PaymentModel;
import com.inventory.payment.ui.CustomerPaymentsUI;
import com.inventory.payment.ui.SupplierPaymentsUI;
import com.inventory.reports.dao.PaymentReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
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
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T. Inventory Jan 21 2014
 */
public class CustomerReceiptReportUI extends SparkLogic {

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

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_DATE = "Date";
	static String TBC_TO_ACT = "To Acct.";
	static String TBC_PAID_AMT = "Amount";
	static String TBC_CUST_TOTAL = "Customer Total";
	static String TBC_CHEQUE_DATE = "Cheq. Date";
	static String TBC_DESCRIPTION = "Description";

	SHorizontalLayout popupContainer;
	SHorizontalLayout mainLay;
	STable table;
	String[] allColumns;
	String[] visibleColumns;

	private SButton generateButton, showButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private SRadioButton statusRadioButton;

	private long customerId;

	private Report report;

	PaymentReportDao daoObj;
	LedgerDao ledDao;

	private WrappedSession session;
	private SettingsValuePojo sett;

	@Override
	public SPanel getGUI() {

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_CUSTOMER, TBC_DATE,
				TBC_TO_ACT, TBC_PAID_AMT, TBC_CUST_TOTAL, TBC_CHEQUE_DATE,
				TBC_DESCRIPTION };
		visibleColumns = new String[] { TBC_SN, TBC_CUSTOMER, TBC_DATE,
				TBC_TO_ACT, TBC_PAID_AMT, TBC_CUST_TOTAL, TBC_CHEQUE_DATE,
				TBC_DESCRIPTION };

		mainLay = new SHorizontalLayout();
		customerId = 0;
		report = new Report(getLoginID());
		popupContainer = new SHorizontalLayout();
		daoObj = new PaymentReportDao();

		ledDao = new LedgerDao();

		setSize(1200, 400);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

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

		table = new STable(null, 1000, 200);

		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
				Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,
				getPropertyName("customer"), null, Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_TO_ACT, String.class, null,
				getPropertyName("to_account"), null, Align.RIGHT);
		table.addContainerProperty(TBC_PAID_AMT, Double.class, null,
				getPropertyName("amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CUST_TOTAL, Double.class, null,
				getPropertyName("customer_total"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CHEQUE_DATE, String.class, null,
				getPropertyName("cheque_date"), null, Align.RIGHT);
		table.addContainerProperty(TBC_DESCRIPTION, String.class, null,
				getPropertyName("description"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.4);
		table.setColumnExpandRatio(TBC_DATE, (float) 1.2);
		table.setColumnExpandRatio(TBC_TO_ACT, 1.5f);
		table.setColumnExpandRatio(TBC_CUSTOMER, 1.5f);
		table.setColumnExpandRatio(TBC_PAID_AMT, 1);
		table.setColumnExpandRatio(TBC_CHEQUE_DATE, 1.2f);
		table.setColumnExpandRatio(TBC_DESCRIPTION, 2);

		table.setVisibleColumns(visibleColumns);
		table.setSizeFull();
		table.setSelectable(true);
		table.setWidth("700");
		table.setHeight("300");

		table.setFooterVisible(true);
		table.setColumnFooter(TBC_DATE, getPropertyName("total"));
		table.setColumnFooter(TBC_PAID_AMT, "0");

		try {

			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				sett = (SettingsValuePojo) session.getAttribute("settings");

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);

			statusRadioButton = new SRadioButton(getPropertyName("status"),
					200, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(
							1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			showButton = new SButton(getPropertyName("show"));
			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);
			
			mainLay.setMargin(true);
			mainLay.setSpacing(true);

			mainPanel.setContent(mainLay);

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
								customerComboField
										.setItemCaptionPropertyId("name");

								table.removeAllItems();

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
							CustomerPaymentsUI purchase = new CustomerPaymentsUI();
							purchase.setCaption(getPropertyName("customer_payment"));
							purchase.getPaymentIdComboField().setValue(
									(Long) item.getItemProperty(TBC_ID).getValue());
							purchase.center();
							getUI().getCurrent().addWindow(purchase);
							purchase.addCloseListener(closeListener);
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
							PaymentModel mdl=new PaymentDao().getPaymentModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("customer_payment")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("payment_no"),mdl.getPayment_id()+""));
							form.addComponent(new SLabel(getPropertyName("from_account"),item.getItemProperty(TBC_TO_ACT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("details"),mdl.getDescription()));
							form.addComponent(new SLabel(getPropertyName("amount"),mdl.getPayment_amount()+ ""));
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

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List<Object> reportList;

							long clientId = 0;
							boolean active = true;
							if ((Integer) statusRadioButton.getValue() == 1)
								active = false;

							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.equals("")) {
								clientId = toLong(customerComboField.getValue()
										.toString());
							}

							List lst = daoObj.getCustomerReceiptReport(
									(Long) officeComboField.getValue(),
									clientId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()), active);

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

								if (obj.getCashOrCheque() == 1) {
									obj.setChequeDate("");
									obj.setIssuedDate("");
								}

								reportList.add(obj);
							}

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("CustomerReceipt_Report");
								report.setReportFileName("Customer Receipt Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("customer_receipt_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
								map.put("TO_ACCOUNT_LABEL", getPropertyName("to_account"));
								map.put("AMOUNT_LABEL", getPropertyName("amount"));
								map.put("CUSTOMER_TOTAL_LABEL", getPropertyName("customer_total"));
								map.put("CHEQUE_DATE_LABEL", getPropertyName("cheque_date"));
								map.put("ISSUE_DATE_LABEL", getPropertyName("issue_date"));
								map.put("INVOICE_PERIOD_LABEL", getPropertyName("invoice_period"));
								map.put("DESCRIPTION_LABEL", getPropertyName("description"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ customerComboField
													.getItemCaption(customerComboField
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

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						table.removeAllItems();
						table.setColumnFooter(TBC_PAID_AMT, "0");
						
						if (officeComboField.getValue() != null) {

							List<Object> reportList;

							long clientId = 0;
							boolean active = true;
							if ((Integer) statusRadioButton.getValue() == 1)
								active = false;

							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.equals("")) {
								clientId = toLong(customerComboField.getValue()
										.toString());
							}

							List lst = daoObj.getCustomerReceiptReport(
									(Long) officeComboField.getValue(),
									clientId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()), active);

							if (lst != null && lst.size() > 0) {

								table.setVisibleColumns(allColumns);
								int ct = 0;
								reportList = new ArrayList<Object>();
								ReportBean obj;
								long oldSup = 0;
								double total = 0;
								double netTotal = 0;
								Iterator it = lst.iterator();
								while (it.hasNext()) {
									obj = (ReportBean) it.next();

									if (obj.getId() == oldSup) {
										total += obj.getAmount();
									} else {
										oldSup = obj.getId();
										total = obj.getAmount();
									}
									netTotal+=obj.getAmount();

									obj.setTotal(total);

									if (obj.getCashOrCheque() == 1) {
										obj.setChequeDate("");
									}

									table.addItem(
											new Object[] {
													ct + 1,
													obj.getNumber(),
													obj.getClient_name(),
													CommonUtil
															.formatDateToDDMMYYYY(obj
																	.getDt()),
													obj.getFrom_account(),
													obj.getAmount(),
													obj.getTotal(),
													obj.getChequeDate(),
													obj.getDescription() }, ct);

									ct++;

									reportList.add(obj);
								}
								table.setColumnFooter(TBC_PAID_AMT, roundNumber(netTotal)+"");
								table.setVisibleColumns(visibleColumns);
							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

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

	@SuppressWarnings("unchecked")
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
