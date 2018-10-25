package com.webspark.Components;

import java.util.Date;

import com.vaadin.ui.DateField;

public class SDateField extends DateField{

	public SDateField() {
		super();
		setDateFormat("dd/MM/yyyy");
		// TODO Auto-generated constructor stub
	}
	
	public SDateField(String caption) {
		super();
		setDateFormat("dd/MM/yyyy");
		setCaption(caption);
		
		// TODO Auto-generated constructor stub
	}
	
	public SDateField(String caption, int width) {
		super();
		
		setCaption(caption);
		setWidth(width+"px");
		setDateFormat("dd/MM/yyyy");
		
		// TODO Auto-generated constructor stub
	}
	
	public SDateField(String caption, int width, String dateFormat) {
		super();
		
		setCaption(caption);
		setWidth(width+"px");
		setDateFormat(dateFormat);
		// TODO Auto-generated constructor stub
	}
	
	public SDateField(String caption, int width, String dateFormat, Date date) {
		super();
		
		setCaption(caption);
		setWidth(width+"px");
		setDateFormat(dateFormat);
		setValue(date);
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
