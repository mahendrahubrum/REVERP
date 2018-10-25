package com.webspark.Components;

import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.ui.ListSelect;

public class SListSelect extends ListSelect {

	public SListSelect() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SListSelect(String caption, Collection<?> options) {
		super(caption, options);
		// TODO Auto-generated constructor stub
	}

	public SListSelect(String caption, Container dataSource) {
		super(caption, dataSource);
		// TODO Auto-generated constructor stub
	}

	public SListSelect(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}
	
	public SListSelect(String caption, int width, int height) {
		super(caption);
		setWidth(width+"");
		setHeight(height+"");
		// TODO Auto-generated constructor stub
	}
	
	
	public SListSelect(String caption, int width, List options, String listKey, String listValue) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	    setNullSelectionAllowed(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
		// TODO Auto-generated constructor stub
	}
	
	public SListSelect(String caption, int width, List options, String listKey, String listValue,
			boolean required) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	    setNullSelectionAllowed(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
	    
	    setRequired(required);
		// TODO Auto-generated constructor stub
	}
	
	public SListSelect(String caption, int width, int height, List options, String listKey, String listValue) {
		setCaption(caption);
		setWidth(width+"px");
		setHeight(height+"");
		setImmediate(true);
	    setNullSelectionAllowed(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
	    
	}
	
}
