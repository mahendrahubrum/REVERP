package com.inventory.subscription.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.subscription.dao.ExpenditureDao;
import com.inventory.subscription.model.SubscriptionExpenditureModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class AddExpenditure extends SparkLogic{

	SPanel mainPanel;
	SFormLayout form;
	SHorizontalLayout buttonLayout,newLayout;
	SComboField expenditureCombo;
	STextField nameField;
	SButton save,update,delete,createNew;
	ExpenditureDao dao;
	@Override
	public SPanel getGUI() {
		try{
			dao=new ExpenditureDao();
			mainPanel=new SPanel();
			setSize(350, 180);
			form=new SFormLayout();
			form.setMargin(true);
			form.setSpacing(true);
			form.setSizeFull();
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			newLayout=new SHorizontalLayout(getPropertyName("item_condition"));
			newLayout.setSpacing(true);
			expenditureCombo=new SComboField(null, 175);
			expenditureCombo.setInputPrompt(getPropertyName("select"));
			loadExpenditures(0);
			createNew=new SButton();
			createNew.setStyleName("createNewBtnStyle");
			createNew.setDescription(getPropertyName("create_new"));
			newLayout.addComponent(expenditureCombo);
			newLayout.addComponent(createNew);
			nameField=new STextField(getPropertyName("name"), 200);
			nameField.setInputPrompt(getPropertyName("item_condition"));
			save=new SButton(getPropertyName("save"));
			update=new SButton(getPropertyName("update"));
			delete=new SButton(getPropertyName("delete"));
			form.addComponent(newLayout);
			form.addComponent(nameField);
			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			update.setVisible(false);
			delete.setVisible(false);
			form.addComponent(buttonLayout);
			form.setComponentAlignment(newLayout, Alignment.MIDDLE_CENTER);
			form.setComponentAlignment(nameField, Alignment.MIDDLE_CENTER);
			form.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(form);

			createNew.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						expenditureCombo.setValue((long)0);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			
			save.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if(expenditureCombo.getValue()==null || expenditureCombo.getValue().toString().equals("0")){
							if(isValid()){
								String name=nameField.getValue().toString();
								SubscriptionExpenditureModel mdl=new SubscriptionExpenditureModel();
								mdl.setName(name);
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setType(SConstants.TRANSPORTATION_EXPENDITUE);
								long id=dao.save(mdl);
								SNotification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);
								loadExpenditures(id);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			expenditureCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						SubscriptionExpenditureModel mdl;
						if(expenditureCombo.getValue()!=null && !expenditureCombo.getValue().toString().equals("0")){
							mdl=dao.getModel(toLong(expenditureCombo.getValue().toString()));
							if(mdl!=null){
								nameField.setValue(mdl.getName());
								save.setVisible(false);
								update.setVisible(true);
								delete.setVisible(true);
							}
						}
						else{
							expenditureCombo.setValue((long)0);
							nameField.setValue("");
							save.setVisible(true);
							update.setVisible(false);
							delete.setVisible(false);
						}
						
					}
					catch(Exception e){
						
					}
				}
			});
			
			update.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						SubscriptionExpenditureModel mdl;
						if(expenditureCombo.getValue()!=null && !expenditureCombo.getValue().toString().equals("0")){
							mdl=dao.getModel(toLong(expenditureCombo.getValue().toString()));
							if(mdl!=null){
								if(isValid()){
									String name=nameField.getValue().toString();
									mdl.setName(name);
									mdl.setType(SConstants.TRANSPORTATION_EXPENDITUE);
									mdl.setOffice(new S_OfficeModel(getOfficeID()));
									long id=dao.update(mdl);
									SNotification.show(getPropertyName("update_success"), Type.WARNING_MESSAGE);
									loadExpenditures(id);
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			delete.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						final SubscriptionExpenditureModel mdl;
						if(expenditureCombo.getValue()!=null && !expenditureCombo.getValue().toString().equals("0")){
							mdl=dao.getModel(toLong(expenditureCombo.getValue().toString()));
							if(mdl!=null){
								ConfirmDialog.show(getUI(), "Are you sure?",new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
				        			        try {
				        			        	dao.delete(mdl);
				        			        	SNotification.show(getPropertyName("delete_success"), Type.WARNING_MESSAGE);
				        						loadExpenditures(0);
											}
				        			        catch (Exception e) {
				        			        	Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
												e.printStackTrace();
				        			        }
										} 
									}
								});
							}
							
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return mainPanel;
	}

	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(nameField.getValue()==null || nameField.getValue().equals("")){
			setRequiredError(nameField, getPropertyName("invalid_data"),true);
			nameField.focus();
			valid=false;
		}
		else
			setRequiredError(nameField, null,false);
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadExpenditures(long id) {
		try{
			List list=new  ArrayList();
			list.add(0,new SubscriptionExpenditureModel(0, "-------Create New-------"));
			list.addAll(dao.getAllExpenditures(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			expenditureCombo.setContainerDataSource(bic);
			expenditureCombo.setItemCaptionPropertyId("name");
			expenditureCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
