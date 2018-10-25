package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.stock.dao.DailyQuotationDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.DailyQuotationDetailsModel;
import com.inventory.config.stock.model.DailyQuotationModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.CountryDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 17, 2013
 */
public class DailyQuotationUI extends SparkLogic {

	private static final long serialVersionUID = 5283872698521209976L;

	private static final String TBL_ITEM_ID = "Item Id";
	private static final String TBL_ITEM_NAME = "Item";
	private static final String TBL_COUNTRY_ID = "Country Id";
	private static final String TBL_COUNTRY_NAME = "Country";
	private static final String TBL_SUPPLIER_ID = "Supplier Id";
	private static final String TBL_SUPPLIER_NAME = "Supplier";
	private static final String TBL_UNIT_ID = "Unit Id";
	private static final String TBL_UNIT_NAME = "Unit";
	private static final String TBL_RATE = "Rate";
	private static final String TBL_QUANTITY = "Quantity";

	private STable table;
	private SDateField dateField;
	public SComboField userComboField;
	private SComboField SupplierComboField;
	private SComboField itemComboField;
	private SNativeSelect unitNativeSelect;
	private STextField quantityField;
	private STextField rateField;

	private SButton addItemButton;
	private SButton updateItemButton;

	private SButton saveButton;
	private SButton deleteButton;

	private String[] allHeaders;
	private String[] reqHeaders;

	private SCollectionContainer bic;
	private CommonMethodsDao comDao;
	private DailyQuotationDao dao;
	private ItemDao itemDao;

	private SButton newItemButton;
	private SDialogBox newItemWindow;
	private ItemPanel itemPanel;
	
	private SButton newSupplierButton;
	SWindow popupWindow;
	private SComboField countryComboField;
	
	CountryDao countryDao;
	
	private SDateField importDateField;
	private SButton importButton;

	@Override
	public SPanel getGUI() {

		setSize(1120, 550);

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);

		SGridLayout userGrid = new SGridLayout();
		userGrid.setSpacing(true);
		userGrid.setRows(1);
		userGrid.setColumns(15);

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
		dao = new DailyQuotationDao();
		itemDao = new ItemDao();
		countryDao = new CountryDao();

