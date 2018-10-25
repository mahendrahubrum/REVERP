package com.inventory.fixedasset.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddUnitUI;
import com.inventory.fixedasset.dao.FixedAssetDao;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class AddFixedAssetUI extends SparkLogic{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SPanel panel;
	private SFormLayout formLayout;
	//private SHorizontalLayout buttonLayout;
	private SComboField fixedAssetComboField;
	private SButton createNewButton;
	private STextField descriptionTextField;
	private SComboField assetTypeGroupComboField;
	private SNativeSelect depreciationTypeNativeSelectField;
	private STextField percentageTextField;
	private SNativeSelect calculationTypeNativeSelectField;
	private SComboField depreciationAccountComboField;
	private SCheckBox movableCheckBox;
	private SCheckBox individualAccountCheckBox;
	private SComboField fixedAssetAccountComboField;
	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private OfficeDao officeDao;
	private SComboField officeComboField;
	private GroupDao groupDao;
	private LedgerDao ledgerDao;
	private FixedAssetDao fixedAssetDao;
	private SNativeSelect unitNativeSelectComboField;
	private UnitDao unitDao;
	private SButton newUnitButton;
	

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		setSize(600, 500);
		panel = new SPanel();
		formLayout = new SFormLayout();
		
		officeDao = new OfficeDao();
		groupDao = new GroupDao();
		ledgerDao = new LedgerDao();
		fixedAssetDao = new FixedAssetDao();
		unitDao = new UnitDao();
		//objDao = new LedgerDao();
		
		
		panel.setSizeFull();
		formLayout.setMargin(true);
		
		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", true, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());
		
		fixedAssetComboField = new SComboField(null, 300);	
				
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));
		
		descriptionTextField = new STextField(getPropertyName("description"), 300, true);
		
		SHorizontalLayout unitLayout=new SHorizontalLayout(getPropertyName("unit"));
		
		try {
			unitNativeSelectComboField = new SNativeSelect(null, 100,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol",true);
			if(unitNativeSelectComboField.getItemIds().iterator().hasNext())
				unitNativeSelectComboField.setValue(unitNativeSelectComboField.getItemIds().iterator().next());
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		newUnitButton = new SButton();
		newUnitButton.setStyleName("smallAddNewBtnStyle");
		newUnitButton.setDescription(getPropertyName("add_new_unit"));	
		
		unitLayout.addComponent(unitNativeSelectComboField);
		unitLayout.addComponent(newUnitButton);
		
		assetTypeGroupComboField = new SComboField(getPropertyName("asset_type_group"), 300, 
				getAssetTypeGroupList(),"id","name",true);
		assetTypeGroupComboField.setInputPrompt("--------------- "+getPropertyName("select")+" ------------------");
		
		depreciationTypeNativeSelectField = new SNativeSelect(getPropertyName("depreciation_type"),
				150, getDepreciationTypeList(), "intKey", "value",true);
		depreciationTypeNativeSelectField.setValue(1);		
		
		percentageTextField = new STextField(getPropertyName("percentage"), 150,true);
		percentageTextField.setStyleName("textfield_align_right");
		
		calculationTypeNativeSelectField = new SNativeSelect(getPropertyName("calculation_type"),
				150, getCalculationTypeList(), "intKey", "value", true);
		calculationTypeNativeSelectField.setNullSelectionAllowed(false);
		calculationTypeNativeSelectField.setValue(1);
		
		depreciationAccountComboField = new SComboField(getPropertyName("depreciation_account"), 300, 
				getDepreciationAccountList(getOfficeID()),"id","name",true);
		depreciationAccountComboField.setInputPrompt("--------------- "+getPropertyName("select")+" ------------------");
		
		movableCheckBox = new SCheckBox(getPropertyName("is_movable"),true);
		
		individualAccountCheckBox = new SCheckBox(getPropertyName("is_individual_account"),true);
		
		fixedAssetAccountComboField = new SComboField(getPropertyName("account"), 300, 
				getDepreciationAccountList(getOfficeID()),"id","name",true);
		fixedAssetAccountComboField.setVisible(false);
		fixedAssetAccountComboField.setInputPrompt("--------------- "+getPropertyName("select")+" ------------------");
		
		saveButton = new SButton(getPropertyName("save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		saveButton.setClickShortcut(KeyCode.ENTER);

		updateButton = new SButton(getPropertyName("update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		updateButton.setClickShortcut(KeyCode.ENTER);
		updateButton.setVisible(false);
		
		deleteButton = new SButton(getPropertyName("delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		deleteButton.setClickShortcut(KeyCode.DELETE);
		deleteButton.setVisible(false);
		
		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.setSpacing(true);
		
		mainButtonLayout.addComponent(saveButton);
		mainButtonLayout.addComponent(updateButton);
		mainButtonLayout.addComponent(deleteButton);

//		cancelButton = new SButton(getPropertyName("cancel"), 78);
//		cancelButton
//				.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
//		cancelButton.setStyleName("deletebtnStyle");
		
		SHorizontalLayout fixedAssetHorizontalLayout = new SHorizontalLayout(
				getPropertyName("fixed_asset"));
		fixedAssetHorizontalLayout.addComponent(fixedAssetComboField);
		fixedAssetHorizontalLayout.addComponent(createNewButton);

		formLayout.addComponent(officeComboField);
		formLayout.addComponent(fixedAssetHorizontalLayout);
		formLayout.addComponent(descriptionTextField);		
		formLayout.addComponent(assetTypeGroupComboField);
		formLayout.addComponent(unitLayout);
		formLayout.addComponent(depreciationTypeNativeSelectField);
		formLayout.addComponent(percentageTextField);
		formLayout.addComponent(calculationTypeNativeSelectField);
		formLayout.addComponent(depreciationAccountComboField);
		formLayout.addComponent(movableCheckBox);
		formLayout.addComponent(individualAccountCheckBox);
		formLayout.addComponent(fixedAssetAccountComboField);
		formLayout.addComponent(mainButtonLayout);
		
		formLayout.setComponentAlignment(mainButtonLayout, Alignment.BOTTOM_CENTER);
		
		panel.setContent(formLayout);
		
		loadFixedAsset(0);
		final CloseListener unitCloseListener = new CloseListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void windowClose(CloseEvent e) {
				try {
					List unitList=unitDao.getAllActiveUnits(getOrganizationID());
					SCollectionContainer unitbic=SCollectionContainer.setList(unitList, "id");
					unitNativeSelectComboField.setContainerDataSource(unitbic);
					unitNativeSelectComboField.setItemCaptionPropertyId("symbol");
					if(unitNativeSelectComboField.getItemIds().iterator().hasNext())
						unitNativeSelectComboField.setValue(unitNativeSelectComboField.getItemIds().iterator().next());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		};
		
		newUnitButton.addClickListener(new ClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					AddUnitUI unitUi=new AddUnitUI();
					unitUi.setCaption(getPropertyName("add_new_unit"));
					unitUi.center();
					getUI().getCurrent().addWindow(unitUi);
					unitUi.addCloseListener(unitCloseListener);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		assetTypeGroupComboField.addValueChangeListener(new ValueChangeListener() {		
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(assetTypeGroupComboField.getValue() == null){
					percentageTextField.setValue("");
				} else {
					loadLatestValuesFromDB();
				}
				
			}
		});
		
		fixedAssetComboField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(fixedAssetComboField.getValue() == null || 
						toLong(fixedAssetComboField.getValue().toString()) == 0){
					clearFields();
				} else {
					setValuesToField();					
				}				
			}

			private void setValuesToField() {
				try {
					FixedAssetModel model = fixedAssetDao.getFixedAssetModel(toLong(fixedAssetComboField.getValue().toString()));
					descriptionTextField.setValue(model.getName());
					assetTypeGroupComboField.setValue(model.getAssetTypeGroup().getId());
					depreciationTypeNativeSelectField.setValue(model.getDepreciationType());
					percentageTextField.setValue(model.getPercentage()+"");
					calculationTypeNativeSelectField.setValue(model.getCalculationType());
					depreciationAccountComboField.setValue(model.getDepreciationAccount().getId());					
					movableCheckBox.setValue(model.isMovable());
					individualAccountCheckBox.setValue(false);
					individualAccountCheckBox.setVisible(false);		
					fixedAssetAccountComboField.setValue(model.getAccount().getId());
					
					
					updateButton.setVisible(true);
					deleteButton.setVisible(true);
					saveButton.setVisible(false);	
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}	

			private void clearFields() {
				descriptionTextField.setValue("");
				assetTypeGroupComboField.setValue(null);
				percentageTextField.setValue("");
				depreciationAccountComboField.setValue(null);
				movableCheckBox.setValue(true);
				individualAccountCheckBox.setValue(true);				
				individualAccountCheckBox.setVisible(true);
		//		fixedAssetComboField.setValue(null);
				
				updateButton.setVisible(false);
				deleteButton.setVisible(false);
				saveButton.setVisible(true);
				
			//	loadLatestValuesFromDB();
			}
		});
		
		createNewButton.addClickListener(new ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				fixedAssetComboField.setValue((long)0);
			}
		});
		individualAccountCheckBox.addValueChangeListener(new ValueChangeListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(individualAccountCheckBox.getValue()){
					fixedAssetAccountComboField.setVisible(false);
					fixedAssetAccountComboField.setValue(null);
				} else {
					fixedAssetAccountComboField.setVisible(true);
				}
			}
		});
		
		officeComboField.addValueChangeListener(new ValueChangeListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				List<LedgerModel> list = getDepreciationAccountList(toLong(officeComboField.getValue().toString()));
				
			//	list.addAll(loanRequestDao.getAllLoanRequestList(getOfficeID()));
				SCollectionContainer bic = SCollectionContainer.setList(list, "id");
				depreciationAccountComboField.setContainerDataSource(bic);
				depreciationAccountComboField.setItemCaptionPropertyId("name");
				depreciationAccountComboField.setValue((long)0);
				
				fixedAssetAccountComboField.setContainerDataSource(bic);
				fixedAssetAccountComboField.setItemCaptionPropertyId("name");
				fixedAssetAccountComboField.setValue((long)0);
				
			}
		});
		
		saveButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					FixedAssetModel fixedAssetModel = null;
					LedgerModel ledgerModel = null;
					try {
						if(individualAccountCheckBox.getValue()){
							ledgerModel = setValueToLedgerModel(0);
						}
						fixedAssetModel = setValueToFixedAssetModel(0);	
						
						FixedAssetDao fixedAssetDao = new FixedAssetDao();
						long id = 0;
						id = fixedAssetDao.save(fixedAssetModel, ledgerModel);
						Notification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
						loadFixedAssetAccounts();
						loadFixedAsset(id);						
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
						
					} catch (Exception e1) {						
						e1.printStackTrace();
					}
				}
				
			}

			private void loadFixedAssetAccounts() {
				List<LedgerModel> list = null;
				try {
					list = getDepreciationAccountList(toLong(officeComboField.getValue().toString()));
				} catch (Exception e) {			
					e.printStackTrace();
				}
				SCollectionContainer bic = SCollectionContainer.setList(list, "id");
				fixedAssetAccountComboField.setContainerDataSource(bic);
				fixedAssetAccountComboField.setItemCaptionPropertyId("name");	
				fixedAssetAccountComboField.setInputPrompt("------ "+getPropertyName("select")+" ------");					
			}
		});
		updateButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(isValid()){
					FixedAssetModel fixedAssetModel = null;
				//	LedgerModel ledgerModel = null;
					try {
						fixedAssetModel = setValueToFixedAssetModel(toLong(fixedAssetComboField.getValue().toString()));	
						
						FixedAssetDao fixedAssetDao = new FixedAssetDao();
						fixedAssetDao.update(fixedAssetModel);
						Notification.show(getPropertyName("update_success"),
								Type.WARNING_MESSAGE);
						fixedAssetComboField.setValue((long)0);
						loadFixedAsset(fixedAssetModel.getId());
					//	long id = fixed
					//	loadFixedAsset(id);
						
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						saveButton.setVisible(false);
						
					} catch (Exception e1) {						
						e1.printStackTrace();
					}
				}				
			}
		});
		
		deleteButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fixedAssetComboField.getValue() != null ||
						toLong(fixedAssetComboField.getValue().toString()) == 0){
					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											fixedAssetDao.delete(toLong(fixedAssetComboField.getValue().toString()));
											Notification.show(getPropertyName("delete_success"),
													Type.WARNING_MESSAGE);										

											loadFixedAsset(0);
											

										} catch (Exception e) {
											Notification
													.show(getPropertyName("Error"),
															Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});	
				}
				
			}
		});
		
	//	loadLatestValuesFromDB();
		
		return panel;
	}

	private LedgerModel setValueToLedgerModel(long ledger_id) throws Exception {
		LedgerModel objModel = new LedgerModel();
		if(ledger_id != 0){
			objModel.setId(ledger_id);
		}
		objModel.setName(descriptionTextField.getValue());
		objModel.setGroup(new GroupModel(
				(Long) assetTypeGroupComboField.getValue()));
		// objModel.setAddress(new AddressModel(1));
		objModel.setCurrent_balance(0);
		objModel.setStatus(SConstants.statuses.status.get(0).getKey());
		objModel.setParentId(ledgerDao.getParentUnderGroup((Long) assetTypeGroupComboField.getValue()));
		objModel.setOffice(new S_OfficeModel(toLong(officeComboField.getValue().toString())));
		objModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
		return objModel;
	}

	private FixedAssetModel setValueToFixedAssetModel(long id) {
		FixedAssetModel model = new FixedAssetModel();		
		if(id != 0){
			model.setId(id);
		}
		model.setOffice(new S_OfficeModel(toLong(officeComboField.getValue().toString())));
		model.setName(descriptionTextField.getValue().trim());
		model.setAssetTypeGroup(new GroupModel(toLong(assetTypeGroupComboField.getValue().toString())));
		model.setDepreciationType(toInt(depreciationTypeNativeSelectField.getValue().toString()));
		model.setPercentage(toDouble(percentageTextField.getValue().trim()));
		model.setCalculationType(toInt(calculationTypeNativeSelectField.getValue().toString()));
		model.setDepreciationAccount(new LedgerModel(toLong(depreciationAccountComboField.getValue().toString())));
		model.setMovable(movableCheckBox.getValue());
		model.setUnit(new UnitModel(toLong(unitNativeSelectComboField.getValue().toString())));
		if(!individualAccountCheckBox.getValue()){
			model.setAccount(new LedgerModel(toLong(fixedAssetAccountComboField.getValue().toString())));		
		}
		
		return model;		
	}

	@SuppressWarnings("unchecked")
	private List<LedgerModel> getDepreciationAccountList(long officeId) {
		List<LedgerModel> list = new ArrayList<LedgerModel>();
	//	list.add(new LedgerModel(0, "--------------- "+getPropertyName("select")+" ------------------"));
		try {
			list.addAll(ledgerDao.getAllActiveGeneralLedgerOnly(officeId));
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<GroupModel> getAssetTypeGroupList() {
		List<GroupModel> list = new ArrayList<GroupModel>();
	//	list.add(new GroupModel(0, "--------------- "+getPropertyName("select")+" ------------------"));
		try {
			list.addAll(groupDao.getAllActiveGroupsNames(getOrganizationID()));
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	private List getOfficeList() {
		try {
			return officeDao.getAllOfficeNamesUnderOrg(getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<KeyValue> getDepreciationTypeList() {
		return Arrays.asList(new KeyValue(SConstants.FixedAsset.FLAT, getPropertyName("flat")),
				new KeyValue(SConstants.FixedAsset.WRITTEN_DOWN_VALUE, getPropertyName("written_down_value")));
	}
	
	private List<KeyValue> getCalculationTypeList() {
		return Arrays.asList(new KeyValue(SConstants.FixedAsset.MONTHLY, getPropertyName("monthly")),
				new KeyValue(SConstants.FixedAsset.QUARTERLY, getPropertyName("quarterly")),
				new KeyValue(SConstants.FixedAsset.HALF_YEARLY, getPropertyName("half_yearly")),
				new KeyValue(SConstants.FixedAsset.YEARLY, getPropertyName("yearly")));
	}


	private void loadFixedAsset(long id) {
		List<FixedAssetModel> list = new ArrayList<FixedAssetModel>();
		list.add(new FixedAssetModel(0, "------ "+getPropertyName("create_new")+" ------"));
		try {
			list.addAll(fixedAssetDao.getAllFixedAssetList(toLong(officeComboField.getValue().toString())));
		} catch (Exception e) {			
			e.printStackTrace();
		}
		SCollectionContainer bic = SCollectionContainer.setList(list, "id");
		fixedAssetComboField.setContainerDataSource(bic);
		fixedAssetComboField.setItemCaptionPropertyId("name");	
		fixedAssetComboField.setValue(id);		
		fixedAssetComboField.setInputPrompt("------ "+getPropertyName("create_new")+" ------");		
	}

	

	@Override
	public Boolean isValid() {
		boolean valid = true;
		if(descriptionTextField.getValue().trim().length() == 0){
			setRequiredError(descriptionTextField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			if(isAlreadyExist()){
				setRequiredError(descriptionTextField, getPropertyName("invalid_data"), true);
				valid = false;
			} else {
				setRequiredError(descriptionTextField, null, false);
			}			
		}
		
		if(assetTypeGroupComboField.getValue() == null){
			setRequiredError(assetTypeGroupComboField, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(assetTypeGroupComboField, null, false);
		}
		
		try{
			if (toDouble(percentageTextField.getValue().trim())==0) {
				setRequiredError(percentageTextField, getPropertyName("invalid_data"),
						true);
				valid = false;
			} else {
				setRequiredError(percentageTextField, null, false);
			}
		}catch(NumberFormatException e){			
			setRequiredError(percentageTextField, getPropertyName("invalid_data"),
					true);
			valid = false;
		}
		
		if(depreciationAccountComboField.getValue() == null){
			setRequiredError(depreciationAccountComboField, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(depreciationAccountComboField, null, false);
		}
		
		if(!individualAccountCheckBox.getValue()){
			if(fixedAssetAccountComboField.getValue() == null){
				setRequiredError(fixedAssetAccountComboField, getPropertyName("invalid_selection"), true);
				valid = false;
			} else {
				setRequiredError(fixedAssetAccountComboField, null, false);
			}
		}		
		return valid;
	}
	private boolean isAlreadyExist() {
		try{
			if(fixedAssetComboField.getValue() == null ||
					toLong(fixedAssetComboField.getValue().toString()) == 0){
				return fixedAssetDao.isAlreadyExistFixedAsset(toLong(officeComboField.getValue().toString()),
						0, descriptionTextField.getValue().trim());
			} else {
				return fixedAssetDao.isAlreadyExistFixedAsset(toLong(officeComboField.getValue().toString()),
						toLong(fixedAssetComboField.getValue().toString()), descriptionTextField.getValue().trim());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	private void loadLatestValuesFromDB() {
		try {
			FixedAssetModel model = fixedAssetDao.getFixedAssetConfiguration(toLong(officeComboField.getValue().toString()),
					toLong(assetTypeGroupComboField.getValue().toString()));
			if(model != null){
				depreciationTypeNativeSelectField.setValue(model.getDepreciationType());
				percentageTextField.setValue(model.getPercentage()+"");
				calculationTypeNativeSelectField.setValue(model.getCalculationType());
				depreciationAccountComboField.setValue(model.getDepreciationAccount().getId());		
				unitNativeSelectComboField.setValue(model.getUnit().getId());
			} else {
				percentageTextField.setValue("");
				depreciationAccountComboField.setValue(null);		
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
