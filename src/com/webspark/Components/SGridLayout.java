package com.webspark.Components;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class SGridLayout extends GridLayout{

	public SGridLayout() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SGridLayout(int columns, int rows, Component... children) {
		super(columns, rows, children);
		// TODO Auto-generated constructor stub
	}

	public SGridLayout(int columns, int rows) {
		super(columns, rows);
		// TODO Auto-generated constructor stub
	}
	public SGridLayout(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public SGridLayout(String caption,int columns, int rows) {
		super(columns, rows);
		setCaption(caption);
	}
}
