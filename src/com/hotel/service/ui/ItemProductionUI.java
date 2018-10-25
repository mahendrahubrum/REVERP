package com.hotel.service.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.hotel.service.dao.ProductionDao;
import com.hotel.service.model.ProductionDetailsModel;
import com.hotel.service.model.ProductionModel;
import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.bean.ProductionBean;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */

public class ItemProductionUI extends SparkLogic {

	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String  TBC_QTY_IN_BASIC_UNIT= "Qty in Basic Unit";
	

	ProductionDao daoObj;

	SComboField productionsLists;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;

	STable table;
	
	STable childTable;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField;
	SNativeSelect unitSelect;

	SButton doneButton;
	
	SButton addItemButton;
	SButton updateItemButton;
	SButton saveProduction;
	SButton updateStockTransfer;
	SButton deleteStockTransfer;
	
	CommonMethodsDao comDao;
	
	OfficeDao pfcDao=new OfficeDao();

	ItemDao itemDao;

	SDateField date;
	SComboField itemSelect;
	
	
	STextField convertionQtyTextField, subconvertionQtyTextField;
	STextField convertedQtyTextField, subconvertedQtyTextField;
	
	
	SLabel itemName;
	
	SComboField subItemSelect;
	SNativeSelect subItemUnitSelect;
	STextField subItmQtyTextField;
	SButton addSubButton;
	SGridLayout addingSubGrid;

	SPopupView pop;
	
	UnitDao unitDao=new UnitDao();
	
	SFormLayout popLay;

	Set<ProductionBean> subItemsMap;
	
