package com.webspark.Components;

import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.BalanceSheetBean;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.Align;
import com.webspark.bean.SelectionFieldBean;
import com.webspark.common.util.KeyValue;
import com.webspark.model.S_OptionGroupModel;
import com.webspark.uac.model.DivisionModel;
import com.webspark.uac.model.S_ModuleModel;

/**
 * @author anil
 * @date 11-Nov-2015
 * @Project REVERP
 */

public class SSelectionField extends SVerticalLayout{

	private static final long serialVersionUID = -8717714167972058640L;
	SButtonLink link;
	TreeTable table;
	SPopupView pop;
	private String TBC_ID="Id";
	private String TBC_NAME="Name";
	public SSelectionField(String caption,final String discription,int width,int height) {
		
		try {
			setCaption(caption);
		
		link=new SButtonLink(discription);
		link.setId("0");
		addComponent(link);
		
		table=new TreeTable();
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
		table.addContainerProperty(TBC_NAME, String.class, null, caption, null, Align.LEFT);
		table.setWidth(width+"px");
		table.setHeight(height+"px");
		table.setVisibleColumns(new Object[]{TBC_NAME});
		table.setSelectable(true);
		table.setNullSelectionAllowed(true);
		table.setNullSelectionItemId(0);
		pop=new SPopupView(null,table);
		addComponent(pop);
		
		link.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				pop.setPopupVisible(true);
				table.setValue(link.getId());
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue()!=null){
					link.setId(table.getItem(table.getValue()).getItemProperty(TBC_ID).getValue().toString());
					link.setCaption(table.getItem(table.getValue()).getItemProperty(TBC_NAME).getValue().toString());
				}else{
					link.setId("0");
					link.setCaption(discription);
				}
				pop.setPopupVisible(false);
			}
		});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void setSize(int width, int height) {
//		link.setWidth(width+"px");
		table.setWidth(width+"px");
		table.setHeight(height+"px");
	}
	
	public void setValue(Object id){
		table.setValue(id);
	}
	public long getValue(){
		long id=0;
		try {
			id=Long.parseLong(link.getId());
		} catch (Exception e) {
			id=0;
		}
		return id;
	}
	public String getItemCaption() {
		return link.getCaption();
	}
	
	public void setLayoutCaption(String caption) {
		setCaption(caption);
	}
	
	public void setItemCaption(String discription) {
		link.setCaption(discription);
	}
	
	
	public void setContainerData(List data) {
		table.setVisibleColumns(new Object[]{TBC_ID,TBC_NAME});
			Iterator iter=data.iterator();
			while (iter.hasNext()) {
				SelectionFieldBean div=(SelectionFieldBean)iter.next();
				if(div!=null){
				table.addItem(new Object[]{
						div.getId(),
						div.getValue()},div.getId());
				table.setParent(div.getId(), div.getParentId());
				table.setCollapsed(div.getId(), false);
				}
			}
		table.setVisibleColumns(new Object[]{TBC_NAME});
	}
}
