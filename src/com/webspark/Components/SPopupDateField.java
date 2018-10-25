package com.webspark.Components;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.ui.PopupDateField;

public class SPopupDateField extends PopupDateField {

	
	
	public SPopupDateField() {
		super();
		setDateFormat("dd/MM/yyyy");
		// TODO Auto-generated constructor stub
	}

	public SPopupDateField(Property dataSource) throws IllegalArgumentException {
		super(dataSource);
		setDateFormat("dd/MM/yyyy");
		// TODO Auto-generated constructor stub
	}

	public SPopupDateField(String caption, Date value) {
		super(caption, value);
		setDateFormat("dd/MM/yyyy");
		// TODO Auto-generated constructor stub
	}

	public SPopupDateField(String caption, Property dataSource) {
		super(caption, dataSource);
		// TODO Auto-generated constructor stub
		setDateFormat("dd/MM/yyyy");
	}

	public SPopupDateField(String caption) {
		super();
		setDateFormat("dd/MM/yyyy");
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