	@SuppressWarnings("deprecation")
	public ItemProductionUI() {
		
		itemName=new SLabel();
		
		subItemsMap=new HashSet<ProductionBean>();

		setId("Transfer");
		setSize(760, 500);
		
		comDao=new CommonMethodsDao();

		daoObj = new ProductionDao();
		itemDao = new ItemDao();

		hLayout = new SHorizontalLayout();
		vLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(9);
		addingGrid.setRows(2);
		
		addingSubGrid= new SGridLayout();
		addingSubGrid.setSizeFull();
		addingSubGrid.setColumns(9);
		addingSubGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(1);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(2);
		buttonsGrid.setRows(2);
		buttonsGrid.setSpacing(true);
		
		doneButton=new SButton("Done");
		
		popLay=new SFormLayout();
		
		popLay.setWidth("760");
		popLay.setHeight("350");
		
		pop=new SPopupView("",popLay);

		form.setSizeFull();

		try {
			
			convertionQtyTextField = new STextField("Cnv.Qty", 40);
			convertionQtyTextField.setStyleName("textfield_align_right");
			convertionQtyTextField.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");
			
			convertedQtyTextField= new STextField("Cnvtd.Qty", 60);
			convertedQtyTextField.setStyleName("textfield_align_right");
			convertedQtyTextField.setDescription("Converted Quantity in Basic Unit");
			convertedQtyTextField.setReadOnly(true);
			
			
			subconvertionQtyTextField = new STextField("Cnv.Qty", 40);
			subconvertionQtyTextField.setStyleName("textfield_align_right");
			subconvertionQtyTextField.setDescription("Convertion Quantity (Value that convert basic unit to selected Unit)");
			
			subconvertedQtyTextField= new STextField("Cnvtd.Qty", 60);
			subconvertedQtyTextField.setStyleName("textfield_align_right");
			subconvertedQtyTextField.setDescription("Converted Quantity in Basic Unit");
			subconvertedQtyTextField.setReadOnly(true);
			
			
			
			
			productionsLists = new SComboField(null, 125, null, "id",
					"comments", false, "New");
			
			loadProductions("New");

			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date());

			masterDetailsGrid.addComponent(new SLabel("Item Production No. :"),
					1, 0);
			masterDetailsGrid.addComponent(productionsLists, 2, 0);
			masterDetailsGrid.addComponent(new SLabel("Date :"), 6, 0);
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

			quantityTextField = new STextField("Qty", 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect("Unit", 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			
			
			subItemSelect= new SComboField("Item",250,
					itemDao.getAllPurchaseOnlyItems(getOfficeID()),
					"id", "name", true, "Select");
			subItemUnitSelect= new SNativeSelect("Unit", 60,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",	"symbol");
			subItmQtyTextField= new STextField("Qty", 60);
			subItmQtyTextField.setStyleName("textfield_align_right");
			addSubButton= new SButton(null, "Add Item");
			addSubButton.setStyleName("addItemBtnStyle");
			
			addingSubGrid.addComponent(subItemSelect);
			addingSubGrid.addComponent(subItmQtyTextField);
			addingSubGrid.addComponent(subItemUnitSelect);
			addingSubGrid.addComponent(subconvertionQtyTextField);
			addingSubGrid.addComponent(subconvertedQtyTextField);
			addingSubGrid.addComponent(addSubButton);
			
			
			
			itemSelect = new SComboField("Item",
					250, itemDao.getAllSalesOnlyItems(getOfficeID()),
					"id", "name", true, "Select");

			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(itemSelect);
			
			
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			
			addingGrid.addComponent(convertionQtyTextField);
			addingGrid.addComponent(convertedQtyTextField);
			
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

			addingGrid.setWidth("700");

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 700, 200);
			childTable= new STable(null, 700, 200);
			childTable.setWidth("700");
			childTable.setHeight("200");
			table.setMultiSelect(true);
			childTable.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					TBC_ITEM_CODE, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					TBC_ITEM_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null, TBC_QTY,
					null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null, TBC_UNIT,
					null, Align.CENTER);
			table.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class, null, TBC_QTY_IN_BASIC_UNIT,
					null, Align.CENTER);
			
			
			
			childTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			childTable.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			childTable.addContainerProperty(TBC_ITEM_CODE, String.class, null,
					TBC_ITEM_CODE, null, Align.CENTER);
			childTable.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					TBC_ITEM_NAME, null, Align.LEFT);
			childTable.addContainerProperty(TBC_QTY, Double.class, null, TBC_QTY,
					null, Align.CENTER);
			childTable.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			childTable.addContainerProperty(TBC_UNIT, String.class, null, TBC_UNIT,
					null, Align.CENTER);
			childTable.addContainerProperty(TBC_QTY_IN_BASIC_UNIT, Double.class, null, TBC_QTY_IN_BASIC_UNIT,
					null, Align.CENTER);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_ID, 1);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			table.setColumnExpandRatio(TBC_QTY, 1);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			
			
			childTable.setColumnExpandRatio(TBC_SN, (float) 0.4);
			childTable.setColumnExpandRatio(TBC_ITEM_ID, 1);
			childTable.setColumnExpandRatio(TBC_ITEM_CODE, 2);
			childTable.setColumnExpandRatio(TBC_ITEM_NAME, 3);
			childTable.setColumnExpandRatio(TBC_QTY, 1);
			childTable.setColumnExpandRatio(TBC_UNIT_ID, 1);
			childTable.setColumnExpandRatio(TBC_UNIT, 1);
			

			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY,TBC_QTY_IN_BASIC_UNIT});
			
			childTable.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_UNIT, TBC_QTY,TBC_QTY_IN_BASIC_UNIT});
			
			
			table.setSizeFull();
			table.setSelectable(true);
			table.setNullSelectionAllowed(true);
