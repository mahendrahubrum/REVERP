/**
 * 
 */
package com.webspark.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.webspark.Components.SHorizontalSplitPanel;

/**
 * @author User
 * 
 */
@Theme("testappstheme")
public class MasterWindow extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4130869970860956852L;

	SHorizontalSplitPanel splitPanel = null;

	Label horLabel = new Label();

	Label verLabel = new Label();

	TextField text1 = null;

	TextField text2 = null;

	TextField text3 = null;

	TextField text4 = null;

	HorizontalLayout horLayout = null;

	VerticalLayout verLayout = null;

	GridLayout gridLayout = null;

	MenuBar menuBar = null;

	Window window = new Window("Hi Window");

	@Override
	protected void init(VaadinRequest request) {

		initBase();

	}

	private void initBase() {

		splitPanel = new SHorizontalSplitPanel();

		horLayout = new HorizontalLayout();

		verLayout = new VerticalLayout();

		gridLayout = new GridLayout(2, 2);
		gridLayout.setSizeFull();

		gridLayout.setMargin(true);

		text1 = new TextField();
		text1.setSizeFull();

		text2 = new TextField();

		text3 = new TextField();

		text4 = new TextField();

		menuBar = new MenuBar();

		final FormLayout content = new FormLayout();
		window.setContent(content);

		horLabel = new Label("Horizontal Label");

		horLabel.setStyleName("two");

		verLabel = new Label("Vertical Label");

		gridLayout.addComponent(text1);

		gridLayout.addComponent(text2);

		gridLayout.addComponent(text3);

		gridLayout.addComponent(text4);

		/*
		 * horLayout.addComponent(horLabel);
		 * 
		 * horLayout.addComponent(splitPanel);
		 * 
		 * horLayout.addComponent(verLabel);
		 */

		setContent(gridLayout);

		UI.getCurrent().addWindow(window);

		Notification.show("Hi Notification",
				Notification.Type.TRAY_NOTIFICATION);

	}

}
