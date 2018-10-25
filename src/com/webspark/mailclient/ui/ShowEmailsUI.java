package com.webspark.mailclient.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.tokenfield.DemoRoot.Contact;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupDateField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.dao.MailDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.mailclient.model.MyMailsModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date 10/03/2014
 */
public class ShowEmailsUI extends SparkLogic {

	private static final long serialVersionUID = -1830134231919939537L;

	private static final String TBL_SINO = "#";
	private static final String TBL_FROM = "From / To";
	private static final String TBL_TITLE = "Title";
	private static final String TBL_DETAILS = "Details";
	private static final String TBL_DATETIME = "Date & Time";
	private static final String TBL_FOLDER = "Folder";

	private STable table;
	String[] reqHeaders;

	private STextField titleField;
	private STextArea detailsField;

	private SPopupDateField datetimeField;
	private WrappedSession session;
	private MailDao daoObj;

	private STextField subjectField;
	private RichTextArea mailArea, detailsTxtField;
	private SFormLayout mailLayout;

	private STokenField toField;
	private SButtonLink attachLink;
	private FileDownloader downloader;
	private SDialogBox dialogBox;
	private SFileUpload fileUpload;
	private SFileUploder uploader;
	private SLabel attachLabel;
	private SButton removeAttachButton;

	private SMail mail;

	SLabel dateTimeLabel;

	SWindow showWindow;
	STextArea fromText, subjectText;

	SFormLayout rplyForm;

	SHorizontalLayout mainHoriz, replyBtnLay;

	UserModel user = null;

	EmailConfigurationModel emainlConfig;

	String checkEmail;

	private SimpleDateFormat df;

