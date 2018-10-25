package com.inventory.subscription.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.dao.PrivilageSetupDao;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.ui.SalesCustomerPanel;
import com.inventory.subscription.dao.RentalTransactionNewDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.RentalTransactionDetailsModel;
import com.inventory.subscription.model.RentalTransactionModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.CurrencyRateDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/****
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 24, 2015
 */

public class RentalTransactionNewUI extends SparkLogic {

	private static final long serialVersionUID = -5415935778881929746L;

	static String TBC_SN = "SN";
	static String TBC_RENTAL_ID = "item_id";
	static String TBC_RENTAL_NAME = "Rental";
	static String TBC_QTY = "Quantity";
	static String TBC_LOCATION = "Location";
	static String TBC_MILAGE = "Milage";
	static String TBC_DESCRIPTION = "Description";
	static String TBC_UNIT_PRICE = "Rate";
	static String TBC_TAX_ID = "tax_id";
	static String TBC_TAX_PERC = "Tax %";
	static String TBC_TAX_AMT = "Tax Amount";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_DISCOUNT = "Discount";
	static String TBC_CESS_AMT = "Cess";
	static String TBC_NET_TOTAL = "Net Total";
	static String TBC_NET_FINAL = "Final Amount";

	RentalTransactionNewDao dao;
	CommonMethodsDao comDao;
	SRadioButton rentRadio;
	private SComboField salesNumberList;
	
	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	STextField locationTextField;
	STextField milageTextField;

	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField netPriceTextField;

	STextField payingAmountTextField;
	STextField creditPeriodTextField;

	SComboField salesManSelect;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSalesButton;
	SButton updateSalesButton;
	SButton deleteSalesButton;
	SButton cancelSalesButton;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SComboField customerSelect;
	SDateField date;
	SDateField expiry_date;
	SDateField manufaturing_date;
	SComboField itemSelectCombo;

	SComboField staffSelect;

	STextField grandTotalAmtTextField;
	STextField shippingChargeTextField;
	STextField exciseDutyTextField;
	STextArea comment;
	STextField description;

	SRadioButton cashOrCreditRadio;

	SettingsValuePojo settings;

	WrappedSession session;

	boolean taxEnable = isTaxEnable();

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	private SButton printButton;

	long status, sales_number = 0;


	private SDialogBox newCustomerWindow;
	private SalesCustomerPanel salesCustomerPanel;

	SNativeSelect salesTypeSelect;


	SHorizontalLayout hrz1;

	SButton newSaleButton;

	UserManagementDao usrDao;
	CustomerDao custDao;
	TaxDao taxDao;
	ItemDao itmDao;
	GradeDao gradeDao;

	private STextField refNoField;

	private SNativeSelect salesLocalTypeField;
	private SNativeSelect currencyNativeSelect;
	private STextField foreignCurrField;
	private CurrencyRateDao rateDao;
	
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings({ "deprecation", "serial" })
	@Override
	public SPanel getGUI() {

		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		dao=new RentalTransactionNewDao();
		comDao = new CommonMethodsDao();
		custDao = new CustomerDao();
		taxDao = new TaxDao();
		itmDao = new ItemDao();
		usrDao = new UserManagementDao();
		taxEnable = isTaxEnable();
		gradeDao = new GradeDao();
		rateDao = new CurrencyRateDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Sale");

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		

		allHeaders = new Object[] { TBC_SN, TBC_RENTAL_ID,TBC_RENTAL_NAME,TBC_DESCRIPTION, TBC_QTY,TBC_LOCATION,TBC_MILAGE, TBC_UNIT_PRICE,
									TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT, TBC_NET_PRICE, TBC_CESS_AMT,
									TBC_NET_TOTAL, TBC_NET_FINAL};

		if (taxEnable) {
			if (isCessEnable()) {
				requiredHeaders =new Object[] { TBC_SN, TBC_RENTAL_NAME,TBC_DESCRIPTION, TBC_UNIT_PRICE, TBC_QTY,TBC_LOCATION,TBC_MILAGE,
												TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT, TBC_CESS_AMT,
												TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL };
			}
			else {
				requiredHeaders = new Object[] { TBC_SN, TBC_RENTAL_NAME,TBC_DESCRIPTION, TBC_UNIT_PRICE, TBC_QTY,TBC_LOCATION,TBC_MILAGE,
												TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT,TBC_NET_TOTAL, TBC_DISCOUNT,
												TBC_NET_FINAL };
			}
		} 
		else {
			requiredHeaders = new Object[] { TBC_SN, TBC_RENTAL_NAME,TBC_DESCRIPTION, TBC_UNIT_PRICE, TBC_QTY,TBC_LOCATION,TBC_MILAGE,
					TBC_NET_PRICE, TBC_DISCOUNT, TBC_NET_FINAL };
		}

		List<Object> templist = new ArrayList<Object>();
		Collections.addAll(templist, requiredHeaders);

		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}
		
		boolean avail=true;
		if(!isSuperAdmin()){
			try {
				avail = new PrivilageSetupDao().isFacilityAccessibleToUser(
						getOfficeID(),
						SConstants.privilegeTypes.SALES_ADMIN, getLoginID());
			} catch (Exception e) {
				avail=false;
			}
		}
		requiredHeaders = templist.toArray(new Object[templist.size()]);

		setSize(1300, 605);

		payingAmountTextField = new STextField(null, 100);
		payingAmountTextField.setValue("0.00");
		payingAmountTextField.setStyleName("textfield_align_right");

		creditPeriodTextField = new STextField(null, 100);
		creditPeriodTextField.setValue("0");
		
		
		rentRadio = new SRadioButton(null, 200,SConstants.specialRentalTypeList, "key", "value");
		rentRadio.setHorizontal(true);
		rentRadio.setValue((long)2);
		
		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(16);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(3);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(9);
		bottomGrid.setRows(4);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(8);
		buttonsGrid.setRows(1);

		qtyTotal = new SLabel(null);
		taxTotal = new SLabel(null);
		netTotal = new SLabel(null);
		qtyTotal.setValue("0.0");
		taxTotal.setValue("0.0");
		netTotal.setValue("0.0");

		pannel.setSizeFull();
		form.setSizeFull();

		try {
			staffSelect = new SComboField(null,125,
							usrDao.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(getOfficeID(), getOrganizationID()), 
							"id","first_name",true,getPropertyName("select"));
			
			salesManSelect = new SComboField(null, 125,new SalesManMapDao().getUsers(getOfficeID(),SConstants.SALES_MAN), "id", "first_name");
			staffSelect.setValue(getLoginID());

			if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin())
				staffSelect.setReadOnly(true);

			salesNumberList = new SComboField(null, 175, null, "id","comments", false, getPropertyName("create_new"));
			
			date = new SDateField(null, 120, getDateFormat(), getWorkingDate());

			customerSelect = new SComboField(null,200,null,"id", "name", true, getPropertyName("select"));
			
			loadSubscriberIncome(0);
			
			salesTypeSelect = new SNativeSelect(null, 120,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");

			Iterator itt = salesTypeSelect.getItemIds().iterator();
			if (itt.hasNext())
				salesTypeSelect.setValue(itt.next());

			cashOrCreditRadio = new SRadioButton(null, 150,
					SConstants.paymentModeList, "key", "value");
			cashOrCreditRadio.setStyleName("radio_horizontal");
			rentRadio.setStyleName("radio_horizontal");
			
			salesLocalTypeField = new SNativeSelect(null, 80,
					SConstants.local_foreign_type.local_foreign_type, "intKey",
					"value");
			salesLocalTypeField.setValue(SConstants.local_foreign_type.LOCAL);

			SHorizontalLayout hrl3 = new SHorizontalLayout();
			hrl3.setSpacing(true);
			hrl3.addComponent(rentRadio);
			
//			if (settings.isLOCAL_FOREIGN_TYPE_ENABLED()) {
//				hrl3.addComponent(new SLabel("Type"));
//				hrl3.addComponent(salesLocalTypeField);
//			}

			refNoField = new STextField(null, 120);
			refNoField.setValue("0");

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesNumberList);
			salLisrLay.addComponent(newSaleButton);
		
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);

			masterDetailsGrid.addComponent(hrl3, 3, 0);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
			// masterDetailsGrid.setComponentAlignment(netTotal,
			// Alignment.MIDDLE_RIGHT);

			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("customer")), 1, 1);

			SHorizontalLayout hrl = new SHorizontalLayout();
			hrl.addComponent(customerSelect);
			masterDetailsGrid.addComponent(hrl, 2, 1);

