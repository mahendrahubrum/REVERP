package com.webspark.uac.ui;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SEncryption;
import com.webspark.uac.dao.ChangePasswordDao;

public class ChangePasswordUI extends SparkLogic {

	/**
	 * @param args
	 */
	WrappedSession session;
	ChangePasswordDao dbOprtn;

	public ChangePasswordUI() {
		super();
		// TODO Auto-generated constructor stub
		setCaption("Change Password");
	}

	SPasswordField txtPassword;
	SPasswordField txtConformPasswrd;
	SPasswordField txtCurrentPasswrd;

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		session = getHttpSession();
		setWidth("490px");
		setHeight("275px");
		List testList = null;
		dbOprtn = new ChangePasswordDao();
		txtPassword = new SPasswordField(getPropertyName("new_password"), 300);
		txtConformPasswrd = new SPasswordField(getPropertyName("confirm_password"), 300);
		txtCurrentPasswrd = new SPasswordField(getPropertyName("current_password"), 300);
		final SLabel lblUser = new SLabel(getPropertyName("login_name"),"");
		final SButton reset = new SButton(getPropertyName("reset"));

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout gd = new SFormLayout();
		gd.setMargin(true);
		gd.setSpacing(true);

		gd.addComponent(lblUser);
		gd.addComponent(txtCurrentPasswrd);
		gd.addComponent(txtPassword);
		gd.addComponent(txtConformPasswrd);
		gd.addComponent(reset);

		pan.setContent(gd);

		lblUser.setValue(session.getAttribute("login_name").toString());

		// resets password

		reset.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {

				String list = null;
				try {
					list = dbOprtn.getPassword(Long.parseLong(session
							.getAttribute("login_id").toString()));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {

					if (isValid()) {

						if ((list).equals(SEncryption.encrypt(txtCurrentPasswrd
								.getValue()))) {

							if (txtPassword.getValue().equals(
									txtConformPasswrd.getValue())) {

								ConfirmDialog.show(getUI(),
										getPropertyName("are_you_sure"),
										new ConfirmDialog.Listener() {
											public void onClose(
													ConfirmDialog dialog) {
												if (dialog.isConfirmed()) {

													try {

														dbOprtn.resetPaswrd(
																Long.parseLong(session
																		.getAttribute(
																				"login_id")
																		.toString()),
																SEncryption
																		.encrypt(txtPassword
																				.getValue()));
														Notification
																.show(getPropertyName("save_success"),
																		Type.WARNING_MESSAGE);

													}

													catch (Exception e) {
														// TODO Auto-generated
														// catch block
														Notification
																.show(getPropertyName("Error"),
																		Type.ERROR_MESSAGE);
														e.printStackTrace();
													}
												}

												// Confirmed to continue
												// DO STUFF
												else {
													// User did not confirm
													// CANCEL STUFF
												}
											}

										});

								txtConformPasswrd.setComponentError(null);
							} else {
								setRequiredError(
										txtConformPasswrd,
										getPropertyName("Error_msg_for_password"),
										true);
							}

							txtCurrentPasswrd.setComponentError(null);
						} else {
							setRequiredError(
									txtCurrentPasswrd,
									getPropertyName("Error_msg_for_wrongpassword"),
									true);
						}

					}
				} catch (ReadOnlyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		addShortcutListener(new ShortcutListener("Save",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				reset.click();
			}
		});

		return pan;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (txtConformPasswrd.getValue() == null
				|| txtConformPasswrd.getValue().equals("")) {
			setRequiredError(txtConformPasswrd,
					getPropertyName("confirm_password"), true);
			ret = false;
			txtConformPasswrd.focus();
		} else
			setRequiredError(txtConformPasswrd, null, false);

		if (txtPassword.getValue() == null || txtPassword.getValue().equals("")) {
			setRequiredError(txtPassword, getPropertyName("invalid_data"), true);
			ret = false;
			txtPassword.focus();
		} else
			setRequiredError(txtPassword, null, false);

		if (txtCurrentPasswrd.getValue() == null
				|| txtCurrentPasswrd.getValue().equals("")) {
			setRequiredError(txtCurrentPasswrd,
					getPropertyName("invalid_data"), true);
			ret = false;
			txtCurrentPasswrd.focus();
		} else
			setRequiredError(txtCurrentPasswrd, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
