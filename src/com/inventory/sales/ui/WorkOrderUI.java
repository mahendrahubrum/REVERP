package com.inventory.sales.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.ContractorDao;
import com.inventory.config.acct.model.ContractorModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddNewUnitUI;
import com.inventory.config.unit.ui.SetUnitManagementUI;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.sales.dao.WorkOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.WorkOrderModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */
public class WorkOrderUI extends SparkLogic {

	private static final long serialVersionUID = -5415935778881929746L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "Tax %";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";
	static String TBC_DISCOUNT = "DISCOUNT";
	static String TBC_PO_ID = "PO ID";
	static String TBC_INV_ID = "INV ID";
	static String TBC_CESS_AMT = "CESS";
	static String TBC_NET_TOTAL = "Net Total";
	static String TBC_NET_FINAL = "Final Amt";

	static String TBC_QTY_IN_BASIC_UNI = "Qty in Basic Unit";

	WorkOrderDao daoObj;

	CommonMethodsDao comDao;

	SComboField workOrderNumberList;

	SPanel pannel;
	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	STextField discountTextField;
	STextField netPriceTextField;

	// STextField payingAmountTextField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;

	ItemDao itemDao;

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	// SComboField officeCompo;
	SDateField date;
	SDateField expiry_date;
	SDateField manufaturing_date;
	SComboField itemSelectCombo;

	SComboField contractorSelect;

	STextField grandTotalAmtTextField;
	STextField shippingChargeTextField;
	STextField exciseDutyTextField;
	STextArea comment;

	// SRadioButton cashOrCreditRadio;

	SettingsValuePojo settings;

	WrappedSession session;

	boolean taxEnable = isTaxEnable();

	private String[] allHeaders;
	private String[] requiredHeaders;

	private SButton printButton;

	long status;

	// private SButton newCustomerButton;
	private SButton newItemButton;

	private SButton newUnitButton;
	private SButton unitMapButton;

	// private SDialogBox newCustomerWindow;
	private SDialogBox newItemWindow;
	// private SalesCustomerPanel salesCustomerPanel;
	private ItemPanel itemPanel;

	SNativeSelect salesTypeSelect;

	SWindow popupWindow;

	ContractorDao contDao;
	TaxDao taxDao;

	SButton createNewButton;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		itemDao = new ItemDao();
		comDao = new CommonMethodsDao();
		contDao = new ContractorDao();
		taxDao = new TaxDao();

		taxEnable = isTaxEnable();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Create new");

