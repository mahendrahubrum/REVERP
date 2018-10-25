package com.inventory.proposal.ui;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.ItemPanel;
import com.inventory.proposal.dao.CustomerEnquiryDao;
import com.inventory.proposal.model.CustomerEnquiryModel;
import com.inventory.sales.ui.SalesCustomerPanel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.StatusDao;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.dao.MailDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.mailclient.model.MyMailsModel;
import com.webspark.mailclient.ui.MyEmailsUI;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Apr 24, 2014
 */

public class CustomerEnquiry extends SparkLogic {

	private static final long serialVersionUID = 5528036817574363406L;

	long id;

	SPanel mainPanel;
	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	private static final String TBL_SINO = "#";
	private static final String TBL_DATE = "Date";
	private static final String TBL_DELIVERY_DATE = "Delivery Date";
	private static final String TBL_DESCRIPTION = "Description";
	private static final String TBL_REF_NO = "Ref. No.";
	private static final String TBL_LEVEL = "Level";
	private static final String TBL_BUDGET = "Budget";

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	SComboField enquiryListCombo, childEnquiry;

	SComboField customerCombo;
	SDateField dateField, deliveryDate;
	STextArea enquiryTextField;
	SComboField itemSelect;
	SComboField responsibleEmployee;
	STextArea description;
	STextField refNoTextField, budgetAmountTextField;
	SNativeSelect statusCombo;

	private SDialogBox newCustomerWindow;
	private SDialogBox newItemWindow;
	private SButton newCustomerButton;
	private SButton newItemButton;

	SalesCustomerPanel salesCustomerPanel;
	ItemPanel itemPanel;

	private STable table;
	String[] reqHeaders;

	List list;
	CustomerEnquiryDao objDao;

	SButton createNewButton, createNewSubButton;
	private SButton syncEmailInboxButton;
	private SButton loadEmailInboxButton;
	private EmailConfigurationModel emainlConfig;
	
	private SPopupView pop;
	private STable popTable;
	private STextArea popSubjectArea;
	private RichTextArea popContentArea;
	private SButton setDetailsButton;
	
	private static final String POP_TBL_FROM = "From / To";
	private static final String POP_TBL_TITLE = "Title";
	private static final String POP_TBL_DETAILS = "Details";
	private static final String POP_TBL_DATETIME = "Date & Time";

	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(1100, 620);
		objDao = new CustomerEnquiryDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		createNewSubButton = new SButton();
		createNewSubButton.setStyleName("createNewBtnStyle");
		createNewSubButton.setDescription(getPropertyName("create_new"));

