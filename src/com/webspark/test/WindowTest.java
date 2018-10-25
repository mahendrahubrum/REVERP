package com.webspark.test;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.BuildingDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SComboField;
import com.webspark.Components.SComboSearchField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;

/**
 * @author Jinshad P.T.
 *
 * Jun 5, 2013
 */
public class WindowTest extends SparkLogic  {
	
	static String TBC_SN="SN";
	static String TBC_ITEM_ID="item_id";
	static String TBC_ITEM_CODE="Item Code";
	static String TBC_ITEM_NAME="Item Name";
	static String TBC_QTY="Qty";
	static String TBC_UNIT_ID="unit_id";
	static String TBC_UNIT="Unit";
	static String TBC_UNIT_PRICE="Unit Price";
	static String TBC_TAX_ID="TaxID";
	static String TBC_TAX_PERC="TaxPerc";
	static String TBC_TAX_AMT="TaxAmt";
	static String TBC_NET_PRICE="Net Price";
	
	
	SPanel pannel;
	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	SComboSearchField a;
	
	STable table;
	
	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	
	SComboField itemsCompo;
	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
//	STextField discount;
	STextField netPriceTextField;
	
	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	
	ItemDao itemDao=new ItemDao();
	
	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;
	
	STextField referenceNoTextField;
	SComboField buildingSelect;
	SComboField supplierSelect;
	SDateField date;
	SDateField expected_delivery_date;
	
	
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		setSize(1200, 600);
		
		pannel=new SPanel();
		hLayout=new SHorizontalLayout();
		vLayout=new SVerticalLayout();
		form=new SFormLayout();
		
		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(8);
		addingGrid.setRows(2);
		
		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
//		masterDetailsGrid.setWidth("99%");
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(2);
		
		
		qtyTotal=new SLabel(null);
		taxTotal=new SLabel(null);
		netTotal=new SLabel(null);
		qtyTotal.setValue("0.0");
		taxTotal.setValue("0.0");
		netTotal.setValue("0.0");
//		netTotal.setStyleName("text_align_right");
		
