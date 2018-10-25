package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.LoginOptionMappingDao;
import com.webspark.dao.OrganizationOptionMappingDao;
import com.webspark.model.OrganizationOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 11, 2014
 */

public class OrganizationOptionMapping extends SparkLogic {

	private static final long serialVersionUID = 6200961420201057328L;


	final SFormLayout content;
	SOptionGroup optionGroup;
	SComboField organization;

	SCheckBox checkAll;

	final SButton save = new SButton(getPropertyName("Save"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	LoginOptionMappingDao lomDao = new LoginOptionMappingDao();
	OrganizationOptionMappingDao dao = new OrganizationOptionMappingDao();
	OrganizationDao organizationDao=new OrganizationDao();

	List optionsList = null;

	public OrganizationOptionMapping() throws Exception {

		setWidth("600px");
		setHeight("600px");
		content = new SFormLayout();

		long role = (Long) getHttpSession().getAttribute("role_id");

		List testList = organizationDao.getAllOrganizations();

		organization = new SComboField(getPropertyName("organization"), 300, testList, "id",
				"name");
		organization.setInputPrompt(getPropertyName("select"));

		checkAll = new SCheckBox(null, false);
		if (role == 1)
			optionsList = lomDao.getAllOptionsByRole(true);
		else
			optionsList = lomDao.getAllOptionsByRole(false);

		optionGroup = new SOptionGroup(getPropertyName("options"), 300,
				optionsList, "option_id", "option_name", true);
		optionGroup.setImmediate(false);
		// optionGroup.setHorizontal(true);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		content.addComponent(organization);

		SHorizontalLayout hLay = new SHorizontalLayout();
		hLay.addComponent(new SLabel(getPropertyName("check_all")));
		hLay.addComponent(checkAll);
		hLay.setSpacing(true);
		content.addComponent(hLay);
		save.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(save);

		content.addComponent(optionGroup);
		content.addComponent(buttonLayout);

		content.setSizeUndefined();

		setContent(content);

		checkAll.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {

					if (checkAll.getValue()) {
						Set lst = new HashSet();
						for (Object optId : optionGroup.getItemIds()) {
							lst.add(optId);
						}
						optionGroup.setValue(lst);
					} else {
						optionGroup.setValue(null);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notification.show(getPropertyName("Error"),
							Type.ERROR_MESSAGE);
				}
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					Set<Long> options_selected = (Set<Long>) optionGroup
							.getValue();

					if (organization.getValue() != null
							|| !organization.getValue().toString().equals("")) {

						List<OrganizationOptionMappingModel> usrOptList = new ArrayList<OrganizationOptionMappingModel>();
						OrganizationOptionMappingModel usr;
						for (Long option_id : options_selected) {
							usr = new OrganizationOptionMappingModel();

							usr.setOrganizationId(new S_OrganizationModel((Long) organization
									.getValue()));
							usr.setOption_id(new S_OptionModel(option_id));
							usrOptList.add(usr);
						}

						dao.updateOptionsToOrganization((Long) organization.getValue(),
								usrOptList);

						Notification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
					}

				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("Error"),
							Type.ERROR_MESSAGE);
				}
			}

		});

		organization.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {

					checkAll.setValue(false);

					if (organization.getValue() != null
							&& !organization.getValue().toString().equals("")) {

						List<Long> optList = dao
								.selectOptionsToOrganization((Long) organization.getValue());

						Set<Long> lst = new HashSet<Long>();
						for (Long optId : optList) {
							if (isAvail((Long) optId))
								lst.add(optId);
						}

						optionGroup.setValue(lst);

					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		organization.setValue(getOrganizationID());

	}

	S_OptionModel optObj;

	public boolean isAvail(long opt_id) {
		for (int i = 0; i < optionsList.size(); i++) {
			optObj = (S_OptionModel) optionsList.get(i);
			if (optObj.getOption_id() == opt_id)
				return true;
		}
		return false;
	}

	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {
		return null;

	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
