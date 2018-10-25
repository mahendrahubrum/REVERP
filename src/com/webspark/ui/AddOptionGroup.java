package com.webspark.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.ModuleDao;
import com.webspark.dao.OptionGroupDao;
import com.webspark.model.S_OptionGroupModel;
import com.webspark.uac.model.S_ModuleModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddOptionGroup extends SparkLogic {

	long id = 0;

	SCollectionContainer bic;

	final SFormLayout content;

	SComboField optionGroups;
	final STextField option_group_name;
	final SComboField module;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	STextField priority_order;

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	OptionGroupDao ogDao = new OptionGroupDao();

	private SFileUpload fileUpload;
	private SFileUploder fileUploder;

	Image image;

	SHorizontalLayout uplaodLayout;

	SButton createNewButton;

	public AddOptionGroup() throws Exception {

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		setCaption(getPropertyName("option_group"));

		setWidth("520px");
		setHeight("360px");
		content = new SFormLayout();

		uplaodLayout = new SHorizontalLayout();
		uplaodLayout.setCaption(getPropertyName("logo"));

		List testList = ogDao.getAllOptionGroups();
		S_OptionGroupModel og = new S_OptionGroupModel();
		og.setId(0);
		og.setOption_group_name("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, og);

		optionGroups = new SComboField(null, 300, testList, "id",
				"option_group_name");
		optionGroups
				.setInputPrompt(getPropertyName("create_new"));

		option_group_name = new STextField(getPropertyName("option_grp_name"),
				300);

		priority_order = new STextField(getPropertyName("priority_order"), 300);
		priority_order.setValue("1");

		testList = new ModuleDao().getAllModules();

		module = new SComboField(getPropertyName("module"), 300, testList,
				"id", "module_name");
		module.setInputPrompt(getPropertyName("select"));

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("option_group"));
		salLisrLay.addComponent(optionGroups);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(option_group_name);
		content.addComponent(module);
		content.addComponent(priority_order);

		fileUploder = new SFileUploder();
		fileUpload = new SFileUpload(null, fileUploder);
		fileUpload.setImmediate(true);
		fileUpload.setButtonCaption(getPropertyName("upload"));

		uplaodLayout.addComponent(fileUpload);

		uplaodLayout.setComponentAlignment(fileUpload, Alignment.MIDDLE_CENTER);
		uplaodLayout.setSpacing(true);

		content.addComponent(uplaodLayout);

		buttonLayout.addComponent(save);
		buttonLayout.addComponent(edit);
		buttonLayout.addComponent(delete);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(cancel);

		content.addComponent(buttonLayout);

		edit.setVisible(false);
		delete.setVisible(false);
		update.setVisible(false);
		cancel.setVisible(false);
		content.setSizeUndefined();

		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				optionGroups.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (optionGroups.getValue() == null
							|| optionGroups.getValue().toString().equals("0")) {

						if (isValid()) {
							S_OptionGroupModel op = new S_OptionGroupModel();
							op.setOption_group_name(option_group_name
									.getValue());
							op.setModule(new S_ModuleModel((Long) module
									.getValue()));
							op.setPriority_order(toInt(priority_order
									.getValue()));

							try {
								id = ogDao.save(op);
								loadOptions(id);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								if (fileUploder.getFile() != null) {
									String dir = VaadinServlet.getCurrent()
											.getServletContext()
											.getRealPath("/")
											+ "VAADIN/themes/testappstheme/moduleicons/";

									saveImageAsPNG(fileUploder.getFile(), dir
											+ "logo" + id + ".png");

									fileUploder.deleteFile();
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
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

		optionGroups.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (optionGroups.getValue() != null
							&& !optionGroups.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						S_OptionGroupModel opt = ogDao
								.getOptionGroup((Long) optionGroups.getValue());

						setWritableAll();
						option_group_name.setValue(opt.getOption_group_name());
						module.setValue(opt.getModule().getId());

						priority_order.setValue(asString(opt
								.getPriority_order()));

						try {
							uplaodLayout.removeComponent(image);
						} catch (Exception e) {
							// TODO: handle exception
						}

						String fil = "moduleicons/logo" + opt.getId() + ".png";

						image = new Image(null, new ThemeResource(fil));
						image.setStyleName("user_photo");
						image.setVisible(true);

						uplaodLayout.addComponent(image, 1);

						setReadOnlyAll();

					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						try {
							uplaodLayout.removeComponent(image);
						} catch (Exception e) {
							// TODO: handle exception
						}

						setWritableAll();
						option_group_name.setValue("");
						module.setValue(null);
						priority_order.setValue("1");
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
					loadOptions(Long.parseLong(optionGroups.getValue()
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
											id = (Long) optionGroups.getValue();
											ogDao.delete(id);

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

										// Confirmed to continue
										// DO STUFF
									} else {
										// User did not confirm
										// CANCEL STUFF
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
					System.out.println("Option :" + optionGroups.getValue());
					if (optionGroups.getValue() != null) {
						
						if(isValid()){

						S_OptionGroupModel op = ogDao
								.getOptionGroup((Long) optionGroups.getValue());

						op.setOption_group_name(option_group_name.getValue());
						op.setModule(new S_ModuleModel((Long) module.getValue()));
						op.setPriority_order(toInt(priority_order.getValue()));

						try {
							ogDao.update(op);
							loadOptions(op.getId());

							if (fileUploder.getFile() != null) {
								String dir = VaadinServlet.getCurrent()
										.getServletContext().getRealPath("/")
										+ "VAADIN/themes/testappstheme/moduleicons/";

								saveImageAsPNG(fileUploder.getFile(), dir
										+ "logo" + op.getId() + ".png");

								fileUploder.deleteFile();
							}

						} catch (Exception e) {
							Notification.show(
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

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
		option_group_name.setReadOnly(true);
		module.setReadOnly(true);
		priority_order.setReadOnly(true);

		option_group_name.focus();
	}

	public void setWritableAll() {
		option_group_name.setReadOnly(false);
		module.setReadOnly(false);
		priority_order.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = ogDao.getAllOptionGroups();

			S_OptionGroupModel sop = new S_OptionGroupModel();
			sop.setId(0);
			sop.setOption_group_name("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			optionGroups
					.setInputPrompt("------------------- Create New -------------------");

			bic = SCollectionContainer.setList(testList, "id");
			optionGroups.setContainerDataSource(bic);
			optionGroups.setItemCaptionPropertyId("option_group_name");

			optionGroups.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// **********************************************************

	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {

		if (option_group_name.getValue() == null
				|| option_group_name.getValue().equals("")) {
			Notification.show(getPropertyName("invalid_data"),
					getPropertyName("invalid_data"), Type.ERROR_MESSAGE);
			return false;
		}

		if (module.getValue() == null || module.getValue().equals("")) {
			Notification.show(getPropertyName("invalid_selection"),
					getPropertyName("invalid_selection"), Type.ERROR_MESSAGE);
			return false;
		}

		if (priority_order.getValue() == null
				|| priority_order.getValue().equals("")) {
			Notification.show(getPropertyName("invalid_data"),
					"Pease enter a priority Order.!", Type.ERROR_MESSAGE);
			return false;
		} else {
			try {
				Integer.parseInt(priority_order.getValue());
			} catch (Exception e) {
				Notification.show(getPropertyName("invalid_data"),
						getPropertyName("invalid_data"), Type.ERROR_MESSAGE);
				return false;
				// TODO: handle exception
			}
		}

		return true;

	}

	public void saveImageAsPNG(File file, String fileName) {

		BufferedImage bufferedImage;

		try {

			// read image file
			bufferedImage = ImageIO.read(file);
			float width = bufferedImage.getWidth(), height = bufferedImage
					.getHeight();
			if (bufferedImage.getWidth() > 100) {
				float div = width / 100;
				width = 100;
				if (div > 1)
					height = height / div;
			}

			// create a blank, RGB, same width and height, and a white
			// background
			BufferedImage newBufferedImage = new BufferedImage((int) width,
					(int) height, BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					(int) width, (int) height, Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "png", new File(fileName));

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
