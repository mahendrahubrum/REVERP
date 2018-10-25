package com.webspark.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.VerticalLayout;
import com.webspark.Components.SUI;

@Theme("testappstheme")
public class SMainGUI extends SUI {

	public SMainGUI() {
		// TODO Auto-generated constructor stub
	}

	public void init(VaadinRequest request) {
		// TODO Auto-generated method stub

		System.out.println(" The Application Started Running !");

		/*
		 * SHorizontalLayout horLayout = new SHorizontalLayout(); STextField
		 * text = new STextField(); text.setSizeFull(); //
		 * text.setHeight("20%");
		 * 
		 * SGridLayout grid = new SGridLayout(); grid.setRows(3);
		 * grid.setColumns(1); //grid.setSizeFull();
		 * 
		 * 
		 * SHorizontalSplitPanel splitPanel = new SHorizontalSplitPanel();
		 * splitPanel.addComponent(new STextField()); splitPanel.setSizeFull();
		 * 
		 * SVerticalSplitPanel verPanel = new SVerticalSplitPanel();
		 * verPanel.addComponent(new STextField()); verPanel.setSizeFull();
		 * 
		 * grid.addComponent(text);
		 * 
		 * grid.addComponent(splitPanel);
		 * 
		 * WebsparkUI window = new WebsparkUI(); window.setModal(false); //
		 * window.setSizeFull(); window.setPositionX(300);
		 * window.setPositionY(200);
		 * 
		 * this.setContent(grid);
		 */

		// this.getCurrent().addWindow(window);

		// From layout checking

		VerticalLayout mainFormLayout = new VerticalLayout();
		// mainFormLayout.setSizeFull();

		VerticalLayout menuFormLayout = new VerticalLayout();

		// VerticalLayout
		// menuFormLayout.setSizeFull();

		/*
		 * // Create a panel with a caption. final Panel panel = new
		 * Panel("Contact Information"); panel.addStyleName("panelexample");
		 * 
		 * // The width of a Panel is 100% by default, make it // shrink to fit
		 * the contents. // panel.setWidth(Sizeable.SIZE_UNDEFINED, 0);
		 * 
		 * // Create a layout inside the panel final FormLayout form = new
		 * FormLayout();
		 * 
		 * // Have some margin around it. form.setMargin(true);
		 * 
		 * // Add some components form.addComponent(new TextField("Name"));
		 * form.addComponent(new TextField("Email"));
		 * 
		 * // Set the layout as the root layout of the panel
		 * panel.setContent(form);
		 */

		/*
		 * final Window window = new Window("Window with a Light Panel");
		 * window.setWidth("400px"); window.setHeight("200px"); final
		 * HorizontalSplitPanel splitter = new HorizontalSplitPanel();
		 * window.setContent(splitter);
		 * 
		 * // Create a panel with a caption. final Panel light = new
		 * Panel("Light Panel"); light.setSizeFull();
		 * 
		 * // The "light" style is a predefined style without borders
		 * light.addStyleName(Runo.PANEL_LIGHT);
		 * 
		 * light.addComponent(new Label("The light Panel has no borders."));
		 * light.getLayout().setMargin(true);
		 * 
		 * // The Panel will act as a "caption" of the left panel // in the
		 * SplitPanel. splitter.addComponent(light);
		 * splitter.setSplitPosition(250, Sizeable.UNITS_PIXELS);
		 * 
		 * main.addWindow(window);
		 */

		/*
		 * STextField text = new STextField(); text.setSizeFull();
		 * 
		 * menuFormLayout.addComponent(text);
		 * 
		 * mainFormLayout.addComponent(menuFormLayout);
		 * 
		 * this.setContent(mainFormLayout);
		 */

	}

}
