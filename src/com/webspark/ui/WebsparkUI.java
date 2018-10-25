package com.webspark.ui;

import java.util.Vector;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class WebsparkUI extends SparkLogic {

	private SPanel panel = null;

	// @Override
	/*
	 * protected void init(VaadinRequest request) { final VerticalLayout layout
	 * = new VerticalLayout(); layout.setMargin(true); setContent(layout);
	 * 
	 * Button button = new Button("Click Me");
	 * 
	 * SMenuBar menu = new SMenuBar(); menu.addItem("Hi", null);
	 * 
	 * button.addClickListener(new Button.ClickListener() { public void
	 * buttonClick(ClickEvent event) { layout.addComponent(new
	 * Label("Thank you for clicking")); } }); layout.addComponent(button);
	 * layout.addComponent(menu); }
	 */

	public SPanel getGUI() {

		panel = new SPanel();

		Button button = new Button(getPropertyName("click_me"));

		// Panel pnl = new Panel();

		final VerticalLayout layout = new VerticalLayout();

		layout.addComponent(button);

		panel.setContent(button);

		return panel;

	}

	public Boolean isValid() {
		return null;

	}

	public Vector<String> process() {
		return null;

	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public void init(VaadinRequest request) {
	 * 
	 * 
	 * }
	 */
}