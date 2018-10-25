package com.inventory.payroll.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.dao.CommissionSalaryDao;
import com.inventory.payroll.dao.EmployeeWorkingTimeDao;
import com.inventory.payroll.dao.PayrollEmployeeMapDao;
import com.inventory.payroll.dao.SalaryDisbursalDao;
import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.inventory.payroll.model.SalaryDisbursalDetailsModel;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.reports.bean.SalaryDisbursalBean;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.model.UserModel;

public class SalaryDisbursalNewUI extends SparkLogic {

	private static final long serialVersionUID = -8859980081504449687L;

	private STable table;
	private STextField reductionField;
	private STextField paymentAmountField;
	private STextField commissionField;
	private STextField overTimeField;
	private STextField rateField;
	
	private SDateField disbursalDateField;
	// private SDateField fromDateField;
	// private SDateField toDateField;
	public InlineDateField forMonthDateField;

	private SalaryDisbursalDao dao;
	private EmployeeWorkingTimeDao workingDao;
	private PayrollEmployeeMapDao payrollMapDao;

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	private static final String TBL_SELECT = "Select";
	private static final String TBL_SALARY_ID = "Id";
	private static final String TBL_EMP_ID = "Emp Id";
	private static final String TBL_EMP_NAME = "Employee";
	private static final String TBL_SALARY_TYPE_ID = "Salary Type Id";
	private static final String TBL_SALARY_TYPE = "Salary Type";
	private static final String TBL_WORKING_TIME = "Worked Days";
	private static final String TBL_SALARY = "Payment Amount";
	private static final String TBL_COMMISSION = "Commission Salary";
	private static final String TBL_PREVIOUS = "Previous Due";
	private static final String TBL_OVER_TIME = "Extra Work Time";
	private static final String TBL_RATE = "Rate";
	private static final String TBL_EXTRA = "Extra Pay";
	private static final String TBL_REDUCTION = "Advance to pay";
	private static final String TBL_NET_SALARY = "Net Salary";
	private static final String TBL_STATUS_ID = "Status Id";
	private static final String TBL_STATUS = "Status";
	private static final String TBL_COMMISSION_STATUS = "Commission Status";
	

	private SButton payButton;
	private SButton deleteButton;
	private SButton addButton;
	private SButton printButton;

	private Calendar cal;

	private SettingsValuePojo settings;
	private WrappedSession session;
	private SHorizontalLayout tableLayout;
	private SFormLayout popFormLayout;

