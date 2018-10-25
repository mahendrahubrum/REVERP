package com.webspark.Components;

import java.util.Iterator;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SessionUtil;
import com.webspark.core.SReflection;
import com.webspark.dao.DBOperations;
import com.webspark.dao.LanguageMappingDao;
import com.webspark.model.ActivityLogModel;
import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_OptionModel;

public class SMultiLink extends SButton {
	WrappedSession session;
	SettingsValuePojo settings;
	public SMultiLink() {
		super();
		setStyleName(BaseTheme.BUTTON_LINK);
		
		addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				openOption(Long.parseLong(event.getButton().getId()));
				
			}
		});
	}

	public SMultiLink(String caption, ClickListener listener) {
		super(caption, listener);
		
		setStyleName(BaseTheme.BUTTON_LINK);
		
		addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println("Hai :"+event.getButton().getId());
				openOption(Long.parseLong(event.getButton().getId()));
			}
		});
	}

	public SMultiLink(String caption, int width, int height) {
		super(caption, width, height);
		
		setStyleName(BaseTheme.BUTTON_LINK);
		
		addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				openOption(Long.parseLong(event.getButton().getId()));
			}
		});
	}

	public SMultiLink(String caption, int width) {
		super(caption, width);
		setStyleName(BaseTheme.BUTTON_LINK);
		
		addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				openOption(Long.parseLong(event.getButton().getId()));
			}
		});
	}

	public SMultiLink(String caption, String description) {
		super(caption, description);
		setStyleName(BaseTheme.BUTTON_LINK);
		
		addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				openOption(Long.parseLong(event.getButton().getId()));
			}
		});
	}

	public SMultiLink(String caption) {
		super(caption);
		setStyleName(BaseTheme.BUTTON_LINK);
		
		
		addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				openOption(Long.parseLong(event.getButton().getId()));
			}
		});
		
	}
	
//	SparkLogic window;
	
	@SuppressWarnings("static-access")
	public void openOption(long option_id) {

		S_OptionModel opt;
		SReflection objSRefl = new SReflection();
		try {
			
			session= new SessionUtil().getHttpSession();
			if(session.getAttribute("settings")!=null)
				settings=(SettingsValuePojo) session.getAttribute("settings");
			opt = new DBOperations().getOptionForOpen(option_id, (Long)session.getAttribute("project_type"));
				
//			if(MainLayout.window!=null) {
//				if(!settings.isKEEP_OTHER_WINDOWS())
//				getUI().removeWindow(MainLayout.window);
////				MainLayout.window.detach();
//			}
			
			if(!settings.isKEEP_OTHER_WINDOWS()) {
				if(getUI()!=null) 
				if(getUI().getWindows()!=null){
					Iterator it= getUI().getWindows().iterator();
					while (it.hasNext()) {
						try {
							getUI().removeWindow((Window) it.next());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
			
			S_LanguageMappingModel optmdl=new LanguageMappingDao().getLanguageMappingModel((long)3, 
																							Long.parseLong(session.getAttribute("language_id")+""), 
																							opt.getOption_id());
			
//			getUI().getCurrent().getWindows().removeAll(getUI().getCurrent().getWindows());
	
			SWindow window = (SparkLogic) objSRefl.getClassInstance(opt.getClass_name());
			if(optmdl!=null)
				window.setCaption(optmdl.getName());
			else
				window.setCaption(opt.getOption_name());
			
	
			window.center();
			window.setCloseShortcut(KeyCode.X, ShortcutAction.ModifierKey.ALT);
			getUI().getCurrent().addWindow(window);
			
			
			session.setAttribute("option_id", opt.getOption_id());
			
			
			
			saveActivity(opt.getOption_id(), "Accesed Option : "+opt.getOption_name(), (Long)session.getAttribute("login_id"), 
					(Long)session.getAttribute("office_id"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public void saveActivity(long optionId, String log, long login_id, long office_id){
		try {
			ActivityLogModel activityLogModel=new ActivityLogModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLog(log);
			activityLogModel.setLogin(login_id);
			activityLogModel.setOffice_id(office_id);
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(0);
			new CommonMethodsDao().saveActivityLog(activityLogModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
}
