package com.inventory.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.dao.OfficeAllocationDao;
import com.inventory.model.OfficeAllocationModel;
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
public class OfficeAllocationUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private OfficeAllocationDao daoObj;

	private SComboField organizationSelect;

	private SComboField userSelect;

	SListSelect mapOfficeSelect;

	UserManagementDao umDao;

	OfficeDao ofcDao;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {
			ofcDao = new OfficeDao();

			umDao = new UserManagementDao();
			setSize(450, 400);

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			List orgList = new OrganizationDao().getAllOrganizations();
			organizationSelect = new SComboField(
					getPropertyName("organization"), 300, orgList, "id", "name");

			userSelect = new SComboField(getPropertyName("user"), 300, null,
					"intKey", "value");

			mapOfficeSelect = new SListSelect(getPropertyName("users"), 300,
					150, ofcDao.getAllOfficeName(), "id", "name");

			mapOfficeSelect.setMultiSelect(true);
			mapOfficeSelect.setNullSelectionAllowed(true);

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new OfficeAllocationDao();
			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(userSelect);

			formLayout.addComponent(mapOfficeSelect);

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

				SCollectionContainer bic1 = null;
				// SCollectionContainer bic2 = null;

				try {
					bic1 = SCollectionContainer.setList(umDao
							.getAllLoginsFromOfficeWithRole(
									(Long) organizationSelect.getValue(),
									SConstants.ROLE_SPECIAL_ADMIN), "id");
					userSelect.setContainerDataSource(bic1);
					userSelect.setItemCaptionPropertyId("login_name");

					// bic2=SCollectionContainer.setList(
					// ofcDao.getAllOfficeNamesUnderOrg((Long)
					// organizationSelect
					// .getValue()), "id");
					// mapOfficeSelect.setContainerDataSource(bic2);
					// mapOfficeSelect.setItemCaptionPropertyId("name");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		userSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				setUsers();

			}
		});

		organizationSelect.setValue(getOrganizationID());

		mapOfficeSelect.addShortcutListener(new ShortcutListener("Clear",
				ShortcutAction.KeyCode.ESCAPE, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				mapOfficeSelect.setValue(null);
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
				if (mapOfficeSelect.getValue() != null) {
					String salesIDs = "";
					options_selected = (Set<Long>) mapOfficeSelect.getValue();
					Iterator it1 = options_selected.iterator();
					OfficeAllocationModel obj;
					while (it1.hasNext()) {
						obj = new OfficeAllocationModel();
						obj.setOffice_id((Long) it1.next());
						obj.setLogin_id((Long) userSelect.getValue());
						list.add(obj);
					}

					daoObj.updateOptionsToUser(list,
							(Long) userSelect.getValue());

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
			if (userSelect.getValue() != null)
				mapOfficeSelect.setValue(daoObj.getAllOffices((Long) userSelect
						.getValue()));

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (mapOfficeSelect.getValue() == null) {
			setRequiredError(mapOfficeSelect,
					getPropertyName("invalid_selection"), true);
			mapOfficeSelect.focus();
			ret = false;
		} else
			setRequiredError(mapOfficeSelect, null, false);

		if (userSelect.getValue() == null || userSelect.getValue().equals("")) {
			setRequiredError(userSelect, getPropertyName("invalid_selection"),
					true);
			userSelect.focus();
			ret = false;
		} else
			setRequiredError(userSelect, null, false);

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
