package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemTransferDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ItemTransferInventoryDetails;
import com.inventory.config.stock.model.ItemTransferModel;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
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
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.CommonUtil;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 18, 2013
 */
public class ItemTransferPanel extends SContainerPanel {

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_TO_OFFICE_ID = "To Office ID";
	static String TBC_TO_OFFICE_NAME = "To Office";
	static String TBC_EXISTING_QTY = "Existing Qty";
	static String TBC_STOCK_ID = "Stk ID";

	ItemTransferDao daoObj = new ItemTransferDao();

	SComboField itemTransferNumberList;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;

	SComboField officeCombo;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveStockTransfer;
	SButton updateStockTransfer;
	SButton deleteStockTransfer;

	CommonMethodsDao comDao;

	OfficeDao pfcDao = new OfficeDao();

	ItemDao itemDao;

	SDateField date;
	SComboField itemSelect;

	SPopupView pop;

	UnitDao unitDao = new UnitDao();

	SFormLayout popLay;

	SettingsValuePojo settings;

	STextArea comment;

	private SButton changeStkButton;
	SListSelect stockSelectList;

	@SuppressWarnings("deprecation")
	public ItemTransferPanel() {

		setId("Transfer");
		setSizeFull();

		comDao = new CommonMethodsDao();

		daoObj = new ItemTransferDao();
		itemDao = new ItemDao();

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
		masterDetailsGrid.setRows(1);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);

		popLay = new SFormLayout();

		pop = new SPopupView("", popLay);

		form.setSizeFull();

		try {

			if (getHttpSession().getAttribute("settings") != null)
				settings = (SettingsValuePojo) getHttpSession().getAttribute(
						"settings");

			stockSelectList = new SListSelect(getPropertyName("stock"), 300,
					200);
			stockSelectList.setNullSelectionAllowed(false);

			changeStkButton = new SButton();
			changeStkButton.setStyleName("loadAllBtnStyle");
			changeStkButton.setDescription(getPropertyName("change_stock"));

			List list = new ArrayList();
			list.add(new StockTransferModel(0, "----Create New-----"));
			list.addAll(daoObj
					.getAllStockTransferNumbersAsComment(getOfficeID()));
			itemTransferNumberList = new SComboField(null, 125, list, "id",
					"comments", false, getPropertyName("create_new"));

			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date());

