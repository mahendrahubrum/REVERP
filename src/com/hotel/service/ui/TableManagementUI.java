package com.hotel.service.ui;

import java.util.Iterator;
import java.util.List;

import com.hotel.config.dao.TableDao;
import com.hotel.config.model.TableModel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 23-Sep-2015
 */
public class TableManagementUI extends SparkLogic{

	private static final long serialVersionUID = -3398340907257826588L;
	SGridLayout tableLay;
	SGridLayout contentLay;
	SVerticalLayout infoLay;
	SHorizontalLayout btnLay;
	TableDao dao;
	SFormLayout infoDetLay;
	
	SButton reserveButton;
	SButton bookButton;
	SButton cleanButton;
	
	SLabel tabeLabel;
	SLabel customerLabel;
	SLabel statusLabel;
	
	ClickListener tableClickListener;
	long tableId;
	@Override
	public SPanel getGUI() {
		SPanel pan=new SPanel();
		pan.setSizeFull();
		setSize(810, 600);
		tableId=0;
		try {
			dao=new TableDao(); 
					
			tableLay=new SGridLayout();
			tableLay.setColumns(4);
			tableLay.setStyleName("hotel_table_panel_style");
			tableLay.setSpacing(true);
			tableLay.setHeight("100%");
			tableLay.setWidth("550px");
//			tableLay.setSizeFull();
			
			infoLay=new SVerticalLayout();
			infoLay.setMargin(false);
			infoLay.setStyleName("hotel_info_panel_style");
			infoLay.setWidth("250px");
			infoLay.setHeight("100%");
			btnLay=new SHorizontalLayout();
			btnLay.setSpacing(true);
//			btnLay.setStyleName("hotel_button_panel_style");
			infoLay.addComponent(btnLay);
			infoDetLay=new SFormLayout();
			infoLay.addComponent(infoDetLay);
			
			contentLay=new SGridLayout(2,1);
			contentLay.setSizeFull();
			
			contentLay.addComponent(tableLay,0,0);
			contentLay.addComponent(infoLay,1,0);
			
			tabeLabel=new SLabel("Table : ");
			customerLabel=new SLabel("Customer");
			statusLabel=new SLabel("Status : ");
			
			tabeLabel.setStyleName(Reindeer.LABEL_H2);
			customerLabel.setStyleName(Reindeer.LABEL_H2);
			statusLabel.setStyleName(Reindeer.LABEL_H2);
			
			infoDetLay.addComponent(tabeLabel);
			infoDetLay.addComponent(statusLabel);
//			infoDetLay.addComponent(customerLabel);
			
			
			bookButton=new SButton(null,"Dine");
			bookButton.setPrimaryStyleName("bookButtonStyle");
			reserveButton=new SButton(null,"Reserve");
			reserveButton.setPrimaryStyleName("reserveButtonStyle");
			cleanButton=new SButton(null,"Clean");
			cleanButton.setPrimaryStyleName("cleanButtonStyle");
			
			bookButton.setVisible(false);
			reserveButton.setVisible(false);
			cleanButton.setVisible(false);
			
			btnLay.addComponent(bookButton);
			btnLay.addComponent(reserveButton);
			btnLay.addComponent(cleanButton);
			pan.setContent(contentLay);
			
			cleanButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(tableId!=0){
							dao.cleanTable(tableId);
							createTableLay();
							SNotification.show("Cleaning Done",Type.WARNING_MESSAGE);
							bookButton.setVisible(true);
							reserveButton.setVisible(true);
							cleanButton.setVisible(false);
						}else{
							SNotification.show("Select table for cleaning",Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}
			});
			
			reserveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					SNotification.show("This functionality is not available in demo. Please purchase the full version",Type.WARNING_MESSAGE);
				}
			});
			
			bookButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(tableId!=0){
						CustomerInfoUI info=new CustomerInfoUI();
						info.setTable(tableId);
						getUI().addWindow(info);
					}else{
						SNotification.show("Select a table for booking",Type.WARNING_MESSAGE);
					}
				}
			});
			
			tableClickListener=new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					tableId=0;
					tabeLabel.setValue("");
					statusLabel.setValue("");
					try {
					if(event.getButton().getId()!=null){
						tableId=toLong(event.getButton().getId());
						TableModel tblMdl=dao.getTable(tableId);
						if(tblMdl.getStatus()==SConstants.tableStatus.AVAILABLE){
							bookButton.setVisible(true);
							reserveButton.setVisible(true);
							cleanButton.setVisible(false);
							statusLabel.setValue("Available");
						}else if(tblMdl.getStatus()==SConstants.tableStatus.AWAITING_CLEANING){
							bookButton.setVisible(false);
							reserveButton.setVisible(false);
							cleanButton.setVisible(true);
							statusLabel.setValue("Awaiting Cleaning");
						}else if(tblMdl.getStatus()==SConstants.tableStatus.BUSY){
							bookButton.setVisible(false);
							reserveButton.setVisible(false);
							cleanButton.setVisible(false);
							statusLabel.setValue("Busy");
						}else if(tblMdl.getStatus()==SConstants.tableStatus.RESERVED){
							bookButton.setVisible(false);
							reserveButton.setVisible(false);
							cleanButton.setVisible(false);
							statusLabel.setValue("Reserved");
						}
						
						tabeLabel.setValue(tblMdl.getTableNo());
					}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			
			createTableLay();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pan;
	}

	private void createTableLay() {
		SButton btn=null;
		
		try {
			tableLay.removeAllComponents();
			List list=dao.getAllTables(getOfficeID());
			Iterator iterator=list.iterator();
			TableModel tblMdl;
			while (iterator.hasNext()) {
				tblMdl = (TableModel) iterator.next();
				btn=new SButton(tblMdl.getTableNo());
				btn.setId(""+tblMdl.getId());
				btn.setPrimaryStyleName("hotel_table_style");
				btn.addClickListener(tableClickListener);
				tableLay.addComponent(btn);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
