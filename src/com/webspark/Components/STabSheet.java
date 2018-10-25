package com.webspark.Components;

import com.vaadin.server.Resource;
import com.vaadin.ui.TabSheet;

/**
 * @author Anil K P
 *
 * July 1, 2013
 */
public class STabSheet extends TabSheet{

	public STabSheet() {
		super();
	}
	
	public STabSheet(String caption) {
		super();
		
		setCaption(caption);
		
	}
	
	public STabSheet(String caption, Resource icon) {
		super();
	
		setCaption(caption);
		setIcon(icon);
		
	}
	
	public STabSheet(String caption, float width, float height) {
		super();
		setCaption(caption);
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
	}
	
	
	public void setSize(int width,int height){
		setWidth(String.valueOf(width));
		setHeight(String.valueOf(height));
	}

}
