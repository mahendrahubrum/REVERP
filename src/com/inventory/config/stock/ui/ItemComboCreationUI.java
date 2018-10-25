package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.bean.ProductionBean;
import com.inventory.config.stock.dao.ItemComboCreationDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemComboDetailsModel;
import com.inventory.config.stock.model.ItemComboModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.ItemReportBean;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SPopupView;
import com.webspark.Components.STabSheet;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 29, 2014
 */
public class ItemComboCreationUI extends SparkLogic {

	private static final long serialVersionUID = -4225321716927995508L;
	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";

	static String POP_TBL_ITEM = "Item";
	static String POP_TBL_QTY = "Required Quantity";
	static String POP_TBL_RES = "Reserved Quantity";
	static String POP_TBL_AVAIL = "Available Quantity";

	static String SO_TBL_ITEM = "Item";
	static String SO_TBL_QTY = "Quantity";
	static String SO_TBL_UNIT = "Unit";

	ItemComboCreationDao daoObj;

	SComboField comboItemField;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;

	SButton doneButton;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveProduction;
	SButton releaseProduction;

	CommonMethodsDao comDao;

	ItemDao itemDao;
	PurchaseDao purchDao;

	SDateField date;
	SComboField itemSelect;
	private SComboField catalogNoField;
	private SButton catalogButton;

	STextField convertionQtyTextField;
	STextField convertedQtyTextField;

	SLabel itemName;

	SPopupView pop;

	UnitDao unitDao;

	SFormLayout popLay;

	private SPopupView popup;
	private STable popTable;
	private SFormLayout popForm;

	STabSheet tab;

	private SDialogBox newItemWindow;
	private ItemPanel itemPanel;
	private SButton newItemButton;

	private STextField quantityField;
	private STextField unitField;
	private STextField barcodeField;

	WrappedSession session;
	SettingsValuePojo settings;

	private SFormLayout releaseFormLayout;
	private SComboField releaseComboField;
	private STextField releaseQtyField;

	private double maxReleaseQuantity;

	private SLabel tableDescLabel;

	private STextField barcodeQtyField;
	private SButton barcodeButton;

	private SWindow window;

	long stockID;

	@Override
	public SPanel getGUI() {

		SPanel panel = new SPanel();
		panel.setSizeFull();

		stockID = 0;

		itemName = new SLabel();
		unitDao = new UnitDao();

		tab = new STabSheet(null, 900, 480);

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		setSize(910, 550);

		comDao = new CommonMethodsDao();

		daoObj = new ItemComboCreationDao();
		itemDao = new ItemDao();
		purchDao = new PurchaseDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(11);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSpacing(true);
		masterDetailsGrid.setColumns(12);
		masterDetailsGrid.setRows(1);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);

		doneButton = new SButton(getPropertyName("done"));

		popLay = new SFormLayout();

		popLay.setWidth("760");
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

			// loadProductions("New");

			comboItemField = new SComboField(null, 200, null, "id", "name",
					false, "SELECT");
			date = new SDateField(null, 100, getDateFormat(), new Date());
			quantityField = new STextField(null, 50);
			quantityField.setValue("0");
			unitField = new STextField(null, 80);
			unitField.setValue("0");
			barcodeField = new STextField(null, 100);

			loadComboItems((long) 0);

