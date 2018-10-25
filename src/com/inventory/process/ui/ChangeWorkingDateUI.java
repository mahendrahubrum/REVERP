package com.inventory.process.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 3, 2013
 */
public class ChangeWorkingDateUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	SDateField toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			setSize(340, 210);

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			List orgList = new ArrayList();
			orgList.add(new S_OrganizationModel(0, "ALL"));
			orgList.addAll(new OrganizationDao().getAllOrganizations());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200, orgList, "id", "name");
			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");

			/*
			 * if(isSystemAdmin() || isSuperAdmin()) {
			 * organizationSelect.setEnabled(true);
			 * officeSelect.setEnabled(true); } else {
			 * organizationSelect.setEnabled(false); if(isOrganizationAdmin()) {
			 * officeSelect.setEnabled(true); } else {
			 * officeSelect.setEnabled(false); } }
			 */

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), new Date(getWorkingDate().getTime()));

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(toDate);

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
				List lst = new ArrayList();
				try {
					if (organizationSelect.getValue() != null) {
						long org_id = (Long) organizationSelect.getValue();

						S_OfficeModel ofc = new S_OfficeModel(0, "ALL");
						lst.add(ofc);
						if (org_id != 0) {
							lst.addAll(new OfficeDao()
									.getAllOfficeNamesUnderOrg((Long) organizationSelect
											.getValue()));
						}

					}

					bic = SCollectionContainer.setList(lst, "id");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				officeSelect.setContainerDataSource(bic);
				officeSelect.setItemCaptionPropertyId("name");

			}
		});

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());

		mainPanel.setContent(formLayout);

		return mainPanel;
	}

	protected void generateReport() {
		try {
			long org_id = 0, ofc_id = 0;

			if (organizationSelect.getValue() != null)
				org_id = (Long) organizationSelect.getValue();
			if (officeSelect.getValue() != null)
				ofc_id = (Long) officeSelect.getValue();

			new CommonMethodsDao().updateWorkingDate(ofc_id, org_id,
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));

			getHttpSession().invalidate();
			getUI().getPage().setLocation(
					VaadinService.getCurrentRequest().getContextPath() + "/");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("select_date"), true);
			toDate.focus();
			ret = false;
		} else
			setRequiredError(toDate, null, false);

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
