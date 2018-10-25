package com.webspark.Components;

import com.vaadin.ui.PopupView;
import com.webspark.dao.DBOperations;

public class SHelpPopupView extends PopupView {

	public SHelpPopupView(String content) {
		super("", new SHTMLLabel(null, content));
		// TODO Auto-generated constructor stub
	}
	
	public SHelpPopupView(long option_id) throws Exception {
		super("", new SHTMLLabel(null, new DBOperations().getOptionModel(option_id).getDescription(), 500, 500));
		// TODO Auto-generated constructor stub
	}

}
