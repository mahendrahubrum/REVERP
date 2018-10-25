package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemReceiveDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ItemReceiveModel;
import com.inventory.config.stock.model.ItemTransferInventoryDetails;
import com.inventory.purchase.model.ItemStockModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
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
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;

/**
 * @author Anil K P.
 * 
 *         25 oct 2013
 */
@SuppressWarnings("serial")
public class ItemRecievePanel extends SContainerPanel {

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
	static String TBC_ITEM = "Item";
	static String TBC_WASTE = "Waste Quantity";

	ItemReceiveDao daoObj;

	SComboField stockTransferNumberList;
	SComboField itemComboField;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout masterDetailsGrid;

	SButton receiveStockTransfer;

	SButton arrangeStockTransfered;

	ItemDao itemDao;

	@SuppressWarnings("deprecation")
	public ItemRecievePanel() {

		setId("Receiver");
		
		setSizeFull();

		daoObj = new ItemReceiveDao();
		itemDao = new ItemDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(2);

		form.setSizeFull();

		try {

			stockTransferNumberList = new SComboField(null, 125,
					daoObj.getAllStockTransferNumbersAsComment(getOfficeID()),
					"id", "comments", false, getPropertyName("select"));

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("stock_transfr_no")), 1, 0);
			masterDetailsGrid.addComponent(stockTransferNumberList, 2, 0);
			masterDetailsGrid.setSpacing(true);

			masterDetailsGrid.setColumnExpandRatio(1, 2);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.setStyleName("master_border");

//			form.setStyleName("po_style");

