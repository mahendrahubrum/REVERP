package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.StockReceiveDao;
import com.inventory.config.tax.dao.StockRackMappingDao;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.dao.BuildingDao;
import com.inventory.dao.RackDao;
import com.inventory.dao.RoomDao;
import com.inventory.model.RackModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.StockRackMappingModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.SConstants;

public class StockRackMappingPannel extends SContainerPanel {

	private static final long serialVersionUID = 2681782027678255070L;
	static String TSR_SN = "SN";
	static String TSR_RACK_ID = "RackID";
	static String TSR_RACK_NAME = "Rack No";
	static String TSR_STOCK_ID = "StockID";
	static String TSR_STOCK_DETAILS = "Stock";
	static String TSR_QTY = "Qty";
	static String TSR_QUANTITY_IN_BASIC_UNIT = "Qty In Basic Unit";
	static String TSR_UNIT_ID = "UNIT_ID";
	static String TSR_UNIT_NAME = "Unit";

	STable rackStockTable;
	PurchaseDao purDaoObj = new PurchaseDao();
	StockRackMappingDao daoObj = new StockRackMappingDao();

	CommonMethodsDao comDao;

	SGridLayout stkrakmapGrid;
	SVerticalLayout stkrkVLay;

	STextField quantityInRackTextField;
	SNativeButton addStockToRack = new SNativeButton(getPropertyName("add"));
	SNativeButton updateStockToRack = new SNativeButton(
			getPropertyName("update"));
	SNativeButton saveStockToRack = new SNativeButton(getPropertyName("save"));

	SComboField stockSelect;
	SComboField rackSelect;
	SComboField buildingSelect;
	SComboField roomSelect;

	SRadioButton modeSelect;

	SNativeSelect unitSelect;

	UnitDao untDao = new UnitDao();

	BuildingDao bldDao = new BuildingDao();
	RoomDao rmDao = new RoomDao();

	List<Object> salesStockList;

	private String[] allHeaders;
	private String[] requiredHeaders;

	@SuppressWarnings("deprecation")
	public StockRackMappingPannel(final long purchase_id) {
		super();

		setSize(700, 400);
		try {

			comDao = new CommonMethodsDao();

			updateStockToRack.setVisible(false);

			rackStockTable = new STable(null, 600, 200);

			stkrakmapGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			rackStockTable.setSizeFull();
			rackStockTable.setSelectable(true);
			rackStockTable.setMultiSelect(true);

			rackStockTable.setWidth("660px");
			rackStockTable.setHeight("180px");

			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			allHeaders = new String[] { TSR_SN, TSR_RACK_ID, TSR_RACK_NAME,
					TSR_STOCK_ID, TSR_STOCK_DETAILS, TSR_QTY, TSR_UNIT_ID,
					TSR_UNIT_NAME, TSR_QUANTITY_IN_BASIC_UNIT };
			requiredHeaders = new String[] { TSR_SN, TSR_RACK_NAME,
					TSR_STOCK_DETAILS, TSR_QTY, TSR_UNIT_NAME };

			if (purchase_id != 0) {
				modeSelect = new SRadioButton(getPropertyName("mode"), 500,
						SConstants.stoc_rack_map.purchase_mode, "key", "value");
			} else {
				modeSelect = new SRadioButton(getPropertyName("mode"), 500,
						SConstants.stoc_rack_map.common_modes, "key", "value");
			}

			modeSelect.addStyleName("radio_horizontal");

			buildingSelect = new SComboField(getPropertyName("building"), 100,
					bldDao.getAllActiveBuildingNamesUnderOffice(getOfficeID()),
					"id", "name", false, getPropertyName("select"));

			roomSelect = new SComboField(getPropertyName("room"), 100, null,
					"id", "room_number", false, getPropertyName("select"));

			stkrkVLay.addComponent(modeSelect);

			rackStockTable.addContainerProperty(TSR_SN, Integer.class, null,
					"#", null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_RACK_ID, Long.class, null,
					getPropertyName("rack_id"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_RACK_NAME, String.class,
					null, getPropertyName("rack_no"), null, Align.LEFT);
			rackStockTable.addContainerProperty(TSR_STOCK_ID, Long.class, null,
					getPropertyName("stock_id"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_STOCK_DETAILS,
					String.class, null, getPropertyName("stock"), null,
					Align.LEFT);
			rackStockTable.addContainerProperty(TSR_QTY, Double.class, null,
					getPropertyName("qty"), null, Align.RIGHT);

			rackStockTable.addContainerProperty(TSR_UNIT_ID, Long.class, null,
					getPropertyName("unit_id"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_UNIT_NAME, String.class,
					null, getPropertyName("unit"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_QUANTITY_IN_BASIC_UNIT,
					Double.class, null, getPropertyName("qty_basic_unit"),
					null, Align.RIGHT);

			rackStockTable.setColumnExpandRatio(TSR_SN, (float) .5);
			rackStockTable.setColumnExpandRatio(TSR_STOCK_DETAILS, 2);
			rackStockTable.setColumnExpandRatio(TSR_RACK_NAME, 1);
			rackStockTable.setColumnExpandRatio(TSR_QTY, 1);

			stkrakmapGrid.setColumns(8);
			stkrakmapGrid.setRows(2);

			stockSelect = new SComboField(null, 250, null, "id", "details",
					true, getPropertyName("select"));

			rackSelect = new SComboField(getPropertyName("rack"), 120, null,
					"id", "rack_number", true, getPropertyName("select"));

			quantityInRackTextField = new STextField(
					getPropertyName("quantity"), 80);
			// stkrakmapGrid.addComponent();
			stkrakmapGrid.addComponent(buildingSelect);
			stkrakmapGrid.addComponent(roomSelect);
			stkrakmapGrid.addComponent(rackSelect);

			stkrakmapGrid.addComponent(unitSelect);
			stkrakmapGrid.addComponent(quantityInRackTextField);

			stkrakmapGrid.addComponent(addStockToRack);
			stkrakmapGrid.addComponent(updateStockToRack);

			quantityInRackTextField.setStyleName("textfield_align_right");

			stkrakmapGrid.setComponentAlignment(addStockToRack,
					Alignment.BOTTOM_RIGHT);
			stkrakmapGrid.setComponentAlignment(updateStockToRack,
					Alignment.BOTTOM_RIGHT);

			stkrakmapGrid.setSpacing(true);

			SGridLayout miniGrid = new SGridLayout();
			miniGrid.setColumns(4);
			miniGrid.setRows(1);
			SLabel lb = new SLabel();
			lb.setValue(getPropertyName("stock"));
			miniGrid.addComponent(lb, 1, 0);
			miniGrid.addComponent(stockSelect, 2, 0);
			miniGrid.setWidth("400px");
			// miniGrid.setComponentAlignment(stockSelect,
			// Alignment.TOP_CENTER);

			stkrkVLay.addComponent(miniGrid);
			stkrkVLay.setComponentAlignment(miniGrid, Alignment.TOP_CENTER);
			stkrkVLay.addComponent(rackStockTable);
			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(stkrakmapGrid);
			stkrkVLay.addComponent(saveStockToRack);
			stkrkVLay.setComponentAlignment(saveStockToRack,
					Alignment.BOTTOM_CENTER);

			rackStockTable.setVisibleColumns(requiredHeaders);

			setContent(stkrkVLay);

			rackStockTable.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					Collection selectedItems = null;

					if (rackStockTable.getValue() != null) {
						selectedItems = (Collection) rackStockTable.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = rackStockTable.getItem(selectedItems
								.iterator().next());

						// if(item.getItemProperty(
						// TSR_MAP_ID).getValue().toString().equals("0")) {
						stockSelect.setNewValue(item.getItemProperty(
								TSR_STOCK_ID).getValue());
						rackSelect.setNewValue(item
								.getItemProperty(TSR_RACK_ID).getValue());
						quantityInRackTextField.setValue(""
								+ item.getItemProperty(TSR_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TSR_UNIT_ID)
								.getValue());

						updateStockToRack.setVisible(true);
						addStockToRack.setVisible(false);

					} else {
						updateStockToRack.setVisible(false);
						addStockToRack.setVisible(true);
						rackSelect.setValue(null);
						quantityInRackTextField.setValue("");
						setDefaultValues();
					}
				}
			});

			addStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(rackStockTable, null, false);
						if (isStockRackValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							rackStockTable.setVisibleColumns(allHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							// double
							// conv_rate=comDao.getConvertionRate(stock.getItem().getId(),
							// unit_id, 0);

							Item itm;

							int id = 0, ct = rackStockTable.getItemIds().size();
							Iterator it1 = rackStockTable.getItemIds()
									.iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								itm = rackStockTable.getItem(id);
								if (itm.getItemProperty(TSR_RACK_ID)
										.getValue()
										.toString()
										.equals(rackSelect.getValue()
												.toString())
										&& itm.getItemProperty(TSR_UNIT_ID)
												.getValue()
												.toString()
												.equals(unitSelect.getValue()
														.toString())) {
									exist = true;
									exist_id = id;
								}
								total_qty += (Double) itm.getItemProperty(
										TSR_QUANTITY_IN_BASIC_UNIT).getValue();
							}

							double old_bal = total_qty;

							double conv_rat = comDao.getConvertionRate(stock
									.getItem().getId(), (Long) unitSelect
									.getValue(), 0);

							total_qty += conv_rat
									* Double.parseDouble(quantityInRackTextField
											.getValue());

							id++;
							ct++;

							// RackModel rack=new
							// RackDao().getRack((Long)rackSelect.getValue());

							if (total_qty > stock.getQuantity()) {
								setRequiredError(
										quantityInRackTextField,
										getPropertyName("total_qty_greater")
												+ (stock.getQuantity() - old_bal),
										true);
							} else {
								setRequiredError(quantityInRackTextField, null,
										false);

								if (exist) {
									itm = rackStockTable.getItem(exist_id);
									itm.getItemProperty(TSR_QTY)
											.setValue(
													(Double) itm
															.getItemProperty(
																	TSR_QTY)
															.getValue()
															+ Double.parseDouble(quantityInRackTextField
																	.getValue()));

									itm.getItemProperty(
											TSR_QUANTITY_IN_BASIC_UNIT)
											.setValue(
													(Double) itm
															.getItemProperty(
																	TSR_QUANTITY_IN_BASIC_UNIT)
															.getValue()
															+ conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()));
									itm.getItemProperty(TSR_UNIT_ID).setValue(
											(Long) unitSelect.getValue());
									itm.getItemProperty(TSR_UNIT_NAME)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));

								} else {
									rackStockTable.addItem(
											new Object[] {
													ct,
													(Long) rackSelect
															.getValue(),
													rackSelect
															.getItemCaption(rackSelect
																	.getValue()),
													(Long) stockSelect
															.getValue(),
													stock.getId()
															+ " ; "
															+ stock.getItem()
																	.getName()
															+ " Exp: "
															+ stock.getExpiry_date(),
													Double.parseDouble(quantityInRackTextField
															.getValue()),
													(Long) unitSelect
															.getValue(),
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()),
													conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()) },
											id);

								}

