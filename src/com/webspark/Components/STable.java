package com.webspark.Components;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

public class STable extends Table{

	public STable() {
		super();
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}

	public STable(String caption, Container dataSource) {
		super(caption, dataSource);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}

	public STable(String caption) {
		super(caption);
		setImmediate(true);
		// TODO Auto-generated constructor stub
	}
	
	public STable(String caption, int width, int height) {
		setCaption(caption);
		setWidth(width+"");
		setHeight(height+"");
		setImmediate(true);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String formatPropertyValue(Object rowId, Object colId,
			Property<?> property) {
		return super.formatPropertyValue(rowId, colId, property);
	}
	
}
