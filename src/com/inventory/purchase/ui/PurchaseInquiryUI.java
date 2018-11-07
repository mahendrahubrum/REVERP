package com.inventory.purchase.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.acct.ui.AddSupplier;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.purchase.dao.PurchaseInquiryDao;
import com.inventory.purchase.model.PurchaseInquiryDetailsModel;
import com.inventory.purchase.model.PurchaseInquiryModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
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
import com.webspark.Components.SComboSearchField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SSelectionField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.business.AddressBusiness;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author anil
 *
 */

/**
 * 
 * @author sangeeth
 *
 */

public class PurchaseInquiryUI extends SparkLogic {

	private static final long serialVersionUID = 7300632721366226830L;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_EDITABLE = "Editable";
	static String TBC_ITEM_ID = "Item Id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_CONVERTION_QTY = "Convertion Qty";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";
	static String TBC_UNIT_ID = "Unit Id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_CID="Currency Id";
	static String TBC_CURRENCY="Currency";
	static String TBC_CONV_RATE="Conv Rate";
	static String TBC_NET_PRICE = "Net Price";

	PurchaseInquiryDao dao;
	SComboField purchaseInquiryCombo;
	STextField referenceNoField;
	SDateField dateField;
	SComboField supplierCombo;
	SButton newSupplierButton;
	SComboField responsilbeEmployeeCombo;
	STextField contactField;
	Date previousDate;
	
	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	SComboSearchField a;

	STable table;

	SHorizontalLayout itemLayout;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	SComboField itemCombo;
	SButton newItemButton;
	STextField quantityField;
	STextField convertionQuantityField;
	STextField convertedQuantityField;
	SComboField departmentCombo;
	SSelectionField divisionCombo;
	SNativeSelect unitSelect;
	SCurrencyField unitPriceField;
	STextField netPriceField;

	SButton addItemButton;
	SButton updateItemButton;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;
	SButton printButton;
	SButton sendMailButton;

	ItemDao itemDao = new ItemDao();

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	SWindow popupWindow;

	SCurrencyField netAmountField;
	SCurrencyField convertedField;
	STextArea comment;

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	boolean taxEnable = isTaxEnable();

	CommonMethodsDao comDao;
	ItemDao itmDao;
	TaxDao taxDao;
	UnitDao untDao;
	SupplierDao supDao;

	SButton createNewButton;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings({"serial", "rawtypes", "unchecked" })
	@Override
	public SPanel getGUI() {
		previousDate=new Date();
		previousDate=getWorkingDate();
		dao=new PurchaseInquiryDao();
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		taxEnable = isTaxEnable();

		comDao = new CommonMethodsDao();
		itmDao = new ItemDao();
		taxDao = new TaxDao();
		untDao = new UnitDao();
		supDao=new SupplierDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Purchase Inquiry");
		
		allHeaders = new String[] { TBC_SN, TBC_ID, TBC_EDITABLE, TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME,
				TBC_QTY, TBC_CONVERTION_QTY, TBC_QTY_IN_BASIC_UNIT, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE, TBC_CID, TBC_CURRENCY, TBC_CONV_RATE, TBC_NET_PRICE };
		
		requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,	TBC_NET_PRICE };

		List<Object> tempList = new ArrayList<Object>();
		Collections.addAll(tempList, requiredHeaders);
		
		requiredHeaders = tempList.toArray(new String[tempList.size()]);

		setSize(1200, 625);

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		itemLayout = new SHorizontalLayout();
		itemLayout.setSizeFull();

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(2);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(12);
		bottomGrid.setRows(2);

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
			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);
			purchaseInquiryCombo = new SComboField(null, 150, null, "id","ref_no", false, getPropertyName("create_new"));
			referenceNoField = new STextField(null, 150);
			dateField = new SDateField(null, 100, getDateFormat());
			supplierCombo = new SComboField(null, 150, new LedgerDao().getAllSuppliers(getOfficeID()), 
					"id", "name", true, getPropertyName("select"));
			newSupplierButton = new SButton();
			newSupplierButton.setStyleName("addNewBtnStyle");
			newSupplierButton.setDescription("Add New Supplier");
			responsilbeEmployeeCombo = new SComboField(null,150,
					new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name", false, getPropertyName("select"));
			contactField = new STextField(null, 150);
			contactField.setReadOnly(true);
			
			List departmentList=new ArrayList();
			departmentList.add(0, new DepartmentModel(0, "None"));
			departmentList.addAll(new DepartmentDao().getDepartments(getOrganizationID()));
			divisionCombo = new SSelectionField(null,getPropertyName("none"),200, 400);
			divisionCombo.setContainerData(new DivisionDao().getDivisionsHierarchy(getOrganizationID()));
			departmentCombo = new SComboField(null, 100, departmentList,"id", "name", false, getPropertyName("select"));
			departmentCombo.setValue((long)0);
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(purchaseInquiryCombo);
			salLisrLay.addComponent(createNewButton);
			
			SHorizontalLayout supplierLayout = new SHorizontalLayout();
			supplierLayout.addComponent(supplierCombo);
			supplierLayout.addComponent(newSupplierButton);
			
			masterDetailsGrid.addComponent(new SLabel("Inquiry No"), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(referenceNoField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			masterDetailsGrid.addComponent(dateField, 6, 0);
			
	
			if(settings.isDIVISION_ENABLED()){
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("Division")), 7, 0);
				masterDetailsGrid.addComponent(divisionCombo, 8, 0);
			}
			
			if(settings.isDEPARTMENT_ENABLED()){
				masterDetailsGrid.addComponent(new SLabel(getPropertyName("department")), 7,1);
				masterDetailsGrid.addComponent(departmentCombo, 8, 1);
			}
			
			
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(referenceNoField,Alignment.MIDDLE_LEFT);
			masterDetailsGrid.setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 1);
			masterDetailsGrid.setColumnExpandRatio(2, 1);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 1);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 1);
			masterDetailsGrid.setColumnExpandRatio(7, 1);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("supplier")), 1, 1);
			masterDetailsGrid.addComponent(supplierLayout, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("employee")), 3, 1);
			masterDetailsGrid.addComponent(responsilbeEmployeeCombo, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("contact")), 5, 1);
			masterDetailsGrid.addComponent(contactField, 6, 1);
			masterDetailsGrid.setStyleName("master_border");

			if(settings.isSHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE()){
				itemCombo = new SComboField(getPropertyName("item"), 100,
						null, "id", "name",true,getPropertyName("select"));
			} else {
				itemCombo = new SComboField(getPropertyName("item"), 100,
						itmDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()), "id", "name",true,getPropertyName("select"));
			}
