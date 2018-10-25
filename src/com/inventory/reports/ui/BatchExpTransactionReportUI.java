package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.expenditureposting.dao.ExpenditurePaymentSetupDao;
import com.inventory.expenditureposting.model.BatchExpenditurePaymentMasterModel;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupModel;
import com.inventory.expenditureposting.ui.ExpenditurePaymentsPayUI;
import com.vaadin.data.Item;
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
 * @author Jinshad P.T. WebSpark.
 * @Date Mar 10 2014
 */

public class BatchExpTransactionReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField groupCombo;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	ExpenditurePaymentSetupDao daoObj;
	
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_TRANSACTION = "Group";
	static String TBC_FROM = "From Acct";
	static String TBC_TO = "To Acct";
	static String TBC_ACTUAL = "Actual";
	static String TBC_PAID = "Paid";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {

		daoObj = new ExpenditurePaymentSetupDao();

		customerId = 0;
		report = new Report(getLoginID());
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DATE,TBC_TRANSACTION, TBC_FROM,TBC_TO,TBC_ACTUAL,TBC_PAID};
		visibleColumns = new Object[] { TBC_SN, TBC_DATE,TBC_TRANSACTION, TBC_FROM,TBC_TO,TBC_ACTUAL,TBC_PAID};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 775, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_TRANSACTION, String.class, null,getPropertyName("group"), null, Align.LEFT);
		table.addContainerProperty(TBC_FROM, String.class, null,getPropertyName("from_account"), null, Align.LEFT);
		table.addContainerProperty(TBC_TO, String.class, null,getPropertyName("to_account"), null, Align.LEFT);
		table.addContainerProperty(TBC_ACTUAL, Double.class, null,getPropertyName("actual_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_PAID, Double.class, null,getPropertyName("paid"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 1);
		table.setColumnExpandRatio(TBC_TRANSACTION, (float) 1.5);
		table.setColumnExpandRatio(TBC_FROM, (float) 1.5);
		table.setColumnExpandRatio(TBC_TO, (float) 1.5);
		table.setColumnExpandRatio(TBC_ACTUAL, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAID, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		
		setSize(1150, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField(getPropertyName("from_date"), 150,getDateFormat());
		toDateField = new SDateField(getPropertyName("to_date"), 150,getDateFormat());

		fromDateField.setValue(getMonthStartDate());
		toDateField.setValue(getWorkingDate());
		

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			groupCombo = new SComboField(getPropertyName("group"), 200, null,
					"id", "name", false, getPropertyName("all"));

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(groupCombo);
			mainFormLayout.addComponent(fromDateField);
			mainFormLayout.addComponent(toDateField);

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
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadGroupCombo(toLong(officeComboField.getValue()
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
			
			final Action action = new Action(getPropertyName("edit"));
			
			table.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							ExpenditurePaymentsPayUI option=new ExpenditurePaymentsPayUI(); 
							option.setCaption(getPropertyName("expenditure_posting_payment"));
							option.getBatchExpPaymentNumbersCombo().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
					return new Action[] { action };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							BatchExpenditurePaymentMasterModel mdl=new ExpenditurePaymentSetupDao().getBatchHistory(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("batch_expendeture")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("batch"),mdl.getNumber()+""));
							form.addComponent(new SLabel(getPropertyName("group"),item.getItemProperty(TBC_TRANSACTION).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("from_account"),item.getItemProperty(TBC_FROM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("to_account"),item.getItemProperty(TBC_TO).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("actual_amount"),item.getItemProperty(TBC_ACTUAL).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("paid_amount"),item.getItemProperty(TBC_PAID).getValue().toString()));
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
						List<Object> reportList;
						long emp_id = 0;

						if (groupCombo.getValue() != null
								&& !groupCombo.getValue().toString()
										.equals("0")) {
							emp_id = (Long) groupCombo.getValue();
						}

						reportList = daoObj.getPaymentHistoryReport(
									(Long) officeComboField.getValue(), 
									emp_id,
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
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
										bean.getReal_amount(),
										bean.getAmount()},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
						
						table.setVisibleColumns(visibleColumns);
						
						
						
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

						List<Object> reportList;
						long emp_id = 0;

						if (groupCombo.getValue() != null
								&& !groupCombo.getValue().toString()
										.equals("0")) {
							emp_id = (Long) groupCombo.getValue();
						}

						reportList = daoObj.getPaymentHistoryReport(
								(Long) officeComboField.getValue(), emp_id,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));

						if (reportList.size() > 0) {
							report.setJrxmlFileName("BatchExpTransactionsReport");
							report.setReportFileName("Batch Exp Transactions Report");
							HashMap<String, Object> map = new HashMap<String, Object>();
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("batch_expenditure_transaction_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("GROUP_LABEL", getPropertyName("group"));
							map.put("TRANSACTION_LABEL", getPropertyName("transaction"));
							map.put("NUMBER_LABEL", getPropertyName("number"));
							map.put("FROM_ACCOUNT_LABEL", getPropertyName("from_account"));
							map.put("TO_ACCOUNT_LABEL", getPropertyName("to_account"));
							map.put("ACTUAL_AMOUNT_LABEL", getPropertyName("actual_amount"));
							map.put("PAID_ACCOUNT_LABEL", getPropertyName("paid_account"));
							map.put("NARRATION_LABEL", getPropertyName("narration"));

							String subHeader = "";

							if (groupCombo.getValue() != null)
								if (!groupCombo.getValue().toString()
										.equals("0"))
									subHeader += getPropertyName("group")+" : "
											+ groupCombo
													.getItemCaption(groupCombo
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

	protected void loadGroupCombo(long officeId) {
		List<Object> custList = null;
		try {

			if (officeId != 0) {
				custList = new ExpenditurePaymentSetupDao()
						.getAllSetups(officeId);
			}
			ExpenditurePaymentSetupModel objModel = new ExpenditurePaymentSetupModel();
			objModel.setId(0);
			objModel.setGroup_name(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, objModel);

			custContainer = SCollectionContainer.setList(custList, "id");
			groupCombo.setContainerDataSource(custContainer);
			groupCombo.setItemCaptionPropertyId("group_name");
			groupCombo.setValue(getLoginID());

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