	Calendar startCalendar;
	Calendar endCalendar;
	SFormLayout commissionLayout;
	double prevAmount=0;
	private SCheckBox allBox;
	

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		setSize(1200, 470);
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		SPanel mainPanel = new SPanel();
		mainPanel.setSizeFull();
		dao = new SalaryDisbursalDao();
		workingDao = new EmployeeWorkingTimeDao();
		payrollMapDao = new PayrollEmployeeMapDao();
		startCalendar=getCalendar();
		endCalendar=getCalendar();
		startCalendar.set(Calendar.DATE, 1);
		endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE));
		final SVerticalLayout mainFormLayout = new SVerticalLayout();
		mainFormLayout.setMargin(true);
		mainFormLayout.setSpacing(true);
		final SGridLayout buttGrid = new SGridLayout();
		buttGrid.setRows(1);
		buttGrid.setColumns(8);
		SGridLayout dateGridLayout = new SGridLayout();
		dateGridLayout.setRows(1);
		dateGridLayout.setColumns(8);
		dateGridLayout.setSpacing(true);
		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);
		mainPanel.setContent(mainFormLayout);
		forMonthDateField = new InlineDateField();
		forMonthDateField.setResolution(Resolution.MONTH);
		forMonthDateField.setImmediate(true);

		// fromDateField = new SDateField("From Date");
		// toDateField = new SDateField("To Date");

		cal = Calendar.getInstance();
		cal.setTime(getWorkingDate());
		cal.set(Calendar.DATE, 1);
		forMonthDateField.setValue(cal.getTime());

		// fromDateField.setValue(cal.getTime());
		// cal.set(Calendar.DATE, cal.getMaximum(Calendar.DATE));
		// cal.add(Calendar.DATE, -1);
		// toDateField.setValue(cal.getTime());
		commissionLayout=new SFormLayout();
		disbursalDateField = new SDateField(null, 100, getDateFormat(),
				getWorkingDate());

		reductionField = new STextField(getPropertyName("advance_pay"), 100);
		paymentAmountField = new STextField(getPropertyName("payment_amount"), 100);
		overTimeField = new STextField(getPropertyName("over_time"), 100);
		rateField = new STextField(getPropertyName("rate_hour"), 100);
		commissionField = new STextField(getPropertyName("commission_amount"), 100);
		overTimeField.setValue("0");
		rateField.setValue("0");
		commissionField.setReadOnly(true);
		
		paymentAmountField.setImmediate(true);
		overTimeField.setImmediate(true);
		rateField.setImmediate(true);
		commissionField.setImmediate(true);
		commissionLayout.addComponent(commissionField);
		allHeaders = new String[] { TBL_SELECT, TBL_SALARY_ID, TBL_EMP_ID, TBL_EMP_NAME,TBL_SALARY_TYPE_ID, TBL_SALARY_TYPE, TBL_WORKING_TIME,
				TBL_SALARY, TBL_COMMISSION, TBL_PREVIOUS, TBL_OVER_TIME, TBL_RATE, TBL_EXTRA, TBL_REDUCTION, TBL_NET_SALARY, TBL_STATUS_ID,TBL_STATUS,TBL_COMMISSION_STATUS };

		requiredHeaders = new String[] { TBL_SELECT, TBL_EMP_NAME, TBL_SALARY_TYPE,
				TBL_WORKING_TIME, TBL_COMMISSION, TBL_PREVIOUS,TBL_OVER_TIME, TBL_RATE, TBL_EXTRA,TBL_SALARY, TBL_REDUCTION, TBL_NET_SALARY,TBL_STATUS };

		allBox = new SCheckBox(getPropertyName("select_all"));

		table = new STable(null, 1150, 250);

		table.addContainerProperty(TBL_SELECT, SCheckBox.class, null,getPropertyName("select"), null, Align.CENTER);
		table.addContainerProperty(TBL_SALARY_ID, Long.class, null, TBL_SALARY_ID,null, Align.CENTER);
		table.addContainerProperty(TBL_EMP_ID, Long.class, null, TBL_EMP_ID,null, Align.CENTER);
		table.addContainerProperty(TBL_EMP_NAME, String.class, null,getPropertyName("employee"), null, Align.LEFT);
		table.addContainerProperty(TBL_SALARY_TYPE_ID, Integer.class, null,TBL_SALARY_TYPE_ID, null, Align.CENTER);
		table.addContainerProperty(TBL_SALARY_TYPE, String.class, null,getPropertyName("salary_type"), null, Align.LEFT);
		table.addContainerProperty(TBL_WORKING_TIME, Double.class, null,getPropertyName("worked_days"), null, Align.RIGHT);
		table.addContainerProperty(TBL_SALARY, Double.class, null,getPropertyName("payment_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBL_COMMISSION, Double.class, null,getPropertyName("commission_salary"), null, Align.RIGHT);
		table.addContainerProperty(TBL_PREVIOUS, Double.class, null,getPropertyName("previous_due"), null, Align.RIGHT);
		table.addContainerProperty(TBL_OVER_TIME, Double.class, null,getPropertyName("over_time"), null, Align.RIGHT);
		table.addContainerProperty(TBL_EXTRA, Double.class, null,getPropertyName("over_time_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBL_RATE, Double.class, null,getPropertyName("rate_hour"), null, Align.RIGHT);
		table.addContainerProperty(TBL_REDUCTION, Double.class, null,getPropertyName("advance_to_pay"), null, Align.RIGHT);
		table.addContainerProperty(TBL_NET_SALARY, Double.class, null,getPropertyName("net_salary"), null, Align.RIGHT);
		table.addContainerProperty(TBL_STATUS_ID, Long.class, null,TBL_STATUS_ID, null, Align.RIGHT);
		table.addContainerProperty(TBL_STATUS, String.class, null,getPropertyName("status"), null, Align.LEFT);
		table.addContainerProperty(TBL_COMMISSION_STATUS, Long.class, null,TBL_COMMISSION_STATUS, null, Align.LEFT);

		table.setColumnExpandRatio(TBL_SELECT, 0.5f);
		table.setColumnExpandRatio(TBL_EMP_NAME, 2f);
		table.setColumnExpandRatio(TBL_SALARY_TYPE_ID, 0.5f);
		table.setColumnExpandRatio(TBL_SALARY_TYPE, 1f);
		table.setColumnExpandRatio(TBL_WORKING_TIME, 1f);
		table.setColumnExpandRatio(TBL_SALARY, 1f);
		table.setColumnExpandRatio(TBL_COMMISSION, 1f);
		table.setColumnExpandRatio(TBL_PREVIOUS, 1f);
		table.setColumnExpandRatio(TBL_OVER_TIME, 1f);
		table.setColumnExpandRatio(TBL_RATE, 1f);
		table.setColumnExpandRatio(TBL_EXTRA, 1f);
		table.setColumnExpandRatio(TBL_REDUCTION, 1f);
		table.setColumnExpandRatio(TBL_NET_SALARY, 1f);
		table.setColumnExpandRatio(TBL_STATUS_ID, 0.5f);
		table.setColumnExpandRatio(TBL_STATUS, 1.0f);

		table.setSelectable(true);
//		loadData();

		tableLayout = new SHorizontalLayout();
		tableLayout.setSizeFull();

		popFormLayout = new SFormLayout();

		addButton = new SButton("Add");
		addButton.setClickShortcut(KeyCode.ENTER);
		buttGrid.addComponent(commissionLayout, 0, 0);
		buttGrid.addComponent(new SFormLayout(overTimeField), 1, 0);
		buttGrid.addComponent(new SFormLayout(rateField), 2, 0);
		buttGrid.addComponent(new SFormLayout(paymentAmountField), 3, 0);
		buttGrid.addComponent(addButton, 4, 0);
		buttGrid.setComponentAlignment(addButton, Alignment.MIDDLE_CENTER);

		payButton = new SButton(getPropertyName("pay"));
		payButton.setIcon(new ThemeResource("icons/pay.png"));
		payButton.setStyleName("deletebtnStyle");
		printButton = new SButton(getPropertyName("print"));
		printButton.setIcon(new ThemeResource("icons/print.png"));
		printButton.setStyleName("deletebtnStyle");
		deleteButton = new SButton(getPropertyName("delete"));
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");

		// dateHorizontalLayout.addComponent(fromDateField);
		// dateHorizontalLayout.addComponent(toDateField);

		dateGridLayout.addComponent(new SLabel(getPropertyName("month")), 0, 0);
		dateGridLayout.addComponent(forMonthDateField, 1, 0);
		dateGridLayout.addComponent(popFormLayout, 3, 0);
		dateGridLayout.addComponent(new SLabel(
				getPropertyName("dispursal_date")), 4, 0);
		dateGridLayout.addComponent(disbursalDateField, 6, 0);
		mainFormLayout.addComponent(dateGridLayout);
		// mainFormLayout.addComponent(dateHorizontalLayout);

		mainFormLayout.addComponent(allBox);
		mainFormLayout.addComponent(table);

		// mainFormLayout.addComponent(buttGrid);

		SGridLayout gridLayout = new SGridLayout();
		gridLayout.setRows(1);
		gridLayout.setColumns(8);
		gridLayout.setSizeFull();

		SHorizontalLayout hLay = new SHorizontalLayout();
		hLay.setSpacing(true);

		hLay.addComponent(payButton);
		hLay.addComponent(deleteButton);
		hLay.addComponent(printButton);
		gridLayout.addComponent(hLay, 4, 0);
		mainFormLayout.addComponent(gridLayout);

		
		allBox.addValueChangeListener(new ValueChangeListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void valueChange(ValueChangeEvent event) {
				SCheckBox box = null;
				Item item;
				boolean flag = false;

				if (allBox.getValue())
					flag = true;

				Iterator itr = table.getItemIds().iterator();
				while (itr.hasNext()) {
					item = table.getItem(itr.next());
					box = (SCheckBox) item.getItemProperty(TBL_SELECT).getValue();
						box.setValue(flag);
				}
			}
		});

		
		
//		table.addValueChangeListener(new ValueChangeListener() {
//
//			@SuppressWarnings("rawtypes")
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//
//				reductionField.setValue("");
//				if (table.getValue() != null) {
//
//					Item item = table.getItem(table.getValue());
//					long employeeId = toLong(item.getItemProperty(TBL_EMP_ID).getValue().toString());
//					PayrollEmployeeMapModel mapMdl = null;
//					SLabel lbl = null;
//					String content = "";
//					long cid=0;
//					try {
//						List list = payrollMapDao.getPayRollMap(employeeId);
//						for (int i = 0; i < list.size(); i++) {
//							mapMdl = (PayrollEmployeeMapModel) list.get(i);
//							if(mapMdl.getComponent().getCommission()!=0){
//								cid=1;
//							}
//							content += mapMdl.getComponent().getName() + " : "+ mapMdl.getValue() + ", ";
//						}
//						double commission=roundNumber(toDouble(item.getItemProperty(TBL_COMMISSION).getValue().toString()) + 
//								toDouble(item.getItemProperty(TBL_PREVIOUS).getValue().toString()));
//						prevAmount=roundNumber(toDouble(item.getItemProperty(TBL_PREVIOUS).getValue().toString()));
//						double advance=roundNumber(toDouble(item.getItemProperty(TBL_REDUCTION).getValue().toString()));
//						double payment=roundNumber(toDouble(item.getItemProperty(TBL_SALARY).getValue().toString()));
//						double overTime=roundNumber(toDouble(item.getItemProperty(TBL_OVER_TIME).getValue().toString()));
//						double rate=roundNumber(toDouble(item.getItemProperty(TBL_RATE).getValue().toString()));
//						if(cid!=1){
//							commissionLayout.setVisible(false);
//						}
//						else{
//							commissionLayout.setVisible(true);
//						}
//						reductionField.setValue(advance+"");
//						paymentAmountField.setValue(payment+"");
//						overTimeField.setValue(overTime+"");
//						rateField.setValue(rate+"");
//						commissionField.setNewValue(commission+"");
//						
//						
//						lbl = new SLabel(content);
//						lbl.setStyleName(Reindeer.LABEL_H2);
//						SFormLayout conFormLayout = new SFormLayout();
//						conFormLayout.addComponent(lbl);
//						
//						conFormLayout.addComponent(buttGrid);
//						SPopupView pop = new SPopupView("", conFormLayout);
//						popFormLayout.removeAllComponents();
//						popFormLayout.addComponent(pop);
//						pop.setHideOnMouseOut(false);
//						pop.setPopupVisible(true);
//
//					} 
//					catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				else {
//					popFormLayout.removeAllComponents();
//					reductionField.setValue("");
//				}
//			}
//		});

		
		
		addButton.addClickListener(new ClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				reductionField.setComponentError(null);
				paymentAmountField.setComponentError(null);
				commissionField.setReadOnly(false);
				if (amountValid()) {
					double pay = toDouble(paymentAmountField.getValue().toString());
					double amount = toDouble(reductionField.getValue().toString());
					double commission = toDouble(commissionField.getValue().toString());
					double overTime = toDouble(overTimeField.getValue().toString());
					double rate = toDouble(rateField.getValue().toString());
					if(pay>=commission){
						table.getItem(table.getValue()).getItemProperty(TBL_SALARY).setValue(roundNumber(pay));
						table.getItem(table.getValue()).getItemProperty(TBL_OVER_TIME).setValue(roundNumber(overTime));
						table.getItem(table.getValue()).getItemProperty(TBL_RATE).setValue(roundNumber(rate));
						table.getItem(table.getValue()).getItemProperty(TBL_EXTRA).setValue(roundNumber(overTime*rate));
						table.getItem(table.getValue()).getItemProperty(TBL_REDUCTION).setValue(roundNumber(amount));
						table.getItem(table.getValue()).getItemProperty(TBL_NET_SALARY).setValue(roundNumber(pay - amount+(overTime*rate)));
						table.getItem(table.getValue()).getItemProperty(TBL_COMMISSION).setValue(roundNumber(commission - prevAmount));
						reductionField.setValue("0");
						overTimeField.setValue("0");
						rateField.setValue("0");
						paymentAmountField.setValue("0");
						commissionField.setNewValue("0");
						popFormLayout.removeAllComponents();
					}
					else{
						setRequiredError(paymentAmountField, getPropertyName("invalid_data"), true);
					}
				}
				commissionField.setReadOnly(true);
			}
		});

		
		
		forMonthDateField.addListener(new Listener() {

			@Override
			public void componentEvent(Event event) {
				try{
					if(forMonthDateField.getValue()!=null){
						startCalendar.setTime(forMonthDateField.getValue());
						endCalendar.setTime(forMonthDateField.getValue());
						
//						startCalendar.set(Calendar.DATE, 1);
						endCalendar.set(Calendar.DATE, endCalendar.getActualMaximum(Calendar.DATE));
						
//						loadData();
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		
		
//		payButton.addClickListener(new ClickListener() {
//
//			@SuppressWarnings({ "rawtypes", "unchecked" })
//			@Override
//			public void buttonClick(ClickEvent event) {
//				if (isValid()) {
//					try{
//						SalaryDisbursalNewModel model = null;
//						SalaryDisbursalDetailsModel obj = null;
//
//						SCheckBox box = null;
//						Hashtable<TransactionModel, SalaryDisbursalNewModel> hash = new Hashtable<TransactionModel, SalaryDisbursalNewModel>();
//
//						FinTransaction trans = null;
//						long ledgId = 0;
//						long emplId = 0;
//						double creditAmount = 0;
//
//						List<SalaryDisbursalDetailsModel> details;
//						List lst = null;
//						Item item;
//						Iterator it1;
//						PayrollEmployeeMapModel pem;
//						Iterator itr = table.getItemIds().iterator();
//						while (itr.hasNext()) {
//							creditAmount = 0;
//							item = table.getItem(itr.next());
//							box = (SCheckBox) item.getItemProperty(TBL_SELECT).getValue();
//
//							emplId = (Long) item.getItemProperty(TBL_EMP_ID).getValue();
//							trans = new FinTransaction();
//
//							if (box.getValue()) {
//								ledgId = dao.getLedgerFromEmployee(emplId);
//								
//								long id=toLong(item.getItemProperty(TBL_SALARY_ID).getValue().toString());
//								if(id!=0){
//									model=dao.getSalaryDisbursalNewModel(id);
//								}
//								else{
//									model = new SalaryDisbursalNewModel();
//								}
//								double comm = roundNumber(toDouble(item.getItemProperty(TBL_COMMISSION).getValue().toString()));
//								double prev = roundNumber(toDouble(item.getItemProperty(TBL_PREVIOUS).getValue().toString()));
//								double adv = roundNumber(toDouble(item.getItemProperty(TBL_REDUCTION).getValue().toString()));	
//								
//								double bal=roundNumber((comm+prev)-adv);
//								if(bal<0){
//									bal=Math.abs(bal);
//								}
//								else{
//									bal=0;
//								}
//								
//								model.setEmploy(new UserModel((Long) item.getItemProperty(TBL_EMP_ID).getValue()));
//								model.setStatus(SConstants.payroll.STATUS_PAID);
//								model.setMonth(CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()));
//								model.setDispursal_date(CommonUtil.getSQLDateFromUtilDate(disbursalDateField.getValue()));
//								model.setOver_time(roundNumber(toDouble(item.getItemProperty(TBL_OVER_TIME).getValue().toString())));
//								model.setRate_over_time(roundNumber(toDouble(item.getItemProperty(TBL_RATE).getValue().toString())));
//								model.setCommission_amount(roundNumber(comm));
//								model.setPrevious_amount(roundNumber(prev));
//								model.setAdvance_payed(roundNumber(adv));
//								model.setPaid_amount(roundNumber(toDouble(item.getItemProperty(TBL_SALARY).getValue().toString())));
//								model.setTotal_salary(roundNumber(toDouble(item.getItemProperty(TBL_NET_SALARY).getValue().toString())));
//								model.setBalance_amount(roundNumber(bal));
//								
//								details = new ArrayList<SalaryDisbursalDetailsModel>();
//
//								lst = new PayrollEmployeeMapDao().getPayRollMap(emplId);
//
//								if (lst.size() > 0) {
//									it1 = null;
//									it1 = lst.iterator();
//									while (it1.hasNext()) {
//										pem = (PayrollEmployeeMapModel) it1.next();
//
//										obj = new SalaryDisbursalDetailsModel();
//										obj.setComponent(pem.getComponent());
//
//										if (pem.getComponent().getAction() == SConstants.payroll.ADDITION) { // Addition
//											obj.setAmount(getAmount(pem.getComponent().getId(),emplId));
//										creditAmount += obj.getAmount();
//
//										} 
//										else { // Deduction
//											obj.setAmount(getAmount(pem.getComponent().getId(), model.getEmploy().getId()));
//											trans.addTransaction(SConstants.CR,
//																ledgId, 
//																pem.getComponent().getLedger().getId(),
//																Math.ceil(roundNumber(obj.getAmount())));
//										}
//										details.add(obj);
//									}
//									if(toDouble(item.getItemProperty(TBL_NET_SALARY).getValue().toString())!=creditAmount)
//										creditAmount=toDouble(item.getItemProperty(TBL_NET_SALARY).getValue().toString());
//									model.setDetailsList(details);
//								}
//								else{
//									creditAmount=toDouble(item.getItemProperty(TBL_NET_SALARY).getValue().toString());
//									model.setDetailsList(null);
//								}
//								
//								trans.addTransaction(SConstants.CR,settings.getSALARY_PAYMENT_ACCOUNT(),ledgId,Math.ceil(roundNumber(creditAmount)));
//									
//								TransactionModel transaction=null;
//								if(model.getId()!=0){
//									transaction=dao.getTransactionModel(model.getTransaction_id());
//									transaction.setTransaction_details_list(trans.getChildList());
//									transaction.setDate(model.getDispursal_date());
//									transaction.setLogin_id(getLoginID());
//								}
//								else{
//									transaction=trans.getTransaction(SConstants.PAYROLL_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(disbursalDateField.getValue()));
//								}
//								hash.put(transaction,model);
//							}
//						}
//						if(hash.size()>0){
//							dao.saveEmployeeSalaryDisbursal(hash);
//							saveActivity(getOptionId(),"Salary net Paid for " + hash.size()+ " employees.");
//							Notification.show(getPropertyName("paid_successfully"),Type.WARNING_MESSAGE);
//							loadData();
//						}
//					}
//					catch(Exception e){
//						SNotification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		});

		
		
//		deleteButton.addClickListener(new ClickListener() {
//
//			@SuppressWarnings("unchecked")
//			@Override
//			public void buttonClick(ClickEvent event) {
//
//				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
//
//					@SuppressWarnings("rawtypes")
//					@Override
//					public void onClose(ConfirmDialog arg0) {
//						if (arg0.isConfirmed()) {
//							Iterator itr = table.getItemIds().iterator();
//							SCheckBox box = null;
//							Item item;
//							long emplId = 0;
//							long id=0;
//							List deleteList = new ArrayList();
//							SalaryDisbursalNewModel mdl = null;
//
//							while (itr.hasNext()) {
//								item = table.getItem(itr.next());
//								box = (SCheckBox) item.getItemProperty(TBL_SELECT).getValue();
//								emplId = (Long) item.getItemProperty(TBL_EMP_ID).getValue();
//								id = (Long) item.getItemProperty(TBL_SALARY_ID).getValue();
//								if (box.getValue()) {
//									try {
////										mdl = dao.getDisbursalModel(CommonUtil.getSQLDateFromUtilDate(forMonthDateField.getValue()),emplId);
//										mdl = dao.getSalaryDisbursalNewModel(id);
//										if (mdl != null) {
//											deleteList.add(mdl);
//										}
//									} 
//									catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							}
//							try {
//								if (deleteList.size() > 0) {
//									dao.delete(deleteList);
//									saveActivity(getOptionId(),"Deleted "+ deleteList.size()+ " Salary Payments.");
//									Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
//									loadData();
//								} 
//								else {
//									SNotification.show(getPropertyName("nothing_to_delete"),Type.WARNING_MESSAGE);
//								}
//							} 
//							catch (Exception e) {
//								Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
//								e.printStackTrace();
//							}
//						}
//					}
//				});
//			}
//		});
		
		
		
		printButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				List<Object> reportList = new ArrayList<Object>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				SalaryDisbursalBean bean = null;
				double total = 0;
				try {

					Item item;
					Iterator iter=table.getItemIds().iterator();
					while (iter.hasNext()) {
						item = table.getItem(iter.next());
						bean=new SalaryDisbursalBean(item.getItemProperty(TBL_EMP_NAME).getValue().toString()
								, item.getItemProperty(TBL_STATUS).getValue().toString(), 
								toDouble(item.getItemProperty(TBL_WORKING_TIME).getValue().toString())
								, toDouble(item.getItemProperty(TBL_SALARY).getValue().toString()),
								 toDouble(item.getItemProperty(TBL_REDUCTION).getValue().toString())
								, toDouble(item.getItemProperty(TBL_NET_SALARY).getValue().toString()));
						
						if(toLong(item.getItemProperty(TBL_STATUS_ID).getValue().toString())==SConstants.payroll.STATUS_PAID)
							total+=toDouble(item.getItemProperty(TBL_NET_SALARY).getValue().toString());
						reportList.add(bean);
					}

					map.put("MONTH", new SimpleDateFormat("MMM-yyyy").format(forMonthDateField.getValue()));
					map.put("DISBURSAL_DATE", disbursalDateField.getValue().toString());
					map.put("TOTAL_PAID_AMOUNT", total);

					Report report = new Report(getLoginID());
					report.setJrxmlFileName("SalaryDisbursalPrint");
					report.setReportFileName("SalaryDisbursalPrint");
					report.setReportTitle("Disbursal Report");
					report.setIncludeHeader(true);
					report.setReportType(Report.PDF);
					report.createReport(reportList, map);

					report.print();

					reportList.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
		});

		
		
		return mainPanel;
	}

	
	
//	public double getAmount(long id, long empId) throws Exception {
//
//		double amt = 0;
//		try {
//			PayrollEmployeeMapModel map = dao.getPayRollMap(empId, id);
//			if (map != null) {
//				if (map.getComponent().getType() == SConstants.payroll.FIXED) {
//					amt = map.getValue();
//				} 
//				else {
//					double par_amt = getAmount(map.getComponent().getParent_id(), empId);
//					amt = par_amt * map.getValue() / 100;
//				}
//			}
//			return amt;
//		} catch (Exception e) {
//			throw e;
//		}
//
//	}

	
	
	protected boolean amountValid() {
		boolean valid = true;

		if (paymentAmountField.getValue() == null
				|| paymentAmountField.getValue().equals("")) {
			valid = false;
			setRequiredError(paymentAmountField, getPropertyName("invalid_data"),
					true);
		} else {
			try {
				toDouble(paymentAmountField.getValue().toString());
			} catch (Exception e) {
				valid = false;
				setRequiredError(paymentAmountField,
						getPropertyName("invalid_data"), true);
			}

		}
		
		if (rateField.getValue() == null
				|| rateField.getValue().equals("")) {
			valid = false;
			setRequiredError(rateField, getPropertyName("invalid_data"),
					true);
		} else {
			try {
				toDouble(rateField.getValue().toString());
			} catch (Exception e) {
				valid = false;
				setRequiredError(rateField,
						getPropertyName("invalid_data"), true);
			}

		}
		
		if (overTimeField.getValue() == null
				|| overTimeField.getValue().equals("")) {
			valid = false;
			setRequiredError(overTimeField, getPropertyName("invalid_data"),
					true);
		} else {
			try {
				toDouble(overTimeField.getValue().toString());
			} catch (Exception e) {
				valid = false;
				setRequiredError(overTimeField,
						getPropertyName("invalid_data"), true);
			}

		}
		
		commissionField.setReadOnly(false);
		if (commissionField.getValue() == null
				|| commissionField.getValue().equals("")) {
			valid = false;
			setRequiredError(commissionField, getPropertyName("invalid_data"),
					true);
		} else {
			try {
				toDouble(commissionField.getValue().toString());
			} catch (Exception e) {
				valid = false;
				setRequiredError(commissionField,
						getPropertyName("invalid_data"), true);
			}

		}

		commissionField.setReadOnly(true);
		
		if (reductionField.getValue() == null
				|| reductionField.getValue().equals("")) {
			valid = false;
			setRequiredError(reductionField, getPropertyName("invalid_data"),
					true);
		} else {
			try {
				toDouble(reductionField.getValue().toString());
			} catch (Exception e) {
				valid = false;
				setRequiredError(reductionField,
						getPropertyName("invalid_data"), true);
			}

		}
		return valid;
	}

	
	
//	@SuppressWarnings("rawtypes")
//	public void loadData() {
//
//		try {
//
//			allBox.setValue(false);
//
//			table.setVisibleColumns(allHeaders);
//			table.removeAllItems();
//
//			List list = null;
//			if (settings.isSHOW_ALL_EMPLOYEES_ON_PAYROLL())
//				list = workingDao.getEmployees(getOrganizationID());
//			else
//				list = workingDao.getEmployeesUnderOffice(getOfficeID());
//			
//			UserModel model = null;
//			Object row[] = null;
//			SCheckBox select;
//			
//			
//			long status = 1;
//			String statusString = "Not Paid";
//
//			KeyValue kv = null;
//			Iterator itr = SConstants.payroll.salaryTypes.iterator();
//			String type = "";
//			double workingTime = 0;
//			SalaryDisbursalNewModel disbursalModel;
//			
//			for (int i = 0; i < list.size(); i++) {
//
//				model = (UserModel) list.get(i);
//				
//				double commissionSalary = 0;
//				double previousBalance=0;
//				double previousDue = 0;
//				double salary = 0;
//				double advance=0;
//				double netTotal=0;
//				double overTime=0;
//				double rate=0;
//				long isCommission=0;
//				long id=0;
//				
//				while (itr.hasNext()) {
//				
//					kv = (KeyValue) itr.next();
//					if (kv.getIntKey() == model.getSalary_type()) {
//						
//						type = kv.getValue();
//						break;
//					}
//					
//					type = "";
//				}
//				
//				select = new SCheckBox();
//				
//				workingTime = workingDao.getWorkingTime(CommonUtil.getSQLDateFromUtilDate(forMonthDateField.getValue()),
//														model.getId());
//
//				if (workingTime <= 0) {
//					workingTime = 30;
//				}
//				
//				List comlist=dao.getComponentModel(model.getId(), getOfficeID());
//				if(comlist.size()>0){
//					Iterator comitr=comlist.iterator();
//					while (comitr.hasNext()) {
//						PayrollEmployeeMapModel paymdl=(PayrollEmployeeMapModel)comitr.next();
//						if(paymdl.getComponent().getCommission()!=0){
//							isCommission=1;
//						}
//					}
//				}
//				else{
//					isCommission=0;
//				}
//				
//				disbursalModel = null;
//				disbursalModel = dao.getDisbursalModel(CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()),model.getId());
//				
////				System.out.println("Start "+CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()));
////				
////				System.out.println("User Id "+model.getId());
////				
////				System.out.println("Disbursal Id "+disbursalModel.getId());
//				
//				statusString = "Not Paid";
//				status = 1;
//				
//				
//				if (disbursalModel != null) {
//
//					commissionSalary=roundNumber(disbursalModel.getCommission_amount());
//
//					previousDue=roundNumber(disbursalModel.getPrevious_amount());
//					
//					salary=roundNumber(disbursalModel.getPaid_amount());
//					
//					advance=roundNumber(disbursalModel.getAdvance_payed());
//					
//					netTotal=roundNumber(disbursalModel.getTotal_salary());
//					
//					overTime=roundNumber(disbursalModel.getOver_time());
//					
//					rate=roundNumber(disbursalModel.getRate_over_time());
//					
//					status = disbursalModel.getStatus();
//					statusString = "Paid";
//					id=disbursalModel.getId();
//					
//				}
//				else{
//					commissionSalary=roundNumber(new CommissionSalaryDao().getCommissionSalary(
//															model.getId(), 
//															getOfficeID(),
//															CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()),
//															CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())));
//					
//					previousBalance=roundNumber(dao.getEmployeesCarryBalance(
//															model.getId(),
//															getOfficeID(),
//															CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime())));
//					
//					if(commissionSalary>=previousBalance)
//						commissionSalary-=previousBalance;
//					
//					previousDue=roundNumber(new CommissionSalaryDao().getPreviousDue(
//															model.getId(), 
//															getOfficeID(),
//															CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime())));
//					
//					salary = roundNumber(calculateSalary(workingTime, model.getSalary_type(),model.getId())+commissionSalary+previousDue);
//					
//					advance = roundNumber(dao.getEmployeesTotalAdvanceAmount(
//															model, 
//															CommonUtil.getSQLDateFromUtilDate(disbursalDateField.getValue()),
//															CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime())));
//					overTime=0;
//					rate=0;
//					netTotal=roundNumber(salary-advance+(overTime*rate));
//				}
//					
//				row = new Object[] {
//						select,
//						id,
//						model.getId(),
//						model.getFirst_name() + " " + model.getMiddle_name()+ " " + model.getLast_name(),
//						model.getSalary_type(),
//						type, 
//						workingTime,
//						Math.ceil(salary),
//						Math.ceil(commissionSalary),
//						Math.ceil(previousDue),
//						roundNumber(overTime),
//						roundNumber(rate),
//						roundNumber(overTime*rate),
//						Math.ceil(advance),
//						Math.ceil(netTotal),
//						status,
//						statusString,
//						isCommission};
//
//				table.addItem(row, i + 1);
//			}
//			table.setVisibleColumns(requiredHeaders);
//			
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	
	
//	private double calculateSalary(double workingTime, int salary_type,
//			long empId) {
//
//		double salary = 0;
//		double componentTotal = 0;
//		PayrollEmployeeMapModel mapModel;
//		try {
//			List<?> mapList = payrollMapDao.getPayRollMap(empId);
//			for (int i = 0; i < mapList.size(); i++) {
//				mapModel = (PayrollEmployeeMapModel) mapList.get(i);
//
//				if (mapModel.getComponent().getAction() == SConstants.payroll.ADDITION) {
//					componentTotal += getAmount(mapModel.getComponent().getId(), empId);
//				} else if (mapModel.getComponent().getAction() == SConstants.payroll.DEDUCTION) {
//					componentTotal -= getAmount(mapModel.getComponent().getId(), empId);
//				}
//			}
//			if (salary_type == SConstants.payroll.MONTHLY_SALARY) {
//				salary = componentTotal;
//			} else if (salary_type == SConstants.payroll.DAILY_SALARY) {
//				salary = componentTotal * workingTime;
//			}
//
//			double oneDaySal = salary / 30;
//
//			salary = oneDaySal * workingTime;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return roundNumber(salary);
//	}

	
	
	@Override
	public Boolean isValid() {
		table.setComponentError(null);
		boolean flag = true;

		if (table.getItemIds().size() <= 0) {
			flag = false;
			setRequiredError(table, getPropertyName("no_employees_found"), true);
		}

		flag = false;
		Iterator itr = table.getItemIds().iterator();
		SCheckBox box;
		Item item;
		while (itr.hasNext()) {
			item = table.getItem(itr.next());
			box = (SCheckBox) item.getItemProperty(TBL_SELECT).getValue();
			if (box.getValue()) {
				flag = true;
			}
		}
		if (!flag) {
			setRequiredError(table, getPropertyName("invalid_selection"), true);
		}

		return flag;
	}

	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	
	public InlineDateField getForMonthDateField() {
		return forMonthDateField;
	}
	
	
	
	public void setForMonthDateField(InlineDateField forMonthDateField) {
		this.forMonthDateField = forMonthDateField;
	}
	
}