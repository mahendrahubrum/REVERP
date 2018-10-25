package com.webspark.Components;

import java.util.List;

/**
 * @Author Jinshad P.T.
 */

public class SComboField extends SComboBox{

	public SComboField() {
		// TODO Auto-generated constructor stub
		setNullSelectionAllowed(false);
	}
	
	public SComboField(String caption) {
		setCaption(caption);
		setFilteringMode(FILTERINGMODE_CONTAINS);
		setImmediate(true);
		
		setNullSelectionAllowed(false);
		// TODO Auto-generated constructor stub
	}
	
	public SComboField(String caption, int width) {
		setCaption(caption);
		setWidth(width+"px");
		setFilteringMode(FILTERINGMODE_CONTAINS);
		setImmediate(true);
		
		setNullSelectionAllowed(false);
		
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 * We can use this for creating a Autocompleter. And can set list of objects(Pojo) as values. 
	 * Caption means the Label of component and width is the width of component.
     * 
     * @param options
     * 			'options' are the list of Pojos or beans
     * @param listKey
     *            'listKey' is the field name inside the pojo
     * that we need to set as listKey(this value will be the output of component.getValu() )
     * @param listValue
     *           'listValue' also a field name of the pojo that shows the values of the component in UI.
     */
	public SComboField(String caption, int width, List options, String listKey, String listValue) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	    setNullSelectionAllowed(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
	    setFilteringMode(FILTERINGMODE_CONTAINS);
		// TODO Auto-generated constructor stub
	}
	
	public SComboField(String caption, int width, List options, String listKey, String listValue,
			boolean required) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	    setNullSelectionAllowed(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
	    
	    setRequired(required);
	    setFilteringMode(FILTERINGMODE_CONTAINS);
		// TODO Auto-generated constructor stub
	}
	
	public SComboField(String caption, int width, List options, String listKey, String listValue,
			boolean required, String inputPrompt) {
		setCaption(caption);
		setWidth(width+"px");
		setImmediate(true);
	    setNullSelectionAllowed(false);
		
	    if(options!=null){
		    SCollectionContainer bic= SCollectionContainer.setList(options, listKey);
		    setContainerDataSource(bic);
	        setItemCaptionPropertyId(listValue);
	    }
	    
	    setRequired(required);
	    
	    int a=inputPrompt.length()-6;
	    
	    if(inputPrompt!=null){
	    	String hif="";
		    for (int i = 0; i < (width/12)-(2*a); i++) {
		    	hif+="-";
			}
		    
		    setInputPrompt(hif+" "+inputPrompt+" "+hif);
	    }
	    
	    setFilteringMode(FILTERINGMODE_CONTAINS);
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