		try {
			syncEmailInboxButton = new SButton();
			syncEmailInboxButton.setStyleName("loadAllBtnStyle");
			syncEmailInboxButton.setDescription("Synchonize email from this customer");
			
			loadEmailInboxButton = new SButton();
			loadEmailInboxButton.setStyleName("loadAllBtnStyle");
			loadEmailInboxButton.setDescription("Load email from this customer");
			
			newCustomerButton = new SButton();
			newCustomerButton.setStyleName("addNewBtnStyle");
			newCustomerButton.setDescription("Add new Customer");

			newItemButton = new SButton();
			newItemButton.setStyleName("addNewBtnStyle");
			newItemButton.setDescription("Add new Item");

			newCustomerWindow = new SDialogBox(getPropertyName("add_customer"),
					700, 600);
			newCustomerWindow.center();
			newCustomerWindow.setResizable(false);
			newCustomerWindow.setModal(true);
			newCustomerWindow.setCloseShortcut(KeyCode.ESCAPE);
			salesCustomerPanel = new SalesCustomerPanel();
			newCustomerWindow.addComponent(salesCustomerPanel);

			newItemWindow = new SDialogBox(getPropertyName("add_item"), 500,
					600);
			newItemWindow.center();
			newItemWindow.setResizable(false);
			newItemWindow.setModal(true);
			newItemWindow.setCloseShortcut(KeyCode.ESCAPE);
			itemPanel = new ItemPanel();
			newItemWindow.addComponent(itemPanel);

			reqHeaders = new String[] { TBL_SINO, TBL_DATE, TBL_DESCRIPTION,
					TBL_REF_NO, TBL_LEVEL, TBL_BUDGET, TBL_DELIVERY_DATE };

			table = new STable(getPropertyName("recent_enquries"), 400, 400);
			table.addContainerProperty(TBL_SINO, Integer.class, null, TBL_SINO,
					null, Align.CENTER);
			table.addContainerProperty(TBL_DATE, Date.class, null,
					getPropertyName("date"), null, Align.CENTER);
			table.addContainerProperty(TBL_DELIVERY_DATE, Date.class, null,
					getPropertyName("delivery_date"), null, Align.CENTER);
			table.addContainerProperty(TBL_DESCRIPTION, String.class, null,
					getPropertyName("description"), null, Align.LEFT);
			table.addContainerProperty(TBL_REF_NO, String.class, null,
					getPropertyName("ref_no"), null, Align.LEFT);
			table.addContainerProperty(TBL_LEVEL, Integer.class, null,
					getPropertyName("level"), null, Align.LEFT);
			table.addContainerProperty(TBL_BUDGET, Double.class, null,
					getPropertyName("budget"), null, Align.LEFT);
			table.setSelectable(true);

			table.setColumnWidth(TBL_SINO, 20);
			table.setColumnWidth(TBL_DESCRIPTION, 250);
			table.setColumnWidth(TBL_REF_NO, 100);
			table.setColumnWidth(TBL_DATE, 100);
			table.setColumnWidth(TBL_DELIVERY_DATE, 100);

			table.setWidth("600");
			table.setHeight("420");

			// underOldEnquiry=new SCheckBox("Under Other Enquiry",false);
			// underOldEnquiry.setImmediate(true);
			CustomerEnquiryModel sop = new CustomerEnquiryModel((long)0,"------------------- Create New -------------------");
			List childlist = new ArrayList();
			childlist.add(0, sop);
			childEnquiry = new SComboField(null, 250,childlist,"id","enquiry");
			customerCombo = new SComboField(null, 230,
					new LedgerDao().getAllCustomers(getOfficeID()), "id",
					"name");
			itemSelect = new SComboField(null, 250,
					new ItemDao().getAllActiveItems(getOfficeID()), "id",
					"name");

			responsibleEmployee = new SComboField(
					getPropertyName("responsible_employee"),
					250,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
									getOfficeID(), getOrganizationID()), "id",
					"first_name");

			dateField = new SDateField(getPropertyName("date"), 100);
			deliveryDate = new SDateField(getPropertyName("delivery_date"), 100);
			description = new STextArea(getPropertyName("description"), 250);

			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			save = new SButton(getPropertyName("save"));
			edit = new SButton(getPropertyName("edit"));
			delete = new SButton(getPropertyName("delete"));
			update = new SButton(getPropertyName("update"));
			cancel = new SButton(getPropertyName("cancel"));

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

			enquiryListCombo = new SComboField(null, 250);

			loadOptions(0);

			statusCombo = new SNativeSelect(getPropertyName("status"), 250,
					new StatusDao().getStatuses("GroupModel", "status"),
					"value", "name");

			// groupCombo = new SComboField("Group", 250, new
			// GroupDao().getAllGroupsNames(getOrganizationID()), "id", "name"
			// , true, "Select");

