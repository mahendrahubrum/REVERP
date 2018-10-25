package com.inventory.reports.ui;

import java.util.HashMap;
import java.util.List;

import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.finance.dao.BankReconciliationDao;
import com.inventory.reports.bean.BankReconciliationReportBean;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;

public class BankReconciliationReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SComboField officeComboField;
	private SPanel panel;
	private SFormLayout formLayout;
	private SHorizontalLayout buttonLayout;
	private SVerticalLayout mainVerticalLayout;
	private OfficeDao officeDao;
	private BankAccountDao bankAccountDao;
	private BankReconciliationDao bankReconciliationDao;
	
	private SComboField bankAccountComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SButton generateButton;
	private STable table;
	private SButton showButton;
	private SHorizontalLayout mainHorizontalLayout;
	private SReportChoiceField reportChoiceField;
	private Report report;
	private SparkLogic spark;

	private final static String TBC_PARTICULARS = "Particulars";
	private final static String TBC_CR = "CR";
	private final static String TBC_DR = "DR";
	private final static String TBC_BALANCE = "Balance";

	@Override
	public SPanel getGUI() {
		setSize(1200, 400);
		panel = new SPanel();
		panel.setSizeFull();

		formLayout = new SFormLayout();
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		mainVerticalLayout = new SVerticalLayout();
		mainVerticalLayout.setSpacing(true);

		mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.setSpacing(true);

		officeDao = new OfficeDao();
		bankAccountDao = new BankAccountDao();
		bankReconciliationDao = new BankReconciliationDao();
		report = new Report(getLoginID());
		

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", true, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());

		bankAccountComboField = new SComboField(
				getPropertyName("bank_account"), 350,
				getBankAccountList(getOfficeID()), "id", "name", true,
				getPropertyName("select"));

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getFinStartDate());

		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getFinEndDate());

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		showButton = new SButton(getPropertyName("show"));

		table = new STable(null, 700, 300);
		table.addContainerProperty(TBC_PARTICULARS, String.class, null,
				getPropertyName("particulars"), null, Align.LEFT);
		table.addContainerProperty(TBC_CR, String.class, null, getPropertyName("cr"), null,
				Align.RIGHT);
		table.addContainerProperty(TBC_DR, String.class, null, getPropertyName("dr"), null,
				Align.RIGHT);
		table.addContainerProperty(TBC_BALANCE, String.class, null,
				getPropertyName("balance"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_PARTICULARS, 4f);
		table.setColumnExpandRatio(TBC_CR, 2f);
		table.setColumnExpandRatio(TBC_DR, 2f);
		table.setColumnExpandRatio(TBC_BALANCE, 2f);

		formLayout.addComponent(officeComboField);
		formLayout.addComponent(bankAccountComboField);
		formLayout.addComponent(dateHorizontalLayout);
		formLayout.addComponent(reportChoiceField);

		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);

		mainVerticalLayout.addComponent(formLayout);
		mainVerticalLayout.addComponent(buttonLayout);

		mainVerticalLayout.setComponentAlignment(formLayout,
				Alignment.MIDDLE_CENTER);
		mainVerticalLayout.setComponentAlignment(buttonLayout,
				Alignment.MIDDLE_CENTER);

		mainHorizontalLayout.addComponent(mainVerticalLayout);
		mainHorizontalLayout.addComponent(table);
		mainHorizontalLayout.setComponentAlignment(table,
				Alignment.MIDDLE_CENTER);

		panel.setContent(mainHorizontalLayout);
		
		spark = this;
		
		showButton.addClickListener(new ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					try {
						List<BankReconciliationReportBean> beanList = bankReconciliationDao
								.getBankReconciliationReportDetails(spark, toLong(bankAccountComboField.getValue().toString()),
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						table.removeAllItems();
						for(BankReconciliationReportBean bean : beanList){
							table.addItem(new Object[] { bean.getParticulars(),
									bean.getCr() != 0 ? bean.getCr()+"" : " ",
									bean.getDr() != 0 ? bean.getDr()+"" : " ",
									bean.getBalance() != 0 ? (bean.getBalance() < 0 ? Math.abs(bean.getBalance())+" Dr" : bean.getBalance()+"") : " "},
									table.getItemIds().size() + 1);
						}
						
						if(beanList.size() < 0){
							SNotification.show(getPropertyName("no_data_available"), Type.WARNING_MESSAGE);						
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}				
			}
		});
		
		generateButton.addClickListener(new ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					try {
						List beanList = bankReconciliationDao
								.getBankReconciliationReportDetails(spark,toLong(bankAccountComboField.getValue().toString()),
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						
						if (beanList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("PARTICULARS_LABEL", getPropertyName("particulars"));
							map.put("CR_LABEL", getPropertyName("cr"));
							map.put("DR_LABEL", getPropertyName("dr"));
							map.put("BALANCE_LABEL", getPropertyName("balance"));
							
							report.setJrxmlFileName("Bank_Reconciliation");
							report.setReportFileName("Bank Reconciliation Statement");
							report.setReportTitle(getPropertyName("bank_reconciliation_statement"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(beanList, map);

							beanList.clear();						
						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			private String getSubTitle() {
				StringBuffer titleStringBuffer = new StringBuffer();
				titleStringBuffer
						.append(getPropertyName("office")+" : "+ officeComboField.getItemCaption(officeComboField.getValue()))		
						.append("\n"+getPropertyName("bank_account")+" : "+ bankAccountComboField.getItemCaption(bankAccountComboField.getValue()))		

						.append("\n"+getPropertyName("from_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDateField
										.getValue()))
						.append(" "+getPropertyName("to_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(toDateField
										.getValue()));
				return titleStringBuffer.toString();
			}
		});

		return panel;
	}

	@SuppressWarnings("rawtypes")
	private List getOfficeList() {
		try {
			return officeDao.getAllOfficeNamesUnderOrg(getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private List getBankAccountList(long office_id) {
		try {
			return bankAccountDao
					.getAllActiveBankAccountNamesWithLedgerID(office_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (officeComboField.getValue() == null
				|| officeComboField.getValue().equals("")) {
			setRequiredError(officeComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(officeComboField, null, false);
		}

		if (bankAccountComboField.getValue() == null
				|| bankAccountComboField.getValue().equals("")) {
			setRequiredError(bankAccountComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(bankAccountComboField, null, false);
		}

		if (fromDateField.getValue() == null
				|| fromDateField.getValue().equals("")) {
			setRequiredError(fromDateField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			setRequiredError(fromDateField, null, false);
		}

		if (toDateField.getValue() == null || toDateField.getValue().equals("")) {
			setRequiredError(toDateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(toDateField, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
