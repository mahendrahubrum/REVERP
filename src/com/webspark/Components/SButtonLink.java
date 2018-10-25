package com.webspark.Components;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Jinshad P.T.
 *
 * Jun 6, 2013
 */
public class SButtonLink extends Button{

	public SButtonLink() {
		super();
		setStyleName(BaseTheme.BUTTON_LINK);
		// TODO Auto-generated constructor stub
	}

	public SButtonLink(String caption, ClickListener listener) {
		super(caption, listener);
		setStyleName(BaseTheme.BUTTON_LINK);
		// TODO Auto-generated constructor stub
	}

	public SButtonLink(String caption) {
		super(caption);
		setStyleName(BaseTheme.BUTTON_LINK);
		// TODO Auto-generated constructor stub
	}
	
	public SButtonLink(String caption, boolean isCustomStyle) {
		super(caption);
		if(isCustomStyle)
			setPrimaryStyleName(BaseTheme.BUTTON_LINK);
		else
			setStyleName(BaseTheme.BUTTON_LINK);
		// TODO Auto-generated constructor stub
	}
	
	// Need to add setStyleName("link"); on all constructor
	
	

}
