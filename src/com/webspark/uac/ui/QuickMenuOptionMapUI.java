package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.dao.QuickMenuDao;
import com.webspark.model.QuickMenuModel;
import com.webspark.model.S_OptionModel;
import com.webspark.ui.NewMainLayout;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class QuickMenuOptionMapUI extends SparkLogic {
	
	long id=0;
	
	final SFormLayout content;
	SOptionGroup optionGroup;
	
	SCheckBox checkAll;
    
	final SButton save=new SButton(getPropertyName("Save"));
    
    final SHorizontalLayout buttonLayout=new SHorizontalLayout();
    
    QuickMenuDao lomDao=new QuickMenuDao();
    
    List optionsList=null;
    
    SettingsValuePojo settings;
	WrappedSession session;
	
	public QuickMenuOptionMapUI() throws Exception {
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		setWidth("600px");
		setHeight("600px");
        content = new SFormLayout();
        
        checkAll=new SCheckBox(null, false);
        
        optionsList=lomDao.getUserAllocatedOptions(getLoginID());
        
        optionGroup = new SOptionGroup(getPropertyName("options"), 300, optionsList,"option_id", "option_name", true);
        optionGroup.setImmediate(false);
        
        List<Long> optList=lomDao.getQuickMenuOptions(getLoginID());
		Set<Long> lst = new HashSet<Long>();
		for(Long optId: optList){
			if(isAvail((Long)optId))
				lst.add(optId);
		}
		
		optionGroup.setValue(lst);
        
        content.setMargin(true);
        content.setWidth("280px");
        content.setHeight("200px");
        
        SHorizontalLayout hLay=new SHorizontalLayout();
        hLay.addComponent(new SLabel(getPropertyName("check_all")));
        hLay.addComponent(checkAll);
        hLay.setSpacing(true);
        content.addComponent(hLay);
        save.setClickShortcut(KeyCode.ENTER);
        buttonLayout.addComponent(save);
        
        content.addComponent(optionGroup);
        content.addComponent(buttonLayout);

        
        content.setSizeUndefined();
        
        setContent(content);
        
        checkAll.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
        		try {
        			
        			if(checkAll.getValue()) {
        				Set lst = new HashSet();
	            		for(Object optId: optionGroup.getItemIds()){
	            			lst.add(optId);
	            		}
	            		optionGroup.setValue(lst);
        			}
        			else {
        				optionGroup.setValue(null);
        			}
        			
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
				}
        	}
        });
        
        
        save.addClickListener(new Button.ClickListener(){
        	public void buttonClick(ClickEvent event){
        		try {
        			
        			Set<Long> options_selected=(Set<Long>) optionGroup.getValue();
        			
        			List<QuickMenuModel> usrOptList=new ArrayList<QuickMenuModel>();
        			QuickMenuModel usr;
        			for (Long option_id: options_selected) {
        				usr=new QuickMenuModel();
        				
        				usr.setLogin_id(getLoginID());
        				usr.setOption_id(new S_OptionModel(option_id));
        				usrOptList.add(usr);
					}
        			
        			lomDao.updateQuickMenuToUser(getLoginID(), usrOptList);
	        			
	        		Notification.show(getPropertyName("Success"), getPropertyName("save_success"),Type.WARNING_MESSAGE);
	        		
	        		List<Long> optList=lomDao.getQuickMenuOptions(getLoginID());
        			Set<Long> lst = new HashSet<Long>();
        			for(Long optId: optList){
        				if(isAvail((Long)optId))
        					lst.add(optId);
        			}
        			
        			if(settings.getTHEME()==SConstants.REVERP_THEME)
        				((NewMainLayout)getUI().getCurrent().getContent()).createQuickMenu();
	        		
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
				}
        	}
        	
        });
        
        
	}
	
	S_OptionModel optObj;
	public boolean isAvail(long opt_id) {
		for (int i=0; i<optionsList.size();i++) {
			optObj=(S_OptionModel) optionsList.get(i);
			if(optObj.getOption_id()==opt_id)
				return true;
		}
		return false;
	}
	

	
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
			
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
