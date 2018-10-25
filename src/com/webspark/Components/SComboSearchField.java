package com.webspark.Components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SearchFieldDao;

/**
 * @author Jinshad P.T.
 *
 * Jun 17, 2013
 */
public class SComboSearchField extends SFormLayout {
	
	
	SGridLayout grid;
	
	SRadioButton likeSelect;
	STextField searchTextBox;
	SButton searchButton;
	SComboField comboField;
	
	List list=new ArrayList();
	
	SearchFieldDao objDao=new SearchFieldDao();
	
	String dataQuery="";
	
	@SuppressWarnings("deprecation")
	public SComboSearchField(String caption, int width, final String modelNameWithPackage, 
					final String listKey, final String listValue, final String condition,
					final String inputPrompt, final Object firstElement, final String[] criterias, final String appendinField) throws Exception {
		super();
		setCaption(caption);
		
		long count=0;
		String countQuery="select count("+listKey+") from "+modelNameWithPackage+" ";
		dataQuery="select new "+modelNameWithPackage+"("+listKey+","+listValue+") from "+modelNameWithPackage+" ";
		
		if(condition!=null && !condition.toString().equals("")){
			countQuery+=" where "+condition;
			dataQuery+=" where "+condition;
		}
		
		count=objDao.getDataSize(countQuery);
		
		
		if(count>SConstants.composearchOptions.MAXIMUM_DATA_SIZE){
		
			grid=new SGridLayout();
			grid.setColumns(1);
			grid.setRows(3);
			
			grid.setWidth(width+"");
			
			likeSelect=new SRadioButton(null, width, SConstants.composearchOptions.likeOption,
					"key", "value");
			
			likeSelect.setValue((long)1);
			
			likeSelect.addStyleName("radio_horizontal");
	
			
			SGridLayout gr=new SGridLayout();
			gr.setColumns(2);
			gr.setRows(1);
			
			
			searchTextBox=new STextField(null, width-30);
			
			if(firstElement!=null)
				list.add(firstElement);
			comboField=new SComboField(null , width, list, listKey, listValue);
			
			comboField.setTextInputAllowed(false);
			
			if(inputPrompt!=null){
		    	String hif="";
			    for (int i = 0; i < width/12; i++) {
			    	hif+="-";
				}
			    
			    comboField.setInputPrompt(hif+" "+inputPrompt+" "+hif);
		    }
			
			searchButton=new SButton();
			searchButton.setWidth("2");
			searchButton.setHeight("2");
			searchButton.setStyleName("scombosearchIcon");
			
			gr.addComponent(searchTextBox);
			gr.addComponent(searchButton);
			
			gr.setSpacing(false);
			grid.setSpacing(false);
			
			grid.addComponent(likeSelect);
			grid.addComponent(gr);
			grid.addComponent(comboField);
			
			setStyleName("remove_leftspace");
			
			removeAllComponents();
			addComponent(grid);
			
			
			
			searchButton.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
	        			if(searchTextBox.getValue().length()<SConstants.composearchOptions.MINIMUM_CHAR_LENGTH){
	        				searchTextBox.setComponentError(new UserError("Minimum character size is "+
	        						SConstants.composearchOptions.MINIMUM_CHAR_LENGTH));
	        				
	        				
	        				list=new ArrayList();
        					
        					if(firstElement!=null)
        						list.add(0, firstElement);
        					
        					SCollectionContainer bic=SCollectionContainer.setList(list, listKey);
        					comboField.setContainerDataSource(bic);
        					comboField.setItemCaptionPropertyId(listValue);
        					
        					if (list.size() == 1) {
								comboField.setValue(comboField.getItemIds()
										.iterator().next());
							}
        					
	        			}
	        			else{
	        				searchTextBox.setComponentError(null);
	        				
	        				if(true){
//	        					"concat("+listKey+", '( ',"+listValue+", ' )')"
	        					if(appendinField!=null)
	        						dataQuery="select new "+modelNameWithPackage+"("+listKey+", concat("+listValue+", ' ( ',"+appendinField+", ' )')"+") from "+modelNameWithPackage+" ";
	        					else
	        						dataQuery="select new "+modelNameWithPackage+"("+listKey+","+listValue+") from "+modelNameWithPackage+" ";
	        					
	        					if(condition!=null && !condition.toString().equals("")){
	        						dataQuery+=" where "+condition+" and ";
	        					}
	        					else{
	        						dataQuery+=" where ";
	        					}
	        					
	        					
	        					if(criterias!=null){
	        						int ct=0;
	        						for (int i = 0; i < criterias.length; i++) {
	        							if((Long)likeSelect.getValue()==1){
	        								if(ct!=0)
	        									dataQuery+=" or ";
			        						dataQuery+=criterias[i]+" like '"+searchTextBox.getValue().trim()+"%' ";
			        					}
			        					else if((Long)likeSelect.getValue()==2){
			        						
			        						if(ct!=0)
	        									dataQuery+=" or ";
			        						
			        						dataQuery+=criterias[i]+" like '%"+searchTextBox.getValue().trim()+"' ";
			        					}
			        					else{
			        						
			        						if(ct!=0)
	        									dataQuery+=" or ";
			        						
			        						dataQuery+=criterias[i]+" like '%"+searchTextBox.getValue().trim()+"%' ";
			        					}
	        							
	        							ct++;
									}
	        					}
	        					else{
	        					
		        					if((Long)likeSelect.getValue()==1){
		        						dataQuery+=listValue+" like '"+searchTextBox.getValue().trim()+"%' ";
		        					}
		        					else if((Long)likeSelect.getValue()==2){
		        						dataQuery+=listValue+" like '%"+searchTextBox.getValue().trim()+"' ";
		        					}
		        					else{
		        						dataQuery+=listValue+" like '%"+searchTextBox.getValue().trim()+"%' ";
		        					}
	        					}
	        					
	        					dataQuery+="order by "+listValue;
	        					
	        					
	        					list=new ArrayList();
	        					
	        					if(firstElement!=null)
	        						list.add(0, firstElement);
	        					list.addAll(objDao.getData(dataQuery));
	        					
	        					
	        					
	        					SCollectionContainer bic=SCollectionContainer.setList(list, listKey);
	        					comboField.setContainerDataSource(bic);
	        					comboField.setItemCaptionPropertyId(listValue);
	        					
								if (list.size() == 2) {
									Iterator it = comboField.getItemIds()
											.iterator();
									it.next();
									comboField.setValue(it.next());
								}
								else if (list.size() == 1) {
									comboField.setValue(comboField.getItemIds()
											.iterator().next());
								}
	        				}
	        			}
		        		
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
			
			
			
			likeSelect.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
				
	        		try {
	        			
	        			if(searchTextBox.getValue().length()<SConstants.composearchOptions.MINIMUM_CHAR_LENGTH){
	        				searchTextBox.setComponentError(new UserError("Minimum character size is "+
	        						SConstants.composearchOptions.MINIMUM_CHAR_LENGTH));
	        			}
	        			else{
	        				searchTextBox.setComponentError(null);
	        				
	        				if(true){
	        					
	        					if(appendinField!=null)
	        						dataQuery="select new "+modelNameWithPackage+"("+listKey+", concat("+listValue+", ' ( ',"+appendinField+", ' )')"+") from "+modelNameWithPackage+" ";
	        					else
	        						dataQuery="select new "+modelNameWithPackage+"("+listKey+","+listValue+") from "+modelNameWithPackage+" ";
	        					
	        					
	        					if(condition!=null && !condition.toString().equals("")){
	        						dataQuery+=" where "+condition+" and ";
	        					}
	        					else{
	        						dataQuery+=" where ";
	        					}
	        					
	        					
	        					
	        					if(criterias!=null){
	        						int ct=0;
	        						for (int i = 0; i < criterias.length; i++) {
	        							if((Long)likeSelect.getValue()==1){
	        								if(ct!=0)
	        									dataQuery+=" or ";
			        						dataQuery+=criterias[i]+" like '"+searchTextBox.getValue().trim()+"%' ";
			        					}
			        					else if((Long)likeSelect.getValue()==2){
			        						
			        						if(ct!=0)
	        									dataQuery+=" or ";
			        						
			        						dataQuery+=criterias[i]+" like '%"+searchTextBox.getValue().trim()+"' ";
			        					}
			        					else{
			        						
			        						if(ct!=0)
	        									dataQuery+=" or ";
			        						
			        						dataQuery+=criterias[i]+" like '%"+searchTextBox.getValue().trim()+"%' ";
			        					}
	        							
	        							ct++;
									}
	        					}
	        					else{
	        					
		        					if((Long)likeSelect.getValue()==1){
		        						dataQuery+=listValue+" like '"+searchTextBox.getValue().trim()+"%' ";
		        					}
		        					else if((Long)likeSelect.getValue()==2){
		        						dataQuery+=listValue+" like '%"+searchTextBox.getValue().trim()+"' ";
		        					}
		        					else{
		        						dataQuery+=listValue+" like '%"+searchTextBox.getValue().trim()+"%' ";
		        					}
	        					}
	        					
	        					dataQuery+="order by "+listValue;
	        					
	        					list=new ArrayList();
	        					
	        					if(firstElement!=null)
	        						list.add(0, firstElement);
	        					list.addAll(objDao.getData(dataQuery));
	        					
	        					SCollectionContainer bic=SCollectionContainer.setList(list, listKey);
	        					comboField.setContainerDataSource(bic);
	        					comboField.setItemCaptionPropertyId(listValue);
	        					
								if (list.size() == 2) {
									Iterator it = comboField.getItemIds()
											.iterator();
									it.next();
									comboField.setValue(it.next());
								}
								else if (list.size() == 1) {
									comboField.setValue(comboField.getItemIds()
											.iterator().next());
								}
								
								
								
	        				}
	        			}
		        		
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
			
			
			
		
		}
		else{
			
			if(firstElement!=null)
				list.add(0, firstElement);
			list.addAll(objDao.getData(dataQuery));
			
			comboField=new SComboField(null , width, list, listKey, listValue);
			
			if(inputPrompt!=null){
		    	String hif="";
			    for (int i = 0; i < width/12; i++) {
			    	hif+="-";
				}
			    
			    comboField.setInputPrompt(hif+" "+inputPrompt+" "+hif);
		    }
			
			addComponent(comboField);
			
		}
		
		
		
		
		
		
		
		// TODO Auto-generated constructor stub
	}
	
	
	public SComboField getComboField() {
		return comboField;
	}
	public SButton getSearchButton() {
		return searchButton;
	}
	public STextField getSearchTextField() {
		return searchTextBox;
	}
	public SRadioButton getRadio() {
		return likeSelect;
	}
	
	public void setValue(Object value){
		comboField.setValue(value);
	}
	
	public Object getValue(){
		return comboField.getValue();
	}
	
}
