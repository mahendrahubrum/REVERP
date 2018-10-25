package com.webspark.Components;

import org.vaadin.tokenfield.TokenField;

import com.vaadin.ui.Layout;

public class STokenField extends TokenField {

	public STokenField() {
		super();
		// TODO Auto-generated constructor stub
	}

	public STokenField(Layout lo, InsertPosition insertPosition) {
		super(lo, insertPosition);
		// TODO Auto-generated constructor stub
	}

	public STokenField(Layout lo) {
		super(lo);
		// TODO Auto-generated constructor stub
	}

	public STokenField(String caption, InsertPosition insertPosition) {
		super(caption, insertPosition);
		// TODO Auto-generated constructor stub
	}

	public STokenField(String caption, Layout lo, InsertPosition insertPosition) {
		super(caption, lo, insertPosition);
		// TODO Auto-generated constructor stub
	}

	public STokenField(String caption, Layout lo) {
		super(caption, lo);
		// TODO Auto-generated constructor stub
	}

	public STokenField(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}
	
	public void setNewValue(Object value){
		if(isReadOnly()){
			setReadOnly(false);
			setValue(value);
			setReadOnly(true);
		}
		else{
			setValue(value);
		}
		
	}

}
