package com.webspark.Components;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * @date 13/08/2014
 */
public class SConfirmWithCommonds extends SWindow {
	
	SFormLayout layout;
	STextArea comments;
	SComboField userSelect;
	SButton okButton, cancelButton;

	public SConfirmWithCommonds(String caption, long office_id) {
		super();
		
		try {
			
			setCaption(caption);
			layout = new SFormLayout();
			layout.setMargin(true);
			comments=new STextArea("Comments", 200, 40);
			
			userSelect=new SComboField("Submit To", 200, new UserManagementDao().getAllLoginsFromOffice(office_id), "id", "login_name");
	
			okButton=new SButton("Ok");
			cancelButton=new SButton("Cancel");
			
			okButton.setId("1");
			cancelButton.setId("0");
			
			layout.addComponent(new SHTMLLabel(null,"<b>Are you Sure..?</b>"));
			layout.addComponent(comments);
			layout.addComponent(userSelect);
			layout.addComponent(new SHorizontalLayout(true, okButton,cancelButton));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContent(layout);
		
		setClosable(false);
		setResizable(false);
		
		setModal(true);
	}

	public SConfirmWithCommonds(String caption, int width, int height) {
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
	
	public Long getUserID() {
		if(userSelect.getValue()!=null)
			return (Long)userSelect.getValue();
		else
			return (long)0;
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

}