	public ShowEmailsUI(String email) {
		setSize(1000, 580);

		checkEmail = email;

		showWindow = new SWindow(null, 1100, 640);
		showWindow.setCloseShortcut(KeyCode.ESCAPE);

		SPanel panel = new SPanel();

		mail = new SMail();

		dateTimeLabel = new SLabel(getPropertyName("date_n_time"));

		df = new SimpleDateFormat("ddMMYYHHmmss");

		session = getHttpSession();
		daoObj = new MailDao();

		mainHoriz = new SHorizontalLayout();

		final SFormLayout personalLayout = new SFormLayout();
		personalLayout.setStyleName("layout_border");

		reqHeaders = new String[] { TBL_SINO, TBL_TITLE, TBL_FOLDER,
				TBL_DETAILS, TBL_DATETIME };

		table = new STable(null, 400, 400);
		table.addContainerProperty(TBL_SINO, Integer.class, null, TBL_SINO,
				null, Align.CENTER);
		table.addContainerProperty(TBL_FROM, String.class, null, TBL_FROM,
				null, Align.CENTER);
		table.addContainerProperty(TBL_TITLE, String.class, null, TBL_TITLE,
				null, Align.LEFT);
		table.addContainerProperty(TBL_DETAILS, String.class, null,
				TBL_DETAILS, null, Align.LEFT);
		table.addContainerProperty(TBL_FOLDER, String.class, null, TBL_FOLDER,
				null, Align.LEFT);
		table.addContainerProperty(TBL_DATETIME, Timestamp.class, null,
				TBL_DATETIME, null, Align.CENTER);
		table.setSelectable(true);
		table.setMultiSelect(true);

		table.setColumnWidth(TBL_SINO, 20);
		table.setColumnWidth(TBL_TITLE, 250);
		table.setColumnWidth(TBL_DETAILS, 200);
		table.setColumnWidth(TBL_FROM, 200);

		table.setColumnWidth(TBL_DATETIME, 130);
		table.setColumnWidth(TBL_FOLDER, 70);

		table.setWidth("950");
		table.setHeight("500");

		try {
			user = new UserManagementDao().getUser(getLoginID());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		dialogBox = new SDialogBox(getPropertyName("new_mail"), 800, 700);
		dialogBox.setModal(true);

		toField = new STokenField(getPropertyName("to")) {
			protected void onTokenInput(Object tokenId) {
				Set<Object> set = (Set<Object>) getValue();
				Contact c = new Contact("", tokenId.toString());
				if (set != null && set.contains(c)) {
					// duplicate
				} else {
					try {
						new InternetAddress(tokenId.toString()).validate();
						addToken(tokenId);
					} catch (Exception e) {
						SNotification.show(getPropertyName("invalid_data"),
								Type.TRAY_NOTIFICATION);
						// TODO: handle exception
					}
				}
			}
		};
		toField.setInputPrompt("Select");
		toField.setStyleName(STokenField.STYLE_TOKENFIELD);
		toField.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);
		toField.setWidth("650");
		toField.setInputWidth("150");

		subjectField = new STextField(getPropertyName("subject"), 600);
		subjectField.setMaxLength(550);
		mailArea = new RichTextArea();
		mailArea.setCaption(null);
		mailArea.setWidth("600px");
		mailArea.setHeight("350px");

		detailsTxtField = new RichTextArea();
		detailsTxtField.setCaption(getPropertyName("details"));
		detailsTxtField.setWidth("980px");
		detailsTxtField.setHeight("400px");

		fromText = new STextArea(getPropertyName("from/to"), 980, 40);
		subjectText = new STextArea(getPropertyName("subject"), 980, 40);

		SHorizontalLayout attachLayout = new SHorizontalLayout();
		attachLayout.setSpacing(true);
		uploader = new SFileUploder();
		fileUpload = new SFileUpload(null, uploader);
		fileUpload.setImmediate(true);
		fileUpload.markAsDirty();
		fileUpload.setButtonCaption(getPropertyName("attach_a_file"));
		attachLabel = new SLabel();
		removeAttachButton = new SButton(getPropertyName("remove_attachment"));

		mailLayout = new SFormLayout();
		mailLayout.setSizeFull();
		mailLayout.setMargin(true);
		mailLayout.setSpacing(true);

		attachLayout.addComponent(fileUpload);
		attachLayout.addComponent(removeAttachButton);
		attachLayout.addComponent(attachLabel);
		mailLayout.addComponent(toField);
		mailLayout.addComponent(subjectField);
		mailLayout.addComponent(mailArea);
		mailLayout.addComponent(attachLayout);

		dialogBox.setContent(mailLayout);

		SLabel subjectLabel = new SLabel(getPropertyName("subject") + " : ");
		SLabel dateLabel = new SLabel(getPropertyName("date") + " : ");
		SLabel contentLabel = new SLabel(getPropertyName("content") + " : ");
		contentLabel.setContentMode(ContentMode.HTML);
		attachLink = new SButtonLink(getPropertyName("download"));
		attachLink.setVisible(false);

		downloader = new FileDownloader(new FileResource(new File("")));
		downloader.extend(attachLink);

		titleField = new STextField(getPropertyName("title"), 200);
		detailsField = new STextArea(getPropertyName("details"), 200);
		datetimeField = new SPopupDateField(getPropertyName("date_n_time"));
		datetimeField.setValue(new Date());

		personalLayout.addComponent(titleField);
		personalLayout.addComponent(detailsField);
		personalLayout.addComponent(datetimeField);

		SHorizontalLayout hor = new SHorizontalLayout();
		hor.addComponent(table);
		hor.setSpacing(true);
		hor.setComponentAlignment(table, Alignment.MIDDLE_RIGHT);

		mainHoriz.addComponent(table);

		setContent(mainHoriz);

		try {
			emainlConfig = new EmailConfigDao()
					.getEmailConfiguration(getLoginID());
			if (emainlConfig != null) {
				loadList();
			} else {
				SNotification.show(getPropertyName("mail_config_msg"),
						Type.ERROR_MESSAGE);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		/*
		 * final Action actionDelete = new Action("Delete");
		 * 
		 * table.addActionHandler(new Action.Handler() {
		 * 
		 * @Override public Action[] getActions(final Object target, final
		 * Object sender) { // if(deleteItemButton.isVisible()) //
		 * deleteItemButton.click(); return new Action[] { actionDelete }; }
		 * 
		 * @Override public void handleAction(final Action action, final Object
		 * sender, final Object target) { deleteItem(); }
		 * 
		 * });
		 */

		fileUpload.addListener(new Listener() {

			@Override
			public void componentEvent(Event event) {
				if (uploader.getFile() != null) {
					attachLabel.setValue(uploader.getFile().getName());
				} else
					attachLabel.setValue("");
			}
		});

		removeAttachButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (uploader.getFile() != null) {
					attachLabel.setValue("");
					uploader.deleteFile();
				}
			}
		});

