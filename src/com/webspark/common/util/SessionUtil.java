package com.webspark.common.util;

import com.vaadin.server.WrappedSession;
import com.webspark.ui.MainGUI;

public class SessionUtil extends MainGUI {
	
	WrappedSession sessionObject;
	public WrappedSession getHttpSession(){
		try {
//			if(sessionObject==null) {
				MainGUI obj= (MainGUI) getUI().getCurrent();
				sessionObject=obj.sessionObj;
//			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sessionObject;
	}
	
}
