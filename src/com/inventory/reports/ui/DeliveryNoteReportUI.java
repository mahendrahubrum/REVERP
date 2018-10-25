package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.DeliveryNoteReportBean;
import com.inventory.reports.dao.DeliveryNoteReportDao;
import com.inventory.sales.dao.DeliveryNoteDao;
import com.inventory.sales.model.DeliveryNoteDetailsModel;
import com.inventory.sales.model.DeliveryNoteModel;
import com.inventory.sales.ui.DeliveryNoteUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
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
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 28, 2013
 */
public class DeliveryNoteReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField responsilbeEmployeeCombo;
	private SComboField deliveryNoteNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long employId;

	private Report report;

	CustomerDao customerDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_OFFICE = "Office";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_SALES_MAN = "Sales Man";
	static String TBC_SALES = "Sales No";
	static String TBC_ITEMS = "Items";
	static String TBC_AMOUNT = "Amount";
	

	SHorizontalLayout popupContainer, mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	private WrappedSession session;
	private SettingsValuePojo settings;

	private HashMap<Long, String> currencyHashMap;

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE, TBC_OFFICE, TBC_CUSTOMER, TBC_SALES_MAN, TBC_SALES, TBC_ITEMS, TBC_AMOUNT};
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE, TBC_OFFICE,TBC_CUSTOMER, TBC_SALES_MAN, TBC_SALES,TBC_ITEMS, TBC_AMOUNT};
		mainHorizontal = new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton = new SButton(getPropertyName("show"));
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		List<Object> tempList = new ArrayList<Object>();
		Collections.addAll(tempList, visibleColumns);
		if(settings.isSALES_MAN_WISE_SALES()){
			tempList.remove(TBC_CUSTOMER);
		}
		visibleColumns = tempList.toArray(new String[tempList.size()]);
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"),null, Align.LEFT);
		table.addContainerProperty(TBC_OFFICE, String.class, null,getPropertyName("office"), null, Align.LEFT);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALES, String.class, null,getPropertyName("sales_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, String.class, null, getPropertyName("amount"),null, Align.LEFT);
		
		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 2);
		table.setColumnExpandRatio(TBC_ITEMS, (float) 1);
		table.setColumnExpandRatio(TBC_OFFICE, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		customerDao = new CustomerDao();

		employId = 0;
		report = new Report(getLoginID());

		setSize(1050, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {

			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			fromDateField = new SDateField(getPropertyName("from_date"));
			fromDateField.setValue(getMonthStartDate());
			toDateField = new SDateField(getPropertyName("to_date"));
			toDateField.setValue(getWorkingDate());
			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);

			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, getPropertyName("all"));
//			new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),"id", "first_name"
			responsilbeEmployeeCombo = new SComboField(getPropertyName("sales_man"),200,null, "id", "first_name", false, getPropertyName("select"));
			if(!settings.isSALES_MAN_WISE_SALES())
				mainFormLayout.addComponent(customerComboField);
			else
				mainFormLayout.addComponent(responsilbeEmployeeCombo);
			List<Object> deliveryNoteBillList = new DeliveryNoteDao()
					.getAllDeliveryNoteNumbers(getOfficeID());
			DeliveryNoteModel delNotModel = new DeliveryNoteModel();
			delNotModel.setId(0);
			delNotModel
					.setDeliveryNo(getPropertyName("all"));
			if (deliveryNoteBillList == null) {
				deliveryNoteBillList = new ArrayList<Object>();
			}
			deliveryNoteBillList.add(0, delNotModel);

			deliveryNoteNoComboField = new SComboField(
					getPropertyName("delivery_note_no"), 200,
					deliveryNoteBillList, "id", "deliveryNo", false, getPropertyName("all"));
			mainFormLayout.addComponent(deliveryNoteNoComboField);

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
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

			mainPanel.setContent(mainHorizontal);

			
			customerComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					employId = 0;
					if (customerComboField.getValue() != null && !customerComboField.getValue().toString().equals("0")) {
						employId = toLong(customerComboField.getValue().toString());
					}
					loadBillNo(employId, toLong(officeComboField.getValue().toString()));
				}
			});

			
			responsilbeEmployeeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					employId = 0;
					if (responsilbeEmployeeCombo.getValue() != null && !responsilbeEmployeeCombo.getValue().toString().equals("0")) {
						employId = toLong(responsilbeEmployeeCombo.getValue().toString());
					}
					loadBillNo(employId, toLong(officeComboField.getValue().toString()));
				}
			});
			
			
			fromDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(employId, toLong(officeComboField.getValue().toString()));
				}
			});
			
			
			toDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(employId, toLong(officeComboField.getValue()
							.toString()));
				}
			});
			
			
			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					// loadCustomerCombo(toLong(officeComboField.getValue()
					// .toString()));
					loadBillNo(employId, toLong(officeComboField.getValue().toString()));

					List customerList = new ArrayList();
					
					if(!settings.isSALES_MAN_WISE_SALES()){
						try {
							customerList = customerDao.getAllActiveCustomerNamesWithLedgerID((Long) officeComboField.getValue());
						} catch (Exception e) {
							e.printStackTrace();
						}
						customerList.add(0, new CustomerModel(0, getPropertyName("all")));
						container = SCollectionContainer.setList(customerList, "id");
						customerComboField.setContainerDataSource(container);
						customerComboField.setItemCaptionPropertyId("name");
					}
					else{
						try {
							customerList= new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice((Long) officeComboField.getValue());
						} catch (Exception e) {
							e.printStackTrace();
						}
						customerList.add(0, new UserModel(0, getPropertyName("all")));
						container = SCollectionContainer.setList(customerList, "id");
						responsilbeEmployeeCombo.setContainerDataSource(container);
						responsilbeEmployeeCombo.setItemCaptionPropertyId("first_name");
					}
					customerComboField.setValue((long)0);
					responsilbeEmployeeCombo.setValue((long)0);
				}
			});

			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			
			final Action action = new Action("Edit");

			
			table.addActionHandler(new Handler() {

				@Override
				public void handleAction(Action action, Object sender,
						Object target) {
					try {
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							DeliveryNoteUI option = new DeliveryNoteUI();
							option.setCaption(getPropertyName("delivery_note"));
							option.getSalesOrderNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
					} catch (Exception e) {
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
					try {
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							DeliveryNoteModel mdl = new DeliveryNoteDao().getDeliveryNoteModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("delivery_note")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getDeliveryNo()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
//							if (isShippingChargeEnable())
//								form.addComponent(new SLabel(getPropertyName("shipping_charge"),mdl.getShipping_charge() + ""));
							double amount = mdl.getAmount() / mdl.getConversionRate();
							if(mdl.getCurrencyId() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(mdl.getAmount())+" "+getCurrencyDescription(getCurrencyID())));										
							} else {
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())
										+" ("+roundNumber(mdl.getAmount())+" "+getCurrencyDescription(mdl.getCurrencyId())+")"));								
							}
				//			form.addComponent(new SLabel(getPropertyName("net_amount"),mdl.getAmount() + ""));
						//	form.addComponent(new SLabel(getPropertyName("paid_amount"),mdl.getPayment_amount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(mdl.getDelivery_note_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
						//	grid.addComponent(new SLabel(null, getPropertyName("discount")),5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
							grid.setSpacing(true);
							
							int i = 1;
							DeliveryNoteDetailsModel invObj;
							Iterator itr = mdl.getDelivery_note_details_list().iterator();
							while(itr.hasNext()){
								invObj=(DeliveryNoteDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
							//	grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity())+ " "+getCurrencyDescription(getCurrencyID())), 6, i);
								i++;
							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), mdl.getComments()));
							form.setStyleName("grid_max_limit");
							popupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					} catch (Exception e) {
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
						boolean noData = true;
						DeliveryNoteModel delNotModel = null;
						DeliveryNoteDetailsModel inventoryDetailsModel = null;
						DeliveryNoteReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();

						long delNotNo = 0;
						long empId = 0;

						if (deliveryNoteNoComboField.getValue() != null
								&& !deliveryNoteNoComboField.getValue().equals(
										"")
								&& !deliveryNoteNoComboField.getValue()
										.toString().equals("0")) {
							delNotNo = toLong(deliveryNoteNoComboField
											.getValue().toString());
						}
						if(!settings.isSALES_MAN_WISE_SALES()){
							if (customerComboField.getValue() != null && !customerComboField.getValue().equals("")) {
								empId = toLong(customerComboField.getValue()
										.toString());
							}
						}
						else{
							if (responsilbeEmployeeCombo.getValue() != null && !responsilbeEmployeeCombo.getValue().equals("")) {
								empId = toLong(responsilbeEmployeeCombo.getValue()
										.toString());
							}
						}
						List<Object> delNotModelList=new ArrayList<Object>();
						
						if(!settings.isSALES_MAN_WISE_SALES()){
							delNotModelList = new DeliveryNoteReportDao().getSalesDetailsCustomer(delNotNo, empId, CommonUtil
									.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
									toLong(officeComboField.getValue()
											.toString()));
						}
						else{
							delNotModelList = new DeliveryNoteReportDao().getSalesDetailsSalesMan(delNotNo, empId, CommonUtil
									.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
									toLong(officeComboField.getValue()
											.toString()));
						}

						List<DeliveryNoteDetailsModel> detailsList;
						double amount ;
						for (int i = 0; i < delNotModelList.size(); i++) {
							noData = false;
							delNotModel = (DeliveryNoteModel) delNotModelList
									.get(i);
							detailsList = delNotModel
									.getDelivery_note_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += " , ";
								}
								items += inventoryDetailsModel.getItem()
										.getName();
							}

						/*	user = customerDao.getCustomer(delNotModel
									.getCustomer()Responsible_employee());*/
							amount = delNotModel.getAmount() / delNotModel.getConversionRate();
							UserModel userMdl=new UserManagementDao().getUser(delNotModel.getResponsible_employee());
							if(!settings.isSALES_MAN_WISE_SALES()){
								if(delNotModel.getCurrencyId() == getCurrencyID()){
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													delNotModel.getId(),
													delNotModel.getDate().toString(),
													delNotModel.getOffice().getName(),
													delNotModel.getCustomer().getName(),
													userMdl.getFirst_name()+" "+userMdl.getMiddle_name()+" "+userMdl.getLast_name(),
													delNotModel.getDeliveryNo(),
													items,
													delNotModel.getAmount()+" "+getCurrencyDescription(getCurrencyID())},
											table.getItemIds().size() + 1);
								} else {
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													delNotModel.getId(),
													delNotModel.getDate().toString(),
													delNotModel.getOffice().getName(),
													delNotModel
													.getCustomer().getName(),
													userMdl.getFirst_name()+" "+userMdl.getMiddle_name()+" "+userMdl.getLast_name(),
													delNotModel.getDeliveryNo(),
													items,
													roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())+
													" ("+delNotModel.getAmount()+" "+getCurrencyDescription(delNotModel.getCurrencyId())+")"},
											table.getItemIds().size() + 1);
								}
							}
							else{
								if(delNotModel.getCurrencyId() == getCurrencyID()){
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													delNotModel.getId(),
													delNotModel.getDate().toString(),
													delNotModel.getOffice().getName(),
													"",
													userMdl.getFirst_name()+" "+userMdl.getMiddle_name()+" "+userMdl.getLast_name(),
													delNotModel.getDeliveryNo(),
													items,
													delNotModel.getAmount()+" "+getCurrencyDescription(getCurrencyID())},
											table.getItemIds().size() + 1);
								} else {
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													delNotModel.getId(),
													delNotModel.getDate().toString(),
													delNotModel.getOffice().getName(),
													"",
													userMdl.getFirst_name()+" "+userMdl.getMiddle_name()+" "+userMdl.getLast_name(),
													delNotModel.getDeliveryNo(),
													items,
													roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())+
													" ("+delNotModel.getAmount()+" "+getCurrencyDescription(delNotModel.getCurrencyId())+")"},
											table.getItemIds().size() + 1);
								}
							}
							
							
						}
						table.setVisibleColumns(visibleColumns);
						
						if(noData) {
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						DeliveryNoteModel delNotModel = null;
						DeliveryNoteDetailsModel inventoryDetailsModel = null;
						DeliveryNoteReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();

						long delNotNo = 0;
						long empId = 0;

						if (deliveryNoteNoComboField.getValue() != null
								&& !deliveryNoteNoComboField.getValue().equals(
										"")
								&& !deliveryNoteNoComboField.getValue()
										.toString().equals("0")) {
							delNotNo = toLong(deliveryNoteNoComboField
											.getValue().toString());
						}
						if(!settings.isSALES_MAN_WISE_SALES()){
							if (customerComboField.getValue() != null && !customerComboField.getValue().equals("")) {
								empId = toLong(customerComboField.getValue()
										.toString());
							}
						}
						else{
							if (responsilbeEmployeeCombo.getValue() != null && !responsilbeEmployeeCombo.getValue().equals("")) {
								empId = toLong(responsilbeEmployeeCombo.getValue()
										.toString());
							}
						}
						List<Object> delNotModelList=new ArrayList<Object>();
						
						if(!settings.isSALES_MAN_WISE_SALES()){
							delNotModelList = new DeliveryNoteReportDao().getSalesDetailsCustomer(delNotNo, empId, CommonUtil
									.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
									toLong(officeComboField.getValue()
											.toString()));
						}
						else{
							delNotModelList = new DeliveryNoteReportDao().getSalesDetailsSalesMan(delNotNo, empId, CommonUtil
									.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDateField
											.getValue()),
									toLong(officeComboField.getValue()
											.toString()));
						}

						UserModel user;
						List<DeliveryNoteDetailsModel> detailsList;
						double amount;
						for (int i = 0; i < delNotModelList.size(); i++) {
							noData = false;
							delNotModel = (DeliveryNoteModel) delNotModelList
									.get(i);
							detailsList = delNotModel
									.getDelivery_note_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += " , ";
								}
								items += inventoryDetailsModel.getItem()
										.getName();
							}

						/*	user = customerDao.getUser(delNotModel
									.getResponsible_employee());*/
							
							if(!settings.isSALES_MAN_WISE_SALES()){
								amount = delNotModel.getAmount() / delNotModel.getConversionRate();
								if(delNotModel.getCurrencyId() == getCurrencyID()){
									reportBean = new DeliveryNoteReportBean(delNotModel.getDate().toString(),
											delNotModel.getCustomer().getName(),
											String.valueOf(delNotModel.getDeliveryNo()),
											delNotModel.getOffice().getName(), items,
											delNotModel.getAmount());
									reportBean.setCurrency(getCurrencyDescription(getCurrencyID()));
								} else {
									reportBean = new DeliveryNoteReportBean(delNotModel.getDate().toString(),
											delNotModel.getCustomer().getName(),
											String.valueOf(delNotModel.getDeliveryNo()),
											delNotModel.getOffice().getName(), items,
											roundNumber(amount));
									reportBean.setCurrency(getCurrencyDescription(getCurrencyID())+
											" ("+delNotModel.getAmount()+" "+getCurrencyDescription(delNotModel.getCurrencyId())+")");
									
								}
							}
							else{
								amount = delNotModel.getAmount() / delNotModel.getConversionRate();
								UserModel userMdl=new UserManagementDao().getUser(delNotModel.getResponsible_employee());
								if(delNotModel.getCurrencyId() == getCurrencyID()){
									reportBean = new DeliveryNoteReportBean(delNotModel.getDate().toString(),
											userMdl.getFirst_name()+" "+userMdl.getMiddle_name()+" "+userMdl.getLast_name(),
											String.valueOf(delNotModel.getDeliveryNo()),
											delNotModel.getOffice().getName(), items,
											delNotModel.getAmount());
									reportBean.setCurrency(getCurrencyDescription(getCurrencyID()));
								} else {
									reportBean = new DeliveryNoteReportBean(delNotModel.getDate().toString(),
											userMdl.getFirst_name()+" "+userMdl.getMiddle_name()+" "+userMdl.getLast_name(),
											String.valueOf(delNotModel.getDeliveryNo()),
											delNotModel.getOffice().getName(), items,
											roundNumber(amount));
									reportBean.setCurrency(getCurrencyDescription(getCurrencyID())+
											" ("+delNotModel.getAmount()+" "+getCurrencyDescription(delNotModel.getCurrencyId())+")");
								}
							}
							reportList.add(reportBean);
						}
						if (!noData) {
							
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("DeliveryNote_Report");
							report.setReportFileName("Delivery Note Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("delivery_note_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("OFFICE_LABEL", getPropertyName("office"));
							
							if(!settings.isSALES_MAN_WISE_SALES())
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							else
								map.put("CUSTOMER_LABEL", getPropertyName("sales_man"));
							map.put("SALES_LABEL", getPropertyName("sales"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							String subHeader = "";
							if (employId != 0) {
								subHeader += getPropertyName("employee")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (delNotNo != 0) {
								subHeader += getPropertyName("delivery_note_no")+" : "
										+ deliveryNoteNoComboField
												.getItemCaption(deliveryNoteNoComboField
														.getValue());
							}

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
							delNotModelList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			officeComboField.setValue(getOfficeID());

			if (isOrganizationAdmin() || isSuperAdmin() || isSystemAdmin())
				officeComboField.setEnabled(true);
			else
				officeComboField.setEnabled(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}
	
	
	private String getCurrencyDescription(long currencyId) {
		if(currencyHashMap == null){
			currencyHashMap = new HashMap<Long, String>();
			try {
				List list = new CurrencyManagementDao().getCurrencySymbol();
				Iterator<CurrencyModel> itr = list.iterator();
				while(itr.hasNext()){
					CurrencyModel model = itr.next();
					currencyHashMap.put(model.getId(), model.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return currencyHashMap.get(currencyId);
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadBillNo(long employId, long officeId) {
		List<Object> salesList = null;
		try {
			if (employId != 0) {
				if(!settings.isSALES_MAN_WISE_SALES()){
					
					salesList = new DeliveryNoteReportDao().getAllDNNumbersForCustomer(officeId, 
								employId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
					
				}
				else{
					salesList = new DeliveryNoteReportDao().getAllDNNumbersForSalesMan(officeId, 
								employId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
				}
			} else {
				salesList = new DeliveryNoteReportDao().getAllSalesNumbersAsComment(officeId);
			}
			DeliveryNoteModel delNotModel = new DeliveryNoteModel();
			delNotModel.setId(0);
			delNotModel
					.setDeliveryNo(getPropertyName("all"));
			if (salesList == null) {
				salesList = new ArrayList<Object>();
			}
			salesList.add(0, delNotModel);
			container = SCollectionContainer.setList(salesList, "id");
			deliveryNoteNoComboField.setContainerDataSource(container);
			deliveryNoteNoComboField.setItemCaptionPropertyId("deliveryNo");
			deliveryNoteNoComboField.setValue((long)0);
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
