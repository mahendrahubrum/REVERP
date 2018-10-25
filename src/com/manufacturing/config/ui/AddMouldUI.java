package com.manufacturing.config.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.manufacturing.config.dao.MouldDao;
import com.manufacturing.config.model.MouldModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class AddMouldUI extends SparkLogic{

	private SOfficeComboField officeComboFIeld;
	private SButton createNewButton;
	private SComboField mouldCombo;
	private STextField mouldNameTextField ;
	private SComboField statusComboField;
	private STextArea descriptionTextArea;
	private MouldDao mouldDao;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public SPanel getGUI() {
		setSize(500, 400);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		final SVerticalLayout mainLayout = new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		
		final SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		
		final SHorizontalLayout createNewLayout = new SHorizontalLayout(getPropertyName("mould"));
		createNewLayout.setSpacing(true);	
		
		final SFormLayout formLayout = new SFormLayout();
		formLayout.setSpacing(true);
		
		officeComboFIeld = new SOfficeComboField(getPropertyName("office"), 150);		
		
		createNewButton= new SButton();
	    createNewButton.setStyleName("createNewBtnStyle");
	    createNewButton.setDescription("--------- "+getPropertyName("create_new")+" --------");
	    
	    List testList=   new ArrayList();
	    final MouldModel sop=new MouldModel();
        sop.setId(0);
        sop.setMouldName("--------- "+getPropertyName("create_new")+" --------");
              
        testList.add(0, sop);
        
        mouldCombo = new SComboField(null,300, testList,"id", "mouldName");
        mouldCombo.setInputPrompt("--------- "+getPropertyName("create_new")+" --------");        
        
        mouldNameTextField = new STextField(getPropertyName("mould_name"),300);
        
        statusComboField = new SComboField(getPropertyName("status"),300,
        		Arrays.asList(new KeyValue(0, getPropertyName("active")), 
        				new KeyValue(1, getPropertyName("inactive"))), "intKey", "value");
        statusComboField.setInputPrompt(getPropertyName("select"));
        statusComboField.setValue(0);
        
        descriptionTextArea = new STextArea(getPropertyName("details"),300,60);
        
        final SButton saveButton = new SButton(getPropertyName("save"));
        saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		       
        final SButton deleteButton = new SButton(getPropertyName("delete"));
        deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		deleteButton.setVisible(false);
		
        final SButton updateButton = new SButton(getPropertyName("update"));
        updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		updateButton.setVisible(false);
        
        createNewLayout.addComponent(mouldCombo);
        createNewLayout.addComponent(createNewButton);
        
        buttonLayout.addComponent(saveButton);
        buttonLayout.addComponent(updateButton);
        buttonLayout.addComponent(deleteButton);
		
		formLayout.addComponent(officeComboFIeld);
		formLayout.addComponent(createNewLayout);
		formLayout.addComponent(mouldNameTextField);
		formLayout.addComponent(descriptionTextArea);
		formLayout.addComponent(statusComboField);
		
		mainLayout.addComponent(formLayout);
		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
		
		panel.setContent(mainLayout);
		
		mouldDao = new MouldDao();
		loadOptions(0);
		
		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				mouldCombo.setValue((long)0);
			}
		});
		mouldCombo.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrors();				
				if (mouldCombo.getValue() != null && !mouldCombo.getValue().toString().equals("0")) {
					try {
						MouldModel model = mouldDao
								.getMouldModel(toLong(mouldCombo.getValue().toString()));
						mouldNameTextField.setValue(model.getMouldName());
						descriptionTextArea.setValue(model.getDetails());
						statusComboField.setValue(model.getStatus());
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					mouldNameTextField.setValue("");
					descriptionTextArea.setValue("");
					statusComboField.setValue(0);
					
					updateButton.setVisible(false);
					deleteButton.setVisible(false);
					saveButton.setVisible(true);
				}
			}
		});
		
		saveButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					try{
						MouldModel model = new MouldModel();
						model.setMouldName(mouldNameTextField.getValue());
						model.setOffice(new S_OfficeModel(toLong(officeComboFIeld.getValue().toString())));
						model.setStatus(toInt(statusComboField.getValue().toString()));
						model.setDetails(descriptionTextArea.getValue());
						
						long id = mouldDao.save(model);
						SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						saveActivity(getOptionId(),"Mould Name : "+ model.getMouldName(), id);
						loadOptions(id);
						
					} catch(Exception e) { 
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.WARNING_MESSAGE);
					}
				}
			}
		});
		
		updateButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (isValid()) {
						if (mouldCombo.getValue() != null && !mouldCombo.getValue().toString().equals("0")) {
							MouldModel model = mouldDao.getMouldModel((Long)mouldCombo.getValue());
							model.setMouldName(mouldNameTextField.getValue());
							model.setOffice(new S_OfficeModel(toLong(officeComboFIeld.getValue().toString())));
							model.setStatus(toInt(statusComboField.getValue().toString()));
							model.setDetails(descriptionTextArea.getValue());
							
							mouldDao.update(model);
							SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							saveActivity(getOptionId(),"Mould Name : "+ model.getMouldName(), model.getId());
							loadOptions(model.getId());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					SNotification.show(getPropertyName("error"), Type.WARNING_MESSAGE);
				}
			}
		});
		
		
		deleteButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(),getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {

							try {
								mouldDao.delete(toLong(mouldCombo.getValue()+""));
								SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);										
								loadOptions(0);

							} catch (Exception e) {
								SNotification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}
				});	
			}
		});
		
		
		addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {

				if (saveButton.isVisible())
					saveButton.click();
				else
					updateButton.click();
			}
		});

		
		return panel;
	}

	protected void clearErrors() {
		setRequiredError(mouldNameTextField, null, false);
		setRequiredError(descriptionTextArea, null, false);
		setRequiredError(statusComboField, null, false);		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.add(0, new MouldModel(0, "------ "+getPropertyName("create_new")+" --------"));
			list.addAll(mouldDao.getAllMouldList(toLong(officeComboFIeld.getValue().toString())));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			mouldCombo.setContainerDataSource(bic);
			mouldCombo.setItemCaptionPropertyId("mouldName");
			mouldCombo.setInputPrompt(getPropertyName("create_new"));
			mouldCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (mouldNameTextField.getValue() == null || mouldNameTextField.getValue().equals("")) {
			setRequiredError(mouldNameTextField, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(mouldNameTextField, null, false);
		}
		if (descriptionTextArea.getValue() == null || descriptionTextArea.getValue().equals("")) {
			setRequiredError(descriptionTextArea, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(descriptionTextArea, null, false);
		}		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