//			SHorizontalLayout horr = new SHorizontalLayout();
//			horr.addComponent(new SLabel(getPropertyName("billing_staff")));
//			horr.addComponent(staffSelect);
//			horr.setSpacing(true);
//			masterDetailsGrid.addComponent(horr, 3, 1);
			
			SHorizontalLayout horr = new SHorizontalLayout();
			horr.addComponent(new SLabel(getPropertyName("sales_man")));
			horr.addComponent(salesManSelect);
			horr.setSpacing(true);
			masterDetailsGrid.addComponent(horr, 3, 1);

//			SHorizontalLayout resp = new SHorizontalLayout();
//			resp.addComponent(new SLabel(getPropertyName("sales_man")));
//			resp.addComponent(salesManSelect);
//			resp.setSpacing(true);
//			masterDetailsGrid.addComponent(resp, 3, 2);

//			masterDetailsGrid.addComponent(new SLabel(getPropertyName("sales_type")), 6, 1);
//			masterDetailsGrid.addComponent(salesTypeSelect, 8, 1);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("max_credit_period")), 0, 2);
			masterDetailsGrid.addComponent(creditPeriodTextField, 2, 2);

			masterDetailsGrid.setStyleName("master_border");

			quantityTextField = new STextField(getPropertyName("quantity"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			
			locationTextField = new STextField(getPropertyName("location"), 60);
			
			milageTextField = new STextField(getPropertyName("kilo_meter"), 60);
			milageTextField.setStyleName("textfield_align_right");

			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setNewValue("0.00");
			unitPriceTextField.setStyleName("textfield_align_right");

			if (!settings.isSALE_PRICE_EDITABLE()) {
				unitPriceTextField.setReadOnly(true);
			}

			if (taxEnable) {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80,taxDao.getAllActiveTaxesFromType(getOfficeID(),SConstants.tax.SALES_TAX), "id", "name");
				taxSelect.setVisible(true);
			} else {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80, null,"id", "name");
				taxSelect.setVisible(false);
			}

			discountTextField = new STextField(getPropertyName("discount"), 80,
					"0.0");
			discountTextField.setStyleName("textfield_align_right");

			netPriceTextField = new STextField(getPropertyName("net_price"),
					100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("textfield_align_right");
			
			expiry_date = new SDateField(getPropertyName("exp_date"), 100,"dd/MMM/yyyy", new Date());
			manufaturing_date = new SDateField(getPropertyName("mfg_date"),100, "dd/MMM/yyyy", new Date());
			
			itemSelectCombo = new SComboField(getPropertyName("rental_item"), 150,new SubscriptionCreationDao().getAllSubscriptions(getOfficeID(), (long)0 ),
					"id","name", true, getPropertyName("select"));
			description= new STextField(getPropertyName("description"), 130);
			description.setMaxLength(300);

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SHorizontalLayout buttonLay = new SHorizontalLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			hrz1 = new SHorizontalLayout();
			hrz1.addComponent(itemSelectCombo);


			addingGrid.addComponent(hrz1);
			addingGrid.addComponent(unitPriceTextField);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(locationTextField);
			addingGrid.addComponent(milageTextField);
//			addingGrid.addComponent(discountTextField);
			addingGrid.addComponent(description);
			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 1.5f);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(7, 1);
			addingGrid.setColumnExpandRatio(8, 2);
			addingGrid.setColumnExpandRatio(9, 2);

			addingGrid.setWidth("1230");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1000, 200);

			table.setMultiSelect(false);
			table.setSelectable(true);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_RENTAL_ID, Long.class, null,TBC_RENTAL_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_RENTAL_NAME, String.class, null,getPropertyName("rental"), null, Align.LEFT);
			table.addContainerProperty(TBC_DESCRIPTION, String.class, null,getPropertyName("description"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_LOCATION, String.class, null,getPropertyName("location"), null, Align.CENTER);
			table.addContainerProperty(TBC_MILAGE, Double.class, null,getPropertyName("milage"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null,TBC_TAX_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TAX_PERC, Double.class, null,getPropertyName("tax_percentage"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_AMT, Double.class, null,getPropertyName("tax_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null,getPropertyName("discount"), null, Align.CENTER);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null,getPropertyName("net_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CESS_AMT, Double.class, null,getPropertyName("cess"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_TOTAL, Double.class, null,getPropertyName("net_total"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_FINAL, Double.class, null,getPropertyName("final_amount"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_RENTAL_NAME, 1.5f);
			table.setColumnExpandRatio(TBC_DESCRIPTION, 2f);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_LOCATION, 1);
			table.setColumnExpandRatio(TBC_MILAGE, 1);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, (float) 1.3);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, (float) 1);
			table.setColumnExpandRatio(TBC_NET_TOTAL,(float) 1);
			table.setColumnExpandRatio(TBC_NET_FINAL, (float) 1);
			table.setColumnExpandRatio(TBC_CESS_AMT, (float) 0.6);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);
			// table.setEditable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_RENTAL_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_DISCOUNT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1230");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			currencyNativeSelect = new SNativeSelect(null, 80,
					new CurrencyManagementDao().getCurrencyCode(), "id", "name");
			currencyNativeSelect.setValue(getCurrencyID());
			foreignCurrField = new STextField(null, 100);
			foreignCurrField.setValue("0.0");
			foreignCurrField.setReadOnly(true);

			grandTotalAmtTextField = new STextField(null, 120, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			// grandTotalAmtTextField.setStyleName("textfield_align_right");

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

			comment = new STextArea(null, 250, 40);

			bottomGrid.addComponent(new SLabel(""), 1, 0);

			if (!isDiscountEnable()) {
				discountTextField.setVisible(false);
			}
			if (isExciceDutyEnable()) {
				bottomGrid.addComponent(new SLabel(
						getPropertyName("excise_duty")), 4, 2);
				bottomGrid.addComponent(exciseDutyTextField, 5, 2);
				bottomGrid.setComponentAlignment(exciseDutyTextField,
						Alignment.TOP_RIGHT);

			}
			if (isShippingChargeEnable()) {
				bottomGrid.addComponent(new SLabel(
						getPropertyName("shipping_charge")), 4, 1);
				bottomGrid.addComponent(shippingChargeTextField, 5, 1);
				bottomGrid.setComponentAlignment(shippingChargeTextField,
						Alignment.TOP_RIGHT);

			}

			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.addComponent(
					new SLabel(getPropertyName("paying_amount")), 1, 3);
			bottomGrid.addComponent(payingAmountTextField, 3, 3);

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),
					4, 3);
			bottomGrid.addComponent(grandTotalAmtTextField, 5, 3);
			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			if (settings.isMULTIPLE_CURRENCY_ENABLED()) {
				bottomGrid.addComponent(
						new SLabel(getPropertyName("amount_in")), 6, 3);
				bottomGrid.addComponent(currencyNativeSelect, 7, 3);
				bottomGrid.addComponent(foreignCurrField, 8, 3);
			}

			saveSalesButton = new SButton(getPropertyName("save"), 70);
			saveSalesButton.setStyleName("savebtnStyle");
			saveSalesButton
					.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateSalesButton = new SButton(getPropertyName("update"), 80);
			updateSalesButton.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updateSalesButton.setStyleName("updatebtnStyle");

			deleteSalesButton = new SButton(getPropertyName("delete"), 78);
			deleteSalesButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteSalesButton.setStyleName("deletebtnStyle");

			cancelSalesButton = new SButton(getPropertyName("cancel"), 78);
			cancelSalesButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			cancelSalesButton.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveSalesButton);
			mainButtonLayout.addComponent(updateSalesButton);
			if (!settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(deleteSalesButton);
			else
				mainButtonLayout.addComponent(cancelSalesButton);
			updateSalesButton.setVisible(false);
			deleteSalesButton.setVisible(false);
			cancelSalesButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			// Added by anil
			printButton = new SButton(getPropertyName("print"));
			printButton.setIcon(new ThemeResource("icons/print.png"));
			mainButtonLayout.addComponent(printButton);
			printButton.setVisible(false);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);
			
			form.setWidth("700");
			
			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			
			pannel.setContent(windowNotif);
			
			Object a = table.getValue();
			newCustomerWindow = new SDialogBox(getPropertyName("add_customer"),700, 600);
			newCustomerWindow.center();
			newCustomerWindow.setResizable(false);
			newCustomerWindow.setModal(true);
			newCustomerWindow.setCloseShortcut(KeyCode.ESCAPE);
			salesCustomerPanel = new SalesCustomerPanel();
			newCustomerWindow.addComponent(salesCustomerPanel);

			loadSale(0);

			Iterator itr = cashOrCreditRadio.getItemIds().iterator();
			itr.next();
			cashOrCreditRadio.setValue(itr.next());

			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)salesNumberList.getValue(),confirmBox.getUserID());
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					confirmBox.close();
				}
			};
			
			confirmBox.setClickListener(confirmListener);
			
			ClickListener clickListnr=new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
						if(salesNumberList.getValue()!=null && !salesNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)salesNumberList.getValue(),
									"Sales : No. "+salesNumberList.getItemCaption(salesNumberList.getValue()));
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						}
						else
							Notification.show("Select an Invoice..!",
									"Select an Invoice for save in session",
									Type.HUMANIZED_MESSAGE);
					}
					else if(event.getButton().getId().equals(windowNotif.REPORT_ISSUE)) {
						if(salesNumberList.getValue()!=null && !salesNumberList.getValue().toString().equals("0")) {
							confirmBox.open();
						}
						else
							Notification.show("Select an Invoice..!", "Select an Invoice for Save in session",
									Type.HUMANIZED_MESSAGE);
					}
					else {
						try {
							helpPopup=new SHelpPopupView(getOptionId());
							popupLay.removeAllComponents();
							popupLay.addComponent(helpPopup);
							helpPopup.setPopupVisible(true);
							helpPopup.setHideOnMouseOut(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			};
			
			windowNotif.setClickListener(clickListnr);
			
			rentRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(toLong(rentRadio.getValue().toString())==1){
							loadSubscriberTransportation(0);
						}
						else{
							loadSubscriberIncome(0);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesNumberList.setValue((long)0);
				}
			});

			customerSelect.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

							try {
								
								if (customerSelect.getValue() != null
										&& !customerSelect.getValue()
												.equals("")) {
									CustomerModel cust = custDao
											.getCustomerFromLedger((Long) customerSelect
													.getValue());
									if (cust != null) {
										salesManSelect.setValue(cust
												.getResponsible_person());
										creditPeriodTextField.setValue(asString(cust
												.getMax_credit_period()));
										customerSelect
												.setDescription("<h1><i>Current Balance</i> : "
														+ roundNumber(cust
																.getLedger()
																.getCurrent_balance())
														+ "</h1>");
										salesTypeSelect.setValue(cust
												.getSales_type());

										if (settings
												.isDISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT()) {
											customerSelect
													.setDescription("<h1><i>Current Balance</i> : "
															+ roundNumber(cust
																	.getLedger()
																	.getCurrent_balance())
															+ "</h1><br>"
															+ "<h2><i>Credit Limit</i> : "
															+ cust.getCredit_limit()
															+ "</h2>");
											if ((salesNumberList.getValue() == null
													|| salesNumberList
															.getValue().equals(
																	"") || salesNumberList
													.getValue().toString()
													.equals("0"))
													&& (cust.getCredit_limit() < cust
															.getLedger()
															.getCurrent_balance())) {
												buttonsGrid.setVisible(false);
												SNotification
														.show(getPropertyName("limit_excess"),
																Type.ERROR_MESSAGE);
											} else {
												buttonsGrid.setVisible(true);
											}

										}
									}
								} else {
									customerSelect.setDescription(null);
									Iterator itt = salesTypeSelect.getItemIds()
											.iterator();
									if (itt.hasNext())
										salesTypeSelect.setValue(itt.next());
								}

							} 
							catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
							}
						}
			});

			saveSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					try{
						if(isValid()){
							if(salesNumberList.getValue()==null || salesNumberList.getValue().toString().equals("0")){
								RentalTransactionModel mdl=new RentalTransactionModel();
								long customer_id=(Long)customerSelect.getValue();
								grandTotalAmtTextField.setReadOnly(false);
								List<RentalTransactionDetailsModel> itemsList = new ArrayList<RentalTransactionDetailsModel>();
								Iterator itr=table.getItemIds().iterator();
								while (itr.hasNext()) {
									RentalTransactionDetailsModel det=new RentalTransactionDetailsModel();
									Item item=table.getItem(itr.next());
									det.setRental(new SubscriptionCreationModel((Long)item.getItemProperty(TBC_RENTAL_ID).getValue()));
									det.setDescription(item.getItemProperty(TBC_DESCRIPTION).getValue().toString());
									det.setQunatity((Double)item.getItemProperty(TBC_QTY).getValue());
									det.setUnit_price((Double)item.getItemProperty(TBC_UNIT_PRICE).getValue());
									
									det.setTax(new TaxModel((Long)item.getItemProperty(TBC_TAX_ID).getValue()));
									det.setTax_amount((Double)item.getItemProperty(TBC_TAX_AMT).getValue());
									det.setTax_percentage((Double)item.getItemProperty(TBC_TAX_PERC).getValue());
									det.setDiscount_amount((Double)item.getItemProperty(TBC_DISCOUNT).getValue());
									det.setCess_amount((Double)item.getItemProperty(TBC_CESS_AMT).getValue());
									det.setLocation(item.getItemProperty(TBC_LOCATION).getValue().toString());
									det.setMilage((Double)item.getItemProperty(TBC_MILAGE).getValue());
									itemsList.add(det);
								}
								mdl.setInventory_details_list(itemsList);
								mdl.setSales_number(getNextSequence("Rental Transaction", getLoginID()));
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
								mdl.setCreated_time(CommonUtil.getCurrentDateTime());
								mdl.setResponsible_person((Long) salesManSelect.getValue());
								mdl.setSales_person((Long) salesManSelect.getValue());
								mdl.setCustomer(new LedgerModel((Long)customerSelect.getValue()));
								mdl.setShipping_charge(toDouble(shippingChargeTextField.getValue().toString()));
								mdl.setExcise_duty(toDouble(exciseDutyTextField.getValue().toString()));
								mdl.setPayment_amount(toDouble(payingAmountTextField.getValue().toString()));
								mdl.setAmount(toDouble(grandTotalAmtTextField.getValue().toString()));
								mdl.setActive(true);
								mdl.setRent_type(toLong(rentRadio.getValue().toString()));
								mdl.setCredit_period(Integer.parseInt(creditPeriodTextField.getValue().toString()));
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setLogin(new S_LoginModel(getLoginID()));
								mdl.setComments(comment.getValue().toString());
								mdl.setCurrencyId((Long)currencyNativeSelect.getValue());
								double convRate = rateDao.getConversionRate(getCurrencyID(), mdl.getCurrencyId());
								mdl.setForeignCurrencyAmount(convRate* mdl.getAmount());
								mdl.setSales_local_type(Integer.parseInt(salesLocalTypeField.getValue().toString()));
								if(toDouble(grandTotalAmtTextField.getValue().toString())>toDouble(payingAmountTextField.getValue().toString()))
									mdl.setPayment_done('N');
								else
									mdl.setPayment_done('Y');
								mdl.setPaid_by_payment((double)0);
								
								FinTransaction trans = new FinTransaction();
								double totalAmount = toDouble(grandTotalAmtTextField.getValue());
								double netAmount = totalAmount;
								double payingAmount = toDouble(payingAmountTextField.getValue());
								double amount=0;
								long salesAcc = settings.getSALES_ACCOUNT();
//								if ((Integer) salesLocalTypeField.getValue() == SConstants.local_foreign_type.FOREIGN)
//									salesAcc = settings.getSALES_FOREIGN_ACCOUNT();

								if (payingAmount == netAmount) {
									trans.addTransaction(SConstants.CR,
											customer_id,
											settings.getCASH_ACCOUNT(),
											roundNumber(payingAmount));
									trans.addTransaction(SConstants.CR, salesAcc,
											customer_id, roundNumber(netAmount));

									mdl.setStatus(1);
									status = 1;
								} 
								else if (payingAmount == 0) {
									trans.addTransaction(SConstants.CR, 
														salesAcc,
														customer_id, 
														roundNumber(netAmount));
									mdl.setStatus(2);
									status = 2;
								} 
								else {
									trans.addTransaction(SConstants.CR,
														customer_id,
														settings.getCASH_ACCOUNT(),
														roundNumber(payingAmount));
									trans.addTransaction(SConstants.CR, 
														salesAcc,
														customer_id, 
														roundNumber(netAmount));
									status = 3;
									mdl.setStatus(3);
								}

								if (taxEnable) {
									if (settings.getSALES_TAX_ACCOUNT() != 0) {
										amount = toDouble(table.getColumnFooter(TBC_TAX_AMT));
										if (amount != 0) {
											trans.addTransaction(
																SConstants.CR,
																settings.getSALES_TAX_ACCOUNT(),
																settings.getCGS_ACCOUNT(),
																roundNumber(amount));
											totalAmount -= amount;
										}
									}

									if (settings.isCESS_ENABLED()) {
										if (settings.getCESS_ACCOUNT() != 0) {
											amount = toDouble(table.getColumnFooter(TBC_CESS_AMT));
											if (amount != 0) {
												trans.addTransaction(SConstants.CR,
																	settings.getCESS_ACCOUNT(),
																	settings.getCGS_ACCOUNT(),
														roundNumber(amount));
												totalAmount -= amount;
											}
										}
									}
								}

								if (settings.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
									amount = toDouble(exciseDutyTextField.getValue());
									if (amount != 0) {
										trans.addTransaction(
															SConstants.CR,
															settings.getSALES_EXCISE_DUTY_ACCOUNT(),
															settings.getCGS_ACCOUNT(),
															roundNumber(amount));
										totalAmount -= amount;
									}

								}

								if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
									amount = toDouble(shippingChargeTextField.getValue());
									if (amount != 0) {
										trans.addTransaction(
												SConstants.CR,
												settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
												settings.getCGS_ACCOUNT(),
												roundNumber(amount));
										totalAmount -= amount;
									}
								}

								if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
									amount = 0;
									try {
										amount = toDouble(table.getColumnFooter(TBC_DISCOUNT));
									} 
									catch (Exception e) {
										e.printStackTrace();
									}

									if (amount != 0) {
										trans.addTransaction(SConstants.CR,
															settings.getSALES_DESCOUNT_ACCOUNT(),
															settings.getCGS_ACCOUNT(),
															roundNumber(amount));
										totalAmount -= amount;
									}
								}

								if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
									if (amount != 0) {
										trans.addTransaction(	SConstants.CR,
																settings.getSALES_REVENUE_ACCOUNT(),
																settings.getCGS_ACCOUNT(),
																roundNumber(totalAmount));
									}
								}
								long id = dao.save(	mdl,trans.getTransaction(SConstants.RENTAL_TRANSACTION,CommonUtil.getSQLDateFromUtilDate(date.getValue())));
								loadSale(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								grandTotalAmtTextField.setReadOnly(true);
							}
						}
					}
					catch(Exception e){
						grandTotalAmtTextField.setReadOnly(true);
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

			salesNumberList.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try{
						if(salesNumberList.getValue()!=null && !salesNumberList.getValue().toString().equals("0")){
							RentalTransactionModel mdl=dao.getRentalTransactionModel((Long)salesNumberList.getValue());
							if(mdl!=null){
								table.removeAllItems();
								table.setVisibleColumns(allHeaders);
								Iterator itr=mdl.getInventory_details_list().iterator();
								while (itr.hasNext()) {
									RentalTransactionDetailsModel det = (RentalTransactionDetailsModel) itr.next();
									table.addItem(new Object[]{table.getItemIds().size()+1,
											det.getRental().getId(),
											det.getRental().getName(),
											det.getDescription(),
											det.getQunatity(),
											det.getLocation(),
											det.getMilage(),
											det.getUnit_price(),
											det.getTax().getId(),
											det.getTax_amount(),
											det.getTax_percentage(),
											det.getDiscount_amount(),
											roundNumber(det.getQunatity()*det.getUnit_price()),
											det.getCess_amount(),
											roundNumber((det.getQunatity()*det.getUnit_price())+det.getTax_amount()+det.getCess_amount()),
											roundNumber((det.getQunatity()*det.getUnit_price())+det.getTax_amount()+det.getCess_amount()-det.getDiscount_amount())
									},table.getItemIds().size()+1);
								}
								rentRadio.setValue(mdl.getRent_type());
								date.setValue(mdl.getDate());
								customerSelect.setValue(mdl.getCustomer().getId());
								creditPeriodTextField.setValue(mdl.getCredit_period()+"");
								salesManSelect.setValue(mdl.getResponsible_person());
								comment.setValue(mdl.getComments());
								payingAmountTextField.setValue(roundNumber(mdl.getPayment_amount())+"");
								shippingChargeTextField.setValue(roundNumber(mdl.getShipping_charge())+"");
								exciseDutyTextField.setValue(roundNumber(mdl.getExcise_duty())+"");
								currencyNativeSelect.setValue(mdl.getCurrencyId());
								calculateTotals();
								double convRate = rateDao.getConversionRate(getCurrencyID(), mdl.getCurrencyId());
								foreignCurrField.setNewValue(roundNumber(convRate* mdl.getAmount())+"");
								table.setVisibleColumns(requiredHeaders);
								status = mdl.getStatus();
								saveSalesButton.setVisible(false);
								updateSalesButton.setVisible(true);
								deleteSalesButton.setVisible(true);
								printButton.setVisible(true);
							}
						}
						else{
							saveSalesButton.setVisible(true);
							updateSalesButton.setVisible(false);
							deleteSalesButton.setVisible(false);
							printButton.setVisible(false);
							rentRadio.setValue((long)2);
							date.setValue(getWorkingDate());
							customerSelect.setValue(null);
							creditPeriodTextField.setValue("0");
							salesManSelect.setValue(null);
							table.removeAllItems();
							resetItems();
							table.setColumnFooter(TBC_QTY, "0");
							table.setColumnFooter(TBC_NET_PRICE, "0");
							table.setColumnFooter(TBC_TAX_AMT, "0");
							table.setColumnFooter(TBC_NET_TOTAL, "0");
							table.setColumnFooter(TBC_DISCOUNT, "0");
							table.setColumnFooter(TBC_NET_FINAL, "0");
							comment.setValue("");
							payingAmountTextField.setValue("0");
							shippingChargeTextField.setValue("0");
							exciseDutyTextField.setValue("0");
							grandTotalAmtTextField.setNewValue("0");
							foreignCurrField.setNewValue("0");
							currencyNativeSelect.setValue(getCurrencyID());
						}
					}
					catch(Exception e){
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

			updateSalesButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void buttonClick(ClickEvent event) {

					try{
						if(isValid()){
							long customer_id = (Long) customerSelect.getValue();
							RentalTransactionModel mdl=dao.getRentalTransactionModel((Long)salesNumberList.getValue());
							grandTotalAmtTextField.setReadOnly(false);
							List<RentalTransactionDetailsModel> itemsList = new ArrayList<RentalTransactionDetailsModel>();
							Iterator itr=table.getItemIds().iterator();
							while (itr.hasNext()) {
								RentalTransactionDetailsModel det=new RentalTransactionDetailsModel();
								Item item=table.getItem(itr.next());
								det.setRental(new SubscriptionCreationModel((Long)item.getItemProperty(TBC_RENTAL_ID).getValue()));
								det.setDescription(item.getItemProperty(TBC_DESCRIPTION).getValue().toString());
								det.setQunatity((Double)item.getItemProperty(TBC_QTY).getValue());
								det.setUnit_price((Double)item.getItemProperty(TBC_UNIT_PRICE).getValue());
								det.setTax(new TaxModel((Long)item.getItemProperty(TBC_TAX_ID).getValue()));
								det.setTax_amount((Double)item.getItemProperty(TBC_TAX_AMT).getValue());
								det.setTax_percentage((Double)item.getItemProperty(TBC_TAX_PERC).getValue());
								det.setDiscount_amount((Double)item.getItemProperty(TBC_DISCOUNT).getValue());
								det.setCess_amount((Double)item.getItemProperty(TBC_CESS_AMT).getValue());
								det.setLocation(item.getItemProperty(TBC_LOCATION).getValue().toString());
								det.setMilage((Double)item.getItemProperty(TBC_MILAGE).getValue());
								itemsList.add(det);
							}
							mdl.setInventory_details_list(itemsList);
							mdl.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							mdl.setCreated_time(CommonUtil.getCurrentDateTime());
							mdl.setResponsible_person((Long) salesManSelect.getValue());
							mdl.setSales_person((Long) salesManSelect.getValue());
							mdl.setCustomer(new LedgerModel((Long)customerSelect.getValue()));
							mdl.setShipping_charge(toDouble(shippingChargeTextField.getValue().toString()));
							mdl.setExcise_duty(toDouble(exciseDutyTextField.getValue().toString()));
							mdl.setPayment_amount(toDouble(payingAmountTextField.getValue().toString()));
							mdl.setAmount(toDouble(grandTotalAmtTextField.getValue().toString()));
							mdl.setActive(true);
							mdl.setRent_type(toLong(rentRadio.getValue().toString()));
							mdl.setCredit_period(Integer.parseInt(creditPeriodTextField.getValue().toString()));
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							mdl.setLogin(new S_LoginModel(getLoginID()));
							mdl.setComments(comment.getValue().toString());
							mdl.setCurrencyId((Long)currencyNativeSelect.getValue());
							double convRate = rateDao.getConversionRate(getCurrencyID(), mdl.getCurrencyId());
							mdl.setForeignCurrencyAmount(convRate* mdl.getAmount());
							mdl.setSales_local_type(Integer.parseInt(salesLocalTypeField.getValue().toString()));
							
							if(toDouble(grandTotalAmtTextField.getValue().toString())>(toDouble(payingAmountTextField.getValue().toString())+mdl.getPaid_by_payment()))
								mdl.setPayment_done('N');
							else
								mdl.setPayment_done('Y');
							
							FinTransaction trans = new FinTransaction();
							double totalAmount = toDouble(grandTotalAmtTextField.getValue());
							double netAmount = totalAmount;
							double payingAmount = toDouble(payingAmountTextField.getValue());
							double amount=0;
							long salesAcc = settings.getSALES_ACCOUNT();
//							if ((Integer) salesLocalTypeField.getValue() == SConstants.local_foreign_type.FOREIGN)
//								salesAcc = settings.getSALES_FOREIGN_ACCOUNT();

							if (payingAmount == netAmount) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmount));
								trans.addTransaction(SConstants.CR, salesAcc,
										customer_id, roundNumber(netAmount));

								mdl.setStatus(1);
								status = 1;
							} 
							else if (payingAmount == 0) {
								trans.addTransaction(SConstants.CR, 
													salesAcc,
													customer_id, 
													roundNumber(netAmount));
								mdl.setStatus(2);
								status = 2;
							} 
							else {
								trans.addTransaction(SConstants.CR,
													customer_id,
													settings.getCASH_ACCOUNT(),
													roundNumber(payingAmount));
								trans.addTransaction(SConstants.CR, 
													salesAcc,
													customer_id, 
													roundNumber(netAmount));
								status = 3;
								mdl.setStatus(3);
							}

							if (taxEnable) {
								if (settings.getSALES_TAX_ACCOUNT() != 0) {
									amount = toDouble(table.getColumnFooter(TBC_TAX_AMT));
									if (amount != 0) {
										trans.addTransaction(
															SConstants.CR,
															settings.getSALES_TAX_ACCOUNT(),
															settings.getCGS_ACCOUNT(),
															roundNumber(amount));
										totalAmount -= amount;
									}
								}

								if (settings.isCESS_ENABLED()) {
									if (settings.getCESS_ACCOUNT() != 0) {
										amount = toDouble(table.getColumnFooter(TBC_CESS_AMT));
										if (amount != 0) {
											trans.addTransaction(SConstants.CR,
																settings.getCESS_ACCOUNT(),
																settings.getCGS_ACCOUNT(),
													roundNumber(amount));
											totalAmount -= amount;
										}
									}
								}
							}

							if (settings.getSALES_EXCISE_DUTY_ACCOUNT() != 0) {
								amount = toDouble(exciseDutyTextField.getValue());
								if (amount != 0) {
									trans.addTransaction(
														SConstants.CR,
														settings.getSALES_EXCISE_DUTY_ACCOUNT(),
														settings.getCGS_ACCOUNT(),
														roundNumber(amount));
									totalAmount -= amount;
								}

							}

							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
								amount = toDouble(shippingChargeTextField.getValue());
								if (amount != 0) {
									trans.addTransaction(
											SConstants.CR,
											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
											settings.getCGS_ACCOUNT(),
											roundNumber(amount));
									totalAmount -= amount;
								}
							}

							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
								amount = 0;
								try {
									amount = toDouble(table.getColumnFooter(TBC_DISCOUNT));
								} 
								catch (Exception e) {
									e.printStackTrace();
								}

								if (amount != 0) {
									trans.addTransaction(SConstants.CR,
														settings.getSALES_DESCOUNT_ACCOUNT(),
														settings.getCGS_ACCOUNT(),
														roundNumber(amount));
									totalAmount -= amount;
								}
							}

							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
								if (amount != 0) {
									trans.addTransaction(	SConstants.CR,
															settings.getSALES_REVENUE_ACCOUNT(),
															settings.getCGS_ACCOUNT(),
															roundNumber(totalAmount));
								}
							}
							
							TransactionModel transaction = dao.getTransaction(mdl.getTransaction_id());
							transaction.setTransaction_details_list(trans.getChildList());
							transaction.setDate(mdl.getDate());
							transaction.setLogin_id(getLoginID());

							dao.update(mdl, transaction);
							loadSale(mdl.getId());
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							grandTotalAmtTextField.setReadOnly(true);
						}
					}
					catch(Exception e){
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
					
				}
			});

			deleteSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesNumberList.getValue() != null && !salesNumberList.getValue().toString().equals("0")) {
						ConfirmDialog.show(getUI(), "Are you sure?",new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.delete((Long) salesNumberList.getValue());
										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadSale(0);
									} 
									catch (Exception e) {
										e.printStackTrace();
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									}
								}
							}
						});
					}
				}
			});

			table.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();
					if (table.getValue() != null) {
						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
						Item item=table.getItem(table.getValue());
						itemSelectCombo.setValue(item.getItemProperty(TBC_RENTAL_ID).getValue());
						description.setValue(item.getItemProperty(TBC_DESCRIPTION).getValue()+"");
						quantityTextField.setValue(item.getItemProperty(TBC_QTY).getValue()+"");
						unitPriceTextField.setValue(item.getItemProperty(TBC_UNIT_PRICE).getValue()+"");
						locationTextField.setValue(item.getItemProperty(TBC_LOCATION).getValue()+"");
						milageTextField.setValue(item.getItemProperty(TBC_MILAGE).getValue()+"");
						if((Long)item.getItemProperty(TBC_TAX_ID).getValue()!=0)
							taxSelect.setValue(item.getItemProperty(TBC_TAX_ID).getValue());
						else
							taxSelect.setValue(null);
						discountTextField.setValue(item.getItemProperty(TBC_DISCOUNT).getValue()+"");
						netPriceTextField.setNewValue(item.getItemProperty(TBC_NET_FINAL).getValue()+"");
					}
					else {
						resetItems();
						visibleAddupdateSalesButton(true, false);
					}
				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public void buttonClick(ClickEvent event) {
					
					try {
						if (table.getComponentError() != null)
							setRequiredError(table, null, false);
						if (isAddingValid()) {
							List idList=new ArrayList();
							double price=0,quantity=0,discount=0;
							SubscriptionCreationModel mdl = new SubscriptionCreationDao().getCreationModel((Long)itemSelectCombo.getValue());
							if(mdl!=null){
//								Iterator itr=table.getItemIds().iterator();
//								while (itr.hasNext()) {
//									Item item=table.getItem(itr.next());
//									long id=toLong(item.getItemProperty(TBC_RENTAL_ID).getValue().toString());
//									idList.add(id);
//								}
//								if(idList.contains(mdl.getId())){
//									
//								}
//								else{
									price = toDouble(unitPriceTextField.getValue());
									quantity = toDouble(quantityTextField.getValue());
									discount = toDouble(discountTextField.getValue());
									netPriceTextField.setNewValue(getFormattedAmount(roundNumber(price* quantity)));
									table.setVisibleColumns(allHeaders);
									double tax_amount=0,tax_percent=0,total=0,cess=0;
									TaxModel tax=null;
									if (taxEnable) {
										tax=  taxDao.getTax((Long) taxSelect.getValue());
										if(tax!=null){
											if(tax.getValue_type()==1){
												tax_percent=tax.getValue();
												tax_amount = roundNumber(price * quantity* tax_percent / 100);
											}
											else{
												tax_percent=0;
												tax_amount=tax.getValue();
											}
										}
									}
									else{
										tax = new TaxModel(1);
										tax_percent=0;
										tax_amount=0;
									}
									
									total=roundNumber(price * quantity);
									
									if(isCessEnable()){
										cess=(tax_amount*getCessPercentage()/100);
									}
									else{
										cess=0;
									}
									double milage=0;	
									if(milageTextField.getValue().toString().length()>0)
										milage=toDouble(milageTextField.getValue().toString());
									else
										milage=0;
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											(Long)itemSelectCombo.getValue(),
											itemSelectCombo.getItemCaption((Long)itemSelectCombo.getValue()),
											description.getValue(),
											toDouble(quantityTextField.getValue().toString()),
											locationTextField.getValue(),
											milage,
											toDouble(unitPriceTextField.getValue().toString()),
											tax.getId(),
											tax_amount,
											tax_percent,
											toDouble(discountTextField.getValue().toString()),
											total,
											cess,
											roundNumber(total + tax_amount + cess),
											roundNumber(total + tax_amount + cess - discount)},table.getItemIds().size()+1);
									
									table.setVisibleColumns(requiredHeaders);
									resetItems();
									calculateTotals();
//								}
							}
							else{
								resetItems();
							}
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try{
						if(isAddingValid()){
							if(table.getValue()!=null){
								Item item=table.getItem(table.getValue());
								double price=0,quantity=0,discount=0;
								price = toDouble(unitPriceTextField.getValue());
								quantity = toDouble(quantityTextField.getValue());
								discount = toDouble(discountTextField.getValue());
								netPriceTextField.setNewValue(getFormattedAmount(roundNumber(price* quantity)));
//								table.setVisibleColumns(allHeaders);
								double tax_amount=0,tax_percent=0,total=0,cess=0;
								TaxModel tax=null;
								if (taxEnable) {
									tax=  taxDao.getTax((Long) taxSelect.getValue());
									if(tax!=null){
										if(tax.getValue_type()==1){
											tax_percent=tax.getValue();
											tax_amount = roundNumber(price * quantity* tax_percent / 100);
										}
										else{
											tax_percent=0;
											tax_amount=tax.getValue();
										}
									}
								}
								else{
									tax = new TaxModel(1);
									tax_percent=0;
									tax_amount=0;
								}
								
								total=roundNumber(price * quantity);
								
								if(isCessEnable()){
									cess=(tax_amount*getCessPercentage()/100);
								}
								else{
									cess=0;
								}
								
								item.getItemProperty(TBC_RENTAL_ID).setValue((Long)itemSelectCombo.getValue());
								item.getItemProperty(TBC_RENTAL_NAME).setValue(itemSelectCombo.getItemCaption((Long)itemSelectCombo.getValue()));
								item.getItemProperty(TBC_DESCRIPTION).setValue(description.getValue());
								item.getItemProperty(TBC_QTY).setValue(toDouble(quantityTextField.getValue().toString()));
								item.getItemProperty(TBC_LOCATION).setValue(locationTextField.getValue());
								item.getItemProperty(TBC_MILAGE).setValue(toDouble(milageTextField.getValue().toString()));
								item.getItemProperty(TBC_UNIT_PRICE).setValue(toDouble(unitPriceTextField.getValue().toString()));
								item.getItemProperty(TBC_TAX_ID).setValue(tax.getId());
								item.getItemProperty(TBC_TAX_AMT).setValue(tax_amount);
								item.getItemProperty(TBC_TAX_PERC).setValue(tax_percent);
								item.getItemProperty(TBC_DISCOUNT).setValue(toDouble(discountTextField.getValue().toString()));
								item.getItemProperty(TBC_NET_PRICE).setValue(total);
								item.getItemProperty(TBC_CESS_AMT).setValue(cess);
								item.getItemProperty(TBC_NET_TOTAL).setValue(roundNumber(total + tax_amount + cess));
								item.getItemProperty(TBC_NET_FINAL).setValue(roundNumber(total + tax_amount + cess - discount));
								table.setValue(null);
								calculateTotals();
							}
							else{
								resetItems();
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

			// Added by Anil

			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					List<Object> reportList = new ArrayList<Object>();
					SalesPrintBean bean = null;
					NumberToWords numberToWords = new NumberToWords();
					double total = 0;
					try {

						RentalTransactionModel salObj = dao.getRentalTransactionModel((Long) salesNumberList.getValue());

						String address = "";
//							address = new AddressDao().getAddressString(salObj.getCustomer().getAddress().getId());

						map.put("CUSTOMER_NAME", salObj.getCustomer().getName());
						map.put("CUSTOMER_ADDRESS", address);
						map.put("SALES_BILL_NO", salObj.getSales_number());
						map.put("PURCH_BILL_DATE", CommonUtil.formatDateToDDMMMYYYY(salObj.getDate()));
						map.put("SALES_MAN", usrDao.getUserNameFromLoginID(salObj
										.getSales_person()));

						map.put("CURRENCY", salObj.getOffice().getCurrency()
								.getCode());
						map.put("PAID_AMOUNT", salObj.getPayment_amount());
						map.put("CUR_DATE", getFormattedTime(new Date()));
						String adr1 = "", adr2 = "";
						if (salObj.getOffice().getAddress() != null) {

							adr1 += salObj.getOffice().getAddress()
									.getCountry().getName();

							if (salObj.getOffice().getAddress().getPhone() != null
									&& salObj.getOffice().getAddress()
											.getPhone().length() > 0)
								adr2 += "Tel : "
										+ salObj.getOffice().getAddress()
												.getPhone() + "   ";

						}
						map.put("ADDRESS1", adr1);
						map.put("ADDRESS2", adr2);
						map.put("OFFICE_ADDRESS",
								new AddressDao().getAddressString(salObj
										.getOffice().getAddress().getId()));

						String resp = "";
						if (salObj.getResponsible_person() != 0) {
							UserModel usrObj = usrDao.getUserFromLogin(salObj
									.getResponsible_person());

							if(usrObj!=null){
							resp = usrObj.getFirst_name();

							if (usrObj.getAddress() != null) {
								if (usrObj.getAddress().getMobile() != null
										&& !usrObj.getAddress().getMobile()
												.equals(""))
									resp += " Mob: "
											+ usrObj.getAddress().getMobile();
								if (usrObj.getAddress().getPhone() != null
										&& !usrObj.getAddress().getPhone()
												.equals(""))
									resp += " Ph: "
											+ usrObj.getAddress().getPhone();
							}
							}
						}
						map.put("RESPONSIBLE_PERSON", resp);

						String type = "";
						if (status == 1) {
							type = "Cash Sale";
						} else {
							type = "Credit Sale";
						}
						map.put("SALES_TYPE", getPropertyName("rental"));
						map.put("TAX_ENABLED", isTaxEnable());
						map.put("OFFICE_NAME", salObj.getOffice().getName());

						map.put("IMAGE_PATH", VaadinServlet.getCurrent()
								.getServletContext().getRealPath("/")
								.toString());

						map.put("TELEPHONE", salObj.getOffice().getAddress()
								.getPhone());
						double netTotal = 0;
						RentalTransactionDetailsModel invObj;
						Iterator<RentalTransactionDetailsModel> itr1 = salObj.getInventory_details_list().iterator();
						while (itr1.hasNext()) {
							invObj = itr1.next();

							bean = new SalesPrintBean(	invObj.getRental().getName(), 
														invObj.getQunatity(),
														invObj.getUnit_price(),
														roundNumber(invObj.getQunatity() *invObj.getUnit_price()),
														roundNumber((invObj.getQunatity() * invObj.getUnit_price())- invObj.getDiscount_amount()+ invObj.getCess_amount()+ invObj.getTax_amount()),
														roundNumber(invObj.getMilage()),invObj.getLocation());
							
							bean.setDiscount(invObj.getDiscount_amount());
							bean.setCurrency(salObj.getOffice().getCurrency()
									.getCode());
							bean.setDescription(invObj.getDescription());

							total += bean.getTotal();
							netTotal += invObj.getQunatity()
									* invObj.getUnit_price();

							reportList.add(bean);
						}
						total += salObj.getShipping_charge();
						map.put("GRAND_TOTAL", total);
						map.put("NET_TOTAL", netTotal);
						map.put("ORGANIZATION_NAME", salObj.getOffice()
								.getOrganization().getName());

						S_OfficeModel officeModel = new OfficeDao()
								.getOffice(getOfficeID());
						map.put("AMOUNT_IN_WORDS", numberToWords.convertNumber(
								getFormattedAmount(total) , officeModel
										.getCurrency().getInteger_part(),
								officeModel.getCurrency().getFractional_part()));

						Report report = new Report(getLoginID());
						report.setJrxmlFileName("Rental_Print_A4");
						report.setReportFileName("Rental Print");
						
						map.put("REPORT_TITLE_LABEL", getPropertyName("rental"));
						map.put("SL_NO_LABEL", getPropertyName("sl_no"));
						map.put("DESCRIPTION_LABEL", getPropertyName("description"));
						map.put("QUANTITY_LABEL", getPropertyName("quantity"));
						map.put("RATE_LABEL", getPropertyName("rate"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						map.put("DATE_LABEL", getPropertyName("date"));
						map.put("RESPONSIBLE_PERSON_LABEL", getPropertyName("resp_prsn"));
						map.put("NO_LABEL", getPropertyName("num"));
						map.put("MR_LABEL", getPropertyName("mr"));
						map.put("RECEIVED_BY_LABEL", getPropertyName("received_by"));
						map.put("MOBILE_NO_LABEL", getPropertyName("mobile_no"));
						map.put("ISSUED_BY_LABEL", getPropertyName("issued_by"));
						map.put("ITEM_LABEL", getPropertyName("item"));
						map.put("KILOMETER_LABEL", getPropertyName("kilo_meter"));
						map.put("LOCATION_LABEL", getPropertyName("location"));
						
						// report.setReportTitle("Sales Invoice");
						report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						report.createReport(reportList, map);

						report.print();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (!isFinYearBackEntry()) {
				saveSalesButton.setVisible(false);
				updateSalesButton.setVisible(false);
				deleteSalesButton.setVisible(false);
				cancelSalesButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if(isCalculationValid()){
							calculateNetPrice();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			quantityTextField.setImmediate(true);

			quantityTextField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if(isCalculationValid()){
							calculateNetPrice();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}

				}
			});

		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		currencyNativeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				calculateForeignCurrencyAmount();
			}
		});

		return pannel;
		
	}

	protected void calculateForeignCurrencyAmount() {
		try {
			double convRate = rateDao.getConversionRate(getCurrencyID(),
					toLong(currencyNativeSelect.getValue().toString()));
			foreignCurrField.setNewValue(getFormattedAmount(convRate
					* (toDouble(grandTotalAmtTextField.getValue()))));
		} catch (Exception e) {
			foreignCurrField.setNewValue(grandTotalAmtTextField.getValue());
		}
	}
	
	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc = 0;
		try {
			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
//			disc = Double.parseDouble(discountTextField.getValue());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		netPriceTextField.setNewValue(getFormattedAmount(roundNumber(toDouble(asString(new BigDecimal((unitPrc * qty) - disc))))));
	}

	@SuppressWarnings("rawtypes")
	public void calculateTotals() {
		try {

			double quantity = 0, tax = 0, net = 0, discount = 0, ttl_bfr_tax = 0, ttl_bfr_disc = 0, cess = 0;
			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				quantity += (Double) item.getItemProperty(TBC_QTY).getValue();
				if (taxEnable) {
					tax += (Double) item.getItemProperty(TBC_TAX_AMT).getValue();
					cess += (Double) item.getItemProperty(TBC_CESS_AMT).getValue();
				}
				net += (Double) item.getItemProperty(TBC_NET_FINAL).getValue();
				discount += (Double) item.getItemProperty(TBC_DISCOUNT)
						.getValue();

				ttl_bfr_tax += (Double) item.getItemProperty(TBC_NET_TOTAL).getValue();
				ttl_bfr_disc += (Double) item.getItemProperty(TBC_NET_FINAL).getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(quantity)));
			table.setColumnFooter(TBC_TAX_AMT, getFormattedAmount(roundNumber(tax)));
			table.setColumnFooter(TBC_NET_PRICE, getFormattedAmount(roundNumber(ttl_bfr_disc)));
			table.setColumnFooter(TBC_DISCOUNT, getFormattedAmount(roundNumber(discount)));
			table.setColumnFooter(TBC_CESS_AMT, getFormattedAmount(roundNumber(cess)));
			table.setColumnFooter(TBC_NET_TOTAL,getFormattedAmount(roundNumber(ttl_bfr_tax)));
			table.setColumnFooter(TBC_NET_FINAL,getFormattedAmount(roundNumber(net)));
			double ship_charg = 0, excise_duty = 0;
			try {
				ship_charg = toDouble(shippingChargeTextField.getValue());
				excise_duty = toDouble(exciseDutyTextField.getValue());
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			grandTotalAmtTextField.setNewValue(getFormattedAmount(roundNumber(net + ship_charg + excise_duty)));
			calculateForeignCurrencyAmount();
		} 
		catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			/*if (discountTextField.getValue() == null || discountTextField.getValue().equals("")) {
				discountTextField.setValue("0");
			} 
			else {
				try {
					if (toDouble(discountTextField.getValue()) < 0) {
						setRequiredError(discountTextField,getPropertyName("invalid_data"), true);
						discountTextField.focus();
						ret = false;
					} 
					else
						setRequiredError(discountTextField, null, false);
				} 
				catch (Exception e) {
					setRequiredError(discountTextField,getPropertyName("invalid_data"), true);
					discountTextField.focus();
					ret = false;
				}
			}*/

			if (taxEnable) {
				if (taxSelect.getValue() == null || taxSelect.getValue().equals("")) {
					setRequiredError(taxSelect,getPropertyName("invalid_selection"), true);
					taxSelect.focus();
					ret = false;
				} 
				else
					setRequiredError(taxSelect, null, false);
			}

			if (unitPriceTextField.getValue() == null || unitPriceTextField.getValue().equals("")) {
				setRequiredError(unitPriceTextField,getPropertyName("invalid_data"), true);
				unitPriceTextField.focus();
				unitPriceTextField.selectAll();
				ret = false;
			} 
			else {
				try {
					if (toDouble(unitPriceTextField.getValue()) <= 0) {
						setRequiredError(unitPriceTextField,getPropertyName("invalid_data"), true);
						unitPriceTextField.focus();
						unitPriceTextField.selectAll();
						ret = false;
					} 
					else
						setRequiredError(unitPriceTextField, null, false);
				} 
				catch (Exception e) {
					setRequiredError(unitPriceTextField,getPropertyName("invalid_data"), true);
					unitPriceTextField.focus();
					unitPriceTextField.selectAll();
					ret = false;
				}
			}

			if (quantityTextField.getValue() == null || quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				quantityTextField.selectAll();
				ret = false;
			} 
			else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,getPropertyName("invalid_data"), true);
						quantityTextField.focus();
						quantityTextField.selectAll();
						ret = false;
					} 
					else
						setRequiredError(quantityTextField, null, false);
				} 
				catch (Exception e) {
					setRequiredError(quantityTextField,getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					quantityTextField.selectAll();
					ret = false;
				}
			}
			
			if (milageTextField.getValue()!= null && milageTextField.getValue().toString().length()!=0) {
				try {
					if (toDouble(milageTextField.getValue()) < 0) {
						setRequiredError(milageTextField,getPropertyName("invalid_data"), true);
						milageTextField.focus();
						milageTextField.selectAll();
						ret = false;
					} 
					else
						setRequiredError(milageTextField, null, false);
				} 
				catch (Exception e) {
					setRequiredError(milageTextField,getPropertyName("invalid_data"), true);
					milageTextField.focus();
					milageTextField.selectAll();
					ret = false;
				}
			}
			
			if (itemSelectCombo.getValue() == null || itemSelectCombo.getValue().equals("")) {
				setRequiredError(itemSelectCombo,getPropertyName("invalid_selection"), true);
				itemSelectCombo.focus();
				ret = false;
			} 
			else
				setRequiredError(itemSelectCombo, null, false);

		} 
		catch (Exception e) {
			ret = false;
		}
		
		return ret;

	}

	public void visibleAddupdateSalesButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}
	
	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				Collection selectedItems = (Collection) table.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					// Item item=table.getItem(selectedItems.iterator().next());
					table.removeItem(it1.next());
				}

				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = table.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

				calculateTotals();
			}
			itemSelectCombo.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	public void loadSale(long id) {
		List list;
		try {
			list=new ArrayList();
			list.add(0,new RentalTransactionModel(0, getPropertyName("create_new")));
			list.addAll(dao.getAllRentalTransactions(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			salesNumberList.setContainerDataSource(bic);
			salesNumberList.setItemCaptionPropertyId("comments");
			salesNumberList.setValue(id);
		} 
		catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (payingAmountTextField.getValue() == null || payingAmountTextField.getValue().equals("")) {
			payingAmountTextField.setValue("0");
		} 
		else {
			try {
				if (toDouble(payingAmountTextField.getValue()) < 0) {
					setRequiredError(payingAmountTextField,getPropertyName("invalid_data"), true);
					payingAmountTextField.focus();
					ret = false;
				} 
				else
					setRequiredError(payingAmountTextField, null, false);
			} 
			catch (Exception e) {
				setRequiredError(payingAmountTextField,getPropertyName("invalid_data"), true);
				payingAmountTextField.focus();
				ret = false;
			}
		}

		if ((Long) cashOrCreditRadio.getValue() == 1) {
			if (toDouble(payingAmountTextField.getValue()) != toDouble(grandTotalAmtTextField.getValue())) {
				setRequiredError(payingAmountTextField,getPropertyName("invalid_data"), true);
				payingAmountTextField.focus();
				ret = false;
			}
		} 
		else if (toDouble(payingAmountTextField.getValue()) >= toDouble(grandTotalAmtTextField.getValue())
				&& toDouble(grandTotalAmtTextField.getValue()) != 0) {
			setRequiredError(payingAmountTextField,getPropertyName("invalid_data"), true);
			payingAmountTextField.focus();
			ret = false;
		}

		if (shippingChargeTextField.getValue() == null || shippingChargeTextField.getValue().equals("")) {
			setRequiredError(shippingChargeTextField,getPropertyName("invalid_data"), true);
			shippingChargeTextField.focus();
			ret = false;
		} 
		else {
			try {
				if (toDouble(shippingChargeTextField.getValue()) < 0) {
					setRequiredError(shippingChargeTextField,getPropertyName("invalid_data"), true);
					shippingChargeTextField.focus();
					ret = false;
				} 
				else
					setRequiredError(shippingChargeTextField, null, false);
			} 
			catch (Exception e) {
				setRequiredError(shippingChargeTextField,getPropertyName("invalid_data"), true);
				shippingChargeTextField.focus();
				ret = false;
			}
		}

		if (exciseDutyTextField.getValue() == null || exciseDutyTextField.getValue().equals("")) {
			setRequiredError(exciseDutyTextField,getPropertyName("invalid_data"), true);
			exciseDutyTextField.focus();
			ret = false;
		} 
		else {
			try {
				if (toDouble(exciseDutyTextField.getValue()) < 0) {
					setRequiredError(exciseDutyTextField,getPropertyName("invalid_data"), true);
					exciseDutyTextField.focus();
					ret = false;
				} 
				else
					setRequiredError(exciseDutyTextField, null, false);
			} 
			catch (Exception e) {
				setRequiredError(exciseDutyTextField,getPropertyName("invalid_data"), true);
				exciseDutyTextField.focus();
				ret = false;
			}
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			itemSelectCombo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (salesManSelect.getValue() == null || salesManSelect.getValue().equals("")) {
			setRequiredError(salesManSelect,getPropertyName("invalid_selection"), true);
			salesManSelect.focus();
			ret = false;
		} 
		else
			setRequiredError(salesManSelect, null, false);

		if (creditPeriodTextField.getValue() == null || creditPeriodTextField.getValue().equals("")) {
			creditPeriodTextField.setValue("0");
		} 
		else {
			try {
				if (toInt(creditPeriodTextField.getValue()) < 0) {
					setRequiredError(creditPeriodTextField,getPropertyName("invalid_data"), true);
					creditPeriodTextField.focus();
					ret = false;
				} 
				else
					setRequiredError(creditPeriodTextField, null, false);
			} 
			catch (Exception e) {
				setRequiredError(creditPeriodTextField,getPropertyName("invalid_data"), true);
				creditPeriodTextField.focus();
				ret = false;
			}
		}

		if (refNoField.getValue() == null || refNoField.getValue().equals("")) {
			setRequiredError(refNoField, getPropertyName("invalid_data"), true);
			refNoField.focus();
			ret = false;
		} 
		else {
			try {
				if (toInt(refNoField.getValue()) < 0) {
					setRequiredError(refNoField,getPropertyName("invalid_data"), true);
					refNoField.focus();
					ret = false;
				} 
				else
					setRequiredError(refNoField, null, false);
			}
			catch (Exception e) {
				setRequiredError(refNoField, getPropertyName("invalid_data"),true);
				refNoField.focus();
				ret = false;
			}
		}

//		if (staffSelect.getValue() == null || staffSelect.getValue().equals("")) {
//			setRequiredError(staffSelect,getPropertyName("invalid_selection"), true);
//			staffSelect.focus();
//			ret = false;
//		} 
//		else
//			setRequiredError(staffSelect, null, false);

		if (customerSelect.getValue() == null || customerSelect.getValue().equals("")) {
			setRequiredError(customerSelect,getPropertyName("invalid_selection"), true);
			customerSelect.focus();
			ret = false;
		} 
		else
			setRequiredError(customerSelect, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} 
		else
			setRequiredError(date, null, false);
		
		if (ret)
			calculateTotals();
		return ret;
	}
	
	public void removeAllErrors() {
		if (discountTextField.getComponentError() != null)
			setRequiredError(discountTextField, null, false);
		if (taxSelect.getComponentError() != null)
			setRequiredError(taxSelect, null, false);
		if (unitPriceTextField.getComponentError() != null)
			setRequiredError(unitPriceTextField, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (itemSelectCombo.getComponentError() != null)
			setRequiredError(itemSelectCombo, null, false);
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		if (salesManSelect.getComponentError() != null)
			setRequiredError(salesManSelect, null, false);
	}
	
	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SComboField getSalesNumberList() {
		return salesNumberList;
	}
	
	public void setSalesNumberList(SComboField salesNumberList) {
		this.salesNumberList = salesNumberList;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return salesNumberList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberIncome(long id){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllIncomeSubscriptions(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			customerSelect.setContainerDataSource(bic);
			customerSelect.setItemCaptionPropertyId("name");
			customerSelect.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberTransportation(long id){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllTransportationSubscriptions(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			customerSelect.setContainerDataSource(bic);
			customerSelect.setItemCaptionPropertyId("name");
			customerSelect.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void resetItems(){
		itemSelectCombo.setNewValue(null);
		description.setValue("");
		quantityTextField.setValue("0");
		unitPriceTextField.setNewValue("0");
		locationTextField.setValue("");
		milageTextField.setValue("0");
		discountTextField.setValue("0");
		netPriceTextField.setNewValue("0.0");
		taxSelect.setValue(null);
		addItemButton.setVisible(true);
		updateItemButton.setVisible(false);
	}

	public boolean isCalculationValid(){
		boolean ret=true;
		if (unitPriceTextField.getValue() == null || unitPriceTextField.getValue().equals("")) {
			ret = false;
		} 
		else {
			try {
				if (toDouble(unitPriceTextField.getValue()) <= 0) {
					ret = false;
				} 
			} 
			catch (Exception e) {
				ret = false;
			}
		}

		if (quantityTextField.getValue() == null || quantityTextField.getValue().equals("")) {
			ret = false;
		} 
		else {
			try {
				if (toDouble(quantityTextField.getValue()) <= 0) {
					ret = false;
				} 
			} 
			catch (Exception e) {
				ret = false;
			}
		}
		return ret;
	}
	
}
