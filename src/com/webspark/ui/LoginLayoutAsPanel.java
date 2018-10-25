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
public class LoginLayoutAsPanel extends SVerticalLayout {

	SReflection objSRefl = new SReflection();
	SparkLogic window;

	WrappedSession session = new SessionUtil().getHttpSession();

	public LoginLayoutAsPanel() {
		super();

		SPanel mainPanel = new SPanel();

		SPanel menuPanel = new SPanel();

		mainPanel.setContent(new Label("Main Panel"));

		// window = (SparkLogic)
		// objSRefl.getClassInstance("com.webspark.ui.Login");

		SPanel pan = (SPanel) objSRefl
				.getClassInstance("com.webspark.ui.Login");
		pan.setWidth("400px");
		pan.setHeight("400px");
		// window.center();
		//
		// getUI().getCurrent().addWindow(window);
		addComponent(pan);

		// TODO Auto-generated constructor stub
	}

}
