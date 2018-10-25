package com.inventory.sales.ui;

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
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ManufacturingDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.BuildingDao;
import com.inventory.model.BuildingModel;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Mar 31, 2014
 */
public class SalesOrderManufacturingUI extends SparkLogic {

	private static final long serialVersionUID = 3333522117558365747L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "TaxPerc";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_DISCOUNT = "DISCOUNT";
	// static String TBC_STOCK_ID="stock_id";

	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";

	static String POP_TBL_ITEM = "Item";
	static String POP_TBL_QTY = "Required Quantity";
	static String POP_TBL_AVAIL = "Available Quantity";

	SettingsValuePojo settings;
	SalesOrderDao daoObj;

	SComboField salesOrderNumberList;

	SPanel pannel;
	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	SComboSearchField a;

	STable table;

	private SButton newSaleButton;
	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	SComboField employSelect;

	SComboField itemSelectCombo;
	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField netPriceTextField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSOButton;
	SButton updateSOButton;
	SButton deleteSOButton;
	SButton cancelSOButton;
	SButton printButton;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	STextField referenceNoTextField;
	SComboField buildingSelect;
	SComboField customerSelect;
	SDateField date;
	SDateField expected_delivery_date;

	SNativeSelect salesTypeSelect;

	STextField grandTotalAmtTextField;
	STextArea comment;

	private String[] allHeaders;
	private String[] requiredHeaders;

	boolean taxEnable = isTaxEnable();

	CommonMethodsDao comDao;
	TaxDao taxDao;

	private SButton stockAvailBtn;
	private SPopupView popup;
	private STable popTable;
	private SFormLayout popForm;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		comDao = new CommonMethodsDao();
		taxDao = new TaxDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new SO");

		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute(
					"settings");

