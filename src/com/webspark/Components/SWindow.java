package com.webspark.Components;

import com.vaadin.ui.Window;

public class SWindow extends Window{

	public SWindow() {
		// TODO Auto-generated constructor stub
	}
	
	public SWindow(String caption) {
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}
	public SWindow(String caption, int width, int height) {
		setCaption(caption);
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
		// TODO Auto-generated constructor stub
	}

}

