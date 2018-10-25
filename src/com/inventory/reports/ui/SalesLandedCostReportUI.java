package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.LandedCostReportBean;
import com.inventory.reports.dao.LandedCostReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.uac.model.S_OfficeModel;

public class SalesLandedCostReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String TBC_SN = "#";
	private static String TBC_BILL_NO = "Bill No";
	private static String TBC_DATE = "Date";
	private static String TBC_CUSTOMER = "Customer";
	private static String TBC_AMOUNT = "Amount";
	private static String TBC_ITEM = "Item";
	private static String TBC_QUANTITY = "Quantity";
	private static String TBC_UNIT = "Unit";
	private static String TBC_UNIT_PRICE = "Unit Price";
	private static String TBC_LANDED_COST = "Landed Cost";
	
	
	
	
	
	private SPanel mainPanel;
	private SFormLayout mainFormLayout;
	private SComboField officeComboField;
	private OfficeDao officeDao;
	private ItemDao itemDao;
	private SComboField itemComboField;
	private SComboField customerComboField;
	private LedgerDao ledDao;
	private SComboField billNoComboField;
	private SalesDao salesDao;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable table;
	private LandedCostReportDao landedCostReportDao;
	private Report report;
	private SLabel billNoLabel;
	private SLabel dateLabel;
	private SLabel itemLabel;
	private SLabel customerLabel;
	private SLabel quantityLabel;
	private SLabel unitPriceLabel;
	private SLabel amountLabel;
	private SLabel landedCostLabel;
	private SPopupView popupView;
	private HashMap<Long, String> currencyHashMap;

	@Override
	public SPanel getGUI() {
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		ledDao = new LedgerDao();
		salesDao = new SalesDao();
		landedCostReportDao = new LandedCostReportDao();
		report = new Report(getLoginID());
		
		createPopUpView();

		setSize(1200, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("Select"));

		List<ItemModel> list = getItemList(getOfficeID());
		itemComboField = new SComboField(getPropertyName("item"), 125, list,
				"id", "name", false, getPropertyName("all"));
		itemComboField.setInputPrompt(list.get(0).getName());

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		fromDateField.setImmediate(true);

		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		toDateField.setImmediate(true);

		customerComboField = new SComboField(getPropertyName("customer"), 200,
				getCustomerList(getOfficeID()), "id", "name", false,
				getPropertyName("all"));
		customerComboField.setInputPrompt("--------- " + getPropertyName("all")
				+ " ----------");

		billNoComboField = new SComboField(getPropertyName("sales_no"), 200,
				null, "id", "sales_number", false,
				getPropertyName("all"));
		billNoComboField.setInputPrompt("--------- " + getPropertyName("all")
				+ " ----------");

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.SPACEBAR);

		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);

		SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.addComponent(generateButton);
		buttonHorizontalLayout.addComponent(showButton);
		buttonHorizontalLayout.setSpacing(true);
		
		table = new STable(null, 800, 250);
		table.setSelectable(true);

		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		table.addContainerProperty(TBC_BILL_NO, String.class, null, getPropertyName("bill_no"), null,
				Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,
				getPropertyName("customer"), null, Align.LEFT);		
		table.addContainerProperty(TBC_ITEM, String.class, null,
				getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_QUANTITY, Double.class, null,
				getPropertyName("quantity"), null, Align.RIGHT);		
		table.addContainerProperty(TBC_UNIT, String.class, null,
				getPropertyName("unit"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,
				getPropertyName("unit_price"), null, Align.RIGHT);
		table.addContainerProperty(TBC_AMOUNT, String.class, null,
				getPropertyName("amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_LANDED_COST, String.class, null,
				getPropertyName("landed_cost"), null, Align.RIGHT);
		
		

		table.setColumnExpandRatio(TBC_SN, (float) 0.4);
		table.setColumnExpandRatio(TBC_BILL_NO, 1f);
		table.setColumnExpandRatio(TBC_DATE, (float) 2);		
		table.setColumnExpandRatio(TBC_CUSTOMER, 3f);
		table.setColumnExpandRatio(TBC_AMOUNT, 3);
		table.setColumnExpandRatio(TBC_ITEM, 2f);
		table.setColumnExpandRatio(TBC_QUANTITY, 1f);
		table.setColumnExpandRatio(TBC_UNIT, 0.7f);
		table.setColumnExpandRatio(TBC_UNIT_PRICE, 1.5f);
		table.setColumnExpandRatio(TBC_LANDED_COST, 2f);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(itemComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(customerComboField);
		mainFormLayout.addComponent(billNoComboField);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);
		mainFormLayout.addComponent(popupView);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.addComponent(mainFormLayout);
		mainHorizontalLayout.addComponent(table);

		mainPanel.setContent(mainHorizontalLayout);

		officeComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				long offiecId = toLong(officeComboField.getValue());
				loadItemComboField(offiecId);
				loadSupplierComboField(offiecId);
				loadBillNo(offiecId, toLong(customerComboField.getValue()));
			}

		});
		officeComboField.setValue(getOfficeID());
		
		
		fromDateField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				long offiecId = toLong(officeComboField.getValue());
				// loadItemComboField(offiecId);
				// loadSupplierComboField(offiecId);
				loadBillNo(offiecId, toLong(customerComboField.getValue()));
			}

		});

		toDateField.addValueChangeListener(new ValueChangeListener() {

			/**
	 * 
	 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				long offiecId = toLong(officeComboField.getValue());
				// loadItemComboField(offiecId);
				// loadSupplierComboField(offiecId);
				loadBillNo(offiecId, toLong(customerComboField.getValue()));
			}

		});
		
		customerComboField.addValueChangeListener(new ValueChangeListener() {

			/**
	 * 
	 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				long offiecId = toLong(officeComboField.getValue());
				// loadItemComboField(offiecId);
				// loadSupplierComboField(offiecId);
				loadBillNo(offiecId, toLong(customerComboField.getValue()));
			}

		});
		
		showButton.addClickListener(new ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				table.removeAllItems();
				if(isValid()){
					
					try {
						List<LandedCostReportBean> list = landedCostReportDao
								.getSalesLandedCostReportDetails(toLong(officeComboField.getValue()),
										toLong(itemComboField.getValue()),
										toLong(customerComboField.getValue()),
										(Long)billNoComboField.getValue(),
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										getCurrencyDescription(getCurrencyID()));
					//	boolean isDataExist = false;
						for(LandedCostReportBean bean : list){							
							table.addItem(new Object[]{table.getItemIds().size() + 1,
									bean.getBillNo(),
									bean.getDate(),
									bean.getTag(),
									bean.getItem(),
									bean.getQuantity(),
									bean.getUnit(),
									bean.getUnitPrice(),
									bean.getAmount()+" "+bean.getCurrency(),
									bean.getLandedCost()+" "+bean.getCurrency()
							},table.getItemIds().size() + 1);							
						}
						
						if(list.size() <= 0){
							SNotification.show(getPropertyName("no_data_available"), Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
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
				table.removeAllItems();
				if(isValid()){
					
					try {
						List list = landedCostReportDao
								.getSalesLandedCostReportDetails(toLong(officeComboField.getValue()),
										toLong(itemComboField.getValue()),
										toLong(customerComboField.getValue()),
										(Long)billNoComboField.getValue(),
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										getCurrencyDescription(getCurrencyID()));
//					//	boolean isDataExist = false;
//						for(LandedCostReportBean bean : list){							
//							table.addItem(new Object[]{table.getItemIds().size() + 1,
//									bean.getBillNo(),
//									bean.getDate(),
//									bean.getSupplier(),
//									bean.getItem(),
//									bean.getQuantity(),
//									bean.getUnit(),
//									bean.getUnitPrice(),
//									bean.getAmount(),
//									bean.getLandedCost()
//							},table.getItemIds().size() + 1);							
//						}
						
						if(list.size() > 0){
							HashMap<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("tag", getPropertyName("customer"));
							parameters.put("SL_NO_LABEL", getPropertyName("sl_no"));
							parameters.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							parameters.put("DATE_LABEL", getPropertyName("date"));
							parameters.put("ITEM_LABEL", getPropertyName("item"));
							parameters.put("QUANTITY_LABEL", getPropertyName("quantity"));
							parameters.put("UNIT_LABEL", getPropertyName("unit"));
							parameters.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							parameters.put("AMOUNT_LABEL", getPropertyName("amount"));
							parameters.put("LANDED_COST_LABEL", getPropertyName("landed_cost"));
							
							report.setJrxmlFileName("Land_Cost_Report");
							report.setReportFileName("Land_Cost_Report");
							report.setReportTitle(getPropertyName("sales_landed_cost_report"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(list, parameters);
							
						} else {
							SNotification.show(getPropertyName("no_data_available"), Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}

			private String getSubTitle() {
				StringBuffer title = new StringBuffer();
				title.append(""+getPropertyName("from_date")+" : "+CommonUtil.formatDateToDDMMMYYYY(fromDateField.getValue())
						+"\t"+getPropertyName("to_date")+" : "+CommonUtil.formatDateToDDMMMYYYY(toDateField.getValue()));
				return title.toString();
			}
		});

table.addValueChangeListener(new ValueChangeListener() {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void valueChange(ValueChangeEvent event) {
		if(table.getValue() == null){
			return;
		}
		System.out.println("===value === "+table.getValue());
		Item item = table.getItem(table.getValue());
		billNoLabel.setValue(item.getItemProperty(TBC_BILL_NO).toString());
		dateLabel.setValue(item.getItemProperty(TBC_DATE).toString());
		customerLabel.setValue(item.getItemProperty(TBC_CUSTOMER).toString());
		itemLabel.setValue(item.getItemProperty(TBC_ITEM).toString());
		quantityLabel.setValue(item.getItemProperty(TBC_QUANTITY).toString()+" "+
				item.getItemProperty(TBC_UNIT).toString());
		unitPriceLabel.setValue(item.getItemProperty(TBC_UNIT_PRICE).toString());
		amountLabel.setValue(item.getItemProperty(TBC_AMOUNT).toString());
		landedCostLabel.setValue(item.getItemProperty(TBC_LANDED_COST).toString());
		
		
		popupView.setPopupVisible(true);
	}
});

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
	private void createPopUpView() {

		SFormLayout popUpFormLayout = new SFormLayout();
		
		billNoLabel = new SLabel(getPropertyName("bill_no") +" :");		
		dateLabel = new SLabel(getPropertyName("date")+" :");
		customerLabel = new SLabel(getPropertyName("customer")+" :");
		itemLabel = new SLabel(getPropertyName("item")+" :");
		quantityLabel = new SLabel(getPropertyName("quantity")+" :");
		unitPriceLabel = new SLabel(getPropertyName("unit_price")+" :");
		amountLabel = new SLabel(getPropertyName("amount")+" :");
		landedCostLabel = new SLabel(getPropertyName("landed_cost")+" :");

		popUpFormLayout.addComponent(billNoLabel);
		popUpFormLayout.addComponent(dateLabel);
		popUpFormLayout.addComponent(customerLabel);
		popUpFormLayout.addComponent(itemLabel);
		popUpFormLayout.addComponent(quantityLabel);
		popUpFormLayout.addComponent(unitPriceLabel);
		popUpFormLayout.addComponent(amountLabel);
		popUpFormLayout.addComponent(landedCostLabel);		
		
		popupView = new SPopupView(null, popUpFormLayout);
		popupView.setHideOnMouseOut(false);

	}

	private long toLong(Object obj) {
		if(obj == null || obj.toString().trim().equals("")){
			return 0;
		} else {
			return toLong(obj.toString());
		}		
	}

	private void loadBillNo(long officeId, long supplierId) {
		billNoComboField.setContainerDataSource(SCollectionContainer.setList(getBillNoList(officeId, supplierId),"id"));
		billNoComboField.setItemCaptionPropertyId("sales_number");
		billNoComboField.setValue((long)0);
	}

	private void loadSupplierComboField(long offiecId) {
		customerComboField.setContainerDataSource(SCollectionContainer.setList(getCustomerList(offiecId), "id"));
		customerComboField.setItemCaptionPropertyId("name");
		customerComboField.setValue(0);
	}

	private void loadItemComboField(long offiecId) {
		itemComboField.setContainerDataSource(SCollectionContainer.setList(getItemList(offiecId), "id"));
		itemComboField.setItemCaptionPropertyId("name");
		itemComboField.setValue(0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getBillNoList(long officeId, long supplierId) {
		List list = new ArrayList();
		try {
			
			if (supplierId == 0) {
				list.addAll(salesDao.getAllSalesNumbersByDate(officeId,
						CommonUtil.getSQLDateFromUtilDate(fromDateField
								.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), ""));
			} else {
				list.addAll(salesDao.getAllSalesNumbersForCustomer(officeId,supplierId,
						CommonUtil.getSQLDateFromUtilDate(fromDateField
								.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), ""));

			}
			
			list.add(0, new SalesModel(0, "--------- "+getPropertyName("all")+" ---------"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getCustomerList(long officeId) {
		try {
			List customerList = ledDao.getAllCustomers(officeId);
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("--------------"+getPropertyName("all")+"---------");
			if (customerList == null) {
				customerList = new ArrayList<LedgerModel>();
			}
			customerList.add(0, ledgerModel);

			return customerList;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ItemModel> getItemList(long office_id) {
		List<ItemModel> list = new ArrayList<ItemModel>();
		list.add(new ItemModel(0, "---- "+getPropertyName("all")+" -----"));
		try {
			list.addAll(itemDao.getAllItemsWithCode(office_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	private List<S_OfficeModel> getOfficeList() {
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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
