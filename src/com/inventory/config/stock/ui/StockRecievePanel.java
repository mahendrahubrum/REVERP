package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.StockReceiveDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.StockTransferInventoryDetails;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.purchase.model.ItemStockModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 23, 2013
 */
@SuppressWarnings("serial")
public class StockRecievePanel extends SContainerPanel {

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
	static String TBC_EXISTING_QTY = "Existing Qty";
	static String TBC_ITEM = "Item";

	StockReceiveDao daoObj = new StockReceiveDao();

	SComboField stockTransferNumberList;
	SComboField itemComboField;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;

	SGridLayout masterDetailsGrid;

	SButton receiveStockTransfer;

	SButton arrangeStockTransfered;
	OfficeDao ofcDao = new OfficeDao();

	ItemDao itemDao = new ItemDao();

	@SuppressWarnings("deprecation")
	public StockRecievePanel() {

		setId("Receiver");
		setSize(760, 460);

		daoObj = new StockReceiveDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(1);

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

			form.setStyleName("po_style");

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
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
					TBC_STOCK_ID, null, Align.RIGHT);
			table.addContainerProperty(TBC_TO_OFFICE_ID, Long.class, null,
					TBC_TO_OFFICE_ID, null, Align.RIGHT);
			table.addContainerProperty(TBC_TO_OFFICE_NAME, String.class, null,
					TBC_TO_OFFICE_NAME, null, Align.CENTER);
			table.addContainerProperty(TBC_EXISTING_QTY, Double.class, null,
					TBC_EXISTING_QTY, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM, SComboField.class, null,
					getPropertyName("item"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY, TBC_ITEM });

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

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			receiveStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						try {

							StockTransferModel poObj = new StockTransferModel();

							List itemsList = new ArrayList();
							SComboField comboField = null;

							Item item;
							ItemStockModel stk;
							ItemStockModel stock;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								item = table.getItem(it.next());
								comboField = (SComboField) item
										.getItemProperty(TBC_ITEM).getValue();
								stk = daoObj.getItemStocks((Long) item
										.getItemProperty(TBC_STOCK_ID)
										.getValue());

								stock = new ItemStockModel();
								stock.setExpiry_date(stk.getExpiry_date());
								stock.setItem(new ItemModel((Long) comboField
										.getValue()));
								stock.setPurchase_id(0);
								stock.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								stock.setBalance((Double) item.getItemProperty(
										TBC_QTY).getValue());
								stock.setStatus(2);
								stock.setManufacturing_date(stk
										.getManufacturing_date());
								stock.setDate_time(CommonUtil
										.getCurrentDateTime());

								itemsList.add(stock);
							}

							daoObj.receiveStock(itemsList,
									(Long) stockTransferNumberList.getValue());

							SPanel pan = new StockRackMappingPannel(
									daoObj.getStockListOfTransfer((Long) stockTransferNumberList
											.getValue()),
									(Long) stockTransferNumberList.getValue());
							SWindow wind = new SWindow("Stock Rack Mapping");
							wind.setContent(pan);

							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							receiveStockTransfer.setEnabled(false);
							receiveStockTransfer
									.setDescription(getPropertyName("this_is_already_rxd"));

						} catch (Exception e) {
							e.printStackTrace();
						}
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
						SWindow wind = new SWindow(
								getPropertyName("stock_arrangement"));
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
					.addListener(new Property.ValueChangeListener() {

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
											TBC_STOCK_ID, TBC_TO_OFFICE_ID,
											TBC_TO_OFFICE_NAME,
											TBC_EXISTING_QTY, TBC_ITEM });

									table.removeAllItems();

									StockTransferInventoryDetails invObj;
									Iterator it = itemList.iterator();
									while (it.hasNext()) {
										invObj = (StockTransferInventoryDetails) it
												.next();
										itemComboField = new SComboField(
												null,
												200,
												itemDao.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
												"id", "name", true, "Select");
										itemComboField.setValue(invObj.getStock_id()
												.getItem().getId());
										table.addItem(
												new Object[] {
														table.getItemIds()
																.size() + 1,
														invObj.getStock_id().getItem()
																.getId(),
														invObj.getStock_id().getItem()
																.getItem_code(),
														invObj.getStock_id().getItem()
																.getName(),
														invObj.getStock_id().getQuantity(),
														invObj.getUnit()
																.getId(),
														invObj.getUnit()
																.getSymbol(),
														invObj.getStock_id(),
														0/*invObj.getTo_office_id()*/,
														0,/*ofcDao.getOfficeName(invObj
																.getTo_office_id()),
														*/invObj.getQuantity(),
														itemComboField },
												table.getItemIds().size() + 1);

									}

									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_UNIT, TBC_QTY,
											TBC_ITEM });

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
			// TODO: handle exception
		}
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
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
