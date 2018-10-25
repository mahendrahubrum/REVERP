package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.dao.ItemWiseStockAuditingReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
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
import com.webspark.Components.ReportReview;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithReview;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 19, 2015
 */

public class ItemWiseSalePurchaseReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;
	SPopupView popUp, subPopUp;
	LedgerDao ledDao;
	SNativeButton closeBtn;
	ItemWiseStockAuditingReportDao daoObj;

	SConfirmWithReview confirmBox;
	ReportReview review;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SID = "SID";
	static String TBC_PID = "PID";
	static String TBC_TYPE = "Type";
	static String TBC_ITEM = "Item";
	static String TBC_NUMBER = "Number";
	static String TBC_OPENING = "Opening Stock";
	static String TBC_SALE = "Sales";
	static String TBC_SALE_RATE = "Sales Rate";
	static String TBC_PURCHASE = "Purchase";
	static String TBC_PURCHASE_RATE = "Purchase Rate";
	static String TBC_SALE_RETURN = "Sales Return";
	static String TBC_PURCHASE_RETURN = "Purchase Return";
	static String TBC_CLOSING = "Closing Stock";
	static String TBC_STOCK_VALUE = "Stock Value";
	
	SHorizontalLayout popupContainer,mainHorizontal,popUpHor;
	Object[] allColumns;
	Object[] visibleColumns;
	Object[] allSubColumns;
	Object[] visibleSubColumns;
	STable table,subtable;
	SButton showButton;
	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();
		closeBtn = new SNativeButton("X");
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_ITEM, TBC_OPENING, TBC_SALE, TBC_SALE_RATE, TBC_PURCHASE, TBC_PURCHASE_RATE, TBC_SALE_RETURN, TBC_PURCHASE_RETURN, TBC_CLOSING, TBC_STOCK_VALUE};
		
		allSubColumns = new Object[] { TBC_SN, TBC_SID,TBC_PID, TBC_TYPE, TBC_ITEM, TBC_NUMBER, TBC_SALE, TBC_SALE_RATE, TBC_PURCHASE, TBC_PURCHASE_RATE};
		
		visibleColumns = new Object[]{ TBC_SN, TBC_ITEM, TBC_OPENING, TBC_SALE, TBC_SALE_RATE, TBC_PURCHASE, TBC_PURCHASE_RATE, TBC_SALE_RETURN, TBC_PURCHASE_RETURN, TBC_CLOSING, TBC_STOCK_VALUE};
		
		visibleSubColumns = new Object[] { TBC_SN, TBC_TYPE, TBC_ITEM, TBC_NUMBER, TBC_SALE, TBC_SALE_RATE, TBC_PURCHASE, TBC_PURCHASE_RATE};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		popUpHor = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 1000, 250);
		subtable = new STable(null, 750, 250);
		
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);
		table.addContainerProperty(TBC_OPENING, String.class, null, getPropertyName("opening_stock"), null,Align.CENTER);
		table.addContainerProperty(TBC_SALE, String.class, null,getPropertyName("sales"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALE_RATE, Double.class, null,getPropertyName("sale_rate"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE, String.class, null,getPropertyName("purchase"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE_RATE, Double.class, null,getPropertyName("purchase_rate"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALE_RETURN, String.class, null,getPropertyName("sales_return"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE_RETURN, String.class, null,getPropertyName("purchase_return"), null, Align.LEFT);
		table.addContainerProperty(TBC_CLOSING, String.class, null,getPropertyName("closing_stock"), null, Align.LEFT);
		table.addContainerProperty(TBC_STOCK_VALUE, Double.class, null,getPropertyName("stock_value"), null, Align.LEFT);
		
		subtable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		subtable.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
		subtable.addContainerProperty(TBC_PID, Long.class, null, TBC_PID, null,Align.CENTER);
		subtable.addContainerProperty(TBC_TYPE, String.class, null, getPropertyName("type"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_NUMBER, String.class, null, getPropertyName("number"), null,Align.CENTER);
		subtable.addContainerProperty(TBC_SALE, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
		subtable.addContainerProperty(TBC_SALE_RATE, Double.class, null,getPropertyName("sale_rate"), null, Align.LEFT);
		subtable.addContainerProperty(TBC_PURCHASE, Double.class, null,getPropertyName("purchase"), null, Align.LEFT);
		subtable.addContainerProperty(TBC_PURCHASE_RATE, Double.class, null,getPropertyName("purchase_rate"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_ITEM, (float) 2);
		table.setColumnExpandRatio(TBC_OPENING, (float) 1.5);
		table.setColumnExpandRatio(TBC_SALE, (float) 1.5);
		table.setColumnExpandRatio(TBC_SALE_RATE, (float) 1);
		table.setColumnExpandRatio(TBC_PURCHASE, (float) 1.5);
		table.setColumnExpandRatio(TBC_PURCHASE_RATE, (float) 1);
		table.setColumnExpandRatio(TBC_PURCHASE_RETURN, (float) 1.5);
		
		
		subtable.setColumnExpandRatio(TBC_TYPE, 1f);
		subtable.setColumnExpandRatio(TBC_ITEM, (float) 2);
		subtable.setColumnExpandRatio(TBC_NUMBER, 1f);
		subtable.setColumnExpandRatio(TBC_SALE, 1f);
		subtable.setColumnExpandRatio(TBC_SALE_RATE, 1f);
		subtable.setColumnExpandRatio(TBC_PURCHASE, 1f);
		subtable.setColumnExpandRatio(TBC_PURCHASE_RATE, 1f);
		
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		subtable.setSelectable(true);
		subtable.setMultiSelect(false);
		subtable.setVisibleColumns(visibleSubColumns);
		
		daoObj = new ItemWiseStockAuditingReportDao();
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1350, 350);
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

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(popUpHor);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.addComponent(table);
			
			mainHorizontal.setMargin(true);
			
			review.addComponent(mainHorizontal, "left: 0px; right: 0px; z-index:-1;");
			mainPanel.setContent(review);
			
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReview(getOptionId(),confirmBox.getTitle(),confirmBox.getComments()	,getLoginID(),report.getReportFile());
							SNotification.show("Review Saved");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					confirmBox.close();
					confirmBox.setTitle("");
					confirmBox.setComments("");
				}
				
			};
			confirmBox.setClickListener(confirmListener);
			
			
			review.setClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals(ReportReview.REVIEW)){
						if(generateReport())
							confirmBox.open();
					}
				}
			});

			
			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});
			
			
			organizationComboField.addValueChangeListener(new Property.ValueChangeListener() {
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

			
			itemsComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					customerId = 0;
					if (itemsComboField.getValue() != null
							&& !itemsComboField.getValue().toString()
									.equals("0")) {
						customerId = toLong(itemsComboField.getValue()
								.toString());
					}
				}
			});

			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadItemsCombo(toLong(officeComboField.getValue().toString()));
				}
			});

			
			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};
			
			
			subtable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (subtable.getValue() != null) {
							Item item = subtable.getItem(subtable.getValue());
							if(item.getItemProperty(TBC_TYPE).getValue().toString().equals("Sales")){
								SalesNewUI sales = new SalesNewUI();
								sales.setCaption(getPropertyName("sales"));
								sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_SID).getValue());
								sales.center();
								popUp.setVisible(false);
								getUI().getCurrent().addWindow(sales);
								sales.addCloseListener(closeListener);
							}
							else if(item.getItemProperty(TBC_TYPE).getValue().toString().equals("Purchase")){
								PurchaseUI sales = new PurchaseUI();
								sales.setCaption(getPropertyName("purchase"));
								sales.getPurchaseNumberList().setValue((Long) item.getItemProperty(TBC_PID).getValue());
								sales.center();
								popUp.setVisible(false);
								getUI().getCurrent().addWindow(sales);
								sales.addCloseListener(closeListener);
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

			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(isValid()){
							if (table.getValue() != null) {
								subtable.removeAllItems();
								subtable.setVisibleColumns(allSubColumns);
								Item item=table.getItem(table.getValue());
								long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
								List detailList=daoObj.getSalePurchaseDetails(	id,
																				(Long)officeComboField.getValue(),
																				CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																				CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
								if(detailList.size()>0){
									Iterator it=detailList.iterator();
									while (it.hasNext()) {
										ReportBean bean = (ReportBean) it.next();
										
										subtable.addItem(new Object[]{subtable.getItemIds().size()+1,
																		bean.getId(),
																		bean.getNumber(),
																		bean.getTitle(),
																		bean.getItem_name(),
																		bean.getPaymentNo(),
																		roundNumber(bean.getSalesQty()),
																		roundNumber(bean.getSale()),
																		roundNumber(bean.getPurchaseQty()),
																		roundNumber(bean.getPurchase())},subtable.getItemIds().size()+1);
									}
								}
								subtable.setVisibleColumns(visibleSubColumns);
								popUp = new SPopupView("",new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(
										null,"<h2><u style='margin-left: 40px;'>Item Wise Sales & Purchase",
										725), closeBtn), subtable));
								popUpHor.addComponent(popUp);
								popUp.setPopupVisible(true);
								popUp.setHideOnMouseOut(false);
							}
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
							SFormLayout form = new SFormLayout();
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							if(item.getItemProperty(TBC_TYPE).getValue().toString().equals("Sales")){
								long id = (Long) item.getItemProperty(TBC_SID).getValue();
								SalesModel sale=new SalesDao().getSale(id);
								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
								form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
//								form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
//								if (isShippingChargeEnable())
//									form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
								form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber(sale.getAmount()) + ""));
								form.addComponent(new SLabel(getPropertyName("paid_amount"),roundNumber(sale.getPayment_amount()) + ""));
								form.addComponent(new SLabel(getPropertyName("payment"),roundNumber(sale.getPaid_by_payment()) + ""));
								form.addComponent(new SLabel(getPropertyName("balance"),roundNumber(sale.getAmount()-sale.getPayment_amount()-sale.getPaid_by_payment()) + ""));
								grid.setColumns(12);
								grid.setRows(sale
										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
								grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
								grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
								grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
								grid.setSpacing(true);
								
								int i = 1;
								SalesInventoryDetailsModel invObj;
								Iterator itr = sale.getInventory_details_list().iterator();
								while(itr.hasNext()){
									invObj=(SalesInventoryDetailsModel)itr.next();
									grid.addComponent(new SLabel(null, i + ""),	0, i);
									grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
									grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
									grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
//									grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
//									grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
//																		- invObj.getDiscount_amount() 
//																		+ invObj.getTax_amount())+ ""), 6, i);
									i++;
								}
								form.addComponent(grid);
								form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
								form.setStyleName("grid_max_limit");
							}
							else if(item.getItemProperty(TBC_TYPE).getValue().toString().equals("Purchase")){
								long id = (Long) item.getItemProperty(TBC_PID).getValue();
								PurchaseModel sale=new PurchaseDao().getPurchaseModel(id);
								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("purchase_no"),sale.getPurchase_no()+""));
								form.addComponent(new SLabel(getPropertyName("supplier"),sale.getSupplier().getName()));
								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
