package com.webspark.Components;

import java.util.Iterator;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class SKeyPad extends GridLayout{
	
	private static final long serialVersionUID = -3844837305953278044L;
	
	STextField keyboardTextField;
	STextField field;

	public SKeyPad() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SKeyPad(int columns, int rows, Component... children) {
		super(columns, rows, children);
		// TODO Auto-generated constructor stub
	}

	public SKeyPad(int columns, int rows) {
		super(columns, rows);
		// TODO Auto-generated constructor stub
	}
	public SKeyPad(String caption, int keyWidth, int keyHeight) {
		
		
		setCaption(caption);
		setStyleName("hotel_keypad_style");
		
		setColumns(5);
		setRows(5);
		setSpacing(true);
		
		keyboardTextField=new STextField();
		keyboardTextField.setWidth("300px");
		keyboardTextField.setHeight("50px");
		keyboardTextField.setStyleName("keypad_text_style");
		addComponent(keyboardTextField,0,0,4,0);
		
		SButton btn=new SButton("1",50,50,"1");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,0,1);
		
		btn=new SButton("2",50,50,"2");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,1,1);
		
		btn=new SButton("3",50,50,"3");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,2,1);
		
//		btn=new SButton("X",keyWidth,keyHeight,"Close");
//		btn.setPrimaryStyleName("keypadRedBtn");
//		addComponent(btn);
		
		btn=new SButton("4",50,50,"4");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,0,2);
		
		btn=new SButton("5",50,50,"5");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,1,2);
		
		btn=new SButton("6",50,50,"6");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,2,2);

		
		btn=new SButton("7",50,50,"7");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,0,3);
		
		btn=new SButton("8",50,50,"8");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,1,3);
		
		btn=new SButton("9",50,50,"9");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,2,3);
		
		
		btn=new SButton(".",50,50,".");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,0,4);
		
		btn=new SButton("0",50,50,"0");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,1,4);
		
		btn=new SButton("00",50,50,"00");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,2,4);
		
		
//		btn=new SButton("⌫",keyHeight,keyHeight,"Del");
		btn=new SButton("⬅",70,50,"Del");
		btn.setPrimaryStyleName("keypadBtn");
		btn.addClickListener(listener);
		addComponent(btn,3,1);
		
		btn=new SButton("Clear",70,50,"Refresh");
		btn.setPrimaryStyleName("keypadRedBtn");
		btn.addClickListener(listener);
		addComponent(btn,3,2);
		
		btn=new SButton("Enter",70,120,"Enter");
		btn.setPrimaryStyleName("keypadGrnBtn");
		btn.setClickShortcut(KeyCode.ENTER);
		btn.addClickListener(listener);
		addComponent(btn,3,3,3,4);
		
	}
	
	public void setValue(String value) {
		keyboardTextField.setValue(value);
	}
	
	public String getValue() {
		return String.valueOf(keyboardTextField.getValue());
	}
	
	
	
	
	
	ClickListener listener=new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			if(event.getButton().getId()!=null){
					
					if(event.getButton().getId().equals("1")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"1");
					}
					else if(event.getButton().getId().equals("2")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"2");
					}
					else if(event.getButton().getId().equals("3")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"3");
					}
					else if(event.getButton().getId().equals("4")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"4");
					}
					else if(event.getButton().getId().equals("5")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"5");
					}
					else if(event.getButton().getId().equals("6")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"6");
					}
					else if(event.getButton().getId().equals("7")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"7");
					}
					else if(event.getButton().getId().equals("8")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"8");
					}
					else if(event.getButton().getId().equals("9")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"9");
					}
					else if(event.getButton().getId().equals("0")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"0");
					}
					else if(event.getButton().getId().equals("00")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"00");
					}
					else if(event.getButton().getId().equals(".")) {
						if(!keyboardTextField.getValue().contains("."))
							keyboardTextField.setValue(keyboardTextField.getValue()+".");
					}
					else if(event.getButton().getId().equals("Refresh")) {
						keyboardTextField.setValue("");
					}
					else if(event.getButton().getId().equals("Del")) {
						if (keyboardTextField.getValue().length()>0) {
							keyboardTextField.setValue(keyboardTextField.getValue().substring(0, keyboardTextField.getValue().length()-1));
						 }
					}
					else if(event.getButton().getId().equals("Enter")) {
						try{
							((SPopupView)getParent()).setPopupVisible(false);
							field.setValue(keyboardTextField.getValue());
						}catch(Exception e){
						}
					}
					
				}
			
			}
	};
	
	
	public void setListener(ClickListener listener) {
		try {
			SButton btn;
			Iterator it=getComponentIterator();
			while (it.hasNext()) {
				btn=(SButton) it.next();
				btn.addClickListener(listener);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void setField(STextField field) {
		this.field=field;
	}
	
	
}
