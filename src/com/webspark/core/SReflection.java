package com.webspark.core;

import java.io.Serializable;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @Author Jinshad P.T.
 */

public class SReflection implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9097645810374207054L;

	public Object getClassInstance(String class_name) {
		Object obj=null;
		try {
			
			Class cls = Class.forName(class_name);
			obj = cls.newInstance();
	 
		} catch (Exception e) {
			Notification.show("Window Not Found..!", "Window for this option is not found. Please Contact with SparkNova.",
                    Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
		finally{
			return obj;
		}
	}
}