		popupWindow = new SWindow();

		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
				TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_UNIT_PRICE,
				TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC, TBC_DISCOUNT,
				TBC_NET_PRICE, TBC_PO_ID, TBC_INV_ID, TBC_CESS_AMT,
				TBC_NET_TOTAL, TBC_NET_FINAL, TBC_QTY_IN_BASIC_UNI };

		if (taxEnable) {
			if (isCessEnable()) {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME, TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT, TBC_CESS_AMT,
						TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL };
			} else {
				requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
						TBC_ITEM_NAME, TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY,
						TBC_NET_PRICE, TBC_TAX_PERC, TBC_TAX_AMT,
						TBC_NET_TOTAL, TBC_DISCOUNT, TBC_NET_FINAL };
			}
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_UNIT_PRICE, TBC_QTY,
					TBC_NET_PRICE, TBC_DISCOUNT, TBC_NET_FINAL };
		}

		List<String> templist = new ArrayList<String>();
		Collections.addAll(templist, requiredHeaders);

		/*
		 * if (!isManufDateEnable()) { templist.remove(TBC_MANUFACT_DATE);
		 * templist.remove(TBC_EXPIRE_DATE); }
		 */
		if (!isDiscountEnable()) {
			templist.remove(TBC_DISCOUNT);
		}

		requiredHeaders = templist.toArray(new String[templist.size()]);

		setSize(1260, 600);

		session = getHttpSession();

		daoObj = new WorkOrderDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		// isPaymentNow=new SCheckBox("Receiving Cash", true);

		// payingAmountTextField = new STextField(null, 100);
		// payingAmountTextField.setValue("0.00");
		// payingAmountTextField.setStyleName("textfield_align_right");
		// payingAmountTextField.setVisible(false);

		pannel = new SPanel();
		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(9);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(2);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(6);
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

			contractorSelect = new SComboField(
					null,
					160,
					contDao.getAllActiveContractorNamesWithLedgerID(getOfficeID()),
					"id", "name");

			contractorSelect.setValue(getUserID());

			workOrderNumberList = new SComboField(null, 160);

			date = new SDateField(null, 120, getDateFormat(), new Date(
					getWorkingDate().getTime()));

			// officeCompo=new SComboField(null, 300, new
			// OfficeDao().getAllOfficeNamesUnderOrg(getOrganizationID()), "id",
			// "name");

			salesTypeSelect = new SNativeSelect(null, 120,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");

			Iterator itt = salesTypeSelect.getItemIds().iterator();
			if (itt.hasNext())
				itt.next();
			if (itt.hasNext())
				salesTypeSelect.setValue(itt.next());

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("work_order_no")), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(workOrderNumberList);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
			// masterDetailsGrid.setComponentAlignment(netTotal,
			// Alignment.MIDDLE_RIGHT);

			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			// masterDetailsGrid.addComponent(new SLabel("Office :"), 1, 1);

			// SHorizontalLayout hrl=new SHorizontalLayout();
			// hrl.addComponent(officeCompo);
			//
			// masterDetailsGrid.addComponent(hrl, 2, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("contractor")), 1, 1);
			masterDetailsGrid.addComponent(contractorSelect, 2, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("sales_type")), 6, 1);
			masterDetailsGrid.addComponent(salesTypeSelect, 8, 1);

			masterDetailsGrid.setStyleName("master_border");

			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");

			newUnitButton = new SButton();
			newUnitButton.setStyleName("smallAddNewBtnStyle");
			newUnitButton.setDescription("Add new Unit");
			unitMapButton = new SButton();
			unitMapButton.setStyleName("mapBtnStyle");
			unitMapButton
					.setDescription("Set Convertion Quantity ( Unit Management )");

			newItemButton.setDescription("Add new Item");

			quantityTextField = new STextField(getPropertyName("quantity"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60, null,
					"id", "symbol");
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

			expiry_date = new SDateField(getPropertyName("exp_date"), 100,
					"dd/MMM/yyyy", new Date());
			manufaturing_date = new SDateField(getPropertyName("mfg_date"),
					100, "dd/MMM/yyyy", new Date());
			itemSelectCombo = new SComboField(getPropertyName("item"), 250,
					daoObj.getAllItems(getOfficeID()), "id", "name", true,
					getPropertyName("select"));

			loadWorkOrders(0);

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			SHorizontalLayout hrz1 = new SHorizontalLayout();
			hrz1.addComponent(itemSelectCombo);
			hrz1.addComponent(newItemButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);

			addingGrid.addComponent(hrz1);

			addingGrid.addComponent(quantityTextField);

			SHorizontalLayout hrz2 = new SHorizontalLayout();
			hrz2.addComponent(unitSelect);

			SVerticalLayout vert = new SVerticalLayout();
			vert.addComponent(unitMapButton);
			vert.addComponent(newUnitButton);
			vert.setSpacing(false);
			hrz2.addComponent(vert);
			hrz2.setComponentAlignment(vert, Alignment.MIDDLE_CENTER);
			hrz2.setSpacing(true);
			addingGrid.addComponent(hrz2);

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
			addingGrid.setColumnExpandRatio(8, 3);

			addingGrid.setWidth("1200");

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
					getPropertyName("item_code"), null, Align.CENTER);
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
			table.addContainerProperty(TBC_PO_ID, Long.class, null, TBC_PO_ID,
					null, Align.CENTER);
			table.addContainerProperty(TBC_INV_ID, Long.class, null,
					TBC_INV_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CESS_AMT, Double.class, null,
					getPropertyName("cess"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_TOTAL, Double.class, null,
					getPropertyName("net_total"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_FINAL, Double.class, null,
					getPropertyName("final_amount"), null, Align.RIGHT);
			/*
			 * table.addContainerProperty(TBC_MANUFACT_DATE, Date.class, null,
			 * TBC_MANUFACT_DATE, null, Align.RIGHT);
			 * table.addContainerProperty(TBC_EXPIRE_DATE, Date.class, null,
			 * TBC_EXPIRE_DATE, null, Align.RIGHT);
			 * table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
			 * TBC_STOCK_ID, null, Align.RIGHT);
			 */
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNI, Double.class,
					null, TBC_QTY_IN_BASIC_UNI, null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, (float) 1.3);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 2);
			table.setColumnExpandRatio(TBC_NET_TOTAL, 2);
			table.setColumnExpandRatio(TBC_NET_FINAL, 2);
			table.setColumnExpandRatio(TBC_CESS_AMT, (float) 0.8);
			// table.setColumnExpandRatio(TBC_MANUFACT_DATE, (float) 1.1);
			// table.setColumnExpandRatio(TBC_EXPIRE_DATE, (float) 1.1);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);
			// table.setEditable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_DISCOUNT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1200");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			grandTotalAmtTextField = new STextField(null, 200, "0.0");
			grandTotalAmtTextField.setReadOnly(true);
			grandTotalAmtTextField.setStyleName("textfield_align_right");

			shippingChargeTextField = new STextField(null, 120, "0.0");
			shippingChargeTextField.setStyleName("textfield_align_right");

			exciseDutyTextField = new STextField(null, 120, "0.0");
			exciseDutyTextField.setStyleName("textfield_align_right");

			comment = new STextArea(null, 400, 30);

			bottomGrid.addComponent(new SLabel(""), 1, 0);

			if (!isDiscountEnable()) {
				discountTextField.setVisible(false);
			}
			if (isExciceDutyEnable()) {
				bottomGrid.addComponent(new SLabel(
						getPropertyName("excise_duty")), 4, 2);
				bottomGrid.addComponent(exciseDutyTextField, 5, 2);
				bottomGrid.setComponentAlignment(shippingChargeTextField,
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

			bottomGrid.addComponent(new SLabel(getPropertyName("net_amount")),
					4, 3);
			bottomGrid.addComponent(grandTotalAmtTextField, 5, 3);

			// bottomGrid.addComponent(new SLabel("Paying Amt :"), 2, 3);
			// bottomGrid.addComponent(payingAmountTextField, 3, 3);

			bottomGrid.setComponentAlignment(grandTotalAmtTextField,
					Alignment.TOP_RIGHT);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveButton);
			mainButtonLayout.addComponent(updateButton);
			mainButtonLayout.addComponent(deleteButton);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			// Added by anil
			printButton = new SButton(getPropertyName("print"));
			mainButtonLayout.addComponent(printButton);
			printButton.setVisible(false);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			// officeCompo.focus();

			pannel.setContent(hLayout);

			Collection aa = (Collection) table.getValue();

			newItemWindow = new SDialogBox(getPropertyName("add_item"), 500,
					600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			newItemWindow.addComponent(itemPanel);

			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					workOrderNumberList.setValue((long) 0);
				}
			});

			newItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
					getUI().getCurrent().addWindow(newItemWindow);
				}
			});

			newUnitButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					popupWindow.setContent(new AddNewUnitUI());

					popupWindow.setWidth("502");
					popupWindow.setHeight("455");

					popupWindow.center();
					popupWindow.setModal(true);

					getUI().getCurrent().addWindow(popupWindow);
				}
			});

			unitMapButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					long itemId = 0;

					if (itemSelectCombo.getValue() != null) {
						ItemModel itm = null;
						try {
							itm = daoObj.getItem((Long) itemSelectCombo
									.getValue());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						itemId = itm.getId();
					}

					popupWindow.setContent(new SetUnitManagementUI(itemId,
							(Long) salesTypeSelect.getValue(), true));

					popupWindow.setWidth("910");
					popupWindow.setHeight("498");

					popupWindow.center();
					popupWindow.setModal(true);
					getUI().getCurrent().addWindow(popupWindow);

				}
			});

			newItemWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					reloadItemStocks();
				}
			});

			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					resetUnit();
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