//								form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
//								if (isShippingChargeEnable())
//									form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
								form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber(sale.getAmount()) + ""));
//								form.addComponent(new SLabel(getPropertyName("paid_amount"),roundNumber(sale.getPayment_amount()) + ""));
//								form.addComponent(new SLabel(getPropertyName("payment"),roundNumber(sale.getPaid_by_payment()) + ""));
//								form.addComponent(new SLabel(getPropertyName("balance"),roundNumber(sale.getAmount()-sale.getPayment_amount()-sale.getPaid_by_payment()) + ""));
								grid.setColumns(12);
//								grid.setRows(sale
//										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
								grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
								grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
								grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
								grid.setSpacing(true);
								
								int i = 1;
//								PurchaseInventoryDetailsModel invObj;
//								Iterator itr = sale.getInventory_details_list().iterator();
//								while(itr.hasNext()){
//									invObj=(PurchaseInventoryDetailsModel)itr.next();
//									grid.addComponent(new SLabel(null, i + ""),	0, i);
//									grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
//									grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
//									grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
//									grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
//									grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
//									grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
//																		- invObj.getDiscount_amount() 
//																		+ invObj.getTax_amount())+ ""), 6, i);
//									i++;
//								}
								form.addComponent(grid);
								form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
								form.setStyleName("grid_max_limit");
							}
							subPopUp=new SPopupView("", form);
							popupContainer.addComponent(subPopUp);
							subPopUp.setPopupVisible(true);
							subPopUp.setHideOnMouseOut(false);
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
					if (isValid()) {
						showReport();
					}
				}
			});
			
			
			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					generateReport();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	
	@SuppressWarnings("rawtypes")
	protected boolean showReport() {
		boolean flag=false;
		table.removeAllItems();
		table.setVisibleColumns(allColumns);
		try {

			List<Object> reportList;

			long itmId = 0;

			if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("")) {
				itmId = toLong(itemsComboField.getValue().toString());
			}

			reportList = daoObj.getItemWiseStockReport(	itmId, 
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														toLong(officeComboField.getValue().toString()));
			
			if(reportList.size()>0){
				ReportBean bean=null;
				Iterator itr=reportList.iterator();
				while(itr.hasNext()){
					bean=(ReportBean)itr.next();
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							bean.getId(),
							bean.getItem_name(),
							roundNumber(bean.getOpening())+" "+bean.getUnit(),
							roundNumber(bean.getSalesQty())+" "+bean.getUnit(),
							roundNumber(bean.getSale()),
							roundNumber(bean.getPurchaseQty())+" "+bean.getUnit(),
							roundNumber(bean.getPurchase()),
							roundNumber(bean.getSalesRtnQty())+" "+bean.getUnit(),
							roundNumber(bean.getPurchaseRtnQty())+" "+bean.getUnit(),
							roundNumber(bean.getClosing())+" "+bean.getUnit(),
							roundNumber(bean.getBalance())},table.getItemIds().size()+1);
				}
			}
			else{
				SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
			}
			table.setVisibleColumns(visibleColumns);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	protected boolean generateReport() {
		boolean flag=false;

		try {

			List<Object> reportList;

			long itmId = 0;

			if (itemsComboField.getValue() != null
					&& !itemsComboField.getValue().equals("")) {
				itmId = toLong(itemsComboField.getValue()
						.toString());
			}

			reportList = daoObj.getItemWiseStockReport(
					itmId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue()),
					toLong(officeComboField.getValue().toString()));

			if (reportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				report.setJrxmlFileName("ItemWiseSalePurchaseReport");
				report.setReportFileName("Item Wise Sale Purchase Report");
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_sale_purchase_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("OPENING_STOCK_LABEL", getPropertyName("opening_stock"));
				map.put("PURCHASE_LABEL", getPropertyName("purchase"));
				map.put("SALES_LABEL", getPropertyName("sales"));
				map.put("SALES_RETURN_LABEL", getPropertyName("sales_return"));
				map.put("PURCHASE_RETURN_LABEL", getPropertyName("purchase_return"));
				map.put("BALANCE_LABEL", getPropertyName("balance"));
				map.put("SALES_RATE_LABEL", getPropertyName("sales_rate"));
				map.put("PURCHAE_RATE_LABEL", getPropertyName("purchae_rate"));
				map.put("STOCK_VALUE", getPropertyName("stock_value"));
				map.put("CLOSING_LABEL", getPropertyName("closing_stock"));
				
				report.setReportTitle("Stock Auditing Report");

				String subHeader = "";

				if (itemsComboField.getValue() != null)
					if (!itemsComboField.getValue().toString()
							.equals("0"))
						subHeader += getPropertyName("item")+" : "
								+ itemsComboField
										.getItemCaption(itemsComboField
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
				flag=true;

			} else {
				SNotification.show(
						getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void loadItemsCombo(long officeId) {
		List itemsList = null;
		try {
			if (officeId != 0) {
				itemsList = new ItemDao()
						.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
								.getValue());
			}

			ItemModel salesModel = new ItemModel(0,
					getPropertyName("all"));
			if (itemsList == null) {
				itemsList = new ArrayList<Object>();
			}
			itemsList.add(0, salesModel);

			SCollectionContainer bic1 = SCollectionContainer.setList(itemsList,
					"id");
			itemsComboField.setContainerDataSource(bic1);
			itemsComboField.setItemCaptionPropertyId("name");
			itemsComboField.setValue((long)0);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		

		if(fromDateField.getValue()==null) {
			setRequiredError(fromDateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(fromDateField, null, false);
		if(toDateField.getValue()==null) {
			setRequiredError(toDateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(toDateField, null, false);
		if(fromDateField.getValue().compareTo(toDateField.getValue())>0) {
			setRequiredError(fromDateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(fromDateField, null, false);
		if(itemsComboField.getValue()==null || itemsComboField.getValue().toString().equals("")) {
			itemsComboField.setValue((long)0);
		}
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
