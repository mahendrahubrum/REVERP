package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.haijian.ExcelExporter;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ManualTradingEntryDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManualTradingDetailsModel;
import com.inventory.config.stock.model.ManualTradingMasterModel;
import com.inventory.config.unit.dao.UnitDao;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 4, 2013
 */
public class ManualTradingEntryUI extends SparkLogic {

	private static final long serialVersionUID = 6839565802492683458L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_OPEN_QTY = "Opening Qty";
	static String TBC_OPEN_UNIT_ID = "Open Unit ID";
	static String TBC_OPEN_UNIT = "Open Unit";

	static String TBC_P_QTY = "Purchase Qty";
	static String TBC_S_QTY = "Sales Qty";
	static String TBC_P_UNIT_ID = "P Unit";
	static String TBC_P_UNIT = "Unit ";
	static String TBC_S_UNIT_ID = "S Unit ID";
	static String TBC_S_UNIT = "Unit";
	static String TBC_WASTE = "Waste";
	static String TBC_WASTE_UNIT_ID = "Waste Unit ID";
	static String TBC_WASTE_UNIT = "Waste Unit";
	static String TBC_BALANCE_QTY = "Balance Qty";

	ManualTradingEntryDao daoObj = new ManualTradingEntryDao();

	SComboField usersCombo;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;

	SGridLayout extraAddingGrid;

	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField openingQtyTextField;

	STextField totalPurchaseTextField;
	STextField totalSalesTextField;
	STextField clientTextField;

	STextField wasteTextField;

	SNativeSelect trnTypSelect;

	SNativeSelect openingUnitSelect;
	SNativeSelect purchUnitSelect;
	SNativeSelect saleUnitSelect;
	SNativeSelect clientUnitSelect;
	SNativeSelect wasteUnitSelect;

	SComboField clientSelect;

	SComboField officeCombo;

	SButton addItemButton;
	SButton saveStockTransfer;

	SButton addExtraItemButton;

	ItemDao itemDao;

	SDateField date;
	SComboField itemSelect;

	// STextArea comment;

	String old_ref_id = "";

	String[] allFields = new String[] { TBC_SN, TBC_ITEM_ID, TBC_ITEM_NAME,
			TBC_OPEN_QTY, TBC_OPEN_UNIT_ID, TBC_OPEN_UNIT, TBC_P_QTY,
			TBC_S_QTY, TBC_P_UNIT_ID, TBC_P_UNIT, TBC_S_UNIT_ID, TBC_S_UNIT,
			TBC_WASTE, TBC_WASTE_UNIT_ID, TBC_WASTE_UNIT, TBC_BALANCE_QTY };

	ArrayList<String> visibleColumns = new ArrayList<String>(Arrays.asList(
			TBC_SN, TBC_ITEM_NAME, TBC_OPEN_QTY, TBC_OPEN_UNIT, TBC_P_QTY,
			TBC_P_UNIT, TBC_S_QTY, TBC_S_UNIT, TBC_WASTE, TBC_WASTE_UNIT,
			TBC_BALANCE_QTY));

	ArrayList<String> headerColumns = new ArrayList<String>(Arrays.asList(
			TBC_SN, TBC_ITEM_NAME, TBC_OPEN_QTY, TBC_OPEN_UNIT, TBC_P_QTY,
			TBC_P_UNIT, TBC_S_QTY, TBC_S_UNIT, TBC_WASTE, TBC_WASTE_UNIT,
			TBC_BALANCE_QTY));

	SRadioButton viewType;

	Button excelExportButton = new Button(getPropertyName("export_to_excel"));

	ExcelExporter excelExporter;
	SHorizontalLayout mainButtonLayout;

	@SuppressWarnings("deprecation")
	public ManualTradingEntryUI() {

		setId("Transfer");
		setSize(1300, 530);

		daoObj = new ManualTradingEntryDao();
		itemDao = new ItemDao();

		List lst = new ArrayList(Arrays.asList(new KeyValue(1, "Normal"),
				new KeyValue(2, "Condenced"), new KeyValue(3, "Detailed")));

		viewType = new SRadioButton(null, 250, lst, "intKey", "value");
		viewType.setValue((int) 1);

		clientSelect = new SComboField(getPropertyName("customer"), 130);

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		// addingGrid.setSizeFull();
		addingGrid.setColumns(14);
		addingGrid.setRows(2);

		extraAddingGrid = new SGridLayout();
		// extraAddingGrid.setSizeFull();
		extraAddingGrid.setColumns(14);
		extraAddingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(14);
		masterDetailsGrid.setRows(2);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);

		form.setSizeFull();