//			childTable.setSizeFull();
			childTable.setSelectable(true);
			childTable.setNullSelectionAllowed(true);
			

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, "Total :");
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setPageLength(table.size());

			table.setWidth("700");
			table.setHeight("200");

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			saveProduction = new SButton("Save", 70);
			saveProduction.setStyleName("savebtnStyle");
			saveProduction.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			updateStockTransfer = new SButton("Update", 80);
			updateStockTransfer.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updateStockTransfer.setStyleName("updatebtnStyle");

			deleteStockTransfer = new SButton("Delete", 78);
			deleteStockTransfer.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteStockTransfer.setStyleName("deletebtnStyle");

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveProduction);
			mainButtonLayout.addComponent(updateStockTransfer);
			mainButtonLayout.addComponent(deleteStockTransfer);
			updateStockTransfer.setVisible(false);
			deleteStockTransfer.setVisible(false);

			buttonsGrid.setColumnExpandRatio(0, 1);
			buttonsGrid.setColumnExpandRatio(1, 5);

			buttonsGrid.addComponent(mainButtonLayout, 1, 1);
			mainButtonLayout.setSpacing(true);
			buttonsGrid.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);

			form.addComponent(masterDetailsGrid);
			form.addComponent(pop);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(form);

			hLayout.setMargin(true);

			setContent(hLayout);

			itemSelect.focus();
			
			popLay.addComponent(new SHorizontalLayout(new SLabel(null, "Item Name  :  ", 100), itemName));
			popLay.addComponent(childTable);
			popLay.addComponent(addingSubGrid);
			popLay.addComponent(doneButton);
			pop.setPrimaryStyleName("pop_style");
			
			doneButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					pop.setPopupVisible(false);
					table.setValue(null);
				}
			});
			
			saveProduction.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						if (isValid()) {
							
							List list=new ArrayList();
							List<ProductionDetailsModel> childList;
							
							
							ProductionModel objModel;
							ProductionDetailsModel detObj;
							
							long production_no=getNextSequence(
									"Production Number", getLoginID());
							
							Iterator it2;
							ProductionBean bean;
							Object parent;
							Set set;
							Iterator it1 = table.getItemIds().iterator();
							while (it1.hasNext()) {
								parent=it1.next();
								Item item=table.getItem(parent);
								objModel=new ProductionModel();
										
								objModel.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID).getValue()));
								objModel.setProduction_no(production_no);
								objModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID).getValue()));
								objModel.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								objModel.setOffice(new S_OfficeModel(getOfficeID()));
								objModel.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
								objModel.setQty_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue());
								
								it2=subItemsMap.iterator();
								childList=new ArrayList();
								while(it2.hasNext()) {
									bean=(ProductionBean) it2.next();
									if(bean.getParent_id()==(Integer)parent) {
										detObj=new ProductionDetailsModel();
										detObj.setItem(new ItemModel(bean.getItem_id()));
										detObj.setQuantity(bean.getQuatity());
										detObj.setUnit(new UnitModel(bean.getUnit_id()));
										detObj.setQty_in_basic_unit(bean.getQty_in_basic_unit());
										childList.add(detObj);
									}
								}
								
								objModel.setDetails_list(childList);
								list.add(objModel);
							}
							
							
							daoObj.save(list);

							loadProductions(asString(production_no));

							Notification.show("Success",
									"Saved Successfully..!",
									Type.WARNING_MESSAGE);
							
							reloadStock();
							
							
							
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			productionsLists.addValueChangeListener(new ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
							try {
								
								removeAllErrors();

								updateStockTransfer.setVisible(true);
								deleteStockTransfer.setVisible(true);
								saveProduction.setVisible(false);
								if (!productionsLists.getValue().toString().equals("New")) {

									List poObj = daoObj
											.getProductionDetails(toLong(productionsLists
													.getValue().toString()));

									table.setVisibleColumns(new String[] { TBC_SN,
											TBC_ITEM_ID, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
											TBC_UNIT,TBC_QTY_IN_BASIC_UNIT});

									table.removeAllItems();
									
									subItemsMap=new HashSet<ProductionBean>();

									int parent_id=1, child_id=1;
									Iterator it2;
									Iterator it = poObj.iterator();
									ProductionDetailsModel objDetModel;
									ProductionModel invObj;
									while (it.hasNext()) {
										invObj = (ProductionModel) it.next();

										table.addItem(
												new Object[] {
														parent_id,invObj.getItem().getId(),
														invObj.getItem().getItem_code(),
														invObj.getItem().getName(),
														invObj.getQuantity(),
														invObj.getUnit().getId(),
														invObj.getUnit().getSymbol(), invObj.getQty_in_basic_unit()},parent_id);
										
										child_id=1;
										it2=invObj.getDetails_list().iterator();
										while(it2.hasNext()) {
											objDetModel=(ProductionDetailsModel) it2.next();
											
											subItemsMap.add(new ProductionBean(parent_id, child_id, objDetModel.getItem().getId(), objDetModel.getUnit().getId() , objDetModel.getQuantity()
													, objDetModel.getItem().getItem_code(), objDetModel.getItem().getName(), objDetModel.getItem().getName(),objDetModel.getQty_in_basic_unit()));
											child_id++;
										}
										
										date.setValue(invObj.getDate());
										
										parent_id++;
										
									}
									
									table.setVisibleColumns(new String[] {
											TBC_SN, TBC_ITEM_CODE,
											TBC_ITEM_NAME, TBC_UNIT, TBC_QTY });

//									date.setValue(poObj.getDate());

								} else {
									table.removeAllItems();
									subItemsMap=new HashSet<ProductionBean>();
									
									childTable.removeAllItems();
									date.setValue(getWorkingDate());

									saveProduction.setVisible(true);
									updateStockTransfer.setVisible(false);
									deleteStockTransfer.setVisible(false);
								}


								itemSelect.setValue(null);
								itemSelect.focus();
								quantityTextField.setValue("0.0");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});
			

			updateStockTransfer.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							List list=new ArrayList();
							List<ProductionDetailsModel> childList;
							
							
							ProductionModel objModel;
							ProductionDetailsModel detObj;
							
							long production_no=toLong(productionsLists.getValue().toString());
							
							Iterator it2;
							ProductionBean bean;
							Object parent;
							Set set;
							Iterator it1 = table.getItemIds().iterator();
							while (it1.hasNext()) {
								parent=it1.next();
								Item item=table.getItem(parent);
								objModel=new ProductionModel();
										
								objModel.setItem(new ItemModel((Long) item
										.getItemProperty(TBC_ITEM_ID).getValue()));
								objModel.setProduction_no(production_no);
								objModel.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID).getValue()));
								objModel.setQuantity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								objModel.setOffice(new S_OfficeModel(getOfficeID()));
								objModel.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
								objModel.setQty_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY_IN_BASIC_UNIT).getValue());
								
								it2=subItemsMap.iterator();
								childList=new ArrayList();
								while(it2.hasNext()) {
									bean=(ProductionBean) it2.next();
									if(bean.getParent_id()==(Integer)parent) {
										detObj=new ProductionDetailsModel();
										detObj.setItem(new ItemModel(bean.getItem_id()));
										detObj.setQuantity(bean.getQuatity());
										detObj.setUnit(new UnitModel(bean.getUnit_id()));
										detObj.setQty_in_basic_unit(bean.getQty_in_basic_unit());
										childList.add(detObj);
									}
								}
								
								objModel.setDetails_list(childList);
								list.add(objModel);
							}
							
							
							daoObj.update(list,production_no);

							loadProductions(asString(production_no));
							
							reloadStock();

							Notification.show("Success",
									"Updated Successfully..!",
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

					if (productionsLists.getValue() != null
							&& !productionsLists.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete(toLong(productionsLists
														.getValue().toString()));
												Notification
														.show("Success",
																"Deleted Successfully..!",
																Type.WARNING_MESSAGE);
												loadProductions("New");

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
						Object obj=selectedItems.iterator()
								.next();
						
						Item item = table.getItem(obj);
						
						itemName.setValue(item.getItemProperty(TBC_ITEM_NAME)
								.getValue().toString());
						
						loadSubTableItems((Integer) obj);
						
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						
						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						
						itemSelect.setValue(item.getItemProperty(TBC_ITEM_ID)
								.getValue());
						
						
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());
						
						convertionQtyTextField.setNewValue(asString(toDouble(item.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
								.getValue().toString())/toDouble(item.getItemProperty(TBC_QTY)
										.getValue().toString())));
						
						convertedQtyTextField.setNewValue(item.getItemProperty(TBC_QTY_IN_BASIC_UNIT)
								.getValue().toString());
						
						visibleAddupdateStockTransfer(false, true);

						itemSelect.focus();
						
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

							boolean already_added_item = false;

							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());

							double qty = 0, totalSameStockQty = 0, qty_bsc;
							
							qty = toDouble(quantityTextField.getValue());
							qty_bsc= toDouble(convertedQtyTextField.getValue());

							int id = 0;
							Item item;
							Object obj;
							Iterator itr2 = table.getItemIds().iterator();
							while (itr2.hasNext()) {
								obj=itr2.next();
								item = table.getItem(obj);

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(itemSelect.getValue()
												.toString())) {

									item.getItemProperty(TBC_QTY).setValue((Double) item
											.getItemProperty(TBC_QTY)
											.getValue()+qty);
											
											already_added_item = true;
											
											id=(Integer) obj;
								}
							}

							

							totalSameStockQty += qty;

							
							if (!already_added_item) {

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT,TBC_QTY_IN_BASIC_UNIT});

								UnitModel objUnit = unitDao
										.getUnit((Long) unitSelect.getValue());

								int ct = 0;
								Iterator it = table.getItemIds().iterator();
								while (it.hasNext()) {
									id = (Integer) it.next();
								}
								id++;


								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,
												stk.getId(),
												stk.getItem_code(),
												stk.getName(),
												qty,
												objUnit.getId(),
												objUnit.getSymbol(),qty_bsc}, id);
								

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY });

								itemSelect.setValue(null);
								itemSelect.focus();
								quantityTextField.setValue("0.0");
							}

								calculateTotals();

								itemSelect.setValue(null);
								itemSelect.focus();
								quantityTextField.setValue("0.0");
								
								Set<Integer> set=new HashSet<Integer>();
								set.add(id);
								table.setValue(set);
								
								// officeCombo.setValue(null);
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			addSubButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (childTable.getComponentError() != null)
							setRequiredError(childTable, null, false);

						if (isSubAddingValid()) {

							boolean already_added_item = false;

							ItemModel stk = itemDao.getItem((Long) subItemSelect
									.getValue());

							double qty = 0, totalSameStockQty = 0, qty_in_bsc;
							
							qty = toDouble(subItmQtyTextField.getValue());
							qty_in_bsc=toDouble(subconvertedQtyTextField.getValue());
							Item item;
							Iterator itr2 = childTable.getItemIds().iterator();
							while (itr2.hasNext()) {
								item = childTable.getItem(itr2.next());

								if (item.getItemProperty(TBC_ITEM_ID)
										.getValue()
										.toString()
										.equals(subItemSelect.getValue()
												.toString())) {
//
//									item.getItemProperty(TBC_QTY).setValue((Double) item
//											.getItemProperty(TBC_QTY)
//											.getValue()+qty);
											
											already_added_item = true;

								}
							}
							
							
							
							totalSameStockQty += qty;
							
							
							if (!already_added_item) {

								childTable.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_ID, TBC_ITEM_CODE,
										TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
										TBC_UNIT,TBC_QTY_IN_BASIC_UNIT});

								UnitModel objUnit = unitDao
										.getUnit((Long) subItemUnitSelect.getValue());

								int id = 0, ct = 0;
								Iterator it = childTable.getItemIds().iterator();
								while (it.hasNext()) {
									id = (Integer) it.next();
								}
								id++;
								
								
								
								childTable.addItem(
										new Object[] {
												childTable.getItemIds().size() + 1,
												stk.getId(),
												stk.getItem_code(),
												stk.getName(),
												qty,
												objUnit.getId(),
												objUnit.getSymbol(),qty_in_bsc}, id);
								
								subItemsMap.add(new ProductionBean((Integer) ((Set)table.getValue()).iterator().next(),id, stk.getId(), objUnit.getId()
										, qty,stk.getItem_code(),stk.getName(), objUnit.getSymbol(), qty_in_bsc));
								
								
								

								childTable.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY });

								subItemSelect.setValue(null);
								subItemSelect.focus();
								subItmQtyTextField.setValue("0.0");
								
								setRequiredError(subItemSelect,
										null, false);
								
								subItemSelect.setValue(null);
								subItemSelect.focus();
								subItmQtyTextField.setValue("0.0");
								
							}
							else {
								setRequiredError(subItemSelect,
										"Item Already Exist", true);
												
							}

								
								// officeCombo.setValue(null);
							}
					} catch (Exception e) {
						// TODO Auto-generated catch block
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
											.getValue();
								}
							}
							totalSameStockQty += qty;

