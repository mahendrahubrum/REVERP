package com.webspark.Components;

import com.vaadin.server.UserError;

public class SUserError extends UserError{

	public SUserError(String message, ContentMode contentMode,
			ErrorLevel errorLevel) {
		super(message, contentMode, errorLevel);
		// TODO Auto-generated constructor stub
	}

	public SUserError(String textErrorMessage) {
		super(textErrorMessage);
		// TODO Auto-generated constructor stub
	}
	
}
