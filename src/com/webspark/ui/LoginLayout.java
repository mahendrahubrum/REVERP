package com.webspark.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.WrappedSession;
import com.webspark.Components.SPanel;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SessionUtil;
import com.webspark.core.SReflection;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class LoginLayout extends SVerticalLayout {

	SReflection objSRefl = new SReflection();
	SparkLogic window;

	WrappedSession session = new SessionUtil().getHttpSession();

	float screen_width;
	float screen_height;

	public LoginLayout() {
		super();

		screen_width = getUI().getCurrent().getPage().getBrowserWindowWidth();
		screen_height = getUI().getCurrent().getPage().getBrowserWindowHeight();

		SPanel pan = (SPanel) objSRefl
				.getClassInstance("com.webspark.ui.Login");
		setWidth(String.valueOf(screen_width));
		setHeight(String.valueOf(screen_height));
		// window.center();

		pan.setWidth(String.valueOf(screen_width));
		pan.setHeight(String.valueOf(screen_height));

		pan.setStyleName("bg_black");

		// getUI().getCurrent().addWindow(window);
		addComponent(pan);

		setStyleName("bg_black");

		// TODO Auto-generated constructor stub
	}

}
