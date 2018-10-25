package com.inventory.reports.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.NotPayedSaleReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 19, 2015
 */

@SuppressWarnings("serial")
public class NotPayedPurchaseReportUI extends SparkLogic {

//	private String PROMPT_ALL = getPropertyName("all");

	private SComboField customerComboField;
	private SReportChoiceField choiceField;
	private SOfficeComboField officeComboField;
	private SButton generateButton, generateDetailedButton;

	private NotPayedSaleReportDao objDao;
	
	private SDateField fromDateField;
	private SDateField toDateField;
	
	private SHorizontalLayout dateHorizontalLayout;

	private Report report;

	SupplierDao cusDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SUPPLIER = "Customer";
	static String TBC_TOTAL = "Total Amount";
	static String TBC_PAID = "Paid Amount";
	static String TBC_BALANCE = "Balance";
	
	static String TBC_INVOICE = "Invoice No";
	static String TBC_DATE = "Date";
	static String TBC_DUE = "Due";
	
	SPopupView popUp,subPop;
	SNativeButton closeBtn;
	
	SHorizontalLayout popupContainer,popHor,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	
	Object[] allSubColumns;
	Object[] visibleSubColumns;
	
	STable table;
	STable subtable;
	SButton showButton;

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public SPanel getGUI() {

		cusDao = new SupplierDao();
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_SUPPLIER,TBC_TOTAL,TBC_PAID,TBC_BALANCE};
		allSubColumns = new Object[] { TBC_SN, TBC_ID, TBC_SUPPLIER, TBC_INVOICE, TBC_DATE, TBC_TOTAL, TBC_PAID, TBC_BALANCE, TBC_DUE};
		
