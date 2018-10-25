package com.inventory.sales.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.BuildingDao;
import com.inventory.model.BuildingModel;
import com.inventory.sales.dao.CustomerSalesOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 24, 2013
 */
public class CustomerSalesOrderUI extends SparkLogic {

	private static final long serialVersionUID = -4709602165894250378L;

	private static final String TBL_ITEM_ID = "Item Id";
	private static final String TBL_ITEM_NAME = "Item Name";
	private static final String TBL_RATE = "Rate";
	private static final String TBL_QUANTITY = "Quantity";
	private static final String TBL_UNIT = "Unit";

	private STable table;
	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private SButton submitButton;

	private String[] allHeaders;
	private String[] requiredHeaders;

	private SComboField billNoField;
	private SDateField dateField;
	private SNativeSelect salesTypeSelect;

	private CustomerSalesOrderDao dao;
	private CommonMethodsDao comDao;

	boolean taxEnable;

	private ItemDao itemDao;

	private Listener listener;

	private SLabel statusLabel;
	private SimpleDateFormat df;

	private SButton newSaleButton;

	@SuppressWarnings("unchecked")
	@Override
	public SPanel getGUI() {

		setSize(700, 700);

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout lay = new SFormLayout();
		lay.setSizeFull();

		SGridLayout dateLayout = new SGridLayout();
		dateLayout.setSpacing(true);
		dateLayout.setColumns(9);
		dateLayout.setRows(1);

		SGridLayout grid = new SGridLayout();
		grid.setSpacing(true);
		grid.setColumns(6);
		grid.setRows(1);
		grid.setSizeFull();

		SHorizontalLayout buttHorizontalLayout = new SHorizontalLayout();
		buttHorizontalLayout.setSpacing(true);
		try {
			df = new SimpleDateFormat("yyyy-MM-dd");
			taxEnable = isTaxEnable();
			comDao = new CommonMethodsDao();
			dao = new CustomerSalesOrderDao();
			itemDao = new ItemDao();

			long customerLedgerId = dao.getLedgerFromLogin(getLoginID());
			long salType = 0;

			newSaleButton = new SButton();
			newSaleButton.setStyleName("createNewBtnStyle");
			newSaleButton.setDescription("Add new Item");

			List list = new ArrayList();
			list.add(new SalesOrderModel(0, "-----------Create New------------"));
			try {
				list.addAll(dao.getBillNumbers(getLoginID(), getOfficeID()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			billNoField = new SComboField(null, 200, list, "id", "ref_no");
			billNoField.setInputPrompt(getPropertyName("create_new"));
			dateField = new SDateField(null, 100, getDateFormat(),
					new java.util.Date());
			dateField.setReadOnly(true);

			statusLabel = new SLabel(null, "New");
			statusLabel.setStyleName(Reindeer.LABEL_H2);

			salesTypeSelect = new SNativeSelect(null, 120,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");
			long sal = dao.getAllActiveSalesTypeNames(customerLedgerId,
					CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
			if (sal > 0)
				salesTypeSelect.setValue(sal);

			if (salesTypeSelect.getValue() != null)
				salType = (Long) salesTypeSelect.getValue();

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(billNoField);
			salLisrLay.addComponent(newSaleButton);

			dateLayout.addComponent(new SLabel(
					getPropertyName("sales_order_no")), 1, 0);
			dateLayout.addComponent(salLisrLay, 2, 0);
			dateLayout.addComponent(new SLabel(getPropertyName("date")), 4, 0);
			dateLayout.addComponent(dateField, 5, 0);
			dateLayout
					.addComponent(new SLabel(getPropertyName("status")), 6, 0);
			dateLayout.addComponent(statusLabel, 7, 0);

			allHeaders = new String[] { TBL_ITEM_ID, TBL_ITEM_NAME, TBL_UNIT,
					TBL_RATE, TBL_QUANTITY };
			requiredHeaders = new String[] { TBL_ITEM_NAME, TBL_UNIT, TBL_RATE,
					TBL_QUANTITY };

			table = new STable(null, 600, 450);
			table.addContainerProperty(TBL_ITEM_ID, Long.class, null,
					TBL_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT, SNativeSelect.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBL_RATE, Double.class, null,
					getPropertyName("rate"), null, Align.RIGHT);
			table.addContainerProperty(TBL_QUANTITY, STextField.class, null,
					getPropertyName("quantity"), null, Align.CENTER);

			saveButton = new SButton(getPropertyName("add_cart"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/addcart.png"));

			updateButton = new SButton(getPropertyName("update_cart"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			updateButton.setVisible(false);

			deleteButton = new SButton(getPropertyName("delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			deleteButton.setVisible(false);

			submitButton = new SButton(getPropertyName("place_order"));
			submitButton.setIcon(new ThemeResource("icons/cart.png"));
			submitButton.setVisible(false);

			buttHorizontalLayout.addComponent(saveButton);
			buttHorizontalLayout.addComponent(updateButton);
			buttHorizontalLayout.addComponent(deleteButton);

			grid.addComponent(buttHorizontalLayout, 3, 0);
			grid.addComponent(submitButton, 4, 0);

			lay.addComponent(dateLayout);
			lay.addComponent(table);
			lay.addComponent(grid);

			pan.setContent(lay);

			listener = new Listener() {

				@Override
				public void componentEvent(Event event) {
					try {

						Item tblItem = null;
						long itemId = toLong(event.getComponent().getId());
						long unitId = toLong(((SNativeSelect) event
								.getComponent()).getValue().toString());
						double rate = 0;

						rate = dao.getItemPrice(dao
								.getLedgerFromLogin(getLoginID()), CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()),
								(Long) salesTypeSelect.getValue(), itemId,
								unitId);

						Iterator tblItr = table.getItemIds().iterator();
						while (tblItr.hasNext()) {
							tblItem = table.getItem(tblItr.next());
							if (toLong(tblItem.getItemProperty(TBL_ITEM_ID)
									.getValue().toString()) == itemId) {
								tblItem.getItemProperty(TBL_RATE)
										.setValue(rate);
								break;
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			loadItems(customerLedgerId,
					CommonUtil.getSQLDateFromUtilDate(dateField.getValue()),
					salType);

			newSaleButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					billNoField.setValue((long) 0);
				}
			});

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {

						SalesOrderModel poObj = new SalesOrderModel();

						List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();

						SalesInventoryDetailsModel invObj;
						Item item;
						Iterator it = table.getItemIds().iterator();

						STextField field = null;
						SNativeSelect unitSelect = null;
						double quantity = 0;
						double total = 0;
						long billNo = 0;

						List buildingList = new BuildingDao()
								.getAllActiveBuildingNamesUnderOffice(getOfficeID());
						long customerLedgerId = dao
								.getLedgerFromLogin(getLoginID());

						while (it.hasNext()) {

							item = table.getItem(it.next());

							field = (STextField) item.getItemProperty(
									TBL_QUANTITY).getValue();
							quantity = toDouble(field.getValue().toString());

							if (quantity > 0) {
								total += toDouble(item
										.getItemProperty(TBL_RATE).getValue()
										.toString());
								unitSelect = (SNativeSelect) item
										.getItemProperty(TBL_UNIT).getValue();

								invObj = new SalesInventoryDetailsModel();

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBL_ITEM_ID)
										.getValue()));
								invObj.setQunatity(quantity);
//								invObj.setBalance(quantity);
//
//								invObj.setTax(new TaxModel(1));
//								invObj.setTax_amount(0);
//								invObj.setTax_percentage(0);
								invObj.setUnit(new UnitModel((Long) unitSelect
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBL_RATE).getValue());

								invObj.setQuantity_in_basic_unit(quantity);

								itemsList.add(invObj);
							}
						}

//						if (itemsList.size() > 0) {
//
//							billNo = getNextSequence("Customer Sales Order Id",
//									getLoginID());
//							poObj.setTotal_amount(total);
//							poObj.setBuilding(new BuildingModel(
//									(Long) (((BuildingModel) (buildingList
//											.get(0))).getId())));
//							poObj.setComments("");
//							poObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(dateField
//											.getValue()));
//							poObj.setRequired_delivery_date(CommonUtil
//									.getSQLDateFromUtilDate(dateField
//											.getValue()));
//							poObj.setLogin(new S_LoginModel(getLoginID()));
//							poObj.setOffice(new S_OfficeModel(getOfficeID()));
//							poObj.setRef_no(billNo + "");
//							poObj.setStatus(SConstants.statuses.SALES_ORDER_CUSTOMER_CREATED);
//							poObj.setCustomer(new LedgerModel(customerLedgerId));
//							poObj.setInventory_details_list(itemsList);
//							poObj.setSales_person((long) 0);
//							poObj.setSales_order_number(billNo);
//
//							poObj.setSales_type((Long) salesTypeSelect
//									.getValue());
//
//							long id = dao.save(poObj);
//
//							saveActivity(getOptionId(),
//									"Customer Sales Order Saved. Customer SO No : "
//											+ id + ", Customer : "
//											+ getLoginName() + ", Amount : "
//											+ poObj.getTotal_amount());
//
//							loadSO(id);
//
//							Notification.show(getPropertyName("save_success"),
//									Type.WARNING_MESSAGE);
//						} else {
//							Notification.show(getPropertyName("error"),
//									Type.WARNING_MESSAGE);
//						}

					} catch (Exception e) {
						SNotification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

			updateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {

						SalesOrderModel poObj = dao
								.getSalesOrder((Long) billNoField.getValue());

						List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();

						SalesInventoryDetailsModel invObj;
						Item item;
						Iterator it = table.getItemIds().iterator();

						STextField field = null;
						SNativeSelect unitSelect = null;
						double quantity = 0;
						double total = 0;

						List buildingList = new BuildingDao()
								.getAllActiveBuildingNamesUnderOffice(getOfficeID());
						long customerLedgerId = dao
								.getLedgerFromLogin(getLoginID());

						while (it.hasNext()) {

							item = table.getItem(it.next());

							field = (STextField) item.getItemProperty(
									TBL_QUANTITY).getValue();
							quantity = toDouble(field.getValue().toString());

							if (quantity > 0) {
								total += toDouble(item
										.getItemProperty(TBL_RATE).getValue()
										.toString());
								unitSelect = (SNativeSelect) item
										.getItemProperty(TBL_UNIT).getValue();

								invObj = new SalesInventoryDetailsModel();

								invObj.setItem(new ItemModel((Long) item
										.getItemProperty(TBL_ITEM_ID)
										.getValue()));
								invObj.setQunatity(quantity);
//								invObj.setBalance(quantity);

								invObj.setTax(new TaxModel(1));
//								invObj.setTax_amount(0);
//								invObj.setTax_percentage(0);
								invObj.setUnit(new UnitModel((Long) unitSelect
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBL_RATE).getValue());

								invObj.setQuantity_in_basic_unit(quantity);

								itemsList.add(invObj);
							}
						}
//						if (itemsList.size() > 0) {
//							poObj.setTotal_amount(total);
//							poObj.setBuilding(new BuildingModel(
//									(Long) (((BuildingModel) (buildingList
//											.get(0))).getId())));
//							poObj.setComments("");
//							poObj.setDate(CommonUtil
//									.getSQLDateFromUtilDate(dateField
//											.getValue()));
//							poObj.setRequired_delivery_date(CommonUtil
//									.getSQLDateFromUtilDate(dateField
//											.getValue()));
//							poObj.setLogin(new S_LoginModel(getLoginID()));
//							poObj.setOffice(new S_OfficeModel(getOfficeID()));
//							poObj.setStatus(SConstants.statuses.SALES_ORDER_CUSTOMER_CREATED);
//							poObj.setCustomer(new LedgerModel(customerLedgerId));
//							poObj.setInventory_details_list(itemsList);
//
//							poObj.setSales_type((Long) salesTypeSelect
//									.getValue());
//							dao.update(poObj);
//
//							saveActivity(
//									getOptionId(),
//									"Customer Sales Order Updated. Customer SO No : "
//											+ billNoField.getValue()
//											+ ", Customer : " + getLoginName()
//											+ ", Amount : "
//											+ poObj.getTotal_amount());
//
//							loadSO(poObj.getId());
//
//							Notification.show(
//									getPropertyName("update_success"),
//									Type.WARNING_MESSAGE);
//						} else {
//							Notification.show(getPropertyName("error"),
//									Type.WARNING_MESSAGE);
//						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			deleteButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {

								@Override
								public void onClose(ConfirmDialog dlg) {
									if (dlg.isConfirmed()) {
										try {
											dao.delete(toLong(billNoField
													.getValue().toString()));

											saveActivity(
													getOptionId(),
													"Customer Sales Order Deleted. Customer SO No : "
															+ billNoField
																	.getValue()
															+ ", Customer : "
															+ getLoginName());

											SNotification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);
											loadSO((long) 0);
										} catch (Exception e) {
											SNotification.show(
													getPropertyName("error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});
				}
			});

			submitButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						ConfirmDialog
								.show(getUI(),
										"Submit",
										"This will place a sales order. You will not be able to edit the order after submission.",
										"Submit Order", "Cancel",
										new ConfirmDialog.Listener() {

											@Override
											public void onClose(
													ConfirmDialog arg0) {
												if (arg0.isConfirmed()) {
													try {
														SalesOrderModel poObj = dao
																.getSalesOrder((Long) billNoField
																		.getValue());

														List<SalesInventoryDetailsModel> itemsList = new ArrayList<SalesInventoryDetailsModel>();

														SalesInventoryDetailsModel invObj;
														Item item;
														Iterator it = table
																.getItemIds()
																.iterator();

														STextField field = null;
														SNativeSelect unitSelect = null;
														double quantity = 0;
														double total = 0;

														List buildingList = new BuildingDao()
																.getAllActiveBuildingNamesUnderOffice(getOfficeID());
														long customerLedgerId = dao
																.getLedgerFromLogin(getLoginID());

														while (it.hasNext()) {

															item = table.getItem(it
																	.next());

															field = (STextField) item
																	.getItemProperty(
																			TBL_QUANTITY)
																	.getValue();
															quantity = toDouble(field
																	.getValue()
																	.toString());

															if (quantity > 0) {
																total += toDouble(item
																		.getItemProperty(
																				TBL_RATE)
																		.getValue()
																		.toString());
																unitSelect = (SNativeSelect) item
																		.getItemProperty(
																				TBL_UNIT)
																		.getValue();

																invObj = new SalesInventoryDetailsModel();

																invObj.setItem(new ItemModel(
																		(Long) item
																				.getItemProperty(
																						TBL_ITEM_ID)
																				.getValue()));
																invObj.setQunatity(quantity);
//																invObj.setBalance(quantity);

																invObj.setTax(new TaxModel(
																		1));
//																invObj.setTax_amount(0);
//																invObj.setTax_percentage(0);
																invObj.setUnit(new UnitModel(
																		(Long) unitSelect
																				.getValue()));
																invObj.setUnit_price((Double) item
																		.getItemProperty(
																				TBL_RATE)
																		.getValue());

																invObj.setQuantity_in_basic_unit(quantity);

																itemsList
																		.add(invObj);
															}
														}
//														if (itemsList.size() > 0) {
//
//															poObj.setTotal_amount(total);
//															poObj.setBuilding(new BuildingModel(
//																	(Long) (((BuildingModel) (buildingList
//																			.get(0)))
//																			.getId())));
//															poObj.setComments("");
//															poObj.setDate(CommonUtil
//																	.getSQLDateFromUtilDate(dateField
//																			.getValue()));
//															poObj.setRequired_delivery_date(CommonUtil
//																	.getSQLDateFromUtilDate(dateField
//																			.getValue()));
//															poObj.setLogin(new S_LoginModel(
//																	getLoginID()));
//															poObj.setOffice(new S_OfficeModel(
//																	getOfficeID()));
//															poObj.setStatus(SConstants.statuses.SALES_ORDER_CUSTOMER_SUBMITTED);
//															poObj.setCustomer(new LedgerModel(
//																	customerLedgerId));
//															poObj.setInventory_details_list(itemsList);
//
//															poObj.setSales_type((Long) salesTypeSelect
//																	.getValue());
//															dao.update(poObj);
//
//															saveActivity(
//																	getOptionId(),
//																	"Customer Sales Order Submitted. Customer SO No : "
//																			+ billNoField
//																					.getValue()
//																			+ ", Customer : "
//																			+ getLoginName()
//																			+ ", Amount : "
//																			+ poObj.getTotal_amount());
//
//															loadSO(poObj
//																	.getId());
//
//															Notification
//																	.show(getPropertyName("success"),
//																			Type.WARNING_MESSAGE);
//														} else {
//															Notification
//																	.show(getPropertyName("error"),
//																			Type.WARNING_MESSAGE);
//														}

													} catch (Exception e) {
														e.printStackTrace();
														SNotification
																.show(getPropertyName("error"),
																		Type.ERROR_MESSAGE);
													}
												}
											}
										});
					}
				}
			});

			billNoField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					resetTable();
					try {
						long custId = dao.getLedgerFromLogin(getLoginID());
						if (billNoField.getValue() != null
								&& !billNoField.getValue().equals("")
								&& !billNoField.getValue().toString()
										.equals("0")) {

							SalesOrderModel ordModel = dao
									.getSalesOrder((Long) billNoField
											.getValue());
							if (ordModel != null) {
//								loadItems(custId, ordModel.getDate(),
//										ordModel.getSales_type());
								loadData(ordModel);
							}
						} else {

							dateField.setNewValue(new java.util.Date());

							loadItems(custId, CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()),
									(Long) salesTypeSelect.getValue());

							statusLabel.setValue("New");

							saveButton.setVisible(true);
							updateButton.setVisible(false);
							deleteButton.setVisible(false);
							submitButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return pan;
	}

	protected void loadSO(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new SalesOrderModel(0, "----------Create New----------"));
			list.addAll(dao.getBillNumbers(getLoginID(), getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			billNoField.setContainerDataSource(bic);
			billNoField.setItemCaptionPropertyId("ref_no");

			billNoField.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadItems(long customerLedgerId, Date date, long salesType) {

		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);

			ItemModel mdl = null;
			double rate = 0;

			STextField quantityField = null;

			SNativeSelect unitSelect = null;

			List list = dao.getAllActiveItemsFromOfc(customerLedgerId, date,
					salesType);

			List unitList = null;

			if (list != null) {
				Iterator itrtest;
				for (int i = 0; i < list.size(); i++) {

					mdl = (ItemModel) list.get(i);

					quantityField = new STextField();
					quantityField.setStyleName("textfield_align_right");
					quantityField.setValue("0");

					unitList = dao.getAllActiveUnits(mdl.getId(),
							customerLedgerId, date, salesType);

					unitSelect = new SNativeSelect(null, 80, unitList, "id",
							"symbol");
					unitSelect.setValue(mdl.getUnit().getId());
					unitSelect.setId(mdl.getId() + "");
					unitSelect.addListener(listener);

					if (unitSelect.getValue() == null) {
						itrtest = unitSelect.getItemIds().iterator();
						if (itrtest.hasNext())
							unitSelect.setValue(itrtest.next());
					}

					rate = dao.getItemPrice(customerLedgerId, date, salesType,
							mdl.getId(), mdl.getUnit().getId());

					Object[] rows = new Object[] { mdl.getId(), mdl.getName(),
							unitSelect, rate, quantityField };
					table.addItem(rows, i + 1);
				}
			} else {
				SNotification
						.show(getPropertyName("error_define_daily_rate"), Type.ERROR_MESSAGE);
			}
			table.setVisibleColumns(requiredHeaders);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadData(SalesOrderModel ordModel) {
		try {

			SalesInventoryDetailsModel detailMdl = null;
			List list = ordModel.getOrder_details_list();
			Iterator itr = list.iterator();
			Iterator tblItr = table.getItemIds().iterator();
			Item tblItem = null;
			STextField qtyField = null;
			SNativeSelect unitSelect = null;

			while (itr.hasNext()) {
				detailMdl = (SalesInventoryDetailsModel) itr.next();

				while (tblItr.hasNext()) {
					tblItem = table.getItem(tblItr.next());

					if (detailMdl.getItem().getId() == toLong(tblItem
							.getItemProperty(TBL_ITEM_ID).getValue().toString())) {

						((STextField) (tblItem.getItemProperty(TBL_QUANTITY)
								.getValue())).setValue(detailMdl.getQunatity()
								+ "");
						((SNativeSelect) (tblItem.getItemProperty(TBL_UNIT)
								.getValue())).setValue(detailMdl.getUnit()
								.getId());
						break;

					}
				}

			}

			dateField.setNewValue(ordModel.getDate());

			saveButton.setVisible(false);
			updateButton.setVisible(true);
			deleteButton.setVisible(true);
			submitButton.setVisible(true);

//			if (ordModel.getStatus() == SConstants.statuses.SALES_ORDER_CUSTOMER_SUBMITTED
//					|| ordModel.getStatus() == SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED) {
//				updateButton.setEnabled(false);
//				deleteButton.setEnabled(false);
//				submitButton.setEnabled(false);
//				statusLabel.setValue("Order Placed");
//				if (ordModel.getStatus() == SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED)
//					statusLabel.setValue("Awaiting Sales");
//
//			} else {
//				updateButton.setEnabled(true);
//				deleteButton.setEnabled(true);
//				submitButton.setEnabled(true);
//				statusLabel.setValue("New");
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void resetTable() {
		Iterator tblItr = table.getItemIds().iterator();
		Item tblItem = null;
		STextField qtyField = null;
		SNativeSelect unitSelect = null;
		ItemModel itemModel = null;
		try {
			while (tblItr.hasNext()) {
				tblItem = table.getItem(tblItr.next());

				qtyField = (STextField) (tblItem.getItemProperty(TBL_QUANTITY)
						.getValue());
				unitSelect = (SNativeSelect) (tblItem.getItemProperty(TBL_UNIT)
						.getValue());

				itemModel = itemDao.getItem(toLong(tblItem
						.getItemProperty(TBL_ITEM_ID).getValue().toString()));

				qtyField.setValue("0");
				unitSelect.setValue(itemModel.getUnit().getId());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		if (!df.format(dateField.getValue()).equals(
				df.format(new java.util.Date()))) {
			SNotification
					.show("This order was created on previous date. You will have to place the order on the same date",
							Type.TRAY_NOTIFICATION);
			return false;
		}

		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
