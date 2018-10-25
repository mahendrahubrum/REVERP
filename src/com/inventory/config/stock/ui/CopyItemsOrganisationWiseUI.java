package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.CopyToolOrganizationDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.SalesTypeModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.model.ItemSubGroupModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class CopyItemsOrganisationWiseUI  extends SparkLogic {

	SPanel mainPanel;
	SFormLayout mainLayout;
	SComboField fromOrganizationField;
	SComboField fromOfficeField;
	SComboField toOrganizationField;
	SComboField toOfficeField;
	SComboField categoryField;
	SCheckBox selectBox;
	STable table;
	SButton saveButton;
	SButton createNewButton;
	CopyToolOrganizationDao dao;
	
	static String TBC_SELECT = "Select";
	static String TBC_ITEM_ID = "Id";
	static String TBC_NAME = "Name";
	
	private Object allHeaders[];
	private Object requiredHeaders[];
	
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		allHeaders=new Object[]{TBC_SELECT, TBC_ITEM_ID, TBC_NAME};
		requiredHeaders=new Object[]{TBC_SELECT, TBC_NAME};
		try {
			dao=new CopyToolOrganizationDao();
			setSize(625, 625);
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			fromOrganizationField=new SComboField(null, 200, new OrganizationDao().getAllOrganizations(), "id", "name", true, "Select");
			fromOfficeField=new SComboField(getPropertyName("from_office"), 200, null, "id", "name", true, "Select");
			toOrganizationField=new SComboField(getPropertyName("to_organization"), 200, new OrganizationDao().getAllOrganizations(), "id", "name", true, "Select");
			toOfficeField=new SComboField(getPropertyName("to_office"), 200, null, "id", "name", true, "Select");
			List<KeyValue> categoryList = Arrays.asList(new KeyValue((long) 1, "Ledger"), new KeyValue((long) 2, "Item"));
			categoryField=new SComboField(getPropertyName("category"), 200, categoryList, "key", "value", true, "Select");
			selectBox=new SCheckBox("Select All");
			selectBox.setImmediate(true);
			table=new STable(null, 450, 300);
			table.addContainerProperty(TBC_SELECT, SCheckBox.class, false, getPropertyName("select"), null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null, getPropertyName("id"), null, Align.CENTER);
			table.addContainerProperty(TBC_NAME, String.class, null, getPropertyName("name"), null, Align.LEFT);
			table.setColumnExpandRatio(TBC_SELECT, 0.5f);
			table.setColumnExpandRatio(TBC_NAME, 3.5f);
			table.setSelectable(false);
			table.setVisibleColumns(requiredHeaders);
			saveButton=new SButton(getPropertyName("update"));
			createNewButton=new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("new"));
			SHorizontalLayout hrl=new SHorizontalLayout(getPropertyName("from_organization"));
			hrl.addComponent(fromOrganizationField);
			hrl.addComponent(createNewButton);
			
			SHorizontalLayout hrl1=new SHorizontalLayout();
			hrl1.addComponent(saveButton);
			
			mainLayout.addComponent(hrl);
			mainLayout.addComponent(fromOfficeField);
			mainLayout.addComponent(categoryField);
			mainLayout.addComponent(toOrganizationField);
			mainLayout.addComponent(toOfficeField);
			mainLayout.addComponent(selectBox);
			mainLayout.addComponent(table);
			mainLayout.addComponent(hrl1);
			mainLayout.setComponentAlignment(hrl1, Alignment.MIDDLE_CENTER);
			
			mainPanel.setContent(mainLayout);
			
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						fromOrganizationField.setComponentError(null);
						fromOfficeField.setComponentError(null);
						toOrganizationField.setComponentError(null);
						toOfficeField.setComponentError(null);
						categoryField.setComponentError(null);
						table.setComponentError(null);
						fromOrganizationField.setValue(null);
						fromOfficeField.setValue(null);
						toOrganizationField.setValue(null);
						toOfficeField.setValue(null);
						categoryField.setValue(null);
						selectBox.setValue(null);
						table.removeAllItems();
					} catch (ReadOnlyException e) {
						e.printStackTrace();
					} catch (ConversionException e) {
						e.printStackTrace();
					}
				}
			});
			
			
			fromOrganizationField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(fromOrganizationField.getValue()!=null){
							setRequiredError(fromOrganizationField, null, false);
							List list=new ArrayList();
							list=new OfficeDao().getAllOfficesUnderOrg((Long)fromOrganizationField.getValue());
							fromOfficeField.setContainerDataSource(SCollectionContainer.setList(list, "id"));
							fromOfficeField.setItemCaptionPropertyId("name");
							if(isLoadable()){
								loadTable();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			fromOfficeField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(fromOfficeField.getValue()!=null){
							setRequiredError(fromOfficeField, null, false);
							if(isLoadable()){
								loadTable();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			toOrganizationField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(toOrganizationField.getValue()!=null){
							setRequiredError(toOrganizationField, null, false);
							List list=new ArrayList();
							list=new OfficeDao().getAllOfficesUnderOrg((Long)toOrganizationField.getValue());
							toOfficeField.setContainerDataSource(SCollectionContainer.setList(list, "id"));
							toOfficeField.setItemCaptionPropertyId("name");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			toOfficeField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(toOfficeField.getValue()!=null){
							setRequiredError(toOfficeField, null, false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			categoryField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(categoryField.getValue()!=null){
							setRequiredError(categoryField, null, false);
							if(isLoadable()){
								loadTable();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			selectBox.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					Iterator itr=table.getItemIds().iterator();
					while (itr.hasNext()) {
						Item item = table.getItem(itr.next());
						SCheckBox check=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
						check.setValue(selectBox.getValue());
					}
				}
			});
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							Iterator itr=table.getItemIds().iterator();
							long cat=(Long)categoryField.getValue();
							
							List<LedgerModel> list=new ArrayList<LedgerModel>();
							
							Hashtable<ItemModel, ItemUnitMangementModel> hash = new Hashtable<ItemModel, ItemUnitMangementModel>();
							
							while (itr.hasNext()) {
								Item itm = table.getItem(itr.next());
								SCheckBox check=(SCheckBox)itm.getItemProperty(TBC_SELECT).getValue();
								if(check.getValue()){
									if(cat==(long)1){
										LedgerModel ledger=new LedgerDao().getLedgeer((Long)itm.getItemProperty(TBC_ITEM_ID).getValue());
										if(!dao.isLedgerExist(itm.getItemProperty(TBC_NAME).getValue().toString(), (Long)toOfficeField.getValue())){
											LedgerModel objModel = new LedgerModel();
											objModel.setName(ledger.getName());
											objModel.setGroup(new GroupModel(getSettings().getEXPENDETURE_SHOW_ACCOUNTS()));
											objModel.setCurrent_balance(0);
											objModel.setStatus(1);
											objModel.setOffice(new S_OfficeModel((Long) toOfficeField.getValue()));
											list.add(objModel);
										}
										else{
											continue;
										}
									}
									else if(cat==(long)2){
										ItemModel item=new ItemDao().getItem((Long)itm.getItemProperty(TBC_ITEM_ID).getValue());
										if(!dao.isItemExist(item.getItem_code(), (Long)toOfficeField.getValue())){
											
											ItemModel objModel = new ItemModel();
											objModel.setItem_code(item.getItem_code());
											objModel.setName(item.getName());
											objModel.setCurrent_balalnce((double)0);
											objModel.setOpening_balance((double)0);
											objModel.setStatus(item.getStatus());
											objModel.setSub_group(new ItemSubGroupModel(item.getSub_group().getId()));
											objModel.setOffice(new S_OfficeModel((Long) toOfficeField.getValue()));
											objModel.setCess_enabled(item.getCess_enabled());
											objModel.setRate(roundNumber(item.getRate()));
											objModel.setIcon(item.getIcon());
											objModel.setAffect_type(item.getAffect_type());
											objModel.setReservedQuantity(0);
											objModel.setBrand(item.getBrand());
											objModel.setItem_model(item.getItem_model());
											objModel.setDesciption(item.getDesciption());
											objModel.setSpecification(item.getSpecification());
											objModel.setColour(item.getColour());
											objModel.setSalesTax(new TaxModel(item.getSalesTax().getId()));
											objModel.setPurchaseTax(new TaxModel(item.getPurchaseTax().getId()));
											objModel.setUnit(new UnitModel(item.getUnit().getId()));
											objModel.setReorder_level(roundNumber(item.getReorder_level()));


											ItemUnitMangementModel objMdl = new ItemUnitMangementModel();

											objMdl.setAlternateUnit(objModel.getUnit().getId());
											objMdl.setBasicUnit(objModel.getUnit().getId());
											objMdl.setConvertion_rate(1);
											objMdl.setSales_type(0);
											objMdl.setItem_price(roundNumber(item.getRate()));
											objMdl.setStatus(2);
											
											hash.put(objModel, objMdl);
											
										}
										else{
											continue;
										}
									}
								}
								else {
									continue;
								}
							}
							boolean saved=false;
							if(list.size()>0){
								saved=dao.save(list);
							}
							else if(hash.size()>0){
								saved=dao.save(hash);
							}
							SNotification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}
	

	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(fromOrganizationField.getValue()==null){
			valid=false;
			setRequiredError(fromOrganizationField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(fromOrganizationField, null, false);
		
		if(fromOfficeField.getValue()==null){
			valid=false;
			setRequiredError(fromOfficeField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(fromOfficeField, null, false);
		
		if(toOrganizationField.getValue()==null){
			valid=false;
			setRequiredError(toOrganizationField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(toOrganizationField, null, false);
		
		if(toOfficeField.getValue()==null){
			valid=false;
			setRequiredError(toOfficeField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(toOfficeField, null, false);
		
		if(categoryField.getValue()==null){
			valid=false;
			setRequiredError(categoryField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(categoryField, null, false);
		
		if(table.getItemIds().size()<=0){
			valid=false;
			setRequiredError(table, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(table, null, false);

		return valid;
	}
	
	
	public Boolean isLoadable() {
		boolean valid=true;
		if(fromOrganizationField.getValue()==null){
			valid=false;
		}
		
		if(fromOfficeField.getValue()==null){
			valid=false;
		}
		
		if(categoryField.getValue()==null){
			valid=false;
		}

		return valid;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void loadTable(){
		try {
			long cat=(Long)categoryField.getValue();
			List list=new ArrayList();
			Iterator itr;
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			if(cat==(long)1){
				list=new LedgerDao().getAllActiveLedgerNames((Long)fromOfficeField.getValue());
				itr=list.iterator();
				while (itr.hasNext()) {
					LedgerModel ledger = (LedgerModel) itr.next();
					table.addItem(new Object[]{
							new SCheckBox(null, false),
							ledger.getId(),
							ledger.getName()},table.getItemIds().size()+1);
				}
			}
			else if(cat==(long)2){
				list=new ItemDao().getAllActiveItems((Long)fromOfficeField.getValue());
				itr=list.iterator();
				while (itr.hasNext()) {
					ItemModel item = (ItemModel) itr.next();
					table.addItem(new Object[]{
							new SCheckBox(null, false),
							item.getId(),
							item.getName()},table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(requiredHeaders);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}