		taxEnable = isTaxEnable();

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_QTY_IN_BASIC_UNI };

		if (taxEnable) {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
					TBC_TAX_AMT, TBC_DISCOUNT, TBC_NET_PRICE };
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE,
					TBC_DISCOUNT, TBC_NET_PRICE };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}

		requiredHeaders = templist.toArray(new String[templist.size()]);

		setSize(1200, 618);

		daoObj = new SalesOrderDao();

		pannel = new SPanel();
		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(8);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(3);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(8);
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

			employSelect = new SComboField(
					null,
					125,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
									getOfficeID(), getOrganizationID()), "id",
					"first_name");
			employSelect.setValue(getLoginID());

			if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin())
				employSelect.setReadOnly(true);

			List list = new ArrayList();
			list.add(new SalesOrderModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesOrdersUnderOffice(getOfficeID()));
			salesOrderNumberList = new SComboField(null, 125, list, "id",
					"order_no", false, "Create New");

			referenceNoTextField = new STextField(null, 120);
			date = new SDateField(null, 120, getDateFormat(), new Date(
					getWorkingDate().getTime()));
			expected_delivery_date = new SDateField(null, 120, getDateFormat(),
					new Date(getWorkingDate().getTime()));

			buildingSelect = new SComboField(
					null,
					160,
					new BuildingDao()
							.getAllActiveBuildingNamesUnderOffice(getOfficeID()),
					"id", "name", true, "Select");
			customerSelect = new SComboField(null, 250,
					new LedgerDao().getAllCustomers(getOfficeID()), "id",
					"name", true, "Select");

			salesTypeSelect = new SNativeSelect(null, 140,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");
			salesTypeSelect.setValue(salesTypeSelect.getItemIds().iterator()
					.next());

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(salesOrderNumberList);
			salLisrLay.addComponent(newSaleButton);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_order_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(referenceNoTextField, 4, 0);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_type")), 1, 2);
			masterDetailsGrid.addComponent(salesTypeSelect, 2, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("employee")), 3, 2);
			masterDetailsGrid.addComponent(employSelect, 4, 2);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(referenceNoTextField,
					Alignment.MIDDLE_LEFT);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 1);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("customer")), 1, 1);
			masterDetailsGrid.addComponent(customerSelect, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("building")), 3, 1);
			masterDetailsGrid.addComponent(buildingSelect, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("expected_delivery_date")), 6, 1);
			masterDetailsGrid.addComponent(expected_delivery_date, 8, 1);

			masterDetailsGrid.setStyleName("master_border");

			popForm = new SFormLayout();
			popup = new SPopupView(null, popForm);

			popTable = new STable(null, 500, 250);
			popTable.addContainerProperty(POP_TBL_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBL_QTY, String.class, null,
					getPropertyName("quantity"), null, Align.CENTER);
			popTable.addContainerProperty(POP_TBL_AVAIL, String.class, null,
					getPropertyName("available_quantity"), null, Align.CENTER);
			popForm.addComponent(popTable);

			SHorizontalLayout hlay = new SHorizontalLayout();
			itemSelectCombo = new SComboField(getPropertyName("item"), 250,
					daoObj.getAllItems(getOfficeID()), "id", "name", true,
					"Select");

			stockAvailBtn = new SButton(null, "Available Raw materials");
			stockAvailBtn.setStyleName("loadAllBtnStyle");
			hlay.addComponent(itemSelectCombo);
			hlay.addComponent(stockAvailBtn);
			hlay.addComponent(popup);
			hlay.setComponentAlignment(stockAvailBtn, Alignment.BOTTOM_LEFT);

			quantityTextField = new STextField(getPropertyName("quantity"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setValue("0.00");
			unitPriceTextField.setStyleName("textfield_align_right");

			if (taxEnable) {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80,
						taxDao.getAllActiveTaxesFromType(getOfficeID(),
								SConstants.tax.SALES_TAX), "id", "name");
				taxSelect.setVisible(true);
			} else {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80, null,
						"id", "name");
				taxSelect.setVisible(false);
			}

			discountTextField = new STextField(getPropertyName("discount"), 80,
					"0.0");
			discountTextField.setStyleName("textfield_align_right");
			netPriceTextField = new STextField(getPropertyName("net_price"),
					100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("textfield_align_right");

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(hlay);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			addingGrid.addComponent(unitPriceTextField);
			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(discountTextField);
			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 2);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(7, 3);

			addingGrid.setWidth("1130");
			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1000, 200);

			table.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					getPropertyName("item-code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,
					getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,
					getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null,
					TBC_TAX_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TAX_PERC, Double.class, null,
					getPropertyName("tax_percentage"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_AMT, Double.class, null,
					getPropertyName("tax_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_DISCOUNT, Double.class, null,
					getPropertyName("discount"), null, Align.CENTER);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null,
					getPropertyName("net_price"), null, Align.RIGHT);
			// table.addContainerProperty(TBC_STOCK_ID, Long.class,
			// null,TBC_STOCK_ID, null, Align.CENTER);

			table.addContainerProperty(TBC_QTY_IN_BASIC_UNI, Double.class,
					null, getPropertyName("qty_basic_unit"), null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, 1);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 4);
			table.setColumnExpandRatio(TBC_QTY, 2);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 2);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 3);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);
			// table.setEditable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, "Total :");
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_DISCOUNT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1130");
			table.setHeight("200");

			grandTotalAmtTextField = new STextField(null, 200, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");
			comment = new STextArea(null, 400, 70);

			bottomGrid.addComponent(new SLabel(""), 6, 0);
			bottomGrid.addComponent(
					new SLabel(getPropertyName("total_amount")), 6, 1);
			bottomGrid.addComponent(grandTotalAmtTextField, 7, 1);
			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			saveSOButton = new SButton(getPropertyName("save"), 70);
			saveSOButton.setStyleName("savebtnStyle");
			saveSOButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateSOButton = new SButton(getPropertyName("update"), 80);
			updateSOButton
					.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateSOButton.setStyleName("updatebtnStyle");

			deleteSOButton = new SButton(getPropertyName("delete"), 78);
			deleteSOButton
					.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteSOButton.setStyleName("deletebtnStyle");

			cancelSOButton = new SButton(getPropertyName("cancel"), 78);
			cancelSOButton
					.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			cancelSOButton.setStyleName("deletebtnStyle");

			printButton = new SButton(getPropertyName("print"), 78);

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveSOButton);
			mainButtonLayout.addComponent(updateSOButton);
			if (!settings.isKEEP_DELETED_DATA())
				mainButtonLayout.addComponent(deleteSOButton);
			else
				mainButtonLayout.addComponent(cancelSOButton);
			mainButtonLayout.addComponent(printButton);

			updateSOButton.setVisible(false);
			deleteSOButton.setVisible(false);
			cancelSOButton.setVisible(false);
			printButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			customerSelect.focus();

			pannel.setContent(hLayout);

			stockAvailBtn.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						itemSelectCombo.setComponentError(null);
						popTable.removeAllItems();
						if (itemSelectCombo.getValue() != null
								&& !itemSelectCombo.getValue().equals("")) {
							List mapList = new ManufacturingDao()
									.getItemForManufacturing((Long) itemSelectCombo
											.getValue());
							if (mapList != null) {
								ManufacturingMapModel mapMdl;
								Object[] row;
								Iterator iter = mapList.iterator();
								while (iter.hasNext()) {
									mapMdl = (ManufacturingMapModel) iter
											.next();

									row = new Object[] {
											mapMdl.getSubItem().getName(),
											mapMdl.getQuantity()
													+ " "
													+ mapMdl.getUnit()
															.getSymbol(),
											(mapMdl.getSubItem()
													.getCurrent_balalnce() - mapMdl
													.getSubItem()
													.getReservedQuantity())
													+ " "
													+ mapMdl.getSubItem()
															.getUnit()
															.getSymbol() };
									popTable.addItem(row, popTable.getItemIds()
											.size() + 1);
								}
							}
							popup.setPopupVisible(true);

						} else {
							setRequiredError(itemSelectCombo,
									getPropertyName("invalid_selection"), true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (!isDiscountEnable()) {
				discountTextField.setVisible(false);
			}

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salesOrderNumberList.setValue((long) 0);
				}
			});

			customerSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (customerSelect.getValue() != null) {
							customerSelect.setDescription("<h1><i>Current Balance</i> : "
									+ comDao.getLedgerCurrentBalance((Long) customerSelect
											.getValue()) + "</h1>");
						} else
							customerSelect.setDescription(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

//			saveSOButton.addClickListener(new Button.ClickListener() {
//				@Override
//				public void buttonClick(ClickEvent event) {
//
//					try {
//
//						if (isValid()) {
//
//							SalesOrderModel poObj = new SalesOrderModel();
//
//							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
//
//							SalesInventoryDetailsModel invObj;
//							Item item;
//							Iterator it = table.getItemIds().iterator();
//							while (it.hasNext()) {
//								invObj = new SalesInventoryDetailsModel();
//
//								item = table.getItem(it.next());
//
//								// invObj.setStock_id((Long)
//								// item.getItemProperty(TBC_STOCK_ID).getValue());
//								invObj.setItem(new ItemModel((Long) item
//										.getItemProperty(TBC_ITEM_ID)
//										.getValue()));
//								invObj.setQunatity((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//								invObj.setBalance((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//
//								if (taxEnable) {
//									invObj.setTax(new TaxModel((Long) item
//											.getItemProperty(TBC_TAX_ID)
//											.getValue()));
//									invObj.setTax_amount((Double) item
//											.getItemProperty(TBC_TAX_AMT)
//											.getValue());
//									invObj.setTax_percentage((Double) item
//											.getItemProperty(TBC_TAX_PERC)
//											.getValue());
//								} else {
//									invObj.setTax(new TaxModel(1));
//									invObj.setTax_amount(0);
//									invObj.setTax_percentage(0);
//								}
//								invObj.setUnit(new UnitModel((Long) item
//										.getItemProperty(TBC_UNIT_ID)
//										.getValue()));
//								invObj.setUnit_price((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue());
//
//								invObj.setQuantity_in_basic_unit((Double) item
//										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
//										.getValue());
//
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue());
//								}
//
//								// invObj.setManufacturing_date(CommonUtil.getCurrentSQLDate());
//								// invObj.setExpiry_date(CommonUtil.getCurrentSQLDate());
//
//								itemsList.add(invObj);
//							}
//
//							poObj.setTotal_amount(Double
//									.parseDouble(grandTotalAmtTextField
//											.getValue()));
//							poObj.setBuilding(new BuildingModel(
//									(Long) buildingSelect.getValue()));
//							poObj.setComments(comment.getValue());
//							poObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							poObj.setRequired_delivery_date(CommonUtil
//									.getSQLDateFromUtilDate(expected_delivery_date
//											.getValue()));
//							poObj.setLogin(new S_LoginModel(getLoginID()));
//							poObj.setOffice(new S_OfficeModel(getOfficeID()));
//							poObj.setRef_no(referenceNoTextField.getValue());
//							poObj.setStatus(1);
//							poObj.setCustomer(new LedgerModel(
//									(Long) customerSelect.getValue()));
//							poObj.setInventory_details_list(itemsList);
//							poObj.setSales_person((Long) employSelect
//									.getValue());
//							poObj.setSales_order_number(getNextSequence(
//									"Sales Order Id", getLoginID()));
//
//							poObj.setSales_type((Long) salesTypeSelect
//									.getValue());
//
//							long id = daoObj.save(poObj);
//							daoObj.updateItem(poObj.getInventory_details_list());
//
//							loadSO(id);
//
//							saveActivity(
//									getOptionId(),
//									"New Sales Order Created. Bill No : "
//											+ poObj.getSales_order_number()
//											+ ", Customer : "
//											+ customerSelect
//													.getItemCaption(customerSelect
//															.getValue())
//											+ ", Amount : "
//											+ poObj.getTotal_amount(),poObj.getId());
//
//							Notification.show(getPropertyName("save_success"),
//									Type.WARNING_MESSAGE);
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//			});

			salesOrderNumberList
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

