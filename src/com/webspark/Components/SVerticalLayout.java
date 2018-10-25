package com.webspark.Components;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class SVerticalLayout extends VerticalLayout{

	public SVerticalLayout() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SVerticalLayout(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	public SVerticalLayout(Component... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}
	public SVerticalLayout(boolean spacing, Component... children) {
		super(children);
		setSpacing(spacing);
		// TODO Auto-generated constructor stub
	}
	
	
	public void setSize(int width, int height) {
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
	}
}