//							if (totalSameStockQty <= (stk.getCurrent_balalnce() + totalUsedQty)) {

								item = table.getItem(selectedItems
										.iterator().next());

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
								item.getItemProperty(TBC_QTY_IN_BASIC_UNIT).setValue(toDouble(convertedQtyTextField.getValue()));
								
								item.getItemProperty(TBC_UNIT_ID).setValue(
										objUnit.getId());
								item.getItemProperty(TBC_UNIT).setValue(
										objUnit.getSymbol());

								table.setVisibleColumns(new String[] { TBC_SN,
										TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
										TBC_QTY });

								itemSelect.setValue(null);
								quantityTextField.setValue("0.0");

								visibleAddupdateStockTransfer(true, false);

								itemSelect.focus();
								
								Object obj=table.getValue();
								table.setValue(null);

								calculateTotals();
								
								table.setValue(obj);
							/*} else {
								setRequiredError(
										quantityTextField,
										"No sufficient quantity available. Bal : "
												+ (stk.getCurrent_balalnce()
														+ totalUsedQty
														- totalSameStockQty + qty),
										true);
								quantityTextField.focus();
							}*/
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			itemSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemSelect.getValue() != null) {
							ItemModel stk = itemDao.getItem((Long) itemSelect
									.getValue());
							unitSelect.setValue(stk.getUnit().getId());
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					quantityTextField.focus();
					quantityTextField.selectAll();

				}
			});
			
			
			
			
			unitSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (unitSelect.getValue() != null) {
							if (itemSelect.getValue() != null) {
								
								ItemModel itm = itemDao.getItem((Long) itemSelect.getValue());
								
								if(((Long) unitSelect.getValue())==itm.getUnit().getId()) {
									convertionQtyTextField.setValue("1");
									convertionQtyTextField.setVisible(false);
									convertedQtyTextField.setVisible(false);
								}
								else {
									convertionQtyTextField.setVisible(true);
									convertedQtyTextField.setVisible(true);
									
									convertionQtyTextField.setCaption("Qty - "+itm.getUnit().getSymbol());
									convertedQtyTextField.setCaption("Qty - "+itm.getUnit().getSymbol());
									
									double cnvr_qty=comDao.getConvertionRate(itm.getId(),(Long) unitSelect.getValue(),
											0);
									
									convertionQtyTextField.setValue(asString(cnvr_qty));
									
								}
								
								
								if (quantityTextField.getValue() != null
										&& !quantityTextField.getValue().equals("")) {
									
									convertedQtyTextField.setNewValue(asString(Double
											.parseDouble(quantityTextField
													.getValue())
											* Double.parseDouble(convertionQtyTextField
													.getValue())));
									
								}

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}

				}
			});
			
			
			
			
			quantityTextField.setImmediate(true);
			
			quantityTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",	"Error Message :" + e.getCause(),Type.ERROR_MESSAGE);
					}

				}
			});
			
			convertionQtyTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						try {
							if(convertionQtyTextField.getValue().equals("") || toDouble(convertionQtyTextField.getValue())<=0) {
								convertionQtyTextField.setValue("1");
							}
						} catch (Exception e) {
							convertionQtyTextField.setValue("1");
							// TODO: handle exception
						}
						
						calculateNetPrice();
						
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",	"Error Message :" + e.getCause(),Type.ERROR_MESSAGE);
					}

				}
			});
			
			
			
			subItemSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (subItemSelect.getValue() != null) {
							ItemModel stk = itemDao.getItem((Long) subItemSelect
									.getValue());
							subItemUnitSelect.setValue(stk.getUnit().getId());
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					subItmQtyTextField.focus();
					subItmQtyTextField.selectAll();

				}
			});
			
			
			
			
			subItemUnitSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (subItemUnitSelect.getValue() != null) {
							if (subItemSelect.getValue() != null) {
								
								ItemModel itm = itemDao.getItem((Long) subItemSelect.getValue());
								
								if(((Long) subItemUnitSelect.getValue())==itm.getUnit().getId()) {
									subconvertionQtyTextField.setValue("1");
									subconvertionQtyTextField.setVisible(false);
									subconvertedQtyTextField.setVisible(false);
								}
								else {
									subconvertionQtyTextField.setVisible(true);
									subconvertedQtyTextField.setVisible(true);
									
									subconvertionQtyTextField.setCaption("Qty - "+itm.getUnit().getSymbol());
									subconvertedQtyTextField.setCaption("Qty - "+itm.getUnit().getSymbol());
									
									double cnvr_qty=comDao.getConvertionRate(itm.getId(),(Long) subItemUnitSelect.getValue(),
											0);
									
									subconvertionQtyTextField.setValue(asString(cnvr_qty));
									
								}
								
								
								if (subItmQtyTextField.getValue() != null
										&& !subItmQtyTextField.getValue().equals("")) {
									
									subconvertedQtyTextField.setNewValue(asString(Double
											.parseDouble(subItmQtyTextField
													.getValue())
											* Double.parseDouble(subconvertionQtyTextField
													.getValue())));
									
								}
								
								calculateSubNetPrice();

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}

				}
			});
			
			
			
			
			subItmQtyTextField.setImmediate(true);
			
			subItmQtyTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateSubNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",	"Error Message :" + e.getCause(),Type.ERROR_MESSAGE);
					}

				}
			});
			
			subconvertionQtyTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						try {
							if(subItmQtyTextField.getValue().equals("") || toDouble(subconvertionQtyTextField.getValue())<=0) {
								subconvertionQtyTextField.setValue("1");
							}
						} catch (Exception e) {
							subconvertionQtyTextField.setValue("1");
							// TODO: handle exception
						}
						
						calculateSubNetPrice();
						
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",	"Error Message :" + e.getCause(),Type.ERROR_MESSAGE);
					}

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
					loadProductions("New");
				}
			});

			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
					pop.setPopupVisible(false);
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
			
			final Action actionsUBDelete = new Action("Delete");

			childTable.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionsUBDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteSubItem();
				}

			});
			
			

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	
	public void calculateNetPrice(){
		
		try {
			
			convertedQtyTextField.setNewValue(asString(Double.parseDouble(quantityTextField.getValue())
					* Double.parseDouble(convertionQtyTextField
							.getValue())));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public void calculateSubNetPrice(){
		
		try {
			
			subconvertedQtyTextField.setNewValue(asString(Double.parseDouble(subItmQtyTextField.getValue())
					* Double.parseDouble(subconvertionQtyTextField
							.getValue())));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public void loadSubTableItems (int id) {
		try {
			childTable.removeAllItems();
			
			childTable.setVisibleColumns(new String[] { TBC_SN,
					TBC_ITEM_ID, TBC_ITEM_CODE,
					TBC_ITEM_NAME, TBC_QTY, TBC_UNIT_ID,
					TBC_UNIT});
			int ch_id=1;
			ProductionBean bean;
			Iterator<ProductionBean> it=subItemsMap.iterator();
			while(it.hasNext()) {
				bean=it.next();
				
				if(bean.getParent_id()==id) {
					childTable.addItem(
							new Object[] {
									ch_id,
									bean.getItem_id(),
									bean.getItem_code(),
									bean.getItem_name(),
									bean.getQuatity(),
									bean.getUnit_id(),
									bean.getUnit_name()}, ch_id);
					
					ch_id++;
				}
			}
			
			childTable.setVisibleColumns(new String[] { TBC_SN,
					TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT,
					TBC_QTY });
			
			
		} catch (Exception e) {
			// TODO: handle exception
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

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField, "Enter a Quantity", true);
				quantityTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								"Quantity must be greater than Zero", true);
						quantityTextField.focus();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							"Enter a valid Quantity", true);
					quantityTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (itemSelect.getValue() == null
					|| itemSelect.getValue().equals("")) {
				setRequiredError(itemSelect, "Select an Item", true);
				itemSelect.focus();
				ret = false;
			} else
				setRequiredError(itemSelect, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}
	
	
	public boolean isSubAddingValid() {
		boolean ret = true;
		try {

			if (subItmQtyTextField.getValue() == null
					|| subItmQtyTextField.getValue().equals("")) {
				setRequiredError(subItmQtyTextField, "Enter a Quantity", true);
				subItmQtyTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(subItmQtyTextField.getValue()) <= 0) {
						setRequiredError(subItmQtyTextField,
								"Quantity must be greater than Zero", true);
						subItmQtyTextField.focus();
						ret = false;
					} else
						setRequiredError(subItmQtyTextField, null, false);
				} catch (Exception e) {
					setRequiredError(subItmQtyTextField,
							"Enter a valid Quantity", true);
					subItmQtyTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

			if (subItemSelect.getValue() == null
					|| subItemSelect.getValue().equals("")) {
				setRequiredError(subItemSelect, "Select an Item", true);
				subItemSelect.focus();
				ret = false;
			} else
				setRequiredError(subItemSelect, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}
	
	
	
	public boolean isValidChildForAll() {
		boolean avail=true;
		try {
			childTable.setComponentError(null);
			Iterator it2;
			ProductionBean bean;
			Object parent;
			boolean childAvail;
			Set set;
			Iterator it1 = table.getItemIds().iterator();
			while (it1.hasNext()) {
				parent=it1.next();
				childAvail=false;
				it2=subItemsMap.iterator();
				while(it2.hasNext()) {
					bean=(ProductionBean) it2.next();
					if(bean.getParent_id()==(Integer)parent) {
						childAvail=true;
					}
				}
				
				if(!childAvail) {
					set=new HashSet();
					set.add(parent);
					table.setValue(null);
					table.setValue(set);
					setRequiredError(childTable, "Add some materials for production.", true);
					
					avail=false;
					break;
				}
				
				
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return avail;
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
	
	
	
	
	
	
	
	public void deleteSubItem() {
		try {

			if (childTable.getValue() != null) {

				Iterator it2;
				ProductionBean bean;
				Collection selectedItems = (Collection) childTable.getValue();
				Object child;
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					child=it1.next();
					childTable.removeItem(child);
					
					it2=subItemsMap.iterator();
					while(it2.hasNext()) {
						bean=(ProductionBean) it2.next();
						
						if(bean.getParent_id()==(Integer)((Set)table.getValue()).iterator().next() &&
								bean.getChild_id()==(Integer)child) {
							it2.remove();
						}
						
						
					}
					
					
					
				}

				int SN = 0;
				Item newitem;
				Iterator it = childTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = childTable.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

				calculateTotals();
			}
			subItemSelect.focus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	

	public void loadProductions(String id) {
		try {
			productionsLists.removeAllItems();
			productionsLists.addItem("New");
			Iterator it=daoObj.getAllProductionNumbers(getOfficeID()).iterator();
			while(it.hasNext()) {
				productionsLists.addItem(asString(it.next()));
			}
			
			productionsLists.setValue(id);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadStock() {
		try {
			
			SCollectionContainer bic = SCollectionContainer.setList(itemDao.getAllSalesOnlyItems(getOfficeID()),
					"id");
			itemSelect.setContainerDataSource(bic);
			itemSelect.setItemCaptionPropertyId("name");
			
			
			SCollectionContainer bic1 = SCollectionContainer.setList(itemDao.getAllPurchaseOnlyItems(getOfficeID()),
					"id");
			subItemSelect.setContainerDataSource(bic1);
			subItemSelect.setItemCaptionPropertyId("name");
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			itemSelect.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, "Select a Date", true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		if (ret) {
			ret=isValidChildForAll();
		}
		return ret;
	}

	public void removeAllErrors() {
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (itemSelect.getComponentError() != null)
			setRequiredError(itemSelect, null, false);
	}

	public Boolean getHelp() {
		return null;
	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
