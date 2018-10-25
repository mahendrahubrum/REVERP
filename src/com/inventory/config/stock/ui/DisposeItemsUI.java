package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.DisposeItemsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.DisposalItemsDetailsModel;
import com.inventory.config.stock.model.DisposeItemsModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.purchase.model.ItemStockModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class DisposeItemsUI extends SparkLogic{
	
	STextField quantityTextField;
	ItemPanel itemPanel;
	SComboField itemSelectCombo;
	SComboField stockComboField;
	SHorizontalLayout itemLayout;
	SHorizontalLayout buttonLayout;
	SDateField dateField;
	SNativeSelect unitSelect;
	STextField convertionQuantityField;
	STextField convertedQuantityField;
	
	SButton saveButton;
	
	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_STOCK_TYPE = "Type";
	static String TBC_STOCK_ID = "stock_id";
	static String TBC_STOCK = "Stock";
	static String TBC_QTY = "Qty";
	static String TBC_CONVERTION_QTY = "Conversion Qty";
	static String TBC_QTY_IN_BASIC_UNIT = "Qty in Basic Unit";
	static String TBC_UNIT_ID = "Unit_ID";
	static String TBC_UNIT = "Unit";
	
	STable table;
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	private SButton newItemButton;
	private SButton addItemButton;
	private SButton updateItemButton;
	private SGridLayout itemGrid;
	private SGridLayout buttonGrid;
	
	DisposeItemsDao dao;
	CommonMethodsDao commonMtdDao;
	SWindow newItemWindow;
	private SButton deleteButton;
	private SButton updateButton;
	
	SRadioButton typeButton;
	
	
	@Override
	public SPanel getGUI() {
		SPanel panel=new SPanel();
		panel.setSizeFull();
		center();
		setSize(900, 500);
		setCaption("Dispose Items");
		
		
		itemGrid = new SGridLayout();
		itemGrid.setSizeFull();
		itemGrid.setColumns(12);
		itemGrid.setRows(2);
		
		buttonGrid = new SGridLayout();
		buttonGrid.setSizeFull();
		buttonGrid.setColumns(10);
		buttonGrid.setRows(1);
		buttonGrid.setSpacing(true);
		
		allHeaders = new String[] { TBC_SN, TBC_ITEM_ID,
				TBC_ITEM_NAME,TBC_STOCK_TYPE,TBC_STOCK_ID,TBC_STOCK, TBC_QTY,TBC_CONVERTION_QTY,TBC_QTY_IN_BASIC_UNIT,TBC_UNIT_ID,TBC_UNIT };
		requiredHeaders = new String[] { TBC_SN,
				TBC_ITEM_NAME,TBC_STOCK, TBC_QTY,TBC_UNIT };
		
		try {
			SFormLayout form=new SFormLayout();
			form.setMargin(true);
			form.setSpacing(true);
			form.setSizeFull();
			
			dao=new DisposeItemsDao();
			commonMtdDao=new CommonMethodsDao();
			
			dateField=new SDateField(null, 120, getDateFormat());
			dateField.setImmediate(true);
			
			itemSelectCombo = new SComboField("Item", 120,
					dao.getAllItemsRealStckWithAffectType(getOfficeID()), "id",
					"name", true, getPropertyName("select"));
			stockComboField= new SComboField("Stock", 120);
			stockComboField.setInputPrompt(getPropertyName("select"));
			
			typeButton = new SRadioButton(null, 100, Arrays.asList(
					new KeyValue(1, "Good Stock"), new KeyValue(2,
							"Returned Stock")), "intKey", "value");
			typeButton.setValue(1);
			
			quantityTextField=new STextField("Quantity",80);
			quantityTextField.setStyleName("textfield_align_right");
			quantityTextField.setValue("0");
			convertionQuantityField = new STextField(getPropertyName("convertion_qty"), 40);
			convertionQuantityField.setStyleName("textfield_align_right");
			convertionQuantityField.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");
			convertedQuantityField = new STextField(getPropertyName("converted_qty"), 60);
			convertedQuantityField.setStyleName("textfield_align_right");
			convertedQuantityField.setDescription("Converted Quantity in Basic Unit");
			convertedQuantityField.setReadOnly(true);
			convertionQuantityField.setValue("1");
			convertionQuantityField.setVisible(false);
			convertedQuantityField.setVisible(false);
			convertionQuantityField.setImmediate(true);
			convertedQuantityField.setImmediate(true);
			
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60);
			
			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");
			newItemButton.setDescription("Add new Item");
			
			newItemWindow = new SWindow();
			newItemWindow.center();
			newItemWindow.setModal(true);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			
			SHorizontalLayout itemButtonLay = new SHorizontalLayout();
			itemButtonLay.addComponent(addItemButton);
			itemButtonLay.addComponent(updateItemButton);
			
			SHorizontalLayout hrz1 = new SHorizontalLayout();
			hrz1.addComponent(itemSelectCombo);
			hrz1.addComponent(newItemButton);
			hrz1.setComponentAlignment(newItemButton, Alignment.BOTTOM_LEFT);
			
			itemGrid.addComponent(hrz1);
			itemGrid.addComponent(typeButton);
//			itemGrid.addComponent(new SLabel(getPropertyName("stock"),50));
			itemGrid.addComponent(stockComboField);
			itemGrid.addComponent(quantityTextField);
			itemGrid.addComponent(convertionQuantityField);
			itemGrid.addComponent(convertedQuantityField);
			itemGrid.addComponent(unitSelect);
			itemGrid.addComponent(itemButtonLay);
			itemGrid.setComponentAlignment(itemButtonLay, Alignment.MIDDLE_CENTER);
			
			
			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			
			deleteButton = new SButton(getPropertyName("delete"), 80);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			deleteButton.setVisible(false);
			
			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			updateButton.setVisible(false);
			
			buttonGrid.addComponent(saveButton,4,0);
			buttonGrid.addComponent(deleteButton,6,0);
			buttonGrid.addComponent(updateButton,5,0);
		
			table = new STable(null);
			table.setMultiSelect(true);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_STOCK, String.class, null,getPropertyName("stock"), null, Align.LEFT);
			table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
			table.addContainerProperty(TBC_STOCK_TYPE, Integer.class, null,TBC_STOCK_TYPE, null, Align.LEFT);
			table.addContainerProperty(TBC_STOCK_ID, Long.class, null,TBC_STOCK_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,TBC_UNIT_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,getPropertyName("quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_CONVERTION_QTY, Double.class, null,"Conversion Qty", null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class, null,"Qty Basic Unit", null, Align.CENTER);
			
			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			
			table.setVisibleColumns(requiredHeaders);
			table.setSizeFull();
			table.setSelectable(true);
			table.setNullSelectionAllowed(true);
			table.setPageLength(table.size());
			table.setWidth("820");
			table.setHeight("200");
			
			form.addComponent(dateField);
			form.addComponent(table);
			form.addComponent(itemGrid);
			form.addComponent(buttonGrid);
			
			panel.setContent(form);
			
			
			newItemButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
					ItemPanel itemPanel = new ItemPanel();
					newItemWindow.setContent(itemPanel);
					newItemWindow.setId("ITEM");
					newItemWindow.center();
					newItemWindow.setCaption("Add New Item");
					getUI().getCurrent().addWindow(newItemWindow);
				}
			});
			
			
			newItemWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					reloadItemStocks();
				}
			});
			
			
			itemSelectCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					quantityTextField.selectAll();
					quantityTextField.focus();
					if(itemSelectCombo.getValue()!=null){
						loadStocks(toLong(itemSelectCombo.getValue().toString()));
						loadUnits(toLong(itemSelectCombo.getValue().toString()));
					}else{
						convertionQuantityField.setValue("1");
						convertedQuantityField.setVisible(false);
						convertionQuantityField.setVisible(false);
					}
				}
			});
			
			unitSelect.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							if (itemSelectCombo.getValue() != null) {

								ItemModel itm = new ItemDao().getItem((Long) itemSelectCombo.getValue());
								if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
									convertionQuantityField.setValue("1");
									convertionQuantityField.setVisible(false);
									convertedQuantityField.setVisible(false);
									convertedQuantityField.setNewValue(quantityTextField.getValue());
								} 
								else {
									convertionQuantityField.setVisible(true);
									convertedQuantityField.setVisible(true);
									double cnvr_qty = commonMtdDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(),0);
									convertionQuantityField.setValue(asString(cnvr_qty));
									convertedQuantityField.setNewValue(""+ toDouble(quantityTextField.getValue().toString())
											* toDouble(convertionQuantityField.getValue().toString()));
								}
							}
						}
					}catch(Exception e){}
				}
			});
			
			quantityTextField.setImmediate(true);
			quantityTextField.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {

							if (itemSelectCombo.getValue() != null) {

								ItemModel itm = new ItemDao().getItem((Long) itemSelectCombo.getValue());
								if (((Long) unitSelect.getValue()) == itm.getUnit().getId()) {
									convertionQuantityField.setValue("1");
									convertedQuantityField.setNewValue(quantityTextField.getValue());
								} 
								else {
									double cnvr_qty = commonMtdDao.getConvertionRate(itm.getId(), (Long) unitSelect.getValue(),0);
									convertionQuantityField.setValue(asString(cnvr_qty));
									try {
										convertedQuantityField.setNewValue(""+ toDouble(quantityTextField.getValue().toString())
												* cnvr_qty);
									} catch (Exception e) {
										convertedQuantityField.setNewValue(quantityTextField.getValue());
									}
									
								}
							}
						}
					}catch(Exception e){}
				}
			});
			
			convertionQuantityField.setImmediate(true);
			convertionQuantityField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (convertionQuantityField.getValue()==null||convertionQuantityField.getValue().equals("") || toDouble(convertionQuantityField.getValue()) <= 0) {
							convertionQuantityField.setValue("1");
						}
						convertedQuantityField.setNewValue(""+ toDouble(quantityTextField.getValue().toString())
										* toDouble(convertionQuantityField.getValue().toString()));
						
					} catch (Exception e) {
						convertionQuantityField.setValue("1");
						e.printStackTrace();
					}
				}
			});
			
			typeButton.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(itemSelectCombo.getValue()!=null)
						loadStocks(toLong(itemSelectCombo.getValue().toString()));
						
				}
			});
			
			addItemButton.addClickListener(new ClickListener() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void buttonClick(ClickEvent event) {
					if (table.getComponentError() != null)
						setRequiredError(table, null, false);
					try {
						if (isAddingValid(false)) {
							table.setVisibleColumns(allHeaders);
							double qty = 0;
							
							ItemModel itm = dao.getItem((Long) itemSelectCombo.getValue());
							qty = toDouble(quantityTextField.getValue().trim());
							
//							boolean isAdded=false;
//							Iterator itr=table.getItemIds().iterator();
//							while (itr.hasNext()) {
//								Item item = table.getItem(itr.next());
//								if((toLong(item.getItemProperty(TBC_ITEM_ID).getValue().toString().trim())==toLong(itemSelectCombo.getValue().toString().trim()))){
//									isAdded=true;
//								}
//							}
//							itr=table.getItemIds().iterator();
//							if(isAdded){
//								while (itr.hasNext()) {
//									Item item = table.getItem(itr.next());
//									if((toLong(item.getItemProperty(TBC_ITEM_ID).getValue().toString().trim())==toLong(itemSelectCombo.getValue().toString().trim()))){
//										item.getItemProperty(TBC_QTY).setValue(roundNumber(qty));
//									}
//								}
//							}
//							else{
							table.addItem(new Object[] {table.getItemIds().size() + 1,
														itm.getId(),itm.getName(),(Integer)typeButton.getValue(),(Long)stockComboField.getValue(),
														stockComboField.getItemCaption(stockComboField.getValue()),
														qty,toDouble(convertionQuantityField.getValue()),toDouble(convertedQuantityField.getValue()),
														(Long)unitSelect.getValue(),unitSelect.getItemCaption(unitSelect.getValue())}, table.getItemIds().size() + 1);
//							}
							table.setVisibleColumns(requiredHeaders);
							itemSelectCombo.setValue(null);
							quantityTextField.setValue("0.0");
							
						}
					}  catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
						
					if (selectedItems != null && selectedItems.size() == 1) {
						Item item = table.getItem(selectedItems.iterator().next());
						
						itemSelectCombo.setValue(item.getItemProperty(TBC_ITEM_ID).getValue());
						typeButton.setValue(item.getItemProperty(TBC_STOCK_TYPE).getValue());
						stockComboField.setValue(item.getItemProperty(TBC_STOCK_ID).getValue());
						quantityTextField.setValue(""+ item.getItemProperty(TBC_QTY).getValue());
						convertionQuantityField.setNewValue(""+ item.getItemProperty(TBC_CONVERTION_QTY).getValue());
						convertedQuantityField.setNewValue(""+ item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue());
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID).getValue());
						visibleAddupdateItemButton(false, true);
						itemSelectCombo.focus();
						
						}else {
							itemSelectCombo.setValue(null);
							stockComboField.setValue(null);
							quantityTextField.setValue("0.0");
							convertionQuantityField.setNewValue("1");
							typeButton.setValue(1);
							visibleAddupdateItemButton(true, false);
						}
					}
				}
			});
			
			
			itemGrid.addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});
			
			
			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void buttonClick(ClickEvent event) {
					try {
						if (isAddingValid(true)) {
							ItemModel itm = dao.getItem((Long) itemSelectCombo.getValue());
							Collection selectedItems = null;
							if (table.getValue() != null) {
								selectedItems = (Collection) table.getValue();
								if (selectedItems != null && selectedItems.size() == 1) {
									Item item = table.getItem(selectedItems.iterator().next());
									double  qty = 0;
									qty = toDouble(quantityTextField.getValue());
									
									item.getItemProperty(TBC_ITEM_ID).setValue(
											itm.getId());
									item.getItemProperty(TBC_ITEM_NAME).setValue(
											itm.getName());
									item.getItemProperty(TBC_STOCK_TYPE).setValue(
											(Integer)typeButton.getValue());
									item.getItemProperty(TBC_STOCK_ID).setValue((Long)stockComboField.getValue());
									item.getItemProperty(TBC_STOCK).setValue(stockComboField.getItemCaption(stockComboField.getValue()));
									item.getItemProperty(TBC_UNIT).setValue(unitSelect.getItemCaption(unitSelect.getValue()));
									item.getItemProperty(TBC_UNIT_ID).setValue((Long)unitSelect.getValue());
									item.getItemProperty(TBC_QTY).setValue(qty);
									item.getItemProperty(TBC_CONVERTION_QTY).setValue(toDouble(convertionQuantityField.getValue()));
									item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).setValue(toDouble(convertedQuantityField.getValue()));
									table.setVisibleColumns(requiredHeaders);
									visibleAddupdateItemButton(true, false);
									itemSelectCombo.focus();
									table.setValue(null);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			
			/*table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
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
			});*/

			
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
			
			
			saveButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							DisposeItemsModel mdl=new DisposeItemsModel();
							List<DisposalItemsDetailsModel> itemsList = new ArrayList<DisposalItemsDetailsModel>();
							DisposalItemsDetailsModel itemObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								itemObj = new DisposalItemsDetailsModel();
								item = table.getItem(it.next());
								itemObj.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
								itemObj.setQunatity((Double) item.getItemProperty(TBC_QTY).getValue());
								itemObj.setQty_in_basic_unit((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue());
								itemObj.setStockId((Long) item.getItemProperty(TBC_STOCK_ID).getValue());
								itemObj.setType((Integer) item.getItemProperty(TBC_STOCK_TYPE).getValue());
								itemObj.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
								itemsList.add(itemObj);
							}
							
							mdl.setItem_details_list(itemsList);
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							dao.save(mdl);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							reloadItemStocks();
							Date date=dateField.getValue();
							dateField.setValue(null);
							dateField.setValue(date);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						table.setVisibleColumns(allHeaders);
						table.removeAllItems();
						itemSelectCombo.setValue(null);
						quantityTextField.setValue("0.0");
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						reloadItemStocks();
						if(dateField.getValue()!=null){
							
							long id=0;
							id=dao.getDisposeItemsModelId(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), getOfficeID());
							if(id!=0){
								DisposeItemsModel mdl=dao.getDisposeItemsModel(id);
								DisposalItemsDetailsModel detObj;
								Iterator it = mdl.getItem_details_list().iterator();
								while (it.hasNext()) {
									detObj = (DisposalItemsDetailsModel) it.next();
									
									String stock=commonMtdDao.getStockModelWithDetails(detObj.getStockId()).getStock_details();
//									 TBC_SN, TBC_ITEM_ID,TBC_ITEM_NAME,TBC_STOCK_TYPE,TBC_STOCK_ID,TBC_STOCK,
//										TBC_QTY,TBC_CONVERTION_QTY,TBC_QTY_IN_BASIC_UNIT,TBC_UNIT_ID,TBC_UNIT 
									table.addItem(new Object[] {table.getItemIds().size() + 1,
																detObj.getItem().getId(),
																detObj.getItem().getName(),detObj.getType(),
																detObj.getStockId(),stock,detObj.getQunatity(),
																detObj.getQty_in_basic_unit()/detObj.getQunatity(),
																detObj.getQty_in_basic_unit(),detObj.getUnit().getId(),
																detObj.getUnit().getSymbol()},
																table.getItemIds().size() + 1);
								}
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}else{
								Notification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						table.setVisibleColumns(requiredHeaders);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			updateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							
							long id=dao.getDisposeItemsModelId(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), getOfficeID());
							DisposeItemsModel disMdl =dao.getDisposeItemsModel(id);
							
							List<DisposalItemsDetailsModel> itemsList = new ArrayList<DisposalItemsDetailsModel>();
							DisposalItemsDetailsModel itemObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								itemObj = new DisposalItemsDetailsModel();
								item = table.getItem(it.next());
								itemObj.setItem(new ItemModel((Long) item.getItemProperty(TBC_ITEM_ID).getValue()));
								itemObj.setQunatity((Double) item.getItemProperty(TBC_QTY).getValue());
								itemObj.setQty_in_basic_unit((Double) item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue());
								itemObj.setStockId((Long) item.getItemProperty(TBC_STOCK_ID).getValue());
								itemObj.setType((Integer) item.getItemProperty(TBC_STOCK_TYPE).getValue());
								itemObj.setUnit(new UnitModel((Long) item.getItemProperty(TBC_UNIT_ID).getValue()));
								itemsList.add(itemObj);
							}
							
							disMdl.setItem_details_list(itemsList);
							disMdl.setOffice(new S_OfficeModel(getOfficeID()));
							disMdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							
							dao.update(disMdl);
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							reloadItemStocks();
							Date date=dateField.getValue();
							dateField.setValue(null);
							dateField.setValue(date);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			
			deleteButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												long id=dao.getDisposeItemsModelId(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), getOfficeID());
												dao.delete(id);
												Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
												dateField.setValue(null);
												dateField.setValue(getWorkingDate());
											} catch (Exception e) {
												Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}
	
	protected void loadStocks(long itemId) {
		try {
			List lst = null;
			
			if(toInt(typeButton.getValue().toString())==1){
				lst = commonMtdDao.getStocks((Long) itemSelectCombo.getValue(),
						getSettings().isUSE_SALES_RATE_FROM_STOCK());
			}else{
				lst = commonMtdDao.getGRVStocks((Long) itemSelectCombo.getValue(),
						getSettings().isUSE_SALES_RATE_FROM_STOCK());
			}
			
			SCollectionContainer bic2 = SCollectionContainer.setList(lst, "id");
			stockComboField.setContainerDataSource(bic2);
			stockComboField.setItemCaptionPropertyId("stock_details");
			
		} catch (Exception e) {
		}
		
	}
	private void loadUnits(long itemId) {
		try {
			ItemModel itm=new ItemDao().getItem(itemId);
			SCollectionContainer bic = SCollectionContainer.setList(commonMtdDao.getAllItemUnitDetails(itm.getId()), "id");
			unitSelect.setContainerDataSource(bic);
			unitSelect.setItemCaptionPropertyId("symbol");
			unitSelect.setValue(null);
			unitSelect.setValue(itm.getUnit().getId());
		} catch (Exception e) {
		}

	}

	protected void reloadItemStocks() {
		try {
			List list = dao.getAllItemsRealStckWithAffectType(getOfficeID());
			
			if(list.size()>0){
				CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
				itemSelectCombo.setContainerDataSource(bic);
				itemSelectCombo.setItemCaptionPropertyId("name");
				if (getHttpSession().getAttribute("saved_id") != null) {
					itemSelectCombo.setValue((Long) getHttpSession().getAttribute(
							"saved_id"));
					getHttpSession().removeAttribute("saved_id");
				}
			}else{
				Notification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void visibleAddupdateItemButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}
	
	public boolean isAddingValid(boolean isUpdate) {
		boolean ret = true;
		try {
			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField,
						getPropertyName("invalid_data"), true);
				quantityTextField.focus();
				quantityTextField.selectAll();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								getPropertyName("invalid_data"), true);
						quantityTextField.focus();
						quantityTextField.selectAll();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					quantityTextField.selectAll();
					ret = false;
				}
			}

			if (itemSelectCombo.getValue() == null || itemSelectCombo.getValue().equals("")) {
				setRequiredError(itemSelectCombo, getPropertyName("invalid_selection"), true);
				itemSelectCombo.focus();
				ret = false;
			} else
				setRequiredError(itemSelectCombo, null, false);
			
			if (stockComboField.getValue() == null || stockComboField.getValue().equals("")) {
				setRequiredError(stockComboField, getPropertyName("invalid_selection"), true);
				stockComboField.focus();
				ret = false;
			} else
				setRequiredError(stockComboField, null, false);
			
			if(stockComboField.getValue()!=null ){
				double qty=0;
				try {
					qty=toDouble(convertedQuantityField.getValue().toString().trim());
				} catch (Exception e) {
					qty=0;
				}
//				if(table.getValue()!=null){
//					Collection selectedItems = null;
//					if (table.getValue() != null) {
//						selectedItems = (Collection) table.getValue();
//						if (selectedItems != null && selectedItems.size() == 1) {
//							Item item = table.getItem(selectedItems.iterator().next());
//							tbQty=(Double)item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue();
//						}
//					}
//				}
				ItemStockModel mdl=new ItemDao().getItemStockModel((Long)stockComboField.getValue());
				if((mdl.getBalance())<qty){
					setRequiredError(quantityTextField,getPropertyName("invalid_data"), true);
					quantityTextField.focus();
					quantityTextField.selectAll();
					ret = false;
				}
				else
					setRequiredError(quantityTextField, null, false);
				
			}
			
			if(ret&&!isUpdate){
				Iterator itr=table.getItemIds().iterator();
				while (itr.hasNext()) {
					Item item = table.getItem(itr.next());
					if((toLong(item.getItemProperty(TBC_STOCK_ID).getValue().toString().trim())==toLong(stockComboField.getValue().toString().trim()))){
						setRequiredError(quantityTextField,getPropertyName("already added"), true);
						quantityTextField.focus();
						quantityTextField.selectAll();
						ret = false;
					}
				}
			}
				
		} catch (Exception e) {
			ret = false;
		}
		return ret;
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
			}
			itemSelectCombo.focus();
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	

	@Override
	public Boolean isValid() {
		boolean ret=true;
		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
