package com.webspark.test;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;

@Push
public class PushAroundUI extends UI
       implements Broadcaster.BroadcastListener {
    
    VerticalLayout messages = new VerticalLayout();
    SFormLayout fm;
    SButton bt, bt2;
    @Override
    protected void init(VaadinRequest request) {
    	fm=new SFormLayout();
    	bt=new SButton("Exec");
    	bt2=new SButton("Ref");
    	fm.addComponent(messages);
    	fm.addComponent(bt);
    	fm.addComponent(bt2);
    	
    	setContent(fm);
    	
    	
    	bt.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
				Broadcaster.broadcast("System Will Restart now..!!");
				
			}
		});
    	
//    	bt2.addClickListener(new ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
    	
    	
    	
        // Register to receive broadcasts
        Broadcaster.register(this);
    }

    // Must also unregister when the UI expires    
    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    @Override
    public void receiveBroadcast(final String message) {
        // Must lock the session to execute logic safely
        access(new Runnable() {
            @Override
            public void run() {
                // Show it somehow
            	Notification.show("SS",
        				message,
        				Type.ERROR_MESSAGE);
            }
        });
    }
}
