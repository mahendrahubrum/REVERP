package com.webspark.Components;

import com.vaadin.ui.Notification;

/**
 * @author Jinshad P.T.
 *
 * Jun 6, 2013
 */
public class SNotification extends Notification{
	
	public SNotification(String caption, String description, Type type) {
		super(caption, description, type);
		// TODO Auto-generated constructor stub
	}

	public SNotification(String caption, String description, Type type,
			boolean htmlContentAllowed) {
		super(caption, description, type, htmlContentAllowed);
		// TODO Auto-generated constructor stub
	}

	public SNotification(String caption, String description) {
		super(caption, description);
		// TODO Auto-generated constructor stub
	}

	public SNotification(String caption, Type type) {
		super(caption, type);
		// TODO Auto-generated constructor stub
	}

	public SNotification(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
