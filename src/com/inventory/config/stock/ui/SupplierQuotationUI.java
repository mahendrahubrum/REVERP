package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.bean.QuotationBean;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SupplierQuotationDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.SupplierQuotationDetailsModel;
import com.inventory.config.stock.model.SupplierQuotationModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.CountryDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 27, 2013
 */
public class SupplierQuotationUI extends SparkLogic {

	private static final long serialVersionUID = 5283872698521209976L;

	private static final String TBL_ITEM_ID = "Item Id";
	private static final String TBL_COUNTRY_ID = "Country Id";
	private static final String TBL_ITEM_NAME = "Item";
	private static final String TBL_COUNTRY_NAME = "Country";
	private static final String TBL_UNIT_ID = "Unit Id";
	private static final String TBL_UNIT_NAME = "Unit";
	private static final String TBL_RATE = "Rate";
	private static final String TBL_CURRENCY_NAME = "Currency";
	private static final String TBL_CURRENCY_ID = "Currency ID";

	private STable table;
	private SDateField dateField;
	private SComboField supplierQuotationCombo;
	private SComboField itemComboField;
	private SNativeSelect unitNativeSelect;
	private STextField rateField;
	private SNativeSelect currencySelect;

	private SButton addItemButton;
	private SButton updateItemButton;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;

	private String[] allHeaders;
	private String[] reqHeaders;

	private SCollectionContainer bic;
	private CommonMethodsDao comDao;
	private SupplierQuotationDao dao;
	private CountryDao countryDao;

	private SComboField countryComboField;

	@Override
	public SPanel getGUI() {

		setSize(780, 500);

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);

		SGridLayout userGrid = new SGridLayout();
		userGrid.setSpacing(true);
		userGrid.setRows(1);
		userGrid.setColumns(6);

		SGridLayout grid = new SGridLayout();
		grid.setSpacing(true);
		grid.setRows(1);
		grid.setColumns(14);
		grid.setStyleName("po_border");

		SGridLayout buttonGrid = new SGridLayout();
		buttonGrid.setSpacing(true);
		buttonGrid.setSizeFull();
		buttonGrid.setRows(1);
		buttonGrid.setColumns(8);

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		comDao = new CommonMethodsDao();
		dao = new SupplierQuotationDao();
		countryDao=new CountryDao();

