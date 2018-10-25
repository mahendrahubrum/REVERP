package com.webspark.Components;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;

/**
 * @author Jinshad P.T.
 *
 * Jun 6, 2013
 */
public class SCheckBox extends CheckBox {

	public SCheckBox() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SCheckBox(String caption) {
		super();
		
		setCaption(caption);
		
		// TODO Auto-generated constructor stub
	}

	public SCheckBox(String caption,String id) {
		super();
		
		setCaption(caption);
		setId(id);
	}
	
	public SCheckBox(String caption, int width) {
		super();
		
		setCaption(caption);
		setWidth(width+"px");
		// TODO Auto-generated constructor stub
	}

	public SCheckBox(String caption, boolean initialState) {
		super(caption, initialState);
		// TODO Auto-generated constructor stub
	}

	public SCheckBox(String caption, Property<?> dataSource) {
		super(caption, dataSource);
		// TODO Auto-generated constructor stub
	}
	

}
