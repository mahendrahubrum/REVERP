package com.webspark.Components;

import com.vaadin.ui.Label;

public class SLabel extends Label{

	public SLabel() {
		// TODO Auto-generated constructor stub
	}
	
	public SLabel(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public SLabel(String caption, int width) {
		setCaption(caption);
		setWidth(String.valueOf(width));
		// TODO Auto-generated constructor stub
	}
	
	
	
	public SLabel(String caption, String value) {
		setCaption(caption);
		setValue(value);
		// TODO Auto-generated constructor stub
	}
	
	public SLabel(String caption, String value, String style) {
		setCaption(caption);
		setValue(value);
		setStyleName(style);
		// TODO Auto-generated constructor stub
	}
	public SLabel(String caption, String value, int width) {
		setCaption(caption);
		setValue(value);
		setWidth(String.valueOf(width));
		// TODO Auto-generated constructor stub
	}
	
	public SLabel(String caption, String value, int width, String style) {
		setCaption(caption);
		setValue(value);
		setWidth(String.valueOf(width));
		setStyleName(style);
		// TODO Auto-generated constructor stub
	}
	
	

}
