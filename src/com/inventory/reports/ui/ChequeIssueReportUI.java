package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.ChequeIssueBean;
import com.inventory.reports.dao.ChequeIssueReportDao;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 23, 2013
 */
public class ChequeIssueReportUI extends SparkLogic {

	private static final long serialVersionUID = -7132889536978398200L;

	private SOfficeComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
	private SComboField bankComboField;
	private SReportChoiceField reportChoiceField;

	private SButton generateButton;

	private Report report;

	private LedgerDao ledDao;
	private ChequeIssueReportDao dao;

	List<Object> suppliersList;

	CollectionContainer bic;

	private SRadioButton radioButton;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_CDATE = "Cheque Date";
	static String TBC_IDATE = "Issue Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_BANK = "Bank";
	static String TBC_AMOUNT = "Amount";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	private SHorizontalLayout buttonHorizontalLayout;

	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_CDATE,TBC_IDATE,TBC_CUSTOMER,TBC_BANK,TBC_AMOUNT};
		visibleColumns = new Object[]{ TBC_SN, TBC_CDATE,TBC_IDATE,TBC_CUSTOMER,TBC_BANK,TBC_AMOUNT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_CDATE, String.class, null,getPropertyName("cheque_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_IDATE, String.class, null,getPropertyName("issue_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_BANK, String.class, null,getPropertyName("bank"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_BANK, 1);
		table.setColumnExpandRatio(TBC_CDATE, (float) 1);
		table.setColumnExpandRatio(TBC_IDATE, (float) 1);
		table.setColumnExpandRatio(TBC_BANK, (float) 2);
		table.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 2);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		report = new Report(getLoginID());

		setSize(1100, 375);
		SPanel mainPanel = new SPanel();
		mainPanel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout(
				getPropertyName("cheque_date"));
		dateHorizontalLayout.setSpacing(true);

		SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {
			dao = new ChequeIssueReportDao();
			ledDao = new LedgerDao();

			officeComboField = new SOfficeComboField(getPropertyName("office"),
					250);
			fromDateField = new SDateField(getPropertyName("from_date"));
			fromDateField.setValue(getMonthStartDate());
			toDateField = new SDateField(getPropertyName("to_date"));
			toDateField.setValue(getWorkingDate());

			List<Object> supplierList = ledDao.getAllCustomers(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (supplierList == null)
				supplierList = new ArrayList<Object>();

			supplierList.add(0, ledgerModel);
			supplierComboField = new SComboField(getPropertyName("customer"),
					250, supplierList, "id", "name", false, getPropertyName("all"));

			radioButton = new SRadioButton(null, 200, SConstants.clientsList,
					"intKey", "value");
			radioButton.setValue(1);
			radioButton.setStyleName("radio_horizontal");

			List<Object> bankList = null;
//					bankList = 	ledDao.getAllActiveLedgerNamesUnderGroup(
//					SConstants.BANK_ACCOUNT_GROUP_ID, getOfficeID());
			LedgerModel bankMdl = new LedgerModel();
			bankMdl.setId(0);
			bankMdl.setName(getPropertyName("all"));
			if (bankList == null)
				bankList = new ArrayList<Object>();

			bankList.add(0, bankMdl);
			bankComboField = new SComboField(getPropertyName("bank"), 250,
					bankList, "id", "name", false, getPropertyName("all"));

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));

			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(radioButton);
			mainFormLayout.addComponent(supplierComboField);
			mainFormLayout.addComponent(bankComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);
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

			radioButton.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if ((Integer) radioButton.getValue() == 1) {
						supplierComboField.setCaption(getPropertyName("customer"));
						table.setColumnHeader(TBC_CUSTOMER, getPropertyName("customer"));
					} else {
						supplierComboField.setCaption(getPropertyName("supplier"));
						table.setColumnHeader(TBC_CUSTOMER, getPropertyName("supplier"));
					}
					loadSupplierCombo((Integer) radioButton.getValue());
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (officeComboField.getValue() != null) {

						try {

//							suppliersList = ledDao
//									.getAllActiveLedgerNamesUnderGroup(
//											SConstants.BANK_ACCOUNT_GROUP_ID,
//											(Long) officeComboField.getValue());
							LedgerModel ledgerModel = new LedgerModel();
							ledgerModel.setId(0);
							ledgerModel
									.setName(getPropertyName("all"));
							if (suppliersList == null)
								suppliersList = new ArrayList<Object>();
							suppliersList.add(0, ledgerModel);

							bic = CollectionContainer.fromBeans(suppliersList,
									"id");
							bankComboField.setContainerDataSource(bic);
							bankComboField.setItemCaptionPropertyId("name");

							loadSupplierCombo((Integer) radioButton.getValue());

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							SalesModel mdl=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("cheque_issue")+"</u></h2>"));
							form.addComponent(new SLabel(radioButton.getItemCaption(radioButton.getValue()),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("bank"),item.getItemProperty(TBC_BANK).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("cheque_date"),item.getItemProperty(TBC_CDATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("issue_date"),item.getItemProperty(TBC_IDATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
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
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						long supplierId = 0;
						long bankId = 0;

						if (supplierComboField.getValue() != null
								&& !supplierComboField.getValue().equals("")) {
							supplierId = (Long) supplierComboField.getValue();
						}
						if (bankComboField.getValue() != null
								&& !bankComboField.getValue().equals("")) {
							bankId = (Long) bankComboField.getValue();
						}

						int type = 0;
						if ((Integer) radioButton.getValue() == 1)
							type = SConstants.CUSTOMER_PAYMENTS;
						else
							type = SConstants.SUPPLIER_PAYMENTS;

						List reportList = dao.getReportList(CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField
										.getValue()), supplierId, bankId,
								(Long) officeComboField.getValue(), type);
						
						if(reportList.size()>0){
							ChequeIssueBean bean=null;
							Iterator itr=reportList.iterator();
							while (itr.hasNext()) {
								bean = (ChequeIssueBean) itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										bean.getDate(),
										bean.getIssuedDate(),
										bean.getSupplier(),
										bean.getBank(),
										bean.getAmount()},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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

						long supplierId = 0;
						long bankId = 0;

						if (supplierComboField.getValue() != null
								&& !supplierComboField.getValue().equals("")) {
							supplierId = (Long) supplierComboField.getValue();
						}
						if (bankComboField.getValue() != null
								&& !bankComboField.getValue().equals("")) {
							bankId = (Long) bankComboField.getValue();
						}

						int type = 0;
						if ((Integer) radioButton.getValue() == 1)
							type = SConstants.CUSTOMER_PAYMENTS;
						else
							type = SConstants.SUPPLIER_PAYMENTS;

						List reportList = dao.getReportList(CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField
										.getValue()), supplierId, bankId,
								(Long) officeComboField.getValue(), type);

						if (reportList != null && reportList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("ChequeIssueReport");
							report.setReportFileName("ChequeIssueReport");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("cheque_issue_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("CHEQUE_DATE_LABEL", getPropertyName("cheque_date"));
							map.put("ISSUE_DATE_LABEL", getPropertyName("issue_date"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("BANK_LABEL", getPropertyName("bank"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							String subHeader = "";

							subHeader += getPropertyName("from")+" : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t "+getPropertyName("to")+" : "
									+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue());

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);

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

	protected void loadSupplierCombo(Integer type) {
		try {
			List<Object> supplierList = null;
			if (type == 1)
				supplierList = ledDao.getAllCustomers(getOfficeID());
			else
				supplierList = ledDao.getAllSuppliers(getOfficeID());

			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (supplierList == null)
				supplierList = new ArrayList<Object>();

			SCollectionContainer bc = SCollectionContainer.setList(
					supplierList, "id");
			supplierComboField.setContainerDataSource(bc);
			supplierComboField.setItemCaptionPropertyId("name");

			supplierList.add(0, ledgerModel);
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