		try {

			dateField = new SDateField(null, 100, getDateFormat(),
					getWorkingDate());
			dateField.setReadOnly(true);
			supplierQuotationCombo = new SComboField(null, 200, null, "id", "");
			supplierQuotationCombo.setValue(getLoginID());

			allHeaders = new String[] { TBL_ITEM_ID, TBL_ITEM_NAME,TBL_COUNTRY_ID,TBL_COUNTRY_NAME,
					TBL_UNIT_ID, TBL_UNIT_NAME, TBL_RATE, TBL_CURRENCY_ID,
					TBL_CURRENCY_NAME };
			reqHeaders = new String[] { TBL_ITEM_NAME, TBL_COUNTRY_NAME ,TBL_UNIT_NAME, TBL_RATE,
					TBL_CURRENCY_NAME };

			table = new STable(null, 700, 300);
			table.addContainerProperty(TBL_ITEM_ID, Long.class, null,
					TBL_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_NAME, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			table.addContainerProperty(TBL_COUNTRY_ID, Long.class, null,
					TBL_COUNTRY_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_COUNTRY_NAME, String.class, null,
					getPropertyName("country"), null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT_ID, Long.class, null,
					TBL_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_UNIT_NAME, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBL_RATE, Double.class, null,
					getPropertyName("rate"), null, Align.RIGHT);
			table.addContainerProperty(TBL_CURRENCY_ID, Long.class, null,
					TBL_CURRENCY_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_CURRENCY_NAME, String.class, null,
					getPropertyName("currency"), null, Align.CENTER);
			table.setSelectable(true);
			table.setVisibleColumns(reqHeaders);

			itemComboField = new SComboField(
					null,
					150,
					new ItemDao()
							.getAllActiveItemsWithAppendingItemCode(getOfficeID()),
					"id", "name");
			itemComboField.setInputPrompt(getPropertyName("select"));
			
			List countryList = null;
			try {
				countryList = countryDao.getCountry();
			} catch (Exception e) {
				countryList = new ArrayList();
			}
			countryComboField = new SComboField(null, 120, countryList, "id",
					"name", true, "Select");
			countryComboField.setValue(getCountryID());
			
			unitNativeSelect = new SNativeSelect(null, 60,
					new UnitDao().getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			unitNativeSelect.setValue((long) 1);
			rateField = new STextField(null, 80);
			rateField.setStyleName("textfield_align_right");
			rateField.setValue("0.00");

			currencySelect = new SNativeSelect(null, 60,
					new CurrencyManagementDao().getCurrencyCode(), "id", "name");

			addItemButton = new SButton(null, getPropertyName("add"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			saveButton = new SButton(getPropertyName("save"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			deleteButton = new SButton(getPropertyName("delete"));
			deleteButton.setVisible(false);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);

			buttonLayout.addComponent(deleteButton);
			buttonGrid.addComponent(buttonLayout, 4, 0);

			grid.addComponent(new SLabel(getPropertyName("item")), 2, 0);
			grid.addComponent(itemComboField, 3, 0);
			grid.addComponent(new SLabel(getPropertyName("country")), 4, 0);
			grid.addComponent(countryComboField, 5, 0);
			grid.addComponent(new SLabel(getPropertyName("unit")), 6, 0);
			grid.addComponent(unitNativeSelect, 7, 0);
			grid.addComponent(new SLabel(getPropertyName("rate")), 8, 0);
			grid.addComponent(rateField, 9, 0);
			grid.addComponent(currencySelect, 10, 0);

			grid.addComponent(addItemButton, 11, 0);
			grid.addComponent(updateItemButton, 12, 0);
			grid.setComponentAlignment(addItemButton, Alignment.MIDDLE_CENTER);
			grid.setComponentAlignment(updateItemButton,
					Alignment.MIDDLE_CENTER);

			userGrid.addComponent(new SLabel(getPropertyName("quotations")), 1,
					0);
			userGrid.addComponent(supplierQuotationCombo, 2, 0);
			userGrid.addComponent(new SLabel(getPropertyName("date")), 4, 0);
			userGrid.addComponent(dateField, 5, 0);

			layout.addComponent(userGrid);
			layout.addComponent(table);
			layout.addComponent(grid);
			layout.addComponent(buttonGrid);

			currencySelect.setValue(getCurrencyID());

			loadQuotation(0);

			updateButton.setVisible(false);

			itemComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemComboField.getValue() != null
								&& !itemComboField.getValue().equals("")) {

							ItemModel itm = new ItemDao()
									.getItem((Long) itemComboField.getValue());
							// bic = SCollectionContainer.setList(
							// comDao.getAllItemUnitDetails(itm.getId()),
							// "id");
							// unitNativeSelect.setContainerDataSource(bic);
							// unitNativeSelect.setItemCaptionPropertyId("symbol");
							unitNativeSelect.setValue(itm.getUnit().getId());

							rateField.focus();
							rateField.selectAll();

						} else {
							unitNativeSelect.setValue((long) 1);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			addItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (doneValid()) {

						boolean added = false;
						Item item = null;
						table.setVisibleColumns(allHeaders);

						Iterator itr1 = table.getItemIds().iterator();
						while (itr1.hasNext()) {
							item = table.getItem(itr1.next());
							if (item.getItemProperty(TBL_ITEM_ID)
									.getValue()
									.toString()
									.equals(itemComboField.getValue()
											.toString())
									&& item.getItemProperty(TBL_UNIT_ID)
											.getValue()
											.toString()
											.equals(unitNativeSelect.getValue()
													.toString())) {

								item.getItemProperty(TBL_ITEM_ID).setValue(
										toLong(itemComboField.getValue()
												.toString()));
								item.getItemProperty(TBL_ITEM_NAME).setValue(
										itemComboField
												.getItemCaption(itemComboField
														.getValue()));
								
								
								item.getItemProperty(TBL_COUNTRY_ID).setValue(
										toLong(countryComboField.getValue()
												.toString()));
								item.getItemProperty(TBL_COUNTRY_NAME).setValue(
										countryComboField
												.getItemCaption(countryComboField
														.getValue()));
								
								item.getItemProperty(TBL_UNIT_ID).setValue(
										toLong(unitNativeSelect.getValue()
												.toString()));
								item.getItemProperty(TBL_UNIT_NAME)
										.setValue(
												unitNativeSelect
														.getItemCaption(unitNativeSelect
																.getValue()));
								item.getItemProperty(TBL_RATE).setValue(
										toDouble(rateField.getValue()));

								item.getItemProperty(TBL_CURRENCY_ID).setValue(
										toLong(currencySelect.getValue()
												.toString()));
								item.getItemProperty(TBL_CURRENCY_NAME)
										.setValue(
												currencySelect
														.getItemCaption(currencySelect
																.getValue()));

								added = true;

								break;
							}
						}

						if (!added) {
							Object[] row = new Object[] {
									toLong(itemComboField.getValue().toString()),
									itemComboField
											.getItemCaption(itemComboField
													.getValue()),
									toLong(countryComboField.getValue().toString()),
									countryComboField
													.getItemCaption(countryComboField
															.getValue()),
									toLong(unitNativeSelect.getValue()
											.toString()),
									unitNativeSelect
											.getItemCaption(unitNativeSelect
													.getValue()),
									toDouble(rateField.getValue()),
									(Long) currencySelect.getValue(),
									currencySelect
											.getItemCaption(currencySelect
													.getValue()) };

							int id = 0;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								id = (Integer) it.next();
							}
							id++;
							table.addItem(row, id);

						}
						table.setVisibleColumns(reqHeaders);
						itemComboField.setValue(null);
						countryComboField.setValue(getCountryID());
						unitNativeSelect.setValue((long) 1);
						rateField.setValue("0.00");
						itemComboField.focus();

					}
				}
			});

			updateItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (doneValid()) {

						if (table.getValue() != null) {

							table.setVisibleColumns(allHeaders);

							Item item = table.getItem(table.getValue());
							item.getItemProperty(TBL_ITEM_ID)
									.setValue(
											toLong(itemComboField.getValue()
													.toString()));
							item.getItemProperty(TBL_ITEM_NAME).setValue(
									itemComboField
											.getItemCaption(itemComboField
													.getValue()));
							item.getItemProperty(TBL_COUNTRY_ID).setValue(
									toLong(countryComboField.getValue()
											.toString()));
							item.getItemProperty(TBL_COUNTRY_NAME).setValue(
									countryComboField
											.getItemCaption(countryComboField
													.getValue()));
							item.getItemProperty(TBL_UNIT_ID).setValue(
									toLong(unitNativeSelect.getValue()
											.toString()));
							item.getItemProperty(TBL_UNIT_NAME).setValue(
									unitNativeSelect
											.getItemCaption(unitNativeSelect
													.getValue()));
							item.getItemProperty(TBL_RATE).setValue(
									toDouble(rateField.getValue()));

							item.getItemProperty(TBL_CURRENCY_ID)
									.setValue(
											toLong(currencySelect.getValue()
													.toString()));
							item.getItemProperty(TBL_CURRENCY_NAME).setValue(
									currencySelect
											.getItemCaption(currencySelect
													.getValue()));
						}

						table.setVisibleColumns(reqHeaders);

						itemComboField.setValue(null);
						unitNativeSelect.setValue((long) 1);
						countryComboField.setValue(getCountryID());
						rateField.setValue("0.00");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						table.setValue(null);
						itemComboField.focus();

					}
				}
			});

			supplierQuotationCombo
					.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
							if (supplierQuotationCombo.getValue() != null
									&& (Long) supplierQuotationCombo.getValue() != 0) {
								loadTableData();
								saveButton.setVisible(false);
								deleteButton.setVisible(true);
								updateButton.setVisible(true);
							} else {
								saveButton.setVisible(true);
								deleteButton.setVisible(false);
								updateButton.setVisible(false);
								dateField.setNewValue(new Date());
							}
						}
					});

			table.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (table.getValue() != null) {

						Item item = table.getItem(table.getValue());

						itemComboField.setValue(toLong(item
								.getItemProperty(TBL_ITEM_ID).getValue()
								.toString()));
						countryComboField.setValue(toLong(item
								.getItemProperty(TBL_COUNTRY_ID).getValue()
								.toString()));
						unitNativeSelect.setValue(toLong(item
								.getItemProperty(TBL_UNIT_ID).getValue()
								.toString()));

						currencySelect.setValue(toLong(item
								.getItemProperty(TBL_CURRENCY_ID).getValue()
								.toString()));

						rateField.setValue(item.getItemProperty(TBL_RATE)
								.getValue().toString());

						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
						rateField.focus();

					} else {
						itemComboField.setValue(null);
						countryComboField.setValue(getCountryID());
						unitNativeSelect.setValue((long) 1);
						rateField.setValue("0.00");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						itemComboField.focus();
					}
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
					table.removeItem(table.getValue());
				}

			});

			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.removeItem(table.getValue());
				}
			});

			grid.addShortcutListener(new ShortcutListener("Add",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			saveButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {

						try {

							SupplierQuotationModel quotationModel = new SupplierQuotationModel();
							SupplierQuotationDetailsModel detailsModel = null;
							Iterator itr1 = table.getItemIds().iterator();
							Item item = null;
							List detailsList = new ArrayList();
							while (itr1.hasNext()) {
								item = table.getItem(itr1.next());
								detailsModel = new SupplierQuotationDetailsModel();
								detailsModel.setItem(new ItemModel((Long) item
										.getItemProperty(TBL_ITEM_ID)
										.getValue()));
								detailsModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBL_UNIT_ID)
										.getValue()));
								detailsModel.setRate(toDouble(item
										.getItemProperty(TBL_RATE).getValue()
										.toString()));
								detailsModel.setCurrency(new CurrencyModel(
										(Long) item.getItemProperty(
												TBL_CURRENCY_ID).getValue()));
								detailsModel.setCountryId((Long) item
										.getItemProperty(TBL_COUNTRY_ID)
										.getValue());
								detailsList.add(detailsModel);
							}
							quotationModel
									.setQuotation_details_list(detailsList);
							quotationModel.setLogin_id(getLoginID());
							quotationModel.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));

							quotationModel.setQuotation_number(getNextSequence(
									"Supplier Quotation ID", getLoginID()));
							quotationModel.setOffice_id(getOfficeID());

							long id = dao.save(quotationModel);
							loadQuotation(id);
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
							// saveButton.setVisible(false);
							deleteButton.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("Error"),
									Type.ERROR_MESSAGE);
						}
					}
				}
			});

			updateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {

						try {

							SupplierQuotationModel quotationModel = dao
									.getQuotationModel((Long) supplierQuotationCombo
											.getValue());
							SupplierQuotationDetailsModel detailsModel = null;
							Iterator itr1 = table.getItemIds().iterator();
							Item item = null;
							List detailsList = new ArrayList();
							while (itr1.hasNext()) {
								item = table.getItem(itr1.next());
								detailsModel = new SupplierQuotationDetailsModel();
								detailsModel.setItem(new ItemModel((Long) item
										.getItemProperty(TBL_ITEM_ID)
										.getValue()));
								detailsModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBL_UNIT_ID)
										.getValue()));
								detailsModel.setRate(toDouble(item
										.getItemProperty(TBL_RATE).getValue()
										.toString()));
								detailsModel.setCurrency(new CurrencyModel(
										(Long) item.getItemProperty(
												TBL_CURRENCY_ID).getValue()));
								detailsModel.setCountryId((Long) item
										.getItemProperty(TBL_COUNTRY_ID)
										.getValue());
								detailsList.add(detailsModel);
							}
							quotationModel
									.setQuotation_details_list(detailsList);
							quotationModel.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							quotationModel.setOffice_id(getOfficeID());

							long id = dao.update(quotationModel);
							loadQuotation(id);
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
							// saveButton.setVisible(false);
							deleteButton.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("Error"),
									Type.ERROR_MESSAGE);
						}
					}
				}
			});

			deleteButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											dao.delete((Long) supplierQuotationCombo
													.getValue());
											loadQuotation(0);
											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);
										} catch (Exception e) {
											Notification.show(
													getPropertyName("Error"),

													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});
				}
			});

			dateField.setImmediate(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		pan.setContent(layout);

		return pan;
	}

	List qtnList;

	protected void loadQuotation(long id) {
		try {
			qtnList = new ArrayList();
			qtnList.add(new QuotationBean(0, "---- Create New ----"));
			qtnList.addAll(dao.getAllQuotation(getLoginID()));
			bic = SCollectionContainer.setList(qtnList, "id");
			supplierQuotationCombo.setContainerDataSource(bic);
			supplierQuotationCombo.setItemCaptionPropertyId("number");
			supplierQuotationCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadTableData() {
		try {

			SupplierQuotationModel quotationModel = dao
					.getQuotationModel((Long) supplierQuotationCombo.getValue());
			if (quotationModel != null) {
				SupplierQuotationDetailsModel detailsModel = null;
				List quotList = quotationModel.getQuotation_details_list();
				Iterator itr = quotList.iterator();
				Object[] rows = null;
				int i = 1;
				dateField.setNewValue(CommonUtil
						.getUtilFromSQLDate(quotationModel.getDate()));
				table.setVisibleColumns(allHeaders);

				while (itr.hasNext()) {
					detailsModel = (SupplierQuotationDetailsModel) itr.next();
					rows = new Object[] { detailsModel.getItem().getId(),
							detailsModel.getItem().getName(),detailsModel.getCountryId(),countryDao.getCountryName(detailsModel.getCountryId()),
							detailsModel.getUnit().getId(),
							detailsModel.getUnit().getName(),
							detailsModel.getRate(),
							detailsModel.getCurrency().getId(),
							detailsModel.getCurrency().getSymbol() };
					table.addItem(rows, i);
					i++;
				}

				table.setVisibleColumns(reqHeaders);

				// saveButton.setVisible(false);
				deleteButton.setVisible(true);
			} else {
				saveButton.setVisible(true);
				deleteButton.setVisible(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean doneValid() {
		boolean valid = true;

		itemComboField.setComponentError(null);
		unitNativeSelect.setComponentError(null);
		rateField.setComponentError(null);

		if (itemComboField.getValue() == null
				|| itemComboField.getValue().equals("")) {
			setRequiredError(itemComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
		if (unitNativeSelect.getValue() == null
				|| unitNativeSelect.getValue().equals("")) {
			setRequiredError(unitNativeSelect,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
		if (rateField.getValue() == null || rateField.getValue().equals("")) {
			setRequiredError(rateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			try {
				if (toDouble(rateField.getValue().toString()) <= 0) {
					setRequiredError(rateField,
							getPropertyName("invalid_data"), true);
					valid = false;
				}
			} catch (Exception e) {
				setRequiredError(rateField, getPropertyName("invalid_data"),
						true);
				valid = false;
			}
		}

		return valid;
	}

	@Override
	public Boolean isValid() {

		supplierQuotationCombo.setComponentError(null);
		table.setComponentError(null);

		boolean valid = true;

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, "Select Date", true);
			valid = false;
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			valid = false;
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	
	public SComboField getSupplierQuotationCombo() {
		return supplierQuotationCombo;
	}

	public void setSupplierQuotationCombo(SComboField supplierQuotationCombo) {
		this.supplierQuotationCombo = supplierQuotationCombo;
	}

	@Override
	public SComboField getBillNoFiled() {
		return supplierQuotationCombo;
	}
	
}