			newItemWindow = new SDialogBox("Add Item", 500, 600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			itemPanel.getAffectType().setValue(
					SConstants.affect_type.MANUFACTURING);
			newItemWindow.addComponent(itemPanel);

			newItemButton = new SButton(null, "Add New Item");
			newItemButton.setStyleName("addNewBtnStyle");

			SHorizontalLayout hrz1 = new SHorizontalLayout();
			hrz1.addComponent(comboItemField);
			hrz1.addComponent(newItemButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("item")),
					1, 0);
			masterDetailsGrid.addComponent(hrz1, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("quantity")), 3, 0);
			masterDetailsGrid.addComponent(quantityField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("unit_price")), 5, 0);
			masterDetailsGrid.addComponent(unitField, 6, 0);

			if (settings.isBARCODE_ENABLED()) {
				masterDetailsGrid.addComponent(new SLabel(
						getPropertyName("barcode")), 7, 0);
				masterDetailsGrid.addComponent(barcodeField, 8, 0);
			}
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					9, 0);
			masterDetailsGrid.addComponent(date, 10, 0);
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
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");

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

			itemSelect = new SComboField(getPropertyName("item"), 250,
					itemDao.getAllItemsWithCodeAndBalance(getOfficeID()), "id",
					"name", true, "Select");
			SHorizontalLayout hlay = new SHorizontalLayout();

			hlay.addComponent(itemSelect);
			hlay.addComponent(popup);

			addItemButton = new SButton(null, getPropertyName("add_item"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			catalogNoField = new SComboField("Catalog No", 100,
					daoObj.getCatalogNo(getOfficeID()), "id", "comments");
			catalogNoField.setInputPrompt("-----Select------");

			catalogButton = new SButton("Load");

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(catalogNoField);
			addingGrid.addComponent(catalogButton);
			addingGrid.setComponentAlignment(catalogButton,
					Alignment.MIDDLE_LEFT);
			addingGrid.addComponent(hlay);

			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);

			addingGrid.addComponent(convertionQtyTextField);
			addingGrid.addComponent(convertedQtyTextField);

			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 1);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 2);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(9, 3);
			addingGrid.setColumnExpandRatio(10, 3);

			addingGrid.setWidth("700");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			table = new STable(null, 820, 200);
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
					getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class,
					null, getPropertyName("qty_basic_unit"), null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY });

			table.setSelectable(true);
			table.setNullSelectionAllowed(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			saveProduction = new SButton(getPropertyName("save"), 70);
			saveProduction.setStyleName("savebtnStyle");
			saveProduction.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			releaseProduction = new SButton(getPropertyName("Release"), 78);
			releaseProduction.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			releaseProduction.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveProduction);

			buttonsGrid.setColumnExpandRatio(0, 1);
			buttonsGrid.setColumnExpandRatio(1, 5);

			buttonsGrid.addComponent(mainButtonLayout, 1, 1);
			mainButtonLayout.setSpacing(true);
			buttonsGrid.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			tableDescLabel = new SLabel();
			form.addComponent(masterDetailsGrid);
			form.addComponent(tableDescLabel);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			hLayout.setId("Combo Creation");
			tab.addTab(hLayout, "Combo Creation");

			panel.setContent(tab);

			itemSelect.focus();

			popLay.addComponent(new SHorizontalLayout(new SLabel(null,
					getPropertyName("item_name"), 100), itemName));
			pop.setPrimaryStyleName("pop_style");

			releaseFormLayout = new SFormLayout();
			releaseFormLayout.setId("Release Stock");
			releaseFormLayout.setMargin(true);
			releaseComboField = new SComboField(getPropertyName("item"), 200);

			releaseQtyField = new STextField(getPropertyName("quantity"));
			releaseFormLayout.addComponent(releaseComboField);
			releaseFormLayout.addComponent(releaseQtyField);
			releaseFormLayout.addComponent(releaseProduction);
			loadReleaseComboItems((long) 0);

			tab.addTab(releaseFormLayout, "Release Stock");

			window = new SWindow();
			window.setWidth("300px");
			window.setHeight("160px");
			window.setCaption("Barcode Print");
			window.center();

			barcodeQtyField = new STextField(getPropertyName("no_barcodes"),
					100);
			barcodeQtyField.setValue("1");
			barcodeButton = new SButton(getPropertyName("print"));

			SFormLayout bardLay = new SFormLayout();
			bardLay.setSpacing(true);
			bardLay.setMargin(true);

			bardLay.addComponent(barcodeQtyField);
			bardLay.addComponent(barcodeButton);

			window.setContent(bardLay);

			tab.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					if (tab.getSelectedTab().getId().equals("Release Stock")) {
						setSize(400, 400);
						tab.setSize(380, 320);
						center();

						loadReleaseComboItems((long) 0);
					} else {
						setSize(910, 550);
						tab.setSize(900, 480);
						center();

						loadComboItems((long) 0);
					}
				}
			});

			newItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
					getUI().getCurrent().addWindow(newItemWindow);
					newItemWindow.setCaption("Add New Item");
				}
			});

			doneButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					pop.setPopupVisible(false);
					table.setValue(null);
				}
			});

			catalogButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					if (catalogNoField.getValue() != null
							&& !catalogNoField.getValue().equals("")) {

						try {

							table.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME,
									TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
									TBC_QTY_IN_BASIC_UNIT });

							PurchaseInventoryDetailsModel detMdl;
							PurchaseModel mdl;

							List list = daoObj.getPurchaseFromCatalogNo(
									catalogNoField
											.getItemCaption(catalogNoField
													.getValue()), getOfficeID());
							Iterator iterator = list.iterator();
							while (iterator.hasNext()) {

								mdl = (PurchaseModel) iterator.next();

								Iterator iter = mdl.getPurchase_details_list()
										.iterator();
								while (iter.hasNext()) {
									detMdl = (PurchaseInventoryDetailsModel) iter
											.next();

									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													detMdl.getItem().getId(),
													detMdl.getItem()
															.getItem_code(),
													detMdl.getItem().getName(),
													detMdl.getQunatity(),
													detMdl.getUnit().getId(),
													detMdl.getUnit()
															.getSymbol(),
													detMdl.getQty_in_basic_unit() },
											table.getItemIds().size() + 1);

								}
							}
							table.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
									TBC_QTY });

							itemSelect.setValue(null);
							quantityTextField.setValue("0.0");

							calculateTotals();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						setRequiredError(catalogNoField,
								getPropertyName("invalid_selection"), true);
					}
				}
			});

			saveProduction.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isValid()) {

							ItemComboModel objModel;

							Iterator it2;
							ProductionBean bean;
							Object parent;
							Set set;
							Iterator it1 = table.getItemIds().iterator();

							List detailsList = new ArrayList();
							ItemComboDetailsModel details = null;
							List mapList = null;
							Iterator mapIter;

							objModel = new ItemComboModel();

							objModel.setItem(new ItemModel(
									(Long) comboItemField.getValue()));
							objModel.setQuantity(toDouble(quantityField
									.getValue()));
							objModel.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objModel.setUnitPrice(toDouble(unitField.getValue()));

							mapIter = table.getItemIds().iterator();
							while (it1.hasNext()) {

								parent = it1.next();
								Item item = table.getItem(parent);

								details = new ItemComboDetailsModel();
								details.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								details.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								details.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								details.setQty_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
										.getValue());
								detailsList.add(details);
							}
							objModel.setItem_combo_details_list(detailsList);

							stockID = daoObj.save(objModel,
									barcodeField.getValue());

							loadComboItems((long) 0);

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							if (settings.isBARCODE_ENABLED()) {
								getUI().getCurrent().addWindow(window);
							}

							reloadStock();

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			barcodeButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					barcodeField.setComponentError(null);
					if (barcodeQtyField.getValue() != null
							&& !barcodeQtyField.getValue().equals("")) {
						try {
							if (toInt(barcodeQtyField.getValue()) <= 0) {
								setRequiredError(barcodeQtyField,
										"Enter Valid Number", true);
							} else {

								List<Object> reportList = new ArrayList<Object>();

								Report report = new Report(getLoginID());

								ItemReportBean bean = null;
								ItemStockModel stk = daoObj.getStock(stockID);

								for (int i = 0; i < toInt(barcodeQtyField
										.getValue().toString()); i++) {
									bean = new ItemReportBean();
									bean.setCode(stk.getBarcode());
									bean.setCurrency(stk.getItem().getOffice()
											.getCurrency().getSymbol());
									bean.setName(stk.getItem().getName());
									bean.setRate(comDao.getItemPrice(stk
											.getItem().getId(),
											toLong(unitField.getValue()), -1));
									reportList.add(bean);
								}

								report.setJrxmlFileName("PurchaseBarcodeWithPrice");
								report.setReportFileName("PurchaseBarcode");
								report.setReportTitle("");
								report.setIncludeHeader(false);
								report.setReportType(Report.PDF);
								report.createReport(reportList, null);

								report.print();

							}
						} catch (Exception e) {
							setRequiredError(barcodeQtyField,
									getPropertyName("enter_valid_number"), true);
						}
					} else {
						setRequiredError(barcodeQtyField,
								getPropertyName("enter_valid_number"), true);
					}
				}
			});

			comboItemField.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						removeAllErrors();
						table.removeAllItems();

						if (comboItemField.getValue() != null
								&& !comboItemField.getValue().equals("")) {

							ItemModel itemMdl = itemDao
									.getItem((Long) comboItemField.getValue());

							tableDescLabel
									.setValue("Items required to create 1 "
											+ itemMdl.getUnit().getSymbol()
											+ " of " + itemMdl.getName());

							ItemComboModel comboMdl = daoObj
									.getItemComboModel(toLong(comboItemField
											.getValue().toString()));
							if (comboMdl != null) {

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT, TBC_QTY_IN_BASIC_UNIT });

								int parent_id = 1, child_id = 1;
								Iterator it = comboMdl
										.getItem_combo_details_list()
										.iterator();
								ItemComboDetailsModel invObj;
								SalesOrderModel soMld;
								while (it.hasNext()) {
									invObj = (ItemComboDetailsModel) it.next();

									double conv_rat = comDao.getConvertionRate(
											invObj.getItem().getId(), invObj
													.getUnit().getId(), 0);

									table.addItem(
											new Object[] {
													parent_id,
													invObj.getItem().getId(),
													invObj.getItem()
															.getItem_code(),
													invObj.getItem().getName(),
													invObj.getQuantity(),
													invObj.getUnit().getId(),
													invObj.getUnit()
															.getSymbol(),
													invObj.getQty_in_basic_unit() },
											parent_id);

									parent_id++;

								}

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY });

								if (settings.isBARCODE_ENABLED()) {
									barcodeField.setValue(daoObj
											.getBarcodeFromStock(itemMdl
													.getId()));
								}

							}
						} else {
							table.removeAllItems();

							date.setValue(getWorkingDate());
							barcodeField.setValue("");

						}

						itemSelect.setValue(null);
						itemSelect.focus();
						quantityTextField.setValue("0.0");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			releaseProduction.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (isReleaseValid()) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.release(
														toLong(releaseComboField
																.getValue()
																.toString()),
														toDouble(releaseQtyField
																.getValue()));
												Notification
														.show(getPropertyName("Success"),
																Type.WARNING_MESSAGE);
												loadReleaseComboItems((long) 0);

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}

				}

				private boolean isReleaseValid() {
					boolean valid = true;

					try {
						if (toDouble(releaseQtyField.getValue().toString()) <= 0) {
							setRequiredError(releaseQtyField,
									getPropertyName("invalid_data"), true);
							valid = false;
							releaseQtyField.focus();
						} else
							releaseQtyField.setComponentError(null);
					} catch (Exception e) {
						setRequiredError(releaseQtyField,
								getPropertyName("invalid_data"), true);
						valid = false;
						releaseQtyField.focus();
					}

					if (releaseComboField.getValue() == null
							|| releaseComboField.getValue().equals("")) {
						setRequiredError(releaseComboField,
								getPropertyName("invalid_selection"), true);
						valid = false;
					} else
						releaseComboField.setComponentError(null);

					if (valid) {
						if (toDouble(releaseQtyField.getValue().toString()) > maxReleaseQuantity) {
							setRequiredError(releaseQtyField,
									getPropertyName("Not enough stock"), true);
							valid = false;
							releaseQtyField.focus();
						}
					}

					return valid;
				}
			});

			releaseComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					maxReleaseQuantity = 0;
					if (releaseComboField.getValue() != null
							&& !releaseComboField.getValue().equals("")) {
						try {
							maxReleaseQuantity = itemDao.getItem(
									(Long) releaseComboField.getValue())
									.getCurrent_balalnce();
							releaseQtyField.setValue(maxReleaseQuantity + "");
						} catch (Exception e) {
							e.printStackTrace();
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

						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());

						itemSelect.setValue(item.getItemProperty(TBC_ITEM_ID)
								.getValue());

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

						visibleAddupdateStockTransfer(false, true);

						itemSelect.focus();

					} else {
						itemSelect.setValue(null);
						quantityTextField.setValue("0.0");
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

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT, TBC_QTY_IN_BASIC_UNIT });

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
												table.getItemIds().size() + 1,
												stk.getId(),
												stk.getItem_code(),
												stk.getName(), qty,
												objUnit.getId(),
												objUnit.getSymbol(), qty_bsc },
										id);

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY });

								itemSelect.setValue(null);
								itemSelect.focus();
								quantityTextField.setValue("0.0");
							}

							calculateTotals();

							itemSelect.setValue(null);
							itemSelect.focus();
							quantityTextField.setValue("0.0");

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
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									stk.getName());
							item.getItemProperty(TBC_QTY).setValue(qty);
							item.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
									.setValue(
											toDouble(convertedQtyTextField
													.getValue()));

							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());

							table.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
									TBC_QTY });

							itemSelect.setValue(null);
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
							quantityTextField.focus();
							quantityTextField.selectAll();

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
					loadComboItems((long) 0);
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

			newItemWindow.addCloseListener(new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					reloadItemStocks();
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return panel;
	}

	public void calculateNetPrice() {

		try {

			convertedQtyTextField.setNewValue(asString(Double
					.parseDouble(quantityTextField.getValue())
					* Double.parseDouble(convertionQtyTextField.getValue())));

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

			if (itemSelect.getValue() == null
					|| itemSelect.getValue().equals("")) {
				setRequiredError(itemSelect,
						getPropertyName("invalid_selection"), true);
				itemSelect.focus();
				ret = false;
			} else
				setRequiredError(itemSelect, null, false);

			if (comboItemField.getValue() != null
					&& !comboItemField.getValue().equals("")
					&& itemSelect.getValue() != null
					&& !itemSelect.getValue().equals("")) {
				if (toLong(comboItemField.getValue().toString()) == toLong(itemSelect
						.getValue().toString())) {
					setRequiredError(itemSelect,
							getPropertyName("invalid_selection"), true);
					itemSelect.focus();
					ret = false;
				} else
					setRequiredError(itemSelect, null, false);
			}

			if (ret) {
				if (comboItemField.getValue() != null
						&& !comboItemField.getValue().equals("")) {
					if (daoObj.isItemStockExists((Long) comboItemField
							.getValue())) {
						setRequiredError(comboItemField,
								getPropertyName("unable_update_stock"), true);
						comboItemField.focus();
						ret = false;
					} else
						comboItemField.setComponentError(null);
				}
			}

		} catch (Exception e) {
		}

		return ret;

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

	public void loadComboItems(long id) {
		try {
			SCollectionContainer cont = SCollectionContainer.setList(
					itemDao.getAllManufacturingItems(getOfficeID()), "id");
			comboItemField.setContainerDataSource(cont);
			comboItemField.setItemCaptionPropertyId("name");
			comboItemField.setInputPrompt("----------Select-----------");

			if (id != 0)
				comboItemField.setValue(id);
			else {
				comboItemField.setValue(null);
				quantityField.setValue("0");
				unitField.setValue("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadReleaseComboItems(long id) {
		try {
			SCollectionContainer cont = SCollectionContainer.setList(
					daoObj.getAllComboItems(getOfficeID()), "id");
			releaseComboField.setContainerDataSource(cont);
			releaseComboField.setItemCaptionPropertyId("name");
			releaseComboField.setInputPrompt("----------Select-----------");

			if (id != 0)
				releaseComboField.setValue(id);
			else {
				releaseComboField.setValue(null);
				releaseQtyField.setValue("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadStock() {
		try {

			SCollectionContainer bic = SCollectionContainer.setList(
					itemDao.getAllItemsWithCodeAndBalance(getOfficeID()), "id");
			itemSelect.setContainerDataSource(bic);
			itemSelect.setItemCaptionPropertyId("name");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		barcodeField.setComponentError(null);

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

		if (comboItemField.getValue() == null
				|| comboItemField.getValue().equals("")) {
			setRequiredError(comboItemField,
					getPropertyName("invalid_selection"), true);
			comboItemField.focus();
			ret = false;
		} else
			setRequiredError(comboItemField, null, false);

		try {
			if (toDouble(quantityField.getValue().toString()) <= 0) {
				setRequiredError(quantityField,
						getPropertyName("invalid_data"), true);
				quantityField.focus();
				ret = false;
			} else
				setRequiredError(quantityField, null, false);

		} catch (Exception e) {
			setRequiredError(quantityField, getPropertyName("invalid_data"),
					true);
			quantityField.focus();
			ret = false;
		}

		try {
			if (toDouble(unitField.getValue().toString()) <= 0) {
				setRequiredError(unitField, getPropertyName("invalid_data"),
						true);
				unitField.focus();
				ret = false;
			} else
				setRequiredError(unitField, null, false);

		} catch (Exception e) {
			setRequiredError(unitField, getPropertyName("invalid_data"), true);
			unitField.focus();
			ret = false;
		}

		if (ret && barcodeField.getValue() != null
				&& barcodeField.getValue().toString().trim().length() > 0) {
			try {
				if (daoObj.isBarcodeExists((Long) comboItemField.getValue(),
						barcodeField.getValue())) {
					setRequiredError(barcodeField,
							"Barcode already assigned to another item", true);
					barcodeField.focus();
					ret = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

	protected void reloadItemStocks() {
		try {
			List list = itemDao.getAllManufacturingItems(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			comboItemField.setContainerDataSource(bic);
			comboItemField.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				comboItemField.setValue((Long) getHttpSession().getAttribute(
						"saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