			officeCombo = new SComboField(getPropertyName("to_offc"), 125,
					pfcDao.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name", false, getPropertyName("select"));
			masterDetailsGrid.setHeight("25");
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("stock_transfr_no")), 1, 0);
			masterDetailsGrid.addComponent(itemTransferNumberList, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 6, 0);
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
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			itemSelect = new SComboField(
					getPropertyName("items"),
					250,
					daoObj.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
					"id", "name", true, getPropertyName("select"));

			addItemButton = new SButton(null, getPropertyName("add_item"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(itemSelect);
			addingGrid.addComponent(changeStkButton);
			addingGrid.addComponent(popLay);

			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			addingGrid.addComponent(officeCombo);
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

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

//			form.setStyleName("po_style");

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
					getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_TO_OFFICE_ID, Long.class, null,
					getPropertyName("to_office_id"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TO_OFFICE_NAME, String.class, null,
					getPropertyName("to_offc"), null, Align.CENTER);
			table.addContainerProperty(TBC_EXISTING_QTY, Double.class, null,
					getPropertyName("existing_qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
					TBC_STOCK_ID, null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY, TBC_TO_OFFICE_NAME });

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("700");
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
			form.addComponent(pop);
			form.addComponent(addingGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			itemSelect.focus();

			pop.setHideOnMouseOut(false);
			changeStkButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if (itemSelect.getValue() != null
							&& unitSelect.getValue() != null) {
						try {

							popLay.removeAllComponents();
							// stockSelectList.detach();

							popLay.addComponent(stockSelectList);

							pop.setPopupVisible(true);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						popLay.removeAllComponents();
						popLay.addComponent(new SLabel(
								getPropertyName("invalid_selection")));
						pop.setPopupVisible(true);
					}
				}
			});

			saveStockTransfer.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isValid()) {

							ItemTransferModel poObj = new ItemTransferModel();

							List<ItemTransferInventoryDetails> itemsList = new ArrayList<ItemTransferInventoryDetails>();

							double conv_rat = 0;

							ItemTransferInventoryDetails invObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new ItemTransferInventoryDetails();

								item = table.getItem(it.next());
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setTo_office_id((Long) item
										.getItemProperty(TBC_TO_OFFICE_ID)
										.getValue());

								invObj.setStk_id((Long) item.getItemProperty(
										TBC_STOCK_ID).getValue());

								conv_rat = new CommonMethodsDao()
										.getConvertionRate((Long) item
												.getItemProperty(TBC_ITEM_ID)
												.getValue(), (Long) item
												.getItemProperty(TBC_UNIT_ID)
												.getValue(), 1);

								invObj.setQuantity_in_basic_unit(conv_rat
										* ((Double) item.getItemProperty(
												TBC_QTY).getValue()));

								itemsList.add(invObj);
							}

							poObj.setComments(comment.getValue());
							poObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							poObj.setLogin(new S_LoginModel(getLoginID()));
							poObj.setOffice(new S_OfficeModel(getOfficeID()));
							poObj.setStatus(1);
							poObj.setInventory_details_list(itemsList);

							poObj.setTransfer_no(getNextSequence(
									"Stock Transfer Number", getLoginID()));

							long id = daoObj.save(poObj);

							loadStockTransfer(id);

							reloadStock();

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			itemTransferNumberList
					.addValueChangeListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								removeAllErrors();

								updateStockTransfer.setVisible(true);
								deleteStockTransfer.setVisible(true);
								saveStockTransfer.setVisible(false);
								if (itemTransferNumberList.getValue() != null
										&& !itemTransferNumberList.getValue()
												.toString().equals("0")) {

									ItemTransferModel poObj = daoObj
											.getItemTransfer((Long) itemTransferNumberList
													.getValue());

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_QTY,
											TBC_UNIT_ID, TBC_UNIT,
											TBC_TO_OFFICE_ID,
											TBC_TO_OFFICE_NAME,
											TBC_EXISTING_QTY, TBC_STOCK_ID });

									table.removeAllItems();

									Iterator it = poObj
											.getInventory_details_list()
											.iterator();
									ItemTransferInventoryDetails invObj;
									while (it.hasNext()) {
										invObj = (ItemTransferInventoryDetails) it
												.next();

										table.addItem(
												new Object[] {
														table.getItemIds()
																.size() + 1,
														invObj.getItem()
																.getId(),
														invObj.getItem()
																.getItem_code(),
														invObj.getItem()
																.getName(),
														invObj.getQunatity(),
														invObj.getUnit()
																.getId(),
														invObj.getUnit()
																.getSymbol(),
														invObj.getTo_office_id(),
														pfcDao.getOfficeName(invObj
																.getTo_office_id()),
														invObj.getQunatity(),
														invObj.getStk_id() },
												table.getItemIds().size() + 1);

									}

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_UNIT, TBC_QTY,
											TBC_TO_OFFICE_NAME });

									comment.setValue(poObj.getComments());
									date.setValue(poObj.getDate());

									isValid();
									updateStockTransfer.setVisible(true);
									deleteStockTransfer.setVisible(true);
									saveStockTransfer.setVisible(false);

									if (poObj.getStatus() == 2) {
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
									table.removeAllItems();

									comment.setValue("");
									date.setValue(new Date());

									saveStockTransfer.setVisible(true);
									updateStockTransfer.setVisible(false);
									deleteStockTransfer.setVisible(false);
								}

								calculateTotals();

								itemSelect.setValue(null);
								itemSelect.focus();
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

							ItemTransferModel poObj = daoObj
									.getItemTransfer((Long) itemTransferNumberList
											.getValue());

							List<ItemTransferInventoryDetails> itemsList = new ArrayList<ItemTransferInventoryDetails>();

							double conv_rat = 0;

							ItemTransferInventoryDetails invObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new ItemTransferInventoryDetails();

								item = table.getItem(it.next());
								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setTo_office_id((Long) item
										.getItemProperty(TBC_TO_OFFICE_ID)
										.getValue());

								invObj.setStk_id((Long) item.getItemProperty(
										TBC_STOCK_ID).getValue());

								conv_rat = new CommonMethodsDao()
										.getConvertionRate((Long) item
												.getItemProperty(TBC_ITEM_ID)
												.getValue(), (Long) item
												.getItemProperty(TBC_UNIT_ID)
												.getValue(), 1);

								invObj.setQuantity_in_basic_unit(conv_rat
										* ((Double) item.getItemProperty(
												TBC_QTY).getValue()));

								itemsList.add(invObj);
							}

							poObj.setComments(comment.getValue());
							poObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							poObj.setLogin(new S_LoginModel(getLoginID()));
							poObj.setOffice(new S_OfficeModel(getOfficeID()));
							poObj.setInventory_details_list(itemsList);

							daoObj.update(poObj);

							loadStockTransfer(poObj.getId());

							reloadStock();

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

					if (itemTransferNumberList.getValue() != null
							&& !itemTransferNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) itemTransferNumberList
														.getValue());
												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);
												loadStockTransfer(0);

												reloadStock();

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
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

						Item item = table.getItem(selectedItems.iterator()
								.next());

						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());

						officeCombo.setValue(item.getItemProperty(
								TBC_TO_OFFICE_ID).getValue());
						itemSelect.setValue(item.getItemProperty(TBC_ITEM_ID)
								.getValue());
						visibleAddupdateStockTransfer(false, true);

						itemSelect.focus();

						stockSelectList.setValue(item.getItemProperty(
								TBC_STOCK_ID).getValue());

					} else {
						itemSelect.setValue(null);
						quantityTextField.setValue("0.0");
						// officeCombo.setValue(null);

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

							boolean already_added_item = false, eror_hapnd = false;

							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());

							double qty = 0, totalameStockSamOfcQty = 0, totalSameStockQty = 0;

							Item item;
							Iterator itr2 = table.getItemIds().iterator();
							while (itr2.hasNext()) {
								item = table.getItem(itr2.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelect.getValue()
												.toString())) {

									if ((Double) item.getItemProperty(
											TBC_EXISTING_QTY).getValue() > 0) {
										totalameStockSamOfcQty += (Double) item
												.getItemProperty(TBC_QTY)
												.getValue()
												- (Double) item
														.getItemProperty(
																TBC_EXISTING_QTY)
														.getValue();

										if (item.getItemProperty(
												TBC_TO_OFFICE_ID)
												.getValue()
												.toString()
												.equals(officeCombo.getValue()
														.toString()))
											totalameStockSamOfcQty -= (Double) item
													.getItemProperty(
															TBC_EXISTING_QTY)
													.getValue();
									} else {
										totalSameStockQty += (Double) item
												.getItemProperty(TBC_QTY)
												.getValue();
										if (item.getItemProperty(
												TBC_TO_OFFICE_ID)
												.getValue()
												.toString()
												.equals(officeCombo.getValue()
														.toString()))
											totalameStockSamOfcQty += (Double) item
													.getItemProperty(TBC_QTY)
													.getValue();

									}

								}
							}

							qty = toDouble(quantityTextField.getValue());

							totalameStockSamOfcQty += qty;
							totalSameStockQty += qty;

							Iterator itr1 = table.getItemIds().iterator();
							List delList = new ArrayList();
							while (itr1.hasNext()) {
								item = table.getItem(itr1.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelect.getValue()
												.toString())
										&& item.getItemProperty(
												TBC_TO_OFFICE_ID)
												.getValue()
												.toString()
												.equals(officeCombo.getValue()
														.toString())) {

									if ((Double) item.getItemProperty(
											TBC_EXISTING_QTY).getValue() > 0) {

										if ((totalameStockSamOfcQty + (Double) item
												.getItemProperty(
														TBC_EXISTING_QTY)
												.getValue()) <= stk
												.getCurrent_balalnce()) {
											item.getItemProperty(TBC_QTY)
													.setValue(
															qty
																	+ (Double) item
																			.getItemProperty(
																					TBC_EXISTING_QTY)
																			.getValue());
										} else {
											eror_hapnd = true;
											setRequiredError(
													quantityTextField,
													getPropertyName("no_sufficient_bal")
															+ (stk.getCurrent_balalnce()
																	- totalameStockSamOfcQty
																	+ qty - (Double) item
																	.getItemProperty(
																			TBC_EXISTING_QTY)
																	.getValue()),
													true);
											quantityTextField.focus();
										}
									} else {
										if (totalSameStockQty <= stk
												.getCurrent_balalnce()) {
											item.getItemProperty(TBC_QTY)
													.setValue(
															totalameStockSamOfcQty);
										} else {
											eror_hapnd = true;
											setRequiredError(
													quantityTextField,
													getPropertyName("no_sufficient_bal")
															+ (stk.getCurrent_balalnce()
																	- totalSameStockQty + qty),
													true);
											quantityTextField.focus();
										}
									}
									already_added_item = true;

									break;
								}
							}

							if (!already_added_item) {

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT, TBC_TO_OFFICE_ID,
										TBC_TO_OFFICE_NAME, TBC_EXISTING_QTY,
										TBC_STOCK_ID });

								UnitModel objUnit = unitDao
										.getUnit((Long) unitSelect.getValue());

								int id = 0, ct = 0;
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									id = (Integer) it.next();
								}
								id++;

								if (totalSameStockQty <= stk
										.getCurrent_balalnce()) {

									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													stk.getId(),
													stk.getItem_code(),
													stk.getName(),
													qty,
													objUnit.getId(),
													objUnit.getSymbol(),
													(Long) officeCombo
															.getValue(),
													officeCombo
															.getItemCaption(officeCombo
																	.getValue()),
													(double) 0.0,
													(Long) stockSelectList
															.getValue() }, id);
								} else {
									setRequiredError(
											quantityTextField,
											getPropertyName("no_sufficient_bal")
													+ (stk.getCurrent_balalnce()
															- totalSameStockQty + qty),
											true);
									eror_hapnd = true;
									quantityTextField.focus();
								}

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY, TBC_TO_OFFICE_NAME });

								if (!eror_hapnd) {
									itemSelect.setValue(null);
									itemSelect.focus();
									quantityTextField.setValue("0.0");
								}
							}

							if (!eror_hapnd) {
								calculateTotals();

								itemSelect.setValue(null);
								itemSelect.focus();
								quantityTextField.setValue("0.0");
								// officeCombo.setValue(null);
							}
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
							totalUsedQty += (Double) selectedItem
									.getItemProperty(TBC_EXISTING_QTY)
									.getValue();

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
											.getValue()
											- (Double) item.getItemProperty(
													TBC_EXISTING_QTY)
													.getValue();
								}
							}
							totalSameStockQty += qty;

							if (totalSameStockQty <= (stk.getCurrent_balalnce() + totalUsedQty)) {

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
								item.getItemProperty(TBC_UNIT_ID).setValue(
										objUnit.getId());
								item.getItemProperty(TBC_UNIT).setValue(
										objUnit.getSymbol());

								item.getItemProperty(TBC_TO_OFFICE_ID)
										.setValue((Long) officeCombo.getValue());
								item.getItemProperty(TBC_TO_OFFICE_NAME)
										.setValue(
												officeCombo
														.getItemCaption(officeCombo
																.getValue()));

								item.getItemProperty(TBC_STOCK_ID).setValue(
										(Long) stockSelectList.getValue());

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY, TBC_TO_OFFICE_NAME });

								itemSelect.setValue(null);
								quantityTextField.setValue("0.0");

								visibleAddupdateStockTransfer(true, false);

								itemSelect.focus();

								table.setValue(null);

								calculateTotals();

							} else {
								setRequiredError(
										quantityTextField,
										getPropertyName("no_sufficient_bal")
												+ (stk.getCurrent_balalnce()
														+ totalUsedQty
														- totalSameStockQty + qty),
										true);
								quantityTextField.focus();
							}
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
								List stkList = null;
								if (itemSelect.getValue() != null) {
									ItemModel stk = itemDao
											.getItem((Long) itemSelect
													.getValue());
									unitSelect.setValue(stk.getUnit().getId());
								}

								List lst = comDao.getStocks(
										(Long) itemSelect.getValue(),
										settings.isUSE_SALES_RATE_FROM_STOCK());

								SCollectionContainer bic1 = SCollectionContainer
										.setList(lst, "id");
								stockSelectList.setContainerDataSource(bic1);
								stockSelectList
										.setItemCaptionPropertyId("stock_details");

								long stk_id = comDao
										.getDefaultStockToSelect((Long) itemSelect
												.getValue());

								if (stk_id != 0)
									stockSelectList.setValue(stk_id);
								else {
									Iterator it = stockSelectList.getItemIds()
											.iterator();
									if (it.hasNext())
										stockSelectList.setValue(it.next());
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							quantityTextField.focus();
							quantityTextField.selectAll();

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

			if (officeCombo.getValue() == null
					|| officeCombo.getValue().equals("")) {
				setRequiredError(officeCombo,
						getPropertyName("invalid_selection"), true);
				officeCombo.focus();
				ret = false;
			} else
				setRequiredError(officeCombo, null, false);

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

	public void loadStockTransfer(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new StockTransferModel(0, "----Create New-----"));
			list.addAll(daoObj
					.getAllStockTransferNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			itemTransferNumberList.setContainerDataSource(bic);
			itemTransferNumberList.setItemCaptionPropertyId("comments");

			itemTransferNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadStock() {
		try {

			SCollectionContainer bic = SCollectionContainer.setList(daoObj
					.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
					"id");
			itemSelect.setContainerDataSource(bic);
			itemSelect.setItemCaptionPropertyId("name");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
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

		if (ret)
			calculateTotals();
		return ret;
	}

	public void removeAllErrors() {
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		if (officeCombo.getComponentError() != null)
			setRequiredError(officeCombo, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (itemSelect.getComponentError() != null)
			setRequiredError(itemSelect, null, false);
	}

	public Boolean getHelp() {
		return null;
	}

}
