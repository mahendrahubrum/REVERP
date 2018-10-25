package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.reports.dao.CustomerProfitReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesUI;
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
public class ItemWiseProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemsComboField;
	private SComboField itemGroupCombo;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private Report report;

	LedgerDao ledDao;

	CustomerProfitReportDao daoObj;
	
	SConfirmWithReview confirmBox;
	ReportReview review;
	
	static String TBC_SN = "SN";
	static String TBC_SID = "SID";
	static String TBC_CUSTOMER = "Item";
	static String TBC_PAMOUNT = "Purchase Amount";
	static String TBC_SAMOUNT = "Sale Amount";
	static String TBC_LOSS = "Profit";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();

		daoObj = new CustomerProfitReportDao();

		report = new Report(getLoginID());
		allColumns = new Object[] { TBC_SN, TBC_SID,TBC_CUSTOMER,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		visibleColumns = new Object[]{ TBC_SN, TBC_CUSTOMER,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_SAMOUNT, Double.class, null,getPropertyName("sale_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_PAMOUNT, Double.class, null,getPropertyName("purchase_amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_LOSS, Double.class, null,getPropertyName("profit"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 2);
		table.setColumnExpandRatio(TBC_SAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_LOSS, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());

		setSize(1050, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

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
			
			
			itemGroupCombo = new SComboField(getPropertyName("item_group"), 200, null, "id", "name", true, getPropertyName("all"));

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(itemGroupCombo);
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
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
			
			review.addComponent(mainHorizontal, "left: 0px; right: 0px; z-index:-1;");
			
			mainPanel.setContent(review);
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
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


			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadItemGroupCombo(toLong(officeComboField.getValue()
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
			
			itemGroupCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadItemsCombo(toLong(officeComboField.getValue()
							.toString()),toLong(itemGroupCombo.getValue().toString()));
				}
			});
			itemGroupCombo.setValue((long)0);

			loadItemsCombo(toLong(officeComboField.getValue()
					.toString()),toLong(itemGroupCombo.getValue().toString()));

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};
			
			final Action actionSales = new Action(getPropertyName("edit"));
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SalesUI option=new SalesUI();
							option.setCaption(getPropertyName("sales"));
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_SID).getValue());
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
					return new Action[] { actionSales };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_SID).getValue();
							SalesModel mdl=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),mdl.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("sale_amount"),item.getItemProperty(TBC_SAMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("purchase_amount"),item.getItemProperty(TBC_PAMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("profit"),item.getItemProperty(TBC_LOSS).getValue().toString()));
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
					showReport();
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

		try {
			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			List<Object> reportList;

			long itmId = 0;

			if (itemsComboField.getValue() != null
					&& !itemsComboField.getValue().equals("")) {
				itmId = toLong(itemsComboField.getValue()
						.toString());
			}

			reportList = daoObj.getItemWiseProfitReport(itmId,
					CommonUtil.getSQLDateFromUtilDate(fromDateField
							.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue()),
					toLong(officeComboField.getValue().toString()),
					toLong(itemGroupCombo.getValue().toString()));
			if(reportList.size()>0){
				ReportBean bean=null;
				Iterator itr=reportList.iterator();
				while(itr.hasNext()){
					bean=(ReportBean)itr.next();
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							bean.getId(),
							bean.getClient_name(),
							bean.getInwards(),
							bean.getOutwards(),
							bean.getProfit()},table.getItemIds().size()+1);
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

			reportList = daoObj.getItemWiseProfitReport(itmId,
					CommonUtil.getSQLDateFromUtilDate(fromDateField
							.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue()),
					toLong(officeComboField.getValue().toString()),
					toLong(itemGroupCombo.getValue().toString()));

			if (reportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				report.setJrxmlFileName("ItemWiseProfit_Report");
				report.setReportFileName("Item Wise Profit Report");
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_profit_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("SALE_AMOUNT_LABEL", getPropertyName("sale_amount"));
				map.put("PURCHASE_AMOUNT_LABEL", getPropertyName("purchase_amount"));
				map.put("PROFIT_LABEL", getPropertyName("profit"));
				map.put("TOTAL_LABEL", getPropertyName("total"));
				

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

	protected void loadItemsCombo(long officeId,long groupId) {
		List itemsList = null;
		try {
			itemsList = new ItemDao()
						.getAllActiveItemsWithAppendingItemCodeUnderGroup(officeId,groupId);

			ItemModel salesModel = new ItemModel(0,getPropertyName("all"));
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
	protected void loadItemGroupCombo(long officeId) {
		List itemGroupList = null;
		try {
			itemGroupList = new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID());
			itemGroupList.add(0, new ItemGroupModel((long)0, getPropertyName("all")));
			
			SCollectionContainer bic1 = SCollectionContainer.setList(itemGroupList,
					"id");
			itemGroupCombo.setContainerDataSource(bic1);
			itemGroupCombo.setItemCaptionPropertyId("name");
			itemGroupCombo.setValue((long)0);
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
