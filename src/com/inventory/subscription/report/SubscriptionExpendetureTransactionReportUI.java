package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.ui.SubscripitonExpendetureTransactionUI;
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
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T. Inventory Dec 31, 2013
 */
public class SubscriptionExpendetureTransactionReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField expendetureLedgerComboField;
	private SComboField fromAcctComboField;
	private SReportChoiceField reportChoiceField;
	SComboField subscriptionCombo;

	private SPanel mainPanel;

	static final long EXPENSE = 4;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

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
	static String TBC_VEHICLE = "Vehicle";
	static String TBC_TOTAL = "Amount";
	SHorizontalLayout popupContainer;
	Object[] allColumns;
	Object[] visibleColumns;
	SHorizontalLayout mainHorizontal;
	STable table;
	SButton showButton;

	@SuppressWarnings({ "unchecked", "deprecation", "serial" })
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_BILL, TBC_FROM, TBC_EXPENDITURE,TBC_VEHICLE, TBC_TOTAL };
		visibleColumns = new Object[] { TBC_SN, TBC_DATE,TBC_BILL, TBC_FROM, TBC_EXPENDITURE, TBC_VEHICLE,TBC_TOTAL };
		popupContainer = new SHorizontalLayout();
		mainHorizontal=new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("bill"), null, Align.LEFT);
		table.addContainerProperty(TBC_FROM, String.class, null,getPropertyName("from_account"), null, Align.LEFT);
		table.addContainerProperty(TBC_EXPENDITURE, String.class, null,getPropertyName("expendeture"), null, Align.LEFT);
		table.addContainerProperty(TBC_VEHICLE, String.class, null,getPropertyName("vehicle"), null, Align.LEFT);
		table.addContainerProperty(TBC_TOTAL, Double.class, null,getPropertyName("amount"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 1);
		table.setColumnExpandRatio(TBC_FROM, 1);
		table.setColumnExpandRatio(TBC_EXPENDITURE, (float) 1);
		table.setColumnExpandRatio(TBC_VEHICLE, (float) 1);
		table.setColumnExpandRatio(TBC_TOTAL, (float) 0.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		ledDao = new LedgerDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1050, 400);
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
		subscriptionCombo=new SComboField(getPropertyName("rental"), 200);
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		modeRadio = new SRadioButton(getPropertyName("mode"), 200,
				Arrays.asList(new KeyValue((int) 1, "Cash Only"), new KeyValue(
						(int) 2, "Bank & Cash")), "intKey", "value");
		modeRadio.setHorizontal(true);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			expendetureLedgerComboField = new SComboField(
					getPropertyName("expenditure_ledger"), 200);

			mainFormLayout.addComponent(expendetureLedgerComboField);

			mainFormLayout.addComponent(modeRadio);

			fromAcctComboField = new SComboField(
					getPropertyName("from_account"), 200, null, "id", "name",
					false, " ALL ");
			mainFormLayout.addComponent(fromAcctComboField);
			mainFormLayout.addComponent(subscriptionCombo);
			
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

			modeRadio.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {

					if (modeRadio.getValue() != null) {
						List actList = null;
						List rentalList=null;
						try {
							rentalList=new SubscriptionCreationDao().getAllSubscriptions((Long) officeComboField
									.getValue(),getLoginID());
							if (((Integer) modeRadio.getValue()) == 1) {
								actList = ledDao
										.getAllLedgersUnderGroupAndSubGroups(
												(Long) officeComboField
														.getValue(),
												(Long) organizationComboField
														.getValue(),
												getSettings().getCASH_GROUP());
							} else {
								actList = new ArrayList();
								actList.addAll(ledDao
										.getAllLedgersUnderGroupAndSubGroups(
												(Long) officeComboField
														.getValue(),
												(Long) organizationComboField
														.getValue(),
														getSettings().getCASH_GROUP()));
//								actList.addAll(ledDao.getAllLedgersUnderGroupAndSubGroups(
//										(Long) officeComboField.getValue(),
//										(Long) organizationComboField
//												.getValue(),
//										SConstants.BANK_ACCOUNT_GROUP_ID));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						LedgerModel ledg = new LedgerModel();
						SubscriptionCreationModel cmdl=new SubscriptionCreationModel();
						cmdl.setId(0);
						cmdl.setName("----------------ALL-----------------");
						ledg.setId(0);
						ledg.setName("----------------ALL-----------------");
						if (actList == null) {
							actList = new ArrayList<Object>();
						}
						if (rentalList == null) {
							rentalList = new ArrayList<Object>();
						}
						actList.add(0, ledg);
						rentalList.add(0,cmdl);
						
						CollectionContainer bic = CollectionContainer.fromBeans(actList, "id");
						fromAcctComboField.setContainerDataSource(bic);
						fromAcctComboField.setItemCaptionPropertyId("name");
						fromAcctComboField.setInputPrompt(getPropertyName("select"));
						
						SCollectionContainer rbic = SCollectionContainer.setList(rentalList, "id");
						subscriptionCombo.setContainerDataSource(rbic);
						subscriptionCombo.setItemCaptionPropertyId("name");
						subscriptionCombo.setInputPrompt(getPropertyName("select"));

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

						List<Object> expentAcctList = new ExpendetureTransactionDao()
								.getAllDirectAddedLedgersUnderType(
										(Long) officeComboField.getValue(),
										EXPENSE);
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel
								.setName("---------------------ALL-------------------");
						if (expentAcctList == null) {
							expentAcctList = new ArrayList<Object>();
						}
						expentAcctList.add(0, ledgerModel);

						SCollectionContainer bic2 = SCollectionContainer
								.setList(expentAcctList, "id");
						expendetureLedgerComboField
								.setContainerDataSource(bic2);
						expendetureLedgerComboField
								.setItemCaptionPropertyId("name");

						if (modeRadio.getValue() != null) {
							List actList = null;
							try {

								if (((Integer) modeRadio.getValue()) == 1) {
									actList = ledDao
											.getAllLedgersUnderGroupAndSubGroups(
													(Long) officeComboField
															.getValue(),
													(Long) organizationComboField
															.getValue(),
															getSettings().getCASH_GROUP());
								} else {
									actList = new ArrayList();
									actList.addAll(ledDao
											.getAllLedgersUnderGroupAndSubGroups(
													(Long) officeComboField
															.getValue(),
													(Long) organizationComboField
															.getValue(),
															getSettings().getCASH_GROUP()));
//									actList.addAll(ledDao
//											.getAllLedgersUnderGroupAndSubGroups(
//													(Long) officeComboField
//															.getValue(),
//													(Long) organizationComboField
//															.getValue(),
//													SConstants.BANK_ACCOUNT_GROUP_ID));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							LedgerModel ledg = new LedgerModel();
							ledg.setId(0);
							ledg.setName("----------------ALL-----------------");
							if (actList == null) {
								actList = new ArrayList<Object>();
							}
							actList.add(0, ledg);

							CollectionContainer bic = CollectionContainer
									.fromBeans(actList, "id");
							fromAcctComboField.setContainerDataSource(bic);
							fromAcctComboField.setItemCaptionPropertyId("name");
							fromAcctComboField
									.setInputPrompt(getPropertyName("select"));

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
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
			modeRadio.setValue(1);
			fromAcctComboField.setValue((long)0);
			expendetureLedgerComboField.setValue((long)0);
			subscriptionCombo.setValue((long)0);
			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action("Edit");
			
			table.addActionHandler(new Handler() {
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SubscripitonExpendetureTransactionUI option=new SubscripitonExpendetureTransactionUI();
							option.setCaption("Rental Expendeture Transaction");
							option.getAccountDepositNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							PaymentDepositModel mdl=new ExpendetureTransactionDao().getExpendetureTransaction(id);
							TransactionModel tr=mdl.getTransaction();
							TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("expendeture")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("bill"),mdl.getBill_no()+""));
							form.addComponent(new SLabel(getPropertyName("from_account"),item.getItemProperty(TBC_FROM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("expendeture"),item.getItemProperty(TBC_EXPENDITURE).getValue().toString()));
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
			
			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						
						if (officeComboField.getValue() != null) {

							List<Object> reportList;
							long fromActId = 0;
							long expId = 0;

							if (fromAcctComboField.getValue() != null
									&& !fromAcctComboField.getValue()
											.equals("")
									&& !fromAcctComboField.getValue()
											.toString().equals("0")) {
								fromActId = (Long) fromAcctComboField
										.getValue();
							}
							if (expendetureLedgerComboField.getValue() != null
									&& !expendetureLedgerComboField.getValue()
											.equals("")) {
								expId = toLong(expendetureLedgerComboField
										.getValue().toString());
							}
							reportList = new ExpendetureTransactionDao().getSubscriptionExpendetureReport(
																		fromActId,
																		expId,
																		CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																		CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																		(Long) officeComboField.getValue(),(Long) subscriptionCombo.getValue());
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getDt().toString(),
											bean.getParticulars(),
											bean.getFrom_acct(),
											bean.getTo_acct(),
											bean.getActivity(),
											bean.getAmount()},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show("No Data Available",Type.WARNING_MESSAGE);
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
							long expId = 0;

							if (fromAcctComboField.getValue() != null
									&& !fromAcctComboField.getValue()
											.equals("")
									&& !fromAcctComboField.getValue()
											.toString().equals("0")) {
								fromActId = (Long) fromAcctComboField
										.getValue();
							}
							if (expendetureLedgerComboField.getValue() != null
									&& !expendetureLedgerComboField.getValue()
											.equals("")) {
								expId = toLong(expendetureLedgerComboField
										.getValue().toString());
							}

							reportList = new ExpendetureTransactionDao()
									.getSubscriptionExpendetureReport(
											fromActId,
											expId,
											CommonUtil
													.getSQLDateFromUtilDate(fromDateField
															.getValue()),
											CommonUtil
													.getSQLDateFromUtilDate(toDateField
															.getValue()),
											(Long) officeComboField.getValue(),(Long) subscriptionCombo.getValue());

							if (reportList.size() > 0) {

								HashMap<String, Object> params = new HashMap<String, Object>();

								report.setJrxmlFileName("RentalExpendetureTransactionReport");
								report.setReportFileName("Rental Expendeture Transaction Report");
								report.setReportTitle("Rental Expendeture Transaction Report");
								String subHeader = "";
								if (customerId != 0) {
									subHeader += "Customer : "
											+ expendetureLedgerComboField
													.getItemCaption(expendetureLedgerComboField
															.getValue()) + "\t";
								}

								subHeader += "\n From : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t To : "
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
			expendetureLedgerComboField.setContainerDataSource(custContainer);
			expendetureLedgerComboField.setItemCaptionPropertyId("name");
			expendetureLedgerComboField.setValue(0);
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
