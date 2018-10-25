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
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SCurrencyField;
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
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 10, 2013
 */

/**
 * @author sangeeth
 * @date 28-Dec-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class UnitManagementUI extends SparkLogic {

	SPanel panel = null;

	static String TSR_SN = "SN";
	static String TSJ_UNIT_ID = "Unit ID";
	static String TSJ_UNIT_NAME = "Alternate Unit";
	static String TSJ_CONVERSION_RATE = "Conversion Quantity";
	static String TSJ_SALES_TYPE = "Sales Type";
	static String TSJ_SALES_TYPE_ID = "Sales Type ID";
	static String TSJ_PRICE = "Item Rate";
	static String TSJ_CID = "CID";
	static String TSJ_CURRENCY = "Currency";
	static String TSJ_CONV_RATE = "Conv Rate";

	STable table;
	UnitManagementDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout accountPaymentAddGrid;
	SVerticalLayout stkrkVLay;

	public SComboField itemCombo;

	SNativeSelect salesTypeSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;

	SComboField unitList;

	STextField conversionRateextField;

	SCurrencyField itemPriceField;

	SButton saveButton;

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	STextField basicUnit;

	SRadioButton saleOrPurchaseRadio;

	LedgerDao ledgerDao;

	ItemDao itmDao;
	SalesTypeDao stpDao;
	UnitDao untDao;

	@SuppressWarnings({"rawtypes" })
	@Override
	public SPanel getGUI() {

		stpDao = new SalesTypeDao();
		untDao = new UnitDao();
		allHeaders = new String[] { TSR_SN, TSJ_UNIT_ID, TSJ_UNIT_NAME,
				TSJ_CONVERSION_RATE, TSJ_SALES_TYPE, TSJ_SALES_TYPE_ID,
				TSJ_PRICE, TSJ_CID, TSJ_CURRENCY, TSJ_CONV_RATE };

		requiredHeaders = new String[] { TSR_SN, TSJ_UNIT_NAME,
				TSJ_CONVERSION_RATE, TSJ_SALES_TYPE, TSJ_PRICE, TSJ_CURRENCY };

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

		itemPriceField = new SCurrencyField(getPropertyName("item_price"), 100, getWorkingDate());
		itemPriceField.setNewValue(getCurrencyID(), 0.0);
		itemPriceField.setStyleName("textfield_align_right");

		basicUnit.setReadOnly(true);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(900, 500);

		try {

			salesTypeSelect = new SNativeSelect(getPropertyName("sales_type"),
					140, stpDao.getAllActiveSalesTypeNames(getOfficeID()),
					"id", "name");

			saleOrPurchaseRadio = new SRadioButton(getPropertyName("type"), 170,
					SConstants.saleOrPurchaseList, "intKey", "value");
			saleOrPurchaseRadio.setStyleName("radio_horizontal");
			updateItemButton.setVisible(false);

			saleOrPurchaseRadio.setValue(1);

			table = new STable(null, 800, 200);

			List list = itmDao
					.getAllActiveItemsWithAppendingItemCode(getOfficeID());

			itemCombo = new SComboField(null, 200, list, "id", "name", true,
					getPropertyName("select"));

			unitList = new SComboField(getPropertyName("unit"), 200,
					untDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol", true, getPropertyName("select"));

			accountPaymentAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);
			// masterDetailsGrid.setWidth("860");

			table.setSizeFull();
			table.setSelectable(true);
			table.setMultiSelect(true);

			table.setWidth("860px");
			table.setHeight("180px");

			table.addContainerProperty(TSR_SN,
					Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TSJ_UNIT_ID,
					Long.class, null, TSJ_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TSJ_UNIT_NAME,
					String.class, null, getPropertyName("alt_unit"), null,
					Align.LEFT);
			table.addContainerProperty(TSJ_PRICE, Double.class, null, getPropertyName("item_rate"), null,Align.RIGHT);
			table.addContainerProperty(TSJ_CID, Long.class, null, TSJ_CID, null,Align.CENTER);
			table.addContainerProperty(TSJ_CURRENCY, String.class, null, getPropertyName("currency"), null,Align.CENTER);
			table.addContainerProperty(TSJ_CONV_RATE, Double.class, null, TSJ_CONV_RATE, null,Align.CENTER);
			
			
			table.addContainerProperty(TSJ_CONVERSION_RATE,
					Double.class, null, getPropertyName("conv_qty"), null,
					Align.CENTER);
			table.addContainerProperty(TSJ_SALES_TYPE,
					String.class, null, getPropertyName("sales_type"), null,
					Align.LEFT);
			table.addContainerProperty(TSJ_SALES_TYPE_ID,
					Long.class, null, TSJ_SALES_TYPE_ID, null, Align.CENTER);

			table.setColumnExpandRatio(TSR_SN, (float) .5);
			table.setColumnExpandRatio(TSJ_UNIT_NAME, 1.5f);
			table.setColumnExpandRatio(TSJ_CONVERSION_RATE, 1.5f);	
			table.setColumnExpandRatio(TSJ_CURRENCY, .5f);	

			accountPaymentAddGrid.setColumns(8);
			accountPaymentAddGrid.setRows(2);

			accountPaymentAddGrid.setStyleName("unitmngmt_adding_grid");

			conversionRateextField = new STextField(
					getPropertyName("conv_qty"), 80);

			accountPaymentAddGrid.addComponent(saleOrPurchaseRadio);
			accountPaymentAddGrid.addComponent(salesTypeSelect);
			accountPaymentAddGrid.addComponent(unitList);
			accountPaymentAddGrid.addComponent(conversionRateextField);
			accountPaymentAddGrid.addComponent(itemPriceField);

			accountPaymentAddGrid.addComponent(addItemButton);
			accountPaymentAddGrid.addComponent(updateItemButton);

			conversionRateextField.setStyleName("textfield_align_right");

			accountPaymentAddGrid.setComponentAlignment(itemPriceField,
					Alignment.MIDDLE_RIGHT);

			accountPaymentAddGrid.setComponentAlignment(addItemButton,
					Alignment.BOTTOM_RIGHT);
			accountPaymentAddGrid.setComponentAlignment(updateItemButton,
					Alignment.BOTTOM_RIGHT);

			accountPaymentAddGrid.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("item")),
					1, 0);
			masterDetailsGrid.addComponent(itemCombo, 2, 0);
			// masterDetailsGrid.addComponent(new SLabel("Date :"), 6, 0);

			// masterDetailsGrid.addComponent(new SLabel("Pay To :"), 3, 1);
			// masterDetailsGrid.addComponent(, 4, 1);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("basic_unit")), 3, 0);
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

			stkrkVLay.addComponent(table);

			stkrkVLay.addComponent(accountPaymentAddGrid);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(accountPaymentAddGrid,
					Alignment.BOTTOM_CENTER);

			table.setVisibleColumns(requiredHeaders);

			// table.setFooterVisible(true);
			// table.setColumnFooter(TSJ_CONVERSION_RATE,
			// asString(roundNumber(0)));
			// table.setColumnFooter(TSJ_UNIT_NAME, "Total");

			panel.setContent(stkrkVLay);

			
			saleOrPurchaseRadio.addValueChangeListener(new Property.ValueChangeListener() {
				@SuppressWarnings("unchecked")
				public void valueChange(ValueChangeEvent event) {
					try {

						List lst = new ArrayList();
						if ((Integer) saleOrPurchaseRadio.getValue() == 1) {
							salesTypeSelect.setVisible(true);
							lst = stpDao
									.getAllActiveSalesTypeNames(getOfficeID());
							SCollectionContainer bic = SCollectionContainer
									.setList(lst, "id");
							salesTypeSelect.setContainerDataSource(bic);
							salesTypeSelect.setItemCaptionPropertyId("name");
						} else {
							salesTypeSelect.setVisible(false);
							lst.add(new SalesTypeModel(0, "Purchase"));
							SCollectionContainer bic = SCollectionContainer.setList(lst, "id");
							salesTypeSelect.setContainerDataSource(bic);
							salesTypeSelect.setItemCaptionPropertyId("name");

							salesTypeSelect.setValue((long) 0);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			salesTypeSelect.setValue(0);

			
			itemCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						table.setVisibleColumns(allHeaders);

						table.removeAllItems();
						if (itemCombo.getValue() != null
								&& !itemCombo.getValue().toString()
										.equals("0")) {

							ItemModel item = itmDao.getItem((Long) itemCombo
									.getValue());

							basicUnit
									.setNewValue(item.getUnit().getName()
											+ " ( "
											+ item.getUnit().getSymbol() + " )");

							List list = daoObj.getAllItemUnitDetails(item
									.getId());

							ItemUnitMangementModel obj;
							String saleTypeName = "";
							int ct = 0;
							Iterator it = list.iterator();
							while (it.hasNext()) {

								ct++;
								saleTypeName = "";
								obj = (ItemUnitMangementModel) it.next();
								if (obj.getSales_type() != 0)
									saleTypeName = stpDao.getSalesType(
											obj.getSales_type()).getName();

								table.addItem(new Object[] {ct,
															obj.getAlternateUnit(),
															untDao.getUnit(obj.getAlternateUnit()).getSymbol(),
															obj.getConvertion_rate(),
															saleTypeName,
															obj.getSales_type(),
															roundNumber(obj.getItem_price()),
															obj.getPurchaseCurrency().getId(),
															obj.getPurchaseCurrency().getCode(),
															roundNumber(obj.getPurchase_convertion_rate())}, ct);
							}

						} else {
							basicUnit.setNewValue("");
							unitList.setValue(null);

						}
						table.setVisibleColumns(requiredHeaders);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			
			table.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table
								.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = table.getItem(selectedItems.iterator().next());
						if((Long)item.getItemProperty(TSJ_SALES_TYPE_ID).getValue()!=0)
							saleOrPurchaseRadio.setValue(1);
						else
							saleOrPurchaseRadio.setValue(2);
						unitList.setValue(item.getItemProperty(TSJ_UNIT_ID).getValue());
						conversionRateextField.setValue(asString(item.getItemProperty(TSJ_CONVERSION_RATE).getValue()));
						itemPriceField.setNewValue((Long) item.getItemProperty(TSJ_CID).getValue(),(Double) item.getItemProperty(TSJ_PRICE).getValue());
						itemPriceField.conversionField.setNewValue((Double)item.getItemProperty(TSJ_CONV_RATE).getValue()+"");
						if((Long)item.getItemProperty(TSJ_SALES_TYPE_ID).getValue()!=0)
							salesTypeSelect.setValue(item.getItemProperty(TSJ_SALES_TYPE_ID).getValue());
						updateItemButton.setVisible(true);
						addItemButton.setVisible(false);
						unitList.focus();
					} else {
						saleOrPurchaseRadio.setValue(1);
						updateItemButton.setVisible(false);
						addItemButton.setVisible(true);
						unitList.setValue(null);
						conversionRateextField.setValue("");
						unitList.focus();
						itemPriceField.setNewValue(getCurrencyID(), 0.0);
					}
				}
			});

			
			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						table.setVisibleColumns(allHeaders);

						setRequiredError(table, null, false);
						if (isAddingValid()) {

							Collection selectedItems = null;
							if (table.getValue() != null) {
								selectedItems = (Collection) table
										.getValue();
							}

							int sel_id = (Integer) selectedItems.iterator()
									.next();

							int id;
							Item item;
							boolean exist = false;
							Iterator it1 = table
									.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								if (id != sel_id) {

									item = table.getItem(id);

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

								item = table.getItem(sel_id);

								item.getItemProperty(TSJ_UNIT_ID).setValue(
										unitList.getValue());
								item.getItemProperty(TSJ_UNIT_NAME).setValue(
										unitList.getItemCaption(unitList
												.getValue()));

								double amt = toDouble(conversionRateextField
										.getValue());
								double price = itemPriceField.getValue();
								item.getItemProperty(TSJ_CONVERSION_RATE).setValue(amt);
								item.getItemProperty(TSJ_PRICE).setValue(roundNumber(price));
								item.getItemProperty(TSJ_CID).setValue(itemPriceField.getCurrency());
								item.getItemProperty(TSJ_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(itemPriceField.getCurrency()).getCode());
								item.getItemProperty(TSJ_CONV_RATE).setValue(roundNumber(itemPriceField.getConversionRate()));
								
								
								if ((Integer) saleOrPurchaseRadio.getValue() == 1) {
									item.getItemProperty(TSJ_SALES_TYPE_ID).setValue((Long) salesTypeSelect.getValue());
									item.getItemProperty(TSJ_SALES_TYPE).setValue(salesTypeSelect.getItemCaption(salesTypeSelect.getValue()));
								}
								else {
									item.getItemProperty(TSJ_SALES_TYPE_ID).setValue((long)0);
									item.getItemProperty(TSJ_SALES_TYPE).setValue("");
								}
								updateItemButton.setVisible(false);
								addItemButton.setVisible(true);
								table.setValue(null);
							} else {
								setRequiredError(unitList,
										getPropertyName("unit_exist"), true);
								conversionRateextField.focus();
							}

						}

						table
								.setVisibleColumns(requiredHeaders);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						table.setVisibleColumns(allHeaders);

						setRequiredError(table, null, false);
						if (isAddingValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;

							Item item;
							int id = 0, ct = table
									.getItemIds().size();
							Iterator it1 = table
									.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								item = table.getItem(id);
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
								double price = itemPriceField.getValue();

								table.addItem(
										new Object[] {ct,
													(Long) unitList.getValue(),
													unitList.getItemCaption(unitList.getValue()), amt,
													salTyp, salTypeId,
													roundNumber(price),
													itemPriceField.getCurrency(),
													new CurrencyManagementDao().getselecteditem(itemPriceField.getCurrency()).getCode(),
													roundNumber(itemPriceField.getConversionRate())}, id);

								unitList.setValue(null);
								conversionRateextField.setValue("");
								itemPriceField.setNewValue(getCurrencyID(), 0.0);
								unitList.focus();

							} else {
								setRequiredError(unitList,
										getPropertyName("unit_exist"), true);
								conversionRateextField.focus();
							}
						}

						table
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
							ItemModel itm = itmDao.getItem((Long) itemCombo
									.getValue());

							Item item;
							ItemUnitMangementModel objMdl;
							FinTransaction tran = new FinTransaction();
							Iterator it = table.getItemIds()
									.iterator();
							while (it.hasNext()) {
								item = table.getItem(it
										.next());

								objMdl = new ItemUnitMangementModel();

								objMdl.setAlternateUnit((Long) item.getItemProperty(TSJ_UNIT_ID).getValue());
								objMdl.setBasicUnit(itm.getUnit().getId());
								objMdl.setConvertion_rate((Double) item.getItemProperty(TSJ_CONVERSION_RATE).getValue());
								objMdl.setItem(itm);
								objMdl.setSales_type((Long) item.getItemProperty(TSJ_SALES_TYPE_ID).getValue());
								objMdl.setStatus(1);
								objMdl.setItem_price(roundNumber((Double) item.getItemProperty(TSJ_PRICE).getValue()));
								objMdl.setPurchaseCurrency(new CurrencyModel((Long) item.getItemProperty(TSJ_CID).getValue()));
								objMdl.setPurchase_convertion_rate(roundNumber((Double) item.getItemProperty(TSJ_CONV_RATE).getValue()));
								list.add(objMdl);
							}

							daoObj.save(list, (Long) itemCombo.getValue());

							itemCombo.setValue(null);
							itemCombo.setValue(itm.getId());

							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			/*addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});*/

			table.addShortcutListener(new ShortcutListener(
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

			
			table.addActionHandler(new Action.Handler() {
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

			
			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
				}
			});

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	
	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				Collection selectedItems = (Collection) table
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					table.removeItem(it1.next());
				}

				Item newitem;
				int SN = 0;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = table.getItem((Integer) it
							.next());

					newitem.getItemProperty(TSR_SN).setValue(SN);

				}
			}
			table.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public Boolean isAddingValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if(!itemPriceField.isFieldValid(getWorkingDate())){
			setRequiredError(itemPriceField,getPropertyName("invalid_data"), true);
			ret = false;
		}
		else
			setRequiredError(itemPriceField, null, false);
		
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
	 * Iterator it=table.getItemIds().iterator(); while
	 * (it.hasNext()) { Item item=table.getItem(it.next());
	 * 
	 * amtttl+=(Double)item.getItemProperty( TSJ_CONVERSION_RATE).getValue(); }
	 * 
	 * table.setColumnFooter(TSJ_CONVERSION_RATE,
	 * asString(roundNumber(amtttl)));
	 * 
	 * 
	 * } catch (Exception e) { // TODO: handle exception
	 * Notification.show("Error..!!",
	 * "Error Message from Method calculateTotal() :"+e.getCause(),
	 * Type.ERROR_MESSAGE); } }
	 */

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table,
					getPropertyName("add_some_items"), true);
			ret = false;
		} else
			setRequiredError(table, null, false);

		return ret;
	}

	
	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
