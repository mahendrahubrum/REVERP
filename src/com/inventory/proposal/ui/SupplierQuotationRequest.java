package com.inventory.proposal.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.proposal.dao.CustomerEnquiryDao;
import com.inventory.proposal.dao.SupplierQuotationRequestDao;
import com.inventory.proposal.model.CustomerEnquiryModel;
import com.inventory.proposal.model.SupplierQuotationRequestModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date Apr 24, 2014
 */

public class SupplierQuotationRequest extends SparkLogic {

	private static final long serialVersionUID = 5008903281115180160L;
	long id;
	SPanel mainPanel;

	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	SWindow popupWindow;
	private SButton newSupplierButton;

	CollectionContainer bic;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	SComboField supplierQuotationCombo;

	STokenField suppliersListSelect;
	SDateField dateField;
	SComboField enquiryCombo, subEnquiryCompo;
	STextArea headTextField;
	STextArea contentTextArea;
	SComboField responsibleEmployee;
	STextField budgetAmountTextField;

	List list;
	SupplierQuotationRequestDao objDao;
	CustomerEnquiryDao enqDao;

	SButton createNewButton;
	SCheckBox sendEmailCheckBox;
	SMail mail;

	private static final String TBL_SINO = "#";
	private static final String TBL_DATE = "Date";
	private static final String TBL_CONTENTS = "Contents";
	private static final String TBL_HEAD = "Head";
	private static final String TBL_SUPPLIERS = "Suppliers";

	private STable table;
	String[] reqHeaders;

	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mail = new SMail();

		popupWindow = new SWindow();
		popupWindow.center();
		popupWindow.setModal(true);

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(1100, 580);
		objDao = new SupplierQuotationRequestDao();
		enqDao = new CustomerEnquiryDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {

			newSupplierButton = new SButton();
			newSupplierButton.setStyleName("addNewBtnStyle");
			newSupplierButton.setDescription("Add new Supplier");

			reqHeaders = new String[] { TBL_SINO, TBL_DATE, TBL_CONTENTS,
					TBL_HEAD, TBL_SUPPLIERS };

			table = new STable(getPropertyName("recent_proposals"), 400, 400);
			table.addContainerProperty(TBL_SINO, Integer.class, null, TBL_SINO,
					null, Align.CENTER);
			table.addContainerProperty(TBL_DATE, Date.class, null,
					getPropertyName("date"), null, Align.CENTER);
			table.addContainerProperty(TBL_CONTENTS, String.class, null,
					getPropertyName("contents"), null, Align.LEFT);
			table.addContainerProperty(TBL_HEAD, String.class, null,
					getPropertyName("head"), null, Align.LEFT);
			table.addContainerProperty(TBL_SUPPLIERS, String.class, null,
					getPropertyName("suppliers"), null, Align.LEFT);
			table.setSelectable(true);

			table.setColumnWidth(TBL_SINO, 20);
			table.setColumnWidth(TBL_CONTENTS, 250);
			table.setColumnWidth(TBL_HEAD, 100);
			table.setColumnWidth(TBL_DATE, 100);

			table.setWidth("650");
			table.setHeight("360");

			sendEmailCheckBox = new SCheckBox(
					getPropertyName("send_email_suppliers"), true);

			suppliersListSelect = new STokenField();
			bic = CollectionContainer.fromBeans(
					new LedgerDao().getAllSuppliers(getOfficeID()), "id");
			suppliersListSelect.setContainerDataSource(bic);
			suppliersListSelect.setTokenCaptionPropertyId("name");
			suppliersListSelect.setWidth("200");
			suppliersListSelect.setStyleName(STokenField.STYLE_TOKENFIELD);
			suppliersListSelect.setNewTokensAllowed(false);
			suppliersListSelect.setInputWidth("200");
			suppliersListSelect.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);
			suppliersListSelect.setInputPrompt("-------------SELECT--------------");

