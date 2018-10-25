package com.webspark.Components;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.PopupDateField;

public class SDateTimeField extends PopupDateField {

	
	
	public SDateTimeField() {
		super();
		setResolution(Resolution.SECOND);
		// TODO Auto-generated constructor stub
	}

	public SDateTimeField(Property dataSource) throws IllegalArgumentException {
		super(dataSource);
		setResolution(Resolution.SECOND);
		// TODO Auto-generated constructor stub
	}

	public SDateTimeField(String caption, Date value) {
		super(caption, value);
		setResolution(Resolution.SECOND);
		// TODO Auto-generated constructor stub
	}
	
	public SDateTimeField(String caption, int width, Date value) {
		super(caption, value);
		setResolution(Resolution.SECOND);
		setWidth(String.valueOf(width));
		// TODO Auto-generated constructor stub
	}
	
	public SDateTimeField(String caption, int width) {
		super(caption);
		setResolution(Resolution.SECOND);
		setWidth(String.valueOf(width));
		// TODO Auto-generated constructor stub
	}

	public SDateTimeField(String caption, Property dataSource) {
		super(caption, dataSource);
		// TODO Auto-generated constructor stub
		setResolution(Resolution.SECOND);
	}

	public SDateTimeField(String caption) {
		super();
		setResolution(Resolution.SECOND);
		setCaption(caption);
		
		// TODO Auto-generated constructor stub
	}
	
	public void setNewValue(Date value){
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
