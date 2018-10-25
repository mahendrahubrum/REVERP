package com.webspark.Components;

import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.ui.OptionGroup;

/**
 * @Author Jinshad P.T.
 */

public class SOptionGroup extends OptionGroup{

	public SOptionGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SOptionGroup(String caption, int width, List options, String listKey, String listValue,
			boolean MultiSelection) {
		super();
		
		setCaption(caption);
		setMultiSelect(MultiSelection);
		setWidth(width+"px");
		setImmediate(true);
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

	public SOptionGroup(String caption, Collection<?> options) {
		super(caption, options);
		// TODO Auto-generated constructor stub
	}

	public SOptionGroup(String caption, Container dataSource) {
		super(caption, dataSource);
		// TODO Auto-generated constructor stub
	}

	public SOptionGroup(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}
	

}
