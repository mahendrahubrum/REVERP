package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.ContractorDao;
import com.inventory.config.acct.model.ContractorModel;
import com.inventory.reports.bean.DeliveryNoteReportBean;
import com.inventory.reports.dao.WorkOrderReportDao;
import com.inventory.sales.dao.DeliveryNoteDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.WorkOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.WorkOrderModel;
import com.inventory.sales.ui.WorkOrderUI;
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
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupDateField;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class WorkOrderReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SPopupDateField fromDateField;
	private SPopupDateField toDateField;
	private SComboField contractorComboField;
	private SComboField workorderNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long contractorId;

	private Report report;

	WorkOrderDao woDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_OFFICE = "Office";
	static String TBC_CONTRACTOR = "Contractor";
	static String TBC_AMOUNT = "Amount";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_OFFICE, TBC_CONTRACTOR,TBC_AMOUNT};
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_OFFICE, TBC_CONTRACTOR,TBC_AMOUNT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_OFFICE, String.class, null,getPropertyName("office"), null, Align.LEFT);
		table.addContainerProperty(TBC_CONTRACTOR, String.class, null,getPropertyName("contractor"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
		table.setColumnExpandRatio(TBC_OFFICE, 2);
		table.setColumnExpandRatio(TBC_CONTRACTOR, (float) 2);
		table.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		woDao = new WorkOrderDao();

		contractorId = 0;
		report = new Report(getLoginID());

		setSize(1050, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
//		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SPopupDateField(getPropertyName("from_date"));
		fromDateField.setTextFieldEnabled(false);
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SPopupDateField(getPropertyName("to_date"));
		toDateField.setTextFieldEnabled(false);
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		mainFormLayout.addComponent(dateHorizontalLayout);

		fromDateField.setInvalidAllowed(false);
		toDateField.setInvalidAllowed(false);

		try {
			List contractorsList = new ContractorDao()
					.getAllActiveContractorNamesWithLedgerID(getOfficeID());

			ContractorModel objModel = new ContractorModel();
			objModel.setId(0);
			objModel.setName(getPropertyName("all"));
			if (contractorsList == null) {
				contractorsList = new ArrayList<Object>();
			}
			contractorsList.add(0, objModel);
			contractorComboField = new SComboField(
					getPropertyName("contractor"), 200, contractorsList, "id",
					"name", false, getPropertyName("all"));
			mainFormLayout.addComponent(contractorComboField);

			List<Object> deliveryNoteBillList = new DeliveryNoteDao()
					.getAllDeliveryNoteNumbers(getOfficeID());
			WorkOrderModel woModel = new WorkOrderModel();
			woModel.setId(0);
			woModel.setComments(getPropertyName("all"));
			if (deliveryNoteBillList == null) {
				deliveryNoteBillList = new ArrayList<Object>();
			}
			deliveryNoteBillList.add(0, woModel);

			workorderNoComboField = new SComboField(
					getPropertyName("work_order_no"), 200,
					deliveryNoteBillList, "id", "comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(workorderNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

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
			mainPanel.setContent(mainHorizontal);

			contractorComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							contractorId = 0;
							if (contractorComboField.getValue() != null
									&& !contractorComboField.getValue()
											.toString().equals("0")) {
								contractorId = toLong(contractorComboField
										.getValue().toString());
							}
							loadBillNo(contractorId);
						}
					});

			fromDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(contractorId);
				}
			});

			toDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(contractorId);
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
							WorkOrderUI option=new WorkOrderUI();
							option.setCaption(getPropertyName("work_order"));
							option.getWorkOrderNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
					return new Action[] { actionDelete };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							WorkOrderModel work=new WorkOrderDao().getWorkOrder(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("work_order")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("bill"),work.getWork_order_number()+""));
							form.addComponent(new SLabel(getPropertyName("contractor"),work.getContractor().getName()));
							form.addComponent(new SLabel(getPropertyName("office"),work.getOffice().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(work.getDate())));
							form.addComponent(new SLabel(getPropertyName("net_amount"),work.getAmount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
//							grid.setRows(work.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),	5, 0);
							grid.setSpacing(true);
							
							int i = 1;
							SalesInventoryDetailsModel invObj;
//							Iterator itr = work.getInventory_details_list().iterator();
//							while(itr.hasNext()){
//								invObj=(SalesInventoryDetailsModel)itr.next();
//								grid.addComponent(new SLabel(null, i + ""),	0, i);
//								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
//								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
//								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
//								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
//								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
//																	- invObj.getDiscount_amount() 
//																	+ invObj.getTax_amount())+ ""), 5, i);
//								i++;
//							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), work.getComments()));
							form.setStyleName("grid_max_limit");
							popupContainer.removeAllComponents();
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

					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						boolean noData = true;
						WorkOrderModel woModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						DeliveryNoteReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();

						long woNo = 0;
						long contrID = 0;

						if (workorderNoComboField.getValue() != null && !workorderNoComboField.getValue().equals("") && !workorderNoComboField.getValue().toString().equals("0")) {
							woNo = (Long) workorderNoComboField.getValue();
						}
						if (contractorComboField.getValue() != null && !contractorComboField.getValue().equals("")) {
							contrID = toLong(contractorComboField.getValue().toString());
						}
						List<Object> woModelList = new WorkOrderReportDao()
								.getWODetails(woNo, contrID, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()), getOfficeID());

						List<SalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < woModelList.size(); i++) {
							noData = false;
							woModel = (WorkOrderModel) woModelList.get(i);
//							detailsList = woModel.getInventory_details_list();
							items = "";
//							for (int k = 0; k < detailsList.size(); k++) {
//								inventoryDetailsModel = detailsList.get(k);
//								if (k != 0) {
//									items += " , ";
//								}
//								items += inventoryDetailsModel.getItem()
//										.getName();
//							}
							
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									woModel.getId(),
									woModel.getDate().toString(),
									woModel.getOffice().getName(),
									woModel.getContractor().getName(),
									woModel.getAmount()},table.getItemIds().size()+1);
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

					try {
						boolean noData = true;
						WorkOrderModel woModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						DeliveryNoteReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();

						long woNo = 0;
						long contrID = 0;

						if (workorderNoComboField.getValue() != null
								&& !workorderNoComboField.getValue().equals("")
								&& !workorderNoComboField.getValue().toString()
										.equals("0")) {
							woNo = (Long) workorderNoComboField.getValue();
						}
						if (contractorComboField.getValue() != null
								&& !contractorComboField.getValue().equals("")) {
							contrID = toLong(contractorComboField.getValue()
									.toString());
						}
						List<Object> woModelList = new WorkOrderReportDao()
								.getWODetails(woNo, contrID, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()), getOfficeID());

						List<SalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < woModelList.size(); i++) {
							noData = false;
							woModel = (WorkOrderModel) woModelList.get(i);
//							detailsList = woModel.getInventory_details_list();
							items = "";
//							for (int k = 0; k < detailsList.size(); k++) {
//								inventoryDetailsModel = detailsList.get(k);
//								if (k != 0) {
//									items += " , ";
//								}
//								items += inventoryDetailsModel.getItem()
//										.getName();
//							}

							// UserModel user= new
							// UserManagementDao().getUserFromLogin(woModel.getEmploy().getId());

							reportBean = new DeliveryNoteReportBean(woModel
									.getDate().toString(), woModel
									.getContractor().getName(), String
									.valueOf(woModel.getWork_order_number()),
									woModel.getOffice().getName(), items,
									woModel.getAmount());
							reportList.add(reportBean);

						}

						if (!noData) {
							report.setJrxmlFileName("WorkOrder_Report");
							report.setReportFileName("Work Order Report");
							HashMap<String, Object> map = new HashMap<String, Object>();
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("work_order_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("OFFICE_LABEL", getPropertyName("office"));
							map.put("CONTRACTOR_LABEL", getPropertyName("contractor"));
							map.put("WO_NO_LABEL", getPropertyName("work_order_no"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							String subHeader = "";
							if (contractorId != 0) {
								subHeader += getPropertyName("contractor")+" : "
										+ contractorComboField
												.getItemCaption(contractorComboField
														.getValue()) + "\t";
							}
							if (woNo != 0) {
								subHeader += getPropertyName("work_order_no")+" : "
										+ workorderNoComboField
												.getItemCaption(workorderNoComboField
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
							report.createReport(reportList, map);

							reportList.clear();
							woModelList.clear();

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

	private void loadBillNo(long contractorId) {
		List<Object> workOrdersList = null;
		try {
			if (contractorId != 0) {
				workOrdersList = woDao
						.getAllWorkOrderNumbersOfContractorAsComment(
								contractorId, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));
			} else {
				workOrdersList = woDao
						.getAllWorkOrderNumbersOfContractorAsComment(CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField
										.getValue()), getOfficeID());
			}
			WorkOrderModel woModel = new WorkOrderModel();
			woModel.setId(0);
			woModel.setComments(getPropertyName("all"));
			if (workOrdersList == null) {
				workOrdersList = new ArrayList<Object>();
			}
			workOrdersList.add(0, woModel);
			container = SCollectionContainer.setList(workOrdersList, "id");
			workorderNoComboField.setContainerDataSource(container);
			workorderNoComboField.setItemCaptionPropertyId("comments");
			workorderNoComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * protected void loadCustomerCombo(long officeId) { List<Object> custList =
	 * null; try { if (officeId != 0) { custList = new
	 * LedgerDao().getAllActiveLedgerNames(officeId); } else { custList = new
	 * LedgerDao().getAllActiveLedgerNames(); } LedgerModel ledgerModel = new
	 * LedgerModel(); ledgerModel.setId(0);
	 * ledgerModel.setName("---------------------ALL-------------------"); if
	 * (custList == null) { custList = new ArrayList<Object>(); }
	 * custList.add(0, ledgerModel); custContainer =
	 * SCollectionContainer.setList(custList, "id");
	 * contractorComboField.setContainerDataSource(custContainer);
	 * contractorComboField.setItemCaptionPropertyId("name");
	 * contractorComboField.setValue(0); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
