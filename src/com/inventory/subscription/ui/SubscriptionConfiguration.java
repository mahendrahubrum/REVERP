package com.inventory.subscription.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.subscription.dao.SubscriptionConfigurationDao;
import com.inventory.subscription.model.SubscriptionConfigurationModel;
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
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

public class SubscriptionConfiguration extends SparkLogic 
{
	private static final long serialVersionUID = -2872550102070164406L;
	SPanel mainPanel;
	SHorizontalLayout typeLayout,nameLayout,accountLayout,statusLayout,buttonLayout;
	SVerticalLayout mainLayout;
	SComboField subscriptionCombo,statusCombo;
	STextField subscriptionField;
	SRadioButton accountRadio;
	SButton save,delete,update,createNew;
	
	SubscriptionConfigurationDao scdao;
	SubscriptionConfigurationModel scmdl;
	@SuppressWarnings({ "serial" })
	@Override
	public SPanel getGUI() {
		try
		{
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(420, 275);
			
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionConfigurationDao();
			scmdl=new SubscriptionConfigurationModel();
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			typeLayout=new SHorizontalLayout();
			typeLayout.setSpacing(true);
			nameLayout=new SHorizontalLayout();
			nameLayout.setSpacing(true);
			accountLayout=new SHorizontalLayout();
			accountLayout.setSpacing(true);
			statusLayout=new SHorizontalLayout();
			statusLayout.setSpacing(true);
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			subscriptionCombo=new SComboField(null,175);
			reloadSubscriptionCombo(0);
			subscriptionCombo.setInputPrompt(getPropertyName("select"));
			subscriptionField=new STextField(null, 200);
			subscriptionField.setRequired(true);
			subscriptionField.setInputPrompt(getPropertyName("rental_type_name"));
			accountRadio = new SRadioButton(null, 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long)2);
			accountRadio.setHorizontal(true);
			statusCombo=new SComboField(null, 200, SConstants.statuses.status, "key", "value", true, getPropertyName("select"));
			createNew=new SButton();
			createNew.setStyleName("createNewBtnStyle");
			createNew.setDescription(getPropertyName("create_new"));
			save=new SButton(getPropertyName("save"));
			delete=new SButton(getPropertyName("delete"));
			delete.setVisible(false);
			update=new SButton(getPropertyName("update"));
			update.setVisible(false);
			
			/*****************************************************************************************************///Adding to Layout
			typeLayout.addComponent(new SLabel(getPropertyName("rental_type"),100));
			typeLayout.addComponent(subscriptionCombo);
			typeLayout.addComponent(createNew);
			nameLayout.addComponent(new SLabel(getPropertyName("rental_type_name"),100));
			nameLayout.addComponent(subscriptionField);
			accountLayout.addComponent(new SLabel(getPropertyName("account_type"),100));
			accountLayout.addComponent(accountRadio);
			statusLayout.addComponent(new SLabel(getPropertyName("status"),100));
			statusLayout.addComponent(statusCombo);
			buttonLayout.addComponent(save);
			buttonLayout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
			buttonLayout.addComponent(update);
			buttonLayout.setComponentAlignment(update, Alignment.MIDDLE_CENTER);
			buttonLayout.addComponent(delete);
			buttonLayout.setComponentAlignment(delete, Alignment.MIDDLE_CENTER);
			
			/*****************************************************************************************************///Adding to Main Layout
			mainLayout.addComponent(typeLayout);
			mainLayout.addComponent(nameLayout);
			mainLayout.addComponent(accountLayout);
			mainLayout.addComponent(statusLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(typeLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(nameLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(accountLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(statusLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			/*****************************************************************************************************///Listeners
			createNew.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try
					{
						subscriptionCombo.setComponentError(null);
						subscriptionField.setComponentError(null);
						accountRadio.setComponentError(null);
						statusCombo.setComponentError(null);
						reloadSubscriptionCombo(0);
						accountRadio.setValue((long)2);
						subscriptionField.setValue("");
						statusCombo.setValue(0);
						save.setVisible(true);
						update.setVisible(false);
						delete.setVisible(false);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			});
	
			subscriptionCombo.addValueChangeListener(new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					SubscriptionConfigurationModel mdl;
					try
					{
						subscriptionCombo.setComponentError(null);
						subscriptionField.setComponentError(null);
						statusCombo.setComponentError(null);
						if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0"))
						{
							save.setVisible(false);
							update.setVisible(true);
							delete.setVisible(true);
							mdl=scdao.getConfigurationModel(toLong(subscriptionCombo.getValue().toString()));
							if(mdl!=null)
							{
								subscriptionField.setValue(mdl.getName());
								accountRadio.setValue(mdl.getAccount_type());
								statusCombo.setValue(mdl.getStatus());
							}
							else
							{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else 
						{
							save.setVisible(true);
							delete.setVisible(false);
							update.setVisible(false);
							subscriptionField.setValue("");
							statusCombo.setValue(null);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});

			save.addClickListener(new ClickListener()
			{
				private static final long serialVersionUID = -3276990013611169142L;
				@Override
				public void buttonClick(ClickEvent event) 
				{
					SubscriptionConfigurationModel mdl;
					subscriptionCombo.setComponentError(null);
					subscriptionField.setComponentError(null);
					statusCombo.setComponentError(null);
					String name;
					long accountType,status;
					try
					{
						if(isValid())
						{
							name=subscriptionField.getValue().toString();
							accountType=toLong(accountRadio.getValue().toString());
							status=toLong(statusCombo.getValue().toString());
							mdl=new SubscriptionConfigurationModel();
							mdl.setName(name);
							mdl.setOfficeId(getOfficeID());
							mdl.setAccount_type(accountType);
							mdl.setStatus(status);
							long id=scdao.save(mdl);
							reloadSubscriptionCombo(id);
							SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			update.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) 
				{
					String name;
					long accountType,status,id;
					SubscriptionConfigurationModel mdl;
					subscriptionCombo.setComponentError(null);
					subscriptionField.setComponentError(null);
					statusCombo.setComponentError(null);
					try
					{
						if(isValid())
						{
							if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0"))
							{
								id=toLong(subscriptionCombo.getValue().toString());
								name=subscriptionField.getValue().toString();
								accountType=toLong(accountRadio.getValue().toString());
								status=toLong(statusCombo.getValue().toString());
								mdl=scdao.getConfigurationModel(id);
								mdl.setName(name);
								mdl.setOfficeId(getOfficeID());
								mdl.setAccount_type(accountType);
								mdl.setStatus(status);
								scdao.update(mdl);
								reloadSubscriptionCombo(id);
								SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							}
							else
							{
								setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
							}
						}	
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show("Error",Type.ERROR_MESSAGE);
					}
				}
			});
			
			delete.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					final SubscriptionConfigurationModel mdl;
					try
					{
						subscriptionCombo.setComponentError(null);
						subscriptionField.setComponentError(null);
						statusCombo.setComponentError(null);
						if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0"))
						{
							mdl=scdao.getConfigurationModel(toLong(subscriptionCombo.getValue().toString()));
							ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											scdao.delete(mdl);
											reloadSubscriptionCombo(0);
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
						else
						{
							setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainPanel;
	}
	
	/*****************************************************************************************************///Methods
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionCombo(long id)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.add(0,new SubscriptionConfigurationModel(0, "-----------Create New-----------"));
			idList.addAll(scdao.getAllSubscriptionTypes(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionCombo.setContainerDataSource(bic);
			subscriptionCombo.setItemCaptionPropertyId("name");
			subscriptionCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
	
		boolean valid=true;
		if(subscriptionField.getValue().toString().equals("") || subscriptionField.getValue()==null){
			valid=false;
			setRequiredError(subscriptionField, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscriptionField, null, false);
		}
		
		if(statusCombo.getValue() == null || statusCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(statusCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(statusCombo, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
