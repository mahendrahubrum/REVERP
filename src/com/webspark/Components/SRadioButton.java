package com.webspark.Components;

import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.ui.OptionGroup;

/**
 * @author Jinshad P.T.
 *
 * Jun 17, 2013
 */
public class SRadioButton extends OptionGroup {

	public SRadioButton() {
		super();
		setMultiSelect(false);
		// TODO Auto-generated constructor stub
	}

	public SRadioButton(String caption, Collection<?> options) {
		super(caption, options);
		setMultiSelect(false);
		// TODO Auto-generated constructor stub
	}

	public SRadioButton(String caption, Container dataSource) {
		super(caption, dataSource);
		setMultiSelect(false);
		// TODO Auto-generated constructor stub
	}

	public SRadioButton(String caption) {
		super(caption);
		setMultiSelect(false);
		// TODO Auto-generated constructor stub
	}
	
	public SRadioButton(String caption, int width, List options, String listKey, String listValue) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	    setNullSelectionAllowed(false);
	    setMultiSelect(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
		// TODO Auto-generated constructor stub
	}
	
	
	public void setHorizontal(boolean enable) {
		if(enable)
			setStyleName("radio_horizontal");
		else
			setStyleName(null);
	}
	
	
}
