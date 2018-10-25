package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.StockGradingDao;
import com.inventory.config.stock.model.GradeModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jun 4, 2014
 */
public class StockGradingUI extends SparkLogic {

	private static final long serialVersionUID = -5650003844365479643L;

	private SComboField stockComboField;
	private SComboField itemComboField;
	private STable table;
	private STextField quantityField;
	private SNativeSelect gradeNativeSelect;
	private SNativeSelect unitNativeSelect;
	private SButton addItemButton;
	private SButton updateItemButton;
	private SButton deleteItemButton;
	private STextField convertionQtyTextField;
	private STextField convertedQtyTextField;
	private STextField barcodeField;

	private ItemDao itemDao;
	private StockGradingDao dao;
	private GradeDao grdDao;
	private CommonMethodsDao commDao;

	static String TBC_STOCK_ID = "Stock Id";
	static String TBC_GRADE_ID = "Grade Id";
	static String TBC_GRADE = "Grade";
	static String TBC_QTY = "Quantity";
	static String TBC_QTY_IN_BASIC = "Quantity In Basic Unit";
	static String TBC_UNIT_ID = "Unit Id";
	static String TBC_UNIT = "Unit";
	static String TBC_BARCODE = "Barcode";

	private String[] allHeaders;
	private String[] reqHeaders;

	private SButton saveButton;