//			itemCombo = new SComboField(getPropertyName("item"), 100,
//					itmDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()), "id", "name",true,getPropertyName("select"));
			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");
			newItemButton.setDescription("Add New Item");
			
			SHorizontalLayout itemLay = new SHorizontalLayout();
			itemLay.addComponent(itemCombo);
			itemLay.addComponent(newItemButton);
			
			itemLay.setComponentAlignment(newItemButton, Alignment.BOTTOM_CENTER);
			
			quantityField = new STextField(getPropertyName("qty"), 60);
			quantityField.setStyleName("textfield_align_right");
			quantityField.setValue("0");
			convertionQuantityField = new STextField(getPropertyName("convertion_qty"), 40);
			convertionQuantityField.setStyleName("textfield_align_right");
			convertionQuantityField.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");

			convertedQuantityField = new STextField(getPropertyName("converted_qty"), 60);
			convertedQuantityField.setStyleName("textfield_align_right");
			convertedQuantityField.setDescription("Converted Quantity in Basic Unit");
			convertedQuantityField.setReadOnly(true);
			
			
			
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			unitPriceField=new SCurrencyField("Unit Price", 50, getWorkingDate());
			unitPriceField.setStyleName("textfield_align_right");
			unitPriceField.currencySelect.setReadOnly(true);
			unitPriceField.setNewValue(getCurrencyID(), 0);
			
			netPriceField = new STextField(getPropertyName("net_price"),75);
			netPriceField.setNewValue("0.00");
			netPriceField.setStyleName("textfield_align_right");

			netPriceField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			itemLayout.addComponent(itemLay);
			itemLayout.addComponent(quantityField);
			itemLayout.addComponent(convertionQuantityField);
			itemLayout.addComponent(convertedQuantityField);
			itemLayout.addComponent(unitSelect);
			
			itemLayout.addComponent(unitPriceField);
			itemLayout.addComponent(netPriceField);
			itemLayout.addComponent(buttonLay);
			itemLayout.setComponentAlignment(buttonLay, Alignment.BOTTOM_CENTER);
			
			convertionQuantityField.setValue("1");
			convertionQuantityField.setVisible(false);
			convertedQuantityField.setVisible(false);
			convertionQuantityField.setImmediate(true);
			convertedQuantityField.setImmediate(true);

			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			itemLayout.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1100, 200);

			table.setMultiSelect(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null,TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_EDITABLE, Boolean.class, null,TBC_EDITABLE, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null, getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_CONVERTION_QTY, Double.class, null,"Convertion Qty", null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class, null,"Qty Basic Unit", null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null, TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null, getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null, getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CID, Long.class, null, TBC_CID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY, String.class, null, TBC_CURRENCY, null, Align.CENTER);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null, TBC_CONV_RATE, null, Align.LEFT);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null, getPropertyName("net_price"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, 0.35f);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 1);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 2);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT, 0.5f);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 1);
			table.setColumnExpandRatio(TBC_CURRENCY, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 1);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);
			// table.setEditable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("1100");
			table.setHeight("200");

			netAmountField = new SCurrencyField(null, 100, getWorkingDate());
			netAmountField.amountField.setReadOnly(true);
			netAmountField.setStyleName("textfield_align_right");
			convertedField = new SCurrencyField(null, 75,getWorkingDate());
			convertedField.setReadOnly(true);
			
			comment = new STextArea(null, 400, 70);

			bottomGrid.addComponent(new SLabel(""), 6, 0);
			bottomGrid.addComponent(new SLabel(getPropertyName("approximate_amount")), 6, 1);
			bottomGrid.addComponent(netAmountField, 9, 1);
			bottomGrid.addComponent(convertedField, 4, 1);
			convertedField.setVisible(false);
			
			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.setComponentAlignment(netAmountField,
					Alignment.TOP_RIGHT);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton
					.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton
					.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			cancelButton = new SButton(getPropertyName("cancel"), 78);
			cancelButton
					.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			cancelButton.setStyleName("deletebtnStyle");

			sendMailButton = new SButton(getPropertyName("send_mail"), 100);
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");

			printButton = new SButton(getPropertyName("print"), 78);
			printButton.setIcon(new ThemeResource("icons/print.png"));
			printButton.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveButton);
			mainButtonLayout.addComponent(updateButton);
			if (settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(cancelButton);
			else
				mainButtonLayout.addComponent(deleteButton);
			mainButtonLayout.addComponent(printButton);
			mainButtonLayout.addComponent(sendMailButton);

			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			cancelButton.setVisible(false);
			sendMailButton.setVisible(false);
			printButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(itemLayout);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			
			pannel.setContent(windowNotif);
			loadOptions(0);
			
			
			table.addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			
			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			
			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});

			
			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
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
				
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(dateField.getValue()!=null){
						if(previousDate.getTime()!=dateField.getValue().getTime()){
							final long id=(Long)netAmountField.currencySelect.getValue();
							final long cid=(Long)unitPriceField.currencySelect.getValue();
							if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID()){
								ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Update Currency Rate Accordingly.",new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (!dialog.isConfirmed()) {
											dateField.setValue(previousDate);
										}
										previousDate=dateField.getValue();
										netAmountField.setCurrencyDate(previousDate);
										unitPriceField.setCurrencyDate(previousDate);
										netAmountField.currencySelect.setValue(null);
										netAmountField.currencySelect.setValue(id);
										unitPriceField.currencySelect.setNewValue(null);
										unitPriceField.currencySelect.setNewValue(cid);
									}
								});
							}
							netAmountField.currencySelect.setValue(null);
							netAmountField.currencySelect.setValue(id);
							unitPriceField.currencySelect.setNewValue(null);
							unitPriceField.currencySelect.setNewValue(cid);
						}
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)purchaseInquiryCombo.getValue(),confirmBox.getUserID());
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
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
						if(purchaseInquiryCombo.getValue()!=null && !purchaseInquiryCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)purchaseInquiryCombo.getValue(),
									"Purchase Order : No. "+purchaseInquiryCombo.getItemCaption(purchaseInquiryCombo.getValue()));
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
						if(purchaseInquiryCombo.getValue()!=null && !purchaseInquiryCombo.getValue().toString().equals("0")) {
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
			
			
			netAmountField.currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(netAmountField.currencySelect.getValue()!=null){
							if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID()){
								convertedField.setVisible(true);
							}
							else{
								convertedField.setVisible(false);
							}
							netAmountField.setNewValue(convertedField.getValue()*netAmountField.getConversionRate());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			netAmountField.conversionField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(netAmountField.conversionField.getValue()!=null){
							netAmountField.setNewValue(convertedField.getValue()*netAmountField.getConversionRate());
						}
					} catch (ReadOnlyException e) {
						e.printStackTrace();
					}
				}
			});
			
			
			convertedField.amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						netAmountField.setNewValue(convertedField.getValue()*netAmountField.getConversionRate());
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}
			});
			netAmountField.setNewValue(getCurrencyID(), 0);
			
		

			
			newItemButton.addClickListener(new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					ItemPanel itemPanel = new ItemPanel();
					popupWindow.setContent(itemPanel);
					popupWindow.setId("ITEM");
					popupWindow.center();
					popupWindow.setCaption("Add New Item");
					getUI().getCurrent().addWindow(popupWindow);
				}
			});
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					try {
						
						List list=new ArrayList();
						list=new LedgerDao().getAllSuppliers(getOfficeID());
						SCollectionContainer bic=SCollectionContainer.setList(list, "id");
						supplierCombo.setContainerDataSource(bic);
						supplierCombo.setItemCaptionPropertyId("name");
						supplierCombo.setInputPrompt(getPropertyName("select"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};
			
			
			newSupplierButton.addClickListener(new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					AddSupplier supplier=new AddSupplier();
					supplier.center();
					supplier.setCaption("Add Supplier");
					getUI().getCurrent().addWindow(supplier);
					supplier.addCloseListener(closeListener);
				}
			});
			
			
			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					try {
						if (popupWindow.getId().equals("ITEM")) {
							List list=new ArrayList();
							list=itmDao.getAllActiveItemsWithAppendingItemCode(getOfficeID());
							SCollectionContainer bic=SCollectionContainer.setList(list, "id");
							itemCombo.setContainerDataSource(bic);
							itemCombo.setItemCaptionPropertyId("name");
							itemCombo.setInputPrompt(getPropertyName("select"));
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			});

			
			supplierCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (supplierCombo.getValue() != null) {
							supplierCombo.setDescription("<h1><i>Current Balance</i> : "+ comDao.getLedgerCurrentBalance((Long) supplierCombo.getValue()) + "</h1>");
							SupplierModel supObj = supDao.getSupplierFromLedger((Long) supplierCombo.getValue());
							responsilbeEmployeeCombo.setValue(supObj.getResponsible_person());
							contactField.setNewValue(supObj.getAddress().getPhone()+", "+supObj.getAddress().getMobile());
							
							if(settings.isSHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE()){
								table.removeAllItems();
								List<ItemModel> itemModelList = itemDao.getAllActiveItemsFromOfc(getOfficeID());
								List<ItemModel> itemUnderSupplierList = new ArrayList<ItemModel>();
								
								Iterator itr = itemModelList.iterator();
								long supplierId = (Long)supplierCombo.getValue();
								String[] supplierIdsStringArray; 
								while(itr.hasNext()){
									ItemModel model = (ItemModel) itr.next();
									if(model.getPreferred_vendor() == null || model.getPreferred_vendor().trim().length() == 0){
										continue;
									}
									supplierIdsStringArray = model.getPreferred_vendor().split(",");
									
									for(int i = 0; i< supplierIdsStringArray.length; i++){
										if(Long.parseLong(supplierIdsStringArray[i]) == supplierId){
											itemUnderSupplierList.add(model);
											break;
										}
									}
								}
								
								SCollectionContainer bic=SCollectionContainer.setList(itemUnderSupplierList, "id");
								itemCombo.setContainerDataSource(bic);
								itemCombo.setItemCaptionPropertyId("name");
							}
						} else{
							supplierCombo.setDescription(null);
							responsilbeEmployeeCombo.setValue(null);
							contactField.setNewValue("");
						}
						
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					purchaseInquiryCombo.setValue((long) 0);
					purchaseInquiryCombo.setValue(null);
				}
			});
			
			
			itemCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemCombo.getValue() != null) {
							ItemModel itm = itmDao.getItem((Long) itemCombo.getValue());
							SCollectionContainer bic = SCollectionContainer.setList(comDao.getAllItemUnitDetails(itm.getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");
							unitSelect.setValue(null);
							unitSelect.setValue(itm.getUnit().getId());
							quantityField.selectAll();
							quantityField.focus();
						}
						else{
							convertionQuantityField.setValue("1");
							convertedQuantityField.setVisible(false);
							convertionQuantityField.setVisible(false);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			
			unitSelect.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							if (itemCombo.getValue() != null) {

								ItemModel itm = new ItemDao().getItem((Long) itemCombo.getValue());
								if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
									convertionQuantityField.setValue("1");
									convertionQuantityField.setVisible(false);
									convertedQuantityField.setVisible(false);
								} 
								else {
									convertionQuantityField.setVisible(true);
									convertedQuantityField.setVisible(true);
									double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(), 0);
									convertionQuantityField.setValue(asString(cnvr_qty));
								}
								unitPriceField.setNewValue(comDao.getItemCurrency(itm.getId(), (Long) unitSelect.getValue(), (long)0),
										roundNumber(comDao.getItemPrice(itm.getId(), (Long) unitSelect.getValue(), (long)0)));
								if (quantityField.getValue() != null && !quantityField.getValue().toString().equals("0")) {
									convertedQuantityField.setNewValue(asString(Double.parseDouble(quantityField.getValue())
											* Double.parseDouble(convertionQuantityField.getValue())));
									netPriceField.setNewValue(roundNumberToString(unitPriceField.getValue()*toDouble(quantityField.getValue())));
								}
							}
						}
						else
							unitPriceField.setNewValue(getCurrencyID(), 0.0);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			unitPriceField.setImmediate(true);
			quantityField.setImmediate(true);
			quantityField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemCombo.getValue() != null) {

							ItemModel itm = new ItemDao().getItem((Long) itemCombo.getValue());
							if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
								convertionQuantityField.setValue("1");
								convertionQuantityField.setVisible(false);
								convertedQuantityField.setVisible(false);
							} 
							else {
								convertionQuantityField.setVisible(true);
								convertedQuantityField.setVisible(true);
								double cnvr_qty = comDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(), 0);
								convertionQuantityField.setValue(asString(cnvr_qty));
							}
							if (quantityField.getValue() != null && !quantityField.getValue().toString().equals("0")) {
								convertedQuantityField.setNewValue(asString(Double.parseDouble(quantityField.getValue())
										* Double.parseDouble(convertionQuantityField.getValue())));
								netPriceField.setNewValue(roundNumberToString(unitPriceField.getValue()*toDouble(quantityField.getValue())));
							}
						}
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			convertionQuantityField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (convertionQuantityField.getValue().equals("") || toDouble(convertionQuantityField.getValue()) <= 0) {
							convertionQuantityField.setValue("1");
						}
						calculateNetPrice();
					} catch (Exception e) {
						convertionQuantityField.setValue("1");
						e.printStackTrace();
					}
				}
			});

			
			unitPriceField.amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {
							table.setVisibleColumns(allHeaders);
							
							ItemModel itemModel = itemDao.getItem((Long) itemCombo.getValue());
							UnitModel unitModel = untDao.getUnit((Long) unitSelect.getValue());
							double qty=toDouble(quantityField.getValue().toString());
							double amount=unitPriceField.getValue();
							double netPrice=(unitPriceField.getValue()*qty);
							String currency=new CurrencyManagementDao().getselecteditem(unitPriceField.getCurrency()).getCode();
							double conv_rat;
							try {
								conv_rat = toDouble(convertionQuantityField.getValue());
							} catch (Exception e) {
								conv_rat=1;
							}
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										true,
										itemModel.getId(),
										itemModel.getItem_code(),
										itemModel.getName(),
										roundNumber(qty),
										roundNumber(conv_rat),
										roundNumber(conv_rat*qty),
										unitModel.getId(),
										unitModel.getSymbol(),
										roundNumber(amount),
										unitPriceField.getCurrency(),
										currency,
										roundNumber(unitPriceField.getConversionRate()),
										roundNumber(netPrice)},table.getItemIds().size()+1);
							itemCombo.setValue(null);
							quantityField.setValue("0");
							unitSelect.setValue(null);
							unitPriceField.setNewValue(getCurrencyID(),0.0);
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						setRequiredError(itemCombo, null, false);
						Collection selectedItems = null;
						if (table.getValue() != null) {
							selectedItems = (Collection) table.getValue();
						}
						if (selectedItems != null && selectedItems.size() == 1) {
							Item item = table.getItem(selectedItems.iterator().next());
							itemCombo.setValue((Long)item.getItemProperty(TBC_ITEM_ID).getValue());
							quantityField.setValue(roundNumber((Double)item.getItemProperty(TBC_QTY).getValue())+"");
							unitSelect.setValue((Long)item.getItemProperty(TBC_UNIT_ID).getValue());
							unitPriceField.setNewValue((Long)item.getItemProperty(TBC_CID).getValue(), 
													roundNumber((Double)item.getItemProperty(TBC_UNIT_PRICE).getValue()));
							netPriceField.setNewValue(roundNumberToString((Double)item.getItemProperty(TBC_NET_PRICE).getValue()));
							convertionQuantityField.setNewValue(""+ roundNumber((Double)item.getItemProperty(TBC_CONVERTION_QTY).getValue()));
							if(!(Boolean)item.getItemProperty(TBC_EDITABLE).getValue()){
								addItemButton.setVisible(false);
								updateItemButton.setVisible(false);
								setRequiredError(itemCombo, "Quotation Created On This Enquiry. Cannot Edit or Delete", true);
							}
							else{
								addItemButton.setVisible(false);
								updateItemButton.setVisible(true);
							}
						}
						else{
							setRequiredError(itemCombo, null, false);
							itemCombo.setValue(null);
							quantityField.setValue("0");
							unitSelect.setValue(null);
							unitPriceField.setNewValue(getCurrencyID(),0.0);
							netPriceField.setNewValue("0");
							addItemButton.setVisible(true);
							updateItemButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			updateItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid()) {
							Collection selectedItems = null;
							if (table.getValue() != null) {
								selectedItems = (Collection) table.getValue();
							}
							if (selectedItems != null && selectedItems.size() == 1) {
								Item item = table.getItem(selectedItems.iterator().next());
								
								ItemModel itemModel = itemDao.getItem((Long) itemCombo.getValue());
								UnitModel unitModel = untDao.getUnit((Long) unitSelect.getValue());
								double qty=toDouble(quantityField.getValue().toString());
								double amount=unitPriceField.getValue();
								double netPrice=(unitPriceField.getValue()*qty);
								String currency=new CurrencyManagementDao().getselecteditem(unitPriceField.getCurrency()).getCode();
								double conv_rat;
								try {
									conv_rat = toDouble(convertionQuantityField.getValue());
								} catch (Exception e) {
									conv_rat=1;
								}
									item.getItemProperty(TBC_ITEM_ID).setValue(itemModel.getId());
									item.getItemProperty(TBC_ITEM_CODE).setValue(itemModel.getItem_code());
									item.getItemProperty(TBC_ITEM_NAME).setValue(itemModel.getName());
									item.getItemProperty(TBC_QTY).setValue(roundNumber(qty));
									item.getItemProperty(TBC_CONVERTION_QTY).setValue(roundNumber(conv_rat));
									item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).setValue(roundNumber(qty*conv_rat));
									item.getItemProperty(TBC_UNIT_ID).setValue(unitModel.getId());
									item.getItemProperty(TBC_UNIT).setValue(unitModel.getSymbol());
									item.getItemProperty(TBC_UNIT_PRICE).setValue(roundNumber(amount));
									item.getItemProperty(TBC_CID).setValue(unitPriceField.getCurrency());
									item.getItemProperty(TBC_CURRENCY).setValue(currency);
									item.getItemProperty(TBC_CONV_RATE).setValue(roundNumber(unitPriceField.getConversionRate()));
									item.getItemProperty(TBC_NET_PRICE).setValue(roundNumber(netPrice));
									itemCombo.setValue(null);
									quantityField.setValue("0");
									unitSelect.setValue(null);
									unitPriceField.setNewValue(getCurrencyID(),0.0);
									netPriceField.setNewValue("0");
									addItemButton.setVisible(true);
									updateItemButton.setVisible(false);
							}
							calculateTotals();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							boolean savable=true;
							PurchaseInquiryModel mdl=new PurchaseInquiryModel();
							List<PurchaseInquiryDetailsModel> itemsList = new ArrayList<PurchaseInquiryDetailsModel>();
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								PurchaseInquiryDetailsModel det=new PurchaseInquiryDetailsModel();
								Item item = table.getItem(it.next());
								double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
								if(qty<=0){
									savable=false;
									break;
								}
								det.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
								det.setQunatity(roundNumber((Double) item.getItemProperty(TBC_QTY).getValue()));
								det.setQty_in_basic_unit(roundNumber((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue()));
								det.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
								det.setUnit_price(roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()));
								det.setCurrencyId((Long) item.getItemProperty(TBC_CID).getValue());
								det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONV_RATE).getValue()));
								itemsList.add(det);
							}
							if(savable){
								mdl.setInquiry_no(getNextSequence("Purchase Inquiry No", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setSupplier(new LedgerModel((Long)supplierCombo.getValue()));
								mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setComments(comment.getValue());
								mdl.setAmount(roundNumber(netAmountField.getValue()));
								mdl.setCurrencyId(netAmountField.getCurrency());
								mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
								mdl.setActive(true);
								mdl.setDepartment_id((Long)departmentCombo.getValue());
								mdl.setDivision_id((Long)divisionCombo.getValue());
								
								mdl.setInquiry_details_list(itemsList);
								long id=dao.save(mdl);
								loadOptions(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),
										"Purchase Inquiry Created. Inquiry No : "
												+ mdl.getInquiry_no()+ ", Supplier : "
												+ supplierCombo.getItemCaption(supplierCombo.getValue())
												+ ", Approximate Amount : "+ mdl.getAmount(),id);
							}
							else
								Notification.show("Check Item Quantity",Type.ERROR_MESSAGE);
							
						}
					} catch (Exception e) {
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}

				}
			});

			
			purchaseInquiryCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						table.removeAllItems();
						calculateTotals();
						netAmountField.setCurrency(getCurrencyID());
						table.setValue(null);
						referenceNoField.setValue("");
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						netAmountField.setCurrencyDate(getWorkingDate());
						supplierCombo.setValue(null);
						responsilbeEmployeeCombo.setValue(null);
						departmentCombo.setValue((long)0);
						divisionCombo.setValue(null);
						
						comment.setValue("");
						saveButton.setVisible(true);
						printButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						sendMailButton.setVisible(false);
						if(purchaseInquiryCombo.getValue()!=null && !purchaseInquiryCombo.getValue().toString().equals("0")){
							table.setVisibleColumns(allHeaders);
							PurchaseInquiryModel mdl=dao.getPurchaseInquiryModel((Long)purchaseInquiryCombo.getValue());
							referenceNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							netAmountField.setCurrencyDate(mdl.getDate());
							supplierCombo.setValue(mdl.getSupplier().getId());
							responsilbeEmployeeCombo.setValue(mdl.getResponsible_employee());
							comment.setValue(mdl.getComments());
							departmentCombo.setValue(mdl.getDepartment_id());
							divisionCombo.setValue(mdl.getDivision_id());
							Iterator itr=mdl.getInquiry_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseInquiryDetailsModel det=(PurchaseInquiryDetailsModel)itr.next();
								String currency=new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getCode();
								
								boolean editable=true;
								if(det.getLock_count()>0)		
									editable=false;
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										det.getId(),
										editable,
										det.getItem().getId(),
										det.getItem().getItem_code(),
										det.getItem().getName(),
										roundNumber(det.getQunatity()),
										roundNumber(det.getQty_in_basic_unit()/det.getQunatity()),
										roundNumber(det.getQty_in_basic_unit()),
										det.getUnit().getId(),
										det.getUnit().getSymbol(),
										roundNumber(det.getUnit_price()),
										det.getCurrencyId(),
										currency,
										roundNumber(det.getConversionRate()),
										roundNumber(det.getUnit_price()*det.getQunatity())},table.getItemIds().size()+1);
							}
							calculateTotals();
							table.setVisibleColumns(requiredHeaders);
							netAmountField.setCurrency(mdl.getCurrencyId());
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							printButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);
							sendMailButton.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			
			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if(isValid()){
							if(purchaseInquiryCombo.getValue()!=null && !purchaseInquiryCombo.getValue().toString().equals("0")){
								PurchaseInquiryModel mdl=dao.getPurchaseInquiryModel((Long)purchaseInquiryCombo.getValue());
								boolean savable=true;
								List<PurchaseInquiryDetailsModel> itemsList = new ArrayList<PurchaseInquiryDetailsModel>();
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									PurchaseInquiryDetailsModel det=null;
									Item item = table.getItem(it.next());
									long id=(Long) item.getItemProperty(TBC_ID).getValue();
									if(id!=0)
										det=dao.getPurchaseInquiryDetailsModel(id);
									if(det==null)
										det=new PurchaseInquiryDetailsModel();
									double qty=(Double) item.getItemProperty(TBC_QTY).getValue();
									if(qty<=0){
										savable=false;
										break;
									}
									det.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
									det.setQunatity(roundNumber((Double) item.getItemProperty(TBC_QTY).getValue()));
									det.setQty_in_basic_unit(roundNumber((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue()));
									det.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
									det.setUnit_price(roundNumber((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()));
									det.setCurrencyId((Long) item.getItemProperty(TBC_CID).getValue());
									det.setConversionRate(roundNumber((Double) item.getItemProperty(TBC_CONV_RATE).getValue()));
									itemsList.add(det);
								}
								if(savable){
									mdl.setRef_no(referenceNoField.getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									mdl.setSupplier(new LedgerModel((Long)supplierCombo.getValue()));
									mdl.setResponsible_employee((Long)responsilbeEmployeeCombo.getValue());
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									mdl.setComments(comment.getValue());
									mdl.setAmount(roundNumber(netAmountField.getValue()));
									mdl.setCurrencyId(netAmountField.getCurrency());
									mdl.setConversionRate(roundNumber(netAmountField.getConversionRate()));
									mdl.setDepartment_id((Long)departmentCombo.getValue());
									mdl.setDivision_id((Long)divisionCombo.getValue());
									
									mdl.setInquiry_details_list(itemsList);
									dao.update(mdl);
									Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									loadOptions(mdl.getId());
									saveActivity(getOptionId(),
											"Purchase Inquiry Updated. Inquiry No : "
													+ mdl.getInquiry_no()+ ", Supplier : "
													+ supplierCombo.getItemCaption(supplierCombo.getValue())
													+ ", Approximate Amount : "+ mdl.getAmount(),mdl.getId());
								}
								else
									Notification.show("Check Item Quantity",Type.ERROR_MESSAGE);
								
							}
							
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}

				}
			});

			
			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (purchaseInquiryCombo.getValue() != null && !purchaseInquiryCombo.getValue().toString().equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										PurchaseInquiryModel mdl=dao.getPurchaseInquiryModel((Long)purchaseInquiryCombo.getValue());
										if(mdl.getLock_count()>0){
											Notification.show("Quotation Created On This Inquiry. Cannot Delete",Type.ERROR_MESSAGE);
										}
										else{
											dao.delete(mdl);
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											saveActivity(getOptionId()," Purchase Inquiry deleted. Inquiry No : "
														+ purchaseInquiryCombo.getItemCaption(purchaseInquiryCombo.getValue())
														+ ", Supplier : "+supplierCombo.getItemCaption(supplierCombo.getValue()),
														(Long)purchaseInquiryCombo.getValue());
											loadOptions(0);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				}
			});

			
			cancelButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (purchaseInquiryCombo.getValue() != null && !purchaseInquiryCombo.getValue().toString().equals("0")) {
						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										PurchaseInquiryModel mdl=dao.getPurchaseInquiryModel((Long)purchaseInquiryCombo.getValue());
										if(mdl.getLock_count()>0){
											Notification.show("Quotation Created On This Inquiry. Cannot Cancel",Type.ERROR_MESSAGE);
										}
										else{
											dao.cancel(mdl);
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											saveActivity(getOptionId()," Purchase Inquiry Cancelled. Inquiry No : "
														+ purchaseInquiryCombo.getItemCaption(purchaseInquiryCombo.getValue())
														+ ", Supplier : "+supplierCombo.getItemCaption(supplierCombo.getValue()),
														(Long)purchaseInquiryCombo.getValue());
											loadOptions(0);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					}

				}
			});

			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.F4, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			
			if (!isFinYearBackEntry()) {
				saveButton.setVisible(false);
				updateButton.setVisible(false);
				deleteButton.setVisible(false);
				cancelButton.setVisible(false);
				sendMailButton.setVisible(false);
				printButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),Type.WARNING_MESSAGE);
			}

			
			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						if(purchaseInquiryCombo.getValue()!=null && !purchaseInquiryCombo.getValue().toString().equals("0")){
							PurchaseInquiryModel mdl=dao.getPurchaseInquiryModel((Long)purchaseInquiryCombo.getValue());
							SupplierModel ledger = supDao.getSupplierFromLedger(toLong(supplierCombo.getValue().toString()));
							String address = "";
							if (ledger != null) {
								address = new AddressBusiness().getAddressString(ledger.getAddress().getId());
							}
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_inquiry"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase_inquiry"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
							map.put("ADDRESS", address);
							map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("BILL_NO", mdl.getInquiry_no());
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("NET_PRICE_LABEL", getPropertyName("net_price"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							S_OfficeModel office = new OfficeDao().getOffice(mdl.getOffice().getId());
							map.put("OFFICE_NAME", office.getName());
							
							try {
								File headFile=new File(rootPath.trim()+office.getHeader().trim());
								if(headFile.exists()&& !headFile.isDirectory())
									map.put("HEADER", rootPath.trim()+office.getHeader().trim());
								else
									map.put("HEADER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("HEADER", rootPath.trim()+"blank.png");
							}
							
							try {
								File footFile=new File(rootPath.trim()+office.getFooter().trim());
								if(footFile.exists()&& !footFile.isDirectory())
									map.put("FOOTER", rootPath.trim()+office.getFooter().trim());
								else
									map.put("FOOTER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("FOOTER", rootPath.trim()+"blank.png");
							}
							
							Iterator itr=mdl.getInquiry_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseInquiryDetailsModel det = (PurchaseInquiryDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrencyId()).getCode());
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE_ORDER));
							report.setReportFileName("Purchase Inquiry Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.print();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			sendMailButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						HashMap<String, Object> map = new HashMap<String, Object>();
						List<Object> reportList = new ArrayList<Object>();
						
						if(purchaseInquiryCombo.getValue()!=null && !purchaseInquiryCombo.getValue().toString().equals("0")){
							PurchaseInquiryModel mdl=dao.getPurchaseInquiryModel((Long)purchaseInquiryCombo.getValue());
							SupplierModel ledger = supDao.getSupplierFromLedger(toLong(supplierCombo.getValue().toString()));
							String addr = "";
							if (ledger != null) {
								addr = new AddressBusiness().getAddressString(ledger.getAddress().getId());
							}
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_inquiry"));
//							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("purchase_inquiry"));
							
							map.put("LEDGER_NAME_LABEL", getPropertyName("supplier"));
							map.put("LEDGER", mdl.getSupplier().getName());
							map.put("ADDRESS", addr);
							map.put("EMPLOYEE_NAME_LABEL", getPropertyName("sales_man"));
							try {
								UserModel user=new UserManagementDao().getUser(mdl.getResponsible_employee());
								map.put("EMPLOYEE", user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
							} catch (Exception e) {
								map.put("EMPLOYEE", "");
							}
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("INVOICE_DATE", CommonUtil.formatDateToDDMMYYYY(mdl.getDate()));
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("BILL_NO", mdl.getInquiry_no());
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("QUANTITY_LABEL", getPropertyName("quantity"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("UNIT_PRICE_LABEL", getPropertyName("unit_price"));
							map.put("NET_PRICE_LABEL", getPropertyName("net_price"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							S_OfficeModel office = new OfficeDao().getOffice(mdl.getOffice().getId());
							map.put("OFFICE_NAME", office.getName());
							
							try {
								File headFile=new File(rootPath.trim()+office.getHeader().trim());
								if(headFile.exists()&& !headFile.isDirectory())
									map.put("HEADER", rootPath.trim()+office.getHeader().trim());
								else
									map.put("HEADER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("HEADER", rootPath.trim()+"blank.png");
							}
							
							try {
								File footFile=new File(rootPath.trim()+office.getFooter().trim());
								if(footFile.exists()&& !footFile.isDirectory())
									map.put("FOOTER", rootPath.trim()+office.getFooter().trim());
								else
									map.put("FOOTER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("FOOTER", rootPath.trim()+"blank.png");
							}
							
							Iterator itr=mdl.getInquiry_details_list().iterator();
							while (itr.hasNext()) {
								PurchaseInquiryDetailsModel det = (PurchaseInquiryDetailsModel) itr.next();
								AcctReportMainBean bean = new AcctReportMainBean();
								bean.setItem(det.getItem().getName()+" ["+det.getItem().getItem_code()+"]");
								bean.setQuantity(roundNumber(det.getQunatity()));
								bean.setUnit(det.getUnit().getSymbol());
								bean.setAmount(roundNumber(det.getUnit_price()));
								bean.setTotal(roundNumber(det.getUnit_price()*det.getQunatity()/det.getConversionRate()));
								reportList.add(bean);
							}
							map.put("TOTAL", roundNumber(mdl.getAmount()));
							map.put("CURRENCY", new CurrencyManagementDao().getselecteditem(mdl.getCurrencyId()).getCode());
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName(getBillName(SConstants.bills.PURCHASE_ORDER));
							report.setReportFileName("Purchase Inquiry Print");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.print();
							
							InternetAddress toAddr=null;
							Address[] mailTo=new Address[1];
							try {
								toAddr=new InternetAddress(ledger.getAddress().getEmail());
								mailTo[0]=toAddr;
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(toAddr!=null) {
								SMail mail=new SMail();
								File file=new File(report.getReportFile());
								EmailConfigurationModel configModel=new EmailConfigDao().getEmailConfiguration(getUserID());
								if(configModel!=null){
									if(file.exists())
										mail.sendMailWithFromAddress(mailTo, 
																	"You have a new Purchase Inquiry from "+ledger.getName()+" Bill No. "+mdl.getInquiry_no(),
																	"Purchase Inquiry",
																	file, 
																	getUserID());
									Notification.show("Email Successfully Sent to "+toAddr,Type.TRAY_NOTIFICATION);
								}
								else
									SNotification.show("Configure Email", Type.ERROR_MESSAGE);
							}
							else 
								Notification.show("Invalid Email-Id. Please enter valid Email-Id.",Type.ERROR_MESSAGE);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return pannel;
	}

	
	@SuppressWarnings("rawtypes")
	public void calculateTotals() {
		try {
			double qty_ttl = 0, netTotal = 0;
			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
				netTotal += (((Double) item.getItemProperty(TBC_UNIT_PRICE).getValue()/
						(Double) item.getItemProperty(TBC_CONV_RATE).getValue())*
						(Double) item.getItemProperty(TBC_QTY).getValue());
			}
			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, roundNumberToString(netTotal));
			netAmountField.setNewValue(netTotal);
			convertedField.setNewValue(roundNumber(netTotal));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (itemCombo.getValue() == null || itemCombo.getValue().equals("")) {
				setRequiredError(itemCombo, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(itemCombo, null, false);
			
			if (unitSelect.getValue() == null || unitSelect.getValue().equals("")) {
				setRequiredError(unitSelect, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(unitSelect, null, false);

			if (quantityField.getValue() == null || quantityField.getValue().equals("")) {
				setRequiredError(quantityField, getPropertyName("invalid_data"), true);
				ret = false;
			} else {
				try {
					if (Double.parseDouble(quantityField.getValue()) <=0) {
						setRequiredError(quantityField, getPropertyName("invalid_data"), true);
						ret = false;
					}
					else
						setRequiredError(quantityField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityField, getPropertyName("invalid_data"), true);
					ret = false;
				}
			}
			
			if (convertionQuantityField.getValue() == null || convertionQuantityField.getValue().equals("")) {
				setRequiredError(convertionQuantityField, getPropertyName("invalid_data"), true);
				ret = false;
			} else {
				try {
					if (Double.parseDouble(convertionQuantityField.getValue()) <=0) {
						setRequiredError(convertionQuantityField, getPropertyName("invalid_data"), true);
						ret = false;
					}
					else
						setRequiredError(convertionQuantityField, null, false);
				} catch (Exception e) {
					setRequiredError(convertionQuantityField, getPropertyName("invalid_data"), true);
					ret = false;
				}
			}
			
			if(dateField.getValue()==null || dateField.getValue().toString().equals("")){
				ret=false;
				setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			}
			else
				setRequiredError(dateField, null, false);
			
			if(dateField.getValue()!=null){
				if(!unitPriceField.isFieldValid(dateField.getValue()))
					ret=false;
			}
			
			if(unitPriceField.getValue()<0){
				unitPriceField.setNewValue(0.0);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;

	}
	
	
	public void visibleAddupdatePOButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				Collection selectedItems = (Collection) table.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					Object obj=it1.next();
					Item item=table.getItem(obj);
					if((Boolean)item.getItemProperty(TBC_EDITABLE).getValue())
						table.removeItem(obj);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.add(new PurchaseInquiryModel(0, "----Create New-----"));
			list.addAll(dao.getPurchaseInquiryModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			purchaseInquiryCombo.setContainerDataSource(bic);
			purchaseInquiryCombo.setItemCaptionPropertyId("inquiry_no");
			purchaseInquiryCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {

		boolean ret = true;
		
		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (responsilbeEmployeeCombo.getValue() == null || responsilbeEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsilbeEmployeeCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(responsilbeEmployeeCombo, null, false);

		if (supplierCombo.getValue() == null || supplierCombo.getValue().equals("")) {
			setRequiredError(supplierCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(supplierCombo, null, false);
		
		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, "Select a Date", true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
		if(settings.isDEPARTMENT_ENABLED()){
			if (departmentCombo.getValue() == null || departmentCombo.getValue().equals("")) 
				departmentCombo.setValue((long)0);	
		}
		else
			departmentCombo.setValue((long)0);
		
		if((Long)netAmountField.currencySelect.getValue()!=getCurrencyID() && dateField.getValue() != null) {
			
			try {
				double value=new CommonMethodsDao().getCurrencyRate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 
															(Long)netAmountField.currencySelect.getValue());
				if(value==0){
					setRequiredError(netAmountField.rateButton, "Set Conversion Rate", true);
					ret = false;
				}
				else
					setRequiredError(netAmountField.rateButton, null, false);
				
			} catch (Exception e) {
				setRequiredError(netAmountField.rateButton, "Set Conversion Rate", true);
				ret = false;
				e.printStackTrace();
			}
		}

		return ret;
	}

	
	public void calculateNetPrice(){
		if (quantityField.getValue() == null || quantityField.getValue().toString().equals("")) {
			quantityField.setValue("0");
		} else {
			try {
				if (Double.parseDouble(quantityField.getValue()) <0) {
					quantityField.setValue("0");
				} 
			} catch (Exception e) {
				quantityField.setValue("0");
			}
		}
		if(unitPriceField.getValue()<0){
			unitPriceField.setNewValue(0);
		}
		double netPrice;
		try {
			netPrice = toDouble(quantityField.getValue().toString())*unitPriceField.getValue();
		} catch (Exception e) {
			netPrice=0;
		}
		netPriceField.setNewValue(roundNumberToString(netPrice));
	}
	
	
	public void removeAllErrors() {
		if (unitPriceField.getComponentError() != null)
			setRequiredError(unitPriceField, null, false);
		if (quantityField.getComponentError() != null)
			setRequiredError(quantityField, null, false);
		if (itemCombo.getComponentError() != null)
			setRequiredError(itemCombo, null, false);
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		sendMailButton.setComponentError(null);
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	public SComboField getPurchaseOrderNumberList() {
		return purchaseInquiryCombo;
	}

	
	public void setPurchaseOrderNumberList(SComboField purchaseInquiryCombo) {
		this.purchaseInquiryCombo = purchaseInquiryCombo;
	}
	
	
	@Override
	public SComboField getBillNoFiled() {
		return purchaseInquiryCombo;
	}

}
