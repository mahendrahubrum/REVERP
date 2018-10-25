package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.business.LedgerBusiness;
import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.model.ItemSubGroupModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SListSelect;
import com.webspark.Components.STextField;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class LedgerPanel extends SContainerPanel {

	private static final long serialVersionUID = 3464302518882512489L;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField ledgerListCombo;
	STextField ledgerNameTextField;
	SComboField groupCombo;
	SComboField statusCombo;

	// SAddressField address1Field;

	SButton save;
	SButton delete;
	SButton update;

	GroupDao gpDao;
	OfficeDao ofcDao;

	List list;
	LedgerDao objDao = new LedgerDao();
	LedgerBusiness ledgerBusiness = new LedgerBusiness();

	SButton createNewButton;

	private SListSelect officeSel;

	public LedgerPanel() {

		setId("Ledger");
		setSize(520, 320);
		objDao = new LedgerDao();
		gpDao = new GroupDao();
		ofcDao = new OfficeDao();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));

			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			// address1Field=new SAddressField(2);

			save = new SButton(getPropertyName("Save"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);

			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			list = objDao.getAllGeneralLedgers(getOfficeID());
			ItemSubGroupModel og = new ItemSubGroupModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			ledgerListCombo = new SComboField(null, 300, list, "id", "name");
			ledgerListCombo.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300, SConstants.statuses.status, "key", "value");
			statusCombo.setInputPrompt(getPropertyName("select"));
			statusCombo.setValue((long) 1);

			groupCombo = new SComboField(getPropertyName("group"), 300, gpDao.getAllGroupsNames(getOrganizationID()),
					"id", "name", true, getPropertyName("select"));

			ledgerNameTextField = new STextField(getPropertyName("ledger_name"), 300);

			officeSel = new SListSelect(getPropertyName("create_in_offices"), 300,
					ofcDao.getAllOfficeNamesUnderOrg(getOrganizationID()), "id", "name");
			officeSel.setHeight("100px");
			officeSel.setMultiSelect(true);
			officeSel.setNullSelectionAllowed(true);
			HashSet set = new HashSet(officeSel.getItemIds());
			officeSel.setValue(set);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(getPropertyName("ledger"));
			salLisrLay.addComponent(ledgerListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(ledgerNameTextField);
			form.addComponent(groupCombo);
			form.addComponent(statusCombo);
			form.addComponent(officeSel);
			// form.addComponent(address1Field);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			addShortcutListener(new ShortcutListener("Add New", ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});

			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (save.isVisible())
						save.click();
					else
						update.click();
				}
			});

			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					ledgerListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (ledgerListCombo.getValue() == null || ledgerListCombo.getValue().toString().equals("0")) {

							if (isValid()) {
								List ledgerModels = new ArrayList();
								Set set = new HashSet();
								set.addAll((Set) officeSel.getValue());
								List idList = new ArrayList();
								idList.addAll(set);
//								idList.add(getOfficeID());
								S_OfficeModel ofcMdl = null;
								Iterator iter = idList.iterator();
								while (iter.hasNext()) {
									ofcMdl = (S_OfficeModel) ofcDao.getOffice((Long) iter.next());
									LedgerModel ledgerModel = new LedgerModel();
									ledgerModel.setName(ledgerNameTextField.getValue());
									ledgerModel.setGroup(new GroupModel((Long) groupCombo.getValue()));
									// objModel.setAddress(new AddressModel(1));
									ledgerModel.setCurrent_balance(0);
									ledgerModel.setStatus((Long) statusCombo.getValue());
									ledgerModel.setParentId(objDao.getParentUnderGroup((Long) groupCombo.getValue()));

									ledgerModel.setOffice(new S_OfficeModel(ofcMdl.getId()));
									ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
									ledgerModels.add(ledgerModel);
								}

								try {
//									long id=objDao.save(ledgerModels,getOfficeID());
									long id = ledgerBusiness.save(ledgerModels, getOfficeID());
									loadOptions(id);
									if (id == 0) {
										Notification.show(getPropertyName("Duplicate ledger name"), Type.ERROR_MESSAGE);
									} else
										Notification.show(getPropertyName("Success"), getPropertyName("save_success"),
												Type.WARNING_MESSAGE);

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
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

			ledgerListCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (ledgerListCombo.getValue() != null && !ledgerListCombo.getValue().toString().equals("0")) {

							save.setVisible(false);
							delete.setVisible(true);
							update.setVisible(true);
							officeSel.setVisible(false);

							LedgerModel objModel = objDao.getLedgeer((Long) ledgerListCombo.getValue());

							ledgerNameTextField.setValue(objModel.getName());
							// address1Field.loadAddress(objModel.getAddress().getId());
							groupCombo.setValue(objModel.getGroup().getId());
							statusCombo.setValue(objModel.getStatus());

						} else {
							save.setVisible(true);
							delete.setVisible(false);
							update.setVisible(false);
							officeSel.setVisible(true);
							HashSet set = new HashSet(officeSel.getItemIds());
							officeSel.setValue(set);

							ledgerNameTextField.setValue("");
							statusCombo.setValue((long) 1);
							groupCombo.setValue(null);

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

						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {

									try {
										objDao.delete((Long) ledgerListCombo.getValue());

										Notification.show(getPropertyName("Success"),
												getPropertyName("deleted_success"), Type.WARNING_MESSAGE);

										loadOptions(0);

									}catch(ConstraintViolationException ce) {
										Notification.show(getPropertyName("Cannot delete the ledger since it is already in the payment mode"), Type.WARNING_MESSAGE);
										ce.printStackTrace();
									}catch (Exception e) {
										// TODO Auto-generated catch
										// block
										Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
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
						if (ledgerListCombo.getValue() != null) {

							if (isValid()) {

								LedgerModel oldModel = objDao.getLedgeer((Long) ledgerListCombo.getValue());
								// AddressModel addr=address1Field.getAddress();
								// addr.setId(objModel.getAddress().getId());

								LedgerModel objModel;
								List mainList = new ArrayList();
								List idList = objDao.getAllLedgersUnderParentFromLedger(oldModel);
								Iterator iter = idList.iterator();
								while (iter.hasNext()) {
									objModel = (LedgerModel) iter.next();
									objModel.setName(ledgerNameTextField.getValue());
									objModel.setGroup(new GroupModel((Long) groupCombo.getValue()));
									objModel.setCurrent_balance(objModel.getCurrent_balance());
									objModel.setStatus((Long) statusCombo.getValue());
									mainList.add(objModel);
								}

								try {
									long id = objDao.update(mainList, getOfficeID());
									loadOptions(id);
									Notification.show(getPropertyName("Success"), getPropertyName("update_success"),
											Type.WARNING_MESSAGE);
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
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

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllGeneralLedgers(getOfficeID());

			LedgerModel sop = new LedgerModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			ledgerListCombo.setContainerDataSource(bic);
			ledgerListCombo.setItemCaptionPropertyId("name");

			ledgerListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"), true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (groupCombo.getValue() == null || groupCombo.getValue().equals("")) {
			setRequiredError(groupCombo, getPropertyName("invalid_selection"), true);
			groupCombo.focus();
			ret = false;
		} else
			setRequiredError(groupCombo, null, false);

		if (ledgerNameTextField.getValue() == null || ledgerNameTextField.getValue().equals("")) {
			setRequiredError(ledgerNameTextField, getPropertyName("invalid_data"), true);
			ledgerNameTextField.focus();
			ret = false;
		} else
			setRequiredError(ledgerNameTextField, null, false);

		return ret;
	}

	public void removeErrorMsg() {
		statusCombo.setComponentError(null);
		groupCombo.setComponentError(null);
		ledgerNameTextField.setComponentError(null);
	}

	public void reloadGroup() {
		try {
			if (groupCombo.isReadOnly()) {
				Object obj = groupCombo.getValue();
				groupCombo.setReadOnly(false);
				list = gpDao.getAllActiveGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupCombo.setContainerDataSource(bic);
				groupCombo.setItemCaptionPropertyId("name");
				groupCombo.setValue(obj);
				groupCombo.setReadOnly(true);
			} else {
				list = gpDao.getAllActiveGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupCombo.setContainerDataSource(bic);
				groupCombo.setItemCaptionPropertyId("name");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
