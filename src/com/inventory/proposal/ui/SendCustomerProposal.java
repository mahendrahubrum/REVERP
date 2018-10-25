package com.inventory.proposal.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.proposal.dao.SendCustomerProposalDao;
import com.inventory.proposal.dao.SupplierProposalReceiptionDao;
import com.inventory.proposal.dao.SupplierQuotationRequestDao;
import com.inventory.proposal.model.ProposalsSentToCustomersModel;
import com.inventory.proposal.model.SupplierProposalReceiptionModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date Apr 24, 2014
 */

public class SendCustomerProposal extends SparkLogic {

	private static final long serialVersionUID = -8378197681787420442L;

	long id;

	SPanel mainPanel;

	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	SComboField customerProporsalNumbersCombo;
	STextField amountTextField;
	SComboField customerSelect;
	SDateField dateField;
	SComboField requestCombo, supplierProposalsSelect;
	STextArea headTextField;
	STextArea contentTextArea;
	SComboField responsibleEmployee;
	SNativeSelect statusSelect;

	List list;
	SendCustomerProposalDao objDao;

	SButton createNewButton;
	SMail mail;
	SCheckBox sendEmailCheckBox;

	EmailConfigurationModel emainlConfig;
	
	SFileUpload fileUpload;
    SFileUploder fileUploader;
    SVerticalLayout documentLayout;
    SVerticalLayout docMainLayout;
    FileDownloader downloader;
    SHorizontalLayout uploadButtonLayout;
    SButton removeButton;
    public final static String DIR=VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/EmailAttachments/CustomerProposal/";

	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mail = new SMail();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");
		
		uploadButtonLayout=new SHorizontalLayout();
		uploadButtonLayout.setSpacing(true);
		documentLayout = new SVerticalLayout();
		docMainLayout = new SVerticalLayout();
		documentLayout.setSpacing(true);
		docMainLayout.setSpacing(true);
		docMainLayout.setStyleName("plain_layout_bordered");

		setSize(850, 620);
		objDao = new SendCustomerProposalDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		sendEmailCheckBox = new SCheckBox(
				getPropertyName("send_email_customer"), true);

