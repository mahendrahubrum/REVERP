package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionPurchaseDetailsModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.reports.bean.CommissionSalesReportBean;
import com.inventory.reports.dao.ContainerReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class ContainerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private ContainerReportDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;
	SupplierDao suplDao;
	OfficeDao ofcDao;
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Received Date";
	static String TBC_CUSTOMER = "Supplier";
	static String TBC_BILL = "Bill No";
	static String TBC_QUANTITY = "Quantity";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEM = "Items";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {
			
			allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DATE,TBC_CUSTOMER,TBC_BILL,TBC_QUANTITY,TBC_AMOUNT,TBC_ITEM};
			visibleColumns = new Object[]{ TBC_SN,TBC_DATE,TBC_CUSTOMER,TBC_BILL,TBC_QUANTITY,TBC_AMOUNT,TBC_ITEM};
			mainHorizontal=new SHorizontalLayout();
			popupContainer = new SHorizontalLayout();
			showButton=new SButton(getPropertyName("show"));
			
			table = new STable(null, 650, 250);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"), null,Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
			table.addContainerProperty(TBC_BILL, Long.class, null, getPropertyName("bill"), null,Align.CENTER);
			table.addContainerProperty(TBC_QUANTITY, String.class, null,getPropertyName("quantity"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.LEFT);
			table.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
			table.setColumnExpandRatio(TBC_ITEM, (float) 2);
			table.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setVisibleColumns(visibleColumns);
			
			suplDao = new SupplierDao();
			ofcDao = new OfficeDao();

			setSize(1000, 350);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");
			ledgertSelect = new SComboField(getPropertyName("supplier"), 200,
					null, "id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("all"));

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			 formLayout.setSpacing(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new ContainerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(),getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);
			mainHorizontal.addComponent(formLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final CloseListener closeListener = new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				showButton.click();
			}
		};

		final Action actionDelete = new Action(getPropertyName("edit"));
		
//		table.addActionHandler(new Handler() {
//			
//			@Override
//			public void handleAction(Action action, Object sender, Object target) {
//				try{
//					Item item = null;
//					if (table.getValue() != null) {
//						item = table.getItem(table.getValue());
//						CommissionPurchaseUI option=new CommissionPurchaseUI();
//						option.setCaption(getPropertyName("commission_purchase"));
//						option.getPurchaseNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
//						option.center();
//						getUI().getCurrent().addWindow(option);
//						option.addCloseListener(closeListener);
//					}
//				}
//				catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			
//			@Override
//			public Action[] getActions(Object target, Object sender) {
//				return new Action[] { actionDelete };
//			}
//		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("commission_purchase")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("supplier"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("bill"),item.getItemProperty(TBC_BILL).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("quantity"),item.getItemProperty(TBC_QUANTITY).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
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
				if (isValid()) {
					generateReport();
				}
			}
		});

		organizationSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(
							ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
									.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				officeSelect.setContainerDataSource(bic);
				officeSelect.setItemCaptionPropertyId("name");

			}
		});

		officeSelect.addListener(new Property.ValueChangeListener() {
			@SuppressWarnings("unchecked")
			public void valueChange(ValueChangeEvent event) {
				SCollectionContainer bic = null;
				try {
					SupplierModel sup=new SupplierModel(0,getPropertyName("all"));
					List ls=new ArrayList();
					ls.add(0,sup);
					ls.addAll(suplDao.getAllActiveSupplierNamesWithLedgerID((Long) officeSelect
							.getValue()));
					bic = SCollectionContainer.setList(ls
							, "id");
				} catch (Exception e) {
					e.printStackTrace();
				}
				ledgertSelect.setContainerDataSource(bic);
				ledgertSelect.setItemCaptionPropertyId("name");

			}
		});

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());
		
		mainPanel.setContent(mainHorizontal);

		return mainPanel;
	}

	@SuppressWarnings("unchecked")
	protected void showReport() {
		try {

			if (isValid()) {
				table.removeAllItems();
				table.setVisibleColumns(allColumns);
				long ledgerId=0;
				if(ledgertSelect.getValue()!=null&&!ledgertSelect.getValue().equals(""))
					ledgerId=(Long)ledgertSelect.getValue();
					
				List reportList = new ArrayList();
				List lst = daoObj.getSalesDetails(ledgerId ,
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue());

				if (lst.size() > 0) {
					Collections.sort(lst,
							new Comparator<CommissionPurchaseModel>() {
								@Override
								public int compare(
										final CommissionPurchaseModel object1,
										final CommissionPurchaseModel object2) {
									return object1.getIssue_date().compareTo(
											object2.getIssue_date());
								}
							});
					CommissionPurchaseModel obj;
					CommissionPurchaseDetailsModel detailsModel;
					Iterator itr = lst.iterator();
					while (itr.hasNext()) {
						obj = (CommissionPurchaseModel) itr.next();
						String items="";
						Iterator  iter=obj.getCommission_purchase_list().iterator();
						while (iter.hasNext()) {
							detailsModel = (CommissionPurchaseDetailsModel) iter.next();
							items+=detailsModel.getItem().getName()+"("+detailsModel.getQunatity()+detailsModel.getUnit().getSymbol()+")";
						}
						table.addItem(new Object[]{
								table.getItemIds().size()+1,
								obj.getId(),
								obj.getReceived_date().toString(),
								obj.getSupplier().getName(),
								obj.getNumber(),
								obj.getQuantity(),
								obj.getAmount(),
								items},table.getItemIds().size()+1);
					}
					
				}
				else{
					SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
				}
				table.setVisibleColumns(visibleColumns);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void generateReport() {
		try {

			if (isValid()) {
				long ledgerId=0;
				if(ledgertSelect.getValue()!=null&&!ledgertSelect.getValue().equals(""))
					ledgerId=(Long)ledgertSelect.getValue();
					
				List reportList = new ArrayList();
				List lst = daoObj.getSalesDetails(ledgerId ,
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue());

				if (lst.size() > 0) {
					Collections.sort(lst,
							new Comparator<CommissionPurchaseModel>() {
								@Override
								public int compare(
										final CommissionPurchaseModel object1,
										final CommissionPurchaseModel object2) {
									return object1.getIssue_date().compareTo(
											object2.getIssue_date());
								}
							});
				}

				CommissionPurchaseModel obj;
				CommissionPurchaseDetailsModel detailsModel;
				Iterator itr = lst.iterator();
				while (itr.hasNext()) {
					obj = (CommissionPurchaseModel) itr.next();
					
					String items="";
					Iterator  iter=obj.getCommission_purchase_list().iterator();
					while (iter.hasNext()) {
						detailsModel = (CommissionPurchaseDetailsModel) iter.next();
						items+=detailsModel.getItem().getName()+"("+detailsModel.getQunatity()+detailsModel.getUnit().getSymbol()+")";
						
						
					}

					reportList.add(new CommissionSalesReportBean(obj
							.getOffice().getName(), obj.getNumber(), obj
							.getSupplier().getName(), new Date(obj
							.getReceived_date().getTime()), new Date(obj
							.getIssue_date().getTime()), obj.getVesel(), obj
							.getContr_no(), obj.getConsignment_mark(), obj
							.getQuantity(), obj.getSs_cc(), obj.getPackages(),
							obj.getQuality(), obj.getReceived_sound(), obj
									.getDamage(), obj.getEmpty(), obj
									.getShorte(),obj.getAmount(),items ));

				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
//					params.put("LedgerName", ledger.getName());
//					params.put("Balance",
//							roundNumber(ledger.getCurrent_balance()));
//					params.put("Office", ledger.getOffice().getName());
//					params.put("Organization", ledger.getOffice()
//							.getOrganization().getName());
					report.setJrxmlFileName("Container_Report");
					report.setReportFileName("Container Report");
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("container_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("RECEIVED_DATE_LABEL", getPropertyName("received_date"));
					params.put("SUPPLIER_LABEL", getPropertyName("supplier"));
					params.put("CONTAINER_NO_LABEL", getPropertyName("container_no"));
					params.put("BILL_NO_LABEL", getPropertyName("bill_no"));
					params.put("QUANTITY_LABEL", getPropertyName("quantity"));
					params.put("PACKAGE_LABEL", getPropertyName("package"));
					params.put("QUALITY_LABEL", getPropertyName("quality"));
					params.put("DAMAGE_LABEL", getPropertyName("damage"));
					params.put("VESSEL_LABEL", getPropertyName("vessel"));
					params.put("CONSIGNMENT_LABEL", getPropertyName("consignment"));
					params.put("SS_CC_LABEL", getPropertyName("ss_cc"));
					params.put("AMOUNT_LABEL", getPropertyName("amount"));
					params.put("ITEM_LABEL", getPropertyName("item"));
					
					
					
					
					report.setReportSubTitle(getPropertyName("from")+" : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ "   "+getPropertyName("to")+"  : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, params);

					reportList.clear();

				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			toDate.focus();
			ret = false;
		} else
			setRequiredError(toDate, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
