package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.bean.ProductionBean;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ManufacturingMapDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Mar 25, 2014
 */

public class ItemManufacturingMapUI extends SparkLogic {

	private static final long serialVersionUID = -7978556633382357689L;

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";

	ManufacturingMapDao daoObj;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable childTable;

	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	SButton saveProduction;
	SButton deleteStockTransfer;

	CommonMethodsDao comDao;

	ItemDao itemDao;

	STextField subconvertionQtyTextField;
	STextField subconvertedQtyTextField;

	STextField masterQtyField;
//	STextField masterconvertionQtyTextField;
//	STextField masterconvertedQtyTextField;

	SComboField itemName;

	SComboField subItemSelect;
	SNativeSelect subItemUnitSelect;
	STextField subItmQtyTextField;
	SButton addSubButton;
	SButton updateSubButton;
	SGridLayout addingSubGrid;

	UnitDao unitDao = new UnitDao();

	private SComboField unitComboField;
	
	private SDialogBox newItemWindow;
	private ItemPanel itemPanel;
	private SButton newItemButton;
	
	private SDialogBox newSubItemWindow;
	private ItemPanel subItemPanel;
	private SButton newSubItemButton;

	@SuppressWarnings("deprecation")
	public ItemManufacturingMapUI() {

		setId("Transfer");
		setSize(760, 500);

		comDao = new CommonMethodsDao();

		daoObj = new ManufacturingMapDao();
		itemDao = new ItemDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingSubGrid = new SGridLayout();
		addingSubGrid.setSizeFull();
		addingSubGrid.setColumns(9);
		addingSubGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(10);
		masterDetailsGrid.setRows(1);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);

		form.setSizeFull();

		try {

			itemName = new SComboField(null, 200,
					itemDao.getAllManufacturingItems(getOfficeID()), "id",
					"name", true, "Select");

			subconvertionQtyTextField = new STextField(
					getPropertyName("conv_qty"), 40);
			subconvertionQtyTextField.setStyleName("textfield_align_right");
			subconvertionQtyTextField
					.setDescription(getPropertyName("value_basic_to_selected"));
			subconvertionQtyTextField.setVisible(false);

			subconvertedQtyTextField = new STextField(
					getPropertyName("conrted_qty"), 60);
			subconvertedQtyTextField.setStyleName("textfield_align_right");
			subconvertedQtyTextField
					.setDescription(getPropertyName("conrted_qty_basic_unit"));
			subconvertedQtyTextField.setReadOnly(true);
			subconvertedQtyTextField.setVisible(false);
			//----------------------------------------
//			masterconvertionQtyTextField = new STextField(
//					getPropertyName("conv_qty"), 40);
//			masterconvertionQtyTextField.setStyleName("textfield_align_right");
//			masterconvertionQtyTextField
//			.setDescription(getPropertyName("value_basic_to_selected"));
//			masterconvertionQtyTextField.setVisible(false);
//			
//			masterconvertedQtyTextField = new STextField(
//					getPropertyName("conrted_qty"), 60);
//			masterconvertedQtyTextField.setStyleName("textfield_align_right");
//			masterconvertedQtyTextField
//			.setDescription(getPropertyName("conrted_qty_basic_unit"));
//			masterconvertedQtyTextField.setReadOnly(true);
//			masterconvertedQtyTextField.setVisible(false);

			masterQtyField = new STextField(null, 80);
			masterQtyField.setValue("1");
			masterQtyField.setStyleName("textfield_align_right");

			unitComboField = new SComboField(null, 70);
			unitComboField.setReadOnly(true);
			
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
			hrz1.addComponent(itemName);
			hrz1.addComponent(newItemButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("item_name")), 1, 0);
			masterDetailsGrid.addComponent(hrz1, 2, 0);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("quantity")), 3, 0);
			masterDetailsGrid.addComponent(masterQtyField, 4, 0);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("Unit")),
					6, 0);
			masterDetailsGrid.addComponent(unitComboField, 7, 0);
			
