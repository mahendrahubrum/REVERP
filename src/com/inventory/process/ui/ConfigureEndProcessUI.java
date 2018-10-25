package com.inventory.process.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.process.dao.ConfigureEndProcessDao;
import com.inventory.process.model.EndProcessModel;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class ConfigureEndProcessUI extends SparkLogic{
	
	private SComboField processSelectCombo;
	private SComboField officeCombo;
	private STextField classNameField;
	private STextField processNameField;
	private SRadioButton periodTypeRadio;
	
	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;


	SDateField date;
	private SButton createNewButton;
	private SNativeSelect statusSelect;
	
	ConfigureEndProcessDao processDao;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		
		setSize(450, 300);
		
		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		
		try {
			
			formLayout = new SFormLayout();
			formLayout.setSizeFull();
			formLayout.setMargin(true);
			formLayout.setSpacing(true);
			
			processDao=new ConfigureEndProcessDao();
			
			
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));
			
			officeCombo = new SComboField(getPropertyName("office"), 200,
					new OfficeDao().getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			List list=new ArrayList();
			
			list= processDao.getAllProcessModel(getOfficeID());
			EndProcessModel og = new EndProcessModel();
			og.setId(0);
			og.setName(getPropertyName("create_new"));
			if (list == null)
				list = new ArrayList();
			list.add(0, og);
			
			processSelectCombo = new SComboField(null, 250,list, "id","name");
			processSelectCombo.setInputPrompt(getPropertyName("create_new"));
			
			classNameField=new STextField(getPropertyName("class_name"),250);
			processNameField=new STextField(getPropertyName("process_name"),250);
			
			statusSelect = new SNativeSelect(getPropertyName("status"), 250,SConstants.processStatus.status, "key", "value");
			statusSelect.setValue((long)0);
			
			periodTypeRadio = new SRadioButton(getPropertyName("type"),
					250, Arrays.asList(new KeyValue(0, "Day End"), new KeyValue(
							1, "Month End"),new KeyValue(2, "Year End")), "intKey", "value");
			periodTypeRadio.setStyleName("radio_horizontal");
			periodTypeRadio.setValue(0);
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout(getPropertyName("process"));
			salLisrLay.addComponent(processSelectCombo);
			salLisrLay.addComponent(createNewButton);
			salLisrLay.setComponentAlignment(createNewButton, Alignment.BOTTOM_CENTER);

			

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			
			saveButton = new SButton(getPropertyName("Save"));
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setVisible(false);
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setVisible(false);
			
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			
			formLayout.addComponent(salLisrLay);
			//formLayout.addComponent(officeCombo);
			formLayout.addComponent(periodTypeRadio);
			formLayout.addComponent(classNameField);
			formLayout.addComponent(processNameField);
			formLayout.addComponent(statusSelect);
			formLayout.addComponent(buttonLayout);
			formLayout.setComponentAlignment(salLisrLay, Alignment.MIDDLE_CENTER);
			formLayout.setComponentAlignment(periodTypeRadio, Alignment.MIDDLE_CENTER);
			formLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			
			mainPanel.setContent(formLayout);
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					processSelectCombo.setValue((long) 0);
				}
			});
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			saveButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try {
							EndProcessModel mdl=new EndProcessModel();
							mdl.setName(processNameField.getValue());
							mdl.setClass_name(classNameField.getValue());
							//mdl.setOffice(new S_OfficeModel((Long)officeCombo.getValue()));
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							mdl.setStatus((Long) statusSelect.getValue());
							mdl.setType(periodTypeRadio.getValue().toString());
							
							long id = processDao.save(mdl);
							loadOptions(id);
							Notification.show(getPropertyName("Success"), getPropertyName("save_success"),
							        Type.WARNING_MESSAGE);
						} catch (Exception e) {
							Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),
			                        Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}
			});
			
			processSelectCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						classNameField.setValue("");
						processNameField.setValue("");
						statusSelect.setValue(0);
						periodTypeRadio.setValue(0);
						//officeCombo.setValue(getOfficeID());
						
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						if (processSelectCombo.getValue() != null && !processSelectCombo.getValue()
										.toString().equals("0")) {
							EndProcessModel mdl=processDao.getEndProcessModel((Long)processSelectCombo.getValue());
							
							classNameField.setValue(mdl.getClass_name());
							processNameField.setValue(mdl.getName());
							statusSelect.setValue(mdl.getStatus());
							periodTypeRadio.setValue(Integer.parseInt(mdl.getType()));
							//officeCombo.setValue(mdl.getOffice().getId());
							
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			updateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try {
							EndProcessModel mdl=processDao.getEndProcessModel((Long)processSelectCombo.getValue());
							mdl.setName(processNameField.getValue());
							mdl.setClass_name(classNameField.getValue());
							//mdl.setOffice(new S_OfficeModel((Long)officeCombo.getValue()));
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							mdl.setStatus((Long) statusSelect.getValue());
							mdl.setType(periodTypeRadio.getValue().toString());
							
							processDao.update(mdl);
							loadOptions(mdl.getId());
							Notification.show(getPropertyName("Success"), getPropertyName("update_success"),
			                        Type.WARNING_MESSAGE);
						} catch (Exception e) {
							Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),
			                        Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}
			});
			
			deleteButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(processSelectCombo.getValue()!=null){
						try {
							ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
								@Override
								public void onClose(ConfirmDialog dialog) {
									if(dialog.isConfirmed()){
										try {
											processDao.delete((Long) processSelectCombo.getValue());
											 Notification.show(getPropertyName("Success"), getPropertyName("deleted_success"),
								                        Type.WARNING_MESSAGE);
											 loadOptions(0);
										} catch (Exception e) {
											e.printStackTrace();
											Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),
							                        Type.ERROR_MESSAGE);
										}
									}
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});					
			
			
			
			
			
		} catch (ReadOnlyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	return mainPanel;
	}

	@SuppressWarnings("unchecked")
	protected void loadOptions(long id) {
		List testList = new ArrayList();
		try {
			testList.add(0, new EndProcessModel(0, getPropertyName("create_new")));
			testList.addAll(processDao.getAllProcessModel(getOfficeID()));
			CollectionContainer bic = CollectionContainer.fromBeans(testList, "id");
			processSelectCombo.setContainerDataSource(bic);
			processSelectCombo.setItemCaptionPropertyId("name");
			processSelectCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public Boolean isValid() {
		boolean ret=true;
		
		if (statusSelect.getValue() == null || statusSelect.getValue().equals("")) {
			setRequiredError(statusSelect, getPropertyName("invalid_selection"), true);
			statusSelect.focus();
			ret = false;
		} else
			setRequiredError(statusSelect, null, false);
		
		if (classNameField.getValue() == null
				|| classNameField.getValue().equals("")) {
			setRequiredError(classNameField, getPropertyName("invalid_data"), true);
			classNameField.focus();
			ret = false;
		} else
			setRequiredError(classNameField, null, false);
		
		if (processNameField.getValue() == null
				|| processNameField.getValue().equals("")) {
			setRequiredError(processNameField, getPropertyName("invalid_data"), true);
			processNameField.focus();
			ret = false;
		} else
			setRequiredError(processNameField, null, false);
		
		/*if (officeCombo.getValue() == null || officeCombo.getValue().equals("")) {
			setRequiredError(officeCombo, getPropertyName("invalid_selection"), true);
			officeCombo.focus();
			ret = false;
		} else
			setRequiredError(officeCombo, null, false);*/
		
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
}
