package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.ui.BankAccountDepositUI;
import com.inventory.config.acct.ui.BankAccountPaymentUI;
import com.inventory.reports.bean.BankTransactionReportBean;
import com.inventory.reports.dao.BankTransactionReportDao;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 26, 2013
 */

public class BankTransactionReportUI extends SparkLogic {

	private static final long serialVersionUID = -5677871960512468397L;

	private SOfficeComboField officeComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private Report report;
	private SComboField bankComboField;
	private SNativeSelect typeNativeSelect;
	private SDateField fromDateField;
	private SDateField toDateField;
	private static final int PAYMENTS = 1;
	private static final int DEPOSITS = 2;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_BANK = "Bank";
	static String TBC_FROM_TO = "From / To Account";
	static String TBC_AMOUNT = "Amount";
	static String TBC_TYPE = "Type";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	private SHorizontalLayout buttonHorizontalLayout;
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_BANK, TBC_FROM_TO,TBC_AMOUNT,TBC_TYPE };
		visibleColumns = new Object[]  { TBC_SN,  TBC_DATE,TBC_BANK, TBC_FROM_TO,TBC_AMOUNT,TBC_TYPE };
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_BANK, String.class, null,getPropertyName("bank"), null, Align.LEFT);
		table.addContainerProperty(TBC_FROM_TO, String.class, null,getPropertyName("from/to"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_TYPE, String.class, null,getPropertyName("type"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_BANK, 1);
		table.setColumnExpandRatio(TBC_DATE, (float) 1);
		table.setColumnExpandRatio(TBC_FROM_TO, (float) 2);
		table.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
		table.setColumnExpandRatio(TBC_TYPE, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		setSize(1050, 350);

		SPanel panel = new SPanel();
		panel.setSizeFull();
		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);
		report = new Report(getLoginID());

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		try {

			officeComboField = new SOfficeComboField(getPropertyName("office"),
					200);

			List ledList = new ArrayList();
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			ledList.add(ledgerModel);
//			ledList.addAll(new LedgerDao().getAllLedgersUnderGroupAndSubGroups(
//					getOfficeID(), getOrganizationID(),
//					SConstants.BANK_ACCOUNT_GROUP_ID));
			bankComboField = new SComboField(getPropertyName("bank"), 200,
					ledList, "id", "name", false, "All");
			bankComboField.setValue((long) 0);

			List<KeyValue> list = Arrays.asList(new KeyValue(0, getPropertyName("all")),
					new KeyValue(SConstants.BANK_ACCOUNT_PAYMENTS, getPropertyName("payment")),
					new KeyValue(SConstants.BANK_ACCOUNT_DEPOSITS, getPropertyName("deposit")));
			typeNativeSelect = new SNativeSelect(getPropertyName("type"), 200,
					list, "intKey", "value");
			typeNativeSelect.setValue((int) 0);
			typeNativeSelect.setNullSelectionAllowed(false);

			fromDateField = new SDateField(getPropertyName("from_date"));
			toDateField = new SDateField(getPropertyName("to_date"));
			fromDateField.setValue(getMonthStartDate());
			toDateField.setValue(getWorkingDate());
			SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
			dateHorizontalLayout.setSpacing(true);
			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));

			generateButton = new SButton(getPropertyName("generate"));

			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(bankComboField);
			mainFormLayout.addComponent(typeNativeSelect);
			mainFormLayout.addComponent(dateHorizontalLayout);
			mainFormLayout.addComponent(reportChoiceField);
			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			panel.setContent(mainHorizontal);
			
			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						
						List ledList = new ArrayList();
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel.setName("--------------------All-------------------");
						ledList.add(ledgerModel);
						if(officeComboField.getValue()!=null) {
							
//							ledList.addAll(new LedgerDao().getAllLedgersUnderGroupAndSubGroups(
//									(Long)officeComboField.getValue(), getOrganizationID(),
//									SConstants.BANK_ACCOUNT_GROUP_ID));
							
						}
						
						SCollectionContainer office = SCollectionContainer
								.setList(ledList, "id");
						bankComboField.setContainerDataSource(office);
						bankComboField.setInputPrompt(getPropertyName("all"));
						bankComboField.setItemCaptionPropertyId("name");
						bankComboField.setValue((long)0);
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			
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
							long id=(Long) item.getItemProperty(TBC_ID).getValue();
							if(item.getItemProperty(TBC_TYPE).getValue().toString().equals("Deposit")){
								BankAccountDepositUI option=new BankAccountDepositUI();
								option.setCaption("Bank Account Deposit");
								option.getAccountDepositNumberList().setValue(id);
								option.center();
								getUI().getCurrent().addWindow(option);
								option.addCloseListener(closeListener);
							}
							else{
								BankAccountPaymentUI option=new BankAccountPaymentUI();
								option.setCaption("Bank Account Payment");
//								option.getAccountPaymentNumberList().setValue(id);
								option.center();
								getUI().getCurrent().addWindow(option);
								option.addCloseListener(closeListener);
							}
							
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
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("bank_transaction")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("bank"),item.getItemProperty(TBC_BANK).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("type"),item.getItemProperty(TBC_TYPE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("from/to"),item.getItemProperty(TBC_FROM_TO).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
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
					table.removeAllItems();
					table.setVisibleColumns(allColumns);
					if (isValid()) {
						long id=0;
						BankAccountDepositModel dep=null;
						BankAccountPaymentModel pay=null;
						List reportList = new ArrayList();
						List transactionDetails = new ArrayList();
						BankTransactionReportBean beans = null;
						TransactionModel transactionModel = null;
						TransactionDetailsModel detailsModel = null;
						String bankName = "";
						String fromAcctName = "";
						String tranType = "";

						try {
							List list = new BankTransactionReportDao().getAllTransactions(
									toLong(officeComboField.getValue().toString()),
									toLong(bankComboField.getValue().toString()),
									toInt(typeNativeSelect.getValue().toString()),
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),0
									/*SConstants.BANK_ACCOUNT_GROUP_ID*/);

							if (list.size()>0) {
								for (int i = 0; i < list.size(); i++) {
									transactionModel = (TransactionModel) list.get(i);
									transactionDetails = transactionModel.getTransaction_details_list();
									for (int k = 0; k < transactionDetails.size(); k++) {
										detailsModel = (TransactionDetailsModel) transactionDetails.get(k);
										
//										if (detailsModel.getFromAcct().getGroup().getId() == SConstants.BANK_ACCOUNT_GROUP_ID) {
											bankName = detailsModel.getFromAcct().getName();
											fromAcctName = detailsModel.getToAcct().getName();
											tranType = getPropertyName("payment");
//										} 
//										else if (detailsModel.getToAcct().getGroup().getId() == SConstants.BANK_ACCOUNT_GROUP_ID) {
//											bankName = detailsModel.getToAcct().getName();
//											fromAcctName = detailsModel.getFromAcct().getName();
//											tranType = getPropertyName("deposit");
										}
										if (transactionModel.getTransaction_type() == SConstants.BANK_ACCOUNT_DEPOSITS) {
											tranType = getPropertyName("deposit");
										}
										else if (transactionModel.getTransaction_type() == SConstants.BANK_ACCOUNT_PAYMENTS) {
											tranType = getPropertyName("payment");
										}
										if(tranType.equals("Deposit")){
											id=new BankTransactionReportDao().getBankDepositId(transactionModel.getTransaction_id());
										}
										else
											id=new BankTransactionReportDao().getBankPaymentId(transactionModel.getTransaction_id());
										table.addItem(new Object[]{
												table.getItemIds().size()+1,
												id,
												transactionModel.getDate().toString(),
												bankName,
												fromAcctName,
												detailsModel.getAmount(),
												tranType},table.getItemIds().size()+1);
									}
								}
//							} 
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						} 
						catch (Exception e) {
							e.printStackTrace();
						}

					}
					table.setVisibleColumns(visibleColumns);
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						List reportList = new ArrayList();
						List transactionDetails = new ArrayList();
						BankTransactionReportBean beans = null;
						TransactionModel transactionModel = null;
						TransactionDetailsModel detailsModel = null;
						String bankName = "";
						String fromAcctName = "";
						String tranType = "";

						try {

							List list = new BankTransactionReportDao().getAllTransactions(
									toLong(officeComboField.getValue()
											.toString()),
									toLong(bankComboField.getValue().toString()),
									toInt(typeNativeSelect.getValue()
											.toString()),
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),0
								/*	SConstants.BANK_ACCOUNT_GROUP_ID*/);

							if (list != null) {
								for (int i = 0; i < list.size(); i++) {

									transactionModel = (TransactionModel) list
											.get(i);
									transactionDetails = transactionModel
											.getTransaction_details_list();
									for (int k = 0; k < transactionDetails
											.size(); k++) {

										detailsModel = (TransactionDetailsModel) transactionDetails
												.get(k);
										
//										if (detailsModel.getFromAcct()
//												.getGroup().getId() == SConstants.BANK_ACCOUNT_GROUP_ID) {
											bankName = detailsModel
													.getFromAcct().getName();
											fromAcctName = detailsModel
													.getToAcct().getName();
											tranType = getPropertyName("payment");
////										} else if (detailsModel.getToAcct()
//												.getGroup().getId() == SConstants.BANK_ACCOUNT_GROUP_ID) {
//											bankName = detailsModel.getToAcct()
//													.getName();
//											fromAcctName = detailsModel
//													.getFromAcct().getName();
//											tranType = getPropertyName("deposit");
//										}

										if (transactionModel
												.getTransaction_type() == SConstants.BANK_ACCOUNT_DEPOSITS) {
											tranType = getPropertyName("deposit");
										}

										else if (transactionModel
												.getTransaction_type() == SConstants.BANK_ACCOUNT_PAYMENTS) {
											tranType = getPropertyName("payment");
										}
										beans = new BankTransactionReportBean(
												bankName, fromAcctName,
												detailsModel.getAmount(),
												transactionModel.getDate()
														.toString(), tranType);

										reportList.add(beans);
									}
								}
								if (reportList.size() > 0) {
									HashMap<String, Object> map = new HashMap<String, Object>();
									report.setJrxmlFileName("BankTransactionList");
									report.setReportFileName("BankTransactionList");
									
									map.put("REPORT_TITLE_LABEL", getPropertyName("bank_transaction_list"));
									map.put("SL_NO_LABEL", getPropertyName("sl_no"));
									map.put("DATE_LABEL", getPropertyName("date"));
									map.put("BANK_LABEL", getPropertyName("bank"));
									map.put("FROM_TO_LABEL", getPropertyName("from_to_account"));
									map.put("AMOUNT_LABEL", getPropertyName("amount"));
									map.put("TYPE_LABEL", getPropertyName("type"));
									map.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
									
									
									
									String subTitle = "";
									if (toLong(officeComboField.getValue()
											.toString()) != 0)
										subTitle += getPropertyName("office")+" : "
												+ officeComboField
														.getItemCaption(officeComboField
																.getValue())
												+ "\n";
									subTitle += getPropertyName("type")+" : "
											+ typeNativeSelect
													.getItemCaption(typeNativeSelect
															.getValue());

									report.setReportSubTitle(subTitle);
									report.setReportType(toInt(reportChoiceField
											.getValue().toString()));
									report.setIncludeHeader(true);
									report.setOfficeName(officeComboField
											.getItemCaption(officeComboField
													.getValue()));
									report.createReport(reportList, map);

									reportList.clear();
									list.clear();

								} else {
									SNotification
											.show(getPropertyName("no_data_available"),
													Type.WARNING_MESSAGE);
								}
							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
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
