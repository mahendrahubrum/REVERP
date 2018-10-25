package com.webspark.Components;

import com.vaadin.ui.TextField;

public class STextField extends TextField{

	public STextField() {
		// TODO Auto-generated constructor stub
		setImmediate(true);
	}
	
	public STextField(String caption) {
		setCaption(caption);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public STextField(String caption, String value) {
		setCaption(caption);
		setValue(value);
		// TODO Auto-generated constructor stub
	}
	
	public STextField(String caption,int width) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public STextField(String caption,int width, String value) {
		setCaption(caption);
		setWidth(width+"px");
		setValue(value);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public STextField(String caption,int width,int maxLength) {
		setCaption(caption);
		setWidth(width+"px");
		setMaxLength(maxLength);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public STextField(String caption,int width,int maxLength, boolean required) {
		setCaption(caption);
		setWidth(width+"px");
		setMaxLength(maxLength);
		setRequired(required);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public STextField(String caption,int width, boolean required) {
		setCaption(caption);
		setWidth(width+"px");
		setRequired(required);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public void setNewValue(String value){
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
