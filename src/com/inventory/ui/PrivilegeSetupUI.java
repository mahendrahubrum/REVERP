package com.inventory.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.dao.PrivilageSetupDao;
import com.inventory.model.PrivilageSetupModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class PrivilegeSetupUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private PrivilageSetupDao daoObj;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SNativeSelect optionSelect;

	SListSelect userSelect;

	UserManagementDao umDao;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {
			umDao = new UserManagementDao();
			setSize(380, 320);

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");

			optionSelect = new SNativeSelect(
					getPropertyName("privilege_option"), 100,
					SConstants.privilegeTypes.privilageTypes, "intKey", "value");

			optionSelect.setImmediate(true);

			optionSelect.setValue(1);

			userSelect = new SListSelect(getPropertyName("users"), 200, 100);

			userSelect.setMultiSelect(true);
			userSelect.setNullSelectionAllowed(true);

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new PrivilageSetupDao();
			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(optionSelect);
			formLayout.addComponent(userSelect);

			generateButton = new SButton(getPropertyName("set"));
			buttonLayout.addComponent(generateButton);
			formLayout.addComponent(buttonLayout);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
				}
			}
		});

		organizationSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(
							new OfficeDao()
									.getAllOfficeNamesUnderOrg((Long) organizationSelect
											.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				officeSelect.setContainerDataSource(bic);
				officeSelect.setItemCaptionPropertyId("name");

			}
		});

		officeSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(umDao
							.getAllLoginsFromOffice((Long) officeSelect
									.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				userSelect.setContainerDataSource(bic);
				userSelect.setItemCaptionPropertyId("login_name");

				setUsers();

			}
		});

		optionSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(umDao
							.getAllLoginsFromOffice((Long) officeSelect
									.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				userSelect.setContainerDataSource(bic);
				userSelect.setItemCaptionPropertyId("login_name");

				setUsers();

			}
		});

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());

		userSelect.addShortcutListener(new ShortcutListener("Clear",
				ShortcutAction.KeyCode.ESCAPE, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				userSelect.setValue(null);
			}
		});

		mainPanel.setContent(formLayout);

		return mainPanel;
	}

	protected void generateReport() {
		try {

			if (isValid()) {

				List list = new ArrayList();

				Set<Long> options_selected = new HashSet<Long>();
				if (userSelect.getValue() != null) {
					String salesIDs = "";
					options_selected = (Set<Long>) userSelect.getValue();
					Iterator it1 = options_selected.iterator();
					PrivilageSetupModel obj;
					while (it1.hasNext()) {
						obj = new PrivilageSetupModel();
						obj.setLogin_id((Long) it1.next());
						obj.setOffice_id((Long) officeSelect.getValue());
						obj.setOption_id((Integer) optionSelect.getValue());
						list.add(obj);
					}

					daoObj.updateOptionsToUser(list,
							(Long) officeSelect.getValue(),
							(Integer) optionSelect.getValue());

				}

				Notification.show(getPropertyName("save_success"),
						Type.WARNING_MESSAGE);

			}
		} catch (Exception e) {
			Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public void setUsers() {

		try {
			if (officeSelect.getValue() != null
					&& optionSelect.getValue() != null)
				userSelect.setValue(daoObj.getAllUsers(
						(Long) officeSelect.getValue(),
						(Integer) optionSelect.getValue()));

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (optionSelect.getValue() == null
				|| optionSelect.getValue().equals("")) {
			setRequiredError(optionSelect,
					getPropertyName("invalid_selection"), true);
			optionSelect.focus();
			ret = false;
		} else
			setRequiredError(optionSelect, null, false);

		if (userSelect.getValue() == null) {
			setRequiredError(userSelect, getPropertyName("invalid_selection"),
					true);
			userSelect.focus();
			ret = false;
		} else
			setRequiredError(userSelect, null, false);

		if (officeSelect.getValue() == null
				|| officeSelect.getValue().equals("")) {
			setRequiredError(officeSelect,
					getPropertyName("invalid_selection"), true);
			officeSelect.focus();
			ret = false;
		} else
			setRequiredError(officeSelect, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