//							try {
//
//								removeAllErrors();
//
//								if (salesOrderNumberList.getValue() != null
//										&& !salesOrderNumberList.getValue()
//												.toString().equals("0")) {
//
//									SalesOrderModel poObj = daoObj
//											.getSalesOrder((Long) salesOrderNumberList
//													.getValue());
//
//									table.setVisibleColumns(allHeaders);
//
//									table.removeAllItems();
//
//									SalesInventoryDetailsModel invObj;
//									double netTotal;
//									Iterator it = poObj
//											.getInventory_details_list()
//											.iterator();
//									while (it.hasNext()) {
//										invObj = (SalesInventoryDetailsModel) it
//												.next();
//
//										netTotal = (invObj.getUnit_price() * invObj
//												.getQunatity())
//												+ invObj.getTax_amount()
//												- invObj.getDiscount_amount();
//
//										table.addItem(
//												new Object[] {
//														table.getItemIds()
//																.size() + 1,
//														invObj.getItem()
//																.getId(),
//														invObj.getItem()
//																.getItem_code(),
//														invObj.getItem()
//																.getName(),
//														invObj.getQunatity(),
//														invObj.getUnit()
//																.getId(),
//														invObj.getUnit()
//																.getSymbol(),
//														invObj.getUnit_price(),
//														invObj.getTax().getId(),
//														invObj.getTax_amount(),
//														invObj.getTax_percentage(),
//														invObj.getDiscount_amount(),
//														netTotal,
//														invObj.getQuantity_in_basic_unit() },
//												table.getItemIds().size() + 1);
//
//									}
//
//									table.setVisibleColumns(requiredHeaders);
//
//									grandTotalAmtTextField
//											.setNewValue(asString(poObj
//													.getTotal_amount()));
//									if (poObj.getBuilding() != null)
//										buildingSelect.setValue(poObj
//												.getBuilding().getId());
//									else
//										buildingSelect.setValue(null);
//									comment.setValue(poObj.getComments());
//									date.setValue(poObj.getDate());
//									expected_delivery_date.setValue(poObj
//											.getRequired_delivery_date());
//									referenceNoTextField.setValue(poObj
//											.getRef_no());
//									customerSelect.setValue(poObj.getCustomer()
//											.getId());
//									employSelect.setNewValue(poObj
//											.getSales_person());
//									salesTypeSelect.setValue(poObj
//											.getSales_type());
//
//									updateSOButton.setVisible(true);
//									deleteSOButton.setVisible(true);
//									cancelSOButton.setVisible(true);
//									printButton.setVisible(true);
//									saveSOButton.setVisible(false);
//
//									isValid();
//								} else {
//									table.removeAllItems();
//
//									grandTotalAmtTextField.setNewValue("0.0");
//									buildingSelect.setValue(null);
//									comment.setValue("");
//									date.setValue(new Date(getWorkingDate()
//											.getTime()));
//									expected_delivery_date.setValue(new Date(
//											getWorkingDate().getTime()));
//									referenceNoTextField.setValue("");
//									customerSelect.setValue(null);
//									employSelect.setNewValue(getLoginID());
//									saveSOButton.setVisible(true);
//									updateSOButton.setVisible(false);
//									deleteSOButton.setVisible(false);
//									cancelSOButton.setVisible(false);
//									printButton.setVisible(false);
//								}
//
//								calculateTotals();
//
//								itemSelectCombo.setValue(null);
//								itemSelectCombo.focus();
//								quantityTextField.setValue("0.0");
//								unitPriceTextField.setValue("0.0");
//								discountTextField.setNewValue("0.0");
//								netPriceTextField.setNewValue("0.0");
//
//								customerSelect.focus();
//
//								if (!isFinYearBackEntry()) {
//									saveSOButton.setVisible(false);
//									updateSOButton.setVisible(false);
//									deleteSOButton.setVisible(false);
//									cancelSOButton.setVisible(false);
//									printButton.setVisible(false);
//									if (salesOrderNumberList.getValue() == null
//											|| salesOrderNumberList.getValue()
//													.toString().equals("0")) {
//										Notification
//												.show(getPropertyName("warning_transaction"),
//														Type.WARNING_MESSAGE);
//									}
//								}
//
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						}
					});

			updateSOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

