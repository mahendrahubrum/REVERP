package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.proposal.dao.CustomerEnquiryDao;
import com.inventory.proposal.dao.SendCustomerProposalDao;
import com.inventory.proposal.dao.SupplierProposalReceiptionDao;
import com.inventory.proposal.dao.SupplierQuotationRequestDao;
import com.inventory.proposal.model.CustomerEnquiryModel;
import com.inventory.proposal.model.ProposalsSentToCustomersModel;
import com.inventory.proposal.model.SupplierProposalReceiptionModel;
import com.inventory.proposal.model.SupplierQuotationRequestModel;
import com.inventory.reports.dao.CustomerLedgerReportDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         May 8, 2014
 */
public class ProposalReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private CustomerLedgerReportDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField enquiryMasterSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_TITLE = "Title";
	static String TBC_DETAILS = "Details";
	static String TBC_NUNBER = "Number";
	static String TBC_ITEM_NAME = "Item";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_EMPLOYEE = "Employee";
	static String TBC_AMOUNT = "Amount";
	static String TBC_TYPE = "Type";
	static String TBC_TYPE_NAME = "Activity";

	SHorizontalLayout mainLay;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;
	CustomerEnquiryDao enqDao;
	SupplierQuotationRequestDao supQtnReqDao;
	SupplierProposalReceiptionDao supPrpRecDao;
	SendCustomerProposalDao sndCustPrpDao;
	UserManagementDao usrDao;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();
			enqDao = new CustomerEnquiryDao();
			supQtnReqDao = new SupplierQuotationRequestDao();
			supPrpRecDao = new SupplierProposalReceiptionDao();
			sndCustPrpDao = new SendCustomerProposalDao();
			usrDao = new UserManagementDao();

			// new Object[] {
			// ct + 1, obj.getId(),obj.getDate(), obj.getNumber(),
			// obj.getTitle(),obj.getDescription(),obj.getItem_name()
			// ,obj.getClient_name(),
			// obj.getEmployee(), obj.getAmount(),obj.getType()
			// }, ct

			allColumns = new String[] { TBC_SN, TBC_ID, TBC_DATE,
					TBC_TYPE_NAME, TBC_NUNBER, TBC_TITLE, TBC_DETAILS,
					TBC_ITEM_NAME, TBC_CUSTOMER, TBC_EMPLOYEE, TBC_AMOUNT,
					TBC_TYPE };
			visibleColumns = new String[] { TBC_SN, TBC_DATE, TBC_TYPE_NAME,
					TBC_NUNBER, TBC_TITLE, TBC_DETAILS, TBC_ITEM_NAME,
					TBC_CUSTOMER, TBC_EMPLOYEE, TBC_AMOUNT };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1180, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_TYPE, Integer.class, null,
					getPropertyName("type"), null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE_NAME, String.class, null,
					getPropertyName("activity"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_TITLE, String.class, null,
					getPropertyName("title"), null, Align.RIGHT);
			table.addContainerProperty(TBC_DETAILS, String.class, null,
					getPropertyName("details"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NUNBER, Long.class, null,
					getPropertyName("number"), null, Align.RIGHT);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.RIGHT);
			table.addContainerProperty(TBC_EMPLOYEE, String.class, null,
					getPropertyName("employee"), null, Align.RIGHT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,
					getPropertyName("amount"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_TITLE, 1);
			table.setColumnExpandRatio(TBC_DETAILS, 1);
			table.setColumnExpandRatio(TBC_NUNBER, 1);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 1);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("800");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200);

			enquiryMasterSelect = new SComboField(getPropertyName("enquiry"),
					200);
			enquiryMasterSelect
					.setInputPrompt("-------------Select-----------");

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new CustomerLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(enquiryMasterSelect);
			// formLayout.addComponent(fromDate);
			// formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						/*
						 * if(table.getValue()!=null) { Item
						 * itm=table.getItem(table.getValue()); long id=(Long)
						 * itm.getItemProperty(TBC_ID).getValue();
						 * if(itm.getItemProperty
						 * (TBC_TYPE).getValue().equals("Sale")) {
						 * 
						 * SalesModel objModel=new SalesDao().getSale(id);
						 * SFormLayout form=new SFormLayout();
						 * form.addComponent(new
						 * SHTMLLabel(null,"<h2><u>Sale</u></h2>"));
						 * form.addComponent(new
						 * SLabel("Sales No. :",objModel.getSales_number()+""));
						 * form.addComponent(new
						 * SLabel("Customer :",objModel.getCustomer
						 * ().getName())); form.addComponent(new
						 * SLabel("Date :",
						 * CommonUtil.getUtilDateFromSQLDate(objModel
						 * .getDate()))); form.addComponent(new
						 * SLabel("Max. Credit Period :"
						 * ,objModel.getCredit_period()+""));
						 * 
						 * if(isShippingChargeEnable()) form.addComponent(new
						 * SLabel
						 * ("Shipping Charge :",objModel.getShipping_charge
						 * ()+""));
						 * 
						 * form.addComponent(new
						 * SLabel("Net Amount :",objModel.getAmount()+""));
						 * form.addComponent(new
						 * SLabel("Paid Amount :",objModel.
						 * getPayment_amount()+""));
						 * 
						 * SGridLayout grid=new SGridLayout("Item Details :");
						 * grid.setColumns(12);
						 * grid.setRows(objModel.getInventory_details_list
						 * ().size()+3);
						 * 
						 * grid.addComponent(new SLabel(null, "#"), 0,0);
						 * grid.addComponent(new SLabel(null, "Item"), 1,0);
						 * grid.addComponent(new SLabel(null, "Qty"), 2,0);
						 * grid.addComponent(new SLabel(null, "Unit"), 3,0);
						 * grid.addComponent(new SLabel(null, "Unit Price"),
						 * 4,0); grid.addComponent(new SLabel(null, "Discount"),
						 * 5,0); grid.addComponent(new SLabel(null, "Amount"),
						 * 6,0); grid.setSpacing(true); int i=1;
						 * SalesInventoryDetailsModel invObj; Iterator
						 * itmItr=objModel
						 * .getInventory_details_list().iterator(); while
						 * (itmItr.hasNext()) {
						 * invObj=(SalesInventoryDetailsModel) itmItr.next();
						 * 
						 * grid.addComponent(new SLabel(null, i+""), 0,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getItem().getName()), 1,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getQunatity()+""), 2,i); grid.addComponent(new
						 * SLabel(null, invObj.getUnit().getSymbol()), 3,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getUnit_price()+""), 4,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getDiscount_amount()+""), 5,i);
						 * grid.addComponent(new SLabel(null,
						 * (invObj.getUnit_price
						 * ()*invObj.getQunatity()-invObj.getDiscount_amount
						 * ()+invObj.getTax_amount())+""), 6,i); i++; }
						 * 
						 * form.addComponent(grid); form.addComponent(new
						 * SLabel("Comment :",objModel.getComments()));
						 * 
						 * form.setStyleName("grid_max_limit");
						 * 
						 * popupContainer.removeAllComponents(); SPopupView
						 * pop=new SPopupView("", form);
						 * popupContainer.addComponent(pop);
						 * pop.setPopupVisible(true);
						 * pop.setHideOnMouseOut(false); } else
						 * if(itm.getItemProperty
						 * (TBC_TYPE).getValue().equals("Commission Sale")) {
						 * CustomerCommissionSalesModel objModel=new
						 * CustomerCommissionSalesDao().getSale(id); SFormLayout
						 * form=new SFormLayout(); form.addComponent(new
						 * SHTMLLabel
						 * (null,"<h2><u>Customer Commission Sales</u></h2>"));
						 * form.addComponent(new
						 * SLabel("Sales No. :",objModel.getSales_no()+""));
						 * form.addComponent(new
						 * SLabel("Date :",CommonUtil.getUtilDateFromSQLDate
						 * (objModel.getDate()))); form.addComponent(new
						 * SLabel("Amount :",objModel.getAmount()+""));
						 * 
						 * SGridLayout grid=new
						 * SGridLayout("Customer Details :");
						 * grid.setColumns(12);
						 * grid.setRows(objModel.getDetails_list().size()+3);
						 * 
						 * grid.addComponent(new SLabel(null, "#"), 0,0);
						 * grid.addComponent(new SLabel(null, "Customer"), 1,0);
						 * grid.addComponent(new SLabel(null, "Qty"), 2,0);
						 * grid.addComponent(new SLabel(null, "Unit"), 3,0);
						 * grid.addComponent(new SLabel(null, "Unit Price"),
						 * 4,0); grid.addComponent(new SLabel(null, "Amount"),
						 * 6,0); grid.setSpacing(true); int i=1;
						 * CommissionSalesCustomerDetailsModel invObj; Iterator
						 * itmItr=objModel.getDetails_list().iterator(); while
						 * (itmItr.hasNext()) {
						 * invObj=(CommissionSalesCustomerDetailsModel)
						 * itmItr.next();
						 * 
						 * grid.addComponent(new SLabel(null, i+""), 0,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getCustomer().getName()), 1,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getQunatity()+""), 2,i); grid.addComponent(new
						 * SLabel(null, invObj.getUnit().getSymbol()), 3,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getUnit_price()+""), 4,i);
						 * grid.addComponent(new SLabel(null,
						 * (invObj.getUnit_price
						 * ()*invObj.getQunatity()-invObj.getDiscount_amount
						 * ()+invObj.getTax_amount())+""), 6,i); i++; }
						 * 
						 * form.addComponent(grid); form.addComponent(new
						 * SLabel("Comment :",objModel.getComments()));
						 * 
						 * form.setStyleName("grid_max_limit");
						 * 
						 * popupContainer.removeAllComponents(); SPopupView
						 * pop=new SPopupView("", form);
						 * popupContainer.addComponent(pop);
						 * pop.setPopupVisible(true);
						 * pop.setHideOnMouseOut(false); } else
						 * if(itm.getItemProperty
						 * (TBC_TYPE).getValue().equals("Receipt")) {
						 * 
						 * PaymentModel objModel= new
						 * PaymentDao().getPaymentModel(id);
						 * 
						 * SFormLayout form=new SFormLayout();
						 * form.addComponent(new
						 * SHTMLLabel(null,"<h2><u>Customer Receipt</u></h2>"));
						 * form.addComponent(new
						 * SLabel("Receipt No. :",objModel.getPayment_id()+""));
						 * LedgerModel
						 * cust=ledDao.getLedgeer(objModel.getFrom_account_id
						 * ()); if(cust!=null) form.addComponent(new
						 * SLabel("Customer :",cust.getName()));
						 * 
						 * LedgerModel
						 * toAcc=ledDao.getLedgeer(objModel.getTo_account_id());
						 * if(toAcc!=null) form.addComponent(new
						 * SLabel("To Account :",toAcc.getName()));
						 * 
						 * form.addComponent(new
						 * SLabel("Date :",CommonUtil.getUtilDateFromSQLDate
						 * (objModel.getDate())));
						 * 
						 * form.addComponent(new
						 * SLabel("Customer Amount :",objModel
						 * .getSupplier_amount()+"")); form.addComponent(new
						 * SLabel("Discount :",objModel.getDiscount()+""));
						 * form.addComponent(new
						 * SLabel("Payment Amount :",objModel
						 * .getPayment_amount()+""));
						 * 
						 * form.addComponent(new
						 * SLabel("Description :",objModel.getDescription()));
						 * 
						 * form.setWidth("400");
						 * 
						 * form.setStyleName("grid_max_limit");
						 * 
						 * popupContainer.removeAllComponents(); SPopupView
						 * pop=new SPopupView("", form);
						 * popupContainer.addComponent(pop);
						 * pop.setPopupVisible(true);
						 * pop.setHideOnMouseOut(false);
						 * 
						 * } else { SalesReturnModel objModel=new
						 * SalesReturnDao().getSalesReturnModel(id);
						 * 
						 * SFormLayout form=new SFormLayout();
						 * form.addComponent(new
						 * SHTMLLabel(null,"<h2><u>Sales Return</u></h2>"));
						 * form.addComponent(new
						 * SLabel("Credit Note No. :",objModel
						 * .getCredit_note_no()+"")); form.addComponent(new
						 * SLabel
						 * ("Customer :",objModel.getCustomer().getName()));
						 * form.addComponent(new
						 * SLabel("Date :",CommonUtil.getUtilDateFromSQLDate
						 * (objModel.getDate())));
						 * 
						 * form.addComponent(new
						 * SLabel("Net Amount :",objModel.getAmount()+""));
						 * form.addComponent(new
						 * SLabel("Paid Amount :",objModel.
						 * getPayment_amount()+""));
						 * 
						 * SGridLayout grid=new SGridLayout("Item Details :");
						 * grid.setColumns(12);
						 * grid.setRows(objModel.getInventory_details_list
						 * ().size()+3);
						 * 
						 * grid.addComponent(new SLabel(null, "#"), 0,0);
						 * grid.addComponent(new SLabel(null, "Item"), 1,0);
						 * grid.addComponent(new SLabel(null, "Qty"), 2,0);
						 * grid.addComponent(new SLabel(null, "Unit"), 3,0);
						 * grid.addComponent(new SLabel(null, "Stock Qty"),
						 * 4,0); grid.addComponent(new SLabel(null,
						 * "Purch. Rtn Qty"), 5,0); grid.addComponent(new
						 * SLabel(null, "Waste Qty"), 6,0);
						 * grid.addComponent(new SLabel(null, "Unit Price"),
						 * 7,0); grid.addComponent(new SLabel(null, "Discount"),
						 * 8, 0); grid.addComponent(new SLabel(null, "Amount"),
						 * 9,0); grid.setSpacing(true);
						 * 
						 * int i=1; SalesReturnInventoryDetailsModel invObj;
						 * Iterator
						 * itmItr=objModel.getInventory_details_list().iterator
						 * (); while (itmItr.hasNext()) {
						 * invObj=(SalesReturnInventoryDetailsModel)
						 * itmItr.next(); grid.addComponent(new SLabel(null,
						 * i+""), 0,i); grid.addComponent(new SLabel(null,
						 * invObj.getItem().getName()), 1,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getStock_quantity()+""), 2,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getUnit().getSymbol()), 3,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getStock_quantity()+""), 4,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getReturned_quantity()+""), 5,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getWaste_quantity()+""), 6,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getUnit_price()+""), 7,i);
						 * grid.addComponent(new SLabel(null,
						 * invObj.getDiscount_amount()+""), 8,i);
						 * grid.addComponent(new SLabel(null,
						 * (invObj.getUnit_price
						 * ()*(invObj.getStock_quantity()+invObj
						 * .getReturned_quantity
						 * ()+invObj.getWaste_quantity())-invObj
						 * .getDiscount_amount()+invObj.getTax_amount())+""),
						 * 9,i); i++; }
						 * 
						 * form.addComponent(grid); form.addComponent(new
						 * SLabel("Comment :",objModel.getComments()));
						 * 
						 * form.setStyleName("grid_max_limit");
						 * 
						 * popupContainer.removeAllComponents(); SPopupView
						 * pop=new SPopupView("", form);
						 * popupContainer.addComponent(pop);
						 * pop.setPopupVisible(true);
						 * pop.setHideOnMouseOut(false);
						 * 
						 * }
						 * 
						 * }
						 */

					} catch (Exception e) {
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
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								enqDao.getAllMasterEnquiries(getOfficeID()),
								"id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					enquiryMasterSelect.setContainerDataSource(bic);
					enquiryMasterSelect.setItemCaptionPropertyId("enquiry");

				}
			});

			enquiryMasterSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
				}
			});

			mainPanel.setContent(mainLay);

			organizationSelect.setValue(getOrganizationID());
			officeSelect.setValue(getOfficeID());
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

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();

			if (isValid()) {

				// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
				// CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
				Iterator itr1, itr2, itr3, itr4;

				List reportList = new ArrayList();

				List lst = enqDao.getAllLatestEnquiriesUnderMaster(
						(Long) enquiryMasterSelect.getValue(), getOfficeID());

				CustomerEnquiryModel custEnqObj;
				SupplierQuotationRequestModel supQtnRqstObj;
				SupplierProposalReceiptionModel supProRecObj;
				ProposalsSentToCustomersModel cusPrpSndObj;
				itr1 = lst.iterator();
				while (itr1.hasNext()) {
					custEnqObj = (CustomerEnquiryModel) itr1.next();

					reportList.add(new ReportBean(custEnqObj.getId(),
							custEnqObj.getDescription(), custEnqObj.getItem()
									.getName(), custEnqObj.getCustomer()
									.getName(), usrDao
									.getUserNameFromLoginID(custEnqObj
											.getResponsible_employee()),
							custEnqObj.getEnquiry(), custEnqObj
									.getBudget_amount(), custEnqObj.getDate(),
							custEnqObj.getStatus(), custEnqObj.getNumber(),
							custEnqObj.getLevel(), 1, "Customer Enquiry"));

					itr2 = supQtnReqDao
							.getAllSupplierQutationFromCustomerEnquiry(
									custEnqObj.getId()).iterator();
					while (itr2.hasNext()) {
						supQtnRqstObj = (SupplierQuotationRequestModel) itr2
								.next();

						reportList.add(new ReportBean(supQtnRqstObj.getId(),
								supQtnRqstObj.getContent(), supQtnRqstObj
										.getEnquiry().getItem().getName(),
								supQtnRqstObj.getEnquiry().getCustomer()
										.getName(), usrDao
										.getUserNameFromLoginID(supQtnRqstObj
												.getSendBy().getId()),
								supQtnRqstObj.getHead(), supQtnRqstObj
										.getBudget_amount(), supQtnRqstObj
										.getDate(), supQtnRqstObj.getStatus(),
								supQtnRqstObj.getId(), 1, 2,
								"Supplier Quotation Request"));

						itr3 = supPrpRecDao
								.getAllSupplierProposalsDetailsFromRequest(
										supQtnRqstObj.getId()).iterator();
						while (itr3.hasNext()) {
							supProRecObj = (SupplierProposalReceiptionModel) itr3
									.next();

							reportList.add(new ReportBean(supProRecObj.getId(),
									supProRecObj.getContent(), supProRecObj
											.getRequest().getEnquiry()
											.getItem().getName(), supProRecObj
											.getRequest().getEnquiry()
											.getCustomer().getName(),
									usrDao.getUserNameFromLoginID(supProRecObj
											.getSendBy().getId()), supProRecObj
											.getHead(), supProRecObj
											.getAmount(), supProRecObj
											.getDate(), supProRecObj
											.getStatus(), supProRecObj
											.getNumber(), 0, 3,
									"Supplier Proposal Receiption"));

							itr4 = sndCustPrpDao
									.getAllCustomersSentProposalsUnderReception(
											supProRecObj.getId()).iterator();
							while (itr4.hasNext()) {

								cusPrpSndObj = (ProposalsSentToCustomersModel) itr4
										.next();

								reportList
										.add(new ReportBean(
												cusPrpSndObj.getId(),
												cusPrpSndObj.getContent(),
												cusPrpSndObj
														.getSupplier_proposal()
														.getRequest()
														.getEnquiry().getItem()
														.getName(),
												cusPrpSndObj.getCustomer()
														.getName(),
												usrDao.getUserNameFromLoginID(cusPrpSndObj
														.getSendBy().getId()),
												cusPrpSndObj.getHead(),
												cusPrpSndObj
														.getSupplier_proposal()
														.getAmount(),
												cusPrpSndObj.getDate(),
												cusPrpSndObj.getStatus(),
												cusPrpSndObj.getNumber(), 0, 4,
												"Send Proposal to Customer"));

							}
						}
					}
				}

				// long number, int level, int type

				table.setVisibleColumns(allColumns);

				int ct = 0;
				ReportBean obj;
				Iterator itr = reportList.iterator();
				while (itr.hasNext()) {
					obj = (ReportBean) itr.next();

					table.addItem(
							new Object[] { ct + 1, obj.getId(), obj.getDt(),
									obj.getActivity(), obj.getNumber(),
									obj.getTitle(), obj.getDescription(),
									obj.getItem_name(), obj.getClient_name(),
									obj.getEmployee(), obj.getAmount(),
									obj.getType() }, ct);
					ct++;

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

				Iterator itr1, itr2, itr3, itr4;

				List reportList = new ArrayList();

				List lst = enqDao.getAllLatestEnquiriesUnderMaster(
						(Long) enquiryMasterSelect.getValue(), getOfficeID());

				CustomerEnquiryModel custEnqObj;
				SupplierQuotationRequestModel supQtnRqstObj;
				SupplierProposalReceiptionModel supProRecObj;
				ProposalsSentToCustomersModel cusPrpSndObj;
				itr1 = lst.iterator();
				while (itr1.hasNext()) {
					custEnqObj = (CustomerEnquiryModel) itr1.next();

					reportList.add(new ReportBean(custEnqObj.getId(),
							custEnqObj.getDescription(), custEnqObj.getItem()
									.getName(), custEnqObj.getCustomer()
									.getName(), usrDao
									.getUserNameFromLoginID(custEnqObj
											.getResponsible_employee()),
							custEnqObj.getEnquiry(), custEnqObj
									.getBudget_amount(), custEnqObj.getDate(),
							custEnqObj.getStatus(), custEnqObj.getNumber(),
							custEnqObj.getLevel(), 1, "Customer Enquiry"));

					itr2 = supQtnReqDao
							.getAllSupplierQutationFromCustomerEnquiry(
									custEnqObj.getId()).iterator();
					while (itr2.hasNext()) {
						supQtnRqstObj = (SupplierQuotationRequestModel) itr2
								.next();

						reportList.add(new ReportBean(supQtnRqstObj.getId(),
								supQtnRqstObj.getContent(), supQtnRqstObj
										.getEnquiry().getItem().getName(),
								supQtnRqstObj.getEnquiry().getCustomer()
										.getName(), usrDao
										.getUserNameFromLoginID(supQtnRqstObj
												.getSendBy().getId()),
								supQtnRqstObj.getHead(), supQtnRqstObj
										.getBudget_amount(), supQtnRqstObj
										.getDate(), supQtnRqstObj.getStatus(),
								supQtnRqstObj.getId(), 1, 2,
								"Supplier Quotation Request"));

						itr3 = supPrpRecDao
								.getAllSupplierProposalsDetailsFromRequest(
										supQtnRqstObj.getId()).iterator();
						while (itr3.hasNext()) {
							supProRecObj = (SupplierProposalReceiptionModel) itr3
									.next();

							reportList.add(new ReportBean(supProRecObj.getId(),
									supProRecObj.getContent(), supProRecObj
											.getRequest().getEnquiry()
											.getItem().getName(), supProRecObj
											.getRequest().getEnquiry()
											.getCustomer().getName(),
									usrDao.getUserNameFromLoginID(supProRecObj
											.getSendBy().getId()), supProRecObj
											.getHead(), supProRecObj
											.getAmount(), supProRecObj
											.getDate(), supProRecObj
											.getStatus(), supProRecObj
											.getNumber(), 0, 3,
									"Supplier Proposal Receiption"));

							itr4 = sndCustPrpDao
									.getAllCustomersSentProposalsUnderReception(
											supProRecObj.getId()).iterator();
							while (itr4.hasNext()) {

								cusPrpSndObj = (ProposalsSentToCustomersModel) itr4
										.next();

								reportList
										.add(new ReportBean(
												cusPrpSndObj.getId(),
												cusPrpSndObj.getContent(),
												cusPrpSndObj
														.getSupplier_proposal()
														.getRequest()
														.getEnquiry().getItem()
														.getName(),
												cusPrpSndObj.getCustomer()
														.getName(),
												usrDao.getUserNameFromLoginID(cusPrpSndObj
														.getSendBy().getId()),
												cusPrpSndObj.getHead(),
												cusPrpSndObj
														.getSupplier_proposal()
														.getAmount(),
												cusPrpSndObj.getDate(),
												cusPrpSndObj.getStatus(),
												cusPrpSndObj.getNumber(), 0, 4,
												"Send Proposal to Customer"));

							}
						}
					}
				}

				if (reportList.size() > 0) {
					report.setJrxmlFileName("Proposal_Report");
					report.setReportFileName("Proposal Report");
					report.setReportTitle("Proposal Report");
					String subHeader = "";

					report.setReportSubTitle(subHeader);

					report.setIncludeHeader(true);
					report.setIncludeFooter(false);
					report.setReportType(toInt(reportType.getValue().toString()));
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, null);

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

		if (enquiryMasterSelect.getValue() == null
				|| enquiryMasterSelect.getValue().equals("")) {
			setRequiredError(enquiryMasterSelect,
					getPropertyName("invalid_selection"), true);
			enquiryMasterSelect.focus();
			ret = false;
		} else
			setRequiredError(enquiryMasterSelect, null, false);

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
