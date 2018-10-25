package com.webspark.uac.ui;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SEncryption;
import com.webspark.uac.dao.ResetPasswordDao;

public class LoginResetPasswordUI extends SparkLogic {

	/**
	 * @param args
	 */
	ResetPasswordDao dbOprtn;

	SComboField cmbselectlabel;
	long id = 0;
	SCollectionContainer bic;

	public LoginResetPasswordUI() {
		super();
		setCaption("Reset Password");
		// TODO Auto-generated constructor stub
	}

	SPasswordField txtPassword;
	SPasswordField txtConformPasswrd;

	@Override
	public SPanel getGUI() {

		setWidth("480");
		setHeight("250px");

		// TODO Auto-generated method stub
		List testList = null;
		dbOprtn = new ResetPasswordDao();
		txtPassword = new SPasswordField(null, 300);
		txtConformPasswrd = new SPasswordField(null, 300);
		final SLabel lblselect = new SLabel(getPropertyName("login_name"));
		final SLabel lblPass = new SLabel(getPropertyName("new_password"));
		final SLabel lblConformPass = new SLabel(
				getPropertyName("confirm_password"));
		final SButton reset = new SButton(getPropertyName("reset"));

		try {
			testList = dbOprtn.getlabels();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		cmbselectlabel = new SComboField(null, 300, testList, "id", "name");
		cmbselectlabel
				.setInputPrompt(getPropertyName("select"));

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SGridLayout gd = new SGridLayout();
		gd.setColumnExpandRatio(0, 0);
		gd.setColumnExpandRatio(1, 0);
		gd.setColumnExpandRatio(2, 5);
		gd.setRows(10);
		gd.setColumns(5);
		gd.setSpacing(isEnabled());
		gd.setStyleName("label");
		gd.setMargin(true);

		gd.addComponent(lblselect, 0, 5);
		gd.setComponentAlignment(lblselect, Alignment.MIDDLE_RIGHT);
		gd.addComponent(lblPass, 0, 6);
		gd.setComponentAlignment(lblPass, Alignment.MIDDLE_RIGHT);
		gd.addComponent(lblConformPass, 0, 7);
		gd.setComponentAlignment(lblConformPass, Alignment.MIDDLE_RIGHT);
		gd.addComponent(cmbselectlabel, 2, 5);

		gd.addComponent(txtPassword, 2, 6);
		gd.addComponent(txtConformPasswrd, 2, 7);
		gd.addComponent(reset, 2, 9);

		pan.setContent(gd);
		loadOptions(0);

		reset.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				if (cmbselectlabel.getValue() != null) {
					if (isValid()) {

						id = Long.parseLong(cmbselectlabel.getValue()
								.toString());

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {

												dbOprtn.resetPaswrd(
														id,
														SEncryption
																.encrypt(txtPassword
																		.getValue()));
												Notification
														.show(getPropertyName("save_success"),
																Type.WARNING_MESSAGE);

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}

											// Confirmed to continue
											// DO STUFF
										} else {
											// User did not confirm
											// CANCEL STUFF
										}
									}
								});

					}

				}
			}
		});

		return pan;
	}

	private void loadOptions(long id) {
		// TODO Auto-generated method stub
		List testList;
		try {
			testList = dbOprtn.getlabels();

			cmbselectlabel
					.setInputPrompt("------------------- Select user------------------");

			bic = SCollectionContainer.setList(testList, "id");
			cmbselectlabel.setContainerDataSource(bic);
			cmbselectlabel.setItemCaptionPropertyId("login_name");

			cmbselectlabel.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		if (ret) {
			if (!txtPassword.getValue().equals(txtConformPasswrd.getValue())) {
				setRequiredError(txtConformPasswrd,
						getPropertyName("invalid_data"), true);
				ret = false;
				txtConformPasswrd.focus();
			} else
				setRequiredError(txtConformPasswrd, null, false);
		}
		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
