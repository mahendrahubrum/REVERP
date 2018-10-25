package com.webspark.ui;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;
import com.webspark.Components.DocLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.core.SReflection;
import com.webspark.test.Broadcaster;

/**
 * @Author Jinshad P.T.
 */
@Title("REVERP")
@Theme("testappstheme")
//@Push()
//public class MainGUI extends UI implements Broadcaster.BroadcastListener {
public class MainGUI extends UI {
	
	SReflection objSRefl=new SReflection();
	SparkLogic window;
	SettingsValuePojo settings;
	
	NewMainLayout mainLay;
	
	public WrappedSession sessionObj;
	DocLayout doc;
	
	public void init(VaadinRequest request){
		sessionObj=request.getWrappedSession();
		if(sessionObj.getAttribute("settings")!=null)
			settings=(SettingsValuePojo) sessionObj.getAttribute("settings");
//		UI.getCurrent().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
		
	if(sessionObj.getAttribute("login_id")!=null && !sessionObj.getAttribute("login_id").equals("")){
		
		if(settings.getTHEME()==2) {
//			mainLay=new NewMainLayout();
//			doc=new DocLayout(mainLay.getDocListener());
//			doc.addComponent(mainLay, "left: 0px; top: 0px; z-index:1; right: 0;");
//			doc.addClickListener(mainLay.getDocListener());
//			setContent(doc);
			setContent(new NewMainLayout());
		}
		else
			setContent(new MainLayout());
		
		setSizeFull();
		setSizeUndefined();
		setWidth("100%");
		setHeight("100%");
		
	}
	else{
		
		
//		removeWindow(window);
//		window = (SparkLogic) objSRefl.getClassInstance("com.webspark.ui.Login");
////		window.setClosable(false);
//		openSWindow(320, 170);
		setContent(new LoginLayout());
		setSizeFull();
		setSizeUndefined();
		setWidth("100%");
		setHeight("100%");
		
		
//		If use UI.getCurrent().getPushConfiguration().setPushMode(PushMode.DISABLED) then wecan use the real session
//		otherwise we need to use only one session
		
//		UI.getCurrent().getPushConfiguration().setPushMode(PushMode.DISABLED);
		
	}
	
	
//	bt.addClickListener(new ClickListener() {
//		
//		@Override
//		public void buttonClick(ClickEvent event) {
//			// TODO Auto-generated method stub
//			
//			
//			
//		}
//	});
	
	
//	Broadcaster.register(this);
	
		
	}
	
	

	public void openSWindow(int width, int height){
//		window.setWidth(width+"px");
//		window.setHeight(height+"px");
		window.center();
		
		getCurrent().addWindow(window);
	}


	
	public void sendBroadCast(String message) {
		// TODO Auto-generated method stub
		Broadcaster.broadcast(message);
	}
	
	
	
//	 	@Override
//	    public void detach() {
//	        Broadcaster.unregister(this);
//	        super.detach();
//	    }
//
//	    @Override
//	    public void receiveBroadcast(final String message) {
//	        access(new Runnable() {
//	            @Override
//	            public void run() {
//	                // Show it somehow
//	            	SNotification.show(message,
//	        				"",
//	        				Type.ERROR_MESSAGE);
//	            }
//	        });
//	    }
	    
	    public WrappedSession getSparkSession() {
			// TODO Auto-generated method stub
			return sessionObj;
		}
}

