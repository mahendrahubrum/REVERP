package com.webspark.Components;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class SHTMLLabel extends Label{

	public SHTMLLabel() {
		setContentMode(ContentMode.HTML);
		// TODO Auto-generated constructor stub
	}
	
	public SHTMLLabel(String caption) {
		setContentMode(ContentMode.HTML);
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public SHTMLLabel(String caption, String value) {
		setContentMode(ContentMode.HTML);
		setCaption(caption);
		setValue(value);
		// TODO Auto-generated constructor stub
	}
	
	public SHTMLLabel(String caption, String value, int width) {
		setContentMode(ContentMode.HTML);
		setCaption(caption);
		setValue(value);
		setWidth(String.valueOf(width));
		// TODO Auto-generated constructor stub
	}
	
	public SHTMLLabel(String caption, String value, int width, int height) {
		setContentMode(ContentMode.HTML);
		setCaption(caption);
		setValue(value);
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
		// TODO Auto-generated constructor stub
	}

}
