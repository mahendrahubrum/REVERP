package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.LandedCostReportBean;
import com.inventory.reports.dao.LandedCostReportDao;
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

public class PurchaseLandedCostReportUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String TBC_SN = "#";
	private static String TBC_BILL_NO = "Bill No";
	private static String TBC_DATE = "Date";
	private static String TBC_SUPPLIER = "Supplier";
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
	private SComboField supplierComboField;
	private LedgerDao ledDao;
	private SComboField billNoComboField;
	private PurchaseDao purchaseDao;
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
	private SLabel supplierLabel;
	private SLabel quantityLabel;
	private SLabel unitPriceLabel;
	private SLabel amountLabel;
	private SLabel landedCostLabel;
	private SPopupView popupView;
	private HashMap<Long, String> currencyHashMap;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		ledDao = new LedgerDao();
		purchaseDao = new PurchaseDao();
		landedCostReportDao = new LandedCostReportDao();
		report = new Report(getLoginID());

		setSize(1200, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", false, getPropertyName("select"));

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

		supplierComboField = new SComboField(getPropertyName("supplier"), 200,
				getSupplierList(getOfficeID()), "id", "name", false,
				getPropertyName("all"));
		supplierComboField.setInputPrompt("--------- " + getPropertyName("all")
				+ " ----------");

		billNoComboField = new SComboField(getPropertyName("purchase_no"), 200,
				null, "id", "purchase_no", false,
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
		
		createPopUpView();
		
		table = new STable(null, 900, 250);
		table.setSelectable(true);

		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		table.addContainerProperty(TBC_BILL_NO, String.class, null, getPropertyName("bill_no"), null,
				Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_SUPPLIER, String.class, null,
				getPropertyName("supplier"), null, Align.LEFT);		
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
		table.setColumnExpandRatio(TBC_SUPPLIER, 3f);
		table.setColumnExpandRatio(TBC_AMOUNT, 3);
		table.setColumnExpandRatio(TBC_ITEM, 2f);
		table.setColumnExpandRatio(TBC_QUANTITY, 1f);
		table.setColumnExpandRatio(TBC_UNIT, 0.7f);
		table.setColumnExpandRatio(TBC_UNIT_PRICE, 1.5f);
		table.setColumnExpandRatio(TBC_LANDED_COST, 3f);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(itemComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(supplierComboField);
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
				loadBillNo(offiecId, toLong(supplierComboField.getValue()));
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
				loadBillNo(offiecId, toLong(supplierComboField.getValue()));
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
				loadBillNo(offiecId, toLong(supplierComboField.getValue()));
			}

		});
		
		supplierComboField.addValueChangeListener(new ValueChangeListener() {

			/**
	 * 
	 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				long offiecId = toLong(officeComboField.getValue());
				// loadItemComboField(offiecId);
				// loadSupplierComboField(offiecId);
				loadBillNo(offiecId, toLong(supplierComboField.getValue()));
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
								.getPurchaseLandedCostReportDetails(toLong(officeComboField.getValue()),
										toLong(itemComboField.getValue()),
										toLong(supplierComboField.getValue()),
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
								.getPurchaseLandedCostReportDetails(toLong(officeComboField.getValue()),
										toLong(itemComboField.getValue()),
										toLong(supplierComboField.getValue()),
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
							parameters.put("tag", getPropertyName("supplier"));
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
							report.setReportTitle(getPropertyName("purchase_landed_cost_report"));
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
	
	@Override
	public void valueChange(ValueChangeEvent event) {
		System.out.println("===value === "+table.getValue());
		if(table.getValue() == null){
			return;
		}
		Item item = table.getItem(table.getValue());
		billNoLabel.setValue(item.getItemProperty(TBC_BILL_NO).toString());
		dateLabel.setValue(item.getItemProperty(TBC_DATE).toString());
		supplierLabel.setValue(item.getItemProperty(TBC_SUPPLIER).toString());
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
		supplierLabel = new SLabel(getPropertyName("customer")+" :");
		itemLabel = new SLabel(getPropertyName("item")+" :");
		quantityLabel = new SLabel(getPropertyName("quantity")+" :");
		unitPriceLabel = new SLabel(getPropertyName("unit_price")+" :");
		amountLabel = new SLabel(getPropertyName("amount")+" :");
		landedCostLabel = new SLabel(getPropertyName("landed_cost")+" :");

		popUpFormLayout.addComponent(billNoLabel);
		popUpFormLayout.addComponent(dateLabel);
		popUpFormLayout.addComponent(supplierLabel);
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
		billNoComboField.setItemCaptionPropertyId("purchase_no");
		billNoComboField.setValue((long)0);
	}

	private void loadSupplierComboField(long offiecId) {
		supplierComboField.setContainerDataSource(SCollectionContainer.setList(getSupplierList(offiecId), "id"));
		supplierComboField.setItemCaptionPropertyId("name");
		supplierComboField.setValue(0);
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
				list.addAll(purchaseDao.getAllPurchaseNumbersFromDate(officeId,
						CommonUtil.getSQLDateFromUtilDate(fromDateField
								.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), ""));
			} else {
				list.addAll(purchaseDao.getAllPurchaseNumbersForSupplier(officeId,
						supplierId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), ""));

			}
			
			list.add(0, new PurchaseModel(0, "--------- "+getPropertyName("all")+" ---------"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getSupplierList(long officeId) {
		try {
			List supplierList = ledDao.getAllSuppliers(officeId);
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("------- "+getPropertyName("all")+" ------");
			if (supplierList == null) {
				supplierList = new ArrayList<LedgerModel>();
			}
			supplierList.add(0, ledgerModel);

			return supplierList;
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