			enquiryCombo = new SComboField(getPropertyName("enquiry"), 250,
					new CustomerEnquiryDao()
							.getAllMasterEnquiries(getOfficeID()), "id",
					"enquiry");
			subEnquiryCompo = new SComboField(getPropertyName("sub_enquiry"),
					250);

			responsibleEmployee = new SComboField(
					getPropertyName("send_by"),
					250,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
									getOfficeID(), getOrganizationID()), "id",
					"first_name");

			dateField = new SDateField(getPropertyName("date"), 100,
					getDateFormat(), getWorkingDate());
			contentTextArea = new STextArea(getPropertyName("content"), 250);

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

			supplierQuotationCombo = new SComboField(null, 250);

			loadOptions(0);

			// groupCombo = new SComboField("Group", 250, new
			// GroupDao().getAllGroupsNames(getOrganizationID()), "id", "name"
			// , true, "Select");

			headTextField = new STextArea(getPropertyName("head"), 250);
			budgetAmountTextField = new STextField(
					getPropertyName("budget_amount"), 250);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("request"));
			salLisrLay.addComponent(supplierQuotationCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);

			SHorizontalLayout supSel = new SHorizontalLayout(
					getPropertyName("supplier"));
			supSel.setSizeFull();
			supSel.addComponent(newSupplierButton);
			supSel.addComponent(suppliersListSelect);
			form.addComponent(supSel);

			form.addComponent(dateField);
			form.addComponent(enquiryCombo);
			form.addComponent(subEnquiryCompo);
			form.addComponent(headTextField);
			form.addComponent(contentTextArea);
			form.addComponent(budgetAmountTextField);
			form.addComponent(responsibleEmployee);

			form.addComponent(sendEmailCheckBox);

			vLayout.setMargin(true);

			SVerticalLayout lay = new SVerticalLayout(true, form, buttonLayout);
			vLayout.addComponent(new SHorizontalLayout(true, lay, table));

			lay.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			mainPanel.setContent(vLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					supplierQuotationCombo.setValue((long) 0);
				}
			});

