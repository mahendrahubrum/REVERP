package com.webspark.Components;

import com.vaadin.ui.Button;

public class SButton extends Button{

	public SButton() {
		// TODO Auto-generated constructor stub
	}
	
	public SButton(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	
	public SButton(String caption, int width) {
		setCaption(caption);
		setWidth(String.valueOf(width));
		// TODO Auto-generated constructor stub
	}
	
	public SButton(String caption, int width, int height) {
		setCaption(caption);
		setWidth(width+"px");
		setHeight(height+"px");
		
		// TODO Auto-generated constructor stub
	}

	public SButton(String caption, ClickListener listener) {
		super(caption, listener);
		// TODO Auto-generated constructor stub
	}
	
	public SButton(String caption, String description) {
		setCaption(caption);
		setDescription(description);
		// TODO Auto-generated constructor stub
	}
	
	public SButton(String caption, int width, int height, String id) {
		setCaption(caption);
		setWidth(width+"px");
		setHeight(height+"px");
		setId(id);
		// TODO Auto-generated constructor stub
	}
}