			table = new STable(null);

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
					getPropertyName("to_office"), null, Align.CENTER);
			table.addContainerProperty(TBC_EXISTING_QTY, Double.class, null,
					getPropertyName("existing_qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM, SComboField.class, null,
					getPropertyName("item"), null, Align.LEFT);
			table.addContainerProperty(TBC_WASTE, STextField.class, null,
					getPropertyName("waste_quantity"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			table.setColumnExpandRatio(TBC_UNIT, 2);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY, TBC_ITEM, TBC_WASTE });

			table.setSizeFull();
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("750");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			receiveStockTransfer = new SButton(getPropertyName("receive"), 70);
			arrangeStockTransfered = new SButton(
					getPropertyName("arrange_stock"), 130);

			arrangeStockTransfered.setEnabled(false);
			arrangeStockTransfered
					.setDescription(getPropertyName("stock_receive_msg"));

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.setSpacing(true);
			mainButtonLayout.setMargin(true);
			mainButtonLayout.addComponent(receiveStockTransfer);
			mainButtonLayout.addComponent(arrangeStockTransfered);
			mainButtonLayout.setWidth("600");
			mainButtonLayout.setComponentAlignment(receiveStockTransfer,
					Alignment.MIDDLE_RIGHT);
			mainButtonLayout.setComponentAlignment(arrangeStockTransfered,
					Alignment.MIDDLE_LEFT);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(mainButtonLayout);

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			receiveStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (isValid()) {

						ConfirmDialog.show(getUI(),
								"This action cannot be undone. "
										+ getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												SComboField comboField = null;
												List itemsList = new ArrayList();
												List receiveList = new ArrayList();

												Iterator it = table
														.getItemIds()
														.iterator();
												Item item;
												ItemStockModel stock;
												ItemReceiveModel mdl = null;
												STextField waste;
												double wasteQty = 0;
												while (it.hasNext()) {
													item = table.getItem(it
															.next());
													comboField = (SComboField) item
															.getItemProperty(
																	TBC_ITEM)
															.getValue();
													stock = new ItemStockModel();

													stock.setExpiry_date(getWorkingDate());
													stock.setItem(new ItemModel(
															(Long) comboField
																	.getValue()));
													stock.setPurchase_id(0);
													stock.setInv_det_id(0);
													waste = (STextField) item
															.getItemProperty(
																	TBC_WASTE)
															.getValue();
													if (waste.getValue() != null
															&& !waste
																	.getValue()
																	.equals(""))
														wasteQty = toDouble(waste
																.getValue());
													stock.setQuantity((Double) item
															.getItemProperty(
																	TBC_QTY)
															.getValue()
															- wasteQty);
													stock.setBalance((Double) item
															.getItemProperty(
																	TBC_QTY)
															.getValue()
															- wasteQty);
													stock.setStatus(2);
													stock.setManufacturing_date(getWorkingDate());
													stock.setDate_time(CommonUtil
															.getCurrentDateTime());

													mdl = new ItemReceiveModel();
													mdl.setItem(new ItemModel(
															toLong(comboField
																	.getValue()
																	.toString())));
													mdl.setQunatity((Double) item
															.getItemProperty(
																	TBC_QTY)
															.getValue());
													mdl.setTransferId((Long) stockTransferNumberList
															.getValue());
													mdl.setWaste_quantity(wasteQty);
													mdl.setForeignItemId(toLong(item
															.getItemProperty(
																	TBC_ITEM_ID)
															.getValue()
															.toString()));

													itemsList.add(stock);
													receiveList.add(mdl);
												}

												daoObj.receiveStock(
														itemsList,
														receiveList,
														(Long) stockTransferNumberList
																.getValue());

												arrangeStockTransfered
														.setEnabled(true);

												Notification
														.show(
																getPropertyName("save_success"),
																Type.WARNING_MESSAGE);

												receiveStockTransfer
														.setEnabled(false);
												receiveStockTransfer
														.setDescription(getPropertyName("this_is_already_rxd"));

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}
				}
			});

			arrangeStockTransfered.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						List itemsList = daoObj
								.getStockListOfTransfer((Long) stockTransferNumberList
										.getValue());

						StockRackMappingPannel pan = new StockRackMappingPannel(
								itemsList, (Long) stockTransferNumberList
										.getValue());
						SWindow wind = new SWindow("Stock Arrangement");
						wind.setContent(pan);
						wind.center();
						wind.setModal(true);
						getUI().getCurrent().addWindow(wind);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			stockTransferNumberList
					.addValueChangeListener(new ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {

								if (stockTransferNumberList.getValue() != null
										&& !stockTransferNumberList.getValue()
												.toString().equals("0")) {

									List itemList = daoObj.getStockTransfer(
											(Long) stockTransferNumberList
													.getValue(), getOfficeID());

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_ID, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_QTY,
											TBC_UNIT_ID, TBC_UNIT,
											TBC_TO_OFFICE_ID,
											TBC_TO_OFFICE_NAME,
											TBC_EXISTING_QTY, TBC_ITEM,
											TBC_WASTE });

									table.removeAllItems();
									ItemTransferInventoryDetails invObj;
									ItemReceiveModel recMdl = null;
									Iterator it = itemList.iterator();
									STextField waste;
									while (it.hasNext()) {
										invObj = (ItemTransferInventoryDetails) it
												.next();

										itemComboField = new SComboField(
												null,
												200,
												itemDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
												"id", "name", true, getPropertyName("select"));
										waste = new STextField();
										waste.setValue("0");

										recMdl = daoObj.getReceiveModel(invObj
												.getItem().getId(),
												(Long) stockTransferNumberList
														.getValue());
										if (recMdl != null) {
											waste.setValue(recMdl
													.getWaste_quantity() + "");
											itemComboField.setValue(recMdl
													.getItem().getId());
										}

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
														new OfficeDao()
																.getOfficeName(invObj
																		.getTo_office_id()),
														invObj.getQunatity(),
														itemComboField, waste },
												table.getItemIds().size() + 1);

									}

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_UNIT, TBC_QTY,
											TBC_ITEM, TBC_WASTE });

									if (daoObj
											.getStatus((Long) stockTransferNumberList
													.getValue()) != 1) {
										receiveStockTransfer.setEnabled(false);
										receiveStockTransfer
												.setDescription(getPropertyName("this_is_already_rxd"));
										arrangeStockTransfered.setEnabled(true);
										arrangeStockTransfered
												.setDescription(null);
									} else {
										receiveStockTransfer.setEnabled(true);
										receiveStockTransfer
												.setDescription(null);
										arrangeStockTransfered
												.setEnabled(false);
										arrangeStockTransfered
												.setDescription(getPropertyName("stock_receive_msg"));
									}

								} else {
									table.removeAllItems();

								}

								calculateTotals();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void reloadStockTransfer() {
		try {

			Object temp = stockTransferNumberList.getValue();

			SCollectionContainer bic = SCollectionContainer.setList(
					daoObj.getAllStockTransferNumbersAsComment(getOfficeID()),
					"id");
			stockTransferNumberList.setContainerDataSource(bic);
			stockTransferNumberList.setItemCaptionPropertyId("comments");

			stockTransferNumberList.setValue(temp);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public Boolean getHelp() {
		return null;
	}

	public Boolean isValid() {
		boolean valid = true;
		itemComboField.setComponentError(null);
		stockTransferNumberList.setComponentError(null);
		table.setComponentError(null);
		if (itemComboField.getValue() == null
				|| itemComboField.getValue().equals("")) {
			setRequiredError(itemComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
		if (stockTransferNumberList.getValue() == null
				|| stockTransferNumberList.getValue().equals("")) {
			setRequiredError(stockTransferNumberList,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}

		Item item;
		SComboField comboField;
		Iterator it = table.getItemIds().iterator();
		while (it.hasNext()) {
			item = table.getItem(it.next());

			comboField = (SComboField) item.getItemProperty(TBC_ITEM)
					.getValue();
			if (comboField.getValue() == null
					|| comboField.getValue().equals("")) {
				valid = false;
				setRequiredError(table, getPropertyName("invalid_selection"),
						true);
			}
		}

		return valid;
	}

}