//						if (isValid()) {
//
//							WorkOrderModel woObj = new WorkOrderModel();
//
//							List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();
//
//							SalesInventoryDetailsModel invObj;
//							Item item;
//							double std_cost;
//							Iterator it = table.getItemIds().iterator();
//							while (it.hasNext()) {
//								invObj = new SalesInventoryDetailsModel();
//
//								item = table.getItem(it.next());
//
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
//								invObj.setUnit_price((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue());
//
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue());
//								}
//
//								invObj.setOrder_id((Long) item.getItemProperty(
//										TBC_PO_ID).getValue());
//								invObj.setCess_amount((Double) item
//										.getItemProperty(TBC_CESS_AMT)
//										.getValue());
//
//								/*
//								 * invObj.setManufacturing_date(CommonUtil
//								 * .getSQLDateFromUtilDate((Date) item
//								 * .getItemProperty( TBC_MANUFACT_DATE)
//								 * .getValue()));
//								 * invObj.setExpiry_date(CommonUtil
//								 * .getSQLDateFromUtilDate((Date) item
//								 * .getItemProperty( TBC_EXPIRE_DATE)
//								 * .getValue())); invObj.setStock_id((Long)
//								 * item.getItemProperty(
//								 * TBC_STOCK_ID).getValue());
//								 */
//
//								invObj.setId((Long) item.getItemProperty(
//										TBC_INV_ID).getValue());
//
//								invObj.setQuantity_in_basic_unit((Double) item
//										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
//										.getValue());
//
//								itemsList.add(invObj);
//
//								std_cost = itemDao.getStandardCost(invObj
//										.getItem().getId());
//
//							}
//
//							if (isExciceDutyEnable()) {
//								woObj.setExcise_duty(toDouble(exciseDutyTextField
//										.getValue()));
//							}
//							if (isShippingChargeEnable()) {
//								woObj.setShipping_charge(toDouble(shippingChargeTextField
//										.getValue()));
//							}
//
//							woObj.setShipping_charge(toDouble(shippingChargeTextField
//									.getValue()));
//							woObj.setExcise_duty(toDouble(exciseDutyTextField
//									.getValue()));
//							woObj.setAmount(toDouble(grandTotalAmtTextField
//									.getValue()));
//
//							woObj.setComments(comment.getValue());
//							woObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							// woObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
//							woObj.setLogin(new S_LoginModel(getLoginID()));
//							woObj.setOffice(new S_OfficeModel(getOfficeID()));
//							woObj.setStatus(1);
//							woObj.setContractor(new LedgerModel(
//									(Long) contractorSelect.getValue()));
//							woObj.setInventory_details_list(itemsList);
//
//							woObj.setWork_order_number(getNextSequence(
//									"Work Order Number", getLoginID()));
//
//							long id = daoObj.save(woObj);
//
//							loadWorkOrders(id);
//
//							Notification.show(getPropertyName("save_success"),
//									Type.WARNING_MESSAGE);
//						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}

				}
			});

			workOrderNumberList.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

