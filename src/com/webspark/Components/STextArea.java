package com.webspark.Components;

import com.vaadin.ui.TextArea;

public class STextArea extends TextArea{

	public STextArea() {
		// TODO Auto-generated constructor stub
	}
	
	public STextArea(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public STextArea(String caption,int width) {
		setCaption(caption);
		setWidth(width+"px");
		// TODO Auto-generated constructor stub
	}
	
	public STextArea(String caption,int width,int height) {
		setCaption(caption);
		setWidth(width+"px");
		setHeight(height+"px");
		// TODO Auto-generated constructor stub
	}
	
	public STextArea(String caption,int width,int height, int maxLength) {
		setCaption(caption);
		setWidth(width+"px");
		setHeight(height+"px");
		setMaxLength(maxLength);
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