		try {

			customerSelect = new SComboField(getPropertyName("customer"), 250,
					new LedgerDao().getAllCustomers(getOfficeID()), "id",
					"name",false,"Select");

			requestCombo = new SComboField(
					getPropertyName("quotation_request"), 250,
					new SupplierQuotationRequestDao()
							.getAllSupplierQutationRequests(getOfficeID()),
					"id", "head",false,"Select");

			supplierProposalsSelect = new SComboField(
					getPropertyName("proposals"), 250);
			supplierProposalsSelect.setInputPrompt("---------------------_Select------------------");

			responsibleEmployee = new SComboField(
					getPropertyName("send_by"),
					200,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
									getOfficeID(), getOrganizationID()), "id",
					"first_name",false,"Select");
			statusSelect = new SNativeSelect(getPropertyName("status"), 200,
					SConstants.proposalStatuses, "key", "value");
			statusSelect.setValue((long) 1);
			dateField = new SDateField(getPropertyName("date"), 100,
					getDateFormat(), getWorkingDate());
			contentTextArea = new STextArea(getPropertyName("content"), 650,
					200);
			amountTextField = new STextField(getPropertyName("amount"), 200);
			amountTextField.setReadOnly(true);
			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			save = new SButton(getPropertyName("Save"));
			edit = new SButton(getPropertyName("Edit"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));
			cancel = new SButton(getPropertyName("Cancel"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(edit);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(cancel);
			buttonLayout.setSpacing(true);

			edit.setVisible(false);
			delete.setVisible(false);
			update.setVisible(false);
			cancel.setVisible(false);

			customerProporsalNumbersCombo = new SComboField(null, 250);

			loadOptions(0);

			headTextField = new STextArea(getPropertyName("head"), 650);
			
			removeButton = new SButton("Delete");
			fileUploader=new SFileUploder();
	        fileUpload=new SFileUpload(null,fileUploader);
	        fileUpload.setButtonCaption("Upload Document");
	        fileUpload.setImmediate(true);
	        
	        uploadButtonLayout.addComponent(fileUpload);
	        uploadButtonLayout.addComponent(removeButton);
	        docMainLayout.addComponent(uploadButtonLayout);
	        docMainLayout.addComponent(documentLayout);

	        SHorizontalLayout bottomLay = new SHorizontalLayout();
	        bottomLay.setSpacing(true);
	        SFormLayout statusForm = new SFormLayout();
	        statusForm.setSpacing(true);
	        		
			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("proposal_no"));
			salLisrLay.addComponent(customerProporsalNumbersCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(requestCombo);
			form.addComponent(supplierProposalsSelect);
			form.addComponent(customerSelect);
			form.addComponent(headTextField);
			form.addComponent(contentTextArea);
			form.addComponent(bottomLay);
			
			statusForm.addComponent(amountTextField);
			statusForm.addComponent(responsibleEmployee);
			statusForm.addComponent(statusSelect);
			statusForm.addComponent(dateField);
			statusForm.addComponent(sendEmailCheckBox);
			
			bottomLay.addComponent(statusForm);
			bottomLay.addComponent(docMainLayout);
			

			vLayout.addComponent(form);
			vLayout.addComponent(buttonLayout);
			vLayout.setMargin(true);

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
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					customerProporsalNumbersCombo.setValue((long) 0);
				}
			});
			
			fileUpload.addFinishedListener(new FinishedListener() {
				
				@Override
				public void uploadFinished(FinishedEvent event) {
					try {
						if(fileUploader.getFile()!=null){
							SButtonLink link=new SButtonLink(fileUploader.getFile().getName());
							SCheckBox selectBox=new SCheckBox();
							SHorizontalLayout hlay=new SHorizontalLayout(selectBox,link);
							hlay.setId(fileUploader.getFile().getAbsolutePath());
							documentLayout.addComponent(hlay);
							downloader = new FileDownloader(new FileResource(fileUploader.getFile()));
	    					downloader.extend(link);
	    					link.setImmediate(true);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
			removeButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
				@Override
				public void buttonClick(ClickEvent event) {
					
					Iterator compIter=documentLayout.getComponentIterator();
					SHorizontalLayout lay;
					SCheckBox selectBox;
					List remList=new ArrayList();
					while (compIter.hasNext()) {
						lay = (SHorizontalLayout) compIter.next();
						selectBox=(SCheckBox) lay.getComponent(0);
						if(selectBox.getValue()){
							remList.add(lay);
						}
					}
					compIter=remList.iterator();
					while (compIter.hasNext()) {
						documentLayout.removeComponent((SHorizontalLayout)compIter.next());
					}
					
				}
			});

			requestCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (requestCombo.getValue() != null) {
									bic = CollectionContainer.fromBeans(
											new SupplierProposalReceiptionDao()
													.getAllSupplierProposalsFromRequest((Long) requestCombo
															.getValue()), "id");
									supplierProposalsSelect
											.setContainerDataSource(bic);
									supplierProposalsSelect
											.setItemCaptionPropertyId("head");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			supplierProposalsSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (supplierProposalsSelect.getValue() != null) {

									SupplierProposalReceiptionModel obj = new SupplierProposalReceiptionDao()
											.getSupplierProposal((Long) supplierProposalsSelect
													.getValue());
									headTextField.setValue(obj.getHead());
									contentTextArea.setValue(obj.getContent());
									contentTextArea.setValue(obj.getContent());
									amountTextField.setNewValue(asString(obj
											.getAmount()));
									customerSelect
											.setValue(obj.getRequest()
													.getEnquiry().getCustomer()
													.getId());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							ProposalsSentToCustomersModel quotObj = new ProposalsSentToCustomersModel();

							quotObj.setContent(contentTextArea.getValue());
							quotObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							quotObj.setSupplier_proposal(new SupplierProposalReceiptionModel(
									(Long) supplierProposalsSelect.getValue()));
							quotObj.setHead(headTextField.getValue());
							quotObj.setSendBy(new S_LoginModel(
									(Long) responsibleEmployee.getValue()));
							quotObj.setCustomer(new LedgerModel(
									(Long) customerSelect.getValue()));
							quotObj.setOffice(new S_OfficeModel(getOfficeID()));
							quotObj.setNumber(getNextSequence(
									"Customer_Proposal_number", getLoginID()));
							quotObj.setStatus((Long) statusSelect.getValue());

							try {

								id = objDao.save(quotObj);
								
								ArrayList arrayList=saveDocuments(id);
								
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								if (sendEmailCheckBox.getValue()) {
									Address[] emails = emails = new InternetAddress[1];
									String emailID = new LedgerDao()
											.getEmailFromLedgerID((Long) customerSelect
													.getValue());
									if (emailID.length() > 3) {

										String fromMail = new UserManagementDao()
												.getUseEmailFromLogin(quotObj
														.getSendBy().getId());
										try {
											if (new EmailConfigDao()
													.getEmailConfiguration(getLoginID()) != null) {
												emails[0] = new InternetAddress(
														emailID);
												mail.sendMailFromUserEmailWithAttachList(
														emails,
														quotObj.getContent(),
														quotObj.getHead(),
														arrayList, getLoginID(),
														fromMail);
											} else
												Notification
														.show(getPropertyName("email_not_configured"),
																Type.WARNING_MESSAGE);
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
								}

								loadOptions(id);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

							setRequiredError(customerSelect, null, false);

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

			customerProporsalNumbersCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								if (customerProporsalNumbersCombo.getValue() != null
										&& !customerProporsalNumbersCombo
												.getValue().toString()
												.equals("0")) {

									save.setVisible(false);
									edit.setVisible(true);
									delete.setVisible(true);
									update.setVisible(false);
									cancel.setVisible(false);

									ProposalsSentToCustomersModel objModel = objDao
											.getCustomerProposalSent((Long) customerProporsalNumbersCombo
													.getValue());

									setWritableAll();
									headTextField.setValue(objModel.getHead());
									requestCombo.setValue(objModel
											.getSupplier_proposal()
											.getRequest().getId());
									supplierProposalsSelect.setValue(objModel
											.getSupplier_proposal().getId());
									customerSelect.setValue(objModel
											.getCustomer().getId());
									responsibleEmployee.setValue(objModel
											.getSendBy().getId());
									statusSelect.setValue(objModel.getStatus());
									dateField.setValue(objModel.getDate());
									contentTextArea.setValue(objModel
											.getContent());
									sendEmailCheckBox.setValue(false);
									setReadOnlyAll();
									
									File folder=new File(DIR+objModel.getId()+"/");
									if (folder.exists()) {
										for (File file : folder.listFiles()) {
											if (file.exists()) {
												SButtonLink link = new SButtonLink(
														file.getName());
												downloader = new FileDownloader(
														new FileResource(file));
												downloader.extend(link);
												link.setImmediate(true);
												SCheckBox selectBox = new SCheckBox();
												SHorizontalLayout hlay = new SHorizontalLayout(
														selectBox, link);
												hlay.setId(file
														.getAbsolutePath());
												documentLayout
														.addComponent(hlay);
											}
										}
									}
									
								} else {
									save.setVisible(true);
									edit.setVisible(false);
									delete.setVisible(false);
									update.setVisible(false);
									cancel.setVisible(false);
									setWritableAll();
									headTextField.setValue("");
									customerSelect.setValue(null);
									requestCombo.setValue(null);
									responsibleEmployee.setValue(getLoginID());
									statusSelect.setValue((long) 1);
									dateField.setValue(getWorkingDate());
									contentTextArea.setValue("");
									amountTextField.setNewValue("0");
									sendEmailCheckBox.setValue(true);
									documentLayout.removeAllComponents();
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
						loadOptions(Long
								.parseLong(customerProporsalNumbersCombo
										.getValue().toString()));

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
												objDao.delete((Long) customerProporsalNumbersCombo
														.getValue());
												
												File file2=new File(DIR+(Long) customerProporsalNumbersCombo
														.getValue()+"/"); // Old File
												if(file2.exists())
													FileUtils.deleteDirectory(file2);

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
						if (customerProporsalNumbersCombo.getValue() != null) {

							if (isValid()) {

								ProposalsSentToCustomersModel quotObj = objDao
										.getCustomerProposalSent((Long) customerProporsalNumbersCombo
												.getValue());

								quotObj.setContent(contentTextArea.getValue());
								quotObj.setDate(CommonUtil
										.getSQLDateFromUtilDate(dateField
												.getValue()));
								quotObj.setSupplier_proposal(new SupplierProposalReceiptionModel(
										(Long) supplierProposalsSelect
												.getValue()));
								quotObj.setHead(headTextField.getValue());
								quotObj.setSendBy(new S_LoginModel(
										(Long) responsibleEmployee.getValue()));
								quotObj.setCustomer(new LedgerModel(
										(Long) customerSelect.getValue()));
								quotObj.setOffice(new S_OfficeModel(
										getOfficeID()));
								quotObj.setStatus((Long) statusSelect
										.getValue());

								try {

									objDao.update(quotObj);
									ArrayList arr=saveDocuments(quotObj.getId());
									
									Notification.show(
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);

									if (sendEmailCheckBox.getValue()) {
										Address[] emails = emails = new InternetAddress[1];
										String emailID = new LedgerDao()
												.getEmailFromLedgerID((Long) customerSelect
														.getValue());
										if (emailID.length() > 3) {

											String fromMail = new UserManagementDao()
													.getUseEmailFromLogin(quotObj
															.getSendBy()
															.getId());
											try {
												if (new EmailConfigDao()
														.getEmailConfiguration(getLoginID()) != null) {

													emails[0] = new InternetAddress(
															emailID);
													mail.sendMailFromUserEmailWithAttachList(
															emails,
															quotObj.getContent(),
															quotObj.getHead(),
															arr, getLoginID(),
															fromMail);
												} else
													Notification
															.show(getPropertyName("email_not_configured"),
																	Type.WARNING_MESSAGE);

											} catch (Exception e) {
												// TODO: handle exception
											}
										}
									}

									loadOptions(quotObj.getId());

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								setRequiredError(customerSelect, null, false);
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
		}
		return mainPanel;

	}

	protected ArrayList<File> saveDocuments(long id) {
		
		ArrayList<File> docList=new ArrayList<File>();
		try {
			File file = new File(DIR+id+"/");
			File[] listOfFiles=null;
			List deleteList=null;
			if (file.exists()){
				listOfFiles = file.listFiles();
			}
			else
				file.mkdir();
				
			if(listOfFiles!=null)
			deleteList=new ArrayList(Arrays.asList(listOfFiles));
			Iterator compIter=documentLayout.getComponentIterator();
			
			SHorizontalLayout lay;
			File docFile;
			SButtonLink link;
			while (compIter.hasNext()) {
				lay = (SHorizontalLayout) compIter.next();
				link=(SButtonLink) lay.getComponent(1); // Uploaded file
				docFile=new File(DIR+id+"/"+link.getCaption()); // New File to be created.
				try {
					FileUtils.copyFile(new File(lay.getId()), docFile);
					docList.add(docFile);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if(deleteList!=null && deleteList.contains(docFile)){
						deleteList.remove(docFile);
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}	
			}
			
			for(int i=0;i<deleteList.size();i++){
				File fileName=(File)deleteList.get(i);
				FileUtils.forceDelete(fileName);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
		return docList;
	}
	
	
	protected void deleteDocuments(long custId) {
		try {

			String folder =  "";
			File f = new File(folder);
			if (f.exists())
				FileUtils.deleteDirectory(f);
		}
		catch (Exception e) {
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
			// TODO: handle exception
		}
		return details;
	}

	public void setReadOnlyAll() {
		headTextField.setReadOnly(true);
		customerSelect.setReadOnly(true);
		requestCombo.setReadOnly(true);
		responsibleEmployee.setReadOnly(true);
		amountTextField.setReadOnly(true);
		statusSelect.setReadOnly(true);
		dateField.setReadOnly(true);
		contentTextArea.setReadOnly(true);
		supplierProposalsSelect.setReadOnly(true);
		headTextField.focus();
	}

	public void setWritableAll() {
		headTextField.setReadOnly(false);
		customerSelect.setReadOnly(false);
		requestCombo.setReadOnly(false);
		responsibleEmployee.setReadOnly(false);
		amountTextField.setReadOnly(false);
		statusSelect.setReadOnly(false);
		dateField.setReadOnly(false);
		contentTextArea.setReadOnly(false);
		supplierProposalsSelect.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {

			list = objDao.getAllCustomersSentProposals(getOfficeID());

			ProposalsSentToCustomersModel sop = new ProposalsSentToCustomersModel();
			sop.setId(0);
			sop.setHead("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			customerProporsalNumbersCombo.setContainerDataSource(bic);
			customerProporsalNumbersCombo.setItemCaptionPropertyId("head");

			customerProporsalNumbersCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
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

		if (supplierProposalsSelect.getValue() == null
				|| supplierProposalsSelect.getValue().equals("")) {
			setRequiredError(supplierProposalsSelect,
					getPropertyName("invalid_selection"), true);
			supplierProposalsSelect.focus();
			ret = false;
		} else
			setRequiredError(supplierProposalsSelect, null, false);

		if (statusSelect.getValue() == null
				|| statusSelect.getValue().equals("")) {
			setRequiredError(statusSelect,
					getPropertyName("invalid_selection"), true);
			statusSelect.focus();
			ret = false;
		} else
			setRequiredError(statusSelect, null, false);

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

		if (customerSelect.getValue() == null
				|| customerSelect.getValue().equals("")) {
			setRequiredError(customerSelect,
					getPropertyName("invalid_selection"), true);
			customerSelect.focus();
			ret = false;
		} else
			setRequiredError(customerSelect, null, false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
