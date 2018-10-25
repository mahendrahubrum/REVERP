package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.finance.dao.FinanceComponentDao;
import com.inventory.finance.dao.FinancePaymentDao;
import com.inventory.finance.model.FinanceComponentModel;
import com.inventory.reports.bean.FinanceTransactionReportBean;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 17, 2014
 */
public class FinanceLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5804111990040298014L;

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField accountField;
	private SReportChoiceField reportChoiceField;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;

	private Report report;

	FinancePaymentDao dao;
	FinanceComponentDao compDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_LEDGER = "Ledger";
	static String TBC_COMMENT = "Comment";
	static String TBC_BILL = "Bill";
	static String TBC_IN = "Inward";
	static String TBC_OUT = "Outward";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {

		SPanel mainPanel;

		dao = new FinancePaymentDao();
		compDao = new FinanceComponentDao();
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_LEDGER,TBC_COMMENT, TBC_BILL,TBC_IN,TBC_OUT};
		visibleColumns = new Object[]{ TBC_SN, TBC_LEDGER,TBC_BILL,TBC_IN,TBC_OUT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_LEDGER, String.class, null,getPropertyName("ledger"), null, Align.LEFT);
		table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comment"), null, Align.LEFT);
		table.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("bill"), null, Align.LEFT);
		table.addContainerProperty(TBC_IN, Double.class, null,getPropertyName("inward"), null, Align.LEFT);
		table.addContainerProperty(TBC_OUT, Double.class, null,getPropertyName("outward"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_LEDGER, (float) 2);
		table.setColumnExpandRatio(TBC_IN, (float) 1);
		table.setColumnExpandRatio(TBC_OUT, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		report = new Report(getLoginID());

		try {
			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		officeComboField.setValue(getOfficeID());

		setSize(1100, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		SGridLayout dateLay = new SGridLayout(getPropertyName("from_date"));
		dateLay.setRows(1);
		dateLay.setColumns(5);
		dateLay.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField();
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField();
		toDateField.setValue(getWorkingDate());
		dateLay.addComponent(fromDateField);
		dateLay.addComponent(new SLabel(getPropertyName("to_date")));
		dateLay.addComponent(toDateField);

		try {
			ArrayList lst = new ArrayList();
			lst.add(0, new FinanceComponentModel(0,getPropertyName("all")));
			lst.addAll(compDao.getAllActiveComponents(getOfficeID()));
			accountField = new SComboField(getPropertyName("account"), 200,
					lst, "id", "name");
			accountField
					.setInputPrompt(getPropertyName("all"));
			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));

			mainFormLayout.addComponent(dateLay);
			mainFormLayout.addComponent(accountField);
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

			/*final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};
			
			final Action action = new Action("Edit");
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SalesNewUI option=new SalesNewUI();
							option.setCaption("Sales");
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
			});*/
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							SalesModel mdl=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("finance_ledger")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("ledger"),item.getItemProperty(TBC_LEDGER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("inwards"),item.getItemProperty(TBC_IN).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("outwards"),item.getItemProperty(TBC_OUT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("comments"),item.getItemProperty(TBC_COMMENT).getValue().toString()));
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
					List reportList = null;
					table.removeAllItems();
					table.setVisibleColumns(allColumns);
					try {

						long accid = 0;
						if (accountField.getValue() != null
								&& !accountField.getValue().equals("")) {
							accid = (Long) accountField.getValue();
						}

						reportList = dao.getFinLedgerReport(accid, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField
										.getValue()), (Long) officeComboField
										.getValue());
						
						if(reportList.size()>0){
							FinanceTransactionReportBean bean=null;
							Iterator itr=reportList.iterator();
							while(itr.hasNext()){
								bean=(FinanceTransactionReportBean)itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getId(),
										bean.getFromaccount(),
										bean.getDescription(),
										bean.getPaymentNo(),
										bean.getInwards(),bean.getOutwards()},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
						
						table.setVisibleColumns(visibleColumns);
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					List reportList = null;

					try {

						long accid = 0;
						if (accountField.getValue() != null
								&& !accountField.getValue().equals("")) {
							accid = (Long) accountField.getValue();
						}

						reportList = dao.getFinLedgerReport(accid, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField
										.getValue()), (Long) officeComboField
										.getValue());

						if (reportList != null && reportList.size() > 0) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							report.setJrxmlFileName("FinanceLedgerReport");
							report.setReportFileName("FinanceLedgerReport");
							
							params.put("REPORT_TITLE_LABEL", getPropertyName("finance_ledger_report"));
							params.put("SL_NO_LABEL", getPropertyName("sl_no"));
							params.put("DATE_LABEL", getPropertyName("date"));
							params.put("LEDGER_LABEL", getPropertyName("ledger"));
							params.put("COMMENTS_LABEL", getPropertyName("comments"));
							params.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							params.put("INWARDS_LABEL", getPropertyName("inwards"));
							params.put("OUTWARDS_LABEL", getPropertyName("outwards"));
							params.put("BALANCE_LABEL", getPropertyName("balance"));
							params.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							report.setReportTitle("Finance Ledger Report");
							String subHeader = "";

							if (accid != 0)
								subHeader += getPropertyName("account")+" : "
										+ accountField
												.getItemCaption(accountField
														.getValue());

							subHeader += "\t "+getPropertyName("from")+" : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t "+getPropertyName("to")+" : "
									+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue());

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, params);

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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
