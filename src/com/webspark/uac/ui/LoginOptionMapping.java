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
import com.webspark.dao.LoginCreationDao;
import com.webspark.dao.LoginOptionMappingDao;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class LoginOptionMapping extends SparkLogic {

	long id = 0;

	final SFormLayout content;
	SOptionGroup optionGroup;
	SComboField users;

	SCheckBox checkAll;

	final SButton save = new SButton(getPropertyName("Save"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	LoginCreationDao lcDao = new LoginCreationDao();
	LoginOptionMappingDao lomDao = new LoginOptionMappingDao();

	List optionsList = null;

	public LoginOptionMapping() throws Exception {

		setCaption("Add User Role");
		setWidth("600px");
		setHeight("600px");
		content = new SFormLayout();

		long role = (Long) getHttpSession().getAttribute("role_id");

		// **********************************************************

		List testList = null;

		if (isSuperAdmin()) {
			testList = new UserManagementDao().getAllLoginNames();
		} else if (isSystemAdmin()) {
			testList = new UserManagementDao()
					.getAllLoginNamesWithoutSparkAdmin();
		} else {
			testList = new UserManagementDao()
					.getAllLoginsForOrg(getOrganizationID());
		}

		users = new SComboField(getPropertyName("user"), 300, testList, "id",
				"login_name");
		users.setInputPrompt("------------------- Select a User First -------------------");

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
		content.addComponent(users);

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

					if (users.getValue() != null
							|| !users.getValue().toString().equals("")) {

						List<S_LoginOptionMappingModel> usrOptList = new ArrayList<S_LoginOptionMappingModel>();
						S_LoginOptionMappingModel usr;
						for (Long option_id : options_selected) {
							usr = new S_LoginOptionMappingModel();

							usr.setLogin_id(new S_LoginModel((Long) users
									.getValue()));
							usr.setOption_id(new S_OptionModel(option_id));
							usrOptList.add(usr);
						}

						lomDao.updateOptionsToUser((Long) users.getValue(),
								usrOptList);

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

		users.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {

					checkAll.setValue(false);

					if (users.getValue() != null
							&& !users.getValue().toString().equals("")) {

						List<Long> optList = lomDao
								.selectOptionsToUser((Long) users.getValue());

						Set<Long> lst = new HashSet<Long>();
						for (Long optId : optList) {
							if (isAvail((Long) optId))
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

		users.setValue(getLoginID());
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
