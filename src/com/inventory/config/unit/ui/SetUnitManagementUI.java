package com.inventory.config.unit.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.SalesTypeModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.dao.UnitManagementDao;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.transaction.biz.FinTransaction;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Window;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 10, 2013
 */
public class SetUnitManagementUI extends SContainerPanel {

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TSJ_UNIT_ID = "Unit ID";
	static String TSJ_UNIT_NAME = "Alternate Unit";
	static String TSJ_CONVERSION_RATE = "Conversion Quantity";
	static String TSJ_SALES_TYPE = "Sales Type";
	static String TSJ_SALES_TYPE_ID = "Sales Type ID";

	static String TSJ_PRICE = "Item Rate";

	STable accountPaymentEntryTable;
	UnitManagementDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout accountPaymentAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField itemIDList;

	SNativeSelect salesTypeSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;

	SComboField unitList;

	STextField conversionRateextField;

	STextField itemPriceTextField;

	SButton saveButton;

	private String[] allHeaders;
	private String[] requiredHeaders;

	STextField basicUnit;

	SRadioButton saleOrPurchaseRadio;

	LedgerDao ledgerDao;
	SalesTypeDao stDao;
	UnitDao untDao;

	ItemDao itmDao;

	public SetUnitManagementUI(long itemId, long salesTypeID, boolean isSale) {
		// TODO Auto-generated method stub

		stDao = new SalesTypeDao();
		untDao = new UnitDao();

		allHeaders = new String[] { TSR_SN, TSJ_UNIT_ID, TSJ_UNIT_NAME,
				TSJ_CONVERSION_RATE, TSJ_SALES_TYPE, TSJ_SALES_TYPE_ID,
				TSJ_PRICE };

		requiredHeaders = new String[] { TSR_SN, TSJ_UNIT_NAME,
				TSJ_CONVERSION_RATE, TSJ_SALES_TYPE, TSJ_PRICE };

		daoObj = new UnitManagementDao();
		ledgerDao = new LedgerDao();
		itmDao = new ItemDao();

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton(getPropertyName("update"));

		saveButton = new SButton(getPropertyName("Save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		// updateButton=new SButton("Update", 80);
		// updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		// updateButton.setStyleName("updatebtnStyle");

		// deleteButton=new SButton("Delete", 78);
		// deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		// deleteButton.setStyleName("deletebtnStyle");

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveButton);
		// mainButtonLayout.addComponent(updateButton);
		// mainButtonLayout.addComponent(deleteButton);

		// updateButton.setVisible(false);
		// deleteButton.setVisible(false);

		basicUnit = new STextField();

		itemPriceTextField = new STextField(getPropertyName("item_price"));
		itemPriceTextField.setStyleName("textfield_align_right");

		basicUnit.setReadOnly(true);

		panel = new SPanel();
		panel.setSizeFull();

		try {

			salesTypeSelect = new SNativeSelect(getPropertyName("sales_type"),
					140, stDao.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");

			saleOrPurchaseRadio = new SRadioButton(null, 170,
					SConstants.saleOrPurchaseList, "intKey", "value");
			saleOrPurchaseRadio.setStyleName("radio_horizontal");
			updateItemButton.setVisible(false);

			accountPaymentEntryTable = new STable(null, 800, 200);

			List list = itmDao
					.getAllActiveItemsWithAppendingItemCode(getOfficeID());

			itemIDList = new SComboField(null, 200, list, "id", "name", true,
					"Select");

			unitList = new SComboField(getPropertyName("unit"), 200,
					untDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol", true, "Select");

			accountPaymentAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			accountPaymentEntryTable.setSizeFull();
			accountPaymentEntryTable.setSelectable(true);
			accountPaymentEntryTable.setMultiSelect(true);

			accountPaymentEntryTable.setWidth("860px");
			accountPaymentEntryTable.setHeight("180px");

			accountPaymentEntryTable.addContainerProperty(TSR_SN,
					Integer.class, null, "#", null, Align.CENTER);
			accountPaymentEntryTable.addContainerProperty(TSJ_UNIT_ID,
					Long.class, null, TSJ_UNIT_ID, null, Align.CENTER);
			accountPaymentEntryTable.addContainerProperty(TSJ_UNIT_NAME,
					String.class, null, getPropertyName("alt_unit"), null,
					Align.LEFT);
			accountPaymentEntryTable.addContainerProperty(TSJ_PRICE,
					Double.class, null, getPropertyName("item_rate"), null,
					Align.CENTER);
			accountPaymentEntryTable.addContainerProperty(TSJ_CONVERSION_RATE,
					Double.class, null, getPropertyName("conv_qty"), null,
					Align.CENTER);
			accountPaymentEntryTable.addContainerProperty(TSJ_SALES_TYPE,
					String.class, null, getPropertyName("sales_type"), null,
					Align.LEFT);
			accountPaymentEntryTable.addContainerProperty(TSJ_SALES_TYPE_ID,
					Long.class, null, TSJ_SALES_TYPE_ID, null, Align.CENTER);

			accountPaymentEntryTable.setColumnExpandRatio(TSR_SN, (float) .5);
			accountPaymentEntryTable.setColumnExpandRatio(TSJ_UNIT_NAME, 2);
			accountPaymentEntryTable.setColumnExpandRatio(TSJ_CONVERSION_RATE,
					(float) 1.5);

			accountPaymentAddGrid.setColumns(8);
			accountPaymentAddGrid.setRows(2);

			accountPaymentAddGrid.setStyleName("journal_adding_grid");

			conversionRateextField = new STextField(
					getPropertyName("conv_qty"), 80);

			accountPaymentAddGrid.addComponent(salesTypeSelect);
			accountPaymentAddGrid.addComponent(unitList);
			accountPaymentAddGrid.addComponent(conversionRateextField);
			accountPaymentAddGrid.addComponent(itemPriceTextField);

			accountPaymentAddGrid.addComponent(addItemButton);
			accountPaymentAddGrid.addComponent(updateItemButton);

			conversionRateextField.setStyleName("textfield_align_right");

			accountPaymentAddGrid.setComponentAlignment(itemPriceTextField,
					Alignment.MIDDLE_RIGHT);

			accountPaymentAddGrid.setComponentAlignment(addItemButton,
					Alignment.BOTTOM_RIGHT);
			accountPaymentAddGrid.setComponentAlignment(updateItemButton,
					Alignment.BOTTOM_RIGHT);

			accountPaymentAddGrid.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("item")),
					1, 0);
			masterDetailsGrid.addComponent(itemIDList, 2, 0);
			// masterDetailsGrid.addComponent(new SLabel("Date :"), 6, 0);

			// masterDetailsGrid.addComponent(new SLabel("Pay To :"), 3, 1);
			// masterDetailsGrid.addComponent(, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("type")),
					1, 1);
			masterDetailsGrid.addComponent(saleOrPurchaseRadio, 2, 1);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("basic_unit")), 3, 0);
			masterDetailsGrid.addComponent(basicUnit, 4, 0);
			masterDetailsGrid.setSpacing(true);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			accountPaymentAddGrid.setSpacing(true);

			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(accountPaymentEntryTable);