		pannel.setSizeFull();
		form.setSizeFull();
		
		
		
		
		try {
			
			referenceNoTextField=new STextField(null, 120);
			date=new SDateField(null, 120,"dd/MMM/yyyy", new Date());
			expected_delivery_date=new SDateField(null, 120,"dd/MMM/yyyy", new Date());
			
			
			buildingSelect = new SComboField(null, 160,
					new BuildingDao().getAllActiveBuildingNamesUnderOffice(getOfficeID()),
					"id", "name", true, "Select");
			supplierSelect = new SComboField(null, 250,
					new LedgerDao().getAllActiveLedgerNames(getOfficeID()),
					"id", "name", true, "Select");
			
			
			masterDetailsGrid.addComponent(new SLabel("Ref. No. :"), 1, 0);
			masterDetailsGrid.addComponent(referenceNoTextField, 2, 0);
//			masterDetailsGrid.addComponent(new SLabel("Tax Total :"), 5, 0);
//			masterDetailsGrid.addComponent(taxTotal, 6, 0);
			masterDetailsGrid.addComponent(new SLabel("Date :"), 6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(referenceNoTextField, Alignment.MIDDLE_LEFT);
			masterDetailsGrid.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
//			masterDetailsGrid.setComponentAlignment(netTotal, Alignment.MIDDLE_RIGHT);
			
			masterDetailsGrid.setColumnExpandRatio(1, 1);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);
			
			masterDetailsGrid.addComponent(new SLabel("Supplier :"), 1, 1);
			masterDetailsGrid.addComponent(supplierSelect, 2, 1);
			masterDetailsGrid.addComponent(new SLabel("Building :"), 3, 1);
			masterDetailsGrid.addComponent(buildingSelect, 4, 1);
			masterDetailsGrid.addComponent(new SLabel("Expected Delivery Date :"), 6, 1);
			masterDetailsGrid.addComponent(expected_delivery_date, 8, 1);
			
			
			masterDetailsGrid.setStyleName("master_border");
			
			
			
			
			
			
			
			
			
			
			
			itemsCompo=new SComboField("Item", 250, new ItemDao().getAllActiveItems(getOfficeID()), "id", "name");
			
			quantityTextField=new STextField("Qty", 60);
			quantityTextField.setStyleName("text_align_right");
			unitSelect=new SNativeSelect("Unit", 60, new UnitDao().getAllActiveUnits(getOrganizationID()),
					"id", "symbol");
			unitPriceTextField=new STextField("Unit Price", 100);
			unitPriceTextField.setValue("0.00");
//			unitPriceTextField.setStyleName("text_align_right");
			
			taxSelect=new SNativeSelect("Tax", 80, new TaxDao().getAllActiveTaxesFromType(getOfficeID(), 2),
					"id", "name");
//			discount=new STextField("Discount");
			netPriceTextField=new STextField("Net Price", 100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("text_align_right");
			
			netPriceTextField.setReadOnly(true);
			addItemButton=new SNativeButton("Add Item");
			updateItemButton=new SNativeButton("Update");
			updateItemButton.setVisible(false);
			
			SFormLayout buttonLay=new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);
			
			addingGrid.addComponent(itemsCompo);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			addingGrid.addComponent(unitPriceTextField);
			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(buttonLay);
			
			addingGrid.setSpacing(true);
			
			addingGrid.setStyleName("po_border");
			
			form.setStyleName("po_style");
			
			table=new STable(null, 1000,200);
			
			table.setMultiSelect(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null,"#", null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID , null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,TBC_ITEM_CODE, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,TBC_ITEM_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,TBC_QTY, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,TBC_UNIT, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,TBC_UNIT_PRICE, null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null,TBC_TAX_ID, null, Align.CENTER);
	        table.addContainerProperty(TBC_TAX_PERC, Double.class, null,TBC_TAX_PERC, null, Align.RIGHT);
	        table.addContainerProperty(TBC_TAX_AMT, Double.class, null,"Tax Amt", null, Align.RIGHT);
	        table.addContainerProperty(TBC_NET_PRICE, Double.class, null,TBC_NET_PRICE, null, Align.RIGHT);
	        
	        table.setColumnExpandRatio(TBC_SN,1);
	        table.setColumnExpandRatio(TBC_ITEM_ID,1);
	        table.setColumnExpandRatio(TBC_ITEM_CODE,2);
	        table.setColumnExpandRatio(TBC_ITEM_NAME,4);
	        table.setColumnExpandRatio(TBC_QTY,2);
	        table.setColumnExpandRatio(TBC_UNIT_ID,1);
	        table.setColumnExpandRatio(TBC_UNIT,1);
	        table.setColumnExpandRatio(TBC_UNIT_PRICE,2);
	        table.setColumnExpandRatio(TBC_TAX_AMT,1);
	        table.setColumnExpandRatio(TBC_TAX_PERC,1);
	        table.setColumnExpandRatio(TBC_NET_PRICE,3);
	        
	        table.setVisibleColumns(new String[] {TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY , TBC_UNIT,
					TBC_UNIT_PRICE, TBC_TAX_AMT , TBC_NET_PRICE});
	        
	        table.setSizeFull();
	        table.setSelectable(true);
//	        table.setEditable(true);
	        
	        
	        
	        table.setFooterVisible(true);
	        table.setColumnFooter(TBC_ITEM_NAME, "Total :");
	        table.setColumnFooter(TBC_QTY, String.valueOf(0.0));
	        table.setColumnFooter(TBC_TAX_AMT, String.valueOf(0.0));
	        table.setColumnFooter(TBC_NET_PRICE, String.valueOf(0.0));

	        // Adjust the table height a bit
	        table.setPageLength(table.size());
	        
	        
			table.setWidth("1130");
			table.setHeight("200");
			

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			
			form.setWidth("700");
			
			
			hLayout.addComponent(form);
			
			hLayout.setMargin(true);
			
			supplierSelect.focus();
			
			pannel.setContent(hLayout);
			
			
			
			Object a=table.getValue();
			Collection aa=(Collection) a;
			
			table.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					
					Collection selectedItems=null;
					
					if(table.getValue()!=null){
						selectedItems=(Collection) table.getValue();
					}
					
					if(selectedItems!=null && selectedItems.size()==1) {
					
						Item item=table.getItem(selectedItems.iterator().next());
						
//						item.getItemProperty( TBC_ITEM_NAME).setValue("JPTTTTTT");
						
						itemsCompo.setValue(item.getItemProperty( TBC_ITEM_ID).getValue());
						quantityTextField.setValue(""+item.getItemProperty( TBC_QTY).getValue());
						unitSelect.setValue(item.getItemProperty( TBC_UNIT_ID).getValue());
						unitPriceTextField.setValue(""+item.getItemProperty( TBC_UNIT_PRICE).getValue());
						taxSelect.setValue(item.getItemProperty( TBC_TAX_ID).getValue());
						
						setNetValue((Double) item.getItemProperty( TBC_NET_PRICE).getValue());
						
						visibleAddUpdateButton(false,true);
						
						itemsCompo.focus();
						
//						item.getItemProperty( TBC_ITEM_NAME).setValue("JPTTTTTT");
						
					}
					else{
						itemsCompo.setValue(null);
	        			 itemsCompo.focus();
	        			 quantityTextField.setValue("0.0");
	        			 unitPriceTextField.setValue("0.0");
	        			 setNetValue(0.0);
	        			 
	        			 visibleAddUpdateButton(true,false);
	        			 
	        			 itemsCompo.focus();
					}
					
				}

			});
			
			
			addItemButton.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
	        			if(isAddingValid()){
	        			
	        			
		        			double price=0, qty=0, totalAmt=0;	
		        			
		        			price=Double.parseDouble(unitPriceTextField.getValue());
		        			qty=Double.parseDouble(quantityTextField.getValue());
		        			
		        			setNetValue(price*qty);
		        			
		        			table.setVisibleColumns(new String[] {TBC_SN, TBC_ITEM_ID,TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
		        					TBC_UNIT_PRICE,TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC , TBC_NET_PRICE});
		        			
		        			ItemModel itm=itemDao.getItem((Long) itemsCompo.getValue());
		        			UnitModel objUnit=new UnitDao().getUnit((Long) unitSelect.getValue());
		        			
		        			double tax_amt=0, tax_perc=0;
		        			
		        			TaxModel objTax=new TaxDao().getTax((Long) taxSelect.getValue());
		        			
		        			if(objTax.getValue_type()==1){
		        				tax_perc=objTax.getValue();
		        				tax_amt=price*qty*objTax.getValue()/100;
		        			}
		        			else{
		        				tax_perc=0;
		        				tax_amt=objTax.getValue();
		        			}
		        			
		        			totalAmt=price*qty+tax_amt;
		        			
		        			int id=0, ct=0;
		        			Iterator it=table.getItemIds().iterator();
		        			while (it.hasNext()) {
		        				id=(Integer) it.next();
							}
		        			id++;
		        					
		        			 table.addItem(new Object[] {table.getItemIds().size()+1, itm.getId(), itm.getItem_code(),
		        					 itm.getName(), qty , objUnit.getId() , objUnit.getSymbol(),	
		        					 Double.parseDouble(unitPriceTextField.getValue()), objTax.getId(), tax_amt, tax_perc, totalAmt}, id);
		        			
		        			 table.setVisibleColumns(new String[] {TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY , TBC_UNIT,
		        						TBC_UNIT_PRICE, TBC_TAX_AMT , TBC_NET_PRICE});
		        			
		        			
		        			
		        			 
		        			 itemsCompo.setValue(null);
		        			 itemsCompo.focus();
		        			 quantityTextField.setValue("0.0");
		        			 unitPriceTextField.setValue("0.0");
		        			 setNetValue(0.0);
		        			 
		        			 calculateTotals();
		        			 
		        			itemsCompo.focus();
	        			}
	        			
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
			
			
			
			
			updateItemButton.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
	        			if(isAddingValid()){
	        				
	        				Collection selectedItems=(Collection) table.getValue();
	        				
	        				Item item=table.getItem(selectedItems.iterator().next());
	        				
		        			double price=0, qty=0, totalAmt=0;	
		        			
		        			price=Double.parseDouble(unitPriceTextField.getValue());
		        			qty=Double.parseDouble(quantityTextField.getValue());
		        			
		        			setNetValue(price*qty);
		        			
//		        			table.setVisibleColumns(new String[] {TBC_SN, TBC_ITEM_ID,TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
//		        					TBC_UNIT_PRICE,TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC , TBC_NET_PRICE});
		        			
		        			ItemModel itm=itemDao.getItem((Long) itemsCompo.getValue());
		        			UnitModel objUnit=new UnitDao().getUnit((Long) unitSelect.getValue());
		        			
		        			double tax_amt=0, tax_perc=0;
		        			
		        			TaxModel objTax=new TaxDao().getTax((Long) taxSelect.getValue());
		        			
		        			if(objTax.getValue_type()==1){
		        				tax_perc=objTax.getValue();
		        				tax_amt=price*qty*objTax.getValue()/100;
		        			}
		        			else{
		        				tax_perc=0;
		        				tax_amt=objTax.getValue();
		        			}
		        			
		        			totalAmt=price*qty+tax_amt;
		        			
//		        			int id=(Integer) table.getValue();
//		        			table.removeItem(table.getValue());
//		        			 table.addItem(new Object[] {id, itm.getId(), itm.getItem_code(),
//		        					 itm.getName(), qty , objUnit.getId() , objUnit.getSymbol(),	
//		        					 Double.parseDouble(unitPriceTextField.getValue()), objTax.getId(), tax_amt, tax_perc, totalAmt}, id);
		        			
		        			 item.getItemProperty( TBC_ITEM_ID).setValue(itm.getId());
		        			 item.getItemProperty( TBC_ITEM_CODE).setValue(itm.getItem_code());
		        			 item.getItemProperty( TBC_ITEM_NAME).setValue( itm.getName());
		        			 item.getItemProperty( TBC_QTY).setValue(qty);
		        			 item.getItemProperty( TBC_UNIT_ID).setValue(objUnit.getId());
		        			 item.getItemProperty( TBC_UNIT).setValue(objUnit.getSymbol());
		        			 item.getItemProperty( TBC_UNIT_PRICE).setValue(Double.parseDouble(unitPriceTextField.getValue()));
		        			 item.getItemProperty( TBC_TAX_ID).setValue(objTax.getId());
		        			 item.getItemProperty( TBC_TAX_AMT).setValue(tax_amt);
		        			 item.getItemProperty( TBC_TAX_PERC).setValue(tax_perc);
		        			 item.getItemProperty( TBC_NET_PRICE).setValue(totalAmt);
		        			 
		        			 
		        			 table.setVisibleColumns(new String[] {TBC_SN, TBC_ITEM_CODE, TBC_ITEM_NAME,TBC_QTY , TBC_UNIT,
		        						TBC_UNIT_PRICE, TBC_TAX_AMT , TBC_NET_PRICE});
		        			
		        			
		        			
		        			 
		        			 itemsCompo.setValue(null);
		        			 itemsCompo.focus();
		        			 quantityTextField.setValue("0.0");
		        			 unitPriceTextField.setValue("0.0");
		        			 setNetValue(0.0);
		        			 
		        			 visibleAddUpdateButton(true, false);
		        			 
		        			itemsCompo.focus();
		        			
		        			table.setValue(null);
		        			
		        			calculateTotals();
		        			
	        			}
	        			
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
			
			
			
			
			
			itemsCompo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (itemsCompo.getValue() != null) {
							ItemModel itm = new ItemDao()
									.getItem((Long) itemsCompo.getValue());
							taxSelect.setValue(itm.getPurchaseTax().getId());
							unitSelect.setValue(itm.getUnit().getId());
							
							
							
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					quantityTextField.selectAll();
					quantityTextField.focus();
					
					
				}
			});
			
			
			
			addingGrid.addShortcutListener(new ShortcutListener("Submit Item", ShortcutAction.KeyCode.ENTER, null) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	if(addItemButton.isVisible())
		        		addItemButton.click();
		        	else
		        		updateItemButton.click();
		        }
		    });
			
			table.addShortcutListener(new ShortcutListener("Delete Item", ShortcutAction.KeyCode.DELETE, new int[] {
                    ShortcutAction.ModifierKey.SHIFT}) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	deleteItem();
		        }
		    });
			
			addingGrid.addShortcutListener(new ShortcutListener("Clear entereded and edited data and Add new", ShortcutAction.KeyCode.ESCAPE, null) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	table.setValue(null);
		        }
		    });
			
			
			final Action actionDelete = new Action("Delete");

	        table.addActionHandler(new Action.Handler() {
	            @Override
	            public Action[] getActions(final Object target, final Object sender) {
//	            	if(deleteItemButton.isVisible())
//		        		deleteItemButton.click();
	            	 return new Action[] { actionDelete };
	            }

	            @Override
	            public void handleAction(final Action action, final Object sender,
	                    final Object target) {
	            	deleteItem();
	            }

	        });
			
			
			
			
			
			
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return pannel;
	}
	
	
	public void calculateTotals(){
		try {
			
			double qty_ttl=0, tax_ttl=0, net_ttl=0;
			
			Iterator it=table.getItemIds().iterator();
			while (it.hasNext()) {
				Item item=table.getItem(it.next());
				
				qty_ttl+=(Double)item.getItemProperty( TBC_QTY).getValue();
				tax_ttl+=(Double)item.getItemProperty( TBC_TAX_AMT).getValue();
				net_ttl+=(Double)item.getItemProperty( TBC_NET_PRICE).getValue();
			}
			
			table.setColumnFooter(TBC_QTY, String.valueOf(qty_ttl));
	        table.setColumnFooter(TBC_TAX_AMT, String.valueOf(tax_ttl));
	        table.setColumnFooter(TBC_NET_PRICE, String.valueOf(net_ttl));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	
	public boolean isAddingValid(){
		boolean ret=true;
		try {
			
			if(unitPriceTextField.getValue()==null || unitPriceTextField.getValue().equals("")){
				setRequiredError(unitPriceTextField, "Enter Unit Price",true);
				unitPriceTextField.focus();
				ret=false;
			}
			else {
				try {
					if(Double.parseDouble(unitPriceTextField.getValue())<0){
						setRequiredError(unitPriceTextField, "Enter a valid Price",true);
						unitPriceTextField.focus();
						ret=false;
					}
					else
						setRequiredError(unitPriceTextField, null,false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField, "Enter a valid Price",true);
					unitPriceTextField.focus();
					ret=false;
					// TODO: handle exception
				}
			}
			
			if(quantityTextField.getValue()==null || quantityTextField.getValue().equals("")){
				setRequiredError(quantityTextField, "Enter a Quantity",true);
				quantityTextField.focus();
				ret=false;
			}
			else {
				try {
					if(Double.parseDouble(quantityTextField.getValue())<=0){
						setRequiredError(quantityTextField, "Quantity must be greater than Zero",true);
						quantityTextField.focus();
						ret=false;
					}
					else
						setRequiredError(quantityTextField, null,false);
				} catch (Exception e) {
					setRequiredError(quantityTextField, "Enter a valid Quantity",true);
					quantityTextField.focus();
					ret=false;
					// TODO: handle exception
				}
			}
			
			if(itemsCompo.getValue()==null || itemsCompo.getValue().equals("")){
				setRequiredError(itemsCompo, "Select an Item",true);
				itemsCompo.focus();
				ret=false;
			}
			else
				setRequiredError(itemsCompo, null,false);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ret;
		
	}
	
	public void setNetValue(double netValue){
		netPriceTextField.setReadOnly(false);
		netPriceTextField.setValue(netValue+"");
		netPriceTextField.setReadOnly(true);
	}
	
	public void visibleAddUpdateButton(boolean AddVisible, boolean UpdateVisible){
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}
	
	public void deleteItem(){
		try {
			
			if(table.getValue()!=null){
				
				Collection selectedItems=(Collection) table.getValue();
				Iterator it1=selectedItems.iterator();
				while(it1.hasNext()){
//					Item item=table.getItem(selectedItems.iterator().next());
					table.removeItem(it1.next());
				}
				
				
				int SN=0;
				Iterator it=table.getItemIds().iterator();
    			while (it.hasNext()) {
    				SN++;
    				
    				Item newitem=table.getItem((Integer) it.next());
					
					newitem.getItemProperty( TBC_SN).setValue(SN);
    				
				}
				
    			calculateTotals();
			}
			itemsCompo.focus();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
