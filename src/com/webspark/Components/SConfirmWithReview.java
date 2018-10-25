package com.webspark.Components;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 10, 2014
 */
public class SConfirmWithReview extends SWindow {
	
	private static final long serialVersionUID = -220358265972627560L;
	SFormLayout layout;
	STextField title;
	STextArea comments;
//	SComboField userSelect;
	SButton okButton, cancelButton;

	public SConfirmWithReview(String caption, long office_id) {
		super();
		
		try {
			
			setCaption(caption);
			layout = new SFormLayout();
			layout.setMargin(true);
			title=new STextField("Title",200);
			comments=new STextArea("Details", 200, 40);
			
//			userSelect=new SComboField("Submit To", 200, new UserManagementDao().getAllLoginsFromOffice(office_id), "id", "login_name");
	
			okButton=new SButton("Ok");
			cancelButton=new SButton("Cancel");
			
			okButton.setId("1");
			cancelButton.setId("0");
			
			layout.addComponent(new SHTMLLabel(null,"<b>Write your review</b>"));
			layout.addComponent(title);
			layout.addComponent(comments);
			layout.addComponent(new SHorizontalLayout(true, okButton,cancelButton));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		setContent(layout);
		
		setClosable(false);
		setResizable(false);
		
		setModal(true);
	}

	public SConfirmWithReview(String caption, int width, int height) {
		super();

		setCaption(caption);
		setWidth(width + "px");
		setHeight(height + "px");
		layout = new SFormLayout();
		setModal(true);
	}
	
	public void addComponent(Component component) {
		layout.addComponent(component);
		setContent(layout);
	}
	
	
	public String getComments() {
		return comments.getValue();
	}
	
	public void setClickListener(ClickListener listener) {
		okButton.addClickListener(listener);
		cancelButton.addClickListener(listener);
	}

	public void addComponent(SPanel panel) {

		setContent(panel);

	}

	public UI getUIParent() {
		return getUI();
	}
	
	public void open() {
		getUI().getCurrent().addWindow(this);
	}
	
	public void close() {
		getUI().removeWindow(this);
	}

	public String getTitle() {
		return title.getValue();
	}
	
	public void setTitle(String value) {
		title.setValue(value);
	}
	
	public void setComments(String value) {
		comments.setValue(value);
	}

}
