package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.config.acct.model.BankAccountModel;
import com.inventory.reports.bean.TransactionListingReportBean;
import com.inventory.reports.dao.TransactionListingReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;

public class TransactionListingReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SPanel panel;
	private SFormLayout formLayout;
	private SHorizontalLayout buttonLayout;
	private SVerticalLayout mainVerticalLayout;
	private SHorizontalLayout mainHorizontalLayout;
	private SComboField officeComboField;
	private SComboField bankAccountComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable table;
	private OfficeDao officeDao;
	private BankAccountDao bankAccountDao;
	private Report report;
	private SNativeSelect transactionTypeComboField;
	private TransactionListingReportDao transactionListingReportdao;
	private final static String TBC_ACCOUNT = "Account";
	private final static String TBC_FROM_ACCOUNT = "From A/c";
	private final static String TBC_DATE = "Date";
	private final static String TBC_DOC_NO = "Doc. No";
	private final static String TBC_CHEQUE_NO = "Cheque No";
	private final static String TBC_CHEQUE_DATE = "Cheque Date";
	private final static String TBC_AMOUNT = "Amount";
	private final static String TBC_REMARKS = "Remarks";

	@SuppressWarnings("serial")
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
		transactionListingReportdao = new TransactionListingReportDao();
		report = new Report(getLoginID());

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", true, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());

		bankAccountComboField = new SComboField(
				getPropertyName("bank_account"), 350, null, "id", "name", false);
		loadBankAccounts(getOfficeID());
		bankAccountComboField
				.setInputPrompt("----- "+getPropertyName("all")+" ------");

		transactionTypeComboField = new SNativeSelect(
				getPropertyName("transaction_type"), 350,
				getTransactionTypeList(), "intKey", "value");
		transactionTypeComboField.setNullSelectionAllowed(false);
		transactionTypeComboField.setValue(1);

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

		table = new STable(null, 1000, 300);
		table.addContainerProperty(TBC_ACCOUNT, String.class, null,
				getPropertyName("account"), null, Align.LEFT);
		table.addContainerProperty(TBC_FROM_ACCOUNT, String.class, null,
				getPropertyName("from_or_to"), null, Align.LEFT);
		table.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_DOC_NO, String.class, null,
				getPropertyName("doc_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_CHEQUE_NO, String.class, null,
				getPropertyName("cheque_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_CHEQUE_DATE, String.class, null,
				getPropertyName("cheque_date"), null, Align.CENTER);
		table.addContainerProperty(TBC_AMOUNT, String.class, null,
				getPropertyName("amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_REMARKS, String.class, null,
				getPropertyName("remarks"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_ACCOUNT, 3f);
		table.setColumnExpandRatio(TBC_FROM_ACCOUNT, 3f);
		table.setColumnExpandRatio(TBC_DATE, 2f);
		table.setColumnExpandRatio(TBC_DOC_NO, 1f);
		table.setColumnExpandRatio(TBC_CHEQUE_NO, 2f);
		table.setColumnExpandRatio(TBC_CHEQUE_DATE, 2f);
		table.setColumnExpandRatio(TBC_AMOUNT, 2f);
		table.setColumnExpandRatio(TBC_REMARKS, 2f);

		formLayout.addComponent(officeComboField);
		formLayout.addComponent(bankAccountComboField);
		formLayout.addComponent(transactionTypeComboField);
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

		officeComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				loadBankAccounts(toLong(officeComboField.getValue().toString()));
			}

		});
		showButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					table.removeAllItems();
					List<TransactionListingReportBean> list = transactionListingReportdao.getTransactionListingReportDetails(
							toLong(bankAccountComboField.getValue()),
							toInt(transactionTypeComboField.getValue()
									.toString()), CommonUtil
									.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
							toLong(officeComboField.getValue().toString()));

					for (TransactionListingReportBean bean : list) {
						table.addItem(
								new Object[] {
										bean.getAccount(),
										bean.getFromOrToAccount(),
										CommonUtil.formatDateToDDMMYYYY(bean
												.getDate()),
										bean.getDocNo(),
										bean.getChequeNo(),
										bean.getChequeDate() != null ? CommonUtil
												.formatDateToDDMMYYYY(bean
														.getChequeDate()) : " ",
										bean.getAmount() < 0 ?  Math.abs(bean.getAmount())+"Dr" : bean.getAmount()+"",
										bean.getRemarks() }, table.getItemIds()
										.size() + 1);
					}

					if (list.size() <= 0) {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				}
			}

		});

		generateButton.addClickListener(new ClickListener() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
				//	table.removeAllItems();
					List list = transactionListingReportdao.getTransactionListingReportDetails(
							toLong(bankAccountComboField.getValue()),
							toInt(transactionTypeComboField.getValue()
									.toString()), CommonUtil
									.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
							toLong(officeComboField.getValue().toString()));

					if (list.size() > 0) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("ACCOUNT_LABEL", getPropertyName("account"));
						map.put("FROM_TO_LABEL", getPropertyName("from_or_to_account"));
						map.put("DATE_LABEL", getPropertyName("date"));
						map.put("CHEQUE_NO_LABEL", getPropertyName("cheque_no"));
						map.put("CHEQUE_DATE_LABEL", getPropertyName("cheque_date"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						map.put("REMARKS_LABEL", getPropertyName("remarks"));
						map.put("DOC_NO_LABEL", getPropertyName("doc_no"));
						
						report.setJrxmlFileName("Transaction_Listing_report");
						report.setReportFileName("Transaction Listing Report");
						report.setReportTitle(getPropertyName("transaction_listing_report"));
						report.setReportSubTitle(getSubTitle());
						report.setReportType(toInt(reportChoiceField
								.getValue().toString()));
						report.setIncludeHeader(true);
						report.setOfficeName(officeComboField
								.getItemCaption(officeComboField.getValue()));
						report.createReport(list, map);

						list.clear();						
					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				}
			}

			private String getSubTitle() {
				StringBuffer titleStringBuffer = new StringBuffer();
				titleStringBuffer
						.append(""+getPropertyName("office")+" : "+ officeComboField.getItemCaption(officeComboField.getValue()))		
						.append("\n"+getPropertyName("bank_account")+" : "+ 
								(bankAccountComboField.getValue() == null ? getPropertyName("all") :
									bankAccountComboField.getItemCaption(bankAccountComboField.getValue())))		

						.append("\n"+getPropertyName("from_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDateField
										.getValue()))
						.append(" "+getPropertyName("to_date")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(toDateField
										.getValue()))
						.append("\n"+getPropertyName("transaction_type")+" : "+ transactionTypeComboField
								.getItemCaption(transactionTypeComboField.getValue()));
				return titleStringBuffer.toString();
			}

		});
		return panel;
	}

	private long toLong(Object value) {
		if (value != null) {
			return toLong(value.toString());
		}
		return 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadBankAccounts(long office_id) {
		List list = getBankAccountList(office_id);
		if (list == null) {
			list = new ArrayList<Object>();
		}
		list.add(0, new BankAccountModel(0, "----- "+getPropertyName("all")+" ------"));
		bankAccountComboField.setContainerDataSource(SCollectionContainer
				.setList(list, "id"));
		bankAccountComboField.setItemCaptionPropertyId("name");
	}

	@SuppressWarnings("rawtypes")
	private List getTransactionTypeList() {
		List<KeyValue> list = Arrays.asList(new KeyValue(SConstants.BANK_ACCOUNT_DEPOSITS,
				getPropertyName("bank_account_deposits")), new KeyValue(SConstants.BANK_ACCOUNT_PAYMENTS,
				getPropertyName("bank_account_payments")), new KeyValue(SConstants.SALES,
				getPropertyName("sales")), new KeyValue(SConstants.PURCHASE,
				getPropertyName("purchase")), new KeyValue(SConstants.CASH_ACCOUNT_DEPOSITS,
				getPropertyName("cash_account_deposits")), new KeyValue(SConstants.CASH_ACCOUNT_PAYMENTS,
				getPropertyName("cash_account_payments")), new KeyValue(SConstants.CREDIT_NOTE,
				getPropertyName("credit_note")), new KeyValue(SConstants.DEBIT_NOTE,
				getPropertyName("debit_note")), new KeyValue(SConstants.PDC_PAYMENT,
				getPropertyName("pdc_payment")));
		Collections.sort(list, new Comparator<KeyValue>() {

			@Override
			public int compare(KeyValue o1, KeyValue o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		// list.add(0,new KeyValue(0, getPropertyName("all")));
		return list;
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
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
