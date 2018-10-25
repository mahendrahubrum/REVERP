package com.webspark.ui;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.StatusDao;
import com.webspark.model.S_StatusModel;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class AddStatus extends SparkLogic {

	long id = 0;

	SCollectionContainer bic;
	
	private static String TBC_NAME="Name";
	private static String TBC_MODEL_NAME="Model Name";
	private static String TBC_FILED_NAME="Filed Name";
	private static String TBC_VALUE="Value";

	SFormLayout content;

	STextField status_name;
	STextField field_name;
	STextField model_name;
	STextField value;

	STable statusesTable;

	final SButton addNew = new SButton(getPropertyName("add_new"));
	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	StatusDao stsDao = new StatusDao();

	@SuppressWarnings("deprecation")
	public AddStatus() throws Exception {

		setCaption(getPropertyName("status"));
		setWidth("630px");
		setHeight("380px");

		content = new SFormLayout();

		List testList = stsDao.getAllStatuses();

		statusesTable = new STable(getPropertyName("status"));

		statusesTable.addContainerProperty(TBC_NAME,String.class, null,getPropertyName("name"),null,Align.LEFT);
		statusesTable.addContainerProperty(TBC_MODEL_NAME,String.class, null,getPropertyName("model_name"),null,Align.LEFT);
		statusesTable.addContainerProperty(TBC_FILED_NAME,String.class, null,getPropertyName("field_name"),null,Align.LEFT);
		statusesTable.addContainerProperty(TBC_VALUE,Long.class, null,getPropertyName("value"),null,Align.LEFT);

//		statusesTable.setColumnHeaders(new String[] { TBC_NAME, TBC_MODEL_NAME,
//				TBC_FILED_NAME, TBC_VALUE });

		statusesTable.setSizeFull();
		statusesTable.setSelectable(true);
		statusesTable.setWidth("480");
		statusesTable.setHeight("120");

		for (int i = 0; i < testList.size(); i++) {
			S_StatusModel sop = (S_StatusModel) testList.get(i);
			Object[] row = new Object[] { sop.getName(), sop.getModel_name(),
					sop.getField_name(), sop.getValue() };
			statusesTable.addItem(row, sop.getId());
		}

		status_name = new STextField(getPropertyName("status_name") + " :", 300);
		model_name = new STextField(getPropertyName("model_name") + " :", 300);
		field_name = new STextField(getPropertyName("field_name") + " :", 300);
		value = new STextField(getPropertyName("value") + " :", 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		content.addComponent(statusesTable);
		content.addComponent(status_name);
		content.addComponent(model_name);
		content.addComponent(field_name);
		content.addComponent(value);

		buttonLayout.addComponent(addNew);
		buttonLayout.addComponent(save);
		buttonLayout.addComponent(edit);
		buttonLayout.addComponent(delete);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(cancel);

		content.addComponent(buttonLayout);

		addNew.setVisible(false);
		edit.setVisible(false);
		delete.setVisible(false);
		update.setVisible(false);
		cancel.setVisible(false);
		content.setSizeUndefined();

		setContent(content);

		addNew.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					// addNew.setVisible(false);
					loadOptions(0);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (status_name.getValue() != null
							&& !status_name.getValue().equals("")) {
						S_StatusModel sts = new S_StatusModel();
						sts.setName(status_name.getValue());
						sts.setModel_name(model_name.getValue());
						sts.setField_name(field_name.getValue());
						sts.setValue(Long.parseLong(value.getValue()));

						try {
							id = stsDao.saveStatus(sts);
							loadOptions(id);
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Notification.show(getPropertyName("Error"),
									Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
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

		statusesTable.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {

					if (statusesTable.getValue() != null) {

						save.setVisible(false);
						addNew.setVisible(true);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						S_StatusModel sts = stsDao
								.getStatus((Long) statusesTable.getValue());

						setWritableAll();
						status_name.setValue(sts.getName());
						model_name.setValue(sts.getModel_name());
						field_name.setValue(sts.getField_name());
						value.setValue(sts.getValue() + "");
						setReadOnlyAll();
					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);
						addNew.setVisible(false);

						setWritableAll();
						status_name.setValue("");
						model_name.setValue("");
						field_name.setValue("");
						value.setValue("");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		edit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					addNew.setVisible(false);
					edit.setVisible(false);
					delete.setVisible(false);
					update.setVisible(true);
					cancel.setVisible(true);
					setWritableAll();

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		cancel.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					edit.setVisible(false);
					delete.setVisible(false);
					update.setVisible(false);
					cancel.setVisible(false);
					loadOptions(Long.parseLong(statusesTable.getValue()
							.toString()));

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		delete.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											id = Long.parseLong(statusesTable
													.getValue().toString());
											stsDao.delete(id);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

											loadOptions(0);

										} catch (Exception e) {
											// TODO Auto-generated catch block
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}

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

		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (statusesTable.getValue() != null) {

						S_StatusModel sts = stsDao.getStatus(Long
								.parseLong(statusesTable.getValue().toString()));

						sts.setName(status_name.getValue());
						sts.setModel_name(model_name.getValue());
						sts.setField_name(field_name.getValue());
						sts.setValue(Long.parseLong(value.getValue()));

						try {
							stsDao.Update(sts);
							loadOptions(sts.getId());
						} catch (Exception e) {
							Notification.show(getPropertyName("Error"),
									Type.ERROR_MESSAGE);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

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

		addShortcutListener(new ShortcutListener("Add New Purchase",
				ShortcutAction.KeyCode.N,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
			@Override
			public void handleAction(Object sender, Object target) {
				loadOptions(0);
			}
		});

		addShortcutListener(new ShortcutListener("Save",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (save.isVisible())
					save.click();
				else
					update.click();
			}
		});

	}

	public void setReadOnlyAll() {
		status_name.setReadOnly(true);
		model_name.setReadOnly(true);
		field_name.setReadOnly(true);
		value.setReadOnly(true);

		status_name.focus();
	}

	public void setWritableAll() {
		status_name.setReadOnly(false);
		model_name.setReadOnly(false);
		field_name.setReadOnly(false);
		value.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = stsDao.getAllStatuses();

			statusesTable.removeAllItems();

			S_StatusModel sop;
			Object[] row;
			for (int i = 0; i < testList.size(); i++) {
				sop = (S_StatusModel) testList.get(i);
				row = new Object[] { sop.getName(), sop.getModel_name(),
						sop.getField_name(), sop.getValue() };
				statusesTable.addItem(row, sop.getId());
			}

			statusesTable.setValue(id);

			if (id != 0)
				addNew.setVisible(true);
			else
				addNew.setVisible(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
