package com.inventory.proposal.ui;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.proposal.dao.SupplierProposalReceiptionDao;
import com.inventory.proposal.dao.SupplierQuotationRequestDao;
import com.inventory.proposal.model.SupplierProposalReceiptionModel;
import com.inventory.proposal.model.SupplierQuotationRequestModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.dao.MailDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.mailclient.model.MyMailsModel;
import com.webspark.mailclient.ui.ComposeMailUI;
import com.webspark.mailclient.ui.MyEmailsUI;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date Apr 24, 2014
 */

public class SupplierProporsalReceiption extends SparkLogic {

	private static final long serialVersionUID = -8378197681787420442L;

	long id;

	SPanel mainPanel;

	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	private static final String TBL_SINO = "#";
	private static final String TBL_FROM = "From / To";
	private static final String TBL_TITLE = "Title";
	private static final String TBL_DETAILS = "Details";
	private static final String TBL_DATETIME = "Date & Time";
	private static final String TBL_FOLDER = "Folder";

	private STable table;
	String[] reqHeaders;
	STextArea fromText, subjectText;
	STextField amountTextField;
	RichTextArea detailsTxtField;
	SLabel dateTimeLabel;
	SWindow showWindow;
	SFormLayout rplyForm;
	SButton setDetBtn, appendDetBtn;

	CollectionContainer bic;

	SButton save;
	SButton delete;
	SButton update;

	SComboField proporsalNumbersCombo;

	SComboField suppliersSelect;
	SDateField dateField;
	SComboField requestCombo;
	STextArea headTextField;
	STextArea contentTextArea;
	SComboField responsibleEmployee;
	SNativeSelect statusSelect;

	List list;
	SupplierProposalReceiptionDao objDao;

	SButton createNewButton;
	SMail mail;

	EmailConfigurationModel emainlConfig;

