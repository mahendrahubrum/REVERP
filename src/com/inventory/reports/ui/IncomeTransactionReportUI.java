package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.IncomeTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.acct.ui.IncomeTransactionUI;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
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
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T. Inventory Dec 31, 2013
 */
public class IncomeTransactionReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField incomeLedgerComboField;
	private SComboField fromAcctComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	static final long INCOME = 3;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton generateConsolidatedButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	private SRadioButton modeRadio;

	LedgerDao ledDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_BILL = "Bill";
	static String TBC_FROM = "From Account";
	static String TBC_EXPENDITURE = "Expendeture";
	static String TBC_TOTAL = "Amount";
	SHorizontalLayout popupContainer;
	Object[] allColumns;
	Object[] visibleColumns;
	SHorizontalLayout mainHorizontal;
	STable table;
	SButton showButton;
	
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;

	@SuppressWarnings({ "unchecked", "deprecation", "serial" })
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_EXPENDITURE, TBC_TOTAL };
		visibleColumns = new Object[] { TBC_SN, TBC_EXPENDITURE, TBC_TOTAL };
		
		allSubColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_BILL, TBC_FROM, TBC_EXPENDITURE, TBC_TOTAL };
		visibleSubColumns = new Object[] { TBC_SN, TBC_DATE,TBC_BILL, TBC_FROM, TBC_EXPENDITURE, TBC_TOTAL };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		
		popupContainer = new SHorizontalLayout();
		mainHorizontal=new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_EXPENDITURE, String.class, null,getPropertyName("income"), null, Align.LEFT);
		table.addContainerProperty(TBC_TOTAL, Double.class, null,getPropertyName("amount"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_EXPENDITURE, (float) 2);
		table.setColumnExpandRatio(TBC_TOTAL, (float) 0.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		
		subTable = new STable(null, 750, 250);
		subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("bill"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_FROM, String.class, null,getPropertyName("to_account"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_EXPENDITURE, String.class, null,getPropertyName("income"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_TOTAL, Double.class, null,getPropertyName("amount"), null, Align.LEFT);

		subTable.setColumnExpandRatio(TBC_SN, (float) 0.3);
		subTable.setColumnExpandRatio(TBC_DATE, (float) 1);
		subTable.setColumnExpandRatio(TBC_FROM, 1);
		subTable.setColumnExpandRatio(TBC_EXPENDITURE, (float) 2);
		subTable.setColumnExpandRatio(TBC_TOTAL, (float) 0.5);
		subTable.setSelectable(true);
		subTable.setMultiSelect(false);
		subTable.setVisibleColumns(visibleSubColumns);
		
		
		ledDao = new LedgerDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1150, 375);
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
		// mainFormLayout.addComponent(officeComboField);
		List lst = new ArrayList();
		lst.add(new KeyValue((int)0, "All"));
		lst.add(new KeyValue((int)1, "Customers"));
		lst.add(new KeyValue((int)2, "Suppliers"));

		modeRadio = new SRadioButton(getPropertyName("mode"), 200, lst, "intKey", "value");
		modeRadio.setHorizontal(true);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			incomeLedgerComboField = new SComboField(getPropertyName("income_ledger"), 200);

			mainFormLayout.addComponent(incomeLedgerComboField);

			mainFormLayout.addComponent(modeRadio);

			fromAcctComboField = new SComboField(getPropertyName("to_account"), 200, null, "id", "name",false, getPropertyName("all"));
			mainFormLayout.addComponent(fromAcctComboField);

			reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			generateConsolidatedButton = new SButton(getPropertyName("consolidated_report"));

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(generateConsolidatedButton);
			buttonHorizontalLayout.setComponentAlignment(generateConsolidatedButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(popHor);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			mainPanel.setContent(mainHorizontal);

			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});
			
			
			modeRadio.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {

					if (modeRadio.getValue() != null) {
						List actList = new ArrayList();
						try {
							if (((Integer) modeRadio.getValue()) == 1) {
								actList = ledDao.getAllActiveLedgerNamesUnderGroup(getSettings().getCUSTOMER_GROUP(),(Long) officeComboField.getValue());
							}
							else if (((Integer) modeRadio.getValue()) == 2) {
								actList = ledDao.getAllActiveLedgerNamesUnderGroup(getSettings().getSUPPLIER_GROUP(),(Long) officeComboField.getValue());
							} 
							else {
								actList = new ArrayList();
								actList = ledDao.getAllActiveLedgerNames((Long) officeComboField.getValue());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						LedgerModel ledg = new LedgerModel();
						ledg.setId(0);
						ledg.setName(getPropertyName("all"));
						if (actList == null) {
							actList = new ArrayList<Object>();
						}
						actList.add(0, ledg);
						CollectionContainer bic = CollectionContainer.fromBeans(actList, "id");
						fromAcctComboField.setContainerDataSource(bic);
						fromAcctComboField.setItemCaptionPropertyId("name");
						fromAcctComboField.setInputPrompt(getPropertyName("select"));
					}
				}
			});

			
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

			
			officeComboField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						List<Object> expentAcctList = new IncomeTransactionDao().getAllLedgersUnderType((Long) officeComboField.getValue(), INCOME);
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel.setName(getPropertyName("all"));
						if (expentAcctList == null) {
							expentAcctList = new ArrayList<Object>();
						}
						expentAcctList.add(0, ledgerModel);

						SCollectionContainer bic2 = SCollectionContainer.setList(expentAcctList, "id");
						incomeLedgerComboField.setContainerDataSource(bic2);
						incomeLedgerComboField.setItemCaptionPropertyId("name");

						if (modeRadio.getValue() != null) {
							List actList = new ArrayList();
							try {
								if (((Integer) modeRadio.getValue()) == 1) {
									actList = ledDao.getAllActiveLedgerNamesUnderGroup(getSettings().getCUSTOMER_GROUP(),(Long) officeComboField.getValue());
								}
								else if (((Integer) modeRadio.getValue()) == 2) {
									actList = ledDao.getAllActiveLedgerNamesUnderGroup(getSettings().getSUPPLIER_GROUP(),(Long) officeComboField.getValue());
								} 
								else {
									actList = new ArrayList();
									actList = ledDao.getAllActiveLedgerNames((Long) officeComboField.getValue());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							LedgerModel ledg = new LedgerModel();
							ledg.setId(0);
							ledg.setName(getPropertyName("all"));
							if (actList == null) {
								actList = new ArrayList<Object>();
							}
							actList.add(0, ledg);

							CollectionContainer bic = CollectionContainer.fromBeans(actList, "id");
							fromAcctComboField.setContainerDataSource(bic);
							fromAcctComboField.setItemCaptionPropertyId("name");
							fromAcctComboField.setInputPrompt(getPropertyName("select"));
						}
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
			modeRadio.setValue(0);

			
			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			
			final Action actionDelete = new Action("Edit");
			
			
			subTable.addActionHandler(new Handler() {
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (subTable.getValue() != null) {
							item = subTable.getItem(subTable.getValue());
							IncomeTransactionUI option=new IncomeTransactionUI();
							option.setCaption(getPropertyName("income_transactions"));
							option.getBillNoFiled().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							popUp.setPopupVisible(false);
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
			
			
			subTable.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item item = subTable.getItem(subTable.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							PaymentDepositModel mdl=new IncomeTransactionDao().getIncomeTransaction(id);
							TransactionModel tr=mdl.getTransaction();
							TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("income")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("bill"),mdl.getBill_no()+""));
							form.addComponent(new SLabel(getPropertyName("to_account"),item.getItemProperty(TBC_FROM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("income"),item.getItemProperty(TBC_EXPENDITURE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("total"),item.getItemProperty(TBC_TOTAL).getValue().toString()));
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
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							subTable.removeAllItems();
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							List<Object> reportList;
							long toAcct = 0;

							if (fromAcctComboField.getValue() != null && !fromAcctComboField.getValue().equals("") && !fromAcctComboField.getValue().toString().equals("0")) {
								toAcct = (Long) fromAcctComboField.getValue();
							}
							reportList = new IncomeTransactionDao().getIncomeReport(
																			toAcct,
																			id,
																			CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																			CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																			(Long) officeComboField.getValue());
							subTable.setVisibleColumns(allSubColumns);
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									subTable.addItem(new Object[]{
											subTable.getItemIds().size()+1,
                                            bean.getId(),
                                            bean.getDt().toString(),
                                            bean.getParticulars(),
                                            bean.getFrom_acct(),
                                            bean.getTo_acct(),
                                            bean.getAmount()},subTable.getItemIds().size()+1);
								}
							}
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Expense Details",725), closeBtn), subTable));

							popHor.addComponent(popUp);
							popUp.setPopupVisible(true);
							popUp.setHideOnMouseOut(false);
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
							long toAcct = 0;
							long income = 0;

							if (fromAcctComboField.getValue() != null && !fromAcctComboField.getValue().equals("") && !fromAcctComboField.getValue().toString().equals("0")) {
								toAcct = (Long) fromAcctComboField.getValue();
							}
							if (incomeLedgerComboField.getValue() != null && !incomeLedgerComboField.getValue() .equals("")) {
								income = toLong(incomeLedgerComboField.getValue().toString());
							}
							reportList = new IncomeTransactionDao().showIncomeReport(
																				toAcct,
																				income,
																				CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																				CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																				(Long) officeComboField.getValue());
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getName(),
											roundNumber(bean.getAmount())},table.getItemIds().size()+1);
								}
							}
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
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

			
			generateConsolidatedButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (officeComboField.getValue() != null) {

							List<Object> reportList;
							long fromActId = 0;
							long income = 0;

							if (fromAcctComboField.getValue() != null && !fromAcctComboField.getValue().equals("") && !fromAcctComboField.getValue().toString().equals("0")) {
								fromActId = (Long) fromAcctComboField.getValue();
							}
							if (incomeLedgerComboField.getValue() != null && !incomeLedgerComboField.getValue() .equals("")) {
								income = toLong(incomeLedgerComboField.getValue().toString());
							}
							reportList = new IncomeTransactionDao().showIncomeReport(
																				fromActId,
																				income,
																				CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																				CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																				(Long) officeComboField.getValue());
							if(reportList.size()>0){
								HashMap<String, Object> params = new HashMap<String, Object>();//expenditure_account
								report.setJrxmlFileName("Consolidated_3_1_Report");
								report.setReportFileName("ConsolidatedReport");
								
								params.put("REPORT_TITLE_LABEL", getPropertyName("income_transaction_report"));
								params.put("SL_NO_LABEL", getPropertyName("sl_no"));
								params.put("CUSTOMER_LABEL", getPropertyName("income_account"));
								params.put("AMOUNT_LABEL", getPropertyName("amount"));
								params.put("TOTAL_LABEL", getPropertyName("total"));
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ incomeLedgerComboField
													.getItemCaption(incomeLedgerComboField
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

								params.put("Office", officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								params.put(
										"Organization",
										organizationComboField
												.getItemCaption(organizationComboField
														.getValue()));

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, params);

								reportList.clear();
							}
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
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
			
			
			generateButton.addClickListener(new ClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List<Object> reportList;
							long fromActId = 0;
							long income = 0;

							if (fromAcctComboField.getValue() != null && !fromAcctComboField.getValue().equals("") && !fromAcctComboField.getValue().toString().equals("0")) {
								fromActId = (Long) fromAcctComboField.getValue();
							}
							if (incomeLedgerComboField.getValue() != null && !incomeLedgerComboField.getValue() .equals("")) {
								income = toLong(incomeLedgerComboField.getValue().toString());
							}
							reportList = new IncomeTransactionDao().getIncomeReport(
																				fromActId,
																				income,
																				CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																				CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																				(Long) officeComboField.getValue());

							if (reportList.size() > 0) {

								HashMap<String, Object> params = new HashMap<String, Object>();//expenditure_account
								report.setJrxmlFileName("ExpendetureTransactionReport");
								report.setReportFileName("Expendeture Transaction Report");
								
								params.put("REPORT_TITLE_LABEL", getPropertyName("income_transaction_report"));
								params.put("SL_NO_LABEL", getPropertyName("sl_no"));
								params.put("DATE_LABEL", getPropertyName("date"));
								params.put("BILL_NO_LABEL", getPropertyName("bill_no"));
								params.put("FROM_ACCOUNT_LABEL", getPropertyName("to_account"));
								params.put("EXPENDITURE_ACCOUNT_LABEL", getPropertyName("income_account"));
								params.put("AMOUNT_LABEL", getPropertyName("amount"));
								params.put("TOTAL_LABEL", getPropertyName("total"));
								params.put("organization_label", getPropertyName("organization"));
								params.put("office_label", getPropertyName("office"));
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ incomeLedgerComboField
													.getItemCaption(incomeLedgerComboField
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

								params.put("Office", officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								params.put(
										"Organization",
										organizationComboField
												.getItemCaption(organizationComboField
														.getValue()));

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, params);

								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
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
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}

	/*
	 * private void loadBillNo(long customerId, long officeId) { List<Object>
	 * salesList = null; try { if (customerId != 0) { salesList = new SalesDao()
	 * .getAllSalesNumbersForSupplier(officeId, customerId,
	 * CommonUtil.getSQLDateFromUtilDate(fromDateField .getValue()), CommonUtil
	 * .getSQLDateFromUtilDate(toDateField .getValue())); } else { salesList =
	 * new SalesReportDao() .getAllSalesNumbersAsComment(officeId); } SalesModel
	 * salesModel = new SalesModel(); salesModel.setId(0); salesModel
	 * .setComments("---------------------ALL-------------------"); if
	 * (salesList == null) { salesList = new ArrayList<Object>(); }
	 * salesList.add(0, salesModel); container =
	 * SCollectionContainer.setList(salesList, "id");
	 * fromAcctComboField.setContainerDataSource(container);
	 * fromAcctComboField.setItemCaptionPropertyId("comments");
	 * fromAcctComboField.setValue(0); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

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
			incomeLedgerComboField.setContainerDataSource(custContainer);
			incomeLedgerComboField.setItemCaptionPropertyId("name");
			incomeLedgerComboField.setValue(0);
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
