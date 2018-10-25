package com.hotel.service.ui;

import java.util.Iterator;

import com.hotel.config.dao.TableDao;
import com.hotel.config.model.TableModel;
import com.hotel.service.model.CustomerBookingModel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 23-Sep-2015
 */
public class CustomerInfoUI extends SparkLogic{

	private static final long serialVersionUID = 3223446029888784989L;
	STextField custNameField;
	STextArea contactField;
	STextArea remarksField;
	STextField phoneField;
	STextField adultField;
	STextField childsField;
	STextField totalPersonsField;
	SComboField employeeListCombo;
	SComboField tableComboField;
	
	SButton bookButton;
	SButton closeButton;
	SWindow window;
	
	TableDao dao;
	
	@Override
	public SPanel getGUI() {
		SPanel pan=new SPanel();
		pan.setSizeFull();
		center();
		setSize(450, 500);
		setCaption("Customer Details");
		
		window=this;
		
		dao=new TableDao();
		
		
		try {
			SVerticalLayout mainLay=new SVerticalLayout();
			mainLay.setSpacing(true);
			
			SFormLayout form=new SFormLayout();
			form.setMargin(true);
			
			tableComboField=new SComboField("Table",200,dao.getAllTables(getOfficeID()),"id","tableNo");
			custNameField=new STextField("Customer Name",200);
			phoneField=new STextField("Customer Phone",200);
			contactField=new STextArea("Customer Address",200,50);
			remarksField=new STextArea("Remarks",200,50);
			adultField=new STextField("Adults",200);
			adultField.setValue("0");
			adultField.setImmediate(true);
			childsField=new STextField("Childs",200);
			childsField.setImmediate(true);
			childsField.setValue("0");
			totalPersonsField=new STextField("Total",200);
			
			employeeListCombo = new SComboField(
					getPropertyName("employee"),
					200,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name");
			employeeListCombo
					.setInputPrompt("------------------------- Select --------------------------");
			
			form.addComponent(tableComboField);
			form.addComponent(custNameField);
			form.addComponent(phoneField);
			form.addComponent(contactField);
			form.addComponent(employeeListCombo);
			form.addComponent(adultField);
			form.addComponent(childsField);
			form.addComponent(totalPersonsField);
			form.addComponent(remarksField);
			
			mainLay.addComponent(form);
			pan.setContent(mainLay);
			
			bookButton=new SButton("Book");
			closeButton=new SButton("Close");
			SGridLayout btnLay=new SGridLayout(10,1);
			btnLay.setSizeFull();
//			btnLay.setSpacing(true);
			btnLay.addComponent(bookButton,4,0);
			btnLay.addComponent(closeButton,5,0);
			btnLay.setComponentAlignment(bookButton, Alignment.MIDDLE_CENTER);
			btnLay.setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);
			mainLay.addComponent(btnLay);
			
			adultField.addListener(new Listener() {
				
				@Override
				public void componentEvent(Event event) {
					try {
						totalPersonsField.setValue(toInt(adultField.getValue())+toInt(childsField.getValue())+"");
					} catch (Exception e) {
						totalPersonsField.setValue(totalPersonsField.getValue());
					}
				}
			});
			childsField.addListener(new Listener() {
				
				@Override
				public void componentEvent(Event event) {
					try {
						totalPersonsField.setValue(toInt(adultField.getValue())+toInt(childsField.getValue())+"");
					} catch (Exception e) {
						totalPersonsField.setValue(totalPersonsField.getValue());
					}
				}
			});
			
			bookButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(isValid()){
					try {
						CustomerBookingModel model=new CustomerBookingModel();
						model.setAdults(toInt(adultField.getValue()));
						model.setChilds(toInt(childsField.getValue()));
						model.setCustomer_address(contactField.getValue());
						model.setCustomer_name(custNameField.getValue());
						model.setCustomer_phone(phoneField.getValue());
						model.setEmployee((Long)employeeListCombo.getValue());
						model.setRemarks(remarksField.getValue());
						model.setTableNo(new TableModel((Long)tableComboField.getValue()));
						dao.saveCustomerInfo(model);
					
					closeButton.click();
					if(getUI().getCurrent().getWindows()!=null){
						Iterator it= getUI().getCurrent().getWindows().iterator();
						while (it.hasNext()) {
							try {
								getUI().getCurrent().removeWindow((Window) it.next());
							} catch (Exception e) {
							}
						}
					}
					
					HotelSalesUI hotel=new HotelSalesUI();
					hotel.setFields(model);
					
					getUI().getCurrent().addWindow(hotel);
					} catch (Exception e) {
						e.printStackTrace();
					}
					}
				}
			});
			
			closeButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().removeWindow(window);
				}
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pan;
		
	}

	@Override
	public Boolean isValid() {
		boolean valid=true;
		employeeListCombo.setComponentError(null);
		adultField.setComponentError(null);
		childsField.setComponentError(null);
		if(employeeListCombo.getValue()==null||employeeListCombo.getValue().equals("")){
			setRequiredError(employeeListCombo, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		
		try {
			if(toInt(adultField.getValue().toString())<0){
				setRequiredError(adultField, getPropertyName("invalid_data"), true);
				valid=false;
			}
		} catch (Exception e) {
			setRequiredError(adultField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		try {
			if(toInt(childsField.getValue().toString())<0){
				setRequiredError(childsField, getPropertyName("invalid_data"), true);
				valid=false;
			}
		} catch (Exception e) {
			setRequiredError(childsField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	public void setTable(long tabId){
		try {
			tableComboField.setNewValue(tabId);
			TableModel mdl=dao.getTable(tabId);
			employeeListCombo.setValue(mdl.getEmployee().getId());
			
		} catch (Exception e) {
		}
		
	}

}
