package com.webspark.Components;

import java.util.Collection;

import com.vaadin.ui.ComboBox;

public class SComboBox extends ComboBox{
	
	public SComboBox() {
		// TODO Auto-generated constructor stub
	}
	
	
	public SComboBox(String caption, Collection<?> options, int width) {
		
		super(caption, options);
		
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	        
	    setNewItemsAllowed(false);
		
		// TODO Auto-generated constructor stub
	}
	
}