//					try {
//
//						removeAllErrors();
//
//						updateButton.setVisible(true);
//						deleteButton.setVisible(true);
//						printButton.setVisible(true);
//						saveButton.setVisible(false);
//						System.out.println(workOrderNumberList.getValue());
//						if (workOrderNumberList.getValue() != null
//								&& !workOrderNumberList.getValue().toString()
//										.equals("0")) {
//
//							WorkOrderModel woObj = daoObj
//									.getWorkOrder((Long) workOrderNumberList
//											.getValue());
//
//							table.setVisibleColumns(allHeaders);
//
//							table.removeAllItems();
//
//							SalesInventoryDetailsModel invObj;
//							double netTotal;
//							Iterator it = woObj.getInventory_details_list()
//									.iterator();
//							while (it.hasNext()) {
//								invObj = (SalesInventoryDetailsModel) it.next();
//
//								netTotal = roundNumber((invObj.getUnit_price() * invObj
//										.getQunatity())
//										+ invObj.getTax_amount()
//										+ invObj.getCess_amount()
//										- invObj.getDiscount_amount());
//
//								table.addItem(
//										new Object[] {
//												table.getItemIds().size() + 1,
//												invObj.getItem().getId(),
//												invObj.getItem().getItem_code(),
//												invObj.getItem().getName(),
//												invObj.getQunatity(),
//												invObj.getUnit().getId(),
//												invObj.getUnit().getSymbol(),
//												invObj.getUnit_price(),
//												invObj.getTax().getId(),
//												invObj.getTax_amount(),
//												invObj.getTax_percentage(),
//												invObj.getDiscount_amount(),
//												(invObj.getUnit_price() * invObj
//														.getQunatity()),
//												invObj.getOrder_id(),
//												(long) 0,
//												invObj.getCess_amount(),
//												roundNumber((invObj
//														.getUnit_price() * invObj
//														.getQunatity())
//														+ invObj.getTax_amount()
//														+ invObj.getCess_amount()),
//												netTotal,
//												invObj.getQuantity_in_basic_unit() },
//										table.getItemIds().size() + 1);
//							}
//
//							table.setVisibleColumns(requiredHeaders);
//
//							grandTotalAmtTextField.setNewValue(asString(woObj
//									.getAmount()));
//							// buildingSelect.setValue(woObj.getBuilding().getId());
//							comment.setValue(woObj.getComments());
//							date.setValue(woObj.getDate());
//							// expected_delivery_date.setValue(woObj.getExpected_delivery_date());
//
//							contractorSelect.setValue(woObj.getContractor()
//									.getId());
//
//							shippingChargeTextField.setValue(asString(woObj
//									.getShipping_charge()));
//							exciseDutyTextField.setValue(asString(woObj
//									.getExcise_duty()));
//
//							isValid();
//							updateButton.setVisible(true);
//							printButton.setVisible(true);
//							deleteButton.setVisible(true);
//							saveButton.setVisible(false);
//
//							status = woObj.getStatus();
//
//						} else {
//							table.removeAllItems();
//
//							grandTotalAmtTextField.setNewValue("0.0");
//							comment.setValue("");
//							date.setValue(new Date(getWorkingDate().getTime()));
//							// expected_delivery_date.setValue(new Date());
//							contractorSelect.setValue(null);
//
//							saveButton.setVisible(true);
//							updateButton.setVisible(false);
//							printButton.setVisible(false);
//							deleteButton.setVisible(false);
//						}
//
//						calculateTotals();
//
//						itemSelectCombo.setValue(null);
//						itemSelectCombo.focus();
//						quantityTextField.setValue("0.0");
//						unitPriceTextField.setValue("0.0");
//						netPriceTextField.setNewValue("0.0");
//						discountTextField.setNewValue("0.0");
//
//						if (!isFinYearBackEntry()) {
//							saveButton.setVisible(false);
//							updateButton.setVisible(false);
//							deleteButton.setVisible(false);
//							if (workOrderNumberList.getValue() == null
//									|| workOrderNumberList.getValue()
//											.toString().equals("0")) {
//								Notification.show(
//										getPropertyName("warning_transaction"),
//										Type.WARNING_MESSAGE);
//							}
//						}
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Notification.show(getPropertyName("error"),
//								Type.ERROR_MESSAGE);
//					}
				}

			});

			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

