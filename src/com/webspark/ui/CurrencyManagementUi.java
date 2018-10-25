package com.webspark.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.SSlider;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;

public class CurrencyManagementUi extends SparkLogic {

	private static final long serialVersionUID = -4923367832129623858L;

	CurrencyManagementDao dbOprtn;
	SComboField cmbselectlabel;
	long id = 0;

	WrappedSession session;
	SCheckBox chckMaster;

	STextField txtname;
	STextField txtcode;
	STextField txtsymbol;
	STextField integerPartField;
	STextField fractionalPartField;

	SSlider noofPricisionSlider;

	/**
	 * @param args
	 */

	public CurrencyManagementUi() {
		setCaption("Currency Management");
	}

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// session variable
		session = getHttpSession();
		setSizeFull();
		setSize(520, 430);
		// TODO Auto-generated method stub
		dbOprtn = new CurrencyManagementDao();

		txtname = new STextField(getPropertyName("currency_name"), 300);
		txtcode = new STextField(getPropertyName("currency_code"), 300);
		txtsymbol = new STextField(getPropertyName("currency_symbol"), 300);
		final STextField txtBase = new STextField(getPropertyName("current_mastr_currency"), 300);
//		txtsymbol.setMaxLength(2);

		List testList = null;

		final SButton save = new SButton(getPropertyName("Save"));
		final SButton edit = new SButton(getPropertyName("Edit"));
		final SButton delete = new SButton(getPropertyName("Delete"));
		final SButton update = new SButton(getPropertyName("Update"));
		final SButton cancel = new SButton(getPropertyName("Cancel"));

		final SButton set = new SButton(getPropertyName("set_as_master"));

		try {
			testList = dbOprtn.getlabels();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		noofPricisionSlider = new SSlider(getPropertyName("no_of_precisions"), 0, 10, 200);


		CurrencyModel sop = new CurrencyModel();
		sop.setId(0);
		sop.setName("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, sop);
		chckMaster = new SCheckBox(getPropertyName("set_as_master"));
		cmbselectlabel = new SComboField(null, 300, testList, "id", "name");
		cmbselectlabel
				.setInputPrompt(getPropertyName("create_new"));

		integerPartField = new STextField(getPropertyName("int_part_words"), 200);
		fractionalPartField = new STextField(getPropertyName("fractional_part"), 200);

		SPanel pan = new SPanel();
		// an.setWidth("500px");
		// pan.setHeight("400px");
		pan.setSizeFull();

		SFormLayout gd = new SFormLayout();
		gd.setSpacing(true);
		gd.setSizeFull();
		gd.addComponent(txtBase);

		SHorizontalLayout salLisrLay = new SHorizontalLayout(getPropertyName("currency"));
		salLisrLay.addComponent(cmbselectlabel);
		salLisrLay.addComponent(createNewButton);
		gd.addComponent(salLisrLay);

		gd.addComponent(txtname);
		// gd.setComponentAlignment(txtlblname, Alignment.MIDDLE_CENTER);

		gd.addComponent(txtcode);
		// gd.setComponentAlignment(txtdescription, Alignment.MIDDLE_LEFT);

		gd.addComponent(txtsymbol);
		// gd.addComponent(chckMaster, 2, 10);

		gd.addComponent(integerPartField);
		gd.addComponent(fractionalPartField);

		gd.addComponent(noofPricisionSlider);

		SGridLayout gdnew = new SGridLayout();

		gdnew.setRows(3);
		gdnew.setColumns(10);
		gdnew.setSpacing(isEnabled());
		gdnew.addComponent(save, 1, 2);
		gdnew.setComponentAlignment(save, Alignment.MIDDLE_RIGHT);
		gdnew.addComponent(edit, 3, 2);
		gdnew.setComponentAlignment(edit, Alignment.MIDDLE_RIGHT);
		gdnew.addComponent(update, 4, 2);
		gdnew.setComponentAlignment(update, Alignment.MIDDLE_RIGHT);

		gdnew.addComponent(delete, 7, 2);
		gdnew.setComponentAlignment(delete, Alignment.MIDDLE_RIGHT);
		gdnew.addComponent(cancel, 9, 2);
		gdnew.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
		gdnew.addComponent(set, 5, 2);
		gdnew.setComponentAlignment(set, Alignment.MIDDLE_RIGHT);

		gd.addComponent(gdnew);
		gd.setMargin(true);

		// gd.setComponentAlignment(delete, Alignment.MIDDLE_CENTER);
		pan.setContent(gd);

		save.setVisible(true);
		delete.setVisible(false);
		cancel.setVisible(false);
		update.setVisible(false);
		set.setVisible(false);
		edit.setVisible(false);

		try {
			String list = dbOprtn.getCurrency(Long.parseLong(session
					.getAttribute("currency_id").toString()));
			txtBase.setValue(list);
			txtBase.setReadOnly(true);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				cmbselectlabel.setValue((long) 0);
			}
		});

