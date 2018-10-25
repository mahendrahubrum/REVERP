package com.webspark.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Upload;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;

public class TakeBackup extends SparkLogic {

	private SPanel panel;
	private SButton generateButton;
	private SButton downloadButton;
	private String fileName = "";

	private FileResource fileResource;
	private FileDownloader downloader;
	private STextField nameSTextField;
	private Properties properties;

	boolean isWindows;
	boolean isLinux;
	boolean isMac;

	private STabSheet tabSheet;

	private Upload upload;
	private SButton restoreButton;

	@Override
	public SPanel getGUI() {

		setSize(300, 220);

		detectOS();

		properties = new Properties();
		InputStream in = getClass().getResourceAsStream("/settings.properties");

		try {
			properties.load(in);
			in.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		tabSheet = new STabSheet();
		tabSheet.setSizeFull();

		SHorizontalLayout layout = new SHorizontalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		final SFormLayout formLayout = new SFormLayout();
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		panel = new SPanel();
		panel.setSizeFull();

		generateButton = new SButton(getPropertyName("generate"));
		downloadButton = new SButton(getPropertyName("download"));
		downloadButton.setEnabled(false);

		String time = new Date().toString().replace('.', ' ').replace(',', ' ')
				.replace(':', ' ').replaceAll(" ", "");

		nameSTextField = new STextField(getPropertyName("name"), 190);
		nameSTextField.setValue(time);

		formLayout.addComponent(nameSTextField);
		layout.addComponent(generateButton);
		layout.addComponent(downloadButton);
		formLayout.addComponent(layout);
		
		addShortcutListener(new ShortcutListener("Save",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				generateButton.click();
			}
		});

		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				if (isValid()) {

					String realName = nameSTextField.getValue()
							.replace('.', ' ').replace(',', ' ')
							.replace(':', ' ').replace('/', ' ')
							.replace('-', ' ').replace('~', ' ')
							.replace('!', ' ').replace('\\', ' ')
							.replace(';', ' ').trim();

					fileName = "";

					File dir = new File(VaadinServlet.getCurrent()
							.getServletContext().getRealPath("/")
							+ "Backup");

					if (dir != null && dir.isDirectory()) {
						try {

							for (File file : dir.listFiles()) {
								if(!file.isDirectory())
									file.delete();
							}
						} catch (Exception e) {
							Notification.show(
									getPropertyName("no_files_found"),
									Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}

					if (isWindows || isMac) {
						fileName = VaadinServlet.getCurrent()
								.getServletContext().getRealPath("/")
								+ "Backup\\" + realName + ".sql";
					} else {
						fileName = VaadinServlet.getCurrent()
								.getServletContext().getRealPath("/")
								+ "Backup/" + realName + ".sql";
					}
					boolean flag = takeBackup(
							properties.getProperty("database"),
							properties.getProperty("username"),
							properties.getProperty("password"), fileName,
							properties.getProperty("backupscripttype"));
					if (flag) {
						Notification.show(getPropertyName("backup_success"),
								Type.WARNING_MESSAGE);

						fileResource = new FileResource(new File(fileName));
						downloader = new FileDownloader(fileResource);
						if (downloader.getExtensions().size() > 0) {
							downloadButton.removeExtension(downloader);
						}

						// downloader.extend(null);
						downloader.extend(downloadButton);
						downloadButton.setEnabled(true);
						System.out.println("file------------------->        "
								+ fileName);
					} else {
						Notification.show(getPropertyName("backup_failed"),
								Type.ERROR_MESSAGE);
						downloadButton.setEnabled(false);
					}
				}
			}
		});

		// Restore Part

		SHorizontalLayout restorelayout = new SHorizontalLayout();
		restorelayout.setMargin(true);
		restorelayout.setSpacing(true);

		final SFormLayout restoreFormLayout = new SFormLayout();
		restoreFormLayout.setMargin(true);
		restoreFormLayout.setSpacing(true);

		upload = new Upload(getPropertyName("upload"), null);
		upload.setButtonCaption(getPropertyName("upload"));
		restoreButton = new SButton(getPropertyName("restore"));
		restoreButton.setEnabled(false);

		restoreFormLayout.addComponent(upload);
		restorelayout.addComponent(restoreButton);
		restoreFormLayout.addComponent(restorelayout);

		tabSheet.addTab(formLayout, getPropertyName("take_backup"));
		tabSheet.setTabIndex(1);
//		tabSheet.addTab(restoreFormLayout, getPropertyName("restore"));
//		tabSheet.setTabIndex(2);

		tabSheet.addSelectedTabChangeListener(new SelectedTabChangeListener() {

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (event.getSource() == tabSheet.getTab(0)) {
					System.out.println("Tab changed Form Layout"
							+ event.getComponent().getCaption());
				} else if (event.getSource() == tabSheet.getTab(1)) {
					System.out.println("Tab changed Restore Layout");
				}

			}
		});

		panel.setContent(tabSheet);
		return panel;
	}

	public boolean takeBackup(String dbName, String dbUserName,
			String dbPassword, String path, String dbType) {

		String executeCmd = dbType + " -u " + dbUserName + " -p" + dbPassword
				+ " " + dbName + " -r " + path;
		Process runtimeProcess;
		try {
			System.out.println(executeCmd);// this out put works in mysql shell
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			int processComplete = runtimeProcess.waitFor();

			if (processComplete == 0) {
				System.out.println("Backup created successfully");
				return true;
			} else {
				System.out.println("Could not create the backup");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public Boolean isValid() {

		if (nameSTextField.getValue() == null
				|| nameSTextField.getValue().equalsIgnoreCase("")) {
			setRequiredError(nameSTextField, getPropertyName("invalid_data"),
					true);
			return false;
		}
		return true;
	}

	private void detectOS() {
		isWindows = false;
		isLinux = false;
		isMac = false;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			isWindows = true;
		} else if (osName.contains("nix") || osName.contains("nux")) {
			isLinux = true;
		} else if (osName.contains("mac")) {
			isMac = true;
		}
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