		table.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearError();
				if (table.getValue() != null && !table.getValue().equals("")) {
					try {

						Collection selectedItems = (Collection) table
								.getValue();

						if (selectedItems.size() == 1) {

							Item item = table.getItem(selectedItems.iterator()
									.next());

							fromText.setNewValue(item.getItemProperty(TBL_FROM)
									.getValue() + "");
							subjectText
									.setValue(item.getItemProperty(TBL_TITLE)
											.getValue() + "");
							detailsTxtField.setValue(item.getItemProperty(
									TBL_DETAILS).getValue()
									+ "");

							removeAttachButton.click();
							dateTimeLabel.setValue(item.getItemProperty(
									TBL_DATETIME).getValue()
									+ "");
							rplyForm = new SFormLayout(dateTimeLabel, fromText,
									subjectText, detailsTxtField);
							showWindow.setContent(rplyForm);

							fromText.setReadOnly(true);

							getUI().addWindow(showWindow);
							showWindow.setModal(true);
							subjectText.focus();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					titleField.setValue("");
					detailsField.setValue("");
					datetimeField.setValue(new Date());
				}

			}
		});

	}

	private void loadList() {
		try {

			List list = null;
			table.removeAllItems();
			reqHeaders = new String[] { TBL_SINO, TBL_FOLDER, TBL_TITLE,
					TBL_FROM, TBL_DETAILS, TBL_DATETIME };
			table.setVisibleColumns(reqHeaders);

			list = daoObj.getAllEmails(getLoginID(), checkEmail);

			String details = "", folder = "";
			int ct = 1;
			Iterator it = list.iterator();
			while (it.hasNext()) {
				MyMailsModel objModel = (MyMailsModel) it.next();
				details = "";

				details = reaDetailsFile(getLoginID() + "_1_"
						+ objModel.getFolder_id() + "_"
						+ objModel.getMail_number() + ".txt");

				if (objModel.getFolder_id() == 1)
					folder = "Received";
				else if (objModel.getFolder_id() == 2)
					folder = "Sent";
				else
					folder = "Draft";

				table.addItem(
						new Object[] { ct, folder, objModel.getSubject(),
								objModel.getEmails(), details,
								objModel.getDate_time() }, objModel.getId());

				ct++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		clearError();
		boolean valid = true;

		if (datetimeField.getValue() == null
				|| datetimeField.getValue().equals("")) {
			setRequiredError(datetimeField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else
			setRequiredError(datetimeField, null, false);

		if (titleField.getValue() == null || titleField.getValue().equals("")) {
			setRequiredError(titleField, getPropertyName("invalid_data"), true);
			valid = false;
		} else
			setRequiredError(titleField, null, false);

		return valid;
	}

	private void clearError() {
		titleField.setComponentError(null);
		detailsField.setComponentError(null);
	}

	public void createDetailsFile(String details, String fileName) {
		try {
			File file = new File(VaadinServlet.getCurrent().getServletContext()
					.getRealPath("/")
					+ "Mails/" + fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(details);
			output.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String reaDetailsFile(String fileName) {
		String details = "";
		try {
			FileInputStream fisTargetFile = new FileInputStream(new File(
					VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")
							+ "Mails/" + fileName));

			details = IOUtils.toString(fisTargetFile, "UTF-8");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return details;
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										Collection selectedItems = (Collection) table
												.getValue();

										if (selectedItems.size() > 0) {
											daoObj.deleteMails(selectedItems);
										}

										Notification
												.show(getPropertyName("save_success"),
														Type.WARNING_MESSAGE);
										loadList();

									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(
												getPropertyName("Error"),
												Type.ERROR_MESSAGE);
									}
								}
							}
						});

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
