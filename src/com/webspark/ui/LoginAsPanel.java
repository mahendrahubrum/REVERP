package com.webspark.ui;

import java.util.Date;

import com.inventory.config.settings.biz.SettingsBiz;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.webspark.Components.SButton;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.SEncryption;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.LoginDao;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

public class LoginAsPanel extends SContainerPanel {

	WrappedSession session = new SessionUtil().getHttpSession();

	STextField userID;
	SPasswordField password;

	LoginDao lcdObj;
	SButton submit;

	SLabel errLabel;

	public LoginAsPanel() {

		lcdObj = new LoginDao();
		setCaption("Login");

		SPanel pan = new SPanel();
		// pan.setWidth("300px");
		// pan.setHeight("300px");

		lcdObj = new LoginDao();

		final SVerticalLayout layout = new SVerticalLayout();
		setContent(layout);
		layout.setStyleName("marg_100");

		SFormLayout topPanel = new SFormLayout();

		// SWindow window=new SWindow();

		errLabel = new SLabel(null);
		userID = new STextField(null, 200);
		password = new SPasswordField(null, 200);
		submit = new SButton(getPropertyName("login"));

		errLabel.setVisible(false);

		userID.focus();

		// topPanel.addComponent(userID);
		// topPanel.addComponent(password);
		// topPanel.addComponent(submit);
		// topPanel.setComponentAlignment(submit, Alignment.BOTTOM_CENTER);

		// submit.setStyleName("marg_200");

		topPanel.setMargin(true);

		CustomLayout custom = new CustomLayout(getPropertyName("login"));

		final STextField username = new STextField();
		custom.addComponent(userID, "username");

		// final SPasswordField password = new SPasswordField();
		custom.addComponent(password, "password");

		custom.addComponent(errLabel, "err");

		// final SButton ok = new SButton("Login");
		custom.addComponent(submit, "okbutton");

		// userID.setComponentError(new UserError("Bad value"));
		// userID.setRequiredError("Value is required");

		// getCurrent().addWindow(window);

		pan.addShortcutListener(new ShortcutListener("Shortcut Name",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				submit.click();
			}
		});

		submit.addClickListener(new SButton.ClickListener() {
			public void buttonClick(ClickEvent event) {

				try {
					errLabel.setValue("");
					if (isValid()) {

						if (lcdObj.isExist(userID.getValue().toString())) {
							UserModel usr = lcdObj.getUserFromLoginName(userID
									.getValue().toString());

							if (SEncryption.encrypt(
									password.getValue().toString()).equals(
									usr.getLoginId().getPassword())) {
								// Notification.show("Success!", "Success..! ",
								// Type.WARNING_MESSAGE);

								session.setAttribute("login_id", usr
										.getLoginId().getId());

								session.setAttribute("user_id", usr.getId());

								session.setAttribute("role_id", usr
										.getLoginId().getUserType().getId());

								session.setAttribute("login_name", usr
										.getLoginId().getLogin_name());

								// session.setAttribute("user_type",
								// usr.getLoginId().getUserType());

								session.setAttribute("office_id", usr
										.getLoginId().getOffice().getId());

								session.setAttribute("currency_id", usr
										.getLoginId().getOffice().getCurrency()
										.getId());

								session.setAttribute("organization_id", usr
										.getLoginId().getOffice()
										.getOrganization().getId());

								// session.setAttribute("working_date",
								// usr.getLoginId().getOffice().getWorkingDate());
								session.setAttribute("working_date",
										new java.sql.Date(new Date().getTime()));

								// The Settings Session Variables are adding
								// inside this method
								new SettingsBiz().updateSettingsValue(usr
										.getLoginId().getOffice()
										.getOrganization().getId(), usr
										.getLoginId().getOffice().getId());

								getUI().getUI().setContent(new MainLayout());
								// ViewHandl.activateView(MenuView.class);

								// getParent().getUI().getCurrent().focus();
								// new MainGUI();

							} else {
								errLabel.setVisible(true);
								errLabel.setValue("Wrong password..! ");
								password.focus();
								// Notification.show("Wrong!",
								// "Wrong password..! ",
								// Type.ERROR_MESSAGE);

								// close();
								// getUI().getCurrent().addWindow(new Login());
							}
						} else {
							errLabel.setVisible(true);
							// Notification.show("Unknown User",
							// "This user id doesn't exist..! ",
							// Type.ERROR_MESSAGE);
							errLabel.setValue("This user id doesn't exist..! ");
							userID.focus();
							// close();
							// getUI().getCurrent().addWindow(new Login());
						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		setContent(custom);
		// TODO Auto-generated method stub
	}

	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;
		if (userID.getValue() == null || userID.getValue().equals("")) {
			// errLabel.setVisible(true);
			// Notification.show("Log in Name?", "Enter your Login name..! ",
			// Type.TRAY_NOTIFICATION);
			// errLabel.setValue("Enter your Login name..! ");
			setRequiredError(userID, getPropertyName("invalid_data"), true);
			userID.focus();
			ret = false;
		} else
			setRequiredError(userID, getPropertyName("invalid_data"), false);

		if (password.getValue() == null || password.getValue().equals("")) {
			// errLabel.setVisible(true);
			// Notification.show("Password?", "Enter your Password..! ",
			// Type.TRAY_NOTIFICATION);
			// errLabel.setValue("Enter your password..! ");
			setRequiredError(password, getPropertyName("invalid_data"), true);
			if (ret == true)
				password.focus();
			ret = false;
		} else
			setRequiredError(password, getPropertyName("invalid_data"), false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
