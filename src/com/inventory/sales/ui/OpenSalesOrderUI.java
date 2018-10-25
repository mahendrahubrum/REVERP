package com.inventory.sales.ui;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class OpenSalesOrderUI extends SparkLogic{

	private SPanel panel;
	private SComboField officeComboField;
	private OfficeDao officeDao;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SButton loadButton;
	private STable table;
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	private SalesReportDao salesReportDao;
	private SLabel orderNoLabel;
	private SLabel dateLabel;
	private SLabel customerLabel;
	private SLabel totalAmountLabel;
	private SLabel statusLabel;
	private SButton printButton;
	private SPopupView popUpView;
	
	private static final String TBC_ID = "Id";
	private static final String TBC_SLNO = "Sl. No";
	private static final String TBC_ORDER_NO = "Order No";
	private static final String TBC_DATE = "Date";
	private static final String TBC_CUSTOMER = "Customer";
	private static final String TBC_NET_AMOUNT = "Net Amount";
	private static final String TBC_DISCOUNT_AMOUNT = "Discount Amount";
	private static final String TBC_TOTAL_AMOUNT = "Total Amount";
	private static final String TBC_STATUS = "Status";
	
//	int clickCount=0;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		setSize(1000, 500);
		panel = new SPanel();
		panel.setSizeFull();
		
		allHeaders = new Object[]{TBC_ID, TBC_SLNO, TBC_ORDER_NO, TBC_DATE, 
				TBC_CUSTOMER, TBC_NET_AMOUNT,TBC_DISCOUNT_AMOUNT, TBC_TOTAL_AMOUNT, TBC_STATUS};
		visibleHeaders = new Object[]{TBC_SLNO, TBC_ORDER_NO, TBC_DATE, TBC_CUSTOMER, 
				 TBC_NET_AMOUNT,TBC_DISCOUNT_AMOUNT, TBC_TOTAL_AMOUNT, TBC_STATUS};
		
		officeDao = new OfficeDao();
		salesReportDao = new SalesReportDao();
		final SVerticalLayout mainFieldLayout = new SVerticalLayout();
		mainFieldLayout.setMargin(true);
		mainFieldLayout.setSizeFull();
		
		final SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		
		final SHorizontalLayout dateFieldLayout = new SHorizontalLayout();
		dateFieldLayout.setSpacing(true);
		
		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());
		
		fromDateField = new SDateField(getPropertyName("from_date"));		
		fromDateField.setImmediate(true);
		
		Date fromDate = getMonthStartDate();
		fromDate.setMonth(fromDate.getMonth() - 2);
		fromDateField.setValue(fromDate);		

		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		toDateField.setImmediate(true);
		
		loadButton = new SButton(getPropertyName("load"));
		
		table = new STable(null, 900, 250);
		table.setSelectable(true);
		table.addContainerProperty(TBC_ID, Long.class, null,
				getPropertyName("id"), null, Align.LEFT);
		table.addContainerProperty(TBC_SLNO, Integer.class, null,
				getPropertyName("sl_no"), null, Align.CENTER);
		table.addContainerProperty(TBC_ORDER_NO, String.class, null,
				getPropertyName("order_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_DATE,Date.class, null,
				getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,
				getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_NET_AMOUNT, Double.class, null,
				getPropertyName("net_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_DISCOUNT_AMOUNT, Double.class, null,
				getPropertyName("discount_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_TOTAL_AMOUNT, Double.class, null,
				getPropertyName("total_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_STATUS, String.class, null,
				getPropertyName("status"), null, Align.LEFT);
		
		table.setVisibleColumns(visibleHeaders);
		table.setColumnExpandRatio(TBC_SLNO, 0.5f);
		table.setColumnExpandRatio(TBC_ORDER_NO, 1.3f);
		table.setColumnExpandRatio(TBC_CUSTOMER, 2.5f);
		table.setColumnExpandRatio(TBC_DATE, 1f);
		table.setColumnExpandRatio(TBC_NET_AMOUNT, 1.5f);
		
		createPopUpView();
		
		dateFieldLayout.addComponent(fromDateField);
		dateFieldLayout.addComponent(toDateField);
		dateFieldLayout.addComponent(loadButton);
		dateFieldLayout.setComponentAlignment(loadButton, Alignment.BOTTOM_LEFT);
		
		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(dateFieldLayout);
		mainFormLayout.addComponent(popUpView);
		mainFormLayout.addComponent(table);
	//	mainFormLayout.setComponentAlignment(table, Alignment.TOP_CENTER);
		
		mainFieldLayout.addComponent(mainFormLayout);		
		mainFieldLayout.setComponentAlignment(mainFormLayout, Alignment.TOP_CENTER);
			
		panel.setContent(mainFieldLayout);
		
//		table.addHeaderClickListener(new HeaderClickListener() {
//			
//			@Override
//			public void headerClick(HeaderClickEvent event) {
//				if(event.getPropertyId()==TBC_DATE){
//					clickCount+=1;
//					System.out.println(clickCount);
//					loadDataInTable(true);
//				}
//			}
//		});
		
		loadButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					loadDataInTable(false);
				}
			}
		});
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue() != null){
					popUpView.setPopupVisible(true);
					Item item = table.getItem(table.getValue());
					orderNoLabel.setValue(item.getItemProperty(TBC_ORDER_NO).getValue().toString());
					dateLabel.setValue(item.getItemProperty(TBC_DATE).getValue().toString());
					customerLabel.setValue(item.getItemProperty(TBC_CUSTOMER).getValue().toString());
					totalAmountLabel.setValue(item.getItemProperty(TBC_TOTAL_AMOUNT).getValue().toString());
					statusLabel.setValue(item.getItemProperty(TBC_STATUS).getValue().toString());
				}
			}
		});
		
		printButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				SalesOrderUI option = new SalesOrderUI();
				option.getsalesOrderNumberList().setValue((Long)table.getValue());
				option.printButton.click();
			}
		});
		loadButton.click();
		
		
		return panel;
	}

	protected void loadDataInTable(boolean isDateSort) {
		try {
			List list = salesReportDao.getSalesOrderDetails(0, 0, 
					CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
					CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
					toLong(officeComboField.getValue().toString()));
			if(list.size() > 0){
//				System.out.println(clickCount);
//				if(isDateSort){
//					Collections.sort(list,new Comparator<SalesOrderModel>() {
//
//						@Override
//						public int compare(SalesOrderModel o1,SalesOrderModel o2) {
//							if(clickCount%2==0)
//								return CommonUtil.getUtilDateFromSQLDate(o1.getDate()).compareTo(CommonUtil.getUtilDateFromSQLDate(o2.getDate()));
//							else
//								return CommonUtil.getUtilDateFromSQLDate(o2.getDate()).compareTo(CommonUtil.getUtilDateFromSQLDate(o1.getDate()));
//						}
//					});
//				}
//				
				Iterator<?> itr = list.iterator();
				int slNo = 1;
				table.setVisibleColumns(allHeaders);
				
				while(itr.hasNext()){
					SalesOrderModel model = (SalesOrderModel) itr.next();
					table.addItem(new Object[]{model.getId(),
							slNo,
							model.getOrder_no(),
							model.getDate(),
							model.getCustomer().getName(),
							(model.getAmount() + model.getDiscountAmount()),
							model.getDiscountAmount(),
							model.getAmount(),
							(model.getLock_count() > 0 ? getPropertyName("closed") : getPropertyName("pending"))},
							model.getId());			
					slNo++;
				}
				table.setVisibleColumns(visibleHeaders);
			} else {
				SNotification.show(
						getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private void createPopUpView() {		
		orderNoLabel = new SLabel(getPropertyName("order_no")+" : ");		
		dateLabel = new SLabel(getPropertyName("date")+" : ");		
		customerLabel = new SLabel(getPropertyName("customer")+" : ");		
		totalAmountLabel = new SLabel(getPropertyName("total_amount")+" : ");		
		statusLabel = new SLabel(getPropertyName("status")+" : ");		
		
		printButton = new SButton(getPropertyName("print"), 78);
		printButton.setIcon(new ThemeResource("icons/print.png"));
		printButton.setStyleName("deletebtnStyle");
		
		SFormLayout form = new SFormLayout();
		form.setMargin(true);
		form.setSpacing(true);
		form.addComponent(orderNoLabel);
		form.addComponent(dateLabel);
		form.addComponent(customerLabel);
		form.addComponent(totalAmountLabel);
		form.addComponent(statusLabel);
		form.addComponent(printButton);
		form.setComponentAlignment(printButton, Alignment.MIDDLE_CENTER);
		
		popUpView = new SPopupView(null, form);
		popUpView.setPopupVisible(false);
		popUpView.setHideOnMouseOut(false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getOfficeList() {
		List<S_OfficeModel> list = new ArrayList<S_OfficeModel>();
		list.add(new S_OfficeModel(0, "---- "+getPropertyName("select")+" -----"));
		try {
			list.addAll(officeDao.getAllOfficesUnderOrg(getOrganizationID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;
		if(officeComboField.getValue() == null || officeComboField.getValue().equals("")){
			setRequiredError(officeComboField, getPropertyName("invalid_selection"), true);			
			valid = false;
		} else {
			setRequiredError(officeComboField, null, false);
		}
		
		if(fromDateField.getValue() == null || fromDateField.getValue().equals("")){
			setRequiredError(fromDateField, getPropertyName("invalid_date"), true);			
			valid = false;
		} else {
			setRequiredError(fromDateField, null, false);
		}
		
		if(toDateField.getValue() == null || toDateField.getValue().equals("")){
			setRequiredError(toDateField, getPropertyName("invalid_date"), true);			
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