	private SButton syncEmailInboxButton;
	private SButton sendMailButton;
	private LedgerDao ledgDao;

//	private STextArea mailContent
	
	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mail = new SMail();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(1000, 620);
		objDao = new SupplierProposalReceiptionDao();
		ledgDao=new LedgerDao();
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {

			syncEmailInboxButton = new SButton();
			syncEmailInboxButton.setStyleName("loadAllBtnStyle");
			syncEmailInboxButton.setDescription("Synchonize my Email inbox");
			
			sendMailButton=new SButton("Send Mail To Supplier");

			dateTimeLabel = new SLabel(getPropertyName("date_n_time"));
			fromText = new STextArea(getPropertyName("from/to"), 700, 40);
			subjectText = new STextArea(getPropertyName("subject"), 700, 40);
			amountTextField = new STextField(null, 200);
			detailsTxtField = new RichTextArea();
			detailsTxtField.setCaption(getPropertyName("details"));
			detailsTxtField.setWidth("700px");
			detailsTxtField.setHeight("300px");
			showWindow = new SWindow(null, 800, 540);
			showWindow.setWidth("800");
			showWindow.setHeight("540");
			showWindow.setCloseShortcut(KeyCode.ESCAPE);
			setDetBtn = new SButton("Set Content",
					"Set Email Content to Proposal Content");
			appendDetBtn = new SButton("Append Content",
					"Append Email Content to Proposal Content");

			reqHeaders = new String[] { TBL_SINO, TBL_TITLE, TBL_FOLDER,
					TBL_DETAILS, TBL_DATETIME };

			table = new STable(null);
			table.addContainerProperty(TBL_SINO, Integer.class, null, TBL_SINO,
					null, Align.CENTER);
			table.addContainerProperty(TBL_FROM, String.class, null, getPropertyName("from/to"),null, Align.CENTER);
			table.addContainerProperty(TBL_TITLE, String.class, null,getPropertyName("title"), null, Align.LEFT);
			table.addContainerProperty(TBL_DETAILS, String.class, null,getPropertyName("details"), null, Align.LEFT);
			table.addContainerProperty(TBL_FOLDER, String.class, null,getPropertyName("folder"), null, Align.LEFT);
			table.addContainerProperty(TBL_DATETIME, Timestamp.class, null,getPropertyName("date_time"), null, Align.CENTER);
			table.setSelectable(true);

//			table.setColumnWidth(TBL_SINO, 20);
//			table.setColumnWidth(TBL_TITLE, 250);
//			table.setColumnWidth(TBL_DETAILS, 200);
//			table.setColumnWidth(TBL_FROM, 200);
//
//			table.setColumnWidth(TBL_DATETIME, 130);
//			table.setColumnWidth(TBL_FOLDER, 70);
			table.setColumnExpandRatio(TBL_DETAILS, 6f);

			table.setWidth("720");
			table.setHeight("180");

			suppliersSelect = new SComboField(null, 200);
			suppliersSelect.setInputPrompt("----------------Select--------------");
			requestCombo = new SComboField(
					null,
					200,
					new SupplierQuotationRequestDao()
							.getAllSupplierQutationRequestsWithEnquiryID(getOfficeID()),
					"id", "head",false,"Select");

			responsibleEmployee = new SComboField(
					null,
					200,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
									getOfficeID(), getOrganizationID()), "id",
					"first_name",false,"Select");
			statusSelect = new SNativeSelect(null, 180,
					SConstants.proposalStatuses, "key", "value");
			statusSelect.setValue((long) 1);
			dateField = new SDateField(null, 100,
					getDateFormat(), getWorkingDate());
			contentTextArea = new STextArea(getPropertyName("content"), 720,
					150);

			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			save = new SButton(getPropertyName("Save"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(sendMailButton);
			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);
			sendMailButton.setVisible(false);

			proporsalNumbersCombo = new SComboField(null, 200);

			loadOptions(0);

			headTextField = new STextArea(getPropertyName("head"), 720,50);
			
			SGridLayout topGridLayout=new SGridLayout(8,1);
			topGridLayout.setSpacing(true);
			
			SGridLayout bottomGridLayout=new SGridLayout(8,1);
			bottomGridLayout.setSpacing(true);

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(proporsalNumbersCombo);
			salLisrLay.addComponent(createNewButton);
			
			topGridLayout.addComponent(new SLabel(getPropertyName("proposal_no")));
			topGridLayout.addComponent(salLisrLay);
			topGridLayout.addComponent(new SLabel(getPropertyName("supplier_quotation_request")));
			topGridLayout.addComponent(requestCombo);
			topGridLayout.addComponent(new SLabel(getPropertyName("supplier")));
			topGridLayout.addComponent(suppliersSelect);
			form.addComponent(topGridLayout);
			form.addComponent(new SHorizontalLayout(
					getPropertyName("emails_from_suppliers"), true, table,
					syncEmailInboxButton));
			form.addComponent(headTextField);
			form.addComponent(contentTextArea);
			
			
			bottomGridLayout.addComponent(new SLabel(getPropertyName("amount")));
			bottomGridLayout.addComponent(amountTextField);
			bottomGridLayout.addComponent(new SLabel(getPropertyName("date")));
			bottomGridLayout.addComponent(dateField);
			bottomGridLayout.addComponent(new SLabel(getPropertyName("send_by")));
			bottomGridLayout.addComponent(responsibleEmployee);
			bottomGridLayout.addComponent(new SLabel(getPropertyName("status")));
			bottomGridLayout.addComponent(statusSelect);
			
			vLayout.addComponent(topGridLayout);
			vLayout.addComponent(form);
			vLayout.addComponent(bottomGridLayout);
			vLayout.addComponent(buttonLayout);
			vLayout.setMargin(true);
			vLayout.setSpacing(true);

			vLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

			mainPanel.setContent(vLayout);
			
			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});
			
			sendMailButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(suppliersSelect.getValue()!=null){
						try {
							SupplierModel sup=new SupplierDao().getSupplierFromLedger((Long)suppliersSelect.getValue());
							SWindow wind=new ComposeMailUI(sup.getAddress().getEmail());
							wind.center();
							getUI().addWindow(wind);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
					}
				}
			});
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					proporsalNumbersCombo.setValue(null);
				}
			});

			syncEmailInboxButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (suppliersSelect.getValue() != null
							&& !suppliersSelect.getValue().toString()
									.equals("0")) {
						new MyEmailsUI().syncronizeMail("Inbox");
						Object obj = suppliersSelect.getValue();
						suppliersSelect.setValue(null);
						suppliersSelect.setValue(obj);
					}
				}
			});

			requestCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						if (requestCombo.getValue() != null) {
							SupplierQuotationRequestModel obj = new SupplierQuotationRequestDao()
									.getSupplierQuotationRequest((Long) requestCombo
											.getValue());
							List<Long> supLst = new ArrayList<Long>();
							String[] supIDs = obj.getSuppliers().split(",");
							for (String string : supIDs) {
								supLst.add(toLong(string));
							}

							bic = CollectionContainer.fromBeans(ledgDao
									.getAllSuppliersFromIDs(supLst), "id");
							suppliersSelect.setContainerDataSource(bic);
							suppliersSelect.setItemCaptionPropertyId("name");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			suppliersSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (suppliersSelect.getValue() != null
								&& !suppliersSelect.getValue().toString()
										.equals("0")) {

							try {
								emainlConfig = new EmailConfigDao()
										.getEmailConfiguration(getLoginID());
								if (emainlConfig != null) {
									String email = ledgDao
											.getEmailFromLedgerID((Long) suppliersSelect
													.getValue());
									if (email.length() > 3)
										loadList(email);
								} else {
									SNotification.show(
											getPropertyName("mail_config_msg"),
											Type.ERROR_MESSAGE);
								}

							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			setDetBtn.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						contentTextArea.setValue(detailsTxtField.getValue());
						showWindow.close();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			appendDetBtn.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						contentTextArea.setValue(contentTextArea.getValue()
								+ detailsTxtField.getValue());
						showWindow.close();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (table.getValue() != null
							&& !table.getValue().equals("")) {
						try {
							Item item = table.getItem(table.getValue());

							fromText.setNewValue(item.getItemProperty(TBL_FROM)
									.getValue() + "");
							subjectText
									.setValue(item.getItemProperty(TBL_TITLE)
											.getValue() + "");
							detailsTxtField.setValue(item.getItemProperty(
									TBL_DETAILS).getValue()
									+ "");

							dateTimeLabel.setValue(item.getItemProperty(
									TBL_DATETIME).getValue()
									+ "");
							rplyForm = new SFormLayout(dateTimeLabel, fromText,
									subjectText, detailsTxtField,
									new SHorizontalLayout(true, setDetBtn,
											appendDetBtn));
							showWindow.setContent(rplyForm);

							fromText.setReadOnly(true);

							getUI().addWindow(showWindow);
							showWindow.setModal(true);
							subjectText.focus();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						fromText.setNewValue("");
						subjectText.setValue("");
						detailsTxtField.setValue("");
						dateTimeLabel.setValue("");
					}
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							SupplierProposalReceiptionModel quotObj = new SupplierProposalReceiptionModel();

							quotObj.setContent(contentTextArea.getValue());
							quotObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							quotObj.setRequest(new SupplierQuotationRequestModel(
									(Long) requestCombo.getValue()));
							quotObj.setHead(headTextField.getValue());
							quotObj.setSendBy(new S_LoginModel(
									(Long) responsibleEmployee.getValue()));
							quotObj.setSupplier(new LedgerModel(
									(Long) suppliersSelect.getValue()));
							quotObj.setAmount(toDouble(amountTextField
									.getValue()));
							quotObj.setOffice(new S_OfficeModel(getOfficeID()));
							quotObj.setNumber(getNextSequence(
									"Proposal_number", getLoginID()));
							quotObj.setStatus((Long) statusSelect.getValue());

							try {

								id = objDao.save(quotObj);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);
								loadOptions(id);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

							setRequiredError(suppliersSelect, null, false);

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

			proporsalNumbersCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								table.removeAllItems();
								if (proporsalNumbersCombo.getValue() != null
										&& !proporsalNumbersCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);
									sendMailButton.setVisible(true);

									SupplierProposalReceiptionModel objModel = objDao
											.getSupplierProposal((Long) proporsalNumbersCombo
													.getValue());

									headTextField.setValue(objModel.getHead());
									requestCombo.setValue(objModel.getRequest()
											.getId());
									suppliersSelect.setValue(objModel
											.getSupplier().getId());
									responsibleEmployee.setValue(objModel
											.getSendBy().getId());
									statusSelect.setValue(objModel.getStatus());
									dateField.setValue(objModel.getDate());
									contentTextArea.setValue(objModel
											.getContent());
									amountTextField.setValue(asString(objModel
											.getAmount()));

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);
									sendMailButton.setVisible(false);
									headTextField.setValue("");
									suppliersSelect.setValue(null);
									requestCombo.setValue(null);
									responsibleEmployee.setValue(getLoginID());
									statusSelect.setValue((long) 1);
									dateField.setValue(getWorkingDate());
									contentTextArea.setValue("");
									amountTextField.setValue("0");
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



			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												objDao.delete((Long) proporsalNumbersCombo
														.getValue());

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												Notification
														.show(getPropertyName("Error"),
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
						if (proporsalNumbersCombo.getValue() != null) {

							if (isValid()) {

								SupplierProposalReceiptionModel quotObj = objDao
										.getSupplierProposal((Long) proporsalNumbersCombo
												.getValue());

								quotObj.setContent(contentTextArea.getValue());
								quotObj.setDate(CommonUtil
										.getSQLDateFromUtilDate(dateField
												.getValue()));
								quotObj.setRequest(new SupplierQuotationRequestModel(
										(Long) requestCombo.getValue()));
								quotObj.setHead(headTextField.getValue());
								quotObj.setSendBy(new S_LoginModel(
										(Long) responsibleEmployee.getValue()));
								quotObj.setStatus((Long) statusSelect
										.getValue());
								quotObj.setSupplier(new LedgerModel(
										(Long) suppliersSelect.getValue()));
								quotObj.setAmount(toDouble(amountTextField
										.getValue()));

								try {

									objDao.update(quotObj);
									Notification.show(
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);

									loadOptions(quotObj.getId());

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								setRequiredError(suppliersSelect, null, false);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;

	}

	private void loadList(String checkEmail) {
		try {

			List list = null;
			table.removeAllItems();
			reqHeaders = new String[] { TBL_SINO, TBL_FOLDER, TBL_TITLE,
					TBL_FROM, TBL_DETAILS, TBL_DATETIME };
			table.setVisibleColumns(reqHeaders);

			list = new MailDao().getAllEmailsByFolder(getLoginID(), checkEmail,
					1);

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

	public String reaDetailsFile(String fileName) {
		String details = "";
		try {
			FileInputStream fisTargetFile = new FileInputStream(new File(
					VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")
							+ "Mails/" + fileName));

			details = IOUtils.toString(fisTargetFile, "UTF-8");

		} catch (Exception e) {
		}
		return details;
	}

	public void loadOptions(long id) {
		List testList;
		try {

			list = objDao.getAllSupplierProposals(getOfficeID());

			SupplierProposalReceiptionModel sop = new SupplierProposalReceiptionModel();
			sop.setId(0);
			sop.setHead("------------ Create New -------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			proporsalNumbersCombo.setContainerDataSource(bic);
			proporsalNumbersCombo.setItemCaptionPropertyId("head");
			proporsalNumbersCombo.setInputPrompt("------------ Create New -------------");

			if(id!=0)
				proporsalNumbersCombo.setValue(id);
			else
				proporsalNumbersCombo.setValue(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_data"), true);
			dateField.focus();
			ret = false;
		} else
			setRequiredError(dateField, null, false);

		if (responsibleEmployee.getValue() == null
				|| responsibleEmployee.getValue().equals("")) {
			setRequiredError(responsibleEmployee,
					getPropertyName("invalid_selection"), true);
			responsibleEmployee.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployee, null, false);

		if (amountTextField.getValue() == null
				|| amountTextField.getValue().equals("")) {
			setRequiredError(amountTextField, getPropertyName("invalid_data"),
					true);
			amountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(amountTextField.getValue()) < 0) {
					setRequiredError(amountTextField,
							getPropertyName("invalid_data"), true);
					amountTextField.focus();
					ret = false;
				} else
					setRequiredError(amountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(amountTextField,
						getPropertyName("invalid_data"), true);
				amountTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (statusSelect.getValue() == null
				|| statusSelect.getValue().equals("")) {
			setRequiredError(statusSelect,
					getPropertyName("invalid_selection"), true);
			statusSelect.focus();
			ret = false;
		} else
			setRequiredError(statusSelect, null, false);

		if (requestCombo.getValue() == null
				|| requestCombo.getValue().equals("")) {
			setRequiredError(requestCombo,
					getPropertyName("invalid_selection"), true);
			requestCombo.focus();
			ret = false;
		} else
			setRequiredError(requestCombo, null, false);

		if (headTextField.getValue() == null
				|| headTextField.getValue().equals("")) {
			setRequiredError(headTextField,
					getPropertyName("invalid_selection"), true);
			headTextField.focus();
			ret = false;
		} else
			setRequiredError(headTextField, null, false);

		if (suppliersSelect.getValue() == null
				|| suppliersSelect.getValue().equals("")) {
			setRequiredError(suppliersSelect,
					getPropertyName("invalid_selection"), true);
			suppliersSelect.focus();
			ret = false;
		} else
			setRequiredError(suppliersSelect, null, false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
