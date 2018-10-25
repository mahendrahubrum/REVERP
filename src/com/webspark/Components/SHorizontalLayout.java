package com.webspark.Components;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class SHorizontalLayout extends HorizontalLayout{

	public SHorizontalLayout() {
		// TODO Auto-generated constructor stub
	}
	
	public SHorizontalLayout(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public void setSize(int width, int height) {
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
	}

	public SHorizontalLayout(Component... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}
	
	public SHorizontalLayout(boolean isSpacing, Component... children) {
		super(children);
		setSpacing(isSpacing);
		// TODO Auto-generated constructor stub
	}
	
	public SHorizontalLayout(String caption,boolean isSpacing, Component... children) {
		super(children);
		setCaption(caption);
		setSpacing(isSpacing);
		// TODO Auto-generated constructor stub
	}

}
