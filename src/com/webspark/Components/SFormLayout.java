package com.webspark.Components;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;


public class SFormLayout extends FormLayout{

	public SFormLayout() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SFormLayout(Component... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}
	
	public SFormLayout(String caption) {
		super();
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public SFormLayout(String caption, int width, int height) {
		super();
		setCaption(caption);
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
		// TODO Auto-generated constructor stub
	}

}
