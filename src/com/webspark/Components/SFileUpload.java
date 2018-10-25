package com.webspark.Components;

import com.vaadin.ui.Upload;

public class SFileUpload extends Upload {

	public SFileUpload() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SFileUpload(String caption) {
		super();
		setCaption(caption);
		// TODO Auto-generated constructor stub
	}

	public SFileUpload(String caption, Receiver uploadReceiver) {
		super(caption, uploadReceiver);
		// TODO Auto-generated constructor stub
	}

}