								// stockSelect.setValue(null);
								rackSelect.setValue(null);
								quantityInRackTextField.setValue("");
								stockSelect.focus();

							}
							rackStockTable.setVisibleColumns(requiredHeaders);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			updateStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(rackStockTable, null, false);
						if (isStockRackValid()) {

							double total_qty = 0;
							rackStockTable.setVisibleColumns(allHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							Collection selectedItems = null;

							if (rackStockTable.getValue() != null) {
								selectedItems = (Collection) rackStockTable
										.getValue();
							}

							if (selectedItems != null
									&& selectedItems.size() == 1) {

								int sel_id = (Integer) selectedItems.iterator()
										.next();
								Item item = rackStockTable.getItem(sel_id);

								Item itm;
								int id = 0, ct = rackStockTable.getItemIds()
										.size();
								Iterator it1 = rackStockTable.getItemIds()
										.iterator();
								while (it1.hasNext()) {
									id = (Integer) it1.next();

									itm = rackStockTable.getItem(id);

									if (itm.getItemProperty(TSR_STOCK_ID)
											.getValue()
											.toString()
											.equals(stockSelect.getValue()
													.toString())) {
										if (id != sel_id)
											total_qty += (Double) itm
													.getItemProperty(
															TSR_QUANTITY_IN_BASIC_UNIT)
													.getValue();
									}
								}

								double old_bal = total_qty;

								double conv_rat = comDao.getConvertionRate(
										stock.getItem().getId(),
										(Long) unitSelect.getValue(), 0);

								total_qty += conv_rat
										* Double.parseDouble(quantityInRackTextField
												.getValue());

								id++;
								ct++;

								// RackModel rack=new
								// RackDao().getRack((Long)rackSelect.getValue());

								if (total_qty > stock.getQuantity()) {
									setRequiredError(
											quantityInRackTextField,
											getPropertyName("total_qty_greater")
													+ (stock.getQuantity() - old_bal),
											true);
								} else {
									setRequiredError(quantityInRackTextField,
											null, false);

									// if(exist){
									// Item
									// itm=rackStockTable.getItem(exist_id);
									item.getItemProperty(TSR_QTY).setValue(
											toDouble(quantityInRackTextField
													.getValue()));

									item.getItemProperty(
											TSR_QUANTITY_IN_BASIC_UNIT)
											.setValue(
													conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()));
									item.getItemProperty(TSR_UNIT_ID).setValue(
											(Long) unitSelect.getValue());
									item.getItemProperty(TSR_UNIT_NAME)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));

									item.getItemProperty(TSR_RACK_ID).setValue(
											rackSelect.getValue());
									item.getItemProperty(TSR_RACK_NAME)
											.setValue(
													rackSelect
															.getItemCaption(rackSelect
																	.getValue()));

									updateStockToRack.setVisible(false);
									addStockToRack.setVisible(true);

									rackStockTable.setValue(null);

									rackSelect.setValue(null);
									quantityInRackTextField.setValue("");
									stockSelect.focus();

								}
								rackStockTable
										.setVisibleColumns(requiredHeaders);

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			modeSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						rackStockTable.removeAllItems();

						if ((Long) modeSelect.getValue() == 3) {
							stockSelect.setNewValue(null);

							stockSelect
									.setContainerDataSource(SCollectionContainer.setList(
											daoObj.getPendingAvailableStocksList(getOfficeID()),
											"id"));
							stockSelect.setItemCaptionPropertyId("details");

							addStockToRack.setVisible(true);

							updateStockToRack.setVisible(false);
						} else if ((Long) modeSelect.getValue() == 2) {
							stockSelect.setNewValue(null);

							stockSelect
									.setContainerDataSource(SCollectionContainer.setList(
											daoObj.getArrangedAvailabeStocksList(getOfficeID()),
											"id"));
							stockSelect.setItemCaptionPropertyId("details");

							// addStockToRack.setVisible(false);
							// updateStockToRack.setVisible(true);

							addStockToRack.setVisible(true);

							updateStockToRack.setVisible(false);

						} else if ((Long) modeSelect.getValue() == 1) {

							stockSelect.setNewValue(null);

							stockSelect
									.setContainerDataSource(SCollectionContainer.setList(
											daoObj.getStockListOfPurchase(purchase_id),
											"id"));
							stockSelect.setItemCaptionPropertyId("details");

							// addStockToRack.setVisible(false);
							// updateStockToRack.setVisible(true);

							addStockToRack.setVisible(true);

							updateStockToRack.setVisible(false);

						}

						stockSelect.focus();
						rackSelect.setValue(null);
						quantityInRackTextField.setValue("");

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

			stockSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						rackStockTable.removeAllItems();
						if (stockSelect.getValue() != null) {

							rackStockTable.setVisibleColumns(allHeaders);

							StockRackMappingModel srm;
							int ct = 0, id = 0;
							Iterator it1 = daoObj.getStockRackMappings(
									(Long) stockSelect.getValue()).iterator();
							while (it1.hasNext()) {
								ct++;
								id++;
								srm = (StockRackMappingModel) it1.next();
								rackStockTable.addItem(
										new Object[] {
												ct,
												srm.getRack().getId(),
												srm.getRack().getRack_number(),
												srm.getStock().getId(),
												srm.getStock().getId()
														+ " ; "
														+ srm.getStock()
																.getItem()
																.getName()
														+ " Exp: "
														+ srm.getStock()
																.getExpiry_date(),
												srm.getQuantity(),
												srm.getUnit_id(),
												untDao.getUnit(srm.getUnit_id())
														.getSymbol(),
												srm.getQuantity_in_basic_unit() },
										id);
							}

							rackStockTable.setVisibleColumns(requiredHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							SCollectionContainer bic = SCollectionContainer
									.setList(comDao.getAllItemUnitDetails(stock
											.getItem().getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							try {
								unitSelect.setValue(stock.getItem().getUnit()
										.getId());
							} catch (Exception e) {
								unitSelect.setValue(unitSelect.getItemIds()
										.iterator().next());
							}

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

			saveStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (stockSelect.getValue() != null) {

							List mapList = new ArrayList();
							Iterator it1 = rackStockTable.getItemIds()
									.iterator();

							// if(rackStockTable.getItemIds().size()>0) {
							Item itm;
							StockRackMappingModel srObj;
							while (it1.hasNext()) {
								itm = rackStockTable.getItem(it1.next());

								srObj = new StockRackMappingModel();
								srObj.setQuantity((Double) itm.getItemProperty(
										TSR_QTY).getValue());
								srObj.setRack(new RackModel((Long) itm
										.getItemProperty(TSR_RACK_ID)
										.getValue()));
								srObj.setStock(new ItemStockModel((Long) itm
										.getItemProperty(TSR_STOCK_ID)
										.getValue()));

								srObj.setQuantity_in_basic_unit((Double) itm
										.getItemProperty(
												TSR_QUANTITY_IN_BASIC_UNIT)
										.getValue());
								srObj.setBalance((Double) itm.getItemProperty(
										TSR_QUANTITY_IN_BASIC_UNIT).getValue());
								srObj.setUnit_id((Long) itm.getItemProperty(
										TSR_UNIT_ID).getValue());

								mapList.add(srObj);
								// }
							}

							daoObj.saveStockRackMap(mapList,
									(Long) stockSelect.getValue());

							Notification.show(
									getPropertyName("mapped_success"),
									Type.WARNING_MESSAGE);

							Object temp = stockSelect.getValue();

							rackStockTable.removeAllItems();
							if ((Long) modeSelect.getValue() == 3) {
								stockSelect
										.setContainerDataSource(SCollectionContainer.setList(
												daoObj.getPendingAvailableStocksList(getOfficeID()),
												"id"));
								stockSelect.setItemCaptionPropertyId("details");
							} else if ((Long) modeSelect.getValue() == 2) {
								stockSelect
										.setContainerDataSource(SCollectionContainer.setList(
												daoObj.getArrangedAvailabeStocksList(getOfficeID()),
												"id"));
								stockSelect.setItemCaptionPropertyId("details");
							}

							stockSelect.setValue(null);
							stockSelect.setValue(temp);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			rackStockTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					rackStockTable.setValue(null);
				}
			});

			final Action actionDeleteStock = new Action("Delete");

			rackStockTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteStockRack();
				}

			});

			buildingSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (buildingSelect.getValue() != null) {
							roomSelect
									.setContainerDataSource(SCollectionContainer.setList(
											rmDao.getAllRoomNamesFromBuilding((Long) buildingSelect
													.getValue()), "id"));
							roomSelect.setItemCaptionPropertyId("room_number");
						}

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			roomSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (roomSelect.getValue() != null) {
							rackSelect
									.setContainerDataSource(SCollectionContainer.setList(
											new RackDao()
													.getAllActiveRacksUnderRoom((Long) roomSelect
															.getValue()), "id"));
							rackSelect.setItemCaptionPropertyId("rack_number");
						}

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteStockRack();
				}
			});

			if (purchase_id != 0) {
				modeSelect.setValue((long) 1);
			} else {
				modeSelect.setValue((long) 3);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setDefaultValues();

		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	public StockRackMappingPannel(final List stockList, final long transfer_id) {
		super();

		setSize(700, 400);
		try {

			comDao = new CommonMethodsDao();

			updateStockToRack.setVisible(false);

			rackStockTable = new STable(null, 600, 200);

			stkrakmapGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			rackStockTable.setSizeFull();
			rackStockTable.setSelectable(true);
			rackStockTable.setMultiSelect(true);

			rackStockTable.setWidth("660px");
			rackStockTable.setHeight("180px");

			allHeaders = new String[] { TSR_SN, TSR_RACK_ID, TSR_RACK_NAME,
					TSR_STOCK_ID, TSR_STOCK_DETAILS, TSR_QTY, TSR_UNIT_ID,
					TSR_UNIT_NAME, TSR_QUANTITY_IN_BASIC_UNIT };
			requiredHeaders = new String[] { TSR_SN, TSR_RACK_NAME,
					TSR_STOCK_DETAILS, TSR_QTY, TSR_UNIT_NAME };

			// if(purchase_id!=0) {
			// modeSelect=new SRadioButton("Mode", 500,
			// SConstants.stoc_rack_map.purchase_mode, "key", "value");
			// }
			// else{
			// modeSelect=new SRadioButton("Mode", 500,
			// SConstants.stoc_rack_map.common_modes, "key", "value");
			// }

			// modeSelect.addStyleName("radio_horizontal");

			buildingSelect = new SComboField(
					getPropertyName("building"),
					100,
					bldDao.getAllActiveBuildingNamesUnderOffice((Long) getHttpSession()
							.getAttribute("office_id")), "id", "name", false,
					"Select");

			roomSelect = new SComboField(getPropertyName("room"), 100,
					rmDao.getAllRoomNames(), "id", "room_number", false,
					"Select");

			// stkrkVLay.addComponent(modeSelect);

			rackStockTable.addContainerProperty(TSR_SN, Integer.class, null,
					"#", null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_RACK_ID, Long.class, null,
					getPropertyName("rack_id"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_RACK_NAME, String.class,
					null, getPropertyName("rack_no"), null, Align.LEFT);
			rackStockTable.addContainerProperty(TSR_STOCK_ID, Long.class, null,
					getPropertyName("stock_id"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_STOCK_DETAILS,
					String.class, null, getPropertyName("stock"), null,
					Align.LEFT);
			rackStockTable.addContainerProperty(TSR_QTY, Double.class, null,
					getPropertyName("qty"), null, Align.RIGHT);

			// rackStockTable.addContainerProperty(TSR_SN, Integer.class,
			// null,"#", null, Align.CENTER);
			// rackStockTable.addContainerProperty(TSR_RACK_ID, Long.class,
			// null,TSR_RACK_ID , null, Align.CENTER);
			// rackStockTable.addContainerProperty(TSR_RACK_NAME, String.class,
			// null,TSR_RACK_NAME , null, Align.LEFT);
			// rackStockTable.addContainerProperty(TSR_STOCK_ID, Long.class,
			// null,TSR_STOCK_ID , null, Align.CENTER);
			// rackStockTable.addContainerProperty(TSR_STOCK_DETAILS,
			// String.class, null,TSR_STOCK_DETAILS , null, Align.LEFT);
			// rackStockTable.addContainerProperty(TSR_QTY, Double.class,
			// null,TSR_QTY , null, Align.RIGHT);

			rackStockTable.addContainerProperty(TSR_UNIT_ID, Long.class, null,
					TSR_UNIT_ID, null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_UNIT_NAME, String.class,
					null, getPropertyName("unit"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_QUANTITY_IN_BASIC_UNIT,
					Double.class, null, getPropertyName("qty_basic_unit"),
					null, Align.RIGHT);

			rackStockTable.setColumnExpandRatio(TSR_SN, (float) .5);
			rackStockTable.setColumnExpandRatio(TSR_STOCK_DETAILS, 2);
			rackStockTable.setColumnExpandRatio(TSR_RACK_NAME, 1);
			rackStockTable.setColumnExpandRatio(TSR_QTY, 1);

			stkrakmapGrid.setColumns(8);
			stkrakmapGrid.setRows(2);

			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);

			stockSelect = new SComboField(null, 250, stockList, "id",
					"details", true, "Select");

			rackSelect = new SComboField(getPropertyName("rack"), 120,
					new RackDao().getAllActiveRacks(), "id", "rack_number",
					true, "Select");

			quantityInRackTextField = new STextField(
					getPropertyName("quantity"), 80);
			// stkrakmapGrid.addComponent();
			stkrakmapGrid.addComponent(buildingSelect);
			stkrakmapGrid.addComponent(roomSelect);
			stkrakmapGrid.addComponent(rackSelect);
			stkrakmapGrid.addComponent(unitSelect);
			stkrakmapGrid.addComponent(quantityInRackTextField);
			stkrakmapGrid.addComponent(addStockToRack);
			stkrakmapGrid.addComponent(updateStockToRack);

			quantityInRackTextField.setStyleName("textfield_align_right");

			stkrakmapGrid.setComponentAlignment(addStockToRack,
					Alignment.BOTTOM_RIGHT);
			stkrakmapGrid.setComponentAlignment(updateStockToRack,
					Alignment.BOTTOM_RIGHT);

			stkrakmapGrid.setSpacing(true);

			SGridLayout miniGrid = new SGridLayout();
			miniGrid.setColumns(4);
			miniGrid.setRows(1);
			SLabel lb = new SLabel();
			lb.setValue("Stock :");
			miniGrid.addComponent(lb, 1, 0);
			miniGrid.addComponent(stockSelect, 2, 0);
			miniGrid.setWidth("400px");
			// miniGrid.setComponentAlignment(stockSelect,
			// Alignment.TOP_CENTER);

			stkrkVLay.addComponent(miniGrid);
			stkrkVLay.setComponentAlignment(miniGrid, Alignment.TOP_CENTER);
			stkrkVLay.addComponent(rackStockTable);
			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(stkrakmapGrid);
			stkrkVLay.addComponent(saveStockToRack);
			stkrkVLay.setComponentAlignment(saveStockToRack,
					Alignment.BOTTOM_CENTER);

			rackStockTable.setVisibleColumns(requiredHeaders);

			setContent(stkrkVLay);

			rackStockTable.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					Collection selectedItems = null;

					if (rackStockTable.getValue() != null) {
						selectedItems = (Collection) rackStockTable.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = rackStockTable.getItem(selectedItems
								.iterator().next());

						// if(item.getItemProperty(
						// TSR_MAP_ID).getValue().toString().equals("0")) {
						stockSelect.setNewValue(item.getItemProperty(
								TSR_STOCK_ID).getValue());
						rackSelect.setNewValue(item
								.getItemProperty(TSR_RACK_ID).getValue());
						quantityInRackTextField.setValue(""
								+ item.getItemProperty(TSR_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TSR_UNIT_ID)
								.getValue());

						updateStockToRack.setVisible(true);
						addStockToRack.setVisible(false);

					} else {
						updateStockToRack.setVisible(false);
						addStockToRack.setVisible(true);
						rackSelect.setValue(null);
						quantityInRackTextField.setValue("");
						setDefaultValues();
					}
				}
			});

			updateStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(rackStockTable, null, false);
						if (isStockRackValid()) {

							double total_qty = 0;
							rackStockTable.setVisibleColumns(allHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							Collection selectedItems = null;

							if (rackStockTable.getValue() != null) {
								selectedItems = (Collection) rackStockTable
										.getValue();
							}

							if (selectedItems != null
									&& selectedItems.size() == 1) {

								int sel_id = (Integer) selectedItems.iterator()
										.next();
								Item item = rackStockTable.getItem(sel_id);

								Item itm;
								int id = 0, ct = rackStockTable.getItemIds()
										.size();
								Iterator it1 = rackStockTable.getItemIds()
										.iterator();
								while (it1.hasNext()) {
									id = (Integer) it1.next();

									itm = rackStockTable.getItem(id);

									if (itm.getItemProperty(TSR_STOCK_ID)
											.getValue()
											.toString()
											.equals(stockSelect.getValue()
													.toString())) {
										if (id != sel_id)
											total_qty += (Double) itm
													.getItemProperty(
															TSR_QUANTITY_IN_BASIC_UNIT)
													.getValue();
									}
								}

								double old_bal = total_qty;

								double conv_rat = comDao.getConvertionRate(
										stock.getItem().getId(),
										(Long) unitSelect.getValue(), 0);

								total_qty += conv_rat
										* Double.parseDouble(quantityInRackTextField
												.getValue());

								id++;
								ct++;

								// RackModel rack=new
								// RackDao().getRack((Long)rackSelect.getValue());

								if (total_qty > stock.getQuantity()) {
									setRequiredError(
											quantityInRackTextField,
											getPropertyName("total_qty_greater")
													+ (stock.getQuantity() - old_bal),
											true);
								} else {
									setRequiredError(quantityInRackTextField,
											null, false);

									// if(exist){
									// Item
									// itm=rackStockTable.getItem(exist_id);
									item.getItemProperty(TSR_QTY).setValue(
											toDouble(quantityInRackTextField
													.getValue()));

									item.getItemProperty(
											TSR_QUANTITY_IN_BASIC_UNIT)
											.setValue(
													conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()));
									item.getItemProperty(TSR_UNIT_ID).setValue(
											(Long) unitSelect.getValue());
									item.getItemProperty(TSR_UNIT_NAME)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));

									item.getItemProperty(TSR_RACK_ID).setValue(
											rackSelect.getValue());
									item.getItemProperty(TSR_RACK_NAME)
											.setValue(
													rackSelect
															.getItemCaption(rackSelect
																	.getValue()));

									updateStockToRack.setVisible(false);
									addStockToRack.setVisible(true);

									rackStockTable.setValue(null);

									rackSelect.setValue(null);
									quantityInRackTextField.setValue("");
									stockSelect.focus();

								}
								rackStockTable
										.setVisibleColumns(requiredHeaders);

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			stockSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						rackStockTable.removeAllItems();
						if (stockSelect.getValue() != null) {

							rackStockTable.setVisibleColumns(allHeaders);

							StockRackMappingModel srm;
							int ct = 0, id = 0;
							Iterator it1 = daoObj.getStockRackMappings(
									(Long) stockSelect.getValue()).iterator();
							while (it1.hasNext()) {
								ct++;
								id++;
								srm = (StockRackMappingModel) it1.next();
								rackStockTable.addItem(
										new Object[] {
												ct,
												srm.getRack().getId(),
												srm.getRack().getRack_number(),
												srm.getStock().getId(),
												srm.getStock().getId()
														+ " ; "
														+ srm.getStock()
																.getItem()
																.getName()
														+ " Exp: "
														+ srm.getStock()
																.getExpiry_date(),
												srm.getQuantity(),
												srm.getUnit_id(),
												untDao.getUnit(srm.getUnit_id())
														.getSymbol(),
												srm.getQuantity_in_basic_unit() },
										id);
							}

							rackStockTable.setVisibleColumns(requiredHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							SCollectionContainer bic = SCollectionContainer
									.setList(comDao.getAllItemUnitDetails(stock
											.getItem().getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							try {
								unitSelect.setValue(stock.getItem().getUnit()
										.getId());
							} catch (Exception e) {
								unitSelect.setValue(unitSelect.getItemIds()
										.iterator().next());
							}

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

			addStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(rackStockTable, null, false);
						if (isStockRackValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							rackStockTable.setVisibleColumns(allHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							// double
							// conv_rate=comDao.getConvertionRate(stock.getItem().getId(),
							// unit_id, 0);

							Item itm;
							int id = 0, ct = rackStockTable.getItemIds().size();
							Iterator it1 = rackStockTable.getItemIds()
									.iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								itm = rackStockTable.getItem(id);
								if (itm.getItemProperty(TSR_RACK_ID)
										.getValue()
										.toString()
										.equals(rackSelect.getValue()
												.toString())
										&& itm.getItemProperty(TSR_UNIT_ID)
												.getValue()
												.toString()
												.equals(unitSelect.getValue()
														.toString())) {
									exist = true;
									exist_id = id;
								}
								total_qty += (Double) itm.getItemProperty(
										TSR_QUANTITY_IN_BASIC_UNIT).getValue();
							}

							double old_bal = total_qty;

							double conv_rat = comDao.getConvertionRate(stock
									.getItem().getId(), (Long) unitSelect
									.getValue(), 0);

							total_qty += conv_rat
									* Double.parseDouble(quantityInRackTextField
											.getValue());

							id++;
							ct++;

							// RackModel rack=new
							// RackDao().getRack((Long)rackSelect.getValue());

							if (total_qty > stock.getQuantity()) {
								setRequiredError(
										quantityInRackTextField,
										getPropertyName("total_qty_greater")
												+ (stock.getQuantity() - old_bal),
										true);
							} else {
								setRequiredError(quantityInRackTextField, null,
										false);

								if (exist) {
									itm = rackStockTable.getItem(exist_id);
									itm.getItemProperty(TSR_QTY)
											.setValue(
													(Double) itm
															.getItemProperty(
																	TSR_QTY)
															.getValue()
															+ Double.parseDouble(quantityInRackTextField
																	.getValue()));

									itm.getItemProperty(
											TSR_QUANTITY_IN_BASIC_UNIT)
											.setValue(
													(Double) itm
															.getItemProperty(
																	TSR_QUANTITY_IN_BASIC_UNIT)
															.getValue()
															+ conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()));
									itm.getItemProperty(TSR_UNIT_ID).setValue(
											(Long) unitSelect.getValue());
									itm.getItemProperty(TSR_UNIT_NAME)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));

								} else {
									rackStockTable.addItem(
											new Object[] {
													ct,
													(Long) rackSelect
															.getValue(),
													rackSelect
															.getItemCaption(rackSelect
																	.getValue()),
													(Long) stockSelect
															.getValue(),
													stock.getId()
															+ " ; "
															+ stock.getItem()
																	.getName()
															+ " Exp: "
															+ stock.getExpiry_date(),
													Double.parseDouble(quantityInRackTextField
															.getValue()),
													(Long) unitSelect
															.getValue(),
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()),
													conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()) },
											id);

								}

								// stockSelect.setValue(null);
								rackSelect.setValue(null);
								quantityInRackTextField.setValue("");
								stockSelect.focus();

							}
							rackStockTable.setVisibleColumns(requiredHeaders);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			saveStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (stockSelect.getValue() != null) {

							List mapList = new ArrayList();
							Iterator it1 = rackStockTable.getItemIds()
									.iterator();

							// if(rackStockTable.getItemIds().size()>0) {
							Item itm;
							StockRackMappingModel srObj;
							while (it1.hasNext()) {
								itm = rackStockTable.getItem(it1.next());

								srObj = new StockRackMappingModel();
								srObj.setQuantity((Double) itm.getItemProperty(
										TSR_QTY).getValue());
								srObj.setRack(new RackModel((Long) itm
										.getItemProperty(TSR_RACK_ID)
										.getValue()));
								srObj.setStock(new ItemStockModel((Long) itm
										.getItemProperty(TSR_STOCK_ID)
										.getValue()));

								srObj.setQuantity_in_basic_unit((Double) itm
										.getItemProperty(
												TSR_QUANTITY_IN_BASIC_UNIT)
										.getValue());
								srObj.setBalance((Double) itm.getItemProperty(
										TSR_QUANTITY_IN_BASIC_UNIT).getValue());
								srObj.setUnit_id((Long) itm.getItemProperty(
										TSR_UNIT_ID).getValue());

								mapList.add(srObj);
								// }
							}

							daoObj.saveStockRackMap(mapList,
									(Long) stockSelect.getValue());

							Notification.show(
									getPropertyName("mapped_success"),
									Type.WARNING_MESSAGE);

							Object temp = stockSelect.getValue();

							rackStockTable.removeAllItems();
							// if((Long) modeSelect.getValue()==3) {
							stockSelect
									.setContainerDataSource(SCollectionContainer.setList(
											new StockReceiveDao()
													.getStockListOfTransfer(transfer_id),
											"id"));
							stockSelect.setItemCaptionPropertyId("details");
							/*
							 * } else if((Long) modeSelect.getValue()==2) {
							 * stockSelect
							 * .setContainerDataSource(SCollectionContainer
							 * .setList( daoObj.getArrangedAvailabeStocksList(),
							 * "id"));
							 * stockSelect.setItemCaptionPropertyId("details");
							 * }
							 */

							stockSelect.setValue(temp);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			rackStockTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					rackStockTable.setValue(null);
				}
			});

			final Action actionDeleteStock = new Action("Delete");

			rackStockTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteStockRack();
				}

			});

			buildingSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						roomSelect.setContainerDataSource(SCollectionContainer.setList(
								rmDao.getAllRoomNamesFromBuilding((Long) buildingSelect
										.getValue()), "id"));
						roomSelect.setItemCaptionPropertyId("room_number");

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			roomSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						rackSelect.setContainerDataSource(SCollectionContainer.setList(
								new RackDao()
										.getAllActiveRacksUnderRoom((Long) roomSelect
												.getValue()), "id"));
						rackSelect.setItemCaptionPropertyId("rack_number");

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteStockRack();
				}
			});

			setDefaultValues();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated constructor stub
	}

	public void deleteStockRack() {
		try {

			if (rackStockTable.getValue() != null) {

				Collection selectedItems = (Collection) rackStockTable
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					rackStockTable.removeItem(it1.next());
				}

				Item newitem;
				int SN = 0;
				Iterator it = rackStockTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = rackStockTable.getItem((Integer) it.next());

					newitem.getItemProperty(TSR_SN).setValue(SN);

				}
			}
			rackSelect.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isStockRackValid() {
		boolean ret = true;

		if (quantityInRackTextField.getValue() == null
				|| quantityInRackTextField.getValue().equals("")) {
			setRequiredError(quantityInRackTextField,
					getPropertyName("invalid_data"), true);
			quantityInRackTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(quantityInRackTextField.getValue()) <= 0) {
					setRequiredError(quantityInRackTextField,
							getPropertyName("qty_greater_zero"), true);
					quantityInRackTextField.focus();
					ret = false;
				} else
					setRequiredError(quantityInRackTextField, null, false);
			} catch (Exception e) {
				setRequiredError(quantityInRackTextField,
						getPropertyName("invalid_data"), true);
				quantityInRackTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (rackSelect.getValue() == null || rackSelect.getValue().equals("")) {
			setRequiredError(rackSelect, getPropertyName("invalid_selection"),
					true);
			rackSelect.focus();
			ret = false;
		} else
			setRequiredError(rackSelect, null, false);

		if (stockSelect.getValue() == null || stockSelect.getValue().equals("")) {
			setRequiredError(stockSelect, getPropertyName("invalid_selection"),
					true);
			stockSelect.focus();
			ret = false;
		} else
			setRequiredError(stockSelect, null, false);

		return ret;
	}

	// Added By Anil

	public StockRackMappingPannel(List salesStkList, String str) {

		super();

		setSize(700, 400);
		try {
			comDao = new CommonMethodsDao();
			unitSelect = new SNativeSelect("Unit", 60);
			allHeaders = new String[] { TSR_SN, TSR_RACK_ID, TSR_RACK_NAME,
					TSR_STOCK_ID, TSR_STOCK_DETAILS, TSR_QTY, TSR_UNIT_ID,
					TSR_UNIT_NAME, TSR_QUANTITY_IN_BASIC_UNIT };
			requiredHeaders = new String[] { TSR_SN, TSR_RACK_NAME,
					TSR_STOCK_DETAILS, TSR_QTY, TSR_UNIT_NAME };

			updateStockToRack.setVisible(false);

			rackStockTable = new STable(null, 600, 200);

			stkrakmapGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			rackStockTable.setSizeFull();
			rackStockTable.setSelectable(true);
			rackStockTable.setMultiSelect(true);

			rackStockTable.setWidth("660px");
			rackStockTable.setHeight("180px");

			buildingSelect = new SComboField(
					"Building",
					100,
					bldDao.getAllActiveBuildingNamesUnderOffice((Long) getHttpSession()
							.getAttribute("office_id")), "id", "name", false,
					"Select");

			roomSelect = new SComboField("Room", 100, rmDao.getAllRoomNames(),
					"id", "room_number", false, "Select");

			rackStockTable.addContainerProperty(TSR_SN, Integer.class, null,
					"#", null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_RACK_ID, Long.class, null,
					TSR_RACK_ID, null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_RACK_NAME, String.class,
					null, getPropertyName("rack_no"), null, Align.LEFT);
			rackStockTable.addContainerProperty(TSR_STOCK_ID, Long.class, null,
					TSR_STOCK_ID, null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_STOCK_DETAILS,
					String.class, null, getPropertyName("stock"), null,
					Align.LEFT);
			rackStockTable.addContainerProperty(TSR_QTY, Double.class, null,
					getPropertyName("qty"), null, Align.RIGHT);

			rackStockTable.addContainerProperty(TSR_UNIT_ID, Long.class, null,
					TSR_UNIT_ID, null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_UNIT_NAME, String.class,
					null, getPropertyName("unit"), null, Align.CENTER);
			rackStockTable.addContainerProperty(TSR_QUANTITY_IN_BASIC_UNIT,
					Double.class, null, TSR_QUANTITY_IN_BASIC_UNIT, null,
					Align.RIGHT);

			rackStockTable.setColumnExpandRatio(TSR_SN, (float) .5);
			rackStockTable.setColumnExpandRatio(TSR_STOCK_DETAILS, 2);
			rackStockTable.setColumnExpandRatio(TSR_RACK_NAME, 1);
			rackStockTable.setColumnExpandRatio(TSR_QTY, 1);

			stkrakmapGrid.setColumns(8);
			stkrakmapGrid.setRows(2);

			salesStockList = new ArrayList();
			ItemStockModel stkModel = null;
			for (int i = 0; i < salesStkList.size(); i++) {
				stkModel = (ItemStockModel) salesStkList.get(i);
				salesStockList.add(stkModel.getId());
			}

			List<ItemStockModel> stockList = daoObj
					.getStockDetails(salesStockList);

			stockSelect = new SComboField(null, 250, stockList, "id",
					"details", true, "Select");

			rackSelect = new SComboField("Rack", 120,
					new RackDao().getAllActiveRacks(), "id", "rack_number",
					true, "Select");

			quantityInRackTextField = new STextField(
					getPropertyName("quantity"), 80);
			// stkrakmapGrid.addComponent();
			stkrakmapGrid.addComponent(buildingSelect);
			stkrakmapGrid.addComponent(roomSelect);
			stkrakmapGrid.addComponent(rackSelect);
			stkrakmapGrid.addComponent(unitSelect);
			stkrakmapGrid.addComponent(quantityInRackTextField);
			stkrakmapGrid.addComponent(addStockToRack);
			stkrakmapGrid.addComponent(updateStockToRack);

			quantityInRackTextField.setStyleName("textfield_align_right");

			stkrakmapGrid.setComponentAlignment(addStockToRack,
					Alignment.BOTTOM_RIGHT);
			stkrakmapGrid.setComponentAlignment(updateStockToRack,
					Alignment.BOTTOM_RIGHT);

			stkrakmapGrid.setSpacing(true);

			SGridLayout miniGrid = new SGridLayout();
			miniGrid.setColumns(4);
			miniGrid.setRows(1);
			SLabel lb = new SLabel();
			lb.setValue("Stock :");
			miniGrid.addComponent(lb, 1, 0);
			miniGrid.addComponent(stockSelect, 2, 0);
			miniGrid.setWidth("400px");
			// miniGrid.setComponentAlignment(stockSelect,
			// Alignment.TOP_CENTER);

			stkrkVLay.addComponent(miniGrid);
			stkrkVLay.setComponentAlignment(miniGrid, Alignment.TOP_CENTER);
			stkrkVLay.addComponent(rackStockTable);
			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(stkrakmapGrid);
			stkrkVLay.addComponent(saveStockToRack);
			stkrkVLay.setComponentAlignment(saveStockToRack,
					Alignment.BOTTOM_CENTER);

			rackStockTable.setVisibleColumns(requiredHeaders);

			setContent(stkrkVLay);

			rackStockTable.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					Collection selectedItems = null;

					if (rackStockTable.getValue() != null) {
						selectedItems = (Collection) rackStockTable.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = rackStockTable.getItem(selectedItems
								.iterator().next());

						// if(item.getItemProperty(
						// TSR_MAP_ID).getValue().toString().equals("0")) {
						stockSelect.setNewValue(item.getItemProperty(
								TSR_STOCK_ID).getValue());
						rackSelect.setNewValue(item
								.getItemProperty(TSR_RACK_ID).getValue());
						quantityInRackTextField.setValue(""
								+ item.getItemProperty(TSR_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TSR_UNIT_ID)
								.getValue());

						updateStockToRack.setVisible(true);
						addStockToRack.setVisible(false);

					} else {
						updateStockToRack.setVisible(false);
						addStockToRack.setVisible(true);
						rackSelect.setValue(null);
						quantityInRackTextField.setValue("");
						setDefaultValues();
					}
				}
			});

			updateStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(rackStockTable, null, false);
						if (isStockRackValid()) {

							double total_qty = 0;
							rackStockTable.setVisibleColumns(allHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							Collection selectedItems = null;

							if (rackStockTable.getValue() != null) {
								selectedItems = (Collection) rackStockTable
										.getValue();
							}

							if (selectedItems != null
									&& selectedItems.size() == 1) {

								int sel_id = (Integer) selectedItems.iterator()
										.next();
								Item item = rackStockTable.getItem(sel_id);

								Item itm;
								int id = 0, ct = rackStockTable.getItemIds()
										.size();
								Iterator it1 = rackStockTable.getItemIds()
										.iterator();
								while (it1.hasNext()) {
									id = (Integer) it1.next();

									itm = rackStockTable.getItem(id);

									if (itm.getItemProperty(TSR_STOCK_ID)
											.getValue()
											.toString()
											.equals(stockSelect.getValue()
													.toString())) {
										if (id != sel_id)
											total_qty += (Double) itm
													.getItemProperty(
															TSR_QUANTITY_IN_BASIC_UNIT)
													.getValue();
									}
								}

								double old_bal = total_qty;

								double conv_rat = comDao.getConvertionRate(
										stock.getItem().getId(),
										(Long) unitSelect.getValue(), 0);

								total_qty += conv_rat
										* Double.parseDouble(quantityInRackTextField
												.getValue());

								id++;
								ct++;

								// RackModel rack=new
								// RackDao().getRack((Long)rackSelect.getValue());

								if (total_qty > stock.getQuantity()) {
									setRequiredError(
											quantityInRackTextField,
											getPropertyName("total_qty_greater")
													+ (stock.getQuantity() - old_bal),
											true);
								} else {
									setRequiredError(quantityInRackTextField,
											null, false);

									// if(exist){
									// Item
									// itm=rackStockTable.getItem(exist_id);
									item.getItemProperty(TSR_QTY).setValue(
											toDouble(quantityInRackTextField
													.getValue()));

									item.getItemProperty(
											TSR_QUANTITY_IN_BASIC_UNIT)
											.setValue(
													conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()));
									item.getItemProperty(TSR_UNIT_ID).setValue(
											(Long) unitSelect.getValue());
									item.getItemProperty(TSR_UNIT_NAME)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));

									item.getItemProperty(TSR_RACK_ID).setValue(
											rackSelect.getValue());
									item.getItemProperty(TSR_RACK_NAME)
											.setValue(
													rackSelect
															.getItemCaption(rackSelect
																	.getValue()));

									updateStockToRack.setVisible(false);
									addStockToRack.setVisible(true);

									rackStockTable.setValue(null);

									rackSelect.setValue(null);
									quantityInRackTextField.setValue("");
									stockSelect.focus();

								}
								rackStockTable
										.setVisibleColumns(requiredHeaders);

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			stockSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						rackStockTable.removeAllItems();
						if (stockSelect.getValue() != null) {

							rackStockTable.setVisibleColumns(allHeaders);

							StockRackMappingModel srm;
							int ct = 0, id = 0;
							Iterator it1 = daoObj.getStockRackMappings(
									(Long) stockSelect.getValue()).iterator();
							while (it1.hasNext()) {
								ct++;
								id++;
								srm = (StockRackMappingModel) it1.next();
								rackStockTable.addItem(
										new Object[] {
												ct,
												srm.getRack().getId(),
												srm.getRack().getRack_number(),
												srm.getStock().getId(),
												srm.getStock().getId()
														+ " ; "
														+ srm.getStock()
																.getItem()
																.getName()
														+ " Exp: "
														+ srm.getStock()
																.getExpiry_date(),
												srm.getQuantity(),
												srm.getUnit_id(),
												untDao.getUnit(srm.getUnit_id())
														.getSymbol(),
												srm.getQuantity_in_basic_unit() },
										id);
							}

							rackStockTable.setVisibleColumns(requiredHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							SCollectionContainer bic = SCollectionContainer
									.setList(comDao.getAllItemUnitDetails(stock
											.getItem().getId()), "id");
							unitSelect.setContainerDataSource(bic);
							unitSelect.setItemCaptionPropertyId("symbol");

							try {
								unitSelect.setValue(stock.getItem().getUnit()
										.getId());
							} catch (Exception e) {
								unitSelect.setValue(unitSelect.getItemIds()
										.iterator().next());
							}

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			addStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(rackStockTable, null, false);
						if (isStockRackValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							rackStockTable.setVisibleColumns(allHeaders);

							ItemStockModel stock = daoObj
									.getItemStock((Long) stockSelect.getValue());

							// double
							// conv_rate=comDao.getConvertionRate(stock.getItem().getId(),
							// unit_id, 0);

							Item itm;
							int id = 0, ct = rackStockTable.getItemIds().size();
							Iterator it1 = rackStockTable.getItemIds()
									.iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();

								itm = rackStockTable.getItem(id);
								if (itm.getItemProperty(TSR_RACK_ID)
										.getValue()
										.toString()
										.equals(rackSelect.getValue()
												.toString())
										&& itm.getItemProperty(TSR_UNIT_ID)
												.getValue()
												.toString()
												.equals(unitSelect.getValue()
														.toString())) {
									exist = true;
									exist_id = id;
								}
								total_qty += (Double) itm.getItemProperty(
										TSR_QUANTITY_IN_BASIC_UNIT).getValue();
							}

							double old_bal = total_qty;

							double conv_rat = comDao.getConvertionRate(stock
									.getItem().getId(), (Long) unitSelect
									.getValue(), 0);

							total_qty += conv_rat
									* Double.parseDouble(quantityInRackTextField
											.getValue());

							id++;
							ct++;

							// RackModel rack=new
							// RackDao().getRack((Long)rackSelect.getValue());

							if (total_qty > stock.getQuantity()) {
								setRequiredError(
										quantityInRackTextField,
										getPropertyName("total_qty_greater")
												+ (stock.getQuantity() - old_bal),
										true);
							} else {
								setRequiredError(quantityInRackTextField, null,
										false);

								if (exist) {
									itm = rackStockTable.getItem(exist_id);
									itm.getItemProperty(TSR_QTY)
											.setValue(
													(Double) itm
															.getItemProperty(
																	TSR_QTY)
															.getValue()
															+ Double.parseDouble(quantityInRackTextField
																	.getValue()));

									itm.getItemProperty(
											TSR_QUANTITY_IN_BASIC_UNIT)
											.setValue(
													(Double) itm
															.getItemProperty(
																	TSR_QUANTITY_IN_BASIC_UNIT)
															.getValue()
															+ conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()));
									itm.getItemProperty(TSR_UNIT_ID).setValue(
											(Long) unitSelect.getValue());
									itm.getItemProperty(TSR_UNIT_NAME)
											.setValue(
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()));

								} else {
									rackStockTable.addItem(
											new Object[] {
													ct,
													(Long) rackSelect
															.getValue(),
													rackSelect
															.getItemCaption(rackSelect
																	.getValue()),
													(Long) stockSelect
															.getValue(),
													stock.getId()
															+ " ; "
															+ stock.getItem()
																	.getName()
															+ " Exp: "
															+ stock.getExpiry_date(),
													Double.parseDouble(quantityInRackTextField
															.getValue()),
													(Long) unitSelect
															.getValue(),
													unitSelect
															.getItemCaption(unitSelect
																	.getValue()),
													conv_rat
															* toDouble(quantityInRackTextField
																	.getValue()) },
											id);

								}

								// stockSelect.setValue(null);
								rackSelect.setValue(null);
								quantityInRackTextField.setValue("");
								stockSelect.focus();

							}
							rackStockTable.setVisibleColumns(requiredHeaders);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			saveStockToRack.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (stockSelect.getValue() != null) {

							List mapList = new ArrayList();
							Iterator it1 = rackStockTable.getItemIds()
									.iterator();

							// if(rackStockTable.getItemIds().size()>0) {

							Item itm;
							StockRackMappingModel srObj;
							while (it1.hasNext()) {
								itm = rackStockTable.getItem(it1.next());

								srObj = new StockRackMappingModel();
								srObj.setQuantity((Double) itm.getItemProperty(
										TSR_QTY).getValue());
								srObj.setRack(new RackModel((Long) itm
										.getItemProperty(TSR_RACK_ID)
										.getValue()));
								srObj.setStock(new ItemStockModel((Long) itm
										.getItemProperty(TSR_STOCK_ID)
										.getValue()));

								srObj.setQuantity_in_basic_unit((Double) itm
										.getItemProperty(
												TSR_QUANTITY_IN_BASIC_UNIT)
										.getValue());
								srObj.setBalance((Double) itm.getItemProperty(
										TSR_QUANTITY_IN_BASIC_UNIT).getValue());
								srObj.setUnit_id((Long) itm.getItemProperty(
										TSR_UNIT_ID).getValue());

								mapList.add(srObj);
								// }
							}

							daoObj.saveStockRackMap(mapList,
									(Long) stockSelect.getValue());

							Notification.show(getPropertyName("Success"),
									getPropertyName("mapped_success"),
									Type.WARNING_MESSAGE);

							Object temp = stockSelect.getValue();

							rackStockTable.removeAllItems();
							stockSelect
									.setContainerDataSource(SCollectionContainer.setList(
											daoObj.getStockDetails(salesStockList),
											"id"));
							stockSelect.setItemCaptionPropertyId("details");

							stockSelect.setValue(temp);

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			rackStockTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					rackStockTable.setValue(null);
				}
			});

			final Action actionDeleteStock = new Action("Delete");

			rackStockTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteStockRack();
				}

			});

			buildingSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						roomSelect.setContainerDataSource(SCollectionContainer.setList(
								rmDao.getAllRoomNamesFromBuilding((Long) buildingSelect
										.getValue()), "id"));
						roomSelect.setItemCaptionPropertyId("room_number");

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			roomSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						rackSelect.setContainerDataSource(SCollectionContainer.setList(
								new RackDao()
										.getAllActiveRacksUnderRoom((Long) roomSelect
												.getValue()), "id"));
						rackSelect.setItemCaptionPropertyId("rack_number");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDefaultValues() {
		Iterator itr = buildingSelect.getItemIds().iterator();
		if (itr.hasNext()) {
			buildingSelect.setValue(itr.next());
		}

		itr = null;
		itr = roomSelect.getItemIds().iterator();
		if (itr.hasNext()) {
			roomSelect.setValue(itr.next());
		}

	}

}