//						if (isValid()) {
//
//							WorkOrderModel woObj = daoObj
//									.getWorkOrder((Long) workOrderNumberList
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
//								invObj.setUnit_price((Double) item
//										.getItemProperty(TBC_UNIT_PRICE)
//										.getValue());
//								if (isDiscountEnable()) {
//									invObj.setDiscount_amount((Double) item
//											.getItemProperty(TBC_DISCOUNT)
//											.getValue());
//								}
//								invObj.setOrder_id((Long) item.getItemProperty(
//										TBC_PO_ID).getValue());
//								invObj.setCess_amount((Double) item
//										.getItemProperty(TBC_CESS_AMT)
//										.getValue());
//
//								invObj.setQuantity_in_basic_unit((Double) item
//										.getItemProperty(TBC_QTY_IN_BASIC_UNI)
//										.getValue());
//
//								itemsList.add(invObj);
//
//							}
//
//							woObj.setShipping_charge(toDouble(shippingChargeTextField
//									.getValue()));
//							woObj.setExcise_duty(toDouble(exciseDutyTextField
//									.getValue()));
//							woObj.setAmount(toDouble(grandTotalAmtTextField
//									.getValue()));
//							woObj.setComments(comment.getValue());
//							woObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(date.getValue()));
//							woObj.setLogin(new S_LoginModel(getLoginID()));
//							woObj.setOffice(new S_OfficeModel(getOfficeID()));
//							woObj.setContractor(new LedgerModel(
//									(Long) contractorSelect.getValue()));
//							woObj.setInventory_details_list(itemsList);
//
//							daoObj.update(woObj);
//
//							loadWorkOrders(woObj.getId());
//
//							Notification.show(
//									getPropertyName("update_success"),
//									Type.WARNING_MESSAGE);
//						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (workOrderNumberList.getValue() != null
							&& !workOrderNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) workOrderNumberList
														.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);
												loadWorkOrders(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
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

					itemSelectCombo.setReadOnly(false);
					unitSelect.setReadOnly(false);

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = table.getItem(selectedItems.iterator()
								.next());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
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

						itemSelectCombo.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());

						visibleAddupdateButton(false, true);

						itemSelectCombo.focus();

						if ((Long) item.getItemProperty(TBC_PO_ID).getValue() > 0) {
							// itemSelectCombo.setReadOnly(true);
							unitSelect.setReadOnly(true);
							quantityTextField.focus();
						}

						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());

						unitPriceTextField.setValue(""
								+ item.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

						// item.getItemProperty(
						// TBC_ITEM_NAME).setValue("JPTTTTTT");

					} else {

						itemSelectCombo.setValue(null);
						itemSelectCombo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");
						discountTextField.setValue("0.0");

						visibleAddupdateButton(true, false);

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

							boolean already_added_item = false;

							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());

							Iterator itr1 = table.getItemIds().iterator();
							Item item;
							double qty;
							double price, discount, cess_amt, total, cess_perc;
							double tax_amt = 0, tax_perc = 0;
							List delList = new ArrayList();
							while (itr1.hasNext()) {
								// Object obj = itr1.next();
								item = table.getItem(itr1.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelectCombo.getValue()
												.toString())
										&& item.getItemProperty(TBC_UNIT_ID)
												.getValue()
												.toString()
												.equals(unitSelect.getValue()
														.toString())) {

									qty = 0;
									qty = (Double) item
											.getItemProperty(TBC_QTY)
											.getValue()
											+ toDouble(quantityTextField
													.getValue());
									price = toDouble(unitPriceTextField
											.getValue());
									item.getItemProperty(TBC_QTY).setValue(qty);
									item.getItemProperty(TBC_UNIT_PRICE)
											.setValue(price);
									discount = (Double) item.getItemProperty(
											TBC_DISCOUNT).getValue()
											+ toDouble(discountTextField
													.getValue());

									tax_amt = 0;
									tax_perc = 0;

									if (taxEnable) {
										tax_perc = (Double) item
												.getItemProperty(TBC_TAX_PERC)
												.getValue();

										if (tax_perc > 0) {
											tax_amt = roundNumber(price * qty
													* tax_perc / 100);
										} else {
											tax_perc = 0;
											tax_amt = (Double) item
													.getItemProperty(
															TBC_TAX_AMT)
													.getValue();
										}
									}

									total = roundNumber(price * qty);

									cess_amt = 0;
									if (isCessEnableOnItem((Long) item
											.getItemProperty(TBC_ITEM_ID)
											.getValue())) {
										cess_perc = (Double) session
												.getAttribute("cess_percentage");
										cess_amt = roundNumber(tax_amt
												* getCessPercentage() / 100);
									}

									item.getItemProperty(TBC_TAX_AMT).setValue(
											tax_amt);
									item.getItemProperty(TBC_NET_PRICE)
											.setValue(total);
									item.getItemProperty(TBC_DISCOUNT)
											.setValue(discount);
									item.getItemProperty(TBC_CESS_AMT)
											.setValue(cess_amt);
									item.getItemProperty(TBC_NET_TOTAL)
											.setValue(
													roundNumber(total + tax_amt
															+ cess_amt));
									item.getItemProperty(TBC_NET_FINAL)
											.setValue(
													roundNumber(total + tax_amt
															+ cess_amt
															- discount));

									/*
									 * item.getItemProperty(TBC_MANUFACT_DATE)
									 * .setValue( stk.getManufacturing_date());
									 * item.getItemProperty(TBC_EXPIRE_DATE)
									 * .setValue(stk.getExpiry_date());
									 * item.getItemProperty(TBC_STOCK_ID)
									 * .setValue(stk.getId());
									 */

									already_added_item = true;

									break;
								}
							}

							if (!already_added_item) {

								price = 0;
								qty = 0;
								total = 0;
								double discount_amt = 0;

								price = toDouble(unitPriceTextField.getValue());
								qty = toDouble(quantityTextField.getValue());
								discount_amt = toDouble(discountTextField
										.getValue());

								netPriceTextField.setNewValue(asString(price
										* qty));

								table.setVisibleColumns(allHeaders);

								UnitModel objUnit = new UnitDao()
										.getUnit((Long) unitSelect.getValue());

								tax_amt = 0;
								tax_perc = 0;

								TaxModel objTax = null;
								if (taxEnable) {
									objTax = taxDao.getTax((Long) taxSelect
											.getValue());

									if (objTax.getValue_type() == 1) {
										tax_perc = objTax.getValue();
										tax_amt = roundNumber(price * qty
												* objTax.getValue() / 100);
									} else {
										tax_perc = 0;
										tax_amt = roundNumber(objTax.getValue());
									}
								} else {
									objTax = new TaxModel(1);
								}

								total = roundNumber(price * qty);

								int id = 0, ct = 0;
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									id = (Integer) it.next();
								}
								id++;

								cess_amt = 0;
								if (isCessEnableOnItem(itm.getId())) {
									cess_perc = (Double) session
											.getAttribute("cess_percentage");
									cess_amt = roundNumber(tax_amt
											* getCessPercentage() / 100);
								}

								double conv_rat = comDao.getConvertionRate(itm
										.getId(), objUnit.getId(),
										toInt(salesTypeSelect.getValue()
												.toString()));

								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,
												itm.getId(),
												itm.getItem_code(),
												itm.getName(),
												qty,
												objUnit.getId(),
												objUnit.getSymbol(),
												toDouble(unitPriceTextField
														.getValue()),
												objTax.getId(),
												tax_amt,
												tax_perc,
												discount_amt,
												total,
												(long) 0,
												(long) 0,
												cess_amt,
												roundNumber(total + tax_amt
														+ cess_amt),
												roundNumber(total + tax_amt
														+ cess_amt
														- discount_amt),
												conv_rat * qty }, id);

								table.setVisibleColumns(requiredHeaders);

								itemSelectCombo.setValue(null);
								quantityTextField.setValue("0.0");
								unitPriceTextField.setValue("0.0");
								netPriceTextField.setNewValue("0.0");
								discountTextField.setValue("0.0");
							}
							calculateTotals();

							itemSelectCombo.focus();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isAddingValid()) {

							ItemModel itm = daoObj
									.getItem((Long) itemSelectCombo.getValue());

							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());

							double price = 0, qty = 0, total = 0, discount_amt = 0;

							price = toDouble(unitPriceTextField.getValue());
							qty = toDouble(quantityTextField.getValue());
							discount_amt = toDouble(discountTextField
									.getValue());

							netPriceTextField
									.setNewValue(asString(roundNumber(price
											* qty - discount_amt)));

							// table.setVisibleColumns(new String[] {TBC_SN,
							// TBC_ITEM_ID,TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY,
							// TBC_UNIT_ID, TBC_UNIT,
							// TBC_UNIT_PRICE,TBC_TAX_ID, TBC_TAX_AMT,
							// TBC_TAX_PERC , TBC_NET_PRICE});

							UnitModel objUnit = new UnitDao()
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = roundNumber(price * qty
											* objTax.getValue() / 100);
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							total = roundNumber(price * qty);

							double cess_amt = 0;
							if (isCessEnableOnItem(itm.getId())) {
								double cess_perc = roundNumber((Double) session
										.getAttribute("cess_percentage"));
								cess_amt = roundNumber(tax_amt
										* getCessPercentage() / 100);
							}

							// int id=(Integer) table.getValue();
							// table.removeItem(table.getValue());
							// table.addItem(new Object[] {id, itm.getId(),
							// itm.getItem_code(),
							// itm.getName(), qty , objUnit.getId() ,
							// objUnit.getSymbol(),
							// toDouble(unitPriceTextField.getValue()),
							// objTax.getId(), tax_amt, tax_perc, totalAmt},
							// id);

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
									toDouble(unitPriceTextField.getValue()));
							item.getItemProperty(TBC_TAX_ID).setValue(
									objTax.getId());
							item.getItemProperty(TBC_TAX_AMT).setValue(tax_amt);
							item.getItemProperty(TBC_TAX_PERC).setValue(
									tax_perc);
							item.getItemProperty(TBC_NET_PRICE).setValue(total);
							item.getItemProperty(TBC_DISCOUNT).setValue(
									discount_amt);
							item.getItemProperty(TBC_CESS_AMT).setValue(
									cess_amt);
							item.getItemProperty(TBC_NET_TOTAL).setValue(
									roundNumber(total + tax_amt + cess_amt));
							item.getItemProperty(TBC_NET_FINAL).setValue(
									roundNumber(total + tax_amt + cess_amt
											- discount_amt));

							/*
							 * item.getItemProperty(TBC_MANUFACT_DATE).setValue(
							 * stk.getManufacturing_date());
							 * item.getItemProperty(TBC_EXPIRE_DATE).setValue(
							 * stk.getExpiry_date());
							 * item.getItemProperty(TBC_STOCK_ID).setValue(
							 * stk.getId());
							 */

							double conv_rat = comDao.getConvertionRate(
									itm.getId(),
									objUnit.getId(),
									toInt(salesTypeSelect.getValue().toString()));

							item.getItemProperty(TBC_QTY_IN_BASIC_UNI)
									.setValue(conv_rat * qty);

							table.setVisibleColumns(requiredHeaders);

							// itemsCompo.setValue(null);
							// itemsCompo.focus();
							itemSelectCombo.setValue(null);
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");
							discountTextField.setValue("0.0");

							visibleAddupdateButton(true, false);

							itemSelectCombo.focus();

							table.setValue(null);

							calculateTotals();

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
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
					} catch (Exception e) {
						// TODO Auto-generated catch block
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

							if (itemSelectCombo.getValue() != null) {

								ItemModel itm = daoObj
										.getItem((Long) itemSelectCombo
												.getValue());
								unitPriceTextField.setValue(asString(comDao.getItemPrice(
										itm.getId(), (Long) unitSelect
												.getValue(),
										toInt(salesTypeSelect.getValue()
												.toString()))));

								if (quantityTextField.getValue() != null
										&& !quantityTextField.getValue()
												.equals("")) {
									netPriceTextField.setNewValue(asString(Double
											.parseDouble(unitPriceTextField
													.getValue())
											* Double.parseDouble(quantityTextField
													.getValue())));
								}

							}
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

			addingGrid.addShortcutListener(new ShortcutListener("Submit Item",
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

			table.addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadWorkOrders(0);
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

						ContractorModel customerModel = contDao
								.getContractorFromLedgerID(toLong(contractorSelect
										.getValue().toString()));
						String address = "";
//						if (customerModel != null) {
//							address = new AddressDao()
//									.getAddressString(customerModel.getLedger()
//											.getAddress().getId());
//						}

						map.put("CUSTOMER_NAME", contractorSelect
								.getItemCaption(contractorSelect.getValue()));
						map.put("CUSTOMER_ADDRESS", address);
						map.put("SALES_BILL_NO",
								toLong(workOrderNumberList
										.getItemCaption(workOrderNumberList
												.getValue())));
						map.put("BILL_DATE", CommonUtil
								.formatDateToDDMMMYYYY(date.getValue()));
						map.put("SALES_MAN", contractorSelect
								.getItemCaption(contractorSelect.getValue()));

						String type = "";
						if (status == 1) {
							type = "Cash Sale";
						} else {
							type = "Credit Sale";
						}
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
									.getItemProperty(TBC_NET_FINAL).getValue()
									.toString()), item
									.getItemProperty(TBC_UNIT).getValue()
									.toString(), item
									.getItemProperty(TBC_ITEM_CODE).getValue()
									.toString(), toDouble(item
									.getItemProperty(TBC_QTY).getValue()
									.toString()));

							total += toDouble(item
									.getItemProperty(TBC_NET_FINAL).getValue()
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
						report.setJrxmlFileName("Sales_Print_New");
						report.setReportFileName("SalesPrint");
						// report.setReportTitle("Sales Invoice");
						// report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						report.createReport(reportList, map);

						report.print();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (!isFinYearBackEntry()) {
				saveButton.setVisible(false);
				updateButton.setVisible(false);
				deleteButton.setVisible(false);
				Notification.show(getPropertyName("warning_transaction"),
						Type.WARNING_MESSAGE);
			}

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addListener(new Property.ValueChangeListener() {
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

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return pannel;
	}

	protected void reloadItemStocks() {
		try {
			List list = daoObj.getAllItems(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			itemSelectCombo.setContainerDataSource(bic);
			itemSelectCombo.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				itemSelectCombo.setValue((Long) getHttpSession().getAttribute(
						"saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void resetUnit() {
		try {
			Object temp = unitSelect.getValue();
			if (itemSelectCombo.getValue() != null) {
				ItemModel itm = daoObj.getItem((Long) itemSelectCombo
						.getValue());

				List lst = new ArrayList();
				lst.addAll(comDao.getAllItemUnitDetails(itm.getId()));
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

				unitSelect.setValue(null);
				unitSelect.setValue(temp);

			} else {

				List lst = new ArrayList();
				SCollectionContainer bic = SCollectionContainer.setList(lst,
						"id");
				unitSelect.setContainerDataSource(bic);
				unitSelect.setItemCaptionPropertyId("symbol");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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

	protected void reloadContractors() {
		try {
			List list = contDao
					.getAllActiveContractorNamesWithLedgerID(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			contractorSelect.setContainerDataSource(bic);
			contractorSelect.setItemCaptionPropertyId("first_name");

			if (session.getAttribute("new_id") != null) {
				contractorSelect
						.setValue((Long) session.getAttribute("new_id"));
				session.removeAttribute("new_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0, disc_ttl = 0, ttl_bfr_tax = 0, ttl_bfr_disc = 0, cess_ttl = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();

				if (taxEnable) {
					tax_ttl += (Double) item.getItemProperty(TBC_TAX_AMT)
							.getValue();
					cess_ttl += (Double) item.getItemProperty(TBC_CESS_AMT)
							.getValue();
				}

				net_ttl += (Double) item.getItemProperty(TBC_NET_FINAL)
						.getValue();
				disc_ttl += (Double) item.getItemProperty(TBC_DISCOUNT)
						.getValue();

				ttl_bfr_tax += (Double) item.getItemProperty(TBC_NET_TOTAL)
						.getValue();
				ttl_bfr_disc += (Double) item.getItemProperty(TBC_NET_FINAL)
						.getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_TAX_AMT, asString(roundNumber(tax_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));
			table.setColumnFooter(TBC_DISCOUNT, asString(roundNumber(disc_ttl)));
			table.setColumnFooter(TBC_CESS_AMT, asString(roundNumber(cess_ttl)));
			table.setColumnFooter(TBC_NET_TOTAL,
					asString(roundNumber(ttl_bfr_tax)));
			table.setColumnFooter(TBC_NET_FINAL,
					asString(roundNumber(ttl_bfr_disc)));

			double ship_charg = 0, excise_duty = 0;
			try {
				ship_charg = toDouble(shippingChargeTextField.getValue());
				excise_duty = toDouble(exciseDutyTextField.getValue());
			} catch (Exception e) {
				// TODO: handle exception
			}

			grandTotalAmtTextField.setNewValue(asString(roundNumber(net_ttl
					+ ship_charg + excise_duty)));

		} catch (Exception e) {
			// TODO: handle exception
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

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
					if (toDouble(unitPriceTextField.getValue()) < 0) {
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
					if (toDouble(quantityTextField.getValue()) <= 0) {
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

	public void visibleAddupdateButton(boolean AddVisible, boolean UpdateVisible) {
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
			Notification.show(getPropertyName("invalid_data"),
					Type.ERROR_MESSAGE);
		}
	}

	public void loadWorkOrders(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new WorkOrderModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllWorkOrderNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			workOrderNumberList.setContainerDataSource(bic);
			workOrderNumberList.setItemCaptionPropertyId("comments");

			workOrderNumberList.setValue(id);

			reloadItemStocks();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (shippingChargeTextField.getValue() == null
				|| shippingChargeTextField.getValue().equals("")) {
			setRequiredError(shippingChargeTextField,
					getPropertyName("invalid_data"), true);
			shippingChargeTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(shippingChargeTextField.getValue()) < 0) {
					setRequiredError(shippingChargeTextField,
							getPropertyName("invalid_data"), true);
					shippingChargeTextField.focus();
					ret = false;
				} else
					setRequiredError(shippingChargeTextField, null, false);
			} catch (Exception e) {
				setRequiredError(shippingChargeTextField,
						getPropertyName("invalid_data"), true);
				shippingChargeTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (exciseDutyTextField.getValue() == null
				|| exciseDutyTextField.getValue().equals("")) {
			setRequiredError(exciseDutyTextField,
					getPropertyName("invalid_data"), true);
			exciseDutyTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(exciseDutyTextField.getValue()) < 0) {
					setRequiredError(exciseDutyTextField,
							getPropertyName("invalid_data"), true);
					exciseDutyTextField.focus();
					ret = false;
				} else
					setRequiredError(exciseDutyTextField, null, false);
			} catch (Exception e) {
				setRequiredError(exciseDutyTextField,
						getPropertyName("invalid_data"), true);
				exciseDutyTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			itemSelectCombo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		/*
		 * if(buildingSelect.getValue()==null ||
		 * buildingSelect.getValue().equals("")){ setRequiredError(
		 * buildingSelect, "Select a building",true); buildingSelect.focus();
		 * ret=false; } else setRequiredError(buildingSelect , null,false);
		 */

		if (contractorSelect.getValue() == null
				|| contractorSelect.getValue().equals("")) {
			setRequiredError(contractorSelect,
					getPropertyName("invalid_selection"), true);
			contractorSelect.focus();
			ret = false;
		} else
			setRequiredError(contractorSelect, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		if (ret)
			calculateTotals();
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
	
	public SComboField getWorkOrderNumberList() {
		return workOrderNumberList;
	}

	public void setWorkOrderNumberList(SComboField workOrderNumberList) {
		this.workOrderNumberList = workOrderNumberList;
	}

	@Override
	public SComboField getBillNoFiled() {
		return workOrderNumberList;
	}
	
}