			enquiryTextField = new STextArea(getPropertyName("enquiry"), 250);
			// childEnquiry.setVisible(false);
			refNoTextField = new STextField(getPropertyName("ref_no"), 250);
			budgetAmountTextField = new STextField(
					getPropertyName("budget_amount"), 250);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("main_enquiry"));
			salLisrLay.addComponent(enquiryListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);

			SHorizontalLayout subEnq = new SHorizontalLayout(
					getPropertyName("sub_enquiry"));
			subEnq.addComponent(childEnquiry);
			subEnq.addComponent(createNewSubButton);
			form.addComponent(subEnq);

			SHorizontalLayout custSel = new SHorizontalLayout(getPropertyName("customer"));
			custSel.addComponent(customerCombo);
			custSel.addComponent(loadEmailInboxButton);
			custSel.addComponent(newCustomerButton);
			form.addComponent(custSel);

			form.addComponent(dateField);
			form.addComponent(enquiryTextField);
			form.addComponent(description);

			SHorizontalLayout itmSel = new SHorizontalLayout(getPropertyName("item"));
			itmSel.addComponent(itemSelect);
			itmSel.addComponent(newItemButton);
			form.addComponent(itmSel);

			form.addComponent(responsibleEmployee);
			form.addComponent(statusCombo);
			form.addComponent(refNoTextField);
			form.addComponent(deliveryDate);
			form.addComponent(budgetAmountTextField);

			popTable=new STable(null,700,200);
			popTable.addContainerProperty(POP_TBL_FROM, String.class, null,
					getPropertyName("from"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBL_DATETIME, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBL_TITLE, String.class, null,
					getPropertyName("title"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBL_DETAILS, String.class, null,
					getPropertyName("details"), null, Align.LEFT);
			popTable.setColumnExpandRatio(POP_TBL_DETAILS, 10f);
			popTable.setSelectable(true);
			
			popSubjectArea=new STextArea("Subject",700,50);
			popContentArea=new RichTextArea("Content");
			popContentArea.setWidth("700px");
			popContentArea.setHeight("200px");
			setDetailsButton=new SButton("Set in Description");
			
			
//			SHorizontalLayout hLay=new SHorizontalLayout();
			SFormLayout popform=new SFormLayout();
			popform.setSpacing(true);
			
			popform.addComponent(syncEmailInboxButton);
			popform.addComponent(popTable);
			popform.addComponent(popSubjectArea);
			popform.addComponent(popContentArea);
			popform.addComponent(setDetailsButton);
			
			
//			hLay.addComponent(popform);
			
			pop=new SPopupView(null,popform);
			pop.setHideOnMouseOut(false);
			
			
			SVerticalLayout lay = new SVerticalLayout(true, form, buttonLayout);
			vLayout.addComponent(new SHorizontalLayout(true, lay,pop,table));

			lay.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			mainPanel.setContent(vLayout);
			
			popTable.addListener(new Listener() {
				
				@Override
				public void componentEvent(Event event) {
					if(popTable.getValue()!=null){
						Item item=popTable.getItem(popTable.getValue());
						popSubjectArea.setValue(item.getItemProperty(POP_TBL_TITLE).getValue().toString());
						popContentArea.setValue(item.getItemProperty(POP_TBL_DETAILS).getValue().toString());
					}else{
						popSubjectArea.setValue("");
						popContentArea.setValue("");
					}
				}
			});
			setDetailsButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(popContentArea.getValue()!=null&&popContentArea.getValue().toString().trim().length()>0){
						description.setValue(Jsoup.parse(popContentArea.getValue().toString()).text());
						pop.setPopupVisible(false);
					}
				}
			});
			
			syncEmailInboxButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (customerCombo.getValue() != null
							&& !customerCombo.getValue().toString()
									.equals("0")) {
						new MyEmailsUI().syncronizeMail("Inbox");
//						Object obj = customerCombo.getValue();
//						customerCombo.setValue(null);
//						customerCombo.setValue(obj);
					}
				}
			});

			loadEmailInboxButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					customerCombo.setComponentError(null);
					if (customerCombo.getValue() != null
							&& !customerCombo.getValue().toString()
									.equals("0")) {
						try {
							emainlConfig = new EmailConfigDao()
									.getEmailConfiguration(getLoginID());
							if (emainlConfig != null) {
								String email = new LedgerDao()
										.getEmailFromLedgerID((Long) customerCombo
												.getValue());
								if (email.length() > 3)
									loadList(email);
							} else {
								SNotification.show(
										getPropertyName("mail_config_msg"),
										Type.ERROR_MESSAGE);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						
						pop.setPopupVisible(true);
					}else{
						setRequiredError(customerCombo, "Select customer to view emails", true);
					}
				}
			});
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					enquiryListCombo.setValue((long) 0);
				}
			});

			createNewSubButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					childEnquiry.setValue((long) 0);
				}
			});

			// table.addValueChangeListener(new ValueChangeListener() {
			// @Override
			// public void valueChange(ValueChangeEvent event) {
			// if(table.getValue()!=null) {
			//
			// try {
			//
			// loadOptions((Long) table.getValue());
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			//
			// }
			// }
			// });

			newItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					itemPanel.reloadGroup();
					getUI().getCurrent().addWindow(newItemWindow);
					newItemWindow.setCaption("Add New Item");
				}
			});

			newItemWindow.addCloseListener(new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					reloadItemStocks();
				}
			});

			newCustomerButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					salesCustomerPanel.clearFields();
					getUI().getCurrent().addWindow(newCustomerWindow);
				}
			});

			newCustomerWindow.addCloseListener(new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					reloadCustomers();
				}
			});

			final Action actionDelete = new Action("Load This Enquiry");

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

						try {

							loadOptions((Long) table.getValue());
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				}

			});

			customerCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (customerCombo.getValue() != null) {
						try {
							long cur_id = 0;
							if (enquiryListCombo.getValue() != null)
								cur_id = (Long) enquiryListCombo.getValue();

							// List lst=new ArrayList();
							// lst.add(new CustomerEnquiryModel(0, "NEW"));
							// lst.addAll(objDao.getAllFirstEnquiriesUnderCustomer((Long)
							// customerCombo.getValue(),cur_id));
							//
							// bic=CollectionContainer.fromBeans(lst, "id");
							// childEnquiry.setContainerDataSource(bic);
							// childEnquiry.setItemCaptionPropertyId("enquiry");
							//
							// childEnquiry.setValue((long)0);
						} catch (Exception e) {
							// TODO: handle exception
						}
						
					}
				}
			});

			/*
			 * underOldEnquiry.addValueChangeListener(new ValueChangeListener()
			 * {
			 * 
			 * @Override public void valueChange(ValueChangeEvent event) {
			 * 
			 * if(underOldEnquiry.getValue()) childEnquiry.setVisible(true);
			 * else childEnquiry.setVisible(false);
			 * 
			 * } });
			 */

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							CustomerEnquiryModel custEnqObj = new CustomerEnquiryModel();

							custEnqObj.setEnquiry(enquiryTextField.getValue());
							custEnqObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							custEnqObj.setDelivery_date(CommonUtil
									.getSQLDateFromUtilDate(deliveryDate
											.getValue()));
							custEnqObj.setCustomer(new LedgerModel(
									(Long) customerCombo.getValue()));
							custEnqObj.setDescription(description.getValue());
							custEnqObj
									.setResponsible_employee((Long) responsibleEmployee
											.getValue());
							custEnqObj.setItem(new ItemModel((Long) itemSelect
									.getValue()));
							custEnqObj.setOffice(new S_OfficeModel(
									getOfficeID()));
							custEnqObj.setRef_no(refNoTextField.getValue());
							custEnqObj.setStatus((Long) statusCombo.getValue());
							custEnqObj
									.setBudget_amount(toDouble(budgetAmountTextField
											.getValue()));
							if (enquiryListCombo.getValue() != null
									&& !enquiryListCombo.getValue().toString()
											.equals("0")) {
								custEnqObj
										.setParen_req_id((Long) enquiryListCombo
												.getValue());
								custEnqObj.setLevel(objDao
										.getMaxEnquiryLevel((Long) enquiryListCombo
												.getValue()) + 1);

								custEnqObj.setNumber(objDao
										.getEnquiryNumberFromID((Long) enquiryListCombo
												.getValue()));
							} else {
								custEnqObj.setParen_req_id(0);
								custEnqObj.setLevel(0);
								custEnqObj.setNumber(getNextSequence(
										"Customer_Enquiry_No", getLoginID()));
							}

							try {
								id = objDao.save(custEnqObj);
								loadOptions(id);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

							setRequiredError(customerCombo, null, false);

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

			enquiryListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								if (enquiryListCombo.getValue() != null
										&& !enquiryListCombo.getValue()
												.toString().equals("0")) {

									list = objDao.getAllLatestEnquiryIDs(
											(Long) enquiryListCombo.getValue(),
											getOfficeID());

									CustomerEnquiryModel sop = new CustomerEnquiryModel();
									sop.setId(0);
									sop.setEnquiry("------------------- Create New -------------------");
									if (list == null)
										list = new ArrayList();
									list.add(0, sop);

									CustomerEnquiryModel ceObj = (CustomerEnquiryModel) list
											.get(list.size() - 1);

									bic = CollectionContainer.fromBeans(list,
											"id");
									childEnquiry.setContainerDataSource(bic);
									childEnquiry
											.setItemCaptionPropertyId("enquiry");

									childEnquiry.setValue(ceObj.getId());

									loadTableData((Long) enquiryListCombo
											.getValue());

								} else {

									CustomerEnquiryModel sop = new CustomerEnquiryModel();
									sop.setId(0);
									sop.setEnquiry("------------------- Create New -------------------");
									list = new ArrayList();
									list.add(0, sop);

									bic = CollectionContainer.fromBeans(list,
											"id");
									childEnquiry.setContainerDataSource(bic);
									childEnquiry
											.setItemCaptionPropertyId("enquiry");
									childEnquiry.setValue((long) 0);

									table.removeAllItems();

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

			childEnquiry
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								if (childEnquiry.getValue() != null
										&& !childEnquiry.getValue().toString()
												.equals("0")) {

									save.setVisible(false);
									edit.setVisible(true);
									delete.setVisible(true);
									update.setVisible(false);
									cancel.setVisible(false);

									CustomerEnquiryModel objModel = objDao
											.getEnquiry((Long) childEnquiry
													.getValue());

									setWritableAll();
									customerCombo.setValue(null);

									customerCombo.setValue(objModel
											.getCustomer().getId());

									// if(objModel.getParen_req_id()!=0) {
									// underOldEnquiry.setValue(true);
									// childEnquiry.setValue(objModel.getParen_req_id());
									// }else {
									// underOldEnquiry.setValue(false);
									// childEnquiry.setValue((long)0);
									// }

									itemSelect.setValue(objModel.getItem()
											.getId());
									responsibleEmployee.setValue(objModel
											.getResponsible_employee());

									enquiryTextField.setValue(objModel
											.getEnquiry());
									refNoTextField.setValue(objModel
											.getRef_no());
									statusCombo.setValue(objModel.getStatus());

									dateField.setValue(objModel.getDate());
									deliveryDate.setValue(objModel
											.getDelivery_date());
									description.setValue(objModel
											.getDescription());
									budgetAmountTextField
											.setValue(asString(objModel
													.getBudget_amount()));

									setReadOnlyAll();

									// loadTableData((Long)
									// enquiryListCombo.getValue());

								} else {

									save.setVisible(true);
									edit.setVisible(false);
									delete.setVisible(false);
									update.setVisible(false);
									cancel.setVisible(false);

									setWritableAll();
									// childEnquiry.setValue((long)0);
									// underOldEnquiry.setValue(false);
									enquiryTextField.setValue("");
									statusCombo.setValue((long) 1);
									refNoTextField.setValue("0");
									customerCombo.setValue(null);
									itemSelect.setValue(null);
									responsibleEmployee.setValue(getLoginID());
									dateField.setValue(getWorkingDate());
									deliveryDate.setValue(getWorkingDate());
									description.setValue("");
									budgetAmountTextField.setValue("0");
									setDefaultValues();

									if (enquiryListCombo.getValue() != null
											&& !enquiryListCombo.getValue()
													.toString().equals("0")) {

										CustomerEnquiryModel objMdl = objDao
												.getEnquiry((Long) enquiryListCombo
														.getValue());

										customerCombo.setValue(objMdl
												.getCustomer().getId());
										itemSelect.setValue(objMdl.getItem()
												.getId());
										responsibleEmployee.setValue(objMdl
												.getResponsible_employee());
										enquiryTextField.setValue(objMdl
												.getEnquiry());
										statusCombo.setValue(objMdl.getStatus());

										dateField.setValue(objMdl.getDate());
										deliveryDate.setValue(objMdl
												.getDelivery_date());
										description.setValue(objMdl
												.getDescription());
										budgetAmountTextField
												.setValue(asString(objMdl
														.getBudget_amount()));

										setReadOnlyAll();
										setDetailsWritable();
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

			edit.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(true);
						cancel.setVisible(true);

						if (childEnquiry.getValue() != null
								&& !childEnquiry.getValue().toString()
										.equals("0")) {
							if (objDao.isParent((Long) childEnquiry.getValue())) {
								setWritableAll();
							} else {
								setDetailsWritable();
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

			cancel.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);
						loadOptions(Long.parseLong(childEnquiry.getValue()
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
												objDao.delete((Long) childEnquiry
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

						if (childEnquiry.getValue() != null) {
							if (isValid()) {

								CustomerEnquiryModel custEnqObj = objDao
										.getEnquiry((Long) childEnquiry
												.getValue());

								custEnqObj.setEnquiry(enquiryTextField
										.getValue());
								custEnqObj.setDate(CommonUtil
										.getSQLDateFromUtilDate(dateField
												.getValue()));
								custEnqObj.setDelivery_date(CommonUtil
										.getSQLDateFromUtilDate(deliveryDate
												.getValue()));
								custEnqObj.setCustomer(new LedgerModel(
										(Long) customerCombo.getValue()));
								custEnqObj.setDescription(description
										.getValue());
								custEnqObj
										.setResponsible_employee((Long) responsibleEmployee
												.getValue());
								custEnqObj.setItem(new ItemModel(
										(Long) itemSelect.getValue()));
								custEnqObj.setOffice(new S_OfficeModel(
										getOfficeID()));
								custEnqObj.setRef_no(refNoTextField.getValue());
								custEnqObj.setStatus((Long) statusCombo
										.getValue());
								custEnqObj
										.setBudget_amount(toDouble(budgetAmountTextField
												.getValue()));
								// if(childEnquiry.getValue()!=null)
								// custEnqObj.setParen_req_id((Long)
								// childEnquiry.getValue());

								// if(enquiryListCombo.getValue()!=null &&
								// !enquiryListCombo.getValue().toString().equals("0"))
								// {
								// custEnqObj.setParen_req_id((Long)
								// enquiryListCombo.getValue());
								// custEnqObj.setLevel(objDao.getMaxEnquiryLevel((Long)
								// enquiryListCombo.getValue())+1);
								// custEnqObj.setNumber(objDao.getEnquiryNumberFromID((Long)
								// enquiryListCombo.getValue()));
								// }
								// else {
								// custEnqObj.setParen_req_id(0);
								// custEnqObj.setLevel(1);
								// }

								try {

									objDao.update(custEnqObj);

									loadOptions(objDao
											.getEnquiryMinEnqIDFromNumber(custEnqObj
													.getNumber()));

									Notification.show(
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											Type.ERROR_MESSAGE);
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								setRequiredError(customerCombo, null, false);
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

			// addShortcutListener(new ShortcutListener("Add New Purchase",
			// ShortcutAction.KeyCode.N, new int[] {
			// ShortcutAction.ModifierKey.ALT}) {
			// @Override
			// public void handleAction(Object sender, Object target) {
			// loadOptions(0);
			// }
			// });
			//
			//
			// addShortcutListener(new ShortcutListener("Save",
			// ShortcutAction.KeyCode.ENTER, null) {
			// @Override
			// public void handleAction(Object sender, Object target) {
			// if (save.isVisible())
			// save.click();
			// else
			// update.click();
			// }
			// });

			setDefaultValues();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return mainPanel;

	}
	
	private void loadList(String checkEmail) {
		try {

			List list = null;
			popTable.removeAllItems();

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

				popTable.addItem(
						new Object[] { objModel.getEmails(),objModel.getDate_time().toString(), objModel.getSubject(),
								 details
								 }, objModel.getId());

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

	public void setReadOnlyAll() {
		enquiryTextField.setReadOnly(true);
		budgetAmountTextField.setReadOnly(true);
		statusCombo.setReadOnly(true);
		refNoTextField.setReadOnly(true);
		customerCombo.setReadOnly(true);
		itemSelect.setReadOnly(true);
		responsibleEmployee.setReadOnly(true);
		dateField.setReadOnly(true);
		deliveryDate.setReadOnly(true);
		description.setReadOnly(true);
		// childEnquiry.setReadOnly(true);
		// underOldEnquiry.setReadOnly(true);
		enquiryTextField.focus();
	}

	public void setWritableAll() {
		enquiryTextField.setReadOnly(false);
		budgetAmountTextField.setReadOnly(false);
		statusCombo.setReadOnly(false);
		refNoTextField.setReadOnly(false);
		customerCombo.setReadOnly(false);
		itemSelect.setReadOnly(false);
		responsibleEmployee.setReadOnly(false);
		dateField.setReadOnly(false);
		deliveryDate.setReadOnly(false);
		description.setReadOnly(false);
		// childEnquiry.setReadOnly(false);
		// underOldEnquiry.setReadOnly(false);
	}

	public void setDetailsWritable() {
		// enquiryTextField.setReadOnly(false);
		budgetAmountTextField.setReadOnly(false);
		// statusCombo.setReadOnly(false);
		refNoTextField.setReadOnly(false);
		// customerCombo.setReadOnly(false);
		// itemSelect.setReadOnly(false);
		// responsibleEmployee.setReadOnly(false);
		dateField.setReadOnly(false);
		deliveryDate.setReadOnly(false);
		description.setReadOnly(false);
		// childEnquiry.setReadOnly(false);
		// underOldEnquiry.setReadOnly(false);
	}

	protected void reloadCustomers() {
		try {
			List list = new LedgerDao().getAllCustomers(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			customerCombo.setContainerDataSource(bic);
			customerCombo.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("new_id") != null) {
				customerCombo.setValue((Long) getHttpSession().getAttribute(
						"new_id"));
				getHttpSession().removeAttribute("new_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void reloadItemStocks() {
		try {
			List list = new ItemDao().getAllActiveItems(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(list, "id");
			itemSelect.setContainerDataSource(bic);
			itemSelect.setItemCaptionPropertyId("name");

			if (getHttpSession().getAttribute("saved_id") != null) {
				itemSelect.setNewValue((Long) getHttpSession().getAttribute(
						"saved_id"));
				getHttpSession().removeAttribute("saved_id");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadOptions(long id) {
		List testList;
		try {
			if (enquiryListCombo.getValue() != null) {
				long oldId = (Long) enquiryListCombo.getValue();
				loadParentList();
				if (oldId != 0) {
					enquiryListCombo.setValue(null);
					enquiryListCombo.setValue(oldId);
					childEnquiry.setValue(id);
				} else {
					enquiryListCombo.setValue(id);
					childEnquiry.setValue(id);
				}
			} else {
				loadParentList();
				enquiryListCombo.setValue(id);
				childEnquiry.setValue(id);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadParentList() {
		List testList;
		try {

			list = objDao.getAllMasterEnquiries(getOfficeID());

			CustomerEnquiryModel sop = new CustomerEnquiryModel();
			sop.setId(0);
			sop.setEnquiry("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			enquiryListCombo.setContainerDataSource(bic);
			enquiryListCombo.setItemCaptionPropertyId("enquiry");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadTableData(long id) {
		List testList;
		try {
			table.removeAllItems();
			if (id != 0) {
				table.setVisibleColumns(reqHeaders);
				list = objDao.getAllLatestEnquiriesUnderMaster(id,
						getOfficeID());

				CustomerEnquiryModel objModel;
				int ct = 0;
				Iterator it = list.iterator();
				while (it.hasNext()) {
					objModel = (CustomerEnquiryModel) it.next();
					ct++;
					table.addItem(new Object[] { ct, objModel.getDate(),
							objModel.getDescription(), objModel.getRef_no(),
							objModel.getLevel(), objModel.getBudget_amount(),
							objModel.getDelivery_date() }, objModel.getId());
				}

			}

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

		if (deliveryDate.getValue() == null
				|| deliveryDate.getValue().equals("")) {
			setRequiredError(deliveryDate,
					getPropertyName("invalid_selection"), true);
			deliveryDate.focus();
			ret = false;
		} else
			setRequiredError(deliveryDate, null, false);

		if (responsibleEmployee.getValue() == null
				|| responsibleEmployee.getValue().equals("")) {
			setRequiredError(responsibleEmployee,
					getPropertyName("invalid_selection"), true);
			responsibleEmployee.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployee, null, false);

		if (itemSelect.getValue() == null || itemSelect.getValue().equals("")) {
			setRequiredError(itemSelect, getPropertyName("invalid_selection"),
					true);
			itemSelect.focus();
			ret = false;
		} else
			setRequiredError(itemSelect, null, false);

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (enquiryTextField.getValue() == null
				|| enquiryTextField.getValue().equals("")) {
			setRequiredError(enquiryTextField, getPropertyName("invalid_data"),
					true);
			enquiryTextField.focus();
			ret = false;
		} else
			setRequiredError(enquiryTextField, null, false);

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

		if (customerCombo.getValue() == null
				|| customerCombo.getValue().equals("")) {
			setRequiredError(customerCombo,
					getPropertyName("invalid_selection"), true);
			customerCombo.focus();
			ret = false;
		} else
			setRequiredError(customerCombo, null, false);

		return ret;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {
				Item itm = table.getItem(table.getValue());

				if (((Integer) itm.getItemProperty(TBL_LEVEL).getValue()) != 0
						&& table.getItemIds().size() > 1) {
					ConfirmDialog.show(getUI(), "Are you sure?",
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											objDao.delete((Long) table
													.getValue());
											Object obj = enquiryListCombo
													.getValue();
											enquiryListCombo.setValue(null);
											enquiryListCombo.setValue(obj);
										} catch (Exception e) {
											e.printStackTrace();
											Notification.show(
													getPropertyName("error"),
													Type.ERROR_MESSAGE);
										}
									}
								}
							});

				} else if (((Integer) itm.getItemProperty(TBL_LEVEL).getValue()) == 0
						&& table.getItemIds().size() == 1) {
					ConfirmDialog.show(getUI(), "Are you sure?",
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											objDao.delete((Long) table
													.getValue());

											loadOptions(0);

										} catch (Exception e) {
											e.printStackTrace();
											Notification.show(
													getPropertyName("error"),
													Type.ERROR_MESSAGE);
										}
									}
								}
							});
				} else {
					Notification.show(getPropertyName("delete_parent"),
							Type.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("error"),
					Type.ERROR_MESSAGE);
		}
	}

	public void setDefaultValues() {
		try {
			Iterator it = null;
			it = statusCombo.getItemIds().iterator();
			if (it.hasNext())
				statusCombo.setValue(it.next());

			refNoTextField.setValue("");

			responsibleEmployee.setValue(getLoginID());

			dateField.setValue(getWorkingDate());
			deliveryDate.setValue(getWorkingDate());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
