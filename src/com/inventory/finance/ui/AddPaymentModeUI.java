package com.inventory.finance.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.finance.dao.PaymentModeDao;
import com.inventory.finance.model.PaymentModeModel;
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
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
public class AddPaymentModeUI extends SparkLogic{

	private SComboField ledgerComboFIeld;
	private SButton createNewButton;
	private SComboField paymentModeCombo;
	private STextField descriptionTextField ;
	private SComboField statusComboField;
	private PaymentModeDao paymentModeDao;
	private LedgerDao ledgerDao;
	private SRadioButton inwardOutwardField;

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
		
		final SHorizontalLayout createNewLayout = new SHorizontalLayout(getPropertyName("payment_mode"));
		createNewLayout.setSpacing(true);	
		
		final SFormLayout formLayout = new SFormLayout();
		formLayout.setSpacing(true);
		
		ledgerDao = new LedgerDao();	
				
		createNewButton= new SButton();
	    createNewButton.setStyleName("createNewBtnStyle");
	    createNewButton.setDescription("--------- "+getPropertyName("create_new")+" --------");
	    
	    List testList=   new ArrayList();
	    final PaymentModeModel sop=new PaymentModeModel();
        sop.setId(0);
        sop.setDescription("--------- "+getPropertyName("create_new")+" --------");
              
        testList.add(0, sop);
        
        paymentModeCombo = new SComboField(null,300, testList,"id", "description");
        paymentModeCombo.setInputPrompt("--------- "+getPropertyName("create_new")+" --------");        
        
        descriptionTextField = new STextField(getPropertyName("description"),300);
        
        ledgerComboFIeld = new SComboField(getPropertyName("ledger"), 300, 
				getAccountList(getOfficeID()),"id","name",true);
		ledgerComboFIeld.setInputPrompt("--------------- "+getPropertyName("select")+" ------------------");
		
		inwardOutwardField = new SRadioButton(getPropertyName("transaction_type"), 300, 
				Arrays.asList(new KeyValue(SConstants.INWARD, getPropertyName("inward")), 
        				new KeyValue(SConstants.OUTWARD, getPropertyName("outward"))),"intKey","value");
		inwardOutwardField.setValue(SConstants.INWARD);
		        
        statusComboField = new SComboField(getPropertyName("status"),300,
        		Arrays.asList(new KeyValue(0, getPropertyName("active")), 
        				new KeyValue(1, getPropertyName("inactive"))), "intKey", "value");
        statusComboField.setInputPrompt(getPropertyName("select"));
        statusComboField.setValue(0);
        
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
		
		final SButton cancelButton = new SButton(getPropertyName("delete"));
		cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		cancelButton.setStyleName("deletebtnStyle");
		cancelButton.setVisible(false);
        
        createNewLayout.addComponent(paymentModeCombo);
        createNewLayout.addComponent(createNewButton);
        
        buttonLayout.addComponent(saveButton);
        buttonLayout.addComponent(updateButton);
        if(getHttpSession().getAttribute("settings") != null){
        	if(((SettingsValuePojo)getHttpSession().getAttribute("settings")).isKEEP_DELETED_DATA()){
        		 buttonLayout.addComponent(cancelButton);
        	} else {
        		 buttonLayout.addComponent(deleteButton);
        	}
        }
       
		
		formLayout.addComponent(createNewLayout);
		formLayout.addComponent(descriptionTextField);
		formLayout.addComponent(ledgerComboFIeld);
		formLayout.addComponent(inwardOutwardField);
		formLayout.addComponent(statusComboField);
		
		mainLayout.addComponent(formLayout);
		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
		
		panel.setContent(mainLayout);
		