		try {

			totalSalesTextField = new STextField(getPropertyName("sales_qty"),
					60);
			totalPurchaseTextField = new STextField(
					getPropertyName("purchase_qty"), 60);
			clientTextField = new STextField(getPropertyName("qty"), 60);
			wasteTextField = new STextField(getPropertyName("waste"), 60);

			openingQtyTextField = new STextField(
					getPropertyName("opening_qty"), 60);
			openingQtyTextField.setStyleName("textfield_align_right");

			totalSalesTextField.setStyleName("textfield_align_right");
			totalPurchaseTextField.setStyleName("textfield_align_right");
			wasteTextField.setStyleName("textfield_align_right");
			clientTextField.setStyleName("textfield_align_right");

			trnTypSelect = new SNativeSelect(getPropertyName("type"), 80,
					SConstants.clientsList, "intKey", "value");

			List unitList = new UnitDao()
					.getAllActiveUnits(getOrganizationID());

			openingUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitList, "id", "symbol");
			purchUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitList, "id", "symbol");
			saleUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitList, "id", "symbol");
			clientUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitList, "id", "symbol");
			wasteUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitList, "id", "symbol");

			List list = new UserManagementDao()
					.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
							getOfficeID(), getOrganizationID());
			usersCombo = new SComboField(null, 125, list, "id", "first_name",
					false, getPropertyName("select"));

			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date());
			date.setImmediate(true);

			officeCombo = new SComboField(null, 125,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name", false, getPropertyName("select"));

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					1, 0);
			masterDetailsGrid.addComponent(date, 2, 0);

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("office")), 4, 0);
			masterDetailsGrid.addComponent(officeCombo, 5, 0);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("employee")), 6, 0);
			masterDetailsGrid.addComponent(usersCombo, 8, 0);

			masterDetailsGrid.addComponent(viewType, 2, 1);
			viewType.setStyleName("radio_horizontal");

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

			itemSelect = new SComboField(getPropertyName("item"), 150, null,
					"id", "name", true, getPropertyName("select"));

			addItemButton = new SButton(null, getPropertyName("add_item"));
			addItemButton.setStyleName("addItemBtnStyle");

			addExtraItemButton = new SButton(null,
					getPropertyName("add_client_item"));
			addExtraItemButton.setStyleName("addItemBtnStyle");

			// SFormLayout buttonLay = new SFormLayout();
			// buttonLay.addComponent(addItemButton);

			SFormLayout buttonLay1 = new SFormLayout();
			buttonLay1.addComponent(addExtraItemButton);

			addingGrid.addComponent(itemSelect);

			addingGrid.addComponent(openingQtyTextField);
			addingGrid.addComponent(openingUnitSelect);

			addingGrid.addComponent(totalPurchaseTextField);
			addingGrid.addComponent(purchUnitSelect);
			addingGrid.addComponent(totalSalesTextField);
			addingGrid.addComponent(saleUnitSelect);
			addingGrid.addComponent(wasteTextField);
			addingGrid.addComponent(wasteUnitSelect);

			addingGrid.addComponent(addItemButton);
			addingGrid.setComponentAlignment(addItemButton,
					Alignment.MIDDLE_LEFT);

			addingGrid.setHeight("66");

			extraAddingGrid.addComponent(trnTypSelect);
			extraAddingGrid.addComponent(clientSelect);
			extraAddingGrid.addComponent(clientTextField);
			extraAddingGrid.addComponent(clientUnitSelect);
			extraAddingGrid.addComponent(buttonLay1);

			extraAddingGrid.setComponentAlignment(buttonLay1,
					Alignment.MIDDLE_LEFT);

			extraAddingGrid.setWidth("410");
			extraAddingGrid.setHeight("66");
			extraAddingGrid.setSpacing(true);
			extraAddingGrid.setStyleName("po_border");

			addingGrid.setColumnExpandRatio(0, 2);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			addingGrid.setColumnExpandRatio(7, 3);
			addingGrid.setColumnExpandRatio(8, 3);

			addingGrid.setWidth("840");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);

			table.addContainerProperty(TBC_OPEN_QTY, Double.class, null,
					getPropertyName("opening_qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_OPEN_UNIT_ID, Long.class, null,
					TBC_OPEN_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_OPEN_UNIT, String.class, null,
					getPropertyName("open_unit"), null, Align.CENTER);

			table.addContainerProperty(TBC_P_QTY, Double.class, null,
					getPropertyName("purchase_qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_S_QTY, Double.class, null,
					getPropertyName("sales_qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_P_UNIT_ID, Long.class, null,
					TBC_P_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_S_UNIT_ID, Long.class, null,
					TBC_S_UNIT_ID, null, Align.RIGHT);
			table.addContainerProperty(TBC_S_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_P_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_WASTE, Double.class, null,
					getPropertyName("waste"), null, Align.CENTER);
			table.addContainerProperty(TBC_WASTE_UNIT_ID, Long.class, null,
					TBC_WASTE_UNIT_ID, null, Align.RIGHT);
			table.addContainerProperty(TBC_WASTE_UNIT, String.class, null,
					getPropertyName("waste_unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_BALANCE_QTY, Double.class, null,
					getPropertyName("balance_qty"), null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_P_QTY, 1);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 2);
			table.setColumnExpandRatio(TBC_S_QTY, 1);
			table.setColumnExpandRatio(TBC_P_UNIT, (float) 0.5);
			table.setColumnExpandRatio(TBC_S_UNIT, (float) 0.5);
			table.setColumnExpandRatio(TBC_WASTE, 1);
			table.setColumnExpandRatio(TBC_WASTE_UNIT, (float) 0.5);
			table.setColumnExpandRatio(TBC_BALANCE_QTY, 1);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_NAME,
					TBC_OPEN_QTY, TBC_OPEN_UNIT, TBC_P_QTY, TBC_P_UNIT,
					TBC_S_QTY, TBC_S_UNIT, TBC_WASTE, TBC_BALANCE_QTY });

			table.setSizeFull();
			table.setSelectable(true);

			// table.setFooterVisible(true);
			// table.setColumnFooter(TBC_ITEM_NAME, "Total :");
			// table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("1250");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			// comment = new STextArea(null, 250, 40);

			saveStockTransfer = new SButton(getPropertyName("Save"), 70);
			saveStockTransfer.setStyleName("savebtnStyle");
			saveStockTransfer.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			excelExporter = new ExcelExporter(table);
			// pdfExporter = new PdfExporter(table);
			// pdfExporter.setCaption("Export to PDF");
			// pdfExporter.setWithBorder(false);

			mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveStockTransfer);
			mainButtonLayout.addComponent(excelExporter);
			// mainButtonLayout.addComponent(pdfExporter);

			// buttonsGrid.addComponent(new SLabel("Comment ;"), 0, 0);
			// buttonsGrid.addComponent(comment, 1, 0);

			buttonsGrid.setColumnExpandRatio(0, 1);
			buttonsGrid.setColumnExpandRatio(1, 5);

			buttonsGrid.addComponent(mainButtonLayout, 1, 1);
			mainButtonLayout.setSpacing(true);
			buttonsGrid.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);

			SHorizontalLayout horiz = new SHorizontalLayout();
			horiz.addComponent(addingGrid);
			horiz.addComponent(extraAddingGrid);

			form.addComponent(horiz);
			form.addComponent(buttonsGrid);

			form.setWidth("1100");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			itemSelect.focus();

			viewType.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						if ((Integer) viewType.getValue() == 2) {
							table.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_NAME, TBC_OPEN_QTY, TBC_OPEN_UNIT,
									TBC_P_QTY, TBC_P_UNIT, TBC_S_QTY,
									TBC_S_UNIT, TBC_WASTE, TBC_WASTE_UNIT,
									TBC_BALANCE_QTY });
						} else if ((Integer) viewType.getValue() == 3) {

							List<String> test = new ArrayList();
							test.addAll(visibleColumns);

							test.removeAll(new ArrayList<String>(Arrays.asList(
									TBC_OPEN_QTY, TBC_OPEN_UNIT, TBC_P_QTY,
									TBC_P_UNIT, TBC_S_QTY, TBC_S_UNIT,
									TBC_WASTE, TBC_WASTE_UNIT, TBC_BALANCE_QTY)));
							String[] visibles = (String[]) test
									.toArray(new String[test.size()]);
							table.setVisibleColumns(visibles);

							List<String> test1 = new ArrayList();
							test1.addAll(headerColumns);

							test1.removeAll(new ArrayList<String>(Arrays
									.asList(TBC_OPEN_QTY, TBC_OPEN_UNIT,
											TBC_P_QTY, TBC_P_UNIT, TBC_S_QTY,
											TBC_S_UNIT, TBC_WASTE,
											TBC_WASTE_UNIT, TBC_BALANCE_QTY)));

							String[] headers = (String[]) test1
									.toArray(new String[test1.size()]);
							table.setColumnHeaders(headers);

						} else {

							String[] visibles = (String[]) visibleColumns
									.toArray(new String[visibleColumns.size()]);

							table.setVisibleColumns(visibles);

							String[] headers = (String[]) headerColumns
									.toArray(new String[headerColumns.size()]);

							table.setColumnHeaders(headers);

						}

						mainButtonLayout.removeComponent(excelExporter);
						excelExporter = new ExcelExporter(table);
						mainButtonLayout.addComponent(excelExporter);
						excelExporter.setCaption("Export to Excel");

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			date.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						List list = daoObj.getDetails((Long) officeCombo
								.getValue(), CommonUtil
								.getSQLDateFromUtilDate(date.getValue()));

						table.removeAllItems();

						removeContainerProperties(visibleColumns);

						visibleColumns = new ArrayList<String>(Arrays.asList(
								TBC_SN, TBC_ITEM_NAME, TBC_OPEN_QTY,
								TBC_OPEN_UNIT, TBC_P_QTY, TBC_P_UNIT,
								TBC_S_QTY, TBC_S_UNIT, TBC_WASTE,
								TBC_WASTE_UNIT, TBC_BALANCE_QTY));

						headerColumns = new ArrayList<String>(Arrays.asList(
								"#", getPropertyName("item_name"),
								getPropertyName("opening_qty"),
								getPropertyName("open_unit"),
								getPropertyName("purchase_qty"),
								getPropertyName("unit"),
								getPropertyName("sales_qty"),
								getPropertyName("unit"),
								getPropertyName("waste"),
								getPropertyName("waste_unit"),
								getPropertyName("balance_qty")));

						ManualTradingMasterModel objModel;
						ManualTradingDetailsModel childObj;
						Item item;
						Item itm;
						String ref_id;
						String value = "";
						STextField tf;
						String client_name = "";
						int id = 0;
						Iterator mastIter = list.iterator();
						while (mastIter.hasNext()) {
							id++;
							objModel = (ManualTradingMasterModel) mastIter
									.next();

							table.setVisibleColumns(allFields);

							table.addItem(
									new Object[] {
											id,
											objModel.getItem().getId(),
											objModel.getItem().getName()
													+ " [ "
													+ objModel.getItem()
															.getItem_code()
													+ " ]",

											objModel.getOpening_qty(),
											objModel.getOpening_unit(),
											daoObj.getUnitNameFromID(objModel
													.getOpening_unit()),

											objModel.getTotal_purchase(),
											objModel.getTotal_sale(),
											objModel.getPurch_unit(),
											daoObj.getUnitNameFromID(objModel
													.getPurch_unit()),
											objModel.getSale_unit(),
											daoObj.getUnitNameFromID(objModel
													.getSale_unit()),
											objModel.getWaste_qty(),
											objModel.getWaste_unit(),
											daoObj.getUnitNameFromID(objModel
													.getWaste_unit()),
											objModel.getBalance() }, id);

							Iterator childIter = objModel.getDetailsList()
									.iterator();
							while (childIter.hasNext()) {
								childObj = (ManualTradingDetailsModel) childIter
										.next();

								if (childObj.getType() == 1) {
									client_name = daoObj
											.getCustomerNameFromID(childObj
													.getClient_id());
									client_name = "[C] " + client_name;
								} else {
									client_name = daoObj
											.getSupplierNameFromID(childObj
													.getClient_id());
									client_name = "[S] " + client_name;
								}

								item = table.getItem(id);

								ref_id = childObj.getType() + "_"
										+ childObj.getClient_id();
								value = "";
								if (childObj.getQuantity() != 0)
									value = childObj.getQuantity()
											+ " "
											+ daoObj.getUnitNameFromID(childObj
													.getUnit_id());

								if (!isAlreadyAddedForUpdate(
										"" + childObj.getType(),
										childObj.getClient_id() + "")) {

									table.addContainerProperty(ref_id,
											STextField.class, null,
											client_name, null, Align.RIGHT);

									table.setColumnExpandRatio(ref_id, 2);

									itm = table.getItem(id);

									tf = new STextField(null, 100, value);
									tf.setStyleName("textfield_align_right");
									tf.setId("" + childObj.getUnit_id());
									tf.setReadOnly(true);
									itm.getItemProperty(ref_id).setValue(tf);

									visibleColumns.add(ref_id);
									headerColumns.add(client_name);

								} else {

									tf = new STextField(null, 100, value);
									tf.setStyleName("textfield_align_right");
									tf.setId("" + childObj.getUnit_id());
									tf.setReadOnly(true);
									item.getItemProperty(ref_id).setValue(tf);

								}
							}
						}

						// String[] visibles=(String[])
						// visibleColumns.toArray(new
						// String[visibleColumns.size()]);

						table.setVisibleColumns((String[]) visibleColumns
								.toArray(new String[visibleColumns.size()]));

						// String[] headers=(String[]) headerColumns.toArray(new
						// String[headerColumns.size()]);

						table.setColumnHeaders((String[]) headerColumns
								.toArray(new String[headerColumns.size()]));

						mainButtonLayout.removeComponent(excelExporter);
						// mainButtonLayout.removeComponent(pdfExporter);

						excelExporter = new ExcelExporter(table);
						mainButtonLayout.addComponent(excelExporter);
						excelExporter
								.setCaption(getPropertyName("export_to_excel"));

						// pdfExporter = new PdfExporter(table);
						// pdfExporter.setCaption("Export to PDF");
						// pdfExporter.setWithBorder(false);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

			officeCombo.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						List list = daoObj.getDetails((Long) officeCombo
								.getValue(), CommonUtil
								.getSQLDateFromUtilDate(date.getValue()));

						table.removeAllItems();

						removeContainerProperties(visibleColumns);

						visibleColumns = new ArrayList<String>(Arrays.asList(
								TBC_SN, TBC_ITEM_NAME, TBC_OPEN_QTY,
								TBC_OPEN_UNIT, TBC_P_QTY, TBC_P_UNIT,
								TBC_S_QTY, TBC_S_UNIT, TBC_WASTE,
								TBC_WASTE_UNIT, TBC_BALANCE_QTY));

						headerColumns = new ArrayList<String>(Arrays.asList(
								"#", getPropertyName("item_name"),
								getPropertyName("opening_qty"),
								getPropertyName("open_unit"),
								getPropertyName("purchase_qty"),
								getPropertyName("unit"),
								getPropertyName("sales_qty"),
								getPropertyName("unit"),
								getPropertyName("waste"),
								getPropertyName("waste_unit"),
								getPropertyName("balance_qty")));

						ManualTradingMasterModel objModel;
						ManualTradingDetailsModel childObj;
						int id = 0;
						String client_name = "";
						Iterator childIter;
						String value = "";
						String ref_id;
						Item itm;
						Item item;
						STextField tf;
						Iterator mastIter = list.iterator();
						while (mastIter.hasNext()) {
							id++;
							objModel = (ManualTradingMasterModel) mastIter
									.next();

							table.setVisibleColumns(allFields);

							table.addItem(
									new Object[] {
											id,
											objModel.getItem().getId(),
											objModel.getItem().getName()
													+ " [ "
													+ objModel.getItem()
															.getItem_code()
													+ " ]",

											objModel.getOpening_qty(),
											objModel.getOpening_unit(),
											daoObj.getUnitNameFromID(objModel
													.getOpening_unit()),

											objModel.getTotal_purchase(),
											objModel.getTotal_sale(),
											objModel.getPurch_unit(),
											daoObj.getUnitNameFromID(objModel
													.getPurch_unit()),
											objModel.getSale_unit(),
											daoObj.getUnitNameFromID(objModel
													.getSale_unit()),
											objModel.getWaste_qty(),
											objModel.getWaste_unit(),
											daoObj.getUnitNameFromID(objModel
													.getWaste_unit()),
											objModel.getBalance() }, id);

							childIter = objModel.getDetailsList().iterator();
							while (childIter.hasNext()) {
								childObj = (ManualTradingDetailsModel) childIter
										.next();

								if (childObj.getType() == 1) {
									client_name = daoObj
											.getCustomerNameFromID(childObj
													.getClient_id());
									client_name = "[C] " + client_name;
								} else {
									client_name = daoObj
											.getSupplierNameFromID(childObj
													.getClient_id());
									client_name = "[S] " + client_name;
								}

								item = table.getItem(id);

								ref_id = childObj.getType() + "_"
										+ childObj.getClient_id();

								value = "";
								if (childObj.getQuantity() != 0)
									value = childObj.getQuantity()
											+ " "
											+ daoObj.getUnitNameFromID(childObj
													.getUnit_id());

								if (!isAlreadyAddedForUpdate(
										"" + childObj.getType(),
										childObj.getClient_id() + "")) {

									table.addContainerProperty(ref_id,
											STextField.class, null,
											client_name, null, Align.RIGHT);

									table.setColumnExpandRatio(ref_id, 2);

									itm = table.getItem(id);

									tf = new STextField(null, 100, value);
									tf.setStyleName("textfield_align_right");
									tf.setId("" + childObj.getUnit_id());
									tf.setReadOnly(true);
									itm.getItemProperty(ref_id).setValue(tf);

									visibleColumns.add(ref_id);
									headerColumns.add(client_name);

								} else {

									tf = new STextField(null, 100, value);
									tf.setStyleName("textfield_align_right");
									tf.setId("" + childObj.getUnit_id());
									tf.setReadOnly(true);
									item.getItemProperty(ref_id).setValue(tf);

								}

							}

						}

						// String[] visibles=(String[])
						// visibleColumns.toArray(new
						// String[visibleColumns.size()]);

						table.setVisibleColumns((String[]) visibleColumns
								.toArray(new String[visibleColumns.size()]));

						// String[] headers=(String[]) headerColumns.toArray(new
						// String[headerColumns.size()]);

						table.setColumnHeaders((String[]) headerColumns
								.toArray(new String[headerColumns.size()]));

						mainButtonLayout.removeComponent(excelExporter);
						// mainButtonLayout.removeComponent(pdfExporter);

						excelExporter = new ExcelExporter(table);
						mainButtonLayout.addComponent(excelExporter);
						excelExporter
								.setCaption(getPropertyName("export_to_excel"));

						// pdfExporter = new PdfExporter(table);
						// pdfExporter.setCaption("Export to PDF");
						// pdfExporter.setWithBorder(false);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

			saveStockTransfer.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isValid()) {

							List<ManualTradingMasterModel> itemsList = new ArrayList<ManualTradingMasterModel>();

							double conv_rat = 0;

							ManualTradingMasterModel invObj;
							ManualTradingDetailsModel objChild;
							List<ManualTradingDetailsModel> detailsList;
							String[] splits;
							double qtty = 0;
							String id;
							Item item;
							STextField txtf;
							String val;
							String[] vals;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new ManualTradingMasterModel();

								item = table.getItem(it.next());
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));

								invObj.setDate(CommonUtil
										.getSQLDateFromUtilDate(date.getValue()));
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setLogin_id((Long) usersCombo.getValue());

								invObj.setOpening_qty((Double) item
										.getItemProperty(TBC_OPEN_QTY)
										.getValue());
								invObj.setOpening_unit((Long) item
										.getItemProperty(TBC_OPEN_UNIT_ID)
										.getValue());

								invObj.setPurch_unit((Long) item
										.getItemProperty(TBC_P_UNIT_ID)
										.getValue());
								invObj.setSale_unit((Long) item
										.getItemProperty(TBC_S_UNIT_ID)
										.getValue());
								invObj.setTotal_purchase((Double) item
										.getItemProperty(TBC_P_QTY).getValue());
								invObj.setTotal_sale((Double) item
										.getItemProperty(TBC_S_QTY).getValue());
								invObj.setWaste_qty((Double) item
										.getItemProperty(TBC_WASTE).getValue());
								invObj.setWaste_unit((Long) item
										.getItemProperty(TBC_WASTE_UNIT_ID)
										.getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_BALANCE_QTY)
										.getValue());
								invObj.setOffice_id((Long) officeCombo
										.getValue());

								detailsList = new ArrayList<ManualTradingDetailsModel>();

								for (int i = 11; i < visibleColumns.size(); i++) {

									objChild = new ManualTradingDetailsModel();

									id = visibleColumns.get(i);
									splits = id.split("_");
									objChild.setType(toInt(splits[0]));
									objChild.setClient_id(toLong(splits[1]));

									qtty = 0;

									if (item.getItemProperty(id).getValue() != null) {
										txtf = (STextField) item
												.getItemProperty(id).getValue();
										val = txtf.getValue();
										if (val != null && !val.equals("")) {
											vals = val.split(" ");
											if (vals.length > 1) {
												qtty = toDouble(vals[0]);
											}
										}
										objChild.setUnit_id(toLong(txtf.getId()));
									}

									objChild.setQuantity(qtty);

									detailsList.add(objChild);
								}

								invObj.setDetailsList(detailsList);

								itemsList.add(invObj);
							}

							daoObj.save(itemsList, (Long) officeCombo
									.getValue(), CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));

							Notification.show(
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							Date obj = date.getValue();
							date.setValue(null);
							date.setValue(obj);

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

				}
			});

			addExtraItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (table.getValue() != null) {
							if (isSubValid()) {

								if (!isAlreadyAdded()) {

									Item item = table.getItem(table.getValue());

									String ref_id = trnTypSelect.getValue()
											+ "_" + clientSelect.getValue();

									String value = clientTextField.getValue()
											+ " "
											+ clientUnitSelect
													.getItemCaption(clientUnitSelect
															.getValue());

									String tp = "[S] ";
									if ((Integer) trnTypSelect.getValue() == 1) {
										tp = "[C] ";
									}

									table.addContainerProperty(
											ref_id,
											STextField.class,
											null,
											tp
													+ clientSelect
															.getItemCaption(clientSelect
																	.getValue()),
											null, Align.RIGHT);

									table.setColumnExpandRatio(ref_id, 2);
									STextField tf = new STextField(null, 100,
											value);
									tf.setStyleName("textfield_align_right");
									tf.setId("" + clientUnitSelect.getValue());
									tf.setReadOnly(true);
									item.getItemProperty(ref_id).setValue(tf);

									visibleColumns.add(ref_id);

									headerColumns.add(tp
											+ clientSelect
													.getItemCaption(clientSelect
															.getValue()));

								} else {

									Item item = table.getItem(table.getValue());
									String value = clientTextField.getValue()
											+ " "
											+ clientUnitSelect
													.getItemCaption(clientUnitSelect
															.getValue());

									STextField tf = new STextField(null, 100,
											value);
									tf.setStyleName("textfield_align_right");
									tf.setId("" + clientUnitSelect.getValue());
									tf.setReadOnly(true);
									item.getItemProperty(old_ref_id).setValue(
											tf);

								}

								// String[] visibles=(String[])
								// visibleColumns.toArray(new
								// String[visibleColumns.size()]);

								table.setVisibleColumns((String[]) visibleColumns
										.toArray(new String[visibleColumns
												.size()]));

								clientSelect.setValue(null);
								clientTextField.setValue("");
								clientSelect.focus();

							}

						} else {
							Notification.show(
									getPropertyName("invalid_selection"),
									getPropertyName("select_item_table"),
									Type.TRAY_NOTIFICATION);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			trnTypSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					List lst = null;
					try {

						if (((Integer) trnTypSelect.getValue()) == 1) {
							lst = new CustomerDao()
									.getAllCustomersNames((Long) officeCombo
											.getValue());
							clientSelect
									.setCaption(getPropertyName("customer"));
						} else {
							lst = new SupplierDao()
									.getAllSuppliersNames((Long) officeCombo
											.getValue());
							clientSelect.setCaption("Supplier");
						}

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}

					SCollectionContainer bic = SCollectionContainer.setList(
							lst, "id");
					clientSelect.setContainerDataSource(bic);
					clientSelect.setItemCaptionPropertyId("name");

				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						int id = 0;
						if (table.getComponentError() != null)
							setRequiredError(table, null, false);

						if (isAddingValid()) {

							boolean already_added_item = false, eror_hapnd = false;

							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());

							double qty = 0, totalameStockSamOfcQty = 0, totalSameStockQty = 0;

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

									already_added_item = true;

									item.getItemProperty(TBC_OPEN_QTY)
											.setValue(
													toDouble(openingQtyTextField
															.getValue()));
									item.getItemProperty(TBC_OPEN_UNIT_ID)
											.setValue(
													(Long) openingUnitSelect
															.getValue());
									item.getItemProperty(TBC_OPEN_UNIT)
											.setValue(
													openingUnitSelect
															.getItemCaption(openingUnitSelect
																	.getValue()));

									item.getItemProperty(TBC_P_UNIT_ID)
											.setValue(
													(Long) purchUnitSelect
															.getValue());
									item.getItemProperty(TBC_S_UNIT_ID)
											.setValue(
													(Long) saleUnitSelect
															.getValue());
									item.getItemProperty(TBC_P_QTY).setValue(
											toDouble(totalPurchaseTextField
													.getValue()));
									item.getItemProperty(TBC_S_QTY).setValue(
											toDouble(totalSalesTextField
													.getValue()));
									item.getItemProperty(TBC_P_UNIT)
											.setValue(
													purchUnitSelect
															.getItemCaption(purchUnitSelect
																	.getValue()));
									item.getItemProperty(TBC_S_UNIT)
											.setValue(
													saleUnitSelect
															.getItemCaption(saleUnitSelect
																	.getValue()));
									item.getItemProperty(TBC_WASTE_UNIT_ID)
											.setValue(
													(Long) wasteUnitSelect
															.getValue());
									item.getItemProperty(TBC_WASTE_UNIT)
											.setValue(
													wasteUnitSelect
															.getItemCaption(wasteUnitSelect
																	.getValue()));
									item.getItemProperty(TBC_WASTE)
											.setValue(
													toDouble(wasteTextField
															.getValue()));

									item.getItemProperty(TBC_BALANCE_QTY)
											.setValue(
													toDouble(openingQtyTextField
															.getValue())
															+ toDouble(totalPurchaseTextField
																	.getValue())
															- toDouble(totalSalesTextField
																	.getValue())
															- toDouble(wasteTextField
																	.getValue()));

									Notification.show(
											getPropertyName("already_added"),
											getPropertyName("row_is_updated"),
											Type.TRAY_NOTIFICATION);

								}
								id = (Integer) obj;
							}

							id++;
							if (!already_added_item) {
								System.out.println(allFields.length);
								table.setVisibleColumns(allFields);

								double bal = toDouble(openingQtyTextField
										.getValue())
										+ toDouble(totalPurchaseTextField
												.getValue())
										- toDouble(totalSalesTextField
												.getValue())
										- toDouble(wasteTextField.getValue());

								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,

												(Long) itemSelect.getValue(),
												itemSelect
														.getItemCaption(itemSelect
																.getValue()),

												toDouble(openingQtyTextField
														.getValue()),
												(Long) openingUnitSelect
														.getValue(),
												openingUnitSelect
														.getItemCaption(openingUnitSelect
																.getValue()),

												toDouble(totalPurchaseTextField
														.getValue()),
												toDouble(totalSalesTextField
														.getValue()),
												(Long) purchUnitSelect
														.getValue(),
												purchUnitSelect
														.getItemCaption(purchUnitSelect
																.getValue()),
												(Long) saleUnitSelect
														.getValue(),
												saleUnitSelect
														.getItemCaption(saleUnitSelect
																.getValue()),
												toDouble(wasteTextField
														.getValue()),
												(Long) wasteUnitSelect
														.getValue(),
												wasteUnitSelect
														.getItemCaption(wasteUnitSelect
																.getValue()),
												bal }, id);

								table.setValue(id);

								itemSelect.setValue(null);
								totalPurchaseTextField.setValue("");
								totalSalesTextField.setValue("");
								wasteTextField.setValue("");
								itemSelect.focus();
							}

						}

						// String[] visibles=(String[])
						// visibleColumns.toArray(new
						// String[visibleColumns.size()]);

						table.setVisibleColumns((String[]) visibleColumns
								.toArray(new String[visibleColumns.size()]));

						// String[] headers=(String[]) headerColumns.toArray(new
						// String[headerColumns.size()]);

						table.setColumnHeaders((String[]) headerColumns
								.toArray(new String[headerColumns.size()]));

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			officeCombo.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					List lst = null;
					try {
						if (officeCombo.getValue() != null)
							lst = new ItemDao()
									.getAllItemsWithCode((Long) officeCombo
											.getValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SCollectionContainer bic = SCollectionContainer.setList(
							lst, "id");
					itemSelect.setContainerDataSource(bic);
					itemSelect.setItemCaptionPropertyId("name");

				}
			});

			itemSelect.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						List stkList = null;
						if (itemSelect.getValue() != null) {
							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());
							purchUnitSelect.setValue(stk.getUnit().getId());

							openingUnitSelect.setValue(stk.getUnit().getId());
							saleUnitSelect.setValue(stk.getUnit().getId());
							clientUnitSelect.setValue(stk.getUnit().getId());
							wasteUnitSelect.setValue(stk.getUnit().getId());

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					openingQtyTextField.focus();
					openingQtyTextField.selectAll();

				}
			});

			table.addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					addItemButton.click();
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

			usersCombo.setValue(getLoginID());

			officeCombo.setValue(getOfficeID());
			trnTypSelect.setValue((int) 1);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0;

			/*
			 * Iterator it = table.getItemIds().iterator(); while (it.hasNext())
			 * { Item item = table.getItem(it.next());
			 * 
			 * qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue(); }
			 */

			// table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (wasteUnitSelect.getValue() == null
					|| wasteUnitSelect.getValue().equals("")) {
				setRequiredError(wasteUnitSelect,
						getPropertyName("invalid_selection"), true);
				wasteUnitSelect.focus();
				ret = false;
			} else
				setRequiredError(wasteUnitSelect, null, false);

			if (wasteTextField.getValue() == null
					|| wasteTextField.getValue().equals("")) {
				setRequiredError(wasteTextField,
						getPropertyName("invalid_data"), true);
				wasteTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(wasteTextField.getValue()) < 0) {
						setRequiredError(wasteTextField,
								getPropertyName("invalid_data"), true);
						wasteTextField.focus();
						ret = false;
					} else
						setRequiredError(wasteTextField, null, false);
				} catch (Exception e) {
					setRequiredError(wasteTextField,
							getPropertyName("invalid_data"), true);
					wasteTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (saleUnitSelect.getValue() == null
					|| saleUnitSelect.getValue().equals("")) {
				setRequiredError(saleUnitSelect,
						getPropertyName("invalid_selection"), true);
				saleUnitSelect.focus();
				ret = false;
			} else
				setRequiredError(saleUnitSelect, null, false);

			if (totalSalesTextField.getValue() == null
					|| totalSalesTextField.getValue().equals("")) {
				setRequiredError(totalSalesTextField,
						getPropertyName("invalid_data"), true);
				totalSalesTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(totalSalesTextField.getValue()) < 0) {
						setRequiredError(totalSalesTextField,
								getPropertyName("invalid_data"), true);
						totalSalesTextField.focus();
						ret = false;
					} else
						setRequiredError(totalSalesTextField, null, false);
				} catch (Exception e) {
					setRequiredError(totalSalesTextField,
							getPropertyName("invalid_data"), true);
					totalSalesTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (purchUnitSelect.getValue() == null
					|| purchUnitSelect.getValue().equals("")) {
				setRequiredError(purchUnitSelect,
						getPropertyName("invalid_selection"), true);
				purchUnitSelect.focus();
				ret = false;
			} else
				setRequiredError(purchUnitSelect, null, false);

			if (totalPurchaseTextField.getValue() == null
					|| totalPurchaseTextField.getValue().equals("")) {
				setRequiredError(totalPurchaseTextField,
						getPropertyName("invalid_data"), true);
				totalPurchaseTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(totalPurchaseTextField.getValue()) <= 0) {
						setRequiredError(totalPurchaseTextField,
								getPropertyName("qty_greater_zero"), true);
						totalPurchaseTextField.focus();
						ret = false;
					} else
						setRequiredError(totalPurchaseTextField, null, false);
				} catch (Exception e) {
					setRequiredError(totalPurchaseTextField,
							getPropertyName("invalid_data"), true);
					totalPurchaseTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (openingUnitSelect.getValue() == null
					|| openingUnitSelect.getValue().equals("")) {
				setRequiredError(openingUnitSelect,
						getPropertyName("invalid_selection"), true);
				openingUnitSelect.focus();
				ret = false;
			} else
				setRequiredError(openingUnitSelect, null, false);

			if (openingQtyTextField.getValue() == null
					|| openingQtyTextField.getValue().equals("")) {
				setRequiredError(openingQtyTextField,
						getPropertyName("invalid_data"), true);
				openingQtyTextField.focus();
				ret = false;
			} else {
				try {
					toDouble(openingQtyTextField.getValue());

				} catch (Exception e) {
					setRequiredError(openingQtyTextField,
							"Enter a valid Quantity", true);
					openingQtyTextField.focus();
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

			if (ret) {
				if ((toDouble(totalPurchaseTextField.getValue())
						+ toDouble(openingQtyTextField.getValue())
						- toDouble(totalSalesTextField.getValue()) - toDouble(wasteTextField
							.getValue())) < 0) {
					setRequiredError(totalSalesTextField,
							getPropertyName("sales_qty_msg"), true);
					totalSalesTextField.focus();
					ret = false;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	public boolean isSubValid() {
		boolean ret = true;
		try {

			if (clientUnitSelect.getValue() == null
					|| clientUnitSelect.getValue().equals("")) {
				setRequiredError(clientUnitSelect,
						getPropertyName("invalid_selection"), true);
				clientUnitSelect.focus();
				ret = false;
			} else
				setRequiredError(officeCombo, null, false);

			if (clientTextField.getValue() == null
					|| clientTextField.getValue().equals("")) {
				setRequiredError(clientTextField,
						getPropertyName("invalid_data"), true);
				clientTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(clientTextField.getValue()) <= 0) {
						setRequiredError(clientTextField,
								getPropertyName("qty_greater_zero"), true);
						clientTextField.focus();
						ret = false;
					} else
						setRequiredError(clientTextField, null, false);
				} catch (Exception e) {
					setRequiredError(clientTextField,
							getPropertyName("invalid_data"), true);
					clientTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (clientSelect.getValue() == null
					|| clientSelect.getValue().equals("")) {
				setRequiredError(clientSelect,
						getPropertyName("invalid_selection"), true);
				clientSelect.focus();
				ret = false;
			} else
				setRequiredError(clientSelect, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	public void visibleAddupdateStockTransfer(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {
				table.removeItem(table.getValue());

				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					newitem = table.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

			}
			itemSelect.focus();

			if (table.getItemIds().size() <= 0) {
				removeContainerProperties(visibleColumns);
				visibleColumns = new ArrayList<String>(Arrays.asList(TBC_SN,
						TBC_ITEM_NAME, TBC_OPEN_QTY, TBC_OPEN_UNIT, TBC_P_QTY,
						TBC_P_UNIT, TBC_S_QTY, TBC_S_UNIT, TBC_WASTE,
						TBC_WASTE_UNIT, TBC_BALANCE_QTY));

				headerColumns = new ArrayList<String>(Arrays.asList("#",
						getPropertyName("item_name"),
						getPropertyName("opening_qty"),
						getPropertyName("open_unit"),
						getPropertyName("purchase_qty"),
						getPropertyName("unit"), getPropertyName("sales_qty"),
						getPropertyName("unit"), getPropertyName("waste"),
						getPropertyName("waste_unit"),
						getPropertyName("balance_qty")));

				String[] visibles = (String[]) visibleColumns
						.toArray(new String[visibleColumns.size()]);

				table.setVisibleColumns(visibles);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (officeCombo.getValue() == null || officeCombo.getValue().equals("")) {
			setRequiredError(officeCombo, getPropertyName("invalid_selection"),
					true);
			officeCombo.focus();
			ret = false;
		} else
			setRequiredError(officeCombo, null, false);

		if (usersCombo.getValue() == null || usersCombo.getValue().equals("")) {
			setRequiredError(usersCombo, getPropertyName("invalid_selection"),
					true);
			usersCombo.focus();
			ret = false;
		} else
			setRequiredError(usersCombo, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		return ret;
	}

	public void removeAllErrors() {
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		if (officeCombo.getComponentError() != null)
			setRequiredError(officeCombo, null, false);
		if (totalPurchaseTextField.getComponentError() != null)
			setRequiredError(totalPurchaseTextField, null, false);
		if (itemSelect.getComponentError() != null)
			setRequiredError(itemSelect, null, false);
	}

	public boolean isAlreadyAdded() {
		boolean avail = false;

		String[] splted;
		for (String val : visibleColumns) {
			splted = val.split("_");
			if (splted[0].equals(trnTypSelect.getValue().toString())
					&& splted[1].equals(clientSelect.getValue().toString())) {
				avail = true;
				old_ref_id = val;
				break;
			}
		}
		return avail;
	}

	public boolean isAlreadyAddedForUpdate(String type, String client_id) {
		boolean avail = false;

		String[] splted;
		for (String val : visibleColumns) {
			splted = val.split("_");
			if (splted[0].equals(type) && splted[1].equals(client_id)) {
				avail = true;
				old_ref_id = val;
				break;
			}
		}
		return avail;
	}

	public void removeContainerProperties(List<String> lst) {

		String refId;
		for (int i = 11; i < lst.size(); i++) {
			refId = lst.get(i);
			table.removeContainerProperty(refId);
		}

	}

	public Boolean getHelp() {
		return null;
	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
