package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.model.GroupModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.STextField;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OrganizationModel;

public class LedgerGroupPanel extends SContainerPanel {

	private static final long serialVersionUID = -6832577644748381211L;

	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	SCollectionContainer bic;

	SComboField groupListCombo;
	STextField groupNameTextField;
	SComboField statusCombo;
	SComboField actClassCombo;
	SComboField parentListCombo;

	SButton save;
	SButton delete;
	SButton update;

	GroupDao objDao;

	SButton createNewButton;

//	private SCheckBox showAllBox;

	@SuppressWarnings("serial")
	public LedgerGroupPanel() {

		setId("LedgerGroup");
		setSize(500, 320);
		objDao = new GroupDao();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));

			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			save = new SButton(getPropertyName("Save"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			List list = objDao.getAllGroupsNames(getOrganizationID());
			GroupModel og = new GroupModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			groupListCombo = new SComboField(null, 300, list, "id", "name");
			groupListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					SConstants.statuses.status,	"key", "value");
			statusCombo
					.setInputPrompt(getPropertyName("select"));
			statusCombo.setValue((long)1);

			parentListCombo = new SComboField(getPropertyName("parent_group"), 300);
//			showAllBox = new SCheckBox(getPropertyName("show_all_groups"));
//			showAllBox.setImmediate(true);
//			showLayout.addComponent(showAllBox);

			loadParentGroups(0);

			groupNameTextField = new STextField(getPropertyName("group_name"),
					300);

			actClassCombo=new SComboField("Class", 300,
			 SConstants.actClassList, "key","value", false, "Select");

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("group"));
			salLisrLay.addComponent(groupListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(groupNameTextField);
			form.addComponent(parentListCombo);
			 form.addComponent(actClassCombo);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			addShortcutListener(new ShortcutListener("Add New",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
					loadParentGroups(0);
				}
			});

			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (save.isVisible())
						save.click();
					else
						update.click();
				}
			});
