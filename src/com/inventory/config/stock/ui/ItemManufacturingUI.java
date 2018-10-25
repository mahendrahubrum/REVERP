package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.bean.ProductionBean;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ManufacturingDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingDetailsModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.config.stock.model.ManufacturingModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Mar 25, 2014
 */

public class ItemManufacturingUI extends SparkLogic {

	private static final long serialVersionUID = 1396957701704653474L;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";
	static String TBC_SO_ID = "po_id";
	static String TBC_SO = "Sales Order";
	static String TBC_BATCH = "Batch";
	static String TBC_STOCK_ID = "StockId";
	static String TBC_EXPENSE = "Expense";
	
	static String POP_TBL_ITEM = "Item";
	static String POP_TBL_QTY = "Required Quantity";
	static String POP_TBL_RES = "Reserved Quantity";
	static String POP_TBL_AVAIL = "Available Quantity";

	static String SO_TBL_ITEM = "Item";
	static String SO_TBL_QTY = "Quantity";
	static String SO_TBL_UNIT = "Unit";
	

	ManufacturingDao daoObj;

	SComboField productionsLists;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	STable childTable;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField batchTextField;
	STextField quantityTextField;
	STextField expenseField;
	SNativeSelect unitSelect;
	SComboField poComboField;
	private SButton soDetailsButton;

	SButton doneButton;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveProduction;
	SButton updateStockTransfer;
	SButton deleteStockTransfer;

	CommonMethodsDao comDao;

	OfficeDao pfcDao = new OfficeDao();

	ItemDao itemDao;

	SDateField date;
	SComboField itemSelect;

	STextField convertionQtyTextField, subconvertionQtyTextField;
	STextField convertedQtyTextField, subconvertedQtyTextField;

	SLabel itemName;

	SComboField subItemSelect;
	SNativeSelect subItemUnitSelect;
	STextField subItmQtyTextField;
	SButton addSubButton;
	SGridLayout addingSubGrid;

	SPopupView pop;

	UnitDao unitDao = new UnitDao();

	SFormLayout popLay;

	Set<ProductionBean> subItemsMap;

	private SButton stockAvailBtn;
	private SPopupView popup;
	private STable popTable;
	private SFormLayout popForm;

	private SPopupView soPopup;
	private STable soPopTable;
	private SFormLayout soPopForm;
	private SLabel customerLabel;

	private SalesOrderDao soDao;

	private SButton newSaleButton;

	@SuppressWarnings("deprecation")
	public ItemManufacturingUI() {

		itemName = new SLabel();

		subItemsMap = new HashSet<ProductionBean>();

		setId("Transfer");
		setSize(870, 500);

		comDao = new CommonMethodsDao();

		daoObj = new ManufacturingDao();
		itemDao = new ItemDao();
		soDao = new SalesOrderDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(9);
		addingGrid.setRows(2);

		addingSubGrid = new SGridLayout();
		addingSubGrid.setSizeFull();
		addingSubGrid.setColumns(9);
		addingSubGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(1);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);

		doneButton = new SButton(getPropertyName("done"));

		popLay = new SFormLayout();

		popLay.setWidth("840");
		popLay.setHeight("350");

		pop = new SPopupView("", popLay);

		form.setSizeFull();