		pan.addShortcutListener(new ShortcutListener("Save Item",
				ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (save.isVisible()) {
					save.click();
				} else if (edit.isVisible()) {
					edit.click();
				} else if (update.isVisible()) {
					update.click();
				}
			}
		});
		// adding currency

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					CurrencyModel lm = new CurrencyModel();
					lm.setName(txtname.getValue());
					lm.setCode(txtcode.getValue());
					lm.setNo_of_precisions(noofPricisionSlider.getValue()
							.intValue());
					lm.setSymbol(txtsymbol.getValue());
					lm.setInteger_part(integerPartField.getValue().toString());
					lm.setFractional_part(fractionalPartField.getValue()
							.toString());

					// checkbox true for setting as master currency
					if (chckMaster.getValue() == true) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {

												dbOprtn.setCurrency(
														id,
														Long.parseLong(getHttpSession()
																.getAttribute(
																		"office_id")
																.toString()));

												session.setAttribute(
														"currency_id", id);

												// dbOprtn.delete(id);

												Notification
														.show(getPropertyName("currency_save_msg"),
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

					try {
						id = dbOprtn.addOption(lm);
						loadOptions(id);
						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						if (chckMaster.getValue() == (false)) {
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}

				}
			}

		});

		// deleting currency details

		delete.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											id = Long.parseLong(cmbselectlabel
													.getValue().toString());
											dbOprtn.delete(id);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);
											loadOptions(0);

											save.setVisible(true);
											edit.setVisible(false);
											delete.setVisible(false);
											update.setVisible(false);
											cancel.setVisible(false);

											txtcode.setValue("");
											txtname.setValue("");
											txtsymbol.setValue("");
											integerPartField.setValue("");
											fractionalPartField.setValue("");

										} catch (Exception e) {
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}

										// Confirmed to continue
										// DO STUFF
									} else {
										// User did not confirm
										// CANCEL STUFF
										save.setVisible(false);
										edit.setVisible(true);
										delete.setVisible(true);
										update.setVisible(false);
										cancel.setVisible(false);
										chckMaster.setEnabled(false);
									}
								}
							});

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// canceling delete option

		cancel.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				setReadonlyAll();
				save.setVisible(false);
				delete.setVisible(true);
				edit.setVisible(true);
				update.setVisible(false);
				cancel.setVisible(false);
				set.setVisible(false);
				chckMaster.setEnabled(false);

			}

		});

		// setting as master currency

		// set.addClickListener(new Button.ClickListener(){
		// public void buttonClick(ClickEvent event){
		//
		// id=Long.parseLong(cmbselectlabel.getValue().toString());
		//
		// //
		// System.out.println("office currency: "+getHttpSession().getAttribute("currency_id").toString());
		// // System.out.println("current currency: "+id);
		// // //if(id ==
		// Long.parseLong(getHttpSession().getAttribute("currency_id").toString())){
		// // Notification.show("This currency is already master currency!",
		// // Type.WARNING_MESSAGE);
		//
		//
		// ConfirmDialog.show(getUI(), "Are you sure?",
		// new ConfirmDialog.Listener() {
		// public void onClose(ConfirmDialog dialog) {
		// if (dialog.isConfirmed()) {
		//
		// try {
		//
		//
		// System.out.println("officeid"+getHttpSession().getAttribute("office_id").toString());
		// dbOprtn.setCurrency(id,Long.parseLong(getHttpSession().getAttribute("office_id").toString()));
		//
		// session.setAttribute("currency_id",id);
		//
		//
		//
		// //dbOprtn.delete(id);
		//
		// Notification.show("Currency set as master Successfully",
		// Type.WARNING_MESSAGE);
		//
		//
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// Notification.show("Error",
		// "Issue occured on Setting this currency as master.!",
		// Type.WARNING_MESSAGE);
		// e.printStackTrace();
		// }
		//
		//
		// // Confirmed to continue
		// // DO STUFF
		// } else {
		// // User did not confirm
		// // CANCEL STUFF
		// }
		// }
		// });
		//
		//
		// }
		//
		// });

		// combobox item change

		cmbselectlabel.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				try {
					clearErrors();
					if (cmbselectlabel.getValue() != null
							&& !cmbselectlabel.getValue().toString()
									.equalsIgnoreCase("0")) {

						chckMaster.setEnabled(false);

						CurrencyModel lmd = dbOprtn.getselecteditem(Long
								.parseLong(cmbselectlabel.getValue().toString()));

						setWritableAll();
						txtname.setValue(lmd.getName());
						txtcode.setValue(lmd.getCode());
						txtsymbol.setValue(lmd.getSymbol());
						integerPartField.setValue(lmd.getInteger_part());
						fractionalPartField.setValue(lmd.getFractional_part());

						noofPricisionSlider.setValue((double) lmd
								.getNo_of_precisions());

						setReadonlyAll();

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

					} else {

						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						txtname.setValue("");
						txtcode.setValue("");
						txtsymbol.setValue("");
						integerPartField.setValue("");
						fractionalPartField.setValue("");
						// setReadonlyAll();
						chckMaster.setEnabled(false);

					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		edit.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				delete.setVisible(false);
				save.setVisible(false);
				update.setVisible(true);
				cancel.setVisible(true);
				edit.setVisible(false);
				set.setVisible(false);
				setWritableAll();
				chckMaster.setEnabled(false);
			}
		});

		// updating details

		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				if (isValid()) {

					try {
						if (cmbselectlabel.getValue() != null) {

							CurrencyModel op = dbOprtn.getselecteditem(Long
									.parseLong(cmbselectlabel.getValue()
											.toString()));

							op.setName(txtname.getValue());
							op.setCode(txtcode.getValue());
							op.setSymbol(txtsymbol.getValue());
							op.setInteger_part(integerPartField.getValue()
									.toString());
							op.setFractional_part(fractionalPartField
									.getValue().toString());

							op.setNo_of_precisions(noofPricisionSlider
									.getValue().intValue());

							// op.setDescription(description.getValue());

							// op.setGroup(new
							// S_OptionGroupModel((Long)option_group.getValue()));

							try {
								dbOprtn.Update(op);
								loadOptions(op.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					delete.setVisible(true);
					save.setVisible(false);
					cancel.setVisible(false);
					edit.setVisible(true);
					update.setVisible(false);
					set.setVisible(false);
					chckMaster.setEnabled(false);
					setReadonlyAll();
				}
			}

		});

		addShortcutListener(new ShortcutListener("Add New Purchase",
				ShortcutAction.KeyCode.N,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
			@Override
			public void handleAction(Object sender, Object target) {
				loadOptions(0);
			}
		});

		return pan;
	}

	private void loadOptions(long id) {
		// TODO Auto-generated method stub
		List testList;
		try {
			testList = dbOprtn.getlabels();

			CurrencyModel sop = new CurrencyModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			cmbselectlabel
					.setInputPrompt("------------------- Create New -------------------");

			CollectionContainer bic = CollectionContainer.fromBeans(testList,
					"id");
			cmbselectlabel.setContainerDataSource(bic);
			cmbselectlabel.setItemCaptionPropertyId("name");

			cmbselectlabel.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setReadonlyAll() {

		txtcode.setReadOnly(true);
		txtname.setReadOnly(true);
		txtsymbol.setReadOnly(true);
		integerPartField.setReadOnly(true);
		fractionalPartField.setReadOnly(true);
	}

	private void setWritableAll() {
		txtcode.setReadOnly(false);
		txtname.setReadOnly(false);
		integerPartField.setReadOnly(false);
		fractionalPartField.setReadOnly(false);
		txtsymbol.setReadOnly(false);
	}

	@Override
	public Boolean isValid() {
		clearErrors();
		boolean valid = true;
		if (txtname.getValue() == null || txtname.getValue().equals("")) {
			setRequiredError(txtname, getPropertyName("invalid_data"), true);
			valid = false;
		}
		if (txtcode.getValue() == null || txtcode.getValue().equals("")) {
			setRequiredError(txtcode, getPropertyName("invalid_data"), true);
			valid = false;
		}
		if (integerPartField.getValue() == null
				|| integerPartField.getValue().equals("")) {
			setRequiredError(integerPartField, getPropertyName("invalid_data"),
					true);
			valid = false;
		}
		if (fractionalPartField.getValue() == null
				|| fractionalPartField.getValue().equals("")) {
			setRequiredError(fractionalPartField,
					getPropertyName("invalid_data"), true);
			valid = false;
		}

		return valid;
	}

	private void clearErrors() {
		txtname.setComponentError(null);
		txtcode.setComponentError(null);
		integerPartField.setComponentError(null);
		fractionalPartField.setComponentError(null);
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
