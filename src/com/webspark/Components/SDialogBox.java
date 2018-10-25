package com.webspark.Components;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * @author Anil K P
 * @date 27 june,2013
 */
public class SDialogBox extends SWindow {

	SFormLayout layout;

	public SDialogBox() {
		super();
		layout = new SFormLayout();
	}

	public SDialogBox(String caption) {
		super();

		setCaption(caption);
		layout = new SFormLayout();
		setModal(true);

	}

	public SDialogBox(String caption, int width, int height) {
		super();

		setCaption(caption);
		setWidth(width + "px");
		setHeight(height + "px");
		layout = new SFormLayout();
		setModal(true);

	}

	public SDialogBox(String caption, int width, int height, int xposition, int yposition) {
		super();

		setCaption(caption);
		setWidth(width + "px");
		setHeight(height + "px");
		setPositionX(xposition);
		setPositionY(yposition);
		layout = new SFormLayout();
		setModal(true);

	}
	
	public void show() {
		setVisible(true);
	}

	public void hide() {
		setVisible(false);
	}

	public void remove() {
		remove();
	}

	public void dispose() {
		dispose();
	}

	public void addComponent(Component component) {

		layout.addComponent(component);
		setContent(layout);

	}

	public void addComponent(SPanel panel) {

		setContent(panel);

	}

	public UI getUIParent() {
		return getUI();
	}

}