//			showAllBox.addValueChangeListener(new ValueChangeListener() {
//
//				@Override
//				public void valueChange(ValueChangeEvent event) {
//					loadParentGroups();
//				}
//			});
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					groupListCombo.setValue((long) 0);
				}
			});
			
			parentListCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(parentListCombo.getValue()!=null&&!parentListCombo.getValue().toString().equals("0")){
						try {
							long classId=objDao.getClassIdOfGroup((Long)parentListCombo.getValue());
							actClassCombo.setNewValue(classId);
							actClassCombo.setReadOnly(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						actClassCombo.setReadOnly(false);
						actClassCombo.setNewValue(null);
					}
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
							if (isValid() && !isAlreadyExist(false,"")) {
								GroupModel objModel = new GroupModel();
								objModel.setName(groupNameTextField.getValue());
								objModel.setOrganization(new S_OrganizationModel(
										getOrganizationID()));

								int level = 0;
									
								if((Long)parentListCombo.getValue()!=0){
									level=objDao.getLevel((Long)parentListCombo.getValue())+1;
								}
									
//								objModel.setAccount_class_id(parent
//										.getAccount_class_id());
								objModel.setStatus((Long) statusCombo
										.getValue());
								objModel.setLevel(level);
								objModel.setParent_id((Long) parentListCombo
										.getValue());
								objModel.setAccount_class_id((Long) actClassCombo.getValue());
								objModel.setLedgerParentId(1);

								try {
									objDao.save(objModel);
									loadParentGroups(objModel.getParent_id());
									loadOptions(objModel.getId());
									Notification.show(
											getPropertyName("Success"),
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			groupListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							setRequiredError(groupNameTextField, null, false);
							try {
								if (groupListCombo.getValue() != null && !groupListCombo.getValue().toString().equals("0")) {

									save.setVisible(false);
									update.setVisible(true);
									delete.setVisible(true);

									GroupModel objModel = objDao.getGroup((Long) groupListCombo.getValue());

									groupNameTextField.setValue(objModel.getName());
									statusCombo.setValue(objModel.getStatus());
									parentListCombo.setValue(objModel.getParent_id());
									actClassCombo.setNewValue(objModel.getAccount_class_id());

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									groupNameTextField.setValue("");
									statusCombo.setValue((long)1);
									actClassCombo.setNewValue(null);
									parentListCombo.setValue((long)0);
								}

								removeErrorMsg();

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});


			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												objDao.delete((Long) groupListCombo
														.getValue());

												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);
												loadParentGroups(0);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (groupListCombo.getValue() != null) {

							GroupModel objModel = objDao
									.getGroup((Long) groupListCombo
											.getValue());
							
							if (isValid() && !isAlreadyExist(true,objModel.getName())) {


								int level = 0;
								if((Long)parentListCombo.getValue()!=0){
									level=objDao.getLevel((Long)parentListCombo.getValue())+1;
								}
								
								 objModel.setAccount_class_id((Long)
										 actClassCombo.getValue());

								objModel.setName(groupNameTextField.getValue());
								objModel.setStatus((Long) statusCombo
										.getValue());
								objModel.setLevel(level);
								objModel.setParent_id((Long) parentListCombo
										.getValue());

								try {
									objDao.update(objModel);
									loadParentGroups(objModel.getParent_id());
									loadOptions(objModel.getId());
									Notification.show(
											getPropertyName("Success"),
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

		} catch (Exception e) {
		}

	}

	public void loadParentGroups(long id) {
		List testList=null;
		try {
			testList = objDao
					.getAllGroupsNames(getOrganizationID());
			GroupModel sop = new GroupModel();
			sop.setId(0);
			sop.setName("NONE");
			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			bic = SCollectionContainer.setList(testList, "id");
			parentListCombo.setContainerDataSource(bic);
			parentListCombo.setItemCaptionPropertyId("name");
			parentListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void loadOptions(long id) {
		List testList;
		try {
			testList = objDao
					.getAllGroupsNames(getOrganizationID());

			GroupModel sop = new GroupModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			bic = SCollectionContainer.setList(testList, "id");
			groupListCombo.setContainerDataSource(bic);
			groupListCombo.setItemCaptionPropertyId("name");

			groupListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isAlreadyExist(boolean isEdit,String currName) {
		boolean ret = false;
		try {

			if (isEdit) {
				if(!currName.equalsIgnoreCase(groupNameTextField.getValue())){
				ret = objDao.isGroupExist(groupNameTextField.getValue(),
						getOrganizationID());
				if (ret) {
					setRequiredError(groupNameTextField,
							"Name already exist.!", true);
					groupNameTextField.focus();
				}
				}
				return ret;
			} else {
				ret = objDao.isGroupExist(groupNameTextField.getValue(),getOrganizationID());
				if (ret) {
					setRequiredError(groupNameTextField,
							"Name already exist.!", true);
					groupNameTextField.focus();
				}
				return ret;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);
		
		if (actClassCombo.getValue() == null || actClassCombo.getValue().equals("")) {
			setRequiredError(actClassCombo, getPropertyName("invalid_selection"),
					true);
			actClassCombo.focus();
			ret = false;
		} else
			setRequiredError(actClassCombo, null, false);

//		if (parentListCombo.getValue() == null
//				|| parentListCombo.getValue().equals("")) {
//			setRequiredError(parentListCombo,
//					getPropertyName("invalid_selection"), true);
//			parentListCombo.focus();
//			ret = false;
//		} else
//			setRequiredError(parentListCombo, null, false);

		if (groupNameTextField.getValue() == null
				|| groupNameTextField.getValue().equals("")) {
			setRequiredError(groupNameTextField,
					getPropertyName("invalid_data"), true);
			groupNameTextField.focus();
			ret = false;
		} else
			setRequiredError(groupNameTextField, null, false);

		return ret;
	}

	public void removeErrorMsg() {
		parentListCombo.setComponentError(null);
		statusCombo.setComponentError(null);
		groupNameTextField.setComponentError(null);
		actClassCombo.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
