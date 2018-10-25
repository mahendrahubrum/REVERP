package com.webspark.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Label;
import com.webspark.Components.SPanel;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SessionUtil;
import com.webspark.core.SReflection;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class LoginLayoutForWindow extends SVerticalLayout {

	SReflection objSRefl = new SReflection();
	SparkLogic window;

	WrappedSession session = new SessionUtil().getHttpSession();

	public LoginLayoutForWindow() {
		super();

		SPanel mainPanel = new SPanel();

		SPanel menuPanel = new SPanel();

		mainPanel.setContent(new Label("Main Panel"));

		window = (SparkLogic) objSRefl
				.getClassInstance("com.webspark.ui.Login");

		window.center();

		getUI().getCurrent().addWindow(window);
		// addComponent(window.getContent());

		// TODO Auto-generated constructor stub
	}

}
