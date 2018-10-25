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
import com.webspark.common.util.SConstants;
import com.webspark.dao.LoginOptionMappingDao;
import com.webspark.dao.OfficeOptionMappingDao;
import com.webspark.model.OfficeOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 11, 2014
 */

public class OfficeOptionMapping extends SparkLogic {

	private static final long serialVersionUID = -3705435395511080834L;

	long id = 0;

	final SFormLayout content;
	SOptionGroup optionGroup;
	SComboField office;

	SCheckBox checkAll;

	final SButton save = new SButton(getPropertyName("Save"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	LoginOptionMappingDao lomDao = new LoginOptionMappingDao();
	OfficeOptionMappingDao dao = new OfficeOptionMappingDao();
	OfficeDao officeDao=new OfficeDao();

	List optionsList = null;

	public OfficeOptionMapping() throws Exception {

		setCaption("Add User Role");
		setWidth("600px");
		setHeight("600px");
		content = new SFormLayout();

		long role = (Long) getHttpSession().getAttribute("role_id");

		List testList = null;

		if (isSuperAdmin()) {
			testList = officeDao.getAllOfficeNamesUnderAllOrg();
		} else  {
			testList = officeDao
					.getAllOfficeNamesUnderOrg(getOrganizationID());
		}

		office = new SComboField(getPropertyName("office"), 300, testList, "id",
				"name");
		office.setInputPrompt(getPropertyName("select"));

		checkAll = new SCheckBox(null, false);
		if (role == SConstants.ROLE_SYSTEM_ADMIN||role == SConstants.ROLE_SUPER_ADMIN)
			optionsList = dao.getAllOptionsUnderAllOrganization();
		else
			optionsList = dao.getAllOptionsUnderOrganization(getOrganizationID());

		optionGroup = new SOptionGroup(getPropertyName("options"), 300,
				optionsList, "option_id", "option_name", true);
		optionGroup.setImmediate(false);
		// optionGroup.setHorizontal(true);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		content.addComponent(office);

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

					if (office.getValue() != null
							|| !office.getValue().toString().equals("")) {

						List<OfficeOptionMappingModel> usrOptList = new ArrayList<OfficeOptionMappingModel>();
						OfficeOptionMappingModel usr;
						for (Long option_id : options_selected) {
							usr = new OfficeOptionMappingModel();

							usr.setOfficeId(new S_OfficeModel((Long) office
									.getValue()));
							usr.setOption_id(new S_OptionModel(option_id));
							usrOptList.add(usr);
						}

						dao.updateOptionsToOffice((Long) office.getValue(),
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

		office.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {

					checkAll.setValue(false);

					if (office.getValue() != null
							&& !office.getValue().toString().equals("")) {

						List<Long> optList = dao
								.selectOptionsToOffice((Long) office.getValue());

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
		
		office.setValue(getOfficeID());

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
