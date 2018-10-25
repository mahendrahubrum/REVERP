package com.webspark.mailclient.ui;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.model.EmailConfigurationModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date 11 Mar 2014
 */
public class ConfigureEmailUI extends SparkLogic {

	private static final long serialVersionUID = -5334099822138641653L;

	private SComboField hostnameSelect;
	private STextField usernameSelect, maximumNoTextField;
	SPasswordField passwordTextField;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;

	private EmailConfigDao dao;

	private SCollectionContainer bic;

	private SButton createNewButton;

	EmailConfigurationModel mdl;

	@Override
	public SPanel getGUI() {

		SPanel pan = new SPanel();

		setSize(450, 350);

		SFormLayout lay = new SFormLayout();
		lay.setWidth("395");

		SHorizontalLayout btnLayout = new SHorizontalLayout();

		pan.setContent(lay);

		dao = new EmailConfigDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription((getPropertyName("add_new")));

		passwordTextField = new SPasswordField(
				getPropertyName("email_password"), 200);
		usernameSelect = new STextField(getPropertyName("email_username"), 200);
		maximumNoTextField = new STextField(
				getPropertyName("initial_sync_limit"), 200);
		maximumNoTextField.setDescription(getPropertyName("email_msg"));

		hostnameSelect = new SComboField(getPropertyName("host_name"));
		hostnameSelect.addItem("gmail-smtp-msa.l.google.com");
		hostnameSelect.addItem("imap.next.mail.yahoo.com");
		hostnameSelect.addItem("rs16.websitehostserver.net");
		hostnameSelect.addItem("smtpout.asia.secureserver.net");
		
		hostnameSelect.setDescription("Hints :- For Gmail use : gmail-smtp-msa.l.google.com , For Yahoo Mail : smtp.mail.yahoo.com , For Webmail : rs16.websitehostserver.net , For Godaddy Mail : smtpout.asia.secureserver.net ");


		lay.addComponent(hostnameSelect);
		lay.addComponent(usernameSelect);
		lay.addComponent(passwordTextField);
		lay.addComponent(maximumNoTextField);

		saveButton = new SButton(getPropertyName("Save"));
		updateButton = new SButton(getPropertyName("Update"));
		deleteButton = new SButton(getPropertyName("Delete"));

		updateButton.setVisible(false);
		deleteButton.setVisible(false);

		btnLayout.addComponent(saveButton);
		btnLayout.addComponent(updateButton);
		btnLayout.addComponent(deleteButton);

		lay.addComponent(btnLayout);

		lay.addComponent(new SHTMLLabel(null, "<b>"
				+ getPropertyName("mail_synchronization_msg") + "</b>"));

		mdl = null;
		try {
			mdl = dao.getEmailConfiguration(getLoginID());

			if (mdl != null) {
				hostnameSelect.setValue(mdl.getHost_name());
				usernameSelect.setValue(mdl.getUsername());
				passwordTextField.setValue(mdl.getPassword());
				maximumNoTextField.setValue(mdl.getMax_no_emails() + "");
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		lay.setStyleName("cost_calc_style");

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					mdl = null;
					try {
						mdl = dao.getEmailConfiguration(getLoginID());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (mdl != null) {

						ConfirmDialog.show(getUI(),
								getPropertyName("delete_confirmation"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												mdl.setUsername(usernameSelect
														.getValue());
												mdl.setHost_name(hostnameSelect
														.getValue().toString());
												mdl.setUser_id(getLoginID());
												mdl.setPassword(passwordTextField
														.getValue());
												mdl.setMax_no_emails(toInt(maximumNoTextField
														.getValue()));

												dao.saveAndDeleteAllMails(mdl);

												Notification
														.show(getPropertyName("save_success"),
																Type.WARNING_MESSAGE);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
											}
										} else {
											mdl.setUsername(usernameSelect
													.getValue());
											mdl.setHost_name(hostnameSelect
													.getValue().toString());
											mdl.setUser_id(getLoginID());
											mdl.setPassword(passwordTextField
													.getValue());
											mdl.setMax_no_emails(toInt(maximumNoTextField
													.getValue()));

											try {
												dao.save(mdl);
												Notification
														.show(getPropertyName("save_success"),
																Type.WARNING_MESSAGE);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} else {

						mdl = new EmailConfigurationModel();

						mdl.setUsername(usernameSelect.getValue());
						mdl.setHost_name(hostnameSelect.getValue().toString());
						mdl.setUser_id(getLoginID());
						mdl.setPassword(passwordTextField.getValue());
						mdl.setMax_no_emails(toInt(maximumNoTextField
								.getValue()));

						try {
							dao.save(mdl);
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						} catch (Exception e) {
							Notification.show(getPropertyName("Error"),
									Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}

				}
			}
		});

		return pan;
	}

	@Override
	public Boolean isValid() {
		usernameSelect.setComponentError(null);
		boolean flag = true;
		if (usernameSelect.getValue() == null
				|| usernameSelect.getValue().equals("")) {
			setRequiredError(usernameSelect, getPropertyName("invalid_data"),
					true);
			flag = false;
		} else
			usernameSelect.setComponentError(null);

		if (hostnameSelect.getValue() == null
				|| hostnameSelect.getValue().equals("")) {
			setRequiredError(hostnameSelect,
					getPropertyName("invalid_selection"), true);
			flag = false;
		} else
			hostnameSelect.setComponentError(null);

		if (passwordTextField.getValue() == null
				|| passwordTextField.getValue().equals("")) {
			setRequiredError(passwordTextField,
					getPropertyName("invalid_data"), true);
			flag = false;
		} else
			passwordTextField.setComponentError(null);

		try {
			if (toInt(maximumNoTextField.getValue()) < 1
					|| toInt(maximumNoTextField.getValue()) > 20) {
				setRequiredError(maximumNoTextField,
						getPropertyName("invalid_data_entry"), true);
				flag = false;
			}
		} catch (Exception e) {
			setRequiredError(maximumNoTextField,
					getPropertyName("invalid_data"), true);
			flag = false;
			// TODO: handle exception
		}

		return flag;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