//			masterDetailsGrid.addComponent(masterconvertionQtyTextField, 8, 0);
//			masterDetailsGrid.addComponent(masterconvertedQtyTextField, 9, 0);

			masterDetailsGrid.setSpacing(true);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 0.5f);
			masterDetailsGrid.setColumnExpandRatio(6, 2);
			masterDetailsGrid.setColumnExpandRatio(7, 2);
			masterDetailsGrid.setColumnExpandRatio(8, 2);
			masterDetailsGrid.setColumnExpandRatio(9, 2);

			masterDetailsGrid.setStyleName("master_border");

			subItemSelect = new SComboField(getPropertyName("item"), 250, null,
					"id", "name", true, "Select");
			reloadStock();
			subItemUnitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			subItmQtyTextField = new STextField(getPropertyName("qty"), 60);
			subItmQtyTextField.setStyleName("textfield_align_right");
			addSubButton = new SButton(null, getPropertyName("add_item"));
			addSubButton.setStyleName("addItemBtnStyle");
			updateSubButton = new SButton(null, getPropertyName("update"));
			updateSubButton.setStyleName("updateItemBtnStyle");
			updateSubButton.setVisible(false);
			
			newSubItemWindow = new SDialogBox("Add Item", 500, 600);
			newSubItemWindow.center();
			newSubItemWindow.setResizable(false);
			newSubItemWindow.setModal(true);
			newSubItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			subItemPanel = new ItemPanel();
			subItemPanel.getAffectType().setValue(
					SConstants.affect_type.PURCHASE_ONLY);
			newSubItemWindow.addComponent(subItemPanel);

			newSubItemButton = new SButton(null, "Add New Item");
			newSubItemButton.setStyleName("addNewBtnStyle");

			SHorizontalLayout hrz2 = new SHorizontalLayout();
			hrz2.addComponent(subItemSelect);
			hrz2.addComponent(newSubItemButton);

			hrz2.setComponentAlignment(newSubItemButton, Alignment.BOTTOM_LEFT);

			addingSubGrid.addComponent(hrz2);
			addingSubGrid.addComponent(subItmQtyTextField);
			addingSubGrid.addComponent(subItemUnitSelect);
			addingSubGrid.addComponent(subconvertionQtyTextField);
			addingSubGrid.addComponent(subconvertedQtyTextField);
			addingSubGrid.addComponent(addSubButton);
			addingSubGrid.addComponent(updateSubButton);

			form.setStyleName("po_style");

			childTable = new STable(null, 700, 200);
			childTable.setWidth("700");
			childTable.setHeight("200");
			childTable.setMultiSelect(true);

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
					TBC_UNIT_ID, null, Align.CENTER);
			childTable.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			childTable.addContainerProperty(TBC_QTY_IN_BASIC_UNIT,
					Double.class, null, getPropertyName("qty_basic_unit"),
					null, Align.CENTER);

			childTable.setColumnExpandRatio(TBC_SN, (float) 0.4);
			childTable.setColumnExpandRatio(TBC_ITEM_ID, 1);
			childTable.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			childTable.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			childTable.setColumnExpandRatio(TBC_QTY, 1);
			childTable.setColumnExpandRatio(TBC_UNIT_ID, 1);
			childTable.setColumnExpandRatio(TBC_UNIT, 1);

			childTable.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY, TBC_QTY_IN_BASIC_UNIT });

			// childTable.setSizeFull();
			childTable.setSelectable(true);
			childTable.setNullSelectionAllowed(true);

			saveProduction = new SButton(getPropertyName("Save"), 70);
			saveProduction.setStyleName("savebtnStyle");
			saveProduction.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			deleteStockTransfer = new SButton(getPropertyName("Delete"), 78);
			deleteStockTransfer.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteStockTransfer.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveProduction);
			mainButtonLayout.addComponent(deleteStockTransfer);
			deleteStockTransfer.setVisible(false);

			buttonsGrid.setColumnExpandRatio(0, 1);
			buttonsGrid.setColumnExpandRatio(1, 5);

			buttonsGrid.addComponent(mainButtonLayout, 1, 1);
			mainButtonLayout.setSpacing(true);
			buttonsGrid.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			form.addComponent(masterDetailsGrid);
			form.addComponent(childTable);
			form.addComponent(addingSubGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);
			
			newItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
					itemPanel.loadOptions((long)0);
					getUI().getCurrent().addWindow(newItemWindow);
					newItemWindow.setCaption("Add New Item");
				}
			});
			
			newItemWindow.addCloseListener(new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					reloadItemStocks();
				}
			});
			
			newSubItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					subItemPanel.reloadGroup();
					subItemPanel.loadOptions((long)0);
					getUI().getCurrent().addWindow(newSubItemWindow);
					newSubItemWindow.setCaption("Add New Item");
				}
			});
			
			newSubItemWindow.addCloseListener(new CloseListener() {
				
				@Override
				public void windowClose(CloseEvent e) {
					reloadStock();
				}
			});

			saveProduction.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isValid()) {

							List list = new ArrayList();

							ManufacturingMapModel objModel;

							Iterator it2;
							ProductionBean bean;
							Object parent;
							Set set;
							Iterator it1 = childTable.getItemIds().iterator();
							while (it1.hasNext()) {
								parent = it1.next();
								Item item = childTable.getItem(parent);
								objModel = new ManufacturingMapModel();

								objModel.setItem(new ItemModel((Long) itemName
										.getValue()));
								objModel.setSubItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								objModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								objModel.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								objModel.setQty_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
										.getValue());
								objModel.setMaster_quantity(toDouble(masterQtyField
										.getValue().toString()));
								list.add(objModel);
							}

							daoObj.save(list, (Long) itemName.getValue());

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							reloadStock();

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			itemName.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						removeAllErrors();
						
						if( itemName.getValue()!=null&& !itemName.getValue().equals("")){

						deleteStockTransfer.setVisible(true);
						loadTableData();

						ItemModel itm = itemDao.getItem((Long) itemName
								.getValue());

						SCollectionContainer bic = SCollectionContainer
								.setList(comDao.getAllItemUnitDetails(itm
										.getId()), "id");

						unitComboField.setReadOnly(false);
						unitComboField.setContainerDataSource(bic);
						unitComboField.setItemCaptionPropertyId("symbol");
						unitComboField.setNewValue(itm.getUnit().getId());
						unitComboField.setReadOnly(true);
						subItmQtyTextField.setValue("0.0");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			deleteStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (itemName.getValue() != null
							&& !itemName.getValue().toString().equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete(toLong(itemName
														.getValue().toString()));
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												reloadStock();
												loadTableData();

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
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

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			updateSubButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isSubAddingValid()) {
							ItemModel stk = itemDao
									.getItem((Long) subItemSelect.getValue());

							Collection selectedItems = (Collection) childTable
									.getValue();

							double qty = toDouble(subItmQtyTextField.getValue()), totalSameStockQty = 0, totalUsedQty = 0;

							Item selectedItem = childTable
									.getItem(selectedItems.iterator().next());

							Item item;
							Iterator itr2 = childTable.getItemIds().iterator();
							while (itr2.hasNext()) {
								item = childTable.getItem(itr2.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(subItemSelect.getValue()
												.toString())) {
									totalSameStockQty += (Double) item
											.getItemProperty(TBC_QTY)
											.getValue();
								}
							}
							totalSameStockQty += qty;

							item = childTable.getItem(selectedItems.iterator()
									.next());

							qty = toDouble(subItmQtyTextField.getValue());

							UnitModel objUnit = unitDao
									.getUnit((Long) subItemUnitSelect
											.getValue());

							item.getItemProperty(TBC_ITEM_ID).setValue(
									stk.getId());
							item.getItemProperty(TBC_ITEM_CODE).setValue(
									stk.getItem_code());
							item.getItemProperty(TBC_ITEM_NAME).setValue(
									stk.getName());
							item.getItemProperty(TBC_QTY).setValue(qty);
							item.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
									.setValue(
											toDouble(subconvertedQtyTextField
													.getValue()));

							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());

							childTable.setVisibleColumns(new String[] { TBC_SN,
									TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
									TBC_QTY });

							subItemSelect.setValue(null);
							subItmQtyTextField.setValue("0.0");

							subItemSelect.focus();

							childTable.setValue(null);
							visibleAddupdateStockTransfer(true, false);

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

			childTable.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {

					Collection selectedItems = null;

					if (childTable.getValue() != null) {
						selectedItems = (Collection) childTable.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {
						Object obj = selectedItems.iterator().next();

						Item item = childTable.getItem(obj);

						subItmQtyTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());

						subItemSelect.setValue(item
								.getItemProperty(TBC_ITEM_ID).getValue());

						subItemUnitSelect.setValue(item.getItemProperty(
								TBC_UNIT_ID).getValue());

						subconvertionQtyTextField
								.setNewValue(asString(toDouble(item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
										.getValue().toString())
										/ toDouble(item
												.getItemProperty(TBC_QTY)
												.getValue().toString())));

						subconvertedQtyTextField.setNewValue(item
								.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
								.getValue().toString());

						visibleAddupdateStockTransfer(false, true);

						subItemSelect.focus();

					} else {
						subItemSelect.setValue(null);
						subItmQtyTextField.setValue("0.0");

						visibleAddupdateStockTransfer(true, false);

						subItemSelect.focus();
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
								Notification.show(getPropertyName("error"),
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

			childTable.addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addSubButton.isVisible())
						addSubButton.click();
					else
						updateSubButton.click();
				}
			});

			childTable.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteSubItem();
				}
			});

			childTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					childTable.setValue(null);
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
			Iterator it = childTable.getItemIds().iterator();
			while (it.hasNext()) {
				item = childTable.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
			}

			childTable.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));

		} catch (Exception e) {
		}
	}

	private void loadTableData() throws Exception {
		if (itemName.getValue() != null) {

			List poObj = daoObj.getProductionDetails(toLong(itemName.getValue()
					.toString()));

			childTable.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_ID,
					TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
					TBC_UNIT, TBC_QTY_IN_BASIC_UNIT });

			childTable.removeAllItems();

			int parent_id = 1;
			Iterator it2;
			Iterator it = poObj.iterator();
			ManufacturingMapModel invObj = null;
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
			if (invObj != null)
				masterQtyField.setValue(invObj.getMaster_quantity() + "");
			else
				masterQtyField.setValue("1");

		} else {
			childTable.removeAllItems();

			deleteStockTransfer.setVisible(false);
			masterQtyField.setValue("1");
		}
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
								getPropertyName("qty_greater_zero"), true);
						subItmQtyTextField.focus();
						ret = false;
					} else
						setRequiredError(subItmQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(subItmQtyTextField,
							getPropertyName("invalid_data"), true);
					subItmQtyTextField.focus();
					ret = false;
				}
			}

			if (subItemSelect.getValue() == null
					|| subItemSelect.getValue().equals("")) {
				setRequiredError(subItemSelect,
						getPropertyName("invalid_selection"), true);
				subItemSelect.focus();
				ret = false;
			} else
				setRequiredError(subItemSelect, null, false);

			if (subItemSelect.getValue().equals(itemName.getValue())) {
				setRequiredError(subItemSelect,
						getPropertyName("items_cannot_same"), true);
				subItemSelect.focus();
				ret = false;
			} else
				setRequiredError(subItemSelect, null, false);

		} catch (Exception e) {
		}

		return ret;

	}

	public void visibleAddupdateStockTransfer(boolean AddVisible,
			boolean UpdateVisible) {
		addSubButton.setVisible(AddVisible);
		updateSubButton.setVisible(UpdateVisible);
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
	
	protected void reloadItemStocks() {
		try {
			List list = new ArrayList();
			list.addAll(itemDao.getAllAffectAllItems(getOfficeID()));
			list.addAll(itemDao.getAllManufacturingItems(getOfficeID()));
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			itemName.setContainerDataSource(bic);
			itemName.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				itemName.setValue((Long) getHttpSession().getAttribute(
						"saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reloadStock() {
		try {

			List list = new ArrayList();
			list.addAll(itemDao.getAllAffectAllItems(getOfficeID()));
			list.addAll(itemDao.getAllManufacturingItems(getOfficeID()));
			list.addAll(itemDao.getAllPurchaseOnlyItems(getOfficeID()));

			Collections.sort(list, new Comparator<ItemModel>() {
				@Override
				public int compare(final ItemModel object1,
						final ItemModel object2) {
					int result = object1.getName().compareTo(object2.getName());
					return result;
				}
			});
			SCollectionContainer bic1 = SCollectionContainer
					.setList(list, "id");
			subItemSelect.setContainerDataSource(bic1);
			subItemSelect.setItemCaptionPropertyId("name");
			
			if (getHttpSession().getAttribute("saved_id") != null) {
				subItemSelect.setValue((Long) getHttpSession().getAttribute(
						"saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (childTable.getItemIds().size() <= 0) {
			setRequiredError(childTable, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(childTable, null, false);

		if (itemName.getValue() == null || itemName.getValue().equals("")) {
			setRequiredError(itemName, getPropertyName("invalid_selection"),
					true);
			ret = false;
		} else
			setRequiredError(itemName, null, false);

		return ret;
	}

	public void removeAllErrors() {
		if (childTable.getComponentError() != null)
			setRequiredError(childTable, null, false);
		if (itemName.getComponentError() != null)
			setRequiredError(itemName, null, false);
		if (subItmQtyTextField.getComponentError() != null)
			setRequiredError(subItmQtyTextField, null, false);
		if (subItemSelect.getComponentError() != null)
			setRequiredError(subItemSelect, null, false);
	}

	public Boolean getHelp() {
		return null;
	}

	@Override
	public SPanel getGUI() {
		return null;
	}

}
