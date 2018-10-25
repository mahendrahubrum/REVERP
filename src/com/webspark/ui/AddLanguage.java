package com.webspark.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.LanguageDao;
import com.webspark.model.S_LanguageModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class AddLanguage extends SparkLogic {

	long id = 0;

	final SFormLayout content;
	SComboField languageCombo;
	final STextField languageName;
	final STextField fileName;
	final SButton save = new SButton(getPropertyName("save"));
	final SButton edit = new SButton(getPropertyName("edit"));
	final SButton delete = new SButton(getPropertyName("delete"));
	final SButton update = new SButton(getPropertyName("update"));
	final SButton cancel = new SButton(getPropertyName("cancel"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	LanguageDao dao=new LanguageDao();

	SButton createNewButton;
	
	public AddLanguage() throws Exception {
		setSize(480, 250);

		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));


		languageCombo = new SComboField(null, 300, null, "id", "name");
		languageCombo.setInputPrompt(getPropertyName("create_new"));

		languageName = new STextField(getPropertyName("language_name"), 300);
		languageName.setInputPrompt(getPropertyName("language_name"));
		fileName = new STextField(getPropertyName("property_file_name"), 300);
		fileName.setInputPrompt(getPropertyName("property_file_name"));
		
		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("220px");
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("module"));
		salLisrLay.addComponent(languageCombo);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(languageName);
		content.addComponent(fileName);
		
		buttonLayout.addComponent(save);
		buttonLayout.addComponent(edit);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);
		buttonLayout.addComponent(cancel);
		buttonLayout.setSpacing(true);
		content.addComponent(buttonLayout);
		content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

		edit.setVisible(false);
		delete.setVisible(false);
		update.setVisible(false);
		cancel.setVisible(false);
		content.setSizeUndefined();
		loadOptions(0);
		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				languageCombo.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (languageCombo.getValue() == null || languageCombo.getValue().toString().equals("0")) {

						if (isValid()) {
							S_LanguageModel mdl=new S_LanguageModel();
							mdl.setName(languageName.getValue());
							mdl.setProperty(fileName.getValue());
							id = dao.save(mdl);
							loadOptions(id);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						}
					}
				} 
				catch (Exception e) {
					Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}

		});

		languageCombo.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (languageCombo.getValue() != null && !languageCombo.getValue().toString().equals("0")) {
						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);
						S_LanguageModel mdl = dao.getLanguage((Long)languageCombo.getValue());
						setWritableAll();
						languageName.setValue(mdl.getName());
						fileName.setValue(mdl.getProperty());
						setReadOnlyAll();
					} 
					else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);
						setWritableAll();
						languageName.setValue("");
						fileName.setValue("");
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		edit.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					edit.setVisible(false);
					delete.setVisible(false);
					update.setVisible(true);
					cancel.setVisible(true);
					setWritableAll();
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
					loadOptions(Long.parseLong(languageCombo.getValue().toString()));
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		delete.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								try {
									id = Long.parseLong(languageCombo.getValue().toString());
									dao.delete(id);
									Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
									loadOptions(0);
								} 
								catch (Exception e) {
									Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							} 
						}
					});
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if (languageCombo.getValue() != null) {
						S_LanguageModel mdl = dao.getLanguage((Long)languageCombo.getValue());
						mdl.setName(languageName.getValue());
						mdl.setProperty(fileName.getValue());
						dao.update(mdl);
						loadOptions(mdl.getId());
						Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
					}
				} 
				catch (Exception e) {
					Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}

		});

	}

	public void setReadOnlyAll() {
		languageName.setReadOnly(true);
		fileName.setReadOnly(true);
	}

	public void setWritableAll() {
		languageName.setReadOnly(false);
		fileName.setReadOnly(false);
	}

	@SuppressWarnings("unchecked")
	public void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.add(0, new S_LanguageModel(0, "------------------- Create New -------------------"));
			list.addAll(dao.getAllLanguages());
			languageCombo.setInputPrompt(getPropertyName("create_new"));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			languageCombo.setContainerDataSource(bic);
			languageCombo.setItemCaptionPropertyId("name");
			languageCombo.setValue(id);

		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {
		boolean flag=true;
		languageName.setComponentError(null);
		fileName.setComponentError(null);
		if (languageName.getValue() != null && !languageName.getValue().equals(""))
			flag=true;
		else {
			setRequiredError(languageName,getPropertyName("invalid_data"),true);
			flag=false;
		}
		if (fileName.getValue() != null && !fileName.getValue().equals(""))
			flag=true;
		else {
			setRequiredError(fileName,getPropertyName("invalid_data"),true);
			flag=false;
		}
		return flag;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