			stkrkVLay.addComponent(accountPaymentAddGrid);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(accountPaymentAddGrid,
					Alignment.BOTTOM_CENTER);

			accountPaymentEntryTable.setVisibleColumns(requiredHeaders);

			// accountPaymentEntryTable.setFooterVisible(true);
			// accountPaymentEntryTable.setColumnFooter(TSJ_CONVERSION_RATE,
			// asString(roundNumber(0)));
			// accountPaymentEntryTable.setColumnFooter(TSJ_UNIT_NAME, "Total");

			panel.setContent(stkrkVLay);

			saleOrPurchaseRadio.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						List lst = new ArrayList();
						if ((Integer) saleOrPurchaseRadio.getValue() == 1) {
							salesTypeSelect.setVisible(true);
							lst = stDao
									.getAllActiveSalesTypeNames(getOfficeID());
							SCollectionContainer bic = SCollectionContainer
									.setList(lst, "id");
							salesTypeSelect.setContainerDataSource(bic);
							salesTypeSelect.setItemCaptionPropertyId("name");
						} else {
							salesTypeSelect.setVisible(false);
							lst.add(new SalesTypeModel(0, "Purchase"));
							SCollectionContainer bic = SCollectionContainer
									.setList(lst, "id");
							salesTypeSelect.setContainerDataSource(bic);
							salesTypeSelect.setItemCaptionPropertyId("name");

							salesTypeSelect.setValue((long) 0);
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});

			salesTypeSelect.setValue(0);

			itemIDList.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						accountPaymentEntryTable.setVisibleColumns(allHeaders);

						accountPaymentEntryTable.removeAllItems();
						if (itemIDList.getValue() != null
								&& !itemIDList.getValue().toString()
										.equals("0")) {

							ItemModel item = itmDao.getItem((Long) itemIDList
									.getValue());

							basicUnit
									.setNewValue(item.getUnit().getName()
											+ " ( "
											+ item.getUnit().getSymbol() + " )");

							List list = daoObj.getAllItemUnitDetails(item
									.getId());

							int ct = 0;
							String saleTypeName = "";
							ItemUnitMangementModel obj;
							Iterator it = list.iterator();
							while (it.hasNext()) {

								ct++;

								obj = (ItemUnitMangementModel) it.next();
								if (obj.getSales_type() != 0)
									saleTypeName = stDao.getSalesType(
											obj.getSales_type()).getName();

								accountPaymentEntryTable.addItem(
										new Object[] {
												ct,
												obj.getAlternateUnit(),
												untDao.getUnit(
														obj.getAlternateUnit())
														.getSymbol(),
												obj.getConvertion_rate(),
												saleTypeName,
												obj.getSales_type(),
												obj.getItem_price() }, ct);

							}

						} else {
							basicUnit.setValue("");
							unitList.setValue(null);

						}

						accountPaymentEntryTable
								.setVisibleColumns(requiredHeaders);

					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});

			accountPaymentEntryTable
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							Collection selectedItems = null;

							if (accountPaymentEntryTable.getValue() != null) {
								selectedItems = (Collection) accountPaymentEntryTable
										.getValue();
							}

							if (selectedItems != null
									&& selectedItems.size() == 1) {

								Item item = accountPaymentEntryTable
										.getItem(selectedItems.iterator()
												.next());

								unitList.setValue(item.getItemProperty(
										TSJ_UNIT_ID).getValue());
								conversionRateextField.setValue(asString(item
										.getItemProperty(TSJ_CONVERSION_RATE)
										.getValue()));

								itemPriceTextField
										.setValue(asString(item
												.getItemProperty(TSJ_PRICE)
												.getValue()));

								if ((Integer) saleOrPurchaseRadio.getValue() == 1) {
									salesTypeSelect.setValue(item
											.getItemProperty(TSJ_SALES_TYPE_ID)
											.getValue());
								}

								updateItemButton.setVisible(true);
								addItemButton.setVisible(false);

								unitList.focus();
							} else {
								updateItemButton.setVisible(false);
								addItemButton.setVisible(true);

								unitList.setValue(null);
								conversionRateextField.setValue("");
								unitList.focus();
								itemPriceTextField.setValue("");
							}
						}
					});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						accountPaymentEntryTable.setVisibleColumns(allHeaders);

						setRequiredError(accountPaymentEntryTable, null, false);
						if (isAddingValid()) {

							Collection selectedItems = null;
							if (accountPaymentEntryTable.getValue() != null) {
								selectedItems = (Collection) accountPaymentEntryTable
										.getValue();
							}

							int sel_id = (Integer) selectedItems.iterator()
									.next();

							int id;
							Item item;
							boolean exist = false;
							Iterator it1 = accountPaymentEntryTable
									.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								if (id != sel_id) {

									item = accountPaymentEntryTable.getItem(id);

									if (item.getItemProperty(TSJ_UNIT_ID)
											.getValue()
											.toString()
											.equals(unitList.getValue()
													.toString())) {

										// if(salesTypeSelect.isVisible()) {
										if (item.getItemProperty(
												TSJ_SALES_TYPE_ID)
												.getValue()
												.toString()
												.equals(salesTypeSelect
														.getValue().toString())) {
											exist = true;
										}
										// }
										// else
										// exist=true;
									}

								}

							}

							if (!exist) {

								item = accountPaymentEntryTable.getItem(sel_id);

								item.getItemProperty(TSJ_UNIT_ID).setValue(
										unitList.getValue());
								item.getItemProperty(TSJ_UNIT_NAME).setValue(
										unitList.getItemCaption(unitList
												.getValue()));

								double amt = toDouble(conversionRateextField
										.getValue());
								double price = toDouble(itemPriceTextField
										.getValue());
								item.getItemProperty(TSJ_CONVERSION_RATE)
										.setValue(amt);
								item.getItemProperty(TSJ_PRICE).setValue(price);

								if ((Integer) saleOrPurchaseRadio.getValue() == 1) {
									item.getItemProperty(TSJ_SALES_TYPE_ID)
											.setValue(
													(Long) salesTypeSelect
															.getValue());
									item.getItemProperty(TSJ_SALES_TYPE)
											.setValue(
													salesTypeSelect
															.getItemCaption(salesTypeSelect
																	.getValue()));
								}

								updateItemButton.setVisible(false);
								addItemButton.setVisible(true);

								accountPaymentEntryTable.setValue(null);

							} else {
								setRequiredError(unitList,
										getPropertyName("unit_exist"), true);
								conversionRateextField.focus();
							}

						}

						accountPaymentEntryTable
								.setVisibleColumns(requiredHeaders);

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						accountPaymentEntryTable.setVisibleColumns(allHeaders);

						setRequiredError(accountPaymentEntryTable, null, false);
						if (isAddingValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;

							Item item;
							int id = 0, ct = accountPaymentEntryTable
									.getItemIds().size();
							Iterator it1 = accountPaymentEntryTable
									.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								item = accountPaymentEntryTable.getItem(id);
								if (item.getItemProperty(TSJ_UNIT_ID)
										.getValue().toString()
										.equals(unitList.getValue().toString())) {

									// if(salesTypeSelect.isVisible()) {
									if (item.getItemProperty(TSJ_SALES_TYPE_ID)
											.getValue()
											.toString()
											.equals(salesTypeSelect.getValue()
													.toString())) {
										exist = true;
									}
									// }
									// else
									// exist=true;
								}

							}

							if (!exist) {

								id++;
								ct++;

								String salTyp = "";
								long salTypeId = 0;
								if ((Integer) saleOrPurchaseRadio.getValue() == 1) {
									salTyp = salesTypeSelect
											.getItemCaption(salesTypeSelect
													.getValue());
									salTypeId = (Long) salesTypeSelect
											.getValue();
								}

								double amt = toDouble(conversionRateextField
										.getValue());
								double price = toDouble(itemPriceTextField
										.getValue());

								accountPaymentEntryTable.addItem(
										new Object[] {
												ct,
												(Long) unitList.getValue(),
												unitList.getItemCaption(unitList
														.getValue()), amt,
												salTyp, salTypeId, price }, id);

								unitList.setValue(null);
								conversionRateextField.setValue("");
								itemPriceTextField.setValue("");
								unitList.focus();

							} else {
								setRequiredError(unitList,
										getPropertyName("unit_exist"), true);
								conversionRateextField.focus();
							}
						}

						accountPaymentEntryTable
								.setVisibleColumns(requiredHeaders);

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							List list = new ArrayList();
							ItemModel itm = itmDao.getItem((Long) itemIDList
									.getValue());

							Item item;
							ItemUnitMangementModel objMdl;
							FinTransaction tran = new FinTransaction();
							Iterator it = accountPaymentEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								item = accountPaymentEntryTable.getItem(it
										.next());

								objMdl = new ItemUnitMangementModel();

								objMdl.setAlternateUnit((Long) item
										.getItemProperty(TSJ_UNIT_ID)
										.getValue());
								objMdl.setBasicUnit(itm.getUnit().getId());
								objMdl.setConvertion_rate((Double) item
										.getItemProperty(TSJ_CONVERSION_RATE)
										.getValue());
								objMdl.setItem(itm);
								objMdl.setSales_type((Long) item
										.getItemProperty(TSJ_SALES_TYPE_ID)
										.getValue());
								objMdl.setStatus(1);

								objMdl.setItem_price((Double) item
										.getItemProperty(TSJ_PRICE).getValue());

								list.add(objMdl);
							}

							daoObj.save(list, (Long) itemIDList.getValue());

							Iterator itt = getUI().getWindows().iterator();
							itt.next();
							getUI().removeWindow((Window) itt.next());

							itemIDList.setValue(null);
							itemIDList.setValue(itm.getId());

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			accountPaymentEntryTable.addShortcutListener(new ShortcutListener(
					"Submit Item", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			final Action actionDeleteStock = new Action("Delete");

			accountPaymentEntryTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

			accountPaymentEntryTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					accountPaymentEntryTable.setValue(null);
				}
			});

			if (isSale)
				saleOrPurchaseRadio.setValue(1);
			else
				saleOrPurchaseRadio.setValue(2);

			itemIDList.setValue(itemId);
			salesTypeSelect.setValue(salesTypeID);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setContent(panel);
	}

	public void deleteItem() {
		try {

			if (accountPaymentEntryTable.getValue() != null) {

				Collection selectedItems = (Collection) accountPaymentEntryTable
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					accountPaymentEntryTable.removeItem(it1.next());
				}

				int SN = 0;
				Item newitem;
				Iterator it = accountPaymentEntryTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					newitem = accountPaymentEntryTable.getItem((Integer) it
							.next());
					newitem.getItemProperty(TSR_SN).setValue(SN);
				}
			}
			accountPaymentEntryTable.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isAddingValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (itemPriceTextField.getValue() == null
				|| itemPriceTextField.getValue().equals("")) {
			setRequiredError(itemPriceTextField,
					getPropertyName("invalid_data"), true);
			itemPriceTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(itemPriceTextField.getValue()) <= 0) {
					setRequiredError(itemPriceTextField,
							getPropertyName("invalid_data"), true);
					itemPriceTextField.focus();
					ret = false;
				} else
					setRequiredError(itemPriceTextField, null, false);
			} catch (Exception e) {
				setRequiredError(itemPriceTextField,
						getPropertyName("invalid_data"), true);
				itemPriceTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (conversionRateextField.getValue() == null
				|| conversionRateextField.getValue().equals("")) {
			setRequiredError(conversionRateextField,
					getPropertyName("invalid_data"), true);
			conversionRateextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(conversionRateextField.getValue()) <= 0) {
					setRequiredError(conversionRateextField,
							getPropertyName("invalid_data"), true);
					conversionRateextField.focus();
					ret = false;
				} else
					setRequiredError(conversionRateextField, null, false);
			} catch (Exception e) {
				setRequiredError(conversionRateextField,
						getPropertyName("invalid_data"), true);
				conversionRateextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (salesTypeSelect.isVisible()) {

			if (salesTypeSelect.getValue() == null
					|| salesTypeSelect.getValue().equals("")) {
				setRequiredError(salesTypeSelect,
						getPropertyName("invalid_selection"), true);
				salesTypeSelect.focus();
				ret = false;
			} else
				setRequiredError(salesTypeSelect, null, false);

		}

		if (unitList.getValue() == null || unitList.getValue().equals("")) {
			setRequiredError(unitList, getPropertyName("invalid_selection"),
					true);
			unitList.focus();
			ret = false;
		} else
			setRequiredError(unitList, null, false);

		return ret;
	}

	/*
	 * public void calculateTotals(){ try {
	 * 
	 * double amtttl=0;
	 * 
	 * Iterator it=accountPaymentEntryTable.getItemIds().iterator(); while
	 * (it.hasNext()) { Item item=accountPaymentEntryTable.getItem(it.next());
	 * 
	 * amtttl+=(Double)item.getItemProperty( TSJ_CONVERSION_RATE).getValue(); }
	 * 
	 * accountPaymentEntryTable.setColumnFooter(TSJ_CONVERSION_RATE,
	 * asString(roundNumber(amtttl)));
	 * 
	 * 
	 * } catch (Exception e) { // TODO: handle exception
	 * Notification.show("Error..!!",
	 * "Error Message from Method calculateTotal() :"+e.getCause(),
	 * Type.ERROR_MESSAGE); } }
	 */

	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (accountPaymentEntryTable.getItemIds().size() <= 0) {
			setRequiredError(accountPaymentEntryTable,
					getPropertyName("add_some_items"), true);
			ret = false;
		} else
			setRequiredError(accountPaymentEntryTable, null, false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