		visibleColumns = new Object[]{ TBC_SN, TBC_SUPPLIER,TBC_TOTAL,TBC_PAID,TBC_BALANCE};
		visibleSubColumns = new Object[] { TBC_SN, TBC_SUPPLIER, TBC_INVOICE, TBC_DATE, TBC_TOTAL, TBC_PAID, TBC_BALANCE, TBC_DUE};
		closeBtn = new SNativeButton("X");
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		popHor = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_SUPPLIER, String.class, null, getPropertyName("supplier"), null,Align.CENTER);
		table.addContainerProperty(TBC_TOTAL, Double.class, null, getPropertyName("total_amount"), null,Align.CENTER);
		table.addContainerProperty(TBC_PAID, Double.class, null, getPropertyName("paid_amount"), null,Align.CENTER);
		table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.LEFT);

		subtable = new STable(null, 750, 250);
		subtable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		subtable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		subtable.addContainerProperty(TBC_SUPPLIER, String.class, null, getPropertyName("supplier"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_INVOICE, String.class, null, getPropertyName("purchase_no"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_TOTAL, Double.class, null, getPropertyName("total_amount"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_PAID, Double.class, null, getPropertyName("paid_amount"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.LEFT);
		subtable.addContainerProperty(TBC_DUE, String.class, null, getPropertyName("due"), null,Align.CENTER);
		
		
		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_SUPPLIER, (float) 2);
		table.setColumnExpandRatio(TBC_TOTAL, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAID, (float) 1.5);
		table.setColumnExpandRatio(TBC_BALANCE, (float) 1.5);
		
		subtable.setColumnExpandRatio(TBC_SUPPLIER, 1.5f);
		subtable.setColumnExpandRatio(TBC_INVOICE, 0.5f);
		subtable.setColumnExpandRatio(TBC_DATE, 0.75f);
		subtable.setColumnExpandRatio(TBC_TOTAL, 1f);
		subtable.setColumnExpandRatio(TBC_PAID, 1f);
		subtable.setColumnExpandRatio(TBC_BALANCE, 1f);
		subtable.setColumnExpandRatio(TBC_DUE, 0.5f);
		
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		subtable.setSelectable(true);
		subtable.setMultiSelect(false);
		subtable.setVisibleColumns(visibleSubColumns);
		subtable.setFooterVisible(true);
		subtable.setColumnFooter(TBC_TOTAL, "0.0");
		subtable.setColumnFooter(TBC_PAID, "0.0");
		subtable.setColumnFooter(TBC_BALANCE, "0.0");
		
		setSize(1100, 375);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		
		
		dateHorizontalLayout = new SHorizontalLayout(getPropertyName("invoice_between"));
		dateHorizontalLayout.setSpacing(true);
		
		
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		objDao = new NotPayedSaleReportDao();
		report = new Report(getLoginID());

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		List groupList;
		try {
			groupList = cusDao.getAllSuppliersNames(getOfficeID());
		} catch (Exception e) {
			groupList = new ArrayList();
			e.printStackTrace();
		}
		CustomerModel custModel = new CustomerModel();
		custModel.setId(0);
		custModel.setName(getPropertyName("all"));
		groupList.add(0, custModel);
		customerComboField = new SComboField(getPropertyName("supplier"), 200,
				groupList, "id", "name");
		customerComboField.setInputPrompt(getPropertyName("all"));
		customerComboField.setValue((long)0);
		
		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		
		generateDetailedButton = new SButton(getPropertyName("generate_detail"));
		generateDetailedButton.setClickShortcut(KeyCode.ENTER);

		choiceField = new SReportChoiceField(getPropertyName("export_to"));

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(customerComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(choiceField);
		mainFormLayout.addComponent(new SHorizontalLayout(generateButton,showButton,generateDetailedButton));
		
		mainHorizontal.addComponent(popHor);
		mainHorizontal.addComponent(mainFormLayout);
		mainHorizontal.addComponent(popupContainer);
		mainHorizontal.addComponent(table);
		mainHorizontal.setMargin(true);
		
		panel.setContent(mainHorizontal);

		closeBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				popUp.setPopupVisible(false);
			}
		});
		
		
		officeComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				List groupList;
				try {
					groupList = cusDao
							.getAllSupplierNamesList((Long) officeComboField
									.getValue());
				} catch (Exception e) {
					groupList = new ArrayList();
					e.printStackTrace();
				}

				SupplierModel custModel = new SupplierModel();
				custModel.setId(0);
				custModel.setName(getPropertyName("all"));
				groupList.add(0, custModel);

				SCollectionContainer con = SCollectionContainer.setList(groupList, "id");
				customerComboField.setContainerDataSource(con);
				customerComboField.setItemCaptionPropertyId("name");
				customerComboField.setValue(0);

			}
		});

		officeComboField.setValue(getOfficeID());

		
		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				showButton.click();
			}
		};
		
		
		final Action actionDelete = new Action(getPropertyName("edit"));
		
		
		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						subtable.setColumnFooter(TBC_TOTAL, "0.0");
						subtable.setColumnFooter(TBC_PAID, "0.0");
						subtable.setColumnFooter(TBC_BALANCE, "0.0");
						subtable.removeAllItems();
						Item item = table.getItem(table.getValue());
						long id = (Long) item.getItemProperty(TBC_ID).getValue();
						LedgerModel customer=new LedgerDao().getLedgeer(id);
						List detailList=objDao.getPurchaseDetailsForSupplier(customer.getId(),
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
								(Long)officeComboField.getValue());
						if(detailList.size()>0){
							Iterator itr=detailList.iterator();
							subtable.setVisibleColumns(allSubColumns);
							while (itr.hasNext()) {
								PurchaseModel mdl=(PurchaseModel)itr.next();
								subtable.addItem(new Object[]{
										subtable.getItemIds().size()+1,
										mdl.getId(),
										mdl.getSupplier().getName(),
										mdl.getPurchase_no()+"",
										CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
										roundNumber(mdl.getAmount()),
										roundNumber(mdl.getPaymentAmount()+mdl.getPaid_by_payment()),
										roundNumber(mdl.getAmount()-mdl.getPaymentAmount()-mdl.getPaid_by_payment()),
										""+(int) ((new java.util.Date().getTime()-mdl.getDate().getTime())/86400000)},subtable.getItemIds().size()+1);
							}
							subtable.setVisibleColumns(visibleSubColumns);
						}
						Iterator it=subtable.getItemIds().iterator();
						double bal=0,paid=0,total=0;
						while (it.hasNext()) {
							Item itm=subtable.getItem(it.next());
							bal+=roundNumber(toDouble(itm.getItemProperty(TBC_BALANCE).getValue().toString()));
							paid+=roundNumber(toDouble(itm.getItemProperty(TBC_PAID).getValue().toString()));
							total+=roundNumber(toDouble(itm.getItemProperty(TBC_TOTAL).getValue().toString()));
						}
						subtable.setColumnFooter(TBC_TOTAL, roundNumber(total)+"");
						subtable.setColumnFooter(TBC_PAID, roundNumber(paid)+"");
						subtable.setColumnFooter(TBC_BALANCE, roundNumber(bal)+"");
						popUp = new SPopupView("",new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(
								null,"<h2><u style='margin-left: 40px;'>Sales Details ",
								725), closeBtn), subtable));
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
		
		
		subtable.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (subtable.getValue() != null) {
						Item item = subtable.getItem(subtable.getValue());
						long id = (Long) item.getItemProperty(TBC_ID).getValue();
						PurchaseModel sale=new PurchaseDao().getPurchaseModel(id);
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("purchase_no"),sale.getPurchase_no()+""));
						form.addComponent(new SLabel(getPropertyName("supplier"),sale.getSupplier().getName()));
						form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
						form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber(sale.getAmount()) + ""));
						form.addComponent(new SLabel(getPropertyName("paid_amount"),roundNumber(sale.getPaymentAmount()) + ""));
						form.addComponent(new SLabel(getPropertyName("payment"),roundNumber(sale.getPaid_by_payment()) + ""));
						form.addComponent(new SLabel(getPropertyName("balance"),roundNumber(sale.getAmount()-sale.getPaymentAmount()-sale.getPaid_by_payment()) + ""));
						SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
						grid.setColumns(12);
						grid.setRows(sale
								.getPurchase_details_list().size() + 3);

						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
						grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
						grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
						grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
						grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
						grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
						grid.setSpacing(true);
						
						int i = 1;
						PurchaseInventoryDetailsModel invObj;
						Iterator itr = sale.getPurchase_details_list().iterator();
						while(itr.hasNext()){
							invObj=(PurchaseInventoryDetailsModel)itr.next();
							grid.addComponent(new SLabel(null, i + ""),	0, i);
							grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
							grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
							grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
							grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
							grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
							grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																- invObj.getDiscount()
																+ invObj.getTaxAmount())+ ""), 6, i);
							i++;
						}
						form.addComponent(grid);
						form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
						form.setStyleName("grid_max_limit");
						subPop=new SPopupView("", form);
						popupContainer.addComponent(subPop);
						subPop.setPopupVisible(true);
						subPop.setHideOnMouseOut(false);
					}
					else
						subPop.setPopupVisible(false);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		

		subtable.addActionHandler(new Handler() {
			
			@SuppressWarnings("static-access")
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				try{
					if (subtable.getValue() != null) {
						Item item = subtable.getItem(subtable.getValue());
						PurchaseUI sales = new PurchaseUI();
						sales.setCaption(getPropertyName("purchase"));
						sales.getPurchaseNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
						sales.center();
						popUp.setVisible(false);
						getUI().getCurrent().addWindow(sales);
						sales.addCloseListener(closeListener);
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
		
		
		showButton.addClickListener(new ClickListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					table.removeAllItems();
					table.setVisibleColumns(allColumns);
					long ofc_id=0;
					if(officeComboField.getValue()!=null)
						ofc_id=(Long) officeComboField.getValue();
					try {

						List reportList = new ArrayList();

						List list = new ArrayList();

						if (customerComboField.getValue() == null
								|| customerComboField.getValue().equals("")
								|| customerComboField.getValue().toString()
										.equals("0"))
							list = cusDao.getAllSupplierNamesList((Long) officeComboField
											.getValue());
						else {
							list.add(cusDao.getSupplier((Long) customerComboField.getValue()));
						}
						
						Iterator it = list.iterator();
						int i = 0;
						List list2;
						Iterator it2;
						SupplierModel obj;
						String salesNos = " ";
						double payed = 0, total = 0;
						PurchaseModel salObj;
						while (it.hasNext()) {

							obj = (SupplierModel) it.next();

							list2 = objDao.getAllPurchaseDetailsForSupplier(obj.getLedger().getId(),
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
									ofc_id,
									getOrganizationID());

							if (list2.size() > 0) {

								salesNos = " ";
								payed = 0;
								total = 0;
								it2 = list2.iterator();
								while (it2.hasNext()) {
									salObj = (PurchaseModel) it2.next();
									payed += salObj.getPaymentAmount()+salObj.getPaid_by_payment();
									total += salObj.getAmount();
								}

								i++;

								/*payed += new PaymentDao()
										.getCustomerAllPayedAmt(obj.getLedger()
												.getId(), CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
												CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));*/
								if (roundNumber(total - payed)> 0)
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											obj.getLedger().getId(),
											obj.getName(),
											roundNumber(total),
											roundNumber(payed),
											roundNumber(total - payed)},table.getItemIds().size()+1);
							}
						}
						if(table.getItemIds().size()<=0)
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						table.setVisibleColumns(visibleColumns);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
		
		
		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					long ofc_id=0;
					if(officeComboField.getValue()!=null)
						ofc_id=(Long) officeComboField.getValue();
					try {

						List reportList = new ArrayList();

						List list = new ArrayList();

						if (customerComboField.getValue() == null
								|| customerComboField.getValue().equals("")
								|| customerComboField.getValue().toString()
										.equals("0"))
							list = cusDao
									.getAllSupplierNamesList((Long) officeComboField
											.getValue());
						else {
							list.add(cusDao.getSupplier((Long) customerComboField.getValue()));
						}

						AcctReportMainBean details;
						Iterator it = list.iterator();
						int i = 0;
//						Calendar cal;
						List list2;
						Iterator it2;
						SupplierModel obj;
						String salesNos = " ";
						double payed = 0, total = 0;
						PurchaseModel salObj;
						while (it.hasNext()) {

							details = new AcctReportMainBean();

							obj = (SupplierModel) it.next();

//							cal = Calendar.getInstance();
//							cal.setTime(new java.util.Date());
//
//							cal.add(Calendar.DAY_OF_MONTH,
//									-obj.getMax_credit_period());

							list2 = objDao.getAllPurchaseDetailsForSupplier(obj
									.getLedger().getId(), CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),ofc_id,getOrganizationID());

							if (list2.size() > 0) {

								salesNos = " ";
								payed = 0;
								total = 0;
								it2 = list2.iterator();
								while (it2.hasNext()) {
									salObj = (PurchaseModel) it2.next();
									payed += salObj.getPaymentAmount()+salObj.getPaid_by_payment();
									total += salObj.getAmount();
								}

								i++;

								/*payed += new PaymentDao()
										.getCustomerAllPayedAmt(obj.getLedger()
												.getId(), CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
												CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));*/

								details.setName(obj.getName());
								details.setTotal(roundNumber(total));
								details.setPayed(roundNumber(payed));
								details.setNeed_to_pay(roundNumber(total
										- payed));
								if (details.getNeed_to_pay() > 0)
									reportList.add(details);

								// table.addItem(new Object[]
								// {i,obj.getName(),total, total-payed,
								// salesNos.substring(0,
								// salesNos.length()-1)},i);

							}

						}

						if (reportList.size() > 0) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("FromDate", new java.util.Date());
							params.put("ToDate", new java.util.Date());
							
							report.setJrxmlFileName("NotPayedCustomerReport");
							report.setReportFileName("Not Payed Supplier Report");

							params.put("REPORT_TITLE_LABEL", getPropertyName("not_paid_supplier_report"));
							params.put("CUSTOMER_LABEL", getPropertyName("supplier"));
							params.put("TOTAL_AMOUNT_LABEL", getPropertyName("total_amount"));
							params.put("PAID_AMOUNT_LABEL", getPropertyName("amount_paid"));
							params.put("BALANCE_LABEL", getPropertyName("balance"));
							params.put("TOTAL_LABEL", getPropertyName("total"));
							

							report.setIncludeHeader(true);
							report.setReportType((Integer) choiceField
									.getValue());
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, params);

							reportList.clear();
							list.clear();

						} 
						else {
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
		
		
		generateDetailedButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {
						long ofc_id=0;
						if(officeComboField.getValue()!=null)
							ofc_id=(Long) officeComboField.getValue();

						List reportList = new ArrayList();

						List list = new ArrayList();

						if (customerComboField.getValue() == null
								|| customerComboField.getValue().equals("")
								|| customerComboField.getValue().toString()
										.equals("0"))
							list = cusDao
									.getAllSupplierNamesList((Long) officeComboField
											.getValue());
						else {
							list.add(cusDao.getSupplier((Long) customerComboField.getValue()));
						}

						AcctReportMainBean details;
						Iterator it = list.iterator();
//						Calendar cal;
						List list2;
						Iterator it2;
						SupplierModel obj;
						PurchaseModel salObj;
						while (it.hasNext()) {
							

							obj = (SupplierModel) it.next();

//							cal = Calendar.getInstance();
//							cal.setTime(new java.util.Date());
//
//							cal.add(Calendar.DAY_OF_MONTH,
//									-obj.getMax_credit_period());

							list2 = objDao.getDetailedNotPaidPurchase(obj
									.getLedger().getId(), CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),ofc_id,getOrganizationID());
							
							if (list2.size() > 0) {
								
								it2 = list2.iterator();
								while (it2.hasNext()) {
									
									details = new AcctReportMainBean();
									
									salObj = (PurchaseModel) it2.next();
									
									details.setName(obj.getName());
									details.setAmount(salObj.getAmount());
									details.setNo(salObj.getPurchase_no());
									details.setPayed(salObj.getPaymentAmount()+salObj.getPaid_by_payment());
									details.setNeed_to_pay(salObj.getAmount()-salObj.getPaymentAmount()-salObj.getPaid_by_payment());
									details.setDate(salObj.getDate());
								
									
									details.setTo_date(new Date(salObj.getDate().getTime()+(86400000)));
									
									details.setDue_days((int) ((new java.util.Date().getTime()-details.getTo_date().getTime())/86400000));
									
									if (details.getNeed_to_pay() > 0)
										reportList.add(details);
									
								}
								// table.addItem(new Object[]
								// {i,obj.getName(),total, total-payed,
								// salesNos.substring(0,
								// salesNos.length()-1)},i);
							}

						}

						if (reportList.size() > 0) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("FromDate", new java.util.Date());
							params.put("ToDate", new java.util.Date());
							
							report.setJrxmlFileName("NotPaidCustomerDetailedReport");
							report.setReportFileName("Not Payed Customer Report");

							params.put("REPORT_TITLE_LABEL", getPropertyName("not_paid_supplier_report"));
							params.put("SL_NO_LABEL", getPropertyName("sl_no"));
							params.put("CUSTOMER_LABEL", getPropertyName("customer"));
							params.put("INVOICE_NO_LABEL", getPropertyName("purchase_no"));
							params.put("DATE_LABEL", getPropertyName("date"));
							params.put("AMOUNT_LABEL", getPropertyName("amount"));
							params.put("PAID_AMOUNT_LABEL", getPropertyName("amount_paid"));
							params.put("BALANCE_LABEL", getPropertyName("balance"));
							params.put("MAX_CREDIT_PERIOD_LABEL", getPropertyName("max_credit_period"));
							params.put("DUE_DATE_LABEL", getPropertyName("due_date"));
							params.put("DUE_DAYS_LABEL", getPropertyName("due_days"));
							params.put("TOTAL_LABEL", getPropertyName("total"));
							

							report.setIncludeHeader(true);
//							report.setReportSubTitle("Not Paid Customer Report");
							report.setReportType((Integer) choiceField
									.getValue());
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, params);

							reportList.clear();
							list.clear();

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
