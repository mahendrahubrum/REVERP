package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.EmployeeDocumentDao;
import com.webspark.uac.model.EmployeeDocumentCategoryModel;

@SuppressWarnings("serial")
public class AddEmployeeDocument extends SparkLogic{

	SPanel mainPanel;
	SFormLayout form;
	SComboField documentCombo;
	STextField nameField;
	STextField alertField;
	SHorizontalLayout buttonLayout,createLayout,alertLayout;
	SButton save,delete,update,createNew;
	
	EmployeeDocumentDao dao;
	
	@Override
	public SPanel getGUI() {
		try{
			mainPanel=new SPanel();
			setSize(420, 220);
			dao=new EmployeeDocumentDao();
			form=new SFormLayout();
			form.setSizeFull();
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			createLayout=new SHorizontalLayout(getPropertyName("document"));
			createLayout.setSpacing(true);
			alertLayout=new SHorizontalLayout(getPropertyName("alert"));
			alertLayout.setSpacing(true);
			documentCombo=new SComboField(null, 175);
			loadDocumentCombo(0);
			nameField=new STextField(getPropertyName("document_name"), 200);
			alertField=new STextField(null, 100);
			createNew=new SButton();
			createNew.setStyleName("createNewBtnStyle");
			createNew.setDescription(getPropertyName("create_new"));
			save=new SButton(getPropertyName("save"));
			save.setStyleName("savebtnStyle");
			save.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			update=new SButton(getPropertyName("update"));
			update.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			update.setStyleName("updatebtnStyle");
			update.setVisible(false);
			delete=new SButton(getPropertyName("delete"));
			delete.setStyleName("deletebtnStyle");
			delete.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			delete.setVisible(false);
			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			createLayout.addComponent(documentCombo);
			createLayout.addComponent(createNew);
			alertLayout.addComponent(alertField);
			alertLayout.addComponent(new SLabel(getPropertyName("days_expiry")));
			form.addComponent(createLayout);
			form.addComponent(nameField);
			form.addComponent(alertLayout);
			form.addComponent(buttonLayout);
			form.setSpacing(true);
			form.setMargin(true);
			mainPanel.setContent(form);
			
			createNew.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						documentCombo.setValue((long)0);
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
						removeAllErrors();
						if(documentCombo.getValue()==null || documentCombo.getValue().toString().equals("0")){
							if(isValid()){
								EmployeeDocumentCategoryModel mdl=new EmployeeDocumentCategoryModel();
								mdl.setName(nameField.getValue());
								mdl.setAlert_before(Integer.parseInt(alertField.getValue()));
								mdl.setOrg_id(getOrganizationID());
								long id=dao.save(mdl);
								loadDocumentCombo(id);
								SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			documentCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						removeAllErrors();
						if(documentCombo.getValue()!=null && !documentCombo.getValue().toString().equals("0")){
							EmployeeDocumentCategoryModel mdl=dao.getEmployeeDocumentModel((Long)documentCombo.getValue());
							if(mdl!=null){
								nameField.setValue(mdl.getName());
								alertField.setValue(mdl.getAlert_before()+"");
								save.setVisible(false);
								update.setVisible(true);
								delete.setVisible(true);
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else{
							resetAll();
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			update.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						removeAllErrors();
						if(documentCombo.getValue()!=null && !documentCombo.getValue().toString().equals("0")){
							EmployeeDocumentCategoryModel mdl=dao.getEmployeeDocumentModel((Long)documentCombo.getValue());
							if(mdl!=null){
								if(isValid()){
									mdl.setName(nameField.getValue());
									mdl.setAlert_before(Integer.parseInt(alertField.getValue()));
									mdl.setOrg_id(getOrganizationID());
									long id=dao.update(mdl);
									loadDocumentCombo(id);
									SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			delete.addClickListener(new ClickListener()	{
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						removeAllErrors();
						if(documentCombo.getValue()!=null && !documentCombo.getValue().toString().equals("0")) {
							final EmployeeDocumentCategoryModel mdl=dao.getEmployeeDocumentModel((Long)documentCombo.getValue());
							ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											dao.delete(mdl);
											loadDocumentCombo(0);
											SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
										} 
										catch (Exception e) {
											Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
		        			        } 
		        			    }
		        			});
						}
						else{
							setRequiredError(documentCombo, getPropertyName("invalid_selection"), true);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return mainPanel;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadDocumentCombo(long id){
		try{
			List list=new ArrayList();
			list.add(0,new EmployeeDocumentCategoryModel(0, "-----------Create New-----------"));
			list.addAll(dao.getAllDocuments(getOrganizationID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			documentCombo.setContainerDataSource(bic);
			documentCombo.setItemCaptionPropertyId("name");
			documentCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		
		if (alertField.getValue() == null || alertField.getValue().equals("")) {
			setRequiredError(alertField, getPropertyName("invalid_data"), true);
		} 
		else {
			try {
				if (toDouble(alertField.getValue().toString()) <= 0) {
					setRequiredError(alertField, getPropertyName("invalid_data"), true);
					valid=false;
				}
				else{
					setRequiredError(alertField, null, false);
				}
			} 
			catch (Exception e) {
				setRequiredError(alertField, getPropertyName("invalid_data"), true);
				valid=false;
			}
		}
		
		if (nameField.getValue() == null || nameField.getValue().equals("")) {
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		else{
			setRequiredError(nameField, null, false);
		}
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public void removeAllErrors(){
		documentCombo.setComponentError(null);
		nameField.setComponentError(null);
		alertField.setComponentError(null);
	}
	
	public void resetAll(){
		documentCombo.setValue((long)0);
		nameField.setValue("");
		alertField.setValue("");
		save.setVisible(true);
		update.setVisible(false);
		delete.setVisible(false);
	}
	
}
