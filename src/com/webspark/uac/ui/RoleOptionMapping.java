package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.annotations.Theme;
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
import com.webspark.model.S_OptionModel;
import com.webspark.model.S_RoleOptionMappingModel;
import com.webspark.uac.dao.RoleDao;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class RoleOptionMapping extends SparkLogic {

	private static final long serialVersionUID = 7438743701932162777L;

	long id = 0;

	final SFormLayout content;
	SOptionGroup optionGroup;
	SComboField roles;
	final SCheckBox options;

	final SButton save = new SButton(getPropertyName("Save"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	SCheckBox checkAll;
	SCheckBox resetAll;

	RoleDao rolDao = new RoleDao();

	public RoleOptionMapping() throws Exception {

		setWidth("600px");
		setHeight("600px");

		content = new SFormLayout();

		long role = (Long) getHttpSession().getAttribute("role_id");

		// **********************************************************

		checkAll = new SCheckBox(null, false);
		resetAll = new SCheckBox(null, false);
		resetAll.setDescription(getPropertyName("warning_msg"));
		List testList = null;

		if (role == 1) {
			testList = rolDao.getAllRolesFromRole(true);
		} else {
			testList = rolDao.getAllRolesFromRole(false);
		}

		roles = new SComboField(getPropertyName("role"), 300, testList, "id",
				"role_name");
		roles.setInputPrompt(getPropertyName("select"));

		options = new SCheckBox(getPropertyName("options"), 300);

		if (role == 1)
			testList = new LoginOptionMappingDao().getAllOptionsByRole(true,getLanguageID());
		else
			testList = new LoginOptionMappingDao().getAllOptionsByRole(false,getLanguageID());

		optionGroup = new SOptionGroup(getPropertyName("options"), 300,
				testList, "option_id", "option_name", true);
		optionGroup.setImmediate(false);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		content.addComponent(roles);

		SHorizontalLayout hLay = new SHorizontalLayout();
		hLay.addComponent(new SLabel(getPropertyName("check_all")));
		hLay.addComponent(checkAll);
		hLay.addComponent(new SLabel(getPropertyName("reset")));
		hLay.addComponent(resetAll);
		hLay.setSpacing(true);
		content.addComponent(hLay);

		content.addComponent(optionGroup);

		save.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(save);

		content.addComponent(buttonLayout);

		content.setSizeUndefined();

		setContent(content);

		checkAll.addListener(new Property.ValueChangeListener() {
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
							getPropertyName("issue_occured") + e.getCause(),
							Type.ERROR_MESSAGE);
				}
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					Set<Long> options_selected = (Set<Long>) optionGroup
							.getValue();

					if (roles.getValue() != null
							|| !roles.getValue().toString().equals("")) {

						List<S_RoleOptionMappingModel> rolOptList = new ArrayList<S_RoleOptionMappingModel>();
						S_RoleOptionMappingModel rom;
						for (Long option_id : options_selected) {
							rom = new S_RoleOptionMappingModel();

							rom.setRole_id(new S_UserRoleModel((Long) roles
									.getValue()));
							rom.setOption_id(new S_OptionModel(option_id));
							rolOptList.add(rom);
						}
						boolean reset = false;
						if (resetAll.getValue()) {
							reset = true;
						}
						rolDao.updateOptionsToRole((Long) roles.getValue(),
								rolOptList, reset);

						Notification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notification.show(getPropertyName("Error"),
							Type.ERROR_MESSAGE);
				}

			}

		});

		roles.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {

					checkAll.setValue(false);

					if (roles.getValue() != null
							&& !roles.getValue().toString().equals("0")) {

						List<Long> optList = rolDao
								.selectOptionsToRole((Long) roles.getValue());

						Set<Long> lst = new HashSet<Long>();
						for (Long optId : optList) {
							lst.add(optId);
						}

						optionGroup.setValue(lst);

					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