//			newSupplierButton.addClickListener(new ClickListener() {
//				@Override
//				public void buttonClick(ClickEvent event) {
//					SupplierPannel pan = new SupplierPannel();
//					popupWindow.setContent(pan);
//					popupWindow.setId("SUPPLIER");
//					popupWindow.setCaption("Add Supplier");
//					popupWindow.center();
//					getUI().getCurrent().addWindow(popupWindow);
//				}
//			});

			popupWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					reloadSuppliers();
					if (getHttpSession().getAttribute("saved_id") != null) {
						Set<Long> val = (Set<Long>) suppliersListSelect
								.getValue();
						val.add((Long) getHttpSession()
								.getAttribute("saved_id"));
						suppliersListSelect.setValue(null);
						suppliersListSelect.setValue(val);
						getHttpSession().removeAttribute("saved_id");
					}

				}
			});

			final Action loadAction = new Action("Load This Quotation");
			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					// if(deleteItemButton.isVisible())
					// deleteItemButton.click();
					return new Action[] { loadAction };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					if (table.getValue() != null)
						loadOptions((Long) table.getValue());
				}

			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							boolean sendMail = sendEmailCheckBox.getValue();

							SupplierQuotationRequestModel quotObj = new SupplierQuotationRequestModel();

							String supIDs = "";
							Address[] emails = null;
							if (suppliersListSelect.getValue() != null) {
								long suplId = 0;
								int ct = 0;
								String emailID = "";
								emails = new InternetAddress[((Set<Long>) suppliersListSelect
										.getValue()).size()];
								Iterator it1 = ((Set<Long>) suppliersListSelect
										.getValue()).iterator();
								while (it1.hasNext()) {
									suplId = (Long) it1.next();
									try {
										supIDs += suplId + ",";
										if (sendMail) {
											emailID = new LedgerDao()
													.getEmailFromLedgerID(suplId);
											if (emailID.length() > 3) {
												emails[ct] = new InternetAddress(
														emailID);
												ct++;
											}
										}
									} catch (Exception e) {
									}
								}
							}

							quotObj.setContent(contentTextArea.getValue());
							quotObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							quotObj.setEnquiry(new CustomerEnquiryModel(
									(Long) subEnquiryCompo.getValue()));
							quotObj.setHead(headTextField.getValue());
							quotObj.setBudget_amount(toDouble(budgetAmountTextField
									.getValue()));
							quotObj.setSendBy(new S_LoginModel(
									(Long) responsibleEmployee.getValue()));
							quotObj.setSuppliers(supIDs);
							quotObj.setOffice(new S_OfficeModel(getOfficeID()));
							quotObj.setStatus(1);

							String fromMail = new UserManagementDao()
									.getUseEmailFromLogin(quotObj.getSendBy()
											.getId());

							try {

								id = objDao.save(quotObj);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);
								loadOptions(id);

								if (sendMail) {
									if (emails != null) {
										try {
											if (new EmailConfigDao()
													.getEmailConfiguration(getLoginID()) != null) {
												mail.sendMailFromUserEmail(
														emails,
														quotObj.getContent(),
														quotObj.getHead(),
														null, getLoginID(),
														fromMail);
												Notification
														.show(getPropertyName("Success"),
																Type.WARNING_MESSAGE);
											} else
												Notification
														.show(getPropertyName("email_not_configured"),
																Type.WARNING_MESSAGE);

										} catch (Exception e) {
											// TODO: handle exception
										}
									}
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

							setRequiredError(suppliersListSelect, null, false);

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

			supplierQuotationCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								if (supplierQuotationCombo.getValue() != null
										&& !supplierQuotationCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									edit.setVisible(true);
									delete.setVisible(true);
									update.setVisible(false);
									cancel.setVisible(false);

									SupplierQuotationRequestModel objModel = objDao
											.getSupplierQuotationRequest((Long) supplierQuotationCombo
													.getValue());

									setWritableAll();

									Set<Long> sups = new HashSet<Long>();
									String[] supliers = objModel.getSuppliers()
											.split(",");
									for (String spid : supliers) {
										sups.add(toLong(spid));
									}
									suppliersListSelect.setValue(sups);
									enquiryCombo.setValue(enqDao.getParentID(
											objModel.getEnquiry().getId(),
											getOfficeID()));
									subEnquiryCompo.setValue(objModel
											.getEnquiry().getId());
									responsibleEmployee.setValue(objModel
											.getSendBy().getId());
									dateField.setValue(objModel.getDate());
									headTextField.setValue(objModel.getHead());
									contentTextArea.setValue(objModel
											.getContent());
									budgetAmountTextField
											.setValue(asString(objModel
													.getBudget_amount()));

									setReadOnlyAll();
									sendEmailCheckBox.setValue(false);

								} else {
									save.setVisible(true);
									edit.setVisible(false);
									delete.setVisible(false);
									update.setVisible(false);
									cancel.setVisible(false);
									sendEmailCheckBox.setValue(true);
									setWritableAll();
									headTextField.setValue("");
									suppliersListSelect.setValue(null);
									enquiryCombo.setValue(null);
									subEnquiryCompo.setValue(null);
									responsibleEmployee.setValue(getLoginID());
									dateField.setValue(getWorkingDate());
									contentTextArea.setValue("");
									budgetAmountTextField.setValue("0");
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

			enquiryCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (enquiryCombo.getValue() != null) {

									list = enqDao.getAllLatestEnquiryIDs(
											(Long) enquiryCombo.getValue(),
											getOfficeID());

									bic = CollectionContainer.fromBeans(list,
											"id");
									subEnquiryCompo.setContainerDataSource(bic);
									subEnquiryCompo
											.setItemCaptionPropertyId("enquiry");

									table.removeAllItems();

									List list1 = objDao
											.getAllSupplierQutationFromMasterEnquiry(
													(Long) enquiryCombo
															.getValue(),
													getOfficeID());

									SupplierQuotationRequestModel objModel;
									int ct = 0;
									Iterator it = list1.iterator();
									while (it.hasNext()) {
										objModel = (SupplierQuotationRequestModel) it
												.next();
										ct++;
										String suppliers = "";
										String[] sups = objModel.getSuppliers()
												.split(",");
										for (String string : sups) {
											suppliers += new LedgerDao()
													.getLedgerNameFromID(toLong(string))
													+ " , ";
										}

										table.addItem(
												new Object[] { ct,
														objModel.getDate(),
														objModel.getHead(),
														objModel.getContent(),
														suppliers },
												objModel.getId());
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

			subEnquiryCompo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								table.removeAllItems();
								if (subEnquiryCompo.getValue() != null) {

									CustomerEnquiryModel enq = enqDao
											.getEnquiry((Long) subEnquiryCompo
													.getValue());

									list = objDao
											.getAllSupplierQutationFromCustomerEnquiry((Long) subEnquiryCompo
													.getValue());

									SupplierQuotationRequestModel objModel;
									int ct = 0;
									Iterator it = list.iterator();
									while (it.hasNext()) {
										objModel = (SupplierQuotationRequestModel) it
												.next();
										ct++;
										String suppliers = "";
										String[] sups = objModel.getSuppliers()
												.split(",");
										for (String string : sups) {
											suppliers += new LedgerDao()
													.getLedgerNameFromID(toLong(string))
													+ " , ";
										}

										table.addItem(
												new Object[] { ct,
														objModel.getDate(),
														objModel.getHead(),
														objModel.getContent(),
														suppliers },
												objModel.getId());
									}

									contentTextArea.setValue(enq
											.getDescription());
									headTextField.setValue(enq.getEnquiry());
									budgetAmountTextField.setValue(asString(enq
											.getBudget_amount()));

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
						loadOptions(Long.parseLong(supplierQuotationCombo
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
												objDao.delete((Long) supplierQuotationCombo
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
						if (supplierQuotationCombo.getValue() != null) {

							if (isValid()) {
								boolean sendMail = sendEmailCheckBox.getValue();

								SupplierQuotationRequestModel quotObj = objDao
										.getSupplierQuotationRequest((Long) supplierQuotationCombo
												.getValue());

								String supIDs = "";
								Address[] emails = null;
								if (suppliersListSelect.getValue() != null) {
									long suplId = 0;
									int ct = 0;
									String emailID = "";
									emails = new InternetAddress[((Set<Long>) suppliersListSelect
											.getValue()).size()];
									Iterator it1 = ((Set<Long>) suppliersListSelect
											.getValue()).iterator();
									while (it1.hasNext()) {
										suplId = (Long) it1.next();
										try {
											supIDs += suplId + ",";
											if (sendMail) {
												emailID = new LedgerDao()
														.getEmailFromLedgerID(suplId);
												if (emailID.length() > 3) {
													emails[ct] = new InternetAddress(
															emailID);
													ct++;
												}
											}
										} catch (Exception e) {
										}
									}
								}
								quotObj.setContent(contentTextArea.getValue());
								quotObj.setDate(CommonUtil
										.getSQLDateFromUtilDate(dateField
												.getValue()));
								quotObj.setEnquiry(new CustomerEnquiryModel(
										(Long) subEnquiryCompo.getValue()));
								quotObj.setHead(headTextField.getValue());
								quotObj.setBudget_amount(toDouble(budgetAmountTextField
										.getValue()));
								quotObj.setSendBy(new S_LoginModel(
										(Long) responsibleEmployee.getValue()));
								quotObj.setSuppliers(supIDs);

								String fromMail = new UserManagementDao()
										.getUseEmailFromLogin(quotObj
												.getSendBy().getId());

								try {

									objDao.update(quotObj);
									Notification.show(
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);

									loadOptions(quotObj.getId());

									if (sendMail) {
										if (emails != null) {
											try {
												if (new EmailConfigDao()
														.getEmailConfiguration(getLoginID()) != null) {
													mail.sendMailFromUserEmail(
															emails,
															quotObj.getContent(),
															quotObj.getHead(),
															null, getLoginID(),
															fromMail);
													Notification
															.show(getPropertyName("Success"),
																	"Email Successfully Sent.",
																	Type.WARNING_MESSAGE);
												} else
													Notification
															.show(getPropertyName("email_not_configured"),
																	Type.WARNING_MESSAGE);
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
									}

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								setRequiredError(suppliersListSelect, null,
										false);
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

		} catch (Exception e) {
			// TODO: handle exception
		}
		return mainPanel;

	}

	public void setReadOnlyAll() {
		headTextField.setReadOnly(true);
		budgetAmountTextField.setReadOnly(true);
		suppliersListSelect.setReadOnly(true);
		enquiryCombo.setReadOnly(true);
		subEnquiryCompo.setReadOnly(true);
		responsibleEmployee.setReadOnly(true);
		dateField.setReadOnly(true);
		contentTextArea.setReadOnly(true);
		headTextField.focus();

	}

	public void setWritableAll() {
		headTextField.setReadOnly(false);
		budgetAmountTextField.setReadOnly(false);
		suppliersListSelect.setReadOnly(false);
		enquiryCombo.setReadOnly(false);
		subEnquiryCompo.setReadOnly(false);
		responsibleEmployee.setReadOnly(false);
		dateField.setReadOnly(false);
		contentTextArea.setReadOnly(false);
	}

	public void reloadSuppliers() {
		List list;
		try {
			list = new LedgerDao().getAllSuppliers(getOfficeID());
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			suppliersListSelect.setContainerDataSource(bic);
			suppliersListSelect.setTokenCaptionPropertyId("name");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadOptions(long id) {
		List testList;
		try {

			list = objDao.getAllSupplierQutationRequests(getOfficeID());

			SupplierQuotationRequestModel sop = new SupplierQuotationRequestModel();
			sop.setId(0);
			sop.setHead("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			supplierQuotationCombo.setContainerDataSource(bic);
			supplierQuotationCombo.setItemCaptionPropertyId("head");

			supplierQuotationCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_selection"),
					true);
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

		if (enquiryCombo.getValue() == null
				|| enquiryCombo.getValue().equals("")) {
			setRequiredError(enquiryCombo,
					getPropertyName("invalid_selection"), true);
			enquiryCombo.focus();
			ret = false;
		} else
			setRequiredError(enquiryCombo, null, false);

		if (subEnquiryCompo.getValue() == null
				|| subEnquiryCompo.getValue().equals("")) {
			setRequiredError(subEnquiryCompo,
					getPropertyName("invalid_selection"), true);
			subEnquiryCompo.focus();
			ret = false;
		} else
			setRequiredError(subEnquiryCompo, null, false);

		if (headTextField.getValue() == null
				|| headTextField.getValue().equals("")) {
			setRequiredError(headTextField, getPropertyName("invalid_data"),
					true);
			headTextField.focus();
			ret = false;
		} else
			setRequiredError(headTextField, null, false);

		if (budgetAmountTextField.getValue() == null
				|| budgetAmountTextField.getValue().equals("")) {
			setRequiredError(budgetAmountTextField,
					getPropertyName("invalid_data"), true);
			budgetAmountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(budgetAmountTextField.getValue()) < 0) {
					setRequiredError(budgetAmountTextField,
							getPropertyName("invalid_data"), true);
					budgetAmountTextField.focus();
					ret = false;
				} else
					setRequiredError(budgetAmountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(budgetAmountTextField,
						getPropertyName("invalid_data"), true);
				budgetAmountTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (suppliersListSelect.getValue() == null
				|| ((Set) suppliersListSelect.getValue()).size() <= 0) {
			setRequiredError(suppliersListSelect,
					getPropertyName("invalid_selection"), true);
			suppliersListSelect.focus();
			ret = false;
		} else
			setRequiredError(suppliersListSelect, null, false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