//						if (isValid()) {
//
//							SalesOrderModel poObj = daoObj
//									.getSalesOrder((Long) salesOrderNumberList
//											.getValue());
//
//							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
//
//							SalesInventoryDetailsModel invObj;
//							Item item;
//							Iterator it = table.getItemIds().iterator();
//							while (it.hasNext()) {
//								invObj = new SalesInventoryDetailsModel();
//
//								item = table.getItem(it.next());
//
//								// invObj.setStock_id((Long)
//								// item.getItemProperty(TBC_STOCK_ID).getValue());
//								invObj.setItem(new ItemModel((Long) item
//										.getItemProperty(TBC_ITEM_ID)
//										.getValue()));
//								invObj.setQunatity((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//								invObj.setBalance((Double) item
//										.getItemProperty(TBC_QTY).getValue());
//
//								if (taxEnable) {
//									invObj.setTax(new TaxModel((Long) item
//											.getItemProperty(TBC_TAX_ID)
//											.getValue()));
//									invObj.setTax_amount((Double) item
//											.getItemProperty(TBC_TAX_AMT)
//											.getValue());
//									invObj.setTax_percentage((Double) item
//											.getItemProperty(TBC_TAX_PERC)
//											.getValue());
//								} else {
//									invObj.setTax(new TaxModel(1));
//									invObj.setTax_amount(0);
//									invObj.setTax_percentage(0);
//								}
//
//								invObj.setUnit(new UnitModel((Long) item
//										.getItemProperty(TBC_UNIT_ID)
//										.getValue()));
//
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue());
//								}
//
//								invObj.setQuantity_in_basic_unit((Double) item
//										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
//										.getValue());
//
//								invObj.setUnit_price((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue());
//
//								// invObj.setManufacturing_date(CommonUtil.getCurrentSQLDate());
//								// invObj.setExpiry_date(CommonUtil.getCurrentSQLDate());
//
//								itemsList.add(invObj);
//							}
//
//							poObj.setTotal_amount(Double
//									.parseDouble(grandTotalAmtTextField
//											.getValue()));
//							poObj.setBuilding(new BuildingModel(
//									(Long) buildingSelect.getValue()));
//							poObj.setComments(comment.getValue());
//							poObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							poObj.setRequired_delivery_date(CommonUtil
//									.getSQLDateFromUtilDate(expected_delivery_date
//											.getValue()));
//							poObj.setLogin(new S_LoginModel(getLoginID()));
//							poObj.setOffice(new S_OfficeModel(getOfficeID()));
//							poObj.setRef_no(referenceNoTextField.getValue());
//							poObj.setCustomer(new LedgerModel(
//									(Long) customerSelect.getValue()));
//							poObj.setInventory_details_list(itemsList);
//							poObj.setSales_person((Long) employSelect
//									.getValue());
//							poObj.setSales_type((Long) salesTypeSelect
//									.getValue());
//
//							daoObj.update(poObj);
//
//							loadSO(poObj.getId());
//
//							saveActivity(
//									getOptionId(),
//									"Sales Order Updated. Bill No : "
//											+ poObj.getSales_order_number()
//											+ ", Customer : "
//											+ customerSelect
//													.getItemCaption(customerSelect
//															.getValue())
//											+ ", Amount : "
//											+ poObj.getTotal_amount(),poObj.getId());
//
//							Notification.show(
//									getPropertyName("update_success"),
//									Type.WARNING_MESSAGE);
//						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			deleteSOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesOrderNumberList.getValue() != null
							&& !salesOrderNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