		paymentModeDao = new PaymentModeDao();
		loadOptions(0);
		
		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				paymentModeCombo.setValue((long)0);
			}
		});
		paymentModeCombo.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrors();				
				if (paymentModeCombo.getValue() != null && !paymentModeCombo.getValue().toString().equals("0")) {
					try {
						PaymentModeModel model = paymentModeDao
								.getPaymentModeModel(toLong(paymentModeCombo.getValue().toString()));
						descriptionTextField.setValue(model.getDescription());
						ledgerComboFIeld.setValue(model.getLedger().getId());
						inwardOutwardField.setValue(model.getTransactionType());
						statusComboField.setValue(model.getStatus());
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					descriptionTextField.setValue("");
					ledgerComboFIeld.setValue(null);
					inwardOutwardField.setValue((long)SConstants.INWARD);
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
						PaymentModeModel model = new PaymentModeModel();
						model.setDescription(descriptionTextField.getValue());
						model.setOffice(new S_OfficeModel(getOfficeID()));
						model.setStatus(toInt(statusComboField.getValue().toString()));
						model.setLedger(new LedgerModel(toLong(ledgerComboFIeld.getValue().toString())));
						model.setTransactionType(toInt(inwardOutwardField.getValue().toString()));
						
						long id = paymentModeDao.save(model);
						SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
					//	saveActivity(getOptionId(),"Mould Name : "+ model.getMouldName(), id);
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
						if (paymentModeCombo.getValue() != null && !paymentModeCombo.getValue().toString().equals("0")) {
							PaymentModeModel model = paymentModeDao.getPaymentModeModel((Long)paymentModeCombo.getValue());
							model.setDescription(descriptionTextField.getValue());
				//			model.setOffice(new S_OfficeModel(getOfficeID()));
							model.setStatus(toInt(statusComboField.getValue().toString()));
							model.setLedger(new LedgerModel(toLong(ledgerComboFIeld.getValue().toString())));
							model.setTransactionType(toInt(inwardOutwardField.getValue().toString()));
							
							paymentModeDao.update(model);
							SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
						//	saveActivity(getOptionId(),"Mould Name : "+ model.getMouldName(), model.getId());
							loadOptions(model.getId());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					SNotification.show(getPropertyName("error"), Type.WARNING_MESSAGE);
				}
			}
		});
		
cancelButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (isValid()) {
						if (paymentModeCombo.getValue() != null && !paymentModeCombo.getValue().toString().equals("0")) {
							PaymentModeModel model = paymentModeDao.getPaymentModeModel((Long)paymentModeCombo.getValue());
						//	model.setDescription(descriptionTextField.getValue());
				//			model.setOffice(new S_OfficeModel(getOfficeID()));
							model.setStatus((int)1);
					//		model.setLedger(new LedgerModel(toLong(ledgerComboFIeld.getValue().toString())));
						//	model.setTransactionType(toInt(inwardOutwardField.getValue().toString()));
							
							paymentModeDao.update(model);
							SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
						//	saveActivity(getOptionId(),"Mould Name : "+ model.getMouldName(), model.getId());
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
								paymentModeDao.delete(toLong(paymentModeCombo.getValue()+""));
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getAccountList(long officeId) {
		List<LedgerModel> list = new ArrayList<LedgerModel>();
		//	list.add(new LedgerModel(0, "--------------- "+getPropertyName("select")+" ------------------"));
			try {
				list.addAll(ledgerDao.getAllActiveGeneralLedgerOnly(officeId));
			} catch (Exception e) {			
				e.printStackTrace();
			}
			return list;		
	}

	protected void clearErrors() {
		setRequiredError(descriptionTextField, null, false);
		setRequiredError(ledgerComboFIeld, null, false);
		setRequiredError(statusComboField, null, false);		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.add(0, new PaymentModeModel(0, "------ "+getPropertyName("create_new")+" --------"));
			list.addAll(paymentModeDao.getAllPaymentModeList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			paymentModeCombo.setContainerDataSource(bic);
			paymentModeCombo.setItemCaptionPropertyId("description");
			paymentModeCombo.setInputPrompt("------ "+getPropertyName("create_new")+" --------");
			paymentModeCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (descriptionTextField.getValue() == null || descriptionTextField.getValue().equals("")) {
			setRequiredError(descriptionTextField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(descriptionTextField, null, false);
		}
		if (ledgerComboFIeld.getValue() == null || ledgerComboFIeld.getValue().equals("")) {
			setRequiredError(ledgerComboFIeld, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(ledgerComboFIeld, null, false);
		}	
		
		if (statusComboField.getValue() == null || statusComboField.getValue().equals("")) {
			setRequiredError(statusComboField, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(statusComboField, null, false);
		}	
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
