package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.StockTransferDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.StockTransferInventoryDetails;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.LocationDao;
import com.inventory.model.LocationModel;
import com.inventory.purchase.model.ItemStockModel;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 18, 2013
 */
public class StockTransferPanel extends SContainerPanel {

	private static final long serialVersionUID = 7808874071526286297L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_STOCK_ID = "Stock ID";
	static String TBC_TO_OFFICE_ID = "To Office ID";
	static String TBC_TO_OFFICE_NAME = "To Office";
	static String TBC_CONVERTED_QTY = "Converted Qty";
	static String TBC_EXISTING_QTY = "Existing Qty";

	StockTransferDao stockTransferDao = new StockTransferDao();

	SComboField stockTransferNumberComboField;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;

	SComboField fromOfficeCombo;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveStockTransfer;
	SButton updateStockTransfer;
	SButton deleteStockTransfer;

	SDateField dateField;
	SComboField stockIDSelect;

	STextArea comment;

	private SComboField fromLocationCombo;

	private SComboField toOfficeCombo;

	private SComboField toLocationCombo;

	private SLabel label;

	private SComboField itemCombo;

	private ItemDao itemDao;

	private OfficeDao officeDao;

	private UnitDao unitDao;

	private LocationDao locationDao;

	private STextField conversionRateTextField;

	private STextField convertedQuantityTextField;

	private CommonMethodsDao comDao;

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public StockTransferPanel() {

		setId("Transfer");
		setSize(1000, 600);

		stockTransferDao = new StockTransferDao();
		itemDao = new ItemDao();
		officeDao = new OfficeDao();
		unitDao = new UnitDao();
		locationDao = new LocationDao();
		comDao = new CommonMethodsDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(11);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(5);
		masterDetailsGrid.setRows(3);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);

		form.setSizeFull();

		try {

			List<StockTransferModel> list = new ArrayList<StockTransferModel>();
			list.add(new StockTransferModel(0, "----Create New-----"));
			list.addAll(stockTransferDao
					.getAllStockTransferNumbersAsComment(getOfficeID()));
			stockTransferNumberComboField = new SComboField(null, 125, list,
					"id", "comments", false, getPropertyName("create_new"));
			label = new SLabel(getPropertyName("stock_transfr_no"));
			// label.setStyleName("textfield_align_right");
			// stockTransferNoLabel.setWidthUndefined();

			masterDetailsGrid.setHeight("100");
			masterDetailsGrid.setWidth("100%");
			masterDetailsGrid.addComponent(label, 1, 0);
			masterDetailsGrid.addComponent(stockTransferNumberComboField, 2, 0);
			masterDetailsGrid.setComponentAlignment(label,
					Alignment.BOTTOM_RIGHT);

			label = new SLabel(getPropertyName("date"));
			// label.setWidth(null);
			label.setStyleName("textfield_align_right", true);
			dateField = new SDateField(null, 120);
			dateField.setValue(getWorkingDate());

			masterDetailsGrid.addComponent(label, 3, 0);
			masterDetailsGrid.addComponent(dateField, 4, 0);
			// masterDetailsGrid.setComponentAlignment(label,
			// Alignment.MIDDLE_RIGHT);

			label = new SLabel(getPropertyName("from_branch"));
			label.setStyleName("textfield_align_right");
			fromOfficeCombo = new SComboField(null, 200,
					officeDao.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name", false, getPropertyName("select"));
			fromOfficeCombo.setValue(getOfficeID());
			fromOfficeCombo
					.setInputPrompt("------------ Select --------------");

			// fromOfficeCombo.sets

			masterDetailsGrid.addComponent(label, 1, 1);
			masterDetailsGrid.addComponent(fromOfficeCombo, 2, 1);

			List locationList = locationDao.getLocationModelList(getOfficeID());
			locationList.add(0, new LocationModel(0, "None"));
			label = new SLabel(getPropertyName("from_location"));
			label.setStyleName("textfield_align_right");
			fromLocationCombo = new SComboField(null, 200,
					locationList, "id",
					"name", false, getPropertyName("select"));
			fromLocationCombo.setValue((long)0);

			masterDetailsGrid.addComponent(label, 3, 1);
			masterDetailsGrid.addComponent(fromLocationCombo, 4, 1);

			label = new SLabel(getPropertyName("to_branch"));
			label.setStyleName("textfield_align_right");
			toOfficeCombo = new SComboField(null, 200,
					officeDao.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name", false, getPropertyName("select"));
			toOfficeCombo.setValue(getOfficeID());

			masterDetailsGrid.addComponent(label, 1, 2);
			masterDetailsGrid.addComponent(toOfficeCombo, 2, 2);

			locationList = locationDao.getLocationModelList(getOfficeID());
			locationList.add(0, new LocationModel(0, "None"));
			label = new SLabel(getPropertyName("to_location"));
			label.setStyleName("textfield_align_right");
			toLocationCombo = new SComboField(null, 200,
					locationList, "id",
					"name", false, getPropertyName("select"));
			toLocationCombo.setValue((long)0);

			masterDetailsGrid.addComponent(label, 3, 2);
			masterDetailsGrid.addComponent(toLocationCombo, 4, 2);

			masterDetailsGrid.setSpacing(true);
			/*
			 * masterDetailsGrid .setComponentAlignment(date,
			 * Alignment.MIDDLE_LEFT);
			 */
			masterDetailsGrid.setColumnExpandRatio(1, 4.3f);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 3.5f);
			masterDetailsGrid.setColumnExpandRatio(4, 2);

			masterDetailsGrid.setStyleName("master_border");
			// ==========================================================================
			itemCombo = new SComboField(getPropertyName("item"), 200);
			// itemCombo.addStyleName(style)
			loadItemComboField();
			itemCombo.setInputPrompt("------------ All ------------");

			stockIDSelect = new SComboField(getPropertyName("stock"), 300,
					null, "id", "stock_details", true,
					getPropertyName("select"));

			unitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");

			quantityTextField = new STextField(getPropertyName("qty"), 60);
			quantityTextField.setStyleName("textfield_align_right");

			conversionRateTextField = new STextField(
					getPropertyName("conversion_rate"), 60);
			conversionRateTextField.setStyleName("textfield_align_right");
			conversionRateTextField.setValue("1");
			conversionRateTextField.setReadOnly(true);

			convertedQuantityTextField = new STextField(
					getPropertyName("convrted_qty"), 60);
			convertedQuantityTextField.setStyleName("textfield_align_right");
			convertedQuantityTextField.setReadOnly(true);

			addItemButton = new SButton(null, getPropertyName("add_item"));
			addItemButton.setStyleName("addItemBtnStyle");

			updateItemButton = new SButton(null, getPropertyName("update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(itemCombo);
			addingGrid.addComponent(stockIDSelect);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			addingGrid.addComponent(conversionRateTextField);
			addingGrid.addComponent(convertedQuantityTextField);
			addingGrid.addComponent(buttonLay);

			addingGrid.setColumnExpandRatio(0, 2);
			addingGrid.setColumnExpandRatio(1, 1);
			addingGrid.setColumnExpandRatio(2, 1);
			addingGrid.setColumnExpandRatio(3, 1);
			addingGrid.setColumnExpandRatio(4, 1);
			addingGrid.setColumnExpandRatio(5, 1);
			addingGrid.setColumnExpandRatio(6, 1);
			/*
			 * addingGrid.setColumnExpandRatio(5, 1);
			 * addingGrid.setColumnExpandRatio(6, 1);
			 * addingGrid.setColumnExpandRatio(7, 3);
			 * addingGrid.setColumnExpandRatio(8, 3);
			 */
			addingGrid.setWidth("900");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1000, 200);

			table.setMultiSelect(false);

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
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
					TBC_STOCK_ID, null, Align.RIGHT);
			table.addContainerProperty(TBC_EXISTING_QTY, Double.class, null,
					TBC_EXISTING_QTY, null, Align.CENTER);
			/*
			 * table.addContainerProperty(TBC_TO_OFFICE_ID, Long.class, null,
			 * TBC_TO_OFFICE_ID, null, Align.RIGHT);
			 */
			/*
			 * table.addContainerProperty(TBC_TO_OFFICE_NAME, String.class,
			 * null, getPropertyName("to_offc"), null, Align.CENTER);
			 */
			table.addContainerProperty(TBC_CONVERTED_QTY, Double.class, null,
					TBC_CONVERTED_QTY, null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT });

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("900");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			comment = new STextArea(null, 250, 40);

			saveStockTransfer = new SButton(getPropertyName("Save"), 70);
			saveStockTransfer.setStyleName("savebtnStyle");
			saveStockTransfer.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			updateStockTransfer = new SButton(getPropertyName("Update"), 80);
			updateStockTransfer.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updateStockTransfer.setStyleName("updatebtnStyle");

			deleteStockTransfer = new SButton(getPropertyName("Delete"), 78);
			deleteStockTransfer.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteStockTransfer.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveStockTransfer);
			mainButtonLayout.addComponent(updateStockTransfer);
			mainButtonLayout.addComponent(deleteStockTransfer);
			updateStockTransfer.setVisible(false);
			deleteStockTransfer.setVisible(false);

			buttonsGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					0);
			buttonsGrid.addComponent(comment, 1, 0);

			buttonsGrid.setColumnExpandRatio(0, 1);
			buttonsGrid.setColumnExpandRatio(1, 5);

			buttonsGrid.addComponent(mainButtonLayout, 1, 1);
			mainButtonLayout.setSpacing(true);
			buttonsGrid.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("900");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			dateField.focus();

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

			table.addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadStockTransfer(0);
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
			// =============================================================================================
			fromOfficeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadFromLocationComboField();
					loadItemComboField();
				}

				private void loadFromLocationComboField() {
					List<LocationModel> list = new ArrayList<LocationModel>();
					list.add(0, new LocationModel(0, "None"));
					if (fromOfficeCombo.getValue() != null) {

						try {
							list.addAll(locationDao
									.getLocationModelList(toLong(fromOfficeCombo
											.getValue() + "")));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					SCollectionContainer bic = SCollectionContainer.setList(
							list, "id");
					fromLocationCombo.setContainerDataSource(bic);
					fromLocationCombo.setItemCaptionPropertyId("name");
					/*
					 * if (id != 0) { requestNoComboField.setValue(id); } else {
					 * requestNoComboField.setValue(null); }
					 */

					fromLocationCombo
							.setInputPrompt("--------------- Select ---------------");
				}
			});
			// =============================================================================================
			fromLocationCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {

					try {
						loadStockDetails();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});
			// =============================================================================================
			itemCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						loadStockDetails();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});
			// =============================================================================================
			toOfficeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List<LocationModel> list = new ArrayList<LocationModel>();
					list.add(0, new LocationModel(0, "None"));
					if (toOfficeCombo.getValue() != null) {

						try {
							list.addAll(locationDao
									.getLocationModelList(toLong(toOfficeCombo
											.getValue() + "")));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					SCollectionContainer bic = SCollectionContainer.setList(
							list, "id");
					toLocationCombo.setContainerDataSource(bic);
					toLocationCombo.setItemCaptionPropertyId("name");
					/*
					 * if (id != 0) { requestNoComboField.setValue(id); } else {
					 * requestNoComboField.setValue(null); }
					 */

					toLocationCombo
							.setInputPrompt("---------------- Select ------------------");

				}
			});

			saveStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isValid()) {

							StockTransferModel stockTransferModelObj = new StockTransferModel();

							List<StockTransferInventoryDetails> itemsList = new ArrayList<StockTransferInventoryDetails>();

							StockTransferInventoryDetails invDetailsObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invDetailsObj = new StockTransferInventoryDetails();

								item = table.getItem(it.next());

								invDetailsObj.setStock_id(stockTransferDao
										.getItemStocks((Long) item
												.getItemProperty(TBC_STOCK_ID)
												.getValue()));
								invDetailsObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invDetailsObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invDetailsObj
										.setQuantity_in_basic_unit((Double) item
												.getItemProperty(
														TBC_CONVERTED_QTY)
												.getValue());

								itemsList.add(invDetailsObj);
							}

							stockTransferModelObj
									.setTransfer_no(getNextSequence(
											"Stock Transfer Number",
											getLoginID()));
							stockTransferModelObj.setTransfer_date(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							stockTransferModelObj.setFrom_office(new S_OfficeModel(toLong(fromOfficeCombo
											.getValue().toString())));
							stockTransferModelObj.setFrom_location(locationDao.getLocationModel(toLong(fromLocationCombo
											.getValue().toString())));
							stockTransferModelObj.setTo_office(new S_OfficeModel(toLong(toOfficeCombo.getValue()
											.toString())));
							stockTransferModelObj.setTo_location(locationDao.getLocationModel(toLong(toLocationCombo
									.getValue().toString())));
							stockTransferModelObj.setComments(comment
									.getValue());
							stockTransferModelObj.setLogin(new S_LoginModel(
									getLoginID()));
							stockTransferModelObj
									.setStatus(SConstants.statuses.STOCK_TRANSFER);
							stockTransferModelObj
									.setInventory_details_list(itemsList);

							long id = stockTransferDao
									.save(stockTransferModelObj);
							clearMainFields();

							loadStockTransfer(id);
							updateStockTransfer.setVisible(true);
							deleteStockTransfer.setVisible(true);
							saveStockTransfer.setVisible(false);
							// reloadStock();

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			stockTransferNumberComboField
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {

								removeAllErrors();
								clearMainFields();

								updateStockTransfer.setVisible(true);
								deleteStockTransfer.setVisible(true);
								saveStockTransfer.setVisible(false);
								if (stockTransferNumberComboField.getValue() != null
										&& !stockTransferNumberComboField
												.getValue().toString()
												.equals("0")) {

									StockTransferModel poObj = stockTransferDao
											.getStockTransfer((Long) stockTransferNumberComboField
													.getValue());

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_QTY,
											TBC_UNIT_ID, TBC_UNIT,
											TBC_STOCK_ID, TBC_CONVERTED_QTY,TBC_EXISTING_QTY });

							
									StockTransferInventoryDetails invObj;
									Iterator it = poObj
											.getInventory_details_list()
											.iterator();
									while (it.hasNext()) {
										invObj = (StockTransferInventoryDetails) it
												.next();

										table.addItem(
												new Object[] {
														table.getItemIds()
																.size() + 1,
														invObj.getStock_id()
																.getItem()
																.getId(),
														invObj.getStock_id()
																.getItem()
																.getItem_code(),
														invObj.getStock_id()
																.getItem()
																.getName(),
														invObj.getQuantity(),
														invObj.getUnit()
																.getId(),
														invObj.getUnit()
																.getSymbol(),
														invObj.getStock_id()
																.getId(),
														invObj.getQuantity_in_basic_unit(),
														invObj.getQuantity_in_basic_unit()},
												table.getItemIds().size() + 1);

									}

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_QTY, TBC_UNIT });

									fromOfficeCombo.setValue(poObj.getFrom_office().getId());
									toOfficeCombo.setValue(poObj.getTo_office().getId());
									if(poObj.getFrom_location()!=null)
										fromLocationCombo.setValue(poObj.getFrom_location().getId());
									else
										fromLocationCombo.setValue((long)0);
									if(poObj.getFrom_location()!=null)
										toLocationCombo.setValue(poObj.getTo_location().getId());
									else
										toLocationCombo.setValue((long)0);
									dateField.setValue(poObj.getTransfer_date());
									comment.setValue(poObj.getComments());
									

								
									updateStockTransfer.setVisible(true);
									deleteStockTransfer.setVisible(true);
									saveStockTransfer.setVisible(false);

									if (poObj.getStatus() == SConstants.statuses.STOCK_RECEIVED) {
										updateStockTransfer.setEnabled(false);
										deleteStockTransfer.setEnabled(false);
										SNotification
												.show(getPropertyName("item_already_rxd"),
														Type.TRAY_NOTIFICATION);
									} else {
										updateStockTransfer.setEnabled(true);
										deleteStockTransfer.setEnabled(true);
									}

								} else {
									clearMainFields();

									saveStockTransfer.setVisible(true);
									updateStockTransfer.setVisible(false);
									deleteStockTransfer.setVisible(false);
								}

								calculateTotals();

								stockIDSelect.setValue(null);
								stockIDSelect.focus();
								quantityTextField.setValue("0.0");

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

							StockTransferModel stockTransferModelObj = stockTransferDao.getStockTransfer((Long) stockTransferNumberComboField
											.getValue());
						//	stockTransferDao.stockReset(toLong(stockTransferNumberComboField.getValue().toString()));

							List<StockTransferInventoryDetails> itemsList = new ArrayList<StockTransferInventoryDetails>();

							StockTransferInventoryDetails invDetailsObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invDetailsObj = new StockTransferInventoryDetails();

								item = table.getItem(it.next());

								invDetailsObj.setStock_id(stockTransferDao
										.getItemStocks((Long) item
												.getItemProperty(TBC_STOCK_ID)
												.getValue()));
								invDetailsObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invDetailsObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invDetailsObj
										.setQuantity_in_basic_unit((Double) item
												.getItemProperty(
														TBC_CONVERTED_QTY)
												.getValue());

								itemsList.add(invDetailsObj);
							}

							stockTransferModelObj.setTransfer_date(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							stockTransferModelObj.setFrom_office(new S_OfficeModel(toLong(fromOfficeCombo
											.getValue().toString())));
							stockTransferModelObj.setFrom_location(locationDao.getLocationModel(toLong(fromLocationCombo
									.getValue().toString())));
							stockTransferModelObj.setTo_office(new S_OfficeModel(toLong(toOfficeCombo.getValue()
											.toString())));
							stockTransferModelObj.setTo_location(locationDao.getLocationModel(toLong(toLocationCombo
									.getValue().toString())));
							stockTransferModelObj.setComments(comment
									.getValue());
							stockTransferModelObj.setLogin(new S_LoginModel(
									getLoginID()));
							stockTransferModelObj
									.setStatus(SConstants.statuses.STOCK_TRANSFER);
							stockTransferModelObj
							.setInventory_details_list(itemsList);

							stockTransferDao.update(stockTransferModelObj);

							loadStockTransfer(stockTransferModelObj.getId());

							// reloadStock();
					//		stockTransferNumberComboField.setValue(null);

							Notification.show(
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

					if (stockTransferNumberComboField.getValue() != null
							&& !stockTransferNumberComboField.getValue()
									.toString().equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												stockTransferDao
														.delete((Long) stockTransferNumberComboField
																.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);
												loadStockTransfer(0);

												// reloadStock();

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
					itemCombo.setValue(null);
					removeAllErrors();
					Collection<Integer> selectedItems = (Collection<Integer>) table
							.getItemIds();

					Iterator<Integer> itr = selectedItems.iterator();
					int selId = 0;
					while (itr.hasNext()) {
						selId = itr.next();
						if (table.isSelected(selId)) {
							System.out
									.println("============= KKKKKK ====================");
							break;
						}
					}
					/*
					 * Integer selectedItems = null;
					 * 
					 * if (table.getValue() != null) { selectedItems = (Integer)
					 * table.getValue(); }
					 */

					if (selId != 0) {

						Item item = table.getItem(selId);
						
						itemCombo.setValue(item.getItemProperty(
								TBC_ITEM_ID).getValue());

						stockIDSelect.setValue(item.getItemProperty(
								TBC_STOCK_ID).getValue());

						double convertedQty = toDouble(item
								.getItemProperty(TBC_CONVERTED_QTY).getValue()
								.toString());
						double qty = toDouble(item.getItemProperty(TBC_QTY)
								.getValue().toString());

						quantityTextField.setValue(qty + "");
						convertedQuantityTextField.setNewValue(convertedQty
								+ "");
						conversionRateTextField
								.setNewValue(roundNumber(convertedQty / qty)
										+ "");
						unitSelect.setValue((Long) item.getItemProperty(
								TBC_UNIT_ID).getValue());
						System.out.println("======UNIT ID ====  "
								+ item.getItemProperty(TBC_UNIT_ID).getValue()
										.toString());
						System.out.println("======UNIT ID ====  "
								+ unitSelect.getValue());
						// unitSelect.setValue(newValue)

						/*
						 * fromOfficeCombo.setValue(item.getItemProperty(
						 * TBC_TO_OFFICE_ID).getValue());
						 */

						visibleAddupdateStockTransfer(false, true);

						// stockIDSelect.focus();

					} else {
						itemCombo.setValue(null);
						stockIDSelect.setValue(null);
						quantityTextField.setValue("0.0");
						// officeCombo.setValue(null);

						visibleAddupdateStockTransfer(true, false);

						stockIDSelect.focus();
					}

				}

			});

			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					if (table.getComponentError() != null) {
						setRequiredError(table, null, false);
					}
					ItemStockModel itemStockModel = null;
					if (isValidAction()) {
						try {
							itemStockModel = stockTransferDao
									.getItemStocks(toLong(stockIDSelect
											.getValue() + ""));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int id = table.getItemIds().size() + 1;
						table.setVisibleColumns(new String[] { TBC_SN,
								TBC_ITEM_ID, TBC_ITEM_CODE, TBC_ITEM_NAME,
								TBC_QTY, TBC_UNIT_ID, TBC_UNIT, TBC_STOCK_ID,
								TBC_CONVERTED_QTY ,TBC_EXISTING_QTY});
						table.addItem(
								new Object[] {
										id, // 1 int
										itemStockModel.getItem().getId(),// 2
																			// long
										itemStockModel.getItem().getItem_code(),// 3
																				// string
										itemStockModel.getItem().getName(),// 4
																			// string
										toDouble(quantityTextField.getValue()),// 5
																				// double
										toLong(unitSelect.getValue() + ""),// 6
																			// long
										unitSelect.getItemCaption(unitSelect
												.getValue()),// 7 String
										toLong(stockIDSelect.getValue() + ""),// 8
																				// Long
										toDouble(convertedQuantityTextField
												.getValue().toString()),// 9
												// double
												0.0},// 10
												// double
								id);
						System.out.println("======UNIT ID ====  "
								+ toLong(unitSelect.getValue() + ""));
						table.setVisibleColumns(new String[] { TBC_SN,
								TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, TBC_UNIT });
						clearFields();
						calculateTotals();
					}

					/*
					 * try {
					 * 
					 * if (table.getComponentError() != null)
					 * setRequiredError(table, null, false);
					 * 
					 * if (isAddingValid()) {
					 * 
					 * boolean already_added_item = false, eror_hapnd = false;
					 * 
					 * ItemStockModel stk = stockTransferDao
					 * .getItemStocks((Long) stockIDSelect .getValue());
					 * 
					 * double qty = 0, totalameStockSamOfcQty = 0,
					 * totalSameStockQty = 0;
					 * 
					 * Item item; Iterator itr2 = table.getItemIds().iterator();
					 * while (itr2.hasNext()) { System.out.println(
					 * "===== ENTER TO LOOP ========================"); //
					 * Object obj=itr2.next(); item =
					 * table.getItem(itr2.next());
					 * 
					 * if (item.getItemProperty(TBC_STOCK_ID) .getValue()
					 * .toString() .equals(stockIDSelect.getValue()
					 * .toString())) {
					 * 
					 * if ((Double) item.getItemProperty(
					 * TBC_EXISTING_QTY).getValue() > 0) {
					 * totalameStockSamOfcQty += (Double) item
					 * .getItemProperty(TBC_QTY) .getValue() - (Double) item
					 * .getItemProperty( TBC_EXISTING_QTY) .getValue();
					 * 
					 * if (item.getItemProperty( TBC_TO_OFFICE_ID) .getValue()
					 * .toString() .equals(fromOfficeCombo
					 * .getValue().toString())) totalameStockSamOfcQty -=
					 * (Double) item .getItemProperty( TBC_EXISTING_QTY)
					 * .getValue(); } else { totalSameStockQty += (Double) item
					 * .getItemProperty(TBC_QTY) .getValue(); if
					 * (item.getItemProperty( TBC_TO_OFFICE_ID) .getValue()
					 * .toString() .equals(fromOfficeCombo
					 * .getValue().toString())) totalameStockSamOfcQty +=
					 * (Double) item .getItemProperty(TBC_QTY) .getValue();
					 * 
					 * }
					 * 
					 * } }
					 * 
					 * qty = toDouble(quantityTextField.getValue());
					 * System.out.println
					 * ("========== QUANTITY ===================== "+qty);
					 * totalameStockSamOfcQty += qty; totalSameStockQty += qty;
					 * 
					 * Iterator itr1 = table.getItemIds().iterator(); List
					 * delList = new ArrayList(); while (itr1.hasNext()) { //
					 * Object obj=itr1.next(); item =
					 * table.getItem(itr1.next());
					 * 
					 * if (item.getItemProperty(TBC_STOCK_ID) .getValue()
					 * .toString() .equals(stockIDSelect.getValue() .toString())
					 * && item.getItemProperty( TBC_TO_OFFICE_ID) .getValue()
					 * .toString() .equals(fromOfficeCombo
					 * .getValue().toString())) {
					 * 
					 * if ((Double) item.getItemProperty(
					 * TBC_EXISTING_QTY).getValue() > 0) {
					 * 
					 * if ((totalameStockSamOfcQty + (Double) item
					 * .getItemProperty( TBC_EXISTING_QTY) .getValue()) <= stk
					 * .getBalance()) { item.getItemProperty(TBC_QTY) .setValue(
					 * qty + (Double) item .getItemProperty( TBC_EXISTING_QTY)
					 * .getValue()); item.getItemProperty(TBC_STOCK_ID)
					 * .setValue(stk.getId()); } else { eror_hapnd = true;
					 * setRequiredError( quantityTextField,
					 * getPropertyName("no_sufficient_bal") + (stk.getBalance()
					 * - totalameStockSamOfcQty + qty - (Double) item
					 * .getItemProperty( TBC_EXISTING_QTY) .getValue()), true);
					 * quantityTextField.focus(); } } else { if
					 * (totalSameStockQty <= stk .getBalance()) {
					 * item.getItemProperty(TBC_QTY) .setValue(
					 * totalameStockSamOfcQty);
					 * item.getItemProperty(TBC_STOCK_ID)
					 * .setValue(stk.getId()); } else { eror_hapnd = true;
					 * setRequiredError( quantityTextField,
					 * getPropertyName("no_sufficient_bal") + (stk.getBalance()
					 * - totalSameStockQty + qty), true);
					 * quantityTextField.focus(); } } already_added_item = true;
					 * 
					 * break; } }
					 * 
					 * if (!already_added_item) {
					 * 
					 * table.setVisibleColumns( new String[] { TBC_SN,
					 * TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT, TBC_QTY});
					 * 
					 * ItemModel itm = stk.getItem(); UnitModel objUnit =
					 * unitDao .getUnit((Long) unitSelect.getValue());
					 * 
					 * int id = 0, ct = 0; Iterator it =
					 * table.getItemIds().iterator(); while (it.hasNext()) { id
					 * = (Integer) it.next(); } id++;
					 * 
					 * if (totalSameStockQty <= stk.getBalance()) {
					 * System.out.println(
					 * "=================== totalSameStockQty <= stk.getBalance() ========================"
					 * +id+ "\n"+table.getItemIds().size() + 1+"\n"+
					 * itm.getId()+"\n"+ itm.getItem_code()+"\n"+
					 * itm.getName()+"\n"+ qty+"\n"+ objUnit.getId()+"\n"+
					 * objUnit.getSymbol()+"\n"+ stk.getId()); table.addItem(
					 * new Object[] { table.getItemIds().size() + 1,
					 * itm.getId(), itm.getItem_code(), itm.getName(), qty,
					 * objUnit.getId(), objUnit.getSymbol(), stk.getId(), (Long)
					 * fromOfficeCombo .getValue(), fromOfficeCombo
					 * .getItemCaption(fromOfficeCombo .getValue()), (double)
					 * 0.0 }, id); } else { setRequiredError( quantityTextField,
					 * getPropertyName("no_sufficient_bal") + (stk.getBalance()
					 * - totalSameStockQty + qty), true); eror_hapnd = true;
					 * quantityTextField.focus(); }
					 * 
					 * table.setVisibleColumns(new String[] { TBC_SN,
					 * TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT, TBC_QTY});
					 * 
					 * if (!eror_hapnd) { stockIDSelect.setValue(null);
					 * stockIDSelect.focus(); quantityTextField.setValue("0.0");
					 * } }
					 * 
					 * if (!eror_hapnd) { calculateTotals();
					 * 
					 * stockIDSelect.setValue(null); stockIDSelect.focus();
					 * quantityTextField.setValue("0.0"); //
					 * officeCombo.setValue(null); } }
					 * 
					 * } catch (Exception e) { // TODO Auto-generated catch
					 * block try { throw e; } catch (Exception e1) { // TODO
					 * Auto-generated catch block e1.printStackTrace(); } //
					 * e.printStackTrace();
					 * 
					 * }
					 */}

			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					// try {

					if (isValidAction()) {

						if (table.getComponentError() != null) {
							setRequiredError(table, null, false);
						}
						ItemStockModel itemStockModel = null;
						if (isValidAction()) {
							try {
								itemStockModel = stockTransferDao
										.getItemStocks(toLong(stockIDSelect
												.getValue() + ""));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// int id = table.getItemIds().size()+1;
							/*
							 * table.setVisibleColumns( new String[] {
							 * TBC_SN,TBC_ITEM_ID,TBC_ITEM_CODE,
							 * TBC_ITEM_NAME,TBC_QTY,
							 * TBC_UNIT_ID,TBC_UNIT,TBC_STOCK_ID,
							 * TBC_EXISTING_QTY});
							 */
							/*
							 * table.addItem(new Object[]{id, //1 int
							 * itemStockModel.getItem().getId(),//2 long
							 * itemStockModel.getItem().getItem_code(),//3
							 * string itemStockModel.getItem().getName(),//4
							 * string toDouble(quantityTextField.getValue()),//5
							 * double toLong(unitSelect.getValue()+""),//6 long
							 * unitSelect
							 * .getItemCaption(unitSelect.getValue()),//7 String
							 * toLong(stockIDSelect.getValue()+""),//8 Long
							 * (double)0},//9 double id);
							 */
							Collection<Integer> selectedItems = (Collection<Integer>) table
									.getItemIds();

							double qty = toDouble(quantityTextField.getValue());

							for (Integer selId : selectedItems) {
								if (table.isSelected(selId)) {
									System.out
											.println("============= PPPPP ====================");
									Item selectedItem = table.getItem(selId);
									selectedItem.getItemProperty(TBC_ITEM_ID)
											.setValue(
													itemStockModel.getItem()
															.getId());
									selectedItem.getItemProperty(TBC_ITEM_CODE)
											.setValue(
													itemStockModel.getItem()
															.getItem_code());
									selectedItem.getItemProperty(TBC_ITEM_NAME)
											.setValue(
													itemStockModel.getItem()
															.getName());
									selectedItem.getItemProperty(TBC_QTY)
											.setValue(qty);
									selectedItem.getItemProperty(TBC_UNIT_ID)
											.setValue(
													toLong(unitSelect
															.getValue() + ""));
									selectedItem
											.getItemProperty(TBC_UNIT)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));
									selectedItem.getItemProperty(TBC_STOCK_ID)
											.setValue(
													toLong(stockIDSelect
															.getValue() + ""));
									selectedItem.getItemProperty(
											TBC_CONVERTED_QTY).setValue(
											toDouble(convertedQuantityTextField
													.getValue()));

									clearFields();
									visibleAddupdateStockTransfer(true, false);
									break;
								}

							}
							calculateTotals();

							/*
							 * table.setVisibleColumns( new String[] { TBC_SN,
							 * TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY,TBC_UNIT
							 * });
							 */

						}

						/*
						 * 
						 * ItemStockModel stk = stockTransferDao
						 * .getItemStocks((Long) stockIDSelect .getValue());
						 * 
						 * Integer selectedItems = (Integer) table .getValue();
						 * 
						 * double qty = toDouble(quantityTextField.getValue());
						 * 
						 * Item selectedItem = table.getItem(selectedItems);
						 * totalUsedQty += (Double) selectedItem
						 * .getItemProperty(TBC_EXISTING_QTY) .getValue();
						 * 
						 * Iterator itr2 = table.getItemIds().iterator(); Item
						 * item; while (itr2.hasNext()) { // Object
						 * obj=itr2.next(); item = table.getItem(itr2.next());
						 * 
						 * if (item.getItemProperty(TBC_STOCK_ID) .getValue()
						 * .toString() .equals(stockIDSelect.getValue()
						 * .toString())) { totalSameStockQty += (Double) item
						 * .getItemProperty(TBC_QTY) .getValue() - (Double)
						 * item.getItemProperty( TBC_EXISTING_QTY) .getValue();
						 * } } totalSameStockQty += qty;
						 * 
						 * if (totalSameStockQty <= (stk.getBalance() +
						 * totalUsedQty)) {
						 * 
						 * item = table.getItem(selectedItems.iterator()
						 * .next());
						 * 
						 * qty = toDouble(quantityTextField.getValue());
						 * 
						 * ItemModel itm = stk.getItem(); UnitModel objUnit =
						 * unitDao .getUnit((Long) unitSelect.getValue());
						 * 
						 * item.getItemProperty(TBC_ITEM_ID).setValue(
						 * itm.getId());
						 * item.getItemProperty(TBC_ITEM_CODE).setValue(
						 * itm.getItem_code());
						 * item.getItemProperty(TBC_ITEM_NAME).setValue(
						 * itm.getName());
						 * item.getItemProperty(TBC_QTY).setValue(qty);
						 * item.getItemProperty(TBC_UNIT_ID).setValue(
						 * objUnit.getId());
						 * item.getItemProperty(TBC_UNIT).setValue(
						 * objUnit.getSymbol());
						 * item.getItemProperty(TBC_STOCK_ID).setValue(
						 * stk.getId());
						 * 
						 * item.getItemProperty(TBC_TO_OFFICE_ID) .setValue(
						 * (Long) fromOfficeCombo .getValue());
						 * item.getItemProperty(TBC_TO_OFFICE_NAME) .setValue(
						 * fromOfficeCombo .getItemCaption(fromOfficeCombo
						 * .getValue()));
						 * 
						 * table.setVisibleColumns(new String[] { TBC_SN,
						 * TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY,TBC_UNIT });
						 * 
						 * stockIDSelect.setValue(null);
						 * quantityTextField.setValue("0.0");
						 * 
						 * visibleAddupdateStockTransfer(true, false);
						 * 
						 * stockIDSelect.focus();
						 * 
						 * table.setValue(null);
						 * 
						 * calculateTotals();
						 * 
						 * } else { setRequiredError( quantityTextField,
						 * getPropertyName("no_sufficient_bal") +
						 * (stk.getBalance() + totalUsedQty - totalSameStockQty
						 * + qty), true); quantityTextField.focus(); }
						 */}

					/*
					 * } catch (Exception e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); }
					 */
				}
			});

			stockIDSelect.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						List stkList = null;
						if (stockIDSelect.getValue() != null) {
							ItemStockModel stk = stockTransferDao
									.getItemStocks((Long) stockIDSelect
											.getValue());
							unitSelect
									.setValue(stk.getItem().getUnit().getId());
							doBasicUnitConversion();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					quantityTextField.focus();
					quantityTextField.selectAll();

				}
			});
			quantityTextField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					doBasicUnitConversion();
				}
			});

			unitSelect.addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					doBasicUnitConversion();

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

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void clearMainFields() {

		table.removeAllItems();
		fromOfficeCombo.setValue(getOfficeID());
		toOfficeCombo.setValue(getOfficeID());
		dateField.setValue(CommonUtil.getCurrentSQLDate());
		fromLocationCombo.setValue(null);
		toLocationCombo.setValue(null);
		comment.setValue("");

	}

	private void doBasicUnitConversion() {
		try {
			if (unitSelect.getValue() != null) {
				if (stockIDSelect.getValue() != null) {
					ItemModel itm = stockTransferDao.getItemStocks(
							toLong(stockIDSelect.getValue().toString()))
							.getItem();
					if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
						conversionRateTextField.setNewValue("1");
						/*
						 * convertionQuantityField.setVisible(false);
						 * convertedQuantityField.setVisible(false);
						 */
					} else {
						/*
						 * convertionQuantityField.setVisible(true);
						 * convertedQuantityField.setVisible(true);
						 */
						double cnvr_qty = comDao.getConvertionRate(itm.getId(),
								(Long) unitSelect.getValue(), 0);
						conversionRateTextField.setNewValue(asString(cnvr_qty));
					}
					// unitPriceField.setNewValue(roundNumber(comDao.getItemPrice(itm.getId(),
					// (Long) unitSelect.getValue(), 0)));
					if (quantityTextField.getValue() != null
							&& !quantityTextField.getValue().toString().trim()
									.equals("")
							&& toDouble(quantityTextField.getValue()) != 0) {
						convertedQuantityTextField
								.setNewValue(asString(toDouble(quantityTextField
										.getValue())
										* toDouble(conversionRateTextField
												.getValue())));
						// netPriceField.setNewValue(asString(unitPriceField.getValue()*toDouble(quantityField.getValue())/unitPriceField.getConversionRate()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearFields() {
		table.setValue(null);
		itemCombo.setValue(null);
		stockIDSelect.setValue(null);
		quantityTextField.setValue("");
		conversionRateTextField.setNewValue("1");
		convertedQuantityTextField.setNewValue("");

	}

	private boolean isValidAction() {
		boolean valid = true;
		if (stockIDSelect.getValue() == null
				|| stockIDSelect.getValue().equals("")) {
			setRequiredError(stockIDSelect, getPropertyName("invalid_data"),
					true);
			valid = false;
		}
		if (quantityTextField.getValue() == null
				|| quantityTextField.getValue().equals("")) {
			setRequiredError(quantityTextField,
					getPropertyName("invalid_data"), true);
			quantityTextField.focus();
			valid = false;
		} else {
			try {
				if (toDouble(quantityTextField.getValue()) <= 0) {
					setRequiredError(quantityTextField,
							getPropertyName("qty_greater_zero"), true);
					quantityTextField.focus();
					valid = false;
				} else
					setRequiredError(quantityTextField, null, false);
			} catch (Exception e) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				valid = false;
				// TODO: handle exception
			}
		}
		if (conversionRateTextField.getValue() == null
				|| conversionRateTextField.getValue().equals("")) {
			setRequiredError(conversionRateTextField,
					getPropertyName("invalid_data"), true);
			// quantityTextField.focus();
			valid = false;
		} else {
			try {
				if (toDouble(conversionRateTextField.getValue()) <= 0) {
					setRequiredError(conversionRateTextField,
							getPropertyName("qty_greater_zero"), true);
					// quantityTextField.focus();
					valid = false;
				} else
					setRequiredError(conversionRateTextField, null, false);
			} catch (Exception e) {
				setRequiredError(conversionRateTextField,
						getPropertyName("invalid_data"), true);
				// quantityTextField.focus();
				valid = false;
				// TODO: handle exception
			}
		}

		if (convertedQuantityTextField.getValue() == null
				|| convertedQuantityTextField.getValue().equals("")) {
			setRequiredError(convertedQuantityTextField,
					getPropertyName("invalid_data"), true);
			// quantityTextField.focus();
			valid = false;
		} else {
			try {
				if (toDouble(convertedQuantityTextField.getValue()) <= 0) {
					setRequiredError(convertedQuantityTextField,
							getPropertyName("qty_greater_zero"), true);
					// quantityTextField.focus();
					valid = false;
				} else
					setRequiredError(convertedQuantityTextField, null, false);
			} catch (Exception e) {
				setRequiredError(convertedQuantityTextField,
						getPropertyName("invalid_data"), true);
				// quantityTextField.focus();
				valid = false;
				// TODO: handle exception
			}
		}

		if (valid) {
			long available = getAvailableStock();
			if (available < toDouble(quantityTextField.getValue())) {
				setRequiredError(quantityTextField,
						getPropertyName("no_sufficient_bal") + available, true);
				quantityTextField.focus();
				valid = false;
			}
		}
		return valid;
	}

	private long getAvailableStock() {
		try {
			long available = (long) stockTransferDao.getItemStocks(
					toLong(stockIDSelect.getValue() + "")).getBalance();
			Item item;
			Iterator it = table.getItemIds().iterator();
			int id;
			while (it.hasNext()) {
				id = (Integer) it.next();
				item = table.getItem(id);
				available += (Double) item.getItemProperty(
						TBC_EXISTING_QTY).getValue();
				if (table.isSelected(id)) {
					continue;
				}
				if ((Long) item.getItemProperty(TBC_STOCK_ID).getValue() == toLong(stockIDSelect
						.getValue() + "")) {
					available -= (Double) item.getItemProperty(
							TBC_CONVERTED_QTY).getValue();
				}

			}
			return available;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private void loadStockDetails() throws Exception {
		List list = new ArrayList();
		// list.add(0, new ItemStockModel(0,"--------- Select -----------"));
		if ((itemCombo.getValue() == null && fromLocationCombo.getValue() != null)
				|| (itemCombo.getValue() != null
						&& toLong(itemCombo.getValue() + "") == 0 && fromLocationCombo
						.getValue() != null)) {

			list.addAll(stockTransferDao
					.getItemStockListByLocationWIse(toLong(fromLocationCombo
							.getValue() + "")));

		} else
			if (fromLocationCombo.getValue() != null
				&& itemCombo.getValue() != null) {

			list.addAll(stockTransferDao.getItemStockListByLocationAndItemWIse(
					toLong(fromLocationCombo.getValue() + ""),
					toLong(itemCombo.getValue() + "")));

		}

		SCollectionContainer bic = SCollectionContainer.setList(list, "id");
		stockIDSelect.setContainerDataSource(bic);
		stockIDSelect.setItemCaptionPropertyId("stock_details");
		/*
		 * if (id != 0) { requestNoComboField.setValue(id); } else {
		 * requestNoComboField.setValue(null); }
		 */

		stockIDSelect.setInputPrompt("--------- Select -----------");
	}

	private void loadItemComboField() {

		List<LocationModel> list = new ArrayList<LocationModel>();
		list.add(0, new LocationModel(0, "---------- All ----------"));
		if (fromOfficeCombo.getValue() != null) {

			try {
				list.addAll(itemDao.getAllItemsWithCode(toLong(fromOfficeCombo
						.getValue() + "")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SCollectionContainer bic = SCollectionContainer.setList(list, "id");
		itemCombo.setContainerDataSource(bic);
		itemCombo.setItemCaptionPropertyId("name");
		/*
		 * if (id != 0) { requestNoComboField.setValue(id); } else {
		 * requestNoComboField.setValue(null); }
		 */

		itemCombo.setInputPrompt("---------- All -----------");

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

			/*
			 * if (fromOfficeCombo.getValue() == null ||
			 * fromOfficeCombo.getValue().equals("")) {
			 * setRequiredError(fromOfficeCombo,
			 * getPropertyName("invalid_selection"), true);
			 * fromOfficeCombo.focus(); ret = false; } else
			 * setRequiredError(fromOfficeCombo, null, false);
			 */

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

			if (stockIDSelect.getValue() == null
					|| stockIDSelect.getValue().equals("")) {
				setRequiredError(stockIDSelect,
						getPropertyName("invalid_selection"), true);
				stockIDSelect.focus();
				ret = false;
			} else
				setRequiredError(stockIDSelect, null, false);

		} catch (Exception e) {
			// TODO: handle exception
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
				/*
				 * Collection<Integer> selectedItems = (Collection)
				 * table.getValue(); Iterator it1 = selectedItems.iterator();
				 * while (it1.hasNext()) { table.removeItem(it1.next()); }
				 */
				Iterator it = table.getItemIds().iterator();
				Item item = null;
				int id;
				while (it.hasNext()) {
					id = (Integer) it.next();
					item = table.getItem(id);
					if (table.isSelected(id)) {
						table.removeItem(id);
					}

				}
				clearFields();
				visibleAddupdateStockTransfer(true, false);
				Item newitem;
				int SN = 0;
				it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = table.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

				calculateTotals();
			}
			stockIDSelect.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadStockTransfer(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new StockTransferModel(0, "----Create New-----"));
			list.addAll(stockTransferDao
					.getAllStockTransferNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			stockTransferNumberComboField.setContainerDataSource(bic);
			stockTransferNumberComboField.setItemCaptionPropertyId("comments");

			 stockTransferNumberComboField.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * public void reloadStock() { try {
	 * 
	 * SCollectionContainer bic = SCollectionContainer.setList(
	 * stockTransferDao.getItemStockList(getOfficeID()), "id");
	 * stockIDSelect.setContainerDataSource(bic);
	 * stockIDSelect.setItemCaptionPropertyId("stock_details");
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

	public Boolean isValid() {

		boolean valid = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			stockIDSelect.focus();
			valid = false;
		} else {
			setRequiredError(table, null, false);
		}

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_data"), true);
			dateField.focus();
			valid = false;
		} else {
			setRequiredError(dateField, null, false);
		}

		if (fromOfficeCombo.getValue() == null
				|| fromOfficeCombo.getValue().equals("")) {
			setRequiredError(fromOfficeCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(fromOfficeCombo, null, false);
		}
		if (fromLocationCombo.getValue() == null
				|| fromLocationCombo.getValue().equals("")) {
			setRequiredError(fromLocationCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(fromLocationCombo, null, false);
		}
		if (toOfficeCombo.getValue() == null
				|| toOfficeCombo.getValue().equals("")) {
			setRequiredError(toOfficeCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(toOfficeCombo, null, false);
		}

		if (toLocationCombo.getValue() == null
				|| toLocationCombo.getValue().equals("")) {
			setRequiredError(toLocationCombo,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(toLocationCombo, null, false);
		}

		if (valid) {
			if(fromOfficeCombo.getValue().toString()
					.equals(toOfficeCombo.getValue().toString())){
			if (fromLocationCombo.getValue().toString()
					.equals(toLocationCombo.getValue().toString())) {
				setRequiredError(toLocationCombo,
						getPropertyName("invalid_selection"), true);
				valid = false;
			} else {
				setRequiredError(toLocationCombo, null, false);
			}
			}
		}

		if (valid) {
			calculateTotals();
		}

		// TODO Auto-generated method stub
		return valid;
	}

	public void removeAllErrors() {
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);

		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (stockIDSelect.getComponentError() != null)
			setRequiredError(stockIDSelect, null, false);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
