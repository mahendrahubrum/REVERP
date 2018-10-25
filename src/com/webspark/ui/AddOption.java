package com.webspark.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.dao.DBOperations;
import com.webspark.dao.OptionGroupDao;
import com.webspark.model.S_OptionGroupModel;
import com.webspark.model.S_OptionModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddOption extends SparkLogic {
	
	long id=0;
	
	SCollectionContainer bic;
	
	final SFormLayout content;
	
	SComboField options;
	final STextField option_name;
	
	SComboField option_group;
	
    final STextField class_name;
    final STextField analyticsClassName;
    final STextField tool_tips;
    final SComboField status;
    final STextArea description;
    SCheckBox isCreateNew;
    
    final STextField priority_order;
    
    final SButton save=new SButton(getPropertyName("Save"));
    final SButton edit=new SButton(getPropertyName("Edit"));
    final SButton delete=new SButton(getPropertyName("Delete"));
    final SButton update=new SButton(getPropertyName("Update"));
    final SButton cancel=new SButton(getPropertyName("Cancel"));
    
    final SHorizontalLayout buttonLayout=new SHorizontalLayout();
    
    DBOperations dbOper=new DBOperations();
    
    SButton createNewButton;
	
	public AddOption() throws Exception {
		
		setCaption("Options");
		
		setWidth("500px");
		setHeight("470px");
        content = new SFormLayout();
        
        createNewButton= new SButton();
    	createNewButton.setStyleName("createNewBtnStyle");
    	createNewButton.setDescription(getPropertyName("create_new"));
        
    	isCreateNew=new SCheckBox(getPropertyName("is_create_new"), false);
        
        List testList=dbOper.getOptions();
        S_OptionModel sop=new S_OptionModel();
        sop.setOption_id(0);
        sop.setOption_name("------------------- Create New -------------------");
        
        if(testList==null)
        	testList=new ArrayList();
        
        testList.add(0, sop);
        
        options=new SComboField(null,300, testList,"option_id", "option_name");
        options.setInputPrompt(getPropertyName("create_new"));
        
        option_name=new STextField(getPropertyName("option_name"),300);
        class_name=new STextField(getPropertyName("class_name"),300);
        analyticsClassName=new STextField(getPropertyName("analytics_class_name"),300);
        tool_tips=new STextField(getPropertyName("tool_tips"),300);
        
        priority_order=new STextField(getPropertyName("priority_order"),300);
        priority_order.setNewValue("0");
        
        
        status=new SComboField(getPropertyName("status"),300, SConstants.optionStatusList, "key", "value");
        status.setInputPrompt(getPropertyName("select"));
        status.setValue((long)1);
        
        testList=new OptionGroupDao().getAllOptionGroups();
        
        option_group=new SComboField(getPropertyName("option_group"),300, testList, "id", "option_group_name");
        option_group.setInputPrompt(getPropertyName("select"));
        
        description=new STextArea(getPropertyName("description"),300,60);
        
        content.setMargin(true);
        content.setWidth("280px");
        content.setHeight("200px");
        
        SHorizontalLayout salLisrLay=new SHorizontalLayout(getPropertyName("option"));
		salLisrLay.addComponent(options);
		salLisrLay.addComponent(createNewButton);
        content.addComponent(salLisrLay);
        content.addComponent(option_name);
        content.addComponent(option_group);
        content.addComponent(class_name);
        content.addComponent(analyticsClassName);
        content.addComponent(tool_tips);
        content.addComponent(status);
        content.addComponent(description);
        content.addComponent(priority_order);
        content.addComponent(isCreateNew);
        
        buttonLayout.addComponent(save);
        buttonLayout.addComponent(edit);
        buttonLayout.addComponent(delete);
        buttonLayout.addComponent(update);
        buttonLayout.addComponent(cancel);
        
        content.addComponent(buttonLayout);
        
        edit.setVisible(false);
        delete.setVisible(false);
        update.setVisible(false);
        cancel.setVisible(false);
        content.setSizeUndefined();
        
        setContent(content);
        
        createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				options.setValue((long)0);
			}
		});
        
        save.addClickListener(new Button.ClickListener(){
        	public void buttonClick(ClickEvent event){
        		try {
        			
	        		if(options.getValue()==null || options.getValue().toString().equals("0")){
	        			
	        			if(isValid()){
	        			
		        			if(option_name.getValue()!=null && !option_name.getValue().equals("")){
			        			S_OptionModel op=new S_OptionModel();
			                    op.setOption_name(option_name.getValue());
			                    op.setClass_name(class_name.getValue());
			                    op.setAnalyticsClassName(analyticsClassName.getValue());
			                    op.setTool_tip(tool_tips.getValue());
			                    op.setCreate(isCreateNew.getValue());
			                    if((Long)status.getValue()==1)
			                    	op.setActive('Y');
			                    else
			                    	op.setActive('N');
			                    
			                    op.setDescription(description.getValue());
			                    op.setGroup(new S_OptionGroupModel((Long)option_group.getValue()));
			                    
			                    op.setPriority_order(toInt(priority_order.getValue()));
			                    
			                    try {
									id=dbOper.saveOption(op);
									loadOptions(id);
									Notification.show(getPropertyName("Success"), getPropertyName("save_success"),Type.WARNING_MESSAGE);
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
		        			}
	        			}
	        		}
	        		
        		} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
        		
        	}
        	
        });
        
        
        options.addValueChangeListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
				
            	try {
            	if (options.getValue() != null && !options.getValue().toString().equals("0")) {

					save.setVisible(false);
					edit.setVisible(true);
					delete.setVisible(true);
					update.setVisible(false);
					cancel.setVisible(false);
					
					System.out.println("Option :"+options.getValue());
					
					S_OptionModel opt= dbOper.getOptionModel(Long.parseLong(options.getValue().toString()));

					setWritableAll();
					option_name.setValue(opt.getOption_name());
					class_name.setValue(opt.getClass_name());
					analyticsClassName.setValue(opt.getAnalyticsClassName());
					tool_tips.setValue(opt.getTool_tip());
					isCreateNew.setValue(opt.isCreate());
					if(opt.getActive()=='Y')
						status.setValue((long)1);
					else
						status.setValue((long)0);
					
					description.setValue(opt.getDescription());
					
					option_group.setValue(opt.getGroup().getId());
					
					priority_order.setValue(asString(opt.getPriority_order()));
					
					setReadOnlyAll();

				} else {
					save.setVisible(true);
					edit.setVisible(false);
					delete.setVisible(false);
					update.setVisible(false);
					cancel.setVisible(false);
					
					setWritableAll();
					option_name.setValue("");
					class_name.setValue("");
					analyticsClassName.setValue("");
					tool_tips.setValue("");
					isCreateNew.setValue(false);
					status.setValue((long)1);
					description.setValue("");
					option_group.setValue(null);
					priority_order.setValue("1");
					
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
        
        
        edit.addClickListener(new Button.ClickListener(){
        	public void buttonClick(ClickEvent event){
        		try {
        			edit.setVisible(false);
        			delete.setVisible(false);
        			update.setVisible(true);
        			cancel.setVisible(true);
        			setWritableAll();
	        		
        		} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
        
        cancel.addClickListener(new Button.ClickListener(){
        	public void buttonClick(ClickEvent event){
        		try {
        			edit.setVisible(false);
        			delete.setVisible(false);
        			update.setVisible(false);
        			cancel.setVisible(false);
        			loadOptions(Long.parseLong(options.getValue().toString()));
	        		
        		} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
        
        
        delete.addClickListener(new Button.ClickListener(){
        	public void buttonClick(ClickEvent event){
        		try {
        			
        			
        			ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
        			        new ConfirmDialog.Listener() {
        			            public void onClose(ConfirmDialog dialog) {
        			                if (dialog.isConfirmed()) {
        			                	
        			                	try {
        			                		id=Long.parseLong(options.getValue().toString());
											dbOper.delete(id);
											
											 Notification.show(getPropertyName("Success"), getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											
											loadOptions(0);
											
										} catch (Exception e) {
											// TODO Auto-generated catch block
											Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
        			                	
        			                	
        			                    // Confirmed to continue
        								// DO STUFF
        			                } else {
        			                    // User did not confirm
        								// CANCEL STUFF
        			                }
        			            }
        			        });
        			
        			
	        		
        		} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
        
        
        update.addClickListener(new Button.ClickListener(){
        	public void buttonClick(ClickEvent event){
        		try {
        			System.out.println("Option :"+options.getValue());
	        		if(options.getValue()!=null){
	        			
	        			
	        			if(isValid()){
		        			S_OptionModel op= dbOper.getOptionModel(Long.parseLong(options.getValue().toString()));
		        			
		        			
		        			op.setOption_name(option_name.getValue());
		                    op.setTool_tip(tool_tips.getValue());
		                    op.setClass_name(class_name.getValue());
		                    op.setAnalyticsClassName(analyticsClassName.getValue());
		                    op.setCreate(isCreateNew.getValue());
		                    if((Long)status.getValue()==1)
		                    	op.setActive('Y');
		                    else
		                    	op.setActive('N');
		                    
		                    op.setDescription(description.getValue());
		                    
		                    op.setPriority_order(toInt(priority_order.getValue()));
		                    
		                    op.setGroup(new S_OptionGroupModel((Long)option_group.getValue()));
		                    
		                    try {
								dbOper.Update(op);
								loadOptions(op.getOption_id());
								Notification.show(getPropertyName("Success"), getPropertyName("update_success"),Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
								e.printStackTrace();
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
        
        
        
        
        addShortcutListener(new ShortcutListener("Add New Purchase", ShortcutAction.KeyCode.N, new int[] {
                ShortcutAction.ModifierKey.ALT}) {
	        @Override
	        public void handleAction(Object sender, Object target) {
	        	loadOptions(0);
	        }
	    });
        

        addShortcutListener(new ShortcutListener("Save",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (save.isVisible())
					save.click();
				else
					update.click();
			}
		});
        
	}
	
	
	public void setReadOnlyAll(){
		option_name.setReadOnly(true);
		option_group.setReadOnly(true);
	    class_name.setReadOnly(true);
	    analyticsClassName.setReadOnly(true);
	    tool_tips.setReadOnly(true);
	    status.setReadOnly(true);
	    description.setReadOnly(true);
	    priority_order.setReadOnly(true);
	    
	    option_name.focus();
	}
	
	public void setWritableAll(){
		option_name.setReadOnly(false);
		option_group.setReadOnly(false);
	    class_name.setReadOnly(false);
	    analyticsClassName.setReadOnly(false);
	    tool_tips.setReadOnly(false);
	    status.setReadOnly(false);
	    description.setReadOnly(false);
	    priority_order.setReadOnly(false);
	}
	
	
	public void loadOptions(long id){
		List testList;
		try {
			testList = dbOper.getOptions();
			
			S_OptionModel sop=new S_OptionModel();
	        sop.setOption_id(0);
	        sop.setOption_name("------------------- Create New -------------------");
	        
	        if(testList==null)
	        	testList=new ArrayList();
	        testList.add(0, sop);
		
		    options.setInputPrompt("------------------- Create New -------------------");
		      
		    bic=SCollectionContainer.setList(testList, "option_id");
		    options.setContainerDataSource(bic);
		    options.setItemCaptionPropertyId("option_name");
		
		    options.setValue(id);
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
//      **********************************************************
      
      
	}
	
	
	
	
	
	
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		
		if(option_name.getValue()==null || option_name.getValue().equals("")){
			Notification.show(getPropertyName("invalid_data"), getPropertyName("invalid_data"),
                    Type.ERROR_MESSAGE);
			return false;
		}
		
		if(option_group.getValue()==null || option_group.getValue().equals("")){
			Notification.show(getPropertyName("invalid_selection"), getPropertyName("invalid_selection"),
                    Type.ERROR_MESSAGE);
			return false;
		}
		
		
		if(class_name.getValue()==null || class_name.getValue().equals("")){
			Notification.show(getPropertyName("invalid_data"), getPropertyName("invalid_data"),
                    Type.ERROR_MESSAGE);
			return false;
		}
		
		if(status.getValue()==null || status.getValue().equals("")){
			Notification.show(getPropertyName("invalid_selection"), getPropertyName("invalid_selection"),
                    Type.ERROR_MESSAGE);
			return false;
		}
		
		
		if(priority_order.getValue()==null || priority_order.getValue().equals("")){
			Notification.show(getPropertyName("invalid_data"), getPropertyName("invalid_data"),
                    Type.ERROR_MESSAGE);
			return false;
		}
		else {
			try {
				Integer.parseInt(priority_order.getValue());
			} catch (Exception e) {
				Notification.show(getPropertyName("invalid_data"), getPropertyName("invalid_data"),
	                    Type.ERROR_MESSAGE);
				return false;
				// TODO: handle exception
			}
		}
		
		
		
		return true;
	}


	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
