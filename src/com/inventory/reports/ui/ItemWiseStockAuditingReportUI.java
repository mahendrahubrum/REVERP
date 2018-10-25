package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.dao.ItemWiseStockAuditingReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Jan 24 2014
 */
public class ItemWiseStockAuditingReportUI extends SparkLogic {

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

	LedgerDao ledDao;

	ItemWiseStockAuditingReportDao daoObj;

	SConfirmWithReview confirmBox;
	ReportReview review;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM = "Item";
	static String TBC_OPENING = "Opening Stock";
	static String TBC_PURCHASE = "Purchase";
	static String TBC_PURCHASE_RETURN = "Purchase Return";
	static String TBC_SALE = "Sales";
	static String TBC_SALE_RETURN = "Sales Return";
	static String TBC_BALANCE = "Balance";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();

		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_OPENING,TBC_PURCHASE,TBC_SALE,TBC_PURCHASE_RETURN,TBC_SALE_RETURN,TBC_BALANCE};
		visibleColumns = new Object[]{ TBC_SN, TBC_ITEM,TBC_OPENING,TBC_PURCHASE,TBC_SALE,TBC_PURCHASE_RETURN,TBC_SALE_RETURN,TBC_BALANCE};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);
		table.addContainerProperty(TBC_OPENING, String.class, null, getPropertyName("opening_stock"), null,Align.CENTER);
		table.addContainerProperty(TBC_PURCHASE, String.class, null,getPropertyName("purchase"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALE, String.class, null,getPropertyName("sales"), null, Align.LEFT);
		table.addContainerProperty(TBC_PURCHASE_RETURN, String.class, null,getPropertyName("purchase_return"), null, Align.LEFT);
		table.addContainerProperty(TBC_SALE_RETURN, String.class, null,getPropertyName("sales_return"), null, Align.LEFT);
		table.addContainerProperty(TBC_BALANCE, String.class, null,getPropertyName("balance"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, (float) 2);
		table.setColumnExpandRatio(TBC_OPENING, (float) 1.5);
		table.setColumnExpandRatio(TBC_PURCHASE, (float) 1.5);
		table.setColumnExpandRatio(TBC_PURCHASE_RETURN, (float) 1.5);
		table.setColumnExpandRatio(TBC_SALE, (float) 1.5);
		table.setColumnExpandRatio(TBC_SALE_RETURN, (float) 1.5);
		table.setColumnExpandRatio(TBC_BALANCE, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		daoObj = new ItemWiseStockAuditingReportDao();
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1050, 350);
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
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
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

			organizationComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
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
					loadItemsCombo(toLong(officeComboField.getValue()
							.toString()));
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
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_stock")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("opening_stock"),item.getItemProperty(TBC_OPENING).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("purchase"),item.getItemProperty(TBC_PURCHASE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("sales"),item.getItemProperty(TBC_SALE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("purchase_return"),item.getItemProperty(TBC_PURCHASE_RETURN).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("sales_return"),item.getItemProperty(TBC_SALE_RETURN).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("balance"),item.getItemProperty(TBC_BALANCE).getValue().toString()));
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

	protected boolean showReport() {
		boolean flag=false;
		table.removeAllItems();
		table.setVisibleColumns(allColumns);
		try {

			List<Object> reportList;

			long itmId = 0;

			if (itemsComboField.getValue() != null
					&& !itemsComboField.getValue().equals("")) {
				itmId = toLong(itemsComboField.getValue()
						.toString());
			}

			reportList = daoObj.getItemWiseStockAuditingReport(
					itmId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue()),
					toLong(officeComboField.getValue().toString()));
			if(reportList.size()>0){
				ReportBean bean=null;
				Iterator itr=reportList.iterator();
				while(itr.hasNext()){
					bean=(ReportBean)itr.next();
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							(long)0,
							bean.getItem_name(),
							bean.getOpening()+" "+bean.getUnit(),
							bean.getPurchaseQty()+" "+bean.getUnit(),
							bean.getSalesQty()+" "+bean.getUnit(),
							bean.getPurchaseRtnQty()+" "+bean.getUnit(),
							bean.getSalesRtnQty()+" "+bean.getUnit(),
							bean.getClosing()+" "+bean.getUnit()},table.getItemIds().size()+1);
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

			reportList = daoObj.getItemWiseStockAuditingReport(
					itmId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue()),
					toLong(officeComboField.getValue().toString()));

			if (reportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				report.setJrxmlFileName("ItemWiseStockAuditing_Report");
				report.setReportFileName("Stock Auditing Report");
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("stock_auditing_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("OPENING_STOCK_LABEL", getPropertyName("opening_stock"));
				map.put("PURCHASE_LABEL", getPropertyName("purchase"));
				map.put("SALES_LABEL", getPropertyName("sales"));
				map.put("SALES_RETURN_LABEL", getPropertyName("sales_return"));
				map.put("PURCHASE_RETURN_LABEL", getPropertyName("purchase_return"));
				map.put("BALANCE_LABEL", getPropertyName("balance"));
				
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		boolean valid=true;
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
