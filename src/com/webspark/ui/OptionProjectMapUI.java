package com.webspark.ui;

import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.OptionProjectMapDao;
import com.webspark.model.S_OptionModel;
import com.webspark.model.S_ProjectOptionMapModel;
import com.webspark.model.S_ProjectTypeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date Feb 13, 2014
 */
public class OptionProjectMapUI extends SparkLogic {

	private static final long serialVersionUID = 5863690371948089832L;

	private STable table;

	private SButton saveButton;

	private static final String TBL_NO = "#";
	private static final String TBL_OPTION_ID = "Id";
	private static final String TBL_OPTION_NAME = "Option";
	private static final String TBL_CLASS = "Class Name";
	private static final String TBL_PROJECT_ID = "Project ID";

	private Object[] allHeaders;
	private Object[] reqHeaders;

	private OptionProjectMapDao dao;

	private SComboField projectTypeComboField;
	private SComboField optionComboField;
	STextField classTextField;

	@Override
	public SPanel getGUI() {

		setSize(680, 470);

		dao = new OptionProjectMapDao();

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SGridLayout dateLayout = new SGridLayout();
		dateLayout.setSpacing(true);
		dateLayout.setColumns(9);
		dateLayout.setRows(1);

		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);

		try {

			classTextField = new STextField(null, 200);

			projectTypeComboField = new SComboField(null, 150,
					dao.getAllProjectTypes(), "id", "name", true,
					"---- Select -----");

			optionComboField = new SComboField(null, 150, dao.getAllOptions(),
					"option_id", "option_name", true, "---- Select -----");

			allHeaders = new Object[] { TBL_NO, TBL_OPTION_ID, TBL_OPTION_NAME,
					TBL_CLASS, TBL_PROJECT_ID };
			reqHeaders = new Object[] { TBL_NO, TBL_OPTION_NAME, TBL_CLASS };

			table = new STable(null, 600, 300);
			table.addContainerProperty(TBL_NO, Integer.class, null, TBL_NO,
					null, Align.CENTER);
			table.addContainerProperty(TBL_OPTION_ID, Long.class, null,
					TBL_OPTION_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_OPTION_NAME, String.class, null,
					getPropertyName("option"), null, Align.LEFT);
			table.addContainerProperty(TBL_CLASS, String.class, null,
					getPropertyName("class_name"), null, Align.LEFT);
			table.addContainerProperty(TBL_PROJECT_ID, Long.class, null,
					TBL_PROJECT_ID, null, Align.LEFT);

			saveButton = new SButton(getPropertyName("Save"));
			table.setSelectable(true);

			dateLayout.addComponent(
					new SLabel(getPropertyName("project_type")), 1, 0);
			dateLayout.addComponent(projectTypeComboField, 2, 0);

			layout.addComponent(dateLayout);
			layout.addComponent(table);
			layout.addComponent(new SHorizontalLayout(true, new SLabel(null,
					getPropertyName("option")), optionComboField, new SLabel(
					null, getPropertyName("class_name")), classTextField,
					saveButton));

			pan.setContent(layout);

			table.setVisibleColumns(reqHeaders);

			projectTypeComboField
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								loadOptionMaps();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			optionComboField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (optionComboField.getValue() != null)
							classTextField.setValue(dao
									.getOptionsClass((Long) optionComboField
											.getValue()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			table.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					// if(deleteItemButton.isVisible())
					// deleteItemButton.click();
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {

					if (table.getValue() != null) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												dao.delete((Long) table
														.getValue());

												Object prjct = projectTypeComboField
														.getValue();
												projectTypeComboField
														.setValue(null);
												projectTypeComboField
														.setValue(prjct);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});

					}

				}

			});

			saveButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.setComponentError(null);

						if (isValid()) {

							Item item = null;
							boolean isExist = false;
							Iterator itr = table.getItemIds().iterator();
							while (itr.hasNext()) {
								item = table.getItem(itr.next());

								System.out.println(item.getItemProperty(
										TBL_PROJECT_ID).getValue());
								System.out.println(projectTypeComboField
										.getValue());
								System.out.println(item.getItemProperty(
										TBL_OPTION_ID).getValue());
								System.out.println(optionComboField.getValue());

								if (item.getItemProperty(TBL_PROJECT_ID)
										.getValue()
										.toString()
										.equals(projectTypeComboField
												.getValue().toString())
										&& item.getItemProperty(TBL_OPTION_ID)
												.getValue()
												.toString()
												.equals(optionComboField
														.getValue().toString())) {
									isExist = true;
								}
							}

							if (!isExist) {

								S_ProjectOptionMapModel obj = new S_ProjectOptionMapModel();
								obj.setClass_name(classTextField.getValue());
								obj.setOption(new S_OptionModel(
										(Long) optionComboField.getValue()));
								obj.setProject_type(new S_ProjectTypeModel(
										(Long) projectTypeComboField.getValue()));
								dao.save(obj);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								Object prjct = projectTypeComboField.getValue();
								projectTypeComboField.setValue(null);
								projectTypeComboField.setValue(prjct);

								setRequiredError(table, null, false);
							} else {
								setRequiredError(table,
										getPropertyName("invalid_data"), true);
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("issue_occured"),
								Type.ERROR_MESSAGE);
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pan;
	}

	private void loadOptionMaps() {
		Object[] rows = null;
		int index = 1;

		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			S_ProjectOptionMapModel mdl;
			List list = dao
					.getAllProjectOptionMaps((Long) projectTypeComboField
							.getValue());
			for (int i = 0; i < list.size(); i++) {
				mdl = (S_ProjectOptionMapModel) list.get(i);

				rows = new Object[] { index, mdl.getOption().getOption_id(),
						mdl.getOption().getOption_name(), mdl.getClass_name(),
						mdl.getProject_type().getId() };
				table.addItem(rows, mdl.getId());
				index++;
			}

			table.setVisibleColumns(reqHeaders);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (projectTypeComboField.getValue() == null
				|| projectTypeComboField.getValue().equals("")) {
			setRequiredError(projectTypeComboField,
					getPropertyName("invalid_selection"), true);
			projectTypeComboField.focus();
			ret = false;
		} else
			setRequiredError(projectTypeComboField, null, false);

		if (optionComboField.getValue() == null
				|| optionComboField.getValue().equals("")) {
			setRequiredError(optionComboField,
					getPropertyName("invalid_selection"), true);
			optionComboField.focus();
			ret = false;
		} else
			setRequiredError(optionComboField, null, false);

		if (classTextField.getValue() == null
				|| classTextField.getValue().equals("")) {
			setRequiredError(classTextField, getPropertyName("invalid_data"),
					true);
			classTextField.focus();
			ret = false;
		} else
			setRequiredError(classTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
