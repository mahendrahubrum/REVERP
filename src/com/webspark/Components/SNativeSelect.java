package com.webspark.Components;

import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.ui.NativeSelect;

/**
 * @author anil
 * @date 11-Nov-2015
 * @Project REVERP
 */
public class SNativeSelect extends NativeSelect{

	public SNativeSelect() {
		super();
		setNullSelectionAllowed(false);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public SNativeSelect(String caption, int width) {
		super(caption);
		setWidth(String.valueOf(width));
		setNullSelectionAllowed(false);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}

	public SNativeSelect(String caption, Collection<?> options) {
		super(caption, options);
		setNullSelectionAllowed(false);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}

	public SNativeSelect(String caption, Container dataSource) {
		super(caption, dataSource);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}

	public SNativeSelect(String caption) {
		super(caption);
		setNullSelectionAllowed(false);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * 
	 * We can use this for creating a Autocompleter. And can set list of objects(Pojo) as values. 
	 * Caption means the Label of component and width is the width of component.
     * 
     * @param options
     * 			'options' are the list of Pojos or beans
     * @param listKey
     *            'listKey' is the field name inside the pojo
     * that we need to set as listKey(this value will be the output of component.getValu() )
     * @param listValue
     *           'listValue' also a field name of the pojo that shows the values of the component in UI.
     */
	public SNativeSelect(String caption, int width, List options, String listKey, String listValue) {
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
	
	public SNativeSelect(String caption, int width, List options, String listKey, String listValue,
					boolean required ) {
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
	
	public void setNewValue(Object value){
		if(isReadOnly()){
			setReadOnly(false);
			setValue(value);
			setReadOnly(true);
		}
		else{
			setValue(value);
		}
		
	}
	
	

}
