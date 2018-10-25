package com.inventory.config.stock.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.STextField;
import com.webspark.dao.StatusDao;

public class ItemSubGroupPanel extends SContainerPanel {

	private static final long serialVersionUID = 7142160055031212416L;

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField subGroupListCombo;
	STextField subGroupNameTextField;
	STextField codeTextField;
	SComboField groupsCombo;
	SComboField statusCombo;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	ItemSubGroupDao objDao = new ItemSubGroupDao();
	ItemGroupDao gpDao = new ItemGroupDao();

	boolean taxEnable = isTaxEnable();

	SButton newSaleButton;

	SettingsValuePojo settings;

	WrappedSession session;

	SFileUpload fileUpload;
	SFileUploder uploader;
	Image image;
	SButton removeImageButton;

	SimpleDateFormat df;

	@SuppressWarnings("serial")
	public ItemSubGroupPanel() {

		taxEnable = isTaxEnable();

		df = new SimpleDateFormat("ddMMyyyyHHmmssSSS");

		setSize(800, 350);
		setId("Item Sub Group");
		objDao = new ItemSubGroupDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Item Subgroup");

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		try {

			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			save = new SButton(getPropertyName("Save"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			list = objDao.getAllItemSubGroupsNames(getOrganizationID());
			ItemSubGroupModel og = new ItemSubGroupModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			subGroupListCombo = new SComboField(null, 300, list, "id", "name");
			subGroupListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("ItemSubGroupModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			if (statusCombo.getItemIds() != null)
				statusCombo
						.setValue(statusCombo.getItemIds().iterator().next());

			subGroupNameTextField = new STextField(
					getPropertyName("item_sub_grp_name"), 300);
			codeTextField = new STextField(getPropertyName("code"), 300);

			groupsCombo = new SComboField(getPropertyName("item_grp"), 300,
					gpDao.getAllItemGroupsNames(getOrganizationID()), "id",
					"name");
			groupsCombo
					.setInputPrompt(getPropertyName("select"));

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("item_sub_grp"));
			salLisrLay.addComponent(subGroupListCombo);
			salLisrLay.addComponent(newSaleButton);

			form.addComponent(salLisrLay);
			form.addComponent(subGroupNameTextField);
			form.addComponent(codeTextField);
			form.addComponent(groupsCombo);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			String fil = "SubGroupImages/noImage.png";
			image = new SImage(null, new ThemeResource(fil));
			image.setStyleName("user_photo");
			image.setWidth("180");
			image.setHeight("150");
			image.setImmediate(true);

			uploader = new SFileUploder();
			fileUpload = new SFileUpload(null, uploader);
			fileUpload.setButtonCaption(getPropertyName("upload_img"));
			fileUpload.setImmediate(true);
			removeImageButton = new SButton(getPropertyName("remove"));

			SHorizontalLayout uploaderLay = new SHorizontalLayout();
			uploaderLay.setSpacing(true);
			SFormLayout imageMainLayout = new SFormLayout();
			imageMainLayout.setSpacing(true);

			uploaderLay.addComponent(fileUpload);
			uploaderLay.addComponent(removeImageButton);

			imageMainLayout.addComponent(image);
			imageMainLayout.addComponent(uploaderLay);

			hLayout.addComponent(imageMainLayout);

			setContent(hLayout);

			if (settings.isAUTO_CREATE_SUBGROUP_CODE()) {

				subGroupNameTextField.setImmediate(true);
				subGroupNameTextField
						.addValueChangeListener(new ValueChangeListener() {

							@Override
							public void valueChange(ValueChangeEvent event) {
								createCode();
							}
						});
				groupsCombo.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChange(ValueChangeEvent event) {
						createCode();
					}
				});

			}
			
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
			

			fileUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					if (uploader.getFile() != null) {

						image.setSource(new FileResource(uploader.getFile()));
						image.markAsDirty();

					}
				}
			});

			removeImageButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (uploader.getFile() != null) {
						uploader.deleteFile();
					}

					if (subGroupListCombo.getValue() != null
							&& !subGroupListCombo.getValue().equals("")) {
						deleteImage((Long) subGroupListCombo.getValue());
					}

				}
			});

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					subGroupListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (subGroupListCombo.getValue() == null
								|| subGroupListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {

								String fileName = "";
								if (uploader.getFile() != null) {
									fileName = getFileName();
								}

								ItemSubGroupModel objModel = new ItemSubGroupModel();
								objModel.setName(subGroupNameTextField
										.getValue());
								objModel.setCode(codeTextField.getValue());
								objModel.setGroup(new ItemGroupModel(
										(Long) groupsCombo.getValue()));
								objModel.setIcon(fileName);
								objModel.setItemParentId(0);

								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									id = objDao.save(objModel);

									if (uploader.getFile() != null) {
										String dir = VaadinServlet.getCurrent()
												.getServletContext()
												.getRealPath("/")
												+ "VAADIN/themes/testappstheme/SubGroupImages/";

										saveImageAsPNG(uploader.getFile(), dir
												+ fileName);
									}

									loadOptions(id);
									Notification.show(
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			subGroupListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if(uploader.getFile()!=null)
									uploader.deleteFile();
								if (subGroupListCombo.getValue() != null
										&& !subGroupListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									ItemSubGroupModel objModel = objDao
											.getItemSubGroup((Long) subGroupListCombo
													.getValue());
									subGroupNameTextField.setValue(objModel
											.getName());
									codeTextField.setValue(objModel.getCode());
									groupsCombo.setValue(objModel.getGroup()
											.getId());

									statusCombo.setValue(objModel.getStatus());

									String file = objDao
											.getIconName((Long) subGroupListCombo
													.getValue());
									if (file == null
											|| file.trim().length() <= 0)
										file = "noImage.png";
									String str = "SubGroupImages/" + file;
									image.setSource(new ThemeResource(str));


								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									subGroupNameTextField.setValue("");
									codeTextField.setValue("");
									groupsCombo.setValue(null);
									statusCombo.setValue(null);
									image.setSource(new ThemeResource(
											"SubGroupImages/noImage.png"));
									image.markAsDirty();


									if (statusCombo.getItemIds() != null)
										statusCombo
												.setValue(statusCombo
														.getItemIds()
														.iterator().next());

								}
								removeErrorMsgs();

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			groupsCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {

								if (groupsCombo.getValue() != null) {
									ItemGroupModel gp = gpDao
											.getItemGroup((Long) groupsCombo
													.getValue());

								}

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});


			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI().getCurrent(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) subGroupListCombo
														.getValue();
												String fileName = objDao.getIconName(id);
												
												objDao.delete(id);

												String file = VaadinServlet.getCurrent().getServletContext()
														.getRealPath("/")
														+ "VAADIN/themes/testappstheme/SubGroupImages/" + fileName;

												File f = new File(file);
												if (f.exists()&&!f.isDirectory()) {
													f.delete();
												}
												image.setSource(new ThemeResource("SubGroupImages/" + fileName));

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("Error"),

														Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						System.out.println("Option :"
								+ subGroupListCombo.getValue());
						if (subGroupListCombo.getValue() != null) {

							if (isValid()) {

								ItemSubGroupModel objModel = objDao
										.getItemSubGroup((Long) subGroupListCombo
												.getValue());

								String fileName = "";
								if (uploader.getFile() != null) {
									fileName = getFileName();
									deleteImage(objModel.getId());
								}

								objModel.setName(subGroupNameTextField
										.getValue());
								objModel.setCode(codeTextField.getValue());
								objModel.setGroup(new ItemGroupModel(
										(Long) groupsCombo.getValue()));
								objModel.setIcon(fileName);

								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									objDao.update(objModel);

									if (uploader.getFile() != null) {

										String dir = VaadinServlet.getCurrent()
												.getServletContext()
												.getRealPath("/")
												+ "VAADIN/themes/testappstheme/SubGroupImages/";
										saveImageAsPNG(uploader.getFile(), dir
												+ fileName);
									}
									Notification.show(getPropertyName("updated_success"),Type.WARNING_MESSAGE);
									loadOptions(objModel.getId());
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),

									Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void createCode() {

		String groupCode = "";
		if (groupsCombo.getValue() != null
				&& !groupsCombo.getValue().equals("")) {
			try {
				ItemGroupModel groupMdl = gpDao.getItemGroup(toLong(groupsCombo
						.getValue().toString()));
				groupCode = "[" + groupMdl.getCode() + "]";
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		codeTextField.setValue(subGroupNameTextField.getValue() + groupCode);

	}


	public void reloadGroup() {
		try {
			if (groupsCombo.isReadOnly()) {
				Object obj = groupsCombo.getValue();
				groupsCombo.setReadOnly(false);
				list = gpDao.getAllActiveItemGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupsCombo.setContainerDataSource(bic);
				groupsCombo.setItemCaptionPropertyId("name");
				groupsCombo.setValue(obj);
				groupsCombo.setReadOnly(true);
			} else {
				list = gpDao.getAllActiveItemGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupsCombo.setContainerDataSource(bic);
				groupsCombo.setItemCaptionPropertyId("name");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllItemSubGroupsNames(getOrganizationID());

			ItemSubGroupModel sop = new ItemSubGroupModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			subGroupListCombo.setContainerDataSource(bic);
			subGroupListCombo.setItemCaptionPropertyId("name");

			subGroupListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeErrorMsgs() {
		statusCombo.setComponentError(null);
		codeTextField.setComponentError(null);
		groupsCombo.setComponentError(null);
		subGroupNameTextField.setComponentError(null);

	}

	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (groupsCombo.getValue() == null || groupsCombo.getValue().equals("")) {
			setRequiredError(groupsCombo, getPropertyName("invalid_selection"),
					true);
			groupsCombo.focus();
			ret = false;
		} else
			setRequiredError(groupsCombo, null, false);

		if (codeTextField.getValue() == null
				|| codeTextField.getValue().equals("")) {
			setRequiredError(codeTextField, getPropertyName("invalid_data"),
					true);
			codeTextField.focus();
			ret = false;
		} else
			setRequiredError(codeTextField, null, false);

		if (subGroupNameTextField.getValue() == null
				|| subGroupNameTextField.getValue().equals("")) {
			setRequiredError(subGroupNameTextField,
					getPropertyName("invalid_data"), true);
			subGroupNameTextField.focus();
			ret = false;
		} else
			setRequiredError(subGroupNameTextField, null, false);

		return ret;
	}

	public void saveImageAsPNG(File file, String fileName) {

		BufferedImage bufferedImage;

		try {

			if(file!=null&&file.exists()){
			bufferedImage = ImageIO.read(file);
			float width = bufferedImage.getWidth(), height = bufferedImage
					.getHeight();
			if (bufferedImage.getWidth() > 200) {
				float div = width / 200;
				width = 200;
				if (div > 1)
					height = height / div;
			}

			BufferedImage newBufferedImage = new BufferedImage((int) width,
					(int) height, BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					(int) width, (int) height, Color.WHITE, null);

			ImageIO.write(newBufferedImage, "png", new File(fileName));
			}
		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	private void deleteImage(long itmId) {

		try {

			String fileName = objDao.getIconName(itmId);

			String file = VaadinServlet.getCurrent().getServletContext()
					.getRealPath("/")
					+ "VAADIN/themes/testappstheme/SubGroupImages/" + fileName;

			File f = new File(file);
			if (f.exists()&&!f.isDirectory()) {
				f.delete();
			}
			image.setSource(new ThemeResource("SubGroupImages/" + fileName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getFileName() {

		Calendar cal = Calendar.getInstance();

		String fileName = String.valueOf(df.format(cal.getTime())).trim()
				+ ".png";

		if (fileName.length() > 300) {
			fileName = fileName.substring((fileName.length() - 299));
		}
		return fileName;
	}

}