//												daoObj.delete((Long) salesOrderNumberList
//														.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"Sales Order Deleted. Bill No : "
																+ salesOrderNumberList
																		.getItemCaption(salesOrderNumberList
																				.getValue())
																+ ", Customer : "
																+ customerSelect
																		.getItemCaption(customerSelect
																				.getValue()),(Long)salesOrderNumberList
																				.getValue());

												loadSO(0);

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}

				}
			});

			cancelSOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesOrderNumberList.getValue() != null
							&& !salesOrderNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
//												daoObj.cancel((Long) salesOrderNumberList
//														.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"Sales Order Deleted. Bill No : "
																+ salesOrderNumberList
																		.getItemCaption(salesOrderNumberList
																				.getValue())
																+ ", Customer : "
																+ customerSelect
																		.getItemCaption(customerSelect
																				.getValue()),(Long)salesOrderNumberList
																				.getValue());

												loadSO(0);

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}

				}
			});

			table.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = table.getItem(selectedItems.iterator()
								.next());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

						itemSelectCombo.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());
						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());
						unitPriceTextField.setValue(""
								+ item.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

						if (taxEnable) {
							taxSelect.setValue(item.getItemProperty(TBC_TAX_ID)
									.getValue());
						}

						discountTextField
								.setValue(""
										+ item.getItemProperty(TBC_DISCOUNT)
												.getValue());

						netPriceTextField.setNewValue(""
								+ item.getItemProperty(TBC_NET_PRICE)
										.getValue());

						visibleAddupdateSOButton(false, true);

						itemSelectCombo.focus();

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

					} else {
						itemSelectCombo.setValue(null);
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");

						visibleAddupdateSOButton(true, false);

						itemSelectCombo.focus();
					}
				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (table.getComponentError() != null)
							setRequiredError(table, null, false);

						if (isAddingValid()) {

							double price = 0, qty = 0, totalAmt = 0;

							price = Double.parseDouble(unitPriceTextField
									.getValue());
							qty = Double.parseDouble(quantityTextField
									.getValue());

							netPriceTextField
									.setNewValue(asString(price * qty));

							table.setVisibleColumns(allHeaders);

							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());
							// ItemModel itm=stk.getItem();
							UnitModel objUnit = new UnitDao()
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0, discount_amt = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = price * qty * objTax.getValue()
											/ 100;
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							discount_amt = toDouble(discountTextField
									.getValue());

							totalAmt = price * qty + tax_amt - discount_amt;

							int id = 0, ct = 0;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								id = (Integer) it.next();
							}
							id++;

							List mapList = new ManufacturingDao()
									.getItemForManufacturing((Long) itemSelectCombo
											.getValue());
							if (mapList != null) {
								boolean flag = true;
								ManufacturingMapModel mapMdl;
								Iterator iter = mapList.iterator();
								String str = "Following Items don't have sufficient balance.";
								while (iter.hasNext()) {
									mapMdl = (ManufacturingMapModel) iter
											.next();
									if ((mapMdl.getSubItem()
											.getCurrent_balalnce() - mapMdl
											.getSubItem().getReservedQuantity()) < (mapMdl
											.getQuantity() * toDouble(quantityTextField
											.getValue().toString()))) {
										str += " "
												+ mapMdl.getSubItem().getName()
												+ ", ";
										flag = false;
									}
								}
								if (!flag)
									SNotification.show("" + str,
											Type.WARNING_MESSAGE);
							}

							double conv_rat = comDao.getConvertionRate(
									itm.getId(),
									objUnit.getId(),
									toInt(salesTypeSelect.getValue().toString()));

							table.addItem(
									new Object[] {
											table.getItemIds().size() + 1,
											itm.getId(),
											itm.getItem_code(),
											itm.getName(),
											qty,
											objUnit.getId(),
											objUnit.getSymbol(),
											Double.parseDouble(unitPriceTextField
													.getValue()),
											objTax.getId(), tax_amt, tax_perc,
											discount_amt, totalAmt,
											conv_rat * qty }, id);

							table.setVisibleColumns(requiredHeaders);

							itemSelectCombo.setValue(null);
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");

							calculateTotals();

							itemSelectCombo.focus();

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isAddingValid()) {

							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());

							double price = 0, qty = 0, totalAmt = 0, discount_amt = 0;

							price = Double.parseDouble(unitPriceTextField
									.getValue());
							qty = Double.parseDouble(quantityTextField
									.getValue());
							if (discountTextField.getValue() != null)
								discount_amt = toDouble(discountTextField
										.getValue());

							netPriceTextField
									.setNewValue(asString(price * qty));

							// table.setVisibleColumns(new String[] {TBC_SN,
							// TBC_ITEM_ID,TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY,
							// TBC_UNIT_ID, TBC_UNIT,
							// TBC_UNIT_PRICE,TBC_TAX_ID, TBC_TAX_AMT,
							// TBC_TAX_PERC , TBC_NET_PRICE});

							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());
							// ItemModel itm=stk.getItem();
							UnitModel objUnit = new UnitDao()
									.getUnit((Long) unitSelect.getValue());

							List mapList = new ManufacturingDao()
									.getItemForManufacturing((Long) itemSelectCombo
											.getValue());
							if (mapList != null) {
								boolean flag = true;
								ManufacturingMapModel mapMdl;
								Iterator iter = mapList.iterator();
								String str = "Following Items don't have sufficient balance.";
								while (iter.hasNext()) {
									mapMdl = (ManufacturingMapModel) iter
											.next();
									if ((mapMdl.getSubItem()
											.getCurrent_balalnce() - mapMdl
											.getSubItem().getReservedQuantity()) < (mapMdl
											.getQuantity() * toDouble(quantityTextField
											.getValue().toString()))) {
										str += " "
												+ mapMdl.getSubItem().getName()
												+ ", ";
										flag = false;
									}
								}
								if (!flag)
									SNotification.show("" + str,
											Type.WARNING_MESSAGE);
							}

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = price * qty * objTax.getValue()
											/ 100;
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							totalAmt = price * qty + tax_amt - discount_amt;

							// item.getItemProperty(
							// TBC_STOCK_ID).setValue(stk.getId());
							item.getItemProperty(TBC_ITEM_ID).setValue(
									itm.getId());
							item.getItemProperty(TBC_ITEM_CODE).setValue(
									itm.getItem_code());
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									itm.getName());
							item.getItemProperty(TBC_QTY).setValue(qty);
							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());
							item.getItemProperty(TBC_UNIT_PRICE).setValue(
									Double.parseDouble(unitPriceTextField
											.getValue()));
							item.getItemProperty(TBC_TAX_ID).setValue(
									objTax.getId());
							item.getItemProperty(TBC_TAX_AMT).setValue(tax_amt);
							item.getItemProperty(TBC_TAX_PERC).setValue(
									tax_perc);
							item.getItemProperty(TBC_NET_PRICE).setValue(
									totalAmt);
							item.getItemProperty(TBC_DISCOUNT).setValue(
									discount_amt);

							double conv_rat = comDao.getConvertionRate(
									itm.getId(),
									objUnit.getId(),
									toInt(salesTypeSelect.getValue().toString()));

							item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
									.setValue(conv_rat * qty);

							table.setVisibleColumns(requiredHeaders);

							itemSelectCombo.setValue(null);
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");

							visibleAddupdateSOButton(true, false);

							itemSelectCombo.focus();

							table.setValue(null);

							calculateTotals();

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			itemSelectCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemSelectCombo.getValue() != null) {
							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());

							SCollectionContainer bic = SCollectionContainer
									.setList(comDao.getAllItemUnitDetails(itm
											.getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							if (taxEnable) {
								taxSelect.setValue(itm.getSalesTax().getId());
							}

							unitSelect.setValue(itm.getUnit().getId());

							quantityTextField.focus();
							quantityTextField.selectAll();

						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}

				}
			});

			quantityTextField.setImmediate(true);

			quantityTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			unitSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							double unitPrc = 0, qty = 0;
							if (itemSelectCombo.getValue() != null) {
								ItemModel itm = daoObj
										.getItem((Long) itemSelectCombo
												.getValue());
								unitPriceTextField.setValue(asString(comDao.getItemPrice(
										itm.getId(), (Long) unitSelect
												.getValue(),
										toInt(salesTypeSelect.getValue()
												.toString()))));

								try {
									unitPrc = Double
											.parseDouble(unitPriceTextField
													.getValue());
									qty = Double.parseDouble(quantityTextField
											.getValue());
								} catch (Exception e) {
									// TODO: handle exception
								}

							}

							netPriceTextField.setNewValue(asString(unitPrc
									* qty));

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			salesTypeSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					Object obj = unitSelect.getValue();
					unitSelect.setValue(null);
					unitSelect.setValue(obj);
				}
			});

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
					loadSO(0);
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
					// if(deleteItemButton.isVisible())
					// deleteItemButton.click();
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

			if (!isFinYearBackEntry()) {
				saveSOButton.setVisible(false);
				updateSOButton.setVisible(false);
				deleteSOButton.setVisible(false);
				cancelSOButton.setVisible(false);
				printButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					List<Object> reportList = new ArrayList<Object>();
					SalesPrintBean bean = null;
					NumberToWords numberToWords = new NumberToWords();
					double total = 0;
					try {

						CustomerModel customerModel = new CustomerDao()
								.getCustomerFromLedger(toLong(customerSelect
										.getValue().toString()));
						String address = "";
						if (customerModel != null) {
							address = new AddressDao()
									.getAddressString(customerModel
											.getAddress().getId());
						}

						map.put("CUSTOMER_NAME", customerSelect
								.getItemCaption(customerSelect.getValue()));
						map.put("CUSTOMER_ADDRESS", address);
						map.put("SALES_BILL_NO",
								toLong(salesOrderNumberList
										.getItemCaption(salesOrderNumberList
												.getValue())));
						map.put("BILL_DATE", CommonUtil
								.formatDateToDDMMMYYYY(date.getValue()));
						map.put("SALES_MAN", employSelect
								.getItemCaption(employSelect.getValue()));

						String type = "Sales Order";

						map.put("SALES_TYPE", type);
						map.put("OFFICE_NAME", customerModel.getLedger()
								.getOffice().getName());

						Item item;
						Iterator itr1 = table.getItemIds().iterator();
						while (itr1.hasNext()) {
							item = table.getItem(itr1.next());

							bean = new SalesPrintBean(item
									.getItemProperty(TBC_ITEM_NAME).getValue()
									.toString(), toDouble(item
									.getItemProperty(TBC_QTY).getValue()
									.toString()), toDouble(item
									.getItemProperty(TBC_UNIT_PRICE).getValue()
									.toString()), toDouble(item
									.getItemProperty(TBC_NET_PRICE).getValue()
									.toString()), item
									.getItemProperty(TBC_UNIT).getValue()
									.toString(), item
									.getItemProperty(TBC_ITEM_CODE).getValue()
									.toString(), toDouble(item
									.getItemProperty(TBC_QTY).getValue()
									.toString()));

							total += toDouble(item
									.getItemProperty(TBC_NET_PRICE).getValue()
									.toString());

							reportList.add(bean);
						}

						S_OfficeModel officeModel = new OfficeDao()
								.getOffice(getOfficeID());
						map.put("AMOUNT_IN_WORDS", numberToWords.convertNumber(
								roundNumber(total) + "", officeModel
										.getCurrency().getInteger_part(),
								officeModel.getCurrency().getFractional_part()));

						Report report = new Report(getLoginID());
						report.setJrxmlFileName(getBillName(SConstants.bills.SALES_ORDER));
						// report.setJrxmlFileName("SalesOrder_A4_Print");
						report.setReportFileName("SalesOrderPrint");
						report.setReportTitle("Sales Order");
						// report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						report.createReport(reportList, map);

						report.print();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return pannel;
	}

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0, disc = 0;

		try {
			unitPrc = Double.parseDouble(unitPriceTextField.getValue());
			qty = Double.parseDouble(quantityTextField.getValue());
			disc = Double.parseDouble(discountTextField.getValue());
		} catch (Exception e) {
			// TODO: handle exception
		}
		netPriceTextField.setNewValue(asString((unitPrc * qty) - disc));
	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0, disc_ttl = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();

				if (taxEnable) {
					tax_ttl += (Double) item.getItemProperty(TBC_TAX_AMT)
							.getValue();
				}

				net_ttl += (Double) item.getItemProperty(TBC_NET_PRICE)
						.getValue();
				disc_ttl += (Double) item.getItemProperty(TBC_DISCOUNT)
						.getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(qty_ttl));
			table.setColumnFooter(TBC_TAX_AMT, asString(tax_ttl));
			table.setColumnFooter(TBC_NET_PRICE, asString(net_ttl));
			table.setColumnFooter(TBC_DISCOUNT, asString(disc_ttl));

			grandTotalAmtTextField.setNewValue(asString(net_ttl));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (discountTextField.isVisible()) {
				if (discountTextField.getValue() == null
						|| discountTextField.getValue().equals("")) {
					setRequiredError(discountTextField,
							getPropertyName("invalid_data"), true);
					discountTextField.focus();
					discountTextField.selectAll();
					ret = false;
				} else {
					try {
						if (toDouble(discountTextField.getValue()) < 0) {
							setRequiredError(discountTextField,
									getPropertyName("invalid_data"), true);
							discountTextField.focus();
							discountTextField.selectAll();
							ret = false;
						} else
							setRequiredError(discountTextField, null, false);
					} catch (Exception e) {
						setRequiredError(discountTextField,
								getPropertyName("invalid_data"), true);
						discountTextField.focus();
						discountTextField.selectAll();
						ret = false;
						// TODO: handle exception
					}
				}
			}

			if (taxEnable) {
				if (taxSelect.getValue() == null
						|| taxSelect.getValue().equals("")) {
					setRequiredError(taxSelect,
							getPropertyName("invalid_selection"), true);
					taxSelect.focus();
					ret = false;
				} else
					setRequiredError(taxSelect, null, false);
			}

			if (unitPriceTextField.getValue() == null
					|| unitPriceTextField.getValue().equals("")) {
				setRequiredError(unitPriceTextField,
						getPropertyName("invalid_data"), true);
				unitPriceTextField.focus();
				unitPriceTextField.selectAll();
				ret = false;
			} else {
				try {
					if (Double.parseDouble(unitPriceTextField.getValue()) < 0) {
						setRequiredError(unitPriceTextField,
								getPropertyName("invalid_data"), true);
						unitPriceTextField.focus();
						unitPriceTextField.selectAll();
						ret = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField,
							getPropertyName("invalid_data"), true);
					unitPriceTextField.focus();
					unitPriceTextField.selectAll();
					ret = false;
					// TODO: handle exception
				}
			}

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				quantityTextField.selectAll();
				ret = false;
			} else {
				try {
					if (Double.parseDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("invalid_data"), true);
						quantityTextField.focus();
						quantityTextField.selectAll();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					quantityTextField.selectAll();
					ret = false;
					// TODO: handle exception
				}
			}

			if (itemSelectCombo.getValue() == null
					|| itemSelectCombo.getValue().equals("")) {
				setRequiredError(itemSelectCombo,
						getPropertyName("invalid_selection"), true);
				itemSelectCombo.focus();
				ret = false;
			} else
				setRequiredError(itemSelectCombo, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	public void visibleAddupdateSOButton(boolean AddVisible,
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
		}
	}

	public void loadSO(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new SalesOrderModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSalesOrdersUnderOffice(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			salesOrderNumberList.setContainerDataSource(bic);
			salesOrderNumberList.setItemCaptionPropertyId("ref_no");

			salesOrderNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		/*
		 * if(.getValue()==null || .getValue().equals("")){ setRequiredError( ,
		 * "Select a Date",true); .focus(); ret=false; } else setRequiredError(
		 * , null,false);
		 */

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			itemSelectCombo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (buildingSelect.getValue() == null
				|| buildingSelect.getValue().equals("")) {
			setRequiredError(buildingSelect,
					getPropertyName("invalid_selection"), true);
			buildingSelect.focus();
			ret = false;
		} else
			setRequiredError(buildingSelect, null, false);

		if (employSelect.getValue() == null
				|| employSelect.getValue().equals("")) {
			setRequiredError(employSelect,
					getPropertyName("invalid_selection"), true);
			employSelect.focus();
			ret = false;
		} else
			setRequiredError(employSelect, null, false);

		if (customerSelect.getValue() == null
				|| customerSelect.getValue().equals("")) {
			setRequiredError(customerSelect,
					getPropertyName("invalid_selection"), true);
			customerSelect.focus();
			ret = false;
		} else
			setRequiredError(customerSelect, null, false);

		if (expected_delivery_date.getValue() == null
				|| expected_delivery_date.getValue().equals("")) {
			setRequiredError(expected_delivery_date,
					getPropertyName("invalid_selection"), true);
			expected_delivery_date.focus();
			ret = false;
		} else {

			if (((Date) expected_delivery_date.getValue()).getTime() < ((Date) date
					.getValue()).getTime()) {
				setRequiredError(expected_delivery_date,
						getPropertyName("invalid_selection"), true);
				expected_delivery_date.focus();
				ret = false;
			} else
				setRequiredError(expected_delivery_date, null, false);
		}

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		// TODO Auto-generated method stub
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
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