	@Override
	public SPanel getGUI() {
		setSize(820, 580);
		SPanel panel = new SPanel();

		SFormLayout lay = new SFormLayout();
		lay.setSpacing(true);
		lay.setMargin(true);
		// lay.setHeight("560px");

		panel.setContent(lay);

		itemDao = new ItemDao();
		dao = new StockGradingDao();
		grdDao = new GradeDao();
		commDao = new CommonMethodsDao();

		allHeaders = new String[] { TBC_STOCK_ID, TBC_QTY, TBC_QTY_IN_BASIC,
				TBC_UNIT_ID, TBC_UNIT, TBC_GRADE_ID, TBC_GRADE, TBC_BARCODE };
		reqHeaders = new String[] { TBC_QTY, TBC_UNIT, TBC_GRADE, TBC_BARCODE };

		SGridLayout buttonGrid = new SGridLayout(8, 1);
		buttonGrid.setSizeFull();

		SHorizontalLayout addingGrid = new SHorizontalLayout();
		addingGrid.setSpacing(true);
		// addingGrid.setColumns(9);
		// addingGrid.setRows(1);

		try {
			ItemModel item = new ItemModel((long) 0,
					"-----------------ALL------------------");
			List itemLis = new ArrayList();
			itemLis.add(0, item);
			itemLis.addAll(itemDao
					.getAllActiveItemsWithAppendingItemCode(getOfficeID()));
			itemComboField = new SComboField("Item", 250, itemLis, "id", "name");
			itemComboField
					.setInputPrompt("-----------------ALL------------------");
			itemComboField.setNullSelectionAllowed(false);
			stockComboField = new SComboField("Stock", 250);
			stockComboField
					.setInputPrompt("-----------------Select------------------");
			loadStocks();

			table = new STable(null, 700, 300);

			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,
					getPropertyName("stock_id"), null, Align.CENTER);
			table.addContainerProperty(TBC_GRADE_ID, Long.class, null,
					getPropertyName("grade_id"), null, Align.CENTER);
			table.addContainerProperty(TBC_GRADE, String.class, null,
					getPropertyName("grade"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,
					getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC, Double.class, null,
					getPropertyName("quantity_basic_unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					getPropertyName("unit_id"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null, TBC_UNIT,
					null, Align.LEFT);
			table.addContainerProperty(TBC_BARCODE, String.class, null,
					getPropertyName("barcode"), null, Align.LEFT);
			table.setSelectable(true);
			table.setVisibleColumns(reqHeaders);

			quantityField = new STextField(getPropertyName("quantity"), 60);
			quantityField.setStyleName("textfield_align_right");
			convertedQtyTextField = new STextField(
					getPropertyName("converterd_qty"), 60);
			convertedQtyTextField.setStyleName("textfield_align_right");
			convertionQtyTextField = new STextField(
					getPropertyName("conversion_qty"), 60);
			convertionQtyTextField.setStyleName("textfield_align_right");
			convertedQtyTextField.setVisible(false);
			convertionQtyTextField.setVisible(false);
			barcodeField = new STextField(getPropertyName("barcode"), 150);

			GradeModel gradeModel = new GradeModel((long) 0, "None");
			List grdLis = new ArrayList();
			grdLis.add(0, gradeModel);
			grdLis.addAll(grdDao.getAllGrades(getOfficeID()));
			gradeNativeSelect = new SNativeSelect(getPropertyName("grade"), 80,
					grdLis, "id", "name");
			gradeNativeSelect.setValue((long) 0);
			gradeNativeSelect.setValue(0);
			unitNativeSelect = new SNativeSelect(getPropertyName("unit"), 100);

			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			deleteItemButton = new SButton(null, "Delete");
			deleteItemButton.setStyleName("deleteItemBtnStyle");
			deleteItemButton.setVisible(false);

			saveButton = new SButton(getPropertyName("save"));
			buttonGrid.addComponent(saveButton, 4, 0);

			addingGrid.addComponent(quantityField);
			addingGrid.addComponent(convertionQtyTextField);
			addingGrid.addComponent(convertedQtyTextField);
			addingGrid.addComponent(unitNativeSelect);
			addingGrid.addComponent(gradeNativeSelect);
			addingGrid.addComponent(barcodeField);
			addingGrid.addComponent(addItemButton);
			// addingGrid.addComponent(updateItemButton);
			addingGrid.addComponent(deleteItemButton);

			addingGrid.setComponentAlignment(addItemButton,
					Alignment.MIDDLE_CENTER);
			// addingGrid.setComponentAlignment(updateItemButton,
			// Alignment.MIDDLE_CENTER);
			addingGrid.setComponentAlignment(deleteItemButton,
					Alignment.MIDDLE_CENTER);

			lay.addComponent(itemComboField);
			lay.addComponent(stockComboField);
			lay.addComponent(table);
			lay.addComponent(addingGrid);
			lay.addComponent(buttonGrid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		table.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null) {
					Item item = table.getItem(table.getValue());
					quantityField.setValue(item.getItemProperty(TBC_QTY)
							.getValue().toString());
					gradeNativeSelect.setValue(item.getItemProperty(
							TBC_GRADE_ID).getValue());
					unitNativeSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
							.getValue());
					barcodeField.setValue(item.getItemProperty(TBC_BARCODE)
							.getValue().toString());

					addItemButton.setVisible(false);
					updateItemButton.setVisible(true);
					deleteItemButton.setVisible(true);
				} else {
					setDefaultValues();

					addItemButton.setVisible(true);
					updateItemButton.setVisible(false);
					deleteItemButton.setVisible(false);

					convertionQtyTextField.setVisible(false);
					convertedQtyTextField.setVisible(false);

				}
			}
		});

		addItemButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isAddingValid()) {
					table.setVisibleColumns(allHeaders);

					table.addItem(
							new Object[] {
									(Long) stockComboField.getValue(),
									toDouble(quantityField.getValue()),
									toDouble(convertedQtyTextField.getValue()),
									unitNativeSelect.getValue(),
									unitNativeSelect
											.getItemCaption(unitNativeSelect
													.getValue()),
									gradeNativeSelect.getValue(),
									gradeNativeSelect
											.getItemCaption(gradeNativeSelect
													.getValue()),
									barcodeField.getValue() }, table
									.getItemIds().size() + 1);

					table.setVisibleColumns(reqHeaders);

					table.setValue(null);
					setDefaultValues();

					addItemButton.setVisible(true);
					updateItemButton.setVisible(false);
					deleteItemButton.setVisible(false);

					convertionQtyTextField.setVisible(false);
					convertedQtyTextField.setVisible(false);
				}
			}
		});

		updateItemButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isAddingValid()) {
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						item.getItemProperty(TBC_QTY).setValue(
								toDouble(quantityField.getValue()));
						item.getItemProperty(TBC_QTY_IN_BASIC).setValue(
								toDouble(convertedQtyTextField.getValue()));
						item.getItemProperty(TBC_GRADE_ID).setValue(
								(Long) gradeNativeSelect.getValue());
						item.getItemProperty(TBC_GRADE).setValue(
								gradeNativeSelect
										.getItemCaption(gradeNativeSelect
												.getValue()));
						item.getItemProperty(TBC_UNIT_ID).setValue(
								(Long) unitNativeSelect.getValue());
						item.getItemProperty(TBC_UNIT).setValue(
								unitNativeSelect
										.getItemCaption(unitNativeSelect
												.getValue()));
						item.getItemProperty(TBC_BARCODE).setValue(
								barcodeField.getValue());

						table.setValue(null);
					}
				}
			}
		});

		deleteItemButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (table.getValue() != null) {
					table.removeItem(table.getValue());
				}
			}
		});

		itemComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearFields();

				loadStocks();
			}

		});

		stockComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				clearFields();

				if (stockComboField.getValue() != null
						&& !stockComboField.getValue().equals("")) {

					ItemStockModel stk;
					try {
						stk = dao.getStock((Long) stockComboField.getValue());
						SCollectionContainer bic = SCollectionContainer
								.setList(commDao.getAllItemUnitDetails(stk
										.getItem().getId()), "id");

						unitNativeSelect.setContainerDataSource(bic);
						unitNativeSelect.setItemCaptionPropertyId("symbol");
						unitNativeSelect.setValue(stk.getItem().getUnit()
								.getId());

						barcodeField.setValue(stk.getBarcode());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		unitNativeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (unitNativeSelect.getValue() != null
						&& !unitNativeSelect.getValue().equals("")) {
					try {
						ItemStockModel stk = dao
								.getStock((Long) stockComboField.getValue());
						if (stk.getItem().getUnit().getId() == (Long) unitNativeSelect
								.getValue()) {
							convertionQtyTextField.setValue("1");
							convertionQtyTextField.setVisible(false);
							convertedQtyTextField.setVisible(false);
						} else {
							convertionQtyTextField.setVisible(true);
							convertedQtyTextField.setVisible(true);

							convertionQtyTextField.setCaption("Conv Qty");
							convertedQtyTextField.setCaption("Qty - "
									+ stk.getItem().getUnit().getSymbol());

							double cnvr_qty = commDao.getConvertionRate(stk
									.getItem().getId(), (Long) unitNativeSelect
									.getValue(), 0);

							convertionQtyTextField.setValue(asString(cnvr_qty));

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		convertionQtyTextField.setImmediate(true);
		convertionQtyTextField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							try {
								if (convertionQtyTextField.getValue()
										.equals("")
										|| toDouble(convertionQtyTextField
												.getValue()) <= 0) {
									convertionQtyTextField.setValue("1");
								}
							} catch (Exception e) {
								convertionQtyTextField.setValue("1");
							}

							convertedQtyTextField.setNewValue(asString(Double
									.parseDouble(quantityField.getValue())
									* Double.parseDouble(convertionQtyTextField
											.getValue())));

						} catch (Exception e) {
							convertedQtyTextField.setNewValue(quantityField
									.getValue());
						}

					}
				});

		quantityField.setImmediate(true);

		quantityField.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					convertedQtyTextField.setNewValue(asString(Double
							.parseDouble(quantityField.getValue())
							* Double.parseDouble(convertionQtyTextField
									.getValue())));
				} catch (Exception e) {
					convertedQtyTextField.setNewValue(quantityField.getValue());
				}

			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					try {

						List stockList = new ArrayList();
						ItemStockModel stk;
						ItemStockModel oldStk = dao
								.getStock((Long) stockComboField.getValue());

						Iterator iter = table.getItemIds().iterator();
						Item item;
						double qty = 0;
						double oldBalance = oldStk.getBalance();

						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							stk = new ItemStockModel();

							stk.setBalance(toDouble(item
									.getItemProperty(TBC_QTY_IN_BASIC)
									.getValue().toString()));
							stk.setExpiry_date(CommonUtil.getCurrentSQLDate());
							stk.setItem(oldStk.getItem());
							stk.setManufacturing_date(CommonUtil
									.getCurrentSQLDate());
							stk.setRate(oldStk.getRate());
							stk.setPurchase_id(0);
							stk.setInv_det_id(0);
							stk.setQuantity(toDouble(item
									.getItemProperty(TBC_QTY_IN_BASIC)
									.getValue().toString()));
							stk.setStatus(4);
							stk.setDate_time(CommonUtil.getCurrentDateTime());
							stk.setGradeId(toLong(item
									.getItemProperty(TBC_GRADE_ID).getValue()
									.toString()));
							stk.setBarcode(item.getItemProperty(TBC_BARCODE)
									.getValue().toString());
							stk.setBlocked(false);
							stockList.add(stk);

							qty += toDouble(item
									.getItemProperty(TBC_QTY_IN_BASIC)
									.getValue().toString());
						}

						oldStk.setBlocked(true);
						oldStk.setBalance(oldBalance - qty);
						dao.save(stockList, oldStk);
						SNotification.show(getPropertyName("success"),
								Type.WARNING_MESSAGE);

						loadStocks();

					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}

			}
		});

		table.addShortcutListener(new ShortcutListener("Add Item",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (addItemButton.isVisible())
					addItemButton.click();
				else
					updateItemButton.click();
			}
		});

		final Action actionDelete = new Action("Delete");

		table.addActionHandler(new Action.Handler() {
			@Override
			public Action[] getActions(final Object target, final Object sender) {
				return new Action[] { actionDelete };
			}

			@Override
			public void handleAction(final Action action, final Object sender,
					final Object target) {
				deleteItemButton.click();
			}
		});

		return panel;
	}

	protected void setDefaultValues() {

		try {
			if (stockComboField.getValue() != null
					&& !stockComboField.getValue().equals("")) {
				ItemStockModel stk = dao.getStock((Long) stockComboField
						.getValue());

				if (stk != null) {
					quantityField.setValue("0");
					barcodeField.setValue(stk.getBarcode());
					gradeNativeSelect.setValue(0);
					unitNativeSelect.setValue(stk.getItem().getUnit().getId());
				} else {
					quantityField.setValue("0");
					barcodeField.setValue("");
					gradeNativeSelect.setValue(0);
					unitNativeSelect.setValue(null);
				}
			} else {
				quantityField.setValue("0");
				barcodeField.setValue("");
				gradeNativeSelect.setValue(0);
				unitNativeSelect.setValue(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean isAddingValid() {
		boolean flag = true;
		quantityField.setComponentError(null);
		unitNativeSelect.setComponentError(null);
		try {
			if (toDouble(quantityField.getValue().toString()) <= 0) {
				setRequiredError(quantityField,
						getPropertyName("invalid_data"), true);
				flag = false;
			}
		} catch (Exception e) {
			setRequiredError(quantityField, getPropertyName("invalid_data"),
					true);
			flag = false;
		}

		if (unitNativeSelect.getValue() == null
				|| unitNativeSelect.getValue().equals("")) {
			setRequiredError(unitNativeSelect, getPropertyName("select_unit"),
					true);
			flag = false;
		}

		if (flag) {
			if (!hasEnoughQty()) {
				setRequiredError(quantityField,
						getPropertyName("not_enough_quantity"), true);
				flag = false;
			}
		}
		return flag;
	}

	private boolean hasEnoughQty() {
		Iterator iter = table.getItemIds().iterator();
		Item item;
		double qty = toDouble(convertedQtyTextField.getValue());
		try {

			while (iter.hasNext()) {
				item = table.getItem(iter.next());
				qty += toDouble(item.getItemProperty(TBC_QTY_IN_BASIC)
						.getValue().toString());
			}
			ItemStockModel stk = dao
					.getStock((Long) stockComboField.getValue());

			if (stk.getBalance() < qty) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private void clearFields() {
		table.removeAllItems();
		quantityField.setValue("0");
		barcodeField.setValue("");
		gradeNativeSelect.setValue(0);
		unitNativeSelect.setValue(null);
		convertedQtyTextField.setVisible(false);
		convertionQtyTextField.setVisible(false);

		convertionQtyTextField.setValue("1");
		convertedQtyTextField.setValue(quantityField.getValue());
	}

	private void loadStocks() {

		long itemId = 0;
		if (itemComboField.getValue() != null && !itemComboField.getValue().equals(""))
			itemId = (Long) itemComboField.getValue();

		SCollectionContainer bic;
		try {
			System.out.println("Size "+dao.getItemStockList(getOfficeID(), itemId).size());
			bic = SCollectionContainer.setList(dao.getItemStockList(getOfficeID(), itemId), "id");
			stockComboField.setContainerDataSource(bic);
			stockComboField.setItemCaptionPropertyId("details");
			stockComboField.setValue(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		boolean flag = true;

		table.setComponentError(null);

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("insert_some_data"), true);
			flag = false;
		}
		return flag;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
