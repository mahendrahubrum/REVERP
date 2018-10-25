package com.webspark.Components;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.Link;

/**
 * @author Jinshad P.T.
 *
 * Jun 6, 2013
 */
public class SLink extends Link{

	public SLink() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SLink(String caption) {
		super();
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}

	public SLink(String caption, Resource resource, String targetName,
			int width, int height, BorderStyle border) {
		super(caption, resource, targetName, width, height, border);
		// TODO Auto-generated constructor stub
	}

	public SLink(String caption, Resource resource) {
		super(caption, resource);
		// TODO Auto-generated constructor stub
	}

}