		try {

			convertionQtyTextField = new STextField(
					getPropertyName("conv_qty"), 40);
			convertionQtyTextField.setStyleName("textfield_align_right");
			convertionQtyTextField
					.setDescription(getPropertyName("value_basic_to_selected"));
			convertionQtyTextField.setVisible(false);

			convertedQtyTextField = new STextField(
					getPropertyName("conrted_qty"), 60);
			convertedQtyTextField.setStyleName("textfield_align_right");
			convertedQtyTextField
					.setDescription(getPropertyName("conrted_qty_basic_unit"));
			convertedQtyTextField.setReadOnly(true);
			convertedQtyTextField.setVisible(false);

			subconvertionQtyTextField = new STextField(
					getPropertyName("conv_qty"), 40);
			subconvertionQtyTextField.setStyleName("textfield_align_right");
			subconvertionQtyTextField
					.setDescription(getPropertyName("value_basic_to_selected"));

			subconvertedQtyTextField = new STextField(
					getPropertyName("conrted_qty"), 60);
			subconvertedQtyTextField.setStyleName("textfield_align_right");
			subconvertedQtyTextField
					.setDescription(getPropertyName("conrted_qty_basic_unit"));
			subconvertedQtyTextField.setReadOnly(true);

			productionsLists = new SComboField(null, 125, null, "id",
					"comments");
			productionsLists.setInputPrompt("New");

			newSaleButton = new SButton();
			newSaleButton.setStyleName("createNewBtnStyle");
			newSaleButton.setDescription("Add new Sale");

			loadProductions("New");

			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date());

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(productionsLists);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("item_prodction_no")), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.setStyleName("master_border");

			quantityTextField = new STextField(getPropertyName("qty"), 60);
			expenseField= new STextField("Expense", 60);
			expenseField.setValue("0");
			batchTextField = new STextField(getPropertyName("batch"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			batchTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");

			subItemSelect = new SComboField(getPropertyName("item"), 250,
					itemDao.getAllManufacturingItems(getOfficeID()), "id",
					"name", true, "Select");
			subItemUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			subItmQtyTextField = new STextField(getPropertyName("qty"), 60);
			subItmQtyTextField.setStyleName("textfield_align_right");
			addSubButton = new SButton(null, getPropertyName("add_item"));
			addSubButton.setStyleName("addItemBtnStyle");

			addingSubGrid.addComponent(subItemSelect);
			addingSubGrid.addComponent(subItmQtyTextField);
			addingSubGrid.addComponent(subItemUnitSelect);
			addingSubGrid.addComponent(subconvertionQtyTextField);
			addingSubGrid.addComponent(subconvertedQtyTextField);
			addingSubGrid.addComponent(addSubButton);

			popForm = new SFormLayout();
			popup = new SPopupView(null, popForm);

			popTable = new STable(null, 600, 250);
			popTable.addContainerProperty(POP_TBL_ITEM, String.class, null,
					POP_TBL_ITEM, null, Align.LEFT);
			popTable.addContainerProperty(POP_TBL_QTY, String.class, null,
					POP_TBL_QTY, null, Align.CENTER);
			popTable.addContainerProperty(POP_TBL_RES, String.class, null,
					POP_TBL_RES, null, Align.CENTER);
			popTable.addContainerProperty(POP_TBL_AVAIL, String.class, null,
					POP_TBL_AVAIL, null, Align.CENTER);
			popForm.addComponent(popTable);

			SHorizontalLayout hlaySo = new SHorizontalLayout();

			List poList = new ArrayList();
			poList.addAll(soDao.getSalesOrderModelList(getOfficeID()));
			poList.add(0, new SalesOrderModel(0, "NONE"));
			poComboField = new SComboField(getPropertyName("sales_order"), 100,
					poList, "id", "ref_no");
			poComboField.setValue((long) 0);
			soDetailsButton = new SButton(null, "Sales Order Details");
			soDetailsButton.setStyleName("loadAllBtnStyle");

			soPopForm = new SFormLayout();
			soPopup = new SPopupView(null, soPopForm);

			customerLabel = new SLabel();
			soPopTable = new STable(null, 500, 250);
			soPopTable.addContainerProperty(SO_TBL_ITEM, String.class, null,
					SO_TBL_ITEM, null, Align.LEFT);
			soPopTable.addContainerProperty(SO_TBL_QTY, Double.class, null,
					SO_TBL_QTY, null, Align.CENTER);
			soPopTable.addContainerProperty(SO_TBL_UNIT, String.class, null,
					SO_TBL_UNIT, null, Align.CENTER);
			soPopForm.addComponent(customerLabel);
			soPopForm.addComponent(soPopTable);

			hlaySo.addComponent(poComboField);
			hlaySo.addComponent(soDetailsButton);
			hlaySo.addComponent(soPopup);
			hlaySo.setComponentAlignment(soDetailsButton, Alignment.BOTTOM_LEFT);

			itemSelect = new SComboField(getPropertyName("item"), 220,
					itemDao.getAllManufacturingItems(getOfficeID()), "id",
					"name", true, getPropertyName("select"));
			SHorizontalLayout hlay = new SHorizontalLayout();

			stockAvailBtn = new SButton(null,
					getPropertyName("avai_raw_materials"));
			stockAvailBtn.setStyleName("loadAllBtnStyle");
			hlay.addComponent(itemSelect);
			hlay.addComponent(stockAvailBtn);
			hlay.addComponent(popup);
			hlay.setComponentAlignment(stockAvailBtn, Alignment.BOTTOM_LEFT);

			addItemButton = new SButton(null, getPropertyName("add_item"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(hlay);

			addingGrid.addComponent(batchTextField);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			addingGrid.addComponent(expenseField);
			addingGrid.addComponent(hlaySo);

			addingGrid.addComponent(convertionQtyTextField);
			addingGrid.addComponent(convertedQtyTextField);

			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 2);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(7, 1);
			addingGrid.setColumnExpandRatio(8, 2);

			addingGrid.setWidth("810");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 810, 200);
			childTable = new STable(null, 780, 200);
			childTable.setWidth("810");
			childTable.setHeight("200");
			table.setMultiSelect(true);
			childTable.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, "ID", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					getPropertyName("item_code"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,
					getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					getPropertyName("unit_id"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class,
					null, getPropertyName("qty_basic_unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_SO_ID, Long.class, null, TBC_SO_ID,
					null, Align.CENTER);
			table.addContainerProperty(TBC_SO, String.class, null,
					getPropertyName("sales_order"), null, Align.CENTER);
			table.addContainerProperty(TBC_BATCH, Double.class, null,
					getPropertyName("batch"), null, Align.CENTER);
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
					TBC_STOCK_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_EXPENSE, Double.class, null,
					getPropertyName("expense"), null, Align.CENTER);

			childTable.addContainerProperty(TBC_SN, Integer.class, null, "#",
					null, Align.CENTER);
			childTable.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			childTable.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					getPropertyName("item_code"), null, Align.CENTER);
			childTable.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			childTable.addContainerProperty(TBC_QTY, Double.class, null,
					getPropertyName("qty"), null, Align.CENTER);
			childTable.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					getPropertyName("unit_id"), null, Align.CENTER);
			childTable.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			childTable.addContainerProperty(TBC_QTY_IN_BASIC_UNIT,
					Double.class, null, getPropertyName("qty_basic_unit"),
					null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);

			childTable.setColumnExpandRatio(TBC_SN, (float) 0.4);
			childTable.setColumnExpandRatio(TBC_ITEM_ID, 1);
			childTable.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			childTable.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			childTable.setColumnExpandRatio(TBC_QTY, 1);
			childTable.setColumnExpandRatio(TBC_UNIT_ID, 1);
			childTable.setColumnExpandRatio(TBC_UNIT, 1);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT,TBC_BATCH, TBC_QTY,TBC_EXPENSE,
					TBC_SO });

			childTable.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY, TBC_QTY_IN_BASIC_UNIT });

			table.setSizeFull();
			table.setSelectable(true);
			table.setNullSelectionAllowed(true);
			// childTable.setSizeFull();
			childTable.setSelectable(true);
			childTable.setNullSelectionAllowed(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("810");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			saveProduction = new SButton(getPropertyName("save"), 70);
			saveProduction.setStyleName("savebtnStyle");
			saveProduction.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateStockTransfer = new SButton(getPropertyName("update"), 80);
			updateStockTransfer.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updateStockTransfer.setStyleName("updatebtnStyle");

			deleteStockTransfer = new SButton(getPropertyName("Delete"), 78);
			deleteStockTransfer.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteStockTransfer.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveProduction);
			mainButtonLayout.addComponent(updateStockTransfer);
			mainButtonLayout.addComponent(deleteStockTransfer);
			updateStockTransfer.setVisible(false);
			deleteStockTransfer.setVisible(false);

			buttonsGrid.setColumnExpandRatio(0, 1);
			buttonsGrid.setColumnExpandRatio(1, 5);

			buttonsGrid.addComponent(mainButtonLayout, 1, 1);
			mainButtonLayout.setSpacing(true);
			buttonsGrid.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			form.addComponent(masterDetailsGrid);
			form.addComponent(pop);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("780");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			itemSelect.focus();

			popLay.addComponent(new SHorizontalLayout(new SLabel(null,
					getPropertyName("item_name"), 100), itemName));
			popLay.addComponent(childTable);
			// popLay.addComponent(addingSubGrid);
			// popLay.addComponent(doneButton);
			pop.setPrimaryStyleName("pop_style");

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					productionsLists.setValue(null);
				}
			});

			doneButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					pop.setPopupVisible(false);
					table.setValue(null);
				}
			});

			soDetailsButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						customerLabel.setValue("");
						poComboField.setComponentError(null);
						soPopTable.removeAllItems();
						if (poComboField.getValue() != null
								&& !poComboField.getValue().equals("")
								&& !poComboField.getValue().toString()
										.equals("0")) {
							SalesOrderModel ord = soDao
									.getSalesOrderModel((Long) poComboField
											.getValue());
							customerLabel.setValue("Customer : "
									+ ord.getCustomer().getName());
							SalesInventoryDetailsModel detMdl;
							List mapList = ord.getOrder_details_list();
							if (mapList != null) {
								Object[] row;
								Iterator iter = mapList.iterator();
								while (iter.hasNext()) {
									detMdl = (SalesInventoryDetailsModel) iter
											.next();

									row = new Object[] {(long)0,
											detMdl.getItem().getName(),
											detMdl.getQunatity(),
											detMdl.getUnit().getSymbol(),(long)0 };
									soPopTable.addItem(row, soPopTable
											.getItemIds().size() + 1);
								}
							}
							soPopup.setPopupVisible(true);

						} else {
							setRequiredError(poComboField,
									getPropertyName("invalid_selection"), true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			stockAvailBtn.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						itemSelect.setComponentError(null);
						popTable.removeAllItems();
						if (itemSelect.getValue() != null
								&& !itemSelect.getValue().equals("")) {
							List mapList = new ManufacturingDao()
									.getItemForManufacturing((Long) itemSelect
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
											mapMdl.getSubItem()
													.getReservedQuantity()
													+ " "
													+ mapMdl.getSubItem()
															.getUnit()
															.getSymbol(),
											mapMdl.getSubItem()
													.getCurrent_balalnce()
													- mapMdl.getSubItem()
															.getReservedQuantity()
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
							setRequiredError(itemSelect,
									getPropertyName("invalid_selection"), true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			saveProduction.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isValid()) {

							List list = new ArrayList();

							ManufacturingModel objModel;

							long production_no = getNextSequence(
									"Manufacturing Number", getLoginID());

							Iterator it2;
							ProductionBean bean;
							Object parent;
							Set set;
							Iterator it1 = table.getItemIds().iterator();

							List detailsList = new ArrayList();
							ManufacturingDetailsModel details = null;
							ManufacturingMapModel mapModel = null;
							List mapList = null;
							Iterator mapIter;
							
							double qty = 0;
							double convQTY = 0;
							double mainConQty = 0;

							while (it1.hasNext()) {
								
								detailsList = new ArrayList();
								
								parent = it1.next();
								Item item = table.getItem(parent);
								objModel = new ManufacturingModel();

								objModel.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								objModel.setManufacturing_no(production_no);
								objModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								objModel.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setDate(CommonUtil
										.getSQLDateFromUtilDate(date.getValue()));
								objModel.setQty_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
										.getValue());
								objModel.setExpense((Double) item
										.getItemProperty(TBC_EXPENSE)
										.getValue());
								objModel.setSalesOrderId((Long) item
										.getItemProperty(TBC_SO_ID).getValue());
								objModel.setBatch((Double) item
										.getItemProperty(TBC_BATCH).getValue());

								mapList = daoObj
										.getItemForManufacturing((Long) item
												.getItemProperty(TBC_ITEM_ID)
												.getValue());
								mainConQty=0;
								mainConQty=objModel.getQty_in_basic_unit()/objModel.getQuantity();

								mapIter = mapList.iterator();
								
								while (mapIter.hasNext()) {
									qty=0;
									convQTY = 0;
									mapModel = (ManufacturingMapModel) mapIter
											.next();
									details = new ManufacturingDetailsModel();
									details.setItem(mapModel.getSubItem());

									qty = mainConQty*mapModel.getQuantity()
											* (objModel.getQuantity() / mapModel
													.getMaster_quantity());
//									convQTY=comDao.getConvertionQty(mapModel.getSubItem().getId(), mapModel.getUnit().getId(), 0);
									convQTY=(mapModel.getQty_in_basic_unit()/mapModel.getQuantity());
									details.setQuantity(qty);
									details.setQuantityInBasicUnit(qty*convQTY);
									details.setUnit(mapModel.getUnit());
									detailsList.add(details);
								}
								objModel.setManufacturing_details_list(detailsList);

								list.add(objModel);
							}

							daoObj.save(list);

							loadProductions(asString(production_no));

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							reloadStock();

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			productionsLists.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						removeAllErrors();

						updateStockTransfer.setVisible(true);
						deleteStockTransfer.setVisible(true);
						saveProduction.setVisible(false);
						if (productionsLists.getValue() != null
								&& !productionsLists.getValue().toString()
										.equals("New")) {

							List poObj = daoObj
									.getProductionDetails(toLong(productionsLists
											.getValue().toString()),getOfficeID());

							table.setVisibleColumns(new String[] { TBC_SN,TBC_ID,
									TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_BATCH,
									TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
									TBC_QTY_IN_BASIC_UNIT, TBC_SO_ID, TBC_EXPENSE,TBC_SO,TBC_STOCK_ID });

							table.removeAllItems();

							subItemsMap = new HashSet<ProductionBean>();

							int parent_id = 1, child_id = 1;
							long soId = 0;
							String soNo = "";
							Iterator it2;
							Iterator it = poObj.iterator();
							ManufacturingModel invObj;
							SalesOrderModel soMld;
							while (it.hasNext()) {
								invObj = (ManufacturingModel) it.next();
								soId = 0;
								soNo = "NONE";
								if (invObj.getSalesOrderId() != 0) {
									soMld = soDao.getSalesOrderModel(invObj
											.getSalesOrderId());
									soId = soMld.getId();
									soNo = asString(soMld
											.getOrder_details_list());
								}

								table.addItem(
										new Object[] {
												parent_id,invObj.getId(),
												invObj.getItem().getId(),
												invObj.getItem().getItem_code(),
												invObj.getItem().getName(),invObj.getBatch(),
												invObj.getQuantity(),
												invObj.getUnit().getId(),
												invObj.getUnit().getSymbol(),
												invObj.getQty_in_basic_unit(),
												soId,
												invObj.getExpense(),
												soNo,invObj.getStockId() }, parent_id);

								// child_id=1;
								// it2=invObj.getDetails_list().iterator();
								// while(it2.hasNext()) {
								// objDetModel=(ProductionDetailsModel)
								// it2.next();
								//
								// subItemsMap.add(new ProductionBean(parent_id,
								// child_id, objDetModel.getItem().getId(),
								// objDetModel.getUnit().getId() ,
								// objDetModel.getQuantity()
								// , objDetModel.getItem().getItem_code(),
								// objDetModel.getItem().getName(),
								// objDetModel.getItem().getName(),objDetModel.getQty_in_basic_unit()));
								// child_id++;
								// }

								date.setValue(invObj.getDate());

								parent_id++;

							}

							table.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_BATCH, TBC_UNIT,
									TBC_QTY,TBC_EXPENSE ,TBC_SO });

							// date.setValue(poObj.getDate());

						} else {
							table.removeAllItems();
							subItemsMap = new HashSet<ProductionBean>();

							childTable.removeAllItems();
							date.setValue(getWorkingDate());

							saveProduction.setVisible(true);
							updateStockTransfer.setVisible(false);
							deleteStockTransfer.setVisible(false);
						}

						itemSelect.setValue(null);
						itemSelect.focus();
						quantityTextField.setValue("0.0");
						batchTextField.setValue("0.0");

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

			updateStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							List list = new ArrayList();

							ManufacturingModel objModel;

							long production_no = toLong(productionsLists
									.getValue().toString());

							Iterator it2;
							ProductionBean bean;
							Object parent;
							Set set;
							Iterator it1 = table.getItemIds().iterator();

							List detailsList = new ArrayList();
							ManufacturingDetailsModel details = null;
							ManufacturingMapModel mapModel = null;
							List mapList = null;
							Iterator mapIter;
							
							double qty = 0;
							double convQTY = 0;
							double mainConQty = 0;

							while (it1.hasNext()) {
								
								detailsList = new ArrayList();
								
								parent = it1.next();
								Item item = table.getItem(parent);
								objModel = new ManufacturingModel();

								objModel.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								objModel.setManufacturing_no(production_no);
								objModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								objModel.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setExpense((Double) item
										.getItemProperty(TBC_EXPENSE)
										.getValue());
								objModel.setDate(CommonUtil
										.getSQLDateFromUtilDate(date.getValue()));
								objModel.setQty_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
										.getValue());
								objModel.setSalesOrderId((Long) item
										.getItemProperty(TBC_SO_ID).getValue());
								objModel.setSalesOrderId((Long) item
										.getItemProperty(TBC_SO_ID).getValue());
								objModel.setBatch((Double) item
										.getItemProperty(TBC_BATCH).getValue());
								objModel.setId((Long) item
										.getItemProperty(TBC_ID).getValue());
								objModel.setStockId((Long) item
										.getItemProperty(TBC_STOCK_ID).getValue());

								mapList = daoObj
										.getItemForManufacturing((Long) item
												.getItemProperty(TBC_ITEM_ID)
												.getValue());
								
								mainConQty=0;
								mainConQty=objModel.getQty_in_basic_unit()/objModel.getQuantity();

								mapIter = mapList.iterator();
								while (mapIter.hasNext()) {
									qty = 0;
									convQTY = 0;
									
									mapModel = (ManufacturingMapModel) mapIter
											.next();
									details = new ManufacturingDetailsModel();
									details.setItem(mapModel.getSubItem());
									qty = mainConQty*mapModel.getQuantity()
											* (objModel.getQuantity() / mapModel
													.getMaster_quantity());
//									convQTY=comDao.getConvertionQty(mapModel.getSubItem().getId(), mapModel.getUnit().getId(), 0);
									convQTY=(mapModel.getQty_in_basic_unit()/mapModel.getQuantity());
									details.setQuantity(qty);
									details.setQuantityInBasicUnit(qty*convQTY);
									details.setUnit(mapModel.getUnit());
									detailsList.add(details);
								}
								objModel.setManufacturing_details_list(detailsList);

								list.add(objModel);
							}

							daoObj.update(list, production_no,getOfficeID());

							loadProductions(asString(production_no));

							reloadStock();

							Notification.show(getPropertyName("Success"),
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			deleteStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (productionsLists.getValue() != null
							&& !productionsLists.getValue().toString()
									.equals("0")) {
						
						boolean blocked = false;
						Collection selectedItems = table.getItemIds();
						Iterator it1 = selectedItems.iterator();
						while (it1.hasNext()) {
							Item item = table.getItem(it1.next());
							try {
								if (comDao.isStockBlocked((Long) item.getItemProperty(TBC_STOCK_ID).getValue() )) {
									blocked = true;
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						if (!blocked) {
						
						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete(
														toLong(productionsLists
																.getValue()
																.toString()),
														CommonUtil
																.getSQLDateFromUtilDate(date
																		.getValue()),getOfficeID());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);
												loadProductions("New");

												reloadStock();

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}
						else {
							Notification.show(getPropertyName("cannot_delete"),
									Type.ERROR_MESSAGE);
						}

				}	
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {
						Object obj = selectedItems.iterator().next();

						Item item = table.getItem(obj);

						itemName.setValue(item.getItemProperty(TBC_ITEM_NAME)
								.getValue().toString());

						// pop.setPopupVisible(true);
						// pop.setHideOnMouseOut(false);
						loadTableData((Long) item.getItemProperty(TBC_ITEM_ID)
								.getValue());


						itemSelect.setValue(item.getItemProperty(TBC_ITEM_ID)
								.getValue());
						
						batchTextField.setValue(""
								+ item.getItemProperty(TBC_BATCH).getValue());
						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						
						expenseField.setValue(""
								+ item.getItemProperty(TBC_EXPENSE).getValue());


						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());

						convertionQtyTextField
								.setNewValue(asString(toDouble(item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
										.getValue().toString())
										/ toDouble(item
												.getItemProperty(TBC_QTY)
												.getValue().toString())));

						convertedQtyTextField.setNewValue(item
								.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
								.getValue().toString());

						poComboField.setNewValue(toLong(item
								.getItemProperty(TBC_SO_ID).getValue()
								.toString()));

						visibleAddupdateStockTransfer(false, true);

						batchTextField.focus();

					} else {
						itemSelect.setValue(null);
						batchTextField.setValue("0.0");
						quantityTextField.setValue("0.0");
						// officeCombo.setValue(null);
						poComboField.setNewValue((long) 0);
						visibleAddupdateStockTransfer(true, false);

						itemSelect.focus();
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

							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());

							double qty = 0, totalSameStockQty = 0, qty_bsc;

							qty = toDouble(quantityTextField.getValue());
							qty_bsc = toDouble(convertedQtyTextField.getValue());

							int id = 0;
							Item item;
							Object obj;
							Iterator itr2 = table.getItemIds().iterator();
							while (itr2.hasNext()) {
								obj = itr2.next();
								item = table.getItem(obj);

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelect.getValue()
												.toString())
										&& item.getItemProperty(TBC_SO_ID)
												.getValue()
												.toString()
												.equals(poComboField.getValue()
														.toString())) {

									item.getItemProperty(TBC_QTY).setValue(
											(Double) item.getItemProperty(
													TBC_QTY).getValue()
													+ qty);

									already_added_item = true;

									id = (Integer) obj;
								}
							}

							totalSameStockQty += qty;

							if (!already_added_item) {

								table.setVisibleColumns(new String[] { TBC_SN,TBC_ID,
										TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME,TBC_BATCH, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT, TBC_QTY_IN_BASIC_UNIT,
										TBC_SO_ID, TBC_EXPENSE,TBC_SO,TBC_STOCK_ID });

								UnitModel objUnit = unitDao
										.getUnit((Long) unitSelect.getValue());

								int ct = 0;
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									id = (Integer) it.next();
								}
								id++;

								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,(long)0,
												stk.getId(),
												stk.getItem_code(),
												stk.getName(),toDouble(batchTextField.getValue()),
												qty,
												objUnit.getId(),
												objUnit.getSymbol(),
												qty_bsc,
												(Long) poComboField.getValue(),
												toDouble(expenseField.getValue().toString()),
												poComboField.getItemCaption(poComboField
																.getValue()),(long)0 },
										id);

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_BATCH, TBC_UNIT,
										TBC_QTY, TBC_EXPENSE,TBC_SO });

								itemSelect.setValue(null);
								itemSelect.focus();
								batchTextField.setValue("0.0");
								quantityTextField.setValue("0.0");
							}

							calculateTotals();

							itemSelect.setValue(null);
							itemSelect.focus();
							batchTextField.setValue("0.0");
							quantityTextField.setValue("0.0");

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			addSubButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (childTable.getComponentError() != null)
							setRequiredError(childTable, null, false);

						if (isSubAddingValid()) {

							boolean already_added_item = false;

							ItemModel stk = itemDao
									.getItem((Long) subItemSelect.getValue());

							double qty = 0, totalSameStockQty = 0, qty_in_bsc;

							qty = toDouble(subItmQtyTextField.getValue());
							qty_in_bsc = toDouble(subconvertedQtyTextField
									.getValue());
							Item item;
							Iterator itr2 = childTable.getItemIds().iterator();
							while (itr2.hasNext()) {
								item = childTable.getItem(itr2.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(subItemSelect.getValue()
												.toString())) {
									//
									// item.getItemProperty(TBC_QTY).setValue((Double)
									// item
									// .getItemProperty(TBC_QTY)
									// .getValue()+qty);

									already_added_item = true;

								}
							}

							totalSameStockQty += qty;

							if (!already_added_item) {

								childTable.setVisibleColumns(new String[] {
										TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT, TBC_QTY_IN_BASIC_UNIT });

								UnitModel objUnit = unitDao
										.getUnit((Long) subItemUnitSelect
												.getValue());

								int id = 0, ct = 0;
								Iterator it = childTable.getItemIds()
										.iterator();
								while (it.hasNext()) {
									id = (Integer) it.next();
								}
								id++;

								childTable.addItem(
										new Object[] {
												childTable.getItemIds().size() + 1,
												stk.getId(),
												stk.getItem_code(),
												stk.getName(), qty,
												objUnit.getId(),
												objUnit.getSymbol(), qty_in_bsc },
										id);

								subItemsMap.add(new ProductionBean(
										(Integer) ((Set) table.getValue())
												.iterator().next(), id, stk
												.getId(), objUnit.getId(), qty,
										stk.getItem_code(), stk.getName(),
										objUnit.getSymbol(), qty_in_bsc));

								childTable.setVisibleColumns(new String[] {
										TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME,
										TBC_UNIT, TBC_QTY });

								subItemSelect.setValue(null);
								subItemSelect.focus();
								subItmQtyTextField.setValue("0.0");

								setRequiredError(subItemSelect, null, false);

								subItemSelect.setValue(null);
								subItemSelect.focus();
								subItmQtyTextField.setValue("0.0");

							} else {
								setRequiredError(subItemSelect,
										getPropertyName("item_exist"), true);

							}

							// officeCombo.setValue(null);
						}
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

							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());

							Collection selectedItems = (Collection) table
									.getValue();

							double qty = toDouble(quantityTextField.getValue()), totalSameStockQty = 0, totalUsedQty = 0;

							Item selectedItem = table.getItem(selectedItems
									.iterator().next());

							Item item;
							Iterator itr2 = table.getItemIds().iterator();
							while (itr2.hasNext()) {
								item = table.getItem(itr2.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelect.getValue()
												.toString())
										&& item.getItemProperty(TBC_SO_ID)
												.getValue()
												.toString()
												.equals(poComboField.getValue()
														.toString())) {
									totalSameStockQty += (Double) item
											.getItemProperty(TBC_QTY)
											.getValue();
								}
							}
							totalSameStockQty += qty;

							// if (totalSameStockQty <=
							// (stk.getCurrent_balalnce() + totalUsedQty)) {

							item = table.getItem(selectedItems.iterator()
									.next());

							qty = toDouble(quantityTextField.getValue());

							UnitModel objUnit = unitDao
									.getUnit((Long) unitSelect.getValue());

							item.getItemProperty(TBC_ITEM_ID).setValue(
									stk.getId());
							item.getItemProperty(TBC_ITEM_CODE).setValue(
									stk.getItem_code());
							item.getItemProperty(TBC_EXPENSE).setValue(
									toDouble(expenseField
											.getValue()));
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									stk.getName());
							item.getItemProperty(TBC_QTY).setValue(qty);
							item.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
									.setValue(
											toDouble(convertedQtyTextField
													.getValue()));

							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							
							item.getItemProperty(TBC_BATCH).setValue(toDouble(batchTextField
									.getValue()));
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());
							item.getItemProperty(TBC_SO_ID).setValue(
									(Long) poComboField.getValue());
							item.getItemProperty(TBC_SO).setValue(
									poComboField.getItemCaption(poComboField
											.getValue()));

							table.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_BATCH, TBC_UNIT,
									TBC_QTY, TBC_EXPENSE,TBC_SO });

							itemSelect.setValue(null);
							batchTextField.setValue("0.0");
							quantityTextField.setValue("0.0");

							visibleAddupdateStockTransfer(true, false);

							itemSelect.focus();

							table.setValue(null);

							calculateTotals();

							/*
							 * } else { setRequiredError( quantityTextField,
							 * "No sufficient quantity available. Bal : " +
							 * (stk.getCurrent_balalnce() + totalUsedQty -
							 * totalSameStockQty + qty), true);
							 * quantityTextField.focus(); }
							 */
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			itemSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (itemSelect.getValue() != null) {
									ItemModel stk = itemDao
											.getItem((Long) itemSelect
													.getValue());
									unitSelect.setValue(stk.getUnit().getId());
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
							quantityTextField.setValue("0");
							batchTextField.setValue("0");
							batchTextField.focus();
							batchTextField.selectAll();

						}
					});

			unitSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (unitSelect.getValue() != null) {
									if (itemSelect.getValue() != null) {

										ItemModel itm = itemDao
												.getItem((Long) itemSelect
														.getValue());

										if (((Long) unitSelect.getValue()) == itm
												.getUnit().getId()) {
											convertionQtyTextField
													.setValue("1");
											convertionQtyTextField
													.setVisible(false);
											convertedQtyTextField
													.setVisible(false);
										} else {
											convertionQtyTextField
													.setVisible(true);
											convertedQtyTextField
													.setVisible(true);

											convertionQtyTextField
													.setCaption("Qty - "
															+ itm.getUnit()
																	.getSymbol());
											convertedQtyTextField
													.setCaption("Qty - "
															+ itm.getUnit()
																	.getSymbol());

											double cnvr_qty = comDao.getConvertionRate(
													itm.getId(),
													(Long) unitSelect
															.getValue(), 0);

											convertionQtyTextField
													.setValue(asString(cnvr_qty));

										}

										if (quantityTextField.getValue() != null
												&& !quantityTextField
														.getValue().equals("")) {

											convertedQtyTextField.setNewValue(asString(Double
													.parseDouble(quantityTextField
															.getValue())
													* Double.parseDouble(convertionQtyTextField
															.getValue())));

										}

									}
								} else {
									convertionQtyTextField.setValue("1");
									convertionQtyTextField.setVisible(false);
									convertedQtyTextField.setVisible(false);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			quantityTextField.setImmediate(true);

			quantityTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateNetPrice();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			convertionQtyTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								try {
									if (convertionQtyTextField.getValue()
											.equals("")
											|| toDouble(convertionQtyTextField
													.getValue()) <= 0) {
										convertionQtyTextField.setValue("1");
									}
								} catch (Exception e) {
									convertionQtyTextField.setValue("1");
								}

								calculateNetPrice();

							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			subItemSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (subItemSelect.getValue() != null) {
									ItemModel stk = itemDao
											.getItem((Long) subItemSelect
													.getValue());
									subItemUnitSelect.setValue(stk.getUnit()
											.getId());
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
							subItmQtyTextField.focus();
							subItmQtyTextField.selectAll();

						}
					});

			subItemUnitSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (subItemUnitSelect.getValue() != null) {
									if (subItemSelect.getValue() != null) {

										ItemModel itm = itemDao
												.getItem((Long) subItemSelect
														.getValue());

										if (((Long) subItemUnitSelect
												.getValue()) == itm.getUnit()
												.getId()) {
											subconvertionQtyTextField
													.setValue("1");
											subconvertionQtyTextField
													.setVisible(false);
											subconvertedQtyTextField
													.setVisible(false);
										} else {
											subconvertionQtyTextField
													.setVisible(true);
											subconvertedQtyTextField
													.setVisible(true);

											subconvertedQtyTextField
													.setCaption("Qty - "
															+ itm.getUnit()
																	.getSymbol());

											double cnvr_qty = comDao.getConvertionRate(
													itm.getId(),
													(Long) subItemUnitSelect
															.getValue(), 0);

											subconvertionQtyTextField
													.setValue(asString(cnvr_qty));

										}

										if (subItmQtyTextField.getValue() != null
												&& !subItmQtyTextField
														.getValue().equals("")) {

											subconvertedQtyTextField.setNewValue(asString(Double
													.parseDouble(subItmQtyTextField
															.getValue())
													* Double.parseDouble(subconvertionQtyTextField
															.getValue())));

										}

										calculateSubNetPrice();

									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
							}

						}
					});

			subItmQtyTextField.setImmediate(true);

			subItmQtyTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								calculateSubNetPrice();
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
							}

						}
					});
			

			batchTextField.setImmediate(true);

			batchTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if(itemSelect.getValue()!=null&&!itemSelect.getValue().equals("")){
									
									ManufacturingMapModel mdl;
									List ls=daoObj.getManufacturingMapDetails(toLong(itemSelect.getValue().toString()));
									if(ls!=null&&ls.size()>0){
										mdl=(ManufacturingMapModel) ls.get(0);
										double batch=mdl.getMaster_quantity()*toDouble(batchTextField.getValue());
										quantityTextField.setValue(""+batch);
									}
								}
							} catch (Exception e) {
								quantityTextField.setValue("0");
							}

						}
					});

			subconvertionQtyTextField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								try {
									if (subItmQtyTextField.getValue()
											.equals("")
											|| toDouble(subconvertionQtyTextField
													.getValue()) <= 0) {
										subconvertionQtyTextField.setValue("1");
									}
								} catch (Exception e) {
									subconvertionQtyTextField.setValue("1");
								}

								calculateSubNetPrice();

							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
							}

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

			table.addShortcutListener(new ShortcutListener("Add New",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadProductions("New");
				}
			});

			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
					pop.setPopupVisible(false);
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

			final Action actionsUBDelete = new Action("Delete");

			childTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionsUBDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteSubItem();
				}

			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	private void loadTableData(long itemId) {
		try {

			List poObj;

			poObj = daoObj.getManufacturingMapDetails(itemId);
			childTable.removeAllItems();
			childTable.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_ID,
					TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
					TBC_UNIT, TBC_QTY_IN_BASIC_UNIT });

			childTable.removeAllItems();

			int parent_id = 1;
			Iterator it2;
			Iterator it = poObj.iterator();
			ManufacturingMapModel invObj;
			while (it.hasNext()) {
				invObj = (ManufacturingMapModel) it.next();

				childTable.addItem(new Object[] { parent_id,
						invObj.getSubItem().getId(),
						invObj.getSubItem().getItem_code(),
						invObj.getSubItem().getName(), invObj.getQuantity(),
						invObj.getUnit().getId(), invObj.getUnit().getSymbol(),
						invObj.getQty_in_basic_unit() }, parent_id);

				parent_id++;

			}

			childTable.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void calculateNetPrice() {

		try {

			convertedQtyTextField.setNewValue(asString(Double
					.parseDouble(quantityTextField.getValue())
					* Double.parseDouble(convertionQtyTextField.getValue())));

		} catch (Exception e) {
		}
	}

	public void calculateSubNetPrice() {

		try {

			subconvertedQtyTextField
					.setNewValue(asString(Double.parseDouble(subItmQtyTextField
							.getValue())
							* Double.parseDouble(subconvertionQtyTextField
									.getValue())));

		} catch (Exception e) {
		}
	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("qty_greater_zero"), true);
						quantityTextField.focus();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}
			if (expenseField.getValue() == null
					|| expenseField.getValue().equals("")) {
				setRequiredError(expenseField,
						getPropertyName("invalid_data"), true);
				expenseField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(expenseField.getValue()) < 0) {
						setRequiredError(expenseField,
								getPropertyName("invalid_data"), true);
						expenseField.focus();
						ret = false;
					} else
						setRequiredError(expenseField, null, false);
				} catch (Exception e) {
					setRequiredError(expenseField,
							getPropertyName("invalid_data"), true);
					expenseField.focus();
					ret = false;
					// TODO: handle exception
				}
			}
			
			if (batchTextField.getValue() == null
					|| batchTextField.getValue().equals("")) {
				setRequiredError(batchTextField,
						getPropertyName("invalid_data"), true);
				batchTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(batchTextField.getValue()) < 0) {
						setRequiredError(batchTextField,
								getPropertyName("qty_greater_zero"), true);
						batchTextField.focus();
						ret = false;
					} else
						setRequiredError(batchTextField, null, false);
				} catch (Exception e) {
					setRequiredError(batchTextField,
							getPropertyName("invalid_data"), true);
					batchTextField.focus();
					ret = false;
				}
			}

			if (itemSelect.getValue() == null
					|| itemSelect.getValue().equals("")) {
				setRequiredError(itemSelect,
						getPropertyName("invalid_selection"), true);
				itemSelect.focus();
				ret = false;
			} else
				setRequiredError(itemSelect, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	public boolean isSubAddingValid() {
		boolean ret = true;
		try {

			if (subItmQtyTextField.getValue() == null
					|| subItmQtyTextField.getValue().equals("")) {
				setRequiredError(subItmQtyTextField,
						getPropertyName("invalid_data"), true);
				subItmQtyTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(subItmQtyTextField.getValue()) <= 0) {
						setRequiredError(subItmQtyTextField,
								"Quantity must be greater than Zero", true);
						subItmQtyTextField.focus();
						ret = false;
					} else
						setRequiredError(subItmQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(subItmQtyTextField,
							getPropertyName("invalid_data"), true);
					subItmQtyTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (subItemSelect.getValue() == null
					|| subItemSelect.getValue().equals("")) {
				setRequiredError(subItemSelect,
						getPropertyName("qty_greater_zero"), true);
				subItemSelect.focus();
				ret = false;
			} else
				setRequiredError(subItemSelect, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	public boolean isValidChildForAll() {
		boolean avail = true;
		try {
			childTable.setComponentError(null);
			Iterator it2;
			ProductionBean bean;
			Object parent;
			boolean childAvail;
			Set set;
			Iterator it1 = table.getItemIds().iterator();
			while (it1.hasNext()) {
				parent = it1.next();
				childAvail = false;
				it2 = subItemsMap.iterator();
				while (it2.hasNext()) {
					bean = (ProductionBean) it2.next();
					if (bean.getParent_id() == (Integer) parent) {
						childAvail = true;
					}
				}

				if (!childAvail) {
					set = new HashSet();
					set.add(parent);
					table.setValue(null);
					table.setValue(set);
					setRequiredError(childTable,
							getPropertyName("add_materials_production"), true);

					avail = false;
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return avail;
	}

	public void visibleAddupdateStockTransfer(boolean AddVisible,
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
			itemSelect.focus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteSubItem() {
		try {

			if (childTable.getValue() != null) {

				Iterator it2;
				ProductionBean bean;
				Collection selectedItems = (Collection) childTable.getValue();
				Object child;
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					child = it1.next();
					childTable.removeItem(child);

					it2 = subItemsMap.iterator();
					while (it2.hasNext()) {
						bean = (ProductionBean) it2.next();

						if (bean.getParent_id() == (Integer) ((Set) table
								.getValue()).iterator().next()
								&& bean.getChild_id() == (Integer) child) {
							it2.remove();
						}

					}

				}

				int SN = 0;
				Item newitem;
				Iterator it = childTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = childTable.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

				calculateTotals();
			}
			subItemSelect.focus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadProductions(String id) {
		try {
			productionsLists.removeAllItems();
			productionsLists.addItem("New");
			Iterator it = daoObj.getAllProductionNumbers(getOfficeID())
					.iterator();
			while (it.hasNext()) {
				productionsLists.addItem(asString(it.next()));
			}

			productionsLists.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadStock() {
		try {

			List list = new ArrayList();
			list.addAll(itemDao.getAllAffectAllItems(getOfficeID()));
			list.addAll(itemDao.getAllManufacturingItems(getOfficeID()));
			
			SCollectionContainer bic = SCollectionContainer.setList(
					list, "id");
			itemSelect.setContainerDataSource(bic);
			itemSelect.setItemCaptionPropertyId("name");

			SCollectionContainer bic1 = SCollectionContainer.setList(
					list, "id");
			subItemSelect.setContainerDataSource(bic1);
			subItemSelect.setItemCaptionPropertyId("name");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			itemSelect.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		// if (ret) {
		// ret=isValidChildForAll();
		// }
		return ret;
	}

	public void removeAllErrors() {
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (itemSelect.getComponentError() != null)
			setRequiredError(itemSelect, null, false);
	}

	public Boolean getHelp() {
		return null;
	}

	public SComboField getProductionsLists() {
		return productionsLists;
	}

	public void setProductionsLists(SComboField productionsLists) {
		this.productionsLists = productionsLists;
	}

	@Override
	public SComboField getBillNoFiled() {
		return productionsLists;
	}
	
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