		try {

			dateField = new SDateField(null, 100, getDateFormat(),
					getWorkingDate());
			userComboField = new SComboField(null, 200,
					new UserManagementDao().getUsersWithLoginId(getOfficeID()),
					"id", "first_name");
			userComboField.setValue(getLoginID());

			if (!isSuperAdmin() && !isSystemAdmin())
				userComboField.setReadOnly(true);
			
			importDateField=new SDateField(null, 100, getDateFormat(),
					getWorkingDate());
			importButton=new SButton(null,"Import");
			importButton.setPrimaryStyleName("import_btn_style");

			allHeaders = new String[] { TBL_SUPPLIER_ID, TBL_SUPPLIER_NAME,
					TBL_ITEM_ID, TBL_ITEM_NAME,TBL_COUNTRY_ID,TBL_COUNTRY_NAME, TBL_UNIT_ID, TBL_UNIT_NAME,
					TBL_QUANTITY, TBL_RATE };
			reqHeaders = new String[] { TBL_SUPPLIER_NAME, TBL_ITEM_NAME,TBL_COUNTRY_NAME,
					TBL_UNIT_NAME, TBL_QUANTITY, TBL_RATE };

			table = new STable(null, 1000, 300);
			table.addContainerProperty(TBL_SUPPLIER_ID, Long.class, null,
					TBL_SUPPLIER_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_SUPPLIER_NAME, String.class, null,
					getPropertyName("Supplier"), null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_ID, Long.class, null,
					TBL_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_NAME, String.class, null,
					getPropertyName("item"), null, Align.CENTER);
			table.addContainerProperty(TBL_COUNTRY_ID, Long.class, null,
					TBL_COUNTRY_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_COUNTRY_NAME, String.class, null,
					getPropertyName("country"), null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT_ID, Long.class, null,
					TBL_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_UNIT_NAME, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBL_QUANTITY, Double.class, null,
					getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBL_RATE, Double.class, null,
					getPropertyName("rate"), null, Align.CENTER);
			table.setSelectable(true);
			table.setVisibleColumns(reqHeaders);

			SupplierComboField = new SComboField(null, 180,
					new SupplierDao().getAllActiveSuppliers(getOfficeID()),
					"id", "name");
			SupplierComboField
					.setInputPrompt(getPropertyName("select"));
			
			newSupplierButton = new SButton();
			newSupplierButton.setStyleName("addNewBtnStyle");
			newSupplierButton.setDescription("Add new Supplier");
			
			popupWindow = new SWindow();
			popupWindow.center();
			popupWindow.setModal(true);
			
			itemComboField = new SComboField(
					null,
					180,
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
			countryComboField = new SComboField(null, 80, countryList, "id",
					"name", true, getPropertyName("select"));
			countryComboField.setValue(getCountryID());
			
			unitNativeSelect = new SNativeSelect(null, 70,
					new UnitDao().getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			unitNativeSelect.setValue((long) 1);
			rateField = new STextField(null, 80);
			rateField.setStyleName("textfield_align_right");
			rateField.setValue("0.00");

			quantityField = new STextField(null, 40);
			quantityField.setStyleName("textfield_align_right");
			quantityField.setValue("1");

			addItemButton = new SButton(null, getPropertyName("add"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setVisible(false);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");

			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(deleteButton);
			buttonGrid.addComponent(buttonLayout, 4, 0);

			SHorizontalLayout hrz1 = new SHorizontalLayout();
			hrz1.addComponent(itemComboField);
			hrz1.addComponent(newItemButton);

			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);
			
			SHorizontalLayout hr2 = new SHorizontalLayout();
			hr2.addComponent(SupplierComboField);
			hr2.addComponent(newSupplierButton);

			grid.addComponent(new SLabel(getPropertyName("Supplier")), 0, 0);
			grid.addComponent(hr2, 1, 0);
			grid.addComponent(new SLabel(getPropertyName("item")), 2, 0);
			grid.addComponent(hrz1, 3, 0);
			grid.addComponent(new SLabel(getPropertyName("country")), 4, 0);
			grid.addComponent(countryComboField, 5, 0);
			grid.addComponent(new SLabel(getPropertyName("unit")), 6, 0);
			grid.addComponent(unitNativeSelect, 7, 0);
			grid.addComponent(new SLabel(getPropertyName("quantity")), 8, 0);
			grid.addComponent(quantityField, 9, 0);
			grid.addComponent(new SLabel(getPropertyName("rate")), 10, 0);
			grid.addComponent(rateField, 11, 0);
			grid.addComponent(addItemButton, 12, 0);
			grid.addComponent(updateItemButton, 13, 0);
			grid.setComponentAlignment(addItemButton, Alignment.MIDDLE_CENTER);
			grid.setComponentAlignment(updateItemButton,
					Alignment.MIDDLE_CENTER);

			userGrid.addComponent(new SLabel(getPropertyName("user")), 1, 0);
			userGrid.addComponent(userComboField, 2, 0);
			userGrid.addComponent(new SLabel(getPropertyName(getPropertyName("quotation_date"))), 4, 0);
			userGrid.addComponent(dateField, 5, 0);
			
			SHorizontalLayout importLay=new SHorizontalLayout();
			importLay.setSpacing(true);
			importLay.setStyleName("layout_dark_bordered");
			importLay.addComponent(new SLabel(getPropertyName("import_date")));
			importLay.addComponent(importDateField);
			importLay.addComponent(importButton);
			importLay.setComponentAlignment(importButton, Alignment.TOP_CENTER);
			userGrid.addComponent(importLay, 13, 0);
			userGrid.setComponentAlignment(importLay, Alignment.MIDDLE_RIGHT);
			userGrid.setWidth("1000px");
			userGrid.setColumnExpandRatio(1, 1f);
			userGrid.setColumnExpandRatio(2, 1f);
			userGrid.setColumnExpandRatio(4, 2f);
			userGrid.setColumnExpandRatio(5, 2f);

			layout.addComponent(userGrid);
			layout.addComponent(table);
			layout.addComponent(grid);
			layout.addComponent(buttonGrid);

			loadTableData();

			newItemWindow = new SDialogBox(getPropertyName("add_item"), 500,
					600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			newItemWindow.addComponent(itemPanel);
			
			importButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(table.getItemIds().size()>0){
						ConfirmDialog.show(getUI(), "The entered data will be cleared. Are you sure?",new ConfirmDialog.Listener() {
							
							@Override
							public void onClose(ConfirmDialog arg0) {
								if(arg0.isConfirmed()){
									importData();
								}
							}
						});
					}else{
						importData();
					}
				}
			});
			
//			newSupplierButton.addClickListener(new ClickListener() {
//				@Override
//				public void buttonClick(ClickEvent event) {
//					SupplierPannel pan = new SupplierPannel();
//					popupWindow.setContent(pan);
//					popupWindow.setId("SUPPLIER");
//					popupWindow.setCaption("Add Supplier");
//					popupWindow.center();
//					getUI().getCurrent().addWindow(popupWindow);
//				}
//			});
			
			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {

						reloadSuppliers();
						if (getHttpSession().getAttribute("saved_id") != null) {
							SupplierComboField.setValue(getHttpSession()
									.getAttribute("saved_id"));
							getHttpSession().removeAttribute("saved_id");
					}
				}
			});

			newItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
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
						} else {
							unitNativeSelect.setValue((long) 1);
						}
						rateField.selectAll();
						rateField.focus();
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
													.toString())
									&& item.getItemProperty(TBL_SUPPLIER_ID)
											.getValue()
											.toString()
											.equals(SupplierComboField
													.getValue().toString())) {

								item.getItemProperty(TBL_SUPPLIER_ID).setValue(
										toLong(SupplierComboField.getValue()
												.toString()));
								item.getItemProperty(TBL_SUPPLIER_NAME)
										.setValue(
												SupplierComboField
														.getItemCaption(SupplierComboField
																.getValue()));
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
								item.getItemProperty(TBL_QUANTITY).setValue(
										toDouble(quantityField.getValue()));
								item.getItemProperty(TBL_RATE).setValue(
										toDouble(rateField.getValue()));

								added = true;

								break;
							}
						}

						if (!added) {
							Object[] row = new Object[] {
									toLong(SupplierComboField.getValue()
											.toString()),
									SupplierComboField
											.getItemCaption(SupplierComboField
													.getValue()),
									toLong(itemComboField.getValue().toString()),
									itemComboField
											.getItemCaption(itemComboField
													.getValue()),
									toLong(countryComboField.getValue().toString()),
									countryComboField.getItemCaption(countryComboField
											        .getValue()),
									toLong(unitNativeSelect.getValue()
											.toString()),
									unitNativeSelect
											.getItemCaption(unitNativeSelect
													.getValue()),
									toDouble(quantityField.getValue()),
									toDouble(rateField.getValue()) };

							int id = 0;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								id = (Integer) it.next();
							}
							id++;
							table.addItem(row, id);

						}
						table.setVisibleColumns(reqHeaders);

						// SupplierComboField.setValue(null);
						itemComboField.setValue(null);
						unitNativeSelect.setValue((long) 1);
						rateField.setValue("0.00");
						quantityField.setValue("1");
						countryComboField.setValue(getCountryID());

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
							item.getItemProperty(TBL_SUPPLIER_ID).setValue(
									toLong(SupplierComboField.getValue()
											.toString()));
							item.getItemProperty(TBL_SUPPLIER_NAME).setValue(
									SupplierComboField
											.getItemCaption(SupplierComboField
													.getValue()));
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
							item.getItemProperty(TBL_QUANTITY).setValue(
									toDouble(quantityField.getValue()));
							item.getItemProperty(TBL_RATE).setValue(
									toDouble(rateField.getValue()));
						}

						table.setVisibleColumns(reqHeaders);

						SupplierComboField.setValue(null);
						itemComboField.setValue(null);
						unitNativeSelect.setValue((long) 1);
						rateField.setValue("0.00");
						quantityField.setValue("1");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						table.setValue(null);
						countryComboField.setValue(getCountryID());

					}
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (table.getValue() != null) {

						Item item = table.getItem(table.getValue());

						SupplierComboField.setValue(toLong(item
								.getItemProperty(TBL_SUPPLIER_ID).getValue()
								.toString()));
						itemComboField.setValue(toLong(item
								.getItemProperty(TBL_ITEM_ID).getValue()
								.toString()));
						countryComboField.setValue(toLong(item
								.getItemProperty(TBL_COUNTRY_ID).getValue()
								.toString()));
						unitNativeSelect.setValue(toLong(item
								.getItemProperty(TBL_UNIT_ID).getValue()
								.toString()));
						rateField.setValue(item.getItemProperty(TBL_RATE)
								.getValue().toString());
						quantityField.setValue(item
								.getItemProperty(TBL_QUANTITY).getValue()
								.toString());

						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
						rateField.focus();

					} else {
						SupplierComboField.setValue(null);
						itemComboField.setValue(null);
						unitNativeSelect.setValue((long) 1);
						rateField.setValue("0.00");
						quantityField.setValue("1");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						countryComboField.setValue(getCountryID());
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
						DailyQuotationModel oldModel = null;
						try {
							oldModel = dao.getQuotationModel(
									(Long) userComboField.getValue(),
									CommonUtil.getSQLDateFromUtilDate(dateField
											.getValue()));
						} catch (Exception e) {
							e.printStackTrace();
						}

						DailyQuotationModel quotationModel = new DailyQuotationModel();
						DailyQuotationDetailsModel detailsModel = null;
						Iterator itr1 = table.getItemIds().iterator();
						Item item = null;
						List detailsList = new ArrayList();
						while (itr1.hasNext()) {
							item = table.getItem(itr1.next());
							detailsModel = new DailyQuotationDetailsModel();
							detailsModel.setItem(new ItemModel((Long) item
									.getItemProperty(TBL_ITEM_ID).getValue()));
							detailsModel.setSupplier(new SupplierModel(
									(Long) item
											.getItemProperty(TBL_SUPPLIER_ID)
											.getValue()));
							detailsModel.setUnit(new UnitModel((Long) item
									.getItemProperty(TBL_UNIT_ID).getValue()));
							detailsModel.setRate(toDouble(item
									.getItemProperty(TBL_RATE).getValue()
									.toString()));
							detailsModel.setQuantity(toDouble(item
									.getItemProperty(TBL_QUANTITY).getValue()
									.toString()));
							detailsModel.setCountryId((Long) item
									.getItemProperty(TBL_COUNTRY_ID)
									.getValue());
							detailsList.add(detailsModel);
						}
						quotationModel.setQuotation_details_list(detailsList);
						quotationModel.setLogin(new S_LoginModel(
								(Long) userComboField.getValue()));
						quotationModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));

						try {
							dao.save(quotationModel, oldModel);
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
											dao.delete(dao.getQuotationModel(
													(Long) userComboField
															.getValue(),
													CommonUtil
															.getSQLDateFromUtilDate(dateField
																	.getValue())));
											loadTableData();
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

			dateField.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					loadTableData();
				}
			});
			
			userComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadTableData();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		pan.setContent(layout);

		return pan;
	}

	protected void loadTableData() {
		try {

			table.removeAllItems();
			if (dateField.getValue() != null
					&& userComboField.getValue() != null) {
				DailyQuotationModel quotationModel = dao
						.getQuotationModel((Long) userComboField.getValue(),
								CommonUtil.getSQLDateFromUtilDate(dateField
										.getValue()));
				if (quotationModel != null) {
					DailyQuotationDetailsModel detailsModel = null;
					List quotList = quotationModel.getQuotation_details_list();
					Iterator itr = quotList.iterator();
					Object[] rows = null;
					int i = 1;

					table.setVisibleColumns(allHeaders);

					while (itr.hasNext()) {
						detailsModel = (DailyQuotationDetailsModel) itr.next();
						rows = new Object[] {
								detailsModel.getSupplier().getId(),
								detailsModel.getSupplier().getName(),
								detailsModel.getItem().getId(),
								detailsModel.getItem().getName(),
								detailsModel.getCountryId(),countryDao.getCountryName(detailsModel.getCountryId()),
								detailsModel.getUnit().getId(),
								detailsModel.getUnit().getName(),
								detailsModel.getQuantity(),
								detailsModel.getRate() };

						table.addItem(rows, i);
						i++;
					}

					table.setVisibleColumns(reqHeaders);

					// saveButton.setVisible(false);
					deleteButton.setVisible(true);
				} else {
					// saveButton.setVisible(true);
					deleteButton.setVisible(false);
				}
			} else {
				// saveButton.setVisible(true);
				deleteButton.setVisible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean doneValid() {
		boolean valid = true;

		SupplierComboField.setComponentError(null);
		itemComboField.setComponentError(null);
		unitNativeSelect.setComponentError(null);
		rateField.setComponentError(null);
		quantityField.setComponentError(null);

		if (SupplierComboField.getValue() == null
				|| SupplierComboField.getValue().equals("")) {
			setRequiredError(SupplierComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
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
		if (quantityField.getValue() == null
				|| quantityField.getValue().equals("")) {
			setRequiredError(quantityField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			try {
				if (toDouble(quantityField.getValue().toString()) <= 0) {
					setRequiredError(quantityField,
							getPropertyName("invalid_data"), true);
					valid = false;
				}
			} catch (Exception e) {
				setRequiredError(quantityField,
						getPropertyName("invalid_data"), true);
				valid = false;
			}
		}

		return valid;
	}

	@Override
	public Boolean isValid() {

		userComboField.setComponentError(null);
		table.setComponentError(null);

		boolean valid = true;

		if (userComboField.getValue() == null
				|| userComboField.getValue().equals("")) {
			setRequiredError(userComboField,
					getPropertyName("invalid_selection"), true);
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

	protected void reloadItemStocks() {
		try {
			List list = itemDao.getAllItemsWithRealStck(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			itemComboField.setContainerDataSource(bic);
			itemComboField.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				itemComboField.setNewValue((Long) getHttpSession()
						.getAttribute("saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void importData() {
		try {
			DailyQuotationModel model = dao.getQuotationModel(
					(Long) userComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(importDateField
									.getValue()));
			DailyQuotationDetailsModel detailsMdl=null;
			if(model!=null){
				table.removeAllItems();
				table.setVisibleColumns(allHeaders);
				Iterator iter=model.getQuotation_details_list().iterator();
				while (iter.hasNext()) {
					detailsMdl = (DailyQuotationDetailsModel) iter.next();
					
					Object[] row = new Object[] {detailsMdl.getSupplier().getId(),
							detailsMdl.getSupplier().getName(),
							detailsMdl.getItem().getId(),
							detailsMdl.getItem().getName(),
							detailsMdl.getCountryId(),
							countryDao.getCountryName(detailsMdl.getCountryId()),
							detailsMdl.getUnit().getId(),
							detailsMdl.getUnit().getSymbol(),
							detailsMdl.getQuantity(),
							detailsMdl.getRate() };
					
					table.addItem(row,table.getItemIds().size()+1);
				}
				table.setVisibleColumns(reqHeaders);
				
				
			}else{
				SNotification.show("Nothing to import",Type.WARNING_MESSAGE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void reloadSuppliers() {
		List list;
		try {
			list = new SupplierDao().getAllActiveSuppliers(getOfficeID());
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			SupplierComboField.setContainerDataSource(bic);
			SupplierComboField.setItemCaptionPropertyId("name");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SComboField getUserComboField() {
		return userComboField;
	}

	public void setUserComboField(SComboField userComboField) {
		this.userComboField = userComboField;
	}
	
}
